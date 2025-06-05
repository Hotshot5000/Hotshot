/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.Usage;
import headwayent.hotshotengine.renderer.*;
import headwayent.hotshotengine.renderer.ENG_RenderOperation.OperationType;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementType;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_RenderingThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

public abstract class ENG_LayerContainer implements ENG_ILayerContainer {

    //	protected ArrayList<ENG_Layer> mLayers = new ArrayList<ENG_Layer>();
    final TreeMap<Integer, ENG_IndexData> mIndexData = new TreeMap<>();
    protected int writeableBuffersWrittenNum;
    protected boolean mIndexRedrawNeeded, mIndexRedrawAll;
    //    protected ArrayList<RenderOp> mRenderOpList = new ArrayList<RenderOp>();
//	protected ENG_HardwareVertexBuffer mVertexBuffer;
//	protected ArrayList<Integer> mVertexBufferSize = new ArrayList<Integer>();
//	protected ArrayList<ENG_RenderOperation> mRenderOpPtr;
    protected final ArrayList<ENG_TextureAtlas> mAtlasList = new ArrayList<>();
    protected final HashMap<String, Integer> mNameToAtlasPos = new HashMap<>();
    protected int mAtlasNum;

    /**
     * Default for ENG_Screen that uses set
     */
    ENG_LayerContainer() {

    }

    ENG_LayerContainer(ENG_TextureAtlas atlas) {

        ArrayList<ENG_TextureAtlas> atlases = new ArrayList<>();
        atlases.add(atlas);
        set(atlases);
    }

    ENG_LayerContainer(ArrayList<ENG_TextureAtlas> atlasList) {
        set(atlasList);
    }

    protected void set(ArrayList<ENG_TextureAtlas> atlasList) {
        mAtlasList.addAll(atlasList);
        int i = 0;
        for (ENG_TextureAtlas atlas : mAtlasList) {
            mNameToAtlasPos.put(atlas.getName(), i++);
        }
        mAtlasNum = mAtlasList.size();
    }

    public void destroy(boolean skipGLDelete) {
        for (ENG_IndexData d : mIndexData.values()) {
            d.removeAllLayers(skipGLDelete);
        }
    }

    public boolean hasLayers() {
        for (ENG_IndexData data : mIndexData.values()) {
            if (data.getLayerNum() > 0) {
                return true;
            }
        }
        return false;
    }

    public ENG_Layer createLayerNative(int index, byte[] nativeIndex) {
        ENG_Layer layer = new ENG_Layer(index, this, mAtlasList.size(), nativeIndex);

        ENG_IndexData data = addLayerNative(index, layer);
        data.setRedrawNeeded(true);
        mIndexRedrawNeeded = true;
        writeableBuffersWrittenNum = 0;
        return layer;
    }

    public ENG_Layer createLayer(int index) {
        ENG_Layer layer = new ENG_Layer(index, this, mAtlasList.size());

        ENG_IndexData data = addLayer(index, layer);
        data.setRedrawNeeded(true);
        mIndexRedrawNeeded = true;
        writeableBuffersWrittenNum = 0;
        return layer;
    }

    private ENG_IndexData addLayer(int index, ENG_Layer layer) {
        ENG_IndexData data = mIndexData.get(index);
        if (data == null) {
            data = new ENG_IndexDataImpl(this);
            mIndexData.put(index, data);
        }
        data.addLayer(layer);
        return data;
    }

    private ENG_IndexData addLayerNative(int index, ENG_Layer layer) {
        ENG_IndexData data = mIndexData.get(index);
        if (data == null) {
            data = new ENG_IndexDataNativeImpl(this);
            mIndexData.put(index, data);
        }
        data.addLayer(layer);
        return data;
    }

    public void destroyLayer(ENG_Layer layer, boolean skipGLDelete) {
        if (layer == null) {
            return;
        }
        ENG_IndexData indexData = mIndexData.get(layer.getIndex());
        if (indexData != null) {
            indexData.removeLayer(layer, skipGLDelete);
            indexData.setRedrawNeeded(true);
            mIndexRedrawNeeded = true;
            writeableBuffersWrittenNum = 0;
            if (indexData.getLayerNum() == 0) {
                mIndexData.remove(layer.getIndex());
            }
        }
    }

