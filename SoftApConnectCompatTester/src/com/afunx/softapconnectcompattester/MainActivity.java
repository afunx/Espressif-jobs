package com.afunx.softapconnectcompattester;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	private static final Logger log = Logger.getLogger(MainActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		log.debug("onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
	}
}
