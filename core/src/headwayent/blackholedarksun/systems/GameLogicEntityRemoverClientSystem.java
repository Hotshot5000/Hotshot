/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/24/19, 8:11 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems;

import com.artemis.Aspect;

import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;

/**
 * Created by sebas on 21.02.2016.
 */
public abstract class GameLogicEntityRemoverClientSystem extends GameLogicEntityRemoverSystem {

    public GameLogicEntityRemoverClientSystem(Aspect.Builder aspect) {
        super(aspect);
    }

    public boolean isEntityRemovable(EntityProperties entityProperties, ShipProperties shipProperties) {
        return (entityProperties.isDestroyed() && entityProperties.getDestroyedAnimation() == null)
                || (shipProperties != null && shipProperties.isExited())
                || (entityProperties.isDestroyed() && entityProperties.isDestroyedAnimationFinished())
                || (entityProperties.isDestroyed() && entityProperties.isDestroyedDuringAnimation());
    }
}
