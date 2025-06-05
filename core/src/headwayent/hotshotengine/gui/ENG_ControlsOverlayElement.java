/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/19/18, 2:52 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui;

import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.renderer.ENG_OverlayElement;

import java.util.EnumMap;

public class ENG_ControlsOverlayElement extends ENG_AbstractButton {

    public enum ControlDirection {
        NONE, LEFT, LEFT_UP, UP, RIGHT_UP, RIGHT, RIGHT_DOWN, DOWN, LEFT_DOWN
    }

    private final ENG_OverlayElement element;
    private ControlDirection direction = ControlDirection.NONE;
    private final EnumMap<ControlDirection, Integer> controlMap =
            new EnumMap<>(
                    ControlDirection.class);

    public ENG_ControlsOverlayElement(ENG_OverlayElement element) {
        this.element = element;
    }

    public boolean isVisible() {
        return element.isVisible();
    }

    public void show() {
        element.show();
    }

    public void hide() {
        element.hide();
    }

    public ControlDirection getControlsDirection() {
        return direction;
    }

    public ENG_ControlsOverlayElement(ENG_OverlayElement element,
                                      EnumMap<ControlDirection, Integer> controlMap) {
        this.element = element;
        if (controlMap != null) {
            this.controlMap.putAll(controlMap);
        }
    }

    public void setFrameNumberControlDirection(ControlDirection dir, int frameNumber) {
        controlMap.put(dir, frameNumber);
    }

    public boolean isTouched(float x, float y) {
        return element.findElementAt(x, y) == element;
    }

    private int getCurrentFrameNumber() {
        Integer frameNumber = controlMap.get(direction);
        if (frameNumber == null) {
            throw new NullPointerException("Value not set for current control " +
                    "combination");
        }
        return frameNumber;
    }

    public void resetControlDirection() {
        setControlDirection(ControlDirection.NONE);
    }

    public void setControlDirection(ControlDirection dir) {
        direction = dir;
//        element.getMaterial().getTechnique((short) 0).getPass((short) 0)
//                .getTextureUnitState(0).setCurrentFrame(getCurrentFrameNumber());
    }

    private final ENG_Vector2D leftDiag = new ENG_Vector2D();
    private final ENG_Vector2D rightDiag = new ENG_Vector2D();
    private final ENG_Vector2D posVec = new ENG_Vector2D();

    public void setPressed(float x, float y) {
        // Get the diagonals as vecs
        float leftX = element._getDerivedLeft();
        float rightX = leftX + element.getWidth();
        float upY = element._getDerivedTop();
        float bottomY = upY + element._getHeight();

        leftDiag.set(leftX - rightX, upY - bottomY);
        rightDiag.set(rightX - leftX, upY - bottomY);

        posVec.set(x - rightX, y - bottomY);
        float crossWithLeftDiag = leftDiag.crossProduct(posVec);

        posVec.set(x - leftX, y - bottomY);
        float crossWithRightDiag = rightDiag.crossProduct(posVec);

        if (crossWithLeftDiag < 0.0f && crossWithRightDiag < 0.0f) {
            // We're on left
            switch (direction) {
                case UP:
                    direction = ControlDirection.LEFT_UP;
                    break;
                case DOWN:
                    direction = ControlDirection.LEFT_DOWN;
                    break;
                default:
                    direction = ControlDirection.LEFT;
            }
        } else if (crossWithLeftDiag > 0.0f && crossWithRightDiag < 0.0f) {
            // We're on up
            switch (direction) {
                case LEFT:
                    direction = ControlDirection.LEFT_UP;
                    break;
                case RIGHT:
                    direction = ControlDirection.RIGHT_UP;
                    break;
                default:
                    direction = ControlDirection.UP;
            }
        } else if (crossWithLeftDiag > 0.0f && crossWithRightDiag > 0.0f) {
            // We're on right
            switch (direction) {
                case UP:
                    direction = ControlDirection.RIGHT_UP;
                    break;
                case DOWN:
                    direction = ControlDirection.RIGHT_DOWN;
                    break;
                default:
                    direction = ControlDirection.RIGHT;
            }
        } else if (crossWithLeftDiag < 0.0f && crossWithRightDiag > 0.0f) {
            // We're on down
            switch (direction) {
                case LEFT:
                    direction = ControlDirection.LEFT_DOWN;
                    break;
                case RIGHT:
                    direction = ControlDirection.RIGHT_DOWN;
                    break;
                default:
                    direction = ControlDirection.DOWN;
            }
        }
        setControlDirection(direction);
    }
}
