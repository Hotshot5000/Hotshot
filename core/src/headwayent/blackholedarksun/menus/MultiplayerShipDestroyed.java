/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/22/21, 9:43 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import static headwayent.blackholedarksun.menus.language.MenuTexts.*;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.net.NetManager;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.*;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

/**
 * Created by sebas on 27.01.2016.
 */
public class MultiplayerShipDestroyed extends MultiplayerEventFinalization {

    public static class MultiplayerShipDestroyedContainerFactory extends ENG_ContainerManager.ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            return new MultiplayerShipDestroyed(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            c.destroy();
        }
    }

    /** @noinspection deprecation*/
    public MultiplayerShipDestroyed(String name, Bundle bundle) {
        super(name, bundle);


        ENG_Button ok = (ENG_Button) createView("ok", "button", 0.0f, 92.0f, 100.0f, 100.0f);
//        ENG_Button cancel = (ENG_Button) createView("cancel", "button", 0.0f, 92.0f, 100.0f, 100.0f);

        titleView.setText(MULTIPLAYER_SHIP_DESTROYED_TITLE);

        ok.setText(MULTIPLAYER_SHIP_DESTROYED_REENTER_BATTLE);
//        cancel.setText("Cancel");
        ok.setTextColor(ENG_ColorValue.WHITE);
//        cancel.setTextColor(ENG_ColorValue.WHITE);

        ok.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
//        cancel.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        ok.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
//        cancel.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);

        ok.setOnClickListener((x, y) -> {

            NetManager.getSingleton().respawnPlayer();
            ENG_ContainerManager.getSingleton().removeCurrentContainer();
            return true;
        });

//        cancel.setOnClickListener(new ENG_View.OnClickListener() {
//
//            @Override
//            public boolean onClick(int x, int y) {
//                
//                onBackPressed();
//                return true;
//            }
//        });
    }

}
