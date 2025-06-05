/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import headwayent.blackholedarksun.Animation;

import com.artemis.Entity;

public class ShipHitAnimationFactory extends AnimationFactory {

    public ShipHitAnimationFactory(String prefix) {
        super(prefix);
    }

    @Override
    public Animation createInstance(Entity entity) {

        return new ShipHitAnimation(getPrefix() + getInstanceNum(), entity);
    }

    @Override
    public void destroyInstance(Animation anim) {


    }

}
