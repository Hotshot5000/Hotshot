/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelComponentType;

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

public class ENG_PixelFormatDescription {
    public final String name;
    public final byte elemBytes;
    public final int flags;
    public final PixelComponentType componentType;
    public final byte componentCount;
    public final byte rbits;
    public final byte gbits;
    public final byte bbits;
    public final byte abits;
    public final int rmask;
    public final int gmask;
    public final int bmask;
    public final int amask;
    public final byte rshift;
    public final byte gshift;
    public final byte bshift;
    public final byte ashift;

    public ENG_PixelFormatDescription(String name, byte elemBytes, int flags,
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

    public ENG_PixelFormatDescription(String name, int elemBytes, int flags,
                                      PixelComponentType componentType, int componentCount,
                                      int rbits, int gbits, int bbits, int abits,
                                      int rmask, int gmask, int bmask, int amask,
                                      int rshift, int gshift, int bshift, int ashift) {
        this.name = name;
        this.elemBytes = (byte) elemBytes;
        this.flags = flags;
        this.componentType = componentType;
        this.componentCount = (byte) componentCount;
        this.rbits = (byte) rbits;
        this.gbits = (byte) gbits;
        this.bbits = (byte) bbits;
        this.abits = (byte) abits;
        this.rmask = rmask;
        this.gmask = gmask;
        this.bmask = bmask;
        this.amask = amask;
        this.rshift = (byte) rshift;
        this.gshift = (byte) gshift;
        this.bshift = (byte) bshift;
        this.ashift = (byte) ashift;
    }
}
