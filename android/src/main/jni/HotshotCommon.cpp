//
// Created by sebas on 26-Feb-18.
//
#include "HotshotCommon.h"
#include "OgreLogManager.h"

jclass systemCls;
jmethodID nanoTime;
#ifdef REDIRECT_OUTPUT_TO_FILE
FILE *outputFile = NULL;

int openOutputFile(const char *filename)
{
    outputFile = fopen(filename, "a");
    if (outputFile == NULL)
    {
        return errno;
    }
    return 0;
}

void closeOutputFile()
{
    if (outputFile)
    {
        fclose(outputFile);
    }
}
#endif

void log(std::string outString)
{
    const int maxLogSize = 1000;
    for (int i = 0; i <= outString.length() / maxLogSize; i++) {
        int start = i * maxLogSize;
        int end = (i + 1) * maxLogSize;
        end = end > outString.length() ? outString.length() : end;
        std::stringstream str;
        const std::string chunk = outString.substr(start, end);
        str << chunk;
        Ogre::LogManager::getSingleton().logMessage(Ogre::LML_NORMAL, str.str());
    }
}

void initializeNative(JNIEnv *env)
{
    systemCls = env->FindClass("java/lang/System");
    nanoTime = env->GetStaticMethodID(systemCls, "nanoTime", "()J");
}

long long getNanoTime(JNIEnv *env)
{
    return env->CallStaticLongMethod(systemCls, nanoTime);
}

