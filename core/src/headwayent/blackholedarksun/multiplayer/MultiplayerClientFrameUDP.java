/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/16/21, 8:32 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer;

import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;

/**
 * Created by sebas on 11.11.2015.
 */
public class MultiplayerClientFrameUDP extends MultiplayerClientFrame {

//    private final ENG_Vector4D velocity = new ENG_Vector4D();
    private final ENG_Vector3D translate = new ENG_Vector3D();
    private final ENG_Quaternion rotate = new ENG_Quaternion(true);
//    private final ENG_Vector4D angularVelocity = new ENG_Vector4D();

    /**
     * For serialization.
     */
    public MultiplayerClientFrameUDP() {
        super(Type.MULTIPLAYER_CLIENT_FRAME_UDP);
    }

    public MultiplayerClientFrameUDP(long userId) {
        super(Type.MULTIPLAYER_CLIENT_FRAME_UDP);
        setUserId(userId);
    }

    public void setTranslate(ENG_Vector4D v) {
        translate.set(v);
    }

    public void getTranslate(ENG_Vector4D ret) {
        ret.set(translate);
    }

    public void setRotate(ENG_Quaternion q) {
        rotate.set(q);
    }

    public void getRotate(ENG_Quaternion ret) {
        ret.set(rotate);
    }

//    public void setVelocity(ENG_Vector4D vec) {
//        velocity.set(vec);
//    }
//
//    public void getVelocityAsVec(ENG_Vector4D ret) {
//        ret.set(velocity);
//    }
//
//    public void setAngularVelocity(ENG_Vector4D v) {
//        angularVelocity.set(v);
//    }
//
//    public void getAngularVelocityVec(ENG_Vector4D ret) {
//        ret.set(angularVelocity);
//    }

    public ENG_Quaternion _getRotate() {
        return rotate;
    }

    public ENG_Vector4D _getTranslate() {
        return new ENG_Vector4D(translate, true);
    }

//    public ENG_Vector4D _getVelocity() {
//        return velocity;
//    }
//
//    public ENG_Vector4D _getAngularVelocity() {
//        return angularVelocity;
//    }

    @Override
    public String toString() {
        return super.toString() + " translate: " + translate + " rotate: " + rotate/* + " velocity: " + velocity + " angularVelocity: " + angularVelocity*/;
    }
}
