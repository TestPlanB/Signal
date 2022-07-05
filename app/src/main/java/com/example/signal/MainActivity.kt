package com.example.signal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            createANR()
        }
    }

    private external fun throwNativeCrash()
    private fun createANR(){
        while (true){

        }
    }
}