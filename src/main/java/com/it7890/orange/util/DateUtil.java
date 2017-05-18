package com.it7890.orange.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateUtil {

	private static Log log = LogFactory.getLog(DateUtil.class);
	
	public static final String FORMATER_YYYY_MM_DD = "yyyy-MM-dd";
	public static final String FORMATER_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	
	
	/**
	 * 返回java.sql.Date类型的当前时间
	 * @return
	 */
	public static java.sql.Date getSqlDate(){
		return getSqlDate(new java.util.Date());
	}
	
	/**
	 * 获取某日期的当天最早日期，也就是当天的0点0分0秒
	 * @param date
	 * @return
	 */
	public static Date getEariestDate(Date date){
		return DateUtil.StringToDate(DateUtil.formatFromDate("yyyy-MM-dd", date) + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * 获取某日期的当天最晚日期，也就是当天的23点59分59秒
	 * @param date
	 * @return
	 */
	public static Date getLatestDate(Date date){
		return DateUtil.StringToDate(DateUtil.formatFromDate("yyyy-MM-dd", date) + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * 返回java.sql.Date类型的时间
	 * @param date
	 * @return
	 */
	public static java.sql.Date getSqlDate(java.util.Date date){
		return new java.sql.Date(date.getTime());
	}
	
	/**
	 * 以"yyyy-MM-dd"格式来格式化日期
	 * @param date
	 * @return
	 */
	public static String formatFromDate(Date date){
		return formatFromDate(FORMATER_YYYY_MM_DD, date);
	}
	
	/**
	 * 以“yyyy-MM-dd”格式来格式化日期
	 * @param time
	 * @return
	 */
	public static String formatFromDate(Long time) {
		return formatFromDate(FORMATER_YYYY_MM_DD, time);
	}
	
	/**
	 * 按照给定的格式，格式化日期
	 * @param formater	需要的格式，常用的例如"yyyy-MM-dd HH:mm:ss"
	 * @param date  日期
	 * @return
	 */
	public static String formatFromDate(String formater, Date date){
		DateFormat df = new SimpleDateFormat(formater);
		return df.format(date);
	}
	
	/**
	 * 按照给定的格式，格式化日期
	 * @param formater	需要的格式，常用的例如"yyyy-MM-dd HH:mm:ss"
	 * @param time 日期
	 * @return
	 */
	public static String formatFromDate(String formater, Long time) {
		DateFormat df = new SimpleDateFormat(formater);
		return df.format(time);
	}
	
	/**
	 * 按照给定的格式，格式化日期
	 * @param formater	需要的格式，常用的例如"yyyy-MM-dd HH:mm:ss"
	 * @param s  可格式化为日期的字符串
	 * @return
	 */
	public static String formatFromString(String formater, String s){
		DateFormat df = new SimpleDateFormat(formater);
		return df.format(s);
	}
	
	/**
	 * 解析给定格式的日期，格式化为时间格式
	 * @param formater
	 * @param dateStr
	 * @return
	 */
	public static Long formatTimeFromString(String formater, String dateStr) {
		DateFormat df = new SimpleDateFormat(formater);
		Date date = null;
		try {
			date = df.parse(dateStr);
		} catch (ParseException e) {
			if(log.isErrorEnabled()){
				log.error("将字符串格式化成日期时出错", e);
			}
		}
		return date != null ? date.getTime() : 0l;
	}
	
	/**
	 * 字符串转化为日期</br>
	 * @param str	需要被转换为日期的字符串
	 * @param format 格式，常用的为 yyyy-MM-dd HH:mm:ss
	 * @return	java.util.Date，如果出错会返回null
	 */
	public static Date StringToDate(String str, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			if(log.isErrorEnabled()){
				log.error("将字符串格式化成日期时出错", e);
			}
		}
		return date;
	}
	
	/**
	 * 计算两个日期之间的天数</br>
	 * 任何一个参数传空都会返回-1</br>
	 * 返回两个日期的时间差，不关心两个日期的先后</br>
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public static long getDaysBetweenTwoDate(Date dateStart, Date dateEnd){
		if(null == dateStart || null == dateEnd){
			return -1;
		}
		long l = Math.abs(dateStart.getTime() - dateEnd.getTime());
		l = l/(1000*60*60*24l);
		return l;
	}
	/**
	 * @return
	 */
	public static Date getCurrentDateTime() {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		return calendar.getTime();
	}
	
	/**
	 * @return
	 */
	public static Date getCurrentDate() {
		Date now = getCurrentDateTime();
		return StringToDate(FORMATER_YYYY_MM_DD,formatFromDate(FORMATER_YYYY_MM_DD,now));
	}
	
	/**
	 * 判断某字符串是否是日期类型
	 * @param strDate
	 * @return
	 */
	public static boolean isDate(String strDate) {
		Pattern pattern = Pattern
				.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		Matcher m = pattern.matcher(strDate);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判断两个日期是否为同一天
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean isSameDay(Date d1, Date d2){
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
     * 获取当前日期是星期几<br>
     * 
     * @param dt
     * @return 当前日期是星期几
     */
	public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
 
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
 
        return weekDays[w];
    }
	
    /**
     * 获取某日期所在周的周几的日期<br/>
     * 注意！ 本方法以周一作为一周的第1天，周日为最后一天
     * @param date
     * @param number 1、周日,2、周一，3、周二，4、周三，5、周四，6、周五，7、周六
     * @return
     */
    public static Date getDayOFWeek(Date date, int number){   
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	if(calendar.get(Calendar.DAY_OF_WEEK)==1){//如果传入参数Date为周日，当number不为1时 实际上取的是下周的周几
    		if(number!=1){//取周一-周六
    			calendar.add(Calendar.DAY_OF_YEAR, -7);
    		}
    	}else{
    		if(number == 1){//如果取周日，实际上是取的当前calendar的下一周的第1天，也就是calendar中的下周的周日
        		calendar.add(Calendar.DAY_OF_YEAR, 7);
        	}
    	}
    	
    	calendar.set(Calendar.DAY_OF_WEEK,number);
    	return calendar.getTime();   
    }
    
    /**
     * 获取时间到毫秒
     * @return
     */
    public static long getCurrentTimeMillis() {
    	return System.currentTimeMillis();
    }
    
    /**
     * 格式化当前日期 yyyyMMdd格式
     * @return
     */
    public static String getCurrentDateyyyyMMdd() {
    	return formatFromDate("yyyyMMdd", new Date());
    }
    
    /**
     * date加minute分钟
     * @param date
     * @param minute
     * @return
     */
    public static Long DatePlusMinute(Date date, int minute) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)+minute);
    	return calendar.getTimeInMillis();
    }
    
    /**
     * date加minute分钟
     * @param date
     * @param minute
     * @return
     */
    public static Long DatePlusMinute(Long time, int minute) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(time);
    	calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)+minute);
    	return calendar.getTimeInMillis();
    }
    
    /**
     * date加hour小时
     * @param date
     * @param hour 小时数
     * @return
     */
    public static Long DatePlusHour(Date date, int hour) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + hour);
    	return calendar.getTimeInMillis();
    }
    
    /**
     * date加month月
     * @param date
     * @param hour 小时数
     * @return
     */
    public static Long DatePlusMonth(Date date, int month) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + month);
    	return calendar.getTimeInMillis();
    }
    
    /**
     * date减day天
     * @param day 减去的天
     * @return
     */
    public static Long DateCutDay(Date date, int day) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)  - day);
    	return calendar.getTimeInMillis();
    }
    
}
