/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

import headwayent.hotshotengine.renderer.ENG_Font.FontType;

import java.util.ArrayList;

public class ENG_FontResource {

    public static class GlyphStats {
        public final String c;
        public final float u1;
        public final float v1;
        public final float u2;
        public final float v2;

        public GlyphStats(String glyph, float u1, float v1, float u2, float v2) {
            c = glyph;
            this.u1 = u1;
            this.v1 = v1;
            this.u2 = u2;
            this.v2 = v2;
        }
    }

    public String name;
    public FontType type = FontType.FT_IMAGE;
    public String textureSource;
    public ArrayList<GlyphStats> glyph;
}
