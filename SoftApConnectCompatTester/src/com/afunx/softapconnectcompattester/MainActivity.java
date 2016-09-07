package com.afunx.softapconnectcompattester;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final Logger log = Logger.getLogger(MainActivity.class);
	private static final int POPMENU_ID_EDIT_SSID = 0;
	private static final int POPMENU_ID_EDIT_PWD = 1;
	private ListView mListView;
	private List<SoftApXmlModel> mSoftApList;
	private AdapterView.OnItemLongClickListener mOnItemLongClickListener;
	private PopupMenu.OnMenuItemClickListener mOnMenuItemClickListener;
	private SoftApXmlModel mSoftApSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		log.debug("onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		mListView = (ListView) findViewById(R.id.listview);
		mOnItemLongClickListener = new MyOnItemLongClickListener();
		mOnMenuItemClickListener = new MyOnMenuItemClickListener();

		mSoftApList = SoftApPersistentor.loadSoftAps();

		ListAdapter adapter = new MyAdapter();
		mListView.setAdapter(adapter);
		mListView.setOnItemLongClickListener(mOnItemLongClickListener);

	}

	private void showEditSsidDialog() {
		log.debug("showEditSsidDialog()");
		View view = View.inflate(this, R.layout.edit_dialog, null);
		TextView textview = (TextView) view.findViewById(R.id.tv_edit_dialog);
		textview.setText(mSoftApSelected.getSsid());
		EditText edittext = (EditText) view.findViewById(R.id.edt_edit_dialog);
		edittext.setHint(R.string.softap_edit_dialog_ssid_hint);

		new AlertDialog.Builder(this)
				.setView(view)
				.setTitle(R.string.softap_edit_dialog_ssid_title)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO
								log.debug("showEditSsidDialog() confirm");
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
			// TODO
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
			// TODO
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
}
