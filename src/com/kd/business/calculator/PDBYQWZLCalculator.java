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
 * @Description: 配电变压器完整率相关指标的计算 
 * @author EH.WANG
 * @date 2018年6月26日 下午4:35:23
 */
public class PDBYQWZLCalculator implements CalculatorInterface{
	private static final Logger logger = Logger.getLogger(PDBYQWZLCalculator.class);
	@Autowired
	private SystemService systemService;
	@Autowired
	private ResultCodeTableDistinctor resultCodeTableDistinctor;
	
	private static final String calculateSql = 
			SqlReader.readSql("/com/kd/business/sql/calculate/calculatePDBYQWZLCode.sql");
	
	@Override
	public boolean isCalInScore() {
		return true;
	}
	
	@Override
	public String getName() {
		return "配电变压器完整率";
	}
	@Override
	public void calculate(Date date) {
		logger.info("计算配电变压器完整率相关指标开始");
		calculateDayTable(date);
		calculateMonthTable(date);
		logger.info("计算配电变压器完整率相关指标结束");
	}

	@Override
	public void calculateMonthTable(Date date) {
		logger.info("计算配电变压器完整率相关月指标开始");
		String monthTableName = DateUtil.getResultMonthTableName(date);
		
		CalculateCodeUtil.insertIntoMonthIndexNoUnevalu(systemService, "dmslds", date);
		CalculateCodeUtil.insertIntoMonthIndexNoUnevalu(systemService, "pmslds", date);
		resultCodeTableDistinctor.distinctCodeTableByCodes(monthTableName, new String[] {"dmslds", "pmslds"});
		
		CalculateCodeUtil.insertIntoMonthIndex2(systemService,"ldfine", date);
		resultCodeTableDistinctor.distinctCodeTableByCode(monthTableName, "ldfine");
		
		logger.info("计算配电变压器完整率相关月指标结束");
	}

	@Override
	public void calculateDayTable(Date date) {
		logger.info("计算配电变压器完整率相关日指标开始");
		String dayTableName = DateUtil.getResultDayTableName(date);
		String statistime = DateUtil.dateToStr(date);
		
		String insertdmsldsSql = calculateSql.split(";")[0]
				.replace("${dateStr}", statistime)
				.replace("${TABLENAME}", dayTableName);
		String insertpmsldsSql = calculateSql.split(";")[1]
				.replace("${dateStr}", statistime)
				.replace("${TABLENAME}", dayTableName);
		
		
		//插入默认数据
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "dmslds", "个数", statistime, "无", "无");
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "pmslds", "个数", statistime, "无", "无");	
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "ldfine", "数值", statistime, "无", "无");
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "ldfine", "百分比", statistime, "无", "无");
			
		systemService.executeSql(insertdmsldsSql);
		systemService.executeSql(insertpmsldsSql);
		resultCodeTableDistinctor.distinctCodeTableByCodes(dayTableName, new String[] {"dmslds", "pmslds"});
		
		//配电变压器完整率
		CalculateCodeUtil.insertIntoDayIndex(systemService, dayTableName, statistime, 
				"ldfine", "dmslds", "pmslds", "100", date);
		
		resultCodeTableDistinctor.distinctCodeTableByCode(dayTableName, "ldfine");
		logger.info("计算配电变压器完整率相关日指标结束");
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
		return "PDBYQWZLCalculator";
	}
	
}
