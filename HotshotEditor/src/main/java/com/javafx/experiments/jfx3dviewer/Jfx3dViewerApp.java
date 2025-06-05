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

import com.javafx.experiments.osspecific.OSSpecific;
import com.javafx.experiments.utils3d.Utility;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.resource.ENG_ModelResource;
import headwayent.hotshotengine.scriptcompiler.ENG_ModelCompiler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

/**
 * JavaFX 3D Viewer Application
 */
public class Jfx3dViewerApp extends Application {

    public static final String BOX_ASTEROID_PATH = OSSpecific.getSrcResourcePath() + "box.obj";
    public static final String GRID_PATH = OSSpecific.getSrcResourcePath() + "grid.obj";

    public static final String FILE_URL_PROPERTY = "fileUrl";
    public static final String EDITOR_TITLE = "Lightspeed Editor";
    public static final String MAIN_FXML = /*OSSpecific.getResourcePath() + */"main.fxml";
    public static final boolean SCALE_UP_100 = true;
    private static ContentModel contentModel;

    private static final HashMap<String, ENG_ModelResource> loaderShipMap = new HashMap<>();
    private static final HashMap<String, ENG_ModelResource> loaderWeaponMap = new HashMap<>();
    private static final HashMap<String, ENG_ModelResource> loaderSkyboxMap = new HashMap<>();
    private static final HashMap<String, ENG_ModelResource> loaderAsteroidMap = new HashMap<>();
    private static final HashMap<String, ENG_ModelResource> loaderMiscMap = new HashMap<>();
    private static final HashMap<String, ENG_ModelResource> loaderStaticEntitiesMap = new HashMap<>();

    private static final HashMap<String, ENG_ModelResource> loaderMap = new HashMap<>();

    public static ContentModel getContentModel() {
        return contentModel;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private SessionManager sessionManager;

    @Override
    public void start(Stage stage) throws Exception {
        getParameters();
        ENG_Utility.createRandomNumberGenerator(0);
        sessionManager = SessionManager.createSessionManager("Jfx3dViewerApp");
        sessionManager.loadSession();
        contentModel = new ContentModel();
        initializeContentModel();

        List<String> args = getParameters().getRaw();
        if (!args.isEmpty()) {
            sessionManager.getProperties().setProperty(FILE_URL_PROPERTY,
                    new File(args.get(0)).toURI().toURL().toString());
        }

        ENG_ModelCompiler modelCompiler = new ENG_ModelCompiler();
        ArrayList<ENG_ModelResource> loaderShipList = modelCompiler.compile("loader_ship_list.txt", OSSpecific.getSrcResourcePath(), true);
//        ArrayList<ENG_ModelResource> loaderWeaponList = modelCompiler.compile("loader_weapon_list.txt", OSSpecific.getSrcResourcePath(), true);
        ArrayList<ENG_ModelResource> loaderSkyboxList = modelCompiler.compile("loader_skybox_list.txt", OSSpecific.getSrcResourcePath(), true);
        ArrayList<ENG_ModelResource> loaderAsteroidList = modelCompiler.compile("loader_asteroid_list.txt", OSSpecific.getSrcResourcePath(), true);
        ArrayList<ENG_ModelResource> loaderMiscList = modelCompiler.compile("loader_misc_list.txt", OSSpecific.getSrcResourcePath(), true);
        ArrayList<ENG_ModelResource> loaderStaticEntitiesList = modelCompiler.compile("loader_static_entities_list.txt", OSSpecific.getSrcResourcePath(), true);
        addArrayToHashMap(loaderShipList, getLoaderShipMap());
//        addArrayToHashMap(loaderWeaponList, getLoaderWeaponMap());
        addArrayToHashMap(loaderSkyboxList, getLoaderSkyboxMap());
        addArrayToHashMap(loaderAsteroidList, getLoaderAsteroidMap());
        addArrayToHashMap(loaderMiscList, getLoaderMiscMap());
        addArrayToHashMap(loaderStaticEntitiesList, getLoaderStaticEntitiesMap());

        addMapToMap(getLoaderShipMap(), getLoaderMap());
        addMapToMap(getLoaderWeaponMap(), getLoaderMap());
        addMapToMap(getLoaderSkyboxMap(), getLoaderMap());
        addMapToMap(getLoaderAsteroidMap(), getLoaderMap());
        addMapToMap(getLoaderMiscMap(), getLoaderMap());
        addMapToMap(getLoaderStaticEntitiesMap(), getLoaderMap());

        Utility.initKryo();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(MAIN_FXML));
        Parent parent = loader.<Parent>load();
        MainController controller = loader.getController();
        controller.setParent(parent);
        Scene scene = new Scene(parent, 1024, 600);
        stage.setTitle(EDITOR_TITLE);
//        stage.setFullScreen(true);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();

        controller.setFocusOnSplitPane();

        stage.setOnCloseRequest(event -> sessionManager.saveSession());

        //        org.scenicview.ScenicView.show(contentModel.getSubScene().getRoot());
    }

    private static void addArrayToHashMap(ArrayList<ENG_ModelResource> list, HashMap<String, ENG_ModelResource> map) {
        for (ENG_ModelResource res : list) {
            map.put(res.name, res);
        }
    }

    private static void addMapToMap(java.util.Map<String, ENG_ModelResource> src, java.util.Map<String, ENG_ModelResource> dest) {
        for (java.util.Map.Entry<String, ENG_ModelResource> entry : src.entrySet()) {
            ENG_ModelResource put = dest.put(entry.getKey(), entry.getValue());
            if (put != null) {
                throw new IllegalArgumentException("Multiple resources with name " + entry.getKey());
            }
        }
    }

    protected void initializeContentModel() {
    }

    public static ArrayList<String> getSortedCollection(Collection<String> collection) {
        ArrayList<String> list = new ArrayList<>(collection);
        Collections.sort(list);
        return list;
    }

    public static ArrayList<String> getShipNames() {
        return getSortedCollection(getLoaderShipMap().keySet());
    }

    public static ArrayList<String> getSkyboxNames() {
        return getSortedCollection(getLoaderSkyboxMap().keySet());
    }

    public static ArrayList<String> getAsteroidNames() {
        return getSortedCollection(getLoaderAsteroidMap().keySet());
    }

    public static ArrayList<String> getWeaponNames() {
        return getSortedCollection(getLoaderWeaponMap().keySet());
    }

    public static ArrayList<String> getStaticEntitiesNames() {
        return getSortedCollection(getLoaderStaticEntitiesMap().keySet());
    }

    public static HashMap<String, ENG_ModelResource> getLoaderShipMap() {
        return loaderShipMap;
    }

    public static HashMap<String, ENG_ModelResource> getLoaderWeaponMap() {
        return loaderWeaponMap;
    }

    public static HashMap<String, ENG_ModelResource> getLoaderSkyboxMap() {
        return loaderSkyboxMap;
    }

    public static HashMap<String, ENG_ModelResource> getLoaderAsteroidMap() {
        return loaderAsteroidMap;
    }

    public static HashMap<String, ENG_ModelResource> getLoaderMiscMap() {
        return loaderMiscMap;
    }

    public static HashMap<String, ENG_ModelResource> getLoaderStaticEntitiesMap() {
        return loaderStaticEntitiesMap;
    }

    public static HashMap<String, ENG_ModelResource> getLoaderMap() {
        return loaderMap;
    }
}
