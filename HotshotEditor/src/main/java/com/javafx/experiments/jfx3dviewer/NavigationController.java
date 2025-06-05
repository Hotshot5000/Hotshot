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

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollBar;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for settings panel
 */
public class NavigationController implements Initializable {
    public FourWayNavControl eyeNav;
    public ScrollBar zoomBar;
    public FourWayNavControl camNav;
    private final ContentModel contentModel = Jfx3dViewerApp.getContentModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        zoomBar.setMin(-ContentModel.cameraDistance);
        zoomBar.setMax(0);
//        zoomBar.setValue(contentModel.getCameraPosition().getZ());
        zoomBar.setValue(contentModel.getCameraTransform().getPosition().z);
        zoomBar.setVisibleAmount(5);
//        contentModel.getCameraPosition()
//                    .zProperty()
//                    .bindBidirectional(zoomBar.valueProperty());
        contentModel.getCameraTransform().getAffineMat().tzProperty().bindBidirectional(zoomBar.valueProperty());
        Platform.runLater(() -> contentModel.getCameraTransform().getAffineMat().tzProperty().addListener((observable, oldValue, newValue) -> System.out.println("tzProperty: " + newValue)));
//        Platform.runLater(() -> contentModel.getCameraPosition().zProperty().addListener((observable, oldValue, newValue) -> System.out.println("zProperty: " + newValue)));

//        eyeNav.setListener((direction, amount) -> {
//            switch (direction) {
//                case TOP:
//                    contentModel.getCameraLookXRotate().setAngle(contentModel.getCameraLookXRotate().getAngle()+amount);
//                    break;
//                case BOTTOM:
//                    contentModel.getCameraLookXRotate().setAngle(contentModel.getCameraLookXRotate().getAngle()-amount);
//                    break;
//                case LEFT:
//                    contentModel.getCameraLookZRotate().setAngle(contentModel.getCameraLookZRotate().getAngle()-amount);
//                    break;
//                case RIGHT:
//                    contentModel.getCameraLookZRotate().setAngle(contentModel.getCameraLookZRotate().getAngle()+amount);
//                    break;
//            }
//        });
        camNav.setListener((direction, amount) -> {
            switch (direction) {
                case TOP ->
//                    contentModel.getCameraXform().rx.setAngle(contentModel.getCameraXform().rx.getAngle()-amount);
                        contentModel.getCameraTransform().addOrientation(Transform.Axis.X, -amount);
                case BOTTOM ->
//                    contentModel.getCameraXform().rx.setAngle(contentModel.getCameraXform().rx.getAngle()+amount);
                        contentModel.getCameraTransform().addOrientation(Transform.Axis.X, amount);
                case LEFT ->
//                    contentModel.getCameraXform().ry.setAngle(contentModel.getCameraXform().ry.getAngle()+amount);
                        contentModel.getCameraTransform().addOrientation(Transform.Axis.Y, -amount);
                case RIGHT ->
//                    contentModel.getCameraXform().ry.setAngle(contentModel.getCameraXform().ry.getAngle()-amount);
                        contentModel.getCameraTransform().addOrientation(Transform.Axis.Y, amount);
            }
        });
    }
}
