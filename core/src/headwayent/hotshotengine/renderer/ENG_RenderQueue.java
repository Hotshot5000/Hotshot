/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.basictypes.ENG_Byte;
import headwayent.hotshotengine.basictypes.ENG_Short;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

public class ENG_RenderQueue {

    /**
     * Enumeration of queue groups, by which the application may group queued renderables
     * so that they are rendered together with events in between
     *
     * @remarks When passed into methods these are actually passed as a uint8 to allow you
     * to use values in between if you want to.
     */
    public enum RenderQueueGroupID {
        /// Use this queue for objects which must be rendered first e.g. backgrounds
        RENDER_QUEUE_BACKGROUND(0),
        /// First queue (after backgrounds), used for skyboxes if rendered first
        RENDER_QUEUE_SKIES_EARLY(5),
        RENDER_QUEUE_1(10),
        RENDER_QUEUE_2(20),
        RENDER_QUEUE_WORLD_GEOMETRY_1(25),
        RENDER_QUEUE_3(30),
        RENDER_QUEUE_4(40),
        /// The default render queue
        RENDER_QUEUE_MAIN(50),
        RENDER_QUEUE_6(60),
        RENDER_QUEUE_7(70),
        RENDER_QUEUE_WORLD_GEOMETRY_2(75),
        RENDER_QUEUE_8(80),
        RENDER_QUEUE_9(90),
        /// Penultimate queue(before overlays), used for skyboxes if rendered last
        RENDER_QUEUE_SKIES_LATE(95),
        /// Use this queue for objects which must be rendered last e.g. overlays
        RENDER_QUEUE_OVERLAY(100),
        /// Final possible render queue, don't exceed this
        RENDER_QUEUE_MAX(105);

        private final byte id;

        RenderQueueGroupID(int id) {
            this.id = (byte) id;
        }

        public byte getID() {
            return id;
        }
    }

    public static final short OGRE_RENDERABLE_DEFAULT_PRIORITY = 100;

    protected final TreeMap<ENG_Byte, ENG_RenderQueueGroup> mGroups = new TreeMap<>();

    /// The current default queue group
    protected byte mDefaultQueueGroup;
    /// The default priority
    protected short mDefaultRenderablePriority;

    protected boolean mSplitPassesByLightingType;
    protected boolean mSplitNoShadowPasses;
    protected boolean mShadowCastersCannotBeReceivers;

    public ENG_RenderQueue() {
        mGroups.put(new ENG_Byte(RenderQueueGroupID.RENDER_QUEUE_MAIN.getID()),
                new ENG_RenderQueueGroup(this,
                        mSplitPassesByLightingType,
                        mSplitNoShadowPasses,
                        mShadowCastersCannotBeReceivers));
        // set default queue
        mDefaultQueueGroup = RenderQueueGroupID.RENDER_QUEUE_MAIN.getID();
        mDefaultRenderablePriority = OGRE_RENDERABLE_DEFAULT_PRIORITY;
    }

    public void addRenderable(ENG_Renderable pRend, byte groupID, short priority) {
        addRenderable(pRend, new ENG_Byte(groupID), new ENG_Short(priority));
    }

    public void addRenderable(ENG_Renderable pRend, ENG_Byte groupID, ENG_Short priority) {
        ENG_RenderQueueGroup pGroup = mGroups.get(groupID);
        ENG_Technique pTech;
        if (pRend.getMaterial() == null || pRend.getTechnique() == null) {
            System.out.println("Null material and null technique! Loading BaseWhite");
            ENG_Material material =
                    ENG_MaterialManager.getSingleton().getByName("BaseWhite");
            pTech = material.getTechnique((short) 0);
        } else {
            pTech = pRend.getTechnique();
        }
        pGroup.addRenderable(pRend, pTech, priority);
    }

    public void addRenderable(ENG_Renderable pRend, byte groupID) {
        addRenderable(pRend, new ENG_Byte(groupID),
                new ENG_Short(mDefaultRenderablePriority));
    }

    public void addRenderable(ENG_Renderable pRend, ENG_Byte groupID) {
        addRenderable(pRend, groupID, new ENG_Short(mDefaultRenderablePriority));
    }

    public void addRenderable(ENG_Renderable pRend) {
        addRenderable(pRend, new ENG_Byte(mDefaultQueueGroup),
                new ENG_Short(mDefaultRenderablePriority));
    }

    public void clear() {
        clear(false);
    }

