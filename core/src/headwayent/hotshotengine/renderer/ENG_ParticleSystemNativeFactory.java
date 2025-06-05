/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.TreeMap;

/**
 * Created by sebas on 28.06.2017.
 */

public class ENG_ParticleSystemNativeFactory extends ENG_MovableObjectFactoryWithId {

    public static final String FACTORY_TYPE_NAME = "ParticleSystemNative";

    @Override
    protected Object createInstanceImpl(String name, TreeMap<String, String> params) {
        long id = getIdParam(params);
        String template = "";
        if (params != null) {
            template = params.get("templateName");
//            if (template != null) {
//                return ENG_ParticleSystemManager.getSingleton().createSystemImpl(
//                        name, template);
//            }
        }
        int quota = 500;
        if (params != null) {
            String q = params.get("quota");
            try {
                quota = Integer.parseInt(q);
            } catch (NumberFormatException e) {
//                throw new ENG_InvalidFormatParsingException("invalid quota");
            }
        }
        return new ENG_ParticleSystemNative(id, name, template, quota);
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
        ((ENG_ParticleSystemNative) obj).destroy();
    }
}
