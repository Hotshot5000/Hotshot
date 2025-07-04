package headwayent.hotshotengine;

import headwayent.hotshotengine.basictypes.ENG_Double;

public class ENG_Quaternion {

    public double x, y, z, w;

    private final double[] quat = new double[4];
    private static final int[] iNext = {1, 2, 0};

    public ENG_Quaternion() {

    }

    public ENG_Quaternion(boolean identity) {
        if (identity) {
            w = 1.0f;
        }
    }

    public ENG_Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public ENG_Quaternion(double[] q) {
        this.x = q[0];
        this.y = q[1];
        this.z = q[2];
        this.w = q[3];
    }

    public ENG_Quaternion(ENG_Quaternion q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        this.w = q.w;
    }

    public void swap(ENG_Quaternion q) {
        double t = x;
        x = q.x;
        q.x = t;
        t = y;
        y = q.y;
        q.y = t;
        t = z;
        z = q.z;
        q.z = t;
        t = w;
        w = q.w;
        q.w = t;
    }

    public void set(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void setRad(ENG_Vector3D vec, double angle) {
        x = vec.x;
        y = vec.y;
        z = vec.z;
        w = angle;
    }

    public void setDeg(ENG_Vector3D vec, double angle) {
        x = vec.x;
        y = vec.y;
        z = vec.z;
        w = angle * ENG_Math.DEGREES_TO_RADIANS;
    }

    public void set(ENG_Quaternion vec) {
        x = vec.x;
        y = vec.y;
        z = vec.z;
        w = vec.w;
    }


    public void get(ENG_Quaternion vec) {
        vec.x = x;
        vec.y = y;
        vec.z = z;
        vec.w = w;
    }

    public void set(double scalar) {
        x = scalar;
        y = scalar;
        z = scalar;
        w = scalar;
    }

    public double[] getAsArray() {
        quat[0] = x;
        quat[1] = y;
        quat[2] = z;
        quat[3] = w;
        return quat;
    }

    public void getAsArray(double[] q) {
        q[0] = x;
        q[1] = y;
        q[2] = z;
        q[3] = w;
    }

    public void fromRotationMatrix(ENG_Matrix4 mat, ENG_Quaternion q) {
        double[] m = mat.get();
        double fTrace = m[0] + m[5] + m[10];
        double fRoot;

        if (fTrace > 0.0f) {
            fRoot = ENG_Math.sqrt(fTrace + 1.0f);
            q.w = 0.5f * fRoot;
            fRoot = 0.5f / fRoot;
            q.x = (m[9] - m[6]) * fRoot;
            q.y = (m[2] - m[8]) * fRoot;
            q.z = (m[4] - m[1]) * fRoot;
        } else {
            int i = 0;
            if (m[5] > m[0]) {
                i = 1;
            }
            if (m[10] > m[5 * i]) {
                i = 2;
            }
            int j = iNext[i];
            int k = iNext[j];

            fRoot = ENG_Math.sqrt(m[5 * i] - m[5 * j] - m[5 * k] + 1.0f);
            quat[i] = 0.5f * fRoot;
            fRoot = 0.5f / fRoot;
            q.w = (mat.get(k, j) - mat.get(j, k)) * fRoot;
            quat[j] = (mat.get(j, i) + mat.get(i, j)) * fRoot;
            quat[k] = (mat.get(k, i) + mat.get(i, k)) * fRoot;
            q.x = quat[0];
            q.y = quat[1];
            q.z = quat[2];
        }
    }

    public void fromRotationMatrix(ENG_Matrix4 mat) {
        fromRotationMatrix(mat, this);
    }

    public static ENG_Quaternion fromRotationMatrixRet(ENG_Matrix4 mat) {
        ENG_Quaternion q = new ENG_Quaternion();
        q.fromRotationMatrix(mat);
        return q;
    }

    public static void fromRotationMatrixRet(ENG_Matrix4 mat, ENG_Quaternion q) {
        q.fromRotationMatrix(mat);
    }

    public static ENG_Matrix4 toRotationMatrix(ENG_Quaternion q) {
        ENG_Matrix4 mat = new ENG_Matrix4();
        toRotationMatrix(q, mat.get());
        return mat;
    }

    public static void toRotationMatrix(ENG_Quaternion q, double[] m) {
        //	double[] m = mat.get();
        double fTx = q.x + q.x;
        double fTy = q.y + q.y;
        double fTz = q.z + q.z;
        double fTwx = fTx * q.w;
        double fTwy = fTy * q.w;
        double fTwz = fTz * q.w;
        double fTxx = fTx * q.x;
        double fTxy = fTy * q.x;
        double fTxz = fTz * q.x;
        double fTyy = fTy * q.y;
        double fTyz = fTz * q.y;
        double fTzz = fTz * q.z;

        m[0] = 1.0f - (fTyy + fTzz);
        m[1] = fTxy - fTwz;
        m[2] = fTxz + fTwy;
        m[4] = fTxy + fTwz;
        m[5] = 1.0f - (fTxx + fTzz);
        m[6] = fTyz - fTwx;
        m[8] = fTxz - fTwy;
        m[9] = fTyz + fTwx;
        m[10] = 1.0f - (fTxx + fTyy);
    }

    public void toRotationMatrix(ENG_Matrix4 mat) {
        toRotationMatrix(this, mat.get());
    }

    public void toRotationMatrix(double[] mat) {
        toRotationMatrix(this, mat);
    }

    public ENG_Matrix4 toRotationMatrix() {
        return toRotationMatrix(this);
    }

    public static void fromAngleAxisRad(double angle, ENG_Vector3D vec, ENG_Quaternion q) {
        double h = angle * 0.5f;
        double sin = ENG_Math.sin(h);
        q.x = sin * vec.x;
        q.y = sin * vec.y;
        q.z = sin * vec.z;
        q.w = ENG_Math.cos(h);
    }

    public static ENG_Quaternion fromAngleAxisDegRet(double angle, ENG_Vector3D vec) {
        ENG_Quaternion ret = new ENG_Quaternion();
        fromAngleAxisDeg(angle, vec, ret);
        return ret;
    }

    public static ENG_Quaternion fromAngleAxisDegRet(double angle, ENG_Vector4D vec) {
        ENG_Quaternion ret = new ENG_Quaternion();
        fromAngleAxisDeg(angle, vec, ret);
        return ret;
    }

    public static ENG_Quaternion fromAngleAxisRadRet(double angle, ENG_Vector3D vec) {
        ENG_Quaternion ret = new ENG_Quaternion();
        fromAngleAxisRad(angle, vec, ret);
        return ret;
    }

    public static ENG_Quaternion fromAngleAxisRadRet(double angle, ENG_Vector4D vec) {
        ENG_Quaternion ret = new ENG_Quaternion();
        fromAngleAxisRad(angle, vec, ret);
        return ret;
    }

    public static void fromAngleAxisDeg(double angle, ENG_Vector3D vec, ENG_Quaternion q) {
        fromAngleAxisRad(angle * ENG_Math.DEGREES_TO_RADIANS, vec, q);
    }

    public void fromAngleAxisRad(double angle, ENG_Vector3D vec) {
        fromAngleAxisRad(angle, vec, this);
    }

    public void fromAngleAxisDeg(double angle, ENG_Vector3D vec) {
        fromAngleAxisRad(angle * ENG_Math.DEGREES_TO_RADIANS, vec, this);
    }

    public static void fromAngleAxisRad(double angle, ENG_Vector4D vec, ENG_Quaternion q) {
        double h = angle * 0.5f;
        double sin = ENG_Math.sin(h);
        q.x = sin * vec.x;
        q.y = sin * vec.y;
        q.z = sin * vec.z;
        q.w = ENG_Math.cos(h);
    }

    public void fromAngleAxis(ENG_Radian angle, ENG_Vector4D vec) {
        fromAngleAxisRad(angle.valueRadians(), vec, this);
    }

    public void fromAngleAxis(ENG_Degree angle, ENG_Vector4D vec) {
        fromAngleAxisRad(angle.valueRadians(), vec, this);
    }

    public static void fromAngleAxisDeg(double angle, ENG_Vector4D vec, ENG_Quaternion q) {
        fromAngleAxisRad(angle * ENG_Math.DEGREES_TO_RADIANS, vec, q);
    }

    public void fromAngleAxisRad(double angle, ENG_Vector4D vec) {
        fromAngleAxisRad(angle, vec, this);
    }

    public void fromAngleAxisDeg(double angle, ENG_Vector4D vec) {
        fromAngleAxisRad(angle * ENG_Math.DEGREES_TO_RADIANS, vec, this);
    }

    public static double toAngleAxisRad(ENG_Quaternion q, ENG_Vector3D vec) {
        double fSqrLen = q.x * q.x + q.y * q.y + q.z * q.z;
        double angle;
        if (fSqrLen > 0.0f) {
            angle = 2.0f * ENG_Math.acos(q.w);
            double fInvLen = ENG_Math.rsqrt(fSqrLen);
            vec.x = q.x * fInvLen;
            vec.y = q.y * fInvLen;
            vec.z = q.z * fInvLen;
        } else {
            angle = 0.0f;
            vec.x = 1.0f;
            vec.y = 0.0f;
            vec.z = 0.0f;
        }
        return angle;
    }

    public static double toAngleAxisDeg(ENG_Quaternion q, ENG_Vector3D vec) {
        return toAngleAxisRad(q, vec) * ENG_Math.RADIANS_TO_DEGREES;
    }

    public double toAngleAxisRad(ENG_Vector3D vec) {
        return toAngleAxisRad(this, vec);
    }

    public double toAngleAxisDeg(ENG_Vector3D vec) {
        return toAngleAxisRad(this, vec) * ENG_Math.RADIANS_TO_DEGREES;
    }

    public static double toAngleAxisRad(ENG_Quaternion q, ENG_Vector4D vec) {
        double fSqrLen = q.x * q.x + q.y * q.y + q.z * q.z;
        double angle;
        if (fSqrLen > 0.0f) {
            angle = 2.0f * ENG_Math.acos(q.w);
            double fInvLen = ENG_Math.rsqrt(fSqrLen);
            vec.x = q.x * fInvLen;
            vec.y = q.y * fInvLen;
            vec.z = q.z * fInvLen;
        } else {
            angle = 0.0f;
            vec.x = 1.0f;
            vec.y = 0.0f;
            vec.z = 0.0f;
        }
        return angle;
    }

    public static double toAngleAxisDeg(ENG_Quaternion q, ENG_Vector4D vec) {
        return toAngleAxisRad(q, vec) * ENG_Math.RADIANS_TO_DEGREES;
    }

    public double toAngleAxisRad(ENG_Vector4D vec) {
        return toAngleAxisRad(this, vec);
    }

    public double toAngleAxisDeg(ENG_Vector4D vec) {
        return toAngleAxisRad(this, vec) * ENG_Math.RADIANS_TO_DEGREES;
    }

    public String toAngleAxisDeg() {
        ENG_Vector4D vec = new ENG_Vector4D(true);
        double angle = toAngleAxisRad(this, vec) * ENG_Math.RADIANS_TO_DEGREES;
        return vec.toString() + " " + angle;
    }

    public static void fromAxes(ENG_Vector3D xaxis, ENG_Vector3D yaxis, ENG_Vector3D zaxis,
                                ENG_Matrix4 mat, ENG_Quaternion q) {
        double[] m = mat.get();

        m[0] = xaxis.x;
        m[4] = xaxis.y;
        m[8] = xaxis.z;

        m[1] = yaxis.x;
        m[5] = yaxis.y;
        m[9] = yaxis.z;

        m[2] = zaxis.x;
        m[6] = zaxis.y;
        m[10] = zaxis.z;

        fromRotationMatrixRet(mat, q);
    }

    public static void fromAxes(ENG_Vector3D[] vec, ENG_Matrix4 mat, ENG_Quaternion q) {
        fromAxes(vec[0], vec[1], vec[2], mat, q);
    }

    public static void fromAxes(ENG_Vector3D xaxis, ENG_Vector3D yaxis, ENG_Vector3D zaxis,
                                ENG_Quaternion q) {
        ENG_Matrix4 mat = new ENG_Matrix4(0.0f);
        fromAxes(xaxis, yaxis, zaxis, mat, q);
    }

    public static void fromAxes(ENG_Vector3D[] vec, ENG_Quaternion q) {
        fromAxes(vec[0], vec[1], vec[2], q);
    }

    public void fromAxes(ENG_Vector3D xaxis, ENG_Vector3D yaxis, ENG_Vector3D zaxis,
                         ENG_Matrix4 mat) {
        fromAxes(xaxis, yaxis, zaxis, mat, this);
    }

    public void fromAxes(ENG_Vector3D xaxis, ENG_Vector3D yaxis, ENG_Vector3D zaxis) {
        ENG_Matrix4 mat = new ENG_Matrix4(0.0f);
        fromAxes(xaxis, yaxis, zaxis, mat, this);
    }

    public static void fromAxes(ENG_Vector4D xaxis, ENG_Vector4D yaxis, ENG_Vector4D zaxis,
                                ENG_Matrix4 mat, ENG_Quaternion q) {
        double[] m = mat.get();

        m[0] = xaxis.x;
        m[4] = xaxis.y;
        m[8] = xaxis.z;

        m[1] = yaxis.x;
        m[5] = yaxis.y;
        m[9] = yaxis.z;

        m[2] = zaxis.x;
        m[6] = zaxis.y;
        m[10] = zaxis.z;

        fromRotationMatrixRet(mat, q);
    }

    public static void fromAxes(ENG_Vector4D[] vec, ENG_Matrix4 mat, ENG_Quaternion q) {
        fromAxes(vec[0], vec[1], vec[2], mat, q);
    }

    public static void fromAxes(ENG_Vector4D xaxis, ENG_Vector4D yaxis, ENG_Vector4D zaxis,
                                ENG_Quaternion q) {
        ENG_Matrix4 mat = new ENG_Matrix4(0.0f);
        fromAxes(xaxis, yaxis, zaxis, mat, q);
    }

    public static void fromAxes(ENG_Vector4D[] vec, ENG_Quaternion q) {
        fromAxes(vec[0], vec[1], vec[2], q);
    }

    public void fromAxes(ENG_Vector4D xaxis, ENG_Vector4D yaxis, ENG_Vector4D zaxis,
                         ENG_Matrix4 mat) {
        fromAxes(xaxis, yaxis, zaxis, mat, this);
    }

    public void fromAxes(ENG_Vector4D xaxis, ENG_Vector4D yaxis, ENG_Vector4D zaxis) {
        ENG_Matrix4 mat = new ENG_Matrix4(0.0f);
        fromAxes(xaxis, yaxis, zaxis, mat, this);
    }

    public static void toAxes(ENG_Vector3D xaxis, ENG_Vector3D yaxis, ENG_Vector3D zaxis,
                              ENG_Matrix4 mat, ENG_Quaternion q) {
        q.toRotationMatrix(mat);
        double[] m = mat.get();

        xaxis.x = m[0];
        xaxis.y = m[4];
        xaxis.z = m[8];

        yaxis.x = m[1];
        yaxis.y = m[5];
        yaxis.z = m[9];

        zaxis.x = m[2];
        zaxis.y = m[6];
        zaxis.z = m[10];
    }

    public static void toAxes(ENG_Vector3D[] vec, ENG_Matrix4 mat, ENG_Quaternion q) {
        toAxes(vec[0], vec[1], vec[2], mat, q);
    }

    public static void toAxes(ENG_Vector3D xaxis, ENG_Vector3D yaxis, ENG_Vector3D zaxis,
                              ENG_Quaternion q) {
        ENG_Matrix4 mat = new ENG_Matrix4(0.0f);
        toAxes(xaxis, yaxis, zaxis, mat, q);
    }

    public static void toAxes(ENG_Vector3D[] vec, ENG_Quaternion q) {
        toAxes(vec[0], vec[1], vec[2], q);
    }

    public void toAxes(ENG_Vector3D xaxis, ENG_Vector3D yaxis, ENG_Vector3D zaxis,
                       ENG_Matrix4 mat) {
        toAxes(xaxis, yaxis, zaxis, mat, this);
    }

    public void toAxes(ENG_Vector3D xaxis, ENG_Vector3D yaxis, ENG_Vector3D zaxis) {
        toAxes(xaxis, yaxis, zaxis, this);
    }

    public static void toAxes(ENG_Vector4D xaxis, ENG_Vector4D yaxis, ENG_Vector4D zaxis,
                              ENG_Matrix4 mat, ENG_Quaternion q) {
        q.toRotationMatrix(mat);
        double[] m = mat.get();

        xaxis.x = m[0];
        xaxis.y = m[4];
        xaxis.z = m[8];

        yaxis.x = m[1];
        yaxis.y = m[5];
        yaxis.z = m[9];

        zaxis.x = m[2];
        zaxis.y = m[6];
        zaxis.z = m[10];
    }

    public static void toAxes(ENG_Vector4D[] vec, ENG_Matrix4 mat, ENG_Quaternion q) {
        toAxes(vec[0], vec[1], vec[2], mat, q);
    }

    public static void toAxes(ENG_Vector4D xaxis, ENG_Vector4D yaxis, ENG_Vector4D zaxis,
                              ENG_Quaternion q) {
        ENG_Matrix4 mat = new ENG_Matrix4(0.0f);
        toAxes(xaxis, yaxis, zaxis, mat, q);
    }

    public static void toAxes(ENG_Vector4D[] vec, ENG_Quaternion q) {
        toAxes(vec[0], vec[1], vec[2], q);
    }

    public void toAxes(ENG_Vector4D xaxis, ENG_Vector4D yaxis, ENG_Vector4D zaxis,
                       ENG_Matrix4 mat) {
        toAxes(xaxis, yaxis, zaxis, mat, this);
    }

    public void toAxes(ENG_Vector4D xaxis, ENG_Vector4D yaxis, ENG_Vector4D zaxis) {
        toAxes(xaxis, yaxis, zaxis, this);
    }

    public void xAxis(ENG_Vector4D vec) {
        double fTy = 2.0f * y;
        double fTz = 2.0f * z;
        double fTwy = fTy * w;
        double fTwz = fTz * w;
        double fTxy = fTy * x;
        double fTxz = fTz * x;
        double fTyy = fTy * y;
        double fTzz = fTz * z;

        vec.set(1.0f - (fTyy + fTzz), fTxy + fTwz, fTxz - fTwy, 0.0f);
    }

    public ENG_Vector4D xAxisVec4() {
        ENG_Vector4D vec = new ENG_Vector4D();
        xAxis(vec);
        return vec;
    }

    public void yAxis(ENG_Vector4D vec) {
        double fTx = 2.0f * x;
        double fTy = 2.0f * y;
        double fTz = 2.0f * z;
        double fTwx = fTx * w;
        double fTwz = fTz * w;
        double fTxx = fTx * x;
        double fTxy = fTy * x;
        double fTyz = fTz * y;
        double fTzz = fTz * z;

        vec.set(fTxy - fTwz, 1.0f - (fTxx + fTzz), fTyz + fTwx, 0.0f);
    }

    public ENG_Vector4D yAxisVec4() {
        ENG_Vector4D vec = new ENG_Vector4D();
        yAxis(vec);
        return vec;
    }

    public void zAxis(ENG_Vector4D vec) {
        double fTx = 2.0f * x;
        double fTy = 2.0f * y;
        double fTz = 2.0f * z;
        double fTwx = fTx * w;
        double fTwy = fTy * w;
        double fTxx = fTx * x;
        double fTxz = fTz * x;
        double fTyy = fTy * y;
        double fTyz = fTz * y;

        vec.set(fTxz + fTwy, fTyz - fTwx, 1.0f - (fTxx + fTyy), 0.0f);
    }

    public ENG_Vector4D zAxisVec4() {
        ENG_Vector4D vec = new ENG_Vector4D();
        zAxis(vec);
        return vec;
    }

    public void xAxis(ENG_Vector3D vec) {
        double fTy = 2.0f * y;
        double fTz = 2.0f * z;
        double fTwy = fTy * w;
        double fTwz = fTz * w;
        double fTxy = fTy * x;
        double fTxz = fTz * x;
        double fTyy = fTy * y;
        double fTzz = fTz * z;

        vec.set(1.0f - (fTyy + fTzz), fTxy + fTwz, fTxz - fTwy);
    }

    public ENG_Vector3D xAxisVec3() {
        ENG_Vector3D vec = new ENG_Vector3D();
        xAxis(vec);
        return vec;
    }

    public void yAxis(ENG_Vector3D vec) {
        double fTx = 2.0f * x;
        double fTy = 2.0f * y;
        double fTz = 2.0f * z;
        double fTwx = fTx * w;
        double fTwz = fTz * w;
        double fTxx = fTx * x;
        double fTxy = fTy * x;
        double fTyz = fTz * y;
        double fTzz = fTz * z;

        vec.set(fTxy - fTwz, 1.0f - (fTxx + fTzz), fTyz + fTwx);
    }

    public ENG_Vector3D yAxisVec3() {
        ENG_Vector3D vec = new ENG_Vector3D();
        yAxis(vec);
        return vec;
    }

    public void zAxis(ENG_Vector3D vec) {
        double fTx = 2.0f * x;
        double fTy = 2.0f * y;
        double fTz = 2.0f * z;
        double fTwx = fTx * w;
        double fTwy = fTy * w;
        double fTxx = fTx * x;
        double fTxz = fTz * x;
        double fTyy = fTy * y;
        double fTyz = fTz * y;

        vec.set(fTxz + fTwy, fTyz - fTwx, 1.0f - (fTxx + fTyy));
    }

    public ENG_Vector3D zAxisVec3() {
        ENG_Vector3D vec = new ENG_Vector3D();
        zAxis(vec);
        return vec;
    }

    public void add(ENG_Quaternion q, ENG_Quaternion ret) {
        ret.x = x + q.x;
        ret.y = y + q.y;
        ret.z = z + q.z;
        ret.w = w + q.w;
    }

    public ENG_Quaternion addRet(ENG_Quaternion q, ENG_Quaternion ret) {
        add(q, ret);
        return ret;
    }

    public ENG_Quaternion addRet(ENG_Quaternion q) {
        ENG_Quaternion ret = new ENG_Quaternion();
        return addRet(q, ret);
    }

    public void addInPlace(ENG_Quaternion q) {
        x += q.x;
        y += q.y;
        z += q.z;
        w += q.w;
    }

    public void sub(ENG_Quaternion q, ENG_Quaternion ret) {
        ret.x = x - q.x;
        ret.y = y - q.y;
        ret.z = z - q.z;
        ret.w = w - q.w;
    }

    public ENG_Quaternion subRet(ENG_Quaternion q, ENG_Quaternion ret) {
        sub(q, ret);
        return ret;
    }

    public ENG_Quaternion subRet(ENG_Quaternion q) {
        ENG_Quaternion ret = new ENG_Quaternion();
        return subRet(q, ret);
    }

    public void subInPlace(ENG_Quaternion q) {
        x -= q.x;
        y -= q.y;
        z -= q.z;
        w -= q.w;
    }

    public void mul(double scalar, ENG_Quaternion q) {
        q.x = x * scalar;
        q.y = y * scalar;
        q.z = z * scalar;
        q.w = w * scalar;
    }

    public ENG_Quaternion mulRet(double scalar, ENG_Quaternion q) {
        mul(scalar, q);
        return q;
    }

    public ENG_Quaternion mulRet(double scalar) {
        ENG_Quaternion q = new ENG_Quaternion();
        return mulRet(scalar, q);
    }

    public void mulInPlace(double scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        w *= scalar;
    }

    public void mul(ENG_Quaternion q, ENG_Quaternion ret) {
        ret.set(
                w * q.x + x * q.w + y * q.z - z * q.y,
                w * q.y + y * q.w + z * q.x - x * q.z,
                w * q.z + z * q.w + x * q.y - y * q.x,
                w * q.w - x * q.x - y * q.y - z * q.z);
    }

    public ENG_Quaternion mulRet(ENG_Quaternion q, ENG_Quaternion ret) {
        mul(q, ret);
        return ret;
    }

    public ENG_Quaternion mulRet(ENG_Quaternion q) {
        ENG_Quaternion ret = new ENG_Quaternion();
        return mulRet(q, ret);
    }

    public void mulInPlace(ENG_Quaternion q) {
        /*
		 * 
		            w * rkQ.x + x * rkQ.w + y * rkQ.z - z * rkQ.y,
		            w * rkQ.y + y * rkQ.w + z * rkQ.x - x * rkQ.z,
		            w * rkQ.z + z * rkQ.w + x * rkQ.y - y * rkQ.x
		            w * rkQ.w - x * rkQ.x - y * rkQ.y - z * rkQ.z,
		 */
        double qx = w * q.x + x * q.w + y * q.z - z * q.y;
        double qy = w * q.y + y * q.w + z * q.x - x * q.z;
        double qz = w * q.z + z * q.w + x * q.y - y * q.x;
        double qw = w * q.w - x * q.x - y * q.y - z * q.z;
        set(qx, qy, qz, qw);
    }

    public static void negate(ENG_Quaternion q, ENG_Quaternion ret) {
        ret.x = -q.x;
        ret.y = -q.y;
        ret.z = -q.z;
        ret.w = -q.w;
    }

    public static ENG_Quaternion negateRet(ENG_Quaternion q, ENG_Quaternion ret) {
        negate(q, ret);
        return ret;
    }

    public static ENG_Quaternion negateRet(ENG_Quaternion q) {
        ENG_Quaternion ret = new ENG_Quaternion();
        return negateRet(q, ret);
    }

    public void negateInPlace() {
        negate(this, this);
    }

    public double dot(ENG_Quaternion q) {
        return (x * q.x + y * q.y + z * q.z + w * q.w);
    }

    public double norm() {
        return (x * x + y * y + z * z + w * w);
    }

    public void inverse(ENG_Quaternion ret) {
        double norm = norm();
        if (norm > 0.0f) {
            double fInvNorm = 1.0f / norm;
            ret.set(-x * fInvNorm, -y * fInvNorm, -z * fInvNorm, w * fInvNorm);
        } else {
            throw new ArithmeticException();
        }
    }

    public void inverse() {
        inverse(this);
    }

    public ENG_Quaternion inverseRet(ENG_Quaternion ret) {
        inverse(ret);
        return ret;
    }

    public ENG_Quaternion inverseRet() {
        ENG_Quaternion q = new ENG_Quaternion();
        return inverseRet(q);
    }

    public void unitInverse(ENG_Quaternion ret) {
        ret.set(-x, -y, -z, w);
    }

    public void unitInverse() {
        unitInverse(this);
    }

    public ENG_Quaternion unitInverseRet(ENG_Quaternion ret) {
        unitInverse(ret);
        return ret;
    }

    public ENG_Quaternion unitInverseRet() {
        ENG_Quaternion q = new ENG_Quaternion();
        return unitInverseRet(q);
    }

    public void exp(ENG_Quaternion ret) {
        double fAngle = ENG_Math.sqrt(x * x + y * y + z * z);
        double fSin = ENG_Math.sin(fAngle);

        ret.w = ENG_Math.cos(fAngle);

        if (Math.abs(fSin) >= ENG_Math.FLOAT_EPSILON) {
            double fCoeff = fSin / fAngle;
            ret.x = x * fCoeff;
            ret.y = y * fCoeff;
            ret.z = z * fCoeff;
        } else {
            ret.x = x;
            ret.y = y;
            ret.z = z;
        }
    }

    public ENG_Quaternion exp() {
        ENG_Quaternion ret = new ENG_Quaternion();
        exp(ret);
        return ret;
    }

    public void log(ENG_Quaternion ret) {
        ret.w = 0.0f;

        if (Math.abs(w) < 1.0f) {
            double fAngle = ENG_Math.acos(w);
            double fSin = ENG_Math.sin(fAngle);
            if (Math.abs(fSin) >= ENG_Math.FLOAT_EPSILON) {
                double fCoeff = fAngle / fSin;
                ret.x = x * fCoeff;
                ret.y = y * fCoeff;
                ret.z = z * fCoeff;
                return;
            }
        }
        ret.x = x;
        ret.y = y;
        ret.z = z;
    }

    public ENG_Quaternion log() {
        ENG_Quaternion ret = new ENG_Quaternion();
        log(ret);
        return ret;
    }

    public void mul(ENG_Vector3D v, ENG_Vector3D ret) {
        ENG_Vector3D uv = new ENG_Vector3D();
        ENG_Vector3D uuv = new ENG_Vector3D();
        ENG_Vector3D qvec = new ENG_Vector3D();
        mul(v, ret, uv, uuv, qvec);
    }

    public void mul(ENG_Vector3D v, ENG_Vector3D ret, ENG_Vector3D uv, ENG_Vector3D uuv,
                    ENG_Vector3D qvec) {
        qvec.set(x, y, z);
        qvec.crossProduct(v, uv);
        qvec.crossProduct(uv, uuv);
        uv.mulInPlace(2.0f * w);
        uuv.mulInPlace(2.0f);
        uv.addInPlace(uuv);
        ret.set(v);
        ret.addInPlace(uv);
    }

    public ENG_Vector3D mul(ENG_Vector3D v) {
        ENG_Vector3D ret = new ENG_Vector3D();
        mul(v, ret);
        return ret;
    }

    public void mul(ENG_Vector4D v, ENG_Vector4D ret) {
        ENG_Vector4D uv = new ENG_Vector4D();
        ENG_Vector4D uuv = new ENG_Vector4D();
        ENG_Vector4D qvec = new ENG_Vector4D();
        mul(v, ret, uv, uuv, qvec);
    }

    public void mul(ENG_Vector4D v, ENG_Vector4D ret, ENG_Vector4D uv,
                    ENG_Vector4D uuv, ENG_Vector4D qvec) {
        qvec.set(x, y, z, 1.0f);
        qvec.crossProduct(v, uv);
        qvec.crossProduct(uv, uuv);
        uv.mul(2.0f * w);
        uuv.mul(2.0f);
        uv.addInPlace(uuv);
        ret.set(v);
        ret.addInPlace(uv);
    }

    public ENG_Vector4D mul(ENG_Vector4D v) {
        ENG_Vector4D ret = new ENG_Vector4D();
        mul(v, ret);
        return ret;
    }

    public void normalize() {
        mulInPlace(1.0f / ENG_Math.sqrt(norm()));
    }

    public void normalize(ENG_Quaternion ret) {
        mul(1.0f / ENG_Math.sqrt(norm()), ret);
    }

    public static void slerp(double fT, ENG_Quaternion rkP, ENG_Quaternion rkQ,
                             boolean shortestPath, ENG_Quaternion rkT) {
        slerp(fT, rkP, rkQ, shortestPath, rkT, new ENG_Quaternion());
    }

    public static void slerp(double fT, ENG_Quaternion rkP, ENG_Quaternion rkQ,
                             boolean shortestPath, ENG_Quaternion rkT,
                             ENG_Quaternion temp) {
        double fCos = rkP.dot(rkQ);

        if ((fCos < 0.0f) && (shortestPath)) {
            fCos = -fCos;
            negate(rkQ, rkT);
        } else {
            rkT.set(rkQ);
        }

        if (Math.abs(fCos) < (1.0f - ENG_Math.FLOAT_EPSILON)) {
            double fSin = ENG_Math.sqrt(1.0f - ENG_Math.sqr(fCos));
            double fAngle = ENG_Math.atan2(fSin, fCos);
            double fInvSin = 1.0f / fSin;
            double fCoeff0 = ENG_Math.sin((1.0f - fT) * fAngle) * fInvSin;
            double fCoeff1 = ENG_Math.sin(fT * fAngle) * fInvSin;
            rkP.mul(fCoeff0, temp);
            rkT.mulInPlace(fCoeff1);
            rkT.addInPlace(temp);
        } else {
            rkP.mul(1.0f - fT, temp);
            rkT.mulInPlace(fT);
            rkT.addInPlace(temp);
            rkT.normalize();
        }
    }

    public static ENG_Quaternion slerp(double fT, ENG_Quaternion rkP, ENG_Quaternion rkQ,
                                       boolean shortestPath) {
        ENG_Quaternion rkT = new ENG_Quaternion();
        slerp(fT, rkP, rkQ, shortestPath, rkT);
        return rkT;
    }

    public double getRoll(boolean reprojectAxis) {
        if (reprojectAxis) {
            double fTy = 2.0f * y;
            double fTz = 2.0f * z;
            double fTwz = fTz * w;
            double fTxy = fTy * x;
            double fTyy = fTy * y;
            double fTzz = fTz * z;

            return ENG_Math.atan2(fTxy + fTwz, 1.0f - (fTyy + fTzz));
        } else {

            return ENG_Math.atan2(w * w + x * x - y * y - z * z, 2 * (x * y + w * z));
        }
    }

    public double getPitch(boolean reprojectAxis) {
        if (reprojectAxis) {
            double fTx = 2.0f * x;
            double fTz = 2.0f * z;
            double fTwx = fTx * w;
            double fTxx = fTx * x;
            double fTyz = fTz * y;
            double fTzz = fTz * z;

            return ENG_Math.atan2(fTyz + fTwx, 1.0f - (fTxx + fTzz));
        } else {

            return ENG_Math.atan2(w * w - x * x - y * y + z * z, 2 * (y * z + w * x));
        }
    }

    public double getYaw(boolean reprojectAxis) {
        if (reprojectAxis) {
            double fTx = 2.0f * x;
            double fTy = 2.0f * y;
            double fTz = 2.0f * z;
            double fTwy = fTy * w;
            double fTxx = fTx * x;
            double fTxz = fTz * x;
            double fTyy = fTy * y;


            return ENG_Math.atan2(fTxz + fTwy, 1.0f - (fTxx + fTyy));
        } else {

            return ENG_Math.asin(-2 * (x * z - w * y));
        }
    }

    public static void nlerp(double fT, ENG_Quaternion rkP, ENG_Quaternion rkQ,
                             boolean shortestPath, ENG_Quaternion rkT) {
        nlerp(fT, rkP, rkQ, shortestPath, rkT, new ENG_Quaternion());
    }

    public static void nlerp(double fT, ENG_Quaternion rkP, ENG_Quaternion rkQ,
                             boolean shortestPath, ENG_Quaternion rkT,
                             ENG_Quaternion temp) {
        double fCos = rkP.dot(rkQ);
        if ((fCos < 0.0f) && (shortestPath)) {

            negate(rkQ, temp);
            temp.subInPlace(rkP);
            temp.mulInPlace(fT);
            rkT.set(rkP);
            rkT.addInPlace(temp);
        } else {
            rkQ.sub(rkP, temp);
            temp.mulInPlace(fT);
            rkT.set(rkP);
            rkT.addInPlace(temp);
        }
        rkT.normalize();
    }

    public static ENG_Quaternion nlerp(double fT, ENG_Quaternion rkP, ENG_Quaternion rkQ,
                                       boolean shortestPath) {
        ENG_Quaternion rkT = new ENG_Quaternion();
        nlerp(fT, rkP, rkQ, shortestPath, rkT);
        return rkT;
    }

    public boolean equalsFast(ENG_Quaternion q) {
        return ((x == q.x) && (y == q.y) && (z == q.z) && (w == q.w));
    }

    public boolean notEqualsFast(ENG_Quaternion q) {
        return ((x != q.x) || (y != q.y) || (z != q.z) || (w != q.w));
    }

    public boolean equals(ENG_Quaternion q) {
        return ((ENG_Double.compareTo(x, q.x) == ENG_Utility.COMPARE_EQUAL_TO) &&
                (ENG_Double.compareTo(y, q.y) == ENG_Utility.COMPARE_EQUAL_TO) &&
                (ENG_Double.compareTo(z, q.z) == ENG_Utility.COMPARE_EQUAL_TO) &&
                (ENG_Double.compareTo(w, q.w) == ENG_Utility.COMPARE_EQUAL_TO));
    }

    public boolean notEquals(ENG_Quaternion q) {
        return ((ENG_Double.compareTo(x, q.x) != ENG_Utility.COMPARE_EQUAL_TO) ||
                (ENG_Double.compareTo(y, q.y) != ENG_Utility.COMPARE_EQUAL_TO) ||
                (ENG_Double.compareTo(z, q.z) != ENG_Utility.COMPARE_EQUAL_TO) ||
                (ENG_Double.compareTo(w, q.w) != ENG_Utility.COMPARE_EQUAL_TO));
    }

    public boolean isNaN() {
        return ((Double.isNaN(x)) || (Double.isNaN(y)) || (Double.isNaN(z)) || (Double.isNaN(w)));
    }

    public boolean isInfinite() {
        return ((Double.isInfinite(x)) ||
                (Double.isInfinite(y)) || (Double.isInfinite(z)) || (Double.isInfinite(w)));
    }

    public boolean isInvalid() {
        return isNaN() || isInfinite();
    }

    public static ENG_Quaternion getZeroQuat() {
        return new ENG_Quaternion();
    }

    public static ENG_Quaternion getIdentityQuat() {
        return new ENG_Quaternion(true);
    }

    public void setZero() {
        x = 0.0f;
        y = 0.0f;
        z = 0.0f;
        w = 0.0f;
    }

    public void setIdentity() {
        x = 0.0f;
        y = 0.0f;
        z = 0.0f;
        w = 1.0f;
    }

    private static void resetQuatForAdd(double[] q) {
        q[0] = 0.0f;
        q[1] = 0.0f;
        q[2] = 0.0f;
        q[3] = 0.0f;
    }

    private static void resetQuatForMul(double[] q) {
        q[0] = 0.0f;
        q[1] = 0.0f;
        q[2] = 0.0f;
        q[3] = 1.0f;
    }

    public static ENG_Quaternion squad(double t, ENG_Quaternion p,
                                       ENG_Quaternion a, ENG_Quaternion b, ENG_Quaternion q) {
        return squad(t, p, a, b, q, false);
    }

    public static ENG_Quaternion squad(double t, ENG_Quaternion p,
                                       ENG_Quaternion a, ENG_Quaternion b, ENG_Quaternion q,
                                       boolean shortestPath) {
        ENG_Quaternion ret = new ENG_Quaternion();
        ENG_Quaternion t1 = new ENG_Quaternion();
        ENG_Quaternion t2 = new ENG_Quaternion();
        squad(t, p, a, b, q, shortestPath, ret, t1, t2);
        return ret;
    }

    public static void squad(double t, ENG_Quaternion p,
                             ENG_Quaternion a, ENG_Quaternion b, ENG_Quaternion q,
                             ENG_Quaternion ret, ENG_Quaternion t1, ENG_Quaternion t2) {
        squad(t, p, a, b, q, false, ret, t1, t2);
    }

    public static void squad(double t, ENG_Quaternion p,
                             ENG_Quaternion a, ENG_Quaternion b, ENG_Quaternion q,
                             boolean shortestPath,
                             ENG_Quaternion ret, ENG_Quaternion t1, ENG_Quaternion t2) {
        double fSlerpT = 2.0f * t * (1.0f - t);
        ENG_Quaternion.slerp(t, p, q, shortestPath, t1);
        ENG_Quaternion.slerp(t, a, b, false, t2);
        ENG_Quaternion.slerp(fSlerpT, t1, t2, false, ret);
    }

    public String toString() {
        return (String.valueOf(x) + " " + String.valueOf(y) + " " +
                String.valueOf(z) + " " + String.valueOf(w));
    }
}
