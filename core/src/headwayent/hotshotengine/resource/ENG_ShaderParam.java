/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

import headwayent.hotshotengine.resource.shadertype.ENG_ShaderTypeCustom;
import headwayent.hotshotengine.resource.shadertype.ENG_ShaderTypeStandard;

import java.util.ArrayList;

public class ENG_ShaderParam {

    public final ArrayList<ENG_ShaderTypeStandard> standardType =
            new ArrayList<>();
    public final ArrayList<ENG_ShaderTypeCustom> customType =
            new ArrayList<>();
    public ENG_ShaderNamedParams namedParam;
}
