/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/22/21, 9:35 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui.simpleview;

import headwayent.blackholedarksun.MainActivity;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.android.graphics.Rect;
import headwayent.hotshotengine.ENG_RealRect;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.gorillagui.*;
import headwayent.hotshotengine.input.ENG_InputConvertor.KeyEventType;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

import java.util.Objects;
import java.util.TreeMap;

public class ENG_View {

    public static final int INVALID_KEY_CODE = -100;

    public interface OnClickListener {
        /**
         * @param x
         * @param y
         * @return true when handled false if not
         */
        boolean onClick(int x, int y);
    }

    public interface OnKeyCodeListener {
        boolean onKeyCode(int keyCode, KeyEventType type);
    }

    public interface OnCharacterListener {
        boolean onCharacter(char character);
    }

    public interface OnFocusListener {
        /**
         * @param focused If the view is now in focus or has left focus.
         * @return true when handled false if not
         */
        boolean onFocus(boolean focused);
    }

    public interface OnChildChangedListener {
        void onChildChanged(ENG_View child);
    }

    /**
     * Each view can have multiple event listeners which get called
     * each frame to execute code in response to internal changes.
     * If using multiple event listeners make sure the event listening execution
     * order does not matter since there is no guarantee that the eventlisteners
     * will execute in the added order.
     *
     * @author sebi
     */
    public interface EventListener {
        void listen();
    }

    public static class ClickedState {
        public boolean clicked, touchedLastFrame, focused;
    }

    private final String name;
    private final ENG_Layer layer;
    private final ENG_Container parent;
    private float left, top, right, bottom; // In percentage
    private float actLeft, actTop, actRight, actBottom; // In pixels
    private OnClickListener clickListener;
    private OnKeyCodeListener keyCodeListener;
    private OnCharacterListener characterListener;
    private OnFocusListener focusListener;
    private final TreeMap<String, EventListener> eventListeners =
            new TreeMap<>();
    private boolean visible;
    private boolean dirty = true;
    private boolean ignoreDirty;
    private boolean focusable;
    private boolean focused;
    private boolean forceActualCoordinates;
    private boolean updateManually;
    private final ENG_View parentView;
    private OnChildChangedListener childChangedListener;
    private int relativePositionInContainer = ENG_Container.RelativePositionInContainer.MIDDLE.getPos();
    private boolean clickListenerEnabled = true;
    private boolean keyCodeListenerEnabled = true;
    private boolean characterListenerEnabled = true;
    private boolean focusListenerEnabled = true;
    private boolean autoOffsetByNotchSize = true;

    // background data
    private ENG_Rectangle rect;
    private final ENG_ColorValue backgroundColor = new ENG_ColorValue(ENG_ColorValue.WHITE);
    private float borderWidth;
    private final ENG_ColorValue borderColor = new ENG_ColorValue(ENG_ColorValue.WHITE);
    //	private ENG_ColorValue textColor = new ENG_ColorValue(ENG_ColorValue.BLACK);
    private ENG_NinePatch currentNinePatch;
    private ENG_TextureAtlas currentNinePatchAtlas;
    private String currentNinePatchName;
    private String ninePatchPressedName;
    private String ninePatchNotPressedName;
    private boolean backgroundActive;
    final ClickedState clickedState = new ClickedState();
    private ENG_Container.ViewType viewType;

    public ENG_View(String name, ENG_Layer layer, ENG_Container parent) {
        this(name, layer, parent, null);
    }

    public ENG_View(String name, ENG_Layer layer, ENG_Container parent,
                    ENG_View parentView) {
        
        this.name = name;
        this.layer = layer;
        this.parent = parent;
        this.parentView = parentView;
        setVisible(true);
    }

    public void destroy() {
        // If we destroy a view while the container is still valid
        // we need to redraw everything with the view removed.
        markDirty();
        if (rect != null) {
            getLayer().destroyRectangle(rect);
        }
    }

