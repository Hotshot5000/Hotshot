/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
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
public class ReloaderButtonListener extends ENG_ButtonListenerWithState {

    private final ENG_ButtonOverlayElement reloaderOverlay;

    public ReloaderButtonListener(ENG_ButtonOverlayElement reloaderOverlay) {
        this.reloaderOverlay = reloaderOverlay;
    }

    @Override
    public void onClick(float x, float y, ENG_InputConvertor.TouchEventType type) {
        super.onClick(x, y, type);


        reloaderOverlay.setPressed(type == ENG_InputConvertor.TouchEventType.DOWN || type == ENG_InputConvertor.TouchEventType.DOWNED
                || type == ENG_InputConvertor.TouchEventType.MOVE);

    }
}
