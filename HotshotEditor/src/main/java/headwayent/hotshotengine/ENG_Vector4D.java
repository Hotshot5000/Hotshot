package headwayent.hotshotengine;

import java.text.NumberFormat;

import headwayent.hotshotengine.basictypes.ENG_Double;

public class ENG_Vector4D {

    public double x, y, z, w;

    public ENG_Vector4D() {

    }

    public ENG_Vector4D(boolean asPt) {
        if (asPt) {
            w = 1.0f;
        }
    }

    public ENG_Vector4D(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public ENG_Vector4D(double[] vec) {
        set(vec);
    }

    public ENG_Vector4D(double[] vec, int offset) {
        set(vec, offset);
    }

    public ENG_Vector4D(ENG_Vector4D vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        this.w = vec.w;
    }

    public ENG_Vector4D(ENG_Vector3D vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public ENG_Vector4D(double scalar) {
        this.x = scalar;
        this.y = scalar;
        this.z = scalar;
        this.w = scalar;
    }

    public void swap(ENG_Vector4D vec) {
        double temp = x;
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

    public void set(double[] vec) {
        this.x = vec[0];
        this.y = vec[1];
        this.z = vec[2];
        this.w = vec[3];
    }

    public void set(double[] vec, int offset) {
        this.x = vec[offset];
        this.y = vec[offset + 1];
        this.z = vec[offset + 2];
        this.w = vec[offset + 3];
    }

    public void set(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void set(double x, double y, double z) {
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

    public double get(int index) {
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

    public void set(double scalar) {
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
        return ((ENG_Double.compareTo(x, vec.x) == ENG_Utility.COMPARE_EQUAL_TO) &&
                (ENG_Double.compareTo(y, vec.y) == ENG_Utility.COMPARE_EQUAL_TO) &&
                (ENG_Double.compareTo(z, vec.z) == ENG_Utility.COMPARE_EQUAL_TO));
    }

    public boolean notEquals(ENG_Vector4D vec) {
        return ((ENG_Double.compareTo(x, vec.x) != ENG_Utility.COMPARE_EQUAL_TO) ||
                (ENG_Double.compareTo(y, vec.y) != ENG_Utility.COMPARE_EQUAL_TO) ||
                (ENG_Double.compareTo(z, vec.z) != ENG_Utility.COMPARE_EQUAL_TO));
    }

    public boolean equalsFastFull(ENG_Vector4D vec) {
        return ((x == vec.x) && (y == vec.y) && (z == vec.z) && (w == vec.w));
    }

    public boolean notEqualsFastFull(ENG_Vector4D vec) {
        return ((x != vec.x) || (y != vec.y) || (z != vec.z) || (w != vec.w));
    }

    public boolean equalsFull(ENG_Vector4D vec) {
        return ((ENG_Double.compareTo(x, vec.x) == ENG_Utility.COMPARE_EQUAL_TO) &&
                (ENG_Double.compareTo(y, vec.y) == ENG_Utility.COMPARE_EQUAL_TO) &&
                (ENG_Double.compareTo(z, vec.z) == ENG_Utility.COMPARE_EQUAL_TO) &&
                (ENG_Double.compareTo(w, vec.w) == ENG_Utility.COMPARE_EQUAL_TO));
    }

    public boolean notEqualsFull(ENG_Vector4D vec) {
        return ((ENG_Double.compareTo(x, vec.x) != ENG_Utility.COMPARE_EQUAL_TO) ||
                (ENG_Double.compareTo(y, vec.y) != ENG_Utility.COMPARE_EQUAL_TO) ||
                (ENG_Double.compareTo(z, vec.z) != ENG_Utility.COMPARE_EQUAL_TO) ||
                (ENG_Double.compareTo(w, vec.w) != ENG_Utility.COMPARE_EQUAL_TO));
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

    public ENG_Vector4D mulAsPt(double scalar) {
        return new ENG_Vector4D(x * scalar, y * scalar, z * scalar, 1.0f);
    }

    public ENG_Vector4D mulAsVec(double scalar) {
        return new ENG_Vector4D(x * scalar, y * scalar, z * scalar, 0.0f);
    }

    public ENG_Vector4D mulFullRet(double scalar) {
        return new ENG_Vector4D(x * scalar, y * scalar, z * scalar, w * scalar);
    }

    public void mul(double scalar, ENG_Vector4D ret) {
        ret.x = x * scalar;
        ret.y = y * scalar;
        ret.z = z * scalar;
    }

    public void mulFull(double scalar, ENG_Vector4D ret) {
        ret.x = x * scalar;
        ret.y = y * scalar;
        ret.z = z * scalar;
        ret.w = w * scalar;
    }

    public void mul(double scalar, ENG_Vector3D ret) {
        ret.x = x * scalar;
        ret.y = y * scalar;
        ret.z = z * scalar;
    }

    public ENG_Vector4D mulRet(double scalar, ENG_Vector4D ret) {
        ret.x = x * scalar;
        ret.y = y * scalar;
        ret.z = z * scalar;
        return ret;
    }

    public ENG_Vector4D mulRetFull(double scalar, ENG_Vector4D ret) {
        ret.x = x * scalar;
        ret.y = y * scalar;
        ret.z = z * scalar;
        ret.w = w * scalar;
        return ret;
    }

    public ENG_Vector3D mulRet(double scalar, ENG_Vector3D ret) {
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

    public ENG_Vector4D divAsPt(double scalar) {
        double inv = 1.0f / scalar;
        return new ENG_Vector4D(x * inv, y * inv, z * inv, 1.0f);
    }

    public ENG_Vector4D divAsVec(double scalar) {
        double inv = 1.0f / scalar;
        return new ENG_Vector4D(x * inv, y * inv, z * inv, 0.0f);
    }

    public ENG_Vector4D divFull(double scalar) {
        double inv = 1.0f / scalar;
        return new ENG_Vector4D(x * inv, y * inv, z * inv, w * inv);
    }

    public void div(double scalar, ENG_Vector4D ret) {
        double inv = 1.0f / scalar;
        ret.x = x * inv;
        ret.y = y * inv;
        ret.z = z * inv;
    }

    public void div(double scalar, ENG_Vector3D ret) {
        double inv = 1.0f / scalar;
        ret.x = x * inv;
        ret.y = y * inv;
        ret.z = z * inv;
    }

    public void divFull(double scalar, ENG_Vector4D ret) {
        double inv = 1.0f / scalar;
        ret.x = x * inv;
        ret.y = y * inv;
        ret.z = z * inv;
        ret.w = w * inv;
    }

    public ENG_Vector4D divRet(double scalar, ENG_Vector4D ret) {
        double inv = 1.0f / scalar;
        ret.x = x * inv;
        ret.y = y * inv;
        ret.z = z * inv;
        return ret;
    }

    public ENG_Vector4D divRetFull(double scalar, ENG_Vector4D ret) {
        double inv = 1.0f / scalar;
        ret.x = x * inv;
        ret.y = y * inv;
        ret.z = z * inv;
        ret.w = w * inv;
        return ret;
    }

    public ENG_Vector3D divRet(double scalar, ENG_Vector3D ret) {
        double inv = 1.0f / scalar;
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

    public static ENG_Vector4D divInvAsPt(double scalar, ENG_Vector4D vec) {
        return new ENG_Vector4D(scalar / vec.x, scalar / vec.y, scalar / vec.z, 1.0f);
    }

    public static ENG_Vector4D divInvAsVec(double scalar, ENG_Vector4D vec) {
        return new ENG_Vector4D(scalar / vec.x, scalar / vec.y, scalar / vec.z, 0.0f);
    }

    public static ENG_Vector4D divInvAsPt(double scalar, ENG_Vector3D vec) {
        return new ENG_Vector4D(scalar / vec.x, scalar / vec.y, scalar / vec.z, 1.0f);
    }

    public static ENG_Vector4D divInvAsVec(double scalar, ENG_Vector3D vec) {
        return new ENG_Vector4D(scalar / vec.x, scalar / vec.y, scalar / vec.z, 0.0f);
    }

    public static ENG_Vector4D divInvFull(double scalar, ENG_Vector4D vec) {
        return new ENG_Vector4D(scalar / vec.x, scalar / vec.y, scalar / vec.z, scalar / vec.w);
    }

    public static void divInv(double scalar, ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
    }

    public static void divInvFull(double scalar, ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
        ret.w = scalar / vec.w;
    }

    public static void divInv(double scalar, ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
    }

    public static void divInv(double scalar, ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
    }

    public static void divInv(double scalar, ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
    }

    public static ENG_Vector4D divInvRet(double scalar, ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
        return ret;
    }

    public static ENG_Vector4D divInvRetFull(double scalar, ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
        ret.w = scalar / vec.w;
        return ret;
    }

    public static ENG_Vector4D divInvRet(double scalar, ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
        return ret;
    }

    public static ENG_Vector3D divInvRet(double scalar, ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
        return ret;
    }

    public static ENG_Vector3D divInvRet(double scalar, ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
        return ret;
    }

    public static ENG_Vector4D subInvAsPt(double scalar, ENG_Vector4D vec) {
        return new ENG_Vector4D(scalar - vec.x, scalar - vec.y, scalar - vec.z, 1.0f);
    }

    public static ENG_Vector4D subInvAsVec(double scalar, ENG_Vector4D vec) {
        return new ENG_Vector4D(scalar - vec.x, scalar - vec.y, scalar - vec.z, 0.0f);
    }

    public static ENG_Vector4D subInvAsPt(double scalar, ENG_Vector3D vec) {
        return new ENG_Vector4D(scalar - vec.x, scalar - vec.y, scalar - vec.z, 1.0f);
    }

    public static ENG_Vector4D subInvAsVec(double scalar, ENG_Vector3D vec) {
        return new ENG_Vector4D(scalar - vec.x, scalar - vec.y, scalar - vec.z, 0.0f);
    }

    public static ENG_Vector4D subInvFull(double scalar, ENG_Vector4D vec) {
        return new ENG_Vector4D(scalar - vec.x, scalar - vec.y, scalar - vec.z, scalar - vec.w);
    }

    public static void subInv(double scalar, ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
    }

    public static void subInvFull(double scalar, ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
        ret.w = scalar - vec.w;
    }

    public static void subInv(double scalar, ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
    }

    public static void subInv(double scalar, ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
    }

    public static void subInv(double scalar, ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
    }

    public static ENG_Vector4D subInvRet(double scalar, ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
        return ret;
    }

    public static ENG_Vector4D subInvRetFull(double scalar, ENG_Vector4D vec, ENG_Vector4D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
        ret.w = scalar - vec.w;
        return ret;
    }

    public static ENG_Vector4D subInvRet(double scalar, ENG_Vector3D vec, ENG_Vector4D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
        return ret;
    }

    public static ENG_Vector3D subInvRet(double scalar, ENG_Vector4D vec, ENG_Vector3D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
        return ret;
    }

    public static ENG_Vector3D subInvRet(double scalar, ENG_Vector3D vec, ENG_Vector3D ret) {
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

    public void addInPlace(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void addInPlace(double x, double y, double z, double w) {
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

    public void addInPlace(double scalar) {
        x += scalar;
        y += scalar;
        z += scalar;
    }

    public void addInPlaceFull(double scalar) {
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

    public void subInPlace(double scalar) {
        x -= scalar;
        y -= scalar;
        z -= scalar;
    }

    public void subInPlace(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
    }

    public void subInPlace(double x, double y, double z, double w) {
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

    public void mul(double scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
    }

    public void mulFull(double scalar) {
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

    public void divInPlaceFull(double scalar) {
        double inv = 1.0f / scalar;
        x *= inv;
        y *= inv;
        z *= inv;
        w *= inv;
    }

    public void divInPlace(double scalar) {
        double inv = 1.0f / scalar;
        x *= inv;
        y *= inv;
        z *= inv;
    }

    public double length() {
        return ENG_Math.sqrt(x * x + y * y + z * z);
    }

    public double lengthFull() {
        return ENG_Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public static double length(double x, double y, double z) {
        return ENG_Math.sqrt(x * x + y * y + z * z);
    }

    public static double lengthFull(double x, double y, double z, double w) {
        return ENG_Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public static double length(double[] v) {
        return length(v[0], v[1], v[2]);
    }

    public static double lengthFull(double[] v) {
        return lengthFull(v[0], v[1], v[2], v[3]);
    }

    public double squaredLength() {
        return (x * x + y * y + z * z);
    }

    public double squaredLengthFull() {
        return (x * x + y * y + z * z + w * w);
    }

    public static double squaredLength(double x, double y, double z) {
        return (x * x + y * y + z * z);
    }

    public static double squaredLengthFull(double x, double y, double z, double w) {
        return (x * x + y * y + z * z + w * w);
    }

    public double distance(ENG_Vector3D vec) {
        double xDiff = x - vec.x;
        double yDiff = y - vec.y;
        double zDiff = z - vec.z;
        return length(xDiff, yDiff, zDiff);
    }

    public double distance(ENG_Vector4D vec) {
        double xDiff = x - vec.x;
        double yDiff = y - vec.y;
        double zDiff = z - vec.z;
        return length(xDiff, yDiff, zDiff);
    }

    public double distanceFull(ENG_Vector4D vec) {
        double xDiff = x - vec.x;
        double yDiff = y - vec.y;
        double zDiff = z - vec.z;
        double wDiff = w - vec.w;
        return lengthFull(xDiff, yDiff, zDiff, wDiff);
    }

    public double squaredDistance(ENG_Vector4D vec) {
        double xDiff = x - vec.x;
        double yDiff = y - vec.y;
        double zDiff = z - vec.z;
        return squaredLength(xDiff, yDiff, zDiff);
    }

    public double squaredDistanceFull(ENG_Vector4D vec) {
        double xDiff = x - vec.x;
        double yDiff = y - vec.y;
        double zDiff = z - vec.z;
        double wDiff = w - vec.w;
        return squaredLengthFull(xDiff, yDiff, zDiff, wDiff);
    }

    public double squaredDistance(ENG_Vector3D vec) {
        double xDiff = x - vec.x;
        double yDiff = y - vec.y;
        double zDiff = z - vec.z;
        return squaredLength(xDiff, yDiff, zDiff);
    }

    public double dotProduct(ENG_Vector4D vec) {
        return (x * vec.x + y * vec.y + z * vec.z);
    }

    public double dotProductFull(ENG_Vector4D vec) {
        return (x * vec.x + y * vec.y + z * vec.z + w * vec.w);
    }

    public double dotProduct(ENG_Vector3D vec) {
        return (x * vec.x + y * vec.y + z * vec.z);
    }

    public double absDotProduct(ENG_Vector4D vec) {
        return (Math.abs(x * vec.x) + Math.abs(y * vec.y) + Math.abs(z * vec.z));
    }

    public double absDotProductFull(ENG_Vector4D vec) {
        return (Math.abs(x * vec.x) + Math.abs(y * vec.y) + Math.abs(z * vec.z) + Math.abs(w * vec.w));
    }

    public double absDotProduct(ENG_Vector3D vec) {
        return (Math.abs(x * vec.x) + Math.abs(y * vec.y) + Math.abs(z * vec.z));
    }

    public void normalize() {
        double len = this.length();
        if (len > 0.0f) {
            double inv = 1.0f / len;
            x *= inv;
            y *= inv;
            z *= inv;
        }
    }

    public double normalizeRet() {
        double len = this.length();
        if (len > 0.0f) {
            double inv = 1.0f / len;
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
        int xComp = ENG_Double.compareTo(x, vec.x);
        int yComp = ENG_Double.compareTo(y, vec.y);
        int zComp = ENG_Double.compareTo(z, vec.z);

        return (xComp == ENG_Utility.COMPARE_LESS_THAN || xComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (yComp == ENG_Utility.COMPARE_LESS_THAN || yComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (zComp == ENG_Utility.COMPARE_LESS_THAN || zComp == ENG_Utility.COMPARE_EQUAL_TO);
    }

    public boolean compareLessThanOrEqual(ENG_Vector3D vec) {
        int xComp = ENG_Double.compareTo(x, vec.x);
        int yComp = ENG_Double.compareTo(y, vec.y);
        int zComp = ENG_Double.compareTo(z, vec.z);

        return (xComp == ENG_Utility.COMPARE_LESS_THAN || xComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (yComp == ENG_Utility.COMPARE_LESS_THAN || yComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (zComp == ENG_Utility.COMPARE_LESS_THAN || zComp == ENG_Utility.COMPARE_EQUAL_TO);
    }

    public boolean compareLessThan(ENG_Vector4D vec) {
        return ((ENG_Double.compareTo(x, vec.x) == ENG_Utility.COMPARE_LESS_THAN) &&
                (ENG_Double.compareTo(y, vec.y) == ENG_Utility.COMPARE_LESS_THAN) &&
                (ENG_Double.compareTo(z, vec.z) == ENG_Utility.COMPARE_LESS_THAN));
    }

    public boolean compareGreaterThanOrEqual(ENG_Vector4D vec) {
        int xComp = ENG_Double.compareTo(x, vec.x);
        int yComp = ENG_Double.compareTo(y, vec.y);
        int zComp = ENG_Double.compareTo(z, vec.z);

        return (xComp == ENG_Utility.COMPARE_GREATER_THAN || xComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (yComp == ENG_Utility.COMPARE_GREATER_THAN || yComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (zComp == ENG_Utility.COMPARE_GREATER_THAN || zComp == ENG_Utility.COMPARE_EQUAL_TO);
    }

    public boolean compareGreaterThanOrEqual(ENG_Vector3D vec) {
        int xComp = ENG_Double.compareTo(x, vec.x);
        int yComp = ENG_Double.compareTo(y, vec.y);
        int zComp = ENG_Double.compareTo(z, vec.z);

        return (xComp == ENG_Utility.COMPARE_GREATER_THAN || xComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (yComp == ENG_Utility.COMPARE_GREATER_THAN || yComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (zComp == ENG_Utility.COMPARE_GREATER_THAN || zComp == ENG_Utility.COMPARE_EQUAL_TO);
    }

    public boolean compareGreaterThan(ENG_Vector4D vec) {
        return ((ENG_Double.compareTo(x, vec.x) == ENG_Utility.COMPARE_GREATER_THAN) &&
                (ENG_Double.compareTo(y, vec.y) == ENG_Utility.COMPARE_GREATER_THAN) &&
                (ENG_Double.compareTo(z, vec.z) == ENG_Utility.COMPARE_GREATER_THAN));
    }

    public boolean compareLessThan(ENG_Vector3D vec) {
        return ((ENG_Double.compareTo(x, vec.x) == ENG_Utility.COMPARE_LESS_THAN) &&
                (ENG_Double.compareTo(y, vec.y) == ENG_Utility.COMPARE_LESS_THAN) &&
                (ENG_Double.compareTo(z, vec.z) == ENG_Utility.COMPARE_LESS_THAN));
    }

    public boolean compareGreaterThan(ENG_Vector3D vec) {
        return ((ENG_Double.compareTo(x, vec.x) == ENG_Utility.COMPARE_GREATER_THAN) &&
                (ENG_Double.compareTo(y, vec.y) == ENG_Utility.COMPARE_GREATER_THAN) &&
                (ENG_Double.compareTo(z, vec.z) == ENG_Utility.COMPARE_GREATER_THAN));
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

    public ENG_Vector4D randomDeviant(double angle) {
        ENG_Vector4D ret = new ENG_Vector4D();
        randomDeviant(angle, ret);
        return ret;
    }

    public void randomDeviant(double angle, ENG_Vector4D ret) {
        randomDeviant(angle, ENG_Math.VEC4_ZERO, ret);
    }

    public ENG_Vector4D randomDeviantRet(double angle, ENG_Vector4D vec) {
        ENG_Vector4D ret = new ENG_Vector4D();
        randomDeviant(angle, vec, ret);
        return ret;
    }

    public void randomDeviant(double angle, ENG_Vector4D vec, ENG_Vector4D ret) {
        ENG_Vector4D newUp = new ENG_Vector4D();
        if (vec.equals(ENG_Math.VEC4_ZERO)) {
            this.perpendicular(newUp);
        } else {
            newUp.set(vec);
        }


        ENG_Quaternion q = new ENG_Quaternion();
        q.fromAngleAxis(new ENG_Radian(
                ENG_Utility.getRandom().nextDouble() * ENG_Math.TWO_PI), this);
        q.mul(newUp, newUp);

        q.fromAngleAxis(new ENG_Radian(angle), newUp);

        q.mul(this, ret);
    }

    public double angleBetween(ENG_Vector4D vec) {
        double lenProduct = length() * vec.length();

        if (lenProduct < ENG_Math.FLOAT_EPSILON) {
            lenProduct = ENG_Math.FLOAT_EPSILON;
        }

        double f = dotProduct(vec) / lenProduct;
        f = ENG_Math.clamp(f, -1.0f, 1.0f);
        return ENG_Math.acos(f);
    }

    public double angleBetween(ENG_Vector3D vec) {
        double lenProduct = length() * vec.length();

        if (lenProduct < ENG_Math.FLOAT_EPSILON) {
            lenProduct = ENG_Math.FLOAT_EPSILON;
        }

        double f = dotProduct(vec) / lenProduct;
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

        double d = v0.dotProduct(v1);

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
            double s = ENG_Math.sqrt((1.0f + d) * 2.0f);
            double invs = 1.0f / s;

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

    public boolean positionClose(ENG_Vector4D vec, double tolerance) {
        return (squaredDistance(vec) < (squaredLength() + vec.squaredLength()) * tolerance);
    }

    public boolean positionClose(ENG_Vector3D vec, double tolerance) {
        return (squaredDistance(vec) < (squaredLength() + vec.squaredLength()) * tolerance);
    }

    public boolean directionEquals(ENG_Vector4D vec, double tolerance) {
        return (Math.abs(ENG_Math.acos(dotProduct(vec))) <= tolerance);
    }

    public boolean directionEquals(ENG_Vector3D vec, double tolerance) {
        return (Math.abs(ENG_Math.acos(dotProduct(vec))) <= tolerance);
    }

    public boolean isNaNFull() {
        return (Double.isNaN(x) || Double.isNaN(y) || (Double.isNaN(z)) || (Double.isNaN(w)));
    }

    public boolean isNaN() {
        return (Double.isNaN(x) || Double.isNaN(y) || (Double.isNaN(z)));
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

    public void mul(ENG_Matrix4 mat, ENG_Vector4D ret) {
        /*
		 * v.x*mat[0][0] + v.y*mat[1][0] + v.z*mat[2][0] + v.w*mat[3][0],
            v.x*mat[0][1] + v.y*mat[1][1] + v.z*mat[2][1] + v.w*mat[3][1],
            v.x*mat[0][2] + v.y*mat[1][2] + v.z*mat[2][2] + v.w*mat[3][2],
            v.x*mat[0][3] + v.y*mat[1][3] + v.z*mat[2][3] + v.w*mat[3][3]
		 */
        double[] f = mat.get();
        ret.x = x * f[0] + y * f[4] + z * f[8] + w * f[12];
        ret.y = x * f[1] + y * f[5] + z * f[9] + w * f[13];
        ret.z = x * f[2] + y * f[6] + z * f[10] + w * f[14];
        ret.w = x * f[3] + y * f[7] + z * f[11] + w * f[15];
    }

    public ENG_Vector4D mul(ENG_Matrix4 mat) {
        ENG_Vector4D ret = new ENG_Vector4D();
        mul(mat, ret);
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
        return (x + " " + y + " " + z);
    }

    public String toString() {
        return toString(true, ENG_Utility.FORMATTER_DEFAULT);
    }
}
