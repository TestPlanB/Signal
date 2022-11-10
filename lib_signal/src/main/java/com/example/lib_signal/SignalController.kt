package com.example.lib_signal

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import androidx.annotation.Keep
import java.util.*

/**
 * author : TestPlanB
 */


@Keep
object SignalController {
    private var application: Application? = null

    init {
        System.loadLibrary("keep-signal")
    }

    @JvmStatic
    fun signalError() {
        throw SignalException()
    }


    @JvmStatic
    fun callNativeException(signal: Int, nativeStackTrace: String) {
        Log.i("hi_signal", "callNativeException $signal")
        // 获取java堆栈
        val javaStackTrace = Log.getStackTraceString(Throwable())
        Log.i("hi_signal", "javaStackTrace")
        val load = ServiceLoader.load(CallOnCatchSignal::class.java)
        Log.i("hi_signal", "load")
        load.forEach {
            application?.let { it1 ->
                it.onCatchSignal(
                    it1,
                    signal,
                    nativeStackTrace,
                    javaStackTrace
                )
            }
        }

    }

    @JvmStatic
    fun initSignal(signals: IntArray, application: Application) {
        this.application = application
        initWithSignals(signals)
    }

    @JvmStatic
    private external fun initWithSignals(signals: IntArray)


}
