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

public class ENG_Radian implements Comparable<ENG_Radian> {

    private float mRad;

    public ENG_Radian() {

    }

    public ENG_Radian(ENG_Radian r) {
        set(r);
    }

    public ENG_Radian(float f) {
        set(f);
    }

    public ENG_Radian(ENG_Degree d) {
        set(d);
    }

    public void set(float f) {
        mRad = f;
    }

    public void set(ENG_Radian r) {
        mRad = r.mRad;
    }

    public void set(ENG_Degree d) {
        mRad = d.valueRadians();
    }

    public ENG_Radian add(float f) {
        ENG_Radian ret = new ENG_Radian();
        add(f, ret);
        return ret;
    }

    public ENG_Radian add(ENG_Degree d) {
        ENG_Radian ret = new ENG_Radian();
        add(d, ret);
        return ret;
    }

    public ENG_Radian add(ENG_Radian r) {
        ENG_Radian ret = new ENG_Radian();
        add(r, ret);
        return ret;
    }

    public void add(float r, ENG_Radian ret) {
        ret.set(mRad + r);
    }

    public void add(ENG_Radian r, ENG_Radian ret) {
        ret.set(mRad + r.mRad);
    }

    public void add(ENG_Degree d, ENG_Radian ret) {
        ret.set(mRad + d.valueRadians());
    }

    public void addInPlace(float r) {
        mRad += r;
    }

    public void addInPlace(ENG_Radian r) {
        mRad += r.mRad;
    }

    public void addInPlace(ENG_Degree d) {
        mRad += d.valueRadians();
    }

    public ENG_Radian sub(float d) {
        ENG_Radian ret = new ENG_Radian();
        sub(d, ret);
        return ret;
    }

    public ENG_Radian sub(ENG_Degree d) {
        ENG_Radian ret = new ENG_Radian();
        sub(d, ret);
        return ret;
    }

    public ENG_Radian sub(ENG_Radian r) {
        ENG_Radian ret = new ENG_Radian();
        sub(r, ret);
        return ret;
    }

    public void sub(float r, ENG_Radian ret) {
        ret.set(mRad - r);
    }

    public void sub(ENG_Radian r, ENG_Radian ret) {
        ret.set(mRad - r.mRad);
    }

    public void sub(ENG_Degree d, ENG_Radian ret) {
        ret.set(mRad - d.valueRadians());
    }

    public void subInPlace(float r) {
        mRad -= r;
    }

    public void subInPlace(ENG_Radian r) {
        mRad -= r.mRad;
    }

    public void subInPlace(ENG_Degree d) {
        mRad -= d.valueRadians();
    }

    public ENG_Radian mul(float d) {
        ENG_Radian ret = new ENG_Radian();
        mul(d, ret);
        return ret;
    }

    public ENG_Radian mul(ENG_Degree d) {
        ENG_Radian ret = new ENG_Radian();
        mul(d, ret);
        return ret;
    }

    public ENG_Radian mul(ENG_Radian r) {
        ENG_Radian ret = new ENG_Radian();
        mul(r, ret);
        return ret;
    }

    public void mul(float r, ENG_Radian ret) {
        ret.set(mRad * r);
    }

    public void mul(ENG_Radian r, ENG_Radian ret) {
        ret.set(mRad * r.mRad);
    }

    public void mul(ENG_Degree d, ENG_Radian ret) {
        ret.set(mRad * d.valueRadians());
    }

    public void mulInPlace(float r) {
        mRad *= r;
    }

    public void mulInPlace(ENG_Radian r) {
        mRad *= r.mRad;
    }

    public void mulInPlace(ENG_Degree d) {
        mRad *= d.valueRadians();
    }

    public ENG_Radian div(float d) {
        ENG_Radian ret = new ENG_Radian();
        div(d, ret);
        return ret;
    }

    public ENG_Radian div(ENG_Degree d) {
        ENG_Radian ret = new ENG_Radian();
        div(d, ret);
        return ret;
    }

    public ENG_Radian div(ENG_Radian r) {
        ENG_Radian ret = new ENG_Radian();
        div(r, ret);
        return ret;
    }

    public void div(float r, ENG_Radian ret) {
        ret.set(mRad / r);
    }

    public void div(ENG_Radian r, ENG_Radian ret) {
        ret.set(mRad / r.mRad);
    }

    public void div(ENG_Degree d, ENG_Radian ret) {
        ret.set(mRad / d.valueRadians());
    }

    public void divInPlace(float r) {
        mRad /= r;
    }

    public void divInPlace(ENG_Radian r) {
        mRad /= r.mRad;
    }

    public void divInPlace(ENG_Degree d) {
        mRad /= d.valueRadians();
    }

    public float valueDegrees() {
        return ENG_Math.RadiansToDegrees(mRad);
    }

    public float valueRadians() {
        return mRad;
    }

    public String toString() {
        return "Radians: " + mRad;
    }

    @Override
    public int compareTo(ENG_Radian another) {

        if (mRad < another.valueRadians()) {
            return -1;
        } else if (mRad > another.valueRadians()) {
            return 1;
        }
        return 0;
    }

    public boolean equals(Object o) {
        if (o instanceof ENG_Radian) {
            return ENG_Float.compareTo(mRad, ((ENG_Radian) o).valueRadians()) == 0;
        } else if (o instanceof ENG_Degree) {
            return ENG_Float.compareTo(mRad, ((ENG_Degree) o).valueRadians()) == 0;
        }
        if (MainActivity.isDebugmode()) {
            throw new ClassCastException("Object must be degrees or radians");
        }
        return false;
    }
}
