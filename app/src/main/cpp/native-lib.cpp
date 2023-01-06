#include <jni.h>
#include <string>
#include<android/log.h>
#include "unwind.h"



// 测试crash
extern "C"
JNIEXPORT void JNICALL
Java_com_example_signal_MainActivity_throwNativeCrash(JNIEnv *env, jobject thiz) {
    // 向自身发送一个信号
    raise(SIGABRT);

}