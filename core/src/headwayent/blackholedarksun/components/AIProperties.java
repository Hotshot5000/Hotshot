/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.components;

import headwayent.blackholedarksun.systems.AISystem.VelocityChange;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Integer;

import com.artemis.Component;
import com.badlogic.gdx.physics.bullet.collision.ClosestNotMeRayResultCallback;

import java.util.ArrayList;

public class AIProperties extends Component {

    public enum AIState {
        SEEK_CLOSEST_PLAYER, FOLLOW_PLAYER, SHOOT_PLAYER, EVADE_MISSILE,
        COLLISION_RESPONSE, EVADE_HIT, FOLLOW_PLAYER_SHIP, EVADE_LEVEL_LIMITS, REACH_DESTINATION
    }

    public enum AIWaypointState {
        NONE, MOVING_TO_ENTRANCE, MOVING_TO_EXIT, MOVING_TO_WAYPOINT_DESTINATION
    }

    private AIState state = AIState.SEEK_CLOSEST_PLAYER;
    private AIWaypointState waypointState = AIWaypointState.NONE;
    private long followedShip = -1;
    private boolean lockedIn;
    private boolean followCountTimeStarted;
    private long followBeginTime;
    private boolean evadingCollision;
    private long evadingCollisionTimeStarted;
    private boolean enemySelected;
    private long enemySelectionTimeStarted;
    private long weaponCooldownTimeStarted;
    private boolean collided;
    private final ENG_Vector4D collisionEvasionDirectionFromStaticObject = new ENG_Vector4D();
    private boolean collidedWithStaticObject;
    private final ENG_Vector4D collisionEvasionDestination = new ENG_Vector4D();
    private float collisionAngleDirection; // -1 or 1
    private boolean axisAndAngleSelected;
    private float currentCollisionAngle;
    private final ENG_Vector4D initialFrontVec = new ENG_Vector4D();
    private boolean initialFrontVecSet;
    private long collisionResponseMovementTime;
    private boolean collisionResponseMovement;
    private int currentHealth;
    private boolean evadingHit;
    private long hitEvasionTime;
    private boolean patrolling;
    private long patrollingRotationTimeStarted;
    private final ENG_Vector4D patrollingRotationAxis = new ENG_Vector4D();
    private float patrolAngleDirection;
    private float currentPatrollingRotationAngle;
    private boolean patrollingRotationStarted;
    private long reloaderShipIncrementWeaponNumTime;
    private boolean reloaderShouldLeaveWorld;
    private float reloaderCurrentAwayAngle;
    private VelocityChange currentVelocityChange;
    private final ENG_Vector4D destination = new ENG_Vector4D(true);
    private boolean reachDestination;
    private boolean destinationReached;
    private long currentShootingAtShip = -1;
    private int currentLaunchedProjectiles;
    private long limitProjectilesLaunchedStartTime;
    private boolean limitProjectilesLaunched;
    private boolean evadeHitDirectionSet;
    private int evadeHitAxis;
    private float evadeHitDirection;
    private boolean awayFromLimitsPosSet;
    private boolean rotationAwayFromLimitsCompleted;
    private int chasedByEnemyNum;
    private ClosestNotMeRayResultCallback rayResultCallback;

    private String attackEntityName; // Favorite target from the level data.

    // We only save the entrance and exit waypoint points for the next sector.
    // Once the ship exits the sector and if the target is still around the same position
    // we then recalculate and find the next entrance and exit for the next sector
    // that is needs to pass through.
    // TODO does it make more sense to save the whole chain of multiple entrance/exits to avoid recalculation?

    /**
     * When you have just exited a sector you might still be in the same bounding box but you are moving
     * towards the enemy (or next entrance). If you are moving towards an enemy the waypointState will be NONE.
     * How would the system know that you are just leaving the sector and not just spawned in?
     * This variable is compared to the current sector after we left it to make sure we are not
     * acting as if we just spawned in.
     */
    private int lastWaypointSectorId;
    private int currentWaypointSectorId;
    private int currentWaypointId;
    private int currentTargetWaypointId;
    private int entranceWaypointId;
    private int exitWaypointId;
    private ArrayList<ENG_Integer> waypointChainToEvasionWaypoint;
    private int waypointChainToEvasionWaypointCurrentIndex;
    private ENG_Vector4D lastPosition = new ENG_Vector4D(true);
    private ENG_Vector4D lastFollowedShipPosition = new ENG_Vector4D(true);
    private boolean waypointShootPlayer;
    private boolean followingWaypointWhileChased;

