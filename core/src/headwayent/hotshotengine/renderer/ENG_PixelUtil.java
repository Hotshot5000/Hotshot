/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Bitwise;
import headwayent.hotshotengine.material.ENG_Color;

import java.nio.ByteBuffer;
import java.util.Locale;

public class ENG_PixelUtil {

    public enum PixelFormat {
        /// Unknown pixel format.
        PF_UNKNOWN(0),
        /// 8-bit pixel format, all bits luminace.
        PF_L8(1),
        PF_BYTE_L(PF_L8.getFormat()),
        /// 16-bit pixel format, all bits luminace.
        PF_L16(2),
        PF_SHORT_L(PF_L16.getFormat()),
        /// 8-bit pixel format, all bits alpha.
        PF_A8(3),
        PF_BYTE_A(PF_A8.getFormat()),
        /// 8-bit pixel format, 4 bits alpha, 4 bits luminance.
        PF_A4L4(4),
        /// 2 byte pixel format, 1 byte luminance, 1 byte alpha
        PF_BYTE_LA(5),
        /// 16-bit pixel format, 5 bits red, 6 bits green, 5 bits blue.
        PF_R5G6B5(6),
        /// 16-bit pixel format, 5 bits red, 6 bits green, 5 bits blue.
        PF_B5G6R5(7),
        /// 8-bit pixel format, 2 bits blue, 3 bits green, 3 bits red.
        PF_R3G3B2(31),
        /// 16-bit pixel format, 4 bits for alpha, red, green and blue.
        PF_A4R4G4B4(8),
        /// 16-bit pixel format, 5 bits for blue, green, red and 1 for alpha.
        PF_A1R5G5B5(9),
        /// 24-bit pixel format, 8 bits for red, green and blue.
        PF_R8G8B8(10),
        /// 24-bit pixel format, 8 bits for blue, green and red.
        PF_B8G8R8(11),
        /// 32-bit pixel format, 8 bits for alpha, red, green and blue.
        PF_A8R8G8B8(12),
        /// 32-bit pixel format, 8 bits for blue, green, red and alpha.
        PF_A8B8G8R8(13),
        /// 32-bit pixel format, 8 bits for blue, green, red and alpha.
        PF_B8G8R8A8(14),
        /// 32-bit pixel format, 8 bits for red, green, blue and alpha.
        PF_R8G8B8A8(28),
        /// 32-bit pixel format, 8 bits for red, 8 bits for green, 8 bits for blue
        /// like PF_A8R8G8B8, but alpha will get discarded
        PF_X8R8G8B8(26),
        /// 32-bit pixel format, 8 bits for blue, 8 bits for green, 8 bits for red
        /// like PF_A8B8G8R8, but alpha will get discarded
        PF_X8B8G8R8(27),
        /// 3 byte pixel format, 1 byte for red, 1 byte for green, 1 byte for blue
        PF_BYTE_RGB(PF_R8G8B8.getFormat()),
        /// 3 byte pixel format, 1 byte for blue, 1 byte for green, 1 byte for red
        PF_BYTE_BGR(PF_B8G8R8.getFormat()),
        /// 4 byte pixel format, 1 byte for blue, 1 byte for green, 1 byte for red and one byte for alpha
        PF_BYTE_BGRA(PF_B8G8R8A8.getFormat()),
        /// 4 byte pixel format, 1 byte for red, 1 byte for green, 1 byte for blue, and one byte for alpha
        PF_BYTE_RGBA(PF_R8G8B8A8.getFormat()),
        /// 32-bit pixel format, 2 bits for alpha, 10 bits for red, green and blue.
        PF_A2R10G10B10(15),
        /// 32-bit pixel format, 10 bits for blue, green and red, 2 bits for alpha.
        PF_A2B10G10R10(16),
        /// DDS (DirectDraw Surface) DXT1 format
        PF_DXT1(17),
        /// DDS (DirectDraw Surface) DXT2 format
        PF_DXT2(18),
        /// DDS (DirectDraw Surface) DXT3 format
        PF_DXT3(19),
        /// DDS (DirectDraw Surface) DXT4 format
        PF_DXT4(20),
        /// DDS (DirectDraw Surface) DXT5 format
        PF_DXT5(21),
        // 16-bit pixel format, 16 bits (float) for red
        PF_FLOAT16_R(32),
        // 48-bit pixel format, 16 bits (float) for red, 16 bits (float) for green, 16 bits (float) for blue
        PF_FLOAT16_RGB(22),
        // 64-bit pixel format, 16 bits (float) for red, 16 bits (float) for green, 16 bits (float) for blue, 16 bits (float) for alpha
        PF_FLOAT16_RGBA(23),
        // 16-bit pixel format, 16 bits (float) for red
        PF_FLOAT32_R(33),
        // 96-bit pixel format, 32 bits (float) for red, 32 bits (float) for green, 32 bits (float) for blue
        PF_FLOAT32_RGB(24),
        // 128-bit pixel format, 32 bits (float) for red, 32 bits (float) for green, 32 bits (float) for blue, 32 bits (float) for alpha
        PF_FLOAT32_RGBA(25),
        // 32-bit, 2-channel s10e5 floating point pixel format, 16-bit green, 16-bit red
        PF_FLOAT16_GR(35),
        // 64-bit, 2-channel floating point pixel format, 32-bit green, 32-bit red
        PF_FLOAT32_GR(36),
        // Depth texture format
        PF_DEPTH(29),
        // 64-bit pixel format, 16 bits for red, green, blue and alpha
        PF_SHORT_RGBA(30),
        // 32-bit pixel format, 16-bit green, 16-bit red
        PF_SHORT_GR(34),
        // 48-bit pixel format, 16 bits for red, green and blue
        PF_SHORT_RGB(37),
        /// PVRTC (PowerVR) RGB 2 bpp
        PF_PVRTC_RGB2(38),
        /// PVRTC (PowerVR) RGBA 2 bpp
        PF_PVRTC_RGBA2(39),
        /// PVRTC (PowerVR) RGB 4 bpp
        PF_PVRTC_RGB4(40),
        /// PVRTC (PowerVR) RGBA 4 bpp
        PF_PVRTC_RGBA4(41),
        // Number of pixel formats currently defined
        PF_COUNT(42);

        private final int format;

        PixelFormat(int format) {
            this.format = format;
        }

        public int getFormat() {
            return format;
        }

        public static PixelFormat getPixelFormat(int i) {
            switch (i) {
                case 0:
                    return PF_UNKNOWN;
                case 1:
                    return PF_L8;
                case 2:
                    return PF_L16;
                case 3:
                    return PF_A8;
                case 4:
                    return PF_A4L4;
                case 5:
                    return PF_BYTE_LA;
                case 6:
                    return PF_R5G6B5;
                case 7:
                    return PF_B5G6R5;
                case 31:
                    return PF_R3G3B2;
                case 8:
                    return PF_A4R4G4B4;
                case 9:
                    return PF_A1R5G5B5;
                case 10:
                    return PF_R8G8B8;
                case 11:
                    return PF_B8G8R8;
                case 12:
                    return PF_A8R8G8B8;
                case 13:
                    return PF_A8B8G8R8;
                case 14:
                    return PF_B8G8R8A8;
                case 28:
                    return PF_R8G8B8A8;
                case 26:
                    return PF_X8R8G8B8;
                case 27:
                    return PF_X8B8G8R8;
                case 15:
                    return PF_A2R10G10B10;
                case 16:
                    return PF_A2B10G10R10;
                case 17:
                    return PF_DXT1;
                case 18:
                    return PF_DXT2;
                case 19:
                    return PF_DXT3;
                case 20:
                    return PF_DXT4;
                case 21:
                    return PF_DXT5;
                case 32:
                    return PF_FLOAT16_R;
                case 22:
                    return PF_FLOAT16_RGB;
                case 23:
                    return PF_FLOAT16_RGBA;
                case 33:
                    return PF_FLOAT32_R;
                case 24:
                    return PF_FLOAT32_RGB;
                case 25:
                    return PF_FLOAT32_RGBA;
                case 35:
                    return PF_FLOAT16_GR;
                case 36:
                    return PF_FLOAT32_GR;
                case 29:
                    return PF_DEPTH;
                case 30:
                    return PF_SHORT_RGBA;
                case 34:
                    return PF_SHORT_GR;
                case 37:
                    return PF_SHORT_RGB;
                case 38:
                    return PF_PVRTC_RGB2;
                case 39:
                    return PF_PVRTC_RGBA2;
                case 40:
                    return PF_PVRTC_RGB4;
                case 41:
                    return PF_PVRTC_RGBA4;
                default:
                    throw new IllegalArgumentException("Invalid value " + i +
                            " for pixel format");
            }
        }
    }

