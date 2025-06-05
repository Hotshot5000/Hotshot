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
import headwayent.blackholedarksun.levelresource.LevelEvent;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

/**
 * Created by sebas on 05.07.2016.
 */
public class MultiplayerLevelEnded extends MultiplayerEventFinalization {

    public static class MultiplayerLevelEndedContainerFactory extends ENG_ContainerManager.ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            return new MultiplayerLevelEnded(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            c.destroy();
        }
    }

    /** @noinspection deprecation*/
    public MultiplayerLevelEnded(String name, Bundle bundle) {
        super(name, bundle);

        LevelEvent.EventState eventState = (LevelEvent.EventState) bundle.getObject("eventState");
        boolean connectionFailed = eventState == LevelEvent.EventState.CONNECTION_LOST;
        if (connectionFailed) {
            serverTableView.setVisible(false);
            ENG_TextView disconnectedText = (ENG_TextView) createView("disconnected", "textview", 0.0f, 30.0f, 100.0f, 40.0f);
            disconnectedText.setText(MULTIPLAYER_LEVEL_ENDED_THE_CONNECTION_WITH_THE_SERVER_HAS_FAILED);
            disconnectedText.setTextColor(ENG_ColorValue.WHITE);
            disconnectedText.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        }
//        showToast(eventState.toString());

//        ENG_Button ok = (ENG_Button) createView("ok", "button", 0.0f, 82.0f, 100.0f, 90.0f);
        ENG_Button goToMainMenu = (ENG_Button) createView("mainMenu", "button", 0.0f, 92.0f, 100.0f, 100.0f);

        titleView.setText(connectionFailed ? MULTIPLAYER_LEVEL_ENDED_DISCONNECTED : MULTIPLAYER_LEVEL_ENDED_BATTLE_ENDED);

//        ok.setText("Done");
        goToMainMenu.setText(MULTIPLAYER_LEVEL_ENDED_MAIN_MENU);
//        ok.setTextColor(ENG_ColorValue.WHITE);
        goToMainMenu.setTextColor(ENG_ColorValue.WHITE);

//        ok.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        goToMainMenu.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

//        ok.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        goToMainMenu.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);

//        ok.setOnClickListener(new ENG_View.OnClickListener() {
//
//            @Override
//            public boolean onClick(int x, int y) {
//
//                NetManager.getSingleton().respawnPlayer();
//                ENG_ContainerManager.getSingleton().removeCurrentContainer();
//                return true;
//            }
//        });

        goToMainMenu.setOnClickListener((x, y) -> {
            SimpleViewGameMenuManager.setCurrentMenu(SimpleViewGameMenuManager.MAIN_MENU);
            WorldManager.getSingleton().resetToWorldManagerSP();
            return true;
        });
    }
}
