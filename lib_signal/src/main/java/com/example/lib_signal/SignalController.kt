package com.example.lib_signal

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
class SignalController(private val context: Context) {
    companion object {
        init {
            System.loadLibrary("keep-signal")
        }

        @JvmStatic
        fun signalError() {
            throw SignalException()
        }
    }


    fun callNativeException(signal: Int,nativeStackTrace:String) {
        Log.i("hello", "callNativeException $signal")
        var hasCustomHandler = false

        val load = ServiceLoader.load(CallOnCatchSignal::class.java)
        load.forEach {
            hasCustomHandler = true
            it.onCatchSignal(context,signal,nativeStackTrace)
        }

        // context 可能为application等无任务栈的context，需要添加任务栈标记FLAG_ACTIVITY_NEW_TASK
        // 默认处理
        if (!hasCustomHandler) {
            val killIntent: Intent? =
                context.packageManager.getLaunchIntentForPackage(context.packageName)
            killIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            killIntent?.action = "restart"
            context.startActivity(killIntent)
            Process.killProcess(Process.myPid())
            System.exit(0)
        }
    }


    external fun initWithSignals(signals: IntArray)


}
