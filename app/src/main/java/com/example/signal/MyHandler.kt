package com.example.signal

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import com.example.lib_signal.CallOnCatchSignal

class MyHandler : CallOnCatchSignal {
    override fun onCatchSignal(context: Context,signal: Int, nativeStackTrace:String) {
        // 自定义处理，比如弹出一个toast，或者更友好的交互
        Log.i("hello", "custom onCatchSignal ")
        val isANR = checkIsANR(signal, context)
        Log.i("hello", "isANR: $isANR")
        if (isANR) {
            Toast.makeText(context, "自定义anr 处理", Toast.LENGTH_LONG).show()
        }else {
            Toast.makeText(context, "自定义native crash 处理", Toast.LENGTH_LONG).show()
        }
        // 打印native堆栈
        Toast.makeText(context, "当前native 堆栈是 $nativeStackTrace", Toast.LENGTH_LONG).show()
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
    private fun checkIsANR(signal: Int, context: Context): Boolean {
//        val systemService = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        val currentPid = Process.myPid()
//        Log.i("hello", "systemService ${systemService}")

        try {
            val queue = if (Looper.myLooper() == Looper.getMainLooper()) {
                Looper.myQueue()
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Looper.getMainLooper().queue
            } else {
                Looper.myQueue()
            }
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