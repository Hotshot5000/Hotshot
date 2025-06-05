/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:10 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.TreeMap;

public class ENG_GpuNamedConstants {

    public int floatBufferSize;
    public int intBufferSize;
    public final TreeMap<String, ENG_GpuConstantDefinition> map =
            new TreeMap<>();

    /**
     * Indicates whether all array entries will be generated and added to the definitions map
     *
     * @remarks Normally, the number of array entries added to the definitions map is capped at 16
     * to save memory. Setting this value to <code>true</code> allows all of the entries
     * to be generated and added to the map.
     */
    protected static boolean msGenerateAllConstantDefinitionArrayEntries;


    public void setGenerateAllConstantDefinitionArrayEntries(boolean generateAll) {
        msGenerateAllConstantDefinitionArrayEntries = generateAll;
    }

    public boolean getGenerateAllConstantDefinitionArrayEntries() {
        return msGenerateAllConstantDefinitionArrayEntries;
    }

    public void generateConstantDefinitionArrayEntries(String paramName,
                                                       ENG_GpuConstantDefinition baseDef) {
        ENG_GpuConstantDefinition arrayDef = new ENG_GpuConstantDefinition(baseDef);
        arrayDef.arraySize = 1;

        int maxArrayIndex = 1;
        if ((baseDef.arraySize <= 16) || msGenerateAllConstantDefinitionArrayEntries) {
            maxArrayIndex = baseDef.arraySize;
        }

        for (int i = 0; i < maxArrayIndex; ++i) {
            String arrayName = paramName + "[" + i + "]";
            map.put(arrayName, arrayDef);
            // increment location
            arrayDef.physicalIndex += arrayDef.elementSize;
        }
        // note no increment of buffer sizes since this is shared with main array def
    }
}
