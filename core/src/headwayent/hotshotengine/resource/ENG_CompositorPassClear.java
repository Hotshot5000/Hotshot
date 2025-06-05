/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

public class ENG_CompositorPassClear {

    public static final float DEPTH_DEFAULT = 1.0f;

    public float r, g, b, a;
    public float depthValue = 1.0f;
    public int stencilValue;
    public boolean color = true;
    public boolean depth = true;
    public boolean stencil;
}
