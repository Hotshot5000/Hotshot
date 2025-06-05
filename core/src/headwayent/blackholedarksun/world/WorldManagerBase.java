/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/22/21, 6:26 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.world;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import headwayent.blackholedarksun.*;
import headwayent.blackholedarksun.animations.AnimationFactory;
import headwayent.blackholedarksun.components.*;
import headwayent.blackholedarksun.entitydata.DebrisData;
import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.blackholedarksun.entitydata.WeaponData;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.blackholedarksun.levelresource.*;
import headwayent.blackholedarksun.menus.Subtitles;
import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntity;
import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntityTCP;
import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntityUDP;
import headwayent.blackholedarksun.multiplayer.components.PlayerState;
import headwayent.blackholedarksun.parser.CutsceneLoader;
import headwayent.blackholedarksun.parser.ast.CameraAttachEvent;
import headwayent.blackholedarksun.parser.ast.CameraDetachEvent;
import headwayent.blackholedarksun.parser.ast.CameraEvent;
import headwayent.blackholedarksun.parser.ast.Cutscene;
import headwayent.blackholedarksun.parser.ast.Event;
import headwayent.blackholedarksun.parser.ast.InitialConds;
import headwayent.blackholedarksun.parser.ast.ObjDefinition;
import headwayent.blackholedarksun.parser.ast.ObjectEvent;
import headwayent.blackholedarksun.parser.ast.ParallelTask;
import headwayent.blackholedarksun.parser.dispatchers.CutsceneEventDispatcher;
import headwayent.blackholedarksun.physics.EntityMotionState;
import headwayent.blackholedarksun.physics.EntityRigidBody;
import headwayent.blackholedarksun.physics.PhysicsProperties;
import headwayent.blackholedarksun.physics.PhysicsUtility;
import headwayent.blackholedarksun.physics.StaticEntityMotionState;
import headwayent.blackholedarksun.physics.StaticEntityRigidBody;
import headwayent.blackholedarksun.statistics.InGameStatistics;
import headwayent.blackholedarksun.statistics.InGameStatisticsManager;
import headwayent.blackholedarksun.statistics.LevelEventStatistics;
import headwayent.blackholedarksun.statistics.LevelStatistics;
import headwayent.blackholedarksun.statistics.SessionStatistics;
import headwayent.blackholedarksun.statistics.WeaponTypeStatistics;
import headwayent.blackholedarksun.systems.helper.ai.WaypointSystem;
import headwayent.hotshotengine.*;
import headwayent.hotshotengine.audio.ENG_Playable;
import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;
import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;
import headwayent.hotshotengine.renderer.*;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_SlowCallExecutor;

import org.apache.commons.io.FilenameUtils;

import java.util.*;

import static headwayent.hotshotengine.ENG_Utility.currentTimeMillis;

/**
 * Created by sebas on 09.11.2015.
 */
public abstract class WorldManagerBase {
    public static final float RIGID_BODY_FRICTION = 0.9f;
    //    protected HashMap<Long, Entity> entityByIdMap = new HashMap<>();

    public enum LevelState {
        NONE, LOADED, STARTED, PAUSED, ENDED
    }

    public enum EntityFinalState {
        EXITED, DESTROYED;

        public static EntityFinalState getFinalState(Entity e) {
            WorldManager worldManager = WorldManager.getSingleton();
            EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(e);
            ShipProperties shipProperties = worldManager.getShipPropertiesComponentMapper().get(e);
            if (entityProperties.isDestroyed()) {
                return DESTROYED;
            } else if (shipProperties != null && shipProperties.isExited()) {
                return EXITED;
            }
            throw new IllegalArgumentException(entityProperties.getItem().getName() + " is not in a final state");
        }
    }

    interface ComparatorNodeAction {
        boolean execute(String s);
    }

    public static class Sound {
        public String name;
        public long id = -1;
        public long beginTime;
        public long duration;
        public boolean loop;
        public boolean stopSound;
        public float maxDistance = MAX_SOUND_DISTANCE;
        public float volumeMultiplier = 1.0f;
        public int volume; // 0 - 100.
        public int previousVolume; // If we already setVolume() to 0 no need to do it again.
        public float pan;
        public float maxSoundSpeed = 1.0f; // Avoid NaN when dividing for sound speed.
        public boolean startOnProximity;
        public boolean doNotDelete;
        public boolean started;

        public Sound() {

        }

//        public Sound(String name, long id, long beginTime) {
//            this.name = name;
//            this.id = id;
//            this.beginTime = beginTime;
//        }

//        public Sound(String name, long id, boolean loop) {
//            this.name = name;
//            this.id = id;
//            this.loop = loop;
//        }

        public Sound(String name, /*long id,*/ boolean loop, boolean startOnProximity,
                     boolean doNotDeleteWhenOutOfRange, long beginTime, long duration) {
            this.name = name;
//			this.id = id;
            this.loop = loop;
            this.startOnProximity = startOnProximity;
            this.doNotDelete = doNotDeleteWhenOutOfRange;
            this.beginTime = beginTime;
            this.duration = duration;
        }

        public Sound(String soundName, long id, int volume, float pan, long beginTime, long duration) {
            this.name = soundName;
            this.id = id;
            this.volume = volume;
            this.pan = pan;
            this.beginTime = beginTime;
            this.duration = duration;
        }

        public Sound(String soundName, long id, int volume, float pan, boolean loop, long beginTime, long duration) {
            this.name = soundName;
            this.id = id;
            this.volume = volume;
            this.pan = pan;
            this.loop = loop;
            this.beginTime = beginTime;
            this.duration = duration;
        }
    }

    static class AutoRotation {
        public final ENG_Quaternion rot = new ENG_Quaternion(true);
        public final ENG_Vector4D axis = new ENG_Vector4D();
        public final float angle;
        public final ENG_SceneNode node;

        public AutoRotation(float x, float y, float z, float angle,
                            ENG_SceneNode node) {
            axis.set(x, y, z);
            this.angle = angle;
            this.node = node;
        }

        public AutoRotation(ENG_Vector4D axis, float angle, ENG_SceneNode node) {
            this.axis.set(axis);
            this.angle = angle;
            this.node = node;
        }
    }

    static class AvailableName implements Comparable<AvailableName> {
        public String name;
        public boolean available;

        public AvailableName() {

        }

        public AvailableName(String name) {
            this.name = name;
        }

        @Override
        public int compareTo(AvailableName arg0) {

            return name.compareTo(arg0.name);
        }

        @Override
        public boolean equals(Object o) {

            if (o instanceof AvailableName) {
                AvailableName an = (AvailableName) o;
                return name.equals(an.name);
            }
            throw new ClassCastException("o is not of class AvailableName");
        }

        @Override
        public int hashCode() {

            return name.hashCode();
        }
    }

    public static class EntityAndDistance {
        public Entity entity;
        public float distance;

        public EntityAndDistance() {

        }

        public EntityAndDistance(Entity entity, float distance) {
            this.entity = entity;
            this.distance = distance;
        }
    }

    public static final boolean LOAD_FROM_SDCARD = true;
    public static final String ENTITY_GROUP_NAME = "mainGroup";
    public static final String BUNDLE_MISSION_DEBRIEFING = "bundle";
    protected static final float PAN_DISTANCE = 200.0f;
    protected static final float PAN_DISTANCE_INV = 1.0f / PAN_DISTANCE;
    protected static final int SOUND_LIFETIME = 5000; // No sound should be longer than 5 secs.
    protected static final int MAX_HUMAN_DEMO_SHIPS = 4;
    protected static final int MAX_ALIEN_DEMO_SHIPS = 4;
    protected static final int DEMO_SHIP_SPAWN_CHANCE = 10;
    protected static final int MAX_DEMO_SHIPS = 8;
    protected static final String RELOADER = "Reloader";
    // If change here also check in blackholedarksunmain.cpp about usages.
    protected static final String CAMERA_NODE_NAME = "camera";
    protected static final String CUTSCENE_CAMERA = "cutscene_camera";
    protected static final float MAX_SOUND_DISTANCE = 2500.0f;
    protected static final float MAX_ENGINE_SOUND_DISTANCE = 1000.0f;
    protected static final int PIRANHA_MISSILE_NUM = 8;
    protected static final float PIRANHA_MISSILE_POSITION_OFFSET = 10.0f;
    protected static final float PIRANHA_MISSILE_POSITION_HALF_OFFSET = PIRANHA_MISSILE_POSITION_OFFSET * 0.5f;
    protected static final float PIRANHA_MISSILE_ORIENTATION_OFFSET = 45.0f; // Deg
    protected static final float PIRANHA_MISSILE_ORIENTATION_HALF_OFFSET = PIRANHA_MISSILE_ORIENTATION_OFFSET * 0.5f;
    protected static final float RELOADER_INITIAL_DISTANCE_FROM_PLAYER_SHIP = 500.0f;

    protected static WorldManagerBase mgr;
    protected static long gameEntityId;
    protected ENG_SceneManager sceneManager;
    protected GameWorld gameWorld;// = GameWorld.getWorld();
    protected ShipData playerShipData;
    protected TreeMap<Integer, Entity> entityMap = new TreeMap<>();
    protected final HashMap<Long, Entity> gameEntityIdsToEntities = new HashMap<>();
    protected final HashMap<Long, Entity> gameEntityIdsToShips = new HashMap<>();
    protected final HashMap<Long, Entity> itemIdsToEntities = new HashMap<>();
    // Since the client that launches a projectile only knows the tracked ship's client id
    // we need to have a way to map the client id to the item id since the item id
    // is what is used in order for the projectile to track the followed ship.
    protected final HashMap<Long, Long> entityIdsToItemIds = new HashMap<>();
    protected final HashSet<Long> waypointIds = new HashSet<>();
    protected final HashSet<Long> staticObjectIds = new HashSet<>();
    protected LevelState levelState = LevelState.NONE;
    protected final HashMap<String, LevelEvent> levelEventList = new HashMap<>();
    protected final HashMap<String, LevelEntity> levelObjectToEntityMap = new HashMap<>();
    protected final HashMap<Long, LevelEntity> levelObjectIdToEntityMap = new HashMap<>();
    protected final LinkedList<Animation> animationList = new LinkedList<>();
    // Right now we can't have the same animation running multiple times for the same entity.
    protected final HashMap<Long, HashMap<String, Animation>> animationMap = new HashMap<>();
    protected final ArrayList<Animation> animationsToRemove = new ArrayList<>();
    protected final HashMap<Long, ArrayList<String>> animationsEntityIdToRemove = new HashMap<>();
    protected LinkedList<Entity> entityList = new LinkedList<>();
    protected final ArrayList<Entity> entitiesToRemove = new ArrayList<>();
    protected LinkedList<Entity> projectileList = new LinkedList<>();
//    protected HashMap<String, EntityFinalState> finalStateList = new HashMap<String, WorldManager.EntityFinalState>();
    protected final LinkedList<AutoRotation> autoRotateList = new LinkedList<>();
//    protected Bundle currentBundle;
//    protected final ReentrantLock currentBundleLock = new ReentrantLock();
    protected boolean showDemo;
    protected final TreeSet<String> availableNameList = new TreeSet<>();
    protected final HashMap<ENG_Playable, ArrayList<Sound>> currentPlayingSounds = new HashMap<>();
    protected final ENG_Vector4D leftEar = new ENG_Vector4D(-10.0f, 0.0f, 0.0f, 1.0f);
    protected final ENG_Vector4D rightEar = new ENG_Vector4D(10.0f, 0.0f, 0.0f, 1.0f);

    protected final ENG_Vector4D currentShipPosition = new ENG_Vector4D(true);
    protected final ENG_Quaternion currentShipOrientation = new ENG_Quaternion();
    protected final ENG_Vector4D currentShipVelocity = new ENG_Vector4D(true);
    protected final ENG_Vector4D laserLeftOffset = new ENG_Vector4D(-15.0f, -10.0f, 0.0f, 1.0f);
    protected final ENG_Vector4D laserRightOffset = new ENG_Vector4D(15.0f, -10.0f, 0.0f, 1.0f);
    protected final ENG_Vector4D quadLaserLeftOffset = new ENG_Vector4D(-15.0f, -15.0f, 0.0f, 1.0f);
    protected final ENG_Vector4D quadLaserRightOffset = new ENG_Vector4D(15.0f, -15.0f, 0.0f, 1.0f);
    protected final ENG_Vector4D plasmaLeftOffset = new ENG_Vector4D(-20.0f, -10.0f, 0.0f, 1.0f);
    protected final ENG_Vector4D plasmaRightOffset = new ENG_Vector4D(20.0f, -10.0f, 0.0f, 1.0f);
    protected final ENG_Vector4D concussionOffset = new ENG_Vector4D(0.0f, -10.0f, 0.0f, 1.0f);
    protected final ENG_Vector4D homingOffset = new ENG_Vector4D(0.0f, -10.0f, 0.0f, 1.0f);
    protected final ENG_Vector4D megaOffset = new ENG_Vector4D(0.0f, -10.0f, 0.0f, 1.0f);
    protected final ENG_Vector4D piranhaOffset = new ENG_Vector4D(0.0f, -20.0f, 0.0f, 1.0f);
    protected int currentLevel;
    protected long playerShipEntityId = -1;
    protected final ENG_Vector4D chasingProjectilePos = new ENG_Vector4D(true);
    protected Entity currentBeacon;
    protected long currentBeaconId;
    protected boolean reloaderDisabled;

