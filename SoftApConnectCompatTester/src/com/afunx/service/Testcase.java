package com.afunx.service;

import com.espressif.iot.type.net.WifiCipherType;

public class Testcase {

	private final String mSsid;
	private final String mPwd;
	private final boolean mIsSsidHidden;
	private final WifiCipherType mWifiCipherType;
	private final String mDetail;
	// connect timeout in seconds
	private final int mConnTimeout;
	private final int mConnRetry;

	private Testcase(String ssid, String pwd, boolean isSsidHidden,
			WifiCipherType wifiCipherType, String detail, int connTimeout,
			int connRetry) {
		mSsid = ssid;
		mPwd = pwd;
		mIsSsidHidden = isSsidHidden;
		mWifiCipherType = wifiCipherType;
		mDetail = detail;
		mConnTimeout = connTimeout;
		mConnRetry = connRetry;
	}
	
	public static Testcase createInstance(String ssid, String pwd,
			boolean isSsidHidden, WifiCipherType wifiCipherType, String detail,
			int connTimeout, int connRetry) {
		return new Testcase(ssid, pwd, isSsidHidden, wifiCipherType, detail,
				connTimeout, connRetry);
	}

	public String getSsid() {
		return mSsid;
	}

	public String getPwd() {
		return mPwd;
	}

	public boolean getIsSsidHidden() {
		return mIsSsidHidden;
	}

	public WifiCipherType getWifiCipherType() {
		return mWifiCipherType;
	}

	public String getDetail() {
		return mDetail;
	}

	public int getConnTimeout() {
		return mConnTimeout;
	}

	public int getConnRetry() {
		return mConnRetry;
	}

	@Override
	public boolean equals(Object obj) {
		// check the type
		if (obj == null || !(obj instanceof Testcase)) {
			return false;
		}
		Testcase other = (Testcase) obj;
		return other.getIsSsidHidden() == this.getIsSsidHidden()
				&& other.getConnRetry() == this.getConnRetry()
				&& other.getConnTimeout() == this.getConnTimeout()
				&& other.getDetail().equals(this.getDetail())
				&& other.getPwd().equals(this.getPwd())
				&& other.getSsid().equals(this.getSsid())
				&& other.getWifiCipherType() == this.getWifiCipherType();
	}
	
	@Override
	public String toString() {
		return "[" + "ssid=" + mSsid + "," + "pwd=" + mPwd + ","
				+ "isSsidHidden=" + mIsSsidHidden + "," + "cipherType="
				+ mWifiCipherType + "," + "detail=" + mDetail + ","
				+ "connTimeout=" + mConnTimeout + " sec" + "," + "connRetry="
				+ mConnRetry + "]";
	}
}
