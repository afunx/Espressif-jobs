package com.afunx.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.afunx.softapconnectcompattester.MyApplication;

public class FileUtil {

	private static final Logger log = Logger.getLogger(FileUtil.class);

	public static void writeSdCardFile(String filePathRelative,
			String fileNameRelative, String content) throws IOException {
		String fileDir = MyApplication.sharedInstance().getEspRootSDPath()
				+ filePathRelative;
		String filePath = fileDir + fileNameRelative;
		log.debug("try to write content to file:" + filePath + " ...");
		File dir = new File(fileDir);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(filePath);
		FileOutputStream fout = new FileOutputStream(file);
		fout.write(content.getBytes());
		fout.close();
		log.info("write content to file:" + filePath + " suc");
	}

	public static String readSdCardFile(String filePathRelative,
			String fileNameRelative) throws IOException {
		String filePath = MyApplication.sharedInstance().getEspRootSDPath()
				+ filePathRelative + fileNameRelative;
		log.debug("try to read content to file:" + filePath + " ...");
		FileInputStream fin = new FileInputStream(filePath);
		int length = fin.available();
		byte[] buffer = new byte[length];
		fin.read(buffer);
		fin.close();
		String content = new String(buffer);
		log.info("read content to file:" + filePath + " suc");
		return content;
	}
}
