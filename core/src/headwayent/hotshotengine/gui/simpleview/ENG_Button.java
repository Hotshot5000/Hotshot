/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui.simpleview;

import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container.ViewFactory;
import headwayent.hotshotengine.gorillagui.*;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

public class ENG_Button extends ENG_TextView {

    public static class ButtonViewFactory extends ViewFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_View createView(String name, ENG_Layer layer,
                                   ENG_Container parent, Bundle bundle, ENG_View parentView) {

            ENG_Button button = new ENG_Button(name, layer, parent, parentView);
            button.setViewType(ENG_Container.ViewType.VIEW_BUTTON);
            return button;
        }

        @Override
        public void destroyView(ENG_View view) {

            view.destroy();
        }

    }

//	private ENG_Rectangle rect;
//	private ENG_ColorValue backgroundColor = new ENG_ColorValue(ENG_ColorValue.WHITE);
//	private float borderWidth;
//	private ENG_ColorValue borderColor = new ENG_ColorValue(ENG_ColorValue.WHITE);
////	private ENG_ColorValue textColor = new ENG_ColorValue(ENG_ColorValue.BLACK);
//    private ENG_NinePatch ninePatch;
//    private ENG_TextureAtlas ninePatchAtlas;
//    private String ninePatchName;


    public ENG_Button(String name, ENG_Layer layer, ENG_Container parent,
                      ENG_View parentView) {
        super(name, layer, parent, parentView);
        
//		rect = layer.createRectangle(0, 0, 0, 0);
        // Default to black since the rect is white
        setTextColor(ENG_ColorValue.BLACK);
        removeAllEventListeners();
        setHorizontalAlignment(HorizontalAlignment.CENTER);
        setVerticalAlignment(VerticalAlignment.CENTER);
    }

    @Override
    public void destroy() {

        super.destroy();
//		getLayer().destroyRectangle(rect);
    }

    @Override
    public void update(int screenWidth, int screenHeight) {

        super.update(screenWidth, screenHeight);
        setBackgroundActive(isVisible());
//        rect.setVisible(isVisible());
//        if (!isVisible()) {
//            return;
//        }
//		float left = getActLeft();
//		float top = getActTop();
//		float right = getActRight();
//		float bottom = getActBottom();
//		float width = right - left;
//		float height = bottom - top;
//		rect.left(left);
//		rect.top(top);
//		rect.width(width);
//		rect.height(height);
//		rect.backgroundColour(backgroundColor);
//		rect.border(borderWidth, borderColor);
//		ENG_MarkupText mt = getMarkupText();
//        float maxTextWidth = mt.maxTextWidth();
//        float maxTextHeight = mt.maxTextHeight();
//        if (ninePatch != null) {
//            rect.backgroundImage(ninePatch, ninePatchAtlas.getName());
//        }
        // No need to set the alignment every frame. Set it in the constructor.
//        if (getHorizontalAlignment() != HorizontalAlignment.CENTER) {
//            setHorizontalAlignment(HorizontalAlignment.CENTER);
//        }
//        if (getVerticalAlignment() != VerticalAlignment.CENTER) {
//            setVerticalAlignment(VerticalAlignment.CENTER);
//        }
//		float maxTextWidth = mt.maxTextWidth();
//		float maxTextHeight = mt.maxTextHeight();
//		float textLeft = width / 2 - maxTextWidth / 2;
//		float textTop = height / 2 - maxTextHeight / 2;
//		if (textLeft < 0 || textTop < 0) {
        // We are out of the button
//			throw new IllegalArgumentException(mt.text() + " gets out of range " +
//					"for width " + width + " and height " + height + 
//					" with maxTextWidth " + maxTextWidth + 
//					" and maxTextHeight " + maxTextHeight);
//		}
//		mt.left(textLeft + left);
//		mt.top(textTop + top);
//		mt.setTextColor(textColor);
    }

//	public void setBackgroundColor(ENG_ColorValue c) {
//		backgroundColor.set(c);
//		markDirty();
//	}
//
//	public void getBackgroundColor(ENG_ColorValue c) {
//		c.set(backgroundColor);
//	}
//
//	public ENG_ColorValue getBackgroundColor() {
//		return new ENG_ColorValue(backgroundColor);
//	}
//
//	public void setBorderWidth(float width) {
//		borderWidth = width;
//		markDirty();
//	}
//
//	public float getBorderWidth() {
//		return borderWidth;
//	}
//
//	public void setBorderColor(ENG_ColorValue c) {
//		borderColor.set(c);
//		markDirty();
//	}
//
//	public void getBorderColor(ENG_ColorValue ret) {
//		ret.set(borderColor);
//	}
//
//	public ENG_ColorValue getBorderColor() {
//		return new ENG_ColorValue(borderColor);
//	}
//
//    public void setNinePatchBackground(String ninePatch) {
//        ENG_LayerContainer.NinePatchAndAtlas ninePatchAndAtlas =
//                getLayer().getParent().getNinePatch(ninePatch);
//        if (ninePatchAndAtlas == null) {
//            throw new IllegalArgumentException("NinePatch with name: " + ninePatch +
//                    " does not exist");
//        }
//        this.ninePatch = ninePatchAndAtlas.ninePatch;
//        this.ninePatchAtlas = ninePatchAndAtlas.atlas;
//
//        ninePatchName = ninePatch;
//        markDirty();
//    }
//
//    public String getNinePatchBackground() {
//        return ninePatchName;
//    }

//	public void setTextColor(ENG_ColorValue c) {
//		textColor.set(c);
//		markDirty();
//	}
//	
//	public void getTextColor(ENG_ColorValue ret) {
//		ret.set(textColor);
//	}
//	
//	public ENG_ColorValue getTextColor() {
//		return new ENG_ColorValue(textColor);
//	}

}
