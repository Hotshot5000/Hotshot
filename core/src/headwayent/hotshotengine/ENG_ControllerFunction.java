/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

public abstract class ENG_ControllerFunction<T extends ENG_IControllerType<?>>
        implements Comparable<T> {

    private final T deltaCount;
    private final boolean deltaInput;

    public ENG_ControllerFunction(T deltaCount, boolean deltaInput) {
        this.deltaCount = deltaCount;
        this.deltaInput = deltaInput;
    }

    public T getAdjustedInput(T input) {
        if (deltaInput) {
            ENG_IControllerType deltaType = deltaCount;
            deltaType.add(input.get());
            while (deltaType.compare(deltaCount.get(), deltaType.getUpperLimit()) >= 0) {
                deltaType.sub(deltaType.getStep());
            }
            while (deltaType.compare(deltaCount.get(), deltaType.getLowerLimit()) < 0) {
                deltaType.add(deltaType.getStep());
            }
            return deltaCount;
        }
        return input;
    }

    public T getDeltaCount() {
        return deltaCount;
    }

    public abstract T calculate(T eng_IControllerValue);
}
