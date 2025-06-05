/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.basictypes.ENG_Boolean;
import headwayent.hotshotengine.renderer.ENG_CompositionTargetPass.InputMode;
import headwayent.hotshotengine.renderer.ENG_CompositorInstance.RenderSystemOpPair;
import headwayent.hotshotengine.renderer.ENG_CompositorInstance.TargetOperation;
import headwayent.hotshotengine.renderer.ENG_RenderQueue.RenderQueueGroupID;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import com.badlogic.gdx.utils.Logger;

public class ENG_CompositorChain implements ENG_RenderTargetListener {

    public static final int LAST = Integer.MAX_VALUE - 1;
    public static int BEST = 0;

    public static class RQListener implements ENG_RenderQueueListener {

        private TargetOperation mOperation;
        private ENG_SceneManager mSceneManager;
        private ENG_RenderSystem mRenderSystem;
        private ENG_Viewport mViewport;
        private Iterator<RenderSystemOpPair> currentOp;

        @Override
        public void preRenderQueues() {
            

        }

        @Override
        public void postRenderQueues() {
            

        }

        @Override
        public void renderQueueStarted(byte queueGroupId, String invocation,
                                       ENG_Boolean skipThisInvocation) {
            
            // Skip when not matching viewport
            // shadows update is nested within main viewport update
            if (mSceneManager.getCurrentViewport() != mViewport) {
                return;
            }

            flushUpTo(queueGroupId);
            /// If noone wants to render this queue, skip it
            /// Don't skip the OVERLAY queue because that's handled seperately
            if (!mOperation.renderQueues.get(queueGroupId) &&
                    queueGroupId != RenderQueueGroupID.RENDER_QUEUE_OVERLAY.getID()) {
                skipThisInvocation.setValue(true);
            }
        }

        @Override
        public void renderQueueEnded(byte queueGroupId, String invocation,
                                     ENG_Boolean repeatThisInvocation) {
            

        }

        public void setOperation(TargetOperation op,
                                 ENG_SceneManager sm, ENG_RenderSystem rs) {
            mOperation = op;
            mSceneManager = sm;
            mRenderSystem = rs;
            currentOp = op.renderSystemOperations.iterator();

        }

        public void flushUpTo(byte id) {
            while (currentOp.hasNext()) {
                RenderSystemOpPair pair = currentOp.next();
                if (pair.pos <= id) {
                    pair.op.execute(mSceneManager, mRenderSystem);
                }
            }
        }

        public void notifyViewport(ENG_Viewport vp) {
            
            mViewport = vp;
        }

    }

    /// Viewport affected by this CompositorChain
    protected ENG_Viewport mViewport;

    /**
     * Plainly renders the scene; implicit first compositor in the chain.
     */
    protected ENG_CompositorInstance mOriginalScene;

    /// Postfilter instances in this chain
    protected final ArrayList<ENG_CompositorInstance> mInstances =
            new ArrayList<>();

    /// State needs recompile
    protected boolean mDirty = true;
    /// Any compositors enabled?
    protected boolean mAnyCompositorsEnabled;

    /// Compiled state (updated with _compile)
    protected final ArrayList<TargetOperation> mCompiledState =
            new ArrayList<>();
    protected TargetOperation mOutputOperation;
    /// Render System operations queued by last compile, these are created by this
    /// instance thus managed and deleted by it. The list is cleared with
    /// clearCompilationState()
    //typedef vector<CompositorInstance::RenderSystemOperation*>::type RenderSystemOperations;
    protected final ArrayList<ENG_RenderSystemOperation> mRenderSystemOperations =
            new ArrayList<>();

    protected final RQListener mOurListener = new RQListener();
    /// Old viewport settings
    protected int mOldClearEveryFrameBuffers;
    /// Store old scene visibility mask
    protected int mOldVisibilityMask;
    /// Store old find visible objects
    protected boolean mOldFindVisibleObjects;
    /// Store old camera LOD bias      
    protected float mOldLodBias;
    ///	Store old viewport material scheme
    protected String mOldMaterialScheme;
    /// Store old shadows enabled flag
    protected boolean mOldShadowsEnabled;

    public ENG_CompositorChain(ENG_Viewport vp) {

        
        mOldClearEveryFrameBuffers = vp.getClearBuffers();
        mViewport = vp;
        if (vp == null) {
            throw new IllegalArgumentException("vp == null");
        }
    }

