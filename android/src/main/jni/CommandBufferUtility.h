//
// Created by sebas on 20-Aug-17.
//

#ifndef BLACKHOLEDARKSUNONLINE5_COMMANDBUFFERUTILITY_H
#define BLACKHOLEDARKSUNONLINE5_COMMANDBUFFERUTILITY_H

#include "HotshotCommon.h"
#include "OgrePrerequisites.h"
#include "OgreRoot.h"
#include "OgreParticleFXPlugin.h"
#include "OgreConfigFile.h"
#include "OgreViewport.h"
#include "OgreCamera.h"
#include "OgreItem.h"
#include "OgreHlmsUnlit.h"
#include "OgreHlmsPbs.h"
#include "OgreArchiveManager.h"
#include "Compositor/OgreCompositorManager2.h"
#include "Compositor/OgreCompositorWorkspace.h"
#include "OgreOverlaySystem.h"
#include "OgreOverlayManager.h"
#include "OgreOverlay.h"
#include "OgreOverlayContainer.h"
#include "OgrePanelOverlayElement.h"
#include "OgreTextAreaOverlayElement.h"
#include "OgreTechnique.h"
#include "OgreFontManager.h"
#include "OgreFrameStats.h"
#include "OgreHlmsUnlitDatablock.h"
#include "OgreWindowEventUtilities.h"
#include "HotshotLoadingScreen.h"
#include "HotshotGorillaGUIScreenRenderable.h"

#define NATIVE_DEBUG
typedef Ogre::unordered_map<long long int, void*>::type PtrMap;
// We also need to know
typedef Ogre::unordered_map<void*, long long int>::type PtrToDummyPtrMap;
extern int writingBufferSizeInBytes;
extern int currentWriteBufferWrittenSize;
extern PtrMap ptrMap;
extern PtrToDummyPtrMap ptrToDummyPtrMap;

inline void incrementPointer(char** buf, int len)
{
    *buf += len;
}

template <class T>
class BufferRead
{
public:
    T read(char** buf)
    {
        T val = *(reinterpret_cast<T*>(*buf));
        incrementPointer(buf, sizeof(T));
        return val;
    }
};

void checkWriteBufferOverflow(int bytesToAdd);

template <typename T>
void write(char** buf, T val)
{
    *(reinterpret_cast<T*>(*buf)) = val;
    incrementPointer(buf, sizeof(T));
#ifdef NATIVE_DEBUG
    checkWriteBufferOverflow(sizeof(T));
#endif
}

void putInPointerMap(long long int val, void* ptr, const char *type = 0);

inline void* getLongAsPointer(long long l)
{
    return reinterpret_cast<void*>(l);
}

void removePointerFromMap(long long int val, bool justCreated);

/**
 * THE FOLLOWING ASSUMPTION IS WRONG:
 * This should be called once per frame. If we actually have pointers in this map
 * it means that they will be resolved in this frame, so they have no purpose for the next frames.
 */
//inline void removeAllPointersFromMap()
//{
//    ptrMap.clear();
//}

inline bool getPointerInMap(long long int val, void** ptr)
{
    const PtrMap::iterator &iterator = ptrMap.find(val);
    if (iterator == ptrMap.end())
    {
#ifdef NATIVE_DEBUG
        Ogre::String s("Could not find ptr for val: " + SSTR(val));
        LOGI("%s", s.c_str());
        exit(-1);
#endif
        return false;
    }
    *ptr = iterator->second;
    return true;
}

inline unsigned int alignPointer(char** p, unsigned int align)
{
    unsigned long long ptr = reinterpret_cast<unsigned long long>(*p);
    unsigned long long offset = ptr % align;
    if (offset > 0)
    {
        unsigned int bytesAdvanced = align - offset;
        *p += bytesAdvanced;
        return bytesAdvanced;
    }
    return 0;
}



/**
 * This also aligns buffer to prepare for reading the long pointer to which this flag refers.
 * @param buf
 * @return
 */
