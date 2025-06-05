//
// Created by sebas on 22.02.2017.
//

#include <OgreRoot.h>
#include "HotshotCommon.h"
#include "HotshotGorillaGUI.h"
#include "HotshotGorillaGUINinePatch.h"
#include "OgrePixelFormatGpu.h"
#include "OgreTextureBox.h"
#include "OgreColourValue.h"
#include "OgreTextureGpu.h"
// #include "OgreHardwarePixelBuffer.h"
#include "OgreTextureGpuManager.h"
#include "OgreStagingTexture.h"
#include "OgreAsyncTextureTicket.h"
#include "HotshotGorillaGUIScreenRenderable.h"
//#include "Math/Array/OgreNodeMemoryManager.h"
//#include "OgreSceneNode.h"

namespace Hotshot
{

    std::map<::Ogre::uint8, ScreenRenderable*> GorillaGUI::screenRenderableMap;

//    ::Ogre::Texture** GorillaGUI::atlasPtrList = 0;
    void** GorillaGUI::bufArr = 0;
    int GorillaGUI::currentFrameNum = 0;
    size_t GorillaGUI::bufferSize = 0;
//    int GorillaGUI::atlasPtrListSize = 0;
    ::Ogre::NodeMemoryManager *GorillaGUI::mNodeMemoryManager = 0;
    ::Ogre::SceneNode *GorillaGUI::mDummyNode = 0;

