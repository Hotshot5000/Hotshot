/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/30/21, 8:27 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

public class ENG_TouchEvent {

    public enum TouchAction {
        DOWN, UP, MOVE, NONE
    }

    public final float x;
    public final float y;
    public final float dx;
    public final float dy;
    public final int pointerId;
    public final TouchAction action;

    public ENG_TouchEvent(float x, float y, float dx, float dy, int pointerId, TouchAction action) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.pointerId = pointerId;
        this.action = action;
//        System.out.println("TouchEvent x: " + x + " y: " + y + " dx: " + dx + " dy: " + dy + " action: " + action + " currentTime: " + ENG_Utility.nanoTime());
    }
}
