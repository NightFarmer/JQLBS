package com.jqyd.jqlbs.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Created by zhangfan on 2015/11/4.
 */
public class CashFlowDao extends BaseDao<CashFlow, Integer> {
    public CashFlowDao(Context context) {
        super(context);
    }

    @Override
    public Dao<CashFlow, Integer> getDao() throws SQLException {
        return getHelper().getDao(CashFlow.class);
    }



}
