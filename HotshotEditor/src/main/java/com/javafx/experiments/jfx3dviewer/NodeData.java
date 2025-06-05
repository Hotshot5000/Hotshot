package com.javafx.experiments.jfx3dviewer;

import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;
import javafx.scene.Node;

public class NodeData {

    private final ENG_Vector4D nodePos = new ENG_Vector4D(true);
    private final ENG_Quaternion nodeRotation = new ENG_Quaternion(true);
    private final ENG_Matrix4 transform = new ENG_Matrix4();
    private transient Node node;

    public NodeData() {
    }

    public NodeData(Node node) {
        this.node = node;
    }

    public ENG_Vector4D getNodePos() {
        return nodePos;
    }

    public ENG_Quaternion getNodeRotation() {
        return nodeRotation;
    }

    public ENG_Matrix4 getTransform() {
        return transform;
    }

    public Node getNode() {
        return node;
    }
}
