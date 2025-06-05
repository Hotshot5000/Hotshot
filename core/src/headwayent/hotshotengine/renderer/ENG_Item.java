/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/17/21, 4:49 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;


import java.util.ArrayList;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativeClassDeferred;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

/**
 * Created by sebas on 23.06.2017.
 */

public class ENG_Item extends ENG_AttachableObject {

    public static final int AABB_SIZE_IN_BYTES = 24;

    //    private long[] ptr = new long[1];
//    private long id;
//    private String name;
    private String datablockName;
    private int visibilityFlag;
    private final ArrayList<ENG_NativeClassDeferred> subItems = new ArrayList<>();
    private final ENG_SceneManager.SceneMemoryMgrTypes type;
    private final String groupName;
//    private final ENG_AxisAlignedBox worldAABB = new ENG_AxisAlignedBox();
//    private boolean nativePtrSet;
//    protected boolean attached;

    public ENG_Item(long id, String name, String meshName, ENG_Workflows workflow) {
        this(id, name, meshName, 1, workflow);
    }

    public ENG_Item(long id, String name, String meshName, int subItemCount, ENG_Workflows workflow) {
        this(id, name, meshName, ENG_SceneManager.AUTODETECT_RESOURCE_GROUP_NAME, subItemCount,
                ENG_SceneManager.SceneMemoryMgrTypes.SCENE_DYNAMIC, workflow);
    }

    public ENG_Item(long id, String name, String meshName, String groupName, int subItemCount,
                    ENG_SceneManager.SceneMemoryMgrTypes type, ENG_Workflows workflow) {
        this.id = id;
        this.name = name;
        this.groupName = groupName;
        this.type = type;
        initializeNative(meshName, subItemCount, workflow);
    }

    private void initializeNative(String meshName, int subItemCount, ENG_Workflows workflow) {
        if (MainApp.getApplicationMode() == MainApp.Mode.CLIENT) {
            ENG_NativeCalls.sceneManager_createItem(this, meshName, groupName, type, subItemCount, workflow);
        }
    }

    public void setSubItemPtr(long ptr) {
        ENG_NativeClassDeferred subItem = new ENG_NativeClassDeferred();
        subItem.setPtr(ptr);
        subItems.add(subItem);
    }

    public ENG_NativeClassDeferred getSubItem(int pos) {
        return subItems.get(pos);
    }

    public void destroy() {
        ENG_NativeCalls.sceneManager_destroyItem(this);
        subItems.clear();
        destroyed = true;
    }

    public String getDatablockName() {
        return datablockName;
    }

    public void setDatablockName(String datablockName) {
        this.datablockName = datablockName;
        ENG_NativeCalls.item_setDatablock(this, datablockName);
    }

    public int getVisibilityFlag() {
        return visibilityFlag;
    }

    public void setVisibilityFlag(int visibilityFlag) {
        this.visibilityFlag = visibilityFlag;
        ENG_NativeCalls.item_setVisibilityFlag(this, visibilityFlag);
    }

    public ENG_SceneManager.SceneMemoryMgrTypes getType() {
        return type;
    }

    public String getGroupName() {
        return groupName;
    }

    //    public void setWorldAabb(float xCenter, float yCenter, float zCenter,
//                             float xHalfSize, float yHalfSize, float zHalfSize) {
////        worldAABB.setMin(xCenter - xHalfSize, yCenter - yHalfSize, zCenter - zHalfSize);
////        worldAABB.setMax(xCenter + xHalfSize, yCenter + yHalfSize, zCenter + zHalfSize);
//        worldAABB.setMin(xCenter, yCenter, zCenter);
//        worldAABB.setMax(xHalfSize, yHalfSize, zHalfSize);
//    }
//
//    public ENG_AxisAlignedBox getWorldAABB() {
//        return worldAABB;
//    }


}
