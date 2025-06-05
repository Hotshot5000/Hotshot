/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 4:55 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.renderer.ENG_CompositionPass.PassType;
import headwayent.hotshotengine.renderer.ENG_CompositionTargetPass.InputMode;
import headwayent.hotshotengine.renderer.ENG_CompositionTechnique.TextureScope;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.Usage;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;
import headwayent.hotshotengine.renderer.ENG_RenderQueue.RenderQueueGroupID;
import headwayent.hotshotengine.renderer.ENG_Texture.TextureUsage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

@Deprecated
public class ENG_CompositorManager {

//    private static ENG_CompositorManager compositorManager;

    private final HashMap<String, ENG_Compositor> compositors = new HashMap<>();

    private final HashMap<ENG_Viewport, ENG_CompositorChain> mChains = new HashMap<>();

    private ENG_Rectangle2D mRectangle;

    private final ArrayList<ENG_CompositorInstance> mInstances = new ArrayList<>();

    private final HashMap<String, ENG_CompositorLogic> mCompositorLogics = new HashMap<>();

    private final TreeMap<String, ENG_CustomCompositionPass> mCustomCompositionPasses = new TreeMap<>();

    private final TreeMap<TextureDef, ArrayList<ENG_Texture>> mTexturesByDef = new TreeMap<>();

    public static class StringPair implements Comparable<StringPair> {
        public String first, second;

        @Override
        public int compareTo(StringPair t) {
            int c1 = first.compareTo(t.first);
            int c2 = second.compareTo(t.second);

            if (c1 < 0 && c2 < 0) {
                return -1;
            } else if (c1 > 0 && c2 > 0) {
                return 1;
            }
            return 0;
        }
    }

    private final TreeMap<StringPair, TreeMap<TextureDef, ENG_Texture>> mChainTexturesByDef = new TreeMap<>();


    static class TextureDef implements Comparable<TextureDef> {

        public final int width;
        public final int height;
        public final PixelFormat format;
        public final int fsaa;
        public final String fsaaHint;
        public final boolean sRGBwrite;

        public TextureDef(int w, int h, PixelFormat f, int fs, String fhint,
                          boolean sRGB) {
            width = w;
            height = h;
            format = f;
            fsaa = fs;
            fsaaHint = fhint;
            sRGBwrite = sRGB;
        }

        @Override
        public int compareTo(TextureDef t) {


            if (this.format.getFormat() < t.format.getFormat())
                return -1;
            else if (this.format.getFormat() > t.format.getFormat())
                return 1;
            else {
                if (this.width < t.width)
                    return -1;
                else if (this.width > t.width) {
                    return 1;
                } else {
                    if (this.height < t.height)
                        return -1;
                    else if (this.height > t.height)
                        return 1;
                    else {
                        if (this.fsaa < t.fsaa)
                            return -1;
                        else if (this.fsaa > t.fsaa)
                            return 1;
                        else {
                            if (this.fsaaHint.compareTo(t.fsaaHint) < 0)
                                return -1;
                            else if (this.fsaaHint.compareTo(t.fsaaHint) > 0)
                                return 1;
                            else {
                                if (!this.sRGBwrite && t.sRGBwrite)
                                    return -1;
                                else if (this.sRGBwrite && !t.sRGBwrite) {
                                    return 1;
                                }
                            }

                        }
                    }
                }
            }
            return 0;
        }

    }

    private void freeChains() {
        for (ENG_CompositorChain ch : mChains.values()) {
            ch.destroy();
        }
        mChains.clear();
    }

    public ENG_CompositorManager() {
//        if (compositorManager == null) {
//            compositorManager = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
        initialise();
    }

    public ENG_Compositor create(String name) {
        return createImpl(name);
    }

