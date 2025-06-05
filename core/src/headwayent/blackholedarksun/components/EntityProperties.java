/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/22/21, 6:26 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.components;

import com.artemis.Entity;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import headwayent.blackholedarksun.Animation;
import headwayent.blackholedarksun.GameWorld;
import headwayent.blackholedarksun.MainActivity;
import headwayent.blackholedarksun.Utility;
import headwayent.blackholedarksun.animations.AnimationFactory;
import headwayent.blackholedarksun.levelresource.LevelObject;
import headwayent.blackholedarksun.physics.EntityMotionState;
import headwayent.blackholedarksun.physics.EntityRigidBody;
import headwayent.blackholedarksun.physics.PhysicsProperties;
import headwayent.blackholedarksun.physics.PhysicsUtility;
import headwayent.hotshotengine.ENG_AxisAlignedBox;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.audio.ENG_Playable;
import headwayent.hotshotengine.renderer.ENG_Entity;
import headwayent.hotshotengine.renderer.ENG_Item;
import headwayent.hotshotengine.renderer.ENG_Node.TransformSpace;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

public class EntityProperties extends MultiplayerComponent implements ENG_Playable {

    public static final float DEFAULT_DOPPLER_FACTOR = 1.0f;

    @Override
    public ENG_Vector4D getPosition() {
        return getNode().getPosition();
    }

    @Override
    public void getPosition(ENG_Vector4D position) {
        position.set(getNode().getPositionForNative());
    }

    @Override
    public ENG_Quaternion getOrientation() {
        return getNode().getOrientation();
    }

    @Override
    public void getOrientation(ENG_Quaternion orientation) {
        orientation.set(getNode().getOrientationForNative());
    }

    @Override
    public ENG_SceneNode getSceneNode() {
        return getNode();
    }

    @Override
    public ENG_Vector4D getEntityVelocity() {
        return getVelocityAsVec();
    }

    @Override
    public float getMaxSoundSpeed() {
        return maxSpeed;
    }

    public interface OnDestroyedEvent {
        void execute();
    }

    public interface IRemovable {
        void onRemove(Entity entity);
    }

    private static final float DEFAULT_WEIGHT = 20.0f;

    private long entityId;
    private transient Entity gameEntity;
    /** @noinspection deprecation*/
    private transient ENG_Entity entity;
    private transient ENG_Item item;
    private transient ENG_SceneNode node;
    private transient EntityMotionState motionState;
    private transient EntityRigidBody rigidBody;
    private transient PhysicsProperties.RigidBodyType rigidBodyType;
    private transient btRigidBody.btRigidBodyConstructionInfo contructionInfo;
    private transient btCollisionShape collisionShape;
    private transient PhysicsProperties.CollisionShape collisionShapeType;
    private String name;
    private transient LevelObject.LevelObjectType objectType;
    private final ENG_Vector4D velocity = new ENG_Vector4D();
    // The next 2 are only for multiplayer.
    private final ENG_Vector4D translate = new ENG_Vector4D();
    private final ENG_Quaternion rotate = new ENG_Quaternion(true);
    private transient final ENG_Vector4D translatePrevious = new ENG_Vector4D();
    private transient final ENG_Quaternion rotatePrevious = new ENG_Quaternion(true);
    private int health;
    private transient int damage;
    private transient float weight = DEFAULT_WEIGHT;
    private transient float maxSpeed = 1.0f; // Avoid a NaN when dividing for sound speed.
    private boolean destroyed;
    // Making sure that the client has been notified of these events before removing this entity for good.
    private transient boolean destroyedSent;
    private transient float radius;
    private transient boolean invincible;
    private transient Animation destroyedAnimation;

    private boolean destroyedAnimationFinished;
    private boolean destroyedDuringAnimation;
    // Making sure that the client has been notified of these events before removing this entity for good.
    private transient boolean destroyedAnimationFinishedSent;
    private transient boolean destroyedDuringAnimationSent;
    private transient AnimationFactory hitAnimationFactory;
    private transient OnDestroyedEvent onDestroyedEvent;
    private transient boolean destroyEventOnce;
    private transient boolean ignoringCollision;
    private transient String destructionSoundName;
    private transient boolean updateSectionList;
    // -1 for left limit 1 for right 0 no limit touched
    private final transient ENG_Vector4D limitsReached = new ENG_Vector4D();
    private transient boolean scannable;
    private transient boolean showHealth;
    private transient long timedDamageTime;
    private transient boolean timedDamage; // For collisions between ships so you don't die too fast
    private transient String lastEntityTimedDamage;
    private transient long timedDamageCurrentTime;
    private final transient ENG_Vector4D finalPos = new ENG_Vector4D(true);
    private transient boolean justCreated = true;
    private transient IRemovable onRemove;
    private final transient HashMap<String, String> nodeNameToCurrentThreadName = new HashMap<>();
    private final transient ReentrantLock nodeNameToCurrentThreadNameLock = new ReentrantLock();
    private transient boolean unmovable;
    private transient long playerShipHitAnimationDelay;
    private transient short collisionGroup;
    private transient short collisionMask;
    private transient String modelName;
    private final ENG_Vector3D linearVelocityForMP = new ENG_Vector3D();
    private final ENG_Vector3D linearFactorForMP = new ENG_Vector3D();
    private transient long debrisLifeBeginTime;
    private transient long debrisLifeTime;

