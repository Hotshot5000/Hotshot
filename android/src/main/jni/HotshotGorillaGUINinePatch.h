//
// Created by sebas on 23.02.2017.
//

#ifndef BLACKHOLEDARKSUNONLINE4_HOTSHOTGORILLAGUININEPATCH_H
#define BLACKHOLEDARKSUNONLINE4_HOTSHOTGORILLAGUININEPATCH_H

#include "HotshotGorillaGUISprite.h"

namespace Hotshot
{
    enum NinePatchArea {
        NPA_TOP_LEFT, NPA_TOP_MIDDLE, NPA_TOP_RIGHT,
        NPA_MIDDLE_LEFT, NPA_MIDDLE_MIDDLE, NPA_MIDDLE_RIGHT,
        NPA_BOTTOM_LEFT, NPA_BOTTOM_MIDDLE, NPA_BOTTOM_RIGHT
    };

    enum NinePatchPoint {
        NPP_TOP_LEFT, NPP_TOP_MIDDLE_LEFT, NPP_TOP_MIDDLE_RIGHT, NPP_TOP_RIGHT,
        NPP_MIDDLE_TOP_LEFT, NPP_MIDDLE_TOP_MIDDLE_LEFT, NPP_MIDDLE_TOP_MIDDLE_RIGHT, NPP_MIDDLE_TOP_RIGHT,
        NPP_MIDDLE_BOTTOM_LEFT, NPP_MIDDLE_BOTTOM_MIDDLE_LEFT, NPP_MIDDLE_BOTTOM_MIDDLE_RIGHT, NPP_MIDDLE_BOTTOM_RIGHT,
        NPP_BOTTOM_LEFT, NPP_BOTTOM_MIDDLE_LEFT, NPP_BOTTOM_MIDDLE_RIGHT, NPP_BOTTOM_RIGHT
    };

    class NinePatch
    {
    public:
        Sprite stretchableArea[9];
        Sprite drawableArea[9];
        float uvTop, uvLeft, uvRight, uvBottom, spriteWidthInPixels, spriteHeightInPixels;
        float uvTopInPixels, uvLeftInPixels, uvRightInPixels, uvBottomInPixels;

        static NinePatch readNinePatch(JNIEnv *env, jobject np);
        static jobject writeNinePatch(JNIEnv *env, NinePatch& np);
        static void writeNinePatch(JNIEnv *env, jobject ret, NinePatch& np);
    };
}

#endif //BLACKHOLEDARKSUNONLINE4_HOTSHOTGORILLAGUININEPATCH_H
