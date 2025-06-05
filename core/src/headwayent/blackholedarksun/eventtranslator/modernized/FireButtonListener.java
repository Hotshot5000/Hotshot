/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.eventtranslator.modernized;

import headwayent.hotshotengine.gui.ENG_ButtonListenerWithState;
import headwayent.hotshotengine.gui.ENG_ButtonOverlayElement;
import headwayent.hotshotengine.input.ENG_InputConvertor;

/**
 * Created by sebas on 07.04.2016.
 */
public class FireButtonListener extends ENG_ButtonListenerWithState {

    private final ENG_ButtonOverlayElement buttonOverlay;

    public FireButtonListener(String name, ENG_ButtonOverlayElement buttonOverlay) {

        this.buttonOverlay = buttonOverlay;
    }

    @Override
    public void onClick(float x, float y, ENG_InputConvertor.TouchEventType type) {
        super.onClick(x, y, type);
//        System.out.println("FireButtonListener touch type: " + type);


        //	System.out.println("Clicked at " + x + " " + y);
        buttonOverlay.setPressed(type == ENG_InputConvertor.TouchEventType.DOWN || type == ENG_InputConvertor.TouchEventType.DOWNED
                || type == ENG_InputConvertor.TouchEventType.MOVE);

    }
}
