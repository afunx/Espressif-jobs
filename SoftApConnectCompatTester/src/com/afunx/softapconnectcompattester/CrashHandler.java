package com.afunx.softapconnectcompattester;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.Logger;

public class CrashHandler implements UncaughtExceptionHandler {

	private static final Logger log = Logger.getLogger(CrashHandler.class);
	
	private CrashHandler()
    {
    }
    
    private static class InstanceHolder
    {
        static CrashHandler instance = new CrashHandler();
    }
    
    public static CrashHandler getInstance()
    {
        return InstanceHolder.instance;
    }
	
    public void init() {
    	Thread.setDefaultUncaughtExceptionHandler(this);
    }
    
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		log.fatal("uncaughtException()");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		e.printStackTrace(ps);
		log.fatal(baos.toString());
		// let log4j save log
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignore) {
		}
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}

}
