package com.kd.business.detailInsertor;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kd.service.SystemService;
import com.kd.util.DateUtil;
import com.kd.util.SqlReader;

/***
 * 首页一次设备明细入库
 * @author DELL
 *
 */
public class DeviceDetailInsertor implements DetailInsertorInterface{
	private static final Logger logger = 
			Logger.getLogger(DeviceDetailInsertor.class);
	
	
	private static final String calculateSql = 
			SqlReader.readSql("/com/kd/business/sql/insertDetail/DeviceDetailInsertor.sql");
	
	@Override
	public String getName() {
		return "首页一次设备明细";
	}
	@Autowired
	private SystemService systemService;
	@Override
	public void insertDetail(Date date) {
		logger.info("首页一次设备明细入库开始");
		long startTime = System.currentTimeMillis();
		String dayTableName = DateUtil.getResultDayTableName(date);
		String statistime = DateUtil.dateToStr(date);
		//地区
		String dqInsertDetailSql = calculateSql.split(";")[0].replace("${TABLENAME}", dayTableName)
				.replace("${dateStr}", statistime);
		//所有公司
		String allInsertDetailSql = calculateSql.split(";")[1].replace("${TABLENAME}", dayTableName)
				.replace("${dateStr}", statistime);
		//删除垃圾数据
		systemService.executeSql("delete from EVALUSYSTEM.RESULT.DEVICE_STATIS "+ 
								 "where count_time = '"+statistime+"'");
		systemService.executeSql(allInsertDetailSql);
		systemService.executeSql(dqInsertDetailSql);
		logger.info("首页一次设备明细入库完成，耗时 "+(System.currentTimeMillis() - startTime)+" 毫秒");
	}
}
