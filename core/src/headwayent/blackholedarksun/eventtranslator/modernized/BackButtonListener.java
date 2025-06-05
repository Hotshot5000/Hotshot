/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/18/21, 4:58 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.eventtranslator.modernized;

import headwayent.hotshotengine.gui.ENG_ButtonListenerWithState;
import headwayent.hotshotengine.gui.ENG_ButtonOverlayElement;
import headwayent.hotshotengine.input.ENG_InputConvertor;

public class BackButtonListener extends ENG_ButtonListenerWithState {

    private final ENG_ButtonOverlayElement backButtonOverlay;

    public BackButtonListener(String name, ENG_ButtonOverlayElement backButtonOverlay) {
        this.backButtonOverlay = backButtonOverlay;
    }

    @Override
    public void onClick(float x, float y, ENG_InputConvertor.TouchEventType type) {
        super.onClick(x, y, type);

        backButtonOverlay.setPressed(type == ENG_InputConvertor.TouchEventType.DOWN || type == ENG_InputConvertor.TouchEventType.DOWNED
                || type == ENG_InputConvertor.TouchEventType.MOVE);
    }
}
