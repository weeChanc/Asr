//
// Created by c on 2019/2/14.
//
#include <android/log.h>
#include <jni.h>
#include <android/asset_manager_jni.h>
#include <android/asset_manager.h>
#include <fstream>
#include <stdio.h>
#include <iostream>

using namespace std;

#ifndef ASR_ANDROID_UTILS_H
#define ASR_ANDROID_UTILS_H

jobject getApplicationContext(JNIEnv*);
AAssetManager *getAssertManager(JNIEnv*);
const char * jstring2charArray(JNIEnv* env , jstring str);
#endif //ASR_ANDROID_UTILS_H
