/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/19/21, 4:11 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.google.common.collect.MinMaxPriorityQueue;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ProjectileProperties;
import headwayent.blackholedarksun.multiplayer.MultiplayerFrame;
import headwayent.blackholedarksun.multiplayer.MultiplayerServerFrameUDP;
import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntityUDP;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;

import java.util.*;

/**
 * Created by sebas on 01.02.2016.
 */
public class ClientEntityInterpolationSystem extends EntityProcessingSystem {

    private static final float MAX_DISTANCE_DIFF = 70.0f;
    private static final float MAX_DISTANCE_DIFF_SQUARED = MAX_DISTANCE_DIFF * MAX_DISTANCE_DIFF;
    private ComponentMapper<EntityProperties> entityPropertiesMapper;
    private ComponentMapper<ProjectileProperties> projectilePropertiesMapper;
    private final ENG_Vector4D translateBefore = new ENG_Vector4D();
    private final ENG_Vector4D translateAfter = new ENG_Vector4D();
    private final ENG_Vector4D interpTranslateResult = new ENG_Vector4D(true);
    private final ENG_Quaternion rotateBefore = new ENG_Quaternion();
    private final ENG_Quaternion rotateAfter = new ENG_Quaternion();
    private final ENG_Quaternion interpRotateResult = new ENG_Quaternion();
    private final ENG_Vector4D velocityBefore = new ENG_Vector4D();
    private final ENG_Vector4D velocityAfter = new ENG_Vector4D();
    private final ENG_Vector4D interpVelocityResult = new ENG_Vector4D();
//    private final ENG_Vector4D angularVelocityBefore = new ENG_Vector4D();
//    private final ENG_Vector4D angularVelocityAfter = new ENG_Vector4D();
//    private final ENG_Vector4D interpAngularVelocityResult = new ENG_Vector4D();

    // For the player ship.
    private final ENG_Vector4D playerPos = new ENG_Vector4D();
    private final ENG_Quaternion playerOrientation = new ENG_Quaternion();
    private final ENG_Vector4D playerVelocity = new ENG_Vector4D();

    private static class BeforeAndAfter {
        public MultiplayerEntityUDP before, after;
        public long beforeTimestamp, afterTimestamp;
//        public MultiplayerServerFrameUDP beforeServerFrame, afterServerFrame;

        public BeforeAndAfter() {

        }

        public BeforeAndAfter(MultiplayerEntityUDP before, MultiplayerEntityUDP after) {
            this.before = before;
            this.after = after;
        }
    }

    private enum Type {
        INTERPOLATION, EXTRAPOLATION
    }

    private static class MultiplayerFrameComparator implements Comparator<MultiplayerFrame> {

        @Override
        public int compare(MultiplayerFrame o1, MultiplayerFrame o2) {
            return (int) (o1.getTimestamp() - o2.getTimestamp());
        }
    }

    public static final long CLIENT_DELAY = 50;
    private static final long CL_INTERP = 2 * CLIENT_DELAY;
    private static final long QUEUE_SIZE = 3 * CL_INTERP;
    /** @noinspection UnstableApiUsage */ // The queue is time limited not size limited.
    private final MinMaxPriorityQueue<MultiplayerServerFrameUDP> frameQueue = MinMaxPriorityQueue.orderedBy(new MultiplayerFrameComparator()).create();
    private final ArrayList<MultiplayerServerFrameUDP> frameList = new ArrayList<>();
    // Map between entityId to timestamp to entity at that server frame.
//    private HashMap<Long, HashMap<Long, MultiplayerEntityUDP>> entityIdToTimestampToEntityMap = new HashMap<>();
    private final HashMap<Long, BeforeAndAfter> entityIdToEntities = new HashMap<>();
    // Map to easily find the next server frame time for interpolation.
    // Returning null means we are at the last server frame and must extrapolate from the previous ones.
//    private HashMap<Long, Long> nextTimeMap = new HashMap<>();
    private long timeDiff = -1; // The time diff between the client and server.
    private long clientTime;
    private long delayedClientTime;
    private Type type;
    private Type currentType;
    private long latency = -1;