    public enum PixelFormatFlags {

        //No flag for java compatibility
        PFF_NOFLAGS(0),
        // This format has an alpha channel
        PFF_HASALPHA(0x00000001),
        // This format is compressed. This invalidates the values in elemBytes,
        // elemBits and the bit counts as these might not be fixed in a compressed format.
        PFF_COMPRESSED(0x00000002),
        // This is a floating point format
        PFF_FLOAT(0x00000004),
        // This is a depth format (for depth textures)
        PFF_DEPTH(0x00000008),
        // Format is in native endian. Generally true for the 16, 24 and 32 bits
        // formats which can be represented as machine integers.
        PFF_NATIVEENDIAN(0x00000010),
        // This is an intensity format instead of a RGB one. The luminance
        // replaces R,G and B. (but not A)
        PFF_LUMINANCE(0x00000020);

        private final int flag;

        PixelFormatFlags(int flag) {
            this.flag = flag;
        }

        public int getFlag() {
            return flag;
        }
    }

    public enum PixelComponentType {
        PCT_BYTE,    /// Byte per component (8 bit fixed 0.0..1.0)
        PCT_SHORT,   /// Short per component (16 bit fixed 0.0..1.0))
        PCT_FLOAT16, /// 16 bit float per component
        PCT_FLOAT32, /// 32 bit float per component
        PCT_COUNT    /// Number of pixel types
    }

	/*struct PixelFormatDescription {
        /* Name of the format, as in the enum 
        const char *name;
        /* Number of bytes one element (colour value) takes. 
        unsigned char elemBytes;
        /* Pixel format flags, see enum PixelFormatFlags for the bit field
        * definitions
        
        uint32 flags;
        /** Component type
         
        PixelComponentType componentType;
        /** Component count
         
        unsigned char componentCount;
        /* Number of bits for red(or luminance), green, blue, alpha
        *
        unsigned char rbits,gbits,bbits,abits; /*, ibits, dbits, ... */

        /* Masks and shifts as used by packers/unpackers *
        uint32 rmask, gmask, bmask, amask;
        unsigned char rshift, gshift, bshift, ashift;
    };*/
	
/*	public class PixelFormatDescription {
		public String name;
		public byte elemBytes;
		public int flags;
		public PixelComponentType componentType;
		public byte componentCount;
		public byte rbits, gbits, bbits, abits;
		public int rmask, gmask, bmask, amask;
		public byte rshift, gshift, bshift, ashift;
		
		public PixelFormatDescription(String name, byte elemBytes, int flags,
				PixelComponentType componentType, byte componentCount, 
				byte rbits, byte gbits, byte bbits, byte abits,
				int rmask, int gmask, int bmask, int amask,
				byte rshift, byte gshift, byte bshift, byte ashift) {
			this.name = name;
			this.elemBytes = elemBytes;
			this.flags = flags;
			this.componentType = componentType;
			this.componentCount = componentCount;
			this.rbits = rbits;
			this.gbits = gbits;
			this.bbits = bbits;
			this.abits = abits;
			this.rmask = rmask;
			this.gmask = gmask;
			this.bmask = bmask;
			this.amask = amask;
			this.rshift = rshift;
			this.gshift = gshift;
			this.bshift = bshift;
			this.ashift = ashift;
		}
		
		public PixelFormatDescription(String name, int elemBytes, int flags,
				PixelComponentType componentType, int componentCount, 
				int rbits, int gbits, int bbits, int abits,
				int rmask, int gmask, int bmask, int amask,
				int rshift, int gshift, int bshift, int ashift) {
			this.name = name;
			this.elemBytes = (byte)elemBytes;
			this.flags = flags;
			this.componentType = componentType;
			this.componentCount = (byte)componentCount;
			this.rbits = (byte)rbits;
			this.gbits = (byte)gbits;
			this.bbits = (byte)bbits;
			this.abits = (byte)abits;
			this.rmask = rmask;
			this.gmask = gmask;
			this.bmask = bmask;
			this.amask = amask;
			this.rshift = (byte)rshift;
			this.gshift = (byte)gshift;
			this.bshift = (byte)bshift;
			this.ashift = (byte)ashift;
		}
	}*/