    private String entityName;
    private int setStateNum;

//    private boolean ignoreAI; // Used during portal exit so it doesn't look around

    public AIProperties() {

    }

    public AIState getState() {
        return state;
    }

    public void setState(AIState state) {
        this.state = state;
//        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
//            MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval().addObject("AI_STATE TEMP " + entityName + " " + (setStateNum++), state);
//        }
//        if (MainApp.getMainThread().isInputState()) {
//            System.out.println("AI_STATE TEMP " + entityName + " " + (setStateNum++) + " " + state);
//        }
    }

    public AIWaypointState getWaypointState() {
        return waypointState;
    }

    public void setWaypointState(AIWaypointState waypointState) {
        this.waypointState = waypointState;
        System.out.println("waypointState: " + waypointState);
    }

    public long getFollowedShip() {
        return followedShip;
    }

    public void setFollowedShip(long followedShip) {
        this.followedShip = followedShip;
    }

    public void resetFollowedShip() {
        this.followedShip = -1;
    }

    public boolean isLockedIn() {
        return lockedIn;
    }

    public void setLockedIn(boolean lockedIn) {
        this.lockedIn = lockedIn;
    }

    public boolean isFollowCountTimeStarted() {
        return followCountTimeStarted;
    }

    public void setFollowCountTimeStarted(boolean followCountTimeStarted) {
        this.followCountTimeStarted = followCountTimeStarted;
    }

    public long getFollowBeginTime() {
        return followBeginTime;
    }

    public void setFollowBeginTime(long followBeginTime) {
        this.followBeginTime = followBeginTime;
    }

    public void setFollowBeginTime() {
        this.followBeginTime = ENG_Utility.currentTimeMillis();
    }

    public boolean isEvadingCollision() {
        return evadingCollision;
    }

    public void setEvadingCollision(boolean evadingCollision) {
        this.evadingCollision = evadingCollision;
    }

    public long getEvadingCollisionTimeStarted() {
        return evadingCollisionTimeStarted;
    }

    public void setEvadingCollisionTimeStarted(long evadingCollisionTimeStarted) {
        this.evadingCollisionTimeStarted = evadingCollisionTimeStarted;
    }

    public void setEvadingCollisionTimeStarted() {
        this.evadingCollisionTimeStarted = ENG_Utility.currentTimeMillis();
    }

    public boolean isEnemySelected() {
        return enemySelected;
    }

    public void setEnemySelected(boolean enemySelected) {
        this.enemySelected = enemySelected;
    }

    public long getEnemySelectionTimeStarted() {
        return enemySelectionTimeStarted;
    }

    public void setEnemySelectionTimeStarted(long enemySelectionTimeStarted) {
        this.enemySelectionTimeStarted = enemySelectionTimeStarted;
    }

    public void setEnemySelectionTimeStarted() {
        this.enemySelectionTimeStarted = ENG_Utility.currentTimeMillis();
    }

    public long getWeaponCooldownTimeStarted() {
        return weaponCooldownTimeStarted;
    }

    public void setWeaponCooldownTimeStarted(long weaponCooldownTimeStarted) {
        this.weaponCooldownTimeStarted = weaponCooldownTimeStarted;
    }

    public void setWeaponCooldownTimeStarted() {
        this.weaponCooldownTimeStarted = ENG_Utility.currentTimeMillis();
    }

    public boolean isCollided() {
        return collided;
    }

