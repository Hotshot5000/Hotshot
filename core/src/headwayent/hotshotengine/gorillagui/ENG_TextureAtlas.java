/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import com.google.common.collect.TreeMultimap;
import headwayent.hotshotengine.*;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.renderer.ENG_BlendMode.SceneBlendType;
import headwayent.hotshotengine.renderer.*;
import headwayent.hotshotengine.renderer.ENG_Common.CullingMode;
import headwayent.hotshotengine.renderer.ENG_Common.FilterOptions;
import headwayent.hotshotengine.renderer.ENG_TextureUnitState.TextureAddressingMode;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_SlowCallExecutor;

import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.TreeMap;

public class ENG_TextureAtlas {

    private static class Texture {

        private long ptr;
        private final String name;
        private int width, height;

        public Texture(final String textureName, final String groupName) {
            this.name = textureName;
            ENG_SlowCallExecutor.execute(() -> {
                ptr = ENG_TextureAtlas.loadTexture(textureName, groupName);
                width = ENG_TextureAtlas.getTextureWidth(ptr);
                height = ENG_TextureAtlas.getTextureHeight(ptr);
                return 0;
            });

        }

        public int getHeight() {
            return height;
        }

        public String getName() {
            return name;
        }

        public int getWidth() {
            return width;
        }

        public long getPtr() {
            return ptr;
        }
    }

    private final String mName;

//    protected ENG_Texture mTexture;
    Texture mTexture;
    protected ENG_Material m2DMaterial, m3DMaterial;
    protected ENG_Pass m2DPass, m3DPass;
    protected final TreeMap<ENG_Integer, ENG_GlyphData> mGlyphData = new TreeMap<>();
    protected final TreeMap<String, ENG_Sprite> mSprites = new TreeMap<>();
    protected final TreeMap<String, ENG_NinePatch> mNinePatches = new TreeMap<>();
    protected final ENG_Vector2D mWhitePixel = new ENG_Vector2D();
    protected final ENG_Vector2D mInverseTextureSize = new ENG_Vector2D();
    protected final ENG_ColorValue[] mMarkupColour = new ENG_ColorValue[10];

    /**
     * For shader path so they can be loaded
     *
     * @param path
     */
    public static void setBasePath(String path) {
    }

    ENG_TextureAtlas(String name, String path) {
        
        mName = name.substring(0, name.lastIndexOf(".gorilla"));
        for (int i = 0; i < mMarkupColour.length; ++i) {
            mMarkupColour[i] = new ENG_ColorValue();
        }
        _reset();
        _load(name, path);
        _calculateCoordinates();
//        _create2DMaterial();
//        _create3DMaterial();
        _createNinePatches();
    }

    public String getName() {
        return mName;
    }

    public void destroy() {

    }

//    public ENG_Texture getTexture() {
//        return mTexture;
//    }

    public ENG_Material get2DMaterial() {
        return m2DMaterial;
    }

    public ENG_Material get3DMaterial() {
        return m3DMaterial;
    }

    public String get2DMaterialName() {
        return m2DMaterial.getName();
    }

    public String get3DMaterialName() {
        return m3DMaterial.getName();
    }

    public ENG_GlyphData getGlyphData(int index) {
        return getGlyphData(new ENG_Integer(index));
    }

    public ENG_GlyphData getGlyphData(ENG_Integer index) {
        return mGlyphData.get(index);
    }

    public ENG_Sprite getSprite(String name) {
        return mSprites.get(name);
    }

    public ENG_NinePatch getNinePatch(String name) {
        return mNinePatches.get(name);
    }

    public ENG_Vector2D getWhitePixel() {
        return new ENG_Vector2D(mWhitePixel);
    }

    public void getWhitePixel(ENG_Vector2D ret) {
        ret.set(mWhitePixel);
    }

    public float getWhitePixelX() {
        return mWhitePixel.x;
    }

