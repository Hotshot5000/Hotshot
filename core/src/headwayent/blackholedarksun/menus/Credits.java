/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 3/1/23, 3:51 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import static headwayent.blackholedarksun.menus.language.MenuTexts.*;

import headwayent.blackholedarksun.APP_Game;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager.ContainerFactory;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

public class Credits extends ENG_Container {

    public static class CreditsContainerFactory extends ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {

            return new Credits(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {

            c.destroy();
        }

    }

//	private String previousMenuName;

/*	public Credits(String font) {
        super(font);

	}*/

    /** @noinspection deprecation*/
    public Credits(String name, Bundle bundle/*final String previousMenuName*/) {

//		this.previousMenuName = previousMenuName;
        super(name, bundle);
        String creditsString = CREDITS_CREDITS;

        ENG_TextView titleView = (ENG_TextView) createView("title", "textview", 0.0f, 0.0f, 100.0f, 15.0f);
        ENG_TextView textView = (ENG_TextView) createView("text", "textview", 0.0f, 17.0f, 100.0f, 60.0f);

        ENG_Button mainMenuButton = (ENG_Button) createView("back", "button", 0.0f, 82.0f, 100.0f, 100.0f);

        titleView.setText(CREDITS_TITLE);
        textView.setText(creditsString, false);
        mainMenuButton.setText(CREDITS_BACK);

        titleView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
        textView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        mainMenuButton.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        mainMenuButton.setTextColor(ENG_ColorValue.WHITE);
        mainMenuButton.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);

        mainMenuButton.setOnClickListener((x, y) -> {

            ENG_ContainerManager.getSingleton().setPreviousContainer();
//				destroy();
//				MainApp.getMainThread().runOnMainThread(new ENG_IRunOnMainThread() {

//					@Override
//					public void run() {

//						ENG_ContainerManager.getSingleton().destroyContainer(getName());
//						MainApp.getGame().setPreviousMenuName(null);
//					}
//				});

//				goToMainMenu();
//				MainApp.getGame().reloadPreviousMenu();
//				if (previousMenuName != null) {
//				MenuManager.getSingleton().showPreviousMenuOverlay();
//				}
//				final MenuOverlay mainMenu =
//						MenuManager.getSingleton()
//						.getMenuOverlayByName("main_menu");
//				final ENG_ButtonOverlayElement mainMenuCredits =
//						ENG_GUIOverlayManager.getSingleton()
//						.getButtonOverlayElementByName("main_menu_Credits");
            /*mainMenu.addListener(mainMenuCredits,
                    new MenuButtonContainerActivityStarterListener(mainMenuCredits,
                            new Credits(
                                    /*MenuManager.getSingleton().getCurrentShownOverlay().getName())));*/
//				MainApp.getMainThread().runOnMainThread(new ENG_IRunOnMainThread() {

//					@Override
//					public void run() {

//				mainMenu.addListener(mainMenuCredits,
//						new MenuButtonContainerActivityStarterListener(
//								"main_menu",
//								mainMenuCredits,
//								ENG_ContainerManager.getSingleton().createContainer(
//										"Credits", "Credits", null)));
//					}
//				});

            return true;
        });
    }

//	private void goToMainMenu() {
//
//		// Reset the bundle to avoid reloading the debriefing screen
////		MainApp.getGame().goToMainMenu();
////		finish();
//	}

}
