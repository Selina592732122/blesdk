package com.shenghao.blesdk.exception;

import com.clj.fastble.exception.BleException;

public class BleSdkException extends BleException {

    public static final int CODE_NULL = 1000;
    public static final int CODE_CONNECTING = 1001;
    public static final int CODE_CONNECT_ERROR = 1002;
    public static final int CODE_WRITE_ERROR = 1003;

    private int code;
    private String description;

    public BleSdkException(int code, String description) {
        super(code,description);
        this.code = code;
        this.description = description;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "BleSdkException{" +
                "code=" + code +
                ", description='" + description + '\'' +
                '}';
    }
}