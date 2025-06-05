/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

public class ENG_Point {

    public int x, y;

    public ENG_Point() {

    }

    public ENG_Point(int x, int y) {
        set(x, y);
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void offset(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void negate() {
        x = -x;
        y = -y;
    }

    public void limit(int left, int top, int right, int bottom) {
        if (x < left) {
            x = left;
        } else if (x >= right) {
            x = right - 1;
        }
        if (y < top) {
            y = top;
        } else if (y >= bottom) {
            y = bottom - 1;
        }
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof ENG_Point) {
            ENG_Point oth = (ENG_Point) obj;
            if (oth.x == x && oth.y == y) {
                return true;
            }
        }
        return false;
    }

    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }
}
