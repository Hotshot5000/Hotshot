/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;

public class ENG_Bone extends ENG_Node {

    /// The numeric handle of this bone
    protected short mHandle;

    /**
     * Bones set as manuallyControlled are not reseted in Skeleton::reset()
     */
    protected boolean mManuallyControlled;


    /// Pointer back to creator, for child creation (not smart ptr so child does not preserve parent)
    protected ENG_Skeleton mCreator;

    /// The inversed derived scale of the bone in the binding pose
    protected final ENG_Vector4D mBindDerivedInverseScale = new ENG_Vector4D(true);
    /// The inversed derived orientation of the bone in the binding pose
    protected final ENG_Quaternion mBindDerivedInverseOrientation =
            new ENG_Quaternion();
    /// The inversed derived position of the bone in the binding pose
    protected final ENG_Vector4D mBindDerivedInversePosition = new ENG_Vector4D(true);

    public ENG_Bone(short handle, ENG_Skeleton creator) {
        this(handle, ENG_SceneManager.SceneMemoryMgrTypes.SCENE_DYNAMIC, creator);
    }

    public ENG_Bone(short handle, ENG_SceneManager.SceneMemoryMgrTypes type, ENG_Skeleton creator) {
        super(type);
        setup(handle, creator);
    }

    public ENG_Bone(String name, short handle, ENG_Skeleton creator) {
        this(name, ENG_SceneManager.SceneMemoryMgrTypes.SCENE_DYNAMIC, handle, creator);
    }

    public ENG_Bone(String name, ENG_SceneManager.SceneMemoryMgrTypes type, short handle, ENG_Skeleton creator) {
        super(name, type);
        
        setup(handle, creator);
    }

    private void setup(short handle, ENG_Skeleton creator) {
        mHandle = handle;
        mCreator = creator;
    }

    public ENG_Node createChild(short handle) {
        return createChild(handle, ENG_Math.PT4_ZERO, ENG_Math.QUAT_IDENTITY);
    }

    public ENG_Node createChild(short handle, ENG_Vector4D translate,
                                ENG_Quaternion rotate) {
        
        ENG_Bone bone = mCreator.createBone(handle);
        bone.translate(translate);
        bone.rotate(rotate);
        addChild(bone);
        return bone;
    }

    public short getHandle() {
        return mHandle;
    }

    public void setBindingPose() {
        setInitialState();

        _getDerivedPosition(mBindDerivedInversePosition);
        mBindDerivedInversePosition.invertInPlace();
        ENG_Math.PT4_UNIT.div(_getDerivedScale(), mBindDerivedInverseScale);
        _getDerivedOrientation(mBindDerivedInverseOrientation);
        mBindDerivedInverseOrientation.inverse();
    }

    public void reset() {
        resetInitialState();
    }

    public void setManuallyControlled(boolean manuallyControlled) {
        mManuallyControlled = manuallyControlled;
        mCreator._notifyManualBoneStateChange(this);
    }

    public boolean isManuallyControlled() {
        return mManuallyControlled;
    }

    public void _getOffsetTransform(ENG_Matrix4 m) {
        ENG_Vector4D scale =
                _getDerivedScale().mulAsPt(mBindDerivedInverseScale);
        ENG_Quaternion orientation =
                _getDerivedOrientation().mulRet(mBindDerivedInverseOrientation);
        ENG_Vector4D position =
                _getDerivedPosition().addAsPt(
                        orientation.mul(
                                scale.mulAsPt(mBindDerivedInversePosition)));
        m.makeTransform(position, scale, orientation);
    }

    public ENG_Vector4D _getBindingPoseInverseScale() {
        return mBindDerivedInverseScale;
    }

    public ENG_Vector4D _getBindingPoseInversePosition() {
        return mBindDerivedInversePosition;
    }

    public ENG_Quaternion _getBindingPoseInverseOrientation() {
        return mBindDerivedInverseOrientation;
    }

    @Override
    public void needUpdate(boolean forceParentUpdate) {
        
        super.needUpdate(forceParentUpdate);
        if (mManuallyControlled) {
            mCreator._notifyManualBonesDirty();
        }
    }

    @Override
    protected ENG_Node createChildImpl(String name, ENG_SceneManager.SceneMemoryMgrTypes type, boolean updateEveryFrame) {
        // TODO make sure the memory manager type gets here!!!
        return mCreator.createBone(name, type);
    }

    @Override
    protected ENG_Node createChildImpl(String name, boolean updateEveryFrame) {
        
        return mCreator.createBone(name);
    }

    @Override
    protected ENG_Node createChildImpl(boolean updateEveryFrame) {
        
        return mCreator.createBone();
    }

}
