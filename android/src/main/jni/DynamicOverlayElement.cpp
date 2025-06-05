//
// Created by sebas on 29-Aug-17.
//

#include "DynamicOverlayElement.h"
#include "OgreCommon.h"
#include "OgreOverlayElement.h"
//#include "OgrePixelBox.h"
// #include "OgreHardwarePixelBuffer.h"
#include "OgreStagingTexture.h"
#include "OgreColourValue.h"
#include "OgreHlmsUnlitDatablock.h"
#include "HotshotCommon.h"
#include "DynamicOverlayBox.h"
#include "OgreTextureGpuManager.h"
//#include "OgrePixelFormat.h"
//#include "order32.h"
#include <algorithm>
#include <OgreRoot.h>

#include "JniCommon.h"

Hotshot::DynamicOverlayElement::DynamicOverlayElement(Ogre::v1::OverlayElement *_elem, std::string textureName, std::string groupName) : elem(_elem) {
    datablock = (Ogre::HlmsUnlitDatablock*) elem->getDatablock();
    // Hack in order to get around the fact that by default the texture is loaded as a texture array. We need just a 2D texture in this datablock.
    // With a texture array we cannot write pixels in the buffer, for now at least.
    Ogre::TextureGpuManager* textureManager = Ogre::Root::getSingleton().getRenderSystem()->getTextureGpuManager();
    Ogre::TextureGpu *ptr = textureManager->createOrRetrieveTexture(
        textureName, Ogre::GpuPageOutStrategy::AlwaysKeepSystemRamCopy, Ogre::CommonTextureTypes::Diffuse, groupName);
    ptr->scheduleTransitionTo(Ogre::GpuResidency::Resident);
    ptr->waitForData();
    datablock->setTexture(0, ptr);

    image = OGRE_NEW Ogre::Image2();
    image->convertFromTexture(ptr, 0, 0);
    imageBox = image->getData(0);
}

Hotshot::DynamicOverlayElement::~DynamicOverlayElement()
{
    if (image)
        OGRE_DELETE image;
}

void Hotshot::DynamicOverlayElement::updateTexture(std::vector<DynamicOverlayBox>& elements/*, bool t*/)
{
    // Copy all the data from original image.
    memcpy(currentBuffer.data, imageBox.data, currentBuffer.getSizeBytes());
    // No need to lock anymore since there is only one lock and one unlock for updating the texture,
    // for resetting the texture and for setting areas of colour.
    //lock();
    std::vector<Hotshot::DynamicOverlayBox>::iterator it = elements.begin();
    const std::vector<Hotshot::DynamicOverlayBox>::iterator &end = elements.end();
    int colorPos;

    while (it != end)
    {
        DynamicOverlayBox &elem = *it;
        // const Ogre::PixelBox &volume = currentLock->getSubVolume(elem.rect);
        // int* data = (int *)volume.data;
        colorPos = 0;
        for (int y = elem.rect.top; y < elem.rect.bottom; ++y)
        {
            for (int x = elem.rect.left; x < elem.rect.right; ++x)
            {
//                Ogre::String s("colorPos  " + SSTR(colorPos) + "\n");
//                LOGI("%s", s.c_str());

                Ogre::ColourValue &newCol = elem.color[colorPos++];
                int newData;
                switch (stagingTexture->getFormatFamily()) {
                case Ogre::PixelFormatGpu::PFG_RGBA8_UNORM:
#if O32_HOST_ORDER == O32_LITTLE_ENDIAN                    
                    newData = newCol.getAsABGR();
#else
                    newData = newCol.getAsRGBA();
#endif

                    break;
//                 case Ogre::PF_R8G8B8A8:
// #if O32_HOST_ORDER == O32_LITTLE_ENDIAN
//                     newData = newCol.getAsRGBA();
// #else
//                     newData = newCol.getAsABGR();
// #endif
//
//                     break;
//                     case Ogre::PF_A8B8G8R8:
// #if O32_HOST_ORDER == O32_LITTLE_ENDIAN
// //                        if (t) {
// //                            newCol.a = 1.0f;
// //                            newCol.r = 1.0f;
// //                            newCol.g = 1.0f;
// //                            newCol.b = 1.0f;
// //                        }
//                         newData = newCol.getAsABGR();
// #else
//                         newData = newCol.getAsRGBA();
// #endif
//                         break;
                 default:
                     LOGI("%s", "Only A8R8G8B8 and R8G8B8A8");
                     exit(-1);
                 }
                    Ogre::int32* pixelData = reinterpret_cast<Ogre::int32*>(currentBuffer.at(x, y, 0));
                    *pixelData = newData;
                 // *data++ = newData;
             }
//             data += currentLock->rowPitch - volume.right;
        }
        ++it;
    }
    //unlock();
    elements.clear();
}


