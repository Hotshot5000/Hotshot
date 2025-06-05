//
// Created by sebas on 14-Nov-17.
//

#ifndef BLACKHOLEDARKSUNONLINE5_HOTSHOTSCENECOMPOSITOR_H
#define BLACKHOLEDARKSUNONLINE5_HOTSHOTSCENECOMPOSITOR_H

#include <OgreSharedPtr.h>
#include <OgreString.h>

namespace Ogre
{
//    class String;
    class CompositorWorkspace;
    template <typename T> class Controller;
}

namespace Hotshot {


    class FullQuadAnimationController;

    class SceneCompositor
    {
    private:
        ::Ogre::SharedPtr< ::Ogre::ControllerValue<::Ogre::Real> > mVal;
        ::Ogre::SharedPtr< ::Ogre::ControllerFunction<::Ogre::Real> > mFunc;
        ::Ogre::Controller<::Ogre::Real>* mController;

        ::Ogre::String mWorkspaceName;
        ::Ogre::CompositorWorkspace *mWorkspace;
        ::Ogre::String mBaseNodeName;
        ::Ogre::String mPreviousNodeName;

        static long long int mUniqueCompositorAnimationId;

        void insertNode(::Ogre::CompositorWorkspace *workspace, ::Ogre::String &workspaceName,
            ::Ogre::String &baseNodeName, ::Ogre::String &nodeToInsertName, ::Ogre::String &string);
        void destroyAnimation();
        void endAnimation();

    public:
        SceneCompositor();

        ~SceneCompositor();

        long long int startCompositorAnimation(::Ogre::CompositorWorkspace *workspace,
                                      ::Ogre::String &workspaceName,
                                      ::Ogre::String &baseNodeName,
                                      ::Ogre::String &nodeToInsertName,
                                      ::Ogre::String &previousNodeName,
                                      ::Ogre::ColourValue &initialColor,
                                      float scalingStep);

        void stopCompositorAnimation(long long int compositorId);


        void animationEndReached();



    };
}

#endif //BLACKHOLEDARKSUNONLINE5_HOTSHOTSCENECOMPOSITOR_H
