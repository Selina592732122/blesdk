package com.shenghao.blesdkdemo.bean;

public class QRCode {
    private String terminalNo;
    private String timestamp;
    private String inviterPhone;

    public String getInviterPhone() {
        return inviterPhone;
    }

    public void setInviterPhone(String inviterPhone) {
        this.inviterPhone = inviterPhone;
    }

    public String getTerminalNo() {
        return terminalNo;
    }

    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
