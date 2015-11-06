package com.jqyd.jqlbs;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.jqyd.jqlbs.bean.JQLocationBean;
import com.jqyd.jqlbs.db.JQLocationDao;

import java.sql.SQLException;
import java.util.Collections;

/**
 * 定位发起类<br/>
 * Created by zhangfan on 2015/10/28.
 */
public class JQLBSClient {
    static LocationClient mLocationClient;
    final static String url = "http://www.jqgj.com.cn:9090/jqgj_server_client/login!lxsb_new.action";
//                    String url = "http://192.168.1.80:8080/jqgj_2.0_serv/login!lxsb_new.action";

    /**
     * 请求久其定位接口，定位成功与否都直接提交久其服务器
     * 若提交失败则保持至本地，下次联网时一同提交
     *
     * @param callBack 百度定位成功回调
     */
    public static void upLoadLocation(final Context context, final LBSParam lbsParam, final LocationUpLoadCallBack callBack) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new RuntimeException("不能在子线程中调用该方法");
        }
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(context.getApplicationContext());
            BDLocationListener bdLocationListener = new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    new Thread(new UpLoadLocRunnable(bdLocation, context, lbsParam)).start();
                    if (callBack != null) {
                        callBack.onResult(bdLocation.getLocType());
                    }
                }
            };
            mLocationClient.registerLocationListener(bdLocationListener);
            LocationClientOption locOption = new LocationClientOption();
            locOption.disableCache(true);
            locOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//高精度
            mLocationClient.setLocOption(locOption);
        }

        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        } else {
            int result = mLocationClient.requestLocation();
            Log.i("xx", "result" + result);
        }
    }

    /**
     * 请求久其定位接口，定位成功与否都直接提交久其服务器
     * 若提交失败则保持至本地，下次联网时一同提交
     */
    public static void upLoadLocation(Context context, @NonNull LBSParam lbsParam) {
        upLoadLocation(context, lbsParam, null);
    }


    private static class UpLoadLocRunnable implements Runnable {

        Context context;
        BDLocation bdLocation;
        LBSParam lbsParam;

        public UpLoadLocRunnable(BDLocation bdLocation, Context context, LBSParam lbsParam) {
            this.bdLocation = bdLocation;
            this.context = context;
            this.lbsParam = lbsParam;
        }

        @Override
        public void run() {
            Log.i("xx", bdLocation.getLocType() + "" + this);
            JQLocationBean locationBean = new JQLocationBean();
            locationBean.lat = bdLocation.getLatitude();
            locationBean.lon = bdLocation.getLongitude();
            locationBean.loc_method = bdLocation.getLocType();
            locationBean.regsim = lbsParam.regsim;
            locationBean.cosim = lbsParam.cosim;
            locationBean.gguid = lbsParam.gguid;
            locationBean.guid = lbsParam.guid;
            locationBean.zdmc = lbsParam.zdmc;
            long serverTime = NetUtils.getServerTime();
            if (serverTime != 0) {
                locationBean.time = serverTime;
            }

            switch (locationBean.loc_method) {
                case 61:
                    locationBean.loc_method = 1;
                    break;
                case 66:
                    locationBean.loc_method = 0;
                    break;
                case 161:
                    locationBean.loc_method = 3;
                    break;
            }
            Log.i("xx", "一个位置");
            boolean result = NetUtils.upload(Collections.singletonList(locationBean));
            if (!result) {
                try {
                    JQLocationDao jqLocationDao = new JQLocationDao(context);
                    jqLocationDao.save(locationBean);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
