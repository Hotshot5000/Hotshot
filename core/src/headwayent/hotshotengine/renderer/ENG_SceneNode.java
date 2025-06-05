/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/17/21, 5:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainActivity;
import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_AxisAlignedBox;
import headwayent.hotshotengine.ENG_IDisposable;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointerWithSetter;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

import java.util.ArrayList;
import java.util.HashMap;

public class ENG_SceneNode extends ENG_Node implements ENG_NativePointerWithSetter, ENG_IDisposable {

    private long ptr;
    private final HashMap<String, ENG_AttachableObjectIntf> objectList = new HashMap<>();
    private final ENG_SceneManager creator;
    private final ENG_AxisAlignedBox worldAABB = new ENG_AxisAlignedBox();
    private final ENG_Vector4D yawFixedAxis = new ENG_Vector4D();
    //	private ENG_SceneNode parentNode;
    private boolean inSceneGraph;
    private boolean yawFixed;
    private boolean nativePointerSet;
//    private boolean ignoreOrientation;

    public ENG_SceneNode(ENG_SceneManager creator) {
        
        this.creator = creator;
        needUpdate();
        initializeNative();
    }

    public ENG_SceneNode(String name, ENG_SceneManager creator) {
        this(name, creator, null);
    }

    public ENG_SceneNode(String name, ENG_SceneManager creator, ENG_SceneNode parentNode) {
        this(name, creator, ENG_SceneManager.SceneMemoryMgrTypes.SCENE_DYNAMIC, parentNode);
    }

    public ENG_SceneNode(String name, ENG_SceneManager creator, ENG_SceneManager.SceneMemoryMgrTypes type, ENG_SceneNode parentNode) {
        super(name, type);
        
        this.creator = creator;
        needUpdate();
        if (MainApp.getApplicationMode() == MainApp.Mode.CLIENT) {
            if (parentNode != null) {
                ENG_NativeCalls.sceneNode_createChildSceneNode(parentNode, this, type, ENG_Math.VEC3_ZERO, ENG_Math.QUAT_IDENTITY);
            } else {
                initializeNative();
            }
        }
    }

    /**
     * Only used for passing the real root node from native side to java side.
     * @param name
     * @param creator
     * @param ptr
     */
    public ENG_SceneNode(String name, ENG_SceneManager creator, long ptr) {
        super(name);
        
        this.creator = creator;
        needUpdate();
        setPointer(ptr);
        setNativePointer(true);
    }

    public void initializeNative() {
        ENG_NativeCalls.sceneManager_createSceneNode(this, getType());
    }

    public void _findVisibleObjects(ENG_Camera cam, ENG_RenderQueue queue,
                                    ENG_VisibleObjectsBoundsInfo visibleBounds, boolean includeChildren,
                                    boolean displayNodes, boolean onlyShadowCasters) {
        
        throw new UnsupportedOperationException();

//        if (!cam.isVisible(worldAABB)) {
//            return;
//        }
//
//        for (Iterator<Entry<String, ENG_MovableObject>> it = objectList.entrySet().iterator();
//             it.hasNext(); ) {
//            queue.processVisibleObject(it.next().getValue(), cam,
//                    onlyShadowCasters, visibleBounds);
//        }
//
//        if (includeChildren) {
//            for (Iterator<Entry<String, ENG_Node>> it = childrenList.entrySet().iterator();
//                 it.hasNext(); ) {
//                ((ENG_SceneNode) it.next().getValue())._findVisibleObjects(cam,
//                        queue, visibleBounds, includeChildren,
//                        displayNodes, onlyShadowCasters);
//            }
//        }

//        if (displayNodes) {
//
//        }


    }

    public void _updateWithoutBoundsUpdate(boolean updateChildren, boolean parentHasChanged) {
        super._update(updateChildren, parentHasChanged);
    }


    public void _update(boolean updateChildren, boolean parentHasChanged) {
        super._update(updateChildren, parentHasChanged);
        _updateBounds();
    }

    /** @noinspection deprecation*/
    public void _updateBounds() {
//        if (Thread.currentThread().getName().startsWith("MovementSystem_thread")) {
//            System.out.println("Updating bounds for node: " + getName() + " from thread: " + Thread.currentThread().getName());
//        }
        worldAABB.setNull();
        //Merge with attached objects
        for (Object object : objectList.values()) {
            ENG_MovableObject obj = (ENG_MovableObject) object;
            worldAABB.merge(obj.getWorldBoundingBox(true));
        }
        //Merge with children
        for (ENG_Node eng_node : childrenList.values()) {
            ENG_SceneNode node = (ENG_SceneNode) eng_node;
            worldAABB.merge(node.getWorldAABB());
        }
    }

