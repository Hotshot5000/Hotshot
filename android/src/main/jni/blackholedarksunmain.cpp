#include <jni.h>
#include "HotshotCommon.h"
#include "JniCommon.h"

#if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_ANDROID)
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#endif


//#include <GLES2/gl2.h>

#include "OgrePrerequisites.h"
#include "OgreRoot.h"
#include "OgreParticleFXPlugin.h"
#include "OgreException.h"
#include "OgreConfigFile.h"

#include "OgreWindow.h"
#include "OgreViewport.h"
#include "OgreCamera.h"
#include "OgreItem.h"
#include "OgreSceneQuery.h"

#include "OgreHlmsUnlit.h"
#include "OgreHlmsPbs.h"
#include "OgreHlmsManager.h"
#include "OgreArchiveManager.h"
#include "OgreObjectMemoryManager.h"
#include "OgreObjectData.h"

#if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_ANDROID)
#include "Android/OgreAPKFileSystemArchive.h"
#include "Android/OgreAPKZipArchive.h"
#endif

#include "Compositor/OgreCompositorManager2.h"
#include "Compositor/OgreCompositorWorkspace.h"
#include "Compositor/OgreCompositorNodeDef.h"
#include "Compositor/OgreCompositorWorkspaceDef.h"
#include "Compositor/Pass/PassIblSpecular/OgreCompositorPassIblSpecularDef.h"

#include "OgreOverlaySystem.h"
#include "OgreOverlayManager.h"
#include "OgreOverlay.h"
#include "OgreOverlayContainer.h"
#include "OgrePanelOverlayElement.h"
#include "OgreTextAreaOverlayElement.h"

#include "OgreTechnique.h"
#include "OgrePass.h"

#include "OgreFontManager.h"

#include "OgreFrameStats.h"

#include "OgreHlmsUnlitDatablock.h"

#include "OgreMeshManager2.h"
#include "OgreMesh2.h"

#include "OgreWindowEventUtilities.h"
#include "OgrePlatform.h"
//#include "OgreGLES3RenderSystem.h"
//#include "OgreGL3PlusRenderSystem.h"
// #include "OgreTextureManager.h"
#include "OgreTextureGpuManager.h"
#if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_WIN32)
//#include "OgreWin32Window.h"
//#include "OgreWin32EGLWindow.h"
// #include "OgreD3D11RenderWindow.h"
#include "OgreVulkanWindow.h"
#include "Windowing/win32/OgreVulkanWin32Window.h"
#endif


#include "HotshotGorillaGUISprite.h"
#include "HotshotGorillaGUI.h"
#include "HotshotGorillaGUINinePatch.h"
#include "HotshotLoadingScreen.h"
#include "HotshotGorillaGUIScreenRenderable.h"
#include "HotshotGorillaGUIScreen.h"
#include "CommandBufferUtility.h"
#include "FrameEndListenerExecutor.h"
#include "RayQueryExecuteFrameEndListenerExecutor.h"
#include "DynamicOverlayElement.h"
#include "HotshotTiledAnimation.h"
#include <iostream>
#include <OgreBillboardSet.h>
#include <OgreBillboard.h>
#include <OgreParticleSystem.h>
#include <OgreHlmsPbsDatablock.h>
#include <OgreMaterialManager.h>
#include "HotshotSceneCompositor.h"
// #include "OgreHardwarePixelBuffer.h"
// #include "OgreRenderTexture.h"
#include "OgreStagingTexture.h"
#include "OgreTextureGpu.h"
#include "Utils/SmaaUtils.h"
#include "OgreAbiUtils.h"

#ifdef USE_PCZ
#include <OgrePCZSceneManager.h>
#endif

#if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_IOS)
#include "GameViewControllerIntf.h"
#include "GameViewControllerImpl.h"
#include "MetalViewCallbacksIntf.h"
#include "MetalViewCallbacksImpl.h"
#include "iOS/macUtils.h"
#endif

#define TEST_RENDERING_THREAD

enum NativeCallsList {
//    NEWROOT = 1,
//    ROOT_INITIALIZE,
    ROOT_CREATERENDERWINDOW = 1,
//    ROOT_CREATESCENEMANAGER,
//    RENDERSYSTEM_SETCONFIGOPTION,
    SCREEN_CREATE,
    SCREEN_DESTROY,
    SCREEN_CREATELAYER,
    SCREEN_DESTROYLAYER,
    SCREEN_RENDERONCE,
    SCREEN_UPDATEVERTEXLISTSIZE,
    ROOT_RENDERONEFRAME,
    SCREEN_SETVISIBLE,

    CREATE_RAY_QUERY,
    DESTROY_RAY_QUERY,
    RAY_QUERY_SETSORTBYDISTANCE,
    RAY_QUERY_EXECUTE,

    GET_VIEWPORT,

    SCENEMANAGER_ITEM_CREATE,
    SCENEMANAGER_ITEM_CREATE_ALL_PARAMS,
    SCENEMANAGER_ITEM_DESTROY,
    SCENEMANAGER_ITEM_DESTROY_ALL,
    SCENEMANAGER_CREATE_PARTICLE_SYSTEM_STRING,
    SCENEMANAGER_CREATE_PARTICLE_SYSTEM_QUOTA_RESOURCE_GROUP,
    SCENEMANAGER_DESTROY_PARTICLE_SYSTEM,
    SCENEMANAGER_DESTROY_ALL_PARTICLE_SYSTEMS,
    SCENEMANAGER_CREATE_BILLBOARD_SET,
    SCENEMANAGER_DESTROY_BILLBOARD_SET,
    SCENEMANAGER_DESTROY_ALL_BILLBOARD_SETS,
    SCENEMANAGER_CLEAR_SCENE,
    SCENEMANAGER_SET_AMBIENT_LIGHT,
    SCENEMANAGER_SET_SKYBOX,
    SCENEMANAGER_SET_SKYBOX_ALL_PARAMS,
    SCENEMANAGER_SET_SKYBOX_ENABLED,
    SCENEMANAGER_CREATE_SCENE_NODE,
    SCENEMANAGER_CREATE_SCENE_NODE_ALL_PARAMS,
    SCENEMANAGER_DESTROY_SCENE_NODE,
    SCENEMANAGER_CREATE_LIGHT,
    SCENEMANAGER_DESTROY_LIGHT,
    SCENEMANAGER_DESTROY_ALL_LIGHTS,
    SCENEMANAGER_NOTIFY_STATIC_DIRTY,


    NODE_ADD_CHILD,
    NODE_REMOVE_CHILD,
//    NODE_HAS_CHILD,
//    NODE_SET_PARENT,
//    NODE_UNSET_PARENT,

    SCENENODE_CREATE_CHILD,
    SCENENODE_CREATE_CHILD_ALL_PARAMS,
    SCENENODE_REMOVE_AND_DESTROY,
    SCENENODE_REMOVE_AND_DESTROY_ALL_CHILDREN,
    SCENENODE_ATTACH_OBJECT,
    SCENENODE_ATTACH_PARTICLE_SYSTEM,
    SCENENODE_DETACH_OBJECT,
    SCENENODE_DETACH_ALL_OBJECTS,
    SCENENODE_SETPOSITION_XYZ,
    SCENENODE_SETPOSITION_VEC,
    SCENENODE_SETORIENTATION,
    SCENENODE_SETDERIVEDPOSITION_XYZ,
    SCENENODE_SETDERIVEDPOSITION_VEC,
    SCENENODE_SETDERIVEDORIENTATION,
    SCENENODE_SETSCALING_XYZ,
//        SCENENODE_CREATECHILDSCENENODE,
            SCENENODE_LOOKAT,
    SCENENODE_LOOKAT_ALL_PARAMS,
    SCENENODE_SETDIRECTION,
    SCENENODE_SETDIRECTION_ALL_PARAMS,
    SCENENODE_SETVISIBLE,
    SCENENODE_FLIPVISIBILITY,
    SCENENODE_SETSTATIC,
    SCENENODE_SETNAME,

    LIGHT_SET_TYPE,
    LIGHT_SET_SPOTLIGHT_RANGE,
    LIGHT_SET_DIFFUSE_COLOUR,
    LIGHT_SET_SPECULAR_COLOUR,
    LIGHT_SET_ATTENUATION,
    LIGHT_SET_POWER_SCALE,
    LIGHT_SET_ATTENUATION_BASED_ON_RADIUS,
//    LIGHT_SET_POSITION,
    LIGHT_SET_DIRECTION,

    ITEM_SET_DATABLOCK,
    ITEM_SET_VISIBILITY_FLAGS,

    COMPOSITOR_WORKSPACE_SET_ENABLED,

    CAMERA_INVALIDATE_VIEW,
    CAMERA_GET_PROJECTION_MATRIX,
    CAMERA_GET_VIEW_MATRIX,
    CAMERA_IS_VISIBLE_VEC,
    CAMERA_IS_VISIBLE_AXIS_ALIGNED_BOX,

    GET_ITEMS_AABBS,

    MOVABLE_OBJECT_DETACH_FROM_PARENT,

    OVERLAY_MANAGER_CREATE_OVERLAY,
    OVERLAY_MANAGER_DESTROY_OVERLAY_BY_NAME,
    OVERLAY_MANAGER_DESTROY_OVERLAY_BY_PTR,
    OVERLAY_MANAGER_DESTROY_ALL,
    OVERLAY_MANAGER_GET_BY_NAME,
    OVERLAY_MANAGER_UPDATE_DATA,

    OVERLAY_SHOW,
    OVERLAY_HIDE,

    OVERLAY_ELEMENT_SHOW,
    OVERLAY_ELEMENT_HIDE,
    OVERLAY_ELEMENT_SET_LEFT,
    OVERLAY_ELEMENT_SET_TOP,
    OVERLAY_ELEMENT_SET_WIDTH,
    OVERLAY_ELEMENT_SET_HEIGHT,
    OVERLAY_ELEMENT_SET_CAPTION,
    OVERLAY_ELEMENT_SET_METRICS_MODE,
//        OVERLAY_ELEMENT_UPDATE_DATA,
    OVERLAY_ELEMENT_SET_MATERIAL_NAME,
    OVERLAY_ELEMENT_SET_ALIGNMENT,

    GPU_PROGRAM_PARAMS_GET_PROGRAM_PARAMS,
    GPU_PROGRAM_PARAMS_SET_NAMED_CONSTANT_COLOUR,
    GPU_PROGRAM_PARAMS_SET_NAMED_CONSTANT_INT,
    GPU_PROGRAM_PARAMS_SET_NAMED_CONSTANT_FLOAT,

    TEXTURE_MANAGER_GET_BY_NAME,
    TEXTURE_MANAGER_GET_BY_NAME_OVERLAY_ELEMENT,

    FRAME_STATS_UPDATE,

    UNLIT_DATABLOCK_SET_USE_COLOUR,
    UNLIT_DATABLOCK_SET_COLOUR,

    DYNAMIC_OVERLAY_ELEM_CTOR,
    DYNAMIC_OVERLAY_ELEM_DTOR,
    DYNAMIC_OVERLAY_ELEM_RESET_TO_INITIAL_TEXTURE,
    DYNAMIC_OVERLAY_ELEM_UPDATE_FINAL_TEXTURE,
    DYNAMIC_OVERLAY_ELEM_SET_POINT_IN_SCREEN_SPACE,
    DYNAMIC_OVERLAY_ELEM_LOCK,
    DYNAMIC_OVERLAY_ELEM_UNLOCK,
    DYNAMIC_OVERLAY_ELEM_SET_AREA_VEC,
    DYNAMIC_OVERLAY_ELEM_SET_AREA,

    TILED_ANIMATION_CREATE,
    TILED_ANIMATION_DESTROY,
    TILED_ANIMATION_UPDATE_CURRENT_FRAME_NUM,

    BILLBOARDSET_CREATE_BILLBOARD,
    BILLBOARDSET_DESTROY_BILLBOARD,
//    BILLBOARDSET_DESTROY_BILLBOARD_BY_ID,
    BILLBOARDSET_SET_COMMON_UP_VECTOR,
    BILLBOARDSET_SET_COMMON_DIRECTION,
    BILLBOARDSET_SET_DEFAULT_DIMENSIONS,
    BILLBOARDSET_SET_MATERIAL_NAME,
    BILLBOARDSET_SET_DATABLOCK_NAME,
    BILLBOARDSET_SET_BILLBOARD_ORIGIN,
    BILLBOARDSET_SET_BILLBOARD_ROTATION_TYPE,
    BILLBOARDSET_SET_BILLBOARD_TYPE,

    BILLBOARD_SET_ROTATION,

    MOVABLE_OBJECT_SET_RENDER_QUEUE_GROUP,

    PARTICLE_SYSTEM_SET_MATERIAL_NAME,

    SCENE_COMPOSITOR_INSERT_NODE,
    SCENE_COMPOSITOR_REVERT_NODE,

#ifdef TEST_RENDERING_THREAD

    TEST_CALL_RENDERING_THREAD_DATA_FLUSH1 = 200,
    TEST_CALL_RENDERING_THREAD_DATA_FLUSH2,
    TEST_CALL_RENDERING_THREAD_DATA_FLUSH3,
    TEST_CALL_RENDERING_THREAD_DATA_FLUSH4,
    TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY1,
    TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY2,
    TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY3,
    TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY4,
    TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_PTR1 = 215,
    TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_PTR2,
    TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_PTR3,
    TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_PTR4,
    TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_ARRAY1,
    TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_ARRAY2,
    TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_ARRAY3,
    TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_ARRAY4,

    TEST_CALL1 = 240,
    TEST_CALL2,
    TEST_CALL3,
    TEST_CALL4,
    TEST_CALL_W_RESPONSE1,
    TEST_CALL_W_RESPONSE2,
    TEST_CALL_W_RESPONSE3,
    TEST_CALL_W_RESPONSE4,
#endif

    FRAME_ID_POS = 255
};

enum GpuProgramParametersType 
{
    GPU_VERTEX = 1,
    GPU_FRAGMENT,
    GPU_GEOMETRY,
    GPU_COMPUTE,
    GPU_TESSELATION_DOMAIN,
    GPU_TESSELATION_HULL,
    GPU_SHADOW_CASTER_VERTEX,
    GPU_SHADOW_CASTER_FRAGMENT
};

#if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_ANDROID)
AAssetManager *mgr;
ANativeWindow* window;
#endif

#if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_IOS)
GameViewControllerImpl *gameViewControllerImpl = 0;
MetalViewCallbacksImpl *metalViewCallbacksImpl = 0;
#endif

int readingBufferSizeInBytes;
int bufferCount;
jclass byteBufferCls;
jmethodID limit;


jclass vector3DCls;
jmethodID vector3DSet;

Ogre::ParticleFXPlugin* particleFXPlugin;

Ogre::String CAMERA_NODE_NAME = "camera";
Ogre::Camera* mCubeCamera = 0;

enum MovableObjectType
{
    ITEM,
    BILLBOARD_SET
};

class MovableObjectWithType
{
private:
    Ogre::MovableObject *movableObject;
    unsigned char type;
public:
    MovableObjectWithType(Ogre::MovableObject* movableObject_, MovableObjectType type_) :
    movableObject(movableObject_), type((unsigned char) type_) {}

    const Ogre::MovableObject *getMovableObject() { return movableObject; }
    const MovableObjectType getMovableObjectType() { return (MovableObjectType) type; }

    bool operator==(MovableObjectWithType &oth) { return movableObject == oth.getMovableObject() && type == oth.getMovableObjectType(); }
};

Ogre::TextureGpu* mDynamicCubemap; // We hold the shared pointer alive here so we don't get a dangling pointer in the createCubemapTexture() function when exiting.

typedef std::vector<MovableObjectWithType> ItemList;
ItemList itemList;

typedef std::vector<::FrameEndListenerExecutor*> FrameEndListenerExecutorList;
FrameEndListenerExecutorList frameEndListenerExecutorList;

void addToItemList(Ogre::MovableObject* item, MovableObjectType type)
{
//    ::Ogre::String s("addToItemList : " + item->getName() + " type: " + SSTR((int)type) + " itemList size: " + SSTR(itemList.size()) + "\n");
//    LOGI("%s", s.c_str());
    itemList.push_back(MovableObjectWithType(item, type));
}

void removeFromItemList(Ogre::MovableObject* item, MovableObjectType type)
{
//    ::Ogre::String s("removeFromItemList : " + item->getName() + " type: " + SSTR((int)type)  + " itemList size before removal: " + SSTR(itemList.size()) + "\n");
//    LOGI("%s", s.c_str());
    MovableObjectWithType movableObjectWithType(item, type);
    std::vector<MovableObjectWithType>::iterator it = itemList.begin();
    std::vector<MovableObjectWithType>::iterator end = itemList.end();
    while (it != end)
    {
        if ((*it) == movableObjectWithType)
        {
            it = itemList.erase(it);
            break;
        }
        else
        {
            ++it;
        }
    }
//    ::Ogre::String s1("itemList size after removal: " + SSTR(itemList.size()) + "\n");
//    LOGI("%s", s1.c_str());
}

void removeAllFromItemListWithType(MovableObjectType type)
{
//    ::Ogre::String s("removeAllFromItemListWithType : " + SSTR((int)type)  + " itemList size before removal: " + SSTR(itemList.size()) + "\n");
//    LOGI("%s", s.c_str());
    std::vector<MovableObjectWithType>::iterator it = itemList.begin();
    std::vector<MovableObjectWithType>::iterator end = itemList.end();
    while (it != end)
    {
        if (it->getMovableObjectType() == type)
        {
            it = itemList.erase(it);
            end = itemList.end();
        }
        else
        {
            ++it;
        }
    }
//    ::Ogre::String s1("itemList size after removal: " + SSTR(itemList.size()) + "\n");
//    LOGI("%s", s1.c_str());
}

// Should this ever be called???
/*void removeAllFromItemList()
{
    itemList.clear();
}*/

void addFrameEndListenerExecutor(::FrameEndListenerExecutor* frameEndListenerExecutor)
{
    frameEndListenerExecutorList.push_back(frameEndListenerExecutor);
}

void removeFrameEndListenerExecutor(::FrameEndListenerExecutor* frameEndListenerExecutor)
{
    frameEndListenerExecutorList.erase(std::remove(frameEndListenerExecutorList.begin(), frameEndListenerExecutorList.end(), frameEndListenerExecutor),
                                       frameEndListenerExecutorList.end());
    delete frameEndListenerExecutor;
}

void removeAllFrameEndListenerExecutors()
{
    std::vector<FrameEndListenerExecutor *>::iterator it = frameEndListenerExecutorList.begin();
    const std::vector<FrameEndListenerExecutor *>::iterator &end = frameEndListenerExecutorList.end();
    while (it != end)
    {
        delete *it;
        ++it;
    }
    frameEndListenerExecutorList.clear();
}

