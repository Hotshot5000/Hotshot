//
// Created by sebas on 09-Oct-17.
//

#include "HotshotTiledAnimation.h"
#include <OgreBillboardSet.h>
#include <OgreString.h>
#include <OgreRoot.h>
#include <OgreHlmsUnlitDatablock.h>
#include <OgreHlms.h>
#include <OgreHlmsManager.h>
#include <OgreTextureAnimationController.h>
#include <OgrePredefinedControllers.h> // There already is an TexCoordModifierControllerValue defined here. Don't use this one.
#include <OgreControllerManager.h>
#include "HotshotCommon.h"

namespace Hotshot
{

    TiledAnimation::TiledAnimation(::Ogre::v1::BillboardSet &billboardSet,
                                            ::Ogre::String &name, ::Ogre::String &unlitMaterialName,
                                   float speed, int numHorizontalFrames, int numVerticalFrames) : 
    mBillboardSet(billboardSet),
    mTiledAnimationFrameEndListener(*this)
    {
        ::Ogre::HlmsManager *hlmsManager = ::Ogre::Root::getSingleton().getHlmsManager();
        ::Ogre::Hlms *hlms = hlmsManager->getHlms( ::Ogre::HLMS_UNLIT );
        ::Ogre::HlmsUnlitDatablock *datablock = (::Ogre::HlmsUnlitDatablock*) hlms->getDatablock(unlitMaterialName);
        
        mDatablock = (::Ogre::HlmsUnlitDatablock*) datablock->clone(name);
//        const Ogre::HlmsSamplerblock* hlms_samplerblock = mDatablock->getSamplerblock(0);
//        if (hlms_samplerblock)
//        {
//            ::Ogre::String s("samplerBlock refCount: " + SSTR(hlms_samplerblock->mRefCount) + "\n");
//            LOGI("%s", s.c_str());
//        }
//        else
//        {
//            LOGI("samplerblock not defined");
//        }
        Ogre::HlmsSamplerblock samplerblock;
        samplerblock.setAddressingMode(Ogre::TAM_WRAP);
        mDatablock->setSamplerblock(0, samplerblock);

        mDatablock->setEnableAnimationMatrix(0, true);

        mVal.bind(OGRE_NEW Ogre::TextureAnimationControllerValue(mDatablock, 0));
        mCtrlVal = static_cast<Ogre::TextureAnimationControllerValue*>(mVal.get());

        mFunc.bind(OGRE_NEW Ogre::ScaleControllerFunction(speed, true));
        mController = Ogre::ControllerManager::getSingleton().createController(Ogre::ControllerManager::getSingleton().getFrameTimeSource(), mVal, mFunc);

        mCtrlVal->tiledAnimation(numHorizontalFrames, numVerticalFrames);

        billboardSet.setDatablockOrMaterialName(name/*unlitMaterialName*/, Ogre::BLANKSTRING);
        mCtrlVal->addEndFrameListener(&mTiledAnimationFrameEndListener);
    }

    TiledAnimation::~TiledAnimation() {
        if (mController)
        {
            Ogre::ControllerManager::getSingleton().destroyController(mController);
            mController = 0;
        }
        if (mDatablock)
        {
            ::Ogre::HlmsManager *hlmsManager = ::Ogre::Root::getSingleton().getHlmsManager();
            ::Ogre::Hlms *hlms = hlmsManager->getHlms(::Ogre::HLMS_UNLIT);
            hlms->destroyDatablock(mDatablock->getName());
            mDatablock = 0;
        }
        
    }

    void TiledAnimation::disableAnimation() const
    {
        if (mController)
        {
            mController->setEnabled(false);
        }
        mBillboardSet.getParentSceneNode()->flipVisibility();
    }
}
