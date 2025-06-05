/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.renderer.ENG_VertexAnimationTrack.TargetMode;
import headwayent.hotshotengine.renderer.ENG_VertexAnimationTrack.VertexAnimationType;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

public class ENG_Animation {

    public enum InterpolationMode {
        /**
         * Values are interpolated along straight lines.
         */
        IM_LINEAR,
        /**
         * Values are interpolated along a spline, resulting in smoother changes in direction.
         */
        IM_SPLINE
    }

    public enum RotationInterpolationMode {
        /**
         * Values are interpolated linearly. This is faster but does not
         * necessarily give a completely accurate result.
         */
        RIM_LINEAR,
        /**
         * Values are interpolated spherically. This is more accurate but
         * has a higher cost.
         */
        RIM_SPHERICAL
    }

    protected final TreeMap<ENG_Short, ENG_NodeAnimationTrack> mNodeTrackList =
            new TreeMap<>();
    protected final TreeMap<ENG_Short, ENG_NumericAnimationTrack> mNumericTrackList =
            new TreeMap<>();
    protected final TreeMap<ENG_Short, ENG_VertexAnimationTrack> mVertexTrackList =
            new TreeMap<>();
    protected final String mName;
    protected float mLength;

    protected InterpolationMode mInterpolationMode = msDefaultInterpolationMode;
    protected RotationInterpolationMode mRotationInterpolationMode =
            msDefaultRotationInterpolationMode;

    protected static InterpolationMode msDefaultInterpolationMode =
            InterpolationMode.IM_LINEAR;
    protected static RotationInterpolationMode
            msDefaultRotationInterpolationMode = RotationInterpolationMode.RIM_LINEAR;

    protected final ArrayList<ENG_Float> mKeyFrameTimes = new ArrayList<>();
    protected boolean mKeyFrameTimesDirty;

    public ENG_Animation(String name, float len) {
        
        mName = name;
        mLength = len;
    }

    public String getName() {
        return mName;
    }

    public float getLength() {
        return mLength;
    }

    public void setLength(float len) {
        mLength = len;
    }

    public int getNumNodeTracks() {
        return mNodeTrackList.size();
    }

    public ENG_NodeAnimationTrack getNodeTrack(short handle) {
        ENG_NodeAnimationTrack track = mNodeTrackList.get(new ENG_Short(handle));
        if (track == null) {
            throw new IllegalArgumentException(handle +
                    " is an invalid handle for node");
        }
        return track;
    }

    public int getNumNumericTracks() {
        return mNumericTrackList.size();
    }

    public ENG_NumericAnimationTrack getNumericTrack(short handle) {
        ENG_NumericAnimationTrack track = mNumericTrackList.get(new ENG_Short(handle));
        if (track == null) {
            throw new IllegalArgumentException(handle +
                    " is an invalid handle for numeric track");
        }
        return track;
    }

    public int getNumVertexTrack() {
        return mVertexTrackList.size();
    }

    public ENG_VertexAnimationTrack getVertexTrack(short handle) {
        ENG_VertexAnimationTrack track = mVertexTrackList.get(new ENG_Short(handle));
        if (track == null) {
            throw new IllegalArgumentException(handle +
                    " is an invalid handle for vertex track");
        }
        return track;
    }

    public boolean hasNodeTrack(short handle) {
        return mNodeTrackList.containsKey(new ENG_Short(handle));
    }

    public boolean hasNumericTrack(short handle) {
        return mNumericTrackList.containsKey(new ENG_Short(handle));
    }

    public boolean hasVertexTrack(short handle) {
        return mVertexTrackList.containsKey(new ENG_Short(handle));
    }

    public void _keyFrameListChanged() {
        mKeyFrameTimesDirty = true;
    }

    public void destroyNodeTrack(short handle) {
        ENG_NodeAnimationTrack remove = mNodeTrackList.remove(new ENG_Short(handle));
        if (remove != null) {
            _keyFrameListChanged();
        }
    }

    public void destroyNumericTrack(short handle) {
        ENG_NumericAnimationTrack remove = mNumericTrackList.remove(new ENG_Short(handle));
        if (remove != null) {
            _keyFrameListChanged();
        }
    }

    public void destroyVertexTrack(short handle) {
        ENG_VertexAnimationTrack remove = mVertexTrackList.remove(new ENG_Short(handle));
        if (remove != null) {
            _keyFrameListChanged();
        }
    }

