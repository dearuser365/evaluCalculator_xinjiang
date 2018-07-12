package com.kd.business.calculator;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kd.service.SystemService;
import com.kd.util.DateUtil;
import com.kd.util.SqlReader;
/**
 * 配电开关数完整率相关指标的计算
 * */
public class PDKGSWZLCalculator implements CalculatorInterface{
	private static final Logger logger = Logger.getLogger(PDKGSWZLCalculator.class);
	@Autowired
	private SystemService systemService;
	@Autowired
	private ResultCodeTableDistinctor resultCodeTableDistinctor;
	
	private static final String calculateSql = 
			SqlReader.readSql("/com/kd/business/sql/calculate/calculatePDKGSWZLCode.sql");
	
	@Override
	public boolean isCalInScore() {
		return true;
	}
	
	@Override
	public String getName() {
		return "配电开关数完整率";
	}
	@Override
	public void calculate(Date date) {
		logger.info("计算配电开关数完整率相关指标开始");
		calculateDayTable(date);
		calculateMonthTable(date);
		logger.info("计算配电开关数完整率相关指标结束");
	}

	@Override
	public void calculateMonthTable(Date date) {
		logger.info("计算配电开关数完整率相关月指标开始");
		String monthTableName = DateUtil.getResultMonthTableName(date);
		
		CalculateCodeUtil.insertIntoMonthIndexNoUnevalu(systemService, "dmsbreakers", date);
		CalculateCodeUtil.insertIntoMonthIndexNoUnevalu(systemService, "pmsbreakers", date);
		resultCodeTableDistinctor.distinctCodeTableByCodes(monthTableName, new String[] {"dmsbreakers", "pmsbreakers"});
		
		CalculateCodeUtil.insertIntoMonthIndex2(systemService, "breakfine", date);
		resultCodeTableDistinctor.distinctCodeTableByCode(monthTableName, "breakfine");
		
		logger.info("计算配电开关数完整率相关月指标结束");
	}

	@Override
	public void calculateDayTable(Date date) {
		logger.info("计算配电开关数完整率相关日指标开始");
		String dayTableName = DateUtil.getResultDayTableName(date);
		String statistime = DateUtil.dateToStr(date);
		
		String insertdmsbreakersSql = calculateSql.split(";")[0]
				.replace("${dateStr}", statistime)
				.replace("${TABLENAME}", dayTableName);
		String insertpmsbreakersSql = calculateSql.split(";")[1]
				.replace("${dateStr}", statistime)
				.replace("${TABLENAME}", dayTableName);
		
		
		//插入默认数据
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "dmsbreakers", "个数", statistime, "无", "无");
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "pmsbreakers", "个数", statistime, "无", "无");	
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "breakfine", "数值", statistime, "无", "无");
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "breakfine", "百分比", statistime, "无", "无");
			
		systemService.executeSql(insertdmsbreakersSql);
		systemService.executeSql(insertpmsbreakersSql);
		resultCodeTableDistinctor.distinctCodeTableByCodes(dayTableName, new String[] {"dmsbreakers", "pmsbreakers"});
		
		//配电开关数完整率
		CalculateCodeUtil.insertIntoDayIndex(systemService, dayTableName, statistime, 
				"breakfine", "dmsbreakers", "pmsbreakers", "100", date);
		
		resultCodeTableDistinctor.distinctCodeTableByCode(dayTableName, "breakfine");
		logger.info("计算配电开关数完整率相关日指标结束");
	}

	@Override
	public boolean isUnevalu() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void calculateDsUnevalu(Date date) {}

	@Override
	public void callSql(Date date) {}
	
	@Override
	public void updateFlag(Date date) {}

	@Override
	public String getCalculatorName() {
		// TODO Auto-generated method stub
		return "PDKGSWZLCalculator";
	}
	
}
