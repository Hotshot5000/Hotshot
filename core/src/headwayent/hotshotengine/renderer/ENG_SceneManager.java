/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/30/21, 6:23 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainActivity;
import headwayent.hotshotengine.ENG_IDisposable;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Plane;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Ray;
import headwayent.hotshotengine.ENG_RealRect;
import headwayent.hotshotengine.ENG_Sphere;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Boolean;
import headwayent.hotshotengine.basictypes.ENG_Byte;
import headwayent.hotshotengine.basictypes.ENG_Long;
import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.exception.ENG_DuplicateKeyException;
import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;
import headwayent.hotshotengine.renderer.ENG_Common.ClipResult;
import headwayent.hotshotengine.renderer.ENG_Common.CullingMode;
import headwayent.hotshotengine.renderer.ENG_Common.FogMode;
import headwayent.hotshotengine.renderer.ENG_Common.ShadowTechnique;
import headwayent.hotshotengine.renderer.ENG_GpuConstantDefinition.GpuParamVariability;
import headwayent.hotshotengine.renderer.ENG_GpuProgram.GpuProgramType;
import headwayent.hotshotengine.renderer.ENG_Light.LightTypes;
import headwayent.hotshotengine.renderer.ENG_LodListener.EntityMaterialLodChangedEvent;
import headwayent.hotshotengine.renderer.ENG_LodListener.EntityMeshLodChangedEvent;
import headwayent.hotshotengine.renderer.ENG_LodListener.MovableObjectLodChangedEvent;
import headwayent.hotshotengine.renderer.ENG_QueuedRenderableCollection.OrganisationMode;
import headwayent.hotshotengine.renderer.ENG_RenderQueue.RenderQueueGroupID;
import headwayent.hotshotengine.renderer.ENG_RenderSystemCapabilities.Capabilities;
import headwayent.hotshotengine.renderer.ENG_TextureUnitState.ContentType;
import headwayent.hotshotengine.renderer.ENG_TextureUnitState.TextureAddressingMode;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointer;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_SceneManagerNativeWrapper;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_ArrayListRing;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_RenderingThread;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_SlowCallExecutor;
import headwayent.hotshotengine.renderer.opengles.glsl.GLUtility;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

public class ENG_SceneManager implements ENG_IDisposable, ENG_NativePointer {

    public static final int WORLD_GEOMETRY_TYPE_MASK = 0x80000000;
    public static final int ENTITY_TYPE_MASK = 0x40000000;
    public static final int FX_TYPE_MASK = 0x20000000;
    public static final int STATICGEOMETRY_TYPE_MASK = 0x10000000;
    public static final int LIGHT_TYPE_MASK = 0x08000000;
    public static final int FRUSTUM_TYPE_MASK = 0x04000000;
    public static final int USER_TYPE_MASK_LIMIT = FRUSTUM_TYPE_MASK;
    public static final String DEFAULT_RESOURCE_GROUP_NAME = "General";
    public static final String INTERNAL_RESOURCE_GROUP_NAME = "Internal";
    public static final String AUTODETECT_RESOURCE_GROUP_NAME = "Autodetect";
    public static final byte V_1_FAST_RENDER_QUEUE = (byte) 3;
    public static final String MOVABLE_OBJECT_PARAM_PBSWORKFLOW = "pbsworkflow";
    public static final String MOVABLE_OBJECT_PARAM_ID = "id";
    public static final String MOVABLE_OBJECT_MESH_NAME = "meshname";
    public static final String MOVABLE_OBJECT_GROUP_NAME = "groupname";
    public static final String MOVABLE_OBJECT_SCENE_MEMORY_MANAGER_TYPE = "scenetype";
    public static final String MOVABLE_OBJECT_SUBMESH_COUNT = "submeshcount";

    @Override
    public long getPointer() {
        return wrapper.getPtr();
    }

    public enum SceneType
    {
        ST_GENERIC((short) 1),
        ST_EXTERIOR_CLOSE((short) 2),
        ST_EXTERIOR_FAR((short) 4),
        ST_EXTERIOR_REAL_FAR((short) 8),
        ST_INTERIOR((short) 16);

        private final short type;

        SceneType(short s) {
            type = s;
        }

        public short getType() {
            return type;
        }
    }

    public enum InstancingThreadedCullingMethod
    {
        INSTANCING_CULLING_SINGLETHREAD(0),
        INSTANCING_CULLING_THREADED(1);

        private final int cullingMethod;

        InstancingThreadedCullingMethod(int cullingMethod) {
            this.cullingMethod = cullingMethod;
        }

        public int getCullingMethod() {
            return cullingMethod;
        }
    }

    enum IlluminationRenderStage {
        /// No special illumination stage
        IRS_NONE,
        /// Render to texture stage, used for texture based shadows
        IRS_RENDER_TO_TEXTURE,
        /// Render from shadow texture to receivers stage
        IRS_RENDER_RECEIVER_PASS
    }

    enum SpecialCaseRenderQueueMode {
        /// Render only the queues in the special case list
        SCRQM_INCLUDE,
        /// Render all except the queues in the special case list
        SCRQM_EXCLUDE
    }

    public static class Listener {
        public Listener() {

        }

        public void preFindVisibleObjects(ENG_SceneManager source, IlluminationRenderStage irs,
                                          ENG_Viewport v) {

        }

        public void postFindVisibleObjects(ENG_SceneManager source,
                                           IlluminationRenderStage irs,
                                           ENG_Viewport v) {

        }

        public void shadowTexturesUpdated(int numberOfShadowTextures) {

        }

        /** @noinspection deprecation*/
        public void shadowTextureCasterPreViewProj(ENG_Light light,
                                                   ENG_Camera camera, int iteration) {

        }

        /** @noinspection deprecation*/
        public void shadowTextureReceiverPreViewProj(ENG_Light light,
                                                     ENG_Frustum frustum) {

        }

        /** @noinspection deprecation*/
        public boolean sortLightsAffectingFrustum(ArrayList<ENG_Light> lightList) {
            return false;
        }

        public void sceneManagerDestroyed(ENG_SceneManager source) {

        }
    }

    public static class SceneMgrQueuedRenderableVisitor
            extends ENG_QueuedRenderableVisitor {

        protected ENG_Pass mUsedPass;
        /// Target SM to send renderables to
        public ENG_SceneManager targetSceneMgr;
        /// Are we in transparent shadow caster mode?
        public boolean transparentShadowCastersMode;
        /// Automatic light handling?
        public boolean autoLights;
        /** @noinspection deprecation*/ /// Manual light list
        public ArrayList<ENG_Light> manualLightList;
        /// Scissoring if requested?
        public boolean scissoring;

        public SceneMgrQueuedRenderableVisitor() {

        }

        @Override
        public void visit(ENG_RenderablePass rp) {
            
            // Skip this one if we're in transparency cast shadows mode & it doesn't
            // Don't need to implement this one in the other visit methods since
            // transparents are never grouped, always sorted
            if (transparentShadowCastersMode &&
                    !rp.pass.mParent.getParent().getTransparencyCastsShadows()) {
                return;
            }

            // Give SM a chance to eliminate
            if (targetSceneMgr.validateRenderableForRendering(rp.pass, rp.renderable)) {
                mUsedPass = targetSceneMgr._setPass(rp.pass, false, true);
                targetSceneMgr.renderSingleObject(rp.renderable, mUsedPass, scissoring,
                        autoLights, manualLightList);
            }
        }

        @Override
        public boolean visit(ENG_Pass p) {
            
            if (!targetSceneMgr.validatePassForRendering(p)) {
                return false;
            }

            mUsedPass = targetSceneMgr._setPass(p, false, true);

            return true;
        }

        @Override
        public void visit(ENG_Renderable r) {
            
            if (targetSceneMgr.validateRenderableForRendering(mUsedPass, r)) {
                // Render a single object, this will set up auto params if required
                targetSceneMgr.renderSingleObject(r, mUsedPass, scissoring, autoLights, manualLightList);
            }
        }


    }

    public enum SceneMemoryMgrTypes
    {
        SCENE_DYNAMIC(0),
        SCENE_STATIC(1);
        public static final int NUM_SCENE_MEMORY_MANAGER_TYPES = 2;
        private final byte type;

        SceneMemoryMgrTypes(int i) {
            type = (byte) i;
        }

        public byte getType() {
            return type;
        }

        public static SceneMemoryMgrTypes getType(String s) {
            byte b = Byte.parseByte(s);
            switch (b) {
                case 0:
                    return SCENE_DYNAMIC;
                case 1:
                    return SCENE_STATIC;
                default:
                    throw new IllegalArgumentException(s + " is not a valid SceneMemoryMgrTypes");
            }
        }
    }

    private final String name;
    private final ENG_SceneManagerNativeWrapper wrapper;
    protected ENG_RenderQueue mRenderQueue;
    protected boolean mLastRenderQueueInvocationCustom;
    protected final ENG_ColorValue mAmbientLight = new ENG_ColorValue();
    protected ENG_RenderSystem mDestRenderSystem;
    protected final HashMap<String, ENG_Camera> mCameras = new HashMap<>();
    protected final HashMap<String, ENG_SceneNode> mSceneNodeMap = new HashMap<>();
    protected final ArrayList<ENG_SceneNode> mSceneNodeList = new ArrayList<>();
    protected final ArrayList<ENG_SceneNode> mSceneNodeNoUpdateList = new ArrayList<>();
    protected final ArrayList<ENG_SceneNode> mSceneNodeUpdateOnceList = new ArrayList<>();

    enum AttachableObjectType {
        ITEM,
        BILLBOARD_SET
    }

    private static class MovableObjectWithType {
        private final ENG_AttachableObjectIntf attachableObject;
        private final AttachableObjectType type;

        public MovableObjectWithType(ENG_AttachableObjectIntf attachableObject, AttachableObjectType type) {
            this.attachableObject = attachableObject;
            this.type = type;
        }

        public ENG_AttachableObjectIntf getAttachableObject() {
            return attachableObject;
        }

        public AttachableObjectType getType() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MovableObjectWithType that = (MovableObjectWithType) o;

            if (!attachableObject.equals(that.attachableObject)) return false;
            return type == that.type;
        }

