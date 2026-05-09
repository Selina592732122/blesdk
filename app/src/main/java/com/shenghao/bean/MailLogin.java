package com.shenghao.bean;


import java.io.Serializable;

public class MailLogin implements Serializable {
    private String phone;    //token
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
