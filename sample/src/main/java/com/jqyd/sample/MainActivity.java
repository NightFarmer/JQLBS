package com.jqyd.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jqyd.jqlbs.daemon.DaemonUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn1, btn2;
    //AIDL,此处用于bindService
    private String TAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button1:
                new Intent().putExtra("", String.class);
                DaemonUtils.startHeartbeatService(MainActivity.this, SampleCallBack.class);
                break;
            case R.id.button2:
                //关闭Activity
                this.finish();
                break;
        }
    }
}
