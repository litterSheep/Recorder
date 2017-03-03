package com.ly.recorder.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ly.recorder.R;

public class MainActivity extends BaseActivity {

    private Button btn_record, btn_statistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_record = (Button) findViewById(R.id.btn_record);
        btn_statistics = (Button) findViewById(R.id.btn_statistics);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startMyActivity(RecordActivity.class);
            }
        });
        btn_statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMyActivity(StatisticsActivity.class);
            }
        });
    }
}
