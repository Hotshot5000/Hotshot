//
// Created by sebas on 14-Nov-17.
//

#ifndef BLACKHOLEDARKSUNONLINE5_HOTSHOTFULLQUADANIMATIONCONTROLLER_H
#define BLACKHOLEDARKSUNONLINE5_HOTSHOTFULLQUADANIMATIONCONTROLLER_H

#include "OgreController.h"

namespace Hotshot {

    class SceneCompositor;

    class FullQuadAnimationController: public ::Ogre::ControllerValue<::Ogre::Real>
    {
    private:
        SceneCompositor *mParent;
        ::Ogre::ColourValue mStartColour;
        ::Ogre::GpuProgramParametersSharedPtr mProgramParameters;
        ::Ogre::String mNamedConstant;
        Ogre::Real mValue;
        bool mAnimationEndReached;
    public:
        FullQuadAnimationController(SceneCompositor *parent, Ogre::String &materialName, Ogre::String &namedConstant,
                                    Ogre::ColourValue &startColour);

        virtual void setValue(Ogre::Real value);

        virtual Ogre::Real getValue(void) const;
    };
}

#endif //BLACKHOLEDARKSUNONLINE5_HOTSHOTFULLQUADANIMATIONCONTROLLER_H
