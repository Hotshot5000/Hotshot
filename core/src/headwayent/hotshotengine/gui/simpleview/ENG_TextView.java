/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui.simpleview;


import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.gui.simpleview.ENG_Container.ViewFactory;
import headwayent.hotshotengine.gorillagui.ENG_Caption;
import headwayent.hotshotengine.gorillagui.ENG_Layer;
import headwayent.hotshotengine.gorillagui.ENG_MarkupText;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

public class ENG_TextView extends ENG_View {

    public static final String SCROLLBAR_ADDED = "scrollbarAdded";
    public static final String ELLIPSIZE_END_DEFAULT_CHARS = "...";

    public static class TextViewFactory extends ViewFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_View createView(String name, ENG_Layer layer,
                                   ENG_Container parent, Bundle bundle, ENG_View parentView) {

            ENG_TextView textView = new ENG_TextView(name, layer, parent, parentView);
            textView.setViewType(ENG_Container.ViewType.VIEW_TEXTVIEW);
            return textView;
        }

        @Override
        public void destroyView(ENG_View view) {

            view.destroy();
        }

    }

    public static class ScrollbarClickListener implements OnClickListener {

        private final ENG_TextView textView;
//		private ENG_MarkupText markupText;

        public ScrollbarClickListener(ENG_TextView textView) {
            this.textView = textView;
        }

        @Override
        public boolean onClick(int x, int y) {

            // We register clicks on the surface of the text view but
            // we need to pinpoint to see if the user actually hit the
            // up or down arrows
            if (textView._getMarkupText() != null) {
                if (textView._getMarkupText().getScrollUpButton().inside(x, y)) {
                    textView._getMarkupText().previousLine();
                    textView.markDirty();
                    return true;
                } else if (textView._getMarkupText().getScrollDownButton().inside(x, y)) {
                    textView._getMarkupText().nextLine();
                    textView.markDirty();
                    return true;
                }
            }
            return false;
        }

        public ENG_TextView getTextView() {
            return textView;
        }
    }

    public enum HorizontalAlignment {
        LEFT, CENTER, RIGHT
    }

    public enum VerticalAlignment {
        TOP, CENTER, BOTTOM
    }

    public enum Ellipsize {
        NONE,
        END,
        MARQUEE // To be implemented
    }

    private String text = "";
    private ENG_MarkupText markupText;
    private int textSize;
    private boolean addScrollbarListener = true;
    private final ENG_ColorValue textColor = new ENG_ColorValue(ENG_ColorValue.WHITE);
    private boolean textColorChanged = true;
    private boolean skipNewLines;
    private final OnClickListener scrollbarClickListener = new ScrollbarClickListener(this);
    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
    private VerticalAlignment verticalAlignment = VerticalAlignment.TOP;
    private Ellipsize ellipsize = Ellipsize.NONE;
    private String ellipsizeEndChars = ELLIPSIZE_END_DEFAULT_CHARS;
    private ENG_Caption ellipsizeCaption;
    private ENG_Vector2D ellipsizeVec;

    public ENG_TextView(String name, ENG_Layer layer, ENG_Container parent, ENG_View parentView) {
        super(name, layer, parent, parentView);

        addEventListener(SCROLLBAR_ADDED, () -> {

            if (isAddScrollbarListener()) {
                // markupText can be null if the textView is used as a toast. Update was never called in that case but the event listener
                // has been added leading to a NullPointerException.
                if (markupText != null) {
                    if (markupText.isLineBreaksCalculated()) {
                        if (markupText.isScrollbarActive() && getOnClickListener() == null) {
                            setOnClickListener(scrollbarClickListener);
                        }
                    } else {
                        setOnClickListener(null);
                    }
                }
            }
        });
    }

    @Override
    public void destroy() {

        super.destroy();
        // Maybe it has already been removed
        if (hasEventListener(SCROLLBAR_ADDED)) {
            removeEventListener(SCROLLBAR_ADDED);
        }
        if (markupText != null) {
            getLayer().destroyMarkupText(markupText);
        }
        if (ellipsizeCaption != null) {
            getLayer().destroyCaption(ellipsizeCaption);
        }
    }

    @Override
    public void update(int screenWidth, int screenHeight) {

        super.update(screenWidth, screenHeight);
        if (markupText == null) {
            markupText = getLayer().createMarkupText(getTextSize(), 0, 0, text);
//					new ENG_MarkupText(
//					getTextSize(), 0, 0, text, getLayer());
        }
//		if (isDirty()) {
        float left = getActLeft();//ENG_Math.floor(screenWidth * getLeft());
        float top = getActTop();//ENG_Math.floor(screenHeight * getTop());
        float width = getActRight() - left;//ENG_Math.floor(screenWidth * getRight() - left);
        float height = getActBottom() - top;//ENG_Math.floor(screenHeight * getBottom() - top);
        markupText.setVisible(isVisible());
        if (!isVisible()) {
            return;
        }
        markupText.left(left);
        markupText.top(top);
        markupText.width(width);
        markupText.height(height);
        String resizedText = text;
        if (ellipsize != Ellipsize.NONE) {
            if (ellipsizeCaption == null) {
                ellipsizeCaption = getLayer().createCaption(getTextSize(), 0, 0, "");
                ellipsizeVec = new ENG_Vector2D();
            }
            if (ellipsize == Ellipsize.MARQUEE) {
                throw new UnsupportedOperationException("Not implemented");
            } else {
                String ellipsizeChars = "";
                if (ellipsize == Ellipsize.END) {
                    ellipsizeChars = ellipsizeEndChars;
                }
                resizedText = ellipsizeCaption._fitDrawSize(text, width, ellipsizeChars, ellipsizeVec);
            }

        }

        markupText.text(resizedText, skipNewLines);
        // If the line count is bigger than 1 then ignore any Alignment for now
        if (markupText.getLineNum() == 1) {
            float maxTextWidth = markupText.maxTextWidth();
            float maxTextHeight = markupText.maxTextHeight();
            float textLeft = 0.0f, textTop = 0.0f;
            switch (horizontalAlignment) {
                case LEFT:
                    break;
                case CENTER:
                    textLeft = width / 2 - maxTextWidth / 2;
                    break;
                case RIGHT:
                    textLeft = width - maxTextWidth;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            switch (verticalAlignment) {
                case TOP:
                    break;
                case CENTER:
                    textTop = height / 2 - maxTextHeight / 2;
                    break;
                case BOTTOM:
                    textTop = height - maxTextHeight;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            markupText.left(left + textLeft);
            markupText.top(top + textTop);
        }
//			resetDirty();
//		}
        if (textColorChanged) {
            markupText.setTextColor(textColor);
            markupText.setTextColorSet(true);
            textColorChanged = false;
        }

    }

    public ENG_MarkupText _getMarkupText() {
        return markupText;
    }

    public void setText(String text) {
        setText(text, true);
    }

    public void setText(String text, boolean skipNewLines) {
        this.text = text;
        this.skipNewLines = skipNewLines;
        markDirty();
    }

    public String getText() {
        return text;
    }

    public int getTextSize() {
        return textSize;
    }

    /**
     * Make sure to set the textsize before calling update.
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public boolean isAddScrollbarListener() {
        return addScrollbarListener;
    }

    public void setAddScrollbarListener(boolean addScrollbarListener) {
        this.addScrollbarListener = addScrollbarListener;
    }

    public void setTextColor(ENG_ColorValue c) {
        textColor.set(c);
        textColorChanged = true;
        markDirty();
    }

    public void getTextColor(ENG_ColorValue ret) {
        ret.set(textColor);
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        markDirty();
    }

    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        markDirty();
    }

    public Ellipsize getEllipsize() {
        return ellipsize;
    }

    public void setEllipsize(Ellipsize ellipsize) {
        this.ellipsize = ellipsize;
        markDirty();
    }

    public String getEllipsizeEndChars() {
        return ellipsizeEndChars;
    }

    public void setEllipsizeEndChars(String ellipsizeEndChars) {
        this.ellipsizeEndChars = ellipsizeEndChars;
        markDirty();
    }
}
