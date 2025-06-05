/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

import headwayent.hotshotengine.ENG_Degree;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.renderer.ENG_OverlayElement.GuiHorizontalAlignment;
import headwayent.hotshotengine.renderer.ENG_OverlayElement.GuiMetricsMode;
import headwayent.hotshotengine.renderer.ENG_OverlayElement.GuiVerticalAlignment;

import java.util.ArrayList;

public class ENG_OverlayContainerResource {

    public enum OverlayType {
        CONTAINER, ELEMENT
    }

    public enum OverlayElementType {
        PANEL, BORDER_PANEL, TEXT_AREA
    }

    public String name;
    public OverlayType type;
    public OverlayElementType elementType;
    public GuiMetricsMode metricsMode = GuiMetricsMode.GMM_RELATIVE;
    public GuiHorizontalAlignment horzAlig = GuiHorizontalAlignment.GHA_LEFT;
    public GuiVerticalAlignment vertAlig = GuiVerticalAlignment.GVA_TOP;
    public float left, top, width = 1.0f, height = 1.0f;
    public String materialName;
    public String caption = "";
    public ENG_Degree rotationAngle;
    public ENG_Vector3D axis;
    public ArrayList<ENG_OverlayContainerResource> children;
}
