/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 4:51 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;
import headwayent.hotshotengine.renderer.ENG_Common.CompareFunction;
import headwayent.hotshotengine.renderer.ENG_Common.FilterOptions;
import headwayent.hotshotengine.renderer.ENG_Common.FilterType;
import headwayent.hotshotengine.renderer.ENG_CompositionPass.InputTex;
import headwayent.hotshotengine.renderer.ENG_CompositionTargetPass.InputMode;
import headwayent.hotshotengine.renderer.ENG_CompositionTechnique.TextureDefinition;
import headwayent.hotshotengine.renderer.ENG_CompositionTechnique.TextureScope;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;
import headwayent.hotshotengine.renderer.ENG_RenderQueue.RenderQueueGroupID;
import headwayent.hotshotengine.renderer.ENG_RenderSystem.StencilOperation;
import headwayent.hotshotengine.renderer.ENG_Texture.TextureType;
import headwayent.hotshotengine.renderer.ENG_Texture.TextureUsage;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.TreeMap;

import com.badlogic.gdx.utils.Logger;

@Deprecated
public class ENG_CompositorInstance {

    public static final byte RENDER_QUEUE_COUNT =
            (byte) (RenderQueueGroupID.RENDER_QUEUE_MAX.getID() + 1);

    public static class Listener {
        /**
         * Notification of when a render target operation involving a material (like
         * rendering a quad) is compiled, so that miscellaneous parameters that are different
         * per Compositor instance can be set up.
         *
         * @param pass_id Pass identifier within Compositor instance, this is specified
         *                by the user by CompositionPass::setIdentifier().
         * @param mat     Material, this may be changed at will and will only affect
         *                the current instance of the Compositor, not the global material
         *                it was cloned from.
         */
        public void notifyMaterialSetup(int pass_id, ENG_Material mat) {

        }

        /**
         * Notification before a render target operation involving a material (like
         * rendering a quad), so that material parameters can be varied.
         *
         * @param pass_id Pass identifier within Compositor instance, this is specified
         *                by the user by CompositionPass::setIdentifier().
         * @param mat     Material, this may be changed at will and will only affect
         *                the current instance of the Compositor, not the global material
         *                it was cloned from.
         */
        public void notifyMaterialRender(int pass_id, ENG_Material mat) {

        }

        /**
         * Notification after resources have been created (or recreated).
         *
         * @param resizeOnly Was the creation because the viewport was resized?
         */
        public void notifyResourcesCreated(boolean forResizeOnly) {

        }
    }

    static class RenderSystemOpPair {
        public final int pos;
        public final ENG_RenderSystemOperation op;

        public RenderSystemOpPair(int pos, ENG_RenderSystemOperation op) {
            this.pos = pos;
            this.op = op;
        }
    }

    public static class TargetOperation {
        /// Target
        public ENG_RenderTarget target;

        /// Current group ID
        public int currentQueueGroupID;

        /// RenderSystem operations to queue into the scene manager, by
        /// uint8
        public final ArrayList<RenderSystemOpPair> renderSystemOperations =
                new ArrayList<>();

        /// Scene visibility mask
        /// If this is 0, the scene is not rendered at all
        public int visibilityMask = 0xFFFFFFFF;

        /// LOD offset. This is multiplied with the camera LOD offset
        /// 1.0 is default, lower means lower detail, higher means higher detail
        public float lodBias = 1.0f;

        /**
         * A set of render queues to either include or exclude certain render queues.
         */
        //   typedef std::bitset<RENDER_QUEUE_COUNT> RenderQueueBitSet;

        /// Which renderqueues to render from scene
        public final BitSet renderQueues = new BitSet(RENDER_QUEUE_COUNT);

        /**
         * @see CompositionTargetPass::mOnlyInitial
         */
        public boolean onlyInitial;
        /**
         * "Has been rendered" flag; used in combination with
         * onlyInitial to determine whether to skip this target operation.
         */
        public boolean hasBeenRendered;
        /**
         * Whether this op needs to find visible scene objects or not
         */
        public boolean findVisibleObjects;
        /**
         * Which material scheme this op will use
         */
        public String materialScheme = ENG_MaterialManager.DEFAULT_SCHEME_NAME;
        /**
         * Whether shadows will be enabled
         */
        public boolean shadowsEnabled = true;

        public TargetOperation() {

        }

        public TargetOperation(ENG_RenderTarget inTarget) {
            target = inTarget;
        }
    }

    /// Compositor of which this is an instance
    private final ENG_Compositor mCompositor;
    /// Composition technique used by this instance
    private ENG_CompositionTechnique mTechnique;
    /// Composition chain of which this instance is part
    private final ENG_CompositorChain mChain;
    /// Is this instance enabled?
    private boolean mEnabled;
    /// Map from name->local texture
    //typedef map<String,TexturePtr>::type LocalTextureMap;
    private final TreeMap<String, ENG_Texture> mLocalTextures =
            new TreeMap<>();
    /// Store a list of MRTs we've created
    //typedef map<String,MultiRenderTarget*>::type LocalMRTMap;
    private final TreeMap<String, ENG_MultiRenderTarget> mLocalMRTs =
            new TreeMap<>();
    //typedef map<CompositionTechnique::TextureDefinition*, TexturePtr>::type ReserveTextureMap;
    /**
     * Textures that are not currently in use, but that we want to keep for now,
     * for example if we switch techniques but want to keep all textures available
     * in case we switch back.
     */
    private final TreeMap<TextureDefinition, ENG_Texture> mReserveTextures =
            new TreeMap<>();