void Hotshot::DynamicOverlayElement::resetToInitialTexture() 
{
    updateTexture(changedElements/*, false*/);
}

void Hotshot::DynamicOverlayElement::updateFinalTexture()
{
    updateTexture(elementsToUpdate/*, false*/);
}


void Hotshot::DynamicOverlayElement::setPointScreenSpace(float x, float y, int pixelLen,
                                                         Ogre::ColourValue &val,
                                                         bool overwriteTransparentPixels) 
{
    int xPos = (int) (x * currentBuffer.width);
    int yPos = (int) (y * currentBuffer.height);

    setPoint(xPos, yPos, pixelLen, val, overwriteTransparentPixels);
}

void Hotshot::DynamicOverlayElement::lock() {
    JNIEnv *env = getThreadEnv();

    
//     const Ogre::v1::HardwarePixelBufferSharedPtr &hardwarePixelBuffer = texture->getBuffer();
//     currentBuffer = hardwarePixelBuffer.getPointer();
// //    long long renderOneFrameBeginTime = getNanoTime(env);
//     const Ogre::PixelBox &box = currentBuffer->lock(Ogre::Image::Box(0, 0, currentBuffer->getWidth(),
//         currentBuffer->getHeight()), Ogre::v1::HardwarePixelBuffer::LockOptions::HBL_NORMAL);
//     currentLock = (Ogre::PixelBox *) &box;

    Ogre::TextureGpuManager* textureManager = Ogre::Root::getSingleton().getRenderSystem()->getTextureGpuManager();
    const Ogre::TextureGpu* texture = datablock->getTexture(0);
    stagingTexture = textureManager->getStagingTexture(texture->getWidth(), texture->getHeight(), 1u, 1u, Ogre::PFG_RGBA8_SNORM);
    stagingTexture->startMapRegion();
    currentBuffer = stagingTexture->mapRegion(texture->getWidth(), texture->getHeight(), 1u, 1u, Ogre::PFG_RGBA8_SNORM);

//    long long renderOneFrameEndTime = getNanoTime(env);
//    long long timeDiff = renderOneFrameEndTime - renderOneFrameBeginTime;
//    timeDiff /= 1000000;
//    Ogre::String s("lock DynamicOverlayElement time:  " + SSTR(timeDiff) + "\n");
//    LOGI("%s", s.c_str());
}

void Hotshot::DynamicOverlayElement::unlock() {
    JNIEnv *env = getThreadEnv();
//    long long renderOneFrameBeginTime = getNanoTime(env);
    // currentBuffer->unlock();
    // currentLock = NULL;
//    lockPtr = NULL;

    Ogre::TextureGpuManager* textureManager = Ogre::Root::getSingleton().getRenderSystem()->getTextureGpuManager();
    Ogre::TextureGpu* texture = datablock->getTexture(0);
    stagingTexture->stopMapRegion();
    stagingTexture->upload(currentBuffer, texture, 0, 0, 0);
    textureManager->removeStagingTexture(stagingTexture);
    stagingTexture = 0;

//    long long renderOneFrameEndTime = getNanoTime(env);
//    long long timeDiff = renderOneFrameEndTime - renderOneFrameBeginTime;
//    timeDiff /= 1000000;
//    Ogre::String s("unlock DynamicOverlayElement time:  " + SSTR(timeDiff) + "\n");
//    LOGI("%s", s.c_str());
}

void Hotshot::DynamicOverlayElement::setArea(Ogre::Box &rect,
                                             Ogre::ColourValue &val,
                                             bool overwriteTransparentPixels) {
    int len = rect.getWidth() * rect.getHeight();
    std::vector<Ogre::ColourValue> list(len);
    
    for (int i = 0; i < len; ++i) {
        list.push_back(val);
    }

    setArea(rect, list, overwriteTransparentPixels);
}

void Hotshot::DynamicOverlayElement::setArea(Ogre::Box &elem,
                                             std::vector<Ogre::ColourValue> &val,
                                             bool overwriteTransparentPixels) {

    const DynamicOverlayBox &box = setAreaToColor(elem, val, overwriteTransparentPixels);
    changedElements.push_back(box);
}