    public float getWhitePixelY() {
        return mWhitePixel.y;
    }

    public void getTextureSize(ENG_Vector2D ret) {
        ret.set(mTexture.getWidth(), mTexture.getHeight());
    }

    public ENG_Vector2D getTextureSize() {
        return new ENG_Vector2D(mTexture.getWidth(), mTexture.getHeight());
    }

    public long getTexturePtr() {
        return mTexture.getPtr();
    }

    public float getInvTextureCoordsX() {
        return 1.0f / mTexture.getWidth();
    }

    public float getInvTextureCoordsY() {
        return 1.0f / mTexture.getHeight();
    }

    public ENG_Pass get2DPass() {
        return m2DPass;
    }

    protected void _load(String name, String path) {
        ENG_ConfigFile f = new ENG_ConfigFile();
        f.load(name, path, " ", true);

        Iterator<Entry<String, TreeMultimap<String, String>>> it = f.getSectionIterator();
        while (it.hasNext()) {
            Entry<String, TreeMultimap<String, String>> next = it.next();
            String secName = next.getKey().toLowerCase(Locale.US);
            TreeMultimap<String, String> settings = next.getValue();

            if (secName.equals("texture")) {
//                NavigableSet<String> navigableSet = settings.get("file");
//                String first = navigableSet.first();
//                navigableSet.remove(first);
//                navigableSet.add(first + "~" + path);
                _loadTexture(settings);
            } else if (secName.startsWith("font.")) {
                ENG_GlyphData glyphData = new ENG_GlyphData();
                mGlyphData.put(new ENG_Integer(Integer.parseInt(secName.substring(5))), glyphData);
                _loadGlyphs(settings, glyphData);
                _loadKerning(settings, glyphData);
                _loadVerticalOffsets(settings, glyphData);
            } else if (secName.equals("sprites")) {
                _loadSprites(settings);
            } else if (secName.equals("ninepatches")) {
                _loadNinePatches(settings);
            }
        }
    }

    protected void _loadNinePatches(TreeMultimap<String, String> settings) {
        for (Entry<String, String> entry : settings.entries()) {
            String nine_patch_name = entry.getKey();
            String data = entry.getValue();

            int comment = data.indexOf("#");
            if (comment != -1) {
                data = data.substring(0, comment);
            }

            String[] str_values = StringUtils.split(data, " ", 4);
            if (str_values.length != 4) {
                continue;
            }

            ENG_NinePatch ninePatch = new ENG_NinePatch();
            ninePatch.uvLeft = Integer.parseInt(str_values[0]);
            ninePatch.uvTop = Integer.parseInt(str_values[1]);
            ninePatch.spriteWidthInPixels = Integer.parseInt(str_values[2]);
            ninePatch.spriteHeightInPixels = Integer.parseInt(str_values[3]);

            mNinePatches.put(nine_patch_name, ninePatch);
        }
    }

    protected void _loadSprites(TreeMultimap<String, String> settings) {
        
        for (Entry<String, String> entry : settings.entries()) {
            String sprite_name = entry.getKey();
            String data = entry.getValue();

            int comment = data.indexOf("#");
            if (comment != -1) {
                data = data.substring(0, comment);
            }

            String[] str_values = StringUtils.split(data, " ", 4);
            if (str_values.length != 4) {
                continue;
            }

            ENG_Sprite sprite = new ENG_Sprite();
            sprite.uvLeft = Integer.parseInt(str_values[0]);
            sprite.uvTop = Integer.parseInt(str_values[1]);
            sprite.spriteWidth = Integer.parseInt(str_values[2]);
            sprite.spriteHeight = Integer.parseInt(str_values[3]);

            mSprites.put(sprite_name, sprite);
        }
    }