    /// Vector of listeners
    //typedef vector<Listener*>::type Listeners;
    private final ArrayList<Listener> mListeners =
            new ArrayList<>();

    /// Previous instance (set by chain)
    protected ENG_CompositorInstance mPreviousInstance;

    /// The scheme which is being used in this instance
    private String mActiveScheme;

    public static class RSClearOperation extends ENG_RenderSystemOperation {

        /// Which buffers to clear (FrameBufferType)
        public final int buffers;
        /// Colour to clear in case FBT_COLOUR is set
        public final ENG_ColorValue colour = new ENG_ColorValue();
        /// Depth to set in case FBT_DEPTH is set
        public final float depth;
        /// Stencil value to set in case FBT_STENCIL is set
        public final short stencil;

        public RSClearOperation(int buffers, ENG_ColorValue color, float depth,
                                short stencil) {
            this.buffers = buffers;
            this.colour.set(color);
            this.depth = depth;
            this.stencil = stencil;
        }

        @Override
        public void execute(ENG_SceneManager sm, ENG_RenderSystem rs) {
            
            rs.clearFrameBuffer(buffers, colour, depth, stencil);
        }

    }

    public static class RSStencilOperation extends ENG_RenderSystemOperation {

        public final boolean stencilCheck;
        public final CompareFunction func;
        public final int refValue;
        public final int mask;
        public final StencilOperation stencilFailOp;
        public final StencilOperation depthFailOp;
        public final StencilOperation passOp;
        public final boolean twoSidedOperation;

        public RSStencilOperation(boolean stencilCheck, CompareFunction func,
                                  int refValue, int mask, StencilOperation stencilFailOp,
                                  StencilOperation depthFailOp, StencilOperation passOp,
                                  boolean twoSidedOperation) {
            this.stencilCheck = stencilCheck;
            this.func = func;
            this.refValue = refValue;
            this.mask = mask;
            this.stencilFailOp = stencilFailOp;
            this.depthFailOp = depthFailOp;
            this.passOp = passOp;
            this.twoSidedOperation = twoSidedOperation;
        }

        @Override
        public void execute(ENG_SceneManager sm, ENG_RenderSystem rs) {
            
            rs.setStencilCheckEnabled(stencilCheck);
            rs.setStencilBufferParams(func, refValue, mask,
                    stencilFailOp, depthFailOp, passOp, twoSidedOperation);
        }

    }

    public static class RSQuadOperation extends ENG_RenderSystemOperation {

        public final ENG_Material mat;
        public final ENG_Technique technique;
        public final ENG_CompositorInstance instance;
        public final int pass_id;

        public boolean mQuadCornerModified, mQuadFarCorners, mQuadFarCornersViewSpace;
        public float mQuadLeft = -1.0f;
        public float mQuadTop = 1.0f;
        public float mQuadRight = 1.0f;
        public float mQuadBottom = -1.0f;

        public RSQuadOperation(ENG_CompositorInstance inInstance,
                               int inPass_id, ENG_Material inMat) {
            mat = inMat;
            instance = inInstance;
            pass_id = inPass_id;
            mat.load();
            instance._fireNotifyMaterialSetup(pass_id, mat);
            technique = mat.getTechnique((short) 0);
            if (technique == null) {
                throw new NullPointerException("technique is null");
            }
        }

        public void setQuadCorners(float left, float top, float right, float bottom) {
            mQuadLeft = left;
            mQuadTop = top;
            mQuadRight = right;
            mQuadBottom = bottom;
            mQuadCornerModified = true;
        }

        public void setQuadFarCorners(boolean farCorners, boolean farCornersViewSpace) {
            mQuadFarCorners = farCorners;
            mQuadFarCornersViewSpace = farCornersViewSpace;
        }

        private final ENG_Vector4D[] cornerTransforms = new ENG_Vector4D[4];

