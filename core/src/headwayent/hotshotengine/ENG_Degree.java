/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import headwayent.blackholedarksun.MainActivity;
import headwayent.hotshotengine.basictypes.ENG_Float;

public class ENG_Degree implements Comparable<ENG_Degree> {

    private float mDeg;

    public ENG_Degree() {

    }

    public ENG_Degree(ENG_Degree d) {
        set(d);
    }

    public ENG_Degree(ENG_Radian r) {
        set(r);
    }

    public ENG_Degree(float f) {
        set(f);
    }

    public void set(float f) {
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

    public ENG_Degree add(float d) {
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

    public void add(float f, ENG_Degree ret) {
        ret.set(mDeg + f);
    }

    public void addInPlace(ENG_Degree d) {
        mDeg += d.mDeg;
    }

    public void addInPlace(ENG_Radian r) {
        mDeg += r.valueDegrees();
    }

    public void addInPlace(float f) {
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

    public ENG_Degree sub(float d) {
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

    public void sub(float f, ENG_Degree ret) {
        ret.set(mDeg - f);
    }

    public void subInPlace(ENG_Degree d) {
        mDeg -= d.mDeg;
    }

    public void subInPlace(ENG_Radian r) {
        mDeg -= r.valueDegrees();
    }

    public void subInPlace(float f) {
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

    public ENG_Degree mul(float d) {
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

    public void mul(float f, ENG_Degree ret) {
        ret.set(mDeg * f);
    }

    public void mulInPlace(ENG_Degree d) {
        mDeg *= d.mDeg;
    }

    public void mulInPlace(ENG_Radian r) {
        mDeg *= r.valueDegrees();
    }

    public void mulInPlace(float f) {
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

    public ENG_Degree div(float d) {
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

    public void div(float f, ENG_Degree ret) {
        ret.set(mDeg / f);
    }

    public void divInPlace(ENG_Degree d) {
        mDeg /= d.mDeg;
    }

    public void divInPlace(ENG_Radian r) {
        mDeg /= r.valueDegrees();
    }

    public void divInPlace(float f) {
        mDeg /= f;
    }

    public float valueRadians() {
        return ENG_Math.DegreesToRadians(mDeg);
    }

    public float valueDegrees() {
        return mDeg;
    }

    public String toString() {
        return "Degrees: " + mDeg;
    }

    @Override
    public int compareTo(ENG_Degree another) {

        if (mDeg < another.mDeg) {
            return -1;
        } else if (mDeg > another.mDeg) {
            return 1;
        }
        return 0;
    }

    public boolean equals(Object o) {
        if (o instanceof ENG_Degree) {
            return ENG_Float.compareTo(mDeg, ((ENG_Degree) o).valueDegrees()) == 0;
        } else if (o instanceof ENG_Radian) {
            return ENG_Float.compareTo(mDeg, ((ENG_Radian) o).valueDegrees()) == 0;
        }
        if (MainActivity.isDebugmode()) {
            throw new ClassCastException("Object must be degrees or radians");
        }
        return false;
    }
}
