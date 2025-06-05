package headwayent.hotshotengine.basictypes;

import headwayent.hotshotengine.ENG_Utility;

public class ENG_Long implements Comparable<ENG_Long> {

    public static final int SIZE_IN_BYTES = Long.SIZE / ENG_Utility.BYTE_SIZE;
    private long value;

    public ENG_Long() {

    }

    public ENG_Long(long l) {
        value = l;
    }

    public ENG_Long(ENG_Long l) {
        value = l.getValue();
    }

    public ENG_Long(Long l) {
        value = l;
    }

    public ENG_Long(String l) {
        value = Long.valueOf(l);
    }

    public void setValue(long value) {
        this.value = value;
    }

    public void setValue(ENG_Long value) {
        this.value = value.getValue();
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = Long.valueOf(value);
    }

    public long getValue() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }

    public ENG_Long clone() {
        return new ENG_Long(value);
    }

    public boolean equals(Object obj) {
        if (obj instanceof ENG_Long) {
            return value == ((ENG_Long) obj).getValue();
        }
        if (obj instanceof Long) {
            return value == (Long) obj;
        }
        throw new IllegalArgumentException();
    }

    public int hashCode() {
        return (int) value;
    }

    public int compareTo(long l) {
        if (value < l) {
            return ENG_Utility.COMPARE_LESS_THAN;
        }
        if (value > l) {
            return ENG_Utility.COMPARE_GREATER_THAN;
        }
        return ENG_Utility.COMPARE_EQUAL_TO;
    }

    public int compareTo(ENG_Long l) {
        return compareTo(l.getValue());
    }

    public int compareTo(Long l) {
        return compareTo(l.longValue());
    }

    public static ENG_Long valueOf(String l) {
        return new ENG_Long(l);
    }

    public static ENG_Long valueOf(long l) {
        return new ENG_Long(l);
    }

    public static ENG_Long valueOf(Long l) {
        return new ENG_Long(l);
    }

    public static ENG_Long[] createArray(int len) {
        ENG_Long[] arr = new ENG_Long[len];
        for (int i = 0; i < len; ++i) {
            arr[i] = new ENG_Long();
        }
        return arr;
    }

    public static ENG_Long[] createArray(long[] a, int offset, int len) {
        ENG_Long[] arr = new ENG_Long[len];
        for (int i = 0; i < len; ++i) {
            arr[i] = new ENG_Long(a[offset + i]);
        }
        return arr;
    }

    public void add(ENG_Long b, ENG_Long ret) {
        ret.setValue(value + b.getValue());
    }

    public ENG_Long add(ENG_Long b) {
        ENG_Long ret = new ENG_Long();
        add(b, ret);
        return ret;
    }

    public void addInPlace(ENG_Long b) {
        value += b.getValue();
    }

    public void sub(ENG_Long b, ENG_Long ret) {
        ret.setValue(value - b.getValue());
    }

    public ENG_Long sub(ENG_Long b) {
        ENG_Long ret = new ENG_Long();
        sub(b, ret);
        return ret;
    }

    public void subInPlace(ENG_Long b) {
        value -= b.getValue();
    }

    public void mul(ENG_Long b, ENG_Long ret) {
        ret.setValue(value * b.getValue());
    }

    public ENG_Long mul(ENG_Long b) {
        ENG_Long ret = new ENG_Long();
        mul(b, ret);
        return ret;
    }

    public void mulInPlace(ENG_Long b) {
        value *= b.getValue();
    }

    public void div(ENG_Long b, ENG_Long ret) {
        ret.setValue(value / b.getValue());
    }

    public ENG_Long div(ENG_Long b) {
        ENG_Long ret = new ENG_Long();
        div(b, ret);
        return ret;
    }

    public void divInPlace(ENG_Long b) {
        value /= b.getValue();
    }

    public void add(long b, ENG_Long ret) {
        ret.setValue(value + b);
    }

    public ENG_Long add(long b) {
        ENG_Long ret = new ENG_Long();
        add(b, ret);
        return ret;
    }

    public void addInPlace(long b) {
        value += b;
    }

    public void sub(long b, ENG_Long ret) {
        ret.setValue(value - b);
    }

    public ENG_Long sub(long b) {
        ENG_Long ret = new ENG_Long();
        sub(b, ret);
        return ret;
    }

    public void subInPlace(long b) {
        value -= b;
    }

    public void mul(long b, ENG_Long ret) {
        ret.setValue(value * b);
    }

    public ENG_Long mul(long b) {
        ENG_Long ret = new ENG_Long();
        mul(b, ret);
        return ret;
    }

    public void mulInPlace(long b) {
        value *= b;
    }

    public void div(long b, ENG_Long ret) {
        ret.setValue(value / b);
    }

    public ENG_Long div(long b) {
        ENG_Long ret = new ENG_Long();
        div(b, ret);
        return ret;
    }

    public void divInPlace(long b) {
        value /= b;
    }
}
