/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.ENG_RenderSystemCapabilities.Capabilities;

public abstract class ENG_GpuProgram {

    /**
     * Enumerates the types of programs which can run on the GPU.
     */
    public enum GpuProgramType {
        GPT_VERTEX_PROGRAM,
        GPT_FRAGMENT_PROGRAM,
        GPT_GEOMETRY_PROGRAM;

        public static GpuProgramType getGpuProgramType(int i) {
            switch (i) {
                case 0:
                    return GPT_VERTEX_PROGRAM;
                case 1:
                    return GPT_FRAGMENT_PROGRAM;
                case 2:
                    return GPT_GEOMETRY_PROGRAM;
                default:
                    throw new IllegalArgumentException("GpuProgramType " + i +
                            " is not a valid value");
            }
        }
    }

    protected final String mName;

    /// The type of the program
    protected GpuProgramType mType;
    /// The name of the file to load source from (may be blank)
    protected String mFilename;
    protected String mPath;
    /// The assembler source of the program (may be blank until file loaded)
    protected String mSource;
    /// Whether we need to load source from file or not
    protected boolean mLoadFromFile;
    /// Syntax code e.g. arbvp1, vs_2_0 etc
    protected String mSyntaxCode;
    /// Does this (vertex) program include skeletal animation?
    protected boolean mSkeletalAnimation;
    /// Does this (vertex) program include morph animation?
    protected boolean mMorphAnimation;
    /// Does this (vertex) program include pose animation (count of number of poses supported)
    protected short mPoseAnimation;
    /// Does this (vertex) program require support for vertex texture fetch?
    protected boolean mVertexTextureFetch;
    /// Does this (geometry) program require adjacency information?
    protected boolean mNeedsAdjacencyInfo;
    /// The default parameters for use with this object
    protected ENG_GpuProgramParameters mDefaultParams;
    /// Did we encounter a compilation error?
    protected boolean mCompileError;
    /**
     * Parameter name -> ConstantDefinition map, shared instance used by all parameter objects.
     * This is a shared pointer because if the program is recompiled and the parameters
     * change, this definition will alter, but previous params may reference the old def.
     */
    protected ENG_GpuNamedConstants mConstantDefs;
    /// File from which to load named constants manually
    protected String mManualNamedConstantsFile;
    protected boolean mLoadedManualNamedConstants;

    protected boolean isRequiredCapabilitiesSupported() {
        ENG_RenderSystem renderSystem = ENG_RenderRoot.getRenderRoot().getRenderSystem();
        if (renderSystem == null) {
            // It can be null if we are in server mode and have not initialized a render system.
            return false;
        }
        ENG_RenderSystemCapabilities caps = renderSystem.getCapabilities();

        return !(isSkeletalAnimationIncluded() &&
                !caps.hasCapability(Capabilities.RSC_VERTEX_FORMAT_UBYTE4)) && !(isVertexTextureFetchRequired() && !caps.hasCapability(Capabilities.RSC_VERTEX_TEXTURE_FETCH));

    }

    protected void createParameterMappingStructures() {
        createParameterMappingStructures(true);
    }

    protected void createParameterMappingStructures(boolean recreateIfExists) {
        createNamedParameterMappingStructures(recreateIfExists);
    }

    protected void createNamedParameterMappingStructures() {
        createNamedParameterMappingStructures(true);
    }

    protected void createNamedParameterMappingStructures(boolean recreateIfExists) {
        if (recreateIfExists || (mConstantDefs == null)) {
            mConstantDefs = new ENG_GpuNamedConstants();
        }
    }

    public ENG_GpuProgram(String name) {
        mName = name;
        createParameterMappingStructures();
    }

    public String getName() {
        return mName;
    }

    public void setType(GpuProgramType type) {
        mType = type;
    }

    public GpuProgramType getType() {
        return mType;
    }

    public void setSyntaxCode(String syntax) {
        mSyntaxCode = syntax;
    }

    public String getSyntaxCode() {
        return mSyntaxCode;
    }

    public void setSourceFile(String filename, String path) {
        mFilename = filename;
        mPath = path;
        mSource = null;
        mLoadFromFile = true;
        mCompileError = false;
    }

    public void setSource(String source) {
        mSource = source;
        mFilename = null;
        mPath = null;
        mLoadFromFile = false;
        mCompileError = false;
    }

    public String getSourceFile() {
        return mFilename;
    }

    public String getSource() {
        return mSource;
    }

    public ENG_GpuProgram _getBindingDelegate() {
        return this;
    }

    public ENG_GpuProgramParameters createParameters() {
        return ENG_GpuProgramManager.getSingleton().createParameters();
    }

    public boolean isSupported() {
        return !(mCompileError || !isRequiredCapabilitiesSupported());

    }

    public ENG_GpuProgramParameters getDefaultParameters() {
        if (mDefaultParams == null) {
            mDefaultParams = createParameters();
        }
        return mDefaultParams;
    }

    public void setSkeletalAnimationIncluded(boolean included) {
        mSkeletalAnimation = included;
    }

    public boolean isSkeletalAnimationIncluded() {
        return mSkeletalAnimation;
    }

    public void setMorphAnimationIncluded(boolean included) {
        mMorphAnimation = included;
    }

    public boolean isMorphAnimationIncluded() {
        return mMorphAnimation;
    }

    public void setPoseAnimationIncluded(short poseCount) {
        mPoseAnimation = poseCount;
    }

    public boolean isPoseAnimationIncluded() {
        return mPoseAnimation > 0;
    }

    public short getNumberOfPosesIncluded() {
        return mPoseAnimation;
    }

    public void setVertexTextureFetchRequired(boolean r) {
        mVertexTextureFetch = r;
    }

    public boolean isVertexTextureFetchRequired() {
        return mVertexTextureFetch;
    }

    public void setAdjacencyInfoRequired(boolean r) {
        mNeedsAdjacencyInfo = r;
    }

    public boolean isAdjacencyInfoRequired() {
        return mNeedsAdjacencyInfo;
    }

    public boolean hasDefaultParameters() {
        return mDefaultParams != null;
    }

    public boolean getPassSurfaceAndLightStates() {
        return false;
    }

    public boolean getPassFogStates() {
        return true;
    }

    public boolean getPassTransformStates() {
        return false;
    }

    public boolean hasCompileError() {
        return mCompileError;
    }

    public void resetCompileError() {
        mCompileError = false;
    }

    protected abstract void loadFromSource();

    public void load() {
        

    }

    public boolean isLoaded() {
        
        return false;
    }
}
