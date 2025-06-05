/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix3;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Radian;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.renderer.ENG_RenderQueue.RenderQueueGroupID;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointerWithSetter;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

import java.util.Iterator;
import java.util.LinkedList;

public class ENG_Overlay implements ENG_NativePointerWithSetter {

    protected String mName;
    /// Internal root node, used as parent for 3D objects
    protected final ENG_SceneNode mRootNode = new ENG_SceneNode(null);
    // 2D elements
    // OverlayContainers, linked list for easy sorting by zorder later
    // Not a map because sort can be saved since changes infrequent (unlike render queue)
    protected final LinkedList<ENG_OverlayContainer> m2DElements = new LinkedList<>();

    // Degrees of rotation around center
    protected final ENG_Radian mRotate = new ENG_Radian();
    // Scroll values, offsets
    protected float mScrollX, mScrollY;
    // Scale values
    protected float mScaleX = 1.0f, mScaleY = 1.0f;

    protected final ENG_Matrix4 mTransform = new ENG_Matrix4();
    protected boolean mTransformOutOfDate = true;
    protected boolean mTransformUpdated = true;
    protected long mZOrder = 100;
    protected boolean mVisible;
    protected boolean mInitialised;
    protected String mOrigin;

    private final ENG_Matrix3 rot3x3 = new ENG_Matrix3();
    private final ENG_Matrix3 scale3x3 = new ENG_Matrix3();
    private final ENG_Matrix3 mat3 = new ENG_Matrix3();
    private final ENG_Vector3D vec3 = new ENG_Vector3D();

    private final long[] ptr = new long[1];
    private long id;
    private String name;
    private boolean nativePtrSet;

    public Iterator<ENG_OverlayContainer> get2DContainerIterator() {
        return m2DElements.iterator();
    }

    protected void updateTransform() {
        rot3x3.setIdentity();
        scale3x3.set(ENG_Math.MAT3_ZERO);
        rot3x3.fromEulerAnglesXYZ(0.0f, 0.0f, mRotate.valueRadians());

        scale3x3.set(0, 0, mScaleX);
        scale3x3.set(1, 1, mScaleY);
        scale3x3.set(2, 2, 1.0f);

        mTransform.setIdentity();
        rot3x3.concatenate(scale3x3, mat3);
        mTransform.set(mat3);

        vec3.set(mScrollX, mScrollY, 0.0f);
        mTransform.setTrans(vec3);
        mTransformOutOfDate = false;
    }

    protected void initialise() {
        for (ENG_OverlayContainer c : m2DElements) {
            c.initialise();
        }
        mInitialised = true;
    }

    public void destroy(boolean skipGLDelete) {
//        for (ENG_OverlayContainer c : m2DElements) {
//            c.destroy(skipGLDelete);
//        }
        ENG_NativeCalls.overlayManager_destroyOverlay(this);
        mInitialised = false;
    }

    protected void assignZOrders() {
        short zorder = (short) (mZOrder * 100.0f);

        for (ENG_OverlayContainer c : m2DElements) {
            zorder = c._notifyZOrder(zorder);
        }
    }

    public ENG_Overlay() {

    }

    public ENG_Overlay(String name) {
        mName = name;
        ENG_NativeCalls.overlayManager_getByName(this);
    }

    public void setName(String name) {
        mName = name;
    }