extern "C" {
JNIEXPORT void JNICALL
        Java_headwayent_blackholedarksun_BlackholeDarksunMain_glTest(JNIEnv *env, jobject instance);

#if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_ANDROID)

JNIEXPORT void JNICALL
        Java_headwayent_blackholedarksunonline_android_AndroidLauncher_setAssetManager(JNIEnv *env,
                                                                                       jobject instance,
                                                                                       jobject assetManager);
JNIEXPORT void JNICALL
Java_headwayent_blackholedarksunonline_android_AndroidLauncher_setGLSurfaceView(JNIEnv *env,
                                                                                jobject instance,
                                                                                jobject surfaceView);
#endif

JNIEXPORT void JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_pipeline_ENG_1RenderingThread_initializeNative(
        JNIEnv *env, jclass type, jint readingBufferSizeInBytes, jint writingBufferSizeInBytes, jint bufferCount);

JNIEXPORT void JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_pipeline_ENG_1RenderingThread_renderOneFrame(
        JNIEnv *env, jclass type, jobject readingBuffer, jobject writingBuffer, jint currentBuffer);

JNIEXPORT jint JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1IdString_createIdString__Ljava_lang_String_2(
        JNIEnv *env, jclass type, jstring s_);

JNIEXPORT jint JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1IdString_createIdString__I(
        JNIEnv *env, jclass type, jint val);

JNIEXPORT jint JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1IdString_append__ILjava_lang_String_2(
        JNIEnv *env, jclass type, jint hash, jstring oth_);

JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1OverlaySystemNativeWrapper_createOverlaySystem(
        JNIEnv *env, jclass type);

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1OverlaySystemNativeWrapper_destroyOverlaySystem(
        JNIEnv *env, jclass type, jlong ptr);

JNIEXPORT void JNICALL
        Java_headwayent_blackholedarksun_APP_1Game_setupResources(JNIEnv *env, jclass type, jstring path_, jstring filename_);

JNIEXPORT jlong JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1RootNativeWrapper_createRoot(
        JNIEnv *env, jclass type, jstring pluginFileName_, jstring configFileName_,
        jstring logFileName_);

JNIEXPORT void JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1RootNativeWrapper_destroyRoot(
        JNIEnv *env, jclass type, jlong ptr);

JNIEXPORT void JNICALL
        Java_headwayent_blackholedarksun_APP_1Game_loadResources__JLjava_lang_String_2(JNIEnv *env,
                                                                                       jclass type,
                                                                                       jlong rootPtr,
                                                                                       jstring path_);

JNIEXPORT void JNICALL
        Java_headwayent_blackholedarksun_APP_1Game_loadEssentialResources__JLjava_lang_String_2(JNIEnv *env,
                                                                                       jclass type,
                                                                                       jlong rootPtr,
                                                                                       jstring path_);

JNIEXPORT jlong JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1SceneManagerNativeWrapper_createSceneManager(
        JNIEnv *env, jclass type, jlong rootPtr, jshort typeMask, jint numThreads, jint threadCullingMethod,
        jstring name_);

JNIEXPORT void JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1SceneManagerNativeWrapper_destroySceneManager(
        JNIEnv *env, jclass type, jlong rootPtr, jlong ptr);

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1SceneManagerNativeWrapper_initSceneManager(
        JNIEnv *env, jclass type, jlong ptr, jstring zoneType_);

JNIEXPORT void JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1SceneManagerNativeWrapper_addRenderQueueListenerNative(
        JNIEnv *env, jclass type, jlong sceneManagerPtr, jlong renderQueueListener);

JNIEXPORT void JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1SceneManagerNativeWrapper_removeRenderQueueListenerNative(
        JNIEnv *env, jclass type, jlong sceneManagerPtr, jlong renderQueueListener);

JNIEXPORT void JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1RootNativeWrapper_initialiseNative(
        JNIEnv *env, jclass type, jlong rootPtr, jboolean autoCreateWindow, jstring windowTitle_,
        jstring customCapabilitiesConfig_);

JNIEXPORT jlong JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1RootNativeWrapper_getRenderSystemNative(
        JNIEnv *env, jclass type, jlong ptr);

JNIEXPORT void JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_glsles_GLRenderSystemNativeWrapper_setConfigOptionNative(
        JNIEnv *env, jclass type, jlong ptr, jstring name_, jstring value_);

JNIEXPORT jlong JNICALL
        Java_headwayent_hotshotengine_gorillagui_ENG_1TextureAtlas_loadTexture(JNIEnv *env, jclass type,
                                                                               jstring name_, jstring group_);

JNIEXPORT jint JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1TextureAtlas_getTextureWidth(JNIEnv *env, jclass type,
                                                                           jlong ptr);

JNIEXPORT jint JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1TextureAtlas_getTextureHeight(JNIEnv *env,
                                                                            jclass type,
                                                                            jlong ptr);

JNIEXPORT jint JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1TextureAtlas_createNinePatch(JNIEnv *env, jclass type,
                                                                           jlong ptr,
                                                                           jobject ninePatch);

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_ENG_1SceneManager_setShadowDirectionalLightExtrusionDistance__JF(
        JNIEnv *env, jclass type, jlong ptr, jfloat distance);

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_ENG_1SceneManager_setShadowFarDistance__JF(JNIEnv *env,
                                                                                  jclass type,
                                                                                  jlong ptr,
                                                                                  jfloat distance);

JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1SceneManagerNativeWrapper_createCamera__JLjava_lang_String_2ZZ(
        JNIEnv *env, jclass type, jlong ptr, jstring name_, jboolean isVisible,
        jboolean forCubemapping);

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CameraNativeWrapper_setPosition(
        JNIEnv *env, jclass type, jlong ptr, jfloat x, jfloat y, jfloat z);

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CameraNativeWrapper_lookAt(
        JNIEnv *env, jclass type, jlong ptr, jfloat x, jfloat y, jfloat z);

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CameraNativeWrapper_setNearClipDistance(
        JNIEnv *env, jclass type, jlong ptr, jfloat dist);

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CameraNativeWrapper_setFarClipDistance(
        JNIEnv *env, jclass type, jlong ptr, jfloat dist);

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CameraNativeWrapper_setAutoAspectRatio(
        JNIEnv *env, jclass type, jlong ptr, jboolean aspectRatio);

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CameraNativeWrapper_setAspectRatio__JF(
        JNIEnv *env, jclass type, jlong ptr, jfloat aspectRatio);

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CameraNativeWrapper_setFOVy__JF(
        JNIEnv *env, jclass type, jlong ptr, jfloat fovy);

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CameraNativeWrapper_setFixedYawAxis__JF(
        JNIEnv *env, jclass type, jlong ptr, jboolean fixed);

JNIEXPORT jstring JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1TextureAtlas_getTextureName(JNIEnv *env, jclass type,
                                                                          jlong ptr);

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1TextureAtlas_calculateSpriteCoordinates(JNIEnv *env,
                                                                                      jclass type,
                                                                                      jobject s,
                                                                                      jfloat mInverseTextureSizeX,
                                                                                      jfloat mInverseTextureSizeY);

JNIEXPORT void JNICALL
Java_headwayent_blackholedarksun_APP_1Game_showLoadingScreenNative(JNIEnv *env, jclass type,
                                                                   jlong renderWindowPtr, jfloat screenDensity);

JNIEXPORT void JNICALL
Java_headwayent_blackholedarksun_APP_1Game_hideLoadingScreenNative(JNIEnv *env, jclass type);

JNIEXPORT void JNICALL
Java_headwayent_blackholedarksun_APP_1Game_setOnScreenKeyboardVisible(JNIEnv *env, jclass type,
                                                                   jboolean visible, jlong gameViewController, jlong textDelegate);

JNIEXPORT jobjectArray JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1SilverBack_createByteBuffersNative(JNIEnv *env,
                                                                                 jclass type,
                                                                                 jint size,
                                                                                 jint count);

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1SilverBack_setMaterialName(JNIEnv *env, jclass type,
                                                                         jstring materialName_);

JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CompositorWorkspaceNativeWrapper_addWorkspace(
        JNIEnv *env, jclass type, jlong rootPtr, jlong sceneManagerPtr, jlong renderWindowPtr, jlong cameraPtr,
        jstring workspaceName_, jboolean enabled);

/*
* Class:     headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_DynamicCubemapCompositorWorkspaceNativeWrapper
* Method:    createDynamicCubemapWorkspace
* Signature: (JJJJLjava/lang/String;Z)J
*/
JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1DynamicCubemapCompositorWorkspaceNativeWrapper_createDynamicCubemapWorkspace(
    JNIEnv* env, jclass type, jlong rootPtr, jlong sceneManagerPtr, jlong renderWindowPtr, jlong cubeCameraPtr,
    jstring workspaceName_, jboolean enabled);

/*
* Class:     headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_DynamicCubemapCompositorWorkspaceNativeWrapper
* Method:    addWorkspace
* Signature: (JJJJLjava/lang/String;Z)J
*/
JNIEXPORT jlong JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1DynamicCubemapCompositorWorkspaceNativeWrapper_addWorkspace(
        JNIEnv *env, jclass type, jlong rootPtr, jlong sceneManagerPtr, jlong renderWindowPtr, jlong cameraPtr,
jstring workspaceName_, jboolean enabled);

/*
* Class:     headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_DynamicCubemapCompositorWorkspaceNativeWrapper
* Method:    createCubemapTexture
* Signature: ()J
*/
JNIEXPORT jlong JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1DynamicCubemapCompositorWorkspaceNativeWrapper_createCubemapTexture(
        JNIEnv *env, jclass type);

/*
* Class:     headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_DynamicCubemapCompositorWorkspaceNativeWrapper
* Method:    createCubemapCamera
* Signature: ()J
*/
JNIEXPORT jlong JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1DynamicCubemapCompositorWorkspaceNativeWrapper_createCubemapCamera(
        JNIEnv *env, jclass type, jlong sceneManagerPtr);

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_ENG_1RenderRoot_renderOneFrameNative(JNIEnv *env,
                                                                            jclass type,
                                                                            jlong rootPtr);

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_test_TestRenderingThread_testSlowCall1(
        JNIEnv *env, jclass type);

JNIEXPORT jint JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_test_TestRenderingThread_testSlowCall2(
        JNIEnv *env, jclass type, jint p);

JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_test_TestRenderingThread_testSlowCall3(
        JNIEnv *env, jclass type, jint p, jlong l);

JNIEXPORT jboolean JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_test_TestRenderingThread_testSlowCall4(
        JNIEnv *env, jclass type, jint p, jbyte b, jboolean boolParam, jshort s);

JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1ViewportNativeWrapper_getViewport(
        JNIEnv *env, jclass type, jlong renderWindowPtr);

extern "C"
JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1SilverBack_createDummyNode(JNIEnv *env, jclass type);

extern "C"
JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1SilverBack_destroyDummyNode(JNIEnv *env,
                                                                          jclass type);

JNIEXPORT void JNICALL
Java_headwayent_blackholedarksun_BlackholeDarksunMain_initJvmData(JNIEnv *env, jobject instance);

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_android_AndroidRenderWindow_setWindowProc(JNIEnv *env, jclass type,
                                                                        jclass windowsDisplayClass,
                                                                        jobject javaWindowProc);

}

JNIEXPORT void JNICALL
Java_headwayent_blackholedarksun_BlackholeDarksunMain_glTest(JNIEnv *env, jobject instance) {

//    LOGI("Long live the emperor!");

    Ogre::String pluginsPath;
    Ogre::String mResourcePath;

    const Ogre::AbiCookie abiCookie = Ogre::generateAbiCookie();
    Ogre::Root *mRoot = OGRE_NEW Ogre::Root( &abiCookie, 
									pluginsPath,
                                 mResourcePath + "ogre.cfg",
                                 mResourcePath + "Ogre.log" );
#if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_ANDROID)
    Ogre::ArchiveManager::getSingleton().addArchiveFactory( new Ogre::APKFileSystemArchiveFactory(mgr) );
    Ogre::ArchiveManager::getSingleton().addArchiveFactory( new Ogre::APKZipArchiveFactory(mgr) );
#endif

    mRoot->getRenderSystem()->setConfigOption( "sRGB Gamma Conversion", "Yes" );
    mRoot->initialise(false);

    int width   = 1280;
    int height  = 720;

    Ogre::NameValuePairList params;
    params.insert(std::make_pair("currentGLContext", "true"));
//    Ogre::String winHandle;
//    winHandle = Ogre::StringConverter::toString( (uintptr_t)window );
//    params.insert( std::make_pair("externalWindowHandle",  winHandle) );

    Ogre::String windowTitle("Window title");
    Ogre::Window *mRenderWindow = Ogre::Root::getSingleton().createRenderWindow( windowTitle, width, height,
                                                                   true, &params );

//    glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
//    glClear(GL_COLOR_BUFFER_BIT);
}

#if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_ANDROID)
JNIEXPORT void JNICALL
Java_headwayent_blackholedarksunonline_android_AndroidLauncher_setAssetManager(JNIEnv *env,
                                                                               jobject instance,
                                                                               jobject assetManager) {

    mgr = AAssetManager_fromJava(env, assetManager);

}

JNIEXPORT void JNICALL
Java_headwayent_blackholedarksunonline_android_AndroidLauncher_setGLSurfaceView(JNIEnv *env,
                                                                                jobject instance,
                                                                                jobject surfaceView) {

    window = ANativeWindow_fromSurface(env, surfaceView);
    LOGI("Long live the emperor!");
    assert(window);

}
#endif

void addResourceLocation( const Ogre::String &archName, const Ogre::String &typeName,
                          const Ogre::String &secName )
{
    // The archName already contains the path. No need for the macBundlePath().
//#if( OGRE_PLATFORM == OGRE_PLATFORM_APPLE ) || ( OGRE_PLATFORM == OGRE_PLATFORM_APPLE_IOS )
//    // OS X does not set the working directory relative to the app,
//    // In order to make things portable on OS X we need to provide
//    // the loading with it's own bundle path location
//    Ogre::String resourcePath("path: " + Ogre::macBundlePath() + "/" + archName + " typeName: " + typeName + " secName:" + secName);
//    LOGI("%s\n", resourcePath.c_str());
//    Ogre::ResourceGroupManager::getSingleton().addResourceLocation(
//                Ogre::String(Ogre::macBundlePath() + "/" + archName), typeName, secName);
//#else
    Ogre::ResourceGroupManager::getSingleton().addResourceLocation(
            archName, typeName, secName);
//#endif
}

void setupResources(const Ogre::String &path, const Ogre::String &filename)
{
    // Load resource paths from config file
    Ogre::ConfigFile cf;
    //cf.loadFromResourceSystem(mResourcePath + "resources2.cfg", Ogre::ResourceGroupManager::DEFAULT_RESOURCE_GROUP_NAME, "\t;=", true );
    cf.load(path + "/" + filename);

    // Go through all sections & settings in the file
    Ogre::ConfigFile::SectionIterator seci = cf.getSectionIterator();

    Ogre::String secName, typeName, archName;
    while( seci.hasMoreElements() )
    {
        secName = seci.peekNextKey();
        Ogre::ConfigFile::SettingsMultiMap *settings = seci.getNext();

        if( secName != "Hlms" )
        {
            Ogre::ConfigFile::SettingsMultiMap::iterator i;
            for (i = settings->begin(); i != settings->end(); ++i)
            {
                typeName = i->first;
                archName = i->second;
                addResourceLocation( path + archName, typeName, secName );
            }
        }
    }
}

void registerHlms(Ogre::Root* root, Ogre::String &path)
{
    Ogre::ConfigFile cf;
    cf.load( path + "/resources2.cfg" );

    Ogre::String rootHlmsFolder = path + cf.getSetting( "DoNotUseAsResource", "Hlms", "" );

    if( rootHlmsFolder.empty() )
        rootHlmsFolder = "./";
    else if( *(rootHlmsFolder.end() - 1) != '/' )
        rootHlmsFolder += "/";

    Ogre::HlmsUnlit *hlmsUnlit = 0;
    Ogre::HlmsPbs *hlmsPbs = 0;

    Ogre::RenderSystem *renderSystem = root->getRenderSystem();

    Ogre::String shaderSyntax = "GLSL";
    if( renderSystem->getName() == "Direct3D11 Rendering Subsystem" )
        shaderSyntax = "HLSL";
    else if( renderSystem->getName() == "OpenGL ES 3.x Rendering Subsystem" )
        shaderSyntax = "GLSLES3";
    else if( renderSystem->getName() == "Metal Rendering Subsystem" )
        shaderSyntax = "Metal";

    //For retrieval of the paths to the different folders needed
    Ogre::String mainFolderPath;
    Ogre::StringVector libraryFoldersPaths;
    Ogre::StringVector::const_iterator libraryFolderPathIt;
    Ogre::StringVector::const_iterator libraryFolderPathEn;

    Ogre::ArchiveManager &archiveManager = Ogre::ArchiveManager::getSingleton();

    {
        //Create & Register HlmsUnlit
        //Get the path to all the subdirectories used by HlmsUnlit
        Ogre::HlmsUnlit::getDefaultPaths( mainFolderPath, libraryFoldersPaths );
        Ogre::Archive *archiveUnlit = archiveManager.load( rootHlmsFolder + mainFolderPath,
                                                           "FileSystem", true );
        Ogre::ArchiveVec archiveUnlitLibraryFolders;
        libraryFolderPathIt = libraryFoldersPaths.begin();
        libraryFolderPathEn = libraryFoldersPaths.end();
        while( libraryFolderPathIt != libraryFolderPathEn )
        {
            Ogre::Archive *archiveLibrary =
                    archiveManager.load( rootHlmsFolder + *libraryFolderPathIt, "FileSystem", true );
            archiveUnlitLibraryFolders.push_back( archiveLibrary );
            ++libraryFolderPathIt;
        }

        //Create and register the unlit Hlms
        hlmsUnlit = OGRE_NEW Ogre::HlmsUnlit( archiveUnlit, &archiveUnlitLibraryFolders );
        Ogre::Root::getSingleton().getHlmsManager()->registerHlms( hlmsUnlit );
    }

    {
        //Create & Register HlmsPbs
        //Do the same for HlmsPbs:
        Ogre::HlmsPbs::getDefaultPaths( mainFolderPath, libraryFoldersPaths );
        Ogre::Archive *archivePbs = archiveManager.load( rootHlmsFolder + mainFolderPath,
                                                         "FileSystem", true );

        //Get the library archive(s)
        Ogre::ArchiveVec archivePbsLibraryFolders;
        libraryFolderPathIt = libraryFoldersPaths.begin();
        libraryFolderPathEn = libraryFoldersPaths.end();
        while( libraryFolderPathIt != libraryFolderPathEn )
        {
            Ogre::Archive *archiveLibrary =
                    archiveManager.load( rootHlmsFolder + *libraryFolderPathIt, "FileSystem", true );
            archivePbsLibraryFolders.push_back( archiveLibrary );
            ++libraryFolderPathIt;
        }

        //Create and register
        hlmsPbs = OGRE_NEW Ogre::HlmsPbs( archivePbs, &archivePbsLibraryFolders );
        Ogre::Root::getSingleton().getHlmsManager()->registerHlms( hlmsPbs );
    }

    if( renderSystem->getName() == "Direct3D11 Rendering Subsystem" )
    {
        //Set lower limits 512kb instead of the default 4MB per Hlms in D3D 11.0
        //and below to avoid saturating AMD's discard limit (8MB) or
        //saturate the PCIE bus in some low end machines.
        bool supportsNoOverwriteOnTextureBuffers;
        renderSystem->getCustomAttribute( "MapNoOverwriteOnDynamicBufferSRV",
                                          &supportsNoOverwriteOnTextureBuffers );

        if( !supportsNoOverwriteOnTextureBuffers )
        {
            hlmsPbs->setTextureBufferDefaultSize( 512 * 1024 );
            hlmsUnlit->setTextureBufferDefaultSize( 512 * 1024 );
        }
    }
}

void loadResources(Ogre::Root* root, Ogre::String &path)
{
//    registerHlms(root, path);

//#if (HOTSHOT_MODE == HOTSHOT_MODE_CLIENT)
    // Initialise, parse scripts etc
    Ogre::ResourceGroupManager &manager = Ogre::ResourceGroupManager::getSingleton();
    manager.initialiseResourceGroup(path.c_str(), false);

    // UGLY HACK NEED TO PRELOAD ALL v1 EFFECTS!!!
    // "Called RenderQueue::frameEnded mid-render. This may happen if VaoManager::_update got "
    //     "called after RenderQueue::renderPassPrepare but before RenderQueue::render returns. Please "
    //     "move that VaoManager::_update call outside, otherwise we cannot guarantee rendering will "
    //     "be glitch-free, as the BufferPacked buffers from Hlms may be bound at the wrong offset. "
    //     "For more info see https://github.com/OGRECave/ogre-next/issues/33 and "
    //     "https://forums.ogre3d.org/viewtopic.php?f=25&t=95092#p545907"
    if (path == "Rest")
    {
        Ogre::MaterialPtr material = Ogre::MaterialManager::getSingleton().getByName("portal_mat", path);
        material->load(false);
        Ogre::HlmsManager* hlmsManager = Ogre::Root::getSingleton().getHlmsManager();
        Ogre::HlmsDatablock* explosionDatablock = hlmsManager->getDatablock("Fx/ExplosionMaterial");
        explosionDatablock->preload();
        Ogre::HlmsDatablock* countermeasureDatablock = hlmsManager->getDatablock("Fx/CountermeasureMaterial");
        countermeasureDatablock->preload();
        Ogre::HlmsDatablock* movementParticleDatablock = hlmsManager->getDatablock("Fx/movement_particle_mat");
        movementParticleDatablock->preload();
    }
//#endif
}

void loadEssentialResources(Ogre::Root* root, Ogre::String &path)
{
    registerHlms(root, path);

//#if (HOTSHOT_MODE == HOTSHOT_MODE_CLIENT)
    // Initialise, parse scripts etc
    Ogre::ResourceGroupManager &manager = Ogre::ResourceGroupManager::getSingleton();
    manager.initialiseResourceGroup("Essential", false);
//#endif
}


struct OverlayData
{
    Ogre::String name;
    long long int ptr;
	long long int datablockPtr;
	Ogre::String materialName;
    float left;
    float top;
    float width;
    float height;
    std::vector<OverlayData> children;
    Ogre::RealRect clippingRegion;
    unsigned char type;
    unsigned char metrics;

    OverlayData() : name(""), ptr(0), datablockPtr(0), left(0), top(0), width(0), height(0), type(0), metrics(0) {}

};

void extractOverlayData(::Ogre::v1::OverlayElement* overlayElement, OverlayData* overlayData)
{
    const Ogre::String &typeName = overlayElement->getTypeName();
    if (typeName.compare("Panel") == 0)
    {
        overlayData->type = 0;
    }
    else if (typeName.compare("TextArea") == 0)
    {
        overlayData->type = 1;
    }
	else
	{
		::Ogre::String s("Could not find overlay element type: " + SSTR(typeName));
		LOGI("%s", s.c_str());
		exit(-1);
	}
    overlayData->name = overlayElement->getName();
    overlayData->ptr = getPointerAsLong(overlayElement);
	overlayData->datablockPtr = getPointerAsLong(overlayElement->getDatablock());
	overlayData->materialName = overlayElement->getMaterialName();
    overlayData->metrics = overlayElement->getMetricsMode();
    overlayData->left = overlayElement->getLeft();
    overlayData->top = overlayElement->getTop();
    overlayData->width = overlayElement->getWidth();
    overlayData->height = overlayElement->getHeight();
    overlayElement->_getClippingRegion(overlayData->clippingRegion);
    if (overlayElement->isContainer())
    {
        Ogre::v1::OverlayContainer *overlayContainer = (Ogre::v1::OverlayContainer*) overlayElement;
        Ogre::v1::OverlayContainer::ChildIterator it = overlayContainer->getChildIterator();

        while (it.hasMoreElements())
        {
            OverlayData nextData;
            Ogre::v1::OverlayElement *childElement = it.getNext();
            extractOverlayData(childElement, &nextData);
            overlayData->children.push_back(nextData);
        }
    }
}

void writeOverlayDataFields(char** wbuf, OverlayData* overlayData)
{
    writePtr(wbuf, overlayData->ptr);
	writePtr(wbuf, overlayData->datablockPtr);
    write(wbuf, overlayData->type);
    writeString(wbuf, overlayData->name);
    LOGI("writeOverlayDataFields with materialName: %s \n", overlayData->materialName.c_str());
    writeString(wbuf, overlayData->materialName);
    write(wbuf, overlayData->metrics);
    write(wbuf, overlayData->left);
    write(wbuf, overlayData->top);
    write(wbuf, overlayData->width);
    write(wbuf, overlayData->height);
    write(wbuf, overlayData->clippingRegion.left);
    write(wbuf, overlayData->clippingRegion.top);
    write(wbuf, overlayData->clippingRegion.right);
    write(wbuf, overlayData->clippingRegion.bottom);
}

void writeOverlayData(char** wbuf, OverlayData* overlayData)
{
	writeOverlayDataFields(wbuf, overlayData);
	write(wbuf, (int) overlayData->children.size());
    if (overlayData->children.size() == 0)
    {
        return;
    }
    std::vector<OverlayData>::iterator dataIt = overlayData->children.begin();
    const std::vector<OverlayData>::iterator &dataEndIt = overlayData->children.end();
    while (dataIt != dataEndIt)
    {
        OverlayData &childOverlayData = *dataIt;
//        writeOverlayDataFields(wbuf, &childOverlayData);
        writeOverlayData(wbuf, &childOverlayData);
        ++dataIt;
    }
}

char *writeOverlay(char *wbuf, Ogre::v1::Overlay *overlay) {
    LOGI("writeOverlay with name: %s \n", overlay->getName().c_str());
    Ogre::v1::Overlay::Overlay2DElementsIterator it = overlay->get2DElementsIterator();
    std::vector<OverlayData> overlayDataList;

    while (it.hasMoreElements()) {
        OverlayData overlayData;
        Ogre::v1::OverlayContainer *overlayContainer = it.getNext();
		// We're also going to need the datablock so initialize and update the container.
		overlayContainer->initialise();
		overlayContainer->_update();
        extractOverlayData(overlayContainer, &overlayData);
        overlayDataList.push_back(overlayData);
    }

    // We only create one overlay per call for now.
    write(&wbuf, 1);
    writePtr(&wbuf, getPointerAsLong(overlay));
    write(&wbuf, (int) overlayDataList.size());
    std::vector<OverlayData>::iterator dataIt = overlayDataList.begin();
    const std::vector<OverlayData>::iterator &dataEndIt = overlayDataList.end();
    while (dataIt != dataEndIt) {
        OverlayData &overlayData = *dataIt;
        writeOverlayData(&wbuf, &overlayData);
//        std::vector<OverlayData>::iterator childIt = overlayData.children.begin();
//        const std::vector<OverlayData>::iterator &childEndIt = overlayData.children.end();
//        while (childIt != childEndIt) {
//            OverlayData &childOverlayData = *childIt;
//            writeOverlayData(&wbuf, &childOverlayData);
//            ++childIt;
//        }
        ++dataIt;
    }
    return wbuf;
}

