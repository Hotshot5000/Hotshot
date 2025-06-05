/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 10:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.exception.ENG_UndeclaredIdentifierException;
import headwayent.hotshotengine.renderer.ENG_Light.LightTypes;

import java.util.TreeMap;

@Deprecated
public class ENG_LightFactory extends ENG_MovableObjectFactory {

    public static final String FACTORY_TYPE_NAME = "Light";

    public ENG_LightFactory() {

    }

    /** @noinspection deprecation */
    @Override
    protected Object createInstanceImpl(String name,
                                                   TreeMap<String, String> params) {

        ENG_Light light = new ENG_Light(name);

        if (params != null) {
            String ni = params.get("type");
            if (ni != null) {
                switch (ni) {
                    case "point":
                        light.setType(LightTypes.LT_POINT);
                        break;
                    case "directional":
                        light.setType(LightTypes.LT_DIRECTIONAL);
                        break;
                    case "spotlight":
                        light.setType(LightTypes.LT_SPOTLIGHT);
                        break;
                    default:
                        throw new ENG_UndeclaredIdentifierException(
                                "Invalid light type: " + ni);
                }
            }
        }
        return light;
    }

    @Override
    public void destroyInstance(Object obj, boolean skipGLDelete) {


    }

    @Override
    public String getType() {

        return FACTORY_TYPE_NAME;
    }

}
