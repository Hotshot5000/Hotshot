/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_AxisAlignedBox;
import headwayent.hotshotengine.ENG_Controller;
import headwayent.hotshotengine.ENG_ControllerTypeFloat;
import headwayent.hotshotengine.ENG_IControllerValue;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_ParamCommand;
import headwayent.hotshotengine.ENG_ParamDictionary;
import headwayent.hotshotengine.ENG_ParameterDef;
import headwayent.hotshotengine.ENG_ParameterDef.ParameterType;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_StringIntefaceInterface;
import headwayent.hotshotengine.ENG_StringInterface;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.renderer.ENG_Common.SortMode;
import headwayent.hotshotengine.renderer.ENG_Particle.ParticleType;
import headwayent.hotshotengine.renderer.ENG_RenderableImpl.Visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ENG_ParticleSystem extends ENG_MovableObject implements
        ENG_StringIntefaceInterface {

    public static class CmdQuota implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleSystem) target).getParticleQuota());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleSystem) target).setParticleQuota(Integer.parseInt(val));
        }

    }

    public static class CmdEmittedEmitterQuota implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleSystem) target)
                    .getEmittedEmitterQuota());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleSystem) target).setEmittedEmitterQuota(Integer.parseInt(val));
        }

    }

    public static class CmdMaterial implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return ((ENG_ParticleSystem) target).getMaterialName();
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleSystem) target).setMaterialName(val);
        }

    }

    public static class CmdCull implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleSystem) target)
                    .getCullIndividually());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleSystem) target).setCullIndividually(Boolean.parseBoolean(val));
        }

    }

    public static class CmdWidth implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleSystem) target)
                    .getDefaultWidth());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleSystem) target).setDefaultWidth(Integer.parseInt(val));
        }

    }

    public static class CmdHeight implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleSystem) target)
                    .getDefaultHeight());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleSystem) target).setDefaultHeight(Integer.parseInt(val));
        }

    }

    public static class CmdRenderer implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return ((ENG_ParticleSystem) target).getRendererName();
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleSystem) target).setRenderer(val);
        }

    }

    public static class CmdSorted implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleSystem) target)
                    .getSortingEnabled());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleSystem) target).setSortingEnabled(Boolean.parseBoolean(val));
        }

    }

    public static class CmdLocalSpace implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleSystem) target)
                    .getKeepParticlesInLocalSpace());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleSystem) target)
                    .setKeepParticlesInLocalSpace(Boolean.parseBoolean(val));
        }

    }

    public static class CmdIterationInterval implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleSystem) target)
                    .getIterationInterval());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleSystem) target)
                    .setIterationInterval(Float.parseFloat(val));
        }

    }

    public static class CmdNonvisibleTimeout implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleSystem) target)
                    .getNonVisibleUpdateTimeout());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleSystem) target)
                    .setNonVisibleUpdateTimeout(Float.parseFloat(val));
        }

    }

    /// Command objects
    protected static final CmdCull msCullCmd = new CmdCull();
    protected static final CmdHeight msHeightCmd = new CmdHeight();
    protected static final CmdMaterial msMaterialCmd = new CmdMaterial();
    protected static final CmdQuota msQuotaCmd = new CmdQuota();
    protected static final CmdEmittedEmitterQuota msEmittedEmitterQuotaCmd =
            new CmdEmittedEmitterQuota();
    protected static final CmdWidth msWidthCmd = new CmdWidth();
    protected static final CmdRenderer msRendererCmd = new CmdRenderer();
    protected static final CmdSorted msSortedCmd = new CmdSorted();
    protected static final CmdLocalSpace msLocalSpaceCmd = new CmdLocalSpace();
    protected static final CmdIterationInterval msIterationIntervalCmd = new CmdIterationInterval();
    protected static final CmdNonvisibleTimeout msNonvisibleTimeoutCmd = new CmdNonvisibleTimeout();

    private final ENG_StringInterface stringInterface = new ENG_StringInterface(this);

    protected final ENG_AxisAlignedBox mAABB = new ENG_AxisAlignedBox();
    protected float mBoundingRadius = 1.0f;
    protected boolean mBoundsAutoUpdate = true;
    protected float mBoundsUpdateTime = 10.0f;
    protected float mUpdateRemainTime;

    /// World AABB, only used to compare world-space positions to calc bounds
    protected final ENG_AxisAlignedBox mWorldAABB = new ENG_AxisAlignedBox();

    /// Name of the resource group to use to load materials
    protected String mResourceGroupName = "";
    /// Name of the material to use
    protected String mMaterialName = "";
    /// Have we set the material etc on the renderer?
    protected boolean mIsRendererConfigured;
    /// Pointer to the material to use
    protected ENG_Material mpMaterial;
    /// Default width of each particle
    protected float mDefaultWidth;
    /// Default height of each particle
    protected float mDefaultHeight;
    /// Speed factor
    protected float mSpeedFactor = 1.0f;
    /// Iteration interval
    protected float mIterationInterval;
    /// Iteration interval set? Otherwise track default
    protected boolean mIterationIntervalSet;
    /// Particles sorted according to camera?
    protected boolean mSorted;
    /// Particles in local space?
    protected boolean mLocalSpace;
    /// Update timeout when nonvisible (0 for no timeout)
    protected float mNonvisibleTimeout;
    /// Update timeout when nonvisible set? Otherwise track default
    protected boolean mNonvisibleTimeoutSet;
    /// Amount of time non-visible so far
    protected float mTimeSinceLastVisible;
    /// Last frame in which known to be visible
    protected long mLastVisibleFrame;
    /// Controller for time update
    protected ENG_Controller<ENG_ControllerTypeFloat> mTimeController;
    /// Indication whether the emitted emitter pool (= pool with particle emitters that are emitted) is initialised
    protected boolean mEmittedEmitterPoolInitialised;
    /// Used to control if the particle system should emit particles or not.
    protected boolean mIsEmitting = true;

    private static class SortByDirectionFunctor implements Comparator<ENG_Particle> {

        public final ENG_Vector4D sortDir = new ENG_Vector4D();

        public SortByDirectionFunctor() {

        }

        public SortByDirectionFunctor(ENG_Vector4D sortDir) {
            this.sortDir.set(sortDir);
        }

        public void setSortDir(ENG_Vector4D sortDir) {
            this.sortDir.set(sortDir);
        }

        @Override
        public int compare(ENG_Particle arg0, ENG_Particle arg1) {

            float f0 = sortDir.dotProduct(arg0.position);
            float f1 = sortDir.dotProduct(arg1.position);
            return (int) (f0 - f1);
        }


    }

    private static class SortByDistanceFunctor implements Comparator<ENG_Particle> {

        public final ENG_Vector4D sortPos = new ENG_Vector4D();
        public final ENG_Vector4D temp = new ENG_Vector4D();

        public SortByDistanceFunctor() {

        }

        public SortByDistanceFunctor(ENG_Vector4D sortPos) {
            this.sortPos.set(sortPos);
        }

        public void setSortPos(ENG_Vector4D sortPos) {
            this.sortPos.set(sortPos);
        }

        @Override
        public int compare(ENG_Particle lhs, ENG_Particle rhs) {

            sortPos.sub(lhs.position, temp);
            float f0 = -temp.squaredLength();
            sortPos.sub(rhs.position, temp);
            float f1 = -temp.squaredLength();
            return (int) (f0 - f1);
        }


    }

    private static final SortByDirectionFunctor sortByDirectionFunctor =
            new SortByDirectionFunctor();
    private static final SortByDistanceFunctor sortByDistanceFunctor =
            new SortByDistanceFunctor();

    /**
     * Active particle list.
     *
     * @remarks This is a linked list of pointers to particles in the particle pool.
     * @par This allows very fast insertions and deletions from anywhere in
     * the list to activate / deactivate particles as well as reuse of
     * Particle instances in the pool without construction & destruction
     * which avoids memory thrashing.
     */
    protected final LinkedList<ENG_Particle> mActiveParticles =
            new LinkedList<>();

    /**
     * Free particle queue.
     *
     * @remarks This contains a list of the particles free for use as new instances
     * as required by the set. Particle instances are preconstructed up
     * to the estimated size in the mParticlePool vector and are
     * referenced on this deque at startup. As they get used this list
     * reduces, as they get released back to to the set they get added
     * back to the list.
     */
    protected final LinkedList<ENG_Particle> mFreeParticles =
            new LinkedList<>();

    /**
     * Pool of particle instances for use and reuse in the active particle list.
     *
     * @remarks This vector will be pfloatlocated with the estimated size of the set,and will extend as required.
     */
    protected final ArrayList<ENG_Particle> mParticlePool =
            new ArrayList<>();

    /**
     * Pool of emitted emitters for use and reuse in the active emitted emitter list.
     *
     * @remarks The emitters in this pool act as particles and as emitters. The pool is a map containing lists
     * of emitters, identified by their name.
     * @par The emitters in this pool are cloned using emitters that are kept in the main emitter list
     * of the ParticleSystem.
     */
    protected final TreeMap<String, ArrayList<ENG_ParticleEmitter>> mEmittedEmitterPool =
            new TreeMap<>();

    /**
     * Free emitted emitter list.
     *
     * @remarks This contains a list of the emitters free for use as new instances as required by the set.
     */
    protected final TreeMap<String, LinkedList<ENG_ParticleEmitter>> mFreeEmittedEmitters =
            new TreeMap<>();

    /**
     * Active emitted emitter list.
     *
     * @remarks This is a linked list of pointers to emitters in the emitted emitter pool.
     * Emitters that are used are stored (their pointers) in both the list with active particles and in
     * the list with active emitted emitters.
     */
    protected final LinkedList<ENG_ParticleEmitter> mActiveEmittedEmitters
            = new LinkedList<>();

    /// List of particle emitters, ie sources of particles
    protected final ArrayList<ENG_ParticleEmitter> mEmitters =
            new ArrayList<>();
    /// List of particle affectors, ie modifiers of particles
    protected final ArrayList<ENG_ParticleAffector> mAffectors =
            new ArrayList<>();

    /// The renderer used to render this particle system
    protected ENG_ParticleSystemRenderer mRenderer;

    /// Do we cull each particle individually?
    protected boolean mCullIndividual;

    /// The name of the type of renderer used to render this system
    protected String mRendererType = "";

    /// The number of particles in the pool.
    protected int mPoolSize;

    /// The number of emitted emitters in the pool.
    protected int mEmittedEmitterPoolSize;

    /// Optional origin of this particle system (eg script name)
    protected String mOrigin = "";

    /// Default iteration interval
    protected static float msDefaultIterationInterval;
    /// Default nonvisible update timeout
    protected static float msDefaultNonvisibleTimeout;

    public ENG_ParticleSystem() {
        
        initParameters();

        // Default to billboard renderer
        setRenderer("billboard");
    }

    public ENG_ParticleSystem(String name) {
        super(name);
        
        mLastVisibleFrame = ENG_RenderRoot.getRenderRoot().getNextFrameNumber();

        setDefaultDimensions(100, 100);
        setMaterialName("BaseWhite");
        // Default to 10 particles, expect app to specify (will only be increased, not decreased)
        setParticleQuota(10);
        setEmittedEmitterQuota(3);
        initParameters();

        // Default to billboard renderer
        setRenderer("billboard");
    }

    public void destroy(boolean skipGLDelete) {
        mRenderer.destroy(skipGLDelete);
    }

    @Override
    public ENG_StringInterface getStringInterface() {

        return stringInterface;
    }

    public void setRenderer(String name) {
        if (mRenderer != null) {
            destroyVisualParticles(0, mParticlePool.size());
            mRenderer = null;
        }

        if (!name.isEmpty()) {
            mRenderer = ENG_ParticleSystemManager.getSingleton()._createRenderer(name);
            mIsRendererConfigured = false;
        }
    }

    public ENG_ParticleSystemRenderer getRenderer() {
        return mRenderer;
    }

    public String getRendererName() {
        return mRenderer.getType() != null ? mRenderer.getType() : "";
    }

    public ENG_ParticleEmitter addEmitter(String name) {
        ENG_ParticleEmitter emitter =
                ENG_ParticleSystemManager.getSingleton()._createEmitter(name, this);
        mEmitters.add(emitter);
        return emitter;
    }

    public ENG_ParticleEmitter getEmitter(short index) {
        if (index < 0 || index >= mEmitters.size()) {
            throw new IllegalArgumentException("index out of range max is " +
                    mEmitters.size());
        }
        return mEmitters.get(index);
    }

    public short getNumEmitters() {
        return (short) mEmitters.size();
    }

    public void removeEmitter(short index) {
        if (index < 0 || index >= mEmitters.size()) {
            throw new IllegalArgumentException("index out of range max is " +
                    mEmitters.size());
        }
        mEmitters.remove(index);
    }

    public void removeAllEmitters() {
        mEmitters.clear();
    }

    public ENG_ParticleAffector addAffector(String name) {
        ENG_ParticleAffector affector =
                ENG_ParticleSystemManager.getSingleton()._createAffector(name, this);
        mAffectors.add(affector);
        return affector;
    }

    public ENG_ParticleAffector getAffector(short index) {
        if (index < 0 || index >= mAffectors.size()) {
            throw new IllegalArgumentException("index out of range size " +
                    mAffectors.size());
        }
        return mAffectors.get(index);
    }

    public short getNumAffectors() {
        return (short) mAffectors.size();
    }

    public void removeAffector(short index) {
        if (index < 0 || index >= mAffectors.size()) {
            throw new IllegalArgumentException("index out of range size " +
                    mAffectors.size());
        }
        mAffectors.remove(index);
    }

    public void removeAllAffectors() {
        mAffectors.clear();
    }

    public void clear() {
        if (mRenderer != null) {
            mRenderer._notifyParticleCleared(mActiveParticles);
        }
        mFreeParticles.addAll(mActiveParticles);
        mActiveParticles.clear();
    }

    public int getNumParticles() {
        return mActiveParticles.size();
    }

    public ENG_Particle createParticle() {
        ENG_Particle p = null;
        if (!mFreeParticles.isEmpty()) {
            p = mFreeParticles.peek();
            mActiveParticles.add(mFreeParticles.removeFirst());
            p._notifyOwner(this);
        }
        return p;
    }

    public ENG_Particle createEmitterParticle(String emitterName) {
        ENG_Particle p = null;
        LinkedList<ENG_ParticleEmitter> fee = findFreeEmittedEmitter(emitterName);
        if (fee != null && !fee.isEmpty()) {
            p = fee.peek();
            p.particleType = ParticleType.Emitter;
            mActiveParticles.add(fee.removeFirst());
            mActiveEmittedEmitters.add((ENG_ParticleEmitter) p);
            p._notifyOwner(this);
        }
        return p;
    }

    public ENG_Particle getParticle(int index) {
        if (index < 0 || index >= mActiveParticles.size()) {
            throw new IllegalArgumentException("index out of range max size is " +
                    mActiveParticles.size());
        }
        return mActiveParticles.get(index);
    }

    public int getParticleQuota() {
        return mPoolSize;
    }

    public void setParticleQuota(int quota) {
        if (quota >= mParticlePool.size()) {
            mPoolSize = quota;
        }
    }

    public int getEmittedEmitterQuota() {
        return mEmittedEmitterPoolSize;
    }

    public void setEmittedEmitterQuota(int quota) {
        int currentSize = 0;
        for (Entry<String, ArrayList<ENG_ParticleEmitter>> p :
                mEmittedEmitterPool.entrySet()) {
            currentSize += p.getValue().size();
        }
        if (quota >= currentSize) {
            mEmittedEmitterPoolSize = quota;
        }
    }

    public void set(ENG_ParticleSystem rhs) {
        // Blank this system's emitters & affectors
        removeAllEmitters();
        removeAllEmittedEmitters();
        removeAllAffectors();

        for (short i = 0; i < rhs.getNumEmitters(); ++i) {
            ENG_ParticleEmitter emitter = rhs.getEmitter(i);
            ENG_ParticleEmitter newEmm = addEmitter(emitter.getType());
            emitter.getStringInterface().copyParametersTo(newEmm.getStringInterface());
        }

        for (short i = 0; i < rhs.getNumAffectors(); ++i) {
            ENG_ParticleAffector emitter = rhs.getAffector(i);
            ENG_ParticleAffector newEmm = addAffector(emitter.getType());
            emitter.getStringInterface().copyParametersTo(newEmm.getStringInterface());
        }

        setParticleQuota(rhs.getParticleQuota());
        setEmittedEmitterQuota(rhs.getEmittedEmitterQuota());
        setMaterialName(rhs.mMaterialName);
        setDefaultDimensions(rhs.mDefaultWidth, rhs.mDefaultHeight);
        mCullIndividual = rhs.mCullIndividual;
        mSorted = rhs.mSorted;
        mLocalSpace = rhs.mLocalSpace;
        mIterationInterval = rhs.mIterationInterval;
        mIterationIntervalSet = rhs.mIterationIntervalSet;
        mNonvisibleTimeout = rhs.mNonvisibleTimeout;
        mNonvisibleTimeoutSet = rhs.mNonvisibleTimeoutSet;
        // last frame visible and time since last visible should be left default

        setRenderer(rhs.getRendererName());
        // Copy settings
        if (mRenderer != null && rhs.getRenderer() != null) {
            rhs.getRenderer().getStringInterface().copyParametersTo(
                    mRenderer.getStringInterface());
        }
    }

    public void _update(float timeElapsed) {
        // Only update if attached to a node
        if (mParentNode == null)
            return;

        float nonvisibleTimeout = mNonvisibleTimeoutSet ?
                mNonvisibleTimeout : msDefaultNonvisibleTimeout;

        if (nonvisibleTimeout > 0) {
            // Check whether it's been more than one frame (update is ahead of
            // camera notification by one frame because of the ordering)
            long frameDiff = ENG_RenderRoot.getRenderRoot().getNextFrameNumber() - mLastVisibleFrame;
            if (frameDiff > 1 || frameDiff < 0) // < 0 if wrap only
            {
                mTimeSinceLastVisible += timeElapsed;
                if (mTimeSinceLastVisible >= nonvisibleTimeout) {
                    // No update
                    return;
                }
            }
        }

        // Scale incoming speed for the rest of the calculation
        timeElapsed *= mSpeedFactor;

        // Init renderer if not done already
        configureRenderer();

        // Initialise emitted emitters list if not done already
        initialiseEmittedEmitters();

        float iterationInterval = mIterationIntervalSet ?
                mIterationInterval : msDefaultIterationInterval;
        if (iterationInterval > 0) {
            mUpdateRemainTime += timeElapsed;

            while (mUpdateRemainTime >= iterationInterval) {
                // Update existing particles
                _expire(iterationInterval);
                _triggerAffectors(iterationInterval);
                _applyMotion(iterationInterval);

                if (mIsEmitting) {
                    // Emit new particles
                    _triggerEmitters(iterationInterval);
                }

                mUpdateRemainTime -= iterationInterval;
            }
        } else {
            // Update existing particles
            _expire(timeElapsed);
            _triggerAffectors(timeElapsed);
            _applyMotion(timeElapsed);

            if (mIsEmitting) {
                // Emit new particles
                _triggerEmitters(timeElapsed);
            }
        }

        if (!mBoundsAutoUpdate && mBoundsUpdateTime > 0.0f)
            mBoundsUpdateTime -= timeElapsed; // count down 
        _updateBounds();
    }

    public Iterator<ENG_Particle> _getIterator() {
        return mActiveParticles.iterator();
    }

    public void setMaterialName(String name) {
        mMaterialName = name;
        if (mIsRendererConfigured) {
            ENG_Material mat = ENG_MaterialManager.getSingleton().getByName(name);
            mRenderer._setMaterial(mat);
        }
    }

    public String getMaterialName() {
        return mMaterialName;
    }

    public void fastForward(float time) {
        fastForward(time, 0.1f);
    }

    public void fastForward(float time, float interval) {
        for (float ftime = 0; ftime < time; ftime += interval) {
            _update(interval);
        }
    }

    public void setSpeedFactor(float speedFactor) {
        mSpeedFactor = speedFactor;
    }

    public float getSpeedFactor() {
        return mSpeedFactor;
    }

    public void setIterationInterval(float interval) {
        mIterationInterval = interval;
        mIterationIntervalSet = true;
    }

    public float getIterationInterval() {
        return mIterationInterval;
    }

    public static void setDefaultIterationInterval(float interval) {
        msDefaultIterationInterval = interval;
    }

    public static float getDefaultIterationInterval() {
        return msDefaultIterationInterval;
    }

    public void setNonVisibleUpdateTimeout(float timeout) {
        mNonvisibleTimeout = timeout;
        mNonvisibleTimeoutSet = true;
    }

    public float getNonVisibleUpdateTimeout() {
        return mNonvisibleTimeout;
    }

    public static void setDefaultNonVisibleUpdateTimeout(float timeout) {
        msDefaultNonvisibleTimeout = timeout;

    }

    public float getDefaultNonVisibleUpdateTimeout() {
        return msDefaultNonvisibleTimeout;
    }

    public void _notifyCurrentCamera(ENG_Camera cam) {
        super._notifyCurrentCamera(cam);

        // Record visible
        if (isVisible()) {
            mLastVisibleFrame = ENG_RenderRoot.getRenderRoot().getNextFrameNumber();
            mTimeSinceLastVisible = 0.0f;

            if (mSorted) {
                _sortParticles(cam);
            }

            if (mRenderer != null) {
                if (!mIsRendererConfigured)
                    configureRenderer();

                mRenderer._notifyCurrentCamera(cam);
            }
        }
    }

    private static class ParticleSystemUpdateValue implements ENG_IControllerValue<ENG_ControllerTypeFloat> {

        private final ENG_ParticleSystem mTarget;

        public ParticleSystemUpdateValue(ENG_ParticleSystem target) {
            mTarget = target;
        }

        @Override
        public void setValue(ENG_ControllerTypeFloat t) {

            mTarget._update(t.get().getValue());
        }

        @Override
        public ENG_ControllerTypeFloat getValue() {

            return null;
        }


    }

    public void _notifyAttached(ENG_Node parent) {
        _notifyAttached(parent, false);
    }

    public void _notifyAttached(ENG_Node parent, boolean isTagPoint) {
        super._notifyAttached(parent, isTagPoint);

        if (mRenderer != null && mIsRendererConfigured) {
            mRenderer._notifyAttached(parent, isTagPoint);
        }

        if (parent != null && mTimeController == null) {
            // Assume visible
            mTimeSinceLastVisible = 0;
            mLastVisibleFrame = ENG_RenderRoot.getRenderRoot().getNextFrameNumber();

            // Create time controller when attached
            ENG_ControllerManager mgr = ENG_ControllerManager.getSingleton();
            ENG_IControllerValue<ENG_ControllerTypeFloat> updValue =
                    new ParticleSystemUpdateValue(this);
            mTimeController = mgr.createFrameTimePassthroughController(updValue);
        } else if (parent == null && mTimeController != null) {
            // Destroy controller
            //ENG_ControllerManager.getSingleton().destroyController(mTimeController);
            mTimeController = null;
        }
    }

    @Override
    public void _updateRenderQueue(ENG_RenderQueue queue) {


        if (mRenderer != null) {
            mRenderer._updateRenderQueue(queue, mActiveParticles, mCullIndividual);
        }
    }

    @Override
    public void getBoundingBox(ENG_AxisAlignedBox ret) {


        ret.set(mAABB);
    }

    @Override
    public float getBoundingRadius() {

        return mBoundingRadius;
    }

    @Override
    public void visitRenderables(Visitor visitor, boolean debugRenderables) {


        if (mRenderer != null) {
            mRenderer.visitRenderables(visitor, debugRenderables);
        }
    }

    @Override
    public String getMovableType() {

        return ENG_ParticleSystemFactory.FACTORY_TYPE_NAME;
    }

    public void _notifyParticleResized() {

        if (mRenderer != null) {
            mRenderer._notifyParticleResized();
        }
    }

    public void _notifyParticleRotated() {

        if (mRenderer != null) {
            mRenderer._notifyParticleRotated();
        }
    }

    public void setDefaultDimensions(float width, float height) {
        mDefaultWidth = width;
        mDefaultHeight = height;
        if (mRenderer != null) {
            mRenderer._notifyDefaultDimensions(width, height);
        }
    }

    public void setDefaultWidth(float width) {
        mDefaultWidth = width;
        if (mRenderer != null) {
            mRenderer._notifyDefaultDimensions(mDefaultWidth, mDefaultHeight);
        }
    }

    public float getDefaultWidth() {
        return mDefaultWidth;
    }

    public void setDefaultHeight(float height) {
        mDefaultHeight = height;
        if (mRenderer != null) {
            mRenderer._notifyDefaultDimensions(mDefaultWidth, mDefaultHeight);
        }
    }

    public float getDefaultHeight() {
        return mDefaultHeight;

    }

    public boolean getCullIndividually() {
        return mCullIndividual;
    }

    public void setCullIndividually(boolean b) {
        mCullIndividual = b;
    }

    public String getResourceGroupName() {
        return "";
    }

    public String getOrigin() {
        return mOrigin;
    }

    public void setOrigin(String origin) {
        mOrigin = origin;
    }

    public void setRenderQueueGroup(byte queueID) {
        super.setRenderQueueGroup(queueID);
        if (mRenderer != null) {
            mRenderer.setRenderQueueGroup(queueID);
        }
    }

    public void setSortingEnabled(boolean enabled) {
        mSorted = enabled;
    }

    public boolean getSortingEnabled() {
        return mSorted;
    }

    public void setBounds(ENG_AxisAlignedBox aabb) {
        mAABB.set(aabb);
        mBoundingRadius = ENG_Math.boundingRadiusFromAABB(aabb);
    }

    public void setBoundsAutoUpdated(boolean autoUpdate) {
        setBoundsAutoUpdated(autoUpdate, 0.0f);
    }

    public void setBoundsAutoUpdated(boolean autoUpdate, float stopIn) {
        mBoundsAutoUpdate = autoUpdate;
        mBoundsUpdateTime = stopIn;
    }

    void setKeepParticlesInLocalSpace(boolean keepLocal) {
        mLocalSpace = keepLocal;
    }

    public boolean getKeepParticlesInLocalSpace() {
        return mLocalSpace;
    }

    private final ENG_Vector4D min = new ENG_Vector4D();
    private final ENG_Vector4D max = new ENG_Vector4D();
    private final ENG_Vector4D halfScale = new ENG_Vector4D();
    private final ENG_Vector4D defaultPadding = new ENG_Vector4D();
    private final ENG_Vector4D padding = new ENG_Vector4D();
    private final ENG_Vector4D boundsTemp = new ENG_Vector4D();
    private final ENG_Matrix4 boundsMat = new ENG_Matrix4();
    private final ENG_AxisAlignedBox newAABB = new ENG_AxisAlignedBox();

    public void _updateBounds() {
        if (mParentNode != null && (mBoundsAutoUpdate || mBoundsUpdateTime > 0.0f)) {
            if (mActiveParticles.isEmpty()) {
                if (mBoundsAutoUpdate) {
                    mAABB.setNull();
                }
            } else {
                if (!mBoundsAutoUpdate && mWorldAABB.isFinite()) {
                    mWorldAABB.getMin(min);
                    mWorldAABB.getMax(max);
                } else {
                    min.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
                            Float.POSITIVE_INFINITY);
                    max.set(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY,
                            Float.NEGATIVE_INFINITY);
                }

                halfScale.set(0.5f, 0.5f, 0.5f);
                halfScale.mul(Math.max(mDefaultHeight, mDefaultWidth), defaultPadding);

                for (ENG_Particle p : mActiveParticles) {
                    if (p.mOwnDimensions) {
                        halfScale.mul(Math.max(p.mWidth, p.mHeight), padding);
                        p.position.sub(padding, boundsTemp);
                        min.makeFloor(boundsTemp);
                        p.position.add(padding, boundsTemp);
                        max.makeCeil(boundsTemp);
                    } else {
                        p.position.sub(defaultPadding, boundsTemp);
                        min.makeFloor(boundsTemp);
                        p.position.add(defaultPadding, boundsTemp);
                        max.makeCeil(boundsTemp);
                    }
                }

                mWorldAABB.setExtents(min, max);
            }

            if (mLocalSpace) {
                mAABB.merge(mWorldAABB);
            } else {
                mParentNode._getFullTransform(boundsMat);
                boundsMat.invertAffine();
                newAABB.set(mWorldAABB);
                newAABB.transformAffine(boundsMat);
                mAABB.merge(newAABB);
            }
            mParentNode.needUpdate();
        }
    }

    public void setEmitting(boolean b) {
        mIsEmitting = b;
    }

    public boolean getEmitting() {
        return mIsEmitting;
    }

    public int getTypeFlags() {
        return ENG_SceneManager.FX_TYPE_MASK;
    }

    /**
     * Internal method used to expire dead particles.
     */
    protected void _expire(float timeElapsed) {
        for (Iterator<ENG_Particle> it = mActiveParticles.iterator(); it.hasNext(); ) {
            ENG_Particle particle = it.next();

            if (particle.timeToLive < timeElapsed) {
                mRenderer._notifyParticleExpired(particle);
                if (particle.particleType == ParticleType.Visual) {
                    mFreeParticles.add(particle);
                    it.remove();
                } else {
                    ENG_ParticleEmitter emitter = (ENG_ParticleEmitter) particle;
                    LinkedList<ENG_ParticleEmitter> fee =
                            findFreeEmittedEmitter(emitter.getName());
                    fee.add(emitter);
                    // Also erase from mActiveEmittedEmitters
                    removeFromActiveEmittedEmitters(emitter);
                    it.remove();
                }
            } else {
                particle.timeToLive -= timeElapsed;
            }
        }
    }

    private final ArrayList<ENG_Integer> requested = new ArrayList<>();

    /**
     * Spawn new particles based on free quota and emitter requirements.
     */
    protected void _triggerEmitters(float timeElapsed) {
        int emissionAllowed = mFreeParticles.size();
        int emitterCount = mEmitters.size();
        requested.clear();
        if (requested.size() != mEmitters.size()) {
            requested.ensureCapacity(mEmitters.size());
        }

        int totalRequested = 0;
        int i = 0;
        for (Iterator<ENG_ParticleEmitter> it = mEmitters.iterator();
             it.hasNext(); ++i) {
            ENG_ParticleEmitter emitter = it.next();

            if (!emitter.isEmitted()) {
                short s = emitter._getEmissionCount(timeElapsed);
                requested.add(new ENG_Integer(s));
                totalRequested += s;
            }
        }

        for (ENG_ParticleEmitter pe : mActiveEmittedEmitters) {
            totalRequested += pe._getEmissionCount(timeElapsed);
        }

        // Check if the quota will be exceeded, if so reduce demand
        float ratio = 1.0f;
        if (totalRequested > emissionAllowed) {
            // Apportion down requested values to allotted values
            ratio = (float) emissionAllowed / (float) totalRequested;
            for (i = 0; i < emitterCount; ++i) {
                ENG_Integer integer = requested.get(i);
                integer.setValue((int) (integer.getValue() * ratio));
            }
        }

        i = 0;
        for (ENG_ParticleEmitter pe : mEmitters) {
            if (!pe.isEmitted()) {
                _executeTriggerEmitters(pe, requested.get(i++).getValue(),
                        timeElapsed);
            }
        }

        for (ENG_ParticleEmitter pe : mActiveEmittedEmitters) {
            _executeTriggerEmitters(pe,
                    (int) (pe._getEmissionCount(timeElapsed) * ratio),
                    timeElapsed);
        }
    }

    private final ENG_Quaternion trigEmQuat = new ENG_Quaternion();
    private final ENG_Vector4D trigEmTemp = new ENG_Vector4D();
    private final ENG_Vector4D trigEmTemp2 = new ENG_Vector4D();

    /**
     * Helper function that actually performs the emission of particles
     */
    protected void _executeTriggerEmitters(ENG_ParticleEmitter emitter,
                                           int requested, float timeElapsed) {
        float timePoint = 0.0f;

        if (requested == 0) {
            return;
        }

        float timeInc = timeElapsed / requested;

        for (int j = 0; j < requested; ++j) {
            ENG_Particle p;
            String emitterName = emitter.getEmittedEmitter();
            if (emitterName.isEmpty()) {
                p = createParticle();
            } else {
                p = createEmitterParticle(emitterName);
            }

            if (p == null) {
                return;
            }

            emitter._initParticle(p);

            if (!mLocalSpace) {
                /*p.position  =
					(mParentNode._getDerivedOrientation() *
					(mParentNode._getDerivedScale() * p.position))
					+ mParentNode._getDerivedPosition();
    			 * */
                mParentNode._getDerivedOrientation(trigEmQuat);
                mParentNode._getDerivedScale(trigEmTemp);
                trigEmTemp.mulInPlace(p.position);
                trigEmQuat.mul(trigEmTemp, trigEmTemp2);
                mParentNode._getDerivedPosition(trigEmTemp);
                trigEmTemp2.addInPlace(trigEmTemp);
                p.position.set(trigEmTemp2);
    			
    			
    			/*
    			 * p.direction = 
					(mParentNode._getDerivedOrientation() * p.direction);*/

                trigEmQuat.mul(p.direction, trigEmTemp);
                p.direction.set(trigEmTemp);
            }

            p.direction.mul(timePoint, trigEmTemp);
            p.position.addInPlace(trigEmTemp);

            for (ENG_ParticleAffector af : mAffectors) {
                af._initParticle(p);
            }

            // Increment time fragment
            timePoint += timeInc;

            if (p.particleType == ParticleType.Emitter) {
                ENG_ParticleEmitter em = (ENG_ParticleEmitter) p;
                em.setPosition(p.position);
            }

            // Notify renderer
            mRenderer._notifyParticleEmitted(p);
        }
    }

    /**
     * Updates existing particle based on their momentum.
     */
    protected void _applyMotion(float timeElapsed) {
        for (ENG_Particle p : mActiveParticles) {
            p.direction.mul(timeElapsed, trigEmTemp);
            p.position.addInPlace(trigEmTemp);

            if (p.particleType == ParticleType.Emitter) {
                ENG_ParticleEmitter em = (ENG_ParticleEmitter) p;
                em.setPosition(p.position);
            }
        }

        // Notify renderer
        mRenderer._notifyParticleMoved(mActiveParticles);
    }

    /**
     * Applies the effects of affectors.
     */
    protected void _triggerAffectors(float timeElapsed) {
        for (ENG_ParticleAffector af : mAffectors) {
            af._affectParticles(this, timeElapsed);
        }
    }

    private final ENG_Vector4D sortTemp = new ENG_Vector4D();
    private final ENG_Vector4D sortTemp2 = new ENG_Vector4D();
    private final ENG_Quaternion sortQuat = new ENG_Quaternion();

    /**
     * Sort the particles in the system
     **/
    protected void _sortParticles(ENG_Camera cam) {
        if (mRenderer != null) {
            SortMode sortMode = mRenderer._getSortMode();

            if (sortMode == SortMode.SM_DIRECTION) {
                cam.getDerivedDirection(sortTemp);
                if (mLocalSpace) {
                    mParentNode._getDerivedOrientation(sortQuat);
                    sortQuat.unitInverse();
                    sortQuat.mul(sortTemp, sortTemp2);
                    sortTemp.set(sortTemp2);
                }

                sortTemp.invert();
                sortByDirectionFunctor.setSortDir(sortTemp);
                Collections.sort(mActiveParticles, sortByDirectionFunctor);
            } else if (sortMode == SortMode.SM_DISTANCE) {
                cam.getDerivedPosition(sortTemp);
                if (mLocalSpace) {
                    mParentNode._getDerivedOrientation(sortQuat);
                    sortQuat.unitInverse();
                    mParentNode._getDerivedPosition(sortTemp2);
                    sortTemp.subInPlace(sortTemp2);
                    mParentNode._getDerivedScale(sortTemp2);
                    sortTemp.divInPlace(sortTemp2);
                    sortQuat.mul(sortTemp, sortTemp2);
                    sortTemp.set(sortTemp2);
                }
                sortByDistanceFunctor.setSortPos(sortTemp);
                Collections.sort(mActiveParticles, sortByDistanceFunctor);
            }
        }
    }

    /**
     * Resize the internal pool of particles.
     */
    protected void increasePool(int size) {
        int oldSize = mParticlePool.size();

        mParticlePool.ensureCapacity(size);

        for (int i = oldSize; i < size; ++i) {
            mParticlePool.add(new ENG_Particle());
        }

        if (mIsRendererConfigured) {
            createVisualParticles(oldSize, size);
        }
    }

    /**
     * Resize the internal pool of emitted emitters.
     *
     * @remarks The pool consists of multiple vectors containing pointers to particle emitters. Increasing the
     * pool with size implies that the vectors are equally increased. The quota of emitted emitters is
     * defined on a particle system level and not on a particle emitter level. This is to prevent that
     * the number of created emitters becomes too high; the quota is shared amongst the emitted emitters.
     */
    protected void increaseEmittedEmitterPool(int size) {
        int maxNumberOfEmitters = size / mEmittedEmitterPool.size();
        for (Entry<String, ArrayList<ENG_ParticleEmitter>> entry :
                mEmittedEmitterPool.entrySet()) {
            String name = entry.getKey();
            ArrayList<ENG_ParticleEmitter> e = entry.getValue();

            for (ENG_ParticleEmitter emitter : mEmitters) {
                if (emitter != null && !name.isEmpty() &&
                        name.equals(emitter.getName())) {
                    int oldSize = e.size();
                    for (int t = oldSize; t < maxNumberOfEmitters; ++t) {
                        ENG_ParticleEmitter clonedEmitter =
                                ENG_ParticleSystemManager.getSingleton()
                                        ._createEmitter(emitter.getType(), this);
                        emitter.getStringInterface().copyParametersTo(
                                clonedEmitter.getStringInterface());
                        clonedEmitter.setEmitted(emitter.isEmitted());

                        if (clonedEmitter.getDuration() > 0.0f &&
                                (clonedEmitter.getRepeatDelay() > 0.0f ||
                                        clonedEmitter.getMinRepeatDelay() > 0.0f ||
                                        clonedEmitter.getMinRepeatDelay() > 0.0f)) { // Duplicate comparison clearly a bug but shit is deprecated. Won't fix!
                            clonedEmitter.setEnabled(false);
                        }

                        e.add(clonedEmitter);
                    }
                }
            }
        }
    }

    /**
     * Internal method for initialising string interface.
     */
    protected void initParameters() {
        if (getStringInterface().createParamDictionary("ParticleSystem")) {
            ENG_ParamDictionary dict = getStringInterface().getParamDictionary();

            dict.addParameter(new ENG_ParameterDef("quota",
                            "The maximum number of particle allowed at once in this system.",
                            ParameterType.PT_UNSIGNED_INT),
                    msQuotaCmd);

            dict.addParameter(new ENG_ParameterDef("emit_emitter_quota",
                            "The maximum number of emitters to be emitted at once in this system.",
                            ParameterType.PT_UNSIGNED_INT),
                    msEmittedEmitterQuotaCmd);

            dict.addParameter(new ENG_ParameterDef("material",
                            "The name of the material to be used to render all particles in this system.",
                            ParameterType.PT_STRING),
                    msMaterialCmd);

            dict.addParameter(new ENG_ParameterDef("particle_width",
                            "The width of particles in world units.",
                            ParameterType.PT_REAL),
                    msWidthCmd);

            dict.addParameter(new ENG_ParameterDef("particle_height",
                            "The height of particles in world units.",
                            ParameterType.PT_REAL),
                    msHeightCmd);

            dict.addParameter(new ENG_ParameterDef("cull_each",
                            "If true, each particle is culled in it's own right. If false, the entire system is culled as a whole.",
                            ParameterType.PT_BOOL),
                    msCullCmd);

            dict.addParameter(new ENG_ParameterDef("renderer",
                            "Sets the particle system renderer to use (default 'billboard').",
                            ParameterType.PT_STRING),
                    msRendererCmd);

            dict.addParameter(new ENG_ParameterDef("sorted",
                            "Sets whether particles should be sorted relative to the camera. ",
                            ParameterType.PT_BOOL),
                    msSortedCmd);

            dict.addParameter(new ENG_ParameterDef("local_space",
                            "Sets whether particles should be kept in local space rather than " +
                                    "emitted into world space. ",
                            ParameterType.PT_BOOL),
                    msLocalSpaceCmd);

            dict.addParameter(new ENG_ParameterDef("iteration_interval",
                            "Sets a fixed update interval for the system, or 0 for the frame rate. ",
                            ParameterType.PT_REAL),
                    msIterationIntervalCmd);

            dict.addParameter(new ENG_ParameterDef("nonvisible_update_timeout",
                            "Sets a timeout on updates to the system if the system is not visible " +
                                    "for the given number of seconds (0 to always update)",
                            ParameterType.PT_REAL),
                    msNonvisibleTimeoutCmd);

        }
    }

    /**
     * Internal method to configure the renderer.
     */
    protected void configureRenderer() {
        // Actual allocate particles
        int currSize = mParticlePool.size();
        int size = mPoolSize;
        if (currSize < size) {
            increasePool(size);

            for (int i = currSize; i < size; ++i) {
                mFreeParticles.add(mParticlePool.get(i));
            }

            if (mRenderer != null && mIsRendererConfigured) {
                mRenderer._notifyParticleQuota(size);
            }
        }

        if (mRenderer != null && !mIsRendererConfigured) {
            mRenderer._notifyParticleQuota(mParticlePool.size());
            mRenderer._notifyAttached(mParentNode, mParentIsTagPoint);
            mRenderer._notifyDefaultDimensions(mDefaultWidth, mDefaultHeight);
            createVisualParticles(0, mParticlePool.size());
            ENG_Material mat =
                    ENG_MaterialManager.getSingleton().getByName(mMaterialName);
            mRenderer._setMaterial(mat);
            if (mRenderQueueIDSet) {
                mRenderer.setRenderQueueGroup(mRenderQueueID);
            }
            mRenderer.setKeepParticlesInLocalSpace(mLocalSpace);
            mIsRendererConfigured = true;
        }
    }

    /// Internal method for creating ParticleVisualData instances for the pool
    protected void createVisualParticles(int poolstart, int poolend) {
        for (int i = poolstart; i < poolend; ++i) {
            mParticlePool.get(i)._notifyVisualData(mRenderer._createVisualData());
        }
    }

    /// Internal method for destroying ParticleVisualData instances for the pool
    protected void destroyVisualParticles(int poolstart, int poolend) {
        for (int i = poolstart; i < poolend; ++i) {
            ENG_Particle particle = mParticlePool.get(i);
            mRenderer._destroyVisualData(particle);
            particle._notifyVisualData(null);
        }
    }

    /**
     * Create a pool of emitted emitters and assign them to the free emitter list.
     *
     * @remarks The emitters in the pool are grouped by name. This name is the name of the base emitter in the
     * main list with particle emitters, which forms the template of the created emitted emitters.
     */
    protected void initialiseEmittedEmitters() {
        // Initialise the pool if needed
        int currSize = 0;
        if (mEmittedEmitterPool.isEmpty()) {
            if (mEmittedEmitterPoolInitialised) {
                // It was already initialised, but apparently no emitted emitters were used
                return;
            } else {
                initialiseEmittedEmitterPool();
            }
        } else {

            for (Entry<String, ArrayList<ENG_ParticleEmitter>> p :
                    mEmittedEmitterPool.entrySet()) {
                currSize += p.getValue().size();
            }
        }

        int size = mEmittedEmitterPoolSize;
        if (currSize < size && !mEmittedEmitterPool.isEmpty()) {
            // Increase the pool. Equally distribute over all vectors in the map
            increaseEmittedEmitterPool(size);

            // Add new items to the free list
            addFreeEmittedEmitters();
        }
    }

    /**
     * Determine which emitters in the Particle Systems main emitter become a template for creating an
     * pool of emitters that can be emitted.
     */
    protected void initialiseEmittedEmitterPool() {
        if (mEmittedEmitterPoolInitialised) {
            return;
        }

        for (ENG_ParticleEmitter emitter : mEmitters) {
            if (emitter != null && !emitter.getEmittedEmitter().isEmpty()) {
                mEmittedEmitterPool.put(emitter.getEmittedEmitter(),
                        new ArrayList<>());
            }

            for (ENG_ParticleEmitter innerEmitter : mEmitters) {
                if (emitter != null && innerEmitter != null &&
                        !emitter.getEmittedEmitter().isEmpty() &&
                        emitter.getEmittedEmitter().equals(
                                innerEmitter.getEmittedEmitter())) {
                    emitter.setEmitted(true);
                    break;
                } else {
                    emitter.setEmitted(false);
                }
            }
        }
        mEmittedEmitterPoolInitialised = true;
    }

    /**
     * Add  emitters from the pool to the free emitted emitter queue.
     */
    protected void addFreeEmittedEmitters() {
        if (mEmittedEmitterPool.isEmpty()) {
            return;
        }

        for (Entry<String, ArrayList<ENG_ParticleEmitter>> emitter :
                mEmittedEmitterPool.entrySet()) {
            String name = emitter.getKey();
            ArrayList<ENG_ParticleEmitter> emittedEmitter = emitter.getValue();
            LinkedList<ENG_ParticleEmitter> fee = findFreeEmittedEmitter(name);

            if (fee == null) {
                mFreeEmittedEmitters.put(name, new LinkedList<>());
                fee = findFreeEmittedEmitter(name);
            }

            if (fee == null) {
                return;
            }

            fee.addAll(emittedEmitter);
        }
    }

    /**
     * Removes all emitted emitters from this system.
     */
    protected void removeAllEmittedEmitters() {
        // Dont leave any references behind
        mEmittedEmitterPool.clear();
        mFreeEmittedEmitters.clear();
        mActiveEmittedEmitters.clear();
    }

    /**
     * Find the list with free emitted emitters.
     *
     * @param name The name that identifies the list with free emitted emitters.
     */
    protected LinkedList<ENG_ParticleEmitter> findFreeEmittedEmitter(String name) {
        return mFreeEmittedEmitters.get(name);
    }

    /**
     * Removes an emitter from the active emitted emitter list.
     *
     * @param emitter Pointer to a particle emitter.
     * @remarks The emitter will not be destroyed!
     */
    protected void removeFromActiveEmittedEmitters(ENG_ParticleEmitter emitter) {
        if (emitter == null) {
            throw new IllegalArgumentException("emitter is null");
        }
        mActiveEmittedEmitters.remove(emitter);
    }

    /**
     * Moves all emitted emitters from the active list to the free list
     *
     * @remarks The active emitted emitter list will not be cleared and still keeps references to the emitters!
     */
    protected void addActiveEmittedEmittersToFreeList() {
        for (ENG_ParticleEmitter e : mActiveEmittedEmitters) {
            LinkedList<ENG_ParticleEmitter> fee = findFreeEmittedEmitter(e.getName());
            if (fee != null) {
                fee.add(e);
            }
        }
    }

    /**
     * This function clears all data structures that are used in combination with emitted emitters and
     * sets the flag to indicate that the emitted emitter pool must be initialised again.
     *
     * @remarks This function should be called if new emitters are added to a ParticleSystem or deleted from a
     * ParticleSystem. The emitted emitter data structures become out of sync and need to be build up
     * again. The data structures are not reorganised in this function, but by setting a flag,
     * they are rebuild in the regular process flow.
     */
    protected void _notifyReorganiseEmittedEmitterData() {
        removeAllEmittedEmitters();
        mEmittedEmitterPoolInitialised = false;
    }

}
