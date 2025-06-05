/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.automationframework;

import com.artemis.Entity;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.animations.AnimationFactory;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.hotshotengine.ENG_Utility;

import static headwayent.hotshotengine.ENG_Utility.currentTimeMillis;

public class CountermeasureLaunchAutomation extends AutomationFramework {

    private final String shipName;
    private final long delay;
    private long currentTime;

    public CountermeasureLaunchAutomation(String name, String shipNameInLevel, long delayBetweenLaunches) {
        super(name);

        this.shipName = shipNameInLevel;
        this.delay = delayBetweenLaunches;
    }

    @Override
    public void execute() {
        

        if (WorldManager.getSingleton().getLevelState() == WorldManagerBase.LevelState.STARTED) {
            if (ENG_Utility.hasTimePassed(currentTime, delay)) {
                Entity entity = WorldManager.getSingleton().getLevelObject(shipName);
                if (entity != null && !entity.getComponent(EntityProperties.class).isDestroyed()) {
                    ShipProperties shipProperties = entity.getComponent(ShipProperties.class);
                    AnimationFactory factory = shipProperties.getCountermeasuresAnimationFactory();
                    if (factory != null) {
                        if (!shipProperties.isCountermeasureLaunched()) {
                            WorldManager.getSingleton().startAnimation(entity.getComponent(EntityProperties.class).getEntityId(), factory.createInstance(entity));
                        }
                    } else {
                        throw new NullPointerException(
                                "CountermeasureAnimationFactory should " +
                                        "never be null");
                    }
                    currentTime = currentTimeMillis();
                } else {
                    MainApp.getMainThread().runOnMainThread(
                            () -> MainApp.getMainThread().removeAutomation(getName()));
                }
            }
        }
    }

    @Override
    public void notifyParameterSet(String name) {
        

    }

}
