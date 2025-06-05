/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:46 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.ENG_GpuProgramParameters.ACDataType;
import headwayent.hotshotengine.renderer.ENG_GpuProgramParameters.AutoConstantType;
import headwayent.hotshotengine.renderer.ENG_GpuProgramParameters.ElementType;

public class ENG_AutoConstantDefinition {
    public AutoConstantType acType;
    public String name;
    public int elementCount;
    public ElementType elementType;
    public ACDataType dataType;

    public ENG_AutoConstantDefinition(AutoConstantType acType, String name,
                                      int elementCount,
                                      ElementType elementType, ACDataType dataType) {
        set(acType, name, elementCount, elementType, dataType);
    }

    public void set(AutoConstantType acType, String name, int elementCount,
                    ElementType elementType, ACDataType dataType) {
        this.acType = acType;
        this.name = name;
        this.elementCount = elementCount;
        this.elementType = elementType;
        this.dataType = dataType;
    }
}
