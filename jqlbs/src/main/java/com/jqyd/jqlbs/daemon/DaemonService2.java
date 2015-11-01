package com.jqyd.jqlbs.daemon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.jqyd.jqlbs.StrongService;

/**
 * 守护进程1
 * Created by zhangfan on 2015/10/30.
 */
public class DaemonService2 extends Service {

    private String TAG = getClass().getName();

    /**
     * 启动Service2
     */
    private StrongService startS1 = new StrongService.Stub() {
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
        Toast.makeText(getBaseContext(), "Service2 onTrimMemory..." + level, Toast.LENGTH_SHORT)
                .show();
        keepServiceMain();
        keepService1();//保持Service2一直运行
    }

    @Override
    public void onCreate() {
        Toast.makeText(DaemonService2.this, "Service2 onCreate...", Toast.LENGTH_SHORT)
                .show();
        keepServiceMain();
        keepService1();
    }

    /**
     * 判断Service1是否还在运行，如果不是则启动Service1
     */
    private void keepService1() {
//        if (DaemonUtils.isHeartbeatStop1(DaemonService2.this)) {
        Intent ii = new Intent(DaemonService2.this, DaemonService1.class);
        startService(ii);
//        }
        String Process_Name = getPackageName() + ":daemonService1";
        boolean isRun = DaemonUtils.isProessRunning(DaemonService2.this, Process_Name);
        if (!isRun) {
            try {
                startS1.startService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断HeartbeatService是否还在运行，如果不是则启动HeartbeatService
     */
    private void keepServiceMain() {
        if (DaemonUtils.isHeartbeatStop(DaemonService2.this)) {
            Intent i = new Intent(DaemonService2.this, HeartbeatService.class);
            startService(i);
        }
//        String Process_Name = getPackageName() + ":heartbeatService";
//        boolean isRun = DaemonUtils.isProessRunning(DaemonService2.this, Process_Name);
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
        if (thread == null || !thread.isAlive() || DaemonUtils.isHeartbeatStop2(this)) {
            DaemonUtils.heartbeat2(this);
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        DaemonUtils.heartbeat2(DaemonService2.this);
                        keepServiceMain();
                        keepService1();
                        try {
                            Thread.sleep(DaemonUtils.Interval2);
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
        return (IBinder) startS1;
    }
}
