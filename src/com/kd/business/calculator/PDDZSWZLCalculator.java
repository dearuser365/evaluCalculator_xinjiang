package com.kd.business.calculator;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kd.service.SystemService;
import com.kd.util.DateUtil;
import com.kd.util.SqlReader;
/**
 * 配电刀闸数完整率相关指标的计算
 * */
public class PDDZSWZLCalculator implements CalculatorInterface{
	private static final Logger logger = Logger.getLogger(PDDZSWZLCalculator.class);
	@Autowired
	private SystemService systemService;
	@Autowired
	private ResultCodeTableDistinctor resultCodeTableDistinctor;
	
	private static final String calculateSql = 
			SqlReader.readSql("/com/kd/business/sql/calculate/calculatePDDZSWZLCode.sql");
	
	@Override
	public boolean isCalInScore() {
		return true;
	}
	
	@Override
	public String getName() {
		return "配电刀闸数完整率";
	}
	@Override
	public void calculate(Date date) {
		logger.info("计算配电刀闸数完整率相关指标开始");
		calculateDayTable(date);
		calculateMonthTable(date);
		logger.info("计算配电刀闸数完整率相关指标结束");
	}

	@Override
	public void calculateMonthTable(Date date) {
		logger.info("计算配电刀闸数完整率相关月指标开始");
		String monthTableName = DateUtil.getResultMonthTableName(date);
		
		CalculateCodeUtil.insertIntoMonthIndexNoUnevalu(systemService, "dmsdiss", date);
		CalculateCodeUtil.insertIntoMonthIndexNoUnevalu(systemService, "pmsdiss", date);
		resultCodeTableDistinctor.distinctCodeTableByCodes(monthTableName, new String[] {"dmsdiss", "pmsdiss"});
		
		CalculateCodeUtil.insertIntoMonthIndex2(systemService,"disfine",  date);
		resultCodeTableDistinctor.distinctCodeTableByCode(monthTableName, "disfine");
		
		logger.info("计算配电刀闸数完整率相关月指标结束");
	}

	@Override
	public void calculateDayTable(Date date) {
		logger.info("计算配电刀闸数完整率相关日指标开始");
		String dayTableName = DateUtil.getResultDayTableName(date);
		String statistime = DateUtil.dateToStr(date);
		
		String insertdmsdissSql = calculateSql.split(";")[0]
				.replace("${dateStr}", statistime)
				.replace("${TABLENAME}", dayTableName);
		String insertpmsdissSql = calculateSql.split(";")[1]
				.replace("${dateStr}", statistime)
				.replace("${TABLENAME}", dayTableName);
		
		
		//插入默认数据
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "dmsdiss", "个数", statistime, "无", "无");
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "pmsdiss", "个数", statistime, "无", "无");	
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "disfine", "数值", statistime, "无", "无");
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "disfine", "百分比", statistime, "无", "无");
			
		systemService.executeSql(insertdmsdissSql);
		systemService.executeSql(insertpmsdissSql);
		resultCodeTableDistinctor.distinctCodeTableByCodes(dayTableName, new String[] {"dmsdiss", "pmsdiss"});
		
		//配电刀闸数完整率
		CalculateCodeUtil.insertIntoDayIndex(systemService, dayTableName, statistime, 
				"disfine", "dmsdiss", "pmsdiss", "100", date);
		
		resultCodeTableDistinctor.distinctCodeTableByCode(dayTableName, "disfine");
		logger.info("计算配电刀闸数完整率相关日指标结束");
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
		return "PDDZSWZLCalculator";
	}
	
}
