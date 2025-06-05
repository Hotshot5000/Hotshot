/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.IntervalEntityProcessingSystem;

/**
 * Created by sebas on 10.02.2016.
 */
public abstract class MultiplayerDataHolderIntervalSystem extends IntervalEntityProcessingSystem {
    public MultiplayerDataHolderIntervalSystem(Aspect.Builder aspect, float interval) {
        super(aspect, interval);
    }

    @Override
    public void inserted(Entity e) {
        super.inserted(e);
    }

    @Override
    public void removed(Entity e) {
        super.removed(e);
    }
}
