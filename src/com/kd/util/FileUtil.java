package com.kd.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

import org.xml.sax.InputSource;

public class FileUtil {
	public static String getFileContent(File f, String charsetName) {
		String content = "";
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			String tempLine = "";
			fis = new FileInputStream(f);
			isr = new InputStreamReader(fis, charsetName);
			br = new BufferedReader(isr);
			while ((tempLine = br.readLine()) != null) {
				content += tempLine + "\n";
			}
		} catch (FileNotFoundException e) {
			System.out.println("找不到指定文件");
		} catch (IOException e) {
			System.out.println("读取文件失败");
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}

	/**
	 * 新建目录
	 * 
	 * @param folderPath
	 *            String 如 c:/zzl
	 * @return boolean
	 */
	public static void newFolder(String folderPath) {
		try {
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.mkdir();
			}
		} catch (Exception e) {
			System.out.println("新建目录操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 新建文件
	 * 
	 * @param filePathAndName
	 *            String 文件路径及名称 如c:/zzl.txt
	 * @param fileContent
	 *            String 文件内容
	 * @return boolean
	 */
	public static void newFile(String filePathAndName, String fileContent) {

		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}
			FileWriter resultFile = new FileWriter(myFilePath); 
			PrintWriter myFile = new PrintWriter(resultFile);
			String strContent = fileContent;
			myFile.println(strContent);
			resultFile.close();

		} catch (Exception e) {
			System.out.println("新建文件操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 删除文件
	 * 
	 * @param filePathAndName
	 *            String 文件路径及名称 如c:/zzl.txt
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			myDelFile.delete();

		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 删除文件夹
	 * 
	 * @param filePathAndName
	 *            String 文件夹路径及名称 如c:/zzl
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹

		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param path
	 *            String 文件夹路径 如 c:/zzl
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/zzl.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/zzl.txt
	 * @return boolean
	 */
	public static void copyFile(String oldPath, String newPath) {
		InputStream inStream = null;
		FileOutputStream fs = null;
		try {
			int byteread = 0;
			File oldfile = new File(oldPath);
			File newFileFolder = new File(newPath).getParentFile();
			if (!newFileFolder.exists()) {
				newFileFolder.mkdirs();
			}
			if (oldfile.exists()) {
				inStream = new FileInputStream(oldPath);
				fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		} finally{
			try {
				fs.close();
				inStream.close();
			} catch(Exception e) {
				
			}
		}

	}

	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/zzl
	 * @param newPath
	 *            String 复制后路径 如：f:/zzl/ff
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 移动文件到指定目录
	 * 
	 * @param oldPath
	 *            String 如：c:/zzl.txt
	 * @param newPath
	 *            String 如：d:/zzl.txt
	 */
	public static void moveFile(String oldPath, String newPath) {
		copyFile(oldPath, newPath);
		delFile(oldPath);

	}

	/**
	 * 移动文件到指定目录
	 * 
	 * @param oldPath
	 *            String 如：c:/zzl.txt
	 * @param newPath
	 *            String 如：d:/zzl.txt
	 */
	public static void moveFolder(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
		delFolder(oldPath);

	}
	
	/**
	 * charSet转换成UTF-8
	 * @throws Exception 
	 */
	public static String readFile(File file, String charSet) throws Exception {
		InputStream is = new FileInputStream(file);
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[10240];
		int rc = 0;
		while ((rc = is.read(buff, 0, 10240)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		byte[] bytes = swapStream.toByteArray();
		is.close();
		return new String(bytes, charSet).trim();
	}
	/**
	 * charSet转换成UTF-8
	 * @throws Exception 
	 */
	public static InputSource readFileInputSource(File file, String charSet) throws Exception {
		 FileInputStream fis = new FileInputStream(file);
	     Reader reader = new InputStreamReader(fis, charSet);
	     return new InputSource(reader);
	}

}
