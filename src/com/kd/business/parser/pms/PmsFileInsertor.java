package com.kd.business.parser.pms;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class PmsFileInsertor implements ApplicationContextAware{
	
	private static final Logger logger = Logger.getLogger(PmsFileInsertor.class);
	
	private ApplicationContext applicationContext;
	
	@Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }	
	public void parseAndInsertDB(final Date date) {
		logger.info("PMS文件解析入库开始");
		long startTime = System.currentTimeMillis();
		
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
			pmsBusInsertorThread.join();
			pmsSubsInsertorThread.join();
			pmsTransInsertorThread.join();
			pmsCbInsertorThread.join();
			logger.info("PMS文件解析入库完成，耗时 "+(System.currentTimeMillis() - startTime)+" 毫秒");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
