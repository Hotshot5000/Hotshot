//
// Created by sebas on 28.02.2017.
//

#include "OgreRoot.h"
#include "HotshotLoadingScreen.h"
#include "OgreOverlayManager.h"
#include "OgreOverlay.h"
#include "OgreWindow.h"
#include "OgreTextureGpuManager.h"
#include "OgreOverlayContainer.h"

namespace Hotshot
{
    void LoadingScreen::showLoadingScreen(::Ogre::Window *renderWindow, float screenDensity)
    {
        if (showingLoadingScreen)
        {
            return;
        }
        if (!loadingScreenLoaded)
        {
            loadLoadingScreenResources(renderWindow, screenDensity);
            loadingScreenLoaded = true;
        }
        ::Ogre::v1::Overlay *loadingScreenOverlay = ::Ogre::v1::OverlayManager::getSingleton().getByName("Core/LoadingScreenOverlay");
        loadingScreenOverlay->show();
        ::Ogre::Root::getSingleton().renderOneFrame();
        showingLoadingScreen = true;
    }

    void LoadingScreen::hideLoadingScreen()
    {
        if (showingLoadingScreen) {
            ::Ogre::v1::OverlayManager::getSingleton().getByName("Core/LoadingScreenOverlay")->hide();
            showingLoadingScreen = false;
        }
    }

    LoadingScreen &LoadingScreen::getSingleton()
    {
        static LoadingScreen ls;
        return ls;
    }

    void LoadingScreen::loadLoadingScreenResources(::Ogre::Window *renderWindow, float screenDensity)
    {
        unsigned int width;
        unsigned int height;
        int left;
        int top;
        renderWindow->getMetrics(width, height, left, top);
        ::Ogre::v1::Overlay *loadingScreenOverlay = ::Ogre::v1::OverlayManager::getSingleton().getByName("Core/LoadingScreenOverlay");
        ::Ogre::v1::OverlayContainer *loadingScreen = loadingScreenOverlay->getChild("LoadingScreen");
        Ogre::TextureGpuManager* textureManager = Ogre::Root::getSingleton().getRenderSystem()->getTextureGpuManager();
        ::Ogre::TextureGpu *loadingScreenTexture = textureManager->createOrRetrieveTexture(
            "loading_screen.png", Ogre::GpuPageOutStrategy::GpuPageOutStrategy::Discard, 
            Ogre::CommonTextureTypes::Diffuse, "Essential");
        loadingScreenTexture->scheduleTransitionTo(Ogre::GpuResidency::Resident);
        loadingScreenTexture->waitForData();
        float lsWidth = loadingScreenTexture->getWidth();
        float lsHeight = loadingScreenTexture->getHeight();
        // Also take into account the screen density.
        width /= (int) screenDensity;
        height /= (int) screenDensity;
        loadingScreen->setLeft(width / 2 - lsWidth / 2);
        loadingScreen->setTop(height / 2 - lsHeight / 2);
        loadingScreen->setWidth(lsWidth);
        loadingScreen->setHeight(lsHeight);
    }


}





