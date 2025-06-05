/*
 * Copyright (c) 2010, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.javafx.experiments.jfx3dviewer;

import com.javafx.experiments.shape3d.PolygonMeshView;
import com.javafx.experiments.shape3d.SubdivisionMesh;
import com.javafx.experiments.utils3d.Utility;
import headwayent.blackholedarksun.levelresource.Level;
import headwayent.blackholedarksun.levelresource.LevelObject;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.*;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.apache.velocity.VelocityContext;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 3D Content Model for Viewer App. Contains the 3D scene and everything related
 * to it: light, cameras etc.
 */
public class ContentModel {

    public enum Modifier {
        TRANSLATION, ROTATION, SCALING;
    }

    private Modifier currentModifier = Modifier.TRANSLATION;
    private Node currentIntersectedNode;
    private double currentIntersectedNodeDistance;
    private ENG_Vector3D currentIntersectedNodeInitialPos;
    private final HashMap<Node, Group> meshToParentGroupMap = new HashMap<>();
    private final ArrayList<LevelObject> levelObjectList = new ArrayList<>();
    private final ArrayList<LevelObject> shipObjectList = new ArrayList<>();
    private final ArrayList<LevelObject> cargoObjectList = new ArrayList<>();
    private final ArrayList<LevelObject> cargoShipObjectList = new ArrayList<>();
    private final ArrayList<LevelObject> flagObjectList = new ArrayList<>();
    private final ArrayList<LevelObject> asteroidObjectList = new ArrayList<>();
    private final ArrayList<LevelObject> waypointObjectList = new ArrayList<>();
    private final ArrayList<LevelObject> staticObjectList = new ArrayList<>();
    private LevelObject playerShip;
    private final HashMap<Node, LevelObject> nodeToLevelObjectMap = new HashMap<>();
    private final HashMap<Node, NodeData> nodeMap = new HashMap<>();

    private AmbientLight ambientLight = new AmbientLight(Color.DARKGREY);
    private SimpleBooleanProperty ambientLightEnabled = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            if (get()) {
                root3D.getChildren().add(ambientLight);
            } else {
                root3D.getChildren().remove(ambientLight);
            }
        }
    };
    private final AutoScalingGroup autoScalingGroup = new AutoScalingGroup(2);
    private SubdivisionMesh.BoundaryMode boundaryMode = SubdivisionMesh.BoundaryMode.CREASE_EDGES;
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    public static final double cameraDistance = 300;
    private final Rotate cameraLookXRotate = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
    private final Rotate cameraLookZRotate = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);
    //    private final Translate                      cameraPosition      = new Translate(0, 0,0);
    private final Transform cameraTransform = new Transform();
    //    private final Xform                          cameraXform         = new Xform();