    public void destroyAllNodeTracks() {
        mNodeTrackList.clear();
        _keyFrameListChanged();
    }

    public void destroyAllNumericTracks() {
        mNumericTrackList.clear();
        _keyFrameListChanged();
    }

    public void destroyAllVertexTracks() {
        mVertexTrackList.clear();
        _keyFrameListChanged();
    }

    public void destroyAllTracks() {
        destroyAllNodeTracks();
        destroyAllNumericTracks();
        destroyAllVertexTracks();
    }

    public ENG_NodeAnimationTrack createNodeTrack(short handle) {
        if (hasNodeTrack(handle)) {
            throw new IllegalArgumentException(handle +
                    " node track already exists");
        }
        ENG_NodeAnimationTrack track = new ENG_NodeAnimationTrack(this, handle);
        mNodeTrackList.put(new ENG_Short(handle), track);
        return track;
    }

    public ENG_NumericAnimationTrack createNumericTrack(short handle) {
        if (hasNumericTrack(handle)) {
            throw new IllegalArgumentException(handle +
                    " numeric track already exists");
        }
        ENG_NumericAnimationTrack track = new ENG_NumericAnimationTrack(this, handle);
        mNumericTrackList.put(new ENG_Short(handle), track);
        return track;
    }

    public ENG_VertexAnimationTrack createVertexTrack(short handle,
                                                      VertexAnimationType animType) {
        if (hasVertexTrack(handle)) {
            throw new IllegalArgumentException(handle +
                    " vertex track already exists");
        }
        ENG_VertexAnimationTrack track = new ENG_VertexAnimationTrack(this, handle, animType);
        mVertexTrackList.put(new ENG_Short(handle), track);
        return track;
    }

    public ENG_VertexAnimationTrack createVertexTrack(short handle,
                                                      ENG_VertexData data, VertexAnimationType animType) {
        if (hasVertexTrack(handle)) {
            throw new IllegalArgumentException(handle +
                    " vertex track already exists");
        }
        ENG_VertexAnimationTrack track = new ENG_VertexAnimationTrack(this, handle, animType, data);
        mVertexTrackList.put(new ENG_Short(handle), track);
        return track;
    }

    public void apply(float timePos) {
        apply(timePos, 1.0f, 1.0f);
    }

    public void apply(float timePos, float weight) {
        apply(timePos, weight, 1.0f);
    }

    public void apply(float timePos, float weight, float scale) {
        // Calculate time index for fast keyframe search
        ENG_TimeIndex timeIndex = _getTimeIndex(timePos);

        for (ENG_NodeAnimationTrack track : mNodeTrackList.values()) {
            track.apply(timeIndex, weight, scale);
        }
        for (ENG_NumericAnimationTrack track : mNumericTrackList.values()) {
            track.apply(timeIndex, weight, scale);
        }
        for (ENG_VertexAnimationTrack track : mVertexTrackList.values()) {
            track.apply(timeIndex, weight, scale);
        }
    }

    public void applyToNode(ENG_Node node, float timePos) {
        applyToNode(node, timePos, 1.0f, 1.0f);
    }

    public void applyToNode(ENG_Node node, float timePos, float weight) {
        applyToNode(node, timePos, weight, 1.0f);
    }

    public void applyToNode(
            ENG_Node node, float timePos, float weight, float scale) {
        ENG_TimeIndex timeIndex = _getTimeIndex(timePos);
        for (ENG_NodeAnimationTrack track : mNodeTrackList.values()) {
            track.applyToNode(node, timeIndex, weight, scale);
        }
    }

    public void apply(
            ENG_Skeleton skel, float timePos, float weight, float scale) {
        ENG_TimeIndex timeIndex = _getTimeIndex(timePos);
        for (Entry<ENG_Short, ENG_NodeAnimationTrack> track :
                mNodeTrackList.entrySet()) {
            ENG_Bone b = skel.getBone(track.getKey().getValue());
            track.getValue().applyToNode(b, timeIndex, weight, scale);
        }
    }

