/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.pipeline;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Boolean;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Long;
import headwayent.hotshotengine.renderer.ENG_Camera;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointerWithSetter;

import static headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls.readVector4DFull;

/**
 * This class exists only for performance reasons. It pools objects for reuse, such as
 * FrameEndFrameListeners.
 * There probably should be 2 layers of pooling, one at the ByteBuffer level and one
 * at a higher level for objects such as EndListeners. The Higher level objects can select
 * from the lower level pool of ByteBuffers based on the needed size.
 * But, for the beginning, the pooling will only have the higher level objects.
 * Expand if needed for more performance.
 */
public class ENG_NativeCallsObjectPool {

    private static class ENG_FrameEndListenerForObjectCreation extends ENG_RenderedFrameListenerWithBufferCopyWithLock {
        private ENG_NativePointerWithSetter obj;

        public ENG_FrameEndListenerForObjectCreation() {
            super(ENG_Long.SIZE_IN_BYTES, true);
        }

        public ENG_NativePointerWithSetter getObj() {
            return obj;
        }

        public void setObj(ENG_NativePointerWithSetter obj) {
            this.obj = obj;
            reset();
        }

        @Override
        public void runOnMainThread(ByteBuffer responseBuffer) {
            obj.setPointer(responseBuffer.getLong());
            obj.setNativePointer(true);
            addFrameEndListenerForObjectCreation(this);
        }


    }

    private static class ENG_FrameEndListenerForCameraProjectionMatrixRead extends ENG_RenderedFrameListenerWithBufferCopyWithLock {
        private ENG_Camera cameraPtr;
        private ENG_Matrix4 ret;
        private ENG_Boolean matrixSet;
        private final ENG_Vector4D temp = new ENG_Vector4D();

        public ENG_FrameEndListenerForCameraProjectionMatrixRead() {
            super(ENG_Float.SIZE_IN_BYTES * 16, false);
        }

        public void setData(ENG_Camera camera, ENG_Matrix4 ret, ENG_Boolean matrixSet, final int writeableBuffer) {
            this.cameraPtr = camera;
            this.ret = ret;
            this.matrixSet = matrixSet;
            long frameNum = MainApp.getMainThread().getFrameNum();
            reset();
        }

        @Override
        public void runOnMainThread(ByteBuffer responseBuffer) {
            for (int i = 0; i < 4; ++i) {
                readVector4DFull(responseBuffer, temp);
                ret.setRow(i, temp);

            }
            cameraPtr.setProjectionMatrix(ret);
            matrixSet.setValue(true);
//            System.out.println("projMatrix writeableBuffer: " + writeableBuffer +
//                    " currentWriteableBuffer: " + ENG_RenderingThread.getCurrentWriteableBuffer() +
//                    " frameNum: " + frameNum + " currentFrameNum: " + MainApp.getMainThread().getFrameNum());
            addProjectionMatrixListener(this);
        }


    }

    private static class ENG_FrameEndListenerForCameraViewMatrixRead extends ENG_RenderedFrameListenerWithBufferCopyWithLock {
        private ENG_Camera cameraPtr;
        private ENG_Matrix4 ret;
        private ENG_Boolean matrixSet;
        private final ENG_Vector4D temp = new ENG_Vector4D();

        public ENG_FrameEndListenerForCameraViewMatrixRead() {
            super(ENG_Float.SIZE_IN_BYTES * 16, false);
        }

        public void setData(ENG_Camera camera, ENG_Matrix4 ret, ENG_Boolean matrixSet, final int writeableBuffer) {
            this.cameraPtr = camera;
            this.ret = ret;
            this.matrixSet = matrixSet;
            long frameNum = MainApp.getMainThread().getFrameNum();
            reset();
        }

        @Override
        public void runOnMainThread(ByteBuffer responseBuffer) {
            for (int i = 0; i < 4; ++i) {
                readVector4DFull(responseBuffer, temp);
                ret.setRow(i, temp);

            }
            cameraPtr.setViewMatrix(ret);
            matrixSet.setValue(true);
//            System.out.println("viewMatrix writeableBuffer: " + writeableBuffer +
//                    " currentWriteableBuffer: " + ENG_RenderingThread.getCurrentWriteableBuffer() +
//                    " frameNum: " + frameNum + " currentFrameNum: " + MainApp.getMainThread().getFrameNum());
            addViewMatrixListener(this);
        }


    }

    private static final LinkedList<ENG_FrameEndListenerForObjectCreation> frameEndListenerForObjectCreationList = new LinkedList<>();
    private static final LinkedList<ENG_FrameEndListenerForCameraProjectionMatrixRead> frameEndListenerForCameraProjectionMatrixReadList = new LinkedList<>();
    private static final LinkedList<ENG_FrameEndListenerForCameraViewMatrixRead> frameEndListenerForCameraViewMatrixReadList = new LinkedList<>();

    public static ENG_RenderedFrameListenerWithBufferCopyWithLock getFrameEndListenerForObjectCreation(
            final ENG_NativePointerWithSetter obj) {
        ENG_FrameEndListenerForObjectCreation poll = frameEndListenerForObjectCreationList.poll();
        if (poll == null) {
            poll = new ENG_FrameEndListenerForObjectCreation();
        }
        poll.setObj(obj);
        return poll;
    }

    private static void addFrameEndListenerForObjectCreation(ENG_FrameEndListenerForObjectCreation endListener) {
        frameEndListenerForObjectCreationList.add(endListener);
    }

    public static ENG_RenderedFrameListenerWithBufferCopyWithLock getProjectionMatrixListener(
            final ENG_Camera cameraPtr, final ENG_Matrix4 ret, final ENG_Boolean matrixSet, final int writeableBuffer) {
        ENG_FrameEndListenerForCameraProjectionMatrixRead poll = frameEndListenerForCameraProjectionMatrixReadList.poll();
        if (poll == null) {
            poll = new ENG_FrameEndListenerForCameraProjectionMatrixRead();
        }
        poll.setData(cameraPtr, ret, matrixSet, writeableBuffer);
        return poll;
    }

    private static void addProjectionMatrixListener(ENG_FrameEndListenerForCameraProjectionMatrixRead endListener) {
        frameEndListenerForCameraProjectionMatrixReadList.add(endListener);
    }

    public static ENG_RenderedFrameListenerWithBufferCopyWithLock getViewMatrixListener(
            final ENG_Camera cameraPtr, final ENG_Matrix4 ret, final ENG_Boolean matrixSet, final int writeableBuffer) {
        ENG_FrameEndListenerForCameraViewMatrixRead poll = frameEndListenerForCameraViewMatrixReadList.poll();
        if (poll == null) {
            poll = new ENG_FrameEndListenerForCameraViewMatrixRead();
        }
        poll.setData(cameraPtr, ret, matrixSet, writeableBuffer);
        return poll;
    }

    private static void addViewMatrixListener(ENG_FrameEndListenerForCameraViewMatrixRead endListener) {
        frameEndListenerForCameraViewMatrixReadList.add(endListener);
    }
}