    protected void _loadVerticalOffsets(TreeMultimap<String, String> settings,
                                        ENG_GlyphData glyphData) {
        
        for (Entry<String, String> entry : settings.entries()) {
            String left_name = entry.getKey();
            String data = entry.getValue();
            left_name = left_name.toLowerCase(Locale.US);

            if (left_name.length() < 15 ||
                    !left_name.startsWith("verticaloffset_")) {
                continue;
            }

            int comment = data.indexOf("#");
            if (comment != -1) {
                data = data.substring(0, comment);
            }

            left_name = left_name.substring(15);
            int glyph_id = Integer.parseInt(left_name);

            glyphData.mGlyphs.get(glyph_id).verticalOffset = Integer.parseInt(data);
        }
    }

    protected void _loadKerning(TreeMultimap<String, String> settings,
                                ENG_GlyphData glyphData) {
        
        for (Entry<String, String> entry : settings.entries()) {
            String left_name = entry.getKey();
            String data = entry.getValue();
            left_name = left_name.toLowerCase(Locale.US);

            if (left_name.length() < 8 ||
                    !left_name.startsWith("kerning_")) {
                continue;
            }

            int comment = data.indexOf("#");
            if (comment != -1) {
                data = data.substring(0, comment);
            }

            left_name = left_name.substring(8);
            int left_glyph_id = Integer.parseInt(left_name);

            String[] str_values = StringUtils.split(data, " ", 2);
            if (str_values.length != 2) {
                continue;
            }
            int right_glyph_id = Integer.parseInt(str_values[0]);
            int kerning = Integer.parseInt(str_values[1]);

            glyphData.mGlyphs.get(right_glyph_id - glyphData.mRangeBegin)
                    .kerning.add(new ENG_Kerning(left_glyph_id, kerning));
        }
    }

    protected void _loadGlyphs(TreeMultimap<String, String> settings,
                               ENG_GlyphData glyphData) {
        
        ENG_Vector2D offset = new ENG_Vector2D(0.0f, 0.0f);
        float screenDensity = ENG_RenderRoot.getRenderRoot().getScreenDensity();
        for (Entry<String, String> entry : settings.entries()) {
            String name = entry.getKey();
            String data = entry.getValue();

            int comment = data.indexOf("#");
            if (comment != -1) {
                data = data.substring(0, comment);
            }
            name = name.toLowerCase(Locale.US);

            switch (name) {
                case "offset":
                    offset.set(ENG_StringConverter.parseVector2(data));
                    break;
                case "lineheight":
                    glyphData.mLineHeight = Float.parseFloat(data);
                    break;
                case "spacelength":
                    glyphData.mSpaceLength = Float.parseFloat(data);
                    break;
                case "baseline":
                    glyphData.mBaseline = Float.parseFloat(data);
                    break;
                case "monowidth":
                    glyphData.mMonoWidth = Float.parseFloat(data);
                    break;
                case "range":
                    ENG_Vector2D t = ENG_StringConverter.parseVector2(data);
                    glyphData.mRangeBegin = (int) t.x;
                    glyphData.mRangeEnd = (int) t.y;
                    break;
                case "letterspacing":
                    glyphData.mLetterSpacing = Float.parseFloat(data);
                    break;
            }
        }

        for (int index = glyphData.mRangeBegin;
             index <= glyphData.mRangeEnd; ++index) {
            ENG_Glyph glyph = new ENG_Glyph();
            glyphData.mGlyphs.add(glyph);
            String s = "glyph_" + index;
            NavigableSet<String> navigableSet = settings.get(s);
            if (navigableSet == null) {
                continue;
            }
            String[] str_values = StringUtils.split(navigableSet.first(), " ", 5);

            if (str_values.length < 4) {
                continue;
            }

            glyph.uvLeft = offset.x + Float.parseFloat(str_values[0]);
            glyph.uvTop = offset.y + Float.parseFloat(str_values[1]);
            glyph.uvWidth = Float.parseFloat(str_values[2]);// * screenDensity;
            glyph.uvHeight = Float.parseFloat(str_values[3]);// * screenDensity;
            glyph.uvRight = glyph.uvLeft + glyph.uvWidth;
            glyph.uvBottom = glyph.uvTop + glyph.uvHeight;

            if (str_values.length == 5) {
                glyph.glyphAdvance = Float.parseFloat(str_values[4]);
            } else {
                glyph.glyphAdvance = glyph.uvWidth;
            }
        }
    }

