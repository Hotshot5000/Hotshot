/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.renderer.ENG_Camera;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer;
import headwayent.hotshotengine.renderer.ENG_RenderSystem;
import headwayent.hotshotengine.renderer.ENG_SceneManager;
import headwayent.hotshotengine.renderer.ENG_SceneNode;
import headwayent.hotshotengine.renderer.ENG_SimpleRenderable;
import headwayent.hotshotengine.renderer.ENG_Viewport;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * This class is dead. Do not use it. We must fix it at some point.
 */
@Deprecated
public class ENG_ScreenRenderable extends ENG_SimpleRenderable
        implements ENG_ILayerContainer {

    protected final ENG_LayerContainer mLayerContainer;
    protected ENG_SceneManager mSceneMgr;
    protected ENG_RenderSystem mRenderSystem;
    protected ENG_Viewport mViewport;
    protected final ENG_Vector2D mMaxSize = new ENG_Vector2D();

    ENG_ScreenRenderable(ENG_Vector2D maxSize, ENG_TextureAtlas atlas) {
        
        mLayerContainer = new ENG_LayerContainer(atlas) {

            @Override
            public void addVerticesToBuffer(ArrayList<ArrayList<ArrayList<ENG_Integer>>> knownVertexCount, int indexDataNum, ENG_IndexData data, int layerNum, ArrayList<ENG_RenderOp> renderOpList, int atlas) {
                ENG_RenderOp renderOp = renderOpList.get(atlas);

                _resizeVertexBuffer(renderOp, knownVertexCount.get(indexDataNum).get(layerNum).get(atlas).getValue());

                ByteBuffer buf = (ByteBuffer) renderOp.mVertexBuffer.lock(ENG_HardwareBuffer.LockOptions.HBL_DISCARD);


        //                if (!data.mVertices.isEmpty()) {
                ArrayList<ENG_Vertex> vertices = data.getVertexList(layerNum, atlas);
                for (ENG_Vertex l : vertices) {
                    buf.putFloat(l.position.x);
                    buf.putFloat(l.position.y);
                    buf.putFloat(l.position.z);

                    buf.putFloat(l.colour.r);
                    buf.putFloat(l.colour.g);
                    buf.putFloat(l.colour.b);
                    buf.putFloat(l.colour.a);

                    buf.putFloat(l.uv.x);
                    buf.putFloat(l.uv.y);
                }
        //                }

                renderOp.mVertexBuffer.unlock();
                renderOp.mRenderOp.vertexData.vertexCount = knownVertexCount.get(indexDataNum).get(layerNum).get(atlas).getValue();
            }

            @Override
            public void renderOnce() {

                if (mIndexRedrawNeeded) {
                    _renderVertices(false);
                    calculateBoundingBox();
                }
            }
        };
        mMaxSize.set(maxSize);
        mBox.setInfinite();
        setMaterial(atlas.get3DMaterialName());
//		mLayerContainer._createVertexBuffer();
    }

    @Override
    public void renderOnce() {

        mLayerContainer.renderOnce();
    }

    private final ENG_Vector2D halfSize = new ENG_Vector2D();

    @Override
    public void _transform(ArrayList<ArrayList<ENG_Vertex>> vertices,
                           ArrayList<Integer> beginList,
                           ArrayList<Integer> endList) {

        halfSize.set(mMaxSize);
        halfSize.mulInPlace(0.5f);
        for (int i = 0; i < vertices.size(); ++i) {
            ArrayList<ENG_Vertex> list = vertices.get(i);
            int begin = beginList.get(i);
            int end = endList.get(i);
            for (int j = begin; j < end; ++j) {
                ENG_Vector3D position = list.get(j).position;
                position.x = position.x * 0.01f - halfSize.x;
                position.y = position.y * -0.01f + halfSize.y;
            }
        }

    }

    public void frameStarted() {
        renderOnce();
    }

    public void calculateBoundingBox() {

        mBox.setExtents(0, 0, 0, 0, 0, 0);
        for (ENG_IndexData data : mLayerContainer.mIndexData.values()) {
            for (int i = 0; i < data.getLayerNum(); ++i) {
                int vertexListsNum = data.getVertexListsNum(i);
                for (int j = 0; j < vertexListsNum; ++j) {
                    ArrayList<ENG_Vertex> list = data.getVertexList(i, j);
                    for (ENG_Vertex v : list) {
                        mBox.merge(v.position);
                    }
                }
            }
        }

        if (!mBox.isNull()) {
            mBox.merge(new ENG_Vector3D(0, 0, 0.25f));
            mBox.merge(new ENG_Vector3D(0, 0, -0.25f));
        }
        ENG_SceneNode node = getParentSceneNode();
        if (node != null) {
            node._updateBounds();
        }
    }

    @Override
    public float getBoundingRadius() {

        return mBox.getMax().squaredLength();
    }

    private final ENG_Vector3D min = new ENG_Vector3D();
    private final ENG_Vector3D max = new ENG_Vector3D();
    private final ENG_Vector3D mid = new ENG_Vector3D();
    private final ENG_Vector3D dist = new ENG_Vector3D();
    private final ENG_Vector4D temp = new ENG_Vector4D();

    @Override
    public float getSquaredViewDepth(ENG_Camera cam) {

        mBox.getMin(min);
        mBox.getMax(max);
        max.sub(min, mid);
        mid.mulInPlace(0.5f);
        mid.addInPlace(min);
        cam.getDerivedPosition(temp);
        temp.subInPlace(min);
        dist.set(temp);
        return dist.squaredLength();
    }

}
