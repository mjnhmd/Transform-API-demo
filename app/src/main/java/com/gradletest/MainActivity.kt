package com.gradletest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("log_manual", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testInit()
        testCall("onCreate")
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun testInit(){
        test()
    }

    fun testCall(a: String){
    }

    fun test(){
        testCall("test")
    }
}
