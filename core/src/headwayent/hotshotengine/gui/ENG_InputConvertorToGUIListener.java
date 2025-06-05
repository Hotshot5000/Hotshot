/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui;

import headwayent.hotshotengine.gui.ENG_ControlsOverlayElement.ControlDirection;
import headwayent.hotshotengine.input.ENG_InputConvertor.Event;
import headwayent.hotshotengine.input.ENG_InputConvertor.EventType;
import headwayent.hotshotengine.input.ENG_InputConvertor.KeyEventType;
import headwayent.hotshotengine.input.ENG_InputConvertor.TouchEventType;
import headwayent.hotshotengine.input.ENG_InputConvertorListener;
import headwayent.hotshotengine.input.ENG_TouchInputConvertor;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_RenderWindow;

import java.util.Queue;
import java.util.TreeMap;

public class ENG_InputConvertorToGUIListener extends ENG_InputConvertorListener {

    /**
     * Sends the message only if it has been clicked AND released on the same object
     *
     * @author Sebi
     */
    public static abstract class OnClickListenerOnUp implements OnClickListener {

        private final TreeMap<String, Boolean> clickedElements = new TreeMap<>();

//		@Override
//		public void onClick(float x, float y, TouchEventType type) {
//			
//			
//		}

    }

    public interface OnClickListener {
        /**
         * Directed between 0 and 1
         *
         * @param x
         * @param y
         */
        void onClick(float x, float y, TouchEventType type);
    }

    public interface OnKeyCodeListener {
        void onKeyCode(int keyCode, KeyEventType type);
    }

    public interface OnCharacterListener {
        void onCharacter(char character);
    }

    private final ENG_TouchInputConvertor inputConvertor;
    private final TreeMap<String, OnClickListener> clickListeners =
            new TreeMap<>();
    private final TreeMap<String, OnKeyCodeListener> keyCodeListeners =
            new TreeMap<>();
    private final TreeMap<String, OnCharacterListener> characterListeners =
            new TreeMap<>();
    private float initialX, initialY;
    private final Queue<Event> data;

    public ENG_InputConvertorToGUIListener(
            ENG_TouchInputConvertor inputConvertor) {
        this.inputConvertor = inputConvertor;
        data = inputConvertor.getQueue();
    }

    public void addOnClickListener(String name, OnClickListener l) {
        if (clickListeners.containsKey(name)) {
            throw new IllegalArgumentException(name + " listener already added");
        }
        clickListeners.put(name, l);
    }

    public void removeOnClickListener(String name) {
        OnClickListener remove = clickListeners.remove(name);
        if (remove == null) {
            throw new IllegalArgumentException(name + " listener not in this " +
                    "InputConvertor");
        }
    }

    public void addOnKeyCodeListener(String name, OnKeyCodeListener l) {
        if (keyCodeListeners.containsKey(name)) {
            throw new IllegalArgumentException(name + " listener already added");
        }
        keyCodeListeners.put(name, l);
    }

    public void removeOnKeyCodeListener(String name) {
        OnKeyCodeListener remove = keyCodeListeners.remove(name);
        if (remove == null) {
            throw new IllegalArgumentException(name + " listener not in this " +
                    "InputConvertor");
        }
    }

    public void addOnCharacterListener(String name, OnCharacterListener l) {
        if (characterListeners.containsKey(name)) {
            throw new IllegalArgumentException(name + " listener already added");
        }
        characterListeners.put(name, l);
    }

    public void removeOnCharacterListener(String name) {
        OnCharacterListener remove = characterListeners.remove(name);
        if (remove == null) {
            throw new IllegalArgumentException(name + " listener not in this " +
                    "InputConvertor");
        }
    }


    public void removeAllOnClickListeners() {
        clickListeners.clear();
    }

    public void removeAllOnKeyCodeListeners() {
        keyCodeListeners.clear();
    }

    public void removeAllOnCharacterListeners() {
        characterListeners.clear();
    }

    public void removeAllListeners() {
        removeAllOnClickListeners();
        removeAllOnKeyCodeListeners();
        removeAllOnCharacterListeners();
    }

    @Override
    public void routeInput() {
        

//        long inputConvertorReadBeginTime = ENG_Utility.currentTimeMillis();
        // Avoid the dynamic_cast since it kills performance.
        inputConvertor.read();
//        long inputConvertorReadEndTime = ENG_Utility.currentTimeMillis();
//        System.out.println("inputConvertor read time: " + (inputConvertorReadEndTime - inputConvertorReadBeginTime));

//        System.out.println("InputConvertorToGUIListener data queue size: " + data.size());

        //Reset all buttons to not pressed.
        //The stream is continuous so if still pressed it will set it correctly
        ENG_GUIOverlayManager overlayManager = ENG_GUIOverlayManager.getSingleton();
        overlayManager.setButtonPressed(false);
        overlayManager.setControlsOverlayElements(ControlDirection.NONE);
        Event lp;
//        ENG_RenderWindow window = ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow();
        while ((lp = data.poll()) != null) {
//			System.out.println("TouchEventType: " + lp.type);
//			currentX += lp.pos.x;
//			currentY += lp.pos.y;
//			System.out.println("InputConvertor x: " + 
//					(currentX * window.getWidth()) + " y: " + 
//					(currentY * window.getHeight()) + " deltaX: " +
//					lp.pos.x + " deltaY: " + lp.pos.y);
//			currentX = ENG_Math.clamp(currentX, 0.0f, 1.0f);
//			currentY = ENG_Math.clamp(currentY, 0.0f, 1.0f);

            if (lp.eventType == EventType.TOUCH) {
//                long updateAllGUIElementsBeginTime = ENG_Utility.currentTimeMillis();
                overlayManager.updateAllGUIElements(
                        lp.pos.x, lp.pos.y, lp.touchEventType);
//                long updateAllGUIElementsEndTime = ENG_Utility.currentTimeMillis();
//                System.out.println("updateAllGUIElements time: " + (updateAllGUIElementsEndTime - updateAllGUIElementsBeginTime));
                for (OnClickListener l : clickListeners.values()) {
                    l.onClick(lp.pos.x, lp.pos.y, lp.touchEventType);
                }
            } else if (lp.eventType == EventType.KEY_CODE) {
                for (OnKeyCodeListener l : keyCodeListeners.values()) {
                    l.onKeyCode(lp.keyCode, lp.keyEventType);
                }
            } else if (lp.eventType == EventType.CHAR) {
                for (OnCharacterListener l : characterListeners.values()) {
                    l.onCharacter(lp.character);
                }
            }
        }
    }

    public void setInitialPosition(int x, int y) {
        ENG_RenderWindow window = ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow();
        initialX = (float) x / (float) window.getWidth();
        initialY = (float) y / (float) window.getHeight();
        float currentX = initialX;
        float currentY = initialY;
    }

    public float getInitialX() {
        return initialX;
    }

    public float getInitialY() {
        return initialY;
    }

}
