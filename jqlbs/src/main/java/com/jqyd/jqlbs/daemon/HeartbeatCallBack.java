package com.jqyd.jqlbs.daemon;

import android.content.Context;

/**
 * 每10秒回调一次, 此时间间隔是不可靠的，不能作为计时的依据, 此类必须有一个无参数的构造方法<br/>
 * Created by zhangfan on 2015/10/31.
 */
public abstract class HeartbeatCallBack implements Runnable {

    HeartbeatService context;

    public Context getContext() {
        return context;
    }

}
