package com.afunx.softapconnectcompattester;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;

import com.afunx.xml.model.SoftApXmlModel;
import com.afunx.xml.model.parser.DomSoftApXmlParser;
import com.afunx.xml.model.parser.PullSoftApXmlParser;
import com.afunx.xml.model.parser.SaxSoftApXmlParser;
import com.afunx.xml.model.parser.SoftApXmlParser;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	private static final Logger log = Logger.getLogger(MainActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		log.debug("onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

//		testXmlParser();
	}

	private void testXmlParser() {
		try {
			XmlTester.test(getAssets().open("softaps.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class XmlTester {
		public static void test(InputStream is) {
//			testSaxParser(is);
//			testDomParser(is);
//			testPullParser(is);
		}

		private static void testSaxParser(InputStream is) {
			log.debug("testSaxParser()");
			SoftApXmlParser parser = new SaxSoftApXmlParser();
			try {
				List<SoftApXmlModel> softaps = parser.parse(is);
				for(SoftApXmlModel softap: softaps) {
					log.info("softap:"+softap);
				}
				String softapsSerial = parser.serialize(softaps);
				log.info(softapsSerial);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private static void testDomParser(InputStream is) {
			log.debug("testDomParser()");
			SoftApXmlParser parser = new DomSoftApXmlParser();
			try {
				List<SoftApXmlModel> softaps = parser.parse(is);
				for(SoftApXmlModel softap: softaps) {
					log.info("softap:"+softap);
				}
				String softapsSerial = parser.serialize(softaps);
				log.info(softapsSerial);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private static void testPullParser(InputStream is) {
			log.debug("testPullParser()");
			SoftApXmlParser parser = new PullSoftApXmlParser();
			try {
				List<SoftApXmlModel> softaps = parser.parse(is);
				for(SoftApXmlModel softap: softaps) {
					log.info("softap:"+softap);
				}
				String softapsSerial = parser.serialize(softaps);
				log.info(softapsSerial);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
