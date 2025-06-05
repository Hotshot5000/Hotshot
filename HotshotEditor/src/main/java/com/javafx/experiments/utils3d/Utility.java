package com.javafx.experiments.utils3d;

import com.esotericsoftware.kryo.Kryo;
import com.javafx.experiments.jfx3dviewer.Jfx3dViewerApp;
import com.javafx.experiments.jfx3dviewer.NodeData;
import com.javafx.experiments.osspecific.OSSpecific;
import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.blackholedarksun.levelresource.*;
import headwayent.hotshotengine.*;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import javafx.scene.PerspectiveCamera;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.transform.Affine;

import java.util.Optional;

public class Utility {

    private static final Kryo kryo = new Kryo();

    /*
     * returns 3D direction from the Camera position to the mouse
     * in the Scene space
     */

    public static ENG_Vector3D unProjectDirection(PerspectiveCamera camera,
                                                  ENG_Quaternion cameraOrientation,
                                                  double sceneX, double sceneY,
                                                  double sWidth, double sHeight) {
        double tanOfHalfFOV = Math.tan(Math.toRadians(camera.getFieldOfView()) * 0.5f);
        ENG_Vector3D vMouse = new ENG_Vector3D(tanOfHalfFOV * (2 * sceneX / sWidth - 1),
                tanOfHalfFOV * (2 * sceneY / sWidth - sHeight / sWidth), 0);
//        ENG_Vector4D vMouse4D = new ENG_Vector4D(vMouse.x, vMouse.y, vMouse.z, 1.0f);
//        System.out.println("vMouse before orientation: " + vMouse);
        vMouse = cameraOrientation.mul(vMouse);
//        System.out.println("vMouse after orientation: " + vMouse);
//        vMouse4D = cameraOrientation.mul(vMouse4D);
//        System.out.println("vMouse4D after orientation: " + vMouse4D);
        return vMouse;
    }

    public static void setAffine(ENG_Matrix4 trans, Affine transform) {
        transform.setMxx(trans.get(0, 0));
        transform.setMxy(trans.get(0, 1));
        transform.setMxz(trans.get(0, 2));
        transform.setTx(trans.get(0, 3));
        transform.setMyx(trans.get(1, 0));
        transform.setMyy(trans.get(1, 1));
        transform.setMyz(trans.get(1, 2));
        transform.setTy(trans.get(1, 3));
        transform.setMzx(trans.get(2, 0));
        transform.setMzy(trans.get(2, 1));
        transform.setMzz(trans.get(2, 2));
        transform.setTz(trans.get(2, 3));
    }

    public static float randomPointOnAxis(double min, double max) {
        return ENG_Utility.rangeRandom(null, (float) min, (float) max);
    }

    public static void updateAffineTransform(NodeData nodeData, Affine transform) {
        transform.setToIdentity();
        if (Jfx3dViewerApp.SCALE_UP_100) {
            transform.prependScale(100, 100, 100);
        }
        ENG_Vector4D axis = new ENG_Vector4D();
        double angle = nodeData.getNodeRotation().toAngleAxisDeg(axis);
        // Changing orientation for meshes so that they conform with JavaFX 3D which uses X - right Y - down Z - front.
        transform.prependRotation(180, 0, 0, 0, 1, 0, 0);
//        transform.prependRotation(180, 0, 0, 0, 0, 1, 0);
//        transform.prependRotation(180, 0, 0, 0, 0, 0, 1);
        // Hocus pocus to change the coordinate system for our game. X - right Y - up Z - back. JavaFX 3D uses X - right Y - down Z - front.
        transform.prependRotation(angle, 0, 0, 0, axis.x, -axis.y, axis.z);
        transform.prependTranslation(nodeData.getNodePos().x, -nodeData.getNodePos().y, -nodeData.getNodePos().z);

        // Below is garbage. Don't use!
    /*    ENG_Matrix4 transformMat = nodeData.getTransform();
        nodeData.getNodeRotation().toRotationMatrix(transformMat);
        transformMat.setTrans(nodeData.getNodePos());
        // This is just because somehow the exported .obj models are * 0.01 of the
        // original size.
        if (Jfx3dViewerApp.SCALE_UP_100) {
            transformMat.setScale(new ENG_Vector3D(100, 100, 100));
        }

        Utility.setAffine(transformMat, transform);*/
    }

    private static long id = 1;

    public static long nextId() {
        return id++;
    }

    public static String generateUniqueName(String name, long id) {
        return name + "_" + id;
    }

    public static String generateUniqueName(String name) {
        return name + "_" + nextId();
    }

