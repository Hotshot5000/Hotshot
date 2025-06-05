/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public class ENG_GpuConstantDefinition {

    /**
     * Enumeration of the types of constant we may encounter in programs.
     *
     * @note Low-level programs, by definition, will always use either
     * float4 or int4 constant types since that is the fundamental underlying
     * type in assembler.
     */
    public enum GpuConstantType {
        GCT_FLOAT1(1),
        GCT_FLOAT2(2),
        GCT_FLOAT3(3),
        GCT_FLOAT4(4),
        GCT_SAMPLER1D(5),
        GCT_SAMPLER2D(6),
        GCT_SAMPLER3D(7),
        GCT_SAMPLERCUBE(8),
        GCT_SAMPLER1DSHADOW(9),
        GCT_SAMPLER2DSHADOW(10),
        GCT_MATRIX_2X2(11),
        GCT_MATRIX_2X3(12),
        GCT_MATRIX_2X4(13),
        GCT_MATRIX_3X2(14),
        GCT_MATRIX_3X3(15),
        GCT_MATRIX_3X4(16),
        GCT_MATRIX_4X2(17),
        GCT_MATRIX_4X3(18),
        GCT_MATRIX_4X4(19),
        GCT_INT1(20),
        GCT_INT2(21),
        GCT_INT3(22),
        GCT_INT4(23),
        GCT_UNKNOWN(99);

        private final int type;

        GpuConstantType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    /**
     * The variability of a GPU parameter, as derived from auto-params targetting it.
     * These values must be powers of two since they are used in masks.
     */
    public enum GpuParamVariability {
        /// No variation except by manual setting - the default
        GPV_GLOBAL((short) 1),
        /// Varies per object (based on an auto param usually), but not per light setup
        GPV_PER_OBJECT((short) 2),
        /// Varies with light setup
        GPV_LIGHTS((short) 4),
        /// Varies with pass iteration number
        GPV_PASS_ITERATION_NUMBER((short) 8),


        /// Full mask (16-bit)
        GPV_ALL((short) 0xFFFF);

        private final short variability;

        GpuParamVariability(short variability) {
            this.variability = variability;
        }

        public short getVariability() {
            return variability;
        }

    }

    public GpuConstantType constType = GpuConstantType.GCT_UNKNOWN;
    public int physicalIndex = Integer.MAX_VALUE;
    public int logicalIndex;
    public int elementSize;
    public int arraySize = 1;
    public short variability = GpuParamVariability.GPV_GLOBAL.getVariability();

    public ENG_GpuConstantDefinition() {

    }

    public ENG_GpuConstantDefinition(ENG_GpuConstantDefinition def) {
        set(def);
    }

    public void set(ENG_GpuConstantDefinition def) {
        constType = def.constType;
        physicalIndex = def.physicalIndex;
        logicalIndex = def.logicalIndex;
        elementSize = def.elementSize;
        arraySize = def.arraySize;
        variability = def.variability;
    }

    public boolean isFloat() {
        return isFloat(constType);
    }

    public static boolean isFloat(GpuConstantType c) {
        switch (c) {
            case GCT_INT1:
            case GCT_INT2:
            case GCT_INT3:
            case GCT_INT4:
            case GCT_SAMPLER1D:
            case GCT_SAMPLER2D:
            case GCT_SAMPLER3D:
            case GCT_SAMPLERCUBE:
            case GCT_SAMPLER1DSHADOW:
            case GCT_SAMPLER2DSHADOW:
                return false;
            default:
                return true;
        }
    }

    public boolean isSampler() {
        return isSampler(constType);
    }

    public static boolean isSampler(GpuConstantType c) {
        switch (c) {
            case GCT_SAMPLER1D:
            case GCT_SAMPLER2D:
            case GCT_SAMPLER3D:
            case GCT_SAMPLERCUBE:
            case GCT_SAMPLER1DSHADOW:
            case GCT_SAMPLER2DSHADOW:
                return true;
            default:
                return false;
        }
    }

    /**
     * Get the element size of a given type, including whether to pad the
     * elements into multiples of 4 (e.g. SM1 and D3D does, GLSL doesn't)
     */
    public static int getElementSize(GpuConstantType ctype, boolean padToMultiplesOf4) {
        if (padToMultiplesOf4) {
            switch (ctype) {
                case GCT_FLOAT1:
                case GCT_INT1:
                case GCT_SAMPLER1D:
                case GCT_SAMPLER2D:
                case GCT_SAMPLER3D:
                case GCT_SAMPLERCUBE:
                case GCT_SAMPLER1DSHADOW:
                case GCT_SAMPLER2DSHADOW:
                case GCT_FLOAT2:
                case GCT_INT2:
                case GCT_FLOAT3:
                case GCT_INT3:
                case GCT_FLOAT4:
                case GCT_INT4:
                    return 4;
                case GCT_MATRIX_2X2:
                case GCT_MATRIX_2X3:
                case GCT_MATRIX_2X4:
                    return 8; // 2 float4s
                case GCT_MATRIX_3X2:
                case GCT_MATRIX_3X3:
                case GCT_MATRIX_3X4:
                    return 12; // 3 float4s
                case GCT_MATRIX_4X2:
                case GCT_MATRIX_4X3:
                case GCT_MATRIX_4X4:
                    return 16; // 4 float4s
                default:
                    return 4;
            }
        } else {
            switch (ctype) {
                case GCT_FLOAT1:
                case GCT_INT1:
                case GCT_SAMPLER1D:
                case GCT_SAMPLER2D:
                case GCT_SAMPLER3D:
                case GCT_SAMPLERCUBE:
                case GCT_SAMPLER1DSHADOW:
                case GCT_SAMPLER2DSHADOW:
                    return 1;
                case GCT_FLOAT2:
                case GCT_INT2:
                    return 2;
                case GCT_FLOAT3:
                case GCT_INT3:
                    return 3;
                case GCT_FLOAT4:
                case GCT_INT4:
                    return 4;
                case GCT_MATRIX_2X2:
                    return 4;
                case GCT_MATRIX_2X3:
                case GCT_MATRIX_3X2:
                    return 6;
                case GCT_MATRIX_2X4:
                case GCT_MATRIX_4X2:
                    return 8;
                case GCT_MATRIX_3X3:
                    return 9;
                case GCT_MATRIX_3X4:
                case GCT_MATRIX_4X3:
                    return 12;
                case GCT_MATRIX_4X4:
                    return 16;
                default:
                    return 4;
            }

        }
    }
}
