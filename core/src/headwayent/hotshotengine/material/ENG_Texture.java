/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/9/22, 11:55 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.material;

import java.nio.Buffer;
import java.util.ArrayList;

@Deprecated
public class ENG_Texture {

    public static final int NO_TEXTURE = -1;

    public byte alpha;
    public String name;
    public Buffer data;
    /** @noinspection deprecation*/
    public ArrayList<ENG_Color> colorKeys;
    public int colorKeysNum;

    public ENG_Texture() {

    }

    /** @noinspection deprecation*/
    public ENG_Texture(byte alpha, String name, Buffer data,
                       ArrayList<ENG_Color> colorKeys, int colorKeysNum) {
        this.alpha = alpha;
        this.name = name;
        this.data = data;
        this.colorKeys = colorKeys;
        this.colorKeysNum = colorKeysNum;
    }

    /** @noinspection deprecation*/
    public ENG_Texture(ENG_Texture t) {
        this(t.alpha, t.name, t.data, t.colorKeys, t.colorKeysNum);
    }

    /** @noinspection deprecation */
    public static void copyTexture(ENG_Texture dest, ENG_Texture src) {
        dest.alpha = src.alpha;
        dest.name = src.name;
        dest.data = src.data;
        dest.colorKeys = src.colorKeys;
        dest.colorKeysNum = src.colorKeysNum;
    }
}
