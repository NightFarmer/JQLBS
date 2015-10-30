package com.jqyd.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.jqyd.jqlbs.JQLBSClient;
import com.jqyd.jqlbs.LocationUpLoadCallBack;
import com.jqyd.jqlbs.daemon.DaemonService1;
import com.jqyd.jqlbs.daemon.DaemonService2;

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
                Intent i1 = new Intent(MainActivity.this, DaemonService1.class);
                startService(i1);

                Intent i2 = new Intent(MainActivity.this, DaemonService2.class);
                startService(i2);
                break;

            case R.id.button2:
                //关闭Activity
                this.finish();
                break;
        }
    }
}
