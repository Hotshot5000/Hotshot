/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public class ENG_VertexBoneAssignment implements Comparable<ENG_VertexBoneAssignment> {

    public int vertexIndex;
    public short boneIndex;
    public float weight;

    @Override
    public int compareTo(ENG_VertexBoneAssignment another) {
        
        // For sorting in the multimap used by compileBoneAssignments in mesh
        // There we assume it's sorted by vertexIndex and each vertexIndex
        // is sorted by boneIndex
        return boneIndex - another.boneIndex;
    }
}
