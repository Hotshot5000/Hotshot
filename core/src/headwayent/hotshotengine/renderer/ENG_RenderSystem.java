/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Plane;
import headwayent.hotshotengine.ENG_Radian;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Byte;
import headwayent.hotshotengine.renderer.ENG_BlendMode.LayerBlendModeEx;
import headwayent.hotshotengine.renderer.ENG_BlendMode.SceneBlendFactor;
import headwayent.hotshotengine.renderer.ENG_BlendMode.SceneBlendOperation;
import headwayent.hotshotengine.renderer.ENG_Common.CompareFunction;
import headwayent.hotshotengine.renderer.ENG_Common.CullingMode;
import headwayent.hotshotengine.renderer.ENG_Common.FilterOptions;
import headwayent.hotshotengine.renderer.ENG_Common.FilterType;
import headwayent.hotshotengine.renderer.ENG_Common.FogMode;
import headwayent.hotshotengine.renderer.ENG_Common.PolygonMode;
import headwayent.hotshotengine.renderer.ENG_Common.ShadeOptions;
import headwayent.hotshotengine.renderer.ENG_GpuProgram.GpuProgramType;
import headwayent.hotshotengine.renderer.ENG_RenderSystemCapabilities.DriverVersion;
import headwayent.hotshotengine.renderer.ENG_TextureUnitState.TextureAddressingMode;
import headwayent.hotshotengine.renderer.ENG_TextureUnitState.UVWAddressingMode;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Map.Entry;

public abstract class ENG_RenderSystem {

/*	public enum CullingMode
    {
        /// Hardware never culls triangles and renders everything it receives.
        CULL_NONE(1),
        /// Hardware culls triangles whose vertices are listed clockwise in the view (default).
        CULL_CLOCKWISE(2),
        /// Hardware culls triangles whose vertices are listed anticlockwise in the view.
        CULL_ANTICLOCKWISE(3);
        
        private int mode;
        
        private CullingMode(int mode) {
        	this.mode = mode;
        }
        
        public int getMode() {
        	return mode;
        }
    }
	
	public enum ShadeOptions
    {
        SO_FLAT,
        SO_GOURAUD,
        SO_PHONG
    }*/

    public enum TexCoordCalcMethod {
        /// No calculated texture coordinates
        TEXCALC_NONE,
        /// Environment map based on vertex normals
        TEXCALC_ENVIRONMENT_MAP,
        /// Environment map based on vertex positions
        TEXCALC_ENVIRONMENT_MAP_PLANAR,
        TEXCALC_ENVIRONMENT_MAP_REFLECTION,
        TEXCALC_ENVIRONMENT_MAP_NORMAL,
        /// Projective texture
        TEXCALC_PROJECTIVE_TEXTURE;

        public static TexCoordCalcMethod getTexCoordCalcMethod(int i) {
            switch (i) {
                case 0:
                    return TEXCALC_NONE;
                case 1:
                    return TEXCALC_ENVIRONMENT_MAP;
                case 2:
                    return TEXCALC_ENVIRONMENT_MAP_PLANAR;
                case 3:
                    return TEXCALC_ENVIRONMENT_MAP_REFLECTION;
                case 4:
                    return TEXCALC_ENVIRONMENT_MAP_NORMAL;
                case 5:
                    return TEXCALC_PROJECTIVE_TEXTURE;
                default:
                    throw new IllegalArgumentException("The value " + i +
                            " is not a valid texture coordinate calculation method");
            }
        }
    }

    public enum StencilOperation {
        /// Leave the stencil buffer unchanged
        SOP_KEEP,
        /// Set the stencil value to zero
        SOP_ZERO,
        /// Set the stencil value to the reference value
        SOP_REPLACE,
        /// Increase the stencil value by 1, clamping at the maximum value
        SOP_INCREMENT,
        /// Decrease the stencil value by 1, clamping at 0
        SOP_DECREMENT,
        /// Increase the stencil value by 1, wrapping back to 0 when incrementing the maximum value
        SOP_INCREMENT_WRAP,
        /// Decrease the stencil value by 1, wrapping when decrementing 0
        SOP_DECREMENT_WRAP,
        /// Invert the bits of the stencil buffer
        SOP_INVERT;

