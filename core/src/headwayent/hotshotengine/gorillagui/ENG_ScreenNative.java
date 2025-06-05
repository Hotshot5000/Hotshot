/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ApplicationStartSettings;
import headwayent.hotshotengine.ENG_Log;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.basictypes.ENG_Long;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointerDeferred;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

/**
 * Created by sebas on 09.03.2017.
 */

public class ENG_ScreenNative extends ENG_LayerContainer implements ENG_NativePointerDeferred {

    public static final int SCREEN_RENDERABLE_NUM = 32;
//    private static final ENG_FloatArrayList floatList = new ENG_FloatArrayList(512);
    private final long[] screenPtr;
    private final ArrayList<ENG_Long[]> screenRenderableList = new ArrayList<>(SCREEN_RENDERABLE_NUM);
    private final ENG_Long[][] screenRenderableArray = new ENG_Long[SCREEN_RENDERABLE_NUM][];
    private final byte[] queueGroupIds = new byte[SCREEN_RENDERABLE_NUM];
    private boolean visible;

    ENG_ScreenNative(ArrayList<ENG_TextureAtlas> atlases) {
        set(atlases);
        long[] atlasPtr = new long[atlases.size()];
        for (int i = 0; i < atlasPtr.length; ++i) {
            atlasPtr[i] = atlases.get(i).getTexturePtr();
        }
        screenPtr = ENG_NativeCalls.callScreenNative_Create(atlasPtr);

    }

    @Override
    protected void set(ArrayList<ENG_TextureAtlas> atlasList) {
        super.set(atlasList);
    }

    @Override
    public void destroy(boolean skipGLDelete) {
        super.destroy(skipGLDelete);
        ENG_NativeCalls.callScreenNative_Destroy(screenPtr, true);
    }

    protected void addScreenRenderable(ENG_Long[] screenRenderablePtr, byte queueGroupId) {
        screenRenderableList.add(screenRenderablePtr);
        int pos = screenRenderableList.size() - 1;
        screenRenderableArray[pos] = screenRenderablePtr;
        queueGroupIds[pos] = queueGroupId;
    }

    @Override
    public void renderOnce() {
        if (!isVisible()) {
            return;
        }
        _renderVertices(false);
//        ENG_NativeCalls.callScreenNative_RenderOnce();
    }

