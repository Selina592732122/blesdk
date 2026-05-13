package com.shenghao.blesdkdemo.event;

public class ThemeChangeEvent {
    private String imagePath;

    public ThemeChangeEvent(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
