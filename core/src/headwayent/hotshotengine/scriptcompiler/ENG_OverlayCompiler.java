/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.scriptcompiler;

import headwayent.hotshotengine.ENG_Degree;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_OverlayElement.GuiHorizontalAlignment;
import headwayent.hotshotengine.renderer.ENG_OverlayElement.GuiMetricsMode;
import headwayent.hotshotengine.renderer.ENG_OverlayElement.GuiVerticalAlignment;
import headwayent.hotshotengine.renderer.ENG_TextAreaOverlayElement.Alignment;
import headwayent.hotshotengine.resource.ENG_OverlayContainerResource;
import headwayent.hotshotengine.resource.ENG_OverlayContainerResource.OverlayElementType;
import headwayent.hotshotengine.resource.ENG_OverlayContainerResource.OverlayType;
import headwayent.hotshotengine.resource.ENG_OverlayPanelContainer;
import headwayent.hotshotengine.resource.ENG_OverlayPanelContainer.Tiling;
import headwayent.hotshotengine.resource.ENG_OverlayPanelContainer.UVCoords;
import headwayent.hotshotengine.resource.ENG_OverlayResource;
import headwayent.hotshotengine.resource.ENG_OverlayTextAreaElement;
import headwayent.hotshotengine.resource.ENG_Resource;

import java.io.DataInputStream;
import java.util.ArrayList;

public class ENG_OverlayCompiler extends ENG_AbstractCompiler<ArrayList<ENG_OverlayResource>> {

    private static final String ZORDER = "zorder";
    private static final String CONTAINER = "container";
    private static final String ELEMENT = "element";

    private static final String PANEL = "panel";
    private static final String BORDER_PANEL = "borderpanel";
    private static final String TEXT_AREA = "textarea";

    // Base params
    private static final String METRICS_MODE = "metrics_mode";
    private static final String HORZ_ALIGN = "horz_align";
    private static final String VERT_ALIGN = "vert_align";
    private static final String LEFT = "left";
    private static final String TOP = "top";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String MATERIAL = "material";
    private static final String CAPTION = "caption";
    private static final String ROTATION = "rotation";

    private static final String RELATIVE = "relative";
    private static final String PIXELS = "pixels";

    private static final String ALIGN_LEFT = "left";
    private static final String ALIGN_TOP = "top";
    private static final String ALIGN_RIGHT = "right";
    private static final String ALIGN_BOTTOM = "bottom";
    private static final String ALIGN_CENTER = "center";

    // Panel
    private static final String TRANSPARENT = "transparent";
    private static final String TILING = "tiling";
    private static final String UV_COORDS = "uv_coords";

    // TextArea
    private static final String FONT_NAME = "font_name";
    private static final String CHAR_HEIGHT = "char_height";
    private static final String COLOUR = "colour";
    private static final String COLOUR_BOTTOM = "colour_bottom";
    private static final String COLOUR_TOP = "colour_top";
    private static final String ALIGNMENT = "alignment";
    private static final String SPACE_WIDTH = "space_width";

