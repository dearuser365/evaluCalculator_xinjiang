package com.kd.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * <b>Application name:</b> DateUtil.java <br>
 * <b>Application describing: </b> <br>
 * <b>Copyright:</b>Copyright &copy; 2013 zzl 版权所有。<br>
 * <b>Company:</b> zzl <br>
 * <b>Date:</b> 2013-9-12 <br>
 * @author zzl
 * @version $Revision$
 */
public final class DateUtil {

	public static final String DATELONG = "yyyy-MM-dd HH:mm:ss";

    public static final String DATESHORT = "yyyy-MM-dd";
    
    public static final String MONTHLIKE = "yyyy-MM";

    public static final String TIME = " 00:00:00";

    public static final String TIMESTEMP = "yyyyMMddkkmmss";
    
    public static final String DATAyyyyMMdd = "yyyyMMdd";
    
    public final static DateFormat DAY_DFM = new SimpleDateFormat("yyyyMMdd");
	public final static DateFormat MONTH_DFM = new SimpleDateFormat("yyyyMM");
	
    private DateUtil() {
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATELONG);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     * @param strDate
     * @return
     */
    public static Date shortStrToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATAyyyyMMdd);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
     * @param dateDate
     * @return
     */
    public static String dateToStrLong(java.util.Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATELONG);
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式时间转换为字符串 yyyy-MM-dd 00:00:00
     * @param dateDate
     * @param k
     * @return
     */
    public static String dateToStrDefault(java.util.Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATESHORT);
        String dateString = formatter.format(dateDate) + TIME;
        return dateString;
    }

    /**
     * 将短时间格式时间转换为字符串 yyyy-MM-dd
     * @param dateDate
     * @param k
     * @return
     */
    public static String dateToStr(java.util.Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATESHORT);
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式时间转换为字符串 yyyy-MM
     * @param dateDate
     * @param k
     * @return
     */
    public static String dateToStrMonthLike(java.util.Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(MONTHLIKE);
        return formatter.format(dateDate);
    }
    /**
     * {获取当前的月份}
     * @return
     * @author: zzl
     */
    public static int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        return month;
    }
    
    /**
     * {获取当前的日期}
     * @return
     * @author: zzl
     */
    public static int getCurrentDate(){
    	Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        return date;
    }
    /**
     * {获取昨天的日期}
     * @return
     * @author: zzl
     */
    public static int getYesterdayDate(){
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.DAY_OF_YEAR, -1);
    	int date = calendar.get(Calendar.DATE);
    	return date;
    }
    /**
     * {获取与今天相差制定天数的日期}
     * @param flag 与今天相差的天数
     * @return
     * @author: zzl
     */
    public static Date getUserWantDate(int flag) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.DAY_OF_YEAR, flag);
    	return calendar.getTime();
    }
    /**
     * {获取这周是单周还是双周}
     * @param dateDate
     * @param k
     * @return
     */
    public static int getSomeWeek(){
    	Calendar calendar = Calendar.getInstance();
    	int someweek=calendar.get(Calendar.WEEK_OF_YEAR);
    	int week=someweek%2;
    	return week;
    }
    /**
     * {获取今天是两周的第几天}
     * @param dateDate
     * @param k
     * @return
     */
    public static int getDayOfTwoWeek(int some){
    	Calendar calendar = Calendar.getInstance();  
    	//一周第一天是否为星期天  
    	boolean isFirstSunday = (calendar.getFirstDayOfWeek() == Calendar.SUNDAY);  
    	//获取周几  
    	int weekDay = calendar.get(Calendar.DAY_OF_WEEK);  
    	//若一周第一天为星期天，则-1  
    	if(isFirstSunday){  
    	    weekDay = weekDay - 1;  
    	    if(weekDay == 0){  
    	        weekDay = 7;  
    	    }  
    	}
    	if(some==1){
    		weekDay=weekDay+7;
    	}
    	return weekDay;
    }
    public static Date getUserWantMonth(Date date, int flag) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.add(Calendar.MONTH, flag);
    	return calendar.getTime();
    }
  
    public static String getParseFileTime(String time) {
		String[] times = time.split("_");
		String returnStr = times[0] + " " + times[1].replace("-", ":");
		return returnStr;
	}
    public static String getParsesFileTime(String time){
    	String[] times = time.split(" ");
		String returnStr = times[0] + " " + times[1].replace("-", ":");
		return returnStr;
    }
    public static String transSpecialTime(String time) {
		String transTime = "";
		if (time != null) {
			if (time.length() == 14) {
				transTime = 
						time.substring(0, 4) + "-" +
						time.substring(4, 6) + "-" +
						time.substring(6, 8) + " " +
						time.substring(8, 10) + ":" +
						time.substring(10, 12) + ":" +
						time.substring(12, 14);
			}else if(time.contains("_")){
				transTime = DateUtil.getParseFileTime(time);
			}else if(time.contains(" ")){
				transTime = DateUtil.getParsesFileTime(time);
			} else {
				transTime = time;
			}
		}
		return transTime;
	}
//    public static String getResultDayTableName() {
//		return "EVALUSYSTEM.RESULT.DAY_"+DAY_DFM.format(date);
//	}
//	
//	public static String getResultMonthTableName() {
//		return "EVALUSYSTEM.RESULT.MONTH_"+MONTH_DFM.format(date);
//	}
	
	public static String getResultDayTableName(Date date) {
		return "EVALUSYSTEM.RESULT.DAY_"+DAY_DFM.format(date);
	}
	
	public static String getResultMonthTableName(Date date) {
		return "EVALUSYSTEM.RESULT.MONTH_"+MONTH_DFM.format(date);
	}
	public static String getYearMonthDay(Date date) {
		return DAY_DFM.format(date);
	}
	
	public static boolean isCurrentMonth(Date date) {
		if (date != null) {
			String currentDateMonthStr = dateToStrMonthLike(new Date());
			String dateMonthStr = dateToStrMonthLike(date);
			return currentDateMonthStr.equals(dateMonthStr);
		}
		return false;
		
	}
	
	public static List<String> getDates(String startDate, String endDate) {
        List<String> dates = new ArrayList<String>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        try {
        		start.setTime(simpleDateFormat.parse(startDate));
                end.setTime(simpleDateFormat.parse(endDate));
                dates.add(startDate);
                while (start.before(end)) {
                    start.add(Calendar.DAY_OF_MONTH, 1);
                    dates.add(simpleDateFormat.format(start.getTime()));
                }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dates;
    }
	
	public static String transEvaluToDbDate(String date) {
    	String dbDate = null;
    	if (date != null && !date.contains("-")) {
    		if (date.length() == 4) {//yyyy
    			dbDate = date;
    		} else if (date.length() == 6) {//yyyyMM
    			dbDate = (date.substring(0, 4) + 
    					 "-" + 
    					 date.substring(4, 6));
    		} else if (date.length() == 8) {//yyyyMMdd
    			dbDate = (date.substring(0, 4) + 
    					 "-" + date.substring(4, 6) + 
    					 "-" + date.substring(6, 8));
    		}
    	} else {
    		dbDate = date;
    	}
    	return dbDate;
    }
}
