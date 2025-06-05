/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.automationframework;

import com.artemis.Entity;

import headwayent.blackholedarksun.Animation;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.hotshotengine.ENG_Utility;

import static headwayent.hotshotengine.ENG_Utility.currentTimeMillis;

public class ShipDestructionAutomation extends AutomationFramework {

    private final String levelObjectName;
    private final long delayToDestruction;
    private boolean destructionStarted;
    private boolean startingTimeSet;
    private long startingTime;

    public ShipDestructionAutomation(String name,
                                     String levelObjectName, long delayToDestruction) {
        super(name);

        this.levelObjectName = levelObjectName;
        this.delayToDestruction = delayToDestruction;
    }

    @Override
    public void execute() {


        if (WorldManager.getSingleton().getLevelState() == WorldManagerBase.LevelState.STARTED) {
            if (!destructionStarted) {
                if (!startingTimeSet) {
                    Entity entity = WorldManager.getSingleton()
                            .getLevelObject(levelObjectName);
                    if (entity != null) {
                        startingTime = currentTimeMillis();
                        startingTimeSet = true;
                    }
                } else {
                    if (ENG_Utility.hasTimePassed(startingTime, delayToDestruction)) {
                        Entity entity = WorldManager.getSingleton()
                                .getLevelObject(levelObjectName);
                        if (entity != null) {
                            EntityProperties entityProperties =
                                    entity.getComponent(EntityProperties.class);
                            entityProperties.setDestroyed(true);
                            Animation destroyedAnimation =
                                    entityProperties.getDestroyedAnimation();
                            if (destroyedAnimation != null) {
                                WorldManager.getSingleton().startAnimation(entityProperties.getEntityId(), destroyedAnimation);
                            }
                            destructionStarted = true;
                            MainApp.getMainThread().runOnMainThread(
                                    () -> MainApp.getMainThread().removeAutomation(getName()));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void notifyParameterSet(String name) {


    }

}