    public void initialise() {

        /// Create "default" compositor
        /** Compositor that is used to implicitly represent the original
         render in the chain. This is an identity compositor with only an output pass:
         compositor Ogre/Scene
         {
         technique
         {
         target_output
         {
         pass clear
         {
         /// Clear frame
         }
         pass render_scene
         {
         visibility_mask FFFFFFFF
         render_queues SKIES_EARLY SKIES_LATE
         }
         }
         }
         };
         */

        ENG_Compositor scene = create("Ogre/Scene");
        ENG_CompositionTechnique technique = scene.createTechnique();
        ENG_CompositionTargetPass pass = technique.getOutputTargetPass();
        pass.setVisibilityMask(0xFFFFFFFF);

        ENG_CompositionPass clearPass = pass.createPass();
        clearPass.setType(PassType.PT_CLEAR);

        ENG_CompositionPass renderPass = pass.createPass();
        renderPass.setType(PassType.PT_RENDERSCENE);
        renderPass.setFirstRenderQueue(
                RenderQueueGroupID.RENDER_QUEUE_BACKGROUND.getID());
        renderPass.setLastRenderQueue(
                RenderQueueGroupID.RENDER_QUEUE_SKIES_LATE.getID());
        scene.load();
        compositors.put(scene.getName(), scene);
    }

    public void destroy() {
        destroy(false);
    }

    public void destroy(boolean skipGLDelete) {
        freeChains();
        freePooledTextures(false);
        if (mRectangle != null) {
            mRectangle.destroy(skipGLDelete);
            mRectangle = null;
        }
        compositors.clear();
    }

    public ENG_CompositorChain getCompositorChain(ENG_Viewport vp) {
        ENG_CompositorChain chain = mChains.get(vp);
        if (chain != null) {
            return chain;
        }
        chain = new ENG_CompositorChain(vp);
        mChains.put(vp, chain);
        return chain;
    }

    public boolean hasCompositorChain(ENG_Viewport vp) {
        return mChains.containsKey(vp);
    }

    public void removeCompositorChain(ENG_Viewport vp) {
        mChains.remove(vp);
    }

    public void removeAll() {
        freeChains();
    }


    public static ENG_CompositorManager getSingleton() {
//        if (compositorManager == null && MainApp.DEV) {
//            throw new NullPointerException("compositorManager == null");
//        }
//        return compositorManager;
        return MainApp.getGame().getRenderRoot().getCompositorManager();
    }

    public ENG_Rectangle2D _getTexturedRectangle2D() {

        if (mRectangle == null) {
            mRectangle =
                    new ENG_Rectangle2D(true,
                            Usage.HBU_DYNAMIC_WRITE_ONLY_DISCARDABLE.getUsage());

        }
        ENG_RenderSystem rs = ENG_RenderRoot.getRenderRoot().getRenderSystem();
        ENG_Viewport vp = rs._getViewport();
        float hOffset = rs.getHorizontalTexelOffset() / (0.5f * vp.getActualWidth());
        float vOffset = rs.getVerticalTexelOffset() / (0.5f * vp.getActualHeight());
        mRectangle.setCorners(-1 + hOffset, 1 - vOffset, 1 + hOffset, -1 - vOffset);
        return mRectangle;
    }

    public ENG_Compositor getByName(String name) {
        return compositors.get(name);
    }

    public ENG_CompositorInstance addCompositor(ENG_Viewport vp, String compositor) {
        return addCompositor(vp, compositor, -1);
    }

    public ENG_CompositorInstance addCompositor(ENG_Viewport vp,
                                                String compositor, int addPosition) {
        ENG_Compositor comp = getByName(compositor);
        if (comp == null) {
            return null;
        }
        ENG_CompositorChain chain = getCompositorChain(vp);
        return chain.addCompositor(comp, addPosition == -1 ?
                ENG_CompositorChain.LAST : addPosition, "");
    }

    public void removeCompositor(ENG_Viewport vp, String compositor) {
        ENG_CompositorChain chain = getCompositorChain(vp);
        for (int i = 0; i < chain.getNumCompositors(); ++i) {
            ENG_CompositorInstance instance = chain.getCompositor(i);
            if (instance.getCompositor().getName().equals(compositor)) {
                chain.removeCompositor(i);
                return;
            }
        }
    }

    public void setCompositorEnabled(ENG_Viewport vp,
                                     String compositor, boolean value) {
        ENG_CompositorChain chain = getCompositorChain(vp);
        for (int i = 0; i < chain.getNumCompositors(); ++i) {
            ENG_CompositorInstance instance = chain.getCompositor(i);
            if (instance.getCompositor().getName().equals(compositor)) {
                chain.setCompositorEnabled(i, value);
                return;
            }
        }
    }

