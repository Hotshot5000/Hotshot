/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.ArrayList;
import java.util.Iterator;

public class ENG_VertexPoseKeyFrame extends ENG_KeyFrame {

    public ENG_VertexPoseKeyFrame(ENG_AnimationTrack parent, float time) {
        super(parent, time);

    }

    public static class PoseRef {
        public final short poseIndex;
        public float influence;

        public PoseRef(short poseIndex, float influence) {
            this.poseIndex = poseIndex;
            this.influence = influence;
        }
    }

    protected final ArrayList<PoseRef> mPoseRefs =
            new ArrayList<>();

    public void addPoseReference(short poseIndex, float influence) {
        mPoseRefs.add(new PoseRef(poseIndex, influence));
    }

    public void updatePoseReference(short poseIndex, float influence) {
        for (PoseRef pr : mPoseRefs) {
            if (pr.poseIndex == poseIndex) {
                pr.influence = influence;
                return;
            }
        }
        addPoseReference(poseIndex, influence);
    }

    public void removePoseReference(short poseIndex) {
        for (Iterator<PoseRef> it = mPoseRefs.iterator(); it.hasNext(); ) {
            PoseRef pr = it.next();
            if (pr.poseIndex == poseIndex) {
                it.remove();
                return;
            }
        }
    }

    public void removeAllPoseReferences() {
        mPoseRefs.clear();
    }

    public ArrayList<PoseRef> getPoseReferences() {
        return mPoseRefs;
    }

    public Iterator<PoseRef> getPoseReferencesIterator() {
        return mPoseRefs.iterator();
    }

    @Override
    public ENG_KeyFrame _clone(ENG_AnimationTrack newParent) {

        ENG_VertexPoseKeyFrame frame =
                new ENG_VertexPoseKeyFrame(newParent, mTime);
        frame.mPoseRefs.addAll(mPoseRefs);
        return frame;
    }

}
