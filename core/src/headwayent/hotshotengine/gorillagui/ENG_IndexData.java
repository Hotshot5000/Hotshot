/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 3/14/17, 9:02 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sebas on 13.03.2017.
 */
abstract class ENG_IndexData {
    protected final ArrayList<ENG_Layer> mLayers = new ArrayList<>();
    protected final ArrayList<ArrayList<ArrayList<ENG_Vertex>>> mVertices = new ArrayList<>();
    final HashMap<ENG_Layer, ArrayList<ENG_RenderOp>> mLayerMap = new HashMap<>();
    protected final ENG_LayerContainer layerContainer;
    private boolean mRedrawNeeded;

    ENG_IndexData(ENG_LayerContainer layerContainer) {
        this.layerContainer = layerContainer;
    }

    public abstract void addLayer(ENG_Layer layer);

    ArrayList<ENG_RenderOp> getRenderOpList(ENG_Layer l) {
        return mLayerMap.get(l);
    }

    public abstract void removeLayer(ENG_Layer layer, boolean skipGLDelete);

    ArrayList<ArrayList<ENG_Vertex>> getVertices(int i) {
        return mVertices.get(i);
    }

    public void removeAllLayers(boolean skipGLDelete) {
        ArrayList<ENG_Layer> layers = new ArrayList<>(mLayers);
        for (ENG_Layer l : layers) {
            removeLayer(l, skipGLDelete);
        }
        if (!mLayerMap.isEmpty()) {
            throw new IllegalStateException("mLayersMap should be empty, but has: " + mLayerMap.size() + " elements");
        }
        mLayers.clear();
    }

    public ENG_Layer getLayer(int i) {
        return mLayers.get(i);
    }

    public int getLayerNum() {
        return mLayers.size();
    }

    public boolean isRedrawNeeded() {
        return mRedrawNeeded;
    }

    public void setRedrawNeeded(boolean needed) {
        mRedrawNeeded = needed;
    }

    public int getVertexListsNum(int layer) {
        return mVertices.get(layer).size();
    }

    ArrayList<ENG_Vertex> getVertexList(int layer, int i) {
        return mVertices.get(layer).get(i);
    }

    ArrayList<ArrayList<ENG_Vertex>> getLayerVertexList(int layer) {
        return mVertices.get(layer);
    }

    public int getVertexListSize(int layer, int atlasNum) {
        return mVertices.get(layer).get(atlasNum).size();
    }

    public void reset() {
        mLayers.clear();
        mVertices.clear();
        mRedrawNeeded = false;
    }
}
