/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/16/21, 8:32 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import java.text.NumberFormat;

import headwayent.hotshotengine.basictypes.ENG_Float;

public class ENG_Vector4D {

    public float x, y, z, w;

    public ENG_Vector4D() {

    }

    public ENG_Vector4D(boolean asPt) {
        if (asPt) {
            w = 1.0f;
        }
    }

    public ENG_Vector4D(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public ENG_Vector4D(float[] vec) {
        set(vec);
    }

    public ENG_Vector4D(float[] vec, int offset) {
        set(vec, offset);
    }

    public ENG_Vector4D(ENG_Vector4D vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        this.w = vec.w;
    }

    public ENG_Vector4D(ENG_Vector3D vec) {
        this(vec, false);
    }

    public ENG_Vector4D(ENG_Vector3D vec, boolean asPt) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        if (asPt) {
            this.w = 1.0f;
        }
    }

    public ENG_Vector4D(float scalar) {
        this.x = scalar;
        this.y = scalar;
        this.z = scalar;
        this.w = scalar;
    }

    public void swap(ENG_Vector4D vec) {
        float temp = x;
        x = vec.x;
        vec.x = temp;
        temp = y;
        y = vec.y;
        vec.y = temp;
        temp = z;
        z = vec.z;
        vec.z = temp;
        temp = w;
        w = vec.w;
        vec.w = temp;
    }

    public void setVec() {
        w = 0.0f;
    }

    public void setPt() {
        w = 1.0f;
    }

    public boolean isPt() {
        return w == 1.0f;
    }

    public boolean isVec() {
        return w == 0.0f;
    }

    public void set(float[] vec) {
        this.x = vec[0];
        this.y = vec[1];
        this.z = vec[2];
        this.w = vec[3];
    }

