package com.zing.zalo.zalosdk.payment.direct;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.zing.zalo.zalosdk.payment.R;
import com.zing.zalo.zalosdk.payment.direct.CommonPaymentAdapter.PaymentTask;

class SubmitSmlCardVcbAccTask implements PaymentTask {
	
	public SmlCardPaymentAdapter owner;
	String zacTranxID;
	String mac;
	String bankCode;	
	int atmFlag = 1;
	String payUrl;
	
	@Override
	public JSONObject execute() {
		String vcbAccountName = owner.getValue(R.id.zalosdk_acc_name_ctl).trim();
		if (vcbAccountName.isEmpty()) {
			JSONObject result = new JSONObject();
			try {
				result.put("resultCode", Integer.MIN_VALUE);
				result.put("errorStep", 2);
				result.put("shouldStop", false);
				result.put("resultMessage", "Tên truy cập VCB-iB@nking");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return result;
		}
		vcbAccountName = vcbAccountName.replace("'", "\'");
		vcbAccountName = vcbAccountName.replace("\"", "\\\"");
		String vcbPassword = owner.getValue(R.id.zalosdk_acc_password_ctl).replaceAll("\\s", "");
		if (vcbPassword.isEmpty()) {
			JSONObject result = new JSONObject();
			try {
				result.put("resultCode", Integer.MIN_VALUE);
				result.put("errorStep", 2);
				result.put("shouldStop", false);
				result.put("resultMessage", "Mật khẩu VCB-iB@nking");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return result;
		}
		try {
			vcbPassword = URLEncoder.encode(vcbPassword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String vcbCaptcha = (String) owner.getValue(R.id.zalosdk_acc_captchar_ctl);
		if (vcbCaptcha.isEmpty()) {
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
		vcbCaptcha = vcbCaptcha.replace("'", "\'");
		vcbCaptcha = vcbCaptcha.replace("\"", "\\\"");
		
		StringBuilder sb = new StringBuilder();
		sb.append("javascript:{");
		sb.append("var ec = document.getElementById('ctl00__Default_Content_Center_Chk_Terms'); var ocfunc = ec['onclick'];ec.checked='false'; if (typeof(ocfunc) == 'function') {ocfunc.call(ec);};");
		sb.append("document.getElementById('ctl00__Default_Content_Center_TenTC').value='");
		sb.append(vcbAccountName);
		sb.append("';");
		sb.append("document.getElementById('ctl00__Default_Content_Center_MatKH').value='");
		sb.append(vcbPassword);
		sb.append("';");
		sb.append("document.getElementById('ctl00__Default_Content_Center_Random_Img_Str').value='");
		sb.append(vcbCaptcha);
		sb.append("';");
		sb.append("document.getElementById('ctl00__Default_Content_Center_Confirm').click();");
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