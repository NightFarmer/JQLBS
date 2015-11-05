package com.jqyd.jqlbs.daemon;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class DaemonUtils {

    public static final String TAG = "JQLBS_Daemon";
    public static final String CALLBACK = "callback";
    public static final String LocalPosition = "LocalPosition";

    private static final String HEARTBEAT = "DaemonHeartbeat";
    static final long Interval = 10000;
    static final long IntervalOverTime = 30000;

    private static final String HEARTBEAT1 = "DaemonHeartbeat1";
    static final long Interval1 = 5000;
    static final long IntervalOverTime1 = 30000;

    private static final String HEARTBEAT2 = "DaemonHeartbeat2";
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
        long heartbeat = readLong(context, HEARTBEAT);
        return System.currentTimeMillis() - heartbeat > Interval + IntervalOverTime;
    }

    static void heartbeat(Context context) {
        saveLong(context, HEARTBEAT, System.currentTimeMillis());
    }

    static boolean isHeartbeatStop1(Context context) {
        long heartbeat = readLong(context, HEARTBEAT1);
        return System.currentTimeMillis() - heartbeat > Interval1 + IntervalOverTime1;
    }

    static void heartbeat1(Context context) {
        saveLong(context, HEARTBEAT1, System.currentTimeMillis());
    }

    static boolean isHeartbeatStop2(Context context) {
        long heartbeat = readLong(context, HEARTBEAT2);
        return System.currentTimeMillis() - heartbeat > Interval2 + IntervalOverTime2;
    }

    static void heartbeat2(Context context) {
        saveLong(context, HEARTBEAT2, System.currentTimeMillis());
    }

    static boolean needCallback(Context context, long userInterval) {
        long pre_callback = readLong(context, CALLBACK);
        return System.currentTimeMillis() - pre_callback >= userInterval;
    }

    static void recordCallbackTime(Context context) {
        saveLong(context, CALLBACK, System.currentTimeMillis());
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

    public static void save(Context context, String filename, String content) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fileOutputStream.write(content.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) try {
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveLong(Context context, String filename, long time) {
        save(context, filename, ""+time);
    }

    private static long readLong(Context context, String fileName){
        String read = read(context, fileName);
        long aLong = 0;
        try {
            aLong = Long.valueOf(read);
        }catch (Exception ignore){
        }
        return aLong;
    }

    public static String read(Context context, String fileName) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = context.openFileInput(fileName);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byte[] date = byteArrayOutputStream.toByteArray();
            String string = new String(date, "utf-8");
            return string;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) try {
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static void writeSomething(Context context, String filename, String content) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(filename, Context.MODE_APPEND);
            fileOutputStream.write(("" + content).getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) try {
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
