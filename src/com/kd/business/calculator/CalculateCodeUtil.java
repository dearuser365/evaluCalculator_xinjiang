package com.kd.business.calculator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.kd.service.SystemService;
import com.kd.util.DateUtil;
import com.kd.util.SqlReader;

public class CalculateCodeUtil {
	
	private static final Logger logger = Logger.getLogger(CalculateCodeUtil.class);
	
	private final static String calculateSql = 
			SqlReader.readSql("/com/kd/business/sql/calculate/dsUnevalu.sql");
	
	public static void insertIntoMonthNum(SystemService systemService, String code, Date date){
		String monthTableName = DateUtil.getResultMonthTableName(date);
		List<String> unionSqlList = new ArrayList<String>();
		String tempSql = null;
		String sql = "" + 
				"insert into " + monthTableName+ 
				"(code,organ_code,value,datatype,updatetime,statistime,dimname,dimvalue)"+
				"(${insertSql})";

		String insertNumSql = 
				"select code, organ_code, sum(value) as value, datatype, sysdate, max(statistime), "+ 
				"dimvalue, dimname "+
					"from (" + 
						"${codeTableUnionSql}" + 
					") data "+ 
				"where data.dimvalue='无' and data.dimname = '无' "+
				"group by code, organ_code, datatype, dimvalue, dimname";
		
		//如果不是当前月，就是当月之前的月份
		if (DateUtil.isCurrentMonth(date)) {
			int yesterdayDate = DateUtil.getYesterdayDate();
			for (int i = 1; i <= yesterdayDate; i++) {
				String dayTableName = "EVALUSYSTEM.RESULT.DAY_"+
						DateUtil.DAY_DFM.format(DateUtil.getUserWantDate(0-i));
				if (checkTableIsExist(systemService, dayTableName)) {
					tempSql = "select * from "+dayTableName+" where code='"+code+"' and flag = 0";
					unionSqlList.add(tempSql);
				}
			}
		} else {
			String getTableNamesSql = "SELECT 'EVALUSYSTEM.RESULT.'||name as name "+ 
	    			"FROM EVALUSYSTEM.SYSDBA.SYSTABLES "+
	    			"where name like 'DAY_"+DateUtil.MONTH_DFM.format(date)+"%'";
			List<Map<String, Object>> tableNamesMapList = systemService.findForJdbc(getTableNamesSql);
			for (int i = 0;i < tableNamesMapList.size(); i++) {
	    		Map<String, Object> nameMap = tableNamesMapList.get(i);
	    		String tableName = String.valueOf(nameMap.get("name"));
	    		tempSql = "select * from "+tableName+" where code='"+code+"' and flag = 0";
				unionSqlList.add(tempSql);
	    	}
		}
		String insetCodeSql = sql.replace("${insertSql}", 
				insertNumSql.replace("${codeTableUnionSql}", 
						StringUtils.join(unionSqlList.toArray(), " union all ")));
		
		systemService.executeSql(insetCodeSql);
	}
	
	//月指标=日指标的总和
	public static void insertIntoMonthNumFromParams(SystemService systemService, String code, Date date,String datatype){
		String monthTableName = DateUtil.getResultMonthTableName(date);
		List<String> unionSqlList = new ArrayList<String>();
		String tempSql = null;
		String sql = "" + 
				"insert into " + monthTableName+ 
				"(code,organ_code,value,datatype,updatetime,statistime,dimname,dimvalue)"+
				"(${insertSql})";

		String insertNumSql = 
				"select code, organ_code, sum(value) as value, datatype, sysdate, max(statistime), "+ 
				"dimvalue, dimname "+
					"from (" + 
						"${codeTableUnionSql}" + 
					") data "+ 
				"where data.dimvalue='无' and data.dimname = '无' "+
				"group by code, organ_code, datatype, dimvalue, dimname";
		
		//如果不是当前月，就是当月之前的月份
		if (DateUtil.isCurrentMonth(date)) {
			int yesterdayDate = DateUtil.getYesterdayDate();
			for (int i = 1; i <= yesterdayDate; i++) {
				String dayTableName = "EVALUSYSTEM.RESULT.DAY_"+
						DateUtil.DAY_DFM.format(DateUtil.getUserWantDate(0-i));
				if (checkTableIsExist(systemService, dayTableName)) {
					tempSql = "select * from "+dayTableName+" where code='"+code+"' and datatype = '"+datatype+"' and flag = 0";
					unionSqlList.add(tempSql);
				}
			}
		} else {
			String getTableNamesSql = "SELECT 'EVALUSYSTEM.RESULT.'||name as name "+ 
	    			"FROM EVALUSYSTEM.SYSDBA.SYSTABLES "+
	    			"where name like 'DAY_"+DateUtil.MONTH_DFM.format(date)+"%'";
			List<Map<String, Object>> tableNamesMapList = systemService.findForJdbc(getTableNamesSql);
			for (int i = 0;i < tableNamesMapList.size(); i++) {
	    		Map<String, Object> nameMap = tableNamesMapList.get(i);
	    		String tableName = String.valueOf(nameMap.get("name"));
	    		tempSql = "select * from "+tableName+" where code='"+code+"' and datatype = '"+datatype+"' and  flag = 0";
				unionSqlList.add(tempSql);
	    	}
		}
		String insetCodeSql = sql.replace("${insertSql}", 
				insertNumSql.replace("${codeTableUnionSql}", 
						StringUtils.join(unionSqlList.toArray(), " union all ")));
		
		systemService.executeSql(insetCodeSql);
	}
	
