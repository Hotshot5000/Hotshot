/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Matrix3;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.renderer.ENG_GpuConstantDefinition.GpuConstantType;
import headwayent.hotshotengine.renderer.ENG_GpuConstantDefinition.GpuParamVariability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

public class ENG_GpuSharedParameters {

    protected final ENG_GpuNamedConstants mNamedConstants = new ENG_GpuNamedConstants();
    protected final ArrayList<ENG_Float> mFloatConstants = new ArrayList<>();
    protected final ArrayList<ENG_Integer> mIntConstants = new ArrayList<>();
    protected final String mName;
    protected long mFrameLastUpdated =
            ENG_RenderRoot.getRenderRoot().getNextFrameNumber();
    protected long mVersion;

    public ENG_GpuSharedParameters(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void addConstantDefinition(String name, GpuConstantType constType) {
        addConstantDefinition(name, constType, 1);
    }

    public void addConstantDefinition(String name, GpuConstantType constType,
                                      int arraySize) {
        if (mNamedConstants.map.containsKey(name)) {
            throw new IllegalArgumentException("Constant entry with name '" +
                    name + "' already exists. ");
        }
        ENG_GpuConstantDefinition def = new ENG_GpuConstantDefinition();
        def.arraySize = arraySize;
        def.constType = constType;
        // for compatibility we do not pad values to multiples of 4
        // when it comes to arrays, user is responsible for creating matching defs
        def.elementSize = ENG_GpuConstantDefinition.getElementSize(constType, false);

        // not used
        def.logicalIndex = 0;
        def.variability = GpuParamVariability.GPV_GLOBAL.getVariability();

        if (def.isFloat()) {
            def.physicalIndex = mFloatConstants.size();
        } else {
            def.physicalIndex = mIntConstants.size();
        }

        mNamedConstants.map.put(name, def);
        ++mVersion;
    }

    /** @noinspection SuspiciousListRemoveInLoop */
    public void removeConstantDefinition(String name) {
        ENG_GpuConstantDefinition def = mNamedConstants.map.get(name);
        if (def != null) {
            boolean isFloat = def.isFloat();
            int numElems = def.elementSize * def.arraySize;

            for (Entry<String, ENG_GpuConstantDefinition> stringENG_gpuConstantDefinitionEntry : mNamedConstants.map.entrySet()) {
                ENG_GpuConstantDefinition otherDef = stringENG_gpuConstantDefinitionEntry.getValue();
                boolean otherIsFloat = otherDef.isFloat();

                if (((isFloat && otherIsFloat) || ((!isFloat) && (!otherIsFloat))) &&
                        (otherDef.physicalIndex > def.physicalIndex)) {
                    // adjust index
                    otherDef.physicalIndex -= numElems;
                }
            }

            // remove floats and reduce buffer
            if (isFloat) {
                mNamedConstants.floatBufferSize -= numElems;
                int len = def.physicalIndex + numElems;
                for (int i = def.physicalIndex; i < len; ++i) {
                    mFloatConstants.remove(i);
                }
            } else {
                mNamedConstants.intBufferSize -= numElems;
                int len = def.physicalIndex + numElems;
                for (int i = def.physicalIndex; i < len; ++i) {
                    mIntConstants.remove(i);
                }
            }
            ++mVersion;
        }
    }

    public void removeAllConstantDefinitions() {
        mNamedConstants.map.clear();
        mNamedConstants.floatBufferSize = 0;
        mNamedConstants.intBufferSize = 0;
        mFloatConstants.clear();
        mIntConstants.clear();
    }

    public Iterator<Entry<String, ENG_GpuConstantDefinition>>
    getConstantDefinitionIterator() {
        return mNamedConstants.map.entrySet().iterator();
    }

    public ENG_GpuConstantDefinition getConstantDefinition(String name) {
        ENG_GpuConstantDefinition def = mNamedConstants.map.get(name);
        if (def == null) {
            throw new IllegalArgumentException("Constant entry with name '" +
                    name + "' does not exist. ");
        }
        return def;
    }

    public ENG_GpuNamedConstants getConstantDefinitions() {
        return mNamedConstants;
    }

    public long getVersion() {
        return mVersion;
    }

    public void _markDirty() {
        mFrameLastUpdated = ENG_RenderRoot.getRenderRoot().getNextFrameNumber();
    }

    public void setNamedConstantFloatList(String name, ArrayList<ENG_Float> val) {
        ENG_GpuConstantDefinition def = mNamedConstants.map.get(name);
        if (def != null) {
            //	int min = Math.min(count, def.elementSize * def.arraySize);
            mFloatConstants.addAll(def.physicalIndex, val);
        }
        _markDirty();
    }

    public void setNamedConstant(String name, ENG_Float[] val, int count) {
        ENG_GpuConstantDefinition def = mNamedConstants.map.get(name);
        if (def != null) {
            int min = Math.min(count, def.elementSize * def.arraySize);
            int pos = def.physicalIndex;
            for (int i = 0; i < min; ++i) {
                mFloatConstants.add(pos++, val[i]);
            }
        }
        _markDirty();
    }

    public void setNamedConstant(String name, float[] val, int count) {
        ENG_Float[] fval = new ENG_Float[count];
        for (int i = 0; i < count; ++i) {
            fval[i] = new ENG_Float(val[i]);
        }
        setNamedConstant(name, fval, count);
    }

    public void setNamedConstantIntList(String name, ArrayList<ENG_Integer> val) {
        ENG_GpuConstantDefinition def = mNamedConstants.map.get(name);
        if (def != null) {
            //	int min = Math.min(count, def.elementSize * def.arraySize);
            mIntConstants.addAll(def.physicalIndex, val);
        }
        _markDirty();
    }

    public void setNamedConstant(String name, ENG_Integer[] val, int count) {
        ENG_GpuConstantDefinition def = mNamedConstants.map.get(name);
        if (def != null) {
            int min = Math.min(count, def.elementSize * def.arraySize);
            int pos = def.physicalIndex;
            for (int i = 0; i < min; ++i) {
                mIntConstants.add(pos++, val[i]);
            }
        }
        _markDirty();
    }

    public void setNamedConstant(String name, int[] val, int count) {
        ENG_Integer[] ival = new ENG_Integer[count];
        for (int i = 0; i < count; ++i) {
            ival[i] = new ENG_Integer(val[i]);
        }
        setNamedConstant(name, ival, count);
    }

    public void setNamedConstant(String name, ENG_ColorValue color) {
        ENG_GpuConstantDefinition def = mNamedConstants.map.get(name);
        if (def != null) {
            int pos = def.physicalIndex;
            mFloatConstants.add(pos++, new ENG_Float(color.r));
            mFloatConstants.add(pos++, new ENG_Float(color.g));
            mFloatConstants.add(pos++, new ENG_Float(color.b));
            mFloatConstants.add(pos++, new ENG_Float(color.a));
        }
    }

    public void setNamedConstant(String name, float val) {
        setNamedConstant(name, new ENG_Float[]{new ENG_Float(val)}, 1);
    }

    public void setNamedConstant(String name, int val) {
        setNamedConstant(name, new ENG_Integer[]{new ENG_Integer(val)}, 1);
    }

    public void setNamedConstant(String name, ENG_Vector3D val) {
        ENG_Float[] fval = new ENG_Float[]{new ENG_Float(val.x),
                new ENG_Float(val.y), new ENG_Float(val.z)};
        setNamedConstant(name, fval, 3);
    }

    public void setNamedConstant(String name, ENG_Vector4D val) {
        ENG_Float[] fval = new ENG_Float[]{new ENG_Float(val.x),
                new ENG_Float(val.y), new ENG_Float(val.z)};
        setNamedConstant(name, fval, 3);
    }

    public void setNamedConstantFull(String name, ENG_Vector4D val) {
        ENG_Float[] fval = new ENG_Float[]{new ENG_Float(val.x),
                new ENG_Float(val.y), new ENG_Float(val.z), new ENG_Float(val.w)};
        setNamedConstant(name, fval, 4);
    }

    public void setNamedConstant(String name, ENG_Matrix3[] val) {
        for (ENG_Matrix3 engMatrix3 : val) {
            setNamedConstant(name, engMatrix3);
        }
    }

    public void setNamedConstant(String name, ENG_Matrix3 val) {
        ENG_Float[] fval = new ENG_Float[9];
        for (int i = 0; i < 9; ++i) {
            fval[i] = new ENG_Float(val.get()[i]);
        }
        setNamedConstant(name, fval, 9);
    }

    public void setNamedConstant(String name, ENG_Matrix4[] val) {
        for (ENG_Matrix4 engMatrix4 : val) {
            setNamedConstant(name, engMatrix4);
        }
    }

    public void setNamedConstant(String name, ENG_Matrix4 val) {
        ENG_Float[] fval = new ENG_Float[16];
        for (int i = 0; i < 16; ++i) {
            fval[i] = new ENG_Float(val.get()[i]);
        }
        setNamedConstant(name, fval, 16);
    }

    public ENG_Float getFloatPointerToModify(int pos) {
        _markDirty();
        return mFloatConstants.get(pos);
    }

    public ENG_Float getFloatPointer(int pos) {
        return mFloatConstants.get(pos);
    }

    public ENG_Integer getIntegerPointerToModify(int pos) {
        _markDirty();
        return mIntConstants.get(pos);
    }

    public ENG_Integer getIntegerPointer(int pos) {
        return mIntConstants.get(pos);
    }
}
