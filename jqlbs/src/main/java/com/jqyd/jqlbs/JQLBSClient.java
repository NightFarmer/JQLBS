package com.jqyd.jqlbs;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.jqyd.jqlbs.bean.JQLocationBean;
import com.jqyd.jqlbs.bean.LocationBaseBean;
import com.jqyd.jqlbs.db.JQLocationDao;
import com.jqyd.jqlbs.jqgj.LocationUtil;

import java.sql.SQLException;
import java.util.Collections;

/**
 * 定位发起类<br/>
 * Created by zhangfan on 2015/10/28.
 */
public class JQLBSClient {
    static LocationClient mLocationClient;
//    static long preUploadTime;

//    final static String url = "http://www.jqgj.com.cn:9090/jqgj_server_client/login!lxsb_new.action";
//                    String url = "http://192.168.1.80:8080/jqgj_2.0_serv/login!lxsb_new.action";


    private static void location(final Context context, final LBSParam lbsParam, final LocationUpLoadCallBack callBack, final BDLocationListener locationListener, boolean needGPS) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new RuntimeException("不能在子线程中调用该方法");
        }
        if (locationListener == null && !needGPS) {
            JQLocationBean loc = new JQLocationBean();
            boolean success = LocationUtil.getCellLocationData(context, loc);
            if (success) {
                if (lbsParam != null) {
                    new Thread(new UpLoadLocRunnable2(loc, context, lbsParam)).start();
                }
                if (callBack != null) {
                    callBack.onResult(0);
                }
                return;
            }
        }

        if (mLocationClient == null) {
            mLocationClient = new LocationClient(context.getApplicationContext());
            BDLocationListener bdLocationListener = new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    mLocationClient.stop();
                    if (lbsParam != null) {
                        new Thread(new UpLoadLocRunnable(bdLocation, context, lbsParam)).start();
                    }
                    if (callBack != null) {
                        callBack.onResult(bdLocation.getLocType());
                    }
                    if (locationListener != null) {
                        locationListener.onReceiveLocation(bdLocation);
                    }
                }
            };
            mLocationClient.registerLocationListener(bdLocationListener);
            LocationClientOption locOption = new LocationClientOption();
            locOption.disableCache(true);
            locOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//高精度
            mLocationClient.setLocOption(locOption);
        }
        LocationClientOption locOption = mLocationClient.getLocOption();
        if (needGPS) {
            locOption.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
            new Thread(new HandlerGPSLocRunnable(context, lbsParam)).start();
        } else {
            locOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        }
        mLocationClient.setLocOption(locOption);
        mLocationClient.start();
    }


    public static void requestLocation(Context context, @NonNull BDLocationListener bdLocationListener) {
        location(context, null, null, bdLocationListener);
    }

    private static void location(Context context, LBSParam lbsParam, LocationUpLoadCallBack callBack, BDLocationListener locationListener) {
        location(context, lbsParam, callBack, locationListener, false);
    }


    /**
     * 请求久其定位接口，定位成功与否都直接提交久其服务器
     * 若提交失败则保持至本地，下次联网时一同提交
     *
     * @param callBack 百度定位成功回调
     */
    public static void upLoadLocation(final Context context, final LBSParam lbsParam, final LocationUpLoadCallBack callBack) {
        location(context, lbsParam, callBack, null);
    }

    /**
     * 请求久其定位接口，定位成功与否都直接提交久其服务器
     * 若提交失败则保持至本地，下次联网时一同提交
     */
    public static void upLoadLocation(Context context, @NonNull LBSParam lbsParam) {
        upLoadLocation(context, lbsParam, null);
    }

    private static class HandlerGPSLocRunnable implements Runnable {
        LBSParam lbsParam;
        Context context;

        public HandlerGPSLocRunnable(Context context, LBSParam lbsParam) {
            this.lbsParam = lbsParam;
            this.context = context;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(50 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mLocationClient.stop();
            JQLocationBean locationBean = new JQLocationBean();
//            locationBean.loc_method = BDLocation.TypeNetWorkException;
            locationBean.loc_method = -998;
            locationBean.regsim = lbsParam.regsim;
            locationBean.cosim = lbsParam.cosim;
            locationBean.gguid = lbsParam.gguid;
            locationBean.guid = lbsParam.guid;
            locationBean.zdmc = lbsParam.zdmc;
            locationBean.time = locationBean.time - 50 * 1000;
            uploadOneLocation(context, locationBean);
        }
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
            Log.i("xx", bdLocation.getLocType() + "" + this + "-" + bdLocation.getRadius());
            JQLocationBean locationBean = new JQLocationBean();
            locationBean.lat = bdLocation.getLatitude();
            locationBean.lon = bdLocation.getLongitude();
            locationBean.radius = bdLocation.getRadius() + "";
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
//            if (locationBean.time - preUploadTime < 50 * 1000) {
//                return;
//            }
//            preUploadTime = locationBean.time;

            switch (locationBean.loc_method) {
                case BDLocation.TypeGpsLocation://61
                    locationBean.loc_method = 2;
                    break;
                case BDLocation.TypeOffLineLocation://66
                    locationBean.loc_method = 0;
                    if (bdLocation.getRadius() < 1500) {
                        break;
                    }
//                  locationBean.loc_method = BDLocation.TypeOffLineLocationFail;//67
                case BDLocation.TypeOffLineLocationFail:
                case BDLocation.TypeOffLineLocationNetworkFail:
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            JQLBSClient.location(context, lbsParam, null, null, true);
                        }
                    });
                    return;
                case BDLocation.TypeNetWorkLocation://161
                    locationBean.loc_method = 3;
                    break;
            }
            uploadOneLocation(context, locationBean);

        }

    }

    private static class UpLoadLocRunnable2 implements Runnable {

        Context context;
        JQLocationBean locationBean;
        LBSParam lbsParam;

        public UpLoadLocRunnable2(JQLocationBean jqLocationBean, Context context, LBSParam lbsParam) {
            this.locationBean = jqLocationBean;
            this.context = context;
            this.lbsParam = lbsParam;
        }

        @Override
        public void run() {
//            Log.i("xx", bdLocation.getLocType() + "" + this + "-" + bdLocation.getRadius());
            locationBean.regsim = lbsParam.regsim;
            locationBean.cosim = lbsParam.cosim;
            locationBean.gguid = lbsParam.gguid;
            locationBean.guid = lbsParam.guid;
            locationBean.zdmc = lbsParam.zdmc;
            long serverTime = NetUtils.getServerTime();
            if (serverTime != 0) {
                locationBean.time = serverTime;
            }
            uploadOneLocation(context, locationBean);
        }
    }

    private static void uploadOneLocation(Context context, JQLocationBean locationBean) {
        Log.i("xx", "上报一个位置");
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
