package headwayent.hotshotengine.basictypes;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_NumberOperations;
import headwayent.hotshotengine.ENG_Utility;

public class ENG_Double implements Comparable<ENG_Double>,
        ENG_NumberOperations<ENG_Double> {

    public static final int SIZE_IN_BYTES = Double.SIZE / ENG_Utility.BYTE_SIZE;
    public static final double DOUBLE_EPSILON = ENG_Math.DOUBLE_EPSILON;

    private double value;

    public ENG_Double() {

    }

    public ENG_Double(double d) {
        value = d;
    }

    public ENG_Double(ENG_Double d) {
        value = d.getValue();
    }

    public ENG_Double(Double d) {
        value = d;
    }

    public ENG_Double(String d) {
        value = Double.valueOf(d);
    }

    public void setValue(String value) {
        this.value = Double.valueOf(value);
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setValue(ENG_Double value) {
        this.value = value.getValue();
    }

    public double getValue() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }

    public ENG_Double clone() {
        return new ENG_Double(value);
    }

    public boolean equals(Object obj) {
        if (obj instanceof ENG_Double) {
            double res = value - ((ENG_Double) obj).getValue();
            return (res > -DOUBLE_EPSILON) && (res < DOUBLE_EPSILON);
        }
        if (obj instanceof Double) {
            double res = value - (Double) obj;
            return (res > -DOUBLE_EPSILON) && (res < DOUBLE_EPSILON);
        }
        throw new IllegalArgumentException();
    }

    public int hashCode() {
        return (int) value;
    }

    public static int compareTo(double d1, double d2, double tolerance) {
        double res = d1 - d2;
        if ((res < -tolerance) || (res > tolerance)) {
            if (res < 0.0f) {
                return ENG_Utility.COMPARE_LESS_THAN;
            } else {
                return ENG_Utility.COMPARE_GREATER_THAN;
            }
        }
        return ENG_Utility.COMPARE_EQUAL_TO;
    }

    public static int compareTo(double d1, double d2) {
        double res = d1 - d2;
        if ((res < -DOUBLE_EPSILON) || (res > DOUBLE_EPSILON)) {
            if (res < 0.0f) {
                return ENG_Utility.COMPARE_LESS_THAN;
            } else {
                return ENG_Utility.COMPARE_GREATER_THAN;
            }
        }
        return ENG_Utility.COMPARE_EQUAL_TO;
    }

    public static boolean isEqual(double f1, double f2) {
        return isEqual(f1, f2, DOUBLE_EPSILON);
    }

    public static boolean isEqual(double f1, double f2, double tolerance) {
        return Math.abs(f1 - f2) < tolerance;
    }

    public int compareTo(double d) {
        return compareTo(value, d);
    }

    public int compareTo(Double d) {
        return compareTo(d.doubleValue());
    }

    public int compareTo(ENG_Double d) {
        return compareTo(d.getValue());
    }

    public static ENG_Double valueOf(String d) {
        return new ENG_Double(d);
    }

    public static ENG_Double valueOf(double d) {
        return new ENG_Double(d);
    }

    public static ENG_Double valueOf(Double d) {
        return new ENG_Double(d);
    }

    public static ENG_Double[] createArray(int len) {
        ENG_Double[] arr = new ENG_Double[len];
        for (int i = 0; i < len; ++i) {
            arr[i] = new ENG_Double();
        }
        return arr;
    }

    public static ENG_Double[] createArray(double[] a, int offset, int len) {
        ENG_Double[] arr = new ENG_Double[len];
        for (int i = 0; i < len; ++i) {
            arr[i] = new ENG_Double(a[offset + i]);
        }
        return arr;
    }

    @Override
    public ENG_Double add(ENG_NumberOperations<ENG_Double> oth) {
        // TODO Auto-generated method stub
        return new ENG_Double(value + oth.get().getValue());
    }

    @Override
    public ENG_Double sub(ENG_NumberOperations<ENG_Double> oth) {
        // TODO Auto-generated method stub
        return new ENG_Double(value - oth.get().getValue());
    }

    @Override
    public ENG_Double mul(ENG_NumberOperations<ENG_Double> oth) {
        // TODO Auto-generated method stub
        return new ENG_Double(value * oth.get().getValue());
    }

    @Override
    public ENG_Double div(ENG_NumberOperations<ENG_Double> oth) {
        // TODO Auto-generated method stub
        return new ENG_Double(value / oth.get().getValue());
    }

    @Override
    public ENG_Double mul(float oth) {
        // TODO Auto-generated method stub
        return new ENG_Double(value * oth);
    }

    @Override
    public ENG_Double get() {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public void set(ENG_Double val) {
        // TODO Auto-generated method stub
        setValue(val);
    }

    public void add(ENG_Double b, ENG_Double ret) {
        ret.setValue(value + b.getValue());
    }

    public ENG_Double add(ENG_Double b) {
        ENG_Double ret = new ENG_Double();
        add(b, ret);
        return ret;
    }

    public void addInPlace(ENG_Double b) {
        value += b.getValue();
    }

    public void sub(ENG_Double b, ENG_Double ret) {
        ret.setValue(value - b.getValue());
    }

    public ENG_Double sub(ENG_Double b) {
        ENG_Double ret = new ENG_Double();
        sub(b, ret);
        return ret;
    }

    public void subInPlace(ENG_Double b) {
        value -= b.getValue();
    }

    public void mul(ENG_Double b, ENG_Double ret) {
        ret.setValue(value * b.getValue());
    }

    public ENG_Double mul(ENG_Double b) {
        ENG_Double ret = new ENG_Double();
        mul(b, ret);
        return ret;
    }

    public void mulInPlace(ENG_Double b) {
        value *= b.getValue();
    }

    public void div(ENG_Double b, ENG_Double ret) {
        ret.setValue(value / b.getValue());
    }

    public ENG_Double div(ENG_Double b) {
        ENG_Double ret = new ENG_Double();
        div(b, ret);
        return ret;
    }

    public void divInPlace(ENG_Double b) {
        value /= b.getValue();
    }

    public void add(double b, ENG_Double ret) {
        ret.setValue(value + b);
    }

    public ENG_Double add(double b) {
        ENG_Double ret = new ENG_Double();
        add(b, ret);
        return ret;
    }

    public void addInPlace(double b) {
        value += b;
    }

    public void sub(double b, ENG_Double ret) {
        ret.setValue(value - b);
    }

    public ENG_Double sub(double b) {
        ENG_Double ret = new ENG_Double();
        sub(b, ret);
        return ret;
    }

    public void subInPlace(double b) {
        value -= b;
    }

    public void mul(double b, ENG_Double ret) {
        ret.setValue(value * b);
    }

    public ENG_Double mul(double b) {
        ENG_Double ret = new ENG_Double();
        mul(b, ret);
        return ret;
    }

    public void mulInPlace(double b) {
        value *= b;
    }

    public void div(double b, ENG_Double ret) {
        ret.setValue(value / b);
    }

    public ENG_Double div(double b) {
        ENG_Double ret = new ENG_Double();
        div(b, ret);
        return ret;
    }

    public void divInPlace(double b) {
        value /= b;
    }
}
