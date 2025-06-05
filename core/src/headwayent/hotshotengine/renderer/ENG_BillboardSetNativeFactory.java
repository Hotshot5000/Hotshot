/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/29/17, 9:41 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.TreeMap;

/**
 * Created by sebas on 28.06.2017.
 */

public class ENG_BillboardSetNativeFactory extends ENG_MovableObjectFactoryWithId {

    public static final String FACTORY_TYPE_NAME = "BillboardSetNative";

    @Override
    protected Object createInstanceImpl(String name, TreeMap<String, String> params) {
        int poolSize = 0;
        boolean externalData = false;
        long id = getIdParam(params);
        if (params != null) {
            String string = params.get("poolSize");
            if (string != null) {
                poolSize = Integer.parseInt(string);
            }
            string = params.get("externalData");
            if (string != null) {
                externalData = Boolean.parseBoolean(string);
            }
        }
        if (poolSize != 0) {
            return new ENG_BillboardSetNative(id, name, poolSize);
        }
        return new ENG_BillboardSetNative(id, name);
    }

    @Override
    protected Object createInstanceImpl(String name) {
        return super.createInstanceImpl(name);
    }

    @Override
    public String getType() {
        return FACTORY_TYPE_NAME;
    }

    @Override
    public ENG_MovableObject createInstance(String name, ENG_SceneManager manager, TreeMap<String, String> params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ENG_MovableObject createInstance(String name, ENG_SceneManager manager) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void destroyInstance(Object obj, boolean skipGLDelete) {
        ((ENG_BillboardSetNative) obj).destroy();
    }
}
