package com.espressif.iot.log;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Layout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;

public class MyRollingFileAppender extends RollingFileAppender {

	public MyRollingFileAppender(Layout layout, String filename)
			throws IOException {
		super(layout, filename);
	}

	
	private int getExpIndex(String fileName) {
		int index = -1;
		byte[] bytes = fileName.getBytes();
		for (int i = bytes.length - 1; i >= 0; --i) {
			byte b = bytes[i];
			if (b == '.') {
				index = i;
				break;
			}
		}
		return index;
	}
	
	private String getFileExp(String fileName) {
		int index = getExpIndex(fileName);
		return index == -1 ? "" : fileName.substring(index+1);
	}
	
	private String getFilePure(String fileName) {
		int index = getExpIndex(fileName);
		return index == -1 ? fileName : fileName.substring(0, index);
	}
	
	private String getNewFileName(String fileName, int index) {
		String fileNamePure = getFilePure(fileName);
		String fileNameExp = getFileExp(fileName);
		return  fileNamePure + index + "." + fileNameExp;
	}
	
	public// synchronization not necessary since doAppend is alreasy synched
	void rollOver() {
		File target;
		File file;

		LogLog.debug("rolling over count="
				+ ((CountingQuietWriter) qw).getCount());
		LogLog.debug("maxBackupIndex=" + maxBackupIndex);

		// If maxBackups <= 0, then there is no file renaming to be done.
		if (maxBackupIndex > 0) {
			// Delete the oldest file, to keep Windows happy.
//			file = new File(fileName + '.' + maxBackupIndex);
			file = new File(getNewFileName(fileName,maxBackupIndex));
			if (file.exists())
				file.delete();

			// Map {(maxBackupIndex - 1), ..., 2, 1} to {maxBackupIndex, ..., 3,
			// 2}
			for (int i = maxBackupIndex - 1; i >= 1; i--) {
//				file = new File(fileName + "." + i);
				file = new File(getNewFileName(fileName,i));
				if (file.exists()) {
//					target = new File(fileName + '.' + (i + 1));
					target = new File(getNewFileName(fileName, i + 1));
					LogLog.debug("Renaming file " + file + " to " + target);
					file.renameTo(target);
				}
			}

			// Rename fileName to fileName.1
//			fileName = fileNamePure + "." + 1 + fileNameExp;
//			fileName = getNewFileName(fileName, 1);
			
//			target = new File(fileName + "." + 1);
			target = new File(getNewFileName(fileName, 1));

			this.closeFile(); // keep windows happy.

			file = new File(fileName);
			LogLog.debug("Renaming file " + file + " to " + target);
			file.renameTo(target);
		}

		try {
			// This will also close the file. This is OK since multiple
			// close operations are safe.
			this.setFile(fileName, false, bufferedIO, bufferSize);
		} catch (IOException e) {
			LogLog.error("setFile(" + fileName + ", false) call failed.", e);
		}
	}
}
