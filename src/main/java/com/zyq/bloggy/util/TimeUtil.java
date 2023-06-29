package com.zyq.bloggy.util;

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
}
