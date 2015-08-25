package com.zing.zalo.zalosdk.payment.direct;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;

import com.zing.zalo.zalosdk.common.BitmapHelper;
import com.zing.zalo.zalosdk.model.AbstractView;
import com.zing.zalo.zalosdk.model.ZImageView;
import com.zing.zalo.zalosdk.payment.R;
import com.zing.zalo.zalosdk.view.ATMCardOtpXMLParser;

public class AtmCardOtpPaymentAdapter extends CommonPaymentAdapter {

	String zacTranxID;
	String mac;
	String bankCode;
	ATMCardOtpXMLParser xmlParser;
	public AtmCardOtpPaymentAdapter(PaymentChannelActivity owner) {
		super(owner);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.zalosdk_activity_atm_card_otp;
	}
	
	protected void onCaptchaChanged(String encodedImage) {
		ImageView iv = (ImageView) owner.findViewById(R.id.zalosdk_captchar_img_ctl);
		iv.setImageBitmap(BitmapHelper.b64ToImage(encodedImage));
		iv.requestLayout();
		List<AbstractView> list = xmlParser.getFactory().getListAbstractViews();

		for (AbstractView abstractView : list) {
			if (abstractView.getClass().getSimpleName().equals("ZImageView")) {
				ZImageView iView = (ZImageView) abstractView;
				if (iView.getType().equals("captcha")) {
					iView.setCaptcha(encodedImage);
				}
			}
		}
	}
	
	@Override
	protected void initPage() {
		try {
			xmlParser = new ATMCardOtpXMLParser(owner); 
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
		String encodedImage = gVar.getString("captchaPngB64", "");
		onCaptchaChanged(encodedImage);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
	}
	
	@Override
	protected PaymentTask getPaymentTask() {
		SubmitAtmCardOtpTask task = new SubmitAtmCardOtpTask();
		task.owner = this;
		task.zacTranxID = zacTranxID;
		task.mac = mac;
		task.bankCode = bankCode;
		return task;
	}
}