    public void setCollided(boolean collided) {
        this.collided = collided;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public boolean isEvadingHit() {
        return evadingHit;
    }

    public void setEvadingHit(boolean evadingHit) {
        this.evadingHit = evadingHit;
    }

    public long getHitEvasionTime() {
        return hitEvasionTime;
    }

    public void setHitEvasionTime() {
        this.hitEvasionTime = ENG_Utility.currentTimeMillis();
    }

    public void setCollisionEvasionDestination(float x, float y, float z) {
        collisionEvasionDestination.set(x, y, z);
    }

    public void setCollisionEvasionDestination(ENG_Vector4D axis) {
        collisionEvasionDestination.set(axis);
    }

    public void getCollisionEvasionDestination(ENG_Vector4D ret) {
        ret.set(collisionEvasionDestination);
    }

    public ENG_Vector4D getCollisionEvasionDestination() {
        return new ENG_Vector4D(collisionEvasionDestination);
    }

    public void setCollisionEvasionDirectionFromStaticObject(float x, float y, float z) {
        collisionEvasionDirectionFromStaticObject.set(x, y, z);
    }

    public void setCollisionEvasionDirectionFromStaticObject(ENG_Vector4D axis) {
        collisionEvasionDirectionFromStaticObject.set(axis);
    }

    public void getCollisionEvasionDirectionFromStaticObject(ENG_Vector4D ret) {
        ret.set(collisionEvasionDirectionFromStaticObject);
    }

    public ENG_Vector4D getCollisionEvasionDirectionFromStaticObject() {
        return new ENG_Vector4D(collisionEvasionDirectionFromStaticObject);
    }

    public boolean isCollidedWithStaticObject() {
        return collidedWithStaticObject;
    }

    public void setCollidedWithStaticObject(boolean collidedWithStaticObject) {
        this.collidedWithStaticObject = collidedWithStaticObject;
    }

    public float getCollisionAngleDirection() {
        return collisionAngleDirection;
    }

    public void setCollisionAngleDirection(float collisionAngleDirection) {
        this.collisionAngleDirection = Math.signum(collisionAngleDirection);
    /*	if ((collisionAngleDirection > 0.0f &&
                collisionAngleDirection - 1.0f > ENG_Math.FLOAT_EPSILON) ||
				(collisionAngleDirection < 0.0f && 
						collisionAngleDirection + 1.0f < -ENG_Math.FLOAT_EPSILON)) {
			throw new IllegalArgumentException("collisionAngleDirection must be " +
					"either 1.0f or 1.0f");
		}*/

    }

    public boolean isAxisAndAngleSelected() {
        return axisAndAngleSelected;
    }

    public void setAxisAndAngleSelected(boolean axisAndAngleSelected) {
        this.axisAndAngleSelected = axisAndAngleSelected;
    }

    public boolean isInitialFrontVecSet() {
        return initialFrontVecSet;
    }

    public void setInitialFrontVecSet(boolean initialFrontVecSet) {
        this.initialFrontVecSet = initialFrontVecSet;
    }

    public ENG_Vector4D getInitialFrontVec() {
        return new ENG_Vector4D(initialFrontVec);
    }

    public void setInitialFrontVec(ENG_Vector4D initialFrontVec) {
        this.initialFrontVec.set(initialFrontVec);
    }

    public float getCurrentCollisionAngle() {
        return currentCollisionAngle;
    }

    public void setCurrentCollisionAngle(float currentCollisionAngle) {
        this.currentCollisionAngle = currentCollisionAngle;
    }

    public long getCollisionResponseMovementTime() {
        return collisionResponseMovementTime;
    }

    public void setCollisionResponseMovementTime() {
        this.collisionResponseMovementTime = ENG_Utility.currentTimeMillis();
    }

    public boolean isCollisionResponseMovement() {
        return collisionResponseMovement;
    }

    public void setCollisionResponseMovement(boolean collisionResponseMovement) {
        this.collisionResponseMovement = collisionResponseMovement;
    }

    public boolean isPatrolling() {
        return patrolling;
    }

    public void setPatrolling(boolean patrolling) {
        this.patrolling = patrolling;
    }

    public long getPatrollingRotationTimeStarted() {
        return patrollingRotationTimeStarted;
    }

    public void setPatrollingRotationTimeStarted() {
        this.patrollingRotationTimeStarted = ENG_Utility.currentTimeMillis();
    }

    public float getCurrentPatrollingRotationAngle() {
        return currentPatrollingRotationAngle;
    }

    public void setCurrentPatrollingRotationAngle(
            float currentPatrollingRotationAngle) {
        this.currentPatrollingRotationAngle = currentPatrollingRotationAngle;
    }

    public ENG_Vector4D getPatrollingRotationAxis() {
        return new ENG_Vector4D(patrollingRotationAxis);
    }

    public void getPatrollingRotationAxis(ENG_Vector4D ret) {
        ret.set(patrollingRotationAxis);
    }

    public void setPatrollingRotationAxis(ENG_Vector4D patrollingRotationAxis) {
        this.patrollingRotationAxis.set(patrollingRotationAxis);
    }

    public boolean isPatrollingRotationStarted() {
        return patrollingRotationStarted;
    }

    public void setPatrollingRotationStarted(boolean patrollingRotationStarted) {
        this.patrollingRotationStarted = patrollingRotationStarted;
    }

    public float getPatrolAngleDirection() {
        return patrolAngleDirection;
    }

    public void setPatrolAngleDirection(float patrolAngleDirection) {
        this.patrolAngleDirection = Math.signum(patrolAngleDirection);
    }

    public long getReloaderShipIncrementWeaponNumTime() {
        return reloaderShipIncrementWeaponNumTime;
    }

    public void setReloaderShipIncrementWeaponNumTime() {
        this.reloaderShipIncrementWeaponNumTime = ENG_Utility.currentTimeMillis();
    }

    public boolean isReloaderShouldLeaveWorld() {
        return reloaderShouldLeaveWorld;
    }

    public void setReloaderShouldLeaveWorld(boolean reloaderShouldLeaveWorld) {
        this.reloaderShouldLeaveWorld = reloaderShouldLeaveWorld;
    }

    public float getReloaderCurrentAwayAngle() {
        return reloaderCurrentAwayAngle;
    }

    public void setReloaderCurrentAwayAngle(float reloaderCurrentAwayAngle) {
        this.reloaderCurrentAwayAngle = reloaderCurrentAwayAngle;
    }

    public VelocityChange getCurrentVelocityChange() {
        return currentVelocityChange;
    }

    public void setCurrentVelocityChange(VelocityChange currentVelocityChange) {
        this.currentVelocityChange = currentVelocityChange;
        System.out.println("setCurrentVelocityChange id: " + currentVelocityChange.id +
                " initialSpeed: " + currentVelocityChange.initialSpeed + " newSpeed: " + currentVelocityChange.newSpeed);
    }

    /**
     * @return the destination
     */
    public ENG_Vector4D getDestination() {
        return new ENG_Vector4D(destination);
    }

    public void getDestination(ENG_Vector4D ret) {
        ret.set(destination);
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(ENG_Vector4D destination) {
        this.destination.set(destination);
    }

    /**
     * @return the reachDestination
     */
    public boolean isReachDestination() {
        return reachDestination;
    }

    /**
     * @param reachDestination the reachDestination to set
     */
    public void setReachDestination(boolean reachDestination) {
        this.reachDestination = reachDestination;
    }

    /**
     * @return the currentShootingAtShip
     */
    public long getCurrentShootingAtShip() {
        return currentShootingAtShip;
    }

    /**
     * @param currentShootingAtShip the currentShootingAtShip to set
     */
    public void setCurrentShootingAtShip(long currentShootingAtShip) {
        this.currentShootingAtShip = currentShootingAtShip;
    }

    /**
     * @return the currentLaunchedProjectiles
     */
    public int getCurrentLaunchedProjectiles() {
        return currentLaunchedProjectiles;
    }

    public void incrementCurrentLaunchedProjectiles() {
        ++currentLaunchedProjectiles;
    }

    public void decrementCurrentLaunchedProjectiles() {
        --currentLaunchedProjectiles;
    }

    /**
     * @param currentLaunchedProjectiles the currentLaunchedProjectiles to set
     */
    public void setCurrentLaunchedProjectiles(int currentLaunchedProjectiles) {
        this.currentLaunchedProjectiles = currentLaunchedProjectiles;
    }

    /**
     * @return the limitProjectilesLaunchedStartTime
     */
    public long getLimitProjectilesLaunchedStartTime() {
        return limitProjectilesLaunchedStartTime;
    }

    /**
     * @param limitProjectilesLaunchedStartTime the limitProjectilesLaunchedStartTime to set
     */
    public void setLimitProjectilesLaunchedStartTime() {
        this.limitProjectilesLaunchedStartTime = ENG_Utility.currentTimeMillis();
    }

    /**
     * @return the limitProjectilesLaunched
     */
    public boolean isLimitProjectilesLaunched() {
        return limitProjectilesLaunched;
    }

    /**
     * @param limitProjectilesLaunched the limitProjectilesLaunched to set
     */
    public void setLimitProjectilesLaunched(boolean limitProjectilesLaunched) {
        this.limitProjectilesLaunched = limitProjectilesLaunched;
    }

    /**
     * @return the destinationReached
     */
    public boolean isDestinationReached() {
        return destinationReached;
    }

    /**
     * @param destinationReached the destinationReached to set
     */
    public void setDestinationReached(boolean destinationReached) {
        this.destinationReached = destinationReached;
    }

    public boolean isEvadeHitDirectionSet() {
        return evadeHitDirectionSet;
    }

    public void setEvadeHitDirectionSet(boolean evadeHitDirectionSet) {
        this.evadeHitDirectionSet = evadeHitDirectionSet;
    }

    public int getEvadeHitAxis() {
        return evadeHitAxis;
    }

    public float getEvadeHitDirection() {
        return evadeHitDirection;
    }

    public void setEvadeHitAxisAndDirection(int evadeHitAxis,
                                            float evadeHitDirection) {
        this.evadeHitAxis = evadeHitAxis;
        this.evadeHitDirection = evadeHitDirection;
    }

    public boolean isAwayFromLimitsPosSet() {
        return awayFromLimitsPosSet;
    }

    public void setAwayFromLimitsPosSet(boolean awayFromLimitsPosSet) {
        this.awayFromLimitsPosSet = awayFromLimitsPosSet;
    }

    public boolean isRotationAwayFromLimitsCompleted() {
        return rotationAwayFromLimitsCompleted;
    }

    public void setRotationAwayFromLimitsCompleted(boolean rotationAwayFromLimitsCompleted) {
        this.rotationAwayFromLimitsCompleted = rotationAwayFromLimitsCompleted;
    }

    public void resetChasedByEnemyNum() {
        chasedByEnemyNum = 0;
    }

    public void incrementChasedByEnemyNum() {
        ++chasedByEnemyNum;
    }

    public void decrementChasedByEnemyNum() {
        if (chasedByEnemyNum > 0) {
            --chasedByEnemyNum;
        }
    }

    public int getChasedByEnemyNum() {
        return chasedByEnemyNum;
    }

    public void setChasedByEnemyNum(int chasedByEnemyNum) {
        this.chasedByEnemyNum = chasedByEnemyNum;
    }

    public void resetCurrentWaypointAndWaypointSector() {
        currentWaypointSectorId = 0;
        currentWaypointId = 0;
        currentTargetWaypointId = 0;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getAttackEntityName() {
        return attackEntityName;
    }

    public void setAttackEntityName(String attackEntityName) {
        this.attackEntityName = attackEntityName;
    }

    public int getLastWaypointSectorId() {
        return lastWaypointSectorId;
    }

    public void setLastWaypointSectorId(int lastWaypointSectorId) {
        this.lastWaypointSectorId = lastWaypointSectorId;
        System.out.println("setLastWaypointSectorId(): " + lastWaypointSectorId);
    }

    public int getCurrentWaypointSectorId() {
        return currentWaypointSectorId;
    }

    public void setCurrentWaypointSectorId(int currentWaypointSectorId) {
        this.currentWaypointSectorId = currentWaypointSectorId;
        System.out.println("setCurrentWaypointSectorId(): " + currentWaypointSectorId);
    }

    public int getCurrentWaypointId() {
        return currentWaypointId;
    }

    public void setCurrentWaypointId(int currentWaypointId) {
        this.currentWaypointId = currentWaypointId;
        System.out.println("setCurrentWaypointId(): " + currentWaypointId);
    }

    public int getCurrentTargetWaypointId() {
        return currentTargetWaypointId;
    }

    public void setCurrentTargetWaypointId(int currentTargetWaypointId) {
        this.currentTargetWaypointId = currentTargetWaypointId;
        System.out.println("setCurrentTargetWaypointId(): " + currentTargetWaypointId);
    }

    public int getEntranceWaypointId() {
        return entranceWaypointId;
    }

    public void setEntranceWaypointId(int entranceWaypointId) {
        this.entranceWaypointId = entranceWaypointId;
        System.out.println("setEntranceWaypointId(): " + entranceWaypointId);
    }

    public int getExitWaypointId() {
        return exitWaypointId;
    }

    public void setExitWaypointId(int exitWaypointId) {
        this.exitWaypointId = exitWaypointId;
        System.out.println("setExitWaypointId(): " + exitWaypointId);
    }

    public void getLastPosition(ENG_Vector4D ret) {
        ret.set(lastPosition);
    }

    public ENG_Vector4D getLastPosition() {
        return new ENG_Vector4D(lastPosition);
    }

    public void setLastPosition(ENG_Vector4D lastPosition) {
        this.lastPosition.set(lastPosition);
    }

    public void getLastFollowedShipPosition(ENG_Vector4D ret) {
        ret.set(lastFollowedShipPosition);
    }

    public ENG_Vector4D getLastFollowedShipPosition() {
        return new ENG_Vector4D(lastFollowedShipPosition);
    }

    public void setLastFollowedShipPosition(ENG_Vector4D lastFollowedShipPosition) {
        this.lastFollowedShipPosition.set(lastFollowedShipPosition);
    }

    public boolean isWaypointShootPlayer() {
        return waypointShootPlayer;
    }

    public void setWaypointShootPlayer(boolean waypointShootPlayer) {
        this.waypointShootPlayer = waypointShootPlayer;
        System.out.println("waypointShootPlayer: " + waypointShootPlayer);
    }

    public boolean isFollowingWaypointWhileChased() {
        return followingWaypointWhileChased;
    }

    public void setFollowingWaypointWhileChased(boolean followingWaypointWhileChased) {
        this.followingWaypointWhileChased = followingWaypointWhileChased;
    }

    public ArrayList<ENG_Integer> getWaypointChainToEvasionWaypoint() {
        return waypointChainToEvasionWaypoint;
    }

    public void setWaypointChainToEvasionWaypoint(ArrayList<ENG_Integer> waypointChainToEvasionWaypoint) {
        this.waypointChainToEvasionWaypoint = waypointChainToEvasionWaypoint;
    }

    public int getWaypointChainToEvasionWaypointCurrentIndex() {
        return waypointChainToEvasionWaypointCurrentIndex;
    }

    public void setWaypointChainToEvasionWaypointCurrentIndex(int waypointChainToEvasionWaypointCurrentIndex) {
        this.waypointChainToEvasionWaypointCurrentIndex = waypointChainToEvasionWaypointCurrentIndex;
    }

    public void incrementWaypointChainToEvasionWaypointCurrentIndex() {
        ++waypointChainToEvasionWaypointCurrentIndex;
    }

    public void resetWaypointData() {
        waypointState = AIWaypointState.NONE;
        currentWaypointSectorId = 0;
        currentWaypointId = 0;
        currentTargetWaypointId = 0;
//        entranceWaypointId = 0;
//        exitWaypointId = 0;
        waypointChainToEvasionWaypoint = null;
        waypointChainToEvasionWaypointCurrentIndex = 0;
        System.out.println("resetWaypointData()");
    }

    public ClosestNotMeRayResultCallback getRayResultCallback() {
        return rayResultCallback;
    }

    public void setRayResultCallback(ClosestNotMeRayResultCallback rayResultCallback) {
        this.rayResultCallback = rayResultCallback;
    }

    //		public boolean isIgnoreAI() {
//			return ignoreAI;
//		}
//
//		public void setIgnoreAI(boolean ignoreAI) {
//			this.ignoreAI = ignoreAI;
//		}

}
