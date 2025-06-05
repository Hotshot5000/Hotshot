/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:46 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.TreeMap;

public abstract class ENG_RenderWindow extends ENG_RenderTarget {

    protected boolean mIsFullScreen;
    protected boolean mIsPrimary;
    protected boolean mAutoDeactivatedOnFocusChange;
    protected int mLeft;
    protected int mTop;

    protected void _setPrimary() {
        mIsPrimary = true;
    }

    public ENG_RenderWindow() {
        mAutoDeactivatedOnFocusChange = true;
    }

    public abstract void create(String name, int width, int height, boolean fullScreen,
                                TreeMap<String, String> miscParams);

    public void setFullScreen(boolean fullScreen, int width, int height) {

    }

    public abstract void destroy(boolean skipGLDelete);

    public abstract void resize(int width, int height);

    public void windowMovedOrResized() {

    }

    public abstract void reposition(int left, int top);

    public boolean isVisible() {
        return true;
    }

    public void setVisible() {

    }

    public boolean isActive() {
        return mActive && isVisible();
    }

    public abstract boolean isClosed();

    public boolean isPrimary() {
        return mIsPrimary;
    }

    public boolean isFullScreen() {
        return mIsFullScreen;
    }

    public void getMetrics(int[] width, int widthOffset, int[] height, int heightOffset,
                           int[] colourDepth, int depthOffset, int[] left, int leftOffset,
                           int[] top, int topOffset) {
        width[widthOffset] = mWidth;
        height[heightOffset] = mHeight;
        colourDepth[depthOffset] = mColourDepth;
        left[leftOffset] = mLeft;
        top[topOffset] = mTop;
    }

    public boolean isDeactivatedOnFocusChange() {
        return mAutoDeactivatedOnFocusChange;
    }

    public void setDeactivateOnFocusChange(boolean deactivate) {
        mAutoDeactivatedOnFocusChange = deactivate;
    }
}
