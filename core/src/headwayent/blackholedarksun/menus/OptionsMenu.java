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
import headwayent.blackholedarksun.components.CameraProperties.CameraType;
import headwayent.blackholedarksun.input.InGameInputConvertorListener;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_Checkbox;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager.ContainerFactory;
import headwayent.hotshotengine.gui.simpleview.ENG_Slider;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView;
import headwayent.hotshotengine.input.ENG_InputManager;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.microedition.rms.InvalidRecordIDException;
import headwayent.microedition.rms.RecordEnumeration;
import headwayent.microedition.rms.RecordStore;
import headwayent.microedition.rms.RecordStoreException;
import headwayent.microedition.rms.RecordStoreNotFoundException;

public class OptionsMenu extends ENG_Container {

    public static final String OVERLAY_ELEMENT_NAME = "overlay_element_name";
    public static final String MENU_NAME = "menu_name";

    public static class OptionsMenuContainerFactory extends ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {

            return new OptionsMenu(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {

            c.destroy();
        }

    }

    //	public OptionsMenu(String name, String font) {
//		super(name, font, null);
//		
//	}

    /** @noinspection deprecation */
    public OptionsMenu(String name, Bundle bundle) {
        super(name, bundle);


        Bundle sliderBundle = new Bundle();
        sliderBundle.putString(ENG_Slider.BUNDLE_SLIDER_TYPE, "horizontal");
        sliderBundle.putString(ENG_Slider.BUNDLE_VALUE_TYPE, "int");
        sliderBundle.putInt(ENG_Slider.BUNDLE_MIN_VALUE, (int) InGameInputConvertorListener.MIN_SHIP_SENSITIVITY);
        sliderBundle.putInt(ENG_Slider.BUNDLE_MAX_VALUE, (int) InGameInputConvertorListener.MAX_SHIP_SENSITIVITY);
        sliderBundle.putInt(ENG_Slider.BUNDLE_ITEM_STEP, 1);

        ENG_TextView titleView = (ENG_TextView) createView("title", "textview", 0.0f, 0.0f, 100.0f, 8.0f);
        final ENG_Checkbox sound = (ENG_Checkbox) createView("sounds", "checkbox", 0.0f, 10.0f, 100.0f, 18.0f);
        final ENG_Checkbox accel = (ENG_Checkbox) createView("accel", "checkbox", 0.0f, 20.0f, 100.0f, 28.0f);
        final ENG_Checkbox invY = (ENG_Checkbox) createView("invy", "checkbox", 0.0f, 30.0f, 100.0f, 38.0f);
        // TODO 3rd person camera needs physics support so that we don't clip through behind objects. Disable for now.
        final ENG_Checkbox cameraView = (ENG_Checkbox) createView("cameraview", "checkbox", 0.0f, 40.0f, 100.0f, 48.0f);
        final ENG_Checkbox vibration = (ENG_Checkbox) createView("vibration", "checkbox", 0.0f, 50.0f, 100.0f, 59.0f);
        final ENG_Checkbox aimAssist = (ENG_Checkbox) createView("aimassist", "checkbox", 0.0f, 60.0f, 100.0f, 68.0f);
        final ENG_TextView sensitivitySliderText = (ENG_TextView) createView("sensitivityText", "textview", 0.0f, 74.0f, 30.0f, 78.0f);
        final ENG_Slider sensitivitySlider = (ENG_Slider) createView("sensitivity", "slider", 32.0f, 72.0f, 100.0f, 76.0f, sliderBundle);
        // TODO add language selection.
        ENG_Button ok = (ENG_Button) createView("ok", "button", 50.5f, 80.0f, 100.0f, 96.0f);
        ENG_Button cancel = (ENG_Button) createView("cancel", "button", 0.0f, 80.0f, 49.5f, 96.0f);

        titleView.setText(OPTIONS_TITLE);
        sound.setText(OPTIONS_SOUNDS);
        accel.setText(OPTIONS_USE_PHONE_INCLINATION_TO_CONTROL_PLAYER);
        invY.setText(OPTIONS_INVERT_Y_AXIS);
        cameraView.setText(OPTIONS_SET_THIRD_PERSON_CAMERA);
        vibration.setText(OPTIONS_FORCE_FEEDBACK);
        aimAssist.setText(OPTIONS_AIM_ASSIST);
        sensitivitySliderText.setText(OPTIONS_SENSITIVITY);
        ok.setText(OPTIONS_OK);
        cancel.setText(OPTIONS_CANCEL);

        titleView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
        sound.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        accel.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        invY.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        cameraView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        vibration.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        aimAssist.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        sensitivitySliderText.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        ok.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        cancel.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        ok.setTextColor(ENG_ColorValue.WHITE);
        ok.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        cancel.setTextColor(ENG_ColorValue.WHITE);
        cancel.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);

        sound.setChecked(MainApp.getGame().isSoundsEnabled());
        accel.setChecked(MainApp.getGame().isAccelerometerEnabled());
        invY.setChecked(MainApp.getGame().isInvertYAxis());
        final boolean thirdPersonCamera = MainApp.getGame().isThirdPersonCamera();
        cameraView.setChecked(MainApp.getGame().isThirdPersonCamera());
        vibration.setChecked(MainApp.getGame().isVibrationEnabled());
        aimAssist.setChecked(MainApp.getGame().getPreferences().isAimAssistEnabled());

