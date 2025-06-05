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

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.javafx.experiments.osspecific.OSSpecific;
import com.javafx.experiments.shape3d.SubdivisionMesh;
import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.blackholedarksun.levelresource.*;
import headwayent.blackholedarksun.systems.helper.ai.Waypoint;
import headwayent.blackholedarksun.systems.helper.ai.WaypointSector;
import headwayent.blackholedarksun.systems.helper.ai.WaypointSystem;
import headwayent.hotshotengine.*;
import headwayent.hotshotengine.renderer.ENG_Light;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.util.StringConverter;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static com.javafx.experiments.utils3d.Utility.*;

/**
 * Controller class for settings panel
 */
public class SettingsController implements Initializable {

    public static final String EVENT_START = "start";
    public static final int FOV_DEG = 90;
    private static SettingsController settingsController;
    @FXML
    private ToggleButton asteroid2Toggle;
    @FXML
    private ToggleGroup gridGroup;
    @FXML
    private TitledPane x1;
    @FXML
    private TitledPane x2;
    @FXML
    private ToggleButton rotationButton;
    @FXML
    private Insets x3;
    @FXML
    private TitledPane x4;
    @FXML
    private TitledPane x5;
    @FXML
    private ToggleButton translationButton;
    @FXML
    private Button loadAsteroidBtn;
    @FXML
    private ToggleButton ateroid3Toggle;
    @FXML
    private ToggleButton asteroid4Toggle;
    @FXML
    private ToggleButton asteroid1Toggle;
    @FXML
    private ToggleButton asteroid5Toggle;
    @FXML
    private ToggleButton scalingButton;
    @FXML
    private TextField selectionXRot;
    @FXML
    private TextField selectedMeshNameText;
    @FXML
    private TextField selectionYPos;
    @FXML
    private CheckBox gridEnabledCheckBox;
    @FXML
    private TextField selectionYRot;
    @FXML
    private TextField selectionZPos;
    @FXML
    private RadioButton gridYZ;
    @FXML
    private TextField selectionZRot;
    @FXML
    private RadioButton gridXZ;
    @FXML
    private TextField selectedMeshLevelNameText;

    @FXML
    public ColorPicker ambientColorPicker;
    @FXML
    public CheckBox ambientEnableCheckbox;
    @FXML
    public ColorPicker backgroundColorPicker;
    @FXML
    public TreeTableColumn<Node, Double> depthColumn;
    @FXML
    public Label farClipLabel;
    @FXML
    public Slider farClipSlider;
    @FXML
    public Slider fovSlider;
    @FXML
    public TreeTableColumn<Node, Double> heightColumn;
    @FXML
    public TreeTableView<Node> hierarachyTreeTable;
    @FXML
    public TreeTableColumn<Node, String> idColumn;
    @FXML
    public ColorPicker light1ColorPicker;
    @FXML
    public CheckBox light1EnabledCheckBox;
    @FXML
    public CheckBox light1followCameraCheckBox;
    @FXML
    public Slider light1x;
    @FXML
    public Slider light1y;
    @FXML
    public Slider light1z;
    @FXML
    public ColorPicker light2ColorPicker;
    @FXML
    public CheckBox light2EnabledCheckBox;
    @FXML
    public Slider light2x;
    @FXML
    public Slider light2y;
    @FXML
    public Slider light2z;
    @FXML
    public ColorPicker light3ColorPicker;
    @FXML
    public CheckBox light3EnabledCheckBox;
    @FXML
    public Slider light3x;
    @FXML
    public Slider light3y;
    @FXML
    public Slider light3z;
    @FXML
    public CheckBox msaaCheckBox;
    @FXML
    public Label nearClipLabel;
    @FXML
    public Slider nearClipSlider;
    @FXML
    public TreeTableColumn<Node, String> nodeColumn;
    @FXML
    public Label selectedNodeLabel;
    @FXML
    public Accordion settings;
    @FXML
    public CheckBox showAxisCheckBox;
    @FXML
    public ToggleGroup subdivisionBoundaryGroup;
    @FXML
    public ToggleGroup subdivisionLevelGroup;
    @FXML
    public ToggleGroup subdivisionSmoothGroup;
    @FXML
    public ListView<Transform> transformsList;
    @FXML
    public TreeTableColumn<Node, Boolean> visibilityColumn;
    @FXML
    public TreeTableColumn<Node, Double> widthColumn;
    @FXML
    public CheckBox wireFrameCheckbox;
    @FXML
    public TitledPane x6;

    @FXML
    public ToggleGroup asteroidGroup;
    @FXML
    public ToggleGroup modifiersGroup;

    @FXML
    public TextField xPosTextField;
    @FXML
    public TextField yPosTextField;
    @FXML
    public TextField zPosTextField;
    @FXML
    public TextField xRotTextField;
    @FXML
    public TextField yRotTextField;
    @FXML
    public TextField zRotTextField;
    @FXML
    public TextField xSclTextField;
    @FXML
    public TextField ySclTextField;
    @FXML
    public TextField zSclTextField;

    @FXML
    public TextField asteroidClusterXMin;
    @FXML
    public TextField asteroidClusterYMin;
    @FXML
    public TextField asteroidClusterZMin;
    @FXML
    public TextField asteroidClusterXMax;
    @FXML
    public TextField asteroidClusterYMax;
    @FXML
    public TextField asteroidClusterZMax;
    @FXML
    public TextField asteroidCount;
    @FXML
    public TextField selectionXPos;

    @FXML
    public CheckBox yUpCheckBox;

    @FXML
    private TextField gridZPos;
    @FXML
    private TextField gridXPos;
    @FXML
    private TextField gridYPos;

    private final ContentModel contentModel = Jfx3dViewerApp.getContentModel();
    @FXML
    private TextField meshYPosText;
    @FXML
    private TextField meshZRotText;
    @FXML
    private TextField meshXRotText;
    @FXML
    private TextField meshYRotText;
    @FXML
    private ComboBox<String> meshComboBox;
    @FXML
    private TextField meshXPosText;
    @FXML
    private TextField meshZPosText;
    @FXML
    private Button meshSpawn;
    @FXML
    private TextField waypointsWaypointsSectorZMin;
    @FXML
    private TextField startEventLightSpecularColorZ1;
    @FXML
    private TableColumn<PlayerShipSelection, String> playerShipSelectedShipsColumn;
    @FXML
    private TextField meshDestinationXPos;
    @FXML
    private TextField waypointsNextId;
    @FXML
    private Button eventsAddSelected;
    @FXML
    private TextField startEventLightSpecularColorY1;
    @FXML
    private TextField waypointsWaypointZPos;
    @FXML
    private TextField eventsFinalEnd;
    @FXML
    private Button waypointsRemoveSector;
    @FXML
    private TextField startEventLightSpecularColorX1;
    @FXML
    private TextField startEventAmbientLowerHemiG1;
    @FXML
    private Button eventsRemoveLoseCond;
    @FXML
    private TableColumn<PlayerShipSelection, String> playerShipShipSelectionColumn;
    @FXML
    private Button eventsUndoAddSelected;
    @FXML
    private TextField waypointsWaypointsSectorZMax1;
    @FXML
    private TextField meshPrioritize;
    @FXML
    private ComboBox<String> meshBehaviorSelection;
    @FXML
    private Button waypointsCreateWaypointTable;
    @FXML
    private Button selectionSetPosition;
    @FXML
    private TextField startEventAmbientUpperHemiG;
    @FXML
    private TextField waypointsWaypointYPos;
    @FXML
    private TextField levelTitle;
    @FXML
    private TextField startEventAmbientUpperHemiA;
    @FXML
    private TextField waypointsWaypointRadius;
    @FXML
    private TextField startEventAmbientUpperHemiB;
    @FXML
    private TextField playerShipPositionY;
    @FXML
    private TextField playerShipPositionX;
    @FXML
    private TextField playerShipPositionZ;
    @FXML
    private ComboBox<String> eventsWinCondTypeCombo;
    @FXML
    private ComboBox<String> meshEventSelection;
    @FXML
    private TextField startEventAmbientUpperHemiR;
    @FXML
    private CheckBox meshFriendlyEnabled;
    @FXML
    private TextField meshDestinationYPos;
    @FXML
    private ComboBox<String> eventsWinComparatorCombo;
    @FXML
    private ComboBox<String> eventsLoseCondEventSelected;
    @FXML
    private Button waypointsRemoveNextId;
    @FXML
    private Button levelResetLevel;
    @FXML
    private ComboBox<String> waypointsWaypointSectorSelection;
    @FXML
    private Button playerShipAddSelectedShip;
    @FXML
    private TextField meshDestinationZPos;
    @FXML
    private TextField startEventHemiDirZ;
    @FXML
    private Button selectionUndoOrientation;
    @FXML
    private TextField startEventHemiDirY;
    @FXML
    private Button selectionSetRotation;
    @FXML
    private TextField startEventHemiDirX;
    @FXML
    private CheckBox meshGridAttached;
    @FXML
    private Button waypointsWaypointSectorAdd;
    @FXML
    private ComboBox<String> eventsSelectionComboBox;
    @FXML
    private TextField waypointsWaypointXPos;
    @FXML
    private TextField eventsEndCondWinDelay;
    @FXML
    private ComboBox<String> eventsLoseCondTypeCombo;
    @FXML
    private TableView<PlayerShipSelection> playerShipSelectionTable;
    @FXML
    private TextField meshScanRadius;
    @FXML
    private ComboBox<String> waypointsNextIdSelection;
    @FXML
    private Button waypointsRemoveWaypoint;
    @FXML
    private ComboBox<String> waypointsWaypointSelection;
    @FXML
    private CheckBox playerSHipAIEnabled;
    @FXML
    private TextField startEventAmbientLowerHemiR1;
    @FXML
    private CheckBox playerShipInvincibilityEnabled;
    @FXML
    private TextField startEventAmbientLowerHemiA1;
    @FXML
    private Button eventsRemoveWinCond;
    @FXML
    private TextField playerShipOrientationY;
    @FXML
    private TextField playerShipOrientationZ;
    @FXML
    private TextField playerShipOrientationX;
    @FXML
    private Button selectionDespawn;
    @FXML
    private TextField startEventAmbientLowerHemiB1;
    @FXML
    private TextField eventsEventTitle;
    @FXML
    private Button waypointsnextIdAdd;
    @FXML
    private ComboBox<String> startEventSkyboxSelection;
    @FXML
    private TextField startEventLightDirY;
    @FXML
    private TextField startEventLightDirZ;
    @FXML
    private TextField startEventLightDirX;
    @FXML
    private TextField startEventLightPowerScale;
    @FXML
    private ComboBox<String> meshShipTypeSelection;
    @FXML
    private Button eventsUndoCreateEvent;
    @FXML
    private CheckBox waypointsGridAttached;
    @FXML
    private ComboBox<String> eventsLoseComparatorCombo;
    @FXML
    private Button eventsAddWinCond;
    @FXML
    private Button selectionUndoPosition;
    @FXML
    private Button waypointsWaypointAdd;
    @FXML
    private Button playerShipRemoveSelectedShip;
    @FXML
    private TextField waypointsWaypointsSectorXMax1;
    @FXML
    private ComboBox<String> startEventLightTypeSelection;
    @FXML
    private TextField waypointsWaypointsSectorXMin;
    @FXML
    private TextField eventsDelay;
    @FXML
    private Button selectionUndoDespawn;
    @FXML
    private ComboBox<String> playerShipTeamSelection;
    @FXML
    private TextField startEventLightDiffuseColorX;
    @FXML
    private CheckBox meshDestinationEnabled;
    @FXML
    private TextField startEventLightDiffuseColorY;
    @FXML
    private TextField startEventLightDiffuseColorZ;
    @FXML
    private Button eventsAddLoseCond;
    @FXML
    private TextField waypointsWaypointsSectorYMax1;
    @FXML
    private Button levelNewLevel;
    @FXML
    private TextField waypointsWaypointsSectorYMin;
    @FXML
    private TextField meshAttack;
    @FXML
    private TextField eventsLoseEndCond;
    @FXML
    private TextField eventsPreviousEndCond;
    @FXML
    private TextField eventsEndCondLossDelay;
    @FXML
    private CheckBox meshAIEnabled;
    @FXML
    private TextField eventsWinEndCond;
    @FXML
    private Button eventsCreateEvent;
    @FXML
    private TextField startEventLightSpecularColorW;
    @FXML
    private TextField startEventLightDiffuseColorW;
    @FXML
    private TextField meshShipName;
    @FXML
    private TextField meshRotAngleText;
    @FXML
    private CheckBox meshInvincibilityEnabled;
    @FXML
    private TextField eventsEndCondWinDelay1;
    @FXML
    private TextField eventsEndCondLoseObjectiveAchievedDelay;
    @FXML
    private TextField eventsEndCondWinObjectiveAchievedDelay;
    @FXML
    private ComboBox<String> selectedMesh;
    @FXML
    private TextField selectionAngleRot;
    @FXML
    private TextField playerShipOrientationAngle;
    @FXML
    private TextField meshHealth;
    @FXML
    private TextField meshRadius;
    @FXML
    private TextField exitEventText;
    @FXML
    private Button exitEventAddButton;
    @FXML
    private Button eventsMeshAddWinButton;
    @FXML
    private Button eventsMeshAddLoseButton;
    @FXML
    private ComboBox<String> eventsMeshSelectionLoseCond;
    @FXML
    private ComboBox<String> eventsMeshSelectionWinCond;
    @FXML
    private ComboBox<String> eventsWinCondEventSelected;
    private int winCondCount;
    private int loseCondCount;
    @FXML
    private ComboBox<String> exitEventMeshSelection;
    @FXML
    private Button exitEventAddToExitList;
    @FXML
    private TextField eventsFinalEndIgnoreLossEvent;
    @FXML
    private Button eventsFinalEndAddignoreLossEvent;
    @FXML
    private CheckBox waypointGridAttached;
    @FXML
    private Button waypointsAddToCurrentWaypoint;
    @FXML
    private TextField waypointsEntranceMinDistance;
    @FXML
    private TextField waypointsEntranceDirX;
    @FXML
    private TextField waypointsEntranceDirZ;
    @FXML
    private TextField waypointsEntranceDirY;
    @FXML
    private TextField waypointsEntranceAngle;
    @FXML
    private RadioButton gridXY;
    @FXML
    private Button gridSetPositionButton;
    private boolean gridAttached;
    private GridAttachmentType gridAttachmentType = GridAttachmentType.XZ;
    @FXML
    private TextField asteroidRadiusCentreZ;
    @FXML
    private TextField asteroidRadiusCentreX;
    @FXML
    private TextField asteroidRadiusCentreY;
    @FXML
    private Button asteroidRemoveClusterButton;
    @FXML
    private TextField asteroidRadius;
    @FXML
    private Button asteroidAddClusterButton;
    @FXML
    private CheckBox asteroidUseRadiusCheckBox;
    private HashMap<Integer, ArrayList<LevelObject>> asteroidMap = new HashMap<>();
    @FXML
    private CheckBox waypointsVisibilityCheckBox;
    private boolean currentWaypointsVisibility;
    @FXML
    private CheckBox waypointEntraceDirActive;
    private int waypointSectorNextId = 1;
    private int waypointNextId = 1;
    @FXML
    private TextField waypointsWaypointMaxUsers;
    @FXML
    private TextField waypointWaypointsSectorMaxUsers;
    @FXML
    private CheckBox waypointsWaypointActive;

