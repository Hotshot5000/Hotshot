/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import headwayent.hotshotengine.ENG_Vector2D;

import java.util.ArrayList;

class ENG_Glyph {

    public final ENG_Vector2D[] texCoords = new ENG_Vector2D[4];
    public float uvTop, uvBottom, uvWidth, uvHeight, uvLeft, uvRight,
            glyphWidth, glyphHeight, glyphAdvance, verticalOffset;
    final ArrayList<ENG_Kerning> kerning = new ArrayList<>();

    ENG_Glyph() {
        
        for (int i = 0; i < texCoords.length; ++i) {
            texCoords[i] = new ENG_Vector2D();
        }
    }

    float getKerning(int lastChar) {
        if (kerning.isEmpty()) {
            return 0.0f;
        }
        for (ENG_Kerning k : kerning) {
            if (k.character == (lastChar & 0xFF)) {
                return k.kerning;
            }
        }
        return 0.0f;
    }

}
