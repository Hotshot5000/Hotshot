/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:02 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.basictypes.ENG_Byte;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.basictypes.ENG_Short;

public class ENG_VertexElement implements Comparable<ENG_VertexElement> {

    /// Vertex element semantics, used to identify the meaning of vertex buffer contents
    public enum VertexElementSemantic {
        /// Position, 3 reals per vertex
        VES_POSITION,
        /// Blending weights
        VES_BLEND_WEIGHTS,
        /// Blending indices
        VES_BLEND_INDICES,
        /// Normal, 3 reals per vertex
        VES_NORMAL,
        /// Diffuse colours
        VES_DIFFUSE,
        /// Specular colours
        VES_SPECULAR,
        /// Texture coordinates
        VES_TEXTURE_COORDINATES,
        /// Binormal (Y axis if normal is Z)
        VES_BINORMAL,
        /// Tangent (X axis if normal is Z)
        VES_TANGENT;

        public static VertexElementSemantic getVertexElementSemantic(short s) {
            switch (s) {
                case 1:
                    return VES_POSITION;
                case 2:
                    return VES_BLEND_WEIGHTS;
                case 3:
                    return VES_BLEND_INDICES;
                case 4:
                    return VES_NORMAL;
                case 5:
                    return VES_DIFFUSE;
                case 6:
                    return VES_SPECULAR;
                case 7:
                    return VES_TEXTURE_COORDINATES;
                case 8:
                    return VES_BINORMAL;
                case 9:
                    return VES_TANGENT;
                default:
                    throw new IllegalArgumentException(s + " is an invalid " +
                            "VertexElementSemantic");
            }
        }

    }

    /// Vertex element type, used to identify the base types of the vertex contents
    public enum VertexElementType {
        VET_FLOAT1,
        VET_FLOAT2,
        VET_FLOAT3,
        VET_FLOAT4,
        /// alias to more specific colour type - use the current rendersystem's colour packing
        VET_COLOUR,
        VET_SHORT1,
        VET_SHORT2,
        VET_SHORT3,
        VET_SHORT4,
        VET_UBYTE4,
        /// D3D style compact colour
        VET_COLOUR_ARGB,
        /// GL style compact colour
        VET_COLOUR_ABGR;

        public static VertexElementType getVertexElementType(short s) {
            switch (s) {
                case 0:
                    return VET_FLOAT1;
                case 1:
                    return VET_FLOAT2;
                case 2:
                    return VET_FLOAT3;
                case 3:
                    return VET_FLOAT4;
                case 4:
                    return VET_COLOUR;
                case 5:
                    return VET_SHORT1;
                case 6:
                    return VET_SHORT2;
                case 7:
                    return VET_SHORT3;
                case 8:
                    return VET_SHORT4;
                case 9:
                    return VET_UBYTE4;
                case 10:
                    return VET_COLOUR_ARGB;
                case 11:
                    return VET_COLOUR_ABGR;
                default:
                    throw new IllegalArgumentException(s + " is an invalid " +
                            "VertexElementType");
            }
        }
    }
    
    /*/// The source vertex buffer, as bound to an index using VertexBufferBinding
        unsigned short mSource;
        /// The offset in the buffer that this element starts at
        size_t mOffset;
        /// The type of element
        VertexElementType mType;
        /// The meaning of the element
        VertexElementSemantic mSemantic;
        /// Index of the item, only applicable for some elements like texture coords
        unsigned short mIndex;*/

    protected short source;
    protected int offset;
    protected VertexElementType type;
    protected VertexElementSemantic semantic;
    protected short index;

    public ENG_VertexElement() {

    }

    public ENG_VertexElement(short source, int offset, VertexElementType type,
                             VertexElementSemantic semantic, short index) {
        this.source = source;
        this.offset = offset;
        this.type = type;
        this.semantic = semantic;
        this.index = index;
    }

    public static int getTypeSize(VertexElementType type) {
        switch (type) {
            case VET_COLOUR:
            case VET_COLOUR_ABGR:
            case VET_COLOUR_ARGB:
                return ENG_Integer.SIZE_IN_BYTES;
            case VET_FLOAT1:
                return ENG_Float.SIZE_IN_BYTES;
            case VET_FLOAT2:
                return ENG_Float.SIZE_IN_BYTES * 2;
            case VET_FLOAT3:
                return ENG_Float.SIZE_IN_BYTES * 3;
            case VET_FLOAT4:
                return ENG_Float.SIZE_IN_BYTES * 4;
            case VET_SHORT1:
                return ENG_Short.SIZE_IN_BYTES;
            case VET_SHORT2:
                return ENG_Short.SIZE_IN_BYTES * 2;
            case VET_SHORT3:
                return ENG_Short.SIZE_IN_BYTES * 3;
            case VET_SHORT4:
                return ENG_Short.SIZE_IN_BYTES * 4;
            case VET_UBYTE4:
                return ENG_Byte.SIZE_IN_BYTES * 4;
            default:
                throw new IllegalArgumentException("Unknown VertexElementType: " + type);
        }
    }

