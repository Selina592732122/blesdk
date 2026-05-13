package com.shenghao.blesdkdemo.event;

public class BindTerminalEvent {
    private String terminalNo;

    public BindTerminalEvent(String terminalNo) {
        this.terminalNo = terminalNo;
    }

    public String getTerminalNo() {
        return terminalNo;
    }

    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }
}
