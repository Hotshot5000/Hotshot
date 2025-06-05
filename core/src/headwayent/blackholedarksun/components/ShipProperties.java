/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/17/21, 10:02 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.components;

import headwayent.blackholedarksun.Animation;
import headwayent.blackholedarksun.MainActivity;
import headwayent.blackholedarksun.animations.AnimationFactory;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntityTCP;
import headwayent.blackholedarksun.statistics.InGameStatistics;
import headwayent.blackholedarksun.statistics.InGameStatisticsManager;
import headwayent.blackholedarksun.statistics.LevelEventStatistics;
import headwayent.blackholedarksun.statistics.LevelStatistics;
import headwayent.blackholedarksun.statistics.SessionStatistics;
import headwayent.blackholedarksun.world.WorldManagerBase.Sound;
import headwayent.blackholedarksun.world.WorldManagerServerSide;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import headwayent.hotshotengine.statedebugger.ENG_FrameInterval;

import static headwayent.hotshotengine.ENG_Utility.currentTimeMillis;

public class ShipProperties extends MultiplayerComponent {

//    private long entityId;
    private long userId;
    private String name = "";
    private transient ShipData shipData;
    private String shipDataModelName;
    private transient float scanRadius;
    private boolean exited;
    // Making sure that the client has been notified of these events before removing this entity for good.
    private transient boolean exitedSent;
    private transient boolean aiEnabled;

    // Afterburner settings
    private transient long afterburnerTime;
    private transient long currentAfterburnerTime;
    private boolean afterburnerActive;
    private final transient ENG_Vector4D lastSpeed = new ENG_Vector4D();
    private transient boolean lastSpeedSet;
    private transient long afterburnerCooldownTime;
    private transient long currentAfterburnerCooldownTime;
    private transient float afterburnerMaxSpeedCoeficient;
    private transient boolean afterburnerSoundEmitted;
    private transient int lastSpeedScrollPercentageBeforeAfterburner;
    private boolean countermeasureLaunched; // To make sure no duplicate
    private boolean countermeasureTrackingDefenseActive;
    // particle system
    private transient AnimationFactory countermeasuresAnimationFactory;
    private transient long currentSelectedEnemy = -1;
    private final transient HashSet<Long> chasingProjectilesList = new HashSet<>();
    private transient Animation enteredWorldAnimation;
    private transient Animation exitedWorldAnimation;
    private transient String currentScannedCargo;
    private transient long cargoScanStartTime;
    private transient boolean scanningCargo;
    private transient long countermeasuresLastLaunchTime;

    private int kills;
    private transient Sound engineSound;
    // Client created projectiles that we must not send back as newly created projectiles to this client.
    private final transient ArrayList<MultiplayerEntityTCP> clientAddedProjectiles = new ArrayList<>();

    // For sending to the server.
    private transient boolean countermeasureLaunchedSticky;
    private transient boolean afterburnerActiveSticky;
    private boolean showPortalEntering;
    private boolean showPortalExiting;
    private boolean playerLeft;
    private transient boolean playerLeftSent;

    /**
     * Default empty constructor for Kryo serialization.
     */
    public ShipProperties() {

    }

    /**
     * For DataSender only. A little coupling never hurt anyone :)
     * @param oth
     */
    public ShipProperties(long userId, ShipProperties oth) {
        exited = oth.isExited();
        afterburnerActive = oth.isAfterburnerActiveMP();
        countermeasureLaunched = oth.isCountermeasureLaunched();
        kills = oth.getKills();
        this.userId = oth.getUserId(); // Should we also copy the userId???
        name = oth.getName();
        shipDataModelName = oth.getShipDataModelName();
        for (Long chasingEntityId : oth._getChasingProjectileList()) {
            Long clientSpecificEntityId = WorldManagerServerSide.getSingleton().getClientId(userId, chasingEntityId);
            if (clientSpecificEntityId != null) {
                chasingEntityId = clientSpecificEntityId;
            }
            chasingProjectilesList.add(chasingEntityId);
        }

    }

