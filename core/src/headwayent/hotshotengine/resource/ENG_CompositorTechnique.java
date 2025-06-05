/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

import java.util.ArrayList;
import java.util.HashMap;

public class ENG_CompositorTechnique {

    public String name;
    public String compositorLogic;
    public HashMap<String, ENG_TextureResource> textureList;
    public final ArrayList<ENG_CompositorTarget> targetList =
            new ArrayList<>();
}
