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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
     * 若提交失败则保持至本地，下次调用本方法时一同提交
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
//                    if (bdLocation.getLocType() == 63) {
//                        return;
//                    }
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

            switch (locationBean.loc_method) {
                case 61:
                    locationBean.loc_method = 1;
                    break;
                case 66:
                case 161:
                    locationBean.loc_method = 3;
                    break;
            }
            List<JQLocationBean> jqLocationBeans = new ArrayList<>();
            try {
                JQLocationDao jqLocationDao = new JQLocationDao(context);
                jqLocationDao.save(locationBean);
                jqLocationBeans = jqLocationDao.queryAllByAddTime();
            } catch (SQLException e) {
                e.printStackTrace();
            }
//            String locs = DaemonUtils.read(context, DaemonUtils.LocalPosition);
//            Log.i("xx", locs + "\n");
//            try {
//                jqLocationBeans = new Gson().fromJson(locs, new TypeToken<List<JQLocationBean>>() {
//                }.getType());
//            } catch (JsonSyntaxException e) {
//                e.printStackTrace();
//                DaemonUtils.save(context, DaemonUtils.LocalPosition, "");
//            }
            if (jqLocationBeans == null) jqLocationBeans = new ArrayList<>();
//            jqLocationBeans.add(locationBean);
//            DaemonUtils.save(context, DaemonUtils.LocalPosition, new Gson().toJson(jqLocationBeans));
//                    if (jqLocationBeans == null) return;

//                    String json = JQLocationBean.getUpLoadJson(Collections.singletonList(locationBean));
            Log.i("xx", jqLocationBeans.size() + "个位置信息" /*+ bdLocation.getTime()*/);
            final String json = JQLocationBean.getUpLoadJson(jqLocationBeans);
//                    httpClient.get

            final String encodeJson = switchEncode(json);
            final List<JQLocationBean> finalJqLocationBeans = jqLocationBeans;

            OutputStream outputStream = null;
            try {
                URL realUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept-Charset", "utf-8");
                connection.setRequestProperty("Content-Type", "application/json");

                outputStream = connection.getOutputStream();
                outputStream.write(encodeJson.getBytes());
                outputStream.flush();

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), "utf-8"));
                String readLine;
                StringBuilder sb = new StringBuilder();
                while ((readLine = in.readLine()) != null) {
                    sb.append(readLine);
                }
                String result = sb.toString();
                System.out.println(result);
                JQLocationDao jqLocationDao = new JQLocationDao(context);
                jqLocationDao.delete(finalJqLocationBeans);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
