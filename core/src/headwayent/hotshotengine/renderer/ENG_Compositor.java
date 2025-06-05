/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.renderer.ENG_CompositionTechnique.TextureDefinition;
import headwayent.hotshotengine.renderer.ENG_CompositionTechnique.TextureScope;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;
import headwayent.hotshotengine.renderer.ENG_Texture.TextureType;
import headwayent.hotshotengine.renderer.ENG_Texture.TextureUsage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

public class ENG_Compositor {

    private final ArrayList<ENG_CompositionTechnique> mTechniques = new ArrayList<>();
    private final ArrayList<ENG_CompositionTechnique> mSupportedTechniques = new ArrayList<>();

    /// Compilation required
    /// This is set if the techniques change and the supportedness of techniques has to be
    /// re-evaluated.
    private boolean mCompilationRequired = true;

    private final TreeMap<String, ENG_Texture> mGlobalTextures = new TreeMap<>();
    private final TreeMap<String, ENG_MultiRenderTarget> mGlobalMRTs = new TreeMap<>();

    private static int dummyCounter;

    private final String mName;

    /**
     * Create global rendertextures.
     * @noinspection deprecation
     */
    private void createGlobalTextures() {
        if (mSupportedTechniques.isEmpty()) {
            return;
        }

        TreeSet<String> globalTextureNames = new TreeSet<>();

        ENG_CompositionTechnique firstTechnique = mSupportedTechniques.get(0);

        Iterator<TextureDefinition> texDefIt =
                firstTechnique.getTextureDefinitionIterator();

        while (texDefIt.hasNext()) {
            TextureDefinition def = texDefIt.next();

            if (def.scope == TextureScope.TS_GLOBAL) {
                if (!def.refTexName.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Global compositor texture definition " +
                                    "can not be a reference");
                }
                if (def.width == 0 || def.height == 0) {
                    throw new IllegalArgumentException(
                            "Global compositor texture definition " +
                                    "must have absolute size");
                }
                if (def.pooled) {
                    System.out.println(
                            "Pooling global compositor textures has no effect");
                }
                globalTextureNames.add(def.name);

                ENG_RenderTarget rendTarget = null;
                if (def.formatList.size() > 1) {
                    String MRTbaseName = "c" + (dummyCounter++) +
                            "/" + mName + "/" + def.name;

                    ENG_MultiRenderTarget mrt =
                            ENG_RenderRoot.getRenderRoot().getRenderSystem()
                                    .createMultiRenderTarget(MRTbaseName);
                    mGlobalMRTs.put(def.name, mrt);

                    // create and bind individual surfaces
                    int atch = 0;
                    for (PixelFormat pf : def.formatList) {
                        ++atch;
                        String texname = MRTbaseName + "/" + atch;
                        int len = ENG_Utility.getWidthAndHeight(def.width, def.height);
                        ENG_Texture tex = ENG_TextureManager.getSingleton()
                                .createManual(texname,
                                        TextureType.TEX_TYPE_2D, def.width, def.height,
                                        1, 0, pf, TextureUsage.TU_RENDERTARGET.getUsage(),
                                        def.hwGammaWrite && !ENG_PixelUtil.isFloatingPoint(pf),
                                        def.fsaa ? 1 : 0, "");

                        ENG_RenderTexture rt = tex.getBuffer().getRenderTarget();
                        rt.setAutoUpdated(false);
                        mrt.bindSurface(atch, rt);

                        // Also add to local textures so we can look up
                        String mrtLocalName = getMRTTexLocalName(def.name, atch);
                        mGlobalTextures.put(mrtLocalName, tex);
                    }
                } else {
                    String texName = "c" + (dummyCounter++) +
                            "/" + mName + "/" + def.name;
                    texName = texName.replace(" ", "_");
                    int len = ENG_Utility.getWidthAndHeight(def.width, def.height);
                    ENG_Texture tex = ENG_TextureManager.getSingleton()
                            .createManual(texName,
                                    TextureType.TEX_TYPE_2D, len, len,
                                    1, 0, def.formatList.get(0),
                                    TextureUsage.TU_RENDERTARGET.getUsage(),
                                    def.hwGammaWrite &&
                                            !ENG_PixelUtil.isFloatingPoint(def.formatList.get(0)),
                                    def.fsaa ? 1 : 0, "");

                    rendTarget = tex.getBuffer().getRenderTarget();
                    mGlobalTextures.put(def.name, tex);
                }
            }
        }

