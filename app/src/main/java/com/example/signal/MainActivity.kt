package com.example.signal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    companion object{
        init {
            System.loadLibrary("test")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // native crash
        val text = this.findViewById<Button>(R.id.test)
        text.setOnClickListener {
            throwNativeCrash()
        }

        // anr
        val text2 = this.findViewById<Button>(R.id.test2)
        text2.setOnClickListener {
            // 这里只是一个死循环，需要触发尽快anr可以直接点击返回健
            createANR(text2)
        }
    }

    private external fun throwNativeCrash()
    private fun createANR(textView: TextView){
        Handler(Looper.getMainLooper()).postDelayed({
            textView.text = "test anr"
        },3000)
        while (true){

        }
    }
}