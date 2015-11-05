package com.jqyd.jqlbs;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jqyd.jqlbs.bean.JQLocationBean;
import com.jqyd.jqlbs.daemon.DaemonUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 定位发起类<br/>
 * Created by zhangfan on 2015/10/28.
 */
public class JQLBSClient {
    static LocationClient mLocationClient;

    /**
     * 请求久其定位接口，定位成功与否都直接提交久其服务器
     * 若提交失败则保持至本地，下次调用本方法时一同提交
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
//                    if (bdLocation.getLocType() == 63) {
//                        return;
//                    }
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

                    switch (locationBean.loc_method) {
                        case 61:
                            locationBean.loc_method = 1;
                            break;
                        case 66:
                        case 161:
                            locationBean.loc_method = 3;
                            break;
                    }
//                    JQLocationDao jqLocationDao = new JQLocationDao(context);
                    List<JQLocationBean> jqLocationBeans = new ArrayList<>();
//                    try {
//                        jqLocationDao.save(locationBean);
//                        jqLocationBeans = jqLocationDao.queryAll();
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
                    String locs = DaemonUtils.read(context, DaemonUtils.LocalPosition);
                    Log.i("xx", locs+"\n");
                    try {
                        jqLocationBeans = new Gson().fromJson(locs, new TypeToken<List<JQLocationBean>>() {
                        }.getType());
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        DaemonUtils.save(context, DaemonUtils.LocalPosition, "");
                    }
                    if (jqLocationBeans == null) jqLocationBeans = new ArrayList<>();
                    jqLocationBeans.add(locationBean);
                    DaemonUtils.save(context, DaemonUtils.LocalPosition, new Gson().toJson(jqLocationBeans));
//                    if (jqLocationBeans == null) return;

//                    String json = JQLocationBean.getUpLoadJson(Collections.singletonList(locationBean));
                    Log.i("xx", jqLocationBeans.size() + "个位置信息" + bdLocation.getTime());
                    String json = JQLocationBean.getUpLoadJson(jqLocationBeans);
                    AsyncHttpClient httpClient = new AsyncHttpClient();
//                    httpClient.get
                    String url = "http://116.255.134.172:9090/jqgj_server_client/login!lxsb_new.action";
//                    String url = "http://192.168.1.80:8080/jqgj_2.0_serv/login!lxsb_new.action";
                    json = switchEncode(json);
//                    final List<JQLocationBean> finalJqLocationBeans = jqLocationBeans;
                    httpClient.post(context, url, new StringEntity(json, "utf-8"), "application/json", new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                            try {
//                            JQLocationDao jqLocationDao = new JQLocationDao(context);
//                                jqLocationDao.delete(finalJqLocationBeans);
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
                            DaemonUtils.save(context, DaemonUtils.LocalPosition, "");
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.i("xx", responseString);
                        }
                    });


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
     * 若提交失败则保持至本地，下次调用本方法时一同提交
     */
    public static void upLoadLocation(Context context, @NonNull LBSParam lbsParam) {
        upLoadLocation(context, lbsParam, null);
    }

    private static String switchEncode(String param) {
        byte[] temp = param.getBytes();
        String result = "";
        for (byte aTemp : temp) {
            result += aTemp + ";";
        }
        return result;
    }
}