        @Override
        public int hashCode() {
            int result = attachableObject.hashCode();
            result = 31 * result + type.hashCode();
            return result;
        }
    }

    private void removeAllMovableObjectType(AttachableObjectType type) {
        for (Iterator<MovableObjectWithType> it = mMovableObjectList.iterator(); it.hasNext(); ) {
            if (it.next().getType() == type) {
//                System.out.println("removeAllMovableObjectType type: " + type);
                it.remove();
            }
        }
    }

    private int getMovableObjectListSize(AttachableObjectType type) {
        int size = 0;
        for (MovableObjectWithType aMMovableObjectList : mMovableObjectList) {
            if (aMMovableObjectList.getType() == type) {
                ++size;
            }
        }
//        System.out.println("getMovableObjectListSize: " + type + " size: " + size);
        return size;
    }

    public int getMovableObjectListSize() {
//        System.out.println("getMovableObjectListSize: " + mMovableObjectList.size());
        return mMovableObjectList.size();
    }

    private void addMovableObject(ENG_AttachableObjectIntf obj, AttachableObjectType type) {
//        System.out.println("addMovableObject type: " + type);
        mMovableObjectList.add(new MovableObjectWithType(obj, type));
    }

    private void removeMovableObject(ENG_AttachableObjectIntf obj, AttachableObjectType type) {
//        System.out.println("removeMovableObject type: " + type);
        boolean remove = mMovableObjectList.remove(new MovableObjectWithType(obj, type));
        if (!remove) {
            throw new IllegalArgumentException("obj name: " + obj.getName() + " with type: " + type + " does not exist in movableObjectList");
        }
    }

    // This list is in sync with the equivalent list in blackholedarksun.cpp. It is used to transfer AABBs back from the
    // rendering thread to the main thread for collision detection.
    protected final ArrayList<MovableObjectWithType> mMovableObjectList = new ArrayList<>();
    protected final ENG_ArrayListRing<MovableObjectWithType> mMovableObjectRingList = new ENG_ArrayListRing<>(ENG_RenderingThread.BUFFER_COUNT);
    protected int mMovableObjectRingListCurrentFrame;
    private ENG_SceneNode[] mSceneRoot = new ENG_SceneNode[SceneMemoryMgrTypes.NUM_SCENE_MEMORY_MANAGER_TYPES];
    private ENG_Viewport mCurrentViewport;

    protected FogMode mFogMode;
    protected ENG_ColorValue mFogColour = new ENG_ColorValue();
    protected float mFogStart;
    protected float mFogEnd;
    protected float mFogDensity;

    protected final TreeSet<ENG_Byte> mSpecialCaseQueueList = new TreeSet<>();
    protected SpecialCaseRenderQueueMode mSpecialCaseQueueMode = SpecialCaseRenderQueueMode.SCRQM_EXCLUDE;

    protected long mLastFrameNumber;
    protected final ENG_Matrix4[] mTempXform = new ENG_Matrix4[256];
    protected boolean mResetIdentityView;
    protected boolean mResetIdentityProj;

    protected boolean mNormaliseNormalsOnScale = true;
    protected final boolean mFlipCullingOnNegativeScale = true;
    protected CullingMode mPassCullingMode;
    protected final HashMap<ENG_Camera, ENG_VisibleObjectsBoundsInfo> mCamVisibleObjectsMap = new HashMap<>();

    protected boolean mShowBoundingBoxes;

    private final ENG_RenderOperation ro = new ENG_RenderOperation();

    private final HashMap<ENG_NativePointer, ENG_Long> nativeObjectToTempPtr = new HashMap<>();
    private final HashMap<ENG_Long, ENG_NativePointer> nativePtrToNativeObject = new HashMap<>();

    public void addNativeObjectWithTempPtr(ENG_NativePointer nativeObject, long tempPtr) {
        addNativeObjectWithTempPtr(nativeObject, new ENG_Long(tempPtr));
    }

    public void addNativeObjectWithTempPtr(ENG_NativePointer nativeObject, ENG_Long tempPtr) {
        ENG_Long put = nativeObjectToTempPtr.put(nativeObject, tempPtr);
        if (put != null) {
            if (nativeObject instanceof ENG_MovableObject) {
                ENG_MovableObject movableObject = (ENG_MovableObject) nativeObject;
                throw new IllegalArgumentException("Movable Object: " + movableObject.getName() + " already exists with ptr: " +
                        movableObject.getPointer() + ". Trying to add the same object with ptr: " + tempPtr);
            } else {
                throw new IllegalArgumentException("Native Object with ptr: " + nativeObject.getPointer() + " already exists!");
            }
        }
    }

    public void removeNativeObjectWithTempPtr(ENG_NativePointer nativeObject) {
        ENG_Long remove = nativeObjectToTempPtr.remove(nativeObject);
        if (remove == null) {
            if (nativeObject instanceof ENG_MovableObject) {
                ENG_MovableObject movableObject = (ENG_MovableObject) nativeObject;
                throw new IllegalArgumentException("Movable Object: " + movableObject.getName() + " already removed!");
            } else {
                throw new IllegalArgumentException("Native Object with ptr: " + nativeObject.getPointer() + " already removed!");
            }
        }
    }

    public void clearNativeObjectWithTempPtrMap() {
        nativeObjectToTempPtr.clear();
    }

    public long getTempPtrForNativeObject(ENG_NativePointer nativeObject) {
        ENG_Long tempPtr = nativeObjectToTempPtr.get(nativeObject);
        if (tempPtr == null) {
            if (nativeObject instanceof ENG_MovableObject) {
                ENG_MovableObject movableObject = (ENG_MovableObject) nativeObject;
                throw new IllegalArgumentException("Movable Object: " + movableObject.getName() + " does not have a temp ptr!");
            } else {
                throw new IllegalArgumentException("Native Object with ptr: " + nativeObject.getPointer() + " does not have a temp ptr!");
            }
        }
        return tempPtr.getValue();
    }

    public void addNativePtrToNativeObject(long ptr, ENG_NativePointer nativeObject) {
        addNativePtrToNativeObject(new ENG_Long(ptr), nativeObject);
    }

    public void addNativePtrToNativeObject(ENG_Long ptr, ENG_NativePointer nativeObject) {
        ENG_NativePointer put = nativePtrToNativeObject.put(ptr, nativeObject);
        if (MainActivity.isDebugmode()) {
            if (nativeObject instanceof ENG_MovableObject) {
                ENG_MovableObject movableObject = (ENG_MovableObject) nativeObject;
//                System.out.println("Ptr: " + ptr + " linked to movable object: " + movableObject.getName());
            } else if (nativeObject instanceof ENG_Item) {
                ENG_Item item = (ENG_Item) nativeObject;
//                System.out.println("Ptr: " + ptr + " linked to item: " + item.getName());
            } else {
//                System.out.println("Ptr: " + ptr + " linked to nativeObject class: " + nativeObject.getClass().getName());
            }
        }
        if (put != null) {
            if (nativeObject instanceof ENG_MovableObject) {
                ENG_MovableObject movableObject = (ENG_MovableObject) nativeObject;
                throw new IllegalArgumentException("Ptr: " + ptr + " already linked to movable object: " + movableObject.getName() +
                        ". Trying to add the same pointer with movable object: " + movableObject.getName());
            } else if (nativeObject instanceof ENG_Item) {
                ENG_Item item = (ENG_Item) nativeObject;
//                System.out.println("Ptr: " + ptr + " linked to item: " + item.getName());
            } else {
                throw new IllegalArgumentException("Ptr: " + ptr +
                        " already linked to native object. nativeObject class: " + nativeObject.getClass().getName());
            }
        }
    }

    public void removeNativePtrToNativeObject(long ptr) {
        removeNativePtrToNativeObject(new ENG_Long(ptr));
    }

    public void removeNativePtrToNativeObject(ENG_Long ptr) {
        ENG_NativePointer remove = nativePtrToNativeObject.remove(ptr);
        if (MainActivity.isDebugmode()) {
            if (remove instanceof ENG_MovableObject) {
                ENG_MovableObject movableObject = (ENG_MovableObject) remove;
//                System.out.println("removing Ptr: " + ptr + " linked to movable object: " + movableObject.getName());
            } else if (remove instanceof ENG_Item) {
                ENG_Item item = (ENG_Item) remove;
//                System.out.println("removing Ptr: " + ptr + " linked to item: " + item.getName());
            } else {
//                System.out.println("removing ptr: " + ptr + " nativeObject class: " + remove.getClass().getName());
            }
        }
        if (remove == null) {
            throw new IllegalArgumentException("Native object does not exist for ptr: " + ptr);
        }
    }

    public void clearNativePtrToMovableObjectMap() {
        nativePtrToNativeObject.clear();
        if (MainActivity.isDebugmode()) {
            System.out.println("clearNativePtrToMovableObjectMap()");
        }
    }

    public ENG_NativePointer getNativeObjectFromNativePtr(long ptr) {
        return getNativeObjectFromNativePtr(ptr, true);
    }

    public ENG_NativePointer getNativeObjectFromNativePtr(long ptr, boolean throwExceptionIfNotFound) {
        return getNativeObjectFromNativePtr(new ENG_Long(ptr), throwExceptionIfNotFound);
    }

    public ENG_NativePointer getNativeObjectFromNativePtr(ENG_Long ptr) {
        return getNativeObjectFromNativePtr(ptr, true);
    }

    public ENG_NativePointer getNativeObjectFromNativePtr(ENG_Long ptr, boolean throwExceptionIfNotFound) {
        ENG_NativePointer nativeObject = nativePtrToNativeObject.get(ptr);
        if (nativeObject == null && throwExceptionIfNotFound) {
            throw new IllegalArgumentException("Native object does not exist for ptr: " + ptr);
        }
        return nativeObject;
    }

    static class MaterialLess implements Comparator<ENG_Material> {

        @Override
        public int compare(ENG_Material object1, ENG_Material object2) {
            
            if (object1.isTransparent() && !object2.isTransparent()) {
                return 1;
            } else if (!object1.isTransparent() && object2.isTransparent()) {
                return -1;
            } else {
                return 0;
            }
        }

    }

    /** @noinspection deprecation*/
    static class LightLess implements Comparator<ENG_Light> {

        /** @noinspection deprecation */
        @Override
        public int compare(ENG_Light object1, ENG_Light object2) {

            return Float.compare(object1.tempSquareDist, object2.tempSquareDist);
        }

    }

    static class LightInfo {
        /** @noinspection deprecation*/
        public ENG_Light light;
        /** @noinspection deprecation*/
        public ENG_Light.LightTypes type;
        public float range;
        public final ENG_Vector4D position = new ENG_Vector4D();
        public int lightMask;

        public boolean equals(LightInfo li) {
            return ((light == li.light) && (type == li.type) && (range == li.range) &&
                    (position.equals(li.position)) && (lightMask == li.lightMask));
        }
    }

    /** @noinspection deprecation*/
    protected final ArrayList<ENG_Light> mLightsAffectingFrustum = new ArrayList<>();
    protected ArrayList<LightInfo> mCachedLightInfos = new ArrayList<>();
    protected ArrayList<LightInfo> mTestLightInfos = new ArrayList<>();
    protected long mLightsDirtyCounter;
    /** @noinspection deprecation*/
    protected ArrayList<ENG_Light> mShadowTextureCurrentCasterLightList = new ArrayList<>();

    static class MovableObjectCollection {
        public final TreeMap<String, Object> map = new TreeMap<>();
        public ReentrantLock mutex = new ReentrantLock();
    }

    protected final TreeMap<String, MovableObjectCollection> mMovableObjectCollectionMap = new TreeMap<>();

    protected ReentrantLock mMovableObjectCollectionMapMutex = new ReentrantLock();

    protected MovableObjectCollection getMovableObjectCollection(String typeName) {
//        mMovableObjectCollectionMapMutex.lock();
//        try {
            MovableObjectCollection it = mMovableObjectCollectionMap.get(typeName);
            if (it == null) {
                MovableObjectCollection collection = new MovableObjectCollection();
                mMovableObjectCollectionMap.put(typeName, collection);
                return collection;
            } else {
                return it;
            }
//        } finally {
//            mMovableObjectCollectionMapMutex.unlock();
//        }
    }

    public boolean validatePassForRendering(ENG_Pass pass) {
        // Bypass if we're doing a texture shadow render and
        // this pass is after the first (only 1 pass needed for shadow texture render, and
        // one pass for shadow texture receive for modulative technique)
        // Also bypass if passes above the first if render state changes are
        // suppressed since we're not actually using this pass data anyway
        if (!mSuppressShadows && mCurrentViewport.getShadowsEnabled() &&
                ((isShadowTechniqueModulative() &&
                        mIlluminationStage == IlluminationRenderStage.IRS_RENDER_RECEIVER_PASS)
                        || mIlluminationStage == IlluminationRenderStage.IRS_RENDER_TO_TEXTURE ||
                        mSuppressRenderStateChanges) &&
                pass.getIndex().getValue() > 0) {
            return false;
        }

        // If using late material resolving, check if there is a pass with the same index
        // as this one in the 'late' material. If not, skip.
        if (isLateMaterialResolving()) {
            ENG_Technique lateTech = pass.mParent.getParent().getBestTechnique();
            if (lateTech.getNumPasses() <= pass.getIndex().getValue()) {
                return false;
            }
        }

        return true;
    }

    public boolean validateRenderableForRendering(ENG_Pass pass, ENG_Renderable rend) {
        
        // Skip this renderable if we're doing modulative texture shadows, it casts shadows
        // and we're doing the render receivers pass and we're not self-shadowing
        // also if pass number > 0
//        if (!mSuppressShadows && mCurrentViewport.getShadowsEnabled() &&
//                isShadowTechniqueTextureBased()) {
//            if (mIlluminationStage == IlluminationRenderStage.IRS_RENDER_RECEIVER_PASS &&
//                    rend.getCastsShadows() && !mShadowTextureSelfShadow) {
//                return false;
//            }
//        }
//
//        // Some duplication here with validatePassForRendering, for transparents
//        if (((isShadowTechniqueModulative() &&
//                mIlluminationStage == IlluminationRenderStage.IRS_RENDER_RECEIVER_PASS)
//                || mIlluminationStage == IlluminationRenderStage.IRS_RENDER_TO_TEXTURE ||
//                mSuppressRenderStateChanges) &&
//                pass.getIndex().getValue() > 0) {
//            return false;
//        }
        return true;
    }

    /** @noinspection deprecation*/
    protected void renderSingleObject(ENG_Renderable rend, ENG_Pass pass,
                                      boolean lightScissoringClipping, boolean doLightIteration,
                                      ArrayList<ENG_Light> manualLightList) {
        short numMatrices;


        rend.getRenderOperation(ro);

        ro.srcRenderable = rend;

        ENG_GpuProgram vprog = pass.hasVertexProgram() ? pass.getVertexProgram() : null;

        boolean passTransformState = true;

        if (vprog != null) {
            passTransformState = vprog.getPassTransformStates();
        }

        numMatrices = rend.getNumWorldTransforms();

        if (numMatrices > 0) {
            rend.getWorldTransforms(mTempXform);

            if (mCameraRelativeRendering && !rend.getUseIdentityView()) {
                for (short i = 0; i < numMatrices; ++i) {
                    mTempXform[i].setTrans(mTempXform[i].getTransAsVec4().subAsPt(mCameraRelativePosition));
                }
            }

            if (passTransformState) {
                if (numMatrices > 1) {
                    mDestRenderSystem._setWorldMatrixes(mTempXform, numMatrices);
                } else {
                    mDestRenderSystem._setWorldMatrix(mTempXform[0]);
                }
            }
        }
        // Issue view / projection changes if any
        useRenderableViewProjMode(rend, passTransformState);

        mGpuParamsDirty |= GpuParamVariability.GPV_PER_OBJECT.getVariability();

//        if (!mSuppressRenderStateChanges) {
//            boolean passSurfaceAndLightParams = true;

//            if (pass.isProgrammable()) {
                // Tell auto params object about the renderable change
                mAutoParamDataSource.setCurrentRenderable(rend);
                // Tell auto params object about the world matrices, eliminated query from renderable again
                mAutoParamDataSource.setWorldMatrices(mTempXform, numMatrices);
//                if (vprog != null) {
//                    passSurfaceAndLightParams = vprog.getPassSurfaceAndLightStates();
//                }
//            }

            // Reissue any texture gen settings which are dependent on view matrix
        /*	Iterator<ENG_TextureUnitState> texIter = pass.getTextureUnitStateIterator();
			int unit = 0;
			while (texIter.hasNext()) {
				ENG_TextureUnitState pTex = texIter.next();
				if (pTex.hasViewRelativeTextureCoordinateGeneration()) {
					
				}
			}*/

            // Sort out normalisation
            // Assume first world matrix representative - shaders that use multiple
            // matrices should control renormalisation themselves
//            if ((pass.getNormaliseNormals() || mNormaliseNormalsOnScale) &&
//                    mTempXform[0].hasScale()) {
//                mDestRenderSystem.setNormaliseNormals(true);
//            } else {
//                mDestRenderSystem.setNormaliseNormals(false);
//            }

            // Sort out negative scaling
            // Assume first world matrix representative
            if (mFlipCullingOnNegativeScale) {
                CullingMode cullMode = mPassCullingMode;

//                if (mTempXform[0].hasNegativeScale()) {
//                    switch (mPassCullingMode) {
//                        case CULL_CLOCKWISE:
//                            cullMode = CullingMode.CULL_ANTICLOCKWISE;
//                            break;
//                        case CULL_ANTICLOCKWISE:
//                            cullMode = CullingMode.CULL_CLOCKWISE;
//                            break;
//                        case CULL_NONE:
//                            break;
//                    }
//                }

                // this also copes with returning from negative scale in previous render op
                // for same pass
                if (cullMode != mDestRenderSystem._getCullingMode()) {
                    mDestRenderSystem._setCullingMode(cullMode);
                }
            }

            // Set up the solid / wireframe override
            // Precedence is Camera, Object, Material
            // Camera might not override object if not overrideable
//            PolygonMode reqMode = pass.getPolygonMode();
//            if (pass.getPolygonModeOverrideable() && rend.getPolygonModeOverrideable()) {
//                PolygonMode camPolyMode = mCameraInProgress.getPolygonMode();
//                // check camera detial only when render detail is overridable
//                if (reqMode.getMode() > camPolyMode.getMode()) {
//                    // only downgrade detail; if cam says wireframe we don't go up to solid
//                    reqMode = camPolyMode;
//                }
//            }
//            mDestRenderSystem._setPolygonMode(reqMode);

//            if (doLightIteration) {
//
//            } else {
                // Even if manually driving lights, check light type passes
//                boolean skipBecauseOfLightType = false;
//                if (pass.getRunOnlyForOneLightType()) {
//                    if ((manualLightList == null) || (manualLightList.size() == 1 &&
//                            manualLightList.get(0).getType() != pass.getOnlyLightType())) {
//                        skipBecauseOfLightType = true;
//                    }
//                }

//                if (!skipBecauseOfLightType) {
//                    fireRenderSingleObject(rend, pass, mAutoParamDataSource,
//                            manualLightList, mSuppressRenderStateChanges);
                    // Do we need to update GPU program parameters?
//                    if (pass.isProgrammable()) {
//                        // Do we have a manual light list?
//                        if (manualLightList != null) {
//                            useLightsGpuProgram(pass, manualLightList);
//                        }
//                    }

                    // Use manual lights if present, and not using vertex programs that don't use fixed pipeline
//                    if (manualLightList != null && pass.getLightingEnabled() && passSurfaceAndLightParams) {
//                        useLights(manualLightList, pass.getMaxSimultaneousLights());
//                    }

                    // optional light scissoring
                    ClipResult scissored = ClipResult.CLIPPED_NONE;
                    ClipResult clipped = ClipResult.CLIPPED_NONE;
//                    if (lightScissoringClipping && manualLightList != null && pass.getLightScissoringEnabled()) {
//                        scissored = buildAndSetScissor(manualLightList, mCameraInProgress);
//                    }
//                    if (lightScissoringClipping && manualLightList != null && pass.getLightClipPlanesEnabled()) {
//                        clipped = buildAndSetLightClip(manualLightList);
//                    }

                    // don't bother rendering if clipped / scissored entirely
//                    if (scissored != ClipResult.CLIPPED_ALL && clipped != ClipResult.CLIPPED_ALL) {
                        mDestRenderSystem.setCurrentPassIterationCount(pass.getPassIterationCount());

        GLUtility.checkForGLSLError(
                "GLSLLinkProgram::GLSLLinkProgram",
                "Error Before creating GLSL Program Object");

                        // Finalise GPU parameter bindings
                        updateGpuProgramParameters(pass);

        GLUtility.checkForGLSLError(
                "GLSLLinkProgram::GLSLLinkProgram",
                "Error Before creating GLSL Program Object");

//                        if (rend.preRender(this, mDestRenderSystem)) {
                            mDestRenderSystem._render(ro);
//                        }
//                        rend.postRender(this, mDestRenderSystem);
                        // Here we should add a null object and let it render
                        //	MTGLES20.setRenderingAllowed(true);
                        //	GLRenderSurface.getSingleton().requestRender(true);
                        //	MainApp.getMainThread().flushGLPipeline();
//                    }

//                    if (scissored == ClipResult.CLIPPED_SOME) {
//                        resetScissors();
//                    }
//
//                    if (clipped == ClipResult.CLIPPED_SOME) {
//                        resetLightClip();
//                    }
//                }
//            }
//        }
    }

    /** @noinspection deprecation*/
    public ClipResult buildAndSetLightClip(ArrayList<ENG_Light> manualLightList) {
        
        return null;
    }

    /** @noinspection deprecation*/
    public ENG_RealRect getLightScissorRect(ENG_Light l, ENG_Camera cam) {
        checkCachedLightClippingInfo();

        LightClippingInfo ci = mLightClippingInfoMap.get(l);
        if (ci == null) {
            ci = new LightClippingInfo();
            mLightClippingInfoMap.put(l, ci);
        }

        if (!ci.scissorsValid) {
            buildScissor(l, cam, ci.scissorsRect);
            ci.scissorsValid = true;
        }
        return ci.scissorsRect;
    }

    /** @noinspection deprecation*/
    protected void buildScissor(ENG_Light l, ENG_Camera cam,
                                ENG_RealRect scissorsRect) {
        
        ENG_Sphere sphere = new ENG_Sphere(l.mPosition, l.getAttenuationRange());
        float[] f = new float[4];
        cam.projectSphere(sphere, f);
        scissorsRect.left = f[0];
        scissorsRect.top = f[1];
        scissorsRect.right = f[2];
        scissorsRect.bottom = f[3];
    }

    /** @noinspection deprecation */
    public ClipResult buildAndSetScissor(ArrayList<ENG_Light> ll,
                                         ENG_Camera cam) {
        
        if (!mDestRenderSystem.getCapabilities().hasCapability(
                Capabilities.RSC_SCISSOR_TEST)) {
            return ClipResult.CLIPPED_NONE;
        }

        ENG_RealRect finalRect = new ENG_RealRect();

        // init (inverted since we want to grow from nothing)
        finalRect.left = finalRect.bottom = 1.0f;
        finalRect.right = finalRect.top = -1.0f;

        for (int i = 0; i < ll.size(); ++i) {
            ENG_Light l = ll.get(i);

            if (l.getType() == LightTypes.LT_DIRECTIONAL) {
                return ClipResult.CLIPPED_NONE;
            }

            ENG_RealRect scissorRect = getLightScissorRect(l, cam);

            finalRect.left = Math.min(finalRect.left, scissorRect.left);
            finalRect.bottom = Math.min(finalRect.bottom, scissorRect.bottom);
            finalRect.right = Math.max(finalRect.right, scissorRect.right);
            finalRect.top = Math.max(finalRect.top, scissorRect.top);
        }

        if (finalRect.left >= 1.0f || finalRect.right <= -1.0f ||
                finalRect.top <= -1.0f || finalRect.bottom >= 1.0f) {
            return ClipResult.CLIPPED_ALL;
        }

        // Some scissoring?
        if (finalRect.left > -1.0f || finalRect.right < 1.0f ||
                finalRect.bottom > -1.0f || finalRect.top < 1.0f) {
            // Turn normalised device coordinates into pixels
            int iLeft = mCurrentViewport.getActualLeft(),
                    iTop = mCurrentViewport.getActualTop(),
                    iWidth = mCurrentViewport.getActualWidth(),
                    iHeight = mCurrentViewport.getActualHeight();

            int szLeft = (int) (iLeft + ((finalRect.left + 1.0f) * 0.5f * iWidth)),
                    szRight = (int) (iLeft + ((finalRect.right + 1.0f) * 0.5f * iWidth)),
                    szTop = (int) (iTop + ((-finalRect.top + 1.0f) * 0.5f * iHeight)),
                    szBottom = (int) (iTop + ((-finalRect.bottom + 1.0f) * 0.5f * iHeight));

            mDestRenderSystem.setScissorTest(true, szLeft, szTop, szRight, szBottom);

            return ClipResult.CLIPPED_SOME;
        } else {
            return ClipResult.CLIPPED_NONE;
        }

    }

    protected void resetScissors() {
        if (!mDestRenderSystem.getCapabilities().hasCapability(Capabilities.RSC_SCISSOR_TEST)) {
            return;
        }
        mDestRenderSystem.setScissorTest(false);
    }

    protected void checkCachedLightClippingInfo() {
        long frame = ENG_RenderRoot.getRenderRoot().getNextFrameNumber();
        if (frame != mLightClippingInfoMapFrameNumber) {
            // reset cached clip information
            mLightClippingInfoMap.clear();
            mLightClippingInfoMapFrameNumber = frame;
        }
    }

    /** @noinspection deprecation*/
    protected ArrayList<ENG_Plane> getLightClippingPlanes(ENG_Light l) {
        checkCachedLightClippingInfo();

        LightClippingInfo ci = mLightClippingInfoMap.get(l);
        if (ci == null) {
            ci = new LightClippingInfo();
            mLightClippingInfoMap.put(l, ci);
        }
        if (!ci.clipPlanesValid) {
            buildLightClip(l, ci.clipPlanes);
            ci.clipPlanesValid = true;
        }
        return ci.clipPlanes;
    }

    /** @noinspection deprecation*/
    public void buildLightClip(ENG_Light l, ArrayList<ENG_Plane> planes) {
        
        if (!mDestRenderSystem.getCapabilities().hasCapability(
                Capabilities.RSC_USER_CLIP_PLANES)) {
            return;
        }

        planes.clear();

        ENG_Vector4D pos = l.getDerivedPosition();
        float r = l.getAttenuationRange();

        switch (l.getType()) {
            case LT_POINT:
                planes.add(new ENG_Plane(ENG_Math.PT4_X_UNIT, pos.addAsVec(
                        new ENG_Vector4D(-r, 0.0f, 0.0f, 0.0f))));
                planes.add(new ENG_Plane(ENG_Math.PT4_NEGATIVE_X_UNIT, pos.addAsVec(
                        new ENG_Vector4D(r, 0.0f, 0.0f, 0.0f))));
                planes.add(new ENG_Plane(ENG_Math.PT4_Y_UNIT, pos.addAsVec(
                        new ENG_Vector4D(0.0f, -r, 0.0f, 0.0f))));
                planes.add(new ENG_Plane(ENG_Math.PT4_NEGATIVE_Y_UNIT, pos.addAsVec(
                        new ENG_Vector4D(0.0f, r, 0.0f, 0.0f))));
                planes.add(new ENG_Plane(ENG_Math.PT4_Z_UNIT, pos.addAsVec(
                        new ENG_Vector4D(0.0f, 0.0f, -r, 0.0f))));
                planes.add(new ENG_Plane(ENG_Math.PT4_NEGATIVE_Z_UNIT, pos.addAsVec(
                        new ENG_Vector4D(0.0f, 0.0f, r, 0.0f))));
                break;
            case LT_SPOTLIGHT: {
                ENG_Vector4D dir = l.getDerivedDirection();

                planes.add(new ENG_Plane(dir, pos));
                planes.add(new ENG_Plane(dir.invert(), pos.addAsVec(dir.mulAsVec(r))));

                ENG_Vector4D up = ENG_Math.VEC4_Y_UNIT;

                if (Math.abs(up.dotProduct(dir)) >= 1.0f) {
                    up = ENG_Math.VEC4_Z_UNIT;
                }

                ENG_Vector4D right = dir.crossProduct(up);
                right.normalize();
                up = right.crossProduct(dir);
                up.normalize();

                ENG_Quaternion q = new ENG_Quaternion();
                q.fromAxes(right, up, dir.invert());

                ENG_Vector4D tl = new ENG_Vector4D();
                ENG_Vector4D tr = new ENG_Vector4D();
                ENG_Vector4D bl = new ENG_Vector4D();
                ENG_Vector4D br = new ENG_Vector4D();

                float d = ENG_Math.tan(l.getSpotlightOuterAngleCopy().valueRadians() * 0.5f) * r;
                tl = q.mul(new ENG_Vector4D(-d, d, -r, 1.0f));
                tr = q.mul(new ENG_Vector4D(d, d, -r, 1.0f));
                bl = q.mul(new ENG_Vector4D(-d, -d, -r, 1.0f));
                br = q.mul(new ENG_Vector4D(d, -d, -r, 1.0f));

                planes.add(new ENG_Plane(tl.crossProduct(tr).normalizedCopy(), pos));
                planes.add(new ENG_Plane(tr.crossProduct(br).normalizedCopy(), pos));
                planes.add(new ENG_Plane(br.crossProduct(bl).normalizedCopy(), pos));
                planes.add(new ENG_Plane(bl.crossProduct(tl).normalizedCopy(), pos));
            }
            break;
            default:
                break;
        }
    }

    protected void resetLightClip() {
        if (!mDestRenderSystem.getCapabilities().hasCapability(
                Capabilities.RSC_USER_CLIP_PLANES)) {
            return;
        }

        mDestRenderSystem.resetClipPlanes();
    }

    private void useRenderableViewProjMode(ENG_Renderable pRend,
                                           boolean fixedFunction) {
        
        boolean useIdentityView = pRend.getUseIdentityView();

        if (useIdentityView) {
            if (fixedFunction) {
                setViewMatrix(ENG_Math.MAT4_IDENTITY);
            }
            mGpuParamsDirty |= GpuParamVariability.GPV_GLOBAL.getVariability();
            mResetIdentityView = true;
        }

        boolean useIdentityProj = pRend.getUseIdentityProjection();

        if (useIdentityProj) {
            if (fixedFunction) {
                ENG_Matrix4 mat = new ENG_Matrix4();
                mDestRenderSystem._convertProjectionMatrix(ENG_Math.MAT4_IDENTITY, mat);
                mDestRenderSystem._setProjectionMatrix(mat);
            }
            mGpuParamsDirty |= GpuParamVariability.GPV_GLOBAL.getVariability();
            mResetIdentityProj = true;
        }
    }

    protected ENG_AutoParamDataSource createAutoParamDataSource() {
        return new ENG_AutoParamDataSource();
    }

    protected final ENG_AutoParamDataSource mAutoParamDataSource;
    protected ENG_CompositorChain mActiveCompositorChain;
    protected boolean mLateMaterialResolving;

    public static class RenderContext {
        public ENG_RenderQueue renderQueue;
        public ENG_Viewport viewport;
        public ENG_Camera camera;
    }

    /// The active renderable visitor class - subclasses could override this
    protected final SceneMgrQueuedRenderableVisitor mActiveQueuedRenderableVisitor;
    /// Storage for default renderable visitor
    protected final SceneMgrQueuedRenderableVisitor mDefaultQueuedRenderableVisitor =
            new SceneMgrQueuedRenderableVisitor();

    /// Last light sets
    protected int mLastLightHash;
    protected short mLastLightLimit;
    protected int mLastLightHashGpuProgram;
    /// Gpu params that need rebinding (mask of GpuParamVariability)
    protected short mGpuParamsDirty = GpuParamVariability.GPV_ALL.getVariability();

    protected final ArrayList<ENG_RenderQueueListener> mRenderQueueListeners =
            new ArrayList<>();

    protected final ArrayList<ENG_RenderObjectListener> mRenderObjectListeners =
            new ArrayList<>();

    protected final ArrayList<Listener> mListener = new ArrayList<>();

    protected void firePreFindVisibleObjects(ENG_Viewport v) {
        for (int i = 0; i < mListener.size(); ++i) {
            mListener.get(i).preFindVisibleObjects(this, mIlluminationStage, v);
        }
    }

    protected void firePostFindVisibleObjects(ENG_Viewport v) {
        for (int i = 0; i < mListener.size(); ++i) {
            mListener.get(i).postFindVisibleObjects(this, mIlluminationStage, v);
        }
    }

    protected void firePreRenderQueues() {
        for (int i = 0; i < mRenderQueueListeners.size(); ++i) {
            mRenderQueueListeners.get(i).preRenderQueues();
        }
    }

    protected void firePostRenderQueues() {
        for (int i = 0; i < mRenderQueueListeners.size(); ++i) {
            mRenderQueueListeners.get(i).postRenderQueues();
        }
    }

    protected boolean fireRenderQueueStarted(byte id, String invocation) {
        ENG_Boolean skip = new ENG_Boolean();

        for (int i = 0; i < mRenderQueueListeners.size(); ++i) {
            mRenderQueueListeners.get(i).renderQueueStarted(id, invocation, skip);
        }
        return skip.getValue();
    }

    protected boolean fireRenderQueueEnded(byte id, String invocation) {
        ENG_Boolean repeat = new ENG_Boolean();

        for (int i = 0; i < mRenderQueueListeners.size(); ++i) {
            mRenderQueueListeners.get(i).renderQueueEnded(id, invocation, repeat);
        }
        return repeat.getValue();
    }

    /** @noinspection deprecation*/
    protected void fireRenderSingleObject(ENG_Renderable rend, ENG_Pass pass,
                                          ENG_AutoParamDataSource source, ArrayList<ENG_Light> lightList,
                                          boolean suppressRenderStateChanges) {
        for (int i = 0; i < mRenderObjectListeners.size(); ++i) {
            mRenderObjectListeners.get(i).notifyRenderSingleObject(rend, pass,
                    source, lightList, suppressRenderStateChanges);
        }
    }

    /** @noinspection deprecation*/
    protected void useLights(ArrayList<ENG_Light> lights, short limit) {
        if (lights.hashCode() != mLastLightHash || limit != mLastLightLimit) {
            mDestRenderSystem._useLights(lights, limit);
            mLastLightHash = lights.hashCode();
            mLastLightLimit = limit;
        }
    }

    protected void setViewMatrix(ENG_Matrix4 m) {
        mDestRenderSystem._setViewMatrix(m);
        if (mDestRenderSystem.areFixedFunctionLightsInViewSpace()) {
            // reset light hash if we've got lights already set
            mLastLightHash = (mLastLightHash != 0) ? 0 : mLastLightHash;
        }
    }

    /** @noinspection deprecation*/
    protected void useLightsGpuProgram(ENG_Pass pass, ArrayList<ENG_Light> lights) {
        if (lights.hashCode() != mLastLightHashGpuProgram) {
            mAutoParamDataSource.setCurrentLightList(lights);
            mGpuParamsDirty |= GpuParamVariability.GPV_LIGHTS.getVariability();

            mLastLightHashGpuProgram = lights.hashCode();
        }
    }

    protected void bindGpuProgram(ENG_GpuProgram prog) {
        // need to dirty the light hash, and paarams that need resetting, since program params will have been invalidated
        // Use 1 to guarantee changing it (using 0 could result in no change if list is empty)
        // Hash == 1 is almost impossible to achieve otherwise
        mLastLightHashGpuProgram = 1;
        mGpuParamsDirty = GpuParamVariability.GPV_ALL.getVariability();
        mDestRenderSystem.bindGpuProgram(prog);
    }

    protected void updateGpuProgramParameters(ENG_Pass pass) {
//        if (pass.isProgrammable()) {

            if (mGpuParamsDirty == 0) {
                return;
            }

//            if (mGpuParamsDirty != 0)
                pass._updateAutoParams(mAutoParamDataSource, mGpuParamsDirty);

            if (pass.hasVertexProgram()) {
                mDestRenderSystem.bindGpuProgramParameters(GpuProgramType.GPT_VERTEX_PROGRAM,
                        pass.getVertexProgramParameters(), mGpuParamsDirty);
            }

		/*	if (pass.hasGeometryProgram())
			{
				mDestRenderSystem->bindGpuProgramParameters(GPT_GEOMETRY_PROGRAM,
					pass->getGeometryProgramParameters(), mGpuParamsDirty);
			}*/

            if (pass.hasFragmentProgram()) {
                mDestRenderSystem.bindGpuProgramParameters(GpuProgramType.GPT_FRAGMENT_PROGRAM,
                        pass.getFragmentProgramParameters(), mGpuParamsDirty);
            }

            mGpuParamsDirty = 0;
//        }
    }

    protected int mVisibilityMask = ENG_MovableObject.msDefaultVisibilityFlags;
    protected boolean mFindVisibleObjects = true;

    protected final ENG_ColorValue mShadowColour = new ENG_ColorValue(0.25f, 0.25f, 0.25f);
    protected final ShadowTechnique mShadowTechnique = ShadowTechnique.SHADOWTYPE_NONE;
    protected float mDefaultShadowFarDist;
    protected float mDefaultShadowFarDistSquared;

    static class LightClippingInfo {
        public final ENG_RealRect scissorsRect = new ENG_RealRect();
        public final ArrayList<ENG_Plane> clipPlanes = new ArrayList<>();
        public boolean scissorsValid;
        public boolean clipPlanesValid;
    }

    /** @noinspection deprecation*/
    protected TreeMap<ENG_Light, LightClippingInfo> mLightClippingInfoMap;
    protected long mLightClippingInfoMapFrameNumber;

    protected final ArrayList<ENG_LodListener> mLodListeners =
            new ArrayList<>();

    protected final ArrayList<ENG_LodListener.MovableObjectLodChangedEvent>
            mMovableObjectLodChangedEvents =
            new ArrayList<>();

    protected final ArrayList<ENG_LodListener.EntityMeshLodChangedEvent>
            mEntityMeshLodChangedEvents =
            new ArrayList<>();

    protected final ArrayList<ENG_LodListener.EntityMaterialLodChangedEvent>
            mEntityMaterialLodChangedEvents =
            new ArrayList<>();

    private final ENG_Vector4D temp = new ENG_Vector4D();
    private boolean mSuppressRenderStateChanges;
    private boolean mShadowTextureSelfShadow;
    private boolean mDisplayNodes;
    private ENG_Camera mCameraInProgress;
    private float mShadowDirLightExtrudeDist;
    private boolean mSuppressShadows;
    private ENG_SceneNode mSkyPlaneNode;
    private ENG_SceneNode mSkyDomeNode;
    private boolean mSkyPlaneEnabled;
    private boolean mSkyDomeEnabled;

    //protected long mLightsDirtyCounter;

    public ENG_SceneManager(short typeMask, int numThreads, int threadCullingMethod,
                            String instanceName) {
        wrapper = new ENG_SceneManagerNativeWrapper(ENG_RenderRoot.getRenderRoot().getPointer(),
                typeMask, numThreads,
                threadCullingMethod, instanceName);
        for (int i = 0; i < SceneMemoryMgrTypes.NUM_SCENE_MEMORY_MANAGER_TYPES; ++i) {

        }

        for (int i = 0; i < mTempXform.length; ++i) {
            mTempXform[i] = new ENG_Matrix4();
        }
        name = instanceName;
        mGpuParamsDirty = GpuParamVariability.GPV_ALL.getVariability();
        _setDestinationRenderSystem(ENG_RenderRoot.getRenderRoot().getRenderSystem());

        mActiveQueuedRenderableVisitor = mDefaultQueuedRenderableVisitor;

        // create the auto param data source instance
        mAutoParamDataSource = createAutoParamDataSource();

        setV1FastRenderQueue(V_1_FAST_RENDER_QUEUE);
    }

    @Override
    public void destroy() {
        wrapper.destroy();
    }

    public void _setDestinationRenderSystem(ENG_RenderSystem rs) {
        mDestRenderSystem = rs;
    }

    public ENG_RenderQueue getRenderQueue() {
        if (mRenderQueue == null) {
            initRenderQueue();
        }
        return mRenderQueue;
    }

    protected void initRenderQueue() {
        mRenderQueue = new ENG_RenderQueue();
        mRenderQueue.getQueueGroup(
                RenderQueueGroupID.RENDER_QUEUE_BACKGROUND.getID()).setShadowsEnabled(false);
        mRenderQueue.getQueueGroup(
                RenderQueueGroupID.RENDER_QUEUE_OVERLAY.getID()).setShadowsEnabled(false);
        mRenderQueue.getQueueGroup(
                RenderQueueGroupID.RENDER_QUEUE_SKIES_EARLY.getID()).setShadowsEnabled(false);
        mRenderQueue.getQueueGroup(
                RenderQueueGroupID.RENDER_QUEUE_SKIES_LATE.getID()).setShadowsEnabled(false);
    }

    public void addSpecialCaseRenderQueue(byte qid) {
        addSpecialCaseRenderQueue(new ENG_Byte(qid));
    }

    public void addSpecialCaseRenderQueue(ENG_Byte qid) {
        mSpecialCaseQueueList.add(qid);
    }

    public void removeSpecialCaseRenderQueue(byte qid) {
        removeSpecialCaseRenderQueue(new ENG_Byte(qid));
    }

    public void removeSpecialCaseRenderQueue(ENG_Byte qid) {
        mSpecialCaseQueueList.remove(qid);
    }

    public void clearSpecialCaseRenderQueue() {
        mSpecialCaseQueueList.clear();
    }

    public void setSpecialCaseRenderQueueMode(SpecialCaseRenderQueueMode mode) {
        mSpecialCaseQueueMode = mode;
    }

    public SpecialCaseRenderQueueMode getSpecialCaseRenderQueueMode() {
        return mSpecialCaseQueueMode;
    }

    public boolean isRenderQueueToBeProcessed(byte qid) {
        return isRenderQueueToBeProcessed(new ENG_Byte(qid));
    }

    public boolean isRenderQueueToBeProcessed(ENG_Byte qid) {
        boolean inList = mSpecialCaseQueueList.contains(qid);
        return (inList && mSpecialCaseQueueMode == SpecialCaseRenderQueueMode.SCRQM_INCLUDE)
                || (!inList && mSpecialCaseQueueMode == SpecialCaseRenderQueueMode.SCRQM_EXCLUDE);
    }

    public ENG_Camera createCamera(String name) {
        if (mCameras.containsKey(name)) {
            throw new IllegalArgumentException(
                    "A camera with the name " + name + " already exists");
        }

        long cameraPtr = wrapper.createCamera(name, true, false);
        ENG_Camera c = new ENG_Camera(name, this, cameraPtr);
        ENG_SceneNode rootSceneNode = getRootSceneNode();
//        rootSceneNode.attachObject(new ENG_CameraNative(c));
        rootSceneNode.attachCamera(c);

        mCameras.put(name, c);

        mCamVisibleObjectsMap.put(c, new ENG_VisibleObjectsBoundsInfo());

        return c;
    }

    public ENG_Camera getCamera(String name) {
        ENG_Camera c = mCameras.get(name);
        if (c == null) {
            throw new IllegalArgumentException("Cannot find Camera with name " + name);
        }
        return c;
    }

    public boolean hasCamera(String name) {
        return mCameras.containsKey(name);
    }

    public void destroyCamera(ENG_Camera cam) {
        destroyCamera(cam.getName());
    }

    public void destroyCamera(String name) {
        ENG_Camera c = mCameras.get(name);
        if (c != null) {
            mCamVisibleObjectsMap.remove(c);

            mDestRenderSystem._notifyCameraRemoved(c);

            mCameras.remove(name);
        }
    }

    public void destroyAllCameras() {
        for (Entry<String, ENG_Camera> stringENGCameraEntry : mCameras.entrySet()) {
            mDestRenderSystem._notifyCameraRemoved(stringENGCameraEntry.getValue());
        }
        mCameras.clear();
        mCamVisibleObjectsMap.clear();
    }

    public ENG_LightNative createLight(String name, long id) {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("id", String.valueOf(id));
        return (ENG_LightNative) createMovableObject(name, ENG_LightNativeFactory.FACTORY_TYPE_NAME, params);
    }

    public ENG_LightNative getLight(String name) {
        return (ENG_LightNative) getMovableObject(name, ENG_LightNativeFactory.FACTORY_TYPE_NAME);
    }

    public boolean hasLight(String name) {
        return hasMovableObject(name, ENG_LightNativeFactory.FACTORY_TYPE_NAME);
    }

    public void destroyLight(ENG_LightNative l) {
        destroyLight(l, false);
    }

    public void destroyLight(ENG_LightNative l, boolean skipGLDelete) {
        destroyLight(l.getName(), skipGLDelete);
    }

    public void destroyLight(String name) {
        destroyLight(name, false);
    }

    public void destroyLight(String name, boolean skipGLDelete) {
        destroyMovableObject(name, ENG_LightNativeFactory.FACTORY_TYPE_NAME, skipGLDelete);
    }

    public void destroyAllLights() {
        destroyAllLights(false);
    }

    public void destroyAllLights(boolean skipGLDelete) {
        destroyAllMovableObjectsByType(ENG_LightNativeFactory.FACTORY_TYPE_NAME, skipGLDelete);
    }

    /** @noinspection deprecation*/
    public ArrayList<ENG_Light> _getLightsAffectingFrustum() {
        return mLightsAffectingFrustum;
    }

    public ENG_TiledAnimationNative createTiledAnimation(String name, long id, ENG_BillboardSetNative billboardSetNative, String unlitMaterialName,
                                                         float speed, int horizontalFramesNum, int verticalFramesNum) {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("id", String.valueOf(id));
        params.put("billboardSetNative", billboardSetNative.getName());
        params.put("unlitMaterialName", unlitMaterialName);
        params.put("speed", String.valueOf(speed));
        params.put("horizontalFramesNum", String.valueOf(horizontalFramesNum));
        params.put("verticalFramesNum", String.valueOf(verticalFramesNum));
        return (ENG_TiledAnimationNative) createMovableObject(name, ENG_TiledAnimationFactory.FACTORY_TYPE_NAME, params);
    }

    public ENG_TiledAnimationNative getTiledAnimation(String name) {
        return (ENG_TiledAnimationNative) getMovableObject(name, ENG_TiledAnimationFactory.FACTORY_TYPE_NAME);
    }

    public void destroyTiledAnimation(ENG_TiledAnimationNative tiledAnimationNative) {
        destroyMovableObject(tiledAnimationNative.getName(), ENG_TiledAnimationFactory.FACTORY_TYPE_NAME, false);
    }

    public void destroyAllTiledAnimations() {
        destroyAllMovableObjectsByType(ENG_TiledAnimationFactory.FACTORY_TYPE_NAME, false);
    }

    /**
     * Id is mandatory in the new item system.
     * @param name
     * @param id
     * @param meshName
     * @param groupName
     * @return
     */
    public ENG_Item createItem(String name, long id, String meshName, String groupName) {
        return createItem(name, id, meshName, groupName, ENG_Workflows.SpecularWorkflow);
    }


    public ENG_Item createItem(String name, long id, String meshName, String groupName, ENG_Workflows pbsWorkflow) {
        TreeMap<String, String> params = getCreateEntityParams(meshName, groupName);
        params.put(MOVABLE_OBJECT_PARAM_ID, String.valueOf(id));
        params.put(MOVABLE_OBJECT_PARAM_PBSWORKFLOW, String.valueOf(pbsWorkflow.getWorkflow()));
        ENG_Item item = (ENG_Item) createMovableObject(name, ENG_ItemFactory.FACTORY_TYPE_NAME, params);
        addMovableObject(item, AttachableObjectType.ITEM);
        return item;
    }

    public ENG_Item createItem(String name, long id, String meshName, String groupName,
                               SceneMemoryMgrTypes sceneType, ENG_Workflows pbsWorkflow) {
        return createItem(name, id, meshName, groupName, 1, sceneType, pbsWorkflow);
    }

    public ENG_Item createItem(String name, long id, String meshName, String groupName, int subMeshCount,
                               SceneMemoryMgrTypes sceneType, ENG_Workflows pbsWorkflow) {
        TreeMap<String, String> params = getCreateEntityParams(meshName, groupName);
        params.put(MOVABLE_OBJECT_PARAM_ID, String.valueOf(id));
        params.put(MOVABLE_OBJECT_PARAM_PBSWORKFLOW, String.valueOf(pbsWorkflow.getWorkflow()));
        params.put(MOVABLE_OBJECT_SCENE_MEMORY_MANAGER_TYPE, String.valueOf(sceneType.getType()));
        params.put(MOVABLE_OBJECT_SUBMESH_COUNT, String.valueOf(subMeshCount));
        ENG_Item item = (ENG_Item) createMovableObject(name, ENG_ItemFactory.FACTORY_TYPE_NAME, params);
        addMovableObject(item, AttachableObjectType.ITEM);
        return item;
    }

    public ENG_Item getItem(String name) {
        return (ENG_Item) getMovableObject(name, ENG_ItemFactory.FACTORY_TYPE_NAME);
    }

    public void destroyItem(ENG_Item item) {
        removeMovableObject(item, AttachableObjectType.ITEM);

        destroyMovableObject(item.getName(), ENG_ItemFactory.FACTORY_TYPE_NAME, false);
    }

    public void destroyAllItems() {
        removeAllMovableObjectType(AttachableObjectType.ITEM);
        destroyAllMovableObjectsByType(ENG_ItemFactory.FACTORY_TYPE_NAME, false);
    }

    /**
     * Needed to know how big should the expected buffer size be when coming back from the rendering
     * thread with all the aabbs.
     * @return
     */
    public int getItemListSize() {
        return getMovableObjectListSize();
    }

    /**
     * The issue here is that the position that we receive is no longer available.
     * If you are in ship selection and click ok you will receive an update for the aabb of a ship
     * that no longer exists.
     * We could just check if the pos still exists and be done with it but what if the position
     * does exist but it's no longer the same ship? (the one you were looking for has been removed
     * and a new one created in it's place)
     * @param currentFrame
     * @param pos
     * @param xCenter
     * @param yCenter
     * @param zCenter
     * @param xHalfSize
     * @param yHalfSize
     * @param zHalfSize
     */
    public void updateItemAabb(int currentFrame, int pos, float xCenter, float yCenter, float zCenter,
                               float xHalfSize, float yHalfSize, float zHalfSize) {
        ArrayList<MovableObjectWithType> list = mMovableObjectRingList.getList(currentFrame);
//        System.out.println("currentFrame: " + currentFrame);
        MovableObjectWithType movableObjectWithType = list.get(pos);
        if (!movableObjectWithType.getAttachableObject().isDestroyed()) {
            movableObjectWithType.getAttachableObject().setWorldAabb(xCenter, yCenter, zCenter, xHalfSize, yHalfSize, zHalfSize);
        }
    }

    /** @noinspection deprecation */
    @Deprecated
    public ENG_Entity createEntity(String name, String meshName, String groupName) {
        TreeMap<String, String> params = getCreateEntityParams(meshName, groupName);
        return (ENG_Entity) createMovableObject(name, ENG_EntityFactory.FACTORY_TYPE_NAME,
                params);
    }

    /** @noinspection deprecation */
    @Deprecated
    public ENG_Entity createEntity(String name, long id, String meshName, String groupName) {
        TreeMap<String, String> params = getCreateEntityParams(meshName, groupName);
        params.put("id", String.valueOf(id));
        return (ENG_Entity) createMovableObject(name, ENG_EntityFactory.FACTORY_TYPE_NAME,
                params);
    }

    private TreeMap<String, String> getCreateEntityParams(String meshName, String groupName) {
        TreeMap<String, String> params = new TreeMap<>();
        params.put(MOVABLE_OBJECT_MESH_NAME, meshName);
        params.put(MOVABLE_OBJECT_GROUP_NAME, groupName);
        return params;
    }

    /** @noinspection deprecation */
    public ENG_Entity getEntity(String name) {
        return (ENG_Entity) getMovableObject(name, ENG_EntityFactory.FACTORY_TYPE_NAME);
    }

    /** @noinspection deprecation*/
    public boolean hasEntity(String name) {
        return hasMovableObject(name, ENG_EntityFactory.FACTORY_TYPE_NAME);
    }

    /** @noinspection deprecation*/
    public void destroyEntity(ENG_Entity e) {
        destroyEntity(e, false);
    }

    /** @noinspection deprecation*/
    public void destroyEntity(ENG_Entity e, boolean skipGLDelete) {
        destroyMovableObject(e, skipGLDelete);
//		System.out.println("entity destroyed: " + e.getName());
    }

    public void destroyEntity(String name) {
        destroyEntity(name, false);
    }

    /** @noinspection deprecation*/
    public void destroyEntity(String name, boolean skipGLDelete) {
        destroyMovableObject(name, ENG_EntityFactory.FACTORY_TYPE_NAME, skipGLDelete);
//		System.out.println("entity destroyed: " + name);
    }

    public void destroyAllEntities() {
        destroyAllEntities(false);
    }

    /** @noinspection deprecation*/
    public void destroyAllEntities(boolean skipGLDelete) {
        destroyAllMovableObjectsByType(ENG_EntityFactory.FACTORY_TYPE_NAME, skipGLDelete);
    }

    public void clearScene() {

        throw new UnsupportedOperationException();
        // Commented out everything!!!
//        destroyAllMovableObjects();
//
//        getRootSceneNode().removeAllChildren();
//        getRootSceneNode().detachAllObjects();
//
//        mSceneNodeMap.clear();
//        mSceneNodeList.clear();
//
//        // Remove sky nodes since they've been deleted
//        mSkyBoxNode = mSkyPlaneNode = mSkyDomeNode = null;
//        mSkyBoxEnabled = mSkyPlaneEnabled = mSkyDomeEnabled = false;
//
//        if (mRenderQueue != null) {
//            mRenderQueue.clear(true);
//        }
    }

    public boolean isShadowTechniqueStencilBased() {
        return ((mShadowTechnique.getTechnique() & ShadowTechnique.SHADOWDETAILTYPE_STENCIL.getTechnique()) != 0);
    }

    public boolean isShadowTechniqueTextureBased() {
        return ((mShadowTechnique.getTechnique() & ShadowTechnique.SHADOWDETAILTYPE_TEXTURE.getTechnique()) != 0);
    }

    public boolean isShadowTechniqueModulative() {
        return ((mShadowTechnique.getTechnique() & ShadowTechnique.SHADOWDETAILTYPE_MODULATIVE.getTechnique()) != 0);
    }

    public boolean isShadowTechniqueAdditive() {
        return ((mShadowTechnique.getTechnique() & ShadowTechnique.SHADOWDETAILTYPE_ADDITIVE.getTechnique()) != 0);
    }

    public boolean isShadowTechniqueIntegrated() {
        return ((mShadowTechnique.getTechnique() & ShadowTechnique.SHADOWDETAILTYPE_INTEGRATED.getTechnique()) != 0);
    }

    public boolean isShadowTechniqueInUse() {
        return (mShadowTechnique != ShadowTechnique.SHADOWTYPE_NONE);
    }

