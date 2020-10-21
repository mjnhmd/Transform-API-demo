package com.gradletest

import android.os.Bundle
import android.system.Os.access
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("log_manual", "onCreate")
        HunterLoggerHandler.installLogImpl(object : HunterLoggerHandler() {
            override fun log(tag: String, msg: String) {
                //you can use your custom logger here "
                Log.i(tag, "[you can use your custom logger here \"]$msg")
            }
        })
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testInit()
        testCall("onCreate")
        val bool = true
        val byte_v: Byte = 1
        val char_v = 2.toChar()
        val short_v: Short = 3
        val int_v = 4
        val long_v: Long = 5
        val float_v = 6f
        val double_v = 7.0
        val string_v = "string"
        val int_arr = intArrayOf(1, 2, 3)
        method_test_parameter(
            bool,
            byte_v,
            char_v,
            short_v,
            int_v,
            long_v,
            float_v,
            double_v,
            string_v,
            int_arr,
            savedInstanceState
        )
        method_empty_parameter_empty_return()
        method_return_array()
        method_return_boolean()
        method_return_byte()
        method_return_char()
        method_return_double()
        method_return_float()
        method_return_int()
        method_return_long()
        method_return_short()
        try {
            method_throw_exception()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            testVoidMethodWithException()
        } catch (error: Exception) {
        }
    }
    private val TAG = "MainActivity"
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

    private val paramNames: List<String> =
        ArrayList()

    private fun method_test_parameter(
        bool_v: Boolean,
        byte_v: Byte,
        char_v: Char,
        short_v: Short,
        int_v: Int,
        long_v: Long,
        float_v: Float,
        double_v: Double,
        string_v: String,
        arr: IntArray,
        savedInstanceState: Bundle?
    ): Int {
        val insideLocal = 5
        val insideLocal2 = 6
        Log.i(TAG, "insideLocal $insideLocal")
        return insideLocal + insideLocal2
    }

    private fun method_empty_parameter_empty_return() {
    }

    private fun method_return_boolean(): Boolean {
        return true
    }

    private fun method_return_char(): Char {
        return 'c'
    }

    private fun method_return_byte(): Byte {
        return 0x01
    }

    private fun method_return_short(): Short {
        return 2
    }

    private fun method_return_int(): Int {
        return 2
    }

    private fun method_return_long(): Long {
        return 2L
    }

    private fun method_return_double(): Double {
        return 2.0
    }

    private fun method_return_float(): Float {
        return 2.0f
    }

    private fun method_return_array(): IntArray? {
        return intArrayOf(1, 2, 3)
    }

    private fun method_static(str: String): Any? {
        return "object string$str"
    }

    @Throws(java.lang.Exception::class)
    private fun method_throw_exception(): Int {
        val a = 10
        val b = 0
        require(b != 0) { "illagel argu" }
        return a / 0
    }

    private fun testVoidMethodWithException() {
        throw RuntimeException("not impl")
    }
}
