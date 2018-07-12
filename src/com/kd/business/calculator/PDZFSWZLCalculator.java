package com.kd.business.calculator;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kd.service.SystemService;
import com.kd.util.DateUtil;
import com.kd.util.SqlReader;
/**
 * 配电站房数完整率相关指标的计算
 * */
public class PDZFSWZLCalculator implements CalculatorInterface{
	private static final Logger logger = Logger.getLogger(PDZFSWZLCalculator.class);
	@Autowired
	private SystemService systemService;
	@Autowired
	private ResultCodeTableDistinctor resultCodeTableDistinctor;
	
	private static final String calculateSql = 
			SqlReader.readSql("/com/kd/business/sql/calculate/calculatePDZFSWZLCode.sql");
	
	@Override
	public boolean isCalInScore() {
		return true;
	}
	
	@Override
	public String getName() {
		return "配电站房数完整率";
	}
	@Override
	public void calculate(Date date) {
		logger.info("计算配电站房数完整率相关指标开始");
		calculateDayTable(date);
		calculateMonthTable(date);
		logger.info("计算配电站房数完整率相关指标结束");
	}

	@Override
	public void calculateMonthTable(Date date) {
		logger.info("计算配电站房数完整率相关月指标开始");
		String monthTableName = DateUtil.getResultMonthTableName(date);
		
		CalculateCodeUtil.insertIntoMonthIndexNoUnevalu(systemService, "dmssts", date);
		CalculateCodeUtil.insertIntoMonthIndexNoUnevalu(systemService, "pmssts", date);
		resultCodeTableDistinctor.distinctCodeTableByCodes(monthTableName, new String[] {"dmssts", "pmssts"});
		
		CalculateCodeUtil.insertIntoMonthIndex2(systemService, "substionfine",  date);
		resultCodeTableDistinctor.distinctCodeTableByCode(monthTableName, "substionfine");
		
		logger.info("计算配电站房数完整率相关月指标结束");
	}

	@Override
	public void calculateDayTable(Date date) {
		logger.info("计算配电站房数完整率相关日指标开始");
		String dayTableName = DateUtil.getResultDayTableName(date);
		String statistime = DateUtil.dateToStr(date);
		
		String insertdmsstsSql = calculateSql.split(";")[0]
				.replace("${dateStr}", statistime)
				.replace("${TABLENAME}", dayTableName);
		String insertpmsstsSql = calculateSql.split(";")[1]
				.replace("${dateStr}", statistime)
				.replace("${TABLENAME}", dayTableName);
		
		
		//插入默认数据
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "dmssts", "个数", statistime, "无", "无");
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "pmssts", "个数", statistime, "无", "无");	
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "substionfine", "数值", statistime, "无", "无");
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "substionfine", "百分比", statistime, "无", "无");
			
		systemService.executeSql(insertdmsstsSql);
		systemService.executeSql(insertpmsstsSql);
		resultCodeTableDistinctor.distinctCodeTableByCodes(dayTableName, new String[] {"dmssts", "pmssts"});
		
		//配电站房数完整率
		CalculateCodeUtil.insertIntoDayIndex(systemService, dayTableName, statistime, 
				"substionfine", "dmssts", "pmssts", "100", date);
		
		resultCodeTableDistinctor.distinctCodeTableByCode(dayTableName, "substionfine");
		logger.info("计算配电站房数完整率相关日指标结束");
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
		return "PDZFSWZLCalculator";
	}
	
}
