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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";
	
	private SwipeRefreshLayout mSwipeRefreshLayout;
	
	private ListView mListView;
	private BleDeviceAdapter mBleDeviceAdapter;
	private LeScanCallback mLeScanCallback;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// it is brutally sometimes
		BleUtils.openBleBrutally();
		init();
		startLeScan();
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
		// listview OnItemClickListener
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.i(TAG, "item " + position + " is selected");
			}
			
		});
		
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
		startLeScan();
		// stop swipe refresh refreshing
		mSwipeRefreshLayout.setRefreshing(false);
	}
	
	private void startLeScan() {
		BleUtils.startLeScan(mLeScanCallback);
	}
	
	private void stopLeScan() {
		BleUtils.stopLeScan(mLeScanCallback);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// stop scan
		stopLeScan();
	}
	
}