    public ClientEntityInterpolationSystem() {
        super(Aspect.all(EntityProperties.class));
    }

    /** @noinspection UnstableApiUsage */
    @Override
    protected void begin() {
        super.begin();
//        for (MultiplayerServerFrameUDP serverFrameUDP : frameQueue) {
//            ArrayList<MultiplayerEntityUDP> entities = serverFrameUDP.getEntities();
//            for (MultiplayerEntityUDP entityUDP : entities) {
//                long entityId = entityUDP.getEntityId();
//                HashMap<Long, MultiplayerEntityUDP> timestampToEntityMap = entityIdToTimestampToEntityMap.get(entityId);
//                if (timestampToEntityMap == null) {
//                    timestampToEntityMap = new HashMap<>();
//                    entityIdToTimestampToEntityMap.put(entityId, timestampToEntityMap);
//                }
//                timestampToEntityMap.put(serverFrameUDP.getTimestamp(), entityUDP);
//            }
//        }
//        MultiplayerServerFrameUDP previousServerFrameUDP = frameQueue.peekFirst();
//        boolean first = true;
//        for (MultiplayerServerFrameUDP serverFrameUDP : frameQueue) {
//            if (first) {
//                first = false;
//                continue;
//            }
//            nextTimeMap.put(previousServerFrameUDP.getTimestamp(), serverFrameUDP.getTimestamp());
//            previousServerFrameUDP = serverFrameUDP;
//        }
//        nextTimeMap.put(frameQueue.peekLast().getTimestamp(), null);

        if (frameQueue.size() < 2) {
            return;
        }
        if (timeDiff == -1) {
            return;
        }
        entityIdToEntities.clear();

        delayedClientTime = clientTime - CLIENT_DELAY - timeDiff;
//        System.out.println("delayedClientTime: " + delayedClientTime + " clientTime: " + clientTime + " timeDiff: " + timeDiff);
        frameList.clear();
        MultiplayerServerFrameUDP frame;
        while ((frame = frameQueue.poll()) != null) {
            frameList.add(frame);
        }
        // Should we concatenate the frames if they are at the same timestamp??

        frameQueue.addAll(frameList);


//        System.out.println("delayedClientTime: " + delayedClientTime + " first ts: " + frameQueue.peekFirst().getTimestamp() + " last ts: "
//        + frameQueue.peekLast().getTimestamp() + " frameQueue.size(): " + frameQueue.size());
//        StringBuilder stringBuilder = new StringBuilder();
//        System.out.println("PRINTING FRAME LIST");
//        for (MultiplayerServerFrameUDP serverFrameUDP : frameList) {
//            System.out.println(serverFrameUDP.toString());
//        }

        if (delayedClientTime < frameQueue.peekLast().getTimestamp()) {
            // We will have an interpolation.
            type = Type.INTERPOLATION;
//            System.out.println("Preparing frameList for interpolation");
            for (MultiplayerServerFrameUDP serverFrameUDP : frameList) {
//                printReceivedTimestampTimeDiff(serverFrameUDP);
                ArrayList<MultiplayerEntityUDP> entities = serverFrameUDP.getEntities();
                long timestamp = serverFrameUDP.getTimestamp();
                boolean before = false;
                if (timestamp <= delayedClientTime) {
                    before = true;
                }
//                System.out.println("Setting before: " + before);
                for (MultiplayerEntityUDP entityUDP : entities) {
                    long entityId = entityUDP.getEntityId();
                    BeforeAndAfter beforeAndAfter = entityIdToEntities.get(entityId);
                    if (beforeAndAfter == null) {
                        beforeAndAfter = new BeforeAndAfter();
                        entityIdToEntities.put(entityId, beforeAndAfter);
                    }
                    if (before) {
                        beforeAndAfter.before = entityUDP;
                        beforeAndAfter.beforeTimestamp = timestamp;
//                        beforeAndAfter.beforeServerFrame = serverFrameUDP;

//                        if (entityUDP.getEntityName().startsWith("Sebi")) {
//                            System.out.println("Setting the before for: " + entityUDP.getEntityName() + " with pos: " + entityUDP.getTranslate()
//                                    + " and timestamp: " + timestamp);
//                        }
                    } else {
                        if (beforeAndAfter.after == null) {
                            beforeAndAfter.after = entityUDP;
                            beforeAndAfter.afterTimestamp = timestamp;
//                            beforeAndAfter.afterServerFrame = serverFrameUDP;

//                            if (entityUDP.getEntityName().startsWith("Sebi")) {
//                                System.out.println("Setting the after for: " + entityUDP.getEntityName() + " with pos: " + entityUDP.getTranslate()
//                                        + " and timestamp: " + timestamp);
//                            }
                        }
                    }
                }
            }
        } else {
            // We will have an extrapolation.
            type = Type.EXTRAPOLATION;
//            System.out.println("Preparing frameList for extrapolation");
            for (MultiplayerServerFrameUDP serverFrameUDP : frameList) {
//                printReceivedTimestampTimeDiff(serverFrameUDP);
                ArrayList<MultiplayerEntityUDP> entities = serverFrameUDP.getEntities();
                long timestamp = serverFrameUDP.getTimestamp();
                for (MultiplayerEntityUDP entityUDP : entities) {
                    long entityId = entityUDP.getEntityId();
                    BeforeAndAfter beforeAndAfter = entityIdToEntities.get(entityId);
                    if (beforeAndAfter == null) {
                        beforeAndAfter = new BeforeAndAfter();
                        entityIdToEntities.put(entityId, beforeAndAfter);
                    }
                    if (beforeAndAfter.before == null) {
                        beforeAndAfter.before = entityUDP;
                        beforeAndAfter.beforeTimestamp = timestamp;
//                        beforeAndAfter.beforeServerFrame = serverFrameUDP;
//                        System.out.println("Writing first beforeTimestamp for entityId: " + entityId + " timestamp: " + timestamp);
                    } else {
                        // Since we are not concatenating the frames we must make sure that for extrapolation the before and after do not have
                        // the same timestamp. That means they might be the same frame just that it got broken by the UDPBreaker.
                        if (beforeAndAfter.beforeTimestamp != timestamp) {
//                            System.out.println("beforeTimestamp: " + beforeAndAfter.beforeTimestamp + " timestamp: " + timestamp);

                            // If it happens that an udp is broken into multiple pieces, the first udp that was sent might still arrive safely.
                            // That means that now, for the same entity, there are 2 packets with the same data and same timestamp.
                            // So it can happen that the beforeTimestamp becomes the afterTimestamp while the afterTimestamp is overwritten with
                            // the same previous timestamp. Now both before and after timestamps are equal and will cause problems later on.
                            // That is why we must check that the afterTimestamp is also modified.
                            if (beforeAndAfter.after != null && beforeAndAfter.afterTimestamp != timestamp) {
                                beforeAndAfter.before = beforeAndAfter.after;
                                beforeAndAfter.beforeTimestamp = beforeAndAfter.afterTimestamp;
//                                beforeAndAfter.beforeServerFrame = beforeAndAfter.afterServerFrame;
//                                System.out.println("Overwriting beforeTimestamp with afterTimestamp for entityId: " + entityId + " afterTimestamp: "
//                                + beforeAndAfter.afterTimestamp);
                            }
                            beforeAndAfter.after = entityUDP;
                            beforeAndAfter.afterTimestamp = timestamp;
//                            beforeAndAfter.afterServerFrame = serverFrameUDP;
//                            System.out.println("Writing afterTimestamp for entityId: " + entityId + " timestamp: " + timestamp);
                        }

                    }
                }
            }
        }


//        for (Map.Entry<Long, BeforeAndAfter> entityIdBeforeAndAfterEntry : entityIdToEntities.entrySet()) {
//            BeforeAndAfter value = entityIdBeforeAndAfterEntry.getValue();
//            System.out.println("entityId: " + entityIdBeforeAndAfterEntry.getKey() + " beforeTimestamp: " + value.beforeTimestamp
//            + " afterTimestamp: " + value.afterTimestamp);
//            if (value.beforeTimestamp == value.afterTimestamp) {
//                System.out.println("WTF?");
//            }
//        }

    }

