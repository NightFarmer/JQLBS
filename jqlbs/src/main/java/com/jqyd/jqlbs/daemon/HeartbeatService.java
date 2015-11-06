package com.jqyd.jqlbs.daemon;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.jqyd.jqlbs.UploadLocalPosition;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HeartbeatService extends Service {
    Handler handler = new Handler();

    Thread thread;
    //    ThreadLocal<Thread> threadLocal = new ThreadLocal<>();
    HeartbeatCallBack heartbeatCallBack;

    UploadLocalPosition uploadLocalPosition;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        acquireWakeLock();
//        Thread thread = threadLocal.get();
        if (thread == null || !thread.isAlive() || DaemonUtils.isHeartbeatStop(this)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH-mm-ss");
            if (thread == null) {
                DaemonUtils.writeSomething(HeartbeatService.this, "xx", simpleDateFormat.format(new Date()) + "原因thread==null");
            } else if (!thread.isAlive()) {
                DaemonUtils.writeSomething(HeartbeatService.this, "xx", simpleDateFormat.format(new Date()) + "原因原因!thread.isAlive()");
            } else if (DaemonUtils.isHeartbeatStop(this)) {
                DaemonUtils.writeSomething(HeartbeatService.this, "xx", simpleDateFormat.format(new Date()) + "原因DaemonUtils.isHeartbeatStop(this)");
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
//            Log.i("xx", "创建新心跳线程");
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
//                        Log.i("xx", "心跳线程" + Thread.currentThread());
                        DaemonUtils.heartbeat(HeartbeatService.this);
//                        Log.i("xx", "心跳成功");
                        if (heartbeatCallBack != null) {
                            boolean needCallback = DaemonUtils.needCallback(HeartbeatService.this, heartbeatCallBack.intervalTime);
                            if (needCallback) {
                                heartbeatCallBack.context = HeartbeatService.this;
                                handler.post(heartbeatCallBack);
                                DaemonUtils.recordCallbackTime(HeartbeatService.this);
                            }
                        }
                        if (uploadLocalPosition == null || !uploadLocalPosition.isAlive()) {
                            uploadLocalPosition = new UploadLocalPosition();
                            uploadLocalPosition.context = HeartbeatService.this;
                            uploadLocalPosition.start();
                        }
//                        Log.i("xx", "回调成功");
//                        long l = System.currentTimeMillis();
                        try {
                            Thread.sleep(DaemonUtils.Interval);
                        } catch (InterruptedException ignored) {
                        }
//                        Log.i("xx", "睡醒 上次时间" + l / 1000);
                    }
                }
            });
            thread.start();
//            threadLocal.set(thread);
        }
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private PowerManager.WakeLock wakeLock = null;

    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, getClass()
                    .getCanonicalName());
            wakeLock.acquire();
        }
    }
}
