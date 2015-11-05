package com.jqyd.jqlbs.db;

import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangfan on 2015/11/4.
 */
public class Test {

//    private void queCashFlow(int msgType, long lowTime, long highTime) {
//        try {
//            CashFlowDao cashFlowDao = new CashFlowDao(this);
//            Map<String, Object> map = new HashMap<String, Object>();
//            Map<String, Object> lowMap = new HashMap<String, Object>();
//            lowMap.put("create_date", lowTime);
//            Map<String, Object> highMap = new HashMap<String, Object>();
//            highMap.put("create_date", highTime);
//            ArrayList<CashFlow> cashFlows = (ArrayList<CashFlow>)cashFlowDao.query(map, lowMap,
//                    highMap);
//            if (cashFlows != null && cashFlows.size() > 0) {
//                Message message = new Message();
//                message.what = msgType;
//                message.obj = cashFlows;
//                handler.sendMessage(message);
//            } else {
//                handler.sendEmptyMessage(4);
//            }
//        } catch (SQLException e) {
//            handler.sendEmptyMessage(4);
//        }
//    }
}