void extractMessages(JNIEnv *env, char* buf, char* frameIdPos)
{
    // At the address of wbuf we write the frameId if any.
    char* wbuf = (frameIdPos + 1);
    bool renderedOneFrame = false;
    while (*buf != 0)
    {
		// Get rid of the dumbass sign extension.

	    unsigned char* callChar = reinterpret_cast<unsigned char*>(buf++);
        NativeCallsList call = static_cast<NativeCallsList>(*callChar);
//        Ogre::String nativeCallStr("currentCall:  " + SSTR(call) + "\n");
//        LOGI("%s", nativeCallStr.c_str());

//        long long renderOneFrameBeginTime = getNanoTime(env);
        switch (call)
        {
//            case NEWROOT:
//            {
//                Ogre::String pluginsPath = readString(&buf);
//                Ogre::String configPath = readString(&buf);
//                Ogre::String logPath = readString(&buf);
//                Ogre::Root *root = OGRE_NEW Ogre::Root( pluginsPath, configPath, logPath );
//                savePointer(&wbuf, root);
//            }
//                break;
            case ROOT_CREATERENDERWINDOW:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<int> bufferReadInt;
                BufferRead<bool> bufferReadBool;
                alignPointer(&buf, 4);
                long long int rootPtr = bufferReadLong.read(&buf);
                #if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_IOS)
                long long int gameViewControllerPtr = bufferReadLong.read(&buf);
                void *gameViewController = getLongAsPointer(gameViewControllerPtr);
                #endif
                Ogre::String windowName = readString(&buf);
                int width = bufferReadInt.read(&buf);
                int height = bufferReadInt.read(&buf);
                bool fullscreen = bufferReadBool.read(&buf);
                const std::map<Ogre::String, Ogre::String> &map = readMapStringString(&buf);
                Ogre::Root *root = (Ogre::Root*) getLongAsPointer(rootPtr);
                Ogre::NameValuePairList list;
                for (std::map<Ogre::String, Ogre::String>::const_iterator it = map.begin(); it != map.end(); ++it)
                {
                    list.insert(std::make_pair(it->first, it->second));
                }
                Ogre::Window *renderWindow = root->createRenderWindow(windowName, width, height, fullscreen,
                                                                            &list);
//                Ogre::Viewport *viewport = renderWindow->getViewport(0);
//                int actLeft, actTop, actWidth, actHeight;
//                viewport->getActualDimensions(actLeft, actTop, actWidth, actHeight);
                alignPointer(&wbuf, 4);
                write(&wbuf, getPointerAsLong(renderWindow));
                #if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_IOS)
                LOGI("%s", "before setView(renderWindow, gameViewController)");
                setView(renderWindow, gameViewController);
                LOGI("%s", "after setView(renderWindow, gameViewController)");
                #endif
#if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_WIN32)
		        Ogre::VulkanWin32Window* win32RenderWindow = (Ogre::VulkanWin32Window*) renderWindow;
//				Ogre::Win32Window *win32RenderWindow = (Ogre::Win32Window*) renderWindow;
//				Ogre::Win32EGLWindow *win32RenderWindow = (Ogre::Win32EGLWindow*) renderWindow;
		        HWND windowHandle = win32RenderWindow->getWindowHandle();
				HDC hdc = win32RenderWindow->getHDC();
				write(&wbuf, (long long) windowHandle);
				write(&wbuf, (long long) hdc);
#endif
//                write(&wbuf, getPointerAsLong(viewport));
//                write(&wbuf, actLeft);
//                write(&wbuf, actTop);
//                write(&wbuf, actWidth);
//                write(&wbuf, actHeight);
            }
                break;
            case SCREEN_CREATE:
            {
                BufferRead<unsigned char> charBufferRead;
                BufferRead<long long> bufferReadLong;
                unsigned int bufferLen = charBufferRead.read(&buf);
                alignPointer(&buf, 4);
//                Hotshot::GorillaGUI::createAtlasPtrList(bufferLen);
                Hotshot::Screen *screen = new Hotshot::Screen(bufferLen);
                for (unsigned int i = 0; i < bufferLen; ++i)
                {
                    long long int atlasPtr = bufferReadLong.read(&buf);
                    Ogre::TextureGpu *atlasTexture = (Ogre::TextureGpu*) getLongAsPointer(atlasPtr);
//                    const Ogre::String &atlasName = atlasTexture->getName();
//                    Hotshot::GorillaGUI::setAtlasPtr(atlasTexture, i);
                    screen->setAtlasPtr(atlasTexture, i);
                }
                writePtr(&wbuf, getPointerAsLong(screen));
//                alignPointer(&wbuf, 4);
//                write(&wbuf, getPointerAsLong(screen));
            }
                break;
            case SCREEN_DESTROY:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int screenPtr = bufferReadLong.read(&buf);
                Hotshot::Screen *screen = (Hotshot::Screen*) getLongAsPointer(screenPtr);
                delete screen;
//                Hotshot::GorillaGUI::destroyAtlasPtrList();
            }
                break;
            case SCREEN_CREATELAYER:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> charBufferRead;
                alignPointer(&buf, 4);
                long long int screenPtr = bufferReadLong.read(&buf);
                long long int texturePtr = bufferReadLong.read(&buf);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                unsigned char queueGroupId = charBufferRead.read(&buf);
                Hotshot::Screen *screen = (Hotshot::Screen*) getLongAsPointer(screenPtr);
                Ogre::TextureGpu *texture = (::Ogre::TextureGpu*) getLongAsPointer(texturePtr);
                Ogre::SceneManager *sceneManager = (::Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                Hotshot::ScreenRenderable *screenRenderable = screen->createScreenRenderable(
                        texture, sceneManager, queueGroupId);
                writePtr(&wbuf, getPointerAsLong(screenRenderable));
//                alignPointer(&wbuf, 4);
//                write(&wbuf, getPointerAsLong(screenRenderable));
            }
                break;
            case SCREEN_DESTROYLAYER:
            {
                BufferRead<unsigned char> charBufferRead;
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int screenPtr = bufferReadLong.read(&buf);
                Hotshot::Screen *screen = (Hotshot::Screen*) getLongAsPointer(screenPtr);
                unsigned char queueGroupIdLen = charBufferRead.read(&buf);
                for (int i = 0; i < queueGroupIdLen; ++i)
                {
                    unsigned char queueGroupId = charBufferRead.read(&buf);
                    screen->destroyScreenRenderable(queueGroupId);
                }
//                Hotshot::ScreenRenderable *screenRenderable = (Hotshot::ScreenRenderable*) getLongAsPointer(screenRenderablePtr);
//                Hotshot::GorillaGUI::destroyScreenRenderable(screenRenderable);
            }
                break;
            case SCREEN_RENDERONCE:
            {
                // Render each screen renderable in group order.
//                Hotshot::GorillaGUI::renderOnce();
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int screenPtr = bufferReadLong.read(&buf);
                Hotshot::Screen *screen = (Hotshot::Screen*) getLongAsPointer(screenPtr);
                screen->renderOnce();
            }
                break;
            case SCREEN_UPDATEVERTEXLISTSIZE:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<int> bufferReadInt;
                BufferRead<unsigned char> bufferReadChar;
                alignPointer(&buf, 4);
                long long int screenPtr = bufferReadLong.read(&buf);
                int vertexListSize = bufferReadInt.read(&buf);
                unsigned char bufferNum = bufferReadChar.read(&buf);
                unsigned char queueGroupId = bufferReadChar.read(&buf);
                Hotshot::Screen *screen = (Hotshot::Screen*) getLongAsPointer(screenPtr);
                // Ogre::String s = "SCREEN_UPDATEVERTEXLISTSIZE bufferNum: " + SSTR((int)bufferNum) + "\n";
                // LOGI("%s", s.c_str());
                screen->fillVertexBuffer(vertexListSize, queueGroupId, Hotshot::GorillaGUI::getBuffer(bufferNum));
//                Hotshot::ScreenRenderable *screenRenderable = (Hotshot::ScreenRenderable*) getLongAsPointer(screenRenderablePtr);
//                screenRenderable->fillVertexBuffer(vertexListSize, Hotshot::GorillaGUI::getBuffer(bufferNum));
            }
                break;
            case ROOT_RENDERONEFRAME:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int rootPtr = bufferReadLong.read(&buf);
                Ogre::Root *root = (Ogre::Root*) getLongAsPointer(rootPtr);
#if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_WIN32)
				Ogre::WindowEventUtilities::messagePump();
#endif
#if (HOTSHOT_MODE == HOTSHOT_MODE_CLIENT)
                // long long renderOneFrameBeginTime = getNanoTime(env);
                root->renderOneFrame();
                // long long renderOneFrameEndTime = getNanoTime(env);
                // long long timeDiff = renderOneFrameEndTime - renderOneFrameBeginTime;
                // timeDiff /= 1000000;
                // Ogre::String s("renderOneFrame time:  " + SSTR(timeDiff) + " LastFPS: " + SSTR(root->getFrameStats()->getFps()) + "\n");
                // LOGI("%s", s.c_str());
                renderedOneFrame = true;
#elif (HOTSHOT_MODE == HOTSHOT_MODE_SERVER)
                Ogre::SceneManager *sceneManager = root->_getCurrentSceneManager();
                sceneManager->updateSceneGraph();
                sceneManager->clearFrameData();

                // Enable the following code when we will have animations on server side.
//                Ogre::ObjectMemoryManager &memoryManager= sceneManager->_getEntityMemoryManager( (Ogre::SceneMemoryMgrTypes)type );
//
//                const size_t numRenderQueues = memoryManager.getNumRenderQueues();
//                for( size_t i=0; i<numRenderQueues; ++i )
//                {
//                    if( sceneManager->getRenderQueue()->getRenderQueueMode( i ) == Ogre::RenderQueue::FAST )
//                        continue;
//                    Ogre::ObjectData objData;
//                    const size_t totalObjs = memoryManager.getFirstObjectData( objData, i );
//                    for( size_t j=0; j<totalObjs; j += ARRAY_PACKED_REALS )
//                    {
//                        for( size_t k=0; k<ARRAY_PACKED_REALS; ++k )
//                        {
//                            Ogre::uint32 * RESTRICT_ALIAS visibilityFlags = objData.mVisibilityFlags;
//
//                            if( visibilityFlags[k] & Ogre::VisibilityFlags::LAYER_VISIBILITY )
//                            {
//                                objData.mOwner[k]->_updateRenderQueue( sceneManager->getRenderQueue(), camera, camera );
//                            }
//                        }
//                        objData.advancePack();
//                    }
//                }
#endif


            }
                break;
            case SCREEN_SETVISIBLE:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<bool> bufferReadBool;
                alignPointer(&buf, 4);
                long long int screenPtr = bufferReadLong.read(&buf);
                bool visible = bufferReadBool.read(&buf);
                Hotshot::Screen *screen = (Hotshot::Screen*) getLongAsPointer(screenPtr);
                screen->setVisible(visible);
//                Hotshot::ScreenRenderable *screenRenderable = (Hotshot::ScreenRenderable*) getLongAsPointer(screenRenderablePtr);
//                screenRenderable->setScreenRenderableVisible(visible);
            }
                break;
            case CREATE_RAY_QUERY:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> bufferReadChar;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                unsigned char ptrFlag = getPointerFlag(&buf);
                alignPointer(&buf, 4);
                long long int raySceneQueryPtr = bufferReadLong.read(&buf);
                const Ogre::Vector4 &origin = readVector4DAsVec(&buf);
                const Ogre::Vector4 &dir = readVector4DAsVec(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                Ogre::RaySceneQuery *rayQuery = sceneManager->createRayQuery(
                        Ogre::Ray(Ogre::Vector3(origin.x, origin.y, origin.z), Ogre::Vector3(dir.x, dir.y, dir.z)));
                putInPointerMap(raySceneQueryPtr, rayQuery, "create_ray_query");
                writePtr(&wbuf, getPointerAsLong(rayQuery));
//                alignPointer(&wbuf, 4);
//                write(&wbuf, getPointerAsLong(rayQuery));
            }
                break;
            case DESTROY_RAY_QUERY:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int raySceneQueryPtr = bufferReadLong.read(&buf);
                Ogre::RaySceneQuery *raySceneQuery = (Ogre::RaySceneQuery*) getNativePointer(raySceneQueryPtr, ptrJustCreated);
                sceneManager->destroyQuery(raySceneQuery);
                removePointerFromMap(raySceneQueryPtr, ptrJustCreated);
            }
                break;
            case RAY_QUERY_SETSORTBYDISTANCE:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<bool> bufferReadBool;
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int raySceneQueryPtr = bufferReadLong.read(&buf);
                bool sortByDistance = bufferReadBool.read(&buf);
                Ogre::RaySceneQuery *raySceneQuery = (Ogre::RaySceneQuery*) getNativePointer(raySceneQueryPtr, ptrJustCreated);
                raySceneQuery->setSortByDistance(sortByDistance);
            }
                break;
            case RAY_QUERY_EXECUTE:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int raySceneQueryPtr = bufferReadLong.read(&buf);
                // If the ray scene query fails it's because the aabbs haven't been updated for all objects.
                // This usually happens because you have just created a new item in the scene for which no
                // updateSceneGraph() happened. In order to test if this is the case uncomment the next line.
                // If the problem goes away you know who the culprit was.
                // Make sure you only execute ray queries after everything has been update in the scene graph!!!
//                Ogre::Root::getSingleton().renderOneFrame();
                Ogre::RaySceneQuery *raySceneQuery = (Ogre::RaySceneQuery*) getNativePointer(raySceneQueryPtr, ptrJustCreated);
                addFrameEndListenerExecutor(new RayQueryExecuteFrameEndListenerExecutor(raySceneQuery, sceneManager));
//                Ogre::RaySceneQueryResult & raySceneQueryResult = raySceneQuery->execute();
//                write(&wbuf, (int) raySceneQueryResult.size());
//                Ogre::RaySceneQueryResult::iterator it = raySceneQueryResult.begin();
//                const Ogre::RaySceneQueryResult::iterator &end = raySceneQueryResult.end();
//                while (it != end)
//                {
//                    Ogre::RaySceneQueryResultEntry &entry = *it;
//                    Ogre::Real distance = entry.distance;
//                    Ogre::MovableObject *movableObject = entry.movable;
//                    write(&wbuf, distance);
//                    writePtr(&wbuf, getPointerAsLong(movableObject));
////                    alignPointer(&wbuf, 4);
////                    write(&wbuf, getPointerAsLong(movableObject));
//                    ++it;
//                }
            }
                break;
            case GET_VIEWPORT:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int renderWindowPtr = bufferReadLong.read(&buf);
                Ogre::Window *renderWindow = (::Ogre::Window*) getLongAsPointer(renderWindowPtr);
                Ogre::Viewport* viewport = Ogre::Root::getSingleton().getRenderSystem()->getCurrentRenderViewports();
                // Ogre::Viewport *viewport = renderWindow->getViewport(0);
                int actLeft, actTop, actWidth, actHeight;
                viewport->getActualDimensions(actLeft, actTop, actWidth, actHeight);
//                alignPointer(&wbuf, 4);
//                write(&wbuf, getPointerAsLong(renderWindow));
//                write(&wbuf, getPointerAsLong(viewport));
                write(&wbuf, actLeft);
                write(&wbuf, actTop);
            	// TODO actualDimensions are not yet ready here.
                write(&wbuf, renderWindow->getTexture()->getWidth()/*actWidth*/);
                write(&wbuf, renderWindow->getTexture()->getHeight()/*actHeight*/);
                ::Ogre::String prevDataStr("renderWindow width: " + SSTR(renderWindow->getTexture()->getWidth()) + " height: " + SSTR(renderWindow->getTexture()->getHeight()));
                LOGI("%s\n", prevDataStr.c_str());
            }
                break;
            case SCENEMANAGER_ITEM_CREATE:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<char> bufferReadChar;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int itemPtr = bufferReadLong.read(&buf);
                const Ogre::String &meshName = readString(&buf);
                char workflow = bufferReadChar.read(&buf);
                Ogre::Item *item = sceneManager->createItem(meshName);
                addToItemList(item, MovableObjectType::ITEM);
                Ogre::String name(std::string("create_item ") + meshName);
                putInPointerMap(itemPtr, item, name.c_str());
                writePtr(&wbuf, getPointerAsLong(item));
//                alignPointer(&wbuf, 4);
//                write(&wbuf, getPointerAsLong(item));
                size_t itemsCount = item->getNumSubItems();
                for (size_t i = 0; i < itemsCount; ++i)
                {
                    Ogre::SubItem *pSubItem = item->getSubItem(i);
                    if (workflow != (char) Ogre::HlmsPbsDatablock::Workflows::SpecularWorkflow)
                    {
                        Ogre::HlmsPbsDatablock *datablock = (Ogre::HlmsPbsDatablock*) pSubItem->getDatablock();
                        datablock->setWorkflow((Ogre::HlmsPbsDatablock::Workflows) workflow);
                        datablock->setTexture(Ogre::PBSM_REFLECTION, "DynamicCubemap");
                        // For Ogre 2.3 look.
                        // datablock->setBrdf(Ogre::PbsBrdf::DefaultHasDiffuseFresnel);

                    }
                    write(&wbuf, getPointerAsLong(pSubItem));
                }


            }
                break;
            case SCENEMANAGER_ITEM_CREATE_ALL_PARAMS:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<char> bufferReadChar;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int itemPtr = bufferReadLong.read(&buf);
                const Ogre::String &meshName = readString(&buf);
                const Ogre::String &groupName = readString(&buf);
                char sceneMemoryMgrTypeChar = bufferReadChar.read(&buf);
                char workflow = bufferReadChar.read(&buf);
                Ogre::Item *item = sceneManager->createItem(meshName, groupName,
                                                            sceneMemoryMgrTypeChar == 0 ? ::Ogre::SCENE_DYNAMIC : ::Ogre::SCENE_STATIC);
                addToItemList(item, MovableObjectType::ITEM);
                Ogre::String name(std::string("create_item ") + meshName);
                putInPointerMap(itemPtr, item, name.c_str());
                writePtr(&wbuf, getPointerAsLong(item));
//                alignPointer(&wbuf, 4);
//                write(&wbuf, getPointerAsLong(item));
                size_t itemsCount = item->getNumSubItems();
                for (size_t i = 0; i < itemsCount; ++i)
                {
                    Ogre::SubItem *pSubItem = item->getSubItem(i);
                    if (workflow != (char) Ogre::HlmsPbsDatablock::Workflows::SpecularWorkflow)
                    {
                        Ogre::HlmsPbsDatablock *datablock = (Ogre::HlmsPbsDatablock*) pSubItem->getDatablock();
                        datablock->setWorkflow((Ogre::HlmsPbsDatablock::Workflows) workflow);
                        datablock->setTexture(Ogre::PBSM_REFLECTION, "DynamicCubemap");
                        // For Ogre 2.3 look.
                        // datablock->setBrdf(Ogre::PbsBrdf::DefaultHasDiffuseFresnel);
                    }
                    write(&wbuf, getPointerAsLong(pSubItem));
                }
            }
                break;
            case SCENEMANAGER_ITEM_DESTROY:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int itemPtr = bufferReadLong.read(&buf);
                Ogre::Item *item = (Ogre::Item*) getNativePointer(itemPtr, ptrJustCreated);
                removeFromItemList(item, MovableObjectType::ITEM);
                sceneManager->destroyItem(item);
                removePointerFromMap(itemPtr, ptrJustCreated);
            }
                break;
            case SCENEMANAGER_ITEM_DESTROY_ALL:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                removeAllFromItemListWithType(MovableObjectType::ITEM);
                sceneManager->destroyAllItems();
            }
                break;
            case SCENEMANAGER_CREATE_PARTICLE_SYSTEM_STRING:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int particleSystemPtr = bufferReadLong.read(&buf);
                const Ogre::String &templateName = readString(&buf);
                Ogre::ParticleSystem *pParticleSystem = sceneManager->createParticleSystem(templateName);
                Ogre::String name(std::string("create_particle_system ") + templateName);
                putInPointerMap(particleSystemPtr, pParticleSystem, name.c_str());
                writePtr(&wbuf, getPointerAsLong(pParticleSystem));
//                alignPointer(&wbuf, 4);
//                write(&wbuf, getPointerAsLong(pParticleSystem));
            }
                break;
            case SCENEMANAGER_CREATE_PARTICLE_SYSTEM_QUOTA_RESOURCE_GROUP:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<int> bufferReadint;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int particleSystemPtr = bufferReadLong.read(&buf);
                int quota = bufferReadint.read(&buf);
                const Ogre::String &resourceGroup = readString(&buf);
                Ogre::ParticleSystem *pParticleSystem = sceneManager->createParticleSystem(quota, resourceGroup);
                Ogre::String name(std::string("create_particle_system ") + pParticleSystem->getName());
                putInPointerMap(particleSystemPtr, pParticleSystem, name.c_str());
                writePtr(&wbuf, getPointerAsLong(pParticleSystem));
//                alignPointer(&wbuf, 4);
//                write(&wbuf, getPointerAsLong(pParticleSystem));
            }
                break;
            case SCENEMANAGER_DESTROY_PARTICLE_SYSTEM:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int particleSystemPtr = bufferReadLong.read(&buf);
                Ogre::ParticleSystem *particleSystem= (Ogre::ParticleSystem*) getNativePointer(particleSystemPtr, ptrJustCreated);
                sceneManager->destroyParticleSystem(particleSystem);
                removePointerFromMap(particleSystemPtr, ptrJustCreated);
            }
                break;
            case SCENEMANAGER_DESTROY_ALL_PARTICLE_SYSTEMS:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                sceneManager->destroyAllParticleSystems();
            }
                break;
            case SCENEMANAGER_CREATE_BILLBOARD_SET:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned int> bufferReadint;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int billboardSetPtr = bufferReadLong.read(&buf);
                unsigned int poolSize = bufferReadint.read(&buf);
                const Ogre::String &billboardSetName = readString(&buf);
                Ogre::v1::BillboardSet *pBillboardSet = sceneManager->createBillboardSet(poolSize);
            	// TODO clean hack here for some reason setRenderQueueGroup() crashes but this works to not mess up with the rendering order.
                pBillboardSet->setRenderQueueSubGroup(1);
                addToItemList(pBillboardSet, MovableObjectType::BILLBOARD_SET);
                pBillboardSet->setName(billboardSetName);
                Ogre::String name(std::string("create_billboard_set ") + billboardSetName);
                putInPointerMap(billboardSetPtr, pBillboardSet, name.c_str());
                writePtr(&wbuf, getPointerAsLong(pBillboardSet));
