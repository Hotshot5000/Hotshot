/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:46 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.material.ENG_Color;

public class ENG_ColorValue {

    public static final ENG_ColorValue ZERO =
            new ENG_ColorValue(0.0f, 0.0f, 0.0f, 0.0f);
    public static final ENG_ColorValue BLACK = new ENG_ColorValue(0.0f, 0.0f, 0.0f);
    public static final ENG_ColorValue WHITE = new ENG_ColorValue(1.0f, 1.0f, 1.0f);
    public static final ENG_ColorValue RED = new ENG_ColorValue(1.0f, 0.0f, 0.0f);
    public static final ENG_ColorValue GREEN = new ENG_ColorValue(0.0f, 1.0f, 0.0f);
    public static final ENG_ColorValue BLUE = new ENG_ColorValue(0.0f, 0.0f, 1.0f);
    private static final float INV = 1.0f / 255.0f;

    public float r, g, b, a;

    public ENG_ColorValue() {

    }

    public ENG_ColorValue(float red, float green, float blue, float alpha) {
        r = red;
        g = green;
        b = blue;
        a = alpha;
    }

    public ENG_ColorValue(float red, float green, float blue) {
        r = red;
        g = green;
        b = blue;
        a = 1.0f;
    }

    public ENG_ColorValue(byte red, byte green, byte blue, byte alpha) {
        set(red, green, blue, alpha);
    }

    public ENG_ColorValue(byte[] buf, int offset) {
        set(buf, offset);
    }

    public ENG_ColorValue(ENG_ColorValue c) {
        set(c);
    }

    public void set(ENG_ColorValue c) {
        this.r = c.r;
        this.g = c.g;
        this.b = c.b;
        this.a = c.a;
    }

    public void set(float red, float green, float blue) {
        r = red;
        g = green;
        b = blue;
    }

    public void set(float red, float green, float blue, float alpha) {
        r = red;
        g = green;
        b = blue;
        a = alpha;
    }

    public void setAsInt(int red, int green, int blue) {
        setAsInt(red, green, blue, 255);
    }

    public void setAsInt(int red, int green, int blue, int alpha) {
        r = (float) (red & 0xFF) / 255.0f;
        g = (float) (green & 0xFF) / 255.0f;
        b = (float) (blue & 0xFF) / 255.0f;
        a = (float) (alpha & 0xFF) / 255.0f;
    }

    public void setAsInt(int[] buf, int offset) {
        r = (float) (buf[offset] & 0xFF) / 255.0f;
        g = (float) (buf[offset + 1] & 0xFF) / 255.0f;
        b = (float) (buf[offset + 2] & 0xFF) / 255.0f;
        a = (float) (buf[offset + 3] & 0xFF) / 255.0f;
    }

    public void set(byte red, byte green, byte blue, byte alpha) {
//		throw new UnsupportedOperationException("We're in fucking java that doesn't " +
//				"understand unsigned!!!!!!!!");
        r = (float) (red & 0xFF) / 255.0f;
        g = (float) (green & 0xFF) / 255.0f;
        b = (float) (blue & 0xFF) / 255.0f;
        a = (float) (alpha & 0xFF) / 255.0f;
    }

    public void set(byte[] buf, int offset) {
//		throw new UnsupportedOperationException("We're in fucking java that doesn't " +
//				"understand unsigned!!!!!!!!");
        r = (float) (buf[offset] & 0xFF) / 255.0f;
        g = (float) (buf[offset + 1] & 0xFF) / 255.0f;
        b = (float) (buf[offset + 2] & 0xFF) / 255.0f;
        a = (float) (buf[offset + 3] & 0xFF) / 255.0f;
    }

    public void copy(ENG_ColorValue ret) {
        ret.r = r;
        ret.g = g;
        ret.b = b;
        ret.a = a;
    }

    public ENG_ColorValue copy() {
        ENG_ColorValue ret = new ENG_ColorValue();
        copy(ret);
        return ret;
    }

    public void saturate() {
        if (r < 0.0f) {
            r = 0.0f;
        } else if (r > 1.0f) {
            r = 1.0f;
        }

        if (g < 0.0f) {
            g = 0.0f;
        } else if (g > 1.0f) {
            g = 1.0f;
        }

        if (b < 0.0f) {
            b = 0.0f;
        } else if (b > 1.0f) {
            b = 1.0f;
        }

        if (a < 0.0f) {
            a = 0.0f;
        } else if (a > 1.0f) {
            a = 1.0f;
        }
    }

    public void saturate(ENG_ColorValue ret) {
        copy(ret);
        ret.saturate();
    }

