package com.afunx.ble.adapter;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import com.afunx.ble.device.BleDevice;
import com.afunx.ble.utils.BleDeviceUtils;
import com.afunx.blewifidemo.R;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BleDeviceAdapter extends BaseAdapter {

	private final List<BleDevice> mBleDeviceList;
	private final Context mContext;
	private boolean mIsFilterOpen;

	public BleDeviceAdapter(Context context) {
		mBleDeviceList = new ArrayList<BleDevice>();
		mContext = context;
	}
	
	/**
	 * set filter open or close
	 * 
	 * @param isFilterOpen
	 *            true means open while false means close
	 */
	public void setIsFilterOpen(boolean isFilterOpen) {
		if (isFilterOpen != mIsFilterOpen) {
			mIsFilterOpen = isFilterOpen;
			notifyDataSetChanged();
		}
	}
	
	/**
	 * check whether the ble device belong to Espressif
	 * 
	 * @param device
	 *            the ble device to be checked
	 * @return whether the ble device belong to Espressif
	 */
	private boolean isEspBleDevice(BleDevice device) {
		return BleDeviceUtils.isEspBleDevice(device);
	}

	/**
	 * filter some device
	 * 
	 * @param device
	 *            the device to be filtered
	 * @return true when the device is to be added or updated
	 */
	private boolean filterInterval(BleDevice device) {
		if (!mIsFilterOpen) {
			return true;
		}
		return isEspBleDevice(device);
	}
	
	/**
	 * get ble device from mBleDeviceList according to device
	 * 
	 * @param device
	 *            the according device
	 * @return ble device from mBleDeviceList according to device
	 */
	private BleDevice getBleDevice(BleDevice device) {
		BleDevice result = null;
		for (BleDevice deviceInList : mBleDeviceList) {
			if (deviceInList.equals(device)) {
				result = deviceInList;
				break;
			}
		}
		return result;
	}
	
	/**
	 * add or update ble device
	 * 
	 * @param device
	 *            the device to be added or updated
	 * @return whether the device is added or updated
	 */
	private boolean addOrUpdateDeviceInternal(BleDevice device) {
		BleDevice deviceInList = getBleDevice(device);
		if (deviceInList == null) {
			// add
			mBleDeviceList.add(device);
		} else {
			// update
			deviceInList.setRssi(device.getRssi());
			deviceInList.setBluetoothDevice(device.getBluetoothDevice());
			deviceInList.setScanRecord(device.getScanRecord());
		}
		return filterInterval(device);
	}

	/**
	 * add or update device and notify data set changed
	 * 
	 * @param device
	 *            the device to be added or updated
	 */
	public void addOrUpdateDevice(BleDevice device) {
		if (addOrUpdateDeviceInternal(device)) {
			notifyDataSetChanged();
		}
	}
	
	/**
	 * add or update deviceList and notify data set changed at last(only once)
	 * 
	 * @param deviceList
	 *            the device list to be added or updated
	 */
	public void addOrUpdateDeviceList(List<BleDevice> deviceList) {
		boolean isChanged = false;
		for (BleDevice device : deviceList) {
			if (addOrUpdateDeviceInternal(device)) {
				isChanged = true;
			}
		}
		if (isChanged) {
			notifyDataSetChanged();
		}
	}
	
	private int getCountInternal() {
		if (!mIsFilterOpen) {
			return mBleDeviceList.size();
		} else {
			int count = 0;
			for (BleDevice device : mBleDeviceList) {
				if (BleDeviceUtils.isEspBleDevice(device)) {
					count++;
				}
			}
			return count;
		}
	}
	
	private BleDevice getItemInternal(int position) {
		if (!mIsFilterOpen) {
			return mBleDeviceList.get(position);
		} else {
			int index = 0;
			for (BleDevice device : mBleDeviceList) {
				if (BleDeviceUtils.isEspBleDevice(device)) {
					if (index == position) {
						return device;
					}
					index++;
				}
			}
			throw new ConcurrentModificationException();
		}
	}
	
	/**
	 * clear deviceList and notify data set invalidated
	 */
	public void clear() {
		mBleDeviceList.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return getCountInternal();
	}

	@Override
	public Object getItem(int position) {
		return getItemInternal(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	private static class ViewHolder {
		TextView tvDevName;
		TextView tvDevAddr;
		TextView tvDevSig;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// get view holder
		ViewHolder viewHolder = null;
		if(convertView == null) {
			convertView = View.inflate(mContext, R.layout.ble_device_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvDevName = (TextView) convertView.findViewById(R.id.tv_dev_name);
			viewHolder.tvDevAddr = (TextView) convertView.findViewById(R.id.tv_dev_addr);
			viewHolder.tvDevSig = (TextView) convertView.findViewById(R.id.tv_dev_sig);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// set view content
		final BleDevice device = getItemInternal(position);
		final Resources resources = mContext.getResources();
		// device name
		String devName = device.getBluetoothDevice().getName();
		if (TextUtils.isEmpty(devName)) {
			devName = resources.getString(R.string.device_name_unknown_device);
		}
		viewHolder.tvDevName.setText(devName);
		// device address
		String titleAddr = resources.getString(R.string.device_address);
		String devAddr = device.getBluetoothDevice().getAddress();
		viewHolder.tvDevAddr.setText(titleAddr + ": " + devAddr);
		// device signal
		String titleSig = resources.getString(R.string.device_signal);
		int devSig = device.getRssi();
		viewHolder.tvDevSig.setText(titleSig + ": " + devSig);
		return convertView;
	}

}