//    public void setShadowFarDistance(float distance) {
//        mDefaultShadowFarDist = distance;
//        mDefaultShadowFarDistSquared = distance * distance;
//    }

    public float getShadowFarDistance() {
        return mDefaultShadowFarDist;
    }

    public float getShadowFarDistanceSquared() {
        return mDefaultShadowFarDistSquared;
    }

    public ENG_VisibleObjectsBoundsInfo getVisibleObjectsBoundsInfo(ENG_Camera camera) {
        return null;
    }
	
/*	public long _getLightsDirtyCounter() {
		return mLightsDirtyCounter;
	}*/

    /** @noinspection deprecation*/
    public void _populateLightList(ENG_Vector4D position,
                                   float radius, ArrayList<ENG_Light> lightList,
                                   int lightMask) {

    }

    /** @noinspection deprecation*/
    public void _populateLightList(ENG_SceneNode sn, float radius,
                                   ArrayList<ENG_Light> lightList,
                                   int lightMask) {
        sn._getDerivedPosition(temp);
        _populateLightList(temp, radius, lightList, lightMask);
    }

    public int _getCombinedVisibilityMask() {
        if (mCurrentViewport != null) {
            return (mCurrentViewport.getVisibilityMask() & mVisibilityMask);
        } else {
            return mVisibilityMask;
        }
    }

    public ENG_SceneNode createSceneNodeImpl() {
        return new ENG_SceneNode(this);
    }

    public ENG_SceneNode createSceneNodeImpl(String name) {
        return createSceneNodeImpl(name, null);
    }

    public ENG_SceneNode createSceneNodeImpl(String name, ENG_SceneNode parentNode) {
        return createSceneNodeImpl(name, SceneMemoryMgrTypes.SCENE_DYNAMIC, parentNode);
    }

    public ENG_SceneNode createSceneNodeImpl(String name, SceneMemoryMgrTypes type, ENG_SceneNode parentNode) {
        return new ENG_SceneNode(name, this, type, parentNode);
    }

    private void putInList(ENG_SceneNode node, boolean updateEveryFrame) {
        if (MainActivity.isDebugmode()) {
            if (mSceneNodeMap.containsKey(node.getName())) {
                throw new ENG_DuplicateKeyException("node: " + node.getName() +
                        " is already in the list");
            }
        }
        mSceneNodeMap.put(node.getName(), node);
        if (updateEveryFrame) {
            mSceneNodeList.add(node);
        } else {
            mSceneNodeNoUpdateList.add(node);
            mSceneNodeUpdateOnceList.add(node);
        }
    }

    public ENG_SceneNode createSceneNode(boolean updateEveryFrame) {
        ENG_SceneNode node = createSceneNodeImpl();
        putInList(node, updateEveryFrame);
        return node;
    }

    public ENG_SceneNode createSceneNode(String name, ENG_SceneNode parentNode, boolean updateEveryFrame) {
        return createSceneNode(name, parentNode, SceneMemoryMgrTypes.SCENE_DYNAMIC, updateEveryFrame);
    }

    public ENG_SceneNode createSceneNode(String name, ENG_SceneNode parentNode,
                                         SceneMemoryMgrTypes type, boolean updateEveryFrame) {
        ENG_SceneNode node = createSceneNodeImpl(name, type, parentNode);
        putInList(node, updateEveryFrame);
        return node;
    }

    public ENG_SceneNode createSceneNode(String name, boolean updateEveryFrame) {
        return createSceneNode(name, SceneMemoryMgrTypes.SCENE_DYNAMIC, updateEveryFrame);
    }

    public ENG_SceneNode createSceneNode(String name, SceneMemoryMgrTypes type, boolean updateEveryFrame) {
        return createSceneNode(name, null, type, updateEveryFrame);
    }

    public void destroySceneNode(String name) {
        ENG_SceneNode node = mSceneNodeMap.get(name);
        if (node == null) {
            if (MainActivity.isDebugmode()) {
                throw new NullPointerException("scene node: " + name + " not found");
            }
        } else {
            ENG_Node parentNode = node.getParent();
            if (parentNode != null) {
                parentNode.removeChild(node);
            }
            mSceneNodeMap.remove(name);
            mSceneNodeList.remove(node);
            mSceneNodeNoUpdateList.remove(node);
            mSceneNodeUpdateOnceList.remove(node);
        }

    }

    public void destroySceneNode(ENG_SceneNode node) {
        destroySceneNode(node.getName());
    }

    public void notifyStaticDirty(ENG_SceneNode node) {
        if (!node.isStatic()) {
            throw new IllegalArgumentException("Node: " + node.getName() + " is not static!");
        }
        ENG_NativeCalls.sceneManager_notifyStaticDirty(node);
    }

    /**
     * We no longer have the sceneRoot created on our side. We only 'borrow' the pointer from
     * native.
     * Also, all nodes that want to be rendered must have at least this as a parent.
     * Free nodes are not accepted. We don't currently have a mechanism to destroy a node
     * because destroying it on the native side means that the java side won't know about it
     * and might try to use it!!!
     * @return the rootSceneNode
     */
    public ENG_SceneNode getRootSceneNode() {
        return getRootSceneNode(SceneMemoryMgrTypes.SCENE_DYNAMIC);
    }

    public ENG_SceneNode getRootSceneNode(SceneMemoryMgrTypes type) {
        if (mSceneRoot[type.getType()] == null) {
            long rootSceneNodePtr = getRootSceneNodeNative(type);
            mSceneRoot[type.getType()] = new ENG_SceneNode("Root", this, rootSceneNodePtr);
            mSceneRoot[type.getType()]._notifyRootNode();
        }
        return mSceneRoot[type.getType()];
    }

    public ENG_SceneNode createRootSceneNode() {
        ENG_SceneNode root = createSceneNodeImpl("Root");
        root._notifyRootNode();
        return root;
    }

    /**
     * For temporary creating a different scene for the loading screen. Ugly
     *
     * @param root
     */
