//
// Created by sebas on 23.02.2017.
//

#ifndef BLACKHOLEDARKSUNONLINE4_HOTSHOTGORILLAGUISPRITE_H
#define BLACKHOLEDARKSUNONLINE4_HOTSHOTGORILLAGUISPRITE_H

#include <jni.h>
#include "OgreVector2.h"

namespace Hotshot
{
    enum QuadCorner
    {
        TopLeft,
        TopRight,
        BottomRight,
        BottomLeft
    };

    class Sprite
    {
    public:
        ::Ogre::Vector2 texCoords[4];
        float uvTop, uvLeft, uvRight, uvBottom, spriteWidth, spriteHeight;

        static Sprite readSprite(JNIEnv *env, jobject spr);
        static jobject writeSprite(JNIEnv *env, Sprite& spr);
        static void writeSprite(JNIEnv *env, jobject ret, Sprite& spr);
    };
}

#endif //BLACKHOLEDARKSUNONLINE4_HOTSHOTGORILLAGUISPRITE_H
