/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.Usage;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public abstract class ENG_Texture {

    public enum TextureUsage {
        /// @copydoc HardwareBuffer::Usage
        TU_STATIC(Usage.HBU_STATIC.getUsage()),
        TU_DYNAMIC(Usage.HBU_DYNAMIC.getUsage()),
        TU_WRITE_ONLY(Usage.HBU_WRITE_ONLY.getUsage()),
        TU_STATIC_WRITE_ONLY(Usage.HBU_STATIC_WRITE_ONLY.getUsage()),
        TU_DYNAMIC_WRITE_ONLY(Usage.HBU_DYNAMIC_WRITE_ONLY.getUsage()),
        TU_DYNAMIC_WRITE_ONLY_DISCARDABLE(
                Usage.HBU_DYNAMIC_WRITE_ONLY_DISCARDABLE.getUsage()),
        /// mipmaps will be automatically generated for this texture
        TU_AUTOMIPMAP(0x100),
        /// this texture will be a render target, i.e. used as a target for render to texture
        /// setting this flag will ignore all other texture usages except TU_AUTOMIPMAP
        TU_RENDERTARGET(0x200),
        /// default to automatic mipmap generation static textures
        TU_DEFAULT(TU_AUTOMIPMAP.getUsage() | TU_STATIC_WRITE_ONLY.getUsage());

        private final int usage;

        TextureUsage(int usage) {
            this.usage = usage;
        }

        public int getUsage() {
            return usage;
        }

    }

    public enum TextureType {
        /// 1D texture, used in combination with 1D texture coordinates
        TEX_TYPE_1D(1),
        /// 2D texture, used in combination with 2D texture coordinates (default)
        TEX_TYPE_2D(2),
        /// 3D volume texture, used in combination with 3D texture coordinates
        TEX_TYPE_3D(3),
        /// 3D cube map, used in combination with 3D texture coordinates
        TEX_TYPE_CUBE_MAP(4);

        private final int type;

        TextureType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        /**
         * For GIWS since we don't have enum wrapping
         *
         * @param i
         * @return
         */
        public static TextureType getTextureType(int i) {
            switch (i) {
                case 1:
                    return TEX_TYPE_1D;
                case 2:
                    return TEX_TYPE_2D;
                case 3:
                    return TEX_TYPE_3D;
                case 4:
                    return TEX_TYPE_CUBE_MAP;
                default:
                    throw new IllegalArgumentException("Cannot convert " + i +
                            " to a texture type");
            }
        }
    }

    public enum TextureMipmap {
        /// Generate mipmaps up to 1x1
        MIP_UNLIMITED(0x7FFFFFFF),
        /// Use TextureManager default
        MIP_DEFAULT(-1);

        private final int mipmap;

        TextureMipmap(int mipmap) {
            this.mipmap = mipmap;
        }

        public int getMipmap() {
            return mipmap;
        }
    }

    protected int width;
    protected int height;
    protected int depth;

    protected int numRequestedMipmaps;
    protected int numMipmaps;
    protected boolean mipmapsHardwareGenerated;
    protected float gamma = 1.0f;
    protected boolean hwGamma;
    protected int FSAA;
    protected String FSAAHint;

    protected TextureType textureType = TextureType.TEX_TYPE_2D;
    protected PixelFormat format = PixelFormat.PF_UNKNOWN;
    protected int usage = TextureUsage.TU_DEFAULT.getUsage();
    protected PixelFormat srcFormat = PixelFormat.PF_UNKNOWN;
    protected int srcWidth;
    protected int srcHeight;
    protected int srcDepth;

    protected PixelFormat desiredFormat = PixelFormat.PF_UNKNOWN;
    protected short desiredIntegerBitDepth;
    protected short desiredFloatBitDepth;

    protected boolean treatLuminanceAsAlpha;

    protected boolean internalResourceCreate;
    private boolean mInternalResourcesCreated;
    public final String name;

    private boolean useShadowBuffer; //Hack since we can't download a texture in es 2.0


    public boolean isUseShadowBuffer() {
        return useShadowBuffer;
    }

    public void setUseShadowBuffer(boolean useShadowBuffer) {
        this.useShadowBuffer = useShadowBuffer;
    }

    public ENG_HardwarePixelBuffer getBuffer() {
        return getBuffer(0, 0);
    }

    public abstract ENG_HardwarePixelBuffer getBuffer(int face, int mipmap);

    public abstract void createInternalResourcesImpl();

    public abstract void freeInternalResourceImpl(boolean skipGLDelete);

    public ENG_Texture(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void copyToTexture(ENG_Texture target) {
        if (getNumFaces() != target.getNumFaces()) {
            throw new IllegalArgumentException("Texture types must match");
        }
        /*size_t numMips = std::min(getNumMipmaps(), target->getNumMipmaps());
        if((mUsage & TU_AUTOMIPMAP) || (target->getUsage()&TU_AUTOMIPMAP))
            numMips = 0;
        for(unsigned int face=0; face<getNumFaces(); face++)
        {
            for(unsigned int mip=0; mip<=numMips; mip++)
            {
                target->getBuffer(face, mip)->blit(getBuffer(face, mip));
            }
        }*/
        int numMips = Math.min(getNumMipmaps(), target.getNumMipmaps());
        if (((usage & TextureUsage.TU_AUTOMIPMAP.getUsage()) != 0) ||
                ((target.getUsage() & TextureUsage.TU_AUTOMIPMAP.getUsage()) != 0)) {
            numMips = 0;
        }
        for (int face = 0; face < getNumFaces(); ++face) {
            for (int mip = 0; mip < numMips; ++mip) {
                target.getBuffer(face, mip).blit(getBuffer(face, mip));
            }
        }
    }

    public void loadRawData(InputStream is, int width, int height,
                            PixelFormat pf) {

        ENG_Image img = new ENG_Image();
        img.loadRawData(is, width, height, pf);
        loadImage(img);
    }

    public void loadImage(ENG_Image image) {
        ArrayList<ENG_Image> imgList = new ArrayList<>();
        imgList.add(image);
        _loadImages(imgList);
    }

    public void _loadImages(ArrayList<ENG_Image> images, boolean useShadowBuffer) {
        setUseShadowBuffer(useShadowBuffer);
        _loadImages(images);
    }

    public void _loadImages(ArrayList<ENG_Image> images) {
        if (images.isEmpty()) {
            throw new IllegalArgumentException("Cannot load empty vector of images");
        }
		
		/*// Set desired texture size and properties from images[0]
		mSrcWidth = mWidth = images[0]->getWidth();
		mSrcHeight = mHeight = images[0]->getHeight();
		mSrcDepth = mDepth = images[0]->getDepth();

        // Get source image format and adjust if required
        mSrcFormat = images[0]->getFormat();
        if (mTreatLuminanceAsAlpha && mSrcFormat == PF_L8)
        {
            mSrcFormat = PF_A8;
        }*/
        ENG_Image first = images.get(0);
        srcWidth = width = first.getWidth();
        srcHeight = height = first.getHeight();
        srcDepth = depth = first.getDepth();
        srcFormat = first.getFormat();
        if ((treatLuminanceAsAlpha) && (srcFormat == PixelFormat.PF_L8)) {
            srcFormat = PixelFormat.PF_A8;
        }
		
		/*if (mDesiredFormat != PF_UNKNOWN)
        {
            // If have desired format, use it
            mFormat = mDesiredFormat;
        }
        else
        {
            // Get the format according with desired bit depth
            mFormat = PixelUtil::getFormatForBitDepths(mSrcFormat, mDesiredIntegerBitDepth, mDesiredFloatBitDepth);
        }*/
        if (desiredFormat != PixelFormat.PF_UNKNOWN) {
            format = desiredFormat;
        } else {
            format = ENG_PixelUtil.getFormatForBitDepths(
                    srcFormat, desiredIntegerBitDepth, desiredFloatBitDepth);
        }
		
		/*// The custom mipmaps in the image have priority over everything
        size_t imageMips = images[0]->getNumMipmaps();

		if(imageMips > 0)
		{
			mNumMipmaps = mNumRequestedMipmaps = images[0]->getNumMipmaps();
			// Disable flag for auto mip generation
			mUsage &= ~TU_AUTOMIPMAP;
		}

        // Create the texture
        createInternalResources();*/
        int imageMips = first.getNumMipmaps();
        if (imageMips > 0) {
            numMipmaps = numRequestedMipmaps = first.getNumMipmaps();
            usage &= ~TextureUsage.TU_AUTOMIPMAP.getUsage();
        }

        createInternalResources();
		
		/*size_t faces;
		bool multiImage; // Load from multiple images?
		if(images.size() > 1)
		{
			faces = images.size();
			multiImage = true;
		}
		else
		{
			faces = images[0]->getNumFaces();
			multiImage = false;
		}*/
        int faces;
        boolean multiImage = false;
        if (images.size() > 1) {
            faces = images.size();
            multiImage = true;
        } else {
            faces = first.getNumFaces();
        }

        // Check wether number of faces in images exceeds number of faces
        // in this texture. If so, clamp it.
        if (faces > getNumFaces())
            faces = getNumFaces();

        ENG_PixelBox src = new ENG_PixelBox();
        for (int mip = 0; mip <= imageMips; ++mip) {
            for (int i = 0; i < faces; ++i) {
				/*PixelBox src;
                if(multiImage)
                {
                    // Load from multiple images
                    src = images[i]->getPixelBox(0, mip);
                }
                else
                {
                    // Load from faces of images[0]
                    src = images[0]->getPixelBox(i, mip);
                }*/
                if (multiImage) {
                    src.set(images.get(i).getPixelBox(0, mip));
                } else {
                    src.set(images.get(0).getPixelBox(i, mip));
                }

                src.pixelFormat = srcFormat;

                if (gamma != 1.0f) {
					/*// Apply gamma correction
                    // Do not overwrite original image but do gamma correction in temporary buffer
                    MemoryDataStreamPtr buf; // for scoped deletion of conversion buffer
                    buf.bind(OGRE_NEW MemoryDataStream(
                        PixelUtil::getMemorySize(
                            src.getWidth(), src.getHeight(), src.getDepth(), src.format)));
                    
                    PixelBox corrected = PixelBox(src.getWidth(), src.getHeight(), src.getDepth(), src.format, buf->getPtr());
                    PixelUtil::bulkPixelConversion(src, corrected);
                    
                    Image::applyGamma(static_cast<uint8*>(corrected.data), mGamma, corrected.getConsecutiveSize(), 
                        static_cast<uchar>(PixelUtil::getNumElemBits(src.format)));
    
                    // Destination: entire texture. blitFromMemory does the scaling to
                    // a power of two for us when needed
                    getBuffer(i, mip)->blitFromMemory(corrected);*/
                    ByteBuffer buf = ENG_Utility.allocateDirect(
                            ENG_PixelUtil.getMemorySize(src.getWidth(),
                                    src.getHeight(), src.getDepth(), src.pixelFormat));
                    ENG_PixelBox corrected = new ENG_PixelBox(
                            src.getWidth(), src.getHeight(), src.getDepth(),
                            src.pixelFormat, buf);
                    ENG_PixelUtil.bulkPixelConversion(src, corrected);

                    ENG_Image.applyGamma(corrected.data, gamma,
                            corrected.getConsecutiveSize(),
                            (byte) ENG_PixelUtil.getNumElemBits(srcFormat));

                    getBuffer(i, mip).blitFromMemory(corrected);
                } else {
                    getBuffer(i, mip).blitFromMemory(src);
                }
            }
        }
		
		/*// Update size (the final size, not including temp space)
        mSize = getNumFaces() * PixelUtil::getMemorySize(mWidth, mHeight, mDepth, mFormat);*/
        int size = getNumFaces() * ENG_PixelUtil.getMemorySize(width, height, depth, format);
//		MainApp.getMainThread().flushGLPipeline();
    }

    public void createInternalResources() {
        if (!mInternalResourcesCreated) {
            createInternalResourcesImpl();
            mInternalResourcesCreated = true;
        }
    }

    public void convertToImage(ENG_Image destImage, boolean includeMipMaps) {
		/*size_t numMips = includeMipMaps? getNumMipmaps() + 1 : 1;
		size_t dataSize = Image::calculateSize(numMips,
			getNumFaces(), getWidth(), getHeight(), getDepth(), getFormat());

		void* pixData = OGRE_MALLOC(dataSize, Ogre::MEMCATEGORY_GENERAL);
		// if there are multiple faces and mipmaps we must pack them into the data
		// faces, then mips
		void* currentPixData = pixData;*/
        int numMips = includeMipMaps ? getNumMipmaps() + 1 : 1;
        //	int dataSize = ENG_Image.calculateSize(numMips,
        //			getNumFaces(), getWidth(), getHeight(), getDepth(), getFormat());
        //	ByteBuffer pixData = ENG_Utility.allocateDirect(dataSize);
        ArrayList<ArrayList<ByteBuffer>> pixData =
                new ArrayList<>();
        for (int face = 0; face < getNumFaces(); ++face) {
            pixData.add(new ArrayList<>());
            for (int mip = 0; mip < numMips; ++mip) {
                int mipDataSize = ENG_PixelUtil.getMemorySize(
                        getWidth(), getHeight(), getDepth(), getFormat());
                ByteBuffer currentPixData = ENG_Utility.allocateDirect(mipDataSize);
                ENG_PixelBox pixBox =
                        new ENG_PixelBox(getWidth(), getHeight(), getDepth(),
                                getFormat(), currentPixData);
                getBuffer(face, mip).blitToMemory(pixBox);
                pixData.get(face).add(currentPixData);
            }
        }
		/*for (size_t face = 0; face < getNumFaces(); ++face)
		{
			for (size_t mip = 0; mip < numMips; ++mip)
			{
				size_t mipDataSize = PixelUtil::getMemorySize(getWidth(), getHeight(), getDepth(), getFormat());

				Ogre::PixelBox pixBox(getWidth(), getHeight(), getDepth(), getFormat(), currentPixData);
				getBuffer(face, mip)->blitToMemory(pixBox);

				currentPixData = (void*)((char*)currentPixData + mipDataSize);

			}
		}*/
		/*// load, and tell Image to delete the memory when it's done.
		destImage.loadDynamicImage((Ogre::uchar*)pixData, getWidth(), getHeight(), getDepth(), getFormat(), true, 
			getNumFaces(), numMips - 1);*/
        destImage.loadDynamicImage(pixData, getWidth(), getHeight(), getDepth(),
                getFormat(), false, getNumFaces(), numMips - 1);
    }

    public void setTextureType(TextureType ttype) {
        textureType = ttype;
    }

    public TextureType getTextureType() {
        return textureType;
    }

    public void setNumMipmaps(int num) {
        numRequestedMipmaps = numMipmaps = num;
    }

    public int getNumMipmaps() {
        return numMipmaps;
    }

    public boolean getMipmapsHardwareGenerated() {
        return mipmapsHardwareGenerated;
    }

    public void setGamma(float g) {
        gamma = g;
    }

    public float getGamma() {
        return gamma;
    }

    public void setHardwareGammaEnabled(boolean b) {
        hwGamma = b;
    }

    public boolean isHardwareGammaEnabled() {
        return hwGamma;
    }

    public void setFSAA(int fsaa, String fsaaHint) {
        FSAA = fsaa;
        FSAAHint = fsaaHint;
    }

    public int getFSAA() {
        return FSAA;
    }

    public String getFSAAHint() {
        return FSAAHint;
    }

    public void setFormat(PixelFormat pf) {
        format = pf;
        desiredFormat = pf;
        srcFormat = pf;
    }

    public boolean hasAlpha() {
        return ENG_PixelUtil.hasAlpha(format);
    }

    public void setDesiredIntegerBitDepth(short depth) {
        desiredIntegerBitDepth = depth;
    }

    public short getDesiredIntegerBitDepth() {
        return desiredIntegerBitDepth;
    }

    public void setDesiredFloatBitDepth(short depth) {
        desiredFloatBitDepth = depth;
    }

    public short getDesiredFloatBitDepth() {
        return desiredFloatBitDepth;
    }

    public void setDesiredBitDepths(short intDepth, short floatDepth) {
        desiredIntegerBitDepth = intDepth;
        desiredFloatBitDepth = floatDepth;
    }

    public void setTreatLuminanceAsAlpha(boolean alpha) {
        treatLuminanceAsAlpha = alpha;
    }

    public boolean getTreatLuminanceAsAlpha() {
        return treatLuminanceAsAlpha;
    }

    public int calculateSize() {
        return (getNumFaces() * ENG_PixelUtil.getMemorySize(width, height, depth, format));
    }

    public int getNumFaces() {
        return (getTextureType() == TextureType.TEX_TYPE_CUBE_MAP ? 6 : 1);
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        srcWidth = this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        srcHeight = this.height = height;
    }

    /**
     * @return the depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @param depth the depth to set
     */
    public void setDepth(int depth) {
        srcDepth = this.depth = depth;
    }

    /**
     * @return the srcWidth
     */
    public int getSrcWidth() {
        return srcWidth;
    }

    /**
     * @return the srcHeight
     */
    public int getSrcHeight() {
        return srcHeight;
    }

    /**
     * @return the srcDepth
     */
    public int getSrcDepth() {
        return srcDepth;
    }

    public void setUsage(int u) {
        usage = u;
    }

    public int getUsage() {
        return usage;
    }

    public PixelFormat getFormat() {
        return format;
    }

    public PixelFormat getDesiredFormat() {
        return desiredFormat;
    }

    public PixelFormat getSrcFormat() {
        return srcFormat;
    }

    public abstract void prepareImpl();

    public abstract void loadImpl();

    public void unloadImpl(boolean skipGLDelete) {
        freeInternalResourceImpl(skipGLDelete);
    }
}