    protected final ArrayList<Long> cargoIdList = new ArrayList<>();
    protected final HashMap<String, Long> cargoNameToIdMap = new HashMap<>();

    protected final ENG_Vector4D beaconToPlayerShip = new ENG_Vector4D();
    protected final ENG_Vector4D playerPos = new ENG_Vector4D();
    protected final ENG_Vector4D beaconPos = new ENG_Vector4D();
    protected ENG_Entity currentSkybox;
    protected String eventEndReason;
    protected long currentReloaderShipId = -1;

    protected final ENG_Vector4D otherPos = new ENG_Vector4D(true);

    protected final HashMap<String, Cutscene> cutsceneMap = new HashMap<>();

    protected ComponentMapper<EntityProperties> entityPropertiesComponentMapper;// = gameWorld.getMapper(EntityProperties.class);
    protected ComponentMapper<ShipProperties> shipPropertiesComponentMapper;// = gameWorld.getMapper(ShipProperties.class);
    protected ComponentMapper<AIProperties> aiPropertiesComponentMapper;// = gameWorld.getMapper(AIProperties.class);
    protected ComponentMapper<BeaconProperties> beaconPropertiesComponentMapper;// = gameWorld.getMapper(BeaconProperties.class);
    protected ComponentMapper<CameraProperties> cameraPropertiesComponentMapper;// = gameWorld.getMapper(CameraProperties.class);
    protected ComponentMapper<CargoProperties> cargoPropertiesComponentMapper;// = gameWorld.getMapper(CargoProperties.class);
    protected ComponentMapper<MultiplayerComponent> multiplayerComponentComponentMapper;// = gameWorld.getMapper(MultiplayerComponent.class);
    protected ComponentMapper<ProjectileProperties> projectilePropertiesComponentMapper;// = gameWorld.getMapper(ProjectileProperties.class);
    protected ComponentMapper<TrackerProperties> trackerPropertiesComponentMapper;// = gameWorld.getMapper(TrackerProperties.class);
    protected ComponentMapper<WeaponProperties> weaponPropertiesComponentMapper;// = gameWorld.getMapper(WeaponProperties.class);
    protected ComponentMapper<MultiplayerEntity> multiplayerEntityComponentMapper;// = gameWorld.getMapper(MultiplayerEntity.class);
    protected ComponentMapper<MultiplayerEntityTCP> multiplayerEntityTCPComponentMapper;// = gameWorld.getMapper(MultiplayerEntityTCP.class);
    protected ComponentMapper<MultiplayerEntityUDP> multiplayerEntityUDPComponentMapper;// = gameWorld.getMapper(MultiplayerEntityUDP.class);
    protected ComponentMapper<PlayerState> playerStateComponentMapper;// = gameWorld.getMapper(PlayerState.class);
    protected ComponentMapper<WaypointProperties> waypointPropertiesComponentMapper;
    protected ComponentMapper<StaticEntityProperties> staticEntityPropertiesComponentMapper;

    // For each added projectiles by a player this client frame we must make sure that
    // they are allowed to collide with each other.
    protected final ArrayList<btRigidBody> addedProjectilesPerPlayer = new ArrayList<>();
    protected static final HashMap<String, EntityAabb> aabbMap = new HashMap<>();
    protected CutsceneEventDispatcher cutsceneEventDispatcher;
    protected ENG_SceneNode cameraNode;

    public static class EntityAabb {
        public final ENG_Vector3D centre = new ENG_Vector3D();
        public final ENG_Vector3D halfSize = new ENG_Vector3D();
    }

    private btDiscreteDynamicsWorld dynamicWorld;

    public void setDynamicWorld(btDiscreteDynamicsWorld dynamicWorld) {
        this.dynamicWorld = dynamicWorld;
    }

    public btDiscreteDynamicsWorld getDynamicWorld() {
        return dynamicWorld;
    }

    private static PhysicsProperties.CollisionShape collisionShapeType = PhysicsProperties.CollisionShape.BOX;

    public static PhysicsProperties.CollisionShape getCollisionShapeType() {
        return collisionShapeType;
    }

    public static void setCollisionShapeType(PhysicsProperties.CollisionShape collisionShapeType) {
        WorldManagerBase.collisionShapeType = collisionShapeType;
    }

    public static EntityAabb getEntityAabb(final String meshName) {
        EntityAabb entityAabb = aabbMap.get(meshName);
        if (entityAabb == null) {
            entityAabb = new EntityAabb();
            // We need this since the meshes haven't yet been necessarily loaded and to avoid the race condition
            // in resource manager. Ideally everything should be loaded before of the start of the level,
            // but we would not be able to do this in multiplayer since we can't know which ships will be playing.
            final EntityAabb finalEntityAabb = entityAabb;
            ENG_SlowCallExecutor.execute(() -> {
                PhysicsUtility.getAabb(meshName, "", finalEntityAabb.centre, finalEntityAabb.halfSize);
                return 0;
            });
            aabbMap.put(meshName, entityAabb);
        }
        return entityAabb;
    }

    // Only for unique objects in level.
//    protected HashMap<Long, String> entityIdToLevelObjectName = new HashMap<>();

//    public LinkedList<Entity> getProjectileList() {
//        return projectileList;
//    }
//
//    public void addToEntitiesToRemove(Entity e) {
//        entitiesToRemove.add(e);
//    }

    public WorldManagerBase() {
        mgr = this;
        reinitializeWorld();
    }

    public void reinitializeWorld() {
        gameWorld = GameWorld.getWorld();
        entityPropertiesComponentMapper = gameWorld.getMapper(EntityProperties.class);
        shipPropertiesComponentMapper = gameWorld.getMapper(ShipProperties.class);
        aiPropertiesComponentMapper = gameWorld.getMapper(AIProperties.class);
        beaconPropertiesComponentMapper = gameWorld.getMapper(BeaconProperties.class);
        cameraPropertiesComponentMapper = gameWorld.getMapper(CameraProperties.class);
        cargoPropertiesComponentMapper = gameWorld.getMapper(CargoProperties.class);
        multiplayerComponentComponentMapper = gameWorld.getMapper(MultiplayerComponent.class);
        projectilePropertiesComponentMapper = gameWorld.getMapper(ProjectileProperties.class);
        trackerPropertiesComponentMapper = gameWorld.getMapper(TrackerProperties.class);
        weaponPropertiesComponentMapper = gameWorld.getMapper(WeaponProperties.class);
        multiplayerEntityComponentMapper = gameWorld.getMapper(MultiplayerEntity.class);
        multiplayerEntityTCPComponentMapper = gameWorld.getMapper(MultiplayerEntityTCP.class);
        multiplayerEntityUDPComponentMapper = gameWorld.getMapper(MultiplayerEntityUDP.class);
        playerStateComponentMapper = gameWorld.getMapper(PlayerState.class);
        waypointPropertiesComponentMapper = gameWorld.getMapper(WaypointProperties.class);
        staticEntityPropertiesComponentMapper = gameWorld.getMapper(StaticEntityProperties.class);
        WaypointSystem.getSingleton().removeAllWaypointSectors();
        WaypointSystem.getSingleton().setVisible(false);
        removeAllWaypointIds();
        removeAllStaticObjectIds();
    }

    public ComponentMapper<EntityProperties> getEntityPropertiesComponentMapper() {
        return entityPropertiesComponentMapper;
    }

    public ComponentMapper<ShipProperties> getShipPropertiesComponentMapper() {
        return shipPropertiesComponentMapper;
    }

    public ComponentMapper<AIProperties> getAiPropertiesComponentMapper() {
        return aiPropertiesComponentMapper;
    }

    public ComponentMapper<BeaconProperties> getBeaconPropertiesComponentMapper() {
        return beaconPropertiesComponentMapper;
    }

    public ComponentMapper<CameraProperties> getCameraPropertiesComponentMapper() {
        return cameraPropertiesComponentMapper;
    }

    public ComponentMapper<CargoProperties> getCargoPropertiesComponentMapper() {
        return cargoPropertiesComponentMapper;
    }

    public ComponentMapper<MultiplayerComponent> getMultiplayerComponentComponentMapper() {
        return multiplayerComponentComponentMapper;
    }

    public ComponentMapper<ProjectileProperties> getProjectilePropertiesComponentMapper() {
        return projectilePropertiesComponentMapper;
    }

    public ComponentMapper<TrackerProperties> getTrackerPropertiesComponentMapper() {
        return trackerPropertiesComponentMapper;
    }

    public ComponentMapper<WeaponProperties> getWeaponPropertiesComponentMapper() {
        return weaponPropertiesComponentMapper;
    }

    public ComponentMapper<MultiplayerEntity> getMultiplayerEntityComponentMapper() {
        return multiplayerEntityComponentMapper;
    }

    public ComponentMapper<MultiplayerEntityTCP> getMultiplayerEntityTCPComponentMapper() {
        return multiplayerEntityTCPComponentMapper;
    }

    public ComponentMapper<MultiplayerEntityUDP> getMultiplayerEntityUDPComponentMapper() {
        return multiplayerEntityUDPComponentMapper;
    }

    public ComponentMapper<PlayerState> getPlayerStateComponentMapper() {
        return playerStateComponentMapper;
    }

    public ComponentMapper<WaypointProperties> getWaypointPropertiesComponentMapper() {
        return waypointPropertiesComponentMapper;
    }

    public ComponentMapper<StaticEntityProperties> getStaticEntityPropertiesComponentMapper() {
        return staticEntityPropertiesComponentMapper;
    }

    abstract void prepareLevel();

    public void resetIdCounter() {
        gameEntityId = 0;
    }

    abstract void resetWorld();

    protected void initializeEndEvents(LevelBase level) {
        for (int i = 0; i < level.getLevelEventNum(); ++i) {
            LevelEvent levelEvent = level.getLevelEvent(i);
            // Check for missing comparators that mean everything should be
            // anded
            createDefaultComparatorNode(levelEvent.prevCondEndRoot, levelEvent.prevCondList);
            extractWinLossEndConditions(levelEvent);
            createEndCondComparatorNodes(levelEvent.endCond.winNode, levelEvent.endCond.winList);
            createEndCondComparatorNodes(levelEvent.endCond.lossNode, levelEvent.endCond.lossList);
        }
        createDefaultComparatorNode(level.levelEnd.endEvents, level.levelEnd.endEventList);
    }

    protected void extractWinLossEndConditions(LevelEvent levelEvent) {
        levelEventList.put(levelEvent.name, levelEvent);
        for (LevelEndCond.EndCond endCond : levelEvent.endCond.winList) {
            levelEvent.winEndCondList.put(endCond.name, endCond);
        }
        for (LevelEndCond.EndCond endCond : levelEvent.endCond.lossList) {
            levelEvent.lossEndCondList.put(endCond.name, endCond);
        }
    }

    public abstract void update(long currentTime);


    protected boolean executeActionOnNode(ComparatorNode node,
                                          ComparatorNodeAction action) {
        if (node.op != null) {
            boolean result = executeActionOnNode(node.leaves.get(0), action);
            for (int i = 1; i < node.leaves.size(); ++i) {
                switch (node.op) {
                    case AND:
                        result &= executeActionOnNode(node.leaves.get(i), action);
                        if (!result) {
                            return false;
                        }
                        break;
                    case OR:
                        result |= executeActionOnNode(node.leaves.get(i), action);
                        if (result) {
                            return true;
                        }
                        break;
                    case NOT:
                        result = !executeActionOnNode(node.leaves.get(i), action);
                        break;
                    case XOR:
                        result ^= executeActionOnNode(node.leaves.get(i), action);
                        break;
                    default:
                        throw new IllegalArgumentException(node.op
                                + " is an invalid operator");
                }

            }
            return result;
        } else if (node.s != null) {
            return action.execute(node.s);
        } else {
            throw new IllegalArgumentException(
                    "Invalid node. Must either have "
                            + "leaves or be a terminal node with a level event string");
        }
    }

    protected boolean checkIfDelayPassed(ArrayList<LevelEndCond.EndCond> list) {
        boolean delayPassed = true;
        for (LevelEndCond.EndCond cond : list) {
            if (cond.objectiveAchievedDelaySecs > 0) {

                if (cond.delayStartTime == 0) {
                    cond.delayStartTime = currentTimeMillis();
                    cond.objectiveAchievedDelaySecs = getDelay(cond.objectiveAchievedDelayType, cond.objectiveAchievedDelaySecs);
                    delayPassed = false;
                } else if (!ENG_Utility.hasTimePassed(
                        FrameInterval.OBJECTIVE_ACHIEVED_DELAY + cond.name,
                        cond.delayStartTime,
                        cond.objectiveAchievedDelaySecs)) {
                    delayPassed = false;

                }
            }
        }
        return delayPassed;
    }

//    public Entity getEntityFromLevelObjectEntityId(String name) {
//        LevelEntity entity = levelObjectToEntityMap.get(name);
//        if (entity == null) {
//            throw new IllegalArgumentException(name + " is not a valid name of level object in the level");
//        }
//        return entity.getEntity();
//    }

    public Entity getEntityFromLevelObjectEntityId(long id) {
        LevelEntity levelEntity = getLevelEntityFromEntityId(id);
        if (levelEntity == null) {
            throw new IllegalArgumentException(id + " is not a valid id of level object in the level");
        }
        return levelEntity.getEntity();
    }