        @Override
        public void execute(ENG_SceneManager sm, ENG_RenderSystem rs) {
            
            // Fire listener
            instance._fireNotifyMaterialRender(pass_id, mat);

            ENG_Viewport vp = rs._getViewport();
            ENG_Rectangle2D rect =
                    ENG_CompositorManager.getSingleton()._getTexturedRectangle2D();

            if (mQuadCornerModified) {
                // insure positions are using peculiar render system offsets
                float hOffset =
                        rs.getHorizontalTexelOffset() / (0.5f * vp.getActualWidth());
                float vOffset =
                        rs.getVerticalTexelOffset() / (0.5f * vp.getActualHeight());
                rect.setCorners(
                        mQuadLeft + hOffset, mQuadTop - vOffset,
                        mQuadRight + hOffset, mQuadBottom - vOffset);
            }

            if (mQuadFarCorners) {
                ENG_Vector4D[] corners = vp.getCamera().getWorldSpaceCorners();
                if (mQuadFarCornersViewSpace) {
                    if (cornerTransforms == null) {
                        for (int i = 0; i < cornerTransforms.length; ++i) {
                            cornerTransforms[i] = new ENG_Vector4D();
                        }
                    }
                    cornerTransforms[0].set(corners[5]);
                    cornerTransforms[1].set(corners[6]);
                    cornerTransforms[2].set(corners[4]);
                    cornerTransforms[3].set(corners[7]);
                    ENG_Matrix4 viewMat = vp.getCamera().getViewMatrix(true);
                    for (int i = 0; i < 4; ++i) {
                        viewMat.transform(cornerTransforms[i]);
                    }

                    rect.setNormals(
//							viewMat.transform(corners[5]), 
//							viewMat.transform(corners[6]), 
//							viewMat.transform(corners[4]), 
//							viewMat.transform(corners[7])
                            cornerTransforms[0],
                            cornerTransforms[1],
                            cornerTransforms[2],
                            cornerTransforms[3]
                    );
                } else {
                    rect.setNormals(corners[5], corners[6], corners[4], corners[7]);
                }
            }

            Iterator<ENG_Pass> iterator = technique.getPassIterator();
            while (iterator.hasNext()) {
                ENG_Pass pass = iterator.next();
                sm._injectRenderWithPass(
                        pass,
                        rect,
                        false, // don't allow replacement of shadow passes
                        false, null);
            }
        }

    }

    public static class RSSetSchemeOperation extends ENG_RenderSystemOperation {

        public String mPreviousScheme;
        public boolean mPreviousLateResolving;

        public final String mSchemeName;

        public RSSetSchemeOperation(String schemeName) {
            mSchemeName = schemeName;
        }

        @Override
        public void execute(ENG_SceneManager sm, ENG_RenderSystem rs) {
            
            ENG_MaterialManager matMgr = ENG_MaterialManager.getSingleton();
            String mPreviousScheme = matMgr.getActiveScheme();
            matMgr.setActiveScheme(mSchemeName);
            mPreviousLateResolving = sm.isLateMaterialResolving();
            sm.setLateMaterialResolving(true);
        }

        public String getPreviousScheme() {
            return mPreviousScheme;
        }

        public boolean getPreviousLateResolving() {
            return mPreviousLateResolving;
        }

    }

    public static class RSRestoreSchemeOperation extends ENG_RenderSystemOperation {

        public final RSSetSchemeOperation mSetOperation;

        public RSRestoreSchemeOperation(RSSetSchemeOperation setOperation) {
            mSetOperation = setOperation;
        }

        @Override
        public void execute(ENG_SceneManager sm, ENG_RenderSystem rs) {
            
            ENG_MaterialManager.getSingleton().setActiveScheme(
                    mSetOperation.getPreviousScheme());
            sm.setLateMaterialResolving(mSetOperation.getPreviousLateResolving());
        }

    }

