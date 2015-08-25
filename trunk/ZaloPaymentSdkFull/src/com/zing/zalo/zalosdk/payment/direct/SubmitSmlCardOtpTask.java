package com.zing.zalo.zalosdk.payment.direct;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.zing.zalo.zalosdk.payment.R;
import com.zing.zalo.zalosdk.payment.direct.CommonPaymentAdapter.PaymentTask;

class SubmitSmlCardOtpTask implements PaymentTask {
	
	public SmlCardPaymentAdapter owner;
	String zacTranxID;
	String mac;
	String bankCode;	
	int atmFlag = 1;
	String payUrl;
	
	@Override
	public JSONObject execute() {
		String otp = owner.getValue(R.id.zalosdk_otp_ctl).replaceAll("\\s", "");
		if (otp.isEmpty()) {
			JSONObject result = new JSONObject();
			try {
				result.put("resultCode", Integer.MIN_VALUE);
				result.put("errorStep", 2);
				result.put("shouldStop", false);
				result.put("resultMessage", "Bạn chưa nhập mã xác nhận");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return result;
		}
		try {
			otp = URLEncoder.encode(otp, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String captCha = owner.getValue(R.id.zalosdk_captchar_ctl).replaceAll("\\s", "");
		if (captCha.isEmpty()) {
			JSONObject result = new JSONObject();
			try {
				result.put("resultCode", Integer.MIN_VALUE);
				result.put("errorStep", 2);
				result.put("shouldStop", false);
				result.put("resultMessage", "Bạn chưa nhập mã bảo mật");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return result;
		}
		try {
			captCha = URLEncoder.encode(captCha, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("javascript:{");
		sb.append("document.getElementById('otp').value='");
		sb.append(otp);
		sb.append("';");
		sb.append("document.getElementById('otpimg').value='");
		sb.append(captCha);
		sb.append("';");
		sb.append("var form=document.getElementsByTagName('form')[0];");		
		sb.append("form.onsubmit=null;");
		sb.append("form.submit();");
		sb.append("};");	
		payUrl = sb.toString();
		return null;
	}
	
	@Override
	public void onCompleted(JSONObject result) {
		if (result != null) {
			owner.onPaymentComplete(result);
		} else {
			Log.i(getClass().getName(), payUrl);
			owner.paymentBridge.loadUrl(payUrl);
		}
	}
}