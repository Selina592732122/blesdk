package com.shenghao.bean;

/**
 * 报警弹窗bean
 */
public class NoticeDialogBean {
    private int id;
    private String title;
    private String content;
    private int readStatus;
    private String noticeTimestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getNoticeTimestamp() {
        return noticeTimestamp;
    }

    public void setNoticeTimestamp(String noticeTimestamp) {
        this.noticeTimestamp = noticeTimestamp;
    }
}
