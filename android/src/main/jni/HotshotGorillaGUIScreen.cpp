//
// Created by sebas on 31.03.2017.
//

#include <OgreTextureGpu.h>
#include <OgreSceneManager.h>
#include <OgreRoot.h>
#include "HotshotCommon.h"
#include "HotshotGorillaGUIScreen.h"
// #include "OgreHardwarePixelBuffer.h"
#include "OgreStagingTexture.h"
#include "HotshotGorillaGUIScreenRenderable.h"

namespace Hotshot
{

    void Screen::createAtlasPtrList(int size) {
        atlasPtrList = (::Ogre::TextureGpu **) malloc(sizeof(::Ogre::TextureGpu*) * size);
        atlasPtrListSize = size;
    }

    void Screen::destroyAtlasPtrList() {
        free(atlasPtrList);
    }

    Screen::Screen(int atlasNum) {
        createAtlasPtrList(atlasNum);
    }

    Screen::~Screen() {
        destroyAtlasPtrList();
    }

//    void Screen::addScreenRenderable(ScreenRenderable *screenRenderable) {
//
//    }
//
//    void Screen::removeScreenRenderable(ScreenRenderable *screenRenderable) {
//
//    }

    void Screen::setAtlasPtr(::Ogre::TextureGpu *texture, int pos) {
        atlasPtrList[pos] = texture;
    }

    ScreenRenderable *Screen::createScreenRenderable(::Ogre::TextureGpu *texture,
                                                     ::Ogre::SceneManager *sceneManager,
                                                     unsigned char queueGroupId) {
        ::Ogre::ObjectMemoryManager &objectMemoryManager = sceneManager->_getEntityMemoryManager(::Ogre::SCENE_DYNAMIC);
        ScreenRenderable *screenRenderable = new ScreenRenderable(
                ::Ogre::Id::generateNewId<ScreenRenderable>(),
                &objectMemoryManager, sceneManager, queueGroupId);
		screenRenderable->setRenderQueueGroup(queueGroupId);
        screenRenderable->setTexture(texture);
        screenRenderableMap.insert(std::make_pair(queueGroupId, screenRenderable));
        return screenRenderable;
    }

    void Screen::destroyScreenRenderable(::Ogre::uint8 queueGroupId) {
        const std::map<unsigned char, Hotshot::ScreenRenderable *>::iterator &it = screenRenderableMap.find(queueGroupId);
        if (it == screenRenderableMap.end())
        {
            ::Ogre::String str("No element with queueGroupId " + SSTR(queueGroupId) + " found");
            LOGI("%s", str.c_str());
        }
        else
        {
			delete it->second;
            unsigned int erasedCount = screenRenderableMap.erase(queueGroupId);
            
        }

//        delete screenRenderable;
    }

    bool Screen::isVisible() const {
        return mVisible;
    }

    void Screen::setVisible(bool mVisible) {
        Screen::mVisible = mVisible;
        std::map<unsigned char, Hotshot::ScreenRenderable *>::iterator it = screenRenderableMap.begin();
        const std::map<unsigned char, Hotshot::ScreenRenderable *>::iterator &end = screenRenderableMap.end();
        while (it != end)
        {
            it->second->setScreenRenderableVisible(mVisible);
            ++it;
        }
    }

    void Screen::renderOnce() {
        std::map<unsigned char, Hotshot::ScreenRenderable *>::iterator it = screenRenderableMap.begin();
        const std::map<unsigned char, Hotshot::ScreenRenderable *>::iterator &endIterator = screenRenderableMap.end();
        while (it != endIterator)
        {
            ScreenRenderable *screenRenderable = it->second;
            ++it;
        }
    }

    void Screen::fillVertexBuffer(int size, ::Ogre::uint8 queueGroupId, void *buffer) {
        const std::map<unsigned char, Hotshot::ScreenRenderable *>::iterator &it = screenRenderableMap.find(queueGroupId);
        if (it == screenRenderableMap.end())
        {
            ::Ogre::String str("No element with queueGroupId " + SSTR(queueGroupId) + " found");
            LOGI("%s", str.c_str());
        }
        else
        {
            ScreenRenderable *screenRenderable = it->second;
            screenRenderable->fillVertexBuffer(size, buffer);
        }
    }
}
