package com.weechan.asr;

/**
 * @author 214652773@qq.com
 * @user c
 * @create 2019/2/13 22:33
 */

public class Analyze {
    static {
        System.loadLibrary("core");
    }

    public static native void analyze(String path);

    public static native String injectBasePath(String path);
}
