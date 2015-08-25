package com.zing.zalo.zalosdk.common;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;

public class DimensionHelper {
	public static float dipsToPXs(int dip, Resources r) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
	}
	
	public static float getScaleFactor(Activity owner) {
		Display display = owner.getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		switch (metrics.densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			return 1f/2;
		case DisplayMetrics.DENSITY_MEDIUM:			
			return 2f/3;
		case DisplayMetrics.DENSITY_HIGH:
		case DisplayMetrics.DENSITY_400:
		case DisplayMetrics.DENSITY_XHIGH:
		case DisplayMetrics.DENSITY_XXHIGH:
		case DisplayMetrics.DENSITY_XXXHIGH:
		default:
			return 1f;
		}
	}
}
