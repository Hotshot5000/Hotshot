/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainActivity;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.basictypes.ENG_Integer;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;

public abstract class ENG_RenderTarget {

    public static final int OGRE_NUM_RENDERTARGET_GROUPS = 10;
    public static final int OGRE_DEFAULT_RT_GROUP = 4;
    public static final int OGRE_REND_TO_TEX_RT_GROUP = 2;

    public enum StatFlags {
        SF_NONE(0),
        SF_FPS(1),
        SF_AVG_FPS(2),
        SF_BEST_FPS(4),
        SF_WORST_FPS(8),
        SF_TRIANGLE_COUNT(16),
        SF_ALL(0xFFFF);

        private final int flags;

        StatFlags(int flags) {
            this.flags = flags;
        }

        public int getFlags() {
            return flags;
        }
    }

    public static class FrameStats {
        public float lastFPS;
        public float avgFPS;
        public float bestFPS;
        public float worstFPS;
        public long bestFrameTime;
        public long worstFrameTime;
        public int triangleCount;
        public int batchCount;
        public float bestFrameTimeFloat;
        public float worstFrameTimeFloat;
        public float lastTime;
        public float avgTime;
    }

    public enum FrameBuffer {
        FB_FRONT,
        FB_BACK,
        FB_AUTO
    }

    protected String mName;
    protected byte mPriority = OGRE_DEFAULT_RT_GROUP;

    protected int mWidth;
    protected int mHeight;
    protected float mInvWidth;
    protected float mInvHeight;
    protected int mColourDepth;
    protected boolean mIsDepthBuffered;

    protected final FrameStats mStats = new FrameStats();

    protected long mLastSecond;
    protected long mLastTime;
    protected int mFrameCount;

    protected boolean mActive = true;
    protected boolean mAutoUpdate = true;
    // Hardware sRGB gamma conversion done on write?
    protected boolean mHwGamma;

    protected int mFSAA;
    protected String mFSAAHint;

    protected final TreeMap<ENG_Integer, ENG_Viewport> mViewportList = new TreeMap<>();
    protected final ArrayList<ENG_Integer> mViewportIntList = new ArrayList<>();

    protected final ArrayList<ENG_RenderTargetListener> mListeners = new ArrayList<>();

    private final ENG_RenderTargetEvent revt = new ENG_RenderTargetEvent(this);
    private final ENG_RenderTargetViewportEvent vevnt = new ENG_RenderTargetViewportEvent();

    private final ENG_Integer ind = new ENG_Integer();

/*	protected void updateStats() {
		
	}*/

    public void updateImpl() {
        _beginUpdate();
        _updateAutoUpdatedViewports(true);
        _endUpdate();
    }

    protected void firePreUpdate() {
        int len = mListeners.size();

        for (int i = 0; i < len; ++i) {
            mListeners.get(i).preRenderTargetUpdate(revt);
        }
    }

    protected void firePostUpdate() {
        int len = mListeners.size();

        for (int i = 0; i < len; ++i) {
            mListeners.get(i).postRenderTargetUpdate(revt);
        }
    }

    protected void fireViewportPreUpdate(ENG_Viewport vp) {
        vevnt.source = vp;

        int len = mListeners.size();

        for (int i = 0; i < len; ++i) {
            mListeners.get(i).preViewportUpdate(vevnt);
        }
    }

    protected void fireViewportPostUpdate(ENG_Viewport vp) {
        vevnt.source = vp;

        int len = mListeners.size();

        for (int i = 0; i < len; ++i) {
            mListeners.get(i).postViewportUpdate(vevnt);
        }
    }

    protected void fireViewportAdded(ENG_Viewport vp) {
        vevnt.source = vp;

        int len = mListeners.size();

        for (int i = 0; i < len; ++i) {
            mListeners.get(i).viewportAdded(vevnt);
        }
    }

    protected void fireViewportRemoved(ENG_Viewport vp) {
        vevnt.source = vp;

        int len = mListeners.size();

        for (int i = 0; i < len; ++i) {
            mListeners.get(i).viewportRemoved(vevnt);
        }
    }

    public ENG_RenderTarget() {
        resetStatistics();
    }

    public void _setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void _setWidth(int width) {
        mWidth = width;
        mInvWidth = 1.0f / width;
    }

    public int getWidth() {
        return mWidth;
    }

    public void _setHeight(int height) {
        mHeight = height;
        mInvHeight = 1.0f / height;
    }

    public int getHeight() {
        return mHeight;
    }

    public float getInvWidth() {
        return mInvWidth;
    }

    public float getInvHeight() {
        return mInvHeight;
    }

    public void _setColourDepth(int colDepth) {
        mColourDepth = colDepth;
    }

    public int getColourDepth() {
        return mColourDepth;
    }

