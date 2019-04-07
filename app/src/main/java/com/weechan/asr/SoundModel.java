package com.weechan.asr;

import com.weechan.asr.data.SoundSource;
import com.weechan.asr.utils.AudioRecorder;
import com.weechan.asr.utils.other.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SoundModel {

    private File outFile;
    private OutputStream out;
    List<SoundSource> sources = new ArrayList<>();


    public SoundModel() {
    }

    void start(Runnable runnable) {
        try {
            out = new BufferedOutputStream(new FileOutputStream(outFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        AudioRecorder.getInstant().startRecord(new AudioRecorder.Listener() {
            @Override
            public void onDataAvaliable(byte[] data) {
                runnable.run();
                try {
                    out.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPause() {
            }

            @Override
            public void onStop() {
            }
        });
    }

    void stop() {
        AudioRecorder.getInstant().stop();
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void init() throws Exception {
        IOUtils.extraFile(App.app.getAssets().open("model.zip"), new File(App.app.getFilesDir(), "model"));
        IOUtils.extraFile(App.app.getAssets().open("testdata.zip"), new File(App.app.getFilesDir(), "testdata"));
        new File(App.app.getFilesDir(), "sound-asr").mkdir();
        outFile = new File(new File(App.app.getFilesDir(), "sound-asr"), System.currentTimeMillis() + "_record.pcm");
        sources = SoundSource.Companion.readAllSoundSource(new File(App.app.getFilesDir(), "testdata").getAbsolutePath());
    }

}
