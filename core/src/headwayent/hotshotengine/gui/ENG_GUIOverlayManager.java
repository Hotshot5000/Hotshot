/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 4:51 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_RealRect;
import headwayent.hotshotengine.gui.ENG_ControlsOverlayElement.ControlDirection;
import headwayent.hotshotengine.gui.ENG_ScrollOverlayContainer.ScrollType;
import headwayent.hotshotengine.input.ENG_InputConvertor.TouchEventType;
import headwayent.hotshotengine.renderer.ENG_OverlayContainer;
import headwayent.hotshotengine.renderer.ENG_OverlayElement;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ENG_GUIOverlayManager {

    private final TreeMap<String, ENG_ButtonOverlayElement> buttonOverlayElements = new TreeMap<>();
    private final TreeMap<String, ENG_ScrollOverlayContainer> scrollOverlayContainers = new TreeMap<>();
    private final TreeMap<String, ENG_ControlsOverlayElement> controlsOverlayElements = new TreeMap<>();
    private final TreeMap<String, ENG_DynamicOverlayElement> dynamicOverlayElements = new TreeMap<>();
//    private static ENG_GUIOverlayManager mgr;

    public ENG_GUIOverlayManager() {
//        if (mgr == null) {
//            mgr = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
    }

    public ENG_ButtonOverlayElement getButtonOverlayElementByName(String name) {
        return buttonOverlayElements.get(name);
    }

    public ENG_ScrollOverlayContainer getScrollOverlayContainerByName(String name) {
        return scrollOverlayContainers.get(name);
    }

    public ENG_ControlsOverlayElement getControlsOverlayElementByName(String name) {
        return controlsOverlayElements.get(name);
    }

    public ENG_DynamicOverlayElement getDynamicOverlayElementByName(String name) {
        return dynamicOverlayElements.get(name);
    }

    public ENG_DynamicOverlayElement createDynamicOverlayElement(
            ENG_OverlayElement element, String textureName, String groupName) {
        ENG_DynamicOverlayElement dynElem = new ENG_DynamicOverlayElement(element, textureName, groupName);
        dynamicOverlayElements.put(element.getName(), dynElem);
        return dynElem;
    }

    public void destroyDynamicOverlayElement(String name) {
        ENG_DynamicOverlayElement remove = dynamicOverlayElements.remove(name);
        if (remove == null) {
            throw new IllegalArgumentException(name + " does not exist");
        }

    }

    public void destroyAllDynamicOverlayElements() {
        dynamicOverlayElements.clear();
    }

    /**
     * @param element the element you want to "upgrade" to the controls element
     * @return the controls for which you must setFrameNumberControlDirection()
     */
    public ENG_ControlsOverlayElement createControlsOverlayElement(
            ENG_OverlayElement element) {
        return createControlsOverlayElement(element, null);
    }

    public ENG_ControlsOverlayElement createControlsOverlayElement(
            ENG_OverlayElement element, EnumMap<ControlDirection, Integer> controlMap) {
        ENG_ControlsOverlayElement controls = new ENG_ControlsOverlayElement(element,
                controlMap);
        controlsOverlayElements.put(element.getName(), controls);
        return controls;
    }

    public void destroyControlsOverlayElement(String name) {
        ENG_ControlsOverlayElement remove = controlsOverlayElements.remove(name);
        if (remove == null) {
            throw new IllegalArgumentException("Controls overlay element with name " +
                    name + " could not be found");
        }
    }

    public void destroyAllControlsOverlayElements() {
        controlsOverlayElements.clear();
    }

    /**
     * @param container the container of the scroll object
     * @param indicator the scroll object
     * @return
     */
    public ENG_ScrollOverlayContainer createScrollOverlayContainer(
            ENG_OverlayContainer container, ENG_OverlayElement indicator,
            ScrollType scrollType) {
        ENG_ScrollOverlayContainer scrollContainer =
                new ENG_ScrollOverlayContainer(container, indicator, scrollType);
        scrollOverlayContainers.put(container.getName(), scrollContainer);
        return scrollContainer;
    }

    public void destroyScrollOverlayContainer(String name) {
        ENG_ScrollOverlayContainer container = scrollOverlayContainers.remove(name);
        if (container == null) {
            throw new IllegalArgumentException("scroll overlay " + name + " not found");
        }
    }

    public void destroyAllScrollOverlayContainers() {
        scrollOverlayContainers.clear();
    }

    public ENG_ButtonOverlayElement createButtonOverlayElement(
            ENG_OverlayElement overlay) {
        // Create a button overlay by incorporating an overlay
        ENG_ButtonOverlayElement buttonOverlay = new ENG_ButtonOverlayElement(
                overlay);
        buttonOverlayElements.put(overlay.getName(), buttonOverlay);
        return buttonOverlay;
    }

    public void destroyButtonOverlayElement(String name) {
        ENG_AbstractButton element = buttonOverlayElements.remove(name);
        if (element == null) {
            throw new IllegalArgumentException("button overlay element " + name +
                    " not found");
        }
    }

    public void destroyAllButtonOverlayElements() {
        buttonOverlayElements.clear();
    }

    private final ENG_RealRect rect = new ENG_RealRect();

    /**
     * The coordinates must be provided in screen space between 0 and 1
     * Returns true if any of the GUI elements has handled the event, false otherwise.
     *
     * @param eventType
     * @param x
     * @param y
     */
    public boolean updateAllGUIElements(float x, float y, TouchEventType type) {
        boolean handled = updateAllButtons(x, y, type);
        handled |= updateAllScrollButtons(x, y, type);
        handled |= updateAllMovementControls(x, y, type);
        return handled;
    }

    private boolean updateAllMovementControls(float x, float y, TouchEventType type) {
        boolean handled = false;
        for (ENG_ControlsOverlayElement controls : controlsOverlayElements.values()) {
            if (controls.isVisible() && controls.isTouched(x, y)) {
                controls.updateListeners(x, y, type);
                handled = true;
            }
        }
        return handled;
    }

    private boolean updateAllScrollButtons(float x, float y, TouchEventType type) {
        boolean handled = false;
        for (ENG_ScrollOverlayContainer scrollOverlay : scrollOverlayContainers.values()) {
            ENG_OverlayContainer container = scrollOverlay.getContainer();
            ENG_OverlayElement elem = container.findElementAt(x, y);
            if (elem != null) {
                //It's the container or one of its children
                //Either way we don't care
                scrollOverlay.updateListeners(x, y, type);
                handled = true;
            } else {
                scrollOverlay.updateListeners(x, y, TouchEventType.UP);
            }
        }
        return handled;
    }

    private boolean updateAllButtons(float x, float y, TouchEventType type) {
        boolean handled = false;
        for (ENG_ButtonOverlayElement buttonOverlay : buttonOverlayElements.values()) {
            if (buttonOverlay.isVisible()) {
                if (buttonOverlay.isTouched(x, y)) {
                    buttonOverlay.updateListeners(x, y, type);
                    handled = true;
                } else {
                    buttonOverlay.updateListeners(x, y, TouchEventType.UP);
                }
                /*
				// translate into overlay space
				ENG_Overlay overlay = buttonOverlay.getParentOverlayContainer();
				
				Iterator<ENG_OverlayContainer> iterator = 
						overlay.get2DContainerIterator();
				while (iterator.hasNext()) {
					ENG_OverlayContainer next = iterator.next();
					ENG_OverlayElement elem = next.findElementAt(x, y);//findElementAt(next, x, y);
					if (elem != null) {
						//We have a hit. Check if it's a button
						ENG_ButtonOverlayElement button = 
								getButtonOverlayElementByName(elem.getName());
						if (button != null) {
							button.updateListeners(x, y);
						}
					}
				}*/
            }
        }
        return handled;
    }

    public void setButtonPressed(boolean b) {
        for (ENG_ButtonOverlayElement button : buttonOverlayElements.values()) {
            button.setPressed(b, false);
        }
    }

    public void setControlsOverlayElements(ControlDirection dir) {
        for (ENG_ControlsOverlayElement controls : controlsOverlayElements.values()) {
            controls.setControlDirection(dir);
        }
    }

    private static class ElementWithPosition {
        public final ENG_OverlayElement elem;
        public final float x;
        public final float y;

        public ElementWithPosition(ENG_OverlayElement elem, float x, float y) {
            this.elem = elem;
            this.x = x;
            this.y = y;
        }
    }

//	private ElementWithPosition elemWithPos = new ElementWithPosition();

    private ElementWithPosition findElementAt(
            ENG_OverlayElement next, float x, float y) {
        if (next.isEnabled() && next.isVisible()) {

            rect.left = next.getLeft();
            rect.top = next.getTop();
            rect.right = rect.left + next.getWidth();
            rect.bottom = rect.top + next.getHeight();

            if (rect.inside(x, y)) {
                System.out.println("Inside at " + x + " " + y);
                ElementWithPosition elemWithPos = null;
                if (next.isContainer()) {
                    ENG_OverlayContainer cont = (ENG_OverlayContainer) next;
                    Iterator<Entry<String, ENG_OverlayElement>> iterator =
                            cont.getChildIterator();
                    while (iterator.hasNext()) {
                        Entry<String, ENG_OverlayElement> entry = iterator.next();
                        // If overlapping return the first one or the last one?
                        float transX = x - next.getLeft();
                        float transY = y - next.getTop();
                        elemWithPos = findElementAt(entry.getValue(), transX, transY);
                    }
                }
                if (elemWithPos == null) {
                    elemWithPos = new ElementWithPosition(next, x, y);
                }
                return elemWithPos;
            } else {
                System.out.println("Outside at " + x + " " + y);
            }

        }
        return null;
    }

    public static ENG_GUIOverlayManager getSingleton() {
//        return mgr;
        return MainApp.getGame().getGuiOverlayManager();
    }

}