    @Override
    void addVerticesToBuffer(ArrayList<ArrayList<ArrayList<ENG_Integer>>> knownVertexCount,
                                    int indexDataNum, ENG_IndexData data, int layerNum,
                                    ArrayList<ENG_RenderOp> renderOpList, int atlas) {
        ENG_SilverBack silverBack = ENG_SilverBack.getSingleton();
        ApplicationStartSettings applicationSettings = MainApp.getMainThread().getApplicationSettings();
        float screenWidth = applicationSettings.screenWidth;
        float screenHeight = applicationSettings.screenHeight;
        // Right now we have 18 buffers. 3 * 6 where 6 is the number of indexData.
        // Make sure the queueGroupIds follow the correct indices!!! Don't forget to take atlas into account.
        int bufferNum = silverBack.getCurrentBufferListStart() * ENG_Container.MAX_LAYER_NUM * ENG_Container.TEXTURE_ATLAS_NUM +
                indexDataNum * this.mAtlasNum + atlas;
        ByteBuffer buf = silverBack.getBufferList()[bufferNum];
//        ENG_Utility.memset(buf, (byte) 0);

        buf.position(0);

        ArrayList<ENG_Vertex> vertices = data.getVertexList(layerNum, atlas);

//        if (vertices.isEmpty()) {
//            return;
//        }

//        ENG_Log.getInstance().log("silverBack.getCurrentBufferListStart(): " + silverBack.getCurrentBufferListStart() +
//                " indexDataNum: " + indexDataNum + " atlas: " + atlas +
//                " vertices.size(): " + vertices.size() + " bufferNum: " + bufferNum);
//        floatList.clearFast();
        try {
            for (ENG_Vertex l : vertices) {
    //            floatList.add((l.position.x / screenWidth) * 2.0f - 1.0f);
    //            floatList.add(((screenHeight - l.position.y) / screenHeight * 2.0f - 1.0f));
    //            floatList.add(1.0f);
    //
    //            floatList.add(l.colour.r);
    //            floatList.add(l.colour.g);
    //            floatList.add(l.colour.b);
    //            floatList.add(l.colour.a);
    //
    //            floatList.add(l.uv.x);
    //            floatList.add(l.uv.y);

                    buf.putFloat((l.position.x / screenWidth) * 2.0f - 1.0f);
                    buf.putFloat(((screenHeight - l.position.y) / screenHeight * 2.0f - 1.0f));
                    buf.putFloat(1.0f);

                    buf.putFloat(l.colour.r);
                    buf.putFloat(l.colour.g);
                    buf.putFloat(l.colour.b);
                    buf.putFloat(l.colour.a);

                    buf.putFloat(l.uv.x);
                    buf.putFloat(l.uv.y);

            }
        } catch (BufferOverflowException e) {
            e.printStackTrace();
        }

//        floatList.writeToFloatBuffer(buf.asFloatBuffer());

//        buf.putFloat(-0.5f);
//        buf.putFloat(0.5f);
//        buf.putFloat(1.0f);
//
//        buf.putFloat(1.0f);
//        buf.putFloat(0.0f);
//        buf.putFloat(0.0f);
//        buf.putFloat(1.0f);
//
//        buf.putFloat(0.0f);
//        buf.putFloat(0.0f);
//
//        buf.putFloat(-0.5f);
//        buf.putFloat(-0.5f);
//        buf.putFloat(1.0f);
//
//        buf.putFloat(1.0f);
//        buf.putFloat(0.0f);
//        buf.putFloat(0.0f);
//        buf.putFloat(1.0f);
//
//        buf.putFloat(0.0f);
//        buf.putFloat(0.0f);
//
//        buf.putFloat(0.5f);
//        buf.putFloat(0.5f);
//        buf.putFloat(1.0f);
//
//        buf.putFloat(1.0f);
//        buf.putFloat(0.0f);
//        buf.putFloat(0.0f);
//        buf.putFloat(1.0f);
//
//        buf.putFloat(0.0f);
//        buf.putFloat(0.0f);
//
//        buf.putFloat(0.5f);
//        buf.putFloat(-0.5f);
//        buf.putFloat(1.0f);
//
//        buf.putFloat(1.0f);
//        buf.putFloat(0.0f);
//        buf.putFloat(0.0f);
//        buf.putFloat(1.0f);
//
//        buf.putFloat(0.0f);
//        buf.putFloat(0.0f);

//        if (vertices.size() > 0) {
//            System.out.println("addVerticesToBuffer vertices.size(): " + vertices.size() +
//                    " queueGroupId: " + queueGroupIds[indexDataNum] + " screenPtr: " + screenPtr[0] + " bufferNum: " + bufferNum +
//                    " layerNum: " + layerNum + " atlasNum: " + atlas + " indexData: " + data + " indexDataNum: " + indexDataNum);
//        }

        // Make sure the queueGroupIds follow the correct indices!!! Don't forget to take atlas into account.
        ENG_NativeCalls.callScreenNative_UpdateVertexListSize(screenPtr, screenRenderableArray,
//                screenRenderableList.get(
//                        layerNum * ENG_Container.TEXTURE_ATLAS_NUM + atlas)[0].getValue(),
                        vertices.size(), (byte) bufferNum,
                queueGroupIds[indexDataNum * this.mAtlasNum + atlas], false);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            ENG_NativeCalls.callScreen_SetVisible(screenPtr, visible, false);
            // Use per screen renderable setVisible() only if absolutely necessary.
//            for (ENG_Long[] ptr : screenRenderableList) {
//                ENG_NativeCalls.callScreenRenderable_SetVisible(ptr[0].getValue(), visible);
//            }
        }

    }

    @Override
    public long[] getPointer() {
        return screenPtr;
    }
}