inline unsigned char getPointerFlag(char** buf)
{
    BufferRead<unsigned char> bufferRead;
    unsigned char flag = bufferRead.read(buf);
    alignPointer(buf, 4);
    return flag;
}

inline bool isPointerJustCreated(unsigned char flag)
{
    return (flag & 0x1) != 0;
}

inline void* getNativePointer(long long int val, bool pointerJustCreated)
{
    if (pointerJustCreated)
    {
        void* ptr;
        getPointerInMap(val, &ptr);
        return ptr;
    }
    else
    {
        removePointerFromMap(val, false);
        return getLongAsPointer(val);
    }
}

inline void savePointer(char** wbuf, void* ptr)
{
    *(reinterpret_cast<long long*>(*wbuf)) = reinterpret_cast<long long>(ptr);
    incrementPointer(wbuf, sizeof(long long));
}



inline void resetWriteBufferOverflow()
{
    currentWriteBufferWrittenSize = 0;
}

inline int getCurrentWriteBufferWrittenSize()
{
    return currentWriteBufferWrittenSize;
}

inline void writePtr(char** buf, long long int ptr)
{
    alignPointer(buf, 4);
    write(buf, ptr);
}

inline void writeString(char** buf, const Ogre::String& s)
{
    unsigned int length = s.length();
    **buf = static_cast<char>(length);
    ++(*buf);
    memcpy(*buf, s.c_str(), length);
    incrementPointer(buf, length);
#ifdef NATIVE_DEBUG
    checkWriteBufferOverflow(length);
#endif
}

inline void writeColour(char** buf, Ogre::ColourValue col)
{
    write(buf, col.r);
    write(buf, col.g);
    write(buf, col.b);
    write(buf, col.a);
}

inline Ogre::ColourValue readColour(char ** buf)
{
    BufferRead<float> bufferRead;
    float r = bufferRead.read(buf);
    float g = bufferRead.read(buf);
    float b = bufferRead.read(buf);
    float a = bufferRead.read(buf);
    return Ogre::ColourValue(r, g, b, a);
}

inline void writeVector2D(char** buf, Ogre::Vector2 v)
{
    write(buf, v.x);
    write(buf, v.y);
}

inline void writeVector3D(char** buf, Ogre::Vector3 v)
{
    write(buf, v.x);
    write(buf, v.y);
    write(buf, v.z);
}

inline void writeVector4D(char** buf, Ogre::Vector4 v)
{
    write(buf, v.x);
    write(buf, v.y);
    write(buf, v.z);
}

inline void writeVector4DFull(char** buf, Ogre::Vector4 v)
{
    write(buf, v.x);
    write(buf, v.y);
    write(buf, v.z);
    write(buf, v.w);
}

inline void writeMatrix3(char** buf, Ogre::Matrix3& mat)
{
    for (int i = 0; i < 3; ++i)
    {
        Ogre::Real* p = mat[i];
        write(buf, *p++);
        write(buf, *p++);
        write(buf, *p++);
    }
}

inline void writeMatrix4(char** buf, Ogre::Matrix4& mat)
{
    for (int i = 0; i < 4; ++i)
    {
        Ogre::Real* p = mat[i];
        write(buf, *p++);
        write(buf, *p++);
        write(buf, *p++);
        write(buf, *p++);
    }
}

inline void writeAxisAlignedBox(char** buf, Ogre::AxisAlignedBox box)
{
    writeVector3D(buf, box.getMinimum());
    writeVector3D(buf, box.getMaximum());
}

inline Ogre::Vector2 readVector2D(char** buf)
{
    BufferRead<float> bufferRead;
    float x = bufferRead.read(buf);
    float y = bufferRead.read(buf);
    return Ogre::Vector2(x, y);
}

inline Ogre::Vector3 readVector3D(char** buf)
{
    BufferRead<float> bufferRead;
    float x = bufferRead.read(buf);
    float y = bufferRead.read(buf);
    float z = bufferRead.read(buf);
    return Ogre::Vector3(x, y, z);
}

