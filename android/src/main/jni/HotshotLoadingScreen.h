//
// Created by sebas on 28.02.2017.
//

#ifndef BLACKHOLEDARKSUNONLINE4_HOTSHOTLOADINGSCREEN_H
#define BLACKHOLEDARKSUNONLINE4_HOTSHOTLOADINGSCREEN_H

namespace Ogre
{
    class Window;
}

namespace Hotshot
{


    class LoadingScreen
    {
    private:
        bool loadingScreenLoaded, showingLoadingScreen;

        LoadingScreen() : loadingScreenLoaded(false), showingLoadingScreen(false) {}

        void loadLoadingScreenResources(::Ogre::Window *renderWindow, float screenDensity);
    public:
        void showLoadingScreen(::Ogre::Window *renderWindow, float screenDensity);
        void hideLoadingScreen();

        static LoadingScreen& getSingleton();
    };
}

#endif //BLACKHOLEDARKSUNONLINE4_HOTSHOTLOADINGSCREEN_H
