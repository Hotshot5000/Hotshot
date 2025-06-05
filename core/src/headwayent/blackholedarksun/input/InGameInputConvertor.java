/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/17/21, 7:27 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.input;

import com.badlogic.gdx.Input;

import headwayent.blackholedarksun.HudManager;
import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.gui.ENG_GUIOverlayManager;
import headwayent.hotshotengine.input.ENG_InputConvertor;
import headwayent.hotshotengine.input.ENG_KeyEvent;
import headwayent.hotshotengine.input.ENG_KeyEvent.KeyAction;
import headwayent.hotshotengine.input.ENG_MouseAndKeyboardInput;
import headwayent.hotshotengine.input.ENG_MouseAndKeyboardInput.MouseAndKeyboardEvents;
import headwayent.hotshotengine.input.ENG_TouchEvent;
import headwayent.hotshotengine.input.ENG_TouchEvent.TouchAction;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.statedebugger.ENG_FrameInterval;

public class InGameInputConvertor extends ENG_InputConvertor {

    public static final int AUTO_SCROLL_LIMIT = 20;
    private final String name;
    private ENG_MouseAndKeyboardInput input;
    private int maxQueueLen;
    private InGameEvent lastInGameEvent;
    private final ENG_Vector2D transformedCoords = new ENG_Vector2D();
    private boolean firePressed;
    private boolean scrollPressed;
    private boolean controlsPressed;
    private boolean afterburnerPressed;
    private boolean countermeasuresPressed;
    private boolean reloaderPressed;
    private boolean rotationLeftPressed;
    private boolean rotationRightPressed;
    private boolean weaponSelectionPreviousPressed;
    private boolean weaponSelectionNextPressed;
    private boolean enemySelectionPreviousPressed;
    private boolean enemySelectionNextPressed;
    private boolean attackSelectedEnemyPressed;
    private boolean defendPlayerShipPressed;
    private boolean orientationPressed;
    private boolean backPressed;

    private int firePressedPointerId = -1;
    private int scrollPressedPointerId = -1;
    private int controlsPressedPointerId = -1;
    private int afterburnerPressedPointerId = -1;
    private int countermeasuresPressedPointerId = -1;
    private int reloaderPressedPointerId = -1;
    private int rotationLeftPressedPointerId = -1;
    private int rotationRightPressedPointerId = -1;
    private int weaponSelectionPreviousPressedPointerId = -1;
    private int weaponSelectionNextPressedPointerId = -1;
    private int enemySelectionPreviousPressedPointerId = -1;
    private int enemySelectionNextPressedPointerId = -1;
    private int attackSelectedEnemyPressedPointerId = -1;
    private int defendPlayerShipPressedPointerId = -1;
    private int backPressedPointerId = -1;
    private InGameEvent event = new InGameEvent();
    private final InGameEvent[] eventList = new InGameEvent[2];
    private MouseAndKeyboardEvents events;
    private float inv_width, inv_height;
    private byte callNum;
    
//    private boolean firePressedForCurrentLoop;
//    private boolean scrollPressedForCurrentLoop;
//    private boolean controlsPressedForCurrentLoop;
//    private boolean afterburnerPressedForCurrentLoop;
//    private boolean countermeasuresPressedForCurrentLoop;
//    private boolean reloaderPressedForCurrentLoop;
//    private boolean rotationLeftPressedForCurrentLoop;
//    private boolean rotationRightPressedForCurrentLoop;
//    private boolean weaponSelectionPreviousPressedForCurrentLoop;
//    private boolean weaponSelectionNextPressedForCurrentLoop;
//    private boolean orientationPressedForCurrentLoop;

    // This works only if INPUT_DELTA_COUNT == 16.
    private short pointerIdFlags;



    public InGameInputConvertor(String name,
                                ENG_MouseAndKeyboardInput input, int maxQueueLen) {

        this.name = name;
        setInput(input);
        setMaxQueueLen(maxQueueLen);
        resetInvScreenCoords();
        for (int i = 0; i < eventList.length; ++i) {
            eventList[i] = new InGameEvent();
        }
    }

