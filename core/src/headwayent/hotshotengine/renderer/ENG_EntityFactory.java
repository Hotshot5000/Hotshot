/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 10:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.TreeMap;

@Deprecated
public class ENG_EntityFactory extends ENG_MovableObjectFactory {

    public static final String FACTORY_TYPE_NAME = "Entity";

    /** @noinspection deprecation */
    @Override
    protected Object createInstanceImpl(String name,
                                                   TreeMap<String, String> params) {

        if (params == null) {
            throw new IllegalArgumentException("params must be filled with " +
                    "mesh and resourceGroup!");
        }
        ENG_Mesh mesh = ENG_MeshManager.getSingleton().getByName(params.get("mesh"));

        String id = params.get("id");
        long lId = 0;
        if (id != null) {
            lId = Long.parseLong(id);
        }

        return new ENG_Entity(name, lId, mesh);
    }

    /** @noinspection deprecation*/
    @Override
    public void destroyInstance(Object obj, boolean skipGLDelete) {


        ((ENG_Entity) obj).destroy(skipGLDelete);
    }

    @Override
    public String getType() {

        return FACTORY_TYPE_NAME;
    }

}
