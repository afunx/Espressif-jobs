package com.afunx.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.afunx.view.ProgressBar4Executing;
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
		private TestcasesResult mTestcasesResult;
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
			int connTimeout = testcases.getTestConnTimeout();
			int connRetry = testcases.getTestConnRetry();
			Testcase testcase = Testcase.createInstance(ssid, pwd, isSsidHidden, wifiCipherType, detail, connTimeout, connRetry);
			return testcase;
		}
		
		private SoftApXmlModel getNextSoftap(Testcases testcases) {
			++mTotalCount;
			List<SoftApXmlModel> softaps = testcases.getSelectedSoftaps();
			int size = testcases.getTestCount();
			SoftApXmlModel softap = softaps.get(mIndex4Next);//TODO index 2, size is 2
			// 0: Single Cycle 1: Loop Cycle
			switch (testcases.getTestMode()) {
			case 0:
				mIndex4Next = mTotalCount / size;
				break;
			case 1:
			default:
				mIndex4Next = (++mIndex4Next) % size;
				break;
			}
			return softap;
		}

		private boolean disconnectWifi(String ssid) {
			gWifiAdmin.disableConnected(ssid);
			while (gWifiAdmin.isWifiConnected(ssid)) {
				// wait wifi disconnected
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					return false;
				}
			}
			return true;
		}
		
		private long connectWifi(int connTimeout, int connRetry, String ssid,
				WifiCipherType type,boolean isSsidHidden,String password) {
			final long startTimestamp = System.currentTimeMillis();
			final long retryTimestamp = 1000 * connTimeout / connRetry;
			long currentTimestamp = startTimestamp;
			long costTimestamp = 0;
			// enable connected first time
			gWifiAdmin.enableConnected(ssid, type, isSsidHidden, password);
			int connTime = 1;
			while (costTimestamp < 1000 * connTimeout) {
				if (costTimestamp / connTime > retryTimestamp) {
					gWifiAdmin.enableConnected(ssid, type, isSsidHidden,
							password);
					++connTime;
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// return 0 means fail
					return 0;
				}
				currentTimestamp = System.currentTimeMillis();
				costTimestamp = currentTimestamp - startTimestamp;
				if(gWifiAdmin.isWifiConnected(ssid)){
					return costTimestamp;
				}
			}
			// return -1 means fail
			return -1;
		}
		
		private void onExecuting(Testcase testcase) {
			TestcaseResult result = TestcaseResult.createFailResult(testcase);
			ProgressBar4Executing progressbar = mTestcases.getProgressBar();
			progressbar.updateResultCur(result.getCurTestcaseStr());
		}
		
		private void onSuc(Testcase testcase,long consumeTimestamp) {
			TestcaseResult result = TestcaseResult.createSucResult(testcase, consumeTimestamp);
			ProgressBar4Executing progressbar = mTestcases.getProgressBar();
			progressbar.updateResultLast(result.getPrevTestcaseStr());
			mTestcasesResult.suc(testcase, consumeTimestamp);
			log4jSaveResult();
		}
		
		private void onFail(Testcase testcase) {
			TestcaseResult result = TestcaseResult.createFailResult(testcase);
			ProgressBar4Executing progressbar = mTestcases.getProgressBar();
			progressbar.updateResultLast(result.getPrevTestcaseStr());
			mTestcasesResult.fail(testcase);
			log4jSaveResult();
		}
		
		private void log4jSaveResult() {
			
			ProgressBar4Executing progressbar = mTestcases.getProgressBar();
			String shortResultStr = mTestcasesResult.getShortResultStr();
			progressbar.updateCompleteness(shortResultStr);
			
			String resultStr = mTestcasesResult.getResultsStr();
			log.info(resultStr);
		}
		
		private void doTestcaseTask(Testcase testcase) {
			log.debug("doTestcaseTask() testcase:" + testcase);
			onExecuting(testcase);
			final int connTimeout = testcase.getConnTimeout();
			final int connRetry = testcase.getConnRetry();
			final String ssid = testcase.getSsid();
			final WifiCipherType type = testcase.getWifiCipherType();
			final boolean isSsidHidden = testcase.getIsSsidHidden();
			final String password = testcase.getPwd();
			// 0. check wifi type
			if (type == WifiCipherType.WIFICIPHER_INVALID) {
				// invalid wifi cipher should fail surely
				onFail(testcase);
			}
			// 1. disable wifi
			if (!disconnectWifi(ssid)) {
				// cancel
				return;
			}
			// 2. connect wifi
			long result = connectWifi(connTimeout, connRetry, ssid, type,
					isSsidHidden, password);
			if (result == 0) {
				// cancel
			} else if (result < 0) {
				// fail to connect wifi
				onFail(testcase);
			} else {
				// suc to connect wifi
				onSuc(testcase, result);
			}
		}
		
		private boolean doTestcasesTask() {
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
			mTestcasesResult = TestcasesResult.createInstance(testcases);
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
					mTestcases.getProgressBar().dismiss();
					final TestcasesResult testcasesResult = mTestcasesResult;
					mTestcases.getProgressBar().showResultDialog(testcasesResult);
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