//                alignPointer(&wbuf, 4);
//                write(&wbuf, getPointerAsLong(pBillboardSet));
            }
                break;
            case SCENEMANAGER_DESTROY_BILLBOARD_SET:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int billboardSetPtr = bufferReadLong.read(&buf);
                Ogre::v1::BillboardSet *billboardSet = (Ogre::v1::BillboardSet*) getNativePointer(billboardSetPtr, ptrJustCreated);
                removeFromItemList(billboardSet, MovableObjectType::BILLBOARD_SET);
                removePointerFromMap(billboardSetPtr, ptrJustCreated);
                sceneManager->destroyBillboardSet(billboardSet);

            }
                break;
            case SCENEMANAGER_DESTROY_ALL_BILLBOARD_SETS:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                removeAllFromItemListWithType(MovableObjectType::BILLBOARD_SET);
                sceneManager->destroyAllBillboardSets();

            }
                break;
            case SCENEMANAGER_CLEAR_SCENE:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                sceneManager->clearScene(true);
            }
                break;
            case SCENEMANAGER_SET_AMBIENT_LIGHT:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                const Ogre::ColourValue &upperHemisphere = readColour(&buf);
                const Ogre::ColourValue &lowerHemisphere = readColour(&buf);
                const Ogre::Vector3 &hemishpereDir = readVector3D(&buf);
                float envmapScale = bufferReadFloat.read(&buf);
                sceneManager->setAmbientLight(upperHemisphere, lowerHemisphere, hemishpereDir, envmapScale);
            }
                break;
	/*		case SCENEMANAGER_SET_SKYBOX:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<bool> bufferReadBool;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                bool enabled = bufferReadBool.read(&buf);
                const Ogre::String &materialName = readString(&buf);
                sceneManager->setSkyBox(enabled, materialName);
            }
                break;
            case SCENEMANAGER_SET_SKYBOX_ALL_PARAMS:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<bool> bufferReadBool;
                BufferRead<unsigned char> bufferReadChar;
                BufferRead<float> bufferReadFloat;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                bool enabled = bufferReadBool.read(&buf);
                const Ogre::String &materialName = readString(&buf);
                float distance = bufferReadFloat.read(&buf);
                bool drawFirst = bufferReadBool.read(&buf);
				const Ogre::Quaternion &orientation = readQuaternion(&buf);
                const Ogre::String &groupName = readString(&buf);
                sceneManager->setSkyBox(enabled, materialName, distance, drawFirst, orientation, groupName);
            }
                break;
            case SCENEMANAGER_SET_SKYBOX_ENABLED:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<bool> bufferReadBool;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                bool enabled = bufferReadBool.read(&buf);
                sceneManager->setSkyBoxEnabled(enabled);
            }
                break; */
            case SCENEMANAGER_CREATE_SCENE_NODE:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int sceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *pNode = sceneManager->createSceneNode();
                Ogre::String name(std::string("create_scene_node ") + pNode->getName());
                putInPointerMap(sceneNodePtr, pNode, name.c_str());
                writePtr(&wbuf, getPointerAsLong(pNode));
//                alignPointer(&wbuf, 4);
//                write(&wbuf, getPointerAsLong(pNode));
            }
                break;
            case SCENEMANAGER_CREATE_SCENE_NODE_ALL_PARAMS:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> bufferReadChar;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int sceneNodePtr = bufferReadLong.read(&buf);
                unsigned char sceneMemoryMgrTypeChar = bufferReadChar.read(&buf);
                Ogre::SceneNode *pNode = sceneManager->createSceneNode(sceneMemoryMgrTypeChar == 0 ? ::Ogre::SCENE_DYNAMIC : ::Ogre::SCENE_STATIC);
                Ogre::String name(std::string("create_scene_node ") + pNode->getName());
                putInPointerMap(sceneNodePtr, pNode, name.c_str());
                writePtr(&wbuf, getPointerAsLong(pNode));
//                alignPointer(&wbuf, 4);
//                write(&wbuf, getPointerAsLong(pNode));
            }
                break;
            case SCENEMANAGER_DESTROY_SCENE_NODE:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int sceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode= (Ogre::SceneNode*) getNativePointer(sceneNodePtr, ptrJustCreated);
                sceneManager->destroySceneNode(sceneNode);
                removePointerFromMap(sceneNodePtr, ptrJustCreated);
            }
                break;
            case SCENEMANAGER_CREATE_LIGHT:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int lightPtr = bufferReadLong.read(&buf);
                Ogre::Light *pLight = sceneManager->createLight();
                Ogre::String name(std::string("create_light ") + pLight->getName());
                putInPointerMap(lightPtr, pLight, name.c_str());
                writePtr(&wbuf, getPointerAsLong(pLight));
//                alignPointer(&wbuf, 4);
//                write(&wbuf, getPointerAsLong(pLight));
            }
                break;
            case SCENEMANAGER_DESTROY_LIGHT:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int lightPtr = bufferReadLong.read(&buf);
                Ogre::Light *light= (Ogre::Light*) getNativePointer(lightPtr, ptrJustCreated);
                sceneManager->destroyLight(light);
                removePointerFromMap(lightPtr, ptrJustCreated);
            }
                break;
            case SCENEMANAGER_DESTROY_ALL_LIGHTS:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                sceneManager->destroyAllLights();
            }
                break;
            case SCENEMANAGER_NOTIFY_STATIC_DIRTY:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int sceneManagerPtr = bufferReadLong.read(&buf);
                Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int sceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode= (Ogre::SceneNode*) getNativePointer(sceneNodePtr, ptrJustCreated);
                sceneManager->notifyStaticDirty(sceneNode);
            }
                break;
            case NODE_ADD_CHILD:
            {
                BufferRead<long long> bufferReadLong;
                unsigned char parentPtrFlag = getPointerFlag(&buf);
                bool parentPtrJustCreated = isPointerJustCreated(parentPtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *parentSceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, parentPtrJustCreated);
                unsigned char nodePtrFlag = getPointerFlag(&buf);
                bool nodePtrJustCreated = isPointerJustCreated(nodePtrFlag);
                long long int nodeSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(nodeSceneNodePtr, nodePtrJustCreated);
                parentSceneNode->addChild(sceneNode);
            }
                break;
            case NODE_REMOVE_CHILD:
            {
                BufferRead<long long> bufferReadLong;
                unsigned char parentPtrFlag = getPointerFlag(&buf);
                bool parentPtrJustCreated = isPointerJustCreated(parentPtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *parentSceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, parentPtrJustCreated);
                unsigned char nodePtrFlag = getPointerFlag(&buf);
                bool nodePtrJustCreated = isPointerJustCreated(nodePtrFlag);
                long long int nodeSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(nodeSceneNodePtr, nodePtrJustCreated);
                parentSceneNode->removeChild(sceneNode);
            }
                break;
/*            case NODE_HAS_CHILD:
            {
                BufferRead<long long> bufferReadLong;
                unsigned char parentPtrFlag = getPointerFlag(&buf);
                bool parentPtrJustCreated = isPointerJustCreated(parentPtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *parentSceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, parentPtrJustCreated);
                unsigned char nodePtrFlag = getPointerFlag(&buf);
                bool nodePtrJustCreated = isPointerJustCreated(nodePtrFlag);
                long long int nodeSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(nodeSceneNodePtr, nodePtrJustCreated);
                // There is no hasChild() method in Ogre 2.1. If really needed it can be added.
                bool hasChild = parentSceneNode->hasChild(sceneNode);
                write(&wbuf, (unsigned char) hasChild);
            }
                break;*/
/*            case NODE_SET_PARENT:
            {
                BufferRead<long long> bufferReadLong;
                unsigned char nodePtrFlag = getPointerFlag(&buf);
                bool nodePtrJustCreated = isPointerJustCreated(nodePtrFlag);
                long long int nodeSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(nodeSceneNodePtr, nodePtrJustCreated);
                unsigned char parentPtrFlag = getPointerFlag(&buf);
                bool parentPtrJustCreated = isPointerJustCreated(parentPtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *parentSceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, parentPtrJustCreated);
                // It's a protected method in Ogre 2.1. If needed make it public.
                sceneNode->setParent(parentSceneNode);
            }
                break;
            case NODE_UNSET_PARENT:
            {
                BufferRead<long long> bufferReadLong;
                unsigned char nodePtrFlag = getPointerFlag(&buf);
                bool nodePtrJustCreated = isPointerJustCreated(nodePtrFlag);
                long long int nodeSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(nodeSceneNodePtr, nodePtrJustCreated);
                // It's a protected method in Ogre 2.1. If needed make it public.
                sceneNode->unsetParent();
            }
                break;*/
            case SCENENODE_CREATE_CHILD:
            {
                BufferRead<long long> bufferReadLong;
				unsigned char parentPtrFlag = getPointerFlag(&buf);
				bool parentPtrJustCreated = isPointerJustCreated(parentPtrFlag);
				long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, parentPtrJustCreated);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int sceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *pNode = sceneNode->createChildSceneNode();
                Ogre::String name(std::string("create_scene_node ") + pNode->getName());
                putInPointerMap(sceneNodePtr, pNode, name.c_str());
                writePtr(&wbuf, getPointerAsLong(pNode));
            }
                break;
            case SCENENODE_CREATE_CHILD_ALL_PARAMS:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> bufferReadChar;
//                alignPointer(&buf, 4);
				unsigned char parentPtrFlag = getPointerFlag(&buf);
				bool parentPtrJustCreated = isPointerJustCreated(parentPtrFlag);
				long long int parentSceneNodePtr = bufferReadLong.read(&buf);
				Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, parentPtrJustCreated);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int sceneNodePtr = bufferReadLong.read(&buf);
                unsigned char sceneMemoryMgrTypeChar = bufferReadChar.read(&buf);
                const Ogre::Vector3 &translate = readVector3D(&buf);
                const Ogre::Quaternion &rotate = readQuaternion(&buf);
                Ogre::SceneNode *pNode = sceneNode->createChildSceneNode(
                        sceneMemoryMgrTypeChar == 0 ? ::Ogre::SCENE_DYNAMIC : ::Ogre::SCENE_STATIC,
                        translate, rotate);
                Ogre::String name(std::string("create_scene_node ") + pNode->getName());
                putInPointerMap(sceneNodePtr, pNode, name.c_str());
                writePtr(&wbuf, getPointerAsLong(pNode));
//                alignPointer(&wbuf, 4);
//                write(&wbuf, getPointerAsLong(pNode));
            }
                break;
            case SCENENODE_REMOVE_AND_DESTROY:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNodeParent = (Ogre::SceneNode*) getLongAsPointer(parentSceneNodePtr);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int sceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNodeChild= (Ogre::SceneNode*) getNativePointer(sceneNodePtr, ptrJustCreated);
                sceneNodeParent->removeAndDestroyChild(sceneNodeChild);
                removePointerFromMap(sceneNodePtr, ptrJustCreated);
            }
                break;
            case SCENENODE_REMOVE_AND_DESTROY_ALL_CHILDREN:
            {
                BufferRead<long long> bufferReadLong;
//                alignPointer(&buf, 4);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNodeParent = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, ptrJustCreated);
                sceneNodeParent->removeAndDestroyAllChildren();
//                removePointerFromMap(parentSceneNodePtr, ptrJustCreated);
            }
                break;
            case SCENENODE_ATTACH_OBJECT:
            {
                BufferRead<long long> bufferReadLong;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int itemPtr = bufferReadLong.read(&buf);
                Ogre::MovableObject *item = (Ogre::MovableObject*) getNativePointer(itemPtr, ptrJustCreated);
                sceneNode->attachObject(item);
//                putInPointerMap(itemPtr, item);
//                alignPointer(&wbuf, 4);
//                write(&wbuf, getPointerAsLong(item));
            }
                break;
            case SCENENODE_ATTACH_PARTICLE_SYSTEM:
            {
                BufferRead<long long> bufferReadLong;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int itemPtr = bufferReadLong.read(&buf);
                Ogre::MovableObject *item = static_cast<Ogre::MovableObject *>(static_cast<Ogre::ParticleSystem *>(getNativePointer(itemPtr, ptrJustCreated)));
                sceneNode->attachObject(item);
//                putInPointerMap(itemPtr, item);
//                alignPointer(&wbuf, 4);
//                write(&wbuf, getPointerAsLong(item));
            }
                break;
            case SCENENODE_DETACH_OBJECT:
            {
                BufferRead<long long> bufferReadLong;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int itemPtr = bufferReadLong.read(&buf);
                Ogre::MovableObject *item= (Ogre::MovableObject*) getNativePointer(itemPtr, ptrJustCreated);
                sceneNode->detachObject(item);
//                putInPointerMap(itemPtr, item);
//                alignPointer(&wbuf, 4);
//                write(&wbuf, getPointerAsLong(item));
            }
                break;
            case SCENENODE_DETACH_ALL_OBJECTS:
            {
                BufferRead<long long> bufferReadLong;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                sceneNode->detachAllObjects();
            }
                break;
            case SCENENODE_SETPOSITION_XYZ:
            {
                BufferRead<long long> bufferReadLong;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                const Ogre::Vector3 &position = readVector3D(&buf);
                sceneNode->setPosition(position);
            }
                break;
            case SCENENODE_SETPOSITION_VEC:
            {
                BufferRead<long long> bufferReadLong;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                const Ogre::Vector3 &position = readVector3D(&buf);
                sceneNode->setPosition(position);
            	// TODO get rid of string comparison for each and every scene node position update!!!
                if (mCubeCamera && sceneNode->getName() == CAMERA_NODE_NAME)
                {
                    mCubeCamera->setPosition(position);
                }
            }
                break;
            case SCENENODE_SETORIENTATION:
            {
                BufferRead<long long> bufferReadLong;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                const Ogre::Quaternion &orientation = readQuaternion(&buf);
                sceneNode->setOrientation(orientation);
            }
                break;
            case SCENENODE_SETDERIVEDPOSITION_XYZ:
            {
                BufferRead<long long> bufferReadLong;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                const Ogre::Vector3 &position = readVector3D(&buf);
                sceneNode->_setDerivedPosition(position);
            }
                break;
            case SCENENODE_SETDERIVEDPOSITION_VEC:
            {
                BufferRead<long long> bufferReadLong;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                const Ogre::Vector3 &position = readVector3D(&buf);
                sceneNode->_setDerivedPosition(position);
            }
                break;
            case SCENENODE_SETDERIVEDORIENTATION:
            {
                BufferRead<long long> bufferReadLong;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                const Ogre::Quaternion &orientation = readQuaternion(&buf);
                sceneNode->_setDerivedOrientation(orientation);
            }
                break;
            case SCENENODE_SETSCALING_XYZ:
            {
                BufferRead<long long> bufferReadLong;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                const Ogre::Vector3 &scale = readVector3D(&buf);
                sceneNode->setScale(scale);
            }
                break;
            case SCENENODE_LOOKAT:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> bufferReadChar;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                const Ogre::Vector3 &targetPoint = readVector3D(&buf);
                unsigned char relativeToChar = bufferReadChar.read(&buf);
//                const Ogre::Vector3 &localDirectionVector = readVector3D(&buf);
                ::Ogre::Node::TransformSpace transformSpace = (::Ogre::Node::TransformSpace) relativeToChar;
                sceneNode->lookAt(targetPoint, transformSpace);
            }
                break;
            case SCENENODE_LOOKAT_ALL_PARAMS:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> bufferReadChar;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                const Ogre::Vector3 &targetPoint = readVector3D(&buf);
                unsigned char relativeToChar = bufferReadChar.read(&buf);
                const Ogre::Vector3 &localDirectionVector = readVector3D(&buf);
                ::Ogre::Node::TransformSpace transformSpace = (::Ogre::Node::TransformSpace) relativeToChar;
                sceneNode->lookAt(targetPoint, transformSpace, localDirectionVector);
            }
                break;
            case SCENENODE_SETDIRECTION:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> bufferReadChar;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                const Ogre::Vector3 &targetPoint = readVector3D(&buf);
                unsigned char relativeToChar = bufferReadChar.read(&buf);
//                const Ogre::Vector3 &localDirectionVector = readVector3D(&buf);
                ::Ogre::Node::TransformSpace transformSpace = (::Ogre::Node::TransformSpace) relativeToChar;
                sceneNode->setDirection(targetPoint, transformSpace);
            }
                break;
            case SCENENODE_SETDIRECTION_ALL_PARAMS:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> bufferReadChar;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                const Ogre::Vector3 &targetPoint = readVector3D(&buf);
                unsigned char relativeToChar = bufferReadChar.read(&buf);
                const Ogre::Vector3 &localDirectionVector = readVector3D(&buf);
                ::Ogre::Node::TransformSpace transformSpace = (::Ogre::Node::TransformSpace) relativeToChar;
                sceneNode->setDirection(targetPoint, transformSpace, localDirectionVector);
            }
                break;
            case SCENENODE_SETVISIBLE:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<bool> bufferReadBool;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                bool visibility = bufferReadBool.read(&buf);
                sceneNode->setVisible(visibility);
            }
                break;
            case SCENENODE_FLIPVISIBILITY:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<bool> bufferReadBool;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                sceneNode->flipVisibility();
            }
                break;
            case SCENENODE_SETSTATIC:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<bool> bufferReadBool;
//                alignPointer(&buf, 4);
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode *sceneNode = (Ogre::SceneNode*) getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                bool isStatic = bufferReadBool.read(&buf);
                sceneNode->setStatic(isStatic);
            }
                break;
	        case SCENENODE_SETNAME:
        	{
                BufferRead<long long> bufferReadLong;
                unsigned char sceneNodePtrFlag = getPointerFlag(&buf);
                bool sceneNodePtrJustCreated = isPointerJustCreated(sceneNodePtrFlag);
                long long int parentSceneNodePtr = bufferReadLong.read(&buf);
                Ogre::SceneNode* sceneNode = (Ogre::SceneNode*)getNativePointer(parentSceneNodePtr, sceneNodePtrJustCreated);
                const Ogre::String& sceneNodeName = readString(&buf);
                sceneNode->setName(sceneNodeName);
        	}
	            break;
            case LIGHT_SET_TYPE:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> bufferReadChar;
                unsigned char lightPtrFlag = getPointerFlag(&buf);
                bool lightPtrJustCreated = isPointerJustCreated(lightPtrFlag);
                long long int lightPtr = bufferReadLong.read(&buf);
                Ogre::Light *light = (Ogre::Light*) getNativePointer(lightPtr, lightPtrJustCreated);
                unsigned char lightType = bufferReadChar.read(&buf);
                light->setType((Ogre::Light::LightTypes) lightType);
            }
                break;
            case LIGHT_SET_SPOTLIGHT_RANGE:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
                unsigned char lightPtrFlag = getPointerFlag(&buf);
                bool lightPtrJustCreated = isPointerJustCreated(lightPtrFlag);
                long long int lightPtr = bufferReadLong.read(&buf);
                Ogre::Light *light = (Ogre::Light*) getNativePointer(lightPtr, lightPtrJustCreated);
                float innerAngle = bufferReadFloat.read(&buf);
                float outerAngle = bufferReadFloat.read(&buf);
                float falloff = bufferReadFloat.read(&buf);
                light->setSpotlightRange(Ogre::Radian(innerAngle), Ogre::Radian(outerAngle), falloff);
            }
                break;
            case LIGHT_SET_DIFFUSE_COLOUR:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
                unsigned char lightPtrFlag = getPointerFlag(&buf);
                bool lightPtrJustCreated = isPointerJustCreated(lightPtrFlag);
                long long int lightPtr = bufferReadLong.read(&buf);
                Ogre::Light *light = (Ogre::Light*) getNativePointer(lightPtr, lightPtrJustCreated);
                float r = bufferReadFloat.read(&buf);
                float g = bufferReadFloat.read(&buf);
                float b = bufferReadFloat.read(&buf);
                light->setDiffuseColour(r, g, b);
            }
                break;
            case LIGHT_SET_SPECULAR_COLOUR:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
                unsigned char lightPtrFlag = getPointerFlag(&buf);
                bool lightPtrJustCreated = isPointerJustCreated(lightPtrFlag);
                long long int lightPtr = bufferReadLong.read(&buf);
                Ogre::Light *light = (Ogre::Light*) getNativePointer(lightPtr, lightPtrJustCreated);
                float r = bufferReadFloat.read(&buf);
                float g = bufferReadFloat.read(&buf);
                float b = bufferReadFloat.read(&buf);
                light->setSpecularColour(r, g, b);
            }
                break;
            case LIGHT_SET_ATTENUATION:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
                unsigned char lightPtrFlag = getPointerFlag(&buf);
                bool lightPtrJustCreated = isPointerJustCreated(lightPtrFlag);
                long long int lightPtr = bufferReadLong.read(&buf);
                Ogre::Light *light = (Ogre::Light*) getNativePointer(lightPtr, lightPtrJustCreated);
                const Ogre::Vector4 &vec = readVector4DFull(&buf);
                light->setAttenuation(vec.x, vec.y, vec.z, vec.w);
            }
                break;
            case LIGHT_SET_POWER_SCALE:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
                unsigned char lightPtrFlag = getPointerFlag(&buf);
                bool lightPtrJustCreated = isPointerJustCreated(lightPtrFlag);
                long long int lightPtr = bufferReadLong.read(&buf);
                Ogre::Light *light = (Ogre::Light*) getNativePointer(lightPtr, lightPtrJustCreated);
                float power = bufferReadFloat.read(&buf);
                light->setPowerScale(power);
            }
                break;
            case LIGHT_SET_ATTENUATION_BASED_ON_RADIUS:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
                unsigned char lightPtrFlag = getPointerFlag(&buf);
                bool lightPtrJustCreated = isPointerJustCreated(lightPtrFlag);
                long long int lightPtr = bufferReadLong.read(&buf);
                Ogre::Light *light = (Ogre::Light*) getNativePointer(lightPtr, lightPtrJustCreated);
                const Ogre::Vector2 &vec = readVector2D(&buf);
                light->setAttenuationBasedOnRadius(vec.x, vec.y);
            }
                break;
