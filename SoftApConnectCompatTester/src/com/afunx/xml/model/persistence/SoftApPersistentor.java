package com.afunx.xml.model.persistence;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;

import com.afunx.softapconnectcompattester.MyApplication;
import com.afunx.util.FileUtil;
import com.afunx.xml.model.SoftApXmlModel;
import com.afunx.xml.model.parser.SaxSoftApXmlParser;
import com.afunx.xml.model.parser.SoftApXmlParser;

public class SoftApPersistentor {

	private static final Logger log = Logger
			.getLogger(SoftApPersistentor.class);

	private static final String FILE_PATH_RELATIVE = "configure/";
	private static final String FILE_NAME_RELATIVE = "softap.xml";

	private static List<SoftApXmlModel> loadSoftApsByAssets() {
		log.debug("loadSoftApsByAssets()...");
		InputStream is = null;
		List<SoftApXmlModel> softaps = null;
		try {
			is = MyApplication.sharedInstance().getAssets().open("softaps.xml");
			SoftApXmlParser parser = new SaxSoftApXmlParser();
			softaps = parser.parse(is);
			log.debug("loadSoftApsByAssets() suc");
		} catch (Exception e) {
			log.warn("loadSoftApsByAssets() fail");
			log.warn("Exception e:" + e.getLocalizedMessage());
		}

		return softaps;
	}

	private static List<SoftApXmlModel> loadSoftApsByFile() {
		log.debug("loadSoftApsByFile()...");
		String content = null;
		List<SoftApXmlModel> softaps = null;
		try {
			content = FileUtil.readSdCardFile(FILE_PATH_RELATIVE,
					FILE_NAME_RELATIVE);
			InputStream is = new ByteArrayInputStream(content.getBytes());
			SoftApXmlParser parser = new SaxSoftApXmlParser();
			softaps = parser.parse(is);
			log.debug("loadSoftApsByFile() suc");
		} catch (Exception e) {
			log.warn("loadSoftApsByFile() fail");
			log.warn("Exception e:" + e.getLocalizedMessage());
		}

		return softaps;
	}

	private static void saveSoftApsByFile(List<SoftApXmlModel> softaps) {
		log.debug("saveSoftApsByFile()...");
		SoftApXmlParser parser = new SaxSoftApXmlParser();
		try {
			String content = parser.serialize(softaps);
			FileUtil.writeSdCardFile(FILE_PATH_RELATIVE, FILE_NAME_RELATIVE,
					content);
			log.debug("saveSoftApsByFile() suc");
		} catch (Exception e) {
			log.warn("saveSoftApsByFile() fail");
			log.warn("Exception e:" + e.getLocalizedMessage());
		}
	}

	/**
	 * load softaps by xml from sdcard or assets(when sdcard fail)
	 * 
	 * @return softaps
	 */
	public static List<SoftApXmlModel> loadSoftAps() {
		List<SoftApXmlModel> softaps = null;
		softaps = loadSoftApsByFile();
		if (softaps == null) {
			softaps = loadSoftApsByAssets();
			saveSoftApsByFile(softaps);
		}
		log.debug("loadSoftAps() softaps:" + softaps);
		return softaps;
	}

	/**
	 * save softaps by xml in sdcard
	 * 
	 * @param softaps
	 */
	public static void saveSoftAps(List<SoftApXmlModel> softaps) {
		saveSoftApsByFile(softaps);
	}

}
