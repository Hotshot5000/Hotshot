/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/18/21, 7:57 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.world;

import com.artemis.Entity;

import headwayent.blackholedarksun.*;
import headwayent.blackholedarksun.animations.PlayerShipDeathCamAnimation;
import headwayent.blackholedarksun.animations.ProjectileExplosionAnimation;
import headwayent.blackholedarksun.components.*;
import headwayent.blackholedarksun.compositor.SceneCompositor;
import headwayent.blackholedarksun.entitydata.WeaponData;
import headwayent.blackholedarksun.levelresource.Level;
import headwayent.blackholedarksun.levelresource.LevelBase;
import headwayent.blackholedarksun.levelresource.LevelStart;
import headwayent.blackholedarksun.parser.ast.AmbientLight;
import headwayent.blackholedarksun.parser.ast.Cutscene;
import headwayent.blackholedarksun.parser.ast.InitialConds;
import headwayent.blackholedarksun.physics.EntityMotionState;
import headwayent.blackholedarksun.physics.PhysicsProperties;
import headwayent.blackholedarksun.physics.PlayerShipMotionState;
import headwayent.blackholedarksun.systems.helper.ai.skynet.SquadManager;
import headwayent.hotshotengine.*;
import headwayent.hotshotengine.audio.ENG_Playable;
import headwayent.hotshotengine.renderer.*;

import java.util.*;
import java.util.Map.Entry;

import headwayent.blackholedarksun.components.CameraProperties.CameraType;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.systems.MovementSystem;
import headwayent.hotshotengine.audio.ENG_ISoundRoot;
import headwayent.hotshotengine.audio.ENG_ISoundRoot.PlayType;
import headwayent.hotshotengine.audio.ENG_SoundManager;
//import headwayent.hotshotengine.ENG_ModelLoader.GroupMaterial;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.input.ENG_InputManager;

import static headwayent.hotshotengine.ENG_Utility.currentTimeMillis;

public abstract class WorldManager extends WorldManagerBase {
    public static final String LEVEL_LIGHT = "LevelLight";
    public static final String LEVEL_LIGHT_NODE = "LevelLightNode";
    public static final long SOUND_UPDATE_MIN_DELTA = 70;
    private long lastSoundUpdateTime;
    private boolean levelDirectionalLightingCreated;

//    protected static WorldManager mgr;

    public WorldManager() {
//        if (mgr != null && getClass().equals(mgr.getClass())) {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
//        mgr = this;
    }

    public void setNodeAutoRotate(ENG_Vector4D axis, float angle,
                                  ENG_SceneNode node) {
        autoRotateList.add(new AutoRotation(axis, angle, node));
    }

    public void setNodeAutoRotate(float x, float y, float z, float angle,
                                  ENG_SceneNode node) {
        autoRotateList.add(new AutoRotation(x, y, z, angle, node));
    }

    public void removeNodeAutoRotate(ENG_SceneNode node) {
        removeNodeAutoRotate(node.getName());
    }

    public void removeNodeAutoRotate(String nodeName) {
        AutoRotation delRot = null;
        for (AutoRotation autoRot : autoRotateList) {
            if (autoRot.node.getName().equals(nodeName)) {
                delRot = autoRot;
                break;
            }
        }
        if (delRot == null) {
            throw new IllegalArgumentException(nodeName
                    + " is not a valid node name " + "in auto rotation list");
        }
        autoRotateList.remove(delRot);
    }

    protected void updateAutoRotateList() {
        for (AutoRotation autoRot : autoRotateList) {
            ENG_Quaternion.fromAngleAxisDeg(autoRot.angle
                            * GameWorld.getWorld().getDelta(), autoRot.axis,
                    autoRot.rot);
            autoRot.node.rotate(autoRot.rot);
//			((ENG_Entity) autoRot.node.getAttachedObject(0))
//			.getAnimationState("Default")
//			.addTime((float) GameWorld.getWorld().getDelta());
        }
    }

    public void destroyLevelResources() {
        // if (getLevelState() == LevelState.ENDED) {
        // Animations must be finished before everything since they may
        // change some entities
        destroyAnimations();
        destroySkybox(false);
        destroyEntities();

        destroyPlayerCameraNode();
        destroyLevelLighting();
        LevelBase level = gameWorld.getCurrentLevel();

        if (level == null) {
            setLevelState(LevelState.ENDED);
        } else {
            // HACK! If we have a cutscene and we reset everything after it it doesn't mean that
            // we have ended the level.
            if (!level.cutsceneActive ||
                    (level.currentCutsceneType != Cutscene.CutsceneType.LEVEL_BEGINNING &&
                            level.currentCutsceneType != Cutscene.CutsceneType.DURING_LEVEL &&
                            level.currentCutsceneType != Cutscene.CutsceneType.STORY)) {
                {
                    setLevelState(LevelState.ENDED);
                }
            }
        }
        // }
    }

    public void reloadLevelResources() {
        if (getLevelState() == LevelState.STARTED) {
//            loadLevelResources();
            reloadSkybox();
            reloadEntities();
            reloadAnimations();
        } else if (isShowDemo()) {
            resetWorld();
        }
    }

    private void destroyPlayerCameraNode() {
        if (playerShipEntityId != -1) {
            destroyCameraNode(CAMERA_NODE_NAME);

        }
    }

    private void reloadAnimations() {

        for (Animation anim : animationList) {
            anim.reloadResources();
        }
    }

