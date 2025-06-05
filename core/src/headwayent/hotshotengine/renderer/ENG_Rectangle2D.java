/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.LockOptions;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementType;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class ENG_Rectangle2D extends ENG_SimpleRenderable {

    private static final int POSITION_BINDING = 0;
    private static final int NORMAL_BINDING = 1;
    private static final int TEXCOORD_BINDING = 2;

    @Override
    public void getWorldTransforms(ENG_Matrix4[] xform) {
        
        xform[0].set(ENG_Math.MAT4_IDENTITY);
    }

    public ENG_Rectangle2D(boolean includeTextureCoords,
                           int vBufUsage) {
        // use identity projection and view matrices
        mUseIdentityProjection = true;
        mUseIdentityView = true;

        mRenderOp.vertexData = new ENG_VertexData();

        mRenderOp.indexData = null;
        mRenderOp.vertexData.vertexCount = 4;
        mRenderOp.vertexData.vertexStart = 0;
        mRenderOp.operationType = ENG_RenderOperation.OperationType.OT_TRIANGLE_STRIP;
        mRenderOp.useIndexes = false;

        ENG_VertexDeclaration decl = mRenderOp.vertexData.vertexDeclaration;
        ENG_VertexBufferBinding bind = mRenderOp.vertexData.vertexBufferBinding;

        decl.addElement((short) POSITION_BINDING, 0,
                VertexElementType.VET_FLOAT3, VertexElementSemantic.VES_POSITION);

        ENG_HardwareVertexBuffer vbuf = ENG_HardwareBufferManager.getSingleton().
                createVertexBuffer(
                        decl.getVertexSize((short) POSITION_BINDING),
                        mRenderOp.vertexData.vertexCount,
                        vBufUsage, true);

        // Bind buffer
        bind.setBinding((short) POSITION_BINDING, vbuf);

        decl.addElement((short) NORMAL_BINDING, 0,
                VertexElementType.VET_FLOAT3, VertexElementSemantic.VES_NORMAL);

        vbuf = ENG_HardwareBufferManager.getSingleton().
                createVertexBuffer(
                        decl.getVertexSize((short) NORMAL_BINDING),
                        mRenderOp.vertexData.vertexCount,
                        vBufUsage, true);

        bind.setBinding((short) NORMAL_BINDING, vbuf);

        FloatBuffer buffer =
                ((ByteBuffer) vbuf.lock(LockOptions.HBL_DISCARD)).asFloatBuffer();

        float[] norm = {0.0f, 0.0f, 1.0f};

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 3; ++j) {
                buffer.put(norm[j]);
            }
        }

        vbuf.unlock();

        if (includeTextureCoords) {
            decl.addElement((short) TEXCOORD_BINDING, 0,
                    VertexElementType.VET_FLOAT3,
                    VertexElementSemantic.VES_TEXTURE_COORDINATES);

            vbuf = ENG_HardwareBufferManager.getSingleton().
                    createVertexBuffer(
                            decl.getVertexSize((short) TEXCOORD_BINDING),
                            mRenderOp.vertexData.vertexCount,
                            vBufUsage, true);

            // Bind buffer
            bind.setBinding((short) TEXCOORD_BINDING, vbuf);

            buffer =
                    ((ByteBuffer) vbuf.lock(LockOptions.HBL_DISCARD)).asFloatBuffer();

            buffer.put(0.0f);
            buffer.put(0.0f);
            buffer.put(0.0f);
            buffer.put(1.0f);
            buffer.put(1.0f);
            buffer.put(0.0f);
            buffer.put(1.0f);
            buffer.put(1.0f);

            vbuf.unlock();
        }

        setMaterial("BaseWhiteNoLighting");

    }

    public void destroy(boolean skipGLDelete) {
        mRenderOp.vertexData.destroy(skipGLDelete);
    }

    public void setCorners(float left, float top, float right, float bottom) {
        setCorners(left, top, right, bottom, true);
    }

    public void setCorners(float left, float top, float right, float bottom,
                           boolean updateAABB) {
        ENG_HardwareVertexBuffer vbuf =
                mRenderOp.vertexData.vertexBufferBinding.getBuffer(
                        (short) POSITION_BINDING);

        FloatBuffer buffer =
                ((ByteBuffer) vbuf.lock(LockOptions.HBL_DISCARD)).asFloatBuffer();

        buffer.put(left);
        buffer.put(top);
        buffer.put(-1.0f);

        buffer.put(left);
        buffer.put(bottom);
        buffer.put(-1.0f);

        buffer.put(right);
        buffer.put(top);
        buffer.put(-1.0f);

        buffer.put(right);
        buffer.put(bottom);
        buffer.put(-1.0f);

        vbuf.unlock();

        if (updateAABB) {
            mBox.setExtents(
                    Math.min(left, right), Math.min(top, bottom), 0,
                    Math.max(left, right), Math.max(top, bottom), 0);
        }
    }

    public void setNormals(ENG_Vector4D topLeft, ENG_Vector4D bottomLeft,
                           ENG_Vector4D topRight, ENG_Vector4D bottomRight) {
        ENG_HardwareVertexBuffer vbuf =
                mRenderOp.vertexData.vertexBufferBinding.getBuffer(
                        (short) NORMAL_BINDING);

        FloatBuffer buffer =
                ((ByteBuffer) vbuf.lock(LockOptions.HBL_DISCARD)).asFloatBuffer();

        buffer.put(topLeft.x);
        buffer.put(topLeft.y);
        buffer.put(topLeft.z);

        buffer.put(bottomLeft.x);
        buffer.put(bottomLeft.y);
        buffer.put(bottomLeft.z);

        buffer.put(topRight.x);
        buffer.put(topRight.y);
        buffer.put(topRight.z);

        buffer.put(bottomRight.x);
        buffer.put(bottomRight.y);
        buffer.put(bottomRight.z);

        vbuf.unlock();
    }

    @Override
    public float getSquaredViewDepth(ENG_Camera cam) {
        
        return 0;
    }

    @Override
    public float getBoundingRadius() {
        
        return 0;
    }
}
