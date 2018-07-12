package com.kd.business.parser.pms;

import java.io.File;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

public class PmsTransFileInsertor {
	@Autowired
	private SystemService systemService;
	@Autowired
	private OrganLoader organLoader;
	@Autowired
	private CbdscLoader cbdscLoader;
	@Autowired
	private CbStateLoader cbStateLoader;
	
	private static final int THREAD_NUM = 15;
	
	private static final Logger logger = Logger.getLogger(PmsTransFileInsertor.class);

	Map<String, Organ> pmsNameMap = null;
	Map<String, CbState> cbStateMap = null;
	Map<String, Cbdsc> cbdscMap = null;

	public PmsTransFileInsertor() {
	}

	// 配变入库
	public void insertToDb(Date date) {
		pmsNameMap = organLoader.getPmsNameMap();
		cbStateMap = cbStateLoader.getCbStateMap();
		cbdscMap = cbdscLoader.getCbdscMap();
		
		Properties properties = new PropertiesUtil("sysConfig.properties").getProperties();
		
		String currentDateStr = DateUtil.dateToStr(new Date());
		String currentTimeStr = DateUtil.dateToStrLong(date);
		
		String transFolderStr = properties.getProperty("pmsTransDir");
		String bakFolderStr = 
				FileParseUtilTool.getPmsFtpBakFolderStr(properties, transFolderStr, currentDateStr);
		
		File transFolder = new File(transFolderStr);
		File[] transFiles = transFolder.listFiles();
		
		logger.info("PMS配变文件入库开始");
		
		if (transFiles != null) {
			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(THREAD_NUM);
			for (File f : transFiles) {
				fixedThreadPool.execute(new Thread(new PmsTransThreadInsertor(this, f, currentTimeStr, bakFolderStr)));
			}
			fixedThreadPool.shutdown();
			try {
				fixedThreadPool.awaitTermination(90, TimeUnit.MINUTES);
				logger.info("PMS配变文件入库开始");
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		//去重复
		removeDuplication();
	}

	public void insertSubs(File f, String dateStr, String bakFolder) {
		try {
			String tempFileName = f.getName();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(f);
			XPath xpath = XPathFactory.newInstance().newXPath();
			if (tempFileName.endsWith("007.xml") 
					|| tempFileName.endsWith("008.xml")) {
				//增加
				String tempCodeName = tempFileName.split("@")[1].split("_")[0];
				String organCode = pmsNameMap.get(tempCodeName).getCode();
				NodeList addNodeListPowerTransformer = (NodeList) xpath.evaluate("//RDF/PowerTransformer", doc,
						XPathConstants.NODESET);
				
				insertDataStrToDb(addNodeListPowerTransformer, dateStr, organCode);
				
			} else if (tempFileName.endsWith("009.xml")){
				//删除
				String delStr = (String) xpath.evaluate("//Message/Payload/Subject/Description/text()", doc,
						XPathConstants.STRING);
				removeDataByIdListStr(delStr);
			}
			// 拷贝文件到bak文件夹
			FileUtil.moveFile(f.getAbsolutePath(), bakFolder + f.getName());
			logger.info("保存备份文件：" + bakFolder + f.getName());

		} catch (Exception e) {
			String errorFileStr = bakFolder 
					+File.separator + "error" 
					+File.separator + f.getName();
			FileUtil.moveFile(f.getAbsolutePath(), errorFileStr);
			logger.error("文件解析错误，保存error文件:" + errorFileStr, e);
		}
	}

	private void insertDataStrToDb(NodeList nodeList, String dateStr, String organCode) {
		Node tempNode = null;
		
		List<String> sqlList =new ArrayList<String>();
		
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node subsNode = nodeList.item(i);
			NodeList subsChildrenNode = subsNode.getChildNodes();
			String tempDevid = subsNode.getAttributes().getNamedItem("rdf:ID").getTextContent();
			String tempName = null;
			String  tempDv = null;
//			String insertSql = null;
			for (int j = 0; j < subsChildrenNode.getLength(); j++) {
				tempNode = subsChildrenNode.item(j);
				if ("cim:Naming.name".equals(tempNode.getNodeName())) {
					tempName = tempNode.getTextContent();
				}
				if ("cim:PowerSystemResource.Circuits".equals(tempNode.getNodeName())) {
					tempDv = tempNode.getAttributes().getNamedItem("rdf:resource").getTextContent();
					if (tempDv != null && tempDv.length() >= 1) {
						tempDv = tempDv.substring(1);
					}
				}
				
			}
			
			if (tempName != null && tempName.length() > 45) {
				tempName = tempName.substring(0, 45);
			}
			
//			insertSql = "INSERT INTO EVALUSYSTEM.DETAIL.PMSLD(DV, LD, NAME, GTIME, ORGAN_CODE) VALUES(?, ?, ?, ?, ?)";
//			systemService.executeSql(insertSql, tempDv, tempDevid, tempName, dateStr, organCode);
			
			String sql = "INSERT INTO EVALUSYSTEM.DETAIL.PMSLD(DV, LD, NAME, GTIME, ORGAN_CODE) VALUES('"+tempDv+"','"+tempDevid+"','"+tempName+"','"+ dateStr+"',"+organCode+")";
			sqlList.add(sql);
			
			tempName = null;
			tempDv = null;
			tempDevid = null;
		}
		
		String[] sqlArrays = sqlList.toArray(new String[sqlList.size()]);
		systemService.pushSqls(sqlArrays);
	}
	private void removeDataByIdListStr(String removeIdListStr) {
		String[] idList = removeIdListStr.trim().split(";");
		for (String id : idList) {
			id = id.trim();
			if (!id.isEmpty()) {
				systemService.executeSql("delete from EVALUSYSTEM.DETAIL.PMSLD where LD='"+id+"'");
			}
		}
	}

	private void removeDuplication() {
		logger.info("PMS配变去重复数据开始");
		String sql = SqlReader.readSql("/com/kd/business/sql/distinct/distinctPmsTransTable.sql");
		systemService.executeSql(sql);
		logger.info("PMS配变去重复数据结束");
	}
	
	public class PmsTransThreadInsertor implements Runnable {
		private PmsTransFileInsertor insertor;
		private File f;
		private String dateStr;
		String bakFolder;
		
		public PmsTransThreadInsertor(PmsTransFileInsertor insertor, File f, String dateStr, String bakFolder) {
			this.insertor = insertor;
			this.f = f;
			this.dateStr = dateStr;
			this.bakFolder = bakFolder;
		}
		@Override
		public void run() {
			insertor.insertSubs(f, dateStr, bakFolder);
		}
		
	}
}
