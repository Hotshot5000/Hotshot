/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

public enum ENG_Border {
    Border_North(0),
    Border_South(1),
    Border_East(2),
    Border_West(3);

    private final int border;

    ENG_Border(int b) {
        border = b;
    }

    public int getBorder() {
        return border;
    }
}
