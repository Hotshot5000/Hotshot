/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 7/9/17, 10:57 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.TreeMap;

/**
 * This class is used as a hack to determine if it should use the native factory vs the
 * normal factory. It's necessary to extend this class in order to differentiate
 * using instanceof in the scene manager.
 * Created by sebas on 28.06.2017.
 */

abstract class ENG_MovableObjectFactoryWithId extends ENG_MovableObjectFactory {
    protected long getIdParam(TreeMap<String, String> params) {
        String id = params.get(ENG_SceneManager.MOVABLE_OBJECT_PARAM_ID);
        long lId = 0;
        if (id != null) {
            lId = Long.parseLong(id);
        }
        return lId;
    }
}
