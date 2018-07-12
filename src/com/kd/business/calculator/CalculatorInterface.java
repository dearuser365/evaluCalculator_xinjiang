package com.kd.business.calculator;

import java.util.Date;

public interface CalculatorInterface {
	public String getName();
	//是否计入总分
	public boolean isCalInScore();
	//是否免考核
	public boolean isUnevalu();
	public void calculate(Date date);
	public void calculateDayTable(Date date);
	public void calculateMonthTable(Date date);
	/**
	 * 计算地市免考核
	 * @param date
	 */
	public void calculateDsUnevalu(Date date);
	
	/***
	 * 调用存储过程
	 * @param date
	 */
	public void callSql(Date date);
	
	
	public void updateFlag(Date date);
	
	public String getCalculatorName();
}