    public void _createVertexBuffer(ENG_RenderOp renderOp, int initialSize) {
        renderOp.mVertexBufferSize = initialSize * 6;
        renderOp.mRenderOp.vertexData = new ENG_VertexData();
        renderOp.mRenderOp.vertexData.vertexStart = 0;

        ENG_VertexDeclaration vertexDecl =
                renderOp.mRenderOp.vertexData.vertexDeclaration;
        int offset = 0;

        vertexDecl.addElement((short) 0,
                offset, VertexElementType.VET_FLOAT3,
                VertexElementSemantic.VES_POSITION);
        offset += ENG_VertexElement.getTypeSize(VertexElementType.VET_FLOAT3);
        vertexDecl.addElement((short) 0,
                offset, VertexElementType.VET_FLOAT4,
                VertexElementSemantic.VES_DIFFUSE);
        offset += ENG_VertexElement.getTypeSize(VertexElementType.VET_FLOAT4);
        vertexDecl.addElement((short) 0,
                offset, VertexElementType.VET_FLOAT2,
                VertexElementSemantic.VES_TEXTURE_COORDINATES);

        renderOp.mVertexBuffer = ENG_HardwareBufferManager.getSingleton()
                .createVertexBuffer(
                        vertexDecl.getVertexSize((short) 0),
                        renderOp.mVertexBufferSize,
                        Usage.HBU_DYNAMIC_WRITE_ONLY_DISCARDABLE.getUsage(),
                        true);

        renderOp.mRenderOp.vertexData.vertexBufferBinding.setBinding(
                (short) 0, renderOp.mVertexBuffer);
        renderOp.mRenderOp.operationType = OperationType.OT_TRIANGLE_LIST;
        renderOp.mRenderOp.useIndexes = false;
    }

    public void _destroyVertexBuffer(ENG_RenderOp renderOp, boolean skipGLDelete) {

//		mVertexBuffer.destroy();
        renderOp.mRenderOp.vertexData.vertexBufferBinding.unsetAllBindings(skipGLDelete);
        renderOp.mRenderOp.vertexData = null;
        renderOp.mVertexBufferSize = 0;
    }

    public void _resizeVertexBuffer(ENG_RenderOp renderOp, int requestedSize) {
        if (renderOp.mVertexBufferSize == 0) {
            _createVertexBuffer(renderOp, requestedSize);
        }
        if (requestedSize > renderOp.mVertexBufferSize) {
            int newVertexBufferSize = 1;
            while (newVertexBufferSize < requestedSize) {
                newVertexBufferSize *= 2;
            }
            if (renderOp.mVertexBuffer != null) {
                renderOp.mVertexBuffer.destroy(false);
            }
            renderOp.mVertexBuffer = ENG_HardwareBufferManager.getSingleton()
                    .createVertexBuffer(
                            renderOp.mRenderOp.vertexData.vertexDeclaration
                                    .getVertexSize((short) 0),
                            newVertexBufferSize,
                            Usage.HBU_DYNAMIC_WRITE_ONLY_DISCARDABLE.getUsage(), true);
            renderOp.mVertexBufferSize = newVertexBufferSize;
            renderOp.mRenderOp.vertexData.vertexStart = 0;
            renderOp.mRenderOp.vertexData.vertexBufferBinding
                    .setBinding((short) 0, renderOp.mVertexBuffer);
        }
    }

    public void _recalculateIndexes() {
        for (ENG_IndexData data : mIndexData.values()) {
            data.reset();
        }
        for (ENG_IndexData data : mIndexData.values()) {
            for (int i = 0; i < data.getLayerNum(); ++i) {
                ENG_Layer layer = data.getLayer(i);
                addLayer(layer.getIndex(), layer);
            }
        }
        ArrayList<Integer> list = new ArrayList<>();
        for (Entry<Integer, ENG_IndexData> entry : mIndexData.entrySet()) {
            if (entry.getValue().getLayerNum() == 0) {
                list.add(entry.getKey());
            }
        }
        for (Integer i : list) {
            mIndexData.remove(i);
        }
        mIndexRedrawAll = true;
    }

    public void _redrawIndex(int index, boolean force) {
        ENG_IndexData indexData = mIndexData.get(index);
        if (indexData == null) {
            return;
        }
        drawIndex(force, indexData);
    }

    private void drawIndex(boolean force, ENG_IndexData indexData) {
        for (int i = 0; i < indexData.getLayerNum(); ++i) {
            ArrayList<ArrayList<ENG_Vertex>> layerVertexList = indexData.getLayerVertexList(i);
            for (int j = 0; j < layerVertexList.size(); ++j) {
                ArrayList<ENG_Vertex> list = indexData.getVertexList(i, j);
                list.clear();
            }
        }
        indexData.setRedrawNeeded(false);
        for (int i = 0; i < indexData.getLayerNum(); ++i) {
            ENG_Layer l = indexData.getLayer(i);
            if (l.mVisible) {
                l._render(indexData.getVertices(i), force);
            }
        }
    }