    int Hotshot::GorillaGUI::createNinePatch(JNIEnv *env, ::Ogre::TextureGpu* mTexture, jobject np)
    {
        NinePatch ninePatch = NinePatch::readNinePatch(env, np);
        ::Ogre::TextureGpuManager* textureManager = ::Ogre::Root::getSingleton().getRenderSystem()->getTextureGpuManager();
        // ::Ogre::StagingTexture* stagingTexture =
        //     textureManager->getStagingTexture(mTexture->getWidth(), mTexture->getHeight(), 1u, 1u, ::Ogre::PFG_RGBA8_SNORM);
        // stagingTexture->startMapRegion();
        // ::Ogre::TextureBox currentLock = stagingTexture->mapRegion(mTexture->getWidth(), mTexture->getHeight(), 1u, 1u, ::Ogre::PFG_RGBA8_SNORM);

        ::Ogre::Image2 image;
        image.convertFromTexture(mTexture, 0, 0);
        ::Ogre::TextureBox currentLock = image.getData( 0 );

        // ::Ogre::AsyncTextureTicket* asyncTicket =
        //     textureManager->createAsyncTextureTicket(mTexture->getWidth(), mTexture->getHeight(), 1,
        //         mTexture->getTextureType(),
        //         mTexture->getPixelFormat());
        // asyncTicket->download(mTexture, 0, true);
        //
        // ::Ogre::TextureBox dstBox = mTexture->getData(mip - minMip);
        //
        // if (asyncTicket->canMapMoreThanOneSlice())
        // {
        //     const TextureBox srcBox = asyncTicket->map(0);
        //     dstBox.copyFrom(srcBox);
        //     asyncTicket->unmap();
        // }
        // else
        // {
        //     for (size_t i = 0; i < asyncTicket->getNumSlices(); ++i)
        //     {
        //         const TextureBox srcBox = asyncTicket->map(i);
        //         dstBox.copyFrom(srcBox);
        //         dstBox.data = dstBox.at(0, 0, 1u);
        //         --dstBox.numSlices;
        //         asyncTicket->unmap();
        //     }
        // }
        //
        // textureManager->destroyAsyncTextureTicket(asyncTicket);
        // asyncTicket = 0;

        // const ::Ogre::v1::HardwarePixelBufferSharedPtr &buffer = mTexture->getBuffer();
        // void *lock = buffer->lock(::Ogre::v1::HardwarePixelBuffer::HBL_READ_ONLY);
        // const ::Ogre::TextureBox &currentLock = buffer->getCurrentLock();

//        int *pInt = (int*) currentLock.data;
//        for (int i = 0; i < 262144; ++i)
//        {
//            if (*pInt++ != 0)
//            {
//                ::Ogre::String lockData("locked data:  " + SSTR(i) + " " + SSTR(*pInt));
//                LOGI("%s", lockData.c_str());
//            }
//
//        }
        
        int width = (int) ninePatch.spriteWidthInPixels;//currentLock.getWidth();
        int height = (int) ninePatch.spriteHeightInPixels;//currentLock.getHeight();
        ::Ogre::Box topVolume = ::Ogre::Box(
                (::Ogre::uint32) ninePatch.uvLeftInPixels,//0,
                (::Ogre::uint32) ninePatch.uvTopInPixels,//0,
                (::Ogre::uint32) ninePatch.uvRightInPixels,//width,
                (::Ogre::uint32) (ninePatch.uvTopInPixels + 1));

        int bytesPerPixel = currentLock.bytesPerPixel;
        int topLineBegin = -1, topLineEnd = -1,
                leftLineBegin = -1, leftLineEnd = -1,
                rightLineBegin = -1, rightLineEnd = -1,
                bottomLineBegin = -1, bottomLineEnd = -1;
        ::Ogre::ColourValue col;
        // uint8_t *data = reinterpret_cast<uint8_t *>(currentLock.data);
        for (int x = topVolume.left; x < topVolume.right; ++x) {
            int prevData = *(reinterpret_cast<int*>(currentLock.at(x, topVolume.top, 0)));

//            ::Ogre::String prevDataStr("x: " + SSTR(x) + " prevDataTopLeftRight " + SSTR(prevData) + " topVolume.top: " + SSTR(topVolume.top) + " texWidth: " + SSTR(mTexture->getWidth()) + " texHeight: " + SSTR(mTexture->getHeight()) + " texName: " + SSTR(mTexture->getNameStr()));
//            LOGI("%s\n", prevDataStr.c_str());
            extractColor(mTexture, currentLock, col, prevData);
            if (equalsWithoutAlpha(col, ::Ogre::ColourValue::Black) && col.a > 0.0f) {
                if (topLineBegin == -1) {
                    topLineBegin = x - topVolume.left;
                }
                if (topLineBegin != -1) {
                    topLineEnd = x - topVolume.left;
                }
            }
            // data += bytesPerPixel;
        }


        ::Ogre::Box bottomVolume = ::Ogre::Box(
                (::Ogre::uint32) ninePatch.uvLeftInPixels,//0,
                (::Ogre::uint32) (ninePatch.uvBottomInPixels - 1),//currentLock.getHeight() - 1,
                (::Ogre::uint32) ninePatch.uvRightInPixels,//width,
                (::Ogre::uint32) ninePatch.uvBottomInPixels//currentLock.getHeight()
        );
        // data = reinterpret_cast<uint8_t *>(bottomVolume.data);
        for (int x = bottomVolume.left; x < bottomVolume.right; ++x) {
            int prevData = *(reinterpret_cast<int*>(currentLock.at(x, bottomVolume.top, 0)));
            // int prevData = *(reinterpret_cast<int*>(data));
            extractColor(mTexture, currentLock, col, prevData);

//            ::Ogre::String prevDataStr("prevDataBottomLeftRight " + SSTR(prevData));
//            LOGI("%s", prevDataStr.c_str());
            if (equalsWithoutAlpha(col, ::Ogre::ColourValue::Black) && col.a > 0.0f) {
                if (bottomLineBegin == -1) {
                    bottomLineBegin = x - bottomVolume.left;
                }
                if (bottomLineBegin != -1) {
                    bottomLineEnd = x - bottomVolume.left;
                }
            }
            // data += bytesPerPixel;
        }

        ::Ogre::Box leftVolume = ::Ogre::Box(
                (::Ogre::uint32) ninePatch.uvLeftInPixels,//0,
                (::Ogre::uint32) ninePatch.uvTopInPixels,//0,
                (::Ogre::uint32) (ninePatch.uvLeftInPixels + 1),//1,
                (::Ogre::uint32) ninePatch.uvBottomInPixels//currentLock.getHeight()
        );
        // data = reinterpret_cast<uint8_t *>(leftVolume.data);
        for (int y = leftVolume.top; y < leftVolume.bottom; ++y) {
//            for (int x = leftVolume.left; x < leftVolume.right; ++x) {
            int prevData = *(reinterpret_cast<int*>(currentLock.at(leftVolume.left, y, 0)));
            // int prevData = *(reinterpret_cast<int*>(data));
            extractColor(mTexture, currentLock, col, prevData);

//            ::Ogre::String prevDataStr("prevDataLeftTopBottom " + SSTR(prevData));
//            LOGI("%s", prevDataStr.c_str());
            if (equalsWithoutAlpha(col, ::Ogre::ColourValue::Black) && col.a > 0.0f) {
//                    System.out.println("black found at: " + x + " " + y);
                if (leftLineBegin == -1) {
                    leftLineBegin = y - leftVolume.top;
                }
                if (leftLineBegin != -1) {
                    leftLineEnd = y - leftVolume.top;
                }
            }
//                bufferPos += leftVolume.rowPitch;
            // data += bytesPerPixel * leftVolume.rowPitch;
//            }
        }

        ::Ogre::Box rightVolume = ::Ogre::Box(
                (::Ogre::uint32) (ninePatch.uvRightInPixels - 1),//currentLock.getWidth() - 1,
                (::Ogre::uint32) ninePatch.uvTopInPixels,//0,
                (::Ogre::uint32) ninePatch.uvRightInPixels,//currentLock.getWidth(),
                (::Ogre::uint32) ninePatch.uvBottomInPixels//currentLock.getHeight()
        );
        // data = reinterpret_cast<uint8_t *>(rightVolume.data);
        for (int y = rightVolume.top; y < rightVolume.bottom; ++y) {
            int prevData = *(reinterpret_cast<int*>(currentLock.at(rightVolume.left, y, 0)));
            // int prevData = *(reinterpret_cast<int*>(data));
            extractColor(mTexture, currentLock, col, prevData);

//            ::Ogre::String prevDataStr("prevDataRightTopBottom " + SSTR(prevData));
//            LOGI("%s", prevDataStr.c_str());
            if (equalsWithoutAlpha(col, ::Ogre::ColourValue::Black) && col.a > 0.0f) {
                if (rightLineBegin == -1) {
                    rightLineBegin = y - rightVolume.top;
                }
                if (rightLineBegin != -1) {
                    rightLineEnd = y - rightVolume.top;
                }
            }
            // data += rightVolume.rowPitch * bytesPerPixel;
        }
        // buffer->unlock();

        // stagingTexture->stopMapRegion();
        // stagingTexture->upload(currentLock, mTexture, 0, 0, 0);
        // textureManager->removeStagingTexture(stagingTexture);
        // stagingTexture = 0;

        // Now we have the lines for stretchable areas and drawable areas

        if (topLineBegin == -1 || topLineEnd == -1 ||
            leftLineBegin == -1 || leftLineEnd == -1) {
            ::Ogre::String errorStr("Nine patch " + mTexture->getNameStr() +
                                               " invalid with topLineBegin: " +
                                            SSTR(topLineBegin) + " topLineEnd: " +
                                            SSTR(topLineEnd) + " leftLineBegin: " +
                                            SSTR(leftLineBegin) +
                                          " leftLineEnd: " + SSTR(leftLineEnd));
            LOGI("%s", errorStr.c_str());
            return -1;
        }

        // Get rid of pixels from margin that define the lines
        topLineBegin -= 1;
        topLineEnd -= 1;
        leftLineBegin -= 1;
        leftLineEnd -= 1;
        bottomLineBegin -= 1;
        bottomLineEnd -= 1;
        rightLineBegin -= 1;
        rightLineEnd -= 1;

        width -= 2;
        height -= 2;
        
        float inverseTextureSizeX = 1.0f / mTexture->getWidth();
        float inverseTextureSizeY = 1.0f / mTexture->getHeight();

        // THE TOP AND LEFT STRETCHABLE AREA

        // TOP LEFT
        Sprite& topLeftStretchable = ninePatch.stretchableArea[NPA_TOP_LEFT];
        topLeftStretchable.uvLeft = ninePatch.uvLeftInPixels + 1;
        topLeftStretchable.uvTop = ninePatch.uvTopInPixels + 1;
        topLeftStretchable.spriteWidth = topLineBegin;
        topLeftStretchable.spriteHeight = leftLineBegin;
        calculateSpriteCoordinates(topLeftStretchable, inverseTextureSizeX, inverseTextureSizeY);

        // TOP MIDDLE
        Sprite& topMiddleStretchable = ninePatch.stretchableArea[NPA_TOP_MIDDLE];
        topMiddleStretchable.uvLeft = ninePatch.uvLeftInPixels + topLineBegin;
        topMiddleStretchable.uvTop = ninePatch.uvTopInPixels + 1;
        topMiddleStretchable.spriteWidth = topLineEnd - topLineBegin;
        topMiddleStretchable.spriteHeight = leftLineBegin;
        calculateSpriteCoordinates(topMiddleStretchable, inverseTextureSizeX, inverseTextureSizeY);

        // TOP RIGHT
        Sprite& topRightStretchable = ninePatch.stretchableArea[NPA_TOP_RIGHT];
        topRightStretchable.uvLeft = ninePatch.uvLeftInPixels + topLineEnd;
        topRightStretchable.uvTop = ninePatch.uvTopInPixels + 1;
        topRightStretchable.spriteWidth = width - topLineEnd;
        topRightStretchable.spriteHeight = leftLineBegin;
        calculateSpriteCoordinates(topRightStretchable, inverseTextureSizeX, inverseTextureSizeY);

        // MIDDLE LEFT
        Sprite& middleLeftStretchable = ninePatch.stretchableArea[NPA_MIDDLE_LEFT];
        middleLeftStretchable.uvLeft = ninePatch.uvLeftInPixels + 1;
        middleLeftStretchable.uvTop = ninePatch.uvTopInPixels + leftLineBegin;
        middleLeftStretchable.spriteWidth = topLineBegin;
        middleLeftStretchable.spriteHeight = leftLineEnd - leftLineBegin;
        calculateSpriteCoordinates(middleLeftStretchable, inverseTextureSizeX, inverseTextureSizeY);

        // MIDDLE MIDDLE
        Sprite& middleMiddleStretchable = ninePatch.stretchableArea[NPA_MIDDLE_MIDDLE];
        middleMiddleStretchable.uvLeft = ninePatch.uvLeftInPixels + topLineBegin;
        middleMiddleStretchable.uvTop = ninePatch.uvTopInPixels + leftLineBegin;
        middleMiddleStretchable.spriteWidth = topLineEnd - topLineBegin;
        middleMiddleStretchable.spriteHeight = leftLineEnd - leftLineBegin;
        calculateSpriteCoordinates(middleMiddleStretchable, inverseTextureSizeX, inverseTextureSizeY);

        // MIDDLE RIGHT
        Sprite& middleRightStretchable = ninePatch.stretchableArea[NPA_MIDDLE_RIGHT];
        middleRightStretchable.uvLeft = ninePatch.uvLeftInPixels + topLineEnd;
        middleRightStretchable.uvTop = ninePatch.uvTopInPixels + leftLineBegin;
        middleRightStretchable.spriteWidth = width - topLineEnd;
        middleRightStretchable.spriteHeight = leftLineEnd - leftLineBegin;
        calculateSpriteCoordinates(middleRightStretchable, inverseTextureSizeX, inverseTextureSizeY);

        // BOTTOM LEFT
        Sprite& bottomLeftStretchable = ninePatch.stretchableArea[NPA_BOTTOM_LEFT];
        bottomLeftStretchable.uvLeft = ninePatch.uvLeftInPixels + 1;
        bottomLeftStretchable.uvTop = ninePatch.uvTopInPixels + leftLineEnd;
        bottomLeftStretchable.spriteWidth = topLineBegin;
        bottomLeftStretchable.spriteHeight = height - leftLineEnd;
        calculateSpriteCoordinates(bottomLeftStretchable, inverseTextureSizeX, inverseTextureSizeY);

        // BOTTOM MIDDLE
        Sprite& bottomMiddleStretchable = ninePatch.stretchableArea[NPA_BOTTOM_MIDDLE];
        bottomMiddleStretchable.uvLeft = ninePatch.uvLeftInPixels + topLineBegin;
        bottomMiddleStretchable.uvTop = ninePatch.uvTopInPixels + leftLineEnd;
        bottomMiddleStretchable.spriteWidth = topLineEnd - topLineBegin;
        bottomMiddleStretchable.spriteHeight = height - leftLineEnd;
        calculateSpriteCoordinates(bottomMiddleStretchable, inverseTextureSizeX, inverseTextureSizeY);

        // BOTTOM RIGHT
        Sprite& bottomRightStretchable = ninePatch.stretchableArea[NPA_BOTTOM_RIGHT];
        bottomRightStretchable.uvLeft = ninePatch.uvLeftInPixels + topLineEnd;
        bottomRightStretchable.uvTop = ninePatch.uvTopInPixels + leftLineEnd;
        bottomRightStretchable.spriteWidth = width - topLineEnd;
        bottomRightStretchable.spriteHeight = height - leftLineEnd;
        calculateSpriteCoordinates(bottomRightStretchable, inverseTextureSizeX, inverseTextureSizeY);

        // RIGHT AND BOTTOM DRAWABLE AREA

        if (bottomLineBegin == -1 || bottomLineEnd == -1 ||
            rightLineBegin == -1 || rightLineEnd == -1) {
            ::Ogre::String str("No drawable area specified for Nine Patch " + mTexture->getNameStr() +
                             " with bottomLineBegin: " + SSTR(bottomLineBegin) + " bottomLineEnd : " +
                             SSTR(bottomLineEnd) + " rightLineBegin: " + SSTR(rightLineBegin) +
                             " rightLineEnd: " + SSTR(rightLineEnd));
            LOGI("%s", str.c_str());
            return -1;
        }

        // TOP LEFT
        Sprite& topLeftDrawable = ninePatch.drawableArea[NPA_TOP_LEFT];
        topLeftDrawable.uvLeft = ninePatch.uvLeftInPixels + 1;
        topLeftDrawable.uvTop = ninePatch.uvTopInPixels + 1;
        topLeftDrawable.spriteWidth = bottomLineBegin;
        topLeftDrawable.spriteHeight = rightLineBegin;
        calculateSpriteCoordinates(topLeftDrawable, inverseTextureSizeX, inverseTextureSizeY);

        // TOP MIDDLE
        Sprite& topMiddleDrawable = ninePatch.drawableArea[NPA_TOP_MIDDLE];
        topMiddleDrawable.uvLeft = ninePatch.uvLeftInPixels + bottomLineBegin;
        topMiddleDrawable.uvTop = ninePatch.uvTopInPixels + 1;
        topMiddleDrawable.spriteWidth = bottomLineEnd - bottomLineBegin;
        topMiddleDrawable.spriteHeight = rightLineBegin;
        calculateSpriteCoordinates(topMiddleDrawable, inverseTextureSizeX, inverseTextureSizeY);

        // TOP RIGHT
        Sprite& topRightDrawable = ninePatch.drawableArea[NPA_TOP_RIGHT];
        topRightDrawable.uvLeft = ninePatch.uvLeftInPixels + bottomLineEnd;
        topRightDrawable.uvTop = ninePatch.uvTopInPixels + 1;
        topRightDrawable.spriteWidth = width - bottomLineEnd;
        topRightDrawable.spriteHeight = rightLineBegin;
        calculateSpriteCoordinates(topRightDrawable, inverseTextureSizeX, inverseTextureSizeY);

        // MIDDLE LEFT
        Sprite& middleLeftDrawable = ninePatch.drawableArea[NPA_MIDDLE_LEFT];
        middleLeftDrawable.uvLeft = ninePatch.uvLeftInPixels + 1;
        middleLeftDrawable.uvTop = ninePatch.uvTopInPixels + rightLineBegin;
        middleLeftDrawable.spriteWidth = bottomLineBegin;
        middleLeftDrawable.spriteHeight = rightLineEnd - rightLineBegin;
        calculateSpriteCoordinates(middleLeftDrawable, inverseTextureSizeX, inverseTextureSizeY);

        // MIDDLE MIDDLE
        Sprite& middleMiddleDrawable = ninePatch.drawableArea[NPA_MIDDLE_MIDDLE];
        middleMiddleDrawable.uvLeft = ninePatch.uvLeftInPixels + bottomLineBegin;
        middleMiddleDrawable.uvTop = ninePatch.uvTopInPixels + rightLineBegin;
        middleMiddleDrawable.spriteWidth = bottomLineEnd - bottomLineBegin;
        middleMiddleDrawable.spriteHeight = rightLineEnd - rightLineBegin;
        calculateSpriteCoordinates(middleMiddleDrawable, inverseTextureSizeX, inverseTextureSizeY);

        // MIDDLE RIGHT
        Sprite& middleRightDrawable = ninePatch.drawableArea[NPA_MIDDLE_RIGHT];
        middleRightDrawable.uvLeft = ninePatch.uvLeftInPixels + bottomLineEnd;
        middleRightDrawable.uvTop = ninePatch.uvTopInPixels + rightLineBegin;
        middleRightDrawable.spriteWidth = width - bottomLineEnd;
        middleRightDrawable.spriteHeight = rightLineEnd - rightLineBegin;
        calculateSpriteCoordinates(middleRightDrawable, inverseTextureSizeX, inverseTextureSizeY);

        // BOTTOM LEFT
        Sprite& bottomLeftDrawable = ninePatch.drawableArea[NPA_BOTTOM_LEFT];
        bottomLeftDrawable.uvLeft = ninePatch.uvLeftInPixels + 1;
        bottomLeftDrawable.uvTop = ninePatch.uvTopInPixels + rightLineEnd;
        bottomLeftDrawable.spriteWidth = bottomLineBegin;
        bottomLeftDrawable.spriteHeight = height - rightLineEnd;
        calculateSpriteCoordinates(bottomLeftDrawable, inverseTextureSizeX, inverseTextureSizeY);

        // BOTTOM MIDDLE
        Sprite& bottomMiddleDrawable = ninePatch.drawableArea[NPA_BOTTOM_MIDDLE];
        bottomMiddleDrawable.uvLeft = ninePatch.uvLeftInPixels + bottomLineBegin;
        bottomMiddleDrawable.uvTop = ninePatch.uvTopInPixels + rightLineEnd;
        bottomMiddleDrawable.spriteWidth = bottomLineEnd - bottomLineBegin;
        bottomMiddleDrawable.spriteHeight = height - rightLineEnd;
        calculateSpriteCoordinates(bottomMiddleDrawable, inverseTextureSizeX, inverseTextureSizeY);

        // BOTTOM RIGHT
        Sprite& bottomRightDrawable = ninePatch.drawableArea[NPA_BOTTOM_RIGHT];
        bottomRightDrawable.uvLeft = ninePatch.uvLeftInPixels + bottomLineEnd;
        bottomRightDrawable.uvTop = ninePatch.uvTopInPixels + rightLineEnd;
        bottomRightDrawable.spriteWidth = width - bottomLineEnd;
        bottomRightDrawable.spriteHeight = height - rightLineEnd;
        calculateSpriteCoordinates(bottomRightDrawable, inverseTextureSizeX, inverseTextureSizeY);

        NinePatch::writeNinePatch(env, np, ninePatch);
		return 0;
    }

