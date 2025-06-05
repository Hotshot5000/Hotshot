/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import headwayent.blackholedarksun.parser.dispatchers.AbstractEventDispatcher;

public class InitialConds extends Event {

    public static final String TYPE = "InitialConds";
    private final HashMap<String, ArrayList<Param>> map;
    private Skybox skybox;
    private LightDir lightDir;
    private LightType lightType;
    private LightDiffuseColor lightDiffuseColor;
    private LightSpecularColor lightSpecularColor;
    private UseSkyboxDataFromLevel useSkyboxDataFromLevel;
    private LightPowerScale lightPowerScale;
    private AmbientLight ambientLight;
    private final ArrayList<ObjDefinition> objDefinitionList = new ArrayList<>();


    @Override
    public void init() {
        super.init();
        for (Map.Entry<String, ArrayList<Param>> initialCond : map.entrySet()) {
            String s = initialCond.getKey();
            Param param0 = initialCond.getValue().get(0);
            if (s.equalsIgnoreCase(Skybox.TYPE)) {
                skybox = (Skybox) param0;
                param0.init();
            } else if (s.equalsIgnoreCase(LightDir.TYPE)) {
                lightDir = (LightDir) param0;
                param0.init();
            } else if (s.equalsIgnoreCase(LightType.TYPE)) {
                lightType = (LightType) param0;
                param0.init();
            } else if (s.equalsIgnoreCase(LightPowerScale.TYPE)) {
                lightPowerScale = (LightPowerScale) param0;
                param0.init();
            } else if (s.equalsIgnoreCase(LightDiffuseColor.TYPE)) {
                lightDiffuseColor = (LightDiffuseColor) param0;
                param0.init();
            } else if (s.equalsIgnoreCase(LightSpecularColor.TYPE)) {
                lightSpecularColor = (LightSpecularColor) param0;
                param0.init();
            } else if (s.equalsIgnoreCase(AmbientLight.TYPE)) {
                ambientLight = (AmbientLight) param0;
                param0.init();
            } else if (s.equalsIgnoreCase(UseSkyboxDataFromLevel.TYPE)) {
                useSkyboxDataFromLevel = (UseSkyboxDataFromLevel) param0;
                param0.init();
            } else if (s.equalsIgnoreCase(ObjDefinition.TYPE)) {
                for (Param param : initialCond.getValue()) {
                    objDefinitionList.add((ObjDefinition) param);
                    param.init();
                }
            }
        }

    }

    @Override
    public boolean accept(AbstractEventDispatcher dispatcher) {
        return false;
    }

    public InitialConds(HashMap<String, ArrayList<Param>> map) {
        super(TYPE);
        this.map = map;
    }

    public HashMap<String, ArrayList<Param>> getMap() {
        return map;
    }

    public Skybox getSkybox() {
        return skybox;
    }

    public LightDir getLightDir() {
        return lightDir;
    }

    public LightType getLightType() {
        return lightType;
    }

    public LightDiffuseColor getLightDiffuseColor() {
        return lightDiffuseColor;
    }

    public LightSpecularColor getLightSpecularColor() {
        return lightSpecularColor;
    }

    public UseSkyboxDataFromLevel getUseSkyboxDataFromLevel() {
        return useSkyboxDataFromLevel;
    }

    public LightPowerScale getLightPowerScale() {
        return lightPowerScale;
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    public ArrayList<ObjDefinition> getObjDefinitionList() {
        return objDefinitionList;
    }
}