    private void printReceivedTimestampTimeDiff(MultiplayerServerFrameUDP serverFrameUDP) {
//        System.out.println("Using frame after: " + (ENG_Utility.currentTimeMillis() - serverFrameUDP.getReceivedTimestamp()));
    }

    private boolean checkInterpolationTypeChanged() {
        if (currentType != type) {
            currentType = type;
            return true;
        }
        return false;
    }

    /** @noinspection UnstableApiUsage */
    @Override
    protected void process(Entity e) {
        if (type == null) {
            return;
        }
        EntityProperties entityProperties = entityPropertiesMapper.get(e);

        boolean playerShip = false;
        if (entityProperties.getEntityId() == WorldManager.getSingleton().getPlayerShipEntityId()) {
            playerShip = true;
//            return;
        }
//        Queue<MultiplayerServerFrameUDP> udpQueue = entityIdToEntityUDPQueue.get(entityProperties.getEntityId());
//        if (udpQueue == null) {
//            return;
//        }
        if (frameQueue.size() < 2) {
//            System.out.println("FRAME QUEUE SIZE < 2");
            return;
        }
        if (frameQueue.peekLast().getTimestamp() - frameQueue.peekFirst().getTimestamp() < CLIENT_DELAY) {
//            System.out.println("Timestamp last: " + frameQueue.peekLast().getTimestamp() + " first: " + frameQueue.peekFirst().getTimestamp());
//            System.out.println("Queue size: " + frameQueue.size());
//            System.out.println("FRAME QUEUE TIMESTAMP DIFF < CLIENT_DELAY");
            return;
        }

//        long beforeTimestamp = -1;
//        long afterTimestamp = -1;
//        for (MultiplayerServerFrameUDP serverFrameUDP : frameQueue) {
//            long timestamp = serverFrameUDP.getTimestamp();
//            if (timestamp <= delayedClientTime) {
//                beforeTimestamp = timestamp;
//            }
//            if (timestamp > delayedClientTime) {
//                afterTimestamp = timestamp;
//                break;
//            }
//        }
//
//        if (beforeTimestamp == -1) {
//            // WTF???
//            throw new IllegalStateException();
//        }
//        HashMap<Long, MultiplayerEntityUDP> timestampToEntityMap = entityIdToTimestampToEntityMap.get(entityProperties.getEntityId());
        BeforeAndAfter beforeAndAfter = entityIdToEntities.get(entityProperties.getEntityId());
        if (beforeAndAfter == null) {
            // There was no change for this entity.
            return;
        }

        // If we have a projectile update we must make sure that it is no
        // longer updated by the MovementSystem.
        ProjectileProperties projectileProperties = projectilePropertiesMapper.getSafe(e);
        if (projectileProperties != null) {
            projectileProperties.setUpdateHandledByServer();
        }

//        if (checkInterpolationTypeChanged()) {
//            if (type == Type.INTERPOLATION) {
//                System.out.println("INTERPOLATING");
//            } else if (type == Type.EXTRAPOLATION) {
//                System.out.println("EXTRAPOLATING");
//            }
//        }

        boolean interpolated = false;
        if (type == Type.INTERPOLATION) {
//            System.out.println("INTERPOLATING");
//            System.out.println("Interpolating entity: " + entityProperties.getName());
            if (beforeAndAfter.before == null) {
                // Interpolate between current data and the future data.
                beforeAndAfter.before = new MultiplayerEntityUDP();
                beforeAndAfter.before.setTranslate(entityProperties.getNode().getPosition());
                beforeAndAfter.before.setRotate(entityProperties.getNode().getOrientation());
                beforeAndAfter.before.setVelocity(entityProperties.getVelocityOriginal());
                beforeAndAfter.beforeTimestamp = delayedClientTime;

//                if (entityProperties.getNode().getName().startsWith("John")) {
//                    System.out.println("before null");
//                }
            }
            if (beforeAndAfter.after != null) {
                interpolated = interpolate(beforeAndAfter, e);
            } else {
                return;
            }
        } else if (type == Type.EXTRAPOLATION) {
//            System.out.println("EXTRAPOLATING");
            if (beforeAndAfter.after == null) {
                // We only have one value so we cannot extrapolate.
//                System.out.println("after null");
                return;
            }
//            if (entityProperties.getNode().getName().startsWith("John")) {
//                System.out.println("Extrapolating entity: " + entityProperties.getName());
//            }
            interpolated = interpolate(beforeAndAfter, e);
        }

//        System.out.println("interpTranslateResult: " + interpTranslateResult.toString() + " interpRotateResult: " + interpRotateResult + " interpVelocityResult: "
//        + interpVelocityResult.toString());

        if (playerShip) {
            // Check if we are too far apart from the server data.
            if (type != Type.EXTRAPOLATION) {
                // There is no point moving the player around based on extrapolated data. Only interpolated data makes sense.
                entityProperties.getNode().getPosition(playerPos);
                entityProperties.getNode().getOrientation(playerOrientation);
                entityProperties.getVelocityAsVec(playerVelocity);
//                System.out.println("Player diff len: " + (playerPos.length() - interpTranslateResult.length()));
//                System.out.println("Player diff squared len: " + Math.abs(playerPos.squaredLength() - interpTranslateResult.squaredLength()));
                if (playerPos.distance(interpTranslateResult) > MAX_DISTANCE_DIFF) {
//                    if (entityProperties.getNode().getName().startsWith("Sebi")) {
//                        System.out.println("Player distance len: " + playerPos.distance(interpTranslateResult));
//                    }
                    setInterpolatedData(entityProperties);
                } else {

                }
            }
        } else {
            if (interpolated) {
                setInterpolatedData(entityProperties);
            }
        }
//        if (afterTimestamp == -1) {
//            // We must extrapolate.
//        } else {
////            MultiplayerEntityUDP beforeEntityUDP = timestampToEntityMap.get(beforeTimestamp);
//        }

    }

