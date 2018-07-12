package com.kd.business.parser.dms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.kd.business.saxHandler.DmsSaxHandler;
import com.kd.service.SystemService;
import com.kd.util.FileUtil;
import com.kd.util.SqlReader;

/**
 * 开关台账文件解析
 * @author DELL
 *
 */
public class DmsCbFileInsertor implements DmsFileInsertorInterface {
	@Autowired
	private SystemService systemService;

	private static final Logger logger = Logger.getLogger(DmsCbFileInsertor.class);
	
	@Override
	public boolean isMyFile(String fileName) {
		boolean isMyFile = false;
		if (fileName != null && fileName.endsWith("_cb.xml")) {
			isMyFile = true;
		}
		return isMyFile;
	}

	//解析开关和刀闸入库
	@Override
	public void insertToDB(File f, String bakFolder) {
		try {
			SAXParserFactory saxpf = SAXParserFactory.newInstance();
			SAXParser saxp = saxpf.newSAXParser();
			List<String> sqlList = new ArrayList<String>();
			
			saxp.parse(FileUtil.readFileInputSource(f, "GBK"), new DmsCbHandler(f, bakFolder,sqlList,systemService));
			if(sqlList.size()>0){				
				String[] sqlArrays = sqlList.toArray(new String[sqlList.size()]);					
				systemService.pushSqls(sqlArrays);
			}				
			FileUtil.moveFile(f.getAbsolutePath(), bakFolder + f.getName());
			logger.info("保存备份文件：" + bakFolder + f.getName());
		} catch (Exception e) {
			String errorFileStr = bakFolder 
					+File.separator + "error" 
					+File.separator + f.getName();
			FileUtil.moveFile(f.getAbsolutePath(), errorFileStr);
			logger.error("文件解析错误，保存error文件:" + errorFileStr, e);
			e.printStackTrace();
		}
	}
	
	public void removeDuplication() {
		logger.info("DMS开关刀闸去重复数据开始");
		String sql = SqlReader.readSql("/com/kd/business/sql/distinct/distinctDmsCbDisTable.sql");
		systemService.executeSql(sql);
		logger.info("DMS开关刀闸去重复数据结束");
	}
	
	class DmsCbHandler extends DmsSaxHandler {
		public DmsCbHandler() {
		}

		public DmsCbHandler(File file, String bakFolder,List<String> sqlList,SystemService systemService) {
			super(file, bakFolder,sqlList,systemService);
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {			
			super.startElement(uri, localName, qName, attributes);
			if (qName.equals("da")) {
				String flag = null;
				String name = attributes.getValue("name");
				String subsid = attributes.getValue("subsid");
				//5107是高压用户，高压用户的不参加状态比对
				if (subsid!= null 
						&& subsid.length() >= 36 
						&& "5107".equals(subsid.substring(32, 36))) {
					flag = "0";
				} else {
					flag = "1";
				}
				
				if (name.length() > 45) {
					// 为了规避数据库字符串截断
					name = name.substring(0, 45);
				}
//				systemService.executeSql("INSERT INTO EVALUSYSTEM.DETAIL.DMSCB(ORGAN_CODE,DV,CB,NAME,TYPE,GTIME,STATUS,FLAG,REMOTETYPE) "
//					+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", code, 
//					attributes.getValue("dvid"), 
//					attributes.getValue("cb"), 
//					name, "1", gTime, 
//					attributes.getValue("status"), flag, 
//					attributes.getValue("remotetype"));
				String sql = "INSERT INTO EVALUSYSTEM.DETAIL.DMSCB(ORGAN_CODE,DV,CB,NAME,TYPE,GTIME,STATUS,FLAG,REMOTETYPE)VALUES("
						+code+",'"+attributes.getValue("dvid")+"','"+attributes.getValue("cb")+"','"+name+"','1','"+gTime+
						"',"+attributes.getValue("status")+","+flag+","+attributes.getValue("remotetype")+")";
			
				sqlList.add(sql);
			}						
		}
	}
}