        public static StencilOperation getStencilOperation(int i) {
            switch (i) {
                case 0:
                    return SOP_KEEP;
                case 1:
                    return SOP_ZERO;
                case 2:
                    return SOP_REPLACE;
                case 3:
                    return SOP_INCREMENT;
                case 4:
                    return SOP_DECREMENT;
                case 5:
                    return SOP_INCREMENT_WRAP;
                case 6:
                    return SOP_DECREMENT_WRAP;
                case 7:
                    return SOP_INVERT;
                default:
                    throw new IllegalArgumentException("The value " + i +
                            " is not a valid stencil operation");
            }
        }
    }


    protected final TreeMap<String, ENG_RenderTarget> mRenderTargets = new TreeMap<>();
    protected final TreeMap<ENG_Byte, ArrayList<ENG_RenderTarget>> mPrioritisedRenderTargets = new TreeMap<>();
    protected ENG_RenderTarget mActiveRenderTarget;
    protected ENG_GpuProgramParameters mActiveVertexGpuProgramParameters;
    protected ENG_GpuProgramParameters mActiveGeometryGpuProgramParameters;
    protected ENG_GpuProgramParameters mActiveFragmentGpuProgramParameters;

    protected ENG_TextureManager mTextureManager;
    //protected boolean mVSync;

    //protected boolean mInvertVertexWinding;

    protected ENG_Viewport mActiveViewport;
    protected CullingMode mCullingMode = CullingMode.CULL_CLOCKWISE;

    protected boolean mVSync = true;
    protected int mVSyncInterval = 1;
    protected boolean mWBuffer;

    protected int mBatchCount;
    protected int mFaceCount;
    protected int mVertexCount;

//    protected final ENG_ColorValue[][] mManualBlendColours =
//            new ENG_ColorValue[ENG_Config.MAX_TEXTURE_LAYERS][2];
    protected boolean mInvertVertexWinding;

    /// Texture units from this upwards are disabled
    protected int mDisabledTexUnitsFrom;

    /// number of times to render the current state
    protected int mCurrentPassIterationCount;
    protected int mCurrentPassIterationNum;
    /// Whether to update the depth bias per render call
    protected boolean mDerivedDepthBias;
    protected float mDerivedDepthBiasBase;
    protected float mDerivedDepthBiasMultiplier;
    protected float mDerivedDepthBiasSlopeScale;

    protected final ArrayList<String> mEventNames = new ArrayList<>();

    protected LinkedList<ENG_HardwareOcclusionQuery> mHwOcclusionQueries =
            new LinkedList<>();

    protected boolean mVertexProgramBound;
    protected boolean mGeometryProgramBound;
    protected boolean mFragmentProgramBound;

    protected ArrayList<ENG_Plane> mClipPlanes = new ArrayList<>();
    // Indicator that we need to re-set the clip planes on next render call
    protected boolean mClipPlanesDirty = true;

    /// Used to store the capabilities of the graphics card
    protected ENG_RenderSystemCapabilities mRealCapabilities;
    protected ENG_RenderSystemCapabilities mCurrentCapabilities;
    protected boolean mUseCustomCapabilities;

    protected abstract void setClipPlanesImpl(ArrayList<ENG_Plane> clipPlanes);

    protected abstract void initialiseFromRenderSystemCapabilities(
            ENG_RenderSystemCapabilities caps, ENG_RenderTarget primary);

    protected final DriverVersion mDriverVersion = new DriverVersion();

    protected boolean mTexProjRelative;
    protected final ENG_Vector4D mTexProjRelativeOrigin = new ENG_Vector4D();

    public ENG_RenderSystem() {

    }

    //public abstract String getName();

    public void _initRenderTargets() {
        for (Entry<String, ENG_RenderTarget> stringENGRenderTargetEntry : mRenderTargets.entrySet()) {
            stringENGRenderTargetEntry.getValue().resetStatistics();
        }
    }

    public void _updateAllRenderTargets() {
        _updateAllRenderTargets(true);
    }

    public void _updateAllRenderTargets(boolean swapBuffers) {
        for (Entry<ENG_Byte, ArrayList<ENG_RenderTarget>> entry : mPrioritisedRenderTargets.entrySet()) {
            for (int i = 0; i < entry.getValue().size(); ++i) {
                if (entry.getValue().get(i).isActive() &&
                        entry.getValue().get(i).isAutoUpdated()) {
                    entry.getValue().get(i).update(swapBuffers);
                }
            }
        }
    }

