//
// Created by sebas on 20-Aug-17.
//

#ifndef BLACKHOLEDARKSUNONLINE5_RAYQUERYEXECUTEFRAMEENDLISTENEREXECUTOR_H
#define BLACKHOLEDARKSUNONLINE5_RAYQUERYEXECUTEFRAMEENDLISTENEREXECUTOR_H


#include <OgreSceneQuery.h>
#include "FrameEndListenerExecutor.h"

class RayQueryExecuteFrameEndListenerExecutor : public FrameEndListenerExecutor {
private:
    Ogre::RaySceneQuery *raySceneQuery;
    Ogre::SceneManager *sceneManager;
public:
    RayQueryExecuteFrameEndListenerExecutor(Ogre::RaySceneQuery *_raySceneQuery, Ogre::SceneManager *_sceneManager);
    virtual ~RayQueryExecuteFrameEndListenerExecutor() {}
    virtual char* execute(char* writeBuffer);
};


#endif //BLACKHOLEDARKSUNONLINE5_RAYQUERYEXECUTEFRAMEENDLISTENEREXECUTOR_H
