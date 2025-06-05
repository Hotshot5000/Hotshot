/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.ENG_Common.CompareFunction;
import headwayent.hotshotengine.renderer.ENG_Common.FrameBufferType;
import headwayent.hotshotengine.renderer.ENG_RenderQueue.RenderQueueGroupID;
import headwayent.hotshotengine.renderer.ENG_RenderSystem.StencilOperation;

public class ENG_CompositionPass {

    /**
     * Enumeration that enumerates the various composition pass types.
     */
    public enum PassType {
        PT_CLEAR,           // Clear target to one colour
        PT_STENCIL,            // Set stencil operation
        PT_RENDERSCENE,     // Render the scene or part of it
        PT_RENDERQUAD,      // Render a full screen quad
        PT_RENDERCUSTOM        // Render a custom sequence
    }

    public static class InputTex {
        /// Name (local) of the input texture (empty == no input)
        public String name;
        /// MRT surface index if applicable
        public int mrtIndex;

        public InputTex() {
            name = "";
        }

        public InputTex(String name, int mrtIndex) {
            set(name, mrtIndex);
        }

        public void set(String name, int mrtIndex) {
            this.name = name;
            this.mrtIndex = mrtIndex;
        }
    }

    /// Parent technique
    private final ENG_CompositionTargetPass mParent;
    /// Type of composition pass
    private PassType mType = PassType.PT_RENDERQUAD;
    /// Identifier for this pass
    private int mIdentifier;
    /// Material used for rendering
    private ENG_Material mMaterial;
    /// [first,last] render queue to render this pass (in case of PT_RENDERSCENE)
    private byte mFirstRenderQueue =
            RenderQueueGroupID.RENDER_QUEUE_BACKGROUND.getID();
    private byte mLastRenderQueue =
            RenderQueueGroupID.RENDER_QUEUE_SKIES_LATE.getID();
    /// Material scheme name
    private String mMaterialScheme = "";
    /// Clear buffers (in case of PT_CLEAR)
    private int mClearBuffers =
            FrameBufferType.FBT_COLOUR.getType() |
                    FrameBufferType.FBT_DEPTH.getType();
    /// Clear colour (in case of PT_CLEAR)
    private final ENG_ColorValue mClearColour = new ENG_ColorValue(ENG_ColorValue.BLACK);
    /// Clear depth (in case of PT_CLEAR)
    private float mClearDepth = 1.0f;
    /// Clear stencil value (in case of PT_CLEAR)
    private int mClearStencil;
    /// Inputs (for material used for rendering the quad)
    /// An empty string signifies that no input is used
    private final InputTex[] mInputs = new InputTex[ENG_Config.MAX_TEXTURE_LAYERS];
    /// Stencil operation parameters
    private boolean mStencilCheck;
    private CompareFunction mStencilFunc = CompareFunction.CMPF_ALWAYS_PASS;
    private int mStencilRefValue;
    private int mStencilMask = 0xFFFFFFFF;
    private StencilOperation mStencilFailOp = StencilOperation.SOP_KEEP;
    private StencilOperation mStencilDepthFailOp = StencilOperation.SOP_KEEP;
    private StencilOperation mStencilPassOp = StencilOperation.SOP_KEEP;
    private boolean mStencilTwoSidedOperation;

    /// true if quad should not cover whole screen
    private boolean mQuadCornerModified;
    /// quad positions in normalised coordinates [-1;1]x[-1;1] (in case of PT_RENDERQUAD)
    private float mQuadLeft = -1.0f;
    private float mQuadTop = 1.0f;
    private float mQuadRight = 1.0f;
    private float mQuadBottom = -1.0f;

    private boolean mQuadFarCorners, mQuadFarCornersViewSpace;
    //The type name of the custom composition pass.
    private String mCustomType;

    public ENG_CompositionPass(
            ENG_CompositionTargetPass parent) {

        mParent = parent;
        for (int i = 0; i < mInputs.length; ++i) {
            mInputs[i] = new InputTex();
        }
    }

    public void setType(PassType type) {
        mType = type;
    }

    public PassType getType() {
        return mType;
    }

    public void setIdentifier(int id) {
        mIdentifier = id;
    }

    public int getIdentifier() {
        return mIdentifier;
    }

    public void setMaterial(ENG_Material mat) {
        mMaterial = mat;
    }

    public void setMaterial(String name) {
        mMaterial = ENG_MaterialManager.getSingleton().getByName(name);
    }

    public ENG_Material getMaterial() {
        return mMaterial;
    }

    public void setClearBuffers(int clear) {
        mClearBuffers = clear;
    }

    public int getClearBuffers() {
        return mClearBuffers;
    }

    public void setClearColour(ENG_ColorValue color) {
        mClearColour.set(color);
    }

    public ENG_ColorValue getClearColour() {
        return new ENG_ColorValue(mClearColour);
    }

