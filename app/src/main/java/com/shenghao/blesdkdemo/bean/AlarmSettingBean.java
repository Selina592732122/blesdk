package com.shenghao.blesdkdemo.bean;

public class AlarmSettingBean {
    private String noticeType;
    private int value;  //0-开启；1-关闭
    private String desc;

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isChecked() {
        if (value == 0) {
            return true;
        }
        return false;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "AlarmSettingBean{" +
                "noticeType='" + noticeType + '\'' +
                ", value=" + value +
                ", desc='" + desc + '\'' +
                '}';
    }
}
