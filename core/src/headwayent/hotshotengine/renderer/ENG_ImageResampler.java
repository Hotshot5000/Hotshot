/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:46 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.basictypes.ENG_Float;

import java.nio.ByteBuffer;

public class ENG_ImageResampler {

	/*ColourValue x1y1z1, x2y1z1, x1y2z1, x2y2z1;
					ColourValue x1y1z2, x2y1z2, x1y2z2, x2y2z2;*/

    private static final ENG_ColorValue x1y1z1 = new ENG_ColorValue();
    private static final ENG_ColorValue x2y1z1 = new ENG_ColorValue();
    private static final ENG_ColorValue x1y2z1 = new ENG_ColorValue();
    private static final ENG_ColorValue x2y2z1 = new ENG_ColorValue();
    private static final ENG_ColorValue x1y1z2 = new ENG_ColorValue();
    private static final ENG_ColorValue x2y1z2 = new ENG_ColorValue();
    private static final ENG_ColorValue x1y2z2 = new ENG_ColorValue();
    private static final ENG_ColorValue x2y2z2 = new ENG_ColorValue();
    private static final ENG_ColorValue accum1 = new ENG_ColorValue();
    private static final float[] accum = {0.0f, 0.0f, 0.0f, 0.0f};

    public static void scaleNearestResampler(ENG_PixelBox src, ENG_PixelBox dst,
                                             int elemsize) {
		/*// srcdata stays at beginning, pdst is a moving pointer
		uchar* srcdata = (uchar*)src.data;
		uchar* pdst = (uchar*)dst.data;

		// sx_48,sy_48,sz_48 represent current position in source
		// using 16/48-bit fixed precision, incremented by steps
		uint64 stepx = ((uint64)src.getWidth() << 48) / dst.getWidth();
		uint64 stepy = ((uint64)src.getHeight() << 48) / dst.getHeight();
		uint64 stepz = ((uint64)src.getDepth() << 48) / dst.getDepth();*/
        ByteBuffer srcdata = src.data;
        ByteBuffer pdst = dst.data;

        long stepx = ((long) src.getWidth() << 48) / dst.getWidth();
        long stepy = ((long) src.getHeight() << 48) / dst.getHeight();
        long stepz = ((long) src.getDepth() << 48) / dst.getDepth();
		
		/*// note: ((stepz>>1) - 1) is an extra half-step increment to adjust
		// for the center of the destination pixel, not the top-left corner
		uint64 sz_48 = (stepz >> 1) - 1;
		for (size_t z = dst.front; z < dst.back; z++, sz_48 += stepz) {
			size_t srczoff = (size_t)(sz_48 >> 48) * src.slicePitch;
			
			uint64 sy_48 = (stepy >> 1) - 1;
			for (size_t y = dst.top; y < dst.bottom; y++, sy_48 += stepy) {
				size_t srcyoff = (size_t)(sy_48 >> 48) * src.rowPitch;
			
				uint64 sx_48 = (stepx >> 1) - 1;
				for (size_t x = dst.left; x < dst.right; x++, sx_48 += stepx) {
					uchar* psrc = srcdata +
						elemsize*((size_t)(sx_48 >> 48) + srcyoff + srczoff);
                    memcpy(pdst, psrc, elemsize);
					pdst += elemsize;
				}
				pdst += elemsize*dst.getRowSkip();
			}
			pdst += elemsize*dst.getSliceSkip();
		}*/
        long sz_48 = (stepz >> 1) - 1;
        for (int z = dst.front; z < dst.back; ++z, sz_48 += stepz) {
            int srczoff = (int) (sz_48 >> 48) * src.slicePitch;

            long sy_48 = (stepy >> 1) - 1;
            for (int y = dst.top; y < dst.bottom; ++y, sy_48 += stepy) {
                int srcyoff = (int) (sy_48 >> 48) * src.rowPitch;

                long sx_48 = (stepx >> 1) - 1;
                for (int x = dst.left; x < dst.right; ++x, sx_48 += stepx) {
                    int psrc = srcdata.position() +
                            elemsize * ((int) (sx_48 >> 48) + srcyoff + srczoff);
                    srcdata.limit(psrc + elemsize);
                    srcdata.position(psrc);
                    pdst.put(srcdata);
                }
                pdst.position(pdst.position() + elemsize * dst.getRowSkip());
            }
            pdst.position(pdst.position() + elemsize * dst.getSliceSkip());
        }
    }

