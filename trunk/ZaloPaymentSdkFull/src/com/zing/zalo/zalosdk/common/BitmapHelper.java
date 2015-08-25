package com.zing.zalo.zalosdk.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class BitmapHelper {
	public static Bitmap b64ToImage(String encodedImage) {		
		byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		return decodedByte;
	}
}