    public LevelEntity getLevelEntityFromEntityId(long id) {
        return levelObjectIdToEntityMap.get(id);
    }

    protected boolean isLevelEventEnded(ComparatorNode node,
                                        final LevelEvent levelEvent) {
        return executeActionOnNode(node, s -> {

            LevelEndCond.EndCond endCond = null;
            LevelEndCond.EndCond winEndCond = levelEvent.winEndCondList.get(s);
            LevelEndCond.EndCond lossEndCond = levelEvent.lossEndCondList.get(s);
            if (winEndCond == null && lossEndCond == null) {
                throw new IllegalArgumentException(s + " could not be found neither in win list or in loss list");
            } else if (winEndCond != null) {
                endCond = winEndCond;
            } else if (lossEndCond != null) {
                endCond = lossEndCond;
            }
            if (endCond.conditionMet) {
                return true;
            }
            switch (endCond.type) {
                case SHIP_DESTINATION_REACHED: {

                    boolean shipReached = true;

                    for (String name : endCond.objects) {
                        LevelEntity levelEntity = getLevelEntity(name);
                        if (levelEntity == null) {
                            throw new NullPointerException(name + " is not a valid level entity");
                        }
                        if (levelEntity.isDestroyed() || levelEntity.isExited()) {
                            shipReached = false;
                            break;
                        }
                        Entity entity = levelEntity.getEntity();
                        if (entity == null) {
                            throw new NullPointerException(name + " ship " + "does not exist in level");
                        }
                        AIProperties aiProperties = aiPropertiesComponentMapper.getSafe(entity);
                        if (aiProperties == null) {
                            throw new NullPointerException(name + " does not " + "have an AI component");
                        }
                        if (!aiProperties.isDestinationReached()) {
                            shipReached = false;
                            break;
                        }
                    }
                    if (shipReached) {
                        endCond.conditionMet = true;
                        return true;
                    }
                }
                break;
                case CARGO_SCANNED: {
                    boolean scanned = true;
                    for (String name : endCond.objects) {
                        LevelEntity levelEntity = getLevelEntity(name);
                        if (levelEntity == null) {
                            throw new NullPointerException(name + " is not a valid level entity");
                        }
                        if (levelEntity.isDestroyed() || levelEntity.isExited()) {
                            scanned = false;
                            break;
                        }
                        Entity entity = levelEntity.getEntity();
                        if (entity != null) {
                            CargoProperties cargoProperties = cargoPropertiesComponentMapper.getSafe(entity);
                            if (cargoProperties != null) {
                                if (!cargoProperties.isScanned()) {
                                    scanned = false;
                                    break;
                                }
                            } else {
                                throw new IllegalArgumentException("Cargo " + name + " is not " + "a cargo type");
                            }
                        } else {
                            throw new ENG_InvalidFieldStateException("The cargo " + name + " does not exist or it's killed");
                        }
                    }
                    if (scanned) {
                        endCond.conditionMet = true;
                        return true;
                    }
                }
                break;
                case TIME_ELAPSED: {
                    long delay = getDelay(endCond.delayType, endCond.secs);
//                        System.out.println("TIME_ELAPSED currentStartingTime: " + (event.currentStartingTime / 1000) + " currentTime: " + (ENG_Utility.currentTimeMillis() / 1000));
                    if (ENG_Utility.hasTimePassed(FrameInterval.LEVEL_END_TIME_LAPSED + levelEvent.name, levelEvent.currentStartingTime, delay)) {
                        endCond.conditionMet = true;
                        return true;
                    }
                }
                break;
                case DESTROYED: {
                    boolean destroyed = true;
                    for (String name : endCond.objects) {
                        LevelEntity levelEntity = getLevelEntity(name);
                        if (levelEntity == null) {
                            throw new NullPointerException(name + " is not a valid level entity");
                        }
                        if (!levelEntity.isDestroyed()) {
                            destroyed = false;
                            break; // Out of the for
                        }
                    }
                    if (destroyed) {
                        endCond.conditionMet = true;
                        return true;
                    }
                }
                break;
                case PLAYER_SHIP_DESTINATION_REACHED: {
                    Entity playerShip = getPlayerShip();
                    EntityProperties playerShipEntityProperties = entityPropertiesComponentMapper.get(playerShip);
                    Entity beacon = getLevelEntity(endCond.objects.get(0)).getEntity();
                    EntityProperties beaconEntityProperties = entityPropertiesComponentMapper.get(beacon);
                    BeaconProperties beaconProperties = beaconPropertiesComponentMapper.getSafe(beacon);
                    if (beaconProperties == null) {
                        throw new ENG_InvalidFieldStateException(endCond.objects.get(0) + " is not a valid beacon object");
                    }
                    playerShipEntityProperties.getNode().getPosition(playerPos);
                    beaconEntityProperties.getNode().getPosition(beaconPos);
                    playerPos.sub(beaconPos, beaconToPlayerShip);
                    if (beaconEntityProperties.getRadius() > beaconToPlayerShip.length()) {
                        beaconProperties.setReached(true);
                        beaconEntityProperties.setDestroyed(true);
                        currentBeacon = null;
                        endCond.conditionMet = true;
                        return true;
                    }
                }
                break;
                case EXITED: {
                    // Tell the objects to exit
                    boolean exited = true;
                    for (String name : levelEvent.exitObjects) {
                        LevelEntity levelEntity = getLevelEntity(name);
                        if (levelEntity == null) {
                            throw new NullPointerException(name + " is not a valid level entity");
                        }
                        if (!levelEntity.isExited()) {
                            exited = false;
                            break; // Out of the for
                        }
                    }
                    if (exited) {
                        endCond.conditionMet = true;
                        return true;
                    }
                }
                break;
                case EXITED_OR_DESTROYED: {
                    boolean exitedOrDestroyed = true;
                    for (String name : levelEvent.exitObjects) {
                        LevelEntity levelEntity = getLevelEntity(name);
                        if (levelEntity == null) {
//                                throw new NullPointerException(name + " is not a valid level entity");
                            continue;
                        }
                        if (!levelEntity.isExited() && !levelEntity.isDestroyed()) {
                            exitedOrDestroyed = false;
                            break; // Out of the for
                        }
                    }
                    if (exitedOrDestroyed) {
                        endCond.conditionMet = true;
                        return true;
                    }
                }
                break;
                case TEXT_SHOWN: {
                    if (levelEvent.validateLevelEvent() && ENG_Utility.hasTimePassed(FrameInterval.LEVEL_END_TEXT_SHOWN + levelEvent.name, levelEvent.currentStartingTime, levelEvent.textShownDuration)) {
                        endCond.conditionMet = true;
                        return true;
                    }
                }
                break;
                default:
                    throw new IllegalArgumentException(endCond.type + " not supported as an end condition type");
            }
            return false;
        });
    }

    protected boolean isLevelEnd(ComparatorNode node, final ArrayList<String> lossIgnoreList) {
        return executeActionOnNode(node, s -> {

            LevelEvent levelEvent = getLevelEvent(s);
            // In the event when we want to ignore a loss for situations
            // like
            // when we want to start another event even if we lost another
            // one
            // This would normally have ended the level with a loss but by
            // this
            // we can avoid such a thing while maintaining backward
            // compatibility
            // with the fact that a loss should trigger a level end
            if (levelEvent.state == LevelEvent.EventState.WON) return true;
            return !lossIgnoreList.contains(levelEvent.name) && (levelEvent.state == LevelEvent.EventState.LOST);
        });
    }

    protected boolean isLevelWon(ComparatorNode node) {
        return executeActionOnNode(node, s -> {

            LevelEvent levelEvent = getLevelEvent(s);
            return levelEvent.state == LevelEvent.EventState.WON;
        });
    }

    protected boolean getLevelEventEndedReason(ComparatorNode node,
                                               final LevelEvent event) {
        return executeActionOnNode(node, s -> {
            LevelEndCond.EndCond endCond = event.lossEndCondList.get(s);
            if (endCond.conditionMet) {
                switch (endCond.type) {
                    case CARGO_SCANNED:
                        eventEndReason = "";
                        break;
                    case DESTROYED:
                        eventEndReason = "Vital cargo ship destroyed";
                        break;
                    case EXITED:
                        eventEndReason = "Enemy cargo ship escaped";
                        break;
                    case EXITED_OR_DESTROYED:
                        eventEndReason = "";
                        break;
                    case PLAYER_SHIP_DESTINATION_REACHED:
                        eventEndReason = "";
                        break;
                    case TIME_ELAPSED:
                        eventEndReason = "Mission time elapsed";
                        break;
                    case SHIP_DESTINATION_REACHED:
                        eventEndReason = "Enemy cargo ship escaped";
                        break;
                    case TEXT_SHOWN:
                        eventEndReason = "";
                        break;
                    default:
                        throw new IllegalArgumentException(endCond.type
                                + " is an invalid event type");
                }
            }
            return true;
        });
    }

    protected boolean isLevelEventStartable(ComparatorNode node) {
        return executeActionOnNode(node, s -> {

            LevelEvent levelEvent = getLevelEvent(s);
            return levelEvent.state == LevelEvent.EventState.WON
                    || levelEvent.state == LevelEvent.EventState.LOST;
        });

    }

    private LevelEvent getLevelEvent(String name) {
        LevelEvent event = levelEventList.get(name);
        if (event == null) {
            throw new IllegalArgumentException(name
                    + " is not a valid level event " + "name");
        }
        return event;
    }

    protected Cutscene loadCutscene(String cutsceneName) {
        Cutscene cutscene = CutsceneLoader.loadCutscene(cutsceneName + ".hsl", MainApp.getGame().getGameResourcesDir());
        cutsceneMap.put(cutsceneName, cutscene);
        return cutscene;
    }

    /**
     * For now this can be called only from single player.
     * @param cutsceneName
     * @param cutsceneType
     */
    protected void playCutscene(String cutsceneName, boolean createCameraNode, Cutscene.CutsceneType cutsceneType) {
        if (cutsceneName == null || cutsceneName.isEmpty()) {
            throw new IllegalArgumentException("cutsceneName is: " + cutsceneName);
        }
        if (cutsceneType == null) {
            throw new IllegalArgumentException("currentCutsceneType is null");
        }
        if (cutsceneEventDispatcher == null) {
            cutsceneEventDispatcher = new CutsceneEventDispatcher((WorldManagerSP) this);
        }
        Cutscene cutscene = cutsceneMap.get(cutsceneName);
        if (cutscene == null) {
            throw new IllegalArgumentException(cutsceneName + " not loaded in cutscene map");
        }
        Level level = (Level) gameWorld.getCurrentLevel();
        level.activeCutscene = cutscene;
        level.currentCutsceneType = cutsceneType;
        level.cutsceneActive = true;
        level.cutsceneCameraNodeCreated = createCameraNode;
        cutscene.init();

        ENG_SceneNode cameraNode = null;
        if (createCameraNode) {
            cameraNode = createCameraNode(CUTSCENE_CAMERA);
        } else {
            CameraProperties cameraProperties = cameraPropertiesComponentMapper.getSafe(getPlayerShip());
            if (cameraProperties == null) {
                throw new IllegalArgumentException("player ship not initialized");
            }
            cameraProperties.setAnimatedCamera(true);
            cameraNode = cameraProperties.getNode();
        }
        cutsceneEventDispatcher.setCameraNode(cameraNode);

        InitialConds initialConds = cutscene.getInitialConds();
        if (initialConds == null) {
            throw new IllegalStateException("Initial conds should not be null");
        }
        if (!level.levelStart.useSkyboxDataFromLevel) {
            ((WorldManager) this).createLevelLightingV2(initialConds);
            ((WorldManager) this).createSkybox(initialConds.getSkybox().getSkyboxName());
        }
        createEntitiesV2(initialConds.getObjDefinitionList());
//        for (ObjDefinition objDefinition : initialConds.getObjDefinitionList()) {
//
//        }
//
//
//        for (Map.Entry<String, Param> initialCond : cutscene.getInitialConds().getMap().entrySet()) {
//
//        }

    }

    protected void updateCutsceneEvents() {
        Level level = (Level) gameWorld.getCurrentLevel();
        if (level == null || !level.cutsceneActive) {
            return;
        }
        Cutscene cutscene = level.activeCutscene;
        Event eventToRemove = null;
        for (Event event : cutscene.getInGameEventList()) {
            if (event.getState() == Event.EventState.NONE) {
                event.setState(Event.EventState.STARTABLE);
            }
            boolean eventDispatched = event.acceptConditionally(cutsceneEventDispatcher);
            boolean parallelEvent = event.name.equals(ParallelTask.TYPE);
            if (eventDispatched || parallelEvent) {
                if (event.getState() == Event.EventState.FINISHED) {
                    eventToRemove = event;
                }
                if (!parallelEvent) {
                    break;
                }
            }
            if (event.name.equalsIgnoreCase(ObjectEvent.TYPE)) {

            } else if (event.name.equalsIgnoreCase(CameraAttachEvent.TYPE)) {

            } else if (event.name.equalsIgnoreCase(CameraDetachEvent.TYPE)) {

            } else if (event.name.equalsIgnoreCase(CameraEvent.TYPE)) {

            } else if (event.name.equalsIgnoreCase(ParallelTask.TYPE)) {

            }
        }
        if (eventToRemove != null) {
            System.out.println("Removing event: " + eventToRemove.name);
            cutscene.getInGameEventList().remove(eventToRemove);
        }
        if (cutscene.getInGameEventList().isEmpty()) {
            endCutscene();
        }
    }

