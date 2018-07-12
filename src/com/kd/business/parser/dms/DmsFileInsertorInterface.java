package com.kd.business.parser.dms;

import java.io.File;

public interface DmsFileInsertorInterface {
	public boolean isMyFile(String fileName);
	public void insertToDB(File f, String bakFolder);
}
