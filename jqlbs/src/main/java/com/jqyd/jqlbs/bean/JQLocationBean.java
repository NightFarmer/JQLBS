package com.jqyd.jqlbs.bean;

import com.google.gson.Gson;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangfan on 2015/11/2.
 */
@DatabaseTable
public class JQLocationBean extends LocationBaseBean implements Serializable {
//    @DatabaseField
//    public String regsim = "15936216767";//手机号
//    @DatabaseField
//    public String cosim = "18603718778";//管理员手机号
//    @DatabaseField
//    public String zdmc = "张帆";//姓名
//    @DatabaseField
//    public String gguid = "7BF23FB9-6412-DEC1-B591-E1AD971F47F5";//部门id
//    @DatabaseField
//    public String guid = "82BA9185-1739-E892-2726-23CFF7E6139B";//人员id

    @DatabaseField
    public String regsim = "13253539385";//手机号
    @DatabaseField
    public String cosim = "18603718778";//管理员手机号
    @DatabaseField
    public String zdmc = "白玉格";//姓名
    @DatabaseField
    public String gguid = "7BF23FB9-6412-DEC1-B591-E1AD971F47F5";//部门id
    @DatabaseField
    public String guid = "3AEF542D-DC5D-CB54-E8F5-02AB22D764";//人员id

    @DatabaseField
    public int loc_method = 3;//位置上报的方式；0小区；1卫星；3百度；-1百度失败

    @DatabaseField
    public String yys = "0";//运营商1、联通2、电信3、移动
    @DatabaseField
    public String country_code = " ";//移动国家码
    @DatabaseField
    public String ncode = " ";//移动网络码
    //    loc.setCcode(imsi.substring(0, 3));// 移动国家码
//    loc.setNcode(imsi.substring(3, 5));// 移动网络码
    @DatabaseField
    public String lac_code = " ";//位置区码
    @DatabaseField
    public String cell_id = " ";//小区编号
    @DatabaseField
    public String radius = "-1";//误差半径

    @DatabaseField
    public String signal_strength = "0";

    public static String getUpLoadJson(List<JQLocationBean> recordList) {
        UpLoadBean upLoadBean = new UpLoadBean();
        upLoadBean.recordList = recordList;
        return new Gson().toJson(upLoadBean);
    }

    public static class UpLoadBean {
        List<JQLocationBean> recordList;
    }
}