    private void endCutscene() {
        Level level = (Level) gameWorld.getCurrentLevel();
        if (!level.cutsceneActive) {
            throw new IllegalStateException("No playing cutscene to end");
        }
        Subtitles.hideSubtitles();
        if (level.cutsceneCameraNodeCreated) {
            destroyCameraNode(CUTSCENE_CAMERA);
        }
        switch (level.currentCutsceneType) {
            case LEVEL_BEGINNING: {
                // We need to reset the world and remove all the created entities.
                // At the same time we cannot just call resetWorld() because that also
                // destroys the movement threads and sets the level to ended. It must
                // be refactored to support partial reset in some way.
                resetWorld();
                // We force this here so that we don't restart the cutscene in loadLevel.
                level.levelStart.cutsceneName = null;
                loadLevel();
            }
                break;
            case DURING_LEVEL: {

            }
                break;
            case LEVEL_ENDING: {

            }
                break;
            case STORY: {

            }
                break;
            default:
                throw new IllegalStateException("currentCutsceneType is: " + level.currentCutsceneType);
        }

        level.activeCutscene = null;
        level.currentCutsceneType = null;
        level.cutsceneActive = false;
        level.cutsceneCameraNodeCreated = false;
        cutsceneEventDispatcher = null;
    }

    protected void updateLevelEvents() {
        Level level = (Level) gameWorld.getCurrentLevel();

        if (level.cutsceneActive) {
            return;
        }

        for (LevelEvent levelEvent : level.levelEventList) {
            if (levelEvent.state == LevelEvent.EventState.NONE
                    && ((levelEvent.prevCondEndRoot.op != null && isLevelEventStartable(levelEvent.prevCondEndRoot)) || (levelEvent.prevCondList.isEmpty()))) {
                levelEvent.currentStartingTime = currentTimeMillis();
                levelEvent.state = LevelEvent.EventState.STARTABLE;
            } else if (levelEvent.state == LevelEvent.EventState.STARTABLE) {
                long delay = getDelay(levelEvent.delayType, levelEvent.delay);
                if (ENG_Utility.hasTimePassed(FrameInterval.LEVEL_EVENT_START_TIME + levelEvent.name, levelEvent.currentStartingTime, delay)) {
                    levelEvent.state = LevelEvent.EventState.STARTED;
                    levelEvent.currentStartingTime = currentTimeMillis();
                    if (!levelEvent.spawn.isEmpty()) {

                        spawnObjects(levelEvent);
                    }
                    if (!levelEvent.exitObjects.isEmpty()) {
                        exitObjectsFromLevel(levelEvent);
                    }
                    if (levelEvent.textShown != null && !levelEvent.textShown.isEmpty()) {
                        HudManager.getSingleton().setTutorialInfoText(levelEvent.textShown, levelEvent.textShownDuration);
                    }

                    InGameStatistics statistics = InGameStatisticsManager.getInstance().getInGameStatistics();
                    SessionStatistics latestSessionStatistics = statistics.getLatestSessionStatistics();
                    if (latestSessionStatistics != null) {
                        LevelStatistics latestLevelStatistics = latestSessionStatistics.getLatestLevelStatistics();
                        if (latestLevelStatistics != null) {
                            LevelEventStatistics levelEventStatistics = new LevelEventStatistics();
                            levelEventStatistics.name = levelEvent.name;
                            levelEventStatistics.state = LevelEvent.EventState.STARTED.toString();
                            levelEventStatistics.levelEventStartDate = ENG_DateUtils.getCurrentDateTimestamp();
                            levelEventStatistics.levelEventBeginTime = currentTimeMillis();

                            updateWeaponData(levelEventStatistics, levelEventStatistics.weaponTypeStatisticsEventBeginList);

                            Entity entity = getEntityByGameEntityId(playerShipEntityId);
                            if (entity != null) {
                                EntityProperties entityProperties = entityPropertiesComponentMapper.get(entity);
                                levelEventStatistics.healthEventBegin = entityProperties.getHealth();
                            }

                            latestLevelStatistics.levelEventStatisticsList.add(levelEventStatistics);
                        }
                    }
                }
            } else if (levelEvent.state == LevelEvent.EventState.STARTED) {
				/*
				 * if (levelEvent.state == EventState.WON || levelEvent.state ==
				 * EventState.LOST) { continue; // No use checking again }
				 */
                boolean eventWon = false;
                boolean eventLost = false;

                if (levelEvent.endCond.winNode.s != null || levelEvent.endCond.winNode.op != null) {
                    if (isLevelEventEnded(levelEvent.endCond.winNode, levelEvent)) {
                        // Check if any delay before we can call it a real end
                        if (checkIfDelayPassed(levelEvent.endCond.winList)) {
                            eventWon = true;
                        }
                    }
                }
                if (levelEvent.endCond.lossNode.s != null || levelEvent.endCond.lossNode.op != null) {
                    if (isLevelEventEnded(levelEvent.endCond.lossNode, levelEvent)) {
                        // Check if any delay before we can call it a real end
                        if (checkIfDelayPassed(levelEvent.endCond.lossList)) {
                            eventLost = true;
                        }
                    }
                }
                if (levelEvent.endCond.winNode.s == null
                        && levelEvent.endCond.winNode.op == null
                        && levelEvent.endCond.lossNode.s == null
                        && levelEvent.endCond.lossNode.op == null) {
                    // We have no win or loss cond
                    throw new ENG_InvalidFieldStateException("An event must either have an win or loss condition. Event Name: " + levelEvent.name);
                }
                // levelEvent.eventCompleted = true;
                // In case we both win and lose at the same time consider it a
                // win
                if (eventWon) {
                    levelEvent.state = LevelEvent.EventState.WON;
                } else if (eventLost) {
                    getLevelEventEndedReason(levelEvent.endCond.lossNode, levelEvent);
                    levelEvent.state = LevelEvent.EventState.LOST;
                }

                if (eventWon || eventLost) {
                    InGameStatistics statistics = InGameStatisticsManager.getInstance().getInGameStatistics();
                    SessionStatistics latestSessionStatistics = statistics.getLatestSessionStatistics();
                    if (latestSessionStatistics != null) {
                        LevelStatistics latestLevelStatistics = latestSessionStatistics.getLatestLevelStatistics();
                        if (latestLevelStatistics != null) {
                            LevelEventStatistics latestLevelEventStatistics = latestLevelStatistics.getLatestLevelEventStatistics();
                            if (latestLevelEventStatistics != null) {
                                latestLevelEventStatistics.state = levelEvent.state.toString();
                                latestLevelEventStatistics.levelEventEndDate = ENG_DateUtils.getCurrentDateTimestamp();
                                latestLevelEventStatistics.levelEventDuration = currentTimeMillis() - latestLevelEventStatistics.levelEventBeginTime;

                                updateWeaponData(latestLevelEventStatistics, latestLevelEventStatistics.weaponTypeStatisticsEventEndList);

                                Entity entity = getEntityByGameEntityId(playerShipEntityId);
                                if (entity != null) {
                                    EntityProperties entityProperties = entityPropertiesComponentMapper.get(entity);
                                    latestLevelEventStatistics.healthEventEnd = entityProperties.getHealth();
                                }
                            }
                        }
                    }
                }
            } else if (levelEvent.state == LevelEvent.EventState.WON) {
                // level.levelEventState = EventState.WON;
            } else if (levelEvent.state == LevelEvent.EventState.LOST) {

                if (!level.levelEnd.endEventIgnoreLossList.contains(levelEvent.name)) {
                    // level.levelEventState = EventState.LOST;
                    endLevel(level, LevelEvent.EventState.LOST);
                    // Get out of the for cause the level event list is empty
                    // now
                    return;
                }
            }/*
			 * else { // Should never get here throw new
			 * ENG_InvalidFieldStateException(levelEvent.state +
			 * " state is not handled in the world manager"); }
			 */
        }

        // Level end should never be null but for testing purposes
        // when we rip the whole level apart
        if (level.levelEnd != null && isLevelEnd(level.levelEnd.endEvents, level.levelEnd.endEventIgnoreLossList)) {

            endLevel(level, isLevelWon(level.levelEnd.endEvents) ? LevelEvent.EventState.WON : LevelEvent.EventState.LOST);
        }
    }

    protected void updateWeaponData(LevelEventStatistics levelEventStatistics, ArrayList<WeaponTypeStatistics> weaponTypeStatisticsList) {
        Entity entity = getEntityByGameEntityId(playerShipEntityId);
        if (entity != null) {
            WeaponProperties weaponProperties = weaponPropertiesComponentMapper.getSafe(entity);
            if (weaponProperties != null) {
                for (Map.Entry<WeaponData.WeaponType, Integer> weaponTypeIntegerEntry : weaponProperties.getWeaponAmmo().entrySet()) {
                    WeaponTypeStatistics weaponTypeStatistics = new WeaponTypeStatistics();
                    weaponTypeStatistics.weaponType = weaponTypeIntegerEntry.getKey().toString();
                    weaponTypeStatistics.currentAmmo = weaponTypeIntegerEntry.getValue();
                    weaponTypeStatisticsList.add(weaponTypeStatistics);
                }

            } else {
                // Can weaponProperties ever be null?
                System.out.println("weaponProperties is null");
            }
        }
    }

    protected abstract void exitObjectsFromLevel(LevelEvent levelEvent);

    protected abstract void spawnObjects(LevelEvent levelEvent);

    protected abstract void endLevel(LevelBase level, LevelEvent.EventState eventState);

    public void clearEntitiesFromList(Collection<Entity> entityList) {
        for (Entity entity : entitiesToRemove) {
            if (!entityList.remove(entity)) {
                throw new IllegalArgumentException("Entity " + entityPropertiesComponentMapper.get(entity).getItem().getName());
            }
        }
        entitiesToRemove.clear();
    }

    public void removeFromWorld(Entity entity, boolean removeLevelObjectToEntity) {
        EntityProperties entityProperties = entityPropertiesComponentMapper.get(entity);
        ShipProperties shipProperties = shipPropertiesComponentMapper.getSafe(entity);
        TrackerProperties trackerProperties = trackerPropertiesComponentMapper.getSafe(entity);
//		System.out.println("removing from movableObjectsToEntities: " + entityProperties.getEntity().getName());
        boolean removed = removeEntityByGameEntityId(entityProperties.getEntityId());
        removeEntityByItemId(entityProperties.getItem().getId());
        removeItemIdByEntityId(entityProperties.getEntityId());
        if (!removed) {
            throw new IllegalArgumentException(entityProperties.getItem().getName() + " not found in movableObjectsToEntities");
        }
        if (shipProperties != null) {
            System.out.println("removing from movableObjectsToShips: " + entityProperties.getName() + " with entityId: " + entityProperties.getEntityId());
            boolean removedShip = removeFromShipListByGameEntityId(entityProperties.getEntityId());
            if (!removedShip) {
                throw new IllegalArgumentException(entityProperties.getItem().getName() + " not found in movableObjectsToShips");
            }
        }
        // Make it destroyed for the levelObject
        LevelEntity levelEntity = getLevelEntityFromEntityId(entityProperties.getEntityId());
        if (levelEntity != null) {
            levelEntity.setDestroyed();
        }

        // This is always false for now. Level objects never get removed until the end of the level.
        if (removeLevelObjectToEntity) {
            LevelEntity removedLevelObjectToEntity = levelObjectToEntityMap.remove(entityProperties.getEntity().getName());
            if (removedLevelObjectToEntity == null) {
                throw new IllegalArgumentException(entityProperties.getEntity().getName() + " not found in levelObjectToEntityMap");
            }
        }
    }

    public void onRemoveRemovableEntity(Entity entity, EntityProperties entityProperties, ShipProperties shipProperties) {
        CargoProperties cargoProperties = cargoPropertiesComponentMapper.getSafe(entity);
        if (cargoProperties != null) {
            updateNumCargosInLevel(entityProperties.getItem().getId());
        }
        // Also remove all the active sounds
        // Moved to update all sounds

        // Notify that the engine sound must be stopped
        if (shipProperties != null && shipProperties.getEngineSound() != null) {
            Sound sound = shipProperties.getEngineSound();
            sound.stopSound = true;
            sound.doNotDelete = false;
        }
    }

    public void removeFromAvailableNameList(String name) {
        availableNameList.remove(name);
    }

    protected AIProperties addAIProperties(Entity gameEntity, EntityProperties entityProp, LevelObject obj) {
//        ComponentMapper<AIProperties> aiPropertiesComponentMapper = gameWorld.getMapper(AIProperties.class);
        AIProperties aiProp = aiPropertiesComponentMapper.create(gameEntity);
//        AIProperties aiProp = new AIProperties();
        aiProp.setCurrentHealth(entityProp.getHealth());
        aiProp.setReachDestination(obj.reachDestination);
        aiProp.setDestination(obj.destination);
        aiProp.setAttackEntityName(obj.attackName);
        return aiProp;
//        gameEntity.addComponent(aiProp);

    }

    protected abstract void createEntity(String modelName, LevelObject obj);

    public void createDebris(Entity entity, int num) {
        for (int i = 0; i < num; ++i) {
            createDebris(entity);
        }
    }

