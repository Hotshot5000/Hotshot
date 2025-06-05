/*
 * Created by Sebastian Bugiu on 06/04/2025, 13:20
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 06/04/2025, 13:20
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

public class StaticEntityMotionState extends btMotionState {

    private final ENG_SceneNode sceneNode;

    public StaticEntityMotionState(ENG_SceneNode sceneNode) {
        this.sceneNode = sceneNode;
    }

    @Override
    public void getWorldTransform(Matrix4 worldTrans) {
//        super.getWorldTransform(worldTrans);
        ENG_Vector4D derivedPosition = sceneNode._getDerivedPosition();
        ENG_Quaternion derivedOrientation = sceneNode._getDerivedOrientation();
        ENG_Vector4D derivedScale = sceneNode._getDerivedScale();
        worldTrans.set(
                new Vector3(derivedPosition.x, derivedPosition.y, derivedPosition.z),
                new Quaternion(derivedOrientation.x, derivedOrientation.y, derivedOrientation.z, derivedOrientation.w),
                new Vector3(derivedScale.x, derivedScale.y, derivedScale.z));
    }

    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
//        super.setWorldTransform(worldTrans);
        System.out.println("StaticEntityMotionState setWorldTransform()");
    }
}
