/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Degree;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Radian;
import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Integer;

public abstract class ENG_AnimableValue {

    public enum ValueType {
        INT,
        REAL,
        VECTOR2,
        VECTOR3,
        VECTOR4,
        QUATERNION,
        COLOUR,
        RADIAN,
        DEGREE
    }

    protected final ValueType mType;
    protected final float[] mBaseValueReal = new float[4];
    protected int mBaseValueInt;

    protected void setAsBaseValue(int v) {
        mBaseValueInt = v;
    }

    protected void setAsBaseValue(float v) {
        mBaseValueReal[0] = v;
    }

    protected void setAsBaseValue(ENG_Vector2D v) {
        mBaseValueReal[0] = v.x;
        mBaseValueReal[1] = v.y;
    }

    protected void setAsBaseValue(ENG_Vector3D v) {
        mBaseValueReal[0] = v.x;
        mBaseValueReal[1] = v.y;
        mBaseValueReal[2] = v.z;
    }

    protected void setAsBaseValue(ENG_Vector4D v) {
        mBaseValueReal[0] = v.x;
        mBaseValueReal[1] = v.y;
        mBaseValueReal[2] = v.z;
        mBaseValueReal[3] = v.w;
    }

    protected void setAsBaseValue(ENG_Quaternion v) {
        mBaseValueReal[0] = v.w;
        mBaseValueReal[1] = v.x;
        mBaseValueReal[2] = v.y;
        mBaseValueReal[3] = v.z;
    }

    protected void setAsBaseValue(ENG_ColorValue v) {
        mBaseValueReal[0] = v.r;
        mBaseValueReal[1] = v.g;
        mBaseValueReal[2] = v.b;
        mBaseValueReal[3] = v.a;
    }

    protected void setAsBaseValue(ENG_Radian v) {
        mBaseValueReal[0] = v.valueRadians();
    }

    protected void setAsBaseValue(ENG_Degree v) {
        mBaseValueReal[0] = v.valueDegrees();
    }

    protected void setAsBaseValue(Object v) {
        switch (mType) {
            case INT:
                setAsBaseValue(((ENG_Integer) v).getValue());
                break;
            case REAL:
                setAsBaseValue(((ENG_Float) v).getValue());
                break;
            case VECTOR2:
                setAsBaseValue((ENG_Vector2D) v);
                break;
            case VECTOR3:
                setAsBaseValue((ENG_Vector3D) v);
                break;
            case VECTOR4:
                setAsBaseValue((ENG_Vector4D) v);
                break;
            case QUATERNION:
                setAsBaseValue((ENG_Quaternion) v);
                break;
            case COLOUR:
                setAsBaseValue((ENG_ColorValue) v);
                break;
            case RADIAN:
                setAsBaseValue((ENG_Radian) v);
                break;
            case DEGREE:
                setAsBaseValue((ENG_Degree) v);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public ENG_AnimableValue(ValueType t) {
        mType = t;
    }

    public ValueType getType() {
        return mType;
    }

    public abstract void setCurrentStateAsBaseValue();

    public void setValue(int v) {
        throw new UnsupportedOperationException();
    }

    public void setValue(float v) {
        throw new UnsupportedOperationException();
    }

    public void setValue(ENG_Vector2D v) {
        throw new UnsupportedOperationException();
    }

    public void setValue(ENG_Vector3D v) {
        throw new UnsupportedOperationException();
    }

    public void setValue(ENG_Vector4D v) {
        throw new UnsupportedOperationException();
    }

    public void setValue(ENG_Quaternion v) {
        throw new UnsupportedOperationException();
    }

    public void setValue(ENG_ColorValue v) {
        throw new UnsupportedOperationException();
    }

    public void setValue(ENG_Radian v) {
        throw new UnsupportedOperationException();
    }

    public void setValue(ENG_Degree v) {
        throw new UnsupportedOperationException();
    }

    public void setValue(Object v) {
        switch (mType) {
            case INT:
                setValue(((ENG_Integer) v).getValue());
                break;
            case REAL:
                setValue(((ENG_Float) v).getValue());
                break;
            case VECTOR2:
                setValue((ENG_Vector2D) v);
                break;
            case VECTOR3:
                setValue((ENG_Vector3D) v);
                break;
            case VECTOR4:
                setValue((ENG_Vector4D) v);
                break;
            case QUATERNION:
                setValue((ENG_Quaternion) v);
                break;
            case COLOUR:
                setValue((ENG_ColorValue) v);
                break;
            case RADIAN:
                setValue((ENG_Radian) v);
                break;
            case DEGREE:
                setValue((ENG_Degree) v);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void resetToBaseValue() {
        switch (mType) {
            case INT:
                setValue(mBaseValueInt);
                break;
            case REAL:
                setValue(mBaseValueReal[0]);
                break;
            case VECTOR2:
                setValue(new ENG_Vector2D(mBaseValueReal));
                break;
            case VECTOR3:
                setValue(new ENG_Vector3D(mBaseValueReal));
                break;
            case VECTOR4:
                setValue(new ENG_Vector4D(mBaseValueReal));
                break;
            case QUATERNION:
                setValue(new ENG_Quaternion(mBaseValueReal));
                break;
            case COLOUR:
                setValue(new ENG_ColorValue(mBaseValueReal[0],
                        mBaseValueReal[1], mBaseValueReal[2], mBaseValueReal[3]));
                break;
            case RADIAN:
                setValue(new ENG_Radian(mBaseValueReal[0]));
                break;
            case DEGREE:
                setValue(new ENG_Degree(mBaseValueReal[0]));
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void applyDeltaValue(int v) {
        throw new UnsupportedOperationException();
    }

    public void applyDeltaValue(float v) {
        throw new UnsupportedOperationException();
    }

    public void applyDeltaValue(ENG_Vector2D v) {
        throw new UnsupportedOperationException();
    }

    public void applyDeltaValue(ENG_Vector3D v) {
        throw new UnsupportedOperationException();
    }

    public void applyDeltaValue(ENG_Vector4D v) {
        throw new UnsupportedOperationException();
    }

    public void applyDeltaValue(ENG_Quaternion v) {
        throw new UnsupportedOperationException();
    }

    public void applyDeltaValue(ENG_ColorValue v) {
        throw new UnsupportedOperationException();
    }

    public void applyDeltaValue(ENG_Radian v) {
        throw new UnsupportedOperationException();
    }

    public void applyDeltaValue(ENG_Degree v) {
        throw new UnsupportedOperationException();
    }

    public void applyDeltaValue(Object v) {
        switch (mType) {
            case INT:
                applyDeltaValue(((ENG_Integer) v).getValue());
                break;
            case REAL:
                applyDeltaValue(((ENG_Float) v).getValue());
                break;
            case VECTOR2:
                applyDeltaValue((ENG_Vector2D) v);
                break;
            case VECTOR3:
                applyDeltaValue((ENG_Vector3D) v);
                break;
            case VECTOR4:
                applyDeltaValue((ENG_Vector4D) v);
                break;
            case QUATERNION:
                applyDeltaValue((ENG_Quaternion) v);
                break;
            case COLOUR:
                applyDeltaValue((ENG_ColorValue) v);
                break;
            case RADIAN:
                applyDeltaValue((ENG_Radian) v);
                break;
            case DEGREE:
                applyDeltaValue((ENG_Degree) v);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }


}
