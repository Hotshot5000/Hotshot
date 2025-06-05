/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_IDisposable;
import headwayent.hotshotengine.ENG_Log;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.android.AndroidRenderWindow;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.basictypes.ENG_Long;
import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;
import headwayent.hotshotengine.gorillagui.ENG_SilverBack;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointer;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_RootNativeWrapper;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_RenderingThread;
import headwayent.hotshotengine.renderer.nullrendersystem.NullGpuProgramManager;
import headwayent.hotshotengine.renderer.nullrendersystem.NullProgramFactory;
import headwayent.hotshotengine.renderer.nullrendersystem.NullTextureManager;
import headwayent.hotshotengine.renderer.opengles.GLDefaultHardwareBufferManagerBase;
import headwayent.hotshotengine.renderer.opengles.GLRenderSystem;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import com.badlogic.gdx.Gdx;

public class ENG_RenderRoot implements ENG_IDisposable, ENG_NativePointer {

    private static ENG_RenderRoot renderRoot;// = new ENG_RenderRoot();
    private static final ReentrantLock renderRootLock = new ReentrantLock();
    //    private long ptr;
    private final ENG_RootNativeWrapper wrapper;
    protected final Stack<ENG_SceneManager> mSceneManagerStack = new Stack<>();
    protected final ArrayList<ENG_RenderSystem> mRenderers = new ArrayList<>();
    private ENG_HardwareBufferManager hardwareBufferManager;
    protected ENG_RenderSystem mActiveRenderSystem;
    protected String mVersion = ENG_OgrePrerequisites.OGRE_VERSION_MAJOR +
            "." + ENG_OgrePrerequisites.OGRE_VERSION_MINOR + "." +
            ENG_OgrePrerequisites.OGRE_VERSION_PATCH +
            ENG_OgrePrerequisites.OGRE_VERSION_SUFFIX + " " +
            "(" + ENG_OgrePrerequisites.OGRE_VERSION_NAME + ")";
    protected String mConfigFileName;
    protected boolean mQueuedEnd;
    // In case multiple render windows are created, only once are the resources loaded.
    protected boolean mFirstTimePostWindowInit;

    //protected long mNextFrame;

    protected ENG_CompositorManager mCompositorManager;

    protected ENG_ControllerManager mControllerManager;
    //protected Stack<ENG_SceneManager> mSceneManagerStack = new Stack<ENG_SceneManager>();
    protected ENG_MaterialManager mMaterialManager;// = new ENG_MaterialManager();
    /** @noinspection deprecation*/
    protected ENG_MeshManager mMeshManager;// = new ENG_MeshManager();

    protected ENG_LodStrategyManager mLodManager;// = new ENG_LodStrategyManager();

    //protected ENG_GpuProgramManager mGpuProgramManager;
    protected ENG_HighLevelGpuProgramManager mHighLevelGpuProgramManager;// =
    //new ENG_HighLevelGpuProgramManager();

    protected long mNextFrame;
    protected float mFrameSmoothingTime;
    protected boolean mRemoveQueueStructuresOnClear;

    protected final TreeMap<String, ENG_MovableObjectFactory> mMovableObjectFactoryMap = new TreeMap<>();
    protected int mNextMovableObjectTypeFlag = 1;

    protected ENG_MovableObjectFactory mEntityFactory;// = new ENG_EntityFactory();
    protected ENG_MovableObjectFactory mLightFactory;// = new ENG_LightFactory();
    protected ENG_MovableObjectFactory mBillboardSetFactory;

    protected ENG_MovableObjectFactory mItemFactory;// = new ENG_EntityFactory();
    protected ENG_MovableObjectFactory mParticleSystemNativeFactory;// = new ENG_LightFactory();
    protected ENG_MovableObjectFactory mBillboardSetNativeFactory;
    protected ENG_MovableObjectFactory mLightNativeFactory;

    //Just one scene manager currently supported
    protected ENG_SceneManager mSceneManager;// = new ENG_SceneManager("Main");

    protected ENG_ParticleSystemManager mParticleManager;

    protected ENG_OverlayManager mOverlayManager;
    protected ENG_PanelOverlayElementFactory mPanelFactory;
    protected ENG_TextAreaOverlayElementFactory mTextAreaFactory;