	//指标值=分子/分母
	public static void insertIntoDayIndex(SystemService systemService, String tableName, 
			String dateStr, String indexCode, String fzCode, String fmCode, String defaultValue, Date date){
		String statistime = DateUtil.dateToStr(date);
		String calculateIndexSql = SqlReader.readSql("/com/kd/business/sql/calculate/calculateIndexDay.sql");
		String insertSql = calculateIndexSql.split(";")[0]
								.replace("${defaultValue}", defaultValue)
								.replace("${dateStr}", statistime)
								.replace("${TABLENAME}", tableName)
								.replace("${dateStr}", dateStr)
								.replace("${indexCode}", indexCode)
								.replace("${fzCode}", fzCode)
								.replace("${fmCode}", fmCode);
		systemService.executeSql(insertSql);
	}
	//指标值=分子/分母（指标大于等于60分就给100分）
	public static void insertIntoZlpRightDayIndex(SystemService systemService, String tableName, 
			String dateStr, String indexCode, String fzCode, String fmCode, String defaultValue, Date date){
		String statistime = DateUtil.dateToStr(date);
		String calculateIndexSql = SqlReader.readSql("/com/kd/business/sql/calculate/calculateIndexDay.sql");
		String insertSql = calculateIndexSql.split(";")[4]
								.replace("${defaultValue}", defaultValue)
								.replace("${dateStr}", statistime)
								.replace("${TABLENAME}", tableName)
								.replace("${dateStr}", dateStr)
								.replace("${indexCode}", indexCode)
								.replace("${fzCode}", fzCode)
								.replace("${fmCode}", fmCode);
		systemService.executeSql(insertSql);
	}
	//指标值=分子/分母
	public static void insertIntoDayIndexWithXianDefault(SystemService systemService, String tableName, 
			String dateStr, String indexCode, String fzCode, String fmCode, String defaultValue, String xianDefaultValue, Date date){
		String statistime = DateUtil.dateToStr(date);
		String calculateIndexSql = SqlReader.readSql("/com/kd/business/sql/calculate/calculateIndexDay.sql");
		String insertSql = calculateIndexSql.split(";")[2]
								.replace("${defaultValue}", defaultValue)
								.replace("${defaultXianValue}", xianDefaultValue)
								.replace("${dateStr}", statistime)
								.replace("${TABLENAME}", tableName)
								.replace("${dateStr}", dateStr)
								.replace("${indexCode}", indexCode)
								.replace("${fzCode}", fzCode)
								.replace("${fmCode}", fmCode);
		systemService.executeSql(insertSql);
	}
	
	//指标值=分子/分母,分子分母已经插入
	public static void insertIntoMonthIndex(SystemService systemService, String tableName, 
			 String indexCode, String fzCode, String fmCode, String defaultValue, Date date){
		String calculateIndexSql = SqlReader.readSql("/com/kd/business/sql/calculate/calculateIndexMonth.sql");
		String statistime = DateUtil.dateToStr(date);
		String insertSql = calculateIndexSql.split(";")[0]
				.replace("${defaultValue}", defaultValue)
				.replace("${dateStr}", statistime)
				.replace("${TABLENAME}", tableName)
				.replace("${indexCode}", indexCode)
				.replace("${fzCode}", fzCode)
				.replace("${fmCode}", fmCode);
		systemService.executeSql(insertSql);
	}
	//专属故障转供应用率月指标计算
	public static void insertIntoMonthIndexGzzg(SystemService systemService, String tableName, 
			 String indexCode, String fzCode, String fmCode, Date date){
		String calculateIndexSql = SqlReader.readSql("/com/kd/business/sql/calculate/calculateIndexMonth.sql");
		String statistime = DateUtil.dateToStr(date);
		String insertSql = calculateIndexSql.split(";")[2]				
				.replace("${dateStr}", statistime)
				.replace("${TABLENAME}", tableName)
				.replace("${indexCode}", indexCode)
				.replace("${fzCode}", fzCode)
				.replace("${fmCode}", fmCode);
		systemService.executeSql(insertSql);
	}
	//指标值=1 - 分子/分母
	public static void insertIntoDayIndex2(SystemService systemService, String tableName, 
			String dateStr, String indexCode, String fzCode, String fmCode, String defaultValue, Date date){
		String calculateIndexSql = SqlReader.readSql("/com/kd/business/sql/calculate/calculateIndexDay.sql");
		String statistime = DateUtil.dateToStr(date);
		String insertSql = calculateIndexSql.split(";")[1]
				.replace("${defaultValue}", defaultValue)
				.replace("${dateStr}", statistime)
				.replace("${TABLENAME}", tableName)
				.replace("${dateStr}", dateStr)
				.replace("${indexCode}", indexCode)
				.replace("${fzCode}", fzCode)
				.replace("${fmCode}", fmCode);
		systemService.executeSql(insertSql);
	}
	//指标值=1 - 分子/分母（分子为0，默认100）
		public static void insertIntoDayIndex3(SystemService systemService, String tableName, 
				String dateStr, String indexCode, String fzCode, String fmCode, String defaultValue, Date date){
			String calculateIndexSql = SqlReader.readSql("/com/kd/business/sql/calculate/calculateIndexDay.sql");
			String statistime = DateUtil.dateToStr(date);
			String insertSql = calculateIndexSql.split(";")[3]
					.replace("${defaultValue}", defaultValue)
					.replace("${dateStr}", statistime)
					.replace("${TABLENAME}", tableName)
					.replace("${dateStr}", dateStr)
					.replace("${indexCode}", indexCode)
					.replace("${fzCode}", fzCode)
					.replace("${fmCode}", fmCode);
			systemService.executeSql(insertSql);
		}

