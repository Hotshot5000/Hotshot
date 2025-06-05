/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:13 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_ByteBufferInputStream;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormatFlags;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ENG_Image {

    public enum ImageFlags {
        IF_NONE(0),
        IF_COMPRESSED(0x00000001),
        IF_CUBEMAP(0x00000002),
        IF_3D_TEXTURE(0x00000004);

        private int flags;

        ImageFlags(int flags) {
            this.flags = flags;
        }

        public int getFlags() {
            return flags;
        }

        public void addFlags(ImageFlags img) {
            this.flags |= img.getFlags();
        }

        public static ImageFlags getImageFlags(int i) {
            if (i == 0) {
                return IF_NONE;
            }
            ImageFlags flags = IF_NONE;
            if ((i & IF_COMPRESSED.getFlags()) != 0) {
                flags.addFlags(IF_COMPRESSED);
            }
            if ((i & IF_CUBEMAP.getFlags()) != 0) {
                flags.addFlags(IF_CUBEMAP);
            }
            if ((i & IF_3D_TEXTURE.getFlags()) != 0) {
                flags.addFlags(IF_3D_TEXTURE);
            }
            return flags;
        }
    }

    public enum Filter {
        FILTER_NEAREST,
        FILTER_LINEAR,
        FILTER_BILINEAR,
        FILTER_BOX,
        FILTER_TRIANGLE,
        FILTER_BICUBIC
    }

    protected int width;
    protected int height;
    protected int depth;
    protected int size;
    protected int numMipmaps;
    protected ImageFlags flags;
    protected PixelFormat format = PixelFormat.PF_UNKNOWN;
    protected byte pixelSize;
    protected ArrayList<ArrayList<ByteBuffer>> buffer =
            new ArrayList<>();
    protected boolean autoDelete = true;

    public ENG_Image() {

    }

    public ENG_Image(ENG_Image img) {
        set(img);
    }

    public void set(ENG_ByteBufferInputStream is,
                    int width, int height, int depth, int size,
                    int numMipmaps, int flags, int format, byte pixelSize,
                    boolean autoDelete) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.size = size;
        this.numMipmaps = numMipmaps;
        this.flags = ImageFlags.getImageFlags(flags);
        this.format = PixelFormat.getPixelFormat(format);
        this.pixelSize = pixelSize;

        this.autoDelete = autoDelete;

        // We're copying anyway
        this.buffer = loadDataFromInputStream(is.getByteBufferInputStream(),
                width, height, depth, 1, numMipmaps);
    }

    public void set(ENG_Image img) {
        width = img.width;
        height = img.height;
        depth = img.depth;
        size = img.size;
        numMipmaps = img.numMipmaps;
        flags = img.flags;
        format = img.format;
        pixelSize = img.pixelSize;

        autoDelete = img.autoDelete;

        if (autoDelete) {
            for (int i = 0; i < img.getData().size(); ++i) {
                getData().add(new ArrayList<>());
                for (int j = 0; j < img.getData().get(i).size(); ++j) {
                    ByteBuffer imgBuf = img.getData().get(i).get(j);
                    ByteBuffer copy = ENG_Utility.allocateDirect(imgBuf.capacity());
                    int pos = imgBuf.position();
                    int limit = imgBuf.limit();
                    imgBuf.limit(imgBuf.capacity());
                    imgBuf.position(0);
                    copy.put(imgBuf);
                    copy.position(0);
                    imgBuf.limit(limit);
                    imgBuf.position(pos);
                    getData().get(i).add(imgBuf);
                }
            }
        /*	buffer = ENG_Utility.allocateDirect(size);
			int pos = img.buffer.position();
			int limit = img.buffer.limit();
			img.buffer.limit(size);
			img.buffer.position(0);
			buffer.put(img.buffer);
			buffer.position(0);
			img.buffer.limit(limit);
			img.buffer.position(pos);*/
        } else {
            buffer = img.buffer;
        }
    }

    public static int calculateSize(int mipmaps, int faces,
                                    int width, int height, int depth,
                                    int format) {
        return calculateSize(mipmaps, faces, width, height, depth,
                PixelFormat.getPixelFormat(format));
    }

    public static int calculateSize(int mipmaps, int faces,
                                    int width, int height, int depth,
                                    PixelFormat format) {
		/*size_t size = 0;
        for(size_t mip=0; mip<=mipmaps; ++mip)
        {
            size += PixelUtil::getMemorySize(width, height, depth, format)*faces; 
            if(width!=1) width /= 2;
            if(height!=1) height /= 2;
            if(depth!=1) depth /= 2;
        }
        return size;*/
        int size = 0;
        for (int mip = 0; mip <= mipmaps; ++mip) {
            size += ENG_PixelUtil.getMemorySize(width, height, depth, format) * faces;
            if (width != 1) {
                width /= 2;
            }
            if (height != 1) {
                height /= 2;
            }
            if (depth != 1) {
                depth /= 2;
            }
        }
        return size;
    }

    public boolean hasFlag(ImageFlags imgFlag) {
        return (flags.getFlags() & imgFlag.getFlags()) != 0;
    }

    public int getNumFaces() {
        if (hasFlag(ImageFlags.IF_CUBEMAP)) {
            return 6;
        }
        return 1;
    }

    public int getRowSpan() {
        return width * pixelSize;
    }

    public int getBPP() {
        return pixelSize * 8;
    }

    public boolean getHasAlpha() {
        return ((ENG_PixelUtil.getFlags(format) &
                PixelFormatFlags.PFF_HASALPHA.getFlag()) != 0);
    }

    public static void applyGamma(ByteBuffer buffer, float gamma, int size, byte bpp) {
		/*if( gamma == 1.0f )
			return;

		//NB only 24/32-bit supported
		if( bpp != 24 && bpp != 32 ) return;

		uint stride = bpp >> 3;*/

        if (gamma == 1.0f) {
            return;
        }

        if ((bpp != 24) && (bpp != 32)) {
            return;
        }

        int stride = bpp >> 3;
		
		/*for( size_t i = 0, j = size / stride; i < j; i++, buffer += stride )
		{
			float r, g, b;

			r = (float)buffer[0];
			g = (float)buffer[1];
			b = (float)buffer[2];

			r = r * gamma;
			g = g * gamma;
			b = b * gamma;

			float scale = 1.0f, tmp;

			if( r > 255.0f && (tmp=(255.0f/r)) < scale )
				scale = tmp;
			if( g > 255.0f && (tmp=(255.0f/g)) < scale )
				scale = tmp;
			if( b > 255.0f && (tmp=(255.0f/b)) < scale )
				scale = tmp;

			r *= scale; g *= scale; b *= scale;

			buffer[0] = (uchar)r;
			buffer[1] = (uchar)g;
			buffer[2] = (uchar)b;
		}*/

        int bufferPos = buffer.position();
        for (int i = 0, j = size / stride; i < j; ++i, bufferPos += stride) {
            buffer.position(bufferPos);
            float r = buffer.get();
            float g = buffer.get();
            float b = buffer.get();

            r *= gamma;
            g *= gamma;
            b *= gamma;

            float scale = 1.0f;
            float tmp;

            if ((r > 255.0f) && ((tmp = (255.0f / r)) < scale)) {
                scale = tmp;
            }
            if ((g > 255.0f) && ((tmp = (255.0f / g)) < scale)) {
                scale = tmp;
            }
            if ((b > 255.0f) && ((tmp = (255.0f / b)) < scale)) {
                scale = tmp;
            }

            r *= scale;
            g *= scale;
            b *= scale;

            buffer.position(bufferPos);
            buffer.put((byte) r);
            buffer.put((byte) g);
            buffer.put((byte) b);
        }
    }

    public void loadDynamicImage(ArrayList<ArrayList<ByteBuffer>> data,
                                 int width, int height, int depth,
                                 PixelFormat format, boolean autoDelete, int numFaces, int numMipMaps) {
		/*freeMemory();
		// Set image metadata
		m_uWidth = uWidth;
		m_uHeight = uHeight;
		m_uDepth = depth;
		m_eFormat = eFormat;
		m_ucPixelSize = static_cast<uchar>(PixelUtil::getNumElemBytes( m_eFormat ));
		m_uNumMipmaps = numMipMaps;
		m_uFlags = 0;*/
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.format = format;
        this.pixelSize = (byte) ENG_PixelUtil.getNumElemBytes(format);
        this.numMipmaps = numMipMaps;
        this.flags = ImageFlags.IF_NONE;
		/*if (PixelUtil::isCompressed(eFormat))
			m_uFlags |= IF_COMPRESSED;
		if (m_uDepth != 1)
			m_uFlags |= IF_3D_TEXTURE;
		if(numFaces == 6)
			m_uFlags |= IF_CUBEMAP;
		if(numFaces != 6 && numFaces != 1)
			OGRE_EXCEPT(Exception::ERR_INVALIDPARAMS, 
			"Number of faces currently must be 6 or 1.", 
			"Image::loadDynamicImage");

		m_uSize = calculateSize(numMipMaps, numFaces, uWidth, uHeight, depth, eFormat);
		m_pBuffer = pData;
		m_bAutoDelete = autoDelete;*/
        if (ENG_PixelUtil.isCompressed(format)) {
            this.flags.addFlags(ImageFlags.IF_COMPRESSED);
        }
        if (this.depth != 1) {
            this.flags.addFlags(ImageFlags.IF_3D_TEXTURE);
        }
        if (numFaces == 6) {
            this.flags.addFlags(ImageFlags.IF_CUBEMAP);
        }
        if ((numFaces != 6) && (numFaces != 1)) {
            throw new IllegalArgumentException(
                    "Number of faces currently must be 6 or 1");
        }

        this.size = calculateSize(numMipMaps, numFaces, width, height, depth, format);
        this.buffer = data;
        this.autoDelete = autoDelete;
    }

    public ENG_PixelBox getPixelBox(int face, int mipmap) {
        ENG_PixelBox ret = new ENG_PixelBox();
        getPixelBox(face, mipmap, ret);
        return ret;
    }

    public void loadRawData(InputStream is,
                            int width, int height,
                            PixelFormat pf) {
        loadRawData(is, width, height, 1, pf, 1, 0);
    }

    public void loadRawData(InputStream is,
                            int width, int height, int depth,
                            PixelFormat pf) {
        loadRawData(is, width, height, depth, pf, 1, 0);
    }

    public void loadRawData(InputStream is,
                            int width, int height, int depth,
                            PixelFormat pf, int numFaces, int numMipmaps) {
        ArrayList<ArrayList<ByteBuffer>> bufList = loadDataFromInputStream(is,
                width, height, depth, numFaces, numMipmaps);
        loadDynamicImage(bufList, width, height, depth, pf, true,
                numFaces, numMipmaps);
    }

    private ArrayList<ArrayList<ByteBuffer>> loadDataFromInputStream(
            InputStream is, int width, int height, int depth, int numFaces,
            int numMipmaps) {
        int size = width * height * depth;
//		int bufCount = numFaces * numMipmaps;
        byte[] b = new byte[size];
        ArrayList<ArrayList<ByteBuffer>> bufList =
                new ArrayList<>();
        for (int i = 0; i < numFaces; ++i) {
            bufList.add(new ArrayList<>());
        }
        try {
            for (int i = 0; i < numFaces; ++i) {
                for (int j = 0; j < numMipmaps; ++j) {
                    int read = is.read(b);
                    if (read != size) {
                        throw new IOException("read bytes " + read +
                                " different " +
                                "from supposed read " + size);
                    }
                    ByteBuffer buf = ENG_Utility.allocateDirect(size);
                    buf.put(b);
                    buf.flip();
                    bufList.get(i).add(buf);
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
        return bufList;
    }

//	public void getPixelBox(int face, int mipmap, ENG_PixelBox ret) {
    // Image data is arranged as:
    // face 0, top level (mip 0)
    // face 0, mip 1
    // face 0, mip 2
    // face 1, top level (mip 0)
    // face 1, mip 1
    // face 1, mip 2
    // etc
		
		/*if(mipmap > getNumMipmaps())
			OGRE_EXCEPT( Exception::ERR_NOT_IMPLEMENTED,
			"Mipmap index out of range",
			"Image::getPixelBox" ) ;
		if(face >= getNumFaces())
			OGRE_EXCEPT(Exception::ERR_INVALIDPARAMS, "Face index out of range",
			"Image::getPixelBox");
        // Calculate mipmap offset and size
        uint8 *offset = const_cast<uint8*>(getData());
		// Base offset is number of full faces
        size_t width = getWidth(), height=getHeight(), depth=getDepth();
		size_t numMips = getNumMipmaps();*/
	/*	if (mipmap > getNumMipmaps()) {
			throw new IllegalArgumentException("Mipmap index out of range");
		}
		if (face >= getNumFaces()) {
			throw new IllegalArgumentException("Face index out of range");
		}
		int offset = getData().position();
		int width = getWidth();
		int height = getHeight();
		int depth = getDepth();
		int numMips = getNumMipmaps();*/
		
		/*// Figure out the offsets 
		size_t fullFaceSize = 0;
		size_t finalFaceSize = 0;
		size_t finalWidth = 0, finalHeight = 0, finalDepth = 0;*/
    //	int fullFaceSize = 0;
    //	int finalFaceSize = 0;
    //	int finalWidth = 0, finalHeight = 0, finalDepth = 0;
		
		/*for(size_t mip=0; mip <= numMips; ++mip)
        {
			if (mip == mipmap)
			{
				finalFaceSize = fullFaceSize;
				finalWidth = width;
				finalHeight = height;
				finalDepth = depth;
			}
            fullFaceSize += PixelUtil::getMemorySize(width, height, depth, getFormat());

            /// Half size in each dimension
            if(width!=1) width /= 2;
            if(height!=1) height /= 2;
            if(depth!=1) depth /= 2;
        }*/
	/*	for (int mip = 0; mip <= numMips; ++mip) {
			if (mip == mipmap) {
				finalFaceSize = fullFaceSize;
				finalWidth = width;
				finalHeight = height;
				finalDepth = depth;
			}
			fullFaceSize += ENG_PixelUtil.getMemorySize(
					width, height, depth, getFormat());
			if (width != 1) {
				width /= 2;
			}
			if (height != 1) {
				height /= 2;				
			}
			if (depth != 1) {
				depth /= 2;
			}
		}*/
		/*// Advance pointer by number of full faces, plus mip offset into
		offset += face * fullFaceSize;
		offset += finalFaceSize;
		// Return subface as pixelbox
		PixelBox src(finalWidth, finalHeight, finalDepth, getFormat(), offset);
		return src;*/
	/*	offset += face * fullFaceSize;
		offset += finalFaceSize;
		getData().position(offset);
		ret.set(finalWidth, finalHeight, finalDepth, getFormat(), getData());*/
//	}

    public void getPixelBox(int face, int mipmap, ENG_PixelBox ret) {
        if (mipmap > getNumMipmaps()) {
            throw new IllegalArgumentException("Mipmap index out of range");
        }
        if (face >= getNumFaces()) {
            throw new IllegalArgumentException("Face index out of range");
        }
        ByteBuffer data = getData().get(face).get(mipmap);
        int width = getWidth();
        int height = getHeight();
        int depth = getDepth();
        if (mipmap != 0) {
            int half = 2 * mipmap;
            width /= half;
            height /= half;
            depth /= half;
        }
        ret.set(width, height, depth, getFormat(), data);
    }

    public void resize(int width, int height, Filter filter) {
		/*// resizing dynamic images is not supported
		assert(m_bAutoDelete);
		assert(m_uDepth == 1);

		// reassign buffer to temp image, make sure auto-delete is true
		Image temp;
		temp.loadDynamicImage(m_pBuffer, m_uWidth, m_uHeight, 1, m_eFormat, true);
		// do not delete[] m_pBuffer!  temp will destroy it*/
        if (!autoDelete) {
            throw new UnsupportedOperationException(
                    "Resizing dynamic images not supported");
        }
        if (depth != 1) {
            throw new UnsupportedOperationException("Depth must be 1");
        }
        ENG_Image temp = new ENG_Image();
        temp.loadDynamicImage(buffer, width, height, 1, format, false, 1, 0);
		
		/*// set new dimensions, allocate new buffer
		m_uWidth = width;
		m_uHeight = height;
		m_uSize = PixelUtil::getMemorySize(m_uWidth, m_uHeight, 1, m_eFormat);
		m_pBuffer = OGRE_ALLOC_T(uchar, m_uSize, MEMCATEGORY_GENERAL);
        m_uNumMipmaps = 0; // Loses precomputed mipmaps

		// scale the image from temp into our resized buffer
		Image::scale(temp.getPixelBox(), getPixelBox(), filter);*/
        this.width = width;
        this.height = height;
        this.size = ENG_PixelUtil.getMemorySize(width, height, 1, format);
        this.buffer.clear();
        this.buffer.add(new ArrayList<>());
        this.buffer.get(0).add(ENG_Utility.allocateDirect(size));
        this.numMipmaps = 0;

        scale(temp.getPixelBox(0, 0), getPixelBox(0, 0), filter);
    }

    public static void scale(ENG_PixelBox src, ENG_PixelBox scaled, Filter filter) {
		/*assert(PixelUtil::isAccessible(src.format));
		assert(PixelUtil::isAccessible(scaled.format));
		MemoryDataStreamPtr buf; // For auto-delete
		PixelBox temp;*/

        if (!ENG_PixelUtil.isAccessible(src.pixelFormat)) {
            throw new IllegalArgumentException("src is not accessible");
        }
        if (!ENG_PixelUtil.isAccessible(scaled.pixelFormat)) {
            throw new IllegalArgumentException("scaled is not accessible");
        }
        ByteBuffer buf = null;
        ENG_PixelBox temp;// new ENG_PixelBox();

        switch (filter) {
            default:
            case FILTER_NEAREST:
			/*if(src.format == scaled.format) 
			{
				// No intermediate buffer needed
				temp = scaled;
			}
			else
			{
				// Allocate temporary buffer of destination size in source format 
				temp = PixelBox(scaled.getWidth(), scaled.getHeight(), scaled.getDepth(), src.format);
				buf.bind(OGRE_NEW MemoryDataStream(temp.getConsecutiveSize()));
				temp.data = buf->getPtr();
			}*/
                if (src.pixelFormat == scaled.pixelFormat) {
                    temp = scaled;
                } else {
                    temp = new ENG_PixelBox(scaled.getWidth(), scaled.getHeight(),
                            scaled.getDepth(), src.pixelFormat, null);
                    temp.data = ENG_Utility.allocateDirect(temp.getConsecutiveSize());
                }
                ENG_ImageResampler.scaleNearestResampler(src, temp,
                        ENG_PixelUtil.getNumElemBytes(src.pixelFormat));
                if (temp.data != scaled.data) {
                    ENG_PixelUtil.bulkPixelConversion(temp, scaled);
                }
                break;
            case FILTER_LINEAR:
            case FILTER_BILINEAR:
                switch (src.pixelFormat) {
                    case PF_L8:
                    case PF_A8:
                    case PF_BYTE_LA:
                    case PF_R8G8B8:
                    case PF_B8G8R8:
                    case PF_R8G8B8A8:
                    case PF_B8G8R8A8:
                    case PF_A8B8G8R8:
                    case PF_A8R8G8B8:
                    case PF_X8B8G8R8:
                    case PF_X8R8G8B8:
                        if (src.pixelFormat == scaled.pixelFormat) {
                            temp = scaled;
                        } else {
                            temp = new ENG_PixelBox(scaled.getWidth(), scaled.getHeight(),
                                    scaled.getDepth(), src.pixelFormat, null);
                            temp.data = ENG_Utility.allocateDirect(temp.getConsecutiveSize());
                        }
                        ENG_ImageResampler.scaleLinearResampler_Byte(src, temp,
                                ENG_PixelUtil.getNumElemBytes(src.pixelFormat));
                        if (temp.data != scaled.data) {
                            ENG_PixelUtil.bulkPixelConversion(temp, scaled);
                        }
                        break;
                    case PF_FLOAT32_RGB:
                    case PF_FLOAT32_RGBA:
                        if (scaled.pixelFormat == PixelFormat.PF_FLOAT32_RGB ||
                                scaled.pixelFormat == PixelFormat.PF_FLOAT32_RGBA) {
                            ENG_ImageResampler.scaleLinearResampler_Float32(src, scaled);
                            break;
                        }
                        //Fall through
                    default:
                        ENG_ImageResampler.scaleLinearResampler(src, scaled);
                }
        }
    }

    public void getColorAt(int x, int y, int z, ENG_ColorValue ret) {
        ByteBuffer buffer = getData().get(0).get(0);
        int pos = buffer.position();
        buffer.position(pixelSize * (z * width * height + y * width + x));
        ENG_PixelUtil.unpackColour(ret, format, buffer);
        buffer.position(pos);
    }

    public ENG_ColorValue getColorAt(int x, int y, int z) {
        ENG_ColorValue ret = new ENG_ColorValue();
        getColorAt(x, y, z, ret);
        return ret;
    }

    public ArrayList<ArrayList<ByteBuffer>> getData() {
        return buffer;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @return the numMipmaps
     */
    public int getNumMipmaps() {
        return numMipmaps;
    }

    /**
     * @return the format
     */
    public PixelFormat getFormat() {
        return format;
    }

    /**
     * @return the buffer
     */
//	public ByteBuffer getBuffer() {
//		return buffer;
//	}
}