    void GorillaGUI::extractColor(::Ogre::TextureGpu* texture, const ::Ogre::TextureBox& currentLock, ::Ogre::ColourValue& col,
                                  int prevData) {

        switch (texture->getPixelFormat()) {
        case ::Ogre::PixelFormatGpu::PFG_RGBA8_UNORM_SRGB:
#if O32_HOST_ORDER == O32_LITTLE_ENDIAN
                col.setAsABGR(prevData);
#else
                col.setAsRGBA(prevData);
#endif
                break;
/*            case ::Ogre::PixelFormat::PF_R8G8B8A8:
#if O32_HOST_ORDER == O32_LITTLE_ENDIAN
                col.setAsRGBA(prevData);
#else
                col.setAsABGR(prevData);
#endif
                break;
			case ::Ogre::PixelFormat::PF_A8R8G8B8:
#if O32_HOST_ORDER == O32_LITTLE_ENDIAN
				col.setAsARGB(prevData);
#else
				col.setAsBGRA(prevData);
#endif
				break;
			case ::Ogre::PixelFormat::PF_B8G8R8A8:
#if O32_HOST_ORDER == O32_LITTLE_ENDIAN
				col.setAsBGRA(prevData);
#else
				col.setAsARGB(prevData);
#endif
				break;*/
//                if (ENG_Utility.getEndianness() == ENG_Utility.Endianness.LITTLE_ENDIAN) {
//            col.setAsBGRA(prevData);
//        } else {
//            col.setAsARGB(prevData);
//        }
//
//                break;
//            case ::Ogre::PixelFormat::PF_R8G8B8A8:
//                if (ENG_Utility.getEndianness() == ENG_Utility.Endianness.LITTLE_ENDIAN) {
//            col.setAsABGR(prevData);
//        } else {
//            col.setAsRGBA(prevData);
//        }
//
//                break;
//            default:
//                throw new ENG_UnsupportedPixelFormatException(
//                        "Only A8R8G8B8 and R8G8B8A8");
        }
    }