    private void collectPasses(TargetOperation finalState,
                               ENG_CompositionTargetPass target) {
        ENG_Pass targetpass;
        ENG_Technique srctech;
        ENG_Material mat = null, srcmat;

        Iterator<ENG_CompositionPass> iterator = target.getPassIterator();
        while (iterator.hasNext()) {
            ENG_CompositionPass pass = iterator.next();
            switch (pass.getType()) {
                case PT_CLEAR:
                    queueRenderSystemOp(finalState, new RSClearOperation(
                            pass.getClearBuffers(),
                            pass.getClearColour(),
                            pass.getClearDepth(),
                            (short) pass.getClearStencil()));
                    break;
                case PT_STENCIL:
                    queueRenderSystemOp(finalState, new RSStencilOperation(
                            pass.getStencilCheck(),
                            pass.getStencilFunc(),
                            pass.getStencilRefValue(),
                            pass.getStencilMask(),
                            pass.getStencilFailOp(),
                            pass.getStencilDepthFailOp(),
                            pass.getStencilPassOp(),
                            pass.getStencilTwoSidedOperation()));
                    break;
                case PT_RENDERSCENE:
                    if (pass.getFirstRenderQueue() < finalState.currentQueueGroupID) {
                        new Logger("Compositor").debug(//ENG_CompositorInstance.class.getName(),
                                "Warning in compilation of Compositor " +
                                        mCompositor.getName() + ": Attempt to render queue " +
                                        pass.getFirstRenderQueue() + " before " +
                                        finalState.currentQueueGroupID);
                    }

                    RSSetSchemeOperation setSchemeOperation = null;
                    if (!pass.getMaterialScheme().isEmpty()) {
                        //Add the triggers that will set the scheme and restore it each frame
                        finalState.currentQueueGroupID = pass.getFirstRenderQueue();
                        setSchemeOperation =
                                new RSSetSchemeOperation(pass.getMaterialScheme());
                        queueRenderSystemOp(finalState, setSchemeOperation);
                    }

                    for (int x = pass.getFirstRenderQueue();
                         x <= pass.getLastRenderQueue(); ++x) {
                        if (x < 0) {
                            throw new ENG_InvalidFieldStateException("x < 0 in pass" +
                                    "getFirstRenderQueue(). x: " + x);
                        }
                        finalState.renderQueues.set(x);
                    }
                    finalState.currentQueueGroupID = pass.getLastRenderQueue() + 1;

                    if (setSchemeOperation != null) {
                        //Restoring the scheme after the queues have been rendered
                        queueRenderSystemOp(finalState,
                                new RSRestoreSchemeOperation(setSchemeOperation));
                    }

                    finalState.findVisibleObjects = true;
                    break;
                case PT_RENDERQUAD: {
                    srcmat = pass.getMaterial();
                    if (srcmat == null) {
                        new Logger("Compositor").info(//ENG_CompositorInstance.class.getName(),
                                "Warning in compilation of Compositor "
                                        + mCompositor.getName() +
                                        ": No material defined for composition pass");
                        break;
                    }
                    srcmat.load();
                    if (srcmat.getNumSupportedTechniques() == 0) {
                        new Logger("Compositor").info(//ENG_CompositorInstance.class.getName(),
                                "Warning in compilation of Compositor "
                                        + mCompositor.getName() + ": material " +
                                        srcmat.getName() + " has no supported techniques");
                        break;
                    }
                    srctech = srcmat.getBestTechnique(new ENG_Short((short) 0), null);
                    /// Create local material
                    ENG_Material localMat = createLocalMaterial(srcmat.getName());
                    /// Copy and adapt passes from source material
                    Iterator<ENG_Pass> passIterator = srctech.getPassIterator();
                    while (passIterator.hasNext()) {
                        ENG_Pass srcpass = passIterator.next();
                        /// Create new target pass
                        targetpass = localMat.getTechnique((short) 0).createPass();
                        targetpass.set(srcpass);
                        if (targetpass.getTextureUnitState(0).getTextureFiltering(
                                FilterType.FT_MIP) != FilterOptions.FO_NONE) {
                            throw new IllegalArgumentException("Invalid filter type " +
                                    "for compositor. Must always be none in gles 2.0 " +
                                    "for npot textures");
                        }
                        /// Set up inputs
                        for (int x = 0; x < pass.getNumInputs(); ++x) {
                            InputTex inp = pass.getInput(x);
                            if (!inp.name.isEmpty()) {
                                if (x < targetpass.getNumTextureUnitStates()) {
                                    targetpass.getTextureUnitState(x).setTextureName(
                                            getSourceForTex(inp.name, inp.mrtIndex));
                                } else {
                                    new Logger("Compositor").info(//ENG_CompositorInstance.class.getName(),
                                            "Warning in compilation of Compositor "
                                                    + mCompositor.getName() +
                                                    ": material " + srcmat.getName() +
                                                    " texture unit "
                                                    + x + " out of bounds");
                                }
                            }
                        }
                    }

                    RSQuadOperation rsQuadOperation =
                            new RSQuadOperation(this, pass.getIdentifier(), localMat);
                    float[] ret = new float[4];
                    if (pass.getQuadCorners(ret)) {
                        float left = ret[0];
                        float top = ret[1];
                        float right = ret[2];
                        float bottom = ret[3];
                        rsQuadOperation.setQuadCorners(left, top, right, bottom);
                    }
                    rsQuadOperation.setQuadFarCorners(
                            pass.getQuadFarCorners(), pass.getQuadFarCornersViewSpace());
                    queueRenderSystemOp(finalState, rsQuadOperation);

                } // end case PT_RENDERQUAD
                break;
                case PT_RENDERCUSTOM:
                    ENG_RenderSystemOperation customOperation =
                            ENG_CompositorManager.getSingleton().getCustomCompositionPass(
                                    pass.getCustomType()).createOperation(this, pass);
                    queueRenderSystemOp(finalState, customOperation);
                    break;
            }
        }
    }

    private void notifyResized() {
        freeResources(true, true);
        createResources(true);
    }

    private String getMRTTexLocalName(String baseName, int attachment) {
        return baseName + "/" + attachment;
    }

    public void freeResources(boolean forResizeOnly, boolean clearReserveTextures) {
        Iterator<TextureDefinition> iterator = mTechnique.getTextureDefinitionIterator();
        while (iterator.hasNext()) {
            TextureDefinition def = iterator.next();
            if (!def.refCompName.isEmpty()) {
                continue;
            }
            if (!forResizeOnly || def.width == 0 || def.height == 0) {
                int subSurf = def.formatList.size();
                for (int i = 0; i < subSurf; ++i) {
                    String texName = subSurf > 1 ? getMRTTexLocalName(def.name, i) :
                            def.name;
                    ENG_Texture texture = mLocalTextures.get(texName);
                    if (texture != null) {
                        if (!def.pooled && def.scope != TextureScope.TS_GLOBAL) {
                            ENG_TextureManager.getSingleton().destroyTexture(
                                    texture.getName());
                        }
                        mLocalTextures.remove(texName);
                    }
                }

                if (subSurf > 1) {
                    ENG_MultiRenderTarget renderTarget = mLocalMRTs.get(def.name);
                    if (renderTarget != null && def.scope != TextureScope.TS_GLOBAL) {
                        ENG_RenderRoot.getRenderRoot().getRenderSystem()
                                .destroyRenderTarget(renderTarget.getName());
                        mLocalMRTs.remove(def.name);
                    }
                }
            }
        }

        if (clearReserveTextures) {
            if (forResizeOnly) {
                ArrayList<TextureDefinition> defList =
                        new ArrayList<>();
                for (TextureDefinition def : mReserveTextures.keySet()) {
                    if (def.width == 0 || def.height == 0) {
                        defList.add(def);
                    }
                }
                for (TextureDefinition def : defList) {
                    mReserveTextures.remove(def);
                }
            } else {
                mReserveTextures.clear();
            }
        }

        ENG_CompositorManager.getSingleton().freePooledTextures(true);
    }

