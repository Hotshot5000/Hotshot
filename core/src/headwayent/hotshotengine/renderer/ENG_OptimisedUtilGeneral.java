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
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.renderer.ENG_EdgeData.Triangle;

import java.nio.ByteBuffer;

public class ENG_OptimisedUtilGeneral extends ENG_OptimisedUtil {

    // NOT THREAD SAFE. BE SURE TO CHECK THIS IF IT BECOME MT
    private final ENG_Vector4D sourceVec = new ENG_Vector4D();
    private final ENG_Vector4D sourceNorm = new ENG_Vector4D();
    private final ENG_Vector4D accumVecPos = new ENG_Vector4D();
    private final ENG_Vector4D accumVecNorm = new ENG_Vector4D();

    @Override
    public void softwareVertexSkinning(ByteBuffer srcPosPtr, int srcPosPtrBase,
                                       ByteBuffer destPosPtr, int destPosPtrBase,
                                       ByteBuffer srcNormPtr, int srcNormPtrBase,
                                       ByteBuffer destNormPtr, int destNormPtrBase,
                                       ByteBuffer blendWeightPtr, int blendWeightBase,
                                       ByteBuffer blendIndexPtr, int blendIndexBase,
                                       ENG_Matrix4[] blendMatrices,
                                       int srcPosStride, int destPosStride, int srcNormStride,
                                       int destNormStride, int blendWeightStride, int blendIndexStride,
                                       int numWeightsPerVertex, int numVertices) {
        

	/*	int srcPosPtrPos = srcPosPtr.position();
		int destPosPtrPos = destPosPtr.position();
		int srcNormPtrPos = srcNormPtr.position();
		int destNormPtrPos = destNormPtr.position();
		int blendWeightPtrPos = blendWeightPtr.position();
		int blendIndexPtrPos = blendIndexPtr.position();*/

        sourceVec.set(ENG_Math.PT4_ZERO);
        sourceNorm.set(ENG_Math.PT4_ZERO);
        accumVecPos.set(ENG_Math.PT4_ZERO);
        accumVecNorm.set(ENG_Math.PT4_ZERO);
		
	/*	int srcPosPtrBase = 0;
		int srcNormPtrBase = 0;
		int destPosPtrBase = 0;
		int destNormPtrBase = 0;
		int blendWeightBase = 0;
		int blendIndexBase = 0;
	*/

        for (int vertIdx = 0; vertIdx < numVertices; ++vertIdx) {


            sourceVec.x = srcPosPtr.getFloat(srcPosPtrBase);
            sourceVec.y = srcPosPtr.getFloat(srcPosPtrBase + ENG_Float.SIZE_IN_BYTES);
            sourceVec.z = srcPosPtr.getFloat(srcPosPtrBase + 2 * ENG_Float.SIZE_IN_BYTES);

            if (srcNormPtr != null) {
                sourceNorm.x = srcNormPtr.getFloat(srcNormPtrBase);
                sourceNorm.y = srcNormPtr.getFloat(srcNormPtrBase + ENG_Float.SIZE_IN_BYTES);
                sourceNorm.z = srcNormPtr.getFloat(srcNormPtrBase + 2 * ENG_Float.SIZE_IN_BYTES);
            }

            accumVecPos.set(ENG_Math.PT4_ZERO);
            accumVecNorm.set(ENG_Math.PT4_ZERO);


            for (short blendIdx = 0;
                 blendIdx < numWeightsPerVertex; ++blendIdx) {
                float weight =
                        blendWeightPtr.getFloat(blendWeightBase + blendIdx);
//				accumVecPos.set(sourceVec);
//				accumVecNorm.set(sourceNorm);
                if (weight != 0.0f) {
                    ENG_Matrix4 matrix =
                            blendMatrices[blendIndexPtr.get(
                                    blendIndexBase + blendIdx)];
                    float[] mat = matrix.get();

                    accumVecPos.x +=
                            (mat[0] * sourceVec.x +
                                    mat[1] * sourceVec.y +
                                    mat[2] * sourceVec.z +
                                    mat[3])
                                    * weight;
                    accumVecPos.y +=
                            (mat[4] * sourceVec.x +
                                    mat[5] * sourceVec.y +
                                    mat[6] * sourceVec.z +
                                    mat[7])
                                    * weight;
                    accumVecPos.z +=
                            (mat[8] * sourceVec.x +
                                    mat[9] * sourceVec.y +
                                    mat[10] * sourceVec.z +
                                    mat[11])
                                    * weight;

                    if (srcNormPtr != null) {

                        accumVecNorm.x +=
                                (mat[0] * sourceNorm.x +
                                        mat[1] * sourceNorm.y +
                                        mat[2] * sourceNorm.z)
                                        * weight;
                        accumVecNorm.y +=
                                (mat[4] * sourceNorm.x +
                                        mat[5] * sourceNorm.y +
                                        mat[6] * sourceNorm.z)
                                        * weight;
                        accumVecNorm.z +=
                                (mat[8] * sourceNorm.x +
                                        mat[9] * sourceNorm.y +
                                        mat[10] * sourceNorm.z)
                                        * weight;
                    }
                }
            }

            destPosPtr.putFloat(destPosPtrBase, accumVecPos.x);
            destPosPtr.putFloat(destPosPtrBase + ENG_Float.SIZE_IN_BYTES, accumVecPos.y);
            destPosPtr.putFloat(destPosPtrBase + 2 * ENG_Float.SIZE_IN_BYTES, accumVecPos.z);

            if (srcNormPtr != null) {
                accumVecNorm.normalize();
                destNormPtr.putFloat(destNormPtrBase, accumVecNorm.x);
                destNormPtr.putFloat(destNormPtrBase + ENG_Float.SIZE_IN_BYTES, accumVecNorm.y);
                destNormPtr.putFloat(destNormPtrBase + 2 * ENG_Float.SIZE_IN_BYTES, accumVecNorm.z);

                srcNormPtrBase += srcNormStride;
                destNormPtrBase += destNormStride;
            }

            srcPosPtrBase += srcPosStride;
            destPosPtrBase += destPosStride;
            blendIndexBase += blendIndexStride;
            blendWeightBase += blendWeightStride;
        }
    }