    protected ENG_FontManager mFontManager;
    protected ENG_SkeletonManager mSkeletonManager;

    protected ENG_SilverBack mGorilla;

    protected final TreeMap<String, ENG_RenderQueueInvocationSequence> mRQSequenceMap = new TreeMap<>();

    /// Are we initialised yet?
    protected boolean mIsInitialised;

    protected final HashSet<ENG_FrameListener> mFrameListeners = new HashSet<>();
    protected final HashSet<ENG_FrameListener> mRemovedFrameListeners = new HashSet<>();

    protected final ArrayDeque<ENG_Long>[] mEventTimes = new ArrayDeque[FrameEventTimeType.FETT_COUNT.ordinal()];
    private ENG_RenderWindow currentRenderWindow;
    private float screenDensity;
    private static CountDownLatch continuousRenderingLatch;
    private static final ReentrantLock continuousRenderingLock = new ReentrantLock();
    private static boolean continuousRendering;// = true;

    /**
     * Indicates the type of event to be considered by calculateEventTime().
     */
    enum FrameEventTimeType {
        FETT_ANY,
        FETT_STARTED,
        FETT_QUEUED,
        FETT_ENDED,
        FETT_COUNT
    }

    //protected boolean mRemoveQueueStructuresOnClear;

    public ENG_RenderRoot(MainApp.Mode mode) {
        this("plugins_d.cfg", "ogre.cfg", "Ogre.log", mode);
    }

    /** @noinspection deprecation */
    public ENG_RenderRoot(String pluginFileName,
                          String configFileName,
                          String logFileName, MainApp.Mode mode) {
//        if (renderRoot == null) {
//            renderRoot = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
        wrapper = new ENG_RootNativeWrapper(pluginFileName, configFileName, logFileName);
        setRenderRoot(this);
        if (mode == MainApp.Mode.CLIENT) {

//            ptr = ENG_NativeCalls.callNewRoot(pluginFileName, configFileName, logFileName);
            for (int i = 0; i < FrameEventTimeType.FETT_COUNT.ordinal(); ++i) {
                mEventTimes[i] = new ArrayDeque<>();
            }
            mActiveRenderSystem = new GLRenderSystem(wrapper.getRenderSystem());
//            hardwareBufferManager = ((GLRenderSystem) mActiveRenderSystem).getHardwareBufferManager();
            mControllerManager = new ENG_ControllerManager();
            //Lod manager must be init before the material manager because
            //of the initialise() method that requires it
            mLodManager = new ENG_LodStrategyManager();
            mMaterialManager = new ENG_MaterialManager();
            mMeshManager = new ENG_MeshManager();

            mParticleManager = new ENG_ParticleSystemManager();
            // Hack to get it running without further ado
            mParticleManager._initialise();
//            ParticleFXPlugin.install();

            mOverlayManager = new ENG_OverlayManager();
            mPanelFactory = new ENG_PanelOverlayElementFactory();
            mTextAreaFactory = new ENG_TextAreaOverlayElementFactory();
            mOverlayManager.addOverlayElementFactory(mPanelFactory);
            mOverlayManager.addOverlayElementFactory(mTextAreaFactory);
            mFontManager = new ENG_FontManager();

            mHighLevelGpuProgramManager = new ENG_HighLevelGpuProgramManager();

            mEntityFactory = new ENG_EntityFactory();
            mLightFactory = new ENG_LightFactory();
            mBillboardSetFactory = new ENG_BillboardSetFactory();

            mItemFactory = new ENG_ItemFactory();
            mParticleSystemNativeFactory = new ENG_ParticleSystemNativeFactory();
            mBillboardSetNativeFactory = new ENG_BillboardSetNativeFactory();
            mLightNativeFactory = new ENG_LightNativeFactory();
            ENG_TiledAnimationFactory mTiledAnimationFactory = new ENG_TiledAnimationFactory();

            addMovableObjectFactory(mEntityFactory);
            addMovableObjectFactory(mLightFactory);
            addMovableObjectFactory(mBillboardSetFactory);

            addMovableObjectFactory(mItemFactory);
            addMovableObjectFactory(mParticleSystemNativeFactory);
            addMovableObjectFactory(mBillboardSetNativeFactory);
            addMovableObjectFactory(mLightNativeFactory);
            addMovableObjectFactory(mTiledAnimationFactory);

            mCompositorManager = new ENG_CompositorManager();
            mSkeletonManager = new ENG_SkeletonManager();
            mGorilla = new ENG_SilverBack();
            screenDensity = Gdx.graphics.getDensity();
            ENG_Log.getInstance().log("screenDensity: " + screenDensity);
            if (screenDensity < 1.0f) {
                screenDensity = 1.0f;
                ENG_Log.getInstance().log("screenDensity < 1.0f adjusted to: " + screenDensity);
            }

            // Init the native wrapper.
            ENG_SceneCompositor sceneCompositor = ENG_SceneCompositor.getSingleton();

        } else if (mode == MainApp.Mode.SERVER) {

            mActiveRenderSystem = new GLRenderSystem(wrapper.getRenderSystem());
            mControllerManager = new ENG_ControllerManager();

            mLodManager = new ENG_LodStrategyManager();
            mMaterialManager = new ENG_MaterialManager();
            mMeshManager = new ENG_MeshManager();

            mParticleManager = new ENG_ParticleSystemManager();
            // Hack to get it running without further ado
            mParticleManager._initialise();
//            ParticleFXPlugin.install();

            mHighLevelGpuProgramManager = new ENG_HighLevelGpuProgramManager();
            NullGpuProgramManager nullGpuProgramManager = new NullGpuProgramManager();
            NullTextureManager nullTextureManager = new NullTextureManager();
            mHighLevelGpuProgramManager.addFactory(new NullProgramFactory());

            hardwareBufferManager = new ENG_HardwareBufferManager(new GLDefaultHardwareBufferManagerBase());

            mEntityFactory = new ENG_EntityFactory();
            mItemFactory = new ENG_ItemFactory();
            addMovableObjectFactory(mEntityFactory);
            addMovableObjectFactory(mItemFactory);

        }
    }

