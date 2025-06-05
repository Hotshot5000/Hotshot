/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import java.util.ArrayList;
import java.util.TreeMap;

public class ENG_ParamDictionary {

    private final ArrayList<ENG_ParameterDef> mParamDefs = new ArrayList<>();
    private final TreeMap<String, ENG_ParamCommand> mParamCommands =
            new TreeMap<>();

    public ENG_ParamCommand getParamCommand(String name) {
        return mParamCommands.get(name);
    }

    public ArrayList<ENG_ParameterDef> getParameterList() {
        return mParamDefs;
    }

    public void addParameter(ENG_ParameterDef paramDef, ENG_ParamCommand cmd) {
        mParamDefs.add(paramDef);
        mParamCommands.put(paramDef.name, cmd);
    }
}
