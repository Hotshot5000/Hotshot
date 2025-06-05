/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.renderer.ENG_Common.FrameBufferType;
import headwayent.hotshotengine.renderer.ENG_Frustum.OrientationMode;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointerWithSetter;

public class ENG_Viewport implements ENG_NativePointerWithSetter {

    private long ptr;
    private int visibilityMask = ENG_MovableObject.msDefaultVisibilityFlags;

    protected ENG_Camera mCamera;

    protected ENG_RenderTarget mTarget;

    //protected boolean mIsAutoUpdated;
    //protected boolean mShowOverlays;

    protected float mRelLeft, mRelTop, mRelWidth, mRelHeight;
    protected int mActLeft, mActTop, mActWidth, mActHeight;
    protected int mZOrder;
    protected final ENG_ColorValue mBackColour = new ENG_ColorValue(ENG_ColorValue.BLACK);
    protected boolean mClearEveryFrame = true;
    protected int mClearBuffers =
            FrameBufferType.FBT_COLOUR.getType() | FrameBufferType.FBT_DEPTH.getType();
    protected boolean mUpdated;
    protected boolean mShowOverlays = true;
    protected boolean mShowSkies = true;
    protected boolean mShowShadows = true;
    protected int mVisibilityMask = 0xFFFFFFFF;
    // Render queue invocation sequence name
    protected String mRQSequenceName;
    protected ENG_RenderQueueInvocationSequence mRQSequence;
    /// Material scheme
    protected String mMaterialSchemeName = ENG_MaterialManager.DEFAULT_SCHEME_NAME;
    /// Viewport orientation mode
    protected OrientationMode mOrientationMode;
    protected static OrientationMode mDefaultOrientationMode;

    /// Automatic rendering on/off
    protected boolean mIsAutoUpdated = true;

    // DO NOT CALL. JUST FOR GIWS
    public ENG_Viewport() {

    }

    public ENG_Viewport(ENG_Camera cam, ENG_RenderTarget target,
                        int left, int top, int width, int height, int ZOrder) {
        mCamera = cam;
        mTarget = target;
//        mRelLeft = left;
//        mRelTop = top;
//        mRelWidth = width;
//        mRelHeight = height;
        mActLeft = left;
        mActTop = top;
        mActWidth = width;
        mActHeight = height;
        mZOrder = ZOrder;
        // Set the default orientation mode
        mOrientationMode = mDefaultOrientationMode;

        _updateDimensions();

//        cam._notifyViewport(this);
    }

    public boolean _isUpdated() {
        return mUpdated;
    }

    public void _clearUpdateFlag() {
        mUpdated = false;
    }

    public void _updateDimensions() {
//        float height = (float) mTarget.getHeight();
//        float width = (float) mTarget.getWidth();
//
//        mActLeft = (int) (mRelLeft * width);
//        mActTop = (int) (mRelTop * height);
//        mActWidth = (int) (mRelWidth * width);
//        mActHeight = (int) (mRelHeight * height);

        if (mCamera != null) {
            if (mCamera.getAutoAspectRatio()) {
                mCamera.setAspectRatio((float) mActWidth / (float) mActHeight);
            }
        }
        mUpdated = true;
    }

    public int getZOrder() {
        return mZOrder;
    }

    public int _getNumRenderedFaces() {
        return (mCamera != null) ? mCamera._getNumRenderedFaces() : 0;
    }

    public int _getNumRenderedBatches() {
        return (mCamera != null) ? mCamera._getNumRenderedBatches() : 0;
    }

    public void update() {
        if (mCamera != null) {
            mCamera._renderScene(this, mShowOverlays);
        }
    }

    public void setOrientationMode(OrientationMode orientationMode,
                                   boolean setDefault) {
        mOrientationMode = orientationMode;

        if (setDefault) {
            setDefaultOrientationMode(orientationMode);
        }

        if (mCamera != null) {
            mCamera.setOrientationMode(mOrientationMode);
        }
    }

    public void setDefaultOrientationMode(OrientationMode orientationMode) {

        mDefaultOrientationMode = orientationMode;
    }

    public OrientationMode getOrientationMode() {
        return mOrientationMode;
    }

    public OrientationMode getDefaultOrientationMode() {
        return mDefaultOrientationMode;
    }

    public void setBackgroundColour(ENG_ColorValue colour) {
        mBackColour.set(colour);
    }

    public ENG_ColorValue getBackgroundColour() {
        return mBackColour;
    }

    public void setClearEveryFrame(boolean clear) {
        setClearEveryFrame(clear,
                FrameBufferType.FBT_COLOUR.getType() |
                        FrameBufferType.FBT_DEPTH.getType());
    }

    public void setClearEveryFrame(boolean clear, int buffers) {
        mClearEveryFrame = clear;
        mClearBuffers = buffers;
    }