    @Override
    protected void end() {
        super.end();
    }

    private void setInterpolatedData(EntityProperties entityProperties) {
//        entityProperties.setPosition(interpTranslateResult);
//        entityProperties.setOrientation(interpRotateResult);

//        System.out.println("client interpolation entity: " + entityProperties.getNode().getName() + " pos: " + interpTranslateResult +
//        " orientation: " + interpRotateResult + " velocity: " + interpVelocityResult +
//                " current local pos: " + entityProperties.getNode().getPositionForNative() +
//        " current local orientation: " + entityProperties.getNode().getOrientationForNative() +
//        " current local velocity: " + entityProperties.getVelocityOriginal());

//        float distanceBetweenCurrentAndInterp = interpTranslateResult.distance(entityProperties.getNode().getPositionForNative());
//        if (distanceBetweenCurrentAndInterp > 20.0f) {
//            System.out.println("Distance between current pos and interpTranslateResult > 20.0f: " + entityProperties.getNode().getName());
//        }

        entityProperties.setTransform(interpTranslateResult, interpRotateResult, true);
        entityProperties.setVelocity(interpVelocityResult);

        // Physics stuff.
//        entityProperties.setAngularVelocity(interpAngularVelocityResult);
    }

    private int extrapolationFinishedCount;
    private boolean extrapolationFinished;