    protected void _loadTexture(TreeMultimap<String, String> settings) {
        
        for (Entry<String, String> entry : settings.entries()) {
            String name = entry.getKey();
            String data = entry.getValue();

            int comment = data.indexOf("#");
            if (comment != -1) {
                data = data.substring(0, comment);
            }
            name = name.toLowerCase(Locale.US);

            String textureName = data;
            String groupName = "";
            if (name.equals("file")) {
                int groupSplit = data.indexOf("~");
                if (groupSplit != -1) {
                    textureName = data.substring(0, groupSplit);
                    groupName = data.substring(groupSplit + 1);
                    textureName = textureName.trim();
                    groupName = groupName.trim();
                }
                mTexture = new Texture(textureName, groupName);
//                mTexture = ENG_TextureManager.getSingleton().getByName(data);
//                if (mTexture == null) {
//                    // We need the shadow buffer to read back when using nine patches
//                    // for getting the stretchable and drawable areas
//                    textureName = ENG_TextureLoader.loadTexture(textureName,
//                            groupName.isEmpty() ?
//                                    ENG_CompilerUtil.getBasePath() :
//                                    groupName, true,
//                            ENG_Texture.TextureUsage.TU_DEFAULT, true);
//                    mTexture = ENG_TextureManager.getSingleton().getByName(textureName);
//                }
                mInverseTextureSize.x = 1.0f / mTexture.getWidth();
                mInverseTextureSize.y = 1.0f / mTexture.getHeight();
            } else if (name.equals("whitepixel")) {
                mWhitePixel.set(ENG_StringConverter.parseVector2(data));
                mWhitePixel.x *= mInverseTextureSize.x;
                mWhitePixel.y *= mInverseTextureSize.y;
            }
        }
    }

    public ENG_Material createOrGet2DMasterMaterial() {
        ENG_Material material =
                ENG_MaterialManager.getSingleton().getByName("Gorilla2D");
        if (material != null) {
            ENG_Pass pass = material.getTechnique((short) 0).getPass((short) 0);
            if (pass.hasVertexProgram()) {
                pass.getVertexProgram().load();
            }
            if (pass.hasFragmentProgram()) {
                pass.getFragmentProgram().load();
            }
            return material;
        }
        material = ENG_MaterialManager.getSingleton().create("Gorilla2D");

        ENG_Pass pass = material.createTechnique().createPass();
        //material.getTechnique((short) 0).getPass((short) 0);
        pass.setCullingMode(CullingMode.CULL_NONE);
        pass.setDepthCheckEnabled(false);
        pass.setDepthWriteEnabled(false);
        pass.setLightingEnabled(false);
        pass.setSceneBlending(SceneBlendType.SBT_TRANSPARENT_ALPHA);
        ENG_TextureUnitState state = pass.createTextureUnitState();
        state.setTextureAddressingMode(TextureAddressingMode.TAM_CLAMP);
        state.setTextureFiltering(FilterOptions.FO_NONE,
                FilterOptions.FO_NONE, FilterOptions.FO_NONE);
        pass.setVertexProgram("raw/panel_gorilla_vs.txt");
        pass.setFragmentProgram("raw/panel_gorilla_fs.txt");
        return material;
    }

