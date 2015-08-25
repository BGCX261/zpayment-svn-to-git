package com.zing.zalo.zalosdk.payment.direct;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.zing.zalo.zalosdk.model.AbstractView;
import com.zing.zalo.zalosdk.model.ZTextView;
import com.zing.zalo.zalosdk.payment.R;
import com.zing.zalo.zalosdk.view.CommonXMLParser;
import com.zing.zalo.zalosdk.view.MobileAccOptXMLParser;

public class MobileAccOtpPaymentAdapter extends CommonPaymentAdapter {

	String zacTranxID;
	String mac;
	String accPhone;
	CommonXMLParser xmlParser;
	public MobileAccOtpPaymentAdapter(PaymentChannelActivity owner) {
		super(owner);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.zalosdk_activity_mobile_acc_otp;
	}

	@Override
	protected void initPage() {
		try {
			xmlParser = new MobileAccOptXMLParser(owner);
			xmlParser.loadViewFromXml();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		SharedPreferences gVar = owner.getSharedPreferences("zacPref", 0);
		zacTranxID = gVar.getString("zacTranxID", "");
		accPhone = gVar.getString("otpPhone", "");
		mac = gVar.getString("otpMac", "");
		
		List<AbstractView> list = xmlParser.getFactory().getListAbstractViews();
		for (AbstractView abstractView : list) {
			if (abstractView.getClass().getSimpleName().equals("ZTextView")) {
				ZTextView e = (ZTextView) abstractView;
				if (e.getAppend().equals("otpPhone")) {
					TextView t = (TextView)owner.findViewById(R.id.view_root).findViewWithTag("otpPhone");
					t.setText(t.getText().toString() + accPhone);
					break;
				}
			}
		}
//		TextView otp_note_ctl = (TextView) owner.findViewById(R.id.zalosdk_otp_note_ctl);
//		otp_note_ctl.setText(otp_note_ctl.getText().toString() + accPhone);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
	}
	
	@Override
	protected PaymentTask getPaymentTask() {
		SubmitMobileAccOtpTask task = new SubmitMobileAccOtpTask();
		task.owner = this;
		task.zacTranxID = zacTranxID;
		task.mac = mac;
		return task;
	}
}
