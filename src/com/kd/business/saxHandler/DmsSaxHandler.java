package com.kd.business.saxHandler;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.kd.service.SystemService;
import com.kd.util.DateUtil;
import com.kd.util.FileUtil;


public class DmsSaxHandler  extends DefaultHandler{

	private SystemService systemService;
	private static final Logger logger = Logger.getLogger(DmsSaxHandler.class);
	
	protected String bakFolder;
	protected File file;

	protected String code;
	protected String gTime;
	protected String countTime;

	protected List<String> sqlList;
	

	public DmsSaxHandler() {}

	public DmsSaxHandler(File file, String bakFolder,List<String> sqlList,SystemService systemService) {
		this.bakFolder = bakFolder;
		this.file = file;
		this.sqlList = sqlList;
		this.systemService = systemService;
		
	}
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		if (qName.equals("head")) {
			code = attributes.getValue("code");
			gTime = attributes.getValue("count");
			countTime = DateUtil.transSpecialTime(attributes.getValue("count"));
			String type = attributes.getValue("type");
			if("cblist".equals(type)){
				systemService.executeSql("DELETE FROM EVALUSYSTEM.DETAIL.DMSCB WHERE ORGAN_CODE = "+code+" AND TYPE = 1");
			}else if("dsclist".equals(type)){
				systemService.executeSql("DELETE FROM EVALUSYSTEM.DETAIL.DMSCB WHERE ORGAN_CODE = "+code+" AND TYPE = 0");
			}else if("subslist".equals(type)){
				systemService.executeSql("DELETE FROM EVALUSYSTEM.DETAIL.DMSST WHERE ORGAN_CODE = "+code);
			}else if("translist".equals(type)){
				systemService.executeSql("DELETE FROM EVALUSYSTEM.DETAIL.DMSLD WHERE ORGAN_CODE = "+code);
			}else if("buslist".equals(type)){
				systemService.executeSql("DELETE FROM EVALUSYSTEM.DETAIL.DMSBUS WHERE ORGAN_CODE = "+code);
			}
		}
	}
	@Override
	public void startDocument() throws SAXException {
		logger.info("开始解析文件：" + file.getName());
	}
	@Override
	public void endDocument() throws SAXException {
		logger.info("解析文件结束：" + file.getName());
	}
	
}