    public void set(ShipProperties oth) {
        exited = oth.isExited();
        // Don't set them here. We need to know if this is the player ship or not.
//        afterburnerActive = oth.isAfterburnerActiveMP();
//        countermeasureLaunched = oth.isCountermeasureLaunched();
//        chasingProjectilesList.clear();
//        chasingProjectilesList.addAll(oth._getChasingProjectileList());
        kills = oth.getKills();
        showPortalEntering = oth.isShowPortalEntering();
        showPortalExiting = oth.isShowPortalExiting();
        playerLeft = oth.isPlayerLeft();
    }

//    public ShipProperties()

//    public ShipProperties(long entityId) {
//        this.entityId = entityId;
//    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getShipDataModelName() {
        return shipDataModelName;
    }

    public void setShipDataModelName(String shipDataModelName) {
        this.shipDataModelName = shipDataModelName;
    }

    public float getNextVelocityDelta(float currentVelocity, float finalSpeed,  int accelerationPercentage) {
        return getNextVelocityDelta(currentVelocity, finalSpeed, shipData.maxSpeed, accelerationPercentage, 1);
    }

    public float getPreviousVelocityDelta(float currentVelocity, float finalSpeed, int accelerationPercentage) {
        return getNextVelocityDelta(currentVelocity, finalSpeed, shipData.maxSpeed, accelerationPercentage, -1);
    }

    public float getNextVelocityDelta(float currentVelocity, float finalSpeed, int accelerationPercentage, int direction) {
        return getNextVelocityDelta(currentVelocity, finalSpeed, shipData.maxSpeed, accelerationPercentage, direction);
    }

    public static float getNextVelocityDelta(float currentVelocity, float finalSpeed, float maxSpeed, int accelerationPercentage) {
        return getNextVelocityDelta(currentVelocity, finalSpeed, maxSpeed, accelerationPercentage, 1);
    }

    public static float getPreviousVelocityDelta(float currentVelocity, float finalSpeed,  float maxSpeed, int accelerationPercentage) {
        return getNextVelocityDelta(currentVelocity, finalSpeed, maxSpeed, accelerationPercentage, -1);
    }

    public static float getNextVelocityDelta(float currentVelocity, float finalSpeed, float maxSpeed, int accelerationPercentage, int direction) {
        float currentPercentage = currentVelocity / maxSpeed * 100.0f;
        float finalPercentage = finalSpeed / maxSpeed * 100.0f;
        currentPercentage = ENG_Math.clamp(currentPercentage, 0, 100);
        finalPercentage = ENG_Math.clamp(finalPercentage, 0, 100);
        if (direction == 1) {
            if (finalPercentage - currentPercentage <= accelerationPercentage) {
                return getVelocity(finalPercentage, maxSpeed);
            }
        } else if (direction == -1) {
            if (currentPercentage - finalPercentage <= accelerationPercentage) {
                return getVelocity(finalPercentage, maxSpeed);
            }
        } else {
            if (MainActivity.isDebugmode()) {
                throw new IllegalArgumentException("direction is " + direction);
            }
        }
        currentPercentage += (accelerationPercentage * direction);
        currentPercentage = ENG_Math.clamp(currentPercentage, 0, 100);
        return getVelocity(currentPercentage, maxSpeed);
    }

    public float getVelocity(int percent) {
        return getVelocity(percent, shipData.maxSpeed);
    }

    public static float getVelocity(float percent, float maxSpeed) {
        if (percent < 0.0f || percent > 100.0f) {
            throw new IllegalArgumentException(percent
                    + " must be between 0 and 100");
        }
        return percent * maxSpeed / 100.0f;
    }

