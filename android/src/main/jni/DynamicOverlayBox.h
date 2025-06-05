//
// Created by sebas on 29-Aug-17.
//

#ifndef BLACKHOLEDARKSUNONLINE5_DYNAMICOVERLAYBOX_H
#define BLACKHOLEDARKSUNONLINE5_DYNAMICOVERLAYBOX_H

#include "OgreCommon.h"
#include "OgreColourValue.h"
#include <vector>

namespace Hotshot {

    class DynamicOverlayBox
    {
    public:
        ::Ogre::Box rect;
        std::vector<::Ogre::ColourValue> color;

        DynamicOverlayBox() {}
        DynamicOverlayBox(int x, int y, int xLen, int yLen, ::Ogre::ColourValue col);

        bool operator==(const DynamicOverlayBox& b);
        bool operator!=(const DynamicOverlayBox& b);
    };
}

#endif //BLACKHOLEDARKSUNONLINE5_DYNAMICOVERLAYBOX_H
