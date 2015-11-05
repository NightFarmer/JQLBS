package com.jqyd.jqlbs.daemon;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.jqyd.jqlbs.StrongService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 守护进程2
 * Created by zhangfan on 2015/10/30.
 */
public class DaemonService1 extends Service {

    private String TAG = getClass().getName();

    /**
     * 启动Service2
     */
    private StrongService startS2 = new StrongService.Stub() {
        @Override
        public void stopService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), DaemonService2.class);
            getBaseContext().stopService(i);
        }

        @Override
        public void startService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), DaemonService2.class);
            getBaseContext().startService(i);
        }
    };

//    /**
//     * 启动HeartbeatService
//     */
//    private StrongService startMain = new StrongService.Stub() {
//        @Override
//        public void stopService() throws RemoteException {
//            Intent i = new Intent(getBaseContext(), HeartbeatService.class);
//            getBaseContext().stopService(i);
//        }
//
//        @Override
//        public void startService() throws RemoteException {
//            Intent i = new Intent(getBaseContext(), HeartbeatService.class);
//            getBaseContext().startService(i);
//        }
//    };

    @Override
    public void onTrimMemory(int level) {
        Toast.makeText(getBaseContext(), "Service1 onTrimMemory..." + level, Toast.LENGTH_SHORT)
                .show();
        keepServiceMain();
        keepService2();//保持Service2一直运行
    }

    @Override
    public void onCreate() {
        acquireWakeLock();
        Toast.makeText(DaemonService1.this, "Service1 onCreate...", Toast.LENGTH_SHORT)
                .show();
        keepServiceMain();
        keepService2();
    }

    /**
     * 判断Service2是否还在运行，如果不是则启动Service2
     */
    private void keepService2() {
        if (DaemonUtils.isHeartbeatStop2(DaemonService1.this)) {
            Intent i2 = new Intent(DaemonService1.this, DaemonService2.class);
            startService(i2);
        }
        String Process_Name = getPackageName() + ":daemonService2";
        boolean isRun = DaemonUtils.isProessRunning(DaemonService1.this, Process_Name);
        if (!isRun) {
            try {
                startS2.startService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断HeartbeatService是否还在运行，如果不是则启动HeartbeatService
     */
    private void keepServiceMain() {
        if (DaemonUtils.isHeartbeatStop(DaemonService1.this)) {
            Intent i = new Intent(DaemonService1.this, HeartbeatService.class);
            startService(i);
        }
//        String Process_Name = getPackageName() + ":heartbeatService";
//        boolean isRun = DaemonUtils.isProessRunning(DaemonService1.this, Process_Name);
//        if (!isRun) {
//            try {
//                startMain.startService();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
    }

    Thread thread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (thread == null || !thread.isAlive() || DaemonUtils.isHeartbeatStop1(this)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH-mm-ss");
            if (thread == null) {
                DaemonUtils.writeSomething(DaemonService1.this, "xx1", simpleDateFormat.format(new Date()) + "原因thread==null");
            } else if (!thread.isAlive()) {
                DaemonUtils.writeSomething(DaemonService1.this, "xx1", simpleDateFormat.format(new Date()) + "原因原因!thread.isAlive()");
            } else if (DaemonUtils.isHeartbeatStop(this)) {
                DaemonUtils.writeSomething(DaemonService1.this, "xx1", simpleDateFormat.format(new Date()) + "原因DaemonUtils.isHeartbeatStop(this)");
            }
            DaemonUtils.heartbeat1(this);
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        DaemonUtils.heartbeat1(DaemonService1.this);
                        keepServiceMain();
                        keepService2();
                        try {
                            Thread.sleep(DaemonUtils.Interval1);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            });
            thread.start();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return (IBinder) startS2;
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