    public void setParent(ENG_Node parent) {
        super.setParent(parent);
        if (parent != null) {
            ENG_SceneNode node = (ENG_SceneNode) parent;
            setInSceneGraph(node.isInSceneGraph());
        } else {
            setInSceneGraph(false);
        }
    }

    /**
     * Hack needed because when you create a camera on the native side, that camera is added
     * to the root scene node by default, but there is no way for us to know about that on the
     * java side. So we simply added as a null and remember to detach it when we actually
     * attach it to a scene node that is not the root.
     * @param c
     */
    public void attachCamera(ENG_Camera c) {
        ENG_AttachableObjectIntf put = objectList.put(c.getName(), new ENG_CameraNative(c));
        c._notifyAttached(this);
        if (put != null) {
            throw new IllegalArgumentException("camera: " + c.getName() +
                    " is already in the list of this sceneNode");
        }
    }

    public void attachParticleSystem(ENG_AttachableObjectIntf item) {
        if (item.isAttached()) {
            throw new IllegalArgumentException("object: " + item.getName() +
                    " is already attached");
        }
//        item.setAttached(true);
        item._notifyAttached(this);
        ENG_NativeCalls.sceneNode_attachParticleSystem(this, item);
        Object prev = objectList.put(item.getName(), item);
        if (prev != null) {
            throw new IllegalArgumentException("object: " + item.getName() +
                    " is already in the list of this sceneNode");
        }
        needUpdate(false);
    }

    public void attachObject(ENG_AttachableObjectIntf item) {
        if (item.isAttached()) {
            throw new IllegalArgumentException("object: " + item.getName() +
                    " is already attached");
        }
//        item.setAttached(true);
        item._notifyAttached(this);
        ENG_NativeCalls.sceneNode_attachObject(this, item);
        Object prev = objectList.put(item.getName(), item);
        if (prev != null) {
            throw new IllegalArgumentException("object: " + item.getName() +
                    " is already in the list of this sceneNode");
        }
        needUpdate(false);
    }

    public void attachObject(ENG_MovableObject obj) {
        throw new UnsupportedOperationException();
//        if (obj.isAttached()) {
//            throw new IllegalArgumentException("object: " + obj.getName() +
//                    " is already attached");
//        }
//        obj._notifyAttached(this);
//        Object prev = objectList.put(obj.getName(), obj);
//        if (prev != null) {
//            throw new IllegalArgumentException("object: " + obj.getName() +
//                    " is already in the list of this sceneNode");
//        }
//        needUpdate(false);
    }

    public int numAttachedObjects() {
        return objectList.size();
    }

//    public ENG_MovableObject getAttachedObject(int index) {
//        if (index < objectList.size()) {
//            Iterator<ENG_MovableObject> objIt = objectList.values().iterator();
//            while ((objIt.hasNext()) && ((index--) > 0)) {
//                objIt.next();
//            }
//            return objIt.next();
//        } else {
//            throw new ArrayIndexOutOfBoundsException("index: " + index + " is out of " +
//                    objectList.size() + " array size");
//        }
//    }
//
//    public ENG_MovableObject getAttachedObject(String name) {
//        ENG_MovableObject obj = objectList.get(name);
//        if (obj == null) {
//            if (MainActivity.isDebugmode()) {
//                throw new IllegalArgumentException("name " + name +
//                        " could not be found in the object list");
//            }
//        }
//        return obj;
//    }

    public ENG_AttachableObjectIntf getAttachedObject(String name) {
        ENG_AttachableObjectIntf obj = objectList.get(name);
        if (obj == null) {
            if (MainActivity.isDebugmode()) {
                throw new IllegalArgumentException("name " + name +
                        " could not be found in the object list");
            }
        }
        return obj;
    }

