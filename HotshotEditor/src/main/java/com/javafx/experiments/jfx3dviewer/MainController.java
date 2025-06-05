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

import com.javafx.experiments.importers.Importer3D;
import com.javafx.experiments.importers.Optimizer;
import com.javafx.experiments.osspecific.OSSpecific;
import com.javafx.experiments.utils3d.Utility;
import headwayent.blackholedarksun.levelresource.LevelObject;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.commons.io.FilenameUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for main fxml file.
 */
public class MainController implements Initializable {

    public static final String NAVIGATION_FXML = /*OSSpecific.getResourcePath() + */"navigation.fxml";
    public static final String SETTINGS_FXML = /*OSSpecific.getResourcePath() + */"settings.fxml";
    public static final String LIGHTSPEED_LEVEL_EXTENSION = "*.lspl";
    private static MainController singleton;
    public Button endBtn;
    public Button ffBtn;
    public CheckMenuItem loadAsPolygonsCheckBox;
    public ToggleButton loopBtn;
    public SplitMenuButton openMenuBtn;
    public CheckMenuItem optimizeCheckBox;
    public ToggleButton playBtn;
    public Button rwBtn;
    public ToggleButton settingsBtn;
    public SplitPane splitPane;
    public Button startBtn;
    public Label status;
    public TimelineDisplay timelineDisplay;
    private final ContentModel contentModel = Jfx3dViewerApp.getContentModel();
    private File loadedPath = new File(System.getProperty("user.dir"));
    private String loadedURL;
    private int meshCount = 0;
    private int nodeCount = 0;
    private SessionManager sessionManager = SessionManager.getSessionManager();
    private double settingsLastWidth = -1;
    private Accordion settingsPanel;
    private String[] supportedFormatRegex;
    private TimelineController timelineController;
    private int triangleCount = 0;
    private Parent parent;
    private Parent navigationPanel;
    private VelocityContext templateContext;
    private Template currentLoadedTemplate;

    @FXML
    public void saveLevel(ActionEvent event) {
        System.out.println("saveLevel");
        if (currentLoadedTemplate == null) {
            return;
        }
        FileChooser chooser = new FileChooser();
        if (loadedPath != null) {
            chooser.setInitialDirectory(loadedPath);
        }
        chooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("Hotshot level", "*.txt"));
        chooser.setTitle("Export level");
        File newFile = chooser.showSaveDialog(openMenuBtn.getScene().getWindow());
        if (newFile != null) {
            SettingsController settingsController = SettingsController.getSingleton();
            settingsController.readLevelStart();
            settingsController.readPlayerShipSelection();
            contentModel.export(templateContext);
            try {
                String outputFilename = newFile.toURI().toURL().toString();
                PrintWriter writer = new PrintWriter(newFile);
                currentLoadedTemplate.merge(templateContext, writer);
                writer.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No file was chosen");
        }
    }

    @FXML
    public void loadTemplate(ActionEvent event) {
        System.out.println("loadTemplate");
        if (currentLoadedTemplate != null) return;
        currentLoadedTemplate = Velocity.getTemplate(OSSpecific.getSrcResourcePath() + "level_default.vm"//newFile.getName()
//                        .toURI()
//                        .toURL()
                .toString());
//        FileChooser chooser = new FileChooser();
//        if (loadedPath != null) {
//            chooser.setInitialDirectory(loadedPath.getAbsoluteFile().getParentFile());
//        }
//        chooser.getExtensionFilters()
//                .addAll(new FileChooser.ExtensionFilter("Velocity template", "*.vm"));
//        chooser.setTitle("Load level template");
//        File newFile = chooser.showOpenDialog(openMenuBtn.getScene().getWindow());
//        if (newFile != null) {
//            currentLoadedTemplate = Velocity.getTemplate(OSSpecific.getSrcResourcePath() + "level_default.vm"//newFile.getName()
////                        .toURI()
////                        .toURL()
//                    .toString());
//            String name = FilenameUtils.getName(newFile.getAbsolutePath());
//            String fullPath = FilenameUtils.getFullPath(newFile.getAbsolutePath());
////            headwayent.blackholedarksun.levelresource.Level level = (headwayent.blackholedarksun.levelresource.Level) LevelLoader.compileLevel(name, fullPath, false);
////            contentModel.setLevel(level);
//        }
    }

