/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.exception.ENG_UndeclaredIdentifierException;
import headwayent.hotshotengine.renderer.ENG_QueuedRenderableCollection.OrganisationMode;

import java.util.Iterator;

public class ENG_RenderPriorityGroup {

    protected final ENG_RenderQueueGroup mParent;
    protected boolean mSplitPassesByLightingType;
    protected boolean mSplitNoShadowPasses;
    protected boolean mShadowCastersNotReceivers;

    /// Solid pass list, used when no shadows,
    //modulative shadows, or ambient passes for additive
    protected final ENG_QueuedRenderableCollection mSolidsBasic = new ENG_QueuedRenderableCollection();
    // Optimization hack. We don't use all these so we may as well avoid a clear() on each of them uselessly.
//    /// Solid per-light pass list, used with additive shadows
//    protected ENG_QueuedRenderableCollection mSolidsDiffuseSpecular = new ENG_QueuedRenderableCollection();
//    /// Solid decal (texture) pass list, used with additive shadows
//    protected ENG_QueuedRenderableCollection mSolidsDecal = new ENG_QueuedRenderableCollection();
//    /// Solid pass list, used when shadows are enabled but shadow receive
//    //is turned off for these passes
//    protected ENG_QueuedRenderableCollection mSolidsNoShadowReceive = new ENG_QueuedRenderableCollection();
//    /// Unsorted transparent list
//    protected ENG_QueuedRenderableCollection mTransparentsUnsorted = new ENG_QueuedRenderableCollection();
    /// Transparent list
    protected final ENG_QueuedRenderableCollection mTransparents = new ENG_QueuedRenderableCollection();

    protected void removePassEntry(ENG_Pass p) {
        mSolidsBasic.removePassGroup(p);
//        mSolidsDiffuseSpecular.removePassGroup(p);
//        mSolidsNoShadowReceive.removePassGroup(p);
//        mSolidsDecal.removePassGroup(p);
//        mTransparentsUnsorted.removePassGroup(p);
        mTransparents.removePassGroup(p); // shouldn't be any, but for completeness
    }

    protected void addSolidRenderable(ENG_Technique pTech, ENG_Renderable rend, boolean toNoShadowMap) {
        Iterator<ENG_Pass> pi = pTech.getPassIterator();

        ENG_QueuedRenderableCollection collection;
        if (toNoShadowMap) {
//            collection = mSolidsNoShadowReceive;
            throw new UnsupportedOperationException();
        } else {
            collection = mSolidsBasic;
        }

        while (pi.hasNext()) {
            collection.addRenderable(pi.next(), rend);
        }
    }

    protected void addSolidRenderableSplitByLightType(ENG_Technique pTech,
                                                      ENG_Renderable rend) {
        Iterator<ENG_IlluminationPass> pi = pTech.getIlluminationPassIterator();

        while (pi.hasNext()) {
            ENG_IlluminationPass p = pi.next();
            ENG_QueuedRenderableCollection collection;

            switch (p.stage) {
                case IS_AMBIENT:
                    collection = mSolidsBasic;
                    break;
                case IS_PER_LIGHT:
//                    collection = mSolidsDiffuseSpecular;
                    throw new UnsupportedOperationException();
//                    break;
                case IS_DECAL:
//                    collection = mSolidsDecal;
                    throw new UnsupportedOperationException();
//                    break;
                default:
                    //Should never get here
                    throw new ENG_UndeclaredIdentifierException();
            }
            collection.addRenderable(p.pass, rend);
        }
    }

    protected void addUnsortedTransparentRenderable(ENG_Technique pTech,
                                                    ENG_Renderable rend) {
        throw new UnsupportedOperationException();
//        Iterator<ENG_Pass> pi = pTech.getPassIterator();
//
//        while (pi.hasNext()) {
//            mTransparentsUnsorted.addRenderable(pi.next(), rend);
//        }
    }

    protected void addTransparentRenderable(ENG_Technique pTech,
                                            ENG_Renderable rend) {
        Iterator<ENG_Pass> pi = pTech.getPassIterator();

        while (pi.hasNext()) {
            mTransparents.addRenderable(pi.next(), rend);
        }
    }

    public ENG_RenderPriorityGroup(ENG_RenderQueueGroup parent,
                                   boolean splitPassesByLightingType,
                                   boolean splitNoShadowPasses,
                                   boolean shadowCastersNotReceivers) {
        mParent = parent;
        mSplitPassesByLightingType = splitPassesByLightingType;
        mSplitNoShadowPasses = splitNoShadowPasses;
        mShadowCastersNotReceivers = shadowCastersNotReceivers;

        // Initialise collection sorting options
        // this can become dynamic according to invocation later
        defaultOrganisationMode();

        // Transparents will always be sorted this way
        mTransparents.addOrganisationMode(OrganisationMode.OM_SORT_DESCENDING);
    }

    public ENG_QueuedRenderableCollection getSolidsBasic() {
        return mSolidsBasic;
    }

