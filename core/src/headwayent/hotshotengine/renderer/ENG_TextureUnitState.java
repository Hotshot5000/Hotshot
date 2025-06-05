/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:10 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Controller;
import headwayent.hotshotengine.ENG_ControllerTypeFloat;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.renderer.ENG_BlendMode.LayerBlendModeEx;
import headwayent.hotshotengine.renderer.ENG_BlendMode.LayerBlendOperation;
import headwayent.hotshotengine.renderer.ENG_BlendMode.LayerBlendOperationEx;
import headwayent.hotshotengine.renderer.ENG_BlendMode.LayerBlendSource;
import headwayent.hotshotengine.renderer.ENG_BlendMode.LayerBlendType;
import headwayent.hotshotengine.renderer.ENG_BlendMode.SceneBlendFactor;
import headwayent.hotshotengine.renderer.ENG_Common.FilterOptions;
import headwayent.hotshotengine.renderer.ENG_Common.FilterType;
import headwayent.hotshotengine.renderer.ENG_Common.TextureFilterOptions;
import headwayent.hotshotengine.renderer.ENG_Common.WaveformType;
import headwayent.hotshotengine.renderer.ENG_Pass.BuiltinHashFunction;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;
import headwayent.hotshotengine.renderer.ENG_Texture.TextureMipmap;
import headwayent.hotshotengine.renderer.ENG_Texture.TextureType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map.Entry;

public class ENG_TextureUnitState {

    /**
     * Definition of the broad types of texture effect you can apply to a texture unit.
     *
     * @note Note that these have no effect when using the programmable pipeline, since their
     * effect is overridden by the vertex / fragment programs.
     */
    public enum TextureEffectType {
        /// Generate all texture coords based on angle between camera and vertex
        ET_ENVIRONMENT_MAP,
        /// Generate texture coords based on a frustum
        ET_PROJECTIVE_TEXTURE,
        /// Constant u/v scrolling effect
        ET_UVSCROLL,
        /// Constant u scrolling effect
        ET_USCROLL,
        /// Constant u/v scrolling effect
        ET_VSCROLL,
        /// Constant rotation
        ET_ROTATE,
        /// More complex transform
        ET_TRANSFORM

    }

    /**
     * Useful enumeration when dealing with procedural transforms.
     *
     * @note Note that these have no effect when using the programmable pipeline, since their
     * effect is overridden by the vertex / fragment programs.
     */
    public enum TextureTransformType {
        TT_TRANSLATE_U,
        TT_TRANSLATE_V,
        TT_SCALE_U,
        TT_SCALE_V,
        TT_ROTATE
    }

    /**
     * Texture addressing modes - default is TAM_WRAP.
     *
     * @note These settings are relevant in both the fixed-function and the
     * programmable pipeline.
     */
    public enum TextureAddressingMode {
        /// Texture wraps at values over 1.0
        TAM_WRAP,
        /// Texture mirrors (flips) at joins over 1.0
        TAM_MIRROR,
        /// Texture clamps at 1.0
        TAM_CLAMP,
        /// Texture coordinates outside the range [0.0, 1.0] are set to the border colour
        TAM_BORDER;

        public static TextureAddressingMode getTextureAddressingMode(int i) {
            switch (i) {
                case 0:
                    return TAM_WRAP;
                case 1:
                    return TAM_MIRROR;
                case 2:
                    return TAM_CLAMP;
                case 3:
                    return TAM_BORDER;
                default:
                    throw new IllegalArgumentException("The value " + i +
                            " is not a valid texture addressing mode");
            }
        }
    }

    public static class UVWAddressingMode {
        public TextureAddressingMode u, v, w;

        public void set(UVWAddressingMode mode) {
            u = mode.u;
            v = mode.v;
            w = mode.w;
        }

        public UVWAddressingMode() {

        }

        public UVWAddressingMode(int u, int v, int w) {
            set(u, v, w);
        }

        public void set(int u, int v, int w) {
            this.u = TextureAddressingMode.getTextureAddressingMode(u);
            this.v = TextureAddressingMode.getTextureAddressingMode(v);
            this.w = TextureAddressingMode.getTextureAddressingMode(w);
        }
    }

    /**
     * Enum identifying the frame indexes for faces of a cube map (not the composite 3D type.
     */
    public enum TextureCubeFace {
        CUBE_FRONT(0),
        CUBE_BACK(1),
        CUBE_LEFT(2),
        CUBE_RIGHT(3),
        CUBE_UP(4),
        CUBE_DOWN(5);

        private final int face;

        TextureCubeFace(int face) {
            this.face = face;
        }

        public int getFace() {
            return face;
        }
    }

    /**
     * The type of unit to bind the texture settings to.
     */
    public enum BindingType {
        /**
         * Regular fragment processing unit - the default.
         */
        BT_FRAGMENT(0),
        /**
         * Vertex processing unit - indicates this unit will be used for
         * a vertex texture fetch.
         */
        BT_VERTEX(1);

        private final int type;