    public ENG_CompositorInstance getNextInstance(ENG_CompositorInstance curr) {
        return getNextInstance(curr, true);
    }

    public ENG_CompositorInstance getNextInstance(ENG_CompositorInstance curr,
                                                  boolean activeOnly) {
        boolean found = false;
        for (int i = 0; i < mInstances.size(); ++i) {
            if (found) {
                if (mInstances.get(i).getEnabled() || !activeOnly) {
                    return mInstances.get(i);
                }
            } else {
                if (mInstances.get(i) == curr) {
                    found = true;
                }
            }
        }
        return null;
    }

    public ENG_CompositorInstance getPreviousInstance(ENG_CompositorInstance curr) {
        return getPreviousInstance(curr, true);
    }

    public ENG_CompositorInstance getPreviousInstance(ENG_CompositorInstance curr,
                                                      boolean activeOnly) {
        boolean found = false;
        for (int i = mInstances.size() - 1; i >= 0; --i) {
            if (found) {
                if (mInstances.get(i).getEnabled() || !activeOnly) {
                    return mInstances.get(i);
                }
            } else {
                if (mInstances.get(i) == curr) {
                    found = true;
                }
            }
        }
        return null;
    }

    public void setCompositorEnabled(int position, boolean state) {
        ENG_CompositorInstance inst = getCompositor(position);

        if (!state && inst.getEnabled()) {
            // If we're disabling a 'middle' compositor in a chain, we have to be
            // careful about textures which might have been shared by non-adjacent
            // instances which have now become adjacent.
            ENG_CompositorInstance nextInstance = getNextInstance(inst, true);
            if (nextInstance != null) {
                Iterator<ENG_CompositionTargetPass> passIterator =
                        nextInstance.getTechnique().getTargetPassIterator();
                while (passIterator.hasNext()) {
                    ENG_CompositionTargetPass tp = passIterator.next();
                    if (tp.getInputMode() == InputMode.IM_PREVIOUS) {
                        if (nextInstance.getTechnique().getTextureDefinition(tp.getOutputName()).pooled) {
                            // recreate
                            nextInstance.freeResources(false, true);
                            nextInstance.createResources(false);
                        }
                    }
                }
            }
        }
        inst.setEnabled(state);
    }

    public void _compile() {
        clearCompiledState();

        boolean compositorsEnabled = false;

        // force default scheme so materials for compositor quads will determined correctly
        ENG_MaterialManager matMgr = ENG_MaterialManager.getSingleton();
        String prevMaterialScheme = matMgr.getActiveScheme();
        matMgr.setActiveScheme(ENG_MaterialManager.DEFAULT_SCHEME_NAME);

        /// Set previous CompositorInstance for each compositor in the list
        ENG_CompositorInstance lastComposition = mOriginalScene;
        mOriginalScene.mPreviousInstance = null;
        ENG_CompositionPass pass =
                mOriginalScene.getTechnique().getOutputTargetPass().getPass(0);
        pass.setClearBuffers(mViewport.getClearBuffers());
        pass.setClearColour(mViewport.getBackgroundColour());

        for (ENG_CompositorInstance inst : mInstances) {
            if (inst.getEnabled()) {
                compositorsEnabled = true;
                inst.mPreviousInstance = lastComposition;
                lastComposition = inst;
            }
        }

        /// Compile misc targets
        lastComposition._compileTargetOperations(mCompiledState);

        /// Final target viewport (0)
        mOutputOperation.renderSystemOperations.clear();
        lastComposition._compileOutputOperation(mOutputOperation);

        // Deal with viewport settings
        if (compositorsEnabled != mAnyCompositorsEnabled) {
            mAnyCompositorsEnabled = compositorsEnabled;
            if (mAnyCompositorsEnabled) {
                // Save old viewport clearing options
                mOldClearEveryFrameBuffers = mViewport.getClearBuffers();
                // Don't clear anything every frame since we have our own clear ops
                mViewport.setClearEveryFrame(false);
            } else {
                // Reset clearing options
                mViewport.setClearEveryFrame(mOldClearEveryFrameBuffers > 0,
                        mOldClearEveryFrameBuffers);
            }
        }

        // restore material scheme
        matMgr.setActiveScheme(prevMaterialScheme);


        mDirty = false;
    }

