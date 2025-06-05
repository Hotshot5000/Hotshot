/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui.simpleview;

import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.ENG_RealRect;
import headwayent.hotshotengine.gorillagui.*;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebas on 16.08.2015.
 */
public class ENG_DropdownList extends ENG_TextView {

    public static final String TEXT_LIST = "text_list";

    public static class DropdownListFactory extends ENG_Container.ViewFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_View createView(String name, ENG_Layer layer, ENG_Container parent, Bundle bundle, ENG_View parentView) {
            ENG_DropdownList dropdownList = new ENG_DropdownList(name, layer, parent, bundle, parentView);
            dropdownList.setViewType(ENG_Container.ViewType.VIEW_DROPDOWNLIST);
            return dropdownList;
        }

        @Override
        public void destroyView(ENG_View view) {
            view.destroy();
        }
    }

    public static final float BOX_RECTANGLE_BORDER_WIDTH_DEFAULT = 1.0f;
    public static final ENG_ColorValue BOX_RECTANGLE_BACKGROUND_COLOR_DEFAULT = ENG_ColorValue.BLACK;
    public static final ENG_ColorValue BOX_RECTANGLE_BORDER_COLOR_DEFAULT = ENG_ColorValue.WHITE;
    public static final float DROPDOWN_BUTTON_LEN = 20.0f;
    private static final float SCROLL_ARROW_LEN = 20.0f;
    private String text;
    private final ArrayList<String> textList = new ArrayList<>();
    private int currentElem;
    private ENG_Caption textCaption;
    private final ENG_Rectangle boxRectangle;
    private final ENG_Rectangle scrollUpRectangle;
    private final ENG_Rectangle scrollDownRectangle;
    private final ENG_RealRect scrollUpButton = new ENG_RealRect();
    private final ENG_RealRect scrollDownButton = new ENG_RealRect();
    private float scrollbarWidth = SCROLL_ARROW_LEN;
    private boolean dropdownVisible; // For future use when we actually make the list to drop down.

    /** @noinspection deprecation*/
    public ENG_DropdownList(String name, ENG_Layer layer, ENG_Container parent, Bundle bundle, ENG_View parentView) {
        super(name, layer, parent, parentView);

        List<String> textList = (List<String>) bundle.getObject(TEXT_LIST);
        if (textList != null) {
            this.textList.addAll(textList);
        }

        boxRectangle = layer.createRectangle(0, 0, 0, 0);
        scrollUpRectangle = layer.createRectangle(0, 0, 0, 0, 1);
        scrollDownRectangle = layer.createRectangle(0, 0, 0, 0, 1);

        boxRectangle.backgroundColour(BOX_RECTANGLE_BACKGROUND_COLOR_DEFAULT);
//        scrollUpRectangle.backgroundColour(ENG_ColorValue.BLACK);
//        scrollDownRectangle.backgroundColour(ENG_ColorValue.BLACK);

        boxRectangle.border(BOX_RECTANGLE_BORDER_WIDTH_DEFAULT, BOX_RECTANGLE_BORDER_COLOR_DEFAULT);

        ENG_Sprite up = layer.getAtlas().getSprite(ENG_MarkupText.scrollUpSprite);
        ENG_Sprite down = layer.getAtlas().getSprite(ENG_MarkupText.scrollDownSprite);
        scrollUpRectangle.backgroundImage(up);
        scrollDownRectangle.backgroundImage(down);
//        scrollUpRectangle.backgroundColour(ENG_ColorValue.WHITE);
//        scrollDownRectangle.backgroundColour(ENG_ColorValue.BLACK);

        setFocusable(false);

        removeAllEventListeners();

        setOnClickListener((x, y) -> {
            if (scrollDownButton.inside(x, y)) {
                nextElement();
            } else if (scrollUpButton.inside(x, y)) {
                previousElement();
            } else {
                nextElement();
            }
            return true;
        });
    }

    @Override
    public void destroy() {
        super.destroy();

        ENG_Layer layer = getLayer();
        layer.destroyRectangle(scrollUpRectangle);
        layer.destroyRectangle(scrollDownRectangle);
        layer.destroyRectangle(boxRectangle);
        if (textCaption != null) {
            layer.destroyCaption(textCaption);
        }
    }

    private void createCaption() {
        if (textCaption == null) {
            textCaption = getLayer().createCaption(getTextSize(), 0, 0, getCurrentElementText());
        }
    }

    @Override
    public void update(int screenWidth, int screenHeight) {
        super.update(screenWidth, screenHeight);
        createCaption();
        textCaption.text(getCurrentElementText());
        float left = getActLeft();
        float top = getActTop();
        float right = getActRight();
        float bottom = getActBottom();
        float width = right - left;
        float height = bottom - top;
        boxRectangle.left(left);
        boxRectangle.top(top);
        boxRectangle.width(width);
        boxRectangle.height(height);
        textCaption.left(left);
        textCaption.top(top);
        textCaption.width(width);
        textCaption.height(height);
        scrollUpRectangle.left(left + width - 2 * height);
        scrollUpRectangle.top(top);
        scrollUpRectangle.width(height);
        scrollUpRectangle.height(height);
        scrollUpButton.set(scrollUpRectangle.left(),
                scrollUpRectangle.top(),
                scrollUpRectangle.left() + scrollUpRectangle.width(),
                scrollUpRectangle.top() + scrollUpRectangle.height());
        scrollDownRectangle.left(left + width - height);
        scrollDownRectangle.top(top);
        scrollDownRectangle.width(height);
        scrollDownRectangle.height(height);
        scrollDownButton.set(scrollDownRectangle.left(),
                scrollDownRectangle.top(),
                scrollDownRectangle.left() + scrollDownRectangle.width(),
                scrollDownRectangle.top() + scrollDownRectangle.height());

    }

    public void addElement(String elem) {
        if (elem == null || elem.isEmpty()) {
            throw new IllegalArgumentException("element cannot be null or empty");
        }
        textList.add(elem);
        markDirty();
    }

    private void nextElement() {
        if (++currentElem >= textList.size()) {
            currentElem = 0;
        }
        markDirty();
    }

    private void previousElement() {
        if (--currentElem < 0) {
            currentElem = textList.size() - 1;
        }
        markDirty();
    }

    public void setCurrentElement(int pos) {
        if (currentElem == pos || pos < 0 || pos >= textList.size()) {
            return;
        }
        currentElem = pos;
        markDirty();
    }

    public int getCurrentElementPos() {
        return currentElem;
    }

    public int getListSize() {
        return textList.size();
    }

    public String getCurrentElementText() {
        if (textList.isEmpty()) {
            return null;
        }
        return textList.get(currentElem);
    }

    public float getScrollbarWidth() {
        return scrollbarWidth;
    }

    public void setScrollbarWidth(float scrollbarWidth) {
        this.scrollbarWidth = scrollbarWidth;
    }
}