    void GorillaGUI::calculateSpriteCoordinates(Sprite &s, 
                                                float mInverseTextureSizeX, float mInverseTextureSizeY) 
    {
        s.uvRight = s.uvLeft + s.spriteWidth;
        s.uvBottom = s.uvTop + s.spriteHeight;

        s.uvLeft *= mInverseTextureSizeX;
        s.uvTop *= mInverseTextureSizeY;
        s.uvRight *= mInverseTextureSizeX;
        s.uvBottom *= mInverseTextureSizeY;

        s.texCoords[TopLeft].x = s.uvLeft;
        s.texCoords[TopLeft].y = s.uvTop;
        s.texCoords[TopRight].x = s.uvRight;
        s.texCoords[TopRight].y = s.uvTop;
        s.texCoords[BottomRight].x = s.uvRight;
        s.texCoords[BottomRight].y = s.uvBottom;
        s.texCoords[BottomLeft].x = s.uvLeft;
        s.texCoords[BottomLeft].y = s.uvBottom;
    }

    bool GorillaGUI::equalsWithoutAlpha(const ::Ogre::ColourValue &val1,
                                        const ::Ogre::ColourValue &val2)
    {
        return val1.r == val2.r && val1.g == val2.g && val1.b == val2.b;
    }

    jobjectArray GorillaGUI::createByteBuffers(JNIEnv *env, int size, int num)
    {
        bufArr = (void **) malloc(sizeof(void *) * num);
        jclass byteBufferCls = env->FindClass("java/nio/ByteBuffer");
        jobjectArray byteBufferArr = env->NewObjectArray(num, byteBufferCls, NULL);
        bufferSize = size;
        for (int i = 0; i < num; ++i)
        {
            void* buf = malloc(size);
            jobject byteBuffer = env->NewDirectByteBuffer(buf, size);
            env->SetObjectArrayElement(byteBufferArr, i, byteBuffer);
            bufArr[i] = buf;
        }
        return byteBufferArr;
    }

