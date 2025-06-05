/*
 * Created by Sebastian Bugiu on 28/11/2024, 12:07
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 28/11/2024, 12:07
 * Copyright (c) 2024.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.classwrappers;

import headwayent.hotshotengine.android.AndroidRenderWindow;
import headwayent.hotshotengine.renderer.ENG_Camera;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_RenderWindow;
import headwayent.hotshotengine.renderer.ENG_SceneManager;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_SlowCallExecutor;

public class ENG_DynamicCubemapCompositorWorkspaceNativeWrapper extends ENG_CompositorWorkspaceNativeWrapper {

    private static long cubemapTexture;
    private static long cubemapCamera;
    private long dynamicCubemapWorkspace;

    public ENG_DynamicCubemapCompositorWorkspaceNativeWrapper(ENG_RenderRoot root, ENG_SceneManager sceneManager, ENG_RenderWindow renderWindow, ENG_Camera camera, String workspaceName, boolean enabled) {
        super(root, sceneManager, renderWindow, camera, workspaceName, enabled);
    }

    @Override
    public void createWorkspace() {
        ENG_SlowCallExecutor.execute(() -> {
            // Only create once.
            if (cubemapTexture == 0) {
                cubemapTexture = createCubemapTexture();
            }
            // Only create once.
            if (cubemapCamera == 0) {
                cubemapCamera = createCubemapCamera(getSceneManager().getPointer());
            }
            dynamicCubemapWorkspace = createDynamicCubemapWorkspace(getRoot().getPointer(),
                    getSceneManager().getPointer(),
                    ((AndroidRenderWindow) getRenderWindow()).getPointer(),
                    getCubemapCamera(),
                    "CubemapRendererNode_" + getWorkspaceName(),
                    isEnabled());
            setPtr(addWorkspace(
                    getRoot().getPointer(),
                    getSceneManager().getPointer(),
                    ((AndroidRenderWindow) getRenderWindow()).getPointer(),
                    getCamera().getPointer(),
                    getWorkspaceName(),
                    isEnabled()));
            return 0;
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        ENG_NativeCalls.compositorWorkspace_setEnabled(dynamicCubemapWorkspace, enabled);
        super.setEnabled(enabled);
    }

    @Override
    public void destroy() {
        removeWorkspace(getRoot().getPointer(), dynamicCubemapWorkspace);
        super.destroy();
    }

    @Override
    public long getCubemapTexture() {
        return cubemapTexture;
    }

    @Override
    public long getCubemapCamera() {
        return cubemapCamera;
    }

    @Override
    public long getDynamicCubemapWorkspace() {
        return dynamicCubemapWorkspace;
    }

    public static native long createCubemapTexture();
    public static native long createCubemapCamera(long sceneManagerPtr);
    public static native long createDynamicCubemapWorkspace(long rootPtr, long sceneManagerPtr, long renderWindowPtr,
                                                            long cameraPtr, String workspaceName, boolean enabled);
    public static native long addWorkspace(long rootPtr, long sceneManagerPtr, long renderWindowPtr,
                                           long cameraPtr, String workspaceName, boolean enabled);
}
