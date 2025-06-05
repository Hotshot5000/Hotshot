/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:10 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeSet;

public class ENG_RenderSystemCapabilities {

    private static final int CAPS_CATEGORY_SIZE = 4;
    private static final int OGRE_CAPS_BITSHIFT = (32 - CAPS_CATEGORY_SIZE);
    private static final int CAPS_CATEGORY_MASK =
            (((1 << CAPS_CATEGORY_SIZE) - 1) << OGRE_CAPS_BITSHIFT);

    private static int OGRE_CAPS_VALUE(int cat, int val) {
        return ((cat << OGRE_CAPS_BITSHIFT) | (1 << val));
    }

    public enum CapabilitiesCategory {
        CAPS_CATEGORY_COMMON(0),
        CAPS_CATEGORY_COMMON_2(1),
        CAPS_CATEGORY_D3D9(2),
        CAPS_CATEGORY_GL(3),
        /// Placeholder for max value
        CAPS_CATEGORY_COUNT(4);

        private final int category;

        CapabilitiesCategory(int category) {
            this.category = category;
        }

        public int getCategory() {
            return category;
        }
    }

    public enum Capabilities {
        /// Supports generating mipmaps in hardware
        RSC_AUTOMIPMAP(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 0)),
        RSC_BLENDING(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 1)),
        /// Supports anisotropic texture filtering
        RSC_ANISOTROPY(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 2)),
        /// Supports fixed-function DOT3 texture blend
        RSC_DOT3(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 3)),
        /// Supports cube mapping
        RSC_CUBEMAPPING(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 4)),
        /// Supports hardware stencil buffer
        RSC_HWSTENCIL(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 5)),
        /// Supports hardware vertex and index buffers
        RSC_VBO(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 7)),
        /// Supports vertex programs (vertex shaders)
        RSC_VERTEX_PROGRAM(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 9)),
        /// Supports fragment programs (pixel shaders)
        RSC_FRAGMENT_PROGRAM(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 10)),
        /// Supports performing a scissor test to exclude areas of the screen
        RSC_SCISSOR_TEST(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 11)),
        /// Supports separate stencil updates for both front and back faces
        RSC_TWO_SIDED_STENCIL(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 12)),
        /// Supports wrapping the stencil value at the range extremeties
        RSC_STENCIL_WRAP(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 13)),
        /// Supports hardware occlusion queries
        RSC_HWOCCLUSION(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 14)),
        /// Supports user clipping planes
        RSC_USER_CLIP_PLANES(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 15)),
        /// Supports the VET_UBYTE4 vertex element type
        RSC_VERTEX_FORMAT_UBYTE4(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 16)),
        /// Supports infinite far plane projection
        RSC_INFINITE_FAR_PLANE(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 17)),
        /// Supports hardware render-to-texture (bigger than framebuffer)
        RSC_HWRENDER_TO_TEXTURE(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 18)),
        /// Supports float textures and render targets
        RSC_TEXTURE_FLOAT(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 19)),
        /// Supports non-power of two textures
        RSC_NON_POWER_OF_2_TEXTURES(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 20)),
        /// Supports 3d (volume) textures
        RSC_TEXTURE_3D(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 21)),
        /// Supports basic point sprite rendering
        RSC_POINT_SPRITES(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 22)),
        /// Supports extra point parameters (minsize, maxsize, attenuation)
        RSC_POINT_EXTENDED_PARAMETERS(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 23)),
        /// Supports vertex texture fetch
        RSC_VERTEX_TEXTURE_FETCH(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 24)),
        /// Supports mipmap LOD biasing
        RSC_MIPMAP_LOD_BIAS(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 25)),
        /// Supports hardware geometry programs
        RSC_GEOMETRY_PROGRAM(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 26)),
        /// Supports rendering to vertex buffers
        RSC_HWRENDER_TO_VERTEX_BUFFER(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory(), 27)),

        /// Supports compressed textures
        RSC_TEXTURE_COMPRESSION(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON_2.getCategory(), 0)),
        /// Supports compressed textures in the DXT/ST3C formats
        RSC_TEXTURE_COMPRESSION_DXT(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON_2.getCategory(), 1)),
        /// Supports compressed textures in the VTC format
        RSC_TEXTURE_COMPRESSION_VTC(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON_2.getCategory(), 2)),
        /// Supports compressed textures in the PVRTC format
        RSC_TEXTURE_COMPRESSION_PVRTC(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON_2.getCategory(), 3)),
        /// Supports fixed-function pipeline
        RSC_FIXED_FUNCTION(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON_2.getCategory(), 4)),
        /// Supports MRTs with different bit depths
        RSC_MRT_DIFFERENT_BIT_DEPTHS(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON_2.getCategory(), 5)),
        /// Supports Alpha to Coverage (A2C)
        RSC_ALPHA_TO_COVERAGE(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON_2.getCategory(), 6)),
        /// Supports Blending operations other than +
        RSC_ADVANCED_BLEND_OPERATIONS(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_COMMON_2.getCategory(), 7)),

        // ***** DirectX specific caps *****
        /// Is DirectX feature "per stage constants" supported
        RSC_PERSTAGECONSTANT(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_D3D9.getCategory(), 0)),

        // ***** GL Specific Caps *****
        /// Supports openGL GLEW version 1.5
        RSC_GL1_5_NOVBO(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_GL.getCategory(), 1)),
        /// Support for Frame Buffer Objects (FBOs)
        RSC_FBO(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_GL.getCategory(), 2)),
        /// Support for Frame Buffer Objects ARB implementation (regular FBO is higher precedence)
        RSC_FBO_ARB(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_GL.getCategory(), 3)),
        /// Support for Frame Buffer Objects ATI implementation (ARB FBO is higher precedence)
        RSC_FBO_ATI(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_GL.getCategory(), 4)),
        /// Support for PBuffer
        RSC_PBUFFER(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_GL.getCategory(), 5)),
        /// Support for GL 1.5 but without HW occlusion workaround
        RSC_GL1_5_NOHWOCCLUSION(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_GL.getCategory(), 6)),
        /// Support for point parameters ARB implementation
        RSC_POINT_EXTENDED_PARAMETERS_ARB(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_GL.getCategory(), 7)),
        /// Support for point parameters EXT implementation
        RSC_POINT_EXTENDED_PARAMETERS_EXT(OGRE_CAPS_VALUE(CapabilitiesCategory.CAPS_CATEGORY_GL.getCategory(), 8));

        private final int capability;

        Capabilities(int capability) {
            this.capability = capability;
        }

        public int getCapability() {
            return capability;
        }


    }

    static class DriverVersion {
        public int major, minor, release, build;

        public DriverVersion() {

        }

        public String toString() {
            return major + "." + minor + "." +
                    release + "." + build;
        }

        public void fromString(String versionString) {
            String[] tokens = versionString.split("\\.");
            if (tokens != null) {
                major = Integer.parseInt(tokens[0]);
                if (tokens.length > 1) {
                    minor = Integer.parseInt(tokens[1]);
                }
                if (tokens.length > 2) {
                    release = Integer.parseInt(tokens[2]);
                }
                if (tokens.length > 3) {
                    build = Integer.parseInt(tokens[3]);
                }
            }
        }
    }

    public enum GPUVendor {
        GPU_UNKNOWN(0),
        GPU_NVIDIA(1),
        GPU_ATI(2),
        GPU_INTEL(3),
        GPU_S3(4),
        GPU_MATROX(5),
        GPU_3DLABS(6),
        GPU_SIS(7),
        GPU_IMAGINATION_TECHNOLOGIES(8),
        GPU_APPLE(9),  // Apple Software Renderer
        GPU_NOKIA(10),

        /// placeholder
        GPU_VENDOR_COUNT(11);

        private int GPUVendor;

        GPUVendor(int GPUVendor) {
            this.GPUVendor = GPUVendor;
        }

        public int getGPUVendor() {
            return GPUVendor;
        }

        public void setGPUVendor(int GPUVendor) {
            this.GPUVendor = GPUVendor;
        }
    }

    private DriverVersion mDriverVersion = new DriverVersion();
    private GPUVendor mVendor;
    private static final ArrayList<String> msGPUVendorStrings = new ArrayList<>();
    /// The number of world matrices available
    private short mNumWorldMatrices;
    /// The number of texture units available
    private short mNumTextureUnits;
    /// The stencil buffer bit depth
    private short mStencilBufferBitDepth;
    /// The number of matrices available for hardware blending
    private short mNumVertexBlendMatrices;
    /// Stores the capabilities flags.
    private final int[] mCapabilities = new int[CapabilitiesCategory.CAPS_CATEGORY_COUNT.getCategory()];
    /// Which categories are relevant
    private final boolean[] mCategoryRelevant = new boolean[CapabilitiesCategory.CAPS_CATEGORY_COUNT.getCategory()];
    /// The name of the device as reported by the render system
    private String mDeviceName;
    /// The identifier associated with the render system for which these capabilities are valid
    private String mRenderSystemName;

    /// The number of floating-point constants vertex programs support
    private short mVertexProgramConstantFloatCount;
    /// The number of integer constants vertex programs support
    private short mVertexProgramConstantIntCount;
    /// The number of boolean constants vertex programs support
    private short mVertexProgramConstantBoolCount;
    /// The number of floating-point constants geometry programs support
    private short mGeometryProgramConstantFloatCount;
    /// The number of integer constants vertex geometry support
    private short mGeometryProgramConstantIntCount;
    /// The number of boolean constants vertex geometry support
    private short mGeometryProgramConstantBoolCount;
    /// The number of floating-point constants fragment programs support
    private short mFragmentProgramConstantFloatCount;
    /// The number of integer constants fragment programs support
    private short mFragmentProgramConstantIntCount;
    /// The number of boolean constants fragment programs support
    private short mFragmentProgramConstantBoolCount;
    /// The number of simultaneous render targets supported
    private short mNumMultiRenderTargets;
    /// The maximum point size
    private float mMaxPointSize;
    /// Are non-POW2 textures feature-limited?
    private boolean mNonPOW2TexturesLimited;
    /// The number of vertex texture units supported
    private short mNumVertexTextureUnits;
    /// Are vertex texture units shared with fragment processor?
    private boolean mVertexTextureUnitsShared;
    /// The number of vertices a geometry program can emit in a single run
    private int mGeometryProgramNumOutputVertices;
    /// The list of supported shader profiles
    private final TreeSet<String> mSupportedShaderProfiles = new TreeSet<>();

    public ENG_RenderSystemCapabilities() {
        mNumMultiRenderTargets = 1;
        mCategoryRelevant[CapabilitiesCategory.CAPS_CATEGORY_COMMON.getCategory()] = true;
        mCategoryRelevant[CapabilitiesCategory.CAPS_CATEGORY_COMMON_2.getCategory()] = true;
    }

    public int calculateSize() {
        return 0;
    }

    public void setDriverVersion(DriverVersion version) {
        mDriverVersion = version;
    }

    public void parseDriverVersionFromString(String versionString) {
        DriverVersion version = new DriverVersion();
        version.fromString(versionString);
        setDriverVersion(version);
    }

    public DriverVersion getDriverVersion() {
        return mDriverVersion;
    }

    public GPUVendor getVendor() {
        return mVendor;
    }

    public void setVendor(GPUVendor vendor) {
        mVendor = vendor;
    }

    public void parseVendorFromString(String vendorString) {
        setVendor(vendorFromString(vendorString));
    }

    public static GPUVendor vendorFromString(String vendorString) {
        initVendorStrings();
        GPUVendor ret = GPUVendor.GPU_UNKNOWN;
        String cmpString = vendorString.toLowerCase(Locale.US);
        for (int i = 0; i < GPUVendor.GPU_VENDOR_COUNT.getGPUVendor(); ++i) {
            if (msGPUVendorStrings.get(i).equals(cmpString)) {
                ret.setGPUVendor(i);
                break;
            }
        }
        return ret;
    }

    public static String vendorToString(GPUVendor vendor) {
        initVendorStrings();
        return msGPUVendorStrings.get(vendor.getGPUVendor());
    }

    public boolean isDriverOlderThanVersion(DriverVersion v) {
        if (mDriverVersion.major < v.major)
            return true;
        else if (mDriverVersion.major == v.major &&
                mDriverVersion.minor < v.minor)
            return true;
        else if (mDriverVersion.major == v.major &&
                mDriverVersion.minor == v.minor &&
                mDriverVersion.release < v.release)
            return true;
        else if (mDriverVersion.major == v.major &&
                mDriverVersion.minor == v.minor &&
                mDriverVersion.release == v.release &&
                mDriverVersion.build < v.build)
            return true;
        return false;
    }

    public boolean isCapabilityRenderSystemSpecific(Capabilities c) {
        int cat = c.getCapability() >> OGRE_CAPS_BITSHIFT;
        return cat == CapabilitiesCategory.CAPS_CATEGORY_GL.getCategory() ||
                cat == CapabilitiesCategory.CAPS_CATEGORY_D3D9.getCategory();
    }

    public void setCapability(Capabilities c) {
        int index = (CAPS_CATEGORY_MASK & c.getCapability()) >> OGRE_CAPS_BITSHIFT;
        // zero out the index from the stored capability
        mCapabilities[index] |= (c.getCapability() & ~CAPS_CATEGORY_MASK);
    }

    public void unsetCapability(Capabilities c) {
        int index = (CAPS_CATEGORY_MASK & c.getCapability()) >> OGRE_CAPS_BITSHIFT;
        // zero out the index from the stored capability
        mCapabilities[index] &= (~c.getCapability() | CAPS_CATEGORY_MASK);
    }

    public boolean hasCapability(Capabilities c) {
        int index = (CAPS_CATEGORY_MASK & c.getCapability()) >> OGRE_CAPS_BITSHIFT;
        // test against
        return (mCapabilities[index] & (c.getCapability() & ~CAPS_CATEGORY_MASK)) != 0;
    }

    public void addShaderProfile(String profile) {
        mSupportedShaderProfiles.add(profile);
    }

    public void removeShaderProfile(String profile) {
        mSupportedShaderProfiles.remove(profile);
    }

    public boolean isShaderProfileSupported(String profile) {
        return (mSupportedShaderProfiles.contains(profile));
    }

    public TreeSet<String> getSupportedShaderProfile() {
        return mSupportedShaderProfiles;
    }

    public void setCategoryRelevant(CapabilitiesCategory cat, boolean relevant) {
        mCategoryRelevant[cat.getCategory()] = relevant;
    }

    public boolean isCategoryRelevant(CapabilitiesCategory cat) {
        return mCategoryRelevant[cat.getCategory()];
    }

    private static void initVendorStrings() {
        if (msGPUVendorStrings.isEmpty()) {
            msGPUVendorStrings.ensureCapacity(GPUVendor.GPU_VENDOR_COUNT.getGPUVendor());
            msGPUVendorStrings.add(GPUVendor.GPU_UNKNOWN.getGPUVendor(), "unknown");
            msGPUVendorStrings.add(GPUVendor.GPU_NVIDIA.getGPUVendor(), "nvidia");
            msGPUVendorStrings.add(GPUVendor.GPU_ATI.getGPUVendor(), "ati");
            msGPUVendorStrings.add(GPUVendor.GPU_INTEL.getGPUVendor(), "intel");
            msGPUVendorStrings.add(GPUVendor.GPU_3DLABS.getGPUVendor(), "3dlabs");
            msGPUVendorStrings.add(GPUVendor.GPU_S3.getGPUVendor(), "s3");
            msGPUVendorStrings.add(GPUVendor.GPU_MATROX.getGPUVendor(), "matrox");
            msGPUVendorStrings.add(GPUVendor.GPU_SIS.getGPUVendor(), "sis");
            msGPUVendorStrings.add(GPUVendor.GPU_IMAGINATION_TECHNOLOGIES.getGPUVendor(), "imagination technologies");
            msGPUVendorStrings.add(GPUVendor.GPU_APPLE.getGPUVendor(), "apple");
        }
    }

    /**
     * @return the mNumWorldMatrices
     */
    public short getmNumWorldMatrices() {
        return mNumWorldMatrices;
    }

    /**
     * @param mNumWorldMatrices the mNumWorldMatrices to set
     */
    public void setmNumWorldMatrices(short mNumWorldMatrices) {
        this.mNumWorldMatrices = mNumWorldMatrices;
    }

    /**
     * @return the mNumTextureUnits
     */
    public short getmNumTextureUnits() {
        return mNumTextureUnits;
    }

    /**
     * @param mNumTextureUnits the mNumTextureUnits to set
     */
    public void setmNumTextureUnits(short mNumTextureUnits) {
        this.mNumTextureUnits = mNumTextureUnits;
    }

    /**
     * @return the mStencilBufferBitDepth
     */
    public short getmStencilBufferBitDepth() {
        return mStencilBufferBitDepth;
    }

    /**
     * @param mStencilBufferBitDepth the mStencilBufferBitDepth to set
     */
    public void setmStencilBufferBitDepth(short mStencilBufferBitDepth) {
        this.mStencilBufferBitDepth = mStencilBufferBitDepth;
    }

    /**
     * @return the mNumVertexBlendMatrices
     */
    public short getmNumVertexBlendMatrices() {
        return mNumVertexBlendMatrices;
    }

    /**
     * @param mNumVertexBlendMatrices the mNumVertexBlendMatrices to set
     */
    public void setmNumVertexBlendMatrices(short mNumVertexBlendMatrices) {
        this.mNumVertexBlendMatrices = mNumVertexBlendMatrices;
    }

    /**
     * @return the mDeviceName
     */
    public String getmDeviceName() {
        return mDeviceName;
    }

    /**
     * @param mDeviceName the mDeviceName to set
     */
    public void setmDeviceName(String mDeviceName) {
        this.mDeviceName = mDeviceName;
    }

    /**
     * @return the mRenderSystemName
     */
    public String getmRenderSystemName() {
        return mRenderSystemName;
    }

    /**
     * @param mRenderSystemName the mRenderSystemName to set
     */
    public void setmRenderSystemName(String mRenderSystemName) {
        this.mRenderSystemName = mRenderSystemName;
    }

    /**
     * @return the mVertexProgramConstantFloatCount
     */
    public short getmVertexProgramConstantFloatCount() {
        return mVertexProgramConstantFloatCount;
    }

    /**
     * @param mVertexProgramConstantFloatCount the mVertexProgramConstantFloatCount to set
     */
    public void setmVertexProgramConstantFloatCount(
            short mVertexProgramConstantFloatCount) {
        this.mVertexProgramConstantFloatCount = mVertexProgramConstantFloatCount;
    }

    /**
     * @return the mVertexProgramConstantIntCount
     */
    public short getmVertexProgramConstantIntCount() {
        return mVertexProgramConstantIntCount;
    }

    /**
     * @param mVertexProgramConstantIntCount the mVertexProgramConstantIntCount to set
     */
    public void setmVertexProgramConstantIntCount(
            short mVertexProgramConstantIntCount) {
        this.mVertexProgramConstantIntCount = mVertexProgramConstantIntCount;
    }

    /**
     * @return the mVertexProgramConstantBoolCount
     */
    public short getmVertexProgramConstantBoolCount() {
        return mVertexProgramConstantBoolCount;
    }

    /**
     * @param mVertexProgramConstantBoolCount the mVertexProgramConstantBoolCount to set
     */
    public void setmVertexProgramConstantBoolCount(
            short mVertexProgramConstantBoolCount) {
        this.mVertexProgramConstantBoolCount = mVertexProgramConstantBoolCount;
    }

    /**
     * @return the mGeometryProgramConstantFloatCount
     */
    public short getmGeometryProgramConstantFloatCount() {
        return mGeometryProgramConstantFloatCount;
    }

    /**
     * @param mGeometryProgramConstantFloatCount the mGeometryProgramConstantFloatCount to set
     */
    public void setmGeometryProgramConstantFloatCount(
            short mGeometryProgramConstantFloatCount) {
        this.mGeometryProgramConstantFloatCount = mGeometryProgramConstantFloatCount;
    }

    /**
     * @return the mGeometryProgramConstantIntCount
     */
    public short getmGeometryProgramConstantIntCount() {
        return mGeometryProgramConstantIntCount;
    }

    /**
     * @param mGeometryProgramConstantIntCount the mGeometryProgramConstantIntCount to set
     */
    public void setmGeometryProgramConstantIntCount(
            short mGeometryProgramConstantIntCount) {
        this.mGeometryProgramConstantIntCount = mGeometryProgramConstantIntCount;
    }

    /**
     * @return the mGeometryProgramConstantBoolCount
     */
    public short getmGeometryProgramConstantBoolCount() {
        return mGeometryProgramConstantBoolCount;
    }

    /**
     * @param mGeometryProgramConstantBoolCount the mGeometryProgramConstantBoolCount to set
     */
    public void setmGeometryProgramConstantBoolCount(
            short mGeometryProgramConstantBoolCount) {
        this.mGeometryProgramConstantBoolCount = mGeometryProgramConstantBoolCount;
    }

    /**
     * @return the mFragmentProgramConstantFloatCount
     */
    public short getmFragmentProgramConstantFloatCount() {
        return mFragmentProgramConstantFloatCount;
    }

    /**
     * @param mFragmentProgramConstantFloatCount the mFragmentProgramConstantFloatCount to set
     */
    public void setmFragmentProgramConstantFloatCount(
            short mFragmentProgramConstantFloatCount) {
        this.mFragmentProgramConstantFloatCount = mFragmentProgramConstantFloatCount;
    }

    /**
     * @return the mFragmentProgramConstantIntCount
     */
    public short getmFragmentProgramConstantIntCount() {
        return mFragmentProgramConstantIntCount;
    }

    /**
     * @param mFragmentProgramConstantIntCount the mFragmentProgramConstantIntCount to set
     */
    public void setmFragmentProgramConstantIntCount(
            short mFragmentProgramConstantIntCount) {
        this.mFragmentProgramConstantIntCount = mFragmentProgramConstantIntCount;
    }

    /**
     * @return the mFragmentProgramConstantBoolCount
     */
    public short getmFragmentProgramConstantBoolCount() {
        return mFragmentProgramConstantBoolCount;
    }

    /**
     * @param mFragmentProgramConstantBoolCount the mFragmentProgramConstantBoolCount to set
     */
    public void setmFragmentProgramConstantBoolCount(
            short mFragmentProgramConstantBoolCount) {
        this.mFragmentProgramConstantBoolCount = mFragmentProgramConstantBoolCount;
    }

    /**
     * @return the mNumMultiRenderTargets
     */
    public short getmNumMultiRenderTargets() {
        return mNumMultiRenderTargets;
    }

    /**
     * @param mNumMultiRenderTargets the mNumMultiRenderTargets to set
     */
    public void setmNumMultiRenderTargets(short mNumMultiRenderTargets) {
        this.mNumMultiRenderTargets = mNumMultiRenderTargets;
    }

    /**
     * @return the mMaxPointSize
     */
    public float getmMaxPointSize() {
        return mMaxPointSize;
    }

    /**
     * @param mMaxPointSize the mMaxPointSize to set
     */
    public void setmMaxPointSize(float mMaxPointSize) {
        this.mMaxPointSize = mMaxPointSize;
    }

    /**
     * @return the mNonPOW2TexturesLimited
     */
    public boolean ismNonPOW2TexturesLimited() {
        return mNonPOW2TexturesLimited;
    }

    /**
     * @param mNonPOW2TexturesLimited the mNonPOW2TexturesLimited to set
     */
    public void setmNonPOW2TexturesLimited(boolean mNonPOW2TexturesLimited) {
        this.mNonPOW2TexturesLimited = mNonPOW2TexturesLimited;
    }

    /**
     * @return the mNumVertexTextureUnits
     */
    public short getmNumVertexTextureUnits() {
        return mNumVertexTextureUnits;
    }

    /**
     * @param mNumVertexTextureUnits the mNumVertexTextureUnits to set
     */
    public void setmNumVertexTextureUnits(short mNumVertexTextureUnits) {
        this.mNumVertexTextureUnits = mNumVertexTextureUnits;
    }

    /**
     * @return the mVertexTextureUnitsShared
     */
    public boolean ismVertexTextureUnitsShared() {
        return mVertexTextureUnitsShared;
    }

    /**
     * @param mVertexTextureUnitsShared the mVertexTextureUnitsShared to set
     */
    public void setmVertexTextureUnitsShared(boolean mVertexTextureUnitsShared) {
        this.mVertexTextureUnitsShared = mVertexTextureUnitsShared;
    }

    /**
     * @return the mGeometryProgramNumOutputVertices
     */
    public int getmGeometryProgramNumOutputVertices() {
        return mGeometryProgramNumOutputVertices;
    }

    /**
     * @param mGeometryProgramNumOutputVertices the mGeometryProgramNumOutputVertices to set
     */
    public void setmGeometryProgramNumOutputVertices(
            int mGeometryProgramNumOutputVertices) {
        this.mGeometryProgramNumOutputVertices = mGeometryProgramNumOutputVertices;
    }
}
