/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/18/21, 7:57 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems;

import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.blackholedarksun.entitydata.ShipData.ShipTeam;
import headwayent.blackholedarksun.entitydata.WeaponData;
import headwayent.blackholedarksun.entitydata.WeaponData.WeaponType;
import headwayent.blackholedarksun.entitydata.WeaponData.WeaponComparator;
import headwayent.blackholedarksun.components.AIProperties;
import headwayent.blackholedarksun.components.AIProperties.AIState;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.components.WeaponProperties;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.blackholedarksun.physics.EntityContactListener;
import headwayent.blackholedarksun.physics.EntityRigidBody;
import headwayent.blackholedarksun.physics.PhysicsUtility;
import headwayent.blackholedarksun.systems.helper.ai.Utilities;
import headwayent.blackholedarksun.systems.helper.ai.Waypoint;
import headwayent.blackholedarksun.systems.helper.ai.WaypointSector;
import headwayent.blackholedarksun.systems.helper.ai.WaypointSystem;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.hotshotengine.*;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.basictypes.ENG_Long;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ClosestNotMeRayResultCallback;

import headwayent.blackholedarksun.*;
import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;

public class AISystem extends IntervalEntityProcessingSystem {
    private static final float ESCAPING_LIMITS_ACCELERATION_RATE = 0.1f;
    private static final float EVADING_COLLISION_VELOCITY_CHANGE_RATE = 0.2f;
    private static final int MAX_CHASING_PROJECTILES_NUM = 5;
    private static final int MAX_PROJECTILES_LAUNCHED_DELAY = 2000;

    private static final float MIN_DISTANCE_SLOW_DOWN_RATE = 0.2f;
    private static final float ENEMY_MIN_DISTANCE = 100000.0f;
    private static final float REACHING_DESTINATION_VELOCITY_CHANGE_STEP = 0.1f;
    private static final int VELOCITY_CHANGE_NAME_MIN_DISTANCE_WAYPOINT = 18;
    private static final int VELOCITY_CHANGE_NAME_ACCELERATE_TO_MIN_SPEED_FOLLOWING_WAYPOINT = 17;
    private static final int VELOCITY_CHANGE_NAME_ACCELERATE_TO_MAX_SPEED_FOLLOWING_WAYPOINT = 16;
    private static final int VELOCITY_CHANGE_NAME_TOWARD_ENEMY_SHIP_DIFFERENT_SECTOR = 15;
    private static final int VELOCITY_CHANGE_NAME_TOWARD_ENEMY_SHIP_SAME_SECTOR = 14;
    private static final int VELOCITY_CHANGE_NAME_ESCAPING_LIMITS_FULL_STOP = 13;
    private static final int VELOCITY_CHANGE_NAME_EVADE_MISSILE = 12;
    private static final int VELOCITY_CHANGE_NAME_EVADING_COLLISION = 11;
    private static final int VELOCITY_CHANGE_NAME_DESTINATION_REACHED = 10;
    private static final int VELOCITY_CHANGE_NAME_REACHING_DESTINATION = 9;
    private static final int VELOCITY_CHANGE_NAME_ESCAPING_LIMITS = 8;
    private static final int VELOCITY_CHANGE_NAME_MIN_DISTANCE = 7;
    private static final int VELOCITY_CHANGE_NAME_FOLLOWED_SHIP_VELOCITY = 6;
    private static final int VELOCITY_CHANGE_NAME_EVADE_HIT = 5;
    private static final int VELOCITY_CHANGE_NAME_COLLISION_RESPONSE = 4;
    private static final int VELOCITY_CHANGE_NAME_ACCELERATE_TO_MAX_SPEED = 3;
    private static final int VELOCITY_CHANGE_NAME_PATROL_SLOW_DOWN = 2;
    private static final int VELOCITY_CHANGE_NAME_RELOADER_TOWARDS_SHIP = 1;
    private static final int VELOCITY_CHANGE_NAME_RELOADER_STOP = 0;
    private static final float INSIDE_SECTOR_SPEED_CHANGE_RATE = 0.5f;
    private static final float OUTSIDE_SECTOR_SPEED_CHANGE_RATE = 0.5f;
    private static final float RELOADER_AWAY_ANGLE = 160.0f * ENG_Math.DEGREES_TO_RADIANS;
    private static final int RELOADER_TIME_BETWEEN_RELOADING_UNITS = 1000;
    private static final float RELOADER_SPEED_CHANGE_RATE = 0.5f;
    private static final float FOLLOWING_SHIP_ACCELERATION_RATE = ESCAPING_LIMITS_ACCELERATION_RATE;
    private static final int PATROLING_ROTATION_TIME = 3000;
    private static final float PATROL_SPEED_COEFFICIENT = 0.25f;
    private static final int NO_ENEMY_DIRECTION_CHANGE_CHANCE = 10;
    private static final float COLLISION_RESPONSE_ACCELERATION_STEP = 0.3f;
    private static final int COLLISION_RESPONSE_ACCELERATION_TIME = 4000;
    private static final float COLLISION_RESPONSE_ACCELERATION_ANGLE = 45.0f * ENG_Math.DEGREES_TO_RADIANS;
    private static final float EVASION_HIT_SPEED_CHANGE_STEP = 0.3f;
    private static final float EVASION_MISSILE_SPEED_CHANGE_STEP = 0.3f;
    private static final float NO_TARGET_SPEED_CHANGE_STEP = ESCAPING_LIMITS_ACCELERATION_RATE;
    private static final int SPECIAL_WEAPON_CHANCE = 10;
    private static final int CHECK_TARGETED_CHANCE = 200;
    private static final int FOLLOW_PLAYER_TO_SEEK_PLAYER_RAND = 20;
    private static final boolean DEBUG = false;
    private static final int ENEMY_SEEK_RAND = 1; // 100;
    private static final float MIN_FOLLOW_DIST = 40000000.0f;
    private static final float TARGET_MIN_DISTANCE = 4000000.0f;
    private static final float SPEED_CHANGE_RATE = 1.0f / 10.0f;
    private static final float COLLISION_EVASION_ANGLE = 135.0f * ENG_Math.DEGREES_TO_RADIANS;
    private static final float COLLISION_EVASION_FRONT_ANGLE = 180.0f * ENG_Math.DEGREES_TO_RADIANS - COLLISION_EVASION_ANGLE;
    private static final float EVASION_ACCELERATION = 1.0f;
    private static final long EVADE_COLLISION_TIME = 3000;
    private static final float TARGETING_ANGLE = 1.0f * ENG_Math.DEGREES_TO_RADIANS;
    private static final float ESCAPE_LEVEL_LIMITS_ANGLE = 20.0f * ENG_Math.DEGREES_TO_RADIANS;
    private static final float MIN_COUNTERMEASURES_DISTANCE = ENG_Math.sqr(800.0f);
    private static final float MIN_EVASION_DISTANCE = ENG_Math.sqr(500.0f);
    private static final long EVASION_HIT_TIME = 5000;
    private static final int CHASING_SHIP_MAX_NUM = 4;
    private static final float WAYPOINT_SLOW_MOVE_DISTANCE = 300.0f;
    private static final float WAYPOINT_MIN_SPEED = 30.0f;
    private static final float WAYPOINT_RECHECK_DISTANCE = 200.0f;
    public static final float WAYPOINT_DISTANCE_FROM_WHERE_ALLOWED_TO_SHOOT = 150;// 150 just for testing. 600.0f;
    public static final float WAYPOINT_TARGETING_ANGLE = 15.0f * ENG_Math.DEGREES_TO_RADIANS;
    public static final float WAYPOINT_EVASION_ANGLE_DIFF = 35.0f;
    public static final float WAYPOINT_EVASION_MIN_ANGLE =
            (ENG_Math.HALF_PI_DEG.valueDegrees() - WAYPOINT_EVASION_ANGLE_DIFF) * ENG_Math.DEGREES_TO_RADIANS;
    public static final float WAYPOINT_EVASION_MAX_ANGLE =
            (ENG_Math.HALF_PI_DEG.valueDegrees() + WAYPOINT_EVASION_ANGLE_DIFF) * ENG_Math.DEGREES_TO_RADIANS;
    public static final int EVASION_WAYPOINT_CHAIN_MAX_SIZE = 4;
    public static final float EVASION_WAYPOINT_CHAIN_MAX_ANGLE = 40 * ENG_Math.DEGREES_TO_RADIANS;
    public static final int EVASION_WAYPOINT_CHAIN_MAX_COUNT = 2;
    public static final float WAYPOINT_MAX_SPEED = 150.0f;
    public static final float ZERO_SPEED = 0.3f; // There is a bug with rotation around ship axis when ship is completely stationary.
    public static final float FOLLOW_SHIP_RAY_TEST_DISTANCE_AHEAD = 500.0f;
    private ComponentMapper<EntityProperties> entityPropertiesMapper;
    private ComponentMapper<ShipProperties> shipPropertiesMapper;
    private ComponentMapper<AIProperties> aIPropertiesMapper;
    private ComponentMapper<WeaponProperties> weaponPropertiesMapper;
    private final ENG_Vector4D currentPos = new ENG_Vector4D(true);
    private final ENG_Vector4D otherPos = new ENG_Vector4D(true);
    private final ENG_Vector4D otherShipVelocity = new ENG_Vector4D();
    private final ENG_Vector4D distVec = new ENG_Vector4D(true);
    private final ENG_Vector4D currentFrontVec = new ENG_Vector4D();
    private final ENG_Vector4D otherFrontVec = new ENG_Vector4D();
    private final ENG_Quaternion rotation = new ENG_Quaternion();
    private final WeaponComparator weaponComparator = new WeaponComparator();
    private final ArrayList<WeaponType> currentWeaponsList = new ArrayList<>();
    private final ENG_Vector4D perpendicularVec = new ENG_Vector4D();
    private boolean projectileCreated;
    private final ENG_Vector4D currentUpVec = new ENG_Vector4D();
    private boolean horzRot;
    private final ENG_Vector4D crossPosition = new ENG_Vector4D(true);
    private final ENG_Vector4D transformedCrossPosition = new ENG_Vector4D(true);
    private final ENG_Vector4D collisionResponseAxis = new ENG_Vector4D();
    private final ENG_Vector4D patrolingRotationAxis = new ENG_Vector4D();
    private final ENG_ClosestObjectData data = new ENG_ClosestObjectData();
    private final ENG_Vector4D levelLimits = new ENG_Vector4D();
    private final ENG_Vector4D currentLevelLimits = new ENG_Vector4D();
    private final ENG_Vector4D awayFromLimitsPos = new ENG_Vector4D();
    private final ENG_Vector4D destination = new ENG_Vector4D(true);
    private final ENG_Vector4D rayTo = new ENG_Vector4D(true);
    private final float updateInterval;
    private boolean rotationAwayFromLimitsCompleted = true;
//    private boolean awayFromLimitsPosSet;
//    private float beginTime;

    private static class WaypointSectorIdWithPosition {
        public ENG_Integer waypointSectorId;
        public ENG_Vector4D position;

        public WaypointSectorIdWithPosition(ENG_Integer waypointSectorId, ENG_Vector4D position) {
            this.waypointSectorId = waypointSectorId;
            this.position = position;
        }
    }

    private static class WaypointIdsWithDistances implements Comparable<WaypointIdsWithDistances> {
        public ENG_Integer firstWaypointId;
        public ENG_Integer secondWaypointId;
        public float distanceBetween;

        public WaypointIdsWithDistances(ENG_Integer firstWaypointId, ENG_Integer secondWaypointId, float distanceBetween) {
            this.firstWaypointId = firstWaypointId;
            this.secondWaypointId = secondWaypointId;
            this.distanceBetween = distanceBetween;
        }