    public void _swapAllRenderTargetBuffers() {
        _swapAllRenderTargetBuffers(true);
    }

    public void _swapAllRenderTargetBuffers(boolean waitForVSync) {
        for (Entry<ENG_Byte, ArrayList<ENG_RenderTarget>> entry : mPrioritisedRenderTargets.entrySet()) {
            for (int i = 0; i < entry.getValue().size(); ++i) {
                if (entry.getValue().get(i).isActive() &&
                        entry.getValue().get(i).isAutoUpdated()) {
                    entry.getValue().get(i).swapBuffers(waitForVSync);
                }
            }
        }
    }

    public ENG_RenderWindow _initialise(boolean autoCreateWindow, String windowTitle) {
        mVertexProgramBound = false;
        mGeometryProgramBound = false;
        mFragmentProgramBound = false;
        return null;
    }

    public ENG_RenderWindow _createRenderWindow(String name,
                                                int width, int height, boolean fullScreen) {
        return _createRenderWindow(name, width, height, fullScreen, null);
    }

    public abstract ENG_RenderWindow _createRenderWindow(String name,
                                                         int width, int height, boolean fullScreen,
                                                         TreeMap<String, String> miscParams);

    public abstract ENG_MultiRenderTarget createMultiRenderTarget(String name);

    public void destroyRenderWindow(String name) {
        detachRenderTarget(name);
    }

    public void destroyRenderTexture(String name) {
        detachRenderTarget(name);
    }

    public void destroyRenderTarget(String name) {
        destroyRenderTarget(name, false);
    }

    public void destroyRenderTarget(String name, boolean skipGLDelete) {
        ENG_RenderTarget detachRenderTarget = detachRenderTarget(name);
        detachRenderTarget.destroy(skipGLDelete);
    }

    public void attachRenderTarget(ENG_RenderTarget target) {
        if (target.getPriority() >= ENG_RenderTarget.OGRE_NUM_RENDERTARGET_GROUPS) {
            throw new IllegalArgumentException("Invalid priority for render target!");
        }
        mRenderTargets.put(target.getName(), target);
        ENG_Byte priority = new ENG_Byte(target.getPriority());
        ArrayList<ENG_RenderTarget> list = mPrioritisedRenderTargets.get(priority);

        if (list == null) {
            list = new ArrayList<>();
            mPrioritisedRenderTargets.put(priority, list);
        }
        list.add(target);
    }

    public ENG_RenderTarget getRenderTarget(String name) {
        return mRenderTargets.get(name);
    }

    public ENG_RenderTarget detachRenderTarget(String name) {
        ENG_RenderTarget ret = mRenderTargets.get(name);
        if (ret != null) {
            for (Entry<ENG_Byte, ArrayList<ENG_RenderTarget>> engByteArrayListEntry : mPrioritisedRenderTargets.entrySet()) {
                if (engByteArrayListEntry.getValue().remove(ret)) {
                    break;
                }
            }
            mRenderTargets.remove(name);
        }

        if (ret == mActiveRenderTarget) {
            mActiveRenderTarget = null;
        }

        return ret;
    }

    public Iterator<Entry<String, ENG_RenderTarget>> getIterator() {
        return mRenderTargets.entrySet().iterator();
    }

    public abstract String getErrorDescription(long errorNumber);

    public void useCustomRenderSystemCapabilities(
            ENG_RenderSystemCapabilities capabilities) {
        mCurrentCapabilities = capabilities;
        mUseCustomCapabilities = true;
    }

    public boolean getWaitForVerticalBlank() {
        return mVSync;
    }

    public void setWaitForVerticalBlank(boolean vsync) {
        mVSync = vsync;
    }

    public ENG_Viewport _getViewport() {
        return mActiveViewport;
    }

    /** @noinspection deprecation*/
    public abstract void _useLights(ArrayList<ENG_Light> lights, short limit);

    public boolean areFixedFunctionLightsInViewSpace() {
        return false;
    }

    public abstract void _setWorldMatrix(ENG_Matrix4 m);

