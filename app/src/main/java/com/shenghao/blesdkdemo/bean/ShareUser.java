package com.shenghao.blesdkdemo.bean;

import java.io.Serializable;

public class ShareUser implements Serializable {
    private String avatar;
    private String remark;
    private String phone;
    private String terminalNo;
    private String sysCreated;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTerminalNo() {
        return terminalNo;
    }

    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }

    public String getSysCreated() {
        return sysCreated;
    }

    public void setSysCreated(String sysCreated) {
        this.sysCreated = sysCreated;
    }
}
