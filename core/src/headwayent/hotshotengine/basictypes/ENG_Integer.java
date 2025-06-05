/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:08 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.basictypes;

import headwayent.hotshotengine.ENG_Utility;

public class ENG_Integer implements Comparable<ENG_Integer> {

    public static final int SIZE_IN_BYTES = Integer.SIZE / ENG_Utility.BYTE_SIZE;
    private int value;

    public ENG_Integer() {

    }

    public ENG_Integer(int i) {
        value = i;
    }

    public ENG_Integer(ENG_Integer i) {
        value = i.getValue();
    }

    public ENG_Integer(Integer i) {
        value = i;
    }

    public ENG_Integer(String i) {
        value = Integer.parseInt(i);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setValue(ENG_Integer value) {
        this.value = value.getValue();
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = Integer.parseInt(value);
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }

    public ENG_Integer clone() {
        return new ENG_Integer(value);
    }

    public boolean equals(Object obj) {
        if (obj instanceof ENG_Integer) {
            return value == ((ENG_Integer) obj).getValue();
        }
        if (obj instanceof Integer) {
            return value == (Integer) obj;
        }
        throw new IllegalArgumentException();
    }

    public int hashCode() {
        return value;
    }

    public int compareTo(int i) {
        return Integer.compare(value, i);
    }

    public int compareTo(ENG_Integer i) {
        return compareTo(i.getValue());
    }

    public int compareTo(Integer i) {
        return compareTo(i.intValue());
    }

    public static ENG_Integer valueOf(String i) {
        return new ENG_Integer(i);
    }

    public static ENG_Integer valueOf(int i) {
        return new ENG_Integer(i);
    }

    public static ENG_Integer valueOf(Integer i) {
        return new ENG_Integer(i);
    }

    public static ENG_Integer[] createArray(int len) {
        ENG_Integer[] arr = new ENG_Integer[len];
        for (int i = 0; i < len; ++i) {
            arr[i] = new ENG_Integer();
        }
        return arr;
    }

    public static ENG_Integer[] createArray(int[] a, int offset, int len) {
        ENG_Integer[] arr = new ENG_Integer[len];
        for (int i = 0; i < len; ++i) {
            arr[i] = new ENG_Integer(a[offset + i]);
        }
        return arr;
    }

    public void add(ENG_Integer b, ENG_Integer ret) {
        ret.setValue(value + b.getValue());
    }

    public ENG_Integer add(ENG_Integer b) {
        ENG_Integer ret = new ENG_Integer();
        add(b, ret);
        return ret;
    }

    public void addInPlace(ENG_Integer b) {
        value += b.getValue();
    }

    public void sub(ENG_Integer b, ENG_Integer ret) {
        ret.setValue(value - b.getValue());
    }

    public ENG_Integer sub(ENG_Integer b) {
        ENG_Integer ret = new ENG_Integer();
        sub(b, ret);
        return ret;
    }

    public void subInPlace(ENG_Integer b) {
        value -= b.getValue();
    }

    public void mul(ENG_Integer b, ENG_Integer ret) {
        ret.setValue(value * b.getValue());
    }

    public ENG_Integer mul(ENG_Integer b) {
        ENG_Integer ret = new ENG_Integer();
        mul(b, ret);
        return ret;
    }

    public void mulInPlace(ENG_Integer b) {
        value *= b.getValue();
    }

    public void div(ENG_Integer b, ENG_Integer ret) {
        ret.setValue(value / b.getValue());
    }

    public ENG_Integer div(ENG_Integer b) {
        ENG_Integer ret = new ENG_Integer();
        div(b, ret);
        return ret;
    }

    public void divInPlace(ENG_Integer b) {
        value /= b.getValue();
    }

    public void add(int b, ENG_Integer ret) {
        ret.setValue(value + b);
    }

    public ENG_Integer add(int b) {
        ENG_Integer ret = new ENG_Integer();
        add(b, ret);
        return ret;
    }

    public void addInPlace(int b) {
        value += b;
    }

    public void sub(int b, ENG_Integer ret) {
        ret.setValue(value - b);
    }

    public ENG_Integer sub(int b) {
        ENG_Integer ret = new ENG_Integer();
        sub(b, ret);
        return ret;
    }

    public void subInPlace(int b) {
        value -= b;
    }

    public void mul(int b, ENG_Integer ret) {
        ret.setValue(value * b);
    }

    public ENG_Integer mul(int b) {
        ENG_Integer ret = new ENG_Integer();
        mul(b, ret);
        return ret;
    }

    public void mulInPlace(int b) {
        value *= b;
    }

    public void div(int b, ENG_Integer ret) {
        ret.setValue(value / b);
    }

    public ENG_Integer div(int b) {
        ENG_Integer ret = new ENG_Integer();
        div(b, ret);
        return ret;
    }

    public void divInPlace(int b) {
        value /= b;
    }
}
