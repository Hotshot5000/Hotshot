/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/3/16, 3:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.android.graphics;

/**
 * Point holds two integer coordinates
 */
public class Point/* implements Parcelable*/ {
    public int x;
    public int y;

    public Point() {
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point src) {
        this.x = src.x;
        this.y = src.y;
    }

    /**
     * Set the point's x and y coordinates
     */
    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Negate the point's coordinates
     */
    public final void negate() {
        x = -x;
        y = -y;
    }

    /**
     * Offset the point's coordinates by dx, dy
     */
    public final void offset(int dx, int dy) {
        x += dx;
        y += dy;
    }

    /**
     * Returns true if the point's coordinates equal (x,y)
     */
    public final boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        return x == point.x && y == point.y;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
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
//        out.writeInt(x);
//        out.writeInt(y);
//    }
//
//    public static final Parcelable.Creator<Point> CREATOR = new Parcelable.Creator<Point>() {
//        /**
//         * Return a new point from the data in the specified parcel.
//         */
//        public Point createFromParcel(Parcel in) {
//            Point r = new Point();
//            r.readFromParcel(in);
//            return r;
//        }
//
//        /**
//         * Return an array of rectangles of the specified size.
//         */
//        public Point[] newArray(int size) {
//            return new Point[size];
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
//        x = in.readInt();
//        y = in.readInt();
//    }
}
