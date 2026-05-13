package com.shenghao.blesdkdemo.bean;

import java.io.Serializable;

public class SysNotice implements Serializable {
    private long id;
    private String phone;
    private String title;
    private String content;
    private String sysCreated;
    private int status;//状态（0-拒绝 1-同意 2-未操作）

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSysCreated() {
        return sysCreated;
    }

    public void setSysCreated(String sysCreated) {
        this.sysCreated = sysCreated;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