    public void getClearColour(ENG_ColorValue ret) {
        ret.set(mClearColour);
    }

    public void setInput(int id, String input, int mrtIndex) {
        if (id < 0 || id >= mInputs.length) {
            throw new IllegalArgumentException("id out of range: " + id +
                    ". Maximum is " + mInputs.length);
        }
        mInputs[id].set(input, mrtIndex);
    }

    public InputTex getInput(int id) {
        if (id < 0 || id >= mInputs.length) {
            throw new IllegalArgumentException("id out of range: " + id +
                    ". Maximum is " + mInputs.length);
        }
        return mInputs[id];
    }

    public int getNumInputs() {
        int count = 0;
        for (int i = 0; i < mInputs.length; ++i) {
            if (!mInputs[i].name.isEmpty()) {
                count = i + 1;
            }
        }
        return count;
    }

    public void clearAllInputs() {
        for (InputTex mInput : mInputs) {
            mInput.name = "";
        }
    }

    public ENG_CompositionTargetPass getParent() {
        return mParent;
    }

    public void setFirstRenderQueue(byte id) {
        mFirstRenderQueue = id;
    }

    public byte getFirstRenderQueue() {
        return mFirstRenderQueue;
    }

    public void setLastRenderQueue(byte id) {
        mLastRenderQueue = id;
    }

    public byte getLastRenderQueue() {
        return mLastRenderQueue;
    }

    public void setMaterialScheme(String scheme) {
        mMaterialScheme = scheme;
    }

    public String getMaterialScheme() {
        return mMaterialScheme;
    }

    public void setClearDepth(float depth) {
        mClearDepth = depth;
    }

    public float getClearDepth() {
        return mClearDepth;
    }

    public void setClearStencil(int value) {
        mClearStencil = value;
    }

    public int getClearStencil() {
        return mClearStencil;
    }

    public void setStencilCheck(boolean b) {
        mStencilCheck = b;
    }

    public boolean getStencilCheck() {
        return mStencilCheck;
    }

    public void setStencilFunc(CompareFunction comp) {
        mStencilFunc = comp;
    }

    public CompareFunction getStencilFunc() {
        return mStencilFunc;
    }

    public void setStencilRefValue(int v) {
        mStencilRefValue = v;
    }

    public int getStencilRefValue() {
        return mStencilRefValue;
    }

    public void setStencilMask(int mask) {
        mStencilMask = mask;
    }

    public int getStencilMask() {
        return mStencilMask;
    }

    public void setStencilFailOp(StencilOperation op) {
        mStencilFailOp = op;
    }

    public StencilOperation getStencilFailOp() {
        return mStencilFailOp;
    }

    public void setStencilDepthFailOp(StencilOperation op) {
        mStencilDepthFailOp = op;
    }

    public StencilOperation getStencilDepthFailOp() {
        return mStencilDepthFailOp;
    }

    public void setStencilPassOp(StencilOperation op) {
        mStencilPassOp = op;
    }

    public StencilOperation getStencilPassOp() {
        return mStencilPassOp;
    }

    public void setStencilTwoSidedOperation(boolean v) {
        mStencilTwoSidedOperation = v;
    }

    public boolean getStencilTwoSidedOperation() {
        return mStencilTwoSidedOperation;
    }

    public void setQuadCorners(float left, float top, float right, float bottom) {
        mQuadCornerModified = true;
        mQuadLeft = left;
        mQuadTop = top;
        mQuadRight = right;
        mQuadBottom = bottom;
    }

    /**
     * @param ret 0 - left, 1 - top, 2 - right, 3 - bottom
     * @return
     */
    public boolean getQuadCorners(float[] ret) {
        if (ret.length < 4) {
            throw new IllegalArgumentException("ret.length must be at least 4!");
        }
        ret[0] = mQuadLeft;
        ret[1] = mQuadTop;
        ret[2] = mQuadRight;
        ret[3] = mQuadBottom;
        return mQuadCornerModified;
    }

    public void setQuadFarCorners(boolean farCorners, boolean farCornersViewSpace) {
        mQuadFarCorners = farCorners;
        mQuadFarCornersViewSpace = farCornersViewSpace;
    }

    public boolean getQuadFarCorners() {
        return mQuadFarCorners;
    }

    public boolean getQuadFarCornersViewSpace() {
        return mQuadFarCornersViewSpace;
    }

    public void setCustomType(String type) {
        mCustomType = type;
    }

    public String getCustomType() {
        return mCustomType;
    }

    public boolean _isSupported() {
        
        if (mType == PassType.PT_RENDERQUAD) {
            if (mMaterial == null) {
                return false;
            }

            mMaterial.compile();
            if (mMaterial.getNumSupportedTechniques() == 0) {
                return false;
            }
        }

        return true;
    }

}