    public static void showErrorDialog(String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Error");
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    public static boolean showConfirmationDialog(String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Confirm");
        alert.setContentText(contentText);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static boolean isTextFieldValid(TextField textField) {
        return !textField.getText().isBlank();
    }

    public static void writeVector(ENG_Vector4D v, TextField x, TextField y, TextField z) {
        x.setText(String.valueOf(v.x));
        y.setText(String.valueOf(v.y));
        z.setText(String.valueOf(v.z));
    }

    public static void writeVector(ENG_Vector4D v, TextField x, TextField y, TextField z, TextField w) {
        writeVector(v, x, y, z);
        w.setText(String.valueOf(v.w));
    }

    public static void writeVector(ENG_Vector3D v, TextField x, TextField y, TextField z) {
        x.setText(String.valueOf(v.x));
        y.setText(String.valueOf(v.y));
        z.setText(String.valueOf(v.z));
    }

    public static void writeVector(ENG_Vector2D v, TextField x, TextField y, TextField z) {
        x.setText(String.valueOf(v.x));
        y.setText(String.valueOf(v.y));
    }

    public static void writeQuaternion(ENG_Quaternion q, TextField x, TextField y, TextField z, TextField w) {
        ENG_Vector4D axis = new ENG_Vector4D();
        double angleDeg = q.toAngleAxisDeg(axis);
        writeVector(axis, x, y, z);
        w.setText(String.valueOf(angleDeg));
    }

    public static void writeColorValue(ENG_ColorValue c, TextField r, TextField g, TextField b, TextField a) {
        r.setText(String.valueOf(c.r));
        g.setText(String.valueOf(c.g));
        b.setText(String.valueOf(c.b));
        a.setText(String.valueOf(c.a));
    }

    public static ENG_Vector4D readVector4DAsPt(TextField x, TextField y, TextField z) {
        if (isTextFieldValid(x) && isTextFieldValid(y) && isTextFieldValid(z)) {
            return new ENG_Vector4D(Float.parseFloat(x.getText()),
                    Float.parseFloat(y.getText()),
                    Float.parseFloat(z.getText()), 1.0);
        }
        throw new IllegalArgumentException();
    }

    public static ENG_Vector4D readVector4DAsVec(TextField x, TextField y, TextField z) {
        if (isTextFieldValid(x) && isTextFieldValid(y) && isTextFieldValid(z)) {
            return new ENG_Vector4D(Float.parseFloat(x.getText()),
                    Float.parseFloat(y.getText()),
                    Float.parseFloat(z.getText()), 0.0);
        }
        throw new IllegalArgumentException();
    }

    public static ENG_Vector3D readVector3D(TextField x, TextField y, TextField z) {
        if (isTextFieldValid(x) && isTextFieldValid(y) && isTextFieldValid(z)) {
            return new ENG_Vector3D(Float.parseFloat(x.getText()),
                    Float.parseFloat(y.getText()),
                    Float.parseFloat(z.getText()));
        }
        throw new IllegalArgumentException();
    }

    public static ENG_Vector2D readVector2D(TextField x, TextField y) {
        if (isTextFieldValid(x) && isTextFieldValid(y)) {
            return new ENG_Vector2D(Float.parseFloat(x.getText()),
                    Float.parseFloat(y.getText()));
        }
        throw new IllegalArgumentException();
    }

    public static ENG_Quaternion readQuaternion(TextField x, TextField y, TextField z, TextField angle) {
        if (isTextFieldValid(x) && isTextFieldValid(y) && isTextFieldValid(z) && isTextFieldValid(angle)) {
            ENG_Quaternion quaternion = new ENG_Quaternion();
            quaternion.fromAngleAxisDeg(Float.parseFloat(angle.getText()), readVector3D(x, y, z));
            return quaternion;
        }
        throw new IllegalArgumentException();
    }

    public static float readFloat(TextField f) {
        if (isTextFieldValid(f)) {
            return Float.parseFloat(f.getText());
        }
        throw new IllegalArgumentException();
    }

    public static int readInt(TextField f) {
        if (isTextFieldValid(f)) {
            return Integer.parseInt(f.getText());
        }
        throw new IllegalArgumentException();
    }

    public static long readLong(TextField f) {
        if (isTextFieldValid(f)) {
            return Long.parseLong(f.getText());
        }
        throw new IllegalArgumentException();
    }

    public static boolean readBoolean(TextField f) {
        if (isTextFieldValid(f)) {
            return Boolean.parseBoolean(f.getText()) || Integer.parseInt(f.getText()) == 1;
        }
        throw new IllegalArgumentException();
    }

    public static short readShort(TextField f) {
        if (isTextFieldValid(f)) {
            return Short.parseShort(f.getText());
        }
        throw new IllegalArgumentException();
    }

    public static byte readByte(TextField f) {
        if (isTextFieldValid(f)) {
            return Byte.parseByte(f.getText());
        }
        throw new IllegalArgumentException();
    }

    public static ENG_ColorValue readColorValue(TextField r, TextField g, TextField b, TextField a) {
        if (isTextFieldValid(r) && isTextFieldValid(g) && isTextFieldValid(b) && isTextFieldValid(a)) {
            return new ENG_ColorValue(Float.parseFloat(r.getText()),
                    Float.parseFloat(g.getText()),
                    Float.parseFloat(b.getText()),
                    Float.parseFloat(a.getText()));
        }
        throw new IllegalArgumentException();
    }

    public static String getText(TextField textField) {
        return getText(textField, "");
    }

    public static String getText(TextField textField, String error) {
        if (textField.getText().isBlank()) {
            if (error.isBlank()) {
                error = "TextField is blank!";
            }
            showErrorDialog(error);
            throw new IllegalArgumentException();
        }
        return textField.getText();
    }

    public static String getMeshName(String name) {
        return OSSpecific.getSrcResourcePath() + name + ".obj";
    }

    public static void initKryo() {
        kryo.register(Level.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(headwayent.blackholedarksun.levelresource.LevelEnd.class);
        kryo.register(headwayent.blackholedarksun.levelresource.LevelEndCond.class);
        kryo.register(headwayent.blackholedarksun.levelresource.LevelEndCond.EndCondType.class);
        kryo.register(headwayent.blackholedarksun.levelresource.LevelEndCond.EndCond.class);
        kryo.register(headwayent.blackholedarksun.levelresource.ComparatorNode.class);
        kryo.register(headwayent.blackholedarksun.levelresource.ComparatorOperator.class);
        kryo.register(headwayent.blackholedarksun.levelresource.LevelStart.class);
        kryo.register(headwayent.blackholedarksun.levelresource.LevelObject.class);
        kryo.register(headwayent.blackholedarksun.levelresource.LevelEvent.class);
        kryo.register(headwayent.blackholedarksun.levelresource.LevelEvent.DelayType.class);
        kryo.register(LevelEvent.EventState.class);
        kryo.register(headwayent.blackholedarksun.levelresource.LevelObject.LevelObjectBehavior.class);
        kryo.register(headwayent.blackholedarksun.levelresource.LevelObject.LevelObjectType.class);
        kryo.register(LevelPlayerShipSelection.class);
        kryo.register(ShipData.ShipTeam.class);
        kryo.register(LevelSpawnPoint.class);
        kryo.register(headwayent.hotshotengine.renderer.ENG_ColorValue.class);
        kryo.register(headwayent.hotshotengine.ENG_Vector2D.class);
        kryo.register(headwayent.hotshotengine.ENG_Vector3D.class);
        kryo.register(headwayent.hotshotengine.ENG_Vector4D.class);
        kryo.register(headwayent.hotshotengine.ENG_Quaternion.class);
        kryo.register(LevelWaypointSector.class);
        kryo.register(LevelWaypoint.class);
        kryo.register(headwayent.hotshotengine.renderer.ENG_Light.LightTypes.class);
        kryo.register(NodeData.class);
        kryo.register(double[].class);
        kryo.register(float[].class);
        kryo.register(int[].class);
        kryo.register(short[].class);
        kryo.register(byte[].class);
        kryo.register(boolean[].class);
        kryo.register(long[].class);
        kryo.register(double[][].class);
        kryo.register(float[][].class);
        kryo.register(int[][].class);
        kryo.register(short[][].class);
        kryo.register(byte[][].class);
        kryo.register(boolean[][].class);
        kryo.register(long[][].class);
        kryo.register(java.util.EnumMap.class);
        kryo.register(java.util.HashMap.class);
        kryo.register(java.util.TreeMap.class);
        kryo.register(java.util.LinkedList.class);
        kryo.register(headwayent.hotshotengine.ENG_Matrix4.class);
        kryo.register(headwayent.hotshotengine.ENG_Matrix3.class);
        kryo.register(headwayent.hotshotengine.ENG_AxisAlignedBox.class);
        kryo.register(headwayent.hotshotengine.ENG_AxisAlignedBox.Extent.class);
        kryo.register(ENG_AxisAlignedBox.Corner.class);
    }

    public static Kryo getKryo() {
        return kryo;
    }
}
