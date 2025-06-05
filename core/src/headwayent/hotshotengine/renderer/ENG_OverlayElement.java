/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_ParamCommand;
import headwayent.hotshotengine.ENG_ParamDictionary;
import headwayent.hotshotengine.ENG_ParameterDef;
import headwayent.hotshotengine.ENG_ParameterDef.ParameterType;
import headwayent.hotshotengine.ENG_RealRect;
import headwayent.hotshotengine.ENG_StringIntefaceInterface;
import headwayent.hotshotengine.ENG_StringInterface;
import headwayent.hotshotengine.renderer.ENG_RenderQueue.RenderQueueGroupID;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointerWithSetter;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

import java.util.ArrayList;

public abstract class ENG_OverlayElement extends ENG_RenderableImpl implements
        ENG_StringIntefaceInterface, ENG_NativePointerWithSetter {

    /**
     * Enum describing how the position / size of an element is to be recorded.
     */
    public enum GuiMetricsMode {
        /// 'left', 'top', 'height' and 'width' are parametrics from 0.0 to 1.0
        GMM_RELATIVE(0),
        /// Positions & sizes are in absolute pixels
        GMM_PIXELS(1),
        /// Positions & sizes are in virtual pixels
        GMM_RELATIVE_ASPECT_ADJUSTED(2);

        private final byte pos;

        GuiMetricsMode(int b) {
            pos = (byte) b;
        }

        public byte getPos() {
            return pos;
        }

        public static GuiMetricsMode get(byte b) {
            switch (b) {
                case 0:
                    return GMM_RELATIVE;
                case 1:
                    return GMM_PIXELS;
                case 2:
                    return GMM_RELATIVE_ASPECT_ADJUSTED;
            }
            throw new IllegalArgumentException(b + " is an invalid GuiMetricsMode");
        }
    }

    /**
     * Enum describing where '0' is in relation to the parent in the horizontal dimension.
     *
     * @remarks Affects how 'left' is interpreted.
     */
    public enum GuiHorizontalAlignment {
        GHA_LEFT(0),
        GHA_CENTER(1),
        GHA_RIGHT(2);

        private final byte pos;

        GuiHorizontalAlignment(int b) {
            pos = (byte) b;
        }

        public byte getPos() {
            return pos;
        }

        public static GuiHorizontalAlignment get(byte b) {
            switch (b) {
                case 0:
                    return GHA_LEFT;
                case 1:
                    return GHA_CENTER;
                case 2:
                    return GHA_RIGHT;
            }
            throw new IllegalArgumentException(b + " is an invalid GuiHorizontalAlignment");
        }
    }

    /**
     * Enum describing where '0' is in relation to the parent in the vertical dimension.
     *
     * @remarks Affects how 'top' is interpreted.
     */
    public enum GuiVerticalAlignment {
        GVA_TOP(0),
        GVA_CENTER(1),
        GVA_BOTTOM(2);

        private final byte pos;

        GuiVerticalAlignment(int b) {
            pos = (byte) b;
        }

        public byte getPos() {
            return pos;
        }

        public static GuiVerticalAlignment get(byte b) {
            switch (b) {
                case 0:
                    return GVA_TOP;
                case 1:
                    return GVA_CENTER;
                case 2:
                    return GVA_BOTTOM;
            }
            throw new IllegalArgumentException(b + " is an invalid GuiVerticalAlignment");
        }
    }

    public static class CmdLeft implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_OverlayElement) target).getLeft());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_OverlayElement) target).setLeft(Float.parseFloat(val));
        }

    }

    public static class CmdTop implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_OverlayElement) target).getTop());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_OverlayElement) target).setTop(Float.parseFloat(val));
        }

    }

    public static class CmdWidth implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_OverlayElement) target).getWidth());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_OverlayElement) target).setWidth(Float.parseFloat(val));
        }

    }

    public static class CmdHeight implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_OverlayElement) target).getHeight());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_OverlayElement) target).setHeight(Float.parseFloat(val));
        }

    }

    public static class CmdMaterial implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return ((ENG_OverlayElement) target).getMaterialName();
        }

        @Override
        public void doSet(Object target, String val) {

            if (!val.isEmpty()) {
                ((ENG_OverlayElement) target).setMaterialName(val);
            }
        }

    }

    public static class CmdCaption implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return ((ENG_OverlayElement) target).getCaption();
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_OverlayElement) target).setCaption(val);
        }

    }

    public static class CmdMetricsMode implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            GuiMetricsMode mode = ((ENG_OverlayElement) target).getMetricsMode();
            switch (mode) {
                case GMM_PIXELS:
                    return "pixels";
                case GMM_RELATIVE_ASPECT_ADJUSTED:
                    return "relative_aspect_adjusted";
                case GMM_RELATIVE:
                    return "relative";
            }
            return "";
        }

        @Override
        public void doSet(Object target, String val) {

            switch (val) {
                case "pixels":
                    ((ENG_OverlayElement) target).setMetricsMode(GuiMetricsMode.GMM_PIXELS);
                    break;
                case "relative_aspect_adjusted":
                    ((ENG_OverlayElement) target).setMetricsMode(
                            GuiMetricsMode.GMM_RELATIVE_ASPECT_ADJUSTED);
                    break;
                default:
                    ((ENG_OverlayElement) target).setMetricsMode(
                            GuiMetricsMode.GMM_RELATIVE);
                    break;
            }
        }

    }

    public static class CmdHorizontalAlign implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            GuiHorizontalAlignment alignment =
                    ((ENG_OverlayElement) target).getHorizontalAlignment();
            switch (alignment) {
                case GHA_CENTER:
                    return "center";
                case GHA_LEFT:
                    return "left";
                case GHA_RIGHT:
                    return "right";
            }
            return "center";
        }

        @Override
        public void doSet(Object target, String val) {

            switch (val) {
                case "left":
                    ((ENG_OverlayElement) target).setHorizontalAlignment(
                            GuiHorizontalAlignment.GHA_LEFT);
                    break;
                case "right":
                    ((ENG_OverlayElement) target).setHorizontalAlignment(
                            GuiHorizontalAlignment.GHA_RIGHT);
                    break;
                default:
                    ((ENG_OverlayElement) target).setHorizontalAlignment(
                            GuiHorizontalAlignment.GHA_CENTER);
                    break;
            }
        }

    }

    public static class CmdVerticalAlign implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            GuiVerticalAlignment alignment =
                    ((ENG_OverlayElement) target).getVerticalAlignment();
            switch (alignment) {
                case GVA_TOP:
                    return "top";
                case GVA_CENTER:
                    return "center";
                case GVA_BOTTOM:
                    return "bottom";
            }
            return "center";
        }

        @Override
        public void doSet(Object target, String val) {

            switch (val) {
                case "top":
                    ((ENG_OverlayElement) target).setVerticalAlignment(
                            GuiVerticalAlignment.GVA_TOP);
                    break;
                case "bottom":
                    ((ENG_OverlayElement) target).setVerticalAlignment(
                            GuiVerticalAlignment.GVA_BOTTOM);
                    break;
                default:
                    ((ENG_OverlayElement) target).setVerticalAlignment(
                            GuiVerticalAlignment.GVA_CENTER);
                    break;
            }
        }

    }

    public static class CmdVisible implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            boolean b = ((ENG_OverlayElement) target).isVisible();
            if (b) {
                return "true";
            } else {
                return "false";
            }

        }

        @Override
        public void doSet(Object target, String val) {

            if (val.equals("true")) {
                ((ENG_OverlayElement) target).show();
            } else if (val.equals("false")) {
                ((ENG_OverlayElement) target).hide();
            }
        }

    }

    // Command object for setting / getting parameters
    protected static final CmdLeft msLeftCmd = new CmdLeft();
    protected static final CmdTop msTopCmd = new CmdTop();
    protected static final CmdWidth msWidthCmd = new CmdWidth();
    protected static final CmdHeight msHeightCmd = new CmdHeight();
    protected static final CmdMaterial msMaterialCmd = new CmdMaterial();
    protected static final CmdCaption msCaptionCmd = new CmdCaption();
    protected static final CmdMetricsMode msMetricsModeCmd = new CmdMetricsMode();
    protected static final CmdHorizontalAlign msHorizontalAlignCmd = new CmdHorizontalAlign();
    protected static final CmdVerticalAlign msVerticalAlignCmd = new CmdVerticalAlign();
    protected static final CmdVisible msVisibleCmd = new CmdVisible();

    protected final String mName;
    protected boolean mVisible = true;
    protected boolean mCloneable = true;
    protected float mLeft;
    protected float mTop;
    protected float mWidth = 1.0f;
    protected float mHeight = 1.0f;
    protected String mMaterialName;
    protected ENG_Material mpMaterial;
    protected String mCaption = "";
    protected final ENG_ColorValue mColour = new ENG_ColorValue();
    protected ENG_RealRect mClippingRegion = new ENG_RealRect();

    protected GuiMetricsMode mMetricsMode = GuiMetricsMode.GMM_RELATIVE;
    protected GuiHorizontalAlignment mHorzAlign = GuiHorizontalAlignment.GHA_LEFT;
    protected GuiVerticalAlignment mVertAlign = GuiVerticalAlignment.GVA_TOP;

    // metric-mode positions, used in GMM_PIXELS & GMM_RELATIVE_ASPECT_ADJUSTED mode.
    protected float mPixelTop;
    protected float mPixelLeft;
    protected float mPixelWidth = 1.0f;
    protected float mPixelHeight = 1.0f;
    protected float mPixelScaleX = 1.0f;
    protected float mPixelScaleY = 1.0f;

    private final ENG_HlmsUnlitDatablock datablock = new ENG_HlmsUnlitDatablock();
    private final long[] ptr = new long[1];
    private long id;
    private String name;
    private boolean nativePtrSet;

    // Parent pointer
    protected ENG_OverlayContainer mParent;
    // Overlay attached to
    protected ENG_Overlay mOverlay;

    // Derived positions from parent
    protected float mDerivedLeft;
    protected float mDerivedTop;
    protected boolean mDerivedOutOfDate = true;

    /// Flag indicating if the vertex positions need recalculating
    protected boolean mGeomPositionsOutOfDate = true;
    /// Flag indicating if the vertex uvs need recalculating
    protected boolean mGeomUVsOutOfDate = true;

    // Zorder for when sending to render queue
    // Derived from parent
    protected short mZOrder;

    // world transforms
    protected final ENG_Matrix4 mXForm = new ENG_Matrix4();

    // is element enabled
    protected boolean mEnabled = true;

    // is element initialised
    protected boolean mInitialised;

    // Used to see if this element is created from a Template
    protected ENG_OverlayElement mSourceTemplate;

    protected abstract void updatePositionGeometry();

    protected abstract void updateTextureGeometry();

    protected void addBaseParameters() {
        ENG_ParamDictionary dict = getStringInterface().getParamDictionary();

        dict.addParameter(new ENG_ParameterDef("left",
                        "The position of the left border of the gui element."
                        , ParameterType.PT_REAL),
                msLeftCmd);
        dict.addParameter(new ENG_ParameterDef("top",
                        "The position of the top border of the gui element."
                        , ParameterType.PT_REAL),
                msTopCmd);
        dict.addParameter(new ENG_ParameterDef("width",
                        "The width of the element."
                        , ParameterType.PT_REAL),
                msWidthCmd);
        dict.addParameter(new ENG_ParameterDef("height",
                        "The height of the element."
                        , ParameterType.PT_REAL),
                msHeightCmd);
        dict.addParameter(new ENG_ParameterDef("material",
                        "The name of the material to use."
                        , ParameterType.PT_STRING),
                msMaterialCmd);
        dict.addParameter(new ENG_ParameterDef("caption",
                        "The element caption, if supported."
                        , ParameterType.PT_STRING),
                msCaptionCmd);
        dict.addParameter(new ENG_ParameterDef("metrics_mode",
                        "The type of metrics to use, either 'relative' to the screen, 'pixels' or 'relative_aspect_adjusted'."
                        , ParameterType.PT_STRING),
                msMetricsModeCmd);
        dict.addParameter(new ENG_ParameterDef("horz_align",
                        "The horizontal alignment, 'left', 'right' or 'center'."
                        , ParameterType.PT_STRING),
                msHorizontalAlignCmd);
        dict.addParameter(new ENG_ParameterDef("vert_align",
                        "The vertical alignment, 'top', 'bottom' or 'center'."
                        , ParameterType.PT_STRING),
                msVerticalAlignCmd);
        dict.addParameter(new ENG_ParameterDef("visible",
                        "Initial visibility of element, either 'true' or 'false' (default true)."
                        , ParameterType.PT_STRING),
                msVisibleCmd);
    }

    public ENG_OverlayElement(String name) {
        
        mName = name;
        // default overlays to preserve their own detail level
        mPolygonModeOverrideable = false;

        // use identity projection and view matrices
        mUseIdentityProjection = true;
        mUseIdentityView = true;
    }

    private final ENG_StringInterface stringInterface = new ENG_StringInterface(this);

    @Override
    public ENG_StringInterface getStringInterface() {

        return stringInterface;
    }

    public abstract void initialise();

    public abstract void destroy(boolean skipGLCall);

    public String getName() {
        return mName;
    }

    public void show() {
        if (mVisible) {
            return;
        }
        mVisible = true;
        ENG_NativeCalls.overlayElement_show(this);
    }

    public void hide() {
        if (!mVisible) {
            return;
        }
        mVisible = false;
        ENG_NativeCalls.overlayElement_hide(this);
    }

    public boolean isVisible() {
        return mVisible;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean b) {
        mEnabled = b;
    }

    public void setDimensions(float width, float height) {
        if (mMetricsMode != GuiMetricsMode.GMM_RELATIVE) {
            mPixelWidth = width;
            mPixelHeight = height;
        } else {
            mWidth = width;
            mHeight = height;
        }
//        mDerivedOutOfDate = true;
        _positionsOutOfDate();
    }

    public void setPosition(float left, float top) {
        if (mMetricsMode != GuiMetricsMode.GMM_RELATIVE) {
            mPixelLeft = left;
            mPixelTop = top;
        } else {
            mLeft = left;
            mTop = top;
        }
//        mDerivedOutOfDate = true;
        _positionsOutOfDate();
    }

    public void setWidth(float width) {
        if (mMetricsMode != GuiMetricsMode.GMM_RELATIVE) {
            mPixelWidth = width;
        } else {
            mWidth = width;
        }
//        mDerivedOutOfDate = true;
        _positionsOutOfDate();
        ENG_NativeCalls.overlayElement_setWidth(this, width);
    }

    public float getWidth() {
        if (mMetricsMode != GuiMetricsMode.GMM_RELATIVE) {
            return mPixelWidth;
        } else {
            return mWidth;
        }
    }

    public void setHeight(float height) {
        if (mMetricsMode != GuiMetricsMode.GMM_RELATIVE) {
            mPixelHeight = height;
        } else {
            mHeight = height;
        }
//        mDerivedOutOfDate = true;
        _positionsOutOfDate();
        ENG_NativeCalls.overlayElement_setHeight(this, height);
    }

    public float getHeight() {
        if (mMetricsMode != GuiMetricsMode.GMM_RELATIVE) {
            return mPixelHeight;
        } else {
            return mHeight;
        }
    }

    public void setLeft(float left) {
        if (mMetricsMode != GuiMetricsMode.GMM_RELATIVE) {
            mPixelLeft = left;
        } else {
            mLeft = left;
        }
//        mDerivedOutOfDate = true;
        _positionsOutOfDate();
        ENG_NativeCalls.overlayElement_setLeft(this, left);
    }

    public float getLeft() {
        if (mMetricsMode != GuiMetricsMode.GMM_RELATIVE) {
            return mPixelLeft;
        } else {
            return mLeft;
        }
    }

    public void setTop(float top) {
        if (mMetricsMode != GuiMetricsMode.GMM_RELATIVE) {
            mPixelTop = top;
        } else {
            mTop = top;
        }
//        mDerivedOutOfDate = true;
        _positionsOutOfDate();
        ENG_NativeCalls.overlayElement_setTop(this, top);
    }

    /**
     * Gets the left of this element in relation to the screen (where 0 = far left, 1.0 = far right)
     */
    public float _getLeft() {
        return mLeft;
    }

    /**
     * Gets the top of this element in relation to the screen (where 0 = far left, 1.0 = far right)
     */
    public float _getTop() {
        return mTop;
    }

    /**
     * Gets the width of this element in relation to the screen (where 1.0 = screen width)
     */
    public float _getWidth() {
        return mWidth;
    }

    /**
     * Gets the height of this element in relation to the screen (where 1.0 = screen height)
     */
    public float _getHeight() {
        return mHeight;
    }

    public void _setLeft(float left) {
        mLeft = left;
        mPixelLeft = left / mPixelScaleX;

//        mDerivedOutOfDate = true;
        _positionsOutOfDate();
    }

    public void _setTop(float top) {
        mTop = top;
        mPixelTop = top / mPixelScaleY;

//        mDerivedOutOfDate = true;
        _positionsOutOfDate();
    }

    public void _setWidth(float width) {
        mWidth = width;
        mPixelWidth = width / mPixelScaleX;

//        mDerivedOutOfDate = true;
        _positionsOutOfDate();
    }

    public void _setHeight(float height) {
        mHeight = height;
        mPixelHeight = height / mPixelScaleY;

//        mDerivedOutOfDate = true;
        _positionsOutOfDate();
    }

    public void _setPosition(float left, float top) {
        mLeft = left;
        mTop = top;
        mPixelLeft = left / mPixelScaleX;
        mPixelTop = top / mPixelScaleY;

//        mDerivedOutOfDate = true;
        _positionsOutOfDate();
    }

    public void _setDimensions(float width, float height) {
        mWidth = width;
        mHeight = height;
        mPixelWidth = width / mPixelScaleX;
        mPixelHeight = height / mPixelScaleY;

//        mDerivedOutOfDate = true;
        _positionsOutOfDate();
    }

    public String getMaterialName() {
        return mMaterialName;
    }

    public void setMaterialName(String name) {
        setMaterialName(name, true);
    }

    public void setMaterialName(String name, boolean callNative) {
        mMaterialName = name;
        if (callNative) {
            ENG_NativeCalls.overlayElement_setMaterialName(this, name);
        }
//        if (!name.isEmpty()) {
//            mpMaterial = ENG_MaterialManager.getSingleton().getByName(name);
//            if (mpMaterial == null) {
//                throw new IllegalArgumentException(name + " not valid material name");
//            }
//            mpMaterial.load();
//            // Set some prerequisites to be sure
//            mpMaterial.setLightingEnabled(false);
//            mpMaterial.setDepthCheckEnabled(false);
//        } else {
//            mpMaterial = null;
//        }
    }

    public void _positionsOutOfDate() {

        mGeomPositionsOutOfDate = true;
    }

    public float getTop() {
        if (mMetricsMode != GuiMetricsMode.GMM_RELATIVE) {
            return mPixelTop;
        } else {
            return mTop;
        }
    }

    @Override
    public ENG_Material getMaterial() {

        return mpMaterial;
    }

    public void _update() {
        // Check size if pixel-based
        switch (mMetricsMode) {
            case GMM_PIXELS:
                if (ENG_OverlayManager.getSingleton().hasViewportChanged() ||
                        mGeomPositionsOutOfDate) {
                    float vpWidth, vpHeight;
                    ENG_OverlayManager oMgr = ENG_OverlayManager.getSingleton();
                    vpWidth = oMgr.getViewportWidth();
                    vpHeight = oMgr.getViewportHeight();

                    mPixelScaleX = 1.0f / vpWidth;
                    mPixelScaleY = 1.0f / vpHeight;

                    mLeft = mPixelLeft * mPixelScaleX;
                    mTop = mPixelTop * mPixelScaleY;
                    mWidth = mPixelWidth * mPixelScaleX;
                    mHeight = mPixelHeight * mPixelScaleY;
                }
                break;

            case GMM_RELATIVE_ASPECT_ADJUSTED:
                if (ENG_OverlayManager.getSingleton().hasViewportChanged() || mGeomPositionsOutOfDate) {
                    float vpWidth, vpHeight;
                    ENG_OverlayManager oMgr = ENG_OverlayManager.getSingleton();
                    vpWidth = oMgr.getViewportWidth();
                    vpHeight = oMgr.getViewportHeight();

                    mPixelScaleX = 1.0f / (10000.0f * (vpWidth / vpHeight));
                    mPixelScaleY = 1.0f / 10000.0f;

                    mLeft = mPixelLeft * mPixelScaleX;
                    mTop = mPixelTop * mPixelScaleY;
                    mWidth = mPixelWidth * mPixelScaleX;
                    mHeight = mPixelHeight * mPixelScaleY;
                }
                break;
            default:
                break;
        }

        _updateFromParent();
        // NB container subclasses will update children too

        // Tell self to update own position geometry
        if (mGeomPositionsOutOfDate && mInitialised) {
            updatePositionGeometry();
            mGeomPositionsOutOfDate = false;
        }
        // Tell self to update own texture geometry
        if (mGeomUVsOutOfDate && mInitialised) {
            updateTextureGeometry();
            mGeomUVsOutOfDate = false;
        }
    }

    public void _updateFromParent() {
        float parentLeft, parentTop, parentBottom = 0, parentRight = 0;

        if (mParent != null) {
            parentLeft = mParent._getDerivedLeft();
            parentTop = mParent._getDerivedTop();
            if (mHorzAlign == GuiHorizontalAlignment.GHA_CENTER ||
                    mHorzAlign == GuiHorizontalAlignment.GHA_RIGHT) {
                parentRight = parentLeft + mParent._getRelativeWidth();
            }
            if (mVertAlign == GuiVerticalAlignment.GVA_CENTER ||
                    mVertAlign == GuiVerticalAlignment.GVA_BOTTOM) {
                parentBottom = parentTop + mParent._getRelativeHeight();
            }
        } else {
            ENG_RenderSystem rSys = ENG_RenderRoot.getRenderRoot().getRenderSystem();
            ENG_OverlayManager oMgr = ENG_OverlayManager.getSingleton();

            // Everything is on native side so we ignore these.
            float hOffset = 0.0f;//rSys.getHorizontalTexelOffset() / oMgr.getViewportWidth();
            float vOffset = 0.0f;//rSys.getVerticalTexelOffset() / oMgr.getViewportHeight();

            parentLeft = 0.0f + hOffset;
            parentTop = 0.0f + vOffset;
            parentRight = 1.0f + hOffset;
            parentBottom = 1.0f + vOffset;
        }

        switch (mHorzAlign) {
            case GHA_CENTER:
                mDerivedLeft = ((parentLeft + parentRight) * 0.5f) + mLeft;
                break;
            case GHA_LEFT:
                mDerivedLeft = parentLeft + mLeft;
                break;
            case GHA_RIGHT:
                mDerivedLeft = parentRight + mLeft;
                break;
        }
        switch (mVertAlign) {
            case GVA_CENTER:
                mDerivedTop = ((parentTop + parentBottom) * 0.5f) + mTop;
                break;
            case GVA_TOP:
                mDerivedTop = parentTop + mTop;
                break;
            case GVA_BOTTOM:
                mDerivedTop = parentBottom + mTop;
                break;
        }

        mDerivedOutOfDate = false;

        if (mParent != null) {
            ENG_RealRect parent = new ENG_RealRect();
            ENG_RealRect child = new ENG_RealRect();

            mParent._getClippingRegion(parent);

            child.left = mDerivedLeft;
            child.top = mDerivedTop;
            child.right = mDerivedLeft + mWidth;
            child.bottom = mDerivedTop + mHeight;

            mClippingRegion = parent.intersect(child);
        } else {
            mClippingRegion.left = mDerivedLeft;
            mClippingRegion.top = mDerivedTop;
            mClippingRegion.right = mDerivedLeft + mWidth;
            mClippingRegion.bottom = mDerivedTop + mHeight;
        }
    }

    public void _notifyParent(ENG_OverlayContainer parent, ENG_Overlay overlay) {
        mParent = parent;
        mOverlay = overlay;

        if (mOverlay != null && mOverlay.isInitialised() && !mInitialised) {
            initialise();
        }

//        mDerivedOutOfDate = true;
    }

    public float _getDerivedLeft() {
        if (mDerivedOutOfDate) {
            _updateFromParent();
        }
        return mDerivedLeft;
    }

    public float _getDerivedTop() {
        if (mDerivedOutOfDate) {
            _updateFromParent();
        }
        return mDerivedTop;
    }

    public float _getRelativeWidth() {
        return mWidth;
    }

    public float _getRelativeHeight() {
        return mHeight;
    }

    public void _getClippingRegion(ENG_RealRect ret) {
        if (mDerivedOutOfDate) {
            _updateFromParent();
        }
        ret.set(mClippingRegion);
    }

    /**
     * Used for native to get the data from native to java.
     * @param rect
     */
    public void _setClippingRegion(ENG_RealRect rect) {
        mClippingRegion.set(rect);
    }

    public short _notifyZOrder(short zorder) {
        mZOrder = zorder;
        return (short) (zorder + 1);
    }

    public void _notifyWorldTransforms(ENG_Matrix4 xform) {
        mXForm.set(xform);
    }

    public void _notifyViewport() {
        switch (mMetricsMode) {
            case GMM_PIXELS: {
                float vpWidth, vpHeight;
                ENG_OverlayManager oMgr = ENG_OverlayManager.getSingleton();
                vpWidth = oMgr.getViewportWidth();
                vpHeight = oMgr.getViewportHeight();

                mPixelScaleX = 1.0f / vpWidth;
                mPixelScaleY = 1.0f / vpHeight;
            }
            break;

            case GMM_RELATIVE_ASPECT_ADJUSTED: {
                float vpWidth, vpHeight;
                ENG_OverlayManager oMgr = ENG_OverlayManager.getSingleton();
                vpWidth = oMgr.getViewportWidth();
                vpHeight = oMgr.getViewportHeight();

                mPixelScaleX = 1.0f / (10000.0f * (vpWidth / vpHeight));
                mPixelScaleY = 1.0f / 10000.0f;
            }
            break;

            case GMM_RELATIVE:
                mPixelScaleX = 1.0f;
                mPixelScaleY = 1.0f;
                mPixelLeft = mLeft;
                mPixelTop = mTop;
                mPixelWidth = mWidth;
                mPixelHeight = mHeight;
                break;
        }

        mLeft = mPixelLeft * mPixelScaleX;
        mTop = mPixelTop * mPixelScaleY;
        mWidth = mPixelWidth * mPixelScaleX;
        mHeight = mPixelHeight * mPixelScaleY;

        mGeomPositionsOutOfDate = true;
    }

    public void _updateRenderQueue(ENG_RenderQueue queue) {
        if (mVisible) {
            queue.addRenderable(this, RenderQueueGroupID.RENDER_QUEUE_OVERLAY.getID(),
                    mZOrder);
        }
    }

    public void visitRenderables(Visitor visitor) {
        visitRenderables(visitor, false);
    }

    public void visitRenderables(Visitor visitor, boolean debugRenderables) {
        visitor.visit(this, (short) 0, false);
    }

    public abstract String getTypeName();

    public void setCaption(String caption) {
        mCaption = caption;
        _positionsOutOfDate();
        ENG_NativeCalls.overlayElement_setCaption(this, caption);
    }

    public String getCaption() {
        return mCaption;
    }

    public void setColour(ENG_ColorValue col) {
        mColour.set(col);
    }

    public void getColour(ENG_ColorValue ret) {
        ret.set(mColour);
    }

    public ENG_ColorValue getColour() {
        return new ENG_ColorValue(mColour);
    }

    public void setMetricsMode(GuiMetricsMode gmm) {
//        switch (gmm) {
//            case GMM_PIXELS: {
//                float vpWidth, vpHeight;
//                ENG_OverlayManager oMgr = ENG_OverlayManager.getSingleton();
//                vpWidth = oMgr.getViewportWidth();
//                vpHeight = oMgr.getViewportHeight();
//
//                // cope with temporarily zero dimensions, avoid divide by zero
//                vpWidth = vpWidth == 0.0f ? 1.0f : vpWidth;
//                vpHeight = vpHeight == 0.0f ? 1.0f : vpHeight;
//
//                mPixelScaleX = 1.0f / vpWidth;
//                mPixelScaleY = 1.0f / vpHeight;
//
//                if (mMetricsMode == GuiMetricsMode.GMM_RELATIVE) {
//                    mPixelLeft = mLeft;
//                    mPixelTop = mTop;
//                    mPixelWidth = mWidth;
//                    mPixelHeight = mHeight;
//                }
//            }
//            break;
//
//            case GMM_RELATIVE_ASPECT_ADJUSTED: {
//                float vpWidth, vpHeight;
//                ENG_OverlayManager oMgr = ENG_OverlayManager.getSingleton();
//                vpWidth = oMgr.getViewportWidth();
//                vpHeight = oMgr.getViewportHeight();
//
//                mPixelScaleX = 1.0f / (10000.0f * (vpWidth / vpHeight));
//                mPixelScaleY = 1.0f / 10000.0f;
//
//                if (mMetricsMode == GuiMetricsMode.GMM_RELATIVE) {
//                    mPixelLeft = mLeft;
//                    mPixelTop = mTop;
//                    mPixelWidth = mWidth;
//                    mPixelHeight = mHeight;
//                }
//            }
//            break;
//
//            case GMM_RELATIVE:
//                mPixelScaleX = 1.0f;
//                mPixelScaleY = 1.0f;
//                mPixelLeft = mLeft;
//                mPixelTop = mTop;
//                mPixelWidth = mWidth;
//                mPixelHeight = mHeight;
//                break;
//        }
//
//        mLeft = mPixelLeft * mPixelScaleX;
//        mTop = mPixelTop * mPixelScaleY;
//        mWidth = mPixelWidth * mPixelScaleX;
//        mHeight = mPixelHeight * mPixelScaleY;
//
//        mMetricsMode = gmm;
//        mDerivedOutOfDate = true;
//        _positionsOutOfDate();
        mMetricsMode = gmm;
        ENG_NativeCalls.overlayElement_setMetricsMode(this, gmm);
    }

    public GuiMetricsMode getMetricsMode() {
        return mMetricsMode;
    }

    public void setHorizontalAlignment(GuiHorizontalAlignment gha) {
        mHorzAlign = gha;
        _positionsOutOfDate();
    }

    public GuiHorizontalAlignment getHorizontalAlignment() {
        return mHorzAlign;
    }

    public void setVerticalAlignment(GuiVerticalAlignment gva) {
        mVertAlign = gva;
        _positionsOutOfDate();
    }

    public GuiVerticalAlignment getVerticalAlignment() {
        return mVertAlign;
    }

    public boolean contains(float x, float y) {
        return mClippingRegion.inside(x, y);
    }

    public ENG_OverlayElement findElementAt(float x, float y) {
        if (contains(x, y)) {
            return this;
        }
        return null;
    }

    /**
     * returns false as this class is not a container type
     */
    public boolean isContainer() {
        return false;
    }

    public boolean isKeyEnabled() {
        return false;
    }

    public boolean isCloneable() {
        return mCloneable;
    }

    public void setCloneable(boolean c) {
        mCloneable = c;
    }

    public ENG_OverlayContainer getParent() {
        return mParent;
    }

    public void _setParent(ENG_OverlayContainer c) {
        mParent = c;
    }

    public short getZOrder() {
        return mZOrder;
    }


    @Override
    public void getWorldTransforms(ENG_Matrix4[] xform) {


        mOverlay._getWorldTransforms(xform[0]);
    }

    @Override
    public float getSquaredViewDepth(ENG_Camera cam) {

        return 10000.0f - (float) getZOrder();
    }

    /** @noinspection deprecation*/
    @Override
    public ArrayList<ENG_Light> getLights() {

        return null;
    }

    public void copyFromTemplate(ENG_OverlayElement templateOverlay) {
        templateOverlay.getStringInterface().copyParametersTo(
                this.getStringInterface());
        mSourceTemplate = templateOverlay;
    }

    public ENG_OverlayElement clone(String instanceName) {
        ENG_OverlayElement newElement = ENG_OverlayManager.getSingleton()
                .createOverlayElement(getTypeName(), instanceName + "/" + mName);
        getStringInterface().copyParametersTo(newElement.getStringInterface());
        return newElement;
    }

    public ENG_OverlayElement getSourceTemplate() {
        return mSourceTemplate;
    }

    @Override
    public long getPointer() {
        return ptr[0];
    }

    @Override
    public void setPointer(long ptr) {
        this.ptr[0] = ptr;
    }

    @Override
    public boolean isNativePointerSet() {
        return nativePtrSet;
    }

    @Override
    public void setNativePointer(boolean set) {
        nativePtrSet = set;
    }

    public ENG_HlmsUnlitDatablock getDatablock() {
        return datablock;
    }
}
