package com.kd.business.task;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kd.business.calculator.Calculator;
import com.kd.business.detailInsertor.DeviceDetailInsertor;
import com.kd.business.tableCreator.TableCreator;
import com.kd.util.DateUtil;
public class CalculateTask {
	private static final Logger logger = Logger.getLogger(CalculateTask.class);
	@Autowired
	TableCreator tableCreator;
	@Autowired
	Calculator calculator;
	@Autowired
	DeviceDetailInsertor deviceDetailInsertor;
	
	public void calculate(){
		calculateIndex(DateUtil.getUserWantDate(-1));
	}
	public void calculateIndex(Date date){
		logger.info("开始计算考核指标");
		long startTime = System.currentTimeMillis();
		
		tableCreator.creatTable(date);
		
		calculator.calculate(date);
		
		//由于需要等待c语言的计算结果，最后计算首页一次设备指标
		deviceDetailInsertor.insertDetail(date);
		
		logger.info("今日考核指标计算完成，耗时 "+(System.currentTimeMillis() - startTime)+" 毫秒");
		
	}
 
}
