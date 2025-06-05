/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/3/16, 3:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.android.graphics;

import headwayent.hotshotengine.ENG_Math;


/**
 * PointF holds two float coordinates
 */
@SuppressWarnings("UnaryPlus")
public class PointF/* implements Parcelable*/ {
    public float x;
    public float y;

    public PointF() {
    }

    public PointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PointF(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    /**
     * Set the point's x and y coordinates
     */
    public final void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Set the point's x and y coordinates to the coordinates of p
     */
    public final void set(PointF p) {
        this.x = p.x;
        this.y = p.y;
    }

    public final void negate() {
        x = -x;
        y = -y;
    }

    public final void offset(float dx, float dy) {
        x += dx;
        y += dy;
    }

    /**
     * Returns true if the point's coordinates equal (x,y)
     */
    public final boolean equals(float x, float y) {
        return this.x == x && this.y == y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PointF pointF = (PointF) o;

        return Float.compare(pointF.x, x) == 0 && Float.compare(pointF.y, y) == 0;

    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PointF(" + x + ", " + y + ")";
    }

    /**
     * Return the euclidian distance from (0,0) to the point
     */
    public final float length() {
        return length(x, y);
    }

    /**
     * Returns the euclidian distance from (0,0) to (x,y)
     */
    public static float length(float x, float y) {
        return ENG_Math.sqrt(x * x + y * y);
    }

    /**
     * Parcelable interface methods
     */
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    /**
//     * Write this point to the specified parcel. To restore a point from
//     * a parcel, use readFromParcel()
//     * @param out The parcel to write the point's coordinates into
//     */
//    @Override
//    public void writeToParcel(Parcel out, int flags) {
//        out.writeFloat(x);
//        out.writeFloat(y);
//    }
//
//    public static final Parcelable.Creator<PointF> CREATOR = new Parcelable.Creator<PointF>() {
//        /**
//         * Return a new point from the data in the specified parcel.
//         */
//        public PointF createFromParcel(Parcel in) {
//            PointF r = new PointF();
//            r.readFromParcel(in);
//            return r;
//        }
//
//        /**
//         * Return an array of rectangles of the specified size.
//         */
//        public PointF[] newArray(int size) {
//            return new PointF[size];
//        }
//    };
//
//    /**
//     * Set the point's coordinates from the data stored in the specified
//     * parcel. To write a point to a parcel, call writeToParcel().
//     *
//     * @param in The parcel to read the point's coordinates from
//     */
//    public void readFromParcel(Parcel in) {
//        x = in.readFloat();
//        y = in.readFloat();
//    }
}
