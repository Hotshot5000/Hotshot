/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 10:55 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.TreeMap;

@Deprecated
public class ENG_BillboardSetFactory extends ENG_MovableObjectFactory {

    public static final String FACTORY_TYPE_NAME = "BillboardSet";

    /** @noinspection deprecation */
    @Override
    protected Object createInstanceImpl(String name,
                                                   TreeMap<String, String> params) {

        int poolSize = 0;
        boolean externalData = false;
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
            return new ENG_BillboardSet(name, poolSize, externalData);
        }
        return new ENG_BillboardSet(name, 20);
    }

    @Override
    public String getType() {

        return FACTORY_TYPE_NAME;
    }

    /** @noinspection deprecation*/
    @Override
    public void destroyInstance(Object obj, boolean skipGLDelete) {


        ((ENG_BillboardSet) obj).destroy(skipGLDelete);
    }

}