inline Ogre::Vector4 readVector4DAsPt(char** buf)
{
    BufferRead<float> bufferRead;
    float x = bufferRead.read(buf);
    float y = bufferRead.read(buf);
    float z = bufferRead.read(buf);
    return Ogre::Vector4(x, y, z, 1.0f);
}

inline Ogre::Vector4 readVector4DAsVec(char** buf)
{
    BufferRead<float> bufferRead;
    float x = bufferRead.read(buf);
    float y = bufferRead.read(buf);
    float z = bufferRead.read(buf);
    return Ogre::Vector4(x, y, z, 0.0f);
}

inline Ogre::Vector4 readVector4DFull(char** buf)
{
    BufferRead<float> bufferRead;
    float x = bufferRead.read(buf);
    float y = bufferRead.read(buf);
    float z = bufferRead.read(buf);
    float w = bufferRead.read(buf);
    return Ogre::Vector4(x, y, z, w);
}

inline Ogre::String readString(char** buf)
{
    unsigned char strlen = (unsigned char) **buf;
    ++(*buf);
    Ogre::String str(*buf, strlen);
    incrementPointer(buf, strlen);
    return str;
}

inline Ogre::Quaternion readQuaternion(char** buf)
{
    BufferRead<float> bufferRead;
    float x = bufferRead.read(buf);
    float y = bufferRead.read(buf);
    float z = bufferRead.read(buf);
    float w = bufferRead.read(buf);
    return Ogre::Quaternion(w, x, y, z);
}

inline Ogre::Matrix3 readMatrix3(char** buf)
{
    BufferRead<float> bufferRead;
    Ogre::Real arr[3][3];
    Ogre::Real *ptr = (Ogre::Real *) &arr[0][0];
    Ogre::Matrix3 mat;
    for (int i = 0; i < 9; ++i)
    {
        *ptr++ = bufferRead.read(buf);
    }
    return Ogre::Matrix3(arr);
}

inline Ogre::Matrix4 readMatrix4(char** buf)
{
    BufferRead<float> bufferRead;
	float v[16];

    for (int i = 0; i < 16; ++i)
    {
        v[i] = bufferRead.read(buf);
    }
	return Ogre::Matrix4(v[0], v[1], v[2], v[3],
                         v[4], v[5], v[6], v[7],
                         v[8], v[9], v[10], v[11],
                         v[12], v[13], v[14], v[15]);
}

inline Ogre::AxisAlignedBox readAxisAlignedBox(char** buf)
{
    const Ogre::Vector3 &min = readVector3D(buf);
    const Ogre::Vector3 &max = readVector3D(buf);
    return Ogre::AxisAlignedBox(min, max);
}

inline std::map<Ogre::String, Ogre::String> readMapStringString(char** buf)
{
    unsigned char mapSize = (unsigned char) **buf;
    ++(*buf);
    std::map<Ogre::String, Ogre::String> map;
    for (int i = 0; i < mapSize; ++i)
    {
        const Ogre::String &key = readString(buf);
        const Ogre::String &value = readString(buf);
        map.insert(make_pair(key, value));
    }
    return map;
}

inline std::map<Ogre::String, int> readMapStringInt(char** buf)
{
    unsigned char mapSize = (unsigned char) **buf;
    ++(*buf);
    std::map<Ogre::String, int> map;
    BufferRead<int> bufferRead;
    for (int i = 0; i < mapSize; ++i)
    {
        const Ogre::String &key = readString(buf);
        const int &value = bufferRead.read(buf);
//        LOGI("%i", value);
        map.insert(make_pair(key, value));
    }
    return map;
}

inline long long getPointerAsLong(void *p)
{
    return reinterpret_cast<long long>(p);
}

#endif //BLACKHOLEDARKSUNONLINE5_COMMANDBUFFERUTILITY_H
