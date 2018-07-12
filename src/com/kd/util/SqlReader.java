package com.kd.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class SqlReader {
	public static String readSql(String packageStr) {
		String sql = "-1";
		InputStream is = SqlReader.class
				.getResourceAsStream(packageStr);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int i;
			while ((i = is.read()) != -1) {
				baos.write(i);
			}
			sql = baos.toString("UTF-8");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sql;
	}
	public static String readSql(String packageStr, int index, Object data) {
		String sql = "-1";
		InputStream is = SqlReader.class
				.getResourceAsStream(packageStr);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int i;
			while ((i = is.read()) != -1) {
				baos.write(i);
			}
			sql = baos.toString("UTF-8");
		} catch(Exception e) {
			e.printStackTrace();
		}
		sql = sql.split(";")[index];
		sql = DetailSqlHelper.makeSql(sql, data);
		return sql;
	}
}
