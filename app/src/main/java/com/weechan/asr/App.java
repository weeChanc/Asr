package com.weechan.asr;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

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
