package com.javafx.experiments.objects;

import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector3D;

public class Asteroid {

    private String meshName = "";
    private ENG_Vector3D position = new ENG_Vector3D();
    private ENG_Quaternion orientation = new ENG_Quaternion(true);
    private int health;

    public Asteroid() {

    }

    public Asteroid(String meshName, ENG_Vector3D position, ENG_Quaternion orientation, int health) {
        this.meshName = meshName;
        this.position.set(position);
        this.orientation.set(orientation);
        this.health = health;
    }

    public String getMeshName() {
        return meshName;
    }

    public ENG_Vector3D getPosition() {
        return position;
    }

    public ENG_Quaternion getOrientation() {
        return orientation;
    }

    public int getHealth() {
        return health;
    }
}