	//指标值= 1 - 分子/分母,分子分母已经插入
	public static void insertIntoMonthIndex2(SystemService systemService, String tableName, 
			String indexCode, String fzCode, String fmCode, String defaultValue, Date date){
		
		String calculateIndexSql = SqlReader.readSql("/com/kd/business/sql/calculate/calculateIndexMonth.sql");
		String statistime = DateUtil.dateToStr(date);
		String insertSql = calculateIndexSql.split(";")[1]
				.replace("${defaultValue}", defaultValue)
				.replace("${dateStr}", statistime)
				.replace("${TABLENAME}", tableName)
				.replace("${indexCode}", indexCode)
				.replace("${fzCode}", fzCode)
				.replace("${fmCode}", fmCode);
		systemService.executeSql(insertSql);
	}
	
	//应用于日指标的平均值
	public static void insertIntoMonthIndex(SystemService systemService, String code, Date date){
		//月表
		String monthTableName = DateUtil.getResultMonthTableName(date);
		//查询是否全月免考核，查询flag个数
		String sql = "select count(*) as count from (${selectSql})";
		//flag个数
		Integer flag1Count = 0;
		//获取organ_codelist
		String getOrganMapList = "select * from EVALUSYSTEM.CONFIG.ORGAN";
		List<Map<String, Object>> organMapList = systemService.findForJdbc(getOrganMapList);
		
		String insertsql = "" + 
				"insert into " + monthTableName + 
				"(code,organ_code,value,datatype,updatetime,statistime,dimname,dimvalue)"+
				"(${insertSql})";

		String insertNumSql = 
				"select code, organ_code, avg(value) as value, datatype, sysdate, max(statistime), "+ 
				"dimname, dimvalue "+
					"from (" + 
						"${codeTableUnionSql}" + 
					") "+ 
				"group by code, organ_code, datatype, dimvalue, dimname";
		
		for(Map<String,Object> map : organMapList){
			String organ_code = String.valueOf(map.get("ORGAN_CODE"));
			//全月select查询语句list
			List<String> unionSqlList = new ArrayList<String>();
			//如果不是当前月，就是当月之前的月份
			if (DateUtil.isCurrentMonth(date)) {
				int yesterdayDate = DateUtil.getYesterdayDate();
				for (int i = 1; i <= yesterdayDate; i++) {
					//日表
					String dayTableName = "EVALUSYSTEM.RESULT.DAY_"+
							DateUtil.DAY_DFM.format(DateUtil.getUserWantDate(0-i));
					if (checkTableIsExist(systemService, dayTableName)) {
						unionSqlList.add("select * from "+dayTableName+" where code='"+code+
								"' and dimname='无' and datatype='数值' and dimvalue='无' and flag != 1 and organ_code='"
								+organ_code+"'");
						
						unionSqlList.add("select * from "+dayTableName+" where code='"+code+
								"' and dimname='无' and datatype='百分比' and dimvalue='无' and flag != 1 and organ_code='"
								+organ_code+"'");
					}
				}
				
			} else {
				String getTableNamesSql = "SELECT 'EVALUSYSTEM.RESULT.'||name as name "+ 
						"FROM EVALUSYSTEM.SYSDBA.SYSTABLES "+
						"where name like 'DAY_"+DateUtil.MONTH_DFM.format(date)+"%'";
				List<Map<String, Object>> tableNamesMapList = systemService.findForJdbc(getTableNamesSql);
				for (int i = 0;i < tableNamesMapList.size(); i++) {
					Map<String, Object> nameMap = tableNamesMapList.get(i);
					String tableName = String.valueOf(nameMap.get("name"));
					unionSqlList.add("select * from "+tableName+" where code='"+code+
							"' and dimname='无' and datatype='数值' and dimvalue='无' and flag != 1 and organ_code='"+
							organ_code+"'");
					
					unionSqlList.add("select * from "+tableName+" where code='"+code+
							"' and dimname='无' and datatype='百分比' and dimvalue='无' and flag != 1 and organ_code='"+
							organ_code+"'");
					
				}
			}
			String selectFlagCountSql = sql.replace("${selectSql}",						
					StringUtils.join(unionSqlList.toArray(), "\n union all \n"));
			
			Map<String,Object> countMap = systemService.findOneForJdbc(selectFlagCountSql,null);
			if(countMap.containsKey("COUNT")){
				String countStr = String.valueOf(countMap.get("COUNT"));
				flag1Count = Integer.valueOf(countStr);
			}
			
			if (flag1Count > 0) {
				String insetCodeSql = insertsql.replace("${insertSql}", 
						insertNumSql.replace("${codeTableUnionSql}", 
								StringUtils.join(unionSqlList.toArray(), "\n union all \n")));
//				logger.info(insetCodeSql);
				systemService.executeSql(insetCodeSql);
			} else if(flag1Count == 0){
				logger.info("code="+code+"&organ_code="+organ_code+"进行整月免考核");
				//全月免考核,直接设置月指标 为100
				String insertSql = "INSERT INTO "+ monthTableName+" "
						+ "(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE) ";
				insertSql += "(";
				insertSql += "select "
						+ " '"+code+"' as code,"
						+ " organ.organ_code,"
						+ " '100' as value,"
						+ " '数值' as datatype,"
						+ " sysdate as updatetime,"
						+ " '"+DateUtil.dateToStr(date)+"' as statistime,"
						+ " '无' as dimname,"
						+ "	'无' as dimvalue"
						+ " from EVALUSYSTEM.config.organ organ where organ_code = " + organ_code;
				
				insertSql += " UNION ALL ";
				
				insertSql += "select "
						+ " '"+code+"' as code,"
						+ " organ.organ_code,"
						+ " '1' as value,"
						+ " '百分比' as datatype,"
						+ " sysdate as updatetime,"
						+ " '"+DateUtil.dateToStr(date)+"' as statistime,"
						+ " '无' as dimname,"
						+ "	'无' as dimvalue"
						+ " from EVALUSYSTEM.config.organ organ where organ_code = " + organ_code;
				
				insertSql += ")";
				systemService.executeSql(insertSql);
			}
		}
	}
	