    public void set(float[] vec, int offset) {
        this.x = vec[offset];
        this.y = vec[offset + 1];
        this.z = vec[offset + 2];
        this.w = vec[offset + 3];
    }

    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;

    }

    public void set(ENG_Vector3D vec) {
        x = vec.x;
        y = vec.y;
        z = vec.z;
        w = 1.0f;
    }

    public void set(ENG_Vector4D vec) {
        x = vec.x;
        y = vec.y;
        z = vec.z;
        w = vec.w;
    }

    public void get(ENG_Vector2D vec) {
        vec.x = x;
        vec.y = y;
    }

    public void get(ENG_Vector3D vec) {
        vec.x = x;
        vec.y = y;
        vec.z = z;
    }

    public void get(ENG_Vector4D vec) {
        vec.x = x;
        vec.y = y;
        vec.z = z;
        vec.w = w;
    }

    public float get(int index) {
        switch (index) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
            case 3:
                return w;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void set(float scalar) {
        x = scalar;
        y = scalar;
        z = scalar;
        w = scalar;
    }

    public static ENG_Vector4D createPoint() {
        return new ENG_Vector4D(true);
    }

    public static ENG_Vector4D createVector() {
        return new ENG_Vector4D();
    }

    public boolean equalsFast(ENG_Vector4D vec) {
        return ((x == vec.x) && (y == vec.y) && (z == vec.z));
    }

    public boolean notEqualsFast(ENG_Vector4D vec) {
        return ((x != vec.x) || (y != vec.y) || (z != vec.z));
    }

    public boolean equals(ENG_Vector4D vec) {
        return ((ENG_Float.compareTo(x, vec.x) == ENG_Utility.COMPARE_EQUAL_TO) &&
                (ENG_Float.compareTo(y, vec.y) == ENG_Utility.COMPARE_EQUAL_TO) &&
                (ENG_Float.compareTo(z, vec.z) == ENG_Utility.COMPARE_EQUAL_TO));
    }

    public boolean notEquals(ENG_Vector4D vec) {
        return ((ENG_Float.compareTo(x, vec.x) != ENG_Utility.COMPARE_EQUAL_TO) ||
                (ENG_Float.compareTo(y, vec.y) != ENG_Utility.COMPARE_EQUAL_TO) ||
                (ENG_Float.compareTo(z, vec.z) != ENG_Utility.COMPARE_EQUAL_TO));
    }

    public boolean equalsFastFull(ENG_Vector4D vec) {
        return ((x == vec.x) && (y == vec.y) && (z == vec.z) && (w == vec.w));
    }

    public boolean notEqualsFastFull(ENG_Vector4D vec) {
        return ((x != vec.x) || (y != vec.y) || (z != vec.z) || (w != vec.w));
    }

    public boolean equalsFull(ENG_Vector4D vec) {
        return ((ENG_Float.compareTo(x, vec.x) == ENG_Utility.COMPARE_EQUAL_TO) &&
                (ENG_Float.compareTo(y, vec.y) == ENG_Utility.COMPARE_EQUAL_TO) &&
                (ENG_Float.compareTo(z, vec.z) == ENG_Utility.COMPARE_EQUAL_TO) &&
                (ENG_Float.compareTo(w, vec.w) == ENG_Utility.COMPARE_EQUAL_TO));
    }

    public boolean notEqualsFull(ENG_Vector4D vec) {
        return ((ENG_Float.compareTo(x, vec.x) != ENG_Utility.COMPARE_EQUAL_TO) ||
                (ENG_Float.compareTo(y, vec.y) != ENG_Utility.COMPARE_EQUAL_TO) ||
                (ENG_Float.compareTo(z, vec.z) != ENG_Utility.COMPARE_EQUAL_TO) ||
                (ENG_Float.compareTo(w, vec.w) != ENG_Utility.COMPARE_EQUAL_TO));
    }

    public ENG_Vector4D addAsVec(ENG_Vector4D vec) {
        return new ENG_Vector4D(x + vec.x, y + vec.y, z + vec.z, 0.0f);
    }

    public ENG_Vector4D addAsPt(ENG_Vector4D vec) {
        return new ENG_Vector4D(x + vec.x, y + vec.y, z + vec.z, 1.0f);
    }

    public ENG_Vector4D addAsVec(ENG_Vector3D vec) {
        return new ENG_Vector4D(x + vec.x, y + vec.y, z + vec.z, 0.0f);
    }

    public ENG_Vector4D addAsPt(ENG_Vector3D vec) {
        return new ENG_Vector4D(x + vec.x, y + vec.y, z + vec.z, 1.0f);
    }

    public ENG_Vector4D addFull(ENG_Vector4D vec) {
        return new ENG_Vector4D(x + vec.x, y + vec.y, z + vec.z, w + vec.w);
    }

    public void add(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = x + vec.x;
        ret.y = y + vec.y;
        ret.z = z + vec.z;
    }

    public void add(ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = x + vec.x;
        ret.y = y + vec.y;
        ret.z = z + vec.z;
    }

    public void add(ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = x + vec.x;
        ret.y = y + vec.y;
        ret.z = z + vec.z;
    }

    public void add(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = x + vec.x;
        ret.y = y + vec.y;
        ret.z = z + vec.z;
    }

    public void addFull(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = x + vec.x;
        ret.y = y + vec.y;
        ret.z = z + vec.z;
        ret.w = w + vec.w;
    }

    public ENG_Vector4D addRet(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = x + vec.x;
        ret.y = y + vec.y;
        ret.z = z + vec.z;
        return ret;
    }

    public ENG_Vector4D addRetFull(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = x + vec.x;
        ret.y = y + vec.y;
        ret.z = z + vec.z;
        ret.w = w + vec.w;
        return ret;
    }

    public ENG_Vector4D addRet(ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = x + vec.x;
        ret.y = y + vec.y;
        ret.z = z + vec.z;
        return ret;
    }

    public ENG_Vector3D addRet(ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = x + vec.x;
        ret.y = y + vec.y;
        ret.z = z + vec.z;
        return ret;
    }

    public ENG_Vector3D addRet(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = x + vec.x;
        ret.y = y + vec.y;
        ret.z = z + vec.z;
        return ret;
    }

    public ENG_Vector4D subAsPt(ENG_Vector4D vec) {
        return new ENG_Vector4D(x - vec.x, y - vec.y, z - vec.z, 1.0f);
    }

    public ENG_Vector4D subAsVec(ENG_Vector4D vec) {
        return new ENG_Vector4D(x - vec.x, y - vec.y, z - vec.z, 0.0f);
    }

    public ENG_Vector4D subAsPt(ENG_Vector3D vec) {
        return new ENG_Vector4D(x - vec.x, y - vec.y, z - vec.z, 1.0f);
    }

    public ENG_Vector4D subAsVec(ENG_Vector3D vec) {
        return new ENG_Vector4D(x - vec.x, y - vec.y, z - vec.z, 0.0f);
    }

    public ENG_Vector4D subFull(ENG_Vector4D vec) {
        return new ENG_Vector4D(x - vec.x, y - vec.y, z - vec.z, w - vec.w);
    }

    public void sub(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = x - vec.x;
        ret.y = y - vec.y;
        ret.z = z - vec.z;
    }

    public void sub(ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = x - vec.x;
        ret.y = y - vec.y;
        ret.z = z - vec.z;
    }

    public void sub(ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = x - vec.x;
        ret.y = y - vec.y;
        ret.z = z - vec.z;
    }

    public void sub(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = x - vec.x;
        ret.y = y - vec.y;
        ret.z = z - vec.z;
    }

    public void subFull(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = x - vec.x;
        ret.y = y - vec.y;
        ret.z = z - vec.z;
        ret.w = w - vec.w;
    }

    public ENG_Vector4D subRet(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = x - vec.x;
        ret.y = y - vec.y;
        ret.z = z - vec.z;
        return ret;
    }

    public ENG_Vector4D subRetFull(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = x - vec.x;
        ret.y = y - vec.y;
        ret.z = z - vec.z;
        ret.w = w - vec.w;
        return ret;
    }

    public ENG_Vector4D subRet(ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = x - vec.x;
        ret.y = y - vec.y;
        ret.z = z - vec.z;
        return ret;
    }

    public ENG_Vector3D subRet(ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = x - vec.x;
        ret.y = y - vec.y;
        ret.z = z - vec.z;
        return ret;
    }

    public ENG_Vector3D subRet(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = x - vec.x;
        ret.y = y - vec.y;
        ret.z = z - vec.z;
        return ret;
    }

    public ENG_Vector4D mulAsPt(float scalar) {
        return new ENG_Vector4D(x * scalar, y * scalar, z * scalar, 1.0f);
    }

    public ENG_Vector4D mulAsVec(float scalar) {
        return new ENG_Vector4D(x * scalar, y * scalar, z * scalar, 0.0f);
    }

    public ENG_Vector4D mulFullRet(float scalar) {
        return new ENG_Vector4D(x * scalar, y * scalar, z * scalar, w * scalar);
    }

    public void mul(float scalar, ENG_Vector4D ret) {
        ret.x = x * scalar;
        ret.y = y * scalar;
        ret.z = z * scalar;
    }

    public void mulFull(float scalar, ENG_Vector4D ret) {
        ret.x = x * scalar;
        ret.y = y * scalar;
        ret.z = z * scalar;
        ret.w = w * scalar;
    }

    public void mul(float scalar, ENG_Vector3D ret) {
        ret.x = x * scalar;
        ret.y = y * scalar;
        ret.z = z * scalar;
    }

    public ENG_Vector4D mulRet(float scalar, ENG_Vector4D ret) {
        ret.x = x * scalar;
        ret.y = y * scalar;
        ret.z = z * scalar;
        return ret;
    }

    public ENG_Vector4D mulRetFull(float scalar, ENG_Vector4D ret) {
        ret.x = x * scalar;
        ret.y = y * scalar;
        ret.z = z * scalar;
        ret.w = w * scalar;
        return ret;
    }

    public ENG_Vector3D mulRet(float scalar, ENG_Vector3D ret) {
        ret.x = x * scalar;
        ret.y = y * scalar;
        ret.z = z * scalar;
        return ret;
    }

    public ENG_Vector4D mulAsPt(ENG_Vector4D vec) {
        return new ENG_Vector4D(x * vec.x, y * vec.y, z * vec.z, 1.0f);
    }

    public ENG_Vector4D mulAsVec(ENG_Vector4D vec) {
        return new ENG_Vector4D(x * vec.x, y * vec.y, z * vec.z, 0.0f);
    }

    public ENG_Vector4D mulAsPt(ENG_Vector3D vec) {
        return new ENG_Vector4D(x * vec.x, y * vec.y, z * vec.z, 1.0f);
    }

    public ENG_Vector4D mulAsVec(ENG_Vector3D vec) {
        return new ENG_Vector4D(x * vec.x, y * vec.y, z * vec.z, 0.0f);
    }

    public ENG_Vector4D mulFull(ENG_Vector4D vec) {
        return new ENG_Vector4D(x * vec.x, y * vec.y, z * vec.z, w * vec.w);
    }

    public void mul(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = x * vec.x;
        ret.y = y * vec.y;
        ret.z = z * vec.z;
    }

    public void mul(ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = x * vec.x;
        ret.y = y * vec.y;
        ret.z = z * vec.z;
    }

    public void mul(ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = x * vec.x;
        ret.y = y * vec.y;
        ret.z = z * vec.z;
    }

    public void mul(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = x * vec.x;
        ret.y = y * vec.y;
        ret.z = z * vec.z;
    }

    public void mulFull(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = x * vec.x;
        ret.y = y * vec.y;
        ret.z = z * vec.z;
        ret.w = w * vec.w;
    }

    public ENG_Vector4D mulRet(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = x * vec.x;
        ret.y = y * vec.y;
        ret.z = z * vec.z;
        return ret;
    }

    public ENG_Vector4D mulRetFull(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = x * vec.x;
        ret.y = y * vec.y;
        ret.z = z * vec.z;
        ret.w = w * vec.w;
        return ret;
    }

    public ENG_Vector4D mulRet(ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = x * vec.x;
        ret.y = y * vec.y;
        ret.z = z * vec.z;
        return ret;
    }

    public ENG_Vector3D mulRet(ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = x * vec.x;
        ret.y = y * vec.y;
        ret.z = z * vec.z;
        return ret;
    }

    public ENG_Vector3D mulRet(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = x * vec.x;
        ret.y = y * vec.y;
        ret.z = z * vec.z;
        return ret;
    }

    public ENG_Vector4D divAsPt(float scalar) {
        float inv = 1.0f / scalar;
        return new ENG_Vector4D(x * inv, y * inv, z * inv, 1.0f);
    }

    public ENG_Vector4D divAsVec(float scalar) {
        float inv = 1.0f / scalar;
        return new ENG_Vector4D(x * inv, y * inv, z * inv, 0.0f);
    }

    public ENG_Vector4D divFull(float scalar) {
        float inv = 1.0f / scalar;
        return new ENG_Vector4D(x * inv, y * inv, z * inv, w * inv);
    }

    public void div(float scalar, ENG_Vector4D ret) {
        float inv = 1.0f / scalar;
        ret.x = x * inv;
        ret.y = y * inv;
        ret.z = z * inv;
    }

    public void div(float scalar, ENG_Vector3D ret) {
        float inv = 1.0f / scalar;
        ret.x = x * inv;
        ret.y = y * inv;
        ret.z = z * inv;
    }

    public void divFull(float scalar, ENG_Vector4D ret) {
        float inv = 1.0f / scalar;
        ret.x = x * inv;
        ret.y = y * inv;
        ret.z = z * inv;
        ret.w = w * inv;
    }

    public ENG_Vector4D divRet(float scalar, ENG_Vector4D ret) {
        float inv = 1.0f / scalar;
        ret.x = x * inv;
        ret.y = y * inv;
        ret.z = z * inv;
        return ret;
    }

    public ENG_Vector4D divRetFull(float scalar, ENG_Vector4D ret) {
        float inv = 1.0f / scalar;
        ret.x = x * inv;
        ret.y = y * inv;
        ret.z = z * inv;
        ret.w = w * inv;
        return ret;
    }

    public ENG_Vector3D divRet(float scalar, ENG_Vector3D ret) {
        float inv = 1.0f / scalar;
        ret.x = x * inv;
        ret.y = y * inv;
        ret.z = z * inv;
        return ret;
    }

    public ENG_Vector4D divAsPt(ENG_Vector4D vec) {
        return new ENG_Vector4D(x / vec.x, y / vec.y, z / vec.z, 1.0f);
    }

    public ENG_Vector4D divAsVec(ENG_Vector4D vec) {
        return new ENG_Vector4D(x / vec.x, y / vec.y, z / vec.z, 0.0f);
    }

    public ENG_Vector4D divAsPt(ENG_Vector3D vec) {
        return new ENG_Vector4D(x / vec.x, y / vec.y, z / vec.z, 1.0f);
    }

    public ENG_Vector4D divAsVec(ENG_Vector3D vec) {
        return new ENG_Vector4D(x / vec.x, y / vec.y, z / vec.z, 0.0f);
    }

    public ENG_Vector4D divFullRet(ENG_Vector4D vec) {
        return new ENG_Vector4D(x / vec.x, y / vec.y, z / vec.z, w / vec.w);
    }

    public void div(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = x / vec.x;
        ret.y = y / vec.y;
        ret.z = z / vec.z;
    }

    public void divFull(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = x / vec.x;
        ret.y = y / vec.y;
        ret.z = z / vec.z;
        ret.w = w / vec.w;
    }

    public void div(ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = x / vec.x;
        ret.y = y / vec.y;
        ret.z = z / vec.z;
    }

    public void div(ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = x / vec.x;
        ret.y = y / vec.y;
        ret.z = z / vec.z;
    }

    public void div(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = x / vec.x;
        ret.y = y / vec.y;
        ret.z = z / vec.z;
    }

    public ENG_Vector4D divRet(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = x / vec.x;
        ret.y = y / vec.y;
        ret.z = z / vec.z;
        return ret;
    }

    public ENG_Vector4D divRetFull(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = x / vec.x;
        ret.y = y / vec.y;
        ret.z = z / vec.z;
        ret.w = w / vec.w;
        return ret;
    }

    public ENG_Vector4D divRet(ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = x / vec.x;
        ret.y = y / vec.y;
        ret.z = z / vec.z;
        return ret;
    }

    public ENG_Vector3D divRet(ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = x / vec.x;
        ret.y = y / vec.y;
        ret.z = z / vec.z;
        return ret;
    }

    public ENG_Vector3D divRet(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = x / vec.x;
        ret.y = y / vec.y;
        ret.z = z / vec.z;
        return ret;
    }

    public void invertInPlace() {
        x = -x;
        y = -y;
        z = -z;
    }

    public void invertInPlaceFull() {
        x = -x;
        y = -y;
        z = -z;
        w = -w;
    }

    public ENG_Vector4D invert() {
        return new ENG_Vector4D(-x, -y, -z, w);
    }

    public ENG_Vector4D invertFull() {
        return new ENG_Vector4D(-x, -y, -z, -w);
    }

    public void invert(ENG_Vector4D ret) {
        ret.x = -x;
        ret.y = -y;
        ret.z = -z;
    }

    public void invert(ENG_Vector3D ret) {
        ret.x = -x;
        ret.y = -y;
        ret.z = -z;
    }

    public void invertFull(ENG_Vector4D ret) {
        ret.x = -x;
        ret.y = -y;
        ret.z = -z;
        ret.w = w;
    }

    public ENG_Vector4D invertRet(ENG_Vector4D ret) {
        ret.x = -x;
        ret.y = -y;
        ret.z = -z;
        return ret;
    }

    public ENG_Vector4D invertRetFull(ENG_Vector4D ret) {
        ret.x = -x;
        ret.y = -y;
        ret.z = -z;
        ret.w = w;
        return ret;
    }

    public ENG_Vector3D invertRet(ENG_Vector3D ret) {
        ret.x = -x;
        ret.y = -y;
        ret.z = -z;
        return ret;
    }

    public static ENG_Vector4D divInvAsPt(float scalar, ENG_Vector4D vec) {
        return new ENG_Vector4D(scalar / vec.x, scalar / vec.y, scalar / vec.z, 1.0f);
    }

    public static ENG_Vector4D divInvAsVec(float scalar, ENG_Vector4D vec) {
        return new ENG_Vector4D(scalar / vec.x, scalar / vec.y, scalar / vec.z, 0.0f);
    }

    public static ENG_Vector4D divInvAsPt(float scalar, ENG_Vector3D vec) {
        return new ENG_Vector4D(scalar / vec.x, scalar / vec.y, scalar / vec.z, 1.0f);
    }

    public static ENG_Vector4D divInvAsVec(float scalar, ENG_Vector3D vec) {
        return new ENG_Vector4D(scalar / vec.x, scalar / vec.y, scalar / vec.z, 0.0f);
    }

    public static ENG_Vector4D divInvFull(float scalar, ENG_Vector4D vec) {
        return new ENG_Vector4D(scalar / vec.x, scalar / vec.y, scalar / vec.z, scalar / vec.w);
    }

    public static void divInv(float scalar, ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
    }

    public static void divInvFull(float scalar, ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
        ret.w = scalar / vec.w;
    }

    public static void divInv(float scalar, ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
    }

    public static void divInv(float scalar, ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
    }

    public static void divInv(float scalar, ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
    }

    public static ENG_Vector4D divInvRet(float scalar, ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
        return ret;
    }

    public static ENG_Vector4D divInvRetFull(float scalar, ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
        ret.w = scalar / vec.w;
        return ret;
    }

    public static ENG_Vector4D divInvRet(float scalar, ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
        return ret;
    }

    public static ENG_Vector3D divInvRet(float scalar, ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
        return ret;
    }

    public static ENG_Vector3D divInvRet(float scalar, ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
        return ret;
    }

    public static ENG_Vector4D subInvAsPt(float scalar, ENG_Vector4D vec) {
        return new ENG_Vector4D(scalar - vec.x, scalar - vec.y, scalar - vec.z, 1.0f);
    }

    public static ENG_Vector4D subInvAsVec(float scalar, ENG_Vector4D vec) {
        return new ENG_Vector4D(scalar - vec.x, scalar - vec.y, scalar - vec.z, 0.0f);
    }

    public static ENG_Vector4D subInvAsPt(float scalar, ENG_Vector3D vec) {
        return new ENG_Vector4D(scalar - vec.x, scalar - vec.y, scalar - vec.z, 1.0f);
    }

    public static ENG_Vector4D subInvAsVec(float scalar, ENG_Vector3D vec) {
        return new ENG_Vector4D(scalar - vec.x, scalar - vec.y, scalar - vec.z, 0.0f);
    }

    public static ENG_Vector4D subInvFull(float scalar, ENG_Vector4D vec) {
        return new ENG_Vector4D(scalar - vec.x, scalar - vec.y, scalar - vec.z, scalar - vec.w);
    }

    public static void subInv(float scalar, ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
    }

    public static void subInvFull(float scalar, ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
        ret.w = scalar - vec.w;
    }

    public static void subInv(float scalar, ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
    }

    public static void subInv(float scalar, ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
    }

    public static void subInv(float scalar, ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
    }

    public static ENG_Vector4D subInvRet(float scalar, ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
        return ret;
    }

    public static ENG_Vector4D subInvRetFull(float scalar, ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
        ret.w = scalar - vec.w;
        return ret;
    }

    public static ENG_Vector4D subInvRet(float scalar, ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
        return ret;
    }

    public static ENG_Vector3D subInvRet(float scalar, ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
        return ret;
    }

    public static ENG_Vector3D subInvRet(float scalar, ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
        return ret;
    }

    public void addInPlace(ENG_Vector4D vec) {
        x += vec.x;
        y += vec.y;
        z += vec.z;
    }

    public void addInPlaceFull(ENG_Vector4D vec) {
        x += vec.x;
        y += vec.y;
        z += vec.z;
        w += vec.w;
    }

    public void addInPlace(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void addInPlace(float x, float y, float z, float w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
    }

    public void addInPlace(ENG_Vector3D vec) {
        x += vec.x;
        y += vec.y;
        z += vec.z;
    }

    public void addInPlace(float scalar) {
        x += scalar;
        y += scalar;
        z += scalar;
    }

    public void addInPlaceFull(float scalar) {
        x += scalar;
        y += scalar;
        z += scalar;
        w += scalar;
    }

    public void subInPlace(ENG_Vector4D vec) {
        x -= vec.x;
        y -= vec.y;
        z -= vec.z;
    }

    public void subInPlaceFull(ENG_Vector4D vec) {
        x -= vec.x;
        y -= vec.y;
        z -= vec.z;
        w -= vec.w;
    }

    public void subInPlace(ENG_Vector3D vec) {
        x -= vec.x;
        y -= vec.y;
        z -= vec.z;
    }

    public void subInPlace(float scalar) {
        x -= scalar;
        y -= scalar;
        z -= scalar;
    }

    public void subInPlace(float x, float y, float z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
    }

    public void subInPlace(float x, float y, float z, float w) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
    }

    public void mulInPlace(ENG_Vector4D vec) {
        x *= vec.x;
        y *= vec.y;
        z *= vec.z;
    }

    public void mulInPlaceFull(ENG_Vector4D vec) {
        x *= vec.x;
        y *= vec.y;
        z *= vec.z;
        w *= vec.w;
    }

    public void mulInPlace(ENG_Vector3D vec) {
        x *= vec.x;
        y *= vec.y;
        z *= vec.z;
    }

    public void mul(float scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
    }

    public void mulFull(float scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        w *= scalar;
    }

    public void div(ENG_Vector4D vec) {
        x /= vec.x;
        y /= vec.y;
        z /= vec.z;
    }

    public void divInPlaceFull(ENG_Vector4D vec) {
        x /= vec.x;
        y /= vec.y;
        z /= vec.z;
        w /= vec.w;
    }

    public void divInPlace(ENG_Vector4D vec) {
        x /= vec.x;
        y /= vec.y;
        z /= vec.z;
    }

    public void divInPlace(ENG_Vector3D vec) {
        x /= vec.x;
        y /= vec.y;
        z /= vec.z;
    }

    public void divInPlaceFull(float scalar) {
        float inv = 1.0f / scalar;
        x *= inv;
        y *= inv;
        z *= inv;
        w *= inv;
    }

    public void divInPlace(float scalar) {
        float inv = 1.0f / scalar;
        x *= inv;
        y *= inv;
        z *= inv;
    }

    public float length() {
        return ENG_Math.sqrt(x * x + y * y + z * z);
    }

    public float lengthFull() {
        return ENG_Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public static float length(float x, float y, float z) {
        return ENG_Math.sqrt(x * x + y * y + z * z);
    }

    public static float lengthFull(float x, float y, float z, float w) {
        return ENG_Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public static float length(float[] v) {
        return length(v[0], v[1], v[2]);
    }

    public static float lengthFull(float[] v) {
        return lengthFull(v[0], v[1], v[2], v[3]);
    }

    public float squaredLength() {
        return (x * x + y * y + z * z);
    }

    public float squaredLengthFull() {
        return (x * x + y * y + z * z + w * w);
    }

    public static float squaredLength(float x, float y, float z) {
        return (x * x + y * y + z * z);
    }

    public static float squaredLengthFull(float x, float y, float z, float w) {
        return (x * x + y * y + z * z + w * w);
    }

    public float distance(ENG_Vector3D vec) {
        float xDiff = x - vec.x;
        float yDiff = y - vec.y;
        float zDiff = z - vec.z;
        return length(xDiff, yDiff, zDiff);
    }

    public float distance(ENG_Vector4D vec) {
        float xDiff = x - vec.x;
        float yDiff = y - vec.y;
        float zDiff = z - vec.z;
        return length(xDiff, yDiff, zDiff);
    }

    public float distanceFull(ENG_Vector4D vec) {
        float xDiff = x - vec.x;
        float yDiff = y - vec.y;
        float zDiff = z - vec.z;
        float wDiff = w - vec.w;
        return lengthFull(xDiff, yDiff, zDiff, wDiff);
    }

    public float squaredDistance(ENG_Vector4D vec) {
        float xDiff = x - vec.x;
        float yDiff = y - vec.y;
        float zDiff = z - vec.z;
        return squaredLength(xDiff, yDiff, zDiff);
    }

    public float squaredDistanceFull(ENG_Vector4D vec) {
        float xDiff = x - vec.x;
        float yDiff = y - vec.y;
        float zDiff = z - vec.z;
        float wDiff = w - vec.w;
        return squaredLengthFull(xDiff, yDiff, zDiff, wDiff);
    }

    public float squaredDistance(ENG_Vector3D vec) {
        float xDiff = x - vec.x;
        float yDiff = y - vec.y;
        float zDiff = z - vec.z;
        return squaredLength(xDiff, yDiff, zDiff);
    }

    public float dotProduct(ENG_Vector4D vec) {
        return (x * vec.x + y * vec.y + z * vec.z);
    }

    public float dotProductFull(ENG_Vector4D vec) {
        return (x * vec.x + y * vec.y + z * vec.z + w * vec.w);
    }

    public float dotProduct(ENG_Vector3D vec) {
        return (x * vec.x + y * vec.y + z * vec.z);
    }

    public float absDotProduct(ENG_Vector4D vec) {
        return (Math.abs(x * vec.x) + Math.abs(y * vec.y) + Math.abs(z * vec.z));
    }

    public float absDotProductFull(ENG_Vector4D vec) {
        return (Math.abs(x * vec.x) + Math.abs(y * vec.y) + Math.abs(z * vec.z) + Math.abs(w * vec.w));
    }

    public float absDotProduct(ENG_Vector3D vec) {
        return (Math.abs(x * vec.x) + Math.abs(y * vec.y) + Math.abs(z * vec.z));
    }

    public void normalize() {
        float len = this.length();
        if (len > 0.0f) {
            float inv = 1.0f / len;
            x *= inv;
            y *= inv;
            z *= inv;
        }
    }

    public float normalizeRet() {
        float len = this.length();
        if (len > 0.0f) {
            float inv = 1.0f / len;
            x *= inv;
            y *= inv;
            z *= inv;
        }
        return len;
    }

    public ENG_Vector4D midPoint(ENG_Vector4D vec) {
        return new ENG_Vector4D((x + vec.x) * 0.5f, (y + vec.y) * 0.5f, (z + vec.z) * 0.5f, 1.0f);
    }

    public ENG_Vector4D midPoint(ENG_Vector3D vec) {
        return new ENG_Vector4D((x + vec.x) * 0.5f, (y + vec.y) * 0.5f, (z + vec.z) * 0.5f, 1.0f);
    }

    public void midPoint(ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = (x + vec.x) * 0.5f;
        ret.y = (y + vec.y) * 0.5f;
        ret.z = (z + vec.z) * 0.5f;
        ret.w = 1.0f;
    }

    public void midPoint(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = (x + vec.x) * 0.5f;
        ret.y = (y + vec.y) * 0.5f;
        ret.z = (z + vec.z) * 0.5f;
        ret.w = 1.0f;
    }

    public void midPoint(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = (x + vec.x) * 0.5f;
        ret.y = (y + vec.y) * 0.5f;
        ret.z = (z + vec.z) * 0.5f;
    }

    public boolean compareLessThanOrEqual(ENG_Vector4D vec) {
        int xComp = ENG_Float.compareTo(x, vec.x);
        int yComp = ENG_Float.compareTo(y, vec.y);
        int zComp = ENG_Float.compareTo(z, vec.z);

        return (xComp == ENG_Utility.COMPARE_LESS_THAN || xComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (yComp == ENG_Utility.COMPARE_LESS_THAN || yComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (zComp == ENG_Utility.COMPARE_LESS_THAN || zComp == ENG_Utility.COMPARE_EQUAL_TO);
    }

    public boolean compareLessThanOrEqual(ENG_Vector3D vec) {
        int xComp = ENG_Float.compareTo(x, vec.x);
        int yComp = ENG_Float.compareTo(y, vec.y);
        int zComp = ENG_Float.compareTo(z, vec.z);

        return (xComp == ENG_Utility.COMPARE_LESS_THAN || xComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (yComp == ENG_Utility.COMPARE_LESS_THAN || yComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (zComp == ENG_Utility.COMPARE_LESS_THAN || zComp == ENG_Utility.COMPARE_EQUAL_TO);
    }

    public boolean compareLessThan(ENG_Vector4D vec) {
        return ((ENG_Float.compareTo(x, vec.x) == ENG_Utility.COMPARE_LESS_THAN) &&
                (ENG_Float.compareTo(y, vec.y) == ENG_Utility.COMPARE_LESS_THAN) &&
                (ENG_Float.compareTo(z, vec.z) == ENG_Utility.COMPARE_LESS_THAN));
    }

    public boolean compareGreaterThanOrEqual(ENG_Vector4D vec) {
        int xComp = ENG_Float.compareTo(x, vec.x);
        int yComp = ENG_Float.compareTo(y, vec.y);
        int zComp = ENG_Float.compareTo(z, vec.z);

        return (xComp == ENG_Utility.COMPARE_GREATER_THAN || xComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (yComp == ENG_Utility.COMPARE_GREATER_THAN || yComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (zComp == ENG_Utility.COMPARE_GREATER_THAN || zComp == ENG_Utility.COMPARE_EQUAL_TO);
    }

    public boolean compareGreaterThanOrEqual(ENG_Vector3D vec) {
        int xComp = ENG_Float.compareTo(x, vec.x);
        int yComp = ENG_Float.compareTo(y, vec.y);
        int zComp = ENG_Float.compareTo(z, vec.z);

        return (xComp == ENG_Utility.COMPARE_GREATER_THAN || xComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (yComp == ENG_Utility.COMPARE_GREATER_THAN || yComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (zComp == ENG_Utility.COMPARE_GREATER_THAN || zComp == ENG_Utility.COMPARE_EQUAL_TO);
    }

    public boolean compareGreaterThan(ENG_Vector4D vec) {
        return ((ENG_Float.compareTo(x, vec.x) == ENG_Utility.COMPARE_GREATER_THAN) &&
                (ENG_Float.compareTo(y, vec.y) == ENG_Utility.COMPARE_GREATER_THAN) &&
                (ENG_Float.compareTo(z, vec.z) == ENG_Utility.COMPARE_GREATER_THAN));
    }

    public boolean compareLessThan(ENG_Vector3D vec) {
        return ((ENG_Float.compareTo(x, vec.x) == ENG_Utility.COMPARE_LESS_THAN) &&
                (ENG_Float.compareTo(y, vec.y) == ENG_Utility.COMPARE_LESS_THAN) &&
                (ENG_Float.compareTo(z, vec.z) == ENG_Utility.COMPARE_LESS_THAN));
    }

    public boolean compareGreaterThan(ENG_Vector3D vec) {
        return ((ENG_Float.compareTo(x, vec.x) == ENG_Utility.COMPARE_GREATER_THAN) &&
                (ENG_Float.compareTo(y, vec.y) == ENG_Utility.COMPARE_GREATER_THAN) &&
                (ENG_Float.compareTo(z, vec.z) == ENG_Utility.COMPARE_GREATER_THAN));
    }

    public void makeFloor(ENG_Vector4D vec) {
        if (vec.x < x) {
            x = vec.x;
        }
        if (vec.y < y) {
            y = vec.y;
        }
        if (vec.z < z) {
            z = vec.z;
        }
    }

    public void makeFloor(ENG_Vector3D vec) {
        if (vec.x < x) {
            x = vec.x;
        }
        if (vec.y < y) {
            y = vec.y;
        }
        if (vec.z < z) {
            z = vec.z;
        }
    }

    public void makeCeil(ENG_Vector4D vec) {
        if (vec.x > x) {
            x = vec.x;
        }
        if (vec.y > y) {
            y = vec.y;
        }
        if (vec.z > z) {
            z = vec.z;
        }
    }

    public void makeCeil(ENG_Vector3D vec) {
        if (vec.x > x) {
            x = vec.x;
        }
        if (vec.y > y) {
            y = vec.y;
        }
        if (vec.z > z) {
            z = vec.z;
        }
    }

    /**
     *
     * @return w is 1.0
     */
    public ENG_Vector4D makeAbsRet() {
        ENG_Vector4D ret = new ENG_Vector4D(true);
        makeAbs(ret);
        return ret;
    }

    public ENG_Vector4D makeAbsRet(boolean asPt) {
        ENG_Vector4D ret = new ENG_Vector4D(asPt);
        makeAbs(ret);
        return ret;
    }

    /**
     *
     * @param ret w is unmodified.
     */
    public void makeAbs(ENG_Vector4D ret) {
        ret.x = Math.abs(x);
        ret.y = Math.abs(y);
        ret.z = Math.abs(z);
    }

    /**
     * w is unmodified.
     */
    public void makeAbs() {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
    }

    public void crossProduct(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = y * vec.z - z * vec.y;
        ret.y = z * vec.x - x * vec.z;
        ret.z = x * vec.y - y * vec.x;
    }

    public void crossProduct(ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = y * vec.z - z * vec.y;
        ret.y = z * vec.x - x * vec.z;
        ret.z = x * vec.y - y * vec.x;
    }

    public void crossProduct(ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = y * vec.z - z * vec.y;
        ret.y = z * vec.x - x * vec.z;
        ret.z = x * vec.y - y * vec.x;
    }

    public void crossProduct(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = y * vec.z - z * vec.y;
        ret.y = z * vec.x - x * vec.z;
        ret.z = x * vec.y - y * vec.x;
    }

    public ENG_Vector4D crossProductRet(ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = y * vec.z - z * vec.y;
        ret.y = z * vec.x - x * vec.z;
        ret.z = x * vec.y - y * vec.x;
        return ret;
    }

    public ENG_Vector3D crossProductRet(ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = y * vec.z - z * vec.y;
        ret.y = z * vec.x - x * vec.z;
        ret.z = x * vec.y - y * vec.x;
        return ret;
    }

    public ENG_Vector4D crossProductRet(ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = y * vec.z - z * vec.y;
        ret.y = z * vec.x - x * vec.z;
        ret.z = x * vec.y - y * vec.x;
        return ret;
    }

    public ENG_Vector3D crossProductRet(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = y * vec.z - z * vec.y;
        ret.y = z * vec.x - x * vec.z;
        ret.z = x * vec.y - y * vec.x;
        return ret;
    }

    public ENG_Vector4D crossProduct(ENG_Vector4D vec) {
        return new ENG_Vector4D(y * vec.z - z * vec.y,
                z * vec.x - x * vec.z,
                x * vec.y - y * vec.x, 0.0f);
    }

    public ENG_Vector4D crossProduct(ENG_Vector3D vec) {
        return new ENG_Vector4D(y * vec.z - z * vec.y,
                z * vec.x - x * vec.z,
                x * vec.y - y * vec.x, 0.0f);
    }

    public void perpendicular(ENG_Vector4D ret) {

        this.crossProduct(ENG_Math.VEC4_X_UNIT, ret);
        if (ret.squaredLength() < ENG_Math.FLOAT_EPSILON_SQUARED) {
            this.crossProduct(ENG_Math.VEC4_Y_UNIT, ret);
        }
        ret.normalize();

    }

    public void perpendicular(ENG_Vector3D ret) {

        this.crossProduct(ENG_Math.VEC3_X_UNIT, ret);
        if (ret.squaredLength() < ENG_Math.FLOAT_EPSILON_SQUARED) {
            this.crossProduct(ENG_Math.VEC3_Y_UNIT, ret);
        }
        ret.normalize();

    }

    public ENG_Vector4D perpendicularRet(ENG_Vector4D ret) {

        this.crossProduct(ENG_Math.VEC4_X_UNIT, ret);
        if (ret.squaredLength() < ENG_Math.FLOAT_EPSILON_SQUARED) {
            this.crossProduct(ENG_Math.VEC4_Y_UNIT, ret);
        }
        ret.normalize();
        return ret;
    }

    public ENG_Vector3D perpendicularRet(ENG_Vector3D ret) {

        this.crossProduct(ENG_Math.VEC3_X_UNIT, ret);
        if (ret.squaredLength() < ENG_Math.FLOAT_EPSILON_SQUARED) {
            this.crossProduct(ENG_Math.VEC3_Y_UNIT, ret);
        }
        ret.normalize();
        return ret;
    }

    public ENG_Vector4D perpendicular() {
        return perpendicularRet(new ENG_Vector4D());
    }

    public ENG_Vector4D randomDeviant(float angle) {
        ENG_Vector4D ret = new ENG_Vector4D();
        randomDeviant(angle, ret);
        return ret;
    }

    public void randomDeviant(float angle, ENG_Vector4D ret) {
        randomDeviant(angle, ENG_Math.VEC4_ZERO, ret);
    }

    public ENG_Vector4D randomDeviantRet(float angle, ENG_Vector4D vec) {
        ENG_Vector4D ret = new ENG_Vector4D();
        randomDeviant(angle, vec, ret);
        return ret;
    }

    public void randomDeviant(float angle, ENG_Vector4D vec, ENG_Vector4D ret) {
        ENG_Vector4D newUp = new ENG_Vector4D();
        if (vec.equals(ENG_Math.VEC4_ZERO)) {
            this.perpendicular(newUp);
        } else {
            newUp.set(vec);
        }


        ENG_Quaternion q = new ENG_Quaternion();
        q.fromAngleAxis(new ENG_Radian(
                ENG_Utility.getRandom().nextFloat() * ENG_Math.TWO_PI), this);
        q.mul(newUp, newUp);

        q.fromAngleAxis(new ENG_Radian(angle), newUp);

        q.mul(this, ret);
    }

    public float angleBetween(ENG_Vector4D vec) {
        float lenProduct = length() * vec.length();

        if (lenProduct < ENG_Math.FLOAT_EPSILON) {
            lenProduct = ENG_Math.FLOAT_EPSILON;
        }

        float f = dotProduct(vec) / lenProduct;
        f = ENG_Math.clamp(f, -1.0f, 1.0f);
        return ENG_Math.acos(f);
    }

    public float angleBetween(ENG_Vector3D vec) {
        float lenProduct = length() * vec.length();

        if (lenProduct < ENG_Math.FLOAT_EPSILON) {
            lenProduct = ENG_Math.FLOAT_EPSILON;
        }

        float f = dotProduct(vec) / lenProduct;
        f = ENG_Math.clamp(f, -1.0f, 1.0f);
        return ENG_Math.acos(f);
    }

    public ENG_Quaternion getRotationTo(ENG_Vector4D dest, ENG_Vector4D fallbackAxis) {
        ENG_Quaternion q = new ENG_Quaternion();
        getRotationTo(dest, fallbackAxis, q, new ENG_Vector4D(), new ENG_Vector4D(),
                new ENG_Vector4D());
        return q;
    }

    public ENG_Quaternion getRotationTo(ENG_Vector4D dest) {
        ENG_Quaternion q = new ENG_Quaternion();
        getRotationTo(dest, q, new ENG_Vector4D(), new ENG_Vector4D(),
                new ENG_Vector4D());
        return q;
    }

    public void getRotationTo(ENG_Vector4D dest, ENG_Quaternion q) {
        getRotationTo(dest, ENG_Math.VEC4_ZERO, q,
                new ENG_Vector4D(), new ENG_Vector4D(),
                new ENG_Vector4D());
    }

    public void getRotationTo(ENG_Vector4D dest, ENG_Quaternion q,
                              ENG_Vector4D v0, ENG_Vector4D v1, ENG_Vector4D axis) {
        getRotationTo(dest, ENG_Math.VEC4_ZERO, q, v0, v1, axis);
    }

    public void getRotationTo(ENG_Vector4D dest, ENG_Vector4D fallbackAxis,
                              ENG_Quaternion q, ENG_Vector4D v0, ENG_Vector4D v1,
                              ENG_Vector4D axis) {
        v0.set(this);
        v1.set(dest);
        v0.normalize();
        v1.normalize();

        float d = v0.dotProduct(v1);

        if (d >= 1.0f) {
            q.set(ENG_Math.QUAT_IDENTITY);
            return;
        }
        if (d < (1e-6f - 1.0f)) {
            if (fallbackAxis.notEquals(ENG_Math.VEC4_ZERO)) {
                q.fromAngleAxis(ENG_Math.PI_RAD, fallbackAxis);
            } else {
                ENG_Math.VEC4_X_UNIT.crossProduct(this, axis);
                if (axis.isZeroLength()) {
                    ENG_Math.VEC4_Y_UNIT.crossProduct(this, axis);
                }
                axis.normalize();
                q.fromAngleAxis(ENG_Math.PI_RAD, axis);
            }
        } else {
            float s = ENG_Math.sqrt((1.0f + d) * 2.0f);
            float invs = 1.0f / s;

            v0.crossProduct(v1, axis);

            q.x = axis.x * invs;
            q.y = axis.y * invs;
            q.z = axis.z * invs;
            q.w = s * 0.5f;
            q.normalize();
        }
    }

    public boolean isZeroLength() {
        return (squaredLength() < ENG_Math.FLOAT_EPSILON_SQUARED);
    }

    public void normalizedCopy(ENG_Vector4D ret) {
        ret.set(this);
        ret.normalize();
    }

    public ENG_Vector4D normalizedCopy() {
        ENG_Vector4D ret = new ENG_Vector4D();
        normalizedCopy(ret);
        return ret;
    }

    public void reflect(ENG_Vector4D normal, ENG_Vector4D ret) {
        sub(normal.mulRet(2.0f * this.dotProduct(normal), ret), ret);
    }

    public ENG_Vector4D reflect(ENG_Vector4D normal) {
        ENG_Vector4D vec = normal.mulAsPt(2.0f * this.dotProduct(normal));
        return this.subAsPt(vec);
    }

    public boolean positionClose(ENG_Vector4D vec, float tolerance) {
        return (squaredDistance(vec) < (squaredLength() + vec.squaredLength()) * tolerance);
    }

    public boolean positionClose(ENG_Vector3D vec, float tolerance) {
        return (squaredDistance(vec) < (squaredLength() + vec.squaredLength()) * tolerance);
    }

    public boolean directionEquals(ENG_Vector4D vec, float tolerance) {
        return (Math.abs(ENG_Math.acos(dotProduct(vec))) <= tolerance);
    }

    public boolean directionEquals(ENG_Vector3D vec, float tolerance) {
        return (Math.abs(ENG_Math.acos(dotProduct(vec))) <= tolerance);
    }

    public boolean isNaNFull() {
        return (Float.isNaN(x) || Float.isNaN(y) || (Float.isNaN(z)) || (Float.isNaN(w)));
    }

    public boolean isNaN() {
        return (Float.isNaN(x) || Float.isNaN(y) || (Float.isNaN(z)));
    }

    public void lerpInPlace(final ENG_Vector4D target, float alpha) {
        x += alpha * (target.x - x);
        y += alpha * (target.y - y);
        z += alpha * (target.z - z);
    }

    public ENG_Vector4D lerpAsPt(final ENG_Vector4D target, float alpha) {
        ENG_Vector4D ret = new ENG_Vector4D(true);
        lerp(target, alpha, ret);
        return ret;
    }

    public ENG_Vector4D lerpAsPVec(final ENG_Vector4D target, float alpha) {
        ENG_Vector4D ret = new ENG_Vector4D();
        lerp(target, alpha, ret);
        return ret;
    }

    public void lerp(final ENG_Vector4D target, float alpha, ENG_Vector4D ret) {
        ret.x += alpha * (target.x - x);
        ret.y += alpha * (target.y - y);
        ret.z += alpha * (target.z - z);
    }

    /**
     * Are you sure you don't mean mat.transform(this, ret) ???
     * @param mat
     * @param ret
     */
    public void mul(ENG_Matrix4 mat, ENG_Vector4D ret) {
        mat.transform(this, ret);
    }

    public ENG_Vector4D mul(ENG_Matrix4 mat) {
        ENG_Vector4D ret = new ENG_Vector4D();
        mul(mat, ret);
        return ret;
    }

    /**
     * Are you sure you don't mean mat.transform(this, ret) ???
     * @param mat
     * @param ret
     */
    public void mulColumn(ENG_Matrix4 mat, ENG_Vector4D ret) {
        /*
		 * v.x*mat[0][0] + v.y*mat[1][0] + v.z*mat[2][0] + v.w*mat[3][0],
            v.x*mat[0][1] + v.y*mat[1][1] + v.z*mat[2][1] + v.w*mat[3][1],
            v.x*mat[0][2] + v.y*mat[1][2] + v.z*mat[2][2] + v.w*mat[3][2],
            v.x*mat[0][3] + v.y*mat[1][3] + v.z*mat[2][3] + v.w*mat[3][3]
		 */
        float[] f = mat.get();
        ret.x = x * f[0] + y * f[4] + z * f[8] + w * f[12];
        ret.y = x * f[1] + y * f[5] + z * f[9] + w * f[13];
        ret.z = x * f[2] + y * f[6] + z * f[10] + w * f[14];
        ret.w = x * f[3] + y * f[7] + z * f[11] + w * f[15];
    }

    public ENG_Vector4D mulColumn(ENG_Matrix4 mat) {
        ENG_Vector4D ret = new ENG_Vector4D();
        mulColumn(mat, ret);
        return ret;
    }

    public void getAsVector3D(ENG_Vector3D ret) {
        ret.set(this);
    }

    public ENG_Vector3D getAsVector3D() {
        ENG_Vector3D ret = new ENG_Vector3D();
        getAsVector3D(ret);
        return ret;
    }

    public String toString(boolean format, NumberFormat formatter) {
        return (/*"Vector3("*/formatter.format(x) + " " + formatter.format(y) + " " +
                formatter.format(z) + " " + formatter.format(w)/* + ")"*/);
    }

    public String toString() {
        return toString(true, ENG_Utility.FORMATTER_DEFAULT);
    }
}
