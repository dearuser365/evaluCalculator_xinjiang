package com.kd.business.unevalu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.kd.business.calculator.CalculateCodeUtil;
import com.kd.business.calculator.CalculatorInterface;
import com.kd.business.calculator.DistrictCalculator;
import com.kd.business.calculator.ResultCodeTableDistinctor;
import com.kd.business.calculator.ScoreCalculator;
import com.kd.service.SystemService;
import com.kd.util.DateUtil;

public class UnevaluCalculator implements ApplicationContextAware{
	private static final Logger logger = Logger.getLogger(UnevaluCalculator.class);
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private ResultCodeTableDistinctor resultCodeTableDistinctor;
	
	private ApplicationContext applicationContext;
	
	@Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
	public static void main(String[] args) {
		new UnevaluCalculator().calculateUnevalu();
	}
	//计算当天同意的免考核,每天23点开始计算
	public void calculateUnevalu(){
		logger.info("免考核每日计算开始");
		//每天重新计算前20天的免考核
/*		final Date currentDate = DateUtil.getUserWantDate(-1);
		Thread calCurDetailThread = new Thread(new Runnable() {
			public void run() {
				calculateUnevaluByMonth(currentDate);
			}
		});
		
		calCurDetailThread.start();
		
		try {
			calCurDetailThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		logger.info("免考核每日计算结束");
	}
	
	public void calculateUnevaluByMonth(final Date date) {
		Map<String, CalculatorInterface> calculateMap = applicationContext.getBeansOfType(CalculatorInterface.class);

		final DistrictCalculator districtCalculator 
			= applicationContext.getBean(DistrictCalculator.class);
		ResultCodeTableDistinctor resultCodeTableDistinctor
			= applicationContext.getBean(ResultCodeTableDistinctor.class);
		SystemService service 
				= applicationContext.getBean(SystemService.class);
		
		String name = "计算免考核" + DateUtil.MONTH_DFM.format(date);

		logger.info("开始"+name);
		long startTime = System.currentTimeMillis();

		//重算日指标：包含（拓扑正确率，研判正确率 ）,此外还包含flag置1
		//calculatDayUnevalu(service, districtCalculator, date);
		
		final List<Date> dateList = new ArrayList<Date>();
		Date tempDate = null;
		
		for (int i = 0; i < 20; i++) {
			tempDate = DateUtil.getUserWantDate(i-20);
			dateList.add(tempDate);
		}
		
		//地市免考核驳回date
		final List<Date> dateList2 = new ArrayList<Date>();
		for(Date datt : dateList){
			String selectsql = "select count(*) as count from EVALUSYSTEM.\"PUBLIC\".UNEVALU "
					+ "where applicant_type = 'dsuneval' and valid = 0 "
					+ "and unevalu_date = '"+DateUtil.dateToStr(datt)+"'";
			Map<String,Object> map = service.findOneForJdbc(selectsql);
			if(map != null && map.containsKey("COUNT")){
				int count = Integer.valueOf(String.valueOf(map.get("COUNT")));
				if(count > 0){
					dateList2.add(datt);
				}
			}
		}
		
		//地市免考核date
		final List<Date> dateList3 = new ArrayList<Date>();
		for(Date datt : dateList){
			String selectsql = "select count(*) as count from EVALUSYSTEM.\"PUBLIC\".UNEVALU "
					+ "where applicant_type = 'dsuneval' and valid = 2 "
					+ "and unevalu_date = '"+DateUtil.dateToStr(datt)+"'";
			
			Map<String,Object> map = service.findOneForJdbc(selectsql);
			if(map != null && map.containsKey("COUNT")){
				int count = Integer.valueOf(String.valueOf(map.get("COUNT")));
				if(count > 0){
					dateList3.add(datt);
				}
			}
		}
		
		//获取需要计算地市免考核的code
		final List<String> codeList = new ArrayList<String>();
		for(Date datt : dateList3){
			String selectCodeAql = "select *  from EVALUSYSTEM.\"PUBLIC\".UNEVALU "
					+ "where applicant_type = 'dsuneval' and valid = 2 "
					+ "and unevalu_date = '"+DateUtil.dateToStr(datt)+"'";
			
			List<Map<String,Object>> list = service.findForJdbc(selectCodeAql);
			if(!list.isEmpty() && list.size()>0){
				for(Map<String,Object> map : list){
					if(map != null && map.containsKey("UNEVALU_CODE")){
						String code  = String.valueOf(map.get("UNEVALU_CODE"));
						codeList.add(code);
					}
				}
			}
		}
		
		//获取需要计算地市免考核驳回的code
		final List<String> codeList2 = new ArrayList<String>();
		for(Date datt : dateList2){
			String selectCodeAql = "select *  from EVALUSYSTEM.\"PUBLIC\".UNEVALU "
					+ "where applicant_type = 'dsuneval' and valid = 0 "
					+ " and unevalu_code !='_ypRight' and unevalu_code !='_toporight' and unevalu_code !='dlypScore'"
					+ " and unevalu_date = '"+DateUtil.dateToStr(datt)+"'";
			
			List<Map<String,Object>> list = service.findForJdbc(selectCodeAql);
			if(!list.isEmpty() && list.size()>0){
				for(Map<String,Object> map : list){
					if(map != null && map.containsKey("UNEVALU_CODE")){
						String code  = String.valueOf(map.get("UNEVALU_CODE"));
						codeList2.add(code);
					}
				}
			}
		}
		
		final Map<String, String> calculatorMap =  getCalculatorMap();
		
		//多种指标计算
		List<Thread> threadList = new ArrayList<Thread>();
		for(final Map.Entry<String, CalculatorInterface> entry:calculateMap.entrySet()){ 
			final CalculatorInterface ci = entry.getValue();
			if (ci.isCalInScore()) {
				Thread calculateThread = new Thread(new Runnable() {
					public void run() {
						for(Date datee : dateList2){
							for(String code : codeList2){
								if(calculatorMap.containsKey(code) && calculatorMap.get(code).equals(ci.getCalculatorName())){
									logger.info("计算"+ci+"的日指标"+datee);
									
									ci.calculateDayTable(datee);

									ci.updateFlag(datee);
									setAgreeFlag(DateUtil.DAY_DFM.format(datee));
									//计算日地区指标
									districtCalculator.calculateDate(datee);
									//重新算日分
									applicationContext.getBean(ScoreCalculator.class).calculateDayTable(datee);
								}
							}
						}
						for(Date datee : dateList3){
							for(String code : codeList){
								if(calculatorMap.containsKey(code) && calculatorMap.get(code).equals(ci.getCalculatorName())){
									logger.info("计算"+code+"的地市免考核日指标"+datee);
									ci.calculateDsUnevalu(datee);
									//计算日地区指标
									districtCalculator.calculateDate(datee);
									//重新算日分
									applicationContext.getBean(ScoreCalculator.class).calculateDayTable(datee);
								}
							}
						}
						logger.info("计算"+ci+"的月指标"+date);
						ci.calculateMonthTable(date);
						
						Date newdate =  DateUtil.getUserWantDate(-20);
						Calendar c=Calendar.getInstance();
						c.setTime(newdate);
						int month1=c.get(Calendar.MONTH);
						c.setTime(date);
						int month2=c.get(Calendar.MONTH);
						if(month1!=month2){
							ci.calculateMonthTable(newdate);
						}
					}
				});
				calculateThread.start();
				threadList.add(calculateThread);
			}
		}
		for (Thread thread : threadList) {
			try {
				thread.join();
			} catch (InterruptedException e) {}
		}
		
		//重新算月分
		applicationContext.getBean(ScoreCalculator.class).calculateMonthTable(date);
		
		//计算地区指标
		districtCalculator.calculateMonth(date);
		
		//最后一次去重
		resultCodeTableDistinctor.distinctCodeTable(DateUtil.getResultMonthTableName(date));
		
		logger.info(name+"完成, 耗时"+(System.currentTimeMillis() - startTime)+" 毫秒");
	}
	
	//手动置flag位
	public void setFlag(String dateStr, 
			String organCode, String code, String kxIds, String ypIds, String flag) throws ParseException {
		
		SystemService service 
			= applicationContext.getBean(SystemService.class);
		
		String tableName = DateUtil.getResultDayTableName(DateUtil.DAY_DFM.parse(dateStr));
		if (CalculateCodeUtil.checkTableIsExist(service, tableName)) {
			String updateSql = null;
			
			updateSql = "update "+tableName+" set "+
					"flag = ?,updateTime = sysdate "+
					"where organ_code=? and code = ?";
			Map<String, String[]> codeMap = getUnevaluFlagMap();
			for (String ucode : codeMap.get(code)) {
				service.executeSql(updateSql, flag, organCode, ucode);
			}
		}
		
		if ("1".equals(flag)) {//插入手动免考核表
			service.executeSql("delete from EVALUSYSTEM.\"PUBLIC\".MANUAL_UNEVALU "+
					"where UNEVALU_DATE=? and ORGAN_CODE=? and UNEVALU_CODE=?", 
					DateUtil.DAY_DFM.parse(dateStr), organCode, code);
			service.executeSql("INSERT INTO EVALUSYSTEM.\"PUBLIC\".MANUAL_UNEVALU("+
					"UNEVALU_DATE,ORGAN_CODE,UNEVALU_CODE,DV_ID,YP_ID) "+
					"VALUES(?, ?, ?, ?, ?)", 
					DateUtil.DAY_DFM.parse(dateStr), organCode, code, kxIds, ypIds);
		} else if ("0".equals(flag)) {//从手动免考核表中删除
			service.executeSql("delete from EVALUSYSTEM.\"PUBLIC\".MANUAL_UNEVALU "+
									"where UNEVALU_DATE=? and ORGAN_CODE=? and UNEVALU_CODE=?", 
									DateUtil.DAY_DFM.parse(dateStr), organCode, code);
		} 
	}
	
	//用于在重算后恢复计算后免考核flag=1
	public void setAgreeFlag(final String dateStr) {
		SystemService service = applicationContext.getBean(SystemService.class);
		try {
			final Date date = new SimpleDateFormat("yyyyMMdd").parse(dateStr);
			List<Map<String, Object>> unevaluList = getUnevaluAgreeListByDate(service, date);
			
			for (Map<String, Object> map : unevaluList) {
				String tableName = DateUtil.getResultDayTableName(date);
				String code = String.valueOf(map.get("UNEVALU_CODE"));
				String organCode = String.valueOf(map.get("ORGAN_CODE"));
//				String kxIds = map.get("DV_ID") == null?null:String.valueOf(map.get("DV_ID"));
//				String ypIds = map.get("YP_ID") == null?null:String.valueOf(map.get("YP_ID"));
				String flag = map.get("VALID") == null?"1":String.valueOf(map.get("VALID"));
				
				if (CalculateCodeUtil.checkTableIsExist(service, tableName)) {
					String updateSql = null;
/*					if (code.equals("_toporight") && kxIds != null && !kxIds.isEmpty()) {
						updateSql = "update "+tableName+" set "+
								"flag = ?,updateTime = sysdate "+
								"where organ_code=? and dimvalue = ? "+
								"and code='topogood'";
						if(kxIds.contains(" ")){
							kxIds = kxIds.replace(" ", "");
						}
						
						for (String kxId : kxIds.split(",")) {
							service.executeSql(updateSql, flag, organCode, kxId);
						}
					} else if(code.equals("_ypRight") && ypIds != null && !ypIds.isEmpty()) {
						updateSql = "update EVALUSYSTEM.RESULT.DMSJUDGEDUP set flag=? where organ_code=? and YPID=? and error_type in ('1','2','100','5')";
						if(ypIds.contains(" ")){
							ypIds = ypIds.replace(" ", "");
						}
						String updateSqlGpms = "update EVALUSYSTEM.RESULT.DMSJUDGEDUP set flag=? where organ_code=? and GPMSID=? and error_type in ('1','2','100','5')";
						for (String ypId : ypIds.split(",")) {
							if(ypId.length() == 36){
								service.executeSql(updateSqlGpms, flag, organCode, ypId);
							}else{
								service.executeSql(updateSql, flag, organCode, ypId);
							}
						}
					}else if(code.equals("dlypScore") && ypIds != null && !ypIds.isEmpty()) {
						updateSql = "update EVALUSYSTEM.RESULT.DMSJUDGEDUP set flag_1=? where organ_code=? and YPID=? and error_type in ('1','2','100','5')";
						if(ypIds.contains(" ")){
							ypIds = ypIds.replace(" ", "");
						}
						String updateSqlGpms = "update EVALUSYSTEM.RESULT.DMSJUDGEDUP set flag_1=? where organ_code=? and GPMSID=? and error_type in ('1','2','100','5')";
						for (String ypId : ypIds.split(",")) {
							if(ypId.length() == 36){
								service.executeSql(updateSqlGpms, flag, organCode, ypId);
							}else{
								service.executeSql(updateSql, flag, organCode, ypId);
							}
						}
					} else {
*/
						updateSql = "update "+tableName+" set "+
								"flag = ?,updateTime = sysdate "+
								"where organ_code=? and code = ?";
						Map<String, String[]> codeMap = getUnevaluFlagMap();
						String[] codes = codeMap.get(code);
						if (codes != null) {
							for (String ucode : codes) {
								service.executeSql(updateSql, flag, organCode, ucode);
							}
						} else {
							System.out.println("置位flag"+code);
							service.executeSql(updateSql, flag, organCode, code);
						}

/*						
					}
*/
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private Map<String, String[]> getUnevaluFlagMap() {
    	Map<String, String[]> unevaluFlagMap = new HashMap<String, String[]>();
    	unevaluFlagMap.put("_devfine", new String[]{"_devfine"});
    	unevaluFlagMap.put("_tzmatch", new String[]{"_tzmatch"});
    	unevaluFlagMap.put("_tzsuccess", new String[]{"_tzsuccess"});
    	unevaluFlagMap.put("_xsdcltrate", new String[]{"_xsdcltrate"});
    	unevaluFlagMap.put("_yddfine", new String[]{"_yddfine","_gdmsyddnum","_gpmsyddnum"});
    	unevaluFlagMap.put("_ypPublish", new String[]{"_ypPublish", "_ypPubInNum", "_ypPubNum"});
    	unevaluFlagMap.put("_ypRight", new String[]{"_ypRight", "_ypSucNum", "_ypTotalNum"});
    	unevaluFlagMap.put("_zlpprocessfine", new String[]{"_zlpprocessfine", "_dmszlpronum", "_gpmszlpnum"});
    	unevaluFlagMap.put("_toporight", new String[]{"_toporight"});
    	unevaluFlagMap.put("_rectification", new String[]{"_rectification"});
    	unevaluFlagMap.put("checkFile", new String[]{"checkFile"});
    	unevaluFlagMap.put("gwup", new String[]{"gwup"});
    	unevaluFlagMap.put("_ztgj", new String[]{"_ztgj","_ztgjError"});
    	unevaluFlagMap.put("topogoodup", new String[]{"topogoodup"});
    	unevaluFlagMap.put("devD16G16", new String[]{"devD16G16"});
    	unevaluFlagMap.put("zlpRight", new String[]{"zlpRight"});
    	unevaluFlagMap.put("dlypScore", new String[]{"dlypScore", "dlypRightNum", "dlypNum"});
    	unevaluFlagMap.put("gdmodel", new String[]{"gdmodel"});
    	
    	return unevaluFlagMap;
    }
	
	
	//通过的免考核
	private List<Map<String, Object>> getUnevaluAgreeListByDate(SystemService systemService, Date date) {
		String dateStr = DateUtil.dateToStr(date);
		String getUnevaluByDateSql = "select "+
										"UNEVALU_CODE, ORGAN_CODE,DV_ID,YP_ID,VALID "+
										"from EVALUSYSTEM.\"PUBLIC\".UNEVALU "+
										"where valid in ('1','2')  and unevalu_date = '"+dateStr+"'"+
									 "UNION ALL "+
									 "select "+
									 	"UNEVALU_CODE, ORGAN_CODE,DV_ID,YP_ID,1 as VALID "+
										"from EVALUSYSTEM.\"PUBLIC\".MANUAL_UNEVALU "+
										"where unevalu_date = '"+dateStr+"'";
		return systemService.findForJdbc(getUnevaluByDateSql);
	}
	
	
	/***
	 * 计算指标免考核
	 * @param date
	 * @param className
	 */
	public void calculateUnevaluByMonth(final Date date,String className) {
		Map<String, CalculatorInterface> calculateMap = applicationContext.getBeansOfType(CalculatorInterface.class);

		final DistrictCalculator districtCalculator 
			= applicationContext.getBean(DistrictCalculator.class);
		ResultCodeTableDistinctor resultCodeTableDistinctor
			= applicationContext.getBean(ResultCodeTableDistinctor.class);
		SystemService service 
				= applicationContext.getBean(SystemService.class);
		
		String name = "计算免考核" + DateUtil.MONTH_DFM.format(date);

		logger.info("开始"+name);
		long startTime = System.currentTimeMillis();

		//重算日指标：包含（拓扑正确率，研判正确率 ）,此外还包含flag置1
		//calculatDayUnevalu(service, districtCalculator, date,className);
		
		final List<Date> dateList = new ArrayList<Date>();
		
		Date tempDate = null;
		String tempDateStr = null;
		
		if (DateUtil.isCurrentMonth(date)) {
			int yesterdayDate = DateUtil.getYesterdayDate();
			for (int i = 1; i <= yesterdayDate; i++) {
				tempDate = DateUtil.getUserWantDate(0-i);
				dateList.add(tempDate);
			}
		} else {
			String getTableNamesSql = "SELECT substr(name, 5 ,8) as dateStr "+ 
	    			"FROM EVALUSYSTEM.SYSDBA.SYSTABLES "+
	    			"where name like 'DAY_"+DateUtil.MONTH_DFM.format(date)+"%'";
			List<Map<String, Object>> tableNamesMapList = service.findForJdbc(getTableNamesSql);
			for (int i = 0;i < tableNamesMapList.size(); i++) {
	    		Map<String, Object> nameMap = tableNamesMapList.get(i);
	    		tempDateStr = String.valueOf(nameMap.get("DATESTR"));
	    		try {
	    			tempDate = DateUtil.DAY_DFM.parse(tempDateStr);
					dateList.add(tempDate);
	    		} catch(Exception e) {
	    			e.printStackTrace();
	    		}
	    		
	    	}
		}
		
		/**
		 * 计算地市免考核驳回date
		 */
		final List<Date> dateList2 = new ArrayList<Date>();
		for(Date datt : dateList){
			String selectsql = "select count(*) as count from EVALUSYSTEM.\"PUBLIC\".UNEVALU "
					+ "where applicant_type = 'dsuneval' and valid = 0 "
					+ "and unevalu_date = '"+DateUtil.dateToStr(datt)+"'";
			Map<String,Object> map = service.findOneForJdbc(selectsql);
			if(map != null && map.containsKey("COUNT")){
				int count = Integer.valueOf(String.valueOf(map.get("COUNT")));
				if(count > 0){
					dateList2.add(datt);
				}
			}
		}
		final Map<String, String> calculatorMap =  getCalculatorMap();
		
		Map<String, String> calculatorMap2 =  getCalculatorMap2();
		String selectCode = calculatorMap2.get(className);
		
		
		/**
		 * 计算地市免考核date
		 */
		final List<Date> dateList3 = new ArrayList<Date>();
		for(Date datt : dateList){
			String selectsql = "select count(*) as count from EVALUSYSTEM.\"PUBLIC\".UNEVALU "
					+ "where applicant_type = 'dsuneval' and valid = 2 "
					+ "and unevalu_date = '"+DateUtil.dateToStr(datt)+"'";
			
			if(selectCode != null && !"".equals(selectCode) && !"null".equals(selectCode)){
				selectsql += " and UNEVALU_CODE = '"+selectCode+"' ";
			}
			Map<String,Object> map = service.findOneForJdbc(selectsql);
			if(map != null && map.containsKey("COUNT")){
				int count = Integer.valueOf(String.valueOf(map.get("COUNT")));
				if(count > 0){
					dateList3.add(datt);
				}
			}
		}
		
		//获取需要计算地市免考核的code
		final List<String> codeList = new ArrayList<String>();
		for(Date datt : dateList3){
			String selectCodeAql = "select *  from EVALUSYSTEM.\"PUBLIC\".UNEVALU "
					+ "where applicant_type = 'dsuneval' and valid = 2 "
					+ "and unevalu_date = '"+DateUtil.dateToStr(datt)+"'";
			
			List<Map<String,Object>> list = service.findForJdbc(selectCodeAql);
			if(!list.isEmpty() && list.size()>0){
				for(Map<String,Object> map : list){
					if(map != null && map.containsKey("UNEVALU_CODE")){
						String code  = String.valueOf(map.get("UNEVALU_CODE"));
						if(!codeList.contains(code)){
							codeList.add(code);
						}
					}
				}
			}
		}
		
		//获取需要计算免考核驳回的code
		final List<String> codeList2 = new ArrayList<String>();
		for(Date datt : dateList2){
			String selectCodeAql = "select *  from EVALUSYSTEM.\"PUBLIC\".UNEVALU "
					+ "where applicant_type = 'dsuneval' and valid = 0 "
					+ " and unevalu_code !='_ypRight' and unevalu_code !='_toporight' and unevalu_code !='dlypScore'"
					+ " and unevalu_date = '"+DateUtil.dateToStr(datt)+"'";
			
			List<Map<String,Object>> list = service.findForJdbc(selectCodeAql);
			if(!list.isEmpty() && list.size()>0){
				for(Map<String,Object> map : list){
					if(map != null && map.containsKey("UNEVALU_CODE")){
						String code  = String.valueOf(map.get("UNEVALU_CODE"));
						if(!codeList2.contains(code)){
							codeList2.add(code);
						}
					}
				}
			}
		}
		
		
		
		
		//月指标
		//多种指标计算
		//全部重算
		if("-1".equals(className)){
			logger.info("全部指标重新计算");
			List<Thread> threadList = new ArrayList<Thread>();
			for(final Map.Entry<String, CalculatorInterface> entry:calculateMap.entrySet()){ 
				final CalculatorInterface ci = entry.getValue();
				if (ci.isCalInScore()) {
					Thread calculateThread = new Thread(new Runnable() {
						public void run() {
							for(Date datee : dateList2){
								for(String code : codeList2){
									if(calculatorMap.containsKey(code) && calculatorMap.get(code).equals(ci.getCalculatorName())){
										logger.info("计算"+ci+"的日指标"+datee);
										
										ci.calculateDayTable(datee);
										
										ci.updateFlag(datee);
										setAgreeFlag(DateUtil.DAY_DFM.format(datee));
										//计算日地区指标
										districtCalculator.calculateDate(datee);
										//重新算日分
										applicationContext.getBean(ScoreCalculator.class).calculateDayTable(datee);
									}
								}
							}
							for(Date datee : dateList3){
								for(String code : codeList){
									if(calculatorMap.containsKey(code) && calculatorMap.get(code).equals(ci.getCalculatorName())){
										logger.info("计算"+code+"的地市免考核日指标"+datee);
										ci.calculateDsUnevalu(datee);
										//计算日地区指标
										districtCalculator.calculateDate(datee);
										//重新算日分
										applicationContext.getBean(ScoreCalculator.class).calculateDayTable(datee);
									}
								}
							}
							logger.info("计算"+ci+"的月日指标");
							ci.calculateMonthTable(date);
						}
					});
					calculateThread.start();
					threadList.add(calculateThread);
				}
			}
			for (Thread thread : threadList) {
				try {
					thread.join();
				} catch (InterruptedException e) {}
			}
			
		//单个指标重算
		}else{
			logger.info("单个指标重新计算" + className);
			for(final Map.Entry<String, CalculatorInterface> entry:calculateMap.entrySet()){ 
				final CalculatorInterface ci = entry.getValue();
				if (ci.isUnevalu()) {
					if(className.equals(ci.getClass().getSimpleName())){
						for(Date datee : dateList2){
							for(String code : codeList2){
								if(calculatorMap.containsKey(code) && calculatorMap.get(code).equals(ci.getCalculatorName())){
									logger.info("计算"+ci+"的日指标"+datee);
									
									ci.calculateDayTable(datee);
								
									ci.updateFlag(datee);
									setAgreeFlag(DateUtil.DAY_DFM.format(datee));
									//计算日地区指标
									districtCalculator.calculateDate(datee);
									//重新算日分
									applicationContext.getBean(ScoreCalculator.class).calculateDayTable(datee);
								}
							}
						}
						for(Date datee : dateList3){
							for(String code : codeList){
								if(calculatorMap.containsKey(code) && calculatorMap.get(code).equals(ci.getCalculatorName())){
									logger.info("计算"+code+"的地市免考核日指标"+datee);
									ci.calculateDsUnevalu(datee);
									//计算日地区指标
									districtCalculator.calculateDate(datee);
									//重新算日分
									applicationContext.getBean(ScoreCalculator.class).calculateDayTable(datee);
								}
							}
						}
						logger.info("计算"+ci+"的月指标");
						ci.calculateMonthTable(date);
						break;
					}
				}
			}
		}
		
		//计算地区指标
		districtCalculator.calculateMonth(date);
		
		//重新算月分
		applicationContext.getBean(ScoreCalculator.class).calculateMonthTable(date);
		
		//最后一次去重
		resultCodeTableDistinctor.distinctCodeTable(DateUtil.getResultMonthTableName(date));
		
		logger.info(name+"完成, 耗时"+(System.currentTimeMillis() - startTime)+" 毫秒");
	}
	
	private static Map<String, String> getCalculatorMap() {
    	Map<String, String> calculatorMap = new HashMap<String, String>();
    	calculatorMap.put("_ztgj", "ZtgjCalculator");
    	calculatorMap.put("_zlpprocessfine", "ZlpCalculator");
    	calculatorMap.put("_ypPublish", "YPFBCalculator");
//    	calculatorMap.put("_ypRight", "YPCalculator");
    	calculatorMap.put("_yddfine", "YDCalculator");
    	calculatorMap.put("_xsdcltrate", "XsdCalculator");
    	calculatorMap.put("_rectification", "TwoWeeksCorrectiveCalculator");
//    	calculatorMap.put("_toporight", "TopoRightCalculator");
    	calculatorMap.put("gwup", "GwZbCalculator");
    	calculatorMap.put("_tzmatch", "DmsYcCjCalculator");
    	calculatorMap.put("_devfine", "DevCalculator");
    	calculatorMap.put("checkFile", "CheckFileCalculator");
    	calculatorMap.put("zlpRight", "ZlpRightCalculator");
    	calculatorMap.put("dlypScore", "DlypScoreCalculator");
    	calculatorMap.put("gdmodel", "GdModelCalculator");
    	return calculatorMap;
    }
	
	private static Map<String, String> getCalculatorMap2() {
    	Map<String, String> calculatorMap = new HashMap<String, String>();
    	calculatorMap.put("ZtgjCalculator","_ztgj" );
    	calculatorMap.put( "ZlpCalculator","_zlpprocessfine");
    	calculatorMap.put("YPFBCalculator","_ypPublish" );
    	calculatorMap.put("YDCalculator","_yddfine" );
    	calculatorMap.put("XsdCalculator","_xsdcltrate" );
    	calculatorMap.put("TwoWeeksCorrectiveCalculator","_rectification" );
    	calculatorMap.put("GwZbCalculator","gwup" );
    	calculatorMap.put("DmsYcCjCalculator","_tzmatch" );
    	calculatorMap.put("DevCalculator","_devfine" );
    	calculatorMap.put("CheckFileCalculator","checkFile");
    	calculatorMap.put("ZlpRightCalculator", "zlpRight");
    	calculatorMap.put("DlypScoreCalculator", "dlypScore");
    	calculatorMap.put("GdModelCalculator", "gdmodel");
    	return calculatorMap;
    }
	
}
