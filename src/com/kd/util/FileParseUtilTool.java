package com.kd.util;

import java.io.File;
import java.util.Properties;

public class FileParseUtilTool {
	public static File getPmsFtpBakFolder(Properties properties, String folder, String dateStr){
		return new File(getPmsFtpBakFolderStr(properties, folder, dateStr));
	}
	public static String getPmsFtpBakFolderStr(Properties properties, String folder, String logDateStr){
		String bakFolderStr = 
				(properties.getProperty("pmsBakFileMainDir")
						+File.separator
						+new File(folder).getName()).replace("${dateStr}", logDateStr)
						+File.separator;
		return bakFolderStr;
	}
	public static String getDmsFtpBakFolderStr(Properties properties, String logDateStr){
		String bakFolderStr = 
				(properties.getProperty("dmsXmlBakFolder")
						+File.separator
						+logDateStr
				+File.separator);
		return bakFolderStr;
	}
}
