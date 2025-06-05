/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:01 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import java.util.Comparator;

public interface ENG_IControllerType<T> extends Comparator<T> {

    void add(T val);

    void sub(T val);

    void mul(T val);

    void div(T val);

    T get();

    T getLowerLimit();

    T getUpperLimit();

    T getStep();
}
