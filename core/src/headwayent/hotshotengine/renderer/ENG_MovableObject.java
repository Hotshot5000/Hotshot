/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 4:51 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_AxisAlignedBox;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Sphere;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_LodListener.MovableObjectLodChangedEvent;
import headwayent.hotshotengine.renderer.ENG_RenderQueue.RenderQueueGroupID;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointerWithSetter;

import java.util.ArrayList;

public abstract class ENG_MovableObject implements ENG_AnimableObjectInterface, ENG_NativePointerWithSetter {

    //public static final int DEFAULT_VISIBILITY_FLAGS = 0xffffffff;
    //private static final int msDefaultQueryFlags = 0xFFFFFFFF;

    protected long mId;
    protected long mPointer;
    protected String mName;
    protected ENG_MovableObjectFactory mCreator;
    protected ENG_SceneManager mManager;
    protected ENG_Node mParentNode;
    protected boolean mParentIsTagPoint;
    protected final ENG_AxisAlignedBox mWorldAABB = new ENG_AxisAlignedBox();
    protected final ENG_Sphere mWorldBoundingSphere = new ENG_Sphere();
    protected ENG_AxisAlignedBox mWorldDarkCapBounds = new ENG_AxisAlignedBox();
    protected boolean mCastShadows;
    protected final ENG_Matrix4 fullTransform = new ENG_Matrix4();
    protected boolean mDebugDisplay;
    protected float mUpperDistance;
    protected float mSquaredUpperDistance;
    protected boolean mBeyondFarDistance;
    protected byte mRenderQueueID = RenderQueueGroupID.RENDER_QUEUE_MAIN.getID();
    protected boolean mRenderQueueIDSet;
    protected short mRenderQueuePriority = 100;
    protected boolean mRenderQueuePrioritySet;
    /// Flags determining whether this object is included / excluded from scene queries
    protected int mQueryFlags = msDefaultQueryFlags;
    /// Flags determining whether this object is visible (compared to SceneManager mask)
    //protected int mVisibilityFlags;
    protected int mVisibilityFlags = msDefaultVisibilityFlags;
    //protected boolean mBeyondFarDistance;
    protected boolean attached;
    protected boolean visible = true;
    /// Does rendering this object disabled by listener?
    protected boolean mRenderingDisabled;

    /** @noinspection deprecation*/
    protected final ArrayList<ENG_Light> mLightList = new ArrayList<>();

    /// The last frame that this light list was updated in
    protected long mLightListUpdated;

    /// the light mask defined for this movable. This will be taken into consideration when deciding which light should affect this movable
    protected int mLightMask = 0xFFFFFFFF;

    // Static members
    /// Default query flags
    protected static int msDefaultQueryFlags = 0xFFFFFFFF;
    /// Default visibility flags
    protected static int msDefaultVisibilityFlags = 0xFFFFFFFF;

    private final ENG_Vector4D derivedScale = new ENG_Vector4D();
    private final ENG_Vector4D derivedPosition = new ENG_Vector4D();

    private final ENG_Vector4D notifyCameraTemp = new ENG_Vector4D();

    private final ENG_AnimableObject animableObject = new ENG_AnimableObject();

    private boolean nativePointerSet;

    public ENG_AnimableObject getAnimableObject() {
        return animableObject;
    }

    public ENG_MovableObject() {

    }

    public ENG_MovableObject(String name) {
        this();
        this.mName = name;
    }

    public ENG_MovableObject(String name, long id) {
        this(name);
        mId = id;
    }

    public long getId() {
        return mId;
    }

    @Override
    public long getPointer() {
        return mPointer;
    }

    @Override
    public void setPointer(long ptr) {
        mPointer = ptr;
    }

    @Override
    public boolean isNativePointerSet() {
        return nativePointerSet;
    }

    @Override
    public void setNativePointer(boolean set) {
        nativePointerSet = set;
    }

    public void _notifyAttached(ENG_Node parent, boolean isTagPoint) {
        mParentNode = parent;
        this.mParentIsTagPoint = isTagPoint;

        // Mark light list being dirty, simply decrease
        // counter by one for minimise overhead
        --mLightListUpdated;
    }

    public ENG_Node getParentNode() {
        return mParentNode;
    }

    public ENG_SceneNode getParentSceneNode() {
        //	if (mParentIsTagPoint) {
        //Ignore animation for now
        //	} else {
        return (ENG_SceneNode) mParentNode;
        //	}
    }

    public boolean isAttached() {
        return mParentNode != null;
    }

    public void detachFromParent() {
        if (isAttached()) {
            if (mParentIsTagPoint) {
                //Ignore for now
            } else {
                ((ENG_SceneNode) mParentNode).detachObject(this);
            }
        }
    }

    public boolean isInScene() {
        //Ignore for now
        return mParentNode != null && !mParentIsTagPoint && ((ENG_SceneNode) mParentNode).isInSceneGraph();
    }

    public void _notifyMoved() {
        // Mark light list being dirty, simply decrease
        // counter by one for minimise overhead
        --mLightListUpdated;
    }