    public ENG_Material createOrGet3DMasterMaterial() {
        ENG_Material material =
                ENG_MaterialManager.getSingleton().getByName("Gorilla3D");
        if (material != null) {
            ENG_Pass pass = material.getTechnique((short) 0).getPass((short) 0);
            if (pass.hasVertexProgram()) {
                pass.getVertexProgram().load();
            }
            if (pass.hasFragmentProgram()) {
                pass.getFragmentProgram().load();
            }
            return material;
        }
        material = ENG_MaterialManager.getSingleton().create("Gorilla3D");
        ENG_Pass pass = material.createTechnique().createPass();
        //material.getTechnique((short) 0).getPass((short) 0);
        pass.setCullingMode(CullingMode.CULL_NONE);
        pass.setDepthCheckEnabled(false);
        pass.setDepthWriteEnabled(false);
        pass.setLightingEnabled(false);
        pass.setSceneBlending(SceneBlendType.SBT_TRANSPARENT_ALPHA);
        ENG_TextureUnitState state = pass.createTextureUnitState();
        state.setTextureAddressingMode(TextureAddressingMode.TAM_CLAMP);
        state.setTextureFiltering(FilterOptions.FO_ANISOTROPIC,
                FilterOptions.FO_ANISOTROPIC, FilterOptions.FO_ANISOTROPIC);
        pass.setVertexProgram("raw/panel_gorilla_vs.txt");
        pass.setFragmentProgram("raw/panel_gorilla_fs.txt");
        return material;
    }

    public void _create2DMaterial() {
        String matName = "Gorilla2D." + mTexture.getName();
        m2DMaterial = ENG_MaterialManager.getSingleton().getByName(matName);
        if (m2DMaterial == null) {
            m2DMaterial = createOrGet2DMasterMaterial().clone(matName);
        }
        m2DPass = m2DMaterial.getTechnique((short) 0).getPass((short) 0);
        m2DPass.getTextureUnitState(0).setTextureName(mTexture.getName());
    }

    public void _create3DMaterial() {
        String matName = "Gorilla3D." + mTexture.getName();
        m3DMaterial = ENG_MaterialManager.getSingleton().getByName(matName);
        if (m3DMaterial == null) {
            m3DMaterial = createOrGet2DMasterMaterial().clone(matName);
        }
        m3DPass = m3DMaterial.getTechnique((short) 0).getPass((short) 0);
        m3DPass.getTextureUnitState(0).setTextureName(mTexture.getName());
    }

    public void _calculateCoordinates() {
        ENG_RenderSystem rs = ENG_RenderRoot.getRenderRoot().getRenderSystem();
        float texelX = rs.getHorizontalTexelOffset();
        float texelY = rs.getVerticalTexelOffset();

        for (ENG_GlyphData gd : mGlyphData.values()) {
            for (ENG_Glyph g : gd.mGlyphs) {
                g.uvLeft -= texelX;
                g.uvTop -= texelY;
                g.uvRight += texelX;
                g.uvBottom += texelY;

                g.uvLeft *= mInverseTextureSize.x;
                g.uvTop *= mInverseTextureSize.y;
                g.uvRight *= mInverseTextureSize.x;
                g.uvBottom *= mInverseTextureSize.y;

                g.texCoords[ENG_QuadCorner.TopLeft.getCorner()].x = g.uvLeft;
                g.texCoords[ENG_QuadCorner.TopLeft.getCorner()].y = g.uvTop;
                g.texCoords[ENG_QuadCorner.TopRight.getCorner()].x = g.uvRight;
                g.texCoords[ENG_QuadCorner.TopRight.getCorner()].y = g.uvTop;
                g.texCoords[ENG_QuadCorner.BottomRight.getCorner()].x = g.uvRight;
                g.texCoords[ENG_QuadCorner.BottomRight.getCorner()].y = g.uvBottom;
                g.texCoords[ENG_QuadCorner.BottomLeft.getCorner()].x = g.uvLeft;
                g.texCoords[ENG_QuadCorner.BottomLeft.getCorner()].y = g.uvBottom;
                g.glyphWidth = g.uvWidth;
                g.glyphHeight = g.uvHeight;
            }
        }
        for (ENG_Sprite s : mSprites.values()) {


            _calculateSpriteCoordinates(s);
        }

        for (ENG_NinePatch ninePatch : mNinePatches.values()) {
            ninePatch.uvRight = ninePatch.uvLeft + ninePatch.spriteWidthInPixels;
            ninePatch.uvBottom = ninePatch.uvTop + ninePatch.spriteHeightInPixels;

            ninePatch.uvLeftInPixels = ninePatch.uvLeft;
            ninePatch.uvRightInPixels = ninePatch.uvRight;
            ninePatch.uvTopInPixels = ninePatch.uvTop;
            ninePatch.uvBottomInPixels = ninePatch.uvBottom;

            ninePatch.uvLeft *= mInverseTextureSize.x;
            ninePatch.uvTop *= mInverseTextureSize.y;
            ninePatch.uvRight *= mInverseTextureSize.x;
            ninePatch.uvBottom *= mInverseTextureSize.y;
        }
    }