    private void destroyAnimations() {

        for (Animation anim : animationList) {
            anim.destroyResources();
        }
    }

    private void destroySkybox(boolean skipGLDelete) {
//        if (currentSkybox != null) {
//            sceneManager.setSkyBox(false, null, true, null, null);
//            sceneManager.destroyEntity(currentSkybox, skipGLDelete);
//        }
        SceneCompositor.getSingleton().setDefaultCompositor();
    }

    private void reloadSkybox() {
//        if (currentSkybox != null) {
//            destroySkybox(true);
//            createSkybox();
//        }
    }

    private void reloadEntities() {
        for (Entity entity : gameEntityIdsToEntities.values()) {

            // ENG_Entity e = entityProperties.getEntity();
            reloadEntity(entity);
        }
    }

    public abstract void destroyEntities();

//    private void destroyEntity(Entity entity) {
//        EntityProperties entityProperties = entity.getComponent(EntityProperties.class);
//        // System.out.println(
//        // "destroying entity: " + entityProperties.getEntity().getName());
//        ENG_Entity oldEntity = entityProperties.getEntity();
//        ENG_SceneNode node = entityProperties.getNode();
//
//        node.detachObject(oldEntity);
//        sceneManager.destroyEntity(oldEntity);
//        sceneManager.getRootSceneNode().removeAndDestroyChild(node.getName());
//        entityProperties.clearSectionList();
//        gameWorld.deleteEntity(entity);
//        // To actually delete shit...
//        // gameWorld.process();
//    }

    /** @noinspection deprecation */
    private void reloadEntity(Entity entity) {
        EntityProperties entityProperties = entityPropertiesComponentMapper.get(entity);
        ShipProperties shipProperties = shipPropertiesComponentMapper.getSafe(entity);
        ProjectileProperties projectileProperties = projectilePropertiesComponentMapper.getSafe(entity);
        ENG_Entity oldEntity = entityProperties.getEntity();
        String name = oldEntity.getName();
        String meshName = oldEntity.getMesh().getName();
        boolean visible = oldEntity.getVisible();
        ENG_SceneNode node = entityProperties.getNode();

        // We have reloaded the level resources but not the projectile resources
//        if (projectileProperties != null) {
//            ENG_ModelResource weaponFileData = MainApp.getGame().getWeaponResource(projectileProperties.getType());
//            loadResources(weaponFileData);
//        }

        node.detachObject(oldEntity);
        sceneManager.destroyEntity(oldEntity, true);
        ENG_Entity newEntity = sceneManager.createEntity(oldEntity.getName(), entityProperties.getEntityId(), meshName, ENTITY_GROUP_NAME);
        newEntity.setVisible(visible);
        entityProperties.setEntity(newEntity);
        node.attachObject(newEntity);

    }

    @Override
    public void resetWorld() {
        LevelBase level = gameWorld.getCurrentLevel();
        destroyLevelResources();
//        entityMap.clear();
        resetIdCounter();
//        playerShip = null;
        if (level == null) {
            playerShipData = null;
        } else {
            // HACK! If we have a cutscene and we reset everything after it it doesn't mean that
            // we have to remove player data.
            if (!level.cutsceneActive ||
                    (level.currentCutsceneType != Cutscene.CutsceneType.LEVEL_BEGINNING &&
                            level.currentCutsceneType != Cutscene.CutsceneType.DURING_LEVEL &&
                            level.currentCutsceneType != Cutscene.CutsceneType.STORY)) {
                playerShipData = null;
            }
        }
        playerShipEntityId = -1;
        gameEntityIdsToEntities.clear();
        gameEntityIdsToShips.clear();
        itemIdsToEntities.clear();
        entityIdsToItemIds.clear();
        levelEventList.clear();
        levelObjectToEntityMap.clear();
        levelObjectIdToEntityMap.clear();
//        for (Entity e : entityList) {
//            // removeRemovableEntities(entityList);
//            e.deleteFromWorld();
//        }
//        gameWorld.resetSectionList();
//        entityList.clear();
//        projectileList.clear();
//        finalStateList.clear();
        animationList.clear();
        // If we are in a cutscene we can reset the level state at the end and loadLevel() will
        // set it to started after.
        if (level == null) {
            setLevelState(LevelState.NONE);
        } else {
            // HACK! If we have a cutscene and we reset everything after it it doesn't mean that
            // we have to remove player data.
            if (!level.cutsceneActive ||
                    (level.currentCutsceneType != Cutscene.CutsceneType.LEVEL_BEGINNING &&
                            level.currentCutsceneType != Cutscene.CutsceneType.DURING_LEVEL &&
                            level.currentCutsceneType != Cutscene.CutsceneType.STORY)) {
                setLevelState(LevelState.NONE);
            }
        }
        reloaderDisabled = false;
        cargoIdList.clear();
        cargoNameToIdMap.clear();
        currentBeacon = null;
        availableNameList.clear();
        cutsceneMap.clear();
        ENG_ISoundRoot sound = MainApp.getGame().getSound();
        for (ArrayList<Sound> soundList : currentPlayingSounds.values()) {
            for (Sound snd : soundList) {
                sound.stopSound(snd.name, snd.id);
            }
        }
        currentPlayingSounds.clear();
        if (level == null) {
            gameWorld.getSystem(MovementSystem.class).destroyThreads();
        } else {
            // HACK! If we have a cutscene and we reset everything after it it doesn't mean that
            // we have ended the level and need to destroy the movement threads.
            if (!level.cutsceneActive ||
                    (level.currentCutsceneType != Cutscene.CutsceneType.LEVEL_BEGINNING &&
                            level.currentCutsceneType != Cutscene.CutsceneType.DURING_LEVEL &&
                            level.currentCutsceneType != Cutscene.CutsceneType.STORY)) {
                gameWorld.getSystem(MovementSystem.class).destroyThreads();
            }
        }
        SquadManager.getInstance().reset();
    }

