package com.weechan.asr;

import android.app.Application;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.weechan.asr.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author 214652773@qq.com
 * @user c
 * @create 2019/1/30 0:08
 */

public class App extends Application {
    public static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;


        try {
            IOUtils.extraFile(getAssets().open("model.zip"), new File(getFilesDir(), "model"));
            String result = Analyze.injectBasePath(getFilesDir().getAbsolutePath() + "/model");
            Toast.makeText(app, result + "  " + new File(Environment.getExternalStorageDirectory().getPath() + "/SA1_.wav").exists(), Toast.LENGTH_SHORT).show();

            Analyze.analyze(Environment.getExternalStorageDirectory().getPath() + "/SA1_.wav");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
