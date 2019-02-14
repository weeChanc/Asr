//
// Created by c on 2019/1/30.
//

#include <android/log.h>
#include <jni.h>
#include <android/asset_manager_jni.h>
#include <android/asset_manager.h>
#include <fstream>
#include <stdio.h>
#include <iostream>
#include "android-utils.h"

using namespace std;

AAssetManager *getAssertManager(JNIEnv *env) {
    jobject ctx = getApplicationContext(env);
    jclass klass = env->FindClass("android/content/ContextWrapper");
    jmethodID method_id = env->GetMethodID(klass, "toString",
                                           "()Ljava/lang/String;");
    jobject assetManager = (*env).CallObjectMethod(ctx, (*env)
            .GetMethodID(klass, "getAssets", "()Landroid/content/res/AssetManager;"));
    AAssetManager *mgr = AAssetManager_fromJava(env, assetManager);
    return mgr;
};

jobject getApplicationContext(JNIEnv* env){
    jclass klass = (*env).FindClass("com/weechan/asr/App");
    jfieldID field_id = (*env).GetStaticFieldID(klass,"app","Lcom/weechan/asr/App;");
    return (*env).GetStaticObjectField(klass,field_id);
}

const char * jstring2charArray(JNIEnv* env , jstring str){
    return (*env).GetStringUTFChars(str, nullptr);
}


