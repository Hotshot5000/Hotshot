/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.eventtranslator.modernized;

import headwayent.hotshotengine.gui.ENG_ButtonListenerWithState;
import headwayent.hotshotengine.gui.ENG_ScrollOverlayContainer;
import headwayent.hotshotengine.input.ENG_InputConvertor;

/**
 * Created by sebas on 07.04.2016.
 */
public class SpeedScrollListener extends ENG_ButtonListenerWithState {

    private final ENG_ScrollOverlayContainer container;

    public SpeedScrollListener(ENG_ScrollOverlayContainer container) {
        this.container = container;
    }

    @Override
    public void onClick(float x, float y, ENG_InputConvertor.TouchEventType type) {
        super.onClick(x, y, type);


        //	System.out.println("Clicked at " + x + " " + y);
        if (type == ENG_InputConvertor.TouchEventType.DOWN || type == ENG_InputConvertor.TouchEventType.DOWNED
                || type == ENG_InputConvertor.TouchEventType.MOVE) {
            container.scroll(x, y);
            if (container.getPercentage() == 0 || container.getPercentage() == 100) {
                setClicked(false);
            }
        } else {
            setClicked(false);
        }
    }
}
