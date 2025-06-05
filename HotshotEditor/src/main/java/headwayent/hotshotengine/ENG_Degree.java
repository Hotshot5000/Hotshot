package headwayent.hotshotengine;

import headwayent.hotshotengine.basictypes.ENG_Double;

public class ENG_Degree implements Comparable<ENG_Degree> {

    private double mDeg;

    public ENG_Degree() {

    }

    public ENG_Degree(ENG_Degree d) {
        set(d);
    }

    public ENG_Degree(ENG_Radian r) {
        set(r);
    }

    public ENG_Degree(double f) {
        set(f);
    }

    public void set(double f) {
        mDeg = f;
    }

    public void set(ENG_Degree d) {
        mDeg = d.valueDegrees();
    }

    public void set(ENG_Radian r) {
        mDeg = r.valueDegrees();
    }

    public ENG_Degree add(ENG_Degree d) {
        ENG_Degree ret = new ENG_Degree();
        add(d, ret);
        return ret;
    }

    public ENG_Degree add(ENG_Radian d) {
        ENG_Degree ret = new ENG_Degree();
        add(d, ret);
        return ret;
    }

    public ENG_Degree add(double d) {
        ENG_Degree ret = new ENG_Degree();
        add(d, ret);
        return ret;
    }

    public void add(ENG_Degree d, ENG_Degree ret) {
        ret.set(mDeg + d.mDeg);
    }

    public void add(ENG_Radian r, ENG_Degree ret) {
        ret.set(mDeg + r.valueDegrees());
    }

    public void add(double f, ENG_Degree ret) {
        ret.set(mDeg + f);
    }

    public void addInPlace(ENG_Degree d) {
        mDeg += d.mDeg;
    }

    public void addInPlace(ENG_Radian r) {
        mDeg += r.valueDegrees();
    }

    public void addInPlace(double f) {
        mDeg += f;
    }

    public ENG_Degree sub(ENG_Degree d) {
        ENG_Degree ret = new ENG_Degree();
        sub(d, ret);
        return ret;
    }

    public ENG_Degree sub(ENG_Radian d) {
        ENG_Degree ret = new ENG_Degree();
        sub(d, ret);
        return ret;
    }

    public ENG_Degree sub(double d) {
        ENG_Degree ret = new ENG_Degree();
        sub(d, ret);
        return ret;
    }

    public void sub(ENG_Degree d, ENG_Degree ret) {
        ret.set(mDeg - d.mDeg);
    }

    public void sub(ENG_Radian r, ENG_Degree ret) {
        ret.set(mDeg - r.valueDegrees());
    }

    public void sub(double f, ENG_Degree ret) {
        ret.set(mDeg - f);
    }

    public void subInPlace(ENG_Degree d) {
        mDeg -= d.mDeg;
    }

    public void subInPlace(ENG_Radian r) {
        mDeg -= r.valueDegrees();
    }

    public void subInPlace(double f) {
        mDeg -= f;
    }

    public ENG_Degree mul(ENG_Degree d) {
        ENG_Degree ret = new ENG_Degree();
        mul(d, ret);
        return ret;
    }

    public ENG_Degree mul(ENG_Radian d) {
        ENG_Degree ret = new ENG_Degree();
        mul(d, ret);
        return ret;
    }

    public ENG_Degree mul(double d) {
        ENG_Degree ret = new ENG_Degree();
        mul(d, ret);
        return ret;
    }

    public void mul(ENG_Degree d, ENG_Degree ret) {
        ret.set(mDeg * d.mDeg);
    }

    public void mul(ENG_Radian r, ENG_Degree ret) {
        ret.set(mDeg * r.valueDegrees());
    }

    public void mul(double f, ENG_Degree ret) {
        ret.set(mDeg * f);
    }

    public void mulInPlace(ENG_Degree d) {
        mDeg *= d.mDeg;
    }

    public void mulInPlace(ENG_Radian r) {
        mDeg *= r.valueDegrees();
    }

    public void mulInPlace(double f) {
        mDeg *= f;
    }

    public ENG_Degree div(ENG_Degree d) {
        ENG_Degree ret = new ENG_Degree();
        div(d, ret);
        return ret;
    }

    public ENG_Degree div(ENG_Radian d) {
        ENG_Degree ret = new ENG_Degree();
        div(d, ret);
        return ret;
    }

    public ENG_Degree div(double d) {
        ENG_Degree ret = new ENG_Degree();
        div(d, ret);
        return ret;
    }

    public void div(ENG_Degree d, ENG_Degree ret) {
        ret.set(mDeg / d.mDeg);
    }

    public void div(ENG_Radian r, ENG_Degree ret) {
        ret.set(mDeg / r.valueDegrees());
    }

    public void div(double f, ENG_Degree ret) {
        ret.set(mDeg / f);
    }

    public void divInPlace(ENG_Degree d) {
        mDeg /= d.mDeg;
    }

    public void divInPlace(ENG_Radian r) {
        mDeg /= r.valueDegrees();
    }

    public void divInPlace(double f) {
        mDeg /= f;
    }

    public double valueRadians() {
        return ENG_Math.DegreesToRadians(mDeg);
    }

    public double valueDegrees() {
        return mDeg;
    }

    public String toString() {
        return "Degrees: " + mDeg;
    }

    @Override
    public int compareTo(ENG_Degree another) {
        // TODO Auto-generated method stub
        if (mDeg < another.mDeg) {
            return -1;
        } else if (mDeg > another.mDeg) {
            return 1;
        }
        return 0;
    }

    public boolean equals(Object o) {
        if (o instanceof ENG_Degree) {
            return ENG_Double.compareTo(mDeg, ((ENG_Degree) o).valueDegrees()) == 0;
        } else if (o instanceof ENG_Radian) {
            return ENG_Double.compareTo(mDeg, ((ENG_Radian) o).valueDegrees()) == 0;
        }
        return false;
    }
}
