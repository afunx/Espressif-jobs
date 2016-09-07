package com.afunx.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.afunx.xml.model.SoftApXmlModel;
import com.espressif.iot.base.net.wifi.WifiAdmin;
import com.espressif.iot.type.net.WifiCipherType;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class SoftapTestService extends Service {

	private static final Logger log = Logger.getLogger(SoftapTestService.class);
	
	private MyBinder myBinder;
	
	private SoftapTestTask mSoftapTestTask;
	
	public class MyBinder extends Binder {
		public SoftapTestService getService() {
			return SoftapTestService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		log.debug("onBind");
		myBinder = new MyBinder();
		mSoftapTestTask = new SoftapTestTask();
		return myBinder;
	}
	
	public void startService(Testcases testcases) {
		log.debug("startService()");
		mSoftapTestTask.startTask(testcases);
	}

	public void stopService() {
		log.debug("stopService()");
		mSoftapTestTask.stopTask();
	}

	private class SoftapTestTask {
		private int mTotalCount;
		private int mIndex4Next;
		private Testcases mTestcases;
		private Thread mThread;
		private volatile boolean mIsInterrupted = true;
		
		private WifiAdmin gWifiAdmin = WifiAdmin.getInstance();

		private Testcase getNextTestcase(Testcases testcases,SoftApXmlModel softap) {
			String ssid = softap.getSsid();
			String pwd = softap.getPassword();
			boolean isSsidHidden = false;
			WifiCipherType wifiCipherType = WifiCipherType.WIFICIPHER_INVALID;
			// 0: WEP 1: WPA 2: OPEN 3: WEP-SSID-HIDDEN 4: WPA-SSID-HIDDEN 5: OPEN-SSID-HIDDEN 6: INVALID
			switch(softap.getCipherType()){
			case 0:
				wifiCipherType = WifiCipherType.WIFICIPHER_WEP;
				break;
			case 1:
				wifiCipherType = WifiCipherType.WIFICIPHER_WPA;
				break;
			case 2:
				wifiCipherType = WifiCipherType.WIFICIPHER_OPEN;
				break;
			case 3:
				wifiCipherType = WifiCipherType.WIFICIPHER_WEP;
				isSsidHidden = true;
				break;
			case 4:
				wifiCipherType = WifiCipherType.WIFICIPHER_WPA;
				isSsidHidden = true;
				break;
			case 5:
				wifiCipherType = WifiCipherType.WIFICIPHER_OPEN;
				isSsidHidden = true;
				break;
			case 6:
			default:
				break;
			}
			String detail = softap.getDetail();
			int connTimeout = testcases.getTestConnTiemout();
			int connRetry = testcases.getTestConnRetry();
			Testcase testcase = Testcase.createInstance(ssid, pwd, isSsidHidden, wifiCipherType, detail, connTimeout, connRetry);
			return testcase;
		}
		
		private SoftApXmlModel getNextSoftap(Testcases testcases) {
			List<SoftApXmlModel> softaps = testcases.getSelectedSoftaps();
			int size = softaps.size();
			SoftApXmlModel softap = softaps.get(mIndex4Next);
			// 0: Single Cycle 1: Loop Cycle
			switch (testcases.getTestMode()) {
			case 0:
				mIndex4Next = (mTotalCount+1) / size;
				break;
			case 1:
			default:
				mIndex4Next = (++mIndex4Next) % size;
				break;
			}
			return softap;
		}
		
		private void doTestcaseTask(Testcase testcase) {
			log.debug("doTestcaseTask() testcase:" + testcase);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		private boolean doTestcasesTask() {
			++mTotalCount;
			// get next testcase
			SoftApXmlModel softap = getNextSoftap(mTestcases);
			Testcase testcase = getNextTestcase(mTestcases, softap);
			// do testcase
			doTestcaseTask(testcase);
			// check whether to do testcase task continue
			return mTotalCount < mTestcases.getTestCountTotal();
		}
		
		public void startTask(Testcases testcases) {
			mTestcases = testcases;
			mTotalCount = 0;
			mIndex4Next = 0;
			if (!mIsInterrupted) {
				stopTask();
			}
			mIsInterrupted = false;
			mThread = new Thread() {
				public void run() {
					while (!mIsInterrupted && doTestcasesTask()) {
						// do testcase task
					}
					log.debug("doTestcaseTask() finished");
				}
			};
			mThread.start();
		}

		public void stopTask() {
			mIsInterrupted = true;
			if (mThread != null) {
				mThread.interrupt();
				mThread = null;
			}
		}
	}
}
