package com.shenghao.okhttp;

import java.io.Serializable;

public class OkHttpBaseResp implements Serializable {

    private int code;
    private String msg;
    private String server_timestamp;

    public OkHttpBaseResp() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getServer_timestamp() {
        return server_timestamp;
    }

    public void setServer_timestamp(String server_timestamp) {
        this.server_timestamp = server_timestamp;
    }

    public boolean isSuccess() {
        return code == 0;
    }

    /**
     * token过期
     */
    public boolean invalidToken() {
        return code == 40010;
    }
}
