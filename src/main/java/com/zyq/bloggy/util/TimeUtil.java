package com.zyq.bloggy.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    private static final SimpleDateFormat datFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static long getTimestampOfYearStart(int year) {
        String date = new StringBuffer().append(year).append("-01-01").toString();
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(datFormat.parse(date));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return cal.getTimeInMillis();
    }

    public static long getTimestampOfYearEnd(int year) {
        String date = new StringBuffer().append(year).append("-12-31").toString();
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(datFormat.parse(date));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return cal.getTimeInMillis();
    }

    public static String getDateString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh-MM-ss");
        return dateFormat.format(date);
    }

    /**
     * 获取从现在往前指定天数的时间戳
     *
     * @param num
     * @return
     */
    public static Timestamp getTimestampRange(Integer num) {
        LocalDate today = LocalDate.now();
        LocalDate beginDay = today.minusDays(num);
        Timestamp start = Timestamp.valueOf(beginDay.atStartOfDay());
        return start;
    }
}
