package com.zing.zalo.zalosdk.payment.direct;

import java.io.IOException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.zing.zalo.zalosdk.payment.R;
import com.zing.zalo.zalosdk.view.PaymentGateWayXMLParser;

public class PaymentGatewayActivity extends Activity implements OnClickListener{

	boolean canBack;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        canBack = true;
        try {
			ZaloPaymentInfo info = ZaloPaymentInfo.parse(getIntent().getStringExtra("payInfo"));
			setContentView(R.layout.zalosdk_activity_payment_gateway);
			Log.i(getClass().getName(), "amount:" + info.amount);
			findViewById(R.id.zalosdk_exit_ctl).setOnClickListener(this);			
			if (info.amount <= 0) {
				findViewById(R.id.zalosdk_zingcard_ctl).setOnClickListener(this);
				findViewById(R.id.zalosdk_mobile_card_ctl).setOnClickListener(this);
				findViewById(R.id.zalosdk_mobile_account_ctl).setOnClickListener(this);
				findViewById(R.id.zalosdk_atm_ctl).setOnClickListener(this);
				findViewById(R.id.zalosdk_call_ctl).setOnClickListener(this);
	        } else {	        	
	        	findViewById(R.id.zalosdk_zingcard_ctl).setVisibility(View.GONE);
				findViewById(R.id.zalosdk_mobile_card_ctl).setVisibility(View.GONE);
				findViewById(R.id.zalosdk_atm_ctl).setOnClickListener(this);				
				boolean shouldForward = true;				
	        	if (MobileAccPaymentAdapter.isSupported(info.amount)) {
	        		findViewById(R.id.zalosdk_mobile_account_ctl).setOnClickListener(this);
	        		shouldForward = false;
	        	}
	        	if (shouldForward) {
	        		setVisible(false);
	        		canBack = false;
	        		findViewById(R.id.zalosdk_atm_ctl).performClick();
	        	}
	        }
		} catch (JSONException e) {
			e.printStackTrace();
		}        
        
		try {
			new PaymentGateWayXMLParser(this).loadViewFromXml();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
    }
    
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.zalosdk_exit_ctl) {
			finish();
			try {
				ZaloPaymentService.Instance.getPaymentListner().onCancel();
			} catch (Exception e) {			
			}
		} else if (id == R.id.zalosdk_call_ctl) {
			TextView phone = (TextView) findViewById(R.id.zalosdk_hotline_ctl);
			startActivity(CallHelper.getCallIntent(phone.getText().toString()));
		} else if (id == R.id.zalosdk_zingcard_ctl 
				|| id == R.id.zalosdk_mobile_card_ctl 
				|| id == R.id.zalosdk_mobile_account_ctl 
				|| id == R.id.zalosdk_atm_ctl){
			Intent intent = new Intent(this, PaymentChannelActivity.class);
			intent.putExtra("payInfo", getIntent().getStringExtra("payInfo"));
			intent.putExtra("channel", v.getId());
			SharedPreferences gVar = getSharedPreferences("zacPref", 0);
			Editor edit = gVar.edit();
			edit.putBoolean("step0_canBack", canBack);
			edit.commit();
			finish();
			startActivity(intent);
		}
//		could not use switch case since adt-v14	
//		switch (v.getId()) {
//		case R.id.exit_ctl:
//			finish();
//			break;
//		case R.id.zingcard_ctl:
//		case R.id.mobile_card_ctl:
//		case R.id.mobile_account_ctl:
//		case R.id.atm_ctl:
//			Intent intent = new Intent(this, PaymentChannelActivity.class);
//			intent.putExtra("payInfo", getIntent().getStringExtra("payInfo"));
//			intent.putExtra("channel", v.getId());
//			finish();
//			startActivity(intent);
//			break;
//		case R.id.call_ctl:
//			TextView phone = (TextView) findViewById(R.id.hotline_ctl);
//			startActivity(CallHelper.getCallIntent(phone.getText().toString()));
//			break;
//		}
	}    
}
