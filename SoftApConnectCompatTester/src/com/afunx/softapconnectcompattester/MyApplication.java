package com.afunx.softapconnectcompattester;

import org.apache.log4j.Logger;

import com.espressif.iot.log.InitLogger;

import android.app.Application;
import android.os.Environment;

public class MyApplication extends Application {
	
	private static final Logger log = Logger.getLogger(MyApplication.class);
	
	private static MyApplication instance;

	public static MyApplication sharedInstance() {
		if (instance == null) {
			throw new NullPointerException(
					"MyApplication instance is null, please register in AndroidManifest.xml first");
		}
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		InitLogger.init();
		log.debug("onCreate()");
	}
	
	public String getEspRootSDPath()
    {
        String path = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            path = Environment.getExternalStorageDirectory().toString() + "/Espressif/SoftApConnectCompatTester/";
        }
        log.info("getEspRootSDPath() path="+path);
        return path;
    }
}