//    public long getEntityId() {
//        return entityId;
//    }

    /**
     * Only used for debugging and legacy. Don't use it. Use the name from EntityProperties.
     * @return
     */
    @Deprecated
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getScanRadius() {
        return scanRadius;
    }

    public void setScanRadius(float scanRadius) {
        this.scanRadius = scanRadius;
    }

    public ShipData getShipData() {
        return shipData;
    }

    public void setShipData(ShipData shipData) {
        this.shipData = shipData;
    }

    public long getAfterburnerTime() {
        return afterburnerTime;
    }

    public void setAfterburnerTime(long afterburnerTime) {
        this.afterburnerTime = afterburnerTime;
    }

    public boolean isAfterburnerActiveMP() {
        return afterburnerActive;
    }

    public boolean isAfterburnerActive() {
//        System.out.println("Checking hasTimePassed for ship_afterburner_time " + name);
        boolean timePassed = ENG_Utility.hasTimePassed(FrameInterval.SHIP_AFTERBURNER_TIME + name, currentAfterburnerTime, afterburnerTime);
        //        if (MainApp.getMainThread().isInputState()) {
//            FrameInterval currentFrameInterval = (FrameInterval) MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
////            timePassed = currentFrameInterval.getTimers().getIsAfterburnerActive(name);
//        } else {
//            timePassed = ENG_Utility.hasTimePassed(FrameInterval.SHIP_AFTERBURNER_TIME + name, currentAfterburnerTime, afterburnerTime);
//        }
//        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
//            FrameInterval currentFrameInterval = (FrameInterval) MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
////            currentFrameInterval.getTimers().setIsAfterburnerActive(name, timePassed);
//        }

//        System.out.println("ShipProperties isAfterburnerActive before check: " + afterburnerActive);
        if (afterburnerActive && timePassed) {
            afterburnerActive = false;
            currentAfterburnerCooldownTime = currentTimeMillis();
//            System.out.println("ShipProperties AfterburnerActive: false");
        }
        return afterburnerActive;
    }

    /** @noinspection deprecation */
    public void setAfterburnerActive(boolean afterburnerActive) {
//        System.out.println("ShipProperties attempting setAfterburnerActive: " + afterburnerActive);
        if (afterburnerActive && !this.afterburnerActive) {
            currentAfterburnerTime = currentTimeMillis();
        }
        boolean b;
        if (MainApp.getMainThread().isInputState()) {
            ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            b = (boolean) currentFrameInterval.getObject(FrameInterval.SET_AFTERBURNER_ACTIVE + getName());
        } else {
            b = currentTimeMillis() - currentAfterburnerCooldownTime < afterburnerCooldownTime;
        }
        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
            ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            currentFrameInterval.addObject(FrameInterval.SET_AFTERBURNER_ACTIVE + getName(), b);
        }
        if (afterburnerActive && b) {
            return;
        }
        this.afterburnerActive = afterburnerActive;
        this.afterburnerActiveSticky = afterburnerActive;