    @Override
    public void destroy() {
        if (MainApp.getApplicationMode() == MainApp.Mode.CLIENT) {
            mSceneManager.removeRenderQueueListener(mOverlayManager.getPointer());
            mOverlayManager.destroy();
        }
        mSceneManager.destroy();
        wrapper.destroy();
    }

    @Override
    public long getPointer() {
        return wrapper.getPtr();
    }

    public void initialise(boolean autoCreateWindow) {
        initialise(autoCreateWindow, "OGRE Render Window", "");
    }

    public void initialise(boolean autoCreateWindow, String windowTitle,
                           String customCapabilitiesConfig) {
        wrapper.initialise(autoCreateWindow, windowTitle, customCapabilitiesConfig);
    }

    public ENG_SceneManager getSceneManager(String name) {
        return mSceneManager;
    }

    public ENG_SceneManager getSceneManager() {
        return mSceneManager;
    }

    public ENG_SceneManager createSceneManager(String name) {
        mSceneManager = new ENG_SceneManager(ENG_SceneManager.SceneType.ST_GENERIC.getType(),
                1, ENG_SceneManager.InstancingThreadedCullingMethod.INSTANCING_CULLING_SINGLETHREAD.getCullingMethod(),
                name);
        return mSceneManager;
    }

    public ENG_RenderWindow createRenderWindow(String name, int width, int height,
                                               boolean fullScreen, TreeMap<String, String> params) {

        if (mActiveRenderSystem == null) {
            throw new NullPointerException("Cannot create window - no render " +
                    "system has been selected.");
        }


        ENG_RenderWindow win = mActiveRenderSystem._createRenderWindow(
                name, width, height, fullScreen, params);

        currentRenderWindow = win;

        return win;
    }

    public ENG_RenderWindow getCurrentRenderWindow() {
        return currentRenderWindow;
    }

    protected void populateFrameEvent(FrameEventTimeType type, ENG_FrameEvent evtToUpdate) {
        long now = ENG_Utility.currentTimeMillis();
        evtToUpdate.timeSinceLastEvent = calculateEventTime(now, FrameEventTimeType.FETT_ANY);
        evtToUpdate.timeSinceLastFrame = calculateEventTime(now, type);
    }

