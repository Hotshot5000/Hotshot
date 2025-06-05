/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/7/16, 3:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import static headwayent.blackholedarksun.menus.language.MenuTexts.*;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView;

/**
 * Created by sebas on 19.05.2016.
 */
public class FatalErrorMenu extends ENG_Container {

    public static final String ERROR_STR = "error_str";

    public static class FatalErrorMenuContainerFactory extends ENG_ContainerManager.ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            return new FatalErrorMenu(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            c.destroy();
        }
    }

    /** @noinspection deprecation*/
    public FatalErrorMenu(String name, Bundle bundle) {
        super(name, bundle);

        String errorStr = bundle.getString(ERROR_STR);

        ENG_TextView titleView = (ENG_TextView) createView("title", "textview", 0.0f, 0.0f, 100.0f, 15.0f);
        ENG_TextView textView = (ENG_TextView) createView("text", "textview", 0.0f, 17.0f, 100.0f, 60.0f);

        ENG_Button mainMenuButton = (ENG_Button) createView("back", "button", 0.0f, 82.0f, 100.0f, 100.0f);

        titleView.setText(FATAL_ERROR_TITLE);
        textView.setText(errorStr);
        mainMenuButton.setText(FATAL_ERROR_BACK);

        titleView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
        textView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        mainMenuButton.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        mainMenuButton.setOnClickListener((x, y) -> {
            MainApp.getGame().exitGame();
            return true;
        });
    }
}
