/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 9:57 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.EntitySystem;

/**
 * This system has an empty aspect so it processes no entities, but it still gets invoked.
 * You can use this system if you need to execute some game logic and not have to concern
 * yourself about aspects or entities.
 *
 * @author Arni Arent
 */
public abstract class VoidEntitySystem extends EntitySystem {

    /** @noinspection deprecation*/
    public VoidEntitySystem() {
        super(Aspect.getEmpty());
    }

//    @Override
//    protected final void processEntities(ImmutableBag<Entity> entities) {
//        processSystem();
//    }

    protected abstract void processSystem();

    @Override
    protected boolean checkProcessing() {
        return true;
    }

}
