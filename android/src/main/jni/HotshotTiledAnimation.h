//
// Created by sebas on 09-Oct-17.
//

#ifndef BLACKHOLEDARKSUNONLINE5_HOTSHOTTILEDANIMATION_H
#define BLACKHOLEDARKSUNONLINE5_HOTSHOTTILEDANIMATION_H

#include "HotshotCommon.h"

#if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_ANDROID)
#define OGRE_STATIC_LIB
#endif

#include <OgreCommon.h>
#include <OgreSharedPtr.h>
#include <OgreTextureAnimationController.h>
#include "HotshotTiledAnimationFrameEndListener.h"

namespace Ogre
{
//    class String;
    class HlmsUnlitDatablock;
    template <typename T> class Controller;
    namespace v1
    {
        class BillboardSet;
    }
}

namespace Hotshot
{

    class TiledAnimation
    {
    private:
        ::Ogre::HlmsUnlitDatablock* mDatablock;
        ::Ogre::Controller<::Ogre::Real>* mController;
        ::Ogre::TextureAnimationControllerValue* mCtrlVal;
        ::Ogre::SharedPtr< ::Ogre::ControllerValue<::Ogre::Real> > mVal;
        ::Ogre::SharedPtr< ::Ogre::ControllerFunction<::Ogre::Real> > mFunc;
        ::Ogre::v1::BillboardSet &mBillboardSet;
        TiledAnimationFrameEndListener mTiledAnimationFrameEndListener;
    public:
        TiledAnimation(::Ogre::v1::BillboardSet& billboardSet, ::Ogre::String& name, ::Ogre::String& unlitMaterialName,
                       float speed, int numHorizontalFrames, int numVerticalFrames);
        ~TiledAnimation();

        void disableAnimation() const;

        int getCurrentFrameNum()
        {
            return mCtrlVal->getLastFrame();
        }
    };
}

#endif //BLACKHOLEDARKSUNONLINE5_HOTSHOTTILEDANIMATION_H
