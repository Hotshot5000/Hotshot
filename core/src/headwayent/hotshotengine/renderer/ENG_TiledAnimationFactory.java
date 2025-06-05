/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:08 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.TreeMap;

/**
 * Created by sebas on 18-Oct-17.
 */

public class ENG_TiledAnimationFactory extends ENG_MovableObjectFactoryWithId {

    public static final String FACTORY_TYPE_NAME = "TiledAnimation";

    @Override
    protected Object createInstanceImpl(String name, TreeMap<String, String> params) {
        long lId = getIdParam(params);
        String billboardSetNative = params.get("billboardSetNative");
        String unlitMaterialName = params.get("unlitMaterialName");
        String speed = params.get("speed");
        String horizontalFramesNum = params.get("horizontalFramesNum");
        String verticalFramesNum = params.get("verticalFramesNum");
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        return new ENG_TiledAnimationNative(lId, sceneManager.getBillboardSetNative(billboardSetNative), name, unlitMaterialName,
                Float.parseFloat(speed), Integer.parseInt(horizontalFramesNum), Integer.parseInt(verticalFramesNum));
    }

    @Override
    public String getType() {
        return FACTORY_TYPE_NAME;
    }

    @Override
    public void destroyInstance(Object obj, boolean skipGLDelete) {
        ((ENG_TiledAnimationNative) obj).destroy();
    }

    /**
     * UGLY HACK THAT VIOLATES OO PRINCIPLES.
     * @param name
     * @param manager
     * @return
     */
    @Override
    public ENG_MovableObject createInstance(String name, ENG_SceneManager manager) {
        throw new UnsupportedOperationException();
    }

    /**
     * UGLY HACK THAT VIOLATES OO PRINCIPLES.
     * @param name
     * @param manager
     * @param params
     * @return
     */
    @Override
    public ENG_MovableObject createInstance(String name, ENG_SceneManager manager, TreeMap<String, String> params) {
        throw new UnsupportedOperationException();
    }
}
