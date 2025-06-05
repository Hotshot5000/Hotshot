/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 4:51 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui.simpleview;

import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container.ViewFactory;
import headwayent.hotshotengine.gorillagui.ENG_Layer;
import headwayent.hotshotengine.gorillagui.ENG_MarkupText;
import headwayent.hotshotengine.gorillagui.ENG_Rectangle;

public class ENG_ImageTextView extends ENG_TextView {

    public static final float DEFAULT_SPACING = 5.0f;
    public static final float IMAGE_SIZE = 65.0f;

    public static class ImageTextViewFactory extends ViewFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_View createView(String name, ENG_Layer layer,
                                   ENG_Container parent, Bundle bundle, ENG_View parentView) {

            ENG_ImageTextView imageTextView = new ENG_ImageTextView(name, layer, parent, parentView);
            imageTextView.setViewType(ENG_Container.ViewType.VIEW_IMAGETEXTVIEW);
            return imageTextView;
        }

        @Override
        public void destroyView(ENG_View view) {
            
            view.destroy();
        }

    }

    private float imageSize = IMAGE_SIZE;
    private float defaultSpacing = DEFAULT_SPACING;
    private final ENG_Rectangle imageRect;
    private String imageName;

    public ENG_ImageTextView(String name, ENG_Layer layer,
                             ENG_Container parent, ENG_View parentView) {
        super(name, layer, parent, parentView);

        imageRect = layer.createRectangle(0, 0, 0, 0);
    }

    @Override
    public void destroy() {
        
        super.destroy();
        getLayer().destroyRectangle(imageRect);
    }

    @Override
    public void update(int screenWidth, int screenHeight) {
        
        super.update(screenWidth, screenHeight);
        imageRect.setVisible(isVisible());
        if (!isVisible()) {
            return;
        }
        float left = getActLeft();
        float top = getActTop();
        float right = getActRight();
        float bottom = getActBottom();
        float width = right - left;
        float height = bottom - top;
        if (width < imageSize || height < imageSize) {
            imageSize = Math.min(width, height);
        }
        // Centre the imagebox on the left side
        float imageTop = height / 2 - imageSize / 2 + top;
        imageRect.left(left);
        imageRect.top(imageTop);
        imageRect.width(imageSize);
        imageRect.height(imageSize);

        float markupTextLeft = left + imageSize + defaultSpacing;
        float markupTextWidth = width - markupTextLeft;
        ENG_MarkupText mt = _getMarkupText();
        mt.left(markupTextLeft);
        mt.top(top);
        mt.width(markupTextWidth);
        mt.height(height);
    }

    public float getImageSize() {
        return imageSize;
    }

    public void setImageSize(float imageSize) {
        this.imageSize = imageSize;
        markDirty();
    }

    public float getDefaultSpacing() {
        return defaultSpacing;
    }

    public void setDefaultSpacing(float defaultSpacing) {
        this.defaultSpacing = defaultSpacing;
        markDirty();
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
        imageRect.backgroundImage(imageName);
        markDirty();
    }

}
