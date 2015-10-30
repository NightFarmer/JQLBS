package com.jqyd.jqlbs.daemon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.jqyd.jqlbs.StrongService;

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

    @Override
    public void onTrimMemory(int level) {
        Toast.makeText(getBaseContext(), "Service1 onTrimMemory..." + level, Toast.LENGTH_SHORT)
                .show();
        keepService2();//保持Service2一直运行
    }

    @Override
    public void onCreate() {
        Toast.makeText(DaemonService1.this, "Service1 onCreate...", Toast.LENGTH_SHORT)
                .show();
        keepService2();
    }

    /**
     * 判断Service2是否还在运行，如果不是则启动Service2
     */
    private void keepService2() {
        String Process_Name = getPackageName() + ":daemonService2";
        boolean isRun = Utils.isProessRunning(DaemonService1.this, Process_Name);
        if (!isRun) {
            try {
                Toast.makeText(getBaseContext(), "重新启动 Service2", Toast.LENGTH_SHORT).show();
                startS2.startService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return (IBinder) startS2;
    }
}