//    public void setRootSceneNode(ENG_SceneNode root) {
//        assert (root != null);
//        mSceneRoot = root;
//    }

    public ENG_SceneNode getSceneNode(String name) {
        ENG_SceneNode node = mSceneNodeMap.get(name);
        if (node == null) {
            throw new IllegalArgumentException("SceneNode '" + name + "' not found.");
        }
        return node;
    }

    public boolean hasSceneNode(String name) {
        return mSceneNodeMap.containsKey(name);
    }

    public void _setPass(ENG_Pass pass) {
        _setPass(pass, false, true);
    }

    public ENG_Pass _setPass(ENG_Pass pass, boolean evenIfSuppressed,
                             boolean shadowDerivation) {
        if (isLateMaterialResolving()) {
            ENG_Technique lateTech = pass.mParent.getParent().getBestTechnique();
            if (lateTech.getNumPasses() > pass.getIndex().getValue()) {
                pass = lateTech.getPass(pass.getIndex().getValue());
            }
        }

        if (!mSuppressRenderStateChanges || evenIfSuppressed) {
            mAutoParamDataSource.setCurrentPass(pass);

//            boolean passSurfaceAndLightParams = true;
            boolean passFogParams = true;

            if (pass.hasVertexProgram()) {
                bindGpuProgram(pass.getVertexProgram()._getBindingDelegate());

//                passSurfaceAndLightParams = pass.getVertexProgram().getPassSurfaceAndLightStates();
            } else {
                if (mDestRenderSystem.isGpuProgramBound(GpuProgramType.GPT_VERTEX_PROGRAM)) {
                    mDestRenderSystem.unbindGpuProgram(GpuProgramType.GPT_VERTEX_PROGRAM);
                }
            }

//            if (passSurfaceAndLightParams) {
//                // Set surface reflectance properties, only valid if lighting is enabled
//                if (pass.getLightingEnabled()) {
//                    mDestRenderSystem._setSurfaceParams(
//                            pass.getAmbient(),
//                            pass.getDiffuse(),
//                            pass.getSpecular(),
//                            pass.getSelfIllumination(),
//                            pass.getShininess(),
//                            pass.getVertexColourTracking());
//                }
//
//                // Dynamic lighting enabled?
//                mDestRenderSystem.setLightingEnabled(pass.getLightingEnabled());
//            }

            // Using a fragment program?
            if (pass.hasFragmentProgram()) {
                bindGpuProgram(pass.getFragmentProgram()._getBindingDelegate());

                passFogParams = pass.getFragmentProgram().getPassFogStates();
            } else {
                if (mDestRenderSystem.isGpuProgramBound(GpuProgramType.GPT_FRAGMENT_PROGRAM)) {
                    mDestRenderSystem.unbindGpuProgram(GpuProgramType.GPT_FRAGMENT_PROGRAM);
                }
            }

            if (passFogParams) {
                // New fog params can either be from scene or from material
//                FogMode newFogMode;
//                ENG_ColorValue newFogColour = new ENG_ColorValue();
//                float newFogStart, newFogEnd, newFogDensity;
//
//                if (pass.getFogOverride()) {
//                    // New fog params from material
//                    newFogMode = pass.getFogMode();
//                    newFogColour = pass.getFogColour();
//                    newFogStart = pass.getFogStart();
//                    newFogEnd = pass.getFogEnd();
//                    newFogDensity = pass.getFogDensity();
//                } else {
//                    // New fog params from scene
//                    newFogMode = mFogMode;
//                    newFogColour = mFogColour;
//                    newFogStart = mFogStart;
//                    newFogEnd = mFogEnd;
//                    newFogDensity = mFogDensity;
//                }
//
//                mDestRenderSystem._setFog(
//                        newFogMode, newFogColour, newFogDensity, newFogStart, newFogEnd);
//
//                // Tell params about ORIGINAL fog
//                // Need to be able to override fixed function fog, but still have
//                // original fog parameters available to a shader than chooses to use
//                mAutoParamDataSource.setFog(
//		            /*useless mFogMode,*/ mFogColour, mFogDensity, mFogStart, mFogEnd);
//
//                if (pass.hasSeparateSceneBlending()) {
//                    mDestRenderSystem._setSeparateSceneBlending(pass.getSourceBlendFactor(),
//                            pass.getDestBlendFactor(),
//                            pass.getSourceBlendFactor(), pass.getDestBlendFactor(),
//                            pass.getSceneBlendingOperation(),
//                            pass.getSceneBlendingOperationAlpha());
//                } else {
                    mDestRenderSystem._setSceneBlending(
                            pass.getSourceBlendFactor(), pass.getDestBlendFactor(),
                            pass.getSceneBlendingOperation());
//                }
            }
//
//            // Set point parameters
//            mDestRenderSystem._setPointParameters(
//                    pass.getPointSize(),
//                    pass.isPointAttenuationEnabled(),
//                    pass.getPointAttenuationConstant(),
//                    pass.getPointAttenuationLinear(),
//                    pass.getPointAttenuationQuadratic(),
//                    pass.getPointMinSize(),
//                    pass.getPointMaxSize());
//
//            if (mDestRenderSystem.getCapabilities().hasCapability(
//                    Capabilities.RSC_POINT_SPRITES)) {
//                mDestRenderSystem._setPointSpritesEnabled(pass.getPointSpritesEnabled());
//            }

            // Texture unit settings

            Iterator<ENG_TextureUnitState> texIter = pass.getTextureUnitStateIterator();
            int unit = 0;

            //No shadow while

            while (texIter.hasNext()) {
                ENG_TextureUnitState pTex = texIter.next();
                if (pTex.getContentType() == ContentType.CONTENT_COMPOSITOR) {
                    ENG_CompositorChain activeCompositor = _getActiveCompositorChain();
                    if (activeCompositor == null) {
                        throw new ENG_InvalidFieldStateException("A pass that wishes to reference a compositor texture attempted to render in a pipeline without a compositor");
                    }
                    ENG_CompositorInstance refComp = activeCompositor.getCompositor(pTex.getReferencedCompositorName());
                    if (refComp == null) {
                        throw new IllegalArgumentException("Invalid compositor content_type compositor name");
                    }
                    ENG_Texture refTex = refComp.getTextureInstance(pTex.getReferencedTextureName(), pTex.getReferencedMRTIndex());
                    if (refTex == null) {
                        throw new IllegalArgumentException("Invalid compositor content_type texture name");
                    }
                    pTex._setTexturePtr(refTex);
                }
                mDestRenderSystem._setTextureUnitSettings(unit, pTex);
                ++unit;
            }

            // Disable remaining texture units
            mDestRenderSystem._disableTextureUnitsFrom(pass.getNumTextureUnitStates());

            // Set up non-texture related material settings
            // Depth buffer settings
            mDestRenderSystem._setDepthBufferFunction(pass.getDepthFunction());
            mDestRenderSystem._setDepthBufferCheckEnabled(pass.getDepthCheckEnabled());
            mDestRenderSystem._setDepthBufferWriteEnabled(pass.getDepthWriteEnabled());
            mDestRenderSystem._setDepthBias(pass.getDepthBiasConstant(),
                    pass.getDepthBiasSlopeScale());
            // Alpha-reject settings
            mDestRenderSystem._setAlphaRejectSettings(
                    pass.getAlphaRejectFunction(), pass.getAlphaRejectValue(),
                    pass.isAlphaToCoverageEnabled());
            // Set colour write mode
            // Right now we only use on/off, not per-channel
            boolean colWrite = pass.getColourWriteEnabled();
            mDestRenderSystem._setColourBufferWriteEnabled(colWrite,
                    colWrite, colWrite, colWrite);

            mPassCullingMode = pass.getCullingMode();

            mDestRenderSystem._setCullingMode(mPassCullingMode);

            // Shading
            mDestRenderSystem.setShadingType(pass.getShadingMode());
            // Polygon mode
            mDestRenderSystem._setPolygonMode(pass.getPolygonMode());

            // set pass number
            mAutoParamDataSource.setPassNumber(pass.getIndex().getValue());

            // mark global params as dirty
            mGpuParamsDirty |= GpuParamVariability.GPV_GLOBAL.getVariability();

        }

        return pass;
    }

    protected void prepareRenderQueue() {
        ENG_RenderQueue q = getRenderQueue();

        q.clear(ENG_RenderRoot.getRenderRoot().getRemoveRenderQueueStructuresOnClear());

        ENG_RenderQueueInvocationSequence seq =
                mCurrentViewport._getRenderQueueInvocationSequence();

        if (seq != null) {
            Iterator<ENG_RenderQueueInvocation> invokeIt = seq.iterator();

            while (invokeIt.hasNext()) {
                ENG_RenderQueueInvocation invocation = invokeIt.next();
                ENG_RenderQueueGroup group =
                        q.getQueueGroup(invocation.getRenderQueueGroupID());
                group.resetOrganisationMode();
            }

            invokeIt = seq.iterator();

            while (invokeIt.hasNext()) {
                ENG_RenderQueueInvocation invocation = invokeIt.next();
                ENG_RenderQueueGroup group =
                        q.getQueueGroup(invocation.getRenderQueueGroupID());

                group.addOrganisationMode(invocation.getSolidsOrganisation());

                updateRenderQueueGroupSplitOptions(group, invocation.isSuppressShadows(),
                        invocation.isSuppressRenderStateChanges());
            }

            mLastRenderQueueInvocationCustom = true;
        } else {
            if (mLastRenderQueueInvocationCustom) {
                Iterator<Entry<ENG_Byte, ENG_RenderQueueGroup>> groupIter =
                        q._getQueueGroupIterator();
                while (groupIter.hasNext()) {
                    groupIter.next().getValue().defaultOrganisationMode();
                }
            }

            updateRenderQueueSplitOptions();

            mLastRenderQueueInvocationCustom = false;
        }
    }

    public void updateRenderQueueSplitOptions() {
        if (isShadowTechniqueStencilBased()) {
            getRenderQueue().setShadowCastersCannotBeReceivers(false);
        } else {
            getRenderQueue().setShadowCastersCannotBeReceivers(!mShadowTextureSelfShadow);
        }

        if (isShadowTechniqueAdditive() && !isShadowTechniqueIntegrated() &&
                mCurrentViewport.getShadowsEnabled()) {
            getRenderQueue().setSplitPassesByLightingType(true);
        } else {
            getRenderQueue().setSplitPassesByLightingType(false);
        }

        if (isShadowTechniqueInUse() && mCurrentViewport.getShadowsEnabled() &&
                !isShadowTechniqueIntegrated()) {
            getRenderQueue().setSplitNoShadowPasses(true);
        } else {
            getRenderQueue().setSplitNoShadowPasses(false);
        }
    }

    public void updateRenderQueueGroupSplitOptions(ENG_RenderQueueGroup group,
                                                   boolean suppressShadows, boolean suppressRenderState) {
        if (isShadowTechniqueStencilBased()) {
            group.setShadowCastersCannotBeReceivers(false);
        }

        if (isShadowTechniqueTextureBased()) {
            group.setShadowCastersCannotBeReceivers(!mShadowTextureSelfShadow);
        }

        if (!suppressShadows && isShadowTechniqueAdditive() &&
                !isShadowTechniqueIntegrated() &&
                mCurrentViewport.getShadowsEnabled()) {
            group.setSplitPassesByLightingType(true);
        } else {
            group.setSplitPassesByLightingType(false);
        }

        if (isShadowTechniqueInUse() && mCurrentViewport.getShadowsEnabled() &&
                !suppressShadows) {
            group.setSplitNoShadowPasses(true);
        } else {
            group.setSplitNoShadowPasses(false);
        }
    }
	