//            case LIGHT_SET_POSITION:
//            {
//                BufferRead<long long> bufferReadLong;
//                BufferRead<float> bufferReadFloat;
//                unsigned char lightPtrFlag = getPointerFlag(&buf);
//                bool lightPtrJustCreated = isPointerJustCreated(lightPtrFlag);
//                long long int lightPtr = bufferReadLong.read(&buf);
//                Ogre::Light *light = (Ogre::Light*) getNativePointer(lightPtr, lightPtrJustCreated);
//                const Ogre::Vector3 &pos = readVector3D(&buf);
//                light->setPosi
//            }
                break;
            case LIGHT_SET_DIRECTION:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
                unsigned char lightPtrFlag = getPointerFlag(&buf);
                bool lightPtrJustCreated = isPointerJustCreated(lightPtrFlag);
                long long int lightPtr = bufferReadLong.read(&buf);
                Ogre::Light *light = (Ogre::Light*) getNativePointer(lightPtr, lightPtrJustCreated);
                const Ogre::Vector3 &pos = readVector3D(&buf);
                light->setDirection(pos);
            }
                break;
            case ITEM_SET_DATABLOCK:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
                unsigned char itemPtrFlag = getPointerFlag(&buf);
                bool itemPtrJustCreated = isPointerJustCreated(itemPtrFlag);
                long long int itemPtr = bufferReadLong.read(&buf);
                Ogre::Item *item = (Ogre::Item*) getNativePointer(itemPtr, itemPtrJustCreated);
                const Ogre::String &datablockName = readString(&buf);
                item->setDatablock(datablockName);
            }
                break;
            case ITEM_SET_VISIBILITY_FLAGS:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<int> bufferReadInt;
                unsigned char itemPtrFlag = getPointerFlag(&buf);
                bool itemPtrJustCreated = isPointerJustCreated(itemPtrFlag);
                long long int itemPtr = bufferReadLong.read(&buf);
                Ogre::Item *item = (Ogre::Item*) getNativePointer(itemPtr, itemPtrJustCreated);
                const int visibilityFlag = bufferReadInt.read(&buf);
                item->setVisibilityFlags(visibilityFlag);
            }
                break;
            case COMPOSITOR_WORKSPACE_SET_ENABLED:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned  char> bufferReadChar;
                alignPointer(&buf, 4);
                long long int compositorWorkspacePtr = bufferReadLong.read(&buf);
                Ogre::CompositorWorkspace* compositorWorkspace = (Ogre::CompositorWorkspace*) compositorWorkspacePtr;
                unsigned char enabled = bufferReadChar.read(&buf);
                // LOGI("compositor ptr: %s, enabled: %s \n", SSTR(compositorWorkspacePtr).c_str(), SSTR(enabled ? 1 : 0).c_str());
                compositorWorkspace->setEnabled((bool) enabled);
            }
                break;
            case CAMERA_INVALIDATE_VIEW:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int cameraPtr = bufferReadLong.read(&buf);
                Ogre::Camera* camera = (Ogre::Camera*) cameraPtr;

//                camera->invalidateView();
            }
                break;
            case CAMERA_GET_PROJECTION_MATRIX:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int cameraPtr = bufferReadLong.read(&buf);
                Ogre::Camera* camera = (Ogre::Camera*) cameraPtr;
                Ogre::Matrix4 projectionMatrix = camera->getProjectionMatrix();
                writeMatrix4(&wbuf, projectionMatrix);
            }
                break;
            case CAMERA_GET_VIEW_MATRIX:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int cameraPtr = bufferReadLong.read(&buf);
                Ogre::Camera* camera = (Ogre::Camera*) cameraPtr;
                Ogre::Matrix4 viewMatrix = camera->getViewMatrix();
                writeMatrix4(&wbuf, viewMatrix);
            }
                break;
            case CAMERA_IS_VISIBLE_VEC:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> bufferReadChar;
                alignPointer(&buf, 4);
                long long int cameraPtr = bufferReadLong.read(&buf);
                Ogre::Camera* camera = (Ogre::Camera*) cameraPtr;
                Ogre::Vector4 centre = readVector4DAsPt(&buf);
                bool visible = camera->isVisible(Ogre::Vector3(centre.x, centre.y, centre.z), 0);
                write(&wbuf, (unsigned char) visible);
            }
                break;
            case CAMERA_IS_VISIBLE_AXIS_ALIGNED_BOX:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> bufferReadChar;
                alignPointer(&buf, 4);
                long long int cameraPtr = bufferReadLong.read(&buf);
                Ogre::Camera* camera = (Ogre::Camera*) cameraPtr;
                const Ogre::AxisAlignedBox &box = readAxisAlignedBox(&buf);
                bool visible = camera->isVisible(box);
                write(&wbuf, (unsigned char) visible);
            }
                break;
            case GET_ITEMS_AABBS:
            {
                std::vector<MovableObjectWithType>::iterator it = itemList.begin();
                const std::vector<MovableObjectWithType>::iterator &end = itemList.end();
//                LOGI("itemList size: %s", (SSTR(itemList.size()) + "\n").c_str());
                while (it != end)
                {
                    const Ogre::MovableObject* item = it->getMovableObject();
                    const Ogre::Aabb &aabb = item->getWorldAabb();
                    const Ogre::Vector3 &minimum = aabb.getMinimum();
                    const Ogre::Vector3 &maximum = aabb.getMaximum();
                    unsigned int bytesWritten = alignPointer(&wbuf, 4);
                    checkWriteBufferOverflow(bytesWritten);
                    writeVector3D(&wbuf, minimum);
                    writeVector3D(&wbuf, maximum);
                    ++it;
                }
            }
                break;
            case MOVABLE_OBJECT_DETACH_FROM_PARENT:
            {
                BufferRead<long long> bufferReadLong;
                unsigned char itemPtrFlag = getPointerFlag(&buf);
                bool itemPtrJustCreated = isPointerJustCreated(itemPtrFlag);
                long long int itemPtr = bufferReadLong.read(&buf);
                Ogre::MovableObject *movableObject = (Ogre::MovableObject*) getNativePointer(itemPtr, itemPtrJustCreated);
                movableObject->detachFromParent();
            }
                break;
            case OVERLAY_MANAGER_CREATE_OVERLAY:
            {
                BufferRead<long long> bufferReadLong;
				alignPointer(&buf, 4);
                long long int overlayManagerPtr = bufferReadLong.read(&buf);
                Ogre::v1::OverlayManager *overlayManager = (Ogre::v1::OverlayManager*) overlayManagerPtr;
                const Ogre::String &overlayName = readString(&buf);
                Ogre::v1::Overlay *overlay = overlayManager->create(overlayName);
                wbuf = writeOverlay(wbuf, overlay);

            }
                break;
            case OVERLAY_MANAGER_DESTROY_OVERLAY_BY_NAME:
            {
                BufferRead<long long> bufferReadLong;
				alignPointer(&buf, 4);
                long long int overlayManagerPtr = bufferReadLong.read(&buf);
                Ogre::v1::OverlayManager *overlayManager = (Ogre::v1::OverlayManager*) overlayManagerPtr;
                const Ogre::String &overlayName = readString(&buf);
                overlayManager->destroy(overlayName);
            }
                break;
            case OVERLAY_MANAGER_DESTROY_OVERLAY_BY_PTR:
            {
                BufferRead<long long> bufferReadLong;
				alignPointer(&buf, 4);
                long long int overlayManagerPtr = bufferReadLong.read(&buf);
                Ogre::v1::OverlayManager *overlayManager = (Ogre::v1::OverlayManager*) overlayManagerPtr;
                long long int overlayPtr = bufferReadLong.read(&buf);
                Ogre::v1::Overlay *overlay = (Ogre::v1::Overlay*) overlayPtr;
                overlayManager->destroy(overlay);
            }
                break;
            case OVERLAY_MANAGER_DESTROY_ALL:
            {
                BufferRead<long long> bufferReadLong;
				alignPointer(&buf, 4);
                long long int overlayManagerPtr = bufferReadLong.read(&buf);
                Ogre::v1::OverlayManager *overlayManager = (Ogre::v1::OverlayManager*) overlayManagerPtr;
                overlayManager->destroyAll();
            }
                break;
            case OVERLAY_MANAGER_GET_BY_NAME:
            {
                BufferRead<long long> bufferReadLong;
				alignPointer(&buf, 4);
                long long int overlayManagerPtr = bufferReadLong.read(&buf);
                Ogre::v1::OverlayManager *overlayManager = (Ogre::v1::OverlayManager*) overlayManagerPtr;
                const Ogre::String &overlayName = readString(&buf);
                Ogre::v1::Overlay *overlay = overlayManager->getByName(overlayName);
                wbuf = writeOverlay(wbuf, overlay);
            }
                break;
            case OVERLAY_MANAGER_UPDATE_DATA:break;
            case OVERLAY_SHOW:
            {
                BufferRead<long long> bufferReadLong;
				alignPointer(&buf, 4);
                long long int overlayPtr = bufferReadLong.read(&buf);
                Ogre::v1::Overlay *overlay = (Ogre::v1::Overlay*) overlayPtr;
                overlay->show();
            }
                break;
            case OVERLAY_HIDE:
            {
                BufferRead<long long> bufferReadLong;
				alignPointer(&buf, 4);
                long long int overlayPtr = bufferReadLong.read(&buf);
                Ogre::v1::Overlay *overlay = (Ogre::v1::Overlay*) overlayPtr;
                overlay->hide();
            }
                break;
            case OVERLAY_ELEMENT_SHOW:
            {
                BufferRead<long long> bufferReadLong;
				alignPointer(&buf, 4);
                long long int overlayElementPtr = bufferReadLong.read(&buf);
                Ogre::v1::OverlayElement *overlayElement = (Ogre::v1::OverlayElement*) overlayElementPtr;
                overlayElement->show();
            }
                break;
            case OVERLAY_ELEMENT_HIDE:
            {
                BufferRead<long long> bufferReadLong;
				alignPointer(&buf, 4);
                long long int overlayElementPtr = bufferReadLong.read(&buf);
                Ogre::v1::OverlayElement *overlayElement = (Ogre::v1::OverlayElement*) overlayElementPtr;
                overlayElement->hide();
            }
                break;
            case OVERLAY_ELEMENT_SET_LEFT:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
				alignPointer(&buf, 4);
                long long int overlayElementPtr = bufferReadLong.read(&buf);
                Ogre::v1::OverlayElement *overlayElement = (Ogre::v1::OverlayElement*) overlayElementPtr;
                float val = bufferReadFloat.read(&buf);
                overlayElement->setLeft(val);
            }
                break;
            case OVERLAY_ELEMENT_SET_TOP:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
				alignPointer(&buf, 4);
                long long int overlayElementPtr = bufferReadLong.read(&buf);
                Ogre::v1::OverlayElement *overlayElement = (Ogre::v1::OverlayElement*) overlayElementPtr;
                float val = bufferReadFloat.read(&buf);
                overlayElement->setTop(val);
            }
                break;
            case OVERLAY_ELEMENT_SET_WIDTH:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
				alignPointer(&buf, 4);
                long long int overlayElementPtr = bufferReadLong.read(&buf);
                Ogre::v1::OverlayElement *overlayElement = (Ogre::v1::OverlayElement*) overlayElementPtr;
                float val = bufferReadFloat.read(&buf);
                overlayElement->setWidth(val);
            }
                break;
            case OVERLAY_ELEMENT_SET_HEIGHT:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
				alignPointer(&buf, 4);
                long long int overlayElementPtr = bufferReadLong.read(&buf);
                Ogre::v1::OverlayElement *overlayElement = (Ogre::v1::OverlayElement*) overlayElementPtr;
                float val = bufferReadFloat.read(&buf);
                overlayElement->setHeight(val);
            }
                break;
            case OVERLAY_ELEMENT_SET_CAPTION:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
				alignPointer(&buf, 4);
                long long int overlayElementPtr = bufferReadLong.read(&buf);
                Ogre::v1::OverlayElement *overlayElement = (Ogre::v1::OverlayElement*) overlayElementPtr;
                const Ogre::String &val = readString(&buf);
                overlayElement->setCaption(val);
            }
                break;
            case OVERLAY_ELEMENT_SET_METRICS_MODE:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> bufferReadChar;
				alignPointer(&buf, 4);
                long long int overlayElementPtr = bufferReadLong.read(&buf);
                Ogre::v1::OverlayElement *overlayElement = (Ogre::v1::OverlayElement*) overlayElementPtr;
                unsigned char val = bufferReadChar.read(&buf);
                Ogre::v1::GuiMetricsMode mode = (Ogre::v1::GuiMetricsMode) val;
                overlayElement->setMetricsMode(mode);
            }
                break;
            case OVERLAY_ELEMENT_SET_MATERIAL_NAME:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int overlayElementPtr = bufferReadLong.read(&buf);
                Ogre::v1::OverlayElement *overlayElement = (Ogre::v1::OverlayElement*) overlayElementPtr;
                Ogre::String materialName = readString(&buf);
                overlayElement->setMaterialName(materialName);
            }
                break;
            case OVERLAY_ELEMENT_SET_ALIGNMENT:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> bufferReadChar;
                alignPointer(&buf, 4);
                long long int overlayElementPtr = bufferReadLong.read(&buf);
                Ogre::v1::TextAreaOverlayElement *overlayElement = (Ogre::v1::TextAreaOverlayElement*) overlayElementPtr;
                unsigned char val = bufferReadChar.read(&buf);
                Ogre::v1::TextAreaOverlayElement::Alignment alignment = (Ogre::v1::TextAreaOverlayElement::Alignment) val;
                overlayElement->setAlignment(alignment);
            }
                break;
            case GPU_PROGRAM_PARAMS_GET_PROGRAM_PARAMS:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<short> bufferReadShort;
                BufferRead<unsigned char> bufferReadChar;
                unsigned char gpuProgramParamsPtrFlag = getPointerFlag(&buf);
                bool gpuProgramParamsPtrJustCreated = isPointerJustCreated(gpuProgramParamsPtrFlag);
                long long int gpuProgramParamsPtr = bufferReadLong.read(&buf);
                Ogre::GpuProgramParameters *gpuProgramParams = (Ogre::GpuProgramParameters*) getNativePointer(gpuProgramParamsPtr, gpuProgramParamsPtrJustCreated);
                long long int renderablePtr = bufferReadLong.read(&buf);
                Ogre::Renderable *renderable = (Ogre::Renderable*) renderablePtr;
                short technique = bufferReadShort.read(&buf);
                short passVal = bufferReadShort.read(&buf);
                GpuProgramParametersType type = (GpuProgramParametersType) bufferReadChar.read(&buf);
                Ogre::Pass *pass = renderable->getMaterial()->getTechnique(technique)->getPass(passVal);
                Ogre::GpuProgramParametersSharedPtr gpuProgramParametersSharedPtr;
                switch (type)
                {
                    case GPU_VERTEX:
                        gpuProgramParametersSharedPtr = pass->getVertexProgramParameters();
                        break;
                    case GPU_FRAGMENT:
                        gpuProgramParametersSharedPtr = pass->getFragmentProgramParameters();
                        break;
                    case GPU_GEOMETRY:
                        gpuProgramParametersSharedPtr = pass->getGeometryProgramParameters();
                        break;
                    case GPU_COMPUTE:
                        gpuProgramParametersSharedPtr = pass->getComputeProgramParameters();
                        break;
                    case GPU_TESSELATION_DOMAIN:
                        gpuProgramParametersSharedPtr = pass->getTessellationDomainProgramParameters();
                        break;
                    case GPU_TESSELATION_HULL:
                        gpuProgramParametersSharedPtr = pass->getTessellationHullProgramParameters();
                        break;
                 /*   case GPU_SHADOW_CASTER_VERTEX:
                        gpuProgramParametersSharedPtr = pass->getShadowCasterVertexProgramParameters();
                        break;
                    case GPU_SHADOW_CASTER_FRAGMENT:
                        gpuProgramParametersSharedPtr = pass->getShadowCasterFragmentProgramParameters();
                        break;*/
                }
                writePtr(&wbuf, getPointerAsLong(gpuProgramParametersSharedPtr.getPointer()));
            }
                break;
            case GPU_PROGRAM_PARAMS_SET_NAMED_CONSTANT_COLOUR:
            {
                BufferRead<long long> bufferReadLong;
                unsigned char gpuProgramParamsPtrFlag = getPointerFlag(&buf);
                bool gpuProgramParamsPtrJustCreated = isPointerJustCreated(gpuProgramParamsPtrFlag);
                long long int gpuProgramParamsPtr = bufferReadLong.read(&buf);
                Ogre::GpuProgramParameters *gpuProgramParams = (Ogre::GpuProgramParameters*) getNativePointer(gpuProgramParamsPtr, gpuProgramParamsPtrJustCreated);
                const Ogre::String &paramName = readString(&buf);
                const Ogre::ColourValue &col = readColour(&buf);
                gpuProgramParams->setNamedConstant(paramName, col);
            }
                break;
            case GPU_PROGRAM_PARAMS_SET_NAMED_CONSTANT_INT:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<int> bufferReadInt;
                unsigned char gpuProgramParamsPtrFlag = getPointerFlag(&buf);
                bool gpuProgramParamsPtrJustCreated = isPointerJustCreated(gpuProgramParamsPtrFlag);
                long long int gpuProgramParamsPtr = bufferReadLong.read(&buf);
                Ogre::GpuProgramParameters *gpuProgramParams = (Ogre::GpuProgramParameters*) getNativePointer(gpuProgramParamsPtr, gpuProgramParamsPtrJustCreated);
                const Ogre::String &paramName = readString(&buf);
                int val = bufferReadInt.read(&buf);
                gpuProgramParams->setNamedConstant(paramName, val);
            }
                break;
            case GPU_PROGRAM_PARAMS_SET_NAMED_CONSTANT_FLOAT:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
                unsigned char gpuProgramParamsPtrFlag = getPointerFlag(&buf);
                bool gpuProgramParamsPtrJustCreated = isPointerJustCreated(gpuProgramParamsPtrFlag);
                long long int gpuProgramParamsPtr = bufferReadLong.read(&buf);
                Ogre::GpuProgramParameters *gpuProgramParams = (Ogre::GpuProgramParameters*) getNativePointer(gpuProgramParamsPtr, gpuProgramParamsPtrJustCreated);
                const Ogre::String &paramName = readString(&buf);
                float val = bufferReadFloat.read(&buf);
                gpuProgramParams->setNamedConstant(paramName, val);
            }
                break;
            case TEXTURE_MANAGER_GET_BY_NAME:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<int> bufferReadInt;
                BufferRead<short> bufferReadShort;
				alignPointer(&buf, 4);
                long long int renderablePtr = bufferReadLong.read(&buf);
                Ogre::Renderable *renderable = (Ogre::Renderable*) renderablePtr;
//                short technique = bufferReadShort.read(&buf);
//                short passVal = bufferReadShort.read(&buf);
//                int textureUnitState = bufferReadInt.read(&buf);
                Ogre::HlmsUnlitDatablock *datablock = (Ogre::HlmsUnlitDatablock*) renderable->getDatablock();
                Ogre::TextureGpu *texturePtr = datablock->getTexture(0);
                texturePtr->scheduleTransitionTo(Ogre::GpuResidency::Resident);
                texturePtr->waitForData();
//                const Ogre::TexturePtr &texture = Ogre::TextureManager::getSingleton().getByName(
//                                        renderable->getMaterial()->getTechnique(technique)->getPass(passVal)->getTextureUnitState(textureUnitState)->getName());
                writePtr(&wbuf, getPointerAsLong(texturePtr));
				write(&wbuf, texturePtr->getWidth());
				write(&wbuf, texturePtr->getHeight());
            }
                break;
            case TEXTURE_MANAGER_GET_BY_NAME_OVERLAY_ELEMENT:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<int> bufferReadInt;
                BufferRead<short> bufferReadShort;
                alignPointer(&buf, 4);
                long long int renderablePtr = bufferReadLong.read(&buf);
                Ogre::Renderable *renderable = (Ogre::Renderable*) renderablePtr;
//                short technique = bufferReadShort.read(&buf);
//                short passVal = bufferReadShort.read(&buf);
//                int textureUnitState = bufferReadInt.read(&buf);
                Ogre::v1::OverlayElement *overlayElement = (Ogre::v1::OverlayElement*) renderablePtr;
				overlayElement->initialise();
//				overlayElement->show();
                overlayElement->_update();
                Ogre::HlmsUnlitDatablock *datablock = (Ogre::HlmsUnlitDatablock*) overlayElement->getDatablock();
                Ogre::TextureGpu *texturePtr = datablock->getTexture(0);
                texturePtr->scheduleTransitionTo(Ogre::GpuResidency::Resident);
                texturePtr->waitForData();
