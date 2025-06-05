//
// Created by sebas on 09-Nov-17.
//

#include "HotshotTiledAnimationFrameEndListener.h"
#include "HotshotTiledAnimation.h"

namespace Hotshot
{
    TiledAnimationFrameEndListener::TiledAnimationFrameEndListener(TiledAnimation &tiledAnimation) : mTiledAnimation(tiledAnimation) {

    }

    void TiledAnimationFrameEndListener::animationReachedLastFrame(::Ogre::TextureAnimationControllerValue &controllerValue) {
        mTiledAnimation.disableAnimation();
    }
}
