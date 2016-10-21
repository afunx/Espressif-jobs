package com.afunx.blewifidemo;

import com.afunx.ble.adapter.BleDeviceAdapter;
import com.afunx.ble.constants.BleKeys;
import com.afunx.ble.device.BleDevice;
import com.afunx.ble.utils.BleUtils;
import com.afunx.ble.utils.WifiUtils;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";
	
	private SwipeRefreshLayout mSwipeRefreshLayout;
	
	private ListView mListView;
	private CheckBox mFilterCb;
	private BleDeviceAdapter mBleDeviceAdapter;
	private LeScanCallback mLeScanCallback;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// it is brutally sometimes
		BleUtils.openBleBrutally();
		WifiUtils.openWifiBrutally(getApplicationContext());
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		startLeScan();
	}
	
	@Override
	protected void onPause() {
		stopLeScan();
		super.onPause();
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
				Intent intent = new Intent(MainActivity.this, ConnectWifiActivity.class);
				BleDevice bleDevice = (BleDevice) mBleDeviceAdapter.getItem(position);
				intent.putExtra(BleKeys.BLE_ADDRESS, bleDevice.getBluetoothDevice().getAddress());
				startActivity(intent);
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
		
		// Filter CheckBox
		mFilterCb = (CheckBox) findViewById(R.id.cb_filter);
		mFilterCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mBleDeviceAdapter.setIsFilterOpen(isChecked);
			}
		});
		mBleDeviceAdapter.setIsFilterOpen(mFilterCb.isChecked());
	}

	private void doRefresh() {
		// clear ble devices in UI
		mBleDeviceAdapter.clear();
		// stop swipe refresh refreshing
		mSwipeRefreshLayout.setRefreshing(false);
	}
	
	private void startLeScan() {
		BleUtils.startLeScan(mLeScanCallback);
	}
	
	private void stopLeScan() {
		BleUtils.stopLeScan(mLeScanCallback);
	}
	
}
