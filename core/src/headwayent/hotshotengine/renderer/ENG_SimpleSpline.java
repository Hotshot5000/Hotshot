/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/24/21, 8:41 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;

import java.util.ArrayList;

public class ENG_SimpleSpline {

    protected boolean mAutoCalc;
    protected final ArrayList<ENG_Vector3D> mPoints = new ArrayList<>();
    protected final ArrayList<ENG_Vector3D> mTangents = new ArrayList<>();
    protected final ENG_Matrix4 mCoeffs = new ENG_Matrix4();

    public ENG_SimpleSpline() {

        // Set up matrix
        // Hermite polynomial
    /*    mCoeffs[0][0] = 2;
	    mCoeffs[0][1] = -2;
	    mCoeffs[0][2] = 1;
	    mCoeffs[0][3] = 1;
	    mCoeffs[1][0] = -3;
	    mCoeffs[1][1] = 3;
	    mCoeffs[1][2] = -2;
	    mCoeffs[1][3] = -1;
	    mCoeffs[2][0] = 0;
	    mCoeffs[2][1] = 0;
	    mCoeffs[2][2] = 1;
	    mCoeffs[2][3] = 0;
	    mCoeffs[3][0] = 1;
	    mCoeffs[3][1] = 0;
	    mCoeffs[3][2] = 0;
	    mCoeffs[3][3] = 0;*/

        mCoeffs.set(0, 0, 2);
        mCoeffs.set(0, 1, -2);
        mCoeffs.set(0, 2, 1);
        mCoeffs.set(0, 3, 1);
        mCoeffs.set(1, 0, -3);
        mCoeffs.set(1, 1, 3);
        mCoeffs.set(1, 2, -2);
        mCoeffs.set(1, 3, -1);
        mCoeffs.set(2, 0, 0);
        mCoeffs.set(2, 1, 0);
        mCoeffs.set(2, 2, 1);
        mCoeffs.set(2, 3, 0);
        mCoeffs.set(3, 0, 1);
        mCoeffs.set(3, 1, 0);
        mCoeffs.set(3, 2, 0);
        mCoeffs.set(3, 3, 0);

        mAutoCalc = true;
    }

    public void addPoint(ENG_Vector3D v) {
        mPoints.add(v);
        if (mAutoCalc) {
            recalcTangents();
        }
    }

    public ENG_Vector3D getPoint(int index) {
        assert (index > 0 && index < mPoints.size());
        return mPoints.get(index);
    }

    public int getNumPoints() {
        return mPoints.size();
    }

    public void clear() {
        mPoints.clear();
        mTangents.clear();
    }

    public void updatePoint(int index, ENG_Vector3D v) {
        assert (index > 0 && index < mPoints.size());
        mPoints.get(index).set(v);
        if (mAutoCalc) {
            recalcTangents();
        }
    }

    public ENG_Vector3D interpolate(float t) {
        float fSeg = t * (mPoints.size() - 1);
        int segIdx = (int) fSeg;
        // Apportion t 
        t = fSeg - segIdx;

        return interpolate(segIdx, t);
    }

    public ENG_Vector3D interpolate(int fromIndex, float t) {
        assert (fromIndex >= 0 && fromIndex < mPoints.size());

        if ((fromIndex + 1) == mPoints.size()) {
            return new ENG_Vector3D(mPoints.get(fromIndex));
        }

        if (t == 0.0f) {
            return new ENG_Vector3D(mPoints.get(fromIndex));
        }
        if (t == 1.0f) {
            return new ENG_Vector3D(mPoints.get(fromIndex + 1));
        }

        float t2 = t * t;
        float t3 = t2 * t;

        ENG_Vector4D powers = new ENG_Vector4D(t3, t2, t, 1.0f);

        ENG_Vector3D point1 = mPoints.get(fromIndex);
        ENG_Vector3D point2 = mPoints.get(fromIndex + 1);
        ENG_Vector3D tan1 = mTangents.get(fromIndex);
        ENG_Vector3D tan2 = mTangents.get(fromIndex + 1);

        ENG_Matrix4 pt = new ENG_Matrix4();
		/*
		pt[0][0] = point1.x;
        pt[0][1] = point1.y;
        pt[0][2] = point1.z;
        pt[0][3] = 1.0f;
        pt[1][0] = point2.x;
        pt[1][1] = point2.y;
        pt[1][2] = point2.z;
        pt[1][3] = 1.0f;
        pt[2][0] = tan1.x;
        pt[2][1] = tan1.y;
        pt[2][2] = tan1.z;
        pt[2][3] = 1.0f;
        pt[3][0] = tan2.x;
        pt[3][1] = tan2.y;
        pt[3][2] = tan2.z;
        pt[3][3] = 1.0f;
        */

        pt.set(0, 0, point1.x);
        pt.set(0, 1, point1.y);
        pt.set(0, 2, point1.z);
        pt.set(0, 3, 1.0f);
        pt.set(1, 0, point2.x);
        pt.set(1, 1, point2.y);
        pt.set(1, 2, point2.z);
        pt.set(1, 3, 1.0f);
        pt.set(2, 0, tan1.x);
        pt.set(2, 1, tan1.y);
        pt.set(2, 2, tan1.z);
        pt.set(2, 3, 1.0f);
        pt.set(3, 0, tan2.x);
        pt.set(3, 1, tan2.y);
        pt.set(3, 2, tan2.z);
        pt.set(3, 3, 1.0f);

        ENG_Vector4D mul = powers.mulColumn(mCoeffs.concatenate(pt));

        return new ENG_Vector3D(mul.x, mul.y, mul.z);
    }

    public void setAutoCalculate(boolean b) {
        mAutoCalc = b;
    }

    public void recalcTangents() {
        int numPoints = mPoints.size();
        if (numPoints < 2) {
            return;
        }

        boolean isClosed;
        isClosed = mPoints.get(0) == mPoints.get(numPoints - 1);

        mTangents.clear();

        for (int i = 0; i < numPoints; ++i) {
            if (i == 0) {
                if (isClosed) {
                    mTangents.add(mPoints.get(1).sub(mPoints.get(numPoints - 2)).mul(0.5f));
                } else {
                    mTangents.add(mPoints.get(1).sub(mPoints.get(0)).mul(0.5f));
                }
            } else if (i == numPoints - 1) {
                if (isClosed) {
                    mTangents.add(new ENG_Vector3D(mPoints.get(0)));
                } else {
                    mTangents.add(mPoints.get(i).sub(mPoints.get(i - 1)).mul(0.5f));
                }
            } else {
                mTangents.add(mPoints.get(i + 1).sub(mPoints.get(i - 1)).mul(0.5f));
            }
        }
    }

}
