package com.afunx.xml.model;

public class SoftApXmlModel {

	private String mSsid = null;
	private String mPassword = null;
	// 0: WEP 1: WPA 2: OPEN 3: INVALID
	private int mCipherType = -1;

	public String getSsid() {
		return mSsid;
	}

	public void setSsid(String ssid) {
		mSsid = ssid;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String password) {
		mPassword = password;
	}

	public int getCipherType() {
		return mCipherType;
	}

	public void setCipherType(int cipherType) {
		mCipherType = cipherType;
	}

	@Override
	public String toString() {
		return "[" + "ssid=" + mSsid + "," + "password=" + mPassword + ","
				+ "cipherType=" + mCipherType + "]";
	}
}