    public ENG_ColorValue saturateRet() {
        ENG_ColorValue ret = copy();
        ret.saturate();
        return ret;
    }

    public void add(ENG_ColorValue v, ENG_ColorValue ret) {
        ret.r = r + v.r;
        ret.g = g + v.g;
        ret.b = b + v.b;
        ret.a = a + v.a;
    }

    public ENG_ColorValue add(ENG_ColorValue v) {
        ENG_ColorValue ret = new ENG_ColorValue();
        add(v, ret);
        return ret;
    }

    public void addInPlace(ENG_ColorValue v) {
        r += v.r;
        g += v.g;
        b += v.b;
        a += v.a;
    }

    public void add(float v, ENG_ColorValue ret) {
        ret.r = r + v;
        ret.g = g + v;
        ret.b = b + v;
        ret.a = a + v;
    }

    public ENG_ColorValue add(float v) {
        ENG_ColorValue ret = new ENG_ColorValue();
        add(v, ret);
        return ret;
    }

    public void addInPlace(float v) {
        r += v;
        g += v;
        b += v;
        a += v;
    }

    public void sub(ENG_ColorValue v, ENG_ColorValue ret) {
        ret.r = r - v.r;
        ret.g = g - v.g;
        ret.b = b - v.b;
        ret.a = a - v.a;
    }

    public ENG_ColorValue sub(ENG_ColorValue v) {
        ENG_ColorValue ret = new ENG_ColorValue();
        sub(v, ret);
        return ret;
    }

    public void subInPlace(ENG_ColorValue v) {
        r -= v.r;
        g -= v.g;
        b -= v.b;
        a -= v.a;
    }

    public void sub(float v, ENG_ColorValue ret) {
        ret.r = r - v;
        ret.g = g - v;
        ret.b = b - v;
        ret.a = a - v;
    }

    public ENG_ColorValue sub(float v) {
        ENG_ColorValue ret = new ENG_ColorValue();
        sub(v, ret);
        return ret;
    }

    public void subInPlace(float v) {
        r -= v;
        g -= v;
        b -= v;
        a -= v;
    }

    public void mul(ENG_ColorValue v, ENG_ColorValue ret) {
        ret.r = r * v.r;
        ret.g = g * v.g;
        ret.b = b * v.b;
        ret.a = a * v.a;
    }

    public static void mulScalar(float s, ENG_ColorValue v, ENG_ColorValue ret) {
        ret.r = s * v.r;
        ret.g = s * v.g;
        ret.b = s * v.b;
        ret.a = s * v.a;
    }

    public ENG_ColorValue mul(ENG_ColorValue v) {
        ENG_ColorValue ret = new ENG_ColorValue();
        mul(v, ret);
        return ret;
    }

    public void mulInPlace(ENG_ColorValue v) {
        r *= v.r;
        g *= v.g;
        b *= v.b;
        a *= v.a;
    }

    public void mul(float v, ENG_ColorValue ret) {
        ret.r = r * v;
        ret.g = g * v;
        ret.b = b * v;
        ret.a = a * v;
    }

    public ENG_ColorValue mul(float v) {
        ENG_ColorValue ret = new ENG_ColorValue();
        mul(v, ret);
        return ret;
    }

    public void mulInPlace(float v) {
        r *= v;
        g *= v;
        b *= v;
        a *= v;
    }

    public void div(ENG_ColorValue v, ENG_ColorValue ret) {
        ret.r = r / v.r;
        ret.g = g / v.g;
        ret.b = b / v.b;
        ret.a = a / v.a;
    }

    public ENG_ColorValue div(ENG_ColorValue v) {
        ENG_ColorValue ret = new ENG_ColorValue();
        div(v, ret);
        return ret;
    }

    public void divInPlace(ENG_ColorValue v) {
        r /= v.r;
        g /= v.g;
        b /= v.b;
        a /= v.a;
    }

    public void div(float v, ENG_ColorValue ret) {
        v = 1.0f / v;
        ret.r = r * v;
        ret.g = g * v;
        ret.b = b * v;
        ret.a = a * v;
    }

    public ENG_ColorValue div(float v) {
        ENG_ColorValue ret = new ENG_ColorValue();
        div(v, ret);
        return ret;
    }

    public void divInPlace(float v) {
        v = 1.0f / v;
        r *= v;
        g *= v;
        b *= v;
        a *= v;
    }

    public void invertWithAlpha() {
        r = 1.0f - r;
        g = 1.0f - g;
        b = 1.0f - b;
        a = 1.0f - a;
    }

    public void invert() {
        r = 1.0f - r;
        g = 1.0f - g;
        b = 1.0f - b;
    }