    @Override
    public void preRenderTargetUpdate(ENG_RenderTargetEvent evt) {
        

        if (mDirty) {
            _compile();
        }

        if (!mAnyCompositorsEnabled) {
            return;
        }

        ENG_Camera cam = mViewport.getCamera();
        if (cam == null) {
            return;
        }

        cam.getSceneManager()._setActiveCompositorChain(this);

        for (TargetOperation op : mCompiledState) {
            if (op.onlyInitial && op.hasBeenRendered) {
                continue;
            }
            op.hasBeenRendered = true;
            preTargetOperation(op, op.target.getViewport(0), cam);
            op.target.update();
            postTargetOperation(op, op.target.getViewport(0), cam);
        }
    }

    @Override
    public void postRenderTargetUpdate(ENG_RenderTargetEvent evt) {
        

        ENG_Camera cam = mViewport.getCamera();
        if (cam != null) {
            cam.getSceneManager()._setActiveCompositorChain(null);
        }
    }

    @Override
    public void preViewportUpdate(ENG_RenderTargetViewportEvent evt) {
        

        if ((evt.source != mViewport) || (!mAnyCompositorsEnabled)) {
            return;
        }

        // set original scene details from viewport
        ENG_CompositionPass pass = mOriginalScene.getTechnique()
                .getOutputTargetPass().getPass(0);
        ENG_CompositionTargetPass passParent = pass.getParent();
        if (pass.getClearBuffers() != mViewport.getClearBuffers() ||
                pass.getClearColour().notEquals(mViewport.getBackgroundColour()) ||
                passParent.getVisibilityMask() != mViewport.getVisibilityMask() ||
                !Objects.equals(passParent.getMaterialScheme(), mViewport.getMaterialScheme()) ||
                passParent.getShadowsEnabled() != mViewport.getShadowsEnabled()) {
            // recompile if viewport settings are different
            pass.setClearBuffers(mViewport.getClearBuffers());
            pass.setClearColour(mViewport.getBackgroundColour());
            passParent.setVisibilityMask(mViewport.getVisibilityMask());
            passParent.setMaterialScheme(mViewport.getMaterialScheme());
            passParent.setShadowsEnabled(mViewport.getShadowsEnabled());
            _compile();
        }

        ENG_Camera cam = mViewport.getCamera();
        if (cam != null) {
            /// Prepare for output operation
            preTargetOperation(mOutputOperation, mViewport, cam);
        }
    }

    @Override
    public void postViewportUpdate(ENG_RenderTargetViewportEvent evt) {
        
        if ((evt.source != mViewport) || (!mAnyCompositorsEnabled)) {
            return;
        }

        ENG_Camera cam = mViewport.getCamera();
        if (cam != null) {
            /// Prepare for output operation
            postTargetOperation(mOutputOperation, mViewport, cam);
        }
    }

    @Override
    public void viewportAdded(ENG_RenderTargetViewportEvent evt) {
        

    }

    @Override
    public void viewportRemoved(ENG_RenderTargetViewportEvent evt) {
        

        // check this is the viewport we're attached to (multi-viewport targets)
        if (evt.source == mViewport) {
            // this chain is now orphaned
            // can't delete it since held from outside, but release all resources being used
            destroyResources();
        }
    }

    public void _queuedOperation(ENG_RenderSystemOperation op) {
        
        mRenderSystemOperations.add(op);
    }

    public ENG_CompositorInstance getCompositor(int index) {
        return mInstances.get(index);
    }

    public ENG_CompositorInstance getCompositor(String name) {
        for (ENG_CompositorInstance comp : mInstances) {
            if (comp.getCompositor().getName().equals(name)) {
                return comp;
            }
        }
        return null;
    }

    public void _markDirty() {
        
        mDirty = true;
    }

    public ENG_Viewport getViewport() {
        return mViewport;
    }

    public void _notifyViewport(ENG_Viewport vp) {
        mViewport = vp;
    }

    public Iterator<ENG_CompositorInstance> getCompositors() {
        
        return mInstances.iterator();
    }

    private void clearCompiledState() {
        mRenderSystemOperations.clear();

        /// Clear compiled state
        mCompiledState.clear();
        mOutputOperation = new TargetOperation(null);
    }