	//平均值(不计算在免考核内的分子分母数量)
	public static void insertIntoMonthIndexNoUnevalu(SystemService systemService, String code, Date date){
		//月表
		String monthTableName = DateUtil.getResultMonthTableName(date);
		//获取organ_codelist
		String getOrganMapList = "select * from EVALUSYSTEM.CONFIG.ORGAN";
		List<Map<String, Object>> organMapList = systemService.findForJdbc(getOrganMapList);
		
		String insertsql = "" + 
				"insert into " + monthTableName+ 
				"(code,organ_code,value,datatype,updatetime,statistime,dimname,dimvalue)"+
				"(${insertSql})";

		String insertNumSql = 
				"select code, organ_code, round(avg(value)) as value, datatype, sysdate, max(statistime), "+ 
				"dimname, dimvalue "+
					"from (" + 
						"${codeTableUnionSql}" + 
					") "+ 
				"group by code, organ_code, datatype, dimvalue, dimname";
		
		for(Map<String,Object> map : organMapList){
			String organ_code = String.valueOf(map.get("ORGAN_CODE"));
			//全月select查询语句list
			List<String> unionSqlList = new ArrayList<String>();
			//如果不是当前月，就是当月之前的月份
			if (DateUtil.isCurrentMonth(date)) {
				int yesterdayDate = DateUtil.getYesterdayDate();
				for (int i = 1; i <= yesterdayDate; i++) {
					//日表
					String dayTableName = "EVALUSYSTEM.RESULT.DAY_"+
							DateUtil.DAY_DFM.format(DateUtil.getUserWantDate(0-i));
					if (checkTableIsExist(systemService, dayTableName)) {
						unionSqlList.add("select * from "+dayTableName+" where code='"+code+
								"' and dimname='无' and datatype='个数' and dimvalue='无' and flag = 0 and organ_code='"
								+organ_code+"'");
					}
				}
				
			} else {
				String getTableNamesSql = "SELECT 'EVALUSYSTEM.RESULT.'||name as name "+ 
						"FROM EVALUSYSTEM.SYSDBA.SYSTABLES "+
						"where name like 'DAY_"+DateUtil.MONTH_DFM.format(date)+"%'";
				List<Map<String, Object>> tableNamesMapList = systemService.findForJdbc(getTableNamesSql);
				for (int i = 0;i < tableNamesMapList.size(); i++) {
					Map<String, Object> nameMap = tableNamesMapList.get(i);
					String tableName = String.valueOf(nameMap.get("name"));
					unionSqlList.add("select * from "+tableName+" where code='"+code+
							"' and dimname='无' and datatype='个数' and dimvalue='无' and flag = 0 and organ_code='"+
							organ_code+"'");
				}
			}
			
			String insetCodeSql = insertsql.replace("${insertSql}", 
					insertNumSql.replace("${codeTableUnionSql}", 
							StringUtils.join(unionSqlList.toArray(), "\n union all \n")));
			logger.info(insetCodeSql);
			systemService.executeSql(insetCodeSql);
		}
	}
	