    public void _notifyCurrentCamera(ENG_Camera cam) {
        if (mParentNode != null) {
            if (cam.getUseRenderingDistance() && mUpperDistance > 0.0f) {
                float rad = getBoundingRadius();
                float squaredDepth = mParentNode.getSquaredViewDepth(
                        cam.getLodCamera(), notifyCameraTemp);

                ENG_Vector4D scl = mParentNode._getDerivedScale();
                float factor = Math.max(Math.max(scl.x, scl.y), scl.z);

                float maxDist = mUpperDistance + rad * factor;
                mBeyondFarDistance = squaredDepth > ENG_Math.sqr(maxDist);
            } else {
                mBeyondFarDistance = false;
            }

            MovableObjectLodChangedEvent evt = new MovableObjectLodChangedEvent();
            evt.movableObject = this;
            evt.camera = cam;

            cam.getSceneManager()._notifyMovableObjectLodChanged(evt);
        }
    }

    public void setRenderQueueGroup(byte queueID) {
        if (queueID > ENG_RenderQueue.RenderQueueGroupID.RENDER_QUEUE_MAX.getID()) {
            throw new IllegalArgumentException("Render queue out of range!");
        }
        mRenderQueueID = queueID;
        mRenderQueueIDSet = true;
    }

    public void setRenderQueueGroupAndPriority(byte queueID, short priority) {
        setRenderQueueGroup(queueID);
        mRenderQueuePriority = priority;
        mRenderQueuePrioritySet = true;
    }

    public byte getRenderQueueGroup() {
        return mRenderQueueID;
    }

    public void _notifyCreator(ENG_MovableObjectFactory creator) {
        mCreator = creator;
    }

    public ENG_MovableObjectFactory _getCreator() {
        return mCreator;
    }

    public void _notifyManager(ENG_SceneManager manager) {
        mManager = manager;
    }

    public ENG_SceneManager _getManager() {
        return mManager;
    }

    public void addVisibilityFlags(int flags) {
        mVisibilityFlags |= flags;
    }

    public void removeVisibilityFlags(int flags) {
        mVisibilityFlags &= ~flags;
    }

    /**
     * @return the visibilityFlags
     */
    public int getVisibilityFlags() {
        return mVisibilityFlags;
    }

    /**
     * @param visibilityFlags the visibilityFlags to set
     */
    public void setVisibilityFlags(int visibilityFlags) {
        this.mVisibilityFlags = visibilityFlags;
    }

    public void _notifyAttached(ENG_Node node) {
        mParentNode = node;
    }

    public void setRenderingDistance(float dist) {
        mUpperDistance = dist;
        mSquaredUpperDistance = dist * dist;
    }

    public void setQueryFlags(int flags) {
        mQueryFlags = flags;
    }

    public int getQueryFlags() {
        return mQueryFlags;
    }

    public void addQueryFlags(int flags) {
        mQueryFlags |= flags;
    }

    public void removeQueryFlags(int flags) {
        mQueryFlags &= ~flags;
    }

    public static void setDefaultQueryFlags(int flags) {
        msDefaultQueryFlags = flags;
    }

    public static int getDefaultQueryFlags() {
        return msDefaultQueryFlags;
    }

    public static void setDefaultVisibilityFlags(int flags) {
        msDefaultVisibilityFlags = flags;
    }

    public static int getDefaultVisibilityFlags() {
        return msDefaultVisibilityFlags;
    }

    public int getLightMask() {
        return mLightMask;
    }

    /** @noinspection deprecation*/
    public ArrayList<ENG_Light> _getLightList() {
        return mLightList;
    }

    public void setDebugDisplayEnabled(boolean enabled) {
        mDebugDisplay = enabled;
    }

    public boolean getDebugDisplayEnabled() {
        return mDebugDisplay;
    }

    public float getRenderingDistance() {
        return mUpperDistance;
    }

    /**
     * @return the upperDistance
     */
    public float getUpperDistance() {
        return mUpperDistance;
    }

    /**
     * @return the squaredUpperDistance
     */
    public float getSquaredUpperDistance() {
        return mSquaredUpperDistance;
    }

//    /**
//     * @return the attached
//     */
//    public boolean isAttached() {
//        return attached;
//    }
//
//    /**
//     * @param atached the attached to set
//     */
//    public void setAttached(boolean atached) {
//        this.attached = atached;
//    }

    /**
     * @return the name
     */
    public String getName() {
        return mName;
    }

    public ENG_AxisAlignedBox getWorldBoundingBox() {
        return getWorldBoundingBox(false);
    }

    public ENG_AxisAlignedBox getWorldBoundingBox(boolean derive) {
        if (derive) {
            this.getBoundingBox(mWorldAABB);
            _getParentNodeFullTransform(fullTransform);
            mWorldAABB.transformAffine(fullTransform);
//            System.out.println("fullTransform: " + fullTransform + " mWorldAABB: " + mWorldAABB);
        }
        return mWorldAABB;
    }

    public void getWorldBoundingBox(ENG_AxisAlignedBox copy) {
        getWorldBoundingBox(false, copy);
    }