    private boolean interpolate(BeforeAndAfter beforeAndAfter, Entity e) {



        // Interpolate between the 2 values.
        translateBefore.set(beforeAndAfter.before.getTranslate());
        translateAfter.set(beforeAndAfter.after.getTranslate());
        rotateBefore.set(beforeAndAfter.before.getRotate());
        rotateAfter.set(beforeAndAfter.after.getRotate());
        velocityBefore.set(beforeAndAfter.before.getVelocity());
        velocityAfter.set(beforeAndAfter.after.getVelocity());
//        angularVelocityBefore.set(beforeAndAfter.before.getAngularVelocity());
//        angularVelocityAfter.set(beforeAndAfter.after.getAngularVelocity());

        // Temp only for debugging.
        beforeAndAfter.before.setOrigTranslate(translateBefore);
        beforeAndAfter.after.setOrigTranslate(translateAfter);
        beforeAndAfter.before.setOrigRotate(rotateBefore);
        beforeAndAfter.after.setOrigRotate(rotateAfter);
        beforeAndAfter.before.setOrigVelocity(velocityBefore);
        beforeAndAfter.after.setOrigVelocity(velocityAfter);
//        beforeAndAfter.before.setOrigAngularVelocity(angularVelocityBefore);
//        beforeAndAfter.after.setOrigAngularVelocity(angularVelocityAfter);

        long timeDelta = beforeAndAfter.afterTimestamp - beforeAndAfter.beforeTimestamp;
        long localDelayedClientTime = delayedClientTime - beforeAndAfter.beforeTimestamp;
        float interpPoint = ((float) localDelayedClientTime) / ((float) timeDelta);

        EntityProperties entityProperties = entityPropertiesMapper.get(e);

//        if (entityProperties.getNode().getName().startsWith("Sebi")) {
//            System.out.println(entityProperties.getNode().getName() + " interpPoint: " + interpPoint + " beforeOrigTranslate: " + beforeAndAfter.before.getOrigTranslate() +
//            " afterOrigTranslate: " + beforeAndAfter.after.getOrigTranslate());
//        }



        if (interpPoint > 1.25f) {
//            System.out.println("interpPoint > 1.25f: " + interpPoint);
            return false;
        }
        if (projectilePropertiesMapper.getSafe(e) != null) {
//            System.out.println("timeDelta: " + timeDelta + " localDelayedClientTime: " + localDelayedClientTime + " interpPoint: " + interpPoint);
        }

        if (projectilePropertiesMapper.getSafe(e) != null) {
//            System.out.println("Before timestamp: " + beforeAndAfter.beforeTimestamp + " after timestamp: " + beforeAndAfter.afterTimestamp);
        }


        ENG_Vector4D translateBeforeCopy = new ENG_Vector4D(translateBefore);
        ENG_Vector4D translateAfterCopy = new ENG_Vector4D(translateAfter);

        translateBefore.mul(1.0f - interpPoint);
        translateAfter.mul(interpPoint);
        translateBefore.add(translateAfter, interpTranslateResult);
        if (projectilePropertiesMapper.getSafe(e) != null) {
//            System.out.println("translateBefore: " + translateBeforeCopy + " translateAfter: " + translateAfterCopy + " interpTranslateResult: " + interpTranslateResult);
        }


        ENG_Quaternion.slerp(interpPoint, rotateBefore, rotateAfter, true, interpRotateResult);

//        interpRotateResult.normalize();
//        System.out.println("rotateBefore: " + rotateBefore + " rotateAfter: " + rotateAfter + " interpRotateResult: " + interpRotateResult);
        ENG_Vector4D angleAxisInterpRotateResult = new ENG_Vector4D();
        float interpRotateResultAngle = interpRotateResult.toAngleAxisDeg(angleAxisInterpRotateResult);
//        System.out.println("Angle axis: " + angleAxisInterpRotateResult);

        ENG_Vector4D rotateBeforeAngleAxis = new ENG_Vector4D();
        float rotateBeforeAngle = rotateBefore.toAngleAxisDeg(rotateBeforeAngleAxis);
        ENG_Vector4D rotateAfterAngleAxis = new ENG_Vector4D();
        float rotateAfterAngle = rotateAfter.toAngleAxisDeg(rotateAfterAngleAxis);

//        if (entityProperties.getNode().getName().startsWith("John") && Math.abs(interpRotateResultAngle) > ENG_Math.FLOAT_EPSILON) {
//            if (interpPoint > 1.0f) {
////                printRotation(type, interpPoint, angleAxisInterpRotateResult, interpRotateResultAngle, rotateBeforeAngleAxis, rotateBeforeAngle, rotateAfterAngleAxis, rotateAfterAngle);
//                printTranslation(type, interpPoint, interpTranslateResult, translateBeforeCopy, translateAfterCopy);
//                extrapolationFinished = true;
//                extrapolationFinishedCount = 0;
//            }
//            if (extrapolationFinished) {
////                printRotation(type, interpPoint, angleAxisInterpRotateResult, interpRotateResultAngle, rotateBeforeAngleAxis, rotateBeforeAngle, rotateAfterAngleAxis, rotateAfterAngle);
//                printTranslation(type, interpPoint, interpTranslateResult, translateBeforeCopy, translateAfterCopy);
//                if (++extrapolationFinishedCount >= 500000) {
//                    extrapolationFinished = false;
//                    extrapolationFinishedCount = 0;
//                }
//            }
//        }

        ENG_Vector4D velocityBeforeCopy = new ENG_Vector4D(velocityBefore);
        ENG_Vector4D velocityAfterCopy = new ENG_Vector4D(velocityAfter);

        velocityBefore.mul(1.0f - interpPoint);
        velocityAfter.mul(interpPoint);
        velocityBefore.add(velocityAfter, interpVelocityResult);
//        System.out.println("velocityBefore: " + velocityBeforeCopy + " velocityAfter: " + velocityAfterCopy + " interpVelocityResult: " + interpVelocityResult);

//        ENG_Vector4D angularVelocityBeforeCopy = new ENG_Vector4D(angularVelocityBefore);
//        ENG_Vector4D angularVelocityAfterCopy = new ENG_Vector4D(angularVelocityAfter);
//
//        angularVelocityBefore.mul(1.0f - interpPoint);
//        angularVelocityAfter.mul(interpPoint);
//        angularVelocityBefore.add(angularVelocityAfter, interpAngularVelocityResult);

//        System.out.println("angularVelocityBefore: " + angularVelocityBeforeCopy + " angularVelocityAfter: " + angularVelocityAfterCopy + " interpAngularVelocityResult: " + interpAngularVelocityResult);



        return true;
    }