	//平均值,向下取整(不计算在免考核内的分子分母数量)
	public static void insertIntoMonthIndexNoUnevaluFloor(SystemService systemService, String code, Date date){
		//月表
		String monthTableName = DateUtil.getResultMonthTableName(date);
		//获取organ_codelist
		String getOrganMapList = "select * from EVALUSYSTEM.CONFIG.ORGAN";
		List<Map<String, Object>> organMapList = systemService.findForJdbc(getOrganMapList);
		
		String insertsql = "" + 
				"insert into " + monthTableName+ 
				"(code,organ_code,value,datatype,updatetime,statistime,dimname,dimvalue)"+
				"(${insertSql})";

		String insertNumSql = 
				"select code, organ_code, floor(avg(value)) as value, datatype, sysdate, max(statistime), "+ 
				"dimname, dimvalue "+
					"from (" + 
						"${codeTableUnionSql}" + 
					") "+ 
				"group by code, organ_code, datatype, dimvalue, dimname";
		
		for(Map<String,Object> map : organMapList){
			String organ_code = String.valueOf(map.get("ORGAN_CODE"));
			//全月select查询语句list
			List<String> unionSqlList = new ArrayList<String>();
			//如果不是当前月，就是当月之前的月份
			if (DateUtil.isCurrentMonth(date)) {
				int yesterdayDate = DateUtil.getYesterdayDate();
				for (int i = 1; i <= yesterdayDate; i++) {
					//日表
					String dayTableName = "EVALUSYSTEM.RESULT.DAY_"+
							DateUtil.DAY_DFM.format(DateUtil.getUserWantDate(0-i));
					if (checkTableIsExist(systemService, dayTableName)) {
						unionSqlList.add("select * from "+dayTableName+" where code='"+code+
								"' and dimname='无' and datatype='个数' and dimvalue='无' and flag = 0 and organ_code='"
								+organ_code+"'");
					}
				}
				
			} else {
				String getTableNamesSql = "SELECT 'EVALUSYSTEM.RESULT.'||name as name "+ 
						"FROM EVALUSYSTEM.SYSDBA.SYSTABLES "+
						"where name like 'DAY_"+DateUtil.MONTH_DFM.format(date)+"%'";
				List<Map<String, Object>> tableNamesMapList = systemService.findForJdbc(getTableNamesSql);
				for (int i = 0;i < tableNamesMapList.size(); i++) {
					Map<String, Object> nameMap = tableNamesMapList.get(i);
					String tableName = String.valueOf(nameMap.get("name"));
					unionSqlList.add("select * from "+tableName+" where code='"+code+
							"' and dimname='无' and datatype='个数' and dimvalue='无' and flag = 0 and organ_code='"+
							organ_code+"'");
				}
			}
			
			String insetCodeSql = insertsql.replace("${insertSql}", 
					insertNumSql.replace("${codeTableUnionSql}", 
							StringUtils.join(unionSqlList.toArray(), "\n union all \n")));
//				logger.info(insetCodeSql);
			systemService.executeSql(insetCodeSql);
		}
	}
	
