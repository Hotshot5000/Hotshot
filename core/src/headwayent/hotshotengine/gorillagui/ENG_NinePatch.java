/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

/**
 * Created by Sebi on 19.06.2014.
 */
public class ENG_NinePatch {

    public enum NinePatchArea {
        TOP_LEFT(0), TOP_MIDDLE(1), TOP_RIGHT(2),
        MIDDLE_LEFT(3), MIDDLE_MIDDLE(4), MIDDLE_RIGHT(5),
        BOTTOM_LEFT(6), BOTTOM_MIDDLE(7), BOTTOM_RIGHT(8);

        private final int area;

        NinePatchArea(int area) {
            this.area = area;
        }

        public int getArea() {
            return area;
        }
    }

    public enum NinePatchPoint {
        TOP_LEFT(0), TOP_MIDDLE_LEFT(1), TOP_MIDDLE_RIGHT(2), TOP_RIGHT(3),
        MIDDLE_TOP_LEFT(4), MIDDLE_TOP_MIDDLE_LEFT(5), MIDDLE_TOP_MIDDLE_RIGHT(6), MIDDLE_TOP_RIGHT(7),
        MIDDLE_BOTTOM_LEFT(8), MIDDLE_BOTTOM_MIDDLE_LEFT(9), MIDDLE_BOTTOM_MIDDLE_RIGHT(10), MIDDLE_BOTTOM_RIGHT(11),
        BOTTOM_LEFT(12), BOTTOM_MIDDLE_LEFT(13), BOTTOM_MIDDLE_RIGHT(14), BOTTOM_RIGHT(15);

        private final int point;

        NinePatchPoint(int point) {
            this.point = point;
        }

        public int getPoint() {
            return point;
        }
    }

    public final ENG_Sprite[] stretchableArea = new ENG_Sprite[9];
    public final ENG_Sprite[] drawableArea = new ENG_Sprite[9];
    public float uvTop, uvLeft, uvRight, uvBottom, spriteWidthInPixels, spriteHeightInPixels;
    public float uvTopInPixels, uvLeftInPixels, uvRightInPixels, uvBottomInPixels;

    ENG_NinePatch() {
        for (int i = 0; i < 9; ++i) {
            stretchableArea[i] = new ENG_Sprite();
            drawableArea[i] = new ENG_Sprite();
        }
    }
}