    public boolean hasAttachedObject(String name) {
        return objectList.containsKey(name);
    }

//    public ENG_MovableObject detachObject(int index) {
//        if (index < objectList.size()) {
//            Iterator<ENG_MovableObject> objIt = objectList.values().iterator();
//            while ((objIt.hasNext()) && ((index--) > 0)) {
//                objIt.next();
//            }
//            ENG_MovableObject obj = objIt.next();
//            objectList.remove(obj.getName());
//            obj._notifyAttached(null);
//            needUpdate(false);
//            return obj;
//        } else {
//            throw new ArrayIndexOutOfBoundsException("index: " + index + " is out of " +
//                    objectList.size() + " array size");
//        }
//    }

    public void detachObject(String name) {
        ENG_AttachableObjectIntf obj = objectList.remove(name);
        if (obj != null) {
//            obj._notifyAttached(null);
//            needUpdate(false);
            ENG_NativeCalls.sceneNode_detachObject(this, obj);
            obj._notifyAttached(null);
        } else {
            if (MainActivity.isDebugmode()) {
                throw new IllegalArgumentException("name " + name +
                        " could not be found in the object list");
            }
        }
//        return obj;
    }

    public void detachObject(ENG_MovableObject obj) {
        detachObject(obj.getName());
    }

    public void detachAllObjects() {
        for (ENG_AttachableObjectIntf obj : objectList.values()) {

//            obj._notifyAttached(null);
        }
        ENG_NativeCalls.sceneNode_detachAllObjects(this);
        objectList.clear();
        needUpdate(false);
    }

//	public void _updateFromParentImpl() {

//	}


    @Override
    protected ENG_Node createChildImpl(String name, ENG_SceneManager.SceneMemoryMgrTypes type, boolean updateEveryFrame) {
        if (MainActivity.isDebugmode()) {
            if (creator == null) {
                throw new NullPointerException("scene manager isn't set");
            }
        }
        return creator.createSceneNode(name, this, type, updateEveryFrame);
    }

    @Override
    protected ENG_Node createChildImpl(String name, boolean updateEveryFrame) {

        if (MainActivity.isDebugmode()) {
            if (creator == null) {
                throw new NullPointerException("scene manager isn't set");
            }
        }
        return creator.createSceneNode(name, this, updateEveryFrame);
    }

    @Override
    protected ENG_Node createChildImpl(boolean updateEveryFrame) {

        if (MainActivity.isDebugmode()) {
            if (creator == null) {
                throw new NullPointerException("scene manager isn't set");
            }
        }
        return creator.createSceneNode(updateEveryFrame);

    }

//    public Iterator<Entry<String, ENG_MovableObject>> getAttachedObjectIterator() {
//        return objectList.entrySet().iterator();
//    }

    public void removeAndDestroyAllChildren() {
        // To avoid ConcurrentModificationException.
        HashMap<String, ENG_Node> childrenListCopy = new HashMap<>(childrenList);
        for (ENG_Node eng_node : childrenListCopy.values()) {
            ENG_SceneNode node = (ENG_SceneNode) eng_node;
            node.removeAndDestroyAllChildren();
            node.getCreator().destroySceneNode(node);
        }
        childrenList.clear();
        needUpdate(false);
        ENG_NativeCalls.sceneNode_removeAndDestroyAllChildren(this);
    }

    public void removeAndDestroyChild(String name) {
        ENG_SceneNode node = (ENG_SceneNode) getChild(name);
        removeAndDestroyChildIntern(node);
    }

    public void removeAndDestroyChild(int index) {
        ENG_SceneNode node = (ENG_SceneNode) getChild(index);
        removeAndDestroyChildIntern(node);
    }

    private void removeAndDestroyChildIntern(ENG_SceneNode node) {
        node.removeAndDestroyAllChildren();
        removeChild(node.getName());
        node.getCreator().destroySceneNode(node.getName());
        ENG_NativeCalls.sceneNode_removeAndDestroyChild(this, node);
    }

    public void _notifyRootNode() {
        inSceneGraph = true;
    }

    /** @noinspection deprecation*/
    public void findLigths(ArrayList<ENG_Light> destList, float radius, int lightMask) {
        if (creator != null) {
            creator._populateLightList(this, radius, destList, lightMask);
        } else {
            destList.clear();
        }
    }

    public ENG_SceneNode createChildSceneNode(boolean updateEveryFrame) {
        return createChildSceneNode(ENG_Math.PT4_ZERO, ENG_Math.QUAT_IDENTITY, updateEveryFrame);
    }