    public void clear(boolean destroyPassMaps) {
//        for (Iterator<Entry<ENG_Byte, ENG_RenderQueueGroup>> it =
//             mGroups.entrySet().iterator(); it.hasNext(); ) {
//            it.next().getValue().clear(destroyPassMaps);
//        }
        for (ENG_RenderQueueGroup renderQueueGroup : mGroups.values()) {
            renderQueueGroup.clear(destroyPassMaps);
        }
        ENG_Pass.processPendingPassUpdates();
    }

    public Iterator<Entry<ENG_Byte, ENG_RenderQueueGroup>> _getQueueGroupIterator() {
        return mGroups.entrySet().iterator();
    }

    public byte getDefaultQueueGroup() {
        return mDefaultQueueGroup;
    }

    public void setDefaultQueueGroup(byte grp) {
        mDefaultQueueGroup = grp;
    }

    public short getDefaultRenderablePriority() {
        return mDefaultRenderablePriority;
    }

    public void setDefaultRenderablePriority(short priority) {
        mDefaultRenderablePriority = priority;
    }

    public ENG_RenderQueueGroup getQueueGroup(byte groupID) {
        ENG_RenderQueueGroup it = mGroups.get(new ENG_Byte(groupID));
        if (it == null) {
            it = new ENG_RenderQueueGroup(this,
                    mSplitPassesByLightingType,
                    mSplitNoShadowPasses,
                    mShadowCastersCannotBeReceivers);
            mGroups.put(new ENG_Byte(groupID), it);
        }
        return it;
    }

    public void setSplitPassesByLightingType(boolean split) {
        mSplitPassesByLightingType = split;
        for (Entry<ENG_Byte, ENG_RenderQueueGroup> engByteENGRenderQueueGroupEntry : mGroups.entrySet()) {
            engByteENGRenderQueueGroupEntry.getValue().setSplitPassesByLightingType(split);
        }
    }

    public boolean getSplitPassesByLightingType() {
        return mSplitPassesByLightingType;
    }

    public void setSplitNoShadowPasses(boolean split) {
        mSplitNoShadowPasses = split;
        for (Entry<ENG_Byte, ENG_RenderQueueGroup> engByteENGRenderQueueGroupEntry : mGroups.entrySet()) {
            engByteENGRenderQueueGroupEntry.getValue().setSplitNoShadowPasses(split);
        }
    }

    public boolean getSplitNoShadowPasses() {
        return mSplitNoShadowPasses;
    }

    public void setShadowCastersCannotBeReceivers(boolean ind) {
        mShadowCastersCannotBeReceivers = ind;
        for (Entry<ENG_Byte, ENG_RenderQueueGroup> engByteENGRenderQueueGroupEntry : mGroups.entrySet()) {
            engByteENGRenderQueueGroupEntry.getValue().setShadowCastersCannotBeReceivers(ind);
        }
    }

    public boolean getShadowCastersCannotBeReceivers() {
        return mShadowCastersCannotBeReceivers;
    }

    public void merge(ENG_RenderQueue rhs) {
        Iterator<Entry<ENG_Byte, ENG_RenderQueueGroup>> it =
                rhs._getQueueGroupIterator();
        while (it.hasNext()) {
            Entry<ENG_Byte, ENG_RenderQueueGroup> entry = it.next();
            ENG_Byte key = entry.getKey();
            ENG_RenderQueueGroup srcGroup = entry.getValue();
            ENG_RenderQueueGroup dstGroup = getQueueGroup(key.getValue());

            dstGroup.merge(srcGroup);
        }
    }

    public void processVisibleObject(ENG_MovableObject mo, ENG_Camera cam,
                                     boolean onlyShadowCasters, ENG_VisibleObjectsBoundsInfo visibleBounds) {
//        boolean receiveShadows = getQueueGroup(mo.getRenderQueueGroup()).getShadowsEnabled() && mo.getReceiveShadows();

        mo._notifyCurrentCamera(cam);

        if (mo.isVisible() && (!onlyShadowCasters || mo.getCastShadows())) {
            mo._updateRenderQueue(this);

            if (visibleBounds != null) {
                visibleBounds.merge(mo.getWorldBoundingBox(true),
                        mo.getWorldBoundingSphere(true), cam, /*receiveShadows*/false);
            }
        }
//        else if (mo.isVisible() && onlyShadowCasters && !mo.getCastShadows() &&
//                receiveShadows) {
//            visibleBounds.mergeNonRenderedButInFrustum(
//                    mo.getWorldBoundingSphere(true), cam);
//        }
    }
}
