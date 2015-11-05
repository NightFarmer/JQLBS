package com.jqyd.jqlbs.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.jqyd.jqlbs.bean.JQLocationBean;

import java.sql.SQLException;

/**
 * Created by zhangfan on 2015/11/4.
 */
public class JQLocationDao extends BaseDao<JQLocationBean, Integer> {

    public JQLocationDao(Context context) {
        super(context);
    }

    @Override
    public Dao<JQLocationBean, Integer> getDao() throws SQLException {
        return getHelper().getDao(JQLocationBean.class);
    }
}
