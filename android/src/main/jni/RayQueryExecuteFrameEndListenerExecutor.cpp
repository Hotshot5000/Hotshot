//
// Created by sebas on 20-Aug-17.
//

#include "RayQueryExecuteFrameEndListenerExecutor.h"
#include "CommandBufferUtility.h"

static const int MAX_RAY_SCENE_QUERY_RESPONSE_SIZE = 32;

RayQueryExecuteFrameEndListenerExecutor::RayQueryExecuteFrameEndListenerExecutor(
        Ogre::RaySceneQuery *_raySceneQuery, Ogre::SceneManager *_sceneManager) : FrameEndListenerExecutor(true), raySceneQuery(_raySceneQuery), sceneManager(_sceneManager) {

}

char* RayQueryExecuteFrameEndListenerExecutor::execute(char *wbuf) {
//    char* wbuf = *writeBuffer;
    Ogre::RaySceneQueryResult & raySceneQueryResult = raySceneQuery->execute();
    const int resultSize = raySceneQueryResult.size() < MAX_RAY_SCENE_QUERY_RESPONSE_SIZE ? raySceneQueryResult.size() : MAX_RAY_SCENE_QUERY_RESPONSE_SIZE;
    write(&wbuf, resultSize);
    Ogre::RaySceneQueryResult::iterator it = raySceneQueryResult.begin();
    const Ogre::RaySceneQueryResult::iterator &end = raySceneQueryResult.end();
    int i = 0;
    // Even if we have more than max, only write up to the limit.
    while (it != end && i < resultSize)
    {
        Ogre::RaySceneQueryResultEntry &entry = *it;
        Ogre::Real distance = entry.distance;
        Ogre::MovableObject *movableObject = entry.movable;
        write(&wbuf, distance);
        writePtr(&wbuf, getPointerAsLong(movableObject));
//                    alignPointer(&wbuf, 4);
//                    write(&wbuf, getPointerAsLong(movableObject));
        ++it;
        ++i;
    }
    incrementPointer(&wbuf, (MAX_RAY_SCENE_QUERY_RESPONSE_SIZE - resultSize) * 12);
#ifdef NATIVE_DEBUG
    checkWriteBufferOverflow((MAX_RAY_SCENE_QUERY_RESPONSE_SIZE - resultSize) * 12);
#endif
    sceneManager->destroyQuery(raySceneQuery);
    removePointerFromMap(getPointerAsLong(raySceneQuery), false);
    return wbuf;
}


