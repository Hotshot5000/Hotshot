/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/9/22, 11:54 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.material;

@Deprecated
public class ENG_Skin {

    public static final int MAX_SKIN_TEXTURE_NUM = 8;

    public boolean alpha;
    public int material;
    public final int[] texture = new int[MAX_SKIN_TEXTURE_NUM];

    public ENG_Skin() {

    }

    /** @noinspection deprecation*/
    public ENG_Skin(ENG_Skin s) {
        this(s.alpha, s.material, s.texture);
    }

    public ENG_Skin(boolean alpha, int material, int[] texture) {
        this.alpha = alpha;
        this.material = material;
        System.arraycopy(texture, 0, this.texture, 0, this.texture.length);
    }
}