//                const Ogre::TexturePtr &texture = Ogre::TextureManager::getSingleton().getByName(
//                                        renderable->getMaterial()->getTechnique(technique)->getPass(passVal)->getTextureUnitState(textureUnitState)->getName());
                writePtr(&wbuf, getPointerAsLong(texturePtr));
				write(&wbuf, texturePtr->getWidth());
				write(&wbuf, texturePtr->getHeight());
                ::Ogre::String prevDataStr("overlayElement texture width: " + SSTR(texturePtr->getWidth()) + " height: " + SSTR(texturePtr->getHeight()) + " texture name: " + texturePtr->getNameStr());
                LOGI("%s\n", prevDataStr.c_str());
            }
                break;
            case FRAME_STATS_UPDATE:
            {
                BufferRead<long long> bufferReadLong;
				alignPointer(&buf, 4);
                long long int rootPtr = bufferReadLong.read(&buf);
                Ogre::Root *root = (Ogre::Root*) rootPtr;
                const Ogre::FrameStats *frameStats = root->getFrameStats();
                write(&wbuf, frameStats->getFps());
                write(&wbuf, frameStats->getAvgFps());
                write(&wbuf, frameStats->getBestTime());
                write(&wbuf, frameStats->getWorstTime());
                write(&wbuf, frameStats->getLastTime());
                write(&wbuf, frameStats->getAvgTime());
            }
                break;
            case UNLIT_DATABLOCK_SET_USE_COLOUR:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> bufferReadChar;
                alignPointer(&buf, 4);
                long long int datablockPtr = bufferReadLong.read(&buf);
                unsigned char useColour = bufferReadChar.read(&buf);
                Ogre::HlmsUnlitDatablock *datablock = (Ogre::HlmsUnlitDatablock*) datablockPtr;
                datablock->setUseColour(useColour);
            }
                break;
            case UNLIT_DATABLOCK_SET_COLOUR:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int datablockPtr = bufferReadLong.read(&buf);
                const Ogre::ColourValue &colour = readColour(&buf);
                Ogre::HlmsUnlitDatablock *datablock = (Ogre::HlmsUnlitDatablock*) datablockPtr;
                datablock->setColour(colour);
            }
                break;
            case DYNAMIC_OVERLAY_ELEM_CTOR:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int elemPtr = bufferReadLong.read(&buf);
                std::string textureName = readString(&buf);
                std::string groupName = readString(&buf);
                Ogre::v1::OverlayElement *element = (Ogre::v1::OverlayElement*) elemPtr;
                Hotshot::DynamicOverlayElement *dynamicOverlayElement = new Hotshot::DynamicOverlayElement(element, textureName, groupName);
                writePtr(&wbuf, getPointerAsLong(dynamicOverlayElement));
            }
                break;
            case DYNAMIC_OVERLAY_ELEM_DTOR:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int elemPtr = bufferReadLong.read(&buf);
                Hotshot::DynamicOverlayElement *dynamicOverlayElement = (Hotshot::DynamicOverlayElement*) elemPtr;
                delete dynamicOverlayElement;
            }
                break;
            case DYNAMIC_OVERLAY_ELEM_RESET_TO_INITIAL_TEXTURE:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int elemPtr = bufferReadLong.read(&buf);
                Hotshot::DynamicOverlayElement *dynamicOverlayElement = (Hotshot::DynamicOverlayElement*) elemPtr;
                dynamicOverlayElement->resetToInitialTexture();
            }
                break;
            case DYNAMIC_OVERLAY_ELEM_UPDATE_FINAL_TEXTURE:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int elemPtr = bufferReadLong.read(&buf);
                Hotshot::DynamicOverlayElement *dynamicOverlayElement = (Hotshot::DynamicOverlayElement*) elemPtr;
                dynamicOverlayElement->updateFinalTexture();
            }
                break;
            case DYNAMIC_OVERLAY_ELEM_SET_POINT_IN_SCREEN_SPACE:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<int> bufferReadInt;
                BufferRead<float> bufferReadFloat;
                BufferRead<unsigned char> bufferReadChar;
                alignPointer(&buf, 4);
                long long int elemPtr = bufferReadLong.read(&buf);
                float x = bufferReadFloat.read(&buf);
                float y = bufferReadFloat.read(&buf);
                int pixelLen = bufferReadInt.read(&buf);
                Ogre::ColourValue colour = readColour(&buf);
                bool overwriteTransparentPixels = (bool) bufferReadChar.read(&buf);
                Hotshot::DynamicOverlayElement *dynamicOverlayElement = (Hotshot::DynamicOverlayElement*) elemPtr;
                dynamicOverlayElement->setPointScreenSpace(x, y, pixelLen, colour, overwriteTransparentPixels);
            }
                break;
            case DYNAMIC_OVERLAY_ELEM_LOCK:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int elemPtr = bufferReadLong.read(&buf);
                Hotshot::DynamicOverlayElement *dynamicOverlayElement = (Hotshot::DynamicOverlayElement*) elemPtr;
                dynamicOverlayElement->lock();
            }
                break;
            case DYNAMIC_OVERLAY_ELEM_UNLOCK:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int elemPtr = bufferReadLong.read(&buf);
                Hotshot::DynamicOverlayElement *dynamicOverlayElement = (Hotshot::DynamicOverlayElement*) elemPtr;
                dynamicOverlayElement->unlock();
            }
                break;
            case DYNAMIC_OVERLAY_ELEM_SET_AREA_VEC:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<int> bufferReadInt;
                BufferRead<unsigned char> bufferReadChar;
                alignPointer(&buf, 4);
                long long int elemPtr = bufferReadLong.read(&buf);
                Hotshot::DynamicOverlayElement *dynamicOverlayElement = (Hotshot::DynamicOverlayElement*) elemPtr;
                int left = bufferReadInt.read(&buf);
                int top = bufferReadInt.read(&buf);
                int right = bufferReadInt.read(&buf);
                int bottom = bufferReadInt.read(&buf);
                int front = bufferReadInt.read(&buf);
                int back = bufferReadInt.read(&buf);
                std::vector<Ogre::ColourValue> list;
                int listSize = bufferReadInt.read(&buf);
                for (int i = 0; i < listSize; ++i) {
                    Ogre::ColourValue value = readColour(&buf);
                    list.push_back(value);
                }
                bool overwriteTransparentPixels = (bool) bufferReadChar.read(&buf);
                Ogre::Box box(left, top, right, bottom, front, back);
                dynamicOverlayElement->setArea(box, list, overwriteTransparentPixels);
            }
                break;
            case DYNAMIC_OVERLAY_ELEM_SET_AREA:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<int> bufferReadInt;
                BufferRead<unsigned char> bufferReadChar;
                alignPointer(&buf, 4);
                long long int elemPtr = bufferReadLong.read(&buf);
                Hotshot::DynamicOverlayElement *dynamicOverlayElement = (Hotshot::DynamicOverlayElement*) elemPtr;
                int left = bufferReadInt.read(&buf);
                int top = bufferReadInt.read(&buf);
                int right = bufferReadInt.read(&buf);
                int bottom = bufferReadInt.read(&buf);
                int front = bufferReadInt.read(&buf);
                int back = bufferReadInt.read(&buf);
                Ogre::ColourValue value = readColour(&buf);
                bool overwriteTransparentPixels = (bool) bufferReadChar.read(&buf);
                Ogre::Box box(left, top, right, bottom, front, back);
                dynamicOverlayElement->setArea(box, value, overwriteTransparentPixels);
            }
                break;
            case TILED_ANIMATION_CREATE:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<int> bufferReadInt;
                BufferRead<float> bufferReadFloat;
//                alignPointer(&buf, 4);
                unsigned char tiledAnimationPtrFlag = getPointerFlag(&buf);
                bool tiledAnimationPtrJustCreated = isPointerJustCreated(tiledAnimationPtrFlag);
                long long int tiledAnimationPtr = bufferReadLong.read(&buf);
                unsigned char billboardSetPtrFlag = getPointerFlag(&buf);
                bool billboardSetPtrJustCreated = isPointerJustCreated(billboardSetPtrFlag);
                long long int billboardSetPtr = bufferReadLong.read(&buf);
                ::Ogre::v1::BillboardSet *billboardSet  = (::Ogre::v1::BillboardSet *) getNativePointer(billboardSetPtr, billboardSetPtrJustCreated); // What if we don't find it in the release build???
                Ogre::String name = readString(&buf);
                Ogre::String unlitMaterialName = readString(&buf);
                float speed = bufferReadFloat.read(&buf);
                int horizontalFramesNum = bufferReadInt.read(&buf);
                int verticalFramesNum = bufferReadInt.read(&buf);
                Hotshot::TiledAnimation *tiledAnimation = new Hotshot::TiledAnimation(*billboardSet, name, unlitMaterialName, speed, horizontalFramesNum, verticalFramesNum);
                Ogre::String name2(std::string("create_tiled_animation ") + name);
                putInPointerMap(tiledAnimationPtr, tiledAnimation, name2.c_str());
                writePtr(&wbuf, getPointerAsLong(tiledAnimation));
            }
                break;
            case TILED_ANIMATION_DESTROY:
            {
                BufferRead<long long> bufferReadLong;
//                alignPointer(&buf, 4);
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int tiledAnimationPtr = bufferReadLong.read(&buf);
                Hotshot::TiledAnimation *tiledAnimation = (Hotshot::TiledAnimation*) getNativePointer(tiledAnimationPtr, ptrJustCreated);
                removePointerFromMap(tiledAnimationPtr, ptrJustCreated);
                delete tiledAnimation;

            }
                break;
            case TILED_ANIMATION_UPDATE_CURRENT_FRAME_NUM:
            {
                BufferRead<long long> bufferReadLong;
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool ptrJustCreated = isPointerJustCreated(ptrFlag);
                long long int tiledAnimationPtr = bufferReadLong.read(&buf);
                Hotshot::TiledAnimation *tiledAnimation = (Hotshot::TiledAnimation*) getNativePointer(tiledAnimationPtr, ptrJustCreated);
                int currentFrameNum = tiledAnimation->getCurrentFrameNum();
                write(&wbuf, currentFrameNum);
            }
                break;
            case BILLBOARDSET_CREATE_BILLBOARD:
            {
                BufferRead<long long> bufferReadLong;
//                alignPointer(&buf, 4);
                unsigned char billboardSetPtrFlag = getPointerFlag(&buf);
                bool billboardSetPtrJustCreated = isPointerJustCreated(billboardSetPtrFlag);
                long long int billboardSetPtr = bufferReadLong.read(&buf);
                ::Ogre::v1::BillboardSet *billboardSet  = (::Ogre::v1::BillboardSet *) getNativePointer(billboardSetPtr, billboardSetPtrJustCreated); // What if we don't find it in the release build???
                unsigned char billboardPtrFlag = getPointerFlag(&buf);
                bool billboardPtrJustCreated = isPointerJustCreated(billboardPtrFlag);
                long long int billboardPtr = bufferReadLong.read(&buf);
                Ogre::Vector3 pos = readVector3D(&buf);
                Ogre::ColourValue colour = readColour(&buf);
                Ogre::v1::Billboard *billboard = billboardSet->createBillboard(pos, colour);
                Ogre::String name(std::string("create_billboard ") + billboardSet->getName());
                putInPointerMap(billboardPtr, billboard, name.c_str());
                writePtr(&wbuf, getPointerAsLong(billboard));
            }
                break;
            case BILLBOARDSET_DESTROY_BILLBOARD:
            {
                BufferRead<long long> bufferReadLong;
//                alignPointer(&buf, 4);
                unsigned char billboardSetPtrFlag = getPointerFlag(&buf);
                bool billboardSetPtrJustCreated = isPointerJustCreated(billboardSetPtrFlag);
                long long int billboardSetPtr = bufferReadLong.read(&buf);
                ::Ogre::v1::BillboardSet *billboardSet  = (::Ogre::v1::BillboardSet *) getNativePointer(billboardSetPtr, billboardSetPtrJustCreated); // What if we don't find it in the release build???
                unsigned char billboardPtrFlag = getPointerFlag(&buf);
                bool billboardPtrJustCreated = isPointerJustCreated(billboardPtrFlag);
                long long int billboardPtr = bufferReadLong.read(&buf);
                Ogre::v1::Billboard *billboard = (::Ogre::v1::Billboard*) getNativePointer(billboardPtr, billboardPtrJustCreated);
                billboardSet->removeBillboard(billboard);
                removePointerFromMap(billboardPtr, billboardPtrJustCreated);
            }
                break;
//            case BILLBOARDSET_DESTROY_BILLBOARD_BY_ID: // Using the pos means we do not know to removePointerFromMap()
//            {
//                BufferRead<long long> bufferReadLong;
//                BufferRead<int> bufferReadInt;
//                alignPointer(&buf, 4);
//                unsigned char billboardSetPtrFlag = getPointerFlag(&buf);
//                bool billboardSetPtrJustCreated = isPointerJustCreated(billboardSetPtrFlag);
//                long long int billboardSetPtr = bufferReadLong.read(&buf);
//                ::Ogre::v1::BillboardSet *billboardSet  = (::Ogre::v1::BillboardSet *) getNativePointer(billboardSetPtr, billboardSetPtrJustCreated); // What if we don't find it in the release build???
//                int pos = bufferReadInt.read(&buf);
//                billboardSet->removeBillboard(pos);
//                removePointerFromMap(billboardPtr, billboardPtrJustCreated);
//            }
                break;
            case BILLBOARDSET_SET_COMMON_UP_VECTOR:
            {
                BufferRead<long long> bufferReadLong;
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool billboardSetPtrJustCreated = isPointerJustCreated(ptrFlag);
                long long int billboardSetPtr = bufferReadLong.read(&buf);
                ::Ogre::v1::BillboardSet *billboardSet  = (::Ogre::v1::BillboardSet *) getNativePointer(billboardSetPtr, billboardSetPtrJustCreated);
                const Ogre::Vector3 &upVector = readVector3D(&buf);
                billboardSet->setCommonUpVector(upVector);
            }
                break;
            case BILLBOARDSET_SET_COMMON_DIRECTION:
            {
                BufferRead<long long> bufferReadLong;
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool billboardSetPtrJustCreated = isPointerJustCreated(ptrFlag);
                long long int billboardSetPtr = bufferReadLong.read(&buf);
                ::Ogre::v1::BillboardSet *billboardSet  = (::Ogre::v1::BillboardSet *) getNativePointer(billboardSetPtr, billboardSetPtrJustCreated);
                const Ogre::Vector3 &commonDirection = readVector3D(&buf);
                billboardSet->setCommonDirection(commonDirection);
            }
                break;
            case BILLBOARDSET_SET_DEFAULT_DIMENSIONS:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool billboardSetPtrJustCreated = isPointerJustCreated(ptrFlag);
                long long int billboardSetPtr = bufferReadLong.read(&buf);
                ::Ogre::v1::BillboardSet *billboardSet  = (::Ogre::v1::BillboardSet *) getNativePointer(billboardSetPtr, billboardSetPtrJustCreated);
                float xDim = bufferReadFloat.read(&buf);
                float yDim = bufferReadFloat.read(&buf);
                billboardSet->setDefaultDimensions(xDim, yDim);
            }
                break;
            case BILLBOARDSET_SET_MATERIAL_NAME:
            {
                BufferRead<long long> bufferReadLong;
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool billboardSetPtrJustCreated = isPointerJustCreated(ptrFlag);
                long long int billboardSetPtr = bufferReadLong.read(&buf);
                ::Ogre::v1::BillboardSet *billboardSet  = (::Ogre::v1::BillboardSet *) getNativePointer(billboardSetPtr, billboardSetPtrJustCreated);
                const Ogre::String &materialName = readString(&buf);
                const Ogre::String &groupName = readString(&buf);
                billboardSet->setMaterialName(materialName, groupName);
            }
                break;
            case BILLBOARDSET_SET_DATABLOCK_NAME:
            {
                BufferRead<long long> bufferReadLong;
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool billboardSetPtrJustCreated = isPointerJustCreated(ptrFlag);
                long long int billboardSetPtr = bufferReadLong.read(&buf);
                ::Ogre::v1::BillboardSet *billboardSet  = (::Ogre::v1::BillboardSet *) getNativePointer(billboardSetPtr, billboardSetPtrJustCreated);
                const Ogre::String &materialName = readString(&buf);
                Ogre::HlmsManager *hlmsManager = Ogre::Root::getSingleton().getHlmsManager();
                //Give preference to HLMS materials of the same name
                Ogre::HlmsDatablock *datablock = hlmsManager->getDatablockNoDefault( materialName );
                if (datablock)
                {
                    billboardSet->setDatablock( datablock );
                }
                else
                {
                    LOGI("datablock with name %s could not be found!", (materialName + "\n").c_str());
                }
            }
                break;
            case BILLBOARDSET_SET_BILLBOARD_ORIGIN:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> bufferReadChar;
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool billboardSetPtrJustCreated = isPointerJustCreated(ptrFlag);
                long long int billboardSetPtr = bufferReadLong.read(&buf);
                ::Ogre::v1::BillboardSet *billboardSet  = (::Ogre::v1::BillboardSet *) getNativePointer(billboardSetPtr, billboardSetPtrJustCreated);
                unsigned char origin = bufferReadChar.read(&buf);
                billboardSet->setBillboardOrigin((Ogre::v1::BillboardOrigin) origin);
            }
                break;
            case BILLBOARDSET_SET_BILLBOARD_ROTATION_TYPE:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> bufferReadChar;
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool billboardSetPtrJustCreated = isPointerJustCreated(ptrFlag);
                long long int billboardSetPtr = bufferReadLong.read(&buf);
                ::Ogre::v1::BillboardSet *billboardSet  = (::Ogre::v1::BillboardSet *) getNativePointer(billboardSetPtr, billboardSetPtrJustCreated);
                unsigned char rotationType = bufferReadChar.read(&buf);
                billboardSet->setBillboardRotationType((Ogre::v1::BillboardRotationType) rotationType);
            }
                break;
            case BILLBOARDSET_SET_BILLBOARD_TYPE:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> bufferReadChar;
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool billboardSetPtrJustCreated = isPointerJustCreated(ptrFlag);
                long long int billboardSetPtr = bufferReadLong.read(&buf);
                ::Ogre::v1::BillboardSet *billboardSet  = (::Ogre::v1::BillboardSet *) getNativePointer(billboardSetPtr, billboardSetPtrJustCreated);
                unsigned char type = bufferReadChar.read(&buf);
                billboardSet->setBillboardType((Ogre::v1::BillboardType) type);
            }
                break;
            case BILLBOARD_SET_ROTATION:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
                unsigned char ptrFlag = getPointerFlag(&buf);
                bool billboardPtrJustCreated = isPointerJustCreated(ptrFlag);
                long long int billboardPtr = bufferReadLong.read(&buf);
                ::Ogre::v1::Billboard *billboard  = (::Ogre::v1::Billboard *) getNativePointer(billboardPtr, billboardPtrJustCreated);
                float rotation = bufferReadFloat.read(&buf);
                billboard->setRotation(Ogre::Radian(rotation));
            }
                break;
            case MOVABLE_OBJECT_SET_RENDER_QUEUE_GROUP:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<unsigned char> bufferReadChar;
                unsigned char movableObjectPtrFlag = getPointerFlag(&buf);
                bool movableObjectPtrJustCreated = isPointerJustCreated(movableObjectPtrFlag);
                long long int movableObjectPtr = bufferReadLong.read(&buf);
                ::Ogre::MovableObject *movableObject  = (::Ogre::MovableObject *) getNativePointer(movableObjectPtr, movableObjectPtrJustCreated);
                unsigned char renderQueueGroup = bufferReadChar.read(&buf);
                movableObject->setRenderQueueGroup(renderQueueGroup);
            }
                break;
            case PARTICLE_SYSTEM_SET_MATERIAL_NAME:
            {
                BufferRead<long long> bufferReadLong;
                unsigned char particleSystemPtrFlag = getPointerFlag(&buf);
                bool particleSystemPtrJustCreated = isPointerJustCreated(particleSystemPtrFlag);
                long long int particleSystemPtr = bufferReadLong.read(&buf);
                ::Ogre::ParticleSystem *particleSystem  = (::Ogre::ParticleSystem *) getNativePointer(particleSystemPtr, particleSystemPtrJustCreated);
                Ogre::String materialName = readString(&buf);
                particleSystem->setMaterialName(materialName);
            }
                break;
            case SCENE_COMPOSITOR_INSERT_NODE:
            {
                BufferRead<long long> bufferReadLong;
                BufferRead<float> bufferReadFloat;
                alignPointer(&buf, 4);
                long long int sceneCompositorPtr = bufferReadLong.read(&buf);
                long long int workspacePtr = bufferReadLong.read(&buf);
                Ogre::String workspaceName = readString(&buf);
                Ogre::String baseNodeName = readString(&buf);
                Ogre::String nodeToInsertName = readString(&buf);
                Ogre::String previousNodeName = readString(&buf);
                Ogre::ColourValue rgba = readColour(&buf);
                float scaleStep = bufferReadFloat.read(&buf);
                Hotshot::SceneCompositor *sceneCompositor = (Hotshot::SceneCompositor*) sceneCompositorPtr;
                Ogre::CompositorWorkspace *workspace = (::Ogre::CompositorWorkspace*) workspacePtr;
                long long int compositorAnimationId = sceneCompositor->startCompositorAnimation(workspace, workspaceName,
                                                                                              baseNodeName, nodeToInsertName,
                                                                                                previousNodeName, rgba, scaleStep);
                alignPointer(&wbuf, 4);
                write(&wbuf, compositorAnimationId);
            }
                break;
            case SCENE_COMPOSITOR_REVERT_NODE:
            {
                BufferRead<long long> bufferReadLong;
                alignPointer(&buf, 4);
                long long int sceneCompositorPtr = bufferReadLong.read(&buf);
                long long int compositorId = bufferReadLong.read(&buf);
                Hotshot::SceneCompositor *sceneCompositor = (Hotshot::SceneCompositor*) sceneCompositorPtr;
                sceneCompositor->stopCompositorAnimation(compositorId);
            }
                break;
            case FRAME_ID_POS:
            {
                // Execute the remaining frame end listeners.
                std::vector<FrameEndListenerExecutor *>::iterator it = frameEndListenerExecutorList.begin();
//                const std::vector<FrameEndListenerExecutor *>::iterator &end = frameEndListenerExecutorList.end();
                while (it != frameEndListenerExecutorList.end())
                {
                    FrameEndListenerExecutor *frameEndListenerExecutor = *it;
                    if (renderedOneFrame || !frameEndListenerExecutor->isExecuteAfterRenderOneFrame())
                    {
                        // Temporary hack to get the current frame num to compare it with the java side.
//                        int currentFrameNum = Hotshot::GorillaGUI::getCurrentFrameNum();
//                        Ogre::String s("renderOneFrame currentFrameNum:  " + SSTR(currentFrameNum) + "\n");
//                        LOGI("%s", s.c_str());
//                        s = ("Write buffer size before frameEndListenerExecutor: " + SSTR(getCurrentWriteBufferWrittenSize()) + "\n");
//                        LOGI("%s", s.c_str());
                        wbuf = frameEndListenerExecutor->execute(wbuf);
//                        s = ("Write buffer size after frameEndListenerExecutor: " + SSTR(getCurrentWriteBufferWrittenSize()) + "\n");
//                        LOGI("%s", s.c_str());
                        delete frameEndListenerExecutor;
                        it = frameEndListenerExecutorList.erase(it);
                    }
                    else
                    {
                        ++it;
                    }
                }
                *frameIdPos = *buf++;
                if (renderedOneFrame)
                {
                    *frameIdPos = (*frameIdPos | (char) 0x80);
                }
                // The frame id is guaranteed to be the last thing in the buffer.
                return;
            }
                break;
