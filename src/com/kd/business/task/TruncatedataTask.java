package com.kd.business.task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kd.service.SystemService;
import com.kd.util.DateUtil;
import com.kd.util.PropertiesUtil;

/**
 * 
 * All rights Reserved, Designed By 科东    
 * @Description: 删除（清空）过时的台账数据 
 * @author EH.WANG
 * @date 2018年6月26日 下午2:37:25
 */
public class TruncatedataTask {
	private static final Logger logger = Logger.getLogger(TruncatedataTask.class);
	@Autowired
	private SystemService systemService;
	Properties properties = new PropertiesUtil("sysConfig.properties").getProperties();
	public void truncatedata(){
				
		logger.info("调用删除发送全量地区的台账数据的存储过程开始");
		Date date = DateUtil.getUserWantDate(-1);
		String weekofday = getWeekOfDate(date);
		String sql = "select organ_code from EVALUSYSTEM.config.datatrunc where weekofday = '"+weekofday+"'";
		logger.info("获取当天哪些地区发送PMS全量台账数据。");
		List<Map<String,Object>> list = systemService.findForJdbc(sql);
		if(!list.isEmpty() && list.size()>0){
			for(Map<String,Object> map : list){
				if(map != null && map.containsKey("ORGAN_CODE")){
					String organ_code = String.valueOf(map.get("ORGAN_CODE"));
					logger.info("哪些地区发送PMS全量台账数据。"+organ_code);
					logger.info("删除发送全量地区的台账数据。"+organ_code);
					systemService.executeSql("call EVALUSYSTEM.detail.truncatedata("+organ_code+")");
				}
			}
		}
		logger.info("调用删除发送全量地区的台账数据的存储过程结束");		
	}
	
	 /**
     * 获取当前日期是星期几<br>
     * 0代表周日
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"0", "1", "2", "3", "4", "5", "6"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

 
}
