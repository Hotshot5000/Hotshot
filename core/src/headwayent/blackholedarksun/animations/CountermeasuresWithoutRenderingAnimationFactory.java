/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import com.artemis.Entity;
import headwayent.blackholedarksun.Animation;

/**
 * Created by sebas on 20.02.2016.
 */
public class CountermeasuresWithoutRenderingAnimationFactory extends AnimationFactory {

    public CountermeasuresWithoutRenderingAnimationFactory(String prefix) {
        super(prefix);
    }

    @Override
    public Animation createInstance(Entity entity) {

        return new CountermeasuresWithoutRenderingAnimation(getPrefix() + getInstanceNum(), entity, CountermeasuresWithoutRenderingAnimation.TOTAL_ANIM_TIME);
    }

    @Override
    public void destroyInstance(Animation anim) {


    }
}