    private transient float dopplerFactor = DEFAULT_DOPPLER_FACTOR;

    private int activationState;
    // Used for the collision detection algorithm. When a collision resolves with an entity destruction we don't immediately destroy it
    // but wait until the collision has been resolved for both entities involved in the collision.
//    private transient boolean shouldDestroy;
//    private transient MultiplayerClientFrameTCP lastMultiplayerClientFrameTCP;

    // For multiplayer
//    private LevelObject.LevelObjectType type;

    /**
     * Default empty constructor for Kryo serialization.
     */
    public EntityProperties() {

    }

    /**
     * Only for multiplayer
     * @param entityId
     * @param oth
     */
    public EntityProperties(long entityId, EntityProperties oth) {
        this.entityId = entityId;
        this.name = oth.getName();
        set(oth);
    }

    public void set(EntityProperties oth) {
        setTranslate(oth.getTranslate());
        setRotate(oth.getRotate());
        setVelocity(oth.getVelocity());
        setHealth(oth.getHealth());
        setDestroyed(oth.isDestroyed());
        setDestroyedAnimationFinished(oth.isDestroyedAnimationFinished());
        setDestroyedDuringAnimation(oth.isDestroyedDuringAnimation());

        System.out.println(oth.getName() + " health: " + oth.getHealth() + " destroyed: " + oth.isDestroyed());
    }

