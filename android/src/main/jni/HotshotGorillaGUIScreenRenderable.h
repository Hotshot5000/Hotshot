//
// Created by sebas on 02.03.2017.
//

#ifndef BLACKHOLEDARKSUNONLINE4_HOTSHOTGORILLAGUISCREENRENDERABLE_H
#define BLACKHOLEDARKSUNONLINE4_HOTSHOTGORILLAGUISCREENRENDERABLE_H

//#include <OgreId.h>
//#include <OgreRenderOperation.h>
#include "OgreSimpleRenderable.h"
#include "OgreRenderQueueListener.h"
#include "OgreHardwareBufferManager.h"
#include "OgreMatrix4.h"

namespace Hotshot
{
    class Screen;

    class ScreenRenderable : public ::Ogre::v1::SimpleRenderable, public ::Ogre::RenderQueueListener
    {

    public:
        static ::Ogre::String mMaterialName;
        ::Ogre::SceneManager *manager;
//        ::Ogre::SceneNode *sceneNode;
        Screen* parent;
        ::Ogre::v1::HardwareVertexBufferSharedPtr mVertexBuffer;
//        ::Ogre::v1::RenderOperation mRenderOp;
        int mVertexBufferSize;
        ::Ogre::uint8 mQueueGroupId;
        ::Ogre::uint32 mLastSize;
        bool mVisible;

        ScreenRenderable(::Ogre::IdType id, ::Ogre::ObjectMemoryManager *objectMemoryManager,
                         ::Ogre::SceneManager *manager, ::Ogre::uint8 queueGroupId);

        ~ScreenRenderable();

        virtual void renderQueueStarted( ::Ogre::RenderQueue *rq, ::Ogre::uint8 queueGroupId,
                                         const ::Ogre::String& invocation,
                                         bool& skipThisInvocation);

         // IF YOU GET non-virtual thunk error  ERROR IT MEANS THAT OGRE IS BUILT IN RELEASE MODE BUT LIBNATIVE IN DEBUG!!!
//        void setMaterial( const ::Ogre::MaterialPtr &mat ) override
//        {
//            ::Ogre::v1::SimpleRenderable::setMaterial(mat);
//        }

        void _createVertexBuffer(size_t initialSize = 128);
        void _resizeVertexBuffer(size_t requestedSize);
        void _destroyVertexBuffer();

        void fillVertexBuffer(int size, void* buffer);

        void setTexture(::Ogre::TextureGpu* texture);


        static const ::Ogre::String &getMaterialName() {
            return mMaterialName;
        }

        static void setMaterialName(const ::Ogre::String &materialName) {
            ScreenRenderable::mMaterialName = materialName;
        }

        ::Ogre::uint8 getQueueGroupId() const {
            return mQueueGroupId;
        }

        bool isScreenRenderableVisible() const;

        void setScreenRenderableVisible(bool mVisible);

        virtual void getRenderOperation(::Ogre::v1::RenderOperation &op, bool casterPass);

        virtual void getWorldTransforms(::Ogre::Matrix4 *xform) const;

        virtual const ::Ogre::LightList &getLights(void) const;
    };
}

#endif //BLACKHOLEDARKSUNONLINE4_HOTSHOTGORILLAGUISCREENRENDERABLE_H
