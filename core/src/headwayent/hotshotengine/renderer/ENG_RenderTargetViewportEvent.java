/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public class ENG_RenderTargetViewportEvent {

    public ENG_Viewport source;

    public ENG_RenderTargetViewportEvent() {

    }

    public ENG_RenderTargetViewportEvent(ENG_Viewport source) {
        this.source = source;
    }
}
