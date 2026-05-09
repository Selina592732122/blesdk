package com.shenghao.bean;

/**
 * 通知bean
 */
public class NoticeData {
    private int id;
    private String phone;
    private String terminalNo;
    private String title;
    private String content;
    private int readStatus; //0-未读；1-已读
    private String time;
    private String gcj02;
    private long noticeTimestamp;
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGcj02() {
        return gcj02;
    }

    public void setGcj02(String gcj02) {
        this.gcj02 = gcj02;
    }

    public long getNoticeTimestamp() {
        return noticeTimestamp;
    }

    public void setNoticeTimestamp(long noticeTimestamp) {
        this.noticeTimestamp = noticeTimestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * 是否已读
     */
    public boolean hasRead() {
        return readStatus != 0;
    }
}
