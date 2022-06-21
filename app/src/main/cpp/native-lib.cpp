#include <jni.h>
#include <string>
#include<android/log.h>



// 测试crash
extern "C"
JNIEXPORT void JNICALL
Java_com_example_signal_MainActivity_throwNativeCrash(JNIEnv *env, jobject thiz) {
    int i = 0 / 0;
    jstring j = (jstring) "132" + i;
    char *name = const_cast<char *>(env->GetStringUTFChars(j, NULL));
    __android_log_print(ANDROID_LOG_INFO, "hello", "%s", &"jni will crash"[(*name)]);

}