package com.afunx.softapconnectcompattester;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.afunx.xml.model.SoftApXmlModel;
import com.afunx.xml.model.persistence.SoftApPersistentor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

public class MainActivity extends Activity {

	private static final Logger log = Logger.getLogger(MainActivity.class);
	private static final int POPMENU_ID_EDIT_SSID = 0;
	private static final int POPMENU_ID_EDIT_PWD = 1;
	private static final int POPMENU_ID_EDIT_DETAIL = 2;
	private ListView mListView;
	private MyAdapter mAdapter;
	private List<SoftApXmlModel> mSoftApList;
	private List<SoftApXmlModel> mSoftApListPrev;
	private AdapterView.OnItemLongClickListener mOnItemLongClickListener;
	private PopupMenu.OnMenuItemClickListener mOnMenuItemClickListener;
	private SoftApXmlModel mSoftApSelected;

	
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
	
	private void doRefreshIfNecessary() {
		log.debug("doRefreshIfNecessary()");
		log.debug("doRefreshIfNecessary() prev list:" + mSoftApListPrev);
		log.debug("doRefreshIfNecessary() cur list:" + mSoftApList);
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

	}
	
	private void showEditSsidDialog() {
		log.debug("showEditSsidDialog()");
		View view = View.inflate(this, R.layout.edit_dialog, null);
		TextView textview = (TextView) view.findViewById(R.id.tv_edit_dialog);
		textview.setText(mSoftApSelected.getSsid());
		final EditText edittext = (EditText) view.findViewById(R.id.edt_edit_dialog);
		edittext.setHint(R.string.softap_edit_dialog_ssid_hint);

		new AlertDialog.Builder(this)
				.setView(view)
				.setTitle(R.string.softap_edit_dialog_ssid_title)
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
		textview.setText(mSoftApSelected.getSsid());
		final EditText edittext = (EditText) view.findViewById(R.id.edt_edit_dialog);
		edittext.setHint(R.string.softap_edit_dialog_pwd_hint);

		new AlertDialog.Builder(this)
				.setView(view)
				.setTitle(R.string.softap_edit_dialog_pwd_title)
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
		textview.setText(mSoftApSelected.getSsid());
		final EditText edittext = (EditText) view.findViewById(R.id.edt_edit_dialog);
		edittext.setHint(R.string.softap_edit_dialog_detail_hint);

		new AlertDialog.Builder(this)
				.setView(view)
				.setTitle(R.string.softap_edit_dialog_detail_title)
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

			if (convertView == null) {
				SoftApXmlModel softap = mSoftApList.get(position);
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
				holder.softap = softap;
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			SoftApXmlModel softap = holder.softap;
			holder.tvSsidHolder.setText(softap.getSsid());
			holder.tvPwdHolder.setText(softap.getPassword());
			// cipher is [0,3]
			int cipherType = softap.getCipherType();
			cipherType = cipherType > 3 ? 3 : Math.max(0, cipherType);
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