    public boolean getClearEveryFrame() {
        return mClearEveryFrame;
    }

    public int getClearBuffers() {
        return mClearBuffers;
    }

    public void clear(int buffers, ENG_ColorValue col,
                      float depth, short stencil) {
        ENG_RenderSystem rs = ENG_RenderRoot.getRenderRoot().getRenderSystem();
        if (rs != null) {
            ENG_Viewport currentvp = rs._getViewport();
            rs._setViewport(this);
            rs.clearFrameBuffer(buffers, col, depth, stencil);
            if ((currentvp != null) && (currentvp != this)) {
                rs._setViewport(currentvp);
            }
        }
    }

    public void setRenderQueueInvocationSequenceName(String sequenceName) {
        mRQSequenceName = sequenceName;
        if (mRQSequenceName.isEmpty()) {
            mRQSequence = null;
        } else {
            mRQSequence =
                    ENG_RenderRoot.getRenderRoot().getRenderQueueInvocationSequence(
                            sequenceName);
        }
    }

    public String getRenderQueueInvocationSequenceName() {
        return mRQSequenceName;
    }

    public ENG_RenderQueueInvocationSequence _getRenderQueueInvocationSequence() {
        return mRQSequence;
    }

    /** @noinspection SuspiciousNameCombination */
    public void pointOrientedToScreen(ENG_Vector2D v,
                                      int orientationMode, ENG_Vector2D outv) {
        float orX = v.x;
        float orY = v.y;
        switch (orientationMode) {
            case 1:
                outv.x = orY;
                outv.y = 1.0f - orX;
                break;
            case 2:
                outv.x = 1.0f - orX;
                outv.y = 1.0f - orY;
                break;
            case 3:
                outv.x = 1.0f - orY;
                outv.y = orX;
                break;
            default:
                outv.x = orX;
                outv.y = orY;
                break;
        }
    }

    public void setMaterialScheme(String schemeName) {
        mMaterialSchemeName = schemeName;
    }

    public String getMaterialScheme() {
        return mMaterialSchemeName;
    }

    public ENG_RenderTarget getTarget() {
        return mTarget;
    }

    public void setAutoUpdated(boolean autoup) {
        mIsAutoUpdated = autoup;
    }

    public boolean isAutoUpdated() {
        return mIsAutoUpdated;
    }

    public void setOverlaysEnabled(boolean enabled) {
        mShowOverlays = enabled;
    }

    public boolean getOverlaysEnabled() {
        return mShowOverlays;
    }

    /**
     * @return the visibilityMask
     */
    public int getVisibilityMask() {
        return visibilityMask;
    }

    /**
     * @param visibilityMask the visibilityMask to set
     */
    public void setVisibilityMask(int visibilityMask) {
        this.visibilityMask = visibilityMask;
    }

    public void setCamera(ENG_Camera camera) {
        if (mCamera != null) {
            if (mCamera.getViewport() == this) {
                mCamera._notifyViewport(null);
            }
        }
        mCamera = camera;
        _updateDimensions();
        if (camera != null) {
            mCamera._notifyViewport(this);
        }
    }

    public void setDimensions(float left, float top, float width, float height) {
        mRelLeft = left;
        mRelTop = top;
        mRelWidth = width;
        mRelHeight = height;
        _updateDimensions();
    }

    public ENG_Camera getCamera() {
        return mCamera;
    }

    /**
     * @return the mRelLeft
     */
    public float getLeft() {
        return mRelLeft;
    }

    /**
     * @return the mRelTop
     */
    public float getTop() {
        return mRelTop;
    }

    /**
     * @return the mRelWidth
     */
    public float getWidth() {
        return mRelWidth;
    }

    /**
     * @return the mRelHeight
     */
    public float getHeight() {
        return mRelHeight;
    }

    /**
     * @return the mActLeft
     */
    public int getActualLeft() {
        return mActLeft;
    }

    /**
     * @return the mActTop
     */
    public int getActualTop() {
        return mActTop;
    }

    /**
     * @return the mActWidth
     */
    public int getActualWidth() {
        return mActWidth;
    }

    /**
     * @return the mActHeight
     */
    public int getActualHeight() {
        return mActHeight;
    }

    public boolean getShadowsEnabled() {

        return mShowShadows;
    }

    public void setShadowsEnabled(boolean shadowsEnabled) {

        mShowShadows = shadowsEnabled;
    }

    public boolean getSkiesEnabled() {

        return mShowSkies;
    }

    public void setSkiesEnabled(boolean b) {
        mShowSkies = b;
    }

    @Override
    public long getPointer() {
        return ptr;
    }

    @Override
    public void setPointer(long ptr) {
        this.ptr = ptr;
    }

    @Override
    public boolean isNativePointerSet() {
        return true;
    }

    @Override
    public void setNativePointer(boolean set) {

    }
}
