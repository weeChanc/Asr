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
import java.util.List;
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
    }


}
