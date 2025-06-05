//
// Created by sebas on 31.03.2017.
//

#ifndef BLACKHOLEDARKSUNONLINE4_HOTSHOTGORILLAGUISCREEN_H
#define BLACKHOLEDARKSUNONLINE4_HOTSHOTGORILLAGUISCREEN_H

#include <vector>

namespace Hotshot
{
    namespace Ogre
    {
        class TextureGpu;
        class SceneManager;
    }

    class ScreenRenderable;

    class Screen
    {
    private:
        std::map<::Ogre::uint8, ScreenRenderable*> screenRenderableMap;
//        std::vector<ScreenRenderable*> screenRenderableList;
        ::Ogre::TextureGpu** atlasPtrList;
        int atlasPtrListSize;
        bool mVisible;

        void createAtlasPtrList(int size);
        void destroyAtlasPtrList();


    public:

        Screen(int atlasNum);
        ~Screen();

        ScreenRenderable *createScreenRenderable(::Ogre::TextureGpu *texture, ::Ogre::SceneManager *sceneManager, unsigned char queueGroupId);
        void destroyScreenRenderable(::Ogre::uint8 queueGroupId);

//        void addScreenRenderable(ScreenRenderable* screenRenderable);
//        void removeScreenRenderable(ScreenRenderable* screenRenderable);

        void renderOnce();

        void setAtlasPtr(::Ogre::TextureGpu* texture, int pos);

        void fillVertexBuffer(int size, ::Ogre::uint8 queueGroupId, void* buffer);

        bool isVisible() const;

        void setVisible(bool mVisible);
    };
}

#endif //BLACKHOLEDARKSUNONLINE4_HOTSHOTGORILLAGUISCREEN_H
