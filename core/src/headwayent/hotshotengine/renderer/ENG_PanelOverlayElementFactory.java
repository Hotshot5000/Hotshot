/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public class ENG_PanelOverlayElementFactory extends ENG_OverlayElementFactory {

    @Override
    public ENG_OverlayElement createInstance(String instanceName) {
        
        return new ENG_PanelOverlayElement(instanceName);
    }

    @Override
    public String getTypeName() {
        
        return "Panel";
    }

}
