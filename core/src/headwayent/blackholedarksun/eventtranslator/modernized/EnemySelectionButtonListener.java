/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/3/21, 4:54 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.eventtranslator.modernized;

import headwayent.hotshotengine.gui.ENG_ButtonListenerWithState;
import headwayent.hotshotengine.gui.ENG_ButtonOverlayElement;
import headwayent.hotshotengine.input.ENG_InputConvertor;

public class EnemySelectionButtonListener  extends ENG_ButtonListenerWithState {

    public enum EnemySelectionDirection {
        PREVIOUS, NEXT
    }

    private final ENG_ButtonOverlayElement enemySelectionButton;

    public EnemySelectionButtonListener(String name, ENG_ButtonOverlayElement enemySelectionButton,
                                        EnemySelectionDirection dir) {
        this.enemySelectionButton = enemySelectionButton;
    }

    @Override
    public void onClick(float x, float y, ENG_InputConvertor.TouchEventType type) {
        super.onClick(x, y, type);


        enemySelectionButton.setPressed(type == ENG_InputConvertor.TouchEventType.DOWN || type == ENG_InputConvertor.TouchEventType.DOWNED
                /*|| type == ENG_InputConvertor.TouchEventType.MOVE*/);
//        System.out.println("EnemySelectionButtonListener clicked with type: " + type);
    }
}
