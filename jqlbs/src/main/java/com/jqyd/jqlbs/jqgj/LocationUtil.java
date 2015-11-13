package com.jqyd.jqlbs.jqgj;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.jqyd.jqlbs.bean.JQLocationBean;

import java.util.HashMap;

/**
 * Created by zhangfan on 2015/11/12.
 */
public class LocationUtil {

    public static boolean getCellLocationData(Context context, JQLocationBean loc) {
        boolean result = false;// 默认失败
        // 1、获取小区位置
        int cellId = -1;
        int lac = 0;
        try {
            takeCellInfos(context, loc);
            cellId = Integer.parseInt(loc.cell_id);
            lac = Integer.parseInt(loc.lac_code);
        } catch (NumberFormatException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

//        shareMethod.recordLog("当前网络基站编号：" + cellId + "客户编号：" + custId+"位置区域码："+lac);
        String locInfo = getCellInfo(context, cellId, lac);
        if (!locInfo.equals("")) {// 小区位置获取成功
            System.out
                    .println("******************通过小区获取位置信息******************");
            String longitude = locInfo.split("#")[1];
            String latitude = locInfo.split("#")[2];
            if (validateLonLat(longitude, latitude)) {
                try {
                    String s = locInfo.split("#")[4];
                    if (loc.lon <= 0 || Double.valueOf(s) < Double.valueOf(loc.radius)) {
                        loc.radius = s;
                        loc.lon = (Double.valueOf(longitude));
                        loc.lat = (Double.valueOf(latitude));
                        loc.loc_method = 0;
                    }
                    result = true;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    result = false;
                }
            }
        }
        return result;
    }

    /**
     * 获取基站信息,如果cellid为-1，说明当前基站信息是未知的，不用查询 param cellId
     */
    private static String getCellInfo(Context context, int cellid, int lac) {
        Optdb_interfce db = new Optdb_interfce(context);
        String locinfo = "";
        if (cellid != -1) {
            try {
                HashMap<String, String> map = db.searchLacs(cellid, lac);

                db.close_SqlDb();

                if (map != null) {// 小区对比成功
                    locinfo = "0#" + map.get("lon") + "#" + map.get("lat")
                            + "#" + "0" + "#" + map.get("radius") + "#"
                            + map.get("loc_type");
                } else {
                    System.out.println("************获取小区位置失败**************");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return locinfo;
    }


    private static void takeCellInfos(Context context, JQLocationBean loc) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        int cid = -1;
        int lac = -1;
        int type = tm.getNetworkType();
        /**
         * 当前使用的网络类型：
         * 例如：NETWORK_TYPE_UNKNOWN  网络类型未知  0
         NETWORK_TYPE_GPRS    GPRS网络  1
         NETWORK_TYPE_EDGE    EDGE网络  2
         NETWORK_TYPE_UMTS    UMTS网络  3
         NETWORK_TYPE_HSDPA    HSDPA网络  8
         NETWORK_TYPE_HSUPA    HSUPA网络  9
         NETWORK_TYPE_HSPA    HSPA网络  10
         NETWORK_TYPE_CDMA    CDMA网络,IS95A 或 IS95B.  4
         NETWORK_TYPE_EVDO_0   EVDO网络, revision 0.  5
         NETWORK_TYPE_EVDO_A   EVDO网络, revision A.  6
         NETWORK_TYPE_1xRTT   1xRTT网络  7
         */
        // 在中国，联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EGDE，电信的2G为CDMA，电信的3G为EVDO
        // G网
        /** Network type is unknown
         NETWORK_TYPE_UNKNOWN = 0;
         Current network is GPRS
         NETWORK_TYPE_GPRS = 1;
         Current network is EDGE
         NETWORK_TYPE_EDGE = 2;
         Current network is UMTS
         NETWORK_TYPE_UMTS = 3;
         Current network is CDMA: Either IS95A or IS95B
         NETWORK_TYPE_CDMA = 4;
         Current network is EVDO revision 0
         NETWORK_TYPE_EVDO_0 = 5;
         Current network is EVDO revision A
         NETWORK_TYPE_EVDO_A = 6;
         Current network is 1xRTT
         NETWORK_TYPE_1xRTT = 7;
         Current network is HSDPA
         NETWORK_TYPE_HSDPA = 8;
         Current network is HSUPA
         NETWORK_TYPE_HSUPA = 9;
         Current network is HSPA
         NETWORK_TYPE_HSPA = 10;
         Current network is iDen
         NETWORK_TYPE_IDEN = 11;综合数字增强网络（简称iDEN），是摩托罗拉发明的一种无线通信技术
         Current network is EVDO revision B
         NETWORK_TYPE_EVDO_B = 12;
         Current network is LTE
         NETWORK_TYPE_LTE = 13;
         Current network is eHRPD
         NETWORK_TYPE_EHRPD = 14;
         Current network is HSPA+
         TelephonyManager.NETWORK_TYPE_HSPAP = 15;
         */

        //	String a=android.os.Build.VERSION.RELEASE;
        //高版本系统兼容低版本软件，但低版本系统无法兼容只适用高版本的软件。

        try {
            if (type == TelephonyManager.NETWORK_TYPE_UMTS
                    || type == TelephonyManager.NETWORK_TYPE_HSDPA
                    || type == TelephonyManager.NETWORK_TYPE_HSPAP//3.1版本以上才有
                    || type == TelephonyManager.NETWORK_TYPE_EDGE
                    || type == TelephonyManager.NETWORK_TYPE_GPRS
                    || type == TelephonyManager.NETWORK_TYPE_HSPA
                    || type == TelephonyManager.NETWORK_TYPE_LTE//3.0版本一上
                    || type == TelephonyManager.NETWORK_TYPE_HSUPA) {
                GsmCellLocation gcl = (GsmCellLocation) tm.getCellLocation();
                try {
                    cid = gcl.getCid();// 小区编号
                    lac = gcl.getLac();// 位置区域码
                } catch (Exception e) {
                    e.printStackTrace();
//                    wf.writeToFile("G网获取小区编号或位置区域码出现错误！");
                }
//                Log.i(TAG, "------------G网cid--------------:" + cid);
                // 信号强度
                // C网
            } else if (type == TelephonyManager.NETWORK_TYPE_CDMA
                    || type == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || type == TelephonyManager.NETWORK_TYPE_EVDO_A
                    || type == TelephonyManager.NETWORK_TYPE_1xRTT
                    || type == TelephonyManager.NETWORK_TYPE_EHRPD //3.0版本一上
                    || type == TelephonyManager.NETWORK_TYPE_EVDO_B //3.0版本一上
                    ) {
                // 参考地址：http://topic.csdn.net/u/20110110/23/c2f6524a-8746-4c1b-914e-57c7c9cced02.html
                CdmaCellLocation cdl = (CdmaCellLocation) tm.getCellLocation();
                try {
                    cid = cdl.getBaseStationId();
                    lac = cdl.getNetworkId();
                } catch (Exception e) {
                    e.printStackTrace();
//                    wf.writeToFile("C网获取小区编号或位置区域码出现错误！");
                }
                // 信号强度
//                Log.i(TAG, "------------------C网cid-------------:" + cid);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            wf.writeToFile("------------读取小区编号时出现异常-------------");
        }
//        Log.i(TAG, "本机网络类型：" + type);
//        wf.writeToFile("小区编号：" + cid+"位置区域码:"+lac);
        if (cid == lac) {
            cid = -1;
        }
        loc.cell_id = cid + "";
        loc.lac_code = lac + "";
//        loc.setCell_id(cid + "");// 小区编号
//        loc.setLac(lac + "");// 位置区域码
        String imsi = tm.getSubscriberId();
        if (imsi == null || imsi.equals("")) {
            imsi = "000000000000000";
        }
        loc.country_code = imsi.substring(0, 3);
        loc.ncode = imsi.substring(3, 5);
//        loc.setCcode(imsi.substring(0, 3));// 移动国家码
//        loc.setNcode(imsi.substring(3, 5));// 移动网络码
        String operator = tm.getSimOperator();
        if (operator != null && !operator.equals("")) {
            switch (operator) {
                case "46000":
                case "46002":
                    // 中国移动
                    operator = "3";
                    break;
                case "46001":
                    // 中国联通
                    operator = "1";
                    break;
                case "46003":
                    // 中国电信
                    operator = "2";
                    break;
            }
        } else {
            operator = "-1";
        }
        loc.yys = operator;
//        loc.setYys(operator);
    }

    /**
     * Description: 验证经纬度是否合法 Title: validateLonLat
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @return boolean true数据没问题false数据为空
     */
    private static boolean validateLonLat(String longitude, String latitude) {
        try {
            return Double.valueOf(longitude) > 0 && Double.valueOf(latitude) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
