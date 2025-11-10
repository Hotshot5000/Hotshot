/*
 * Created by Sebastian Bugiu on 22/10/2025, 16:00
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 22/10/2025, 16:00
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems.helper.ai.skynet;

import com.artemis.Entity;

import java.util.ArrayList;

import headwayent.blackholedarksun.HudManager;
import headwayent.blackholedarksun.components.AIProperties;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.systems.AISystem;
import headwayent.blackholedarksun.systems.helper.ai.Utilities;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.hotshotengine.ENG_ClosestObjectData;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Long;

public class SquadProperties {

    private static final float AHEAD_POSITION_TO_AIM_FOR = 200.0f;
    private int squadId;
    private long leaderId = -1;
    private ArrayList<ENG_Long> ids = new ArrayList<>();
    private float minDistanceAllowed; // If closest member is closer than minDistance then move away.
    private float maxDistanceAllowed; // If closest member is too far away get closer to create a swarm.
    private String squadName;
    private boolean distanceSet;

    public SquadProperties(int squadId) {
        this.squadId = squadId;
    }

    public void addId(long id) {
        addId(new ENG_Long(id));
    }

    public void addId(ENG_Long id) {
        for (ENG_Long shipId : ids) {
            if (shipId.getValue() == id.getValue()) {
                throw new IllegalArgumentException(id + " already added");
            }
        }

        ids.add(id);
    }

    public void removeId(long id) {
        removeId(new ENG_Long(id));
    }

    public void removeId(ENG_Long id) {
        boolean remove = ids.remove(id);
        if (!remove) {
            throw new IllegalArgumentException(id + " is not in this squad");
        }
        if (id.getValue() == leaderId) {
            selectLeader();
        }
    }

    public void selectLeader() {
        if (leaderId == -1) {
            throw new IllegalStateException("leaderId == -1");
        }
        Entity leaderEntity = WorldManagerBase.getSingleton().getEntityByItemId(leaderId);
        EntityProperties entityPropertiesLeader = WorldManager.getSingleton().getEntityPropertiesComponentMapper().get(leaderEntity);
        AIProperties aiPropertiesLeader = WorldManager.getSingleton().getAiPropertiesComponentMapper().getSafe(leaderEntity);
        if (!aiPropertiesLeader.isSquadLeader()) {
            throw new IllegalStateException(entityPropertiesLeader.getName() + " is not the current squad leader!");
        }
        ArrayList<Long> others = new ArrayList<>();
        for (ENG_Long id : ids) {
            others.add(id.getValue());
        }
        if (others.isEmpty()) {
            // This squad is dead.
            SquadManager.getInstance().removeSquad(squadId);
            if (squadName != null) {
                HudManager.getSingleton().setBelowCrosshairText("Squad " + squadName + " lost");
            }
        } else {
            ENG_ClosestObjectData data = new ENG_ClosestObjectData();
            WorldManager.getSingleton().getClosestObject(entityPropertiesLeader.getPosition(), others.iterator(), data);
            leaderId = data.objectId;
        }
    }

    public int getSquadSize() {
        return ids.size();
    }

    public long getLeaderId() {
        return leaderId;
    }

    public void clear() {
        leaderId = -1;
        ids.clear();
    }

    public void update() {
        // Recalculate distances between squad members.
        WorldManagerBase worldManagerBase = WorldManagerBase.getSingleton();
        WorldManager worldManager = WorldManager.getSingleton();
        SquadManager squadManager = SquadManager.getInstance();
        for (int i = 0, idsSize = ids.size() - 1; i < idsSize; i++) {
            float minDistance = Float.MAX_VALUE;
            SquadMemberProperties squadMemberProperties0 = squadManager.getSquadMemberProperties(ids.get(i));
            SquadMemberProperties squadMemberProperties1 = null;
            Entity entity0 = worldManagerBase.getEntityByItemId(ids.get(i).getValue());
            EntityProperties entityProperties0 = worldManager.getEntityPropertiesComponentMapper().get(entity0);
            AIProperties aiProperties0 = worldManager.getAiPropertiesComponentMapper().getSafe(entity0);
            if (aiProperties0 == null) {
                throw new IllegalStateException(entityProperties0.getName() + " does not have AIProperties");
            }
            EntityProperties otherEntityProperties = null;
            AIProperties otherAIProperties = null;
            int j = i + 1;
            int closestDistancePos = -1;
            for ( int restIds = ids.size(); j < restIds; j++) {
                Entity entity1 = worldManagerBase.getEntityByItemId(ids.get(j).getValue());
                EntityProperties entityProperties1 = worldManager.getEntityPropertiesComponentMapper().get(entity1);
                AIProperties aiProperties1 = worldManager.getAiPropertiesComponentMapper().getSafe(entity1);
                if (aiProperties1 == null) {
                    throw new IllegalStateException(entityProperties1.getName() + " does not have AIProperties");
                }
                float distance = entityProperties0.getPosition().distance(entityProperties1.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                    squadMemberProperties1 = squadManager.getSquadMemberProperties(ids.get(j));
                    otherEntityProperties = entityProperties1;
                    otherAIProperties = aiProperties1;
                    closestDistancePos = j;
                }
            }
            if (closestDistancePos == -1) {
                System.out.println("How did we get here? minDistance: " + minDistance);
                return;
            }
            // Just to shut up the compiler.
            assert squadMemberProperties1 != null;
            squadMemberProperties0.setLastPosition(squadMemberProperties0.getPosition());
            squadMemberProperties1.setLastPosition(squadMemberProperties1.getPosition());

            squadMemberProperties0.setPosition(entityProperties0.getPosition());
            squadMemberProperties1.setPosition(otherEntityProperties.getPosition());

            squadMemberProperties0.setLastTargetPosition(squadMemberProperties0.getTargetPosition());
            squadMemberProperties1.setLastTargetPosition(squadMemberProperties1.getTargetPosition());

            squadMemberProperties0.setTargetPosition(otherEntityProperties.getPosition());
            squadMemberProperties1.setTargetPosition(entityProperties0.getPosition());

            squadMemberProperties0.setLastOrientation(squadMemberProperties0.getOrientation());
            squadMemberProperties1.setLastOrientation(squadMemberProperties1.getOrientation());

            squadMemberProperties0.setOrientation(entityProperties0.getOrientation());
            squadMemberProperties1.setOrientation(otherEntityProperties.getOrientation());

            squadMemberProperties0.setLastTargetOrientation(squadMemberProperties0.getTargetOrientation());
            squadMemberProperties1.setLastTargetOrientation(squadMemberProperties1.getTargetOrientation());

            squadMemberProperties0.setTargetOrientation(otherEntityProperties.getOrientation());
            squadMemberProperties1.setTargetOrientation(entityProperties0.getOrientation());

            squadMemberProperties0.setLastClosestMemberDistance(squadMemberProperties0.getClosestMemberDistance());
            squadMemberProperties1.setLastClosestMemberDistance(squadMemberProperties1.getClosestMemberDistance());

            squadMemberProperties0.setClosestMemberDistance(minDistance);
            squadMemberProperties1.setClosestMemberDistance(minDistance);

            squadMemberProperties0.setLastClosestMemberId(squadMemberProperties0.getClosestMemberId());
            squadMemberProperties1.setLastClosestMemberId(squadMemberProperties1.getClosestMemberId());

            squadMemberProperties0.setClosestMemberId(ids.get(closestDistancePos).getValue());
            squadMemberProperties1.setClosestMemberId(ids.get(i).getValue());

            squadMemberProperties0.setLastClosestMemberSpeed(squadMemberProperties0.getClosestMemberSpeed());
            squadMemberProperties1.setLastClosestMemberSpeed(squadMemberProperties1.getClosestMemberSpeed());

            squadMemberProperties0.setClosestMemberSpeed(otherEntityProperties.getVelocity());
            squadMemberProperties1.setClosestMemberSpeed(entityProperties0.getVelocity());
        }
        // Adjust positions to maintain a selected distance.
        if (leaderId == -1) return;
        Entity leaderEntity = worldManagerBase.getEntityByItemId(leaderId);
        EntityProperties entityPropertiesLeader = worldManager.getEntityPropertiesComponentMapper().get(leaderEntity);
        AIProperties aiPropertiesLeader = worldManager.getAiPropertiesComponentMapper().getSafe(leaderEntity);
        for (int i = 0, idsSize = ids.size(); i < idsSize; i++) {
            ENG_Long id = ids.get(i);
            Entity entity = worldManagerBase.getEntityByItemId(id.getValue());
            EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(entity);
            ShipProperties shipProperties = worldManager.getShipPropertiesComponentMapper().getSafe(entity);
            if (shipProperties == null) {
                throw new IllegalStateException(entityProperties.getName() + " does not have ShipProperties");
            }
            AIProperties aiProperties = worldManager.getAiPropertiesComponentMapper().getSafe(entity);
            if (aiProperties == null) {
                throw new IllegalStateException(entityProperties.getName() + " does not have AIProperties");
            }
            if (!aiProperties.isSquadLeader()) {
                // The squad leader does not need to move away. The rest move away from him.
                SquadMemberProperties squadMemberProperties = squadManager.getSquadMemberProperties(id);
                if (squadMemberProperties.getClosestMemberId() != squadMemberProperties.getLastClosestMemberId()) {
                    // Bail out for now since we don't have 2 positions to infer the next target position.
                    // This will need to be rectified since we can end up in a situation where we keep bouncing between
                    // 2 different closest members each frame and never able to calculate where to go next.
                    continue;
                }
                aiProperties.resetSquadFinalTorque();
                float closestMemberDiff = squadMemberProperties.getClosestMemberDistance() -
                        squadMemberProperties.getLastClosestMemberDistance();
                Entity closestEntityByItemId = worldManagerBase.getEntityByItemId(squadMemberProperties.getClosestMemberId());
                EntityProperties closestEntityProperties = worldManager.getEntityPropertiesComponentMapper().get(closestEntityByItemId);
                ENG_Vector4D closestFrontVec = closestEntityProperties.getNode().getLocalInverseZAxis();
                ENG_Vector4D frontVec = entityProperties.getNode().getLocalInverseZAxis();
                ENG_Vector4D closestMemberPosition = closestEntityProperties.getPosition();
                if (squadMemberProperties.getClosestMemberDistance() < minDistanceAllowed) {
                    // Get farther to maintain same distance.
                    ENG_Vector4D targetPosition = closestMemberPosition.addAsPt(closestFrontVec.mulAsVec(AHEAD_POSITION_TO_AIM_FOR));
                    ENG_Vector4D targetDir = targetPosition.subAsVec(entityProperties.getPosition());
                    targetDir.normalize();
                    targetDir.invertInPlace();
                    ENG_Vector4D finalTargetDir = targetDir.reflect(closestFrontVec);
                    float angleBetween = frontVec.angleBetween(finalTargetDir);
                    if (angleBetween > AISystem.FRONT_VEC_CLOSEST_SQUAD_MEMBER_DISTANCE_ALIGNMENT) {
                        ENG_Vector4D torque = new ENG_Vector4D(true);
                        ENG_Math.rotateToPositionTorque(frontVec, finalTargetDir,
                                Utilities.getUpdateInterval(), torque, shipProperties.getShipData().maxAngularVelocity);
                        aiProperties.getSquadFinalTorque().addInPlace(torque);
                        System.out.println(entityProperties.getName() + " moving away from: " + closestEntityProperties.getName() + " torque: " + torque);
                    }
                } else if (squadMemberProperties.getClosestMemberDistance() > maxDistanceAllowed) {
                    // Get closer to maintain same distance.
                    ENG_Vector4D targetPosition = closestMemberPosition.addAsPt(closestFrontVec.mulAsVec(AHEAD_POSITION_TO_AIM_FOR));
                    ENG_Vector4D targetDir = targetPosition.subAsVec(entityProperties.getPosition());
                    targetDir.normalize();
                    float angleBetween = frontVec.angleBetween(targetDir);
                    if (angleBetween > AISystem.FRONT_VEC_CLOSEST_SQUAD_MEMBER_DISTANCE_ALIGNMENT) {
                        ENG_Vector4D torque = new ENG_Vector4D(true);
                        ENG_Math.rotateToPositionTorque(frontVec, targetDir,
                                Utilities.getUpdateInterval(), torque, shipProperties.getShipData().maxAngularVelocity);
                        aiProperties.getSquadFinalTorque().addInPlace(torque);
                        System.out.println(entityProperties.getName() + " moving closer to: " + closestEntityProperties.getName() + " torque: " + torque);
                    }
                } else {
                    float angleBetween = closestFrontVec.angleBetween(frontVec);
                    if (angleBetween > AISystem.FRONT_VEC_CLOSEST_SQUAD_MEMBER_ALIGNMENT) {
                        ENG_Vector4D torque = new ENG_Vector4D(true);
                        ENG_Math.rotateToPositionTorque(frontVec, closestFrontVec,
                                Utilities.getUpdateInterval(), torque, shipProperties.getShipData().maxAngularVelocity);
                        aiProperties.getSquadFinalTorque().addInPlace(torque);
                        System.out.println(entityProperties.getName() + " aligning with: " + closestEntityProperties.getName() + " torque: " + torque);
                    }
                }
                // Adjust velocity to maintain close distance.
                // Adjust based on closest ship variation.
                float accelerationDiff = squadMemberProperties.getClosestMemberSpeed() -
                        squadMemberProperties.getLastClosestMemberSpeed();
                // If we are at a distance we should accelerate or decelerate drastically.
                float velocityDiff = closestEntityProperties.getVelocity() - entityProperties.getVelocity();
                // TODO adjust here to combine both acceleration and large velocity diffs.
                float finalVelocity = entityProperties.getVelocity() +
                        (velocityDiff > 50.0f ? velocityDiff : 0.0f) +
                        accelerationDiff;
                System.out.println(entityProperties.getName() + " squad final velocity: " + finalVelocity +
                        " accelerationDiff: " + accelerationDiff + " velocityDiff: " + velocityDiff);
                aiProperties.setSquadVelocity(finalVelocity);
            } else {
                AIProperties.AIState leaderState = aiPropertiesLeader.getState();
                if (leaderState == AIProperties.AIState.SHOOT_PLAYER) {
                    // Break formation and attack same enemy.
                } else if (leaderState == AIProperties.AIState.EVADE_MISSILE) {

                }
            }
        }

    }

    public int getSquadId() {
        return squadId;
    }

    public float getMinDistanceAllowed() {
        return minDistanceAllowed;
    }

    public void setMinDistanceAllowed(float minDistanceAllowed) {
        this.minDistanceAllowed = minDistanceAllowed;
    }

    public float getMaxDistanceAllowed() {
        return maxDistanceAllowed;
    }

    public void setMaxDistanceAllowed(float maxDistanceAllowed) {
        this.maxDistanceAllowed = maxDistanceAllowed;
    }

    public boolean isDistanceSet() {
        return distanceSet;
    }

    public void setDistanceSet(boolean distanceSet) {
        this.distanceSet = distanceSet;
    }

    public String getSquadName() {
        return squadName;
    }

    public void setSquadName(String squadName) {
        this.squadName = squadName;
    }

    public void setLeaderId(long leaderId) {
        this.leaderId = leaderId;
    }

    public ArrayList<ENG_Long> getIds() {
        return ids;
    }
}
