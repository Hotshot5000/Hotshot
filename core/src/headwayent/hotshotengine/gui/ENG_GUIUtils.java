/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:45 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui;

import headwayent.blackholedarksun.MainActivity;
import headwayent.hotshotengine.ENG_RealRect;
import headwayent.hotshotengine.renderer.ENG_OverlayElement;

public class ENG_GUIUtils {

    /**
     * In case you want to stretch an overlay element over a screen area
     * It assumes that both the element and the coords are in the same "space".
     *
     * @param elem
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public static void stretchOverlay(ENG_OverlayElement elem, float left, float top,
                                      float right, float bottom) {
        elem.setLeft(left);
        elem.setTop(top);
        elem.setWidth(right - left);
        elem.setHeight(bottom - top);
    }

    public static void stretchOverlay(ENG_OverlayElement elem, ENG_RealRect rect) {
        stretchOverlay(elem, rect.left, rect.top, rect.right, rect.bottom);
    }

    public static boolean isScreenSpace(float x, float y) {
        return isScreenSpace(x, y, MainActivity.isDebugmode());
    }

    public static boolean isScreenSpace(float x, float y, boolean throwException) {
        if (x >= 0.0f && x <= 1.0f && y >= 0.0f && y <= 1.0f) {
            return true;
        }
        if (throwException) {
            throw new IllegalArgumentException(x + " " + y + " not in screen space!");
        }
        return false;
    }
}