    public void _setWorldMatrixes(ENG_Matrix4[] m, short count) {
        _setWorldMatrix(ENG_Math.MAT4_IDENTITY);
    }

    public abstract void _setViewMatrix(ENG_Matrix4 m);

    public abstract void _setProjectionMatrix(ENG_Matrix4 m);

    public void _setTextureUnitSettings(int texUnit, ENG_TextureUnitState tl) {
        ENG_Texture tex = tl._getTexturePtr();

        // Shared vertex / fragment textures or no vertex texture support
        // Bind texture (may be blank)
        _setTexture(texUnit, true, tex);

        // Set texture coordinate set
        _setTextureCoordSet(texUnit, tl.getTextureCoordSet());

        // Set texture layer filtering
        _setTextureUnitFiltering(texUnit,
                tl.getTextureFiltering(FilterType.FT_MIN),
                tl.getTextureFiltering(FilterType.FT_MAG),
                tl.getTextureFiltering(FilterType.FT_MIP));

        // Set texture layer filtering
        _setTextureLayerAnisotropy(texUnit, tl.getTextureAnisotropy());

        // Set mipmap biasing
        _setTextureMipmapBias(texUnit, tl.getTextureMipmapBias());

        // Set blend modes
        // Note, colour before alpha is important
        _setTextureBlendMode(texUnit, tl.getColourBlendMode());
        _setTextureBlendMode(texUnit, tl.getAlphaBlendMode());

        // Texture addressing mode
        UVWAddressingMode uvw = tl.getTextureAddressingMode();
        _setTextureAddressingMode(texUnit, uvw);
        // Set texture border colour only if required
        if (uvw.u == TextureAddressingMode.TAM_BORDER ||
                uvw.v == TextureAddressingMode.TAM_BORDER ||
                uvw.w == TextureAddressingMode.TAM_BORDER) {
            _setTextureBorderColour(texUnit, tl.getTextureBorderColour());
        }

        // Change tetxure matrix
        //   _setTextureMatrix(texUnit, tl.getTextureTransform()); //Not in programmable!
    }

    public void _disableTextureUnit(int texUnit) {

    }

    public void _disableTextureUnitsFrom(int texUnit) {

    }

    public abstract void _setSurfaceParams(ENG_ColorValue ambient, ENG_ColorValue diffuse,
                                           ENG_ColorValue specular, ENG_ColorValue emissive, float shininess, int tracking);

    public abstract void _setPointSpritesEnabled(boolean enabled);

    public abstract void _setPointParameters(float size, boolean attenuationEnabled,
                                             float constant, float linear, float quadratic, float minSize, float maxSize);

    public void _setTexture(int unit, boolean enabled,
                            ENG_Texture texPtr) {

    }

    public void _setTexture(int unit, boolean enabled, String texname) {

    }

    public void _setVertexTexture(int unit, ENG_Texture tex) {
        throw new UnsupportedOperationException(
                "This rendersystem does not support separate vertex texture samplers, " +
                        "you should use the regular texture samplers which are shared between " +
                        "the vertex and fragment units.");
    }

    public abstract void _setTextureCoordSet(int unit, int index);

    /**
     * For GIWS
     *
     * @param unit
     * @param calcMethod
     */
    public void _setTextureCoordCalculation(int unit, int calcMethod) {
        _setTextureCoordCalculation(unit,
                TexCoordCalcMethod.getTexCoordCalcMethod(calcMethod));
    }

    public void _setTextureCoordCalculation(int unit, TexCoordCalcMethod m) {
        _setTextureCoordCalculation(unit, m, null);
    }

    public abstract void _setTextureCoordCalculation(
            int unit, TexCoordCalcMethod m,
            ENG_Frustum frustum);

    public void _setTextureBlendMode(int unit, int blendType, int operation,
                                     int src1, int src2) {
        _setTextureBlendMode(unit,
                new LayerBlendModeEx(blendType, operation, src1, src2));
    }

    public abstract void _setTextureBlendMode(int unit, LayerBlendModeEx bm);

