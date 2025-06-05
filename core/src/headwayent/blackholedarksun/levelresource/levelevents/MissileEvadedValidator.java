/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/19/21, 8:52 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource.levelevents;

import com.artemis.Entity;

import java.util.ArrayList;

import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.levelresource.LevelEvent;
import headwayent.blackholedarksun.levelresource.LevelEventValidator;
import headwayent.blackholedarksun.levelresource.LevelEventValidatorFactory;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.hotshotengine.ENG_Utility;

public class MissileEvadedValidator implements LevelEventValidator {

    public static class MissileEvadedValidatorFactory extends LevelEventValidatorFactory {

        public static final String TYPE = "MissileEvadedValidator";

        @Override
        public LevelEventValidator createLevelEventValidator(ArrayList<String> paramList) {
            return new MissileEvadedValidator(paramList);
        }

        @Override
        public int readAhead() {
            return 1;
        }
    }

    private static final long WAIT_DURATION = 20000;
    private int currentChasingProjectiles;
    private final int chasingProjectilesNum;
    private int chasingProjectilesEvadedNum;
    // TODO This is a hack because if the player attacks the enemy directly
    // the enemy might not have any time to actually attack the player
    // so the enemy might get destroyed before having a chance to launch any
    // missile.
    private long startTime;
    private boolean sticky;

    public MissileEvadedValidator(ArrayList<String> paramList) {
        chasingProjectilesNum = Integer.parseInt(paramList.get(0));
    }

    @Override
    public boolean validate(LevelEvent levelEvent) {
        if (sticky) {
            return true;
        }
        WorldManager worldManager = WorldManager.getSingleton();
        Entity playerShip = worldManager.getPlayerShip();
        if (playerShip != null) {
            // The reason why a following projectile has been destroyed is not
            // readily available in the current design. We will simply assume
            // that when a following projectile gets destroyed it's because of
            // the players ship's incredible avoidance skills :).
            ShipProperties shipProperties = worldManager.getShipPropertiesComponentMapper().getSafe(playerShip);
            if (shipProperties != null) {
                int chasingProjectiles = shipProperties.getChasingProjectilesNum();
                if (chasingProjectiles < currentChasingProjectiles) {
                    ++chasingProjectilesEvadedNum;
                }
                currentChasingProjectiles = shipProperties.getChasingProjectilesNum();
                sticky = chasingProjectilesEvadedNum >= chasingProjectilesNum;
            }
        }
        if (startTime == 0) {
            startTime = ENG_Utility.currentTimeMillis();
        }
        if (ENG_Utility.hasTimePassed(startTime, WAIT_DURATION)) {
            sticky = true; // TODO HACK!
        }
        return sticky;
    }
}
