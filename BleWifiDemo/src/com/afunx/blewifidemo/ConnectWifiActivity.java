package com.afunx.blewifidemo;

import java.util.UUID;

import com.afunx.ble.constants.BleKeys;
import com.afunx.ble.task.WifiConnectTask;
import com.afunx.ble.utils.BleUtils;
import com.afunx.ble.utils.ByteUtils;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConnectWifiActivity extends Activity {
	
	private SsidSpinnerFragment mFragmentSsid;
	private EditText mEdtPwd;
	private Button mBtnConfirm;
	private String mBleAddress;
	private Handler mHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	// set window attributes
	private void setWindowAttributes() {
		Window window = getWindow();
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		// get display pixel of width and height
		final int width = getResources().getDisplayMetrics().widthPixels;
		final int height = getResources().getDisplayMetrics().heightPixels;
		final float scaleX = 0.6f;
		final float scaleY = 0.6f;
		layoutParams.width = (int) (width * scaleX);
		layoutParams.height = (int) (height * scaleY);
		window.setAttributes(layoutParams);
	}
	
	private void init() {
		setContentView(R.layout.activity_dialog);

		setWindowAttributes();

		mEdtPwd = (EditText) findViewById(R.id.edtPwd);
		mBtnConfirm = (Button) findViewById(R.id.btn_confirm_wifi);
		mBtnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				configureWifi();
			}
		});

		mBleAddress = getIntent().getExtras().getString(BleKeys.BLE_ADDRESS);
		mHandler = new Handler();
		mFragmentSsid = (SsidSpinnerFragment) getFragmentManager().findFragmentById(R.id.fragment_ssid_spinner);
	}
	
	private void configureWifi() {
		final String ssid = mFragmentSsid.getSsid();
		final String pwd = mEdtPwd.getText().toString();
		WifiConnectTask task = WifiConnectTask.createInstance(this, mBleAddress);
		task.setSsid(ssid);
		task.setPassword(pwd);
		Runnable callbackSuc = new Runnable() {
			@Override
			public void run() {
				mHandler.post(new Runnable(){
					@Override
					public void run() {
						Toast.makeText(ConnectWifiActivity.this,
								R.string.configure_suc, Toast.LENGTH_SHORT)
								.show();
					}
				});
			}
			
		};
		task.setSucCallback(callbackSuc);
		Runnable callbackFail = new Runnable() {
			@Override
			public void run() {
				mHandler.post(new Runnable(){
					@Override
					public void run() {
						Toast.makeText(ConnectWifiActivity.this,
								R.string.configure_fail, Toast.LENGTH_SHORT)
								.show();
					}
				});
			}
		};
		task.setFailCallback(callbackFail);
		BleUtils.Callback bleCallback = new BleUtils.Callback() {

			@Override
			public void onCharacteristicChanged(BluetoothGatt gatt,
					final BluetoothGattCharacteristic characteristic) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						UUID uuid = characteristic.getUuid();
						byte[] value = characteristic.getValue();
						String text = "uuid:" + uuid + ", value:"
								+ ByteUtils.prettyFormat(value);
						Toast.makeText(ConnectWifiActivity.this, text,
								Toast.LENGTH_LONG).show();
					}
				});
			}
		};
		task.setBleCallback(bleCallback);
		new Thread(task).start();
		finish();
	}
	
}