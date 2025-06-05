//
// Created by sebas on 09-Nov-17.
//

#ifndef BLACKHOLEDARKSUNONLINE5_HOTSHOTTILEDANIMATIONFRAMEENDLISTENER_H
#define BLACKHOLEDARKSUNONLINE5_HOTSHOTTILEDANIMATIONFRAMEENDLISTENER_H

#include <OgreTextureAnimationController.h>

namespace Hotshot {

    class TiledAnimation;

    class TiledAnimationFrameEndListener : public ::Ogre::EndFrameListener {
    private:
        TiledAnimation &mTiledAnimation;
    public:
        TiledAnimationFrameEndListener(TiledAnimation &tiledAnimation);

        void animationReachedLastFrame(::Ogre::TextureAnimationControllerValue &controllerValue);
    };

}

#endif //BLACKHOLEDARKSUNONLINE5_HOTSHOTTILEDANIMATIONFRAMEENDLISTENER_H