    /**
     * For GIWS
     *
     * @param unit
     * @param minFilter
     * @param magFilter
     * @param mipFilter
     */
    public void _setTextureUnitFiltering(int unit, int minFilter,
                                         int magFilter, int mipFilter) {
        _setTextureUnitFiltering(unit,
                FilterOptions.getFilterOptions(minFilter),
                FilterOptions.getFilterOptions(magFilter),
                FilterOptions.getFilterOptions(mipFilter));
    }

    public void _setTextureUnitFiltering(int unit, FilterOptions minFilter,
                                         FilterOptions magFilter, FilterOptions mipFilter) {
        _setTextureUnitFiltering(unit, FilterType.FT_MIN, minFilter);
        _setTextureUnitFiltering(unit, FilterType.FT_MAG, magFilter);
        _setTextureUnitFiltering(unit, FilterType.FT_MIP, mipFilter);
    }

    public abstract void _setTextureUnitFiltering(int unit,
                                                  FilterType ftype, FilterOptions filter);

    public abstract void _setTextureLayerAnisotropy(int unit, int maxAnisotropy);

    /**
     * For GIWS
     *
     * @param unit
     * @param u
     * @param v
     * @param w
     */
    public void _setTextureAddressingMode(int unit,
                                          int u,
                                          int v,
                                          int w) {
        _setTextureAddressingMode(unit, new UVWAddressingMode(u, v, w));
    }

    public abstract void _setTextureAddressingMode(int unit, UVWAddressingMode uvw);

    public abstract void _setTextureBorderColour(int unit, ENG_ColorValue colour);

    public abstract void _setTextureMipmapBias(int unit, float bias);

    public abstract void _setTextureMatrix(int unit, ENG_Matrix4 xform);

    public void _setSceneBlending(int sourceFactor, int destFactor,
                                  int op) {
        _setSceneBlending(SceneBlendFactor.getSceneBlendFactor(sourceFactor),
                SceneBlendFactor.getSceneBlendFactor(destFactor),
                SceneBlendOperation.getSceneBlendOperation(op));
    }

    public abstract void _setSceneBlending(SceneBlendFactor sourceFactor,
                                           SceneBlendFactor destFactor, SceneBlendOperation op);

    public void _setSeparateSceneBlending(int sourceFactor,
                                          int destFactor, int sourceFactorAlpha,
                                          int destFactorAlpha, int op, int alphaOp) {
        _setSeparateSceneBlending(
                SceneBlendFactor.getSceneBlendFactor(sourceFactor),
                SceneBlendFactor.getSceneBlendFactor(destFactor),
                SceneBlendFactor.getSceneBlendFactor(sourceFactorAlpha),
                SceneBlendFactor.getSceneBlendFactor(destFactorAlpha),
                SceneBlendOperation.getSceneBlendOperation(op),
                SceneBlendOperation.getSceneBlendOperation(alphaOp));
    }

    public abstract void _setSeparateSceneBlending(
            SceneBlendFactor sourceFactor,
            SceneBlendFactor destFactor, SceneBlendFactor sourceFactorAlpha,
            SceneBlendFactor destFactorAlpha, SceneBlendOperation op,
            SceneBlendOperation alphaOp);

    public void _setAlphaRejectSettings(int func, byte value,
                                        boolean alphaToCoverage) {
        _setAlphaRejectSettings(CompareFunction.getCompareFunction(func),
                value, alphaToCoverage);
    }

    public abstract void _setAlphaRejectSettings(CompareFunction func,
                                                 byte value, boolean alphaToCoverage);

    public void _setTextureProjectionRelativeTo(
            boolean enabled, ENG_Vector4D pos) {
        mTexProjRelative = enabled;
        mTexProjRelativeOrigin.set(pos);
    }

    public abstract void _beginFrame();

    public Object _pauseFrame() {
        _endFrame();
        return new Object();
    }

    public void _resumeFrame(Object obj) {
        _beginFrame();
    }

    public abstract void _endFrame();

    //public abstract void _setViewport(ENG_Viewport vp);
	
/*	public ENG_Viewport _getViewport() {
		return mActiveViewport;
	}*/

    public void _setCullingMode(int mode) {
        _setCullingMode(CullingMode.getCullingMode(mode));
    }

    public abstract void _setCullingMode(CullingMode mode);

    public CullingMode _getCullingMode() {
        return mCullingMode;
    }

