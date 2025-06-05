/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import headwayent.hotshotengine.basictypes.ENG_Boolean;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.renderer.ENG_RenderQueue.RenderQueueGroupID;
import headwayent.hotshotengine.renderer.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ENG_Screen extends ENG_LayerContainer implements ENG_RenderQueueListener {

    //	protected ArrayList<RenderOp> mRenderOp = new ArrayList<RenderOp>();
    protected ENG_SceneManager mSceneMgr;
    protected ENG_RenderSystem mRenderSystem;
    protected ENG_Viewport mViewport;
    protected float mWidth, mHeight, mInvWidth, mInvHeight;
    protected final ENG_Vector4D mScale = new ENG_Vector4D(ENG_Math.PT4_UNIT);
    protected boolean mIsVisible = true;
    protected boolean mCanRender;
    protected final ENG_Matrix4 mVertexTransform = new ENG_Matrix4();

    ENG_Screen(ENG_Viewport vp, ENG_TextureAtlas atlas) {
        ArrayList<ENG_TextureAtlas> atlases = new ArrayList<>();
        atlases.add(atlas);
        set(vp, atlases);
    }

    ENG_Screen(ENG_Viewport vp, ArrayList<ENG_TextureAtlas> atlasList) {
        
        set(vp, atlasList);

    }

    private void set(ENG_Viewport vp, ArrayList<ENG_TextureAtlas> atlasList) {
        super.set(atlasList);
        int mAtlasNum = atlasList.size();
        mViewport = vp;
//        for (int i = 0; i < mAtlasNum; ++i) {
//            mRenderOp.add(new RenderOp());
//        }
//        mRenderOpPtr = mRenderOp;
//        mRenderOpList.addAll(mRenderOp);
        mSceneMgr = vp.getCamera().getSceneManager();
        mRenderSystem = ENG_RenderRoot.getRenderRoot().getRenderSystem();
        mWidth = vp.getActualWidth();
        mHeight = vp.getActualHeight();
        mInvWidth = 1.0f / mWidth;
        mInvHeight = 1.0f / mHeight;

        mVertexTransform.makeTransform(ENG_Math.PT4_ZERO, mScale, ENG_Math.QUAT_IDENTITY);
        mSceneMgr.addRenderQueueListener(this);
//        for (int i = 0; i < mAtlasNum; ++i) {
//            _createVertexBuffer(mRenderOp.get(i), 32);
//        }
    }

    public void destroy(boolean skipGLDelete) {
        super.destroy(skipGLDelete);
        mSceneMgr.removeRenderQueueListener(this);
    }

    @Override
    public void preRenderQueues() {


    }

    @Override
    public void postRenderQueues() {


    }

    @Override
    public void renderQueueStarted(byte queueGroupId, String invocation,
                                   ENG_Boolean skipThisInvocation) {


    }

    @Override
    public void renderQueueEnded(byte queueGroupId, String invocation,
                                 ENG_Boolean repeatThisInvocation) {

        if (mRenderSystem._getViewport() != mViewport || queueGroupId != RenderQueueGroupID.RENDER_QUEUE_OVERLAY.getID()) {
            return;
        }
        if (mIsVisible && hasLayers()) {
            renderOnce();
        }
    }

    public void _prepareRenderSystem(int atlas) {
        mSceneMgr._setPass(mAtlasList.get(atlas).get2DPass());
    }

    @Override
    public void renderOnce() {

        _renderVertices(false);
        for (ENG_IndexData data : mIndexData.values()) {
            for (int layer = 0; layer < data.getLayerNum(); ++layer) {
                ArrayList<ENG_RenderOp> renderOpList = data.getRenderOpList(data.getLayer(layer));
                for (int atlas = 0; atlas < renderOpList.size(); ++atlas) {
                    ENG_RenderOp renderOp = renderOpList.get(atlas);
                    if (renderOp.mRenderOp.vertexData.vertexCount > 0) {
                        _prepareRenderSystem(atlas);
                        mRenderSystem._render(renderOp.mRenderOp);
                    }
                }
            }
        }
    }

    private final ENG_Vector3D temp = new ENG_Vector3D();

    @Override
    public void _transform(ArrayList<ArrayList<ENG_Vertex>> verticesList,
                           ArrayList<Integer> beginList,
                           ArrayList<Integer> endList) {

        for (int j = 0; j < verticesList.size(); ++j) {
            ArrayList<ENG_Vertex> vertices = verticesList.get(j);
            int begin = beginList.get(j);
            int end = endList.get(j);
            for (int i = begin; i < end; ++i) {
                vertices.get(i).position.x =
                        ((vertices.get(i).position.x) * mInvWidth) * 2 - 1;
                vertices.get(i).position.y =
                        ((vertices.get(i).position.y) * mInvHeight) * -2 + 1;
            }
            if (!mVertexTransform.equals(ENG_Math.MAT4_IDENTITY)) {
                for (int i = begin; i < end; ++i) {
                    ENG_Vector3D position = vertices.get(i).position;
                    mVertexTransform.transform(position, temp);
                    position.set(temp);
                }
            }
        }
    }

    public float getTexelOffsetX() {
        return mRenderSystem.getHorizontalTexelOffset();
    }

    public float getTexelOffsetY() {
        return mRenderSystem.getVerticalTexelOffset();
    }

    public float getWidth() {
        return mWidth;
    }

    public float getHeight() {
        return mHeight;
    }

    public boolean isVisible() {
        return mIsVisible;
    }

    public void setVisible(boolean b) {
        mIsVisible = b;
    }

    public void hide() {
        mIsVisible = false;
    }

    public void show() {
        mIsVisible = true;
    }

    @Override
    void addVerticesToBuffer(ArrayList<ArrayList<ArrayList<ENG_Integer>>> knownVertexCount,
                                    int indexDataNum, ENG_IndexData data, int layerNum,
                                    ArrayList<ENG_RenderOp> renderOpList, int atlas) {
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
}
