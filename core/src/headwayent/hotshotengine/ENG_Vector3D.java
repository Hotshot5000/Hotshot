/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 7/19/21, 4:48 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import java.text.NumberFormat;

import headwayent.hotshotengine.basictypes.ENG_Float;

public class ENG_Vector3D {

    public float x, y, z;
//	private static final ENG_Vector3D temp = new ENG_Vector3D();

    public ENG_Vector3D() {

    }

    public ENG_Vector3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ENG_Vector3D(float[] vec) {
        this.x = vec[0];
        this.y = vec[1];
        this.z = vec[2];
    }

    public ENG_Vector3D(float[] vec, int offset) {
        this.x = vec[offset];
        this.y = vec[offset + 1];
        this.z = vec[offset + 2];
    }

    public ENG_Vector3D(ENG_Vector3D vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public ENG_Vector3D(float scalar) {
        this.x = scalar;
        this.y = scalar;
        this.z = scalar;
    }

    public ENG_Vector3D(ENG_Vector4D translate) {
        
        set(translate);
    }

    public ENG_Vector3D(ENG_Vector2D vec) {
        set(vec);
    }

    public void swap(ENG_Vector3D vec) {
        float temp = x;
        x = vec.x;
        vec.x = temp;
        temp = y;
        y = vec.y;
        vec.y = temp;
        temp = z;
        z = vec.z;
        vec.z = temp;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(ENG_Vector4D vec) {
        x = vec.x;
        y = vec.y;
        z = vec.z;
    }

    public void set(ENG_Vector3D vec) {
        x = vec.x;
        y = vec.y;
        z = vec.z;
    }

    public void set(ENG_Vector2D vec) {
        x = vec.x;
        y = vec.y;
    }

    public void set(float scalar) {
        x = scalar;
        y = scalar;
        z = scalar;
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
    }

    public float get(int index) {
        switch (index) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
            default:
                throw new IllegalArgumentException();
        }
    }

    public boolean equalsFast(ENG_Vector3D vec) {
        return ((x == vec.x) && (y == vec.y) && (z == vec.z));
    }

    public boolean notEqualsFast(ENG_Vector3D vec) {
        return ((x != vec.x) || (y != vec.y) || (z != vec.z));
    }

    public boolean equals(ENG_Vector3D vec) {
        return ((ENG_Float.compareTo(x, vec.x) == ENG_Utility.COMPARE_EQUAL_TO) &&
                (ENG_Float.compareTo(y, vec.y) == ENG_Utility.COMPARE_EQUAL_TO) &&
                (ENG_Float.compareTo(z, vec.z) == ENG_Utility.COMPARE_EQUAL_TO));
    }

    public boolean notEquals(ENG_Vector3D vec) {
        return ((ENG_Float.compareTo(x, vec.x) != ENG_Utility.COMPARE_EQUAL_TO) ||
                (ENG_Float.compareTo(y, vec.y) != ENG_Utility.COMPARE_EQUAL_TO) ||
                (ENG_Float.compareTo(z, vec.z) != ENG_Utility.COMPARE_EQUAL_TO));
    }

    public ENG_Vector3D add(ENG_Vector3D vec) {
        return new ENG_Vector3D(x + vec.x, y + vec.y, z + vec.z);
    }

    public void add(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = x + vec.x;
        ret.y = y + vec.y;
        ret.z = z + vec.z;
    }

    public ENG_Vector3D addRet(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = x + vec.x;
        ret.y = y + vec.y;
        ret.z = z + vec.z;
        return ret;
    }

    public ENG_Vector3D sub(ENG_Vector3D vec) {
        return new ENG_Vector3D(x - vec.x, y - vec.y, z - vec.z);
    }

    public void sub(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = x - vec.x;
        ret.y = y - vec.y;
        ret.z = z - vec.z;
    }

    public ENG_Vector3D subRet(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = x - vec.x;
        ret.y = y - vec.y;
        ret.z = z - vec.z;
        return ret;
    }

    /**
     * Notice that this does  not behave like ENG_Vector4D which does everything in place.
     * @param scalar
     * @return
     */
    public ENG_Vector3D mul(float scalar) {
        return new ENG_Vector3D(x * scalar, y * scalar, z * scalar);
    }

    public void mul(float scalar, ENG_Vector3D ret) {
        ret.x = x * scalar;
        ret.y = y * scalar;
        ret.z = z * scalar;
    }

    public ENG_Vector3D mulRet(float scalar, ENG_Vector3D ret) {
        ret.x = x * scalar;
        ret.y = y * scalar;
        ret.z = z * scalar;
        return ret;
    }

    public ENG_Vector3D mul(ENG_Vector3D vec) {
        return new ENG_Vector3D(x * vec.x, y * vec.y, z * vec.z);
    }

    public void mul(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = x * vec.x;
        ret.y = y * vec.y;
        ret.z = z * vec.z;
    }

    public ENG_Vector3D mulRet(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = x * vec.x;
        ret.y = y * vec.y;
        ret.z = z * vec.z;
        return ret;
    }

    public ENG_Vector3D div(float scalar) {
        float inv = 1.0f / scalar;
        return new ENG_Vector3D(x * inv, y * inv, z * inv);
    }

    public void div(float scalar, ENG_Vector3D ret) {
        float inv = 1.0f / scalar;
        ret.x = x * inv;
        ret.y = y * inv;
        ret.z = z * inv;
    }

    public ENG_Vector3D divRet(float scalar, ENG_Vector3D ret) {
        float inv = 1.0f / scalar;
        ret.x = x * inv;
        ret.y = y * inv;
        ret.z = z * inv;
        return ret;
    }

    public ENG_Vector3D div(ENG_Vector3D vec) {
        return new ENG_Vector3D(x / vec.x, y / vec.y, z / vec.z);
    }

    public void div(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = x / vec.x;
        ret.y = y / vec.y;
        ret.z = z / vec.z;
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

    public ENG_Vector3D invert() {
        return new ENG_Vector3D(-x, -y, -z);
    }

    public void invert(ENG_Vector3D ret) {
        ret.x = -x;
        ret.y = -y;
        ret.z = -z;
    }

    public ENG_Vector3D invertRet(ENG_Vector3D ret) {
        ret.x = -x;
        ret.y = -y;
        ret.z = -z;
        return ret;
    }

    public static ENG_Vector3D divInv(float scalar, ENG_Vector3D vec) {
        return new ENG_Vector3D(scalar / vec.x, scalar / vec.y, scalar / vec.z);
    }

    public static void divInv(float scalar, ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
    }

    public static ENG_Vector3D divInvRet(float scalar, ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        ret.z = scalar / vec.z;
        return ret;
    }

    public static ENG_Vector3D subInv(float scalar, ENG_Vector3D vec) {
        return new ENG_Vector3D(scalar - vec.x, scalar - vec.y, scalar - vec.z);
    }

    public static void subInv(float scalar, ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
    }

    public static ENG_Vector3D subInvRet(float scalar, ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        ret.z = scalar - vec.z;
        return ret;
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

    public void addInPlace(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void subInPlace(float x, float y, float z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
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

    public void mulInPlace(ENG_Vector3D vec) {
        x *= vec.x;
        y *= vec.y;
        z *= vec.z;
    }

    public void mulInPlace(float scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
    }

    public void divInPlace(ENG_Vector3D vec) {
        x /= vec.x;
        y /= vec.y;
        z /= vec.z;
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

    public static float length(float x, float y, float z) {
        return ENG_Math.sqrt(x * x + y * y + z * z);
    }

    public static float length(float[] v) {
        return length(v[0], v[1], v[2]);
    }

    public float squaredLength() {
        return (x * x + y * y + z * z);
    }

    public static float squaredLength(float x, float y, float z) {
        return (x * x + y * y + z * z);
    }

    public float distance(ENG_Vector3D vec) {
        float xDiff = x - vec.x;
        float yDiff = y - vec.y;
        float zDiff = z - vec.z;
        return length(xDiff, yDiff, zDiff);
    }

    public float squaredDistance(ENG_Vector3D vec) {
        float xDiff = x - vec.x;
        float yDiff = y - vec.y;
        float zDiff = z - vec.z;
        return squaredLength(xDiff, yDiff, zDiff);
    }

    public float dotProduct(ENG_Vector3D vec) {
        return (x * vec.x + y * vec.y + z * vec.z);
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

    public ENG_Vector3D midPoint(ENG_Vector3D vec) {
        return new ENG_Vector3D((x + vec.x) * 0.5f, (y + vec.y) * 0.5f, (z + vec.z) * 0.5f);
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

        return !((xComp != ENG_Utility.COMPARE_LESS_THAN && xComp != ENG_Utility.COMPARE_EQUAL_TO) ||
                (yComp != ENG_Utility.COMPARE_LESS_THAN && yComp != ENG_Utility.COMPARE_EQUAL_TO) ||
                (zComp != ENG_Utility.COMPARE_LESS_THAN && zComp != ENG_Utility.COMPARE_EQUAL_TO));
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

    public ENG_Vector3D makeAbsRet() {
        ENG_Vector3D ret = new ENG_Vector3D();
        makeAbs(ret);
        return ret;
    }

    public void makeAbs(ENG_Vector3D ret) {
        ret.x = Math.abs(x);
        ret.y = Math.abs(y);
        ret.z = Math.abs(z);
    }

    public void makeAbs() {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
    }

    public void crossProduct(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = y * vec.z - z * vec.y;
        ret.y = z * vec.x - x * vec.z;
        ret.z = x * vec.y - y * vec.x;
    }

    public ENG_Vector3D crossProductRet(ENG_Vector3D vec, ENG_Vector3D ret) {
        ret.x = y * vec.z - z * vec.y;
        ret.y = z * vec.x - x * vec.z;
        ret.z = x * vec.y - y * vec.x;
        return ret;
    }

    public ENG_Vector3D crossProduct(ENG_Vector3D vec) {
        return new ENG_Vector3D(y * vec.z - z * vec.y,
                z * vec.x - x * vec.z,
                x * vec.y - y * vec.x);
    }

    public void perpendicular(ENG_Vector3D ret) {

        this.crossProduct(ENG_Math.VEC3_X_UNIT, ret);
        if (ret.squaredLength() < ENG_Math.FLOAT_EPSILON_SQUARED) {
            this.crossProduct(ENG_Math.VEC3_Y_UNIT, ret);
        }
        ret.normalize();

    }

    public ENG_Vector3D perpendicularRet(ENG_Vector3D ret) {

        this.crossProduct(ENG_Math.VEC3_X_UNIT, ret);
        if (ret.squaredLength() < ENG_Math.FLOAT_EPSILON_SQUARED) {
            this.crossProduct(ENG_Math.VEC3_Y_UNIT, ret);
        }
        ret.normalize();
        return ret;
    }

    public ENG_Vector3D perpendicular() {
        return perpendicularRet(new ENG_Vector3D());
    }

    public ENG_Vector3D randomDeviant(float angle) {
        ENG_Vector3D ret = new ENG_Vector3D();
        randomDeviant(angle, ret);
        return ret;
    }

    public void randomDeviant(float angle, ENG_Vector3D ret) {
        randomDeviant(angle, ENG_Math.VEC3_ZERO, ret);
    }

    public ENG_Vector3D randomDeviantRet(float angle, ENG_Vector3D vec) {
        ENG_Vector3D ret = new ENG_Vector3D();
        randomDeviant(angle, vec, ret);
        return ret;
    }

    public void randomDeviant(float angle, ENG_Vector3D vec, ENG_Vector3D ret) {
        ENG_Vector3D newUp = new ENG_Vector3D();
        if (vec.equals(ENG_Math.VEC3_ZERO)) {
            this.perpendicular(newUp);
        } else {
            newUp.set(vec);
        }


        ENG_Quaternion q = new ENG_Quaternion();
        q.fromAngleAxis(new ENG_Radian(
                        ENG_Utility.getRandom().nextFloat() * ENG_Math.TWO_PI),
                new ENG_Vector4D(this));
        q.mul(newUp, newUp);

        q.fromAngleAxis(new ENG_Radian(angle), new ENG_Vector4D(newUp));

        q.mul(this, ret);
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

    public void getRotationTo() {

    }

    public boolean isZeroLength() {
        return (squaredLength() < ENG_Math.FLOAT_EPSILON_SQUARED);
    }

    public void normalizedCopy(ENG_Vector3D ret) {
        ret.set(this);
        ret.normalize();
    }

    public ENG_Vector3D normalizedCopy() {
        ENG_Vector3D ret = new ENG_Vector3D();
        normalizedCopy(ret);
        return ret;
    }

    public void reflect(ENG_Vector3D normal, ENG_Vector3D ret) {
        sub(normal.mulRet(2.0f * this.dotProduct(normal), ret), ret);
    }

//    public ENG_Vector3D reflect(ENG_Vector3D normal) {
//        ENG_Vector3D vec = normal.mul(2.0f * this.dotProduct(normal));
//        return this.sub(vec);
//    }

    public boolean positionClose(ENG_Vector3D vec, float tolerance) {
        return (squaredDistance(vec) < (squaredLength() + vec.squaredLength()) * tolerance);
    }

    public boolean directionEquals(ENG_Vector3D vec, float tolerance) {
        return (Math.abs(ENG_Math.acos(dotProduct(vec))) <= tolerance);
    }

    public boolean isNaN() {
        return (Float.isNaN(x) || Float.isNaN(y) || (Float.isNaN(z)));
    }

    public void lerpInPlace(final ENG_Vector3D target, float alpha) {
        x += alpha * (target.x - x);
        y += alpha * (target.y - y);
        z += alpha * (target.z - z);
    }

    public ENG_Vector3D lerp(final ENG_Vector3D target, float alpha) {
        ENG_Vector3D ret = new ENG_Vector3D();
        lerp(target, alpha, ret);
        return ret;
    }

    public void lerp(final ENG_Vector3D target, float alpha, ENG_Vector3D ret) {
        ret.x += alpha * (target.x - x);
        ret.y += alpha * (target.y - y);
        ret.z += alpha * (target.z - z);
    }

    public void getAsVector4D(ENG_Vector4D ret) {
        ret.set(this);
    }

    public ENG_Vector4D getAsVector4D() {
        return getAsVector4D(false);
    }

    public ENG_Vector4D getAsVector4D(boolean asPt) {
        ENG_Vector4D ret = new ENG_Vector4D(asPt);
        getAsVector4D(ret);
        return ret;
    }

    public String toString(boolean format, NumberFormat formatter) {
        return ("Vector3(" + formatter.format(x) + ", " + formatter.format(y) + ", " +
                formatter.format(z) + ")");
    }

    public String toString() {
        return toString(true, ENG_Utility.FORMATTER_DEFAULT);
    }

}
