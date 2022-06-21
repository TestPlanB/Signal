#include <jni.h>
#include <string>
#include <signal.h>
#include<android/log.h>

#define TAG "hi_signal"

/**
 * author : TestPlanB
 */

jobject currentObj;
JNIEnv *currentEnv = NULL;

void SigFunc(int sig_num,siginfo* info,void* ptr) {
    // 这里判空并不代表这个对象就是安全的，因为有可能是脏内存

    if (currentEnv == NULL || currentObj == NULL) {
        return;
    }
    __android_log_print(ANDROID_LOG_INFO, TAG, "%d catch", sig_num);
    __android_log_print(ANDROID_LOG_INFO, TAG, "crash info pid:%d ", info->si_pid);
    jclass main = currentEnv->FindClass("com/example/lib_signal/SignalController");
    jmethodID id = currentEnv->GetMethodID(main, "callNativeException", "(I)V");
    if (!id) {
        return;
    }
    currentEnv->CallVoidMethod(currentObj, id, sig_num);
    currentEnv->DeleteGlobalRef(currentObj);


}

extern "C" jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    jint result = -1;
    // 直接用vm进行赋值，不然不可靠
    if (vm->GetEnv((void **) &currentEnv, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }
    return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {

}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_lib_1signal_SignalController_initWithSignals(JNIEnv *env, jobject thiz,
                                                              jintArray signals) {
    // 必须生成全局变量，直接赋值的话只是局部变量，被回收后存在脏变量风险
    currentObj = env->NewGlobalRef(thiz);
    // 注意释放内存
    jint *signalsFromJava = env->GetIntArrayElements(signals, 0);
    int size = env->GetArrayLength(signals);
    do {
        struct sigaction sigc;
        //sigc.sa_handler = SigFunc;
        sigc.sa_sigaction = SigFunc;
        sigemptyset(&sigc.sa_mask);
        sigc.sa_flags = SA_SIGINFO;




        // 注册所有信号
        for (int i = 0; i < size; i++) {
            // 这里不需要旧的处理函数
            int flag = sigaction(signalsFromJava[i], &sigc, NULL);
            if (flag == -1) {
                __android_log_print(ANDROID_LOG_INFO, TAG, "register fail ===== signals[%d] ",
                                    i);
                // 异常处理
                jclass main = currentEnv->FindClass("com/example/lib_signal/SignalController");
                jmethodID id = currentEnv->GetStaticMethodID(main, "signalError", "()V");
                env->CallStaticVoidMethod(main,id);
                break;
            }
        }


    } while (0);

    env->ReleaseIntArrayElements(signals, signalsFromJava, 0);

}
