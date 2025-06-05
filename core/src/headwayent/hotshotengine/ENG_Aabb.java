/*
 * Created by Sebastian Bugiu on 17/02/2025, 18:26
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 17/02/2025, 18:26
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import java.util.Objects;

public class ENG_Aabb {

    public static final ENG_Aabb BOX_INFINITE = new ENG_Aabb(new ENG_Vector3D(),
            new ENG_Vector3D(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
    public static final ENG_Aabb BOX_NULL = new ENG_Aabb(new ENG_Vector3D(),
            new ENG_Vector3D(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY));
    public static final ENG_Aabb BOX_ZERO = new ENG_Aabb();

    public final ENG_Vector3D center = new ENG_Vector3D();
    public final ENG_Vector3D halfSize = new ENG_Vector3D();

    public ENG_Aabb() {

    }

    public ENG_Aabb(ENG_Vector3D center, ENG_Vector3D halfSize) {
        this.center.set(center);
        this.halfSize.set(halfSize);
    }

    public void setExtents(ENG_Vector3D min, ENG_Vector3D max) {
        if (min.x > max.x || min.y > max.y || min.z > max.z) {
            throw new IllegalArgumentException("The minimum corner of the box must be less than or equal to maximum corner");
        }
        center.set(max.add(min).mul(0.5f));
        halfSize.set(max.sub(min).mul(0.5f));
    }

    public void getMinimum(ENG_Vector3D ret) {
        ret.set(center.sub(halfSize));
    }

    public ENG_Vector3D getMinimum() {
        ENG_Vector3D ret = new ENG_Vector3D();
        getMinimum(ret);
        return ret;
    }

    public void getMaximum(ENG_Vector3D ret) {
        ret.set(center.add(halfSize));
    }

    public ENG_Vector3D getMaximum() {
        ENG_Vector3D ret = new ENG_Vector3D();
        getMaximum(ret);
        return ret;
    }

    public void getSize(ENG_Vector3D ret) {
        ret.set(halfSize.mul(2.0f));
    }

    public ENG_Vector3D getSize() {
        ENG_Vector3D ret = new ENG_Vector3D();
        getSize(ret);
        return ret;
    }

    /**
     * Merges the passed in box into the current box. The result is the
     * box which encompasses both.
     * @param aabb
     */
    public void merge(ENG_Aabb aabb) {
        ENG_Vector3D max = center.add(halfSize);
        max.makeCeil(aabb.center.add(aabb.halfSize));

        ENG_Vector3D min = center.sub(halfSize);
        max.makeFloor(aabb.center.sub(aabb.halfSize));

        if (!max.isNaN()) {
            center.set(max.add(min).mul(0.5f));
        }
        halfSize.set(max.sub(min).mul(0.5f));
    }

    /**
     * Extends the box to encompass the specified point (if needed).
     * @param point
     */
    public void merge(ENG_Vector3D point) {
        ENG_Vector3D max = center.add(halfSize);
        max.makeCeil(point);

        ENG_Vector3D min = center.sub(halfSize);
        max.makeFloor(point);

        if (!max.isNaN()) {
            center.set(max.add(min).mul(0.5f));
        }
        halfSize.set(max.sub(min).mul(0.5f));
    }

    public void transformAffine(ENG_Matrix4 mat) {
        if (!mat.isAffine()) {
            throw new IllegalArgumentException("mat is not affine");
        }
        mat.transformAffine(center);

        float x = Math.abs(halfSize.x) == Float.POSITIVE_INFINITY ? halfSize.x :
                Math.abs(mat.get(0, 0)) * halfSize.x + Math.abs(mat.get(0, 1)) * halfSize.y + Math.abs(mat.get(0, 2)) * halfSize.z;
        float y = Math.abs(halfSize.y) == Float.POSITIVE_INFINITY ? halfSize.y :
                Math.abs(mat.get(1, 0)) * halfSize.x + Math.abs(mat.get(1, 1)) * halfSize.y + Math.abs(mat.get(1, 2)) * halfSize.z;
        float z = Math.abs(halfSize.z) == Float.POSITIVE_INFINITY ? halfSize.z :
                Math.abs(mat.get(2, 0)) * halfSize.x + Math.abs(mat.get(2, 1)) * halfSize.y + Math.abs(mat.get(2, 2)) * halfSize.z;

        halfSize.set(x, y, z);
    }

    public boolean intersects(ENG_Aabb aabb) {
        ENG_Vector3D dist = center.sub(aabb.center);
        ENG_Vector3D sumHalfSizes = halfSize.add(aabb.halfSize);

        return (Math.abs(dist.x) <= sumHalfSizes.x) &&
                (Math.abs(dist.y) <= sumHalfSizes.y) &&
                (Math.abs(dist.z) <= sumHalfSizes.z);
    }

    public float volume() {
        ENG_Vector3D size = halfSize.mul(2.0f);
        return size.x * size.y * size.z;
    }

    public boolean contains(ENG_Aabb aabb) {
        ENG_Vector3D dist = center.sub(aabb.center);

        return (Math.abs(dist.x) + aabb.halfSize.x <= halfSize.x) &&
                (Math.abs(dist.y) + aabb.halfSize.y <= halfSize.y) &&
                (Math.abs(dist.z) + aabb.halfSize.z <= halfSize.z);
    }

    public boolean contains(ENG_Vector3D v) {
        ENG_Vector3D dist = center.sub(v);

        return (Math.abs(dist.x) <= halfSize.x) &&
                (Math.abs(dist.y) <= halfSize.y) &&
                (Math.abs(dist.z) <= halfSize.z);
    }

    public float distance(ENG_Vector3D v) {
        ENG_Vector3D dist = center.sub(v);

        dist.x = Math.abs(dist.x) - halfSize.x;
        dist.y = Math.abs(dist.y) - halfSize.y;
        dist.z = Math.abs(dist.z) - halfSize.z;

        return Math.max( Math.min( Math.min( dist.x, dist.y ), dist.z ), 1.0f );
    }

    public float getRadius() {
        return ENG_Math.sqrt(halfSize.dotProduct(halfSize));
    }

    public float getRadiusOrigin() {
        return center.makeAbsRet().add(halfSize).length();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ENG_Aabb)) return false;
        ENG_Aabb engAabb = (ENG_Aabb) o;
        return center.equalsFast(engAabb.center) && halfSize.equalsFast(engAabb.halfSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(center, halfSize);
    }

    @Override
    public String toString() {
        return "ENG_Aabb{" +
                "center=" + center +
                ", halfSize=" + halfSize +
                '}';
    }
}
