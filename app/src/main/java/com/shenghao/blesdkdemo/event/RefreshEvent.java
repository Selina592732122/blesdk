package com.shenghao.blesdkdemo.event;

public class RefreshEvent {
    private boolean shouldRefresh;

    public RefreshEvent(boolean shouldRefresh) {
        this.shouldRefresh = shouldRefresh;
    }

    public boolean isShouldRefresh() {
        return shouldRefresh;
    }

    public void setShouldRefresh(boolean shouldRefresh) {
        this.shouldRefresh = shouldRefresh;
    }
}
