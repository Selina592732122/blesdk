package com.shenghao.bean;

import java.io.Serializable;

public class ThemeBean implements Serializable {
    private int id;
    private String appFlag;
    private String name;
    private String themeImage;
    private int seq;
    private String isCurrentTheme;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppFlag() {
        return appFlag;
    }

    public void setAppFlag(String appFlag) {
        this.appFlag = appFlag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThemeImage() {
        return themeImage;
    }

    public void setThemeImage(String themeImage) {
        this.themeImage = themeImage;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getIsCurrentTheme() {
        return isCurrentTheme;
    }

    public void setIsCurrentTheme(String isCurrentTheme) {
        this.isCurrentTheme = isCurrentTheme;
    }
}
