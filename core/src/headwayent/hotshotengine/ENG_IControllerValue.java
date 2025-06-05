/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:01 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

public interface ENG_IControllerValue<T extends ENG_IControllerType<?>> {

    void setValue(T t);

    T getValue();
}
