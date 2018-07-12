package com.kd.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * <b>Application name:</b> PropertiesUtil.java <br>
 * <b>Application describing: </b> <br>
 * <b>Copyright:</b> Copyright &copy; 2015 zhursh 版权所有。<br>
 * <b>Company:</b> zhursh <br>
 * <b>Date:</b> 2015-6-29 <br>
 * @author <a href="mailto:zhursh133@sina.com"> zhursh </a>
 * @version V1.0
 */
public class PropertiesUtil {
    private String properiesName = "";

    public PropertiesUtil() {

    }

    public PropertiesUtil(String fileName) {
        this.properiesName = fileName;
    }

    public String readProperty(String key) {
        String value = "";
        InputStream is = null;
        try {
            is = PropertiesUtil.class.getClassLoader().getResourceAsStream(properiesName);
            Properties p = new Properties();
            p.load(is);
            value = p.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    public Properties getProperties() {
        Properties p = new Properties();
        InputStream is = null;
        try {
            is = PropertiesUtil.class.getClassLoader().getResourceAsStream(properiesName);
            p.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return p;
    }

    public void writeProperty(String key, String value) {
        InputStream is = null;
        OutputStream os = null;
        Properties p = new Properties();
        try {
            is = new FileInputStream(properiesName);
            p.load(is);
            os = new FileOutputStream(PropertiesUtil.class.getClassLoader().getResource(properiesName).getFile());

            p.setProperty(key, value);
            p.store(os, key);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is) is.close();
                if (null != os) os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
