/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

public class ENG_OverlayPanelContainer extends ENG_OverlayContainerResource {

    public static class Tiling {
        public final int layer;
        public final int x;
        public final int y;

        public Tiling(int layer, int x, int y) {
            this.layer = layer;
            this.x = x;
            this.y = y;
        }
    }

    public static class UVCoords {
        public final float topleftU;
        public final float topleftV;
        public final float bottomrightU;
        public final float bottomrightV;

        public UVCoords(float tlu, float tlv, float bru, float brv) {
            topleftU = tlu;
            topleftV = tlv;
            bottomrightU = bru;
            bottomrightV = brv;
        }
    }

    public boolean transparent;
    public Tiling tiling;
    public UVCoords uvCoords;
}