    public void addEventListener(String name, EventListener l) {
        if (eventListeners.containsKey(name)) {
            throw new IllegalArgumentException(name + " already exists" +
                    " as an event listener");
        }
        eventListeners.put(name, l);
    }

    public void removeEventListener(String name) {
        EventListener remove = eventListeners.remove(name);
        if (remove == null) {
            throw new IllegalArgumentException(name + " could not be found " +
                    "in event listener list");
        }
    }

    public boolean hasEventListener(String name) {
        return eventListeners.containsKey(name);
    }

    public void removeAllEventListeners() {
        eventListeners.clear();
    }

    public void updateEventListeners() {
        for (EventListener l : eventListeners.values()) {
            l.listen();
        }
    }

    private void notifyChildViewChanged() {
        if (hasParentView() && getParentView().getOnChildChangedListener() != null) {
            getParentView().getOnChildChangedListener().onChildChanged(this);
        }
    }

    public boolean handleOnClickListener(int x, int y) {
        boolean ret = false;
        if (clickListener != null && isClickListenerEnabled()) {
            ret = clickListener.onClick(x, y);
            notifyChildViewChanged();
        }
        return ret;
    }

    public boolean handleOnKeyCodeListener(int keyCode, KeyEventType type) {
        boolean ret = false;
        if (keyCodeListener != null && isKeyCodeListenerEnabled()) {
            ret = keyCodeListener.onKeyCode(keyCode, type);
            notifyChildViewChanged();
        }
        return ret;
    }

    public boolean handleOnCharacterListener(char character) {
        boolean ret = false;
        if (characterListener != null && isCharacterListenerEnabled()) {
            ret = characterListener.onCharacter(character);
            notifyChildViewChanged();
        }
        return ret;
    }

    public boolean handleOnFocusListener(boolean focused) {
        boolean ret = false;
        if (focused != isFocused()) {
            setFocused(focused);
            if (focusListener != null && isFocusListenerEnabled()) {
                ret = focusListener.onFocus(focused);
                notifyChildViewChanged();
            }
        }
        return ret;
    }

    public void notifyParent() {
        parent.markDirty();
    }

    public void draw(int screenWidth, int screenHeight) {
        if (dirty || ignoreDirty) {
            // Moved dirty = false before the update
            // so that we can markDirty() in the update in case
            // we need continuous updating such as for blinking cursors etc.
            dirty = false;
            update(screenWidth, screenHeight);
//            System.out.println("Updating view: " + getName());

        }
    }

    private final ENG_RealRect position = new ENG_RealRect();
    private final ENG_Vector2D screenSize = new ENG_Vector2D();
    private final Rect actRect = new Rect();

    public void update(int screenWidth, int screenHeight) {
//		if (dirty) {
        if (!forceActualCoordinates) {
            position.set(getLeft(), getTop(), getRight(), getBottom());
            screenSize.set(screenWidth, screenHeight);
            ENG_Utility.convertFromScreenPercentageToActualPixels(position, screenSize, actRect);
            actLeft = actRect.left;
            actRight = actRect.right;
            actTop = actRect.top;
            actBottom = actRect.bottom;
        }

//        if (!isVisible()) {
//            return;
//        }
        if (isBackgroundActive()) {
            if (rect == null) {
                rect = layer.createRectangle(0, 0, 0, 0);
            }
            float left = getActLeft();
            float top = getActTop();
            float right = getActRight();
            float bottom = getActBottom();
            float width = right - left;
            float height = bottom - top;
            rect.left(left);
            rect.top(top);
            rect.width(width);
            rect.height(height);
            rect.backgroundColour(backgroundColor);
            rect.border(borderWidth, borderColor);
            if (currentNinePatch != null) {
                rect.backgroundImage(currentNinePatch, currentNinePatchAtlas.getName());
            }
        }
        if (rect != null) {
            rect.setVisible(isBackgroundActive() && isVisible());
        }


//		}
    }

