package com.zing.zalo.zalosdk.payment.direct;

import android.content.Intent;
import android.net.Uri;

public class CallHelper {
	public static Intent getCallIntent( String phone){
	    Intent intent = new Intent(Intent.ACTION_DIAL);
	    intent.setData(Uri.parse("tel:" + phone.replaceAll("\\s", "")));
	    return intent;
	}
}
