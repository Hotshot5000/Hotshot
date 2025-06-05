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
public class WeaponSelectionButtonListener extends ENG_ButtonListenerWithState {

    public enum WeaponSelectionDirection {
        PREVIOUS, NEXT
    }

    private final ENG_ButtonOverlayElement weapon;

    public WeaponSelectionButtonListener(String name, ENG_ButtonOverlayElement weapon,
                                         WeaponSelectionDirection dir) {
        this.weapon = weapon;
    }

    @Override
    public void onClick(float x, float y, ENG_InputConvertor.TouchEventType type) {
        super.onClick(x, y, type);


        weapon.setPressed(type == ENG_InputConvertor.TouchEventType.DOWN || type == ENG_InputConvertor.TouchEventType.DOWNED
                /*|| type == ENG_InputConvertor.TouchEventType.MOVE*/);
//        System.out.println("WeaponSelectionButtonListener clicked with type: " + type);
    }
}
