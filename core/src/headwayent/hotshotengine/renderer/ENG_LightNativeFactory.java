/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 7/10/17, 12:24 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.TreeMap;

/**
 * Created by sebas on 09.07.2017.
 */

public class ENG_LightNativeFactory extends ENG_MovableObjectFactoryWithId {

    public static final String FACTORY_TYPE_NAME = "LightNative";

    @Override
    protected Object createInstanceImpl(String name, TreeMap<String, String> params) {
        long id = getIdParam(params);
        return new ENG_LightNative(name, id);
    }

    @Override
    public String getType() {
        return FACTORY_TYPE_NAME;
    }

    @Override
    public void destroyInstance(Object obj, boolean skipGLDelete) {
        ((ENG_LightNative) obj).destroy();
    }
}
