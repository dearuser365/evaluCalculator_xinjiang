package com.kd.business.parser.pms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import com.kd.business.loader.CbStateLoader;
import com.kd.business.loader.CbdscLoader;
import com.kd.business.loader.OrganLoader;
import com.kd.entity.CbState;
import com.kd.entity.Cbdsc;
import com.kd.entity.Organ;
import com.kd.service.SystemService;
import com.kd.util.DateUtil;
import com.kd.util.FileParseUtilTool;
import com.kd.util.FileUtil;
import com.kd.util.PropertiesUtil;
import com.kd.util.SqlReader;

public class PmsCbFileInsertor {
	@Autowired
	private SystemService systemService;
	@Autowired
	private OrganLoader organLoader;
	@Autowired
	private CbdscLoader cbdscLoader;
	@Autowired
	private CbStateLoader cbStateLoader;

	private static final int THREAD_NUM = 20;
	
	private static final Logger logger = Logger.getLogger(PmsCbFileInsertor.class);
	
	Map<String, Organ> pmsNameMap = null;
	Map<String, CbState> cbStateMap = null;
	Map<String, Cbdsc> cbdscMap = null;

	public PmsCbFileInsertor(){}
	
	//解析开关和刀闸入库
	public void insertToDb(Date date) {
		pmsNameMap = organLoader.getPmsNameMap();
		cbStateMap = cbStateLoader.getCbStateMap();
		cbdscMap = cbdscLoader.getCbdscMap();
		
		Properties properties = new PropertiesUtil("sysConfig.properties").getProperties();
		
		String currentDateStr = DateUtil.dateToStr(new Date());
		String currentTimeStr = DateUtil.dateToStrLong(date);
		
		String cbFolderStr = properties.getProperty("pmsCbDir");
		String bakFolderStr = FileParseUtilTool.getPmsFtpBakFolderStr(properties, cbFolderStr, currentDateStr);
		
		File cbFolder = new File(cbFolderStr);
		File[] cbFiles = cbFolder.listFiles();

		logger.info("PMS开关刀闸文件入库开始");
		
		if (cbFiles != null) {
			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(THREAD_NUM);
			for (File f : cbFiles) {
				fixedThreadPool.execute(new Thread(new PmsCbThreadInsertor(this, f, currentTimeStr, bakFolderStr)));
			}
			fixedThreadPool.shutdown();
			try {
				fixedThreadPool.awaitTermination(90, TimeUnit.MINUTES);
				logger.info("PMS开关刀闸文件入库结束");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		//去重复
		removeDuplication();
	}
		
	private void insertCb(File f, String dateStr, String bakFolderStr) {
		logger.info("存储文件"+f.getAbsolutePath());
		try {
			String tempFileName = f.getName();
			if (tempFileName.endsWith("007.xml") 
					|| tempFileName.endsWith("008.xml")) {
				//增加
				String tempCodeName = tempFileName.split("@")[1].split("_")[0];
				String organCode = pmsNameMap.get(tempCodeName).getCode();
				List<String> dataList = parseFile(f);
				List<String> errorDataList = new ArrayList<String>();
				List<String> sqlList = new ArrayList<String>();
				for (String data : dataList) {
					data = data.trim();
					if (data != null && !data.isEmpty()) {
						boolean success = insertDataStrToDb(data, organCode, dateStr,sqlList);
						if (!success) {
							errorDataList.add(data);
						}
					}
				}
				if (!errorDataList.isEmpty()) {
					logger.error("文件中存在不规范数据,如下:");
					for(String errorData : errorDataList) {
						logger.error(errorData);
					}
					throw new Exception("文件中存在不规范数据");
				}
				String[] sqlArrays = sqlList.toArray(new String[sqlList.size()]);
				systemService.pushSqls(sqlArrays);
			}else if (tempFileName.endsWith("009.xml")){
				//删除
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = builder.parse(f);
				XPath xpath = XPathFactory.newInstance().newXPath();
				String delStr = (String) xpath.evaluate("//Message/Payload/Subject/Description/text()", doc,
						XPathConstants.STRING);
				removeDataByIdListStr(delStr);
			}
			// 拷贝文件到bak文件夹
			FileUtil.moveFile(f.getAbsolutePath(), bakFolderStr + f.getName());
			logger.info("保存备份文件：" + bakFolderStr + f.getName());
		} catch (Exception e) {
			String errorFileStr = bakFolderStr 
					+File.separator + "error" 
					+File.separator + f.getName();
			FileUtil.moveFile(f.getAbsolutePath(), errorFileStr);
			logger.error("文件解析错误，保存error文件:" + errorFileStr, e);
		}
	}
	private boolean insertDataStrToDb(String data, String organCode, String dateStr,List<String> sqlList) {
		String[] datas = data.split(":");
		boolean success = false;
		String insertSql = "";
		try {
			String id = null;
			String name = null;
			String type = "0";
			String status = "10";
			String psr_typeNew = null;
			
//			List<Map<String,Object>> map=systemService.findForJdbc("SELECT * FROM EVALUSYSTEM.CONFIG.CBDSC");
			
			if (datas[0] != null) {
				id = datas[0];
				Cbdsc cbdsc = null;
				psr_typeNew = datas[0].substring(0,datas[0].lastIndexOf("_"));
				cbdsc = cbdscMap.get(psr_typeNew);
				type = cbdsc.getType();						
				if (id.length() >= 38) {
					cbdsc = cbdscMap.get(id.substring(32, 38));
				}
				if (cbdsc != null) {
					 type = cbdsc.getType();
				}
				if (datas.length > 1 && datas[1] != null) {
					name = datas[1].replace("[", "").replace("]", "");
					if (name.length() > 45) {
						//为了规避数据库字符串截断
						name = name.substring(0, 45);
					}
				}
				if (datas.length > 2 && datas[2] != null) {
					CbState cbstate = cbStateMap.get(datas[2]);
					
					if (cbstate != null) {
						status = cbstate.getNum();
						if ("41".equals(status)){
							status = "1";						
						}
					}
				}
				String sql = "INSERT INTO EVALUSYSTEM.DETAIL.PMSCB(ORGAN_CODE, CB, NAME, TYPE, GTIME, STATUS) values("+organCode+",'"+id+"','"+name+"',"+type+",'"+dateStr+"',"+status+")";
				sqlList.add(sql);
				
				
				
				success = true;
			} else {
				success = false;
			}
			
		} catch(Exception e) {
			logger.error(insertSql, e);
			success = false;
		}
		
		return success;
	}
	private List<String> parseFile(File f){
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		String str = "";
		List<String> dataList = new ArrayList<String>();
		try {
			boolean parseStart = false;
			fis = new FileInputStream(f);
			isr = new InputStreamReader(fis, "UTF-8");
			br = new BufferedReader(isr);
			while ((str = br.readLine()) != null) {
				str = str.trim();
				if (parseStart && !str.isEmpty() 
						&& !"</Description>".equals(str)) {
					dataList.add(str);
				}
				if ("<Description>".equals(str)) {
					parseStart = true;
				}
				if ("</Description>".equals(str)) {
					break;
				}
			}
		} catch (Exception e) {
			logger.error("解析文件失败", e);
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dataList;
	}
	
	private void removeDataByIdListStr(String removeIdListStr) {
		String[] idList = removeIdListStr.trim().split(";");
		for (String id : idList) {
			id = id.trim();
			if (!id.isEmpty()) {
				systemService.executeSql("delete from EVALUSYSTEM.DETAIL.PMSCB where CB='"+id+"'");
			}
		}
	}
	
	private void removeDuplication() {
		logger.info("PMS开关刀闸去重复数据开始");
		String sql = SqlReader.readSql("/com/kd/business/sql/distinct/distinctPmsCbDisTable.sql");
		systemService.executeSql(sql);
		logger.info("PMS开关刀闸去重复数据结束");
	}
	
	public class PmsCbThreadInsertor implements Runnable {
		private PmsCbFileInsertor insertor;
		private File f;
		private String dateStr;
		String bakFolder;
		
		public PmsCbThreadInsertor(PmsCbFileInsertor insertor, File f, String dateStr, String bakFolder) {
			this.insertor = insertor;
			this.f = f;
			this.dateStr = dateStr;
			this.bakFolder = bakFolder;
		}
		@Override
		public void run() {
			insertor.insertCb(f, dateStr, bakFolder);
		}
		
	}
}