        ENG_InputManager inputManager = MainApp.getGame().getInputManager();
        final InGameInputConvertorListener inputConvertorListener =
                (InGameInputConvertorListener) inputManager.getInputConvertorListener(APP_Game.TO_IN_GAME_LISTENER);
        // Slider type is int but inputConvertorListener returns float.
        // Make sure to always cast to int before using sensitivitySlider.
        sensitivitySlider.setSliderValue((int) inputConvertorListener.getSensitivity());

        ok.setOnClickListener((x, y) -> {


            MainApp.getGame().setSoundsEnabled(sound.isChecked());
            MainApp.getGame().setAccelerometerEnabled(accel.isChecked());
            MainApp.getGame().setInvertYAxis(invY.isChecked());
            MainApp.getGame().setThirdPersonCamera(cameraView.isChecked());
            MainApp.getGame().setVibrationEnabled(vibration.isChecked());
            MainApp.getGame().getPreferences().setAimAssistEnabled(aimAssist.isChecked());
            MainApp.getGame().setAimAssistEnabled(aimAssist.isChecked());

            if (thirdPersonCamera != cameraView.isChecked()// &&
                    /*WorldManager.getSingleton().getLevelState() ==
                    LevelState.STARTED*/) {
                // We must change the camera if in game
                WorldManager.getSingleton().setCameraType(cameraView.isChecked() ? CameraType.THIRD_PERSON : CameraType.FIRST_PERSON);
//					if (cameraView.isChecked()) {
//
//					} else {
//
//					}
            }
            int sensitivity = (int) sensitivitySlider.getCurrentValue();
            if (((int) inputConvertorListener.getSensitivity()) != sensitivity) {
                MainApp.getGame().getPreferences().setShipSensitivity(sensitivity);
                inputConvertorListener.setSensitivity(sensitivity);
            }
            // First set the new variables then recreate the screen

            byte[] b = new byte[8];
//                b[0] = (byte) shipRadioIndex;

            if (MainApp.getGame().getSound().isEnabled()) {
                b[1] = (byte) 1;
            } else {
                b[1] = (byte) 0;
            }

            if (MainApp.getGame().isAccelerometerEnabled()) {
                b[2] = (byte) 1;
            } else {
                b[2] = (byte) 0;
            }
//                b[3] = (byte) orientationRadioIndex;
            b[4] = MainApp.getGame().isInvertYAxis() ? (byte) 1 : (byte) 0;
            b[5] = MainApp.getGame().isThirdPersonCamera() ? (byte) 1 : (byte) 0;
            b[6] = MainApp.getGame().isVibrationEnabled() ? (byte) 1 : (byte) 0;
//game.setShipOptionsIndex(shipRadioIndex);
//game.setOrientationIndex(orientationRadioIndex);
            try {
                RecordStore rs = RecordStore.openRecordStore(
//                                MainActivity.getInstance(),
                        "shipoptions", true);
                RecordEnumeration rsEnum = rs.enumerateRecords(null, null, true);
                while (rsEnum.hasNextElement()) {
                    try {
                        rs.deleteRecord(rsEnum.nextRecordId());
                    } catch (InvalidRecordIDException e) {

                        e.printStackTrace();
                    } catch (RecordStoreException e) {
                        e.printStackTrace();
                    }
                }
                rsEnum.rebuild();
                rs.addRecord(b, 0, b.length);
                rsEnum.destroy();
                rs.closeRecordStore();
            } catch (RecordStoreException e) {
                //    gameEngine.errorMsg("Could not save the ship options! You will need to reset them the next time " +
                //            "you play the game");
//                        Toast.makeText(MainActivity.getInstance(),
//                                "Could not save the options! You will need to reset "
//                                + "them the next time "
//                                + "you play the game", Toast.LENGTH_SHORT).show();
            } catch (RecordStoreNotFoundException e) {

                e.printStackTrace();
            }
            onBackPressed();
            return true;
        });

        cancel.setOnClickListener((x, y) -> {

            onBackPressed();
            return true;
        });
    }

    private void onBackPressed() {
        ENG_ContainerManager.getSingleton().setPreviousContainer();
//		ENG_ContainerManager.getSingleton().destroyContainer(getName());
//		MainApp.getGame().setPreviousMenuName(null);
//		MenuManager.getSingleton().showPreviousMenuOverlay();
//
//		String menuName = bundle.getString(MENU_NAME);
//		String overlayElementName = bundle.getString(OVERLAY_ELEMENT_NAME);
//
//		final MenuOverlay menu = MenuManager.getSingleton()
//				.getMenuOverlayByName(menuName);
//		final ENG_ButtonOverlayElement optionsMenu = ENG_GUIOverlayManager
//				.getSingleton().getButtonOverlayElementByName(
//						overlayElementName);
//
//		menu.addListener(optionsMenu,
//				new MenuButtonContainerActivityStarterListener(menuName,
//						optionsMenu, ENG_ContainerManager.getSingleton()
//								.createContainer(overlayElementName,
//										"OptionsMenu", bundle)));
    }

}
