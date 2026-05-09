package com.shenghao.okhttp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OkHttpRequestParams {
    private List<OkHttpItem> headerItemList;
    private List<OkHttpItem> bodyItemList;

    public void addHeader(String key, String value) {
        if (headerItemList == null) {
            headerItemList = new ArrayList<>();
        }
        headerItemList.add(new OkHttpItem(key, value));
    }

    public void addBody(String key, String value) {
        if (bodyItemList == null) {
            bodyItemList = new ArrayList<>();
        }
        bodyItemList.add(new OkHttpItem(key, value));
    }

    public void addBody(String key, Boolean value) {
        if (bodyItemList == null) {
            bodyItemList = new ArrayList<>();
        }
        bodyItemList.add(new OkHttpItem(key, String.valueOf(value)));
    }

    public void addBody(String key, File file) {
        if (bodyItemList == null) {
            bodyItemList = new ArrayList<>();
        }
        bodyItemList.add(new OkHttpItem(key, file));
    }

    public List<OkHttpItem> getHeaderItemList() {
        return headerItemList;
    }

    public List<OkHttpItem> getBodyItemList() {
        return bodyItemList;
    }
}
