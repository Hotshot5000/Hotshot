/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/22/21, 9:43 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menuresource.Menu;
import headwayent.blackholedarksun.menuresource.MenuSelection;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.menusystemsimpleview.viewlisteners.OnCharacterListenerWithType;
import headwayent.blackholedarksun.menusystemsimpleview.viewlisteners.OnClickListenerWithType;
import headwayent.blackholedarksun.menusystemsimpleview.viewlisteners.OnKeyCodeListenerWithType;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager.ContainerFactory;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView.HorizontalAlignment;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView.VerticalAlignment;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GenericMenu extends ENG_Container {

    public static final String BUNDLE_SPECIFIC_HEIGHTS = "specific_heights";
    public static final String BUNDLE_BUTTON_DIFF_DISTANCE = "button_diff_distance";
    public static final String BUNDLE_TITLE_HEIGHT = "title_height";
    public static final String BUNDLE_BOTTOM_BUTTONS_HEIGHT = "bottom_buttons_height";

    private static final float BUTTON_DIFF_DISTANCE = 1.0f;
    private static final float TITLE_HEIGHT = 15.0f;
    private static final float BUTTON_HEIGHT = 100.0f - TITLE_HEIGHT;
    public static final String BUNDLE_MENU = "menu";

    public static class GenericMenuFactory extends ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {

            return new GenericMenu(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {

            c.destroy();
        }

    }

    private final HashMap<String, ENG_Button> buttonMap = new HashMap<>();
    private final ArrayList<ENG_Button> buttonList = new ArrayList<>();
    private final ENG_ColorValue buttonBackgroundColor = new ENG_ColorValue(ENG_ColorValue.ZERO);
    private float borderWidth;
    private final ENG_ColorValue borderColor = new ENG_ColorValue(ENG_ColorValue.WHITE);
    private final ENG_ColorValue textColor = new ENG_ColorValue(ENG_ColorValue.WHITE);


    private float titleHeight;
    private float bottomButtonsHeight;
    private final float buttonsHeight;
    private final float buttonsDiffDistance;
    private final float bottomsButtonsBeginningPos;

//	public GenericMenu(String name, String font) {
//		super(name, font, null);
//		
//	}

    /** @noinspection deprecation*/
    public GenericMenu(String name, Bundle bundle) {
        super(name, bundle);
        
        Menu menu = (Menu) bundle.get(BUNDLE_MENU);

        boolean shouldOffset = MainApp.getGame().getNotchHeight() > 0.0;

        float titleTop =  (shouldOffset ? NOTCH_VERTICAL_OFFSET : 0.0f);
        if (bundle.getBoolean(BUNDLE_SPECIFIC_HEIGHTS)) {
            titleHeight = bundle.getFloat(BUNDLE_TITLE_HEIGHT, -1.0f);
            if (titleHeight == -1.0f) {
                throw new IllegalArgumentException("title height not specified");
            }
            titleHeight += (shouldOffset ? NOTCH_VERTICAL_OFFSET : 0.0f);
            bottomButtonsHeight = bundle.getFloat(BUNDLE_BOTTOM_BUTTONS_HEIGHT, -1.0f);
            if (bottomButtonsHeight == -1.0f) {
                throw new IllegalArgumentException("bottoms buttons height not specified");
            }
            bottomButtonsHeight -= (shouldOffset ? NOTCH_VERTICAL_OFFSET : 0.0f);
            buttonsDiffDistance = bundle.getFloat(BUNDLE_BUTTON_DIFF_DISTANCE, -1.0f);
            if (buttonsDiffDistance == -1.0f) {
                throw new IllegalArgumentException("buttons diff distance not specified");
            }
            buttonsHeight = 100.0f - titleHeight - titleTop - bottomButtonsHeight - (shouldOffset ? NOTCH_VERTICAL_OFFSET : 0.0f);
        } else {
            titleHeight = TITLE_HEIGHT + (shouldOffset ? NOTCH_VERTICAL_OFFSET : 0.0f);
            buttonsHeight = BUTTON_HEIGHT - titleTop - (shouldOffset ? NOTCH_VERTICAL_OFFSET : 0.0f);
            buttonsDiffDistance = BUTTON_DIFF_DISTANCE;
        }

        ENG_TextView titleView = (ENG_TextView) createView("title", "textview", 0.0f, titleTop, 100.0f, titleHeight, false, false);

        titleView.setText(menu.menuTitle);

        titleView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);

        titleView.setHorizontalAlignment(HorizontalAlignment.CENTER);
        titleView.setVerticalAlignment(VerticalAlignment.CENTER);

        // We assume that you don't mix the valid selections with the
        // back to previous menu selections. Put the back buttons at the end of the
        // selections list.
        ArrayList<MenuSelection> validSelections = menu.getValidSelections();
        ArrayList<MenuSelection> previousMenuSelections =
                menu.getGoToPreviousMenuSelections();
        float buttonHeight = buttonsHeight / menu.selectionList.size() - buttonsDiffDistance;
        float currentTop = titleHeight;
        for (MenuSelection menuSelection : validSelections) {
            ENG_Button button = (ENG_Button) createView(
                    menuSelection.name, "button", (shouldOffset ? NOTCH_HORIZONTAL_OFFSET : 0.0f),
                    currentTop,
                    (shouldOffset ? 100.0f - NOTCH_HORIZONTAL_OFFSET : 100.0f), currentTop + buttonHeight, false, false);
            button.setText(menuSelection.name);
            button.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
            buttonMap.put(menuSelection.name, button);
            buttonList.add(button);
            currentTop += buttonHeight + buttonsDiffDistance;
        }
        for (MenuSelection menuSelection : previousMenuSelections) {
            ENG_Button button = (ENG_Button) createView(
                    menuSelection.name, "button", (shouldOffset ? NOTCH_HORIZONTAL_OFFSET : 0.0f),
                    currentTop + buttonsDiffDistance,
                    (shouldOffset ? 100.0f - NOTCH_HORIZONTAL_OFFSET : 100.0f), currentTop + buttonHeight, false, false);
            button.setText(menuSelection.name);
            button.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
            button.setTextColor(ENG_ColorValue.WHITE);
            button.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
            button.setOnClickListener((x, y) -> {
                ENG_ContainerManager.getSingleton().setPreviousContainer();
                return true;
            });
            currentTop += buttonHeight;
        }
        bottomsButtonsBeginningPos = currentTop + buttonsDiffDistance;
        setButtonBackgroundColor(buttonBackgroundColor);
        setTextColor(textColor);
    }

    public void setOnClickListener(String buttonName, OnClickListenerWithType listener) {
        getButton(buttonName).setOnClickListener(listener);
    }

    public void setOnKeyCodeListener(String buttonName, OnKeyCodeListenerWithType listener) {
        getButton(buttonName).setOnKeyCodeListener(listener);
    }

    public void setOnCharacterListener(String buttonName, OnCharacterListenerWithType listener) {
        getButton(buttonName).setOnCharacterListener(listener);
    }

    public ENG_Button getButton(int num) {
        if (num < 0 || num >= buttonList.size()) {
            throw new IllegalArgumentException(num + " is not valid button number." +
                    " Must be between 0 and " + buttonList.size());
        }
        return buttonList.get(num);
    }

    public ArrayList<ENG_Button> getButtonList() {
        return buttonList;
    }

    public ENG_Button getButton(String name) {
        ENG_Button button = buttonMap.get(name);
        if (button == null) {
            throw new IllegalArgumentException(name + " is an invalid " +
                    "button name");
        }
        return button;
    }

    public HashMap<String, ENG_Button> getButtonMap() {
        return buttonMap;
    }

    @Override
    public void onRecreation(ENG_Container previousContainer) {
        super.onRecreation(previousContainer);
        GenericMenu prev = (GenericMenu) previousContainer;
        for (Map.Entry<String, ENG_Button> entry : prev.getButtonMap().entrySet()) {

            OnClickListenerWithType onClickListener = (OnClickListenerWithType) entry.getValue().getOnClickListener();
            OnKeyCodeListenerWithType onKeyCodeListener = (OnKeyCodeListenerWithType) entry.getValue().getOnKeyCodeListener();
            OnCharacterListenerWithType onCharacterListener = (OnCharacterListenerWithType) entry.getValue().getOnCharacterListener();

            if (onClickListener != null) {
                setOnClickListener(entry.getKey(), SimpleViewGameMenuManager.getSingleton().createOnClickListenerWithType(
                        onClickListener.getType(), onClickListener.getBundle()));
            }
            if (onKeyCodeListener != null) {
                setOnKeyCodeListener(entry.getKey(), SimpleViewGameMenuManager.getSingleton().createOnKeyCodeListenerWithType(
                                onKeyCodeListener.getType(), onKeyCodeListener.getBundle()));
            }
            if (onCharacterListener != null) {
                setOnCharacterListener(entry.getKey(), SimpleViewGameMenuManager.getSingleton().createOnCharacterListenerWithType(
                                onCharacterListener.getType(), onCharacterListener.getBundle())
                );
            }
        }
        // Automatically recreated
        recreateContainerListeners(previousContainer);
//        for (ContainerListener l : previousContainer.getListeners()) {
//            ENG_ContainerManager.getSingleton().createContainerListener(this, l.getType(), null);
//        }
    }

    //	@Override
//	public void update() {
//
//		super.update();
//	}

    public void setButtonBackgroundColor(ENG_ColorValue c) {
        buttonBackgroundColor.set(c);
        for (ENG_Button button : buttonMap.values()) {
            button.setBackgroundColor(c);
        }
        markDirty();
    }

    public void getButtonBackgroundColor(ENG_ColorValue c) {
        c.set(buttonBackgroundColor);
    }

    public ENG_ColorValue getButtonBackgroundColor() {
        return new ENG_ColorValue(buttonBackgroundColor);
    }

    public void setBorderWidth(float width) {
        borderWidth = width;
//		borderColor.set(c);
        for (ENG_Button button : buttonMap.values()) {
            button.setBorderWidth(width);
        }
        markDirty();
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderColor(ENG_ColorValue c) {
        borderColor.set(c);
        for (ENG_Button button : buttonMap.values()) {
            button.setBorderColor(c);
        }
        markDirty();
    }

    public void getBorderColor(ENG_ColorValue ret) {
        ret.set(borderColor);
    }

    public ENG_ColorValue getBorderColor() {
        return new ENG_ColorValue(borderColor);
    }

    public void setTextColor(ENG_ColorValue c) {
        textColor.set(c);
        for (ENG_Button button : buttonMap.values()) {
            button.setTextColor(c);
        }
        markDirty();
    }

    public void getTextColor(ENG_ColorValue ret) {
        ret.set(textColor);
    }

    public ENG_ColorValue getTextColor() {
        return new ENG_ColorValue(textColor);
    }

    public float getTitleHeight() {
        return titleHeight;
    }

    public float getBottomButtonsHeight() {
        return bottomButtonsHeight;
    }

    public float getButtonsHeight() {
        return buttonsHeight;
    }

    public float getButtonsDiffDistance() {
        return buttonsDiffDistance;
    }

    public float getBottomsButtonsBeginningPos() {
        return bottomsButtonsBeginningPos;
    }
}
