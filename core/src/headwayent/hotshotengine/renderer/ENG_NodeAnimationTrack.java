/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.renderer.ENG_Animation.InterpolationMode;
import headwayent.hotshotengine.renderer.ENG_Animation.RotationInterpolationMode;

import java.util.ArrayList;

public class ENG_NodeAnimationTrack extends ENG_AnimationTrack {

    protected static class Splines {
        public final ENG_SimpleSpline positionSpline = new ENG_SimpleSpline();
        public final ENG_SimpleSpline scaleSpline = new ENG_SimpleSpline();
        public final ENG_RotationalSpline rotationSpline = new ENG_RotationalSpline();
    }

    protected ENG_Node mTargetNode;
    protected Splines mSplines;
    protected boolean mSplineBuildNeeded;
    /// Defines if rotation is done using shortest path
    protected boolean mUseShortestRotationPath = true;

    public ENG_NodeAnimationTrack(ENG_Animation parent, short handle) {
        super(parent, handle);
        
    }

    @Override
    protected ENG_KeyFrame createKeyFrameImpl(float time) {
        
        return new ENG_TransformKeyFrame(this, time);
    }

    protected void buildInterpolationSplines() {
        if (mSplines == null) {
            mSplines = new Splines();
        }

        // Don't calc automatically, do it on request at the end
        mSplines.positionSpline.setAutoCalculate(false);
        mSplines.rotationSpline.setAutoCalculate(false);
        mSplines.scaleSpline.setAutoCalculate(false);

        mSplines.positionSpline.clear();
        mSplines.rotationSpline.clear();
        mSplines.scaleSpline.clear();

        for (ENG_KeyFrame key : mKeyFrames) {
            ENG_TransformKeyFrame kf = (ENG_TransformKeyFrame) key;
            mSplines.positionSpline.addPoint(
                    new ENG_Vector3D(kf.getTranslate().x,
                            kf.getTranslate().y,
                            kf.getTranslate().z));
            mSplines.rotationSpline.addPoint(kf.getRotation());
            mSplines.scaleSpline.addPoint(new ENG_Vector3D(kf.getScale().x,
                    kf.getScale().y,
                    kf.getScale().z));
        }

        mSplines.positionSpline.recalcTangents();
        mSplines.rotationSpline.recalcTangents();
        mSplines.scaleSpline.recalcTangents();


        mSplineBuildNeeded = false;
    }

