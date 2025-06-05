//
// Created by sebas on 22.02.2017.
//

#ifndef BLACKHOLEDARKSUNONLINE4_HOTSHOTGORILLAGUI_H
#define BLACKHOLEDARKSUNONLINE4_HOTSHOTGORILLAGUI_H

#include <jni.h>
#include <OgreColourValue.h>

namespace Hotshot
{

    namespace Ogre
    {
        class TextureBox;
        class ColourValue;
        class Texture;
        class NodeMemoryManager;
        class SceneNode;
    }

    class Sprite;
    class ScreenRenderable;

    class GorillaGUI
    {
    private:
        static std::map<::Ogre::uint8, ScreenRenderable*> screenRenderableMap;

//        static ::Ogre::Texture** atlasPtrList;
        static void** bufArr;
        static int currentFrameNum;
        static size_t bufferSize;
//        static int atlasPtrListSize;

        static ::Ogre::NodeMemoryManager *mNodeMemoryManager;
        static ::Ogre::SceneNode *mDummyNode;



        static bool equalsWithoutAlpha(const ::Ogre::ColourValue& val1, const ::Ogre::ColourValue& val2);
        static void extractColor(::Ogre::TextureGpu* stagingTexture, const ::Ogre::TextureBox& currentLock, ::Ogre::ColourValue& col, int prevData);

    public:
//        static ScreenRenderable* createScreenRenderable(::Ogre::uint8 queueGroupId);
//        static void destroyScreenRenderable(ScreenRenderable* screenRenderable);

        static void calculateSpriteCoordinates(Sprite& s, float mInverseTextureSizeX, float mInverseTextureSizeY);
        static int createNinePatch(JNIEnv *env, ::Ogre::TextureGpu* mTexture, jobject np);

        static jobjectArray createByteBuffers(JNIEnv *env, int size, int num);

        static void createDummyNode();
        static void destroyDummyNode();

//        static void createAtlasPtrList(int size);
//        static void destroyAtlasPtrList();
//        static void setAtlasPtr(::Ogre::Texture* texture, int pos);

//        static void renderOnce();

        static void* getBuffer(int buffer)
        {
            return bufArr[buffer];
        }

        static int getCurrentFrameNum() {
            return currentFrameNum;
        }

        static void setCurrentFrameNum(int currentFrameNum) {
            GorillaGUI::currentFrameNum = currentFrameNum;
        }

        static ::Ogre::SceneNode *getDummyNode();

        static size_t getBufferSize()
        {
            return bufferSize;
        }
    };
}

#endif //BLACKHOLEDARKSUNONLINE4_HOTSHOTGORILLAGUI_H
