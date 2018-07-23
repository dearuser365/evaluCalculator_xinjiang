package com.kd.business.calculator;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kd.service.SystemService;
import com.kd.util.DateUtil;
import com.kd.util.SqlReader;

/**
 * 
 * All rights Reserved, Designed By 科东    
 * @Description: 计算地区指标 
 * @author EH.WANG
 * @date 2018年6月28日 上午10:50:54
 */
public class DistrictCalculator {
	private static final Logger logger = Logger.getLogger(DistrictCalculator.class);
	
	
//	organ.flag=0/1 县公司/本部  organ.subtype=0/1 有/无县公司
//	averall.flag=0/1 地区=本部+县公司/地区=本部  averall.code_type=0/1 指标值(如数值、百分比)/总数（如个数，小时）
	private static final String calculateAverage1Sql = 
			SqlReader.readSql("/com/kd/business/sql/calculate/calculateAverage1.sql");
	
	private static final String calculateAverage2Sql = 
			SqlReader.readSql("/com/kd/business/sql/calculate/calculateAverage2.sql");
	
	private static final String calculateAverage3Sql = 
			SqlReader.readSql("/com/kd/business/sql/calculate/calculateAverage3.sql");

	private static final String calculateAverage4Sql = 
			SqlReader.readSql("/com/kd/business/sql/calculate/calculateAverage4.sql");
	
	private static final String calculateAll1 = 
			SqlReader.readSql("/com/kd/business/sql/calculate/calculateAll1.sql");

	private static final String calculateAll2 = 
			SqlReader.readSql("/com/kd/business/sql/calculate/calculateAll2.sql");
	
	@Autowired
	private SystemService systemService;
	@Autowired
	private ResultCodeTableDistinctor resultCodeTableDistinctor;
	
	public void calculate(Date date) {
		calculateDate(date);
		calculateMonth(date);
	}
	public void calculateDate(Date date) {
		logger.info("计算日表地区指标");
		calculateDistrict(DateUtil.getResultDayTableName(date), date);
		logger.info("计算日表地区指标结束");
	}
	public void calculateMonth(Date date) {
		logger.info("计算月表地区指标");
		calculateDistrict(DateUtil.getResultMonthTableName(date), date);
		logger.info("计算月表地区指标结束");
	}
	private void calculateDistrict(String tableName, Date date) {
		String statistime = DateUtil.dateToStr(date);
		systemService.executeSql(calculateAverage1Sql
				.replace("${TABLENAME}", tableName)
				.replace("${dateStr}", statistime));
		systemService.executeSql(calculateAverage2Sql
				.replace("${TABLENAME}", tableName)
				.replace("${dateStr}", statistime));
		systemService.executeSql(calculateAverage3Sql
				.replace("${TABLENAME}", tableName)
				.replace("${dateStr}", statistime));
		systemService.executeSql(calculateAverage4Sql
				.replace("${TABLENAME}", tableName)
				.replace("${dateStr}", statistime));
		systemService.executeSql(calculateAll1
				.replace("${TABLENAME}", tableName)
				.replace("${dateStr}", statistime));
		systemService.executeSql(calculateAll2
				.replace("${TABLENAME}", tableName)
				.replace("${dateStr}", statistime));
		
		resultCodeTableDistinctor.distinctCodeTable(tableName);
	}
	
}
