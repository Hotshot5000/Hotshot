//
// Created by sebas on 14-Nov-17.
//

#include <OgreMaterialManager.h>
#include <OgreTechnique.h>
#include <OgrePass.h>
#include "HotshotFullQuadAnimationController.h"
#include "HotshotSceneCompositor.h"
#include "HotshotCommon.h"
#include "OgreGpuProgramParams.h"

Hotshot::FullQuadAnimationController::FullQuadAnimationController(SceneCompositor *parent,
                                                                  ::Ogre::String& materialName,
                                                                  ::Ogre::String& namedConstant,
                                                                  Ogre::ColourValue &startColour) :
        mParent(parent),
        mNamedConstant(namedConstant),
        mStartColour(startColour),
        mValue(0.0f),
        mAnimationEndReached(false)
{
    ::Ogre::MaterialManager &materialManager = ::Ogre::MaterialManager::getSingleton();
    const ::Ogre::MaterialPtr &coloredMaterial = materialManager.getByName(materialName);
    mProgramParameters = coloredMaterial->getTechnique(0)->getPass(0)->getFragmentProgramParameters();

}

void Hotshot::FullQuadAnimationController::setValue(::Ogre::Real value) {
    if (mAnimationEndReached)
    {
        return;
    }
    mValue += value;
    ::Ogre::Real finalVal = 1.0f - mValue;
//    ::Ogre::String s("FinalVal: " + SSTR(finalVal));
//    LOGI("%s", s.c_str());
    if (finalVal < 0.0f)
    {
        mParent->animationEndReached();
        mAnimationEndReached = true;
        return;
    }
    ::Ogre::ColourValue currentColour = mStartColour * finalVal;
    mProgramParameters->setNamedConstant(mNamedConstant, currentColour);
}

Ogre::Real Hotshot::FullQuadAnimationController::getValue(void) const {
    return 0.0f;
}