    @FXML
    public void export(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        if (loadedPath != null) {
            chooser.setInitialDirectory(loadedPath);
        }
        chooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("LightSpeed level", LIGHTSPEED_LEVEL_EXTENSION));
        chooser.setTitle("Save level");
        File newFile = chooser.showSaveDialog(openMenuBtn.getScene().getWindow());
        if (newFile != null) {
            SettingsController.getSingleton().saveLevel(newFile);
//            String extension = newFile.getName().substring(newFile.getName().lastIndexOf('.') + 1).toLowerCase();
//            //            System.out.println("extension = " + extension);
//            if ("java".equals(extension)) {
//                final String url = loadedURL;
//                //                System.out.println("url = " + loadedPath);
//                final String baseUrl = url.substring(0, url.lastIndexOf('/'));
//
//                JavaSourceExporter javaSourceExporter = new JavaSourceExporter(baseUrl,
//                        contentModel.getContent(), contentModel.getTimeline(), newFile);
//                javaSourceExporter.export();
//            } else if ("fxml".equals(extension)) {
//                new FXMLExporter(newFile.getAbsolutePath()).export(contentModel.getContent());
//            } else {
//                System.err.println("Can not export a file of type [."
//                        + extension + "]");
//            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        singleton = this;
        try {
            // CREATE NAVIGATOR CONTROLS
            navigationPanel = FXMLLoader.load(Objects.requireNonNull(MainController.class.getResource(NAVIGATION_FXML)));
            // CREATE SETTINGS PANEL
            settingsPanel = FXMLLoader.load(Objects.requireNonNull(MainController.class.getResource(SETTINGS_FXML)));
            // SETUP SPLIT PANE
            splitPane.getItems().addAll(new SubSceneResizer(contentModel.subSceneProperty(), navigationPanel), settingsPanel);
            splitPane.getDividers().get(0).setPosition(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // create timelineController;
        timelineController = new TimelineController(startBtn, rwBtn, playBtn, ffBtn, endBtn, loopBtn);
        timelineController.timelineProperty().bind(contentModel.timelineProperty());
        timelineDisplay.timelineProperty().bind(contentModel.timelineProperty());
        // listen for drops
        supportedFormatRegex = Importer3D.getSupportedFormatExtensionFilters();
        for (int i = 0; i < supportedFormatRegex.length; i++) {
            supportedFormatRegex[i] = "." + supportedFormatRegex[i].replaceAll("\\.", "\\.");
            //            System.out.println("supportedFormatRegex[i] = " + supportedFormatRegex[i]);
        }
        contentModel.getSubScene().setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                boolean hasSupportedFile = false;
                fileLoop:
                for (File file : db.getFiles()) {
                    for (String format : supportedFormatRegex) {
                        if (file.getName().matches(format)) {
                            hasSupportedFile = true;
                            break fileLoop;
                        }
                    }
                }
                if (hasSupportedFile) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
            }
            event.consume();
        });
        contentModel.getSubScene().setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                File supportedFile = null;
                fileLoop:
                for (File file : db.getFiles()) {
                    for (String format : supportedFormatRegex) {
                        if (file.getName().matches(format)) {
                            supportedFile = file;
                            break fileLoop;
                        }
                    }
                }
                if (supportedFile != null) {
                    // workaround for RT-30195
                    if (supportedFile.getAbsolutePath().indexOf('%') != -1) {
                        supportedFile = new File(URLDecoder.decode(supportedFile.getAbsolutePath()));
                    }
                    // TODO
//                    load(supportedFile);
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        sessionManager.bind(settingsBtn.selectedProperty(), "settingsBtn");
        sessionManager.bind(splitPane.getDividers().get(0).positionProperty(), "settingsSplitPanePosition");
        sessionManager.bind(optimizeCheckBox.selectedProperty(), "optimize");
        sessionManager.bind(loadAsPolygonsCheckBox.selectedProperty(), "loadAsPolygons");
        sessionManager.bind(loopBtn.selectedProperty(), "loop");

        String url = sessionManager.getProperties().getProperty(Jfx3dViewerApp.FILE_URL_PROPERTY);
        if (url == null) {
//            url = ContentModel.class.getResource(OSSpecific.getSrcResourcePath() + "drop-here-large-yUp.obj").toExternalForm();
        }

        // do initial status update
        updateStatus();

        Velocity.init();
        templateContext = new VelocityContext();
        loadTemplate(null);
    }

    @FXML
    public void open(ActionEvent actionEvent) {
//        SettingsController.getSingleton().loadLevel();
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Supported files",
                LIGHTSPEED_LEVEL_EXTENSION));
        if (loadedPath != null) {
            chooser.setInitialDirectory(loadedPath);
        }
        chooser.setTitle("Select file to load");
        File newFile = chooser.showOpenDialog(openMenuBtn.getScene().getWindow());
        if (newFile != null) {
            // TODO
//            load(newFile);
            SettingsController.getSingleton().loadLevel(newFile);
        }
    }

    public void toggleSettings(ActionEvent event) {
        final SplitPane.Divider divider = splitPane.getDividers().get(0);
        if (settingsBtn.isSelected()) {
            if (settingsLastWidth == -1) {
                settingsLastWidth = settingsPanel.prefWidth(-1);
            }
            final double divPos = 1 - (settingsLastWidth / splitPane.getWidth());
            new Timeline(new KeyFrame(Duration.seconds(0.3), event1 ->
                    settingsPanel.setMinWidth(Region.USE_PREF_SIZE),
                    new KeyValue(divider.positionProperty(), divPos, Interpolator.EASE_BOTH))).play();
        } else {
            settingsLastWidth = settingsPanel.getWidth();
            settingsPanel.setMinWidth(0);
            new Timeline(new KeyFrame(Duration.seconds(0.3), new KeyValue(divider.positionProperty(), 1))).play();
        }
    }

