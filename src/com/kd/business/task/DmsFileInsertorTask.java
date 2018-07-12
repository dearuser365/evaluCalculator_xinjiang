package com.kd.business.task;
import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.kd.business.parser.dms.DmsBusFileInsertor;
import com.kd.business.parser.dms.DmsCbFileInsertor;
import com.kd.business.parser.dms.DmsDisFileInsertor;
import com.kd.business.parser.dms.DmsSubsFileInsertor;
import com.kd.business.parser.dms.DmsTransFileInsertor;
import com.kd.business.parser.dms.DmsFileInsertorInterface;
import com.kd.business.tableCreator.TableCreator;
import com.kd.service.SystemService;
import com.kd.util.DateUtil;
import com.kd.util.FileParseUtilTool;
import com.kd.util.FileUtil;
import com.kd.util.PropertiesUtil;

/**
 * 
 * All rights Reserved, Designed By 科东    
 * @Description: DMS文件入库定时任务 
 * @author EH.WANG
 * @date 2018年6月14日 下午1:18:56
 */
public class DmsFileInsertorTask implements ApplicationContextAware{
	
	private static final Logger logger = Logger.getLogger(DmsFileInsertorTask.class);
	
	@Autowired
	TableCreator tableCreator;
	@Autowired
	SystemService systemService;
	
	Properties properties = new PropertiesUtil("sysConfig.properties").getProperties();
	
	private ApplicationContext applicationContext; 
	
	@Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
	public void parseAndInsertDB() {
			long startTime = System.currentTimeMillis();
			logger.info("DMS文件解析入库开始");
			
			Map<String, DmsFileInsertorInterface> map = applicationContext.getBeansOfType(DmsFileInsertorInterface.class);
			
			//ExecutorService fixedThreadPool = Executors.newFixedThreadPool(50);
			
			String currentDateStr = DateUtil.dateToStr(new Date());
			
			String dmsXmlFolderStr = properties.getProperty("dmsXmlFolder");		
			
			String bakFolderStr = FileParseUtilTool.getDmsFtpBakFolderStr(properties, currentDateStr);		
			
			File dmsXMLFolder = new File(dmsXmlFolderStr);
						
			for (File f : dmsXMLFolder.listFiles()) {
				tableCreator.creatTable(new Date());
				String errorFileStr = bakFolderStr+File.separator +"error"+File.separator + f.getName();
				DmsFileInsertorInterface tempInsertor = getInsertorByFile(map, f);
				//DMS文件解析入库
				if(tempInsertor instanceof DmsCbFileInsertor
						|| tempInsertor instanceof DmsBusFileInsertor					
						|| tempInsertor instanceof DmsDisFileInsertor
						|| tempInsertor instanceof DmsSubsFileInsertor
						|| tempInsertor instanceof DmsTransFileInsertor){
					if (tempInsertor != null) {
	//						fixedThreadPool.execute(new Thread(new DmsThreadInsertorRunnable(tempInsertor, f, bakFolderStr)));	
						new DmsThreadInsertor(tempInsertor, f, bakFolderStr).DmsThreadInsertorTask();
					}
				}else{					
					FileUtil.moveFile(f.getAbsolutePath(), errorFileStr);					
				}
			
			}
//			removeDuplication();
			

//			fixedThreadPool.shutdown();
			try {
//				fixedThreadPool.awaitTermination(180, TimeUnit.MINUTES);
				logger.info("DMS文件入库完成，耗时 "+(System.currentTimeMillis() - startTime)+" 毫秒");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
//			InterruptedException
		}
		private DmsFileInsertorInterface getInsertorByFile(Map<String, DmsFileInsertorInterface> map, File file) {
			DmsFileInsertorInterface tempInsertor;
			for(Map.Entry<String, DmsFileInsertorInterface> entry : map.entrySet()){ 
				tempInsertor = entry.getValue();
				if (tempInsertor.isMyFile(file.getName())){
					return tempInsertor;
				}
			} 
			return null;
		}
		
		public class DmsThreadInsertor {
			private DmsFileInsertorInterface insertor;
			private File f;
			String bakFolder;
			
			public DmsThreadInsertor(DmsFileInsertorInterface insertor, File f, String bakFolder) {
				this.insertor = insertor;
				this.f = f;
				this.bakFolder = bakFolder;
				
			}
			
			public void DmsThreadInsertorTask(){
			
				insertor.insertToDB(f, bakFolder);
			}
			
		}
		
		public class DmsThreadInsertorRunnable implements Runnable {
			private DmsFileInsertorInterface insertor;
			private File f;
			String bakFolder;
			
			public DmsThreadInsertorRunnable(DmsFileInsertorInterface insertor, File f, String bakFolder) {
				this.insertor = insertor;
				this.f = f;
				this.bakFolder = bakFolder;
				
			}
			@Override
			public void run() {
				insertor.insertToDB(f, bakFolder);
			}
			
		}
	
}
