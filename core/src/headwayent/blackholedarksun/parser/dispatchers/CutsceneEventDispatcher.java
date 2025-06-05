/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.dispatchers;

import com.artemis.ComponentMapper;
import com.artemis.Entity;

import java.util.ArrayList;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.blackholedarksun.levelresource.LevelEvent;
import headwayent.blackholedarksun.menus.Subtitles;
import headwayent.blackholedarksun.parser.ast.CameraAttachEvent;
import headwayent.blackholedarksun.parser.ast.CameraDetachEvent;
import headwayent.blackholedarksun.parser.ast.CameraEvent;
import headwayent.blackholedarksun.parser.ast.CameraLookAt;
import headwayent.blackholedarksun.parser.ast.ChangeOrientation;
import headwayent.blackholedarksun.parser.ast.ChangePosition;
import headwayent.blackholedarksun.parser.ast.ChangeSpeed;
import headwayent.blackholedarksun.parser.ast.CompletionTime;
import headwayent.blackholedarksun.parser.ast.DelayedEvent;
import headwayent.blackholedarksun.parser.ast.Event;
import headwayent.blackholedarksun.parser.ast.Exit;
import headwayent.blackholedarksun.parser.ast.ObjDefinition;
import headwayent.blackholedarksun.parser.ast.ObjectEvent;
import headwayent.blackholedarksun.parser.ast.Orientation;
import headwayent.blackholedarksun.parser.ast.ParallelTask;
import headwayent.blackholedarksun.parser.ast.PlaySound;
import headwayent.blackholedarksun.parser.ast.Position;
import headwayent.blackholedarksun.parser.ast.SetSpeed;
import headwayent.blackholedarksun.parser.ast.ShowText;
import headwayent.blackholedarksun.parser.ast.Spawn;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.blackholedarksun.world.WorldManagerSP;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.audio.ENG_Playable;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_SceneManager;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

import static headwayent.hotshotengine.ENG_Utility.currentTimeMillis;

public class CutsceneEventDispatcher extends AbstractEventDispatcher {

    private interface DelayStartCallable {
        boolean call(Event event);
    }

    private interface DelayEndCallable {
        boolean call(Event event);
    }

    private final WorldManagerSP worldManager;
    private ENG_SceneNode cameraNode;
    private String cameraAttachedObject;
    private Event currentEvent;

    public CutsceneEventDispatcher(WorldManagerSP worldManager) {
        this.worldManager = worldManager;
    }

    @Override
    public void begin() {
        boolean ignoreEvents = false;
    }

    @Override
    public void end() {

    }

    /**
     * If there is no current event then we consider the new event good to run.
     * @param event
     * @return
     */
    private boolean isCurrentEvent(Event event) {
        return currentEvent == event || currentEvent == null;
    }

    private boolean checkDelayStart(DelayedEvent event, DelayStartCallable callable) {
        boolean eventHandled = false;
        if (event.getState() == Event.EventState.NONE) {
            event.setState(Event.EventState.STARTABLE);
            event.currentStartingTime = currentTimeMillis();
            eventHandled = true;
        } else if (event.getState() == Event.EventState.STARTABLE) {
            long delay = event.getDelayStart() == null ? 0 :
                    WorldManagerBase.getDelay(event.getDelayStart().getDelayType(), event.getDelayStart().getTime());
            if (ENG_Utility.hasTimePassed(FrameInterval.CUTSCENE_OBJECT_EVENT_START_TIME + event.name, event.currentStartingTime, delay)) {
                event.currentStartingTime = currentTimeMillis();
                event.setState(Event.EventState.STARTED);
                if (callable != null) {
                    eventHandled = callable.call(event);
                } else {
                    eventHandled = true;
                }
            }
            eventHandled = true;
        }
        return eventHandled;
    }

    private boolean checkDelayEnd(DelayedEvent event, DelayEndCallable callable) {
        boolean eventHandled = false;
        if (event.getState() == Event.EventState.FINISHABLE) {
            event.setState(Event.EventState.WAITING_FOR_END_DELAY);
            event.currentStartingTime = currentTimeMillis();
            eventHandled = true;
        } else if (event.getState() == Event.EventState.WAITING_FOR_END_DELAY) {
            long delay = event.getDelayEnd() == null ? 0 :
                    WorldManagerBase.getDelay(event.getDelayEnd().getDelayType(), event.getDelayEnd().getTime());
            if (ENG_Utility.hasTimePassed(FrameInterval.CUTSCENE_OBJECT_EVENT_END_TIME + event.name, event.currentStartingTime, delay)) {
                event.currentStartingTime = currentTimeMillis();
                event.setState(Event.EventState.FINISHED);
                if (callable != null) {
                    eventHandled = callable.call(event);
                } else {
                    eventHandled = true;
                }
            }
            eventHandled = true;
        }
        return eventHandled;
    }

