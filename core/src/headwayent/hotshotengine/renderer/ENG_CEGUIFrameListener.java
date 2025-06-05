/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public class ENG_CEGUIFrameListener extends ENG_FrameListener {

    private boolean enabled;

    public ENG_CEGUIFrameListener() {
        
    }

    @Override
    public boolean frameStarted(ENG_FrameEvent evt) {
        
        return true;
    }

    @Override
    public boolean frameRenderingQueued(ENG_FrameEvent evt) {
        
        if (enabled) {
            renderAllGUIContexts();
        }

        return true;
    }

    @Override
    public boolean frameEnded(ENG_FrameEvent evt) {
        
        return true;
    }

    public void setCEGUIRenderEnabled(boolean b) {
        enabled = b;
    }

    public boolean isCEGUIRenderEnabled() {
        return enabled;
    }

    private native void renderAllGUIContexts();

    static {
        System.loadLibrary("");
    }

}