    public void invertWithAlpha(ENG_ColorValue ret) {
        ret.r = 1.0f - r;
        ret.g = 1.0f - g;
        ret.b = 1.0f - b;
        ret.a = 1.0f - a;
    }

    public ENG_ColorValue invertWithAlphaRet() {
        ENG_ColorValue ret = new ENG_ColorValue();
        invertWithAlpha(ret);
        return ret;
    }

    public void invert(ENG_ColorValue ret) {
        ret.r = 1.0f - r;
        ret.g = 1.0f - g;
        ret.b = 1.0f - b;
    }

    public ENG_ColorValue invertRet() {
        ENG_ColorValue ret = new ENG_ColorValue();
        invert(ret);
        return ret;
    }

    /** @noinspection deprecation*/
    public void toColor(ENG_Color c) {
        c.r = (byte) (r * 255.0f);
        c.g = (byte) (g * 255.0f);
        c.b = (byte) (b * 255.0f);
        c.a = (byte) (a * 255.0f);
    }

    /** @noinspection deprecation */
    public ENG_Color toColor() {
        ENG_Color c = new ENG_Color();
        toColor(c);
        return c;
    }

    public void toColor(byte[] buf, int offset) {
        buf[offset] = (byte) (r * 255.0f);
        buf[offset + 1] = (byte) (g * 255.0f);
        buf[offset + 2] = (byte) (b * 255.0f);
        buf[offset + 3] = (byte) (a * 255.0f);
    }

    public byte[] toColorByte() {
        return new byte[]{
                (byte) (r * 255.0f), (byte) (g * 255.0f),
                (byte) (b * 255.0f), (byte) (a * 255.0f)};
    }

    public void setHSB(float hue, float saturation, float brightness) {
        /*// wrap hue
		if (hue > 1.0f)
		{
			hue -= (int)hue;
		}
		else if (hue < 0.0f)
		{
			hue += (int)hue + 1;
		}
		// clamp saturation / brightness
		saturation = std::min(saturation, (Real)1.0);
		saturation = std::max(saturation, (Real)0.0);
		brightness = std::min(brightness, (Real)1.0);
		brightness = std::max(brightness, (Real)0.0);

		if (brightness == 0.0f)
		{   
			// early exit, this has to be black
			r = g = b = 0.0f;
			return;
		}*/
        if (hue > 1.0f) {
            hue -= (int) hue;
        } else if (hue < 0.0f) {
            hue += (int) hue + 1;
        }
        saturation = Math.min(saturation, 1.0f);
        saturation = Math.max(saturation, 0.0f);
        brightness = Math.min(brightness, 1.0f);
        brightness = Math.max(brightness, 0.0f);

        if (brightness == 0.0f) {
            r = 0.0f;
            g = 0.0f;
            b = 0.0f;
            return;
        }
		
		/*if (saturation == 0.0f)
		{   
			// early exit, this has to be grey

			r = g = b = brightness;
			return;
		}


		Real hueDomain  = hue * 6.0f;
		if (hueDomain >= 6.0f)
		{
			// wrap around, and allow mathematical errors
			hueDomain = 0.0f;
		}
		unsigned short domain = (unsigned short)hueDomain;
		Real f1 = brightness * (1 - saturation);
		Real f2 = brightness * (1 - saturation * (hueDomain - domain));
		Real f3 = brightness * (1 - saturation * (1 - (hueDomain - domain)));*/

        if (saturation == 0.0f) {
            r = brightness;
            g = brightness;
            b = brightness;
            return;
        }

        float hueDomain = hue * 6.0f;
        if (hueDomain >= 6.0f) {
            hueDomain = 0.0f;
        }

        short domain = (short) hueDomain;
        float f1 = brightness * (1.0f - saturation);
        float f2 = brightness * (1.0f - saturation * (hueDomain - domain));
        float f3 = brightness * (1.0f - saturation * (1.0f - (hueDomain - domain)));
		
		/*switch (domain)
		{
		case 0:
			// red domain; green ascends
			r = brightness;
			g = f3;
			b = f1;
			break;
		case 1:
			// yellow domain; red descends
			r = f2;
			g = brightness;
			b = f1;
			break;
		case 2:
			// green domain; blue ascends
			r = f1;
			g = brightness;
			b = f3;
			break;
		case 3:
			// cyan domain; green descends
			r = f1;
			g = f2;
			b = brightness;
			break;
		case 4:
			// blue domain; red ascends
			r = f3;
			g = f1;
			b = brightness;
			break;
		case 5:
			// magenta domain; blue descends
			r = brightness;
			g = f1;
			b = f2;
			break;
		}*/

        switch (domain) {
            case 0:
                r = brightness;
                g = f3;
                b = f1;
                break;
            case 1:
                r = f2;
                g = brightness;
                b = f1;
                break;
            case 2:
                r = f1;
                g = brightness;
                b = f3;
                break;
            case 3:
                r = f1;
                g = f2;
                b = brightness;
                break;
            case 4:
                r = f3;
                g = f1;
                b = brightness;
                break;
            case 5:
                r = brightness;
                g = f1;
                b = f2;
                break;
            default:
                //Should never get here
                throw new IllegalArgumentException("domain out of range: " + domain);
        }
    }

