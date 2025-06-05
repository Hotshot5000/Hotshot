//
// Created by sebas on 02.03.2017.
//

#include "HotshotGorillaGUIScreenRenderable.h"
#include "HotshotGorillaGUI.h"
#include "OgreRoot.h"
#include "OgreTextureGpuManager.h"
#include "OgreHlms.h"
#include "OgreHlmsUnlitDatablock.h"
#include "HotshotCommon.h"

namespace Hotshot
{
    ::Ogre::String ScreenRenderable::mMaterialName = "";

    ScreenRenderable::ScreenRenderable(::Ogre::IdType id, ::Ogre::ObjectMemoryManager *objectMemoryManager,
                                                ::Ogre::SceneManager *manager, ::Ogre::uint8 queueGroupId) :
            SimpleRenderable(id, objectMemoryManager, manager), mQueueGroupId(queueGroupId), mLastSize(0), mVisible(false)
    {
        _createVertexBuffer();
        // use identity projection and view matrices
        mUseIdentityProjection = true;
        mUseIdentityView = true;
//        sceneNode = manager->createSceneNode();
//        sceneNode->setPosition(::Ogre::Vector3(0, 0, -10.0f));
//        sceneNode->attachObject(this);
        ::Ogre::SceneNode *dummyNode = GorillaGUI::getDummyNode();
        dummyNode->attachObject(this);
        setDefaultQueryFlags(0x0);
        manager->addRenderQueueListener(this);
    }

    ScreenRenderable::~ScreenRenderable()
    {
        ::Ogre::SceneManager *sceneManager = _getManager();
//        sceneManager->destroySceneNode(sceneNode);
        ::Ogre::SceneNode *dummyNode = GorillaGUI::getDummyNode();
        dummyNode->detachObject(this);
        sceneManager->removeRenderQueueListener(this);
        _destroyVertexBuffer();
    }

    void ScreenRenderable::renderQueueStarted(::Ogre::RenderQueue *rq, ::Ogre::uint8 queueGroupId,
                                              const ::Ogre::String &invocation,
                                              bool &skipThisInvocation)
    {
        if (!mVisible || mLastSize == 0)
        {
            return;
        }

        if (queueGroupId == mQueueGroupId)
        {
            rq->addRenderableV1(queueGroupId, false, this, this);
        }
    }

    void ScreenRenderable::_createVertexBuffer(size_t initialSize)
    {
        mVertexBufferSize = initialSize * 6;
        mRenderOp.vertexData = OGRE_NEW ::Ogre::v1::VertexData( 0 );
        mRenderOp.vertexData->vertexStart = 0;
		mRenderOp.vertexData->vertexCount = mVertexBufferSize;

        ::Ogre::v1::VertexDeclaration* vertexDecl = mRenderOp.vertexData->vertexDeclaration;
        size_t offset = 0;

        // Position.
        vertexDecl->addElement(0,0, ::Ogre::VET_FLOAT3, ::Ogre::VES_POSITION);
        offset += ::Ogre::v1::VertexElement::getTypeSize(::Ogre::VET_FLOAT3);

        // Colour
        vertexDecl->addElement(0, offset, ::Ogre::VET_FLOAT4, ::Ogre::VES_DIFFUSE);
        offset += ::Ogre::v1::VertexElement::getTypeSize(::Ogre::VET_FLOAT4);

        // Texture Coordinates
        vertexDecl->addElement(0, offset, ::Ogre::VET_FLOAT2, ::Ogre::VES_TEXTURE_COORDINATES);

        mVertexBuffer = ::Ogre::v1::HardwareBufferManager::getSingletonPtr()
                ->createVertexBuffer(
                        vertexDecl->getVertexSize(0),
                        mVertexBufferSize,
                        ::Ogre::v1::HardwareBuffer::HBU_DYNAMIC_WRITE_ONLY_DISCARDABLE,
                        false
                );

        mRenderOp.vertexData->vertexBufferBinding->setBinding(0, mVertexBuffer);
        mRenderOp.operationType = ::Ogre::OperationType ::OT_TRIANGLE_LIST;
        mRenderOp.useIndexes = false;
        mRenderOp.useGlobalInstancingVertexBufferIsAvailable = false;
    }

