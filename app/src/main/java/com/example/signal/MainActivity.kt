package com.example.signal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        val text = this.findViewById<TextView>(R.id.test)
        text.setOnClickListener {
            throwNativeCrash()
        }
    }

    private external fun throwNativeCrash()
}