	//平均值(不计算在免考核内的分子分母数量)
		public static void insertIntoMonthIndexNoUnevalu2(SystemService systemService, String code, Date date){
			//月表
			String monthTableName = DateUtil.getResultMonthTableName(date);
			//获取organ_codelist
			String getOrganMapList = "select * from EVALUSYSTEM.CONFIG.ORGAN";
			List<Map<String, Object>> organMapList = systemService.findForJdbc(getOrganMapList);
			
			String insertsql = "" + 
					"insert into " + monthTableName+ 
					"(code,organ_code,value,datatype,updatetime,statistime,dimname,dimvalue)"+
					"(${insertSql})";

			String insertNumSql = 
					"select code, organ_code, round(avg(value)) as value, datatype, sysdate, max(statistime), "+ 
					"dimname, dimvalue "+
						"from (" + 
							"${codeTableUnionSql}" + 
						") "+ 
					"group by code, organ_code, datatype, dimvalue, dimname";
			
			for(Map<String,Object> map : organMapList){
				String organ_code = String.valueOf(map.get("ORGAN_CODE"));
				//全月select查询语句list
				List<String> unionSqlList = new ArrayList<String>();
				//如果不是当前月，就是当月之前的月份
				if (DateUtil.isCurrentMonth(date)) {
					int yesterdayDate = DateUtil.getYesterdayDate();
					for (int i = 1; i <= yesterdayDate; i++) {
						//日表
						String dayTableName = "EVALUSYSTEM.RESULT.DAY_"+
								DateUtil.DAY_DFM.format(DateUtil.getUserWantDate(0-i));
						if (checkTableIsExist(systemService, dayTableName)) {
							unionSqlList.add("select * from "+dayTableName+" where code='"+code+
									"' and dimname='无' and datatype='小时' and dimvalue='无' and flag = 0 and organ_code='"
									+organ_code+"'");
						}
					}
					
				} else {
					String getTableNamesSql = "SELECT 'EVALUSYSTEM.RESULT.'||name as name "+ 
							"FROM EVALUSYSTEM.SYSDBA.SYSTABLES "+
							"where name like 'DAY_"+DateUtil.MONTH_DFM.format(date)+"%'";
					List<Map<String, Object>> tableNamesMapList = systemService.findForJdbc(getTableNamesSql);
					for (int i = 0;i < tableNamesMapList.size(); i++) {
						Map<String, Object> nameMap = tableNamesMapList.get(i);
						String tableName = String.valueOf(nameMap.get("name"));
						unionSqlList.add("select * from "+tableName+" where code='"+code+
								"' and dimname='无' and datatype='小时' and dimvalue='无' and flag = 0 and organ_code='"+
								organ_code+"'");
					}
				}
				
				String insetCodeSql = insertsql.replace("${insertSql}", 
						insertNumSql.replace("${codeTableUnionSql}", 
								StringUtils.join(unionSqlList.toArray(), "\n union all \n")));
//				logger.info(insetCodeSql);
				systemService.executeSql(insetCodeSql);
			}
		}
	
	
	//月指标取日指标的平均值(不计算在免考核内的指标)
	public static void insertIntoMonthIndex2(SystemService systemService, String code, Date date){
		//月表
		String monthTableName = DateUtil.getResultMonthTableName(date);
		//获取organ_codelist
		String getOrganMapList = "select * from EVALUSYSTEM.CONFIG.ORGAN";
		List<Map<String, Object>> organMapList = systemService.findForJdbc(getOrganMapList);
		
		String insertsql = "" + 
				"insert into " + monthTableName+ 
				"(code,organ_code,value,datatype,updatetime,statistime,dimname,dimvalue)"+
				"(${insertSql})";

		String insertNumSql = 
				"select code, organ_code, round(avg(value),2) as value, datatype, sysdate, max(statistime), "+ 
				"dimname, dimvalue "+
					"from (" + 
						"${codeTableUnionSql}" + 
					") "+ 
				"group by code, organ_code, datatype, dimvalue, dimname";
		
		for(Map<String,Object> map : organMapList){
			String organ_code = String.valueOf(map.get("ORGAN_CODE"));
			//全月select查询语句list
			List<String> unionSqlList = new ArrayList<String>();
			//如果不是当前月，就是当月之前的月份
			if (DateUtil.isCurrentMonth(date)) {
				int yesterdayDate = DateUtil.getYesterdayDate();
				for (int i = 1; i <= yesterdayDate; i++) {
					//日表
					String dayTableName = "EVALUSYSTEM.RESULT.DAY_"+
							DateUtil.DAY_DFM.format(DateUtil.getUserWantDate(0-i));
					if (checkTableIsExist(systemService, dayTableName)) {
						unionSqlList.add("select * from "+dayTableName+" where code='"+code+
								"' and dimname='无' and datatype='数值' and dimvalue='无' and flag = 0 and organ_code='"
								+organ_code+"'");
						
						unionSqlList.add("select * from "+dayTableName+" where code='"+code+
								"' and dimname='无' and datatype='百分比' and dimvalue='无' and flag = 0 and organ_code='"
								+organ_code+"'");
					}
				}
				
			} else {
				String getTableNamesSql = "SELECT 'EVALUSYSTEM.RESULT.'||name as name "+ 
						"FROM EVALUSYSTEM.SYSDBA.SYSTABLES "+
						"where name like 'DAY_"+DateUtil.MONTH_DFM.format(date)+"%'";
				List<Map<String, Object>> tableNamesMapList = systemService.findForJdbc(getTableNamesSql);
				for (int i = 0;i < tableNamesMapList.size(); i++) {
					Map<String, Object> nameMap = tableNamesMapList.get(i);
					String tableName = String.valueOf(nameMap.get("name"));
					unionSqlList.add("select * from "+tableName+" where code='"+code+
							"' and dimname='无' and datatype='数值' and dimvalue='无' and flag = 0 and organ_code='"+
							organ_code+"'");
					
					unionSqlList.add("select * from "+tableName+" where code='"+code+
							"' and dimname='无' and datatype='百分比' and dimvalue='无' and flag = 0 and organ_code='"
							+organ_code+"'");
				}
			}
			
			String insetCodeSql = insertsql.replace("${insertSql}", 
					insertNumSql.replace("${codeTableUnionSql}", 
							StringUtils.join(unionSqlList.toArray(), "\n union all \n")));
//				logger.info(insetCodeSql);
			systemService.executeSql(insetCodeSql);
		}
	}
	
	public static boolean checkTableIsExist(SystemService systemService, String tableName) {
    	String checkSql = "SELECT COUNT(*) FROM EVALUSYSTEM.SYSDBA.SYSTABLES "
    						+ "WHERE NAME = '"+getPureTableName(tableName)+"'";
    	return systemService.getCountForJdbc(checkSql) == 0?false:true;
    }
	
	private static String getPureTableName(final String tableName){
    	String pureTableName = tableName.substring(tableName.lastIndexOf(".") + 1);
    	return pureTableName;
    }
	
	/**
	 * 向日表中插入默认数据
	 * @param systemService
	 * @param tablename
	 * @param code
	 * @param datatype
	 * @param statistime
	 * @param dimvalue
	 * @param dimname
	 * @return
	 */
	public static void insertDefaultValue(SystemService systemService,String tablename,
			String code,String datatype,String statistime,String dimvalue,String dimname){
		String insertSql = "INSERT INTO ${TABLENAME} "
				+ "(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)";
		
		insertSql += "select "
				+ " '${code}' as code,"
				+ " organ.organ_code,"
				+ " 0 as value,"
				+ " '${datatype}' as datatype,"
				+ " sysdate as updatetime,"
				+ " '${dateStr}' as statistime,"
				+ " '${dimname}' as dimname,"
				+ "	'${dimvalue}' as dimvalue,"
				+ " 0 as flag "
				+ " from EVALUSYSTEM.config.organ organ";	
		
		insertSql = insertSql.replace("${TABLENAME}", tablename).replace("${code}", code)
						.replace("${datatype}", datatype).replace("${dateStr}", statistime)
						.replace("${dimname}", dimname).replace("${dimvalue}", dimvalue);
		
		systemService.executeSql(insertSql);
	}
	
