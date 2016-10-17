package com.afunx.blewifidemo;

import com.afunx.ble.adapter.BleDeviceAdapter;
import com.afunx.ble.device.BleDevice;
import com.afunx.ble.utils.BleUtils;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	private SwipeRefreshLayout mSwipeRefreshLayout;
	
	private ListView mListView;
	private BleDeviceAdapter mBleDeviceAdapter;
	private LeScanCallback mLeScanCallback;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		
	}
	
	private void init() {
		setContentView(R.layout.activity_main);
		// swipe refresh layout and listview
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
		mListView = (ListView) findViewById(R.id.lv_devices);
		mListView.setEmptyView(findViewById(R.id.pb_empty));
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				doRefresh();
			}
		});
		// ble device adapter
		mBleDeviceAdapter = new BleDeviceAdapter(this);
		mListView.setAdapter(mBleDeviceAdapter);
		// LeScanCallback
		mLeScanCallback = new LeScanCallback() {
			@Override
			public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
				BleDevice bleDevice = new BleDevice();
				bleDevice.setBluetoothDevice(device);
				bleDevice.setRssi(rssi);
				bleDevice.setScanRecord(scanRecord);
				mBleDeviceAdapter.addOrUpdateDevice(bleDevice);
			}
		};
	}

	private void doRefresh() {
		// clear ble devices in UI
		mBleDeviceAdapter.clear();
		// start scan
		BleUtils.startLeScan(mLeScanCallback);
		// stop swipe refresh refreshing
		mSwipeRefreshLayout.setRefreshing(false);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// stop scan
		BleUtils.stopLeScan(mLeScanCallback);
	}
	
}