    public void getHSB(float[] hue, float[] saturation, float[] brightness) {
		/*Real vMin = std::min(r, std::min(g, b));
		Real vMax = std::max(r, std::max(g, b));
		Real delta = vMax - vMin;

		*brightness = vMax;*/

        float vMin = Math.min(r, Math.min(g, b));
        float vMax = Math.max(r, Math.max(g, b));
        float delta = vMax - vMin;
		
		/*if (Math::RealEqual(delta, 0.0f, 1e-6))
		{
			// grey
			*hue = 0;
			*saturation = 0;
		}*/

        if (ENG_Float.compareTo(delta, 0.0f, (float) 1e-6) ==
                ENG_Utility.COMPARE_EQUAL_TO) {
            hue[0] = 0.0f;
            saturation[0] = 0.0f;
        } else {
			/*else                                    
		{
			// a colour
			*saturation = delta / vMax;

			Real deltaR = (((vMax - r) / 6.0f) + (delta / 2.0f)) / delta;
			Real deltaG = (((vMax - g) / 6.0f) + (delta / 2.0f)) / delta;
			Real deltaB = (((vMax - b) / 6.0f) + (delta / 2.0f)) / delta;

			if (Math::RealEqual(r, vMax))
				*hue = deltaB - deltaG;
			else if (Math::RealEqual(g, vMax))
				*hue = 0.3333333f + deltaR - deltaB;
			else if (Math::RealEqual(b, vMax)) 
				*hue = 0.6666667f + deltaG - deltaR;

			if (*hue < 0.0f) 
				*hue += 1.0f;
			if (*hue > 1.0f)
				*hue -= 1.0f;
		}

*/
            saturation[0] = delta / vMax;

            float deltaR = (((vMax - r) / 6.0f) + (delta / 2.0f)) / delta;
            float deltaG = (((vMax - g) / 6.0f) + (delta / 2.0f)) / delta;
            float deltaB = (((vMax - b) / 6.0f) + (delta / 2.0f)) / delta;

            if (Float.compare(r, vMax) == ENG_Utility.COMPARE_EQUAL_TO) {
                hue[0] = deltaB - deltaG;
            } else if (Float.compare(g, vMax) == ENG_Utility.COMPARE_EQUAL_TO) {
                hue[0] = 0.3333333f + deltaR - deltaB;
            } else if (Float.compare(b, vMax) == ENG_Utility.COMPARE_EQUAL_TO) {
                hue[0] = 0.6666667f + deltaG - deltaR;
            }

            if (hue[0] < 0.0f) {
                hue[0] += 1.0f;
            }
            if (hue[0] > 1.0f) {
                hue[0] -= 1.0f;
            }
        }
    }

    public int getAsRGBA() {
        byte u8;
        int u32 = 0;

        u8 = (byte) (r * 255.0f);
        u32 += (u8 & 0xff) << 24;

        u8 = (byte) (g * 255.0f);
        u32 += (u8 & 0xff) << 16;

        u8 = (byte) (b * 255.0f);
        u32 += (u8 & 0xff) << 8;

        u8 = (byte) (a * 255.0f);
        u32 += (u8 & 0xff);

        return u32;
    }

    public int getAsARGB() {
        byte u8;
        int u32 = 0;

        u8 = (byte) (a * 255.0f);
        u32 += (u8 & 0xff) << 24;

        u8 = (byte) (r * 255.0f);
        u32 += (u8 & 0xff) << 16;

        u8 = (byte) (g * 255.0f);
        u32 += (u8 & 0xff) << 8;

        u8 = (byte) (b * 255.0f);
        u32 += (u8 & 0xff);

        return u32;
    }

    public int getAsBGRA() {
        byte u8;
        int u32 = 0;

        u8 = (byte) (b * 255.0f);
        u32 += (u8 & 0xff) << 24;

        u8 = (byte) (g * 255.0f);
        u32 += (u8 & 0xff) << 16;

        u8 = (byte) (r * 255.0f);
        u32 += (u8 & 0xff) << 8;

        u8 = (byte) (a * 255.0f);
        u32 += (u8 & 0xff);

        return u32;
    }

