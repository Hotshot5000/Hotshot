/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/2/18, 10:54 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_AxisAlignedBox;

/**
 * Created by sebas on 24-Oct-17.
 */

public class ENG_AttachableObject implements ENG_AttachableObjectIntf {

    protected ENG_Node parentNode;
    protected final long[] ptr = new long[1];
    protected long id;
    protected String name;
    protected boolean nativePtrSet;
//    protected boolean attached;
    private final ENG_AxisAlignedBox worldAABB = new ENG_AxisAlignedBox();
    protected boolean destroyed;

    @Override
    public boolean isAttached() {
        return parentNode != null;
    }

//    @Override
//    public void setAttached(boolean attached) {
//        this.attached = attached;
//    }

    @Override
    public void _notifyAttached(ENG_Node node) {
        parentNode = node;
    }

    @Override
    public ENG_Node getParentNode() {
        return parentNode;
    }

    @Override
    public void detachFromParent() {
        if (isAttached()) {
            ((ENG_SceneNode) parentNode).detachObject(name);
        }
    }

    @Override
    public long getPointer() {
        return ptr[0];
    }

    @Override
    public void setPointer(long ptr) {
        this.ptr[0] = ptr;
    }

    @Override
    public boolean isNativePointerSet() {
        return nativePtrSet;
    }

    @Override
    public void setNativePointer(boolean set) {
        nativePtrSet = set;
    }

    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setWorldAabb(float xCenter, float yCenter, float zCenter,
                             float xHalfSize, float yHalfSize, float zHalfSize) {
//        worldAABB.setMin(xCenter - xHalfSize, yCenter - yHalfSize, zCenter - zHalfSize);
//        worldAABB.setMax(xCenter + xHalfSize, yCenter + yHalfSize, zCenter + zHalfSize);
        worldAABB.setMin(xCenter, yCenter, zCenter);
        worldAABB.setMax(xHalfSize, yHalfSize, zHalfSize);
    }

    public ENG_AxisAlignedBox getWorldAABB() {
        return worldAABB;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
