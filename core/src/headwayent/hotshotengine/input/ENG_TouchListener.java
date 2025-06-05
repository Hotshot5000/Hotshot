/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/10/21, 7:23 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

import headwayent.hotshotengine.input.ENG_KeyEvent.KeyAction;
import headwayent.hotshotengine.input.ENG_TouchEvent.TouchAction;

import java.util.ArrayList;

public class ENG_TouchListener implements /*OnTouchListener*/ ENG_IInputListener {

    public static class ENG_TouchListenerFactory extends ENG_IInputListenerFactory {

        public static final String TYPE = "touchListener";

        @Override
        public ENG_IInputListener createInstance(ENG_IInput input) {

            return new ENG_TouchListener((ENG_TouchInput) input);
        }

        @Override
        public String getTypeName() {

            return TYPE;
        }

    }

    private final ArrayList<Integer> pointerIds = new ArrayList<>();
    private ENG_TouchInput input;
    private int initialX, initialY, x, y;
    private boolean initialValuesSet;
    private int deltaX;
    private int deltaY;


    public ENG_QueueInput getTouchInput() {
        return input;
    }

    public void setTouchInput(ENG_TouchInput touchInput) {
        this.input = touchInput;
    }

    public ENG_TouchListener(ENG_TouchInput touchInput) {
        this.input = touchInput;
    }

/*	@Override
    public boolean onTouch(View view, MotionEvent event) {

		final int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			
			final int pointerCount = event.getPointerCount();
			if (pointerCount != 1) {
				System.out.println("Action down has more than one pointerCount " +
						pointerCount);
			}
			int pointerId = event.getPointerId(0);
			float xPos = event.getX(0);
			float yPos = event.getY(0);
			pointerIds.add(pointerId);
			touchInput.addEvent(
					new ENG_TouchEvent(xPos, yPos, pointerId, TouchAction.DOWN));
			break;
		}
		case MotionEvent.ACTION_POINTER_DOWN: {
			final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) 
	                >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
	        final int pointerId = event.getPointerId(pointerIndex);
	        float xPos = event.getX(pointerIndex);
			float yPos = event.getY(pointerIndex);
			touchInput.addEvent(
					new ENG_TouchEvent(xPos, yPos, pointerId, TouchAction.DOWN));
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			for (int i : pointerIds) {
				final int pointerIndex = event.findPointerIndex(i);
				final int historySize = event.getHistorySize();
				
				for (int h = 0; h < historySize; h++) {
					float xPos = event.getHistoricalX(pointerIndex, h); 
					float yPos = event.getHistoricalY(pointerIndex, h);
					touchInput.addEvent(
							new ENG_TouchEvent(xPos, yPos, i, TouchAction.MOVE));
				//	System.out.println("Historical Motion event " + xPos + " " + yPos);
				}
				float xPos = event.getX(pointerIndex);
				float yPos = event.getY(pointerIndex);
				touchInput.addEvent(
						new ENG_TouchEvent(xPos, yPos, i, TouchAction.MOVE));
			//	System.out.println("Motion event " + xPos + " " + yPos);
			}
		/*	final int historySize = event.getHistorySize();
			final int pointerCount = event.getPointerCount();
			
			for (int h = 0; h < historySize; h++) {
				for (int p = 0; p < pointerCount; p++) {
					int pointerId = event.getPointerId(p);
					float xPos = event.getHistoricalX(p, h); 
					float yPos = event.getHistoricalY(p, h);
					System.out.println("Historical Motion event " + xPos + " " + yPos);
				}
			}
			for (int p = 0; p < pointerCount; ++p) {
				int pointerId = event.getPointerId(p);
				float xPos = event.getX(p);
				float yPos = event.getY(p);
				System.out.println("Motion event " + xPos + " " + yPos);
			}*/
/*			break;
		}
		case MotionEvent.ACTION_POINTER_UP: {
			final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) 
	                >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
	        final int pointerId = event.getPointerId(pointerIndex);
	        float xPos = event.getX(pointerIndex);
			float yPos = event.getY(pointerIndex);
			touchInput.addEvent(
					new ENG_TouchEvent(xPos, yPos, pointerId, TouchAction.UP));
			if (pointerIds.contains(pointerId)) {
				pointerIds.remove(pointerId);
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			int pointerId = event.getPointerId(event.getActionIndex());
			float xPos = event.getX(0);
			float yPos = event.getY(0);
			if (pointerIds.contains(pointerId)) {
				pointerIds.remove(pointerId);
			}
			touchInput.addEvent(
					new ENG_TouchEvent(xPos, yPos, pointerId, TouchAction.UP));
			break;
		}
		case MotionEvent.ACTION_CANCEL: {
			for (int i : pointerIds) {
				final int pointerIndex = event.findPointerIndex(i);
				float xPos = event.getX(pointerIndex);
				float yPos = event.getY(pointerIndex);
				touchInput.addEvent(
						new ENG_TouchEvent(xPos, yPos, i, TouchAction.UP));
				
			}
			pointerIds.clear();
			break;
		}
		}
		
		
		return true;
	}
	*/

    @Override
    public boolean keyDown(int keycode) {

        input.addEvent(new ENG_KeyEvent(keycode, KeyAction.DOWNED));
//		String string = Keys.toString(keycode);
//		System.out.println("Key down: " + keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {

        input.addEvent(new ENG_KeyEvent(keycode, KeyAction.UP));
//		System.out.println("Key up: " + keycode);
        return true;
    }

    @Override
    public boolean keyTyped(char character) {

        input.addEvent(new ENG_KeyEvent(character));
//		System.out.println("Character: " + character);
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

//        if (initialValuesSet) {
            input.addEvent(
//					new ENG_TouchEvent(deltaX, deltaY, pointer, TouchAction.DOWN)
                    new ENG_TouchEvent(screenX, screenY, 0, 0, pointer, TouchAction.DOWN));
//			System.out.println("touch down dx: " + deltaX + " y: " + deltaY);
//        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

//        if (initialValuesSet) {
            input.addEvent(
//					new ENG_TouchEvent(deltaX, deltaY, pointer, TouchAction.UP)
                    new ENG_TouchEvent(screenX, screenY, 0, 0, pointer, TouchAction.UP)
            );
//			System.out.println("touch up x: " + deltaX + " y: " + deltaY);
//        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        input.addEvent(
                new ENG_TouchEvent(screenX, screenY, 0, 0, pointer, TouchAction.MOVE));
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {

//        if (!initialValuesSet) {
////			initialX = screenX;
////			initialY = screenY;
//            x = screenX;
//            y = screenY;
//            initialValuesSet = true;
//            return true;
//        }
//        deltaX = screenX - x;
//        deltaY = screenY - y;
        input.addEvent(
//				new ENG_TouchEvent(deltaX, deltaY, 0, TouchAction.MOVE)
                new ENG_TouchEvent(screenX, screenY, 0, 0, 0, TouchAction.MOVE)
        );
//		System.out.println("mouse moved x: " + (screenX) + " y: " + (screenY));
//        x = screenX;
//        y = screenY;

        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return true;
    }

//    @Override
//    public boolean scrolled(int amount) {
//
//        return true;
//    }

    @Override
    public boolean isCursorGrabbed() {

        return false;
    }

    @Override
    public boolean isBackKeyCaught() {
        return false;
    }

    @Override
    public boolean isMenuKeyCaught() {
        return false;
    }

}