    public void preTargetOperation(
            TargetOperation op, ENG_Viewport vp, ENG_Camera cam) {
        ENG_SceneManager sm = cam.getSceneManager();

        mOurListener.setOperation(op, sm, sm.getDestinationRenderSystem());
        mOurListener.notifyViewport(vp);
        /// Register it
        sm.addRenderQueueListener(mOurListener);
        /// Set visiblity mask
        mOldVisibilityMask = sm.getVisibilityMask();
        sm.setVisibilityMask(op.visibilityMask);
        /// Set whether we find visibles
        mOldFindVisibleObjects = sm.getFindVisibleObjects();
        sm.setFindVisibleObjects(op.findVisibleObjects);
        /// Set LOD bias level
        mOldLodBias = cam.getLodBias();
        cam.setLodBias(cam.getLodBias() * op.lodBias);
        /// Set material scheme
        mOldMaterialScheme = vp.getMaterialScheme();
        vp.setMaterialScheme(op.materialScheme);
        /// Set shadows enabled
        mOldShadowsEnabled = vp.getShadowsEnabled();
        vp.setShadowsEnabled(op.shadowsEnabled);
        /// XXX TODO
        //vp->setClearEveryFrame( true );
        //vp->setOverlaysEnabled( false );
        //vp->setBackgroundColour( op.clearColour );
    }

    public void postTargetOperation(
            TargetOperation op, ENG_Viewport vp, ENG_Camera cam) {
        ENG_SceneManager sm = cam.getSceneManager();
        /// Unregister our listener
        sm.removeRenderQueueListener(mOurListener);
        /// Restore default scene and camera settings
        sm.setVisibilityMask(mOldVisibilityMask);
        sm.setFindVisibleObjects(mOldFindVisibleObjects);
        cam.setLodBias(mOldLodBias);
        vp.setMaterialScheme(mOldMaterialScheme);
        vp.setShadowsEnabled(mOldShadowsEnabled);
    }

    private void destroyResources() {
        clearCompiledState();

        if (mViewport != null) {
            removeAllCompositors();
            /// Destroy "original scene" compositor instance
            if (mOriginalScene != null) {
                mViewport.getTarget().removeListener(this);
                mOriginalScene = null;
            }
            mViewport = null;
        }
    }

    public void destroy() {
        destroyResources();
    }

    public ENG_CompositorInstance addCompositor(ENG_Compositor filter) {
        return addCompositor(filter, LAST, "");
    }

    public ENG_CompositorInstance addCompositor(
            ENG_Compositor filter, int addPosition, String scheme) {
        if (mOriginalScene == null) {
            mViewport.getTarget().addListener(this);
            ENG_Compositor base =
                    ENG_CompositorManager.getSingleton().load("Ogre/Scene");
            mOriginalScene =
                    new ENG_CompositorInstance(base.getSupportedTechnique(), this);
        }

        ENG_CompositionTechnique tech = filter.getSupportedTechnique(scheme);
        if (tech == null) {
            new Logger("Compositor").info(//ENG_CompositorChain.class.getName(),
                    "CompositorChain: Compositor " + filter.getName() +
                            " has no supported techniques.");
            return null;
        }

        ENG_CompositorInstance t = new ENG_CompositorInstance(tech, this);

        if (addPosition == LAST) {
            addPosition = mInstances.size();
        } else if (addPosition > mInstances.size()) {
            throw new IllegalArgumentException("Index out of bounds: addPosition " +
                    addPosition);
        }
        mInstances.add(addPosition, t);

        mDirty = true;
        mAnyCompositorsEnabled = true;
        return t;
    }

    public void removeCompositor(int index) {
        ENG_CompositorInstance remove = mInstances.remove(index);
        if (remove == null) {
            throw new IllegalArgumentException(index + " is an invalid compositor " +
                    "chain index");
        }
        remove.destroy();
        mDirty = true;
    }

    public int getNumCompositors() {
        return mInstances.size();
    }

    public void removeAllCompositors() {
        
        for (ENG_CompositorInstance inst : mInstances) {
            inst.destroy();
        }
        mInstances.clear();

        mDirty = true;
    }

    public void _removeInstance(ENG_CompositorInstance comp) {
        mInstances.remove(comp);
    }

    public ENG_CompositorInstance _getOriginalSceneCompositor() {
        return mOriginalScene;
    }

}
