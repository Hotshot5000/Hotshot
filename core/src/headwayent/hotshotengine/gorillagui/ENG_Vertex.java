/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

class ENG_Vertex {

    public final ENG_Vector3D position = new ENG_Vector3D();
    public final ENG_ColorValue colour = new ENG_ColorValue();
    public final ENG_Vector2D uv = new ENG_Vector2D();

    ENG_Vertex() {

    }

    ENG_Vertex(ENG_Vertex v) {
        set(v);
    }

    public void set(ENG_Vertex v) {
        position.set(v.position);
        colour.set(v.colour);
        uv.set(v.uv);
    }

}