//    private void doLoad(String fileUrl) {
//        doLoad(fileUrl, null);
//    }

    private void doLoad(LevelObject levelObject) {
        try {
            loadedURL = new File(levelObject.meshName).toURI().toURL().toString();
        } catch (MalformedURLException e) {
            Utility.showErrorDialog(e.toString());
            throw new IllegalArgumentException(e);
        }
        sessionManager.getProperties().setProperty(Jfx3dViewerApp.FILE_URL_PROPERTY, loadedURL);
        try {
            Pair<Node, Timeline> content = Importer3D.loadIncludingAnimation(loadedURL, loadAsPolygonsCheckBox.isSelected());
            Timeline timeline = content.getValue();
            Node root = content.getKey();
            if (optimizeCheckBox.isSelected()) {
                new Optimizer(timeline, root, true).optimize();
            }
            root.setUserData(levelObject);
            contentModel.setContent(root);
            contentModel.setTimeline(timeline);

            if (timeline != null) {
                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.play();
            }
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            Utility.showErrorDialog(ex.toString());
        }
        updateStatus();
    }

//    public void load(File file) {
//        load(file, null);
//    }

    public void load(LevelObject levelObject) {
//        loadedPath = file;
        try {
            doLoad(levelObject);
        } catch (Exception ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            Utility.showErrorDialog(ex.toString());
        }
        requestSceneFocus();
    }

    public void unload(String name) {

    }

    private void updateCount(Node node) {
        nodeCount++;
        if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                updateCount(child);
            }
        } else if (node instanceof Box) {
            meshCount++;
            triangleCount += 6 * 2;
        } else if (node instanceof MeshView) {
            TriangleMesh mesh = (TriangleMesh) ((MeshView) node).getMesh();
            if (mesh != null) {
                meshCount++;
                triangleCount += mesh.getFaces().size() / mesh.getFaceElementSize();
            }
        }
    }

//    private int callCount;

    public void updateStatus() {
        nodeCount = 0;
        meshCount = 0;
        triangleCount = 0;
        updateCount(contentModel.getRoot3D());
        Node content = contentModel.getContent();
        ENG_Vector3D cameraPosition = contentModel.getCameraTransform().getPosition();
        ENG_Vector4D axis = new ENG_Vector4D();
        double angle = contentModel.getCameraTransform().getOrientation().toAngleAxisDeg(axis);
        final Bounds bounds = content == null ? new BoundingBox(0, 0, 0, 0)
                : content.getBoundsInLocal();
//        String format = String.format("Nodes [%d] :: Meshes [%d] :: Triangles [%d] :: "
//                        + "Bounds [w=%.2f,h=%.2f,d=%.2f], callCount [%d]",
//                nodeCount, meshCount, triangleCount,
//                bounds.getWidth(), bounds.getHeight(), bounds.getDepth(), ++callCount);
        // Hocus pocus to change the coordinate system for our game. X - right Y - up Z - back. JavaFX 3D uses X - right Y - down Z - front.
        String format = String.format("Nodes [%d] :: Meshes [%d] :: Triangles [%d] :: "
                        + "Bounds [w=%.2f,h=%.2f,d=%.2f] " +
                        "CameraPos [pos=%.2f %.2f %.2f] CameraAngleAxis [axis=%.2f %.2f %.2f angle=%.2f]",
                nodeCount, meshCount, triangleCount,
                bounds.getWidth(), bounds.getHeight(), bounds.getDepth(),
                cameraPosition.x, -cameraPosition.y, -cameraPosition.z,
                axis.x, -axis.y, axis.z,
                angle);
        status.setText(format);
//        System.out.println(format);
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Parent getParent() {
        return parent;
    }

    public void setFocusOnSplitPane() {
        splitPane.requestFocus();
        contentModel.rebuildSubScene();
//        navigationPanel.requestFocus();
//        navigationPanel.setOnKeyPressed(event -> System.out.println("navigationPanel key pressed"));
        requestSceneFocus();
//        splitPane.focusedProperty().addListener((observable, oldValue, newValue) -> System.out.println("splitPane is: " + (newValue ? "focused" : "unfocused")));
//        contentModel.getSubScene().focusedProperty().addListener((observable, oldValue, newValue) -> System.out.println("subScene is: " + (newValue ? "focused" : "unfocused")));
    }

    private void requestSceneFocus() {
        contentModel.getSubScene().requestFocus();
    }

    public static MainController getSingleton() {
        return singleton;
    }
}
