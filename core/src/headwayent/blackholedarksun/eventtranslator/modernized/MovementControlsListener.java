/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.eventtranslator.modernized;

import headwayent.hotshotengine.gui.ENG_ButtonListenerWithState;
import headwayent.hotshotengine.gui.ENG_ControlsOverlayElement;
import headwayent.hotshotengine.input.ENG_InputConvertor;

/**
 * Created by sebas on 07.04.2016.
 */
public class MovementControlsListener extends ENG_ButtonListenerWithState {

    private final ENG_ControlsOverlayElement controls;

    public MovementControlsListener(String name, ENG_ControlsOverlayElement element) {
        controls = element;
    }

    @Override
    public void onClick(float x, float y, ENG_InputConvertor.TouchEventType type) {
        super.onClick(x, y, type);


        //	System.out.println("Controls hit at " + x + " " + y + " " +
        //			controls.getControlsDirection());


        controls.setPressed(x, y);
    }
}