    @FXML
    public void removeAsteroidCluster(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        ToggleButton selectedToggle = (ToggleButton) asteroidGroup.getSelectedToggle();
        int asteroidGroupPosition = Integer.parseInt(selectedToggle.getText());
        ArrayList<LevelObject> levelObjects = asteroidMap.get(asteroidGroupPosition);
        if (levelObjects == null) {
            return;
        }
        levelObjects.forEach(levelObject -> contentModel.removeCurrentIntersectedNode(levelObject.nodeData.getNode(), true));
        levelObjects.clear();
    }

    public void saveLevelOnExit() {
        if (!checkLevelCreated()) return;
        if (!showConfirmationDialog("Save?")) {
            return;
        }
        MainController.getSingleton().export(null);
    }

    public void saveLevel(File newFile) {
        try (Output output = new Output(new FileOutputStream(newFile))) {
            getKryo().writeObject(output, contentModel.getLevel());
            System.out.println("Save succeeded to " + newFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void loadLevel(File newFile) {
        try (Input input = new Input(new FileInputStream(newFile))) {
            Level level = getKryo().readObject(input, Level.class);
            contentModel.setLevel(level);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onWaypointSelected(ActionEvent actionEvent) {

    }

    public enum GridAttachmentType {
        XZ, YZ, XY
    }

    @FXML
    public void onMeshSelected(ActionEvent actionEvent) {
    }

    @FXML
    public void onExitEventAdd(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        if (!checkCurrentLevelEventValid()) return;
        LevelEvent levelEvent = getCurrentLevelEvent();
        assert levelEvent != null;
        levelEvent.exitObjects.clear();
        levelEvent.exitObjects.addAll(Arrays.asList(getText(exitEventText, "No exit event added!").trim().split(" ")));
    }

    @FXML
    public void onMeshAddToWinCond(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        if (!checkCurrentLevelEventValid()) return;
        if (!eventsWinEndCond.getText().contains(eventsMeshSelectionWinCond.getValue())) {
            eventsWinEndCond.setText((eventsWinEndCond.getText().trim() + " " + eventsMeshSelectionWinCond.getValue()).trim());
        }
    }

    @FXML
    public void onMeshAddToLoseCond(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        if (!checkCurrentLevelEventValid()) return;
        if (!eventsLoseEndCond.getText().contains(eventsMeshSelectionLoseCond.getValue())) {
            eventsLoseEndCond.setText((eventsLoseEndCond.getText().trim() + " " + eventsMeshSelectionLoseCond.getValue()).trim());
        }
    }

    @FXML
    public void onSelectionSetPosition(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
//        if (!checkCurrentLevelEventValid()) return;
        contentModel.getCurrentIntersectedNodeLevelObject().position.set(readVector3D(selectionXPos, selectionYPos, selectionZPos));
        contentModel.updatePositionForCurrentIntersectedNode();
    }

    @FXML
    public void onSelectionUndoPosition(ActionEvent actionEvent) {
    }

    @FXML
    public void onSelectionUndoOrientation(ActionEvent actionEvent) {
    }

    @FXML
    public void onSelectionOnDespawn(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        if (!checkCurrentLevelEventValid()) return;
        eventsMeshSelectionWinCond.getItems().remove(selectedMeshNameText.getText());
        eventsMeshSelectionLoseCond.getItems().remove(selectedMeshNameText.getText());
        exitEventMeshSelection.getItems().remove(selectedMeshNameText.getText());
    }

    @FXML
    public void onSelectionUndoDespawn(ActionEvent actionEvent) {
        contentModel.removeCurrentIntersectedNode(contentModel.getCurrentIntersectedNode());
    }

    public void despawnSelected(LevelObject mesh) {
        despawnSelected(mesh, false);
    }

    public void despawnSelected(LevelObject mesh, boolean searchOnlyStartObjects) {
        if (!checkLevelCreated()) return;
//        if (!checkCurrentLevelEventValid()) return;
        if (!searchOnlyStartObjects && (meshEventSelection.getValue() == null || meshEventSelection.getValue().isBlank())) {
            showErrorDialog("Event for this mesh must be selected");
            return;
        }
//        LevelObject mesh = readMesh();
        contentModel.getLevel().levelEventList.stream().filter(levelEvent -> levelEvent.name.equalsIgnoreCase(meshEventSelection.getValue())).forEach(levelEvent -> levelEvent.spawn.removeIf(levelObject -> levelObject.name.equalsIgnoreCase(mesh.name)));
        if (searchOnlyStartObjects || meshEventSelection.getValue().equalsIgnoreCase(EVENT_START)) {
            contentModel.getLevel().getLevelStart().startObjects.removeIf(levelObject -> levelObject.name.equalsIgnoreCase(mesh.name));
        } else {
            contentModel.getLevel().levelEventList.stream().filter(levelEvent ->
                            levelEvent.name.equalsIgnoreCase(meshEventSelection.getValue()))
                    .forEach(levelEvent -> levelEvent.spawn.removeIf(levelObject -> levelObject.name.equalsIgnoreCase(mesh.name)));
        }
        selectedMesh.getItems().remove(mesh.name);
        eventsMeshSelectionWinCond.getItems().remove(mesh.name);
        eventsMeshSelectionLoseCond.getItems().remove(mesh.name);
        exitEventMeshSelection.getItems().remove(mesh.name);
    }

    @FXML
    public void onSelectionSetOrientation(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
//        if (!checkCurrentLevelEventValid()) return;
        contentModel.getCurrentIntersectedNodeLevelObject().orientation.set(readQuaternion(selectionXRot, selectionYRot, selectionZRot, selectionAngleRot));
        contentModel.updateOrientationForCurrentIntersectedNode();
    }

    public void updateSelectionPositionAndOrientation(ENG_Vector4D position, ENG_Quaternion orientation) {
        writeVector(position, selectionXPos, selectionYPos, selectionZPos);
        writeQuaternion(orientation, selectionXRot, selectionYRot, selectionZRot, selectionAngleRot);
    }

    @FXML
    public void onExitEventAddToList(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        if (!checkCurrentLevelEventValid()) return;
        if (!exitEventText.getText().contains(exitEventMeshSelection.getValue())) {
            exitEventText.setText((exitEventText.getText().trim() + " " + exitEventMeshSelection.getValue()).trim());
        }
    }

    @FXML
    public void onAddFinalEndIgnoreLossEvent(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        if (!checkCurrentLevelEventValid()) return;
        contentModel.getLevel().levelEnd.endEventIgnoreLossList.addAll(Arrays.asList(
                getText(eventsFinalEndIgnoreLossEvent, "No events to ignore added!").trim().split(" ")));
    }

    @FXML
    public void onWaypointAttachToGrid(ActionEvent actionEvent) {
    }

    @FXML
    public void onWaypointAddToSelectedWaypoint(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        if (waypointsWaypointXPos.getText().isBlank() ||
                waypointsWaypointYPos.getText().isBlank() ||
                waypointsWaypointZPos.getText().isBlank() ||
                waypointsEntranceDirX.getText().isBlank() ||
                waypointsEntranceDirY.getText().isBlank() ||
                waypointsEntranceDirZ.getText().isBlank() ||
                waypointsEntranceAngle.getText().isBlank() ||
                waypointsEntranceMinDistance.getText().isBlank() ||
                waypointsWaypointRadius.getText().isBlank()) {
            showErrorDialog("Please enter a valid waypoint coords!");
            return;
        }
        contentModel.getLevel().levelStart.waypointSectors.stream()
                .filter(levelWaypointSector -> levelWaypointSector.getId() == Integer.parseInt(contentModel.getCurrentWaypointSector()))
                .findFirst().flatMap(levelWaypointSector -> levelWaypointSector.getWaypoints().stream()
                        .filter(levelWaypoint -> levelWaypoint.getId() == Integer.parseInt(contentModel.getCurrentWaypoint()))
                        .findFirst()).ifPresent(levelWaypoint -> {
                            boolean waypointInsideExtents = false;
                            ENG_Vector4D position = readVector4DAsPt(waypointsWaypointXPos, waypointsWaypointYPos, waypointsWaypointZPos);
                            for (LevelWaypointSector waypointSector : contentModel.getLevel().levelStart.waypointSectors) {
                                if (waypointSector.getId() == Integer.parseInt(contentModel.getCurrentWaypointSector())) {
                                    if (waypointSector.getBox().contains(position)) {
                                        waypointInsideExtents = true;
                                    } else {
                                        showErrorDialog("Please enter valid waypoint coords which are inside the sector extents!");
                                    }
                                    break;
                                }
                            }

                            if (waypointInsideExtents) {
                                levelWaypoint.position.set(position);
                                if (waypointEntraceDirActive.isSelected()) {
                                    levelWaypoint.entranceOrExitDirection.set(readVector4DAsPt(waypointsEntranceDirX, waypointsEntranceDirY, waypointsEntranceDirZ));
                                    levelWaypoint.entranceOrExitAngle = readFloat(waypointsEntranceAngle);
                                    levelWaypoint.entranceOrExitMinDistance = readFloat(waypointsEntranceMinDistance);
                                    levelWaypoint.entranceOrExitActive = true;
                                }
                                levelWaypoint.radius = readFloat(waypointsWaypointRadius);
                                levelWaypoint.maxWaypointAttachmentCount = readInt(waypointsWaypointMaxUsers);
                                levelWaypoint.active = waypointsWaypointActive.isSelected();
                                showWaypoint(Integer.parseInt(contentModel.getCurrentWaypointSector()), Integer.parseInt(contentModel.getCurrentWaypoint()), levelWaypoint.position);
                            }
                });
    }

    @FXML
    public void onGridXYSelected(ActionEvent actionEvent) {
        updateGridPositionAndOrientation();
    }

    @FXML
    public void onGridSetPosition(ActionEvent actionEvent) {
        if (contentModel.getGridNode() == null) {
            return;
        }
        LevelObject gridLevelObject = contentModel.getGridLevelObject();
        Affine affine = (Affine) contentModel.getGridNode().getTransforms().get(0);
        ENG_Vector3D gridPosition = readVector3D(gridXPos, gridYPos, gridZPos);
        gridLevelObject.position.set(gridPosition);
        updateGridPositionAndOrientation();
    }

    private static class PlayerShipSelection {
        public SimpleStringProperty name = new SimpleStringProperty();
        public SimpleBooleanProperty selected = new SimpleBooleanProperty();

        public PlayerShipSelection(String name) {
            this.name.set(name);
        }
    }

    @FXML
    public void onGridEnabled(ActionEvent actionEvent) {
        updateGridStatus();
    }

    private void updateGridPositionAndOrientation() {
        if (contentModel.getGridNode() == null) {
            return;
        }
        LevelObject gridLevelObject = contentModel.getGridLevelObject();
        Affine affine = (Affine) contentModel.getGridNode().getTransforms().get(0);
        affine.setToIdentity();
        // TODO no time to understand how the Affine class works. Also, don't care...
        if (Jfx3dViewerApp.SCALE_UP_100) {
            affine.prependScale(100, 100, 100);
        }
        if (gridXY.isSelected()) {
            gridLevelObject.orientation.set(ENG_Quaternion.fromAngleAxisDegRet(90, new ENG_Vector3D(1, 0, 0)));
            affine.prependRotation(90, 0, 0, 0, 1, 0, 0);
            gridAttachmentType = GridAttachmentType.XY;
        } else if (gridXZ.isSelected()) {
            gridLevelObject.orientation.mulInPlace(ENG_Quaternion.fromAngleAxisDegRet(0, new ENG_Vector3D(0, 1, 0)));
            affine.prependRotation(90, 0, 0, 0, 0, 1, 0);
            gridAttachmentType = GridAttachmentType.XZ;
        } else if (gridYZ.isSelected()) {
            gridLevelObject.orientation.mulInPlace(ENG_Quaternion.fromAngleAxisDegRet(90, new ENG_Vector3D(0, 0, 1)));
            affine.prependRotation(90, 0, 0, 0, 0, 0, 1);
            gridAttachmentType = GridAttachmentType.YZ;
        }
        // Hocus pocus to change the coordinate system for our game. X - right Y - up Z - back. JavaFX 3D uses X - right Y - down Z - front.
        affine.prependTranslation(gridLevelObject.position.x, -gridLevelObject.position.y, -gridLevelObject.position.z);
//        ENG_Vector4D axis = new ENG_Vector4D();
//        double angle = gridLevelObject.orientation.toAngleAxisDeg(axis);
//        gridLevelObject.nodeData.getNodeRotation().set(gridLevelObject.orientation);
//        Utility.updateAffineTransform(gridLevelObject.nodeData, (Affine) contentModel.getGridNode().getTransforms().get(0));
    }

    private void updateGridStatus() {
        if (gridEnabledCheckBox.isSelected()) {
            if (contentModel.getGridNode() == null) {
                System.out.println("loading grid");

                LevelObject levelObject = new LevelObject();
                levelObject.type = LevelObject.LevelObjectType.ASTEROID;
                levelObject.name = "grid";
                levelObject.meshName = Jfx3dViewerApp.GRID_PATH;
                levelObject.position.set(0, 0, 0);
                levelObject.health = 50;

                MainController.getSingleton().load(levelObject);
            } else {
                contentModel.getGridNode().setVisible(true);
            }
        } else {
//            MainController.getSingleton().unload("grid");
            if (contentModel.getGridNode() != null) {
                contentModel.getGridNode().setVisible(false);
            }
        }
    }

    @FXML
    public void createMesh(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
//        if (!checkCurrentLevelEventValid()) return;
        if (meshEventSelection.getValue() == null || meshEventSelection.getValue().isBlank()) {
            showErrorDialog("Event for this mesh must be selected");
            return;
        }
        LevelObject mesh = readMesh();
        for (LevelEvent levelEvent : contentModel.getLevel().levelEventList) {
            if (levelEvent.name.equalsIgnoreCase(meshEventSelection.getValue())) {
                for (LevelObject levelObject : levelEvent.spawn) {
                    if (levelObject.name.equalsIgnoreCase(mesh.name)) {
                        showErrorDialog("Mesh: " + mesh.name + " already added!");
                        return;
                    }
                }
            }
        }
        if (mesh.type == LevelObject.LevelObjectType.PLAYER_SHIP) {
            if (!meshEventSelection.getValue().equalsIgnoreCase(EVENT_START)) {
                showErrorDialog("Player ship can only be added to the start event!");
                return;
            }
            for (LevelObject levelObject : contentModel.getLevel().getLevelStart().startObjects) {
                if (levelObject.type == LevelObject.LevelObjectType.PLAYER_SHIP) {
                    showErrorDialog("Player ship already added to start event!");
                    return;
                }
            }
        }
        if (meshEventSelection.getValue().equalsIgnoreCase(EVENT_START)) {
            contentModel.getLevel().getLevelStart().startObjects.add(mesh);
        } else {
            contentModel.getLevel().levelEventList.stream().filter(levelEvent ->
                            levelEvent.name.equalsIgnoreCase(meshEventSelection.getValue()))
                    .forEach(levelEvent -> levelEvent.spawn.add(mesh));
        }
        selectedMesh.getItems().add(mesh.name);
        eventsMeshSelectionWinCond.getItems().add(mesh.name);
        eventsMeshSelectionLoseCond.getItems().add(mesh.name);
        exitEventMeshSelection.getItems().add(mesh.name);

        MainController.getSingleton().load(mesh);
    }

    @FXML
    public void onGridXZSelected(ActionEvent actionEvent) {
        updateGridPositionAndOrientation();
    }

    @FXML
    public void onAddLoseCond(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        if (!checkCurrentLevelEventValid()) return;
        LevelEndCond levelEndCond = Objects.requireNonNull(getCurrentLevelEvent()).endCond;
        LevelEndCond.EndCond endCond = new LevelEndCond.EndCond();
        endCond.name = "lose_cond" + (loseCondCount++);
        addEventEndCond(endCond, eventsLoseCondTypeCombo, eventsEndCondLossDelay, eventsEndCondLoseObjectiveAchievedDelay, eventsLoseEndCond);
        levelEndCond.lossList.add(endCond);
        if (eventsLoseComparatorCombo.getValue() != null) {
            levelEndCond.lossNode.op = ComparatorOperator.getType(eventsLoseComparatorCombo.getValue());
        }
        eventsLoseCondEventSelected.getItems().add(endCond.name);
    }

    private void addEventEndCond(LevelEndCond.EndCond endCond, ComboBox<String> eventsLoseCondTypeCombo, TextField eventsEndCondLossDelay, TextField eventsEndCondLoseObjectiveAchievedDelay, TextField eventsLoseEndCond) {
        endCond.type = LevelEndCond.EndCondType.getType(eventsLoseCondTypeCombo.getValue());
        if (!eventsEndCondLossDelay.getText().isBlank()) {
            endCond.secs = readInt(eventsEndCondLossDelay);
            endCond.delayType = LevelEvent.DelayType.MSECS;
        }
        if (!eventsEndCondLoseObjectiveAchievedDelay.getText().isBlank()) {
            endCond.objectiveAchievedDelaySecs = readInt(eventsEndCondLoseObjectiveAchievedDelay);
            endCond.objectiveAchievedDelayType = LevelEvent.DelayType.MSECS;
        }
        if (endCond.objects == null) {
            endCond.objects = new ArrayList<>();
        }
        endCond.objects.addAll(Arrays.asList(eventsLoseEndCond.getText().trim().split(" ")));
    }

    @FXML
    public void onRemoveSector(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        if (contentModel.getCurrentWaypointSector() == null || contentModel.getCurrentWaypointSector().isBlank()) {
            showErrorDialog("Please select a sector!");
            return;
        }
        int sectorId = Integer.parseInt(contentModel.getCurrentWaypointSector());
        waypointsWaypointSectorSelection.getItems().removeIf(s -> sectorId == Integer.parseInt(s));
        contentModel.getLevel().levelStart.waypointSectors.removeIf(w -> w.getId() == sectorId);
        waypointsWaypointSelection.getItems().clear();
        waypointsNextIdSelection.getItems().clear();
    }

    @FXML
    public void onSkyboxSelected(ActionEvent actionEvent) {
    }

    @FXML
    public void onLoseCond(ActionEvent actionEvent) {
    }

    @FXML
    public void onWinComparatorSelected(ActionEvent actionEvent) {
    }

    @FXML
    public void onWinCondEventSelected(ActionEvent actionEvent) {
    }

    @FXML
    public void onPlayerShipRemoved(ActionEvent actionEvent) {
        updateSelectedPlayerShips(false);
    }

    private void updateSelectedPlayerShips(boolean newValue) {
        ObservableList<PlayerShipSelection> selectedItems = playerShipSelectionTable.getSelectionModel().getSelectedItems();
        selectedItems.forEach(selectedItem -> selectedItem.selected.set(newValue));
        playerShipSelectionTable.refresh();
    }

    @FXML
    public void onPlayerShipAdded(ActionEvent actionEvent) {
        updateSelectedPlayerShips(true);
//        String selectedType = playerShipTeamSelection.getValue();
//        if (selectedType != null && !selectedType.isBlank()) {
//            updatePlayerShipSelection(selectedType);
//        }
    }

    @FXML
    public void onRemoveNextId(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        if (contentModel.getCurrentWaypointSector() == null || contentModel.getCurrentWaypointSector().isBlank()) {
            showErrorDialog("Please select a sector");
            return;
        }
        if (contentModel.getCurrentWaypoint() == null || contentModel.getCurrentWaypoint().isBlank()) {
            showErrorDialog("No level waypoint selected!");
            return;
        }
        if (contentModel.getCurrentWaypointNextId() == null || contentModel.getCurrentWaypointNextId().isBlank()) {
            showErrorDialog("No level waypoint next id selected!");
            return;
        }
        int sectorId = Integer.parseInt(contentModel.getCurrentWaypointSector());
        int waypointId = Integer.parseInt(contentModel.getCurrentWaypoint());
        int nextId = Integer.parseInt(contentModel.getCurrentWaypointNextId());
        contentModel.getLevel().levelStart.waypointSectors.stream()
                .filter(levelWaypointSector -> levelWaypointSector.getId() == sectorId)
                .findFirst().flatMap(levelWaypointSector -> levelWaypointSector.getWaypoints().stream()
                        .filter(levelWaypoint -> levelWaypoint.getId() == waypointId)
                        .findFirst()).ifPresent(levelWaypoint -> {
                    if (levelWaypoint.nextIds.removeIf(integer -> integer == nextId)) {
                        Collections.sort(levelWaypoint.nextIds);
                        waypointsNextIdSelection.getItems().removeIf(s -> s.equals(contentModel.getCurrentWaypointNextId()));
                    }
                });
    }

    @FXML
    public void onAttachToGrid(ActionEvent actionEvent) {
        updateGridAttached();
    }

    private void updateGridAttached() {
        gridAttached = meshGridAttached.isSelected();
    }

    @FXML
    public void onMeshBehaviorSelection(ActionEvent actionEvent) {
    }

    @FXML
    public void onPlayerShipTeamSelection(ActionEvent actionEvent) {
    }

    @FXML
    public void onLoseComparatorSelected(ActionEvent actionEvent) {
    }

    @FXML
    public void onCreateEvent(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        LevelEvent levelEvent = new LevelEvent();
        levelEvent.name = getText(eventsEventTitle, "Missing title");
        if (!eventsPreviousEndCond.getText().isBlank()) {
            levelEvent.prevCondList.addAll(Arrays.asList(eventsPreviousEndCond.getText().trim().split(" ")));
        }
        levelEvent.delayType = LevelEvent.DelayType.MSECS;
        levelEvent.endCond = new LevelEndCond();
        levelEvent.endCond.name = levelEvent.name + "_end";
        contentModel.getLevel().levelEventList.add(levelEvent);
        eventsSelectionComboBox.getItems().add(levelEvent.name);
        eventsSelectionComboBox.getSelectionModel().selectLast();
        meshEventSelection.getItems().add(levelEvent.name);
    }

    @FXML
    public void onResetLevel(ActionEvent actionEvent) {
        if (showConfirmationDialog("Confirm level reset")) {
            contentModel.setLevel(null);
            reset();
        }
    }

    @FXML
    public void onMeshSelection(ActionEvent actionEvent) {
    }

    @FXML
    public void onPlayerShipInvincibilityEnabled(ActionEvent actionEvent) {
    }

    @FXML
    public void onCreateWaypointTable(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        WaypointSystem waypointSystem = WaypointSystem.getSingleton();
        waypointSystem.removeAllWaypointSectors();
        Level level = contentModel.getLevel();
        WaypointSystem.createWaypoints(level);
        level.levelStart.waypointSectors.forEach(levelWaypointSector -> {
            levelWaypointSector.waypointTable = waypointSystem.getWaypointSector(levelWaypointSector.getId()).getWaypointTable().getTable();
        });
        level.levelStart.waypointSectors.forEach(levelWaypointSector -> {
            int[][] waypointTable = levelWaypointSector.waypointTable;
            for (int i = 0, waypointTableLength = waypointTable.length; i < waypointTableLength; i++) {
                int[] rows = waypointTable[i];
                levelWaypointSector.waypointTableList.add(new ArrayList<>());
                for (int column : rows) {
                    levelWaypointSector.waypointTableList.get(i).add(column);
                }
            }
        });
    }

    @FXML
    public void onRemoveLoseCond(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        if (!checkCurrentLevelEventValid()) return;
        removeEventEndCond(Objects.requireNonNull(getCurrentLevelEvent()).endCond.lossList, eventsLoseCondEventSelected);
    }

    private void removeEventEndCond(ArrayList<LevelEndCond.EndCond> endCond, ComboBox<String> eventsLoseCondEventSelected) {
        int pos = -1;
        for (int i = 0, winListSize = endCond.size(); i < winListSize; i++) {
            LevelEndCond.EndCond currentEndCond = endCond.get(i);
            if (currentEndCond.name.equalsIgnoreCase(eventsLoseCondEventSelected.getValue())) {
                pos = i;
                break;
            }
        }
        if (pos == -1) {
            throw new IllegalStateException("could not find: " + eventsLoseCondEventSelected.getValue());
        }
        endCond.remove(pos);
        eventsLoseCondEventSelected.getItems().remove(pos);
    }

    @FXML
    public void onWinCond(ActionEvent actionEvent) {
    }

    @FXML
    public void onLoseCondEventSelected(ActionEvent actionEvent) {
    }

    @FXML
    public void onPlayerShipAIEnabled(ActionEvent actionEvent) {
    }

    @FXML
    public void onAddWinCond(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        if (!checkCurrentLevelEventValid()) return;
        LevelEndCond levelEndCond = Objects.requireNonNull(getCurrentLevelEvent()).endCond;
        LevelEndCond.EndCond endCond = new LevelEndCond.EndCond();
        endCond.name = "win_cond" + (winCondCount++);
        addEventEndCond(endCond, eventsWinCondTypeCombo, eventsEndCondWinDelay, eventsEndCondWinObjectiveAchievedDelay, eventsWinEndCond);
        levelEndCond.winList.add(endCond);
        if (eventsWinComparatorCombo.getValue() != null) {
            levelEndCond.winNode.op = ComparatorOperator.getType(eventsWinComparatorCombo.getValue());
        }
        eventsWinCondEventSelected.getItems().add(endCond.name);
    }

    @FXML
    public void onWinCondTypeCombo(ActionEvent actionEvent) {
    }

    @FXML
    public void onWaypointGridAttached(ActionEvent actionEvent) {
    }

    @FXML
    public void onNextIdAdd(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        if (waypointsNextId.getText().isBlank()) {
            showErrorDialog("Please enter a valid waypoint next id");
            return;
        }
        if (contentModel.getCurrentWaypointSector() == null) {
            showErrorDialog("Please select a sector");
            return;
        }
        if (contentModel.getCurrentWaypoint() == null) {
            showErrorDialog("No level waypoint selected!");
            return;
        }
        int sectorId = Integer.parseInt(contentModel.getCurrentWaypointSector());
        int waypointId = Integer.parseInt(contentModel.getCurrentWaypoint());
        int nextId = Integer.parseInt(waypointsNextId.getText());
        if (nextId < 1) {
            showErrorDialog("Please enter a valid waypoint next id");
            return;
        }
        contentModel.getLevel().levelStart.waypointSectors.stream()
                .filter(levelWaypointSector -> levelWaypointSector.getId() == sectorId)
                .findFirst().flatMap(levelWaypointSector -> levelWaypointSector.getWaypoints().stream()
                        .filter(levelWaypoint -> levelWaypoint.getId() == waypointId)
                        .findFirst()).ifPresent(levelWaypoint -> {
                    if (!levelWaypoint.nextIds.contains(nextId)) {
                        levelWaypoint.nextIds.add(nextId);
                        Collections.sort(levelWaypoint.nextIds);
                        waypointsNextIdSelection.getItems().add(waypointsNextId.getText());
                    }
                });
    }

    @FXML
    public void onWaypointAdd(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        String currentWaypointSector = contentModel.getCurrentWaypointSector();
        if (currentWaypointSector == null || currentWaypointSector.isBlank()) {
            showErrorDialog("Please select a sector");
            return;
        }
        LevelWaypoint levelWaypoint = new LevelWaypoint();
        int sectorId = Integer.parseInt(currentWaypointSector);
        contentModel.getLevel().levelStart.waypointSectors.stream()
                .filter(levelWaypointSector -> levelWaypointSector.getId() == sectorId)
                .findFirst().ifPresent(levelWaypointSector -> {
                    levelWaypointSector.waypoints.add(levelWaypoint);
                    levelWaypoint.id = levelWaypointSector.nextWaypointId++;
                });
        waypointsWaypointSelection.getItems().add(String.valueOf(levelWaypoint.id));
    }

    private void updateWaypointNextIds(int sectorId, int waypointId) {
        waypointsNextIdSelection.getItems().clear();
        contentModel.getLevel().levelStart.waypointSectors.stream()
                .filter(levelWaypointSector -> levelWaypointSector.getId() == sectorId)
                .findFirst().flatMap(levelWaypointSector -> levelWaypointSector.getWaypoints().stream()
                        .filter(waypoint -> waypoint.getId() == waypointId)
                        .findFirst()).ifPresent(waypoint -> waypoint.nextIds.forEach(nextId -> {
                    waypointsNextIdSelection.getItems().add(String.valueOf(nextId));
                }));
    }

    private void updateWaypoints(int sectorId) {
        waypointsWaypointSelection.getItems().clear();
        contentModel.getLevel().levelStart.waypointSectors.stream()
                .filter(levelWaypointSector -> levelWaypointSector.getId() == sectorId)
                .findFirst().ifPresent(levelWaypointSector -> levelWaypointSector.getWaypoints()
                        .forEach(levelWaypoint -> waypointsWaypointSelection.getItems().add(String.valueOf(levelWaypoint.getId()))));
    }

    public void showWaypoints() {
        if (!checkLevelCreated(false)) return;
        contentModel.getLevel().getLevelStart().waypointSectors
                .forEach(waypointSector -> waypointSector.getWaypoints()
                        .forEach(waypoint -> showWaypoint(waypointSector.getId(), waypoint.getId(), waypoint.getPosition())));
    }

    private void showWaypoint(int waypointSectorId, int waypointId, ENG_Vector4D position) {
        if (!checkLevelCreated()) return;
        if (!waypointsVisibilityCheckBox.isSelected()) return;

        LevelObject levelObject = new LevelObject();
        levelObject.meshName = getMeshName("flag_red");
        levelObject.name = "waypoint_" + waypointSectorId + "_" + waypointId;
        levelObject.type = LevelObject.LevelObjectType.WAYPOINT;
        levelObject.position.set(position);
        levelObject.waypointSectorId = waypointSectorId;
        levelObject.waypointId = waypointId;
        contentModel.getLevel().getLevelStart().waypointObjects.add(levelObject);
        MainController.getSingleton().load(levelObject);
    }

    @FXML
    public void onLoseCondTypeCombo(ActionEvent actionEvent) {
    }

    @FXML
    public void onWaypointSectorSelected(ActionEvent actionEvent) {

    }

    @FXML
    public void onNextIdSelected(ActionEvent actionEvent) {
    }

    @FXML
    public void onUndoCreateEvent(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        if (!checkCurrentLevelEventValid()) return;
        boolean remove = contentModel.getLevel().levelEventList.remove(getCurrentLevelEvent());
        if (!remove) {
            showErrorDialog("Current event: " + Objects.requireNonNull(getCurrentLevelEvent()).name + " could not be deleted!");
            return;
        }
        updateEventSelection();
    }

    private boolean checkCurrentLevelEventValid() {
        if (getCurrentLevelEvent() == null) {
            showErrorDialog("No level event selected!");
            return false;
        }
        return true;
    }

    @FXML
    public void onRemoveWaypoint(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        if (contentModel.getCurrentWaypointSector() == null || contentModel.getCurrentWaypointSector().isBlank()) {
            showErrorDialog("Please select a sector");
            return;
        }
        if (contentModel.getCurrentWaypoint() == null || contentModel.getCurrentWaypoint().isBlank()) {
            showErrorDialog("No level waypoint selected!");
            return;
        }
        int sectorId = Integer.parseInt(contentModel.getCurrentWaypointSector());
        int waypointId = Integer.parseInt(contentModel.getCurrentWaypoint());
        waypointsWaypointSelection.getItems().removeIf(s -> waypointId == Integer.parseInt(s));
        contentModel.getLevel().levelStart.waypointSectors.stream().filter(levelWaypointSector -> levelWaypointSector.getId() == sectorId)
                .findFirst().ifPresent(levelWaypointSector -> {
                    levelWaypointSector.waypoints.removeIf(levelWaypoint -> levelWaypoint.getId() == waypointId);
                    // We also need to compact the waypoint ids on removal so there are no gaps between waypoint ids.
                    // This is needed in order to not have gaps in the waypoint table.
                    removeNextIds(levelWaypointSector, waypointId);
                    --levelWaypointSector.nextWaypointId;
                    if (levelWaypointSector.waypoints.size() != waypointId - 1) {
                        // If this wasn't the last waypoint in the list we need to close the gap.
                        levelWaypointSector.waypoints.stream()
                                .filter(levelWaypoint -> levelWaypoint.getId() > waypointId)
                                .forEach(levelWaypoint -> {
                                    // Lower all the waypoints next ids pointing to this one.
                                    levelWaypointSector.waypoints.forEach(levelWaypoint1 -> {
                                        // This is already handled in removeNextIds() above.
//                                        levelWaypoint1.nextIds.removeIf(nextId -> nextId == waypointId);
                                        if (levelWaypoint1.nextIds.removeIf(nextId -> nextId == levelWaypoint.getId())) {
                                            levelWaypoint1.nextIds.add(levelWaypoint.getId() - 1);
                                            Collections.sort(levelWaypoint1.nextIds);
                                        }
                                    });
                                    --levelWaypoint.id;
                                });
                    } else {
                        // We have deleted the last waypoint added.

                    }
                });

        hideWaypoint(sectorId, waypointId);
        waypointsNextIdSelection.getItems().clear();
        updateWaypoints(sectorId);
    }

    private static void removeNextIds(LevelWaypointSector levelWaypointSector, int waypointId) {
        levelWaypointSector.waypoints.forEach(levelWaypoint -> levelWaypoint.nextIds.removeIf(id -> id == waypointId));
    }

    private void hideWaypoint(int sectorId, int waypointId) {
        if (!checkLevelCreated()) return;
        contentModel.getLevel().getLevelStart().waypointObjects.stream()
                .filter(levelObject -> levelObject.name.equals("waypoint_" + sectorId + "_" + waypointId))
                .findFirst().ifPresent(levelObject -> contentModel.removeCurrentIntersectedNode(levelObject.nodeData.getNode(), true));
    }

    private void updateWaypointsVisibility() {
        if (currentWaypointsVisibility != waypointsVisibilityCheckBox.isSelected()) {
            if (currentWaypointsVisibility) {
                hideWaypoints();
            } else {
                showWaypoints();
            }
            currentWaypointsVisibility = waypointsVisibilityCheckBox.isSelected();
        }
    }

    private void hideWaypoints() {
        if (!checkLevelCreated(false)) return;
        contentModel.getLevel().getLevelStart().waypointObjects
                .forEach(levelObject -> contentModel.removeCurrentIntersectedNode(levelObject.nodeData.getNode(), true));
        contentModel.getLevel().getLevelStart().waypointObjects.clear();
    }

    @FXML
    public void onNewLevel(ActionEvent actionEvent) {
        if (levelTitle.getText().isBlank()) {
            showErrorDialog("Please enter a level title");
            return;
        }
        if (contentModel.getLevel() != null) {
            showErrorDialog("Please reset the level to create a new level");
            return;
        }
        Level level = new Level();
        level.name = levelTitle.getText();
        level.levelStart = new LevelStart();
        level.levelEnd = new LevelEnd();
//        LevelEvent levelEvent = new LevelEvent();
//        levelEvent.name = EVENT_START;
//        level.levelEventList.add(levelEvent);
        contentModel.removeAllEvents();
        contentModel.addEvent(EVENT_START);
        updateEventSelection();
        contentModel.setLevel(level);
    }

    private void updateEventSelection() {
        eventsSelectionComboBox.setItems(FXCollections.observableArrayList(contentModel.getEvents()));
        meshEventSelection.setItems(FXCollections.observableArrayList(contentModel.getEvents()));
    }

    @FXML
    public void onMeshDestinationEnabled(ActionEvent actionEvent) {
    }

    @FXML
    public void onShipTypeSelection(ActionEvent actionEvent) {
    }

    @FXML
    public void onRemoveWinCond(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        if (!checkCurrentLevelEventValid()) return;
        removeEventEndCond(Objects.requireNonNull(getCurrentLevelEvent()).endCond.winList, eventsWinCondEventSelected);
    }

    @FXML
    public void onWaypointSectorAdd(ActionEvent actionEvent) {
        if (!checkLevelCreated()) return;
        if (waypointsWaypointsSectorXMin.getText().isBlank() ||
                waypointsWaypointsSectorYMin.getText().isBlank() ||
                waypointsWaypointsSectorZMin.getText().isBlank() ||
                waypointsWaypointsSectorXMax1.getText().isBlank() ||
                waypointsWaypointsSectorYMax1.getText().isBlank() ||
                waypointsWaypointsSectorZMax1.getText().isBlank()) {
            showErrorDialog("Please enter valid extents for waypoint sector!");
            return;
        }
        LevelWaypointSector waypointSector = new LevelWaypointSector();
        waypointSector.id = waypointSectorNextId++;
        waypointSector.box.setExtents(readVector3D(waypointsWaypointsSectorXMin, waypointsWaypointsSectorYMin, waypointsWaypointsSectorZMin),
                readVector3D(waypointsWaypointsSectorXMax1, waypointsWaypointsSectorYMax1, waypointsWaypointsSectorZMax1));
        waypointSector.maxTotalWaypointAttachmentCount = readInt(waypointWaypointsSectorMaxUsers);
        // Make sure the sector doesn't overlap any other sector.
        final boolean[] overlap = {false};
        contentModel.getLevel().levelStart.waypointSectors.forEach(levelWaypointSector -> {
            if (levelWaypointSector.getBox().intersects(waypointSector.getBox())) {
                showErrorDialog("waypoint sector overlaps another sector with extents: " + levelWaypointSector.getBox());
                overlap[0] = true;
            }
        });
        if (!overlap[0]) {
            contentModel.getLevel().levelStart.waypointSectors.add(waypointSector);
            waypointsWaypointSectorSelection.getItems().add(String.valueOf(waypointSector.id));
        } else {
            --waypointSectorNextId;
        }
    }

    @FXML
    public void onGridYZSelected(ActionEvent actionEvent) {
        updateGridPositionAndOrientation();
    }

    @FXML
    public void onMeshEventSelection(ActionEvent actionEvent) {
    }

    @FXML
    public void onUndoAddSelected(ActionEvent actionEvent) {
    }

    @FXML
    public void onEventSelected(ActionEvent actionEvent) {
//        if (!checkLevelCreated()) return;
//        if (!eventsSelectionComboBox.getValue().isBlank()) {
//            contentModel.setCurrentSelectedEvent(eventsSelectionComboBox.getValue());
//        }
    }

    @FXML
    public void onLightTypeSelected(ActionEvent actionEvent) {
    }

    @FXML
    public void onAddSelected(ActionEvent actionEvent) {
    }

    private static class Power10DoubleBinding extends DoubleBinding {

        private final DoubleProperty prop;

        public Power10DoubleBinding(DoubleProperty prop) {
            this.prop = prop;
            bind(prop);
        }

        @Override
        protected double computeValue() {
            return Math.pow(10, prop.getValue());
        }
    }

    private class TreeItemImpl extends TreeItem<Node> {

        public TreeItemImpl(Node node) {
            super(node);
            if (node instanceof Parent) {
                for (Node n : ((Parent) node).getChildrenUnmodifiable()) {
                    getChildren().add(new TreeItemImpl(n));
                }
            }
            node.setOnMouseClicked(t -> {
                TreeItem<Node> parent = getParent();
                while (parent != null) {
                    parent.setExpanded(true);
                    parent = parent.getParent();
                }
                hierarachyTreeTable.getSelectionModel().select(TreeItemImpl.this);
                hierarachyTreeTable.scrollTo(hierarachyTreeTable.getSelectionModel().getSelectedIndex());
                t.consume();
            });
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settingsController = this;
        // keep one pane open always
        settings.expandedPaneProperty()
                .addListener((observable, oldValue,
                              newValue) -> Platform.runLater(() -> {
                    if (settings.getExpandedPane() == null) {
                        settings.setExpandedPane(settings.getPanes().get(0));
                    }
                }));
        // wire up settings in OPTIONS
        contentModel.msaaProperty().bind(msaaCheckBox.selectedProperty());
        contentModel.showAxisProperty().bind(showAxisCheckBox.selectedProperty());
        contentModel.yUpProperty().bind(yUpCheckBox.selectedProperty());
        backgroundColorPicker.setValue((Color) contentModel.getSubScene().getFill());
        contentModel.getSubScene().fillProperty().bind(backgroundColorPicker.valueProperty());
        wireFrameCheckbox.selectedProperty()
                .addListener((observable, oldValue,
                              isWireframe) -> contentModel.setWireFrame(isWireframe));
        subdivisionLevelGroup.selectedToggleProperty()
                .addListener((observable, oldValue, selectedToggle) -> {
                    if (selectedToggle == null && oldValue != null) {
                        subdivisionLevelGroup.selectToggle(oldValue);
                        selectedToggle = oldValue;
                    } else {
                        assert selectedToggle != null;
                        contentModel.setSubdivisionLevel(Integer.parseInt((String) selectedToggle.getUserData()));
                    }
                });
        asteroidGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null && oldValue != null) {
                subdivisionLevelGroup.selectToggle(oldValue);
            } else {
                assert newValue != null;
                contentModel.setSubdivisionLevel(Integer.parseInt((String) newValue.getUserData()));
            }
        }));
        modifiersGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            contentModel.setActiveModifierType((ToggleButton) newValue);
        }));
        subdivisionBoundaryGroup.selectedToggleProperty()
                .addListener((observable, oldValue, selectedToggle) -> {
                    if (selectedToggle == null && oldValue != null) {
                        subdivisionBoundaryGroup.selectToggle(oldValue);
                        selectedToggle = oldValue;
                    } else {
                        assert selectedToggle != null;
                        contentModel.setBoundaryMode((SubdivisionMesh.BoundaryMode) selectedToggle.getUserData());
                    }
                });
        subdivisionSmoothGroup.selectedToggleProperty()
                .addListener((observable, oldValue, selectedToggle) -> {
                    if (selectedToggle == null && oldValue != null) {
                        subdivisionSmoothGroup.selectToggle(oldValue);
                        selectedToggle = oldValue;
                    } else {
                        assert selectedToggle != null;
                        contentModel.setMapBorderMode((SubdivisionMesh.MapBorderMode) selectedToggle.getUserData());
                    }
                });
        // wire up settings in LIGHTS
        ambientEnableCheckbox.setSelected(contentModel.getAmbientLightEnabled());
        contentModel.ambientLightEnabledProperty().bind(ambientEnableCheckbox.selectedProperty());
        ambientColorPicker.setValue(contentModel.getAmbientLight().getColor());
        contentModel.getAmbientLight().colorProperty().bind(ambientColorPicker.valueProperty());

