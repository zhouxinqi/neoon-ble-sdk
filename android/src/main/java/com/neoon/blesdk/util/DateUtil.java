package com.neoon.blesdk.util;

import android.content.ContentResolver;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 作者:东芝(2017/12/5).
 * 功能:日期工具
 */

public class DateUtil {
    public static final String SECOND = "ss";
    public static final String MINUTE = "mm";
    public static final String HOUR = "HH";
    public static final String DAY = "dd";
    public static final String DD = "dd";
    public static final String MONTH = "MM";
    public static final String MM = "MM";
    public static final String YEAR = "yyyy";
    public static final String YYYY = "yyyy";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM = "yyyy-MM";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String MM_DD = "MM-dd";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String MM_SS = "mm:ss";
    public static final String HH_MM = "HH:mm";
    public static final int CALENDAR_SECOND = Calendar.SECOND;
    public static final int CALENDAR_MINUTE = Calendar.MINUTE;
    public static final int CALENDAR_HOUR = Calendar.HOUR_OF_DAY;
    public static final int CALENDAR_DAY = Calendar.DAY_OF_MONTH;
    public static final int CALENDAR_MONTH = Calendar.MONTH;
    public static final int CALENDAR_YEAR = Calendar.YEAR;

    public static final int OTHER_DAY = -1;
    public static final int TODAY = 0;
    public static final int TOMORROW = 1;
    // 秒数常亮
    private static final int S_YEAR = 365 * 24 * 60 * 60;// 年
    private static final int S_MONTH = 30 * 24 * 60 * 60;// 月
    private static final int S_DAY = 24 * 60 * 60;// 天
    private static final int S_HOUR = 60 * 60;// 小时
    private static final int S_MINUTE = 60;// 分钟
    // 星期
    private static String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四",
            "星期五", "星期六"};

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------当天日期计算-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 得到当前日期的字符串
     *
     * @return
     */
    public static String getCurrentDate(String format) {
        return getDate(format, System.currentTimeMillis());
    }

    /**
     * 得到当前日期的毫秒数（精确到时分）
     *
     * @return
     */
    public static long getCurrentDate() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /**
     * 获取想要的日期字符串
     *
     * @param format 格式
     * @param day    天
     * @return String
     */
    public static String getWhichDate(String format, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return getDate(format, calendar);
    }

    public static Calendar copy(Calendar calendar) {
        Calendar currentCalendar = getCurrentCalendar();
        currentCalendar.setTimeInMillis(calendar.getTimeInMillis());
        return currentCalendar;
    }

    /**
     * 得到当前Calendar
     *
     * @return
     */
    public static Calendar getCurrentCalendar() {
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.setTimeInMillis(System.currentTimeMillis());
        return c;
    }

    /**
     * 得到当天开始时间 则凌晨(2017-12-01 00:00:00)
     *
     * @return
     */
    public static Calendar getCurrentCalendarBegin() {
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    /**
     * 得到当天开始时间 则凌晨(2017-12-01 23:59:59)
     *
     * @return
     */
    public static Calendar getCurrentCalendarEnd() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.MILLISECOND, 0/*999*/);
        return c;
    }


    /**
     * 当前是否上午
     *
     * @return
     */
    public static boolean isCurrentAM() {
        return getCurrentCalendar().get(Calendar.AM_PM) == Calendar.AM;
    }

    /**
     * 当前是否下午
     *
     * @return
     */
    public static boolean isCurrentPM() {
        return getCurrentCalendar().get(Calendar.AM_PM) == Calendar.PM;
    }

    /**
     * 是24小时制吗?
     * @param context
     * @return
     */
    public static boolean isTimeUnit24(Context context) {
        try {
            ContentResolver cv = context.getContentResolver();
            String TIME_12_24 = android.provider.Settings.System.getString(cv,
                    android.provider.Settings.System.TIME_12_24);
            return "24".equals(TIME_12_24);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------当天日期计算-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------日期格式化-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 得到年
     *
     * @return
     */
    public static int getYear(Calendar calendar) {
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 得到年
     *
     * @return
     */
    public static int getYear(Date date) {
        return getYear(convertDateToCalendar(date));
    }

    /**
     * 得到年
     *
     * @return
     */
    public static int getYear(long timeInMillis) {
        return getYear(convertLongToCalendar(timeInMillis));
    }

    /**
     * 得到月
     *
     * @return
     */
    public static int getMonth(Calendar calendar) {
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 得到月
     *
     * @return
     */
    public static int getMonth(Date date) {
        return getMonth(convertDateToCalendar(date));
    }

    /**
     * 得到月
     *
     * @return
     */
    public static int getMonth(long timeInMillis) {
        return getMonth(convertLongToCalendar(timeInMillis));
    }

    /**
     * 得到月第几号(得到天)
     *
     * @return
     */
    public static int getDay(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 得到月第几号(得到天)
     *
     * @return
     */
    public static int getDay(Date date) {
        return getDay(convertDateToCalendar(date));
    }

    /**
     * 得到月第几号(得到天)
     *
     * @return
     */
    public static int getDay(long timeInMillis) {
        return getDay(convertLongToCalendar(timeInMillis));
    }

    /**
     * 时
     *
     * @return
     */
    public static int getHour(String date) throws ParseException {
        return getHour(convertStringToCalendar(YYYY_MM_DD_HH_MM_SS, date));

    }

    /**
     * 时
     *
     * @return
     */
    public static int getHour(Calendar calendar) {
        return calendar.get(Calendar.HOUR_OF_DAY);

    }

    /**
     * 时
     *
     * @return
     */
    public static int getHour(Date date) {
        return getHour(convertDateToCalendar(date));

    }

    /**
     * 时
     *
     * @return
     */
    public static int getHour(long timeInMillis) {
        return getHour(convertLongToCalendar(timeInMillis));

    }

    /**
     * 分
     *
     * @return
     */
    public static int getMinute(String date) throws ParseException {
        return getMinute(convertStringToCalendar(YYYY_MM_DD_HH_MM_SS, date));

    }

    /**
     * 分
     *
     * @return
     */
    public static int getMinute(Calendar calendar) {
        return calendar.get(Calendar.MINUTE);

    }

    /**
     * 分
     *
     * @return
     */
    public static int getMinute(Date date) {
        return getMinute(convertDateToCalendar(date));

    }


    /**
     * 分
     *
     * @return
     */
    public static int getMinute(long timeInMillis) {
        return getMinute(convertLongToCalendar(timeInMillis));

    }


    /**
     * 秒
     *
     * @return
     */
    public static int getSecond(String date) throws ParseException {
        return getSecond(convertStringToCalendar(YYYY_MM_DD_HH_MM_SS, date));
    }

    /**
     * 秒
     *
     * @return
     */
    public static int getSecond(Calendar calendar) {
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 秒
     *
     * @return
     */
    public static int getSecond(Date date) {
        return getSecond(convertDateToCalendar(date));
    }

    /**
     * 秒
     *
     * @return
     */
    public static int getSecond(long timeInMillis) {
        return getSecond(convertLongToCalendar(timeInMillis));
    }

    /**
     * 格式化日期时间
     *
     * @param format 格式如yyyy-MM-dd HH:mm:SS...
     * @return
     */
    public static String getDate(String format, long timeMillis) {
        return new SimpleDateFormat(format, Locale.ENGLISH).format(timeMillis);
    }


    /**
     * 格式化日期时间
     *
     * @param format 格式如yyyy-MM-dd HH:mm:SS...
     * @return
     */
    public static String getDate(String format, Date date) {
        return new SimpleDateFormat(format, Locale.ENGLISH).format(date);
    }

    /**
     * 格式化日期时间
     *
     * @param inFormat  格式如yyyy-MM-dd HH:mm:SS...
     * @param outFormat 格式如yyyy-MM-dd HH:mm:SS...
     * @return
     */
    public static String getDate(String inFormat, String outFormat, String date) {
        try {
            return convertStringToNewString(inFormat, outFormat, date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化日期时间
     *
     * @param format 格式如yyyy-MM-dd HH:mm:SS...
     * @return
     */
    public static String getDate(String format, Calendar calendar) {
        return getDate(format, calendar.getTime());
    }

    /**
     * 获取年最后一天
     *
     * @return
     */
    public static Calendar getYearLastDate(Calendar calendar) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(calendar.getTimeInMillis());
        c.set(Calendar.MONTH, c.getActualMaximum(Calendar.MONTH));
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    /**
     * 获取年第一天
     *
     * @return
     */
    public static Calendar getYearFirstDate(Calendar calendar) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(calendar.getTimeInMillis());
        c.set(Calendar.MONTH,0);
        c.set(Calendar.DAY_OF_MONTH,1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    /**
     * 获取月最后一天
     *
     * @return
     */
    public static Calendar getMonthLastDate(Calendar calendar) {
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(calendar.getTimeInMillis());
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        return ca;
    }

    /**
     * 获取月第一天
     *
     * @return
     */
    public static Calendar getMonthFirstDate(Calendar calendar) {
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(calendar.getTimeInMillis());
        ca.set(Calendar.DAY_OF_MONTH, 1);//
        return ca;
    }

    /**
     * 获取日期本周第一天
     *
     * @return
     */
    public static Calendar getWeekFirstDate(Calendar calendar) {
        Calendar ca = Calendar.getInstance(Locale.CHINA);
        ca.setTimeInMillis(calendar.getTimeInMillis());
        ca.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return ca;
    }

    /**
     * 获取日期本周最后一天
     *
     * @return
     */
    public static Calendar getWeekLastDate(Calendar calendar) {
        Calendar ca = Calendar.getInstance(Locale.CHINA);
        ca.setTimeInMillis(calendar.getTimeInMillis());
        ca.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        return ca;
    }

    /**
     * 获取日期相差天数
     *
     * @return
     */
    public static int getDateOffset(Calendar low, Calendar high) {
        return Math.round(((low.getTimeInMillis() - high.getTimeInMillis()) * 1.0f / (1000 * 3600 * 23.9f)));
    }
    /**
     * 获取日期相差天数
     *
     * @return
     */
    public static int getDateOffset(String low, String high) {
        try {
            return  getDateOffset(convertStringToCalendar(YYYY_MM_DD,low),convertStringToCalendar(YYYY_MM_DD,high));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 得到某天星期几(中文)
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String getWeekString(int year, int month, int day) {
        return weekDays[getWeekIndex(year, month, day)];
    }

    /**
     * 得到某天星期几
     *
     * @param calendar
     * @return
     */
    public static String getWeekString(Calendar calendar) {
        return weekDays[getWeekIndex(calendar)];
    }


    /**
     * 得到某天星期几(中文)
     * 某周的第几天
     *
     * @param year
     * @param month
     * @param day
     * @return index
     */
    public static int getWeekIndex(int year, int month, int day) {
        Calendar time = Calendar.getInstance();
        time.set(year, month - 1, day);
        return time.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 得到某天星期几(中文)
     * 某周的第几天
     *
     * @return index
     */
    public static int getWeekIndex(Calendar calendar) {
        Calendar time = Calendar.getInstance();
        time.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        return time.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 得到某月的第几天
     *
     * @param year
     * @param month
     * @param day
     * @return index
     */
    public static int getMonthIndex(int year, int month, int day) {
        Calendar time = Calendar.getInstance();
        time.set(year, month - 1, day);
        return time.get(Calendar.DAY_OF_MONTH) - 1;
    }

    /**
     * 得到某月的第几天
     *
     * @return index
     */
    public static int getMonthIndex(Calendar calendar) {
        Calendar time = Calendar.getInstance();
        time.set(calendar.get(Calendar.YEAR), calendar.get(CALENDAR_MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        return time.get(Calendar.DAY_OF_MONTH) - 1;
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------日期格式化-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------日期类型互相转换-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    public static Date convertCalendarToDate(Calendar calendar) {
        return convertLongToDate(convertCalendarToLong(calendar));
    }

    public static Date convertLongToDate(long timeInMillis) {
        return new Date(timeInMillis);
    }

    public static Date convertStringToDate(String format, String date) throws ParseException {
        return convertLongToDate(convertStringToLong(format, date));
    }

    public static long convertDateToLong(Date date) throws ParseException {
        return date.getTime();
    }

    public static long convertCalendarToLong(Calendar calendar) {
        return calendar.getTimeInMillis();
    }

    public static long convertStringToLong(String format, String date) throws ParseException {
        return new SimpleDateFormat(format, Locale.ENGLISH).parse(date).getTime();
    }


    public static Calendar convertDateToCalendar(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return instance;
    }

    public static Calendar convertLongToCalendar(long timeInMillis) {
        return convertDateToCalendar(convertLongToDate(timeInMillis));
    }

    public static Calendar convertStringToCalendar(String format, String date) throws ParseException {
        return convertDateToCalendar(convertStringToDate(format, date));
    }
    public static String convertCalendarToString(String format, Calendar calendar)  {
        return getDate(format,convertCalendarToDate(calendar));
    }

    /**
     * 将字符串的日期转成新的规则的日期
     *
     * @param inFormat
     * @param outFormat
     * @param inDate
     * @return
     * @throws ParseException
     */
    public static String convertStringToNewString(String inFormat, String outFormat, String inDate) throws ParseException {
        return getDate(outFormat, convertStringToDate(inFormat, inDate));
    }


    /**
     * 时间索引
     *
     * @param calendar
     * @param mEveryMinutes 时间间隔
     * @return
     */
    public static int convertTimeToIndex(Calendar calendar, int mEveryMinutes) {
        return convertTimeToIndex(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), mEveryMinutes);
    }

    /**
     * 时间索引
     *
     * @param mEveryMinutes 时间间隔
     * @return
     */
    public static int convertTimeToIndex(int hour, int minute, int mEveryMinutes) {
        return hour * (60 / mEveryMinutes) + (minute / mEveryMinutes);
    }

    /**
     * 时间索引
     *
     * @param timeIndex
     * @param mEveryMinutes
     * @return
     */
    public static HMS convertIndexToTime(int timeIndex, int mEveryMinutes) {
//        index=35，index%2=r，int h=index/2,if(r>0) h+1
        int m = timeIndex % (60 / mEveryMinutes);
        int h = timeIndex / (60 / mEveryMinutes);
        if(mEveryMinutes>1&&m>0){
            m=mEveryMinutes;
        }
        HMS HMS = new HMS();
        HMS.setHour(h);
        HMS.setMinute(m);
        HMS.setSecond(0);
        return HMS;
    }
    /**
     * 从毫秒中取出 时分秒
     * @param timeInMillis
     * @return
     */
    public static HMS getHMSFromMillis(long timeInMillis) {

        return getHMSFromMinutes((int) (timeInMillis/1000));
    }

    /**
     * 从 分钟中取出 时分秒
     * @param minutes
     * @return
     */
    public static HMS getHMSFromMinutes(int minutes) {
        int hour = minutes / 3600;
        int minute = minutes / 60 % 60;
        int second = minutes % 60;
        return new HMS(hour, minute, second);
    }


    /**
     * 从 字符串的yyyy-MM-dd HH:mm:ss中取出 时分秒
     * 你可以传 任意日期格式 反正最后返回的是时分秒, 如果你传入的不带时分秒 则返回0
     * 如果你传日的 只是时和分 则返回时 秒是0
     * @param inFormat
     * @param time
     * @return
     */
    public static HMS getHMSFromString(String inFormat, String time) throws ParseException {
        Calendar calendar = convertStringToCalendar(inFormat, time);
        return new HMS(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    /**
     * 从 秒中取出 时分秒
     * @param seconds
     * @return
     */
    public static HMS getHMSFromSeconds(int seconds) {
        return getHMSFromMillis(seconds*1000L);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------日期类型互相转换-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------没有对比就没有伤害-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 忽略时间的判断两个值是否是同一日日期 (年月日)
     *
     * @param format date1,date2的格式
     * @param date1
     * @param date2
     * @return
     * @throws Exception
     */
    public static boolean equalsDate(String format, String date1, String date2) {
        return equalsDate(format, date1, format, date2);
    }

    /**
     * 忽略时间的判断两个值是否是同一日日期 (年月日)
     *
     * @param format1 date1 的格式
     * @param format2 date2 的格式
     * @param date1
     * @param date2
     * @return
     * @throws Exception
     */
    public static boolean equalsDate(String format1, String date1, String format2, String date2) {
        try {
            String newDate1 = convertStringToNewString(format1, YYYY_MM_DD, date1);
            String newDate2 = convertStringToNewString(format2, YYYY_MM_DD, date2);
            return newDate1.equalsIgnoreCase(newDate2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 忽略时间的判断两个值是否是同一日日期 (年月日)
     * (该方法的日期可以是YYYY-MM-DD HH:mm:ss 必须遵循YYYY-MM-DD HH:mm:ss 规则  不能是YYYY MM DD或YYYY/MM/DD 或HH-mm-ss等 不要乱来!  否则请使用其他的方法.我有写)
     *
     * @param date1
     * @param date2
     * @return
     * @throws Exception
     */
    public static boolean equalsDate(String date1, String date2) {
        return equalsDate(YYYY_MM_DD, date1, YYYY_MM_DD, date2);
    }

    /**
     * 忽略时间的判断两个值是否是同一日日期 (年月日)
     *
     * @param calendar1
     * @param calendar2
     * @return
     * @throws Exception
     */
    public static boolean equalsDate(Calendar calendar1, Calendar calendar2) {
        return equalsDate(YYYY_MM_DD, getDate(YYYY_MM_DD, calendar1), YYYY_MM_DD, getDate(YYYY_MM_DD, calendar2));
    }

    /**
     * 忽略时间的判断两个值是否是同一日日期 (年月日)
     *
     * @param calendar1
     * @param calendar2
     * @return
     * @throws Exception
     */
    public static boolean equalsDate(String format, Calendar calendar1, Calendar calendar2) {
        return equalsDate(format, getDate(format, calendar1), format, getDate(format, calendar2));
    }

    /**
     * 这个日期是否是今天
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static boolean equalsToday(String date) {
        return equalsDate(date, getCurrentDate(YYYY_MM_DD));
    }

    /**
     * 这个日期是否是今天
     *
     * @param calendar
     * @return
     * @throws Exception
     */
    public static boolean equalsToday(Calendar calendar) {
        return equalsDate(getDate(YYYY_MM_DD, calendar), getCurrentDate(YYYY_MM_DD));
    }


    /**
     * 判断哪一天
     *
     * @param arg 参数日期
     * @return TODAY 今天，TOMORROW明天，YESTERDAY之前
     * @throws ParseException
     */
    public static int whichDay(String arg) throws ParseException {
        boolean isToday = equalsDate(YYYY_MM_DD_HH_MM, arg, getCurrentDate(YYYY_MM_DD_HH_MM));
        boolean isTomorrow = equalsDate(YYYY_MM_DD_HH_MM, arg, getWhichDate(YYYY_MM_DD_HH_MM, 1));
        if (isToday) {
            return TODAY;
        } else if (isTomorrow) {
            return TOMORROW;
        } else {
            return OTHER_DAY;
        }
    }



    public static class HMS {
        int hour;
        int minute;
        int second;

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public int getSecond() {
            return second;
        }

        public void setSecond(int second) {
            this.second = second;
        }

        public HMS() {
        }

        public HMS(int hour, int minute, int second) {
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }
    }
}
