package headwayent.hotshotengine.basictypes;

public class ENG_Boolean implements Comparable<ENG_Boolean> {

    public static final int SIZE_IN_BYTES = 1;
    private boolean value;

    public ENG_Boolean() {

    }

    public ENG_Boolean(boolean b) {
        value = b;
    }

    public ENG_Boolean(ENG_Boolean b) {
        value = b.getValue();
    }

    public ENG_Boolean(Boolean b) {
        value = b;
    }

    public ENG_Boolean(String b) {
        value = Boolean.valueOf(b);
    }

    public void setValue(ENG_Boolean value) {
        this.value = value.getValue();
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = Boolean.valueOf(value);
    }

    public boolean getValue() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }

    public ENG_Boolean clone() {
        return new ENG_Boolean(value);
    }

    public boolean equals(Object obj) {
        if (obj instanceof ENG_Boolean) {
            return value == ((ENG_Boolean) obj).getValue();
        }
        if (obj instanceof Boolean) {
            return value == (Boolean) obj;
        }
        throw new IllegalArgumentException();
    }

    public int hashCode() {
        return value ? 1 : 0;
    }

    public boolean compareTo(boolean b) {
        return value == b;
    }

    public int compareTo(ENG_Boolean b) {
        return 0;
    }

    public boolean compareToBool(ENG_Boolean b) {
        return compareTo(b.getValue());
    }

    public boolean compareTo(Boolean b) {
        return compareTo(b.booleanValue());
    }

    public static ENG_Boolean valueOf(String b) {
        return new ENG_Boolean(b);
    }

    public static ENG_Boolean valueOf(boolean b) {
        return new ENG_Boolean(b);
    }

    public static ENG_Boolean valueOf(Boolean b) {
        return new ENG_Boolean(b);
    }

    public static ENG_Boolean[] createArray(int len) {
        ENG_Boolean[] arr = new ENG_Boolean[len];
        for (int i = 0; i < len; ++i) {
            arr[i] = new ENG_Boolean();
        }
        return arr;
    }

    public static ENG_Boolean[] createArray(boolean[] a, int offset, int len) {
        ENG_Boolean[] arr = new ENG_Boolean[len];
        for (int i = 0; i < len; ++i) {
            arr[i] = new ENG_Boolean(a[offset + i]);
        }
        return arr;
    }

    public void and(ENG_Boolean b, ENG_Boolean ret) {
        ret.setValue(value && b.getValue());
    }

    public ENG_Boolean and(ENG_Boolean b) {
        ENG_Boolean ret = new ENG_Boolean();
        and(b, ret);
        return ret;
    }

    public void andInPlace(ENG_Boolean b) {
        value = value && b.getValue();
    }

    public void or(ENG_Boolean b, ENG_Boolean ret) {
        ret.setValue(value || b.getValue());
    }

    public ENG_Boolean or(ENG_Boolean b) {
        ENG_Boolean ret = new ENG_Boolean();
        or(b, ret);
        return ret;
    }

    public void orInPlace(ENG_Boolean b) {
        value = value || b.getValue();
    }

    public void xor(ENG_Boolean b, ENG_Boolean ret) {
        ret.setValue(value ^ b.getValue());
    }

    public ENG_Boolean xor(ENG_Boolean b) {
        ENG_Boolean ret = new ENG_Boolean();
        xor(b, ret);
        return ret;
    }

    public void xorInPlace(ENG_Boolean b) {
        value = value ^ b.getValue();
    }

    public void not(ENG_Boolean b, ENG_Boolean ret) {
        ret.setValue(!b.getValue());
    }

    public ENG_Boolean not(ENG_Boolean b) {
        ENG_Boolean ret = new ENG_Boolean();
        not(b, ret);
        return ret;
    }

    public void notInPlace(ENG_Boolean b) {
        value = !b.getValue();
    }

    public void and(boolean b, ENG_Boolean ret) {
        ret.setValue(value && b);
    }

    public ENG_Boolean and(boolean b) {
        ENG_Boolean ret = new ENG_Boolean();
        and(b, ret);
        return ret;
    }

    public void andInPlace(boolean b) {
        value = value && b;
    }

    public void or(boolean b, ENG_Boolean ret) {
        ret.setValue(value || b);
    }

    public ENG_Boolean or(boolean b) {
        ENG_Boolean ret = new ENG_Boolean();
        or(b, ret);
        return ret;
    }

    public void orInPlace(boolean b) {
        value = value || b;
    }

    public void xor(boolean b, ENG_Boolean ret) {
        ret.setValue(value ^ b);
    }

    public ENG_Boolean xor(boolean b) {
        ENG_Boolean ret = new ENG_Boolean();
        xor(b, ret);
        return ret;
    }

    public void xorInPlace(boolean b) {
        value = value ^ b;
    }

    public void not(boolean b, ENG_Boolean ret) {
        ret.setValue(!b);
    }

    public ENG_Boolean not(boolean b) {
        ENG_Boolean ret = new ENG_Boolean();
        not(b, ret);
        return ret;
    }

    public void notInPlace(boolean b) {
        value = !b;
    }
}