/*	public void bindGpuProgram(ENG_GpuProgram prog) {
		// need to dirty the light hash, and paarams that need resetting, since program params will have been invalidated
		// Use 1 to guarantee changing it (using 0 could result in no change if list is empty)
		// Hash == 1 is almost impossible to achieve otherwise
		mLastLightHashGpuProgram = 1;
		mGpuParamsDirty = GpuParamVariability.GPV_ALL.getVariability();
		mDestRenderSystem.bindGpuProgram(prog);
	}*/

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the visibilityMask
     */
    public int getVisibilityMask() {
        return mVisibilityMask;
    }

    /**
     * @param visibilityMask the visibilityMask to set
     */
    public void setVisibilityMask(int visibilityMask) {
        this.mVisibilityMask = visibilityMask;
    }

    /**
     * @return the currentViewport
     */
    public ENG_Viewport getCurrentViewport() {
        return mCurrentViewport;
    }

    /**
     * @param currentViewport the currentViewport to set
     */
    public void setCurrentViewport(ENG_Viewport currentViewport) {
        this.mCurrentViewport = currentViewport;
    }

    /** @noinspection deprecation*/
    public void _renderScene(ENG_Camera camera, ENG_Viewport vp,
                             boolean includeOverlays) {
        

        ENG_RenderRoot.getRenderRoot()._pushCurrentSceneManager(this);
        mActiveQueuedRenderableVisitor.targetSceneMgr = this;
        mAutoParamDataSource.setCurrentSceneManager(this);

        mCurrentViewport = vp;

        ArrayList<ENG_Light> emptyLightList = new ArrayList<>();
        useLights(emptyLightList, (short) 0);

        if (isShadowTechniqueInUse()) {

        }

        mCameraInProgress = camera;

        ENG_ControllerManager.getSingleton().updateAllControllers();

        long thisFrameNumber = ENG_RenderRoot.getRenderRoot().getNextFrameNumber();

        if (thisFrameNumber != mLastFrameNumber) {
            _applySceneAnimations();
            mLastFrameNumber = thisFrameNumber;
        }

        sceneGraphMutex.lock();
        try {
            if (mIlluminationStage != IlluminationRenderStage.IRS_RENDER_TO_TEXTURE &&
                    mFindVisibleObjects) {
                findLightsAffectingFrustum(camera);
            }

            _updateSceneGraph(camera);

            camera._autoTrack();

            if (camera.isReflected()) {
                mDestRenderSystem.setInvertVertexWinding(true);
            } else {
                mDestRenderSystem.setInvertVertexWinding(false);
            }

            mAutoParamDataSource.setCurrentViewport(vp);

            setViewport(vp);

            mAutoParamDataSource.setCurrentCamera(camera, mCameraRelativeRendering);

            mAutoParamDataSource.setShadowDirLightExtrusionDistance(mShadowDirLightExtrudeDist);

            mAutoParamDataSource.setAmbientLightColour(mAmbientLight);

            mDestRenderSystem.setAmbientLight(mAmbientLight.r, mAmbientLight.g, mAmbientLight.b);

            mAutoParamDataSource.setCurrentRenderTarget(vp.getTarget());

            if (mDestRenderSystem.getCapabilities()
                    .hasCapability(Capabilities.RSC_USER_CLIP_PLANES)) {
                mDestRenderSystem.resetClipPlanes();

                if (camera.isWindowSet()) {
                    mDestRenderSystem.setClipPlanes(camera.getWindowPlanes());
                }
            }

            prepareRenderQueue();

            if (mFindVisibleObjects) {
                ENG_VisibleObjectsBoundsInfo camVisObjIt = mCamVisibleObjectsMap.get(camera);

                if (camVisObjIt == null) {
                    throw new NullPointerException(
                            "Should never fail to find a visible object bound for a camera, " +
                                    "did you override SceneManager::createCamera or something?");
                }

                camVisObjIt.reset();

                firePreFindVisibleObjects(vp);
                _findVisibleObjects(camera, camVisObjIt,
                        (mIlluminationStage == IlluminationRenderStage.IRS_RENDER_TO_TEXTURE));
                firePostFindVisibleObjects(vp);

                mAutoParamDataSource.setMainCamBoundsInfo(camVisObjIt);


            }

            // Add overlays, if viewport deems it
            if (vp.getOverlaysEnabled() && mIlluminationStage !=
                    IlluminationRenderStage.IRS_RENDER_TO_TEXTURE) {
                ENG_OverlayManager.getSingleton()._queueOverlaysForRendering(
                        camera, getRenderQueue(), vp);
            }
            // Queue skies, if viewport seems it
            if (vp.getSkiesEnabled() && mFindVisibleObjects &&
                    mIlluminationStage !=
                            IlluminationRenderStage.IRS_RENDER_TO_TEXTURE) {
                _queueSkiesForRendering(camera);
            }
			/*} catch (Exception e) {
			System.out.println(e.getMessage());*/
        } finally {
            sceneGraphMutex.unlock();
        }

        mDestRenderSystem._beginGeometryCount();

        if (mCurrentViewport.getClearEveryFrame()) {
            mDestRenderSystem.clearFrameBuffer(mCurrentViewport.getClearBuffers(),
                    mCurrentViewport.getBackgroundColour());
        }

        mDestRenderSystem._beginFrame();

        mDestRenderSystem._setPolygonMode(camera.getPolygonMode());

        mDestRenderSystem._setProjectionMatrix(mCameraInProgress.getProjectionMatrixRS());

        mCameraInProgress.getViewMatrix(true, mCachedViewMatrix);

        if (mCameraRelativeRendering) {
            mCachedViewMatrix.setTrans(ENG_Math.VEC4_ZERO);
            mCameraInProgress.getDerivedPosition(mCameraRelativePosition);
        }
        mDestRenderSystem._setTextureProjectionRelativeTo(mCameraRelativeRendering,
                camera.getDerivedPosition());

        setViewMatrix(mCachedViewMatrix);

        _renderVisibleObjects();

        mDestRenderSystem._endFrame();

        camera._notifyRenderedFaces(mDestRenderSystem._getFaceCount());

        camera._notifyRenderedBatches(mDestRenderSystem._getBatchCount());

        ENG_RenderRoot.getRenderRoot()._popCurrentSceneManager(this);
    }

    public void _applySceneAnimations() {
        

    }

    private void _renderVisibleObjects() {
        
        ENG_RenderQueueInvocationSequence invocationSequence =
                mCurrentViewport._getRenderQueueInvocationSequence();

        if ((invocationSequence != null) &&
                (mIlluminationStage != IlluminationRenderStage.IRS_RENDER_TO_TEXTURE)) {
            renderVisibleObjectsCustomSequence(invocationSequence);
        } else {
            renderVisibleObjectsDefaultSequence();
        }
    }

    private void renderVisibleObjectsCustomSequence(ENG_RenderQueueInvocationSequence seq) {

    }

    private void renderVisibleObjectsDefaultSequence() {
        firePreRenderQueues();

        Iterator<Entry<ENG_Byte, ENG_RenderQueueGroup>> queueIt =
                getRenderQueue()._getQueueGroupIterator();

        while (queueIt.hasNext()) {
            Entry<ENG_Byte, ENG_RenderQueueGroup> entry = queueIt.next();
            ENG_Byte qId = entry.getKey();

            if (!isRenderQueueToBeProcessed(qId)) {
                continue;
            }

            boolean repeatQueue;
            do {
                if (fireRenderQueueStarted(qId.getValue(),
                        mIlluminationStage == IlluminationRenderStage.IRS_RENDER_TO_TEXTURE ?
                                ENG_RenderQueueInvocation.RENDER_QUEUE_INVOCATION_SHADOWS :
                                "")) {
                    break;
                }

                _renderQueueGroupObjects(entry.getValue(), OrganisationMode.OM_PASS_GROUP);

                repeatQueue = fireRenderQueueEnded(qId.getValue(),
                        mIlluminationStage == IlluminationRenderStage.IRS_RENDER_TO_TEXTURE ?
                                ENG_RenderQueueInvocation.RENDER_QUEUE_INVOCATION_SHADOWS :
                                "");
            } while (repeatQueue);
        }

        firePostRenderQueues();
    }

    private void _renderQueueGroupObjects(ENG_RenderQueueGroup pGroup, OrganisationMode om) {
        
//        boolean doShadows = pGroup.getShadowsEnabled() &&
//                mCurrentViewport.getShadowsEnabled() &&
//                !mSuppressShadows && !mSuppressRenderStateChanges;

//        if (doShadows && mShadowTechnique == ShadowTechnique.SHADOWTYPE_STENCIL_ADDITIVE) {
//
//        } else if (doShadows && mShadowTechnique ==
//                ShadowTechnique.SHADOWTYPE_STENCIL_MODULATIVE) {
//
//        } else if (isShadowTechniqueTextureBased()) {
//
//        } else {
            // No shadows, ordinary pass
            renderBasicQueueGroupObjects(pGroup, om);
//        }
    }

    private void renderBasicQueueGroupObjects(ENG_RenderQueueGroup pGroup,
                                              OrganisationMode om) {
        
        Iterator<Entry<ENG_Short, ENG_RenderPriorityGroup>> groupIt = pGroup.getIterator();

        while (groupIt.hasNext()) {
            ENG_RenderPriorityGroup pPriorityGrp = groupIt.next().getValue();

            pPriorityGrp.sort(mCameraInProgress);

            //Rewrite to not use lightIteration
            // Do solids
            renderObjects(pPriorityGrp.getSolidsBasic(), om, true, false);
            // Do unsorted transparents
//            renderObjects(pPriorityGrp.getTransparentsUnsorted(), om, true, false);
            // Do transparents (always descending)
            renderObjects(pPriorityGrp.getTransparents(),
                    OrganisationMode.OM_SORT_DESCENDING, true, false);
        }
    }

    private void renderObjects(ENG_QueuedRenderableCollection objs,
                               OrganisationMode om, boolean lightScissoringClipping, boolean doLightIteration) {
        
        renderObjects(objs, om, lightScissoringClipping, doLightIteration, null);
    }

    /** @noinspection deprecation*/
    private void renderObjects(ENG_QueuedRenderableCollection objs,
                               OrganisationMode om, boolean lightScissoringClipping,
                               boolean doLightIteration, ArrayList<ENG_Light> manualLightList) {
        
        mActiveQueuedRenderableVisitor.autoLights = doLightIteration;
        mActiveQueuedRenderableVisitor.manualLightList = manualLightList;
        mActiveQueuedRenderableVisitor.transparentShadowCastersMode = false;
        mActiveQueuedRenderableVisitor.scissoring = lightScissoringClipping;
        // Use visitor
        objs.acceptVisitor(mActiveQueuedRenderableVisitor, om);
    }

    public void setSuppressShadows(boolean suppress) {
        mSuppressShadows = suppress;
    }

    public boolean getSuppressShadows() {
        return mSuppressShadows;
    }

    protected void setViewport(ENG_Viewport vp) {
        
        mCurrentViewport = vp;
        mDestRenderSystem._setViewport(vp);
        ENG_MaterialManager.getSingleton().setActiveScheme(vp.getMaterialScheme());
    }

    /** @noinspection deprecation */
    public void findLightsAffectingFrustum(ENG_Camera camera) {
        
        MovableObjectCollection lights = getMovableObjectCollection(
                ENG_LightFactory.FACTORY_TYPE_NAME);

//        lights.mutex.lock();
//        try {
            mTestLightInfos.clear();
            mTestLightInfos.ensureCapacity(lights.map.size());

        for (Entry<String, Object> stringENG_movableObjectEntry : lights.map.entrySet()) {
            ENG_Light l = (ENG_Light) stringENG_movableObjectEntry.getValue();

            if (mCameraRelativeRendering) {
                l._setCameraRelative(mCameraInProgress);
            } else {
                l._setCameraRelative(null);
            }

            if (l.isVisible()) {
                LightInfo lightInfo = new LightInfo();
                lightInfo.light = l;
                lightInfo.type = l.getType();
                lightInfo.lightMask = l.getLightMask();
                if (lightInfo.type == LightTypes.LT_DIRECTIONAL) {
                    lightInfo.position.set(ENG_Math.PT4_ZERO);
                    lightInfo.range = 0.0f;
                    mTestLightInfos.add(lightInfo);
                } else {
                    lightInfo.range = l.getAttenuationRange();
                    l.getDerivedPosition(lightInfo.position);
                    ENG_Sphere sphere = new ENG_Sphere(
                            lightInfo.position, lightInfo.range);
                    if (camera.isVisible(sphere)) {
                        mTestLightInfos.add(lightInfo);
                    }
                }
            }
        }
//        } finally {
//            lights.mutex.unlock();
//        }

        if (!mCachedLightInfos.equals(mTestLightInfos)) {
            mLightsAffectingFrustum.clear();
            mLightsAffectingFrustum.ensureCapacity(mTestLightInfos.size());

            for (int i = 0; i < mTestLightInfos.size(); ++i) {
                mLightsAffectingFrustum.add(mTestLightInfos.get(i).light);

                if (isShadowTechniqueTextureBased()) {
                    mLightsAffectingFrustum.get(i)._calcTempSquareDist(
                            camera.getDerivedPosition());
                }
            }

            if (isShadowTechniqueTextureBased()) {

            }

            ArrayList<LightInfo> li = mCachedLightInfos;

            mCachedLightInfos = mTestLightInfos;
            mTestLightInfos = li;

            _notifyLightsDirty();
        }
    }

    public void _notifyLightsDirty() {
        
        ++mLightsDirtyCounter;
    }

    public long _getLightsDirtyCounter() {
        return mLightsDirtyCounter;
    }

    //protected boolean mShowBoundingBoxes;

    public void showBoundingBoxes(boolean bShow) {
        mShowBoundingBoxes = bShow;
    }

    public boolean getShowBoundingBoxes() {
        return mShowBoundingBoxes;
    }

    public ENG_ColorValue getShadowColour() {
        
        return mShadowColour;
    }

    public void addLodListener(ENG_LodListener listener) {
        mLodListeners.add(listener);
    }

    public void removeListener(ENG_LodListener listener) {
        mLodListeners.remove(listener);
    }

    public void _notifyMovableObjectLodChanged(MovableObjectLodChangedEvent evt) {
        boolean queueEvent = false;
        for (int i = 0; i < mLodListeners.size(); ++i) {
            if (mLodListeners.get(i).prequeueMovableObjectLodChanged(evt)) {
                queueEvent = true;
            }
        }

        if (queueEvent) {
            mMovableObjectLodChangedEvents.add(evt);
        }
    }

    public void _notifyEntityMeshLodChanged(EntityMeshLodChangedEvent evt) {
        boolean queueEvent = false;
        for (int i = 0; i < mLodListeners.size(); ++i) {
            if (mLodListeners.get(i).prequeueEntityMeshLodChanged(evt)) {
                queueEvent = true;
            }
        }

        if (queueEvent) {
            mEntityMeshLodChangedEvents.add(evt);
        }
    }

    public void _notifyEntityMaterialLodChanged(EntityMaterialLodChangedEvent evt) {
        boolean queueEvent = false;
        for (int i = 0; i < mLodListeners.size(); ++i) {
            if (mLodListeners.get(i).prequeueEntityMaterialLodChanged(evt)) {
                queueEvent = true;
            }
        }

        if (queueEvent) {
            mEntityMaterialLodChangedEvents.add(evt);
        }
    }

    public void _handleLodEvents() {
        for (ENG_LodListener lod : mLodListeners) {
            for (MovableObjectLodChangedEvent mMovableObjectLodChangedEvent : mMovableObjectLodChangedEvents) {
                lod.postqueueMovableObjectLodChanged(mMovableObjectLodChangedEvent);
            }

            for (EntityMeshLodChangedEvent mEntityMeshLodChangedEvent : mEntityMeshLodChangedEvents) {
                lod.postqueueEntityMeshLodChanged(mEntityMeshLodChangedEvent);
            }

            for (EntityMaterialLodChangedEvent mEntityMaterialLodChangedEvent : mEntityMaterialLodChangedEvents) {
                lod.postqueueEntityMaterialLodChanged(mEntityMaterialLodChangedEvent);
            }
        }

        // Clear event queues
        mMovableObjectLodChangedEvents.clear();
        mEntityMeshLodChangedEvents.clear();
        mEntityMaterialLodChangedEvents.clear();
    }

    public Object createMovableObject(String name, String typeName) {
        return createMovableObject(name, typeName, null);
    }

    public Object createMovableObject(String name, String typeName, TreeMap<String, String> nameValuePair) {
        if (typeName.equals("Camera")) {
            return createCamera(name);
        }

        ENG_MovableObjectFactory factory = ENG_RenderRoot.getRenderRoot().getMovableObjectFactory(typeName);
        MovableObjectCollection objectMap = getMovableObjectCollection(typeName);

//        objectMap.mutex.lock();
//        try {
        if (objectMap.map.containsKey(name)) {
            throw new IllegalArgumentException("An object of type '" + typeName + "' with name '" + name + "' already exists.");
        }
        int entityNum = 0;
        for (MovableObjectCollection coll : mMovableObjectCollectionMap.values()) {
            entityNum += coll.map.size();
        }

//        System.out.println("Creating entity with name: " + name + ". Entity count: " + entityNum);
        Object instance = null;
        if (factory instanceof ENG_MovableObjectFactoryWithId) {
            instance = factory.createInstanceImpl(name, nameValuePair);
        } else {
            instance = factory.createInstance(name, this, nameValuePair);
        }
        objectMap.map.put(name, instance);
        return instance;
//        } finally {
//            objectMap.mutex.unlock();
//        }
    }

    public void destroyMovableObject(String name, String typeName) {
        destroyMovableObject(name, typeName, false);
    }

    public void destroyMovableObject(String name, String typeName, boolean skipGLDelete) {
        if (typeName.equals("Camera")) {
            destroyCamera(name);
            return;
        }

        ENG_MovableObjectFactory factory = ENG_RenderRoot.getRenderRoot().getMovableObjectFactory(typeName);
        MovableObjectCollection objectMap = getMovableObjectCollection(typeName);

//        objectMap.mutex.lock();
//        try {

            Object it = objectMap.map.get(name);
            if (it != null) {
                factory.destroyInstance(it, skipGLDelete);
                objectMap.map.remove(name);
            }
//        } finally {
//            objectMap.mutex.unlock();
//        }
    }

    public void destroyAllMovableObjectsByType(String typeName) {
        destroyAllMovableObjectsByType(typeName, false);
    }

    public void destroyAllMovableObjectsByType(String typeName, boolean skipGLDelete) {
        if (typeName.equals("Camera")) {
            destroyAllCameras();
            return;
        }

        ENG_MovableObjectFactory factory = ENG_RenderRoot.getRenderRoot().getMovableObjectFactory(typeName);
        MovableObjectCollection objectMap = getMovableObjectCollection(typeName);

//        objectMap.mutex.lock();
//        try {
        for (Entry<String, Object> stringObjectEntry : objectMap.map.entrySet()) {
            Object obj = stringObjectEntry.getValue();
            if (obj instanceof ENG_MovableObject) {
                if (((ENG_MovableObject) obj)._getManager() == this) {
                    factory.destroyInstance(obj, skipGLDelete);
                }
            } else {
                factory.destroyInstance(obj, skipGLDelete);
            }
        }
            objectMap.map.clear();
//        } finally {
//            objectMap.mutex.unlock();
//        }
    }

    public void destroyAllMovableObjects() {
        destroyAllMovableObjects(false);
    }

    public void destroyAllMovableObjects(boolean skipGLDelete) {
//        mMovableObjectCollectionMapMutex.lock();
//        try {
        for (Entry<String, MovableObjectCollection> entry : mMovableObjectCollectionMap.entrySet()) {
            MovableObjectCollection coll = entry.getValue();

//                coll.mutex.lock();
//                try {
            if (ENG_RenderRoot.getRenderRoot().hasMovableObjectFactory(entry.getKey())) {
                ENG_MovableObjectFactory factory = ENG_RenderRoot.getRenderRoot().getMovableObjectFactory(entry.getKey());
                for (Entry<String, Object> stringObjectEntry : coll.map.entrySet()) {
                    Object obj = stringObjectEntry.getValue();
                    if (obj instanceof ENG_MovableObject) {
                        if (((ENG_MovableObject) obj)._getManager() == this) {
                            factory.destroyInstance(obj, skipGLDelete);
                        }
                    } else {
                        factory.destroyInstance(obj, skipGLDelete);
                    }
                }
            }
            coll.map.clear();
//                } finally {
//                    coll.mutex.unlock();
//                }
        }
//        } finally {
//            mMovableObjectCollectionMapMutex.unlock();
//        }
    }

    public Object getMovableObject(String name, String typeName) {
        if (typeName.equals("Camera")) {
            return getCamera(name);
        }

        MovableObjectCollection objectMap = getMovableObjectCollection(typeName);

//        objectMap.mutex.lock();
//        try {
        Object obj = objectMap.map.get(name);
//        ENG_MovableObject obj = (ENG_MovableObject) o;
            if (obj == null) {
                throw new IllegalArgumentException(
                        "Object named '" + name + "' does not exist.");
            }
            return obj;
//        } finally {
//            objectMap.mutex.unlock();
//        }
    }

    public boolean hasMovableObject(String name, String typeName) {
        if (typeName.equals("Camera")) {
            return hasCamera(name);
        }

        MovableObjectCollection objectMap = getMovableObjectCollection(typeName);

//        objectMap.mutex.lock();
//        try {
            return objectMap.map.containsKey(name);
//        } finally {
//            objectMap.mutex.unlock();
//        }
    }

    public Iterator<Entry<String, Object>> getMovableObjectIterator(String typeName) {
        return getMovableObjectCollection(typeName).map.entrySet().iterator();
    }

    public void destroyMovableObject(ENG_MovableObject obj, boolean skipGLDelete) {
        destroyMovableObject(obj.getName(), obj.getMovableType(), skipGLDelete);
    }

    public void injectMovableObject(ENG_MovableObject m) {
        MovableObjectCollection objectMap = getMovableObjectCollection(m.getMovableType());
//        objectMap.mutex.lock();
//        try {
            objectMap.map.put(m.getName(), m);
//        } finally {
//            objectMap.mutex.unlock();
//        }
    }

    public void extractMovableObject(String name, String typeName) {
        MovableObjectCollection objectMap = getMovableObjectCollection(typeName);
//        objectMap.mutex.lock();
//        try {
            objectMap.map.remove(name);
//        } finally {
//            objectMap.mutex.unlock();
//        }
    }

    public void extractMovableObject(ENG_MovableObject m) {
        extractMovableObject(m.getName(), m.getMovableType());
    }

    public void extractAllMovableObjectsByType(String typeName) {
        MovableObjectCollection objectMap = getMovableObjectCollection(typeName);
//        objectMap.mutex.lock();
//        try {
            objectMap.map.clear();
//        } finally {
//            objectMap.mutex.unlock();
//        }
    }

    public ENG_RenderSystem getDestinationRenderSystem() {
        return mDestRenderSystem;
    }

    public boolean isLateMaterialResolving() {
        return mLateMaterialResolving;
    }

    protected void _updateSceneGraph(ENG_Camera cam) {
        ENG_Node.processQueuedUpdate();

        getRootSceneNode()._update(true, false);
    }

    protected void _findVisibleObjects(ENG_Camera cam,
                                       ENG_VisibleObjectsBoundsInfo visibleBounds,
                                       boolean onlyShadowCasters) {
        getRootSceneNode()._findVisibleObjects(cam, getRenderQueue(),
                visibleBounds, true, mDisplayNodes, onlyShadowCasters);
    }

    public void addListener(Listener l) {
        mListener.add(l);
    }

    public void removeListener(Listener l) {
        mListener.remove(l);
    }

    public void addRenderQueueListener(ENG_RenderQueueListener l) {
        mRenderQueueListeners.add(l);
    }

    public void removeRenderQueueListener(ENG_RenderQueueListener l) {
        mRenderQueueListeners.remove(l);
    }

    public void addRenderQueueListener(long l) {
        wrapper.addRenderQueueListener(l);
    }

    public void removeRenderQueueListener(long l) {
        wrapper.removeRenderQueueListener(l);
    }

    public void addRenderObjectListener(ENG_RenderObjectListener l) {
        mRenderObjectListeners.add(l);
    }

    public void removeRenderObjectListener(ENG_RenderObjectListener l) {
        mRenderObjectListeners.remove(l);
    }

    protected final ReentrantLock sceneGraphMutex = new ReentrantLock();

    protected IlluminationRenderStage mIlluminationStage;

    /// Whether to use camera-relative rendering
    protected boolean mCameraRelativeRendering;
    protected final ENG_Matrix4 mCachedViewMatrix = new ENG_Matrix4();
    protected final ENG_Vector4D mCameraRelativePosition = new ENG_Vector4D();
    private ENG_SceneNode mSkyBoxNode;
    private boolean mSkyBoxEnabled;
    /** @noinspection deprecation*/
    private ENG_Entity mSkyBoxObj;

    public void _injectRenderWithPass(ENG_Pass pass, ENG_Renderable rend) {
        _injectRenderWithPass(pass, rend, true, false, null);
    }

    /** @noinspection deprecation*/
    public void _injectRenderWithPass(ENG_Pass pass, ENG_Renderable rend,
                                      boolean shadowDerivation, boolean doLightIteration,
                                      ArrayList<ENG_Light> manualLightList) {
        
        // render something as if it came from the current queue
        ENG_Pass usedPass = _setPass(pass, false, shadowDerivation);
        renderSingleObject(rend, usedPass, false, doLightIteration, manualLightList);
    }

    public void setLateMaterialResolving(boolean b) {
        
        mLateMaterialResolving = b;
    }

    public boolean getFindVisibleObjects() {
        
        return mFindVisibleObjects;
    }

    public void setFindVisibleObjects(boolean findVisibleObjects) {
        
        mFindVisibleObjects = findVisibleObjects;
    }

    public void _setActiveCompositorChain(
            ENG_CompositorChain eng_CompositorChain) {
        
        mActiveCompositorChain = eng_CompositorChain;
    }

    public ENG_CompositorChain _getActiveCompositorChain() {
        return mActiveCompositorChain;
    }

    public void destroyParticleSystem(ENG_ParticleSystem system) {
        destroyParticleSystem(system, false);
    }

    public void destroyParticleSystem(ENG_ParticleSystem system, boolean skipGLDelete) {
        //	system.destroy();
        destroyMovableObject(system, skipGLDelete);
    }

    public void destroyParticleSystem(String name) {
        //	ENG_MovableObject object =
        //			getMovableObject(name, ENG_ParticleSystemFactory.FACTORY_TYPE_NAME);
        //	((ENG_ParticleSystem)object).destroy();
        destroyMovableObject(name, ENG_ParticleSystemFactory.FACTORY_TYPE_NAME);
    }

    public ENG_ParticleSystem createParticleSystem(String name, String templateName) {
        
        TreeMap<String, String> map = new TreeMap<>();
        map.put("templateName", templateName);
        return (ENG_ParticleSystem) createMovableObject(
                name, ENG_ParticleSystemFactory.FACTORY_TYPE_NAME, map);
    }

    public ENG_ParticleSystem createParticleSystem(String name, int quota,
                                                   String templateName) {
        
        TreeMap<String, String> map = new TreeMap<>();
        map.put("templateName", templateName);
        map.put("quota", String.valueOf(quota));
        return (ENG_ParticleSystem) createMovableObject(
                name, ENG_ParticleSystemFactory.FACTORY_TYPE_NAME, map);
    }

    /** @noinspection deprecation*/
    public ENG_BillboardSet createBillboardSet(String name) {
        return createBillboardSet(name, 20);
    }

    /** @noinspection deprecation */
    public ENG_BillboardSet createBillboardSet(String name, int pool) {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("poolSize", String.valueOf(pool));
        return (ENG_BillboardSet) createMovableObject(
                name, ENG_BillboardSetFactory.FACTORY_TYPE_NAME, params);
    }

    /** @noinspection deprecation */
    public ENG_BillboardSet getBillboardSet(String name) {
        return (ENG_BillboardSet) getMovableObject(name, ENG_BillboardSetFactory.FACTORY_TYPE_NAME);
    }

    /** @noinspection deprecation*/
    public void destroyBillboardSet(ENG_BillboardSet set) {
        destroyBillboardSet(set, false);
    }

    /** @noinspection deprecation*/
    public void destroyBillboardSet(ENG_BillboardSet set, boolean skipGLDelete) {
        destroyMovableObject(set, skipGLDelete);
    }

    public void destroyBillboardSet(String name) {
        destroyBillboardSet(name, false);
    }

    /** @noinspection deprecation*/
    public void destroyBillboardSet(String name, boolean skipGLDelete) {
        destroyMovableObject(name, ENG_BillboardSetFactory.FACTORY_TYPE_NAME, skipGLDelete);
    }

