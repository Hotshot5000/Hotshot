/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/19/20, 12:02 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.TreeMap;

/**
 * Created by sebas on 28.06.2017.
 */

public class ENG_ItemFactory extends ENG_MovableObjectFactoryWithId {

    public static final String FACTORY_TYPE_NAME = "Item";

    @Override
    protected Object createInstanceImpl(String name, TreeMap<String, String> params) {
        String mesh = params.get(ENG_SceneManager.MOVABLE_OBJECT_MESH_NAME);
        long lId = getIdParam(params);
        String pbsWorkflow = params.get(ENG_SceneManager.MOVABLE_OBJECT_PARAM_PBSWORKFLOW);
        ENG_Workflows workflow = ENG_Workflows.SpecularWorkflow;
        if (pbsWorkflow != null) {
            int workflowByte = Integer.parseInt(pbsWorkflow);
            workflow = ENG_Workflows.toWorkflow(workflowByte);
        }
        String sceneMemoryManagerType = params.get(ENG_SceneManager.MOVABLE_OBJECT_SCENE_MEMORY_MANAGER_TYPE);
        ENG_SceneManager.SceneMemoryMgrTypes type = ENG_SceneManager.SceneMemoryMgrTypes.SCENE_DYNAMIC;
        if (sceneMemoryManagerType != null) {
            type = ENG_SceneManager.SceneMemoryMgrTypes.getType(sceneMemoryManagerType);
        }
        String groupName = params.get(ENG_SceneManager.MOVABLE_OBJECT_GROUP_NAME);
        if (groupName == null || groupName.isEmpty()) {
            groupName = ENG_SceneManager.AUTODETECT_RESOURCE_GROUP_NAME;
        }
        int subMeshCount = 1;
        String subMeshCountStr = params.get(ENG_SceneManager.MOVABLE_OBJECT_SUBMESH_COUNT);
        if (subMeshCountStr != null) {
            subMeshCount = Integer.parseInt(subMeshCountStr);
        }
        return new ENG_Item(lId, name, mesh, groupName, subMeshCount, type, workflow);
    }

    @Override
    public String getType() {
        return FACTORY_TYPE_NAME;
    }

    @Override
    public void destroyInstance(Object obj, boolean skipGLDelete) {
        ((ENG_Item) obj).destroy();
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
