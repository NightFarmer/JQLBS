package com.jqyd.jqlbs.db;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by zhangfan on 2015/11/4.
 */
public class CashFlow implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;    // 流水号ID  
    @DatabaseField(generatedId = true, columnName = "cash_flow_id")
    private long cashFlowId;    // 类型：0收，1支  
    @DatabaseField(canBeNull = false, columnName = "type")
    private int type;    // 资金类型 - 0 贷款，1借款，2货款，3其他  
    @DatabaseField(canBeNull = false, columnName = "cash_type")
    private int cashType;    // 资金类型为其他是的 描述  
    @DatabaseField(columnName = "other_type_desc")
    private String otherTypeDesc;    // 是否参与核销：0否，1是  
    @DatabaseField(canBeNull = false, columnName = "write_off_flag")
    private int writeOffFlag;    // 关联用户ID  
    // private long referUserId;  
    @DatabaseField(canBeNull = false, columnName = "amount")
    private double amount;    // 登记日期  
    @DatabaseField(canBeNull = false, columnName = "reg_date")
    private long regDate;    // 创建时间  
    @DatabaseField(canBeNull = false, columnName = "create_date")
    private long createDate;    // 修改时间  
    @DatabaseField(canBeNull = false, columnName = "update_date")
    private long updateDate;    // 关联账单ID  

    public long getCashFlowId() {
        return cashFlowId;
    }

    public void setCashFlowId(long cashFlowId) {
        this.cashFlowId = cashFlowId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCashType() {
        return cashType;
    }

    public void setCashType(int cashType) {
        this.cashType = cashType;
    }

    public String getOtherTypeDesc() {
        return otherTypeDesc;
    }

    public void setOtherTypeDesc(String otherTypeDesc) {
        this.otherTypeDesc = otherTypeDesc;
    }

    public int getWriteOffFlag() {
        return writeOffFlag;
    }

    public void setWriteOffFlag(int writeOffFlag) {
        this.writeOffFlag = writeOffFlag;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getRegDate() {
        return regDate;
    }

    public void setRegDate(long regDate) {
        this.regDate = regDate;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }
}