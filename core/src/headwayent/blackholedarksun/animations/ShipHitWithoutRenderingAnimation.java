/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/27/21, 10:23 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import headwayent.blackholedarksun.Animation;

/**
 * Created by sebas on 19.11.2015.
 */
public abstract class ShipHitWithoutRenderingAnimation extends Animation {
    public static final long TOTAL_ANIM_TIME = 800;

    public ShipHitWithoutRenderingAnimation(String name, long totalTime) {
        super(name, totalTime);
    }
}