    public void destroy() {
        String compositorLogicName = mTechnique.getCompositorLogicName();
        if (compositorLogicName != null && !compositorLogicName.isEmpty()) {
            ENG_CompositorManager.getSingleton()
                    .getCompositorLogic(compositorLogicName).compositorInstanceDestroyed(this);
        }
        freeResources(false, true);
    }

    /** @noinspection deprecation */
    public void createResources(boolean forResizeOnly) {
        Iterator<TextureDefinition> iterator =
                mTechnique.getTextureDefinitionIterator();
        ArrayList<ENG_Texture> assignedTextures = new ArrayList<>();
        while (iterator.hasNext()) {
            TextureDefinition def = iterator.next();

            if (!def.refCompName.isEmpty()) {
                continue;
            }

            if (def.scope == TextureScope.TS_GLOBAL) {
                ENG_Compositor parentComp = mTechnique.getParent();

                if (def.formatList.size() > 1) {
                    int atch = 0;
                    for (PixelFormat pf : def.formatList) {
                        ENG_Texture tex = parentComp.getTextureInstance(def.name, atch++);
                        mLocalTextures.put(getMRTTexLocalName(def.name, atch), tex);
                    }
                    ENG_MultiRenderTarget mrt =
                            (ENG_MultiRenderTarget) parentComp.getRenderTarget(def.name);
                    mLocalMRTs.put(def.name, mrt);
                } else {
                    ENG_Texture tex = parentComp.getTextureInstance(def.name, 0);
                    mLocalTextures.put(def.name, tex);
                }
                continue;
            }

            /// Determine width and height
            int width = def.width;
            int height = def.height;
            int fsaa = 0;
            String fsaaHint = null;
            boolean hwGamma = false;

            // Skip this one if we're only (re)creating for a resize & it's not derived
            // from the target size
            if (forResizeOnly && width != 0 && height != 0) {
                continue;
            }

            TexParams texParams = new TexParams();
            deriveTextureRenderTargetOptions(def.name, texParams);

            if (width == 0) {
                width = (int) (mChain.getViewport().getActualWidth() *
                        def.widthFactor);
            }
            if (height == 0) {
                height = (int) (mChain.getViewport().getActualHeight() *
                        def.heightFactor);
            }

            if (!def.fsaa) {
                fsaa = 0;
                fsaaHint = "";
            }

            hwGamma = hwGamma || def.hwGammaWrite;

            /// Make the tetxure
            ENG_RenderTarget rendTarget;
            if (def.formatList.size() > 1) {
                String MRTBaseName = "c" + (dummyCounter++) + "/" + def.name + "/" +
                        mChain.getViewport().getTarget().getName();
                ENG_MultiRenderTarget mrt = ENG_RenderRoot.getRenderRoot()
                        .getRenderSystem().createMultiRenderTarget(MRTBaseName);
                mLocalMRTs.put(def.name, mrt);

                int atch = 0;
                for (PixelFormat pf : def.formatList) {
                    String texName = MRTBaseName + "/" + (atch++);
                    String mrtLocalName = getMRTTexLocalName(def.name, atch);
                    ENG_Texture tex;
                    if (def.pooled) {
                        tex = ENG_CompositorManager.getSingleton().getPooledTexture(
                                texName,
                                mrtLocalName,
                                width, height, pf, fsaa, fsaaHint,
                                hwGamma && !ENG_PixelUtil.isFloatingPoint(pf),
                                assignedTextures, this, def.scope);
                    } else {
                        int len = ENG_Utility.getWidthAndHeight(width, height);
                        tex = ENG_TextureManager.getSingleton().createManual(
                                texName, TextureType.TEX_TYPE_2D,
                                width, height, 1, 0, pf,
                                TextureUsage.TU_RENDERTARGET.getUsage(),
                                hwGamma && !ENG_PixelUtil.isFloatingPoint(pf),
                                fsaa, fsaaHint);
                    }

                    ENG_RenderTexture rt = tex.getBuffer().getRenderTarget();
                    rt.setAutoUpdated(false);
                    mrt.bindSurface(atch, rt);

                    mLocalTextures.put(mrtLocalName, tex);
                }
                rendTarget = mrt;
            } else {
                String texName = "c" + (dummyCounter++) +
                        "/" + def.name + "/" +
                        mChain.getViewport().getTarget().getName();
                texName = texName.replace(" ", "_");

                ENG_Texture tex;
                if (def.pooled) {
                    tex = ENG_CompositorManager.getSingleton().getPooledTexture(
                            texName,
                            def.name,
                            width, height, def.formatList.get(0), fsaa, fsaaHint,
                            (hwGamma) &&
                                    (!ENG_PixelUtil.isFloatingPoint(def.formatList.get(0))),
                            assignedTextures, this, def.scope);
                } else {
                    int len = ENG_Utility.getWidthAndHeight(width, height);
                    tex = ENG_TextureManager.getSingleton().createManual(
                            texName, TextureType.TEX_TYPE_2D,
                            width, height, 1, 0, def.formatList.get(0),
                            TextureUsage.TU_RENDERTARGET.getUsage(),
                            (hwGamma) &&
                                    (!ENG_PixelUtil.isFloatingPoint(def.formatList.get(0))),
                            fsaa, fsaaHint);
                }

                rendTarget = tex.getBuffer().getRenderTarget();
                mLocalTextures.put(def.name, tex);
            }

            /// Set up viewport over entire texture
            rendTarget.setAutoUpdated(false);

            // We may be sharing / reusing this texture, so test before adding viewport
            if (rendTarget.getNumViewports() == 0) {
                ENG_Camera camera = mChain.getViewport().getCamera();

                // Save last viewport and current aspect ratio
                ENG_Viewport oldViewport = camera.getViewport();
                float aspectRatio = camera.getAspectRatio();

                ENG_Viewport v = rendTarget.addViewport(camera);
                v.setClearEveryFrame(false);
                v.setOverlaysEnabled(false);
                v.setBackgroundColour(new ENG_ColorValue(0.0f, 0.0f, 0.0f, 0.0f));

                // Should restore aspect ratio, in case of auto aspect ratio
                // enabled, it'll changed when add new viewport.
                camera.setAspectRatio(aspectRatio);
                // Should restore last viewport, i.e. never disturb user code
                // which might based on that.
                camera._notifyViewport(oldViewport);
            }
        }
        _fireNotifyResourcesCreated(forResizeOnly);
    }

