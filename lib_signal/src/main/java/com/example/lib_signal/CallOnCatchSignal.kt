package com.example.lib_signal

import android.content.Context

interface CallOnCatchSignal {
    fun onCatchSignal(context: Context,signal: Int,nativeStackTrace:String)
}
