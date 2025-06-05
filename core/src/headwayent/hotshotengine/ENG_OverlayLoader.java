/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import headwayent.hotshotengine.renderer.ENG_Overlay;
import headwayent.hotshotengine.renderer.ENG_OverlayContainer;
import headwayent.hotshotengine.renderer.ENG_OverlayElement;
import headwayent.hotshotengine.renderer.ENG_OverlayManager;
import headwayent.hotshotengine.renderer.ENG_PanelOverlayElement;
import headwayent.hotshotengine.renderer.ENG_TextAreaOverlayElement;
import headwayent.hotshotengine.resource.ENG_OverlayContainerResource;
import headwayent.hotshotengine.resource.ENG_OverlayPanelContainer;
import headwayent.hotshotengine.resource.ENG_OverlayPanelContainer.Tiling;
import headwayent.hotshotengine.resource.ENG_OverlayPanelContainer.UVCoords;
import headwayent.hotshotengine.resource.ENG_OverlayResource;
import headwayent.hotshotengine.resource.ENG_OverlayTextAreaElement;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;
import headwayent.hotshotengine.scriptcompiler.ENG_OverlayCompiler;

import java.util.ArrayList;

public class ENG_OverlayLoader {

    public static void loadMaterialList(String fileName, String path, boolean fromSDCard) {
        ArrayList<String> materialList =
                ENG_CompilerUtil.loadListFromFile(fileName, path);

        for (String mat : materialList) {
            String[] pathAndFileName = ENG_CompilerUtil.getPathAndFileName(mat);
            loadOverlay(pathAndFileName[1], pathAndFileName[0], fromSDCard);
        }
    }

    public static void loadOverlay(String fileName, String path, boolean fromSDCard) {
        loadCompiledResource(new ENG_OverlayCompiler().compile(fileName, path, fromSDCard));
    }

    private static void loadCompiledResource(
            ArrayList<ENG_OverlayResource> resArr) {
        
        for (ENG_OverlayResource res : resArr) {
            ENG_Overlay ov = ENG_OverlayManager.getSingleton().create(res.name);
            ov.setZOrder((short) res.zorder);
            for (ENG_OverlayContainerResource cont : res.containers) {

                ENG_OverlayElement element =
                        createOverlayElement(cont);
                ov.add2D((ENG_OverlayContainer) element);
                initializeOverlayElement(cont, element);
                if (element.isContainer()) {
                    initChildren(cont, (ENG_OverlayContainer) element);
                }
            }
        }
    }

    private static void initChildren(ENG_OverlayContainerResource cont,
                                     ENG_OverlayContainer parent) {
        if (cont.children != null) {
            for (ENG_OverlayContainerResource child : cont.children) {
                ENG_OverlayElement childElement =
                        createOverlayElement(child);
                parent.addChild(childElement);
                initializeOverlayElement(child, childElement);
                if (childElement.isContainer()) {
                    initChildren(child, (ENG_OverlayContainer) childElement);
                }
            }
        }
    }

    private static ENG_OverlayElement createOverlayElement(
            ENG_OverlayContainerResource cont) {
        String typeName;
        switch (cont.elementType) {
            case PANEL:
                typeName = "Panel";
                break;
            case BORDER_PANEL:
                typeName = "BorderPanel";
                break;
            case TEXT_AREA:
                typeName = "TextArea";
                break;
            default:
                //Should never get here
                throw new IllegalArgumentException();
        }
        return ENG_OverlayManager.getSingleton().createOverlayElement(
                typeName, cont.name);
    }

    private static void initializeOverlayElement(
            ENG_OverlayContainerResource cont, ENG_OverlayElement element) {
        element.setLeft(cont.left);
        element.setTop(cont.top);
        element.setDimensions(cont.width, cont.height);
        if (cont.materialName != null) {
            element.setMaterialName(cont.materialName);
        }
        element.setMetricsMode(cont.metricsMode);
        element.setCaption(cont.caption);
        element.setHorizontalAlignment(cont.horzAlig);
        element.setVerticalAlignment(cont.vertAlig);
        if (cont.rotationAngle != null && cont.axis != null) {
            throw new UnsupportedOperationException();
        }
        if (element instanceof ENG_PanelOverlayElement) {
            ENG_PanelOverlayElement panel = (ENG_PanelOverlayElement) element;
            ENG_OverlayPanelContainer panelCont =
                    (ENG_OverlayPanelContainer) cont;
            UVCoords c = panelCont.uvCoords;
            if (c != null) {
                panel.setUV(c.topleftU, c.topleftV, c.bottomrightU, c.bottomrightV);
            }
            Tiling t = panelCont.tiling;
            if (t != null) {
                panel.setTiling(t.x, t.y);
            }
        } else if (element instanceof ENG_TextAreaOverlayElement) {
            ENG_TextAreaOverlayElement text = (ENG_TextAreaOverlayElement) element;
            ENG_OverlayTextAreaElement textCont = (ENG_OverlayTextAreaElement) cont;
            text.setFontName(textCont.fontName);
            text.setCharHeight(textCont.charHeight);
            text.setSpaceWidth(textCont.spaceWidth);
            text.setCaption(textCont.caption);
            text.setAlignment(textCont.alignment);
            if (textCont.color != null) {
                text.setColour(textCont.color);
            }
            if (textCont.bottomColor != null) {
                text.setBottomColour(textCont.bottomColor);
            }
            if (textCont.topColor != null) {
                text.setTopColour(textCont.topColor);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }


}
