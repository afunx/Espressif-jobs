package com.afunx.ble.task;

import java.util.UUID;

import com.afunx.ble.constants.BleKeys;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

public class WifiConnectTask implements Runnable {

	private static final String TAG = "WifiConnectTask";
	private final Context mContext;
	private final String mBleAddress;
	private final Object mLockConnect = new Object();
	private final Object mLockDiscover = new Object();
	private volatile boolean mIsConnected;
	private BluetoothGatt mBluetoothGatt;
	private String mSsid;
	private String mPwd;
	private Runnable mCallbackSuc;
	private Runnable mCallbackFail;
	
	private static final long INTERVAL_WRITE_GATT_CHAR = 200;
	private static final long TIMEOUT_SERVICE_DISCOVER = 1000;

	public void setSsid(String ssid) {
		mSsid = ssid;
	}

	public void setPassword(String password) {
		mPwd = password;
	}
	
	public void setSucCallback(Runnable callbackSuc) {
		mCallbackSuc = callbackSuc;
	}
	
	public void setFailCallback(Runnable callbackFail) {
		mCallbackFail = callbackFail;
	}
	
	private void notifyLockConnect() {
		synchronized (mLockConnect) {
			mLockConnect.notify();
		}
	}
	
	private void notifyLockDiscover() {
		synchronized (mLockDiscover) {
			mLockDiscover.notify();
		}
	}
	
	private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			Log.i(TAG, "onConnectionStateChange() status:" + status
					+ ",newState:" + newState + "Thread:" + Thread.currentThread());
			switch(newState){
			case BluetoothProfile.STATE_CONNECTED:
				mIsConnected = true;
				notifyLockConnect();
				break;
			case BluetoothProfile.STATE_DISCONNECTED:
				if(status==BluetoothProfile.STATE_DISCONNECTED) {
					mIsConnected = false;
					notifyLockConnect();
				}
				else if(status==BluetoothProfile.STATE_CONNECTED) {
					connectAsync();
				}
				break;
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			Log.i(TAG, "onServicesDiscovered() status:" + status);
			if(status==BluetoothGatt.GATT_SUCCESS) {
				notifyLockDiscover();
			}
		}

		@Override
		public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
			Log.i(TAG, "onReliableWriteCompleted() status:" + status);
		}
	};
	
	private WifiConnectTask(Context context, String bleAddress) {
		mContext = context;
		mBleAddress = bleAddress;
	}
	
	public static WifiConnectTask createInstance(Context context,
			String bleAddress) {
		Log.i(TAG, "bleAddress:" + bleAddress);
		return new WifiConnectTask(context, bleAddress);
	}

	private static BluetoothAdapter getAdapter() {
		return BluetoothAdapter.getDefaultAdapter();
	}
	
	private boolean connectAsync() {
		BluetoothAdapter adapter = getAdapter();
		if (adapter == null) {
			Log.w(TAG, "BluetoothAdapter is null");
			return false;
		}
		if (mBluetoothGatt == null) {
			final BluetoothDevice device = adapter.getRemoteDevice(mBleAddress);
			if (device == null) {
				Log.w(TAG, "Device not found");
				return false;
			}
			Log.d(TAG, "connectAsync() connectGatt");
			mBluetoothGatt = device.connectGatt(mContext, false,
					mBluetoothGattCallback);
			return true;
		} else {
			Log.d(TAG, "connectAsync() connect");
			return mBluetoothGatt.connect();
		}
	}
	
	private void sendMsg(BluetoothGattCharacteristic gattChar,String msg) {
		gattChar.setValue(msg);
		mBluetoothGatt.writeCharacteristic(gattChar);
		try {
			Thread.sleep(INTERVAL_WRITE_GATT_CHAR);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sendWifiSsidPwd(BluetoothGattCharacteristic gattChar) {
		// ssid
		String msg = "ssid:" + mSsid;
		Log.i(TAG, "sendWifiSsidPwd() ssid:" + mSsid);
		sendMsg(gattChar, msg);
		// pwd
		msg = "passwd:" + mPwd;
		Log.i(TAG, "sendWifiSsidPwd() pwd:" + mPwd);
		sendMsg(gattChar, msg);
		// confirm
		msg = "confirm:";
		Log.i(TAG, "sendWifiSsidPwd() confirm");
		sendMsg(gattChar, msg);
	}
	

	private void doSucCallback() {
		if (mCallbackSuc!=null) {
			mCallbackSuc.run();
		}
	}
	
	private void doFailCallback() {
		if (mCallbackFail != null) {
			mCallbackFail.run();
		}
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Thread:" + Thread.currentThread());
		mIsConnected = false;
		synchronized (mLockDiscover) {
			synchronized (mLockConnect) {
				// connect async
				if (connectAsync()) {
					Log.i(TAG, "connectAsync() suc");
					// wait connect result
					try {
						mLockConnect.wait();
					} catch (InterruptedException ignore) {
						ignore.printStackTrace();
					}
					if (!mIsConnected) {
						Log.i(TAG, "fail to connect ble: " + mBleAddress);
						doFailCallback();
						return;
					} else {
						mBluetoothGatt.discoverServices();
						try {
							mLockDiscover.wait(TIMEOUT_SERVICE_DISCOVER);
						} catch (InterruptedException ignore) {
							Log.i(TAG, "fail to discover services");
							doFailCallback();
							return;
						}
						// get characteristic
						final UUID serviceUuid = BleKeys.UUID_WIFI_SERVICE;
						final UUID characteristic = BleKeys.UUID_CONFIGURE_CHARACTERISTIC;
						final BluetoothGattService gattService = mBluetoothGatt
								.getService(serviceUuid);
						if (gattService == null) {
							Log.w(TAG, "fail to get service: " + serviceUuid
									+ ", gatt:" + mBluetoothGatt);
							doFailCallback();
							return;
						}
						final BluetoothGattCharacteristic gattCharacteristic = gattService
								.getCharacteristic(characteristic);
						if (gattCharacteristic == null) {
							Log.w(TAG, "fail to get characteristic: "
									+ characteristic);
							doFailCallback();
							return;
						}
						// send wifi ssid and pwd
						sendWifiSsidPwd(gattCharacteristic);
						doSucCallback();
					}
				} else {
					Log.i(TAG, "connectAsync() fail");
					doFailCallback();
				}
			}
		}
		Log.i(TAG, "connectAsync() exit");
	}

}