    public void registerCustomCompositionPass(String name,
                                              ENG_CustomCompositionPass pass) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException(
                    "Custom composition pass name must not be empty.");
        }
        if (mCustomCompositionPasses.containsKey(name)) {
            throw new IllegalArgumentException(
                    "Custom composition pass  '" + name + "' already exists.");
        }
        mCustomCompositionPasses.put(name, pass);
    }

    public ENG_CustomCompositionPass getCustomCompositionPass(String name) {
        ENG_CustomCompositionPass pass = mCustomCompositionPasses.get(name);
        if (pass == null) {
            throw new IllegalArgumentException(
                    "Custom composition pass '" + name + "' not registered.");
        }
        return pass;
    }

    public ENG_Compositor load(String string) {

        ENG_Compositor comp = getByName(string);
        if (comp != null) {
            return comp;
        }
        comp = createImpl(string);
        comp.load();
        return comp;
    }

    /** @noinspection deprecation */
    public ENG_Texture getPooledTexture(String name, String localName,
                                        int width, int height, PixelFormat f, int aa, String aaHint,
                                        boolean srgb, ArrayList<ENG_Texture> texturesAssigned,
                                        ENG_CompositorInstance inst, TextureScope scope) {

        if (scope == TextureScope.TS_GLOBAL) {
            throw new IllegalArgumentException(
                    "Global scope texture can not be pooled.");
        }

        TextureDef def = new TextureDef(width, height, f, aa, aaHint, srgb);

        if (scope == TextureScope.TS_CHAIN) {
            StringPair pair = new StringPair();
            pair.first = inst.getCompositor().getName();
            pair.second = localName;
            TreeMap<TextureDef, ENG_Texture> defMap =
                    mChainTexturesByDef.get(pair);
            ENG_Texture list = defMap.get(def);
            if (list != null) {
                return list;
            }
            int len = ENG_Utility.getWidthAndHeight(width, height);
            ENG_Texture newTex = ENG_TextureManager.getSingleton().createManual(
                    name,
                    headwayent.hotshotengine.renderer.
                            ENG_Texture.TextureType.TEX_TYPE_2D, width, height,
                    1, 0, f, TextureUsage.TU_RENDERTARGET.getUsage(),
                    srgb, aa, aaHint);
            defMap.put(def, newTex);
            return newTex;
        }

        ArrayList<ENG_Texture> list = mTexturesByDef.get(def);
        ArrayList<ENG_Texture> texList = null;
        if (list == null) {
            texList = new ArrayList<>();
            mTexturesByDef.put(def, texList);
        }
        ENG_CompositorInstance previous =
                inst.getChain().getPreviousInstance(inst);
        ENG_CompositorInstance next =
                inst.getChain().getNextInstance(inst);

        ENG_Texture ret = null;

        for (ENG_Texture tex : texList) {
            if (texturesAssigned.contains(tex)) {
                boolean allowReuse = true;
                // ok, we didn't use this one already
                // however, there is an edge case where if we re-use a texture
                // which has an 'input previous' pass, and it is chained from another
                // compositor, we can end up trying to use the same texture for both
                // so, never allow a texture with an input previous pass to be
                // shared with its immediate predecessor in the chain
                if (isInputPreviousTarget(inst, localName)) {
                    // Check whether this is also an input to the output target of previous
                    // can't use CompositorInstance::mPreviousInstance, only set up
                    // during compile
                    if (previous != null && isInputToOutputTarget(previous, tex))
                        allowReuse = false;
                }
                // now check the other way around since we don't know what order they're bound in
                if (isInputToOutputTarget(inst, localName)) {

                    if (next != null && isInputPreviousTarget(next, tex))
                        allowReuse = false;
                }

                if (allowReuse) {
                    ret = tex;
                    break;
                }
            }
        }

        if (ret == null) {
            int len = ENG_Utility.getWidthAndHeight(width, height);
            ret = ENG_TextureManager.getSingleton().createManual(
                    name,
                    headwayent.hotshotengine.renderer
                            .ENG_Texture.TextureType.TEX_TYPE_2D,
                    width, height, 1, 0, f,
                    TextureUsage.TU_RENDERTARGET.getUsage(), srgb, aa, aaHint);
            texList.add(ret);
        }

        texturesAssigned.add(ret);
        return ret;
    }

    public boolean isInputPreviousTarget(ENG_CompositorInstance inst,
                                         ENG_Texture tex) {

        Iterator<ENG_CompositionTargetPass> iterator =
                inst.getTechnique().getTargetPassIterator();
        while (iterator.hasNext()) {
            ENG_CompositionTargetPass next = iterator.next();

            if (next.getInputMode() == InputMode.IM_PREVIOUS) {
                ENG_Texture t = inst.getTextureInstance(next.getOutputName(), 0);
                if (t != null && t == tex) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInputToOutputTarget(ENG_CompositorInstance inst,
                                         String localName) {

        ENG_CompositionTargetPass tp = inst.getTechnique().getOutputTargetPass();
        Iterator<ENG_CompositionPass> passIterator = tp.getPassIterator();
        while (passIterator.hasNext()) {
            ENG_CompositionPass next = passIterator.next();

            for (int i = 0; i < next.getNumInputs(); ++i) {
                if (next.getInput(i).name.equals(localName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInputToOutputTarget(ENG_CompositorInstance inst,
                                         ENG_Texture tex) {

        ENG_CompositionTargetPass tp = inst.getTechnique().getOutputTargetPass();
        Iterator<ENG_CompositionPass> passIterator = tp.getPassIterator();
        while (passIterator.hasNext()) {
            ENG_CompositionPass next = passIterator.next();

            for (int i = 0; i < next.getNumInputs(); ++i) {
                ENG_Texture t = inst.getTextureInstance(next.getInput(i).name, 0);
                if (t != null && t == tex) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInputPreviousTarget(ENG_CompositorInstance inst,
                                         String localName) {

        Iterator<ENG_CompositionTargetPass> iterator =
                inst.getTechnique().getTargetPassIterator();
        while (iterator.hasNext()) {
            ENG_CompositionTargetPass next = iterator.next();
            if (next.getInputMode() == InputMode.IM_PREVIOUS &&
                    next.getOutputName().equals(localName)) {
                return true;
            }
        }
        return false;
    }

    public void freePooledTextures() {
        freePooledTextures(true);
    }

    public void freePooledTextures(boolean onlyIfUnreferenced) {
        if (onlyIfUnreferenced) {
            for (Entry<TextureDef, ArrayList<ENG_Texture>> entry : mTexturesByDef.entrySet()) {
                ArrayList<ENG_Texture> value = entry.getValue();

                for (ENG_Texture t : value) {
                    throw new UnsupportedOperationException("add some ref count" +
                            " for resources");
                }
            }
            for (Entry<StringPair, TreeMap<TextureDef, ENG_Texture>> next : mChainTexturesByDef.entrySet()) {
                TreeMap<TextureDef, ENG_Texture> value = next.getValue();
                for (Entry<TextureDef, ENG_Texture> entry : value.entrySet()) {
                    throw new UnsupportedOperationException("add some ref count" +
                            " for resources");
                }
            }
        } else {
            mTexturesByDef.clear();
            mChainTexturesByDef.clear();
        }
    }

    public void registerCompositorLogic(String name, ENG_CompositorLogic logic) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException(
                    "Compositor logic name must not be empty.");
        }
        ENG_CompositorLogic logic2 = mCompositorLogics.get(name);
        if (logic2 != null) {
            throw new IllegalArgumentException(
                    "Compositor logic '" + name + "' already exists.");
        }
        mCompositorLogics.put(name, logic);
    }

    public ENG_CompositorLogic getCompositorLogic(String name) {
        ENG_CompositorLogic logic = mCompositorLogics.get(name);
        if (logic == null) {
            throw new IllegalArgumentException(
                    "Compositor logic '" + name + "' not registered.");
        }
        return logic;
    }

    public ENG_Compositor createImpl(String name) {
        ENG_Compositor compositor = new ENG_Compositor(name);
        ENG_Compositor put = compositors.put(name, compositor);
        if (put != null) {
            throw new IllegalArgumentException(name + " is already in the compositor" +
                    " list");
        }
        return compositor;
    }
}