    public ENG_SceneNode createChildSceneNode(
            ENG_Vector4D translate, ENG_Quaternion rotation, boolean updateEveryFrame) {
        return (ENG_SceneNode) createChild(translate, rotation, updateEveryFrame);
    }

    public ENG_SceneNode createChildSceneNode(String name) {
        return createChildSceneNode(name, ENG_SceneManager.SceneMemoryMgrTypes.SCENE_DYNAMIC);
    }

    public ENG_SceneNode createChildSceneNode(String name, ENG_SceneManager.SceneMemoryMgrTypes type) {
        return createChildSceneNode(name, type, ENG_Math.PT4_ZERO, ENG_Math.QUAT_IDENTITY, true);
    }

    public ENG_SceneNode createChildSceneNode(String name, boolean updateEveryFrame) {
        return createChildSceneNode(name, ENG_SceneManager.SceneMemoryMgrTypes.SCENE_DYNAMIC, updateEveryFrame);
    }

    public ENG_SceneNode createChildSceneNode(String name, ENG_SceneManager.SceneMemoryMgrTypes type,
                                              boolean updateEveryFrame) {
        return createChildSceneNode(name, type, ENG_Math.PT4_ZERO, ENG_Math.QUAT_IDENTITY, updateEveryFrame);
    }

    public ENG_SceneNode createChildSceneNode(String name,
                                              ENG_Vector4D translate, ENG_Quaternion rotation) {
        return createChildSceneNode(name, ENG_SceneManager.SceneMemoryMgrTypes.SCENE_DYNAMIC, translate, rotation);
    }

    public ENG_SceneNode createChildSceneNode(String name, ENG_SceneManager.SceneMemoryMgrTypes type,
                                              ENG_Vector4D translate, ENG_Quaternion rotation) {
        return (ENG_SceneNode) createChild(name, type, translate, rotation, true);
    }

    public ENG_SceneNode createChildSceneNode(String name,
                                              ENG_Vector4D translate, ENG_Quaternion rotation, boolean updateEveryFrame) {
        return createChildSceneNode(name, ENG_SceneManager.SceneMemoryMgrTypes.SCENE_DYNAMIC, translate, rotation, updateEveryFrame);
    }

    public ENG_SceneNode createChildSceneNode(String name, ENG_SceneManager.SceneMemoryMgrTypes type,
                                              ENG_Vector4D translate, ENG_Quaternion rotation, boolean updateEveryFrame) {
        return (ENG_SceneNode) createChild(name, type, translate, rotation, updateEveryFrame);
    }

    public void setFixedYawAxis(boolean fixed, ENG_Vector4D axis) {
        yawFixed = fixed;
        yawFixedAxis.set(axis);
    }

    public void yaw(float angle, TransformSpace relativeTo) {
        if (yawFixed) {
            rotate(yawFixedAxis, angle, relativeTo);
        } else {
            rotate(ENG_Math.VEC4_Y_UNIT, angle, relativeTo);
        }
    }

    public ENG_SceneNode getParentSceneNode() {
        return (ENG_SceneNode) getParent();
    }

    public void setVisible(boolean visible, boolean cascade) {
        for (ENG_AttachableObjectIntf item : objectList.values()) {
//            eng_movableObject.setVisible(visible);
            ENG_NativeCalls.sceneNode_setVisible(this, visible);
        }
        if (cascade) {
            for (ENG_Node eng_node : childrenList.values()) {
                ((ENG_SceneNode) eng_node).setVisible(visible, cascade);
            }
        }
    }

    public void flipVisibility(boolean cascade) {
        for (ENG_AttachableObjectIntf item : objectList.values()) {
//            obj.setVisible(!obj.getVisible());
            ENG_NativeCalls.sceneNode_flipVisibility(this);
        }
        if (cascade) {
            for (ENG_Node eng_node : childrenList.values()) {
                ((ENG_SceneNode) eng_node).flipVisibility(cascade);
            }
        }
    }

    @Override
    public void setStatic(boolean aStatic) {
        super.setStatic(aStatic);
        ENG_NativeCalls.sceneNode_setStatic(this, aStatic);
    }

    /**
     * @return the inSceneGraph
     */
    public boolean isInSceneGraph() {
        return inSceneGraph;
    }