	/**
	 * 向日表中插入默认数据
	 * @param systemService
	 * @param tablename
	 * @param code
	 * @param datatype
	 * @param statistime
	 * @param dimvalue
	 * @param dimname
	 * @return
	 */
	public static void insertDefaultValue(SystemService systemService,String tablename,
			String code,String datatype,String statistime,String dimvalue,String dimname, String defaultValue){
		String insertSql = "INSERT INTO ${TABLENAME} "
				+ "(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)";
		
		insertSql += "select "
				+ " '${code}' as code,"
				+ " organ.organ_code,"
				+ " "+defaultValue+" as value,"
				+ " '${datatype}' as datatype,"
				+ " sysdate as updatetime,"
				+ " '${dateStr}' as statistime,"
				+ " '${dimname}' as dimname,"
				+ "	'${dimvalue}' as dimvalue,"
				+ " 0 as flag "
				+ " from EVALUSYSTEM.config.organ organ";	
		
		insertSql = insertSql.replace("${TABLENAME}", tablename).replace("${code}", code)
						.replace("${datatype}", datatype).replace("${dateStr}", statistime)
						.replace("${dimname}", dimname).replace("${dimvalue}", dimvalue);
		
		systemService.executeSql(insertSql);
	}
	
	/***
	 * 计算地市免考核
	 * 月指标=分子/分母
	 * @param systemService
	 * @param code 指标code
	 * @param fmCode 分母code
	 * @param fzCode 分子code
	 * @param statistime 时间
	 * @param dayTableName 日表
	 * @return
	 */
	public static void selectDsUnevalu(SystemService systemService, String code, String fmCode,
			String fzCode,String statistime,String dayTableName){
		
		List<Map<String,Object>> list = selectDsUnevaluList(systemService, code, statistime);
		
		List<String> notInOrganCodes = getNorInOrganCodes(list);
		
		StringBuffer notInOrganCode = parseToString(notInOrganCodes);
		
		for(Map<String,Object> map : list){
			int count = Integer.valueOf(String.valueOf(map.get("COUNT")));
			//有地市免考核
			if(count > 0){
				//获取organ_code
				String organ_code = String.valueOf(map.get("ORGAN_CODE"));
				logger.info(organ_code + "有地市免考核");
				//获取分母
				String insertFmSqlDS = calculateSql.split(";")[0]
						.replace("${dateStr}", statistime)
						.replace("${TABLENAME}", dayTableName)
						.replace("${organ_code}", organ_code)
						.replace("${fmCode}", fmCode)
						.replace("${code}", code)
						.replace("${organcodes}", notInOrganCode);
				logger.info("插入地市免考核分母");
//				logger.info(insertFmSqlDS);
				systemService.executeSql(insertFmSqlDS);
				//获取分子
				String insertFzSqlDS = calculateSql.split(";")[1]
						.replace("${dateStr}", statistime)
						.replace("${TABLENAME}", dayTableName)
						.replace("${organ_code}", organ_code)
						.replace("${fzCode}", fzCode)
						.replace("${code}", code)
						.replace("${organcodes}", notInOrganCode)
						.replace("${fmCode}", fmCode);
				logger.info("插入地市免考核分子");
//				logger.info(insertFzSqlDS);
				systemService.executeSql(insertFzSqlDS);
			}
		}
		
	}
	
	/****
	 * 地市免考核
	 * 月指标=日指标的平均值
	 * @param systemService
	 * @param code 指标code
	 * @param statistime 日期
	 * @param dayTableName 日表
	 */
	public static void selectDsUnevalu(SystemService systemService, String code, 
			String statistime,String dayTableName){
		
		List<Map<String,Object>> list = selectDsUnevaluList(systemService, code, statistime);
		
		List<String> notInOrganCodes = getNorInOrganCodes(list);
		
		StringBuffer notInOrganCode = parseToString(notInOrganCodes);
		
		for(Map<String,Object> map : list){
			int count = Integer.valueOf(String.valueOf(map.get("COUNT")));
			//有地市免考核
			if(count > 0){
				//获取organ_code
				String organ_code = String.valueOf(map.get("ORGAN_CODE"));
				
				String insertTopoRightSqlDS = calculateSql.split(";")[2]
						.replace("${dateStr}", statistime)
						.replace("${TABLENAME}", dayTableName)
						.replace("${organ_code}", organ_code)
						.replace("${code}", code)
						.replace("${organcodes}", notInOrganCode);
				
				systemService.executeSql(insertTopoRightSqlDS);
			}
		}
		
	}
	
	
	/***
	 * 获取地市免考核列表
	 * @param systemService
	 * @param code
	 * @param statistime
	 * @param dayTableName
	 * @return
	 */
	public static List<Map<String,Object>> selectDsUnevaluList(SystemService systemService, String code,String statistime){
		String sql = "select organ.organ_code as organ_code, decode(unevalu.count,null,0,unevalu.count) as count "+
				" from EVALUSYSTEM.config.organ organ "+
				" left join ("+
				"select count(*) as count,organ_code from EVALUSYSTEM.\"PUBLIC\".UNEVALU"+ 
				" where unevalu_date = '"+statistime+"' "+
				" and applicant_type = 'dsuneval' "+
				" and unevalu_code = '"+code+"' "+
				" and valid = 2 "+
				" group by organ_code "+
				") unevalu on organ.organ_code = unevalu.organ_code "+
				" where organ.parent_code is null";
		
		List<Map<String,Object>> list = systemService.findForJdbc(sql, null);
		return list;
	}
	
	
	/***
	 * 获取地市免考核的城市
	 * @param list
	 * @return
	 */
	public static List<String> getNorInOrganCodes(List<Map<String,Object>> list){
		
		//查询其他地市指标时候的not in 数组
		List<String> notInOrganCodes = new ArrayList<String>();
		
		for(Map<String,Object> map : list){
			int count = Integer.valueOf(String.valueOf(map.get("COUNT")));
			//有地市免考核
			if(count > 0){
				//获取organ_code
				String organ_code = String.valueOf(map.get("ORGAN_CODE"));
				notInOrganCodes.add(organ_code);
			}
		}
		return notInOrganCodes;
	}
	
