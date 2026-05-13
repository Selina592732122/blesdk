package com.shenghao.blesdkdemo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    public static final String PATTERN_01 = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_02 = "yyyy-MM-dd";
    public static final String PATTERN_03 = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String PATTERN_04 = "yyyy年MM月dd日";
    public static final String PATTERN_05 = "yyyy-MM-dd HH:mm";
    public static final String PATTERN_06 = "HH:mm";
    public static final String PATTERN_07 = "HH:mm:ss";
    public static final String PATTERN_08 = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间戳转换成字符串
     */
    public static String getDateToString(long milliSecond, String pattern) {
        Date date = new Date(milliSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 字符串转换成时间戳
     */
    public static long getStringToDate(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static String getStringByYearMonthDay(int year, int month, int day, String pattern) {
        String dateStr;
        if (PATTERN_04.equals(pattern)) {
            dateStr = year + "年" + fillZero(month) + "月" + fillZero(day) + "日";
        } else {
            dateStr = year + "-" + fillZero(month) + "-" + fillZero(day);
        }
        return dateStr;
    }

    /**
     * 月、日补零
     */
    public static String fillZero(int num) {
        if (num < 10) {
            return "0".concat(String.valueOf(num));
        }
        return String.valueOf(num);
    }

    /**
     * 获取其他月份的当天时间戳
     */
    public static long getTimeMillisByMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, month);
        return calendar.getTimeInMillis();
    }

    /**
     * 时间戳转星期几
     */
    public static String getWeekDay(long time) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(new Date(time));
        int year = cd.get(Calendar.YEAR); //获取年份
        int month = cd.get(Calendar.MONTH); //获取月份
        int day = cd.get(Calendar.DAY_OF_MONTH); //获取日期
        int week = cd.get(Calendar.DAY_OF_WEEK); //获取星期

        String weekString;
        switch (week) {
            case Calendar.SUNDAY:
                weekString = "星期日";
                break;
            case Calendar.MONDAY:
                weekString = "星期一";
                break;
            case Calendar.TUESDAY:
                weekString = "星期二";
                break;
            case Calendar.WEDNESDAY:
                weekString = "星期三";
                break;
            case Calendar.THURSDAY:
                weekString = "星期四";
                break;
            case Calendar.FRIDAY:
                weekString = "星期五";
                break;
            default:
                weekString = "星期六";
                break;

        }

        return weekString;
    }

    /**
     * 将毫秒转换为"00:00:00"格式的时分秒字符串。
     *
     * @param milliseconds 毫秒数
     * @return 格式化后的时分秒字符串
     */
    public static String formatMilliseconds(long milliseconds) {
        long seconds = milliseconds / 1000;
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;

        // 使用String.format来确保每个数字都是两位数，并且前面有0填充
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String getRidingDisplayStartTime(String startTime) {
        return getDateToString(Long.parseLong(startTime), TimeUtils.PATTERN_06);
    }

    public static String getRidingDisplayEndTime(String endTime) {
        return getDateToString(Long.parseLong(endTime), TimeUtils.PATTERN_06);
    }

    public static String getRidingDisplayDate(String startTime) {
        return getDateToString(Long.parseLong(startTime), TimeUtils.PATTERN_02) + " "
                + getWeekDay(Long.parseLong(startTime));
    }

}
