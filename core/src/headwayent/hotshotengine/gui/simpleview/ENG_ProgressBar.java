/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 1/10/21, 1:50 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui.simpleview;

import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gorillagui.ENG_Layer;
import headwayent.hotshotengine.gorillagui.ENG_Rectangle;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

/**
 * Created by sebas on 30.10.2015.
 */
public class ENG_ProgressBar extends ENG_View {

    private final ENG_Rectangle boxRectangle;
    private final ENG_Rectangle progressRectangle;
    private int progress;

    public static class ProgressBarFactory extends ENG_Container.ViewFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_View createView(String name, ENG_Layer layer, ENG_Container parent, Bundle bundle, ENG_View parentView) {
            ENG_ProgressBar progressBar = new ENG_ProgressBar(name, layer, parent, bundle, parentView);
            progressBar.setViewType(ENG_Container.ViewType.VIEW_PROGRESSBAR);
            return progressBar;
        }

        @Override
        public void destroyView(ENG_View view) {
            view.destroy();
        }
    }

    /** @noinspection deprecation*/
    public ENG_ProgressBar(String name, ENG_Layer layer, ENG_Container parent, Bundle bundle, ENG_View parentView) {
        super(name, layer, parent, parentView);

        boxRectangle = layer.createRectangle(0, 0, 0, 0);
        progressRectangle = layer.createRectangle(0, 0, 0, 0, 1);

        boxRectangle.backgroundColour(ENG_ColorValue.BLACK);
        boxRectangle.border(1.0f, ENG_ColorValue.WHITE);

        progressRectangle.backgroundColour(ENG_ColorValue.GREEN);
    }

    @Override
    public void destroy() {
        super.destroy();

        ENG_Layer layer = getLayer();
        layer.destroyRectangle(boxRectangle);
        layer.destroyRectangle(progressRectangle);
    }

    @Override
    public void update(int screenWidth, int screenHeight) {
        super.update(screenWidth, screenHeight);

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

        float progressWidth = ((float) progress) * 0.01f * width;
        progressRectangle.left(left);
        progressRectangle.top(top);
        progressRectangle.width(progressWidth);
        progressRectangle.height(height);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        if (progress < 0) {
            progress = 0;
        } else if (progress > 100) {
            progress = 100;
        }
        if (this.progress != progress) {
            this.progress = progress;
            markDirty();
        }
    }
}