	/***
	 * 将notin数组转换成字符串
	 * @param notInOrganCodes
	 * @return
	 */
	public static StringBuffer parseToString(List<String> notInOrganCodes){
		StringBuffer notInOrganCode = new StringBuffer();
		//将not in 数组转换成String字符串
		for(int i=0;i<notInOrganCodes.size();i++){
			if(i == notInOrganCodes.size()-1){
				notInOrganCode.append(notInOrganCodes.get(i));
			}else{
				notInOrganCode.append(notInOrganCodes.get(i) + ",");
			}
		}
		return notInOrganCode;
	}
	
	
	public static void updateDayTableFlag(SystemService systemService, String code,
			String statistime,String dayTableName){
		
		//通过免考核表查询地市免考核数据
		String getDsUnevaluSql = " select unevalu.valid,unevalu.organ_code "
				+ " from EVALUSYSTEM.config.organ organ "
				+ " left join EVALUSYSTEM.\"PUBLIC\".UNEVALU unevalu "
				+ " on organ.organ_code = unevalu.organ_code "
				+ " where unevalu.unevalu_code = '"+code+"' "
				+ " and unevalu.unevalu_date = '"+statistime+"' "
				+ " and unevalu.applicant_type = 'dsuneval' "
				+ " and unevalu.valid = '2' ";
		
		List<Map<String,Object>> list = systemService.findForJdbc(getDsUnevaluSql, null);
		
		for(Map<String,Object> map : list){
			String organ_code = String.valueOf(map.get("ORGAN_CODE"));
			String updateSql = "update  "+dayTableName
					+ " set flag = '2' "
					+ " where code = '"+code+"' "
					+ " and organ_code = '"+organ_code+"'"
					+ " and statistime = '"+statistime+"' "
					+ " and dimname = '无' "
					+ " and dimvalue = '无'";
			systemService.executeSql(updateSql);
		}
		
		
		
	}
	
	
	public static void updateDayTableFlag2(SystemService systemService, String code,
			String statistime,String dayTableName){
		
		//通过免考核表查询免考核数据
		String getDsUnevaluSql = " select unevalu.valid,unevalu.organ_code "
				+ " from EVALUSYSTEM.config.organ organ "
				+ " left join EVALUSYSTEM.\"PUBLIC\".UNEVALU unevalu "
				+ " on organ.organ_code = unevalu.organ_code "
				+ " where unevalu.unevalu_code = '"+code+"' "
				+ " and unevalu.unevalu_date = '"+statistime+"' "
				+ " and unevalu.applicant_type = 'uneval' "
				+ " and unevalu.valid = '1' ";
		
		List<Map<String,Object>> list = systemService.findForJdbc(getDsUnevaluSql, null);
		
		for(Map<String,Object> map : list){
			String organ_code = String.valueOf(map.get("ORGAN_CODE"));
			String updateSql = "update  "+dayTableName
					+ " set flag = '1' "
					+ " where code = '"+code+"' "
					+ " and organ_code = '"+organ_code+"'"
					+ " and statistime = '"+statistime+"' "
					+ " and dimname = '无' "
					+ " and dimvalue = '无'";
			systemService.executeSql(updateSql);
		}
		
		
		
	}
	
	public static boolean compareDate(Date date){
//		//日期格式为2017-08-03
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		//获取今天的时间
//		Date newdate = null;
//		String datestr = sdf.format(new Date());
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
//		try {
//			newdate = df.parse(datestr);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//		
//		//比较
//		if(date.getTime() == (newdate.getTime()- 24 * 60 * 60 * 1000)){
//			//是当天的
//			return true;
//		}
//		return false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String yesterdayStr = sdf.format(DateUtil.getUserWantDate(-1));
		String dateStr = sdf.format(date);
		
		return yesterdayStr.equals(dateStr);
		
	}
	
}