#ifdef TEST_RENDERING_THREAD
            case TEST_CALL1:
            {
                
            }
                break;
            case TEST_CALL2:
            {

            }
                break;
            case TEST_CALL3:
            {

            }
                break;
            case TEST_CALL4:
            {

            }
                break;
            case TEST_CALL_W_RESPONSE1:
            {
                BufferRead<int> bufferRead;
                int i = bufferRead.read(&buf);
                const Ogre::String &first = readString(&buf);
                const Ogre::String &second = readString(&buf);
                write(&wbuf, i);
//                LOGI("%s", "TEST_CALL_W_RESPONSE1");
            }
                break;
            case TEST_CALL_W_RESPONSE2:
            {
                BufferRead<double> doubleBufferRead;
                BufferRead<char> charBufferRead;
                BufferRead<long long> longBufferRead;
                alignPointer(&buf, 4);
                double i = doubleBufferRead.read(&buf);
                char c = charBufferRead.read(&buf);
                alignPointer(&buf, 4);
                long long l = longBufferRead.read(&buf);
                const Ogre::String &first = readString(&buf);
                const Ogre::String &second = readString(&buf);
                alignPointer(&wbuf, 4);
                write(&wbuf, i);
                write(&wbuf, c);
                alignPointer(&wbuf, 4);
                write(&wbuf, l);
//                LOGI("%s", "TEST_CALL_W_RESPONSE2");
            }
                break;
            case TEST_CALL_W_RESPONSE3:
            {
                const Ogre::Vector4 &vector4 = readVector4DFull(&buf);
                const Ogre::String &first = readString(&buf);
                const Ogre::String &second = readString(&buf);
                writeVector4DFull(&wbuf, vector4);
                writeString(&wbuf, first);
//                LOGI("%s", "TEST_CALL_W_RESPONSE3");
            }
                break;
            case TEST_CALL_W_RESPONSE4:
            {
                const std::map<Ogre::String, int> &map = readMapStringInt(&buf);
                const Ogre::Vector4 &vector4 = readVector4DAsPt(&buf);
                const Ogre::String &first = readString(&buf);
                const Ogre::String &second = readString(&buf);
//                LOGI("%i", map.size());
                write(&wbuf, (int) map.size());
                writeVector4D(&wbuf, vector4);
//                LOGI("%s", "TEST_CALL_W_RESPONSE4");
            }
                break;
            case TEST_CALL_RENDERING_THREAD_DATA_FLUSH1:
            {
                BufferRead<long long> longBufferRead;
                alignPointer(&buf, 4);
                long long l = longBufferRead.read(&buf);
                alignPointer(&wbuf, 4);
                write(&wbuf, l);
//                LOGI("%s", "TEST_CALL_RENDERING_THREAD_DATA_FLUSH1");
            }
                break;
            case TEST_CALL_RENDERING_THREAD_DATA_FLUSH2:
            {
                BufferRead<long long> longBufferRead;
                alignPointer(&buf, 4);
                long long l = longBufferRead.read(&buf);
                alignPointer(&wbuf, 4);
                write(&wbuf, l);
//                LOGI("%s", "TEST_CALL_RENDERING_THREAD_DATA_FLUSH2");
            }
                break;
            case TEST_CALL_RENDERING_THREAD_DATA_FLUSH3:
            {
                BufferRead<long long> longBufferRead;
                alignPointer(&buf, 4);
                long long l = longBufferRead.read(&buf);
                alignPointer(&wbuf, 4);
                write(&wbuf, l);
//                LOGI("%s", "TEST_CALL_RENDERING_THREAD_DATA_FLUSH3");
            }
                break;
            case TEST_CALL_RENDERING_THREAD_DATA_FLUSH4:
            {
                BufferRead<long long> longBufferRead;
                alignPointer(&buf, 4);
                long long l = longBufferRead.read(&buf);
                alignPointer(&wbuf, 4);
                write(&wbuf, l);
//                LOGI("%s", "TEST_CALL_RENDERING_THREAD_DATA_FLUSH4");
            }
                break;
            case TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY1:
            {
                BufferRead<long long> longBufferRead;
                alignPointer(&buf, 4);
                long long l1 = longBufferRead.read(&buf);
                long long l2 = longBufferRead.read(&buf);
                long long l3 = longBufferRead.read(&buf);
                long long l4 = longBufferRead.read(&buf);
                alignPointer(&wbuf, 4);
                write(&wbuf, l1);
                write(&wbuf, l2);
                write(&wbuf, l3);
                write(&wbuf, l4);
//                LOGI("%s", "TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY1");
            }
                break;
            case TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY2:
            {
                BufferRead<long long> longBufferRead;
                alignPointer(&buf, 4);
                long long l1 = longBufferRead.read(&buf);
                long long l2 = longBufferRead.read(&buf);
                long long l3 = longBufferRead.read(&buf);
                long long l4 = longBufferRead.read(&buf);
                alignPointer(&wbuf, 4);
                write(&wbuf, l1);
                write(&wbuf, l2);
                write(&wbuf, l3);
                write(&wbuf, l4);
//                LOGI("%s", "TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY2");
            }
                break;
            case TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY3:
            {
                BufferRead<long long> longBufferRead;
                alignPointer(&buf, 4);
                long long l1 = longBufferRead.read(&buf);
                long long l2 = longBufferRead.read(&buf);
                long long l3 = longBufferRead.read(&buf);
                long long l4 = longBufferRead.read(&buf);
                alignPointer(&wbuf, 4);
                write(&wbuf, l1);
                write(&wbuf, l2);
                write(&wbuf, l3);
                write(&wbuf, l4);
//                LOGI("%s", "TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY3");

            }
                break;
            case TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY4:
            {
                BufferRead<long long> longBufferRead;
                alignPointer(&buf, 4);
                long long l1 = longBufferRead.read(&buf);
                long long l2 = longBufferRead.read(&buf);
                long long l3 = longBufferRead.read(&buf);
                long long l4 = longBufferRead.read(&buf);
                alignPointer(&wbuf, 4);
                write(&wbuf, l1);
                write(&wbuf, l2);
                write(&wbuf, l3);
                write(&wbuf, l4);
//                LOGI("%s", "TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY4");
            }
                break;
            case TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_PTR1:
            {

            }
                break;
            case TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_PTR2:
            {

            }
                break;
            case TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_PTR3:
            {

            }
                break;
            case TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_PTR4:
            {

            }
                break;
            case TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_ARRAY1:
            {

            }
                break;
            case TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_ARRAY2:
            {

            }
                break;
            case TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_ARRAY3:
            {

            }
                break;
            case TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_ARRAY4:
            {

            }
                break;

#endif
            default:break;

        }

//        long long renderOneFrameEndTime = getNanoTime(env);
//        long long timeDiff = renderOneFrameEndTime - renderOneFrameBeginTime;
//        timeDiff /= 1000000;
//        Ogre::String s("message time:  " + SSTR(timeDiff) + "\n");
//        LOGI("%s", s.c_str());
    }
}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_pipeline_ENG_1RenderingThread_initializeNative(
        JNIEnv *env, jclass type, jint readingBufferSizeInBytes, jint writingBufferSizeInBytes, jint bufferCount) {

    ::readingBufferSizeInBytes = readingBufferSizeInBytes;
    ::writingBufferSizeInBytes = writingBufferSizeInBytes;
    ::bufferCount = bufferCount;
    byteBufferCls = env->FindClass("java/nio/ByteBuffer");
    limit = env->GetMethodID(byteBufferCls, "limit", "(I)Ljava/nio/Buffer;");

    vector3DCls = env->FindClass("headwayent/hotshotengine/ENG_Vector3D");
    vector3DSet = env->GetMethodID(vector3DCls, "set", "(FFF)V");

    initializeNative(env);
}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_pipeline_ENG_1RenderingThread_renderOneFrame(
        JNIEnv *env, jclass type, jobject readingBuffer, jobject writingBuffer, jint currentBuffer) {





    char *buf = (char*)env->GetDirectBufferAddress(readingBuffer);
    buf += ::readingBufferSizeInBytes * currentBuffer;
    char *wbuf = (char*)env->GetDirectBufferAddress(writingBuffer);
    // We need to clear the response buffer because we might register an end frame listener
    // end the current rendered frame might contain the same frame id that is required by the
    // new frame listener, which will result in the wrong data being read from the response
    // buffer. By resetting the value we make sure that when the frame ends, the response
    // buffer actually contains the data for that respective frame id and not from a previous
    // listener. We don't need to clear the whole buffer since only the first byte is checked.
    // Each frame listener knows for how many bytes to listen to so it doesn't matter that
    // there might be extra bytes with useless values from a previous listener.
    *wbuf = 0;
#ifdef NATIVE_DEBUG
    resetWriteBufferOverflow();
#endif

    Hotshot::GorillaGUI::setCurrentFrameNum(currentBuffer);



    extractMessages(env, buf, wbuf);

//    Ogre::Root::getSingleton().renderOneFrame();



}

JNIEXPORT jint JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1IdString_createIdString__Ljava_lang_String_2(
        JNIEnv *env, jclass type, jstring s_) {
    const char *s = env->GetStringUTFChars(s_, 0);

    Ogre::IdString str(s);

    env->ReleaseStringUTFChars(s_, s);
    return str.mHash;
}

JNIEXPORT jint JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1IdString_createIdString__I(
        JNIEnv *env, jclass type, jint val) {

    Ogre::IdString str(val);
    return str.mHash;

}

JNIEXPORT jint JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1IdString_append__ILjava_lang_String_2(
        JNIEnv *env, jclass type, jint hash, jstring oth_) {
    const char *oth = env->GetStringUTFChars(oth_, 0);

    Ogre::IdString str;
    str.mHash = (Ogre::uint32) hash;
    str += Ogre::IdString(oth);

    env->ReleaseStringUTFChars(oth_, oth);
    return str.mHash;
}

JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1OverlaySystemNativeWrapper_createOverlaySystem(
        JNIEnv *env, jclass type) {

    Ogre::v1::OverlaySystem *overlaySystem = OGRE_NEW Ogre::v1::OverlaySystem();
    return getPointerAsLong(overlaySystem);

}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1OverlaySystemNativeWrapper_destroyOverlaySystem(
        JNIEnv *env, jclass type, jlong ptr) {

    Ogre::v1::OverlaySystem *overlaySystem = (Ogre::v1::OverlaySystem*) getLongAsPointer(ptr);
    OGRE_DELETE overlaySystem;

}

extern "C"
JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1OverlayManagerNativeWrapper_getOverlayManager(
        JNIEnv *env, jclass type) {

    Ogre::v1::OverlayManager &manager = Ogre::v1::OverlayManager::getSingleton();
    return getPointerAsLong(&manager);

}

extern "C"
JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1FontManagerNativeWrapper_getFontManager(
        JNIEnv *env, jclass type) {

    Ogre::FontManager &manager = Ogre::FontManager::getSingleton();
    return getPointerAsLong(&manager);

}

JNIEXPORT void JNICALL
Java_headwayent_blackholedarksun_APP_1Game_setupResources(JNIEnv *env, jclass type, jstring path_, jstring filename_) {
    const char *path = env->GetStringUTFChars(path_, 0);
    const char *filename = env->GetStringUTFChars(filename_, 0);

    const Ogre::String &pathStr = Ogre::String(path);
    const Ogre::String &filenameStr = Ogre::String(filename);
    setupResources(pathStr, filenameStr);

    env->ReleaseStringUTFChars(path_, path);
}

JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1RootNativeWrapper_createRoot(
        JNIEnv *env, jclass type, jstring pluginFileName_, jstring configFileName_,
        jstring logFileName_) {
    const char *pluginFileName = env->GetStringUTFChars(pluginFileName_, 0);
    const char *configFileName = env->GetStringUTFChars(configFileName_, 0);
    const char *logFileName = env->GetStringUTFChars(logFileName_, 0);

    Ogre::String pluginsPath(pluginFileName);
    Ogre::String configPath(configFileName);
    Ogre::String logPath(logFileName);

//    const Ogre::AbiCookie abiCookie = Ogre::generateAbiCookie();
    Ogre::Root *root = OGRE_NEW Ogre::Root( /*&abiCookie*/0, pluginsPath, configPath, logPath );
#if ((HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_ANDROID) || (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_WIN32) || (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_IOS))
    particleFXPlugin = OGRE_NEW Ogre::ParticleFXPlugin();
    Ogre::NameValuePairList options;
    root->installPlugin(particleFXPlugin, &options);
#endif

    env->ReleaseStringUTFChars(pluginFileName_, pluginFileName);
    env->ReleaseStringUTFChars(configFileName_, configFileName);
    env->ReleaseStringUTFChars(logFileName_, logFileName);

    return getPointerAsLong(root);
}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1RootNativeWrapper_destroyRoot(
        JNIEnv *env, jclass type, jlong ptr) {

    Ogre::Root *root = (Ogre::Root*) getLongAsPointer(ptr);
#if ((HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_ANDROID) || (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_WIN32) || (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_IOS))
    // root->uninstallPlugin(particleFXPlugin);
    // OGRE_DELETE particleFXPlugin;
#endif
    OGRE_DELETE root;

}

JNIEXPORT void JNICALL
Java_headwayent_blackholedarksun_APP_1Game_loadResources__JLjava_lang_String_2(JNIEnv *env,
                                                                               jclass type,
                                                                               jlong rootPtr,
                                                                               jstring path_) {
    const char *path = env->GetStringUTFChars(path_, 0);

    Ogre::String pathStr(path);
    Ogre::Root *root = (Ogre::Root*) getLongAsPointer(rootPtr);
    loadResources(root, pathStr);

    env->ReleaseStringUTFChars(path_, path);
}

JNIEXPORT void JNICALL
Java_headwayent_blackholedarksun_APP_1Game_loadEssentialResources__JLjava_lang_String_2(JNIEnv *env,
                                                                               jclass type,
                                                                               jlong rootPtr,
                                                                               jstring path_) {
    const char *path = env->GetStringUTFChars(path_, 0);

    Ogre::String pathStr(path);
    Ogre::Root *root = (Ogre::Root*) getLongAsPointer(rootPtr);
    loadEssentialResources(root, pathStr);

    env->ReleaseStringUTFChars(path_, path);
}

JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1SceneManagerNativeWrapper_createSceneManager(
        JNIEnv *env, jclass type, jlong rootPtr, jshort typeMask, jint numThreads, jint threadCullingMethod,
        jstring name_) {
    const char *name = env->GetStringUTFChars(name_, 0);

    Ogre::Root *root = (Ogre::Root*) getLongAsPointer(rootPtr);
    Ogre::String instanceName(name);
    Ogre::SceneManager *sceneManager = root->createSceneManager((Ogre::SceneTypeMask) typeMask, (size_t) numThreads, instanceName);

    env->ReleaseStringUTFChars(name_, name);
    
    return getPointerAsLong(sceneManager);
}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1SceneManagerNativeWrapper_destroySceneManager(
        JNIEnv *env, jclass type, jlong rootPtr, jlong ptr) {

    Ogre::Root *root = (Ogre::Root*) getLongAsPointer(rootPtr);
    Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(ptr);

    root->destroySceneManager(sceneManager);
}

JNIEXPORT void JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1SceneManagerNativeWrapper_initSceneManager(
        JNIEnv *env, jclass type, jlong ptr, jstring zoneType_) {
#ifdef USE_PCZ
    const char *zoneType = env->GetStringUTFChars(zoneType_, 0);
    Ogre::PCZSceneManager *sceneManager = (Ogre::PCZSceneManager*) getLongAsPointer(ptr);
    sceneManager->init(zoneType);
    env->ReleaseStringUTFChars(zoneType_, zoneType);
#endif
}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1SceneManagerNativeWrapper_addRenderQueueListenerNative(
        JNIEnv *env, jclass type, jlong sceneManagerPtr, jlong renderQueueListener) {

    Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
    Ogre::RenderQueueListener *listener = (Ogre::RenderQueueListener*) getLongAsPointer(renderQueueListener);
    sceneManager->addRenderQueueListener(listener);
    sceneManager->getRenderQueue()->setSortRenderQueue(
                        Ogre::v1::OverlayManager::getSingleton().mDefaultRenderQueueId,
                        Ogre::RenderQueue::StableSort );

}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1SceneManagerNativeWrapper_removeRenderQueueListenerNative(
        JNIEnv *env, jclass type, jlong sceneManagerPtr, jlong renderQueueListener) {

    Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
    Ogre::RenderQueueListener *listener = (Ogre::RenderQueueListener*) getLongAsPointer(renderQueueListener);
    sceneManager->removeRenderQueueListener(listener);

}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1RootNativeWrapper_initialiseNative(
        JNIEnv *env, jclass type, jlong rootPtr, jboolean autoCreateWindow, jstring windowTitle_,
        jstring customCapabilitiesConfig_) {
    const char *windowTitle = env->GetStringUTFChars(windowTitle_, 0);
    const char *customCapabilitiesConfig = env->GetStringUTFChars(customCapabilitiesConfig_, 0);

    Ogre::Root *root = (Ogre::Root*) getLongAsPointer(rootPtr);
    Ogre::String windowTitleStr(windowTitle);
    Ogre::String customCapabilitiesConfigStr(customCapabilitiesConfig);
    root->initialise(autoCreateWindow, windowTitleStr, customCapabilitiesConfigStr);

    env->ReleaseStringUTFChars(windowTitle_, windowTitle);
    env->ReleaseStringUTFChars(customCapabilitiesConfig_, customCapabilitiesConfig);
}

JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1RootNativeWrapper_getRenderSystemNative(
        JNIEnv *env, jclass type, jlong ptr) {

    Ogre::Root *root = (Ogre::Root*) getLongAsPointer(ptr);
    Ogre::RenderSystem *renderSystem = root->getRenderSystem();
    return getPointerAsLong(renderSystem);

}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_glsles_GLRenderSystemNativeWrapper_setConfigOptionNative(
        JNIEnv *env, jclass type, jlong ptr, jstring name_, jstring value_) {
    const char *name = env->GetStringUTFChars(name_, 0);
    const char *value = env->GetStringUTFChars(value_, 0);

    Ogre::RenderSystem *renderSystem = (Ogre::RenderSystem*) getLongAsPointer(ptr);
    Ogre::String nameStr(name);
    Ogre::String valueStr(value);
    renderSystem->setConfigOption(nameStr, valueStr);

    env->ReleaseStringUTFChars(name_, name);
    env->ReleaseStringUTFChars(value_, value);
}

JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1TextureAtlas_loadTexture(JNIEnv *env, jclass type,
                                                                       jstring name_, jstring group_) {
    const char *name = env->GetStringUTFChars(name_, 0);
    const char *group = env->GetStringUTFChars(group_, 0);

    Ogre::TextureGpuManager* textureManager = Ogre::Root::getSingleton().getRenderSystem()->getTextureGpuManager();
    Ogre::TextureGpu *texture = textureManager->createOrRetrieveTexture(name,
        Ogre::GpuPageOutStrategy::GpuPageOutStrategy::Discard, Ogre::CommonTextureTypes::Diffuse, group);

    texture->scheduleTransitionTo(Ogre::GpuResidency::Resident);
    texture->waitForData();

    long long int pointerAsLong = getPointerAsLong(texture);

    env->ReleaseStringUTFChars(name_, name);
    env->ReleaseStringUTFChars(group_, group);
    
    return pointerAsLong;
}

JNIEXPORT jint JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1TextureAtlas_getTextureWidth(JNIEnv *env, jclass type,
                                                                           jlong ptr) {

    const Ogre::TextureGpu* texturePtr = (Ogre::TextureGpu*) getLongAsPointer(ptr);
    return texturePtr->getWidth();

}

JNIEXPORT jint JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1TextureAtlas_getTextureHeight(JNIEnv *env,
                                                                            jclass type,
                                                                            jlong ptr) {

    const Ogre::TextureGpu* texturePtr = (Ogre::TextureGpu*) getLongAsPointer(ptr);
    return texturePtr->getHeight();

}

// JNIEXPORT jstring JNICALL
// Java_headwayent_hotshotengine_gorillagui_ENG_1TextureAtlas_getTextureName(JNIEnv *env, jclass type,
//                                                                           jlong ptr) {
//
//     const Ogre::TextureGpu* texturePtr = (Ogre::TextureGpu*) getLongAsPointer(ptr);
//     return env->NewStringUTF(texturePtr->getName().c_str());
// }

JNIEXPORT jint JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1TextureAtlas_createNinePatch(JNIEnv *env, jclass type,
                                                                           jlong ptr,
                                                                           jobject ninePatch) {

    Ogre::TextureGpu* texturePtr = (Ogre::TextureGpu*) getLongAsPointer(ptr);
    return Hotshot::GorillaGUI::createNinePatch(env, texturePtr, ninePatch);

}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1TextureAtlas_calculateSpriteCoordinates(JNIEnv *env,
                                                                                      jclass type,
                                                                                      jobject s,
                                                                                      jfloat mInverseTextureSizeX,
                                                                                      jfloat mInverseTextureSizeY) {

    Hotshot::Sprite sprite = Hotshot::Sprite::readSprite(env, s);
    Hotshot::GorillaGUI::calculateSpriteCoordinates(sprite, mInverseTextureSizeX, mInverseTextureSizeY);
    Hotshot::Sprite::writeSprite(env, s, sprite);

}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_ENG_1SceneManager_setShadowDirectionalLightExtrusionDistance__JF(
        JNIEnv *env, jclass type, jlong ptr, jfloat distance) {

    Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(ptr);
    sceneManager->setShadowDirectionalLightExtrusionDistance(distance);

}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_ENG_1SceneManager_setShadowFarDistance__JF(JNIEnv *env,
                                                                                  jclass type,
                                                                                  jlong ptr,
                                                                                  jfloat distance) {

    Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(ptr);
    sceneManager->setShadowFarDistance(distance);

}

JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1SceneManagerNativeWrapper_createCamera__JLjava_lang_String_2ZZ(
        JNIEnv *env, jclass type, jlong ptr, jstring name_, jboolean isVisible,
        jboolean forCubemapping) {
    const char *name = env->GetStringUTFChars(name_, 0);

    Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(ptr);
    Ogre::Camera *pCamera = sceneManager->createCamera(name, isVisible, forCubemapping);

    // By default the camera is attached to the root node. But the Ogre root node isn't the same with the java root node.
    // We cannot detach the camera from root node because we can no longer do lookAt further down the road.
//    pCamera->detachFromParent();

    env->ReleaseStringUTFChars(name_, name);
    return getPointerAsLong(pCamera);
}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CameraNativeWrapper_setPosition(
        JNIEnv *env, jclass type, jlong ptr, jfloat x, jfloat y, jfloat z) {

    Ogre::Camera *camera = (Ogre::Camera*) getLongAsPointer(ptr);
    camera->setPosition(x, y, z);

}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CameraNativeWrapper_lookAt(
        JNIEnv *env, jclass type, jlong ptr, jfloat x, jfloat y, jfloat z) {

    Ogre::Camera *camera = (Ogre::Camera*) getLongAsPointer(ptr);
    camera->lookAt(x, y, z);

}


extern "C"
JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CameraNativeWrapper_setDirection(
        JNIEnv *env, jclass type, jlong ptr, jfloat x, jfloat y, jfloat z) {

    Ogre::Camera *camera = (Ogre::Camera*) getLongAsPointer(ptr);
    camera->setDirection(x, y, z);

}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CameraNativeWrapper_setNearClipDistance(
        JNIEnv *env, jclass type, jlong ptr, jfloat dist) {

    Ogre::Camera *camera = (Ogre::Camera*) getLongAsPointer(ptr);
    camera->setNearClipDistance(dist);

}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CameraNativeWrapper_setFarClipDistance(
        JNIEnv *env, jclass type, jlong ptr, jfloat dist) {

    Ogre::Camera *camera = (Ogre::Camera*) getLongAsPointer(ptr);
    camera->setFarClipDistance(dist);

}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CameraNativeWrapper_setAutoAspectRatio(
        JNIEnv *env, jclass type, jlong ptr, jboolean aspectRatio) {

    Ogre::Camera *camera = (Ogre::Camera*) getLongAsPointer(ptr);
    camera->setAutoAspectRatio(aspectRatio);

}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CameraNativeWrapper_setAspectRatio__JF(
        JNIEnv *env, jclass type, jlong ptr, jfloat aspectRatio) {

    Ogre::Camera *camera = (Ogre::Camera*) getLongAsPointer(ptr);
    camera->setAspectRatio(aspectRatio);

}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CameraNativeWrapper_setFOVy__JF(
        JNIEnv *env, jclass type, jlong ptr, jfloat fovy) {
    Ogre::Camera *camera = (Ogre::Camera*) getLongAsPointer(ptr);
    camera->setFOVy( Ogre::Radian(fovy) );
}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CameraNativeWrapper_setFixedYawAxis__JF(
        JNIEnv *env, jclass type, jlong ptr, jboolean fixed) {
    Ogre::Camera *camera = (Ogre::Camera*) getLongAsPointer(ptr);
    camera->setFixedYawAxis(fixed);
}

