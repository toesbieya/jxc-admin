package cn.toesbieya.jxc.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static final DateTimeFormatter defaultFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String dateFormat() {
        return dateFormat(null, null);
    }

    public static String dateFormat(Long time) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        return dateFormat(localDateTime, null);
    }

    public static String dateFormat(LocalDateTime time) {
        return dateFormat(time, null);
    }

    public static String dateFormat(DateTimeFormatter formatter) {
        return dateFormat(null, formatter);
    }

    public static String dateFormat(LocalDateTime time, DateTimeFormatter formatter) {
        LocalDateTime t = time == null ? LocalDateTime.now() : time;
        DateTimeFormatter f = formatter == null ? defaultFormatter : formatter;
        return t.format(f);
    }

    //获取今日零点的时间戳
    public static long getTimestampNow() {
        long currentTimestamps = System.currentTimeMillis();
        return currentTimestamps - (currentTimestamps + 60 * 60 * 8 * 1000) % (60 * 60 * 24 * 1000);
    }

    //获取n天前零点的时间戳
    public static long getTimestampBeforeNow(int before) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -before);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().getTime();
    }
}
