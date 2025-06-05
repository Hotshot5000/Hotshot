//
// Created by Sebastian Bugiu on 22/07/2018.
//

#include "GameViewControllerImpl.h"
#include "HotshotCommon.h"

//#include <jni.h>

GameViewControllerImpl::GameViewControllerImpl(JNIEnv *_env, jobject main_) : env(_env), main(main_)
{

}

void GameViewControllerImpl::dealloc()
{
    LOGI("GameViewControllerImpl::dealloc()\n");
}

void GameViewControllerImpl::viewDidLoad()
{
    // This method is only called once so there is no need to chache the jclass and all of that.
    LOGI("GameViewControllerImpl::viewDidLoad()\n");
    mainClazz = env->FindClass("headwayent/blackholedarksun/BlackholeDarksunMain");
    render = env->GetMethodID(mainClazz, "render", "()V");

    jclass gameClazz = env->FindClass("headwayent/blackholedarksun/APP_Game");
    jmethodID notifyViewDidLoad = env->GetStaticMethodID(gameClazz, "notifyViewDidLoad", "()V");
    env->CallStaticVoidMethod(gameClazz, notifyViewDidLoad);

}

void GameViewControllerImpl::viewWillAppear(bool animated)
{
    LOGI("GameViewControllerImpl::viewWillAppear\n");
}

void GameViewControllerImpl::viewWillDisappear(bool animated)
{
    LOGI("GameViewControllerImpl::viewWillDisappear\n");
}

void GameViewControllerImpl::mainLoop()
{
//    LOGI("GameViewControllerImpl::mainLoop()\n");
    env->CallVoidMethod(main, render);
}