    public int getAsABGR() {
        byte u8;
        int u32 = 0;

        u8 = (byte) (a * 255.0f);
        u32 += (u8 & 0xff) << 24;

        u8 = (byte) (b * 255.0f);
        u32 += (u8 & 0xff) << 16;

        u8 = (byte) (g * 255.0f);
        u32 += (u8 & 0xff) << 8;

        u8 = (byte) (r * 255.0f);
        u32 += (u8 & 0xff);

        return u32;
    }

    public static ENG_ColorValue createAsRGBA(int val) {
        ENG_ColorValue col = new ENG_ColorValue();
        col.setAsRGBA(val);
        return col;
    }

    public static ENG_ColorValue createAsARGB(int val) {
        ENG_ColorValue col = new ENG_ColorValue();
        col.setAsARGB(val);
        return col;
    }

    public static ENG_ColorValue createAsBGRA(int val) {
        ENG_ColorValue col = new ENG_ColorValue();
        col.setAsBGRA(val);
        return col;
    }

    public static ENG_ColorValue createAsABGR(int val) {
        ENG_ColorValue col = new ENG_ColorValue();
        col.setAsABGR(val);
        return col;
    }

    public void setAsRGB(int val, float alpha) {
        r = ((val >> 16) & 0xff) * INV;
        g = ((val >> 8) & 0xff) * INV;
        b = (val & 0xff) * INV;
        a = alpha;
    }

    public void setAsBGR(int val, float alpha) {
        b = ((val >> 16) & 0xff) * INV;
        g = ((val >> 8) & 0xff) * INV;
        r = (val & 0xff) * INV;
        a = alpha;
    }

    public void setAsRGBA(int val) {
        r = ((val >> 24) & 0xff) * INV;
        g = ((val >> 16) & 0xff) * INV;
        b = ((val >> 8) & 0xff) * INV;
        a = (val & 0xff) * INV;
    }

    public void setAsARGB(int val) {
        a = ((val >> 24) & 0xff) * INV;
        r = ((val >> 16) & 0xff) * INV;
        g = ((val >> 8) & 0xff) * INV;
        b = (val & 0xff) * INV;
    }

    public void setAsBGRA(int val) {
        b = ((val >> 24) & 0xff) * INV;
        g = ((val >> 16) & 0xff) * INV;
        r = ((val >> 8) & 0xff) * INV;
        a = (val & 0xff) * INV;
    }

    public void setAsABGR(int val) {
        a = ((val >> 24) & 0xff) * INV;
        b = ((val >> 16) & 0xff) * INV;
        g = ((val >> 8) & 0xff) * INV;
        r = (val & 0xff) * INV;
    }

    public void setHexColor(String hexColor, float alpha) {
        r = Integer.valueOf(hexColor.substring(1, 3), 16) * INV;
        g = Integer.valueOf(hexColor.substring(3, 5), 16) * INV;
        b = Integer.valueOf(hexColor.substring(5, 7), 16) * INV;
        a = alpha;
    }

    public String getHexColor() {
        int asRGBA = getAsRGBA();
        int i = (asRGBA >> 24) & 0xff;
        int i1 = (asRGBA >> 16) & 0xff;
        int i2 = (asRGBA >> 8) & 0xff;
        return String.format("#%02x%02x%02x", (asRGBA >> 24) & 0xff, (asRGBA >> 16) & 0xff, (asRGBA >> 8) & 0xff);
    }

    public static ENG_ColorValue createFromHex(String hex, float alpha) {
        ENG_ColorValue colorValue = new ENG_ColorValue();
        colorValue.setHexColor(hex, alpha);
        return colorValue;
    }

    public boolean equals(ENG_ColorValue c) {
        return ((r == c.r) && (g == c.g) && (b == c.b) && (a == c.a));
    }

    public boolean notEquals(ENG_ColorValue c) {
        return ((r != c.r) || (g != c.g) || (b != c.b) || (a != c.a));
    }

    public boolean equalsWithoutAlpha(ENG_ColorValue c) {
        return ((r == c.r) && (g == c.g) && (b == c.b));
    }

    public boolean notEqualsWithoutAlpha(ENG_ColorValue c) {
        return ((r != c.r) || (g != c.g) || (b != c.b));
    }

    public String toString() {
        return (/*"ColorValue: " +*/ r + " " + g + " " + b + " " + a);
    }
}
