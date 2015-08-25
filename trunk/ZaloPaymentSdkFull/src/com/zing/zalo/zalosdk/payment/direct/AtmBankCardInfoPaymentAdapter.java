package com.zing.zalo.zalosdk.payment.direct;

import java.io.IOException;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.zing.zalo.zalosdk.common.UiHelper.MYDatePickerDialog;
import com.zing.zalo.zalosdk.payment.R;
import com.zing.zalo.zalosdk.view.ATMCardInfoXMLParser;
import com.zing.zalo.zalosdk.view.CommonXMLParser;

public class AtmBankCardInfoPaymentAdapter extends CommonPaymentAdapter {

	String zacTranxID;
	String mac;
	String bankCode;
	int atmFlag;
	MYDatePickerDialog datePicker;
	CommonXMLParser xmlParser;
	public AtmBankCardInfoPaymentAdapter(PaymentChannelActivity owner) {
		super(owner);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.zalosdk_activity_atm_card_info;
	}

	
	@Override
	protected void initPage() {
		try {
			xmlParser = new ATMCardInfoXMLParser(owner); 
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
		mac = gVar.getString("mac", "");
		bankCode = gVar.getString("bankCode", "");
		atmFlag = gVar.getInt("atmFlag", 1);
		if (1 == (1 & atmFlag)) {
			owner.findViewById(R.id.zalosdk_card_password_ctn_ctl).setVisibility(View.VISIBLE);			
		}
		if (2 == (2 & atmFlag) || 4 == (4 & atmFlag)) {
			initCardDate();
		}
		
	}
	
	
	protected void initCardDate() {
		String title = "Ngày phát hành:";
		if (4 == (4 & atmFlag)) {
			title = "Ngày hết hạn:";
			owner.findViewById(R.id.zalosdk_month_ctl).setTag("hết hạn");
			owner.findViewById(R.id.zalosdk_year_ctl).setTag("hết hạn");
		} else {
			owner.findViewById(R.id.zalosdk_month_ctl).setTag("phát hành");
			owner.findViewById(R.id.zalosdk_year_ctl).setTag("phát hành");
		}
		((TextView)owner.findViewById(R.id.zalosdk_card_date_lb_ctl)).setText(title);		
		owner.findViewById(R.id.zalosdk_card_publish_date_ctn_ctl).setVisibility(View.VISIBLE);		
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
	}
	
	@Override
	protected PaymentTask getPaymentTask() {
		SubmitAtmCardInfoTask task = new SubmitAtmCardInfoTask();
		task.owner = this;
		task.zacTranxID = zacTranxID;
		task.mac = mac;
		task.atmFlag = atmFlag;
		task.bankCode = bankCode;
		return task;
	}

	@Override
	protected void onBackCtl() {
		PaymentAdapterFactory.nextAdapter(this, 0);
	}	
}