//    private final Xform                          cameraXform2        = new Xform();
//    private final Xform                          cameraXform3        = new Xform();
    private final Rotate cameraXRotate = new Rotate(-20, 0, 0, 0, Rotate.X_AXIS);
    private final Rotate cameraYRotate = new Rotate(-20, 0, 0, 0, Rotate.Y_AXIS);
    private ObjectProperty<Node> content = new SimpleObjectProperty<>();
    @SuppressWarnings({"unused"})
    private double dragStartX;
    private double dragStartY;
    private double dragStartRotateX;
    private double dragStartRotateY;
    private EventHandler<KeyEvent> keyEventHandler;
    private PointLight light1 = new PointLight(Color.WHITE);
    private SimpleBooleanProperty light1Enabled = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            if (get()) {
                root3D.getChildren().add(light1);
            } else {
                root3D.getChildren().remove(light1);
            }
        }
    };
    private PointLight light2 = new PointLight(Color.ANTIQUEWHITE);
    private SimpleBooleanProperty light2Enabled = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            if (get()) {
                root3D.getChildren().add(light2);
            } else {
                root3D.getChildren().remove(light2);
            }
        }
    };
    private PointLight light3 = new PointLight(Color.ALICEBLUE);

    private SimpleBooleanProperty light3Enabled = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            if (get()) {
                root3D.getChildren().add(light3);
            } else {
                root3D.getChildren().remove(light3);
            }
        }
    };

    private SubdivisionMesh.MapBorderMode mapBorderMode = SubdivisionMesh.MapBorderMode.NOT_SMOOTH;

    private double mouseDeltaX;

    private double mouseDeltaY;
    private final EventHandler<MouseEvent> mouseEventHandler;
    private double mouseOldX;
    private double mouseOldY;
    private double mousePosX;
    private double mousePosY;
    private SimpleBooleanProperty msaa = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            rebuildSubScene();
        }
    };
    private final Group root3D = new Group();
    private final EventHandler<ScrollEvent> scrollEventHandler = event -> {
        if (event.getTouchCount() > 0) {                                                                                                 // touch pad scroll
//                                                                             cameraXform2.t.setX(cameraXform2.t.getX()
//                                                                                                 - (0.01
//                                                                                                    * event.getDeltaX()));
//                                                                             cameraXform2.t.setY(cameraXform2.t.getY()
//                                                                                                 + (0.01
//                                                                                                    * event.getDeltaY()));
            cameraTransform.addPosition(-0.01f * event.getDeltaX(), 0.01 * event.getDeltaY(), 0);
        } else {
//                                                                             double z = cameraTransform.getPosition().z ;
//                                                                             z = Math.max(z, -1000);
//                                                                             z = Math.min(z, 0);
//                                                                             cameraPosition.setZ(z);
            cameraTransform.addPosition(0, 0, -(event.getDeltaY() * 0.2));
        }
    };
    private final SimpleBooleanProperty showAxis = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            if (get()) {
                if (xAxis == null) {
                    createAxes();
                }
                autoScalingGroup.getChildren().addAll(xAxis, yAxis, zAxis);
                autoScalingGroup.getChildren().addAll(xSphere, ySphere, zSphere);
                //root3D.getChildren().addAll(xAxis, yAxis, zAxis);
                //root3D.getChildren().addAll(xSphere, ySphere, zSphere);
            } else if (xAxis != null) {
                autoScalingGroup.getChildren().removeAll(xAxis, yAxis, zAxis);
                autoScalingGroup.getChildren().removeAll(xSphere, ySphere, zSphere);
                //root3D.getChildren().removeAll(xAxis, yAxis, zAxis);
                //root3D.getChildren().removeAll(xSphere, ySphere, zSphere);
            }
        }
    };
    private int subdivisionLevel = 0;
    private final SimpleObjectProperty<SubScene> subScene = new SimpleObjectProperty<>();

    private final SimpleObjectProperty<Timeline> timeline = new SimpleObjectProperty<>();
    private boolean wireframe = false;
    private Box xAxis, yAxis, zAxis;
    private Sphere xSphere, ySphere, zSphere;
    private SimpleBooleanProperty yUp = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            if (get()) {
                yUpRotate.setAngle(180);
                //cameraPosition.setZ(cameraDistance);
                // camera.setTranslateZ(cameraDistance);
            } else {
                yUpRotate.setAngle(0);
                //cameraPosition.setZ(-cameraDistance);
                // camera.setTranslateZ(-cameraDistance);
            }
        }
    };
    private final Rotate yUpRotate = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);

    private final EventHandler<ZoomEvent> zoomEventHandler = event -> {
        if (!Double.isNaN(event.getZoomFactor())
                && event.getZoomFactor() > 0.8
                && event.getZoomFactor() < 1.2) {
//                                                                             double z = cameraTransform.getPosition().z
//                                                                                        / event.getZoomFactor();
//                                                                             z = Math.max(z,
//                                                                                          -1000);
//                                                                             z = Math.min(z,
//                                                                                          0);
//                                                                             cameraPosition.setZ(z);
            cameraTransform.addPosition(0, 0, cameraTransform.getPosition().z
                    / event.getZoomFactor());
        }
    };

    private Level level;
    private String selectedMeshName;
    private String currentShipType;
    private String currentSelectedShip;
    private String currentSelectedEvent;
    private ArrayList<String> events = new ArrayList<>();
    private String comparatorOperatorWin;
    private String comparatorOperatorLose;
    private String winEndCondType;
    private String loseEndCondType;
    private String currentShipBehavior;
    private String currentSelectedMeshWinCond;
    private String currentSelectedMeshLoseCond;
    private String currentExitEventSelectedMesh;
    private String currentWaypointSector;
    private String currentWaypoint;
    private String currentWaypointNextId;

    public void removeFromObjectList(LevelObject levelObject) {
        switch (levelObject.type) {
            case PLAYER_SHIP -> {
                playerShip = null;
            }
            case FIGHTER_SHIP, CARGO_SHIP, ASTEROID, FLAG_RED, FLAG_BLUE, CARGO, WAYPOINT, STATIC -> {
                getObjectList(levelObject).remove(levelObject);
            }
            case PLAYER_SHIP_SELECTION -> {
            }
            default -> throw new IllegalStateException("Unexpected value: " + levelObject.type);
        }
    }

    public void addToObjectList(LevelObject levelObject) {
        switch (levelObject.type) {
            case PLAYER_SHIP -> {
                playerShip = levelObject;
            }
            case FIGHTER_SHIP, CARGO_SHIP, ASTEROID, FLAG_RED, FLAG_BLUE, CARGO, WAYPOINT, STATIC -> {
                getObjectList(levelObject).add(levelObject);
            }
            case PLAYER_SHIP_SELECTION -> {
            }
            default -> throw new IllegalStateException("Unexpected value: " + levelObject.type);
        }
    }

    public ArrayList<LevelObject> getObjectList(LevelObject levelObject) {
        switch (levelObject.type) {
            case PLAYER_SHIP -> {
                ArrayList<LevelObject> levelObjects = new ArrayList<>();
                levelObjects.add(levelObject);
                return levelObjects;
            }
            case FIGHTER_SHIP -> {
                return shipObjectList;
            }
            case CARGO_SHIP -> {
                return cargoShipObjectList;
            }
            case ASTEROID -> {
                return asteroidObjectList;
            }
            case FLAG_RED, FLAG_BLUE -> {
                return flagObjectList;
            }
            case CARGO -> {
                return cargoObjectList;
            }
            case PLAYER_SHIP_SELECTION -> {
                throw new IllegalStateException("Unexpected value: " + levelObject.type);
            }
            case WAYPOINT -> {
                return waypointObjectList;
            }
            case STATIC -> {
                return staticObjectList;
            }
            default -> throw new IllegalStateException("Unexpected value: " + levelObject.type);
        }
    }

    public void removeAllObjectsFromObjectList() {
        playerShip = null;
        shipObjectList.clear();
        cargoShipObjectList.clear();
        asteroidObjectList.clear();
        flagObjectList.clear();
        cargoObjectList.clear();
        asteroidObjectList.clear();
        waypointObjectList.clear();
        staticObjectList.clear();
    }

    private Node gridNode;

    private LevelObject gridLevelObject;

    {
        getContentProperty().addListener((ov, oldContent, newContent) -> {
//            autoScalingGroup.getChildren()
//                            .remove(oldContent);
            autoScalingGroup.getChildren().add(newContent);
            // Since all of this is a piece of shit of course that what
            // gets added to the autoScalingGroup is actually a group containing the
            // mesh but when you select the mesh node in the pane what you get is
            // the node not its parent group. So we need to map between these
            // two in order to know which group to remove when deleting a node.
            // Beyond retarded.
            Group group = (Group) newContent;
            if (group.getUserData() == null) {
                throw new IllegalStateException();
            }
            LevelObject levelObject = (LevelObject) group.getUserData();
            Node meshNode = group.getChildren().get(0);
            meshToParentGroupMap.put(meshNode, group);
            levelObjectList.add(levelObject);
            addToObjectList(levelObject);
            nodeToLevelObjectMap.put(meshNode, levelObject);
            NodeData nodeData = new NodeData(meshNode);
            nodeData.getNodePos().set(levelObject.position);
            nodeData.getNodeRotation().set(levelObject.orientation);
            if (!levelObject.name.equals("grid")) {
                snapToGrid(nodeData);
                levelObject.position.set(nodeData.getNodePos());
            }
            levelObject.nodeData = nodeData;

            if (meshNode.getTransforms().isEmpty()) {
                meshNode.getTransforms().add(new Affine());
            }

            Utility.updateAffineTransform(nodeData, (Affine) meshNode.getTransforms().get(0));
            if (levelObject.name.equals("grid")) {
                setWireFrame(newContent, true);
                gridNode = meshNode;
                gridLevelObject = levelObject;
            } else {
                setWireFrame(newContent, wireframe);
            }
            // TODO mesh is updated each time these are called even if no rendering needs to happen
            setSubdivisionLevel(newContent, subdivisionLevel);
            setBoundaryMode(newContent, boundaryMode);
            setMapBorderMode(newContent, mapBorderMode);
        });
    }

    {
        keyEventHandler = event -> {
//            System.out.println("KeyEvent ...");
            Timeline timeline = getTimeline();
            Duration currentTime;
            double CONTROL_MULTIPLIER = 5.0;
            double SHIFT_MULTIPLIER = 1.0;
            double ALT_MULTIPLIER = 10.0;
            Platform.runLater(() -> getSubScene().requestFocus());
            //System.out.println("--> handleKeyboard>handle");

            // event.getEventType();

            switch (event.getCode()) {
                case F -> {
                    if (event.isControlDown()) {
                        //onButtonSave();
                    }
                }
                case O -> {
                    if (event.isControlDown()) {
                        //onButtonLoad();
                    }
                }
                case Z -> {
                    if (event.isShiftDown()) {
//                        cameraXform.ry.setAngle(0.0);
//                        cameraXform.rx.setAngle(0.0);
                        camera.setTranslateZ(-300.0);
                        cameraTransform.setOrientation(new ENG_Quaternion(true));
                    }
//                    cameraXform2.t.setX(0.0);
//                    cameraXform2.t.setY(0.0);
                    cameraTransform.setPosition(0, 0, 0);
                }
                  /*
                  case SPACE:
                      if (timelinePlaying) {
                          timeline.pause();
                          timelinePlaying = false;
                      }
                      else {
                          timeline.play();
                          timelinePlaying = true;
                      }
                      break;
       */
                case W, UP -> {
                    if (!event.isAltDown() && !event.isControlDown() && !event.isShiftDown()) {
                        cameraTransform.addPositionWithOrientation(0, 0, 10.0 * CONTROL_MULTIPLIER);
                    } else if (event.isControlDown()) {
                        cameraTransform.addOrientation(Transform.Axis.X, -10.0 * ALT_MULTIPLIER);
                    } else if (event.isShiftDown()) {
                        cameraTransform.addPositionWithOrientation(0, -10.0 * CONTROL_MULTIPLIER, 0);
                    } else if (event.isAltDown()) {
                        cameraTransform.addOrientation(Transform.Axis.X, -2.0 * ALT_MULTIPLIER);
                    }
                }
                case S, DOWN -> {
                    if (!event.isControlDown() && !event.isShiftDown() && !event.isAltDown()) {
                        cameraTransform.addPositionWithOrientation(0, 0, -10.0 * CONTROL_MULTIPLIER);
                    } else if (event.isControlDown()) {
                        cameraTransform.addOrientation(Transform.Axis.X, 10.0 * ALT_MULTIPLIER);
                    } else if (event.isShiftDown()) {
                        cameraTransform.addPositionWithOrientation(0, 10.0 * CONTROL_MULTIPLIER, 0);
                    } else if (event.isAltDown()) {
                        cameraTransform.addOrientation(Transform.Axis.X, 2.0 * ALT_MULTIPLIER);
                    }
                }
                case D, RIGHT -> {
                    if (!event.isControlDown() && !event.isShiftDown() && !event.isAltDown()) {
                        cameraTransform.addPositionWithOrientation(10.0 * CONTROL_MULTIPLIER, 0, 0);
                    } else if (event.isAltDown() && event.isShiftDown()) {
                        cameraTransform.addOrientation(Transform.Axis.Y, -10.0 * ALT_MULTIPLIER);
                    } else if (event.isControlDown()) {
                        cameraTransform.addPositionWithOrientation(10.0 * CONTROL_MULTIPLIER, 0, 0);
                    } else if (event.isShiftDown()) {
                        if (timeline != null) {
                            currentTime = timeline.getCurrentTime();
                            timeline.jumpTo(Frame.frame(Math.round(Frame.toFrame(currentTime)
                                    / 10.0)
                                    * 10
                                    + 10));
                            // timeline.jumpTo(Duration.seconds(currentTime.toSeconds() + ONE_FRAME));
                        }

                    } else if (event.isAltDown()) {
                        cameraTransform.addOrientation(Transform.Axis.Y, -2.0 * ALT_MULTIPLIER);
                    } else {
                        if (timeline != null) {
                            currentTime = timeline.getCurrentTime();
                            timeline.jumpTo(Frame.frame(Frame.toFrame(currentTime)
                                    + 1));
                            // timeline.jumpTo(Duration.seconds(currentTime.toSeconds() + ONE_FRAME));
                        }
                    }
                }
                case A, LEFT -> {
                    if (!event.isControlDown() && !event.isShiftDown() && !event.isAltDown()) {
                        cameraTransform.addPositionWithOrientation(-10.0 * CONTROL_MULTIPLIER, 0, 0);
                    } else if (event.isAltDown() && event.isShiftDown()) {
                        cameraTransform.addOrientation(Transform.Axis.Y, 10.0 * ALT_MULTIPLIER);
                    } else if (event.isControlDown()) {
                        cameraTransform.addPositionWithOrientation(-10.0 * CONTROL_MULTIPLIER, 0, 0);
                    } else if (event.isShiftDown()) {
                        if (timeline != null) {
                            currentTime = timeline.getCurrentTime();
                            timeline.jumpTo(Frame.frame(Math.round(Frame.toFrame(currentTime)
                                    / 10.0)
                                    * 10
                                    - 10));
                            // timeline.jumpTo(Duration.seconds(currentTime.toSeconds() - ONE_FRAME));
                        }
                    } else if (event.isAltDown()) {
                        cameraTransform.addOrientation(Transform.Axis.Y, 2.0 * ALT_MULTIPLIER);
                    } else {
                        if (timeline != null) {
                            currentTime = timeline.getCurrentTime();
                            timeline.jumpTo(Frame.frame(Frame.toFrame(currentTime)
                                    - 1));
                            // timeline.jumpTo(Duration.seconds(currentTime.toSeconds() - ONE_FRAME));
                        }
                    }
                }
                case Q -> cameraTransform.addOrientation(Transform.Axis.Z, -10.0 * CONTROL_MULTIPLIER);
                case E -> cameraTransform.addOrientation(Transform.Axis.Z, 10.0 * CONTROL_MULTIPLIER);
                case R -> {
                    removeCurrentIntersectedNode(currentIntersectedNode);
                }
                default -> {
                }
            }
            //System.out.println(cameraXform.getTranslateX() + ", " + cameraXform.getTranslateY() + ", " + cameraXform.getTranslateZ());
            Platform.runLater(() -> MainController.getSingleton().updateStatus());
        };

    }

    public LevelObject getCurrentIntersectedNodeLevelObject() {
        if (currentIntersectedNode != null) {
            return nodeToLevelObjectMap.get(currentIntersectedNode);
        }
        return null;
    }

    public Node getCurrentIntersectedNode() {
        return currentIntersectedNode;
    }

    public void updatePositionForCurrentIntersectedNode() {
        LevelObject currentIntersectedNodeLevelObject = getCurrentIntersectedNodeLevelObject();
        if (currentIntersectedNodeLevelObject == null) return;
        currentIntersectedNodeLevelObject.nodeData.getNodePos().set(currentIntersectedNodeLevelObject.position);
        Utility.updateAffineTransform(currentIntersectedNodeLevelObject.nodeData, (Affine) currentIntersectedNode.getTransforms().get(0));
    }

    public void updateOrientationForCurrentIntersectedNode() {
        LevelObject currentIntersectedNodeLevelObject = getCurrentIntersectedNodeLevelObject();
        if (currentIntersectedNodeLevelObject == null) return;
        currentIntersectedNodeLevelObject.nodeData.getNodeRotation().set(currentIntersectedNodeLevelObject.orientation);
        Utility.updateAffineTransform(currentIntersectedNodeLevelObject.nodeData, (Affine) currentIntersectedNode.getTransforms().get(0));
    }

    public void removeCurrentIntersectedNode(Node node) {
        removeCurrentIntersectedNode(node, false);
    }

    public void removeCurrentIntersectedNode(Node node, boolean searchOnlyStartObjects) {
        if (node != null) {
            Group group = meshToParentGroupMap.get(node);
            if (group != null) {
                meshToParentGroupMap.remove(node);
                LevelObject levelObject = nodeToLevelObjectMap.remove(node);
                boolean removed = true;
                if (levelObject == null) {
                    System.out.println("Could not find levelObject to remove");
                    removed = false;
                } else {
                    levelObjectList.remove(levelObject);
                    removeFromObjectList(levelObject);
                }
                boolean remove = autoScalingGroup.getChildren().remove(group);
                if (!remove) {
                    System.out.println("node: " + node.getId() + " could not be removed");
                    removed = false;
                }
                NodeData removedNodeData = nodeMap.remove(node);
                if (removedNodeData == null) {
                    System.out.println("Could not find nodeData to remove");
                    removed = false;
                }
                resetCurrentIntersectedNode();
                if (removed) {
                    SettingsController.getSingleton().despawnSelected(levelObject, searchOnlyStartObjects);
                }
            }
        }
    }

    public static final double GRID_ATTACHMENT_DISTANCE = 10.0;

    {
        mouseEventHandler = event -> {
//            System.out.println("MouseEvent ...");
            double flip = -1.0;
            double yFlip = 1.0;
            if (getYUp()) {
                yFlip = 1.0;
            } else {
                yFlip = -1.0;
            }
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                getSubScene().requestFocus();
                dragStartX = event.getSceneX();
                dragStartY = event.getSceneY();
                dragStartRotateX = cameraXRotate.getAngle();
                dragStartRotateY = cameraYRotate.getAngle();
                mousePosX = event.getSceneX();
                mousePosY = event.getSceneY();
                mouseOldX = event.getSceneX();
                mouseOldY = event.getSceneY();

                PickResult pickResult = event.getPickResult();
                if (pickResult.getIntersectedNode() instanceof SubScene) {
                    resetCurrentIntersectedNode();
                } else {
                    currentIntersectedNode = pickResult.getIntersectedNode();
                    LevelObject levelObject = nodeToLevelObjectMap.get(currentIntersectedNode);
                    if (levelObject == null) {
                        currentIntersectedNode = null;
                    } else {
                        NodeData nodeData = levelObject.nodeData;//nodeMap.get(currentIntersectedNode);
                        if (nodeData == null) {
                            nodeData = new NodeData(currentIntersectedNode);
                            nodeMap.put(currentIntersectedNode, nodeData);
                        }
                        currentIntersectedNodeDistance = pickResult.getIntersectedDistance();
                        currentIntersectedNodeInitialPos = Utility.unProjectDirection(camera, cameraTransform.getOrientation(),
                                mousePosX, /*getSubScene().getHeight() / 2*/mousePosY,
                                getSubScene().getWidth(), getSubScene().getHeight());
//                        System.out.println("mouse pressed position: " + levelObject.position);
                        SettingsController.getSingleton().updateSelectionPositionAndOrientation(levelObject.position, levelObject.orientation);
                    }
                }
                MainController.getSingleton().updateStatus();
                LevelObject levelObject = nodeToLevelObjectMap.get(currentIntersectedNode);
                if (levelObject != null) {
                    SettingsController.getSingleton().setSelection(levelObject);
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                double modifier = 1.0;
                double modifierFactor = 0.3;

                if (event.isControlDown()) {
                    modifier = 0.1;
                }
                if (event.isShiftDown()) {
                    modifier = 10.0;
                }

                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = event.getSceneX();
                mousePosY = event.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX); //*DELTA_MULTIPLIER;
                mouseDeltaY = (mousePosY - mouseOldY); //*DELTA_MULTIPLIER;

//                System.out.println("mouseDeltaX: " + mouseDeltaX + " mouseDeltaY: " + mouseDeltaY);

                if (currentIntersectedNode != null && currentIntersectedNode != gridNode) {
                    LevelObject levelObject = nodeToLevelObjectMap.get(currentIntersectedNode);
                    NodeData nodeData = levelObject.nodeData;//nodeMap.get(currentIntersectedNode);
                    if (currentModifier == Modifier.TRANSLATION) {
                        ENG_Vector3D cameraAxis = new ENG_Vector3D();
                        double cameraAngle = cameraTransform.getOrientation().toAngleAxisDeg(cameraAxis);
                        ENG_Vector3D xAxis = new ENG_Vector3D();
                        ENG_Vector3D yAxis = new ENG_Vector3D();
                        ENG_Vector3D zAxis = new ENG_Vector3D();
                        cameraTransform.getOrientation().toAxes(xAxis, yAxis, zAxis);
//                        System.out.println("camera xAxis: " + xAxis + " yAxis: " + yAxis + " zAxis: " + zAxis);
//                        System.out.println("cameraAxis: " + cameraAxis + " cameraAngle: " + cameraAngle);
                        ENG_Vector3D vecPos = Utility.unProjectDirection(camera, cameraTransform.getOrientation(), mousePosX, /*getSubScene().getHeight() / 2*/mousePosY,
                                getSubScene().getWidth(), getSubScene().getHeight());
                        ENG_Vector3D subtract = vecPos.sub(currentIntersectedNodeInitialPos);
                        subtract.mulInPlace(200);
//                        ENG_Matrix4 rotationMatrix = nodeData.getNodeRotation().toRotationMatrix();
//                        ENG_Vector4D transformedMousePos = rotationMatrix.transform(new ENG_Vector4D(subtract.x, subtract.y, subtract.z, 1.0f));
//                        System.out.println("substract: " + subtract + " transformedMousePos: " + transformedMousePos);
//                        subtract.y *= -1;
//                        System.out.println("substract: " + subtract);
                        nodeData.getNodePos().addInPlace(new ENG_Vector4D(subtract.x, -subtract.y, -subtract.z, 1.0));
//                        getCurrentIntersectedNodeTransform().appendTranslation(p.getX(), -1 * p.getY(), p.getZ());
                        snapToGrid(nodeData);
                        levelObject.position.set(nodeData.getNodePos());
//                        System.out.println("mouse dragged position: " + levelObject.position);
                        currentIntersectedNodeInitialPos = vecPos;
                        currentIntersectedNodeDistance = event.getPickResult().getIntersectedDistance();
                        SettingsController.getSingleton().updateSelectionPositionAndOrientation(levelObject.position, levelObject.orientation);
                    } else if (currentModifier == Modifier.ROTATION) {
                        if (mouseDeltaX != 0 || mouseDeltaY != 0) {
                            ENG_Vector3D zVec = new ENG_Vector3D(0, 0, 1);
                            ENG_Vector3D mouseVec = new ENG_Vector3D(mouseDeltaX, mouseDeltaY, 0);
                            ENG_Vector3D rotAxis = mouseVec.crossProduct(zVec);
                            rotAxis.normalize();
                            rotAxis = cameraTransform.getOrientation().mul(rotAxis);
                            ENG_Quaternion currentRot = ENG_Quaternion.fromAngleAxisDegRet(3.0, new ENG_Vector4D(rotAxis.x, rotAxis.y, rotAxis.z, 1.0));
                            currentRot.normalize();
                            ENG_Quaternion nodeRotation = nodeData.getNodeRotation();
                            ENG_Quaternion currentOrientationCopy = new ENG_Quaternion(nodeRotation);
                            ENG_Quaternion inverse = nodeRotation.inverseRet();
                            nodeRotation.mulInPlace(inverse);
                            nodeRotation.mulInPlace(currentRot);
                            nodeRotation.mulInPlace(currentOrientationCopy);
//                            nodeRotation.mulInPlace(currentRot);
//                            nodeRotation.normalize();
//                            LevelObject levelObject = nodeToLevelObjectMap.get(currentIntersectedNode);
                            levelObject.orientation.set(nodeRotation);
                            SettingsController.getSingleton().updateSelectionPositionAndOrientation(levelObject.position, levelObject.orientation);
                            //                            Point3D pivot = new Point3D(getCurrentIntersectedNodeTransform().getTx(), getCurrentIntersectedNodeTransform().getTy(), getCurrentIntersectedNodeTransform().getTz());
//                            System.out.println("rot pivot: " + pivot);
//                            getCurrentIntersectedNodeTransform().appendRotation(1, new Point3D(0, 0, 0), rotAxis);
                        }
                    } else if (currentModifier == Modifier.SCALING) {

                    }
                    Utility.updateAffineTransform(nodeData, getCurrentIntersectedNodeTransform());
                } else {
                    @SuppressWarnings("unused")
                    boolean alt = (true || event.isAltDown()); // For now, don't require ALT to be pressed
                    if (alt && (event.isMiddleButtonDown()
                            || (event.isPrimaryButtonDown()
                            && event.isSecondaryButtonDown()))) {
                        cameraTransform.addPosition(flip * mouseDeltaX * modifierFactor
                                * modifier * 0.3, 0, 0);
                        cameraTransform.addPosition(0, yFlip * mouseDeltaY * modifierFactor
                                * modifier * 0.3, 0);
                    } else if (alt && event.isPrimaryButtonDown()) {
                        cameraTransform.addOrientation(Transform.Axis.X, -yFlip * mouseDeltaY
                                * modifierFactor * modifier
                                * 2.0);
                        cameraTransform.addOrientation(Transform.Axis.Y, -flip * mouseDeltaX
                                * modifierFactor * modifier
                                * 2.0);
                    } else if (alt && event.isSecondaryButtonDown()) {
                        ENG_Vector3D position = cameraTransform.getPosition();
                        double z = position.z;
                        // double z = camera.getTranslateZ();
                        // double newZ = z + yFlip*flip*mouseDeltaX*modifierFactor*modifier;
                        position.z = z - flip * (mouseDeltaX + mouseDeltaY)
                                * modifierFactor * modifier;
//                        cameraPosition.setZ(newZ);
                        // camera.setTranslateZ(newZ);
                        cameraTransform.setPosition(position);
                    }
                }
                MainController.getSingleton().updateStatus();
            } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                nodeMap.clear();
            }
        };
    }

    private void snapToGrid(NodeData nodeData) {
        SettingsController settingsController = SettingsController.getSingleton();
        if (settingsController.isGridAttached()) {
            ENG_Vector4D nodePos = nodeData.getNodePos();
            switch (settingsController.getGridAttachmentType()) {
                case XZ -> {
                    if (Math.abs(nodePos.y - gridLevelObject.position.y) < GRID_ATTACHMENT_DISTANCE) {
                        nodePos.set(nodePos.x, gridLevelObject.position.y, nodePos.z);
                    }
                }
                case YZ -> {
                    if (Math.abs(nodePos.x - gridLevelObject.position.x) < GRID_ATTACHMENT_DISTANCE) {
                        nodePos.set(gridLevelObject.position.x, nodePos.y, nodePos.z);
                    }
                }
                case XY -> {
                    if (Math.abs(nodePos.z - gridLevelObject.position.z) < GRID_ATTACHMENT_DISTANCE) {
                        nodePos.set(nodePos.x, nodePos.y, gridLevelObject.position.z);
                    }
                }
                default ->
                        throw new IllegalStateException("Unexpected value: " + settingsController.getGridAttachmentType());
            }
        }
    }

    private void resetCurrentIntersectedNode() {
        currentIntersectedNode = null;
        currentIntersectedNodeDistance = -1.0;
        currentIntersectedNodeInitialPos = null;
    }

    public ContentModel() {
        // CAMERA
        camera.setNearClip(1.0); // TODO: Workaround as per RT-31255
        camera.setFarClip(10000.0); // TODO: Workaround as per RT-31255
        camera.setFieldOfView(90);

//        camera.getTransforms().addAll(yUpRotate
//                                      //cameraXRotate,
//                                      //cameraYRotate,
////                                      cameraPosition
////                                      cameraLookXRotate,
////                                      cameraLookZRotate
//        );
        //root3D.getChildren().add(camera);

//        root3D.getChildren().add(cameraXform);
//        cameraXform.getChildren().add(cameraXform2);
//        cameraXform2.getChildren().add(cameraXform3);
//        cameraXform3.getChildren().add(camera);
        root3D.getChildren().add(cameraTransform);
        cameraTransform.getChildren().add(camera);

        cameraTransform.setPosition(0, 0, -cameraDistance);
//        cameraTransform.setOrientation(ENG_Quaternion.fromAngleAxisDegRet(180, Transform.AXIS_Z));
//        cameraPosition.setZ(-cameraDistance);
        // camera.setTranslateZ(-cameraDistance);
        root3D.getChildren().add(autoScalingGroup);

        SessionManager sessionManager = SessionManager.getSessionManager();
        sessionManager.bind(cameraLookXRotate.angleProperty(), "cameraLookXRotate");
        sessionManager.bind(cameraLookZRotate.angleProperty(), "cameraLookZRotate");
//        sessionManager.bind(cameraPosition.xProperty(), "cameraPosition.x");
//        sessionManager.bind(cameraPosition.yProperty(), "cameraPosition.y");
//        sessionManager.bind(cameraPosition.zProperty(), "cameraPosition.z");
        sessionManager.bind(cameraTransform.getAffineMat().txProperty(), "cameraPosition.x");
        sessionManager.bind(cameraTransform.getAffineMat().tyProperty(), "cameraPosition.y");
        sessionManager.bind(cameraTransform.getAffineMat().tzProperty(), "cameraPosition.z");
        sessionManager.bind(cameraXRotate.angleProperty(), "cameraXRotate");
        sessionManager.bind(cameraYRotate.angleProperty(), "cameraYRotate");
        sessionManager.bind(camera.nearClipProperty(), "cameraNearClip");
        sessionManager.bind(camera.farClipProperty(), "cameraFarClip");

        // Build SubScene
        rebuildSubScene();
    }

    public Affine getCurrentIntersectedNodeTransform() {
        return (Affine) currentIntersectedNode.getTransforms().get(0);
    }

    public SimpleBooleanProperty ambientLightEnabledProperty() {
        return ambientLightEnabled;
    }

    public ObjectProperty<Node> getContentProperty() {
        return content;
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    public boolean getAmbientLightEnabled() {
        return ambientLightEnabled.get();
    }

    public AutoScalingGroup getAutoScalingGroup() {
        return autoScalingGroup;
    }

    public SubdivisionMesh.BoundaryMode getBoundaryMode() {
        return boundaryMode;
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public Rotate getCameraLookXRotate() {
        return cameraLookXRotate;
    }

    public Rotate getCameraLookZRotate() {
        return cameraLookZRotate;
    }

//    public Translate getCameraPosition() {
//        return cameraPosition;
//    }

    public Rotate getCameraXRotate() {
        return cameraXRotate;
    }

    public Rotate getCameraYRotate() {
        return cameraYRotate;
    }

    public Node getContent() {
        return content.get();
    }

    public PointLight getLight1() {
        return light1;
    }

    public boolean getLight1Enabled() {
        return light1Enabled.get();
    }

    public PointLight getLight2() {
        return light2;
    }

    public boolean getLight2Enabled() {
        return light2Enabled.get();
    }

    public PointLight getLight3() {
        return light3;
    }

    public boolean getLight3Enabled() {
        return light3Enabled.get();
    }

    public SubdivisionMesh.MapBorderMode getMapBorderMode() {
        return mapBorderMode;
    }

    public boolean getMsaa() {
        return msaa.get();
    }

    public Group getRoot3D() {
        return root3D;
    }

    public boolean getShowAxis() {
        return showAxis.get();
    }

    public int getSubdivisionLevel() {
        return subdivisionLevel;
    }

    public SubScene getSubScene() {
        return subScene.get();
    }

    public Timeline getTimeline() {
        return timeline.get();
    }

    public boolean getYUp() {
        return yUp.get();
    }

    public boolean isWireframe() {
        return wireframe;
    }

    public SimpleBooleanProperty light1EnabledProperty() {
        return light1Enabled;
    }

    public SimpleBooleanProperty light2EnabledProperty() {
        return light2Enabled;
    }

    public SimpleBooleanProperty light3EnabledProperty() {
        return light3Enabled;
    }

    public SimpleBooleanProperty msaaProperty() {
        return msaa;
    }

    public void setAmbientLightEnabled(boolean ambientLightEnabled) {
        this.ambientLightEnabled.set(ambientLightEnabled);
    }

    public void setBoundaryMode(SubdivisionMesh.BoundaryMode boundaryMode) {
        this.boundaryMode = boundaryMode;
        setBoundaryMode(root3D, boundaryMode);
    }

    public void setContent(Node content) {
        this.content.set(content);
    }

    public void setLight1Enabled(boolean light1Enabled) {
        this.light1Enabled.set(light1Enabled);
    }

    public void setLight2Enabled(boolean light2Enabled) {
        this.light2Enabled.set(light2Enabled);
    }

    public void setLight3Enabled(boolean light3Enabled) {
        this.light3Enabled.set(light3Enabled);
    }

    public void setMapBorderMode(SubdivisionMesh.MapBorderMode mapBorderMode) {
        this.mapBorderMode = mapBorderMode;
        setMapBorderMode(root3D, mapBorderMode);
    }

    public void setMsaa(boolean msaa) {
        this.msaa.set(msaa);
    }

    public void setShowAxis(boolean showAxis) {
        this.showAxis.set(showAxis);
    }

    public void setSubdivisionLevel(int subdivisionLevel) {
        this.subdivisionLevel = subdivisionLevel;
        setSubdivisionLevel(root3D, subdivisionLevel);
    }

    public void setTimeline(Timeline timeline) {
        this.timeline.set(timeline);
    }

    public void setWireFrame(boolean wireframe) {
        this.wireframe = wireframe;
        setWireFrame(root3D, wireframe);
    }

    public void setYUp(boolean yUp) {
        this.yUp.set(yUp);
    }

    public SimpleBooleanProperty showAxisProperty() {
        return showAxis;
    }

    public SimpleObjectProperty<SubScene> subSceneProperty() {
        return subScene;
    }

    public SimpleObjectProperty<Timeline> timelineProperty() {
        return timeline;
    }

    public SimpleBooleanProperty yUpProperty() {
        return yUp;
    }

    public void setPickedObject() {

    }

    public void setActiveModifierType(ToggleButton modifierType) {
        switch (modifierType.getId()) {
            case "translationButton":
                currentModifier = Modifier.TRANSLATION;
                break;
            case "rotationButton":
                currentModifier = Modifier.ROTATION;
                break;
            case "scalingButton":
                currentModifier = Modifier.SCALING;
                break;
            default:
                throw new IllegalArgumentException(modifierType.getId());
        }
    }

    private void createAxes() {
        double length = 200.0;
        double width = 1.0;
        double radius = 2.0;
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);
        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);
        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        xSphere = new Sphere(radius);
        ySphere = new Sphere(radius);
        zSphere = new Sphere(radius);
        xSphere.setMaterial(redMaterial);
        ySphere.setMaterial(greenMaterial);
        zSphere.setMaterial(blueMaterial);

        xSphere.setTranslateX(100.0);
        ySphere.setTranslateY(100.0);
        zSphere.setTranslateZ(100.0);

        xAxis = new Box(length, width, width);
        yAxis = new Box(width, length, width);
        zAxis = new Box(width, width, length);
        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);
    }

    public void rebuildSubScene() {
        SubScene oldSubScene = this.subScene.get();
        if (oldSubScene != null) {
            oldSubScene.setRoot(new Region());
            oldSubScene.setCamera(null);
            assert mouseEventHandler != null;
            oldSubScene.removeEventHandler(MouseEvent.ANY, mouseEventHandler);
            oldSubScene.removeEventHandler(KeyEvent.ANY, keyEventHandler);
            oldSubScene.removeEventHandler(ScrollEvent.ANY, scrollEventHandler);
        }

        SceneAntialiasing aaVal = msaa.get() ? SceneAntialiasing.BALANCED
                : SceneAntialiasing.DISABLED;
        SubScene subScene = new SubScene(root3D, 400, 400, true, aaVal);
        this.subScene.set(subScene);
        subScene.setFill(Color.ALICEBLUE);
        subScene.setCamera(camera);
        // SCENE EVENT HANDLING FOR CAMERA NAV
        assert mouseEventHandler != null;
        subScene.addEventHandler(MouseEvent.ANY, mouseEventHandler);
//        subScene.setOnKeyPressed(keyEventHandler);
        subScene.addEventHandler(KeyEvent.ANY, keyEventHandler);
//        subScene.addEventFilter(KeyEvent.ANY, keyEventHandler);
        subScene.addEventHandler(ZoomEvent.ANY, zoomEventHandler);
        subScene.addEventHandler(ScrollEvent.ANY, scrollEventHandler);
        root3D.addEventHandler(KeyEvent.ANY, keyEventHandler);
        root3D.setFocusTraversable(true);

//        subScene.setOnKeyPressed(event -> System.out.println("subScene keyPressed"));
//        root3D.setOnKeyPressed(event -> System.out.println("root3d key pressed"));

//        Scene scene = subScene.getScene();
//        scene.addEventHandler(KeyEvent.ANY, keyEventHandler);

        /*
        subScene.sceneProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                System.out.println("hello world");
            }
        });
        */
    }

    private void setBoundaryMode(Node node,
                                 SubdivisionMesh.BoundaryMode boundaryMode) {
        if (node instanceof PolygonMeshView) {
            ((PolygonMeshView) node).setBoundaryMode(boundaryMode);
        } else if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                setBoundaryMode(child, boundaryMode);
            }
        }
    }

    private void setMapBorderMode(Node node,
                                  SubdivisionMesh.MapBorderMode mapBorderMode) {
        if (node instanceof PolygonMeshView) {
            ((PolygonMeshView) node).setMapBorderMode(mapBorderMode);
        } else if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                setMapBorderMode(child, mapBorderMode);
            }
        }
    }

    private void setSubdivisionLevel(Node node, int subdivisionLevel) {
        if (node instanceof PolygonMeshView) {
            ((PolygonMeshView) node).setSubdivisionLevel(subdivisionLevel);
        } else if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                setSubdivisionLevel(child, subdivisionLevel);
            }
        }
    }

    private void setWireFrame(Node node, boolean wireframe) {
        if (node instanceof PolygonMeshView) {
            ((PolygonMeshView) node).setDrawMode(wireframe ? DrawMode.LINE
                    : DrawMode.FILL);
        } else if (node instanceof MeshView) {
            ((MeshView) node).setDrawMode(wireframe ? DrawMode.LINE
                    : DrawMode.FILL);
        } else if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                setWireFrame(child, wireframe);
            }
        }
    }

    public void export(VelocityContext templateContext) {
//        asteroidList.add(new Asteroid("Asteroid0", new ENG_Vector3D(0.0f, 200.0f, -250.0f), new ENG_Quaternion(true), 50));
//        asteroidList.add(new Asteroid("Asteroid1", new ENG_Vector3D(1500.0f -200.0f -250.0f), new ENG_Quaternion(true), 50));
//        templateContext.put("asteroids", levelObjectList);

//        templateContext.put("ships", shipObjectList);
//        templateContext.put("cargo", cargoObjectList);
//        templateContext.put("cargoShips", cargoShipObjectList);
//        templateContext.put("flags", flagObjectList);
//        templateContext.put("asteroids", asteroidObjectList);
//        templateContext.put("waypoints", waypointObjectList);
//        templateContext.put("statics", staticObjectList);
//        templateContext.put("playerShip", playerShip);

        templateContext.put("level", level);
        templateContext.put("newline", "\n");
//        templateContext.put("levelStart", level.levelStart);
//        templateContext.put("levelName", level.name);
    }

    private void loadLevel() {
        level.levelStart.startObjects.forEach(levelObject -> {
            if (levelObject.type == LevelObject.LevelObjectType.PLAYER_SHIP) {
                levelObject.meshName = Utility.getMeshName("ship_human0");
            }
            MainController.getSingleton().load(levelObject);
        });
        level.levelEventList.forEach(levelEvent -> levelEvent.spawn
                .forEach(levelObject -> MainController.getSingleton().load(levelObject)));
        SettingsController settingsController = SettingsController.getSingleton();
        if (settingsController.isCurrentWaypointsVisibility()) {
            settingsController.showWaypoints();
        }
    }

    public Transform getCameraTransform() {
        return cameraTransform;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
        loadLevel();
    }

    public String getSelectedMeshName() {
        return selectedMeshName;
    }

    public void setSelectedMeshName(String selectedMeshName) {
        this.selectedMeshName = selectedMeshName;
    }

    public String getCurrentShipType() {
        return currentShipType;
    }

    public void setCurrentShipType(String currentShipType) {
        this.currentShipType = currentShipType;
    }

    public String getCurrentSelectedShip() {
        return currentSelectedShip;
    }

    public void setCurrentSelectedShip(String currentSelectedShip) {
        this.currentSelectedShip = currentSelectedShip;
    }

    public String getCurrentSelectedEvent() {
        return currentSelectedEvent;
    }

    public void setCurrentSelectedEvent(String currentSelectedEvent) {
        this.currentSelectedEvent = currentSelectedEvent;
    }

    public void addEvent(String eventName) {
        events.add(eventName);
    }

    public void removeEvent(String eventName) {
        events.remove(eventName);
    }

    public void removeAllEvents() {
        events.clear();
    }

    public ArrayList<String> getEvents() {
        return new ArrayList<>(events);
    }

    public String getComparatorOperatorWin() {
        return comparatorOperatorWin;
    }

    public void setComparatorOperatorWin(String comparatorOperatorWin) {
        this.comparatorOperatorWin = comparatorOperatorWin;
    }

    public String getComparatorOperatorLose() {
        return comparatorOperatorLose;
    }

    public void setComparatorOperatorLose(String comparatorOperatorLose) {
        this.comparatorOperatorLose = comparatorOperatorLose;
    }

    public String getWinEndCondType() {
        return winEndCondType;
    }

    public void setWinEndCondType(String winEndCondType) {
        this.winEndCondType = winEndCondType;
    }

    public String getLoseEndCondType() {
        return loseEndCondType;
    }

    public void setLoseEndCondType(String loseEndCondType) {
        this.loseEndCondType = loseEndCondType;
    }

    public String getCurrentShipBehavior() {
        return currentShipBehavior;
    }

    public void setCurrentShipBehavior(String currentShipBehavior) {
        this.currentShipBehavior = currentShipBehavior;
    }

    public String getCurrentSelectedMeshWinCond() {
        return currentSelectedMeshWinCond;
    }

    public void setCurrentSelectedMeshWinCond(String currentSelectedMeshWinCond) {
        this.currentSelectedMeshWinCond = currentSelectedMeshWinCond;
    }

    public String getCurrentSelectedMeshLoseCond() {
        return currentSelectedMeshLoseCond;
    }

    public void setCurrentSelectedMeshLoseCond(String currentSelectedMeshLoseCond) {
        this.currentSelectedMeshLoseCond = currentSelectedMeshLoseCond;
    }

    public String getCurrentExitEventSelectedMesh() {
        return currentExitEventSelectedMesh;
    }

    public void setCurrentExitEventSelectedMesh(String currentExitEventSelectedMesh) {
        this.currentExitEventSelectedMesh = currentExitEventSelectedMesh;
    }

    public String getCurrentWaypointSector() {
        return currentWaypointSector;
    }

    public void setCurrentWaypointSector(String currentWaypointSector) {
        this.currentWaypointSector = currentWaypointSector;
    }

    public String getCurrentWaypoint() {
        return currentWaypoint;
    }

    public void setCurrentWaypoint(String currentWaypoint) {
        this.currentWaypoint = currentWaypoint;
    }

    public String getCurrentWaypointNextId() {
        return currentWaypointNextId;
    }

    public void setCurrentWaypointNextId(String currentWaypointNextId) {
        this.currentWaypointNextId = currentWaypointNextId;
    }

    public Node getGridNode() {
        return gridNode;
    }

    public LevelObject getGridLevelObject() {
        return gridLevelObject;
    }

    //    public Xform getCameraXform() {
//        return cameraXform;
//    }
//
//    public Xform getCameraXform2() {
//        return cameraXform2;
//    }
//
//    public Xform getCameraXform3() {
//        return cameraXform3;
//    }
}
