/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_TextAreaOverlayElement.Alignment;

public class ENG_OverlayTextAreaElement extends ENG_OverlayContainerResource {

    public String fontName;
    public float charHeight;
    public ENG_ColorValue color;
    public ENG_ColorValue bottomColor;
    public ENG_ColorValue topColor;
    public Alignment alignment = Alignment.Left;
    public float spaceWidth;
}