    private static void UNPACK(ENG_ColorValue dst, ENG_PixelBox src, int srcelemsize,
                               int x, int y, int z) {
		/*#define UNPACK(dst,x,y,z) PixelUtil::unpackColour(&dst, src.format, \
	srcdata + srcelemsize*((x)+(y)*src.rowPitch+(z)*src.slicePitch))*/
        src.data.position(srcelemsize * (x + y * src.rowPitch + z * src.slicePitch));
        ENG_PixelUtil.unpackColour(dst, src.pixelFormat, src.data);
    }

    public static void scaleLinearResampler(ENG_PixelBox src, ENG_PixelBox dst) {
		/*size_t srcelemsize = PixelUtil::getNumElemBytes(src.format);
		size_t dstelemsize = PixelUtil::getNumElemBytes(dst.format);

		// srcdata stays at beginning, pdst is a moving pointer
		uchar* srcdata = (uchar*)src.data;
		uchar* pdst = (uchar*)dst.data;
		
		// sx_48,sy_48,sz_48 represent current position in source
		// using 16/48-bit fixed precision, incremented by steps
		uint64 stepx = ((uint64)src.getWidth() << 48) / dst.getWidth();
		uint64 stepy = ((uint64)src.getHeight() << 48) / dst.getHeight();
		uint64 stepz = ((uint64)src.getDepth() << 48) / dst.getDepth();*/
        int srcelemsize = ENG_PixelUtil.getNumElemBytes(src.pixelFormat);
        int dstelemsize = ENG_PixelUtil.getNumElemBytes(dst.pixelFormat);

        ByteBuffer srcdata = src.data;
        ByteBuffer pdst = dst.data;

        long stepx = ((long) src.getWidth() << 48) / dst.getWidth();
        long stepy = ((long) src.getHeight() << 48) / dst.getHeight();
        long stepz = ((long) src.getDepth() << 48) / dst.getDepth();

        int temp;
		
		/*uint64 sz_48 = (stepz >> 1) - 1;
		for (size_t z = dst.front; z < dst.back; z++, sz_48+=stepz) {
			temp = static_cast<unsigned int>(sz_48 >> 32);
			temp = (temp > 0x8000)? temp - 0x8000 : 0;
			size_t sz1 = temp >> 16;				 // src z, sample #1
			size_t sz2 = std::min(sz1+1,src.getDepth()-1);// src z, sample #2
			float szf = (temp & 0xFFFF) / 65536.f; // weight of sample #2

			uint64 sy_48 = (stepy >> 1) - 1;
			for (size_t y = dst.top; y < dst.bottom; y++, sy_48+=stepy) {
				temp = static_cast<unsigned int>(sy_48 >> 32);
				temp = (temp > 0x8000)? temp - 0x8000 : 0;
				size_t sy1 = temp >> 16;					// src y #1
				size_t sy2 = std::min(sy1+1,src.getHeight()-1);// src y #2
				float syf = (temp & 0xFFFF) / 65536.f; // weight of #2*/
        long sz_48 = (stepz >> 1) - 1;
        for (int z = dst.front; z < dst.back; ++z, sz_48 += stepz) {
            temp = (int) (sz_48 >> 32);
            temp = (temp > 0x8000) ? (temp - 0x8000) : 0;
            int sz1 = temp >> 16;
            int sz2 = Math.min(sz1 + 1, src.getDepth() - 1);
            float szf = (temp & 0xffff) / 65536.0f;

            long sy_48 = (stepy >> 1) - 1;
            for (int y = dst.top; y < dst.bottom; ++y, sy_48 += stepy) {
                temp = (int) (sy_48 >> 32);
                temp = (temp > 0x8000) ? (temp - 0x8000) : 0;
                int sy1 = temp >> 16;
                int sy2 = Math.min(sy1 + 1, src.getHeight() - 1);
                float syf = (temp & 0xffff) / 65536.0f;
				/*uint64 sx_48 = (stepx >> 1) - 1;
				for (size_t x = dst.left; x < dst.right; x++, sx_48+=stepx) {
					temp = static_cast<unsigned int>(sx_48 >> 32);
					temp = (temp > 0x8000)? temp - 0x8000 : 0;
					size_t sx1 = temp >> 16;					// src x #1
					size_t sx2 = std::min(sx1+1,src.getWidth()-1);// src x #2
					float sxf = (temp & 0xFFFF) / 65536.f; // weight of #2
				
					ColourValue x1y1z1, x2y1z1, x1y2z1, x2y2z1;
					ColourValue x1y1z2, x2y1z2, x1y2z2, x2y2z2;*/
                long sx_48 = (stepx >> 1) - 1;
                for (int x = dst.left; x < dst.right; ++x, sx_48 += stepx) {
                    temp = (int) (sx_48 >> 32);
                    temp = (temp > 0x8000) ? (temp - 0x8000) : 0;
                    int sx1 = temp >> 16;
                    int sx2 = Math.min(sx1 + 1, src.getWidth() - 1);
                    float sxf = (temp & 0xffff) / 65536.0f;

                    UNPACK(x1y1z1, src, srcelemsize, sx1, sy1, sz1);
                    UNPACK(x2y1z1, src, srcelemsize, sx2, sy1, sz1);
                    UNPACK(x1y2z1, src, srcelemsize, sx1, sy2, sz1);
                    UNPACK(x2y2z1, src, srcelemsize, sx2, sy2, sz1);
                    UNPACK(x1y1z2, src, srcelemsize, sx1, sy1, sz2);
                    UNPACK(x2y1z2, src, srcelemsize, sx2, sy1, sz2);
                    UNPACK(x1y2z2, src, srcelemsize, sx1, sy2, sz2);
                    UNPACK(x2y2z2, src, srcelemsize, sx2, sy2, sz2);
					
					/*ColourValue accum =
						x1y1z1 * ((1.0f - sxf)*(1.0f - syf)*(1.0f - szf)) +
						x2y1z1 * (        sxf *(1.0f - syf)*(1.0f - szf)) +
						x1y2z1 * ((1.0f - sxf)*        syf *(1.0f - szf)) +
						x2y2z1 * (        sxf *        syf *(1.0f - szf)) +
						x1y1z2 * ((1.0f - sxf)*(1.0f - syf)*        szf ) +
						x2y1z2 * (        sxf *(1.0f - syf)*        szf ) +
						x1y2z2 * ((1.0f - sxf)*        syf *        szf ) +
						x2y2z2 * (        sxf *        syf *        szf );*/

                    x1y1z1.mulInPlace((1.0f - sxf) * (1.0f - syf) * (1.0f - szf));
                    x2y1z1.mulInPlace(sxf * (1.0f - syf) * (1.0f - szf));
                    x1y2z1.mulInPlace((1.0f - sxf) * syf * (1.0f - szf));
                    x2y2z1.mulInPlace(sxf * syf * (1.0f - szf));
                    x1y1z2.mulInPlace((1.0f - sxf) * (1.0f - syf) * szf);
                    x2y1z2.mulInPlace(sxf * (1.0f - syf) * szf);
                    x1y2z2.mulInPlace((1.0f - sxf) * syf * szf);
                    x2y2z2.mulInPlace(sxf * syf * szf);
                    accum1.set(x1y1z1);
                    accum1.addInPlace(x2y1z1);
                    accum1.addInPlace(x1y2z1);
                    accum1.addInPlace(x2y2z1);
                    accum1.addInPlace(x1y1z2);
                    accum1.addInPlace(x2y1z2);
                    accum1.addInPlace(x1y2z2);
                    accum1.addInPlace(x2y2z2);
					
					/*PixelUtil::packColour(accum, dst.format, pdst);

					pdst += dstelemsize;
				}
				pdst += dstelemsize*dst.getRowSkip();
			}
			pdst += dstelemsize*dst.getSliceSkip();*/
                    ENG_PixelUtil.packColour(accum1, dst.pixelFormat, pdst);
                    //Already incremented
                }
                pdst.position(pdst.position() + dstelemsize * dst.getRowSkip());
            }
            pdst.position(pdst.position() + dstelemsize * dst.getSliceSkip());
        }
    }

