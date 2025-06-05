/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Serializer;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.resource.ENG_Resource.RandomAccessMode;

public class ENG_SkeletonSerializer extends ENG_Serializer {

    private enum SkeletonChunkID {
        SKELETON_HEADER(0x1000),
        // char* version : Version number check
        SKELETON_BONE(0x2000),
        // Repeating section defining each bone in the system.
        // Bones are assigned indexes automatically based on their order of
        // declaration
        // starting with 0.

        // char* name : name of the bone
        // unsigned short handle : handle of the bone, should be contiguous &
        // start at 0
        // Vector3 position : position of this bone relative to parent
        // Quaternion orientation : orientation of this bone relative to parent
        // Vector3 scale : scale of this bone relative to parent

        SKELETON_BONE_PARENT(0x3000),
        // Record of the parent of a single bone, used to build the node tree
        // Repeating section, listed in Bone Index order, one per Bone

        // unsigned short handle : child bone
        // unsigned short parentHandle : parent bone

        SKELETON_ANIMATION(0x4000),
        // A single animation for this skeleton

        // char* name : Name of the animation
        // float length : Length of the animation in seconds

        SKELETON_ANIMATION_TRACK(0x4100),
        // A single animation track (relates to a single bone)
        // Repeating section (within SKELETON_ANIMATION)

        // unsigned short boneIndex : Index of bone to apply to

        SKELETON_ANIMATION_TRACK_KEYFRAME(0x4110),
        // A single keyframe within the track
        // Repeating section

        // float time : The time position (seconds)
        // Quaternion rotate : Rotation to apply at this keyframe
        // Vector3 translate : Translation to apply at this keyframe
        // Vector3 scale : Scale to apply at this keyframe
        SKELETON_ANIMATION_LINK(0x5000);
        // Link to another skeleton, to re-use its animations

        // char* skeletonName : name of skeleton to get animations from
        // float scale : scale to apply to trans/scale keys

        private final short id;

        SkeletonChunkID(int id) {
            this.id = (short) id;
        }

        public short getId() {
            return id;
        }

    }

    private static final long STREAM_OVERHEAD_SIZE = ENG_Short.SIZE_IN_BYTES
            + ENG_Integer.SIZE_IN_BYTES;

    public ENG_SkeletonSerializer() {
        
        mVersion = "[Serializer_v1.10]";
    }

    public void importSkeleton(String filename, ENG_Skeleton skel) {
        openFile(filename, RandomAccessMode.READ_WRITE);

        determineEndianness();

        readFileHeader();

        while (buf.hasRemaining()) {
            short readChunk = readChunk();
            if (readChunk == SkeletonChunkID.SKELETON_BONE.getId()) {
                readBone(skel);
            } else if (readChunk == SkeletonChunkID.SKELETON_BONE_PARENT
                    .getId()) {
                readBoneParent(skel);
            } else if (readChunk == SkeletonChunkID.SKELETON_ANIMATION.getId()) {
                readAnimation(skel);
            } else if (readChunk == SkeletonChunkID.SKELETON_ANIMATION_LINK
                    .getId()) {
                readSkeletonAnimationLink(skel);
            }
        }

        skel.setBindingsPose();

        closeFile();
    }

    private void readBone(ENG_Skeleton skel) {

        String name = readString();
        short handle = readShorts(1)[0];
        ENG_Bone bone = skel.createBone(name, handle);
        bone.setPosition(readVector3());
        bone.setOrientation(readQuat());
        if (mCurrentstreamLen > calcBoneSizeWithoutScale(skel, bone)) {
            bone.setScale(readVector3());
        }
    }

    private int calcBoneSizeWithoutScale(ENG_Skeleton skel, ENG_Bone bone) {

        int size = (int) STREAM_OVERHEAD_SIZE;
        size += ENG_Short.SIZE_IN_BYTES;
        size += ENG_Float.SIZE_IN_BYTES * 3;
        size += ENG_Float.SIZE_IN_BYTES * 4;
        return size;
    }

    private void readBoneParent(ENG_Skeleton skel) {


        short[] readShorts = readShorts(2);
        short childHandle = readShorts[0];
        short parentHandle = readShorts[1];
        ENG_Bone parentBone = skel.getBone(parentHandle);
        ENG_Bone childBone = skel.getBone(childHandle);
        parentBone.addChild(childBone);
    }

    private void readAnimation(ENG_Skeleton skel) {


        String name = readString();
        float len = readFloats(1)[0];

        ENG_Animation anim = skel.createAnimation(name, len);

        if (buf.hasRemaining()) {
            short readChunk = readChunk();
            while (buf.hasRemaining() &&
                    readChunk == SkeletonChunkID.SKELETON_ANIMATION_TRACK.getId()) {
                readAnimationTrack(anim, skel);
                if (buf.hasRemaining()) {
                    readChunk = readChunk();
                }
            }
            if (buf.hasRemaining()) {
                buf.position((int) (buf.position() - STREAM_OVERHEAD_SIZE));
            }
        }
    }

    private void readAnimationTrack(ENG_Animation anim, ENG_Skeleton skel) {

        short handle = readShorts(1)[0];
        ENG_Bone targetBone = skel.getBone(handle);

        ENG_NodeAnimationTrack track = anim.createNodeTrack(handle, targetBone);

        if (buf.hasRemaining()) {
            short readChunk = readChunk();
            while (buf.hasRemaining() &&
                    readChunk == SkeletonChunkID.SKELETON_ANIMATION_TRACK_KEYFRAME.getId()) {
                readKeyFrame(track, skel);
                if (buf.hasRemaining()) {
                    readChunk = readChunk();
                }
            }
            if (buf.hasRemaining()) {
                buf.position((int) (buf.position() - STREAM_OVERHEAD_SIZE));
            }
        }
    }

    private void readKeyFrame(ENG_NodeAnimationTrack track, ENG_Skeleton skel) {

        ENG_TransformKeyFrame kf = track.createNodeKeyFrame(readFloats(1)[0]);

        kf.setRotation(readQuat());
        ENG_Vector3D tr = readVector3();
        kf.setTranslate(new ENG_Vector4D(tr.x, tr.y, tr.z, 1.0f));
        if (mCurrentstreamLen > calcKeyFrameSizeWithoutScale(skel, track)) {
            ENG_Vector3D sc = readVector3();
            kf.setScale(new ENG_Vector4D(sc.x, sc.y, sc.z, 1.0f));
        }
    }

    private int calcKeyFrameSizeWithoutScale(ENG_Skeleton skel,
                                             ENG_NodeAnimationTrack track) {

        int size = (int) STREAM_OVERHEAD_SIZE;
        size += ENG_Float.SIZE_IN_BYTES;
        size += ENG_Float.SIZE_IN_BYTES * 4;
        size += ENG_Float.SIZE_IN_BYTES * 3;
        return size;
    }

    private void readSkeletonAnimationLink(ENG_Skeleton skel) {


        String name = readString();
        float scale = readFloats(1)[0];
        skel.addLinkedSkeletonAnimationSource(name, scale);
    }

}
