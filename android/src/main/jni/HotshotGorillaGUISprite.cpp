//
// Created by sebas on 23.02.2017.
//

#include "HotshotGorillaGUISprite.h"
#include "HotshotVector.h"

namespace Hotshot
{
    static const char *const VECTOR2_SIGNATURE = "[Lheadwayent/hotshotengine/ENG_Vector2D;";

    Sprite Sprite::readSprite(JNIEnv *env, jobject spr)
    {
        jclass jcClass = env->GetObjectClass(spr);
        jfieldID texCoordsId = env->GetFieldID(jcClass, "texCoords", VECTOR2_SIGNATURE);
        jfieldID uvTopId = env->GetFieldID(jcClass, "uvTop", "F");
        jfieldID uvLeftId = env->GetFieldID(jcClass, "uvLeft", "F");
        jfieldID uvRightId = env->GetFieldID(jcClass, "uvRight", "F");
        jfieldID uvBottomId = env->GetFieldID(jcClass, "uvBottom", "F");
        jfieldID spriteWidthId = env->GetFieldID(jcClass, "spriteWidth", "F");
        jfieldID spriteHeightId = env->GetFieldID(jcClass, "spriteHeight", "F");
        Sprite sprite;
        jobjectArray texCoordsArr = reinterpret_cast<jobjectArray>(env->GetObjectField(spr, texCoordsId));
        for (int i = 0; i < 4; ++i)
        {
            jobject texCoord = env->GetObjectArrayElement(texCoordsArr, i);
            sprite.texCoords[i] = Vector::readVector2(env, texCoord);
        }
        jfloat uvTop = env->GetFloatField(spr, uvTopId);
        jfloat uvLeft = env->GetFloatField(spr, uvLeftId);
        jfloat uvRight = env->GetFloatField(spr, uvRightId);
        jfloat uvBottom = env->GetFloatField(spr, uvBottomId);
        jfloat spriteWidth = env->GetFloatField(spr, spriteWidthId);
        jfloat spriteHeight = env->GetFloatField(spr, spriteHeightId);
        sprite.uvTop = uvTop;
        sprite.uvLeft = uvLeft;
        sprite.uvRight = uvRight;
        sprite.uvBottom = uvBottom;
        sprite.spriteWidth = spriteWidth;
        sprite.spriteHeight = spriteHeight;

        return sprite;
    }

    jobject Sprite::writeSprite(JNIEnv *env, Sprite &spr)
    {
        return nullptr;
    }

    void Sprite::writeSprite(JNIEnv *env, jobject ret, Sprite &spr) 
    {
        jclass jcClass = env->GetObjectClass(ret);
        jfieldID texCoordsId = env->GetFieldID(jcClass, "texCoords", VECTOR2_SIGNATURE);
        jfieldID uvTopId = env->GetFieldID(jcClass, "uvTop", "F");
        jfieldID uvLeftId = env->GetFieldID(jcClass, "uvLeft", "F");
        jfieldID uvRightId = env->GetFieldID(jcClass, "uvRight", "F");
        jfieldID uvBottomId = env->GetFieldID(jcClass, "uvBottom", "F");
        jfieldID spriteWidthId = env->GetFieldID(jcClass, "spriteWidth", "F");
        jfieldID spriteHeightId = env->GetFieldID(jcClass, "spriteHeight", "F");
        jobjectArray texCoordsArr = reinterpret_cast<jobjectArray>(env->GetObjectField(ret, texCoordsId));
        for (int i = 0; i < 4; ++i)
        {
            jobject texCoord = env->GetObjectArrayElement(texCoordsArr, i);
            Vector::writeVector2(env, texCoord, spr.texCoords[i]);
        }
        env->SetFloatField(ret, uvTopId, spr.uvTop);
        env->SetFloatField(ret, uvLeftId, spr.uvLeft);
        env->SetFloatField(ret, uvRightId, spr.uvRight);
        env->SetFloatField(ret, uvBottomId, spr.uvBottom);
        env->SetFloatField(ret, spriteWidthId, spr.spriteWidth);
        env->SetFloatField(ret, spriteHeightId, spr.spriteHeight);
    }


}