    protected float calculateEventTime(long now, FrameEventTimeType type) {
        
        ArrayDeque<ENG_Long> times = mEventTimes[type.ordinal()];
        times.add(new ENG_Long(now));

        if (times.size() == 1) {
            return 0.0f;
        }

        long discardThreshold = (long) (mFrameSmoothingTime * 1000.0f);

        int len = times.size() - 2;
        for (int i = 0; i < len; ++i) {
            if ((now - times.peekFirst().getValue()) > discardThreshold) {
                times.pollFirst();
            } else {
                break;
            }
        }

        return (float) (times.getLast().getValue() - times.getFirst().getValue()) /
                ((times.size() - 1) * 1000);
    }

    /** @noinspection deprecation*/
    public void oneTimePostWindowInit() {
        if (!mFirstTimePostWindowInit) {
            mMaterialManager.initialise();
            ENG_MeshManager.getSingleton()._initialise();
            mFirstTimePostWindowInit = true;
        }
    }

    public boolean getRemoveRenderQueueStructuresOnClear() {
        return mRemoveQueueStructuresOnClear;
    }

    public void setRemoveRenderQueueStructuresOnClear(boolean r) {
        mRemoveQueueStructuresOnClear = r;
    }

    public void addMovableObjectFactory(ENG_MovableObjectFactory fact) {
        addMovableObjectFactory(fact, false);
    }

    public void addMovableObjectFactory(ENG_MovableObjectFactory fact,
                                        boolean overrideExisting) {
        ENG_MovableObjectFactory facti = mMovableObjectFactoryMap.get(fact.getType());
        if ((!overrideExisting) && (facti != null)) {
            throw new IllegalArgumentException("A factory of type '" +
                    fact.getType() + "' already exists.");
        }

        if (fact.requestTypeFlags()) {
            if ((facti != null) && (facti.requestTypeFlags())) {
                fact._notifyTypeFlags(facti.getTypeFlags());
            } else {
                fact._notifyTypeFlags(_allocateNextMovableObjectTypeFlag());
            }
        }

        mMovableObjectFactoryMap.put(fact.getType(), fact);
    }

    public boolean hasMovableObjectFactory(String name) {
        return mMovableObjectFactoryMap.containsKey(name);
    }

    public ENG_MovableObjectFactory getMovableObjectFactory(String name) {
        ENG_MovableObjectFactory fact = mMovableObjectFactoryMap.get(name);
        if (fact == null) {
            throw new IllegalArgumentException("MovableObjectFactory of type "
                    + name + " does not exist");
        }
        return fact;
    }

    public long _allocateNextMovableObjectTypeFlag() {
        if (mNextMovableObjectTypeFlag == ENG_SceneManager.USER_TYPE_MASK_LIMIT) {
            throw new ENG_InvalidFieldStateException("Cannot allocate a type flag since " +
                    "all the available flags have been used.");
        }
        int ret = mNextMovableObjectTypeFlag;
        mNextMovableObjectTypeFlag <<= 1;
        return ret;
    }

    public void removeMovableObjectFactory(ENG_MovableObjectFactory fact) {
        mMovableObjectFactoryMap.remove(fact.getType());
    }

    public Iterator<Entry<String, ENG_MovableObjectFactory>>
    getMovableObjectFactoryIterator() {
        return mMovableObjectFactoryMap.entrySet().iterator();
    }

    public ENG_RenderSystem getRenderSystem() {
        return mActiveRenderSystem;
    }

    public ENG_SceneManager _getCurrentSceneManager() {
        if (mSceneManagerStack.isEmpty()) {
            return null;
        }
        return mSceneManagerStack.peek();
    }

    public void _pushCurrentSceneManager(ENG_SceneManager sceneManager) {
        mSceneManagerStack.add(sceneManager);
    }

    public long getNextFrameNumber() {
        return mNextFrame;
    }

    public void _popCurrentSceneManager(ENG_SceneManager sceneManager) {
        if (mSceneManagerStack.peek() != sceneManager) {
            throw new IllegalArgumentException("sceneManager: " +
                    sceneManager.getName() + " is not at the top of the stack");
        }
        mSceneManagerStack.pop();
    }

