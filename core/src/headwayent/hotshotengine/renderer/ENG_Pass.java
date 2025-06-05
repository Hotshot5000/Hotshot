/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 4:51 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.renderer.ENG_BlendMode.SceneBlendFactor;
import headwayent.hotshotengine.renderer.ENG_BlendMode.SceneBlendOperation;
import headwayent.hotshotengine.renderer.ENG_BlendMode.SceneBlendType;
import headwayent.hotshotengine.renderer.ENG_Common.CompareFunction;
import headwayent.hotshotengine.renderer.ENG_Common.CullingMode;
import headwayent.hotshotengine.renderer.ENG_Common.FogMode;
import headwayent.hotshotengine.renderer.ENG_Common.ManualCullingMode;
import headwayent.hotshotengine.renderer.ENG_Common.PolygonMode;
import headwayent.hotshotengine.renderer.ENG_Common.ShadeOptions;
import headwayent.hotshotengine.renderer.ENG_Common.TextureFilterOptions;
import headwayent.hotshotengine.renderer.ENG_Common.TrackVertexColourEnum;
import headwayent.hotshotengine.renderer.ENG_GpuProgram.GpuProgramType;
import headwayent.hotshotengine.renderer.ENG_Light.LightTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ENG_Pass {

    /// Categorisation of passes for the purpose of additive lighting
    public enum IlluminationStage {
        /// Part of the rendering which occurs without any kind of direct lighting
        IS_AMBIENT,
        /// Part of the rendering which occurs per light
        IS_PER_LIGHT,
        /// Post-lighting rendering
        IS_DECAL,
        /// Not determined
        IS_UNKNOWN
    }

//    private static ReentrantLock idIncrementLock = new ReentrantLock();
    private static int nextId;
    private static final HashSet<Integer> ids = new HashSet<>();

//    public static ReentrantLock mTexUnitChangeMutex = new ReentrantLock();
//    public static ReentrantLock mGpuProgramChangeMutex = new ReentrantLock();
//
//    public static ReentrantLock msDirtyHashListMutex = new ReentrantLock();
//    public static ReentrantLock msPassGraveyardMutex = new ReentrantLock();

    protected static ENG_HashFunc msHashFunc = ENG_MinGpuProgramChangeHashFunc.sMinGpuProgramChangeHashFunc;
    protected static final HashSet<ENG_Pass> msDirtyHash = new HashSet<>();
    protected static final HashSet<ENG_Pass> msPassGraveyard = new HashSet<>();

    private int id;
    protected final ENG_Technique mParent;
    protected final ENG_Short mIndex = new ENG_Short();
    protected String mName;
    protected int mHash;
    protected boolean mHashDirtyQueued;

    // Blending factors
    protected SceneBlendFactor mSourceBlendFactor = SceneBlendFactor.SBF_ONE;
    protected SceneBlendFactor mDestBlendFactor = SceneBlendFactor.SBF_ZERO;
    protected SceneBlendFactor mSourceBlendFactorAlpha = SceneBlendFactor.SBF_ONE;
    protected SceneBlendFactor mDestBlendFactorAlpha = SceneBlendFactor.SBF_ZERO;

    // Used to determine if separate alpha blending should be used for color and alpha channels
    protected boolean mSeparateBlend;

    // Blending operations
    protected SceneBlendOperation mBlendOperation = SceneBlendOperation.SBO_ADD;
    protected SceneBlendOperation mAlphaBlendOperation = SceneBlendOperation.SBO_ADD;

    // Determines if we should use separate blending operations for color and alpha channels
    protected boolean mSeparateBlendOperation;

    // Depth buffer settings
    protected boolean mDepthCheck = true;
    protected boolean mDepthWrite = true;
    protected CompareFunction mDepthFunc = CompareFunction.CMPF_LESS_EQUAL;
    protected float mDepthBiasConstant;
    protected float mDepthBiasSlopeScale;
    protected float mDepthBiasPerIteration;

    // Colour buffer settings
    protected boolean mColourWrite = true;

    // Alpha reject settings
    protected CompareFunction mAlphaRejectFunc = CompareFunction.CMPF_ALWAYS_PASS;
    protected byte mAlphaRejectVal;
    protected boolean mAlphaToCoverageEnabled;

    // Transparent depth sorting
    protected boolean mTransparentSorting = true;
    // Transparent depth sorting forced
    protected boolean mTransparentSortingForced;

    // Culling mode
    protected CullingMode mCullMode = CullingMode.CULL_CLOCKWISE;
    protected ManualCullingMode mManualCullMode = ManualCullingMode.MANUAL_CULL_BACK;

    /// Lighting enabled?
    protected boolean mLightingEnabled = true;
    /// Max simultaneous lights
    protected short mMaxSimultaneousLights = ENG_Config.MAX_SIMULTANEOUS_LIGHTS;
    /// Starting light index
    protected short mStartLight;
    /// Run this pass once per light?
    protected boolean mIteratePerLight;
    /// Iterate per how many lights?
    protected short mLightsPerIteration = 1;
    // Should it only be run for a certain light type?
    protected boolean mRunOnlyForOneLightType;
    protected LightTypes mOnlyLightType = LightTypes.LT_POINT;

    /// Shading options
    protected ShadeOptions mShadeOptions = ShadeOptions.SO_GOURAUD;
    /// Polygon mode
    protected PolygonMode mPolygonMode = PolygonMode.PM_SOLID;
    /// Normalisation
    protected boolean mNormaliseNormals;
    protected boolean mPolygonModeOverrideable = true;

    protected ENG_ColorValue mAmbient = new ENG_ColorValue(ENG_ColorValue.WHITE);
    protected ENG_ColorValue mDiffuse = new ENG_ColorValue(ENG_ColorValue.WHITE);
    protected ENG_ColorValue mSpecular = new ENG_ColorValue(ENG_ColorValue.BLACK);
    protected ENG_ColorValue mEmissive = new ENG_ColorValue(ENG_ColorValue.BLACK);
    protected float mShininess;
    protected int mTracking = TrackVertexColourEnum.TVC_NONE.getColourEnum();

    // Fog
    protected boolean mFogOverride;
    protected FogMode mFogMode = FogMode.FOG_NONE;
    protected ENG_ColorValue mFogColour = new ENG_ColorValue(ENG_ColorValue.WHITE);
    protected float mFogStart;
    protected float mFogEnd = 1.0f;
    protected float mFogDensity = 0.001f;

    //   protected ArrayList<ENG_TextureUnitState> mTextureUnitStates =
    //   	new ArrayList<ENG_TextureUnitState>();

    // Vertex program details
    protected ENG_GpuProgramUsage mVertexProgramUsage;
    // Vertex program details
    protected ENG_GpuProgramUsage mShadowCasterVertexProgramUsage;
    protected ENG_GpuProgramUsage mShadowCasterFragmentProgramUsage;
    // Vertex program details
    protected ENG_GpuProgramUsage mShadowReceiverVertexProgramUsage;
    // Fragment program details
    protected ENG_GpuProgramUsage mFragmentProgramUsage;
    // Fragment program details
    protected ENG_GpuProgramUsage mShadowReceiverFragmentProgramUsage;
    // Geometry program details
    protected ENG_GpuProgramUsage mGeometryProgramUsage;
    // Is this pass queued for deletion?
    protected boolean mQueuedForDeletion;
    // number of pass iterations to perform
    protected int mPassIterationCount = 1;

    // point size, applies when not using per-vertex point size
    protected float mPointSize = 1.0f;
    protected float mPointMinSize;
    protected float mPointMaxSize;
    protected boolean mPointSpritesEnabled;
    protected boolean mPointAttenuationEnabled;
    // constant, linear, quadratic coeffs
    protected final float[] mPointAttenuationCoeffs = new float[3];
    // TU Content type lookups
    protected ArrayList<ENG_Short> mShadowContentTypeLookup =
            new ArrayList<>();
    protected boolean mContentTypeLookupBuilt;

    /// Scissoring for the light?
    protected boolean mLightScissoring;
    /// User clip planes for light?
    protected boolean mLightClipPlanes;
    /// Illumination stage?

    protected IlluminationStage mIlluminationStage = IlluminationStage.IS_UNKNOWN;

    protected final ArrayList<ENG_TextureUnitState> mTextureUnitStates =
            new ArrayList<>();


    public enum BuiltinHashFunction {
        /**
         * Try to minimise the number of texture changes.
         */
        MIN_TEXTURE_CHANGE,
        /**
         * Try to minimise the number of GPU program changes.
         *
         * @note Only really useful if you use GPU programs for all of your
         * materials.
         */
        MIN_GPU_PROGRAM_CHANGE
    }

    public static void setHashFunction(BuiltinHashFunction builtin) {
        switch (builtin) {
            case MIN_TEXTURE_CHANGE:
                msHashFunc =
                        ENG_MinTextureStateChangeHashFunc.sMinTextureStateChangeHashFunc;
                break;
            case MIN_GPU_PROGRAM_CHANGE:
                msHashFunc = ENG_MinGpuProgramChangeHashFunc.sMinGpuProgramChangeHashFunc;
                break;
        }
    }

    public static void setHashFunction(ENG_HashFunc hashFunc) {
        msHashFunc = hashFunc;
    }

    public static ENG_HashFunc getHashFunction() {
        return msHashFunc;
    }

    public static HashSet<ENG_Pass> getDirtyHashList() {
        return msDirtyHash;
    }

    public static HashSet<ENG_Pass> getPassGraveyard() {
        return msPassGraveyard;
    }

    public static ENG_HashFunc getBuiltinHashFunction(
            BuiltinHashFunction builtin) {
        ENG_HashFunc hashFunc = null;

        switch (builtin) {
            case MIN_TEXTURE_CHANGE:
                hashFunc = ENG_MinTextureStateChangeHashFunc.sMinTextureStateChangeHashFunc;
                break;
            case MIN_GPU_PROGRAM_CHANGE:
                hashFunc = ENG_MinGpuProgramChangeHashFunc.sMinGpuProgramChangeHashFunc;
                break;
        }

        return hashFunc;
    }

    public void queueForDeletion() {
        mQueuedForDeletion = true;

        removeAllTextureUnitStates();
        if (mVertexProgramUsage != null) {
            mVertexProgramUsage.destroy();
            mVertexProgramUsage = null;
        }
        if (mShadowCasterVertexProgramUsage != null) {
            mShadowCasterVertexProgramUsage.destroy();
            mShadowCasterVertexProgramUsage = null;
        }
        if (mShadowCasterFragmentProgramUsage != null) {
            mShadowCasterFragmentProgramUsage.destroy();
            mShadowCasterFragmentProgramUsage = null;
        }
        if (mShadowReceiverVertexProgramUsage != null) {
            mShadowReceiverVertexProgramUsage.destroy();
            mShadowReceiverVertexProgramUsage = null;
        }
        if (mShadowReceiverFragmentProgramUsage != null) {
            mShadowReceiverFragmentProgramUsage.destroy();
            mShadowReceiverFragmentProgramUsage = null;
        }
        if (mGeometryProgramUsage != null) {
            mGeometryProgramUsage.destroy();
            mGeometryProgramUsage = null;
        }
        if (mFragmentProgramUsage != null) {
            mFragmentProgramUsage.destroy();
            mFragmentProgramUsage = null;
        }

//        msDirtyHashListMutex.lock();
//        try {
            msDirtyHash.remove(this);
//        } finally {
//            msDirtyHashListMutex.unlock();
//        }

//        msPassGraveyardMutex.lock();
//        try {
            msPassGraveyard.add(this);
//        } finally {
//            msPassGraveyardMutex.unlock();
//        }
    }

    public void _recalculateHash() {
        mHash = msHashFunc.hash(this);
    }

    public void setTextureFiltering(TextureFilterOptions filterType) {
//        mTexUnitChangeMutex.lock();
//        try {
            int len = mTextureUnitStates.size();
            for (int i = 0; i < len; ++i) {
                mTextureUnitStates.get(i).setTextureFiltering(filterType);
            }
//        } finally {
//            mTexUnitChangeMutex.unlock();
//        }
    }

    public void setTextureAnisotropy(int maxAniso) {
//        mTexUnitChangeMutex.lock();
//        try {
            int len = mTextureUnitStates.size();
            for (int i = 0; i < len; ++i) {
                mTextureUnitStates.get(i).setTextureAnisotropy(maxAniso);
            }
//        } finally {
//            mTexUnitChangeMutex.unlock();
//        }
    }

    public void _updateAutoParams(ENG_AutoParamDataSource source, short mask) {
        if (hasVertexProgram()) {
            mVertexProgramUsage.getParameters()._updateAutoParams(source, mask);
        }
        if (hasGeometryProgram()) {
            mGeometryProgramUsage.getParameters()._updateAutoParams(source, mask);
        }
        if (hasFragmentProgram()) {
            mFragmentProgramUsage.getParameters()._updateAutoParams(source, mask);
        }
    }

    public static void processPendingPassUpdates() {
//        msPassGraveyardMutex.lock();
//        try {
            for (ENG_Pass pass : msPassGraveyard) {
                pass.destroy();
            }
            msPassGraveyard.clear();
//        } finally {
//            msPassGraveyardMutex.unlock();
//        }
        //        msDirtyHashListMutex.lock();
//        try {
        HashSet<ENG_Pass> tempHashList = new HashSet<>(msDirtyHash);
//        } finally {
//            msDirtyHashListMutex.unlock();
//        }
        for (ENG_Pass pass : tempHashList) {
            pass._recalculateHash();
        }
    }

//	public void destroy() {
//		
//	}


    public ENG_Pass(ENG_Technique parent, ENG_Short index) {
        mParent = parent;
        mIndex.setValue(index);

        mPointAttenuationCoeffs[0] = 1.0f;
        mPointAttenuationCoeffs[1] = mPointAttenuationCoeffs[2] = 0.0f;

        mName = String.valueOf(index);

        _recalculateHash();

        generateId();
    }

    public ENG_Pass(ENG_Technique parent, short index) {
        mParent = parent;
        mIndex.setValue(index);

        mPointAttenuationCoeffs[0] = 1.0f;
        mPointAttenuationCoeffs[1] = mPointAttenuationCoeffs[2] = 0.0f;

        mName = String.valueOf(index);

        _recalculateHash();

        generateId();
    }

    public ENG_Pass(ENG_Technique parent, ENG_Short index, ENG_Pass oth) {
        set(oth);
        mParent = parent;
        mIndex.setValue(index);
        mQueuedForDeletion = false;

        // init the hash inline
        _recalculateHash();

        generateId();
    }

    public ENG_Pass(ENG_Technique parent, short index, ENG_Pass oth) {
        set(oth);
        mParent = parent;
        mIndex.setValue(index);
        mQueuedForDeletion = false;

        // init the hash inline
        _recalculateHash();

        generateId();
    }

    private void generateId() {
//        idIncrementLock.lock();
//        try {
            boolean found;
            do {
                found = ids.contains(++nextId);
            } while (found);
            id = nextId;
            ids.add(id);
//        } finally {
//            idIncrementLock.unlock();
//        }
    }

    public void destroy() {
        if (mVertexProgramUsage != null) {
            mVertexProgramUsage.destroy();
        }
        if (mFragmentProgramUsage != null) {
            mFragmentProgramUsage.destroy();
        }
        if (mShadowCasterVertexProgramUsage != null) {
            mShadowCasterVertexProgramUsage.destroy();
        }
        if (mShadowCasterFragmentProgramUsage != null) {
            mShadowCasterFragmentProgramUsage.destroy();
        }
        if (mShadowReceiverVertexProgramUsage != null) {
            mShadowReceiverVertexProgramUsage.destroy();
        }
        if (mShadowReceiverFragmentProgramUsage != null) {
            mShadowReceiverFragmentProgramUsage.destroy();
        }

//        idIncrementLock.lock();
//        try {
            ids.remove(id);
//        } finally {
//            idIncrementLock.unlock();
//        }
    }

    public void set(ENG_Pass oth) {
        mName = oth.mName;
        mHash = oth.mHash;
        mAmbient = oth.mAmbient;
        mDiffuse = oth.mDiffuse;
        mSpecular = oth.mSpecular;
        mEmissive = oth.mEmissive;
        mShininess = oth.mShininess;
        mTracking = oth.mTracking;

        // Copy fog parameters
        mFogOverride = oth.mFogOverride;
        mFogMode = oth.mFogMode;
        mFogColour = oth.mFogColour;
        mFogStart = oth.mFogStart;
        mFogEnd = oth.mFogEnd;
        mFogDensity = oth.mFogDensity;

        // Default blending (overwrite)
        mSourceBlendFactor = oth.mSourceBlendFactor;
        mDestBlendFactor = oth.mDestBlendFactor;
        mSourceBlendFactorAlpha = oth.mSourceBlendFactorAlpha;
        mDestBlendFactorAlpha = oth.mDestBlendFactorAlpha;
        mSeparateBlend = oth.mSeparateBlend;

        mBlendOperation = oth.mBlendOperation;
        mAlphaBlendOperation = oth.mAlphaBlendOperation;
        mSeparateBlendOperation = oth.mSeparateBlendOperation;

        mDepthCheck = oth.mDepthCheck;
        mDepthWrite = oth.mDepthWrite;
        mAlphaRejectFunc = oth.mAlphaRejectFunc;
        mAlphaRejectVal = oth.mAlphaRejectVal;
        mAlphaToCoverageEnabled = oth.mAlphaToCoverageEnabled;
        mTransparentSorting = oth.mTransparentSorting;
        mTransparentSortingForced = oth.mTransparentSortingForced;
        mColourWrite = oth.mColourWrite;
        mDepthFunc = oth.mDepthFunc;
        mDepthBiasConstant = oth.mDepthBiasConstant;
        mDepthBiasSlopeScale = oth.mDepthBiasSlopeScale;
        mDepthBiasPerIteration = oth.mDepthBiasPerIteration;
        mCullMode = oth.mCullMode;
        mManualCullMode = oth.mManualCullMode;
        mLightingEnabled = oth.mLightingEnabled;
        mMaxSimultaneousLights = oth.mMaxSimultaneousLights;
        mStartLight = oth.mStartLight;
        mIteratePerLight = oth.mIteratePerLight;
        mLightsPerIteration = oth.mLightsPerIteration;
        mRunOnlyForOneLightType = oth.mRunOnlyForOneLightType;
        mNormaliseNormals = oth.mNormaliseNormals;
        mOnlyLightType = oth.mOnlyLightType;
        mShadeOptions = oth.mShadeOptions;
        mPolygonMode = oth.mPolygonMode;
        mPolygonModeOverrideable = oth.mPolygonModeOverrideable;
        mPassIterationCount = oth.mPassIterationCount;
        mPointSize = oth.mPointSize;
        mPointMinSize = oth.mPointMinSize;
        mPointMaxSize = oth.mPointMaxSize;
        mPointSpritesEnabled = oth.mPointSpritesEnabled;
        mPointAttenuationEnabled = oth.mPointAttenuationEnabled;
        //memcpy(mPointAttenuationCoeffs, oth.mPointAttenuationCoeffs, sizeof(Real)*3);
        System.arraycopy(mPointAttenuationCoeffs, 0, oth.mPointAttenuationCoeffs, 0, 3);
        mShadowContentTypeLookup = oth.mShadowContentTypeLookup;
        mContentTypeLookupBuilt = oth.mContentTypeLookupBuilt;
        mLightScissoring = oth.mLightScissoring;
        mLightClipPlanes = oth.mLightClipPlanes;
        mIlluminationStage = oth.mIlluminationStage;

        if (oth.mVertexProgramUsage != null) {
            mVertexProgramUsage =
                    new ENG_GpuProgramUsage(oth.mVertexProgramUsage, this);
        }
        if (oth.mGeometryProgramUsage != null) {
            mGeometryProgramUsage =
                    new ENG_GpuProgramUsage(oth.mGeometryProgramUsage, this);
        }
        if (oth.mFragmentProgramUsage != null) {
            mFragmentProgramUsage =
                    new ENG_GpuProgramUsage(oth.mFragmentProgramUsage, this);
        }
        if (oth.mShadowCasterVertexProgramUsage != null) {
            mShadowCasterVertexProgramUsage =
                    new ENG_GpuProgramUsage(oth.mShadowCasterVertexProgramUsage, this);
        }
        if (oth.mShadowReceiverFragmentProgramUsage != null) {
            mShadowReceiverFragmentProgramUsage =
                    new ENG_GpuProgramUsage(
                            oth.mShadowReceiverFragmentProgramUsage, this);
        }
        if (oth.mShadowReceiverVertexProgramUsage != null) {
            mShadowReceiverVertexProgramUsage =
                    new ENG_GpuProgramUsage(
                            oth.mShadowReceiverVertexProgramUsage, this);
        }

        mTextureUnitStates.clear();

        for (ENG_TextureUnitState tex : oth.mTextureUnitStates) {
            ENG_TextureUnitState t = new ENG_TextureUnitState(this, tex);
            mTextureUnitStates.add(t);
        }

        _dirtyHash();
    }

    public short getNumTextureUnitStates() {
        return (short) mTextureUnitStates.size();
    }

    public ENG_TextureUnitState getTextureUnitState(short index) {
//        mTexUnitChangeMutex.lock();
//        try {
            if (index >= mTextureUnitStates.size()) {
                throw new IllegalArgumentException("Index out of bounds");
            }
            return mTextureUnitStates.get(index);
//        } finally {
//            mTexUnitChangeMutex.unlock();
//        }
    }

    public ENG_Short getIndex() {
        return mIndex;
    }

    public boolean isProgrammable() {
        return true;
    }

    public boolean hasVertexProgram() {
        return mVertexProgramUsage != null;
    }

    public boolean hasFragmentProgram() {
        return mFragmentProgramUsage != null;
    }

    public boolean hasGeometryProgram() {
        return mGeometryProgramUsage != null;
    }

    public boolean hasShadowCasterVertexProgram() {
        return mShadowCasterVertexProgramUsage != null;
    }

    public boolean hasShadowReceiverVertexProgram() {
        return mShadowReceiverVertexProgramUsage != null;
    }

    public boolean hasShadowReceiverFragmentProgram() {
        return mShadowReceiverFragmentProgramUsage != null;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setVertexColourTracking(int tracking) {
        mTracking = tracking;
    }

    public int getVertexColourTracking() {
        return mTracking;
    }

    public float getPointSize() {
        return mPointSize;
    }

    public void setPointSize(float ps) {
        mPointSize = ps;
    }

    public void setPointSpritesEnabled(boolean enabled) {
        mPointSpritesEnabled = enabled;
    }

    public boolean getPointSpritesEnabled() {
        return mPointSpritesEnabled;
    }

    public void setPointAttenuation(boolean enabled) {
        setPointAttenuation(enabled, 0.0f, 1.0f, 0.0f);
    }

    public void setPointAttenuation(boolean enabled, float constant, float linear,
                                    float quadratic) {
        mPointAttenuationEnabled = enabled;
        mPointAttenuationCoeffs[0] = constant;
        mPointAttenuationCoeffs[1] = linear;
        mPointAttenuationCoeffs[2] = quadratic;
    }

    public boolean isPointAttenuationEnabled() {
        return mPointAttenuationEnabled;
    }

    public float getPointAttenuationConstant() {
        return mPointAttenuationCoeffs[0];
    }

    public float getPointAttenuationLinear() {
        return mPointAttenuationCoeffs[1];
    }

    public float getPointAttenuationQuadratic() {
        return mPointAttenuationCoeffs[2];
    }

    public void setPointMinSize(float sz) {
        mPointMinSize = sz;
    }

    public float getPointMinSize() {
        return mPointMinSize;
    }

    public void setPointMaxSize(float sz) {
        mPointMaxSize = sz;
    }

    public float getPointMaxSize() {
        return mPointMaxSize;
    }

    public void addTextureUnitState(ENG_TextureUnitState state) {
//        mTexUnitChangeMutex.lock();
//        try {
            if (state == null) {
                throw new NullPointerException("state must not be null!");
            }

            if ((state.getParent() == null) || (state.getParent() == this)) {
                mTextureUnitStates.add(state);

                state._notifyParent(this);

                if ((state.getName() == null) || (state.getName().isEmpty())) {
                    state.setName(String.valueOf(mTextureUnitStates.size() - 1));
                    state.setTextureNameAlias("");
                }
                mParent._notifyNeedsRecompile();
                _dirtyHash();
            } else {
                throw new IllegalArgumentException(
                        "TextureUnitState already attached to another pass");
            }
            mContentTypeLookupBuilt = false;
//        } finally {
//            mTexUnitChangeMutex.unlock();
//        }
    }

    public ENG_TextureUnitState getTextureUnitState(int index) {
//        mTexUnitChangeMutex.lock();
//        try {
            if (index >= mTextureUnitStates.size()) {
                throw new IllegalArgumentException("Index out of bounds");
            }
            return mTextureUnitStates.get(index);
//        } finally {
//            mTexUnitChangeMutex.unlock();
//        }
    }

    public ENG_TextureUnitState getTextureUnitState(String name) {
//        mTexUnitChangeMutex.lock();
//        try {
            int len = mTextureUnitStates.size();
            for (int i = 0; i < len; ++i) {
                if (mTextureUnitStates.get(i).getName().equals(name)) {
                    return mTextureUnitStates.get(i);
                }
            }
            return null;
//        } finally {
//            mTexUnitChangeMutex.unlock();
//        }
    }

    public short getTextureUnitStateIndex(ENG_TextureUnitState state) {
//        mTexUnitChangeMutex.lock();
//        try {
            if (state.getParent() == this) {
                return (short) mTextureUnitStates.indexOf(state);
            } else {
                throw new IllegalArgumentException(
                        "TextureUnitState is not attached to this pass");
            }
//        } finally {
//            mTexUnitChangeMutex.unlock();
//        }
    }

    public Iterator<ENG_TextureUnitState> getTextureUnitStateIterator() {
        return mTextureUnitStates.iterator();
    }

    public void removeTextureUnitState(short index) {
//        mTexUnitChangeMutex.lock();
//        try {
            if (index >= mTextureUnitStates.size()) {
                throw new IllegalArgumentException("Index out of bounds");
            }
            mTextureUnitStates.remove(index);
            if (!mQueuedForDeletion) {
                // Needs recompilation
                mParent._notifyNeedsRecompile();
            }
            _dirtyHash();
            mContentTypeLookupBuilt = false;
//        } finally {
//            mTexUnitChangeMutex.unlock();
//        }
    }

    public void removeAllTextureUnitStates() {
//        mTexUnitChangeMutex.lock();
//        try {
            mTextureUnitStates.clear();
            if (!mQueuedForDeletion) {
                // Needs recompilation
                mParent._notifyNeedsRecompile();
            }
            _dirtyHash();
            mContentTypeLookupBuilt = false;
//        } finally {
//            mTexUnitChangeMutex.unlock();
//        }
    }

    public ENG_SceneBlendFactorSrcDest _getBlendFlags(SceneBlendType type) {
        ENG_SceneBlendFactorSrcDest srcDst = new ENG_SceneBlendFactorSrcDest();
        _getBlendFlags(type, srcDst);
        return srcDst;
    }

    public void _getBlendFlags(SceneBlendType type,
                               ENG_SceneBlendFactorSrcDest srcDst) {
        switch (type) {
            case SBT_TRANSPARENT_ALPHA:
                srcDst.source = SceneBlendFactor.SBF_SOURCE_ALPHA;
                srcDst.dest = SceneBlendFactor.SBF_ONE_MINUS_SOURCE_ALPHA;
                return;
            case SBT_TRANSPARENT_COLOUR:
                srcDst.source = SceneBlendFactor.SBF_SOURCE_COLOUR;
                srcDst.dest = SceneBlendFactor.SBF_ONE_MINUS_SOURCE_COLOUR;
                return;
            case SBT_MODULATE:
                srcDst.source = SceneBlendFactor.SBF_DEST_COLOUR;
                srcDst.dest = SceneBlendFactor.SBF_ZERO;
                return;
            case SBT_ADD:
                srcDst.source = SceneBlendFactor.SBF_ONE;
                srcDst.dest = SceneBlendFactor.SBF_ONE;
                return;
            case SBT_REPLACE:
                srcDst.source = SceneBlendFactor.SBF_ONE;
                srcDst.dest = SceneBlendFactor.SBF_ZERO;
                return;
        }

        // Default to SBT_REPLACE

        srcDst.source = SceneBlendFactor.SBF_ONE;
        srcDst.dest = SceneBlendFactor.SBF_ZERO;
    }

    public void setSceneBlending(SceneBlendType type) {
        ENG_SceneBlendFactorSrcDest srcDst = new ENG_SceneBlendFactorSrcDest();
        _getBlendFlags(type, srcDst);
        setSceneBlending(srcDst);
    }

    public void setSeparateSceneBlending(SceneBlendType sbt, SceneBlendType sbta) {
        ENG_SceneBlendFactorSrcDest srcDst = new ENG_SceneBlendFactorSrcDest();
        ENG_SceneBlendFactorSrcDest srcDstAlpha = new ENG_SceneBlendFactorSrcDest();

        _getBlendFlags(sbt, srcDst);
        _getBlendFlags(sbta, srcDstAlpha);

        setSeparateSceneBlending(srcDst.source, srcDst.dest,
                srcDstAlpha.source, srcDstAlpha.dest);
    }

    public void setSceneBlending(ENG_SceneBlendFactorSrcDest srcDst) {
        setSceneBlending(srcDst.source, srcDst.dest);
    }

    public void setSceneBlending(SceneBlendFactor sourceFactor,
                                 SceneBlendFactor destFactor) {
        mSourceBlendFactor = sourceFactor;
        mDestBlendFactor = destFactor;

        mSeparateBlend = false;
    }

    public void setSeparateSceneBlending(SceneBlendFactor sourceFactor, SceneBlendFactor destFactor,
                                         SceneBlendFactor sourceFactorAlpha, SceneBlendFactor destFactorAlpha) {
        mSourceBlendFactor = sourceFactor;
        mDestBlendFactor = destFactor;
        mSourceBlendFactorAlpha = sourceFactorAlpha;
        mDestBlendFactorAlpha = destFactorAlpha;

        mSeparateBlend = true;
    }

    public SceneBlendFactor getSourceBlendFactor() {
        return mSourceBlendFactor;
    }

    public SceneBlendFactor getDestBlendFactor() {
        return mDestBlendFactor;
    }

    public SceneBlendFactor getSourceBlendFactorAlpha() {
        return mSourceBlendFactorAlpha;
    }

    public SceneBlendFactor getDestBlendFactorAlpha() {
        return mDestBlendFactorAlpha;
    }

    public boolean hasSeparateSceneBlending() {
        return mSeparateBlend;
    }

    public void setSceneBlendingOperation(SceneBlendOperation op) {
        mBlendOperation = op;
        mSeparateBlendOperation = false;
    }

    public void setSeparateSceneBlendingOperation(SceneBlendOperation op,
                                                  SceneBlendOperation alphaOp) {
        mBlendOperation = op;
        mAlphaBlendOperation = alphaOp;
        mSeparateBlendOperation = true;
    }

    public SceneBlendOperation getSceneBlendingOperation() {
        return mBlendOperation;
    }

    public SceneBlendOperation getSceneBlendingOperationAlpha() {
        return mAlphaBlendOperation;
    }

    public boolean hasSeparateSceneBlendingOperations() {
        return mSeparateBlendOperation;
    }

    public boolean isTransparent() {
        // Transparent if any of the destination colour is taken into account
        return !(mDestBlendFactor == SceneBlendFactor.SBF_ZERO &&
                mSourceBlendFactor != SceneBlendFactor.SBF_DEST_COLOUR &&
                mSourceBlendFactor != SceneBlendFactor.SBF_ONE_MINUS_DEST_COLOUR &&
                mSourceBlendFactor != SceneBlendFactor.SBF_DEST_ALPHA &&
                mSourceBlendFactor != SceneBlendFactor.SBF_ONE_MINUS_DEST_ALPHA);
    }

    public void setDepthCheckEnabled(boolean enabled) {
        mDepthCheck = enabled;
    }

    public boolean getDepthCheckEnabled() {
        return mDepthCheck;
    }

    public void setDepthWriteEnabled(boolean enabled) {
        mDepthWrite = enabled;
    }

    public boolean getDepthWriteEnabled() {
        return mDepthWrite;
    }

    public void setDepthFunction(CompareFunction func) {
        mDepthFunc = func;
    }

    public CompareFunction getDepthFunction() {
        return mDepthFunc;
    }

    public void setAlphaRejectSettings(CompareFunction func,
                                       byte value, boolean alphaToCoverage) {
        mAlphaRejectFunc = func;
        mAlphaRejectVal = value;
        mAlphaToCoverageEnabled = alphaToCoverage;
    }

    public CompareFunction getAlphaRejectFunction() {
        return mAlphaRejectFunc;
    }

    public void setAlphaRejectFunction(CompareFunction func) {
        mAlphaRejectFunc = func;
    }

    public byte getAlphaRejectValue() {
        return mAlphaRejectVal;
    }

    public void setAlphaRejectValue(byte value) {
        mAlphaRejectVal = value;
    }

    public boolean isAlphaToCoverageEnabled() {
        return mAlphaToCoverageEnabled;
    }

    public void setAlphaToCoverageEnabled(boolean enabled) {
        mAlphaToCoverageEnabled = enabled;
    }

    public void setTransparentSortingEnabled(boolean enabled) {
        mTransparentSorting = enabled;
    }

    public boolean getTransparentSortingEnabled() {
        return mTransparentSorting;
    }

    public void setTransparentSortingForced(boolean enabled) {
        mTransparentSortingForced = enabled;
    }

    public boolean getTransparentSortingEnabledForced() {
        return mTransparentSortingForced;
    }

    public void setColourWriteEnabled(boolean enabled) {
        mColourWrite = enabled;
    }

    public boolean getColourWriteEnabled() {
        return mColourWrite;
    }

    public void setCullingMode(CullingMode mode) {
        mCullMode = mode;
    }

    public CullingMode getCullingMode() {
        return mCullMode;
    }

    public void setManualCullingMode(ManualCullingMode mode) {
        mManualCullMode = mode;
    }

    public ManualCullingMode getManualCullingMode() {
        return mManualCullMode;
    }

    public void setLightingEnabled(boolean enabled) {
        mLightingEnabled = enabled;
    }

    public boolean getLightingEnabled() {
        return mLightingEnabled;
    }

    public void setMaxSimultaneousLights(short maxLights) {
        mMaxSimultaneousLights = maxLights;
    }

    public short getMaxSimultaneousLights() {
        return mMaxSimultaneousLights;
    }

    public void setStartLight(short startLight) {
        mStartLight = startLight;
    }

    public short getStartLight() {
        return mStartLight;
    }

    public void setLightCountPerIteration(short c) {
        mLightsPerIteration = c;
    }

    public short getLightCountPerIteration() {
        return mLightsPerIteration;
    }

    public void setIteratePerLight(boolean enabled, boolean onlyForOneLightType,
                                   LightTypes lightType) {
        mIteratePerLight = enabled;
        mRunOnlyForOneLightType = onlyForOneLightType;
        mOnlyLightType = lightType;
    }

    public void setShadingMode(ShadeOptions mode) {
        mShadeOptions = mode;
    }

    public ShadeOptions getShadingMode() {
        return mShadeOptions;
    }

    public void setPolygonMode(PolygonMode mode) {
        mPolygonMode = mode;
    }

    public PolygonMode getPolygonMode() {
        return mPolygonMode;
    }

    public void setFog(boolean overrideScene, FogMode mode,
                       ENG_ColorValue colour, float density,
                       float start, float end) {
        mFogOverride = overrideScene;
        if (overrideScene) {
            mFogMode = mode;
            mFogColour = colour;
            mFogStart = start;
            mFogEnd = end;
            mFogDensity = density;
        }
    }

    public boolean getFogOverride() {
        return mFogOverride;
    }

    public FogMode getFogMode() {
        return mFogMode;
    }

    public ENG_ColorValue getFogColour() {
        return mFogColour;
    }

    public float getFogStart() {
        return mFogStart;
    }

    public float getFogEnd() {
        return mFogEnd;
    }

    public float getFogDensity() {
        return mFogDensity;
    }

    public void setDepthBias(float constantBias, float slopeScaleBias) {
        mDepthBiasConstant = constantBias;
        mDepthBiasSlopeScale = slopeScaleBias;
    }

    public float getDepthBiasConstant() {
        return mDepthBiasConstant;
    }

    public float getDepthBiasSlopeScale() {
        return mDepthBiasSlopeScale;
    }

    public void setIterationDepthBias(float biasPerIteration) {
        mDepthBiasPerIteration = biasPerIteration;
    }

    public float getIterationDepthBias() {
        return mDepthBiasPerIteration;
    }

    public void _notifyIndex(short index) {
        if (mIndex.compareTo(index) != 0) {
            mIndex.setValue(index);
            _dirtyHash();
        }
    }

    public void setVertexProgram(String name) {
        setVertexProgram(name, true);
    }

    public void setVertexProgram(String name, boolean resetParams) {
//        mGpuProgramChangeMutex.lock();
//        try {
            if (!getVertexProgramName().equals(name)) {
                if (name.isEmpty()) {
                    mVertexProgramUsage = null;
                } else {
                    if (mVertexProgramUsage == null) {
                        mVertexProgramUsage = new ENG_GpuProgramUsage(
                                GpuProgramType.GPT_VERTEX_PROGRAM, this);
                    }
                    mVertexProgramUsage.setProgramName(name, resetParams);
                }
                mParent._notifyNeedsRecompile();
                if (getHashFunction() ==
                        getBuiltinHashFunction(
                                BuiltinHashFunction.MIN_GPU_PROGRAM_CHANGE)) {
                    _dirtyHash();
                }
            }
//        } finally {
//            mGpuProgramChangeMutex.unlock();
//        }
    }

    public void setFragmentProgram(String name) {
        setFragmentProgram(name, true);
    }

    public void setFragmentProgram(String name, boolean resetParams) {
//        mGpuProgramChangeMutex.lock();
//        try {
            if (!getFragmentProgramName().equals(name)) {
                if (name.isEmpty()) {
                    mFragmentProgramUsage = null;
                } else {
                    if (mFragmentProgramUsage == null) {
                        mFragmentProgramUsage = new ENG_GpuProgramUsage(
                                GpuProgramType.GPT_FRAGMENT_PROGRAM, this);
                    }
                    mFragmentProgramUsage.setProgramName(name, resetParams);
                }
                mParent._notifyNeedsRecompile();
                if (getHashFunction() ==
                        getBuiltinHashFunction(
                                BuiltinHashFunction.MIN_GPU_PROGRAM_CHANGE)) {
                    _dirtyHash();
                }
            }
//        } finally {
//            mGpuProgramChangeMutex.unlock();
//        }
    }

    public void setGeometryProgram(String name, boolean resetParams) {
//        mGpuProgramChangeMutex.lock();
//        try {
            if (!getGeometryProgramName().equals(name)) {
                if (name.isEmpty()) {
                    mGeometryProgramUsage = null;
                } else {
                    if (mGeometryProgramUsage == null) {
                        mGeometryProgramUsage = new ENG_GpuProgramUsage(
                                GpuProgramType.GPT_GEOMETRY_PROGRAM, this);
                    }
                    mGeometryProgramUsage.setProgramName(name, resetParams);
                }
                mParent._notifyNeedsRecompile();
                if (getHashFunction() ==
                        getBuiltinHashFunction(
                                BuiltinHashFunction.MIN_GPU_PROGRAM_CHANGE)) {
                    _dirtyHash();
                }
            }
//        } finally {
//            mGpuProgramChangeMutex.unlock();
//        }
    }

    public String getVertexProgramName() {
//        mGpuProgramChangeMutex.lock();
//        try {
            if (mVertexProgramUsage == null) {
                return "";
            }
            return mVertexProgramUsage.getProgramName();
//        } finally {
//            mGpuProgramChangeMutex.unlock();
//        }
    }

    public ENG_GpuProgramParameters getVertexProgramParameters() {
//        mGpuProgramChangeMutex.lock();
//        try {
            if (mVertexProgramUsage == null) {
                throw new NullPointerException(
                        "This pass does not have a vertex program assigned!");
            }
            return mVertexProgramUsage.getParameters();
//        } finally {
//            mGpuProgramChangeMutex.unlock();
//        }
    }

    public ENG_GpuProgram getVertexProgram() {
//        mGpuProgramChangeMutex.lock();
//        try {
            return mVertexProgramUsage.getProgram();
//        } finally {
//            mGpuProgramChangeMutex.unlock();
//        }
    }

    public String getFragmentProgramName() {
//        mGpuProgramChangeMutex.lock();
//        try {
            if (mFragmentProgramUsage == null) {
                return "";
            }
            return mFragmentProgramUsage.getProgramName();
//        } finally {
//            mGpuProgramChangeMutex.unlock();
//        }
    }

    public ENG_GpuProgramParameters getFragmentProgramParameters() {
//        mGpuProgramChangeMutex.lock();
//        try {
            if (mFragmentProgramUsage == null) {
                throw new NullPointerException(
                        "This pass does not have a vertex program assigned!");
            }
            return mFragmentProgramUsage.getParameters();
//        } finally {
//            mGpuProgramChangeMutex.unlock();
//        }
    }

    public ENG_GpuProgram getFragmentProgram() {
//        mGpuProgramChangeMutex.lock();
//        try {
            return mFragmentProgramUsage.getProgram();
//        } finally {
//            mGpuProgramChangeMutex.unlock();
//        }
    }

    public String getGeometryProgramName() {
//        mGpuProgramChangeMutex.lock();
//        try {
            if (mGeometryProgramUsage == null) {
                return "";
            }
            return mGeometryProgramUsage.getProgramName();
//        } finally {
//            mGpuProgramChangeMutex.unlock();
//        }
    }

    public ENG_GpuProgramParameters getGeometryProgramParameters() {
//        mGpuProgramChangeMutex.lock();
//        try {
            if (mGeometryProgramUsage == null) {
                throw new NullPointerException(
                        "This pass does not have a vertex program assigned!");
            }
            return mGeometryProgramUsage.getParameters();
//        } finally {
//            mGpuProgramChangeMutex.unlock();
//        }
    }

    public ENG_GpuProgram getGeometryProgram() {
//        mGpuProgramChangeMutex.lock();
//        try {
            return mGeometryProgramUsage.getProgram();
//        } finally {
//            mGpuProgramChangeMutex.unlock();
//        }
    }

    public ENG_TextureUnitState createTextureUnitState() {
        ENG_TextureUnitState t = new ENG_TextureUnitState(this);
        addTextureUnitState(t);
        mContentTypeLookupBuilt = false;
        return t;
    }

    public ENG_TextureUnitState createTextureUnitState(String textureName,
                                                       short texCoordSet) {
        ENG_TextureUnitState t = new ENG_TextureUnitState(this);
        t.setTextureName(textureName);
        t.setTextureCoordSet(texCoordSet);
        addTextureUnitState(t);
        mContentTypeLookupBuilt = false;
        return t;
    }

/*	public String getFragmentProgramName() {
		return null;
	}*/

    public void _dirtyHash() {
//		_recalculateHash();
        // We assume its loaded
//        msDirtyHashListMutex.lock();
//        try {
            msDirtyHash.add(this);
            mHashDirtyQueued = false;
//        } finally {
//            msDirtyHashListMutex.unlock();
//        }
    }

    public static void clearDirtyHashList() {
//        msDirtyHashListMutex.lock();
//        try {
            msDirtyHash.clear();
//        } finally {
//            msDirtyHashListMutex.unlock();
//        }
    }

    public int getHash() {
        return mHash;
    }

    public void setIlluminationStage(IlluminationStage is) {
        mIlluminationStage = is;
    }

    public IlluminationStage getIlluminationStage() {
        return mIlluminationStage;
    }

    public ENG_ColorValue getAmbient() {
        return mAmbient;
    }

    public ENG_ColorValue getDiffuse() {
        return mDiffuse;
    }

    public ENG_ColorValue getSpecular() {
        return mSpecular;
    }

    public ENG_ColorValue getSelfIllumination() {
        return mEmissive;
    }

    public float getShininess() {
        return mShininess;
    }

    public void setNormaliseNormals(boolean normalise) {
        mNormaliseNormals = normalise;
    }

    public boolean getNormaliseNormals() {
        return mNormaliseNormals;
    }

    public void setPolygonModeOverrideable(boolean over) {
        mPolygonModeOverrideable = over;
    }

    public boolean getPolygonModeOverrideable() {
        return mPolygonModeOverrideable;
    }

    public boolean getIteratePerLight() {
        return mIteratePerLight;
    }

    public boolean getRunOnlyForOneLightType() {
        return mRunOnlyForOneLightType;
    }

    public LightTypes getOnlyLightType() {
        return mOnlyLightType;
    }

    public void setLightScissoringEnabled(boolean enabled) {
        mLightScissoring = enabled;
    }

    public boolean getLightScissoringEnabled() {
        return mLightScissoring;
    }

    public void setLightClipPlanesEnabled(boolean enabled) {
        mLightClipPlanes = enabled;
    }

    public boolean getLightClipPlanesEnabled() {
        return mLightClipPlanes;
    }

    public void setPassIterationCount(int count) {
        mPassIterationCount = count;
    }

    public int getPassIterationCount() {
        return mPassIterationCount;
    }

    public void _load() {
        
        for (ENG_TextureUnitState t : mTextureUnitStates) {
            t._load();
        }

        if (mVertexProgramUsage != null) {
            mVertexProgramUsage._load();
        }

        if (mGeometryProgramUsage != null) {
            mGeometryProgramUsage._load();
        }

        if (mFragmentProgramUsage != null) {
            mFragmentProgramUsage._load();
        }

        if (mHashDirtyQueued) {
            _dirtyHash();
        }
    }

    public void _prepare() {
        
        for (ENG_TextureUnitState t : mTextureUnitStates) {
            t._prepare();
        }
    }

    public void _unload() {
        
        for (ENG_TextureUnitState tex : mTextureUnitStates) {
            tex._unload();
        }
    }

    public int getId() {
        return id;
    }

//	@Override
//	public int hashCode() {
//		
////		return super.hashCode();
//		return getHash();
//	}

    // Do not override equals since we need to compare memory addresses
}