    public void getWorldBoundingBox(boolean derive, ENG_AxisAlignedBox copy) {
        copy.set(getWorldBoundingBox(derive));
    }

    public ENG_Sphere getWorldBoundingSphere() {
        return getWorldBoundingSphere(false);
    }

    public ENG_Sphere getWorldBoundingSphere(boolean derive) {
        if (derive) {
            mParentNode._getDerivedScale(derivedScale);
            float factor = Math.max(Math.max(derivedScale.x, derivedScale.y),
                    derivedScale.z);
            mWorldBoundingSphere.radius = getBoundingRadius() * factor;
            mParentNode._getDerivedPosition(derivedPosition);
            mWorldBoundingSphere.center.set(derivedPosition);
        }
        return mWorldBoundingSphere;
    }

    public void getWorldBoundingSphere(ENG_Sphere copy) {
        getWorldBoundingSphere(false, copy);
    }

    public void getWorldBoundingSphere(boolean derive, ENG_Sphere copy) {
        copy.set(mWorldBoundingSphere);
    }

    /** @noinspection deprecation*/
    public ArrayList<ENG_Light> queryLights() {
        if (mParentIsTagPoint) {
            //Ignore for now
        }

        if (mParentNode != null) {
            ENG_SceneNode sn = (ENG_SceneNode) mParentNode;
            long frame = sn.getCreator()._getLightsDirtyCounter();
            if (mLightListUpdated != frame) {
                mLightListUpdated = frame;
                mParentNode._getDerivedScale(derivedScale);
                float factor = Math.max(Math.max(derivedScale.x, derivedScale.y),
                        derivedScale.z);
                sn.findLigths(mLightList, getBoundingRadius() * factor, mLightMask);
            }
        } else {
            mLightList.clear();
        }
        return mLightList;
    }

    public int getTypeFlags() {
        if (mCreator != null) {
            return (int) mCreator.getTypeFlags();
        } else {
            return 0xffffffff;
        }
    }

    public void setLightMask(int lightMask) {
        mLightMask = lightMask;
        mLightListUpdated = 0;
    }

    public boolean isParentTagPoint() {
        return mParentIsTagPoint;
    }

    public abstract void _updateRenderQueue(ENG_RenderQueue queue);

    public ENG_Matrix4 _getParentNodeFullTransform() {
        ENG_Matrix4 ret = new ENG_Matrix4();
        _getParentNodeFullTransform(ret);
        return ret;
    }

    public void _getParentNodeFullTransform(ENG_Matrix4 ret) {
        if (mParentNode != null) {
            mParentNode._getFullTransform(ret);
        } else {
            ret.setIdentity();
        }
    }

/*	public ENG_AxisAlignedBox getWorldBoundingBox(boolean derive) {
		if (derive) {
			getBoundingBox(mWorldAABB);
			mWorldAABB.transformAffine(_getParentNodeFullTransform());
		}
	}*/

    public ENG_AxisAlignedBox getBoundingBox() {
        ENG_AxisAlignedBox ret = new ENG_AxisAlignedBox();
        getBoundingBox(ret);
        return ret;
    }

    public abstract void getBoundingBox(ENG_AxisAlignedBox ret);

    public abstract float getBoundingRadius();

    public void setCastShadows(boolean enabled) {
        mCastShadows = enabled;
    }

    public boolean getCastShadows() {
        return mCastShadows;
    }

    public boolean isVisible() {
        if ((!visible) || (mBeyondFarDistance) || (mRenderingDisabled)) {
            return false;
        }
        ENG_SceneManager sceneManager =
                ENG_RenderRoot.getRenderRoot()._getCurrentSceneManager();
        return !((sceneManager != null) &&
                ((mVisibilityFlags & sceneManager._getCombinedVisibilityMask()) ==
                        0));
    }

    private static class MORecvShadVisitor extends ENG_RenderableImpl.Visitor {

        public boolean anyReceiveShadows;

        public MORecvShadVisitor() {

        }

        @Override
        public void visit(ENG_Renderable rend, short lodIndex, boolean isDebug) {
            
            ENG_Technique tech = rend.getTechnique();
            boolean techReceivesShadows = (tech != null) &&
                    (tech.getParent().getReceiveShadows());
            anyReceiveShadows = anyReceiveShadows ||
                    techReceivesShadows || (tech == null);
        }

    }

    private final MORecvShadVisitor visitor = new MORecvShadVisitor();

    public boolean getReceiveShadows() {
        visitRenderables(visitor);
        return visitor.anyReceiveShadows;
    }

    public void visitRenderables(ENG_RenderableImpl.Visitor visitor) {
        visitRenderables(visitor, false);
    }

    public abstract void visitRenderables(ENG_RenderableImpl.Visitor visitor,
                                          boolean debugRenderables);

    /**
     * @return the visible
     */
    public boolean getVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the beyondFarDistance
     */
    public boolean isBeyondFarDistance() {
        return mBeyondFarDistance;
    }

    public abstract String getMovableType();

//    private static native long getParentNode(long obj);
//    private static native boolean hasParentNode(long obj);
}