    @Override
    public void reset() {
        super.reset();
        firePressed = false;
        scrollPressed = false;
        controlsPressed = false;
        afterburnerPressed = false;
        countermeasuresPressed = false;
        reloaderPressed = false;
        rotationLeftPressed = false;
        rotationRightPressed = false;
        weaponSelectionPreviousPressed = false;
        weaponSelectionNextPressed = false;
        enemySelectionPreviousPressed = false;
        enemySelectionNextPressed = false;
        attackSelectedEnemyPressed = false;
        defendPlayerShipPressed = false;
        orientationPressed = false;
        backPressed = false;
        firePressedPointerId = -1;
        scrollPressedPointerId = -1;
        controlsPressedPointerId = -1;
        afterburnerPressedPointerId = -1;
        countermeasuresPressedPointerId = -1;
        reloaderPressedPointerId = -1;
        rotationLeftPressedPointerId = -1;
        rotationRightPressedPointerId = -1;
        weaponSelectionPreviousPressedPointerId = -1;
        weaponSelectionNextPressedPointerId = -1;
        enemySelectionPreviousPressedPointerId = -1;
        enemySelectionNextPressedPointerId = -1;
        attackSelectedEnemyPressedPointerId = -1;
        defendPlayerShipPressedPointerId = -1;
        int orientationPressedPointerId = -1;
        pointerIdFlags = 0;
        lastInGameEvent = null;
    }

    public InGameEvent[] getEvent() {
        return eventList;
    }

    public void resetInvScreenCoords() {
        inv_width = 1.0f / ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow().getWidth();//GLRenderSurface.getSingleton().getWidth();
        inv_height = 1.0f / ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow().getHeight();//GLRenderSurface.getSingleton().getHeight();
    }

    @Override
    public Object read() {
        
        if (input == null) {
            throw new NullPointerException("You must set an input");
        }

        // Avoid the dynamic_cast since it kills performance.
//        input.getData();

        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
            ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            currentFrameInterval.setMouseAndKeyboardEvents(events);
        }
        if (MainApp.getMainThread().isInputState()) {
            ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            if (currentFrameInterval.getMouseAndKeyboardEvents() != null) {
                events.clearQueues();
                events.set(currentFrameInterval.getMouseAndKeyboardEvents());
            }
        }

        // It looks like calculating the inv width or height takes a long fucking time. Around 17 ms.
        // What the fuck does roboVM do? What the fuck is this?
        // Just calculate these once and save them. If the orientation changes don't forget to update.
        // Fuck this!!!
//        ENG_RenderWindow renderWindow = ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow();
//        float inv_width = 1.0f / ((float) renderWindow.getWidth());
//        float inv_height = 1.0f / ((float) renderWindow.getHeight());

