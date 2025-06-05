package com.javafx.experiments.jfx3dviewer;

import com.javafx.experiments.utils3d.Utility;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector3D;
import javafx.scene.Group;
import javafx.scene.transform.Affine;

public class Transform extends Group {

    public enum Axis {
        X, Y, Z
    }

    public static final ENG_Vector3D AXIS_X = new ENG_Vector3D(1.0f, 0.0f, 0.0f);
    public static final ENG_Vector3D AXIS_Y = new ENG_Vector3D(0.0f, 1.0f, 0.0f);
    public static final ENG_Vector3D AXIS_Z = new ENG_Vector3D(0.0f, 0.0f, 1.0f);

    private Affine affineMat = new Affine();
    private ENG_Vector3D position = new ENG_Vector3D();
    private ENG_Quaternion orientation = new ENG_Quaternion(true);
    private ENG_Vector3D scale = new ENG_Vector3D(1.0f, 1.0f, 1.0f);

    public Transform() {
        getTransforms().add(affineMat);
    }

    private void update() {
        ENG_Matrix4 mat = new ENG_Matrix4();
//        mat.setScale(scale);
        mat.setTrans(position);
        orientation.toRotationMatrix(mat);
        Utility.setAffine(mat, affineMat);
        ENG_Vector3D axis = new ENG_Vector3D();
        double angle = orientation.toAngleAxisDeg(axis);
//        System.out.println("update() position: " + position + " orientation axis: " + axis + " angle: " + angle);
    }

    public void addPositionWithOrientation(double x, double y, double z) {
        addPositionWithOrientation(new ENG_Vector3D(x, y, z));
    }

    public void addPositionWithOrientation(ENG_Vector3D position) {
        addPosition(orientation.mul(position));
    }

    public void addPosition(double x, double y, double z) {
        addPosition(new ENG_Vector3D(x, y, z));
    }

    public void addPosition(ENG_Vector3D position) {
        this.position.addInPlace(position);
        update();
    }

    public ENG_Vector3D getPosition() {
        return new ENG_Vector3D(position);
    }

    public void setPosition(double x, double y, double z) {
        position.set(x, y, z);
        update();
    }

    public void setPosition(ENG_Vector3D position) {
        this.position.set(position);
        update();
    }

    public void addOrientation(Axis axis, double degree) {
        ENG_Vector3D axisVec = null;
        switch (axis) {
            case X -> axisVec = AXIS_X;
            case Y -> axisVec = AXIS_Y;
            case Z -> axisVec = AXIS_Z;
            default -> throw new IllegalStateException("Unexpected value: " + axis);
        }
        addOrientation(ENG_Quaternion.fromAngleAxisDegRet(degree, axisVec));
    }

    public void addOrientation(ENG_Quaternion rotation) {
        orientation.normalize();
        orientation.mulInPlace(rotation);
        update();
    }

    public ENG_Quaternion getOrientation() {
        return new ENG_Quaternion(orientation);
    }

    public void setOrientation(ENG_Quaternion orientation) {
        this.orientation.set(orientation);
        update();
    }

    public void addScale(double x, double y, double z) {
        addScale(new ENG_Vector3D(x, y, z));
    }

    public void addScale(ENG_Vector3D scale) {
        this.scale.addInPlace(scale);
        update();
    }

    public ENG_Vector3D getScale() {
        return new ENG_Vector3D(scale);
    }

    public void setScale(double x, double y, double z) {
        scale.set(x, y, z);
        update();
    }

    public void setScale(ENG_Vector3D scale) {
        this.scale.set(scale);
        update();
    }

    public Affine getAffineMat() {
        return new Affine(affineMat);
    }
}