    public ENG_QueuedRenderableCollection getSolidsDiffuseSpecular() {
        throw new UnsupportedOperationException();
//        return mSolidsDiffuseSpecular;
    }

    public ENG_QueuedRenderableCollection getSolidsDecal() {
        throw new UnsupportedOperationException();
//        return mSolidsDecal;
    }

    public ENG_QueuedRenderableCollection getSolidsNoShadowReceive() {
        throw new UnsupportedOperationException();
//        return mSolidsNoShadowReceive;
    }

    public ENG_QueuedRenderableCollection getTransparentsUnsorted() {
        throw new UnsupportedOperationException();
//        return mTransparentsUnsorted;
    }

    public ENG_QueuedRenderableCollection getTransparents() {
        return mTransparents;
    }

    public void addRenderable(ENG_Renderable rend, ENG_Technique pTech) {
        if ((pTech.isTransparentSortingForced()) ||
                (pTech.isTransparent() && (!pTech.isDepthWriteEnabled() ||
                        !pTech.isDepthCheckEnabled() ||
                        pTech.hasColourWriteDisabled()))) {
            if (pTech.isTransparentSortingEnabled()) {
                addTransparentRenderable(pTech, rend);
            } else {
                addUnsortedTransparentRenderable(pTech, rend);
            }
        } else {
            if (mSplitNoShadowPasses && mParent.mShadowsEnabled &&
                    (!pTech.getParent().getReceiveShadows() ||
                            rend.getCastsShadows() && mShadowCastersNotReceivers)) {
                // Add solid renderable and add passes to no-shadow group
                addSolidRenderable(pTech, rend, true);
            } else {
                if (mSplitPassesByLightingType && mParent.getShadowsEnabled()) {
                    addSolidRenderableSplitByLightType(pTech, rend);
                } else {
                    addSolidRenderable(pTech, rend, false);
                }
            }
        }
    }

    public void resetOrganisationModes() {
        mSolidsBasic.resetOrganisationModes();
//        mSolidsDiffuseSpecular.resetOrganisationModes();
//        mSolidsDecal.resetOrganisationModes();
//        mSolidsNoShadowReceive.resetOrganisationModes();
//        mTransparentsUnsorted.resetOrganisationModes();
    }

    public void addOrganisationMode(OrganisationMode om) {
        mSolidsBasic.addOrganisationMode(om);
//        mSolidsDiffuseSpecular.addOrganisationMode(om);
//        mSolidsDecal.addOrganisationMode(om);
//        mSolidsNoShadowReceive.addOrganisationMode(om);
//        mTransparentsUnsorted.addOrganisationMode(om);
    }

    public void defaultOrganisationMode() {
        resetOrganisationModes();
        addOrganisationMode(OrganisationMode.OM_PASS_GROUP);
    }

    public void clear() {

//        ENG_Pass.msPassGraveyardMutex.lock();
//        try {
            for (ENG_Pass pass : ENG_Pass.msPassGraveyard) {
                removePassEntry(pass);
            }
//        } finally {
//            ENG_Pass.msPassGraveyardMutex.unlock();
//        }

//        ENG_Pass.msDirtyHashListMutex.lock();
//        try {
            for (ENG_Pass pass : ENG_Pass.msDirtyHash) {
                removePassEntry(pass);
            }
//        } finally {
//            ENG_Pass.msDirtyHashListMutex.unlock();
//        }

        mSolidsBasic.clear();
//        mSolidsDecal.clear();
//        mSolidsDiffuseSpecular.clear();
//        mSolidsNoShadowReceive.clear();
//        mTransparentsUnsorted.clear();
        mTransparents.clear();
    }

    public void sort(ENG_Camera cam) {
        mSolidsBasic.sort(cam);
//        mSolidsDecal.sort(cam);
//        mSolidsDiffuseSpecular.sort(cam);
//        mSolidsNoShadowReceive.sort(cam);
//        mTransparentsUnsorted.sort(cam);
        mTransparents.sort(cam);
    }

    public void merge(ENG_RenderPriorityGroup rhs) {
        mSolidsBasic.merge(rhs.mSolidsBasic);
//        mSolidsDecal.merge(rhs.mSolidsDecal);
//        mSolidsDiffuseSpecular.merge(rhs.mSolidsDiffuseSpecular);
//        mSolidsNoShadowReceive.merge(rhs.mSolidsNoShadowReceive);
//        mTransparentsUnsorted.merge(rhs.mTransparentsUnsorted);
        mTransparents.merge(rhs.mTransparents);
    }

    public void setSplitPassesByLightingType(boolean split) {
        mSplitPassesByLightingType = split;
    }

    public void setSplitNoShadowPasses(boolean split) {
        mSplitNoShadowPasses = split;
    }

    public void setShadowCastersCannotBeReceivers(boolean ind) {
        mShadowCastersNotReceivers = ind;
    }

/*	public void removePassEntry(ENG_Pass p) {
		
	}*/
}
