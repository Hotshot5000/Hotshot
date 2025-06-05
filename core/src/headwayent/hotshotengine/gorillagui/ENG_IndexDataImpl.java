/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 3/14/17, 9:02 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import java.util.ArrayList;

/**
 * Created by sebas on 13.03.2017.
 */
class ENG_IndexDataImpl extends ENG_IndexData {

    ENG_IndexDataImpl(ENG_LayerContainer layerContainer) {
        super(layerContainer);
    }

    @Override
    public void addLayer(ENG_Layer layer) {
        // Create the RenderOps for each atlas map.
        ArrayList<ENG_RenderOp> renderOps = new ArrayList<>();
        ArrayList<ArrayList<ENG_Vertex>> lists = new ArrayList<>();
        for (int i = 0; i < layerContainer.mAtlasNum; ++i) {
            ENG_RenderOp renderOp = new ENG_RenderOp();
            layerContainer._createVertexBuffer(renderOp, 32);
            renderOps.add(renderOp);
            lists.add(new ArrayList<>());
        }
        mVertices.add(lists);
        ArrayList<ENG_RenderOp> put = mLayerMap.put(layer, renderOps);
        if (put != null) {
            throw new IllegalArgumentException("layer already added");
        }
        mLayers.add(layer);
    }

    @Override
    public void removeLayer(ENG_Layer layer, boolean skipGLDelete) {
        ArrayList<ENG_RenderOp> remove = mLayerMap.remove(layer);
        if (remove == null) {
            throw new IllegalArgumentException("layer does not exist");
        }
        for (int i = 0; i < layerContainer.mAtlasNum; ++i) {
            layerContainer._destroyVertexBuffer(remove.get(i), skipGLDelete);
        }
        mLayers.remove(layer);
    }

}
