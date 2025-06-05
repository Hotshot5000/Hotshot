/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/6/21, 9:59 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems;

import headwayent.blackholedarksun.HudManager;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;

public class HudSystem extends EntityProcessingSystem {

    private ComponentMapper<EntityProperties> entityPropertiesMapper;
    private ComponentMapper<ShipProperties> shipPropertiesMapper;
    private long updateEntityListForCrossGuideTime;
    private long processTime;
    private long beginTime;
    private long shipProcessTime;
    private long updateRadarData;

    public HudSystem() {
        super(Aspect.all(EntityProperties.class));

    }

    @Override
    protected void begin() {
        super.begin();
//        System.out.println("HUDSYSTEM begin()");
        HudManager hudManager = HudManager.getSingleton();
        hudManager.resetCurrentFrameRadarVisibilityIds();

//        long resetBeginTime = System.currentTimeMillis();
        hudManager.resetCurrentEnemyCrossDataVisibilityIds();
//        System.out.println("resetBeginTime: " + (System.currentTimeMillis() - resetBeginTime));
//        updateRadarData = 0;
//        updateEntityListForCrossGuideTime = 0;
//        processTime = 0;
//        shipProcessTime = 0;
//        beginTime = ENG_Utility.currentTimeMillis();
        hudManager.resetCurrentEnemyList();
    }

    @Override
    protected void end() {
        super.end();
//        System.out.println("HUDSYSTEM end()");
        HudManager hudManager = HudManager.getSingleton();
//        long sendRadarVisibilityDataToNativeBeginTime = ENG_Utility.currentTimeMillis();
        hudManager.sendRadarVisibilityDataToNative();
//        System.out.println("sendRadarVisibilityDataToNative time: " + (ENG_Utility.currentTimeMillis() - sendRadarVisibilityDataToNativeBeginTime));
//        long updateEntityListForCrossGuideBeginTime = System.nanoTime();
        hudManager.updateEntityListForCrossGuide();
//        System.out.println("updateEntityListForCrossGuide time: " + (System.nanoTime() - updateEntityListForCrossGuideBeginTime));
//        updateEntityListForCrossGuideTime += System.nanoTime() - updateEntityListForCrossGuideBeginTime;
//        System.out.println("updateRadarData time: " + (updateRadarData / 1000000.0f));
//        System.out.println("updateEntityListForCrossGuideTime in milis: " + (updateEntityListForCrossGuideTime / 1000000.0f));
//        System.out.println("processTime: " + (processTime / 1000000.0f));
//        System.out.println("shipProcessTime: " + (shipProcessTime / 1000000.0f));
//        System.out.println("HudSystem time: " + (ENG_Utility.currentTimeMillis() - beginTime));
        hudManager.checkCurrentEnemySelectedStillAvailable();
        hudManager.orderEnemyShipsByDistanceFromPlayerShip();

        hudManager.updateThirdPersonCameraPosition();
    }

    @Override
    protected void process(Entity e) {


//        long processTimeBegin = System.nanoTime();
        if (WorldManager.getSingleton().getLevelState() != WorldManagerBase.LevelState.STARTED) {
            return;
        }

        ShipProperties shipProperties = shipPropertiesMapper.getSafe(e);
        if (shipProperties != null) {
            long shipProcessBeginTime = System.nanoTime();
            EntityProperties entityProperties = entityPropertiesMapper.get(e);
            ENG_SceneNode node = entityProperties.getNode();
//            long updateRadarDataBeginTime = System.nanoTime();
            updateRadarData(e, node, entityProperties, shipProperties);
//            updateRadarData += System.nanoTime() - updateRadarDataBeginTime;

            Entity playerShip = WorldManager.getSingleton().getPlayerShip();
            if (playerShip == null) {
                return;
            }
            if (playerShip != e) {
//                long updateEnemyCrossGuideBeginTime = System.nanoTime();
                HudManager.getSingleton().updateEnemyCrossGuide(e);
//                System.out.println("updateEnemyCrossGuide time: " + (System.currentTimeMillis() - updateEnemyCrossGuideBeginTime));
//                updateEntityListForCrossGuideTime += System.nanoTime() - updateEnemyCrossGuideBeginTime;
                HudManager.getSingleton().updateCurrentEnemyList(e);
            }

            if (playerShip == e) {
//                long updateEnemySelectionBeginTime = ENG_Utility.currentTimeMillis();
                HudManager.getSingleton().updateEnemySelection(node, shipProperties);
//                System.out.println("updateEnemySelection time: " + (ENG_Utility.currentTimeMillis() - updateEnemySelectionBeginTime));
            }
            shipProcessTime += System.nanoTime() - shipProcessBeginTime;
        }
//        processTime += System.nanoTime() - processTimeBegin;
    }

    private void updateRadarData(Entity e, ENG_SceneNode node, EntityProperties entityProperties, ShipProperties shipProperties) {
        // For safety make sure it's not our ship
        if (WorldManager.getSingleton().getPlayerShipEntityId() != entityProperties.getEntityId()) {
            HudManager.getSingleton().updateRadarData(e, node, entityProperties, shipProperties);
        }
    }

}