    public static final ENG_PixelFormatDescription[] _pixelFormats = {
            new ENG_PixelFormatDescription("PF_UNKNOWN",
        /* Bytes per element */
                    (byte) 0,
        /* Flags */
                    0,
        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 0,
        /* rbits, gbits, bbits, abits */
                    0, 0, 0, 0,
        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_L8",
        /* Bytes per element */
                    1,
        /* Flags */
                    PixelFormatFlags.PFF_LUMINANCE.getFlag() |
                            PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 1,
        /* rbits, gbits, bbits, abits */
                    8, 0, 0, 0,
        /* Masks and shifts */
                    0xFF, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_L16",
                /* Bytes per element */
                    2,
                /* Flags */
                    PixelFormatFlags.PFF_LUMINANCE.getFlag() |
                            PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
                /* Component type and count */
                    PixelComponentType.PCT_SHORT, 1,
                /* rbits, gbits, bbits, abits */
                    16, 0, 0, 0,
                /* Masks and shifts */
                    0xFFFF, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_A8",
                        /* Bytes per element */
                    1,
                        /* Flags */
                    PixelFormatFlags.PFF_HASALPHA.getFlag() |
                            PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 1,
                        /* rbits, gbits, bbits, abits */
                    0, 0, 0, 8,
                        /* Masks and shifts */
                    0, 0, 0, 0xFF, 0, 0, 0, 0),


            new ENG_PixelFormatDescription("PF_A4L4",
                        /* Bytes per element */
                    1,
                        /* Flags */
                    PixelFormatFlags.PFF_HASALPHA.getFlag() |
                            PixelFormatFlags.PFF_LUMINANCE.getFlag() |
                            PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 2,
                        /* rbits, gbits, bbits, abits */
                    4, 0, 0, 4,
                        /* Masks and shifts */
                    0x0F, 0, 0, 0xF0, 0, 0, 0, 4),

            new ENG_PixelFormatDescription("PF_BYTE_LA",
                        /* Bytes per element */
                    2,
                        /* Flags */
                    PixelFormatFlags.PFF_HASALPHA.getFlag() |
                            PixelFormatFlags.PFF_LUMINANCE.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 2,
                        /* rbits, gbits, bbits, abits */
                    8, 0, 0, 8,
                        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_R5G6B5",
                        /* Bytes per element */
                    2,
                        /* Flags */
                    PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 3,
                        /* rbits, gbits, bbits, abits */
                    5, 6, 5, 0,
                        /* Masks and shifts */
                    0xF800, 0x07E0, 0x001F, 0,
                    11, 5, 0, 0),

            new ENG_PixelFormatDescription("PF_B5G6R5",
                        /* Bytes per element */
                    2,
                        /* Flags */
                    PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 3,
                        /* rbits, gbits, bbits, abits */
                    5, 6, 5, 0,
                        /* Masks and shifts */
                    0x001F, 0x07E0, 0xF800, 0,
                    0, 5, 11, 0),

            new ENG_PixelFormatDescription("PF_A4R4G4B4",
                        /* Bytes per element */
                    2,
                        /* Flags */
                    PixelFormatFlags.PFF_HASALPHA.getFlag() |
                            PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 4,
                        /* rbits, gbits, bbits, abits */
                    4, 4, 4, 4,
                        /* Masks and shifts */
                    0x0F00, 0x00F0, 0x000F, 0xF000,
                    8, 4, 0, 12),

            new ENG_PixelFormatDescription("PF_A1R5G5B5",
                        /* Bytes per element */
                    2,
                        /* Flags */
                    PixelFormatFlags.PFF_HASALPHA.getFlag() |
                            PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 4,
                        /* rbits, gbits, bbits, abits */
                    5, 5, 5, 1,
                        /* Masks and shifts */
                    0x7C00, 0x03E0, 0x001F, 0x8000,
                    10, 5, 0, 15),

            new ENG_PixelFormatDescription("PF_R8G8B8",
				        /* Bytes per element */
                    3,  // 24 bit integer -- special
				        /* Flags */
                    PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
				        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 3,
				        /* rbits, gbits, bbits, abits */
                    8, 8, 8, 0,
				        /* Masks and shifts */
                    0xFF0000, 0x00FF00, 0x0000FF, 0,
                    16, 8, 0, 0),

            new ENG_PixelFormatDescription("PF_B8G8R8",
        	        /* Bytes per element */
                    3,  // 24 bit integer -- special
        	        /* Flags */
                    PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
        	        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 3,
        	        /* rbits, gbits, bbits, abits */
                    8, 8, 8, 0,
        	        /* Masks and shifts */
                    0x0000FF, 0x00FF00, 0xFF0000, 0,
                    0, 8, 16, 0),

            new ENG_PixelFormatDescription("PF_A8R8G8B8",
    	                /* Bytes per element */
                    4,
    	                /* Flags */
                    PixelFormatFlags.PFF_HASALPHA.getFlag() |
                            PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
    	                /* Component type and count */
                    PixelComponentType.PCT_BYTE, 4,
    	                /* rbits, gbits, bbits, abits */
                    8, 8, 8, 8,
    	                /* Masks and shifts */
                    0x00FF0000, 0x0000FF00, 0x000000FF, 0xFF000000,
                    16, 8, 0, 24),

            new ENG_PixelFormatDescription("PF_A8B8G8R8",
                        /* Bytes per element */
                    4,
                        /* Flags */
                    PixelFormatFlags.PFF_HASALPHA.getFlag() |
                            PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 4,
                        /* rbits, gbits, bbits, abits */
                    8, 8, 8, 8,
                        /* Masks and shifts */
                    0x000000FF, 0x0000FF00, 0x00FF0000, 0xFF000000,
                    0, 8, 16, 24),

            new ENG_PixelFormatDescription("PF_B8G8R8A8",
                        /* Bytes per element */
                    4,
                        /* Flags */
                    PixelFormatFlags.PFF_HASALPHA.getFlag() |
                            PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 4,
                        /* rbits, gbits, bbits, abits */
                    8, 8, 8, 8,
                        /* Masks and shifts */
                    0x0000FF00, 0x00FF0000, 0xFF000000, 0x000000FF,
                    8, 16, 24, 0),

            new ENG_PixelFormatDescription("PF_A2R10G10B10",
                        /* Bytes per element */
                    4,
                        /* Flags */
                    PixelFormatFlags.PFF_HASALPHA.getFlag() |
                            PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 4,
                        /* rbits, gbits, bbits, abits */
                    10, 10, 10, 2,
                        /* Masks and shifts */
                    0x3FF00000, 0x000FFC00, 0x000003FF, 0xC0000000,
                    20, 10, 0, 30),

            new ENG_PixelFormatDescription("PF_A2B10G10R10",
                        /* Bytes per element */
                    4,
                        /* Flags */
                    PixelFormatFlags.PFF_HASALPHA.getFlag() |
                            PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 4,
                        /* rbits, gbits, bbits, abits */
                    10, 10, 10, 2,
                        /* Masks and shifts */
                    0x000003FF, 0x000FFC00, 0x3FF00000, 0xC0000000,
                    0, 10, 20, 30),

            new ENG_PixelFormatDescription("PF_DXT1",
                        /* Bytes per element */
                    0,
                        /* Flags */
                    PixelFormatFlags.PFF_COMPRESSED.getFlag() |
                            PixelFormatFlags.PFF_HASALPHA.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 3, // No alpha
                        /* rbits, gbits, bbits, abits */
                    0, 0, 0, 0,
                        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_DXT2",
                        /* Bytes per element */
                    0,
                        /* Flags */
                    PixelFormatFlags.PFF_COMPRESSED.getFlag() |
                            PixelFormatFlags.PFF_HASALPHA.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 4,
                        /* rbits, gbits, bbits, abits */
                    0, 0, 0, 0,
                        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_DXT3",
                        /* Bytes per element */
                    0,
                        /* Flags */
                    PixelFormatFlags.PFF_COMPRESSED.getFlag() |
                            PixelFormatFlags.PFF_HASALPHA.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 4,
                        /* rbits, gbits, bbits, abits */
                    0, 0, 0, 0,
                        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_DXT4",
                        /* Bytes per element */
                    0,
                        /* Flags */
                    PixelFormatFlags.PFF_COMPRESSED.getFlag() |
                            PixelFormatFlags.PFF_HASALPHA.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 4,
                        /* rbits, gbits, bbits, abits */
                    0, 0, 0, 0,
                        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_DXT5",
                        /* Bytes per element */
                    0,
                        /* Flags */
                    PixelFormatFlags.PFF_COMPRESSED.getFlag() |
                            PixelFormatFlags.PFF_HASALPHA.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 4,
                        /* rbits, gbits, bbits, abits */
                    0, 0, 0, 0,
                        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_FLOAT16_RGB",
                        /* Bytes per element */
                    6,
                        /* Flags */
                    PixelFormatFlags.PFF_FLOAT.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_FLOAT16, 3,
                        /* rbits, gbits, bbits, abits */
                    16, 16, 16, 0,
                        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_FLOAT16_RGBA",
                        /* Bytes per element */
                    8,
                        /* Flags */
                    PixelFormatFlags.PFF_FLOAT.getFlag() |
                            PixelFormatFlags.PFF_HASALPHA.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_FLOAT16, 4,
                        /* rbits, gbits, bbits, abits */
                    16, 16, 16, 16,
                        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_FLOAT32_RGB",
                        /* Bytes per element */
                    12,
                        /* Flags */
                    PixelFormatFlags.PFF_FLOAT.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_FLOAT32, 3,
                        /* rbits, gbits, bbits, abits */
                    32, 32, 32, 0,
                        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_FLOAT32_RGBA",
                        /* Bytes per element */
                    16,
                        /* Flags */
                    PixelFormatFlags.PFF_FLOAT.getFlag() |
                            PixelFormatFlags.PFF_HASALPHA.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_FLOAT32, 4,
                        /* rbits, gbits, bbits, abits */
                    32, 32, 32, 32,
                        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_X8R8G8B8",
                        /* Bytes per element */
                    4,
                        /* Flags */
                    PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 3,
                        /* rbits, gbits, bbits, abits */
                    8, 8, 8, 0,
                        /* Masks and shifts */
                    0x00FF0000, 0x0000FF00, 0x000000FF, 0xFF000000,
                    16, 8, 0, 24),

            new ENG_PixelFormatDescription("PF_X8B8G8R8",
                        /* Bytes per element */
                    4,
                        /* Flags */
                    PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 3,
                        /* rbits, gbits, bbits, abits */
                    8, 8, 8, 0,
                        /* Masks and shifts */
                    0x000000FF, 0x0000FF00, 0x00FF0000, 0xFF000000,
                    0, 8, 16, 24),

            new ENG_PixelFormatDescription("PF_R8G8B8A8",
                        /* Bytes per element */
                    4,
                        /* Flags */
                    PixelFormatFlags.PFF_HASALPHA.getFlag() |
                            PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 4,
                        /* rbits, gbits, bbits, abits */
                    8, 8, 8, 8,
                        /* Masks and shifts */
                    0xFF000000, 0x00FF0000, 0x0000FF00, 0x000000FF,
                    24, 16, 8, 0),

            new ENG_PixelFormatDescription("PF_DEPTH",
                        /* Bytes per element */
                    4,
                        /* Flags */
                    PixelFormatFlags.PFF_DEPTH.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_FLOAT32, 1, // ?
                        /* rbits, gbits, bbits, abits */
                    0, 0, 0, 0,
                        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_SHORT_RGBA",
        				/* Bytes per element */
                    8,
        		        /* Flags */
                    PixelFormatFlags.PFF_HASALPHA.getFlag(),
        		        /* Component type and count */
                    PixelComponentType.PCT_SHORT, 4,
        		        /* rbits, gbits, bbits, abits */
                    16, 16, 16, 16,
        		        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_R3G3B2",
				        /* Bytes per element */
                    1,
				        /* Flags */
                    PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
				        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 3,
				        /* rbits, gbits, bbits, abits */
                    3, 3, 2, 0,
				        /* Masks and shifts */
                    0xE0, 0x1C, 0x03, 0,
                    5, 2, 0, 0),

            new ENG_PixelFormatDescription("PF_FLOAT16_R",
		                /* Bytes per element */
                    2,
		                /* Flags */
                    PixelFormatFlags.PFF_FLOAT.getFlag(),
		                /* Component type and count */
                    PixelComponentType.PCT_FLOAT16, 1,
		                /* rbits, gbits, bbits, abits */
                    16, 0, 0, 0,
		                /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_FLOAT32_R",
                        /* Bytes per element */
                    4,
                        /* Flags */
                    PixelFormatFlags.PFF_FLOAT.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_FLOAT32, 1,
                        /* rbits, gbits, bbits, abits */
                    32, 0, 0, 0,
                        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_SHORT_GR",
                        /* Bytes per element */
                    4,
                        /* Flags */
                    PixelFormatFlags.PFF_NATIVEENDIAN.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_SHORT, 2,
                        /* rbits, gbits, bbits, abits */
                    16, 16, 0, 0,
                        /* Masks and shifts */
                    0x0000FFFF, 0xFFFF0000, 0, 0,
                    0, 16, 0, 0),

            new ENG_PixelFormatDescription("PF_FLOAT16_GR",
				        /* Bytes per element */
                    4,
				        /* Flags */
                    PixelFormatFlags.PFF_FLOAT.getFlag(),
				        /* Component type and count */
                    PixelComponentType.PCT_FLOAT16, 2,
				        /* rbits, gbits, bbits, abits */
                    16, 16, 0, 0,
				        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_FLOAT32_GR",
		                /* Bytes per element */
                    8,
		                /* Flags */
                    PixelFormatFlags.PFF_FLOAT.getFlag(),
		                /* Component type and count */
                    PixelComponentType.PCT_FLOAT32, 2,
		                /* rbits, gbits, bbits, abits */
                    32, 32, 0, 0,
		                /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_SHORT_RGB",
                		/* Bytes per element */
                    6,
                        /* Flags */
                    PixelFormatFlags.PFF_NOFLAGS.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_SHORT, 3,
                        /* rbits, gbits, bbits, abits */
                    16, 16, 16, 0,
                        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_PVRTC_RGB2",
        		        /* Bytes per element */
                    0,
        		        /* Flags */
                    PixelFormatFlags.PFF_COMPRESSED.getFlag(),
        		        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 3,
        		        /* rbits, gbits, bbits, abits */
                    0, 0, 0, 0,
        		        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_PVRTC_RGBA2",
		                /* Bytes per element */
                    0,
		                /* Flags */
                    PixelFormatFlags.PFF_COMPRESSED.getFlag() |
                            PixelFormatFlags.PFF_HASALPHA.getFlag(),
		                /* Component type and count */
                    PixelComponentType.PCT_BYTE, 4,
		                /* rbits, gbits, bbits, abits */
                    0, 0, 0, 0,
		                /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_PVRTC_RGB4",
                        /* Bytes per element */
                    0,
                        /* Flags */
                    PixelFormatFlags.PFF_COMPRESSED.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 3,
                        /* rbits, gbits, bbits, abits */
                    0, 0, 0, 0,
                        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0),

            new ENG_PixelFormatDescription("PF_PVRTC_RGBA4",
                        /* Bytes per element */
                    0,
                        /* Flags */
                    PixelFormatFlags.PFF_COMPRESSED.getFlag() |
                            PixelFormatFlags.PFF_HASALPHA.getFlag(),
                        /* Component type and count */
                    PixelComponentType.PCT_BYTE, 4,
                        /* rbits, gbits, bbits, abits */
                    0, 0, 0, 0,
                        /* Masks and shifts */
                    0, 0, 0, 0, 0, 0, 0, 0)
    };