//    public void destroyParticleSystem(ENG_ParticleSystem system) {
//        destroyParticleSystem(system, false);
//    }

    public void destroyParticleSystemNative(ENG_ParticleSystemNative system) {
        //	system.destroy();
        destroyMovableObject(system.getName(), ENG_ParticleSystemNativeFactory.FACTORY_TYPE_NAME, false);
    }

    public void destroyParticleSystemNative(String name) {
        //	ENG_MovableObject object =
        //			getMovableObject(name, ENG_ParticleSystemFactory.FACTORY_TYPE_NAME);
        //	((ENG_ParticleSystem)object).destroy();
        destroyMovableObject(name, ENG_ParticleSystemNativeFactory.FACTORY_TYPE_NAME, false);
    }

    public ENG_ParticleSystemNative createParticleSystemNative(String name, String templateName) {
        
        TreeMap<String, String> map = new TreeMap<>();
        map.put("templateName", templateName);
        return (ENG_ParticleSystemNative) createMovableObject(
                name, ENG_ParticleSystemNativeFactory.FACTORY_TYPE_NAME, map);
    }

    public ENG_ParticleSystemNative createParticleSystemNative(String name, int quota,
                                                   String templateName) {
        
        TreeMap<String, String> map = new TreeMap<>();
        map.put("templateName", templateName);
        map.put("quota", String.valueOf(quota));
        return (ENG_ParticleSystemNative) createMovableObject(
                name, ENG_ParticleSystemNativeFactory.FACTORY_TYPE_NAME, map);
    }

    public ENG_BillboardSetNative createBillboardSetNative(String name) {
        return createBillboardSetNative(name, 20);
    }

    public ENG_BillboardSetNative createBillboardSetNative(String name, int pool) {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("poolSize", String.valueOf(pool));
        ENG_BillboardSetNative billboardSetNative = (ENG_BillboardSetNative) createMovableObject(
                name, ENG_BillboardSetNativeFactory.FACTORY_TYPE_NAME, params);
        addMovableObject(billboardSetNative, AttachableObjectType.BILLBOARD_SET);
        return billboardSetNative;
    }

    public ENG_BillboardSetNative getBillboardSetNative(String name) {
        return (ENG_BillboardSetNative) getMovableObject(name, ENG_BillboardSetNativeFactory.FACTORY_TYPE_NAME);
    }

    public void destroyBillboardSetNative(ENG_BillboardSetNative set) {
        removeMovableObject(set, AttachableObjectType.BILLBOARD_SET);

        destroyMovableObject(set.getName(), ENG_BillboardSetNativeFactory.FACTORY_TYPE_NAME, false);
    }

    public void destroyAllBillboardSetsNative() {
        removeAllMovableObjectType(AttachableObjectType.BILLBOARD_SET);
        destroyAllMovableObjectsByType(ENG_BillboardSetNativeFactory.FACTORY_TYPE_NAME, false);
    }

