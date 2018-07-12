package com.kd.business.task;

import java.util.Properties;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.kd.service.SystemService;
import com.kd.util.FileUtil;
import com.kd.util.PropertiesUtil;

/**
 * 
 * All rights Reserved, Designed By 科东    
 * @Description: 清空DMS、PMS文件任务 
 * @author EH.WANG
 * @date 2018年6月22日 上午9:28:18
 */
public class DelDmsPmsAllFileTask {
	private static final Logger logger = Logger.getLogger(TruncatedataTask.class);
	@Autowired
	private SystemService systemService;
	Properties properties = new PropertiesUtil("sysConfig.properties").getProperties();
	public void delFile(){
		
		logger.info("清空DMS文件路径下的所有文件开始");
		String dmsXmlFolderStr = properties.getProperty("dmsXmlFolder");
		FileUtil.delAllFile(dmsXmlFolderStr);
		logger.info("清空DMS文件路径下的所有文件结束");
		
		logger.info("清空PMS文件路径下的所有文件开始");
		String pmsTransDirStr = properties.getProperty("pmsTransDir");
		FileUtil.delAllFile(pmsTransDirStr);
		String pmsCbDirStr = properties.getProperty("pmsCbDir");
		FileUtil.delAllFile(pmsCbDirStr);
		String pmsBusDirStr = properties.getProperty("pmsBusDir");
		FileUtil.delAllFile(pmsBusDirStr);
		String pmsSubsDirStr = properties.getProperty("pmsSubsDir");
		FileUtil.delAllFile(pmsSubsDirStr);
		logger.info("清空PMS文件路径下的所有文件结束");
			
		logger.info("DMS入库前清表开始");
		systemService.executeSql("TRUNCATE TABLE EVALUSYSTEM.DETAIL.DMSCB");
		systemService.executeSql("TRUNCATE TABLE EVALUSYSTEM.DETAIL.DMSBUS");
		systemService.executeSql("TRUNCATE TABLE EVALUSYSTEM.DETAIL.DMSST");
		systemService.executeSql("TRUNCATE TABLE EVALUSYSTEM.DETAIL.DMSLD");
		logger.info("DMS入库前清表结束");
		
	}
 
}