    @Override
    public void softwareVertexMorph(float t, ByteBuffer srcPos1,
                                    ByteBuffer srcPos2, ByteBuffer dstPos, int numVertices) {
        

        int srcPos1Pos = srcPos1.position();
        int srcPos2Pos = srcPos2.position();
        int dstPosPos = dstPos.position();
        for (int i = 0; i < numVertices; ++i) {
            float f = srcPos1.getFloat();
            dstPos.putFloat(f + t * (srcPos2.getFloat() - f));

            f = srcPos1.getFloat();
            dstPos.putFloat(f + t * (srcPos2.getFloat() - f));

            f = srcPos1.getFloat();
            dstPos.putFloat(f + t * (srcPos2.getFloat() - f));
        }

        srcPos1.position(srcPos1Pos);
        srcPos2.position(srcPos2Pos);
        dstPos.position(dstPosPos);
    }

    @Override
    public void concatenateAffineMatrices(ENG_Matrix4 baseMatrix,
                                          ENG_Matrix4[] srcMatrices, ENG_Matrix4[] dstMatrices,
                                          int numMatrices) {
        

        for (int i = 0; i < numMatrices; ++i) {
            baseMatrix.concatenateAffine(srcMatrices[i], dstMatrices[i]);
        }
    }

    @Override
    public void calculateFaceNormals(ByteBuffer positions,
                                     Triangle[] triangles, ENG_Vector4D[] faceNormals, int numTriangles) {
        

    }

    @Override
    public void calculateLightFacing(ENG_Vector4D lightPos,
                                     ENG_Vector4D[] faceNormals, ByteBuffer lightFacings, int numFaces) {
        

    }

    @Override
    public void extrudeVertices(ENG_Vector4D lightPos, float extrudeDist,
                                ByteBuffer srcPositions, ByteBuffer destPositions, int numVertices) {
        

    }

}
