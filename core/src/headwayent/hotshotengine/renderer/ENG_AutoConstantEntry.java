/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:46 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.ENG_GpuProgramParameters.AutoConstantType;

public class ENG_AutoConstantEntry {
    /// The type of parameter
    public AutoConstantType paramType;
    /// The target (physical) constant index
    public int physicalIndex;
    /**
     * The number of elements per individual entry in this constant
     * Used in case people used packed elements smaller than 4 (e.g. GLSL)
     * and bind an auto which is 4-element packed to it
     */
    public int elementCount;

    public int data;
    public float fdata;

    public short variability;

    public void set(ENG_AutoConstantEntry oth) {
        paramType = oth.paramType;
        physicalIndex = oth.physicalIndex;
        elementCount = oth.elementCount;
        data = oth.data;
        fdata = oth.fdata;
        variability = oth.variability;
    }

    public ENG_AutoConstantEntry(ENG_AutoConstantEntry oth) {
        set(oth);
    }

    public ENG_AutoConstantEntry(AutoConstantType paramType, int physicalIndex,
                                 float fdata, short variability) {
        set(paramType, physicalIndex, fdata, variability);
    }

    public ENG_AutoConstantEntry(AutoConstantType paramType, int physicalIndex,
                                 float fdata, short variability, int elementCount) {
        set(paramType, physicalIndex, fdata, variability, elementCount);
    }

    public ENG_AutoConstantEntry(AutoConstantType paramType, int physicalIndex,
                                 int data, short variability) {
        set(paramType, physicalIndex, data, variability);
    }

    public ENG_AutoConstantEntry(AutoConstantType paramType, int physicalIndex,
                                 int data, short variability, int elementCount) {
        set(paramType, physicalIndex, data, variability, elementCount);
    }

    public void set(AutoConstantType paramType, int physicalIndex,
                    int data, short variability) {
        set(paramType, physicalIndex, data, variability, 4);
    }

    public void set(AutoConstantType paramType, int physicalIndex,
                    int data, short variability, int elementCount) {
        this.paramType = paramType;
        this.physicalIndex = physicalIndex;
        this.data = data;
        this.variability = variability;
        this.elementCount = elementCount;
    }

    public void set(AutoConstantType paramType, int physicalIndex,
                    float fdata, short variability) {
        set(paramType, physicalIndex, fdata, variability, 4);
    }

    public void set(AutoConstantType paramType, int physicalIndex,
                    float fdata, short variability, int elementCount) {
        this.paramType = paramType;
        this.physicalIndex = physicalIndex;
        this.fdata = fdata;
        this.variability = variability;
        this.elementCount = elementCount;
    }
}
