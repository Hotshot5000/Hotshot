/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Matrix4;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

public abstract class ENG_OverlayContainer extends ENG_OverlayElement {

    protected final TreeMap<String, ENG_OverlayElement> mChildren = new TreeMap<>();

    protected final TreeMap<String, ENG_OverlayContainer> mChildContainers = new TreeMap<>();

    protected boolean mChildrenProcessEvents = true;

    public ENG_OverlayContainer(String name) {
        
        super(name);
    }

    public void addChildNative(ENG_OverlayElement o) {
        if (o.isContainer()) {
            addChildImplNative((ENG_OverlayContainer) o);
        } else {
            addChildImplNative(o);
        }
    }

    private void addChildImplNative(ENG_OverlayContainer o) {
        addChildImplNative((ENG_OverlayElement) o);
        mChildContainers.put(o.getName(), o);
    }

    private void addChildImplNative(ENG_OverlayElement o) {
        if (mChildren.get(o.getName()) != null) {
            throw new IllegalArgumentException("duplicate element");
        }
        mChildren.put(o.getName(), o);
    }

    public void removeChildNative(String name) {
        ENG_OverlayElement element = mChildren.get(name);
        if (element == null) {
            throw new IllegalArgumentException(name + " is not found");
        }
        mChildren.remove(name);
        mChildContainers.remove(name);
    }

    public void addChild(ENG_OverlayElement o) {
        if (o.isContainer()) {
            addChildImpl((ENG_OverlayContainer) o);
        } else {
            addChildImpl(o);
        }
    }

    private void addChildImpl(ENG_OverlayElement o) {

        if (mChildren.get(o.getName()) != null) {
            throw new IllegalArgumentException("duplicate element");
        }
        mChildren.put(o.getName(), o);

        // tell child about parent & ZOrder
        o._notifyParent(this, mOverlay);
        o._notifyZOrder((short) (mZOrder + 1));
        o._notifyWorldTransforms(mXForm);
        o._notifyViewport();
    }

    private void addChildImpl(ENG_OverlayContainer o) {

        addChildImpl((ENG_OverlayElement) o);
        mChildContainers.put(o.getName(), o);
    }

    public void removeChild(String name) {
        ENG_OverlayElement element = mChildren.get(name);
        if (element == null) {
            throw new IllegalArgumentException(name + " is not found");
        }
        mChildren.remove(name);
        mChildContainers.remove(name);
        element._setParent(null);
    }

    public ENG_OverlayElement getChild(String name) {
        ENG_OverlayElement element = mChildren.get(name);
        if (element == null) {
            throw new IllegalArgumentException(name + " not found in list");
        }
        return element;
    }

    @Override
    public void initialise() {

        for (ENG_OverlayElement o : mChildren.values()) {
            o.initialise();
        }

        for (ENG_OverlayContainer c : mChildContainers.values()) {
            c.initialise();
        }
    }

    @Override
    public void destroy(boolean skipGLDelete) {
        for (ENG_OverlayElement o : mChildren.values()) {
            o.destroy(skipGLDelete);
        }

        for (ENG_OverlayContainer c : mChildContainers.values()) {
            c.destroy(skipGLDelete);
        }
    }

    public Iterator<Entry<String, ENG_OverlayElement>> getChildIterator() {
        return mChildren.entrySet().iterator();
    }

    public Iterator<ENG_OverlayElement> getChildValuesIterator() {
        return mChildren.values().iterator();
    }

    public Iterator<Entry<String, ENG_OverlayContainer>> getChildContainerIterator() {
        return mChildContainers.entrySet().iterator();
    }

    public Iterator<ENG_OverlayContainer> getChildContainerValuesIterator() {
        return mChildContainers.values().iterator();
    }

    @Override
    public void _positionsOutOfDate() {

        super._positionsOutOfDate();

        for (ENG_OverlayElement o : mChildren.values()) {
            o._positionsOutOfDate();
        }
    }

    public void _notifyParent(ENG_OverlayContainer parent, ENG_Overlay overlay) {

        super._notifyParent(parent, overlay);
        for (ENG_OverlayElement o : mChildren.values()) {
            o._notifyParent(this, overlay);
        }
    }

    public void _notifyWorldTransforms(ENG_Matrix4 xform) {

        super._notifyWorldTransforms(xform);
        for (ENG_OverlayElement o : mChildren.values()) {
            o._notifyWorldTransforms(xform);
        }
    }

    public void _notifyViewport() {

        super._notifyViewport();
        for (ENG_OverlayElement o : mChildren.values()) {
            o._notifyViewport();
        }
    }

    public void _update() {

        super._update();
        for (ENG_OverlayElement o : mChildren.values()) {
            o._update();
        }

    }

    public void _updateRenderQueue(ENG_RenderQueue queue) {

        super._updateRenderQueue(queue);
        for (ENG_OverlayElement o : mChildren.values()) {
            o._updateRenderQueue(queue);
        }
    }

    @Override
    public boolean isContainer() {

        return true;
    }

    public boolean isChildrenProcessEvents() {
        return mChildrenProcessEvents;
    }

    public void setChildrenProcessEvents(boolean b) {
        mChildrenProcessEvents = b;
    }

    public short _notifyZOrder(short zorder) {

        super._notifyZOrder(zorder);
        ++zorder;

        for (ENG_OverlayElement o : mChildren.values()) {
            zorder = o._notifyZOrder(zorder);
        }
        return zorder;
    }

    @Override
    public ENG_OverlayElement findElementAt(float x, float y) {

        ENG_OverlayElement ret = null;

        int currZ = -1;

        if (mVisible) {
            ret = super.findElementAt(x, y);
            if (ret != null && mChildrenProcessEvents) {
                for (ENG_OverlayElement o : mChildren.values()) {
                    if (o.isVisible() && o.isEnabled()) {
                        int z = o.getZOrder();
                        if (z > currZ) {
                            ENG_OverlayElement elementFound = o.findElementAt(x, y);
                            if (elementFound != null) {
                                currZ = z;
                                ret = elementFound;
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public ENG_OverlayElement clone(String instanceName) {

        ENG_OverlayContainer newContainer = (ENG_OverlayContainer) super.clone(instanceName);
        for (ENG_OverlayElement o : mChildren.values()) {
            if (o.isCloneable()) {
                ENG_OverlayElement newElement = o.clone(instanceName);
                newContainer.addChild(newElement);
            }
        }
        return newContainer;

    }

}
