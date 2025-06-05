/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

public enum ENG_QuadCorner {
    TopLeft(0),
    TopRight(1),
    BottomRight(2),
    BottomLeft(3);

    private final int corner;

    ENG_QuadCorner(int c) {
        corner = c;
    }

    public int getCorner() {
        return corner;
    }
}
