package com.jqyd.jqlbs.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by zhangfan on 2015/11/4.
 */
@DatabaseTable
public class LocationBaseBean implements Serializable {
    @DatabaseField(id = true)
    public String id = UUID.randomUUID().toString();
    @DatabaseField
    public double lat;//纬度
    @DatabaseField
    public double lon;//经度
    @DatabaseField
    public long time = System.currentTimeMillis();//定位时间
}
