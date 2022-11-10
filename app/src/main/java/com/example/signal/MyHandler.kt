package com.example.signal

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Process
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.example.lib_signal.CallOnCatchSignal
import com.example.lib_signal.SignalConst
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyHandler : CallOnCatchSignal {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCatchSignal(
        context: Context,
        signal: Int,
        nativeStackTrace: String,
        javaStackTrace: String
    ) {
        // 自定义处理，比如弹出一个toast，或者更友好的交互
        Log.i("hello", "custom onCatchSignal ")

        if (checkIsANR(signal)) {
            Toast.makeText(context, "自定义anr 处理", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "自定义native crash 处理", Toast.LENGTH_LONG).show()
        }
        // 打印native堆栈
        Log.e("hello", "当前native 堆栈是 $nativeStackTrace")

        // 打印java堆栈
        Log.e("hello", "java 堆栈是 $javaStackTrace")

        val restart: Intent? =
            context.packageManager.getLaunchIntentForPackage(context.packageName)
        restart?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        restart?.action = "restart"
        context.startActivity(restart)
        Process.killProcess(Process.myPid())
        System.exit(0)


    }

    //  判断是否是anr
    @SuppressLint("DiscouragedPrivateApi")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkIsANR(signal: Int): Boolean {

        // 如果不是SIGQUIT，就不进入anr判断
        if (signal != SignalConst.SIGQUIT) {
            return false
        }

        try {
            val queue = Looper.getMainLooper().queue
            val field = queue.javaClass.getDeclaredField("mMessages")
            field.isAccessible = true
            val message = field.get(queue) as Message
            // 这里应该根据实际逻辑判断，比如在前台的话就相应的判断，比如超出5s，这里简单比较演示
            return message.`when` < SystemClock.uptimeMillis() - 5000

            // 可以dump这些消息
            //        val processesInErrorStates = systemService.processesInErrorState
//        Log.i("hello", "processesInErrorStates ${processesInErrorStates}")
//        Log.i("hello", "checkIsANR")
//        Log.i("hello", "currentPid $currentPid")
//
//        processesInErrorStates?.let {
//            it.forEach { info ->
//                Log.i("hello", "pid is ${info.pid} ========${info.condition}")
//                if (info.pid == currentPid && info.condition == ActivityManager.ProcessErrorStateInfo.NOT_RESPONDING) {
//                    Log.i("hello", "发生了anr")
//                    return true
//                }
//            }
//        }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }
}