    /**
     * @param inSceneGraph the inSceneGraph to set
     */
    public void setInSceneGraph(boolean inSceneGraph) {
        if (this.inSceneGraph != inSceneGraph) {
            this.inSceneGraph = inSceneGraph;

            //Tell children
            for (ENG_Node eng_node : this.childrenList.values()) {
                ENG_SceneNode node = (ENG_SceneNode) eng_node;
                node.setInSceneGraph(inSceneGraph);
            }
        }
    }

    /**
     * @return the worldAABB
     */
    @Deprecated
    public ENG_AxisAlignedBox getWorldAABB() {
        throw new IllegalArgumentException();
//        return worldAABB;
    }

    /**
     * @return the creator
     */
    public ENG_SceneManager getCreator() {
        return creator;
    }

    @Override
    public long getPointer() {
        return ptr;
    }

    @Override
    public void setPointer(long ptr) {
        this.ptr = ptr;
    }

    @Override
    public boolean isNativePointerSet() {
        return nativePointerSet;
    }

    @Override
    public void setNativePointer(boolean set) {
        nativePointerSet = set;
    }

    @Override
    public void destroy() {

    }

    public void lookAt(ENG_Vector4D destination) {
        lookAt(destination, ENG_Node.TransformSpace.TS_WORLD);
    }

    /**
     *
     * @param destination
     * @param relativeTo only TS_WORLD for now.
     */
    public void lookAt(ENG_Vector4D destination, ENG_Node.TransformSpace relativeTo) {
        lookAt(destination, relativeTo, ENG_Math.PT4_NEGATIVE_Z_UNIT);
    }

    /**
     *
     * @param destination
     * @param relativeTo only TS_WORLD for now.
     * @param localDirectionVector
     */
    public void lookAt(ENG_Vector4D destination,
                                ENG_Node.TransformSpace relativeTo, ENG_Vector4D localDirectionVector) {
        if (relativeTo != ENG_Node.TransformSpace.TS_WORLD) {
            throw new IllegalArgumentException("For now only world transform supported");
        }
        setDirection(destination.subAsPt(getPosition()), relativeTo, localDirectionVector);
    }

    public void setDirection(ENG_Vector4D destination,
                                    ENG_Node.TransformSpace relativeTo, ENG_Vector4D localDirectionVector) {
        ENG_Vector4D targetDir = destination.normalizedCopy();

        switch (relativeTo) {

            case TS_LOCAL:
                targetDir = getOrientation().mul(targetDir);
                break;
            case TS_WORLD:
                break;
            case TS_PARENT:
//                break;
            default:
                throw new IllegalArgumentException("For now only world transform supported");
        }

        ENG_Quaternion targetOrientation = new ENG_Quaternion(true);
        ENG_Quaternion currentOrient = new ENG_Quaternion(getOrientation());
        ENG_Vector4D currentDir = currentOrient.mul(localDirectionVector);
        if (currentDir.addAsPt(targetDir).squaredLength() < 0.00005f) {
            targetOrientation.set(-currentOrient.z, currentOrient.w, currentOrient.x, -currentOrient.y);
        } else {
            ENG_Quaternion rotQuat = currentDir.getRotationTo(targetDir);
            rotQuat.mul(currentOrient, targetOrientation);
        }
        setOrientation(targetOrientation);
    }

    /**
     * Normally the scene node name is only available on java side. If you want to search for node name
     * on native side you need to send the name from here.
     */
    public void setNativeName() {
        ENG_NativeCalls.sceneNode_setName(this);
    }

//    public void lookAt(ENG_Vector4D positionToLookAt) {
//        lookAt(positionToLookAt, TransformSpace.TS_LOCAL);
//    }

//    public void lookAt(ENG_Vector4D positionToLookAt, TransformSpace transformSpace) {
//        ignoreOrientation = true;
//        ENG_NativeCalls.sceneNode_lookAt(this, new ENG_Vector3D(positionToLookAt), transformSpace);
//    }
//
//    public void setDirection(ENG_Vector4D direction) {
//        setDirection(direction, TransformSpace.TS_LOCAL);
//    }
//
//    public void setDirection(ENG_Vector4D direction, TransformSpace transformSpace) {
//        ignoreOrientation = true;
//        ENG_NativeCalls.sceneNode_setDirection(this, new ENG_Vector3D(direction), transformSpace);
//    }
//
//    public boolean isIgnoreOrientation() {
//        return ignoreOrientation;
//    }
//
//    public void setIgnoreOrientation(boolean ignoreOrientation) {
//        this.ignoreOrientation = ignoreOrientation;
//    }
}