    public void _requestIndexRedraw(int index) {
        ENG_IndexData indexData = mIndexData.get(index);
        if (indexData == null) {
            return;
        }
        indexData.setRedrawNeeded(true);
        mIndexRedrawNeeded = true;
        writeableBuffersWrittenNum = 0;
    }

    public void _redrawAllIndexes(boolean force) {
        mIndexRedrawAll = false;
        for (ENG_IndexData data : mIndexData.values()) {
            if (data.isRedrawNeeded() || force) {
                drawIndex(force, data);
            }
        }
    }

    public void _renderVertices(boolean force) {
        if (!mIndexRedrawNeeded && !force) {
            return;
        }

        _redrawAllIndexes(force);

        ArrayList<ArrayList<ArrayList<ENG_Integer>>> knownVertexCount = new ArrayList<>();
        for (ENG_IndexData data : mIndexData.values()) {
            ArrayList<ArrayList<ENG_Integer>> integers = new ArrayList<>();
            for (int layer = 0; layer < data.getLayerNum(); ++layer) {
                ArrayList<ENG_Integer> list = new ArrayList<>();
                for (int atlas = 0; atlas < mAtlasNum; ++atlas) {
                    list.add(new ENG_Integer());
                }
                integers.add(list);
            }
            knownVertexCount.add(integers);
        }

        int indexDataNum = 0;
        for (ENG_IndexData data : mIndexData.values()) {
            for (int layer = 0; layer < data.getLayerNum(); ++layer) {
                int vertexListsNum = data.getVertexListsNum(layer);
                for (int atlas = 0; atlas < vertexListsNum; ++atlas) {
                    knownVertexCount.get(indexDataNum).get(layer).get(atlas).addInPlace(data.getVertexListSize(layer, atlas));
                }
            }
            ++indexDataNum;
        }

        indexDataNum = 0;
        for (ENG_IndexData data : mIndexData.values()) {
            for (int layerNum = 0; layerNum < data.getLayerNum(); ++layerNum) {
                ENG_Layer layer = data.getLayer(layerNum);
                int vertexListsNum = data.getVertexListsNum(layerNum);
                ArrayList<ENG_RenderOp> renderOpList = data.getRenderOpList(layer);
                for (int atlas = 0; atlas < vertexListsNum; ++atlas) {

                    addVerticesToBuffer(knownVertexCount, indexDataNum, data, layerNum, renderOpList, atlas);

                }
            }
            ++indexDataNum;
        }
//        System.out.println("writeableBuffersWrittenNum: " + writeableBuffersWrittenNum);
        if ((++writeableBuffersWrittenNum) == ENG_RenderingThread.getBufferCount()) {
            mIndexRedrawNeeded = false;
        }
    }

    abstract void addVerticesToBuffer(ArrayList<ArrayList<ArrayList<ENG_Integer>>> knownVertexCount,
                                             int indexDataNum, ENG_IndexData data, int layerNum,
                                             ArrayList<ENG_RenderOp> renderOpList, int atlas);

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.GorillaGUI.ENG_ILayerContainer#renderOnce()
     */
    @Override
    public abstract void renderOnce();

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.GorillaGUI.ENG_ILayerContainer#_transform(java.util.ArrayList, int, int)
     */
    @Override
    public void _transform(ArrayList<ArrayList<ENG_Vertex>> vertices,
                           ArrayList<Integer> beginList,
                           ArrayList<Integer> endList) {

    }

    public ENG_TextureAtlas getAtlas() {
        return mAtlasList.get(0);
    }

    public ENG_TextureAtlas getAtlas(int i) {
        return mAtlasList.get(i);
    }

    public static class NinePatchAndAtlas {
        public final ENG_TextureAtlas atlas;
        public final ENG_NinePatch ninePatch;

        public NinePatchAndAtlas(ENG_TextureAtlas atlas, ENG_NinePatch ninePatch) {
            this.atlas = atlas;
            this.ninePatch = ninePatch;
        }
    }

    public NinePatchAndAtlas getNinePatch(String name) {
        for (ENG_TextureAtlas atlas : mAtlasList) {
            ENG_NinePatch ninePatch = atlas.getNinePatch(name);
            if (ninePatch != null) {
                return new NinePatchAndAtlas(atlas, ninePatch);
            }
        }
        return null;
    }

    public float getTexelOffsetX() {
        return 0.0f;
    }

    public float getTexelOffsetY() {
        return 0.0f;
    }

    public Integer getNameToAtlasIndex(String name) {
        return mNameToAtlasPos.get(name);
    }

}
