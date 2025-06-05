/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:01 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

public interface ENG_NumberOperations<E> {

    ENG_NumberOperations<E> add(ENG_NumberOperations<E> oth);

    ENG_NumberOperations<E> sub(ENG_NumberOperations<E> oth);

    ENG_NumberOperations<E> mul(ENG_NumberOperations<E> oth);

    ENG_NumberOperations<E> div(ENG_NumberOperations<E> oth);

    ENG_NumberOperations<E> mul(float oth);

    E get();

    void set(E val);
}
