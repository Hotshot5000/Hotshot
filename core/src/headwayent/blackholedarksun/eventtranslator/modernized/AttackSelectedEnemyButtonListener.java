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

public class AttackSelectedEnemyButtonListener extends ENG_ButtonListenerWithState {

    private final ENG_ButtonOverlayElement attackSelectedEnemyOverlay;

    public AttackSelectedEnemyButtonListener(ENG_ButtonOverlayElement attackSelectedEnemyOverlay) {
        this.attackSelectedEnemyOverlay = attackSelectedEnemyOverlay;
    }

    @Override
    public void onClick(float x, float y, ENG_InputConvertor.TouchEventType type) {
        super.onClick(x, y, type);

        attackSelectedEnemyOverlay.setPressed(type == ENG_InputConvertor.TouchEventType.DOWN || type == ENG_InputConvertor.TouchEventType.DOWNED
                || type == ENG_InputConvertor.TouchEventType.MOVE);
    }
}