    private void deriveTextureRenderTargetOptions(String name,
                                                  TexParams texParams) {
        
        // search for passes on this texture def that either include a render_scene
        // or use input previous
        boolean renderingScene;
        Iterator<ENG_CompositionTargetPass> iterator =
                mTechnique.getTargetPassIterator();
        while (iterator.hasNext()) {
            ENG_CompositionTargetPass tp = iterator.next();

            if (tp.getOutputName().equals(name)) {
                if (tp.getInputMode() == InputMode.IM_PREVIOUS) {
                    // this may be rendering the scene implicitly
                    // Can't check mPreviousInstance against
                    // mChain->_getOriginalSceneCompositor()
                    // at this time, so check the position
                    Iterator<ENG_CompositorInstance> it = mChain.getCompositors();
                    renderingScene = true;
                    while (it.hasNext()) {
                        ENG_CompositorInstance inst = it.next();
                        if (inst == this) {
                            break;
                        } else if (inst.getEnabled()) {
                            // nope, we have another compositor before us, this will
                            // be doing the AA
                            renderingScene = false;
                        }
                    }
                    if (renderingScene) {
                        break;
                    }
                } else {

                }
            }

        }
    }

    public ENG_Compositor getCompositor() {
        return mCompositor;
    }

    public ENG_CompositorChain getChain() {
        return mChain;
    }

    public ENG_CompositionTechnique getTechnique() {
        return mTechnique;
    }

    public boolean getEnabled() {
        
        return mEnabled;
    }

    public void setEnabled(boolean value) {
        if (mEnabled != value) {
            mEnabled = value;

            // Create of free resource.
            if (value) {
                createResources(false);
            } else {
                freeResources(false, true);
            }

            /// Notify chain state needs recompile.
            mChain._markDirty();
        }
    }

    private static class TexParams {
        public int fsaa;
        public String fsaaHint;
        public boolean hwGamma;
    }