    private static boolean parseBaseParams(String s, DataInputStream fp0,
                                           ENG_OverlayContainerResource res) {
        boolean paramParsed = true;
        if (s.equalsIgnoreCase(METRICS_MODE)) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            if (s.equalsIgnoreCase(RELATIVE)) {
                res.metricsMode = GuiMetricsMode.GMM_RELATIVE;
            } else if (s.equalsIgnoreCase(PIXELS)) {
                res.metricsMode = GuiMetricsMode.GMM_PIXELS;
            } else {
                throw new ENG_InvalidFormatParsingException();
            }
        } else if (s.equalsIgnoreCase(HORZ_ALIGN)) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            if (s.equalsIgnoreCase(ALIGN_LEFT)) {
                res.horzAlig = GuiHorizontalAlignment.GHA_LEFT;
            } else if (s.equalsIgnoreCase(ALIGN_RIGHT)) {
                res.horzAlig = GuiHorizontalAlignment.GHA_RIGHT;
            } else if (s.equalsIgnoreCase(ALIGN_CENTER)) {
                res.horzAlig = GuiHorizontalAlignment.GHA_CENTER;
            } else {
                throw new ENG_InvalidFormatParsingException();
            }
        } else if (s.equalsIgnoreCase(VERT_ALIGN)) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            if (s.equalsIgnoreCase(ALIGN_TOP)) {
                res.vertAlig = GuiVerticalAlignment.GVA_TOP;
            } else if (s.equalsIgnoreCase(ALIGN_BOTTOM)) {
                res.vertAlig = GuiVerticalAlignment.GVA_BOTTOM;
            } else if (s.equalsIgnoreCase(ALIGN_CENTER)) {
                res.vertAlig = GuiVerticalAlignment.GVA_CENTER;
            } else {
                throw new ENG_InvalidFormatParsingException();
            }
        } else if (s.equalsIgnoreCase(LEFT)) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            try {
                res.left = Float.parseFloat(s);
            } catch (NumberFormatException e) {
                throw new ENG_InvalidFormatParsingException();
            }
        } else if (s.equalsIgnoreCase(TOP)) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            try {
                res.top = Float.parseFloat(s);
            } catch (NumberFormatException e) {
                throw new ENG_InvalidFormatParsingException();
            }
        } else if (s.equalsIgnoreCase(WIDTH)) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            try {
                res.width = Float.parseFloat(s);
            } catch (NumberFormatException e) {
                throw new ENG_InvalidFormatParsingException();
            }
        } else if (s.equalsIgnoreCase(HEIGHT)) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            try {
                res.height = Float.parseFloat(s);
            } catch (NumberFormatException e) {
                throw new ENG_InvalidFormatParsingException();
            }
        } else if (s.equalsIgnoreCase(MATERIAL)) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            res.materialName = s;
        } else if (s.equalsIgnoreCase(CAPTION)) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            // Use _ as separators then we split it
            res.caption = s.replace("_", " ");
            /*
			 * String[] strs = s.split("_"); StringBuilder b = new
			 * StringBuilder(); for (int i = 0; i < strs.length; ++i) {
			 * b.append(strs[i]); } res.caption = b.toString();
			 */
        } else if (s.equalsIgnoreCase(ROTATION)) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            try {
                res.rotationAngle = new ENG_Degree(Float.parseFloat(s));
            } catch (NumberFormatException e) {
                throw new ENG_InvalidFormatParsingException();
            }
            float x, y, z;
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            try {
                x = Float.parseFloat(s);
            } catch (NumberFormatException e) {
                throw new ENG_InvalidFormatParsingException();
            }
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            try {
                y = Float.parseFloat(s);
            } catch (NumberFormatException e) {
                throw new ENG_InvalidFormatParsingException();
            }
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            try {
                z = Float.parseFloat(s);
            } catch (NumberFormatException e) {
                throw new ENG_InvalidFormatParsingException();
            }
            res.axis = new ENG_Vector3D(x, y, z);
        } else {
            paramParsed = false;
        }
        return paramParsed;
    }

    private static ENG_OverlayTextAreaElement parseTextArea(DataInputStream fp0) {

        String s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        ENG_OverlayTextAreaElement text = new ENG_OverlayTextAreaElement();
        text.name = s;
        text.elementType = OverlayElementType.TEXT_AREA;
        text.type = OverlayType.ELEMENT;
        boolean fontNameSet = false;
        boolean charHeightSet = false;
        boolean colourSet = false;
        boolean spaceWidthSet = false;
        s = ENG_CompilerUtil.getNextWord(fp0);
        if ((s != null) && (s.equalsIgnoreCase(BRACKET_OPEN))) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException();
        }
        while (true) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            if (parseBaseParams(s, fp0, text)) {
                continue;
            }
            if (s.equalsIgnoreCase(FONT_NAME)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                text.fontName = s;
                fontNameSet = true;
            } else if (s.equalsIgnoreCase(CHAR_HEIGHT)) {
                text.charHeight = getFloat(fp0);
                charHeightSet = true;
            } else if (s.equalsIgnoreCase(COLOUR)) {
                if (text.color == null) {
                    text.color = new ENG_ColorValue();
                    text.color.a = 1.0f;
                }
                text.color.r = getFloat(fp0);
                text.color.g = getFloat(fp0);
                text.color.b = getFloat(fp0);
            } else if (s.equalsIgnoreCase(COLOUR_BOTTOM)) {
                if (text.bottomColor == null) {
                    text.bottomColor = new ENG_ColorValue();
                    text.bottomColor.a = 1.0f;
                }
                text.bottomColor.r = getFloat(fp0);
                text.bottomColor.g = getFloat(fp0);
                text.bottomColor.b = getFloat(fp0);
            } else if (s.equalsIgnoreCase(COLOUR_TOP)) {
                if (text.topColor == null) {
                    text.topColor = new ENG_ColorValue();
                    text.topColor.a = 1.0f;
                }
                text.topColor.r = getFloat(fp0);
                text.topColor.g = getFloat(fp0);
                text.topColor.b = getFloat(fp0);
            } else if (s.equalsIgnoreCase(ALIGNMENT)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                if (s.equalsIgnoreCase("left")) {
                    text.alignment = Alignment.Left;
                } else if (s.equalsIgnoreCase("center")) {
                    text.alignment = Alignment.Center;
                } else if (s.equalsIgnoreCase("right")) {
                    text.alignment = Alignment.Right;
                } else {
                    throw new ENG_InvalidFormatParsingException();
                }
            } else if (s.equalsIgnoreCase(SPACE_WIDTH)) {
                text.spaceWidth = getFloat(fp0);
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        if (!fontNameSet) {
            throw new ENG_InvalidFormatParsingException("font name must be set");
        }
        if (!charHeightSet) {
            throw new ENG_InvalidFormatParsingException(
                    "char height must be set");
        }
		/*
		 * if (!spaceWidthSet) { throw new
		 * ENG_InvalidFormatParsingException("space width must be set"); }
		 */
        return text;
    }

    private static ENG_OverlayPanelContainer parsePanel(DataInputStream fp0) {
        String s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        ENG_OverlayPanelContainer panel = new ENG_OverlayPanelContainer();
        panel.name = s;
        panel.elementType = OverlayElementType.PANEL;
        panel.type = OverlayType.CONTAINER;
        s = ENG_CompilerUtil.getNextWord(fp0);
        if ((s != null) && (s.equalsIgnoreCase(BRACKET_OPEN))) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException();
        }
        while (true) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            if (parseBaseParams(s, fp0, panel)) {
                continue;
            }
            if (s.equalsIgnoreCase(TRANSPARENT)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                try {
                    panel.transparent = Boolean.parseBoolean(s);
                } catch (NumberFormatException e) {
                    throw new ENG_InvalidFormatParsingException();
                }
            } else if (s.equalsIgnoreCase(TILING)) {
                int layer;
                int x, y;
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                try {
                    layer = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    throw new ENG_InvalidFormatParsingException();
                }
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                try {
                    x = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    throw new ENG_InvalidFormatParsingException();
                }
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                try {
                    y = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    throw new ENG_InvalidFormatParsingException();
                }
                panel.tiling = new Tiling(layer, x, y);
            } else if (s.equalsIgnoreCase(UV_COORDS)) {
                float tlu, tlv, bru, brv;
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                try {
                    tlu = Float.parseFloat(s);
                } catch (NumberFormatException e) {
                    throw new ENG_InvalidFormatParsingException();
                }
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                try {
                    tlv = Float.parseFloat(s);
                } catch (NumberFormatException e) {
                    throw new ENG_InvalidFormatParsingException();
                }
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                try {
                    bru = Float.parseFloat(s);
                } catch (NumberFormatException e) {
                    throw new ENG_InvalidFormatParsingException();
                }
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                try {
                    brv = Float.parseFloat(s);
                } catch (NumberFormatException e) {
                    throw new ENG_InvalidFormatParsingException();
                }
                panel.uvCoords = new UVCoords(tlu, tlv, bru, brv);
            } else if (s.equalsIgnoreCase(CONTAINER)
                    || s.equalsIgnoreCase(ELEMENT)) {
                parseContainer(fp0, panel);
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        return panel;

    }

    private static void parseContainer(DataInputStream fp0,
                                       ENG_OverlayContainerResource res) {
        String s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (res.children == null) {
            res.children = new ArrayList<>();
        }
        if (s.equalsIgnoreCase(PANEL)) {
            res.children.add(parsePanel(fp0));
        } else if (s.equalsIgnoreCase(BORDER_PANEL)) {

        } else if (s.equalsIgnoreCase(TEXT_AREA)) {
            res.children.add(parseTextArea(fp0));
        } else {
            throw new ENG_InvalidFormatParsingException();
        }
    }

    private static void parseContainer(DataInputStream fp0,
                                       ENG_OverlayResource res) {
        String s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (res.containers == null) {
            res.containers = new ArrayList<>();
        }
        if (s.equalsIgnoreCase(PANEL)) {
            res.containers.add(parsePanel(fp0));
        } else if (s.equalsIgnoreCase(BORDER_PANEL)) {

        } else if (s.equalsIgnoreCase(TEXT_AREA)) {
            res.containers.add(parseTextArea(fp0));
        } else {
            throw new ENG_InvalidFormatParsingException();
        }
    }

    public ArrayList<ENG_OverlayResource> compileImpl(
            String fileName, String path, boolean fromSDCard) {
        DataInputStream fp0 = null;
        try {
            fp0 = ENG_Resource.getFileAsStream(fileName, path, fromSDCard);
            String s;
            ArrayList<ENG_OverlayResource> res = new ArrayList<>();
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                ENG_OverlayResource overlay = new ENG_OverlayResource();
                res.add(overlay);
                overlay.name = s;
                s = ENG_CompilerUtil.getNextWord(fp0);
                if ((s != null) && (s.equalsIgnoreCase(BRACKET_OPEN))) {
                    incrementBracketLevel();
                } else {
                    throw new ENG_InvalidFormatParsingException();
                }
                boolean zorderSet = false;
                while (true) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    if (s.equalsIgnoreCase(ZORDER)) {
                        s = ENG_CompilerUtil.getNextWord(fp0);
                        checkNull(s);
                        try {
                            overlay.zorder = Integer.parseInt(s);
                        } catch (NumberFormatException e) {
                            throw new ENG_InvalidFormatParsingException();
                        }
                        zorderSet = true;
                    } else if (s.equalsIgnoreCase(CONTAINER)) {
                        parseContainer(fp0, overlay);
                    } else if (s.equalsIgnoreCase(ELEMENT)) {
                        throw new ENG_InvalidFormatParsingException(
                                "Cannot have element " + "as first container");
                    } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                        decrementBracketLevel();
                        break;
                    }
                }
                if (!zorderSet) {
                    // Set a default zorder??
                }
            }
            return res;
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
    }
}
