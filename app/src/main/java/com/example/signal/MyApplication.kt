package com.example.signal

import android.app.Application
import com.example.lib_signal.SignalConst
import com.example.lib_signal.SignalController

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SignalController(this).initSignal(intArrayOf(SignalConst.SIGQUIT,SignalConst.SIGABRT,SignalConst.SIGSEGV))
    }
}