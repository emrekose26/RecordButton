package com.emrekose.demo.recordbutton;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.emrekose.recordbutton.OnRecordListener;
import com.emrekose.recordbutton.RecordButton;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    RecordButton recordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordButton = findViewById(R.id.recordBtn);

        recordButton.setRecordListener(new OnRecordListener() {
            @Override
            public void onRecord() {
                Log.e(TAG, "onRecord: ");
            }

            @Override
            public void onRecordCancel() {
                Log.e(TAG, "onRecordCancel: ");
            }

            @Override
            public void onRecordFinish() {
                Log.e(TAG, "onRecordFinish: ");
            }
        });
    }
}
