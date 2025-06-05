/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 10:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.renderer.ENG_FrameEvent;
import headwayent.hotshotengine.renderer.ENG_FrameListener;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_Viewport;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_RenderingThread;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ENG_SilverBack extends ENG_FrameListener {

//    private static ENG_SilverBack s;

    private static final int BUFFER_SIZE = 9 * 4 * 1024 * 64; // 9 float elements per vertex.

    protected final TreeMap<String, ENG_TextureAtlas> mAtlases = new TreeMap<>();
    protected final ArrayList<ENG_Screen> mScreens = new ArrayList<>();
    protected final ArrayList<ENG_ScreenRenderable> mScreenRenderables = new ArrayList<>();
    protected final ArrayList<ENG_ScreenNative> mScreenNativeList = new ArrayList<>();
    protected final ByteBuffer[] bufferList;

    public ENG_SilverBack() {

//        if (s == null) {
//            s = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
        bufferList = ENG_Utility.allocateDirectMemoryAligned(BUFFER_SIZE, ENG_RenderingThread.BUFFER_COUNT
                * ENG_Container.MAX_LAYER_NUM * ENG_Container.TEXTURE_ATLAS_NUM);
        ENG_RenderRoot.getRenderRoot().addFrameListener(this);
        setMaterialName("Core/Gorilla2D");
//        ENG_SlowCallExecutor.execute(new ENG_ISlowCall() {
//            @Override
//            public long execute() {
                createDummyNode();
//                return 0;
//            }
//        });

    }

    public void destroy(boolean skipGLDelete) {
        ENG_RenderRoot.getRenderRoot().removeFrameListener(this);
        for (ENG_Screen scr : mScreens) {
            scr.destroy(skipGLDelete);
        }
        for (ENG_ScreenRenderable scr : mScreenRenderables) {
            scr.mLayerContainer.destroy(skipGLDelete);
        }
//        ENG_SlowCallExecutor.execute(new ENG_ISlowCall() {
//            @Override
//            public long execute() {
                destroyDummyNode();
//                return 0;
//            }
//        });

    }

    public ByteBuffer[] getBufferList() {
        return bufferList;
    }

    public int getCurrentBufferListStart() {
        return ENG_RenderingThread.getCurrentWriteableBuffer();
    }

    /**
     * @param name without extension!
     * @param path
     */
    public void loadAtlas(String name, String path) {
        if (mAtlases.containsKey(name + ".gorilla")) {
            return;
        }
        ENG_TextureAtlas textureAtlas = new ENG_TextureAtlas(name + ".gorilla", path);
        mAtlases.put(name + ".gorilla", textureAtlas);
    }

    public void unloadAtlas(String name) {
        ENG_TextureAtlas atlas = mAtlases.get(name + ".gorilla");
        if (atlas == null) {
            throw new IllegalArgumentException(name + " of texture atlas not found");
        }
        atlas.destroy();
    }

    public void unloadAllAtlases() {
        for (ENG_TextureAtlas atlas : mAtlases.values()) {
            atlas.destroy();
        }
        mAtlases.clear();
    }

    /** @noinspection deprecation*/
    @Deprecated
    public ENG_Screen createScreen(ENG_Viewport vp, String atlasName) {
        ArrayList<String> list = new ArrayList<>();
        list.add(atlasName);
        return createScreen(vp, list);
    }

    @Deprecated
    public ENG_Screen createScreen(ENG_Viewport vp, List<String> atlasNameList) {
        ArrayList<ENG_TextureAtlas> atlases = getTextureAtlases(atlasNameList);
        ENG_Screen screen = new ENG_Screen(vp, atlases);
        mScreens.add(screen);
        return screen;
    }

    public ArrayList<ENG_TextureAtlas> getTextureAtlases(List<String> atlasNameList) {
        ArrayList<ENG_TextureAtlas> atlases = new ArrayList<>();
        for (String atlasName : atlasNameList) {
            atlases.add(mAtlases.get(atlasName + ".gorilla"));
        }
        return atlases;
    }

    @Deprecated
    public ENG_ScreenRenderable createScreenRenderable(ENG_Vector2D maxSize,
                                                       String atlasName) {
        ENG_ScreenRenderable scr = new ENG_ScreenRenderable(maxSize, mAtlases.get(atlasName + ".gorilla"));
        mScreenRenderables.add(scr);
        return scr;
    }

    public ENG_ScreenNative createScreenNative(List<String> atlasNameList) {
        ArrayList<ENG_TextureAtlas> atlases = getTextureAtlases(atlasNameList);
        ENG_ScreenNative screenRenderableNative = new ENG_ScreenNative(atlases);
        mScreenNativeList.add(screenRenderableNative);
        return screenRenderableNative;
    }

    @Deprecated
    public void destroyScreen(ENG_Screen scr, boolean skipGLDelete) {
        if (scr == null) {
            return;
        }
        boolean remove = mScreens.remove(scr);
        if (!remove) {
            throw new IllegalArgumentException("Screen not found");
        }
        scr.destroy(skipGLDelete);
    }

    /** @noinspection deprecation*/
    @Deprecated
    public void destroyScreenRenderable(ENG_ScreenRenderable scr) {
        destroyScreenRenderable(scr, false);
    }

    @Deprecated
    public void destroyScreenRenderable(ENG_ScreenRenderable scr, boolean skipGLDelete) {
        if (scr == null) {
            return;
        }
        boolean remove = mScreenRenderables.remove(scr);
        if (!remove) {
            throw new IllegalArgumentException("ScreenRenderable not found");
        }
        scr.mLayerContainer.destroy(skipGLDelete);
    }

    public void destroyScreenNative(ENG_ScreenNative screenNative, boolean skipGLDelete) {
        if (screenNative == null) {
            return;
        }
        boolean remove = mScreenNativeList.remove(screenNative);
        if (!remove) {
            throw new IllegalArgumentException("ScreenRenderableNative not found");
        }
        screenNative.destroy(skipGLDelete);
    }

    public static ENG_SilverBack getSingleton() {
//        if (MainActivity.isDebugmode() && s == null) {
//            throw new NullPointerException("SilverBack not initialized");
//        }
//        return s;
        return MainApp.getGame().getRenderRoot().getGorilla();
    }

    @Override
    public boolean frameStarted(ENG_FrameEvent evt) {
        
//        for (ENG_ScreenRenderable scr : mScreenRenderables) {
//            scr.frameStarted();
//        }
        for (ENG_ScreenNative screenNative : mScreenNativeList) {
            screenNative.renderOnce();
        }

        return true;
    }

    @Override
    public boolean frameRenderingQueued(ENG_FrameEvent evt) {
        
        return true;
    }

    @Override
    public boolean frameEnded(ENG_FrameEvent evt) {
        
        return true;
    }

    public static native void createDummyNode();
    public static native void destroyDummyNode();
    public static native ByteBuffer[] createByteBuffersNative(int size, int count);
    private static native void setMaterialName(String materialName);

}
