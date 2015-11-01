package com.jqyd.jqlbs.daemon;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

public class DaemonUtils {

    public static final String TAG = "JQLBS_Daemon";
    public static final String CALLBACK = "callback";

    private static final String HEARTBEAT = "heartbeat";
    static final long Interval = 1000;
    static final long IntervalOverTime = 30000;

    private static final String HEARTBEAT1 = "heartbeat1";
    static final long Interval1 = 5000;
    static final long IntervalOverTime1 = 30000;

    private static final String HEARTBEAT2 = "heartbeat2";
    static final long Interval2 = 5000;
    static final long IntervalOverTime2 = 30000;

    /**
     * @return 判断进程是否运行
     */
    static boolean isProessRunning(Context context, String proessName) {

        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        List<RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        if (lists == null) return false;
        for (RunningAppProcessInfo info : lists) {
            if (info.processName.equals(proessName)) {
                isRunning = true;
            }
        }
        return isRunning;
    }

    static boolean isHeartbeatStop(Context context) {
        long heartbeat = read(context, HEARTBEAT);
        return System.currentTimeMillis() - heartbeat > Interval + IntervalOverTime;
    }

    static void heartbeat(Context context) {
        save(context, HEARTBEAT, System.currentTimeMillis());
    }

    static boolean isHeartbeatStop1(Context context) {
        long heartbeat = read(context, HEARTBEAT1);
        return System.currentTimeMillis() - heartbeat > Interval1 + IntervalOverTime1;
    }

    static void heartbeat1(Context context) {
        save(context, HEARTBEAT1, System.currentTimeMillis());
    }

    static boolean isHeartbeatStop2(Context context) {
        long heartbeat = read(context, HEARTBEAT2);
        return System.currentTimeMillis() - heartbeat > Interval2 + IntervalOverTime2;
    }

    static void heartbeat2(Context context) {
        save(context, HEARTBEAT2, System.currentTimeMillis());
    }

    public static void startHeartbeatService(@NonNull Context context, @NonNull Class<? extends HeartbeatCallBack> callBack) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(CALLBACK, callBack.getName());
        edit.commit();

        heartbeat(context);
        heartbeat1(context);
        heartbeat2(context);

        Intent intent = new Intent(context, HeartbeatService.class);
        context.startService(intent);

        Intent i1 = new Intent(context, DaemonService1.class);
        context.startService(i1);

        Intent i2 = new Intent(context, DaemonService2.class);
        context.startService(i2);
    }

    private static void save(Context context, String filename, long time) {
        try {
            FileOutputStream fileOutputStream = null;
            fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fileOutputStream.write(("" + time).getBytes("utf-8"));
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long read(Context context, String fileName) {
        try {
            FileInputStream fileInputStream = context.openFileInput(fileName);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byte[] date = byteArrayOutputStream.toByteArray();
            String string = new String(date, "utf-8");
            return Long.valueOf(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