    private String getSourceForTex(String name, int mrtIndex) {
        
        TextureDefinition texDef = mTechnique.getTextureDefinition(name);
        if (!texDef.refCompName.isEmpty()) {
            ENG_CompositorInstance refCompInst =
                    mChain.getCompositor(texDef.refCompName);
            if (refCompInst == null) {
                throw new ENG_InvalidFieldStateException(
                        "Referencing non-existent compositor");
            }
            ENG_Compositor refComp = refCompInst.getCompositor();
            TextureDefinition refTexDef =
                    refComp.getSupportedTechnique(refCompInst.getScheme())
                            .getTextureDefinition(texDef.refTexName);
            if (refTexDef == null) {
                throw new ENG_InvalidFieldStateException(
                        "Referencing non-existent compositor texture");
            }

            switch (refTexDef.scope) {
                case TS_CHAIN: {
                    refCompInst = null;
                    Iterator<ENG_CompositorInstance> iterator = mChain.getCompositors();
                    boolean beforeMe = true;
                    while (iterator.hasNext()) {
                        ENG_CompositorInstance nextCompInst = iterator.next();
                        if (nextCompInst.getCompositor()
                                .getName().equals(texDef.refCompName)) {
                            refCompInst = nextCompInst;
                            break;
                        }
                        if (nextCompInst == this) {
                            //We encountered ourselves while searching for the compositor -
                            //we are earlier in the chain.
                            beforeMe = false;
                        }
                    }
                    if (refCompInst == null || !refCompInst.getEnabled()) {
                        throw new ENG_InvalidFieldStateException(
                                "Referencing inactive compositor texture");
                    }
                    if (!beforeMe) {
                        throw new ENG_InvalidFieldStateException(
                                "Referencing compositor that is later in the chain");
                    }
                    return refCompInst.getTextureInstanceName(texDef.refTexName, mrtIndex);
                }
                case TS_GLOBAL:
                    return refComp.getTextureInstanceName(texDef.refTexName, mrtIndex);
                case TS_LOCAL:
                default:
                    throw new ENG_InvalidFieldStateException(
                            "Referencing local compositor texture");
            }
        }

        if (texDef.formatList.size() > 1) {
            ENG_MultiRenderTarget target = mLocalMRTs.get(name);
            if (target != null) {
                return target.getName();
            }
        } else {
            ENG_Texture texture = mLocalTextures.get(name);
            if (texture != null) {
                return texture.getName();
            }
        }

        throw new IllegalArgumentException("Non-existent local texture name");

    }

    public ENG_Texture getTextureInstance(String name, int mrtIndex) {
        ENG_Texture texture = mLocalTextures.get(name);
        if (texture != null) {
            return texture;
        }
        ENG_Texture target =
                mLocalTextures.get(getMRTTexLocalName(name, mrtIndex));
        if (target != null) {
            return target;
        }
        return null;
    }

    public String getTextureInstanceName(String name, int mrtIndex) {
        
        return getSourceForTex(name, mrtIndex);
    }

    private ENG_Material createLocalMaterial(String name) {
        
        ENG_Material material = ENG_MaterialManager.getSingleton().create(
                "c" + (dummyCounter++) + "/" + name);

        ENG_MaterialManager.getSingleton().remove(material.getName());
        material.createTechnique();
        material.compile();
        material.getTechnique((short) 0).removeAllPasses();
        return material;
    }

    private void queueRenderSystemOp(
            TargetOperation finalState,
            ENG_RenderSystemOperation op) {
        finalState.renderSystemOperations.add(
                new RenderSystemOpPair(finalState.currentQueueGroupID, op));
        /// Tell parent for deletion
        mChain._queuedOperation(op);
    }

    private static int dummyCounter;


    public ENG_CompositorInstance(
            ENG_CompositionTechnique technique, ENG_CompositorChain chain) {
        mCompositor = technique.getParent();
        mTechnique = technique;
        mChain = chain;

        String logicName = mTechnique.getCompositorLogicName();
        if (!logicName.isEmpty()) {
            ENG_CompositorManager.getSingleton()
                    .getCompositorLogic(logicName).compositorInstanceCreated(this);
        }
    }


    public void _fireNotifyMaterialRender(int pass_id, ENG_Material mat) {
        
        for (Listener l : mListeners) {
            l.notifyMaterialRender(pass_id, mat);
        }
    }


    public void _fireNotifyMaterialSetup(int pass_id, ENG_Material mat) {
        
        for (Listener l : mListeners) {
            l.notifyMaterialSetup(pass_id, mat);
        }
    }

    public void _fireNotifyResourcesCreated(boolean forResizeOnly) {
        for (Listener l : mListeners) {
            l.notifyResourcesCreated(forResizeOnly);
        }
    }

    public void _compileTargetOperations(
            ArrayList<TargetOperation> compiledState) {
        
        if (mPreviousInstance != null) {
            mPreviousInstance._compileTargetOperations(compiledState);
        }
        Iterator<ENG_CompositionTargetPass> passIterator =
                mTechnique.getTargetPassIterator();
        while (passIterator.hasNext()) {
            ENG_CompositionTargetPass target = passIterator.next();

            TargetOperation ts = new TargetOperation(
                    getTargetForTex(target.getOutputName()));
            /// Set "only initial" flag, visibilityMask and lodBias according to CompositionTargetPass.
            ts.onlyInitial = target.getOnlyInitial();
            ts.visibilityMask = target.getVisibilityMask();
            ts.lodBias = target.getLodBias();
            ts.shadowsEnabled = target.getShadowsEnabled();
            ts.materialScheme = target.getMaterialScheme();
            /// Check for input mode previous
            if (target.getInputMode() == InputMode.IM_PREVIOUS) {
                /// Collect target state for previous compositor
                /// The TargetOperation for the final target is collected seperately as it is merged
                /// with later operations
                mPreviousInstance._compileOutputOperation(ts);
            }
            /// Collect passes of our own target
            collectPasses(ts, target);
            compiledState.add(ts);
        }
    }