JNIEXPORT void JNICALL
Java_headwayent_blackholedarksun_APP_1Game_showLoadingScreenNative(JNIEnv *env, jclass type,
                                                                   jlong renderWindowPtr, jfloat screenDensity) {

    Ogre::Window *renderWindow = (Ogre::Window*) getLongAsPointer(renderWindowPtr);
    Hotshot::LoadingScreen::getSingleton().showLoadingScreen(renderWindow, screenDensity);

}

JNIEXPORT void JNICALL
Java_headwayent_blackholedarksun_APP_1Game_hideLoadingScreenNative(JNIEnv *env, jclass type) {

    Hotshot::LoadingScreen::getSingleton().hideLoadingScreen();

}

JNIEXPORT void JNICALL
Java_headwayent_blackholedarksun_APP_1Game_setOnScreenKeyboardVisible(JNIEnv *env, jclass type,
                                                                   jboolean visible, jlong gameViewController_, jlong textDelegate_) {
#if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_IOS)
    void *gameViewController = getLongAsPointer(gameViewController_);
    void *textDelegate = getLongAsPointer(textDelegate_);
    setOnscreenKeyboardVisible(visible, gameViewController, textDelegate);
#endif
}

JNIEXPORT jobjectArray JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1SilverBack_createByteBuffersNative(JNIEnv *env,
                                                                                 jclass type,
                                                                                 jint size,
                                                                                 jint count) {

    return Hotshot::GorillaGUI::createByteBuffers(env, size, count);

}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1SilverBack_setMaterialName(JNIEnv *env, jclass type,
                                                                         jstring materialName_) {
    const char *materialName = env->GetStringUTFChars(materialName_, 0);
    Ogre::String materialNameStr(materialName);

    Hotshot::ScreenRenderable::setMaterialName(materialNameStr);

    env->ReleaseStringUTFChars(materialName_, materialName);
}

Ogre::CompositorWorkspace *
setupCompositor(const Ogre::Root *root, Ogre::SceneManager *sceneManager,
                Ogre::Window *renderWindow, Ogre::Camera *camera,
                const Ogre::String &workspaceNameStr, bool enabled) {
    Ogre::CompositorManager2 *compositorManager = root->getCompositorManager2();
    LOGI("setupCompositor() workspace name: %s IdString: %s testString: %s\n", workspaceNameStr.c_str(), ::Ogre::IdString(workspaceNameStr).getFriendlyText().c_str(), ::Ogre::IdString("HotshotDefaultWorkspace").getFriendlyText().c_str());
    Ogre::CompositorWorkspace *compositorWorkspace = compositorManager->addWorkspace(sceneManager, renderWindow->getTexture(), camera,
                                                                                     workspaceNameStr, enabled );
    return compositorWorkspace;
}

JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CompositorWorkspaceNativeWrapper_addWorkspace(
        JNIEnv *env, jclass type, jlong rootPtr, jlong sceneManagerPtr, jlong renderWindowPtr, jlong cameraPtr,
        jstring workspaceName_, jboolean enabled) {
    const char *workspaceName = env->GetStringUTFChars(workspaceName_, 0);

    Ogre::Root *root = (Ogre::Root*) getLongAsPointer(rootPtr);
    Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);
    Ogre::Window *renderWindow = (Ogre::Window*) getLongAsPointer(renderWindowPtr);
    Ogre::Camera *camera = (Ogre::Camera*) getLongAsPointer(cameraPtr);
    Ogre::String workspaceNameStr(workspaceName);

    Ogre::CompositorWorkspace *compositorWorkspace = setupCompositor(root, sceneManager,
                                                                     renderWindow, camera,
                                                                     workspaceNameStr, enabled);

    env->ReleaseStringUTFChars(workspaceName_, workspaceName);

    return getPointerAsLong(compositorWorkspace);
}

enum IblQuality
{
    MipmapsLowest,
    IblLow,
    IblHigh
};

#if( OGRE_PLATFORM == OGRE_PLATFORM_APPLE ) || ( OGRE_PLATFORM == OGRE_PLATFORM_APPLE_IOS )
IblQuality mIblQuality = IblLow;
#else
IblQuality mIblQuality = IblHigh;
#endif

//Ogre::ResourceStatusMap initialCubemapLayouts; // Make sure createDynamicCubemapWorkspace() is always called before addWorkspace().
// Ogre::ResourceAccessMap initialCubemapUavAccess; // Make sure createDynamicCubemapWorkspace() is always called before addWorkspace().

bool initIbl = false;

JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1DynamicCubemapCompositorWorkspaceNativeWrapper_createDynamicCubemapWorkspace(
    JNIEnv* env, jclass type, jlong rootPtr, jlong sceneManagerPtr, jlong renderWindowPtr, jlong cubeCameraPtr,
    jstring workspaceName_, jboolean enabled) {

    const char* workspaceNameCharStr = env->GetStringUTFChars(workspaceName_, 0);
    Ogre::String workspaceNameStr(workspaceNameCharStr);

    Ogre::Root* root = (Ogre::Root*)getLongAsPointer(rootPtr);
    Ogre::SceneManager* sceneManager = (Ogre::SceneManager*)getLongAsPointer(sceneManagerPtr);
    Ogre::Window* renderWindow = (Ogre::Window*)getLongAsPointer(renderWindowPtr);
    Ogre::Camera* cubeCamera = (Ogre::Camera*)getLongAsPointer(cubeCameraPtr);

    Ogre::CompositorManager2* compositorManager = root->getCompositorManager2();

    // if (!initIbl)
    // {
        Ogre::CompositorNodeDef* nodeDef =
            compositorManager->getNodeDefinitionNonConst(workspaceNameStr);
        const Ogre::CompositorPassDefVec& passes =
            nodeDef->getTargetPass(nodeDef->getNumTargetPasses() - 1u)->getCompositorPasses();

        OGRE_ASSERT_HIGH(dynamic_cast<Ogre::CompositorPassIblSpecularDef*>(passes.back()));
        Ogre::CompositorPassIblSpecularDef* iblSpecPassDef =
            static_cast<Ogre::CompositorPassIblSpecularDef*>(passes.back());
        iblSpecPassDef->mForceMipmapFallback = mIblQuality == MipmapsLowest;
        iblSpecPassDef->mSamplesPerIteration = mIblQuality == IblLow ? 32.0f : 128.0f;
        iblSpecPassDef->mSamplesSingleIterationFallback = iblSpecPassDef->mSamplesPerIteration;
    //     initIbl = true;
    // }

    //Setup the cubemap's compositor.
    Ogre::CompositorChannelVec cubemapExternalChannels(1);
    //Any of the cubemap's render targets will do
    // cubemapExternalChannels[0].target = mDynamicCubemap->getBuffer(0)->getRenderTarget();
    cubemapExternalChannels[0] = mDynamicCubemap;

    Ogre::String workspaceName("DynamicCubemap_cubemap");
    workspaceName += workspaceNameStr;
    if (!compositorManager->hasWorkspaceDefinition(workspaceName))
    {
        Ogre::CompositorWorkspaceDef* workspaceDef = compositorManager->addWorkspaceDefinition(
            workspaceName);
        //"CubemapRendererNode" has been defined in scripts.
        //Very handy (as it 99% the same for everything)
        workspaceDef->connectExternal(0, workspaceNameStr, 0);
    }

    Ogre::ResourceStatus resourceStatus;

    Ogre::CompositorWorkspace* mDynamicCubemapWorkspace =
        compositorManager->addWorkspace( sceneManager, cubemapExternalChannels, cubeCamera,
            workspaceName, enabled, -1/*, (Ogre::UavBufferPackedVec*)0, &initialCubemapLayouts*/ );

    env->ReleaseStringUTFChars(workspaceName_, workspaceNameCharStr);

    return getPointerAsLong(mDynamicCubemapWorkspace);
}

JNIEXPORT jlong JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1DynamicCubemapCompositorWorkspaceNativeWrapper_addWorkspace(
        JNIEnv *env, jclass type, jlong rootPtr, jlong sceneManagerPtr, jlong renderWindowPtr, jlong cameraPtr,
		jstring workspaceName_, jboolean enabled) {

    const char* workspaceNameCharStr = env->GetStringUTFChars(workspaceName_, 0);
    Ogre::String workspaceNameStr(workspaceNameCharStr);

    Ogre::Root* root = (Ogre::Root*)getLongAsPointer(rootPtr);
    Ogre::SceneManager* sceneManager = (Ogre::SceneManager*)getLongAsPointer(sceneManagerPtr);
    Ogre::Window* renderWindow = (Ogre::Window*)getLongAsPointer(renderWindowPtr);
    Ogre::Camera* camera = (Ogre::Camera*)getLongAsPointer(cameraPtr);

    // Also enable SMAA since we are connecting to it in the workspace.
    Demo::SmaaUtils::initialize(root->getRenderSystem(),
                                Demo::SmaaUtils::SMAA_PRESET_ULTRA,
                                Demo::SmaaUtils::EdgeDetectionColour);

    Ogre::CompositorManager2* compositorManager = root->getCompositorManager2();

    //Now setup the regular renderer
    Ogre::CompositorChannelVec externalChannels(2);
    //Render window
    externalChannels[0] = renderWindow->getTexture();
    // externalChannels[0].target = renderWindow;
    // externalChannels[1].target = mDynamicCubemap->getBuffer(0)->getRenderTarget();
    externalChannels[1] = mDynamicCubemap;

    Ogre::CompositorWorkspace *compositorWorkspace = compositorManager->addWorkspace(sceneManager, externalChannels, camera,
        workspaceNameStr, //"Tutorial_DynamicCubemapWorkspace",
        enabled);

    env->ReleaseStringUTFChars(workspaceName_, workspaceNameCharStr);

    return getPointerAsLong(compositorWorkspace);
}



JNIEXPORT jlong JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1DynamicCubemapCompositorWorkspaceNativeWrapper_createCubemapTexture(
        JNIEnv *env, jclass type) {
	Ogre::Root &root = Ogre::Root::getSingleton();
	Ogre::TextureGpuManager* textureManager = root.getRenderSystem()->getTextureGpuManager();

	Ogre::uint32 iblSpecularFlag = 0;
    if (root.getRenderSystem()->getCapabilities()->hasCapability(Ogre::RSC_COMPUTE_PROGRAM) &&
        mIblQuality != MipmapsLowest)
    {
        iblSpecularFlag = Ogre::TextureFlags::TextureFlags::Uav | Ogre::TextureFlags::TextureFlags::Reinterpretable;
    }

    mDynamicCubemap = textureManager->createTexture(
        "DynamicCubemap", "DynamicCubemap", Ogre::GpuPageOutStrategy::GpuPageOutStrategy::Discard,
        Ogre::TextureFlags::TextureFlags::RenderToTexture |       //
        Ogre::TextureFlags::TextureFlags::AllowAutomipmaps |  //
        iblSpecularFlag,
        Ogre::TextureTypes::TextureTypes::TypeCube/*, Ogre::ResourceGroupManager::DEFAULT_RESOURCE_GROUP_NAME*/);
    mDynamicCubemap->scheduleTransitionTo(Ogre::GpuResidency::GpuResidency::OnStorage);
	Ogre::uint32 resolution = 512u;
    if (mIblQuality == MipmapsLowest)
        resolution = 1024u;
    else if (mIblQuality == IblLow)
        resolution = 256u;
    else
        resolution = 1024u;
    mDynamicCubemap->setResolution(resolution, resolution );
    mDynamicCubemap->setNumMipmaps(Ogre::PixelFormatGpuUtils::getMaxMipmapCount(resolution));
    if (mIblQuality != MipmapsLowest)
    {
        // Limit max mipmap to 16x16
        mDynamicCubemap->setNumMipmaps(mDynamicCubemap->getNumMipmaps() - 4u);
    }
    mDynamicCubemap->setPixelFormat(Ogre::PixelFormatGpu::PFG_RGBA8_UNORM_SRGB);
    mDynamicCubemap->scheduleTransitionTo(Ogre::GpuResidency::GpuResidency::Resident);
    mDynamicCubemap->waitForData();

    Ogre::HlmsManager* hlmsManager = root.getHlmsManager();
    assert(dynamic_cast<Ogre::HlmsPbs*>(hlmsManager->getHlms(Ogre::HLMS_PBS)));
    Ogre::HlmsPbs* hlmsPbs = static_cast<Ogre::HlmsPbs*>(hlmsManager->getHlms(Ogre::HLMS_PBS));
    hlmsPbs->resetIblSpecMipmap(0u);

    return getPointerAsLong(mDynamicCubemap);
}

JNIEXPORT jlong JNICALL
        Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1DynamicCubemapCompositorWorkspaceNativeWrapper_createCubemapCamera(
        JNIEnv *env, jclass type, jlong sceneManagerPtr) {
    Ogre::SceneManager *sceneManager = (Ogre::SceneManager*) getLongAsPointer(sceneManagerPtr);

    mCubeCamera = sceneManager->createCamera( "CubeMapCamera", true, true );
    mCubeCamera->setFOVy( Ogre::Degree(90) );
    mCubeCamera->setAspectRatio( 1 );
    mCubeCamera->setFixedYawAxis(false);
    mCubeCamera->setNearClipDistance(0.5);
    //The default far clip distance is way too big for a cubemap-capable camera,
    //hich prevents Ogre from better culling.
    mCubeCamera->setFarClipDistance( 10000 );
    mCubeCamera->setPosition( 0, 1.0, 0 );

    return getPointerAsLong(mCubeCamera);
}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_ENG_1RenderRoot_renderOneFrameNative(JNIEnv *env,
                                                                            jclass type,
                                                                            jlong rootPtr) {

    Ogre::Root *root = (Ogre::Root*) getLongAsPointer(rootPtr);
    root->renderOneFrame();

}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_test_TestRenderingThread_testSlowCall1(
        JNIEnv *env, jclass type) {

//    LOGI("%s", "TEST_SLOW_CALL_1");

}

JNIEXPORT jint JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_test_TestRenderingThread_testSlowCall2(
        JNIEnv *env, jclass type, jint p) {

//    LOGI("%s", "TEST_SLOW_CALL_2");

    return 1234;

}

JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_test_TestRenderingThread_testSlowCall3(
        JNIEnv *env, jclass type, jint p, jlong l) {

//    LOGI("%s", "TEST_SLOW_CALL_3");

    return 12345678;

}

JNIEXPORT jboolean JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_test_TestRenderingThread_testSlowCall4(
        JNIEnv *env, jclass type, jint p, jbyte b, jboolean boolParam, jshort s) {

//    LOGI("%s", "TEST_SLOW_CALL_4");

    return true;

}

JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1ViewportNativeWrapper_getViewport(
        JNIEnv *env, jclass type, jlong renderWindowPtr) {

    Ogre::Window *renderWindow = (::Ogre::Window*) getLongAsPointer(renderWindowPtr);
    Ogre::Viewport* viewport = Ogre::Root::getSingleton().getRenderSystem()->getCurrentRenderViewports();
    // Ogre::Viewport *viewport = renderWindow->getViewport(0);
    return getPointerAsLong(viewport);
}

JNIEXPORT void JNICALL
Java_headwayent_blackholedarksun_BlackholeDarksunMain_initJvmData(JNIEnv *env, jobject instance) {

#if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_WIN32 || HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_IOS)
    initJavaVM(env);
#endif

}

JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_android_AndroidRenderWindow_setWindowProc(JNIEnv *env, jclass type,
                                                                        jclass clazz,
                                                                        jobject wndProc) {

    windowsDisplayClass = (jclass) env->NewGlobalRef(clazz);
    javaWindowProc = env->FromReflectedMethod(wndProc);

}

extern "C"
JNIEXPORT void JNICALL
Java_headwayent_blackholedarksun_physics_PhysicsUtility_getAabb__JLjava_lang_String_2Ljava_lang_String_2Lheadwayent_hotshotengine_ENG_1Vector3D_2Lheadwayent_hotshotengine_ENG_1Vector3D_2(
        JNIEnv *env, jclass type, jlong rootPtr, jstring meshName_, jstring groupName_, jobject centre,
        jobject halfSize) {
    const char *meshName = env->GetStringUTFChars(meshName_, 0);
    const char *groupName = env->GetStringUTFChars(groupName_, 0);

    Ogre::Root *root = (Ogre::Root*) getLongAsPointer(rootPtr);
    Ogre::String groupNameOgre = Ogre::ResourceGroupManager::AUTODETECT_RESOURCE_GROUP_NAME;
    const Ogre::MeshPtr &ptr = Ogre::MeshManager::getSingleton().load(meshName, groupNameOgre);
    const Ogre::Aabb &aabb = ptr->getAabb();

    env->CallVoidMethod(centre, vector3DSet, aabb.mCenter.x, aabb.mCenter.y, aabb.mCenter.z);
    env->CallVoidMethod(halfSize, vector3DSet, aabb.mHalfSize.x, aabb.mHalfSize.y, aabb.mHalfSize.z);

    env->ReleaseStringUTFChars(meshName_, meshName);
    env->ReleaseStringUTFChars(groupName_, groupName);
}

extern "C"
JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1CompositorWorkspaceNativeWrapper_removeWorkspace(
        JNIEnv *env, jclass type, jlong rootPtr, jlong workspacePtr) {

    Ogre::Root *root = (Ogre::Root*) getLongAsPointer(rootPtr);
    Ogre::CompositorWorkspace* compositorWorkspace = (Ogre::CompositorWorkspace*) workspacePtr;
    root->getCompositorManager2()->removeWorkspace(compositorWorkspace);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_headwayent_blackholedarksun_desktop_DesktopLauncher_test(JNIEnv *env, jclass type) {

    LOGI("hi!\n");
//    std::cout << "hi!";
//    printf("hi there!");
//    fflush(stdout);

}

extern "C"
JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1SilverBack_createDummyNode(JNIEnv *env, jclass type) {

    Hotshot::GorillaGUI::createDummyNode();

}

extern "C"
JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_gorillagui_ENG_1SilverBack_destroyDummyNode(JNIEnv *env,
                                                                          jclass type) {

    Hotshot::GorillaGUI::destroyDummyNode();

}

extern "C"
JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_ENG_1SceneManager_getRootSceneNode__JB(JNIEnv *env,
                                                                              jclass type,
                                                                              jlong ptr,
                                                                              jbyte sceneNodeType) {

    Ogre::SceneManager *sceneManager = (::Ogre::SceneManager*) ptr;
    Ogre::SceneNode *rootSceneNode = sceneManager->getRootSceneNode((Ogre::SceneMemoryMgrTypes) sceneNodeType);
    return getPointerAsLong(rootSceneNode);
}

extern "C"
JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_ENG_1SceneManager_setV1FastRenderQueue__JB(JNIEnv *env,
                                                                                  jclass type,
                                                                                  jlong ptr,
                                                                                  jbyte v1FastRenderQueue) {

    Ogre::SceneManager *sceneManager = (::Ogre::SceneManager*) ptr;
    sceneManager->getRenderQueue()->setRenderQueueMode(v1FastRenderQueue, Ogre::RenderQueue::V1_FAST);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1SceneCompositorWrapper_createSceneCompositor(
        JNIEnv *env, jclass type) {

    Hotshot::SceneCompositor *sceneCompositor = new Hotshot::SceneCompositor();
    return getPointerAsLong(sceneCompositor);

}

extern "C"
JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_renderer_nativeinterface_classwrappers_ENG_1SceneCompositorWrapper_destroySceneCompositor(
        JNIEnv *env, jclass type, jlong ptr) {

    Hotshot::SceneCompositor *sceneCompositor = (Hotshot::SceneCompositor*) ptr;
    delete sceneCompositor;

}

extern "C"
JNIEXPORT jint JNICALL
Java_headwayent_blackholedarksun_BlackholeDarksunMain_stringTest(JNIEnv *env, jobject instance, jstring oth_) {
    int result = 0;
#ifdef REDIRECT_OUTPUT_TO_FILE
    result = openOutputFile((Ogre::iOSDocumentsDirectory() + "/native_println_output.txt").c_str());
#endif
    const char *oth = env->GetStringUTFChars(oth_, 0);
    if (result == 0) {
        LOGI("%s", oth);
    }
    ::Ogre::IdString idString(oth);
    //LOGI("mHash=%d, mDebugString=%s", idString.mHash, idString.mDebugString);
    env->ReleaseStringUTFChars(oth_, oth);
    return result;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_android_AndroidRenderWindow_initializeView(JNIEnv *env, jclass type) {

    return 0;

}


#if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_IOS)
extern "C"
JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_android_AndroidRenderWindow_initializeViewController(JNIEnv *env,
                                                                                   jclass type, jlong uiWindowHandle_) {

    void *uiWindowHandle = getLongAsPointer(uiWindowHandle_);
    void* viewControllerPtr = initializeViewController(uiWindowHandle);
    return getPointerAsLong(viewControllerPtr);

}

extern "C"
JNIEXPORT jlong JNICALL
Java_headwayent_hotshotengine_android_AndroidRenderWindow_initializeAppDelegate(JNIEnv *env,
                                                                                jclass type) {

    return 0;

}

extern "C"
JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_android_AndroidRenderWindow_initializeViewControllerCallbacks(
        JNIEnv *env, jclass type, jobject main_) {

    gameViewControllerImpl = new GameViewControllerImpl(env, main_);
    initializeGameViewControllerCallbacks(gameViewControllerImpl);

}

extern "C"
JNIEXPORT void JNICALL
Java_headwayent_hotshotengine_android_AndroidRenderWindow_initializeMetalViewCallbacks(JNIEnv *env,
                                                                                       jclass type,
                                                                                       jobject iosInput) {

    metalViewCallbacksImpl = new MetalViewCallbacksImpl(env, iosInput);
    setMetalViewCallbacks(metalViewCallbacksImpl);

}

extern "C"
JNIEXPORT void JNICALL
Java_headwayent_blackholedarksun_MainActivity_pauseNative(JNIEnv *env, jclass type,
                                                          jlong uiViewController) {

    LOGI("%s", "pauseNative()");
    pause(getLongAsPointer(uiViewController));

}

extern "C"
JNIEXPORT void JNICALL
Java_headwayent_blackholedarksun_MainActivity_resumeNative(JNIEnv *env, jclass type,
                                                           jlong uiViewController) {

    LOGI("%s", "resumeNative()");
    resume(getLongAsPointer(uiViewController));

}
#endif
