//
// Created by sebas on 22.02.2017.
//

#ifndef BLACKHOLEDARKSUNONLINE4_HOTSHOTCOMMON_H
#define BLACKHOLEDARKSUNONLINE4_HOTSHOTCOMMON_H

#define HOTSHOT_PLATFORM_WIN32 1
#define HOTSHOT_PLATFORM_LINUX 2
#define HOTSHOT_PLATFORM_MACOS 3
#define HOTSHOT_PLATFORM_ANDROID 4
#define HOTSHOT_PLATFORM_IOS 5
#define HOTSHOT_PLATFORM_EMSCRIPTEN 6
#define HOTSHOT_PLATFORM_WIN32_GLES3_ANGLE 7

#define HOTSHOT_MODE_CLIENT 1
#define HOTSHOT_MODE_SERVER 2

#define HOTSHOT_PLATFORM HOTSHOT_PLATFORM_WIN32

#define HOTSHOT_MODE HOTSHOT_MODE_CLIENT

// #define REDIRECT_OUTPUT_TO_FILE

#include <stdio.h>

#ifdef REDIRECT_OUTPUT_TO_FILE
extern FILE *outputFile;
int openOutputFile(const char* filename);
void closeOutputFile();
#endif

#if (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_WIN32)

#ifdef REDIRECT_OUTPUT_TO_FILE
#define LOGI(...) do {(void)fprintf(outputFile, __VA_ARGS__); fflush(outputFile);} while(false);
#define LOGW(...) do {(void)fprintf(outputFile, __VA_ARGS__); fflush(outputFile);} while(false);
#else
#define LOGI(...) do {(void)printf(__VA_ARGS__); fflush(stdout);} while(false);
#define LOGW(...) do {(void)printf(__VA_ARGS__); fflush(stdout);} while(false);
#endif

#include <sstream>

#define SSTR( x ) static_cast< std::ostringstream & >( \
        ( std::ostringstream() << std::dec << x ) ).str()

#elif (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_ANDROID)

#include <android/log.h>

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "native-activity", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "native-activity", __VA_ARGS__))

#include <sstream>

#define SSTR( x ) static_cast< std::ostringstream & >( \
        ( std::ostringstream() << std::dec << x ) ).str()

#elif (HOTSHOT_PLATFORM == HOTSHOT_PLATFORM_IOS)

#ifdef REDIRECT_OUTPUT_TO_FILE
#define LOGI(...) do {(void)fprintf(outputFile, __VA_ARGS__); fflush(outputFile);} while(false);
#define LOGW(...) do {(void)fprintf(outputFile, __VA_ARGS__); fflush(outputFile);} while(false);
#else
#define LOGI(...) do {(void)printf(__VA_ARGS__); fflush(stdout);} while(false);
#define LOGW(...) do {(void)printf(__VA_ARGS__); fflush(stdout);} while(false);
#endif

#include <sstream>

template <typename T>
std::string SSTR(const T& value) {
    std::stringstream ss;
    ss << value;
    return ss.str();
}


#endif

#include <string>
#include <jni.h>

void log(std::string outString);

long long getNanoTime(JNIEnv *env);
void initializeNative(JNIEnv *env);

#endif //BLACKHOLEDARKSUNONLINE4_HOTSHOTCOMMON_H
