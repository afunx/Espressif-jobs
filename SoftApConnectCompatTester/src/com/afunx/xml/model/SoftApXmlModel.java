package com.afunx.xml.model;

public class SoftApXmlModel implements Cloneable {

	private String mSsid = "";
	private String mPassword = "";
	// 0: WEP 1: WPA 2: OPEN 3: WEP-SSID-HIDDEN 4: WPA-SSID-HIDDEN 5: OPEN-SSID-HIDDEN 6: INVALID
	private int mCipherType = -1;
	private String mDetail = "";
	private boolean mIsSelected = false;

	public boolean getIsSelected() {
		return mIsSelected;
	}

	public void setIsSelected(boolean isSelected) {
		mIsSelected = isSelected;
	}

	public String getDetail() {
		return mDetail;
	}

	public void setDetail(String detail) {
		mDetail = detail;
	}

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
	public boolean equals(Object obj) {
		// check the type
		if (obj == null || !(obj instanceof SoftApXmlModel)) {
			return false;
		}
		SoftApXmlModel other = (SoftApXmlModel) obj;
		return other.getIsSelected() == this.getIsSelected()
				&& other.getCipherType() == this.getCipherType()
				&& other.getDetail().equals(this.getDetail())
				&& other.getPassword().equals(this.getPassword())
				&& other.getSsid().equals(this.getSsid());
	}
	
	public SoftApXmlModel cloneObject() {
		try {
			return (SoftApXmlModel) clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		throw new RuntimeException();
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String toString() {
		return "[" + "ssid=" + mSsid + "," + "password=" + mPassword + ","
				+ "cipherType=" + mCipherType + "," + "detail=" + mDetail + "]";
	}
}