    public static int getNumElemBytes(PixelFormat format) {
        return getDescriptionFor(format).elemBytes;
    }

    public static int getNumElemBits(PixelFormat format) {
        return getDescriptionFor(format).elemBytes * 8;
    }

    public static int getFlags(PixelFormat format) {
        return getDescriptionFor(format).flags;
    }

    public static boolean hasAlpha(PixelFormat format) {
        return ((getFlags(format) & PixelFormatFlags.PFF_HASALPHA.getFlag()) > 0);
    }

    public static boolean isFloatingPoint(PixelFormat format) {
        return ((getFlags(format) & PixelFormatFlags.PFF_FLOAT.getFlag()) > 0);
    }

    public static boolean isCompressed(PixelFormat format) {
        return ((getFlags(format) & PixelFormatFlags.PFF_COMPRESSED.getFlag()) > 0);
    }

    public static boolean isDepth(PixelFormat format) {
        return ((getFlags(format) & PixelFormatFlags.PFF_DEPTH.getFlag()) > 0);
    }

    public static boolean isNativeEndian(PixelFormat format) {
        return ((getFlags(format) & PixelFormatFlags.PFF_NATIVEENDIAN.getFlag()) > 0);
    }

    public static boolean isLuminance(PixelFormat format) {
        return ((getFlags(format) & PixelFormatFlags.PFF_LUMINANCE.getFlag()) > 0);
    }

    public static boolean isValidExtent(int width, int height, int depth,
                                        PixelFormat format) {
        if (isCompressed(format)) {
            switch (format) {
                case PF_DXT1:
                case PF_DXT2:
                case PF_DXT3:
                case PF_DXT4:
                case PF_DXT5:
                    return (((width & 3) == 0) && ((height & 3) == 0) && (depth == 1));
                default:
                    return true;
            }
        } else {
            return true;
        }
    }