        HudManager hudManager = HudManager.getSingleton();
//        hudManager.resetAllButtons();
        if (MainApp.PLATFORM == MainApp.Platform.DESKTOP) {
            ENG_GUIOverlayManager.getSingleton().setButtonPressed(false);
        }

//        InGameEvent event = new InGameEvent();
        // Just reuse the same object so we can have a pointer at it in the listener so we don't
        // have to make a dynamic_cast<> from Object to InGameEvent. That is very slow.
        if (callNum == 0) {
            event = eventList[0];
            callNum = 1;
        } else if (callNum == 1) {
            event = eventList[1];
            callNum = 0;
        }
        event.reset();
        KeyBindings keyBindings = MainApp.getGame().getKeyBindings();
        // Check if the past InGameEvent contains info for this frame. Only for the desktop version.
        if (MainApp.PLATFORM == MainApp.Platform.DESKTOP) {
            if (lastInGameEvent != null) {
                lastInGameEvent.dx = 0;
                lastInGameEvent.dy = 0;
                // If we have already pressed something like afterburner or next weapon
                // then we no longer consider it this frame. It only works once
                // on each button press.
                if (lastInGameEvent.fire) {
                    boolean shouldContinue = true;
                    for (ENG_TouchEvent e : events.touchEvents) {
                        shouldContinue = e.action == TouchAction.DOWN;
                    }
                    if (shouldContinue) {
                        events.touchEvents.add(new ENG_TouchEvent(0, 0, 0, 0, 0, TouchAction.DOWN));
                    }
                }
                if (lastInGameEvent.speedModification > 0) {
                    boolean shouldContinue = true;
                    for (ENG_KeyEvent e : events.keyEvents) {
                        if (e.keyCode == keyBindings.getAccelerate()) {
                            shouldContinue = e.keyAction == KeyAction.DOWNED;
                        }
                    }
                    if (shouldContinue) {
                        events.keyEvents.add(
                                new ENG_KeyEvent(keyBindings.getAccelerate(),
                                        KeyAction.DOWN));
                    }
                } else if (lastInGameEvent.speedModification < 0) {
                    boolean shouldContinue = true;
                    for (ENG_KeyEvent e : events.keyEvents) {
                        if (e.keyCode == keyBindings.getDecelerate()) {
                            shouldContinue = e.keyAction == KeyAction.DOWNED;
                        }
                    }
                    if (shouldContinue) {
                        events.keyEvents.add(
                                new ENG_KeyEvent(keyBindings.getDecelerate(),
                                        KeyAction.DOWN));
                    }
                }
                if (lastInGameEvent.rotate < 0) {
                    boolean shouldContinue = true;
                    for (ENG_KeyEvent e : events.keyEvents) {
                        if (e.keyCode == keyBindings.getRotateLeft()) {
                            shouldContinue = e.keyAction == KeyAction.DOWNED;
                        }
                    }
                    if (shouldContinue) {
                        events.keyEvents.add(
                                new ENG_KeyEvent(keyBindings.getRotateLeft(),
                                        KeyAction.DOWN));
                    }
                } else if (lastInGameEvent.rotate > 0) {
                    boolean shouldContinue = true;
                    for (ENG_KeyEvent e : events.keyEvents) {
                        if (e.keyCode == keyBindings.getRotateRight()) {
                            shouldContinue = e.keyAction == KeyAction.DOWNED;
                        }
                    }
                    if (shouldContinue) {
                        events.keyEvents.add(
                                new ENG_KeyEvent(keyBindings.getRotateRight(),
                                        KeyAction.DOWN));
                    }
                }
            }
        }
        if (lastInGameEvent != null) {
            if (lastInGameEvent.fire) {
                event.fire = true;
//                System.out.println("Continuing firing from lastInGameEvent");
            }
            if (rotationLeftPressed) {
                event.rotate -= 1;
//                System.out.println("rotating");
            }
            if (rotationRightPressed) {
                event.rotate += 1;
            }
            if (MainApp.Platform.isMobile()) {
                if (!orientationPressed) {
                    event.dx = lastInGameEvent.dx;
                    event.dy = lastInGameEvent.dy;
                }
            }
        }
//        if (MainApp.Platform.isMobile()) {
//            firePressedForCurrentLoop = false;
//            scrollPressedForCurrentLoop = false;
//            controlsPressedForCurrentLoop = false;
//            afterburnerPressedForCurrentLoop = false;
//            countermeasuresPressedForCurrentLoop = false;
//            reloaderPressedForCurrentLoop = false;
//            rotationLeftPressedForCurrentLoop = false;
//            rotationRightPressedForCurrentLoop = false;
//            weaponSelectionPreviousPressedForCurrentLoop = false;
//            weaponSelectionNextPressedForCurrentLoop = false;
//            orientationPressedForCurrentLoop = false;
//        }
        for (ENG_TouchEvent touchEvent : events.touchEvents) {

            boolean shouldUpdatePosition = true;

            if (MainApp.Platform.isMobile()) {
                ENG_Utility.convertPixelsToScreenSpace(touchEvent.x, touchEvent.y, inv_width, inv_height, transformedCoords);
                TouchEventType touchEventType;
                switch (touchEvent.action) {
                    case DOWN:
                        touchEventType = TouchEventType.DOWN;
                        break;
                    case UP:
                        touchEventType = TouchEventType.UP;
//                        event.fire = false;
                        break;
                    case MOVE:
                        touchEventType = TouchEventType.MOVE;
                        break;
                    case NONE:
                    default:
                        // Should never get here.
                        throw new IllegalArgumentException("Should never be NONE");
                }

                // Check if we have a MOVE event type that didn't start on a button.
                // In this case, don't bother updating GUI elements and just let the swipe
                // be considered a swipe and not a button press.
                boolean touchHandled = false;
                if (touchEventType == TouchEventType.DOWN || touchEventType == TouchEventType.UP ||
                        (touchEventType == TouchEventType.MOVE && ((pointerIdFlags & (1 << touchEvent.pointerId)) != 0))) {
                    touchHandled = ENG_GUIOverlayManager.getSingleton().updateAllGUIElements(
                            transformedCoords.x, transformedCoords.y, touchEventType);
//                    System.out.println("updateAllGUIElements pointerId: " + touchEvent.pointerId);
                }



                if (touchHandled && touchEventType == TouchEventType.DOWN) {
                    // We have a touch that began on a GUI element.
                    pointerIdFlags |= (short) (1 << touchEvent.pointerId);
//                    System.out.println("touch down: " + touchEvent.pointerId);
                }

                if (touchEventType == TouchEventType.UP) {
                    pointerIdFlags &= (short) ~(1 << touchEvent.pointerId);
//                    System.out.println("touch up: " + touchEvent.pointerId);
                }

                shouldUpdatePosition = !touchHandled;
//                System.out.println("shouldUpdatePosition: " + shouldUpdatePosition + " pointerId: " + touchEvent.pointerId);


//                event.fire = hudManager.isFireButtonPressed();
//                System.out.println("event.fire: " + event.fire);
//                if (event.fire) {
//                    shouldUpdatePosition = false;
//                }
                if (hudManager.isFireButtonPressed()) {
                    event.fire = true;
//                    shouldUpdatePosition = false;
                    firePressed = true;
                    // If we have multiple touch events happening in the loop we only want
                    // to save the one that caused the action and not the rest of them which
                    // are probably in other parts of the screen but the pressed() method
                    // still returns true.
                    if (firePressedPointerId == -1) {
                        firePressedPointerId = touchEvent.pointerId;
                    }
//                    System.out.println("Fire button pressed firePressedPointerId: " + firePressedPointerId);
                } else if (firePressed && (firePressedPointerId == touchEvent.pointerId && touchEventType == TouchEventType.UP)) {
                    event.fire = false;
//                    shouldUpdatePosition = false;
                    firePressed = false;
                    firePressedPointerId = -1;
//                    System.out.println("Fire button not pressed");
                }
                if (hudManager.isSpeedScrollerButtonPressed()) {
                    // It's already handled by the speed scroller container itself.
//                    shouldUpdatePosition = false;
                    scrollPressed = true;
                    if (scrollPressedPointerId == -1) {
                        scrollPressedPointerId = touchEvent.pointerId;
                    }
                } else if (scrollPressed && (scrollPressedPointerId == touchEvent.pointerId && touchEventType == TouchEventType.UP)) {
                    scrollPressed = false;
                    scrollPressedPointerId = -1;
//                    shouldUpdatePosition = false;
                }
//                System.out.println("Scroll pressed: " + scrollPressed);
                if (hudManager.isControlsButtonPressed()) {
                    // Not used anymore.
//                    shouldUpdatePosition = false;
                    controlsPressed = true;
                    if (controlsPressedPointerId == -1) {
                        controlsPressedPointerId = touchEvent.pointerId;
                    }
                } else if (controlsPressed && (controlsPressedPointerId == touchEvent.pointerId && touchEventType == TouchEventType.UP)) {
                    controlsPressed = false;
                    controlsPressedPointerId = -1;
                }
//                event.afterburner = hudManager.isAfterburnerButtonPressed();
//                if (event.afterburner) {
//                    shouldUpdatePosition = false;
//                }
                if (hudManager.isAfterburnerButtonPressed()) {
                    event.afterburner = true;
//                    shouldUpdatePosition = false;
                    afterburnerPressed = true;
                    if (afterburnerPressedPointerId == -1) {
                        afterburnerPressedPointerId = touchEvent.pointerId;
                    }
                } else if (afterburnerPressed && (afterburnerPressedPointerId == touchEvent.pointerId && touchEventType == TouchEventType.UP)) {
//                    shouldUpdatePosition = false;
                    afterburnerPressed = false;
                    afterburnerPressedPointerId = -1;
                }
                if (hudManager.isCountermeasuresButtonPressed()) {
                    event.countermeasures = true;
//                    shouldUpdatePosition = false;
                    countermeasuresPressed = true;
                    if (countermeasuresPressedPointerId == -1) {
                        countermeasuresPressedPointerId = touchEvent.pointerId;
                    }
                } else if (countermeasuresPressed && (countermeasuresPressedPointerId == touchEvent.pointerId && touchEventType == TouchEventType.UP)) {
//                    shouldUpdatePosition = false;
                    countermeasuresPressed = false;
                    countermeasuresPressedPointerId = -1;
                }
                if (hudManager.isReloaderButtonPressed()) {
                    event.reloadShip = true;
//                    shouldUpdatePosition = false;
                    reloaderPressed = true;
                    if (reloaderPressedPointerId == -1) {
                        reloaderPressedPointerId = touchEvent.pointerId;
                    }
                } else if (reloaderPressed && (reloaderPressedPointerId == touchEvent.pointerId && touchEventType == TouchEventType.UP)) {
//                    shouldUpdatePosition = false;
                    reloaderPressed = false;
                    reloaderPressedPointerId = -1;
                }

                if (hudManager.isRotationLeftButtonPressed()) {
                    event.rotate -= 1;
//                    shouldUpdatePosition = false;
                    rotationLeftPressed = true;
                    if (rotationLeftPressedPointerId == -1) {
                        rotationLeftPressedPointerId = touchEvent.pointerId;
                    }
//                    System.out.println("rotation pressed");
                } else if (rotationLeftPressed && (rotationLeftPressedPointerId == touchEvent.pointerId && touchEventType == TouchEventType.UP)) {
//                    shouldUpdatePosition = false;
                    rotationLeftPressed = false;
                    rotationLeftPressedPointerId = -1;
//                    System.out.println("rotation released");
                }

                if (hudManager.isRotationRightButtonPressed()) {
                    event.rotate += 1;
//                    shouldUpdatePosition = false;
                    rotationRightPressed = true;
                    if (rotationRightPressedPointerId == -1) {
                        rotationRightPressedPointerId = touchEvent.pointerId;
                    }
                } else if (rotationRightPressed && (rotationRightPressedPointerId == touchEvent.pointerId && touchEventType == TouchEventType.UP)) {
//                    shouldUpdatePosition = false;
                    rotationRightPressed = false;
                    rotationRightPressedPointerId = -1;
                }

                if (hudManager.isWeaponSelectionPreviousButtonPressed()) {
                    if (touchEventType == TouchEventType.DOWN) {
                        event.advanceWeapon -= 1;
                    }
//                    shouldUpdatePosition = false;
                    weaponSelectionPreviousPressed = true;
                    if (weaponSelectionPreviousPressedPointerId == -1) {
                        weaponSelectionPreviousPressedPointerId = touchEvent.pointerId;
                    }
//                    System.out.println("WeaponSelection button pressed event: " + event.advanceWeapon);
                } else if (weaponSelectionPreviousPressed && (weaponSelectionPreviousPressedPointerId == touchEvent.pointerId && touchEventType == TouchEventType.UP)) {
//                    shouldUpdatePosition = false;
                    weaponSelectionPreviousPressed = false;
                    weaponSelectionPreviousPressedPointerId = -1;
//                    System.out.println("WeaponSelection button released");
                }
                if (hudManager.isWeaponSelectionNextButtonPressed()) {
                    if (touchEventType == TouchEventType.DOWN) {
                        event.advanceWeapon += 1;
                    }
//                    shouldUpdatePosition = false;
                    weaponSelectionNextPressed = true;
                    if (weaponSelectionNextPressedPointerId == -1) {
                        weaponSelectionNextPressedPointerId = touchEvent.pointerId;
                    }
                } else if (weaponSelectionNextPressed && (weaponSelectionNextPressedPointerId == touchEvent.pointerId && touchEventType == TouchEventType.UP)) {
//                    shouldUpdatePosition = false;
                    weaponSelectionNextPressed = false;
                    weaponSelectionNextPressedPointerId = -1;
                }
                if (hudManager.isEnemySelectionPreviousButtonPressed()) {
                    if (touchEventType == TouchEventType.DOWN) {
                        event.advanceEnemySelection -= 1;
                    }
//                    shouldUpdatePosition = false;
                    enemySelectionPreviousPressed = true;
                    if (enemySelectionPreviousPressedPointerId == -1) {
                        enemySelectionPreviousPressedPointerId = touchEvent.pointerId;
                    }
//                    System.out.println("enemySelection button pressed event: " + event.advanceenemy);
                } else if (enemySelectionPreviousPressed && (enemySelectionPreviousPressedPointerId == touchEvent.pointerId && touchEventType == TouchEventType.UP)) {
//                    shouldUpdatePosition = false;
                    enemySelectionPreviousPressed = false;
                    enemySelectionPreviousPressedPointerId = -1;
//                    System.out.println("enemySelection button released");
                }
                if (hudManager.isEnemySelectionNextButtonPressed()) {
                    if (touchEventType == TouchEventType.DOWN) {
                        event.advanceEnemySelection += 1;
                    }
//                    shouldUpdatePosition = false;
                    enemySelectionNextPressed = true;
                    if (enemySelectionNextPressedPointerId == -1) {
                        enemySelectionNextPressedPointerId = touchEvent.pointerId;
                    }
                } else if (enemySelectionNextPressed && (enemySelectionNextPressedPointerId == touchEvent.pointerId && touchEventType == TouchEventType.UP)) {
//                    shouldUpdatePosition = false;
                    enemySelectionNextPressed = false;
                    enemySelectionNextPressedPointerId = -1;
                }
                if (hudManager.isAttackSelectedEnemyButtonPressed()) {
                    event.attackSelectedEnemy = true;
//                    shouldUpdatePosition = false;
                    attackSelectedEnemyPressed = true;
                    if (attackSelectedEnemyPressedPointerId == -1) {
                        attackSelectedEnemyPressedPointerId = touchEvent.pointerId;
                    }
                } else if (attackSelectedEnemyPressed && (attackSelectedEnemyPressedPointerId == touchEvent.pointerId && touchEventType == TouchEventType.UP)) {
//                    shouldUpdatePosition = false;
                    attackSelectedEnemyPressed = false;
                    attackSelectedEnemyPressedPointerId = -1;
                }
                if (hudManager.isDefendPlayerShipButtonPressed()) {
                    event.defendPlayerShip = true;
//                    shouldUpdatePosition = false;
                    defendPlayerShipPressed = true;
                    if (defendPlayerShipPressedPointerId == -1) {
                        defendPlayerShipPressedPointerId = touchEvent.pointerId;
                    }
                } else if (defendPlayerShipPressed && (defendPlayerShipPressedPointerId == touchEvent.pointerId && touchEventType == TouchEventType.UP)) {
//                    shouldUpdatePosition = false;
                    defendPlayerShipPressed = false;
                    defendPlayerShipPressedPointerId = -1;
                }
                if (hudManager.isBackButtonPressed()) {
                    event.escape = true;
//                    shouldUpdatePosition = false;
                    backPressed = true;
                    if (backPressedPointerId == -1) {
                        backPressedPointerId = touchEvent.pointerId;
                    }
                } else if (backPressed && (backPressedPointerId == touchEvent.pointerId && touchEventType == TouchEventType.UP)) {
//                    shouldUpdatePosition = false;
                    backPressed = false;
                    backPressedPointerId = -1;
                }
            }

            if (shouldUpdatePosition) {
                if (touchEvent.action == TouchAction.UP) {
                    if (MainApp.Platform.isMobile()) {
                        boolean dx = Math.abs(touchEvent.dx) > AUTO_SCROLL_LIMIT;
                        boolean dy = Math.abs(touchEvent.dy) > AUTO_SCROLL_LIMIT;
                        if (dx || dy) {
                            event.dx = (int) touchEvent.dx;
                            event.dy = (int) touchEvent.dy;
                        } else {
                            event.dx = 0;
                            event.dy = 0;
                        }
                    } else {
                        event.dx = 0;
                        event.dy = 0;
                    }

                    orientationPressed = false;
                } else {
                    if (!orientationPressed) {
                        event.dx = 0;
                        event.dy = 0;
                    }
                    event.dx += (int) touchEvent.dx;
                    event.dy += (int) touchEvent.dy;
                    orientationPressed = true;
//                    System.out.println("event dx: " + event.dx + " event dy: " + event.dy +
//                            " dx: " + touchEvent.dx + " y: " + touchEvent.dy +
//                            " pointerId: " + touchEvent.pointerId);
                }
            }
            if (MainApp.PLATFORM == MainApp.Platform.DESKTOP) {
                if (touchEvent.action == TouchAction.DOWN) {
                    event.fire = true;
                } else if (touchEvent.action == TouchAction.UP) {
                    event.fire = false;
                }
            }
        }

