/*
 * Created by Sebastian Bugiu on 08/11/2025, 14:55
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 08/11/2025, 14:53
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.audio;

import headwayent.blackholedarksun.HudManager;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.audio.ENG_Playable;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

public class LevelPlayable implements ENG_Playable {
    protected final ENG_Vector4D position = new ENG_Vector4D(true);
    protected String name = "DefaultAmbient";
    private String soundName = HudManager.BEEP_SND;

    public void setName(String name) {
        this.name = name;
    }

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    public void setPosition(ENG_Vector4D position) {
        this.position.set(position);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ENG_Vector4D getPosition() {
        return new ENG_Vector4D(position);
    }

    @Override
    public void getPosition(ENG_Vector4D position) {
        position.set(this.position);
    }

    @Override
    public ENG_Quaternion getOrientation() {
        return new ENG_Quaternion();
    }

    @Override
    public void getOrientation(ENG_Quaternion orientation) {
        orientation.setIdentity();
    }

    @Override
    public ENG_SceneNode getSceneNode() {
        return null;
    }

    @Override
    public ENG_Vector4D getEntityVelocity() {
        return new ENG_Vector4D();
    }

    @Override
    public ENG_Vector4D getFrontVec() {
        return ENG_Math.VEC4_NEGATIVE_Z_UNIT;
    }

    @Override
    public float getDopplerFactor() {
        return 1.0f;
    }

    @Override
    public float getMaxSoundSpeed() {
        return 1.0f;
    }
}
