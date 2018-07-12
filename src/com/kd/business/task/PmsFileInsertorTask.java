package com.kd.business.task;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.kd.business.parser.pms.PmsBusFileInsertor;
import com.kd.business.parser.pms.PmsCbFileInsertor;
import com.kd.business.parser.pms.PmsSubsFileInsertor;
import com.kd.business.parser.pms.PmsTransFileInsertor;
import com.kd.service.SystemService;

/**
 * 
 * All rights Reserved, Designed By 科东    
 * @Description: PMS文件解析及入库任务 
 * @author EH.WANG
 * @date 2018年6月22日 上午9:26:11
 */
public class PmsFileInsertorTask implements ApplicationContextAware{
	
	private static final Logger logger = Logger.getLogger(PmsFileInsertorTask.class);
	
	private ApplicationContext applicationContext;
	@Autowired	
	private SystemService systemService;
	
	@Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
	public void parseAndInsertDBTime(){
		logger.info("PMS文件解析入库开始");
		parseAndInsertDB(new Date());
	}
	public void parseAndInsertDB(final Date date) {
		long startTime = System.currentTimeMillis();
		systemService.executeSql("TRUNCATE TABLE EVALUSYSTEM.DETAIL.PMSBUS");
		systemService.executeSql("TRUNCATE TABLE EVALUSYSTEM.DETAIL.PMSCB");
		systemService.executeSql("TRUNCATE TABLE EVALUSYSTEM.DETAIL.PMSST");
		systemService.executeSql("TRUNCATE TABLE EVALUSYSTEM.DETAIL.PMSLD");
		
		Thread pmsCbInsertorThread = new Thread(new Runnable(){
			@Override
			public void run() {
				PmsCbFileInsertor pmsCbFileInsertor 
				= applicationContext.getBean(PmsCbFileInsertor.class);
				pmsCbFileInsertor.insertToDb(date);
			}
		});
		
		Thread pmsBusInsertorThread = new Thread(new Runnable(){
			@Override
			public void run() {
				PmsBusFileInsertor pmsBusFileInsertor 
				= applicationContext.getBean(PmsBusFileInsertor.class);
				pmsBusFileInsertor.insertToDb(date);
			}
		});
	
		
		Thread pmsSubsInsertorThread = new Thread(new Runnable(){
			@Override
			public void run() {
				PmsSubsFileInsertor pmsSubsFileInsertor 
				= applicationContext.getBean(PmsSubsFileInsertor.class);
				pmsSubsFileInsertor.insertToDb(date);
			}
		});
		Thread pmsTransInsertorThread = new Thread(new Runnable(){
			@Override
			public void run() {
				PmsTransFileInsertor pmsTransFileInsertor 
				= applicationContext.getBean(PmsTransFileInsertor.class);
				pmsTransFileInsertor.insertToDb(date);
			}
		});
		
		pmsCbInsertorThread.start();
		pmsBusInsertorThread.start();
		pmsSubsInsertorThread.start();
		pmsTransInsertorThread.start();
		
		try {
			pmsCbInsertorThread.join();
			pmsBusInsertorThread.join();
			pmsSubsInsertorThread.join();
			pmsTransInsertorThread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("PMS文件解析入库完成，耗时 "+(System.currentTimeMillis() - startTime)+" 毫秒");
	}

}
