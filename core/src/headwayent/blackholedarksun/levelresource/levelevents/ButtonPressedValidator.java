/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/17/21, 10:23 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource.levelevents;

import com.artemis.Entity;

import headwayent.blackholedarksun.levelresource.LevelEvent;
import headwayent.blackholedarksun.levelresource.LevelEventValidator;
import headwayent.blackholedarksun.world.WorldManager;

public abstract class ButtonPressedValidator implements LevelEventValidator {

    protected Entity playerShip;
    protected boolean sticky;

    @Override
    public boolean validate(LevelEvent levelEvent) {
        if (sticky) {
            return true;
        }
        return checkSticky(levelEvent);
    }

    public boolean checkSticky(LevelEvent levelEvent) {
        WorldManager worldManager = WorldManager.getSingleton();
        playerShip = worldManager.getPlayerShip();
        return playerShip != null;
    }
}
