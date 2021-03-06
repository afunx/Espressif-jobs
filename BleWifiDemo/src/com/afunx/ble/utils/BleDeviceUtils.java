package com.afunx.ble.utils;

import android.bluetooth.BluetoothDevice;

import com.afunx.ble.device.BleDevice;

public class BleDeviceUtils {

	private static final String ESP_BLE_PREFIX = "Espressif_";

	/**
	 * check whether the ble device belong to Espressif
	 * 
	 * @param device
	 *            the ble device to be checked
	 * @return whether the ble device belong to Espressif
	 */
	public static boolean isEspBleDevice(BleDevice bleDevice) {
		String deviceName = bleDevice.getBluetoothDevice().getName();
		return deviceName != null && deviceName.startsWith(ESP_BLE_PREFIX);
	}
	
	/**
	 * check whether the bluetooth device belong to Espressif
	 * 
	 * @param device
	 *            the bluetooth device to be checked
	 * @return whether the bluetooth device belong to Espressif
	 */
	public static boolean isEspBluetoothDevice(BluetoothDevice bluetoothDevice) {
		String deviceName = bluetoothDevice.getName();
		return deviceName != null && deviceName.startsWith(ESP_BLE_PREFIX);
	}
	
}
