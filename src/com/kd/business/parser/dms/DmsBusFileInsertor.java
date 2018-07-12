package com.kd.business.parser.dms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.kd.business.saxHandler.DmsSaxHandler;
import com.kd.service.SystemService;
import com.kd.util.FileUtil;
import com.kd.util.PropertiesUtil;
import com.kd.util.SqlReader;


/**
 * 母线台账文件解析
 * @author DELL
 *
 */
public class DmsBusFileInsertor implements DmsFileInsertorInterface {
	@Autowired
	private SystemService systemService;

	private static final Logger logger = Logger.getLogger(DmsBusFileInsertor.class);
	Properties properties = new PropertiesUtil("sysConfig.properties").getProperties();

	@Override
	public boolean isMyFile(String fileName) {
		boolean isMyFile = false;
		if (fileName != null && fileName.endsWith("_bus.xml")) {
			isMyFile = true;
		}
		return isMyFile;
	}

	// 母线入库
	@Override
	public void insertToDB(File f, String bakFolder) {
		try {			
			SAXParserFactory saxpf = SAXParserFactory.newInstance();
			SAXParser saxp = saxpf.newSAXParser();
			List<String> sqlList = new ArrayList<String>();			
			saxp.parse(FileUtil.readFileInputSource(f, "GBK"), new DmsBusHandler(f, bakFolder, sqlList,systemService));
			if(sqlList.size()>0){				
				String[] sqlArrays = sqlList.toArray(new String[sqlList.size()]);					
				systemService.pushSqls(sqlArrays);
			}			
			FileUtil.moveFile(f.getAbsolutePath(), bakFolder + f.getName());
			logger.info("保存备份文件：" + bakFolder + f.getName());
		} catch (Exception e) {
			String errorFileStr = bakFolder + File.separator + "error" + File.separator + f.getName();
			FileUtil.moveFile(f.getAbsolutePath(), errorFileStr);
			logger.error("文件解析错误，保存error文件:" + errorFileStr, e);
			e.printStackTrace();
		}
	}

	public void removeDuplication() {
		logger.info("DMS母线去重复数据开始");
		String sql = SqlReader.readSql("/com/kd/business/sql/distinct/distinctDmsBusTable.sql");
		systemService.executeSql(sql);
		logger.info("DMS母线去重复数据结束");
	}

	class DmsBusHandler extends DmsSaxHandler {
		public DmsBusHandler() {}

		public DmsBusHandler(File file, String bakFolder,List<String> sqlList,SystemService systemService) {
			super(file, bakFolder, sqlList,systemService);
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			super.startElement(uri, localName, qName, attributes);
			if (qName.equals("da")) {
				String name = attributes.getValue("name");
				if (name.length() > 45) {
					// 为了规避数据库字符串截断
					name = name.substring(0, 45);
				}
//				systemService.executeSql(
//						"INSERT INTO EVALUSYSTEM.DETAIL.DMSBUS(ORGAN_CODE,DV,DEVID,NAME,GTIME) " + 
//						"VALUES(?, ?, ?, ?, ?)", code,
//						attributes.getValue("dvid"), 
//						attributes.getValue("bus"), 
//						name, gTime);
				String sql = "INSERT INTO EVALUSYSTEM.DETAIL.DMSBUS(ORGAN_CODE,DV,DEVID,NAME,GTIME)VALUES("
						+code+",'"+attributes.getValue("dvid")+"','"+attributes.getValue("bus")+"','"+name+"','"+gTime+"')";
				
				sqlList.add(sql);
			}
		}
	}
}
