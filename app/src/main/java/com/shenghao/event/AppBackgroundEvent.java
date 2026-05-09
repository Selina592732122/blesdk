package com.shenghao.event;

public class AppBackgroundEvent {
    private boolean isBackground;   //是否处于后台

    public AppBackgroundEvent(boolean isBackground) {
        this.isBackground = isBackground;
    }

    public boolean isBackground() {
        return isBackground;
    }

    public void setBackground(boolean background) {
        isBackground = background;
    }
}
