/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/22/21, 2:35 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.world;

import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;

import headwayent.blackholedarksun.*;
import headwayent.blackholedarksun.animations.*;
import headwayent.blackholedarksun.components.*;
import headwayent.blackholedarksun.entitydata.AsteroidData;
import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.blackholedarksun.entitydata.WeaponData;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.blackholedarksun.input.InGameInputConvertorListener;
import headwayent.blackholedarksun.levelresource.Level;
import headwayent.blackholedarksun.levelresource.LevelBase;
import headwayent.blackholedarksun.levelresource.LevelEvent;
import headwayent.blackholedarksun.levelresource.LevelObject;
import headwayent.blackholedarksun.loaders.LevelLoader;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.parser.ast.Cutscene;
import headwayent.blackholedarksun.physics.PhysicsProperties;
import headwayent.blackholedarksun.physics.EntityMotionState;
import headwayent.blackholedarksun.physics.PhysicsUtility;
import headwayent.blackholedarksun.physics.PlayerShipMotionState;
import headwayent.blackholedarksun.physics.StaticEntityMotionState;
import headwayent.blackholedarksun.statistics.InGameStatistics;
import headwayent.blackholedarksun.statistics.InGameStatisticsManager;
import headwayent.blackholedarksun.statistics.LevelEventStatistics;
import headwayent.blackholedarksun.statistics.LevelStatistics;
import headwayent.blackholedarksun.statistics.SessionStatistics;
import headwayent.blackholedarksun.systems.EntityDeleterSystem;
import headwayent.blackholedarksun.systems.FollowingShipCounterResetSystem;
import headwayent.blackholedarksun.systems.FollowingShipCounterSPSystem;
import headwayent.blackholedarksun.systems.GameLogicEntityRemoverSPSystem;
import headwayent.blackholedarksun.systems.PlayerEntityDestroyedVerifierSPSystem;
import headwayent.blackholedarksun.systems.ProjectileUpdateSPSystem;
import headwayent.blackholedarksun.systems.StaticEntityDeleterSystem;
import headwayent.hotshotengine.*;
import headwayent.hotshotengine.input.ENG_InputManager;
import headwayent.hotshotengine.renderer.*;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;

import static headwayent.hotshotengine.ENG_Utility.currentTimeMillis;

/**
 * Created by sebas on 05.11.2015.
 */
public class WorldManagerSP extends WorldManager {

    private Animation playerShipDestroyedAnimation;
    private int health;
    private int kills;
    private boolean worldResetted;

    public WorldManagerSP() {
        MainApp.getGame().setGameMode(APP_Game.GameMode.SP);
    }


    @Override
    public void prepareLevel() {
        Level level = (Level) gameWorld.getCurrentLevel();
        initializeEndEvents(level);

        // Add all the cargos to a list for use in the hud manager
        for (LevelObject obj : level.levelStart.startObjects) {
            if (obj.type == LevelObject.LevelObjectType.CARGO) {
                cargoIdList.add(cargoNameToIdMap.get(obj.name));
            }
        }

        initializeAmbientSounds();
    }

    @Override
    public void update(long currentTime) {

        updateAutoRotateList();

        if (levelState == LevelState.STARTED) {

            gameWorld.getSystem(FollowingShipCounterResetSystem.class).process();
            gameWorld.getSystem(FollowingShipCounterSPSystem.class).process();

            gameWorld.getSystem(ProjectileUpdateSPSystem.class).process();

            // We need to check if player destroyed before updating the level events. In the updateLevelEvents() we can also stop the level
            // and remove all the entities which means that the PlayerEntityDestroyedVerifierSPSystem can no longer find the player ship.
            gameWorld.getSystem(PlayerEntityDestroyedVerifierSPSystem.class).process();

            // Maybe we just died and we have resetted the world. We can no longer complete the rest of the update cycle.
            if (worldResetted) {
                return;
            }

            updateLevelEvents();

            updateCutsceneEvents();

//            checkPlayerShipDestroyed();


//            removeRemovableEntities();


            updateSounds(currentTime);

            updateAnimations();

//            System.out.println("Updating GameLogicEntityRemoverSPSystem");
            gameWorld.getSystem(GameLogicEntityRemoverSPSystem.class).process();

//            for (Entity entity : entityList) {
//                AIProperties component = entity.getComponent(AIProperties.class);
//                if (component != null) {
//                    ENG_Frame currentFrame = MainApp.getMainThread().getDebuggingState().getCurrentFrame();
//                    ENG_FrameInterval currentFrameInterval = currentFrame.getCurrentFrameInterval();
//                    EntityProperties entityProperties = entity.getComponent(EntityProperties.class);
//                    if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
//                        currentFrameInterval.addObject("AI_STATE " + entityProperties.getNode().getName(), component.getState());
//                        currentFrameInterval.addObject("AI_POSITION " + entityProperties.getNode().getName(), entityProperties.getNode().getPosition());
//                    } else if (MainApp.getMainThread().isInputState()) {
//                        System.out.println("AI_STATE " + entityProperties.getNode().getName() + " " + component.getState());
//                        System.out.println("AI_POSITION " + entityProperties.getNode().getName() + " " + entityProperties.getNode().getPosition());
//                    }
//                }
//            }

        } else if (showDemo) {

            generateRandomShips();

            gameWorld.getSystem(ProjectileUpdateSPSystem.class).process();

//            removeRemovableEntities();
            gameWorld.getSystem(GameLogicEntityRemoverSPSystem.class).process();

            updateAnimations();
        }
    }