    public void apply(ENG_Skeleton skel, float timePos, float weight,
                      ArrayList<ENG_Float> blendMask, float scale) {
        ENG_TimeIndex timeIndex = _getTimeIndex(timePos);
        for (Entry<ENG_Short, ENG_NodeAnimationTrack> track :
                mNodeTrackList.entrySet()) {
            ENG_Bone b = skel.getBone(track.getKey().getValue());
            track.getValue().applyToNode(
                    b,
                    timeIndex,
                    blendMask.get(b.getHandle()).getValue() * weight,
                    scale);
        }
    }

    /** @noinspection deprecation*/
    public void apply(ENG_Entity entity, float timePos, float weight,
                      boolean software, boolean hardware) {
        ENG_TimeIndex timeIndex = _getTimeIndex(timePos);
        for (Entry<ENG_Short, ENG_VertexAnimationTrack> entry :
                mVertexTrackList.entrySet()) {
            short handle = entry.getKey().getValue();
            ENG_VertexAnimationTrack track = entry.getValue();

            ENG_VertexData swVertexData;
            ENG_VertexData hwVertexData;
            ENG_VertexData origVertexData;
            boolean first;

            if (handle == 0) {
                first = !entity._getBuffersMarkedForAnimation();
                swVertexData = entity._getSoftwareVertexAnimVertexData();
                hwVertexData = entity._getHardwareVertexAnimVertexData();
                origVertexData = entity.getMesh().sharedVertexData;
                entity._markBuffersUsedForAnimation();
            } else {
                ENG_SubEntity s = entity.getSubEntity(handle - 1);
                if (!s.isVisible()) {
                    continue;
                }
                first = s._getBuffersMarkedForAnimation();
                swVertexData = s._getSoftwareVertexAnimVertexData();
                hwVertexData = s._getHardwareVertexAnimVertexData();
                origVertexData = s.getSubMesh().vertexData;
                s._markBuffersUsedForAnimation();
            }

            if (software) {
                if (first && track.getAnimationType() ==
                        VertexAnimationType.VAT_POSE) {
                    ENG_VertexElement origElem =
                            origVertexData.vertexDeclaration
                                    .findElementBySemantic(
                                            VertexElementSemantic.VES_POSITION, 0);
                    ENG_VertexElement destElem =
                            swVertexData.vertexDeclaration
                                    .findElementBySemantic(
                                            VertexElementSemantic.VES_POSITION, 0);
                    ENG_HardwareVertexBuffer origBuf =
                            origVertexData.vertexBufferBinding
                                    .getBuffer(origElem.getSource());
                    ENG_HardwareVertexBuffer destBuf =
                            swVertexData.vertexBufferBinding
                                    .getBuffer(destElem.getSource());
                    destBuf.copyData(origBuf, 0, 0, origBuf.sizeInBytes, true);
                }
                track.setTargetMode(TargetMode.TM_SOFTWARE);
                track.applyToVertexData(
                        swVertexData,
                        timeIndex,
                        weight,
                        entity.getMesh().getPoseList());
            }
            if (hardware) {
                track.setTargetMode(TargetMode.TM_HARDWARE);
                track.applyToVertexData(
                        hwVertexData,
                        timeIndex,
                        weight,
                        entity.getMesh().getPoseList());
            }
        }
    }

    public void setInterpolationMode(InterpolationMode mode) {
        mInterpolationMode = mode;
    }

    public InterpolationMode getInterpolationMode() {
        return mInterpolationMode;
    }

    public void setRotationInterpolationMode(RotationInterpolationMode im) {
        mRotationInterpolationMode = im;
    }

    public RotationInterpolationMode getRotationInterpolationMode() {
        return mRotationInterpolationMode;
    }

    public static void setDefaultInterpolationMode(InterpolationMode im) {
        msDefaultInterpolationMode = im;
    }

    public static InterpolationMode getDefaultInterpolationMode() {
        return msDefaultInterpolationMode;
    }

    public static void setDefaultRotationInterpolationMode(RotationInterpolationMode im) {
        msDefaultRotationInterpolationMode = im;
    }

    public static RotationInterpolationMode getDefaultRotationInterpolationMode() {
        return msDefaultRotationInterpolationMode;
    }

    public ENG_TimeIndex _getTimeIndex(float timePos) {
        if (mKeyFrameTimesDirty) {
            buildKeyFrameTimeList();
        }

        float totalAnimationLength = mLength;

        while (timePos > totalAnimationLength && totalAnimationLength > 0.0f) {
            timePos -= totalAnimationLength;
        }

        int i = 0;
        for (ENG_Float time : mKeyFrameTimes) {
            if (time.getValue() > timePos) {
                break;
            }
            ++i;
        }
        return new ENG_TimeIndex(timePos, i);
    }

