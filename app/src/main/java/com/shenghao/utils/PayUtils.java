package com.shenghao.utils;

import java.text.DecimalFormat;

public class PayUtils {
    /**
     * 移除尾部多余的0
     */
    public static String stripDoubleTrailingZeros(Double amount) {
        // 创建一个 DecimalFormat 对象
        DecimalFormat df = new DecimalFormat("#.####");
        // 使用 DecimalFormat 对象格式化 double 值
        return df.format(amount);
    }
}