//    public void destroyBillboardSetNative(ENG_BillboardSetNative set) {
//
//    }

//    public void destroyBillboardSet(String name) {
//        destroyBillboardSet(name, false);
//    }
//
//    public void destroyBillboardSet(String name, boolean skipGLDelete) {
//        destroyMovableObject(name, ENG_BillboardSetFactory.FACTORY_TYPE_NAME, skipGLDelete);
//    }

    public ENG_RaySceneQuery createRayQuery(ENG_Ray ray) {
        return createRayQuery(ray, 0xFFFFFFFF);
    }

    public ENG_RaySceneQuery createRayQuery(ENG_Ray ray, long mask) {
        ENG_RaySceneQuery q = new ENG_RaySceneQuery(this);
        q.setRay(ray);
        q.setQueryMask((int) (mask));
        return q;
    }

    public void destroyRayQuery(ENG_RaySceneQuery raySceneQuery) {

    }

    public void setSkyBoxNative(boolean enable, String materialName) {
        setSkyBoxNative(enable, materialName, 5000, true, new ENG_Quaternion(ENG_Math.QUAT_IDENTITY), DEFAULT_RESOURCE_GROUP_NAME);
    }

    public void setSkyBoxNative(boolean enable, String materialName, float distance,
                                boolean drawFirst, ENG_Quaternion orientation,
                                String groupName) {
        ENG_NativeCalls.sceneManager_setSkybox(enable, materialName, distance, drawFirst, orientation, groupName);
        mSkyBoxEnabled = enable;
    }

    public void setSkyBoxEnabled(boolean enabled) {
        ENG_NativeCalls.sceneManager_setSkyboxEnabled(enabled);
        mSkyBoxEnabled = enabled;
    }

    public boolean isSkyBoxEnabled() {
        return mSkyBoxEnabled;
    }

    /** @noinspection deprecation*/
    public void setSkyBox(boolean enable, ENG_Entity skybox, ENG_Vector4D scale) {
        setSkyBox(enable, skybox, true, ENG_Math.QUAT_IDENTITY, scale);
    }

    /** @noinspection deprecation*/
    public void setSkyBox(boolean enable, ENG_Entity skybox, boolean drawFirst,
                          ENG_Quaternion orientation, ENG_Vector4D scale) {
        _setSkyBox(enable, skybox, drawFirst ?
                        RenderQueueGroupID.RENDER_QUEUE_SKIES_EARLY.getID() :
                        RenderQueueGroupID.RENDER_QUEUE_SKIES_LATE.getID(), orientation,
                scale);
    }

    /** @noinspection deprecation*/
    public void _setSkyBox(boolean enable, ENG_Entity skybox, byte renderQueue,
                           ENG_Quaternion orientation, ENG_Vector4D scale) {
        if (enable) {
            mSkyBoxObj = skybox;
            if (mSkyBoxNode == null) {
                mSkyBoxNode = createSceneNode("SkyBoxNode", true);
                //	mSkyBoxNode.setInitialState();
            }
            if (!skybox.isAttached()) {
                mSkyBoxNode.attachObject(skybox);
            }
            mSkyBoxNode.setOrientation(orientation);
            mSkyBoxNode.setScale(scale);
            mSkyBoxNode.needUpdate();
            //	mSkyBoxNode._update(true, true);
            skybox.setRenderQueueGroup(renderQueue);
            ENG_Material material = ENG_MaterialManager.getSingleton().getByName(
                    skybox.getMesh().getSubMesh((short) 0).getMaterialName());
            material.setDepthWriteEnabled(false);
            material.getTechnique((short) 0).getPass((short) 0)
                    .getTextureUnitState(0).setTextureAddressingMode(
                    TextureAddressingMode.TAM_CLAMP);
        } else {
            if (mSkyBoxNode != null) {
                mSkyBoxNode.detachAllObjects();
            }
        }
        mSkyBoxEnabled = enable;
    }

    public void _queueSkiesForRendering(ENG_Camera cam) {
        if (mSkyBoxNode != null) {
            //	System.out.println("cam.getDerivedPosition(): " + cam.getDerivedPosition());
            //	System.out.println("cam.getDerivedOrientation() " + cam.getDerivedOrientation());
            mSkyBoxNode.setPosition(cam.getDerivedPosition());
        }
        if (mSkyBoxEnabled && mSkyBoxObj != null && mSkyBoxObj.isVisible()) {
            mSkyBoxObj._updateRenderQueue(getRenderQueue());
        }
    }

    public static class DefaultRaySceneQuery extends ENG_RaySceneQuery {

        public DefaultRaySceneQuery(ENG_SceneManager mgr) {
            super(mgr);
            
//            mSupportedWorldFragments.add(WorldFragmentType.WFT_NONE);
        }

//        private final ENG_Boolean b = new ENG_Boolean();
//        private final ENG_Float f = new ENG_Float();
//
//        @Override
//        public void execute(ENG_RaySceneQueryListener listener) {
//            
//            Iterator<Entry<String, ENG_MovableObjectFactory>> factIt =
//                    ENG_RenderRoot.getRenderRoot().getMovableObjectFactoryIterator();
//            while (factIt.hasNext()) {
//                Iterator<Entry<String, Object>> iterator =
//                        mParentSceneMgr.getMovableObjectIterator(
//                                factIt.next().getValue().getType());
//                while (iterator.hasNext()) {
//                    Entry<String, Object> entry = iterator.next();
//                    ENG_MovableObject a = (ENG_MovableObject) entry.getValue();
//                    // skip whole group if type doesn't match
//                    if ((a.getTypeFlags() & mQueryTypeMask) == 0)
//                        break;
//
//                    if (((a.getQueryFlags() & mQueryMask) != 0) &&
//                            a.isInScene()) {
//                        // Do ray / box test
//
//                        mRay.intersects(a.getWorldBoundingBox(), b, f);
//
//                        if (b.getValue()) {
//                            if (!listener.queryResult(a, f.getValue())) return;
//                        }
//                    }
//                }
//            }
//        }

    }

    public ENG_SceneNode getSkyboxNode() {
        
        return mSkyBoxNode;
    }

    public void prepareForNativeRendering() {
        prepareSceneNodesForNativeRendering();
//        prepareItemRingListForNativeRendering();
    }

    public void prepareSceneNodesForNativeRendering() {
        if (!mSceneNodeUpdateOnceList.isEmpty()) {
            updateNativeSceneNodes(mSceneNodeUpdateOnceList);
            for (int i = 0, mSceneNodeUpdateOnceListSize = mSceneNodeUpdateOnceList.size(); i < mSceneNodeUpdateOnceListSize; i++) {
                ENG_SceneNode sceneNode = mSceneNodeUpdateOnceList.get(i);
                if (sceneNode.isStatic()) {
                    notifyStaticDirty(sceneNode);
                }
            }

            mSceneNodeUpdateOnceList.clear();
        }
        updateNativeSceneNodes(mSceneNodeList);
    }

    private void updateNativeSceneNodes(ArrayList<ENG_SceneNode> sceneNodeList) {
        for (ENG_SceneNode sceneNode : sceneNodeList) {
            // No longer needed when using bullet physics. Weeeeee!!!
            if (MainActivity.isDebugmode()) {
                if (sceneNode.getPositionForNative().isNaN()) {
                    throw new IllegalStateException("position is NAN");
                }
                if (sceneNode.getOrientationForNative().isNaN()) {
                    throw new IllegalStateException("orientation is NAN");
                }
                if (sceneNode.getScaleForNative().isNaN()) {
                    throw new IllegalStateException("scale is NAN");
                }
            }
            ENG_NativeCalls.sceneNode_setPosition(sceneNode, sceneNode.getPositionForNative());
//            if (sceneNode.isIgnoreOrientation()) {
//                sceneNode.setIgnoreOrientation(false);
//                System.out.println("Ignoring orientation for " + sceneNode.getName());
//            } else {
                // Camera is the player ship in first person.
//                if (sceneNode.getName().startsWith("camera")) {
//                    ENG_Vector3D eng_vector3D = new ENG_Vector3D();
//                    float angle = sceneNode.getOrientationForNative().toAngleAxisDeg(eng_vector3D);
//                    System.out.println("setting orientation: " + sceneNode.getName() + " orientation: " + eng_vector3D + " angle: " + angle + " currentTime: " + ENG_Utility.nanoTime());
//                }
                ENG_NativeCalls.sceneNode_setOrientation(sceneNode, sceneNode.getOrientationForNative());
//            }
//            ENG_NativeCalls.sceneNode_setDerivedPosition(sceneNode, sceneNode.getDerivedPositionNative());
//            ENG_NativeCalls.sceneNode_setDerivedOrientation(sceneNode, sceneNode.getDerivedOrientationNative());
            ENG_NativeCalls.sceneNode_setScaling(sceneNode, sceneNode.getScaleForNative());
        }
    }

    /**
     *
     */
    public void prepareItemRingListForNativeRendering() {
        int currentFrame = getMovableObjectRingListCurrentFrame();
        if (getMovableObjectListSize() == 0) {
            return;
        }
        ArrayList<MovableObjectWithType> itemListCopy = mMovableObjectRingList.getNextList();
        itemListCopy.ensureCapacity(getMovableObjectListSize());
        itemListCopy.addAll(mMovableObjectList);
//        System.out.println("Written to buffer num: " + mMovableObjectRingList.getPreviousBuf() + " getMovableObjectListSize: " + getMovableObjectListSize());

    }

    public int getMovableObjectRingListCurrentFrame() {
        return mMovableObjectRingListCurrentFrame;
    }

    public void setMovableObjectRingListCurrentFrame(int itemRingListCurrentFrame) {
        this.mMovableObjectRingListCurrentFrame = itemRingListCurrentFrame;
    }

    public void setShadowDirectionalLightExtrusionDistance(final float distance) {
        ENG_SlowCallExecutor.execute(() -> {
            setShadowDirectionalLightExtrusionDistance(wrapper.getPtr(), distance);
            return 0;
        });

    }

    public void setShadowFarDistance(final float distance) {
        ENG_SlowCallExecutor.execute(() -> {
            setShadowFarDistance(wrapper.getPtr(), distance);
            return 0;
        });

    }

    private long getRootSceneNodeNative(final SceneMemoryMgrTypes type) {
        final long[] ret = new long[1];
        ENG_SlowCallExecutor.execute(() -> {
            ret[0] = getRootSceneNode(wrapper.getPtr(), type.getType());
            return 0;
        });
        return ret[0];
    }

    private void setV1FastRenderQueue(final byte v1FastRenderQueue) {
        ENG_SlowCallExecutor.execute(() -> {
            setV1FastRenderQueue(wrapper.getPtr(), v1FastRenderQueue);
            return 0;
        });
    }

    private static native void setShadowDirectionalLightExtrusionDistance(long ptr, float distance);
    private static native void setShadowFarDistance(long ptr, float distance);
    private static native long getRootSceneNode(long ptr, byte sceneNodeType);
    private static native void setV1FastRenderQueue(long ptr, byte v1FastRenderQueue);

}