    public void _setDepthBufferParams() {
        _setDepthBufferParams(true, true, CompareFunction.CMPF_LESS_EQUAL);
    }

    public void _setDepthBufferParams(boolean depthTest,
                                      boolean depthWrite, int depthFunction) {
        _setDepthBufferParams(depthTest, depthWrite,
                CompareFunction.getCompareFunction(depthFunction));
    }

    public abstract void _setDepthBufferParams(boolean depthTest, boolean depthWrite,
                                               CompareFunction depthFunction);

    public abstract void _setDepthBufferCheckEnabled(boolean enabled);

    public abstract void _setDepthBufferWriteEnabled(boolean enabled);

    public abstract void _setDepthBufferFunction(CompareFunction func);

    public abstract void _setColourBufferWriteEnabled(boolean red, boolean green,
                                                      boolean blue, boolean alpha);

    public abstract void _setDepthBias(float constantBias, float slopeScaleBias);

    public void _setFog() {
        _setFog(FogMode.FOG_NONE, ENG_ColorValue.WHITE, 1.0f, 0.0f, 1.0f);
    }

    public abstract void _setFog(FogMode mode, ENG_ColorValue colour,
                                 float expDensity, float linearStart, float linearEnd);

    public void _beginGeometryCount() {
        mBatchCount = mFaceCount = mVertexCount = 0;
    }

    public int _getFaceCount() {
        return mFaceCount;
    }

    public int _getBatchCount() {
        return mBatchCount;
    }

    public int _getVertexCount() {
        return mVertexCount;
    }

    public int convertColourValue(float red, float green, float blue,
                                  float alpha) {
        return convertColourValue(new ENG_ColorValue(red, green, blue, alpha));
    }

    public int convertColourValue(ENG_ColorValue color) {
        return ENG_VertexElement.convertColorValue(
                color, getColorVertexElementType());
    }

    public void convertColourValue(ENG_ColorValue color, int[] pDest, int offset) {
        pDest[offset] =
                ENG_VertexElement.convertColorValue(color, getColorVertexElementType());
    }

    public void _convertProjectionMatrix(ENG_Matrix4 matrix,
                                         ENG_Matrix4 dest) {
        _convertProjectionMatrix(matrix, dest, false);
    }

    public abstract void _convertProjectionMatrix(ENG_Matrix4 matrix,
                                                  ENG_Matrix4 dest, boolean forGpuProgram);

    public void _makeProjectionMatrix(ENG_Radian fovy, float aspect,
                                      float nearPlane, float farPlane,
                                      ENG_Matrix4 dest) {
        _makeProjectionMatrix(fovy, aspect, nearPlane, farPlane, dest, false);
    }

    public abstract void _makeProjectionMatrix(ENG_Radian fovy, float aspect,
                                               float nearPlane, float farPlane,
                                               ENG_Matrix4 dest, boolean forGpuProgram);

    public void _makeProjectionMatrix(float left, float right,
                                      float bottom, float top, float aspect,
                                      float nearPlane, float farPlane,
                                      ENG_Matrix4 dest) {
        _makeProjectionMatrix(left, right, bottom, top, aspect,
                nearPlane, farPlane, dest, false);
    }

    public abstract void _makeProjectionMatrix(float left, float right,
                                               float bottom, float top, float aspect,
                                               float nearPlane, float farPlane,
                                               ENG_Matrix4 dest, boolean forGpuProgram);

    public void _makeOrthoMatrix(ENG_Radian fovy, float aspect,
                                 float nearPlane, float farPlane,
                                 ENG_Matrix4 dest) {
        _makeOrthoMatrix(fovy, aspect, nearPlane, farPlane, dest, false);
    }

    public abstract void _makeOrthoMatrix(ENG_Radian fovy, float aspect,
                                          float nearPlane, float farPlane,
                                          ENG_Matrix4 dest, boolean forGpuProgram);

    public abstract void _applyObliqueDepthProjection(ENG_Matrix4 matrix, ENG_Plane plane,
                                                      boolean forGpuProgram);

    public abstract void _setPolygonMode(PolygonMode level);

    public abstract void setStencilCheckEnabled(boolean enabled);

