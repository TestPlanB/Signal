package com.example.signal

import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import android.widget.Toast
import com.example.lib_signal.CallOnCatchSignal

class MyHandler :CallOnCatchSignal {
    override fun onCatchSignal(signal: Int,context: Context) {
        // 自定义处理，比如弹出一个toast，或者更友好的交互
        Log.i("hello","custom onCatchSignal ")
        Toast.makeText(context,"自定义native crash 处理",Toast.LENGTH_LONG).show()
        val restart: Intent? =
            context.packageManager.getLaunchIntentForPackage(context.packageName)
        restart?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        restart?.action = "restart"
        context.startActivity(restart)
        Process.killProcess(Process.myPid())
        System.exit(0)
    }
}