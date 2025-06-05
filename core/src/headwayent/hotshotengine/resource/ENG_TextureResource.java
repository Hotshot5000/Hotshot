/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

public class ENG_TextureResource {

	/*PF_A8R8G8B8, PF_R8G8B8A8, PF_R8G8B8, PF_FLOAT16_RGBA, PF_FLOAT16_RGB,
     *  PF_FLOAT16_R, PF_FLOAT32_RGBA, PF_FLOAT32_RGB, and PF_FLOAT32_R. */

    public static final int PF_A8R8G8B8 = 1;
    public static final int PF_R8G8B8A8 = 2;
    public static final int PF_R8G8B8 = 3;
    public static final int PF_FLOAT16_RGBA = 4;
    public static final int PF_FLOAT16_RGB = 5;
    public static final int PF_FLOAT16_R = 6;
    public static final int PF_FLOAT32_RGBA = 7;
    public static final int PF_FLOAT32_RGB = 8;
    public static final int PF_FLOAT32_R = 9;

    public static final int TARGET_WIDTH = -1;
    public static final int TARGET_HEIGHT = -1;
    public static final int TARGET_WIDTH_SCALED = -2;
    public static final int TARGET_HEIGHT_SCALED = -2;

    public static final int LOCAL_SCOPE = 0;
    public static final int CHAIN_SCOPE = 1;
    public static final int GLOBAL_SCOPE = 2;

    public String name;
    public int width, height;
    public String format;
    public int scope;
    public float widthFactor, heightFactor;
    public boolean pooled;
}
