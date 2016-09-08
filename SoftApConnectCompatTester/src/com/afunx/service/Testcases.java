package com.afunx.service;

import java.util.List;

import com.afunx.view.ProgressBar4Executing;
import com.afunx.xml.model.SoftApXmlModel;

public class Testcases {
	//0: Single Cycle 1: Loop Cycle 
	private int mTestMode;
	private int mTestCount;
	// connect timeout in seconds
	private int mTestConnTimeout;
	private int mTestConnRetry;
	private List<SoftApXmlModel> mSelectedSoftaps;
	private ProgressBar4Executing mProgressBar;
	
	public ProgressBar4Executing getProgressBar() {
		return mProgressBar;
	}
	
	public void setProgressBar(ProgressBar4Executing progressBar) {
		mProgressBar = progressBar;
	}
	
	public int getTestMode() {
		return mTestMode;
	}
	
	public void setTestMode(int testMode) {
		mTestMode = testMode;
	}
	
	public int getTestCount() {
		return mTestCount;
	}
	
	public void setTestCount(int testCount) {
		mTestCount = testCount;
	}
	
	public int getTestConnTiemout() {
		return mTestConnTimeout;
	}
	
	public void setTestConnTimeout(int connTimeout) {
		mTestConnTimeout = connTimeout;
	}
	
	public int getTestConnRetry() {
		return mTestConnRetry;
	}
	
	public void setTestConnRetry(int connRetry) {
		mTestConnRetry = connRetry;
	}
	
	public List<SoftApXmlModel> getSelectedSoftaps() {
		return mSelectedSoftaps;
	}
	
	public void setSelectedSoftaps(List<SoftApXmlModel> selectedSoftaps) {
		mSelectedSoftaps = selectedSoftaps;
	}
	
	public int getTestCountTotal() {
		return mTestCount * mSelectedSoftaps.size();
	}
}
