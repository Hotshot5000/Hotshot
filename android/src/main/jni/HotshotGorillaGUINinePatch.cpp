//
// Created by sebas on 23.02.2017.
//

#include "HotshotGorillaGUINinePatch.h"

namespace Hotshot
{
    static const char *const SPRITE_SIGNATURE = "[Lheadwayent/hotshotengine/gorillagui/ENG_Sprite;";

    NinePatch NinePatch::readNinePatch(JNIEnv *env, jobject np)
    {
        jclass jcClass = env->GetObjectClass(np);
        jfieldID stretchableAreaId = env->GetFieldID(jcClass, "stretchableArea", SPRITE_SIGNATURE);
        jfieldID drawableAreaId = env->GetFieldID(jcClass, "drawableArea", SPRITE_SIGNATURE);
        jfieldID uvTopId = env->GetFieldID(jcClass, "uvTop", "F");
        jfieldID uvLeftId = env->GetFieldID(jcClass, "uvLeft", "F");
        jfieldID uvRightId = env->GetFieldID(jcClass, "uvRight", "F");
        jfieldID uvBottomId = env->GetFieldID(jcClass, "uvBottom", "F");
        jfieldID spriteWidthInPixelsId = env->GetFieldID(jcClass, "spriteWidthInPixels", "F");
        jfieldID spriteHeightInPixelsId = env->GetFieldID(jcClass, "spriteHeightInPixels", "F");
        jfieldID uvTopInPixelsId = env->GetFieldID(jcClass, "uvTopInPixels", "F");
        jfieldID uvLeftInPixelsId = env->GetFieldID(jcClass, "uvLeftInPixels", "F");
        jfieldID uvRightInPixelsId = env->GetFieldID(jcClass, "uvRightInPixels", "F");
        jfieldID uvBottomInPixelsId = env->GetFieldID(jcClass, "uvBottomInPixels", "F");
        NinePatch ninePatch;
        jobjectArray stretchableAreaArr = reinterpret_cast<jobjectArray>(env->GetObjectField(np, stretchableAreaId));
        jobjectArray drawableAreaArr = reinterpret_cast<jobjectArray>(env->GetObjectField(np, drawableAreaId));
        for (int i = 0; i < 9; ++i)
        {
            jobject stretchableArea = env->GetObjectArrayElement(stretchableAreaArr, i);
            jobject drawableArea = env->GetObjectArrayElement(drawableAreaArr, i);
            ninePatch.stretchableArea[i] = Sprite::readSprite(env, stretchableArea);
            ninePatch.drawableArea[i] = Sprite::readSprite(env, drawableArea);
        }
        jfloat uvTop = env->GetFloatField(np, uvTopId);
        jfloat uvLeft = env->GetFloatField(np, uvLeftId);
        jfloat uvRight = env->GetFloatField(np, uvRightId);
        jfloat uvBottom = env->GetFloatField(np, uvBottomId);
        jfloat spriteWidthInPixels = env->GetFloatField(np, spriteWidthInPixelsId);
        jfloat spriteHeightInPixels = env->GetFloatField(np, spriteHeightInPixelsId);
        jfloat uvTopInPixels = env->GetFloatField(np, uvTopInPixelsId);
        jfloat uvLeftInPixels = env->GetFloatField(np, uvLeftInPixelsId);
        jfloat uvRightInPixels = env->GetFloatField(np, uvRightInPixelsId);
        jfloat uvBottomInPixels = env->GetFloatField(np, uvBottomInPixelsId);
        ninePatch.uvTop = uvTop;
        ninePatch.uvLeft = uvLeft;
        ninePatch.uvRight = uvRight;
        ninePatch.uvBottom = uvBottom;
        ninePatch.spriteWidthInPixels = spriteWidthInPixels;
        ninePatch.spriteHeightInPixels = spriteHeightInPixels;
        ninePatch.uvTopInPixels = uvTopInPixels;
        ninePatch.uvLeftInPixels = uvLeftInPixels;
        ninePatch.uvRightInPixels = uvRightInPixels;
        ninePatch.uvBottomInPixels = uvBottomInPixels;

        return ninePatch;
    }

    jobject NinePatch::writeNinePatch(JNIEnv *env, NinePatch &np)
    {
        return nullptr;
    }

    void NinePatch::writeNinePatch(JNIEnv *env, jobject ret, NinePatch &np)
    {
        jclass jcClass = env->GetObjectClass(ret);
        jfieldID stretchableAreaId = env->GetFieldID(jcClass, "stretchableArea", SPRITE_SIGNATURE);
        jfieldID drawableAreaId = env->GetFieldID(jcClass, "drawableArea", SPRITE_SIGNATURE);
        jfieldID uvTopId = env->GetFieldID(jcClass, "uvTop", "F");
        jfieldID uvLeftId = env->GetFieldID(jcClass, "uvLeft", "F");
        jfieldID uvRightId = env->GetFieldID(jcClass, "uvRight", "F");
        jfieldID uvBottomId = env->GetFieldID(jcClass, "uvBottom", "F");
        jfieldID spriteWidthInPixelsId = env->GetFieldID(jcClass, "spriteWidthInPixels", "F");
        jfieldID spriteHeightInPixelsId = env->GetFieldID(jcClass, "spriteHeightInPixels", "F");
        jfieldID uvTopInPixelsId = env->GetFieldID(jcClass, "uvTopInPixels", "F");
        jfieldID uvLeftInPixelsId = env->GetFieldID(jcClass, "uvLeftInPixels", "F");
        jfieldID uvRightInPixelsId = env->GetFieldID(jcClass, "uvRightInPixels", "F");
        jfieldID uvBottomInPixelsId = env->GetFieldID(jcClass, "uvBottomInPixels", "F");
        NinePatch &ninePatch = np;
        jobjectArray stretchableAreaArr = reinterpret_cast<jobjectArray>(env->GetObjectField(ret, stretchableAreaId));
        jobjectArray drawableAreaArr = reinterpret_cast<jobjectArray>(env->GetObjectField(ret, drawableAreaId));
        for (int i = 0; i < 9; ++i)
        {
            jobject stretchableArea = env->GetObjectArrayElement(stretchableAreaArr, i);
            jobject drawableArea = env->GetObjectArrayElement(drawableAreaArr, i);
            Sprite::writeSprite(env, stretchableArea, ninePatch.stretchableArea[i]);
            Sprite::writeSprite(env, drawableArea, ninePatch.drawableArea[i]);
        }
        env->SetFloatField(ret, uvTopId, ninePatch.uvTop);
        env->SetFloatField(ret, uvLeftId, ninePatch.uvLeft);
        env->SetFloatField(ret, uvRightId, ninePatch.uvRight);
        env->SetFloatField(ret, uvBottomId, ninePatch.uvBottom);
        env->SetFloatField(ret, spriteWidthInPixelsId, ninePatch.spriteWidthInPixels);
        env->SetFloatField(ret, spriteHeightInPixelsId, ninePatch.spriteHeightInPixels);
        env->SetFloatField(ret, uvTopInPixelsId, ninePatch.uvTopInPixels);
        env->SetFloatField(ret, uvLeftInPixelsId, ninePatch.uvLeftInPixels);
        env->SetFloatField(ret, uvRightInPixelsId, ninePatch.uvRightInPixels);
        env->SetFloatField(ret, uvBottomInPixelsId, ninePatch.uvBottomInPixels);
    }


}




