//
// Created by Sebastian Bugiu on 23/07/2018.
//

#ifndef BLACKHOLEDARKSUNONLINE6_METALVIEWCALLBACKSIMPL_H
#define BLACKHOLEDARKSUNONLINE6_METALVIEWCALLBACKSIMPL_H

#include <jni.h>
#include "MetalViewCallbacks.hpp"

class MetalViewCallbacksImpl : public MetalViewCallbacks
{
private:
    JNIEnv *env;
    jobject iosInput;
    jmethodID toTouchEvents;
    void processTouchEvent(void *touches);
public:
    MetalViewCallbacksImpl(JNIEnv *_env, jobject iosInput_);
    virtual void touchesBegan(void *touches);
    virtual void touchesMoved(void *touches);
    virtual void touchesEnded(void *touches);
    virtual void touchesCancelled(void *touches);
};

#endif //BLACKHOLEDARKSUNONLINE6_METALVIEWCALLBACKSIMPL_H
