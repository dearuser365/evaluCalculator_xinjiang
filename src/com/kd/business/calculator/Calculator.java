package com.kd.business.calculator;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.kd.business.unevalu.UnevaluCalculator;
import com.kd.util.DateUtil;

public class Calculator implements ApplicationContextAware {
	
	private static final Logger logger = 
			Logger.getLogger(Calculator.class);
	
	private ApplicationContext applicationContext;
	
	@Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
	
	public void calculate(Date date) {
		Map<String, CalculatorInterface> calculateMap = applicationContext.getBeansOfType(CalculatorInterface.class);

		DistrictCalculator districtCalculator 
			= applicationContext.getBean(DistrictCalculator.class);
		ResultCodeTableDistinctor resultCodeTableDistinctor
			= applicationContext.getBean(ResultCodeTableDistinctor.class);
		UnevaluCalculator unevaluCalculator
			= applicationContext.getBean(UnevaluCalculator.class);
		
		logger.info("指标值计算开始");
		long startTime = System.currentTimeMillis();
		
		//多种日指标计算
		//对detail数据统计计算得到日表结果
		CalculatorInterface ci = null;
		for(Map.Entry<String, CalculatorInterface> entry:calculateMap.entrySet()){ 
			ci = entry.getValue();
			if (! (ci instanceof ScoreCalculator)) {
				if(ci.isCalInScore()){
					ci.callSql(date);
					ci.calculateDayTable(date);
				}
			}
		} 
		
		//设备平均完整率计算
		applicationContext.getBean(DevCalculator.class).calculate(date);
		
		//对相关结果进行免考核置标志位
		unevaluCalculator.setAgreeFlag(DateUtil.DAY_DFM.format(date));
		
		//多种月指标计算
		for(Map.Entry<String, CalculatorInterface> entry:calculateMap.entrySet()){ 
			ci = entry.getValue();
			if (! (ci instanceof ScoreCalculator)) {
				if(ci.isCalInScore()){
					ci.calculateMonthTable(date);
				}
			}
		} 
		
		//结合权重计算得分
		applicationContext.getBean(ScoreCalculator.class).calculate(date);
		
		//计算地区指标
		districtCalculator.calculate(date);
		
		//最后一次去重
		resultCodeTableDistinctor.distinctCodeTable(DateUtil.getResultDayTableName(date));
		resultCodeTableDistinctor.distinctCodeTable(DateUtil.getResultMonthTableName(date));
		
		
		logger.info("指标值计算完成，耗时 "+(System.currentTimeMillis() - startTime)+" 毫秒");
	}
	
}
