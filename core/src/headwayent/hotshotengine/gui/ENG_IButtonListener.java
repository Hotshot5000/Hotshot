/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:01 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui;

import headwayent.hotshotengine.input.ENG_InputConvertor.TouchEventType;


public interface ENG_IButtonListener {

    /**
     * Listener for each button action. Coordinates must be in "screen space"
     * between 0 and 1
     *
     * @param x
     * @param y
     * @param eventType
     */
    void onClick(float x, float y, TouchEventType type);
}