        for (ENG_KeyEvent e : events.keyEvents) {
            if (e.keyAction == KeyAction.DOWN || e.keyAction == KeyAction.DOWNED) {
//                System.out.println("keyAction: " + e.keyAction);
                if (e.keyCode == keyBindings.getAccelerate()) {
                    event.speedModification += 1;
                }
                if (e.keyCode == keyBindings.getDecelerate()) {
                    event.speedModification -= 1;
                }
                if (e.keyCode == keyBindings.getRotateLeft()) {
                    event.rotate -= 1;
                }
                if (e.keyCode == keyBindings.getRotateRight()) {
                    event.rotate += 1;
                }
            }
            if (e.keyAction == KeyAction.DOWNED) {
                if (e.keyCode == keyBindings.getNextWeapon()) {
                    event.advanceWeapon += 1;
                }
                if (e.keyCode == keyBindings.getPreviousWeapon()) {
                    event.advanceWeapon -= 1;
                }
                if (e.keyCode == keyBindings.getNextEnemySelection()) {
                    event.advanceEnemySelection += 1;
                }
                if (e.keyCode == keyBindings.getPreviousEnemySelection()) {
                    event.advanceEnemySelection -= 1;
                }
                if (e.keyCode == keyBindings.getAfterburner()) {
                    event.afterburner = true;
                }
                if (e.keyCode == keyBindings.getCountermeasures()) {
                    event.countermeasures = true;
                }
                if (e.keyCode == keyBindings.getReloadShip()) {
                    event.reloadShip = true;
                }
                if (e.keyCode == keyBindings.getAttackSelectedEnemy()) {
                    event.attackSelectedEnemy = true;
                }
                if (e.keyCode == keyBindings.getDefendPlayerShip()) {
                    event.defendPlayerShip = true;
                }
                if (e.keyCode == keyBindings.getEscape() || e.keyCode == keyBindings.getEscape2() || (MainApp.Platform.isMobile() && e.keyCode == Input.Keys.BACK)) {
                    event.escape = true;
                }
                if (e.keyCode == keyBindings.getConsole() && MainApp.Platform.isDesktop()) {
                    event.console = true;
                }
            }
        }
        input.clearQueue();
        lastInGameEvent = event;
        return event;
    }

    public ENG_MouseAndKeyboardInput getInput() {
        return input;
    }

    public void setInput(ENG_MouseAndKeyboardInput input) {
        this.input = input;
        events = (MouseAndKeyboardEvents) input.getData();
    }

    public int getMaxQueueLen() {
        return maxQueueLen;
    }

    public void setMaxQueueLen(int maxQueueLen) {
        this.maxQueueLen = maxQueueLen;
    }

    public String getName() {
        return name;
    }

}
