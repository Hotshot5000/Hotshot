/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import java.util.ArrayList;
import java.util.HashMap;

import headwayent.hotshotengine.basictypes.ENG_Byte;
import headwayent.hotshotengine.basictypes.ENG_Long;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

/**
 * Created by sebas on 13.03.2017.
 */

class ENG_IndexDataNativeImpl extends ENG_IndexData {

    private final HashMap<ENG_Layer, ENG_Long[][]> mLayerToScreenRenderablePtr = new HashMap<>();
    private final HashMap<ENG_Layer, ENG_Byte[]> mLayerToScreenRenderableQueueGroupId = new HashMap<>();

    public ENG_IndexDataNativeImpl(ENG_LayerContainer layerContainer) {
        super(layerContainer);
    }

    @Override
    public void addLayer(ENG_Layer layer) {
//        ArrayList<ENG_RenderOp> renderOps = new ArrayList<>();
        ArrayList<ArrayList<ENG_Vertex>> lists = new ArrayList<>();
        byte[] queueGroupId = layer.getQueueGroupId();
        ENG_Long[][] ptrsList = new ENG_Long[layerContainer.mAtlasNum][];
        ENG_Byte[] queueGroupIdByteList = new ENG_Byte[queueGroupId.length];
        for (int i = 0; i < queueGroupId.length; ++i) {
            queueGroupIdByteList[i] = new ENG_Byte(queueGroupId[i]);
        }
        ENG_ScreenNative screenNative = (ENG_ScreenNative) this.layerContainer;
        for (int i = 0; i < this.layerContainer.mAtlasNum; ++i) {
//            ENG_RenderOp renderOp = new ENG_RenderOp();
//            layerContainer._createVertexBuffer(renderOp, 32);
//            renderOps.add(renderOp);
            ENG_Long[] ptr = ENG_NativeCalls.callScreenNative_CreateLayer(
                    screenNative.getPointer(), screenNative.getAtlas(i).getTexturePtr(),
                    ENG_RenderRoot.getRenderRoot().getSceneManager("").getPointer(),
                    queueGroupId[i], false);
            ptrsList[i] = ptr;
            lists.add(new ArrayList<>());
            screenNative.addScreenRenderable(ptr, queueGroupId[i]);
        }
        mLayerToScreenRenderablePtr.put(layer, ptrsList);
        mLayerToScreenRenderableQueueGroupId.put(layer, queueGroupIdByteList);
        mVertices.add(lists);
//        ArrayList<ENG_RenderOp> put = mLayerMap.put(layer, renderOps);
//        if (put != null) {
//            throw new IllegalArgumentException("layer already added");
//        }
        mLayers.add(layer);
    }

    @Override
    public void removeLayer(ENG_Layer layer, boolean skipGLDelete) {
//        ArrayList<ENG_RenderOp> remove = mLayerMap.remove(layer);
//        if (remove == null) {
//            throw new IllegalArgumentException("layer does not exist");
//        }
//        for (int i = 0; i < layerContainer.mAtlasNum; ++i) {
//            layerContainer._destroyVertexBuffer(remove.get(i), skipGLDelete);
//        }
        ENG_Long[][] remove = mLayerToScreenRenderablePtr.remove(layer);
        if (remove == null) {
            throw new IllegalArgumentException("layer does not exist");
        }
        long[] ptrList = new long[remove.length];
        int i = 0;
        for (ENG_Long[] ptr : remove) {
            ptrList[i++] = ptr[0].getValue();
        }
        ENG_ScreenNative screenNative = (ENG_ScreenNative) this.layerContainer;
        ENG_Byte[] remove1 = mLayerToScreenRenderableQueueGroupId.remove(layer);
        if (remove1 == null) {
            throw new IllegalArgumentException("layer does not exist");
        }
        byte[] queueGroupIds = new byte[remove1.length];
        i = 0;
        for (ENG_Byte queueGroupId : remove1) {
            queueGroupIds[i++] = queueGroupId.getValue();
        }
        ENG_NativeCalls.callScreenNative_DestroyLayer(screenNative.getPointer(), ptrList, queueGroupIds, true);

        mLayers.remove(layer);
    }
}
