/*
 * Created by Sebastian Bugiu on 06/04/2025, 13:51
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 06/04/2025, 13:50
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.components;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import headwayent.blackholedarksun.levelresource.LevelObject;
import headwayent.blackholedarksun.physics.EntityMotionState;
import headwayent.blackholedarksun.physics.EntityRigidBody;
import headwayent.blackholedarksun.physics.StaticEntityMotionState;
import headwayent.blackholedarksun.physics.StaticEntityRigidBody;
import headwayent.hotshotengine.renderer.ENG_Item;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

public class StaticEntityProperties extends Component {

    private long entityId;
    private transient Entity gameEntity;
    private transient ENG_Item item;
    private transient ENG_SceneNode node;
    private transient StaticEntityMotionState motionState;
    private transient StaticEntityRigidBody rigidBody;
    private transient btRigidBody.btRigidBodyConstructionInfo contructionInfo;
    private transient btCollisionShape collisionShape;
    private String name;
    private LevelObject.LevelObjectType objectType;
    private transient short collisionGroup;
    private transient short collisionMask;

    /**
     * Default empty constructor for Kryo serialization.
     */
    public StaticEntityProperties() {
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public Entity getGameEntity() {
        return gameEntity;
    }

    public void setGameEntity(Entity gameEntity) {
        this.gameEntity = gameEntity;
    }

    public ENG_Item getItem() {
        return item;
    }

    public void setItem(ENG_Item item) {
        this.item = item;
    }

    public ENG_SceneNode getNode() {
        return node;
    }

    public void setNode(ENG_SceneNode node) {
        this.node = node;
    }

    public StaticEntityMotionState getMotionState() {
        return motionState;
    }

    public void setMotionState(StaticEntityMotionState motionState) {
        this.motionState = motionState;
    }

    public StaticEntityRigidBody getRigidBody() {
        return rigidBody;
    }

    public void setRigidBody(StaticEntityRigidBody rigidBody) {
        this.rigidBody = rigidBody;
    }

    public btRigidBody.btRigidBodyConstructionInfo getContructionInfo() {
        return contructionInfo;
    }

    public void setContructionInfo(btRigidBody.btRigidBodyConstructionInfo contructionInfo) {
        this.contructionInfo = contructionInfo;
    }

    public btCollisionShape getCollisionShape() {
        return collisionShape;
    }

    public void setCollisionShape(btCollisionShape collisionShape) {
        this.collisionShape = collisionShape;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getCollisionGroup() {
        return collisionGroup;
    }

    public void setCollisionGroup(short collisionGroup) {
        this.collisionGroup = collisionGroup;
    }

    public short getCollisionMask() {
        return collisionMask;
    }

    public void setCollisionMask(short collisionMask) {
        this.collisionMask = collisionMask;
    }

    public LevelObject.LevelObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(LevelObject.LevelObjectType objectType) {
        this.objectType = objectType;
    }
}
