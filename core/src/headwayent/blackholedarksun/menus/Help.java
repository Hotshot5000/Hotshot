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
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.*;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager.ContainerFactory;
import headwayent.hotshotengine.gui.simpleview.ENG_View.OnClickListener;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

import java.util.ArrayList;

public class Help extends ENG_Container {

    private static final String GUI_INFO = "gui_info";
    public static final String OVERLAY_ELEMENT_NAME = "overlay_element_name";
    public static final String MENU_NAME = "menu_name";


    public static class HelpContainerFactory extends ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {

            return new Help(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {

            c.destroy();
        }

    }

    private static class ImageViewData {
        public String imageName;
        public String text;

        public ImageViewData(String imageName, String text) {
            this.imageName = imageName;
            this.text = text;
        }

        public ImageViewData() {

        }
    }

    private final ArrayList<ImageViewData> viewDataList =
            new ArrayList<>();
    private ENG_TextView currentImageView;
    private int currentPos;

//	public Help(String name, String font) {
//		super(name, font, null);
//		
//	}

    /** @noinspection deprecation*/
    public Help(String name, final Bundle bundle) {
        super(name, bundle);


        ENG_TextView titleView = (ENG_TextView) createView("title", "textview", 0.0f, 0.0f, 100.0f, 15.0f);
        ENG_Button nextButton = (ENG_Button) createView("next", "button", 51.0f, 67.0f, 100.0f, 82.5f);
        ENG_Button previousButton = (ENG_Button) createView("prev", "button", 0.0f, 67.0f, 49.0f, 82.5f);
        ENG_Button doneButton = (ENG_Button) createView("back", "button", 0.0f, 84.5f, 100.0f, 100.0f);

        titleView.setText(HELP_TITLE);
        nextButton.setText(HELP_NEXT);
        previousButton.setText(HELP_PREVIOUS);
        doneButton.setText(HELP_BACK);

        titleView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
        nextButton.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        previousButton.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        doneButton.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        nextButton.setTextColor(ENG_ColorValue.WHITE);
        nextButton.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        previousButton.setTextColor(ENG_ColorValue.WHITE);
        previousButton.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        doneButton.setTextColor(ENG_ColorValue.WHITE);
        doneButton.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);

        nextButton.setOnClickListener(new OnClickListener() {

            private long timeElapsed;

            @Override
            public boolean onClick(int x, int y) {

//				if (ENG_Utility.hasTimePassed(timeElapsed, 500)) {
//					nextView();
//					timeElapsed = currentTimeMillis();
//				}
                nextView();
                return true;
            }
        });

        previousButton.setOnClickListener(new OnClickListener() {

            private long timeElapsed;

            @Override
            public boolean onClick(int x, int y) {

//				if (ENG_Utility.hasTimePassed(timeElapsed, 500)) {
//					previousView();
//					timeElapsed = currentTimeMillis();
//				}
                previousView();
                return true;
            }
        });

        doneButton.setOnClickListener((x, y) -> {

            ENG_ContainerManager.getSingleton().setPreviousContainer();
//				ENG_ContainerManager.getSingleton().destroyContainer(getName());
//				MainApp.getGame().setPreviousMenuName(null);
//				MenuManager.getSingleton().showPreviousMenuOverlay();
//
//				String menuName = bundle.getString(MENU_NAME);
//				String overlayElementName = bundle.getString(OVERLAY_ELEMENT_NAME);
//
//				final MenuOverlay menu = MenuManager.getSingleton()
//						.getMenuOverlayByName(menuName);
//				final ENG_ButtonOverlayElement optionsMenu = ENG_GUIOverlayManager
//						.getSingleton().getButtonOverlayElementByName(
//								overlayElementName);
//
//				menu.addListener(optionsMenu,
//						new MenuButtonContainerActivityStarterListener(menuName,
//								optionsMenu, ENG_ContainerManager.getSingleton()
//										.createContainer(overlayElementName,
//												"Help", bundle)));
            return true;
        });

        viewDataList.add(new ImageViewData(
                "", HELP_CONTROLS));
//		viewDataList.add(new ImageViewData(
//				"movement_controls_not_pressed",
//				"Press to control the direction of the ship. You can also control the ship using the movement of your phone/tablet by selecting the option in the options menu"));
//		viewDataList.add(new ImageViewData(
//				"rotate_not_pressed", 
//				"Press to rotate your ship."));
        viewDataList.add(new ImageViewData(
                "speed_meter",
//				"Swipe up or down on this bar to accelerate or decelerate your ship"
                HELP_SHIP_SPEED
        ));
//		viewDataList.add(new ImageViewData(
//				"hud_fire_0",
//				"Press to fire the current selected weapon"));
//		viewDataList.add(new ImageViewData(
//				"ab_not_pressed",
//				"Press to activate the afterburner. This will accelerate your ship to great speed for a limited time. For use especially when avoiding chasing enemy missiles."));
//		viewDataList.add(new ImageViewData(
//				"cm_not_pressed",
//				"Press to launch a countermeasure when being chased by enemy missiles. Use with afterburner to increase chances of succesfully avoiding the incoming missile."));
//		viewDataList.add(new ImageViewData(
//				"reloader_not_pressed",
//				"Press to call in the reloader to replenish your missile stock. For the reloading to happen your ship must be at a complete stand still while reloading."));
        viewDataList.add(new ImageViewData(
                "radar",
                HELP_RADAR));
        viewDataList.add(new ImageViewData(
                "crosshair_cross",
                HELP_CROSS));
        viewDataList.add(new ImageViewData(
                "crosshair_green",
                HELP_CROSSHAIR));
//		viewDataList.add(new ImageViewData(
//				"weapon_selection_not_pressed",
//				"Press to cycle between your weapons."));

        setCurrentView(0);
    }

    private void nextView() {
        if (currentPos + 1 >= viewDataList.size()) {
            setCurrentView(0);
        } else {
            setCurrentView(currentPos + 1);
        }
    }

    private void previousView() {
        if (currentPos <= 0) {
            setCurrentView(viewDataList.size() - 1);
        } else {
            setCurrentView(currentPos - 1);
        }
    }

    private void setCurrentView(int pos) {
        if (pos < 0 || pos >= viewDataList.size()) {
            throw new IllegalArgumentException(pos + " out of range 0 and " + viewDataList.size());
        }
        if (currentImageView != null) {
            destroyView(GUI_INFO);
            currentImageView = null;
        }
        ImageViewData data = viewDataList.get(pos);
        if (data.imageName.isEmpty()) {
            currentImageView = (ENG_TextView) createView(GUI_INFO, "textview", 0.0f, 17.0f, 100.0f, 65.0f);
        } else {
            currentImageView = (ENG_ImageTextView) createView(GUI_INFO, "imagetextview", 0.0f, 17.0f, 100.0f, 65.0f);
            ((ENG_ImageTextView) currentImageView).setImageName(data.imageName);

        }
        currentImageView.setText(data.text);

        currentImageView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        currentPos = pos;
    }

}
