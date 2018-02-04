
package com.foundation.common.date;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by fqh on 15-12-25.
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	/* 定义常用时间格式 */
    /**
     * 时间格式 yyyy-MM-dd HH:mm:ss
     */
    public static final String DATE_DEFAULT_STR = "yyyy-MM-dd HH:mm:ss";
    /**
     * 时间格式yyyy-MM-dd kk:mm:ss.SSS
     */
    public static final String DATE_LONG_STR = "yyyy-MM-dd kk:mm:ss.SSS";
    /**
     * 时间格式 yyyy-MM-dd
     */
    public static final String DATE_SMALL_STR = "yyyy-MM-dd";
    /**
     * 时间格式yyMMddHHmmss
     */
    public static final String DATE_KEY_STR = "yyMMddHHmmss";
    /**
     * 时间格式yyyyMMddHHmmss
     */
    public static final String DATE_All_KEY_STR = "yyyyMMddHHmmss";
    /**
     * 时间格式MMddHHmmssSSS
     */
    public static final String DATE_All_KEY_STR_SS = "MMddHHmmssSSS";
    /**
     * 时间格式 HH:mm:ss
     */
    public static final String DATE_TIME_DEFAULT = "HH:mm:ss";
    /**
     * 标准格式时间
     */
    public static final String DATE_STANDARD = "EEE MMM d hh:mm:ss z yyyy";

    /**
     * 每天小时数
     */
    private static final long HOURS_PER_DAY = 24;
    /**
     * 每小时分钟数
     */
    private static final long MINUTES_PER_HOUR = 60;
    /**
     * 每分钟秒数
     */
    private static final long SECONDS_PER_MINUTE = 60;
    /**
     * 每秒的毫秒数
     */
    private static final long MILLIONSECONDS_PER_SECOND = 1000;
    /**
     * 每分钟毫秒数
     */
    private static final long MILLIONSECONDS_PER_MINUTE = MILLIONSECONDS_PER_SECOND * SECONDS_PER_MINUTE;
    /**
     * 每天毫秒数
     */
    @SuppressWarnings("unused")
    private static final long MILLIONSECONDS_SECOND_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLIONSECONDS_PER_SECOND;

    public static TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");


    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};


    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd）
     */
    public static String getDate() {
        return DateFormatUtils.format(new Date(), DATE_SMALL_STR);
    }

    /**
     * 将日期对象格式化成yyyy-MM-dd格式的字符串
     *
     * @param date 待格式化日期对象
     * @return 格式化后的字符串
     * @see #formatDate(Date, String)
     */
    public static String formatDate(Date date) {
        return formatDate(date, DATE_DEFAULT_STR);
    }

    /**
     * 将日期对象格式化成yyyy-MM-dd HH:mm:ss格式的字符串
     *
     * @param date 待格式化日期对象
     * @return 格式化后的字符串
     * @see #formatDate(Date, String)
     */
    public static String forDatetime(Date date) {
        if (date != null) {
            return formatDate(date, DATE_DEFAULT_STR);
        } else {
            return null;
        }

    }

    /**
     * 将日期对象格式化成HH:mm:ss格式的字符串
     *
     * @param date 待格式化日期对象
     * @return 格式化后的字符串
     * @see #formatDate(Date, String)
     */
    public static String formatTime(Date date) {
        return formatDate(date, DATE_TIME_DEFAULT);
    }

    /**
     * 将日期对象格式化成指定类型的字符串
     *
     * @param date   待格式化日期对象
     * @param format 格式化格式
     * @return 格式化后的字符串
     * @see #formatDate(Date, String)
     */
    public static String formatDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }


    /**
     * 带时区的格式化时间
     *
     * @param date
     * @param format
     * @param timeZone
     * @return
     */
    public static String formatDateTimeZone(Date date, String format, TimeZone timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(timeZone);
        return sdf.format(date);
    }

    /**
     * 按照默认yyyy-MM-dd HH:mm:ss格式返回当前时间
     *
     * @return string
     */
    public static String getNowTime() {
        SimpleDateFormat df = new SimpleDateFormat(DATE_DEFAULT_STR);
        return df.format(new Date());
    }

    /**
     * 根据pattern日期格式，返回当前时间字符串
     *
     * @param pattern
     * @return string
     */
    public static String getNowTime(String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(new Date());
    }

    /**
     * 根据日期date字符串，返回对应Date类型时间（yyyy-MM-dd HH:mm:ss）
     *
     * @param date
     * @return Date
     */
    public static Date parse(String date) {
        return parse(date, DATE_DEFAULT_STR);
    }

    /**
     * 日期型字符串转化为日期 格式
     * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
     * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
     * "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 根据日期date字符串，按照pattern解析并返回对应Date类型时间
     *
     * @param date
     * @param pattern
     * @return Date
     */
    public static Date parse(String date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 比较传入的date与当前时间 <br>
     * 返回结果: 1 比当前时间大，0.当前时间相同，-1：比当前时间小
     *
     * @param date
     * @return int
     */
    public static int compareDateWithNow(Date date) {
        Date now = new Date();
        int rnum = date.compareTo(now);
        return rnum;
    }

    /**
     * 传入进来的timestamp与当前时间的进行比较 <br>
     * 返回结果: 1 比当前时间大，0.当前时间相同，-1：比当前时间小
     *
     * @param
     * @return
     */
    public static int compareDateWithNow(long timestamp) {
        long now = nowDateToTimestamp();
        if (timestamp > now) {
            return 1;
        } else if (timestamp < now) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss返回成timestamp
     *
     * @param date
     * @return long
     * @throws ParseException
     */
    public static long dateToTimestamp(String date) throws ParseException {
        return dateToTimestamp(date, DATE_DEFAULT_STR);
    }

    /**
     * 按照dateFormat格式date转换成date日期后，返回该date的timestamp
     *
     * @param date
     * @param dateFormat
     * @return long
     * @throws ParseException
     */
    public static long dateToTimestamp(String date, String dateFormat) throws ParseException {
        long timestamp = new SimpleDateFormat(dateFormat).parse(date).getTime();
        return timestamp;
    }

    /**
     * 返回当前时间的时间戳
     *
     * @return long
     */
    public static long nowDateToTimestamp() {
        long timestamp = new Date().getTime();
        return timestamp;
    }

    /**
     * 将时间戳返回dateFormat格式的日期字符串
     *
     * @param timestamp
     * @param dateFormat
     * @return String
     */
    public static String timestampToDate(long timestamp, String dateFormat) {
        String date = new SimpleDateFormat(dateFormat).format(new Date(timestamp));
        return date;
    }

    /**
     * 将时间戳返回("yyyy-MM-dd HH:mm:ss)格式的日期字符串
     *
     * @param timestamp
     * @return String
     */
    public static String timestampToDate(long timestamp) {
        return timestampToDate(timestamp, DATE_DEFAULT_STR);
    }

    /***
     * 指定日期时间分钟上加上分钟数
     *
     * @param date
     * @param minutes
     * @return
     */
    public static Date changeMinute(Date date, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutes);
        return cal.getTime();
    }

    /**
     * 指定日期时间上加上时间数
     *
     * @param date
     * @param hours
     * @return
     */
    public static Date changeHours(Date date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        return cal.getTime();
    }

    /**
     * 指定的日期加减天数
     *
     * @param date
     * @param days
     * @return
     */
    public static Date changeDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    /**
     * 指定的日期加减年数
     *
     * @param date
     * @param years
     * @return
     */
    public static Date changeYear(Date date, int years) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, years);
        return cal.getTime();
    }

    /**
     * 获得两个日期之间相差的分钟数。（date1 - date2）
     *
     * @param date1
     * @param date2
     * @return 返回两个日期之间相差的分钟数值
     */
    public static int intervalMinutes(Date date1, Date date2) {
        long intervalMillSecond = date1.getTime() - date2.getTime();
        // 相差的分钟数 = 相差的毫秒数 / 每分钟的毫秒数 (小数位采用进位制处理，即大于0则加1)
        return (int) (intervalMillSecond / MILLIONSECONDS_PER_MINUTE + (intervalMillSecond % MILLIONSECONDS_PER_MINUTE > 0 ? 1 : 0));
    }

    /**
     * 获得两个日期之间相差的秒数差（date1 - date2）
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int intervalSeconds(Date date1, Date date2) {
        long intervalMillSecond = date1.getTime() - date2.getTime();
        return (int) (intervalMillSecond / MILLIONSECONDS_PER_SECOND + (intervalMillSecond % MILLIONSECONDS_PER_SECOND > 0 ? 1 : 0));
    }

    /**
     * 比较date1，date2两个时间
     *
     * @return boolean
     * @throws
     */
    public static boolean beforeDate(Date date1, Date date2) {
        return date1.before(date2);
    }

    /**
     * 比较date1，date2，两个时间字符串
     *
     * @return boolean
     * @throws
     */
    public static boolean beforeDate(String date1, String date2) {
        Date dt1, dt2;
        dt1 = parse(date1);
        dt2 = parse(date2);
        return beforeDate(dt1, dt2);
    }

    /**
     * 判断是否闰年
     *
     * @param year
     * @return
     */
    public static boolean isLeapYear(int year) {
        return (year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0));
    }

    /**
     * 一个月有几天
     *
     * @param year
     * @param month
     * @return
     */
    public static int dayInMonth(int year, int month) {
        boolean yearleap = isLeapYear(year);
        int day;
        if (yearleap && month == 2) {
            day = 29;
        } else if (!yearleap && month == 2) {
            day = 28;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            day = 30;
        } else {
            day = 31;
        }
        return day;
    }

    /**
     * 获取输入 周的开始日期
     *
     * @param week like 201412
     * @return 8位日期 like 20140317
     */
    public static String getWeekBeginDate(String week) {
        if (week == null || "".equals(week) || week.length() < 5) {
            throw new RuntimeException("由于缺少必要的参数，系统无法进行制定的周换算.");
        }
        try {
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.MONDAY);   // 设置一个星期的第一天为星期1，默认是星期日
            cal.set(Calendar.YEAR, Integer.parseInt(week.substring(0, 4)));
            cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(week.substring(4,
                    week.length())));
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            return df.format(cal.getTime());
        } catch (Exception e) {
            throw new RuntimeException("进行周运算时输入得参数不符合系统规格." + e);
        }
    }


    /**
     * 获取输入 周的结束日期
     *
     * @param week like 201412
     * @return 8位日期 like 20140323
     */
    public static String getWeekEndDate(String week) {
        if (week == null || "".equals(week) || week.length() < 5) {
            throw new RuntimeException("由于缺少必要的参数，系统无法进行制定的周换算.");
        }
        try {
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.MONDAY);   // 设置一个星期的第一天为星期1，默认是星期日
            cal.set(Calendar.YEAR, Integer.parseInt(week.substring(0, 4)));
            cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(week.substring(4,
                    week.length())));
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            return df.format(cal.getTime());
        } catch (Exception e) {
            throw new RuntimeException("进行周运算时输入得参数不符合系统规格." + e);
        }
    }

    /**
     * 获取过去的小时
     * @param date
     * @return
     */
    public static long pastHour(Date date) {
        long t = new Date().getTime()-date.getTime();
        return t/(60*60*1000);
    }

    /**
     * 获取过去的分钟
     * @param date
     * @return
     */
    public static long pastMinutes(Date date) {
        long t = new Date().getTime()-date.getTime();
        return t/(60*1000);
    }
    /**
     * 转换为时间（天,时:分:秒.毫秒）
     * @param timeMillis
     * @return
     */
    public static String formatDateTime(long timeMillis){
        long day = timeMillis/(24*60*60*1000);
        long hour = (timeMillis/(60*60*1000)-day*24);
        long min = ((timeMillis/(60*1000))-day*24*60-hour*60);
        long s = (timeMillis/1000-day*24*60*60-hour*60*60-min*60);
        long sss = (timeMillis-day*24*60*60*1000-hour*60*60*1000-min*60*1000-s*1000);
        return (day>0?day+",":"")+hour+":"+min+":"+s+"."+sss;
    }


        /**
         * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
         */
        public static String getDate(String pattern) {
            return DateFormatUtils.format(new Date(), pattern);
        }

        /**
         * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
         */
        public static String formatDate(Date date, Object... pattern) {
            String formatDate = null;
            if (pattern != null && pattern.length > 0) {
                formatDate = DateFormatUtils.format(date, pattern[0].toString());
            } else {
                formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
            }
            return formatDate;
        }

        /**
         * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
         */
        public static String formatDateTime(Date date) {
            return formatDate(date, "yyyy-MM-dd HH:mm:ss");
        }

        /**
         * 得到当前时间字符串 格式（HH:mm:ss）
         */
        public static String getTime() {
            return formatDate(new Date(), "HH:mm:ss");
        }

        /**
         * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
         */
        public static String getDateTime() {
            return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        }

        /**
         * 得到当前年份字符串 格式（yyyy）
         */
        public static String getYear() {
            return formatDate(new Date(), "yyyy");
        }

        /**
         * 得到当前月份字符串 格式（MM）
         */
        public static String getMonth() {
            return formatDate(new Date(), "MM");
        }

        /**
         * 得到当天字符串 格式（dd）
         */
        public static String getDay() {
            return formatDate(new Date(), "dd");
        }

        /**
         * 得到当前星期字符串 格式（E）星期几
         */
        public static String getWeek() {
            return formatDate(new Date(), "E");
        }


        /**
         * 获取过去的天数
         * @param date
         * @return
         */
        public static long pastDays(Date date) {
            long t = new Date().getTime()-date.getTime();
            return t/(24*60*60*1000);
        }


        /**
         * 获取两个日期之间的天数
         *
         * @param before
         * @param after
         * @return
         */
        public static double getDistanceOfTwoDate(Date before, Date after) {
            long beforeTime = before.getTime();
            long afterTime = after.getTime();
            return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
        }
        
        /**
         * 
        * @Title: pastYeas 
        * @Description: 获取过去的年数
        * @author chengchen
        * @date 2016年11月15日 下午3:00:44 
        * @param @param date
        * @param @return    设定参数 
        * @return double    返回类型 
        * @throws
         */
        public static double pastYeas(Date date){
        	return pastDays(date)/365L;
        }


        /**
         * @Title:
         * @Description: 通过longtime获取date
         * @author chunyangli
         * @date 2016/12/28 12:04
         * @param     
         * @return    
         * @throws 
         */
        public static Date getDateByLongTime(Long dateL){
            try {
                if(null!=dateL){
                   return new Date(dateL);
                }else{
                    return null;
                }
            } catch (Exception e) {
               
            }
            return null;
        }
    }