    /** @noinspection deprecation*/
    protected void setMaterialLightingDirection(ENG_Entity entity) {
        LevelBase level = gameWorld.getCurrentLevel();
        if (level != null) {
            ENG_MaterialManager
                    .getSingleton()
                    .getByName(
                            entity.getMesh().getSubMesh((short) 0)
                                    .getMaterialName())
                    .getTechnique((short) 0)
                    .getPass((short) 0)
                    .getVertexProgramParameters()
                    .setNamedConstant("lightDir",
                            level.getLevelStart().lightDir.normalizedCopy());
        }
    }

    protected void createLevelLighting(LevelStart levelStart) {
        if (levelDirectionalLightingCreated) {
            return;
        }
        LightingManager lightingManager = LightingManager.getSingleton();
        lightingManager.createDirectionalLight(LEVEL_LIGHT, LEVEL_LIGHT_NODE, levelStart.lightPowerScale,
                levelStart.lightDiffuseColor, levelStart.lightSpecularColor, levelStart.lightDir);
        lightingManager.setAmbientLight(levelStart.ambientLightUpperHemisphere, levelStart.ambientLightLowerHemisphere,
                new ENG_Vector3D(levelStart.ambientLighthemisphereDir));
        levelDirectionalLightingCreated = true;
    }

    protected void createLevelLightingV2(InitialConds initialConds) {
        LightingManager lightingManager = LightingManager.getSingleton();
        lightingManager.createDirectionalLight(LEVEL_LIGHT, LEVEL_LIGHT_NODE,
                initialConds.getLightPowerScale().getLightPowerScale(),
                initialConds.getLightDiffuseColor().getLightDiffuseColor(),
                initialConds.getLightSpecularColor().getLightSpecularColor(),
                initialConds.getLightDir().getLightDir().getAsVector4D());
        AmbientLight ambientLight = initialConds.getAmbientLight();
        lightingManager.setAmbientLight(ambientLight.getUpper(),
                ambientLight.getLower(),
                ambientLight.getDir());
    }

    protected void destroyLevelLighting() {
        if (!levelDirectionalLightingCreated) {
            return;
        }
        LightingManager lightingManager = LightingManager.getSingleton();
        lightingManager.destroyLight(LEVEL_LIGHT);
        // Should we do something about the ambient light??
        levelDirectionalLightingCreated = false;
    }

    public boolean isShowDemo() {
        return showDemo;
    }

    public void setShowDemo(boolean showDemo) {
        this.showDemo = showDemo;
        if (!showDemo) {
            resetWorld();
        }
    }

    public void exitGame() {
        SimpleViewGameMenuManager.exitGame();
    }

    public void exitToMainMenu() {
        MainApp.getGame().setInGamePaused(false);
        setLevelState(LevelState.NONE);
        resetWorld();
        // For the light direction to reset to default
        gameWorld.setCurrentLevel(null);
        ENG_InputManager.getSingleton().setInputStack(APP_Game.TOUCH_INPUT_STACK);
        SimpleViewGameMenuManager.setCurrentMenu(SimpleViewGameMenuManager.MAIN_MENU);
        resetToWorldManagerSP();

    }

    public void resetToWorldManagerSP() {
        // No need to reinitialize the SP world manager if not in MP.
        if (MainApp.getGame().getGameMode() == APP_Game.GameMode.MP) {
            WorldManagerSP worldManagerSP = new WorldManagerSP();
            worldManagerSP.setSceneManager(ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER));
            APP_Game game = MainApp.getGame();
            ((APP_SinglePlayerGame) game).setWorldManager(worldManagerSP);
        }
    }

    public void stopLevel() {
        HudManager.getSingleton().setVisible(false);
        setLevelState(LevelState.NONE);
        resetWorld();
        // For the light direction to reset to default
        gameWorld.setCurrentLevel(null);
        // When exiting the game tell it that next time to show the main menu.
        // If we use showMenuOverlay we cannot create a glAnything in the
        // gameDeactivate after stopping the glThread.
        MainApp.getGame().setNextStartMenuName("main_menu");

        // If exiting make sure that we reenable the demo when we enter again
//        MainApp.getGame().reenableDemo(true);
    }

    protected int getNumShips() {
        return gameEntityIdsToShips.size();
    }

//    protected abstract void checkPlayerShipDestroyed();

    /**
     * Useless shit that must burn in hell below.
     */
    /**
     * Fucking hack for when the user presses the home button while in the
     * debriefing or briefing screen. If we have a currentBundle then we show
     * the debriefing screen (even if the user was in the briefing screen).
     */
