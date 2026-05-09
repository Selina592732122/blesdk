package com.shenghao.bean;

import java.io.Serializable;
import java.util.List;

public class BindWxBean implements Serializable {
    private String phone;
    private String token;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
