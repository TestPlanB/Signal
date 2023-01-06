package com.example.signal

import android.app.Application
import com.pika.lib_signal.SignalConst
import com.pika.lib_signal.SignalController

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SignalController.initSignal(intArrayOf(
            SignalConst.SIGQUIT,
            SignalConst.SIGABRT,
            SignalConst.SIGSEGV),this,MyHandler())
    }
}