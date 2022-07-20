package com.example.lib_signal

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


internal object SignalLog {
    lateinit var process:Process

    fun create(){
        process = ProcessBuilder("logcat", "-v", "process").start()
    }

    fun dumpLog(){
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if(line?.contains("runtime") == true) {
                    // 自定义上传日志
                    Log.i("hello", "test is $line")
                }
            }
        } catch (ignored: IOException) {
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (ignored: IOException) {
                }
            }
        }
    }
}