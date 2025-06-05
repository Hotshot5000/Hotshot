/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;

import java.util.TreeMap;

public class ENG_ParticleSystemFactory extends ENG_MovableObjectFactory {

    public static final String FACTORY_TYPE_NAME = "ParticleSystem";

    public ENG_ParticleSystemFactory() {
        
    }

    @Override
    protected Object createInstanceImpl(String name,
                                                   TreeMap<String, String> params) {

        if (params != null) {
            String template = params.get("templateName");
            if (template != null) {
                return ENG_ParticleSystemManager.getSingleton().createSystemImpl(
                        name, template);
            }
        }
        int quota = 500;
        if (params != null) {
            String q = params.get("quota");
            try {
                quota = Integer.parseInt(q);
            } catch (NumberFormatException e) {
                throw new ENG_InvalidFormatParsingException("invalid quota");
            }
        }
        return ENG_ParticleSystemManager.getSingleton().createSystemImpl(name, quota);
    }

    @Override
    public String getType() {

        return FACTORY_TYPE_NAME;
    }

    @Override
    public void destroyInstance(Object obj, boolean skipGLDelete) {


        ((ENG_ParticleSystem) obj).destroy(skipGLDelete);
    }

}
