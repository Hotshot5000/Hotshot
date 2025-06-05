package headwayent.hotshotengine.basictypes;

import headwayent.hotshotengine.ENG_Utility;

public class ENG_Byte implements Comparable<ENG_Byte> {

    public static final int SIZE_IN_BYTES = Byte.SIZE / ENG_Utility.BYTE_SIZE;
    private byte value;

    public ENG_Byte() {

    }

    public ENG_Byte(byte b) {
        value = b;
    }

    public ENG_Byte(ENG_Byte b) {
        value = b.getValue();
    }

    public ENG_Byte(Byte b) {
        value = b;
    }

    public ENG_Byte(String b) {
        value = Byte.valueOf(b);
    }

    public void setValue(byte b) {
        value = b;
    }

    public void setValue(ENG_Byte b) {
        this.value = b.getValue();
    }

    public void setValue(Byte b) {
        value = b;
    }

    public void setValue(String value) {
        this.value = Byte.valueOf(value);
    }

    public byte getValue() {
        return value;
    }

    public String toString() {
        return String.valueOf((int) value);
    }

    public ENG_Byte clone() {
        return new ENG_Byte(value);
    }

    public boolean equals(Object obj) {
        if (obj instanceof ENG_Byte) {
            return value == ((ENG_Byte) obj).getValue();
        }
        if (obj instanceof Byte) {
            return value == (Byte) obj;
        }
        throw new IllegalArgumentException();
    }

    public int hashCode() {
        return value;
    }

    public int compareTo(byte b) {
        if (value < b) {
            return ENG_Utility.COMPARE_LESS_THAN;
        }
        if (value > b) {
            return ENG_Utility.COMPARE_GREATER_THAN;
        }
        return ENG_Utility.COMPARE_EQUAL_TO;
    }

    public int compareTo(ENG_Byte b) {
        return compareTo(b.getValue());
    }

    public int compareTo(Byte b) {
        return compareTo(b.byteValue());
    }

    public static ENG_Byte valueOf(String b) {
        return new ENG_Byte(b);
    }

    public static ENG_Byte valueOf(byte b) {
        return new ENG_Byte(b);
    }

    public static ENG_Byte valueOf(Byte b) {
        return new ENG_Byte(b);
    }

    public static ENG_Byte[] createArray(int len) {
        ENG_Byte[] arr = new ENG_Byte[len];
        for (int i = 0; i < len; ++i) {
            arr[i] = new ENG_Byte();
        }
        return arr;
    }

    public static ENG_Byte[] createArray(byte[] a, int offset, int len) {
        ENG_Byte[] arr = new ENG_Byte[len];
        for (int i = 0; i < len; ++i) {
            arr[i] = new ENG_Byte(a[offset + i]);
        }
        return arr;
    }

    public void add(ENG_Byte b, ENG_Byte ret) {
        ret.setValue((byte) (value + b.getValue()));
    }

    public ENG_Byte add(ENG_Byte b) {
        ENG_Byte ret = new ENG_Byte();
        add(b, ret);
        return ret;
    }

    public void addInPlace(ENG_Byte b) {
        value += b.getValue();
    }

    public void sub(ENG_Byte b, ENG_Byte ret) {
        ret.setValue((byte) (value - b.getValue()));
    }

    public ENG_Byte sub(ENG_Byte b) {
        ENG_Byte ret = new ENG_Byte();
        sub(b, ret);
        return ret;
    }

    public void subInPlace(ENG_Byte b) {
        value -= b.getValue();
    }

    public void mul(ENG_Byte b, ENG_Byte ret) {
        ret.setValue((byte) (value * b.getValue()));
    }

    public ENG_Byte mul(ENG_Byte b) {
        ENG_Byte ret = new ENG_Byte();
        mul(b, ret);
        return ret;
    }

    public void mulInPlace(ENG_Byte b) {
        value *= b.getValue();
    }

    public void div(ENG_Byte b, ENG_Byte ret) {
        ret.setValue((byte) (value / b.getValue()));
    }

    public ENG_Byte div(ENG_Byte b) {
        ENG_Byte ret = new ENG_Byte();
        div(b, ret);
        return ret;
    }

    public void divInPlace(ENG_Byte b) {
        value /= b.getValue();
    }

    public void add(byte b, ENG_Byte ret) {
        ret.setValue((byte) (value + b));
    }

    public ENG_Byte add(byte b) {
        ENG_Byte ret = new ENG_Byte();
        add(b, ret);
        return ret;
    }

    public void addInPlace(byte b) {
        value += b;
    }

    public void sub(byte b, ENG_Byte ret) {
        ret.setValue((byte) (value - b));
    }

    public ENG_Byte sub(byte b) {
        ENG_Byte ret = new ENG_Byte();
        sub(b, ret);
        return ret;
    }

    public void subInPlace(byte b) {
        value -= b;
    }

    public void mul(byte b, ENG_Byte ret) {
        ret.setValue((byte) (value * b));
    }

    public ENG_Byte mul(byte b) {
        ENG_Byte ret = new ENG_Byte();
        mul(b, ret);
        return ret;
    }

    public void mulInPlace(byte b) {
        value *= b;
    }

    public void div(byte b, ENG_Byte ret) {
        ret.setValue((byte) (value / b));
    }

    public ENG_Byte div(byte b) {
        ENG_Byte ret = new ENG_Byte();
        div(b, ret);
        return ret;
    }

    public void divInPlace(byte b) {
        value /= b;
    }
}