        public boolean equalsFast(WaypointIdsWithDistances o) {
            return (firstWaypointId.getValue() == o.firstWaypointId.getValue() && secondWaypointId.getValue() == o.secondWaypointId.getValue())
                    || (firstWaypointId.getValue() == o.secondWaypointId.getValue() && secondWaypointId.getValue() == o.firstWaypointId.getValue());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof WaypointIdsWithDistances)) return false;
            WaypointIdsWithDistances that = (WaypointIdsWithDistances) o;
            return (Objects.equals(firstWaypointId, that.firstWaypointId) && Objects.equals(secondWaypointId, that.secondWaypointId))
                    || (Objects.equals(firstWaypointId, that.secondWaypointId) && Objects.equals(secondWaypointId, that.firstWaypointId));
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstWaypointId, secondWaypointId);
        }

        @Override
        public int compareTo(WaypointIdsWithDistances o) {
            return (this.distanceBetween < o.distanceBetween) ? -1 : 1;
        }

        @Override
        public String toString() {
            return "WaypointIdsWithDistances{" +
                    "firstWaypointId=" + firstWaypointId +
                    ", secondWaypointId=" + secondWaypointId +
                    ", distanceBetween=" + distanceBetween +
                    '}';
        }
    }

    private static class WaypointGroup {
        public int groupId;
        public ArrayList<ENG_Integer> waypointIds = new ArrayList<>();

        public WaypointGroup(int groupId) {
            this.groupId = groupId;
        }

        /**
         * If we have a group with only one waypoint.
         * Or for when we just use the waypoint ids to get the group separation.
         * @param waypointId
         */
        public void addWaypointId(ENG_Integer waypointId) {
            if (!waypointIds.isEmpty()) {
                throw new IllegalStateException("waypointIds must be empty. Current size: " + waypointIds.size());
            }
            waypointIds.add(waypointId);
        }

        public boolean addWaypointIds(WaypointIdsWithDistances waypointIdsWithDistances) {
            if (waypointIds.isEmpty()) {
                waypointIds.add(waypointIdsWithDistances.firstWaypointId);
                waypointIds.add(waypointIdsWithDistances.secondWaypointId);
            } else {
                // Check if same base.
                if (waypointIds.get(0).getValue() == waypointIdsWithDistances.firstWaypointId.getValue()) {
                    waypointIds.add(waypointIdsWithDistances.secondWaypointId);
                    return true;
                }
                // Check if transitive.
                if (waypointIds.get(waypointIds.size() - 1).getValue() != waypointIdsWithDistances.firstWaypointId.getValue()) {
                    return false;
                } else {
                    waypointIds.add(waypointIdsWithDistances.secondWaypointId);
                }
            }
            return true;
        }

        public boolean contains(WaypointIdsWithDistances waypointIdsWithDistances) {
            return waypointIds.contains(waypointIdsWithDistances.firstWaypointId) &&
                    waypointIds.contains(waypointIdsWithDistances.secondWaypointId);
        }

        @Override
        public String toString() {
            return "WaypointGroup{" +
                    "groupId=" + groupId +
                    ", waypointIds=" + waypointIds +
                    '}';
        }
    }

    public AISystem(double interval) {
        super(Aspect.all(EntityProperties.class, ShipProperties.class), (float) interval);
        
        updateInterval = (float) interval;
        Utilities.setUpdateInterval(updateInterval);
    }

    @Override
    protected void begin() {
        super.begin();
//        beginTime = ENG_Utility.currentTimeMillis();
    }

    @Override
    protected void end() {
        WaypointSystem.getSingleton().update();
//        System.out.println("AISystem time: " + (ENG_Utility.currentTimeMillis() - beginTime));
        super.end();
    }

    @Override
    protected void process(Entity e) {
        AIProperties aiProperties = aIPropertiesMapper.getSafe(e);
        if (aiProperties != null/* && !aiProperties.isIgnoreAI()*/) {

            EntityProperties entityProperties = entityPropertiesMapper.get(e);
//            System.out.println("Ship " + entityProperties.getName() + " starting AI state: " + aiProperties.getState());
            ShipProperties shipProperties = shipPropertiesMapper.get(e);
            if (!shipProperties.isAiEnabled()) {
                return;
            }
            WeaponProperties weaponProperties = weaponPropertiesMapper.get(e);

            aiProperties.setEntityName(entityProperties.getNode().getName());
//			System.out.println("AI SHIP NAME: " + shipProperties.getName());

            switch (aiProperties.getState()) {
                case REACH_DESTINATION: {
                    reachDestination(aiProperties, entityProperties, shipProperties);
                }
                break;
                case EVADE_LEVEL_LIMITS: {
                    evadeLevelLimits(aiProperties, entityProperties, shipProperties);
                }
                break;
                case FOLLOW_PLAYER_SHIP: {
                    followPlayerShip(aiProperties, entityProperties, shipProperties);
                }
                break;
                case SEEK_CLOSEST_PLAYER: {
                    if (checkShouldReachDestination(aiProperties, shipProperties)) break;
                    if (checkIsChased(shipProperties, aiProperties)) break;
                    seekClosestPlayer(aiProperties, entityProperties,
                            shipProperties);
                }
                break;
                case FOLLOW_PLAYER: {
                    Entity followedShip = MainApp.getGame().getWorldManager().getEntityByItemId(aiProperties.getFollowedShip());
                    if (followedShip == null
                            || (aiProperties.getWaypointState() == AIProperties.AIWaypointState.NONE &&
                            ENG_Utility.hasRandomChanceHit(FrameInterval.FOLLOW_PLAYER_TO_SEEK_PLAYER_RAND + entityProperties.getNode().getName(),
                                    FOLLOW_PLAYER_TO_SEEK_PLAYER_RAND))) {
                        aiProperties.resetFollowedShip();
                        aiProperties.setState(AIState.SEEK_CLOSEST_PLAYER);
                        showAIStateChange(shipProperties, aiProperties);
                    } else {
                        rayTestCollisionAhead(entityProperties, aiProperties);
                        if (checkShouldEvadeHit(aiProperties, entityProperties, shipProperties)) break;
                        if (checkCollided(aiProperties, shipProperties)) break;
                        if (checkLimitsReached(entityProperties, aiProperties, shipProperties)) break;

                        EntityProperties otherShipEntityProperties = entityPropertiesMapper.get(followedShip);
                        AIProperties otherAiProperties = aIPropertiesMapper.getSafe(followedShip);

                        entityProperties.getNode().getPosition(currentPos);
                        otherShipEntityProperties.getNode().getPosition(otherPos);
                        otherPos.sub(currentPos, distVec);
                        float squaredDist = distVec.squaredLength();

                        checkMinDistanceFollowedEntity(aiProperties, squaredDist, weaponProperties, shipProperties);

                        WaypointSystem waypointSystem = WaypointSystem.getSingleton();
                        updateWaypointRoute(e, aiProperties, otherAiProperties, entityProperties,
                                otherShipEntityProperties, shipProperties);

                        // MIGHT NEED TO THINK THIS AGAIN!!!
                        if (!isFollowingWaypoint(aiProperties) || aiProperties.isWaypointShootPlayer()) {
                            matchFollowedEntitySpeed(squaredDist, otherShipEntityProperties, entityProperties,
                                    shipProperties, aiProperties);
                        } else {
                            // Orientation is set at the end based on otherPos.
                            accelerateTowardsWaypoint(entityProperties, shipProperties, aiProperties);
                        }

                        // Make sure we're not on collision course
                        boolean evadeCollision = false;
                        if ((!isFollowingWaypoint(aiProperties) || aiProperties.isWaypointShootPlayer())) {
                            checkOnCollisionCourse(squaredDist, otherShipEntityProperties, aiProperties, entityProperties);
                        }

                        if ((!isFollowingWaypoint(aiProperties) || aiProperties.isWaypointShootPlayer())) {
                            if (evadeCollision(aiProperties, entityProperties, shipProperties))
                                break;
                        }

                        // Find if some enemy is targeting us
                        if (ENG_Utility.hasRandomChanceHit(FrameInterval.CHECK_TARGETED_CHANCE + entityProperties.getNode().getName(), CHECK_TARGETED_CHANCE)) {
                            boolean targeted = checkTargeted(aiProperties, shipProperties);
                            if (targeted) {
                                break;
                            }
                        }

                        // Check if we are not followed by a projectile
                        if (!isFollowingWaypoint(aiProperties)) {
                            if (checkIsChased(shipProperties, aiProperties)) break;
                        } else {
                            // Escape by following waypoints to closest exit.
                            aiProperties.setFollowingWaypointWhileChased(shipProperties.isChased());
                            // If we are outside a waypoint sector just evade as usual.
                            if (aiProperties.isFollowingWaypointWhileChased() &&
                                    aiProperties.getCurrentWaypointId() == 0 && aiProperties.getCurrentTargetWaypointId() == 0) {
                                if (checkIsChased(shipProperties, aiProperties)) break;
                            }
                        }

                        // Rotate toward the followed ship to take it in the
                        // crosshair
                        // entityProperties.getNode().getLocalXAxis(currentFrontVec);
                        // entityProperties.getNode().getLocalYAxis(currentFrontVec);

                        // If AI is shooting player it should update based on player position not on target waypoint.
                        if (isFollowingWaypoint(aiProperties) && !aiProperties.isWaypointShootPlayer()) {
                            setOtherPosFromWaypoint(aiProperties);
                        } else {
                            otherShipEntityProperties.getNode().getPosition(otherPos);
                        }
                        entityProperties.getNode().getLocalInverseZAxis(currentFrontVec);
                        entityProperties.getNode().getLocalYAxis(currentUpVec);

                        ENG_Quaternion currentOrientation = new ENG_Quaternion();
                        entityProperties.getNode().getOrientation(currentOrientation);
                        ENG_Vector4D orientedUpVec = currentOrientation.mul(currentUpVec);
                        orientedUpVec.mul(0.3f);
                        ENG_Vector4D underShipPos = currentPos.subAsVec(orientedUpVec);


                        // System.out.println("playerShip pos: " + otherPos);
                        otherPos.sub(underShipPos, distVec);
                        distVec.normalize();
                        float angleBetween = currentFrontVec.angleBetween(distVec);
                        // System.out.println("angleBetween: " + angleBetween *
                        // ENG_Math.RADIANS_TO_DEGREES);
                        // System.out.println("ai system front vec: " +
                        // currentFrontVec);
                        if ((!isFollowingWaypoint(aiProperties) || aiProperties.isWaypointShootPlayer()) &&
                                angleBetween < TARGETING_ANGLE) {
                            entityProperties.getRigidBody().setAngularVelocity(new Vector3());
                            aiProperties.setState(AIState.SHOOT_PLAYER);
                            showAIStateChange(shipProperties, aiProperties);
                        } else {
                            Utility.rotateToPosition(currentFrontVec, distVec, updateInterval, entityProperties,
                                    shipProperties.getShipData().maxAngularVelocity);
                        }
                    }
                }
                break;
                case SHOOT_PLAYER: {
                    // Find the most powerful weapon we have in order to shoot
                    Entity followedShip = checkIfFollowedShipIsDestroyed(aiProperties);
                    if (followedShip == null) break;

                    ShipProperties followedShipShipProperties = checkIfFollowedEntityIsAShip(followedShip);
                    if (checkIfTooManyChasingProjectiles(followedShipShipProperties, aiProperties, shipProperties)) break;

                    if (limitHowManyProjectilesAreLaunchedTowardsEnemyShip(aiProperties, entityProperties, shipProperties)) break;

                    currentWeaponsList.clear();
                    currentWeaponsList.addAll(shipProperties.getShipData().weaponTypeList);
                    Collections.sort(currentWeaponsList, weaponComparator);
                    if (!currentWeaponsList.isEmpty()) {
                        boolean launch = false;
                        for (WeaponType weaponType : currentWeaponsList) {
                            // WeaponType weaponType = currentWeaponsList.get(0);
                            boolean specialWeapon = WeaponType.getSpecialWeapon(weaponType);

                            if (specialWeapon) {
                                weaponProperties.setCurrentWeaponType(weaponType);
                                if (weaponProperties.hasCurrentWeaponAmmo()
                                        && ENG_Utility
                                        .hasRandomChanceHit(FrameInterval.SPECIAL_WEAPON_CHANCE + entityProperties.getNode().getName() + "_" + weaponType, SPECIAL_WEAPON_CHANCE)) {
                                    launch = true;
                                    break;
                                }
                            }
                        }

                        if (!launch) { // No specials for now
                            // weaponProperties.setCurrentWeaponType(currentWeaponsList.get(0));

                            for (WeaponType wpn : currentWeaponsList) {
                                if (!WeaponType.getSpecialWeapon(wpn)) {
                                    weaponProperties.setCurrentWeaponType(wpn);
                                    if (weaponProperties.hasCurrentWeaponAmmo()) {
                                        launch = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (launch) {
                            EntityProperties followedShipEntityComponent = entityPropertiesMapper.get(followedShip);
                            if (followedShipEntityComponent.getNode().getPosition().distance(entityProperties.getNode().getPosition()) >
                                    WeaponData.getWeaponData(weaponProperties.getCurrentWeaponType()).maxDistance) {
                                aiProperties.decrementCurrentLaunchedProjectiles();
                                // aiProperties.setEnemySelected(false);
                                aiProperties.setState(AIState.FOLLOW_PLAYER);
                                break;
                            }

                            if (!aiProperties.isEnemySelected()) {
                                aiProperties.setEnemySelected(true);
                                aiProperties.setEnemySelectionTimeStarted();
                            } else if (ENG_Utility.hasTimePassed(
                                    FrameInterval.WEAPON_ENEMY_SELECTION_TIME + entityProperties.getNode().getName(),
                                    aiProperties.getEnemySelectionTimeStarted(),
                                    WeaponType.getWeaponEnemySelectionTime(weaponProperties.getCurrentWeaponType()))) {
                                if (ENG_Utility
                                        .hasTimePassed(
                                                FrameInterval.WEAPON_COOLDOWN_TIME + entityProperties.getNode().getName(),
                                                aiProperties.getWeaponCooldownTimeStarted(),
                                                WeaponType.getWeaponCooldownTime(weaponProperties.getCurrentWeaponType()))) {
//                                    System.out.println("setCurrentSelectedEnemy from AIManager for ship: " + entityProperties.getName() + " followed ship: " + aiProperties.getFollowedShip());
                                    shipProperties.setCurrentSelectedEnemy(aiProperties.getFollowedShip());

                                    // If we're not on homing missiles we must shoot
                                    // ahead
                                    shootAhead(aiProperties, entityProperties, shipProperties, weaponProperties, followedShip);

                                    // if (!projectileCreated) {
                                    MainApp.getGame().getWorldManager().createProjectile(e);
                                    // projectileCreated = true;
                                    // }
                                    shipProperties.resetCurrentSelectedEnemy();
                                    aiProperties.setWeaponCooldownTimeStarted();
                                    weaponProperties.decrementCurrentWeaponAmmo();
                                    // System.out.println("Projectile " +
                                    // weaponProperties.getCurrentWeaponType() +
                                    // " launched by " + shipProperties.getName());
                                }
                            }
                        }
                    }
                    boolean targeted = checkTargeted(aiProperties, shipProperties);
                    if (targeted) {
                        break;
                    }
                    aiProperties.setState(AIState.FOLLOW_PLAYER);
                }
                break;
                case EVADE_MISSILE: {
                    // Seek the optimal way to escape the closest chasing projectile
                    if (checkFreeFromBeingChased(shipProperties, aiProperties)) break;

                    ClosestProjectile closestProjectile = getClosestProjectile(entityProperties, shipProperties);

                    if (closestProjectile.minDistProjectile != null) {
                        // Launch countermeasures if close enough
                        checkShouldLaunchCountermeasures(e, closestProjectile, entityProperties, shipProperties);
                        EntityProperties projectileEntityProperties = entityPropertiesMapper.get(closestProjectile.minDistProjectile);
                        entityProperties.getNode().getLocalZAxis(currentFrontVec);
                        entityProperties.getNode().getPosition(currentPos);
                        projectileEntityProperties.getNode().getLocalZAxis(otherFrontVec);
                        projectileEntityProperties.getNode().getPosition(otherPos);
                        // Try to evade by moving erratically random and away from
                        // incoming projectile
                        if (closestProjectile.minSquaredDist < MIN_EVASION_DISTANCE) {
                            // Try to create a 90 degrees angle to evade projectile
                            if (ENG_Utility.hasRandomChanceHit(FrameInterval.MIN_EVASION_DISTANCE + entityProperties.getNode().getName(), 2)) {
                                entityProperties.getNode().getLocalXAxis(perpendicularVec);
                            } else {
                                entityProperties.getNode().getLocalYAxis(perpendicularVec);
                            }
                            if (ENG_Utility.hasRandomChanceHit(FrameInterval.MIN_EVASION_DISTANCE_INVERT + entityProperties.getNode().getName(), 2)) {
                                perpendicularVec.invertInPlace();
                            }
//                            ENG_Math.rotateToDirectionDeg(perpendicularVec, currentFrontVec, getRotationAngle(shipProperties), rotation);
                            Utility.rotateToPosition(currentFrontVec, perpendicularVec, updateInterval, entityProperties,
                                    shipProperties.getShipData().maxAngularVelocity);

                        } else {

                            // Try to outrun the projectile

//                            ENG_Math.rotateAwayFromPositionDeg(otherPos, currentPos, currentFrontVec, getRotationAngle(shipProperties), rotation);
                            Utility.rotateAwayFromPosition(currentFrontVec, otherPos, updateInterval, entityProperties,
                                    shipProperties.getShipData().maxAngularVelocity);

                        }
//                        entityProperties.rotate(rotation, true, TransformSpace.TS_WORLD);

                        accelerateToMaxSpeed(shipProperties, entityProperties, aiProperties,
                                VELOCITY_CHANGE_NAME_EVADE_MISSILE, EVASION_MISSILE_SPEED_CHANGE_STEP);
                    }
                }
                break;
                case COLLISION_RESPONSE: {
                    if (aiProperties.isCollided()) {
                        if (!aiProperties.isAxisAndAngleSelected()) {
                            aiProperties.setAxisAndAngleSelected(true);
                            if (aiProperties.isCollidedWithStaticObject()) {
                                aiProperties.getCollisionEvasionDirectionFromStaticObject(collisionResponseAxis);
                                if (collisionResponseAxis.isZeroLength()) {
                                    throw new IllegalStateException(entityProperties.getName() + " has evasionDir zero!");
                                }
                            } else {
                                // Take either the local x or y axis to be the destination towards we rotate the ship.
                                int nextInt = ENG_Utility.getRandom().nextInt(FrameInterval.COLLISION_RESPONSE_AXIS + entityProperties.getNode().getName(), 2);
                                switch (nextInt) {
                                    case 0:
                                        entityProperties.getNode().getLocalXAxis(collisionResponseAxis);
                                        break;
                                    case 1:
                                        entityProperties.getNode().getLocalYAxis(collisionResponseAxis);
                                        break;
                                    default:
                                        throw new IllegalArgumentException();
                                }
                            }
                            collisionResponseAxis.normalize();
                            aiProperties.setCollisionEvasionDestination(collisionResponseAxis);
                            aiProperties.setCollisionAngleDirection(ENG_Utility.hasRandomChanceHit(
                                    FrameInterval.COLLISION_RESPONSE_DIR + entityProperties.getNode().getName(), 2) ? -1.0f : 1.0f);
                            entityProperties.setVelocity(0.0f);
                        }
                    }
                    if (aiProperties.getCurrentCollisionAngle() < COLLISION_RESPONSE_ACCELERATION_ANGLE) {
//                        float rotationAngle = getRotationAngle(shipProperties);
                        entityProperties.getNode().getLocalInverseZAxis(currentFrontVec);
                        if (!aiProperties.isInitialFrontVecSet()) {
                            aiProperties.setInitialFrontVec(currentFrontVec);
                            aiProperties.setInitialFrontVecSet(true);
                        }
                        ENG_Vector4D axis = aiProperties.getCollisionAngleDirection() == 1 ?
                                aiProperties.getCollisionEvasionDestination().invert() :
                                aiProperties.getCollisionEvasionDestination();
                        Utility.rotateToPosition(currentFrontVec,
                                axis,
                                updateInterval, entityProperties,
                                shipProperties.getShipData().maxAngularVelocity);
//                        entityProperties.rotate(ENG_Quaternion.fromAngleAxisDegRet(
//                                        rotationAngle * aiProperties.getCollisionAngleDirection(),
//                                        aiProperties.getCollisionEvasionDestination()));
                        aiProperties.setCurrentCollisionAngle(currentFrontVec.angleBetween(aiProperties.getInitialFrontVec()));
                        if (DEBUG) {
                            System.out.println("COLLISION_RESPONSE: entity: " + entityProperties.getName() + " angleBetweenDeg: " + (currentFrontVec.angleBetween(aiProperties.getInitialFrontVec()) * ENG_Math.RADIANS_TO_DEGREES));
                        }
                    } else {
                        if (!aiProperties.isCollisionResponseMovement()) {
                            aiProperties.setCollisionResponseMovement(true);
                            aiProperties.setCollisionResponseMovementTime();
                        }
                        if (ENG_Utility.hasTimePassed(
                                FrameInterval.COLLISION_RESPONSE_ACCELERATION_TIME + entityProperties.getNode().getName(),
                                aiProperties.getCollisionResponseMovementTime(),
                                COLLISION_RESPONSE_ACCELERATION_TIME)) {
                            aiProperties.setCollisionResponseMovement(false);
                            aiProperties.setAxisAndAngleSelected(false);
                            aiProperties.setCollided(false);
                            aiProperties.setCollidedWithStaticObject(false);
                            aiProperties.setCollisionEvasionDirectionFromStaticObject(ENG_Math.VEC4_ZERO);
                            aiProperties.setState(AIState.SEEK_CLOSEST_PLAYER);
                            aiProperties.setCurrentCollisionAngle(0.0f);
                            aiProperties.setInitialFrontVec(ENG_Math.VEC4_ZERO);
                            aiProperties.setInitialFrontVecSet(false);
                        } else {
                            if (DEBUG) {
                                System.out.println("COLLISION_RESPONSE: entity: " + entityProperties.getName() + " velocityChange: " + shipProperties.getShipData().maxSpeed);
                            }
                            changeVelocity(entityProperties, shipProperties, aiProperties,
                                    new VelocityChange(
                                            VELOCITY_CHANGE_NAME_COLLISION_RESPONSE,
                                            shipProperties.getShipData().maxSpeed,
                                            COLLISION_RESPONSE_ACCELERATION_STEP));
                        }
                    }
                }
                break;
                case EVADE_HIT: {
                    if (!aiProperties.isEvadingHit()) {
                        aiProperties.setEvadingHit(true);
                        aiProperties.setHitEvasionTime();
                    }
                    if (ENG_Utility.hasTimePassed(
                            FrameInterval.EVASION_HIT_TIME + entityProperties.getNode().getName(),
                            aiProperties.getHitEvasionTime(),
                            EVASION_HIT_TIME)) {
                        aiProperties.setEvadingHit(false);
                        aiProperties.setEvadeHitDirectionSet(false);
                        aiProperties.setState(AIState.SEEK_CLOSEST_PLAYER);
                        showAIStateChange(shipProperties, aiProperties);
                    } else {
                        accelerateToMaxSpeed(shipProperties, entityProperties, aiProperties,
                                VELOCITY_CHANGE_NAME_EVADE_HIT, EVASION_HIT_SPEED_CHANGE_STEP);
                        int nextInt;
                        float dir;
                        if (aiProperties.isEvadeHitDirectionSet()) {
                            nextInt = aiProperties.getEvadeHitAxis();
                            dir = aiProperties.getEvadeHitDirection();
                        } else {
                            nextInt = ENG_Utility.getRandom().nextInt(FrameInterval.EVADE_HIT_AXIS + entityProperties.getNode().getName(), 3);
                            dir = ENG_Utility.hasRandomChanceHit(FrameInterval.EVADE_HIT_DIR + entityProperties.getNode().getName(), 2) ? -1.0f : 1.0f;
                            aiProperties.setEvadeHitAxisAndDirection(nextInt, dir);
                            aiProperties.setEvadeHitDirectionSet(true);
                        }
//					System.out.println(shipProperties.getName() +
//							" is evading in dir: " + nextInt);
                        entityProperties.getNode().getLocalInverseZAxis(currentFrontVec);
//                        int nextInt = ENG_Utility.getRandom().nextInt(FrameInterval.COLLISION_RESPONSE_AXIS + entityProperties.getNode().getName(), 3);
                        switch (nextInt) {
                            case 0:
                                entityProperties.getNode().getLocalXAxis(collisionResponseAxis);
                                break;
                            case 1:
                                entityProperties.getNode().getLocalYAxis(collisionResponseAxis);
                                break;
                            case 2:
                                entityProperties.getNode().getLocalZAxis(collisionResponseAxis);
                                break;
                            default:
                                throw new IllegalArgumentException();
                        }
                        Utility.rotateToPosition(currentFrontVec, collisionResponseAxis, updateInterval, entityProperties,
                                shipProperties.getShipData().maxAngularVelocity);
                    }
                }
                break;
                default:
                    throw new IllegalArgumentException(aiProperties.getState() + " is not a handled state");
            }
//            System.out.println("Ship " + entityProperties.getName() + " ending AI state: " + aiProperties.getState());
        }
    }

    private void rayTestCollisionAhead(EntityProperties entityProperties, AIProperties aiProperties) {
        rayTestFrontVec(entityProperties, aiProperties, FOLLOW_SHIP_RAY_TEST_DISTANCE_AHEAD);
        ClosestNotMeRayResultCallback rayResultCallback = aiProperties.getRayResultCallback();
        if (rayResultCallback.hasHit()) {
//            Vector3 hitPosition = PhysicsUtility.getHitPosition(rayResultCallback);
            if (EntityContactListener.isStaticEntityRigidBody(rayResultCallback.getCollisionObject().getUserPointer())) {
                entityProperties.getNode().getLocalInverseZAxis(currentFrontVec);
                aiProperties.setCollisionEvasionDirectionFromStaticObject(new ENG_Vector4D(currentFrontVec).invert());
                aiProperties.setCollidedWithStaticObject(true);
                aiProperties.setCollided(true);
            } else if (EntityContactListener.isEntityRigidBody(rayResultCallback.getCollisionObject().getUserPointer())) {
                EntityRigidBody eEntityRigidBody = (EntityRigidBody) rayResultCallback.getCollisionObject();
                Entity otherEntity = eEntityRigidBody.getEntity();
                EntityProperties otherEntityProperties = entityPropertiesMapper.get(otherEntity);
                if (otherEntityProperties.getObjectType() != null) {
                    // TODO should we deprecate the old way of checking of possible collision?
//                    aiProperties.setEvadingCollision(true);
//                    aiProperties.setEvadingCollisionTimeStarted();
                }
            }
        }
    }

    private static boolean checkFreeFromBeingChased(ShipProperties shipProperties, AIProperties aiProperties) {
        if (!shipProperties.isChased()) {
            aiProperties.setState(AIState.SEEK_CLOSEST_PLAYER);
            showAIStateChange(shipProperties, aiProperties);
            return true;
        }
        return false;
    }

    private static boolean checkShouldReachDestination(AIProperties aiProperties, ShipProperties shipProperties) {
        if (aiProperties.isReachDestination()) {
            aiProperties.setState(AIState.REACH_DESTINATION);
            showAIStateChange(shipProperties, aiProperties);
            return true;
        }
        return false;
    }

    private void accelerateToMaxSpeed(ShipProperties shipProperties, EntityProperties entityProperties,
                                      AIProperties aiProperties, int velocityChangeNameEvadeMissile,
                                      float evasionMissileSpeedChangeStep) {
        float maxSpeed = shipProperties.getShipData().maxSpeed;
        if (maxSpeed > entityProperties.getVelocity()) {
            changeVelocity(entityProperties, shipProperties, aiProperties,
                    new VelocityChange(
                            velocityChangeNameEvadeMissile,
                            maxSpeed, evasionMissileSpeedChangeStep));
        }
    }

    private static void checkShouldLaunchCountermeasures(Entity e, ClosestProjectile closestProjectile,
                                                         EntityProperties entityProperties, ShipProperties shipProperties) {
        if (closestProjectile.minSquaredDist < MIN_COUNTERMEASURES_DISTANCE) {
            if (ENG_Utility.hasTimePassed(
                    FrameInterval.AI_COUNTERMEASURE_TIME + entityProperties.getNode().getName(),
                    shipProperties.getCountermeasuresLastLaunchTime(),
                    ShipData.COUNTERMEASURE_TIME)) {
                MainApp.getGame().getWorldManager().createCountermeasures(e);
                shipProperties.setAfterburnerActive(true);
                shipProperties.setCountermeasuresLastLaunchTime();
                System.out.println("Creating countermeasure for entity: " + entityProperties.getName());
            }
        }
    }

    private ClosestProjectile getClosestProjectile(EntityProperties entityProperties, ShipProperties shipProperties) {
        entityProperties.getNode().getPosition(currentPos);
        MainApp.getGame().getWorldManager().getClosestProjectileSquaredDistance(currentPos, shipProperties, data);
        float minSquaredDist = data.minDist;
        Entity minDistProjectile = MainApp.getGame().getWorldManager().getEntityByGameEntityId(data.objectId);
        return new ClosestProjectile(minSquaredDist, minDistProjectile);
    }

    private static class ClosestProjectile {
        public final float minSquaredDist;
        public final Entity minDistProjectile;

        public ClosestProjectile(float minSquaredDist, Entity minDistProjectile) {
            this.minSquaredDist = minSquaredDist;
            this.minDistProjectile = minDistProjectile;
        }
    }

    private int setOtherPosFromWaypointWaypointSector;
    private int setOtherPosFromWaypointWaypointId;
    private AIProperties.AIWaypointState setOtherPosFromWaypointAIWaypointState;

    private void setOtherPosFromWaypoint(AIProperties aiProperties) {
        int waypointId = getNextWaypointId(aiProperties);
        if (waypointId == 0) {
            throw new IllegalStateException("waypointId is 0");
        }
        Waypoint waypoint = getWaypoint(aiProperties.getCurrentWaypointSectorId(), waypointId);
        otherPos.set(waypoint.getPosition());
        if (WaypointSystem.DEBUG) {
            if (setOtherPosFromWaypointWaypointSector != aiProperties.getCurrentWaypointSectorId() ||
                setOtherPosFromWaypointWaypointId != waypoint.getId() ||
                setOtherPosFromWaypointAIWaypointState != aiProperties.getWaypointState()) {
                System.out.println("setOtherPosFromWaypoint() FOLLOWING_SHIP otherPos: " + otherPos +
                        " waypointSector: " + aiProperties.getCurrentWaypointSectorId() +
                        " waypointId: " + waypoint.getId() + " waypointState: " + aiProperties.getWaypointState());
                setOtherPosFromWaypointWaypointSector = aiProperties.getCurrentWaypointSectorId();;
                setOtherPosFromWaypointWaypointId = waypoint.getId();
                setOtherPosFromWaypointAIWaypointState = aiProperties.getWaypointState();
            }
        }
    }

    private void accelerateTowardsWaypoint(EntityProperties entityProperties, ShipProperties shipProperties, AIProperties aiProperties) {
        Waypoint waypoint = getNextWaypoint(aiProperties);
        float distance = currentPos.distance(waypoint.getPosition());
        if (distance < waypoint.getRadius()) {
            // Slow down when approaching the waypoint.
            changeVelocity(entityProperties, shipProperties, aiProperties, new VelocityChange(
                    VELOCITY_CHANGE_NAME_MIN_DISTANCE_WAYPOINT,
                    ZERO_SPEED, MIN_DISTANCE_SLOW_DOWN_RATE));
        } else {
            // If the AI is closing down on the waypoint we need to move slowly.
            if (distance <= WAYPOINT_SLOW_MOVE_DISTANCE) {
                changeVelocity(entityProperties, shipProperties, aiProperties,
                        new VelocityChange(
                                VELOCITY_CHANGE_NAME_ACCELERATE_TO_MIN_SPEED_FOLLOWING_WAYPOINT,
                                Math.min(shipProperties.getShipData().maxSpeed, WAYPOINT_MIN_SPEED),
                                FOLLOWING_SHIP_ACCELERATION_RATE));
            } else {
                changeVelocity(entityProperties, shipProperties, aiProperties,
                        new VelocityChange(
                                VELOCITY_CHANGE_NAME_ACCELERATE_TO_MAX_SPEED_FOLLOWING_WAYPOINT,
                                Math.min(shipProperties.getShipData().maxSpeed, WAYPOINT_MAX_SPEED),
                                FOLLOWING_SHIP_ACCELERATION_RATE));
            }
        }
    }

    private boolean checkLimitsReached(EntityProperties entityProperties, AIProperties aiProperties, ShipProperties shipProperties) {
        if (entityProperties.isLimitsReached()) {
            aiProperties.setState(AIState.EVADE_LEVEL_LIMITS);
            showAIStateChange(shipProperties, aiProperties);
            return true;
        }
        return false;
    }

    private boolean checkCollided(AIProperties aiProperties, ShipProperties shipProperties) {
        if (aiProperties.isCollided()) {
            // Ignore standard collision response if we are in a waypoint sector.
            if (isFollowingWaypoint(aiProperties) &&
                    (aiProperties.getWaypointState() == AIProperties.AIWaypointState.MOVING_TO_WAYPOINT_DESTINATION ||
                            aiProperties.getWaypointState() == AIProperties.AIWaypointState.MOVING_TO_EXIT)/* || aiProperties.isWaypointShootPlayer()*/) {
                return false;
            }
            aiProperties.setState(AIState.COLLISION_RESPONSE);
            showAIStateChange(shipProperties, aiProperties);
            return true;
        }
        return false;
    }

    private boolean checkShouldEvadeHit(AIProperties aiProperties, EntityProperties entityProperties, ShipProperties shipProperties) {
        if (aiProperties.getCurrentHealth() > entityProperties.getHealth() && !aiProperties.isCollided()) {
            aiProperties.setCurrentHealth(entityProperties.getHealth());
            // If we are following a waypoint from inside a sector or while moving to exit
            // we should still update the health after a collision but skip the EVADE_HIT state.
            // Otherwise, after exiting the sector we would revert to EVADE_HIT even if the hit event
            // happened a long time ago since the health finally gets updated.
            if (isFollowingWaypoint(aiProperties) &&
                    (aiProperties.getWaypointState() == AIProperties.AIWaypointState.MOVING_TO_WAYPOINT_DESTINATION ||
                            aiProperties.getWaypointState() == AIProperties.AIWaypointState.MOVING_TO_EXIT)/* || aiProperties.isWaypointShootPlayer()*/) {
                return false;
            }
            aiProperties.setState(AIState.EVADE_HIT);
            showAIStateChange(shipProperties, aiProperties);
            return true;
        }
        return false;
    }

    private void matchFollowedEntitySpeed(float squaredDist, EntityProperties otherShipEntityProperties,
                                          EntityProperties entityProperties, ShipProperties shipProperties,
                                          AIProperties aiProperties) {
        if (squaredDist > ENEMY_MIN_DISTANCE + otherShipEntityProperties.getItem().getWorldAABB().getHalfSize().squaredLength()) {
            boolean matchingSpeed = false;
            boolean followingWaypoint = isFollowingWaypoint(aiProperties);
            if (squaredDist < TARGET_MIN_DISTANCE) {
                // Try and match the followed ship speed
                float otherShipVelocity = followingWaypoint ? otherShipEntityProperties.getVelocity() * 2.0f / 3.0f :
                        otherShipEntityProperties.getVelocity();
                float velocity = entityProperties.getVelocity();
                if (otherShipVelocity > ENG_Math.FLOAT_EPSILON) {
                    if (otherShipVelocity < shipProperties.getShipData().maxSpeed) {
                        if (otherShipVelocity != velocity) {
                            changeVelocity(entityProperties, shipProperties, aiProperties,
                                    new VelocityChange(
                                            VELOCITY_CHANGE_NAME_FOLLOWED_SHIP_VELOCITY,
                                            otherShipVelocity,
                                            SPEED_CHANGE_RATE));
                        }
                    }
                    matchingSpeed = true;
                }
            }
            if (!matchingSpeed) {
                float maxSpeed = shipProperties.getShipData().maxSpeed;
                maxSpeed = followingWaypoint ? Math.min(100, maxSpeed) : maxSpeed;
                changeVelocity(entityProperties, shipProperties, aiProperties,
                        new VelocityChange(
                                VELOCITY_CHANGE_NAME_ACCELERATE_TO_MAX_SPEED,
                                maxSpeed,
                                FOLLOWING_SHIP_ACCELERATION_RATE));
            }
        } else {
            changeVelocity(entityProperties, shipProperties, aiProperties, new VelocityChange(
                    VELOCITY_CHANGE_NAME_MIN_DISTANCE,
                    ZERO_SPEED, MIN_DISTANCE_SLOW_DOWN_RATE));
        }
    }

    private void checkMinDistanceFollowedEntity(AIProperties aiProperties, float squaredDist,
                                                WeaponProperties weaponProperties, ShipProperties shipProperties) {
        if (!aiProperties.isLockedIn()) {
            if (squaredDist < MIN_FOLLOW_DIST) {
                if (!aiProperties.isFollowCountTimeStarted()) {
                    aiProperties.setFollowBeginTime();
                    aiProperties.setFollowCountTimeStarted(true);
                } else {
                    if (ENG_Utility.hasTimePassed(aiProperties.getFollowBeginTime(), WeaponType.getWeaponEnemySelectionTime(weaponProperties.getCurrentWeaponType()))) {
                        aiProperties.setLockedIn(true);
                    }
                }
            } else {
                aiProperties.setFollowCountTimeStarted(false);

                aiProperties.setState(AIState.SEEK_CLOSEST_PLAYER);
                showAIStateChange(shipProperties, aiProperties);
            }
        }
    }

    private enum WaypointRouteType {
        OUT_OUT,
        OUT_INSIDE,
        INSIDE_OUT,
        INSIDE_INSIDE
    }

    private WaypointRouteType routeType;
    private int updateWaypointRouteWaypointSectorId;
    private int updateWaypointRouteWaypointId;

    private void updateWaypointRoute(Entity e, AIProperties aiProperties, AIProperties otherAiProperties,
                                     EntityProperties entityProperties, EntityProperties otherShipEntityProperties,
                                     ShipProperties shipProperties) {
        // Check if we need to update the waypoint routes.
        WaypointSystem waypointSystem = WaypointSystem.getSingleton();
        int reachedWaypointId = 0;
        boolean reachedNextWaypoint = false;
        if (aiProperties.getWaypointState() != AIProperties.AIWaypointState.NONE) {
            // get*() returns a copy so it's ok to set before doing the check.
            ENG_Vector4D lastPosition = aiProperties.getLastPosition();
            ENG_Vector4D lastFollowedShipPosition = aiProperties.getLastFollowedShipPosition();
            if (lastPosition.distance(currentPos) < WAYPOINT_RECHECK_DISTANCE &&
                    lastFollowedShipPosition.distance(otherPos) < WAYPOINT_RECHECK_DISTANCE) {
                // Check if we reached any destination waypoint.
                int waypointSectorId = aiProperties.getCurrentWaypointSectorId();
                int waypointId = 0;
                Waypoint waypoint = getNextWaypoint(aiProperties);
                if (currentPos.distance(waypoint.getPosition()) <= waypoint.getRadius()) {
                    // Advance to the next waypoint in the chain.
                    if (updateWaypointRouteWaypointSectorId != waypointSectorId ||
                        updateWaypointRouteWaypointId != waypoint.getId()) {
                        ENG_Log.getInstance().log("Moving from waypoint sector id: " + waypointSectorId +
                                " waypoint id: " + waypoint.getId() + " to next waypoint. Current state: " +
                                aiProperties.getWaypointState());
                        updateWaypointRouteWaypointSectorId = waypointSectorId;
                        updateWaypointRouteWaypointId = waypoint.getId();
                    }
                    reachedWaypointId = waypoint.getId();
                    reachedNextWaypoint = true;
                } else {
                    // No need to recheck for entrance/exit/destination waypoint unless we either reach a new waypoint
                    // or we have moved enough distance (either us or the target).

                    boolean shouldFallThrough = false;
                    // If we have been shooting we need to fall through and recheck if we need to go
                    // to the closest waypoint to our target or if we can simply shoot from where we are.
                    if (aiProperties.isWaypointShootPlayer()) {
                        if (!checkAllowedToShoot()) {
                            aiProperties.setWaypointShootPlayer(false);
                        }
                        setLastPositions(aiProperties);
                        shouldFallThrough = true;
                        System.out.println("Should fallthrough aiProperties.isWaypointShootPlayer()");
                    }

                    // If we are being chased then continue to the rest of the method.
                    // If we are being chased we don't want to wait until we have reached the next waypoint.
                    if (aiProperties.isFollowingWaypointWhileChased()) {
                        // Force rechecking if next waypoint reached every time AISystem.process() is called.
                        setLastPositions(aiProperties);
                        shouldFallThrough = true;
                        System.out.println("Should fallthrough aiProperties.isFollowingWaypointWhileChased()");
                    }
                    if (!shouldFallThrough) return;
                }
            } else {
                // Since the positions from AI ship and target have changed so much, we have to recalculate.
                setLastPositions(aiProperties);
            }
        }
        // Check if the target is in a waypoint sector.
        int currentPosWaypointSectorId = waypointSystem.checkPositionInWaypointSector(currentPos);
        int otherPosWaypointSectorId = waypointSystem.checkPositionInWaypointSector(otherPos);

        if (currentPosWaypointSectorId == -1 && aiProperties.isFollowingWaypointWhileChased()) {
            // We are outside so we can revert to normal evasion
            updateWaypointUserCount(entityProperties.getEntityId(), aiProperties.getCurrentWaypointSectorId(),
                    aiProperties.getCurrentTargetWaypointId(), 0, 0);
            aiProperties.resetWaypointData();
            return;
        }
        if (currentPosWaypointSectorId != -1 && aiProperties.getWaypointChainToEvasionWaypoint() != null) {
            boolean reachedChainEnd = false;
            if (reachedNextWaypoint) {
                // The 0 element destination from the array has already been set in checkIsChasedWithinWaypointSector().
                aiProperties.incrementWaypointChainToEvasionWaypointCurrentIndex();
                int waypointChainCurrentIndex = aiProperties.getWaypointChainToEvasionWaypointCurrentIndex();
                if (waypointChainCurrentIndex < aiProperties.getWaypointChainToEvasionWaypoint().size()) {
                    int waypointSectorId = aiProperties.getCurrentWaypointSectorId();
                    ENG_Integer nextWaypointId = aiProperties.getWaypointChainToEvasionWaypoint().get(waypointChainCurrentIndex);
                    aiProperties.setCurrentWaypointId(aiProperties.getCurrentTargetWaypointId());
                    if (waypointSectorId != 0 && nextWaypointId.getValue() != 0) {
                        Waypoint nextWaypoint = getWaypoint(waypointSectorId, nextWaypointId.getValue());
                        AIProperties.AIWaypointState nextWaypointState = AIProperties.AIWaypointState.MOVING_TO_WAYPOINT_DESTINATION;
                        setNextWaypointId(entityProperties, aiProperties, nextWaypointState, waypointSectorId,
                                aiProperties.getCurrentWaypointId(), nextWaypointId.getValue());
                        ENG_Log.getInstance().log("evading reachedNextWaypoint setting next waypoint from chain pos: " +
                                waypointChainCurrentIndex + " currentWaypointId: " + aiProperties.getCurrentWaypointId() +
                                " nextWaypointId: " + nextWaypointId);
                    } else {
                        // Should never get here.
                        throw new IllegalStateException("waypointSectorId: " + waypointSectorId + " nextWaypointId: " + nextWaypointId.getValue());
                    }
                    return;
                } else {
                    // Continue down the method.
                    updateWaypointUserCount(entityProperties.getEntityId(), aiProperties.getCurrentWaypointSectorId(),
                            aiProperties.getCurrentTargetWaypointId(), 0, 0);
                    aiProperties.resetWaypointData();
                    reachedChainEnd = true;
                }
            }
            if (!reachedChainEnd) {
                return;
            }
        }

        // There are 4 scenarios:
        // 1. AI is outside of a sector and target is outside sector. Check if no sector between AI and target.
        // 2. AI is inside a sector and target is outside sector. AI must find the exit from the current sector.
        // 3. AI is outside of a sector and target is inside sector. AI must navigate to the closest entrance to the target sector.
        // 4. AI is inside a sector and target is inside a sector. If same sector just proceed through waypoints.
        // If different sectors then search for closest exit from current sector to the entrance of the target sector.
        // Check if during moving from current position to target we don't get a sector in between the AI and its target.

        // One issue is if the radius of the waypoint means that a waypoint can be reached while the AI is outside
        // of the waypoint sector of which that reached waypoint belongs.
        if (currentPosWaypointSectorId == -1 && aiProperties.getCurrentWaypointSectorId() > 0) {
            currentPosWaypointSectorId = aiProperties.getCurrentWaypointSectorId();
        }

        int currentWaypointId = -1;
        int otherWaypointId = -1;
        if (currentPosWaypointSectorId != -1) {
            WaypointSector waypointSector = waypointSystem.getWaypointSector(currentPosWaypointSectorId);
            Waypoint closestWaypoint = waypointSector.getClosestWaypoint(currentPos);
            if (closestWaypoint == null) {
                // Should never get here.
                throw new IllegalStateException("closestWaypoint is null for currentPos: " + currentPos);
            }
            currentWaypointId = closestWaypoint.getId();
//            aiProperties.setCurrentWaypointSectorId(currentPosWaypointSectorId);
//            aiProperties.setCurrentTargetWaypointId(currentWaypointId);
        } else {
            // Even if we are not in a sector we should check we don't have one ahead of us.
            // And if we do we should go to its entrance or avoid it altogether.
        }
        if (otherPosWaypointSectorId != -1) {
            if (otherAiProperties != null) {
                otherWaypointId = otherAiProperties.getCurrentTargetWaypointId();
            }
        }
        if (currentPosWaypointSectorId == -1 && otherPosWaypointSectorId == -1) {
            // 1) Check if no sector between AI and target.
            // We could use the ray intersection queries but we have a delay between sending the ray
            // and receiving the result.
            // It would be easier to implement to take the vector between the 2 points and break it down
            // based on distance. Break it into 200-500 points along the ray and see if those points
            // are inside of some sectors.
            if (routeType != WaypointRouteType.OUT_OUT) {
                ENG_Log.getInstance().log("ship: " + entityProperties.getName() +
                        " and followed ship: " + otherShipEntityProperties.getName() +
                        " both outside of waypoint sectors");
                routeType = WaypointRouteType.OUT_OUT;
            }
            moveToClosestEntrance(entityProperties, aiProperties, currentWaypointId, AIProperties.AIWaypointState.MOVING_TO_ENTRANCE);
        }
        if (currentPosWaypointSectorId != -1 && otherPosWaypointSectorId == -1) {
            // 2)
            if (routeType != WaypointRouteType.INSIDE_OUT) {
                ENG_Log.getInstance().log("ship: " + entityProperties.getName() +
                        " inside of waypoint sector and followed ship: " + otherShipEntityProperties.getName() +
                        " outside of waypoint sectors");
                routeType = WaypointRouteType.INSIDE_OUT;
            }
            moveToOutsideOfCurrentSector(e, entityProperties, shipProperties, aiProperties, waypointSystem, reachedNextWaypoint,
                    currentPosWaypointSectorId, currentWaypointId, reachedWaypointId);
        }
        if (currentPosWaypointSectorId == -1 && otherPosWaypointSectorId != -1) {
            // 3)
            if (routeType != WaypointRouteType.OUT_INSIDE) {
                ENG_Log.getInstance().log("ship: " + entityProperties.getName() +
                        " outside of waypoint sector and followed ship: " + otherShipEntityProperties.getName() +
                        " inside of waypoint sector");
                routeType = WaypointRouteType.OUT_INSIDE;
            }
            // TODO determine which method to use: go to the nearest entrance,
            //  or go to the nearest entrance that is closest to the enemy ship?

            // For now we go to the nearest entrance like in the currentPosWaypointSectorId == -1 && otherPosWaypointSectorId == -1 case.
            moveToClosestEntrance(entityProperties, aiProperties, currentWaypointId, AIProperties.AIWaypointState.MOVING_TO_ENTRANCE);
        }
        if (currentPosWaypointSectorId != -1 && otherPosWaypointSectorId != -1) {
            // Check if we came from an entrance or just spawned inside of the sector.
            if (currentPosWaypointSectorId == otherPosWaypointSectorId) {
                // 4) A) AI and target are in the same sector.
                if (routeType != WaypointRouteType.INSIDE_INSIDE) {
                    ENG_Log.getInstance().log("ship: " + entityProperties.getName() +
                            " and followed ship: " + otherShipEntityProperties.getName() +
                            " both inside of the same waypoint sector");
                    routeType = WaypointRouteType.INSIDE_INSIDE;
                }
                moveInsideWaypointSectorTowardsTarget(e, aiProperties, entityProperties, shipProperties,
                        currentPosWaypointSectorId, waypointSystem, reachedNextWaypoint);
//                WaypointSector currentWaypointSector = waypointSystem.getWaypointSector(currentPosWaypointSectorId);
//                WaypointSector otherWaypointSector = waypointSystem.getWaypointSector(otherPosWaypointSectorId);
//                int nextWaypointToDestination = currentWaypointSector.getNextWaypointToDestination(currentPos, otherPos, false, false);
//                ArrayList<ENG_Integer> waypointChainFromPosition = currentWaypointSector.getWaypointChainFromPosition(currentPos, otherPos, false, false);
//                Waypoint nextWaypoint = currentWaypointSector.getWaypoint(nextWaypointToDestination);
//                setNextWaypointId(aiProperties, AIProperties.AIWaypointState.MOVING_TO_WAYPOINT_DESTINATION,
//                        currentPosWaypointSectorId, aiProperties.getCurrentWaypointId(), nextWaypointId);
//                setLastPositions(aiProperties);

//                Utility.rotateToPosition(entityProperties.getNode().getLocalInverseZAxis(),
//                        nextWaypoint.getPosition().subAsVec(entityProperties.getNode().getPosition()),
//                        updateInterval, entityProperties,
//                        shipProperties.getShipData().maxAngularVelocity);

//                changeVelocity(entityProperties, shipProperties, aiProperties,
//                        new VelocityChange(
//                                VELOCITY_CHANGE_NAME_TOWARD_ENEMY_SHIP_SAME_SECTOR,
//                                shipProperties.getShipData().maxSpeed,
//                                INSIDE_SECTOR_SPEED_CHANGE_RATE));
            } else {
                // 4) B) AI and target are in different sectors.
                // Check for exit and search if no sectors are in between the target sector.
                ENG_Log.getInstance().log("ship: " + entityProperties.getName() +
                        " and followed ship: " + otherShipEntityProperties.getName() +
                        " both inside of different waypoint sector");
                moveToOutsideOfCurrentSector(e, entityProperties, shipProperties, aiProperties, waypointSystem, reachedNextWaypoint,
                        currentPosWaypointSectorId, currentWaypointId, reachedWaypointId);
//                int waypointSectorId = 0;
//                int nextWaypointId = 0;
//                switch (aiProperties.getWaypointState()) {
//                    case NONE:
//                        // We just spawned in the sector.
//                        // Get the closest exit directing towards the target.
//                        waypointSectorId = currentPosWaypointSectorId;
//                        nextWaypointId = moveToClosestEntranceSpawnedInside(aiProperties, currentWaypointId, waypointSystem, currentPosWaypointSectorId);
//                        break;
//                    case MOVING_TO_EXIT:
//                        // If we had the target in the sector but now the target left the sector and we must also exit.
//                        waypointSectorId = aiProperties.getCurrentWaypointSectorId();
//                        nextWaypointId = waypointSystem.getWaypointSector(waypointSectorId)
//                                .getNextWaypointToDestination(aiProperties.getCurrentWaypointId(), aiProperties.getExitWaypointId());
//                        break;
//                    case MOVING_TO_WAYPOINT_DESTINATION:
//                    case MOVING_TO_ENTRANCE:
//                    default:
//                        throw new IllegalStateException("Unexpected value: " + aiProperties.getWaypointState());
//                }
//                WaypointSector currentWaypointSector = waypointSystem.getWaypointSector(currentPosWaypointSectorId);
//                WaypointSector otherWaypointSector = waypointSystem.getWaypointSector(otherPosWaypointSectorId);
//                int nextWaypointIdToDestination = currentWaypointSector.getNextWaypointToDestination(currentPos, otherPos, false, true);
//                ArrayList<ENG_Integer> waypointChainFromPosition = currentWaypointSector.getWaypointChainFromPosition(currentPos, otherPos, false, true);
//                setNextWaypointId(aiProperties, AIProperties.AIWaypointState.MOVING_TO_EXIT, waypointSectorId,
//                        aiProperties.getCurrentWaypointId(), nextWaypointId);
//                setLastPositions(aiProperties);

//                Utility.rotateToPosition(entityProperties.getNode().getLocalInverseZAxis(),
//                        otherShipEntityProperties.getNode().getPosition().subAsVec(entityProperties.getNode().getPosition()),
//                        updateInterval, entityProperties,
//                        shipProperties.getShipData().maxAngularVelocity);

//                changeVelocity(entityProperties, shipProperties, aiProperties,
//                        new VelocityChange(
//                                VELOCITY_CHANGE_NAME_TOWARD_ENEMY_SHIP_DIFFERENT_SECTOR,
//                                shipProperties.getShipData().maxSpeed,
//                                INSIDE_SECTOR_SPEED_CHANGE_RATE));
                // When we are outside the first sector we must change the speed change rate to outside speed change rate.
            }
        }
    }

    private void moveInsideWaypointSectorTowardsTarget(Entity e, AIProperties aiProperties,
                                                       EntityProperties entityProperties,
                                                       ShipProperties shipProperties,
                                                       int currentPosWaypointSectorId,
                                                       WaypointSystem waypointSystem,
                                                       boolean reachedNextWaypoint) {
        int waypointSectorId = 0;
        int nextWaypointId = 0;
        if (WaypointSystem.DEBUG) {
            ENG_Log.getInstance().log("moveInsideWaypointSectorTowardsTarget(): " + aiProperties.getWaypointState());
        }
        AIProperties.AIWaypointState nextWaypointState = AIProperties.AIWaypointState.MOVING_TO_WAYPOINT_DESTINATION;
        switch (aiProperties.getWaypointState()) {
            case MOVING_TO_EXIT:
            case NONE:
                if (WaypointSystem.DEBUG) {
                    if (aiProperties.getWaypointState() == AIProperties.AIWaypointState.MOVING_TO_EXIT) {
                        ENG_Log.getInstance().log(entityProperties.getName() + " waypoint state: MOVING_TO_EXIT in moveInsideWaypointSectorTowardsTarget");
                    }
                }
                // We just spawned in the sector.
                waypointSectorId = currentPosWaypointSectorId;
                nextWaypointId = waypointSystem.getWaypointSector(currentPosWaypointSectorId)
                        .getNextWaypointToDestination(currentPos, otherPos, false, false);
                break;
            case MOVING_TO_ENTRANCE: {
                // 1. We have arrived at the entrance we must find a path to the enemy position.
                // 2. Or, we were coming from outside of sector, moving to entrance, but somehow we entered the sector
                // from other point other than an entrance. Check if we have indeed arrived at the entry waypoint.
                waypointSectorId = aiProperties.getCurrentWaypointSectorId();
                WaypointSector waypointSector = waypointSystem.getWaypointSector(waypointSectorId);
                Waypoint waypoint = waypointSector.getWaypoint(aiProperties.getEntranceWaypointId());
                if (waypoint.getPosition().distance(currentPos) < waypoint.getRadius()) {
                    int nextTargetWaypoint = waypointSector.getClosestWaypoint(otherPos).getId();
                    nextWaypointId = waypointSector.getNextWaypointToDestination(
                            aiProperties.getCurrentTargetWaypointId(), nextTargetWaypoint);
                } else {
                    nextWaypointId = aiProperties.getEntranceWaypointId();
                    nextWaypointState = AIProperties.AIWaypointState.MOVING_TO_ENTRANCE;
                }
            }
                break;
            case MOVING_TO_WAYPOINT_DESTINATION: {
                // Check if we have arrived at the current waypoint destination and then go to the next destination.
                if (reachedNextWaypoint) {
                    // Get the next position from the target, then the target waypoint id becomes the current waypoint id.
                    waypointSectorId = aiProperties.getCurrentWaypointSectorId();
                    WaypointSector waypointSector = waypointSystem.getWaypointSector(waypointSectorId);
                    int nextTargetWaypoint = waypointSector.getClosestWaypoint(otherPos).getId();
                    if (nextTargetWaypoint == aiProperties.getCurrentTargetWaypointId()) {
                        // If we have arrived at the destination closest to enemy target we can pursue maneuvers
                        // around this checkpoint and the closest checkpoints that also provide shooting positions
                        // towards the enemy target.
                    } else {
                        nextWaypointId = waypointSector.getNextWaypointToDestination(
                                aiProperties.getCurrentTargetWaypointId(), nextTargetWaypoint);
                    }
                    aiProperties.setCurrentWaypointId(aiProperties.getCurrentTargetWaypointId());

                    // If we have arrived at the next checkpoint also check if we are close enough to the target
                    // in order to attack it.
                    if (checkAllowedToShoot()) {
                        aiProperties.setWaypointShootPlayer(true);
                    }
                }
            }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + aiProperties.getWaypointState());
        }
        if (waypointSectorId != 0 && nextWaypointId != 0) {
//            Waypoint nextWaypoint = getWaypoint(waypointSectorId, nextWaypointId);
            setNextWaypointId(entityProperties, aiProperties, nextWaypointState, waypointSectorId,
                    aiProperties.getCurrentWaypointId(), nextWaypointId);
        }
        setLastPositions(aiProperties);
        checkIsChasedWithinWaypointSector(e, entityProperties, shipProperties, aiProperties, waypointSystem);
    }

    private boolean checkAllowedToShoot() {
        // TODO the distance shouldn't be a constant. It should depend on the waypoint density in the region.
        return currentPos.distance(otherPos) < WAYPOINT_DISTANCE_FROM_WHERE_ALLOWED_TO_SHOOT;
    }

    private void moveToOutsideOfCurrentSector(Entity e, EntityProperties entityProperties,
                                              ShipProperties shipProperties, AIProperties aiProperties,
                                              WaypointSystem waypointSystem, boolean reachedNextWaypoint,
                                              int currentPosWaypointSectorId, int currentWaypointId,
                                              int reachedWaypointId) {
        int waypointSectorId = 0;
        int nextWaypointId = 0;
        if (WaypointSystem.DEBUG) {
            ENG_Log.getInstance().log("moveToOutsideOfCurrentSector(): " + aiProperties.getWaypointState());
        }
        switch (aiProperties.getWaypointState()) {
            case MOVING_TO_ENTRANCE: {
                if (aiProperties.getExitWaypointId() < 1) {
                    ENG_Log.getInstance().log("moveToOutsideOfCurrentSector MOVING_TO_ENTRANCE getExitWaypointId: " + aiProperties.getExitWaypointId());
                    break;
                }
                // 1. We have arrived at the entrance we must find a path to the enemy position.
                // 2. Or, we were coming from outside of sector, moving to entrance, but somehow we entered the sector
                // from other point other than an entrance. Check if we have indeed arrived at the entry waypoint.
                waypointSectorId = aiProperties.getCurrentWaypointSectorId();
                WaypointSector waypointSector = waypointSystem.getWaypointSector(waypointSectorId);
                nextWaypointId = waypointSector.getNextWaypointToDestination(
                        aiProperties.getEntranceWaypointId(), aiProperties.getExitWaypointId());
                // If we have just arrived in the sector and we are near the exit we must check so that there id nowhere to go
                // and get a 0 for the nextWaypoint.
                if (nextWaypointId == 0 && aiProperties.getEntranceWaypointId() == aiProperties.getExitWaypointId()) {
                    nextWaypointId = aiProperties.getExitWaypointId();
                }
                aiProperties.setCurrentWaypointId(aiProperties.getEntranceWaypointId());
            }
                break;
            case MOVING_TO_WAYPOINT_DESTINATION: {
                // Check if we have arrived at the current waypoint destination and then go to the next destination.
                if (reachedNextWaypoint) {
                    // Get the next position from the target, then the target waypoint id becomes the current waypoint id.
                    if (aiProperties.getExitWaypointId() < 1) {
                        ENG_Log.getInstance().log("moveToOutsideOfCurrentSector MOVING_TO_WAYPOINT_DESTINATION getExitWaypointId: " + aiProperties.getExitWaypointId());
                        break;
                    }
                    waypointSectorId = aiProperties.getCurrentWaypointSectorId();
                    nextWaypointId = waypointSystem.getWaypointSector(waypointSectorId)
                            .getNextWaypointToDestination(aiProperties.getCurrentTargetWaypointId(), aiProperties.getExitWaypointId());
                    // If we have just arrived in the sector and we are near the exit we must check so that there id nowhere to go
                    // and get a 0 for the nextWaypoint.
                    if (nextWaypointId == 0 && aiProperties.getEntranceWaypointId() == aiProperties.getExitWaypointId()) {
                        nextWaypointId = aiProperties.getExitWaypointId();
                    }
                    aiProperties.setCurrentWaypointId(aiProperties.getCurrentTargetWaypointId());
                }
            }
                break;
            case MOVING_TO_EXIT:
                // If we had the target in the sector but now the target left the sector and we must also exit.
                if (aiProperties.getExitWaypointId() < 1) {
                    ENG_Log.getInstance().log("moveToOutsideOfCurrentSector MOVING_TO_EXIT getExitWaypointId: " + aiProperties.getExitWaypointId());
                    break;
                }
                waypointSectorId = aiProperties.getCurrentWaypointSectorId();
                nextWaypointId = waypointSystem.getWaypointSector(waypointSectorId)
                        .getNextWaypointToDestination(aiProperties.getCurrentWaypointId(), aiProperties.getExitWaypointId());
                // If we have just arrived in the sector and we are near the exit we must check so that there id nowhere to go
                // and get a 0 for the nextWaypoint.
                if (nextWaypointId == 0 && aiProperties.getEntranceWaypointId() == aiProperties.getExitWaypointId()) {
                    nextWaypointId = aiProperties.getExitWaypointId();
                }
                break;
            case NONE: {
                // We must have spawned inside a waypoint sector.
                if (currentPosWaypointSectorId != aiProperties.getLastWaypointSectorId()) {
                    waypointSectorId = currentPosWaypointSectorId;
                    nextWaypointId = moveToClosestEntranceSpawnedInside(entityProperties, aiProperties, currentWaypointId, waypointSystem, currentPosWaypointSectorId);
                }
            }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + aiProperties.getWaypointState());
        }
//            if (WaypointSystem.DEBUG) {
//                if (waypointSectorId == 0 || nextWaypointId == 0) {
//                    throw new IllegalStateException("waypointSectorId: " + waypointSectorId + " nextWaypointId: " + nextWaypointId);
//                }
//            }
        updateNextWaypointId(entityProperties, aiProperties, reachedNextWaypoint, reachedWaypointId, waypointSectorId, nextWaypointId);
        setLastPositions(aiProperties);
        checkIsChasedWithinWaypointSector(e, entityProperties, shipProperties, aiProperties, waypointSystem);
    }

    private static void updateNextWaypointId(EntityProperties entityProperties, AIProperties aiProperties, boolean reachedNextWaypoint,
                                             int reachedWaypointId, int waypointSectorId, int nextWaypointId) {
        if (waypointSectorId != 0 && nextWaypointId != 0) {
//            Waypoint nextWaypoint = getWaypoint(waypointSectorId, nextWaypointId);
            AIProperties.AIWaypointState nextWaypointState = AIProperties.AIWaypointState.MOVING_TO_WAYPOINT_DESTINATION;
            if (nextWaypointId == aiProperties.getExitWaypointId()) {
                if (reachedNextWaypoint && reachedWaypointId == aiProperties.getExitWaypointId()) {
                    nextWaypointState = AIProperties.AIWaypointState.NONE;
                    aiProperties.setLastWaypointSectorId(waypointSectorId);
                    aiProperties.setCurrentWaypointId(0);
                    waypointSectorId = 0;
                    nextWaypointId = 0;
                } else {
                    nextWaypointState = AIProperties.AIWaypointState.MOVING_TO_EXIT;
                }
            }
            setNextWaypointId(entityProperties, aiProperties, nextWaypointState, waypointSectorId,
                    aiProperties.getCurrentWaypointId(), nextWaypointId);
        }
    }

    private void checkIsChasedWithinWaypointSector(Entity e, EntityProperties entityProperties,
                                                   ShipProperties shipProperties, AIProperties aiProperties,
                                                   WaypointSystem waypointSystem) {
        if (!aiProperties.isFollowingWaypointWhileChased()) return;
        // Force moving towards the closest exit.
        ClosestProjectile closestProjectile = getClosestProjectile(entityProperties, shipProperties);

        if (closestProjectile.minDistProjectile == null) return;
        // Check if we are aiming for the next waypoint so we don't run into a wall.
        WaypointSector waypointSector = waypointSystem.getWaypointSector(aiProperties.getCurrentWaypointSectorId());
        Waypoint targetWaypoint = waypointSector.getWaypoint(aiProperties.getCurrentTargetWaypointId());
        entityProperties.getNode().getPosition(currentPos);
        entityProperties.getNode().getLocalInverseZAxis(currentFrontVec);
        ENG_Vector4D waypointDiff = targetWaypoint.getPosition().subAsVec(currentPos);
        waypointDiff.normalize();
        float angleBetween = currentFrontVec.angleBetween(waypointDiff);
        if (angleBetween < WAYPOINT_TARGETING_ANGLE) {
            // Launch countermeasures if close enough
            checkShouldLaunchCountermeasures(e, closestProjectile, entityProperties, shipProperties);
            // Check if the next waypoints are somewhat in front and can go full speed through them.
            ArrayList<ENG_Integer> waypointChainInFront = new ArrayList<>();
            float waypointChainInFrontTotalDistance = waypointSector.getWaypointChainInFront(targetWaypoint.getId(),
                    currentPos, currentFrontVec, EVASION_WAYPOINT_CHAIN_MAX_COUNT, EVASION_WAYPOINT_CHAIN_MAX_ANGLE, waypointChainInFront);
            // Make sure the current waypoint isn't also in the front waypoint chain.
            for (Iterator<ENG_Integer> iterator = waypointChainInFront.iterator(); iterator.hasNext(); ) {
                ENG_Integer waypointId = iterator.next();
                if (waypointId.getValue() == aiProperties.getCurrentWaypointId()) {
                    ENG_Log.getInstance().log("Removed front waypoint chain current waypoint id: " + aiProperties.getCurrentWaypointId());
                    iterator.remove();
                    break;
                }
            }

            if (!waypointChainInFront.isEmpty()) {
                if (waypointChainInFrontTotalDistance > 2000.0f) {
                    accelerateToMaxSpeed(shipProperties, entityProperties, aiProperties,
                            VELOCITY_CHANGE_NAME_EVADE_MISSILE, EVASION_MISSILE_SPEED_CHANGE_STEP);
                } else {
                    float speed = 100.0f;
                    if (speed > entityProperties.getVelocity()) {
                        changeVelocity(entityProperties, shipProperties, aiProperties,
                                new VelocityChange(
                                        VELOCITY_CHANGE_NAME_EVADE_MISSILE,
                                        speed, 0.1f));
                    }
                }
                aiProperties.setWaypointChainToEvasionWaypoint(waypointChainInFront);
                aiProperties.setWaypointChainToEvasionWaypointCurrentIndex(0);
                ENG_Log.getInstance().log("Evading towards front waypoints. Count: " + waypointChainInFront.size());
            } else {
                evadeToClosestCorner(entityProperties, aiProperties, closestProjectile, waypointSector, targetWaypoint);
            }
        } else {
            ENG_Log.getInstance().log("angleBetween > WAYPOINT_TARGETING_ANGLE: " +
                    angleBetween * ENG_Math.RADIANS_TO_DEGREES + " > " +
                    WAYPOINT_TARGETING_ANGLE * ENG_Math.RADIANS_TO_DEGREES);
        }
    }

    private void evadeToClosestCorner(EntityProperties entityProperties, AIProperties aiProperties, ClosestProjectile closestProjectile,
                                      WaypointSector waypointSector, Waypoint targetWaypoint) {
        // Check if we can fool the missile by going after a nearby corner instead of just relying on speed.
        // Check from which direction the missile is incoming.
        EntityProperties projectileEntityProperties = entityPropertiesMapper.get(closestProjectile.minDistProjectile);
        ENG_Vector4D projDiff = projectileEntityProperties.getNode().getPosition().subAsVec(currentPos);
        projDiff.normalize();
        projDiff.invertInPlace();
        // Find a nearby waypoint that is around 90 degrees around the target waypoint.
        // Get the closest waypoints based on radius and find which ones are closest to 90 degrees
        // around the ship front direction.
        ArrayList<Waypoint> closestWaypoints = waypointSector.getClosestWaypoint(targetWaypoint.getPosition(),
                500.0f, new int[] { aiProperties.getCurrentWaypointId(), targetWaypoint.getId() });
        float closestRightAngle = WAYPOINT_EVASION_MAX_ANGLE - ENG_Math.HALF_PI;
        float closestDistance = Float.POSITIVE_INFINITY;
        Waypoint waypointToEvadeTo = null;
        for (Waypoint closestWaypoint : closestWaypoints) {
            ENG_Vector4D diff = closestWaypoint.getPosition().subAsVec(targetWaypoint.getPosition());
            diff.normalize();
            float angleBetweenProjectileFrontAndWaypointDiff = projDiff.angleBetween(diff);
            if (angleBetweenProjectileFrontAndWaypointDiff >= WAYPOINT_EVASION_MIN_ANGLE &&
                    angleBetweenProjectileFrontAndWaypointDiff <= WAYPOINT_EVASION_MAX_ANGLE) {
                float abs = Math.abs(angleBetweenProjectileFrontAndWaypointDiff - ENG_Math.HALF_PI);
                if (abs < closestRightAngle) {
                    closestRightAngle = abs;
                    float distanceBetweenWaypoints = closestWaypoint.getPosition().distance(targetWaypoint.getPosition());
                    if (distanceBetweenWaypoints < closestDistance) {
                        closestDistance = distanceBetweenWaypoints;
                        waypointToEvadeTo = closestWaypoint;
                    }
                }
            }
        }
        if (waypointToEvadeTo != null) {
            // Get the path from the target waypoint to the chosen evasion waypoint.
            ArrayList<ENG_Integer> waypointChainToEvasionWaypoint = new ArrayList<>();
            if (targetWaypoint.getId() == waypointToEvadeTo.getId()) {
                waypointChainToEvasionWaypoint.add(new ENG_Integer(waypointToEvadeTo.getId()));
            } else {
                waypointSector.getWaypointChainFromPosition(
                        targetWaypoint.getPosition(), waypointToEvadeTo.getPosition(),
                        false, false, waypointChainToEvasionWaypoint);
            }
            if (!waypointChainToEvasionWaypoint.isEmpty() &&
                    waypointChainToEvasionWaypoint.size() <= EVASION_WAYPOINT_CHAIN_MAX_SIZE) {
                aiProperties.setWaypointChainToEvasionWaypoint(waypointChainToEvasionWaypoint);
                aiProperties.setWaypointChainToEvasionWaypointCurrentIndex(0);
                setNextWaypointId(entityProperties, aiProperties, AIProperties.AIWaypointState.MOVING_TO_WAYPOINT_DESTINATION,
                        waypointSector.getId(),
                        aiProperties.getCurrentWaypointId(), waypointChainToEvasionWaypoint.get(0).getValue());
                ENG_Log.getInstance().log("Evading from waypoint: " + aiProperties.getCurrentWaypointId() +
                        " to target corner waypoint: " + waypointChainToEvasionWaypoint.get(0).getValue());
            }
        }
    }

    private int moveToClosestEntranceSpawnedInside(EntityProperties entityProperties,
                                                   AIProperties aiProperties, int currentWaypointId,
                                                   WaypointSystem waypointSystem, int currentPosWaypointSectorId) {
        int nextWaypointId;
        moveToClosestEntrance(entityProperties, aiProperties, currentWaypointId, AIProperties.AIWaypointState.MOVING_TO_EXIT);
        nextWaypointId = waypointSystem.getWaypointSector(currentPosWaypointSectorId).getClosestWaypoint(currentPos).getId();
        return nextWaypointId;
    }

    private void moveToClosestEntrance(EntityProperties entityProperties, AIProperties aiProperties,
                                       int currentWaypointId, AIProperties.AIWaypointState aiWaypointState) {
        WaypointSystem waypointSystem = WaypointSystem.getSingleton();
        // We are guaranteed to be outside of any sector so we might just reset the last sector.
        aiProperties.setLastWaypointSectorId(0);
        ArrayList<WaypointSectorIdWithPosition> hitWaypointSectors = getWaypointSectorIdWithPositionsFromCurrentPosToDestination();
        if (hitWaypointSectors.isEmpty()) {
            if (WaypointSystem.DEBUG) {
                ENG_Log.getInstance().log("moveToClosestEntrance() no intersecting waypoint sectors between AI and target");
            }
            updateWaypointUserCount(entityProperties.getEntityId(), aiProperties.getCurrentWaypointSectorId(),
                    aiProperties.getCurrentTargetWaypointId(), 0, 0);
            aiProperties.resetWaypointData();
        }
        for (WaypointSectorIdWithPosition hitWaypointSector : hitWaypointSectors) {
            WaypointSector waypointSector = waypointSystem.getWaypointSector(hitWaypointSector.waypointSectorId);
            Waypoint closestWaypoint = waypointSector.getClosestWaypoint(hitWaypointSector.position, true);
            if (closestWaypoint == null) {
                // Should never get here.
                throw new IllegalStateException("waypointSector Id: " +
                        hitWaypointSector.waypointSectorId +
                        " cannot get the entrance closest to: " + hitWaypointSector.position);
            }
            // Go to the entrance. It might make sense to avoid the sector altogether.
            // For example, the entrance might be on the path to target but the exit is far away,
            // thus positioning the ship at a larger distance from the target than if you avoided the sector.
            ArrayList<Waypoint> entranceOrExitWaypoints = waypointSector.getEntranceOrExitWaypoints();
            // Determine which would be the entrance waypoints and which would be the exit for this case.
            // In the level design the entrance and exit waypoints must be grouped together
            // so the algorithm can determine which ones are which. Clump them together level designer!
            // At least make sure the entrances/exits waypoint ids are clumped together. 1, 2, 3 entrance
            // 7, 8, 9 exit ids.
            if (entranceOrExitWaypoints.size() < 2) {
                // Should never get here.
                throw new IllegalStateException("entranceOrExitWaypoints size: " + entranceOrExitWaypoints.size());
            } else if (entranceOrExitWaypoints.size() == 2) {
                // The closer one is the entrance the other one is the exit.
                Waypoint firstWaypoint = entranceOrExitWaypoints.get(0);
                Waypoint secondWaypoint = entranceOrExitWaypoints.get(1);
                float firstDistance = firstWaypoint.getPosition().distance(currentPos);
                float secondDistance = secondWaypoint.getPosition().distance(currentPos);
                if (firstDistance < secondDistance) {
                    setWaypointIdForEntranceAndExit(aiProperties, aiWaypointState,
                            waypointSector.getId(), firstWaypoint.getId(), secondWaypoint.getId());
                } else {
                    setWaypointIdForEntranceAndExit(aiProperties, aiWaypointState,
                            waypointSector.getId(), secondWaypoint.getId(), firstWaypoint.getId());
                }
                setLastPositions(aiProperties);
            } else { // entranceOrExitWaypoints.size() > 2
                boolean entranceAndExitIdentified = false;
                ArrayList<WaypointIdsWithDistances> waypointIdsWithDistances = getWaypointIdsWithDistances(entranceOrExitWaypoints);
                ArrayList<WaypointGroup> waypointGroups = getWaypointGroups(waypointIdsWithDistances);

                if (waypointGroups.size() >= 2) {
                    entranceAndExitIdentified = true;
                    // Get the closest entrance and closest exit to the target.
                    ClosestWaypointsEntranceExit closest = getClosestWaypointsEntranceExit(waypointGroups, waypointSector);
                    checkClosestNotNull(closest);
                    addEntranceAndExitToAiProperties(closest, waypointSector, aiProperties, aiWaypointState);
                }

                if (!entranceAndExitIdentified) {
                    setEntranceAndExitBasedOnNonConsecutiveWaypointIds(aiProperties, entranceOrExitWaypoints,
                            currentWaypointId, waypointSector, aiWaypointState);
                }
//                                    float waypointDiffDistance = Float.POSITIVE_INFINITY;
//                                    ENG_AxisAlignedBox boxBeforeMerge = new ENG_AxisAlignedBox();
//                                    ENG_AxisAlignedBox box = new ENG_AxisAlignedBox();
//                                    for (int i = 1; i < entranceOrExitWaypoints.size(); ++i) {
//                                        float minWaypointDiffDistance = entranceOrExitWaypoints.get(i - 1).getPosition().distance(entranceOrExitWaypoints.get(i).getPosition());
//
//                                        box.merge(entranceOrExitWaypoints.get(i).getPosition());
//                                    }
            }

            // Go to the exit.
            // Repeat with the next intermediate waypoint sectors.
        }
    }

    private static ArrayList<WaypointIdsWithDistances> getWaypointIdsWithDistances(ArrayList<Waypoint> entranceOrExitWaypoints) {
        ArrayList<WaypointIdsWithDistances> waypointIdsWithDistances = new ArrayList<>();
        for (int i = 0; i < entranceOrExitWaypoints.size() - 1; ++i) {
            for (int j = i + 1; j < entranceOrExitWaypoints.size(); ++j) {
                Waypoint waypoint0 = entranceOrExitWaypoints.get(i);
                Waypoint waypoint1 = entranceOrExitWaypoints.get(j);
                float distanceBetween = waypoint0.getPosition().distance(waypoint1.getPosition());
                waypointIdsWithDistances.add(new WaypointIdsWithDistances(
                        new ENG_Integer(waypoint0.getId()),
                        new ENG_Integer(waypoint1.getId()),
                        distanceBetween));
            }
        }

        Collections.sort(waypointIdsWithDistances);
        if (WaypointSystem.DEBUG) {
            ENG_Log.getInstance().log("Printing waypointIdsWithDistance:");
            for (WaypointIdsWithDistances waypointIdsWithDistance : waypointIdsWithDistances) {
                ENG_Log.getInstance().log(waypointIdsWithDistance.toString());
            }
        }
        return waypointIdsWithDistances;
    }

    private static ArrayList<WaypointGroup> getWaypointGroups(ArrayList<WaypointIdsWithDistances> waypointIdsWithDistances) {
        boolean largeGroupSide = false;
        float smallDistance = 200.0f;
        int currentGroupId = 1;
        ArrayList<WaypointGroup> waypointGroups = new ArrayList<>();
        for (int i = 0, currentGroupIndex = 0, waypointIdsWithDistancesSize = waypointIdsWithDistances.size(); i < waypointIdsWithDistancesSize; i++) {
            WaypointIdsWithDistances waypointIdsWithDistance = waypointIdsWithDistances.get(i);
            if (waypointIdsWithDistance.distanceBetween < smallDistance) {
                // We are in the small sorted distance. We can find the groups here.
                if (waypointGroups.isEmpty()) {
                    currentGroupId = addWaypointGroup(currentGroupId, waypointIdsWithDistance, waypointGroups);
                } else {
                    if (!waypointGroups.get(currentGroupIndex).addWaypointIds(waypointIdsWithDistance)) {
                        currentGroupId = addWaypointGroup(currentGroupId, waypointIdsWithDistance, waypointGroups);
                        ++currentGroupIndex;
                    }
                }
            } else {
                // If the distance is a little larger than the current smallDistance
                // we can increment the smallDistance and try again instead of considering
                // everything to be in the large distance group with single waypoints groups.
                if (!largeGroupSide ) {
                    if (waypointIdsWithDistance.distanceBetween < smallDistance * 1.5f) {
                        --i;
                        smallDistance *= 2.0f;
                        continue;
                    } else {
                        largeGroupSide = true;
                    }
                } else {
                    // Unique ids mean a new group of single waypoint.
                    if (!containsWaypointId(waypointGroups, waypointIdsWithDistance.firstWaypointId)) {
                        currentGroupId = addWaypointGroup(currentGroupId, waypointIdsWithDistance.firstWaypointId, waypointGroups);
                    }
                    if (!containsWaypointId(waypointGroups, waypointIdsWithDistance.secondWaypointId)) {
                        currentGroupId = addWaypointGroup(currentGroupId, waypointIdsWithDistance.secondWaypointId, waypointGroups);
                    }
                }
            }
        }
        if (WaypointSystem.DEBUG) {
            if (waypointGroups.isEmpty()) {
                throw new IllegalStateException("waypointGroups is empty");
            }
        }
        return waypointGroups;
    }

    private void setEntranceAndExitBasedOnNonConsecutiveWaypointIds(AIProperties aiProperties,
                                                                    ArrayList<Waypoint> entranceOrExitWaypoints,
                                                                    int currentWaypointId,
                                                                    WaypointSector waypointSector,
                                                                    AIProperties.AIWaypointState aiWaypointState) {
        // Fuck it, for now just use id chains to determine if it's part of entrance/exit.
        ArrayList<Waypoint> chains = new ArrayList<>(entranceOrExitWaypoints);
        chains.sort((o1, o2) -> {
            return o1.getId() - o2.getId();
        });
        int gapPosition = getGapPosition(currentWaypointId, chains);
        // Get the 2 chains and choose which one is for entrance and which is for exit.
        // Then rerun the same algorithm as above with the WaypointGroup.
        WaypointGroup entranceWaypointGroup = new WaypointGroup(1);
        WaypointGroup exitWaypointGroup = new WaypointGroup(2);
        for (int i = 0; i < gapPosition; ++i) {
            // Avoid addWaypointId() since it checks to only allow one id to be added.
            entranceWaypointGroup.waypointIds.add(new ENG_Integer(chains.get(i).getId()));
        }
        for (int i = gapPosition; i < chains.size(); ++i) {
            // Avoid addWaypointId() since it checks to only allow one id to be added.
            exitWaypointGroup.waypointIds.add(new ENG_Integer(chains.get(i).getId()));
        }
        ArrayList<WaypointGroup> gappedWaypointGroups = new ArrayList<>();
        gappedWaypointGroups.add(entranceWaypointGroup);
        gappedWaypointGroups.add(exitWaypointGroup);
        ClosestWaypointsEntranceExit closest = getClosestWaypointsEntranceExit(gappedWaypointGroups, waypointSector);
        checkClosestNotNull(closest);
        addEntranceAndExitToAiProperties(closest, waypointSector, aiProperties, aiWaypointState);
    }

    private static int getGapPosition(int currentWaypointId, ArrayList<Waypoint> chains) {
        int waypointId = chains.get(0).getId();
        int gapPosition = 0;
        for (int i = 1; i < chains.size(); ++i) {
            int currentWaypointChainId = chains.get(i).getId();
            if (currentWaypointChainId - waypointId > 1) {
                gapPosition = i;
                // This wrongly assumes that there is only an entrance and an exit.
                // There could be multiple gaps as there could be 2 exits.
                // One closer to the target one farther, for example.
                break;
            } else {
                waypointId = currentWaypointId;
            }
        }
        if (gapPosition == 0) {
            // Should never get here.
            throw new IllegalStateException("There is no id gap between entrance and exit waypoints");
        }
        return gapPosition;
    }

    private ArrayList<WaypointSectorIdWithPosition> getWaypointSectorIdWithPositionsFromCurrentPosToDestination() {
        WaypointSystem waypointSystem = WaypointSystem.getSingleton();
        ENG_Vector4D distVec = otherPos.subAsPt(currentPos);
        float distLen = distVec.length();
        int segments = 30;
        if (distLen > 5000.0f) {
            segments = 60;
        }
        float step = distLen / segments;
        ENG_Vector4D unitDist = distVec.normalizedCopy();
        ArrayList<WaypointSectorIdWithPosition> hitWaypointSectors = new ArrayList<>();
        for (int i = 1; i < segments; ++i) {
            ENG_Vector4D ptOnRay = currentPos.addAsPt(unitDist.mulAsPt(i * step));
            int waypointSectorId = waypointSystem.checkPositionInWaypointSector(ptOnRay);
            if (waypointSectorId != -1) {
                hitWaypointSectors.add(new WaypointSectorIdWithPosition(new ENG_Integer(waypointSectorId), ptOnRay));
            }
        }
        return hitWaypointSectors;
    }

    private static Waypoint getNextWaypoint(AIProperties aiProperties) {
        int waypointId = getNextWaypointId(aiProperties);
        Waypoint waypoint = getWaypoint(aiProperties.getCurrentWaypointSectorId(), waypointId);
        if (WaypointSystem.DEBUG) {
            if (waypoint == null) {
                // Should never get here.
                throw new IllegalStateException("waypointSectorId: " + aiProperties.getCurrentWaypointSectorId() + " waypointId: " + waypointId + " are invalid!");
            }
        }
        return waypoint;
    }

    private static int getNextWaypointId(AIProperties aiProperties) {
        int waypointId = 0;
        switch (aiProperties.getWaypointState()) {
            case MOVING_TO_ENTRANCE:
                waypointId = aiProperties.getEntranceWaypointId();
                break;
            case MOVING_TO_EXIT:
                waypointId = aiProperties.getExitWaypointId();
                break;
            case MOVING_TO_WAYPOINT_DESTINATION:
                waypointId = aiProperties.getCurrentTargetWaypointId();
                break;
            case NONE:
            default:
                throw new IllegalStateException("Unexpected value: " + aiProperties.getWaypointState());
        }
        return waypointId;
    }

    private static Waypoint getWaypoint(int waypointSectorId, int waypointId) {
        if (WaypointSystem.DEBUG) {
            if (waypointSectorId < 1 || waypointId < 1) {
                throw new IllegalArgumentException("waypointSectorId: " + waypointSectorId + " waypointId: " + waypointId);
            }
        }
        return WaypointSystem.getSingleton().getWaypointSector(waypointSectorId).getWaypoint(waypointId);
    }

    private boolean limitHowManyProjectilesAreLaunchedTowardsEnemyShip(AIProperties aiProperties,
                                                                       EntityProperties entityProperties,
                                                                       ShipProperties shipProperties) {
        if (aiProperties.getCurrentShootingAtShip() != -1 &&
                aiProperties.getCurrentShootingAtShip() == aiProperties.getFollowedShip()) {
            if (aiProperties.isLimitProjectilesLaunched()) {
                if (ENG_Utility.hasTimePassed(FrameInterval.MAX_PROJECTILES_LAUNCHED_DELAY + entityProperties.getNode().getName(),
                        aiProperties.getLimitProjectilesLaunchedStartTime(),
                        MAX_PROJECTILES_LAUNCHED_DELAY)) {
                    aiProperties.setLimitProjectilesLaunched(false);
                    aiProperties.setCurrentLaunchedProjectiles(0);
                    // System.out.println(shipProperties.getName() +
                    // " max projectiles delay expired");
                } else {
                    aiProperties.setState(AIState.FOLLOW_PLAYER);
                    showAIStateChange(shipProperties, aiProperties);
                    return true;
                }
            }
            if (aiProperties.getCurrentLaunchedProjectiles() < MAX_CHASING_PROJECTILES_NUM) {
                // May be decremented when we realize we are too far
                // away and don't shoot
                aiProperties.incrementCurrentLaunchedProjectiles();
                // System.out.println(shipProperties.getName() +
                // " incrementing projectiles to " +
                // aiProperties.getCurrentLaunchedProjectiles());
            } else {
                aiProperties.setLimitProjectilesLaunched(true);
                aiProperties.setLimitProjectilesLaunchedStartTime();
                // System.out.println(shipProperties.getName() +
                // " max projectiles limit reached");
            }

        } else {
            aiProperties.setCurrentShootingAtShip(aiProperties.getFollowedShip());
            aiProperties.setCurrentLaunchedProjectiles(1);
            aiProperties.setLimitProjectilesLaunched(false);
        }
        return false;
    }

    private ShipProperties checkIfFollowedEntityIsAShip(Entity followedShip) {
        ShipProperties followedShipShipProperties = shipPropertiesMapper.getSafe(followedShip);
        if (followedShipShipProperties == null) {
            throw new ENG_InvalidFieldStateException(entityPropertiesMapper.get(followedShip).getItem().getName() + " is not a ship");
        }
        return followedShipShipProperties;
    }

    private boolean checkIfTooManyChasingProjectiles(ShipProperties followedShipShipProperties,
                                                     AIProperties aiProperties,
                                                     ShipProperties shipProperties) {
        if (followedShipShipProperties.getChasingProjectilesNum() > MAX_CHASING_PROJECTILES_NUM) {
            aiProperties.setEnemySelected(false);
            aiProperties.setState(AIState.SEEK_CLOSEST_PLAYER);
            showAIStateChange(shipProperties, aiProperties);
            return true;
        }
        return false;
    }

    private static Entity checkIfFollowedShipIsDestroyed(AIProperties aiProperties) {
        Entity followedShip = MainApp.getGame().getWorldManager().getEntityByItemId(aiProperties.getFollowedShip());
        if (followedShip == null) {
            aiProperties.setEnemySelected(false);
            aiProperties.setState(AIState.SEEK_CLOSEST_PLAYER);
            return null;
        }
        return followedShip;
    }

    private boolean checkIsChased(ShipProperties shipProperties, AIProperties aiProperties) {
        if (shipProperties.isChased()) {
            aiProperties.setState(AIState.EVADE_MISSILE);
            showAIStateChange(shipProperties, aiProperties);
            return true;
        }
        return false;
    }

    private boolean evadeCollision(AIProperties aiProperties, EntityProperties entityProperties, ShipProperties shipProperties) {
        if (aiProperties.isEvadingCollision()) {
            if (ENG_Utility.hasTimePassed(FrameInterval.EVADE_COLLISION_TIME + entityProperties.getNode().getName(),
                    aiProperties.getEvadingCollisionTimeStarted(),
                    EVADE_COLLISION_TIME)) {
                if (DEBUG) {
                    ENG_Log.getInstance().log("entity: " + entityProperties.getName() + " Evading collision stopped " + ENG_Utility.currentTimeMillis());
                }
                aiProperties.setEvadingCollision(false);
            } else {
                // Accelerate to max speed to evade the followed
                // ship
                changeVelocity(entityProperties, shipProperties, aiProperties,
                        new VelocityChange(
                                VELOCITY_CHANGE_NAME_ACCELERATE_TO_MAX_SPEED,
                                shipProperties.getShipData().maxSpeed,
                                EVADING_COLLISION_VELOCITY_CHANGE_RATE));

                // Rotate to evade collision with followed ship
                entityProperties.getNode().getLocalInverseZAxis(currentFrontVec);
//                                ENG_Math.rotateAwayFromPositionDeg(otherPos, currentPos, currentFrontVec, getRotationAngle(shipProperties), rotation);
//                                entityProperties.rotate(rotation, true, TransformSpace.TS_WORLD);
                if (DEBUG || WaypointSystem.DEBUG) {
                    ENG_Log.getInstance().log("entity: " + entityProperties.getName() + " Evading collision " + ENG_Utility.currentTimeMillis());
                }
                Utility.rotateAwayFromPosition(currentFrontVec, otherPos, updateInterval, entityProperties,
                        shipProperties.getShipData().maxAngularVelocity);
                return true;
            }
        }
        return false;
    }

    private void checkOnCollisionCourse(float squaredDist, EntityProperties otherShipEntityProperties,
                                        AIProperties aiProperties, EntityProperties entityProperties) {
        if (squaredDist < TARGET_MIN_DISTANCE && otherShipEntityProperties.getVelocity() > 0.0f &&
                !aiProperties.isEvadingCollision()) {
            entityProperties.getNode().getLocalInverseZAxis(currentFrontVec);
            otherShipEntityProperties.getNode().getLocalInverseZAxis(otherFrontVec);

            float angleBetween = currentFrontVec.angleBetween(otherFrontVec);
            ENG_Vector4D enemyDist = distVec.normalizedCopy();
            float angleBetweenFrontAndEnemy = currentFrontVec.angleBetween(enemyDist);

            if (DEBUG) {
                ENG_Log.getInstance().log("entity: " + entityProperties.getName() +
                        " collision evasion angle: " + (angleBetween * ENG_Math.RADIANS_TO_DEGREES) +
                        " currentTimeMillis: " + ENG_Utility.currentTimeMillis());
            }

            if (angleBetweenFrontAndEnemy < COLLISION_EVASION_FRONT_ANGLE && angleBetween > COLLISION_EVASION_ANGLE) {
                if (DEBUG || WaypointSystem.DEBUG) {
                    ENG_Log.getInstance().log("entity: " + entityProperties.getName() + " Evading collision start " + ENG_Utility.currentTimeMillis());
                }
                aiProperties.setEvadingCollision(true);
                aiProperties.setEvadingCollisionTimeStarted();
            }
        }
    }

    public static boolean isFollowingWaypoint(AIProperties aiProperties) {
        return aiProperties.getWaypointState() != AIProperties.AIWaypointState.NONE;
        // return aiProperties.getEntranceWaypointId() != 0 || aiProperties.getExitWaypointId() != 0 || aiProperties.getCurrentWaypointIdTarget();
    }

    public static boolean isInsideWaypointSector(AIProperties aiProperties) {
        return aiProperties.getWaypointState() == AIProperties.AIWaypointState.MOVING_TO_WAYPOINT_DESTINATION ||
                aiProperties.getWaypointState() == AIProperties.AIWaypointState.MOVING_TO_EXIT;
    }

    private void addEntranceAndExitToAiProperties(ClosestWaypointsEntranceExit closest, WaypointSector waypointSector,
                                                  AIProperties aiProperties, AIProperties.AIWaypointState aiWaypointState) {
        if (closest.closestEntrance == closest.closestExitToTarget) {
            // We can simply avoid the whole sector then.
            ENG_Integer closestWaypointIdToEntrance = getClosestWaypoint(closest.closestEntrance, waypointSector, currentPos);
            if (WaypointSystem.DEBUG) {
                if (closestWaypointIdToEntrance == null) {
                    throw new IllegalStateException("closestWaypointIdToEntrance == null");
                }
            }
            // Save the entrance waypoint id twice in the ai properties.
            // They will be used for the next frames until the target position changes too much.
            // If the waypoint is the same it will be determined when it will be reached by the AI ship.
            setWaypointIdForEntranceAndExit(aiProperties, aiWaypointState, waypointSector.getId(),
                    closestWaypointIdToEntrance.getValue(), closestWaypointIdToEntrance.getValue());
            setLastPositions(aiProperties);
        } else {
            // We have the entrance and the exit for the AI ship.
            // Find the closest waypoint for the entrance for the AI ship.
            // Find the closest to target waypoint for exit.
            ENG_Integer closestWaypointIdToEntrance = getClosestWaypoint(closest.closestEntrance, waypointSector, currentPos);
            ENG_Integer closestExitToTargetWaypoint = getClosestWaypoint(closest.closestExitToTarget, waypointSector, otherPos);
            if (WaypointSystem.DEBUG) {
                if (closestWaypointIdToEntrance == null) {
                    throw new IllegalStateException("closestWaypointIdToEntrance == null");
                }
                if (closestExitToTargetWaypoint == null) {
                    throw new IllegalStateException("closestExitToTargetWaypoint == null");
                }
            }
            // Save the entrance and exit waypoint ids in the ai properties.
            // They will be used for the next frames until the target position changes too much.
            setWaypointIdForEntranceAndExit(aiProperties, aiWaypointState, waypointSector.getId(),
                    closestWaypointIdToEntrance.getValue(), closestExitToTargetWaypoint.getValue());
            setLastPositions(aiProperties);
        }
    }

    private void setLastPositions(AIProperties aiProperties) {
        aiProperties.setLastPosition(currentPos);
        aiProperties.setLastFollowedShipPosition(otherPos);
    }

    private static void setNextWaypointId(EntityProperties entityProperties, AIProperties aiProperties,
                                          AIProperties.AIWaypointState state,
                                          int waypointSectorId, int currentWaypointId, int targetWaypointId) {
        int previousWaypointSectorId = aiProperties.getCurrentWaypointSectorId();
        int previousWaypointId = aiProperties.getCurrentTargetWaypointId();
        aiProperties.setWaypointState(state);
        aiProperties.setCurrentWaypointSectorId(waypointSectorId);
        aiProperties.setCurrentWaypointId(currentWaypointId);
        aiProperties.setCurrentTargetWaypointId(targetWaypointId);
        updateWaypointUserCount(entityProperties.getEntityId(),
                previousWaypointSectorId, previousWaypointId, waypointSectorId, targetWaypointId);
        ENG_Log.getInstance().log(entityProperties.getName() + " waypointState: " + state +
                " currentWaypointId: " + currentWaypointId +
                " targetWaypointId: " + targetWaypointId, ENG_Log.TYPE_MESSAGE);
        if (WaypointSystem.DEBUG) {
            if (currentWaypointId == targetWaypointId) {
                ENG_Log.getInstance().log("currentWaypointId == targetWaypointId " + currentWaypointId);
            }
            if (currentWaypointId == 0) {
                ENG_Log.getInstance().log("currentWaypointId SET TO 0!");
            }
        }
    }

    private static void updateWaypointUserCount(long userId, int previousWaypointSectorId, int previousWaypointId,
                                                int waypointSectorId, int waypointId) {
        WaypointSystem waypointSystem = WaypointSystem.getSingleton();
        if (previousWaypointSectorId != 0 && previousWaypointId != 0) {
            waypointSystem.getWaypointSector(previousWaypointSectorId).getWaypoint(previousWaypointId).removeWaypointUser(userId);
        }
        if (waypointSectorId != 0 && waypointId != 0) {
            WaypointSector waypointSector = waypointSystem.getWaypointSector(waypointSectorId);
            boolean canAddUserToWaypoint = waypointSector.canAddUserToWaypoint();
            if (/*canAddUserToWaypoint*/true) {
                waypointSector.getWaypoint(waypointId).addWaypointUser(userId);
            }
        }
    }

    private static void updateWaypointUserCount(ENG_Long userId, ENG_Integer previousWaypointSectorId, ENG_Integer previousWaypointId,
                                                ENG_Integer waypointSectorId, ENG_Integer waypointId) {
        WaypointSystem waypointSystem = WaypointSystem.getSingleton();
        if (previousWaypointSectorId.getValue() != 0 && previousWaypointId.getValue() != 0) {
            waypointSystem.getWaypointSector(previousWaypointSectorId).getWaypoint(previousWaypointId).removeWaypointUser(userId);
        }
        if (waypointSectorId.getValue() != 0 && waypointId.getValue() != 0) {
            WaypointSector waypointSector = waypointSystem.getWaypointSector(waypointSectorId);
            boolean canAddUserToWaypoint = waypointSector.canAddUserToWaypoint();
            if (/*canAddUserToWaypoint*/true) {
                waypointSector.getWaypoint(waypointId).addWaypointUser(userId);
            }
        }
    }

    private static void setWaypointIdForEntranceAndExit(AIProperties aiProperties, AIProperties.AIWaypointState state,
                                                        int waypointSectorId, int closestWaypointIdToEntrance,
                                                        int closestExitToTargetWaypoint) {
        aiProperties.setWaypointState(state);
        aiProperties.setCurrentWaypointSectorId(waypointSectorId);
        aiProperties.setEntranceWaypointId(closestWaypointIdToEntrance);
        aiProperties.setExitWaypointId(closestExitToTargetWaypoint);
    }

    private static void checkClosestNotNull(ClosestWaypointsEntranceExit closest) {
        if (WaypointSystem.DEBUG) {
            if (closest.closestEntrance == null) {
                throw new IllegalStateException("closestEntrance == null");
            }
            if (closest.closestExitToTarget == null) {
                throw new IllegalStateException("closestExitToTarget == null");
            }
        }
    }

    private ClosestWaypointsEntranceExit getClosestWaypointsEntranceExit(ArrayList<WaypointGroup> waypointGroups,
                                                                         WaypointSector waypointSector) {
        WaypointGroup closestEntrance = null;
        WaypointGroup closestExitToTarget = null;
        float closestEntranceDistance = Float.POSITIVE_INFINITY;
        float closestExitToTargetDistance = Float.POSITIVE_INFINITY;
        for (WaypointGroup waypointGroup : waypointGroups) {
            if (WaypointSystem.DEBUG) {
                if (waypointGroup.waypointIds.isEmpty()) {
                    throw new IllegalStateException("waypointGroup waypointIds is empty");
                }
            }
            if (waypointGroup.waypointIds.size() == 1) {
                ENG_Integer waypointId = waypointGroup.waypointIds.get(0);
                Waypoint waypoint = waypointSector.getWaypoint(waypointId);
                float closestEntranceDiff = currentPos.distance(waypoint.getPosition());
                if (closestEntranceDiff < closestEntranceDistance) {
                    closestEntranceDistance = closestEntranceDiff;
                    closestEntrance = waypointGroup;
                }
                float closestExitToTargetDiff = otherPos.distance(waypoint.getPosition());
                if (closestExitToTargetDiff < closestExitToTargetDistance) {
                    closestExitToTargetDistance = closestExitToTargetDiff;
                    closestExitToTarget = waypointGroup;
                }
            } else if (waypointGroup.waypointIds.size() >= 2) {
                ENG_AxisAlignedBox box = new ENG_AxisAlignedBox();
                ArrayList<ENG_Integer> waypointIds = waypointGroup.waypointIds;
                for (int i = 0, waypointIdsSize = waypointIds.size(); i < waypointIdsSize; i++) {
                    ENG_Integer waypointId = waypointIds.get(i);
                    box.merge(waypointSector.getWaypoint(waypointId).getPosition());
                }
                ENG_Vector4D center = box.getCenter();
                if (WaypointSystem.DEBUG) {
                    ENG_Log.getInstance().log("getClosestWaypointsEntranceExit() waypointGroup: " +
                            waypointGroup.groupId + " box: " + box + " centre: " + center);
                }
                float closestEntranceDiff = currentPos.distance(center);
                if (closestEntranceDiff < closestEntranceDistance) {
                    closestEntranceDistance = closestEntranceDiff;
                    closestEntrance = waypointGroup;
                }
                float closestExitToTargetDiff = otherPos.distance(center);
                if (closestExitToTargetDiff < closestExitToTargetDistance) {
                    closestExitToTargetDistance = closestExitToTargetDiff;
                    closestExitToTarget = waypointGroup;
                }
            }
        }
        return new ClosestWaypointsEntranceExit(closestEntrance, closestExitToTarget);
    }

    private static class ClosestWaypointsEntranceExit {
        public final WaypointGroup closestEntrance;
        public final WaypointGroup closestExitToTarget;

        public ClosestWaypointsEntranceExit(WaypointGroup closestEntrance, WaypointGroup closestExitToTarget) {
            this.closestEntrance = closestEntrance;
            this.closestExitToTarget = closestExitToTarget;
        }
    }

    private ENG_Integer getClosestWaypoint(WaypointGroup closestEntrance, WaypointSector waypointSector, ENG_Vector4D shipPos) {
        float closest = Float.POSITIVE_INFINITY;
        ENG_Integer closestWaypointToEntrance = null;
        for (ENG_Integer waypointId : closestEntrance.waypointIds) {
            float distance = shipPos.distance(waypointSector.getWaypoint(waypointId).getPosition());
            if (distance < closest) {
                closest = distance;
                closestWaypointToEntrance = waypointId;
            }
        }
        return closestWaypointToEntrance;
    }

    private static boolean containsWaypointId(ArrayList<WaypointGroup> waypointGroups, ENG_Integer waypointId) {
        for (WaypointGroup waypointGroup : waypointGroups) {
            if (waypointGroup.waypointIds.contains(waypointId)) {
                return true;
            }
        }
        return false;
    }

    private static int addWaypointGroup(int currentGroupId, WaypointIdsWithDistances waypointIdsWithDistance,
                                        ArrayList<WaypointGroup> waypointGroups) {
        WaypointGroup waypointGroup = new WaypointGroup(currentGroupId++);
        waypointGroup.addWaypointIds(waypointIdsWithDistance);
        waypointGroups.add(waypointGroup);
        return currentGroupId;
    }

    private static int addWaypointGroup(int currentGroupId, ENG_Integer waypointId, ArrayList<WaypointGroup> waypointGroups) {
        WaypointGroup waypointGroup = new WaypointGroup(currentGroupId++);
        waypointGroup.addWaypointId(waypointId);
        waypointGroups.add(waypointGroup);
        return currentGroupId;
    }

    private final ArrayList<Long> closestEnemyIdsAlreadyTested = new ArrayList<>();

    /** @noinspection deprecation*/
    private void seekClosestPlayer(AIProperties aiProperties, EntityProperties entityProperties, ShipProperties shipProperties) {
        if (ENG_Utility.hasRandomChanceHit(FrameInterval.ENEMY_SEEK_RAND + entityProperties.getNode().getName(), ENEMY_SEEK_RAND)) {
            ImmutableBag<Entity> entities = null;
            boolean usePredeterminedEntityName = false;
            boolean entityToAttackFound = false;
            if (aiProperties.getAttackEntityName() != null) {
                Entity enemyEntity = WorldManager.getSingleton().getLevelObject(aiProperties.getAttackEntityName());
//                if (enemyEntity == null) {
//                    throw new IllegalStateException("attack entity: " + aiProperties.getAttackEntityName() + " is not a valid level object");
//                }
                if (enemyEntity != null) {
                    EntityProperties enemyEntityProperties = entityPropertiesMapper.get(enemyEntity);
                    if (enemyEntityProperties.isDestroyed()) {
                        // Just fallback to searching for the closest enemy player that is alive.
                        entities = GameWorld.getWorld().getManager(GroupManager.class).getEntities(
                                ShipTeam.getOtherTeamAsString(shipProperties.getShipData().team));
                        // No need to keep searching for this destroyed enemy every time we seek a new closest player to attack.
                        aiProperties.setAttackEntityName(null);
                    } else {
                        entities = new Bag<>(1);
                        ((Bag<Entity>) entities).add(enemyEntity);
                        usePredeterminedEntityName = true;
                        entityToAttackFound = true;
                    }
                } else {
                    // Disable further searches if something went wrong?
                    // No. As the object might appear later in the level.
//                    aiProperties.setAttackEntityName(null);
                }
            }
            if (aiProperties.getAttackEntityName() == null || !entityToAttackFound) {
                entities = GameWorld.getWorld().getManager(GroupManager.class).getEntities(
                        ShipTeam.getOtherTeamAsString(shipProperties.getShipData().team));
            }
            entityProperties.getNode().getPosition(currentPos);
            // Find smallest distance
            long closestEnemy = -1;
            int enemyTeamSize = entities.size();
            float currentMinLen = Float.MAX_VALUE;
            closestEnemyIdsAlreadyTested.clear();
            int currentTry = 0;
            while ((currentTry++) < 3) {
                for (int i = 0; i < enemyTeamSize; ++i) {
                    Entity entity = entities.get(i);
                    EntityProperties otherShipEntityProperties = entityPropertiesMapper.get(entity);
                    ShipProperties otherShipShipProperties = shipPropertiesMapper.getSafe(entity);

                    otherShipEntityProperties.getNode().getPosition(otherPos);
                    otherPos.sub(currentPos, distVec);

                    if (otherShipShipProperties != null && otherShipShipProperties.getShipData().shipType == ShipData.ShipType.CARGO) {
                        // Try to prioritize the enemy cargo ship.
                        if (closestEnemyIdsAlreadyTested.contains(otherShipEntityProperties.getItemId())) {
                            continue;
                        }
                        if (i == 0) {
                            currentMinLen = distVec.squaredLength();
                            closestEnemy = otherShipEntityProperties.getItemId();
                        } else {
                            if (distVec.squaredLength() < currentMinLen) {
                                currentMinLen = distVec.squaredLength();
                                closestEnemy = otherShipEntityProperties.getItemId();
                            }
                        }
                    }
                }
                if ((closestEnemy == -1) ||
                        (closestEnemy != -1 && !usePredeterminedEntityName &&
                                ENG_Utility.getRandom().nextInt(FrameInterval.CARGO_SHIP_SELECT_CLOSEST + entityProperties.getNode().getName(), 3) != 0)) {
                    for (int i = 0; i < enemyTeamSize; ++i) {
                        Entity entity = entities.get(i);
                        EntityProperties otherShipEntityProperties = entityPropertiesMapper.get(entity);
//                    ShipProperties otherShipShipProperties = shipPropertiesMapper.get(entity);

                        otherShipEntityProperties.getNode().getPosition(otherPos);
                        otherPos.sub(currentPos, distVec);

                        if (closestEnemyIdsAlreadyTested.contains(otherShipEntityProperties.getItemId())) {
                            continue;
                        }
                        if (i == 0) {
                            currentMinLen = distVec.squaredLength();
                            closestEnemy = otherShipEntityProperties.getItemId();
                        } else {
                            if (distVec.squaredLength() < currentMinLen) {
                                currentMinLen = distVec.squaredLength();
                                closestEnemy = otherShipEntityProperties.getItemId();
                            }
                        }
                    }
                }
                if (closestEnemy != -1) {
                    // Check if the enemy isn't already followed by enough friendly ships.
                    boolean enemyFollowable = true;
                    Entity closestEnemyShip = WorldManager.getSingleton().getEntityByItemId(closestEnemy);
                    if (closestEnemyShip != null) {
                        AIProperties closestEnemyAIProperties = aIPropertiesMapper.getSafe(closestEnemyShip);
                        if (closestEnemyAIProperties != null) {
                            if (closestEnemyAIProperties.getChasedByEnemyNum() > CHASING_SHIP_MAX_NUM) {
                                enemyFollowable = false;
                            }
                            if (isFollowingWaypoint(aiProperties) &&
                                    aiProperties.getCurrentWaypointSectorId() != 0 &&
                                    WaypointSystem.getSingleton().getWaypointSector(aiProperties.getCurrentWaypointSectorId())
                                            .isUserLimitReached()) {
                                enemyFollowable = false;
                            }
                        }
                    }
                    if (enemyFollowable) {
                        // Disable all patrolling data
                        aiProperties.setPatrolling(false);
                        aiProperties.setPatrollingRotationStarted(false);
                        aiProperties.setFollowedShip(closestEnemy);
                        aiProperties.setState(AIState.FOLLOW_PLAYER);
                        if (DEBUG) {
                            System.out.println(entityProperties.getName() + " changed state to FOLLOW_PLAYER. Following: " + closestEnemy);
                        }
                        break;
                    } else {
                        // Search for the next closest enemy.
                        closestEnemyIdsAlreadyTested.add(closestEnemy);
//                        if (DEBUG) {
                            Entity closestEnemyEntity = MainApp.getGame().getWorldManager().getEntityByItemId(closestEnemy);
                            EntityProperties closestEnemyEntityProperties = entityPropertiesMapper.get(closestEnemyEntity);
                            int followedCount = -1;
                            AIProperties closestEnemyAIProperties = aIPropertiesMapper.getSafe(closestEnemyEntity);
                            if (closestEnemyAIProperties != null) {
                                followedCount = closestEnemyAIProperties.getChasedByEnemyNum();
                            }
                            System.out.println(entityProperties.getName() + " couldn't follow: " + closestEnemyEntityProperties.getName() + " because it's already followed by: " + followedCount);
//                        }
                    }
                } else {
                    patrolTheArea(aiProperties, entityProperties, shipProperties);
                    break;
                }
            }
            if (currentTry > 3) {
                // All 3 closest enemies are followed by AI already to their limit.
//                if (DEBUG) {
                    System.out.println(entityProperties.getName() + " patrolling because the 3 closest enemies are maxed on followed number!");
//                }
                patrolTheArea(aiProperties, entityProperties, shipProperties);
            }
        }
    }

    private void patrolTheArea(AIProperties aiProperties, EntityProperties entityProperties, ShipProperties shipProperties) {
        // Slow down and patrol the area
        aiProperties.setPatrolling(true);
        changeVelocity(entityProperties, shipProperties, aiProperties, new VelocityChange(
                        VELOCITY_CHANGE_NAME_PATROL_SLOW_DOWN,
                        shipProperties.getShipData().maxSpeed/* * PATROL_SPEED_COEFFICIENT*/,
                        NO_TARGET_SPEED_CHANGE_STEP));
        if (!aiProperties.isPatrollingRotationStarted()) {
            if (ENG_Utility.hasRandomChanceHit(FrameInterval.NO_ENEMY_DIRECTION_CHANGE_CHANCE + entityProperties.getNode().getName(),
                            NO_ENEMY_DIRECTION_CHANGE_CHANCE)) {
                float dir = ENG_Utility.hasRandomChanceHit(
                        FrameInterval.NO_ENEMY_DIRECTION_CHANGE_CHANCE_DIR + entityProperties.getNode().getName(), 2) ? -1.0f : 1.0f;
                aiProperties.setPatrolAngleDirection(dir);
                int nextInt = ENG_Utility.getRandom().nextInt(FrameInterval.PATROL_AXIS + entityProperties.getNode().getName(), 3);
                switch (nextInt) {
                    case 0:
                        entityProperties.getNode().getLocalXAxis(patrolingRotationAxis);
                        break;
                    case 1:
                        entityProperties.getNode().getLocalYAxis(patrolingRotationAxis);
                        break;
                    case 2:
                        entityProperties.getNode().getLocalZAxis(patrolingRotationAxis);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                aiProperties.setPatrollingRotationAxis(patrolingRotationAxis);
                aiProperties.setPatrollingRotationTimeStarted();
                aiProperties.setPatrollingRotationStarted(true);
            }
        }

        if (aiProperties.isPatrollingRotationStarted()) {
            if (ENG_Utility.hasTimePassed(FrameInterval.PATROLING_ROTATION_TIME + entityProperties.getNode().getName(),
                    aiProperties.getPatrollingRotationTimeStarted(),
                    PATROLING_ROTATION_TIME)) {

                aiProperties.setPatrollingRotationStarted(false);
                aiProperties.setCurrentCollisionAngle(0.0f);
            } else {
                ENG_Vector4D torque = new ENG_Vector4D();

                aiProperties.getPatrollingRotationAxis().mulRet(aiProperties.getPatrolAngleDirection() * shipProperties.getShipData().maxAngularVelocity, torque);
                entityProperties.getRigidBody().applyTorqueImpulse(new Vector3(torque.x, torque.y, torque.z));
//                        float rotationAngle = getRotationAngle(shipProperties);
//                        ENG_Quaternion neworientation = ENG_Quaternion.fromAngleAxisDegRet(
//                                rotationAngle * aiProperties.getPatrolAngleDirection(),
//                                aiProperties.getPatrolingRotationAxis());
//                        ENG_Vector4D test = new ENG_Vector4D();
//                        float angleDeg = neworientation.toAngleAxisDeg(test);
//                        entityProperties.rotate(neworientation);
//                        aiProperties.setCurrentPatrolingRotationAngle(aiProperties.getCurrentPatrolingRotationAngle() + rotationAngle);
            }
        }
    }

    private void rayTestFrontVec(EntityProperties entityProperties, AIProperties aiProperties, float distanceAhead) {
        entityProperties.getNode().getLocalInverseZAxis(currentFrontVec);
        entityProperties.getNode().getPosition(currentPos);
        rayTo.set(currentFrontVec);
        rayTo.normalize();
        rayTo.mul(distanceAhead);
        rayTo.addInPlace(currentPos);
        WorldManagerBase.getSingleton().getDynamicWorld().rayTest(
                PhysicsUtility.toVector3(currentPos),
                PhysicsUtility.toVector3(rayTo),
                aiProperties.getRayResultCallback());
    }

    private void followPlayerShip(AIProperties aiProperties,
                                  EntityProperties entityProperties, ShipProperties shipProperties) {
        Entity playerShip = MainApp.getGame().getWorldManager().getPlayerShip();
        if (playerShip != null) {
            EntityProperties playerShipEntityProperties = entityPropertiesMapper.get(playerShip);
            if (!playerShipEntityProperties.isDestroyed()) {
                float scanRadius = shipProperties.getScanRadius();
                float distance = playerShipEntityProperties.getNode().getPosition().distance(entityProperties.getNode().getPosition());
                if (!aiProperties.isReloaderShouldLeaveWorld()) {
                    if (distance < scanRadius) {
                        changeVelocity(entityProperties, shipProperties, aiProperties,
                                new VelocityChange(VELOCITY_CHANGE_NAME_RELOADER_STOP, 0.0f, RELOADER_SPEED_CHANGE_RATE));
                        if (entityProperties.getVelocity() < ENG_Math.FLOAT_EPSILON && playerShipEntityProperties.getVelocity() < ENG_Math.FLOAT_EPSILON) {
                            // We can begin to reload the player ship
                            ShipProperties playerShipShipProperties = shipPropertiesMapper.get(playerShip);
                            WeaponProperties playerShipWeaponProperties = weaponPropertiesMapper.get(playerShip);
                            if (playerShipWeaponProperties.hasWeapons() && ENG_Utility.hasTimePassed(FrameInterval.RELOADER_TIME_BETWEEN_RELOADING_UNITS,
                                            aiProperties.getReloaderShipIncrementWeaponNumTime(),
                                            RELOADER_TIME_BETWEEN_RELOADING_UNITS)) {
                                aiProperties.setReloaderShipIncrementWeaponNumTime();
                                boolean weaponIncremented = false; // Check if at least a weapon has been incremented.
                                for (WeaponType wpn : playerShipShipProperties.getShipData().weaponTypeList) {
                                    if (!WeaponType.hasInfiniteAmmo(wpn)) {
                                        if (playerShipWeaponProperties.getWeaponAmmo(wpn) < WeaponType.getDefaultMissileNumber(wpn)) {
                                            playerShipWeaponProperties.incrementWeaponAmmo(wpn, 1);
                                            weaponIncremented = true;
                                        }
                                    }
                                }
                                if (!weaponIncremented) {
                                    // All weapons full so leave world
                                    aiProperties.setReloaderShouldLeaveWorld(true);
                                }
                            }
                        }
                    } else {
                        ENG_Quaternion rotation = new ENG_Quaternion();
//                        ENG_Math.rotateTowardPositionDeg(
//                                playerShipEntityProperties.getNode().getPosition(),
//                                entityProperties.getNode().getPosition(),
//                                entityProperties.getNode().getLocalInverseZAxis(),
//                                entityProperties.getNode().getLocalYAxis(),
//                                rotation,
//                                getRotationAngle(shipProperties));
//                        entityProperties.rotate(rotation, true, TransformSpace.TS_WORLD);
                        Utility.rotateToPosition(entityProperties.getNode().getLocalInverseZAxis(),
                                playerShipEntityProperties.getNode().getPosition().subAsVec(entityProperties.getNode().getPosition()),
                                updateInterval, entityProperties,
                                shipProperties.getShipData().maxAngularVelocity);
//                        ENG_Vector4D axis = new ENG_Vector4D();
//                        float angle = rotation.toAngleAxisDeg(axis);
//                        System.out.println("rotation axis: " + axis + " angle: " + angle);
                        changeVelocity(entityProperties, shipProperties, aiProperties,
                                new VelocityChange(
                                        VELOCITY_CHANGE_NAME_RELOADER_TOWARDS_SHIP,
                                        shipProperties.getShipData().maxSpeed,
                                        RELOADER_SPEED_CHANGE_RATE));
                    }
                } else { // Should leave the world
//                    ENG_Quaternion rotation = ENG_Math.rotateAwayFromPositionDeg(
//                                    playerShipEntityProperties.getNode().getPosition(),
//                                    entityProperties.getNode().getPosition(),
//                                    entityProperties.getNode().getLocalInverseZAxis(),
//                                    getRotationAngle(shipProperties));
//                    ENG_Vector4D axis = new ENG_Vector4D();
//                    // We don't care about the axis
//                    float angle = ENG_Quaternion.toAngleAxisDeg(rotation, axis);
//                    aiProperties.setReloaderCurrentAwayAngle(aiProperties.getReloaderCurrentAwayAngle() + angle);
//                    entityProperties.rotate(rotation);
                    ENG_Vector4D frontVec = entityProperties.getNode().getLocalInverseZAxis();
                    ENG_Vector4D targetVec = playerShipEntityProperties.getNode().getPosition().subAsVec(entityProperties.getNode().getPosition());
                    Utility.rotateAwayFromPosition(frontVec,
                            targetVec,
                            updateInterval, entityProperties,
                            shipProperties.getShipData().maxAngularVelocity);
                    aiProperties.setReloaderCurrentAwayAngle(frontVec.angleBetween(targetVec));
                    if (aiProperties.getReloaderCurrentAwayAngle() > RELOADER_AWAY_ANGLE) {
                        Utility.clearAngularVelocity(entityProperties.getRigidBody());
                        MainApp.getGame().getWorldManager().startAnimation(entityProperties.getEntityId(), shipProperties.getExitedWorldAnimation());
                    }
                }
            }
        }
    }

    private void evadeLevelLimits(AIProperties aiProperties, EntityProperties entityProperties, ShipProperties shipProperties) {
        entityProperties.getLimitsReached(levelLimits);
        boolean awayFromLimitsPosSet = aiProperties.isAwayFromLimitsPosSet();
        // Even if we are no longer touching the level limits we must still complete
        // the turn around.
        if (levelLimits.equals(ENG_Math.VEC4_ZERO) && aiProperties.isRotationAwayFromLimitsCompleted()) {
            aiProperties.setState(AIState.SEEK_CLOSEST_PLAYER);
            showAIStateChange(shipProperties, aiProperties);
        } else {
            // We must make sure that we reset the level limits for each frame or we
            // will end up with previous limits never going away since they are
            // only additive in EntityContactListener.
            entityProperties.resetLimitsReached();
            // If we have new level limits should we check and recreate awayFromLimitsPos?
            // Using what we have here we might become stuck switching between 2 levels limits
            // forever.
            if  (awayFromLimitsPosSet && !levelLimits.equals(currentLevelLimits) && !levelLimits.equals(ENG_Math.VEC4_ZERO)) {
                System.out.println("levelLimits: " + levelLimits + " currentLevelLimits: " + currentLevelLimits);
                awayFromLimitsPosSet = false;
            }
            if (!awayFromLimitsPosSet) {
                levelLimits.invert(awayFromLimitsPos);
                awayFromLimitsPos.normalize();
                currentLevelLimits.set(levelLimits);
                // First slow down to 0 then rotate away.
                entityProperties.setVelocity(0.0f);
                aiProperties.setAwayFromLimitsPosSet(true);
                aiProperties.setRotationAwayFromLimitsCompleted(false);
            }

//            awayFromLimitsPos.addInPlace(entityProperties.getNode().getPosition());
            entityProperties.getNode().getPosition(currentPos);
            entityProperties.getNode().getLocalInverseZAxis(currentFrontVec);
//            entityProperties.getNode().getLocalYAxis(currentUpVec);

            float angleBetween = currentFrontVec.angleBetween(awayFromLimitsPos);


//            ENG_Math.rotateTowardPositionDeg(awayFromLimitsPos, currentPos, currentFrontVec, currentUpVec, rotation, getRotationAngle(shipProperties));
//            entityProperties.rotate(rotation, true, TransformSpace.TS_WORLD);
            Utilities.rotateTowardPosition(entityProperties, shipProperties, awayFromLimitsPos);
//            Utility.rotateToPosition(currentFrontVec,
//                    awayFromLimitsPos,
//                    updateInterval, entityProperties,
//                    shipProperties.getShipData().maxAngularVelocity);



            if (angleBetween < ESCAPE_LEVEL_LIMITS_ANGLE) {
                aiProperties.setRotationAwayFromLimitsCompleted(true);
                aiProperties.setAwayFromLimitsPosSet(false);
                // Also get to max speed
                changeVelocity(entityProperties, shipProperties, aiProperties, new VelocityChange(
                        VELOCITY_CHANGE_NAME_ESCAPING_LIMITS,
                        shipProperties.getShipData().maxSpeed,
                        ESCAPING_LIMITS_ACCELERATION_RATE));
            }
            if (DEBUG) {
                System.out.println("ship " + entityProperties.getUniqueName()
                        + " has reached the limits " + levelLimits
                        + " and moved towards " + awayFromLimitsPos
                        + " . Current pos: " + currentPos);
            }
        }
    }

    private void reachDestination(AIProperties aiProperties, EntityProperties entityProperties, ShipProperties shipProperties) {
        aiProperties.getDestination(destination);
        entityProperties.getNode().getPosition(currentPos);
        if (entityProperties.getRadius() < destination.distance(currentPos)) {
            Utilities.rotateTowardPosition(entityProperties, shipProperties, destination);
            accelerateToMaxSpeed(shipProperties, entityProperties, aiProperties,
                    VELOCITY_CHANGE_NAME_REACHING_DESTINATION, REACHING_DESTINATION_VELOCITY_CHANGE_STEP);

        } else {
            aiProperties.setDestinationReached(true);
            changeVelocity(entityProperties, shipProperties, aiProperties, new VelocityChange(
                            VELOCITY_CHANGE_NAME_DESTINATION_REACHED,
                            0.0f,
                            REACHING_DESTINATION_VELOCITY_CHANGE_STEP));
        }
    }

    private float getRotationAngle(ShipProperties shipProperties) {
        return shipProperties.getShipData().turnAngle //* 3//* 10
//				* (float) GameWorld.getWorld().getDelta();
                * updateInterval;
    }

    /** @noinspection deprecation*/
    private static void showAIStateChange(ShipProperties shipProperties, AIProperties aiProperties) {
        if (DEBUG || true) {
            ENG_Log.getInstance().log(shipProperties.getName() + " state changed to " + aiProperties.getState());
        }
    }

    private static void showAIWaypointStateChange(ShipProperties shipProperties, AIProperties aiProperties) {
        if (DEBUG || WaypointSystem.DEBUG) {
            ENG_Log.getInstance().log(shipProperties.getName() + " waypoint state changed to " + aiProperties.getWaypointState());
        }
    }

    private void shootAhead(AIProperties aiProperties, EntityProperties entityProperties, ShipProperties shipProperties, WeaponProperties weaponProperties, Entity followedShip) {
        if (!WeaponType.isHomingMissileType(weaponProperties.getCurrentWeaponType())) {
            Entity ship = MainApp.getGame().getWorldManager().getEntityByItemId(aiProperties.getFollowedShip());
            WorldManagerBase worldManager = WorldManagerBase.getSingleton();
            EntityProperties followedShipEntityProperties = worldManager.getEntityPropertiesComponentMapper().get(ship);

            WeaponData weaponData = WeaponData.getWeaponData(weaponProperties.getCurrentWeaponType());
            boolean valid = ENG_Utility.calculateCollisionPosition(
                    weaponData.maxSpeed,
                    entityProperties.getNode()._getFullTransformNative().invertAffineRet(),
                    crossPosition,
                    followedShipEntityProperties.getVelocityAsVec(),
                    followedShipEntityProperties.getNode().getOrientation(),
                    followedShipEntityProperties.getNode().getPosition());
            if (valid) {

                entityProperties.getNode().getLocalInverseZAxis(currentFrontVec);
                entityProperties.getNode().getLocalYAxis(currentUpVec);
                entityProperties.getNode().getPosition(currentPos);
                EntityProperties otherShipEntityProperties = worldManager.getEntityPropertiesComponentMapper().get(followedShip);
//                System.out.println("crossPosition: " + crossPosition);
                crossPosition.normalize();
//                entityProperties.getNode()._getFullTransformNative().transform(crossPosition, transformedCrossPosition);
                Utility.rotateToPosition(currentFrontVec, crossPosition, updateInterval, entityProperties,
                        180.0f);
            }
        }
    }

    // private VelocityChange currentVelocityChange;
    private void changeVelocity(EntityProperties entityProperties, ShipProperties shipProperties,
                                AIProperties aiProperties, VelocityChange velocityChange) {

        VelocityChange currentVelocityChange = aiProperties.getCurrentVelocityChange();

        if (currentVelocityChange == null ||
                velocityChange.id != currentVelocityChange.id ||
                currentVelocityChange.currentChange - 1.0f > ENG_Math.FLOAT_EPSILON) {
            currentVelocityChange = velocityChange;
            currentVelocityChange.initialSpeed = entityProperties.getVelocity();
            aiProperties.setCurrentVelocityChange(velocityChange);
        }

        float velocity = entityProperties.getVelocity();
        if (Math.abs(velocity - currentVelocityChange.newSpeed) > ENG_Math.FLOAT_EPSILON && currentVelocityChange.currentChange < 1.0f) {
            currentVelocityChange.currentChange += currentVelocityChange.changeStep;
            entityProperties.setVelocity(currentVelocityChange.initialSpeed
                            + (currentVelocityChange.newSpeed - currentVelocityChange.initialSpeed)
                            * currentVelocityChange.currentChange);

        }
    }

    /** @noinspection deprecation*/
    private boolean checkTargeted(AIProperties aiProperties, ShipProperties shipProperties) {
        ImmutableBag<Entity> enemyEntities = GameWorld.getWorld().getManager(GroupManager.class).getEntities(ShipTeam.getOtherTeamAsString(
                shipProperties.getShipData().team));
        int size = enemyEntities.size();
        boolean targeted = false;
        for (int i = 0; i < size; ++i) {
            ENG_SceneNode node = entityPropertiesMapper.get(enemyEntities.get(i)).getNode();
            node.getPosition(otherPos);
            node.getLocalInverseZAxis(otherFrontVec);

            otherPos.sub(currentPos, distVec);
            distVec.normalize();
            float angleBetween = otherFrontVec.angleBetween(distVec);
            if (angleBetween < TARGETING_ANGLE) {
                // We are being targeted try to evade
                aiProperties.setState(AIState.EVADE_MISSILE);
                targeted = true;
                break;
            }
        }
        return targeted;
    }

    public static class VelocityChange {

        public final int id;
        public float newSpeed;
        public float changeStep;
        public float currentChange;
        public float initialSpeed;

        public VelocityChange(int id) {
            this.id = id;
        }

        public VelocityChange(int id, float newSpeed, float changeStep) {
            this.id = id;
            this.newSpeed = newSpeed;
            this.changeStep = changeStep;
        }
    }
}
