/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

/**
 * Created by sebas on 23.07.2017.
 */

public class ENG_CameraNative extends ENG_AttachableObject {

    private final ENG_Camera camera;

    public ENG_CameraNative(ENG_Camera camera) {
        this.camera = camera;
    }

    @Override
    public long getId() {
        return camera.getId();
    }

    @Override
    public String getName() {
        return camera.getName();
    }

    @Override
    public long getPointer() {
        return camera.getPointer();
    }

    @Override
    public void setPointer(long ptr) {
        camera.setPointer(ptr);
    }

    @Override
    public boolean isNativePointerSet() {
        return camera.isNativePointerSet();
    }

    @Override
    public void setNativePointer(boolean set) {
        camera.setNativePointer(set);
    }

    @Override
    public boolean isAttached() {
        return camera.isAttached();
    }

    @Override
    public void _notifyAttached(ENG_Node node) {
        camera._notifyAttached(node);
    }

    @Override
    public ENG_Node getParentNode() {
        return camera.getParentNode();
    }

    @Override
    public void detachFromParent() {
        if (isAttached()) {
            ((ENG_SceneNode) getParentNode()).detachObject(getName());
        }
    }

    //    @Override
//    public void setAttached(boolean attached) {
//        camera.setAttached(attached);
//    }
}
