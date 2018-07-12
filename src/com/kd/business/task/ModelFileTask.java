package com.kd.business.task;

import java.io.File;  
import java.util.Properties;
 

import org.apache.log4j.Logger;


import com.kd.util.FileUtil;
import com.kd.util.PropertiesUtil;
	
public class ModelFileTask {  
	private static final Logger logger = Logger.getLogger(ModelFileTask.class);
	
	Properties properties = new PropertiesUtil("sysConfig.properties").getProperties();
	//测试  
    public  void zipFile() {  
        try {
        	logger.info("开始将图模文件放在检验程序下!");
        	String dmsZipFolderStr = properties.getProperty("dmsZipFolder");
        	String descDirModel = properties.getProperty("zipAssessFolder");
        	File dmsZIPFolder = new File(dmsZipFolderStr);
			for (File f : dmsZIPFolder.listFiles()) {				
				if (isMyFile(f.getName())) {
					  FileUtil.copyFile(f.getAbsolutePath(), descDirModel + File.separator + f.getName()); 
				}
			}
			logger.info("结束将图模文件放在检验程序下!");
             
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    } 
		
	    public boolean isMyFile(String fileName){
	    	boolean isMyFile = false;
			if (fileName != null && fileName.endsWith(".zip")) {
				isMyFile = true;
			}
			return isMyFile;
	    } 
	     
	  
} 
