package com.zing.zalo.zalosdk.payment.direct;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.EditText;

import com.zing.zalo.zalosdk.common.DeviceHelper;
import com.zing.zalo.zalosdk.http.HttpClientRequestEx;
import com.zing.zalo.zalosdk.http.HttpClientRequestEx.Type;
import com.zing.zalo.zalosdk.model.AbstractView;
import com.zing.zalo.zalosdk.model.ZEditView;
import com.zing.zalo.zalosdk.payment.R;
import com.zing.zalo.zalosdk.payment.direct.CommonPaymentAdapter.PaymentTask;

class SubmitAtmCardOtpTask implements PaymentTask {
	private final String _paymentUrl = "http://dev.atm.credits.zaloapp.com/dbcon-otp?";
	
	public AtmCardOtpPaymentAdapter owner;
	String zacTranxID;
	String mac;
	String bankCode;
	String value;
	@Override
	public JSONObject execute() {
		List<AbstractView> list = owner.xmlParser.getFactory().getListAbstractViews();
		StringBuilder paramDynamic = new StringBuilder();
		int i=0;
		for (AbstractView abstractView : list) {
			if (abstractView.getClass().getSimpleName().equals("ZEditView")) {
				ZEditView e = (ZEditView) abstractView;
				if (e.isRequire()) {
					EditText editText = (EditText)owner.owner.findViewById(R.id.view_root).findViewWithTag(e.getParam());
					value = editText.getText().toString().replaceAll("\\s", "");
					int id = Integer.MAX_VALUE/3 + i++ ;
					editText.setId(id);
					
					if (value.isEmpty()) {
						JSONObject result = new JSONObject();
						try {
							result.put("resultCode", Integer.MIN_VALUE);
							result.put("errorStep", 2);
							result.put("resultMessage", e.getErrClientMess());
						} catch (JSONException t) {
							t.printStackTrace();
						}
						return result;
					}
					try {
						value = URLEncoder.encode(value, "UTF-8");
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					paramDynamic.append(e.getParamValue(value));
				}
			}
		}
//		String otp = owner.getValue(R.id.zalosdk_otp_ctl).replaceAll("\\s", "");
//		if (otp.isEmpty()) {
//			JSONObject result = new JSONObject();
//			try {
//				result.put("resultCode", Integer.MIN_VALUE);
//				result.put("errorStep", 2);
//				result.put("shouldStop", false);
//				result.put("resultMessage", "Bạn chưa nhập mã xác nhận");
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			return result;
//		}
//		try {
//			otp = URLEncoder.encode(otp, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		String captCha = owner.getValue(R.id.zalosdk_captchar_ctl).replaceAll("\\s", "");
//		if (captCha.isEmpty()) {
//			JSONObject result = new JSONObject();
//			try {
//				result.put("resultCode", Integer.MIN_VALUE);
//				result.put("errorStep", 2);
//				result.put("shouldStop", false);
//				result.put("resultMessage", "Bạn chưa nhập mã bảo mật");
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			return result;
//		}
//		try {
//			captCha = URLEncoder.encode(captCha, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		StringBuilder sb = new StringBuilder();
		sb.append(_paymentUrl);
		sb.append("receiptID=").append(zacTranxID);
		sb.append("&mac=").append(mac);
		sb.append("&orderNo=").append(zacTranxID);
		//sb.append("&otp=").append(otp);
		sb.append("&bankCode=").append(bankCode);
		//sb.append("&captcha=").append(captCha);
		sb.append(paramDynamic.toString());
		sb.append("&UDID=").append(DeviceHelper.getUDID(owner.owner));
		Log.i(getClass().getName(), sb.toString());
		HttpClientRequestEx request = new HttpClientRequestEx(Type.POST, sb.toString());
		return request.getJSON();		
	}
	
	@Override
	public void onCompleted(JSONObject result) {
		if (result != null) {
			try {
				Log.i(getClass().getName(), result.toString());
				if (result.getInt("resultCode") >= 0) {
					GetStatusTask task = new GetStatusTask();
					task.from = result.getString("src");
					task.zacTranxID = result.getString("zacTranxID");
					task.statusUrl = result.getString("statusUrl");
					task.owner = owner;	
					owner.executePaymentTask(task);
				} else {					
					if (result.getInt("errorStep") == 2) {
						owner.onCaptchaChanged(result.optString("captchaPngB64", "").replaceFirst("data:image/png;base64,", ""));
					}
					owner.onPaymentComplete(result);				
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			owner.onPaymentComplete(result);
		}
	}
}