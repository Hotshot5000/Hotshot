/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/21, 1:55 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import com.artemis.Entity;
import headwayent.blackholedarksun.Animation;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.hotshotengine.ENG_Utility;

/**
 * Created by sebas on 19.11.2015.
 */
public class CountermeasuresWithoutRenderingAnimation extends Animation {
    protected static final long TOTAL_ANIM_TIME = ShipData.COUNTERMEASURE_TIME;
    protected static final long COUNTER_MEASURE_ACTIVE_TIME = 3500;
    protected final EntityProperties entityProperties;
    protected final ShipProperties shipProperties;
    protected long currentCounterMeasureTime;

    public CountermeasuresWithoutRenderingAnimation(String name, Entity shipEntity, long totalTime) {
        super(name, totalTime);
        WorldManagerBase worldManager = WorldManagerBase.getSingleton();
        entityProperties = worldManager.getEntityPropertiesComponentMapper().get(shipEntity);
        shipProperties = worldManager.getShipPropertiesComponentMapper().getSafe(shipEntity);
        if (shipProperties == null) {
            throw new IllegalArgumentException(worldManager.getEntityPropertiesComponentMapper().get(shipEntity).getName() + " is not a valid ship entity");
        }
    }

    @Override
    public void start() {
        super.start();
        shipProperties.setCountermeasureLaunched(true);
        // The time for which the countermeasure has its effect on the
        // tracking projectiles is limited to less than animation time.
        // If we just setCountermeasureLaunched(false) then we get duplicate
        // nodes when creating multiple countermeasures within the animation time limit.
        shipProperties.setCountermeasureTrackingDefenseActive(true);
        currentCounterMeasureTime = ENG_Utility.currentTimeMillis();
    }

    @Override
    public void update() {
        if (ENG_Utility.hasTimePassed(
                FrameInterval.COUNTER_MEASURE_EXPIRATION_TIME +
                        entityProperties.getNode().getName(),
                currentCounterMeasureTime, COUNTER_MEASURE_ACTIVE_TIME)) {
            shipProperties.setCountermeasureTrackingDefenseActive(false);
        }
    }

    @Override
    public void animationFinished() {
        destroyResources();
    }

    @Override
    public void reloadResources() {

    }

    @Override
    public void destroyResourcesImpl() {
        shipProperties.setCountermeasureLaunched(false);
    }
}