    public void setStencilBufferParams() {
        setStencilBufferParams(CompareFunction.CMPF_ALWAYS_PASS, 0, 0xFFFFFFFF,
                StencilOperation.SOP_KEEP, StencilOperation.SOP_KEEP,
                StencilOperation.SOP_KEEP, false);
    }

    public abstract void setStencilBufferParams(CompareFunction func,
                                                int refValue, int mask,
                                                StencilOperation stencilFailOp,
                                                StencilOperation depthFailOp,
                                                StencilOperation passOp,
                                                boolean twoSidedOperation);

    public abstract void setVertexDeclaration(ENG_VertexDeclaration decl);

    public abstract void setVertexBufferBinding(ENG_VertexBufferBinding binding);

    public abstract void setNormaliseNormals(boolean normalise);

    public void _render(ENG_RenderOperation op) {
        int val;

        if (op.useIndexes) {
            val = op.indexData.indexCount;
        } else {
            val = op.vertexData.vertexCount;
        }

        if (mCurrentPassIterationCount > 1) {
            val *= mCurrentPassIterationCount;
        }
        mCurrentPassIterationNum = 0;

        switch (op.operationType) {
            case OT_TRIANGLE_LIST:
                mFaceCount += val / 3;
                break;
            case OT_TRIANGLE_STRIP:
            case OT_TRIANGLE_FAN:
                mFaceCount += val - 2;
                break;
            case OT_POINT_LIST:
            case OT_LINE_LIST:
            case OT_LINE_STRIP:
                break;
        }

        mVertexCount += op.vertexData.vertexCount;
        mBatchCount += mCurrentPassIterationCount;

        if (mClipPlanesDirty) {
            setClipPlanesImpl(mClipPlanes);
            mClipPlanesDirty = false;
        }
    }

    public DriverVersion getDriverVersion() {
        return mDriverVersion;
    }

    public void bindGpuProgram(ENG_GpuProgram prg) {
        switch (prg.getType()) {
            case GPT_VERTEX_PROGRAM:
                // mark clip planes dirty if changed (programmable can change space)
                if (!mVertexProgramBound && !mClipPlanes.isEmpty())
                    mClipPlanesDirty = true;

                mVertexProgramBound = true;
                break;
            case GPT_GEOMETRY_PROGRAM:
                mGeometryProgramBound = true;
                break;
            case GPT_FRAGMENT_PROGRAM:
                mFragmentProgramBound = true;
                break;
        }
    }

    public void bindGpuProgramParameters(int gptype,
                                         ENG_GpuProgramParameters params, int variabilityMask) {
        bindGpuProgramParameters(GpuProgramType.getGpuProgramType(gptype),
                params, variabilityMask);
    }

    public abstract void bindGpuProgramParameters(GpuProgramType gptype,
                                                  ENG_GpuProgramParameters params, int variabilityMask);

    public abstract void bindGpuProgramPassIterationParameters(GpuProgramType gptype);

    public void unbindGpuProgram(int gptype) {
        unbindGpuProgram(GpuProgramType.getGpuProgramType(gptype));
    }

    public void unbindGpuProgram(GpuProgramType gptype) {
        switch (gptype) {
            case GPT_VERTEX_PROGRAM:
                // mark clip planes dirty if changed (programmable can change space)
                if (mVertexProgramBound && !mClipPlanes.isEmpty())
                    mClipPlanesDirty = true;
                mVertexProgramBound = false;
                break;
            case GPT_GEOMETRY_PROGRAM:
                mGeometryProgramBound = false;
                break;
            case GPT_FRAGMENT_PROGRAM:
                mFragmentProgramBound = false;
                break;
        }
    }

    public boolean isGpuProgramBound(GpuProgramType gptype) {
        switch (gptype) {
            case GPT_VERTEX_PROGRAM:
                return mVertexProgramBound;
            case GPT_GEOMETRY_PROGRAM:
                return mGeometryProgramBound;
            case GPT_FRAGMENT_PROGRAM:
                return mFragmentProgramBound;
        }
        // Make compiler happy
        return false;
    }

    public void setClipPlanes(ArrayList<ENG_Plane> clipPlanes) {
        if (clipPlanes != mClipPlanes) {
            mClipPlanes = clipPlanes;
            mClipPlanesDirty = true;
        }
    }

    public void addClipPlane(ENG_Plane p) {
        mClipPlanes.add(p);
        mClipPlanesDirty = true;
    }

