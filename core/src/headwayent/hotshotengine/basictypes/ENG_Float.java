/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:08 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.basictypes;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_NumberOperations;
import headwayent.hotshotengine.ENG_Utility;

public class ENG_Float implements Comparable<ENG_Float>,
        ENG_NumberOperations<ENG_Float> {

    public static final int SIZE_IN_BYTES = Float.SIZE / ENG_Utility.BYTE_SIZE;
    public static final float FLOAT_EPSILON = ENG_Math.FLOAT_EPSILON;

    private float value;

    public ENG_Float() {

    }

    public ENG_Float(float f) {
        value = f;
    }

    public ENG_Float(ENG_Float f) {
        value = f.getValue();
    }

    public ENG_Float(Float f) {
        value = f;
    }

    public ENG_Float(String f) {
        value = Float.parseFloat(f);
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public void setValue(ENG_Float value) {
        this.value = value.getValue();
    }

    public void setValue(String value) {
        this.value = Float.parseFloat(value);
    }

    public float getValue() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }

    public ENG_Float clone() {
        return new ENG_Float(value);
    }

    public boolean equals(Object obj) {
        if (obj instanceof ENG_Float) {
            float res = value - ((ENG_Float) obj).getValue();
            return (res > -FLOAT_EPSILON) && (res < FLOAT_EPSILON);
        }
        if (obj instanceof Float) {
            float res = value - (Float) obj;
            return (res > -FLOAT_EPSILON) && (res < FLOAT_EPSILON);
        }
        throw new IllegalArgumentException();
    }

    public int hashCode() {
        return (int) value;
    }

    public static int compareTo(float f1, float f2, float tolerance) {
        float res = f1 - f2;
        if ((res < -tolerance) || (res > tolerance)) {
            if (res < 0.0f) {
                return ENG_Utility.COMPARE_LESS_THAN;
            } else {
                return ENG_Utility.COMPARE_GREATER_THAN;
            }
        }
        return ENG_Utility.COMPARE_EQUAL_TO;
    }

    public static int compareTo(float f1, float f2) {
        float res = f1 - f2;
        if ((res < -FLOAT_EPSILON) || (res > FLOAT_EPSILON)) {
            if (res < 0.0f) {
                return ENG_Utility.COMPARE_LESS_THAN;
            } else {
                return ENG_Utility.COMPARE_GREATER_THAN;
            }
        }
        return ENG_Utility.COMPARE_EQUAL_TO;
    }

    public static boolean isEqual(float f1, float f2) {
        return isEqual(f1, f2, FLOAT_EPSILON);
    }

    public static boolean isEqual(float f1, float f2, float tolerance) {
        return Math.abs(f1 - f2) < tolerance;
    }

    public int compareTo(float f) {
        return compareTo(value, f);
    }

    public int compareTo(Float f) {
        return compareTo(f.floatValue());
    }

    public int compareTo(ENG_Float f) {
        return compareTo(f.getValue());
    }

    public static ENG_Float valueOf(String f) {
        return new ENG_Float(f);
    }

    public static ENG_Float valueOf(float f) {
        return new ENG_Float(f);
    }

    public static ENG_Float valueOf(Float f) {
        return new ENG_Float(f);
    }

    public static ENG_Float[] createArray(int len) {
        ENG_Float[] arr = new ENG_Float[len];
        for (int i = 0; i < len; ++i) {
            arr[i] = new ENG_Float();
        }
        return arr;
    }

    public static ENG_Float[] createArray(float[] a, int offset, int len) {
        ENG_Float[] arr = new ENG_Float[len];
        for (int i = 0; i < len; ++i) {
            arr[i] = new ENG_Float(a[offset + i]);
        }
        return arr;
    }

    @Override
    public ENG_Float add(ENG_NumberOperations<ENG_Float> oth) {
        
        return new ENG_Float(value + oth.get().getValue());
    }

    @Override
    public ENG_Float sub(ENG_NumberOperations<ENG_Float> oth) {
        
        return new ENG_Float(value - oth.get().getValue());
    }

    @Override
    public ENG_Float mul(ENG_NumberOperations<ENG_Float> oth) {
        
        return new ENG_Float(value * oth.get().getValue());
    }

    @Override
    public ENG_Float div(ENG_NumberOperations<ENG_Float> oth) {
        
        return new ENG_Float(value / oth.get().getValue());
    }

    @Override
    public ENG_Float mul(float oth) {
        
        return new ENG_Float(value * oth);
    }

    @Override
    public ENG_Float get() {
        
        return this;
    }

    @Override
    public void set(ENG_Float val) {
        
        setValue(val);
    }

    public void add(ENG_Float b, ENG_Float ret) {
        ret.setValue(value + b.getValue());
    }

    public ENG_Float add(ENG_Float b) {
        ENG_Float ret = new ENG_Float();
        add(b, ret);
        return ret;
    }

    public void addInPlace(ENG_Float b) {
        value += b.getValue();
    }

    public void sub(ENG_Float b, ENG_Float ret) {
        ret.setValue(value - b.getValue());
    }

    public ENG_Float sub(ENG_Float b) {
        ENG_Float ret = new ENG_Float();
        sub(b, ret);
        return ret;
    }

    public void subInPlace(ENG_Float b) {
        value -= b.getValue();
    }

    public void mul(ENG_Float b, ENG_Float ret) {
        ret.setValue(value * b.getValue());
    }

    public ENG_Float mul(ENG_Float b) {
        ENG_Float ret = new ENG_Float();
        mul(b, ret);
        return ret;
    }

    public void mulInPlace(ENG_Float b) {
        value *= b.getValue();
    }

    public void div(ENG_Float b, ENG_Float ret) {
        ret.setValue(value / b.getValue());
    }

    public ENG_Float div(ENG_Float b) {
        ENG_Float ret = new ENG_Float();
        div(b, ret);
        return ret;
    }

    public void divInPlace(ENG_Float b) {
        value /= b.getValue();
    }

    public void add(float b, ENG_Float ret) {
        ret.setValue(value + b);
    }

    public ENG_Float add(float b) {
        ENG_Float ret = new ENG_Float();
        add(b, ret);
        return ret;
    }

    public void addInPlace(float b) {
        value += b;
    }

    public void sub(float b, ENG_Float ret) {
        ret.setValue(value - b);
    }

    public ENG_Float sub(float b) {
        ENG_Float ret = new ENG_Float();
        sub(b, ret);
        return ret;
    }

    public void subInPlace(float b) {
        value -= b;
    }

    public void mul(float b, ENG_Float ret) {
        ret.setValue(value * b);
    }

//    public ENG_Float mul(float b) {
//        ENG_Float ret = new ENG_Float();
//        mul(b, ret);
//        return ret;
//    }

    public void mulInPlace(float b) {
        value *= b;
    }

    public void div(float b, ENG_Float ret) {
        ret.setValue(value / b);
    }

    public ENG_Float div(float b) {
        ENG_Float ret = new ENG_Float();
        div(b, ret);
        return ret;
    }

    public void divInPlace(float b) {
        value /= b;
    }
}
