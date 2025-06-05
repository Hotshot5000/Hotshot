//
// Created by Sebastian Bugiu on 22/07/2018.
//

#ifndef BLACKHOLEDARKSUNONLINE6_GAMEVIEWCONTROLLERIMPL_H
#define BLACKHOLEDARKSUNONLINE6_GAMEVIEWCONTROLLERIMPL_H

#include "GameViewControllerCallbacks.h"

#include <jni.h>

class GameViewControllerImpl : public GameViewControllerCallbacks
{
private:
    JNIEnv *env;
    jobject main;
    jclass mainClazz;
    jmethodID render;
public:
    GameViewControllerImpl(JNIEnv *_env, jobject main_);
    virtual void dealloc();
    virtual void viewDidLoad();
    virtual void viewWillAppear(bool animated);
    virtual void viewWillDisappear(bool animated);
    virtual void mainLoop();
};

#endif //BLACKHOLEDARKSUNONLINE6_GAMEVIEWCONTROLLERIMPL_H
