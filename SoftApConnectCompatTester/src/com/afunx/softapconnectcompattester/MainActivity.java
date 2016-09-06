package com.afunx.softapconnectcompattester;

import java.util.List;

import org.apache.log4j.Logger;

import com.afunx.xml.model.SoftApXmlModel;
import com.afunx.xml.model.persistence.SoftApPersistentor;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final Logger log = Logger.getLogger(MainActivity.class);
	private static final int POPMENU_ID_EDIT_SSID = 0;
	private static final int POPMENU_ID_EDIT_PWD = 1;
	private ListView mListView;
	private List<SoftApXmlModel> mSoftApList;
	private AdapterView.OnItemLongClickListener mOnItemLongClickListener;
	private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;
	private PopupMenu.OnMenuItemClickListener mOnMenuItemClickListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		log.debug("onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		mListView = (ListView) findViewById(R.id.listview);
		mOnItemLongClickListener = new MyOnItemLongClickListener();
		mOnCheckedChangeListener = new MyOnCheckedChangeListener();
		mOnMenuItemClickListener = new MyOnMenuItemClickListener();

		mSoftApList = SoftApPersistentor.loadSoftAps();

		ListAdapter adapter = new MyAdapter();
		mListView.setAdapter(adapter);
		mListView.setOnItemLongClickListener(mOnItemLongClickListener);

	}

	private class MyOnItemLongClickListener implements
			AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
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

	private class MyOnCheckedChangeListener implements
			CompoundButton.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			log.debug("onCheckedChanged()");
		}

	}

	private class MyOnMenuItemClickListener implements
			PopupMenu.OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			log.debug("onMenuItemClick()");
			if (item.getItemId() == POPMENU_ID_EDIT_SSID) {
				log.debug("onMenuItemClick() edit ssid");
				return true;
			} else if (item.getItemId() == POPMENU_ID_EDIT_PWD) {
				log.debug("onMenuItemClick() edit pwd");
				return true;
			}
			return false;
		}

	}

	private static class ViewHolder {
		private TextView tvSsidHolder;
		private TextView tvPwdHolder;
		private TextView tvCipherTypeHolder;
		private TextView tvDetailHolder;
		private CheckBox cbSelectedHolder;
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
				view = View.inflate(MainActivity.this, R.layout.softap_item,
						null);
				holder = new ViewHolder();
				holder.tvSsidHolder = (TextView) view
						.findViewById(R.id.tv_ssid_item);
				holder.tvPwdHolder = (TextView) view
						.findViewById(R.id.tv_pwd_item);
				holder.tvCipherTypeHolder = (TextView) view
						.findViewById(R.id.tv_cipher_type_item);
				holder.tvDetailHolder = (TextView) view
						.findViewById(R.id.tv_detail_item);
				holder.cbSelectedHolder = (CheckBox) view
						.findViewById(R.id.cb_selected_item);
				holder.cbSelectedHolder
						.setOnCheckedChangeListener(mOnCheckedChangeListener);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			SoftApXmlModel softap = mSoftApList.get(position);
			holder.tvSsidHolder.setText(softap.getSsid());
			holder.tvPwdHolder.setText(softap.getPassword());
			switch (softap.getCipherType()) {
			case 0:
				holder.tvCipherTypeHolder.setText("WEP");
				break;
			case 1:
				holder.tvCipherTypeHolder.setText("WPA");
				break;
			case 2:
				holder.tvCipherTypeHolder.setText("OPEN");
				break;
			case 3:
			default:
				holder.tvCipherTypeHolder.setText("INVALID");
				break;
			}
			holder.tvDetailHolder.setText(softap.getDetail());
			holder.cbSelectedHolder.setChecked(softap.getIsSelected());
			return view;
		}

	}
}
