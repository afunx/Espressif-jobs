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
import com.afunx.xml.model.persistence.SoftApPersistentor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final Logger log = Logger.getLogger(MainActivity.class);
	private ListView mListView;
	private List<SoftApXmlModel> mSoftApList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		log.debug("onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		mListView = (ListView) findViewById(R.id.listview);
		mSoftApList = SoftApPersistentor.loadSoftAps();
		
		ListAdapter adapter = new MyAdapter();
		mListView.setAdapter(adapter);
		
	}
	
	private static class ViewHolder {
		private TextView tvSsidHolder;
		private TextView tvPwdHolder;
		private TextView tvCipherTypeHolder;
	}
	
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mSoftApList.size();
		}

		@Override
		public Object getItem(int position) {
			return mSoftApList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			
			if (convertView == null) {
				view = View.inflate(MainActivity.this, R.layout.softap_item,
						null);
				holder = new ViewHolder();
				holder.tvSsidHolder = (TextView) view.findViewById(R.id.tv_ssid_item);
				holder.tvPwdHolder = (TextView) view.findViewById(R.id.tv_pwd_item);
				holder.tvCipherTypeHolder = (TextView) view.findViewById(R.id.tv_cipher_type_item);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			
			SoftApXmlModel softap = mSoftApList.get(position);
			holder.tvSsidHolder.setText(softap.getSsid());
			holder.tvPwdHolder.setText(softap.getPassword());
			switch (softap.getCipherType()) {
			case 0:
				holder.tvCipherTypeHolder.setText("WEP");
				break;
			case 1:
				holder.tvCipherTypeHolder.setText("WPA");
				break;
			case 2:
				holder.tvCipherTypeHolder.setText("OPEN");
				break;
			case 3:
			default:
				holder.tvCipherTypeHolder.setText("INVALID");
				break;
				
			}
			
			return view;
		}
		
	}

	/**
	 * just for test xml parser start
	 */
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
	/**
	 * just for test xml parser end
	 */
}