    public void createDebris(Entity entity) {
        EntityProperties creatorEntityProperties = entityPropertiesComponentMapper.get(entity);
        if (creatorEntityProperties.getObjectType() == null) {
            // Object types are only set by level objects. So no projectiles.
            throw new IllegalStateException(creatorEntityProperties.getName() + " does not have an object type.");
        }
        LevelObject.LevelObjectType objectType = creatorEntityProperties.getObjectType();
        if (objectType == LevelObject.LevelObjectType.PLAYER_SHIP ||
                objectType == LevelObject.LevelObjectType.PLAYER_SHIP_SELECTION) {
            objectType = LevelObject.LevelObjectType.FIGHTER_SHIP;
        }
        DebrisData entityData = DebrisData.getRandomDebris(objectType);
        String name = creatorEntityProperties.getName() + "_" + entityData.filename;
        // System.out.println("creating debris: " + name);
        if (entityData.filename == null || entityData.filename.isEmpty()) {
            entityData.filename = entityData.name + ".mesh";
        }
        ENG_Item item = sceneManager.createItem(EntityProperties.generateUniqueName(name, gameEntityId), ENG_Utility.getUniqueId(),
                entityData.filename, "", ENG_Workflows.MetallicWorkflow);
//        ENG_Entity entity = sceneManager.createEntity(EntityProperties.generateUniqueName(name, gameEntityId), gameEntityId, meshName, ENTITY_GROUP_NAME);


        EntityAabb entityAabb = getEntityAabb(entityData.filename);

        ENG_SceneNode node = sceneManager.getRootSceneNode().createChildSceneNode(item.getName());
//        node.attachObject(entity);
        node.attachObject(item);

        Entity gameEntity = gameWorld.createEntity();
//        if (MainApp.getGame().getGameMode() == APP_Game.GameMode.SP) {
        addEntityByGameEntityId(gameEntityId, gameEntity);
//        } else {
        // Since the ids are client specific we cannot mix them with the ids that are sent to us by the server.
        // Yes we can. < 0 means client specific >= 0 means from server.
//        }
        addEntityByItemId(item.getId(), gameEntity);
        addItemIdByEntityId(gameEntityId, item.getId());
//        entityList.add(gameEntity);
//        System.out.println("Adding projectile: " + name);
//        projectileList.add(gameEntity);
        EntityProperties entityProp = entityPropertiesComponentMapper.create(gameEntity);
//        EntityProperties entityProp = new EntityProperties(gameEntity, entity, node, id, name);
        entityProp.setGameEntity(gameEntity);
//        entityProp.setEntity(entity);
        entityProp.setItem(item);
        entityProp.setNode(node);
        entityProp.setEntityId(gameEntityId);
        entityProp.setName(name);
        // Unmovable will still be updated in MovementSystem if it's debris.
        entityProp.setUnmovable(true);
        entityProp.setDebrisLifeTime(entityData.lifetime);
        entityProp.setDebrisLifeBeginTime();
        // entityProp.setPosition(i == 0 ? laserLeftOffset : laserRightOffset);
        // entityProp.getNode().rotate(currentShipOrientation,
        // TransformSpace.TS_PARENT);
//        entityProp.setOnRemove(new EntityProperties.IRemovable() {
//            @Override
//            public void onRemove(Entity entity) {
//                projectileList.remove(entity);
//            }
//        });

        creatorEntityProperties.getNode().getPosition(currentShipPosition);
        creatorEntityProperties.getNode().getOrientation(currentShipOrientation);

        ENG_Vector3D radius = entityAabb.centre.add(entityAabb.halfSize);
        radius.addInPlace(10.0f, 10.0f, 10.0f);
        float x = ENG_Utility.rangeRandom(FrameInterval.DEBRIS_RADIUS_RANDOM_X + item.getName(), -1.0f, 1.0f);
        float y = ENG_Utility.rangeRandom(FrameInterval.DEBRIS_RADIUS_RANDOM_Y + item.getName(), -1.0f, 1.0f);
        float z = ENG_Utility.rangeRandom(FrameInterval.DEBRIS_RADIUS_RANDOM_Z + item.getName(), -1.0f, 1.0f);
        ENG_Vector4D posOnRadius = new ENG_Vector4D(true);
        posOnRadius.set(x, y, z);
        posOnRadius.normalize();
        posOnRadius.mulInPlace(radius);
        ENG_Vector4D impulse = new ENG_Vector4D(posOnRadius);
        posOnRadius.addInPlace(currentShipPosition);
        ENG_Quaternion orientation = new ENG_Quaternion(true);
        entityProp.setOrientation(orientation);
        entityProp.setPosition(posOnRadius);

        node.setPosition(posOnRadius.x, posOnRadius.y, posOnRadius.z);
        node.setOrientation(orientation);
        node._updateWithoutBoundsUpdate(false, false);

        EntityMotionState motionState = null;

        short collisionGroup = 0;
        short collisionMask = 0;

        motionState = new EntityMotionState(entityProp);
        collisionGroup = PhysicsProperties.CollisionGroup.STANDARD.getVal();
        collisionMask = PhysicsProperties.CollisionMask.STANDARD.getVal();

        createPhysicsSettings(entityProp, entityData, gameEntity, entityAabb, motionState,
                collisionGroup, collisionMask, PhysicsProperties.RigidBodyType.DEBRIS);

        float maxImpulse = 20.0f;
        float xImpulse = ENG_Utility.rangeRandom(FrameInterval.DEBRIS_RADIUS_RANDOM_IMPULSE_X + item.getName(), -maxImpulse, maxImpulse);
        float yImpulse = ENG_Utility.rangeRandom(FrameInterval.DEBRIS_RADIUS_RANDOM_IMPULSE_Y + item.getName(), -maxImpulse, maxImpulse);
        float zImpulse = ENG_Utility.rangeRandom(FrameInterval.DEBRIS_RADIUS_RANDOM_IMPULSE_Z + item.getName(), -maxImpulse, maxImpulse);
        Vector3 totalForce = creatorEntityProperties.getRigidBody().getTotalForce();
        Vector3 totalTorque = creatorEntityProperties.getRigidBody().getTotalTorque();
        Vector3 linearVelocity = creatorEntityProperties.getRigidBody().getLinearVelocity();
        entityProp.getRigidBody().applyCentralImpulse(
                PhysicsUtility.toVector3(impulse.mulAsPt(100.0f)
                        .addAsPt(new ENG_Vector3D(xImpulse, yImpulse, zImpulse))
                        .addAsPt(PhysicsUtility.toVector3D(linearVelocity))));

        float maxTorqueImpulse = 20.0f;
        float xTorqueImpulse = ENG_Utility.rangeRandom(FrameInterval.DEBRIS_RADIUS_RANDOM_TORQUE_IMPULSE_X + item.getName(), -maxTorqueImpulse, maxTorqueImpulse);
        float yTorqueImpulse = ENG_Utility.rangeRandom(FrameInterval.DEBRIS_RADIUS_RANDOM_TORQUE_IMPULSE_Y + item.getName(), -maxTorqueImpulse, maxTorqueImpulse);
        float zTorqueImpulse = ENG_Utility.rangeRandom(FrameInterval.DEBRIS_RADIUS_RANDOM_TORQUE_IMPULSE_Z + item.getName(), -maxTorqueImpulse, maxTorqueImpulse);
        entityProp.getRigidBody().applyTorqueImpulse(new Vector3(xTorqueImpulse, yTorqueImpulse, zTorqueImpulse));

        incrementGameEntityId();
    }

    public void createPhysicsSettings(EntityProperties entityProp, EntityData entityData,
                                      Entity gameEntity, EntityAabb entityAabb,
                                      EntityMotionState motionState, short collisionGroup, short collisionMask) {
        createPhysicsSettings(entityProp, entityData, gameEntity, entityAabb, motionState,
                collisionGroup, collisionMask, PhysicsProperties.RigidBodyType.ENTITY);
    }

    public void createPhysicsSettings(EntityProperties entityProp, EntityData entityData,
                                      Entity gameEntity, EntityAabb entityAabb,
                                      EntityMotionState motionState, short collisionGroup, short collisionMask,
                                      PhysicsProperties.RigidBodyType rigidBodyType) {
        float weight = entityProp.getWeight();

        boolean calculateLocalInertia = entityData.localInertia.isZeroLength();

        createPhysicsBody(gameEntity, entityAabb.halfSize, entityProp, motionState, collisionGroup,
                collisionMask, weight, calculateLocalInertia ? null : entityData.localInertia, rigidBodyType);

        entityProp.setDamping(entityData.linearDamping, entityData.angularDamping);

        entityProp.setActivationState(Collision.ACTIVE_TAG);
    }

    protected void createStaticEntity(String modelName, LevelObject obj) {
        if (obj.type != LevelObject.LevelObjectType.STATIC) {
            throw new IllegalArgumentException(obj.name + " not a static object");
        }

        ENG_SceneManager.SceneMemoryMgrTypes sceneType = ENG_SceneManager.SceneMemoryMgrTypes.SCENE_STATIC;
        ENG_Item item = sceneManager.createItem(EntityProperties.generateUniqueName(obj.name, gameEntityId),
                ENG_Utility.getUniqueId(), obj.meshName, ENG_SceneManager.AUTODETECT_RESOURCE_GROUP_NAME,
                sceneType, getWorkflow(obj));

        // Make sure to not update very frame only the first frame for the static objects.
        ENG_SceneNode node = sceneManager.getRootSceneNode(sceneType)
                .createChildSceneNode(item.getName(), sceneType, false);
        node.attachObject(item);
        // Set static must be done after attaching so that everything gets set to static.
        node.setStatic(true);

        node.setPosition(obj.position.x, obj.position.y, obj.position.z);
        node.setOrientation(obj.orientation);
        node._updateWithoutBoundsUpdate(false, false);

        StaticEntityMotionState motionState = null;

        short collisionGroup = 0;
        short collisionMask = 0;

        Entity gameEntity = gameWorld.createEntity();
        addEntityByGameEntityId(gameEntityId, gameEntity);
        addEntityByItemId(item.getId(), gameEntity);
        addItemIdByEntityId(gameEntityId, item.getId());

        StaticEntityProperties staticEntityProperties = staticEntityPropertiesComponentMapper.create(gameEntity);
        staticEntityProperties.setGameEntity(gameEntity);
        staticEntityProperties.setItem(item);
        staticEntityProperties.setNode(node);
        staticEntityProperties.setEntityId(gameEntityId);
        staticEntityProperties.setName(obj.name);
        staticEntityProperties.setObjectType(obj.type);

        motionState = new StaticEntityMotionState(node);//new btDefaultMotionState(PhysicsUtility.toVector3(node.getPosition(), PhysicsUtility.toQuaternion(node.getOrientation())));
        collisionGroup = PhysicsProperties.CollisionGroup.STANDARD.getVal();
        collisionMask = PhysicsProperties.CollisionMask.STANDARD.getVal();

        ArrayList<btBvhTriangleMeshShape> bvhTriangleMeshShapeList = PhysicsUtility.createBvhTriangleMeshShape(
                MainApp.getGame().getGameResourcesDir() + "/static_meshes/" + FilenameUtils.getBaseName(obj.meshName) + ".g3dj");

        addStaticObjectId(item.getId());

        // No longer needed as the SceneManager takes care of calling this
        // on the first frame after the static object has been added.
        // Check SceneManager.prepareSceneNodesForNativeRendering().
//        sceneManager.notifyStaticDirty(node);

        createStaticPhysicsBody(gameEntity, staticEntityProperties, motionState, bvhTriangleMeshShapeList, collisionGroup, collisionMask);

        incrementGameEntityId();
    }

    public void createEntitiesV2(ArrayList<ObjDefinition> objList) {
        for (ObjDefinition obj : objList) {
            LevelObject levelObject = obj.getAsLevelObject();
            String meshName = obj.getMeshName().getMeshName();
            if (meshName == null) {
                throw new ENG_InvalidFormatParsingException(
                        "You did not put a " + "mesh type in " + obj.name);
            }
            String extension = FilenameUtils.getExtension(levelObject.meshName);
            if (extension.isEmpty()) {
                levelObject.meshName += ".mesh";
            }

            if (levelObject.type == LevelObject.LevelObjectType.STATIC) {
                createStaticEntity(FilenameUtils.getBaseName(meshName), levelObject);
            } else {
                createEntity(FilenameUtils.getBaseName(meshName)/*data.name*/, levelObject);
            }
//            createEntity(FilenameUtils.getBaseName(meshName)/*data.name*/, levelObject);
        }

    }

    public void createEntities(ArrayList<LevelObject> objList) {

        for (LevelObject obj : objList) {
            String meshName = null;
            if (obj.meshName != null) {
                meshName = obj.meshName;
            } else if (obj.type == LevelObject.LevelObjectType.PLAYER_SHIP) {
                meshName = getPlayerShipData().filename;
                // Hack for entity creation in WorldManager.createEntity();
                obj.meshName = meshName;
            }
            if (meshName == null) {
                throw new ENG_InvalidFormatParsingException(
                        "You did not put a " + "mesh type in " + obj.name);
            }
//            ENG_ModelResource data = MainApp.getGame().getResource(FilenameUtils.getBaseName(meshName));//getObjectData(meshName);

            boolean flag = false;
            switch (obj.type) {
                case FLAG_RED:
                case FLAG_BLUE:

                    flag = true;
                    break;
            }
            String extension = FilenameUtils.getExtension(obj.meshName);
            if (extension.isEmpty()) {
                obj.meshName = obj.meshName + ".mesh";
            }
            if (obj.type == LevelObject.LevelObjectType.STATIC) {
                createStaticEntity(FilenameUtils.getBaseName(obj.meshName), obj);
            } else {
                createEntity(FilenameUtils.getBaseName(obj.meshName)/*data.name*/, obj);
            }
            if (flag) {
                currentBeacon = getEntityByGameEntityId(currentBeaconId);
                entityPropertiesComponentMapper.get(currentBeacon).setIgnoringCollision(true);
            }
        }
    }

