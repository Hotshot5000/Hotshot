/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/22/16, 4:51 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import com.artemis.Entity;

/**
 * Created by sebas on 21.02.2016.
 */
public class ProjectileExplosionWithoutRenderingAnimation extends ExplosionWithoutRenderingAnimation {

    public ProjectileExplosionWithoutRenderingAnimation(String name, Entity entity) {
        super(name, entity, TOTAL_ANIM_TIME);
    }

    @Override
    public void update() {
        if (!shipDestroyed) {
            shipDestroyed = true;
            entityProperties.setDestroyedDuringAnimation(true);

        }
    }
}
