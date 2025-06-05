/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/1/18, 10:24 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

//import headwayent.blackholedarksun.MainActivity;

import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;

public class ENG_AxisAlignedBox {

    public enum Extent {
        EXTENT_NULL,
        EXTENT_FINITE,
        EXTENT_INFINITE
    }

    public enum Corner {
        FAR_LEFT_BOTTOM(0),
        FAR_LEFT_TOP(1),
        FAR_RIGHT_TOP(2),
        FAR_RIGHT_BOTTOM(3),
        NEAR_RIGHT_BOTTOM(7),
        NEAR_LEFT_BOTTOM(6),
        NEAR_LEFT_TOP(5),
        NEAR_RIGHT_TOP(4);

        private final int pos;

        Corner(int pos) {
            this.pos = pos;
        }

        public int getPos() {
            return pos;
        }
    }

    private final ENG_Vector4D min = new ENG_Vector4D(true);
    private final ENG_Vector4D max = new ENG_Vector4D(true);

//    private final ENG_Vector4D temp0 = new ENG_Vector4D(true);
//    private final ENG_Vector4D temp1 = new ENG_Vector4D(true);
//    private final ENG_Vector4D temp2 = new ENG_Vector4D(true);
//    private final ENG_Vector4D temp3 = new ENG_Vector4D(true);


    private Extent extent;

    public ENG_AxisAlignedBox() {
        setMin(-0.5f, -0.5f, -0.5f);
        setMax(0.5f, 0.5f, 0.5f);
        extent = Extent.EXTENT_NULL;
    }

