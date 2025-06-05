/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/10/21, 10:01 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.compositor;

import headwayent.blackholedarksun.APP_Game;
import headwayent.hotshotengine.ENG_CompositorManager2;
import headwayent.hotshotengine.basictypes.ENG_Long;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_SceneCompositor;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_CompositorWorkspaceNativeWrapper;

/**
 * Created by sebas on 18.07.2017.
 */

public class SceneCompositor {

    private static final String DEFAULT_COMP = "HotshotDefaultWorkspace";
    private static final String SHIP_HIT_COMP = "HotshotDefaultWorkspace";
    private static final String SKYBOX_WORKSPACE = "SkyboxWorkspace";
    private static final String SKYBOX_NODE = "SkyboxNode";

    private static final float SCALE_STEP = 0.1f;

    public static final ENG_Long whiteCompositorId = new ENG_Long(1);
    public static final ENG_Long redCompositorId = new ENG_Long(2);

    public enum CompositorColor {
        RED, WHITE
    }

    private ENG_ColorValue getColor(CompositorColor compositorColor) {
        switch (compositorColor) {
            case RED:
                return ENG_ColorValue.RED;
            case WHITE:
                return ENG_ColorValue.WHITE;
            default:
                throw new IllegalArgumentException();
        }
    }

    private enum CompositorEnum {
        MENU,
        IN_GAME
    }

    private static class Workspace {
        public final CompositorEnum compositorEnum;
        public final String compositorName;
        public final int skyboxWorkspaceNum;

        public Workspace(CompositorEnum compositorEnum, String compositorName, int skyboxWorkspaceNum) {
            this.compositorEnum = compositorEnum;
            this.compositorName = compositorName;
            this.skyboxWorkspaceNum = skyboxWorkspaceNum;
        }
    }

    private static final SceneCompositor sceneCompositor = new SceneCompositor();
    private Workspace currentWorkspace;

    private SceneCompositor() {

    }

    public ENG_CompositorWorkspaceNativeWrapper createCompositorWorkspace(
            String workspaceName,
            boolean enabled) {
        return createCompositorWorkspace(workspaceName, enabled, false);
    }

    public ENG_CompositorWorkspaceNativeWrapper createCompositorWorkspace(
            String workspaceName,
            boolean enabled, boolean dynamicCubemap) {
        if (enabled) {
            disableCurrentCompositor();
            CompositorEnum compEnum = null;
            int skyboxWorkspaceNum = -1;
            if (workspaceName.equals(DEFAULT_COMP)) {
                compEnum = CompositorEnum.MENU;
            } else if (workspaceName.startsWith(SKYBOX_WORKSPACE)) {
                compEnum = CompositorEnum.IN_GAME;
                String skyboxNumStr = workspaceName.substring(workspaceName.indexOf(SKYBOX_WORKSPACE) + SKYBOX_WORKSPACE.length());
                skyboxWorkspaceNum = Integer.parseInt(skyboxNumStr);
            } else {
                throw new IllegalArgumentException(workspaceName + " cannot be enabled from the start");
            }
            currentWorkspace = new Workspace(compEnum, workspaceName, skyboxWorkspaceNum);
        }
        ENG_RenderRoot renderRoot = ENG_RenderRoot.getRenderRoot();
        ENG_CompositorWorkspaceNativeWrapper compositorWorkspace = compositorWorkspace =
                ENG_CompositorManager2.getSingleton().createCompositorWorkspace(renderRoot,
                renderRoot.getSceneManager(), renderRoot.getCurrentRenderWindow(),
                renderRoot.getSceneManager().getCamera(APP_Game.MAIN_CAM),
                workspaceName, enabled, dynamicCubemap);
        compositorWorkspace.createWorkspace();
        return compositorWorkspace;
    }

    public void removeCompositorWorkspace(String workspaceName) {
        if (currentWorkspace != null && currentWorkspace.compositorName.equals(workspaceName)) {
            currentWorkspace = null;
        }
        ENG_CompositorManager2.getSingleton().removeCompositorWorkspace(workspaceName);
    }

    private void disableCurrentCompositor() {
        if (currentWorkspace != null) {
            ENG_CompositorManager2.getSingleton().setCompositorEnabled(currentWorkspace.compositorName, false);
        }
    }

    /**
     * No longer working when using dynamic cubemap compositor.
     *  @param compositorColor
     * @param sceneCompositorId used to identify which compositor to remove.
     * @param totalAnimTime
     */
    public void addColoredCompositor(CompositorColor compositorColor, ENG_Long sceneCompositorId, long totalAnimTime) {
//        ENG_SceneCompositor.getSingleton().insertNode(
//                ENG_CompositorManager2.getSingleton().getByName(currentWorkspace.compositorName),
//                currentWorkspace.compositorName,
//                SKYBOX_NODE + currentWorkspace.skyboxWorkspaceNum,
//                "Colored",
//                "DefaultFinalComposition",
//                getColor(compositorColor),
//                1000.0f / totalAnimTime,
//                sceneCompositorId);
    }

    /**
     * No longer working when using dynamic cubemap compositor.
     * @param sceneCompositorId used to identify if the current compositor should be removed. -1 forces the current compositor to
     *                          be removed.
     */
    public void removeColoredCompositor(ENG_Long sceneCompositorId) {
//        ENG_SceneCompositor.getSingleton().revertNode(sceneCompositorId);
    }

    public void setDefaultCompositor() {
        disableCurrentCompositor();
        ENG_CompositorManager2.getSingleton().setCompositorEnabled(DEFAULT_COMP, true);
        currentWorkspace = new Workspace(CompositorEnum.MENU, DEFAULT_COMP, 0);
    }

    public void setInGameCompositor(String workspaceName) {
        disableCurrentCompositor();
        ENG_CompositorManager2.getSingleton().setCompositorEnabled(workspaceName, true);
        String skyboxNumStr = workspaceName.substring(workspaceName.indexOf(SKYBOX_WORKSPACE) + SKYBOX_WORKSPACE.length());
        int skyboxWorkspaceNum = Integer.parseInt(skyboxNumStr);
        currentWorkspace = new Workspace(CompositorEnum.IN_GAME, workspaceName, skyboxWorkspaceNum);
    }

    public void setShipHitCompositor(boolean enabled) {
        ENG_CompositorManager2.getSingleton().setCompositorEnabled(DEFAULT_COMP, enabled);
    }

    public static SceneCompositor getSingleton() {
        return sceneCompositor;
    }
}
