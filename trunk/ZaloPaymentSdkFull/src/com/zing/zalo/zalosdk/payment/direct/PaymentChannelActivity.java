package com.zing.zalo.zalosdk.payment.direct;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class PaymentChannelActivity extends FragmentActivity {
	CommonPaymentAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		adapter = PaymentAdapterFactory.produce(this);
	}

	@Override
	public void onBackPressed() {
		try {
//			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//			v.vibrate(500);
		} catch (Exception e) {
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		adapter.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);		
	}

	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		adapter.onRestoreInstanceState(savedInstanceState);
		super.onRestoreInstanceState(savedInstanceState);				
	}
}