    private static void ACCUM3(int x, int y, int z, float factor,
                               ENG_PixelBox src, int srcchannels, float[] accum) {
		/*{ float f = factor; \
	size_t off = (x+y*src.rowPitch+z*src.slicePitch)*srcchannels; \
    accum[0]+=srcdata[off+0]*f; accum[1]+=srcdata[off+1]*f; \
	accum[2]+=srcdata[off+2]*f; }*/
        int off = (x + y * src.rowPitch + z * src.slicePitch) * srcchannels;
        src.data.position(off);
        accum[0] += src.data.getFloat() * factor;
        accum[1] += src.data.getFloat() * factor;
        accum[2] += src.data.getFloat() * factor;
    }

    private static void ACCUM4(int x, int y, int z, float factor,
                               ENG_PixelBox src, int srcchannels, float[] accum) {
		/*{ float f = factor; \
	size_t off = (x+y*src.rowPitch+z*src.slicePitch)*srcchannels; \
    accum[0]+=srcdata[off+0]*f; accum[1]+=srcdata[off+1]*f; \
	accum[2]+=srcdata[off+2]*f; }*/
        int off = (x + y * src.rowPitch + z * src.slicePitch) * srcchannels;
        src.data.position(off);
        accum[0] += src.data.getFloat() * factor;
        accum[1] += src.data.getFloat() * factor;
        accum[2] += src.data.getFloat() * factor;
        accum[3] += src.data.getFloat() * factor;
    }

