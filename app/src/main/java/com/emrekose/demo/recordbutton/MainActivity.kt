package com.emrekose.demo.recordbutton

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View

import com.emrekose.recordbutton.OnRecordListener
import com.emrekose.recordbutton.RecordButton

class MainActivity : AppCompatActivity() {

    private lateinit var recordButton: RecordButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recordButton = findViewById<View>(R.id.recordBtn) as RecordButton

        recordButton.setRecordListener(object : OnRecordListener {
            override fun onRecord() {
                Log.e(TAG, "onRecord: ")
            }

            override fun onRecordCancel() {
                Log.e(TAG, "onRecordCancel: ")
            }

            override fun onRecordFinish() {
                Log.e(TAG, "onRecordFinish: ")
            }
        })
    }

    companion object {

        private val TAG = MainActivity::class.java.simpleName
    }
}
