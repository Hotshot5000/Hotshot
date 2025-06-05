//
// Created by sebas on 20-Aug-17.
//

#include "CommandBufferUtility.h"

int writingBufferSizeInBytes;
int currentWriteBufferWrittenSize = 0;
PtrMap ptrMap;
PtrToDummyPtrMap ptrToDummyPtrMap;

void checkWriteBufferOverflow(int bytesToAdd) {
    currentWriteBufferWrittenSize += bytesToAdd;
    if (currentWriteBufferWrittenSize > writingBufferSizeInBytes)
    {
        Ogre::String s("Write buffer overflow. currentWriteBufferWrittenSize: " + SSTR(currentWriteBufferWrittenSize) +
                       " writingBufferSizeInBytes: " + SSTR(writingBufferSizeInBytes));
        LOGI("%s", s.c_str());
        exit(-1);
    }
}

void putInPointerMap(long long int val, void *ptr, const char *type) {
    const std::pair<PtrMap::iterator, bool> &pair = ptrMap.insert(
            std::make_pair(val, ptr));
    const std::pair<PtrToDummyPtrMap::iterator, bool> &reversePair = ptrToDummyPtrMap.insert(
            std::make_pair(ptr, val));
//    Ogre::String s("putInPointerMap val: " + SSTR(val) + " with pointer: " + SSTR(ptr) +
//        " ptrMap.size: " + SSTR(ptrMap.size()) + " ptrToDummyPtrMap.size: " + SSTR(ptrToDummyPtrMap.size()) + " type: " + (type ? type : "0"));
//    LOGI("%s\n", s.c_str());
#ifdef NATIVE_DEBUG
    if (!pair.second)
    {
        Ogre::String s("Could not add pair with val: " + SSTR(val) + " and with pointer: " + SSTR(ptr));
        LOGI("%s", s.c_str());
        exit(-1);
    }
    if (!reversePair.second)
    {
        Ogre::String s("Could not add reverse pair with val: " + SSTR(val) + " and with pointer: " + SSTR(ptr));
        LOGI("%s", s.c_str());
        exit(-1);
    }
#endif
}

void removePointerFromMap(long long int val, bool justCreated) {
    unsigned int erasedCount = 0;
    
    
    if (justCreated)
    {
        const PtrMap::iterator &it = ptrMap.find(val);
//        Ogre::String s("removePointerFromMap with val: " + SSTR(val) + " and justCreated: " + SSTR((justCreated ? "true" : "false")) +
//            " ptrMap.size: " + SSTR(ptrMap.size()) + " ptrToDummyPtrMap.size: " + SSTR(ptrToDummyPtrMap.size()));
//        LOGI("%s\n", s.c_str());

        // Make sure you erase it first from here before erasing from ptrMap since the iterator will no longer be valid.
        erasedCount = ptrToDummyPtrMap.erase(it->second);
#ifdef NATIVE_DEBUG
        if (erasedCount != 1)
        {
            Ogre::String s("Could not find to remove val " + SSTR(it->second) + " with val: " + SSTR(val));
            LOGI("%s", s.c_str());
            exit(-1);
        }
#endif
        erasedCount = ptrMap.erase(val);

#ifdef NATIVE_DEBUG
        if (erasedCount != 1)
        {
            Ogre::String s("Could not find to remove ptr with val: " + SSTR(val));
            LOGI("%s", s.c_str());
            exit(-1);
        }
#endif

    }
    else
    {
        void *ptr = getLongAsPointer(val);
        const PtrToDummyPtrMap::iterator &it = ptrToDummyPtrMap.find(ptr);
        if (it != ptrToDummyPtrMap.end())
        {
            long long int dummyPtr = it->second;
            erasedCount = ptrMap.erase(dummyPtr);
//            Ogre::String s("removePointerFromMap with ptr: " + SSTR(ptr) + " for val: " + SSTR(val) + " for dummyPtr: " + SSTR(dummyPtr) +
//                " and justCreated: " + SSTR((justCreated ? "true" : "false")) +
//                " ptrMap.size: " + SSTR(ptrMap.size()) + " ptrToDummyPtrMap.size: " + SSTR(ptrToDummyPtrMap.size()));
//            LOGI("%s\n", s.c_str());
#ifdef NATIVE_DEBUG
            if (erasedCount != 1)
            {
                Ogre::String s("Could not find to remove dummy ptr with val: " + SSTR(dummyPtr));
                LOGI("%s", s.c_str());
                exit(-1);
            }
#endif
            erasedCount = ptrToDummyPtrMap.erase(ptr);
#ifdef NATIVE_DEBUG
            if (erasedCount != 1)
            {
                Ogre::String s("Could not find to remove ptr with val: " + SSTR(val));
                LOGI("%s", s.c_str());
                exit(-1);
            }
#endif
        }
        else
        {
//            Ogre::String s("Could not find ptr: " + SSTR(ptr) + " for val: " + SSTR(val) + " and justCreated: " + SSTR((justCreated ? "true" : "false")));
//            LOGI("%s\n", s.c_str());
        }
    }


}
