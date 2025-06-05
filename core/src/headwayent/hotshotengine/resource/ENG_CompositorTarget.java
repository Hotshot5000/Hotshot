/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

import java.util.ArrayList;

public class ENG_CompositorTarget {

    public static final String TARGET_OUTPUT_NAME = "output";

    public static final int INPUT_NONE = 0;
    public static final int INPUT_PREVIOUS = 1;

    public static final int VISIBILITY_MASK_DEFAULT = -1;

    public String name;
    public final ArrayList<ENG_CompositorPass> passList = new ArrayList<>();
    public int input;
    public int visibilityMask;
    public boolean onlyOnce;
    public boolean output;
}
