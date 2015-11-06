package com.jqyd.jqlbs;

import android.content.Context;
import android.util.Log;

import com.jqyd.jqlbs.bean.JQLocationBean;
import com.jqyd.jqlbs.db.JQLocationDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangfan on 2015/11/6.
 */
public class UploadLocalPosition extends Thread {

    public Context context;

    @Override
    public void run() {
        upLoad();
    }

    private void upLoad() {
        List<JQLocationBean> jqLocationBeans = new ArrayList<>();
        try {
            JQLocationDao jqLocationDao = new JQLocationDao(context);
            jqLocationBeans = jqLocationDao.queryAllByAddTime();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (jqLocationBeans == null || jqLocationBeans.size() == 0) return;

        Log.i("xx", jqLocationBeans.size() + "个本地位置信息");

        boolean result = Utils.upload(jqLocationBeans);
        if (result) {
            JQLocationDao jqLocationDao = new JQLocationDao(context);
            try {
                jqLocationDao.delete(jqLocationBeans);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
