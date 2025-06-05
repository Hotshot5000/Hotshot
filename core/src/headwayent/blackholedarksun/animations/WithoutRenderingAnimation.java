/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import headwayent.blackholedarksun.Animation;

/**
 * Created by sebas on 07.01.2016.
 * Only for multiplayer for the server. We don't bother to actually animate anything on the server.
 */
public class WithoutRenderingAnimation extends Animation {
    public WithoutRenderingAnimation(String name, long totalTime) {
        super(name, totalTime);
    }

    @Override
    public void update() {

    }

    @Override
    public void animationFinished() {

    }

    @Override
    public void reloadResources() {

    }

    @Override
    public void destroyResourcesImpl() {

    }
}
