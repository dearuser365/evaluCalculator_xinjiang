package com.kd.business.calculator;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kd.service.SystemService;
import com.kd.util.DateUtil;
import com.kd.util.SqlReader;
/**
 * 总分重算
 * */
public class ScoreCalculator implements CalculatorInterface {
	
	private static final Logger logger = Logger.getLogger(ScoreCalculator.class);
	@Autowired
	private SystemService systemService;
	@Autowired
	private ResultCodeTableDistinctor resultCodeTableDistinctor;
	
	private static final String calculateSqlDay = 
			SqlReader.readSql("/com/kd/business/sql/calculate/calculateScoreDay.sql");
	private static final String calculateSqlMonth = 
			SqlReader.readSql("/com/kd/business/sql/calculate/calculateScoreMonth.sql");
	
	@Override
	public boolean isCalInScore() {
		return true;
	}
	@Override
	public String getName() {
		return "首页得分计算";
	}
	@Override
	public void calculate(Date date) {
		logger.info("算分开始");
		calculateDayTable(date);
		calculateMonthTable(date);
		logger.info("算分结束");
	}
	
	@Override
	public void calculateMonthTable(Date date) {
		logger.info("算月分开始");
		String monthTableName = DateUtil.getResultMonthTableName(date);
		String statistime = DateUtil.dateToStr(date);
		//用于转换日期格式，进行between and 查询
		String dateStr2 = "";
		if (statistime != null && statistime.length() == 10) {
    		dateStr2 = statistime + " 12:00:00.0";
    	}
		
		String[] calculateMonthSqls = calculateSqlMonth
				.replace("${dateStr}", statistime)
				.replace("${TABLENAME}", monthTableName)
				.replace("${dateStr2}", dateStr2)
				.split(";");
		systemService.executeSql(calculateMonthSqls[0]);
		systemService.executeSql(calculateMonthSqls[1]);
		
		resultCodeTableDistinctor.distinctCodeTableByCode(monthTableName, "score");
		logger.info("算月分结束");
	}
	@Override
	public void calculateDayTable(Date date) {
		String dayTableName = DateUtil.getResultDayTableName(date);
		String statistime = DateUtil.dateToStr(date);
		//用于转换日期格式，进行between and 查询
		String dateStr2 = "";
		if (statistime != null && statistime.length() == 10) {
    		dateStr2 = statistime + " 12:00:00.0";
    	} 
		
		String[] calculateDaySqls = calculateSqlDay
					.replace("${dateStr}", statistime)
					.replace("${TABLENAME}", dayTableName)
					.replace("${dateStr2}", dateStr2)
					.split(";");
		logger.info("算日分开始");
		
		//插入默认数据
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "score", "得分", statistime, "无", "无");
		
		systemService.executeSql(calculateDaySqls[0]);
		systemService.executeSql(calculateDaySqls[1]);
		resultCodeTableDistinctor.distinctCodeTableByCode(dayTableName, "score");
		logger.info("算日分结束");		
	}
	@Override
	public boolean isUnevalu() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void calculateDsUnevalu(Date date) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void callSql(Date date) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateFlag(Date date) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getCalculatorName() {
		// TODO Auto-generated method stub
		return "ScoreCalculator";
	}

}
