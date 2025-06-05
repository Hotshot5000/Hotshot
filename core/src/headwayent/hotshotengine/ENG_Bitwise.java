/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:45 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ENG_Bitwise {

    /**
     * Returns the most significant bit set in a value.
     */
    public static int mostSignificantBitSet(int value) {
        /*unsigned int result = 0;
            while (value != 0) {
                ++result;
                value >>= 1;
            }
            return result-1;*/
        int result = 0;
        while (value != 0) {
            ++result;
            value >>= 1;
        }
        return (result - 1);
    }

    /**
     * Returns the closest power-of-two number greater or equal to value.
     *
     * @note 0 and 1 are powers of two, so
     * firstPO2From(0)==0 and firstPO2From(1)==1.
     */
    public static int firstPO2From(int n) {
        --n;
        n |= n >> 16;
        n |= n >> 8;
        n |= n >> 4;
        n |= n >> 2;
        n |= n >> 1;
        ++n;
        return n;
    }

    /**
     * Determines whether the number is power-of-two or not.
     *
     * @note 0 and 1 are tread as power of two.
     */
    public static boolean isPO2(int n) {
        return ((n & (n - 1)) == 0);
    }

    /**
     * Returns the number of bits a pattern must be shifted right by to
     * remove right-hand zeros.
     */
    public static int getBitShift(int mask) {
		/*if (mask == 0)
				return 0;

			unsigned int result = 0;
			while ((mask & 1) == 0) {
				++result;
				mask >>= 1;
			}
			return result;*/
        if (mask == 0) {
            return 0;
        }
        int result = 0;
        while ((mask & 1) == 0) {
            ++result;
            mask >>= 1;
        }
        return result;
    }

    /**
     * Takes a value with a given src bit mask, and produces another
     * value with a desired bit mask.
     *
     * @remarks This routine is useful for colour conversion.
     */
    public static int convertBitPattern(int srcValue, int srcBitMask, int destBitMask) {
		/*// Mask off irrelevant source value bits (if any)
			srcValue = srcValue & srcBitMask;

			// Shift source down to bottom of DWORD
			const unsigned int srcBitShift = getBitShift(srcBitMask);
			srcValue >>= srcBitShift;

			// Get max value possible in source from srcMask
			const SrcT srcMax = srcBitMask >> srcBitShift;

			// Get max available in dest
			const unsigned int destBitShift = getBitShift(destBitMask);
			const DestT destMax = destBitMask >> destBitShift;

			// Scale source value into destination, and shift back
			DestT destValue = (srcValue * destMax) / srcMax;
			return (destValue << destBitShift);*/
        srcValue = srcValue & srcBitMask;
        int srcBitShift = getBitShift(srcBitMask);
        srcValue >>= srcBitShift;
        int srcMax = srcBitMask >> srcBitShift;
        int destBitShift = getBitShift(destBitMask);
        int destMax = destBitMask >> destBitShift;
        int destValue = (srcValue * destMax) / srcMax;
        return (destValue << destBitShift);
    }

    /**
     * Convert N bit colour channel value to P bits. It fills P bits with the
     * bit pattern repeated. (this is /((1<<n)-1) in fixed point)
     */
    public static int fixedToFixed(int value, int n, int p) {
		/*if(n > p) 
            {
                // Less bits required than available; this is easy
                value >>= n-p;
            } 
            else if(n < p)
            {
                // More bits required than are there, do the fill
                // Use old fashioned division, probably better than a loop
                if(value == 0)
                        value = 0;
                else if(value == (static_cast<unsigned int>(1)<<n)-1)
                        value = (1<<p)-1;
                else    value = value*(1<<p)/((1<<n)-1);
            }
            return value;    */
        if (n > p) {
            value >>= n - p;
        } else if (n < p) {
            if (value == 0) {
                value = 0;
            } else if (value == ((1 << n) - 1)) {
                value = (1 << p) - 1;
            } else {
                value = value * (1 << p) / ((1 << n) - 1);
            }
        }
        return value;
    }

    /**
     * Convert floating point colour channel value between 0.0 and 1.0 (otherwise clamped)
     * to integer of a certain number of bits. Works for any value of bits between 0 and 31.
     */
    public static int floatToFixed(float value, int bits) {
		/*if(value <= 0.0f) return 0;
            else if (value >= 1.0f) return (1<<bits)-1;
            else return (unsigned int)(value * (1<<bits));    */
        if (value <= 0.0f) {
            return 0;
        } else if (value >= 1.0f) {
            return ((1 << bits) - 1);
        } else {
            return (int) (value * (1 << bits));
        }
    }

    /**
     * Fixed point to float
     */
    public static float fixedToFloat(int value, int bits) {
        //return (float)value/(float)((1<<bits)-1);
        return (float) value / (float) ((1 << bits) - 1);
    }

    /**
     * Write a n*8 bits integer value to memory in native endian.
     */
    public static void intWrite(ByteBuffer dest, int n, int value) {
		/*switch(n) {
                case 1:
                    ((uint8*)dest)[0] = (uint8)value;
                    break;
                case 2:
                    ((uint16*)dest)[0] = (uint16)value;
                    break;
                case 3:
#if OGRE_ENDIAN == OGRE_ENDIAN_BIG      
                    ((uint8*)dest)[0] = (uint8)((value >> 16) & 0xFF);
                    ((uint8*)dest)[1] = (uint8)((value >> 8) & 0xFF);
                    ((uint8*)dest)[2] = (uint8)(value & 0xFF);
#else
                    ((uint8*)dest)[2] = (uint8)((value >> 16) & 0xFF);
                    ((uint8*)dest)[1] = (uint8)((value >> 8) & 0xFF);
                    ((uint8*)dest)[0] = (uint8)(value & 0xFF);
#endif
                    break;
                case 4:
                    ((uint32*)dest)[0] = (uint32)value;                
                    break;                
            }        */
        switch (n) {
            case 1:
                dest.put((byte) value);
                break;
            case 2:
                dest.putShort((short) value);
                break;
            case 3:
                if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
                    dest.put((byte) ((value >> 16) & 0xff));
                    dest.put((byte) ((value >> 8) & 0xff));
                    dest.put((byte) (value & 0xff));
                } else {
                    dest.put((byte) (value & 0xff));
                    dest.put((byte) ((value >> 8) & 0xff));
                    dest.put((byte) ((value >> 16) & 0xff));
                }
                break;
            case 4:
                dest.putInt(value);
                break;
            default:
                //Should never get here
                throw new IllegalArgumentException("n must be > 0 && <= 4");
        }
    }

    public static int intRead(ByteBuffer src, int n) {
		/*switch(n) {
                case 1:
                    return ((uint8*)src)[0];
                case 2:
                    return ((uint16*)src)[0];
                case 3:
#if OGRE_ENDIAN == OGRE_ENDIAN_BIG      
                    return ((uint32)((uint8*)src)[0]<<16)|
                            ((uint32)((uint8*)src)[1]<<8)|
                            ((uint32)((uint8*)src)[2]);
#else
                    return ((uint32)((uint8*)src)[0])|
                            ((uint32)((uint8*)src)[1]<<8)|
                            ((uint32)((uint8*)src)[2]<<16);
#endif
                case 4:
                    return ((uint32*)src)[0];
            } 
            return 0; // ?*/
        switch (n) {
            case 1:
                return (src.get());
            case 2:
                return (src.getShort());
            case 3:
                if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
                    return ((src.get() << 16) |
                            (src.get() << 8) |
                            (src.get()));
                } else {
                    return ((src.get()) |
                            (src.get() << 8) |
                            (src.get() << 16));
                }
            case 4:
                return (src.getInt());
            default:
                throw new IllegalArgumentException("n must be > 0 && <= 4");
        }
    }

    /**
     * Convert a float32 to a float16 (NV_half_float)
     * Courtesy of OpenEXR
     */
    public static short floatToHalf(float i) {
        return floatToHalfI(Float.floatToRawIntBits(i));
    }

    /**
     * Converts float in uint32 format to a a half in uint16 format
     */
    public static short floatToHalfI(int i) {
        int s = (i >> 16) & 0x00008000;
        int e = ((i >> 23) & 0x000000ff) - (127 - 15);
        int m = i & 0x007fffff;
        if (e <= 0) {
            if (e < -10) {
                return 0;
            }
            m = (m | 0x00800000) >> (1 - e);
            return (short) (s | (m >> 13));
        } else if (e == (0xff - (127 - 15))) {
            if (m == 0) {
                return (short) (s | 0x7c00);
            } else {
                m >>= 13;
                return (short) (s | 0x7c00 | m | ((m == 0) ? 1 : 0));
            }
        } else {
            if (e > 30) {
                return (short) (s | 0x7c00);
            }
            return (short) (s | (e << 10) | (m >> 13));
        }
	/*	register int s =  (i >> 16) & 0x00008000;
        register int e = ((i >> 23) & 0x000000ff) - (127 - 15);
        register int m =   i        & 0x007fffff;
    
        if (e <= 0)
        {
            if (e < -10)
            {
                return 0;
            }
            m = (m | 0x00800000) >> (1 - e);
    
            return static_cast<uint16>(s | (m >> 13));
        }
        else if (e == 0xff - (127 - 15))
        {
            if (m == 0) // Inf
            {
                return static_cast<uint16>(s | 0x7c00);
            } 
            else    // NAN
            {
                m >>= 13;
                return static_cast<uint16>(s | 0x7c00 | m | (m == 0));
            }
        }
        else
        {
            if (e > 30) // Overflow
            {
                return static_cast<uint16>(s | 0x7c00);
            }
    
            return static_cast<uint16>(s | (e << 10) | (m >> 13));
        }*/
    }

    /**
     * Convert a float16 (NV_half_float) to a float32
     * Courtesy of OpenEXR
     */
    public static float halfToFloat(short y) {
        return Float.intBitsToFloat(halfToFloatI(y));
    }

    /**
     * Converts a half in uint16 format to a float
     * in uint32 format
     */
    public static int halfToFloatI(short y) {
        int s = (y >> 15) & 0x00000001;
        int e = (y >> 10) & 0x0000001f;
        int m = y & 0x000003ff;
        if (e == 0) {
            if (m == 0) {
                return (s << 31);
            } else {
                while ((m & 0x00000400) == 0) {
                    m <<= 1;
                    e -= 1;
                }
                e += 1;
                m &= ~0x00000400;
            }
        } else if (e == 31) {
            if (m == 0) // Inf
            {
                return ((s << 31) | 0x7f800000);
            } else // NaN
            {
                return ((s << 31) | 0x7f800000 | (m << 13));
            }
        }
        e = e + (127 - 15);
        m = m << 13;

        return ((s << 31) | (e << 23) | m);
		/*register int s = (y >> 15) & 0x00000001;
            register int e = (y >> 10) & 0x0000001f;
            register int m =  y        & 0x000003ff;
        
            if (e == 0)
            {
                if (m == 0) // Plus or minus zero
                {
                    return s << 31;
                }
                else // Denormalized number -- renormalize it
                {
                    while (!(m & 0x00000400))
                    {
                        m <<= 1;
                        e -=  1;
                    }
        
                    e += 1;
                    m &= ~0x00000400;
                }
            }
            else if (e == 31)
            {
                if (m == 0) // Inf
                {
                    return (s << 31) | 0x7f800000;
                }
                else // NaN
                {
                    return (s << 31) | 0x7f800000 | (m << 13);
                }
            }
        
            e = e + (127 - 15);
            m = m << 13;
        
            return (s << 31) | (e << 23) | m;*/
    }
}
