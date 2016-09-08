package com.afunx.softapconnectcompattester;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.afunx.service.SoftapTestService;
import com.afunx.service.SoftapTestService.MyBinder;
import com.afunx.service.Testcases;
import com.afunx.view.ProgressBar4Executing;
import com.afunx.xml.model.SoftApXmlModel;
import com.afunx.xml.model.persistence.SoftApPersistentor;
import com.espressif.iot.base.net.wifi.WifiAdmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final Logger log = Logger.getLogger(MainActivity.class);
	
	private static final int POPMENU_ID_EDIT_SSID = 0;
	private static final int POPMENU_ID_EDIT_PWD = 1;
	private static final int POPMENU_ID_EDIT_DETAIL = 2;
	private static final int POPMENU_ID_EDIT_DELETE = 3;
	
	private static final int MENU_ID_ADD_SOFTAP = 0;
	private static final int MENU_ID_START_SERVICE = 1;
	
	private ListView mListView;
	private MyAdapter mAdapter;
	private List<SoftApXmlModel> mSoftApList;
	private List<SoftApXmlModel> mSoftApListPrev;
	private AdapterView.OnItemLongClickListener mOnItemLongClickListener;
	private PopupMenu.OnMenuItemClickListener mOnMenuItemClickListener;
	private SoftApXmlModel mSoftApSelected;

	private SoftapTestService mSoftapTestService;
	private ProgressBar4Executing mProgressBar4Executing;
	
	private ServiceConnection sc = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			log.info("ServiceConnection onServiceDisconnected()");
			mSoftapTestService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			log.info("ServiceConnection onServiceConnected()");
			mSoftapTestService = ((MyBinder) service).getService();
		}
	};
	/**
	 * ========================================================
	 * ==================logic related start===================
	 * ========================================================
	 */
	
	private void cloneSoftApList(List<SoftApXmlModel> dest,List<SoftApXmlModel>src)
	{
		for(SoftApXmlModel softap : src){
			SoftApXmlModel copy = softap.cloneObject();
			dest.add(copy);
		}
	}
	
	private void updateSoftApList(List<SoftApXmlModel> dest,List<SoftApXmlModel>src)
	{
		dest.clear();
		cloneSoftApList(dest, src);
	}

	private SoftApXmlModel pickSoftApBySsid(List<SoftApXmlModel> dest,
			String ssid) {
		// pick softap by ssid
		for (SoftApXmlModel softap : dest) {
			if (softap.getSsid().equals(ssid)) {
				return softap;
			}
		}
		return null;
	}
	
	private boolean compareSoftApList(List<SoftApXmlModel> dest,
			List<SoftApXmlModel> src) {
		// check size
		if (dest.size() != src.size()) {
			return false;
		}
		// check each softap from dest
		for (SoftApXmlModel softapDest : dest) {
			String ssid = softapDest.getSsid();
			SoftApXmlModel softapSrc = pickSoftApBySsid(src, ssid);
			if (softapSrc == null || !softapSrc.equals(softapDest)) {
				return false;
			}
		}
		return true;
	}
	
	private List<SoftApXmlModel> getSelectedSoftApList(
			List<SoftApXmlModel> softApList) {
		List<SoftApXmlModel> selectedSoftaps = new ArrayList<SoftApXmlModel>();
		for (SoftApXmlModel softap : softApList) {
			if(softap.getIsSelected()) {
				selectedSoftaps.add(softap.cloneObject());
			}
		}
		return selectedSoftaps;
	}
	
	// for write xml into sd card is quick, just let it sync
	private void saveSoftApListSync(List<SoftApXmlModel> softapList) {
		log.debug("saveSoftApListSync()");
		List<SoftApXmlModel> copyList = new ArrayList<SoftApXmlModel>();
		updateSoftApList(copyList, softapList);
		SoftApPersistentor.saveSoftAps(copyList);
	}
	
	private void doRefreshIfNecessary() {
		log.debug("doRefreshIfNecessary()");
		log.debug("doRefreshIfNecessary() prev list:" + mSoftApListPrev);
		log.debug("doRefreshIfNecessary() cur list:" + mSoftApList);
		if (!compareSoftApList(mSoftApList, mSoftApListPrev)) {
			log.debug("doRefreshIfNecessary() changed");
			saveSoftApListSync(mSoftApList);
			updateSoftApList(mSoftApListPrev, mSoftApList);
		}
		mAdapter.notifyDataSetChanged();
	}
	
	/**
	 * ========================================================
	 * ===================logic related end====================
	 * ========================================================
	 */
	
	/**
	 * ========================================================
	 * ====================UI related start====================
	 * ========================================================
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		log.debug("onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		mListView = (ListView) findViewById(R.id.listview);
		mOnItemLongClickListener = new MyOnItemLongClickListener();
		mOnMenuItemClickListener = new MyOnMenuItemClickListener();

		mSoftApList = SoftApPersistentor.loadSoftAps();
		mSoftApListPrev = new ArrayList<SoftApXmlModel>();
		cloneSoftApList(mSoftApListPrev, mSoftApList);

		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemLongClickListener(mOnItemLongClickListener);
		
		mProgressBar4Executing = new ProgressBar4Executing();

		Intent intent = new Intent(this, SoftapTestService.class);
		bindService(intent, sc, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		log.debug("onDestroy()");
		if (mSoftapTestService != null) {
			mSoftapTestService.stopService();
		}
		unbindService(sc);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ID_START_SERVICE, Menu.NONE, R.string.softap_menu_item_start_title);
		menu.add(Menu.NONE, MENU_ID_ADD_SOFTAP, Menu.NONE, R.string.softap_menu_item_add_title);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ID_ADD_SOFTAP:
			log.debug("menu item add softap is clicked");
			showAddSoftapDialog();
			return true;
		case MENU_ID_START_SERVICE:
			log.debug("menu item start service is clicked");
			showStartTestDialog();
			return true;
		}
		return false;
	}
	
	private void showExecProgressDialog() {
		log.debug("showExecProgressDialog()");
		final SoftapTestService localService = mSoftapTestService;
		final ProgressBar4Executing progressbar = mProgressBar4Executing;
		Runnable cancelRunnable = new Runnable() {
			@Override
			public void run() {
				if(localService!=null){
					localService.stopService();
				}
			}
		};
		progressbar.show(this, cancelRunnable);
	}
	
	private void showStartTestDialog() {
		log.debug("showStartTestDialog()");
		View view = View.inflate(this, R.layout.start_dialog, null);
		final Spinner spTestmode = (Spinner) view.findViewById(R.id.sp_start_dialog_testmode);
		final EditText edtTestcount = (EditText) view.findViewById(R.id.edt_start_dialog_testcount);
		final EditText edtTestconnTiemout = (EditText) view.findViewById(R.id.edt_start_dialog_conntimeout);
		final EditText edtTestconnRetry = (EditText) view.findViewById(R.id.edt_start_dialog_connretry);
		final TextView tvTestTitle = (TextView) view.findViewById(R.id.tv_start_dialog);
		final List<SoftApXmlModel> selectedSoftaps = getSelectedSoftApList(mSoftApList);
		String testTitle = selectedSoftaps.size() + " softaps is selected";
		tvTestTitle.setText(testTitle);
		
		AlertDialog alertDialog = new AlertDialog.Builder(this)
		.setView(view)
		.setTitle(R.string.softap_menu_item_start_title)
		.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
							public void onClick(DialogInterface dialog,
									int which) {
								log.debug("showStartTestDialog() confirm");
								int testCount = 0;
								String testCountStr = edtTestcount.getText().toString();
								// check test count
								if(!TextUtils.isEmpty(testCountStr)){
									testCount = Integer.parseInt(testCountStr);
								}
								if (testCount <= 0) {
									Toast.makeText(
											MainActivity.this,
											R.string.softap_toast_err_start_testcount_zero,
											Toast.LENGTH_LONG).show();
									return;
								}
								// check test connect timeout
								int connTimeout = 0;
								String connTimeoutStr = edtTestconnTiemout.getText().toString();
								if(!TextUtils.isEmpty(connTimeoutStr)){
									connTimeout = Integer.parseInt(connTimeoutStr);
								}
								if(connTimeout<=0) {
									Toast.makeText(
											MainActivity.this,
											R.string.softap_toast_err_start_testtimeout_zero,
											Toast.LENGTH_LONG).show();
									return;
								}
								// check test retry time
								int connRetry = 0;
								String connRetryStr = edtTestconnRetry.getText().toString();
								if(!TextUtils.isEmpty(connRetryStr)){
									connRetry = Integer.parseInt(connRetryStr);
								}
								if(connRetry<=0) {
									Toast.makeText(
											MainActivity.this,
											R.string.softap_toast_err_start_testretry_zero,
											Toast.LENGTH_LONG).show();
									return;
								}
								// check wifi is open already
								if(!WifiAdmin.getInstance().isWifiEnabled()){
									Toast.makeText(
											MainActivity.this,
											R.string.softap_toast_err_start_wifi_is_closed,
											Toast.LENGTH_LONG).show();
									return;
								}
								int testMode = spTestmode
										.getSelectedItemPosition();
								Testcases testcases = new Testcases();
								testcases.setSelectedSoftaps(selectedSoftaps);
								testcases.setTestConnRetry(connRetry);
								testcases.setTestConnTimeout(connTimeout);
								testcases.setTestCount(testCount);
								testcases.setTestMode(testMode);
								testcases.setProgressBar(mProgressBar4Executing);
								log.debug("showStartTestDialog() testMode:"
										+ testMode + ", testCount:" + testCount);
								showExecProgressDialog();
								mSoftapTestService.startService(testcases);
								
							}

				})
		.setNegativeButton(android.R.string.cancel, null).show();
		alertDialog.setCancelable(false);
	}
	
	private void showAddSoftapDialog() {
		log.debug("showAddSoftapDialog()");
		View view = View.inflate(this, R.layout.add_dialog, null);
		final EditText edtSsid = (EditText) view.findViewById(R.id.edt_edit_add_ssid);
		final EditText edtPwd = (EditText) view.findViewById(R.id.edt_edit_add_pwd);
		final Spinner spCipherType = (Spinner) view.findViewById(R.id.sp_cipher_type_item);
		final EditText edtDetail = (EditText) view.findViewById(R.id.edt_edit_add_detail);
		final CheckBox cbSelected = (CheckBox) view.findViewById(R.id.cb_edit_add_selected);
		
		new AlertDialog.Builder(this)
		.setView(view)
		.setTitle(R.string.softap_menu_item_add_title)
		.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						log.debug("showEditSsidDialog() confirm");
						SoftApXmlModel softap = new SoftApXmlModel();
						String ssid = edtSsid.getText().toString();
						softap.setSsid(ssid);
						String password = edtPwd.getText().toString();
						softap.setPassword(password);
						int cipherType = spCipherType.getSelectedItemPosition();
						softap.setCipherType(cipherType);
						String detail = edtDetail.getText().toString();
						softap.setDetail(detail);
						softap.setIsSelected(cbSelected.isChecked());
						if(pickSoftApBySsid(mSoftApList, ssid)!=null) {
							Toast.makeText(MainActivity.this, R.string.softap_toast_err_softa_exist_already, Toast.LENGTH_LONG).show();
						} else {
							mSoftApList.add(softap);
							doRefreshIfNecessary();
						}
					}

				})
		.setNegativeButton(android.R.string.cancel, null).show();
	}
	
	private void showEditSsidDialog() {
		log.debug("showEditSsidDialog()");
		View view = View.inflate(this, R.layout.edit_dialog, null);
		TextView textview = (TextView) view.findViewById(R.id.tv_edit_dialog);
		textview.setText(mSoftApSelected.getSsid());
		final EditText edittext = (EditText) view.findViewById(R.id.edt_edit_dialog);
		edittext.setHint(R.string.softap_edit_dialog_ssid_hint);
		String title = getString(R.string.softap_edit_dialog_ssid_title) + " " + mSoftApSelected.getSsid();

		new AlertDialog.Builder(this)
				.setView(view)
				.setTitle(title)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								log.debug("showEditSsidDialog() confirm");
								String newSsid = edittext.getText().toString();
								mSoftApSelected.setSsid(newSsid);
								doRefreshIfNecessary();
							}

						})
				.setNegativeButton(android.R.string.cancel, null).show();
	}
	
	private void showEditPwdDialog() {
		log.debug("showEditPwdDialog()");
		View view = View.inflate(this, R.layout.edit_dialog, null);
		TextView textview = (TextView) view.findViewById(R.id.tv_edit_dialog);
		textview.setText(mSoftApSelected.getPassword());
		final EditText edittext = (EditText) view.findViewById(R.id.edt_edit_dialog);
		edittext.setHint(R.string.softap_edit_dialog_pwd_hint);
		String title = getString(R.string.softap_edit_dialog_pwd_title) + " " + mSoftApSelected.getSsid();

		new AlertDialog.Builder(this)
				.setView(view)
				.setTitle(title)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								log.debug("showEditPwdDialog() confirm");
								String newPwd = edittext.getText().toString();
								mSoftApSelected.setPassword(newPwd);
								doRefreshIfNecessary();
							}

						})
				.setNegativeButton(android.R.string.cancel, null).show();
	}
	
	private void showEditDetailDialog() {
		log.debug("showEditDetailDialog()");
		View view = View.inflate(this, R.layout.edit_dialog, null);
		TextView textview = (TextView) view.findViewById(R.id.tv_edit_dialog);
		textview.setText(mSoftApSelected.getDetail());
		final EditText edittext = (EditText) view.findViewById(R.id.edt_edit_dialog);
		edittext.setHint(R.string.softap_edit_dialog_detail_hint);
		String title = getString(R.string.softap_edit_dialog_detail_title) + " " + mSoftApSelected.getSsid();

		new AlertDialog.Builder(this)
				.setView(view)
				.setTitle(title)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								log.debug("showEditDetailDialog() confirm");
								String newDetail = edittext.getText().toString();
								mSoftApSelected.setDetail(newDetail);
								doRefreshIfNecessary();
							}

						})
				.setNegativeButton(android.R.string.cancel, null).show();
	}
	
	private void showEditDeleteDialog() {
		log.debug("showEditDeleteDialog()");
		View view = View.inflate(this, R.layout.edit_dialog, null);
		TextView textview = (TextView) view.findViewById(R.id.tv_edit_dialog);
		String detailText = getString(R.string.softap_edit_dialog_delete_hint);
		textview.setText(detailText);
		final EditText edittext = (EditText) view.findViewById(R.id.edt_edit_dialog);
		edittext.setVisibility(View.GONE);
		String title = getString(R.string.softap_edit_dialog_delete_title) + " " + mSoftApSelected.getSsid();

		new AlertDialog.Builder(this)
				.setView(view)
				.setTitle(title)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								log.debug("showEditDeleteDialog() confirm");
								mSoftApList.remove(mSoftApSelected);
								doRefreshIfNecessary();
							}

						})
				.setNegativeButton(android.R.string.cancel, null).show();
	}
	
	private class MyOnItemLongClickListener implements
			AdapterView.OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			mSoftApSelected = ((ViewHolder) view.getTag()).softap;
			PopupMenu popMenu = new PopupMenu(MainActivity.this, view);
			Menu menu = popMenu.getMenu();
			menu.add(Menu.NONE, POPMENU_ID_EDIT_SSID, 0,
					R.string.softap_popmenu_edit_ssid);
			menu.add(Menu.NONE, POPMENU_ID_EDIT_PWD, 0,
					R.string.softap_popmenu_edit_pwd);
			menu.add(Menu.NONE, POPMENU_ID_EDIT_DETAIL, 0,
					R.string.softap_popmenu_edit_detail);
			menu.add(Menu.NONE, POPMENU_ID_EDIT_DELETE, 0,
					R.string.softap_popmenu_edit_delete);
			popMenu.setOnMenuItemClickListener(mOnMenuItemClickListener);
			popMenu.show();
			return true;
		}

	}
	
	private class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

		private SoftApXmlModel mSoftap;
		
		MyOnItemSelectedListener(SoftApXmlModel softap){
			mSoftap = softap;
		}
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			log.debug("MyOnItemSelectedListener onItemSelected():" + mSoftap);
			mSoftap.setCipherType(position);
			doRefreshIfNecessary();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// ignore
			log.debug("MyOnItemSelectedListener onNothingSelected() ignore");
		}
		
	}

	private class MyOnCheckedChangeListener implements
			CompoundButton.OnCheckedChangeListener {
		
		private SoftApXmlModel mSoftap;
		
		MyOnCheckedChangeListener(SoftApXmlModel softap){
			mSoftap = softap;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			log.debug("MyOnCheckedChangeListener onCheckedChanged():" + mSoftap);
			mSoftap.setIsSelected(isChecked);
			doRefreshIfNecessary();
		}

	}

	private class MyOnMenuItemClickListener implements
			PopupMenu.OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			log.debug("MyOnMenuItemClickListener onMenuItemClick() selected softap:" + mSoftApSelected);
			if (item.getItemId() == POPMENU_ID_EDIT_SSID) {
				showEditSsidDialog();
				return true;
			} else if (item.getItemId() == POPMENU_ID_EDIT_PWD) {
				log.debug("MyOnMenuItemClickListener onMenuItemClick() edit pwd");
				showEditPwdDialog();
				return true;
			} else if (item.getItemId() == POPMENU_ID_EDIT_DETAIL) {
				log.debug("MyOnMenuItemClickListener onMenuItemClick() edit detail");
				showEditDetailDialog();
				return true;
			} else if (item.getItemId() == POPMENU_ID_EDIT_DELETE) {
				log.debug("MyOnMenuItemClickListener onMenuItemClick() edit delete");
				showEditDeleteDialog();
			}
			return false;
		}

	}

	private static class ViewHolder {
		private TextView tvSsidHolder;
		private TextView tvPwdHolder;
		private Spinner spCipherTypeHolder;
		private TextView tvDetailHolder;
		private CheckBox cbSelectedHolder;
		private SoftApXmlModel softap;
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mSoftApList.size();
		}

		@Override
		public Object getItem(int position) {
			return mSoftApList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			SoftApXmlModel softap = mSoftApList.get(position);
			if (convertView == null) {
				
				view = View.inflate(MainActivity.this, R.layout.softap_item,
						null);
				holder = new ViewHolder();
				holder.tvSsidHolder = (TextView) view
						.findViewById(R.id.tv_ssid_item);
				holder.tvPwdHolder = (TextView) view
						.findViewById(R.id.tv_pwd_item);
				holder.spCipherTypeHolder = (Spinner) view
						.findViewById(R.id.sp_cipher_type_item);
				MyOnItemSelectedListener onItemSelectedListener = new MyOnItemSelectedListener(softap);
				holder.spCipherTypeHolder.setOnItemSelectedListener(onItemSelectedListener);
				holder.tvDetailHolder = (TextView) view
						.findViewById(R.id.tv_detail_item);
				holder.cbSelectedHolder = (CheckBox) view
						.findViewById(R.id.cb_selected_item);
				MyOnCheckedChangeListener onCheckedChangeListener = new MyOnCheckedChangeListener(softap);
				holder.cbSelectedHolder
						.setOnCheckedChangeListener(onCheckedChangeListener);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			holder.softap = softap;
			
			holder.tvSsidHolder.setText(softap.getSsid());
			holder.tvPwdHolder.setText(softap.getPassword());
			// cipher is [0,6]
			int cipherType = softap.getCipherType();
			cipherType = cipherType > 6 ? 6 : Math.max(0, cipherType);
			holder.spCipherTypeHolder.setSelection(cipherType);
			holder.tvDetailHolder.setText(softap.getDetail());
			holder.cbSelectedHolder.setChecked(softap.getIsSelected());
			return view;
		}

	}
	/**
	 * ========================================================
	 * =====================UI related end=====================
	 * ========================================================
	 */
}
