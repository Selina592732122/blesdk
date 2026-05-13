package com.shenghao.blesdkdemo.event;

public class WXPayEvent {
    private int errCode;
    private String errStr;

    public WXPayEvent(int errCode, String errStr) {
        this.errCode = errCode;
        this.errStr = errStr;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrStr() {
        return errStr;
    }

    public void setErrStr(String errStr) {
        this.errStr = errStr;
    }
}
