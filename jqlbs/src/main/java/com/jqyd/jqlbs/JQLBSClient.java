package com.jqyd.jqlbs;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;

/**
 * Created by zhangfan on 2015/10/28.
 */
public class JQLBSClient {
    static LocationClient mLocationClient;
    static BDLocationListener bdLocationListener;

    public static synchronized void upLoadLocation(Context context, final LocationUpLoadCallBack callBack) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new RuntimeException("不能在子线程中调用该方法");
        }
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(context.getApplicationContext());
            bdLocationListener = new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    Log.i("xx", bdLocation.getLocType() + "" + this);
                    if (callBack!=null){
                        callBack.onResult(bdLocation.getLocType());
                    }
                }
            };
            mLocationClient.registerLocationListener(bdLocationListener);
        }

        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        } else {
            int result = mLocationClient.requestLocation();
            Log.i("xx", result+"");
        }
    }

    public static synchronized void upLoadLocation(Context context) {
        upLoadLocation(context, null);
    }

}
