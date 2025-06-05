//
// Created by sebas on 29-Aug-17.
//

#ifndef BLACKHOLEDARKSUNONLINE5_DYNAMICOVERLAYELEMENT_H
#define BLACKHOLEDARKSUNONLINE5_DYNAMICOVERLAYELEMENT_H

#include <vector>
#include <OgreColourValue.h>
#include "DynamicOverlayBox.h"
#include "OgreTextureBox.h"

namespace Ogre {
    struct Box;
    class PixelBox;
    class ColourValue;
    class HlmsUnlitDatablock;
    class StagingTexture;
    class Image2;
    namespace v1 {
        class OverlayElement;
        class HardwarePixelBuffer;
    }
}

namespace Hotshot {




    class DynamicOverlayElement
    {
    private:
        std::vector<DynamicOverlayBox> changedElements;
        std::vector<DynamicOverlayBox> elementsToUpdate;
        ::Ogre::v1::OverlayElement *elem;
        ::Ogre::HlmsUnlitDatablock *datablock;
        ::Ogre::TextureBox currentBuffer;
//        void *lockPtr;
        // ::Ogre::PixelBox *currentLock;
        ::Ogre::StagingTexture* stagingTexture;
        ::Ogre::Image2 *image;
        ::Ogre::TextureBox imageBox;

        DynamicOverlayBox setAreaToColor(::Ogre::Box &rect,
                                      std::vector<::Ogre::ColourValue> &val, bool overwriteTransparentPixels);
        void setPoint(int x, int y, int pixelLen, ::Ogre::ColourValue &val,
                      bool overwriteTransparentPixels);
        void updateTexture(std::vector<DynamicOverlayBox>& elements/*, bool t*/);
    public:
        DynamicOverlayElement(::Ogre::v1::OverlayElement *_elem, std::string textureName, std::string groupName);

        ~DynamicOverlayElement();

        void resetToInitialTexture();
        void updateFinalTexture();

        // Already in screen space from the java side.
        void setPointScreenSpace(
                float x, float y, int pixelLen,
                ::Ogre::ColourValue &val, bool overwriteTransparentPixels);

        void lock();
        void unlock();

        void setArea(::Ogre::Box &elem, std::vector<::Ogre::ColourValue> &val, bool overwriteTransparentPixels);
        void setArea(::Ogre::Box &elem, ::Ogre::ColourValue &val, bool overwriteTransparentPixels);
    };
}

#endif //BLACKHOLEDARKSUNONLINE5_DYNAMICOVERLAYELEMENT_H
