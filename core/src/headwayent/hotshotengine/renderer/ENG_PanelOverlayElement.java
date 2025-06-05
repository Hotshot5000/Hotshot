/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:10 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_ParamCommand;
import headwayent.hotshotengine.ENG_ParamDictionary;
import headwayent.hotshotengine.ENG_ParameterDef;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.ENG_ParameterDef.ParameterType;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.LockOptions;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.Usage;
import headwayent.hotshotengine.renderer.ENG_RenderOperation.OperationType;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementType;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class ENG_PanelOverlayElement extends ENG_OverlayContainer {

    public static class CmdTiling implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {
            
            return "0 " + ((ENG_PanelOverlayElement) target).getTileX()
                    + " " +
                    ((ENG_PanelOverlayElement) target).getTileY();
        }

        @Override
        public void doSet(Object target, String val) {
            
            String[] split = val.split(" ");
            int layer = Integer.parseInt(split[0]);
            float x = Float.parseFloat(split[1]);
            float y = Float.parseFloat(split[2]);

            ((ENG_PanelOverlayElement) target).setTiling(x, y, (short) layer);
        }

    }

    public static class CmdTransparent implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {
            
            return String.valueOf(((ENG_PanelOverlayElement) target).isTransparent());
        }

        @Override
        public void doSet(Object target, String val) {
            
            ((ENG_PanelOverlayElement) target).setTransparent(Boolean.parseBoolean(val));
        }

    }

    public static class CmdUVCoords implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {
            
            float[] ret = new float[4];
            ((ENG_PanelOverlayElement) target).getUV(ret);
            return " " + ret[0] + " " + ret[1] + " " + ret[2] + " " + ret[3];
        }

        @Override
        public void doSet(Object target, String val) {
            
            String[] split = val.split(" ");
            ((ENG_PanelOverlayElement) target).setUV(Float.parseFloat(split[0]),
                    Float.parseFloat(split[1]),
                    Float.parseFloat(split[2]),
                    Float.parseFloat(split[3]));
        }

    }

    // Command objects
    protected static final CmdTiling msCmdTiling = new CmdTiling();
    protected static final CmdTransparent msCmdTransparent = new CmdTransparent();
    protected static final CmdUVCoords msCmdUVCoords = new CmdUVCoords();

    // Flag indicating if this panel should be visual or just group things
    protected boolean mTransparent;
    // Texture tiling
    protected final float[] mTileX = new float[ENG_Config.MAX_TEXTURE_LAYERS];
    protected final float[] mTileY = new float[ENG_Config.MAX_TEXTURE_LAYERS];
    protected int mNumTexCoordsInBuffer;
    protected float mU1, mV1, mU2 = 1.0f, mV2 = 1.0f;

    protected final ENG_RenderOperation mRenderOp = new ENG_RenderOperation();

    private static final short POSITION_BINDING = 0;
    private static final short TEXCOORD_BINDING = 1;

    public ENG_PanelOverlayElement(String name) {
        super(name);
        
        // Init tiling
        for (short i = 0; i < ENG_Config.MAX_TEXTURE_COORD_SETS; ++i) {
            mTileX[i] = 1.0f;
            mTileY[i] = 1.0f;
        }

        // No normals or colours
        if (getStringInterface().createParamDictionary("PanelOverlayElement")) {
            addBaseParameters();
        }
    }

    @Override
    protected void addBaseParameters() {
        
        super.addBaseParameters();
        ENG_ParamDictionary dict = getStringInterface().getParamDictionary();

        dict.addParameter(new ENG_ParameterDef("uv_coords",
                        "The texture coordinates for the texture. 1 set of uv values."
                        , ParameterType.PT_STRING),
                msCmdUVCoords);

        dict.addParameter(new ENG_ParameterDef("tiling",
                        "The number of times to repeat the background texture."
                        , ParameterType.PT_STRING),
                msCmdTiling);

        dict.addParameter(new ENG_ParameterDef("transparent",
                        "Sets whether the panel is transparent, i.e. invisible itself " +
                                "but it's contents are still displayed."
                        , ParameterType.PT_BOOL),
                msCmdTransparent);
    }

    @Override
    public void initialise() {
        
        boolean init = !mInitialised;
        super.initialise();
        if (init) {
            mRenderOp.vertexData = new ENG_VertexData();
            ENG_VertexDeclaration decl = mRenderOp.vertexData.vertexDeclaration;
            decl.addElement(POSITION_BINDING, 0, VertexElementType.VET_FLOAT3,
                    VertexElementSemantic.VES_POSITION);

            // Basic vertex data
            mRenderOp.vertexData.vertexStart = 0;
            mRenderOp.vertexData.vertexCount = 4;

            ENG_HardwareVertexBuffer vbuf = ENG_HardwareBufferManager.getSingleton()
                    .createVertexBuffer(decl.getVertexSize(POSITION_BINDING),
                            mRenderOp.vertexData.vertexCount,
                            Usage.HBU_STATIC_WRITE_ONLY.getUsage(), true);

            mRenderOp.vertexData.vertexBufferBinding.setBinding(POSITION_BINDING, vbuf);

            // No indexes & issue as a strip
            mRenderOp.useIndexes = false;
            mRenderOp.operationType = OperationType.OT_TRIANGLE_STRIP;

            mInitialised = true;
        }
    }

    @Override
    public void destroy(boolean skipGLCall) {
        if (mInitialised) {
            mRenderOp.vertexData.destroy(skipGLCall);
            mInitialised = false;
        }
    }

    public void setTiling(float x, float y) {
        setTiling(x, y, (short) 0);
    }

    public void setTiling(float x, float y, short layer) {
        assert (layer < ENG_Config.MAX_TEXTURE_COORD_SETS);
        assert (x != 0 && y != 0);

        mTileX[layer] = x;
        mTileY[layer] = y;

        mGeomUVsOutOfDate = true;
    }

    public float getTileX() {
        return mTileX[0];
    }

    public float getTileX(short layer) {
        return mTileX[layer];
    }

    public float getTileY() {
        return mTileY[0];
    }

    public float getTileY(short layer) {
        return mTileY[layer];
    }

    public void setUV(float u1, float v1, float u2, float v2) {
        mU1 = u1;
        mU2 = u2;
        mV1 = v1;
        mV2 = v2;
        mGeomUVsOutOfDate = true;
    }

    /**
     * 0 - u1 1 - v1 2 - u2 3 - v2
     *
     * @param ret
     */
    public void getUV(float[] ret) {
        ret[0] = mU1;
        ret[1] = mV1;
        ret[2] = mU2;
        ret[3] = mV2;
    }

    public void setTransparent(boolean b) {
        mTransparent = b;
    }

    public boolean isTransparent() {
        return mTransparent;
    }

    @Override
    protected void updatePositionGeometry() {
        

        float left, right, top, bottom;

        left = _getDerivedLeft() * 2 - 1;
        right = left + (mWidth * 2);
        top = -((_getDerivedTop() * 2) - 1);
        bottom = top - (mHeight * 2);

        ENG_HardwareVertexBuffer vbuf =
                mRenderOp.vertexData.vertexBufferBinding.getBuffer(POSITION_BINDING);

        FloatBuffer pPos = ((ByteBuffer) vbuf.lock(
                LockOptions.HBL_DISCARD)).asFloatBuffer();

        float zValue =
                ENG_RenderRoot.getRenderRoot().getRenderSystem()
                        .getMaximumDepthInputValue();

        pPos.put(left);
        pPos.put(top);
        pPos.put(zValue);

        pPos.put(left);
        pPos.put(bottom);
        pPos.put(zValue);

        pPos.put(right);
        pPos.put(top);
        pPos.put(zValue);

        pPos.put(right);
        pPos.put(bottom);
        pPos.put(zValue);

        vbuf.unlock();
    }

    @Override
    protected void updateTextureGeometry() {
        

        if (mpMaterial != null && mInitialised) {
            int numLayers = mpMaterial.getTechnique((short) 0)
                    .getPass((short) 0).getNumTextureUnitStates();

            ENG_VertexDeclaration decl = mRenderOp.vertexData.vertexDeclaration;

            if (mNumTexCoordsInBuffer > numLayers) {
                for (int i = mNumTexCoordsInBuffer; i > numLayers; --i) {
                    decl.removeElement(
                            VertexElementSemantic.VES_TEXTURE_COORDINATES, i);
                }
            } else if (mNumTexCoordsInBuffer < numLayers) {
                int offset = ENG_VertexElement.getTypeSize(
                        VertexElementType.VET_FLOAT2) * mNumTexCoordsInBuffer;
                for (int i = mNumTexCoordsInBuffer; i < numLayers; ++i) {
                    decl.addElement(
                            TEXCOORD_BINDING, offset, VertexElementType.VET_FLOAT2,
                            VertexElementSemantic.VES_TEXTURE_COORDINATES, (short) i);
                    offset +=
                            ENG_VertexElement.getTypeSize(VertexElementType.VET_FLOAT2);
                }
            }

            // if number of layers changed at all, we'll need to reallocate buffer
            if (mNumTexCoordsInBuffer != numLayers) {
                ENG_HardwareVertexBuffer newbuf =
                        ENG_HardwareBufferManager.getSingleton().createVertexBuffer(
                                decl.getVertexSize(TEXCOORD_BINDING),
                                mRenderOp.vertexData.vertexCount,
                                Usage.HBU_STATIC_WRITE_ONLY.getUsage(), true);

                mRenderOp.vertexData.vertexBufferBinding.setBinding(
                        TEXCOORD_BINDING, newbuf);
                mNumTexCoordsInBuffer = numLayers;
            }

            // Get the tcoord buffer & lock
            if (mNumTexCoordsInBuffer > 0) {
                ENG_HardwareVertexBuffer vbuf =
                        mRenderOp.vertexData.vertexBufferBinding.getBuffer(
                                TEXCOORD_BINDING);
                FloatBuffer pVBStart = ((ByteBuffer) vbuf.lock(
                        LockOptions.HBL_DISCARD)).asFloatBuffer();

                int uvsize =
                        ENG_VertexElement.getTypeSize(VertexElementType.VET_FLOAT2) /
                                ENG_Float.SIZE_IN_BYTES;
                int vertexSize = decl.getVertexSize(TEXCOORD_BINDING) /
                        ENG_Float.SIZE_IN_BYTES;
                for (short i = 0; i < numLayers; ++i) {
                    // Calc upper tex coords
                    float upperX = mU2 * mTileX[i];
                    float upperY = mV2 * mTileY[i];

                    int pTex = i * uvsize * ENG_Float.SIZE_IN_BYTES;
                    pVBStart.put(pTex, mU1);
                    pVBStart.put(pTex + 1, mV1);

                    pTex += vertexSize;

                    pVBStart.put(pTex, mU1);
                    pVBStart.put(pTex + 1, upperY);

                    pTex += vertexSize;

                    pVBStart.put(pTex, upperX);
                    pVBStart.put(pTex + 1, mV1);

                    pTex += vertexSize;

                    pVBStart.put(pTex, upperX);
                    pVBStart.put(pTex + 1, upperY);
                }
                vbuf.unlock();
            }
        }
    }

    @Override
    public String getTypeName() {
        
        return "Panel";
    }

    @Override
    public void getRenderOperation(ENG_RenderOperation op) {
        

        op.set(mRenderOp);
    }

    @Override
    public void _updateRenderQueue(ENG_RenderQueue queue) {
        
        if (mVisible) {
            if (!mTransparent && mpMaterial != null) {
                super._updateRenderQueue(queue);
            }

            for (ENG_OverlayElement o : mChildren.values()) {
                o._updateRenderQueue(queue);
            }
        }
    }

}