    void GorillaGUI::createDummyNode()
    {
        // new for NodeMemoryManager and OGRE_NEW for SceneNode is intentional. The same as in Ogre 2.1.
        mNodeMemoryManager = new ::Ogre::NodeMemoryManager();
        mDummyNode = OGRE_NEW ::Ogre::SceneNode( 0, 0, mNodeMemoryManager, 0 );
        mDummyNode->_getFullTransformUpdated();
    }

    void GorillaGUI::destroyDummyNode()
    {
        OGRE_DELETE mDummyNode;
        delete mNodeMemoryManager;
        mDummyNode = 0;
        mNodeMemoryManager = 0;
    }

    ::Ogre::SceneNode *GorillaGUI::getDummyNode()
    {
        return mDummyNode;
    }

//    ScreenRenderable *GorillaGUI::createScreenRenderable(::Ogre::uint8 queueGroupId)
//    {
//        ::Ogre::SceneManager *sceneManager = ::Ogre::Root::getSingleton()._getCurrentSceneManager();
//        ::Ogre::ObjectMemoryManager &objectMemoryManager = sceneManager->_getEntityMemoryManager(::Ogre::SCENE_DYNAMIC);
//        ScreenRenderable *screenRenderable = new ScreenRenderable(
//                ::Ogre::Id::generateNewId<ScreenRenderable>(),
//                &objectMemoryManager, sceneManager, queueGroupId);
//        screenRenderableMap.insert(std::make_pair(queueGroupId, screenRenderable));
//        return screenRenderable;
//    }
//
//    void GorillaGUI::destroyScreenRenderable(ScreenRenderable *screenRenderable)
//    {
//        unsigned int erasedCount = screenRenderableMap.erase(screenRenderable->getQueueGroupId());
//        delete screenRenderable;
//    }

//    void GorillaGUI::createAtlasPtrList(int size)
//    {
//        atlasPtrList = (::Ogre::Texture **) malloc(sizeof(::Ogre::Texture*) * size);
//        atlasPtrListSize = size;
//    }
//
//    void GorillaGUI::destroyAtlasPtrList()
//    {
//        free(atlasPtrList);
//    }
//
//    void GorillaGUI::setAtlasPtr(::Ogre::Texture *texture, int pos)
//    {
//        atlasPtrList[pos] = texture;
//    }

//    void GorillaGUI::renderOnce()
//    {
//        std::map<unsigned char, Hotshot::ScreenRenderable *>::iterator it = screenRenderableMap.begin();
//        const std::map<unsigned char, Hotshot::ScreenRenderable *>::iterator &endIterator = screenRenderableMap.end();
//        while (it != endIterator)
//        {
//            ScreenRenderable *screenRenderable = it->second;
//            ++it;
//        }
//    }


}