        // LIGHT 1
        light1EnabledCheckBox.setSelected(contentModel.getLight1Enabled());
        contentModel.light1EnabledProperty().bind(light1EnabledCheckBox.selectedProperty());
        light1ColorPicker.setValue(contentModel.getLight1().getColor());
        contentModel.getLight1().colorProperty().bind(light1ColorPicker.valueProperty());
        light1x.disableProperty().bind(light1followCameraCheckBox.selectedProperty());
        light1y.disableProperty().bind(light1followCameraCheckBox.selectedProperty());
        light1z.disableProperty().bind(light1followCameraCheckBox.selectedProperty());
        light1followCameraCheckBox.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        contentModel.getLight1()
                                .translateXProperty()
                                .bind(new DoubleBinding() {
                                    {
                                        bind(contentModel.getCamera().boundsInParentProperty());
                                    }

                                    @Override
                                    protected double computeValue() {
                                        return contentModel.getCamera().getBoundsInParent().getMinX();
                                    }
                                });
                        contentModel.getLight1()
                                .translateYProperty()
                                .bind(new DoubleBinding() {
                                    {
                                        bind(contentModel.getCamera().boundsInParentProperty());
                                    }

                                    @Override
                                    protected double computeValue() {
                                        return contentModel.getCamera().getBoundsInParent().getMinY();
                                    }
                                });
                        contentModel.getLight1()
                                .translateZProperty()
                                .bind(new DoubleBinding() {
                                    {
                                        bind(contentModel.getCamera().boundsInParentProperty());
                                    }

                                    @Override
                                    protected double computeValue() {
                                        return contentModel.getCamera().getBoundsInParent().getMinZ();
                                    }
                                });
                    } else {
                        contentModel.getLight1().translateXProperty().bind(light1x.valueProperty());
                        contentModel.getLight1().translateYProperty().bind(light1y.valueProperty());
                        contentModel.getLight1().translateZProperty().bind(light1z.valueProperty());
                    }
                });
        // LIGHT 2
        light2EnabledCheckBox.setSelected(contentModel.getLight2Enabled());
        contentModel.light2EnabledProperty().bind(light2EnabledCheckBox.selectedProperty());
        light2ColorPicker.setValue(contentModel.getLight2().getColor());
        contentModel.getLight2().colorProperty().bind(light2ColorPicker.valueProperty());
        contentModel.getLight2().translateXProperty().bind(light2x.valueProperty());
        contentModel.getLight2().translateYProperty().bind(light2y.valueProperty());
        contentModel.getLight2().translateZProperty().bind(light2z.valueProperty());
        // LIGHT 3
        light3EnabledCheckBox.setSelected(contentModel.getLight3Enabled());
        contentModel.light3EnabledProperty().bind(light3EnabledCheckBox.selectedProperty());
        light3ColorPicker.setValue(contentModel.getLight3().getColor());
        contentModel.getLight3().colorProperty().bind(light3ColorPicker.valueProperty());
        contentModel.getLight3().translateXProperty().bind(light3x.valueProperty());
        contentModel.getLight3().translateYProperty().bind(light3y.valueProperty());
        contentModel.getLight3().translateZProperty().bind(light3z.valueProperty());
        // wire up settings in CAMERA
        fovSlider.setValue(contentModel.getCamera().getFieldOfView());
        contentModel.getCamera().fieldOfViewProperty().bind(fovSlider.valueProperty());
        contentModel.getCamera().fieldOfViewProperty().addListener(((observable, oldValue, newValue) -> System.out.println("new camera fov: " + newValue)));
        Platform.runLater(() -> fovSlider.setValue(FOV_DEG));

        nearClipSlider.setValue(Math.log10(contentModel.getCamera().getNearClip()));
        farClipSlider.setValue(Math.log10(contentModel.getCamera().getFarClip()));
        nearClipLabel.textProperty().bind(Bindings.format(nearClipLabel.getText(), contentModel.getCamera().nearClipProperty()));
        farClipLabel.textProperty().bind(Bindings.format(farClipLabel.getText(), contentModel.getCamera().farClipProperty()));
        contentModel.getCamera().nearClipProperty().bind(new Power10DoubleBinding(nearClipSlider.valueProperty()));
        contentModel.getCamera().farClipProperty().bind(new Power10DoubleBinding(farClipSlider.valueProperty()));

        hierarachyTreeTable.rootProperty().bind(new ObjectBinding<TreeItem<Node>>() {

                    {
                        bind(contentModel.getContentProperty());
                    }

                    @Override
                    protected TreeItem<Node> computeValue() {
                        Node content3D = contentModel.getContent();
                        if (content3D != null) {
                            return new TreeItemImpl(content3D);
                        } else {
                            return null;
                        }
                    }
                });
        hierarachyTreeTable.setOnMouseClicked(t -> {
            if (t.getClickCount() == 2) {
                settings.setExpandedPane(x6);
                t.consume();
            }
        });
        hierarachyTreeTable.setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.SPACE) {
                TreeItem<Node> selectedItem = hierarachyTreeTable.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    Node node = selectedItem.getValue();
                    node.setVisible(!node.isVisible());
                }
                t.consume();
            }
        });
        x6.expandedProperty().addListener((ov, t, t1) -> {
                    if (t1) {
                        TreeItem<Node> selectedItem = hierarachyTreeTable.getSelectionModel().getSelectedItem();
                        if (selectedItem == null) {
                            transformsList.setItems(null);
                            selectedNodeLabel.setText("");
                        } else {
                            Node node = selectedItem.getValue();
                            transformsList.setItems(node.getTransforms());
                            selectedNodeLabel.setText(node.toString());
                        }
                    }
                });
        nodeColumn.setCellValueFactory(p -> p.getValue().valueProperty().asString());
        idColumn.setCellValueFactory(p -> p.getValue().getValue().idProperty());
        visibilityColumn.setCellValueFactory(p -> p.getValue().getValue().visibleProperty());
        visibilityColumn.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(visibilityColumn));
        widthColumn.setCellValueFactory(p -> new ObjectBinding<Double>() {
            {
                bind(p.getValue().getValue().boundsInLocalProperty());
            }

            @Override
            protected Double computeValue() {
                return p.getValue().getValue().getBoundsInLocal().getWidth();
            }
        });
        StringConverter<Double> niceDoubleStringConverter = new StringConverter<Double>() {
            @Override
            public Double fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //Not needed so far
            }

            @Override
            public String toString(Double t) {
                return String.format("%.2f", t);
            }
        };
        widthColumn.setCellFactory(TextFieldTreeTableCell.<Node, Double>forTreeTableColumn(niceDoubleStringConverter));
        heightColumn.setCellFactory(TextFieldTreeTableCell.<Node, Double>forTreeTableColumn(niceDoubleStringConverter));
        depthColumn.setCellFactory(TextFieldTreeTableCell.<Node, Double>forTreeTableColumn(niceDoubleStringConverter));
        heightColumn.setCellValueFactory(p -> new ObjectBinding<Double>() {
            {
                bind(p.getValue().getValue().boundsInLocalProperty());
            }

            @Override
            protected Double computeValue() {
                return p.getValue().getValue().getBoundsInLocal().getHeight();
            }
        });
        depthColumn.setCellValueFactory(p -> new ObjectBinding<Double>() {
            {
                bind(p.getValue().getValue().boundsInLocalProperty());
            }

            @Override
            protected Double computeValue() {
                return p.getValue().getValue().getBoundsInLocal().getDepth();
            }
        });

        SessionManager sessionManager = SessionManager.getSessionManager();

        sessionManager.bind(showAxisCheckBox.selectedProperty(), "showAxis");
        sessionManager.bind(yUpCheckBox.selectedProperty(), "yUp");
        sessionManager.bind(msaaCheckBox.selectedProperty(), "msaa");
        sessionManager.bind(wireFrameCheckbox.selectedProperty(), "wireFrame");
        sessionManager.bind(backgroundColorPicker.valueProperty(), "backgroundColor");
        sessionManager.bind(fovSlider.valueProperty(), "fieldOfView");
        sessionManager.bind(subdivisionLevelGroup, "subdivisionLevel");
        sessionManager.bind(subdivisionBoundaryGroup, "subdivisionBoundary");
        sessionManager.bind(subdivisionSmoothGroup, "subdivisionSmooth");
        sessionManager.bind(light1ColorPicker.valueProperty(), "light1Color");
        sessionManager.bind(light1EnabledCheckBox.selectedProperty(), "light1Enabled");
        sessionManager.bind(light1followCameraCheckBox.selectedProperty(), "light1FollowCamera");
        sessionManager.bind(light1x.valueProperty(), "light1X");
        sessionManager.bind(light1y.valueProperty(), "light1Y");
        sessionManager.bind(light1z.valueProperty(), "light1Z");
        sessionManager.bind(light2ColorPicker.valueProperty(), "light2Color");
        sessionManager.bind(light2EnabledCheckBox.selectedProperty(), "light2Enabled");
        sessionManager.bind(light2x.valueProperty(), "light2X");
        sessionManager.bind(light2y.valueProperty(), "light2Y");
        sessionManager.bind(light2z.valueProperty(), "light2Z");
        sessionManager.bind(light3ColorPicker.valueProperty(), "light3Color");
        sessionManager.bind(light3EnabledCheckBox.selectedProperty(), "light3Enabled");
        sessionManager.bind(light3x.valueProperty(), "light3X");
        sessionManager.bind(light3y.valueProperty(), "light3Y");
        sessionManager.bind(light3z.valueProperty(), "light3Z");
        sessionManager.bind(ambientColorPicker.valueProperty(), "ambient");
        sessionManager.bind(ambientEnableCheckbox.selectedProperty(), "ambientEnable");
        sessionManager.bind(settings, "settingsPane");

        reset();

        eventsWinComparatorCombo.setItems(FXCollections.observableArrayList(
                ComparatorOperator.AND.toString(),
                ComparatorOperator.OR.toString(),
                ComparatorOperator.XOR.toString(),
                ComparatorOperator.NOT.toString()
        ));
        eventsWinComparatorCombo.valueProperty().addListener(
                (observable, oldValue, newValue) -> contentModel.setComparatorOperatorWin(newValue));

        eventsLoseComparatorCombo.setItems(FXCollections.observableArrayList(
                ComparatorOperator.AND.toString(),
                ComparatorOperator.OR.toString(),
                ComparatorOperator.XOR.toString(),
                ComparatorOperator.NOT.toString()
        ));
        eventsLoseComparatorCombo.valueProperty().addListener(
                (observable, oldValue, newValue) -> contentModel.setComparatorOperatorLose(newValue));

        eventsWinCondTypeCombo.setItems(FXCollections.observableArrayList(
                LevelEndCond.EndCondType.DESTROYED.toString(),
                LevelEndCond.EndCondType.TIME_ELAPSED.toString(),
                LevelEndCond.EndCondType.PLAYER_SHIP_DESTINATION_REACHED.toString(),
                LevelEndCond.EndCondType.EXITED.toString(),
                LevelEndCond.EndCondType.CARGO_SCANNED.toString(),
                LevelEndCond.EndCondType.SHIP_DESTINATION_REACHED.toString(),
                LevelEndCond.EndCondType.EXITED_OR_DESTROYED.toString(),
                LevelEndCond.EndCondType.TEXT_SHOWN.toString()
        ));
        eventsWinCondTypeCombo.valueProperty().addListener(
                (observable, oldValue, newValue) -> contentModel.setWinEndCondType(newValue));

        eventsLoseCondTypeCombo.setItems(FXCollections.observableArrayList(
                LevelEndCond.EndCondType.DESTROYED.toString(),
                LevelEndCond.EndCondType.TIME_ELAPSED.toString(),
                LevelEndCond.EndCondType.PLAYER_SHIP_DESTINATION_REACHED.toString(),
                LevelEndCond.EndCondType.EXITED.toString(),
                LevelEndCond.EndCondType.CARGO_SCANNED.toString(),
                LevelEndCond.EndCondType.SHIP_DESTINATION_REACHED.toString(),
                LevelEndCond.EndCondType.EXITED_OR_DESTROYED.toString(),
                LevelEndCond.EndCondType.TEXT_SHOWN.toString()
        ));
        eventsLoseCondTypeCombo.valueProperty().addListener(
                (observable, oldValue, newValue) -> contentModel.setLoseEndCondType(newValue));

        meshComboBox.setItems(FXCollections.observableList(Jfx3dViewerApp.getShipNames()));

        meshComboBox.valueProperty().addListener(
                (ov, oldValue, newValue) -> contentModel.setSelectedMeshName(newValue));

        playerShipTeamSelection.setItems(FXCollections.observableArrayList(ShipData.ShipTeam.HUMAN.toString(), ShipData.ShipTeam.ALIEN.toString()));
        playerShipTeamSelection.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isBlank()) return;
            updatePlayerShipSelection(newValue);
        });
        playerShipSelectionTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        startEventSkyboxSelection.setItems(FXCollections.observableList(Jfx3dViewerApp.getSkyboxNames()));
        startEventSkyboxSelection.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!checkLevelCreated()) return;
            contentModel.getLevel().levelStart.skyboxName = newValue;
        });

        startEventLightTypeSelection.setItems(FXCollections.observableArrayList(
                ENG_Light.LightTypes.getType(ENG_Light.LightTypes.LT_DIRECTIONAL),
                ENG_Light.LightTypes.getType(ENG_Light.LightTypes.LT_POINT),
                ENG_Light.LightTypes.getType(ENG_Light.LightTypes.LT_SPOTLIGHT)
        ));
        startEventLightTypeSelection.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!checkLevelCreated()) return;
            if (newValue == null || newValue.isBlank()) return;
            contentModel.getLevel().levelStart.lightType = ENG_Light.LightTypes.getType(newValue);
        });

        meshShipTypeSelection.setItems(FXCollections.observableArrayList(
                LevelObject.LevelObjectType.PLAYER_SHIP.toString(),
                LevelObject.LevelObjectType.FIGHTER_SHIP.toString(),
                LevelObject.LevelObjectType.CARGO_SHIP.toString(),
                LevelObject.LevelObjectType.ASTEROID.toString(),
                LevelObject.LevelObjectType.FLAG_RED.toString(),
                LevelObject.LevelObjectType.FLAG_BLUE.toString(),
                LevelObject.LevelObjectType.CARGO.toString(),
//                LevelObject.LevelObjectType.PLAYER_SHIP_SELECTION.toString(),
//                LevelObject.LevelObjectType.WAYPOINT.toString(),
                LevelObject.LevelObjectType.STATIC.toString()
        ));
        meshShipTypeSelection.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!checkLevelCreated()) return;
            if (newValue == null || newValue.isBlank()) return;