    public ENG_AxisAlignedBox(Extent extent) {
        this();
        this.extent = extent;
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public ENG_AxisAlignedBox(ENG_AxisAlignedBox a) {
        set(a);
    }

    public ENG_AxisAlignedBox(float xMin, float yMin, float zMin,
                              float xMax, float yMax, float zMax) {
        setExtents(xMin, yMin, zMin, xMax, yMax, zMax);
    }

    public ENG_AxisAlignedBox(ENG_Vector4D min, ENG_Vector4D max) {
        setExtents(min, max);
    }

    public ENG_AxisAlignedBox(ENG_Vector3D min, ENG_Vector3D max) {
        setExtents(min, max);
    }

    public void getMin(ENG_Vector4D ret) {
        ret.set(min);
    }

    public void getMin(ENG_Vector3D ret) {
        ret.set(min);
    }

    public ENG_Vector4D getMin() {
        return min;
    }

    public void set(ENG_AxisAlignedBox a) {
        if (a.isNull()) {
            setNull();
        } else if (a.isInfinite()) {
            setInfinite();
        } else {
            setExtents(a.min, a.max);
        }
    }

    public void getMax(ENG_Vector4D ret) {
        ret.set(max);
    }

    public void getMax(ENG_Vector3D ret) {
        ret.set(max);
    }

    public ENG_Vector4D getMax() {
        return max;
    }

    public void setMin(ENG_Vector3D v) {
        min.set(v);
        extent = Extent.EXTENT_FINITE;
    }

    public void setMax(ENG_Vector3D v) {
        max.set(v);
        extent = Extent.EXTENT_FINITE;
    }

    public void setMin(ENG_Vector4D v) {
        min.set(v);
        extent = Extent.EXTENT_FINITE;
    }

    public void setMax(ENG_Vector4D v) {
        max.set(v);
        extent = Extent.EXTENT_FINITE;
    }

    public void setMin(float x, float y, float z) {
        min.set(x, y, z);
        extent = Extent.EXTENT_FINITE;
    }

    public void setMax(float x, float y, float z) {
        max.set(x, y, z);
        extent = Extent.EXTENT_FINITE;
    }

    public void setMinX(float x) {
        min.x = x;
    }

    public void setMinY(float y) {
        min.y = y;
    }

    public void setMinZ(float z) {
        min.z = z;
    }

    public void setMaxX(float x) {
        max.x = x;
    }

    public void setMaxY(float y) {
        max.y = y;
    }

    public void setMaxZ(float z) {
        max.z = z;
    }

    public boolean isNull() {
        return (extent == Extent.EXTENT_NULL);
    }

    public boolean isFinite() {
        return (extent == Extent.EXTENT_FINITE);
    }

    public boolean isInfinite() {
        return (extent == Extent.EXTENT_INFINITE);
    }

    public void setNull() {
        //	extent = Extent.EXTENT_NULL;
        setExtent(Extent.EXTENT_NULL);
    }

    public void setFinite() {
        //	extent = Extent.EXTENT_FINITE;
        setExtent(Extent.EXTENT_FINITE);
    }

    public void setInfinite() {
        //	extent = Extent.EXTENT_INFINITE;
        setExtent(Extent.EXTENT_INFINITE);
    }

    public void setExtents(ENG_Vector4D min, ENG_Vector4D max) {
    /*	System.out.println("ENG_Vector4D min, ENG_Vector4D max " +
				"min.x " + min.x + " min.y " + min.y +
				" min.z " + min.z + " max.x " + max.x + " max.y " + max.y +
				" max.z " + max.z);*/
        if (min.x <= max.x && min.y <= max.y && min.z <= max.z) {
//            if (MainActivity.isDebugmode()) {
            //	float vol = volume();
            //	System.out.println("box volume: " + vol);
            //	if (Float.isInfinite(vol) || vol < 0.0f) {
            //		throw new IllegalArgumentException();
            //	}
            //	System.out.println("min: " + min + " max: " + max);
			/*	if (ENG_Math.isVectorInvalid(min) ||
						ENG_Math.isVectorInvalid(max)) {
					System.out.println("INFINITY min : " + min);
					System.out.println("INFINITY max : " + max);
					System.out.println(test0);
					System.out.println(test1);
					System.out.println(test2);
					System.out.println(test3);
					System.out.println(test4);
					System.out.println(test5);
					System.out.println(testMat);
					throw new IllegalArgumentException();
				}*/
//            }
            this.min.set(min);
            this.max.set(max);
            extent = Extent.EXTENT_FINITE;

        } else {
            throw new IllegalArgumentException("min: " + min + " max: " + max);
        }
    }

    public void setExtents(ENG_Vector3D min, ENG_Vector3D max) {
	/*	System.out.println("ENG_Vector3D min, ENG_Vector3D max " +
				"min.x " + min.x + " min.y " + min.y +
				" min.z " + min.z + " max.x " + max.x + " max.y " + max.y +
				" max.z " + max.z);*/
        if (min.x <= max.x && min.y <= max.y && min.z <= max.z) {
            this.min.set(min);
            this.max.set(max);
            extent = Extent.EXTENT_FINITE;

        } else {
            throw new IllegalArgumentException("ENG_Vector3D min, ENG_Vector3D max " +
                    "min.x " + min.x + " min.y " + min.y +
                    " min.z " + min.z + " max.x " + max.x + " max.y " + max.y +
                    " max.z " + max.z);
        }
    }

    public void setExtents(float mx, float my, float mz, float Mx, float My, float Mz) {
	/*	System.out.println("float version " +
				"min.x " + min.x + " min.y " + min.y +
				" min.z " + min.z + " max.x " + max.x + " max.y " + max.y +
				" max.z " + max.z);*/
        if ((mx <= Mx) && (my <= My) && (mz <= Mz)) {
            min.set(mx, my, mz);
            max.set(Mx, My, Mz);
            extent = Extent.EXTENT_FINITE;

        } else {
            throw new IllegalArgumentException("float version " +
                    "min.x " + min.x + " min.y " + min.y +
                    " min.z " + min.z + " max.x " + max.x + " max.y " + max.y +
                    " max.z " + max.z);
        }
    }

    public ENG_Vector4D[] getAllCornersVec4() {
        ENG_Vector4D[] v = new ENG_Vector4D[8];
        for (int i = 0; i < 8; ++i) {
            v[i] = new ENG_Vector4D(true);
        }
        getAllCorners(v);
        return v;
    }

    public ENG_Vector3D[] getAllCornersVec3() {
        ENG_Vector3D[] v = new ENG_Vector3D[8];
        for (int i = 0; i < 8; ++i) {
            v[i] = new ENG_Vector3D();
        }
        getAllCorners(v);
        return v;
    }

    public void getAllCorners(ENG_Vector4D[] v) {
        if (!isFinite()) {
            throw new IllegalStateException();
        }
        if (v.length < 8) {
            throw new IllegalArgumentException();
        }


        v[0].set(min);
        v[1].set(min.x, max.y, min.z);
        v[2].set(max.x, max.y, min.z);
        v[3].set(max.x, min.y, min.z);

        v[4].set(max);
        v[5].set(min.x, max.y, max.z);
        v[6].set(min.x, min.y, max.z);
        v[7].set(max.x, min.y, max.z);
    }

    public void getAllCorners(ENG_Vector3D[] v) {
        if (!isFinite()) {
            throw new NullPointerException();
        }
        if (v.length < 8) {
            throw new IllegalArgumentException();
        }


        v[0].set(min);
        v[1].set(min.x, max.y, min.z);
        v[2].set(max.x, max.y, min.z);
        v[3].set(max.x, min.y, min.z);

        v[4].set(max);
        v[5].set(min.x, max.y, max.z);
        v[6].set(min.x, min.y, max.z);
        v[7].set(max.x, min.y, max.z);
    }

    public ENG_Vector3D getCornerVec3(Corner corner) {
        ENG_Vector3D ret = new ENG_Vector3D();
        getCorner(corner, ret);
        return ret;
    }

    public ENG_Vector4D getCornerVec4(Corner corner) {
        ENG_Vector4D ret = new ENG_Vector4D(true);
        getCorner(corner, ret);
        return ret;
    }

    public void getCorner(Corner corner, ENG_Vector4D ret) {


        switch (corner) {
            case FAR_LEFT_BOTTOM:
                ret.set(min);
                return;
            case FAR_LEFT_TOP:
                ret.set(min.x, max.y, min.z);
                return;
            case FAR_RIGHT_BOTTOM:
                ret.set(max.x, min.y, min.z);
                return;
            case NEAR_RIGHT_BOTTOM:
                ret.set(max.x, min.y, max.z);
                return;
            case NEAR_LEFT_BOTTOM:
                ret.set(min.x, min.y, max.z);
                return;
            case NEAR_LEFT_TOP:
                ret.set(min.x, max.y, max.z);
                return;
            case NEAR_RIGHT_TOP:
                ret.set(max);
                return;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void getCorner(Corner corner, ENG_Vector3D ret) {


        switch (corner) {
            case FAR_LEFT_BOTTOM:
                ret.set(min);
                return;
            case FAR_LEFT_TOP:
                ret.set(min.x, max.y, min.z);
                return;
            case FAR_RIGHT_BOTTOM:
                ret.set(max.x, min.y, min.z);
                return;
            case NEAR_RIGHT_BOTTOM:
                ret.set(max.x, min.y, max.z);
                return;
            case NEAR_LEFT_BOTTOM:
                ret.set(min.x, min.y, max.z);
                return;
            case NEAR_LEFT_TOP:
                ret.set(min.x, max.y, max.z);
                return;
            case NEAR_RIGHT_TOP:
                ret.set(max);
                return;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void merge(ENG_AxisAlignedBox a, ENG_Vector4D temp0, ENG_Vector4D temp1) {
        if (a.isNull() || this.isInfinite()) {
            return;
        } else if (a.isInfinite()) {
            this.setInfinite();
        } else if (this.isNull()) {
            setExtents(a.min, a.max);
        } else {
		/*	if (volume() < 0.0f) {
				throw new IllegalArgumentException();
			}*/
            temp0.set(min);
            temp1.set(max);
            temp1.makeCeil(a.max);
            temp0.makeFloor(a.min);
            setExtents(temp0, temp1);
            //	System.out.println("volume: " + volume());
        }

    }

    public void merge(ENG_AxisAlignedBox a) {
        ENG_Vector4D temp0 = new ENG_Vector4D(true);
        ENG_Vector4D temp1 = new ENG_Vector4D(true);
        merge(a, temp0, temp1);
    }

    public void merge(ENG_Vector4D v) {
        switch (extent) {
            case EXTENT_NULL:
                setExtents(v, v);
                return;
            case EXTENT_FINITE:
                max.makeCeil(v);
                min.makeFloor(v);
		/*	if (volume() < 0.0f) {
				throw new IllegalArgumentException();
			}*/
                //	System.out.println("ENG_AxisAlignedBox merge(ENG_Vector4D) min: " + min);
                //	System.out.println("ENG_AxisAlignedBox merge(ENG_Vector4D) max: " + max);
                return;
            case EXTENT_INFINITE:
                return;
            default:
                throw new NullPointerException();
        }
    }

    public void merge(ENG_Vector3D v) {
        switch (extent) {
            case EXTENT_NULL:
                setExtents(v, v);
                return;
            case EXTENT_FINITE:
                max.makeCeil(v);
                min.makeFloor(v);
                return;
            case EXTENT_INFINITE:
                return;
            default:
                throw new NullPointerException();
        }
    }

    public void transform(ENG_Matrix4 mat) {
        if (extent != Extent.EXTENT_FINITE) {
            return;
        }

        ENG_Vector4D temp0 = new ENG_Vector4D(true);
        ENG_Vector4D temp1 = new ENG_Vector4D(true);
        ENG_Vector4D temp2 = new ENG_Vector4D(true);
        ENG_Vector4D temp3 = new ENG_Vector4D(true);
        temp0.set(min);
        temp1.set(max);

        setNull();

        temp2.set(temp0);
        mat.transform(temp2, temp3);
        merge(temp3);

        temp2.z = temp1.z;
        mat.transform(temp2, temp3);
        merge(temp3);

        temp2.y = temp1.y;
        mat.transform(temp2, temp3);
        merge(temp3);

        temp2.z = temp0.z;
        mat.transform(temp2, temp3);
        merge(temp3);

        temp2.x = temp1.x;
        mat.transform(temp2, temp3);
        merge(temp3);

        temp2.z = temp1.z;
        mat.transform(temp2, temp3);
        merge(temp3);

        temp2.y = temp0.y;
        mat.transform(temp2, temp3);
        merge(temp3);

        temp2.z = temp0.z;
        mat.transform(temp2, temp3);
        merge(temp3);


    }
	
/*	private ENG_Vector4D test0 = new ENG_Vector4D(true);
	private ENG_Vector4D test1 = new ENG_Vector4D(true);
	private ENG_Vector4D test2 = new ENG_Vector4D(true);
	private ENG_Vector4D test3 = new ENG_Vector4D(true);
	private ENG_Vector4D test4 = new ENG_Vector4D(true);
	private ENG_Vector4D test5 = new ENG_Vector4D(true);
	private ENG_Matrix4 testMat = new ENG_Matrix4();*/


    public void transformAffine(ENG_Matrix4 mat) {
        if (mat.isAffine()) {
            if (extent != Extent.EXTENT_FINITE) {
                return;
            }

            ENG_Vector4D temp0 = new ENG_Vector4D(true);
            ENG_Vector4D temp1 = new ENG_Vector4D(true);
            ENG_Vector4D temp2 = new ENG_Vector4D(true);
            ENG_Vector4D temp3 = new ENG_Vector4D(true);

            double[] m = mat.get();
            getCenter(temp0);
            getHalfSize(temp1);
            //	test0.set(temp0);
            //	test1.set(temp1);
            //	testMat.set(mat);
            mat.transformAffine(temp0, temp2);
            //	test2.set(temp2);
		/*	if (MainActivity.isDebugmode() && (ENG_Math.isVectorInvalid(temp0) || 
					ENG_Math.isVectorInvalid(temp1) ||
                                        ENG_Math.isVectorInvalid(temp2))) {
				System.out.println(mat);
				System.out.println("min " + min);
				System.out.println("max " + max);
				System.out.println(temp0);
				System.out.println(temp1);
				System.out.println(temp2);
			}*/


            temp0.x = Math.abs(m[0]) * temp1.x + Math.abs(m[1]) * temp1.y + Math.abs(m[2]) * temp1.z;
            temp0.y = Math.abs(m[4]) * temp1.x + Math.abs(m[5]) * temp1.y + Math.abs(m[6]) * temp1.z;
            temp0.z = Math.abs(m[8]) * temp1.x + Math.abs(m[9]) * temp1.y + Math.abs(m[10]) * temp1.z;
            //	test3.set(temp0);
            temp1.set(temp2);
            temp1.subInPlace(temp0);
            //	test4.set(temp1);
            temp3.set(temp2);
            temp3.addInPlace(temp0);
            //	test5.set(temp3);
            setExtents(temp1, temp3);
        } else {
            throw new IllegalArgumentException();
        }


    }

    public boolean intersects(ENG_AxisAlignedBox a) {
        if (isNull() || a.isNull()) {
            return false;
        }
        if (isInfinite() || a.isInfinite()) {
            return true;
        }
        if (max.x < a.min.x) {
            return false;
        }
        if (max.y < a.min.y) {
            return false;
        }
        if (max.z < a.min.z) {
            return false;
        }

        return min.x <= a.max.x && min.y <= a.max.y && min.z <= a.max.z;
    }

    public void intersection(ENG_AxisAlignedBox a, ENG_AxisAlignedBox ret) {
        if (isNull() || a.isNull()) {
            ret.setNull();
            return;
        } else if (isInfinite()) {
            ret.set(a);
            return;
        } else if (a.isInfinite()) {
            ret.set(this);
            return;
        }
        ENG_Vector4D temp0 = new ENG_Vector4D(true);
        ENG_Vector4D temp1 = new ENG_Vector4D(true);
//        ENG_Vector4D temp2 = new ENG_Vector4D(true);
//        ENG_Vector4D temp3 = new ENG_Vector4D(true);
        temp0.set(min);
        temp1.set(max);
        temp0.makeCeil(a.min);
        temp1.makeFloor(a.max);

        if (temp0.compareLessThan(temp1)) {
            ret.setExtents(temp0, temp1);
        } else {
            ret.setNull();
        }
    }

    public ENG_AxisAlignedBox intersection(ENG_AxisAlignedBox a) {
        ENG_AxisAlignedBox ret = new ENG_AxisAlignedBox();
        intersection(a, ret);
        return ret;
    }

    public double volume() {
        switch (extent) {
            case EXTENT_NULL:
                return 0.0;
            case EXTENT_FINITE:
                ENG_Vector4D temp0 = new ENG_Vector4D(true);
                max.sub(min, temp0);
                return temp0.x * temp0.y * temp0.z;
            case EXTENT_INFINITE:
                return Double.POSITIVE_INFINITY;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void scale(ENG_Vector3D v) {
        if (isFinite()) {
            min.mulInPlace(v);
            max.mulInPlace(v);
        }
    }

    public void scale(ENG_Vector4D v) {
        if (isFinite()) {
            min.mulInPlace(v);
            max.mulInPlace(v);
        }
    }

    public boolean intersects(ENG_Sphere sph) {
        return ENG_Math.intersects(sph, this);
    }

    public boolean intersects(ENG_Plane p) {
        return ENG_Math.intersects(p, this);
    }

    public boolean intersects(ENG_Vector4D v) {
        return ENG_Math.intersects(v, this);
    }

    public void getCenter(ENG_Vector4D ret) {
        if (isFinite()) {
            ret.set((min.x + max.x) * 0.5f, (min.y + max.y) * 0.5f, (min.z + max.z) * 0.5f);
        } else {
            throw new ENG_InvalidFieldStateException("Cannot determine the center " +
                    "on a not finite box. Current extent is " + extent);
        }
    }

    public ENG_Vector4D getCenter() {
        ENG_Vector4D ret = new ENG_Vector4D(true);
        getCenter(ret);
        return ret;
    }

    public void getSize(ENG_Vector4D ret) {
        switch (extent) {
            case EXTENT_NULL:
                ret.set(ENG_Math.VEC4_ZERO);
                return;
            case EXTENT_FINITE:
                max.sub(min, ret);
                return;
            case EXTENT_INFINITE:
                ret.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
                return;
            default:
                throw new IllegalArgumentException();
        }
    }

    public ENG_Vector4D getSize() {
        ENG_Vector4D ret = new ENG_Vector4D();
        getSize(ret);
        return ret;
    }

    public void getHalfSize(ENG_Vector4D ret) {
        switch (extent) {
            case EXTENT_NULL:
                ret.set(ENG_Math.VEC4_ZERO);
                return;
            case EXTENT_FINITE:
                max.sub(min, ret);
                ret.mul(0.5f);
                return;
            case EXTENT_INFINITE:
                ret.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
                return;
            default:
                throw new IllegalArgumentException();
        }
    }

    public ENG_Vector4D getHalfSize() {
        ENG_Vector4D ret = new ENG_Vector4D();
        getHalfSize(ret);
        return ret;
    }

    public boolean contains(ENG_Vector4D v) {
        return !isNull() && (isInfinite() || (min.compareLessThan(v) && max.compareGreaterThan(v)));
    }

    public boolean contains(ENG_AxisAlignedBox a) {
        return a.isNull() || isInfinite() || !(isNull() || a.isInfinite()) && (min.compareLessThan(a.min) && a.max.compareLessThan(max));

    }

    public boolean equals(ENG_AxisAlignedBox a) {
        return (min.equals(a.min) && max.equals(a.max));
    }

    public boolean notEquals(ENG_AxisAlignedBox a) {
        return (min.notEquals(a.min) || max.notEquals(a.max));
    }

    public void setExtent(Extent extent) {
        this.extent = extent;
        //        System.out.println("Extent set to " + extent);
    }

    public Extent getExtent() {
        return extent;
    }

    public String toString() {
        switch (extent) {
            case EXTENT_NULL:
                return "AxisAlignedBox(null)";
            case EXTENT_INFINITE:
                return "AxisAlignedBox(infinite)";
            case EXTENT_FINITE:
                return "AxisAlignedBox(min: " + min + " max: " + max + ")";
            default:
                throw new IllegalArgumentException();
        }
    }
}