    public void addFrameListener(ENG_FrameListener newListener) {
        if (!mRemovedFrameListeners.remove(newListener)) {
            mFrameListeners.add(newListener);
        }
    }

    public void removeFrameListener(ENG_FrameListener oldListener) {
        if (mFrameListeners.contains(oldListener)) {
            mRemovedFrameListeners.add(oldListener);
        }
    }

    public boolean _fireFrameStarted(ENG_FrameEvent evt) {
        mFrameListeners.removeAll(mRemovedFrameListeners);
        mRemovedFrameListeners.clear();

        for (ENG_FrameListener mFrameListener : mFrameListeners) {
            if (!mFrameListener.frameStarted(evt)) {
                return false;
            }
        }

        return true;
    }

    public boolean _fireFrameRenderingQueued(ENG_FrameEvent evt) {
        ++mNextFrame;

        mFrameListeners.removeAll(mRemovedFrameListeners);
        mRemovedFrameListeners.clear();

        for (ENG_FrameListener mFrameListener : mFrameListeners) {
            if (!mFrameListener.frameRenderingQueued(evt)) {
                return false;
            }
        }

        return true;
    }

    public boolean _fireFrameEnded(ENG_FrameEvent evt) {
        mFrameListeners.removeAll(mRemovedFrameListeners);
        mRemovedFrameListeners.clear();

        boolean ret = true;
        for (ENG_FrameListener mFrameListener : mFrameListeners) {
            if (!mFrameListener.frameEnded(evt)) {
                ret = false;
                break;
            }
        }

        if (ENG_HardwareBufferManager.getSingleton() != null) {
            ENG_HardwareBufferManager.getSingleton()._releaseBufferCopies();
        }

        return ret;
    }

    public boolean _fireFrameStarted() {
        ENG_FrameEvent evt = new ENG_FrameEvent();
        populateFrameEvent(FrameEventTimeType.FETT_STARTED, evt);

        return _fireFrameStarted(evt);
    }

    public boolean _fireFrameRenderingQueued() {
        ENG_FrameEvent evt = new ENG_FrameEvent();
        populateFrameEvent(FrameEventTimeType.FETT_QUEUED, evt);

        return _fireFrameRenderingQueued(evt);
    }

    public boolean _fireFrameEnded() {
        ENG_FrameEvent evt = new ENG_FrameEvent();
        populateFrameEvent(FrameEventTimeType.FETT_ENDED, evt);

        return _fireFrameEnded(evt);
    }

    public ArrayList<ENG_RenderSystem> getAvailableRenderers() {
        return mRenderers;
    }

    public ENG_RenderSystem getRenderSystemByName(String name) {
        if (name.isEmpty()) {
            return null;
        }

        for (ENG_RenderSystem rs : mRenderers) {
            if (rs.getName().equals(name)) {
                return rs;
            }
        }

        return null;
    }

    public void addRenderSystem(ENG_RenderSystem newRend) {
        mRenderers.add(newRend);
    }

    public ENG_RenderQueueInvocationSequence createRenderQueueInvocationSequence(String name) {
        if (mRQSequenceMap.containsKey(name)) {
            throw new IllegalArgumentException("RenderQueueInvocationSequence with the name " + name +
                    " already exists.");
        }
        return mRQSequenceMap.put(name, new ENG_RenderQueueInvocationSequence(name));
    }

    public ENG_RenderQueueInvocationSequence getRenderQueueInvocationSequence(
            String name) {
        ENG_RenderQueueInvocationSequence rs = mRQSequenceMap.get(name);
        if (rs == null) {
            throw new IllegalArgumentException("RenderQueueInvocationSequence with the name " + name +
                    " not found.");
        }
        return rs;
    }

    public void destroyRenderQueueInvocationSequence(String name) {
        mRQSequenceMap.remove(name);
    }

    public void destroyAllRenderQueueInvocationSequences() {
        mRQSequenceMap.clear();
    }

    public boolean _updateAllRenderTargets() {
        mActiveRenderSystem._updateAllRenderTargets(false);
        // give client app opportunity to use queued GPU time
        boolean ret = _fireFrameRenderingQueued();

        mActiveRenderSystem._swapAllRenderTargetBuffers(
                mActiveRenderSystem.getWaitForVerticalBlank());

        mSceneManager._handleLodEvents();

        return ret;
    }