    public void addClipPlane(float A, float B, float C, float D) {
        addClipPlane(new ENG_Plane(A, B, C, D));
    }

    public void resetClipPlanes() {
        if (!mClipPlanes.isEmpty()) {
            mClipPlanes.clear();
            mClipPlanesDirty = true;
        }
    }

//	public void _initRenderTargets() {

//	}

    public void _notifyCameraRemoved(ENG_Camera cam) {
        for (Entry<String, ENG_RenderTarget> stringENGRenderTargetEntry : mRenderTargets.entrySet()) {
            stringENGRenderTargetEntry.getValue()._notifyCameraRemoved(cam);
        }
    }

//	public void _updateAllRenderTargets(boolean swapBuffers) {

//	}

    public void shutDown() {

    }

    public abstract void _setViewport(ENG_Viewport vp);

    public abstract String getName();

    public abstract TreeMap<String, ENG_ConfigOption> getConfigOptions();

    public abstract void setConfigOption(String name, String value);

    public abstract String validateConfigOptions();

    public abstract ENG_RenderSystemCapabilities createRenderSystemCapabilities();

    public abstract void reinitialize();

    public abstract void setAmbientLight(float r, float g, float b);

    public abstract void setShadingType(ShadeOptions so);

    public abstract void setLightingEnabled(boolean enabled);

    public boolean getWBufferEnabled() {
        return mWBuffer;
    }

    public void setWBufferEnabled(boolean enabled) {
        mWBuffer = enabled;
    }

    public abstract VertexElementType getColorVertexElementType();

//	public abstract void _convertProjectionMatrix(ENG_Matrix4 matrix, ENG_Matrix4 dest,
//			boolean forGpuProgram);

    public boolean getVertexWindingInverted() {
        
        return mInvertVertexWinding;
    }

    public boolean getInvertVertexWinding() {
        return mInvertVertexWinding;
    }

    public void setInvertVertexWinding(boolean invert) {
        mInvertVertexWinding = invert;
    }

    public void setScissorTest(boolean enabled) {
        setScissorTest(enabled, 0, 0, 800, 600);
    }

    public abstract void setScissorTest(boolean enabled, int left, int top,
                                        int right, int bottom);

    public abstract float getHorizontalTexelOffset();

    public abstract float getVerticalTexelOffset();

    public abstract float getMinimumDepthInputValue();

    public abstract float getMaximumDepthInputValue();

    public void setCurrentPassIterationCount(int count) {
        mCurrentPassIterationCount = count;
    }

    public void setDeriveDepthBias(boolean derive) {
        setDeriveDepthBias(derive, 0.0f, 0.0f, 0.0f);
    }

    public void setDeriveDepthBias(boolean derive, float baseValue,
                                   float multiplier, float slopeScale) {
        mDerivedDepthBias = derive;
        mDerivedDepthBiasBase = baseValue;
        mDerivedDepthBiasMultiplier = multiplier;
        mDerivedDepthBiasSlopeScale = slopeScale;
    }

    public abstract void _setRenderTarget(ENG_RenderTarget target);

    public ArrayList<String> getRenderSystemEvents() {
        return mEventNames;
    }

    public abstract void preExtraThreadsStarted();

    public abstract void postExtraThreadsStarted();

    public abstract void registerThread();

    public abstract void unregisterThread();

    public abstract int getDisplayMonitorCount();

    public void clearFrameBuffer(int buffers) {
        clearFrameBuffer(buffers, ENG_ColorValue.BLACK, 1.0f, (short) 0);
    }

    public void clearFrameBuffer(int buffers, ENG_ColorValue colour) {
        clearFrameBuffer(buffers, colour, 1.0f, (short) 0);
    }

    public void clearFrameBuffer(int buffers, ENG_ColorValue colour, float depth) {
        clearFrameBuffer(buffers, colour, depth, (short) 0);
    }

    public abstract void clearFrameBuffer(int buffers, ENG_ColorValue colour,
                                          float depth, short stencil);

    public ENG_RenderSystemCapabilities getCapabilities() {
        return mCurrentCapabilities;
    }

    public ENG_TextureManager getTextureManager() {
        return mTextureManager;
    }
}
