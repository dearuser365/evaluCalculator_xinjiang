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

public class PmsSubsFileInsertor {
	@Autowired	
	private SystemService systemService;
	@Autowired
	private OrganLoader organLoader;
	@Autowired
	private CbdscLoader cbdscLoader;
	@Autowired
	private CbStateLoader cbStateLoader;
	
	private static final int THREAD_NUM = 10;
	
	private static final Logger logger = Logger.getLogger(PmsSubsFileInsertor.class);
	Map<String, Organ> pmsNameMap = null;
	Map<String, CbState> cbStateMap = null;
	Map<String, Cbdsc> cbdscMap = null;

	public PmsSubsFileInsertor() {
	}

	// 厂站入库
		public void insertToDb(Date date) {
			pmsNameMap = organLoader.getPmsNameMap();
			cbStateMap = cbStateLoader.getCbStateMap();
			cbdscMap = cbdscLoader.getCbdscMap();

			Properties properties = new PropertiesUtil("sysConfig.properties").getProperties();
			
			String currentDateStr = DateUtil.dateToStr(new Date());
			String currentTimeStr = DateUtil.dateToStrLong(date);
			
			String subsFolderStr = properties.getProperty("pmsSubsDir");
			String bakFolderStr = FileParseUtilTool.getPmsFtpBakFolderStr(properties, subsFolderStr, currentDateStr);
			
			File subsFolder = new File(subsFolderStr);
			File[] subsFiles = subsFolder.listFiles();
			
			logger.info("PMS厂站文件入库开始");
			
			if (subsFiles != null) {
				ExecutorService fixedThreadPool = Executors.newFixedThreadPool(THREAD_NUM);
				for (File f : subsFiles) {
					fixedThreadPool.execute(new Thread(new PmsSubsThreadInsertor(this, f, currentTimeStr, bakFolderStr)));
//					new PmsSubsThreadInsertor(this, f, currentTimeStr, bakFolderStr).run();
				}
				fixedThreadPool.shutdown();
				try {
					fixedThreadPool.awaitTermination(60, TimeUnit.MINUTES);
					logger.info("PMS厂站文件入库结束");
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

			//去重复
			removeDuplication();
		}

		public void insertSubs(File f, String dateStr, String bakFolder) {
			logger.info("存储文件" + f.getAbsolutePath());
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
					NodeList addNodeListSubStation = (NodeList) xpath.evaluate("//RDF/Substation", doc,
							XPathConstants.NODESET);
					NodeList addNodeListEnergyConsumer = (NodeList) xpath.evaluate("//RDF/EnergyConsumer", doc,
							XPathConstants.NODESET);
					NodeList addNodeListUserPointGroup = (NodeList) xpath.evaluate("//RDF/UserPointGroup", doc,
							XPathConstants.NODESET);
					List<String> sqlList = new ArrayList<String>();
					
					insertDataStrToDb(addNodeListSubStation, dateStr, organCode, sqlList);
					insertDataStrToDb(addNodeListEnergyConsumer, dateStr, organCode,sqlList);
					insertDataStrToDb(addNodeListUserPointGroup, dateStr, organCode, sqlList);
					String[] sqlArrays = sqlList.toArray(new String[sqlList.size()]);
					systemService.pushSqls(sqlArrays);
					
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

		private void insertDataStrToDb(NodeList nodeList, String dateStr, String organCode,List<String> sqlList) {
			Node tempNode = null;
			
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node subsNode = nodeList.item(i);
				NodeList subsChildrenNode = subsNode.getChildNodes();
				String tempDevid = subsNode.getAttributes().getNamedItem("rdf:ID").getTextContent();
				String tempName = null;
				String  tempDv = null;
				//String insertSql = "";
				for (int j = 0; j < subsChildrenNode.getLength(); j++) {
					tempNode = subsChildrenNode.item(j);
					if ("cim:IdentifiedObject.name".equals(tempNode.getNodeName())) {
						tempName = tempNode.getTextContent();
					}
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
				
//				insertSql = "INSERT INTO EVALUSYSTEM.DETAIL.PMSST(DV, DEVID, NAME, GTIME, ORGAN_CODE) VALUES(?, ?, ?, ?, ?)";
//				systemService.executeSql(insertSql, tempDv, tempDevid, tempName, dateStr, organCode);
//				
				String sql = "INSERT INTO EVALUSYSTEM.DETAIL.PMSST(DV, DEVID, NAME, GTIME, ORGAN_CODE) VALUES('"+tempDv+"','"+tempDevid+"','"+tempName+"','"+ dateStr+"',"+organCode+")";
				sqlList.add(sql);
				
				tempName = null;
				tempDv = null;
				tempDevid = null;
			}
			
			
		}
		private void removeDataByIdListStr(String removeIdListStr) {
			String[] idList = removeIdListStr.trim().split(";");
			for (String id : idList) {
				id = id.trim();
				if (!id.isEmpty()) {
					systemService.executeSql("delete from EVALUSYSTEM.DETAIL.PMSST where DEVID='"+id+"'");
				}
			}
		}
	
	private void removeDuplication() {
		logger.info("PMS厂站去重复数据开始");
		String sql = SqlReader.readSql("/com/kd/business/sql/distinct/distinctPmsSubsTable.sql");
		systemService.executeSql(sql);
		logger.info("PMS厂站去重复数据结束");
	}
	
	public class PmsSubsThreadInsertor implements Runnable {
		private PmsSubsFileInsertor insertor;
		private File f;
		private String dateStr;
		String bakFolder;
		
		public PmsSubsThreadInsertor(PmsSubsFileInsertor insertor, File f, String dateStr, String bakFolder) {
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
