/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainActivity;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_NameGenerator;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.exception.ENG_NodeException;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ENG_Node implements Comparator<ENG_Node> {

    public enum TransformSpace {
        // / Transform is relative to the local space
        TS_LOCAL(0),
        // / Transform is relative to the space of the parent node
        TS_PARENT(1),
        // / Transform is relative to world space
        TS_WORLD(2);

        private final byte pos;

        TransformSpace(int i) {
            pos = (byte) i;
        }

        public byte getPos() {
            return pos;
        }
    }

    private final String name;
    private ENG_Node parentNode;
    protected final HashMap<String, ENG_Node> childrenList = new HashMap<>();
    private final ENG_Quaternion orientation = new ENG_Quaternion(true);
    private final ENG_Vector4D position = new ENG_Vector4D(ENG_Math.PT4_ZERO);
    protected final ENG_Vector4D derivedPosition = new ENG_Vector4D();
    private final ENG_Vector4D initialPosition = new ENG_Vector4D(ENG_Math.PT4_ZERO);
    private final ENG_Vector4D scale = new ENG_Vector4D(ENG_Math.PT4_UNIT);
    protected final ENG_Vector4D derivedScale = new ENG_Vector4D();
    private final ENG_Vector4D initialScale = new ENG_Vector4D(ENG_Math.PT4_UNIT);
    private final ENG_Vector4D uv = new ENG_Vector4D();
    private final ENG_Vector4D uuv = new ENG_Vector4D();
    private final ENG_Vector4D qvec = new ENG_Vector4D();
    private final ENG_Vector4D temp = new ENG_Vector4D();
    private final ENG_Vector4D temp2 = new ENG_Vector4D();
    private final ENG_Quaternion tempQuat = new ENG_Quaternion();
    private final ENG_Quaternion tempQuat2 = new ENG_Quaternion();
    protected final ENG_Quaternion derivedOrientation = new ENG_Quaternion();
    private final ENG_Quaternion initialOrientation = new ENG_Quaternion(true);
    private final ENG_Matrix4 cachedTransform = new ENG_Matrix4();
    private static final ENG_NameGenerator nameGenerator = new ENG_NameGenerator("Node_");
    protected static final ArrayList<ENG_Node> queuedUpdates = new ArrayList<>();
    protected final TreeSet<ENG_Node> childrenToUpdate = new TreeSet<>(this);
    protected final ReentrantLock childrenToUpdateLock = new ReentrantLock();
    protected final ENG_Vector3D derivedPositionNative = new ENG_Vector3D();
    protected final ENG_Quaternion derivedOrientationNative = new ENG_Quaternion();
    private final ENG_SceneManager.SceneMemoryMgrTypes type;
    protected boolean inheritOrientation = true;
    protected boolean inheritScale = true;
    protected boolean needParentUpdate;
    protected boolean needChildUpdate;
    protected boolean parentNotified;
    protected boolean queuedForUpdate;
    protected boolean cachedTransformOutOfDate = true;
    protected boolean isStatic;

    static class DebugRenderable extends ENG_RenderableImpl {

        protected ENG_Node mParent;
        /** @noinspection deprecation*/
        protected ENG_Mesh mMeshPtr;
        protected ENG_Material mMat;
        protected float mScaling;

        /** @noinspection deprecation*/
        @Override
        public ArrayList<ENG_Light> getLights() {
            
            return null;
        }

        @Override
        public ENG_Material getMaterial() {
            
            return null;
        }

        @Override
        public void getRenderOperation(ENG_RenderOperation op) {
            

        }

        @Override
        public float getSquaredViewDepth(ENG_Camera cam) {
            
            return 0;
        }

        @Override
        public void getWorldTransforms(ENG_Matrix4[] xform) {
            

        }

    }

    public ENG_Node() {
        this(nameGenerator.generateName());
    }

    public ENG_Node(ENG_SceneManager.SceneMemoryMgrTypes type) {
        this(nameGenerator.generateName(), type);
    }

    public ENG_Node(String name) {
        this(name, ENG_SceneManager.SceneMemoryMgrTypes.SCENE_DYNAMIC);
    }

    public ENG_Node(String name, ENG_SceneManager.SceneMemoryMgrTypes type) {
        this.name = name;
        this.type = type;
        needUpdate(false);
    }

    public int compare(ENG_Node obj0, ENG_Node obj1) {
        return obj0.getName().compareTo(obj1.getName());
    }

    public void addChildNative(ENG_SceneNode node) {
        ENG_NativeCalls.node_addChild((ENG_SceneNode) this, node);
    }

    public void addChild(ENG_Node child) {
        if (child.getParent() != null) {
            // This child already has a parent.
            throw new ENG_NodeException("Node: " + child.getName()
                    + " was already " + "a child of parent: "
                    + child.getParent().getName());
        }
        childrenList.put(child.getName(), child);
        child.setParent(this);
    }

    public int getNumChildren() {
        return childrenList.size();
    }

    protected abstract ENG_Node createChildImpl(String name, ENG_SceneManager.SceneMemoryMgrTypes type, boolean updateEveryFrame);

    protected abstract ENG_Node createChildImpl(String name, boolean updateEveryFrame);

    protected abstract ENG_Node createChildImpl(boolean updateEveryFrame);

    public ENG_Node createChild(String name, boolean updateEveryFrame) {
        return createChild(name, ENG_SceneManager.SceneMemoryMgrTypes.SCENE_DYNAMIC, updateEveryFrame);
    }

    public ENG_Node createChild(String name, ENG_SceneManager.SceneMemoryMgrTypes type, boolean updateEveryFrame) {
        return createChild(name, type, ENG_Math.PT4_ZERO, ENG_Math.QUAT_IDENTITY, updateEveryFrame);
    }

    public ENG_Node createChild(String name, ENG_Vector4D translate,
                                ENG_Quaternion rotate, boolean updateEveryFrame) {
        return createChild(name, ENG_SceneManager.SceneMemoryMgrTypes.SCENE_DYNAMIC, translate, rotate, updateEveryFrame);
    }

    public ENG_Node createChild(String name, ENG_SceneManager.SceneMemoryMgrTypes type,
                                ENG_Vector4D translate, ENG_Quaternion rotate, boolean updateEveryFrame) {
        ENG_Node node = createChildImpl(name, type, updateEveryFrame);
        node.translate(translate, TransformSpace.TS_PARENT);
        node.rotate(rotate, TransformSpace.TS_LOCAL);
        addChild(node);
        return node;
    }

    public ENG_Node createChild(boolean updateEveryFrame) {
        return createChild(ENG_Math.PT4_ZERO, ENG_Math.QUAT_IDENTITY, updateEveryFrame);
    }

    public ENG_Node createChild(ENG_Vector4D translate, ENG_Quaternion rotate, boolean updateEveryFrame) {
        ENG_Node node = createChildImpl(updateEveryFrame);
        node.translate(translate, TransformSpace.TS_PARENT);
        node.rotate(rotate, TransformSpace.TS_LOCAL);
        addChild(node);
        return node;
    }

    public static void queueNeedUpdate(ENG_Node node) {
        if (!node.isQueuedForUpdate()) {
            node.setQueuedForUpdate(true);
            queuedUpdates.add(node);
        }
    }

    public static void processQueuedUpdate() {
        int len = queuedUpdates.size();
        ENG_Node node;
        for (int i = 0; i < len; ++i) {
            node = queuedUpdates.get(i);
            node.setQueuedForUpdate(false);
            node.needUpdate(true);
        }
        queuedUpdates.clear();
    }

    /**
     * @param queuedForUpdate the queuedForUpdate to set
     */
    public void setQueuedForUpdate(boolean queuedForUpdate) {
        this.queuedForUpdate = queuedForUpdate;
    }

    public void _updateFromParent() {
        _updateFromParentImpl();
    }

    public void _updateFromParentImpl() {
//        System.out.println("_updateFromParentImpl() for " + getName());
        if (parentNode != null) {
//            System.out.println("parentNode != null");
            parentNode._getDerivedOrientation(tempQuat);
//            System.out.println("tempQuat: " + tempQuat);
            if (inheritOrientation) {
                tempQuat.mul(orientation, derivedOrientation);
            } else {
                derivedOrientation.set(orientation);
            }
//            System.out.println("orientation: " + orientation);
//            System.out.println("derivedOrientation: " + derivedOrientation);
            parentNode._getDerivedScale(temp);
            if (inheritScale) {
                temp.mul(scale, derivedScale);
            } else {
                derivedScale.set(scale);
            }
//            System.out.println("derivedScale: " + derivedScale);
            temp.mulInPlace(position);
            tempQuat.mul(temp, derivedPosition);
            parentNode._getDerivedPosition(temp);
            derivedPosition.addInPlace(temp);
//            System.out.println("derivedPosition: " + derivedPosition);
        } else {
            derivedOrientation.set(orientation);
            derivedScale.set(scale);
            derivedPosition.set(position);
//            System.out.println("derivedOrientation: " + derivedOrientation + " derivedScale: " + derivedScale + " derivedPosition: " + derivedPosition);
        }
        cachedTransformOutOfDate = true;
        needParentUpdate = false;
    }

    public ENG_Matrix4 _getFullTransform() {
        ENG_Matrix4 mat = new ENG_Matrix4();
        _getFullTransform(mat);
        return mat;
    }

    public void _getFullTransform(ENG_Matrix4 mat) {
        if (cachedTransformOutOfDate) {
            cachedTransform.makeTransform(_getDerivedPosition(),
                    _getDerivedScale(), _getDerivedOrientation());
            cachedTransformOutOfDate = false;
        }
        mat.set(cachedTransform);
    }

    public ENG_Matrix4 _getFullTransformNative() {
        ENG_Matrix4 mat = new ENG_Matrix4();
        _getFullTransformNative(mat);
        return mat;
    }

    public void _getFullTransformNative(ENG_Matrix4 mat) {
        mat.makeTransform(position, scale, orientation);
    }

    public ENG_Node getChild(int index) {
        Iterator<ENG_Node> nodeIt = childrenList.values().iterator();
        if (index < childrenList.size()) {
            while ((nodeIt.hasNext()) && ((index--) > 0)) {
                nodeIt.next();
            }
            return nodeIt.next();
        }
        return null;
    }

    public ENG_Node removeChild(int index) {
        Iterator<ENG_Node> nodeIt = childrenList.values().iterator();
        if (index < childrenList.size()) {
            while ((nodeIt.hasNext()) && ((index--) > 0)) {
                nodeIt.next();
            }
            ENG_Node node = nodeIt.next();
            cancelUpdate(node);
            node.setParent(null);
            childrenList.remove(node.getName());
            return node;
        } else {
            if (MainActivity.isDebugmode()) {
                throw new NoSuchElementException("index: " + index);
            } else {
                return null;
            }
        }
    }

    public void _update(boolean updateChildren, boolean parentHasChanged) {
        parentNotified = false;
        if ((!updateChildren) && (!needParentUpdate) && (!needChildUpdate)
                && (!parentHasChanged)) {
            return;
        }

        if (needParentUpdate || parentHasChanged) {
            _updateFromParent();
        }

        if (needChildUpdate || parentHasChanged) {
            for (ENG_Node eng_node : childrenList.values()) {
                eng_node._update(true, true);
            }
            childrenToUpdate.clear();
        } else {
            for (ENG_Node aChildrenToUpdate : childrenToUpdate) {
                aChildrenToUpdate._update(true, false);
            }
            childrenToUpdate.clear();
        }
        needChildUpdate = false;
    }

    public void needUpdate() {
        needUpdate(false);
    }

    public void needUpdate(boolean forceParentUpdate) {
        needParentUpdate = true;
        needChildUpdate = true;
        cachedTransformOutOfDate = true;
        selfRequest(forceParentUpdate);
        childrenToUpdate.clear();
    }

    public void cancelUpdate(ENG_Node node) {
        childrenToUpdate.remove(node);
        if (childrenToUpdate.isEmpty() && (parentNode != null)
                && (!needChildUpdate)) {
            parentNode.cancelUpdate(this);
            parentNotified = false;
        }
    }

    public void requestUpdate(ENG_Node node, boolean forceParentUpdate) {
        if (needChildUpdate) {
            return;
        }
        childrenToUpdateLock.lock();
        try {
            childrenToUpdate.add(node);
        } finally {
            childrenToUpdateLock.unlock();
        }
        selfRequest(forceParentUpdate);
    }

    private void selfRequest(boolean forceParentUpdate) {
        if ((parentNode != null) && ((!parentNotified) || forceParentUpdate)) {
            parentNode.requestUpdate(this, forceParentUpdate);
            parentNotified = true;
        }
    }

    public void setOrientation(float x, float y, float z, float w) {
        orientation.set(x, y, z, w);
        if (MainActivity.isDebugmode()) {
            if (orientation.isNaN()) {
                throw new IllegalArgumentException("orientation is NAN");
            }
        }
    }

    public void setOrientation(ENG_Quaternion neworientation) {
        if (MainActivity.isDebugmode() && (neworientation.isInvalid())) {
            throw new IllegalArgumentException(neworientation.toString());
        }
        orientation.set(neworientation);
//        System.out.println("Setting neworientation: " + neworientation.toString());
    }

    public void resetOrientation() {
        orientation.set(ENG_Math.QUAT_IDENTITY);
    }

    public void getOrientation(ENG_Quaternion ret) {
        ret.set(orientation);
    }

    public ENG_Quaternion getOrientation() {
        return new ENG_Quaternion(orientation);
    }

    public ENG_Quaternion getOrientationForNative() {
        return orientation;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        needUpdate(false);
    }

    public void setPosition(ENG_Vector4D pos) {
//        if (getName().startsWith("Sebi_Concussion")) {
//            System.out.println("node name: " + getName() + " setPosition: " + pos);
//        }
        position.set(pos);
        needUpdate(false);
    }

    public void setPosition(ENG_Vector3D pos) {
        position.set(pos);
        needUpdate(false);
    }

    public void getPosition(ENG_Vector4D ret) {
        ret.set(position);

    }

    public void getPosition(ENG_Vector3D ret) {
        ret.set(position);

    }

    public ENG_Vector4D getPosition() {
        return new ENG_Vector4D(position);
    }

    public ENG_Vector4D getPositionForNative() {
        return position;
    }

    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
    }

    public void setScale(ENG_Vector4D scale) {
        this.scale.set(scale);
    }

    public void setScale(ENG_Vector3D scale) {
        this.scale.set(scale);
    }

    public void getScale(ENG_Vector4D ret) {
        ret.set(scale);
    }

    public void getScale(ENG_Vector3D ret) {
        ret.set(scale);
    }

    public ENG_Vector4D getScale() {
        return new ENG_Vector4D(scale);
    }

    public ENG_Vector4D getScaleForNative() {
        return scale;
    }

    public void getLocalAxes(ENG_Vector4D x, ENG_Vector4D y, ENG_Vector4D z) {
        orientation.mul(ENG_Math.VEC4_X_UNIT, x, uv, uuv, qvec);
        orientation.mul(ENG_Math.VEC4_Y_UNIT, y, uv, uuv, qvec);
        orientation.mul(ENG_Math.VEC4_Z_UNIT, z, uv, uuv, qvec);
    }

    public void getLocalAxes(ENG_Matrix4 ret) {
        ENG_Vector4D x = new ENG_Vector4D();
        ENG_Vector4D y = new ENG_Vector4D();
        ENG_Vector4D z = new ENG_Vector4D();
        getLocalAxes(x, y, z);
        ret.setColumn(0, x);
        ret.setColumn(1, y);
        ret.setColumn(2, z);
    }

    public ENG_Matrix4 getLocalAxes() {
        ENG_Matrix4 ret = new ENG_Matrix4();
        getLocalAxes(ret);
        return ret;
    }

    public void getLocalAxis(ENG_Vector4D vec, ENG_Vector4D ret) {
        orientation.mul(vec, ret, uv, uuv, qvec);
    }

    public void getLocalXAxis(ENG_Vector4D x) {
        orientation.mul(ENG_Math.VEC4_X_UNIT, x, uv, uuv, qvec);
    }

    public void getLocalYAxis(ENG_Vector4D y) {
        orientation.mul(ENG_Math.VEC4_Y_UNIT, y, uv, uuv, qvec);
    }

    public void getLocalZAxis(ENG_Vector4D z) {
        orientation.mul(ENG_Math.VEC4_Z_UNIT, z, uv, uuv, qvec);
    }

    public void getLocalInverseXAxis(ENG_Vector4D x) {
        orientation.mul(ENG_Math.VEC4_NEGATIVE_X_UNIT, x, uv, uuv, qvec);
    }

    public void getLocalInverseYAxis(ENG_Vector4D y) {
        orientation.mul(ENG_Math.VEC4_NEGATIVE_Y_UNIT, y, uv, uuv, qvec);
    }

    public void getLocalInverseZAxis(ENG_Vector4D z) {
        orientation.mul(ENG_Math.VEC4_NEGATIVE_Z_UNIT, z, uv, uuv, qvec);
    }

    public ENG_Vector4D getLocalXAxis() {
        ENG_Vector4D ret = new ENG_Vector4D();
        getLocalXAxis(ret);
        return ret;
    }

    public ENG_Vector4D getLocalYAxis() {
        ENG_Vector4D ret = new ENG_Vector4D();
        getLocalYAxis(ret);
        return ret;
    }

    public ENG_Vector4D getLocalZAxis() {
        ENG_Vector4D ret = new ENG_Vector4D();
        getLocalZAxis(ret);
        return ret;
    }

    public ENG_Vector4D getLocalInverseXAxis() {
        ENG_Vector4D ret = new ENG_Vector4D();
        getLocalInverseXAxis(ret);
        return ret;
    }

    public ENG_Vector4D getLocalInverseYAxis() {
        ENG_Vector4D ret = new ENG_Vector4D();
        getLocalInverseYAxis(ret);
        return ret;
    }

    public ENG_Vector4D getLocalInverseZAxis() {
        ENG_Vector4D ret = new ENG_Vector4D();
        getLocalInverseZAxis(ret);
        return ret;
    }

    public boolean hasChild(String name) {
        return childrenList.containsKey(name);
    }

    public boolean hasChild(ENG_Node node) {
        return childrenList.containsValue(node);
    }

    public ENG_Node getChild(String name) {
        ENG_Node node = childrenList.get(name);
//        System.out.println("Getting child node: " + name);
        if (MainActivity.isDebugmode()) {
            if (node == null) {
                throw new IllegalArgumentException(name + " child node not found");
            }
        }
        return node;
    }

    public ENG_Node removeChild(String name) {
        ENG_Node node = childrenList.remove(name);
        if (node == null) {
            if (MainActivity.isDebugmode()) {
                throw new IllegalArgumentException("Provided name not found");
            } else {
                return null;
            }
        }
        node.setParent(null);
        cancelUpdate(node);
        return node;
    }

    public void removeChildNative(ENG_SceneNode node) {
        ENG_NativeCalls.node_removeChild((ENG_SceneNode) this, node);
    }

    public ENG_Node removeChild(ENG_Node node) {
        return removeChild(node.getName());
    }

    public void removeAllChildren() {
        Collection<ENG_Node> nodeList = childrenList.values();
        for (ENG_Node aNodeList : nodeList) {
            aNodeList.setParent(null);
        }
        childrenList.clear();
        childrenToUpdate.clear();
    }

    public void scale(ENG_Vector3D vec) {
        scale.mulInPlace(vec);
        needUpdate(false);
    }

    public void scale(ENG_Vector4D vec) {
        scale.mulInPlace(vec);
        needUpdate(false);
    }

    public void scale(float x, float y, float z) {
        temp.set(x, y, z);
        scale(temp);
    }

    public void roll(float angle) {
        roll(angle, TransformSpace.TS_LOCAL);
    }

    public void roll(float angle, TransformSpace relativeTo) {
        rotate(ENG_Math.VEC4_Z_UNIT, angle, relativeTo);
    }

    public void pitch(float angle) {
        pitch(angle, TransformSpace.TS_LOCAL);
    }

    public void pitch(float angle, TransformSpace relativeTo) {
        rotate(ENG_Math.VEC4_X_UNIT, angle, relativeTo);
    }

    public void yaw(float angle) {
        yaw(angle, TransformSpace.TS_LOCAL);
    }

    public void yaw(float angle, TransformSpace relativeTo) {
        rotate(ENG_Math.VEC4_Y_UNIT, angle, relativeTo);
    }

    public void rotate(ENG_Vector4D vec, float angle) {
        rotate(vec, angle, TransformSpace.TS_LOCAL);
    }

    public void rotate(ENG_Vector4D vec, float angle, TransformSpace relativeTo) {
        tempQuat.fromAngleAxisRad(angle, vec);
        rotate(tempQuat, relativeTo);
    }

    public void rotate(ENG_Quaternion q) {
        rotate(q, TransformSpace.TS_LOCAL);
    }

    public void rotate(ENG_Quaternion q, TransformSpace relativeTo) {
        if (MainActivity.isDebugmode() && (q.isInfinite() || q.isNaN())) {
            throw new IllegalArgumentException(q.toString());
        }
        tempQuat.set(q);
        tempQuat.normalize();
        if (MainActivity.isDebugmode()
                && (tempQuat.isInfinite() || tempQuat.isNaN())) {
            throw new IllegalArgumentException(tempQuat.toString());
        }
        switch (relativeTo) {
            case TS_LOCAL:
                // ENG_Quaternion oldQuat = new ENG_Quaternion(orientation);
                orientation.mulInPlace(tempQuat);
                // System.out.println("angle between quats: " +
                // oldQuat.zAxisVec4().angleBetween(orientation.zAxisVec4()) *
                // ENG_Math.RADIANS_TO_DEGREES);
                // System.out.println("frontVec: " + getLocalInverseZAxis());
                // ENG_Vector4D vec = new ENG_Vector4D();
                // float angleAxisDeg = orientation.toAngleAxisDeg(vec);
                // System.out.println("axis: " + vec + " angleAxis: " +
                // angleAxisDeg);
                // float dot =
                // ENG_Quaternion.fromRotationMatrixRet(q.toRotationMatrix()).dot(q);
                // System.out.println("dot: " + dot);
                break;
            case TS_PARENT:
                tempQuat.mul(orientation, orientation);
                // orientation.set(tempQuat);
                break;
            case TS_WORLD:
                _getDerivedOrientationCopy().inverse(tempQuat2);
                orientation.mulInPlace(tempQuat2);
                orientation.mulInPlace(tempQuat);
                orientation.mulInPlace(_getDerivedOrientationCopy());
                break;
            default:
                // Should never get here
                throw new IllegalArgumentException("relativeTo: " + relativeTo);
        }
        needUpdate(false);
    }

    public void translate(ENG_Matrix4 axes, float x, float y, float z) {
        translate(axes, x, y, z, TransformSpace.TS_PARENT);
    }

    public void translate(ENG_Matrix4 axes, float x, float y, float z,
                          TransformSpace relativeTo) {
        temp2.set(x, y, z);
        translate(axes, temp2, relativeTo);
    }

    public void translate(ENG_Matrix4 axes, ENG_Vector4D vec) {
        translate(axes, vec, TransformSpace.TS_PARENT);
    }

    public void translate(ENG_Matrix4 axes, ENG_Vector4D vec,
                          TransformSpace relativeTo) {
        axes.transform(vec, temp);
        translate(temp, relativeTo);
    }

    public void translate(float x, float y, float z) {
        translate(x, y, z, TransformSpace.TS_PARENT);
    }

    public void translate(float x, float y, float z, TransformSpace relativeTo) {
        temp.set(x, y, z);
        translate(temp, relativeTo);
    }

    public void translate(ENG_Vector3D vec) {
        translate(vec, TransformSpace.TS_PARENT);
    }

    public void translate(ENG_Vector3D vec, TransformSpace relativeTo) {
        temp.set(vec);
        translate(temp, relativeTo);
    }

    public void translate(ENG_Vector4D vec) {
        translate(vec, TransformSpace.TS_PARENT);
    }

    public void translate(ENG_Vector4D vec, TransformSpace relativeTo) {
//        if (getName().startsWith("Sebi_Concussion")) {
//            System.out.println("node name: " + getName() + " translate: " + vec + " relativeTo: " + relativeTo);
//        }
        switch (relativeTo) {
            case TS_LOCAL:
                orientation.mul(vec, temp, uv, uuv, qvec);
                position.addInPlace(temp);
                break;
            case TS_PARENT:
                position.addInPlace(vec);
                break;
            case TS_WORLD:
                if (parentNode != null) {
                    parentNode._getDerivedOrientation(tempQuat);
                    tempQuat.inverse();
                    tempQuat.mul(vec, temp, uv, uuv, qvec);
                    parentNode._getDerivedScale(temp2);
                    temp.divInPlace(temp2);
                    position.addInPlace(temp);
                } else {
                    position.addInPlace(vec);
                }
                break;
            default:
                // Should never get here
                throw new IllegalArgumentException("relativeTo: " + relativeTo);
        }
        needUpdate(false);
    }

    public void _setDerivedPositionNative(float x, float y, float z) {
        derivedPositionNative.set(x, y, z);
    }

    public void _setDerivedOrientationNative(float x, float y, float z, float w) {
        derivedOrientationNative.set(x, y, z, w);
    }

    public ENG_Vector3D getDerivedPositionNative() {
        return derivedPositionNative;
    }

    public ENG_Quaternion getDerivedOrientationNative() {
        return derivedOrientationNative;
    }

    public void _setDerivedPosition(ENG_Vector4D pos) {
        parentNode.convertWorldToLocalPosition(pos, temp);
        setPosition(temp);
    }

    public void _setDerivedOrientation(ENG_Quaternion q) {
        parentNode.convertWorldToLocalOrientation(q, tempQuat);
        setOrientation(tempQuat);
    }

    public ENG_Quaternion _getDerivedOrientationCopy() {
        ENG_Quaternion q = new ENG_Quaternion();
        _getDerivedOrientation(q);
        return q;
    }

    public void _getDerivedOrientation(ENG_Quaternion q) {
        if (needParentUpdate) {
            _updateFromParent();
        }
        q.set(derivedOrientation);
    }

    public ENG_Quaternion _getDerivedOrientation() {
        if (needParentUpdate) {
            _updateFromParent();
        }
        return derivedOrientation;
    }

    public ENG_Vector4D _getDerivedScaleCopy() {
        ENG_Vector4D vec = new ENG_Vector4D();
        _getDerivedScale(vec);
        return vec;
    }

    public void _getDerivedScale(ENG_Vector4D vec) {
        if (needParentUpdate) {
            _updateFromParent();
        }
        vec.set(derivedScale);
    }

    public ENG_Vector4D _getDerivedScale() {
        if (needParentUpdate) {
            _updateFromParent();
        }
        return derivedScale;
    }

    public void _getDerivedScale(ENG_Vector3D vec) {
        if (needParentUpdate) {
            _updateFromParent();
        }
        vec.set(derivedScale);
    }

    public ENG_Vector4D _getDerivedPositionCopy() {
        ENG_Vector4D vec = new ENG_Vector4D();
        _getDerivedPosition(vec);
        return vec;
    }

    public void _getDerivedPosition(ENG_Vector4D vec) {
        if (needParentUpdate) {
            _updateFromParent();
        }
        vec.set(derivedPosition);
    }

    public ENG_Vector4D _getDerivedPosition() {
        if (needParentUpdate) {
            _updateFromParent();
        }
        return derivedPosition;
    }

    public void _getDerivedPosition(ENG_Vector3D vec) {
        if (needParentUpdate) {
            _updateFromParent();
        }
        vec.set(derivedPosition);
    }

    public ENG_Vector4D convertWorldToLocalPosition(ENG_Vector4D worldPos) {
        ENG_Vector4D ret = new ENG_Vector4D();
        convertWorldToLocalPosition(worldPos, ret);
        return ret;
    }

    public void convertWorldToLocalPosition(ENG_Vector4D worldPos,
                                            ENG_Vector4D ret) {
        if (needParentUpdate) {
            _updateFromParent();
        }
        derivedOrientation.inverse(tempQuat);
        worldPos.sub(derivedPosition, temp);
        tempQuat.mul(temp, ret, uv, uuv, qvec);
        ret.divInPlace(derivedScale);

    }

    public ENG_Vector4D convertLocalToWorldPosition(ENG_Vector4D localPos) {
        ENG_Vector4D ret = new ENG_Vector4D();
        convertLocalToWorldPosition(localPos, ret);
        return ret;
    }

    public void convertLocalToWorldPosition(ENG_Vector4D localPos,
                                            ENG_Vector4D ret) {
        if (needParentUpdate) {
            _updateFromParent();
        }
        derivedOrientation.mul(localPos, temp);
        temp.mulInPlace(derivedScale);
        temp.addInPlace(derivedPosition);
    }

    public ENG_Quaternion convertWorldToLocalOrientation(
            ENG_Quaternion worldOrientation) {
        ENG_Quaternion ret = new ENG_Quaternion();
        convertWorldToLocalOrientation(worldOrientation, ret);
        return ret;
    }

    public void convertWorldToLocalOrientation(ENG_Quaternion worldOrientation,
                                               ENG_Quaternion ret) {
        if (needParentUpdate) {
            _updateFromParent();
        }
        derivedOrientation.inverse(ret);
        ret.mulInPlace(worldOrientation);
    }

    public ENG_Quaternion convertLocalToWorldOrientation(
            ENG_Quaternion localOrientation) {
        ENG_Quaternion ret = new ENG_Quaternion();
        convertLocalToWorldOrientation(localOrientation, ret);
        return ret;
    }

    public void convertLocalToWorldOrientation(ENG_Quaternion localOrientation,
                                               ENG_Quaternion ret) {
        if (needParentUpdate) {
            _updateFromParent();
        }
        derivedOrientation.mul(localOrientation, ret);
    }

    public void setInitialState() {
        initialPosition.set(position);
        initialOrientation.set(orientation);
        initialScale.set(scale);
    }

    public void resetInitialState() {
        position.set(initialPosition);
        orientation.set(initialOrientation);
        scale.set(initialScale);
        needUpdate(false);
    }

    public ENG_Vector4D getInitialPosition() {
        ENG_Vector4D ret = new ENG_Vector4D();
        getInitialPosition(ret);
        return ret;
    }

    public void getInitialPosition(ENG_Vector4D ret) {
        ret.set(initialPosition);
    }

    public void getInitialPosition(ENG_Vector3D ret) {
        ret.set(initialPosition);
    }

    public ENG_Quaternion getInitialOrientation() {
        ENG_Quaternion ret = new ENG_Quaternion();
        getInitialOrientation(ret);
        return ret;
    }

    public void getInitialOrientation(ENG_Quaternion ret) {
        ret.set(initialOrientation);
    }

    public ENG_Vector4D getInitialScale() {
        ENG_Vector4D ret = new ENG_Vector4D();
        getInitialScale(ret);
        return ret;
    }

    public void getInitialScale(ENG_Vector4D ret) {
        ret.set(initialScale);
    }

    public void getInitialScale(ENG_Vector3D ret) {
        ret.set(initialScale);
    }

    public Iterator<Entry<String, ENG_Node>> getChildIterator() {
        return childrenList.entrySet().iterator();
    }

    public float getSquaredViewDepth(ENG_Camera cam, ENG_Vector4D temp) {
        _getDerivedPosition(temp);
        temp.subInPlace(cam.getDerivedPosition());
        return temp.squaredLength();
    }

    /**
     * @return the parentNode
     */
    public ENG_Node getParent() {
        return parentNode;
    }

    /**
     * @param parentNode the parentNode to set
     */
    public void setParent(ENG_Node parentNode) {
        this.parentNode = parentNode;
        parentNotified = false;
        needUpdate(false);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the inheritOrientation
     */
    public boolean isInheritOrientation() {
        return inheritOrientation;
    }

    /**
     * @param inheritOrientation the inheritOrientation to set
     */
    public void setInheritOrientation(boolean inheritOrientation) {
        this.inheritOrientation = inheritOrientation;
        needUpdate(false);
    }

    /**
     * @return the inheritScale
     */
    public boolean isInheritScale() {
        return inheritScale;
    }

    /**
     * @param inheritScale the inheritScale to set
     */
    public void setInheritScale(boolean inheritScale) {
        this.inheritScale = inheritScale;
        needUpdate(false);
    }

    /**
     * @return the queuedForUpdate
     */
    public boolean isQueuedForUpdate() {
        return queuedForUpdate;
    }

    public ENG_SceneManager.SceneMemoryMgrTypes getType() {
        return type;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }
}