    @Override
    public boolean dispatch(final ObjectEvent event) {
        boolean eventHandled = true;
        if (checkDelayStart(event, event1 -> {
//                if (!event.getObjDefinitionList().isEmpty()) {
//
//                    LevelEvent levelEvent = event.getAsLevelEvent();
//                    worldManager.spawnObjects(levelEvent);
//                    worldManager.exitObjectsFromLevel(levelEvent);
//                }
            return true;
        })) {
            return true;
        }
        if (event.getState() == Event.EventState.STARTED) {
            if (event.getSetSpeed() != null && !event.getSetSpeed().isExecuted()) {
                SetSpeed setSpeed = event.getSetSpeed();
                Entity levelObject = worldManager.getLevelObject(event.getGameObject().getGameObjectName());
                ComponentMapper<EntityProperties> entityPropertiesComponentMapper = worldManager.getEntityPropertiesComponentMapper();
                EntityProperties entityProperties = entityPropertiesComponentMapper.get(levelObject);
                entityProperties.setVelocity(setSpeed.getSpeed());
                setSpeed.setExecuted(true);
                event.setState(Event.EventState.FINISHABLE);
            }
            boolean changeEvent = false;
            if (event.getChangeSpeed() != null && !event.getChangeSpeed().isExecuted()) {
                ChangeSpeed changeSpeed = event.getChangeSpeed();
                Entity levelObject = worldManager.getLevelObject(event.getGameObject().getGameObjectName());
                ComponentMapper<EntityProperties> entityPropertiesComponentMapper = worldManager.getEntityPropertiesComponentMapper();
                ComponentMapper<ShipProperties> shipPropertiesComponentMapper = worldManager.getShipPropertiesComponentMapper();
                EntityProperties entityProperties = entityPropertiesComponentMapper.get(levelObject);
                ShipProperties shipProperties = shipPropertiesComponentMapper.get(levelObject);
                float currentVelocity = entityProperties.getVelocity();
                float finalVelocity = changeSpeed.getFinalSpeed();
                if (finalVelocity < 0.0f || finalVelocity > shipProperties.getShipData().maxSpeed) {
                    throw new IllegalArgumentException("finalVelocity outside of range 0 and " + shipProperties.getShipData().maxSpeed);
                }
                if (Math.abs(currentVelocity - finalVelocity) < ENG_Math.FLOAT_EPSILON) {
                    event.setState(Event.EventState.FINISHABLE);
                    changeSpeed.setExecuted(true);
                } else {
                    entityProperties.setVelocity(shipProperties.getNextVelocityDelta(
                            currentVelocity,
                            finalVelocity,
                            shipProperties.getShipData().acceleration,
                            finalVelocity > currentVelocity ? 1 : -1));
                }
                changeEvent = true;
            }
            if (event.getChangePosition() != null && !event.getChangePosition().isExecuted()) {
                if (changeEvent) {
                    throw new IllegalArgumentException("Cannot have multiple change_* in one event");
                }
                if (event.getCompletionTime() == null) {
                    throw new NullPointerException("change_position must have a completion time");
                }
                ChangePosition changePosition = event.getChangePosition();
                CompletionTime completionTime = event.getCompletionTime();
                if (completionTime.getBeginTime() != 0) {
                    Entity levelObject = worldManager.getLevelObject(event.getGameObject().getGameObjectName());
                    ComponentMapper<EntityProperties> entityPropertiesComponentMapper = worldManager.getEntityPropertiesComponentMapper();
                    ComponentMapper<ShipProperties> shipPropertiesComponentMapper = worldManager.getShipPropertiesComponentMapper();
                    EntityProperties entityProperties = entityPropertiesComponentMapper.get(levelObject);
                    if (changePosition.getBeginPosition() == null) {
                        changePosition.setBeginPosition(entityProperties.getNode().getPositionForNative().getAsVector3D());
                    } else {
                        ENG_Vector3D interpolate = ENG_Utility.interpolate(completionTime.getBeginTime(),
                                WorldManagerBase.getDelay(completionTime.getTimeType(), completionTime.getTime()),
                                ENG_Utility.currentTimeMillis(), changePosition.getBeginPosition(), changePosition.getFinalPosition());
                        entityProperties.setPositionWithoutPhysics(interpolate.getAsVector4D(true));
                        if (interpolate.equals(changePosition.getFinalPosition())) {
                            changePosition.setExecuted(true);
                            event.setState(Event.EventState.FINISHABLE);
                        }
                    }
                }
                changeEvent = true;
            }
            if (event.getChangeOrientation() != null && !event.getChangeOrientation().isExecuted()) {
                if (changeEvent) {
                    throw new IllegalArgumentException("Cannot have multiple change_* in one event");
                }
                if (event.getCompletionTime() == null) {
                    throw new NullPointerException("change_orientation must have a completion time");
                }
                ChangeOrientation changeOrientation = event.getChangeOrientation();
                CompletionTime completionTime = event.getCompletionTime();
                if (completionTime.getBeginTime() != 0) {
                    Entity levelObject = worldManager.getLevelObject(event.getGameObject().getGameObjectName());
                    ComponentMapper<EntityProperties> entityPropertiesComponentMapper = worldManager.getEntityPropertiesComponentMapper();
                    ComponentMapper<ShipProperties> shipPropertiesComponentMapper = worldManager.getShipPropertiesComponentMapper();
                    EntityProperties entityProperties = entityPropertiesComponentMapper.get(levelObject);
                    if (changeOrientation.getBeginOrientation() == null) {
                        changeOrientation.setBeginOrientation(entityProperties.getNode().getOrientation());
                    } else {
                        ENG_Quaternion interpolate = ENG_Utility.interpolateSlerp(completionTime.getBeginTime(),
                                WorldManagerBase.getDelay(completionTime.getTimeType(), completionTime.getTime()),
                                ENG_Utility.currentTimeMillis(), changeOrientation.getBeginOrientation(), changeOrientation.getFinalOrientation());
                        entityProperties.setOrientationWithoutPhysics(interpolate);
                        if (interpolate.equals(changeOrientation.getFinalOrientation())) {
                            changeOrientation.setExecuted(true);
                            event.setState(Event.EventState.FINISHABLE);
                        }
                    }
                }
                changeEvent = true;
            }
            if (event.getCompletionTime() != null && !event.getCompletionTime().isExecuted()) {
                CompletionTime completionTime = event.getCompletionTime();
                if (completionTime.getBeginTime() == 0) {
                    completionTime.setBeginTime(ENG_Utility.currentTimeMillis());
                } else if (ENG_Utility.hasTimePassed(FrameInterval.CUTSCENE_OBJECT_EVENT_COMPLETION_TIME + event.name,
                        completionTime.getBeginTime(),
                        WorldManagerBase.getDelay(completionTime.getTimeType(), completionTime.getTime()))) {
                    completionTime.setExecuted(true);
                    event.setState(Event.EventState.FINISHABLE);
                }
            }
            if (event.getPosition() != null && !event.getPosition().isExecuted()) {
                Position position = event.getPosition();
                Entity levelObject = worldManager.getLevelObject(event.getGameObject().getGameObjectName());
                ComponentMapper<EntityProperties> entityPropertiesComponentMapper = worldManager.getEntityPropertiesComponentMapper();
                ComponentMapper<ShipProperties> shipPropertiesComponentMapper = worldManager.getShipPropertiesComponentMapper();
                EntityProperties entityProperties = entityPropertiesComponentMapper.get(levelObject);
                entityProperties.setPositionWithoutPhysics(position.getPosition().getAsVector4D());
                position.setExecuted(true);
                event.setState(Event.EventState.FINISHABLE);
            }
            if (!event.getSpawnList().isEmpty() && !event.getSpawnList().get(0).isExecuted()) {
                ArrayList<ObjDefinition> objDefinitionList = event.getObjDefinitionList();
                if (objDefinitionList.isEmpty()) {
                    throw new IllegalStateException("Cannot spawn without the object definition");
                }
                ArrayList<ObjDefinition> spawnObjDefinitionLiat = new ArrayList<>();
                boolean found = false;
                for (Spawn spawn : event.getSpawnList()) {
                    found = false;
                    for (ObjDefinition objDefinition : objDefinitionList) {
                        if (objDefinition.getObjName().equalsIgnoreCase(spawn.getSpawnName())) {
                            spawnObjDefinitionLiat.add(objDefinition);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        throw new IllegalStateException("Could not find object to spawn by name: " + spawn.getSpawnName());
                    }
                }
                LevelEvent levelEvent = event.getAsLevelEvent(spawnObjDefinitionLiat);
                worldManager.spawnObjects(levelEvent);
                event.getSpawnList().get(0).setExecuted(true);
                event.setState(Event.EventState.FINISHABLE);
            }
            if (!event.getExitList().isEmpty() && !event.getExitList().get(0).isExecuted()) {
                ArrayList<ObjDefinition> objDefinitionList = event.getObjDefinitionList();
                if (objDefinitionList.isEmpty()) {
                    throw new IllegalStateException("Cannot exit a spawned object without the object definition");
                }
                ArrayList<ObjDefinition> exitObjDefinitionLiat = new ArrayList<>();
                boolean found = false;
                for (Exit exit : event.getExitList()) {
                    found = false;
                    for (ObjDefinition objDefinition : objDefinitionList) {
                        if (objDefinition.getObjName().equalsIgnoreCase(exit.getExitName())) {
                            exitObjDefinitionLiat.add(objDefinition);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        throw new IllegalStateException("Could not find object to exit from cutscene by name: " + exit.getExitName());
                    }
                }
                LevelEvent levelEvent = event.getAsLevelEvent(exitObjDefinitionLiat);
                worldManager.exitObjectsFromLevel(levelEvent);
                event.getExitList().get(0).setExecuted(true);
                event.setState(Event.EventState.FINISHABLE);
            }
//            if (!event.getObjDefinitionList().isEmpty()) {
//                WorldManager.getSingleton().createEntitiesV2(event.getObjDefinitionList());
//                for (ObjDefinition objDefinition : event.getObjDefinitionList()) {
//                    objDefinition.setExecuted(true);
//                }
//                event.setState(Event.EventState.FINISHABLE);
//            }
//            if (event.getDelayEnd() != null) {

//            }
        }
        if (checkDelayEnd(event, event1 -> true)) {
            return true;
        }
        return eventHandled;
    }

    @Override
    public boolean dispatch(CameraEvent event) {
        boolean eventHandled = true;
        if (checkDelayStart(event, null)) {
            return true;
        }
        if (event.getState() == Event.EventState.STARTED) {
            if (event.getPosition() != null && !event.getPosition().isExecuted()) {
                Position position = event.getPosition();
                cameraNode.setPosition(position.getPosition());
                position.setExecuted(true);
                event.setState(Event.EventState.FINISHABLE);
            }
            if (event.getOrientation() != null && !event.getOrientation().isExecuted()) {
                Orientation orientation = event.getOrientation();
                cameraNode.setOrientation(orientation.getOrientation());
                orientation.setExecuted(true);
                event.setState(Event.EventState.FINISHABLE);
            }
            if (event.getLookAt() != null && !event.getLookAt().isExecuted()) {
                handleCameraLookAt(event, event.getLookAt());
            }
            if (event.getPlaySound() != null && !event.getPlaySound().isExecuted()) {
                PlaySound playSound = event.getPlaySound();
                if (playSound.getSoundName() != null) {
                    MainApp.getGame().playSoundMaxVolume(playSound.getSoundName());
                } else if (playSound.getObjName() != null) {
                    Entity levelObject = worldManager.getLevelObject(playSound.getObjName());
                    ComponentMapper<EntityProperties> entityPropertiesComponentMapper = worldManager.getEntityPropertiesComponentMapper();
                    EntityProperties entityProperties = entityPropertiesComponentMapper.get(levelObject);
                    worldManager.playSoundBasedOnDistance(entityProperties, playSound.getSoundName());
                } else if (playSound.getSoundPos() != null) {
                    worldManager.playSoundBasedOnDistance(cameraNode, new ENG_Playable() {

                        @Override
                        public String getName() {
                            return cameraNode.getName();
                        }

                        @Override
                        public ENG_Vector4D getPosition() {
                            return cameraNode.getPosition();
                        }

                        @Override
                        public void getPosition(ENG_Vector4D position) {
                            position.set(cameraNode.getPositionForNative());
                        }

                        @Override
                        public ENG_Quaternion getOrientation() {
                            return cameraNode.getOrientation();
                        }

                        @Override
                        public void getOrientation(ENG_Quaternion orientation) {
                            orientation.set(cameraNode.getOrientationForNative());
                        }

                        @Override
                        public ENG_SceneNode getSceneNode() {
                            return cameraNode;
                        }

                        @Override
                        public ENG_Vector4D getEntityVelocity() {
                            return ENG_Math.VEC4_ZERO;
                        }

                        @Override
                        public float getDopplerFactor() {
                            return 1.0f;
                        }

                        @Override
                        public float getMaxSoundSpeed() {
                            return 1.0f;
                        }
                    }, playSound.getSoundName());
                } else {
                    throw new IllegalStateException("No valid param for playing sound");
                }
                playSound.setExecuted(true);
                event.setState(Event.EventState.FINISHABLE);
            }
            if (event.getShowText() != null && !event.getShowText().isExecuted()) {
                ShowText showText = event.getShowText();
                Subtitles.showSubtitles(showText.getText(), WorldManagerBase.getDelay(showText.getTimeType(), showText.getTime()));
                showText.setExecuted(true);
                event.setState(Event.EventState.FINISHABLE);
            }
//            if (event.getDelayEnd() != null) {

//            }
        }
        if (checkDelayEnd(event, null)) {
            return true;
        }
        return eventHandled;
    }

    private void handleCameraLookAt(Event event, CameraLookAt lookAt) {
        if (cameraAttachedObject != null) {
            // We cannot have the camera attached because looking at a direction
            // will be relative to the attached point not to a free camera.
            // This can be changed in the future when looking will be done in
            // world space.
            throw new IllegalStateException("Cannot look at when attached");
        }
        if (lookAt.getObjectName() != null) {
            Entity levelObject = worldManager.getLevelObject(lookAt.getObjectName());
            ComponentMapper<EntityProperties> entityPropertiesComponentMapper = worldManager.getEntityPropertiesComponentMapper();
            EntityProperties entityProperties = entityPropertiesComponentMapper.get(levelObject);
            cameraNode.lookAt(entityProperties.getNode().getPosition());
//            Utility.lookAtV2(entityProperties.getNode().getPosition(), cameraNode);
//            System.out.println("Looking at entity: " + entityProperties.getNode().getName());
        } else if (lookAt.getPosition() != null) {
            cameraNode.lookAt(lookAt.getPosition().getAsVector4D(true));
//            Utility.lookAtV2(lookAt.getPosition().getAsVector4D(true), cameraNode);
        } else {
            throw new IllegalStateException("No object to look at and nor any position");
        }
        if (lookAt.isDelayActive()) {
            if (lookAt.getBeginTime() == 0) {
                lookAt.setBeginTime(ENG_Utility.currentTimeMillis());
            } else if (ENG_Utility.hasTimePassed(FrameInterval.CUTSCENE_CAMERA_EVENT_LOOK_AT_TIME + event.name,
                    lookAt.getBeginTime(),
                    WorldManagerBase.getDelay(lookAt.getDelayType(), lookAt.getTime()))) {
                lookAt.setExecuted(true);
                event.setState(Event.EventState.FINISHABLE);
            }
        } else {
            lookAt.setExecuted(true);
            event.setState(Event.EventState.FINISHABLE);
        }
    }

    @Override
    public boolean dispatch(CameraAttachEvent event) {
        boolean eventHandled = true;
        if (checkDelayStart(event, null)) {
            return true;
        }
        if (event.getState() == Event.EventState.STARTED) {
            // We can have a look at just before attaching. But we cannot have a continuous
            // following of something by the camera.
            // Why not? The current problem is that we look at the object with the current
            // camera node position before actually getting attached (on native side).
            // So we look from the initial pos to the object not after the attachment happened.
            // For now we will not let to look at continuously while attached to an object.
            if (event.getLookAt() != null && !event.getLookAt().isExecuted()) {
                handleCameraLookAt(event, event.getLookAt());
            }
            if (event.getAttach() != null && !event.getAttach().isExecuted()) {
                if (event.getLookAt() != null && event.getLookAt().isDelayActive()) {
                    // We cannot have the camera attached because looking at a direction
                    // will be relative to the attached point not to a free camera.
                    // This can be changed in the future when looking will be done in
                    // world space.
                    throw new IllegalStateException("Cannot look at when attached");
                }
                attachCameraToObject(event);
                event.getAttach().setExecuted(true);
//                event.setState(Event.EventState.FINISHABLE);
            }
            if (event.getPosition() != null && !event.getPosition().isExecuted()) {
                cameraNode.setPosition(event.getPosition().getPosition());
                event.getPosition().setExecuted(true);
//                event.setState(Event.EventState.FINISHABLE);
            }
            if (event.getOrientation() != null && !event.getOrientation().isExecuted()) {
                Orientation orientation = event.getOrientation();
                cameraNode.setOrientation(orientation.getOrientation());
                orientation.setExecuted(true);
//                event.setState(Event.EventState.FINISHABLE);
            }
//            if (event.getDelayEnd() != null) {

//            }
        }
        if (checkDelayEnd(event, null)) {
            return true;
        }
        return eventHandled;
    }

    private void attachCameraToObject(CameraAttachEvent event) {
        detachCameraAttachedObject();
        Entity levelObject = worldManager.getLevelObject(event.getAttach().getObjectName());
        ComponentMapper<EntityProperties> entityPropertiesComponentMapper = worldManager.getEntityPropertiesComponentMapper();
        EntityProperties entityProperties = entityPropertiesComponentMapper.get(levelObject);
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        sceneManager.getRootSceneNode().removeChildNative(cameraNode);
        entityProperties.getNode().addChildNative(cameraNode);
        cameraAttachedObject = event.getAttach().getObjectName();
        System.out.println("Camera attached to: " + cameraAttachedObject);
    }

    @Override
    public boolean dispatch(CameraDetachEvent event) {
        boolean eventHandled = true;
        if (checkDelayStart(event, null)) {
            return true;
        }
        if (event.getState() == Event.EventState.STARTED) {
            if (event.getDetach() != null && !event.getDetach().isExecuted()) {
//                if (cameraAttachedObject == null) {
//                    throw new IllegalArgumentException("cameraAttachedObject is null");
//                }
                detachCameraAttachedObject();
                event.getDetach().setExecuted(true);
                event.setState(Event.EventState.FINISHABLE);
            }
//            if (event.getDelayEnd() != null) {

//            }
        }
        if (checkDelayEnd(event, null)) {
            return true;
        }
        return eventHandled;
    }

    private void detachCameraAttachedObject() {
        if (cameraAttachedObject == null) {
            return;
        }
        Entity levelObject = worldManager.getLevelObject(cameraAttachedObject);
        ComponentMapper<EntityProperties> entityPropertiesComponentMapper = worldManager.getEntityPropertiesComponentMapper();
        EntityProperties entityProperties = entityPropertiesComponentMapper.get(levelObject);
        entityProperties.getNode().removeChildNative(cameraNode);
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        sceneManager.getRootSceneNode().addChildNative(cameraNode);
        System.out.println("Detached camera from object: " + cameraAttachedObject);
        cameraAttachedObject = null;
    }

    @Override
    public boolean dispatch(ParallelTask task) {
        Event eventToRemove = null;
        boolean ret = false;
        for (DelayedEvent event : task.getEventList()) {
            if (checkDelayStart(event, null)) {
                break;
            }
            if (event.getState() == Event.EventState.STARTED) {
                if (event.acceptConditionally(this)) {
                    break;
                }
//                if (event.getDelayEnd() != null) {

//                }
            }
            if (checkDelayEnd(event, null)) {
                if (event.getState() == Event.EventState.FINISHED) {
                    eventToRemove = event;
                }
                break;
            }
        }
        if (eventToRemove != null) {
            task.getEventList().remove(eventToRemove);
        }
        if (task.getEventList().isEmpty()) {
            task.setState(Event.EventState.FINISHED);
        }
        // Parallel tasks always return false to allow other events to happen at the same time.
        return ret;
    }

    public ENG_SceneNode getCameraNode() {
        return cameraNode;
    }

    public void setCameraNode(ENG_SceneNode cameraNode) {
        this.cameraNode = cameraNode;
    }
}
