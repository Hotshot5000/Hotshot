/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public class ENG_RenderOperation {

    /// The rendering operation type to perform
    public enum OperationType {
        /// A list of points, 1 vertex per point
        OT_POINT_LIST(1),
        /// A list of lines, 2 vertices per line
        OT_LINE_LIST(2),
        /// A strip of connected lines, 1 vertex per line plus 1 start vertex
        OT_LINE_STRIP(3),
        /// A list of triangles, 3 vertices per triangle
        OT_TRIANGLE_LIST(4),
        /// A strip of triangles, 3 vertices for the first triangle, and 1 per triangle after that
        OT_TRIANGLE_STRIP(5),
        /// A fan of triangles, 3 vertices for the first triangle, and 1 per triangle after that
        OT_TRIANGLE_FAN(6);

        private final int type;

        OperationType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public static OperationType getOperationType(short type) {
            switch (type) {
                case 1:
                    return OT_POINT_LIST;
                case 2:
                    return OT_LINE_LIST;
                case 3:
                    return OT_LINE_STRIP;
                case 4:
                    return OT_TRIANGLE_LIST;
                case 5:
                    return OT_TRIANGLE_STRIP;
                case 6:
                    return OT_TRIANGLE_FAN;
                default:
                    throw new IllegalArgumentException(type + " type is not a valid " +
                            "OperationType");
            }
        }
    }

    public ENG_VertexData vertexData;
    public OperationType operationType = OperationType.OT_TRIANGLE_LIST;
    public boolean useIndexes = true;
    public ENG_IndexData indexData;
    public ENG_Renderable srcRenderable;

    public ENG_RenderOperation() {

    }

    public void set(ENG_RenderOperation op) {
        vertexData = op.vertexData;
        operationType = op.operationType;
        useIndexes = op.useIndexes;
        indexData = op.indexData;
        srcRenderable = op.srcRenderable;
    }

    public void initVertexData() {
        vertexData = new ENG_VertexData();
    }

    public void setOperationType(short type) {
        operationType = OperationType.getOperationType(type);
    }

    /**
     * Only for GIWS
     *
     * @return
     */
    public ENG_VertexData getVertexData() {
        return vertexData;
    }

    /**
     * For GIWS
     */
    public void setVertexDataNull() {
        vertexData = null;
    }


}