    public void _beginUpdate() {
        firePreUpdate();

        mStats.triangleCount = 0;
        mStats.batchCount = 0;
    }

    public void _updateAutoUpdatedViewports() {
        _updateAutoUpdatedViewports(true);
    }

    public void _updateAutoUpdatedViewports(boolean updateStatistics) {
        int len = mViewportIntList.size();

        for (int i = 0; i < len; ++i) {
            ENG_Viewport v = mViewportList.get(mViewportIntList.get(i));
            if (v.isAutoUpdated()) {
                _updateViewport(v, updateStatistics);
            }
        }
    }

    public void _endUpdate() {
        firePostUpdate();

        updateStats();
    }

    public void _updateViewport(ENG_Viewport viewport) {
        _updateViewport(viewport, true);
    }

    public void _updateViewport(ENG_Viewport viewport, boolean updateStatistics) {
        if (viewport.getTarget() != this) {
            throw new IllegalArgumentException("the requested viewport is " +
                    "not bound to the rendertarget!");
        }

        fireViewportPreUpdate(viewport);
        viewport.update();
        if (updateStatistics) {
            mStats.triangleCount += viewport._getNumRenderedFaces();
            mStats.batchCount += viewport._getNumRenderedBatches();
        }
        fireViewportPostUpdate(viewport);
    }

    public void _updateViewport(int zorder) {
        _updateViewport(zorder, true);
    }

    public void _updateViewport(int zorder, boolean updateStatistics) {
        ind.setValue(zorder);
        ENG_Viewport v = mViewportList.get(ind);
        if (v != null) {
            _updateViewport(v, updateStatistics);
        } else {
            if (MainActivity.isDebugmode()) {
                throw new IllegalArgumentException("No viewport with given zorder!");
            }
        }
    }

    public ENG_Viewport addViewport(ENG_Camera cam) {
        return addViewport(cam, 0, 0.0f, 0.0f, 1.0f, 1.0f);
    }

    public ENG_Viewport addViewport(ENG_Camera cam, int ZOrder,
                                    float left, float top, float width, float height) {
        return addViewport(cam, ZOrder, (int) left, (int) top, (int) width, (int) height);
    }

    /**
     * Adds a viewport to the rendering target.
     *
     * @param cam    The camera from which the viewport contents will be rendered (mandatory)
     * @param ZOrder The relative order of the viewport with others on the target (allows overlapping
     *               viewports i.e. picture-in-picture). Higher ZOrders are on top of lower ones. The actual number
     *               is irrelevant, only the relative ZOrder matters (you can leave gaps in the numbering)
     * @param left   The relative position of the left of the viewport on the target, as a value between 0 and 1.
     * @param top    The relative position of the top of the viewport on the target, as a value between 0 and 1.
     * @param width  The relative width of the viewport on the target, as a value between 0 and 1.
     * @param height The relative height of the viewport on the target, as a value between 0 and 1.
     * @remarks A viewport is the rectangle into which rendering output is sent. This method adds
     * a viewport to the render target, rendering from the supplied camera. The
     * rest of the parameters are only required if you wish to add more than one viewport
     * to a single rendering target. Note that size information passed to this method is
     * passed as a parametric, i.e. it is relative rather than absolute. This is to allow
     * viewports to automatically resize along with the target.
     */
    public ENG_Viewport addViewport(ENG_Camera cam, int ZOrder,
                                    int left, int top, int width, int height) {
        ind.setValue(ZOrder);

        if (mViewportList.containsKey(ind)) {
            throw new IllegalArgumentException("Can't create another viewport for " +
                    mName + " with Z-Order " + ZOrder +
                    " because a viewport exists with this Z-Order already.");
        }

        ENG_Viewport vp = new ENG_Viewport(cam, this, left, top, width, height, ZOrder);

        ENG_Integer integer = new ENG_Integer(ZOrder);
        mViewportIntList.add(integer);
        mViewportList.put(integer, vp);

        fireViewportAdded(vp);

        return vp;
    }

    public void removeViewport(int ZOrder) {
        ind.setValue(ZOrder);

        if (mViewportList.containsKey(ind)) {
            fireViewportRemoved(mViewportList.get(ind));
            mViewportList.remove(ind);
        } else {
            if (MainActivity.isDebugmode()) {
                throw new IllegalArgumentException("Invalid ZOrder");
            }
        }
    }

    public void removeAllViewports() {

        for (Entry<ENG_Integer, ENG_Viewport> eng_integerENG_viewportEntry : mViewportList.entrySet()) {
            ENG_Viewport vp = eng_integerENG_viewportEntry.getValue();
            fireViewportRemoved(vp);
        }

        mViewportList.clear();
    }

    public FrameStats getStatistics() {
        return mStats;
    }

    public float getLastFPS() {
        return mStats.lastFPS;
    }

