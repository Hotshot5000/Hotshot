/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.classwrappers;

import headwayent.hotshotengine.ENG_IDisposable;
import headwayent.hotshotengine.android.AndroidRenderWindow;
import headwayent.hotshotengine.renderer.ENG_Camera;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_RenderWindow;
import headwayent.hotshotengine.renderer.ENG_SceneManager;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_SlowCallExecutor;

/**
 * Created by sebas on 21.03.2017.
 */

public class ENG_CompositorWorkspaceNativeWrapper extends ENG_NativeClass implements ENG_IDisposable {

    private final ENG_RenderRoot root;
    private final ENG_SceneManager sceneManager;
    private final ENG_RenderWindow renderWindow;
    private final ENG_Camera camera;
    private final String workspaceName;
    private final boolean enabled;

    public ENG_CompositorWorkspaceNativeWrapper(final ENG_RenderRoot root,
                                                final ENG_SceneManager sceneManager,
                                                final ENG_RenderWindow renderWindow,
                                                final ENG_Camera camera,
                                                final String workspaceName,
                                                final boolean enabled) {
        this.root = root;
        this.sceneManager = sceneManager;
        this.renderWindow = renderWindow;
        this.camera = camera;
        this.workspaceName = workspaceName;
        this.enabled = enabled;
    }

    public void createWorkspace() {
        ENG_SlowCallExecutor.execute(() -> {
            setPtr(addWorkspace(
                    root.getPointer(),
                    sceneManager.getPointer(),
                    ((AndroidRenderWindow) renderWindow).getPointer(),
                    camera.getPointer(),
                    workspaceName,
                    enabled));
            return 0;
        });
    }

    public void setEnabled(boolean enabled) {
        ENG_NativeCalls.compositorWorkspace_setEnabled(getPtr(), enabled);
    }

    @Override
    public void destroy() {
        removeWorkspace(root.getPointer(), getPtr());
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public ENG_RenderRoot getRoot() {
        return root;
    }

    public ENG_SceneManager getSceneManager() {
        return sceneManager;
    }

    public ENG_RenderWindow getRenderWindow() {
        return renderWindow;
    }

    public ENG_Camera getCamera() {
        return camera;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public long getCubemapTexture() {
        return 0;
    }

    public long getCubemapCamera() {
        return 0;
    }

    public long getDynamicCubemapWorkspace() {
        return 0;
    }

    public static native long addWorkspace(long rootPtr, long sceneManagerPtr, long renderWindowPtr,
                                           long cameraPtr, String workspaceName, boolean enabled);
    public static native void removeWorkspace(long rootPtr, long workspacePtr);
}
