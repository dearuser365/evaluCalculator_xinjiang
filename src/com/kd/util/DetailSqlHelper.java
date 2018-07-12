package com.kd.util;

import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;


import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class DetailSqlHelper {
	public static String makeSql(String templateContent, Object data) {
		 Configuration config = new Configuration();
//        config.setClassForTemplateLoading(PageHelper.class, "");
        config.setEncoding(Locale.getDefault(), "UTF-8");
        StringWriter out = new StringWriter();
        Template template;
        try {
        	StringTemplateLoader stringLoader = new StringTemplateLoader();  
            stringLoader.putTemplate("detailTemplate", templateContent);  
            stringLoader.putTemplate("detailTemplate",templateContent);  
            config.setTemplateLoader(stringLoader);  
            template = config.getTemplate("detailTemplate","utf-8");  
            template.setEncoding("UTF-8");
            template.process(data, out);
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return out.toString();
	}
}
