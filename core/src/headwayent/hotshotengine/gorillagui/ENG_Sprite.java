/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import headwayent.hotshotengine.ENG_Vector2D;

public class ENG_Sprite {

    public final ENG_Vector2D[] texCoords = new ENG_Vector2D[4];
    public float uvTop, uvLeft, uvRight, uvBottom, spriteWidth, spriteHeight;

    ENG_Sprite() {
        
        for (int i = 0; i < texCoords.length; ++i) {
            texCoords[i] = new ENG_Vector2D();
        }
    }

}