Hotshot::DynamicOverlayBox Hotshot::DynamicOverlayElement::setAreaToColor(Ogre::Box &rect,
                                                                          std::vector<Ogre::ColourValue> &val,
                                                                          bool overwriteTransparentPixels) {
    if (rect.getWidth() * rect.getHeight() != val.size())
    {
        ::Ogre::String errorStr("rect width: " + SSTR(rect.getWidth()) +
                                " height: " + SSTR(rect.getHeight()) +
                                " different from colour size: " + SSTR(val.size()));
        LOGI("%s", errorStr.c_str());
        exit(-1);
    }
    rect.left = std::max(rect.left, currentBuffer.x);
    rect.top = std::max(rect.top, currentBuffer.y);
    rect.front = std::max(rect.front, currentBuffer.z);
    rect.right = std::min(rect.right, currentBuffer.getMaxX());
    rect.bottom = std::min(rect.bottom, currentBuffer.getMaxY());
    rect.back = std::min(rect.back, currentBuffer.getMaxZ());
    // const Ogre::PixelBox &volume = currentBuffer->getSubVolume(rect);
    size_t bytesPerPixel = currentBuffer.bytesPerPixel;
    Hotshot::DynamicOverlayBox oldElement;
    Hotshot::DynamicOverlayBox newElement;
    oldElement.rect = rect;
    newElement.rect = rect;
    int colorPos = 0;
    // int* data = (int *) volume.data;
    for (int y = rect.top; y < rect.bottom; ++y)
    {
        for (int x = rect.left; x < rect.right; ++x) {
            Ogre::int32* pixelData = reinterpret_cast<Ogre::int32*>(currentBuffer.at(x, y, 0));
            Ogre::ColourValue oldCol;
            int prevData = *pixelData;
            switch (stagingTexture->getFormatFamily()) {
            case Ogre::PixelFormatGpu::PFG_RGBA8_UNORM:
#if O32_HOST_ORDER == O32_LITTLE_ENDIAN
                    oldCol.setAsABGR(prevData);                    
#else
                    oldCol.setAsRGBA(prevData);
#endif

                    break;
/*                case Ogre::PF_R8G8B8A8:
#if O32_HOST_ORDER == O32_LITTLE_ENDIAN
                    oldCol.setAsRGBA(prevData);                    
#else
                    oldCol.setAsABGR(prevData);
#endif

                    break;
                case Ogre::PF_A8B8G8R8:
#if O32_HOST_ORDER == O32_LITTLE_ENDIAN
                    oldCol.setAsABGR(prevData);
#else
                    oldCol.setAsRGBA(prevData);
#endif
                    break;*/
                default:
                    LOGI("%s", "Only A8R8G8B8 and R8G8B8A8");
                    exit(-1);
            }
            // Add it anyway for corner cases when even though we don't
            // overwrite we still need the value for when we reset the texture
            oldElement.color.push_back(oldCol);
            
            if (!overwriteTransparentPixels && oldCol.a == 0.0f) {
                // Don't forget to add the offset!!!!!!
                newElement.color.push_back(oldCol);
                // ++data;
                continue;
            }
            Ogre::ColourValue &newCol = val[colorPos++];
            newElement.color.push_back(newCol);
            
//            int newData;
//            switch (currentLock->format) {
//                case Ogre::PF_A8R8G8B8:
//#if O32_HOST_ORDER == O32_LITTLE_ENDIAN                    
//                    newData = newCol.getAsARGB();
//#else
//                    newData = newCol.getAsBGRA();
//#endif
//
//                    break;
//                case Ogre::PF_R8G8B8A8:
//#if O32_HOST_ORDER == O32_LITTLE_ENDIAN
//                    newData = newCol.getAsRGBA();                  
//#else
//                    newData = newCol.getAsABGR();
//#endif
//
//                    break;
//                default:
//                    LOGI("%s", "Only A8R8G8B8 and R8G8B8A8");
//                    exit(-1);
//            }
//            *data++ = newData;
            // ++data;
            
            
        }
        // data += currentLock->rowPitch - volume.right;

    }
    elementsToUpdate.push_back(newElement);
    return oldElement;
}

void Hotshot::DynamicOverlayElement::setPoint(int x, int y, int pixelLen,
                                              Ogre::ColourValue &val,
                                              bool overwriteTransparentPixels) {
    DynamicOverlayBox elem(x, y, pixelLen, pixelLen, val);
    setArea(elem.rect, elem.color, false);
}
