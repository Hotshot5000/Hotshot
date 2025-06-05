/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.world;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.renderer.ENG_LightNative;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_SceneManager;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

/**
 * Created by sebas on 13-Feb-18.
 */
public class Light {
    private final ENG_LightNative light;
    private final ENG_SceneNode lightNode;

    public Light(String lightName, String lightNodeName) {
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager("Main");
        light = sceneManager.createLight(lightName, ENG_Utility.getUniqueId());
        lightNode = sceneManager.getRootSceneNode().createChildSceneNode(lightNodeName, false);
        lightNode.attachObject(light);
    }

    public void destroy() {
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager("Main");
        sceneManager.getRootSceneNode().removeAndDestroyChild(lightNode.getName());
        sceneManager.destroyLight(light);
    }

    public ENG_LightNative getLight() {
        return light;
    }

    public ENG_SceneNode getLightNode() {
        return lightNode;
    }
}