    private void _calculateSpriteCoordinates(final ENG_Sprite s) {
        ENG_SlowCallExecutor.execute(() -> {
            calculateSpriteCoordinates(s, mInverseTextureSize.x, mInverseTextureSize.y);
            return 0;
        });

    }

    public void _createNinePatches() {
        for (ENG_NinePatch ninePatch : mNinePatches.values()) {
            _createNinePatch(ninePatch);
        }
    }

    private void _createNinePatch(final ENG_NinePatch ninePatch) {
        final int[] error = new int[1];
        ENG_SlowCallExecutor.execute(() -> {
            error[0] = createNinePatch(mTexture.getPtr(), ninePatch);
            return 0;
        });

        switch (error[0]) {
            case 0:
                break;
            default:
                throw new IllegalArgumentException("Nine patch " + mTexture.getName() + " invalid error: " + error[0]);
        }

    }

    private void extractColor(ENG_PixelBox currentLock, ENG_ColorValue col, int prevData) {

    }

    public void refreshMarkupColours() {
        mMarkupColour[0].setAsInt(255, 255, 255);
        mMarkupColour[1].setAsInt(0, 0, 0);
        mMarkupColour[2].setAsInt(204, 204, 204);
        mMarkupColour[3].setAsInt(254, 220, 129);
        mMarkupColour[4].setAsInt(254, 138, 129);
        mMarkupColour[5].setAsInt(123, 236, 110);
        mMarkupColour[6].setAsInt(44, 192, 171);
        mMarkupColour[7].setAsInt(199, 93, 142);
        mMarkupColour[8].setAsInt(254, 254, 254);
        mMarkupColour[9].setAsInt(13, 13, 13);
    }

    public void setMarkupColour(int ind, ENG_ColorValue c) {
        if (ind >= 0 && ind < mMarkupColour.length) {
            mMarkupColour[ind].set(c);
        }
    }

    public ENG_ColorValue getMarkupColour(int ind) {
        if (ind >= 0 && ind < mMarkupColour.length) {
            return new ENG_ColorValue(mMarkupColour[ind]);
        }
        return new ENG_ColorValue(ENG_ColorValue.WHITE);
    }

    public void getMarkupColour(int ind, ENG_ColorValue ret) {
        if (ind >= 0 && ind < mMarkupColour.length) {
            ret.set(mMarkupColour[ind]);
            return;
        }
        ret.set(ENG_ColorValue.WHITE);
    }

    public void _reset() {
        refreshMarkupColours();
    }

    private static native long loadTexture(String name, String group);
    private static native int getTextureWidth(long ptr);
    private static native int getTextureHeight(long ptr);
//    private static native String getTextureName(long ptr);
    private static native int createNinePatch(long ptr, ENG_NinePatch ninePatch);
    private static native void calculateSpriteCoordinates(ENG_Sprite s, float mInverseTextureSizeX, float mInverseTextureSizeY);
}