    public static int getMemorySize(int width, int height, int depth,
                                    PixelFormat format) {
        if (isCompressed(format)) {
            switch (format) {
                case PF_DXT1:
                    return ((width + 3) / 4) * ((height + 3) / 4) * 8 * depth;
                case PF_DXT2:
                case PF_DXT3:
                case PF_DXT4:
                case PF_DXT5:
                    return ((width + 3) / 4) * ((height + 3) / 4) * 16 * depth;
                case PF_PVRTC_RGB2:
                case PF_PVRTC_RGBA2:
                    if (depth != 1) {
                        throw new IllegalArgumentException("For PVRTC depth must be 1");
                    }
                    return (Math.max(width, 16) * Math.max(height, 8) * 2 + 7) / 8;
                case PF_PVRTC_RGB4:
                case PF_PVRTC_RGBA4:
                    if (depth != 1) {
                        throw new IllegalArgumentException("For PVRTC depth must be 1");
                    }
                    return (Math.max(width, 8) * Math.max(height, 8) * 4 + 7) / 8;
                default:
                    throw new IllegalArgumentException("Invalid compressed pixel format");
            }
        } else {
            return width * height * depth * getNumElemBytes(format);
        }
    }

    public static void getBitDepths(PixelFormat format, int[] rgba) {
        getBitDepths(format, rgba, 0);
    }

    public static void getBitDepths(PixelFormat format, int[] rgba, int offset) {
        ENG_PixelFormatDescription des = getDescriptionFor(format);
        rgba[offset] = des.rbits;
        rgba[offset + 1] = des.gbits;
        rgba[offset + 2] = des.bbits;
        rgba[offset + 3] = des.abits;
    }

    public static void getBitMasks(PixelFormat format, int[] rgba) {
        getBitMasks(format, rgba, 0);
    }

    public static void getBitMasks(PixelFormat format, int[] rgba, int offset) {
        ENG_PixelFormatDescription des = getDescriptionFor(format);
        rgba[offset] = des.rmask;
        rgba[offset + 1] = des.gmask;
        rgba[offset + 2] = des.bmask;
        rgba[offset + 3] = des.amask;
    }

    public static void getBitShifts(PixelFormat format, int[] rgba) {
        getBitShifts(format, rgba, 0);
    }

    public static void getBitShifts(PixelFormat format, int[] rgba, int offset) {
        ENG_PixelFormatDescription des = getDescriptionFor(format);
        rgba[offset] = des.rshift;
        rgba[offset + 1] = des.gshift;
        rgba[offset + 2] = des.bshift;
        rgba[offset + 3] = des.ashift;
    }

    public static String getFormatName(PixelFormat format) {
        return getDescriptionFor(format).name;
    }

    public static boolean isAccessible(PixelFormat srcFormat) {
        if (srcFormat == PixelFormat.PF_UNKNOWN) {
            return false;
        }
        int flags = getFlags(srcFormat);
        return !(((flags & PixelFormatFlags.PFF_COMPRESSED.getFlag()) != 0) ||
                ((flags & PixelFormatFlags.PFF_DEPTH.getFlag()) != 0));
    }

    public static PixelComponentType getComponentType(PixelFormat format) {
        return getDescriptionFor(format).componentType;
    }

    public static PixelFormat getFormatFromName(String name, boolean accessibleOnly,
                                                boolean caseSensitive) {
        if (!caseSensitive) {
            name = name.toUpperCase(Locale.US);
        }
        for (PixelFormat format : PixelFormat.values()) {
            if ((accessibleOnly) || (isAccessible(format))) {
                if (name.equals(getFormatName(format))) {
                    return format;
                }
            }
        }
        return PixelFormat.PF_UNKNOWN;
    }

    public static PixelFormat getFormatForBitDepths(PixelFormat fmt, short integerBits,
                                                    short floatBits) {
        switch (integerBits) {
            case 16:
                switch (fmt) {
                    case PF_R8G8B8:
                    case PF_X8R8G8B8:
                        return PixelFormat.PF_R5G6B5;

                    case PF_B8G8R8:
                    case PF_X8B8G8R8:
                        return PixelFormat.PF_B5G6R5;

                    case PF_A8R8G8B8:
                    case PF_R8G8B8A8:
                    case PF_A8B8G8R8:
                    case PF_B8G8R8A8:
                        return PixelFormat.PF_A4R4G4B4;

                    case PF_A2R10G10B10:
                    case PF_A2B10G10R10:
                        return PixelFormat.PF_A1R5G5B5;
                    default:
                        // use original image format
                        break;
                }
                break;
            case 32:
                switch (fmt) {
                    case PF_R5G6B5:
                        return PixelFormat.PF_X8R8G8B8;

                    case PF_B5G6R5:
                        return PixelFormat.PF_X8B8G8R8;

                    case PF_A4R4G4B4:
                        return PixelFormat.PF_A8R8G8B8;

                    case PF_A1R5G5B5:
                        return PixelFormat.PF_A2R10G10B10;

                    default:
                        // use original image format
                        break;
                }
                break;
            default:
                // use original image format
                break;
        }

        switch (floatBits) {
            case 16:
                switch (fmt) {
                    case PF_FLOAT32_R:
                        return PixelFormat.PF_FLOAT16_R;

                    case PF_FLOAT32_RGB:
                        return PixelFormat.PF_FLOAT16_RGB;

                    case PF_FLOAT32_RGBA:
                        return PixelFormat.PF_FLOAT16_RGBA;

                    default:
                        // use original image format
                        break;
                }
                break;

            case 32:
                switch (fmt) {
                    case PF_FLOAT16_R:
                        return PixelFormat.PF_FLOAT32_R;

                    case PF_FLOAT16_RGB:
                        return PixelFormat.PF_FLOAT32_RGB;

                    case PF_FLOAT16_RGBA:
                        return PixelFormat.PF_FLOAT32_RGBA;

                    default:
                        // use original image format
                        break;
                }
                break;

            default:
                // use original image format
                break;
        }
        return fmt;
    }

    public static void packColour(ENG_ColorValue colour, PixelFormat pf,
                                  ByteBuffer dest) {
        packColour(colour.r, colour.g, colour.b, colour.a, pf, dest);
    }

    /** @noinspection deprecation*/
    public static void packColour(ENG_Color colour, PixelFormat pf,
                                  ByteBuffer dest) {
        packColour(colour.r, colour.g, colour.b, colour.a, pf, dest);
    }

    public static void packColour(byte r, byte g, byte b, byte a, PixelFormat pf,
                                  ByteBuffer dest) {
        ENG_PixelFormatDescription des = getDescriptionFor(pf);
        if ((des.flags & PixelFormatFlags.PFF_NATIVEENDIAN.getFlag()) != 0) {
			/*unsigned int value = ((Bitwise::fixedToFixed(r, 8, des.rbits)<<des.rshift) & des.rmask) |
                ((Bitwise::fixedToFixed(g, 8, des.gbits)<<des.gshift) & des.gmask) |
                ((Bitwise::fixedToFixed(b, 8, des.bbits)<<des.bshift) & des.bmask) |
                ((Bitwise::fixedToFixed(a, 8, des.abits)<<des.ashift) & des.amask);
            // And write to memory
            Bitwise::intWrite(dest, des.elemBytes, value);*/
            int value =
                    ((ENG_Bitwise.fixedToFixed(r, 8, des.rbits) << des.rshift) & des.rmask) |
                            ((ENG_Bitwise.fixedToFixed(g, 8, des.gbits) << des.gshift) & des.gmask) |
                            ((ENG_Bitwise.fixedToFixed(b, 8, des.bbits) << des.bshift) & des.bmask) |
                            ((ENG_Bitwise.fixedToFixed(a, 8, des.abits) << des.ashift) & des.amask);
            ENG_Bitwise.intWrite(dest, des.elemBytes, value);
        } else {
            packColour((float) r / 255.0f, (float) g / 255.0f, (float) b / 255.0f,
                    (float) a / 255.0f, pf, dest);
        }
    }

