/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/21/20, 9:53 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import java.text.NumberFormat;

import headwayent.hotshotengine.basictypes.ENG_Float;

public class ENG_Vector2D {

    public float x, y;

    public ENG_Vector2D() {

    }

    public ENG_Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public ENG_Vector2D(float[] vec) {
        this.x = vec[0];
        this.y = vec[1];
    }

    public ENG_Vector2D(float[] vec, int offset) {
        this.x = vec[offset];
        this.y = vec[offset + 1];
    }

    public ENG_Vector2D(ENG_Vector2D vec) {
        this.x = vec.x;
        this.y = vec.y;
    }

    public ENG_Vector2D(ENG_Vector3D vec) {
        set(vec);
    }

    public ENG_Vector2D(ENG_Vector4D vec) {
        set(vec);
    }

    public ENG_Vector2D(float scalar) {
        this.x = scalar;
        this.y = scalar;
    }

    public void swap(ENG_Vector2D vec) {
        float temp = x;
        x = vec.x;
        vec.x = temp;
        temp = y;
        y = vec.y;
        vec.y = temp;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(ENG_Vector2D vec) {
        x = vec.x;
        y = vec.y;
    }

    public void set(ENG_Vector3D vec) {
        x = vec.x;
        y = vec.y;
    }

    public void set(ENG_Vector4D vec) {
        x = vec.x;
        y = vec.y;
    }

    public void set(float scalar) {
        x = scalar;
        y = scalar;
    }

    public void get(ENG_Vector2D vec) {
        vec.x = x;
        vec.y = y;
    }

    public void get(ENG_Vector3D vec) {
        vec.x = x;
        vec.y = y;
    }

    public void get(ENG_Vector4D vec) {
        vec.x = x;
        vec.y = y;
    }

    public float angleBetween(ENG_Vector2D vec) {
        float lenProduct = length() * vec.length();

        if (lenProduct < ENG_Math.FLOAT_EPSILON) {
            lenProduct = ENG_Math.FLOAT_EPSILON;
        }

        float f = dotProduct(vec) / lenProduct;
        f = ENG_Math.clamp(f, -1.0f, 1.0f);
        return ENG_Math.acos(f);
    }

    public boolean equalsFast(ENG_Vector2D vec) {
        return ((x == vec.x) && (y == vec.y));
    }

    public boolean notEqualsFast(ENG_Vector2D vec) {
        return ((x != vec.x) || (y != vec.y));
    }

    public boolean equals(ENG_Vector2D vec) {
        return ((ENG_Float.compareTo(x, vec.x) == ENG_Utility.COMPARE_EQUAL_TO) &&
                (ENG_Float.compareTo(y, vec.y) == ENG_Utility.COMPARE_EQUAL_TO));
    }

    public boolean notEquals(ENG_Vector2D vec) {
        return ((ENG_Float.compareTo(x, vec.x) != ENG_Utility.COMPARE_EQUAL_TO) ||
                (ENG_Float.compareTo(y, vec.y) != ENG_Utility.COMPARE_EQUAL_TO));
    }

    public ENG_Vector2D add(ENG_Vector2D vec) {
        return new ENG_Vector2D(x + vec.x, y + vec.y);
    }

    public void add(ENG_Vector2D vec, ENG_Vector2D ret) {
        ret.x = x + vec.x;
        ret.y = y + vec.y;
    }

    public ENG_Vector2D addRet(ENG_Vector2D vec, ENG_Vector2D ret) {
        ret.x = x + vec.x;
        ret.y = y + vec.y;
        return ret;
    }

    public ENG_Vector2D sub(ENG_Vector2D vec) {
        return new ENG_Vector2D(x - vec.x, y - vec.y);
    }

    public void sub(ENG_Vector2D vec, ENG_Vector2D ret) {
        ret.x = x - vec.x;
        ret.y = y - vec.y;
    }

    public ENG_Vector2D subRet(ENG_Vector2D vec, ENG_Vector2D ret) {
        ret.x = x - vec.x;
        ret.y = y - vec.y;
        return ret;
    }

    public ENG_Vector2D mul(float scalar) {
        return new ENG_Vector2D(x * scalar, y * scalar);
    }

    public void mul(float scalar, ENG_Vector2D ret) {
        ret.x = x * scalar;
        ret.y = y * scalar;
    }

    public ENG_Vector2D mulRet(float scalar, ENG_Vector2D ret) {
        ret.x = x * scalar;
        ret.y = y * scalar;
        return ret;
    }

    public ENG_Vector2D mul(ENG_Vector2D vec) {
        return new ENG_Vector2D(x * vec.x, y * vec.y);
    }

    public void mul(ENG_Vector2D vec, ENG_Vector2D ret) {
        ret.x = x * vec.x;
        ret.y = y * vec.y;
    }

    public ENG_Vector2D mulRet(ENG_Vector2D vec, ENG_Vector2D ret) {
        ret.x = x * vec.x;
        ret.y = y * vec.y;
        return ret;
    }

    public ENG_Vector2D div(float scalar) {
        float inv = 1.0f / scalar;
        return new ENG_Vector2D(x * inv, y * inv);
    }

    public void div(float scalar, ENG_Vector2D ret) {
        float inv = 1.0f / scalar;
        ret.x = x * inv;
        ret.y = y * inv;
    }

    public ENG_Vector2D divRet(float scalar, ENG_Vector2D ret) {
        float inv = 1.0f / scalar;
        ret.x = x * inv;
        ret.y = y * inv;
        return ret;
    }

    public ENG_Vector2D div(ENG_Vector2D vec) {
        return new ENG_Vector2D(x / vec.x, y / vec.y);
    }

    public void div(ENG_Vector2D vec, ENG_Vector2D ret) {
        ret.x = x / vec.x;
        ret.y = y / vec.y;
    }

    public ENG_Vector2D divRet(ENG_Vector2D vec, ENG_Vector2D ret) {
        ret.x = x / vec.x;
        ret.y = y / vec.y;
        return ret;
    }

    public void invertInPlace() {
        x = -x;
        y = -y;
    }

    public ENG_Vector2D invert() {
        return new ENG_Vector2D(-x, -y);
    }

    public void invert(ENG_Vector2D ret) {
        ret.x = -x;
        ret.y = -y;
    }

    public ENG_Vector2D invertRet(ENG_Vector2D ret) {
        ret.x = -x;
        ret.y = -y;
        return ret;
    }

//	public static ENG_Vector2D mul(float scalar, ENG_Vector2D vec) {
//		return new ENG_Vector2D(scalar * vec.x, scalar * vec.y);
//	}

    public static ENG_Vector2D divInv(float scalar, ENG_Vector2D vec) {
        return new ENG_Vector2D(scalar / vec.x, scalar / vec.y);
    }

    public static void divInv(float scalar, ENG_Vector2D vec, ENG_Vector2D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
    }

    public static ENG_Vector2D divInvRet(float scalar, ENG_Vector2D vec, ENG_Vector2D ret) {
        ret.x = scalar / vec.x;
        ret.y = scalar / vec.y;
        return ret;
    }

//	public static ENG_Vector2D add(float scalar, ENG_Vector2D vec) {
//		return new ENG_Vector2D(scalar + vec.x, scalar + vec.y);
//	}

//	public static ENG_Vector2D sub(ENG_Vector2D vec, float scalar) {
//		return new ENG_Vector2D(vec.x - scalar, vec.y - scalar);
//	}

    public static ENG_Vector2D subInv(float scalar, ENG_Vector2D vec) {
        return new ENG_Vector2D(scalar - vec.x, scalar - vec.y);
    }

    public static void subInv(float scalar, ENG_Vector2D vec, ENG_Vector2D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
    }

    public static ENG_Vector2D subInvRet(float scalar, ENG_Vector2D vec, ENG_Vector2D ret) {
        ret.x = scalar - vec.x;
        ret.y = scalar - vec.y;
        return ret;
    }

    public void addInPlace(ENG_Vector2D vec) {
        x += vec.x;
        y += vec.y;
    }

    public void addInPlace(float scalar) {
        x += scalar;
        y += scalar;
    }

    public void subInPlace(ENG_Vector2D vec) {
        x -= vec.x;
        y -= vec.y;
    }

    public void subInPlace(float scalar) {
        x -= scalar;
        y -= scalar;
    }

    public void mulInPlace(ENG_Vector2D vec) {
        x *= vec.x;
        y *= vec.y;
    }

    public void mulInPlace(float scalar) {
        x *= scalar;
        y *= scalar;
    }

    public void divInPlace(ENG_Vector2D vec) {
        x /= vec.x;
        y /= vec.y;
    }

    public void divInPlace(float scalar) {
        float inv = 1.0f / scalar;
        x *= inv;
        y *= inv;
    }

    public float length() {
        return ENG_Math.sqrt(x * x + y * y);
    }

    public static float length(float x, float y) {
        return ENG_Math.sqrt(x * x + y * y);
    }

    public static float length(float[] v) {
        return length(v[0], v[1]);
    }

    public float squaredLength() {
        return (x * x + y * y);
    }

    public static float squaredLength(float x, float y) {
        return (x * x + y * y);
    }

    public float distance(ENG_Vector2D vec) {
        float xDiff = x - vec.x;
        float yDiff = y - vec.y;
        return length(xDiff, yDiff);
    }

    public float squaredDistance(ENG_Vector2D vec) {
        float xDiff = x - vec.x;
        float yDiff = y - vec.y;
        return squaredLength(xDiff, yDiff);
    }

    public float dotProduct(ENG_Vector2D vec) {
        return (x * vec.x + y * vec.y);
    }

    public void normalize() {
        float len = this.length();
        if (len > 0.0f) {
            float inv = 1.0f / len;
            x *= inv;
            y *= inv;

        }
    }

    public float normalizeRet() {
        float len = this.length();
        if (len > 0.0f) {
            float inv = 1.0f / len;
            x *= inv;
            y *= inv;

        }
        return len;
    }

    public ENG_Vector2D midPoint(ENG_Vector2D vec) {
        return new ENG_Vector2D((x + vec.x) * 0.5f, (y + vec.y) * 0.5f);
    }

    public void midPoint(ENG_Vector2D vec, ENG_Vector2D ret) {
        ret.x = (x + vec.x) * 0.5f;
        ret.y = (y + vec.y) * 0.5f;
    }

    public boolean compareLessThan(ENG_Vector2D vec) {
        return ((ENG_Float.compareTo(x, vec.x) == ENG_Utility.COMPARE_LESS_THAN) &&
                (ENG_Float.compareTo(y, vec.y) == ENG_Utility.COMPARE_LESS_THAN));
    }

    public boolean compareGreaterThan(ENG_Vector2D vec) {
        return ((ENG_Float.compareTo(x, vec.x) == ENG_Utility.COMPARE_GREATER_THAN) &&
                (ENG_Float.compareTo(y, vec.y) == ENG_Utility.COMPARE_GREATER_THAN));
    }

    public boolean compareLessThanOrEqual(ENG_Vector4D vec) {
        int xComp = ENG_Float.compareTo(x, vec.x);
        int yComp = ENG_Float.compareTo(y, vec.y);

        return (xComp == ENG_Utility.COMPARE_LESS_THAN || xComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (yComp == ENG_Utility.COMPARE_LESS_THAN || yComp == ENG_Utility.COMPARE_EQUAL_TO);
    }

    public boolean compareLessThanOrEqual(ENG_Vector3D vec) {
        int xComp = ENG_Float.compareTo(x, vec.x);
        int yComp = ENG_Float.compareTo(y, vec.y);

        return (xComp == ENG_Utility.COMPARE_LESS_THAN || xComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (yComp == ENG_Utility.COMPARE_LESS_THAN || yComp == ENG_Utility.COMPARE_EQUAL_TO);
    }

    public boolean compareLessThan(ENG_Vector4D vec) {
        return ((ENG_Float.compareTo(x, vec.x) == ENG_Utility.COMPARE_LESS_THAN) &&
                (ENG_Float.compareTo(y, vec.y) == ENG_Utility.COMPARE_LESS_THAN));
    }

    public boolean compareGreaterThanOrEqual(ENG_Vector4D vec) {
        int xComp = ENG_Float.compareTo(x, vec.x);
        int yComp = ENG_Float.compareTo(y, vec.y);

        return (xComp == ENG_Utility.COMPARE_GREATER_THAN || xComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (yComp == ENG_Utility.COMPARE_GREATER_THAN || yComp == ENG_Utility.COMPARE_EQUAL_TO);
    }

    public boolean compareGreaterThanOrEqual(ENG_Vector3D vec) {
        int xComp = ENG_Float.compareTo(x, vec.x);
        int yComp = ENG_Float.compareTo(y, vec.y);


        return (xComp == ENG_Utility.COMPARE_GREATER_THAN || xComp == ENG_Utility.COMPARE_EQUAL_TO) &&
                (yComp == ENG_Utility.COMPARE_GREATER_THAN || yComp == ENG_Utility.COMPARE_EQUAL_TO);
    }

    public boolean compareGreaterThan(ENG_Vector4D vec) {
        return ((ENG_Float.compareTo(x, vec.x) == ENG_Utility.COMPARE_GREATER_THAN) &&
                (ENG_Float.compareTo(y, vec.y) == ENG_Utility.COMPARE_GREATER_THAN));
    }

    public void makeFloor(ENG_Vector2D vec) {
        if (vec.x < x) {
            x = vec.x;
        }
        if (vec.y < y) {
            y = vec.y;
        }
    }

    public void makeCeil(ENG_Vector2D vec) {
        if (vec.x > x) {
            x = vec.x;
        }
        if (vec.y > y) {
            y = vec.y;
        }
    }

    public ENG_Vector2D makeAbsRet() {
        ENG_Vector2D ret = new ENG_Vector2D();
        makeAbs(ret);
        return ret;
    }

    public void makeAbs(ENG_Vector2D ret) {
        ret.x = Math.abs(x);
        ret.y = Math.abs(y);
    }

    public void makeAbs() {
        x = Math.abs(x);
        y = Math.abs(y);
    }

    /** @noinspection SuspiciousNameCombination*/
    public ENG_Vector2D perpendicular() {
        return new ENG_Vector2D(-y, x);
    }

    /** @noinspection SuspiciousNameCombination*/
    public void perpendicular(ENG_Vector2D ret) {
        ret.x = -y;
        ret.y = x;
    }

    public float crossProduct(ENG_Vector2D vec) {
        return (x * vec.y - y * vec.x);
    }

    public ENG_Vector2D randomDeviant(float angle) {
        angle *= ENG_Utility.getRandom().nextFloat() * ENG_Math.TWO_PI;
        float cosa = ENG_Math.cos(angle);
        float sina = ENG_Math.sin(angle);
        return new ENG_Vector2D(cosa * x - sina * y, sina * x + cosa * y);
    }

    public boolean isZeroLength() {
        return (this.squaredLength() < (ENG_Math.FLOAT_EPSILON * ENG_Math.FLOAT_EPSILON));
    }

    public ENG_Vector2D normalizedCopy() {
        ENG_Vector2D vec = new ENG_Vector2D(this);
        vec.normalize();
        return vec;
    }

    public void normalizedCopy(ENG_Vector2D ret) {
        ret.set(this);
        ret.normalize();
    }

    public void reflect(ENG_Vector2D normal, ENG_Vector2D ret) {
        sub(normal.mulRet(2.0f * this.dotProduct(normal), ret), ret);
    }

    public ENG_Vector2D reflect(ENG_Vector2D normal) {
        ENG_Vector2D vec = normal.mul(2.0f * this.dotProduct(normal));
        return this.sub(vec);
    }

    public boolean positionClose(ENG_Vector2D vec, float tolerance) {
        return (squaredDistance(vec) < (squaredLength() + vec.squaredLength()) * tolerance);
    }

    public boolean directionEquals(ENG_Vector2D vec, float tolerance) {
        return (Math.abs(ENG_Math.acos(dotProduct(vec))) <= tolerance);
    }

    public boolean isNaN() {
        return (Float.isNaN(x) || Float.isNaN(y));
    }

    public void lerpInPlace(final ENG_Vector2D target, float alpha) {
        x += alpha * (target.x - x);
        y += alpha * (target.y - y);
    }

    public ENG_Vector2D lerp(final ENG_Vector2D target, float alpha) {
        ENG_Vector2D ret = new ENG_Vector2D();
        lerp(target, alpha, ret);
        return ret;
    }

    public void lerp(final ENG_Vector2D target, float alpha, ENG_Vector2D ret) {
        ret.x += alpha * (target.x - x);
        ret.y += alpha * (target.y - y);
    }

    public String toString(boolean format, NumberFormat formatter) {
        return ("Vector3(" + formatter.format(x) + ", " + formatter.format(y) + ")");
    }

    public String toString() {
        return toString(true, ENG_Utility.FORMATTER_DEFAULT);
    }
}
