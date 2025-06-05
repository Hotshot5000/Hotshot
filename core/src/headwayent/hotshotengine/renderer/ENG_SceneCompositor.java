/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.basictypes.ENG_Long;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_CompositorWorkspaceNativeWrapper;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_SceneCompositorWrapper;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

/**
 * Created by sebas on 15-Nov-17.
 */

public class ENG_SceneCompositor {

    private static final ENG_SceneCompositor compositor = new ENG_SceneCompositor();
    private final ENG_SceneCompositorWrapper wrapper = new ENG_SceneCompositorWrapper();

    private ENG_SceneCompositor() {

    }

    public void insertNode(ENG_CompositorWorkspaceNativeWrapper compositorWorkspace, String workspaceName,
                           String baseNodeName, String nodeToInsertName, String previousNodeName,
                           ENG_ColorValue startColor, float scaleStep, ENG_Long sceneCompositorId) {
        ENG_NativeCalls.sceneCompositor_insertNode(wrapper.getPtr(), compositorWorkspace.getPtr(),
                workspaceName, baseNodeName, nodeToInsertName, previousNodeName, startColor, scaleStep, sceneCompositorId);
    }

    public void revertNode(ENG_Long sceneCompositorId) {
        ENG_NativeCalls.sceneCompositor_revertNode(wrapper.getPtr(), sceneCompositorId.getValue());
    }

    public static ENG_SceneCompositor getSingleton() {
        return compositor;
    }
}
