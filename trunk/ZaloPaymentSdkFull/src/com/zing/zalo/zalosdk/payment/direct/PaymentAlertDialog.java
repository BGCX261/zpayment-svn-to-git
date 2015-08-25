package com.zing.zalo.zalosdk.payment.direct;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zing.zalo.zalosdk.payment.R;

public class PaymentAlertDialog extends PaymentDialog implements android.view.View.OnClickListener {
	static interface OnOkListener {
		public void onOK();
	}
	
	OnOkListener listener = null;
	
	public PaymentAlertDialog(Context context) {
		super(context);
	}

	public PaymentAlertDialog(Context context, OnOkListener listener) {
		super(context);
		this.listener = listener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zalosdk_activity_alert);
		findViewById(R.id.zalosdk_ok_ctl).setOnClickListener(this);		
	}	
	
	public void showAlert(String message) {
		show();
		((TextView)findViewById(R.id.zalosdk_message_ctl)).setText(message);
		setCancelable(false);			
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.zalosdk_ok_ctl) {
			hide();
			if(listener != null) {
				listener.onOK();
			}
		}
//		could not use switch case since adt-v14	
//		switch (v.getId()) {
//		case R.id.ok_ctl:
//			hide();
//			if(listener != null) {
//				listener.onOK();
//			}
//			break;
//		}
	}
}