        BindingType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    public enum ContentType {
        /// Normal texture identified by name
        CONTENT_NAMED(0),
        /// A shadow texture, automatically bound by engine
        CONTENT_SHADOW(1),
        /// A compositor texture, automatically linked to active viewport's chain
        CONTENT_COMPOSITOR(2);

        private final int type;

        ContentType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    public static class TextureEffect {
        public TextureEffectType type;
        public int subtype;
        public float arg1, arg2;
        public WaveformType waveType;
        public float base;
        public float frequency;
        public float phase;
        public float amplitude;
        public ENG_Controller<ENG_ControllerTypeFloat> controller;
        public ENG_Frustum frustum;

        public void set(TextureEffect oth) {
            type = oth.type;
            subtype = oth.subtype;
            arg1 = oth.arg1;
            arg2 = oth.arg2;
            waveType = oth.waveType;
            base = oth.base;
            frequency = oth.frequency;
            phase = oth.phase;
            amplitude = oth.amplitude;
            //Ignore the controller
            frustum = oth.frustum;
        }
    }

    protected int mCurrentFrame;
    protected float mAnimDuration;
    protected boolean mCubic;
    protected TextureType mTextureType = TextureType.TEX_TYPE_2D;
    protected PixelFormat mDesiredFormat = PixelFormat.PF_UNKNOWN;
    protected int mTextureSrcMipmaps = TextureMipmap.MIP_DEFAULT.getMipmap();
    protected int mTextureCoordSetIndex;
    protected UVWAddressingMode mAddressMode = new UVWAddressingMode();
    protected final ENG_ColorValue mBorderColour = new ENG_ColorValue(ENG_ColorValue.BLACK);
    protected final LayerBlendModeEx mColourBlendMode = new LayerBlendModeEx();
    protected SceneBlendFactor mColourBlendFallbackSrc;
    protected SceneBlendFactor mColourBlendFallbackDest;

    protected final LayerBlendModeEx mAlphaBlendMode = new LayerBlendModeEx();
    protected boolean mTextureLoadFailed;
    protected boolean mIsAlpha;
    protected boolean mHwGamma;
    protected boolean mRecalcTexMatrix;
    protected float mUMod, mVMod;
    protected float mUScale = 1.0f, mVScale = 1.0f;
    protected float mRotate;
    protected final ENG_Matrix4 mTexModMatrix = new ENG_Matrix4();

    /// Texture filtering - minification
    protected FilterOptions mMinFilter = FilterOptions.FO_LINEAR;
    /// Texture filtering - magnification
    protected FilterOptions mMagFilter = FilterOptions.FO_LINEAR;
    /// Texture filtering - mipmapping
    protected FilterOptions mMipFilter = FilterOptions.FO_POINT;
    ///Texture anisotropy
    protected int mMaxAniso;
    /// Mipmap bias (always float, not Real)
    protected float mMipmapBias;

    protected boolean mIsDefaultAniso = true;
    protected boolean mIsDefaultFiltering = true;
    /// Binding type (fragment or vertex pipeline)
    protected BindingType mBindingType = BindingType.BT_FRAGMENT;
    /// Content type of texture (normal loaded texture, auto-texture)
    protected ContentType mContentType = ContentType.CONTENT_NAMED;
    /// The index of the referenced texture if referencing an MRT in a compositor
    protected int mCompositorRefMrtIndex;

    protected final ArrayList<String> mFrames = new ArrayList<>();
    protected final ArrayList<ENG_Texture> mFramePtrs = new ArrayList<>();
    protected String mName = "";               // optional name for the TUS
    protected String mTextureNameAlias;       // optional alias for texture frames
    protected final EnumMap<TextureEffectType, ArrayList<TextureEffect>> mEffects =
            new EnumMap<>(TextureEffectType.class);
    protected ENG_Controller<ENG_ControllerTypeFloat> mAnimController;
    ///The data that references the compositor
    protected String mCompositorRefName;
    protected String mCompositorRefTexName;

    protected ENG_Pass mParent;

    private final String[] suffixes = {"_fr", "_bk", "_lf", "_rt", "_up", "_dn"};

    public ENG_TextureUnitState(ENG_Pass parent) {
        mParent = parent;
        mColourBlendMode.blendType = LayerBlendType.LBT_COLOUR;
        mAlphaBlendMode.operation = LayerBlendOperationEx.LBX_MODULATE;
        mAlphaBlendMode.blendType = LayerBlendType.LBT_ALPHA;
        mAlphaBlendMode.source1 = LayerBlendSource.LBS_TEXTURE;
        mAlphaBlendMode.source2 = LayerBlendSource.LBS_CURRENT;

        setColourOperation(LayerBlendOperation.LBO_MODULATE);
        setTextureAddressingMode(TextureAddressingMode.TAM_WRAP);

        if (ENG_Pass.getHashFunction() == ENG_Pass.getBuiltinHashFunction(BuiltinHashFunction.MIN_TEXTURE_CHANGE)) {
            mParent._dirtyHash();
        }
    }

    public ENG_TextureUnitState(ENG_Pass parent, String name, int TexCoordSet) {
        this(parent);

    }

    public ENG_TextureUnitState(ENG_Pass eng_Pass, ENG_TextureUnitState tex) {
        
        mParent = eng_Pass;
        set(tex);
    }

    public void setContentType(ContentType ct) {
        mContentType = ct;
        if ((ct == ContentType.CONTENT_SHADOW) ||
                (ct == ContentType.CONTENT_COMPOSITOR)) {
            mFrames.clear();
            mFramePtrs.clear();
            //mFramePtrs.set(0, null);
        }
    }

    public ContentType getContentType() {
        return mContentType;
    }

    public void addFrameTextureName(String name) {
        setContentType(ContentType.CONTENT_NAMED);
        mTextureLoadFailed = false;

        mFrames.add(name);

        _load();

        if (ENG_Pass.getHashFunction() ==
                ENG_Pass.getBuiltinHashFunction(
                        BuiltinHashFunction.MIN_TEXTURE_CHANGE)) {
            mParent._dirtyHash();
        }
    }

    public void setFrameTextureName(String name, int frameNumber) {
        mTextureLoadFailed = false;
        if (frameNumber < mFrames.size()) {
            mFrames.set(frameNumber, name);
            mFramePtrs.clear(); //Just reload everything to make sure we maintain order

            _load();

            if (ENG_Pass.getHashFunction() ==
                    ENG_Pass.getBuiltinHashFunction(
                            BuiltinHashFunction.MIN_TEXTURE_CHANGE)) {
                mParent._dirtyHash();
            }
        } else {
            throw new IllegalArgumentException("frameNumber out of bounds");
        }
    }

    public void deleteFrameTextureName(int frameNumber) {
        mTextureLoadFailed = false;
        if (frameNumber < mFrames.size()) {
            mFrames.remove(frameNumber);
            mFramePtrs.remove(frameNumber);

            _load();

            if (ENG_Pass.getHashFunction() ==
                    ENG_Pass.getBuiltinHashFunction(
                            BuiltinHashFunction.MIN_TEXTURE_CHANGE)) {
                mParent._dirtyHash();
            }
        } else {
            throw new IllegalArgumentException("frameNumber out of bounds");
        }
    }

    public void setCubicTextureName(String[] name, boolean forUVW) {
        if ((name.length != 6) && (name.length != 1)) {
            throw new IllegalArgumentException(
                    "name must contain 6 or 1 faces name list");
        }
        setContentType(ContentType.CONTENT_NAMED);
        mTextureLoadFailed = false;
        mFrames.ensureCapacity(forUVW ? 1 : 6);
        mFramePtrs.ensureCapacity(forUVW ? 1 : 6);
        mAnimDuration = 0;
        mCurrentFrame = 0;
        mCubic = true;
        mTextureType = forUVW ?
                TextureType.TEX_TYPE_CUBE_MAP : TextureType.TEX_TYPE_2D;

        for (int i = 0; i < (forUVW ? 1 : 6); ++i) {
            mFrames.add(name[i]);
            mFramePtrs.set(i, null);
        }
    }

    /**
     * Sets this texture layer to use a combination of 6 texture maps, each one relating to a face of a cube.
     *
     * @param name   The basic name of the texture e.g. brickwall.jpg, stonefloor.png. There must be 6 versions
     *               of this texture with the suffixes _fr, _bk, _up, _dn, _lf, and _rt (before the extension) which
     *               make up the 6 sides of the box. The textures must all be the same size and be powers of 2 in width & height.
     *               If you can't make your texture names conform to this, use the alternative method of the same name which takes
     *               an array of texture names instead.
     * @param forUVW Set to true if you want a single 3D texture addressable with 3D texture coordinates rather than
     *               6 separate textures. Useful for cubic environment mapping.
     * @remarks Cubic textures are made up of 6 separate texture images. Each one of these is an orthoganal view of the
     * world with a FOV of 90 degrees and an aspect ratio of 1:1. You can generate these from 3D Studio by
     * rendering a scene to a reflection map of a transparent cube and saving the output files.
     * @par Cubic maps can be used either for skyboxes (complete wrap-around skies, like space) or as environment
     * maps to simulate reflections. The system deals with these 2 scenarios in different ways:
     * <ol>
     * <li>
     * <p>
     * for cubic environment maps, the 6 textures are combined into a single 'cubic' texture map which
     * is then addressed using 3D texture coordinates. This is required because you don't know what
     * face of the box you're going to need to address when you render an object, and typically you
     * need to reflect more than one face on the one object, so all 6 textures are needed to be
     * 'active' at once. Cubic environment maps are enabled by calling this method with the forUVW
     * parameter set to true, and then calling setEnvironmentMap(true).
     * </p>
     * <p>
     * Note that not all cards support cubic environment mapping.
     * </p>
     * </li>
     * <li>
     * <p>
     * for skyboxes, the 6 textures are kept separate and used independently for each face of the skybox.
     * This is done because not all cards support 3D cubic maps and skyboxes do not need to use 3D
     * texture coordinates so it is simpler to render each face of the box with 2D coordinates, changing
     * texture between faces.
     * </p>
     * <p>
     * Skyboxes are created by calling SceneManager::setSkyBox.
     * </p>
     * </li>
     * </ul>
     * @note Applies to both fixed-function and programmable pipeline.
     */
    public void setCubicTextureName(String name, boolean forUVW) {
        if (forUVW) {
            String[] nameList = new String[1];
            nameList[0] = name;
            setCubicTextureName(nameList, forUVW);
        } else {
            setContentType(ContentType.CONTENT_NAMED);
            mTextureLoadFailed = false;
            String[] fullNames = new String[6];

            for (int i = 0; i < 6; ++i) {
                fullNames[i] = name + suffixes[i];
            }
            setCubicTextureName(fullNames, forUVW);
        }

    }

    public void setTextureName(String name) {
        setTextureName(name, TextureType.TEX_TYPE_2D);
    }

    public void setTextureName(String name, TextureType type) {
        setContentType(ContentType.CONTENT_NAMED);
        mTextureLoadFailed = false;

        if (type == TextureType.TEX_TYPE_CUBE_MAP) {
            // delegate to cubic texture implementation
            setCubicTextureName(name, true);
        } else {
            mFrames.clear();
            mFramePtrs.clear();
            mFrames.add(name);
            //mFramePtrs.set(0, null);
            // defer load until used, so don't grab pointer yet
            mCurrentFrame = 0;
            mCubic = false;
            mTextureType = type;

            _load(); //Init the mFramesPtr basically

            if (ENG_Pass.getHashFunction() == ENG_Pass.getBuiltinHashFunction(
                    BuiltinHashFunction.MIN_TEXTURE_CHANGE)) {
                mParent._dirtyHash();
            }
        }
    }

    public String getTextureName() {
        if (mCurrentFrame < mFrames.size()) {
            return mFrames.get(mCurrentFrame);
        } else {
            return "";
        }
    }

    /**
     * Sets the names of the texture images for an animated texture.
     *
     * @param name      The base name of the textures to use e.g. wall.jpg for frames wall_0.jpg, wall_1.jpg etc.
     * @param numFrames The number of frames in the sequence.
     * @param duration  The length of time it takes to display the whole animation sequence, in seconds.
     *                  If 0, no automatic transition occurs.
     * @remarks Animated textures are just a series of images making up the frames of the animation. All the images
     * must be the same size, and their names must have a frame number appended before the extension, e.g.
     * if you specify a name of "wall.jpg" with 3 frames, the image names must be "wall_0.jpg", "wall_1.jpg"
     * and "wall_2.jpg".
     * @par You can change the active frame on a texture layer by calling the setCurrentFrame method.
     * @note If you can't make your texture images conform to the naming standard layed out here, you
     * can call the alternative setAnimatedTextureName method which takes an array of names instead.
     * @note Applies to both fixed-function and programmable pipeline.
     */
    public void setAnimatedTextureName(String name, int numFrames, float duration) {
        setContentType(ContentType.CONTENT_NAMED);
        mTextureLoadFailed = false;

        mFrames.ensureCapacity(numFrames);
        mFramePtrs.ensureCapacity(numFrames);
        mAnimDuration = duration;
        mCurrentFrame = 0;
        mCubic = false;

        for (int i = 0; i < numFrames; ++i) {
            String aheadZeros = ENG_Utility.getAheadZeros(i);
            mFrames.add(name + "_" + aheadZeros + i);
            // mFramePtrs.set(i, null);
        }
    }

    public void setAnimatedTextureName(String[] name, int numFrames, float duration) {
        setContentType(ContentType.CONTENT_NAMED);
        mTextureLoadFailed = false;

        mFrames.ensureCapacity(numFrames);
        mFramePtrs.ensureCapacity(numFrames);
        mAnimDuration = duration;
        mCurrentFrame = 0;
        mCubic = false;

        for (int i = 0; i < numFrames; ++i) {
            mFrames.set(i, name[i]);
            mFramePtrs.set(i, null);
        }
    }

    public void setCurrentFrame(int frameNumber) {
        if (frameNumber >= 0 && frameNumber < mFrames.size()) {
            mCurrentFrame = frameNumber;
        } else {
            throw new IllegalArgumentException(
                    "frameNumber paramter value exceeds number of stored frames. " +
                            "Framenumber is " + frameNumber + " and maximum frame number is" +
                            " " + mFrames.size());
        }
    }

    public int getCurrentFrame() {
        return mCurrentFrame;
    }

    public int getNumFrames() {
        return mFrames.size();
    }

    public String getFrameTextureName(int frameNumber) {
        if (frameNumber >= mFrames.size()) {
            throw new IllegalArgumentException(
                    "frameNumber paramter value exceeds number of stored frames.");
        }
        return mFrames.get(frameNumber);
    }

    public void setDesiredFormat(PixelFormat desiredFormat) {
        mDesiredFormat = desiredFormat;
    }

    public PixelFormat getDesiredFormat() {
        return mDesiredFormat;
    }

    public void setNumMipmaps(int numMipmaps) {
        mTextureSrcMipmaps = numMipmaps;
    }

    public int getNumMipmaps() {
        return mTextureSrcMipmaps;
    }

    public void setAlpha(boolean alpha) {
        mIsAlpha = alpha;
    }

    public boolean getAlpha() {
        return mIsAlpha;
    }

    public void setHardwareGammaEnabled(boolean g) {
        mHwGamma = g;
    }

    public boolean getHardwareGammaEnabled() {
        return mHwGamma;
    }

    public void setTextureCoordSet(int set) {
        mTextureCoordSetIndex = set;
    }

    public int getTextureCoordSet() {
        return mTextureCoordSetIndex;
    }

    public boolean isBlank() {
        return mFrames.isEmpty() || ((mFrames.get(0) == null) || (mTextureLoadFailed));
    }

    public boolean isTextureLoadFailing() {
        return mTextureLoadFailed;
    }

    public void retryTextureLoad() {
        mTextureLoadFailed = false;
    }

    public void setBlank() {
        setTextureName(null, TextureType.TEX_TYPE_2D);
    }

    public SceneBlendFactor getColourBlendFallbackSrc() {
        return mColourBlendFallbackSrc;
    }

    public SceneBlendFactor getColourBlendFallbackDest() {
        return mColourBlendFallbackDest;
    }

    public LayerBlendModeEx getColourBlendMode() {
        return mColourBlendMode;
    }

    public LayerBlendModeEx getAlphaBlendMode() {
        return mAlphaBlendMode;
    }

    public UVWAddressingMode getTextureAddressingMode() {
        return mAddressMode;
    }

    public void setBindingType(BindingType bt) {
        mBindingType = bt;
    }

    public BindingType getBindingType() {
        return mBindingType;
    }

    public boolean isCubic() {
        return mCubic;
    }

    public boolean is3D() {
        return mTextureType == TextureType.TEX_TYPE_3D;
    }

    public TextureType getTextureType() {
        return mTextureType;
    }

    public void setColourOperationEx(LayerBlendOperationEx op) {
        setColourOperationEx(op, LayerBlendSource.LBS_TEXTURE,
                LayerBlendSource.LBS_CURRENT
                , ENG_ColorValue.WHITE,
                ENG_ColorValue.WHITE, 0.0f);
    }

    public void setColourOperationEx(LayerBlendOperationEx op,
                                     LayerBlendSource source1,
                                     LayerBlendSource source2) {
        setColourOperationEx(op, source1, source2, ENG_ColorValue.WHITE,
                ENG_ColorValue.WHITE, 0.0f);
    }

    public void setColourOperationEx(LayerBlendOperationEx op,
                                     LayerBlendSource source1,
                                     LayerBlendSource source2,
                                     ENG_ColorValue arg1,
                                     ENG_ColorValue arg2,
                                     float manualBlend) {
        mColourBlendMode.operation = op;
        mColourBlendMode.source1 = source1;
        mColourBlendMode.source2 = source2;
        mColourBlendMode.colourArg1 = arg1;
        mColourBlendMode.colourArg2 = arg2;
        mColourBlendMode.factor = manualBlend;
    }

    public void setColourOpMultipassFallback(SceneBlendFactor sourceFactor,
                                             SceneBlendFactor destFactor) {
        mColourBlendFallbackSrc = sourceFactor;
        mColourBlendFallbackDest = destFactor;
    }

    public void setAlphaOperation(LayerBlendOperationEx op,
                                  LayerBlendSource source1,
                                  LayerBlendSource source2,
                                  float arg1,
                                  float arg2,
                                  float manualBlend) {
        mAlphaBlendMode.operation = op;
        mAlphaBlendMode.source1 = source1;
        mAlphaBlendMode.source2 = source2;
        mAlphaBlendMode.alphaArg1 = arg1;
        mAlphaBlendMode.alphaArg2 = arg2;
        mAlphaBlendMode.factor = manualBlend;
    }

    public void setColourOperation(LayerBlendOperation op) {
        // Set up the multitexture and multipass blending operations
        switch (op) {
            case LBO_REPLACE:
                setColourOperationEx(LayerBlendOperationEx.LBX_SOURCE1, LayerBlendSource.LBS_TEXTURE, LayerBlendSource.LBS_CURRENT);
                setColourOpMultipassFallback(SceneBlendFactor.SBF_ONE, SceneBlendFactor.SBF_ZERO);
                break;
            case LBO_ADD:
                setColourOperationEx(LayerBlendOperationEx.LBX_ADD, LayerBlendSource.LBS_TEXTURE, LayerBlendSource.LBS_CURRENT);
                setColourOpMultipassFallback(SceneBlendFactor.SBF_ONE, SceneBlendFactor.SBF_ONE);
                break;
            case LBO_MODULATE:
                setColourOperationEx(LayerBlendOperationEx.LBX_MODULATE, LayerBlendSource.LBS_TEXTURE, LayerBlendSource.LBS_CURRENT);
                setColourOpMultipassFallback(SceneBlendFactor.SBF_DEST_COLOUR, SceneBlendFactor.SBF_ZERO);
                break;
            case LBO_ALPHA_BLEND:
                setColourOperationEx(LayerBlendOperationEx.LBX_BLEND_TEXTURE_ALPHA, LayerBlendSource.LBS_TEXTURE, LayerBlendSource.LBS_CURRENT);
                setColourOpMultipassFallback(SceneBlendFactor.SBF_SOURCE_ALPHA, SceneBlendFactor.SBF_ONE_MINUS_SOURCE_ALPHA);
                break;
            default:
        }
    }

    public void setTextureAddressingMode(TextureAddressingMode tam) {
        mAddressMode.u = tam;
        mAddressMode.v = tam;
        mAddressMode.w = tam;
    }

    public void setTextureAddressingMode(TextureAddressingMode u,
                                         TextureAddressingMode v, TextureAddressingMode w) {
        mAddressMode.u = u;
        mAddressMode.v = v;
        mAddressMode.w = w;
    }

    public void setTextureAddressingMode(UVWAddressingMode uvw) {
        mAddressMode = uvw;
    }

    public void setTextureBorderColour(ENG_ColorValue border) {
        mBorderColour.set(border);
    }

    public ENG_ColorValue getTextureBorderColour() {
        return new ENG_ColorValue(mBorderColour);
    }

    public void getTextureColourValue(ENG_ColorValue ret) {
        ret.set(mBorderColour);
    }

    public ENG_ColorValue getTextureColourValue() {
        return mBorderColour;
    }

    public void setTextureFiltering(TextureFilterOptions filterType) {
        switch (filterType) {
            case TFO_NONE:
                setTextureFiltering(FilterOptions.FO_POINT, FilterOptions.FO_POINT, FilterOptions.FO_NONE);
                break;
            case TFO_BILINEAR:
                setTextureFiltering(FilterOptions.FO_LINEAR, FilterOptions.FO_LINEAR, FilterOptions.FO_POINT);
                break;
            case TFO_TRILINEAR:
                setTextureFiltering(FilterOptions.FO_LINEAR, FilterOptions.FO_LINEAR, FilterOptions.FO_LINEAR);
                break;
            case TFO_ANISOTROPIC:
                setTextureFiltering(FilterOptions.FO_ANISOTROPIC, FilterOptions.FO_ANISOTROPIC, FilterOptions.FO_LINEAR);
                break;
        }
        mIsDefaultFiltering = false;
    }

    public void setTextureFiltering(FilterType ft, FilterOptions fo) {
        switch (ft) {
            case FT_MIN:
                mMinFilter = fo;
                break;
            case FT_MAG:
                mMagFilter = fo;
                break;
            case FT_MIP:
                mMipFilter = fo;
                break;
        }
        mIsDefaultFiltering = false;
    }

    public void setTextureFiltering(FilterOptions minFilter,
                                    FilterOptions magFilter, FilterOptions mipFilter) {
        mMinFilter = minFilter;
        mMagFilter = magFilter;
        mMipFilter = mipFilter;
        mIsDefaultFiltering = false;
    }

    public FilterOptions getTextureFiltering(FilterType ft) {
        switch (ft) {
            case FT_MIN:
                return mIsDefaultFiltering ?
                        ENG_MaterialManager.getSingleton().getDefaultTextureFiltering(
                                FilterType.FT_MIN) : mMinFilter;
            case FT_MAG:
                return mIsDefaultFiltering ?
                        ENG_MaterialManager.getSingleton().getDefaultTextureFiltering(
                                FilterType.FT_MAG) : mMagFilter;
            case FT_MIP:
                return mIsDefaultFiltering ?
                        ENG_MaterialManager.getSingleton().getDefaultTextureFiltering(
                                FilterType.FT_MIP) : mMipFilter;
        }
        // to keep compiler happy
        return mMinFilter;
    }

    public void setTextureAnisotropy(int maxAniso) {
        mMaxAniso = maxAniso;
        mIsDefaultAniso = false;
    }

    public int getTextureAnisotropy() {
        return mIsDefaultAniso ?
                ENG_MaterialManager.getSingleton().getDefaultAnisotropy() : mMaxAniso;
    }

    public ENG_Pass getParent() {
        return mParent;
    }

    public void setName(String name) {
        mName = name;
        if (mTextureNameAlias == null) {
            mTextureNameAlias = name;
        }
    }

    public String getName() {
        return mName;
    }

    public void setTextureNameAlias(String name) {
        mTextureNameAlias = name;
    }

    public String getTextureNameAlias() {
        return mTextureNameAlias;
    }

    public void setTextureMipmapBias(float bias) {
        mMipmapBias = bias;
    }

    public float getTextureMipmapBias() {
        return mMipmapBias;
    }

    public ENG_Texture _getTexturePtr() {
        return _getTexturePtr(mCurrentFrame);
    }

    public ENG_Texture _getTexturePtr(int frame) {
        if ((frame < 0) || (frame >= mFramePtrs.size())) {
            throw new IllegalArgumentException(frame + " frame out of range 0 - " +
                    (mFramePtrs.size() - 1));
        }
        if (mContentType == ContentType.CONTENT_NAMED) {
            //May add in the future to ensureLoaded()
            return mFramePtrs.get(frame);
        } else {
            return mFramePtrs.get(frame);
        }
    }

    public ENG_Controller<ENG_ControllerTypeFloat> _getAnimController() {
        return mAnimController;
    }

    public void _notifyParent(ENG_Pass pass) {
        mParent = pass;
    }

    public void _load() {
        
        for (int i = 0; i < mFrames.size(); ++i) {
            ensureLoaded(i);
        }
        // Animation controller
        if (mAnimDuration != 0) {
            createAnimController();
        }
        for (ArrayList<TextureEffect> list : mEffects.values()) {
            for (TextureEffect t : list) {
                createEffectController(t);
            }
        }
    }

    private void createAnimController() {
        
        if (mAnimController != null) {
            ENG_ControllerManager.getSingleton().destroyController(mAnimController);
        }
        mAnimController = ENG_ControllerManager.getSingleton().createTextureAnimator(
                this, mAnimDuration);
    }

    public void _unload() {
        if (mAnimController != null) {
            ENG_ControllerManager.getSingleton().destroyController(mAnimController);
        }
        for (ArrayList<TextureEffect> effectList : mEffects.values()) {
            for (TextureEffect e : effectList) {
                ENG_ControllerManager.getSingleton().destroyController(e.controller);
            }
        }
        mFramePtrs.clear();
    }

    private void createEffectController(TextureEffect effect) {
        
        ENG_ControllerManager cMgr = ENG_ControllerManager.getSingleton();
        if (effect.controller != null) {
            cMgr.destroyController(effect.controller);
            effect.controller = null;
        }
        switch (effect.type) {
            case ET_UVSCROLL:
                effect.controller = cMgr.createTextureUVScroller(this, effect.arg1);
                break;
            case ET_USCROLL:
                effect.controller = cMgr.createTextureUScroller(this, effect.arg1);
                break;
            case ET_VSCROLL:
                effect.controller = cMgr.createTextureVScroller(this, effect.arg1);
                break;
            default:
                throw new UnsupportedOperationException("Not implemented yet!");
        }
    }

    private void ensureLoaded(int i) {
        
        if (!mFrames.get(i).isEmpty()) {
            ENG_Texture texture =
                    ENG_TextureManager.getSingleton().getByName(mFrames.get(i));
            if (!mFramePtrs.contains(texture)) {
                mFramePtrs.add(texture);
            }
        }
    }

    public void _prepare() {
        
        for (int i = 0; i < mFrames.size(); ++i) {
            ensurePrepared(i);
        }
    }

    private void ensurePrepared(int i) {
        

    }

    public void set(ENG_TextureUnitState tex) {
        
        mCurrentFrame = tex.mCurrentFrame;
        mAnimDuration = tex.mAnimDuration;
        mCubic = tex.mCubic;

        mTextureType = tex.mTextureType;
        mDesiredFormat = tex.mDesiredFormat;
        mTextureSrcMipmaps = tex.mTextureSrcMipmaps;

        mTextureCoordSetIndex = tex.mTextureCoordSetIndex;
        mAddressMode.set(tex.mAddressMode);
        mBorderColour.set(tex.mBorderColour);

        mColourBlendMode.set(tex.mColourBlendMode);
        mColourBlendFallbackSrc = tex.mColourBlendFallbackSrc;
        mColourBlendFallbackDest = tex.mColourBlendFallbackDest;

        mAlphaBlendMode.set(tex.mAlphaBlendMode);
        mTextureLoadFailed = tex.mTextureLoadFailed;
        mIsAlpha = tex.mIsAlpha;
        mHwGamma = tex.mHwGamma;

        mRecalcTexMatrix = tex.mRecalcTexMatrix;
        mUMod = tex.mUMod;
        mVMod = tex.mVMod;
        mUScale = tex.mUScale;
        mVScale = tex.mVScale;
        mRotate = tex.mRotate;
        mTexModMatrix.set(tex.mTexModMatrix);

        mMinFilter = tex.mMinFilter;
        mMagFilter = tex.mMagFilter;
        mMipFilter = tex.mMipFilter;
        mMaxAniso = tex.mMaxAniso;
        mMipmapBias = tex.mMipmapBias;

        mIsDefaultAniso = tex.mIsDefaultAniso;
        mIsDefaultFiltering = tex.mIsDefaultFiltering;
        mBindingType = tex.mBindingType;
        mContentType = tex.mContentType;
        mCompositorRefMrtIndex = tex.mCompositorRefMrtIndex;

        assert (mEffects.isEmpty());
        mFrames.clear();
        mFrames.addAll(tex.mFrames);
        mFramePtrs.clear();
        mFramePtrs.addAll(tex.mFramePtrs);
        mName = tex.mName;

        for (Entry<TextureEffectType, ArrayList<TextureEffect>> entry :
                tex.mEffects.entrySet()) {
            ArrayList<TextureEffect> list =
                    new ArrayList<>();
            for (TextureEffect t : entry.getValue()) {
                TextureEffect n = new TextureEffect();
                n.set(t);
                list.add(n);
            }
            mEffects.put(entry.getKey(), list);
        }

        mTextureNameAlias = tex.mTextureNameAlias;
        mCompositorRefName = tex.mCompositorRefTexName;
        mCompositorRefTexName = tex.mCompositorRefTexName;

        if (ENG_Pass.getHashFunction() == ENG_Pass.getBuiltinHashFunction(
                BuiltinHashFunction.MIN_TEXTURE_CHANGE)) {
            mParent._dirtyHash();
        }
    }

    public void setCompositorReference(String compositorName,
                                       String textureReference) {
        setCompositorReference(compositorName, textureReference, 0);
    }

    public void setCompositorReference(String compositorName, String textureReference,
                                       int mrtIndex) {
        mCompositorRefName = compositorName;
        mCompositorRefTexName = textureReference;
        mCompositorRefMrtIndex = mrtIndex;
    }

    public String getReferencedCompositorName() {
        return mCompositorRefName;
    }

    public String getReferencedTextureName() {
        return mCompositorRefTexName;
    }

    public int getReferencedMRTIndex() {
        return mCompositorRefMrtIndex;
    }

    public void _setTexturePtr(ENG_Texture refTex) {
        
        _setTexturePtr(refTex, mCurrentFrame);
    }

    public void _setTexturePtr(ENG_Texture refTex, int mCurrentFrame2) {
        
        if (mCurrentFrame2 >= mFramePtrs.size()) {
            throw new IllegalArgumentException("MframePtrs size is " +
                    mFramePtrs.size() + " while setting at position " +
                    mCurrentFrame2);
        }
        mFramePtrs.set(mCurrentFrame2, refTex);
    }

    public ENG_Matrix4 getTextureTransform() {
        
        if (mRecalcTexMatrix)
            recalcTextureMatrix();
        return mTexModMatrix;
    }

    private final ENG_Matrix4 xform = new ENG_Matrix4();

    private void recalcTextureMatrix() {
        
        mTexModMatrix.setIdentity();

        if (mUScale != 1.0f || mVScale != 1.0f) {
            mTexModMatrix.set(0, 0, 1.0f / mUScale);
            mTexModMatrix.set(1, 1, 1.0f / mVScale);
            mTexModMatrix.set(0, 3, (-0.5f * mTexModMatrix.get(0, 0)) + 0.5f);
            mTexModMatrix.set(1, 3, (-0.5f * mTexModMatrix.get(1, 1)) + 0.5f);
        }

        if (mUMod != 0.0f || mVMod != 0.0f) {
            ENG_Matrix4 xlate = new ENG_Matrix4();
            xlate.set(0, 3, mUMod);
            xlate.set(1, 3, mVMod);
            xlate.concatenate(mTexModMatrix, xform);
            mTexModMatrix.set(xform);


        }

        if (mRotate != 0.0f) {

            ENG_Matrix4 rot = new ENG_Matrix4();
            float cosTheta = ENG_Math.cos(mRotate);
            float sinTheta = ENG_Math.sin(mRotate);


            rot.set(0, 0, cosTheta);
            rot.set(0, 1, -sinTheta);
            rot.set(1, 0, sinTheta);
            rot.set(1, 1, cosTheta);

            rot.set(0, 3, 0.5f + ((-0.5f * cosTheta) - (-0.5f * sinTheta)));
            rot.set(1, 3, 0.5f + ((-0.5f * sinTheta) + (-0.5f * cosTheta)));

            rot.concatenate(mTexModMatrix, xform);
            mTexModMatrix.set(xform);


        }


        mRecalcTexMatrix = false;
    }

    public void setTextureUScroll(float value) {
        mUMod = value;
        mRecalcTexMatrix = true;
    }

    public void setTextureVScroll(float value) {
        mVMod = value;
        mRecalcTexMatrix = true;
    }

    public void setTextureUScale(float value) {
        mUScale = value;
        mRecalcTexMatrix = true;
    }

    public void setTextureVScale(float value) {
        mVScale = value;
        mRecalcTexMatrix = true;
    }

    public float getTextureUScroll() {
        return mUMod;
    }

    public float getTextureVScroll() {
        return mVMod;
    }

    public float getTextureUScale() {
        return mUScale;
    }

    public float getTextureVScale() {
        return mVScale;
    }

    public void addEffect(TextureEffect effect) {
        effect.controller = null;

        if (effect.type == TextureEffectType.ET_ENVIRONMENT_MAP
                || effect.type == TextureEffectType.ET_UVSCROLL
                || effect.type == TextureEffectType.ET_USCROLL
                || effect.type == TextureEffectType.ET_VSCROLL
                || effect.type == TextureEffectType.ET_ROTATE
                || effect.type == TextureEffectType.ET_PROJECTIVE_TEXTURE) {
            removeEffect(effect.type);
        }

        createEffectController(effect);

        ArrayList<TextureEffect> list = mEffects.get(effect.type);
        if (list == null) {
            list = new ArrayList<>();
            mEffects.put(effect.type, list);
        }
        list.add(effect);

    }

    public void removeEffect(TextureEffectType type) {
        ArrayList<TextureEffect> list = mEffects.get(type);
        if (list != null) {
            for (TextureEffect e : list) {
                ENG_ControllerManager.getSingleton().destroyController(
                        e.controller);
            }
            mEffects.remove(type);
        }
    }

    public void removeAllEffects() {
        for (Entry<TextureEffectType, ArrayList<TextureEffect>> entry :
                mEffects.entrySet()) {
            for (TextureEffect effect : entry.getValue()) {
                ENG_ControllerManager.getSingleton().destroyController(
                        effect.controller);
            }
        }
        mEffects.clear();
    }

    public void setScrollAnimation(float uSpeed, float vSpeed) {
        // Remove existing effects
        removeEffect(TextureEffectType.ET_UVSCROLL);
        removeEffect(TextureEffectType.ET_USCROLL);
        removeEffect(TextureEffectType.ET_VSCROLL);

        // don't create an effect if the speeds are both 0
        if (uSpeed == 0.0f && vSpeed == 0.0f) {
            return;
        }

        // Create new effect
        TextureEffect eff = new TextureEffect();
        if (uSpeed == vSpeed) {
            eff.type = TextureEffectType.ET_UVSCROLL;
            eff.arg1 = uSpeed;
            addEffect(eff);
        } else {
            if (uSpeed != 0.0f) {
                eff.type = TextureEffectType.ET_USCROLL;
                eff.arg1 = uSpeed;
                addEffect(eff);
            }
            if (vSpeed != 0.0f) {
                eff.type = TextureEffectType.ET_VSCROLL;
                eff.arg1 = vSpeed;
                addEffect(eff);
            }
        }
    }
}