    public float getAverageFPS() {
        return mStats.avgFPS;
    }

    public float getBestFPS() {
        return mStats.bestFPS;
    }

    public float getWorstFPS() {
        return mStats.worstFPS;
    }

    public int getTriangleCount() {
        return mStats.triangleCount;
    }

    public int getBatchCount() {
        return mStats.batchCount;
    }

    public float getBestFrameTime() {
        return mStats.bestFrameTimeFloat;
    }

    public float getWorstFrameTime() {
        return mStats.worstFrameTimeFloat;
    }

    public void resetStatistics() {
        mStats.avgFPS = 0.0f;
        mStats.bestFPS = 0.0f;
        mStats.lastFPS = 0.0f;
        mStats.worstFPS = 999.0f;
        mStats.triangleCount = 0;
        mStats.batchCount = 0;
        mStats.bestFrameTime = 999999;
        mStats.worstFrameTime = 0;
        mLastTime = ENG_Utility.currentTimeMillis();
        mLastSecond = mLastTime;
        mFrameCount = 0;
    }

    public void updateStats() {
        ++mFrameCount;
        long thisTime = ENG_Utility.currentTimeMillis();

        long frameTime = thisTime - mLastTime;
        mLastTime = thisTime;

        mStats.bestFrameTime = Math.min(mStats.bestFrameTime, frameTime);
        mStats.worstFrameTime = Math.max(mStats.worstFrameTime, frameTime);

        if ((thisTime - mLastSecond) > 1000) {
            // new second - not 100% precise
            mStats.lastFPS =
                    (float) mFrameCount / (float) (thisTime - mLastSecond) * 1000.0f;

            if (mStats.avgFPS == 0.0f) {
                mStats.avgFPS = mStats.lastFPS;
            } else {
                mStats.avgFPS = (mStats.avgFPS + mStats.lastFPS) / 2; // not strictly correct, but good enough
            }

            mStats.bestFPS = Math.max(mStats.bestFPS, mStats.lastFPS);
            mStats.worstFPS = Math.min(mStats.worstFPS, mStats.lastFPS);

            mLastSecond = thisTime;
            mFrameCount = 0;
        }
    }

    public Object getCustomAttribute(String name, Object data) {
        throw new UnsupportedOperationException("Attribute not found.");
    }

    public void addListener(ENG_RenderTargetListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(ENG_RenderTargetListener listener) {
        boolean ret = mListeners.remove(listener);

        if (MainActivity.isDebugmode()) {
            if (!ret) {
                throw new IllegalArgumentException("listener is not active");
            }
        }
    }

    public void removeAllListeners() {
        mListeners.clear();
    }

    public int getNumViewports() {
        return mViewportList.size();
    }

    public ENG_Viewport getViewport(int index) {
        if ((index < 0) || (index >= mViewportList.size())) {
            throw new IllegalArgumentException("index out of bounds");
        }

        ind.setValue(index);
        return mViewportList.get(ind);
    }

    public boolean isActive() {
        return mActive;
    }

    public void setActive(boolean active) {
        mActive = active;
    }

    public void _notifyCameraRemoved(ENG_Camera cam) {

        for (Entry<ENG_Integer, ENG_Viewport> eng_integerENG_viewportEntry : mViewportList.entrySet()) {
            ENG_Viewport v = eng_integerENG_viewportEntry.getValue();

            if (v.getCamera() == cam) {
                v.setCamera(null);
            }
        }
    }

    public void setAutoUpdated(boolean autoup) {
        mAutoUpdate = autoup;
    }

    public boolean isAutoUpdated() {
        return mAutoUpdate;
    }

    public boolean isPrimary() {
        return false;
    }

    public Object getImpl() {
        return null;
    }

    public void swapBuffers() {
        swapBuffers(true);
    }

    public void swapBuffers(boolean waitForVSync) {

    }

    public void update() {
        update(true);
    }

    public void update(boolean swapBuffers) {
        updateImpl();

        if (swapBuffers) {
            swapBuffers(
                    ENG_RenderRoot.getRenderRoot().getRenderSystem().getWaitForVerticalBlank());
        }
    }

    public void setPriority(byte priority) {
        mPriority = priority;
    }

    public byte getPriority() {
        return mPriority;
    }

    public void copyContentsToMemory(ENG_PixelBox dst) {
        copyContentsToMemory(dst, FrameBuffer.FB_AUTO);
    }

    public abstract void copyContentsToMemory(ENG_PixelBox dst, FrameBuffer buffer);

    public abstract boolean requiresTextureFlipping();

    public boolean isHardwareGammaEnabled() {
        return mHwGamma;
    }

    public int getFSAA() {
        return mFSAA;
    }

    public String getFSAAHint() {
        return mFSAAHint;
    }

    public void destroy(boolean skipGLDelete) {
        

    }
}
