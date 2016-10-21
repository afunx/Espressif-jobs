package com.afunx.ble.utils;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiUtils {
	
	private static final String TAG = "WifiUtils";
	
	/**
	 * Callback of scan wifi
	 */
	public interface Callback {
		void onWifiScan(List<ScanResult> scanResultList);
	}
	
	private static volatile boolean sIsStop = true;
	private static volatile Thread sThread = null;
	
	private static void startWifiScanInternal(final Context context,
			final Callback callback, final long interval) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		long startTimestamp;
		long sleepTimestamp;
		while (!sIsStop) {
			startTimestamp = System.currentTimeMillis();
			List<ScanResult> scanResultList = wifiManager.getScanResults();
			if (scanResultList != null && !scanResultList.isEmpty()) {
				callback.onWifiScan(scanResultList);
			}
			wifiManager.startScan();
			sleepTimestamp = interval - (System.currentTimeMillis() - startTimestamp);
			if (sleepTimestamp > 0 && scanResultList != null
					&& !scanResultList.isEmpty()) {
				try {
					Thread.sleep(sleepTimestamp);
				} catch (InterruptedException ignore) {
				}
			}
		}
	}
	
	/**
	 * start Wifi Scan async
	 * 
	 * @param context
	 *            the context
	 * @param callback
	 *            the callback
	 * @param interval
	 *            the interval in milliseconds
	 */
	public static void startWifiScan(final Context context,
			final Callback callback, final long interval) {
		if (sIsStop) {
			sIsStop = false;
			sThread = new Thread() {
				public void run() {
					startWifiScanInternal(context, callback, interval);
				}
			};
			sThread.start();
		} else {
			Log.e(TAG, "startWifiScan() has started already");
		}
	}
	
	/**
	 * stop Wifi Scan
	 */
	public static void stopWifiScan() {
		sIsStop = true;
		if (sThread != null) {
			sThread.interrupt();
			sThread = null;
		} else {
			Log.e(TAG, "stopWifiScan()");
		}
	}
	
}