    protected void addLevelObjectByName(String name, LevelEntity levelEntity) {
        if (levelObjectToEntityMap.put(name, levelEntity) != null) {
            throw new IllegalArgumentException(name + " is already a created entity name");
        }
    }

    protected void addLevelObjectById(long entityId, LevelEntity levelEntity) {
        if (levelObjectIdToEntityMap.put(entityId, levelEntity) != null) {
            throw new IllegalArgumentException(entityId + " is already a created entity id");
        }
    }

    protected void createStaticPhysicsBody(Entity gameEntity, StaticEntityProperties staticEntityProperties,
                                           StaticEntityMotionState motionState,
                                           ArrayList<btBvhTriangleMeshShape> triangleMeshShape,
                                           short collisionGroup, short collisionMask) {
        for (btBvhTriangleMeshShape meshShape : triangleMeshShape) {
            btRigidBody.btRigidBodyConstructionInfo constructionInfo =
                    PhysicsUtility.createConstructionInfo(0.0f, motionState, meshShape, null);

            StaticEntityRigidBody rigidBody = PhysicsUtility.createStaticEntityRigidBody(constructionInfo, gameEntity);

            staticEntityProperties.setMotionState(motionState);
            staticEntityProperties.setRigidBody(rigidBody);
            staticEntityProperties.setContructionInfo(constructionInfo);
            staticEntityProperties.setCollisionShape(meshShape);

            staticEntityProperties.setCollisionGroup(collisionGroup);
            staticEntityProperties.setCollisionMask(collisionMask);

            btDiscreteDynamicsWorld btDiscreteDynamicsWorld = MainApp.getGame().getBtDiscreteDynamicsWorld();
            PhysicsUtility.addRigidBody(btDiscreteDynamicsWorld, rigidBody, collisionGroup, collisionMask);
            PhysicsUtility.setGravity(rigidBody, ENG_Math.VEC3_ZERO);
//            PhysicsUtility.setFriction(rigidBody, RIGID_BODY_FRICTION);
        }
    }

    protected void createPhysicsBody(Entity gameEntity, ENG_Vector3D halfSize,
                                     EntityProperties entityProp, EntityMotionState motionState,
                                     short collisionGroup, short collisionMask, float weight) {
        createPhysicsBody(gameEntity, halfSize, entityProp, motionState, collisionGroup, collisionMask, weight, null);
    }

    protected void createPhysicsBody(Entity gameEntity, ENG_Vector3D halfSize,
                                     EntityProperties entityProp, EntityMotionState motionState,
                                     short collisionGroup, short collisionMask, float weight,
                                     ENG_Vector3D localInertia) {
        createPhysicsBody(gameEntity, halfSize, entityProp, motionState,
                collisionGroup, collisionMask, weight, localInertia, PhysicsProperties.RigidBodyType.ENTITY);
    }

    protected void createPhysicsBody(Entity gameEntity, ENG_Vector3D halfSize,
                                     EntityProperties entityProp, EntityMotionState motionState,
                                     short collisionGroup, short collisionMask, float weight,
                                     ENG_Vector3D localInertia, PhysicsProperties.RigidBodyType rigidBodyType) {
        btCollisionShape boxCollisionShape = null;
        switch (collisionShapeType) {
            case BOX:
                boxCollisionShape = PhysicsUtility.createBoxCollisionShape(halfSize);
                break;
            case CAPSULE_Z:
                boxCollisionShape = PhysicsUtility.createCapsuleCollisionShapeZ(halfSize.x, halfSize.z - halfSize.x);
                break;
            case CAPSULE:
//                break;
            case CAPSULE_X:
//                break;
            case CYLINDER:
//                break;
            case BVH_TRIANGLE_MESH:
//                break;
            default:
                throw new IllegalStateException("Unexpected value: " + collisionShapeType);
        }

        float finalWeight = weight;
        if (weight == Float.POSITIVE_INFINITY) {
            finalWeight = 0;
        }
        btRigidBody.btRigidBodyConstructionInfo constructionInfo =
                PhysicsUtility.createConstructionInfo(finalWeight, motionState, boxCollisionShape, localInertia);
        EntityRigidBody rigidBody = null;
        switch (rigidBodyType) {
            case ENTITY:
                rigidBody = PhysicsUtility.createEntityRigidBody(constructionInfo, gameEntity);
                break;
            case DEBRIS:
                rigidBody = PhysicsUtility.createDebrisRigidBody(constructionInfo, gameEntity);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + rigidBodyType);
        }
        if (weight == Float.POSITIVE_INFINITY) {
            rigidBody.setCollisionFlags(btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
        }

        entityProp.setMotionState(motionState);
        entityProp.setRigidBody(rigidBody);
        entityProp.setRigidBodyType(rigidBodyType);
        entityProp.setContructionInfo(constructionInfo);
        entityProp.setCollisionShape(boxCollisionShape);
        entityProp.setCollisionShapeType(collisionShapeType);

        entityProp.setCollisionGroup(collisionGroup);
        entityProp.setCollisionMask(collisionMask);

        btDiscreteDynamicsWorld btDiscreteDynamicsWorld = MainApp.getGame().getBtDiscreteDynamicsWorld();
        PhysicsUtility.addRigidBody(btDiscreteDynamicsWorld, rigidBody, collisionGroup, collisionMask);
        PhysicsUtility.setGravity(rigidBody, ENG_Math.VEC3_ZERO);
//        PhysicsUtility.setFriction(rigidBody, RIGID_BODY_FRICTION);
    }

    public abstract void loadLevel();

    public void loadWaypoints() {
        Level level = (Level) gameWorld.getCurrentLevel();
        WaypointSystem.createWaypoints(level);
        // Don't make the waypoints visible on the server as it's headless anyway.
        if (WaypointSystem.DEBUG && MainApp.getApplicationMode() == MainApp.Mode.CLIENT) {
            WaypointSystem.getSingleton().setVisible(true);
        }
    }

    protected void createDefaultComparatorNode(ComparatorNode node,
                                               ArrayList<String> list) {
        if (node.op == null && !list.isEmpty()) {
            node.op = ComparatorOperator.AND;
            node.leaves = new ArrayList<>();
            for (String s : list) {
                ComparatorNode newNode = new ComparatorNode();
                newNode.s = s;
                node.leaves.add(newNode);
            }
        }
    }

    protected void createEndCondComparatorNodes(ComparatorNode node,
                                                ArrayList<LevelEndCond.EndCond> list) {
        if (node.op == null && !list.isEmpty()) {
            node.op = ComparatorOperator.AND;
            node.leaves = new ArrayList<>();
            for (LevelEndCond.EndCond endCond : list) {
                ComparatorNode newNode = new ComparatorNode();
                newNode.s = endCond.name;
                node.leaves.add(newNode);
            }
        }
    }

    public static long getDelay(LevelEvent.DelayType type, long delay) {
        if (type == LevelEvent.DelayType.SECS) {
            delay *= 1000;
        }
        return delay;
    }

    public LevelEntity getLevelEntity(String name) {
        return levelObjectToEntityMap.get(name);
    }

    public ArrayList<LevelEntity> getLevelEntities() {
        return new ArrayList<>(levelObjectToEntityMap.values());
    }

    public Entity getPlayerShip() {
        // System.out.println("PlayerShip name: " +
        // playerShip.getComponent(ShipProperties.class).getName());
        return getEntityByGameEntityId(playerShipEntityId);
    }

    public void addEntityByGameEntityId(Long gameEntityId, Entity entity) {
        gameEntityIdsToEntities.put(gameEntityId, entity);
//        System.out.println("gameEntityIdsToEntities size: " + gameEntityIdsToEntities.size());
    }

    public boolean removeEntityByGameEntityId(Long gameEntityId) {
//        System.out.println("Removing entity by gameEntityId: " + gameEntityId);
        return gameEntityIdsToEntities.remove(gameEntityId) != null;
    }

    public Entity getEntityByGameEntityId(Long gameEntityId) {
        return gameEntityIdsToEntities.get(gameEntityId);
    }

    public boolean removeFromShipListByGameEntityId(Long gameEntityId) {
//        System.out.println("Removing ship by gameEntityId: " + gameEntityId);
        return gameEntityIdsToShips.remove(gameEntityId) != null;
    }

    public Entity getShipByGameEntityId(Long gameEntityId) {
        return gameEntityIdsToShips.get(gameEntityId);
    }

    public void addEntityByItemId(Long itemId, Entity entity) {
        itemIdsToEntities.put(itemId, entity);
//        System.out.println("itemIdsToEntities size: " + itemIdsToEntities.size());
    }

    public boolean removeEntityByItemId(Long itemId) {
//        System.out.println("Removing entity by itemId: " + itemId);
        return itemIdsToEntities.remove(itemId) != null;
    }

    public Entity getEntityByItemId(Long itemId) {
        return itemIdsToEntities.get(itemId);
    }

    public ArrayList<Entity> getAllEntities() {
        return new ArrayList<>(itemIdsToEntities.values());
    }

    public void addItemIdByEntityId(Long entityId, Long itemId) {
        entityIdsToItemIds.put(entityId, itemId);
//        System.out.println("entityIdsToItemIds size: " + entityIdsToItemIds.size());
    }

    public boolean removeItemIdByEntityId(Long entityId) {
//        System.out.println("Removing itemId by entityId: " + entityId);
        return entityIdsToItemIds.remove(entityId) != null;
    }

    public Long getItemIdByEntityId(Long id) {
        return entityIdsToItemIds.get(id);
    }

    public void addWaypointId(long id) {
        if (!waypointIds.add(id)) {
            throw new IllegalArgumentException(id + " already added to waypointIds");
        }
    }

    public void removeWaypointId(long id) {
        if (!waypointIds.remove(id)) {
            throw new IllegalArgumentException(id + " not found in waypointIds");
        }
    }

    public void removeAllWaypointIds() {
        waypointIds.clear();
    }

    public boolean getWaypointId(long id) {
        return waypointIds.contains(id);
    }

    public HashSet<Long> getWaypointIds() {
        return waypointIds;
    }

    public void addStaticObjectId(long id) {
        if (!staticObjectIds.add(id)) {
            throw new IllegalArgumentException(id + " already added to staticObjectIds");
        }
    }

    public void removeStaticObjectId(long id) {
        if (!staticObjectIds.remove(id)) {
            throw new IllegalArgumentException(id + " not found in staticObjectIds");
        }
    }

    public void removeAllStaticObjectIds() {
        staticObjectIds.clear();
    }

    public boolean getStaticObjectId(long id) {
        return staticObjectIds.contains(id);
    }

    public HashSet<Long> getStaticObjectIds() {
        return staticObjectIds;
    }

    //    public Entity getById(int id) {
//        Entity entity = entityMap.get(id);
//        if (entity == null) {
//            throw new IllegalArgumentException(id
//                    + " does not exist in this world");
//        }
//        return entity;
//    }

    public int getNumCargosInLevel() {
        return cargoIdList.size();
    }

    protected void updateNumCargosInLevel(Long toRemove) {
        if (!cargoIdList.remove(toRemove)) {
            throw new IllegalArgumentException(toRemove + " is not a valid cargo name");
        }
    }

    public int getNumCargosScanned() {
        int scanned = 0;
        for (Long id : cargoIdList) {
            Entity entity = getEntityByGameEntityId(id);
            if (entity != null) {
                CargoProperties cargoProperties = cargoPropertiesComponentMapper.getSafe(entity);
                if (cargoProperties != null) {
                    if (cargoProperties.isScanned()) {
                        ++scanned;
                    }
                } else {
                    throw new ENG_InvalidFieldStateException("Cargo " + id + " is not a valid cargo type");
                }
            } else {
                throw new NullPointerException(id + " is no longer a valid cargo entity");
            }
        }
        return scanned;
    }

    public int getNumCargosNotScanned() {
        return getNumCargosInLevel() - getNumCargosScanned();
    }

    public void startAnimation(Long entityId, Animation anim) {
        if (anim.getAnimationState() == Animation.AnimationState.STARTABLE) {
            animationList.add(anim);
            HashMap<String, Animation> stringAnimationHashMap = animationMap.get(entityId);
            if (stringAnimationHashMap == null) {
                stringAnimationHashMap = new HashMap<>();
                animationMap.put(entityId, stringAnimationHashMap);
            }
            stringAnimationHashMap.put(anim.getName(), anim);
            anim.start();
        }
    }