//            if (!newValue.equalsIgnoreCase(LevelObject.LevelObjectType.FIGHTER_SHIP.toString())) return;
            contentModel.setCurrentShipType(newValue);
            meshComboBox.getItems().clear();
            LevelObject.LevelObjectType type = LevelObject.LevelObjectType.getLevelObjectType(newValue);
            switch (type) {
                case PLAYER_SHIP, FIGHTER_SHIP -> {
                    meshComboBox.setItems(FXCollections.observableArrayList(Jfx3dViewerApp.getShipNames()));
                }
                case CARGO_SHIP -> {
                }
                case ASTEROID -> {
                    meshComboBox.setItems(FXCollections.observableArrayList(Jfx3dViewerApp.getAsteroidNames()));
                }
                case FLAG_RED -> {
                    meshComboBox.setItems(FXCollections.observableArrayList("flag_red"));
                }
                case FLAG_BLUE -> {
                    meshComboBox.setItems(FXCollections.observableArrayList("flag_blue"));
                }
                case CARGO -> {
                    meshComboBox.setItems(FXCollections.observableArrayList("cargo"));
                }
                case PLAYER_SHIP_SELECTION -> {
                }
                case WAYPOINT -> {
                    meshComboBox.setItems(FXCollections.observableArrayList("waypoint"));
                }
                case STATIC -> {
                    meshComboBox.setItems(FXCollections.observableArrayList(Jfx3dViewerApp.getStaticEntitiesNames()));
                }
                default -> throw new IllegalStateException("Unexpected value: " + type);
            }
        });

        meshComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isBlank()) return;
        });

        eventsSelectionComboBox.setItems(FXCollections.observableArrayList(SettingsController.EVENT_START));
        eventsSelectionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!checkLevelCreated()) return;
            if (newValue == null || newValue.isBlank()) return;
            if (newValue.equalsIgnoreCase(SettingsController.EVENT_START)) return;
            contentModel.setCurrentSelectedEvent(newValue);
        });
        meshEventSelection.setItems(FXCollections.observableArrayList(SettingsController.EVENT_START));

        meshBehaviorSelection.setItems(FXCollections.observableArrayList(
                LevelObject.LevelObjectBehavior.AGGRESSIVE.toString(),
                LevelObject.LevelObjectBehavior.DEFENSIVE.toString(),
                LevelObject.LevelObjectBehavior.NEUTRAL.toString()
        ));
        meshBehaviorSelection.valueProperty().addListener(
                (observable, oldValue, newValue) -> contentModel.setCurrentShipBehavior(newValue));

        eventsMeshSelectionWinCond.valueProperty().addListener(
                (observable, oldValue, newValue) -> contentModel.setCurrentSelectedMeshWinCond(newValue));
        eventsMeshSelectionLoseCond.valueProperty().addListener(
                (observable, oldValue, newValue) -> contentModel.setCurrentSelectedMeshLoseCond(newValue));

        exitEventMeshSelection.valueProperty().addListener(
                (observableValue, oldValue, newValue) -> contentModel.setCurrentExitEventSelectedMesh(newValue));

        waypointsWaypointSectorSelection.valueProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if (newValue == null || newValue.isBlank()) return;
                    contentModel.setCurrentWaypointSector(newValue);
                    if (!checkLevelCreated()) return;
                    updateWaypoints(Integer.parseInt(newValue));
                });
        waypointsWaypointSelection.valueProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if (newValue == null || newValue.isBlank()) return;
                    if (contentModel.getCurrentWaypointSector() == null || contentModel.getCurrentWaypointSector().isBlank()) return;
                    contentModel.setCurrentWaypoint(newValue);
                    if (!checkLevelCreated()) return;
                    updateWaypointNextIds(Integer.parseInt(contentModel.getCurrentWaypointSector()), Integer.parseInt(newValue));
                });
        waypointsNextIdSelection.valueProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if (newValue == null || newValue.isBlank()) return;
                    contentModel.setCurrentWaypointNextId(newValue);
                });

        updateGridStatus();
        updateGridPositionAndOrientation();
        updateGridAttached();

        asteroidGroup.getToggles().forEach(toggle -> asteroidMap.put(Integer.valueOf(((ToggleButton) toggle).getText()), null));

        updateWaypointsVisibility();
        waypointsVisibilityCheckBox.selectedProperty().addListener(
                (observable, oldValue, newValue) -> updateWaypointsVisibility());
    }

    private void updatePlayerShipSelection(String newValue) {
        ArrayList<String> selected = Jfx3dViewerApp.getShipNames().stream()
                .filter(shipName -> shipName.contains(newValue.toLowerCase(Locale.US))).collect(Collectors.toCollection(ArrayList::new));
        playerShipShipSelectionColumn.setCellValueFactory(param -> param.getValue().name);
        playerShipSelectedShipsColumn.setCellValueFactory(param -> param.getValue().selected.get() ? param.getValue().name : new SimpleStringProperty(""));
        ArrayList<PlayerShipSelection> playerShips = selected.stream().map(PlayerShipSelection::new).collect(Collectors.toCollection(ArrayList::new));

        playerShipSelectionTable.setItems(FXCollections.observableArrayList(playerShips));
    }

    public boolean checkLevelCreated() {
        return checkLevelCreated(true);
    }

    public boolean checkLevelCreated(boolean showErrorDialog) {
        if (contentModel.getLevel() == null) {
            if (showErrorDialog) {
                showErrorDialog("Level not created");
            }
            return false;
        }
        return true;
    }

    @FXML
    public void loadAsteroid(ActionEvent event) {
        if (!checkLevelCreated()) return;
        System.out.println("loading asteroid");

        LevelObject levelObject = new LevelObject();
        levelObject.type = LevelObject.LevelObjectType.ASTEROID;
        levelObject.name = generateUniqueName("Asteroid");
        levelObject.meshName = getRandomAsteroidMesh();//Jfx3dViewerApp.BOX_ASTEROID_PATH;//"asteroid" + ENG_Utility.getRandom().nextInt(5);
        levelObject.meshInGameName = FilenameUtils.getBaseName(levelObject.meshName);
        levelObject.position.set(0, 0, 0);
        levelObject.health = 50;
        contentModel.getLevel().getLevelStart().startObjects.add(levelObject);
        MainController.getSingleton().load(levelObject);
    }

    private static String getRandomAsteroidMesh() {
        return getMeshName(Jfx3dViewerApp.getAsteroidNames().get(ENG_Utility.getRandom().nextInt(Jfx3dViewerApp.getAsteroidNames().size())));
    }

    @FXML
    public void createAsteroidCluster(ActionEvent event) {
        if (!checkLevelCreated()) return;
        System.out.println("creating asteroid cluster");

        int asteroidNum = Integer.parseInt(asteroidCount.getText());
        ToggleButton selectedToggle = (ToggleButton) asteroidGroup.getSelectedToggle();
        int asteroidGroupPosition = Integer.parseInt(selectedToggle.getText());
        ArrayList<LevelObject> levelObjects = asteroidMap.get(asteroidGroupPosition);
        if (levelObjects == null) {
            levelObjects = new ArrayList<>();
            asteroidMap.put(asteroidGroupPosition, levelObjects);
        } else {
            removeAsteroidCluster(null);
        }
        for (int i = 0; i < asteroidNum; ++i) {
            LevelObject levelObject = new LevelObject();
            levelObject.type = LevelObject.LevelObjectType.ASTEROID;
            levelObject.name = generateUniqueName("Asteroid");
            levelObject.meshName = getRandomAsteroidMesh();//"asteroid" + ENG_Utility.getRandom().nextInt(5);
            levelObject.meshInGameName = FilenameUtils.getBaseName(levelObject.meshName);
            if (asteroidUseRadiusCheckBox.isSelected()) {
                ENG_Vector3D radiusCentre = readVector3D(asteroidRadiusCentreX, asteroidRadiusCentreY, asteroidRadiusCentreZ);
                float radius = readFloat(asteroidRadius);
                float x = (float) (ENG_Utility.getRandom().nextFloat(radius * 2) - radius + radiusCentre.x);
                float y = (float) (ENG_Utility.getRandom().nextFloat(radius * 2) - radius + radiusCentre.y);
                float z = (float) (ENG_Utility.getRandom().nextFloat(radius * 2) - radius + radiusCentre.z);
                levelObject.position.set(x, y, z);
            } else {
                ENG_AxisAlignedBox box = getAsteroidClusterAxisAlignedBox();
                float x = randomPointOnAxis(box.getMin().x, box.getMax().x);
                float y = randomPointOnAxis(box.getMin().y, box.getMax().y);
                float z = randomPointOnAxis(box.getMin().z, box.getMax().z);
                levelObject.position.set(x, y, z);
            }
//            System.out.println("asteroid: " + levelObject.meshName + " position: " + levelObject.position);
            int nextInt = ENG_Utility.getRandom().nextInt(3);
            ENG_Vector4D axis;
            switch (nextInt) {
                case 0 -> axis = ENG_Math.VEC4_X_UNIT;
                case 1 -> axis = ENG_Math.VEC4_Y_UNIT;
                case 2 -> axis = ENG_Math.VEC4_Z_UNIT;
                default -> throw new IllegalArgumentException();
            }
//            levelObject.orientation.fromAngleAxisRad(ENG_Utility.rangeRandom( 0.0f, (float) ENG_Math.TWO_PI), axis);
            levelObject.health = 50;
            contentModel.getLevel().getLevelStart().startObjects.add(levelObject);
            MainController.getSingleton().load(levelObject);
            levelObjects.add(levelObject);
        }
    }

    private ENG_AxisAlignedBox getAsteroidClusterAxisAlignedBox() {
        return new ENG_AxisAlignedBox(readVector3D(asteroidClusterXMin, asteroidClusterYMin, asteroidClusterZMin),
                readVector3D(asteroidClusterXMax, asteroidClusterYMax, asteroidClusterZMax));
    }

    public void readLevelStart() {
        if (!checkLevelCreated()) return;
        LevelStart levelStart = contentModel.getLevel().levelStart;
        levelStart.lightDir.set(readVector4DAsVec(startEventLightDirX, startEventLightDirY, startEventLightDirZ));
        levelStart.lightPowerScale = readFloat(startEventLightPowerScale);
        levelStart.lightDiffuseColor.set(readColorValue(startEventLightDiffuseColorX, startEventLightDiffuseColorY, startEventLightDiffuseColorZ, startEventLightDiffuseColorW));
        levelStart.lightSpecularColor.set(readColorValue(startEventLightSpecularColorX1, startEventLightSpecularColorY1, startEventLightSpecularColorZ1, startEventLightSpecularColorW));
        levelStart.ambientLightUpperHemisphere.set(readColorValue(startEventAmbientUpperHemiR, startEventAmbientUpperHemiG, startEventAmbientUpperHemiB, startEventAmbientUpperHemiA));
        levelStart.ambientLightLowerHemisphere.set(readColorValue(startEventAmbientLowerHemiR1, startEventAmbientLowerHemiG1, startEventAmbientLowerHemiB1, startEventAmbientLowerHemiA1));
        levelStart.ambientLighthemisphereDir.set(readVector4DAsVec(startEventHemiDirX, startEventHemiDirY, startEventHemiDirZ));
    }

    public void readPlayerShip() {
        if (!checkLevelCreated()) return;
        LevelStart levelStart = contentModel.getLevel().levelStart;
        // This type has been added just so we can differentiate in the load resources in WorldManagerBase.
        playerShipSelectionTable.getSelectionModel().getSelectedItems().forEach(selectedItem -> {
            LevelObject levelObject = new LevelObject();
            levelObject.name = selectedItem.name.getName();
            levelObject.type = LevelObject.LevelObjectType.PLAYER_SHIP_SELECTION;
            levelStart.playerShipSelectionObjects.add(levelObject);
        });

    }

    public void readEventWinCond() {
        if (!checkLevelCreated()) return;
        if (!checkCurrentLevelEventValid()) return;
    }

    public void readEventLossCond() {
        if (!checkLevelCreated()) return;
        if (!checkCurrentLevelEventValid()) return;
    }

    public void readEvent() {
        if (!checkLevelCreated()) return;
        if (!checkCurrentLevelEventValid()) return;
        LevelEvent levelEvent = getCurrentLevelEvent();
        assert levelEvent != null;
        levelEvent.name = getText(eventsEventTitle, "Missing title");
        if (!eventsPreviousEndCond.getText().isBlank()) {
            levelEvent.prevCondList.addAll(Arrays.asList(eventsPreviousEndCond.getText().trim().split(" ")));
        }
        levelEvent.delay = readInt(eventsDelay);
        levelEvent.delayType = LevelEvent.DelayType.MSECS;
    }

    public LevelObject readMesh() {
        LevelObject levelObject = new LevelObject();
        if (meshShipTypeSelection.getValue() == null) {
            throw new IllegalArgumentException("No mesh type selected");
        }
        levelObject.type = LevelObject.LevelObjectType.getLevelObjectType(meshShipTypeSelection.getValue());
        levelObject.name = getText(meshShipName);
        levelObject.meshName = getMeshName(meshComboBox.getValue());
        levelObject.meshInGameName = meshComboBox.getValue();
        levelObject.position.set(readVector4DAsVec(meshXPosText, meshYPosText, meshZPosText));
        if (meshGridAttached.isSelected()) {
            levelObject.position.addInPlace(readVector4DAsPt(gridXPos, gridYPos, gridZPos));
        }
        levelObject.health = readInt(meshHealth);
        levelObject.orientation.set(readQuaternion(meshXRotText, meshYRotText, meshZRotText, meshRotAngleText));
        levelObject.radius = meshRadius.getText().isBlank() ? 0.0f : readFloat(meshRadius);
        levelObject.scanRadius = meshScanRadius.getText().isBlank() ? 0.0f : readFloat(meshScanRadius);
        levelObject.ai = meshAIEnabled.isSelected();
        levelObject.aiAsInt = levelObject.ai ? 1 : 0;
        levelObject.invincible = meshInvincibilityEnabled.isSelected();
        levelObject.friendly = meshFriendlyEnabled.isSelected() ? ShipData.ShipTeam.HUMAN : ShipData.ShipTeam.ALIEN;
        levelObject.friendlyAsInt = meshFriendlyEnabled.isSelected() ? 1 : 0;
        if (!meshPrioritize.getText().isBlank()) {
            levelObject.prioritizeList.addAll(Arrays.asList(meshPrioritize.getText().trim().split(" ")));
        }
        levelObject.attackName = meshAttack.getText().isBlank() ? null : meshAttack.getText();
        if (meshDestinationEnabled.isSelected()) {
            levelObject.reachDestination = true;
            levelObject.destination.set(readVector4DAsPt(meshDestinationXPos, meshDestinationYPos, meshDestinationZPos));
        }
        if (meshBehaviorSelection.getValue() != null) {
            levelObject.behavior = meshBehaviorSelection.getValue().isBlank() ? null : LevelObject.LevelObjectBehavior.getBehavior(meshBehaviorSelection.getValue());
        }
        return levelObject;
    }

    public void readPlayerShipSelection() {
        if (!checkLevelCreated()) return;
        LevelStart levelStart = contentModel.getLevel().levelStart;
        if (playerShipTeamSelection.getValue() == null) {
            showErrorDialog("No player ship team selected");
            return;
        }
        ShipData.ShipTeam shipTeam = ShipData.ShipTeam.getValueOf(playerShipTeamSelection.getValue());
        LevelPlayerShipSelection alienPlayerShipSelection = new LevelPlayerShipSelection();
        alienPlayerShipSelection.team = ShipData.ShipTeam.ALIEN;
        LevelPlayerShipSelection humanPlayerShipSelection = new LevelPlayerShipSelection();
        humanPlayerShipSelection.team = ShipData.ShipTeam.HUMAN;
        ObservableList<PlayerShipSelection> selectedItems = playerShipSelectionTable.getSelectionModel().getSelectedItems();
        selectedItems.stream().filter(selectedItem -> selectedItem.selected.get())
                .forEach(selectedItem -> {
            if (shipTeam == ShipData.ShipTeam.ALIEN) {
                alienPlayerShipSelection.shipNameList.add(selectedItem.name.getValue());
            } else if (shipTeam == ShipData.ShipTeam.HUMAN) {
                humanPlayerShipSelection.shipNameList.add(selectedItem.name.getValue());
            }
        });
        levelStart.alienPlayerShipSelectionList.clear();
        levelStart.humanPlayerShipSelectionList.clear();
        levelStart.alienPlayerShipSelectionList.addAll(alienPlayerShipSelection.shipNameList);
        levelStart.humanPlayerShipSelectionList.addAll(humanPlayerShipSelection.shipNameList);

        levelStart.playerShipPosition.set(readVector3D(playerShipPositionX, playerShipPositionY, playerShipPositionZ));
        levelStart.playerShipOrientation.set(readQuaternion(
                playerShipOrientationX, playerShipOrientationY, playerShipOrientationZ, playerShipOrientationAngle));
    }

    private void reset() {

    }

    private LevelEvent getCurrentLevelEvent() {
        if (contentModel.getCurrentSelectedEvent() != null && !contentModel.getCurrentSelectedEvent().isBlank()) {
            return contentModel.getLevel().levelEventList.stream().filter(levelEvent ->
                    levelEvent.name.equalsIgnoreCase(contentModel.getCurrentSelectedEvent())).findFirst().orElse(null);
        }
        return null;
    }

    public void setSelection(LevelObject levelObject) {
        int indexOfSelectedMesh = selectedMesh.getItems().indexOf(levelObject.name);
        if (indexOfSelectedMesh == -1) {
//            throw new IllegalArgumentException(levelObject.name + " could not be found in the selection box");
            return; // Could happen that we select the grid. So don't throw.
        }
        selectedMesh.getSelectionModel().select(indexOfSelectedMesh);
        writeVector(levelObject.position, selectionXPos, selectionYPos, selectionZPos);
        ENG_Vector4D axis = new ENG_Vector4D();
        double angleDeg = levelObject.orientation.toAngleAxisDeg(axis);
        writeVector(axis, selectionXRot, selectionYRot, selectionZRot);
        selectionAngleRot.setText(String.valueOf(angleDeg));
        selectedMeshNameText.setText(FilenameUtils.getBaseName(levelObject.meshName));
        selectedMeshLevelNameText.setText(levelObject.name);
    }

    public boolean isGridAttached() {
        return gridAttached;
    }

    public GridAttachmentType getGridAttachmentType() {
        return gridAttachmentType;
    }

    public boolean isCurrentWaypointsVisibility() {
        return currentWaypointsVisibility;
    }

    public static SettingsController getSingleton() {
        return settingsController;
    }
}
