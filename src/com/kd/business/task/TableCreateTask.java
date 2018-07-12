package com.kd.business.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kd.business.tableCreator.TableCreator;
import com.kd.util.DateUtil;
/**
 * 
 * All rights Reserved, Designed By 科东    
 * @Description: 建表（结果表）定时任务 
 * @author EH.WANG
 * @date 2018年6月13日 下午2:49:35
 */
public class TableCreateTask {
	@Autowired
	private TableCreator tableCreator;

	private static final Logger logger = Logger.getLogger(TableCreateTask.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	/**
	 * 
	 * @Description: 建表任务主入口 创建一整个月的日表和月表
	 * @return void
	 *
	 */
	public void createTable(){
		Date date = DateUtil.getUserWantDate(0);
		String statistime = DateUtil.dateToStr(date);
		String endDate = statistime.substring(8,10);
		//每月的1号创建一整个月的日表和月表
		if("01".equals(endDate)){
			List<String> list = getAllDaysMonthByDate(date);
			if(!list.isEmpty() && list.size()>0){
				for(String dateStr : list){
					try {
						Date date_day = sdf.parse(dateStr);
						tableCreator.createDayTable(date_day);
					} catch (ParseException e) {
						logger.error("定时任务创建日表报错，原因：日期字符串‘"+dateStr+"’格式不符！");
					}
				}
			}
			tableCreator.createMonthTable(date);
		}
	}
 
    /***
     * 根据传入的日期获取所在月份所有日期 
     * @param d
     * @return
     */
	public static List<String> getAllDaysMonthByDate(Date d) {  
	        List<String> list= new ArrayList<String>();  
	        Date date = getMonthStart(d);  
	        Date monthEnd = getMonthEnd(d);  
	        while (!date.after(monthEnd)) {  
	         list.add(sdf.format(date));  
	            date = getNext(date);  
	        }  
	        return list;  
	}
    
    /***
	 * 获取某月的第一天
	 * @param date
	 * @return
	 */
	private static Date getMonthStart(Date date) {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        int index = calendar.get(Calendar.DAY_OF_MONTH);  
        calendar.add(Calendar.DATE, (1 - index));  
        return calendar.getTime();  
    }  
   
	/***
	 * 获取某月的最后一天
	 * @param date
	 * @return
	 */
    private static Date getMonthEnd(Date date) {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.add(Calendar.MONTH, 1);  
        int index = calendar.get(Calendar.DAY_OF_MONTH);  
        calendar.add(Calendar.DATE, (-index));  
        return calendar.getTime();  
    }  
    /***
     * 获取当前日期的下一天
     * @param date
     * @return
     */
    private static Date getNext(Date date) {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.add(Calendar.DATE, 1);  
        return calendar.getTime();  
    }
}
