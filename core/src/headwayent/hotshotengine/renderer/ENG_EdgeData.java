/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Byte;

import java.util.ArrayList;

public class ENG_EdgeData {

    public static class Triangle {
        /**
         * The set of indexes this triangle came from (NB it is possible that the triangles on
         * one side of an edge are using a different vertex buffer from those on the other side.)
         */
        public int indexSet;
        /**
         * The vertex set these vertices came from.
         */
        public int vertexSet;
        public final int[] vertIndex = new int[3];/// Vertex indexes, relative to the original buffer
        public final int[] sharedVertIndex = new int[3]; /// Vertex indexes, relative to a shared vertex buffer with
        // duplicates eliminated (this buffer is not exposed)
    }

    public static class Edge {
        /**
         * The indexes of the 2 tris attached, note that tri 0 is the one where the
         * indexes run _anti_ clockwise along the edge. Indexes must be
         * reversed for tri 1.
         */
        public final int[] triIndex = new int[2];
        /**
         * The vertex indices for this edge. Note that both vertices will be in the vertex
         * set as specified in 'vertexSet', which will also be the same as tri 0
         */
        public final int[] vertIndex = new int[2];
        /**
         * Vertex indices as used in the shared vertex list, not exposed.
         */
        public final int[] sharedVertIndex = new int[2];
        /**
         * Indicates if this is a degenerate edge, ie it does not have 2 triangles
         */
        public boolean degenerate;
    }

    public static class EdgeGroupList {
        /**
         * The vertex set index that contains the vertices for this edge group.
         */
        public int vertexSet;
        /**
         * Pointer to vertex data used by this edge group.
         */
        public ENG_VertexData vertexData;
        /**
         * Index to main triangles array, indicate the first triangle of this edge
         * group, and all triangles of this edge group are stored continuous in
         * main triangles array.
         */
        public int triStart;
        /**
         * Number triangles of this edge group.
         */
        public int triCount;
        /**
         * The edges themselves.
         */
        public final ArrayList<Edge> edges = new ArrayList<>();
    }

    public final ArrayList<Triangle> triangles = new ArrayList<>();

    public final ArrayList<ENG_Vector4D> triangleFaceNormals =
            new ArrayList<>();

    public final ArrayList<ENG_Byte> triangleLightFacings =
            new ArrayList<>();

    public final ArrayList<EdgeGroupList> edgeGroups =
            new ArrayList<>();

    public boolean isClosed;

    public void updateTriangleLightFacing(ENG_Vector4D lightPos) {

    }

    public void updateFaceNormals(int vertexSet,
                                  ENG_HardwareVertexBuffer positionBuffer) {

    }
}
