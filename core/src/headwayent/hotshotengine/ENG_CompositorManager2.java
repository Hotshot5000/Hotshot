/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import java.util.HashMap;

import headwayent.hotshotengine.renderer.ENG_Camera;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_RenderWindow;
import headwayent.hotshotengine.renderer.ENG_SceneManager;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_CompositorWorkspaceNativeWrapper;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_DynamicCubemapCompositorWorkspaceNativeWrapper;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

/**
 * Created by sebas on 18.07.2017.
 */

public class ENG_CompositorManager2 {

    private static final ENG_CompositorManager2 mgr = new ENG_CompositorManager2();
    private final HashMap<String, ENG_CompositorWorkspaceNativeWrapper> compositorMap = new HashMap<>();
    private ENG_CompositorWorkspaceNativeWrapper currentCompositor;

    private ENG_CompositorManager2() {

    }

    public ENG_CompositorWorkspaceNativeWrapper createCompositorWorkspace(
            ENG_RenderRoot renderRoot,
            ENG_SceneManager sceneManager,
            ENG_RenderWindow renderWindow,
            ENG_Camera camera,
            String workspaceName,
            boolean enabled, boolean dynamicCubemap) {
        ENG_CompositorWorkspaceNativeWrapper compositorWorkspace = null;
        if (dynamicCubemap) {
            compositorWorkspace = new ENG_DynamicCubemapCompositorWorkspaceNativeWrapper(
                    renderRoot, sceneManager, renderWindow, camera, workspaceName, enabled);
        } else {
            compositorWorkspace = new ENG_CompositorWorkspaceNativeWrapper(
                    renderRoot, sceneManager, renderWindow, camera, workspaceName, enabled);
        }
        ENG_CompositorWorkspaceNativeWrapper put = compositorMap.put(workspaceName, compositorWorkspace);
        if (put != null) {
            throw new IllegalArgumentException("Compositor name: " + workspaceName + " already exists");
        }
//        if (enabled) {
//            updateCurrentCompositor(compositorWorkspace);
//        }
        return compositorWorkspace;
    }

    public ENG_CompositorWorkspaceNativeWrapper getByName(String workspaceName) {
        ENG_CompositorWorkspaceNativeWrapper compositorWorkspaceNativeWrapper = compositorMap.get(workspaceName);
        if (compositorWorkspaceNativeWrapper == null) {
            throw new IllegalArgumentException(workspaceName + " does not exist");
        }
        return compositorWorkspaceNativeWrapper;
    }

    public void removeCompositorWorkspace(String workspaceName) {
        ENG_CompositorWorkspaceNativeWrapper remove = compositorMap.remove(workspaceName);
        if (remove == null) {
            throw new IllegalArgumentException("WorkspaceName " + workspaceName + " not found");
        }
        remove.destroy();
    }

    public void setCompositorEnabled(String workspaceName, boolean enable) {
        ENG_CompositorWorkspaceNativeWrapper compositorWorkspace = compositorMap.get(workspaceName);
        if (compositorWorkspace == null) {
            throw new IllegalArgumentException("WorkspaceName " + workspaceName + " not found");
        }
//        updateCurrentCompositor(compositorWorkspace);
        compositorWorkspace.setEnabled(enable);
    }

    private void updateCurrentCompositor(ENG_CompositorWorkspaceNativeWrapper compositorWorkspace) {
        if (currentCompositor != null) {
            setCompositorEnabled(currentCompositor.getWorkspaceName(), false);
        }
        currentCompositor = compositorWorkspace;
    }

    public static ENG_CompositorManager2 getSingleton() {
        return mgr;
    }
}
