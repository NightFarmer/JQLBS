package com.jqyd.jqlbs.daemon;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Constructor;


public class HeartbeatService extends Service {
    Handler handler = new Handler();

//    Thread thread;
    ThreadLocal<Thread> threadLocal = new ThreadLocal<>();
    HeartbeatCallBack heartbeatCallBack;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread thread = threadLocal.get();
        if (thread == null/* || DaemonUtils.isHeartbeatStop(this)*/) {
            if (thread==null){
                Log.i("xx", "原因thread==null");
            }else if (!thread.isAlive()){
                Log.i("xx", "原因!thread.isAlive()");
            }else if (DaemonUtils.isHeartbeatStop(this)){
                Log.i("xx", "原因DaemonUtils.isHeartbeatStop(this)");
            }
            try {
                SharedPreferences sharedPreferences = getSharedPreferences(DaemonUtils.TAG, MODE_PRIVATE);
                String strCls = sharedPreferences.getString(DaemonUtils.CALLBACK, null);
                Class<? extends HeartbeatCallBack> aClass = (Class<? extends HeartbeatCallBack>) Class.forName(strCls);
                Constructor<? extends HeartbeatCallBack> constructor = aClass.getConstructor();
                heartbeatCallBack = constructor.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            DaemonUtils.heartbeat(this);
            Log.i("xx", "创建新心跳线程");
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        Log.i("xx", "心跳线程" + Thread.currentThread());
                        DaemonUtils.heartbeat(HeartbeatService.this);
                        Log.i("xx", "心跳成功");
                        if (heartbeatCallBack != null) {
                            heartbeatCallBack.context = HeartbeatService.this;
                            handler.post(heartbeatCallBack);
                        }
                        Log.i("xx", "回调成功");
                        long l = System.currentTimeMillis();
                        try {
                            Thread.sleep(DaemonUtils.Interval);
                        } catch (InterruptedException ignored) {
                        }
                        Log.i("xx", "睡醒 上次时间"+l/1000);
                    }
                }
            });
            thread.start();
            threadLocal.set(thread);
        }else if (!thread.isAlive()){
            thread.start();
        }
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