    public static short getTypeCount(VertexElementType type) {
        switch (type) {
            case VET_COLOUR:
            case VET_COLOUR_ABGR:
            case VET_COLOUR_ARGB:
                return 1;
            case VET_FLOAT1:
                return 1;
            case VET_FLOAT2:
                return 2;
            case VET_FLOAT3:
                return 3;
            case VET_FLOAT4:
                return 4;
            case VET_SHORT1:
                return 1;
            case VET_SHORT2:
                return 2;
            case VET_SHORT3:
                return 3;
            case VET_SHORT4:
                return 4;
            case VET_UBYTE4:
                return 4;
            default:
                throw new IllegalArgumentException("Unknown VertexElementType: " + type);
        }
    }

    public static VertexElementType multiplyTypeCount(VertexElementType baseType,
                                                      short count) {
        switch (baseType) {
            case VET_FLOAT1:
                switch (count) {
                    case 1:
                        return VertexElementType.VET_FLOAT1;
                    case 2:
                        return VertexElementType.VET_FLOAT2;
                    case 3:
                        return VertexElementType.VET_FLOAT3;
                    case 4:
                        return VertexElementType.VET_FLOAT4;
                    default:
                        break;
                }
                break;
            case VET_SHORT1:
                switch (count) {
                    case 1:
                        return VertexElementType.VET_SHORT1;
                    case 2:
                        return VertexElementType.VET_SHORT2;
                    case 3:
                        return VertexElementType.VET_SHORT3;
                    case 4:
                        return VertexElementType.VET_SHORT4;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        throw new IllegalArgumentException("Invalid base type or count: " +
                baseType + " " + count);
    }

    public static VertexElementType getBaseType(VertexElementType multiType) {
        switch (multiType) {
            case VET_FLOAT1:
            case VET_FLOAT2:
            case VET_FLOAT3:
            case VET_FLOAT4:
                return VertexElementType.VET_FLOAT1;
            case VET_COLOUR:
                return VertexElementType.VET_COLOUR;
            case VET_COLOUR_ABGR:
                return VertexElementType.VET_COLOUR_ABGR;
            case VET_COLOUR_ARGB:
                return VertexElementType.VET_COLOUR_ARGB;
            case VET_SHORT1:
            case VET_SHORT2:
            case VET_SHORT3:
            case VET_SHORT4:
                return VertexElementType.VET_SHORT1;
            case VET_UBYTE4:
                return VertexElementType.VET_UBYTE4;
            default:
                throw new IllegalArgumentException("Illegal VertexElementType: " +
                        multiType);
        }
    }

    public static VertexElementType getBestColorVertexElementType() {
        if ((ENG_RenderRoot.getRenderRoot() != null) &&
                (ENG_RenderRoot.getRenderRoot().getRenderSystem() != null)) {
            return
                    ENG_RenderRoot.getRenderRoot().getRenderSystem().getColorVertexElementType();
        }
        return VertexElementType.VET_COLOUR_ABGR;
    }

    public static void convertColorValue(VertexElementType srcType,
                                         VertexElementType destType, int[] ptr) {
        if (srcType == destType) {
            return;
        }
        /* *ptr =
		   ((*ptr&0x00FF0000)>>16)|((*ptr&0x000000FF)<<16)|(*ptr&0xFF00FF00);*/
        ptr[0] = ((ptr[0] & 0x00ff0000) >> 16) | ((ptr[0] & 0x000000ff) << 16) |
                (ptr[0] & 0xff00ff00);
    }

    public static int convertColorValue(ENG_ColorValue src, VertexElementType dst) {
        switch (dst) {
            case VET_COLOUR_ARGB:
                return src.getAsARGB();

            default:
            case VET_COLOUR_ABGR:
                return src.getAsABGR();
        }
    }

    public int compareTo(ENG_VertexElement ve) {
        //ENG_VertexElement ve = (ENG_VertexElement)obj;
        if (this.getSource() < ve.getSource()) {
            return -1;
        } else if (this.getSource() > ve.getSource()) {
            return 1;
        } else {
            if (this.getSemantic().ordinal() < ve.getSemantic().ordinal()) {
                return -1;
            } else if (this.getSemantic().ordinal() > ve.getSemantic().ordinal()) {
                return 1;
            } else {
                return Short.compare(this.getIndex(), ve.getIndex());
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof ENG_VertexElement) {
            ENG_VertexElement ve = (ENG_VertexElement) obj;
            return (type == ve.type) &&
                    (index == ve.index) &&
                    (semantic == ve.semantic) &&
                    (offset == ve.offset) &&
                    (source == ve.source);
        }

        throw new IllegalArgumentException(
                "Use only ENG_VertexElement for comparison");
    }

    public int getSize() {
        return getTypeSize(type);
    }

    public int baseVertexPointerToElement(int basePos) {
        return basePos + offset;
    }


    /**
     * @return the source
     */
    public short getSource() {
        return source;
    }

    /**
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @return the type
     */
    public VertexElementType getType() {
        return type;
    }

    /**
     * @return the semantic
     */
    public VertexElementSemantic getSemantic() {
        return semantic;
    }

    /**
     * @return the index
     */
    public short getIndex() {
        return index;
    }
}
