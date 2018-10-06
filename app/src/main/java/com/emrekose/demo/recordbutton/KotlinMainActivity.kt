package com.emrekose.demo.recordbutton

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.emrekose.kotlin.recordbutton.OnRecordListener
import com.emrekose.kotlin.recordbutton.RecordButton

class KotlinMainActivity: AppCompatActivity() {

    companion object {
        val TAG: String = "Kotlin TAG -> "
    }

    val recordButton: RecordButton by lazy{
        findViewById<RecordButton>(R.id.kotlinRecordBtn)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_main)

        recordButton.recordListener = object : OnRecordListener {
            override fun onRecord() {
                Log.e(TAG, "onRecord: ")
            }

            override fun onRecordCancel() {
                Log.e(TAG, "onRecordCancel: ")
            }

            override fun onRecordFinish() {
                Log.e(TAG, "onRecordFinish: ")
            }
        }

    }

}