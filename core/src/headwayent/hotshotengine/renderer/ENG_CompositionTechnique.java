/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;
import headwayent.hotshotengine.renderer.ENG_Texture.TextureType;
import headwayent.hotshotengine.renderer.ENG_Texture.TextureUsage;

import java.util.ArrayList;
import java.util.Iterator;

public class ENG_CompositionTechnique {

    //The scope of a texture defined by the compositor
    public enum TextureScope {
        //Local texture - only available to the compositor passes in this technique
        TS_LOCAL,
        //Chain texture - available to the other compositors in the chain
        TS_CHAIN,
        //Global texture - available to everyone in every scope
        TS_GLOBAL
    }

    public static class TextureDefinition implements Comparable<TextureDefinition> {
        public String name;
        //Texture definition being a reference is determined by these two fields not being empty.
        public final String refCompName = ""; //If a reference, the name of the compositor being referenced
        public final String refTexName = "";    //If a reference, the name of the texture in the compositor being referenced
        public int width;       // 0 means adapt to target width
        public int height;      // 0 means adapt to target height
        public float widthFactor = 1.0f;  // multiple of target width to use (if width = 0)
        public float heightFactor = 1.0f; // multiple of target height to use (if height = 0)
        public final ArrayList<PixelFormat> formatList =
                new ArrayList<>(); // more than one means MRT
        public boolean fsaa = true;            // FSAA enabled; true = determine from main target (if render_scene), false = disable
        public boolean hwGammaWrite;    // Do sRGB gamma correction on write (only 8-bit per channel formats)
        public boolean pooled;        // whether to use pooled textures for this one
        public TextureScope scope = TextureScope.TS_LOCAL; // Which scope has access to this texture


        @Override
        public int compareTo(TextureDefinition arg0) {
            
            return this.name.compareTo(arg0.name);
        }
    }

    /// Parent compositor
    private final ENG_Compositor mParent;
    /// Local texture definitions
    private final ArrayList<TextureDefinition> mTextureDefinitions =
            new ArrayList<>();

    /// Intermediate target passes
    private final ArrayList<ENG_CompositionTargetPass> mTargetPasses =
            new ArrayList<>();
    /// Output target pass (can be only one)
    private final ENG_CompositionTargetPass mOutputTarget;

    /// Optional scheme name
    private String mSchemeName = "";

    /// Optional compositor logic name
    private String mCompositorLogicName = "";

    public ENG_CompositionTechnique(ENG_Compositor parent) {
        mParent = parent;
        mOutputTarget = new ENG_CompositionTargetPass(this);
    }

    public TextureDefinition createTextureDefinition(String name) {
        TextureDefinition t = new TextureDefinition();
        t.name = name;
        mTextureDefinitions.add(t);
        return t;
    }

    public void removeTextureDefinition(int index) {
        if (index < 0 || index >= mTextureDefinitions.size()) {
            throw new IllegalArgumentException("index out of bounds " + index +
                    ". Texture Definition size: " + mTextureDefinitions.size());
        }
        mTextureDefinitions.remove(index);
    }

    public TextureDefinition getTextureDefinition(int index) {
        if (index < 0 || index >= mTextureDefinitions.size()) {
            throw new IllegalArgumentException("index out of bounds " + index +
                    ". Texture Definition size: " + mTextureDefinitions.size());
        }
        return mTextureDefinitions.get(index);
    }

    public TextureDefinition getTextureDefinition(String name) {
        for (TextureDefinition t : mTextureDefinitions) {
            if (t.name.equals(name)) {
                return t;
            }
        }
        return null;
    }

    public int getNumTextureDefinitions() {
        return mTextureDefinitions.size();
    }

    public void removeAllTextureDefinitions() {
        mTextureDefinitions.clear();
    }

    public Iterator<TextureDefinition> getTextureDefinitionIterator() {
        return mTextureDefinitions.iterator();
    }

    public ENG_CompositionTargetPass createTargetPass() {
        ENG_CompositionTargetPass pass = new ENG_CompositionTargetPass(this);
        mTargetPasses.add(pass);
        return pass;
    }

    public void removeTargetPass(int index) {
        if (index < 0 || index >= mTargetPasses.size()) {
            throw new IllegalArgumentException("index out of range: " + index +
                    ". TargetPasses size: " + mTargetPasses.size());
        }
        mTargetPasses.remove(index);
    }

    public ENG_CompositionTargetPass getTargetPass(int index) {
        if (index < 0 || index >= mTargetPasses.size()) {
            throw new IllegalArgumentException("index out of range: " + index +
                    ". TargetPasses size: " + mTargetPasses.size());
        }
        return mTargetPasses.get(index);
    }

    public void removeAllTargetPasses() {
        mTargetPasses.clear();
    }

    public Iterator<ENG_CompositionTargetPass> getTargetPassIterator() {
        return mTargetPasses.iterator();
    }

    public ENG_CompositionTargetPass getOutputTargetPass() {
        return mOutputTarget;
    }

    public boolean isSupported(boolean acceptTextureDegradation) {

        // A technique is supported if all materials referenced have a supported
        // technique, and the intermediate texture formats requested are supported
        // Material support is a cast-iron requirement, but if no texture formats
        // are directly supported we can let the rendersystem create the closest
        // match for the least demanding technique


        // Check output target pass is supported
        if (!mOutputTarget._isSupported()) {
            return false;
        }

        for (ENG_CompositionTargetPass pass : mTargetPasses) {
            if (!pass._isSupported()) {
                return false;
            }
        }

        for (TextureDefinition t : mTextureDefinitions) {
            if (t.formatList.size() >
                    ENG_RenderRoot.getRenderRoot().getRenderSystem()
                            .getCapabilities().getmNumMultiRenderTargets()) {
                return false;
            }

            for (PixelFormat f : t.formatList) {
                // Check whether equivalent supported
                if (acceptTextureDegradation) {
                    if (ENG_TextureManager.getSingleton().getNativeFormat(
                            TextureType.TEX_TYPE_2D, f,
                            TextureUsage.TU_RENDERTARGET.getUsage()) ==
                            PixelFormat.PF_UNKNOWN) {
                        return false;
                    }
                } else {
                    if (ENG_TextureManager.getSingleton().getNativeFormat(
                            TextureType.TEX_TYPE_2D, f,
                            TextureUsage.TU_RENDERTARGET.getUsage()) == null) {
                        return false;
                    }
                }
            }
        }

        // Must be ok
        return true;
    }

    public ENG_Compositor getParent() {
        return mParent;
    }

    public void setSchemeName(String scheme) {
        mSchemeName = scheme;
    }

    public String getSchemeName() {
        return mSchemeName;
    }

    public void setCompositorLogicName(String compositorLogicName) {
        mCompositorLogicName = compositorLogicName;
    }

    public String getCompositorLogicName() {
        return mCompositorLogicName;
    }

    public void destroy() {
        removeAllTextureDefinitions();
        removeAllTargetPasses();
    }
}
