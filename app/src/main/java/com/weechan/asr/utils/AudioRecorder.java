package com.weechan.asr.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

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

    private int minBuffSize = AudioRecord.getMinBufferSize(44100,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

    private AudioRecord mAudioRecord = null;

    private int mState = ERROR;
    private Listener mListener;

    //就绪或录制中
    private static int READY = 2;
    //暂停
    private static int PAUSE = 3;
    //未初始化或已被释放
    private static int ERROR = -1;

    //开始或继续录音,listener为空,就使用之前的listener,否则使用新的listener
    public void startRecord(Listener listener) {

        if (mState == READY) {
            Log.e(getClass().getSimpleName(), "已经在录制了!!!!");
            return;
        }

        if(mState == ERROR){
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    44100, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, minBuffSize);
            //开始录制
            mAudioRecord.startRecording();
        }

        if (mAudioRecord.getState() != STATE_INITIALIZED) {
            Log.e("AudioRecorder", "权限不足无法录制!!!");
            return;
        }

        mState = READY; //就绪

        if(listener != null)
            this.mListener = listener;

        //唤醒线程进行录制
        synchronized (mReadDataThread){
            mReadDataThread.notify();
        }

    }

    public void pause() {
        if(mState == PAUSE){
            Log.i(getClass().getSimpleName(),"录制已经暂停了!!!");
            return;
        }

        mState = PAUSE;
        if (mListener != null) {
            mListener.onPause();
        }

    }

    public void stop(){
        if(mState == ERROR){
            Log.i(getClass().getSimpleName(),"已经结束录制了!!!");
            return;
        }
        mState = ERROR;
        mListener = null;
        mAudioRecord.release();
    }

    public static List<Short> toShortArray(byte[] data,int pressRatio) {
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

                synchronized (this){
                    //不是就绪的时候锁住当前线程,等待唤醒
                    if(mState != READY){
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }


                read = mAudioRecord.read(buf, 0, minBuffSize);
                if (read >= AudioRecord.SUCCESS) {
                    if (mListener != null)
                        mListener.onDataAvaliable(Arrays.copyOf(buf, read));
                }
            }
        }
    };

    public interface Listener {
        void onDataAvaliable(byte[] data) ;

        void onPause();
    }

}
