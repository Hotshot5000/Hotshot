//
// Created by sebas on 17.06.2017.
//

#include "HotshotCommon.h"
#include "JniCommon.h"

JavaVM *jvm = 0;
jclass windowsDisplayClass = 0;
jmethodID javaWindowProc = 0;

void initJavaVM(JNIEnv *env)
{
    jint i = env->GetJavaVM(&jvm);
    if (i != 0)
    {
        LOGI("Could not initialize jvm pointer");
    }
}



JNIEnv *getThreadEnv() {
    JNIEnv *env;
    jvm->GetEnv((void**)&env, JNI_VERSION_1_4);
    return env;
}



void detachCurrentThread() {
    jvm->DetachCurrentThread();
}

JNIEnv *attachCurrentThread() {
    JNIEnv *env;
    jvm->AttachCurrentThread((void**)&env, NULL);
    return env;
}


