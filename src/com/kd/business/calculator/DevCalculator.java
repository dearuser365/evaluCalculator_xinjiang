package com.kd.business.calculator;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kd.service.SystemService;
import com.kd.util.DateUtil;
import com.kd.util.SqlReader;

public class DevCalculator implements CalculatorInterface{
	private static final Logger logger = Logger.getLogger(DevCalculator.class);
	@Autowired
	private SystemService systemService;
	@Autowired
	private ResultCodeTableDistinctor resultCodeTableDistinctor;
	
	private static final String calculateSql = 
			SqlReader.readSql("/com/kd/business/sql/calculate/calculateDevCode.sql");
	
	@Override
	public boolean isCalInScore() {
		return true;
	}
	
	@Override
	public String getName() {
		return "设备平均完整率";
	}
	@Override
	public void calculate(Date date) {
		logger.info("计算设备完整率相关指标开始");
		calculateDayTable(date);
		calculateMonthTable(date);
		logger.info("计算设备完整率相关指标结束");
	}
	
	@Override
	public void calculateDayTable(Date date) {
		logger.info("计算设备完整率相关日指标开始");
		String dayTableName = DateUtil.getResultDayTableName(date);
		String statistime = DateUtil.dateToStr(date);
		
		String insertDevsqlSql = calculateSql.split(";")[0]
				.replace("${dateStr}", statistime)
				.replace("${TABLENAME}", dayTableName);
		
		//插入默认数据
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "_devfine", "数值", statistime, "无", "无");
		CalculateCodeUtil.insertDefaultValue(systemService, dayTableName, "_devfine", "百分比", statistime, "无", "无");
		
		systemService.executeSql(insertDevsqlSql);
		
		resultCodeTableDistinctor.distinctCodeTableByCode(dayTableName, "_devfine");
		
		logger.info("计算设备完整率相关日指标结束");	
	}
	
	@Override
	public void calculateMonthTable(Date date) {
		logger.info("计算设备完整率相关月指标开始");
		CalculateCodeUtil.insertIntoMonthIndex(systemService, "_devfine", date);
		resultCodeTableDistinctor.distinctCodeTableByCode(DateUtil.getResultMonthTableName(date), "_devfine");
		logger.info("计算设备完整率相关月指标结束");	
	}

	@Override
	public boolean isUnevalu() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void calculateDsUnevalu(Date date) {
		String dayTableName = DateUtil.getResultDayTableName(date);
		String statistime = DateUtil.dateToStr(date);
		//计算地市免考核
		CalculateCodeUtil.selectDsUnevalu(systemService, "_devfine", statistime, dayTableName);
		resultCodeTableDistinctor.distinctCodeTableByCode(dayTableName, "_devfine");
		CalculateCodeUtil.updateDayTableFlag(systemService, "_devfine", statistime, dayTableName);
	}

	@Override
	public void callSql(Date date) {
		// TODO Auto-generated method stub
		
		systemService.executeSql("call EVALUSYSTEM.DETAIL.CHECK_DMS_MODEL");
		systemService.executeSql("call EVALUSYSTEM.DETAIL.CHECK_GPMS_MODEL");
		
		//进行数据统计前，删除边界设备
		systemService.executeSql("call EVALUSYSTEM.DETAIL.DELETEBOUNDARYDAT");
		systemService.executeSql("call EVALUSYSTEM.RESULT.DELETEDATA('"+DateUtil.dateToStr(date)+"')");
		systemService.executeSql("call EVALUSYSTEM.RESULT.DGPMSPROCESS('"+DateUtil.dateToStr(date)+"')");
		
	}
	
	@Override
	public void updateFlag(Date date) {
		// TODO Auto-generated method stub
		
		String dayTableName = DateUtil.getResultDayTableName(date);
		String statistime = DateUtil.dateToStr(date);
		
		CalculateCodeUtil.updateDayTableFlag2(systemService, "_devfine", statistime, dayTableName);
	}

	@Override
	public String getCalculatorName() {
		// TODO Auto-generated method stub
		return "DevCalculator";
	}

}
