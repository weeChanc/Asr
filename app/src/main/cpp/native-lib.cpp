#include <jni.h>
#include <string>
#include <math.h>
#include <fstream>
#include <ostream>
#include <stdio.h>

#include <android/log.h>
#include <android/asset_manager_jni.h>
#include <android/asset_manager.h>


#include "android-utils.cpp"
#include "constant.h"


using namespace std;

extern "C" {
JNIEXPORT jstring JNICALL
Java_com_weechan_asr_nativeUtils_NativeUtils_readFileFromAssets(JNIEnv *env, jclass o, jstring path) {

    if (path == nullptr) return nullptr;

    AAssetManager *mgr = getAssertManager(env);
    const char *realPath = jstring2charArray(env, path);
    AAsset *asset = AAssetManager_open(mgr, realPath, AASSET_MODE_UNKNOWN);

    if (asset == nullptr) {
        __android_log_print(ANDROID_LOG_ERROR, "JNI ERROR", " EMPTY OF ASSET");
        return nullptr;
    }

    long bufferSize = AAsset_getLength(asset);
    char *buffer = (char *) malloc(bufferSize + 1);
    buffer[bufferSize] = 0;
    int numBytesRead = AAsset_read(asset, buffer, bufferSize);
    free(buffer);
    /*关闭文件*/
    AAsset_close(asset);
    return (*env).NewStringUTF(buffer);
}
}

extern "C" {
JNIEXPORT jstring JNICALL
Java_com_weechan_asr_nativeUtils_NativeUtils_readFile(
        JNIEnv *env,
        jclass /* this */) {
    ofstream of(Constant::ASR_BASE_PATH +"/good.txt");
    of << "Hello C++ IO " << flush;
    of.close();
    ifstream in(Constant::ASR_BASE_PATH +"/good.txt");
    string read;
    in >> read;
    in.close();
    return env->NewStringUTF(read.c_str());
}
}

extern "C" {
JNIEXPORT jstring JNICALL
Java_com_weechan_asr_nativeUtils_NativeUtils_injectBasePath(
        JNIEnv *env, jclass klass, jstring basePath) {
    if(Constant::ASR_BASE_PATH.empty()){
        Constant::ASR_BASE_PATH = jstring2charArray(env, basePath);
    }
    return (*env).NewStringUTF(Constant::ASR_BASE_PATH.c_str());
}
}