    public void setActualCorners(float l, float t, float r, float b) {
        actLeft = l;
        actRight = r;
        actTop = t;
        actBottom = b;
    }

    public void setCorners(float l, float t, float r, float b) {
        left = l;
        top = t;
        right = r;
        bottom = b;
        markDirty();
    }

    public void resetDirty() {
        dirty = false;
    }

    public void markDirty() {
        dirty = true;
        notifyParent();
    }

    public boolean isDirty() {
        return dirty;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        left = ENG_Math.clamp(left, 0.0f, 100.0f);
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        top = ENG_Math.clamp(top, 0.0f, 100.0f);
        this.top = top;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        right = ENG_Math.clamp(right, 0.0f, 100.0f);
        this.right = right;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        bottom = ENG_Math.clamp(bottom, 0.0f, 100.0f);
        this.bottom = bottom;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            markDirty();
        }
    }

    public String getName() {
        return name;
    }

    public float getActLeft() {
        return actLeft;
    }

    public float getActTop() {
        return actTop;
    }

    public float getActRight() {
        return actRight;
    }

    public float getActBottom() {
        return actBottom;
    }

    public void setOnClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public OnClickListener getOnClickListener() {
        return clickListener;
    }

    public OnKeyCodeListener getOnKeyCodeListener() {
        return keyCodeListener;
    }

    public void setOnKeyCodeListener(OnKeyCodeListener keyCodeListener) {
        this.keyCodeListener = keyCodeListener;
    }

    public OnCharacterListener getOnCharacterListener() {
        return characterListener;
    }

    public void setOnCharacterListener(OnCharacterListener characterListener) {
        this.characterListener = characterListener;
    }

    public OnFocusListener getOnFocusListener() {
        return focusListener;
    }

    public void setOnFocusListener(OnFocusListener focusListener) {
        this.focusListener = focusListener;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
        markDirty();
    }

    public boolean isFocused() {
        return focused;
    }

    void setFocused(boolean focused) {
        this.focused = focused;
        markDirty();
    }

    public ENG_Container getParent() {
        return parent;
    }

    public ENG_Layer getLayer() {
        return layer;
    }

    public boolean isForceActualCoordinates() {
        return forceActualCoordinates;
    }

    public void setForceActualCoordinates(boolean forceActualCoordinates) {
        this.forceActualCoordinates = forceActualCoordinates;
    }

    public boolean isUpdateManually() {
        return updateManually;
    }

    public void setUpdateManually(boolean updateManually) {
        this.updateManually = updateManually;
    }

    public ENG_View getParentView() {
        return parentView;
    }

    public boolean hasParentView() {
        return parentView != null;
    }

    public OnChildChangedListener getOnChildChangedListener() {
        return childChangedListener;
    }

    public void setOnChildChangedListener(OnChildChangedListener childChangedListener) {
        this.childChangedListener = childChangedListener;
    }

    public void setBackgroundColor(ENG_ColorValue c) {
        backgroundColor.set(c);
        markDirty();
    }

    public void getBackgroundColor(ENG_ColorValue c) {
        c.set(backgroundColor);
    }

    public ENG_ColorValue getBackgroundColor() {
        return new ENG_ColorValue(backgroundColor);
    }

    public void setBorderWidth(float width) {
        borderWidth = width;
        markDirty();
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderColor(ENG_ColorValue c) {
        borderColor.set(c);
        markDirty();
    }

    public void getBorderColor(ENG_ColorValue ret) {
        ret.set(borderColor);
    }

    public ENG_ColorValue getBorderColor() {
        return new ENG_ColorValue(borderColor);
    }

    public void setNinePatchBackground(String notClickedNinePatch, String clickedNinePatch) {
        setNinePatchBackground(notClickedNinePatch);
        this.ninePatchPressedName = clickedNinePatch;
        this.ninePatchNotPressedName = notClickedNinePatch;
    }

    public void setNinePatchBackground(String ninePatch) {
        if (!Objects.equals(currentNinePatchName, ninePatch)) {
            ENG_LayerContainer.NinePatchAndAtlas ninePatchAndAtlas =
                    getLayer().getParent().getNinePatch(ninePatch);
            if (ninePatchAndAtlas == null) {
                throw new IllegalArgumentException("NinePatch with name: " + ninePatch +
                        " does not exist");
            }
            this.currentNinePatch = ninePatchAndAtlas.ninePatch;
            this.currentNinePatchAtlas = ninePatchAndAtlas.atlas;

            currentNinePatchName = ninePatch;
            markDirty();
        }
    }

    public String getCurrentNinePatchBackground() {
        return currentNinePatchName;
    }

    public String getNinePatchPressedName() {
        return ninePatchPressedName;
    }

    public String getNinePatchNotPressedName() {
        return ninePatchNotPressedName;
    }

    public boolean isBackgroundActive() {
        return backgroundActive;
    }

    /**
     * Don't forget to call this when setting the background colour.
     * @param backgroundActive
     */
    public void setBackgroundActive(boolean backgroundActive) {
        if (this.backgroundActive != backgroundActive) {
            this.backgroundActive = backgroundActive;
            markDirty();
        }
    }

    public boolean isClickListenerEnabled() {
        return clickListenerEnabled;
    }

    public void setClickListenerEnabled(boolean clickListenerEnabled) {
        this.clickListenerEnabled = clickListenerEnabled;
    }

    public boolean isFocusListenerEnabled() {
        return focusListenerEnabled;
    }

    public void setFocusListenerEnabled(boolean focusListenerEnabled) {
        this.focusListenerEnabled = focusListenerEnabled;
    }

    public boolean isKeyCodeListenerEnabled() {
        return keyCodeListenerEnabled;
    }

    public void setKeyCodeListenerEnabled(boolean keyCodeListenerEnabled) {
        this.keyCodeListenerEnabled = keyCodeListenerEnabled;
    }

    public boolean isCharacterListenerEnabled() {
        return characterListenerEnabled;
    }

    public void setCharacterListenerEnabled(boolean characterListenerEnabled) {
        this.characterListenerEnabled = characterListenerEnabled;
    }

    public boolean isIgnoreDirty() {
        return ignoreDirty;
    }

    public void setIgnoreDirty(boolean ignoreDirty) {
        this.ignoreDirty = ignoreDirty;
    }

    public int getRelativePositionInContainer() {
        return relativePositionInContainer;
    }

    public void setRelativePositionInContainer(int relativePositionInContainer) {
        this.relativePositionInContainer = relativePositionInContainer;
        // If anything different from middle then make sure we get rid of previous middle setting.
        if ((relativePositionInContainer | ENG_Container.RelativePositionInContainer.MIDDLE.getPos()) != ENG_Container.RelativePositionInContainer.MIDDLE.getPos()) {
            this.relativePositionInContainer &= ~ENG_Container.RelativePositionInContainer.MIDDLE.getPos();
        }
    }

    public boolean isAutoOffsetByNotchSize() {
        return autoOffsetByNotchSize;
    }

    public void setAutoOffsetByNotchSize(boolean autoOffsetByNotchSize) {
        this.autoOffsetByNotchSize = autoOffsetByNotchSize;
    }

    public ENG_Container.ViewType getViewType() {
        if (MainActivity.isDebugmode()) {
            if (viewType == null) {
                throw new IllegalStateException("viewType must not be null");
            }
        }
        return viewType;
    }

    /**
     * This should be called only when the view is created by the factory.
     * NEVER MODIFY THIS AFTER THE FIRST CALL!!!
     * @param viewType
     */
    public void setViewType(ENG_Container.ViewType viewType) {
        this.viewType = viewType;
    }

    /**
     * This is needed for when checking the focus using an ENG_TextField in ENG_TableView
     * with viewToRowParameters map.
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ENG_View eng_view = (ENG_View) o;
        return Objects.equals(name, eng_view.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
