package com.afunx.blewifidemo;

import com.afunx.ble.constants.BleKeys;
import com.afunx.ble.task.WifiConnectTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConnectWifiActivity extends Activity {
	
	private EditText mEdtSsid;
	private EditText mEdtPwd;
	private Button mBtnConfirm;
	private String mBleAddress;
	private Handler mHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		setContentView(R.layout.activity_dialog);
		mEdtSsid = (EditText) findViewById(R.id.edtSsid);
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
	}
	
	private void configureWifi() {
		final String ssid = mEdtSsid.getText().toString();
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
		new Thread(task).start();
		finish();
	}
	
}