    public void _compileOutputOperation(TargetOperation finalState) {
        
        /// Final target
        ENG_CompositionTargetPass tpass = mTechnique.getOutputTargetPass();

        /// Logical-and together the visibilityMask, and multiply the lodBias
        finalState.visibilityMask &= tpass.getVisibilityMask();
        finalState.lodBias *= tpass.getLodBias();
        finalState.materialScheme = tpass.getMaterialScheme();
        finalState.shadowsEnabled = tpass.getShadowsEnabled();

        if (tpass.getInputMode() == ENG_CompositionTargetPass.InputMode.IM_PREVIOUS) {
            /// Collect target state for previous compositor
            /// The TargetOperation for the final target is collected seperately as it is merged
            /// with later operations
            mPreviousInstance._compileOutputOperation(finalState);
        }
        /// Collect passes
        collectPasses(finalState, tpass);
    }

    public ENG_RenderTarget getTargetForTex(String name) {
        ENG_Texture texture = mLocalTextures.get(name);
        if (texture != null) {
            return texture.getBuffer().getRenderTarget();
        }
        ENG_MultiRenderTarget multiRenderTarget = mLocalMRTs.get(name);
        if (multiRenderTarget != null) {
            return multiRenderTarget;
        }

        TextureDefinition texDef = mTechnique.getTextureDefinition(name);
        if ((texDef != null) && (!texDef.refCompName.isEmpty())) {
            ENG_CompositorInstance refCompInst =
                    mChain.getCompositor(texDef.refCompName);
            if (refCompInst == null) {
                throw new ENG_InvalidFieldStateException(
                        "Referencing non-existent compositor");
            }
            ENG_Compositor refComp = refCompInst.getCompositor();
            TextureDefinition refTexDef = refComp.getSupportedTechnique(
                    refCompInst.getScheme()).getTextureDefinition(texDef.refTexName);
            if (refTexDef == null) {
                throw new ENG_InvalidFieldStateException(
                        "Referencing non-existent compositor texture");
            }

            switch (refTexDef.scope) {
                case TS_CHAIN: {
                    //Find the instance and check if it is before us
                    ENG_CompositorInstance refCompInstance = null;
                    Iterator<ENG_CompositorInstance> iterator = mChain.getCompositors();
                    boolean beforeMe = true;
                    while (iterator.hasNext()) {
                        ENG_CompositorInstance nextCompInst = iterator.next();
                        if (nextCompInst.getCompositor().getName()
                                .equals(texDef.refCompName)) {
                            refCompInstance = nextCompInst;
                            break;
                        }
                        if (nextCompInst == this) {
                            //We encountered ourselves while searching for the compositor -
                            //we are earlier in the chain.
                            beforeMe = false;
                        }
                    }

                    if (refCompInstance == null || !refCompInstance.getEnabled()) {
                        throw new ENG_InvalidFieldStateException(
                                "Referencing inactive compositor texture");
                    }
                    if (!beforeMe) {
                        throw new ENG_InvalidFieldStateException(
                                "Referencing compositor that is later in the chain");
                    }
                    return refCompInstance.getRenderTarget(texDef.refTexName);
                }
                case TS_GLOBAL:
                    //Chain and global case - the referenced compositor will know how to handle
                    return refComp.getRenderTarget(texDef.refTexName);
                case TS_LOCAL:
                default:
                    throw new ENG_InvalidFieldStateException(
                            "Referencing local compositor texture");
            }
        }

        throw new NullPointerException("Non-existent local texture name");
    }

    public ENG_RenderTarget getRenderTarget(String name) {
        
        return getTargetForTex(name);
    }

    public void setScheme(String schemeName) {
        setScheme(schemeName, true);
    }

    public void setScheme(String schemeName, boolean reuseTextures) {
        ENG_CompositionTechnique tech = mCompositor.getSupportedTechnique(schemeName);
        if (tech != null) {
            setTechnique(tech, reuseTextures);
            mActiveScheme = tech.getSchemeName();
        }
    }

    public void setTechnique(ENG_CompositionTechnique tech) {
        setTechnique(tech, true);
    }

    public void setTechnique(ENG_CompositionTechnique tech,
                             boolean reuseTextures) {
        
        if (mTechnique != tech) {
            if (reuseTextures) {
                Iterator<TextureDefinition> iterator =
                        mTechnique.getTextureDefinitionIterator();
                while (iterator.hasNext()) {
                    TextureDefinition texDef = iterator.next();
                    if (texDef.pooled) {
                        ENG_Texture texture = mLocalTextures.get(texDef.name);
                        if (texture != null) {
                            mReserveTextures.put(texDef, texture);
                        }
                    }
                }
            }

            // replace technique
            mTechnique = tech;

            if (mEnabled) {
                // free up resources, but keep reserves if reusing
                freeResources(false, !reuseTextures);
                createResources(false);
                /// Notify chain state needs recompile.
                mChain._markDirty();
            }
        }
    }

    public String getScheme() {
        
        return mActiveScheme;
    }

    public void addListener(Listener l) {
        mListeners.add(l);
    }

    public void removeListener(Listener l) {
        mListeners.remove(l);
    }


}
