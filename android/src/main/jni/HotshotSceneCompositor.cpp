//
// Created by sebas on 14-Nov-17.
//

#include <OgreRoot.h>
#include <OgreControllerManager.h>
#include <OgrePredefinedControllers.h>
#include "HotshotSceneCompositor.h"

#include "Compositor/OgreCompositorManager2.h"
#include "Compositor/OgreCompositorWorkspace.h"
#include "Compositor/OgreCompositorWorkspaceDef.h"
#include "Compositor/OgreCompositorNode.h"
#include "Compositor/OgreCompositorNodeDef.h"

#include "OgreMaterialManager.h"
#include "OgreTechnique.h"
#include "OgrePass.h"
#include "HotshotCommon.h"
#include "HotshotFullQuadAnimationController.h"

long long int Hotshot::SceneCompositor::mUniqueCompositorAnimationId = 0;

Hotshot::SceneCompositor::SceneCompositor() : mController(0) {}

Hotshot::SceneCompositor::~SceneCompositor() {

}

long long int Hotshot::SceneCompositor::startCompositorAnimation(::Ogre::CompositorWorkspace *workspace,
                                                        ::Ogre::String &workspaceName,
                                                        ::Ogre::String &baseNodeName,
                                                        ::Ogre::String &nodeToInsertName,
                                                        ::Ogre::String &previousNodeName,
                                                        ::Ogre::ColourValue &initialColor,
                                                        float scalingStep)
{
    destroyAnimation();
    insertNode(workspace, workspaceName, baseNodeName, nodeToInsertName, previousNodeName);

    Ogre::String materialName = Ogre::String("Postprocess/Colored");
    Ogre::String namedConstant = Ogre::String("color");
//    FullQuadAnimationController *animationController = OGRE_NEW FullQuadAnimationController(materialName, namedConstant, rgba);
    mVal.bind(OGRE_NEW FullQuadAnimationController(this, materialName, namedConstant, initialColor));
//    mCtrlVal = static_cast<Ogre::TextureAnimationControllerValue*>(mVal.get());

//    Ogre::ScaleControllerFunction *ctrlFunc = OGRE_NEW Ogre::ScaleControllerFunction(0.1f, true);
    mFunc.bind(OGRE_NEW Ogre::ScaleControllerFunction(scalingStep, false));
    mController = Ogre::ControllerManager::getSingleton().createController(Ogre::ControllerManager::getSingleton().getFrameTimeSource(),
                                                                           mVal, mFunc);
    return mUniqueCompositorAnimationId++;
}

void Hotshot::SceneCompositor::insertNode(Ogre::CompositorWorkspace *workspace, Ogre::String &workspaceName,
                                          Ogre::String &baseNodeName, Ogre::String &nodeToInsertName, Ogre::String &previousNodeName)
{
    mWorkspaceName = workspaceName;
    mWorkspace = workspace;
    mBaseNodeName = baseNodeName;
    mPreviousNodeName = previousNodeName;

    Ogre::Root *root = Ogre::Root::getSingletonPtr();
    Ogre::CompositorManager2 *compositorManager = root->getCompositorManager2();

    //The workspace instance can't return a non-const version of
    //its definition, so we perform the lookup this way.
    Ogre::CompositorWorkspaceDef *workspaceDef = compositorManager->getWorkspaceDefinition(workspaceName);

    //Clear the definition made with scripts as example
    workspaceDef->clearAll();

    workspaceDef->connect( baseNodeName, 0, nodeToInsertName, 0 );
    workspaceDef->connectExternal( 0, nodeToInsertName, 1 );

    workspace->recreateAllNodes();

}

void Hotshot::SceneCompositor::endAnimation() {
    insertNode(mWorkspace, mWorkspaceName, mBaseNodeName, mPreviousNodeName, mPreviousNodeName);


}

void Hotshot::SceneCompositor::destroyAnimation() {
    if (mController)
    {
        Ogre::ControllerManager::getSingleton().destroyController(mController);
        mController = 0;
    }
    mVal.reset();
    mFunc.reset();
}

void Hotshot::SceneCompositor::animationEndReached() {
    endAnimation();

}

void Hotshot::SceneCompositor::stopCompositorAnimation(long long int compositorId) {
    if (compositorId == -1)
    {
        endAnimation();
        destroyAnimation();
    }
    else if (compositorId == mUniqueCompositorAnimationId - 1) // Ignore the previous stop calls.
    {
        endAnimation();
        destroyAnimation();
    }
}