    public void createCountermeasures(Entity ship) {
        ShipProperties shipProperties = shipPropertiesComponentMapper.get(ship);
        AnimationFactory factory = shipProperties.getCountermeasuresAnimationFactory();
        if (factory != null) {
//            System.out.println("WorldManagerBase createCountermeasures for " + entityPropertiesComponentMapper.get(ship).getName() + " isCounterMeasuresLaunched: " + shipProperties.isCountermeasureLaunched());
            // We should allow countermeasures to be launched even if the animation is already started.
            // This fixes the issue when the time for countermeasures cooldown has passes
            // but the previous animation hasn't been disposed by the animation garbage collector.
            // This also means that the caller of this method must make sure that the cooldown
            // has truly expired.
//            if (!shipProperties.isCountermeasureLaunched()) {
                startAnimation(entityPropertiesComponentMapper.get(ship).getEntityId(), factory.createInstance(ship));
//            }
        } else {
            throw new NullPointerException("CountermeasureAnimationFactory should never be null");
        }
    }

    protected void animateShipSpawn(ArrayList<LevelObject> spawnList) {
        for (LevelObject obj : spawnList) {
            Entity entity = getLevelObject(obj.name);
            // Animate only ships
            animateShipSpawn(entity);
        }
    }

    protected abstract void animateShipSpawn(Entity entity);

    protected void animateShipExit(ArrayList<String> spawnList) {
        for (String name : spawnList) {
            Entity entity = getLevelObject(name);
            // Animate only ships
            animateShipExit(entity);
        }
    }

    protected abstract void animateShipExit(Entity entity);

    public abstract void createProjectile(Entity ship);

