package com.weechan.asr.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.media.AudioRecord.STATE_INITIALIZED;

/**
 * @author 214652773@qq.com
 * @user c
 * @create 2018/12/29 10:43
 */

public class AudioRecorder {

    private static AudioRecorder INSTANT;

    public static AudioRecorder getInstant() {
        if (INSTANT == null) {
            synchronized (AudioRecorder.class) {
                if (INSTANT == null) {
                    INSTANT = new AudioRecorder();
                    INSTANT.mReadDataThread.start();
                }
            }
        }
        return INSTANT;
    }

    private AudioRecorder() {
        super();
    }

    private static int sampleRate = 16000;
    private int minBuffSize = AudioRecord.getMinBufferSize(sampleRate,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    private AudioRecord mAudioRecord = null;
    private volatile int mState = ERROR;
    private Listener mListener;

    //为了避免装箱就泛型不要byte了
    private List<byte[]> buff = new ArrayList<>();

    //就绪或录制中
    private static int READY = 2;
    //暂停
    private static int PAUSE = 3;
    //未初始化或已被释放
    private static int ERROR = -1;

    public boolean isRecording() {
        return mState == READY;
    }

    //开始或继续录音,listener为空,就使用之前的listener,否则使用新的listener
    public void startRecord(Listener listener) {

        if (mState == READY) {
            Log.e(getClass().getSimpleName(), "已经在录制了!!!!");
            return;
        }

        if (mState == ERROR) {
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    sampleRate, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, minBuffSize);
            //开始录制
            mAudioRecord.startRecording();
        }

        if (mAudioRecord.getState() != STATE_INITIALIZED) {
            Log.e("AudioRecorder", "权限不足无法录制!!!");
            return;
        }

        mState = READY; //就绪

        if (listener != null)
            this.mListener = listener;

        //唤醒线程进行录制
        synchronized (mReadDataThread) {
            buff.clear();
            mReadDataThread.notify();
        }

    }

    public void pause() {
        if (mState == PAUSE) {
            Log.i(getClass().getSimpleName(), "录制已经暂停了!!!");
            return;
        }

        mState = PAUSE;
        if (mListener != null) {
            mListener.onPause();
        }

    }

    /**
     *
     * @return 返回开始到结束的那一段byte数组
     */
    public List<byte[]> stop() {
        if (mState == ERROR) {
            Log.i(getClass().getSimpleName(), "已经结束录制了!!!");
            return null;
        }
        mState = ERROR;
        if (mListener != null) {
            mListener.onStop();
        }

        mListener = null;
        mAudioRecord.release();
        return new ArrayList<>(buff);
    }

    public static List<Short> toShortArray(byte[] data, int pressRatio) {
        ShortBuffer sb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        short[] buf = new short[sb.limit()];
        sb.get(buf);
        List<Short> list = new ArrayList<>(1 << 8);

        for (int i = 0; i < buf.length; i += pressRatio) {
            list.add(buf[i]);
        }
        return list;
    }

    private final Thread mReadDataThread = new Thread() {
        @Override
        public void run() {
            int read;
            byte[] buf = new byte[minBuffSize];
            while (true) {

                //不是就绪的时候锁住当前线程,等待唤醒
                if (mState != READY) {
                    synchronized (this) {
                        if (mState != READY) {
                            try {
//                                buff.clear();
                                this.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                read = mAudioRecord.read(buf, 0, minBuffSize);
                buff.add(buf);
                if (read >= AudioRecord.SUCCESS) {
                    if (mListener != null)
                        mListener.onDataAvaliable(buf);
                }


            }
        }
    };

    public interface Listener {
        void onDataAvaliable(byte[] data);

        void onPause();

        void onStop();
    }

    /**
     * PCM文件转WAV文件
     * @param sampleRate     采样率，例如15000
     * @param channels       声道数 单声道：1或双声道：2
     * @param bitNum         采样位数，8或16
     */
    public static byte[] convertPcmToWav(byte[] in, int sampleRate,
                                         int channels, int bitNum) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            //采样字节byte率
            long byteRate = sampleRate * channels * bitNum / 8;
            //PCM文件大小
            long totalAudioLen = in.length;
            //总大小，由于不包括RIFF和WAV，所以是44 - 8 = 36，在加上PCM文件大小
            long totalDataLen = totalAudioLen + 36;
            byte[] header = writeWaveFileHeader(totalAudioLen, totalDataLen, sampleRate, channels, byteRate);
            out.write(header);
            out.write(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * 输出WAV头
     * @param totalAudioLen 整个音频PCM数据大小
     * @param totalDataLen  整个数据大小
     * @param sampleRate    采样率
     * @param channels      声道数
     * @param byteRate      采样字节byte率
     * @throws IOException
     */
    private static byte[] writeWaveFileHeader(long totalAudioLen,
                                              long totalDataLen, int sampleRate, int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (channels * 16 / 8);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        return header;
    }

}
