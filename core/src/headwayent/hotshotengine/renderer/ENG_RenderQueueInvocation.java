/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.ENG_QueuedRenderableCollection.OrganisationMode;

public class ENG_RenderQueueInvocation {

    public static final String RENDER_QUEUE_INVOCATION_SHADOWS = "SHADOWS";
    protected final byte mRenderQueueGroupID;
    protected final String mInvocationName;
    protected OrganisationMode mSolidsOrganisation = OrganisationMode.OM_PASS_GROUP;
    protected boolean mSuppressShadows;
    protected boolean mSuppressRenderStateChanges;

    public ENG_RenderQueueInvocation(byte renderQueueGroupID) {
        this(renderQueueGroupID, "");
    }

    public ENG_RenderQueueInvocation(byte renderQueueGroupID, String invocationName) {
        mRenderQueueGroupID = renderQueueGroupID;
        mInvocationName = invocationName;
    }

    public void invoke() {

    }

    /**
     * @return the mSolidsOrganisation
     */
    public OrganisationMode getSolidsOrganisation() {
        return mSolidsOrganisation;
    }

    /**
     * @param mSolidsOrganisation the mSolidsOrganisation to set
     */
    public void setSolidsOrganisation(OrganisationMode mSolidsOrganisation) {
        this.mSolidsOrganisation = mSolidsOrganisation;
    }

    /**
     * @return the mSuppressShadows
     */
    public boolean isSuppressShadows() {
        return mSuppressShadows;
    }

    /**
     * @param mSuppressShadows the mSuppressShadows to set
     */
    public void setSuppressShadows(boolean mSuppressShadows) {
        this.mSuppressShadows = mSuppressShadows;
    }

    /**
     * @return the mSuppressRenderStateChanges
     */
    public boolean isSuppressRenderStateChanges() {
        return mSuppressRenderStateChanges;
    }

    /**
     * @param mSuppressRenderStateChanges the mSuppressRenderStateChanges to set
     */
    public void setSuppressRenderStateChanges(boolean mSuppressRenderStateChanges) {
        this.mSuppressRenderStateChanges = mSuppressRenderStateChanges;
    }

    /**
     * @return the mRenderQueueGroupID
     */
    public byte getRenderQueueGroupID() {
        return mRenderQueueGroupID;
    }

    /**
     * @return the mInvocationName
     */
    public String getInvocationName() {
        return mInvocationName;
    }
}
