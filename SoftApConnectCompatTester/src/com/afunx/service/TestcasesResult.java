package com.afunx.service;

import java.util.ArrayList;
import java.util.List;

import com.afunx.xml.model.SoftApXmlModel;
import com.espressif.iot.type.net.WifiCipherType;

public class TestcasesResult {
	
	private final List<Testcase> mTestcaseList;
	private final List<Integer> mTotalExecTime;
	private final List<Integer> mSucTime;
	private final List<Integer> mFailTime;
	private final List<Long> mSucTotalTimestamp;
	private final int mTotalCount;
	private final int mConnTimeout;
	
	private List<Testcase> getTestcaseList(Testcases testcases) {
		List<Testcase> testcaseList = new ArrayList<Testcase>();
		for (SoftApXmlModel softap : testcases.getSelectedSoftaps()) {
			String ssid = softap.getSsid();
			String pwd = softap.getPassword();
			boolean isSsidHidden = false;
			WifiCipherType wifiCipherType = WifiCipherType.WIFICIPHER_INVALID;
			switch (softap.getCipherType()) {
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
			Testcase testcase = Testcase.createInstance(ssid, pwd,
					isSsidHidden, wifiCipherType, detail, connTimeout,
					connRetry);
			testcaseList.add(testcase);
		}
		return testcaseList;
	}
	
	public TestcasesResult(Testcases testcases) {
		mConnTimeout = testcases.getTestConnTimeout();
		mTotalCount = testcases.getTestCountTotal();
		mTestcaseList = getTestcaseList(testcases);
		mTotalExecTime = new ArrayList<Integer>();
		mSucTime = new ArrayList<Integer>();
		mFailTime = new ArrayList<Integer>();
		mSucTotalTimestamp = new ArrayList<Long>();
		for (int i = 0; i < mTestcaseList.size(); ++i) {
			mTotalExecTime.add(0);
			mSucTime.add(0);
			mFailTime.add(0);
			mSucTotalTimestamp.add(0l);
		}
	}
	
	public static TestcasesResult createInstance(Testcases testcases) {
		return new TestcasesResult(testcases);
	}
	
	private Testcase pickTestcase(Testcase testcase) {
		for (Testcase testcaseInList : mTestcaseList) {
			if (testcaseInList.equals(testcase)) {
				return testcaseInList;
			}
		}
		throw new IllegalStateException("can't find proper testcase");
	}
	
	public void suc(Testcase testcase,long result) {
		Testcase exeTestcase = pickTestcase(testcase);
		int index = mTestcaseList.indexOf(exeTestcase);
		mTotalExecTime.set(index, mTotalExecTime.get(index) + 1);
		mSucTime.set(index, mSucTime.get(index) + 1);
		mSucTotalTimestamp.set(index, mSucTotalTimestamp.get(index) + 1);
	}
	
	public void fail(Testcase testcase) {
		Testcase exeTestcase = pickTestcase(testcase);
		int index = mTestcaseList.indexOf(exeTestcase);
		mTotalExecTime.set(index, mTotalExecTime.get(index) + 1);
		mFailTime.set(index, mFailTime.get(index) + 1);
	}
	
	private String getResultStr(Testcase testcase) {
		int index = mTestcaseList.indexOf(testcase);
		StringBuilder sb = new StringBuilder();
		sb.append("Testcase:\n" + testcase);
		sb.append(", execTime: " + mTotalExecTime.get(index));
		sb.append(", sucTime: " + mSucTime.get(index));
		sb.append(", failTime: " + mFailTime.get(index));
		sb.append(", execSucTotalTime: " + mSucTotalTimestamp.get(index) + " ms");
		long execSucAvgTime = mSucTime.get(index) != 0 ? mSucTotalTimestamp
				.get(index) / mSucTime.get(index) : 0;
		sb.append(", execSucAvgTime: " + execSucAvgTime + " ms");
		return sb.toString();
	}
	
	public String getResultsStr() {
		StringBuilder sb = new StringBuilder();
		for (Testcase testcase : mTestcaseList) {
			sb.append(getResultStr(testcase));
			sb.append("\n");
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private String getPrettyTimeStr(int seconds) {
		final int min = 60;
		final int hour = 60 * min;
		final int day = 24 * hour;
		StringBuilder sb = new StringBuilder();
		if (seconds > day) {
			int days = seconds / day;
			seconds -= days * day;
			sb.append(days + " day ");
		}
		if (seconds > hour) {
			int hours = seconds / hour;
			seconds -= hours * hour;
			sb.append(hours + " hour ");
		}
		if (seconds > min) {
			int mins = seconds / min;
			seconds -= mins * min;
			sb.append(mins + " min ");
		}
		sb.append(seconds + " sec");
		return sb.toString();
	}
	
	public String getShortResultStr() {
		int sucTotal = 0;
		int execTotal = 0;
		for(Integer suc : mSucTime) {
			sucTotal += suc;
		}
		for(Integer total : mTotalExecTime) {
			execTotal += total;
		}
		int maxRemainedTime = (mTotalCount-execTotal) * mConnTimeout;
		StringBuilder sb = new StringBuilder();
		sb.append("suc/exec/total:" + sucTotal + "/" + execTotal + "/" + mTotalCount + "......");
		String prettryTimeStr = getPrettyTimeStr(maxRemainedTime);
		sb.append(prettryTimeStr);
		return sb.toString();
	}
}
