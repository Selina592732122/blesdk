package com.shenghao.http;

import android.text.TextUtils;

/**
 * Created by yuzhang on 2017/11/21.
 */
public class Network {

    //0测试环境 1生产环境
    public static final int NETWORK_ENV = 0;

    public static String SERVER_HOST = null;
    public static final int TIMEOUT_INTERVAL = 10;//请求超时的时间

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_ERROR = -1;

    static {
        switch (NETWORK_ENV) {
            case 0: //测试环境
//                SERVER_HOST = "http://192.168.0.39:8080";
                SERVER_HOST = "https://shenghao.jifangwulian.com/api";
//                SERVER_HOST = "https://haibao.jifanglot.com/api";
//                SERVER_HOST = "https://testapp.jifangwulian.com/api";
                 break;
            case 1: //生产环境
//                SERVER_HOST = "https://app.jifanglot.com/api";
                SERVER_HOST = "https://haibao.jifanglot.com/api";
//                SERVER_HOST = "http://192.168.0.39:8080";
                break;
            default:
//                SERVER_HOST = "https://app.jifanglot.com/api";
                SERVER_HOST = "https://haibao.jifanglot.com/api";
//                SERVER_HOST = "http://192.168.0.39:8080";
                break;
        }
    }

    public static String getOkHttpServerHost() {
        if (!TextUtils.isEmpty(SERVER_HOST)) {
            //将末尾”/“去掉
            return SERVER_HOST;
        }
        return "";
    }

    /*static {
        switch (NETWORK_ENV) {
            case 0:
                SERVER_HOST = "http://106.15.228.8:80/";
                break;
            case 1:
                SERVER_HOST = "http://106.15.228.8:80/";
                break;
            case 2:
                SERVER_HOST = "http://106.15.228.8:80/";
                break;
            default:
                SERVER_HOST = "http://106.15.228.8:80/";
                break;
        }
    }*/
}
