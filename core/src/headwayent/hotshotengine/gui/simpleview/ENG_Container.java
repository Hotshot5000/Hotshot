/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/24/21, 6:02 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui.simpleview;

import headwayent.hotshotengine.android.animation.Animator;
import headwayent.hotshotengine.android.animation.ValueAnimator;
import headwayent.hotshotengine.android.graphics.Rect;
import com.badlogic.gdx.Input;
import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.ShowKeyboardContainerListener;
import headwayent.hotshotengine.*;
import headwayent.hotshotengine.gorillagui.ENG_ScreenNative;
import headwayent.hotshotengine.gui.ENG_InputConvertorToGUIListener;
import headwayent.hotshotengine.gorillagui.ENG_Caption;
import headwayent.hotshotengine.gorillagui.ENG_Layer;
import headwayent.hotshotengine.gorillagui.ENG_SilverBack;
import headwayent.hotshotengine.input.ENG_InputConvertor.KeyEventType;
import headwayent.hotshotengine.input.ENG_InputConvertor.TouchEventType;
import headwayent.hotshotengine.input.ENG_InputConvertorListener;
import headwayent.hotshotengine.input.ENG_InputManager;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_Viewport;

import java.util.*;

public class ENG_Container {

    public static final byte MAIN_NINE_PATCH_QUEUE_GROUP_ID = (byte) 249;
    public static final byte MAIN_QUEUE_GROUP_ID = (byte) 250;
    public static final byte TOAST_NINE_PATCH_QUEUE_GROUP_ID = (byte) 251;
    public static final byte TOAST_QUEUE_GROUP_ID = (byte) 252;
    public static final byte PROGRESSBAR_NINE_PATCH_QUEUE_GROUP_ID = (byte) 253;
    public static final byte PROGRESSBAR_QUEUE_GROUP_ID = (byte) 254;


    public static final int MAX_LAYER_NUM = 3;
    public static final int TEXTURE_ATLAS_NUM = 2;

    public static final String TEXTVIEW = "textview";
    public static final String BUTTON = "button";
    public static final String CHECKBOX = "checkbox";
    public static final String IMAGETEXTVIEW = "imagetextview";
    public static final String TEXTFIELD = "textfield";
    public static final String TABLEVIEW = "tableview";
    public static final String DROPDOWNLIST = "dropdownlist";
    public static final String PROGRESSBAR = "progressbar";
    public static final String SLIDER = "slider";

    public static final String CONTAINER_TYPE = "container_type";
    public static final String RECREATE_AFTER_DESTRUCTION = "recreate_after_destruction";
    private static final String CONTAINER_CLICK_LISTENER = "containerClickListener";
    private static final String CONTAINER_KEYCODE_LISTENER = "containerKeyCodeListener";
    private static final String CONTAINER_CHARACTER_LISTENER = "containerCharacterListener";
    private static final int PROGRESSBAR_LAYER_INDEX = 998;
    private static final int TOAST_LAYER_INDEX = 999;
    public static final long TOAST_SHORT_TIME = 2000;
    public static final long TOAST_LONG_TIME = 4000;
    public static final String TOAST_BACKGROUND_COLOR_HEX = "#48413d";
    public static final int FADE_OUT_TOAST_ANIMATION_DURATION = 2000;
    public static final float TOAST_MARGIN_WIDTH = 15.0f;
    public static final float NOTCH_HORIZONTAL_OFFSET = 10.0f;
    public static final float NOTCH_VERTICAL_OFFSET = 5.0f;
    public static final float NOTCH_LEFT_LIMIT = 20.1f;
    public static final float NOTCH_TOP_LIMIT = 5.1f;
    public static final float NOTCH_RIGHT_LIMIT = 79.9f;
    public static final float NOTCH_BOTTOM_LIMIT = 94.9f;

    public enum RelativePositionInContainer {
        LEFT(0x1),
        TOP(0x2),
        RIGHT(0x4),
        BOTTOM(0x8),
        MIDDLE(0x10);

        private final int pos;

        RelativePositionInContainer(int pos) {
            this.pos = pos;
        }

        public int getPos() {
            return pos;
        }
    }


    public abstract static class ViewFactory {
        /** @noinspection deprecation*/
        public abstract ENG_View createView(String name, ENG_Layer layer,
                                            ENG_Container parent, Bundle bundle, ENG_View parentView);

        public abstract void destroyView(ENG_View view);
    }

    public static abstract class ContainerListenerFactory {
        /** @noinspection deprecation*/
        public abstract ContainerListener createContainerListener(
                ENG_Container container, Bundle bundle);

        public abstract void destroyContainerListener(ContainerListener listener);
    }

    public static abstract class ContainerListener {

        private final String type;
        private ENG_Container parentContainer;
        /** @noinspection deprecation*/ // This bundle is different from the one in parentContainer.
        private Bundle bundle;