    void ScreenRenderable::_resizeVertexBuffer(size_t requestedSize) 
    {
        if (mVertexBufferSize == 0)
            _createVertexBuffer();

        if (requestedSize > mVertexBufferSize)
        {
            size_t newVertexBufferSize = 1;

            while(newVertexBufferSize < requestedSize)
                newVertexBufferSize <<= 1;

            mVertexBuffer = ::Ogre::v1::HardwareBufferManager::getSingletonPtr()->createVertexBuffer(
                    mRenderOp.vertexData->vertexDeclaration->getVertexSize(0),
                    newVertexBufferSize,
                ::Ogre::v1::HardwareBuffer::HBU_DYNAMIC_WRITE_ONLY_DISCARDABLE,
                    false
            );
            mVertexBufferSize = newVertexBufferSize;
            mRenderOp.vertexData->vertexStart = 0;
//			mRenderOp.vertexData->vertexCount = mVertexBufferSize;
            mRenderOp.vertexData->vertexBufferBinding->setBinding(0, mVertexBuffer);
        }
    }

    void ScreenRenderable::_destroyVertexBuffer()
    {
        OGRE_DELETE mRenderOp.vertexData;
        mRenderOp.vertexData = 0;
        mVertexBuffer.reset();
        mVertexBufferSize = 0;
    }

    void ScreenRenderable::setTexture(::Ogre::TextureGpu *texture)
    {
        ::Ogre::HlmsManager *hlmsManager = ::Ogre::Root::getSingleton().getHlmsManager();
        ::Ogre::Hlms *hlms = hlmsManager->getHlms( ::Ogre::HLMS_UNLIT );
        ::Ogre::HlmsUnlitDatablock *datablock = (::Ogre::HlmsUnlitDatablock* ) hlms->getDatablock( mMaterialName );
//        const ::Ogre::TexturePtr &texturePtr = ::Ogre::TextureManager::getSingleton().getByName(texture->getName(), texture->getGroup());
//        datablock->setTexture(0, 0, texturePtr);
        setDatablock(datablock);
    }

    void ScreenRenderable::fillVertexBuffer(int size, void *buffer)
    {
        mLastSize = size;
		if (size == 0)
		{
			return;
		}
		size_t sizeInBytes = size * mRenderOp.vertexData->vertexDeclaration->getVertexSize(0);
        _resizeVertexBuffer(sizeInBytes);

        if (sizeInBytes > Hotshot::GorillaGUI::getBufferSize())
        {
            ::Ogre::String dbgStr = "Requesting more than allocated";
            LOGI("%s", dbgStr.c_str());
            exit(-1);
        }

		// float *dbgPtr = (float*)buffer;
		//
		// for (int i = 0; i < size; ++ i)
		// {
		//
		// 	float xPos = *dbgPtr++;
		// 	float yPos = *dbgPtr++;
		// 	float zPos = *dbgPtr++;
		//
		// 	float rCol = *dbgPtr++;
		// 	float gCol = *dbgPtr++;
		// 	float bCol = *dbgPtr++;
		// 	float aCol = *dbgPtr++;
		//
		// 	float uvU = *dbgPtr++;
		// 	float uvV = *dbgPtr++;
		//
		// 	::Ogre::String dbgStr(
		// 		"xPos: " + SSTR(xPos) +
		// 		" yPos: " + SSTR(yPos) +
		// 		" zPos: " + SSTR(zPos) +
		// 		" rCol: " + SSTR(rCol) +
		// 		" gCol: " + SSTR(gCol) +
		// 		" bCol: " + SSTR(bCol) +
		// 		" aCol: " + SSTR(aCol) +
		// 		" uvU: " + SSTR(uvU) +
		// 		" uvV: " + SSTR(uvV) +
		// 		"\n"
		// 		);
		// 	LOGI("%s", dbgStr.c_str());
		// }

        void *ptr = mVertexBuffer->lock(::Ogre::v1::HardwareBuffer::HBL_DISCARD);

        // memset(ptr, 0, mVertexBufferSize * mRenderOp.vertexData->vertexDeclaration->getVertexSize(0));
        memcpy(ptr, buffer, sizeInBytes);

        mVertexBuffer->unlock();
		mRenderOp.vertexData->vertexCount = size;
    }

    bool ScreenRenderable::isScreenRenderableVisible() const {
        return mVisible;
    }

    void ScreenRenderable::setScreenRenderableVisible(bool mVisible) {
        ScreenRenderable::mVisible = mVisible;
    }

    void ScreenRenderable::getRenderOperation(::Ogre::v1::RenderOperation &op, bool casterPass) {
        ::Ogre::v1::SimpleRenderable::getRenderOperation(op, casterPass);
    }

    void ScreenRenderable::getWorldTransforms(::Ogre::Matrix4 *xform) const {
        *xform = ::Ogre::Matrix4::IDENTITY;
    }

    const ::Ogre::LightList &ScreenRenderable::getLights(void) const {
        return ::Ogre::v1::SimpleRenderable::getLights();
    }


}