    public static void scaleLinearResampler_Float32(ENG_PixelBox src, ENG_PixelBox dst) {
	
		/*size_t srcelemsize = PixelUtil::getNumElemBytes(src.format);
		size_t dstelemsize = PixelUtil::getNumElemBytes(dst.format);

		// srcdata stays at beginning, pdst is a moving pointer
		uchar* srcdata = (uchar*)src.data;
		uchar* pdst = (uchar*)dst.data;
		
		// sx_48,sy_48,sz_48 represent current position in source
		// using 16/48-bit fixed precision, incremented by steps
		uint64 stepx = ((uint64)src.getWidth() << 48) / dst.getWidth();
		uint64 stepy = ((uint64)src.getHeight() << 48) / dst.getHeight();
		uint64 stepz = ((uint64)src.getDepth() << 48) / dst.getDepth();*/
        int srcchannels = ENG_PixelUtil.getNumElemBytes(src.pixelFormat) / ENG_Float.SIZE_IN_BYTES;
        int dstchannels = ENG_PixelUtil.getNumElemBytes(dst.pixelFormat) / ENG_Float.SIZE_IN_BYTES;

        ByteBuffer srcdata = src.data;
        ByteBuffer pdst = dst.data;

        long stepx = ((long) src.getWidth() << 48) / dst.getWidth();
        long stepy = ((long) src.getHeight() << 48) / dst.getHeight();
        long stepz = ((long) src.getDepth() << 48) / dst.getDepth();

        int temp;
		
		/*uint64 sz_48 = (stepz >> 1) - 1;
		for (size_t z = dst.front; z < dst.back; z++, sz_48+=stepz) {
			temp = static_cast<unsigned int>(sz_48 >> 32);
			temp = (temp > 0x8000)? temp - 0x8000 : 0;
			size_t sz1 = temp >> 16;				 // src z, sample #1
			size_t sz2 = std::min(sz1+1,src.getDepth()-1);// src z, sample #2
			float szf = (temp & 0xFFFF) / 65536.f; // weight of sample #2

			uint64 sy_48 = (stepy >> 1) - 1;
			for (size_t y = dst.top; y < dst.bottom; y++, sy_48+=stepy) {
				temp = static_cast<unsigned int>(sy_48 >> 32);
				temp = (temp > 0x8000)? temp - 0x8000 : 0;
				size_t sy1 = temp >> 16;					// src y #1
				size_t sy2 = std::min(sy1+1,src.getHeight()-1);// src y #2
				float syf = (temp & 0xFFFF) / 65536.f; // weight of #2*/
        long sz_48 = (stepz >> 1) - 1;
        for (int z = dst.front; z < dst.back; ++z, sz_48 += stepz) {
            temp = (int) (sz_48 >> 32);
            temp = (temp > 0x8000) ? (temp - 0x8000) : 0;
            int sz1 = temp >> 16;
            int sz2 = Math.min(sz1 + 1, src.getDepth() - 1);
            float szf = (temp & 0xffff) / 65536.0f;

            long sy_48 = (stepy >> 1) - 1;
            for (int y = dst.top; y < dst.bottom; ++y, sy_48 += stepy) {
                temp = (int) (sy_48 >> 32);
                temp = (temp > 0x8000) ? (temp - 0x8000) : 0;
                int sy1 = temp >> 16;
                int sy2 = Math.min(sy1 + 1, src.getHeight() - 1);
                float syf = (temp & 0xffff) / 65536.0f;
				/*uint64 sx_48 = (stepx >> 1) - 1;
				for (size_t x = dst.left; x < dst.right; x++, sx_48+=stepx) {
					temp = static_cast<unsigned int>(sx_48 >> 32);
					temp = (temp > 0x8000)? temp - 0x8000 : 0;
					size_t sx1 = temp >> 16;					// src x #1
					size_t sx2 = std::min(sx1+1,src.getWidth()-1);// src x #2
					float sxf = (temp & 0xFFFF) / 65536.f; // weight of #2
				
					ColourValue x1y1z1, x2y1z1, x1y2z1, x2y2z1;
					ColourValue x1y1z2, x2y1z2, x1y2z2, x2y2z2;*/
                long sx_48 = (stepx >> 1) - 1;
                for (int x = dst.left; x < dst.right; ++x, sx_48 += stepx) {
                    temp = (int) (sx_48 >> 32);
                    temp = (temp > 0x8000) ? (temp - 0x8000) : 0;
                    int sx1 = temp >> 16;
                    int sx2 = Math.min(sx1 + 1, src.getWidth() - 1);
                    float sxf = (temp & 0xffff) / 65536.0f;

                    accum[0] = 0.0f;
                    accum[1] = 0.0f;
                    accum[2] = 0.0f;
                    accum[3] = 0.0f;
					
					/*if (srcchannels == 3 || dstchannels == 3) {
						// RGB, no alpha
						ACCUM3(sx1,sy1,sz1,(1.0f-sxf)*(1.0f-syf)*(1.0f-szf));
						ACCUM3(sx2,sy1,sz1,      sxf *(1.0f-syf)*(1.0f-szf));
						ACCUM3(sx1,sy2,sz1,(1.0f-sxf)*      syf *(1.0f-szf));
						ACCUM3(sx2,sy2,sz1,      sxf *      syf *(1.0f-szf));
						ACCUM3(sx1,sy1,sz2,(1.0f-sxf)*(1.0f-syf)*      szf );
						ACCUM3(sx2,sy1,sz2,      sxf *(1.0f-syf)*      szf );
						ACCUM3(sx1,sy2,sz2,(1.0f-sxf)*      syf *      szf );
						ACCUM3(sx2,sy2,sz2,      sxf *      syf *      szf );
						accum[3] = 1.0f;
					}*/
					/* else {
						// RGBA
						ACCUM4(sx1,sy1,sz1,(1.0f-sxf)*(1.0f-syf)*(1.0f-szf));
						ACCUM4(sx2,sy1,sz1,      sxf *(1.0f-syf)*(1.0f-szf));
						ACCUM4(sx1,sy2,sz1,(1.0f-sxf)*      syf *(1.0f-szf));
						ACCUM4(sx2,sy2,sz1,      sxf *      syf *(1.0f-szf));
						ACCUM4(sx1,sy1,sz2,(1.0f-sxf)*(1.0f-syf)*      szf );
						ACCUM4(sx2,sy1,sz2,      sxf *(1.0f-syf)*      szf );
						ACCUM4(sx1,sy2,sz2,(1.0f-sxf)*      syf *      szf );
						ACCUM4(sx2,sy2,sz2,      sxf *      syf *      szf );
					}*/

                    if ((srcchannels == 3) || (dstchannels == 3)) {
                        ACCUM3(sx1, sy1, sz1, (1.0f - sxf) * (1.0f - syf) * (1.0f - szf), src, srcchannels, accum);
                        ACCUM3(sx2, sy1, sz1, sxf * (1.0f - syf) * (1.0f - szf), src, srcchannels, accum);
                        ACCUM3(sx1, sy2, sz1, (1.0f - sxf) * syf * (1.0f - szf), src, srcchannels, accum);
                        ACCUM3(sx2, sy2, sz1, sxf * syf * (1.0f - szf), src, srcchannels, accum);
                        ACCUM3(sx1, sy1, sz2, (1.0f - sxf) * (1.0f - syf) * szf, src, srcchannels, accum);
                        ACCUM3(sx2, sy1, sz2, sxf * (1.0f - syf) * szf, src, srcchannels, accum);
                        ACCUM3(sx1, sy2, sz2, (1.0f - sxf) * syf * szf, src, srcchannels, accum);
                        ACCUM3(sx2, sy2, sz2, sxf * syf * szf, src, srcchannels, accum);
                        accum[3] = 1.0f;
                    } else {
                        ACCUM4(sx1, sy1, sz1, (1.0f - sxf) * (1.0f - syf) * (1.0f - szf), src, srcchannels, accum);
                        ACCUM4(sx2, sy1, sz1, sxf * (1.0f - syf) * (1.0f - szf), src, srcchannels, accum);
                        ACCUM4(sx1, sy2, sz1, (1.0f - sxf) * syf * (1.0f - szf), src, srcchannels, accum);
                        ACCUM4(sx2, sy2, sz1, sxf * syf * (1.0f - szf), src, srcchannels, accum);
                        ACCUM4(sx1, sy1, sz2, (1.0f - sxf) * (1.0f - syf) * szf, src, srcchannels, accum);
                        ACCUM4(sx2, sy1, sz2, sxf * (1.0f - syf) * szf, src, srcchannels, accum);
                        ACCUM4(sx1, sy2, sz2, (1.0f - sxf) * syf * szf, src, srcchannels, accum);
                        ACCUM4(sx2, sy2, sz2, sxf * syf * szf, src, srcchannels, accum);
                    }
					
					/*memcpy(pdst, accum, sizeof(float)*dstchannels);*/
                    for (int i = 0; i < dstchannels; ++i) {
                        pdst.putFloat(accum[i]);
                    }
				/*	if (dstchannels == 3) {
						for (int i = 0; i < 3; ++i) {
							pdst.putFloat(accum[i]);
						}
					} else {
						for (int i = 0; i < 4; ++i) {
							pdst.putFloat(accum[i]);
						}
					}*/
                    //Already incremented
                }
                pdst.position(pdst.position() + dstchannels * dst.getRowSkip());
            }
            pdst.position(pdst.position() + dstchannels * dst.getSliceSkip());
        }
    }