        public ContainerListener(String type) {
            this.type = type;
        }

        /** @noinspection deprecation*/
        public ContainerListener(String type, ENG_Container container, Bundle bundle) {

            this.type = type;
            this.parentContainer = container;
            this.bundle = bundle;
        }


        public abstract void preContainerUpdate();

        public abstract void postContainerUpdate();

        public abstract void onActivation();

        public abstract void onDestruction();

        public String getType() {
            return type;
        }

        /** @noinspection deprecation*/
        public Bundle getBundle() {
            return bundle;
        }

        public ENG_Container getParentContainer() {
            return parentContainer;
        }
    }

    private final ENG_ScreenNative screen;
    private final ENG_Layer layer;
    private final ENG_Layer toastLayer;
    private final ENG_Layer progressBarLayer;
    private ENG_ProgressBar progressBar;
    private ENG_TextView toastTextView;
    private final ENG_Caption toastCaption;
    private static final HashMap<String, ViewFactory> viewFactoryList = new HashMap<>();
    private static final HashSet<String> cycleViewTypeList = new HashSet<>();
    private final HashMap<String, ENG_View> viewList = new HashMap<>();
    private boolean dirty = true;
    private boolean destroyed;
    // We need the InputConvertorToGUIListener so we keep querying if necessary
    private boolean inputManagerListenerAdded;
    private final String name;
    /** @noinspection deprecation*/
    private final Bundle bundle;
    private boolean recreate;
    private String type;
    private final ArrayList<ContainerListener> listeners = new ArrayList<>();
    private boolean onActivation, onDestruction;

    private boolean toastShowing;
    private Handler toastHandler;
    private ValueAnimator toastValueAnimator;
    private ENG_View currentFocusedView;
    private ENG_View nextFocusedView;
    private final ArrayList<ENG_View> cyclingViewList = new ArrayList<>();
    private int currentCyclePosition;
    private int previouslyDownedKey = ENG_View.INVALID_KEY_CODE;
    private boolean tabCyclingEnabled = true;
    private boolean cycleKeyDowned;
    private boolean progressBarShowing;
    private boolean visible;
    private boolean showKeyboardContainerListenerAdded;
    private boolean finalViewsPositionsCalculated;

    public enum ViewType {
        VIEW_TEXTVIEW,
        VIEW_BUTTON,
        VIEW_CHECKBOX,
        VIEW_IMAGETEXTVIEW,
        VIEW_TEXTFIELD,
        VIEW_TABLEVIEW,
        VIEW_DROPDOWNLIST,
        VIEW_PROGRESSBAR,
        VIEW_SLIDER;

        public static ViewType getViewType(String view) {
            if (view.equalsIgnoreCase(TEXTVIEW)) {
                return VIEW_TEXTVIEW;
            } else if (view.equalsIgnoreCase(BUTTON)) {
                return VIEW_BUTTON;
            } else if (view.equalsIgnoreCase(CHECKBOX)) {
                return VIEW_CHECKBOX;
            } else if (view.equalsIgnoreCase(IMAGETEXTVIEW)) {
                return VIEW_IMAGETEXTVIEW;
            } else if (view.equalsIgnoreCase(TEXTFIELD)) {
                return VIEW_TEXTFIELD;
            } else if (view.equalsIgnoreCase(TABLEVIEW)) {
                return VIEW_TABLEVIEW;
            } else if (view.equalsIgnoreCase(DROPDOWNLIST)) {
                return VIEW_DROPDOWNLIST;
            } else if (view.equalsIgnoreCase(PROGRESSBAR)) {
                return VIEW_PROGRESSBAR;
            } else if (view.equalsIgnoreCase(SLIDER)) {
                return VIEW_SLIDER;
            }
            throw new IllegalArgumentException(view + " is not a valid ViewType");
        }
    }

    static {
        viewFactoryList.put(TEXTVIEW, new ENG_TextView.TextViewFactory());
        viewFactoryList.put(BUTTON, new ENG_Button.ButtonViewFactory());
        viewFactoryList.put(CHECKBOX, new ENG_Checkbox.CheckboxFactory());
        viewFactoryList.put(IMAGETEXTVIEW, new ENG_ImageTextView.ImageTextViewFactory());
        viewFactoryList.put(TEXTFIELD, new ENG_TextField.TextFieldFactory());
        viewFactoryList.put(TABLEVIEW, new ENG_TableView.TableViewFactory());
        viewFactoryList.put(DROPDOWNLIST, new ENG_DropdownList.DropdownListFactory());
        viewFactoryList.put(PROGRESSBAR, new ENG_ProgressBar.ProgressBarFactory());
        viewFactoryList.put(SLIDER, new ENG_Slider.SliderFactory());

        // Fow view cycling when tabbing of shif-tabbing
        cycleViewTypeList.add(TEXTFIELD);
    }

