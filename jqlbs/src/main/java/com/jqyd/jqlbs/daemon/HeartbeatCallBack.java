package com.jqyd.jqlbs.daemon;

import android.content.Context;

/**
 * 回调类, 每分钟回调一次，此时间间隔在部分机型不可靠，不建议作为计时单位，此类必须有一个无参数的构造方法<br/>
 * Created by zhangfan on 2015/10/31.
 */
public abstract class HeartbeatCallBack implements Runnable {
    public final long intervalTime;

    public HeartbeatCallBack() {
        intervalTime = getIntervalTime();
    }

    /**
     * @return 时间间隔，单位毫秒
     */
    public long getIntervalTime() {
        return 60 * 1000;
    }

    HeartbeatService context;

    public Context getContext() {
        return context;
    }

}
