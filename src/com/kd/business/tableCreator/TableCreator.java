package com.kd.business.tableCreator;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kd.service.SystemService;
import com.kd.util.DateUtil;
import com.kd.util.SqlReader;
/**
 * 
 * All rights Reserved, Designed By 科东    
 * @Description: 创建结果表工具类 
 * @author EH.WANG
 * @date 2018年6月14日 上午9:35:58
 */
public class TableCreator {
	@Autowired
	private SystemService systemService;

	public final static String createTableSql = SqlReader.readSql("/com/kd/business/sql/createTable/createTable.sql");
	public final static String createTableIndexSql = SqlReader.readSql("/com/kd/business/sql/createTable/createTableIndex.sql");
	
	private static final Logger logger = Logger.getLogger(TableCreator.class);
	
	public void creatTable(Date date){
		createDayTable(date);
		createMonthTable(date);
	}
	/**
	 * 
	 * @Description: 结果表——日表，创建
	 * @param date
	 * @return void
	 *
	 */
	public void createDayTable(Date date) {
		String dayTableName = DateUtil.getResultDayTableName(date);
		if (!checkTableIsExist(dayTableName)) {
			logger.info("创建日表"+dayTableName);
			String dateString = DateUtil.DAY_DFM.format(new Date());
			String createDayTableSql = createTableSql.replace("${TABLENAME}", dayTableName);
			String createDayTableIndexSql = createTableIndexSql
												.replace("${DATESTRING}", dateString)
												.replace("${TABLENAME}", dayTableName);
			
			systemService.executeSql(createDayTableSql);
			systemService.executeSql(createDayTableIndexSql);
		}
	}
	
	/**
	 * 
	 * @Description: 结果表——日表，创建
	 * @param date
	 * @return void
	 *
	 */
	public void createMonthTable(Date date) {
		String monthTableName = DateUtil.getResultMonthTableName(date);
		
		if (!checkTableIsExist(monthTableName)) {
			logger.info("创建月表"+monthTableName);
			String dateString = DateUtil.MONTH_DFM.format(new Date());
			String createDayTableSql = createTableSql.replace("${TABLENAME}", monthTableName);
			String createDayTableIndexSql = createTableIndexSql
												.replace("${DATESTRING}", dateString)
												.replace("${TABLENAME}", monthTableName);
			
			systemService.executeSql(createDayTableSql);
			systemService.executeSql(createDayTableIndexSql);
		}
		
	}
	
	/**
     * {检查表是否存在}
     * @param tableName
     * @return
     * @author: zzl
     */
    public boolean checkTableIsExist(String tableName) {
    	tableName = tableName.replace("EVALUSYSTEM.RESULT.", "");
    	
    	String checkSql = "SELECT COUNT(*) FROM EVALUSYSTEM.SYSDBA.SYSTABLES "
    						+ "WHERE NAME = '"+getPureTableName(tableName)+"'";
    	return systemService.getCountForJdbc(checkSql) == 0?false:true;
    }
    
    private String getPureTableName(final String tableName){
    	String pureTableName = tableName.substring(tableName.lastIndexOf(".") + 1);
    	return pureTableName;
    }
	
}