//    public void resetCurrentBundle() {
//
//        currentBundleLock.lock();
//        try {
//            currentBundle = null;
//        } finally {
//            currentBundleLock.unlock();
//        }
//    }
//
//    public Bundle getCurrentBundle() {
//        currentBundleLock.lock();
//        try {
//            return currentBundle;
//        } finally {
//            currentBundleLock.unlock();
//        }
//    }
//
//    protected void setCurrentBundle(Bundle bundle) {
//        currentBundleLock.lock();
//        try {
//            currentBundle = bundle;
//        } finally {
//            currentBundleLock.unlock();
//        }
//    }

    public void createDebriefingScreenActivity(Bundle bundle) {
//        currentBundleLock.lock();
//        try {
//            if (currentBundle != null) {
			/*	MainActivity.getInstance().startActivityForResult(
						new Intent().setClass(
								MainActivity.getInstance()
										.getApplicationContext(),
								MissionDebriefingActivity.class).putExtra(
								BUNDLE_MISSION_DEBRIEFING, currentBundle), 0);*/
//				ENG_ContainerManager.getSingleton()
//				.setCurrentContainer(new MissionDebriefing(currentBundle));
                ENG_ContainerManager.getSingleton().setCurrentContainer(
                        ENG_ContainerManager.getSingleton().createContainer("MissionDebriefing", "MissionDebriefing", bundle));
//            }
//        } finally {
//            currentBundleLock.unlock();
//        }
    }

    public void setCameraType(CameraType type) {
        if (getLevelState() == LevelState.STARTED) {
            Entity playerShip = getPlayerShip();
            CameraProperties cameraProperties = cameraPropertiesComponentMapper.get(playerShip);
            EntityProperties entityProperties = entityPropertiesComponentMapper.get(playerShip);
            switch (type) {
                case FIRST_PERSON: {

                    cameraProperties.setType(type);

                    entityProperties.getNode().flipVisibility(false);
//                    MovementSystem movementSystem = gameWorld.getSystem(MovementSystem.class);
//                    movementSystem.resetCameraPositions();
                    ((PlayerShipMotionState) entityProperties.getMotionState()).resetCameraPositions();
                }

                break;
                case THIRD_PERSON: {
                    cameraProperties.setType(type);
                    entityProperties.getNode().flipVisibility(false);
                }
                break;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    public void createSkybox(String name) {
        SceneCompositor.getSingleton().setInGameCompositor(name);
    }

    public EntityAndDistance getClosestChasingProjectile(Iterator<Long> chasingProjectileIterator, ENG_Vector4D pos) {
        EntityAndDistance ret = new EntityAndDistance();
        getClosestChasingProjectile(chasingProjectileIterator, pos, ret);
        return ret;
    }

    public void getClosestChasingProjectile(Iterator<Long> chasingProjectileIterator, ENG_Vector4D pos, EntityAndDistance ed) {
        float minDist = Float.MAX_VALUE;
        Entity minDistEntity = null;
        while (chasingProjectileIterator.hasNext()) {
            Long next = chasingProjectileIterator.next();
            Entity entity = getEntityByGameEntityId(next);
            if (entity != null) {
                entityPropertiesComponentMapper.get(entity).getNode().getPosition(chasingProjectilePos);
                float distance = chasingProjectilePos.distance(pos);
                if (minDist > distance) {
                    minDist = distance;
                    minDistEntity = entity;
                }
            }
        }
        ed.entity = minDistEntity;
        ed.distance = minDist;
    }

    public void setPlayerShipData(headwayent.blackholedarksun.entitydata.ShipData playerShipData) {
        this.playerShipData = playerShipData;
    }

    public long getPlayerShipEntityId() {
        return playerShipEntityId;
    }

    public void setSelectedLevel(int level) {

        currentLevel = level;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public Entity getCurrentBeacon() {

        return currentBeacon;
    }

    public Iterator<Long> getCargoNameListIterator() {
        return cargoIdList.iterator();
    }

    public void getClosestCargoSquaredDistance(ENG_Vector4D pos,
                                               ENG_ClosestObjectData data) {
        getClosestObjectSquaredDistance(pos, cargoIdList.iterator(), data);
    }

    public void getClosestCargo(ENG_Vector4D pos, ENG_ClosestObjectData data) {
        getClosestObject(pos, cargoIdList.iterator(), data);
    }

    public void initializeAmbientSounds() {
        Level level = (Level) gameWorld.getCurrentLevel();
        for (AmbientPlayable ambientSound : level.levelStart.ambientSounds) {
            playSoundBasedOnDistance(ambientSound, ambientSound.getSoundName(), true, false);
        }

    }

    private float getSoundPan(EntityProperties playerShipEntityProperties,
                              ENG_Playable playable) {
        ENG_Matrix4 transform = playerShipEntityProperties.getNode()
                ._getFullTransform();
        return getSoundPan(transform, playable);
    }

    private float getSoundPan(ENG_Matrix4 transform,
                              ENG_Playable playable) {
        transform.invert();
        ENG_Vector4D position = playable.getPosition();
        transform.transform(position);
        float distFromLeftEar = position.distance(leftEar);
        float distFromRightEar = position.distance(rightEar);
        float pan = (distFromLeftEar - distFromRightEar) / leftEar.distance(rightEar);
//		float pan = position.x * PAN_DISTANCE_INV;
        pan = ENG_Math.clamp(pan, -1.0f, 1.0f);
        return pan;
    }

    protected void updateSounds(long currentTime) {
        if (ENG_SoundManager.getSoundEngine() == ENG_SoundManager.SoundEngine.MINIAUDIO_3D) {
            if (currentTime - lastSoundUpdateTime <= SOUND_UPDATE_MIN_DELTA) {
                return;
            }
            lastSoundUpdateTime = currentTime;
            ENG_ISoundRoot soundRoot = MainApp.getGame().getSound();
            Entity playerShip = getShipByGameEntityId(getPlayerShipEntityId());
            ENG_SceneNode listeningNode = null;
            EntityProperties playerShipEntityProperties = null;
            if (playerShip != null) {
                playerShipEntityProperties = entityPropertiesComponentMapper.get(playerShip);
                CameraProperties playerShipCameraProperties = cameraPropertiesComponentMapper.get(playerShip);
                if (playerShipCameraProperties.getType() == CameraType.FIRST_PERSON) {
                    listeningNode = playerShipEntityProperties.getNode();
                } else {
                    listeningNode = cameraNode;
                }
            } else {
                if (!isCameraNodeAvailable()) return;
                listeningNode = cameraNode;
            }
            soundRoot.setListenerPosition(listeningNode.getPosition());
            soundRoot.setListenerFrontDirection(listeningNode.getLocalInverseZAxis());
            soundRoot.setListenerUpDirection(listeningNode.getLocalYAxis());
            if (playerShipEntityProperties != null) {
                ENG_Vector4D entityVelocity = playerShipEntityProperties.getEntityVelocity();
                ENG_Vector4D soundVelocity = entityVelocity.divAsVec(new ENG_Vector4D(1.0f, 1.0f,
                        playerShipEntityProperties.getMaxSoundSpeed() * ENG_SoundManager.MINIAUDIO_3D_SOUND_SPEED_ATTENUATION, 0.0f));
                ENG_Vector4D orientedSoundVelocity = playerShipEntityProperties.getOrientation().mul(soundVelocity);
                soundRoot.setListenerVelocity(soundVelocity);
//                System.out.println("Updating player soundVelocity: " + soundVelocity + " orientedSoundVelocity: " + orientedSoundVelocity + " entity velocity: " + entityVelocity);
            }

            ArrayList<ENG_Playable> entitiesToDelete = new ArrayList<>();
            for (Entry<ENG_Playable, ArrayList<Sound>> entry : currentPlayingSounds.entrySet()) {
                ENG_Playable playable = entry.getKey();
                ArrayList<Sound> soundsToBeDeleted = new ArrayList<>();
                for (Sound sound : entry.getValue()) {
                    if (soundRoot.isSoundEnded(sound.id)) {
                        soundsToBeDeleted.add(sound);
//                        System.out.println("Sound ended name: " + sound.name + " playId: " + sound.id);
                    } else {
//                        System.out.println("Updating sound: " + sound.name + " for node: " + playable.getSceneNode().getName() + " playId: " + sound.id);
                        soundRoot.setSoundPosition(sound.id, playable.getPosition());
                        soundRoot.setSoundFrontDirection(sound.id, playable.getFrontVec());
                        ENG_Vector4D soundVelocity = playable.getEntityVelocity().divAsVec(new ENG_Vector4D(1.0f, 1.0f,
                                sound.maxSoundSpeed * ENG_SoundManager.MINIAUDIO_3D_SOUND_SPEED_ATTENUATION, 0.0f));
                        ENG_Vector4D orientedSoundVelocity = playable.getOrientation().mul(soundVelocity);
//                        System.out.println("Updating soundVelocity: " + soundVelocity + " orientedSoundVelocity: " + orientedSoundVelocity + " entity velocity: " + playable.getEntityVelocity());
                        soundRoot.setSoundVelocity(sound.id, soundVelocity);
//                        soundRoot.setSoundVelocity(sound.id, new ENG_Vector4D(0, 0, -1, 0));
                        soundRoot.setSoundDopplerFactor(sound.id, playable.getDopplerFactor());
                    }
                }
                entry.getValue().removeAll(soundsToBeDeleted);
                if (entry.getValue().isEmpty()) {
                    entitiesToDelete.add(entry.getKey());
                }
            }
            for (ENG_Playable e : entitiesToDelete) {
                currentPlayingSounds.remove(e);
            }
            soundRoot.updateSoundSystem();
        } else {
            if (currentTime - lastSoundUpdateTime <= SOUND_UPDATE_MIN_DELTA) {
                return;
            }
            lastSoundUpdateTime = currentTime;
//            System.out.println("updateSounds()");
//        System.gc();
            Entity playerShip = getShipByGameEntityId(getPlayerShipEntityId());
            if (playerShip != null) {
                EntityProperties playerShipEntityProperties = entityPropertiesComponentMapper.get(playerShip);
                ArrayList<ENG_Playable> entitiesToDelete = new ArrayList<>();
                for (Entry<ENG_Playable, ArrayList<Sound>> entry : currentPlayingSounds.entrySet()) {
                    float distance = entry.getKey().getPosition().distance(playerShipEntityProperties.getNode().getPosition());
//				if (distance < MAX_SOUND_DISTANCE) {
                    ENG_ISoundRoot sound = MainApp.getGame().getSound();
//					int volume = ENG_SoundManager.MAX_SOUND_VOLUME
//							- (int) (distance / MAX_SOUND_DISTANCE * 100.0f);
//					volume = ENG_Math.clamp(volume, 0, ENG_SoundManager.MAX_SOUND_VOLUME);
                    ArrayList<Sound> soundsToBeDeleted = new ArrayList<>();
                    for (Sound snd : entry.getValue()) {
                        int volume = (int) ((ENG_SoundManager.MAX_SOUND_VOLUME - (int) (distance / snd.maxDistance * 100.0f)) * snd.volumeMultiplier);
                        // You cannot have a looping sound that also has a beginTime.
                        if ((!snd.loop && ENG_Utility.hasTimePassed(snd.beginTime, snd.duration))
                                || (snd.stopSound) || (snd.startOnProximity && volume <= 0 && snd.started)) {
                            sound.stopSound(snd.name, snd.id);
                            if (snd.startOnProximity && snd.started) {
                                snd.started = false;
                            }
                            /*System.out.println("sound " + snd.name +
                                    " stopped with id: " + snd.id);*/
                            if (!snd.doNotDelete) {
                                soundsToBeDeleted.add(snd);
                            }
                        } else {
                            if (snd.startOnProximity && volume > 0 && !snd.started) {
                                float pan = getSoundPan(playerShipEntityProperties, entry.getKey());
                                snd.volume = volume;
                                snd.pan = pan;
                                long id = sound.playSound(snd.name, volume, pan, snd.loop ? PlayType.PLAY_LOOP : PlayType.PLAY_ONCE);
//                                System.out.println("startOnProximity playSound() id: " + id);
                                if (id != -1) {
                                    snd.id = id;
                                    snd.started = true;
                                }
                            }

//							sound.setVolume(snd.name, snd.id, volume);
                            if (snd.id != -1) {
                                float pan = getSoundPan(playerShipEntityProperties, entry.getKey());
                                snd.pan = pan;
                                snd.previousVolume = snd.volume;
                                snd.volume = volume;
                                if (snd.previousVolume != 0 || snd.volume != 0) {
                                    sound.setPanAndVolume(snd.name, snd.id, pan, snd.volume);
                                }

//                                System.out.println("sound " + snd.name +
//                                        " set volume with id: " + snd.id +
//                                        " volume: " + volume + " pan: " + pan);
                            }
                        }
                    }
                    entry.getValue().removeAll(soundsToBeDeleted);
                    if (entry.getValue().isEmpty()) {
                        entitiesToDelete.add(entry.getKey());
                    }
//				}
                }
                for (ENG_Playable e : entitiesToDelete) {
                    currentPlayingSounds.remove(e);
                }
            }
        }
    }

    public Sound playSoundFromCameraNode(String soundName) {
        return playSoundFromCameraNode(soundName, false, false);
    }

    public Sound playSoundFromCameraNode(String soundName, boolean loop, boolean startOnProximity) {
        if (cameraNode == null) {
            System.out.println("cameraNode == null when playing sound: " + soundName);
            return null;
        }
        return playSoundBasedOnDistance(new CameraNodePlayable(cameraNode), soundName, loop, startOnProximity);
    }

    public Sound playSoundBasedOnDistance(ENG_Playable playable,
                                          String soundName) {
        return playSoundBasedOnDistance(playable, soundName, false, false);
    }

    public Sound playSoundBasedOnDistance(ENG_Playable playable,
                                          String soundName, boolean loop, boolean startOnProximity) {
        // The sound was calculated relative to the player ship but it's more correct to calculate
        // it relative to the camera. Also, we are guaranteed a camera but not a player ship.
        Entity playerShip = getShipByGameEntityId(getPlayerShipEntityId());
        ENG_SceneNode listeningNode = null;
        if (playerShip != null) {
            EntityProperties playerShipEntityProperties = entityPropertiesComponentMapper.get(playerShip);
            listeningNode = playerShipEntityProperties.getNode();
        } else {
            if (!isCameraNodeAvailable()) return null;
            listeningNode = cameraNode;
        }
        return playSoundBasedOnDistance(listeningNode, playable, soundName, loop, startOnProximity);
    }

    private boolean isCameraNodeAvailable() {
        if (MainApp.getGame().getGameMode() == APP_Game.GameMode.SP) {
            // We are in a cutscene and don't have a player ship so we use the camera node.
            if (cameraNode == null) {
                Level level = (Level) gameWorld.getCurrentLevel();
                if (level != null && level.cutsceneActive) {
                    throw new NullPointerException("cameraNode has not been initialized");
                } else {
                    return false;
                }
            }
        } else if (MainApp.getGame().getGameMode() == APP_Game.GameMode.MP) {
            // If the player ship spawn command hasn't yet arrived we can have a null cameraNode.
            if (cameraNode == null) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param listeningNode only used for non-MiniAudio3D
     * @param playable
     * @param soundName
     * @return
     */
    public Sound playSoundBasedOnDistance(ENG_SceneNode listeningNode, ENG_Playable playable,
                                          String soundName) {
        return playSoundBasedOnDistance(listeningNode, playable, soundName,false, false);
    }

    /**
     *
     * @param listeningNode only used for non-MiniAudio3D
     * @param playable
     * @param soundName
     * @param loop
     * @param startOnProximity
     * @return
     */
    public Sound playSoundBasedOnDistance(ENG_SceneNode listeningNode, ENG_Playable playable,
                                          String soundName, boolean loop, boolean startOnProximity) {
        ENG_ISoundRoot soundRoot = MainApp.getGame().getSound();
        if (ENG_SoundManager.getSoundEngine() == ENG_SoundManager.SoundEngine.MINIAUDIO_3D) {
            long playId = soundRoot.createSoundObject(soundName);
            Sound sound = new Sound();
            sound.name = soundName;
            sound.id = playId;
            sound.loop = loop;
            sound.startOnProximity = startOnProximity;
            sound.duration = soundRoot.getSoundDuration(soundName);
            sound.maxSoundSpeed = playable.getMaxSoundSpeed();
            soundRoot.setSoundRolloff(playId, 0.01f);
            soundRoot.playSoundObject(playId, loop);
//            soundRoot.setSoundAttenuationModel(playId, MAAttenuationModel.LINEAR);
//            System.out.println("Playing sound: " + soundName + " playId: " + playId + " maxSoundSpeed: " + sound.maxSoundSpeed);
            ArrayList<Sound> list = currentPlayingSounds.get(playable);
            if (list == null) {
                list = new ArrayList<>();
                currentPlayingSounds.put(playable, list);
            }
            list.add(sound);
            return sound;
        } else {
            if (startOnProximity) {
                // Do not start the sound unless in close proximity of the ship
                ArrayList<Sound> list = currentPlayingSounds.get(playable);
                if (list == null) {
                    list = new ArrayList<>();
                    currentPlayingSounds.put(playable, list);
                }
                Sound snd = new Sound(soundName, loop, true, true, currentTimeMillis(), soundRoot.getSoundDuration(soundName));
                list.add(snd);
                return snd;
            } else {

                float distance = playable.getPosition().distance(listeningNode.getPosition());
                //			if (distance < MAX_SOUND_DISTANCE) {
                int volume = ENG_SoundManager.MAX_SOUND_VOLUME - (int) (distance / MAX_SOUND_DISTANCE * 100.0f);
                volume = ENG_Math.clamp(volume, 0, ENG_SoundManager.MAX_SOUND_VOLUME);
                //				if (volume > 0) {
                //					sound.setVolume(soundName, volume);
                //					Timer timer = ENG_Utility.createTimerAndStart();
                float pan = getSoundPan(listeningNode._getFullTransform(), playable);
                long id = soundRoot.playSound(soundName, volume, pan, loop ? PlayType.PLAY_LOOP : PlayType.PLAY_ONCE);
                //					ENG_Utility.stopTimer(timer, "playSoundBasedOnDistance()");
                if (id != -1) {
                    ArrayList<Sound> list = currentPlayingSounds.get(playable);
                    if (list == null) {
                        list = new ArrayList<>();
                        currentPlayingSounds.put(playable, list);
                    }
                    Sound snd;
                    if (!loop) {
                        snd = new Sound(soundName, id, volume, pan, currentTimeMillis(), soundRoot.getSoundDuration(soundName));
                    } else {
                        snd = new Sound(soundName, id, volume, pan, true, currentTimeMillis(), soundRoot.getSoundDuration(soundName));
                    }
                    list.add(snd);
                    return snd;
                }
                //				}
                //			}
            }
        }
        return null;
    }

    @Override
    public void destroyEntity(EntityProperties entityProperties) {
        entityProperties.setDestroyed(true);
        Animation destroyedAnimation = entityProperties.getDestroyedAnimation();
        if (destroyedAnimation != null) {
            startAnimation(entityProperties.getEntityId(), destroyedAnimation);
        }
        // Play the destruction sound if no animation
        // Otherwise the sound will be started by the animation.
        if (entityProperties.getDestroyedAnimation() == null) {
            String destructionSoundName = entityProperties.getDestructionSoundName();
            if (destructionSoundName != null) {
                playSoundBasedOnDistance(entityProperties, destructionSoundName);
            }
        }
    }


    public static WorldManager getSingleton() {
        return (WorldManager) MainApp.getGame().getWorldManager();
    }

    /** @noinspection deprecation */
    @Override
    protected Entity createProjectileEntity(long nextId,//WeaponProperties weaponProperties,
                                            EntityProperties shipEntityProperties,
                                            ShipProperties shipProperties, //WeaponData.WeaponType weaponType,
                                            final String meshName, headwayent.blackholedarksun.entitydata.WeaponData weaponData, ENG_Vector4D pos,
                                            ENG_Quaternion orientation, boolean tracking) {
        // Sometimes this bug may happen:
        // A ship gets destroyed and all its chasing projectiles get removed
        // but in the same frame, just before the entity remover gets to
        // remove the ship from the scene, a new projectile gets added in
        // the ship's chasingprojectilelist. So don't create tracking weapons
        // after a ship's death
        if (tracking) {
            if (checkShipDestroyed(shipProperties)) return null;
        }
//        int nextId = weaponProperties.getNextId();
        String name = shipEntityProperties.getName() + "_" + headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.getWeapon(weaponData.weaponType) + nextId;
        // System.out.println("creating projectile: " + name);
        ENG_Item item = sceneManager.createItem(EntityProperties.generateUniqueName(name, gameEntityId), ENG_Utility.getUniqueId(),
                meshName, "", ENG_Workflows.MetallicWorkflow);
//        ENG_Entity entity = sceneManager.createEntity(EntityProperties.generateUniqueName(name, gameEntityId), gameEntityId, meshName, ENTITY_GROUP_NAME);


        EntityAabb entityAabb = getEntityAabb(meshName);

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
        // entityProp.setPosition(i == 0 ? laserLeftOffset : laserRightOffset);
        // entityProp.getNode().rotate(currentShipOrientation,
        // TransformSpace.TS_PARENT);
//        entityProp.setOnRemove(new EntityProperties.IRemovable() {
//            @Override
//            public void onRemove(Entity entity) {
//                projectileList.remove(entity);
//            }
//        });
        entityProp.setOrientation(orientation);
        entityProp.setPosition(pos);

        node.setPosition(pos.x, pos.y, pos.z);
        node.setOrientation(orientation);
        node._updateWithoutBoundsUpdate(false, false);

        ProjectileProperties projectileProp = projectilePropertiesComponentMapper.create(gameEntity);
//        ProjectileProperties projectileProp = new ProjectileProperties(weaponData.weaponType, entityProperties.getName(), entityProperties.getEntityId(), nextId);
        projectileProp.setType(weaponData.weaponType);
        projectileProp.setParentName(shipEntityProperties.getName());
        projectileProp.setParentId(shipEntityProperties.getEntityId());
        projectileProp.setId(nextId);
//        gameEntity.addComponent(entityProp);
//        gameEntity.addComponent(projectileProp);
        updateProjectileData(weaponData, entityProp);
        entityProp.setDestructionSoundName(WeaponData.WeaponType.getProjectileHitSoundName(weaponData.weaponType));
        entityProp.setDestroyedAnimation(new ProjectileExplosionAnimation(item.getName(), gameEntity));
        // projectileProp.setMaxTurnAngle(weaponData.turnAngle);
        TrackerProperties trackerProperties = null;
        if (tracking) {
            // ENG_MovableObject currentSelectedEnemy =
            // HudManager.getSingleton().getCurrentSelectedEnemy();
            entityProp.setOnDestroyedEvent(new WeaponData.WeaponOnDestroyedEvent(gameEntity));
            long currentSelectedEnemy = shipProperties.getCurrentSelectedEnemy();
            if (currentSelectedEnemy != -1) {
                trackerProperties = trackerPropertiesComponentMapper.create(gameEntity);
                trackerProperties.setTrackedEntityId(currentSelectedEnemy);
                trackerProperties.setMaxAngularVelocity(weaponData.maxAngularVelocity);
                trackerProperties.setTrackingDelay(WeaponData.WeaponType.getHomingMissileTrackingDelay(weaponData.weaponType));
                trackerProperties.setTrackingDelayTimeStarted();
//                gameEntity.addComponent(trackingProperties);
                Entity ship = getEntityByItemId(currentSelectedEnemy);
                if (ship != null) {
                    shipPropertiesComponentMapper.get(ship).addChasingProjectile(gameEntityId);
                }
            }
        }
//        gameWorld.addEntity(gameEntity);

        EntityMotionState motionState = new EntityMotionState(entityProp, projectileProp, trackerProperties);
        short collisionGroup = PhysicsProperties.CollisionGroup.STANDARD.getVal();
        short collisionMask = PhysicsProperties.CollisionMask.STANDARD.getVal();

        float weight = entityProp.getWeight();

        createPhysicsBody(gameEntity, entityAabb.halfSize, entityProp, motionState, collisionGroup, collisionMask, weight);

        // Disable collision between the ship and the just launched projectile.
        entityProp.getRigidBody().setIgnoreCollisionCheck(shipEntityProperties.getRigidBody(), true);

        incrementGameEntityId();
        return gameEntity;
    }

    protected void updateProjectileData(WeaponData weaponData, EntityProperties entityProp) {
        entityProp.setDamage(weaponData.damage);
        entityProp.setHealth(weaponData.health);
        entityProp.setWeight(weaponData.weight);
        entityProp.setVelocity(weaponData.maxSpeed);
        entityProp.setMaxSpeed(weaponData.maxSpeed);
    }

    public void setDeathCam(Entity entity, EntityProperties entityProperties, CameraProperties cameraProperties) {
        PlayerShipDeathCamAnimation deathCamAnimation = new PlayerShipDeathCamAnimation(
                "PlayerShipDeathCamAnimation_" + entityProperties.getUniqueName(), entity, cameraProperties.getCamera());
        startAnimation(entityProperties.getEntityId(), deathCamAnimation);
//        ENG_Vector4D position = entityProperties.getNode().getPosition();

//        ENG_Vector4D vec = new ENG_Vector4D(
//                ENG_Utility.getRandom().nextFloat(FrameInterval.PLAYER_SHIP_DESTRUCTION_X + entityProperties.getUniqueName()) * 300.0f - 150.0f,
//                ENG_Utility.getRandom().nextFloat(FrameInterval.PLAYER_SHIP_DESTRUCTION_Y + entityProperties.getUniqueName()) * 300.0f - 150.0f,
//                ENG_Utility.getRandom().nextFloat(FrameInterval.PLAYER_SHIP_DESTRUCTION_Z + entityProperties.getUniqueName()) * 300.0f - 150.0f, 1.0f);
//        cameraProperties.getNode().setPosition(position.addAsPt(vec));
//        cameraProperties.getCamera().lookAt(position);
        System.out.println("player ship destroyed");
    }

    public abstract void reloadLevelDataAndUpdateCurrentEntities();

    public abstract void onPlayerShipDestroyedAnimationFinished();

    public abstract void onPlayerShipDestroyed(Entity entity, EntityProperties entityProperties, ShipProperties shipProperties, CameraProperties cameraProperties);
}
