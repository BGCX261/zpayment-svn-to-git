package com.zing.zalo.zalosdk.oauth.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

public class PackageUtils {

	public static boolean isPackageExists(Context context, String targetPackage) {
		PackageManager pm = context.getPackageManager();
		try {
			pm.getPackageInfo(targetPackage,
					PackageManager.GET_META_DATA);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static void launchApp(Context context, String targetPackage){
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(targetPackage);
		if (intent != null)
		{
		    // start the activity
		    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    context.startActivity(intent);
		}
	}
	
	public static void launchMarketApp(Context context, String targetPackage){
		Intent intent = new Intent(Intent.ACTION_VIEW);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intent.setData(Uri.parse("market://details?id="+targetPackage));
	    context.startActivity(intent);
	}

}
