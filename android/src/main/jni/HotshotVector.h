//
// Created by sebas on 24.02.2017.
//

#ifndef BLACKHOLEDARKSUNONLINE4_HOTSHOTVECTOR_H
#define BLACKHOLEDARKSUNONLINE4_HOTSHOTVECTOR_H

#include <jni.h>
#include "OgreVector2.h"
#include "OgreVector3.h"
#include "OgreVector4.h"

namespace Hotshot
{
    class Vector
    {
    public:
//        float x, y, z, w;
//
//        Vector() {}
//        Vector(float x, float y, float z, float w);

        static ::Ogre::Vector2 readVector2(JNIEnv *env, jobject v);
        static jobject writeVector2(JNIEnv *env, ::Ogre::Vector2 &v);
        static void writeVector2(JNIEnv *env, jobject ret, ::Ogre::Vector2 &v);

        static ::Ogre::Vector3 readVector3(JNIEnv *env, jobject v);
        static jobject writeVector3(JNIEnv *env, ::Ogre::Vector3 &v);
        static void writeVector3(JNIEnv *env, jobject ret, ::Ogre::Vector3 &v);
        
        static ::Ogre::Vector4 readVector4(JNIEnv *env, jobject v);
        static jobject writeVector4(JNIEnv *env, ::Ogre::Vector4 &v);
        static void writeVector4(JNIEnv *env, jobject ret, ::Ogre::Vector4 &v);
    };
}

#endif //BLACKHOLEDARKSUNONLINE4_HOTSHOTVECTOR_H
