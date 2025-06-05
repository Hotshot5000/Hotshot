/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_RealRect;
import headwayent.hotshotengine.ENG_StringIntefaceInterface;
import headwayent.hotshotengine.ENG_StringInterface;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.renderer.ENG_BlendMode.SceneBlendType;
import headwayent.hotshotengine.renderer.ENG_Common.FilterOptions;
import headwayent.hotshotengine.renderer.ENG_TextureUnitState.TextureAddressingMode;

import java.util.ArrayList;
import java.util.TreeMap;

public class ENG_Font implements ENG_StringIntefaceInterface {

    public enum FontType {
        /// Generated from a truetype (.ttf) font
        FT_TRUETYPE,
        /// Loaded from an image created by an artist
        FT_IMAGE
    }

    public static class GlyphInfo {
        public final ENG_Integer codePoint;
        public final ENG_RealRect uvRect;
        public float aspectRatio;

        public GlyphInfo(int codePoint, ENG_RealRect rect, float aspect) {
            this.codePoint = new ENG_Integer(codePoint);
            uvRect = new ENG_RealRect(rect);
            aspectRatio = aspect;
        }
    }

    /// The type of font
    protected FontType mType = FontType.FT_IMAGE;

    /// Source of the font (either an image name or a truetype font)
    protected String mSource;

    /// Size of the truetype font, in points
    protected float mTtfSize;
    /// Resolution (dpi) of truetype font
    protected int mTtfResolution;
    /// Max distance to baseline of this (truetype) font
    protected int mTtfMaxBearingY;

    protected final TreeMap<ENG_Integer, GlyphInfo> mCodePointMap =
            new TreeMap<>();

    public static class CodePointRange {
        public ENG_Integer first, second;
    }

    /// The material which is generated for this font
    protected ENG_Material mpMaterial;

    /// Texture pointer
    protected ENG_Texture mTexture;

    /// for TRUE_TYPE font only
    protected boolean mAntialiasColour;

    /// Range of code points to generate glyphs for (truetype only)
    protected ArrayList<CodePointRange> mCodePointRangeList =
            new ArrayList<>();

    protected final String mName;

    protected void loadImpl() {
        mpMaterial = ENG_MaterialManager.getSingleton().getByName(mName);
        if (mpMaterial == null) {
            throw new IllegalArgumentException(mName + " not valid name");
        }

        boolean blendByAlpha;

        mTexture = ENG_TextureManager.getSingleton().getByName(mSource);

        if (mTexture == null) {
            throw new IllegalArgumentException(mSource + " not valid texture name");
        }
        blendByAlpha = mTexture.hasAlpha();
        ENG_TextureUnitState texLayer = mpMaterial.getTechnique((short) 0)
                .getPass((short) 0).createTextureUnitState(mSource, (short) 0);

        texLayer.setTextureAddressingMode(TextureAddressingMode.TAM_CLAMP);
        texLayer.setTextureFiltering(FilterOptions.FO_LINEAR,
                FilterOptions.FO_LINEAR, FilterOptions.FO_NONE);

        if (blendByAlpha) {
            mpMaterial.setSceneBlending(SceneBlendType.SBT_TRANSPARENT_ALPHA);
        } else {
            mpMaterial.setSceneBlending(SceneBlendType.SBT_ADD);
        }
    }

    public ENG_Font(String name) {
        mName = name;
    }

    private final ENG_StringInterface stringInterface = new ENG_StringInterface(this);

    @Override
    public ENG_StringInterface getStringInterface() {
        
        return stringInterface;
    }

    private boolean mLoaded;

    public void load() {
        if (!mLoaded) {
            mLoaded = true;
            loadImpl();
        }
    }

    public void setType(FontType type) {
        mType = type;
    }

    public FontType getType() {
        return mType;
    }

    public void setSource(String source) {
        mSource = source;
    }

    public String getSource() {
        return mSource;
    }

    public ENG_RealRect getGlyphTexCoords(int codePoint) {
        return mCodePointMap.get(new ENG_Integer(codePoint)).uvRect;
    }

    public void setGlyphTexCoords(int codePoint, float u1, float v1,
                                  float u2, float v2, float textureAspect) {
        GlyphInfo info = mCodePointMap.get(new ENG_Integer(codePoint));
        if (info != null) {
            info.codePoint.setValue(codePoint);
            info.uvRect.set(u1, v1, u2, v2);
            info.aspectRatio = textureAspect * (u2 - u1) / (v2 - v1);
        } else {
            mCodePointMap.put(new ENG_Integer(codePoint), new GlyphInfo(codePoint,
                    new ENG_RealRect(u1, v1, u2, v2),
                    textureAspect * (u2 - u1) / (v2 - v1)));
        }
    }

    public float getGlyphAspectRatio(int codePoint) {
        GlyphInfo info = mCodePointMap.get(new ENG_Integer(codePoint));
        if (info != null) {
            return info.aspectRatio;
        }
        return 1.0f;
    }

    public void setGlyphAspectRatio(int codePoint, float aspect) {
        GlyphInfo info = mCodePointMap.get(new ENG_Integer(codePoint));
        if (info != null) {
            info.aspectRatio = aspect;
        }
    }

    public ENG_Material getMaterial() {
        return mpMaterial;
    }

    public String getName() {
        
        return mName;
    }
}
