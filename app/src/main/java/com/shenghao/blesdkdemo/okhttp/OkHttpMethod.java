package com.shenghao.blesdkdemo.okhttp;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class OkHttpMethod {

    @StringDef({M_GET, M_POST, M_DELETE, M_PUT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RequestMethod {

    }

    public static final String M_GET = "GET";
    public static final String M_POST = "POST";
    public static final String M_DELETE = "DELETE";
    public static final String M_PUT = "PUT";
}