    private void printRotation(Type type, float interpPoint, ENG_Vector4D angleAxisInterpRotateResult, float interpRotateResultAngle, ENG_Vector4D rotateBeforeAngleAxis, float rotateBeforeAngle, ENG_Vector4D rotateAfterAngleAxis, float rotateAfterAngle) {
        System.out.println("John " + (type == Type.INTERPOLATION ? "interpolating" : "extrapolating") + " interpPoint: " + interpPoint + " angle axis: " + angleAxisInterpRotateResult + " angle: " + interpRotateResultAngle + " rotateBefore axis: " + rotateBeforeAngleAxis + " angle: " + rotateBeforeAngle + " rotateAfter axis: " + rotateAfterAngleAxis + " angle: " + rotateAfterAngle);
    }

    private void printTranslation(Type type, float interpPoint, ENG_Vector4D translationInterpResult, ENG_Vector4D translationBefore, ENG_Vector4D translationAfter) {
        System.out.println("John " + (type == Type.INTERPOLATION ? "interpolating" : "extrapolating") + " interpPoint: " + interpPoint + " translationInterpResult: " + translationInterpResult + " translationBefore: " + translationBefore + " translationAfter: " + translationAfter);
    }

    /** @noinspection UnstableApiUsage */
    public void addToQueue(MultiplayerServerFrameUDP multiplayerServerFrameUDP) {
//        Deque<MultiplayerServerFrameUDP> udpQueue = entityIdToEntityUDPQueue.get(entityId);
//        if (udpQueue == null) {
//            udpQueue = new ArrayDeque<>();//EvictingQueue.create(QUEUE_SIZE);
//            entityIdToEntityUDPQueue.put(entityId, udpQueue);
//        }

//        if (frameQueue.isEmpty()) {
//            frameQueue.add(multiplayerServerFrameUDP);
//            return;
//        }
//        System.out.println("Frame time: " + (ENG_Utility.currentTimeMillis() - multiplayerServerFrameUDP.getTimestamp()));
        for (MultiplayerEntityUDP multiplayerEntityUDP : multiplayerServerFrameUDP.getEntities()) {
//            if (multiplayerEntityUDP.getEntityName().startsWith("Sebi")) {
//                System.out.println("received entity: " + multiplayerEntityUDP.getEntityName() + " position: " + multiplayerEntityUDP.getTranslate());
//            }
        }

        printReceivedTimestampTimeDiff(multiplayerServerFrameUDP);
        updateTimeDiff(multiplayerServerFrameUDP);
//        System.out.println("Adding frame with num: " + multiplayerServerFrameUDP.getFrameNum());
        frameQueue.add(multiplayerServerFrameUDP);
        if (timeDiff == -1) {
            return;
        }
        while (frameQueue.peekLast().getTimestamp() - frameQueue.peekFirst().getTimestamp() > QUEUE_SIZE && frameQueue.size() > 2) {
            MultiplayerServerFrameUDP first = frameQueue.pollFirst();
            // Make sure now we don't have a long line of frames that are below the client interp threshold and if we do readd it.
            if (frameQueue.peekLast().getTimestamp() - frameQueue.peekFirst().getTimestamp() < CL_INTERP) {
                frameQueue.add(first);
                break;
            }
        }
//        MultiplayerServerFrameUDP first = frameQueue.peek();
//        boolean firstTime = true;
//        while (frameQueue.peekLast().getTimestamp() - first.getTimestamp() > QUEUE_SIZE && frameQueue.size() > 2) {
//            if (firstTime) {
//                MultiplayerServerFrameUDP serverFrameUDP = frameQueue.pollFirst();
//                first = frameQueue.peekFirst();
////                System.out.println("Removing the last server frame with frame num: " + (serverFrameUDP != null ? serverFrameUDP.getFrameNum() : ""));
//                firstTime = false;
//            } else {
//                first = frameQueue.pollFirst();
////            System.out.println("Removing the last server frame with frame num: " + (first != null ? first.getFrameNum() : ""));
//            }
//        }

    }

    private void updateTimeDiff(MultiplayerServerFrameUDP multiplayerServerFrameUDP) {
        if (latency != -1) {
            timeDiff = clientTime - multiplayerServerFrameUDP.getTimestamp() + latency;
//            System.out.println("Time diff: " + (ENG_Utility.currentTimeMillis() - clientTime));
//            System.out.println("Updating timeDiff: " + timeDiff + " latency: " + latency);
//            System.out.println("current time - clientTime: " + (ENG_Utility.currentTimeMillis() - clientTime));
        }
    }

    public void setClientTime(long clientTime) {
        this.clientTime = clientTime;
//        System.out.println("Time diff: " + (ENG_Utility.currentTimeMillis() - clientTime));
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }
}
