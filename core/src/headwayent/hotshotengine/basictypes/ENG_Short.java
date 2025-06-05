/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:08 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.basictypes;

import headwayent.hotshotengine.ENG_Utility;

public class ENG_Short implements Comparable<ENG_Short> {

    public static final int SIZE_IN_BYTES = Short.SIZE / ENG_Utility.BYTE_SIZE;
    private short value;

/*	public int compareTo(ENG_Short s) {
		if (value < s) {
			return -1;
		}
		if (value > s) {
			return 1;
		}
		return 0;
	}*/

    public ENG_Short() {

    }

    public ENG_Short(short s) {
        value = s;
    }

    public ENG_Short(ENG_Short s) {
        value = s.getValue();
    }

    public ENG_Short(Short s) {
        value = s;
    }

    public ENG_Short(String s) {
        value = Short.parseShort(s);
    }

    public void setValue(short value) {
        this.value = value;
    }

    public void setValue(ENG_Short value) {
        this.value = value.getValue();
    }

    public void setValue(Short value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = Short.parseShort(value);
    }

    public short getValue() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }

    public ENG_Short clone() {
        return new ENG_Short(value);
    }

    public boolean equals(Object obj) {
        if (obj instanceof ENG_Short) {
            return value == ((ENG_Short) obj).getValue();
        }
        if (obj instanceof Short) {
            return value == (Short) obj;
        }
        throw new IllegalArgumentException();
    }

    public int hashCode() {
        return value;
    }

    public int compareTo(short s) {
        return Short.compare(value, s);
    }

    public int compareTo(ENG_Short s) {
        return compareTo(s.getValue());
    }

    public int compareTo(Short s) {
        return compareTo(s.shortValue());
    }

    public static ENG_Short valueOf(String s) {
        return new ENG_Short(s);
    }

    public static ENG_Short valueOf(short s) {
        return new ENG_Short(s);
    }

    public static ENG_Short valueOf(Short s) {
        return new ENG_Short(s);
    }

    public static ENG_Short[] createArray(int len) {
        ENG_Short[] arr = new ENG_Short[len];
        for (int i = 0; i < len; ++i) {
            arr[i] = new ENG_Short();
        }
        return arr;
    }

    public static ENG_Short[] createArray(short[] a, int offset, int len) {
        ENG_Short[] arr = new ENG_Short[len];
        for (int i = 0; i < len; ++i) {
            arr[i] = new ENG_Short(a[offset + i]);
        }
        return arr;
    }

    public void add(ENG_Short b, ENG_Short ret) {
        ret.setValue((short) (value + b.getValue()));
    }

    public ENG_Short add(ENG_Short b) {
        ENG_Short ret = new ENG_Short();
        add(b, ret);
        return ret;
    }

    public void addInPlace(ENG_Short b) {
        value += b.getValue();
    }

    public void sub(ENG_Short b, ENG_Short ret) {
        ret.setValue((short) (value - b.getValue()));
    }

    public ENG_Short sub(ENG_Short b) {
        ENG_Short ret = new ENG_Short();
        sub(b, ret);
        return ret;
    }

    public void subInPlace(ENG_Short b) {
        value -= b.getValue();
    }

    public void mul(ENG_Short b, ENG_Short ret) {
        ret.setValue((short) (value * b.getValue()));
    }

    public ENG_Short mul(ENG_Short b) {
        ENG_Short ret = new ENG_Short();
        mul(b, ret);
        return ret;
    }

    public void mulInPlace(ENG_Short b) {
        value *= b.getValue();
    }

    public void div(ENG_Short b, ENG_Short ret) {
        ret.setValue((short) (value / b.getValue()));
    }

    public ENG_Short div(ENG_Short b) {
        ENG_Short ret = new ENG_Short();
        div(b, ret);
        return ret;
    }

    public void divInPlace(ENG_Short b) {
        value /= b.getValue();
    }

    public void add(short b, ENG_Short ret) {
        ret.setValue((short) (value + b));
    }

    public ENG_Short add(short b) {
        ENG_Short ret = new ENG_Short();
        add(b, ret);
        return ret;
    }

    public void addInPlace(short b) {
        value += b;
    }

    public void sub(short b, ENG_Short ret) {
        ret.setValue((short) (value - b));
    }

    public ENG_Short sub(short b) {
        ENG_Short ret = new ENG_Short();
        sub(b, ret);
        return ret;
    }

    public void subInPlace(short b) {
        value -= b;
    }

    public void mul(short b, ENG_Short ret) {
        ret.setValue((short) (value * b));
    }

    public ENG_Short mul(short b) {
        ENG_Short ret = new ENG_Short();
        mul(b, ret);
        return ret;
    }

    public void mulInPlace(short b) {
        value *= b;
    }

    public void div(short b, ENG_Short ret) {
        ret.setValue((short) (value / b));
    }

    public ENG_Short div(short b) {
        ENG_Short ret = new ENG_Short();
        div(b, ret);
        return ret;
    }

    public void divInPlace(short b) {
        value /= b;
    }
}