    protected void buildKeyFrameTimeList() {
        

        for (ENG_NodeAnimationTrack track : mNodeTrackList.values()) {
            track._collectKeyFrameTimes(mKeyFrameTimes);
        }
        for (ENG_NumericAnimationTrack track : mNumericTrackList.values()) {
            track._collectKeyFrameTimes(mKeyFrameTimes);
        }
        for (ENG_VertexAnimationTrack track : mVertexTrackList.values()) {
            track._collectKeyFrameTimes(mKeyFrameTimes);
        }
        for (ENG_NodeAnimationTrack track : mNodeTrackList.values()) {
            track._buildKeyFrameIndexMap(mKeyFrameTimes);
        }
        for (ENG_NumericAnimationTrack track : mNumericTrackList.values()) {
            track._buildKeyFrameIndexMap(mKeyFrameTimes);
        }
        for (ENG_VertexAnimationTrack track : mVertexTrackList.values()) {
            track._buildKeyFrameIndexMap(mKeyFrameTimes);
        }

        // Reset dirty flag
        mKeyFrameTimesDirty = false;
    }

    public ENG_NodeAnimationTrack createNodeTrack(short mHandle,
                                                  ENG_Node mTargetNode) {
        
        ENG_NodeAnimationTrack track = createNodeTrack(mHandle);
        track.setAssociatedNode(mTargetNode);
        return track;
    }

    public TreeMap<ENG_Short, ENG_NodeAnimationTrack> _getNodeTrackList() {
        return mNodeTrackList;
    }

    public TreeMap<ENG_Short, ENG_NumericAnimationTrack> _getNumericTrackList() {
        return mNumericTrackList;
    }

    public TreeMap<ENG_Short, ENG_VertexAnimationTrack> getVertexTrackList() {
        return mVertexTrackList;
    }

    public ENG_Animation clone(String name) {
        ENG_Animation animation = new ENG_Animation(name, mLength);
        animation.mInterpolationMode = mInterpolationMode;
        animation.mRotationInterpolationMode = mRotationInterpolationMode;

        for (ENG_NodeAnimationTrack track : mNodeTrackList.values()) {
            track._clone(animation);
        }

        for (ENG_NumericAnimationTrack track : mNumericTrackList.values()) {
            track._clone(animation);
        }

        for (ENG_VertexAnimationTrack track : mVertexTrackList.values()) {
            track._clone(animation);
        }

        animation._keyFrameListChanged();
        return animation;
    }

    public void optimise() {
        optimise(true);
    }

    protected void optimise(boolean discardIdentityTracks) {
        optimiseNodeTracks(discardIdentityTracks);
        optimiseVertexTracks();
    }

    protected void optimiseNodeTracks(boolean discardIdentityTracks) {
        ArrayList<ENG_NodeAnimationTrack> list =
                new ArrayList<>();
        for (ENG_NodeAnimationTrack track : mNodeTrackList.values()) {
            if (discardIdentityTracks && !track.hasNonZeroKeyFrames()) {
                list.add(track);
            } else {
                track.optimise();
            }
        }

        for (ENG_NodeAnimationTrack track : list) {
            destroyNodeTrack(track.getHandle());
        }
    }

    protected void optimiseVertexTracks() {
        ArrayList<ENG_VertexAnimationTrack> list =
                new ArrayList<>();
        for (ENG_VertexAnimationTrack track : mVertexTrackList.values()) {
            if (!track.hasNonZeroKeyFrames()) {
                list.add(track);
            } else {
                track.optimise();
            }
        }

        for (ENG_VertexAnimationTrack track : list) {
            destroyVertexTrack(track.getHandle());
        }
    }

    public void _collectIdentityNodeTracks(TreeSet<ENG_Short> tracks) {
        for (Entry<ENG_Short, ENG_NodeAnimationTrack> entry :
                mNodeTrackList.entrySet()) {
            if (entry.getValue().hasNonZeroKeyFrames()) {
                tracks.remove(entry.getKey());
            }
        }
    }

    public void _destroyNodeTracks(TreeSet<ENG_Short> tracks) {
        for (ENG_Short handle : tracks) {
            destroyNodeTrack(handle.getValue());
        }
    }

}