    /**
     * Make sure to only call this from createProjectile()
     * @param shipProperties
     * @return
     */
    protected boolean checkShipDestroyed(ShipProperties shipProperties) {
        long currentSelectedEnemy = shipProperties.getCurrentSelectedEnemy();
        if (currentSelectedEnemy != -1) {
            Entity ship = getEntityByItemId(currentSelectedEnemy);
            if (ship != null) {
                if (entityPropertiesComponentMapper.get(ship).isDestroyed()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected MultiplayerEntityTCP addMultiplayerComponentsToEntity(long entityId, Entity gameEntity) {
//        MultiplayerEntity multiplayerEntity = new MultiplayerEntity(entityId);
//        MultiplayerEntityTCP multiplayerEntityTCP = new MultiplayerEntityTCP(/*entityProperties.getEntityId()*/);
//        MultiplayerEntityUDP multiplayerEntityUDP = new MultiplayerEntityUDP(entityId, gameEntity.getComponent(EntityProperties.class).getName());

        GameWorld world = GameWorld.getWorld();
//        ComponentMapper<MultiplayerEntity> multiplayerEntityComponentMapper = world.getMapper(MultiplayerEntity.class);
//        ComponentMapper<MultiplayerEntityTCP> multiplayerEntityTCPComponentMapper = world.getMapper(MultiplayerEntityTCP.class);
//        ComponentMapper<MultiplayerEntityUDP> multiplayerEntityUDPComponentMapper = world.getMapper(MultiplayerEntityUDP.class);

        MultiplayerEntity multiplayerEntity = multiplayerEntityComponentMapper.create(gameEntity);
        multiplayerEntity.setEntityId(entityId);
        MultiplayerEntityTCP multiplayerEntityTCP = multiplayerEntityTCPComponentMapper.create(gameEntity);
        MultiplayerEntityUDP multiplayerEntityUDP = multiplayerEntityUDPComponentMapper.create(gameEntity);
        multiplayerEntityUDP.setEntityId(entityId);
        multiplayerEntityUDP.setEntityName(world.getMapper(EntityProperties.class).getSafe(gameEntity).getName());
//        gameEntity.addComponent(multiplayerEntity);
//        gameEntity.addComponent(multiplayerEntityTCP);
//        gameEntity.addComponent(multiplayerEntityUDP);
        return multiplayerEntityTCP;
    }

    protected abstract Entity createProjectileEntity(long id,//WeaponProperties weaponProperties,
                                                     EntityProperties entityProperties,
                                                     ShipProperties shipProperties,// WeaponData.WeaponType weaponType,
                                                     String meshName, WeaponData weaponData, ENG_Vector4D pos,
                                                     ENG_Quaternion orientation, boolean tracking);

    protected Entity createProjectileEntity(WeaponProperties weaponProperties,
                                            EntityProperties entityProperties,
                                            ShipProperties shipProperties, //WeaponData.WeaponType weaponType,
                                            String meshName, WeaponData weaponData, ENG_Vector4D pos,
                                            boolean tracking) {
        return createProjectileEntity(weaponProperties.getNextId(), entityProperties, shipProperties, //weaponType,
                meshName, weaponData, pos, currentShipOrientation,
                tracking);
    }

    protected Entity createProjectileEntity(WeaponProperties weaponProperties,
                                            EntityProperties entityProperties,
                                            ShipProperties shipProperties, //WeaponData.WeaponType weaponType,
                                            String meshName, WeaponData weaponData, ENG_Vector4D pos) {
        return createProjectileEntity(weaponProperties.getNextId(), entityProperties, shipProperties, //weaponType,
                meshName, weaponData, pos, currentShipOrientation, false);
    }

    private Entity createProjectileEntity(WeaponProperties weaponProperties,
                                          EntityProperties entityProperties,
                                          ShipProperties shipProperties, //WeaponData.WeaponType weaponType,
                                          String meshName, WeaponData weaponData, ENG_Vector4D pos,
                                          ENG_Quaternion orientation) {
        return createProjectileEntity(weaponProperties.getNextId(), entityProperties, shipProperties, //weaponType,
                meshName, weaponData, pos, orientation, false);
    }

    protected void createProjectileEntityByType(EntityProperties entityProperties,
                                                ShipProperties shipProperties,
                                                WeaponProperties weaponProperties,
//                                                WeaponData.WeaponType weaponType,
                                                final WeaponData weaponData,
                                                ArrayList<Entity> createdProjectileList) {
        switch (weaponData.weaponType) {
            case LASER_GREEN:
            case LASER_RED: {

                ENG_Vector4D leftPos = currentShipPosition.addAsPt(currentShipOrientation.mul(laserLeftOffset));
                ENG_Vector4D rightPos = currentShipPosition.addAsPt(currentShipOrientation.mul(laserRightOffset));
                for (int i = 0; i < 2; ++i) {
                    Entity projectileEntity = createProjectileEntity(weaponProperties, entityProperties, shipProperties,  weaponData.filename,
                            weaponData, i == 0 ? leftPos : rightPos);
                    addToProjectileList(createdProjectileList, projectileEntity);

                }
            }
            break;
            case PLASMA: {
                ENG_Vector4D leftPos = currentShipPosition.addAsPt(currentShipOrientation.mul(plasmaLeftOffset));
                ENG_Vector4D rightPos = currentShipPosition.addAsPt(currentShipOrientation.mul(plasmaRightOffset));
                for (int i = 0; i < 2; ++i) {
                    Entity projectileEntity = createProjectileEntity(weaponProperties, entityProperties, shipProperties, weaponData.filename,
                            weaponData, i == 0 ? leftPos : rightPos);
                    addToProjectileList(createdProjectileList, projectileEntity);

                }
            }
            break;
            case LASER_GREEN_QUAD:
            case LASER_RED_QUAD: {
                ENG_Vector4D leftPos = currentShipPosition.addAsPt(currentShipOrientation.mul(laserLeftOffset));
                ENG_Vector4D rightPos = currentShipPosition.addAsPt(currentShipOrientation.mul(laserRightOffset));
                ENG_Vector4D quadLeftPos = currentShipPosition.addAsPt(currentShipOrientation.mul(quadLaserLeftOffset));
                ENG_Vector4D quadRightPos = currentShipPosition.addAsPt(currentShipOrientation.mul(quadLaserRightOffset));

                Entity projectileEntity = createProjectileEntity(weaponProperties, entityProperties, shipProperties, weaponData.filename, weaponData, leftPos);
                Entity projectileEntity1 = createProjectileEntity(weaponProperties, entityProperties, shipProperties, weaponData.filename, weaponData, rightPos);
                Entity projectileEntity2 = createProjectileEntity(weaponProperties, entityProperties, shipProperties, weaponData.filename, weaponData, quadLeftPos);
                Entity projectileEntity3 = createProjectileEntity(weaponProperties, entityProperties, shipProperties, weaponData.filename, weaponData, quadRightPos);
                addToProjectileList(createdProjectileList, projectileEntity);
                addToProjectileList(createdProjectileList, projectileEntity1);
                addToProjectileList(createdProjectileList, projectileEntity2);
                addToProjectileList(createdProjectileList, projectileEntity3);
            }
            break;
            case CONCUSSION: {
                ENG_Vector4D pos = currentShipPosition.addAsPt(currentShipOrientation.mul(concussionOffset));
                Entity projectileEntity = createProjectileEntity(weaponProperties, entityProperties, shipProperties, weaponData.filename, weaponData, pos);
                addToProjectileList(createdProjectileList, projectileEntity);
            }
            break;
            case HOMING: {
                ENG_Vector4D pos = currentShipPosition.addAsPt(currentShipOrientation.mul(homingOffset));
                Entity projectileEntity = createProjectileEntity(weaponProperties, entityProperties, shipProperties, weaponData.filename, weaponData, pos, true);
                addToProjectileList(createdProjectileList, projectileEntity);
            }
            break;
            case MEGA: {
                ENG_Vector4D pos = currentShipPosition.addAsPt(currentShipOrientation.mul(megaOffset));
                Entity projectileEntity = createProjectileEntity(weaponProperties, entityProperties, shipProperties, weaponData.filename, weaponData, pos, true);
                addToProjectileList(createdProjectileList, projectileEntity);
            }
            break;
            case PIRANHA: {
//                ENG_Mesh mesh = ENG_MeshManager.getSingleton().getByName(weaponData.filename);
//                ENG_AxisAlignedBox bounds = mesh.getBounds();
//
//                // Get the minimum distance between missiles as we do not want them
//                // to
//                // overlap
//                ENG_Vector4D diff = bounds.getMax().subAsPt(bounds.getMin());
//                // Help'em a bit
//                diff.x += 0.1f;
//                diff.y += 0.1f;
//                diff.z = 0.0f;

                ENG_Vector4D pos = currentShipPosition.addAsPt(currentShipOrientation.mul(piranhaOffset));

                String shipName = entityProperties.getNode().getName();

                for (int i = 0; i < PIRANHA_MISSILE_NUM; ++i) {
                    ENG_Vector4D piranhaPos = pos.addAsPt(new ENG_Vector4D(
                            ENG_Utility.getRandom().nextFloat(FrameInterval.PIRANHA_CREATE_PROJ_RAND_X_POS + shipName + "_" + i)
                                    * PIRANHA_MISSILE_POSITION_OFFSET
                                    - PIRANHA_MISSILE_POSITION_HALF_OFFSET,
                            ENG_Utility.getRandom().nextFloat(FrameInterval.PIRANHA_CREATE_PROJ_RAND_Y_POS + shipName + "_" + i)
                                    * PIRANHA_MISSILE_POSITION_OFFSET
                                    - PIRANHA_MISSILE_POSITION_HALF_OFFSET, 0.0f,
                            1.0f));
                    ENG_Quaternion tempRot = currentShipOrientation
                            .mulRet(ENG_Quaternion.fromAngleAxisDegRet(
                                    ENG_Utility.getRandom().nextFloat(FrameInterval.PIRANHA_CREATE_PROJ_RAND_X_ROT + shipName + "_" + i)
                                            * PIRANHA_MISSILE_ORIENTATION_OFFSET
                                            - PIRANHA_MISSILE_ORIENTATION_HALF_OFFSET,
                                    ENG_Math.VEC4_X_UNIT));
                    ENG_Quaternion piranhaRot = tempRot.mulRet(ENG_Quaternion
                            .fromAngleAxisDegRet(ENG_Utility.getRandom().nextFloat(FrameInterval.PIRANHA_CREATE_PROJ_RAND_Y_ROT + shipName + "_" + i)
                                            * PIRANHA_MISSILE_ORIENTATION_OFFSET
                                            - PIRANHA_MISSILE_ORIENTATION_HALF_OFFSET,
                                    ENG_Math.VEC4_Y_UNIT));
                    Entity projectileEntity = createProjectileEntity(weaponProperties.getNextId(), entityProperties, shipProperties, weaponData.filename, weaponData,
                            piranhaPos, piranhaRot, true);
                    EntityProperties projEntityProp = entityPropertiesComponentMapper.get(projectileEntity);
                    addedProjectilesPerPlayer.add(projEntityProp.getRigidBody());
                    addToProjectileList(createdProjectileList, projectileEntity);
                }
                setAddedProjectilesToIgnoreSelfCollision();
            }
            break;
            default:
                // Should never get here
                throw new IllegalArgumentException(weaponData.weaponType + " type not supported");
        }
    }

    private void addToProjectileList(ArrayList<Entity> createdProjectileList, Entity projectileEntity) {
        if (createdProjectileList != null) {
            createdProjectileList.add(projectileEntity);
        }
    }

    public void clearAddedProjectilesPerPlayer() {
        addedProjectilesPerPlayer.clear();
    }

    public ArrayList<btRigidBody> getAddedProjectilesPerPlayer() {
        return addedProjectilesPerPlayer;
    }

    public void setAddedProjectilesToIgnoreSelfCollision() {
        ArrayList<btRigidBody> addedProjectilesPerPlayer = getAddedProjectilesPerPlayer();
        for (int i = 0; i < addedProjectilesPerPlayer.size(); ++i) {
            btRigidBody removed = addedProjectilesPerPlayer.remove(0);
            for (int j = 0; j < addedProjectilesPerPlayer.size(); ++j) {
                addedProjectilesPerPlayer.get(j).setIgnoreCollisionCheck(removed, true);
            }
            addedProjectilesPerPlayer.add(removed);
        }
        clearAddedProjectilesPerPlayer();
    }

    protected boolean createProjectileFromShip(Entity ship, ArrayList<Entity> createdProjectileList) {
        EntityProperties entityProperties = entityPropertiesComponentMapper.get(ship);
        ShipProperties shipProperties = shipPropertiesComponentMapper.get(ship);
        WeaponProperties weaponProperties = weaponPropertiesComponentMapper.getSafe(ship);
        if (weaponProperties == null) {
            throw new IllegalArgumentException(entityProperties.getNode().getName() + " is not a valid ship");
        }
        if (!weaponProperties.hasCurrentWeaponAmmo() && weaponProperties.getCurrentWeaponAmmo() != WeaponData.INFINITE_AMMO) {
            return false;
        }
        WeaponData.WeaponType weaponType = weaponProperties.getCurrentWeaponType();
//        EntityData weaponFileData = getWeaponData(weaponType);
        WeaponData weaponData = WeaponData.getWeaponData(weaponType);


        entityProperties.getNode().getPosition(currentShipPosition);
        entityProperties.getNode().getOrientation(currentShipOrientation);

        createProjectileEntityByType(entityProperties, shipProperties, weaponProperties, weaponData, createdProjectileList);
        return true;
    }

    public abstract void createReloaderEntity();

    public void getClosestObject(ENG_Vector4D pos, Iterator<Long> iterator,
                                 ENG_ClosestObjectData data) {
        getClosestObjectSquaredDistance(pos, iterator, data);
        data.minDist = ENG_Math.sqrt(data.minDist);
    }

    public void getClosestObjectSquaredDistance(ENG_Vector4D pos, Iterator<Long> iterator, ENG_ClosestObjectData data) {
        float minSquaredDist = Float.MAX_VALUE;
        Long minDistProjectile = null;
        while (iterator.hasNext()) {
            long next = iterator.next();
            Entity projectileEntity = getEntityByGameEntityId(next);
            if (projectileEntity != null) {
                EntityProperties projectileEntityProperties = entityPropertiesComponentMapper.get(projectileEntity);
                projectileEntityProperties.getNode().getPosition(otherPos);

                float squaredLength = pos.squaredDistance(otherPos);
                if (squaredLength < minSquaredDist) {
                    minSquaredDist = squaredLength;
                    minDistProjectile = next;
                }
            }
        }
        data.minDist = minSquaredDist;
        data.objectId = minDistProjectile;
    }

    public ArrayList<ENG_ClosestObjectData> getClosestObjectsByDistance(ENG_Vector4D pos, Iterator<Long> iterator) {
        ArrayList<ENG_ClosestObjectData> outputList = new ArrayList<>();
        getClosestObjectsByDistance(pos, iterator, outputList);
        return outputList;
    }

    public void getClosestObjectsByDistance(ENG_Vector4D pos, Iterator<Long> iterator, ArrayList<ENG_ClosestObjectData> outputList) {
        while (iterator.hasNext()) {
            Long next = iterator.next();
            Entity entity = getEntityByGameEntityId(next);
            if (entity != null) {
                EntityProperties projectileEntityProperties = entityPropertiesComponentMapper.get(entity);
                outputList.add(new ENG_ClosestObjectData(pos.distance(projectileEntityProperties.getNode().getPosition()), next));
            }
        }
        Collections.sort(outputList);
    }

    /**
     * Not thread safe!!!
     *
     * @param shipProperties
     * @return
     */
    public ENG_ClosestObjectData getClosestProjectileSquaredDistance(
            ENG_Vector4D pos, ShipProperties shipProperties) {
        ENG_ClosestObjectData data = new ENG_ClosestObjectData();
        getClosestProjectileSquaredDistance(pos, shipProperties, data);
        return data;
    }

    public void getClosestProjectileSquaredDistance(ENG_Vector4D pos, ShipProperties shipProperties, ENG_ClosestObjectData data) {
        Iterator<Long> iterator = shipProperties.getChasingProjectilesIterator();
        getClosestObjectSquaredDistance(pos, iterator, data);
    }

    /**
     * Not thread safe!!!
     *
     * @param shipProperties
     * @return
     */
    public ENG_ClosestObjectData getClosestProjectile(ENG_Vector4D pos,
                                                  ShipProperties shipProperties) {
        ENG_ClosestObjectData data = new ENG_ClosestObjectData();
        getClosestProjectile(pos, shipProperties, data);
        return data;
    }

    public void getClosestProjectile(ENG_Vector4D pos,
                                     ShipProperties shipProperties, ENG_ClosestObjectData data) {
        getClosestProjectileSquaredDistance(pos, shipProperties, data);
        data.minDist = ENG_Math.sqrt(data.minDist);
    }

    public ArrayList<ENG_ClosestObjectData> getClosestProjectilesByDistance(ENG_Vector4D pos, ShipProperties shipProperties) {
        ArrayList<ENG_ClosestObjectData> outputList = new ArrayList<>();
        getClosestProjectilesByDistance(pos, shipProperties, outputList);
        return outputList;
    }

    public void getClosestProjectilesByDistance(ENG_Vector4D pos,
                                                ShipProperties shipProperties, ArrayList<ENG_ClosestObjectData> outputList) {
        Iterator<Long> iterator = shipProperties.getChasingProjectilesIterator();
        getClosestObjectsByDistance(pos, iterator, outputList);
    }

    public Entity getLevelObject(String name) {
        LevelEntity levelEntity = getLevelEntity(name);
        if (levelEntity != null) {
            return levelEntity.getEntity();
        }
        return null;
    }

    public abstract void destroyEntity(EntityProperties entityProperties);

    public ShipData getPlayerShipData() {
        return playerShipData;
    }

    protected void loadReloader(LevelBase level) {
        if (level.getLevelStart().reloaderAllowed) {
            // Nothing needed to be done anymore.
//            loadResources(getReloaderData());
        } else {
            reloaderDisabled = true;
        }
    }

//    protected ENG_ModelResource getReloaderData() {
//        return MainApp.getGame().getShipResource("reloader");
//    }

    protected void addToShipList(long entityId, Entity gameEntity) {
        System.out.println("Adding to shipList entityId: " + entityId);
        Entity put = gameEntityIdsToShips.put(entityId, gameEntity);
        System.out.println("gameEntityIdsToShips size: " + gameEntityIdsToShips.size());
        if (put != null) {
            throw new IllegalArgumentException(entityId + " is already " + "in the ship list");
        }
    }

    protected void setShipWeapons(Entity gameEntity, ShipData shipData) {
        WeaponProperties wpnProps = weaponPropertiesComponentMapper.create(gameEntity);
        setShipWeaponProperties(shipData, wpnProps);
    }

    protected void setShipWeaponProperties(ShipData shipData, WeaponProperties wpnProps) {
        wpnProps.addWeapon(shipData.weaponTypeList);
        if (!shipData.weaponTypeList.isEmpty()) {
            wpnProps.setCurrentWeapon(0);
        }
    }

    protected void setShipData(ShipProperties shipProp, ShipData shipData) {
        shipProp.setShipData(shipData);
        shipProp.setAfterburnerMaxSpeedCoeficient(shipData.afterburnerMaxSpeedCoeficient);
        shipProp.setAfterburnerTime(shipData.afterburnerTime);
        shipProp.setAfterburnerCooldownTime(shipData.afterburnerCooldownTime);
    }

    protected void setWeight(EntityProperties entityProp, LevelObject.LevelObjectType type) {

        switch (type) {
            case ASTEROID:
//                entityProp.setWeight(200.0f);
                throw new IllegalArgumentException("Asteroid weight should not be set here");
            case FIGHTER_SHIP:
            case PLAYER_SHIP:
//            case PLAYER_SHIP_SELECTION:
//                entityProp.setWeight(1.0f);
                throw new IllegalArgumentException("Ship weight should not be set here");
            case CARGO_SHIP:
//                entityProp.setWeight(50000.0f);
                throw new IllegalArgumentException("CargoShip weight should not be set here");
            case FLAG_BLUE:
            case FLAG_RED:
            case WAYPOINT:
                entityProp.setWeight(1.0f);
                break;
            case CARGO:
                entityProp.setWeight(100.0f);
                break;
            case STATIC:
                entityProp.setWeight(Float.POSITIVE_INFINITY);
            default:
                throw new IllegalArgumentException(type + " not found as a valid object" + " type");
        }
    }

    public ENG_SceneManager getSceneManager() {
        return sceneManager;
    }

    public void setSceneManager(ENG_SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public LevelState getLevelState() {
        return levelState;
    }

    public void setLevelState(LevelState levelState) {
        this.levelState = levelState;
    }

    protected void updateAnimations() {
        for (Animation anim : animationList) {
            anim.updateAnimation();
            if (anim.getAnimationState() == Animation.AnimationState.FINISHED) {
                // If repeatable it never gets to finished so no worry
//                System.out.println("Removing animation: " + anim.getName());
                animationsToRemove.add(anim);
            }
        }
        for (Animation anim : animationsToRemove) {
            animationList.remove(anim);
        }
        animationsToRemove.clear();

        for (Map.Entry<Long, HashMap<String, Animation>> entityIdToAnimation : animationMap.entrySet()) {
            for (Animation animation : entityIdToAnimation.getValue().values()) {
                if (animation.getAnimationState() == Animation.AnimationState.FINISHED) {
                    ArrayList<String> animationNames = animationsEntityIdToRemove.get(entityIdToAnimation.getKey());
                    if (animationNames == null) {
                        animationNames = new ArrayList<>();
                        animationsEntityIdToRemove.put(entityIdToAnimation.getKey(), animationNames);
                    }
                    animationNames.add(animation.getName());
                }
            }
        }
        for (Map.Entry<Long, ArrayList<String>> entityIdToAnimationNames : animationsEntityIdToRemove.entrySet()) {
            HashMap<String, Animation> nameToAnimation = animationMap.get(entityIdToAnimationNames.getKey());
            for (String animationName : entityIdToAnimationNames.getValue()) {
                nameToAnimation.remove(animationName);
            }
            if (nameToAnimation.isEmpty()) {
                animationMap.remove(entityIdToAnimationNames.getKey());
            }
        }

        animationsEntityIdToRemove.clear();


    }

    public static ENG_SceneNode createCameraNode(String nodeName, ENG_SceneManager sceneManager) {
        ENG_Camera camera = sceneManager.getCamera(APP_Game.MAIN_CAM);
        ENG_CameraNative cameraNative = new ENG_CameraNative(camera);
        ENG_SceneNode cameraNode = sceneManager.getRootSceneNode().createChildSceneNode(nodeName);
        cameraNative.detachFromParent();
        cameraNode.attachObject(cameraNative);
        return cameraNode;
    }

    public static void destroyCameraNode(String nodeName, ENG_SceneManager sceneManager, ENG_SceneNode currentCameraNode) {
        // It means we also have a camera
        ENG_SceneNode cameraNode = (ENG_SceneNode) sceneManager.getRootSceneNode().getChild(nodeName);
        if (currentCameraNode != cameraNode) {
            throw new IllegalStateException("The camera node is different from one in the WorldManager local nodeName: " +
                    nodeName + " cameraNode name: " + currentCameraNode.getName());
        }
        // cameraNode.setPosition(0.0f, 0.0f, 0.0f);
        // cameraNode.setOrientation(ENG_Math.QUAT_IDENTITY);
        cameraNode.detachObject(APP_Game.MAIN_CAM);
        ENG_Camera camera = sceneManager.getCamera(APP_Game.MAIN_CAM);
        // We also need to detach here if we want to avoid setting the
        // position
        // This way we reset the camera to initial position and orientation
        // camera.detachFromParent();

        sceneManager.getRootSceneNode().removeAndDestroyChild(nodeName);
        // In Ogre 2.1 the camera must always be attached to a node or else we can't render!!!
        sceneManager.getRootSceneNode().attachObject(new ENG_CameraNative(camera));

        camera.setPosition(ENG_Math.PT4_ZERO);
        camera.setOrientation(ENG_Math.QUAT_IDENTITY);
    }

    public ENG_SceneNode createCameraNode(String nodeName) {
        cameraNode = createCameraNode(nodeName, sceneManager);
        return cameraNode;
    }

    public void destroyCameraNode(String nodeName) {
        destroyCameraNode(nodeName, sceneManager, this.cameraNode);
        this.cameraNode = null;
    }

    public ENG_Workflows getWorkflow(LevelObject obj) {
        if (obj.type == LevelObject.LevelObjectType.FLAG_BLUE || obj.type == LevelObject.LevelObjectType.FLAG_RED ||
                obj.type == LevelObject.LevelObjectType.ASTEROID) {
            return ENG_Workflows.SpecularWorkflow;
        }
        return ENG_Workflows.MetallicWorkflow;
    }

    public long getCurrentReloaderShipId() {
        return currentReloaderShipId;
    }

    public static void incrementGameEntityId() {
        ++gameEntityId;
    }

    public static WorldManagerBase getSingleton() {
        return mgr;
    }

}
