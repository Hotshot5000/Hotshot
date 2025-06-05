//
// Created by sebas on 17.06.2017.
//

#ifndef BLACKHOLEDARKSUNONLINE5_JNICOMMON_H
#define BLACKHOLEDARKSUNONLINE5_JNICOMMON_H

#include <jni.h>

extern JavaVM *jvm;
extern jclass windowsDisplayClass;
extern jmethodID javaWindowProc;

void initJavaVM(JNIEnv *env);

JNIEnv *getThreadEnv();

JNIEnv *attachCurrentThread();

void detachCurrentThread();

#endif //BLACKHOLEDARKSUNONLINE5_JNICOMMON_H
