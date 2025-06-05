/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.helpers;

import java.util.ArrayList;

import headwayent.hotshotengine.renderer.ENG_Node;
import headwayent.hotshotengine.renderer.ENG_SceneManager;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

/**
 * Created by sebas on 21.06.2017.
 */

public class ENG_RootSceneNode extends ENG_SceneNode {

    // All the added nodes also go into this vector.
    // After all the final positions have been set, this vector will be used
    // to send the data to the native side, eliminating the need to go through the node tree.
    private final ArrayList<ENG_SceneNode> sceneNodeList = new ArrayList<>();

    public ENG_RootSceneNode(ENG_SceneManager creator) {
        super(creator);
    }

    public ENG_RootSceneNode(String name, ENG_SceneManager creator) {
        super(name, creator);
    }

    @Override
    public void addChild(ENG_Node child) {
        super.addChild(child);
        // Only scene nodes are allowed in the root scene node.
        sceneNodeList.add((ENG_SceneNode) child);
    }

    @Override
    public ENG_Node removeChild(ENG_Node node) {
        ENG_SceneNode child = (ENG_SceneNode) super.removeChild(node);
        sceneNodeList.remove(child);
        return child;
    }

    @Override
    public void removeAllChildren() {
        super.removeAllChildren();
        sceneNodeList.clear();
    }

    @Override
    public void removeAndDestroyAllChildren() {
        super.removeAndDestroyAllChildren();
        sceneNodeList.clear();
    }
}
