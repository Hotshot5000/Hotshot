//
// Created by Sebastian Bugiu on 23/07/2018.
//

#include "MetalViewCallbacksImpl.h"
#include "CommandBufferUtility.h"
#include "HotshotCommon.h"

MetalViewCallbacksImpl::MetalViewCallbacksImpl(JNIEnv *_env, jobject iosInput_) : env(_env), iosInput(iosInput_)
{
    jclass iosInputClazz = env->FindClass("com/badlogic/gdx/backends/iosrobovm/DefaultIOSInput");
    toTouchEvents = env->GetMethodID(iosInputClazz, "toTouchEvents", "(J)V");

}

void MetalViewCallbacksImpl::touchesBegan(void *touches)
{
    //LOGI("%s", "MetalViewCallbacksImpl::touchesBegan\n");
    processTouchEvent(touches);
}

void MetalViewCallbacksImpl::touchesMoved(void *touches)
{
    //LOGI("%s", "MetalViewCallbacksImpl::touchesMoved\n");
    processTouchEvent(touches);
}

void MetalViewCallbacksImpl::touchesEnded(void *touches)
{
    //LOGI("%s", "MetalViewCallbacksImpl::touchesEnded\n");
    processTouchEvent(touches);
}

void MetalViewCallbacksImpl::touchesCancelled(void *touches)
{
    //LOGI("%s", "MetalViewCallbacksImpl::touchesCancelled\n");
    processTouchEvent(touches);
}

void MetalViewCallbacksImpl::processTouchEvent(void *touches)
{
    long long int touchesPtr = getPointerAsLong(touches);
    env->CallVoidMethod(iosInput, toTouchEvents, touchesPtr);
}
