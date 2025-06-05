/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.renderer.ENG_QueuedRenderableCollection.OrganisationMode;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

public class ENG_RenderQueueGroup {

    protected final ENG_RenderQueue mParent;
    protected boolean mSplitPassesByLightingType;
    protected boolean mSplitNoShadowPasses;
    protected boolean mShadowCastersNotReceivers;

    protected final TreeMap<ENG_Short, ENG_RenderPriorityGroup> mPriorityGroups =
            new TreeMap<>();
    /// Whether shadows are enabled for this queue
    protected boolean mShadowsEnabled = true;
    /// Bitmask of the organisation modes requested (for new priority groups)
    protected byte mOrganisationMode;

    public ENG_RenderQueueGroup(ENG_RenderQueue parent,
                                boolean splitPassesByLightingType,
                                boolean splitNoShadowPasses,
                                boolean shadowCastersNotReceivers) {
        mParent = parent;
        mSplitPassesByLightingType = splitPassesByLightingType;
        mSplitNoShadowPasses = splitNoShadowPasses;
        mShadowCastersNotReceivers = shadowCastersNotReceivers;
    }

    public void addRenderable(ENG_Renderable pRend, ENG_Technique pTech,
                              short priority) {
        addRenderable(pRend, pTech, new ENG_Short(priority));
    }

    public void addRenderable(ENG_Renderable pRend, ENG_Technique pTech,
                              ENG_Short priority) {
        ENG_RenderPriorityGroup priorityGroup = mPriorityGroups.get(priority);
        if (priorityGroup == null) {
            priorityGroup = new ENG_RenderPriorityGroup(this,
                    mSplitPassesByLightingType,
                    mSplitNoShadowPasses,
                    mShadowCastersNotReceivers);
            if (mOrganisationMode != 0) {
                priorityGroup.resetOrganisationModes();
                priorityGroup.addOrganisationMode(
                        OrganisationMode.get(mOrganisationMode));
            }
            mPriorityGroups.put(priority, priorityGroup);
        } else {

        }

        priorityGroup.addRenderable(pRend, pTech);
    }

    public void clear() {
        clear(false);
    }

    public void clear(boolean destroy) {
//        for (Entry<ENG_Short, ENG_RenderPriorityGroup> entry : mPriorityGroups.entrySet()) {
//            if (destroy) {
//                mPriorityGroups.put(entry.getKey(), null);
//            } else {
//                entry.getValue().clear();
//            }
//        }

        Iterator<Entry<ENG_Short, ENG_RenderPriorityGroup>> it = mPriorityGroups.entrySet().iterator();

        while (it.hasNext()) {
            if (destroy) {
                ENG_Short s = it.next().getKey();
                mPriorityGroups.put(s, null);
            } else {
                it.next().getValue().clear();
            }
        }

        if (destroy) {
            mPriorityGroups.clear();
        }
    }

    public void setSplitPassesByLightingType(boolean split) {
        mSplitPassesByLightingType = split;
        for (Entry<ENG_Short, ENG_RenderPriorityGroup> engShortENGRenderPriorityGroupEntry : mPriorityGroups.entrySet()) {
            engShortENGRenderPriorityGroupEntry.getValue().setSplitPassesByLightingType(split);
        }
    }

    public void setSplitNoShadowPasses(boolean split) {
        mSplitNoShadowPasses = split;
        for (Entry<ENG_Short, ENG_RenderPriorityGroup> engShortENGRenderPriorityGroupEntry : mPriorityGroups.entrySet()) {
            engShortENGRenderPriorityGroupEntry.getValue().setSplitNoShadowPasses(split);
        }
    }

    public void setShadowCastersCannotBeReceivers(boolean ind) {
        mShadowCastersNotReceivers = ind;
        for (Entry<ENG_Short, ENG_RenderPriorityGroup> engShortENGRenderPriorityGroupEntry : mPriorityGroups.entrySet()) {
            engShortENGRenderPriorityGroupEntry.getValue().setShadowCastersCannotBeReceivers(ind);
        }
    }

    public void resetOrganisationMode() {
        mOrganisationMode = 0;
        for (Entry<ENG_Short, ENG_RenderPriorityGroup> engShortENGRenderPriorityGroupEntry : mPriorityGroups.entrySet()) {
            engShortENGRenderPriorityGroupEntry.getValue().resetOrganisationModes();
        }
    }

    public void addOrganisationMode(OrganisationMode om) {
        mOrganisationMode |= om.getMode();
        for (Entry<ENG_Short, ENG_RenderPriorityGroup> engShortENGRenderPriorityGroupEntry : mPriorityGroups.entrySet()) {
            engShortENGRenderPriorityGroupEntry.getValue().addOrganisationMode(om);
        }
    }

    public void defaultOrganisationMode() {
        mOrganisationMode = 0;
        for (Entry<ENG_Short, ENG_RenderPriorityGroup> engShortENGRenderPriorityGroupEntry : mPriorityGroups.entrySet()) {
            engShortENGRenderPriorityGroupEntry.getValue().defaultOrganisationMode();
        }
    }

    public void merge(ENG_RenderQueueGroup rhs) {
        Iterator<Entry<ENG_Short, ENG_RenderPriorityGroup>> it = rhs.getIterator();

        while (it.hasNext()) {
            Entry<ENG_Short, ENG_RenderPriorityGroup> entry = it.next();
            ENG_RenderPriorityGroup srcPriorityGroup = entry.getValue();
            ENG_RenderPriorityGroup dstPriorityGroup =
                    mPriorityGroups.get(entry.getKey());
            if (dstPriorityGroup == null) {
                dstPriorityGroup = new ENG_RenderPriorityGroup(this,
                        mSplitPassesByLightingType,
                        mSplitNoShadowPasses,
                        mShadowCastersNotReceivers);
                if (mOrganisationMode != 0) {
                    dstPriorityGroup.resetOrganisationModes();
                    dstPriorityGroup.addOrganisationMode(
                            OrganisationMode.get(mOrganisationMode));
                }
                mPriorityGroups.put(entry.getKey(), dstPriorityGroup);
            }
            dstPriorityGroup.merge(srcPriorityGroup);
        }
    }

    public void setShadowsEnabled(boolean enabled) {
        mShadowsEnabled = enabled;
    }

    public boolean getShadowsEnabled() {
        return mShadowsEnabled;
    }

    public Iterator<Entry<ENG_Short, ENG_RenderPriorityGroup>> getIterator() {
        return mPriorityGroups.entrySet().iterator();
    }
}