    @Override
    public void getInterpolatedKeyFrame(ENG_TimeIndex time, ENG_KeyFrame kf) {
        

        if (mListener != null) {
            if (mListener.getInterpolatedKeyFrame(this, time, kf)) {
                return;
            }
        }

        ENG_TransformKeyFrame kret = (ENG_TransformKeyFrame) kf;
        KeyFramesAtTime kft = new KeyFramesAtTime();
        float t = getKeyFramesAtTime(time, kft);
        ENG_TransformKeyFrame k1 = (ENG_TransformKeyFrame) kft.k1;
        ENG_TransformKeyFrame k2 = (ENG_TransformKeyFrame) kft.k2;
        short firstKeyIndex = kft.firstKeyIndex;

        if (t == 0.0f) {
            kret.setTranslate(k1.getTranslate());
            kret.setRotation(k1.getRotation());
            kret.setScale(k1.getScale());
        } else {
            InterpolationMode im = mParent.getInterpolationMode();
            RotationInterpolationMode rim =
                    mParent.getRotationInterpolationMode();
            ENG_Vector4D base;

            switch (im) {
                case IM_LINEAR: {
                    if (rim == RotationInterpolationMode.RIM_LINEAR) {
                        kret.setRotation(ENG_Quaternion.nlerp(t, k1.getRotation(),
                                k2.getRotation(), mUseShortestRotationPath));
                    } else if (rim == RotationInterpolationMode.RIM_SPHERICAL) {
                        kret.setRotation(ENG_Quaternion.slerp(t, k1.getRotation(),
                                k2.getRotation(), mUseShortestRotationPath));
                    }

                    base = k1.getTranslate();
                    kret.setTranslate(base.addAsPt(k2.getTranslate().subAsPt(base).mulAsPt(t)));

                    base = k1.getScale();
                    kret.setScale(base.addAsPt(k2.getScale().subAsPt(base).mulAsPt(t)));
                }
                break;
                case IM_SPLINE: {
                    if (mSplineBuildNeeded) {
                        buildInterpolationSplines();
                    }

                    kret.setRotation(mSplines.rotationSpline.interpolate(firstKeyIndex, t, mUseShortestRotationPath));

                    ENG_Vector3D posInt = mSplines.positionSpline.interpolate(firstKeyIndex, t);
                    kret.setTranslate(new ENG_Vector4D(posInt.x, posInt.y, posInt.z, 1.0f));

                    ENG_Vector3D sclInt = mSplines.scaleSpline.interpolate(firstKeyIndex, t);
                    kret.setScale(new ENG_Vector4D(sclInt.x, sclInt.y, sclInt.z, 1.0f));
                }
                break;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public void apply(ENG_TimeIndex time, float weight, float scale) {
        

        applyToNode(mTargetNode, time, weight, scale);
    }

    public void setAssociatedNode(ENG_Node node) {
        mTargetNode = node;

    }

    public ENG_Node getAssociatedNode() {
        return mTargetNode;
    }

    public void setUseShortestRotationPath(boolean b) {
        mUseShortestRotationPath = b;
    }

    public boolean getUseShortestRotationPath() {
        return mUseShortestRotationPath;
    }

    @Override
    public void _keyFrameDataChanged() {
        
        mSplineBuildNeeded = true;
    }

    public ENG_TransformKeyFrame getNodeKeyFrame(short index) {
        return (ENG_TransformKeyFrame) getKeyFrame(index);
    }

    public void applyToNode(ENG_Node node, ENG_TimeIndex timeIndex,
                            float weight, float scale) {
        if (mKeyFrames.isEmpty() || weight == 0.0f || scale == 0.0f) {
            return;
        }

        ENG_TransformKeyFrame kf = new ENG_TransformKeyFrame(null, timeIndex.getTimePos());
        getInterpolatedKeyFrame(timeIndex, kf);

        ENG_Vector4D translate = kf.getTranslate().mulAsPt(weight * scale);
        node.translate(translate);

        ENG_Quaternion rotate = new ENG_Quaternion(true);

        RotationInterpolationMode rim = mParent.getRotationInterpolationMode();
        if (rim == RotationInterpolationMode.RIM_LINEAR) {
            ENG_Quaternion.nlerp(weight, ENG_Math.QUAT_IDENTITY,
                    kf.getRotation(), mUseShortestRotationPath, rotate);
        } else {
            ENG_Quaternion.slerp(weight, ENG_Math.QUAT_IDENTITY,
                    kf.getRotation(), mUseShortestRotationPath, rotate);
        }

        node.rotate(rotate);

        ENG_Vector4D scaleV = kf.getScale();
        ENG_Vector4D scl = null;
        if (!scaleV.equals(ENG_Math.PT4_UNIT)) {
            if (scale != 1.0f) {
                scl = ENG_Math.PT4_UNIT.addAsPt(
                        scaleV.subAsPt(ENG_Math.PT4_UNIT).mulAsPt(scale));
            } else if (weight != 1.0f) {
                scl = ENG_Math.PT4_UNIT.addAsPt(
                        scaleV.subAsPt(ENG_Math.PT4_UNIT).mulAsPt(weight));
            }
        }
        if (scl != null) {
            node.scale(scl);
        }
    }

    public ENG_NodeAnimationTrack _clone(ENG_Animation newParent) {
        ENG_NodeAnimationTrack track =
                newParent.createNodeTrack(mHandle, mTargetNode);
        track.mUseShortestRotationPath = mUseShortestRotationPath;
        populateClone(track);
        return track;

    }

    public ENG_TransformKeyFrame createNodeKeyFrame(float timePos) {
        return (ENG_TransformKeyFrame) createKeyFrame(timePos);
    }

    @Override
    public boolean hasNonZeroKeyFrames() {
        
        for (ENG_KeyFrame key : mKeyFrames) {
            ENG_TransformKeyFrame kft = (ENG_TransformKeyFrame) key;
            float tolerance = 1e-3f;
            float angleAxisRad =
                    kft.getRotation().toAngleAxisRad(new ENG_Vector3D());
            if (!kft.getTranslate().positionClose(ENG_Math.PT4_ZERO, tolerance) ||
                    !kft.getScale().positionClose(ENG_Math.PT4_UNIT, tolerance) ||
                    ENG_Float.compareTo(angleAxisRad, 0.0f, tolerance) !=
                            ENG_Utility.COMPARE_EQUAL_TO) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void optimise() {
        
        ENG_Vector4D lasttrans = ENG_Math.PT4_ZERO;
        ENG_Vector4D lastscale = ENG_Math.PT4_UNIT;
        ENG_Quaternion lastrotation = ENG_Math.QUAT_IDENTITY;
        float tolerance = 1e-3f;
        int i = 0;
        ArrayList<ENG_Short> removeList = new ArrayList<>();
        int dup = 0;
        for (ENG_KeyFrame key : mKeyFrames) {
            ENG_TransformKeyFrame kft = (ENG_TransformKeyFrame) key;
            if (i != 0 &&
                    kft.getTranslate().equals(lasttrans) &&
                    kft.getScale().equals(lastscale) &&
                    kft.getRotation().equals(lastrotation)) {
                ++dup;
                if (dup == 4) {
                    removeList.add(new ENG_Short((short) (i - 2)));
                    --dup;
                }
            } else {
                dup = 0;
                lasttrans = kft.getTranslate();
                lastscale = kft.getScale();
                lastrotation = kft.getRotation();
            }
            ++i;
        }
        for (ENG_Short s : removeList) {
            removeKeyFrame(s.getValue());
        }
    }

}
