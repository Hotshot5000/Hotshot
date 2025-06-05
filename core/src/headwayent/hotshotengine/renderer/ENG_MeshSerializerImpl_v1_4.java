/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 10:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;

/** @noinspection deprecation*/
@Deprecated
public class ENG_MeshSerializerImpl_v1_4 extends ENG_MeshSerializerImpl {

    public ENG_MeshSerializerImpl_v1_4() {
        
        mVersion = "[MeshSerializer_v1.40]";
    }

    /** @noinspection deprecation*/
    @Override
    public void readMeshLodInfo(ENG_Mesh mesh) {

        String strategyName = readString();
        ENG_LodStrategy strategy = ENG_LodStrategyManager.getSingleton()
                .getStrategy(strategyName);
        mesh.setLodStrategy(strategy);

        mesh.mNumLods = readShorts(1)[0];
        mesh.mIsLodManual = readBools(1)[0];

        if (!mesh.mIsLodManual) {
            short numSubMeshes = mesh.getNumSubMeshes();

            for (short i = 0; i < numSubMeshes; ++i) {
                ENG_SubMesh subMesh = mesh.getSubMesh(i);
                subMesh.mLodFaceList.ensureCapacity(mesh.mNumLods - 1);
            /*	for (int j = 0; j < mesh.mNumLods - 1; ++ j) {
					subMesh.mLodFaceList.add(new ENG_IndexData());
				}*/
            }
        }

        for (int i = 1; i < mesh.mNumLods; ++i) {
            short readChunk = readChunk();
            if (readChunk != MeshChunkID.M_MESH_LOD_USAGE.getID()) {
                throw new ENG_InvalidFormatParsingException("Mesh lod usage " +
                        "missing in " + mesh.getName());
            }
            ENG_MeshLodUsage usage = new ENG_MeshLodUsage();
            usage.value = readFloats(1)[0];
            usage.userValue = ENG_Math.sqrt(usage.value);

            if (mesh.isLodManual()) {
                readMeshLodUsageManual(mesh, i, usage);
            } else {
                readMeshLodUsageGenerated(mesh, i, usage);
            }
            usage.edgeData = null;

            mesh.mMeshLodUsageList.add(usage);
        }
    }

}
