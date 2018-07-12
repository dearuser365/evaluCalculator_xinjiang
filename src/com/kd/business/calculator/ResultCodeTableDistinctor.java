package com.kd.business.calculator;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.kd.service.SystemService;
import com.kd.util.SqlReader;

public class ResultCodeTableDistinctor {
	
	@Autowired
	private SystemService systemService;

	private final static String distinctResultCodeTableByCodeSql = 
			SqlReader.readSql("/com/kd/business/sql/distinct/distinctResultCodeTableByCode.sql");
	
	private final static String distinctResultCodeTableByParamsSql = 
			SqlReader.readSql("/com/kd/business/sql/distinct/distinctResultCodeTableByParams.sql");
	
	public void distinctCodeTableByCode(String tableName, String code) {
		String sql = distinctResultCodeTableByCodeSql
						.replace("${TABLENAME}", tableName)
						.replace("${CODE}", code);
		systemService.executeSql(sql);
	}
	
	public void distinctCodeTableByCodeList(String tableName, List<String> codeList) {
		String sql;
		for (String code:codeList) {
			sql = distinctResultCodeTableByCodeSql
					.replace("${TABLENAME}", tableName)
					.replace("${CODE}", code);
			systemService.executeSql(sql);
		}
	}
	public void distinctCodeTableByCodes(String tableName, String[] codes) {
		String sql;
		for (String code:codes) {
			sql = distinctResultCodeTableByCodeSql
					.replace("${TABLENAME}", tableName)
					.replace("${CODE}", code);
			systemService.executeSql(sql);
		}
	}
	
	public void distinctCodeTableByParams(String tableName, String[] codes,String datatype,String dimvalue,String dimname) {
		String sql;
		for (String code:codes) {
			sql = distinctResultCodeTableByParamsSql
					.replace("${TABLENAME}", tableName)
					.replace("${CODE}", code)
					.replace("${datatype}", datatype)
					.replace("${dimvalue}", dimvalue)
					.replace("${dimname}", dimname);
			systemService.executeSql(sql);
		}
	}
	
	public void distinctCodeTable(String tableName) {
		String sql = SqlReader.readSql("/com/kd/business/sql/distinct/distinctResultCodeTableAll.sql");
		sql = sql.replace("${TABLENAME}", tableName);
		List<Map<String, Object>> dataList = systemService.findForJdbc(sql);
		for (Map<String, Object> data : dataList) {
			String deleteSql = "delete from " + tableName;
			Object code = data.get("code");
			Object organCode = data.get("organCode");
			Object datatype = data.get("datatype");
			Object dimvalue = data.get("dimvalue");
			Object dimname = data.get("dimname");
			Object maxUpdateTime = data.get("maxUpdateTime");
			
			deleteSql += " where code='"+code+"' "; 
			deleteSql += "and organ_code='"+organCode+"' "; 
			
			if (datatype != null) {
				deleteSql += "and datatype='"+datatype+"' ";
			} else {
				deleteSql += "and datatype is null ";
			}
			
			if (dimvalue != null) {
				deleteSql += "and dimvalue='"+dimvalue+"' ";
			} else {
				deleteSql += "and dimvalue is null ";
			}
			
			if (dimname != null) {
				deleteSql += "and dimname='"+dimname+"' ";
			} else {
				deleteSql += "and dimname is null ";
			}
			
			deleteSql += "and '"+maxUpdateTime+"' > updateTime"; 
			systemService.executeSql(deleteSql);
		}
	}
	
}
