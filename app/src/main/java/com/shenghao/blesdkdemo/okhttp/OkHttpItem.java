package com.shenghao.blesdkdemo.okhttp;

import java.io.File;

public class OkHttpItem {
    private String key;
    private String value;
    private File file;

    public OkHttpItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public OkHttpItem(String key, File file) {
        this.key = key;
        this.file = file;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public File getFile() {
        return file;
    }
}