    /** @noinspection deprecation*/
//    public EntityProperties(Entity parent, ENG_Entity entity, ENG_SceneNode node, long entityId, String name) {
//        this.gameEntity = parent;
//        this.entity = entity;
//        this.node = node;
//        this.name = name;//entity.getName();
//        this.entityId = entityId;
//    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public void setGameEntity(Entity gameEntity) {
        this.gameEntity = gameEntity;
    }

    public void setNode(ENG_SceneNode node) {
        this.node = node;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getEntityId() {
        return entityId;
    }

    public long getItemId() {
        return item.getId();
    }

    //    public EntityProperties(Entity parent, ENG_Entity entity, ENG_SceneNode node, LevelObject.LevelObjectType type) {
//        this(parent, entity, node);
//        this.type = type;
//    }

    @Override
    public String getName() {
        return name;
    }

    public String getUniqueName() {
        return generateUniqueName(getName(), getEntityId());
    }

    public static String generateUniqueName(String name, long id) {
        return name + "_" + id;
    }

    public void updateMultiplayerCoordsForSendServerSide() {
        node.getPosition(translate);
//        translate.set(positionWithoutCollisionResolution);
        node.getOrientation(rotate);

        if (!translate.equals(translatePrevious)) {
            makeDirtyUdp();
            translatePrevious.set(translate);
        }
        if (!rotate.equals(rotatePrevious)) {
            makeDirtyUdp();
            rotatePrevious.set(rotate);
        }

//        if (getNode().getName().startsWith("John")) {
//            System.out.println("John updateMultiplayerCoordsForSendServerSide translate: " + translate);
//        }

//        ENG_Vector4D othEntityAxis = new ENG_Vector4D();
//        float othEntityAngle = getRotate().toAngleAxisDeg(othEntityAxis);
//
//        if (getNode().getName().startsWith("John") && Math.abs(othEntityAngle) > ENG_Math.FLOAT_EPSILON) {
//            System.out.println("John updateMultiplayerCoordsForSendServerSide axis: " + othEntityAxis + " angle: " + othEntityAngle);
//        }
    }

    public void updateMultiplayerCoordsForSendClientSide() {
//        node.getPosition(translate);
        translate.set(node.getPositionForNative());
        rotate.set(node.getOrientationForNative());

//        if (getNode().getName().startsWith("John")) {
//            System.out.println("John updateMultiplayerCoordsForSendServerSide translate: " + translate);
//        }

//        ENG_Vector4D othEntityAxis = new ENG_Vector4D();
//        float othEntityAngle = getRotate().toAngleAxisDeg(othEntityAxis);
//
//        if (getNode().getName().startsWith("John") && Math.abs(othEntityAngle) > ENG_Math.FLOAT_EPSILON) {
//            System.out.println("John updateMultiplayerCoordsForSendClientSide axis: " + othEntityAxis + " angle: " + othEntityAngle);
//        }
    }

    public void updateMultiplayerCoordsForReceive() {
        node.setPosition(translate);
        node.setOrientation(rotate);
//        System.out.println("Rotate: " + rotate.toString());

        // TODO
        // No longer used but we still need to notify the physics engine about the new positions.
//        updateSectionList();
    }

    public void setTransform(ENG_Vector4D position, ENG_Quaternion orientation, boolean updatePhysics) {
        setTransform(position, orientation, updatePhysics, false);
    }

    public void setTransform(ENG_Vector4D position, ENG_Quaternion orientation, boolean updatePhysics, boolean clearForces) {
        node.setPosition(position);
        node.setOrientation(orientation);
        if (updatePhysics) {
            Utility.setPosition(rigidBody, motionState, position, orientation, clearForces);
        }
    }

    /**
     * Only for multiplayer.
     * @param v
     */
    public void setTranslate(ENG_Vector4D v) {
        translate.set(v);
    }

    /**
     * Only for multiplayer.
     * @param ret
     */
    public void getTranslate(ENG_Vector4D ret) {
        ret.set(translate);
    }

    /**
     * Only for multiplayer.
     * @return
     */
    public ENG_Vector4D getTranslate() {
        return translate;
    }

    /**
     * Only for multiplayer.
     * @param q
     */
    public void setRotate(ENG_Quaternion q) {
        rotate.set(q);

        ENG_Vector4D othEntityAxis = new ENG_Vector4D();
        float othEntityAngle = getRotate().toAngleAxisDeg(othEntityAxis);

        if (getName().contains("Piranha") && Math.abs(othEntityAngle) > ENG_Math.FLOAT_EPSILON) {
            if (item != null) {
                System.out.println("Piranha " + item.getName() + " setRotate axis: " + othEntityAxis + " angle: " + othEntityAngle);
            } else {
                System.out.println("Piranha " + getName() + " setRotate axis: " + othEntityAxis + " angle: " + othEntityAngle);
            }
        }
    }

    /**
     * Only for multiplayer.
     * @param ret
     */
    public void getRotate(ENG_Quaternion ret) {
        ret.set(rotate);
    }

    /**
     * Only for multiplayer.
     * @return
     */
    public ENG_Quaternion getRotate() {
        return rotate;
    }

    public void clearNodeNameToCurrentThreadName() {
        nodeNameToCurrentThreadName.clear();
    }

    private void addToNodeNameToCurrentThreadName() {
        nodeNameToCurrentThreadNameLock.lock();
        try {


            String value = ENG_Utility.extractThreadNameWithStacktrace();
            String put = nodeNameToCurrentThreadName.put(getNode().getName(), value);
//            if (put != null) {
//                System.out.println("Updating section list for node: " + getNode().getName() + " from thread: " + value +
//                " with previous thread: " + put);
//                System.exit(0);
//            }
            if (nodeNameToCurrentThreadName.size() > 1) {
                for (Map.Entry<String, String> entry : nodeNameToCurrentThreadName.entrySet()) {
                    System.out.println("Updating section list for node: " + getNode().getName() + " from thread: " + entry.getValue());
                }

            }
        } finally {
            nodeNameToCurrentThreadNameLock.unlock();
        }
    }

//    public ENG_Vector4D getPositionWithoutCollisionResolution() {
//        return positionWithoutCollisionResolution;
//    }
//
//    public void setPositionWithoutCollisionResolution(ENG_Vector4D pos) {
//        positionWithoutCollisionResolution.set(pos);
//    }

    public void setPositionWithoutPhysics(ENG_Vector4D pos) {
        setPositionWithoutPhysics(pos, true);
    }

    public void setOrientationWithoutPhysics(ENG_Quaternion orientation) {
        setOrientationWithoutPhysics(orientation, true);
    }

    public void setPositionWithoutPhysics(ENG_Vector4D pos, boolean clearForces) {
        setPositionWithoutPhysics(pos, node.getOrientation(), clearForces);
    }

    public void setOrientationWithoutPhysics(ENG_Quaternion orientation, boolean clearForces) {
        setPositionWithoutPhysics(node.getPositionForNative(), orientation, clearForces);
    }

    public void setPositionWithoutPhysics(ENG_Vector4D pos, ENG_Quaternion orientation, boolean clearForces) {
        node.setPosition(pos);
        node.setOrientation(orientation);
        Utility.setPosition(getRigidBody(), getMotionState(), pos, orientation, clearForces);
    }

    /** @noinspection deprecation*/
    @Deprecated
    public void setPosition(ENG_Vector4D pos) {
        setPosition(pos, true);
    }

    @Deprecated
    public void setPosition(ENG_Vector4D pos, boolean updateSectionList) {
        node.setPosition(pos);
//        if (updateSectionList) {
//            updateSectionList();
//        }
    }

    @Deprecated
    private void setPosition(ENG_Vector4D pos, boolean updateSectionList, boolean updateLimitsReached) {
        node.setPosition(pos);
//        if (updateSectionList) {
//            updateSectionList();
//        }
    }

    /** @noinspection deprecation*/
    @Deprecated
    public void move(ENG_Vector4D pos) {
        move(pos, true);
    }

    @Deprecated
    public void move(ENG_Vector4D pos, boolean updateSectionList) {
        node.translate(pos);
//        if (updateSectionList) {
//            updateSectionList();
//        }
    }

    /** @noinspection deprecation*/
    @Deprecated
    public void setOrientation(ENG_Quaternion neworientation) {
        setOrientation(neworientation, true);
    }

    @Deprecated
    public void setOrientation(ENG_Quaternion neworientation,
                               boolean updateSectionList) {
        node.setOrientation(neworientation);
//        if (updateSectionList) {
//            updateSectionList();
//        }
    }

    /** @noinspection deprecation*/
    @Deprecated
    public void rotate(ENG_Quaternion neworientation) {
        rotate(neworientation, true, TransformSpace.TS_LOCAL);
    }

    /** @noinspection deprecation*/
    @Deprecated
    public void rotate(ENG_Quaternion neworientation, boolean updateSectionList) {
        rotate(neworientation, updateSectionList, TransformSpace.TS_LOCAL);
    }

    /** @noinspection deprecation*/
    @Deprecated
    public void rotate(ENG_Quaternion neworientation, TransformSpace ts) {
        rotate(neworientation, true, ts);
    }

    @Deprecated
    public void rotate(ENG_Quaternion neworientation, boolean updateSectionList,
                       TransformSpace ts) {
        node.rotate(neworientation, ts);
//        if (updateSectionList) {
//            updateSectionList();
//        }
    }

    /** @noinspection deprecation*/
    @Deprecated
    public void rollRad(float angle) {
        rollRad(angle, true);
    }

    @Deprecated
    public void rollRad(float angle, boolean updateSectionList) {
        node.roll(angle);
//        if (updateSectionList) {
//            updateSectionList();
//        }
    }

    /** @noinspection deprecation*/
    @Deprecated
    public void rollDeg(float angle) {
        rollDeg(angle, true);
    }

    @Deprecated
    public void rollDeg(float angle, boolean updateSectionList) {
        node.roll(angle * ENG_Math.DEGREES_TO_RADIANS);
//        if (updateSectionList) {
//            updateSectionList();
//        }
    }

    /** @noinspection deprecation*/
    @Deprecated
    public void yawRad(float angle) {
        yawRad(angle, true);
    }

    @Deprecated
    public void yawRad(float angle, boolean updateSectionList) {
        node.yaw(angle);
//        if (updateSectionList) {
//            updateSectionList();
//        }
    }

    /** @noinspection deprecation*/
    @Deprecated
    public void yawDeg(float angle) {
        yawDeg(angle, true);
    }

    @Deprecated
    public void yawDeg(float angle, boolean updateSectionList) {
        node.yaw(angle * ENG_Math.DEGREES_TO_RADIANS);
//        if (updateSectionList) {
//            updateSectionList();
//        }
    }

    /** @noinspection deprecation*/
    @Deprecated
    public void pitchRad(float angle) {
        pitchRad(angle, true);
    }

    @Deprecated
    public void pitchRad(float angle, boolean updateSectionList) {
        node.pitch(angle);
//        if (updateSectionList) {
//            updateSectionList();
//        }
    }

    /** @noinspection deprecation*/
    @Deprecated
    public void pitchDeg(float angle) {
        pitchDeg(angle, true);
    }

    @Deprecated
    public void pitchDeg(float angle, boolean updateSectionList) {
        node.pitch(angle * ENG_Math.DEGREES_TO_RADIANS);
//        if (updateSectionList) {
//            updateSectionList();
//        }
    }

    public void setVelocity(float x, float y, float z) {
        velocity.set(x, y, z);
    }

    public void setVelocity(float speed) {
        velocity.set(0.0f, 0.0f, -speed);
    }

    public void setVelocity(ENG_Vector4D vec) {
        velocity.set(vec);
    }

    public void getVelocityAsVec(ENG_Vector4D ret) {
        ret.set(velocity);
    }

    public ENG_Vector4D getVelocityAsVec() {
        return new ENG_Vector4D(velocity);
    }

    public ENG_Vector4D getVelocityOriginal() {
        return velocity;
    }

    public float getVelocity() {
        return -velocity.z;
    }

    /*	public float getRoll() {
            return roll;
        }

        public void setRoll(float roll) {
            this.roll = roll;
        }

        public float getYaw() {
            return yaw;
        }

        public void setYaw(float yaw) {
            this.yaw = yaw;
        }

        public float getPitch() {
            return pitch;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
        }
    */
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void decreaseHealth(int dec) {
        health -= dec;
        makeDirtyTcp();
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    /** @noinspection deprecation*/
    public ENG_Entity getEntity() {
        return entity;
    }

    /** @noinspection deprecation*/
    public void setEntity(ENG_Entity entity) {
        //	node.detachObject(this.entity);
        this.entity = entity;
        //	node.attachObject(entity);
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

    public Entity getGameEntity() {
        return gameEntity;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    /**
     * Setting this to true starts the destruction process for the entity.
     * It will be removed from the scene in the next frame cycle and everything dependant on its
     * destruction will be updated (level events, level state etc.).
     * @param destroyed
     */
    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
        if (destroyed && onDestroyedEvent != null && !destroyEventOnce) {
            destroyEventOnce = true;
            onDestroyedEvent.execute();
        }
        makeDirtyTcp();

//        if (getNode() != null && getNode().getName() != null) {
//            System.out.println(getNode().getName() + " setDestroyed: " + destroyed);
//        }
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public Animation getDestroyedAnimation() {
        return destroyedAnimation;
    }

    public void setDestroyedAnimation(Animation destroyedAnimation) {
        this.destroyedAnimation = destroyedAnimation;
    }

    public boolean isDestroyedAnimationFinished() {
        return destroyedAnimationFinished;
    }

    public void setDestroyedAnimationFinished(boolean destroyedAnimationFinished) {
        this.destroyedAnimationFinished = destroyedAnimationFinished;
        makeDirtyTcp();

//        if (getNode() != null && getNode().getName() != null) {
//            System.out.println(getNode().getName() + " setDestroyedAnimationFinished: " + destroyedAnimationFinished);
//        }
    }

    public boolean isDestroyedDuringAnimation() {
        return destroyedDuringAnimation;
    }

    public void setDestroyedDuringAnimation(boolean destroyedDuringAnimation) {
        this.destroyedDuringAnimation = destroyedDuringAnimation;
        makeDirtyTcp();

//        if (getNode() != null && getNode().getName() != null) {
//            System.out.println(getNode().getName() + " setDestroyedDuringAnimation: " + destroyedDuringAnimation);
//        }
    }

    public AnimationFactory getHitAnimationFactory() {
        return hitAnimationFactory;
    }

    public void setHitAnimationFactory(AnimationFactory hitAnimationFactory) {
        this.hitAnimationFactory = hitAnimationFactory;
    }

    public OnDestroyedEvent getOnDestroyedEvent() {
        return onDestroyedEvent;
    }

    public void setOnDestroyedEvent(OnDestroyedEvent onDestroyedEvent) {
        this.onDestroyedEvent = onDestroyedEvent;
    }

    public boolean isIgnoringCollision() {
        return ignoringCollision;
    }

    public void setIgnoringCollision(boolean ignoringCollision) {
        this.ignoringCollision = ignoringCollision;
    }

    public String getDestructionSoundName() {
        return destructionSoundName;
    }

    public void setDestructionSoundName(String destructionSoundName) {
        this.destructionSoundName = destructionSoundName;
    }

    /**
     * @return the updateSectionList
     */
    public boolean isUpdateSectionList() {
        return updateSectionList;
    }

    /**
     * @param updateSectionList the updateSectionList to set
     */
    public void setUpdateSectionList(boolean updateSectionList) {
        this.updateSectionList = updateSectionList;
    }

    private void checkEntityInLevelBounds() {
        checkEntityInLevelBounds(true);
    }

    /** @noinspection deprecation*/
    private void checkEntityInLevelBounds(boolean updateLimits) {
//        node._getDerivedPosition(finalPos);
        node.getPosition(finalPos);
        // Changed from node to item since item is coming from the native side.
        ENG_AxisAlignedBox worldAABB = item.getWorldAABB();
        // The aabb is always one frame behind. The first time we don't have any aabb set.
        if (worldAABB.isNull()) {
            return;
        }
//        System.out.println(getName() + " worldAABB: " + worldAABB);
        if (MainActivity.isDebugmode() && !worldAABB.isFinite()) {
            throw new IllegalArgumentException("node name: " + node.getName() + " has invalid worldAABB: " + worldAABB.getExtent());
        }
        ENG_Vector4D max = worldAABB.getMax();
        ENG_Vector4D min = worldAABB.getMin();
        boolean minHit = false;
        if (updateLimits) {
            limitsReached.set(0.0f, 0.0f, 0.0f);
        }
        //       worldAABB.getHalfSize(halfSize);
        if (max.x > GameWorld.MAX_DISTANCE) {
            //        System.out.println("initial finalPos: " + finalPos);
            finalPos.x -= max.x - (GameWorld.MAX_DISTANCE) + ENG_Math.FLOAT_EPSILON;
            finalPos.x -= 10.0f;
                /*        System.out.println("max.x > GameWorld.MAX_DISTANCE correction: " +
                                (max.x - (GameWorld.MAX_DISTANCE) + ENG_Math.FLOAT_EPSILON) +
                                " max.x: " + max.x +
                                " finalPos: " + finalPos +
                                " node naame: " + node.getName() + 
                                " Entity name: " + getEntity().getName());
                                * 
                                */
            //	setPosition(finalPos);
            minHit = true;
            if (updateLimits) {
                limitsReached.x = 1.0f;
            }
        }
        if (max.y > GameWorld.MAX_DISTANCE) {
            //        System.out.println("initial finalPos: " + finalPos);
            finalPos.y -= max.y - (GameWorld.MAX_DISTANCE) + ENG_Math.FLOAT_EPSILON;
            finalPos.y -= 10.0f;
                /*        System.out.println("max.y > GameWorld.MAX_DISTANCE correction: " +
                                (max.y - (GameWorld.MAX_DISTANCE) + ENG_Math.FLOAT_EPSILON) +
                                " max.y: " + max.y +
                                " finalPos: " + finalPos +
                                " node naame: " + node.getName() + 
                                " Entity name: " + getEntity().getName());
                                * 
                                */
            //	setPosition(finalPos);
            minHit = true;
            if (updateLimits) {
                limitsReached.y = 1.0f;
            }
        }
        if (max.z > GameWorld.MAX_DISTANCE) {
            //        System.out.println("initial finalPos: " + finalPos);
            finalPos.z -= max.z - (GameWorld.MAX_DISTANCE) + ENG_Math.FLOAT_EPSILON;
            finalPos.z -= 10.0f;
                /*        System.out.println("max.z > GameWorld.MAX_DISTANCE correction: " +
                                (max.z - (GameWorld.MAX_DISTANCE) + ENG_Math.FLOAT_EPSILON) +
                                " max.z: " + max.z +
                                " finalPos: " + finalPos +
                                " node naame: " + node.getName() + 
                                " Entity name: " + getEntity().getName());
                                * 
                                */
            //	setPosition(finalPos);
            minHit = true;
            if (updateLimits) {
                limitsReached.z = 1.0f;
            }
        }
        if (min.x < -GameWorld.MAX_DISTANCE) {
            //        System.out.println("initial finalPos: " + finalPos);
            finalPos.x -= min.x + (GameWorld.MAX_DISTANCE) - ENG_Math.FLOAT_EPSILON;
            finalPos.x += 10.0f;
                /*        System.out.println("min.x < -GameWorld.MAX_DISTANCE correction: " +
                                (min.x + (GameWorld.MAX_DISTANCE) + ENG_Math.FLOAT_EPSILON) +
                                " max.x: " + max.x +
                                " finalPos: " + finalPos +
                                " node naame: " + node.getName() + 
                                " Entity name: " + getEntity().getName());
                                * 
                                */
            //	setPosition(finalPos);
            minHit = true;
            if (updateLimits) {
                limitsReached.x = -1.0f;
            }
        }
        if (min.y < -GameWorld.MAX_DISTANCE) {
            //        System.out.println("initial finalPos: " + finalPos);
            finalPos.y -= min.y + (GameWorld.MAX_DISTANCE) - ENG_Math.FLOAT_EPSILON;
            finalPos.y += 10.0f;
                /*        System.out.println("min.y < -GameWorld.MAX_DISTANCE correction: " +
                                (min.y + (GameWorld.MAX_DISTANCE) + ENG_Math.FLOAT_EPSILON) +
                                " max.y: " + max.y +
                                " finalPos: " + finalPos +
                                " node naame: " + node.getName() + 
                                " Entity name: " + getEntity().getName());
                                * 
                                */
            //	setPosition(finalPos);
            minHit = true;
            if (updateLimits) {
                limitsReached.y = -1.0f;
            }
        }
        if (min.z < -GameWorld.MAX_DISTANCE) {
            //        System.out.println("initial finalPos: " + finalPos);
            finalPos.z -= min.z + (GameWorld.MAX_DISTANCE) - ENG_Math.FLOAT_EPSILON;
            finalPos.z += 10.0f;
                /*        System.out.println("min.z < -GameWorld.MAX_DISTANCE correction: " +
                                (min.z - (GameWorld.MAX_DISTANCE) + ENG_Math.FLOAT_EPSILON) +
                                " max.z: " + max.z +
                                " finalPos: " + finalPos +
                                " node naame: " + node.getName() + 
                                " Entity name: " + getEntity().getName());
                                * 
                                */
            //	setPosition(finalPos);
            minHit = true;
            if (updateLimits) {
                limitsReached.z = -1.0f;
            }
        }
        if (minHit) {
            setPosition(finalPos, true, false);
        }
    /*	node.getPosition(finalPos);
        if (finalPos.x > GameWorld.MAX_DISTANCE) {
			finalPos.x = GameWorld.MAX_DISTANCE;
			node.setPosition(finalPos);
		} else if (finalPos.x < -GameWorld.MAX_DISTANCE) {
			finalPos.x = -GameWorld.MAX_DISTANCE;
			node.setPosition(finalPos);
		}
		if (finalPos.y > GameWorld.MAX_DISTANCE) {
			finalPos.y = GameWorld.MAX_DISTANCE;
			node.setPosition(finalPos);
		} else if (finalPos.y < -GameWorld.MAX_DISTANCE) {
			finalPos.y = -GameWorld.MAX_DISTANCE;
			node.setPosition(finalPos);
		}
		if (finalPos.z > GameWorld.MAX_DISTANCE) {
			finalPos.z = GameWorld.MAX_DISTANCE;
			node.setPosition(finalPos);
		} else if (finalPos.z < -GameWorld.MAX_DISTANCE) {
			finalPos.z = -GameWorld.MAX_DISTANCE;
			node.setPosition(finalPos);
		}*/
    }

    public void getLimitsReached(ENG_Vector4D ret) {
        ret.set(limitsReached);
    }

    public ENG_Vector4D getLimitsReached() {
        return new ENG_Vector4D(limitsReached);
    }

    public boolean isLimitsReached() {
        return !limitsReached.equals(ENG_Math.VEC4_ZERO);
    }

    /**
     * This should only be used for updating from EntityContactListener.
     * @return
     */
    public ENG_Vector4D getLimitsReachedOriginal() {
        return limitsReached;
    }

    public void resetLimitsReached() {
        limitsReached.set(ENG_Math.VEC4_ZERO);
    }

    public boolean isScannable() {
        return scannable;
    }

    public void setScannable(boolean scannable) {
        this.scannable = scannable;
    }

    public boolean isShowHealth() {
        return showHealth;
    }
    
    public void setShowHealth(boolean showHealth) {
        this.showHealth = showHealth;
    }

    public boolean isTimedDamage() {
        return timedDamage;
    }

    public void setTimedDamage(boolean timedDamage) {
        this.timedDamage = timedDamage;
    }

    public long getTimedDamageTime() {
        return timedDamageTime;
    }

    public void setTimedDamageTime(long timedDamageTime) {
        this.timedDamageTime = timedDamageTime;
    }

    public String getLastEntityTimedDamage() {
        return lastEntityTimedDamage;
    }

    public void setLastEntityTimedDamage(String lastEntityTimedDamage) {
        this.lastEntityTimedDamage = lastEntityTimedDamage;
    }

    public long getTimedDamageCurrentTime() {
        return timedDamageCurrentTime;
    }

    public void setTimedDamageCurrentTime(/*long timedDamageCurrentTime*/) {
        this.timedDamageCurrentTime = ENG_Utility.currentTimeMillis();
        //timedDamageCurrentTime;
    }

    public boolean isJustCreated() {
        return justCreated;
    }

    public void setJustCreated(boolean justCreated) {
        this.justCreated = justCreated;
    }

    public IRemovable getOnRemove() {
        return onRemove;
    }

    public void setOnRemove(IRemovable onRemove) {
        this.onRemove = onRemove;
    }

    public boolean isDestroyedAnimationFinishedSent() {
        return destroyedAnimationFinishedSent;
    }

    public void setDestroyedAnimationFinishedSent(boolean destroyedAnimationFinishedSent) {
        this.destroyedAnimationFinishedSent = destroyedAnimationFinishedSent;
    }

    public boolean isDestroyedDuringAnimationSent() {
        return destroyedDuringAnimationSent;
    }

    public void setDestroyedDuringAnimationSent(boolean destroyedDuringAnimationSent) {
        this.destroyedDuringAnimationSent = destroyedDuringAnimationSent;
    }

    public boolean isDestroyedSent() {
        return destroyedSent;
    }

    public void setDestroyedSent(boolean destroyedSent) {
        this.destroyedSent = destroyedSent;
    }

    public boolean isUnmovable() {
        return unmovable;
    }

    public void setUnmovable(boolean unmovable) {
        this.unmovable = unmovable;
    }

    public long getPlayerShipHitAnimationDelay() {
        return playerShipHitAnimationDelay;
    }

    public void setPlayerShipHitAnimationDelay(/*long playerShipHitAnimationDelay*/) {
        this.playerShipHitAnimationDelay = ENG_Utility.currentTimeMillis();
    }

    public EntityMotionState getMotionState() {
        return motionState;
    }

    public void setMotionState(EntityMotionState motionState) {
        this.motionState = motionState;
    }

    public EntityRigidBody getRigidBody() {
        return rigidBody;
    }

    public void setRigidBody(EntityRigidBody rigidBody) {
        this.rigidBody = rigidBody;
    }

    public PhysicsProperties.RigidBodyType getRigidBodyType() {
        return rigidBodyType;
    }

    public void setRigidBodyType(PhysicsProperties.RigidBodyType rigidBodyType) {
        this.rigidBodyType = rigidBodyType;
    }

    public short getCollisionGroup() {
        return collisionGroup;
    }

    public btRigidBody.btRigidBodyConstructionInfo getContructionInfo() {
        return contructionInfo;
    }

    public void setContructionInfo(btRigidBody.btRigidBodyConstructionInfo contructionInfo) {
        this.contructionInfo = contructionInfo;
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

    public btCollisionShape getCollisionShape() {
        return collisionShape;
    }

    public void setCollisionShape(btCollisionShape collisionShape) {
        this.collisionShape = collisionShape;
    }

    public PhysicsProperties.CollisionShape getCollisionShapeType() {
        return collisionShapeType;
    }

    public void setCollisionShapeType(PhysicsProperties.CollisionShape collisionShapeType) {
        this.collisionShapeType = collisionShapeType;
    }

    //    private transient float prevLinearVelocity;

    public void setLinearVelocity(ENG_Vector4D vec) {
        rigidBody.setLinearVelocity(PhysicsUtility.convertVector(vec));

//        if (getNode().getName().contains("Sebi") && prevLinearVelocity != vec.length()) {
//            System.out.println("Sebi LinearVelocity: " + vec.length());
//            prevLinearVelocity = vec.length();
//        }
    }

    public void setLinearFactor(ENG_Vector4D vec) {
        rigidBody.setLinearFactor(PhysicsUtility.convertVector(vec));
    }

    public void setAngularVelocity(ENG_Vector4D vec) {
        rigidBody.setAngularVelocity(PhysicsUtility.convertVector(vec));
    }

    public void setAngularFactor(ENG_Vector4D vec) {
        rigidBody.setAngularFactor(PhysicsUtility.convertVector(vec));
    }

    public void setLinearVelocityForMP(ENG_Vector4D vec) {
        linearVelocityForMP.set(vec);
    }

    public void setLinearFactorForMP(ENG_Vector4D vec) {
        linearFactorForMP.set(vec);
    }

    public void setDamping(float linearDamping, float angularDamping) {
        rigidBody.setDamping(linearDamping, angularDamping);
    }

    public void setActivationState(int activationState) {
        this.activationState = activationState;
        rigidBody.setActivationState(activationState);
    }

    public int getActivationState() {
        return activationState;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public float getDopplerFactor() {
        return dopplerFactor;
    }

    public void setDopplerFactor(float dopplerFactor) {
        this.dopplerFactor = dopplerFactor;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public long getDebrisLifeBeginTime() {
        return debrisLifeBeginTime;
    }

    public void setDebrisLifeBeginTime() {
        this.debrisLifeBeginTime = ENG_Utility.currentTimeMillis();
    }

    public long getDebrisLifeTime() {
        return debrisLifeTime;
    }

    public void setDebrisLifeTime(long debrisLifeTime) {
        this.debrisLifeTime = debrisLifeTime;
    }

    public LevelObject.LevelObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(LevelObject.LevelObjectType objectType) {
        this.objectType = objectType;
    }

    //    public boolean isShouldDestroy() {
//        return shouldDestroy;
//    }
//
//    public void setShouldDestroy(boolean shouldDestroy) {
//        this.shouldDestroy = shouldDestroy;
//    }

    //    public MultiplayerClientFrameTCP getLastMultiplayerClientFrameTCP() {
//        return lastMultiplayerClientFrameTCP;
//    }
//
//    public void setLastMultiplayerClientFrameTCP(MultiplayerClientFrameTCP lastMultiplayerClientFrameTCP) {
//        this.lastMultiplayerClientFrameTCP = lastMultiplayerClientFrameTCP;
//    }

    //    public LevelObject.LevelObjectType getType() {
//        return type;
//    }
}
