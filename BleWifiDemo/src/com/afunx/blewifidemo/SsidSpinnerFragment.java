package com.afunx.blewifidemo;

import java.util.List;

import com.afunx.ble.adapter.SsidSpinnerAdapter;
import com.afunx.ble.utils.WifiUtils;

import android.app.Fragment;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

public class SsidSpinnerFragment extends Fragment {

	private Spinner mSpinner;
	private SsidSpinnerAdapter mAdapter;
	private final long mInterval = 5000;
	private Handler mHandler;
	private WifiUtils.Callback mCallback = new WifiUtils.Callback() {
		@Override
		public void onWifiScan(final List<ScanResult> scanResultList) {
			mHandler.post(new Runnable() {
				public void run() {
					// check whether the onWifiScan() callback is called first time
					boolean firstTime = mSpinner.getSelectedItemPosition() == -1;
					// keep selected ssid immutable when spinner items changed
					String selectedSsid = mAdapter.getSelectedSsid(mSpinner
							.getSelectedItemPosition());
					mAdapter.addOrUpdateScanResultList(scanResultList);
					int selectedPosition = mAdapter
							.getSelectedPosition(selectedSsid);
					mSpinner.setSelection(selectedPosition);
					// when first time, set selected item according to the wifi connected
					if (firstTime) {
						String ssidWifiConnected = WifiUtils
								.getConnectedWifiSsid(getActivity());
						if (!TextUtils.isEmpty(ssidWifiConnected)) {
							int wifiSsidPosition = mAdapter
									.getSelectedPosition(ssidWifiConnected);
							mSpinner.setSelection(wifiSsidPosition);
						}
					}
				}
			});
		}
	};

	public String getSsid() {
		return "spinnerSsid";
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Context context = getActivity().getApplicationContext();
		WifiUtils.startWifiScan(context, mCallback, mInterval);
	}

	@Override
	public void onPause() {
		super.onPause();
		WifiUtils.stopWifiScan();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_ssid_spinner, container);
		mSpinner = (Spinner) view.findViewById(R.id.spn_ssid);
		Context context = getActivity();
		mAdapter = new SsidSpinnerAdapter(context);
		mSpinner.setAdapter(mAdapter);
		return view;
	}
}
