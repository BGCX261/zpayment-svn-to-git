package com.zing.zalo.zalosdk.common;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;

public class DeviceHelper {
	public static String getUDID(Activity owner) {
		TelephonyManager telephonyManager = (TelephonyManager)owner.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

}