    public static void scaleLinearResampler_Byte(ENG_PixelBox src, ENG_PixelBox dst,
                                                 int channels) {
		/*if (src.getDepth() > 1 || dst.getDepth() > 1) {
			LinearResampler::scale(src, dst);
			return;
		}

		// srcdata stays at beginning of slice, pdst is a moving pointer
		uchar* srcdata = (uchar*)src.data;
		uchar* pdst = (uchar*)dst.data;

		// sx_48,sy_48 represent current position in source
		// using 16/48-bit fixed precision, incremented by steps
		uint64 stepx = ((uint64)src.getWidth() << 48) / dst.getWidth();
		uint64 stepy = ((uint64)src.getHeight() << 48) / dst.getHeight();*/

        if ((src.getDepth() > 1) || (dst.getDepth() > 1)) {
            scaleLinearResampler(src, dst);
            return;
        }

        ByteBuffer srcdata = src.data;
        ByteBuffer pdst = dst.data;

        long stepx = ((long) src.getWidth() << 48) / dst.getWidth();
        long stepy = ((long) src.getHeight() << 48) / dst.getHeight();

        int temp;
		
		/*uint64 sy_48 = (stepy >> 1) - 1;
		for (size_t y = dst.top; y < dst.bottom; y++, sy_48+=stepy) {
			temp = static_cast<unsigned int>(sy_48 >> 36);
			temp = (temp > 0x800)? temp - 0x800: 0;
			unsigned int syf = temp & 0xFFF;
			size_t sy1 = temp >> 12;
			size_t sy2 = std::min(sy1+1, src.bottom-src.top-1);
			size_t syoff1 = sy1 * src.rowPitch;
			size_t syoff2 = sy2 * src.rowPitch;

			uint64 sx_48 = (stepx >> 1) - 1;
			for (size_t x = dst.left; x < dst.right; x++, sx_48+=stepx) {
				temp = static_cast<unsigned int>(sx_48 >> 36);
				temp = (temp > 0x800)? temp - 0x800 : 0;
				unsigned int sxf = temp & 0xFFF;
				size_t sx1 = temp >> 12;
				size_t sx2 = std::min(sx1+1, src.right-src.left-1);*/

        long sy_48 = (stepy >> 1) - 1;
        for (int y = dst.top; y < dst.bottom; ++y, sy_48 += stepy) {
            temp = (int) (sy_48 >> 36);
            temp = (temp > 0x800) ? (temp - 0x800) : 0;
            int syf = temp & 0xfff;
            int sy1 = temp >> 12;
            int sy2 = Math.min(sy1 + 1, src.bottom - src.top - 1);
            int syoff1 = sy1 * src.rowPitch;
            int syoff2 = sy2 * src.rowPitch;

            long sx_48 = (stepx >> 1) - 1;
            for (int x = dst.left; x < dst.right; ++x, sx_48 += stepx) {
                temp = (int) (sx_48 >> 36);
                temp = (temp > 0x800) ? (temp - 0x800) : 0;
                int sxf = temp & 0xfff;
                int sx1 = temp >> 12;
                int sx2 = Math.min(sx1 + 1, src.right - src.left - 1);
				
				/*unsigned int sxfsyf = sxf*syf;
				for (unsigned int k = 0; k < channels; k++) {
					unsigned int accum =
						srcdata[(sx1 + syoff1)*channels+k]*(0x1000000-(sxf<<12)-(syf<<12)+sxfsyf) +
						srcdata[(sx2 + syoff1)*channels+k]*((sxf<<12)-sxfsyf) +
						srcdata[(sx1 + syoff2)*channels+k]*((syf<<12)-sxfsyf) +
						srcdata[(sx2 + syoff2)*channels+k]*sxfsyf;
					// accum is computed using 8/24-bit fixed-point math
					// (maximum is 0xFF000000; rounding will not cause overflow)
					*pdst++ = static_cast<uchar>((accum + 0x800000) >> 24);
				}*/

                int sxfsyf = sxf * syf;
                for (int k = 0; k < channels; ++k) {
                    srcdata.position((sx1 + syoff1) * channels + k);
                    int accum11 = srcdata.get() * (0x1000000 - (sxf << 12) - (syf << 12) + sxfsyf);
                    srcdata.position((sx2 + syoff1) * channels + k);
                    accum11 += srcdata.get() * ((sxf << 12) - sxfsyf);
                    srcdata.position((sx1 + syoff2) * channels + k);
                    accum11 += srcdata.get() * ((syf << 12) - sxfsyf);
                    srcdata.position((sx2 + syoff2) * channels + k);
                    accum11 += srcdata.get() * sxfsyf;

                    pdst.put((byte) ((accum11 + 0x800000) >> 24));
                }
            }
			/*pdst += channels*dst.getRowSkip();*/
            pdst.position(pdst.position() + channels * dst.getRowSkip());
        }
    }
}