    public void addListener(ContainerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ContainerListener listener) {
        listeners.remove(listener);
    }

    public void removeListener(String type) {
        boolean removed = false;
        ContainerListener r = null;
        for (ContainerListener l : listeners) {
            if (l.getType().equalsIgnoreCase(type)) {
                r = l;
                removed = true;
                break;
            }
        }
        if (removed) {
            listeners.remove(r);
        } else {
            throw new IllegalArgumentException(type + " is not an added listener type");
        }
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    public ArrayList<ContainerListener> getListeners() {
        return listeners;
    }

    /** @noinspection deprecation*/
    public ENG_Container(String name, String[] font, Bundle bundle) {

        for (String f : font) {
            ENG_SilverBack.getSingleton().loadAtlas(f, MainApp.getGame().getGameResourcesRootDir());
        }

//        ENG_Viewport viewport = ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow().getViewport(0);
        screen = ENG_SilverBack.getSingleton().createScreenNative(Arrays.asList(font));
        screen.setVisible(false);
        layer = screen.createLayerNative(0,
                new byte[]{ MAIN_NINE_PATCH_QUEUE_GROUP_ID, MAIN_QUEUE_GROUP_ID });
        toastLayer = screen.createLayerNative(TOAST_LAYER_INDEX,
                new byte[]{ TOAST_NINE_PATCH_QUEUE_GROUP_ID, TOAST_QUEUE_GROUP_ID });
        progressBarLayer = screen.createLayerNative(PROGRESSBAR_LAYER_INDEX,
                new byte[]{ PROGRESSBAR_NINE_PATCH_QUEUE_GROUP_ID, PROGRESSBAR_QUEUE_GROUP_ID });

//        ENG_RenderRoot.getRenderRoot().renderOneFrameFastCall();

        toastCaption = layer.createCaption(APP_Game.GORILLA_DEJAVU_LARGE, 0, 0, "");
        toastCaption.setVisible(false);
        this.name = name;
        this.bundle = bundle;
        checkRecreationValid(bundle);
    }

    /** @noinspection deprecation*/
    private boolean checkRecreationValid(Bundle bundle) {
        boolean ret = false;
        if (bundle != null) {
            recreate = bundle.getBoolean(RECREATE_AFTER_DESTRUCTION);
            if (recreate) {
                type = bundle.getString(CONTAINER_TYPE);
                if (type == null) {
                    throw new IllegalArgumentException("You have set to recreate after destruction yet you didn't provide a container type");
                }
                ret = true;
            }
        } else {
            recreate = false;
        }
        return ret;
    }

    /** @noinspection deprecation*/
    public ENG_Container(String name, Bundle bundle) {
        this(name, new String[]{APP_Game.GORILLA_FONT, APP_Game.GORRILA_NINE_PATCH_BUTTON}, bundle);
    }

    public ENG_Container(String name) {
        this(name, null);
    }

    public void markDirty() {
        dirty = true;
    }

    public void update() {
        if (!onActivation) {
            for (ContainerListener l : listeners) {
                l.onActivation();
            }
            onActivation = true;
            setVisible(true);
        }
        for (ContainerListener l : listeners) {
            l.preContainerUpdate();
        }
        if (!inputManagerListenerAdded) {
            // The InputManager can be null when we have a loading screen but also
            // a progress bar container. At that time we have no InputManager defined.
            if (ENG_InputManager.getSingleton() != null) {
                ENG_InputConvertorListener inputConvertorListener = ENG_InputManager.getSingleton().getInputConvertorListener(APP_Game.TO_GUI_LISTENER);
                if (inputConvertorListener != null) {
                    ENG_InputConvertorToGUIListener guiListener = (ENG_InputConvertorToGUIListener) inputConvertorListener;
                    // We're basing on the idea that there cannot be
                    // more than one container active at a time
                    guiListener.addOnClickListener(CONTAINER_CLICK_LISTENER, (x, y, type) -> {

                        ENG_Vector2D pixels = ENG_Utility.convertFromScreenSpaceToPixels(x, y);
                        int xPos = (int) pixels.x;
                        int yPos = (int) pixels.y;
                        ArrayList<ENG_View> list = new ArrayList<>(viewList.values());
                        for (ENG_View view : list) {
                            if (!view.isVisible()) {
                                continue;
                            }
                            ENG_RealRect rect = new ENG_RealRect(view.getActLeft(), view.getActTop(), view.getActRight(), view.getActBottom());
                            ENG_View.ClickedState clickedState = view.clickedState;
                            if (rect.inside(xPos, yPos)) {
                                if (type == TouchEventType.DOWNED) {
                                    clickedState.clicked = true;
                                    if (view.getNinePatchPressedName() != null && view.getViewType() == ViewType.VIEW_BUTTON) {
                                        view.setNinePatchBackground(view.getNinePatchPressedName());
                                    }
                                } else if (type == TouchEventType.UP) {
                                    if (clickedState.clicked) {
                                        clickedState.clicked = false;
                                        if (view.getNinePatchNotPressedName() != null && view.getViewType() == ViewType.VIEW_BUTTON) {
                                            view.setNinePatchBackground(view.getNinePatchNotPressedName());
                                        }
                                        if (view.isFocusable()) {
                                            if (clickedState.focused) {
                                                view.handleOnClickListener(xPos, yPos);
                                            } else {
                                                setCurrentFocusedView(view);
                                            }
                                        } else {
                                            view.handleOnClickListener(xPos, yPos);
                                        }
                                    }
                                } else if (type == TouchEventType.MOVE) {
                                    if (clickedState.clicked) {
                                        // Kind of a hack because we need to update the slider
                                        // position while moving the finger on screen and not
                                        // only on type == TouchEventType.UP.
                                        if (view.getViewType() == ViewType.VIEW_SLIDER) {
                                            view.handleOnClickListener(xPos, yPos);
                                        }
                                    }
                                }

                            } else {
//								System.out.println(view.getName());
                                clickedState.clicked = false;
                                if (view.getNinePatchNotPressedName() != null && view.getViewType() == ViewType.VIEW_BUTTON) {
                                    view.setNinePatchBackground(view.getNinePatchNotPressedName());
                                }
                            }
                        }
                    });

                    guiListener.addOnKeyCodeListener(CONTAINER_KEYCODE_LISTENER, (keyCode, type) -> {

                        if (type == KeyEventType.DOWN) {
                            if (MainApp.PLATFORM == MainApp.Platform.DESKTOP) {
                                // Use TAB and SHIFT-TAB to shift through the views
                                if (isTabCyclingEnabled() && isPositionCyclingAvailable() && !cycleKeyDowned) {
                                    boolean forward = true;
                                    if (keyCode == Input.Keys.TAB) {
                                        if (previouslyDownedKey == Input.Keys.SHIFT_LEFT || previouslyDownedKey == Input.Keys.SHIFT_RIGHT) {
                                            forward = false;
                                        }
                                        int cyclePos;
                                        if (forward) {
                                            cyclePos = nextPositionInCycle();
                                        } else {
                                            cyclePos = previousPositionInCycle();
                                        }
                                        setCurrentFocusedViewBasedOnCyclePosition(cyclePos);
                                        cycleKeyDowned = true;
                                    } else if (keyCode == Input.Keys.SHIFT_LEFT || keyCode == Input.Keys.SHIFT_RIGHT) {
                                        previouslyDownedKey = keyCode;
                                    }
                                }
                            }
                            ArrayList<ENG_View> list = new ArrayList<>(viewList.values());
                            for (ENG_View view : list) {
                                if ((view.isVisible() && view.isFocusable() && view.isFocused()) || (view.isVisible() && !view.isFocusable())) {
                                    view.handleOnKeyCodeListener(keyCode, type);
                                }
                            }
                        } else if (type == KeyEventType.UP) {
                            if (MainApp.PLATFORM == MainApp.Platform.DESKTOP) {
                                previouslyDownedKey = ENG_View.INVALID_KEY_CODE;
                                if (keyCode == Input.Keys.TAB) {
                                    cycleKeyDowned = false;
                                }
                            }
                        }
                    });
                    guiListener.addOnCharacterListener(CONTAINER_CHARACTER_LISTENER, character -> {

                        ArrayList<ENG_View> list = new ArrayList<>(viewList.values());
                        for (ENG_View view : list) {
                            if ((view.isVisible() && view.isFocusable() && view.isFocused()) || (view.isVisible() && !view.isFocusable())) {
                                view.handleOnCharacterListener(character);
                            }
                        }
                    });
                }
                inputManagerListenerAdded = true;
            }
        }
        if (dirty) {
            // Moved dirty = false before the update
            // so that we can markDirty() in the update in case
            // we need continuous updating such as for blinking cursors etc.
            dirty = false;
            ENG_Viewport viewport = ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow().getViewport(0);
            int screenWidth = viewport.getActualWidth();
            int screenHeight = viewport.getActualHeight();
            ArrayList<ENG_View> list = new ArrayList<>(viewList.values());
            calculateFinalViewsPositions(list);
            for (ENG_View view : list) {
                if (!view.isUpdateManually()) {
                    view.draw(screenWidth, screenHeight);
                }
            }

        }
        ArrayList<ENG_View> list = new ArrayList<>(viewList.values());
        for (ENG_View view : list) {
            view.updateEventListeners();
        }
        for (ContainerListener l : listeners) {
            l.postContainerUpdate();
        }
    }

    private void calculateFinalViewsPositions(ArrayList<ENG_View> list) {
        if (!finalViewsPositionsCalculated) {
            finalViewsPositionsCalculated = true;
            if (MainApp.getGame().getNotchHeight() > 0.0) {
                ArrayList<ENG_View> leftViews = new ArrayList<>();
                ArrayList<ENG_View> topViews = new ArrayList<>();
                ArrayList<ENG_View> rightViews = new ArrayList<>();
                ArrayList<ENG_View> bottomViews = new ArrayList<>();
                float leftestViewPos = 100.0f;
                float topViewPos = 100.0f;
                float rightestViewPos = 0.0f;
                float bottomViewPos = 0.0f;
                // Get the view limits.
                for (ENG_View view : list) {
                    if (!view.isAutoOffsetByNotchSize()) {
                        continue;
                    }
                    if (view.getLeft() < NOTCH_LEFT_LIMIT && leftestViewPos > view.getLeft()) {
                        leftestViewPos = view.getLeft();
                    }
                    if (view.getTop() < NOTCH_TOP_LIMIT && topViewPos > view.getTop()) {
                        topViewPos = view.getTop();
                    }
                    if (view.getRight() > NOTCH_RIGHT_LIMIT && rightestViewPos < view.getRight()) {
                        rightestViewPos = view.getRight();
                    }
                    if (view.getBottom() > NOTCH_BOTTOM_LIMIT && bottomViewPos < view.getBottom()) {
                        bottomViewPos = view.getBottom();
                    }
                }
                // Get the limits which should be taken into account when offsetting the notch.
                for (ENG_View view : list) {
                    if (!view.isAutoOffsetByNotchSize()) {
                        continue;
                    }
                    if (ENG_Math.nearlyEqual(leftestViewPos, view.getLeft())) {
                        view.setRelativePositionInContainer(view.getRelativePositionInContainer() | RelativePositionInContainer.LEFT.getPos());
                    }
                    if (ENG_Math.nearlyEqual(topViewPos, view.getTop())) {
                        view.setRelativePositionInContainer(view.getRelativePositionInContainer() | RelativePositionInContainer.TOP.getPos());
                    }
                    if (ENG_Math.nearlyEqual(rightestViewPos, view.getRight())) {
                        view.setRelativePositionInContainer(view.getRelativePositionInContainer() | RelativePositionInContainer.RIGHT.getPos());
                    }
                    if (ENG_Math.nearlyEqual(bottomViewPos, view.getBottom())) {
                        view.setRelativePositionInContainer(view.getRelativePositionInContainer() | RelativePositionInContainer.BOTTOM.getPos());
                    }
                }
                for (ENG_View view : list) {
                    if (!view.isAutoOffsetByNotchSize()) {
                        continue;
                    }
                    // TODO HACK when we finally get the notch height in pixels or percentage
                    // we will be able to know exactly how much to offset.
                    // For now just offset by fixed percentage.
                    if ((view.getRelativePositionInContainer() & RelativePositionInContainer.LEFT.getPos()) != 0) {
                        float prevPos = view.getRight();
                        view.setLeft(view.getLeft() + NOTCH_HORIZONTAL_OFFSET);
                        if (view.getRight() - view.getLeft() < 10.0f) {
                            view.setRight(prevPos + NOTCH_HORIZONTAL_OFFSET);
                        }
                    }
                    if ((view.getRelativePositionInContainer() & RelativePositionInContainer.TOP.getPos()) != 0) {
                        float prevPos = view.getBottom();
                        view.setTop(view.getTop() + NOTCH_VERTICAL_OFFSET);
                        if (view.getBottom() - view.getTop() < 10.0f) {
                            view.setBottom(prevPos + NOTCH_VERTICAL_OFFSET);
                        }
                    }
                    if ((view.getRelativePositionInContainer() & RelativePositionInContainer.RIGHT.getPos()) != 0) {
                        float prevPos = view.getLeft();
                        view.setRight(view.getRight() - NOTCH_HORIZONTAL_OFFSET);
                        if (view.getRight() - view.getLeft()  < 10.0f) {
                            view.setLeft(prevPos - NOTCH_HORIZONTAL_OFFSET);
                        }
                    }
                    if ((view.getRelativePositionInContainer() & RelativePositionInContainer.BOTTOM.getPos()) != 0) {
                        float prevPos = view.getTop();
                        view.setBottom(view.getBottom() - NOTCH_VERTICAL_OFFSET);
                        if (view.getBottom() - view.getTop() < 10.0f) {
                            view.setTop(prevPos - NOTCH_VERTICAL_OFFSET);
                        }
                    }
                }
            }
        }
    }

    public void destroy() {
        destroy(false, false);
    }

    // Skip recreation used when reloading resources on android.
    public void destroy(boolean skipRecreation, boolean skipGLDelete) {
        if (!destroyed) {
            removeToastIfShowing();
            ENG_SilverBack.getSingleton().destroyScreenNative(screen, skipGLDelete);
            if (inputManagerListenerAdded) {
                // The InputManager can be null when we have a loading screen but also
                // a progress bar container. At that time we have no InputManager defined.
                if (ENG_InputManager.getSingleton() != null) {
                    ENG_InputConvertorListener inputConvertorListener = ENG_InputManager.getSingleton().getInputConvertorListener(APP_Game.TO_GUI_LISTENER);
                    if (inputConvertorListener != null) {
                        ENG_InputConvertorToGUIListener list = (ENG_InputConvertorToGUIListener) inputConvertorListener;
                        list.removeOnClickListener(CONTAINER_CLICK_LISTENER);
                        list.removeOnKeyCodeListener(CONTAINER_KEYCODE_LISTENER);
                        list.removeOnCharacterListener(CONTAINER_CHARACTER_LISTENER);
                    }
                }
            }
            Set<String> set = viewList.keySet();
            ArrayList<String> viewNameList = new ArrayList<>(set);
            for (String name : viewNameList) {
                destroyView(name);
            }
            layer.destroyCaption(toastCaption);
            destroyed = true;
            if (recreate && !skipRecreation) {
                ENG_Container container = ENG_ContainerManager.getSingleton().createContainer(getName(), type, getBundle());
//                recreateContainerListeners(container);

                container.onRecreation(this);
            }
        }
        if (!onDestruction) {
            for (ContainerListener l : listeners) {
                l.onDestruction();
            }
            onDestruction = true;
            // Under the new design the screen has been destroyed by now so setting visibility is
            // modifying an already released object.
//            setVisible(false);
        }
    }

    public void recreateContainerListeners(ENG_Container previousContainer) {
        // Also recreate all listeners
        for (ContainerListener l : previousContainer.getListeners()) {
            ENG_ContainerManager.getSingleton().createContainerListener(this, l.getType(), l.getBundle());
        }
    }

    public void onRecreation(ENG_Container previousContainer) {

    }

    public ENG_View createView(String name, String type, float left, float top,
                               float right, float bottom, ENG_Layer layer) {
        return createView(name, type, left, top, right, bottom,
                null, false, null, layer, true);
    }

    public ENG_View createView(String name, String type, float left, float top,
                               float right, float bottom, ENG_View parentView) {
        return createView(name, type, left, top, right, bottom,
                null, false, parentView, layer, true);
    }

    public ENG_View createView(String name, String type, float left, float top,
                               float right, float bottom, boolean updateManually) {
        return createView(name, type, left, top, right, bottom, null,
                updateManually, null, layer, true);
    }

    public ENG_View createView(String name, String type, float left, float top,
                               float right, float bottom) {
        return createView(name, type, left, top, right, bottom, false, true);
    }

    public ENG_View createView(String name, String type, float left, float top,
                               float right, float bottom, boolean updateManually, boolean useNotchOffset) {
//        if (useNotchOffset && MainApp.getGame().getNotchHeight() > 0.0) {
//            // TODO HACK when we finally get the notch height in pixels or percentage
//            // we will be able to know exactly how much to offset.
//            // For now just offset by fixed percentage.
//            left += NOTCH_OFFSET;
//            top += NOTCH_OFFSET;
//            right -= NOTCH_OFFSET;
//            bottom -= NOTCH_OFFSET;
//        }
        return createView(name, type, left, top, right, bottom, null, false,
                null, layer, useNotchOffset);
    }

    /** @noinspection deprecation*/
    public ENG_View createView(String name, String type, float left, float top,
                               float right, float bottom, Bundle bundle) {
//        if (MainApp.getGame().getNotchHeight() > 0.0) {
//            // TODO HACK when we finally get the notch height in pixels or percentage
//            // we will be able to know exactly how much to offset.
//            // For now just offset by fixed percentage.
//            left += NOTCH_OFFSET;
//            top += NOTCH_OFFSET;
//            right -= NOTCH_OFFSET;
//            bottom -= NOTCH_OFFSET;
//        }
        return createView(name, type, left, top, right, bottom, bundle, false,
                null, layer, true);
    }

    /** @noinspection deprecation*/
    public ENG_View createView(String name, String type, float left, float top,
                               float right, float bottom, Bundle bundle, boolean updateManually,
                               ENG_View parentView, ENG_Layer layer, boolean useNotchOffset) {
        ViewFactory viewFactory = viewFactoryList.get(type.toLowerCase(Locale.US));
        if (viewFactory == null) {
            throw new IllegalArgumentException(type + " is not a valid " +
                    "view factory type");
        }
        ENG_View view = viewFactory.createView(name, layer, this, bundle, parentView);
        view.setCorners(left, top, right, bottom);
        view.setUpdateManually(updateManually);
        view.setAutoOffsetByNotchSize(useNotchOffset);
        ENG_View put = viewList.put(name, view);
        if (put != null) {
            throw new IllegalArgumentException(name + " already exists in " +
                    "this container");
        }
        if (cycleViewTypeList.contains(type)) {
            cyclingViewList.add(view);
        }
        return view;
    }

    public void destroyView(String name) {
        ENG_View remove = viewList.remove(name);
        if (remove != null) {
            removeItemFromCyclingList(remove);
            remove.destroy();
        } else {
            throw new IllegalArgumentException(name + " does not exist " +
                    "in this container");
        }
//		markDirty();
    }

    public boolean clicked(int x, int y) {
        update();
        for (ENG_View view : viewList.values()) {
            if (view.getActLeft() < x && view.getActRight() > x &&
                    view.getActTop() < y && view.getActBottom() > y) {
                return view.handleOnClickListener(x, y);
            }
        }
        return false;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public String getName() {
        return name;
    }

    /** @noinspection deprecation*/
    public Bundle getBundle() {
        return bundle;
    }

    public boolean isRecreate() {
        return recreate;
    }

    public void setRecreate(boolean recreate) {
        if (!recreate && this.recreate) {
            bundle.putBoolean(RECREATE_AFTER_DESTRUCTION, false);
        } else if (recreate && !this.recreate) {
            if (!checkRecreationValid(bundle)) {
                throw new IllegalArgumentException("Cannot set recreate " +
                        "if the bundle is not set with reacreate " +
                        "on destruction and the container type.");
            }
        }
        this.recreate = recreate;
    }

    public String getType() {
        return type;
    }

    public void setProgress(int progress) {
        if (!progressBarShowing) {
            showProgressBar();
        }
        progressBar.setProgress(progress);
    }

    public void showProgressBar() {
        if (!progressBarShowing) {
            progressBarShowing = true;
            progressBar = (ENG_ProgressBar) createView("defaultprogressbar", PROGRESSBAR, 30.0f, 60.0f, 70.0f, 70.0f, progressBarLayer);
        }
    }

    public void hideProgressBar() {
        if (progressBarShowing) {
            ENG_Container.this.destroyView(progressBar.getName());
            progressBar = null;
            progressBarShowing = false;
        }
    }

    public void showToast(String text) {
        showToast(text, TOAST_SHORT_TIME);
    }

    public void showToast(final String text, final long duration) {

        removeToastIfShowing();
        toastShowing = true;
        ENG_Vector2D textSize = new ENG_Vector2D();
        ENG_Viewport viewport =
                ENG_RenderRoot.getRenderRoot()
                        .getCurrentRenderWindow().getViewport(0);
        String resizedText = toastCaption._fitDrawSize(text, viewport.getActualWidth() - 2 * TOAST_MARGIN_WIDTH,
                ENG_TextView.ELLIPSIZE_END_DEFAULT_CHARS, textSize);
        float textWidthInPixels = textSize.x * 0.5f + TOAST_MARGIN_WIDTH;
        ENG_RealRect screenPercentage = ENG_Utility.convertFromActualPixelsToScreenPercentage(new Rect((int) textWidthInPixels, 0, 0, 0));
        float begin = 50.0f - screenPercentage.left;
        float right = 50.0f + screenPercentage.left;
        toastTextView = (ENG_TextView) createView("toastTextView", TEXTVIEW, begin, 60.0f, right, 70.0f, toastLayer);
        toastTextView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
        toastTextView.setTextColor(new ENG_ColorValue(ENG_ColorValue.WHITE));
        toastTextView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        toastTextView.setVerticalAlignment(ENG_TextView.VerticalAlignment.CENTER);
        ENG_ColorValue backgroundColor = ENG_ColorValue.createFromHex(TOAST_BACKGROUND_COLOR_HEX, 1.0f);
        toastTextView.setBackgroundColor(backgroundColor);
        toastTextView.setBackgroundActive(true);
        toastTextView.setText(resizedText);

        toastHandler = new Handler(Looper.getMainLooper());

        toastHandler.postDelayed(() -> {
            toastValueAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
            toastValueAnimator.setDuration(FADE_OUT_TOAST_ANIMATION_DURATION);
            toastValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                private final ENG_ColorValue backgroundColor = new ENG_ColorValue();
                private final ENG_ColorValue textColor = new ENG_ColorValue();

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (toastTextView == null) {
                        return;
                    }
                    toastTextView.getBackgroundColor(backgroundColor);
                    toastTextView.getTextColor(textColor);
                    float animatedValue = (float) animation.getAnimatedValue();
//                        System.out.println("animatedValue: " + animatedValue);
                    backgroundColor.a = animatedValue;
                    textColor.a = animatedValue;
                    toastTextView.setBackgroundColor(backgroundColor);
                    toastTextView.setTextColor(textColor);
                }
            });
            toastValueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    resetAllViewsIgnoreDirty();
                    destroyToastTextView();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    resetAllViewsIgnoreDirty();
                    destroyToastTextView();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
//                toastValueAnimator.setRepeatCount(3);
            toastValueAnimator.start();
        }, duration);

