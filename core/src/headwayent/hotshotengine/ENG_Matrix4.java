/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 9:04 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import java.text.NumberFormat;

import headwayent.hotshotengine.basictypes.ENG_Float;

@SuppressWarnings("UnaryPlus")
public class ENG_Matrix4 {

    private float[] m = new float[16];
    // Too much paranoia about allocating in the inner loop leads to wasted memory...
    // that we don't have enough of in android.
    /*private float[] temp = new float[16];
	private float[] temp2 = new float[16];
	private ENG_Vector3D vec3 = new ENG_Vector3D();
	private ENG_Vector4D vec4 = new ENG_Vector4D();*/

    public ENG_Matrix4() {
        setIdentity(m);
    }

    public ENG_Matrix4(float scalar) {
        if (scalar != 0.0f) {
            set(scalar);
        }
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public ENG_Matrix4(ENG_Matrix4 mat) {
        System.arraycopy(mat.get(), 0, m, 0, m.length);
    }

    public ENG_Matrix4(float[] mat) {
        System.arraycopy(mat, 0, m, 0, m.length);
    }

    public ENG_Matrix4(float m0, float m1, float m2, float m3,
                       float m4, float m5, float m6, float m7,
                       float m8, float m9, float m10, float m11,
                       float m12, float m13, float m14, float m15) {
        set(m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12, m13, m14, m15);
    }

    private static void setMat3ToMat4(float[] mat4, float[] mat3) {
        mat4[0] = mat3[0];
        mat4[1] = mat3[1];
        mat4[2] = mat3[2];
        mat4[4] = mat3[3];
        mat4[5] = mat3[4];
        mat4[6] = mat3[5];
        mat4[8] = mat3[6];
        mat4[9] = mat3[7];
        mat4[10] = mat3[8];
    }

    public void fromAxis(ENG_Vector3D x, ENG_Vector3D y, ENG_Vector3D z) {
        matrix_set_basis_vectors(m, x, y, z);
    }

    public void fromAxis(ENG_Vector4D x, ENG_Vector4D y, ENG_Vector4D z) {
        matrix_set_basis_vectors(m, x, y, z);
    }

    private static void extractm4Fromm3(ENG_Matrix3 s, ENG_Matrix4 d) {
        float[] src = s.get();
        float[] dest = d.get();

        dest[0] = src[0];
        dest[1] = src[1];
        dest[2] = src[2];
        dest[4] = src[3];
        dest[5] = src[4];
        dest[6] = src[5];
        dest[8] = src[6];
        dest[9] = src[7];
        dest[10] = src[8];
    }

    public void extractMatrix(ENG_Matrix3 mat) {
        extractm4Fromm3(mat, this);
    }

    public ENG_Matrix4 extractMatrixRet(ENG_Matrix3 mat) {
        ENG_Matrix4 ret = new ENG_Matrix4();
        ret.set(this);
        extractm4Fromm3(mat, ret);
        return ret;
    }

    public boolean hasScale() {
        float t = m[0] * m[0] + m[4] * m[4] + m[8] * m[8];
        if (ENG_Float.compareTo(t, 1.0f, (float) 1e-04) == ENG_Utility.COMPARE_EQUAL_TO) {
            return true;
        }
        t = m[1] * m[1] + m[5] * m[5] + m[9] * m[9];
        if (ENG_Float.compareTo(t, 1.0f, (float) 1e-04) == ENG_Utility.COMPARE_EQUAL_TO) {
            return true;
        }
        t = m[2] * m[2] + m[6] * m[6] + m[10] * m[10];
        return ENG_Float.compareTo(t, 1.0f, (float) 1e-04) == ENG_Utility.COMPARE_EQUAL_TO;
    }

    public boolean hasNegativeScale() {
        return determinant() < 0.0f;
    }

    public void setRow(int row, ENG_Vector3D vec) {
        switch (row) {
            case 0:
                m[0] = vec.x;
                m[1] = vec.y;
                m[2] = vec.z;
                break;
            case 1:
                m[4] = vec.x;
                m[5] = vec.y;
                m[6] = vec.z;
                break;
            case 2:
                m[8] = vec.x;
                m[9] = vec.y;
                m[10] = vec.z;
                break;
            case 3:
                m[12] = vec.x;
                m[13] = vec.y;
                m[14] = vec.z;
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
                m[3] = vec.w;
                break;
            case 1:
                m[4] = vec.x;
                m[5] = vec.y;
                m[6] = vec.z;
                m[7] = vec.w;
                break;
            case 2:
                m[8] = vec.x;
                m[9] = vec.y;
                m[10] = vec.z;
                m[11] = vec.w;
                break;
            case 3:
                m[12] = vec.x;
                m[13] = vec.y;
                m[14] = vec.z;
                m[15] = vec.w;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void setColumn(int col, ENG_Vector3D vec) {
        switch (col) {
            case 0:
                m[0] = vec.x;
                m[4] = vec.y;
                m[8] = vec.z;
                break;
            case 1:
                m[1] = vec.x;
                m[5] = vec.y;
                m[9] = vec.z;
                break;
            case 2:
                m[2] = vec.x;
                m[6] = vec.y;
                m[10] = vec.z;
                break;
            case 3:
                m[3] = vec.x;
                m[7] = vec.y;
                m[11] = vec.z;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void setColumn(int col, ENG_Vector4D vec) {
        switch (col) {
            case 0:
                m[0] = vec.x;
                m[4] = vec.y;
                m[8] = vec.z;
                m[12] = vec.w;
                break;
            case 1:
                m[1] = vec.x;
                m[5] = vec.y;
                m[9] = vec.z;
                m[13] = vec.w;
                break;
            case 2:
                m[2] = vec.x;
                m[6] = vec.y;
                m[10] = vec.z;
                m[14] = vec.w;
                break;
            case 3:
                m[3] = vec.x;
                m[7] = vec.y;
                m[11] = vec.z;
                m[15] = vec.w;
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
                ret.x = m[4];
                ret.y = m[5];
                ret.z = m[6];
                break;
            case 2:
                ret.x = m[8];
                ret.y = m[9];
                ret.z = m[10];
                break;
            case 3:
                ret.x = m[12];
                ret.y = m[13];
                ret.z = m[14];
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
                ret.w = m[3];
                break;
            case 1:
                ret.x = m[4];
                ret.y = m[5];
                ret.z = m[6];
                ret.w = m[7];
                break;
            case 2:
                ret.x = m[8];
                ret.y = m[9];
                ret.z = m[10];
                ret.w = m[11];
                break;
            case 3:
                ret.x = m[12];
                ret.y = m[13];
                ret.z = m[14];
                ret.w = m[15];
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
                ret.y = m[4];
                ret.z = m[8];
                break;
            case 1:
                ret.x = m[1];
                ret.y = m[5];
                ret.z = m[9];
                break;
            case 2:
                ret.x = m[2];
                ret.y = m[6];
                ret.z = m[10];
                break;
            case 3:
                ret.x = m[3];
                ret.y = m[7];
                ret.z = m[11];
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void getColumn(int col, ENG_Vector4D ret) {
        switch (col) {
            case 0:
                ret.x = m[0];
                ret.y = m[4];
                ret.z = m[8];
                ret.w = m[12];
                break;
            case 1:
                ret.x = m[1];
                ret.y = m[5];
                ret.z = m[9];
                ret.w = m[13];
                break;
            case 2:
                ret.x = m[2];
                ret.y = m[6];
                ret.z = m[10];
                ret.w = m[14];
                break;
            case 3:
                ret.x = m[3];
                ret.y = m[7];
                ret.z = m[11];
                ret.w = m[15];
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
        m[9] = scalar;
        m[10] = scalar;
        m[11] = scalar;
        m[12] = scalar;
        m[13] = scalar;
        m[14] = scalar;
        m[15] = scalar;
    }

    public void set(ENG_Matrix4 mat) {
        set(mat.get());
    }

    public void set3x3(ENG_Matrix4 mat) {
        setMat4toMat43x3(m, mat.get());

    }

    private static void setMat4toMat43x3(float[] m0, float[] m1) {
        m0[0] = m1[0];
        m0[1] = m1[1];
        m0[2] = m1[2];
        m0[4] = m1[4];
        m0[5] = m1[5];
        m0[6] = m1[6];
        m0[8] = m1[8];
        m0[9] = m1[9];
        m0[10] = m1[10];
    }

    public void set(ENG_Matrix3 mat) {
        setMat3ToMat4(m, mat.get());
    }

    public void setRef(float[] m) {
        if (m == null) {
            throw new NullPointerException();
        }
        if (m.length < 16) {
            throw new IllegalArgumentException();
        }
        this.m = m;
    }

    public void set(float m0, float m1, float m2, float m3,
                    float m4, float m5, float m6, float m7,
                    float m8, float m9, float m10, float m11,
                    float m12, float m13, float m14, float m15) {
        m[0] = m0;
        m[1] = m1;
        m[2] = m2;
        m[3] = m3;
        m[4] = m4;
        m[5] = m5;
        m[6] = m6;
        m[7] = m7;
        m[8] = m8;
        m[9] = m9;
        m[10] = m10;
        m[11] = m11;
        m[12] = m12;
        m[13] = m13;
        m[14] = m14;
        m[15] = m15;
    }

    public void set(float[] m) {
        if (m == null) {
            throw new NullPointerException();
        }
        if (m.length < 16) {
            throw new IllegalArgumentException();
        }
        System.arraycopy(m, 0, this.m, 0, this.m.length);
    }

    public void set(int row, int column, float v) {
        if ((row < 4) && (column < 4)) {
            m[4 * row + column] = v;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public float[] get() {
        return m;
    }

    public void get(float[] m) {
        if (m == null) {
            throw new NullPointerException();
        }
        if (m.length < 16) {
            throw new IllegalArgumentException();
        }
        System.arraycopy(this.m, 0, m, 0, this.m.length);
    }

    public float get(int row, int column) {
        if ((row < 4) && (column < 4)) {
            return m[4 * row + column];
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void swap(ENG_Matrix4 mat) {
        float[] temp = new float[16];
        System.arraycopy(m, 0, temp, 0, 16);
        System.arraycopy(mat.get(), 0, m, 0, 16);
        System.arraycopy(temp, 0, mat.get(), 0, 16);
    }

    public void setIdentity() {
        setIdentityFull(m);
    }

    public static float minor(ENG_Matrix4 m, int r0, int r1, int r2,
                              int c0, int c1, int c2) {


        return (
                m.get(r0, c0) * (m.get(r1, c1) * m.get(r2, c2) - m.get(r2, c1) * m.get(r1, c2)) -
                        m.get(r0, c1) * (m.get(r1, c0) * m.get(r2, c2) - m.get(r2, c0) * m.get(r1, c2)) +
                        m.get(r0, c2) * (m.get(r1, c0) * m.get(r2, c1) - m.get(r2, c0) * m.get(r1, c1))
        );
    }

    public void adjoint(ENG_Matrix4 ret) {
        ret.set(minor(this, 1, 2, 3, 1, 2, 3),
                -minor(this, 0, 2, 3, 1, 2, 3),
                minor(this, 0, 1, 3, 1, 2, 3),
                -minor(this, 0, 1, 2, 1, 2, 3),

                -minor(this, 1, 2, 3, 0, 2, 3),
                minor(this, 0, 2, 3, 0, 2, 3),
                -minor(this, 0, 1, 3, 0, 2, 3),
                minor(this, 0, 1, 2, 0, 2, 3),

                minor(this, 1, 2, 3, 0, 1, 3),
                -minor(this, 0, 2, 3, 0, 1, 3),
                minor(this, 0, 1, 3, 0, 1, 3),
                -minor(this, 0, 1, 2, 0, 1, 3),

                -minor(this, 1, 2, 3, 0, 1, 2),
                minor(this, 0, 2, 3, 0, 1, 2),
                -minor(this, 0, 1, 3, 0, 1, 2),
                minor(this, 0, 1, 2, 0, 1, 2)
        );
    }

    public float determinant() {
        return m[0] * minor(this, 1, 2, 3, 1, 2, 3) -
                m[1] * minor(this, 1, 2, 3, 0, 2, 3) +
                m[2] * minor(this, 1, 2, 3, 0, 1, 3) -
                m[3] * minor(this, 1, 2, 3, 0, 1, 2);
    }

    public static void invert(ENG_Matrix4 src, ENG_Matrix4 ret) {
        float[] m = src.get();
        float m00 = m[0], m01 = m[1], m02 = m[2], m03 = m[3];
        float m10 = m[4], m11 = m[5], m12 = m[6], m13 = m[7];
        float m20 = m[8], m21 = m[9], m22 = m[10], m23 = m[11];
        float m30 = m[12], m31 = m[13], m32 = m[14], m33 = m[15];

        float v0 = m20 * m31 - m21 * m30;
        float v1 = m20 * m32 - m22 * m30;
        float v2 = m20 * m33 - m23 * m30;
        float v3 = m21 * m32 - m22 * m31;
        float v4 = m21 * m33 - m23 * m31;
        float v5 = m22 * m33 - m23 * m32;

        float t00 = +(v5 * m11 - v4 * m12 + v3 * m13);
        float t10 = -(v5 * m10 - v2 * m12 + v1 * m13);
        float t20 = +(v4 * m10 - v2 * m11 + v0 * m13);
        float t30 = -(v3 * m10 - v1 * m11 + v0 * m12);

        float invDet = 1 / (t00 * m00 + t10 * m01 + t20 * m02 + t30 * m03);

        float d00 = t00 * invDet;
        float d10 = t10 * invDet;
        float d20 = t20 * invDet;
        float d30 = t30 * invDet;

        float d01 = -(v5 * m01 - v4 * m02 + v3 * m03) * invDet;
        float d11 = +(v5 * m00 - v2 * m02 + v1 * m03) * invDet;
        float d21 = -(v4 * m00 - v2 * m01 + v0 * m03) * invDet;
        float d31 = +(v3 * m00 - v1 * m01 + v0 * m02) * invDet;

        v0 = m10 * m31 - m11 * m30;
        v1 = m10 * m32 - m12 * m30;
        v2 = m10 * m33 - m13 * m30;
        v3 = m11 * m32 - m12 * m31;
        v4 = m11 * m33 - m13 * m31;
        v5 = m12 * m33 - m13 * m32;

        float d02 = +(v5 * m01 - v4 * m02 + v3 * m03) * invDet;
        float d12 = -(v5 * m00 - v2 * m02 + v1 * m03) * invDet;
        float d22 = +(v4 * m00 - v2 * m01 + v0 * m03) * invDet;
        float d32 = -(v3 * m00 - v1 * m01 + v0 * m02) * invDet;

        v0 = m21 * m10 - m20 * m11;
        v1 = m22 * m10 - m20 * m12;
        v2 = m23 * m10 - m20 * m13;
        v3 = m22 * m11 - m21 * m12;
        v4 = m23 * m11 - m21 * m13;
        v5 = m23 * m12 - m22 * m13;

        float d03 = -(v5 * m01 - v4 * m02 + v3 * m03) * invDet;
        float d13 = +(v5 * m00 - v2 * m02 + v1 * m03) * invDet;
        float d23 = -(v4 * m00 - v2 * m01 + v0 * m03) * invDet;
        float d33 = +(v3 * m00 - v1 * m01 + v0 * m02) * invDet;

        ret.set(d00, d01, d02, d03,
                d10, d11, d12, d13,
                d20, d21, d22, d23,
                d30, d31, d32, d33);
    }

    public ENG_Matrix4 invertRet() {
        ENG_Matrix4 ret = new ENG_Matrix4(0.0f);
        invert(this, ret);
        return ret;
    }

    public void invert() {
        invert(this, this);
    }

    public void invert(ENG_Matrix4 ret) {
        invert(this, ret);
    }

    public void invertAffine(ENG_Matrix4 ret) {
        invertAffine(this, ret);
    }
	
/*	public ENG_Matrix4 invertAffineRet() {
		ENG_Matrix4 ret = new ENG_Matrix4();
		invertAffine(this, ret);
		return ret;
	}*/

    public static void invertAffine(ENG_Matrix4 src, ENG_Matrix4 ret) {
        if (src.isAffine()) {
            float[] m = src.get();
            float m10 = m[4], m11 = m[5], m12 = m[6];
            float m20 = m[8], m21 = m[9], m22 = m[10];

            float t00 = m22 * m11 - m21 * m12;
            float t10 = m20 * m12 - m22 * m10;
            float t20 = m21 * m10 - m20 * m11;

            float m00 = m[0], m01 = m[1], m02 = m[2];

            float invDet = 1 / (m00 * t00 + m01 * t10 + m02 * t20);

            t00 *= invDet;
            t10 *= invDet;
            t20 *= invDet;

            m00 *= invDet;
            m01 *= invDet;
            m02 *= invDet;

            float r00 = t00;
            float r01 = m02 * m21 - m01 * m22;
            float r02 = m01 * m12 - m02 * m11;

            float r10 = t10;
            float r11 = m00 * m22 - m02 * m20;
            float r12 = m02 * m10 - m00 * m12;

            float r20 = t20;
            float r21 = m01 * m20 - m00 * m21;
            float r22 = m00 * m11 - m01 * m10;

            float m03 = m[3], m13 = m[7], m23 = m[11];

            float r03 = -(r00 * m03 + r01 * m13 + r02 * m23);
            float r13 = -(r10 * m03 + r11 * m13 + r12 * m23);
            float r23 = -(r20 * m03 + r21 * m13 + r22 * m23);

            ret.set(r00, r01, r02, r03,
                    r10, r11, r12, r13,
                    r20, r21, r22, r23,
                    0.0f, 0.0f, 0.0f, 1.0f);
        } else {
            throw new ArithmeticException();
        }
    }

    public ENG_Matrix4 invertAffineRet() {
        ENG_Matrix4 ret = new ENG_Matrix4(0.0f);
        invertAffine(this, ret);
        return ret;
    }

    public void invertAffine() {
        invertAffine(this, this);
    }

    public void makeTransform(ENG_Vector3D position, ENG_Vector3D scale,
                              ENG_Quaternion orientation) {
        float[] temp = new float[16];
        orientation.toRotationMatrix(temp);

        m[0] = scale.x * temp[0];
        m[1] = scale.y * temp[1];
        m[2] = scale.z * temp[2];
        m[3] = position.x;

        m[4] = scale.x * temp[4];
        m[5] = scale.y * temp[5];
        m[6] = scale.z * temp[6];
        m[7] = position.y;

        m[8] = scale.x * temp[8];
        m[9] = scale.y * temp[9];
        m[10] = scale.z * temp[10];
        m[11] = position.z;

        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = 0.0f;
        m[15] = 1.0f;


    }

    public void makeTransform(ENG_Vector4D position, ENG_Vector4D scale,
                              ENG_Quaternion orientation) {
        float[] temp = new float[16];
        orientation.toRotationMatrix(temp);

        m[0] = scale.x * temp[0];
        m[1] = scale.y * temp[1];
        m[2] = scale.z * temp[2];
        m[3] = position.x;

        m[4] = scale.x * temp[4];
        m[5] = scale.y * temp[5];
        m[6] = scale.z * temp[6];
        m[7] = position.y;

        m[8] = scale.x * temp[8];
        m[9] = scale.y * temp[9];
        m[10] = scale.z * temp[10];
        m[11] = position.z;

        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = 0.0f;
        m[15] = 1.0f;
		
	/*	if (MainActivity.isDebugmode()) {
                        for (int i = 0; i < m.length; ++i) {
                                if (Float.isInfinite(m[i]) || Float.isNaN(m[i])) {
                                        String s = "";
                                        for (int j = 0; j < m.length; ++j) {
                                                s += "ind " + j + " val " + m[j] + " ";
                                        }
                                        throw new ArithmeticException(s);
                                }
                        }
                }*/
    }

    public void makeInverseTransform(ENG_Vector3D position, ENG_Vector3D scale,
                                     ENG_Quaternion orientation, ENG_Vector3D invTranslate, ENG_Vector3D invScale,
                                     ENG_Quaternion invRot, ENG_Vector3D uv, ENG_Vector3D uuv, ENG_Vector3D qvec) {
        position.invert(invTranslate);
        invScale.set(1.0f / scale.x, 1.0f / scale.y, 1.0f / scale.z);
        orientation.inverse(invRot);

        invRot.mul(invTranslate, invTranslate, uv, uuv, qvec);
        invTranslate.mulInPlace(invScale);
        float[] temp = new float[16];
        invRot.toRotationMatrix(temp);

        m[0] = invScale.x * temp[0];
        m[1] = invScale.x * temp[1];
        m[2] = invScale.x * temp[2];
        m[3] = invTranslate.x;

        m[4] = invScale.y * temp[4];
        m[5] = invScale.y * temp[5];
        m[6] = invScale.y * temp[6];
        m[7] = invTranslate.y;

        m[8] = invScale.z * temp[8];
        m[9] = invScale.z * temp[9];
        m[10] = invScale.z * temp[10];
        m[11] = invTranslate.z;

        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = 0.0f;
        m[15] = 1.0f;


    }

    public void makeInverseTransform(ENG_Vector3D position, ENG_Vector3D scale,
                                     ENG_Quaternion orientation) {
        ENG_Vector3D invTranslate = new ENG_Vector3D();
        ENG_Vector3D invScale = new ENG_Vector3D();
        ENG_Quaternion invRot = new ENG_Quaternion();
        position.invert(invTranslate);
        invScale.set(1.0f / scale.x, 1.0f / scale.y, 1.0f / scale.z);
        orientation.inverse(invRot);

        invRot.mul(invTranslate, invTranslate);
        invTranslate.mulInPlace(invScale);
        float[] temp = new float[16];
        invRot.toRotationMatrix(temp);

        m[0] = invScale.x * temp[0];
        m[1] = invScale.x * temp[1];
        m[2] = invScale.x * temp[2];
        m[3] = invTranslate.x;

        m[4] = invScale.y * temp[4];
        m[5] = invScale.y * temp[5];
        m[6] = invScale.y * temp[6];
        m[7] = invTranslate.y;

        m[8] = invScale.z * temp[8];
        m[9] = invScale.z * temp[9];
        m[10] = invScale.z * temp[10];
        m[11] = invTranslate.z;

        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = 0.0f;
        m[15] = 1.0f;


    }

    public void makeInverseTransform(ENG_Vector4D position, ENG_Vector4D scale,
                                     ENG_Quaternion orientation, ENG_Vector4D invTranslate, ENG_Vector4D invScale,
                                     ENG_Quaternion invRot, ENG_Vector4D uv, ENG_Vector4D uuv, ENG_Vector4D qvec) {
        position.invert(invTranslate);
        invScale.set(1.0f / scale.x, 1.0f / scale.y, 1.0f / scale.z, 1.0f);
        orientation.inverse(invRot);

        invRot.mul(invTranslate, invTranslate, uv, uuv, qvec);
        invTranslate.mulInPlace(invScale);
        float[] temp = new float[16];
        invRot.toRotationMatrix(temp);

        m[0] = invScale.x * temp[0];
        m[1] = invScale.x * temp[1];
        m[2] = invScale.x * temp[2];
        m[3] = invTranslate.x;

        m[4] = invScale.y * temp[4];
        m[5] = invScale.y * temp[5];
        m[6] = invScale.y * temp[6];
        m[7] = invTranslate.y;

        m[8] = invScale.z * temp[8];
        m[9] = invScale.z * temp[9];
        m[10] = invScale.z * temp[10];
        m[11] = invTranslate.z;

        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = 0.0f;
        m[15] = 1.0f;


    }

    public void makeInverseTransform(ENG_Vector4D position, ENG_Vector4D scale,
                                     ENG_Quaternion orientation) {
        ENG_Vector3D invTranslate = new ENG_Vector3D();
        ENG_Vector3D invScale = new ENG_Vector3D();
        ENG_Quaternion invRot = new ENG_Quaternion();
        position.invert(invTranslate);
        invScale.set(1.0f / scale.x, 1.0f / scale.y, 1.0f / scale.z);
        orientation.inverse(invRot);

        invRot.mul(invTranslate, invTranslate);
        invTranslate.mulInPlace(invScale);
        float[] temp = new float[16];
        invRot.toRotationMatrix(temp);

        m[0] = invScale.x * temp[0];
        m[1] = invScale.x * temp[1];
        m[2] = invScale.x * temp[2];
        m[3] = invTranslate.x;

        m[4] = invScale.y * temp[4];
        m[5] = invScale.y * temp[5];
        m[6] = invScale.y * temp[6];
        m[7] = invTranslate.y;

        m[8] = invScale.z * temp[8];
        m[9] = invScale.z * temp[9];
        m[10] = invScale.z * temp[10];
        m[11] = invTranslate.z;

        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = 0.0f;
        m[15] = 1.0f;


    }

    public boolean isAffine() {
        return ((m[12] == 0.0f) && (m[13] == 0.0f) && (m[14] == 0.0f) && (m[15] == 1.0f));
    }

    public void transformAffine(ENG_Vector3D v, ENG_Vector3D ret) {
        if (isAffine()) {
            ret.x = m[0] * v.x + m[1] * v.y + m[2] * v.z + m[3];
            ret.y = m[4] * v.x + m[5] * v.y + m[6] * v.z + m[7];
            ret.z = m[8] * v.x + m[9] * v.y + m[10] * v.z + m[11];
        }
    }

    public void transformAffine(ENG_Vector4D v, ENG_Vector4D ret) {


        if (isAffine()) {
            ret.x = m[0] * v.x + m[1] * v.y + m[2] * v.z + m[3] * v.w;
            ret.y = m[4] * v.x + m[5] * v.y + m[6] * v.z + m[7] * v.w;
            ret.z = m[8] * v.x + m[9] * v.y + m[10] * v.z + m[11] * v.w;
            ret.w = v.w;
        }
    }

    public void transformAffine(ENG_Vector4D vec) {
        ENG_Vector4D vec4 = new ENG_Vector4D();
        vec4.set(vec);
        transformAffine(vec4, vec);
    }

    public void transformAffine(ENG_Vector3D vec) {
        ENG_Vector3D vec3 = new ENG_Vector3D();
        vec3.set(vec);
        transformAffine(vec3, vec);
    }

    public ENG_Vector3D transformAffineRet(ENG_Vector3D vec) {
        ENG_Vector3D ret = new ENG_Vector3D();
        transformAffine(vec, ret);
        return ret;
    }

    public ENG_Vector4D transformAffineRet(ENG_Vector4D vec) {
        ENG_Vector4D ret = new ENG_Vector4D(true);
        transformAffine(vec, ret);
        return ret;
    }

    public ENG_Matrix4 transposeRet() {
        ENG_Matrix4 ret = new ENG_Matrix4();
        transpose(ret);
        return ret;
    }

    public void transpose(ENG_Matrix4 ret) {
        float[] v = ret.get();

        v[0] = m[0];
        v[1] = m[4];
        v[2] = m[8];
        v[3] = m[12];

        v[4] = m[1];
        v[5] = m[5];
        v[6] = m[9];
        v[7] = m[13];

        v[8] = m[2];
        v[9] = m[6];
        v[10] = m[10];
        v[11] = m[14];

        v[12] = m[3];
        v[13] = m[7];
        v[14] = m[11];
        v[15] = m[15];
    }

    public void transpose() {
        float[] temp = new float[16];
        this.get(temp);

        m[0] = temp[0];
        m[1] = temp[4];
        m[2] = temp[8];
        m[3] = temp[12];

        m[4] = temp[1];
        m[5] = temp[5];
        m[6] = temp[9];
        m[7] = temp[13];

        m[8] = temp[2];
        m[9] = temp[6];
        m[10] = temp[10];
        m[11] = temp[14];

        m[12] = temp[3];
        m[13] = temp[7];
        m[14] = temp[11];
        m[15] = temp[15];

    }

    private static void addm4m4m4(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] + m2[0];
        ret[1] = m1[1] + m2[1];
        ret[2] = m1[2] + m2[2];
        ret[3] = m1[3] + m2[3];
        ret[4] = m1[4] + m2[4];
        ret[5] = m1[5] + m2[5];
        ret[6] = m1[6] + m2[6];
        ret[7] = m1[7] + m2[7];
        ret[8] = m1[8] + m2[8];
        ret[9] = m1[9] + m2[9];
        ret[10] = m1[10] + m2[10];
        ret[11] = m1[11] + m2[11];
        ret[12] = m1[12] + m2[12];
        ret[13] = m1[13] + m2[13];
        ret[14] = m1[14] + m2[14];
        ret[15] = m1[15] + m2[15];
    }

    private static void subm4m4m4(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] - m2[0];
        ret[1] = m1[1] - m2[1];
        ret[2] = m1[2] - m2[2];
        ret[3] = m1[3] - m2[3];
        ret[4] = m1[4] - m2[4];
        ret[5] = m1[5] - m2[5];
        ret[6] = m1[6] - m2[6];
        ret[7] = m1[7] - m2[7];
        ret[8] = m1[8] - m2[8];
        ret[9] = m1[9] - m2[9];
        ret[10] = m1[10] - m2[10];
        ret[11] = m1[11] - m2[11];
        ret[12] = m1[12] - m2[12];
        ret[13] = m1[13] - m2[13];
        ret[14] = m1[14] - m2[14];
        ret[15] = m1[15] - m2[15];
    }

    private static void addm3m3m4(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] + m2[0];
        ret[1] = m1[1] + m2[1];
        ret[2] = m1[2] + m2[2];
        ret[4] = m1[3] + m2[3];
        ret[5] = m1[4] + m2[4];
        ret[6] = m1[5] + m2[5];
        ret[8] = m1[6] + m2[6];
        ret[9] = m1[7] + m2[7];
        ret[10] = m1[8] + m2[8];
    }

    private static void addm4m3m4(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] + m2[0];
        ret[1] = m1[1] + m2[1];
        ret[2] = m1[2] + m2[2];
        ret[4] = m1[4] + m2[3];
        ret[5] = m1[5] + m2[4];
        ret[6] = m1[6] + m2[5];
        ret[8] = m1[8] + m2[6];
        ret[9] = m1[9] + m2[7];
        ret[10] = m1[10] + m2[8];
    }

    private static void addm3m4m4(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] + m2[0];
        ret[1] = m1[1] + m2[1];
        ret[2] = m1[2] + m2[2];
        ret[4] = m1[3] + m2[4];
        ret[5] = m1[4] + m2[5];
        ret[6] = m1[5] + m2[6];
        ret[8] = m1[6] + m2[8];
        ret[9] = m1[7] + m2[9];
        ret[10] = m1[8] + m2[10];
    }

    private static void subm3m3m4(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] - m2[0];
        ret[1] = m1[1] - m2[1];
        ret[2] = m1[2] - m2[2];
        ret[4] = m1[3] - m2[3];
        ret[5] = m1[4] - m2[4];
        ret[6] = m1[5] - m2[5];
        ret[8] = m1[6] - m2[6];
        ret[9] = m1[7] - m2[7];
        ret[10] = m1[8] - m2[8];

    }

    private static void subm4m3m4(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] - m2[0];
        ret[1] = m1[1] - m2[1];
        ret[2] = m1[2] - m2[2];
        ret[4] = m1[4] - m2[3];
        ret[5] = m1[5] - m2[4];
        ret[6] = m1[6] - m2[5];
        ret[8] = m1[8] - m2[6];
        ret[9] = m1[9] - m2[7];
        ret[10] = m1[10] - m2[8];
    }

    private static void subm3m4m4(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] - m2[0];
        ret[1] = m1[1] - m2[1];
        ret[2] = m1[2] - m2[2];
        ret[4] = m1[3] - m2[4];
        ret[5] = m1[4] - m2[5];
        ret[6] = m1[5] - m2[6];
        ret[8] = m1[6] - m2[8];
        ret[9] = m1[7] - m2[9];
        ret[10] = m1[8] - m2[10];
    }

    private static void concatenate(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] * m2[0] + m1[1] * m2[4] + m1[2] * m2[8] + m1[3] * m2[12];
        ret[1] = m1[0] * m2[1] + m1[1] * m2[5] + m1[2] * m2[9] + m1[3] * m2[13];
        ret[2] = m1[0] * m2[2] + m1[1] * m2[6] + m1[2] * m2[10] + m1[3] * m2[14];
        ret[3] = m1[0] * m2[3] + m1[1] * m2[7] + m1[2] * m2[11] + m1[3] * m2[15];

        ret[4] = m1[4] * m2[0] + m1[5] * m2[4] + m1[6] * m2[8] + m1[7] * m2[12];
        ret[5] = m1[4] * m2[1] + m1[5] * m2[5] + m1[6] * m2[9] + m1[7] * m2[13];
        ret[6] = m1[4] * m2[2] + m1[5] * m2[6] + m1[6] * m2[10] + m1[7] * m2[14];
        ret[7] = m1[4] * m2[3] + m1[5] * m2[7] + m1[6] * m2[11] + m1[7] * m2[15];

        ret[8] = m1[8] * m2[0] + m1[9] * m2[4] + m1[10] * m2[8] + m1[11] * m2[12];
        ret[9] = m1[8] * m2[1] + m1[9] * m2[5] + m1[10] * m2[9] + m1[11] * m2[13];
        ret[10] = m1[8] * m2[2] + m1[9] * m2[6] + m1[10] * m2[10] + m1[11] * m2[14];
        ret[11] = m1[8] * m2[3] + m1[9] * m2[7] + m1[10] * m2[11] + m1[11] * m2[15];

        ret[12] = m1[12] * m2[0] + m1[13] * m2[4] + m1[14] * m2[8] + m1[15] * m2[12];
        ret[13] = m1[12] * m2[1] + m1[13] * m2[5] + m1[14] * m2[9] + m1[15] * m2[13];
        ret[14] = m1[12] * m2[2] + m1[13] * m2[6] + m1[14] * m2[10] + m1[15] * m2[14];
        ret[15] = m1[12] * m2[3] + m1[13] * m2[7] + m1[14] * m2[11] + m1[15] * m2[15];
    }

    private static void concatenateAffine(float[] m1, float[] m2, float[] ret) {
        ret[0] = m1[0] * m2[0] + m1[1] * m2[4] + m1[2] * m2[8];
        ret[1] = m1[0] * m2[1] + m1[1] * m2[5] + m1[2] * m2[9];
        ret[2] = m1[0] * m2[2] + m1[1] * m2[6] + m1[2] * m2[10];
        ret[3] = m1[0] * m2[3] + m1[1] * m2[7] + m1[2] * m2[11] + m1[3];

        ret[4] = m1[4] * m2[0] + m1[5] * m2[4] + m1[6] * m2[8];
        ret[5] = m1[4] * m2[1] + m1[5] * m2[5] + m1[6] * m2[9];
        ret[6] = m1[4] * m2[2] + m1[5] * m2[6] + m1[6] * m2[10];
        ret[7] = m1[4] * m2[3] + m1[5] * m2[7] + m1[6] * m2[11] + m1[7];

        ret[8] = m1[8] * m2[0] + m1[9] * m2[4] + m1[10] * m2[8];
        ret[9] = m1[8] * m2[1] + m1[9] * m2[5] + m1[10] * m2[9];
        ret[10] = m1[8] * m2[2] + m1[9] * m2[6] + m1[10] * m2[10];
        ret[11] = m1[8] * m2[3] + m1[9] * m2[7] + m1[10] * m2[11] + m1[11];

        ret[12] = 0.0f;
        ret[13] = 0.0f;
        ret[14] = 0.0f;
        ret[15] = 1.0f;
    }

    public void concatenateAffine(ENG_Matrix4 mat, ENG_Matrix4 ret) {
        float[] m2 = mat.get();
        float[] r = ret.get();
        concatenateAffine(m, m2, r);
    }

    public ENG_Matrix4 concatenateAffine(ENG_Matrix4 mat) {
        ENG_Matrix4 ret = new ENG_Matrix4();
        concatenateAffine(m, mat.get(), ret.get());
        return ret;
    }

    public void addInPlace(ENG_Matrix4 mat) {
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
        m[9] += f[9];
        m[10] += f[10];
        m[11] += f[11];
        m[12] += f[12];
        m[13] += f[13];
        m[14] += f[14];
        m[15] += f[15];
    }

    public void addInPlace(ENG_Matrix3 mat) {
        float[] f = mat.get();
        m[0] += f[0];
        m[1] += f[1];
        m[2] += f[2];

        m[4] += f[3];
        m[5] += f[4];
        m[6] += f[5];

        m[8] += f[6];
        m[9] += f[7];
        m[10] += f[8];

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
        m[9] -= f[9];
        m[10] -= f[10];
        m[11] -= f[11];
        m[12] -= f[12];
        m[13] -= f[13];
        m[14] -= f[14];
        m[15] -= f[15];
    }

    public void subInPlace(ENG_Matrix3 mat) {
        float[] f = mat.get();
        m[0] -= f[0];
        m[1] -= f[1];
        m[2] -= f[2];

        m[4] -= f[3];
        m[5] -= f[4];
        m[6] -= f[5];

        m[8] -= f[6];
        m[9] -= f[7];
        m[10] -= f[8];
    }

    public ENG_Matrix4 add(ENG_Matrix4 mat) {
        ENG_Matrix4 ret = new ENG_Matrix4();
        addm4m4m4(m, mat.get(), ret.get());
        return ret;
    }

    public ENG_Matrix4 add(ENG_Matrix3 mat) {
        ENG_Matrix4 ret = new ENG_Matrix4();
        addm4m3m4(m, mat.get(), ret.get());
        return ret;
    }

    public void add(ENG_Matrix4 mat, ENG_Matrix4 ret) {
        addm4m4m4(m, mat.get(), ret.get());
    }

    public void add(ENG_Matrix3 mat, ENG_Matrix4 ret) {
        addm4m3m4(m, mat.get(), ret.get());
    }

    public void sub(ENG_Matrix4 mat, ENG_Matrix4 ret) {
        subm4m4m4(m, mat.get(), ret.get());
    }

    public void sub(ENG_Matrix3 mat, ENG_Matrix4 ret) {
        subm4m3m4(m, mat.get(), ret.get());
    }

    public ENG_Matrix4 sub(ENG_Matrix4 mat) {
        ENG_Matrix4 ret = new ENG_Matrix4();
        subm4m4m4(m, mat.get(), ret.get());
        return ret;
    }

    public ENG_Matrix4 sub(ENG_Matrix3 mat) {
        ENG_Matrix4 ret = new ENG_Matrix4();
        subm4m3m4(m, mat.get(), ret.get());
        return ret;
    }

    public ENG_Matrix4 mulRet(float scalar) {
        ENG_Matrix4 mat = new ENG_Matrix4();
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
        m[9] *= scalar;
        m[10] *= scalar;
        m[11] *= scalar;
        m[12] *= scalar;
        m[13] *= scalar;
        m[14] *= scalar;
        m[15] *= scalar;
    }

    public ENG_Matrix4 divRet(float scalar) {
        ENG_Matrix4 mat = new ENG_Matrix4();
        mat.set(m);
        mat.div(scalar);
        return mat;
    }

    public void div(float scalar) {
        scalar = 1.0f / scalar;
        mul(scalar);
    }

    public void transform(float[] vectors) {
        if (vectors == null) {
            throw new NullPointerException();
        }
        if ((vectors.length % 4) != 0) {
            throw new IllegalArgumentException();
        }
    }

    private void transform(float[] m, ENG_Vector3D vec, ENG_Vector3D ret) {
        float fInvW = 1.0f / (m[3] * vec.x + m[7] * vec.y + m[11] * vec.z);

        ret.x = (m[0] * vec.x + m[1] * vec.y + m[2] * vec.z + m[3]) * fInvW;
        ret.y = (m[4] * vec.x + m[5] * vec.y + m[6] * vec.z + m[7]) * fInvW;
        ret.z = (m[8] * vec.x + m[9] * vec.y + m[10] * vec.z + m[11]) * fInvW;
    }

    private void transform(float[] m, ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = m[0] * vec.x + m[1] * vec.y + m[2] * vec.z + m[3] * vec.w;
        ret.y = m[4] * vec.x + m[5] * vec.y + m[6] * vec.z + m[7] * vec.w;
        ret.z = m[8] * vec.x + m[9] * vec.y + m[10] * vec.z + m[11] * vec.w;
        ret.w = m[12] * vec.x + m[13] * vec.y + m[14] * vec.z + m[15] * vec.w;
    }

    public void transform(ENG_Vector3D vec, ENG_Vector3D ret) {
        transform(m, vec, ret);
    }

    public void transform(ENG_Vector4D vec, ENG_Vector4D ret) {
        transform(m, vec, ret);
    }

    /**
     * @param vec transforms in place!!!
     * @return original vector from the parameter
     */
    public ENG_Vector3D transform(ENG_Vector3D vec) {
        ENG_Vector3D vec3 = new ENG_Vector3D();
        vec3.set(vec);
        transform(m, vec3, vec);
        return vec3;
    }

    /**
     * @param vec transforms in place!!!
     * @return original vector from the parameter
     */
    public ENG_Vector4D transform(ENG_Vector4D vec) {
        ENG_Vector4D vec4 = new ENG_Vector4D();
        vec4.set(vec);
        transform(m, vec4, vec);
        return vec4;
    }

    public void transform(ENG_Plane p, ENG_Plane ret,
                          ENG_Matrix4 tempMat, ENG_Vector4D tempVec) {
		/*Plane ret;
			Matrix4 invTrans = inverse().transpose();
			Vector4 v4( p.normal.x, p.normal.y, p.normal.z, p.d );
			v4 = invTrans * v4;
			ret.normal.x = v4.x; 
			ret.normal.y = v4.y; 
			ret.normal.z = v4.z;
			ret.d = v4.w / ret.normal.normalise();

            return ret;*/
        invert(tempMat);
        tempMat.transpose();
        tempVec.set(p.normal.x, p.normal.y, p.normal.z, p.d);
        tempMat.transform(tempVec);
        ret.normal.x = tempVec.x;
        ret.normal.y = tempVec.y;
        ret.normal.z = tempVec.z;
        ret.d = tempVec.w / ret.normal.normalizeRet();
    }

    public ENG_Plane transform(ENG_Plane p) {
        ENG_Plane ret = new ENG_Plane();
        ENG_Matrix4 tempMat = new ENG_Matrix4();
        ENG_Vector4D tempVec = new ENG_Vector4D();
        transform(p, ret, tempMat, tempVec);
        return ret;
    }

    public ENG_Plane transform(ENG_Plane p, ENG_Matrix4 tempMat, ENG_Vector4D tempVec) {
        ENG_Plane ret = new ENG_Plane();
        transform(p, ret, tempMat, tempVec);
        return ret;
    }

    public void setTrans(ENG_Vector3D vec) {
        m[3] = vec.x;
        m[7] = vec.y;
        m[11] = vec.z;
    }

    public void setTrans(ENG_Vector4D vec) {
        m[3] = vec.x;
        m[7] = vec.y;
        m[11] = vec.z;
    }

    public ENG_Vector3D getTransAsVec3() {
        return new ENG_Vector3D(m[3], m[7], m[11]);
    }

    public ENG_Vector4D getTransAsVec4() {
        return new ENG_Vector4D(m[3], m[7], m[11], m[15]);
    }

    public void getTrans(ENG_Vector3D vec) {
        vec.x = m[3];
        vec.y = m[7];
        vec.z = m[11];
    }

    public void getTrans(ENG_Vector4D vec) {
        vec.x = m[3];
        vec.y = m[7];
        vec.z = m[11];
        vec.w = m[15];
    }

    public void makeTrans(ENG_Vector4D vec) {
        makeTrans(this, vec);
    }

    public void makeTrans(ENG_Vector3D vec) {
        makeTrans(this, vec);
    }

    public void makeTrans(float x, float y, float z) {
        m[0] = 1.0f;
        m[1] = 0.0f;
        m[2] = 0.0f;
        m[3] = x;
        m[4] = 0.0f;
        m[5] = 1.0f;
        m[6] = 0.0f;
        m[7] = y;
        m[8] = 0.0f;
        m[9] = 0.0f;
        m[10] = 1.0f;
        m[11] = z;
        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = 0.0f;
        m[15] = 1.0f;
    }

    private static void makeTrans(float[] m, ENG_Vector3D vec) {
        //	float[] m = mat.get();
        m[0] = 1.0f;
        m[1] = 0.0f;
        m[2] = 0.0f;
        m[3] = vec.x;
        m[4] = 0.0f;
        m[5] = 1.0f;
        m[6] = 0.0f;
        m[7] = vec.y;
        m[8] = 0.0f;
        m[9] = 0.0f;
        m[10] = 1.0f;
        m[11] = vec.z;
        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = 0.0f;
        m[15] = 1.0f;
    }

    public static ENG_Matrix4 makeTranslation(ENG_Vector3D vec) {
        ENG_Matrix4 mat = new ENG_Matrix4(0.0f);
        makeTrans(mat.get(), vec);
        return mat;
    }

    public static void makeTrans(ENG_Matrix4 mat, ENG_Vector3D vec) {
        makeTrans(mat.get(), vec);
    }

    private static void makeTrans(float[] m, ENG_Vector4D vec) {
        //	float[] m = mat.get();
        m[0] = 1.0f;
        m[1] = 0.0f;
        m[2] = 0.0f;
        m[3] = vec.x;
        m[4] = 0.0f;
        m[5] = 1.0f;
        m[6] = 0.0f;
        m[7] = vec.y;
        m[8] = 0.0f;
        m[9] = 0.0f;
        m[10] = 1.0f;
        m[11] = vec.z;
        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = 0.0f;
        m[15] = 1.0f;
    }

    public static ENG_Matrix4 makeTranslation(ENG_Vector4D vec) {
        ENG_Matrix4 mat = new ENG_Matrix4(0.0f);
        makeTrans(mat.get(), vec);
        return mat;
    }

    public static void makeTrans(ENG_Matrix4 mat, ENG_Vector4D vec) {
        makeTrans(mat.get(), vec);
    }

    public void postTranslate(float x, float y, float z) {
        ENG_Vector3D vec3 = new ENG_Vector3D();
        float[] temp = new float[16];
        float[] temp2 = new float[16];
        vec3.set(x, y, z);
        makeTrans(temp2, vec3);
        this.get(temp);
        concatenate(temp, temp2, m);
    }

    public void postTranslate(ENG_Vector3D v) {
        float[] temp = new float[16];
        float[] temp2 = new float[16];
        makeTrans(temp2, v);
        this.get(temp);
        concatenate(temp, temp2, m);
    }

    public void postTranslate(ENG_Vector4D v) {
        float[] temp = new float[16];
        float[] temp2 = new float[16];
        makeTrans(temp2, v);
        this.get(temp);
        concatenate(temp, temp2, m);
    }

    public void setScale(ENG_Vector3D vec) {
        m[0] = vec.x;
        m[5] = vec.y;
        m[10] = vec.z;
    }

    public void setScale(ENG_Vector4D vec) {
        m[0] = vec.x;
        m[5] = vec.y;
        m[10] = vec.z;
    }

    public ENG_Vector3D getScaleAsVec3() {
        return new ENG_Vector3D(m[3], m[7], m[11]);
    }

    public ENG_Vector4D getScaleAsVec4() {
        return new ENG_Vector4D(m[3], m[7], m[11], m[15]);
    }

    public void getScale(ENG_Vector3D vec) {
        vec.x = m[3];
        vec.y = m[7];
        vec.z = m[11];
    }

    public void getScale(ENG_Vector4D vec) {
        vec.x = m[3];
        vec.y = m[7];
        vec.z = m[11];
        vec.w = m[15];
    }

    private static void makeScale(float[] m, ENG_Vector3D vec) {
        m[0] = vec.x;
        m[1] = 0.0f;
        m[2] = 0.0f;
        m[3] = 0.0f;
        m[4] = 0.0f;
        m[5] = vec.y;
        m[6] = 0.0f;
        m[7] = 0.0f;
        m[8] = 0.0f;
        m[9] = 0.0f;
        m[10] = vec.z;
        m[11] = 0.0f;
        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = 0.0f;
        m[15] = 1.0f;
    }

    public static ENG_Matrix4 makeScale(ENG_Vector3D vec) {
        ENG_Matrix4 mat = new ENG_Matrix4(0.0f);
        makeScale(mat.get(), vec);
        return mat;
    }

    public static void makeScale(ENG_Matrix4 mat, ENG_Vector3D vec) {
        makeScale(mat.get(), vec);
    }

    private static void makeScale(float[] m, ENG_Vector4D vec) {
        m[0] = vec.x;
        m[1] = 0.0f;
        m[2] = 0.0f;
        m[3] = 0.0f;
        m[4] = 0.0f;
        m[5] = vec.y;
        m[6] = 0.0f;
        m[7] = 0.0f;
        m[8] = 0.0f;
        m[9] = 0.0f;
        m[10] = vec.z;
        m[11] = 0.0f;
        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = 0.0f;
        m[15] = 1.0f;
    }

    public static ENG_Matrix4 makeScale(ENG_Vector4D vec) {
        ENG_Matrix4 mat = new ENG_Matrix4(0.0f);
        makeScale(mat.get(), vec);
        return mat;
    }

    public static void makeScale(ENG_Matrix4 mat, ENG_Vector4D vec) {
        makeScale(mat.get(), vec);
    }

    public void postScale(float x, float y, float z) {
        ENG_Vector3D vec3 = new ENG_Vector3D();
        float[] temp = new float[16];
        float[] temp2 = new float[16];
        vec3.set(x, y, z);
        makeScale(temp2, vec3);
        this.get(temp);
        concatenate(temp, temp2, m);
    }

    public void postScale(ENG_Vector3D v) {
        float[] temp = new float[16];
        float[] temp2 = new float[16];
        makeScale(temp2, v);
        this.get(temp);
        concatenate(temp, temp2, m);
    }

    public void postScale(ENG_Vector4D v) {
        float[] temp = new float[16];
        float[] temp2 = new float[16];
        makeScale(temp2, v);
        this.get(temp);
        concatenate(temp, temp2, m);
    }

    /*
     * 	xx(1-c)+c    xy(1-c)-zs   xz(1-c)+ys   0
        yx(1-c)+zs   yy(1-c)+c    yz(1-c)-xs   0
        xz(1-c)-ys   yz(1-c)+xs   zz(1-c)+c    0
             0            0            0       	1
     */
    private void makeRotate(float[] m, float angle, float x, float y, float z) {
        float c = ENG_Math.cos(angle);
        float s = ENG_Math.sin(angle);
        float invc = 1.0f - c;
        ENG_Vector3D vec3 = new ENG_Vector3D();
        vec3.set(x, y, z);
        if (vec3.equalsFast(ENG_Math.VEC3_ZERO)) {
            throw new IllegalArgumentException();
        }
        if (vec3.squaredLength() != 1.0f) {
            vec3.normalize();
            x = vec3.x;
            y = vec3.y;
            z = vec3.z;
        }
        m[0] = x * x * invc + c;
        m[1] = x * y * invc - z * s;
        m[2] = x * z * invc + y * s;
        m[3] = 0.0f;

        m[4] = y * x * invc + z * s;
        m[5] = y * y * invc + c;
        m[6] = y * z * invc - x * s;
        m[7] = 0.0f;

        m[8] = x * z * invc - y * s;
        m[9] = y * z * invc + x * s;
        m[10] = z * z * invc + c;
        m[11] = 0.0f;

        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = 0.0f;
        m[15] = 1.0f;
    }

    public void makeRotate(float angle, float x, float y, float z, ENG_Matrix4 mat) {
        makeRotate(mat.get(), angle, x, y, z);
    }

    public ENG_Matrix4 makeRotate(float angle, float x, float y, float z) {
        ENG_Matrix4 mat = new ENG_Matrix4();
        makeRotate(mat.get(), angle, x, y, z);
        return mat;
    }

    public void setRotate(float angle, float x, float y, float z) {
        makeRotate(m, angle, x, y, z);
    }

    public void postRotate(float angle, float x, float y, float z) {
        float[] temp = new float[16];
        float[] temp2 = new float[16];
        makeRotate(temp2, angle, x, y, z);
        this.get(temp);
        concatenate(temp, temp2, m);
    }

    public boolean equals(ENG_Matrix4 mat) {
        float[] f = mat.get();
        return !((m[0] != f[0]) || (m[1] != f[1]) || (m[2] != f[2]) || (m[3] != f[3]) ||
                (m[4] != f[4]) || (m[5] != f[5]) || (m[6] != f[6]) || (m[7] != f[7]) ||
                (m[8] != f[8]) || (m[9] != f[9]) || (m[10] != f[10]) || (m[11] != f[11]) ||
                (m[12] != f[12]) || (m[13] != f[13]) || (m[14] != f[14]) || (m[15] != f[15]));
    }

    public boolean notEquals(ENG_Matrix4 mat) {
        float[] f = mat.get();
        return (m[0] != f[0]) || (m[1] != f[1]) || (m[2] != f[2]) || (m[3] != f[3]) ||
                (m[4] != f[4]) || (m[5] != f[5]) || (m[6] != f[6]) || (m[7] != f[7]) ||
                (m[8] != f[8]) || (m[9] != f[9]) || (m[10] != f[10]) || (m[11] != f[11]) ||
                (m[12] != f[12]) || (m[13] != f[13]) || (m[14] != f[14]) || (m[15] != f[15]);
    }

    public void postMultiply(ENG_Matrix4 mat) {
        if (mat == null) {
            throw new NullPointerException();
        }
        float[] temp = new float[16];

        this.get(temp);


        concatenate(temp, mat.get(), m);

    }

    public void concatenate(ENG_Matrix4 mat, ENG_Matrix4 ret) {
        float[] retm = ret.get();
        float[] m2 = mat.get();

        concatenate(m, m2, retm);
    }

    public ENG_Matrix4 concatenate(ENG_Matrix4 mat) {
        ENG_Matrix4 ret = new ENG_Matrix4();
        concatenate(mat, ret);
        return ret;
    }

    public ENG_Matrix4 negate3x3() {
        ENG_Matrix4 ret = new ENG_Matrix4();
        negate3x3(ret);
        return ret;
    }

    public void negate3x3(ENG_Matrix4 ret) {
        negate3x3(ret, this);
    }

    public void negateInPlace3x3() {
        negate3x3(this, this);
    }

    private static void negate3x3(ENG_Matrix4 mat0, ENG_Matrix4 mat1) {
        float[] m0 = mat0.get();
        float[] m1 = mat1.get();

        m0[0] = -m1[0];
        m0[1] = -m1[1];
        m0[2] = -m1[2];
        //	m0[3] = -m1[3];
        m0[4] = -m1[4];
        m0[5] = -m1[5];
        m0[6] = -m1[6];
        //	m0[7] = -m1[7];
        m0[8] = -m1[8];
        m0[9] = -m1[9];
        m0[10] = -m1[10];
	/*	m0[11] = -m1[11];
		m0[12] = -m1[12];
		m0[13] = -m1[13];
		m0[14] = -m1[14];
		m0[15] = -m1[15];*/
    }

    public ENG_Matrix4 negate() {
        ENG_Matrix4 ret = new ENG_Matrix4();
        negate(ret);
        return ret;
    }

    public void negate(ENG_Matrix4 ret) {
        negate(ret, this);
    }

    public void negateInPlace() {
        negate(this, this);
    }

    private static void negate(ENG_Matrix4 mat0, ENG_Matrix4 mat1) {
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
        m0[9] = -m1[9];
        m0[10] = -m1[10];
        m0[11] = -m1[11];
        m0[12] = -m1[12];
        m0[13] = -m1[13];
        m0[14] = -m1[14];
        m0[15] = -m1[15];
    }

    private static void setIdentity(float[] mat) {
        mat[0] = 1.0f;
        mat[5] = 1.0f;
        mat[10] = 1.0f;
        mat[15] = 1.0f;
    }

    private static void setIdentityFull(float[] mat) {
        mat[0] = 1.0f;
        mat[1] = 0.0f;
        mat[2] = 0.0f;
        mat[3] = 0.0f;
        mat[4] = 0.0f;
        mat[5] = 1.0f;
        mat[6] = 0.0f;
        mat[7] = 0.0f;
        mat[8] = 0.0f;
        mat[9] = 0.0f;
        mat[10] = 1.0f;
        mat[11] = 0.0f;
        mat[12] = 0.0f;
        mat[13] = 0.0f;
        mat[14] = 0.0f;
        mat[15] = 1.0f;
    }

    private static void matrix_set_translation(float[] mat, float x, float y, float z) {
        mat[3] = x;
        mat[7] = y;
        mat[11] = z;
    }

    private static void matrix_set_basis_vectors(float[] mat, ENG_Vector3D xAxis, ENG_Vector3D yAxis, ENG_Vector3D zAxis) {
        mat[0] = xAxis.x;
        mat[1] = yAxis.x;
        mat[2] = zAxis.x;

        mat[4] = xAxis.y;
        mat[5] = yAxis.y;
        mat[6] = zAxis.y;

        mat[8] = xAxis.z;
        mat[9] = yAxis.z;
        mat[10] = zAxis.z;
    }

    private static void matrix_set_transposed_basis_vectors(float[] mat, ENG_Vector3D xAxis, ENG_Vector3D yAxis, ENG_Vector3D zAxis) {
        mat[0] = xAxis.x;
        mat[1] = xAxis.y;
        mat[2] = xAxis.z;

        mat[4] = yAxis.x;
        mat[5] = yAxis.y;
        mat[6] = yAxis.z;

        mat[8] = zAxis.x;
        mat[9] = zAxis.y;
        mat[10] = zAxis.z;
    }

    private static void matrix_set_basis_vectors(float[] mat, ENG_Vector4D xAxis, ENG_Vector4D yAxis, ENG_Vector4D zAxis) {
        mat[0] = xAxis.x;
        mat[1] = yAxis.x;
        mat[2] = zAxis.x;

        mat[4] = xAxis.y;
        mat[5] = yAxis.y;
        mat[6] = zAxis.y;

        mat[8] = xAxis.z;
        mat[9] = yAxis.z;
        mat[10] = zAxis.z;
    }

    private static void matrix_set_transposed_basis_vectors(float[] mat, ENG_Vector4D xAxis, ENG_Vector4D yAxis, ENG_Vector4D zAxis) {
        mat[0] = xAxis.x;
        mat[1] = xAxis.y;
        mat[2] = xAxis.z;

        mat[4] = yAxis.x;
        mat[5] = yAxis.y;
        mat[6] = yAxis.z;

        mat[8] = zAxis.x;
        mat[9] = zAxis.y;
        mat[10] = zAxis.z;
    }

    private static void set_identity_matrix_set_basis_vectors(
            float[] mat, float[] x, float[] y, float[] z) {
        mat[0] = x[0];
        mat[1] = y[0];
        mat[2] = z[0];
        mat[3] = 0.0f;
        mat[4] = x[1];
        mat[5] = y[1];
        mat[6] = z[1];
        mat[7] = 0.0f;
        mat[8] = x[2];
        mat[9] = y[2];
        mat[10] = z[2];
        mat[11] = 0.0f;
        mat[12] = 0.0f;
        mat[13] = 0.0f;
        mat[14] = 0.0f;
        mat[15] = 1.0f;
    }

    private static void set_identity_matrix_set_transposed_basis_vectors(
            float[] mat, float[] x, float[] y, float[] z) {
        mat[0] = x[0];
        mat[1] = x[1];
        mat[2] = x[2];
        mat[3] = 0.0f;
        mat[4] = y[0];
        mat[5] = y[1];
        mat[6] = y[2];
        mat[7] = 0.0f;
        mat[8] = z[0];
        mat[9] = z[1];
        mat[10] = z[2];
        mat[11] = 0.0f;
        mat[12] = 0.0f;
        mat[13] = 0.0f;
        mat[14] = 0.0f;
        mat[15] = 1.0f;
    }

    public String toString(boolean format, NumberFormat formatter) {
        StringBuilder s = new StringBuilder();
        s.append("\nMatrix4(").append(formatter.format(m[0]));
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
}
