/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

import java.util.List;

public class ENG_GorillaUtility {

    private ENG_GorillaUtility() {

    }

    static void pushVertex(List<ENG_Vertex> vertices,
                                  float x, float y, ENG_Vector2D uv,
                                  ENG_ColorValue col) {
        ENG_Vertex vertex = new ENG_Vertex();
        vertex.position.x = x;
        vertex.position.y = y;
        vertex.position.z = 0.0f;
        vertex.uv.set(uv);
        vertex.colour.set(col);
        vertices.add(vertex);
    }

    static void pushTriangle(List<ENG_Vertex> vertices,
                                    ENG_Vector2D a, ENG_Vector2D b, ENG_Vector2D c,
                                    ENG_Vector2D uv, ENG_ColorValue col) {
        pushVertex(vertices, a.x, a.y, uv, col);
        pushVertex(vertices, b.x, b.y, uv, col);
        pushVertex(vertices, c.x, c.y, uv, col);
    }

    static void pushQuad(List<ENG_Vertex> vertices,
                                ENG_Vector2D[] positions,
                                ENG_ColorValue[] cols, ENG_Vector2D[] uv) {
        pushVertex(vertices, positions[3].x, positions[3].y, uv[3], cols[3]);
        pushVertex(vertices, positions[1].x, positions[1].y, uv[1], cols[1]);
        pushVertex(vertices, positions[0].x, positions[0].y, uv[0], cols[0]);

        pushVertex(vertices, positions[3].x, positions[3].y, uv[3], cols[3]);
        pushVertex(vertices, positions[2].x, positions[2].y, uv[2], cols[2]);
        pushVertex(vertices, positions[1].x, positions[1].y, uv[1], cols[1]);
    }

    static void pushQuad2(List<ENG_Vertex> vertices,
                                 ENG_Vector2D[] positions,
                                 ENG_ColorValue col, ENG_Vector2D[] uv) {
        pushVertex(vertices, positions[3].x, positions[3].y, uv[3], col);
        pushVertex(vertices, positions[1].x, positions[1].y, uv[1], col);
        pushVertex(vertices, positions[0].x, positions[0].y, uv[0], col);

        pushVertex(vertices, positions[3].x, positions[3].y, uv[3], col);
        pushVertex(vertices, positions[2].x, positions[2].y, uv[2], col);
        pushVertex(vertices, positions[1].x, positions[1].y, uv[1], col);
    }

}