    public boolean _updateAllRenderTargets(ENG_FrameEvent evt) {
        mActiveRenderSystem._updateAllRenderTargets(false);
        // give client app opportunity to use queued GPU time
        boolean ret = _fireFrameRenderingQueued(evt);

        mActiveRenderSystem._swapAllRenderTargetBuffers(
                mActiveRenderSystem.getWaitForVerticalBlank());

        mSceneManager._handleLodEvents();

        return ret;
    }

    public void clearEventTimes() {
        for (int i = 0; i < FrameEventTimeType.FETT_COUNT.ordinal(); ++i) {
            mEventTimes[i].clear();
        }
    }

    public void queueEndRendering() {
        mQueuedEnd = true;
    }

    public void startRendering() {
        if (mActiveRenderSystem == null) {
            throw new NullPointerException("No active render system");
        }

        mActiveRenderSystem._initRenderTargets();

        // Clear event times
        clearEventTimes();

        // Infinite loop, until broken out of by frame listeners
        // or break out by calling queueEndRendering()
        mQueuedEnd = false;

        while (!mQueuedEnd) {


            if (!renderOneFrame()) {
                break;
            }
        }
    }

    public boolean renderOneFrame() {
        
        return _fireFrameStarted() /*&& _updateAllRenderTargets()*/ && _fireFrameEnded();

    }

    public void prepareForRenderingOneFrameNative() {
        mSceneManager.prepareForNativeRendering();
        ENG_NativeCalls.frameStats_update(wrapper.getPtr(), currentRenderWindow.mStats);
    }

    /**
     * We need this after we call ENG_NativeCalls.getItemsAabbs();
     */
    public void prepareForRenderingOneFrameNativeLastCallBeforeFlush() {
        mSceneManager.prepareItemRingListForNativeRendering();
    }

    public boolean renderOneFrame(float timeSinceLastFrame) {
        ENG_FrameEvent evt = new ENG_FrameEvent();
        evt.timeSinceLastFrame = timeSinceLastFrame;

        long now = ENG_Utility.currentTimeMillis();
        evt.timeSinceLastEvent = calculateEventTime(now, FrameEventTimeType.FETT_ANY);

        if (!_fireFrameStarted(evt)) {
            return false;
        }

        if (!_updateAllRenderTargets(evt)) {
            return false;
        }

        now = ENG_Utility.currentTimeMillis();
        evt.timeSinceLastEvent = calculateEventTime(now, FrameEventTimeType.FETT_ANY);

        return _fireFrameEnded(evt);
    }

    public void shutdown() {

    }

    public void setRenderSystem(ENG_RenderSystem system) {
        if ((mActiveRenderSystem != null) && (mActiveRenderSystem != system)) {
            mActiveRenderSystem.shutDown();
        }

        mActiveRenderSystem = system;

        _getCurrentSceneManager()._setDestinationRenderSystem(mActiveRenderSystem);
    }

    public ENG_RenderTarget getRenderTarget(String name) {
        if (mActiveRenderSystem == null) {
            throw new NullPointerException("Cannot get target - no render " +
                    "system has been selected.");
        }

        return mActiveRenderSystem.getRenderTarget(name);
    }

    public void setFrameSmoothingPeriod(float period) {
        mFrameSmoothingTime = period;
    }

    public float getFrameSmoothingPeriod() {
        return mFrameSmoothingTime;
    }

    /**
     * @return the renderRoot
     */
    public static ENG_RenderRoot getRenderRoot() {
        return renderRoot;
    }

    public static ENG_RenderRoot getRenderRootWithLock() {

        renderRootLock.lock();
        try {
            return renderRoot;
        } finally {
            renderRootLock.unlock();
        }
    }

    private static void setRenderRoot(ENG_RenderRoot root) {
        renderRootLock.lock();
        try {
            renderRoot = root;
        } finally {
            renderRootLock.unlock();
        }
    }

    public void convertColourValue(ENG_ColorValue mColour, ENG_Integer colour) {
        
        assert (mActiveRenderSystem != null);
        int[] i = new int[1];
        mActiveRenderSystem.convertColourValue(mColour, i, 0);
        colour.setValue(i[0]);
    }

