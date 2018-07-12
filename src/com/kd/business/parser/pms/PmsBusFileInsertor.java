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

public class PmsBusFileInsertor {
	@Autowired
	private SystemService systemService;
	@Autowired
	private OrganLoader organLoader;
	@Autowired
	private CbdscLoader cbdscLoader;
	@Autowired
	private CbStateLoader cbStateLoader;
	
	private static final int THREAD_NUM = 10;
	
	private static final Logger logger = Logger.getLogger(PmsBusFileInsertor.class);
	Properties properties = new PropertiesUtil("sysConfig.properties").getProperties();

	Map<String, Organ> pmsNameMap = null;
	Map<String, CbState> cbStateMap = null;
	Map<String, Cbdsc> cbdscMap = null;

	public PmsBusFileInsertor() {
	}

	// 母线入库
	public void insertToDb(Date date) {
		pmsNameMap = organLoader.getPmsNameMap();
		cbStateMap = cbStateLoader.getCbStateMap();
		cbdscMap = cbdscLoader.getCbdscMap();

		String currentDateStr = DateUtil.dateToStr(new Date());
		String currentTimeStr = DateUtil.dateToStrLong(date);
		
		String busFolderStr = properties.getProperty("pmsBusDir");
		String bakFolderStr = FileParseUtilTool.getPmsFtpBakFolderStr(properties, busFolderStr, currentDateStr);
		
		File busFolder = new File(busFolderStr);
		File[] busFiles = busFolder.listFiles();
		
		logger.info("PMS母线文件入库开始");		
		if (busFiles != null) {
			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(THREAD_NUM);
			for (File f : busFiles) {
				fixedThreadPool.execute(new Thread(new PmsBusThreadInsertor(this, f, currentTimeStr, bakFolderStr)));
			}
			fixedThreadPool.shutdown();
			try {
				fixedThreadPool.awaitTermination(60, TimeUnit.MINUTES);
				logger.info("PMS母线文件入库结束");
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		//去重复
		removeDuplication();
	}

	public void insertBus(File f, String dateStr, String bakFolder) {
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
				String tempDv = null;
				String tempDevid = null;
				String tempName = null;

				NodeList addNodeList = (NodeList) xpath.evaluate("//RDF/BusbarSection", doc,
						XPathConstants.NODESET);
				Node tempNode = null;
				
				List<String> sqlList = new ArrayList<String>();
				for (int i = 0; i < addNodeList.getLength(); i++) {
					Node busNode = addNodeList.item(i);
					NodeList busChildrenNode = busNode.getChildNodes();
					//id
					tempDevid = busNode.getAttributes().getNamedItem("rdf:ID").getTextContent();
					
					for (int j = 0; j < busChildrenNode.getLength(); j++) {
						tempNode = busChildrenNode.item(j);
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
					
					if (tempName != null && tempName.length() > 50) {
						tempName = tempName.substring(0, 50);
					}
					
//					String sql = "INSERT INTO EVALUSYSTEM.DETAIL.PMSBUS(DV, DEVID, NAME, GTIME, ORGAN_CODE) "
//							+ "VALUES(?,?,?,?,?)";
//					
//					systemService.executeSql(sql, tempDv, tempDevid, tempName, dateStr, organCode);
					
					String sql = "INSERT INTO EVALUSYSTEM.DETAIL.PMSBUS(DV, DEVID, NAME, GTIME, ORGAN_CODE) values('"+tempDv+"','"+tempDevid+"','"+tempName+"','"+ dateStr+"',"+organCode+")";
					sqlList.add(sql);
				
					tempDv = null;
					tempDevid = null;
					tempName = null;
				}
				
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

	private void removeDataByIdListStr(String removeIdListStr) {
		String[] idList = removeIdListStr.trim().split(";");
		for (String id : idList) {
			id = id.trim();
			if (!id.isEmpty()) {
				systemService.executeSql("delete from EVALUSYSTEM.DETAIL.PMSBUS where DEVID='"+id+"'");
			}
		}
	}
	
	private void removeDuplication(){
		logger.info("PMS母线去重复数据开始");
		String sql = SqlReader.readSql("/com/kd/business/sql/distinct/distinctPmsBusTable.sql");
		systemService.executeSql(sql);
		logger.info("PMS母线去重复数据结束");
	}
	
	public class PmsBusThreadInsertor implements Runnable {
		private PmsBusFileInsertor insertor;
		private File f;
		private String dateStr;
		String bakFolder;
		
		public PmsBusThreadInsertor(PmsBusFileInsertor insertor, File f, String dateStr, String bakFolder) {
			this.insertor = insertor;
			this.f = f;
			this.dateStr = dateStr;
			this.bakFolder = bakFolder;
		}
		@Override
		public void run() {
			insertor.insertBus(f, dateStr, bakFolder);
		}
		
	}

}
