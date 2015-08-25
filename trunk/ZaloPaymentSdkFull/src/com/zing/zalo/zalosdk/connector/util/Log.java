/**
 * 
 */
package com.zing.zalo.zalosdk.connector.util;

import com.zing.zalo.zalosdk.payment.BuildConfig;

public class Log {
    @SuppressWarnings("unused")
	static final boolean LOG = false && BuildConfig.DEBUG;

    public static void i(String tag, String string) {
        if (LOG) android.util.Log.i(tag, string);
    }
    public static void e(String tag, String string) {
        if (LOG) android.util.Log.e(tag, string);
    }
    public static void e(String tag, String string, Throwable r) {
        if (LOG) android.util.Log.e(tag, string, r);
    }    
    public static void d(String tag, String string) {
        if (LOG) android.util.Log.d(tag, string);
    }
    public static void v(String tag, String string) {
        if (LOG) android.util.Log.v(tag, string);
    }
    public static void w(String tag, String string) {
        if (LOG) android.util.Log.w(tag, string);
    }
    public static void w(String tag, String string, Throwable r) {
        if (LOG) android.util.Log.w(tag, string, r);
    }
    
    public static void w(String tag, Object[] objs) {
        if (LOG) {
        	for (Object object : objs) {
        		android.util.Log.w(tag, object.toString());	
			}        	
        }
    }
	public static void w(String tag, Throwable ioe) {
		if (LOG) {
			android.util.Log.w(tag, ioe);
		}
	}
}