    private void generateRandomShips() {

        if (getNumShips() < MAX_DEMO_SHIPS && ENG_Utility.hasRandomChanceHit(FrameInterval.DEMO_SHIP_SPAWN_CHANCE, DEMO_SHIP_SPAWN_CHANCE)) {
            int alienShips = 0;
            int humanShips = 0;
            for (Entity entity : gameEntityIdsToShips.values()) {
                ShipProperties shipProperties = shipPropertiesComponentMapper.get(entity);
                switch (shipProperties.getShipData().team) {
                    case ALIEN:
                        ++alienShips;
                        break;
                    case HUMAN:
                        ++humanShips;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid team: " + shipProperties.getShipData().team);
                }
            }
            ArrayList<headwayent.blackholedarksun.entitydata.ShipData> list = null;
            if (alienShips < MAX_ALIEN_DEMO_SHIPS && humanShips < MAX_HUMAN_DEMO_SHIPS) {
                list = headwayent.blackholedarksun.entitydata.ShipData.getShipData(headwayent.blackholedarksun.entitydata.ShipData.TEAM_ANY, headwayent.blackholedarksun.entitydata.ShipData.TYPE_FIGHTER);

            } else if (alienShips < MAX_ALIEN_DEMO_SHIPS) {
                list = headwayent.blackholedarksun.entitydata.ShipData.getShipData(headwayent.blackholedarksun.entitydata.ShipData.TEAM_ALIEN, headwayent.blackholedarksun.entitydata.ShipData.TYPE_FIGHTER);
            } else if (humanShips < MAX_HUMAN_DEMO_SHIPS) {
                list = headwayent.blackholedarksun.entitydata.ShipData.getShipData(headwayent.blackholedarksun.entitydata.ShipData.TEAM_HUMAN, headwayent.blackholedarksun.entitydata.ShipData.TYPE_FIGHTER);
            }
            headwayent.blackholedarksun.entitydata.ShipData shipData = list.get(ENG_Utility.getRandom().nextInt(FrameInterval.GENERATE_RANDOM_SHIPS, list.size()));
            LevelObject obj = new LevelObject();
            obj.meshName = FilenameUtils.getBaseName(shipData.filename);
            int tryCount;
            String baseName;
            switch (shipData.team) {
                case ALIEN:
                    baseName = "alien";
                    tryCount = MAX_ALIEN_DEMO_SHIPS;
                    break;
                case HUMAN:
                    baseName = "human";
                    tryCount = MAX_HUMAN_DEMO_SHIPS;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid team " + shipData.team);
            }
            // Here we are sure that not all the names are taken since we
            // entered the if! Make sure this stays true!
            for (int i = 0; i < tryCount; ++i) {
                obj.name = baseName + i;
                if (!availableNameList.contains(obj.name)) {
                    // We have a valid name
                    // Make sure we removed it from the level as it
                    // does not remove itself when a ship dies
                    levelObjectToEntityMap.remove(obj.name);
                    break;
                }
            }
            // System.out.println("demo ship created with name: " + obj.name);
            float x = randomPointOnAxis(FrameInterval.GENERATE_RANDOM_SHIP_X_AXIS + obj.name);
            float y = randomPointOnAxis(FrameInterval.GENERATE_RANDOM_SHIP_Y_AXIS + obj.name);
            float z = ENG_Utility.rangeRandom(FrameInterval.GENERATE_RANDOM_SHIP_Z_AXIS + obj.name, -2000.0f, -300.0f);// randomPointOnAxis();
            obj.position.set(x, y, z);
            int nextInt = ENG_Utility.getRandom().nextInt(FrameInterval.GENERATE_RANDOM_SHIPS_AXIS, 3);
            ENG_Vector4D axis;
            switch (nextInt) {
                case 0:
                    axis = ENG_Math.VEC4_X_UNIT;
                    break;
                case 1:
                    axis = ENG_Math.VEC4_Y_UNIT;
                    break;
                case 2:
                    axis = ENG_Math.VEC4_Z_UNIT;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            obj.orientation.fromAngleAxisRad(ENG_Utility.rangeRandom(FrameInterval.GENERATE_RANDOM_SHIPS_AXIS_RAD, 0.0f, ENG_Math.TWO_PI), axis);
            obj.type = LevelObject.LevelObjectType.FIGHTER_SHIP;
            obj.ai = true;
            ArrayList<LevelObject> objList = new ArrayList<>();
            objList.add(obj);
//            loadLevelObjects(objList);
            createEntities(objList);
        }
    }

    private float randomPointOnAxis(String s) {
        return ENG_Utility.rangeRandom(s, -GameWorld.MAX_DISTANCE / 5,
                GameWorld.MAX_DISTANCE / 5);
    }

//    @Override
//    protected void checkPlayerShipDestroyed() {
//        if (playerShipDestroyedAnimation != null) {
//            if (playerShipDestroyedAnimation.getAnimationState() == Animation.AnimationState.FINISHED) {
//                setLevelState(LevelState.ENDED);
//                eventEndReason = "Your ship has been destroyed";
//                createDebriefingScreen(true, health, kills);
//                resetWorld();
//            }
//        }
//        if (playerShipEntityId != -1) {
//            if (playerShipDestroyedAnimation != null) {
//                return;
//            }
//            Entity entity = getEntityFromLevelObjectEntityId(playerShipEntityId);
//            if (entity != null) {
//                EntityProperties entityProperties = entity.getComponent(EntityProperties.class);
//                if (entityProperties.isDestroyed()) {
//                    HudManager.getSingleton().setVisible(false);
//                    ENG_InputManager.getSingleton().setInputStack(APP_Game.TOUCH_INPUT_STACK);
//                    CameraProperties cameraProperties = entity.getComponent(CameraProperties.class);
//                    ShipProperties shipProperties = entity.getComponent(ShipProperties.class);
//                    onPlayerShipDestroyed(entity, entityProperties, shipProperties, cameraProperties);
//                    playerShipDestroyedAnimation = entityProperties.getDestroyedAnimation();
//                }
//            }
//        }
//
//    }

    @Override
    public void onPlayerShipDestroyed(Entity entity, EntityProperties entityProperties, ShipProperties shipProperties, CameraProperties cameraProperties) {
        setDeathCam(entity, entityProperties, cameraProperties);
        health = entityProperties.getHealth();
        kills = shipProperties.getKills();
    }

    @Override
    public void onPlayerShipDestroyedAnimationFinished() {
        setLevelState(LevelState.ENDED);
        eventEndReason = "Your ship has been destroyed";
        createDebriefingScreen(true, health, kills);
        resetWorld();
        worldResetted = true;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    //    @Override
//    protected void updateProjectiles() {
//
//        for (Entity entity : projectileList) {
//            ProjectileProperties projectileProperties = entity.getComponent(ProjectileProperties.class);
//            if (projectileProperties != null) {
//                Entity ship = getEntityFromLevelObjectEntityId(projectileProperties.getParentName());
//                if (ship != null) {
//                    WeaponProperties launcher = ship.getComponent(WeaponProperties.class);
//                    if (launcher != null && entity.getComponent(EntityProperties.class).isDestroyed()) {
//                        // System.out.println("removing weapon id " + id +
//                        // " from ship: " +
//                        // getEntityFromLevelObjectEntityId(
//                        // projectileProperties.getParentName())
//                        // .getComponent(ShipProperties.class).getName());
//                        launcher.removeId(projectileProperties.getType(), projectileProperties.getId());
//                        // removeFromWorld(entity, false);
//                        entitiesToRemove.add(entity);
//                    }
//                }
//            }
//        }
//        clearEntitiesFromList(projectileList);
//        // removeRemovableEntities(true, projectileList, false);
//
//    }

    @Override
    public void exitObjectsFromLevel(LevelEvent levelEvent) {
        animateShipExit(levelEvent.exitObjects);
    }

    @Override
    public void spawnObjects(LevelEvent levelEvent) {
        updateHudSpawnText(levelEvent);
//        loadLevelObjects(levelEvent.spawn);

        createEntities(levelEvent.spawn);
        // Animate the spawn with a portal and transfer controls
        // once the animation is done
//						long beginTime = System.currentTimeMillis();
        animateShipSpawn(levelEvent.spawn);
//						long loadTime = System.currentTimeMillis() - beginTime;
//						System.out.println("Objects load time: " + loadTime);
    }

    private void updateHudSpawnText(LevelEvent levelEvent) {
        if (!levelEvent.spawn.isEmpty()) {
            LevelObject levelObject = levelEvent.spawn.get(0);
            boolean cargo = false;
            boolean showText = false;
            switch (levelObject.type) {
                case CARGO_SHIP:
                    cargo = true;
                    showText = true;
                    break;
                case FIGHTER_SHIP:
                    cargo = false;
                    showText = true;
                    break;
                default:
            }
            if (showText) {
                if (levelObject.friendly == ShipData.ShipTeam.HUMAN) {
                    HudManager.getSingleton().setSpawnInfoText(
                            cargo ? "Cargo ship entered"
                                    : "Reinforcements arrived");
                } else {

                    HudManager.getSingleton().setSpawnInfoText(
                            cargo ? "Enemy cargo ship entered"
                                    : "Enemy ships entered");
                }
            }
        }
    }

    /**
     * Make sure you call this only once when the level ended. No endLevel in
     * loops or we run the risk of incrementing the max level multiple times
     * among other issues.
     *
     * @param level
     * @param eventState
     */
    @Override
    protected void endLevel(LevelBase level, LevelEvent.EventState eventState) {

        level.levelEnded = true;
        setLevelState(LevelState.ENDED);
        Entity playerShipEntity = getEntityFromLevelObjectEntityId(playerShipEntityId);
        EntityProperties entityProperties = null;
        if (playerShipEntity != null) {
            entityProperties = entityPropertiesComponentMapper.get(playerShipEntity);
        }
        if (eventState != LevelEvent.EventState.WON && eventState != LevelEvent.EventState.LOST) {
            throw new IllegalArgumentException("The level must either be won or lost");
        }
        boolean lost = eventState == LevelEvent.EventState.LOST;
        if (playerShipEntity == null || (entityProperties != null && entityProperties.isDestroyed())) {
            // The level did end but we are dead so it's still a failure
            lost = true;
        }
        HudManager.getSingleton().setVisible(false);
//		HudManager.getSingleton().destroyMovementFlares();
        ENG_InputManager.getSingleton().setInputStack(APP_Game.TOUCH_INPUT_STACK);
        if (!lost) {
            // Maybe we are at a new max level so increment
            MainApp.getGame().incrementLevelReached();
        }
        // If we don't have a playerShipEntity it means that it has been removed by the PortalExitingPlayerShipAnimation.
        // The health and kills will have been set by the animation in exitShip().
        if (playerShipEntity != null) {
            ShipProperties shipProperties = shipPropertiesComponentMapper.get(playerShipEntity);
            //noinspection DataFlowIssue
            health = entityProperties.getHealth();
            kills = shipProperties.getKills();
        }
        createDebriefingScreen(lost, health, kills);
        resetWorld();
        // For the light direction to reset to default
        gameWorld.setCurrentLevel(null);

        InGameStatistics statistics = InGameStatisticsManager.getInstance().getInGameStatistics();
        SessionStatistics latestSessionStatistics = statistics.getLatestSessionStatistics();
        if (latestSessionStatistics != null) {
            LevelStatistics latestLevelStatistics = latestSessionStatistics.getLatestLevelStatistics();
            if (latestLevelStatistics != null) {
                latestLevelStatistics.levelEndDate = ENG_DateUtils.getCurrentDateTimestamp();
            }
        }
    }

    private void createDebriefingScreen(boolean loss, int health, int kills) {
        setDebriefingScreen(createDebriefingScreenText(loss, health, kills), loss);
    }

    private String createDebriefingScreenText(boolean loss, int health, int kills) {
//        EntityProperties entityProperties = getPlayerShip().getComponent(EntityProperties.class);
//        ShipProperties shipProperties = getPlayerShip().getComponent(ShipProperties.class);
//        int health = entityProperties.getHealth();
//        int kills = shipProperties.getKills();
        return "Mission name: " + APP_Game.levelTitleList[currentLevel]
                + "\nKills: " + kills
                + ((health < 0) ? "" : ("\nHealth: " + health))
                + (loss ? "\nReason for loss: " + eventEndReason : "");
    }

    /** @noinspection deprecation*/
    private void setDebriefingScreen(String text, boolean loss) {
        Bundle bundle = new Bundle();
        bundle.putString("title", loss ? "Mission Failed" : "Mission Completed");
        bundle.putString("text", text);
        bundle.putBoolean("loss", loss);
        bundle.putInt("level", getCurrentLevel());

//        setCurrentBundle(bundle);
        createDebriefingScreenActivity(bundle);
    }

    @Override
    protected void animateShipSpawn(Entity entity) {
        ShipProperties shipProperties = shipPropertiesComponentMapper.getSafe(entity);
        if (shipProperties != null) {

            EntityProperties entityProperties = entityPropertiesComponentMapper.get(entity);
            PortalEnteringAnimation anim = new PortalEnteringAnimation("PortalEnteringAnimation " + entityProperties.getName(), entity);
//			Timer timer = ENG_Utility.createTimerAndStart();
            startAnimation(entityProperties.getEntityId(), anim);
//			ENG_Utility.stopTimer(timer, "startAnimation()");
        }
    }

    @Override
    protected void animateShipExit(Entity entity) {
        if (entity != null) {
            ShipProperties shipProperties = shipPropertiesComponentMapper.getSafe(entity);
            if (shipProperties != null) {
                EntityProperties entityProperties = entityPropertiesComponentMapper.get(entity);
                Animation anim = null;
                if (cameraPropertiesComponentMapper.getSafe(entity) != null) {
                    anim = new PortalExitingPlayerShipAnimation("PortalExitingPlayerShipAnimation " + entityProperties.getName(), entity);
                } else {
                    anim = new PortalExitingAnimation("PortalExitingAnimation " + entityProperties.getName(), entity);
                }
                Utility.clearAngularVelocity(entityProperties.getRigidBody());
                startAnimation(entityProperties.getEntityId(), anim);
            }
        }
    }

    // private ENG_Vector4D leftPos = new ENG_Vector4D();
    // private ENG_Vector4D rightPos = new ENG_Vector4D();

    @Override
    public void createProjectile(Entity ship) {
        if (createProjectileFromShip(ship, null)) {
            EntityProperties entityProperties = entityPropertiesComponentMapper.get(ship);
            WeaponProperties weaponProperties = weaponPropertiesComponentMapper.get(ship);
            WeaponData.WeaponType weaponType = weaponProperties.getCurrentWeaponType();
            playSoundBasedOnDistance(entityProperties, WeaponData.WeaponType.getProjectileLaunchSoundName(weaponType));
        }

    }

    @Override
    public void resetWorld() {
        super.resetWorld();
        currentReloaderShipId = -1;
    }

    @Override
    public void reloadLevelDataAndUpdateCurrentEntities() {
        if (getLevelState() != LevelState.STARTED) {
            return;
        }
        int levelNum = getCurrentLevel();
        APP_Game game = MainApp.getGame();
        Level level = (Level) LevelLoader.compileLevel(
                levelNum, game.getSinglePlayerLevelList(), false);
        Level oldCurrentLevel = (Level) gameWorld.getCurrentLevel();
        // We must make sure that the newly loaded level is aware of the current
        // level state. We don't want to trigger new level events that have already happened.
        for (int i = 0; i < level.levelEventList.size(); ++i) {
            LevelEvent levelEvent = level.levelEventList.get(i);
            LevelEvent oldLevelEvent = oldCurrentLevel.levelEventList.get(i);
            levelEvent.state = oldLevelEvent.state;
            levelEvent.currentStartingTime = oldLevelEvent.currentStartingTime;
        }
        levelEventList.clear();
        for (int i = 0; i < level.getLevelEventNum(); ++i) {
            LevelEvent levelEvent = level.getLevelEvent(i);
            LevelEvent oldLevelEvent = oldCurrentLevel.levelEventList.get(i);

            extractWinLossEndConditions(levelEvent);
            levelEvent.prevCondEndRoot.set(oldLevelEvent.prevCondEndRoot);
            levelEvent.endCond.winNode.set(oldLevelEvent.endCond.winNode);
            levelEvent.endCond.lossNode.set(oldLevelEvent.endCond.lossNode);
        }
//        initializeEndEvents(level);
        gameWorld.setCurrentLevel(level);
        for (Entity entity : gameEntityIdsToEntities.values()) {
            EntityProperties entityProperties = entityPropertiesComponentMapper.get(entity);

            LevelObject currentLevelObject = null;
            for (LevelObject levelObject : level.levelStart.startObjects) {
                if (entityProperties.getName().equals(levelObject.name)) {
                    currentLevelObject = levelObject;
                    break;
                }
            }
            if (currentLevelObject == null) {
                for (LevelEvent levelEvent : level.levelEventList) {
                    for (LevelObject levelObject : levelEvent.spawn) {
                        if (entityProperties.getName().equals(levelObject.name)) {
                            currentLevelObject = levelObject;
                            break;
                        }
                    }
                }
            }

            if (currentLevelObject != null) {
                entityProperties.setHealth(currentLevelObject.health);
                entityProperties.setDamage(currentLevelObject.damage);
            }

            ShipProperties shipProperties = shipPropertiesComponentMapper.getSafe(entity);
            if (shipProperties != null) {
                ShipData shipData = MainApp.getGame().getNameToShipMap(entityProperties.getModelName());
                entityProperties.setHealth(shipData.health);
                shipProperties.setShipData(shipData);

                WeaponProperties weaponProperties = weaponPropertiesComponentMapper.getSafe(entity);
                if (weaponProperties != null) {
                    weaponProperties.removeAllWeapons();
                    setShipWeaponProperties(shipData, weaponProperties);
                }
            }
            CameraProperties cameraProperties = cameraPropertiesComponentMapper.getSafe(entity);
            if (cameraProperties != null) {
                // We have the player ship.
                // Player ship properties is set separately from other ship data.
                playerShipData = MainApp.getGame().getNameToShipMap(playerShipData.inGameName);
                entityProperties.setHealth(playerShipData.health);
            }

            ProjectileProperties projectileProperties = projectilePropertiesComponentMapper.getSafe(entity);
            if (projectileProperties != null) {
                WeaponData weaponData = WeaponData.getWeaponData(projectileProperties.getType());
                updateProjectileData(weaponData, entityProperties);
                TrackerProperties trackerProperties = trackerPropertiesComponentMapper.getSafe(entity);
                if (trackerProperties != null) {
                    trackerProperties.setMaxAngularVelocity(weaponData.maxAngularVelocity);
                }
            }
        }

    }

    @Override
    public void createReloaderEntity() {
        if (getShipByGameEntityId(currentReloaderShipId) == null
                && getEntityByGameEntityId(playerShipEntityId) != null
                && !reloaderDisabled) {
            // Change this to EntityData. That contains the needed filename.
            ShipData entityData = MainApp.getGame().getNameToShipMap("reloader");

            ENG_Item item = sceneManager.createItem(EntityProperties.generateUniqueName(entityData.name, gameEntityId),
                    ENG_Utility.getUniqueId(), entityData.filename, "", ENG_Workflows.MetallicWorkflow);
            currentReloaderShipId = gameEntityId;

            // This is a blocking call!!!
            EntityAabb entityAabb = getEntityAabb(entityData.filename);

            ENG_SceneNode node = sceneManager.getRootSceneNode().createChildSceneNode(item.getName());
            node.attachObject(item);
            Entity gameEntity = gameWorld.createEntity();
            addEntityByGameEntityId(gameEntityId, gameEntity);
            addEntityByItemId(item.getId(), gameEntity);
            addItemIdByEntityId(gameEntityId, item.getId());
//            entityList.add(gameEntity);
            EntityProperties entityProp = entityPropertiesComponentMapper.create(gameEntity);
//            EntityProperties entityProperties = new EntityProperties(gameEntity, entity, node, id, reloaderName);
            entityProp.setGameEntity(gameEntity);
//            entityProperties.setEntity(entity);
            entityProp.setItem(item);
            entityProp.setNode(node);
            entityProp.setEntityId(gameEntityId);
            entityProp.setName(entityData.name);
            entityProp.setHealth(entityData.health);
            entityProp.setDamage(LevelObject.DEFAULT_DAMAGE_DEALT);
            entityProp.setMaxSpeed(entityData.maxSpeed);
            entityProp.setTimedDamage(true);
            ENG_Vector4D reloaderPos = ENG_Math.generateRandomPositionOnRadius(FrameInterval.CREATE_RELOADER_ENTITY, RELOADER_INITIAL_DISTANCE_FROM_PLAYER_SHIP);
            reloaderPos.addInPlace(entityPropertiesComponentMapper.get(getPlayerShip()).getNode().getPosition());
            entityProp.setPosition(
//                    new ENG_Vector4D(0.0f, 0.0f, -500.0f, 1.0f)
                    reloaderPos);
            EntityProperties playerShipEntityProperties = entityPropertiesComponentMapper.get(getPlayerShip());
            ENG_Quaternion orientation = new ENG_Quaternion();
            ENG_Math.rotateTowardPositionDeg(playerShipEntityProperties.getNode().getPosition(),
                    entityProp.getNode().getPosition(),
                    entityProp.getNode().getLocalInverseZAxis(),
                    entityProp.getNode().getLocalYAxis(),
                    orientation,
                    ENG_Math.TWO_PI * ENG_Math.RADIANS_TO_DEGREES);
            entityProp.setOrientation(orientation);
            entityProp.setWeight(headwayent.blackholedarksun.entitydata.ShipData.RELOADER_SHIP_WEIGHT);
            entityProp.setOnRemove(entity -> currentReloaderShipId = -1);
//            gameEntity.addComponent(entityProperties);

            addToShipList(entityProp.getEntityId(), gameEntity);

            ShipProperties shipProp = shipPropertiesComponentMapper.create(gameEntity);
            shipProp.setName(RELOADER);
            shipProp.setAiEnabled(true);
            // Use scan radius as radius to allow reloading the player ship
            shipProp.setScanRadius(ShipData.RELOADER_SCAN_RADIUS);
            gameWorld.getManager(GroupManager.class).add(gameEntity, entityData.team.toString());
            setShipData(shipProp, entityData);
//            gameEntity.addComponent(shipProp);
            entityProp.setDestroyedAnimation(new ShipExplosionAnimation("ShipExplosionAnimation " + entityData.name, gameEntity));
            shipProp.setEnteredWorldAnimation(new PortalEnteringAnimation("PortalEnteringAnimation " + entityData.name, gameEntity));
            shipProp.setExitedWorldAnimation(new PortalExitingAnimation("PortalExitingAnimation " + entityData.name, gameEntity));
//            entityMap.put(id, gameEntity);
//            ++id;

            AIProperties aiProp = aiPropertiesComponentMapper.create(gameEntity);
            aiProp.setCurrentHealth(entityProp.getHealth());

//            gameEntity.addComponent(aiProp);

//            gameWorld.addEntity(gameEntity);

            aiPropertiesComponentMapper.get(gameEntity).setState(AIProperties.AIState.FOLLOW_PLAYER_SHIP);

            EntityMotionState motionState = new EntityMotionState(entityProp, shipProp);
            short collisionGroup = PhysicsProperties.CollisionGroup.STANDARD.getVal();
            short collisionMask = PhysicsProperties.CollisionMask.STANDARD.getVal();

            entityProp.setWeight(entityData.weight);

            createPhysicsSettings(entityProp, entityData, gameEntity, entityAabb, motionState, collisionGroup, collisionMask);

            startAnimation(entityProp.getEntityId(), shipProp.getEnteredWorldAnimation());

            incrementGameEntityId();

            InGameStatistics statistics = InGameStatisticsManager.getInstance().getInGameStatistics();
            SessionStatistics latestSessionStatistics = statistics.getLatestSessionStatistics();
            if (latestSessionStatistics != null) {
                LevelStatistics latestLevelStatistics = latestSessionStatistics.getLatestLevelStatistics();
                if (latestLevelStatistics != null) {
                    LevelEventStatistics latestLevelEventStatistics = latestLevelStatistics.getLatestLevelEventStatistics();
                    if (latestLevelEventStatistics != null) {
                        ++latestLevelEventStatistics.reloaderCalledNum;
                    }
                }
            }

        }
    }

    @Override
    protected void createStaticEntity(String modelName, LevelObject obj) {
        super.createStaticEntity(modelName, obj);
    }

    @Override
    public void createDebris(Entity entity) {
        super.createDebris(entity);
    }

    /** @noinspection deprecation */
    @Override
    protected void createEntity(String modelName, final LevelObject obj) {
        // AvailableName availableName = new AvailableName(obj.name);
        ENG_Log.getInstance().log("creating entity: " + modelName, ENG_Log.TYPE_MESSAGE);
        long beginTime = currentTimeMillis();
        availableNameList.add(obj.name);

        ENG_Item item = sceneManager.createItem(EntityProperties.generateUniqueName(obj.name, gameEntityId),
                ENG_Utility.getUniqueId(), obj.meshName, "", getWorkflow(obj));

        // This is a blocking call!!!
        EntityAabb entityAabb = getEntityAabb(obj.meshName);
        System.out.println("centre: " + entityAabb.centre.toString() + " halfSize: " + entityAabb.halfSize.toString());

        ENG_SceneNode node = sceneManager.getRootSceneNode().createChildSceneNode(item.getName());
        node.attachObject(item);

        // Update the position and orientation here in order to get the world transform that we can use in bullet.

        Entity gameEntity = gameWorld.createEntity();
        addEntityByGameEntityId(gameEntityId, gameEntity);
        addEntityByItemId(item.getId(), gameEntity);
        addItemIdByEntityId(gameEntityId, item.getId());
//        entityList.add(gameEntity);
        EntityProperties entityProp = entityPropertiesComponentMapper.create(gameEntity);
        entityProp.setGameEntity(gameEntity);
        entityProp.setItem(item);
        entityProp.setNode(node);
        entityProp.setEntityId(gameEntityId);
        entityProp.setName(obj.name);
        entityProp.setHealth(obj.health);
        entityProp.setDamage(obj.damage);
        entityProp.setTimedDamage(true);
        entityProp.setTimedDamageTime(1000);
        entityProp.setVelocity(obj.velocity);
        entityProp.setPosition(obj.position);
        entityProp.setOrientation(obj.orientation);
        entityProp.setRadius(obj.radius);
        entityProp.setInvincible(obj.invincible);
        entityProp.setModelName(modelName);
        entityProp.setObjectType(obj.type);

        node.setPosition(obj.position.x, obj.position.y, obj.position.z);
        node.setOrientation(obj.orientation);
        node._updateWithoutBoundsUpdate(false, false);

        EntityMotionState motionState = null;

        short collisionGroup = 0;
        short collisionMask = 0;

        LevelEntity levelEntity = new LevelEntity(gameEntity);
        addLevelObjectByName(obj.name, levelEntity);
        addLevelObjectById(gameEntityId, levelEntity);
        EntityData entityData = null;
        AIProperties aiProperties = null;
        if (obj.type == LevelObject.LevelObjectType.FLAG_BLUE
                || obj.type == LevelObject.LevelObjectType.FLAG_RED) {
            currentBeaconId = gameEntityId;
            BeaconProperties beaconProperties = beaconPropertiesComponentMapper.create(gameEntity);
            motionState = new EntityMotionState(entityProp);
            collisionGroup = PhysicsProperties.CollisionGroup.TRANSPARENT.getVal();
            collisionMask = PhysicsProperties.CollisionMask.TRANSPARENT.getVal();
        }
        if (obj.type == LevelObject.LevelObjectType.WAYPOINT) {
            WaypointProperties waypointProperties = waypointPropertiesComponentMapper.create(gameEntity);
            waypointProperties.setWaypointSectorId(obj.waypointSectorId);
            waypointProperties.setWaypointId(obj.waypointId);
            motionState = new EntityMotionState(entityProp);
            collisionGroup = PhysicsProperties.CollisionGroup.TRANSPARENT.getVal();
            collisionMask = PhysicsProperties.CollisionMask.TRANSPARENT.getVal();
            addWaypointId(item.getId());
        }
        if (obj.type == LevelObject.LevelObjectType.CARGO) {
            cargoNameToIdMap.put(obj.name, gameEntityId);
            CargoProperties cargoProperties = cargoPropertiesComponentMapper.create(gameEntity);
            entityProp.setInvincible(true);
            entityProp.setScannable(true);
            entityProp.setUnmovable(true);
            motionState = new EntityMotionState(entityProp);
            collisionGroup = PhysicsProperties.CollisionGroup.STANDARD.getVal();
            collisionMask = PhysicsProperties.CollisionMask.STANDARD.getVal();
        }
        if (obj.type == LevelObject.LevelObjectType.ASTEROID) {
            AsteroidData asteroidData = MainApp.getGame().getAsteroidData(modelName);
            entityData = asteroidData;
            entityProp.setUnmovable(true);
            // FIXME BEGIN
            entityProp.setDestroyedAnimation(new ExplosionAnimation("ExplosionAnimation " + node.getName(), gameEntity, ExplosionAnimation.EXPLOSION_SMALL_MAT, 3.0f));
            // FIXME END
            entityProp.setDestructionSoundName(APP_Game.getAsteroidExplosionSoundName(ENG_Utility.getRandom()
                    .nextInt(FrameInterval.ASTEROID_CREATE_ENTITY + obj.name, APP_Game.ASTEROID_SOUND_NUM)));
            motionState = new EntityMotionState(entityProp);
            collisionGroup = PhysicsProperties.CollisionGroup.STANDARD.getVal();
            collisionMask = PhysicsProperties.CollisionMask.STANDARD.getVal();
        }
        if (obj.type == LevelObject.LevelObjectType.FIGHTER_SHIP
                || obj.type == LevelObject.LevelObjectType.CARGO_SHIP) {

            addToShipList(entityProp.getEntityId(), gameEntity);
            entityProp.setUpdateSectionList(true);
            entityProp.setScannable(true);
            entityProp.setShowHealth(true);
            ShipProperties shipProp = shipPropertiesComponentMapper.create(gameEntity);
            shipProp.setName(obj.name);
            shipProp.setScanRadius(obj.scanRadius);
            ShipData shipData = MainApp.getGame().getNameToShipMap(modelName);
            entityData = shipData;
            entityProp.setHealth(shipData.health); // We also have armor for the future.
            entityProp.setMaxSpeed(shipData.maxSpeed);
            gameWorld.getManager(GroupManager.class).add(gameEntity, shipData.team.toString());
            setShipData(shipProp, shipData);
//            gameEntity.addComponent(shipProp);
            setShipWeapons(gameEntity, shipData);

            motionState = new EntityMotionState(entityProp, shipProp);
            collisionGroup = PhysicsProperties.CollisionGroup.STANDARD.getVal();
            collisionMask = PhysicsProperties.CollisionMask.STANDARD.getVal();

            if (obj.type == LevelObject.LevelObjectType.FIGHTER_SHIP) {
                entityProp.setDestroyedAnimation(new ShipExplosionAnimation("ShipExplosionAnimation " + obj.name, gameEntity));
            } else if (obj.type == LevelObject.LevelObjectType.CARGO_SHIP) {
                entityProp.setDestroyedAnimation(new CargoShipExplosionAnimation("CargoShipExplosionAnimation " + obj.name, gameEntity));
            }
            if (obj.type == LevelObject.LevelObjectType.FIGHTER_SHIP) {
                entityProp.setDestructionSoundName(headwayent.blackholedarksun.entitydata.ShipData.getShipDestroyedSound(ENG_Utility.getRandom()
                        .nextInt(FrameInterval.DESTRUCTION_SOUND_CREATE_ENTITY + obj.name, headwayent.blackholedarksun.entitydata.ShipData.SHIP_DESTROYED_SOUND_NUM)));
            } else if (obj.type == LevelObject.LevelObjectType.CARGO_SHIP) {
                entityProp.setDestructionSoundName(headwayent.blackholedarksun.entitydata.ShipData.getCargoShipDestroyedSound(
                        ENG_Utility.getRandom()
                                .nextInt(FrameInterval.CARGO_DESTRUCTION_SOUND_CREATE_ENTITY + obj.name, headwayent.blackholedarksun.entitydata.ShipData.CARGO_SHIP_DESTROYED_SOUND_NUM)));
            }
            shipProp.setCountermeasuresAnimationFactory(new CountermeasuresAnimationFactory("CountermeasureAnimation " + entityProp.getName() + " "));
            shipProp.setAiEnabled(obj.ai);
            if (obj.type != LevelObject.LevelObjectType.PLAYER_SHIP) {
                Sound sound = playSoundBasedOnDistance(entityProp, shipData.engineSoundName, true, true);
                if (sound != null) {
                    sound.maxDistance = MAX_ENGINE_SOUND_DISTANCE;
                    sound.volumeMultiplier = 0.5f;
                    shipProp.setEngineSound(sound);
                }
            }
            if (obj.type == LevelObject.LevelObjectType.FIGHTER_SHIP) {
                if (obj.squadNum != -1) {

                }
            }
//            entityMap.put(id, gameEntity);
//            ++id;
        }
        if (obj.ai) {
            aiProperties = addAIProperties(gameEntity, entityProp, obj);

        }
        if (obj.type == LevelObject.LevelObjectType.PLAYER_SHIP) {

            addToShipList(entityProp.getEntityId(), gameEntity);
            entityProp.setUpdateSectionList(true);
            ENG_Camera camera = sceneManager.getCamera(APP_Game.MAIN_CAM);
            ENG_CameraNative cameraNative = new ENG_CameraNative(camera);
            cameraNode = sceneManager.getRootSceneNode().createChildSceneNode(CAMERA_NODE_NAME);
            cameraNode.setNativeName();
            CameraProperties camProp = cameraPropertiesComponentMapper.create(gameEntity);
//            CameraProperties camProp = new CameraProperties(camera, cameraNode);
            camProp.setCamera(camera);
            camProp.setNode(cameraNode);
            boolean thirdPersonCamera = MainApp.getGame().isThirdPersonCamera();
            camProp.setType(thirdPersonCamera ? CameraProperties.CameraType.THIRD_PERSON : CameraProperties.CameraType.FIRST_PERSON);
//            gameEntity.addComponent(camProp);

            cameraNative.detachFromParent();
            cameraNode.attachObject(cameraNative);
            if (!thirdPersonCamera) {
                // Make the ship invisible
                node.flipVisibility(false);
            }
            // Get around the bug described at
            // http://www.ogre3d.org/forums/viewtopic.php?f=1&t=72872
            // TODO Is this still needed in Ogre 2.1?
            camera.invalidateView();
//            camera.lookAt(node.getLocalInverseZAxis());
//            camProp.setAnimatedCamera(true);
//            cameraNode.lookAt(new ENG_Vector4D(0, 1, -1, 1));

            // System.out.println("camera attached with address " + cameraNode);
            ShipProperties shipProp = shipPropertiesComponentMapper.create(gameEntity);
//            ShipProperties shipProp = new ShipProperties();
            shipProp.setName(obj.name);
            shipProp.setScanRadius(obj.scanRadius);
            if (playerShipData == null) {
                throw new NullPointerException("playerShipData is null");
            }
            entityData = playerShipData;
            setShipData(shipProp, playerShipData);
//            gameEntity.addComponent(shipProp);
            setShipWeapons(gameEntity, playerShipData);

            entityProp.setHealth(playerShipData.health); // We also have armor for the future.
//            entityMap.put(id, gameEntity);
//            ++id;
//            playerShip = gameEntity;
            playerShipEntityId = entityProp.getEntityId();
            PlayerEntityDestroyedVerifierSPSystem playerEntityDestroyedVerifierSPSystem = gameWorld.getSystem(PlayerEntityDestroyedVerifierSPSystem.class);
            playerEntityDestroyedVerifierSPSystem.setPlayerShipEntityId(playerShipEntityId);
            // Reset or else we cannot detect a new player ship destruction !!!
            playerEntityDestroyedVerifierSPSystem.reset();

            HudManager hudManager = HudManager.getSingleton();
            // FIXME HudManager should always be not null but for now we don't have a hudmanager available.
            if (hudManager != null) {
                hudManager.setMaxScrollPercentageChange(playerShipData.maxPercentageAcceleration);
                HudManager.getSingleton().setScrollStartingPercentage(playerShipData.initialSpeedPercentual);
            }
            entityProp.setVelocity(shipProp.getVelocity(playerShipData.initialSpeedPercentual));

            SimpleViewGameMenuManager.updateMenuState(SimpleViewGameMenuManager.MenuState.IN_GAME_OVERLAY);

            entityProp.setHitAnimationFactory(new ShipHitAnimationFactory("ShipHitAnimation " + obj.name + " "));
            shipProp.setCountermeasuresAnimationFactory(new CountermeasuresAnimationFactory("CountermeasuresAnimation " + obj.name + " "));

            entityProp.setDestroyedAnimation(new ShipExplosionAnimation("ShipExplosionAnimation " + obj.name, gameEntity));

            entityProp.setDestructionSoundName(headwayent.blackholedarksun.entitydata.ShipData.getShipDestroyedSound(ENG_Utility.getRandom()
                    .nextInt(FrameInterval.DESTRUCTION_SOUND_CREATE_ENTITY + obj.name, headwayent.blackholedarksun.entitydata.ShipData.SHIP_DESTROYED_SOUND_NUM)));

//            entityProp.setHealth((int) getPlayerShipData().armor);
            gameWorld.getManager(GroupManager.class).add(gameEntity, getPlayerShipData().team.toString());

            motionState = new PlayerShipMotionState(entityProp, shipProp, camProp);
            collisionGroup = PhysicsProperties.CollisionGroup.STANDARD.getVal();
            collisionMask = PhysicsProperties.CollisionMask.STANDARD.getVal();
        }

        if (entityData == null) {
            setWeight(entityProp, obj.type);
            entityData = new EntityData();
        } else {
            entityProp.setWeight(entityData.weight);
        }

        createPhysicsSettings(entityProp, entityData, gameEntity, entityAabb, motionState, collisionGroup, collisionMask);
//        PhysicsUtility.addGhostObject(btDiscreteDynamicsWorld, ghostObject, collisionGroup, collisionMask);
//        motionState.setGhostObject(ghostObject);

        if (aiProperties != null) {
            aiProperties.setRayResultCallback(PhysicsUtility.createRayTest(
                    entityProp.getRigidBody(), new Vector3(), new Vector3(), collisionGroup, collisionMask));
        }

        incrementGameEntityId();

//        gameWorld.addEntity(gameEntity);

//		long loadTime = System.currentTimeMillis() - beginTime;
//		System.out.println("Time to load ship " + obj.name + " with meshName " +
//				obj.meshName + ": " + loadTime);
    }

    @Override
    public void loadLevel() {
        reinitializeWorld();
        Level level = (Level) gameWorld.getCurrentLevel();

        if (!level.cutsceneListLoaded) {
            for (String name : level.cutsceneNameList) {
                level.cutsceneList.add(loadCutscene(name));
            }
            level.cutsceneListLoaded = true;
        }


        // First check if there is a cutscene to play.
        if (level.levelStart.cutsceneName != null) {
            Cutscene cutscene = loadCutscene(level.levelStart.cutsceneName);
            level.cutsceneList.add(cutscene);
            level.levelStart.cutscene = cutscene;
            level.levelStart.cutscene.setUseSkyboxDataFromLevel(level.levelStart.useSkyboxDataFromLevel);
            // Check if we can simply use the skybox and lighting from the level.
            if (level.levelStart.cutscene.isUseSkyboxDataFromLevel()) {
                createSkybox(level.levelStart.skyboxName);
                createLevelLighting(level.levelStart);
            }

            playCutscene(level.levelStart.cutsceneName, true, Cutscene.CutsceneType.LEVEL_BEGINNING);
        } else {

            createSkybox(level.levelStart.skyboxName);

            createLevelLighting(level.levelStart);

            loadReloader(level);
//        loadLevelObjects(level.levelStart.startObjects);
            createEntities(level.levelStart.startObjects);
            prepareLevel();
            // When loading the level also reset the position from
            // which we calculate the pitch and yaw.

            loadWaypoints();

        }
        if (MainApp.getGame().isAccelerometerEnabled()) {
//            MainApp.getGame().getInputConvertorToMovement().resetOriginalOrientation();
        }
        ((InGameInputConvertorListener) ENG_InputManager.getSingleton().getInputConvertorListener(APP_Game.TO_IN_GAME_LISTENER)).reset();
        HudManager hudManager = HudManager.getSingleton();
        // FIXME!!! hudmanager should always be not null!!!
        if (hudManager != null) {
            hudManager.reset();
        }
        setLevelState(LevelState.STARTED);
        worldResetted = false;
    }

    @Override
    public void destroyEntities() {
        gameWorld.getSystem(EntityDeleterSystem.class).process();
        gameWorld.getSystem(StaticEntityDeleterSystem.class).process();
    }
}
