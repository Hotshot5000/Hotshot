/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.ArrayList;

import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;

public class ENG_TagPoint extends ENG_Bone {

    /** @noinspection deprecation*/
    private ENG_Entity mParentEntity;
    private ENG_MovableObject mChildObject;
    private final ENG_Matrix4 mFullLocalTransform = new ENG_Matrix4();
    private boolean mInheritParentEntityOrientation = true;
    private boolean mInheritParentEntityScale = true;

    public ENG_TagPoint(short handle, ENG_Skeleton creator) {
        super(handle, creator);

    }

    public ENG_TagPoint(String name, short handle, ENG_Skeleton creator) {
        super(name, handle, creator);

    }

    /** @noinspection deprecation*/
    public ENG_Entity getParentEntity() {
        return mParentEntity;
    }

    public ENG_MovableObject getChildObject() {
        return mChildObject;
    }

    public void setInheritParentEntityOrientation(boolean b) {
        mInheritParentEntityOrientation = b;
    }

    public boolean getInheritParentEntityOrientation() {
        return mInheritParentEntityOrientation;
    }

    public void setInheritParentEntityScale(boolean b) {
        mInheritParentEntityScale = b;
    }

    public boolean getInheritParentEntityScale() {
        return mInheritParentEntityScale;
    }

    public ENG_Matrix4 getParentEntityTransform() {
        return mParentEntity._getParentNodeFullTransform();
    }

    public ENG_Matrix4 _getFullLocalTransform() {
        return mFullLocalTransform;
    }

    @Override
    public void needUpdate(boolean forceParentUpdate) {
        
        super.needUpdate(forceParentUpdate);

        if (mParentEntity != null) {
            ENG_Node node = mParentEntity.getParentNode();
            if (node != null) {
                node.needUpdate(forceParentUpdate);
            }
        }
    }

    @Override
    public void _updateFromParentImpl() {
        
        super._updateFromParentImpl();

        mFullLocalTransform.makeTransform(
                derivedPosition,
                derivedScale,
                derivedOrientation);

        if (mParentEntity != null) {
            ENG_Node entityParentNode = mParentEntity.getParentNode();
            if (entityParentNode != null) {
                ENG_Quaternion parentOrientation =
                        entityParentNode._getDerivedOrientation();
                if (mInheritParentEntityOrientation) {
                    parentOrientation.mul(
                            derivedOrientation, derivedOrientation);
                }
                ENG_Vector4D parentScale =
                        entityParentNode._getDerivedScale();
                if (mInheritParentEntityScale) {
                    derivedScale.mulInPlace(parentScale);
                }

                derivedPosition.set(
                        parentOrientation.mul(
                                parentScale.mulAsPt(derivedPosition)));

                derivedPosition.addInPlace(
                        entityParentNode._getDerivedPosition());
            }
        }

        if (mChildObject != null) {
            mChildObject._notifyMoved();
        }
        mParentEntity.queryLights();
    }

    /** @noinspection deprecation*/
    public ArrayList<ENG_Light> getLights() {
        return mParentEntity.queryLights();
    }

    /** @noinspection deprecation*/
    public void setParentEntity(ENG_Entity object) {
        
        mParentEntity = object;
    }

    public void setChildObject(ENG_MovableObject object) {
        
        mChildObject = object;
    }

}
