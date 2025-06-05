/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:45 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import headwayent.blackholedarksun.MainActivity;

public class ENG_Box {

    public int left, top, right = 1, bottom = 1, front, back = 1;

    public ENG_Box() {

    }

    public ENG_Box(int left, int top, int front, int right,
                   int bottom, int back) {
        set(left, top, front, right, bottom, back);
    }

    public ENG_Box(int left, int top, int right, int bottom) {
        set(left, top, 0, right, bottom, 1);
    }

    public ENG_Box(ENG_Box box) {
        set(box);
    }

    public void set(int left, int top, int front, int right,
                    int bottom, int back) {
        if ((left > right) ||
                (top > bottom) ||
                (front > back)) {
            throw new IllegalArgumentException();
        }
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.front = front;
        this.back = back;
    }

    public void set(ENG_Box box) {
        this.left = box.left;
        this.top = box.top;
        this.right = box.right;
        this.bottom = box.bottom;
        this.front = box.front;
        this.back = box.back;
    }

    public boolean contains(ENG_Box box) {
        return ((box.left >= left) && (box.top >= top) && (box.front >= front) &&
                (box.right <= right) && (box.bottom <= bottom) && (box.back <= back));
    }

    public void intersection(ENG_Box box, ENG_Box ret) {
        ret.left = Math.max(left, box.left);
        ret.top = Math.max(top, box.top);
        ret.front = Math.max(front, box.front);
        ret.right = Math.min(right, box.right);
        ret.bottom = Math.min(bottom, box.bottom);
        ret.back = Math.min(back, box.back);
    }

    public ENG_Box intersection(ENG_Box box) {
        ENG_Box ret = new ENG_Box();
        intersection(box, ret);
        return ret;
    }

    public void intersectInPlace(ENG_Box box) {
        intersection(box, this);
    }

    public int getWidth() {
        return right - left;
    }

    public int getHeight() {
        return bottom - top;
    }

    public int getDepth() {
        return back - front;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ENG_Box) {
            ENG_Box box = (ENG_Box) obj;
            return (left == box.left) &&
                    (top == box.top) &&
                    (right == box.right) &&
                    (bottom == box.bottom) &&
                    (front == box.front) &&
                    (back == box.back);
        }
        if (MainActivity.isDebugmode()) {
            throw new ClassCastException("Parameter must be of ENG_Box type");
        } else {
            return false;
        }
    }

    public String toString() {
        return ("ENG_Box: left " + left + " top " + top + " right " + right +
                " bottom " + bottom + " front " + front + " back " + back);
    }
}
