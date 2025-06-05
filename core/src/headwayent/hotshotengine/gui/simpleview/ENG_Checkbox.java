/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 4:51 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui.simpleview;

import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.ENG_RealRect;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.gui.simpleview.ENG_Container.ViewFactory;
import headwayent.hotshotengine.gorillagui.ENG_Layer;
import headwayent.hotshotengine.gorillagui.ENG_MarkupText;
import headwayent.hotshotengine.gorillagui.ENG_Rectangle;
import headwayent.hotshotengine.statedebugger.ENG_FrameInterval;

import static headwayent.hotshotengine.ENG_Utility.currentTimeMillis;

public class ENG_Checkbox extends ENG_TextView {

    public static class CheckboxFactory extends ViewFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_View createView(String name, ENG_Layer layer,
                                   ENG_Container parent, Bundle bundle, ENG_View parentView) {

            ENG_Checkbox checkbox = new ENG_Checkbox(name, layer, parent, parentView);
            checkbox.setViewType(ENG_Container.ViewType.VIEW_CHECKBOX);
            return checkbox;
        }

        @Override
        public void destroyView(ENG_View view) {

            view.destroy();
        }

    }

    public static class CheckboxClickListener extends ScrollbarClickListener {

        private static final int WAIT_BETWEEN_CLICKS_DELAY = 100;
        private long lastTime;

        public CheckboxClickListener(ENG_TextView textView) {
            super(textView);
            
        }

        @Override
        public boolean onClick(int x, int y) {

            boolean ret = super.onClick(x, y);
            ENG_Checkbox box = (ENG_Checkbox) getTextView();
            if (ENG_Utility.hasTimePassed(ENG_FrameInterval.CHECKBOX_WAIT_BETWEEN_CLICKS_DELAY + getTextView().getName(), lastTime, WAIT_BETWEEN_CLICKS_DELAY) &&
                    box.getCheckboxRect().inside(x, y)) {
                lastTime = currentTimeMillis();
                box.setChecked(!box.isChecked());
                return true;
            }
            return ret;
        }

    }

    public static final float DEFAULT_SPACING = 5.0f;

    public static final float CHECKBOX_SIZE = 35.0f;

    private boolean checked;
    private final ENG_Rectangle uncheckedRect;
    private final ENG_Rectangle checkedRect;
    private float checkboxSize = CHECKBOX_SIZE;
    private float defaultSpacing = DEFAULT_SPACING;
    private final ENG_RealRect checkboxRect = new ENG_RealRect();

    public ENG_Checkbox(String name, ENG_Layer layer, ENG_Container parent,
                        ENG_View parentView) {
        super(name, layer, parent, parentView);
        
        uncheckedRect = layer.createRectangle(0, 0, 0, 0);
        checkedRect = layer.createRectangle(0, 0, 0, 0);
        uncheckedRect.backgroundImage("checkbox_not_pressed");
        checkedRect.backgroundImage("checkbox_pressed");
        uncheckedRect.setVisible(false);
        checkedRect.setVisible(false);
        // Never forget to remove everything from textView default scrollbar
        // event listener
        removeAllEventListeners();
        OnClickListener clickListener = new CheckboxClickListener(this);
        setOnClickListener(clickListener);
    }

    @Override
    public void destroy() {

        super.destroy();
        getLayer().destroyRectangle(checkedRect);
        getLayer().destroyRectangle(uncheckedRect);
    }

    @Override
    public void update(int screenWidth, int screenHeight) {

        super.update(screenWidth, screenHeight);
        checkedRect.setVisible(isVisible());
        uncheckedRect.setVisible(isVisible());
        if (!isVisible()) {
            return;
        }
        float left = getActLeft();
        float top = getActTop();
        float right = getActRight();
        float bottom = getActBottom();
        float width = right - left;
        float height = bottom - top;
        if (width < checkboxSize || height < checkboxSize) {
            checkboxSize = Math.min(width, height);
        }
        // Centre the checkbox on the left side
        float checkboxTop = height / 2 - checkboxSize / 2 + top;
        checkedRect.left(left);
        checkedRect.top(checkboxTop);
        checkedRect.width(checkboxSize);
        checkedRect.height(checkboxSize);
        uncheckedRect.left(left);
        uncheckedRect.top(checkboxTop);
        uncheckedRect.width(checkboxSize);
        uncheckedRect.height(checkboxSize);
        if (checked) {
            checkedRect.setVisible(true);
            uncheckedRect.setVisible(false);
        } else {
            checkedRect.setVisible(false);
            uncheckedRect.setVisible(true);
        }
        checkboxRect.set(left, checkboxTop,
                left + checkboxSize,
                checkboxTop + checkboxSize);
        float markupTextLeft = left + checkboxSize + defaultSpacing;
        float markupTextWidth = width - markupTextLeft;
        ENG_MarkupText mt = _getMarkupText();
        mt.left(markupTextLeft);
        mt.top(top);
        mt.width(markupTextWidth);
        mt.height(height);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            markDirty();
        }

    }

    public float getCheckboxSize() {
        return checkboxSize;
    }

    public void setCheckboxSize(float checkboxSize) {
        this.checkboxSize = checkboxSize;
        markDirty();
    }

    public float getDefaultSpacing() {
        return defaultSpacing;
    }

    public void setDefaultSpacing(float defaultSpacing) {
        this.defaultSpacing = defaultSpacing;
        markDirty();
    }

    protected ENG_RealRect getCheckboxRect() {
        return checkboxRect;
    }

}
