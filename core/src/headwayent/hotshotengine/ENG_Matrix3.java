/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import java.text.NumberFormat;

import headwayent.hotshotengine.basictypes.ENG_Float;

public class ENG_Matrix3 {

    private float[] m = new float[9];
/*	private float[] temp = new float[9];

	private ENG_Vector3D vec3 = new ENG_Vector3D();
	private ENG_Vector3D vec32 = new ENG_Vector3D();*/

    public ENG_Matrix3() {
        setIdentity(m);
    }

    public ENG_Matrix3(float scalar) {
        if (scalar != 0.0f) {
            set(scalar);
        }
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public ENG_Matrix3(ENG_Matrix3 mat) {
        System.arraycopy(mat.get(), 0, m, 0, m.length);
    }

    public ENG_Matrix3(ENG_Matrix4 mat) {
        setMat4ToMat3(m, mat.get());
    }

    public ENG_Matrix3(float[] mat, boolean copy) {
        if (copy) {
            System.arraycopy(mat, 0, m, 0, m.length);
        } else {
            if (mat.length >= 9) {
                m = mat;
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    public ENG_Matrix3(float m0, float m1, float m2, float m3,
                       float m4, float m5, float m6, float m7,
                       float m8) {
        set(m0, m1, m2, m3, m4, m5, m6, m7, m8);
    }

    public void fromAxis(ENG_Vector3D x, ENG_Vector3D y, ENG_Vector3D z) {
        matrix_set_basis_vectors(m, x, y, z);
    }

    public void fromAxis(ENG_Vector4D x, ENG_Vector4D y, ENG_Vector4D z) {
        matrix_set_basis_vectors(m, x, y, z);
    }

    private static boolean invert(float[] s, float[] d, float tolerance) {
        d[0] = s[4] * s[8] - s[5] * s[7];
        d[1] = s[2] * s[7] - s[1] * s[8];
        d[2] = s[1] * s[5] - s[2] * s[4];
        d[3] = s[5] * s[6] - s[3] * s[8];
        d[4] = s[0] * s[8] - s[2] * s[6];
        d[5] = s[2] * s[3] - s[0] * s[5];
        d[6] = s[3] * s[7] - s[4] * s[6];
        d[7] = s[1] * s[3] - s[0] * s[7];
        d[8] = s[0] * s[4] - s[1] * s[3];

        float fDet = s[0] * d[0] + s[1] * d[3] + s[2] * d[6];

        if ((fDet <= tolerance) && (fDet >= -tolerance)) {
            return false;
        }

        float fInvDet = 1.0f / fDet;

        mul(d, fInvDet);

        return true;
    }

    private static void extractm3Fromm4(ENG_Matrix4 s, ENG_Matrix3 d) {
        float[] src = s.get();
        float[] dest = d.get();
        dest[0] = src[0];
        dest[1] = src[1];
        dest[2] = src[2];
        dest[3] = src[4];
        dest[4] = src[5];
        dest[5] = src[6];
        dest[6] = src[8];
        dest[7] = src[9];
        dest[8] = src[10];
    }

    public void extractMatrix(ENG_Matrix4 mat) {
        extractm3Fromm4(mat, this);
    }

    public ENG_Matrix3 extractMatrixRet(ENG_Matrix4 mat) {
        ENG_Matrix3 ret = new ENG_Matrix3(0.0f);
        ret.set(this);
        extractm3Fromm4(mat, ret);
        return ret;
    }

    public float determinant() {
        float x = m[4] * m[8] - m[5] * m[7];
        float y = m[5] * m[6] - m[3] * m[8];
        float z = m[3] * m[7] - m[4] * m[6];
        return (m[0] * x + m[1] * y + m[2] * z);
    }

    public static boolean invert(ENG_Matrix3 src, ENG_Matrix3 dest, float tolerance) {
        float[] s = src.get();
        float[] d = dest.get();
        return invert(s, d, tolerance);

    }

    public ENG_Matrix3 invertRet() {
        ENG_Matrix3 mat = new ENG_Matrix3(0.0f);
        if (invert(this, mat, ENG_Math.FLOAT_EPSILON)) {
            return mat;
        }
        return null;
    }

    public void invert() throws ArithmeticException {
        float[] temp = new float[9];


        if (invert(this.get(), temp, ENG_Math.FLOAT_EPSILON)) {
            this.set(temp);
        } else {
            throw new ArithmeticException();
        }
    }

    public void setRow(int row, ENG_Vector3D vec) {
        switch (row) {
            case 0:
                m[0] = vec.x;
                m[1] = vec.y;
                m[2] = vec.z;
                break;
            case 1:
                m[3] = vec.x;
                m[4] = vec.y;
                m[5] = vec.z;
                break;
            case 2:
                m[6] = vec.x;
                m[7] = vec.y;
                m[8] = vec.z;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void setRow(int row, ENG_Vector4D vec) {
        switch (row) {
            case 0:
                m[0] = vec.x;
                m[1] = vec.y;
                m[2] = vec.z;
                break;
            case 1:
                m[3] = vec.x;
                m[4] = vec.y;
                m[5] = vec.z;
                break;
            case 2:
                m[6] = vec.x;
                m[7] = vec.y;
                m[8] = vec.z;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void setColumn(int col, ENG_Vector3D vec) {
        switch (col) {
            case 0:
                m[0] = vec.x;
                m[3] = vec.y;
                m[6] = vec.z;
                break;
            case 1:
                m[1] = vec.x;
                m[4] = vec.y;
                m[7] = vec.z;
                break;
            case 2:
                m[2] = vec.x;
                m[5] = vec.y;
                m[8] = vec.z;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void setColumn(int col, ENG_Vector4D vec) {
        switch (col) {
            case 0:
                m[0] = vec.x;
                m[3] = vec.y;
                m[6] = vec.z;
                break;
            case 1:
                m[1] = vec.x;
                m[4] = vec.y;
                m[7] = vec.z;
                break;
            case 2:
                m[2] = vec.x;
                m[5] = vec.y;
                m[8] = vec.z;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void getRow(int row, ENG_Vector3D ret) {

        switch (row) {
            case 0:
                ret.x = m[0];
                ret.y = m[1];
                ret.z = m[2];
                break;
            case 1:
                ret.x = m[3];
                ret.y = m[4];
                ret.z = m[5];
                break;
            case 2:
                ret.x = m[6];
                ret.y = m[7];
                ret.z = m[8];
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void getRow(int row, ENG_Vector4D ret) {

        switch (row) {
            case 0:
                ret.x = m[0];
                ret.y = m[1];
                ret.z = m[2];
                break;
            case 1:
                ret.x = m[3];
                ret.y = m[4];
                ret.z = m[5];
                break;
            case 2:
                ret.x = m[6];
                ret.y = m[7];
                ret.z = m[8];
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public ENG_Vector3D getRowAsVec3(int row) {
        ENG_Vector3D ret = new ENG_Vector3D();
        getRow(row, ret);
        return ret;
    }

    public ENG_Vector4D getRowAsVec4(int row) {
        ENG_Vector4D ret = new ENG_Vector4D();
        getRow(row, ret);
        return ret;
    }

    public void getColumn(int col, ENG_Vector3D ret) {
        switch (col) {
            case 0:
                ret.x = m[0];
                ret.y = m[3];
                ret.z = m[6];
                break;
            case 1:
                ret.x = m[1];
                ret.y = m[4];
                ret.z = m[7];
                break;
            case 2:
                ret.x = m[2];
                ret.y = m[5];
                ret.z = m[8];
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void getColumn(int col, ENG_Vector4D ret) {
        switch (col) {
            case 0:
                ret.x = m[0];
                ret.y = m[3];
                ret.z = m[6];
                break;
            case 1:
                ret.x = m[1];
                ret.y = m[4];
                ret.z = m[7];
                break;
            case 2:
                ret.x = m[2];
                ret.y = m[5];
                ret.z = m[8];
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public ENG_Vector3D getColumnAsVec3(int col) {
        ENG_Vector3D ret = new ENG_Vector3D();
        getColumn(col, ret);
        return ret;
    }

    public ENG_Vector4D getColumnAsVec4(int col) {
        ENG_Vector4D ret = new ENG_Vector4D();
        getColumn(col, ret);
        return ret;
    }

    public float get(int row, int column) {
        if ((row < 3) && (column < 3)) {
            return m[3 * row + column];
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void set(float scalar) {
        m[0] = scalar;
        m[1] = scalar;
        m[2] = scalar;
        m[3] = scalar;
        m[4] = scalar;
        m[5] = scalar;
        m[6] = scalar;
        m[7] = scalar;
        m[8] = scalar;

    }

    public void set(float m0, float m1, float m2, float m3,
                    float m4, float m5, float m6, float m7,
                    float m8) {
        m[0] = m0;
        m[1] = m1;
        m[2] = m2;
        m[3] = m3;
        m[4] = m4;
        m[5] = m5;
        m[6] = m6;
        m[7] = m7;
        m[8] = m8;

    }

    public void setRef(float[] m) {
        if (m == null) {
            throw new NullPointerException();
        }
        if (m.length < 9) {
            throw new IllegalArgumentException();
        }
        this.m = m;
    }

    public void set(ENG_Matrix4 mat) {
        setMat4ToMat3(m, mat.get());
    }

    public void set(ENG_Matrix3 mat) {
        set(mat.get());
    }

    public void set(float[] m) {
        if (m == null) {
            throw new NullPointerException();
        }
        if (m.length < 9) {
            throw new IllegalArgumentException();
        }
        System.arraycopy(m, 0, this.m, 0, this.m.length);
    }

    public float[] get() {
        return m;
    }

    public void get(float[] m) {
        if (m == null) {
            throw new NullPointerException();
        }
        if (m.length < 9) {
            throw new IllegalArgumentException();
        }
        System.arraycopy(this.m, 0, m, 0, this.m.length);
    }

    public void swap(ENG_Matrix4 mat) {
        float[] temp = new float[9];
        System.arraycopy(m, 0, temp, 0, 9);
        System.arraycopy(mat.get(), 0, m, 0, 9);
        System.arraycopy(temp, 0, mat.get(), 0, 9);
    }

    public void setIdentity() {
        setIdentityFull(m);
    }

    public ENG_Matrix3 transposeRet() {
        ENG_Matrix3 ret = new ENG_Matrix3();
        transpose(ret);
        return ret;
    }

    public void transpose(ENG_Matrix3 ret) {
        float[] v = ret.get();

        v[0] = m[0];
        v[1] = m[3];
        v[2] = m[6];

        v[3] = m[1];
        v[4] = m[4];
        v[5] = m[7];

        v[6] = m[2];
        v[7] = m[5];
        v[8] = m[8];
    }

    public void transpose() {
        float[] temp = new float[9];
        this.get(temp);

        m[0] = temp[0];
        m[1] = temp[3];
        m[2] = temp[6];

        m[3] = temp[1];
        m[4] = temp[4];
        m[5] = temp[7];

        m[6] = temp[2];
        m[7] = temp[5];
        m[8] = temp[8];


    }

    private static void addm3m3m3(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] + m2[0];
        ret[1] = m1[1] + m2[1];
        ret[2] = m1[2] + m2[2];
        ret[3] = m1[3] + m2[3];
        ret[4] = m1[4] + m2[4];
        ret[5] = m1[5] + m2[5];
        ret[6] = m1[6] + m2[6];
        ret[7] = m1[7] + m2[7];
        ret[8] = m1[8] + m2[8];
    }

    private static void addm4m3m3(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] + m2[0];
        ret[1] = m1[1] + m2[1];
        ret[2] = m1[2] + m2[2];
        ret[3] = m1[4] + m2[3];
        ret[4] = m1[5] + m2[4];
        ret[5] = m1[6] + m2[5];
        ret[6] = m1[8] + m2[6];
        ret[7] = m1[9] + m2[7];
        ret[8] = m1[10] + m2[8];
    }

    private static void addm3m4m3(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] + m2[0];
        ret[1] = m1[1] + m2[1];
        ret[2] = m1[2] + m2[2];
        ret[3] = m1[3] + m2[4];
        ret[4] = m1[4] + m2[5];
        ret[5] = m1[5] + m2[6];
        ret[6] = m1[6] + m2[8];
        ret[7] = m1[7] + m2[9];
        ret[8] = m1[8] + m2[10];
    }

    private static void addm4m4m3(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] + m2[0];
        ret[1] = m1[1] + m2[1];
        ret[2] = m1[2] + m2[2];
        ret[3] = m1[4] + m2[4];
        ret[4] = m1[5] + m2[5];
        ret[5] = m1[6] + m2[6];
        ret[6] = m1[8] + m2[8];
        ret[7] = m1[9] + m2[9];
        ret[8] = m1[10] + m2[10];
    }

    private static void subm3m3m3(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] - m2[0];
        ret[1] = m1[1] - m2[1];
        ret[2] = m1[2] - m2[2];
        ret[3] = m1[3] - m2[3];
        ret[4] = m1[4] - m2[4];
        ret[5] = m1[5] - m2[5];
        ret[6] = m1[6] - m2[6];
        ret[7] = m1[7] - m2[7];
        ret[8] = m1[8] - m2[8];

    }

    private static void subm4m3m3(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] - m2[0];
        ret[1] = m1[1] - m2[1];
        ret[2] = m1[2] - m2[2];
        ret[3] = m1[4] - m2[3];
        ret[4] = m1[5] - m2[4];
        ret[5] = m1[6] - m2[5];
        ret[6] = m1[8] - m2[6];
        ret[7] = m1[9] - m2[7];
        ret[8] = m1[10] - m2[8];
    }

    private static void subm3m4m3(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] - m2[0];
        ret[1] = m1[1] - m2[1];
        ret[2] = m1[2] - m2[2];
        ret[3] = m1[3] - m2[4];
        ret[4] = m1[4] - m2[5];
        ret[5] = m1[5] - m2[6];
        ret[6] = m1[6] - m2[8];
        ret[7] = m1[7] - m2[9];
        ret[8] = m1[8] - m2[10];
    }

    private static void subm4m4m3(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] - m2[0];
        ret[1] = m1[1] - m2[1];
        ret[2] = m1[2] - m2[2];
        ret[3] = m1[4] - m2[4];
        ret[4] = m1[5] - m2[5];
        ret[5] = m1[6] - m2[6];
        ret[6] = m1[8] - m2[8];
        ret[7] = m1[9] - m2[9];
        ret[8] = m1[10] - m2[10];
    }

    private static void concatenate(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] * m2[0] + m1[1] * m2[3] + m1[2] * m2[6];
        ret[1] = m1[0] * m2[1] + m1[1] * m2[4] + m1[2] * m2[7];
        ret[2] = m1[0] * m2[2] + m1[1] * m2[5] + m1[2] * m2[8];

        ret[3] = m1[3] * m2[0] + m1[4] * m2[3] + m1[5] * m2[6];
        ret[4] = m1[3] * m2[1] + m1[4] * m2[4] + m1[5] * m2[7];
        ret[5] = m1[3] * m2[2] + m1[4] * m2[5] + m1[5] * m2[8];

        ret[6] = m1[6] * m2[0] + m1[7] * m2[3] + m1[8] * m2[6];
        ret[7] = m1[6] * m2[1] + m1[7] * m2[4] + m1[8] * m2[7];
        ret[8] = m1[6] * m2[2] + m1[7] * m2[5] + m1[8] * m2[8];
    }

    public void addInPlace(ENG_Matrix3 mat) {
        float[] f = mat.get();
        m[0] += f[0];
        m[1] += f[1];
        m[2] += f[2];
        m[3] += f[3];
        m[4] += f[4];
        m[5] += f[5];
        m[6] += f[6];
        m[7] += f[7];
        m[8] += f[8];

    }

    public void addInPlace(ENG_Matrix4 mat) {
        float[] f = mat.get();
        m[0] += f[0];
        m[1] += f[1];
        m[2] += f[2];

        m[3] += f[4];
        m[4] += f[5];
        m[5] += f[6];

        m[6] += f[8];
        m[7] += f[9];
        m[8] += f[10];

    }

    public void subInPlace(ENG_Matrix4 mat) {
        float[] f = mat.get();
        m[0] -= f[0];
        m[1] -= f[1];
        m[2] -= f[2];
        m[3] -= f[3];
        m[4] -= f[4];
        m[5] -= f[5];
        m[6] -= f[6];
        m[7] -= f[7];
        m[8] -= f[8];

    }

    public void subInPlace(ENG_Matrix3 mat) {
        float[] f = mat.get();
        m[0] -= f[0];
        m[1] -= f[1];
        m[2] -= f[2];

        m[3] -= f[4];
        m[4] -= f[5];
        m[5] -= f[6];

        m[6] -= f[8];
        m[7] -= f[9];
        m[8] -= f[10];
    }

    public ENG_Matrix3 add(ENG_Matrix4 mat) {
        ENG_Matrix3 ret = new ENG_Matrix3();
        addm3m4m3(m, mat.get(), ret.get());
        return ret;
    }

    public ENG_Matrix3 add(ENG_Matrix3 mat) {
        ENG_Matrix3 ret = new ENG_Matrix3();
        addm3m3m3(m, mat.get(), ret.get());
        return ret;
    }

    public void add(ENG_Matrix4 mat, ENG_Matrix3 ret) {
        addm3m4m3(m, mat.get(), ret.get());
    }

    public void add(ENG_Matrix3 mat, ENG_Matrix3 ret) {
        addm3m3m3(m, mat.get(), ret.get());
    }

    public void sub(ENG_Matrix4 mat, ENG_Matrix3 ret) {
        subm3m4m3(m, mat.get(), ret.get());
    }

    public void sub(ENG_Matrix3 mat, ENG_Matrix3 ret) {
        subm3m3m3(m, mat.get(), ret.get());
    }

    public ENG_Matrix3 sub(ENG_Matrix4 mat) {
        ENG_Matrix3 ret = new ENG_Matrix3();
        subm3m4m3(m, mat.get(), ret.get());
        return ret;
    }

    public ENG_Matrix3 sub(ENG_Matrix3 mat) {
        ENG_Matrix3 ret = new ENG_Matrix3();
        subm3m3m3(m, mat.get(), ret.get());
        return ret;
    }

    public ENG_Matrix3 mulRet(float scalar) {
        ENG_Matrix3 mat = new ENG_Matrix3();
        mat.set(m);
        mat.mul(scalar);
        return mat;
    }

    public void mul(float scalar) {
        mul(m, scalar);

    }

    private static void mul(float[] m, float scalar) {
        m[0] *= scalar;
        m[1] *= scalar;
        m[2] *= scalar;
        m[3] *= scalar;
        m[4] *= scalar;
        m[5] *= scalar;
        m[6] *= scalar;
        m[7] *= scalar;
        m[8] *= scalar;
    }

    public ENG_Matrix3 divRet(float scalar) {
        ENG_Matrix3 mat = new ENG_Matrix3();
        mat.set(m);
        mat.div(scalar);
        return mat;
    }

    public void div(float scalar) {
        scalar = 1.0f / scalar;
        mul(scalar);
    }

    public boolean equals(ENG_Matrix3 mat) {
        float[] f = mat.get();
        return !((m[0] != f[0]) || (m[1] != f[1]) || (m[2] != f[2]) || (m[3] != f[3]) ||
                (m[4] != f[4]) || (m[5] != f[5]) || (m[6] != f[6]) || (m[7] != f[7]) ||
                (m[8] != f[8]));
    }

    public boolean notEquals(ENG_Matrix3 mat) {
        float[] f = mat.get();
        return (m[0] != f[0]) || (m[1] != f[1]) || (m[2] != f[2]) || (m[3] != f[3]) ||
                (m[4] != f[4]) || (m[5] != f[5]) || (m[6] != f[6]) || (m[7] != f[7]) ||
                (m[8] != f[8]);
    }

    public void postMultiply(ENG_Matrix3 mat) {
        if (mat == null) {
            throw new NullPointerException();
        }
        float[] temp = new float[9];

        this.get(temp);
        float[] m2 = mat.get();

        concatenate(temp, m2, m);

    }

    public void concatenate(ENG_Matrix3 mat, ENG_Matrix3 ret) {
        float[] retm = ret.get();
        float[] m2 = mat.get();

        concatenate(m, m2, retm);
    }

    public ENG_Matrix3 concatenate(ENG_Matrix3 mat) {
        ENG_Matrix3 ret = new ENG_Matrix3();
        concatenate(mat, ret);
        return ret;
    }

    private static void setIdentity(float[] mat) {
        mat[0] = 1.0f;
        mat[4] = 1.0f;
        mat[8] = 1.0f;

    }

    private static void setIdentityFull(float[] mat) {
        mat[0] = 1.0f;
        mat[1] = 0.0f;
        mat[2] = 0.0f;
        mat[3] = 0.0f;
        mat[4] = 1.0f;
        mat[5] = 0.0f;
        mat[6] = 0.0f;
        mat[7] = 0.0f;
        mat[8] = 1.0f;


    }

    private static void setMat4ToMat3(float[] mat3, float[] mat4) {
        mat3[0] = mat4[0];
        mat3[1] = mat4[1];
        mat3[2] = mat4[2];
        mat3[3] = mat4[4];
        mat3[4] = mat4[5];
        mat3[5] = mat4[6];
        mat3[6] = mat4[8];
        mat3[7] = mat4[9];
        mat3[8] = mat4[10];
    }

//	private static void matrix_set_translation(float [] mat, float x, float y, float z) {
//        mat[3] = x;
//        mat[7] = y;
//        mat[11] = z;
//    }

    private static void matrix_set_basis_vectors(float[] mat, ENG_Vector3D xAxis, ENG_Vector3D yAxis, ENG_Vector3D zAxis) {
        mat[0] = xAxis.x;
        mat[1] = yAxis.x;
        mat[2] = zAxis.x;

        mat[3] = xAxis.y;
        mat[4] = yAxis.y;
        mat[5] = zAxis.y;

        mat[6] = xAxis.z;
        mat[7] = yAxis.z;
        mat[8] = zAxis.z;
    }

    private static void matrix_set_transposed_basis_vectors(float[] mat, ENG_Vector3D xAxis, ENG_Vector3D yAxis, ENG_Vector3D zAxis) {
        mat[0] = xAxis.x;
        mat[1] = xAxis.y;
        mat[2] = xAxis.z;

        mat[3] = yAxis.x;
        mat[4] = yAxis.y;
        mat[5] = yAxis.z;

        mat[6] = zAxis.x;
        mat[7] = zAxis.y;
        mat[8] = zAxis.z;
    }

    private static void matrix_set_basis_vectors(float[] mat, ENG_Vector4D xAxis, ENG_Vector4D yAxis, ENG_Vector4D zAxis) {
        mat[0] = xAxis.x;
        mat[1] = yAxis.x;
        mat[2] = zAxis.x;

        mat[3] = xAxis.y;
        mat[4] = yAxis.y;
        mat[5] = zAxis.y;

        mat[6] = xAxis.z;
        mat[7] = yAxis.z;
        mat[8] = zAxis.z;
    }

    private static void matrix_set_transposed_basis_vectors(float[] mat, ENG_Vector4D xAxis, ENG_Vector4D yAxis, ENG_Vector4D zAxis) {
        mat[0] = xAxis.x;
        mat[1] = xAxis.y;
        mat[2] = xAxis.z;

        mat[3] = yAxis.x;
        mat[4] = yAxis.y;
        mat[5] = yAxis.z;

        mat[6] = zAxis.x;
        mat[7] = zAxis.y;
        mat[8] = zAxis.z;
    }

    private void transform(float[] m, ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = m[0] * vec.x + m[1] * vec.y + m[2] * vec.z;
        ret.y = m[3] * vec.x + m[4] * vec.y + m[5] * vec.z;
        ret.z = m[6] * vec.x + m[7] * vec.y + m[8] * vec.z;

    }

    public void transform(ENG_Vector3D vec, ENG_Vector3D ret) {
        transform(m, vec, ret);
    }

    public void transform(ENG_Vector4D vec, ENG_Vector3D ret) {
        ENG_Vector3D vec3 = new ENG_Vector3D();
        vec3.set(vec);
        transform(m, vec3, ret);
    }

    public void transform(ENG_Vector4D vec, ENG_Vector4D ret) {
        ENG_Vector3D vec3 = new ENG_Vector3D();
        ENG_Vector3D vec32 = new ENG_Vector3D();
        vec3.set(vec);
        transform(m, vec3, vec32);
        ret.set(vec32);
    }

    public void transform(ENG_Vector3D vec) {
        ENG_Vector3D vec3 = new ENG_Vector3D();

        vec3.set(vec);
        transform(m, vec3, vec);
    }

    public void transform(ENG_Vector4D vec) {
        ENG_Vector3D vec3 = new ENG_Vector3D();
        ENG_Vector3D vec32 = new ENG_Vector3D();
        vec3.set(vec);
        transform(m, vec3, vec32);
        vec.set(vec32);
    }

    public ENG_Matrix3 negate() {
        ENG_Matrix3 ret = new ENG_Matrix3();
        negate(ret);
        return ret;
    }

    public void negate(ENG_Matrix3 ret) {
        negate(ret, this);
    }

    public void negateInPlace() {
        negate(this, this);
    }

    private static void negate(ENG_Matrix3 mat0, ENG_Matrix3 mat1) {
        float[] m0 = mat0.get();
        float[] m1 = mat1.get();

        m0[0] = -m1[0];
        m0[1] = -m1[1];
        m0[2] = -m1[2];
        m0[3] = -m1[3];
        m0[4] = -m1[4];
        m0[5] = -m1[5];
        m0[6] = -m1[6];
        m0[7] = -m1[7];
        m0[8] = -m1[8];

    }

    public boolean hasScale() {
        float t = m[0] * m[0] + m[3] * m[3] + m[6] * m[6];
        if (ENG_Float.compareTo(t, 1.0f, (float) 1e-04) == ENG_Utility.COMPARE_EQUAL_TO) {
            return true;
        }
        t = m[1] * m[1] + m[4] * m[4] + m[7] * m[7];
        if (ENG_Float.compareTo(t, 1.0f, (float) 1e-04) == ENG_Utility.COMPARE_EQUAL_TO) {
            return true;
        }
        t = m[2] * m[2] + m[5] * m[5] + m[8] * m[8];
        return ENG_Float.compareTo(t, 1.0f, (float) 1e-04) == ENG_Utility.COMPARE_EQUAL_TO;
    }

    public String toString(boolean format, NumberFormat formatter) {
        StringBuilder s = new StringBuilder();
        s.append("\nMatrix3(").append(formatter.format(m[0]));
        for (int i = 1; i < m.length; ++i) {
            if (i % 4 == 0) {
                s.append("\n");
            }
            s.append(", ").append(formatter.format(m[i]));
        }
        s.append(")");
        return s.toString();
    }

    public String toString() {
        return toString(true, ENG_Utility.FORMATTER_DEFAULT);
    }

    public void fromAxisAngle(ENG_Vector4D rkAxis, float fRadians) {

        float fCos = ENG_Math.cos(fRadians);
        float fSin = ENG_Math.sin(fRadians);
        float fOneMinusCos = 1.0f - fCos;
        float fX2 = rkAxis.x * rkAxis.x;
        float fY2 = rkAxis.y * rkAxis.y;
        float fZ2 = rkAxis.z * rkAxis.z;
        float fXYM = rkAxis.x * rkAxis.y * fOneMinusCos;
        float fXZM = rkAxis.x * rkAxis.z * fOneMinusCos;
        float fYZM = rkAxis.y * rkAxis.z * fOneMinusCos;
        float fXSin = rkAxis.x * fSin;
        float fYSin = rkAxis.y * fSin;
        float fZSin = rkAxis.z * fSin;

        m[0] = fX2 * fOneMinusCos + fCos;
        m[1] = fXYM - fZSin;
        m[2] = fXZM + fYSin;
        m[3] = fXYM + fZSin;
        m[4] = fY2 * fOneMinusCos + fCos;
        m[5] = fYZM - fXSin;
        m[6] = fXZM - fYSin;
        m[7] = fYZM + fXSin;
        m[8] = fZ2 * fOneMinusCos + fCos;

    }

    public void fromEulerAnglesXYZ(float fYAngle, float fPAngle, float fRAngle) {

        float fCos, fSin;

        fCos = ENG_Math.cos(fYAngle);
        fSin = ENG_Math.sin(fYAngle);
        ENG_Matrix3 kXMat = new ENG_Matrix3(1.0f, 0.0f, 0.0f, 0.0f, fCos, -fSin, 0.0f, fSin, fCos);

        fCos = ENG_Math.cos(fPAngle);
        fSin = ENG_Math.sin(fPAngle);
        ENG_Matrix3 kYMat = new ENG_Matrix3(fCos, 0.0f, fSin, 0.0f, 1.0f, 0.0f, -fSin, 0.0f, fCos);

        fCos = ENG_Math.cos(fRAngle);
        fSin = ENG_Math.sin(fRAngle);
        ENG_Matrix3 kZMat = new ENG_Matrix3(fCos, -fSin, 0.0f, fSin, fCos, 0.0f, 0.0f, 0.0f, 1.0f);

        set(kXMat.concatenate(kYMat.concatenate(kZMat)));
    }

    public void set(int row, int column, float v) {
        if ((row < 3) && (column < 3)) {
            m[3 * row + column] = v;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setTrans(ENG_Vector3D vec3) {

        m[2] = vec3.x;
        m[5] = vec3.y;
        m[8] = vec3.z;
    }
}
