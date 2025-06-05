/*
 * Created by Sebastian Bugiu on 24/09/2024, 13:56
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 24/09/2024, 13:56
 * Copyright (c) 2024.
 * All rights reserved.
 */

package headwayent.blackholedarksun.eventtranslator.modernized;

import headwayent.hotshotengine.gui.ENG_ButtonListenerWithState;
import headwayent.hotshotengine.gui.ENG_ButtonOverlayElement;
import headwayent.hotshotengine.input.ENG_InputConvertor;

public class DefendPlayerShipButtonListener extends ENG_ButtonListenerWithState {

    private final ENG_ButtonOverlayElement defendPlayerShipOverlay;

    public DefendPlayerShipButtonListener(ENG_ButtonOverlayElement defendPlayerShipOverlay) {
        this.defendPlayerShipOverlay = defendPlayerShipOverlay;
    }

    @Override
    public void onClick(float x, float y, ENG_InputConvertor.TouchEventType type) {
        super.onClick(x, y, type);

        defendPlayerShipOverlay.setPressed(type == ENG_InputConvertor.TouchEventType.DOWN || type == ENG_InputConvertor.TouchEventType.DOWNED
                || type == ENG_InputConvertor.TouchEventType.MOVE);
    }
}
