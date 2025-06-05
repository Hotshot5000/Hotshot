/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/10/21, 7:23 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

import java.util.ArrayList;

import headwayent.hotshotengine.input.ENG_KeyEvent.KeyAction;
import headwayent.hotshotengine.input.ENG_TouchEvent.TouchAction;

public class ENG_MouseAndKeyboardListener implements ENG_IInputListener {

    public static class ENG_MouseAndKeyboardListenerFactory
            extends ENG_IInputListenerFactory {

        public static final String TYPE = "mouseAndKeyboardListener";

        @Override
        public ENG_IInputListener createInstance(ENG_IInput input) {
            
            return new ENG_MouseAndKeyboardListener((ENG_MouseAndKeyboardInput) input);
        }

        @Override
        public String getTypeName() {
            
            return TYPE;
        }

    }

    public static final int INPUT_DELTA_COUNT = 16;

    private ENG_MouseAndKeyboardInput input;
    private int initialX, initialY, x, y;
    private boolean initialValuesSet;
//    private int deltaX;
//    private int deltaY;
//    private boolean cursorGrabbed;
    private final ArrayList<InputDelta> inputDeltas = new ArrayList<>(INPUT_DELTA_COUNT);

    private static class InputDelta {
        public int x;
        public int y;
        public int deltaX;
        public int deltaY;
        public final int pointerId;

        public InputDelta(int deltaX, int deltaY, int pointerId) {
            this.deltaX = deltaX;
            this.deltaY = deltaY;
            this.pointerId = pointerId;
        }

        public InputDelta(int pointerId) {
            this.pointerId = pointerId;
        }
    }

    private InputDelta getInputDelta(int pointerId) {
        int size = inputDeltas.size();
        for (int i = 0; i < size; ++i) {
            InputDelta inputDelta = inputDeltas.get(i);
            if (inputDelta.pointerId == pointerId) {
                return inputDelta;
            }
        }
        return null;
    }

    public ENG_MouseAndKeyboardListener(ENG_MouseAndKeyboardInput input) {
        
        setInput(input);
        for (int i = 0; i < INPUT_DELTA_COUNT; ++i) {
            inputDeltas.add(new InputDelta(i));
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        
        input.addEvent(new ENG_KeyEvent(keycode, KeyAction.DOWNED));
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        
        input.addEvent(new ENG_KeyEvent(keycode, KeyAction.UP));
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        
        input.addEvent(new ENG_KeyEvent(character));
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        
        setInitialValues(screenX, screenY, pointer);
        InputDelta inputDelta = getInputDelta(pointer);
        if (inputDelta == null) {
            // More than 16 pointers??? Dafuq?
            return true;
        }
//        if (initialValuesSet) {
            input.addEvent(
                    new ENG_TouchEvent(screenX, screenY, inputDelta.deltaX, inputDelta.deltaY, pointer, TouchAction.DOWN)
//					new ENG_TouchEvent(screenX, screenY, pointer, TouchAction.DOWN)
            );
//			System.out.println("touch down x: " + deltaX + " y: " + deltaY + " pointer: " + pointer);
//        System.out.println("touch down x: " + screenX + " y: " + screenY);
//        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        
            InputDelta inputDelta = getInputDelta(pointer);
            if (inputDelta == null) {
                // More than 16 pointers??? Dafuq?
                return true;
            }
//        if (initialValuesSet) {
            input.addEvent(
                    new ENG_TouchEvent(screenX, screenY, inputDelta.deltaX, inputDelta.deltaY, pointer, TouchAction.UP)
//					new ENG_TouchEvent(screenX, screenY, pointer, TouchAction.UP)
            );

            inputDelta.deltaX = 0;
            inputDelta.deltaY = 0;
//            initialValuesSet = false;
//        System.out.println("touch up x: " + screenX + " y: " + screenY);
//			System.out.println("touch up x: " + deltaX + " y: " + deltaY);
//        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        
//		input.addEvent(
//				new ENG_TouchEvent(screenX, screenY, pointer, TouchAction.MOVE));
        sendInputDelta(screenX, screenY, pointer);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        
        sendInputDelta(screenX, screenY, 0);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return true;
    }

    public void sendInputDelta(int screenX, int screenY, int pointer) {
//        if (setInitialValues(screenX, screenY, pointer)) return;
        InputDelta inputDelta = getInputDelta(pointer);
        if (inputDelta == null) {
            // More than 16 pointers??? Dafuq?
            return;
        }
        inputDelta.deltaX = screenX - inputDelta.x;
        inputDelta.deltaY = screenY - inputDelta.y;
        input.addEvent(
                new ENG_TouchEvent(screenX, screenY, inputDelta.deltaX, inputDelta.deltaY, pointer, TouchAction.MOVE)
//				new ENG_TouchEvent(screenX, screenY, 0, TouchAction.MOVE)
        );
//        System.out.println("mouse moved screen x: " + (screenX) + " screen y: " + (screenY) + " delta x: "
//                + inputDelta.deltaX + " delta y: " + inputDelta.deltaY + " pointer: " + pointer);
//        System.out.println("touchDragged x: " + screenX + " y: " + screenY);
        inputDelta.x = screenX;
        inputDelta.y = screenY;
    }

    private boolean setInitialValues(int screenX, int screenY, int pointerId) {
//        if (!initialValuesSet) {
//			initialX = screenX;
//			initialY = screenY;
        InputDelta inputDelta = getInputDelta(pointerId);
        if (inputDelta == null) {
            // More than 16 pointers??? Dafuq?
            return true;
        }
        inputDelta.x = screenX;
        inputDelta.y = screenY;
//            initialValuesSet = true;
            return true;
//        }
//        return false;
    }

//    @Override
//    public boolean scrolled(int amount) {
//
//        return true;
//    }

    @Override
    public boolean isCursorGrabbed() {
        
        return true;//cursorGrabbed;
    }

    @Override
    public boolean isBackKeyCaught() {
        return true;
    }

    @Override
    public boolean isMenuKeyCaught() {
        return false;
    }

    /**
     * Will only be updated when you call ENG_InputManager.setInputListener()
     *
     * @param cursorGrabbed
     */
//	public void setCursorGrabbed(boolean cursorGrabbed) {
//		this.cursorGrabbed = cursorGrabbed;
//	}
    public ENG_MouseAndKeyboardInput getInput() {
        return input;
    }

    public void setInput(ENG_MouseAndKeyboardInput input) {
        this.input = input;
    }

}
