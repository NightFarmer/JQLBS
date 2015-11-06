package com.jqyd.jqlbs;

import com.jqyd.jqlbs.bean.JQLocationBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

/**
 * 网络通讯相关工具
 * Created by zhangfan on 2015/11/6.
 */
public class NetUtils {
    final static String url = "http://www.jqgj.com.cn:9090/jqgj_server_client/login!lxsb_new.action";

    public static String switchEncode(String param) {
        byte[] temp = param.getBytes();
        String result = "";
        for (byte aTemp : temp) {
            result += aTemp + ";";
        }
        return result;
    }

    public static boolean upload(List<JQLocationBean> jqLocationBeans) {
        final String json = JQLocationBean.getUpLoadJson(jqLocationBeans);

        final String encodeJson = switchEncode(json);

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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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

    private static final String[] serverTimeSet = {
            "http://open.baidu.com/",
            "http://cdn.code.baidu.com/",
            "http://www.qcloud.com/",
            "http://pan.baidu.com/",
            "http://developer.baidu.com/"
    };

    public static long getServerTime() {
        long serverTime = 0;
        for (String net : serverTimeSet) {
            if (serverTime != 0) break;
            try {
                URL url = new URL(net);
                URLConnection uc = url.openConnection();
                uc.connect();
                serverTime = uc.getDate(); // 取得网络日期时间
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return serverTime;
    }
}
