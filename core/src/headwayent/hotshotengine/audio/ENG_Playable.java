/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 3:00 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.audio;

import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

public interface ENG_Playable {

    String getName();
    ENG_Vector4D getPosition();
    void getPosition(ENG_Vector4D position);

    ENG_Quaternion getOrientation();
    void getOrientation(ENG_Quaternion orientation);

    ENG_SceneNode getSceneNode();

    ENG_Vector4D getEntityVelocity();

    float getDopplerFactor();

    float getMaxSoundSpeed();
}
