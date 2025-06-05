/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/16/21, 8:09 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.components;

import com.artemis.Component;

import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;

/**
 * Created by sebas on 20.11.2015.
 */
public class MultiplayerEntityUDP extends Component {

    private long entityId;
    private /*transient*/ String entityName;
    private final ENG_Vector3D velocity = new ENG_Vector3D();
    private final ENG_Vector3D translate = new ENG_Vector3D();
    private final ENG_Quaternion rotate = new ENG_Quaternion(true);

//    private final ENG_Vector4D linearVelocity = new ENG_Vector4D();
//    private final ENG_Vector4D angularVelocity = new ENG_Vector4D();

    // Only for debugging to see the original received data before interpolation.
    private transient final ENG_Vector4D origVelocity = new ENG_Vector4D();
    private transient final ENG_Vector4D origTranslate = new ENG_Vector4D();
    private transient final ENG_Quaternion origRotate = new ENG_Quaternion(true);
    private transient final ENG_Vector4D origAngularVelocity = new ENG_Vector4D();

    /**
     * For serialization.
     */
    public MultiplayerEntityUDP() {

    }

    public MultiplayerEntityUDP(MultiplayerEntityUDP oth) {
        setEntityId(oth.getEntityId());
        setEntityName(oth.getEntityName());
        set(oth);
    }

    public MultiplayerEntityUDP(long entityId) {
        this.entityId = entityId;
    }

    public MultiplayerEntityUDP(long entityId, String name) {
        this(entityId);
        setEntityName(name);
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setTranslate(ENG_Vector4D v) {
        translate.set(v);
    }

    public void getTranslate(ENG_Vector4D ret) {
        ret.set(translate);
    }

    public void getTranslate(ENG_Vector3D ret) {
        ret.set(translate);
    }

    public void setRotate(ENG_Quaternion q) {
        rotate.set(q);
    }

    public void getRotate(ENG_Quaternion ret) {
        ret.set(rotate);
    }

    public void setVelocity(ENG_Vector4D vec) {
        velocity.set(vec);
    }

    public void getVelocityAsVec(ENG_Vector4D ret) {
        ret.set(velocity);
    }

    public void getVelocityAsVec(ENG_Vector3D ret) {
        ret.set(velocity);
    }

//    public void setLinearVelocity(ENG_Vector4D v) {
//        linearVelocity.set(v);
//    }
//
//    public void setAngularVelocity(ENG_Vector4D v) {
//        angularVelocity.set(v);
//    }
//
//    public void getLinearVelocityVec(ENG_Vector4D ret) {
//        ret.set(linearVelocity);
//    }
//
//    public void getAngularVelocityVec(ENG_Vector4D ret) {
//        ret.set(angularVelocity);
//    }

    public ENG_Quaternion getRotate() {
        return rotate;
    }

    public ENG_Vector3D getTranslate() {
        return translate;
    }

    public ENG_Vector3D getVelocity() {
        return velocity;
    }

//    public ENG_Vector4D getLinearVelocity() {
//        return linearVelocity;
//    }
//
//    public ENG_Vector4D getAngularVelocity() {
//        return angularVelocity;
//    }

    //    @Override
    public void set(MultiplayerEntityUDP entity) {
//        super.set(entity);
//        MultiplayerEntityUDP udpEntity = (MultiplayerEntityUDP) entity;
        entity.getTranslate(translate);
        entity.getRotate(rotate);
        entity.getVelocityAsVec(velocity);
    }

    public void setOrigTranslate(ENG_Vector4D v) {
        origTranslate.set(v);
    }

    public void setOrigRotate(ENG_Quaternion q) {
        origRotate.set(q);
    }

    public void setOrigVelocity(ENG_Vector4D v) {
        origVelocity.set(v);
    }

    public void setOrigAngularVelocity(ENG_Vector4D v) {
        origAngularVelocity.set(v);
    }

    public ENG_Quaternion getOrigRotate() {
        return origRotate;
    }

    public ENG_Vector4D getOrigTranslate() {
        return origTranslate;
    }

    public ENG_Vector4D getOrigVelocity() {
        return origVelocity;
    }

    public ENG_Vector4D getOrigAngularVelocity() {
        return origAngularVelocity;
    }

    @Override
    public String toString() {
        return "entityId: " + entityId + " translate: " + translate + " rotate: " + rotate + " velocity: " + velocity;
    }
}
