/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems;

import com.artemis.systems.VoidEntitySystem;
import headwayent.hotshotengine.ENG_Math;

/**
 * Created by sebas on 11.02.2016.
 */
public abstract class IntervalVoidEntitySystem extends VoidEntitySystem {

    private float acc;
    private final float interval;

    public IntervalVoidEntitySystem(float interval) {
        this.interval = interval;
    }

    @Override
    protected boolean checkProcessing() {
        acc += world.getDelta();
        if (ENG_Math.nearlyEqual(acc, interval) || acc > interval) {
            acc -= interval;
            return true;
        }
        return false;
    }
}