//        System.out.println("ShipProperties afterburnerActive: " + afterburnerActive);

        if (afterburnerActive) {
            InGameStatistics statistics = InGameStatisticsManager.getInstance().getInGameStatistics();
            SessionStatistics latestSessionStatistics = statistics.getLatestSessionStatistics();
            if (latestSessionStatistics != null) {
                LevelStatistics latestLevelStatistics = latestSessionStatistics.getLatestLevelStatistics();
                if (latestLevelStatistics != null) {
                    LevelEventStatistics latestLevelEventStatistics = latestLevelStatistics.getLatestLevelEventStatistics();
                    if (latestLevelEventStatistics != null) {
                        ++latestLevelEventStatistics.afterburnerStartNum;
                    }
                }
            }
        }

        makeDirtyTcp();
    }

    public boolean isAfterburnerSoundEmitted() {
        return afterburnerSoundEmitted;
    }

    public void setAfterburnerSoundEmitted(boolean b) {
        afterburnerSoundEmitted = b;
    }

    public ENG_Vector4D getLastSpeed() {
        return lastSpeed;
    }

    public void setLastSpeed(ENG_Vector4D lastSpeed) {
        this.lastSpeed.set(lastSpeed);
    }

    public boolean isLastSpeedSet() {
        return lastSpeedSet;
    }

    public void setLastSpeedSet(boolean lastSpeedSet) {
        this.lastSpeedSet = lastSpeedSet;
//        System.out.println("Setting lastSpeed: " + lastSpeedSet);
    }

    public float getAfterburnerMaxSpeedCoeficient() {
        return afterburnerMaxSpeedCoeficient;
    }

    public void setAfterburnerMaxSpeedCoeficient(
            float afterburnerMaxSpeedCoeficient) {
        this.afterburnerMaxSpeedCoeficient = afterburnerMaxSpeedCoeficient;
    }

    public long getAfterburnerCooldownTime() {
        return afterburnerCooldownTime;
    }

    public void setAfterburnerCooldownTime(long afterburnerCooldownTime) {
        this.afterburnerCooldownTime = afterburnerCooldownTime;
    }

    public int getLastSpeedScrollPercentageBeforeAfterburner() {
        return lastSpeedScrollPercentageBeforeAfterburner;
    }

    public void setLastSpeedScrollPercentageBeforeAfterburner(
            int lastSpeedScrollPercentageBeforeAfterburner) {
        this.lastSpeedScrollPercentageBeforeAfterburner = lastSpeedScrollPercentageBeforeAfterburner;
    }

    public boolean isExited() {
        return exited;
    }

    public void setExited(boolean exited) {
        this.exited = exited;
        makeDirtyTcp();
    }

    public boolean isAiEnabled() {
        return aiEnabled;
    }

    public void setAiEnabled(boolean aiEnabled) {
        this.aiEnabled = aiEnabled;
    }

    public int getKills() {
        return kills;
    }

    public void incrementKills() {
        ++kills;
        makeDirtyTcp();
    }

    public boolean isCountermeasureLaunched() {
        return countermeasureLaunched;
    }

    public void setCountermeasureLaunched(boolean countermeasureLaunched) {
        this.countermeasureLaunched = countermeasureLaunched;
        this.countermeasureLaunchedSticky = countermeasureLaunched;
        makeDirtyTcp();
    }

    public boolean isCountermeasureTrackingDefenseActive() {
        return countermeasureTrackingDefenseActive;
    }

    /**
     * Only for internal use by CountermeasureAnimationWithoutRendering to distinguish
     * between animation time and tracking defense time.
     * @param countermeasureTrackingDefenseActive
     */
    public void setCountermeasureTrackingDefenseActive(boolean countermeasureTrackingDefenseActive) {
        this.countermeasureTrackingDefenseActive = countermeasureTrackingDefenseActive;
    }

    public AnimationFactory getCountermeasuresAnimationFactory() {
        return countermeasuresAnimationFactory;
    }

    public void setCountermeasuresAnimationFactory(AnimationFactory countermeasuresAnimationFactory) {
        this.countermeasuresAnimationFactory = countermeasuresAnimationFactory;
    }

    /**
     * the selected enemy id is the unique item id.
     * @return
     */
    public long getCurrentSelectedEnemy() {
        return currentSelectedEnemy;
    }

    /**
     * the selected enemy id is the unique item id.
     * @param currentSelectedEnemy
     */
    public void setCurrentSelectedEnemy(long currentSelectedEnemy) {
        this.currentSelectedEnemy = currentSelectedEnemy;
    }

    /**
     * the selected enemy id is the unique item id.
     */
    public void resetCurrentSelectedEnemy() {
        currentSelectedEnemy = -1;
    }

    public void addChasingProjectile(Long id) {
//		System.out.println(name + " projectile added chasing " + id);
        if (!chasingProjectilesList.add(id)) {
            throw new IllegalArgumentException(id + " is already a chasing projectile");
        }
//        makeDirty();
    }

    public void removeChasingProjectile(Long id) {
//		System.out.println(name + " projectile removed chasing " + id);
        if (!chasingProjectilesList.remove(id)) {
            throw new IllegalArgumentException(id + " is not a valid chasing projectile");
        }
//        makeDirty();
    }

    public void removeAllChasingProjectiles() {
        chasingProjectilesList.clear();
        makeDirtyTcp();
    }

    public Iterator<Long> getChasingProjectilesIterator() {
        return chasingProjectilesList.iterator();
    }

    public HashSet<Long> _getChasingProjectileList() {
        return chasingProjectilesList;
    }

    public boolean isChased() {
        return !chasingProjectilesList.isEmpty();
    }

    public int getChasingProjectilesNum() {
        return chasingProjectilesList.size();
    }

    public Animation getEnteredWorldAnimation() {
        return enteredWorldAnimation;
    }

    public void setEnteredWorldAnimation(Animation enteredWorldAnimation) {
        this.enteredWorldAnimation = enteredWorldAnimation;
    }

    public Animation getExitedWorldAnimation() {
        return exitedWorldAnimation;
    }

    public void setExitedWorldAnimation(Animation exitedWorldAnimation) {
        this.exitedWorldAnimation = exitedWorldAnimation;
    }

    public String getCurrentScannedCargo() {
        return currentScannedCargo;
    }

    public void setCurrentScannedCargo(String currentScannedCargo) {
        this.currentScannedCargo = currentScannedCargo;
    }

    public long getCargoScanStartTime() {
        return cargoScanStartTime;
    }

    public void setCargoScanStartTime() {
        this.cargoScanStartTime = currentTimeMillis();
    }

    public boolean isScanningCargo() {
        return scanningCargo;
    }

    public void setScanningCargo(boolean scanningCargo) {
        this.scanningCargo = scanningCargo;
    }

    public long getCountermeasuresLastLaunchTime() {
        return countermeasuresLastLaunchTime;
    }

    public void setCountermeasuresLastLaunchTime() {
        this.countermeasuresLastLaunchTime = currentTimeMillis();
    }

    public Sound getEngineSound() {
        return engineSound;
    }

    public void setEngineSound(Sound engineSound) {
        this.engineSound = engineSound;
    }

    public ArrayList<MultiplayerEntityTCP> getClientAddedProjectiles() {
        return clientAddedProjectiles;
    }

    public void addToClientAddedProjectilesList(ArrayList<MultiplayerEntityTCP> list) {
        clientAddedProjectiles.addAll(list);
    }

    public void clearClientAddedProjectilesList() {
        clientAddedProjectiles.clear();
    }

    public boolean isCountermeasureLaunchedSticky() {
        return countermeasureLaunchedSticky;
    }

    public void setCountermeasureLaunchedSticky(boolean countermeasureLaunchedSticky) {
        this.countermeasureLaunchedSticky = countermeasureLaunchedSticky;
    }

    public boolean isAfterburnerActiveSticky() {
        return afterburnerActiveSticky;
    }

    public void setAfterburnerActiveSticky(boolean afterburnerActiveSticky) {
        this.afterburnerActiveSticky = afterburnerActiveSticky;
    }

    public boolean isExitedSent() {
        return exitedSent;
    }

    public void setExitedSent(boolean exitedSent) {
        this.exitedSent = exitedSent;
    }

    public boolean isShowPortalEntering() {
        return showPortalEntering;
    }

    public void setShowPortalEntering(boolean showPortalEntering) {
        this.showPortalEntering = showPortalEntering;
        makeDirtyTcp();
    }

    public boolean isShowPortalExiting() {
        return showPortalExiting;
    }

    public void setShowPortalExiting(boolean showPortalExiting) {
        this.showPortalExiting = showPortalExiting;
        makeDirtyTcp();
    }

    public boolean isPlayerLeft() {
        return playerLeft;
    }

    public void setPlayerLeft(boolean playerLeft) {
        this.playerLeft = playerLeft;
        makeDirtyTcp();
    }

    public boolean isPlayerLeftSent() {
        return playerLeftSent;
    }

    public void setPlayerLeftSent(boolean playerLeftSent) {
        this.playerLeftSent = playerLeftSent;
    }
}