    public static void packColour(float r, float g, float b, float a, PixelFormat pf,
                                  ByteBuffer dest) {
        ENG_PixelFormatDescription des = getDescriptionFor(pf);
        if ((des.flags & PixelFormatFlags.PFF_NATIVEENDIAN.getFlag()) != 0) {
			/*const unsigned int value = ((Bitwise::floatToFixed(r, des.rbits)<<des.rshift) & des.rmask) |
                ((Bitwise::floatToFixed(g, des.gbits)<<des.gshift) & des.gmask) |
                ((Bitwise::floatToFixed(b, des.bbits)<<des.bshift) & des.bmask) |
                ((Bitwise::floatToFixed(a, des.abits)<<des.ashift) & des.amask);*/
            int value =
                    ((ENG_Bitwise.floatToFixed(r, des.rbits) << des.rshift) & des.rmask) |
                            ((ENG_Bitwise.floatToFixed(g, des.gbits) << des.gshift) & des.gmask) |
                            ((ENG_Bitwise.floatToFixed(b, des.bbits) << des.bshift) & des.bmask) |
                            ((ENG_Bitwise.floatToFixed(a, des.abits) << des.ashift) & des.amask);
            ENG_Bitwise.intWrite(dest, des.elemBytes, value);
        } else {
            switch (pf) {
                case PF_FLOAT32_R:
                    dest.putFloat(r);
                    break;
                case PF_FLOAT32_GR:
                    dest.putFloat(g);
                    dest.putFloat(r);
                    break;
                case PF_FLOAT32_RGB:
                    dest.putFloat(r);
                    dest.putFloat(g);
                    dest.putFloat(b);
                    break;
                case PF_FLOAT32_RGBA:
                    dest.putFloat(r);
                    dest.putFloat(g);
                    dest.putFloat(b);
                    dest.putFloat(a);
                    break;
                case PF_FLOAT16_R:
                    dest.putShort(ENG_Bitwise.floatToHalf(r));
                    break;
                case PF_FLOAT16_GR:
                    dest.putShort(ENG_Bitwise.floatToHalf(r));
                    dest.putShort(ENG_Bitwise.floatToHalf(g));
                    break;
                case PF_FLOAT16_RGB:
                    dest.putShort(ENG_Bitwise.floatToHalf(r));
                    dest.putShort(ENG_Bitwise.floatToHalf(g));
                    dest.putShort(ENG_Bitwise.floatToHalf(b));
                    break;
                case PF_FLOAT16_RGBA:
                    dest.putShort(ENG_Bitwise.floatToHalf(r));
                    dest.putShort(ENG_Bitwise.floatToHalf(g));
                    dest.putShort(ENG_Bitwise.floatToHalf(b));
                    dest.putShort(ENG_Bitwise.floatToHalf(a));
                    break;
                case PF_SHORT_RGB:
                    dest.putShort((short) ENG_Bitwise.floatToFixed(r, 16));
                    dest.putShort((short) ENG_Bitwise.floatToFixed(g, 16));
                    dest.putShort((short) ENG_Bitwise.floatToFixed(b, 16));
                    break;
                case PF_SHORT_RGBA:
                    dest.putShort((short) ENG_Bitwise.floatToFixed(r, 16));
                    dest.putShort((short) ENG_Bitwise.floatToFixed(g, 16));
                    dest.putShort((short) ENG_Bitwise.floatToFixed(b, 16));
                    dest.putShort((short) ENG_Bitwise.floatToFixed(a, 16));
                    break;
                case PF_BYTE_LA:
                    dest.put((byte) ENG_Bitwise.floatToFixed(r, 8));
                    dest.put((byte) ENG_Bitwise.floatToFixed(a, 8));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid PixelFormat");
            }
        }
    }

    /** @noinspection deprecation*/
    public static void unpackColour(ENG_Color colour, PixelFormat pf,
                                    ByteBuffer src) {
        ENG_PixelFormatDescription des = getDescriptionFor(pf);
        if ((des.flags & PixelFormatFlags.PFF_NATIVEENDIAN.getFlag()) != 0) {
			/*const unsigned int value = Bitwise::intRead(src, des.elemBytes);
            if(des.flags & PFF_LUMINANCE)
            {
                // Luminance format -- only rbits used
                *r = *g = *b = (uint8)Bitwise::fixedToFixed(
                    (value & des.rmask)>>des.rshift, des.rbits, 8);
            }
            else
            {
                *r = (uint8)Bitwise::fixedToFixed((value & des.rmask)>>des.rshift, des.rbits, 8);
                *g = (uint8)Bitwise::fixedToFixed((value & des.gmask)>>des.gshift, des.gbits, 8);
                *b = (uint8)Bitwise::fixedToFixed((value & des.bmask)>>des.bshift, des.bbits, 8);
            }*/
            int value = ENG_Bitwise.intRead(src, des.elemBytes);
            if ((des.flags & PixelFormatFlags.PFF_LUMINANCE.getFlag()) != 0) {
                byte temp = (byte) ENG_Bitwise.fixedToFixed(
                        (value & des.rmask) >> des.rshift, des.rbits, 8);
                colour.r = temp;
                colour.g = temp;
                colour.b = temp;
            } else {
                colour.r = (byte) ENG_Bitwise.fixedToFixed(
                        (value & des.rmask) >> des.rshift, des.rbits, 8);
                colour.g = (byte) ENG_Bitwise.fixedToFixed(
                        (value & des.gmask) >> des.gshift, des.gbits, 8);
                colour.b = (byte) ENG_Bitwise.fixedToFixed(
                        (value & des.bmask) >> des.bshift, des.bbits, 8);
            }
			/*if(des.flags & PFF_HASALPHA)
            {
                *a = (uint8)Bitwise::fixedToFixed((value & des.amask)>>des.ashift, des.abits, 8);
            }
            else
            {
                *a = 255; // No alpha, default a component to full
            }*/
            if ((des.flags & PixelFormatFlags.PFF_HASALPHA.getFlag()) != 0) {
                colour.a = (byte) ENG_Bitwise.fixedToFixed(
                        (value & des.amask) >> des.ashift, des.abits, 8);
            } else {
                colour.a = (byte) 255;
            }
        } else {
            ENG_ColorValue col = new ENG_ColorValue();
            unpackColour(col, pf, src);
            colour.r = (byte) ENG_Bitwise.floatToFixed(col.r, 8);
            colour.g = (byte) ENG_Bitwise.floatToFixed(col.g, 8);
            colour.b = (byte) ENG_Bitwise.floatToFixed(col.b, 8);
            colour.a = (byte) ENG_Bitwise.floatToFixed(col.a, 8);
			/*float rr, gg, bb, aa;
            unpackColour(&rr,&gg,&bb,&aa, pf, src);
            *r = (uint8)Bitwise::floatToFixed(rr, 8);
            *g = (uint8)Bitwise::floatToFixed(gg, 8);
            *b = (uint8)Bitwise::floatToFixed(bb, 8);
            *a = (uint8)Bitwise::floatToFixed(aa, 8);*/
        }
    }

