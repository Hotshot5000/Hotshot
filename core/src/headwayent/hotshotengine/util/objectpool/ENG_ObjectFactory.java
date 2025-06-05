/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/23/18, 3:13 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.util.objectpool;

/**
 * Created by sebas on 22-Feb-18.
 */

public interface ENG_ObjectFactory<T extends ENG_PoolObject> {

    T create();
    void destroy(T obj);
}
