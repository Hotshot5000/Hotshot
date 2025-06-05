//
// Created by sebas on 24.02.2017.
//

#include "HotshotVector.h"

namespace Hotshot
{

    ::Ogre::Vector2 Vector::readVector2(JNIEnv *env, jobject v) {
        jclass jcClass = env->GetObjectClass(v);
        jfieldID xId = env->GetFieldID(jcClass, "x", "F");
        jfieldID yId = env->GetFieldID(jcClass, "y", "F");
        jfloat x = env->GetFloatField(v, xId);
        jfloat y = env->GetFloatField(v, yId);
        return ::Ogre::Vector2(x, y);
    }

    jobject Vector::writeVector2(JNIEnv *env, ::Ogre::Vector2 &v)
    {
        // TODO implementation missing.
        return nullptr;
    }

    void Vector::writeVector2(JNIEnv *env, jobject ret, ::Ogre::Vector2 &v)
    {
        jclass jcClass = env->GetObjectClass(ret);
        jfieldID xId = env->GetFieldID(jcClass, "x", "F");
        jfieldID yId = env->GetFieldID(jcClass, "y", "F");
        env->SetFloatField(ret, xId, v.x);
        env->SetFloatField(ret, yId, v.y);
    }

    ::Ogre::Vector3 Vector::readVector3(JNIEnv *env, jobject v) {
        jclass jcClass = env->GetObjectClass(v);
        jfieldID xId = env->GetFieldID(jcClass, "x", "F");
        jfieldID yId = env->GetFieldID(jcClass, "y", "F");
        jfieldID zId = env->GetFieldID(jcClass, "z", "F");
        jfloat x = env->GetFloatField(v, xId);
        jfloat y = env->GetFloatField(v, yId);
        jfloat z = env->GetFloatField(v, zId);
        return ::Ogre::Vector3(x, y, z);
    }

    jobject Vector::writeVector3(JNIEnv *env, ::Ogre::Vector3 &v)
    {
        // TODO implementation missing.
        return nullptr;
    }

    void Vector::writeVector3(JNIEnv *env, jobject ret, ::Ogre::Vector3 &v)
    {
        jclass jcClass = env->GetObjectClass(ret);
        jfieldID xId = env->GetFieldID(jcClass, "x", "F");
        jfieldID yId = env->GetFieldID(jcClass, "y", "F");
        jfieldID zId = env->GetFieldID(jcClass, "z", "F");
        env->SetFloatField(ret, xId, v.x);
        env->SetFloatField(ret, yId, v.y);
        env->SetFloatField(ret, zId, v.z);
    }

    ::Ogre::Vector4 Vector::readVector4(JNIEnv *env, jobject v) {
        jclass jcClass = env->GetObjectClass(v);
        jfieldID xId = env->GetFieldID(jcClass, "x", "F");
        jfieldID yId = env->GetFieldID(jcClass, "y", "F");
        jfieldID zId = env->GetFieldID(jcClass, "z", "F");
        jfieldID wId = env->GetFieldID(jcClass, "w", "F");
        jfloat x = env->GetFloatField(v, xId);
        jfloat y = env->GetFloatField(v, yId);
        jfloat z = env->GetFloatField(v, zId);
        jfloat w = env->GetFloatField(v, wId);
        return ::Ogre::Vector4(x, y, z, w);
    }

    jobject Vector::writeVector4(JNIEnv *env, ::Ogre::Vector4 &v)
    {
        // TODO implementation missing.
        return nullptr;
    }

    void Vector::writeVector4(JNIEnv *env, jobject ret, ::Ogre::Vector4 &v)
    {
        jclass jcClass = env->GetObjectClass(ret);
        jfieldID xId = env->GetFieldID(jcClass, "x", "F");
        jfieldID yId = env->GetFieldID(jcClass, "y", "F");
        jfieldID zId = env->GetFieldID(jcClass, "z", "F");
        jfieldID wId = env->GetFieldID(jcClass, "w", "F");
        env->SetFloatField(ret, xId, v.x);
        env->SetFloatField(ret, yId, v.y);
        env->SetFloatField(ret, zId, v.z);
        env->SetFloatField(ret, wId, v.w);
    }



//    Vector::Vector(float x_, float y_, float z_, float w_) : x(x_), y(y_), z(z_), w(w_) {
//
//    }


}





