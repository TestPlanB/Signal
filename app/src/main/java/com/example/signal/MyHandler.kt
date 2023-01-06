package com.example.signal

import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import com.pika.lib_signal.CallOnCatchSignal
import com.pika.lib_signal.SignalController
import com.pika.lib_signal.utils.Utils

class MyHandler : CallOnCatchSignal {
    companion object{
        const val TAG = "hi_signal"
    }

    // 自定义的anr处理，这里由使用者的规则判定收到SIGQUIT信号后的行为
    @RequiresApi(Build.VERSION_CODES.M)
    override fun checkIsAnr(): Boolean {
        try {
            val queue = Looper.getMainLooper().queue
            val field = queue.javaClass.getDeclaredField("mMessages")
            field.isAccessible = true
            val message = field.get(queue) as Message
            // 注意！！！这里应该根据实际逻辑判断，比如在前台的话就相应的判断，比如超出5s，这里只是简单比较演示
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

    override fun handleAnr(context: Context,logcat: String) {

        Log.e(TAG, "发生了anr，重启")
        Log.e(TAG,"logcat start -----------------------")
        Log.e(TAG,logcat)
        Log.e(TAG,"logcat end   -----------------------")

        val restart: Intent? =
            context.packageManager.getLaunchIntentForPackage(context.packageName)
        restart?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        restart?.action = "restart"
        context.startActivity(restart)
        Process.killProcess(Process.myPid())
        System.exit(0)
    }

    override fun handleCrash(context: Context,signal:Int,logcat:String) {

        Log.e(TAG, "发生了crash，信号是$signal 重启")
        Log.e(TAG,"logcat start -----------------------")
        Log.e(TAG,logcat)
        Log.e(TAG,"logcat end   -----------------------")


        val restart: Intent? =
            context.packageManager.getLaunchIntentForPackage(context.packageName)
        restart?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        restart?.action = "restart"
        context.startActivity(restart)
        Process.killProcess(Process.myPid())
        System.exit(0)
    }

}