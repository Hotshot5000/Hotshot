/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntity;

/**
 * Created by sebas on 02.01.2016.
 */
public class MultiplayerEntityProcessingClientSystem extends MultiplayerEntityProcessingSystem {
    public MultiplayerEntityProcessingClientSystem() {
        super(Aspect.all(MultiplayerEntity.class));
    }

    @Override
    protected void process(Entity e) {

    }
}
