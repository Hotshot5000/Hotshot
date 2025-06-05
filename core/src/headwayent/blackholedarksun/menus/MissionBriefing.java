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
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager.ContainerFactory;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

public class MissionBriefing extends ENG_Container {

    public static final String DONE_BUTTON = "done";

    public static class MissionBriefingContainerFactory extends ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {

            return new MissionBriefing(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {

            c.destroy();
        }

    }

    public static final String BUNDLE = "bundle";

    private final int levelNum;

    /** @noinspection deprecation*/
    public MissionBriefing(String name, Bundle bundle) {
        
        super(name, bundle);
        String title = bundle.getString("title");
        if (title == null) {
            throw new IllegalArgumentException("title not found");
        }
        String text = bundle.getString("text");
        if (text == null) {
            throw new IllegalArgumentException("text not found");
        }
        levelNum = bundle.getInt("level");

        ENG_TextView titleView = (ENG_TextView) createView("title", "textview", 0.0f, 0.0f, 100.0f, 20.0f);
        ENG_TextView textView = (ENG_TextView) createView("text", "textview", 0.0f, 22.0f, 100.0f, 80.0f);
        ENG_Button doneView = (ENG_Button) createView(DONE_BUTTON, "button", 0.0f, 82.0f, 100.0f, 100.0f);

        titleView.setText(title);
        textView.setText(text);
        doneView.setText(MISSION_BRIEFING_DONE);

        titleView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
        textView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        doneView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        doneView.setTextColor(ENG_ColorValue.WHITE);
        doneView.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);


        doneView.setOnClickListener((x, y) -> {

//				destroy();
//				ENG_ContainerManager.getSingleton().destroyContainer(getName());
//            MainApp.getGame().setPreviousMenuName(null);
//            MainApp.getGame().reenableDemo(false);
            WorldManager.getSingleton().setSelectedLevel(levelNum);
            SimpleViewGameMenuManager.updateMenuState(SimpleViewGameMenuManager.MenuState.IN_SHIP_SELECTION);
            return true;
        });
    }

    @Override
    public void onRecreation(ENG_Container previousContainer) {
        super.onRecreation(previousContainer);
        recreateContainerListeners(previousContainer);
    }
}