    private static final boolean GL_DEBUG = MainApp.GL_DEBUG;

    public static boolean isGLDebugEnabled() {
        return GL_DEBUG;
    }

    public float getScreenDensity() {
        return screenDensity;
    }

//    public void setSwapBufferEnabled(boolean swap) {
//    	Gdx.app.setSwapBuffersAfterRendering(swap);
//    }
//    
//    public boolean isSwapBufferEnabled() {
//    	return Gdx.app.isSwapBuffersAfterRenderingEnabled();
//    }


    public ENG_SilverBack getGorilla() {
        return mGorilla;
    }

    public ENG_CompositorManager getCompositorManager() {
        return mCompositorManager;
    }

    public ENG_ControllerManager getControllerManager() {
        return mControllerManager;
    }

    public ENG_LodStrategyManager getLodManager() {
        return mLodManager;
    }

    public ENG_FontManager getFontManager() {
        return mFontManager;
    }

    public ENG_RenderSystem getActiveRenderSystem() {
        return mActiveRenderSystem;
    }

    public ENG_HardwareBufferManager getHardwareBufferManager() {
        return hardwareBufferManager;
    }

    public void setHardwareBufferManager(ENG_HardwareBufferManager hardwareBufferManager) {
        this.hardwareBufferManager = hardwareBufferManager;
    }

    public ENG_HighLevelGpuProgramManager getHighLevelGpuProgramManager() {
        return mHighLevelGpuProgramManager;
    }

    public ENG_MaterialManager getMaterialManager() {
        return mMaterialManager;
    }

    /** @noinspection deprecation*/
    public ENG_MeshManager getMeshManager() {
        return mMeshManager;
    }

    public ENG_OverlayManager getOverlayManager() {
        return mOverlayManager;
    }

    public ENG_ParticleSystemManager getParticleManager() {
        return mParticleManager;
    }

    public ENG_SkeletonManager getSkeletonManager() {
        return mSkeletonManager;
    }

    public static boolean isContinuousRendering() {
        continuousRenderingLock.lock();
        try {
            return continuousRendering;
        } finally {
            continuousRenderingLock.unlock();
        }
    }

    public static ReentrantLock getContinuousRenderingLock() {
        return continuousRenderingLock;
    }

    public static boolean isContinuousRenderingWithoutLock() {
        return continuousRendering;
    }

    /**
     * When you set the continuous rendering to false we must make sure we do so after the
     * rendering thread has finished flushing the pipeline and finished the slow call.
     * @param continuousRenderingLocal
     */
    public static void setContinuousRendering(boolean continuousRenderingLocal) {
        boolean shouldWait = false;
        if (continuousRendering && !continuousRenderingLocal) {

            shouldWait = true;
        }
        continuousRenderingLock.lock();
        try {
            continuousRendering = continuousRenderingLocal;
            if (shouldWait) {
                continuousRenderingLatch = new CountDownLatch(1);
            }
        } finally {
            continuousRenderingLock.unlock();
        }
        if (shouldWait) {
            try {
                continuousRenderingLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            continuousRenderingLatch = null;
        }
        Gdx.graphics.setContinuousRendering(continuousRenderingLocal);
    }

    public static void requestRenderingIfRequired() {
        boolean continuousRenderingLocal;
        continuousRenderingLock.lock();
        try {
            continuousRenderingLocal = continuousRendering;
        } finally {
            continuousRenderingLock.unlock();
        }
        if (!continuousRenderingLocal) {
            Gdx.graphics.requestRendering();
        }
    }

    public static void releaseContinuousRenderingLatch() {
        if (continuousRenderingLatch != null) {
            continuousRenderingLatch.countDown();
        }
    }

    public void renderOneFrameFastCall() {
        renderOneFrameFastCall(false);
    }

    public void renderOneFrameFastCall(boolean waitForRenderingToFinish) {
        ENG_NativeCalls.callRoot_RenderOneFrame(((AndroidRenderWindow) ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow()).getPointer());
        ENG_RenderingThread.flushPipeline(waitForRenderingToFinish);
    }

    public void renderOneFrameSlowCall() {
        renderOneFrameNative(getPointer());
    }

    private static native void renderOneFrameNative(long rootPtr);
}