        for (int i = 1; i < mSupportedTechniques.size(); ++i) {
            ENG_CompositionTechnique technique = mSupportedTechniques.get(i);
            boolean isConsistent = true;
            int numGlobals = 0;
            Iterator<TextureDefinition> defIt =
                    technique.getTextureDefinitionIterator();
            while (defIt.hasNext()) {
                TextureDefinition texDef = defIt.next();
                if (texDef.scope == TextureScope.TS_GLOBAL) {
                    if (!globalTextureNames.contains(texDef.name)) {
                        isConsistent = false;
                        break;
                    }
                    ++numGlobals;
                }

            }
            if (numGlobals != globalTextureNames.size()) {
                isConsistent = false;
            }
            if (!isConsistent) {
                throw new IllegalArgumentException(
                        "Different composition techniques define " +
                                "different global textures");
            }
        }
    }

    private String getMRTTexLocalName(String baseName, int atch) {

        return baseName + "/" + atch;
    }

    /**
     * Destroy global rendertextures.
     */
    private void freeGlobalTextures() {
        mGlobalMRTs.clear();
        mGlobalTextures.clear();
    }

    public ENG_Compositor(String name) {
        mName = name;
    }

    public void destroy() {
        removeAllTechniques();
    }

    protected void loadImpl() {
        if (mCompilationRequired) {
            compile();
        }
        createGlobalTextures();
    }

    protected void compile() {

        mSupportedTechniques.clear();
        for (ENG_CompositionTechnique t : mTechniques) {
            if (t.isSupported(false)) {
                mSupportedTechniques.add(t);
            }

        }

        if (mSupportedTechniques.isEmpty()) {
            for (ENG_CompositionTechnique t : mTechniques) {
                if (t.isSupported(true)) {
                    mSupportedTechniques.add(t);
                }

            }
        }

        mCompilationRequired = false;
    }

    public ENG_CompositionTechnique createTechnique() {
        ENG_CompositionTechnique t = new ENG_CompositionTechnique(this);
        mTechniques.add(t);
        mCompilationRequired = true;
        return t;
    }

    public void removeTechnique(int index) {
        if (index < 0 || index >= mTechniques.size()) {
            throw new IllegalArgumentException("Index out of bounds: " + index +
                    ". Techniques size: " + mTechniques.size());
        }
        mTechniques.remove(index);
        mSupportedTechniques.clear();
        mCompilationRequired = true;
    }

    public ENG_CompositionTechnique getTechnique(int index) {
        if (index < 0 || index >= mTechniques.size()) {
            throw new IllegalArgumentException("Index out of bounds: " + index +
                    ". Techniques size: " + mTechniques.size());
        }
        return mTechniques.get(index);
    }

    public int getNumTechniques() {
        return mTechniques.size();
    }

    public void removeAllTechniques() {
        for (ENG_CompositionTechnique tech : mTechniques) {
            tech.destroy();
        }
        mTechniques.clear();
        mSupportedTechniques.clear();
        mCompilationRequired = true;
    }

    public Iterator<ENG_CompositionTechnique> getTechniqueIterator() {
        return mTechniques.iterator();
    }

    public ENG_CompositionTechnique getSupportedTechnique(int index) {
        if (index < 0 || index >= mSupportedTechniques.size()) {
            throw new IllegalArgumentException("Index out of bounds: " + index +
                    ". Supported Techniques size: " + mSupportedTechniques.size());
        }
        return mSupportedTechniques.get(index);
    }

    public int getNumSupportedTechniques() {
        return mSupportedTechniques.size();
    }

    public Iterator<ENG_CompositionTechnique> getSupportedTechniqueIterator() {
        return mSupportedTechniques.iterator();
    }

    public ENG_CompositionTechnique getSupportedTechnique() {
        return getSupportedTechnique("");
    }

    public ENG_CompositionTechnique getSupportedTechnique(String schemeName) {
        for (ENG_CompositionTechnique t : mSupportedTechniques) {
            if (t.getSchemeName().equals(schemeName)) {
                return t;
            }
        }
        for (ENG_CompositionTechnique t : mSupportedTechniques) {
            if (t.getSchemeName().isEmpty()) {
                return t;
            }
        }
        return null;
    }

    public void load() {
        loadImpl();
    }

    public String getName() {

        return mName;
    }

    public ENG_Texture getTextureInstance(String name, int mrtIndex) {

        return null;
    }

    public ENG_RenderTarget getRenderTarget(String name) {

        return null;
    }

    public String getTextureInstanceName(String refTexName, int mrtIndex) {

        return getTextureInstance(refTexName, mrtIndex).getName();
    }
}