        // If we start animating the toast we must make sure that we also render all the other
        // objects. So we must force a dirty on all elements in the container.
//        markAllViewsIgnoreDirty();
    }

    private void removeToastIfShowing() {
        if (toastShowing) {
            toastHandler.removeCallbacksAndMessages(null);
            if (toastValueAnimator != null && toastValueAnimator.isStarted()) {
                toastValueAnimator.cancel();
            } else {
                destroyToastTextView();
            }
        }
    }

    private void destroyToastTextView() {
        if (toastTextView == null) {
            return;
        }
        if (isDestroyed()) {
            return;
        }
        ENG_Container.this.destroyView(toastTextView.getName());
        toastTextView = null;
        toastShowing = false;
    }

    private void markAllViewsIgnoreDirty() {
        for (ENG_View view : viewList.values()) {
            view.setIgnoreDirty(true);
        }
    }

    private void resetAllViewsIgnoreDirty() {
        for (ENG_View view : viewList.values()) {
            view.setIgnoreDirty(false);
        }
    }

    public void setCurrentFocusedView(ENG_View view) {
        if (!view.isFocusable()) {
            throw new IllegalArgumentException(view.getName() + " is not focusable");
        }
        if (!view.isVisible()) {
            return;
        }
        nextFocusedView = view;
        if (isVisible()) {
            applyCurrentFocusedView();
        } else if (!showKeyboardContainerListenerAdded){
            ENG_ContainerManager.getSingleton().createContainerListener(this, ShowKeyboardContainerListener.ShowKeyboardContainerListenerFactory.TYPE, null);
            showKeyboardContainerListenerAdded = true;
        }

    }

    public void applyCurrentFocusedView() {
        if (nextFocusedView == null) {
//            throw new NullPointerException("Missing nextFocusedView");
            return;
        }
        if (currentFocusedView != null) {
            setViewFocused(currentFocusedView, false);
        }
        currentFocusedView = nextFocusedView;
        setViewFocused(currentFocusedView, true);
        nextFocusedView = null;
    }

    private void setViewFocused(ENG_View view, boolean focused) {
        view.handleOnFocusListener(focused);
        view.clickedState.focused = focused;
    }

    private boolean isPositionCyclingAvailable() {
        return !cyclingViewList.isEmpty();
    }

    private int nextPositionInCycle() {
        ++currentCyclePosition;
        if (currentCyclePosition >= cyclingViewList.size()) {
            currentCyclePosition = 0;
        }
        return currentCyclePosition;
    }

    private int previousPositionInCycle() {
        --currentCyclePosition;
        if (currentCyclePosition < 0) {
            currentCyclePosition = cyclingViewList.size() - 1;
        }
        return currentCyclePosition;
    }

    private int getCurrentCyclePosition() {
        return currentCyclePosition;
    }

    public void setCurrentCyclePosition(int currentCyclePosition) {
        if (currentCyclePosition < 0 || currentCyclePosition >= cyclingViewList.size()) {
            throw new IllegalArgumentException(currentCyclePosition + " is out of range 0 - " + cyclingViewList.size());
        }
        this.currentCyclePosition = currentCyclePosition;
        setCurrentFocusedViewBasedOnCyclePosition(currentCyclePosition);
    }

    private void setCurrentFocusedViewBasedOnCyclePosition(int cyclePosition) {
        setCurrentFocusedView(cyclingViewList.get(cyclePosition));
    }

    private void removeItemFromCyclingList(ENG_View remove) {
        cyclingViewList.remove(remove);
        if (!cyclingViewList.isEmpty()) {
            if (currentCyclePosition >= cyclingViewList.size()) {
                currentCyclePosition = cyclingViewList.size() - 1;
            }
        } else {
            currentCyclePosition = 0;
        }

    }

    public boolean isTabCyclingEnabled() {
        return tabCyclingEnabled;
    }

    public void setTabCyclingEnabled(boolean tabCyclingEnabled) {
        this.tabCyclingEnabled = tabCyclingEnabled;
    }

    /**
     * Only for menu automation
     *
     * @param name
     * @return
     */
    public ENG_View _getView(String name) {
        return viewList.get(name);
    }

    public boolean isVisible() {
        return visible;
    }

    private void setVisible(boolean visible) {
        this.visible = visible;
        screen.setVisible(visible);
    }
}
