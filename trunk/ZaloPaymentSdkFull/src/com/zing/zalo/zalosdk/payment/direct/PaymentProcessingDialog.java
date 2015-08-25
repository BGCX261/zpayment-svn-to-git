package com.zing.zalo.zalosdk.payment.direct;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zing.zalo.zalosdk.payment.R;

public class PaymentProcessingDialog extends PaymentDialog implements OnCancelListener {
		
	public enum Status {
		PROCESSING,
		SUCCESS,
		FAILED
	}
	
	int viewIndex = 0;
	Handler handler;
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
		    if(viewIndex > 0 && isShowing()) {
		    	hide();
		    	if (listener != null) {
					listener.onClose();
				}
		    }
		}
	};
	
	static interface OnCloseListener {
		public void onClose();
	}
	
	OnCloseListener listener;
	public PaymentProcessingDialog(Context context, OnCloseListener listener) {
		super(context);		
		handler = new Handler();
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zalosdk_activity_processing);
		showProcessingView();
	}
	
	private void showProcessingView() {
		((TextView)findViewById(R.id.zalosdk_message_ctl)).setText(R.string.zalosdk_processing);
		findViewById(R.id.zalosdk_status_ctl).setVisibility(View.GONE);
		findViewById(R.id.zalosdk_indicator_ctl).setVisibility(View.VISIBLE);
		setCancelable(false);
		viewIndex = 0;
		handler.removeCallbacks(runnable);
	}
	
	private void showSuccessView() {
		((TextView)findViewById(R.id.zalosdk_message_ctl)).setText(R.string.zalosdk_success);
		findViewById(R.id.zalosdk_indicator_ctl).setVisibility(View.GONE);
		ImageView status = (ImageView) findViewById(R.id.zalosdk_status_ctl);
		status.setImageResource(R.drawable.zalosdk_ic_success);
		status.setVisibility(View.VISIBLE);
		setCancelable(true);
		viewIndex = 1;
		autoClose();
	}
	
	private void showUnSuccessView() {
		((TextView)findViewById(R.id.zalosdk_message_ctl)).setText(R.string.zalosdk_unsuccess);
		findViewById(R.id.zalosdk_indicator_ctl).setVisibility(View.GONE);
		ImageView status = (ImageView) findViewById(R.id.zalosdk_status_ctl);
		status.setImageResource(R.drawable.zalosdk_ic_fail);
		status.setVisibility(View.VISIBLE);
		setCancelable(true);
		viewIndex = 1;
		autoClose();
	}
	
	private void autoClose() {
		handler.postDelayed(runnable, 3000);
	}
	
	public void showView(Status status) {
		show();
		switch (status) {
		case PROCESSING:
			showProcessingView();
			break;
		case SUCCESS:
			showSuccessView();
			break;
		case FAILED:
			showUnSuccessView();
			break;			
		}		
	}
	
	public void showUnSuccessView(String message) {
		((TextView)findViewById(R.id.zalosdk_message_ctl)).setText(message);
		findViewById(R.id.zalosdk_indicator_ctl).setVisibility(View.GONE);
		ImageView status = (ImageView) findViewById(R.id.zalosdk_status_ctl);
		status.setVisibility(View.VISIBLE);
		setCancelable(true);
		if (!isShowing()) {
			show();
		}
		viewIndex = 1;
		autoClose();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (listener != null) {
			listener.onClose();
		}
	}
}
