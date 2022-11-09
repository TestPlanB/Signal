#include <jni.h>
#include <string>
#include <signal.h>
#include<android/log.h>
#include "unwind-utils.h"


#define TAG "hi_signal"
#define SIGNAL_CRASH_STACK_SIZE (1024 * 128)

/**
 * author : TestPlanB
 */

jobject currentObj;
JNIEnv *currentEnv = nullptr;

//uintptr_t getPc(const ucontext_t *uc) {
//#if (defined(__arm__))
//    return uc->uc_mcontext.arm_pc;
//#elif defined(__aarch64__)
//    return uc->uc_mcontext.pc;
//#elif (defined(__x86_64__))
//    return uc->uc_mcontext.gregs[REG_RIP];
//#else
//#error "unsupport"
//#endif
//}


void SigFunc(int sig_num, siginfo *info, void *ptr) {

    // 这里判空并不代表这个对象就是安全的，因为有可能是脏内存

    if (currentEnv == nullptr || currentObj == nullptr) {
        return;
    }
    __android_log_print(ANDROID_LOG_INFO, TAG, "%d catch", sig_num);
    __android_log_print(ANDROID_LOG_INFO, TAG, "crash info pid:%d ", info->si_pid);
    jclass main = currentEnv->FindClass("com/example/lib_signal/SignalController");
    jmethodID id = currentEnv->GetMethodID(main, "callNativeException", "(ILjava/lang/String;)V");
    if (!id) {
        return;
    }

    jstring nativeStackTrace  = currentEnv->NewStringUTF(backtraceToLogcat().c_str());
    currentEnv->CallVoidMethod(currentObj, id, sig_num,nativeStackTrace);

    // 释放资源
    currentEnv->DeleteGlobalRef(currentObj);
    currentEnv->DeleteLocalRef(nativeStackTrace);


}
// 统一处理异常
void Handle_Exception(){
    // 异常处理
    jclass main = currentEnv->FindClass("com/example/lib_signal/SignalController");
    jmethodID id = currentEnv->GetStaticMethodID(main, "signalError", "()V");
    currentEnv->CallStaticVoidMethod(main, id);
}

extern "C" jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    jint result = -1;
    // 直接用vm进行赋值，不然不可靠
    backtraceToLogcat();
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
    bool needMask = false;

    for (int i = 0; i < size; i++) {
        if (signalsFromJava[i] == SIGQUIT) {
            needMask = true;
        }
    }

    do {
        sigset_t mask;
        sigset_t old;
        // 这里需要stack_t，临时的栈，因为SIGSEGV时，当前栈空间已经是处于进程所能控制的栈中，此时就不能在这个栈里面操作，否则就会异常循环
        stack_t ss;
        if(NULL == (ss.ss_sp = calloc(1, SIGNAL_CRASH_STACK_SIZE))){
            Handle_Exception();
            break;
        }
        ss.ss_size  = SIGNAL_CRASH_STACK_SIZE;
        ss.ss_flags = 0;
        if(0 != sigaltstack(&ss, NULL)) {
            Handle_Exception();
            break;
        }

        if (needMask) {
            sigemptyset(&mask);
            sigaddset(&mask, SIGQUIT);
            if (0 != pthread_sigmask(SIG_UNBLOCK, &mask, &old)) {
                break;
            }
        }


        struct sigaction sigc;
        //sigc.sa_handler = SigFunc;
        sigc.sa_sigaction = SigFunc;
        sigemptyset(&sigc.sa_mask);
        sigc.sa_flags = SA_SIGINFO| SA_ONSTACK;


        // 注册所有信号
        for (int i = 0; i < size; i++) {
            // 这里不需要旧的处理函数
            // 指定SIGKILL和SIGSTOP以外的所有信号
            int flag = sigaction(signalsFromJava[i], &sigc, nullptr);
            if (flag == -1) {
                __android_log_print(ANDROID_LOG_INFO, TAG, "register fail ===== signals[%d] ",
                                    i);
                Handle_Exception();
                // 失败后需要恢复原样
                if (needMask) {
                    pthread_sigmask(SIG_UNBLOCK, &old, nullptr);
                }
                break;
            }
        }


    } while (0);

    env->ReleaseIntArrayElements(signals, signalsFromJava, 0);

}
