/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_EdgeData.Triangle;

import java.nio.ByteBuffer;

public abstract class ENG_OptimisedUtil {

    private static final ENG_OptimisedUtil impl = _detectImplementation();

    public static ENG_OptimisedUtil getImplementation() {
        return impl;
    }

    private static ENG_OptimisedUtil _detectImplementation() {
        return new ENG_OptimisedUtilGeneral();
    }

    public abstract void softwareVertexSkinning(
            ByteBuffer srcPosPtr, int srcPosPtrBase,
            ByteBuffer destPosPtr, int destPosPtrBase,
            ByteBuffer srcNormPtr, int srcNormPtrBase,
            ByteBuffer destNormPtr, int destNormPtrBase,
            ByteBuffer blendWeightPtr, int blendWeightBase,
            ByteBuffer blendIndexPtr, int blendIndexBase,
            ENG_Matrix4[] blendMatrices,
            int srcPosStride, int destPosStride, int srcNormStride,
            int destNormStride, int blendWeightStride, int blendIndexStride,
            int numWeightsPerVertex, int numVertices);

    public abstract void softwareVertexMorph(
            float t,
            ByteBuffer srcPos1, ByteBuffer srcPos2,
            ByteBuffer dstPos,
            int numVertices);

    public abstract void concatenateAffineMatrices(
            ENG_Matrix4 baseMatrix,
            ENG_Matrix4[] srcMatrices,
            ENG_Matrix4[] dstMatrices,
            int numMatrices);

    public abstract void calculateFaceNormals(
            ByteBuffer positions,
            Triangle[] triangles,
            ENG_Vector4D[] faceNormals,
            int numTriangles);

    public abstract void calculateLightFacing(
            ENG_Vector4D lightPos,
            ENG_Vector4D[] faceNormals,
            ByteBuffer lightFacings,
            int numFaces);

    public abstract void extrudeVertices(
            ENG_Vector4D lightPos,
            float extrudeDist,
            ByteBuffer srcPositions,
            ByteBuffer destPositions,
            int numVertices);
}
