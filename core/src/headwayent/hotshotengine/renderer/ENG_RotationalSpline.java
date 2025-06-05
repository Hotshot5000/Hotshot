/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Quaternion;

import java.util.ArrayList;

public class ENG_RotationalSpline {

    protected final ArrayList<ENG_Quaternion> mPoints = new ArrayList<>();
    protected final ArrayList<ENG_Quaternion> mTangents = new ArrayList<>();

    protected boolean mAutoCalc;

    public ENG_RotationalSpline() {
        
    }

    public void addPoint(ENG_Quaternion q) {
        mPoints.add(q);
        if (mAutoCalc) {
            recalcTangents();
        }
    }

    public ENG_Quaternion getPoint(int index) {
        assert (index >= 0 && index < mPoints.size());
        return mPoints.get(index);
    }

    public int getNumPoints() {
        return mPoints.size();
    }

    public void clear() {
        mPoints.clear();
        mTangents.clear();
    }

    public void updatePoint(int index, ENG_Quaternion q) {
        assert (index >= 0 && index < mPoints.size());
        mPoints.get(index).set(q);
        if (mAutoCalc) {
            recalcTangents();
        }
    }

    public ENG_Quaternion interpolate(float t, boolean useShortestPath) {
        float fSeg = t * (mPoints.size() - 1);
        int segIdx = (int) fSeg;
        // Apportion t 
        t = fSeg - segIdx;

        return interpolate(segIdx, t, useShortestPath);
    }

    public ENG_Quaternion interpolate(int fromIndex, float t) {
        return interpolate(fromIndex, t, true);
    }

    public ENG_Quaternion interpolate(int fromIndex, float t, boolean useShortestPath) {
        assert (fromIndex >= 0 && fromIndex < mPoints.size());

        if ((fromIndex + 1) == mPoints.size()) {
            return mPoints.get(fromIndex);
        }
        if (t == 0.0f) {
            return mPoints.get(fromIndex);
        }
        if (t == 1.0f) {
            return mPoints.get(fromIndex + 1);
        }

        ENG_Quaternion p = mPoints.get(fromIndex);
        ENG_Quaternion q = mPoints.get(fromIndex + 1);
        ENG_Quaternion a = mPoints.get(fromIndex);
        ENG_Quaternion b = mPoints.get(fromIndex + 1);

        return ENG_Quaternion.squad(t, p, a, b, q, useShortestPath);
    }

    public void setAutoCalculate(boolean b) {
        mAutoCalc = b;
    }

    public void recalcTangents() {

        int numPoints = mPoints.size();

        if (numPoints < 2) {
            return;
        }

        mTangents.clear();

        boolean isClosed;
        isClosed = mPoints.get(0) == mPoints.get(numPoints - 1);

        ENG_Quaternion invp = new ENG_Quaternion();
        ENG_Quaternion part1 = new ENG_Quaternion();
        ENG_Quaternion part2 = new ENG_Quaternion();
        ENG_Quaternion preExp = new ENG_Quaternion();

        for (int i = 0; i < numPoints; ++i) {
            ENG_Quaternion p = mPoints.get(i);
            p.inverse(invp);

            if (i == 0) {
                invp.mulRet(mPoints.get(i + 1)).log(part1);
                if (isClosed) {
                    invp.mulRet(mPoints.get(numPoints - 2)).log(part2);
                } else {
                    invp.mulRet(p).log(part2);
                }
            } else if (i == numPoints - 1) {
                if (isClosed) {
                    invp.mulRet(mPoints.get(1)).log(part1);
                } else {
                    invp.mulRet(p).log(part1);
                }
                invp.mulRet(mPoints.get(i - 1)).log(part2);
            } else {
                invp.mulRet(mPoints.get(i + 1)).log(part1);
                invp.mulRet(mPoints.get(i - 1)).log(part2);
            }
            part1.addRet(part2).mul(-0.25f, preExp);
            mTangents.add(p.mulRet(preExp.exp()));
        }
    }

}