    public static void unpackColour(ENG_ColorValue colour, PixelFormat pf,
                                    ByteBuffer src) {
        ENG_PixelFormatDescription des = getDescriptionFor(pf);
        if ((des.flags & PixelFormatFlags.PFF_NATIVEENDIAN.getFlag()) != 0) {
            int value = ENG_Bitwise.intRead(src, des.elemBytes);
            if ((des.flags & PixelFormatFlags.PFF_LUMINANCE.getFlag()) != 0) {
                byte temp = (byte) ENG_Bitwise.fixedToFloat(
                        (value & des.rmask) >> des.rshift, des.rbits);
                colour.r = temp;
                colour.g = temp;
                colour.b = temp;
            } else {
                colour.r = (byte) ENG_Bitwise.fixedToFloat(
                        (value & des.rmask) >> des.rshift, des.rbits);
                colour.g = (byte) ENG_Bitwise.fixedToFloat(
                        (value & des.gmask) >> des.gshift, des.gbits);
                colour.b = (byte) ENG_Bitwise.fixedToFloat(
                        (value & des.bmask) >> des.bshift, des.bbits);
            }
            if ((des.flags & PixelFormatFlags.PFF_HASALPHA.getFlag()) != 0) {
                colour.a = (byte) ENG_Bitwise.fixedToFloat(
                        (value & des.amask) >> des.ashift, des.abits);
            } else {
                colour.a = 1.0f;
            }
        } else {
            switch (pf) {
                case PF_FLOAT32_R:
                    colour.r = colour.g = colour.b = src.getFloat();
                    colour.a = 1.0f;
                    break;
                case PF_FLOAT32_GR:
                    colour.g = src.getFloat();
                    colour.r = colour.b = src.getFloat();
                    colour.a = 1.0f;
                    break;
                case PF_FLOAT32_RGB:
                    colour.r = src.getFloat();
                    colour.g = src.getFloat();
                    colour.b = src.getFloat();
                    colour.a = 1.0f;
                    break;
                case PF_FLOAT32_RGBA:
                    colour.r = src.getFloat();
                    colour.g = src.getFloat();
                    colour.b = src.getFloat();
                    colour.a = src.getFloat();
                    break;
                case PF_FLOAT16_R:
                    colour.r = colour.g = colour.b =
                            ENG_Bitwise.halfToFloat(src.getShort());
                    colour.a = 1.0f;
                    break;
                case PF_FLOAT16_GR:
                    colour.g = ENG_Bitwise.halfToFloat(src.getShort());
                    colour.r = colour.b = ENG_Bitwise.halfToFloat(src.getShort());
                    colour.a = 1.0f;
                    break;
                case PF_FLOAT16_RGB:
                    colour.r = ENG_Bitwise.halfToFloat(src.getShort());
                    colour.g = ENG_Bitwise.halfToFloat(src.getShort());
                    colour.b = ENG_Bitwise.halfToFloat(src.getShort());
                    colour.a = 1.0f;
                    break;
                case PF_FLOAT16_RGBA:
                    colour.r = ENG_Bitwise.halfToFloat(src.getShort());
                    colour.g = ENG_Bitwise.halfToFloat(src.getShort());
                    colour.b = ENG_Bitwise.halfToFloat(src.getShort());
                    colour.a = ENG_Bitwise.halfToFloat(src.getShort());
                    break;
                case PF_SHORT_RGB:
                    colour.r = ENG_Bitwise.fixedToFloat(src.getShort(), 16);
                    colour.g = ENG_Bitwise.fixedToFloat(src.getShort(), 16);
                    colour.b = ENG_Bitwise.fixedToFloat(src.getShort(), 16);
                    colour.a = 1.0f;
                    break;
                case PF_SHORT_RGBA:
                    colour.r = ENG_Bitwise.fixedToFloat(src.getShort(), 16);
                    colour.g = ENG_Bitwise.fixedToFloat(src.getShort(), 16);
                    colour.b = ENG_Bitwise.fixedToFloat(src.getShort(), 16);
                    colour.a = ENG_Bitwise.fixedToFloat(src.getShort(), 16);
                    break;
                case PF_BYTE_LA:
                    colour.r = colour.g = colour.b = ENG_Bitwise.fixedToFloat(src.get(), 8);
                    colour.a = ENG_Bitwise.fixedToFloat(src.get(), 8);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid PixelFormat");
            }
        }
    }

    private static ENG_PixelFormatDescription getDescriptionFor(PixelFormat format) {
        //	int ord = format.getFormat();
        return _pixelFormats[format.getFormat()];
    }

