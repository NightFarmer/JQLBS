package com.jqyd.jqlbs.jqgj;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.HashMap;

/**
 * 操作Sqllite3数据库的接口程序
 * @author wangliang
 *
 */
public class Optdb_interfce {
	
	//数据库对象
	private static SQLiteDatabase db = null;
	//数据库游标
	private Cursor cursor = null;
	//数据库名
	private static String DATABASE_JQGJ="JQGJ.db";
	//数据变量
	private String group_flag = "";
	
	/**
	 * 数据库表字段信息
	 */
	//基站信息表
	private static final String T_LACINFO = "CREATE TABLE T_LACINFO(ID INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,CID VARCHAR(50),CELL_ID VARCHAR(50)," +
			"LAC VARCHAR(50),CCODE VARCHAR(50),NCODE VARCHAR(50),XHQD VARCHAR(100),RADIUS VARCHAR(500),LOC_TYPE INT";

	/**
	 * 创建或打开数据库
	 */
	public Optdb_interfce(Context context){
//		wf = new WriteFile(context, JqydDateUtil.getDateDayTwo(new Date())+"DateBaseFile");
		
			try {
				this.db = context.openOrCreateDatabase(DATABASE_JQGJ, context.MODE_APPEND, null);
//				wf.writeToFile("当前数据库地址："+this.db!=null?this.db.getPath():"数据库对象db为空");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	
	/**
	 * 判断数据库是否已打开
	 * @return
	 */
	public boolean isOpen(){
		boolean flag = false;
		try {
			if(db != null && db.isOpen()){	
				//如果游标未关闭，则关闭游标
				if(cursor != null && !cursor.isClosed()){
					cursor.close();
				}
				flag = true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

	
	/**
	 * 传入Cell id，查询基站信息表
	 * @return
	 */
	public HashMap<String,String> searchLacs(int cell_id ,int lac){
//		System.out.println("查询基站表--------基站编号："+cell_id+",客户编号："+custId+",LAC"+lac);
//		if(custId == null || custId.equals("-1") || custId.equals("")){
			cursor = db.rawQuery("SELECT * FROM T_LACINFO WHERE CELL_ID = ? AND LAC = ?",new String[]{cell_id+"",lac+""});
//		}else{
//			cursor = db.rawQuery("SELECT * FROM T_LACINFO WHERE CELL_ID = ? AND CID=? AND LAC = ?",new String[]{cell_id+"",custId,lac+""});
//		}
		HashMap<String,String> map = null;
		while(cursor.moveToNext()){	
			map = new HashMap<String,String>();
			map.put("lon", cursor.getString(cursor.getColumnIndex("REMARK1")));
			map.put("lat",cursor.getString(cursor.getColumnIndex("REMARK2")));		
			map.put("radius",cursor.getString(cursor.getColumnIndex("RADIUS")));
			map.put("loc_type", cursor.getString(cursor.getColumnIndex("LOC_TYPE")));			
			System.out.println("基站信息-----------经度："+cursor.getString(cursor.getColumnIndex("REMARK1"))+",纬度："+cursor.getString(cursor.getColumnIndex("REMARK2"))
					+"loc_type:"+cursor.getString(cursor.getColumnIndex("LOC_TYPE")));
		}
		System.out.println("查询基站信息表---------结果："+map);
		return map;
	}
	
	/**
	 * 关闭Sqllite3数据库
	 */
	public void close_SqlDb(){
		System.out.println("************关闭数据库连接************");
		if(cursor != null){
			cursor.close();
		}
		if(db != null){
			db.close();
		}				
	}
}
