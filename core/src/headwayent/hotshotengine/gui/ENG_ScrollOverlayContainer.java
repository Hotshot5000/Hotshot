/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui;

import java.util.ArrayList;

import headwayent.hotshotengine.renderer.ENG_OverlayContainer;
import headwayent.hotshotengine.renderer.ENG_OverlayElement;

public class ENG_ScrollOverlayContainer extends ENG_AbstractButton {

    public static final int DEFAULT_MAX_PERCENTAGE_CHANGE = 100;

    public enum ScrollType {
        HORIZONTAL, VERTICAL
    }

    private final ENG_OverlayContainer container;
    private final ENG_OverlayElement element;
    private final ScrollType scrollType;
    private int percentage;
    private int maxPercentageChange = DEFAULT_MAX_PERCENTAGE_CHANGE;
    // The real indicator moving distance
    private final float scrollingDistance;
    private final float percentScroll;
    private final ArrayList<ENG_IButtonListener> listeners =
            new ArrayList<>();

    public ENG_ScrollOverlayContainer(ENG_OverlayContainer container,
                                      ENG_OverlayElement element, ScrollType scrollType) {
        this.container = container;
        this.element = element;
        // Check if element is of this container
        if (container.getChild(element.getName()) == null) {
            throw new IllegalArgumentException("Element " + element.getName() +
                    " not a child of " + container.getName());
        }
        this.scrollType = scrollType;
        switch (scrollType) {
            case HORIZONTAL:
                scrollingDistance = container.getWidth() - element.getWidth();
                break;
            case VERTICAL:
                scrollingDistance = container.getHeight() - element.getHeight();
                break;
            default:
                // Should never get here
                throw new IllegalArgumentException("Invalid scrollType " + scrollType);
        }

        // Create the mapping between real pixels and percentage
        percentScroll = scrollingDistance / 100.0f;
    }

    /**
     * Scroll the indicator centre to the x, y coords in screen space
     *
     * @param x
     * @param y
     */
    public void scroll(float x, float y) {
        switch (scrollType) {
            case HORIZONTAL: {
                float derivedLeft = container._getDerivedLeft();
                float right = container.getWidth();
                float distance = x - derivedLeft;
                float left = element.getLeft();
                float pos = distance - element.getWidth() * 0.5f;
                if (Math.abs(pos - left) > percentScroll * maxPercentageChange) {
                    pos = left + (percentScroll * maxPercentageChange) *
                            Math.signum(pos - left);
                }
                if (pos < 0.0f) {
                    pos = 0.0f;
                } else if (pos + element.getWidth() >= right) {
                    pos = right - element.getWidth();
                }
                element.setLeft(pos);
                percentage = (int) (pos / percentScroll);
            }
            break;
            case VERTICAL: {
                float derivedTop = container._getDerivedTop();
                float bottom = container.getHeight();
                float distance = y - derivedTop;
                float top = element.getTop();
                float pos = distance - element.getHeight() * 0.5f;
                if (Math.abs(pos - top) > percentScroll * maxPercentageChange) {
                    pos = top + (percentScroll * maxPercentageChange) *
                            Math.signum(pos - top);
                }
                if (pos < 0.0f) {
                    pos = 0.0f;
                } else if (pos + element.getHeight() >= bottom) {
                    pos = bottom - element.getHeight();
                }
                element.setTop(pos);
                percentage = (int) ((scrollingDistance - pos) / percentScroll);
            }
            break;
        }


    }


    public ENG_OverlayContainer getContainer() {
        return container;
    }

    public ENG_OverlayElement getElement() {
        return element;
    }

    public ScrollType getScrollType() {
        return scrollType;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException(percentage + " must be between 0 and " +
                    " 100");
        }
        this.percentage = percentage;
        switch (scrollType) {
            case HORIZONTAL: {
                float pos = percentage * percentScroll;
                if (pos < 0.0f) {
                    pos = 0.0f;
                } else if (pos + element.getWidth() >= container.getWidth()) {
                    pos = container.getWidth() - element.getWidth();
                }
                element.setLeft(pos);
            }
            break;
            case VERTICAL: {
                // This is inverted
                float pos = scrollingDistance - percentage * percentScroll;
                if (pos < 0.0f) {
                    pos = 0.0f;
                } else if (pos + element.getHeight() >= container.getHeight()) {
                    pos = container.getHeight() - element.getHeight();
                }
                element.setTop(pos);
            }
            break;
        }
    }

    public int getMaxPercentageChange() {
        return maxPercentageChange;
    }

    public void setMaxPercentageChange(int maxPercentageChange) {
        if (maxPercentageChange < 1 || maxPercentageChange > 100) {
            throw new IllegalArgumentException(maxPercentageChange + " must be in " +
                    "the range of 1 and 100");
        }
        this.maxPercentageChange = maxPercentageChange;
    }
}