    public static void bulkPixelConversion(ENG_PixelBox src, ENG_PixelBox dst) {
		/*assert(src.getWidth() == dst.getWidth() &&
			   src.getHeight() == dst.getHeight() &&
			   src.getDepth() == dst.getDepth());

		// Check for compressed formats, we don't support decompression, compression or recoding
		if(PixelUtil::isCompressed(src.format) || PixelUtil::isCompressed(dst.format))
		{
			if(src.format == dst.format)
			{
				memcpy(dst.data, src.data, src.getConsecutiveSize());
				return;
			}
			else
			{
				OGRE_EXCEPT(Exception::ERR_NOT_IMPLEMENTED,
					"This method can not be used to compress or decompress images",
					"PixelUtil::bulkPixelConversion");
			}
		}*/
        if ((src.getWidth() != dst.getWidth()) ||
                (src.getHeight() != dst.getHeight()) ||
                (src.getDepth() != dst.getDepth())) {
            throw new IllegalArgumentException(
                    "The images are not the same dimensions");
        }

        if (isCompressed(src.pixelFormat) || isCompressed(dst.pixelFormat)) {
            if (src.pixelFormat == dst.pixelFormat) {
                dst.data.limit(src.getConsecutiveSize());
                src.data.limit(src.getConsecutiveSize());
                dst.data.put(src.data);
                return;
            }
            throw new IllegalArgumentException(
                    "This method can not be used to compress or decompress images");
        }
		
		/*if(src.format == dst.format) {
            // Everything consecutive?
            if(src.isConsecutive() && dst.isConsecutive())
            {
				memcpy(dst.data, src.data, src.getConsecutiveSize());
                return;
            }

            const size_t srcPixelSize = PixelUtil::getNumElemBytes(src.format);
            const size_t dstPixelSize = PixelUtil::getNumElemBytes(dst.format);
            uint8 *srcptr = static_cast<uint8*>(src.data)
                + (src.left + src.top * src.rowPitch + src.front * src.slicePitch) * srcPixelSize;
            uint8 *dstptr = static_cast<uint8*>(dst.data)
				+ (dst.left + dst.top * dst.rowPitch + dst.front * dst.slicePitch) * dstPixelSize;
				*/
        if (src.pixelFormat == dst.pixelFormat) {
            if (src.isConsecutive() && dst.isConsecutive()) {
                dst.data.limit(dst.data.position() + src.getConsecutiveSize());
                src.data.limit(src.data.position() + src.getConsecutiveSize());
                dst.data.put(src.data);
                return;
            }

            int srcPixelSize = getNumElemBytes(src.pixelFormat);
            int dstPixelSize = getNumElemBytes(dst.pixelFormat);
            int srcptr = src.data.position() + (src.left + src.top * src.rowPitch +
                    src.front * src.slicePitch) * srcPixelSize;
            int dstptr = dst.data.position() + (dst.left + dst.top * dst.rowPitch +
                    dst.front * dst.slicePitch) * dstPixelSize;
			
			/*// Calculate pitches+skips in bytes
            const size_t srcRowPitchBytes = src.rowPitch*srcPixelSize;
            //const size_t srcRowSkipBytes = src.getRowSkip()*srcPixelSize;
            const size_t srcSliceSkipBytes = src.getSliceSkip()*srcPixelSize;

            const size_t dstRowPitchBytes = dst.rowPitch*dstPixelSize;
            //const size_t dstRowSkipBytes = dst.getRowSkip()*dstPixelSize;
            const size_t dstSliceSkipBytes = dst.getSliceSkip()*dstPixelSize;

            // Otherwise, copy per row
            const size_t rowSize = src.getWidth()*srcPixelSize;*/

            int srcRowPitchBytes = src.rowPitch * srcPixelSize;
            int srcSliceSkipBytes = src.getSliceSkip() * srcPixelSize;
            int dstRowPitchBytes = dst.rowPitch * dstPixelSize;
            int dstSliceSkipBytes = dst.getSliceSkip() * dstPixelSize;

            int rowSize = src.getWidth() * srcPixelSize;
			
			/*for(size_t z=src.front; z<src.back; z++)
            {
                for(size_t y=src.top; y<src.bottom; y++)
                {
					memcpy(dstptr, srcptr, rowSize);
                    srcptr += srcRowPitchBytes;
                    dstptr += dstRowPitchBytes;
                }
                srcptr += srcSliceSkipBytes;
                dstptr += dstSliceSkipBytes;
            }
            return;*/

            for (int z = src.front; z < src.back; ++z) {
                for (int y = src.top; y < src.bottom; ++y) {
                    dst.data.limit(dstptr + rowSize);
                    src.data.limit(srcptr + rowSize);
                    dst.data.position(dstptr);
                    src.data.position(srcptr);
                    dst.data.put(src.data);
                    srcptr += srcRowPitchBytes;
                    dstptr += dstRowPitchBytes;
                }
                srcptr += srcSliceSkipBytes;
                dstptr += dstSliceSkipBytes;
            }
            return;
        }
		/*// Converting to PF_X8R8G8B8 is exactly the same as converting to
		// PF_A8R8G8B8. (same with PF_X8B8G8R8 and PF_A8B8G8R8)
		if(dst.format == PF_X8R8G8B8 || dst.format == PF_X8B8G8R8)
		{
			// Do the same conversion, with PF_A8R8G8B8, which has a lot of
			// optimized conversions
			PixelBox tempdst = dst;
			tempdst.format = dst.format==PF_X8R8G8B8?PF_A8R8G8B8:PF_A8B8G8R8;
			bulkPixelConversion(src, tempdst);
			return;
		}*/
        if ((dst.pixelFormat == PixelFormat.PF_X8R8G8B8) ||
                (dst.pixelFormat == PixelFormat.PF_X8B8G8R8)) {
            ENG_PixelBox tempdst = new ENG_PixelBox(dst);
            tempdst.pixelFormat = (dst.pixelFormat == PixelFormat.PF_X8R8G8B8) ?
                    PixelFormat.PF_A8R8G8B8 : PixelFormat.PF_A8B8G8R8;
            bulkPixelConversion(src, tempdst);
            return;
        }
		
		/*// Converting from PF_X8R8G8B8 is exactly the same as converting from
		// PF_A8R8G8B8, given that the destination format does not have alpha.
		if((src.format == PF_X8R8G8B8||src.format == PF_X8B8G8R8) && !hasAlpha(dst.format))
		{
			// Do the same conversion, with PF_A8R8G8B8, which has a lot of
			// optimized conversions
			PixelBox tempsrc = src;
			tempsrc.format = src.format==PF_X8R8G8B8?PF_A8R8G8B8:PF_A8B8G8R8;
			bulkPixelConversion(tempsrc, dst);
			return;
		}*/
        if (((src.pixelFormat == PixelFormat.PF_X8R8G8B8) ||
                (src.pixelFormat == PixelFormat.PF_X8B8G8R8)) &&
                (!hasAlpha(dst.pixelFormat))) {
            ENG_PixelBox tempsrc = new ENG_PixelBox(src);
            tempsrc.pixelFormat = (src.pixelFormat == PixelFormat.PF_X8R8G8B8) ?
                    PixelFormat.PF_A8R8G8B8 : PixelFormat.PF_A8B8G8R8;
            bulkPixelConversion(tempsrc, dst);
            return;
        }
//		byte[] array = src.data.array();
//		int conversion = doOptimizedConversion(src, dst);
//        if (MainApp.USE_NATIVE_OPTIMIZATIONS &&
//                src.pixelFormat == PixelFormat.PF_A8R8G8B8 &&
//                dst.pixelFormat == PixelFormat.PF_R8G8B8A8) {
//		/*	if (doOptimizedConversion(src, dst) == 1)
//	        {
//	            // If so, good
//	            return;
//	        }*/
//        }
		
		/*const size_t srcPixelSize = PixelUtil::getNumElemBytes(src.format);
        const size_t dstPixelSize = PixelUtil::getNumElemBytes(dst.format);
        uint8 *srcptr = static_cast<uint8*>(src.data)
            + (src.left + src.top * src.rowPitch + src.front * src.slicePitch) * srcPixelSize;
        uint8 *dstptr = static_cast<uint8*>(dst.data)
            + (dst.left + dst.top * dst.rowPitch + dst.front * dst.slicePitch) * dstPixelSize;*/
        int srcPixelSize = getNumElemBytes(src.pixelFormat);
        int dstPixelSize = getNumElemBytes(dst.pixelFormat);
        int srcptr = src.data.position() + (src.left + src.top * src.rowPitch +
                src.front * src.slicePitch) * srcPixelSize;
        int dstptr = dst.data.position() + (dst.left + dst.top * dst.rowPitch +
                dst.front * dst.slicePitch) * dstPixelSize;
		
		/*const size_t srcRowSkipBytes = src.getRowSkip()*srcPixelSize;
        const size_t srcSliceSkipBytes = src.getSliceSkip()*srcPixelSize;
        const size_t dstRowSkipBytes = dst.getRowSkip()*dstPixelSize;
        const size_t dstSliceSkipBytes = dst.getSliceSkip()*dstPixelSize;*/

        int srcRowSkipBytes = src.getRowSkip() * srcPixelSize;
        int srcSliceSkipBytes = src.getSliceSkip() * srcPixelSize;
        int dstRowSkipBytes = dst.getRowSkip() * dstPixelSize;
        int dstSliceSkipBytes = dst.getSliceSkip() * dstPixelSize;
		
		/*float r,g,b,a;
        for(size_t z=src.front; z<src.back; z++)
        {
            for(size_t y=src.top; y<src.bottom; y++)
            {
                for(size_t x=src.left; x<src.right; x++)
                {
                    unpackColour(&r, &g, &b, &a, src.format, srcptr);
                    packColour(r, g, b, a, dst.format, dstptr);
                    srcptr += srcPixelSize;
                    dstptr += dstPixelSize;
                }
                srcptr += srcRowSkipBytes;
                dstptr += dstRowSkipBytes;
            }
            srcptr += srcSliceSkipBytes;
            dstptr += dstSliceSkipBytes;
        }*/
        ENG_ColorValue c = new ENG_ColorValue();
        //	int srcpos = src.data.position();
        //	int dstpos = dst.data.position();
        for (int z = src.front; z < src.back; ++z) {
            for (int y = src.top; y < src.bottom; ++y) {
                for (int x = src.left; x < src.right; ++x) {
                    //	srcpos = src.data.position();
                    //	dstpos = dst.data.position();
                    src.data.position(srcptr);
                    dst.data.position(dstptr);
                    unpackColour(c, src.pixelFormat, src.data);
                    packColour(c, dst.pixelFormat, dst.data);
                    srcptr += srcPixelSize;
                    dstptr += dstPixelSize;

                }
                srcptr += srcRowSkipBytes;
                dstptr += dstRowSkipBytes;

            }
            srcptr += srcSliceSkipBytes;
            dstptr += dstSliceSkipBytes;
        }
    }

//	static {
    //    System.loadLibrary("pixelconv");
//	}

/*	private static native int doOptimizedConversion(
			ENG_PixelBox src, ENG_PixelBox dst);*/
}