    public ENG_OverlayContainer getChild(String name) {
        for (ENG_OverlayContainer c : m2DElements) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public String getName() {
        return mName;
    }

    public void setZOrder(short zorder) {
        // Limit to 650 since this is multiplied by 100 to pad out for containers
        assert (zorder <= 650);

        mZOrder = zorder;

        assignZOrders();
    }

    public short getZOrder() {
        return (short) mZOrder;
    }

    public boolean isVisible() {
        return mVisible;
    }

    public boolean isInitialised() {
        return mInitialised;
    }

    public void show() {
        if (mVisible) {
            return;
        }
        mVisible = true;
//        if (!mInitialised) {
//            initialise();
//        }
        ENG_NativeCalls.overlay_show(this);
    }

    public void hide() {
        if (!mVisible) {
            return;
        }
        mVisible = false;
        ENG_NativeCalls.overlay_hide(this);
    }

    public void add2DNative(ENG_OverlayContainer cont) {
        m2DElements.add(cont);
    }

    public void remove2DNative(ENG_OverlayContainer cont) {
        m2DElements.remove(cont);
    }

    public void add2D(ENG_OverlayContainer cont) {
        m2DElements.add(cont);
        // Notify parent
        cont._notifyParent(null, this);

        assignZOrders();

        ENG_Matrix4 xform = new ENG_Matrix4();
        _getWorldTransforms(xform);
        cont._notifyWorldTransforms(xform);
        cont._notifyViewport();
    }

    public void remove2D(ENG_OverlayContainer cont) {
        m2DElements.remove(cont);
        cont._notifyParent(null, null);
        assignZOrders();
    }

    public void add3D(ENG_SceneNode node) {
        mRootNode.addChild(node);
    }

    public void remove3D(ENG_SceneNode node) {
        mRootNode.removeChild(node);
    }

    public void clear() {
        mRootNode.removeAllChildren();
        m2DElements.clear();
    }

    public void setScroll(float x, float y) {
        mScrollX = x;
        mScrollY = y;
        mTransformOutOfDate = true;
        mTransformUpdated = true;
    }

    public float getScrollX() {
        return mScrollX;
    }

    public float getScrollY() {
        return mScrollY;
    }

    public void scroll(float xoff, float yoff) {
        mScrollX += xoff;
        mScrollY += yoff;
        mTransformOutOfDate = true;
        mTransformUpdated = true;
    }

    public void setRotate(ENG_Radian angle) {
        mRotate.set(angle);
        mTransformOutOfDate = true;
        mTransformUpdated = true;
    }

    public void getRotate(ENG_Radian ret) {
        ret.set(mRotate);
    }

    public ENG_Radian getRotate() {
        return new ENG_Radian(mRotate);
    }

    public void rotate(ENG_Radian angle) {
        setRotate(new ENG_Radian(mRotate.valueRadians() + angle.valueRadians()));
    }

    public void setScale(float x, float y) {
        mScaleX = x;
        mScaleY = y;
        mTransformOutOfDate = true;
        mTransformUpdated = true;
    }

    public float getScaleX() {
        return mScaleX;
    }

    public float getScaleY() {
        return mScaleY;
    }

    public void _getWorldTransforms(ENG_Matrix4 xform) {
        
        if (mTransformOutOfDate) {
            updateTransform();
        }
        xform.set(mTransform);
    }

    public void _findVisibleObjects(ENG_Camera cam, ENG_RenderQueue queue) {
        if (ENG_OverlayManager.getSingleton().hasViewportChanged()) {
            for (ENG_OverlayContainer o : m2DElements) {
                o._notifyViewport();
            }
        }

        // update elements
        if (mTransformUpdated) {
            ENG_Matrix4 xform = new ENG_Matrix4();
            _getWorldTransforms(xform);

            for (ENG_OverlayContainer o : m2DElements) {
                o._notifyWorldTransforms(xform);
            }
            mTransformUpdated = false;
        }

        if (mVisible) {
            mRootNode.setPosition(cam.getDerivedPosition());
            mRootNode.setOrientation(cam.getDerivedOrientation());
            mRootNode._update(true, false);

            byte oldgrp = queue.getDefaultQueueGroup();
            short oldPriority = queue.getDefaultRenderablePriority();
            queue.setDefaultQueueGroup(RenderQueueGroupID.RENDER_QUEUE_OVERLAY.getID());
            queue.setDefaultRenderablePriority((short) ((mZOrder * 100) - 1));
            mRootNode._findVisibleObjects(cam, queue, null, true, false, false);
            // Reset the group
            queue.setDefaultQueueGroup(oldgrp);
            queue.setDefaultRenderablePriority(oldPriority);

            for (ENG_OverlayContainer o : m2DElements) {
                o._update();
                o._updateRenderQueue(queue);
            }
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
}
