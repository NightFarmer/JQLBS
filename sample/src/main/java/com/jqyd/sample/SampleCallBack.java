package com.jqyd.sample;

import android.util.Log;

import com.jqyd.jqlbs.JQLBSClient;
import com.jqyd.jqlbs.LBSParam;
import com.jqyd.jqlbs.LocationUpLoadCallBack;
import com.jqyd.jqlbs.daemon.HeartbeatCallBack;

/**
 * Created by zhangfan on 2015/10/31.
 */
public class SampleCallBack extends HeartbeatCallBack {

    @Override
    public void run() {
        Log.i("xx", "beat:" + System.currentTimeMillis()/1000+"--"+Thread.currentThread()+"--"+getContext());
        LBSParam lbsParam = new LBSParam();

        JQLBSClient.upLoadLocation(getContext(), lbsParam);
    }

}
