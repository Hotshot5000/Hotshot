/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

public abstract class ENG_FactoryObj<T> {

    public abstract String getType();

    public abstract T createInstance(String name);

    public abstract void destroyInstance(T instance);
}
