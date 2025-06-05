/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.LockOptions;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.Usage;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementType;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ENG_Pose {

    /// Target geometry index
    protected final short mTarget;
    /// Optional name
    protected final String mName;
    protected final TreeMap<ENG_Integer, ENG_Vector3D> mVertexOffsetMap =
            new TreeMap<>();
    protected ENG_HardwareVertexBuffer mBuffer;

    public ENG_Pose(short target) {
        this(target, "");
    }

    public ENG_Pose(short target, String name) {

        mTarget = target;
        mName = name;
    }

    public short getTarget() {
        return mTarget;
    }

    public String getName() {
        return mName;
    }

    private void nullifyBuffer() {
        nullifyBuffer(false);
    }

    private void nullifyBuffer(boolean skipGLDelete) {
        if (mBuffer != null) {
            mBuffer.destroy(skipGLDelete);
            mBuffer = null;
        }
    }

    public void addVertex(int index, ENG_Vector3D v) {
        mVertexOffsetMap.put(new ENG_Integer(index), v);
        nullifyBuffer();
    }

    public void removeVertex(int index) {
        ENG_Vector3D remove = mVertexOffsetMap.remove(new ENG_Integer(index));
        if (remove != null) {
            nullifyBuffer();
        }
    }

    public void clearVertexOffsets() {
        mVertexOffsetMap.clear();
        nullifyBuffer();
    }

    public TreeMap<ENG_Integer, ENG_Vector3D> getVertexOffsets() {
        return mVertexOffsetMap;
    }

    public ENG_Pose clone() {
        ENG_Pose pose = new ENG_Pose(mTarget, mName);
        pose.mVertexOffsetMap.putAll(mVertexOffsetMap);
        return pose;
    }

    public ENG_HardwareVertexBuffer _getHardwareVertexBuffer(int vertexCount) {

        if (mBuffer == null) {
            mBuffer = ENG_HardwareBufferManager.getSingleton()
                    .createVertexBuffer(
                            ENG_VertexElement.getTypeSize(
                                    VertexElementType.VET_FLOAT3),
                            vertexCount,
                            Usage.HBU_STATIC_WRITE_ONLY.getUsage());
            FloatBuffer buf =
                    ((ByteBuffer) mBuffer.lock(LockOptions.HBL_DISCARD))
                            .asFloatBuffer();
            ENG_Utility.memset(buf, 0.0f);
            for (Entry<ENG_Integer, ENG_Vector3D> entry :
                    mVertexOffsetMap.entrySet()) {
                buf.position(entry.getKey().getValue());
                ENG_Vector3D value = entry.getValue();
                buf.put(value.x);
                buf.put(value.y);
                buf.put(value.z);
            }
            buf.rewind();
        }
        return mBuffer;
    }


}
