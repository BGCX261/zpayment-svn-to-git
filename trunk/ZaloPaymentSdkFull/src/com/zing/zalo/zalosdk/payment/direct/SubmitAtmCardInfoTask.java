package com.zing.zalo.zalosdk.payment.direct;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.EditText;
import com.zing.zalo.zalosdk.common.DeviceHelper;
import com.zing.zalo.zalosdk.http.HttpClientRequestEx;
import com.zing.zalo.zalosdk.http.HttpClientRequestEx.Type;
import com.zing.zalo.zalosdk.model.AbstractView;
import com.zing.zalo.zalosdk.model.ZEditView;
import com.zing.zalo.zalosdk.model.ZHiddenEditView;
import com.zing.zalo.zalosdk.payment.R;
import com.zing.zalo.zalosdk.payment.direct.CommonPaymentAdapter.PaymentTask;

class SubmitAtmCardInfoTask implements PaymentTask {
	private final String _paymentUrl = "http://dev.atm.credits.zaloapp.com/dbsubmit-card?";
	
	public AtmBankCardInfoPaymentAdapter owner;
	String zacTranxID;
	String mac;
	String bankCode;	
	int atmFlag = 1;
	String value;
	@Override
	public JSONObject execute() {
		String cardPassword = "";
		String cardMonth = "";
		String cardYear = "";
		List<AbstractView> list = owner.xmlParser.getFactory().getListAbstractViews();
		StringBuilder paramDynamic = new StringBuilder();
		for (AbstractView abstractView : list) {
			if (abstractView.getClass().getSimpleName().equals("ZEditView")) {
				ZEditView e = (ZEditView) abstractView;
				if (e.isRequire()) {
					//TODO check regular Expressions
					value = ((EditText)owner.xmlParser.getViewRoot().findViewWithTag(e.getParam())).getText().toString();//.replaceAll("\\s", "");
					
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
					} catch (UnsupportedEncodingException t) {
						t.printStackTrace();
					}
					paramDynamic.append(e.getParamValue(value));
				}
			} else if (abstractView.getClass().getSimpleName().equals("ZHiddenEditView")) {
				ZHiddenEditView h = (ZHiddenEditView) abstractView;
				
				int id = h.getId();
				if (id == R.id.card_password_ctl) {
					cardPassword = owner.getValue(R.id.card_password_ctl);
					paramDynamic.append(h.getParamValue(cardPassword));
				} else if (id == R.id.zalosdk_month_ctl) {
					cardMonth = (String) owner.getValue(R.id.zalosdk_month_ctl);
					paramDynamic.append(h.getParamValue(cardMonth));
				} else if (id == R.id.zalosdk_year_ctl) {
					cardYear = (String) owner.getValue(R.id.zalosdk_year_ctl);	
					paramDynamic.append(h.getParamValue(cardYear));
				}
				
			}
		}
		
//		String cardHolderName = owner.getValue(R.id.zalosdk_card_holder_name_ctl).trim();
//		if (cardHolderName.isEmpty()) {
//			JSONObject result = new JSONObject();
//			try {
//				result.put("resultCode", Integer.MIN_VALUE);
//				result.put("errorStep", 2);
//				result.put("shouldStop", false);
//				result.put("resultMessage", "Bạn chưa nhập tên chủ thẻ");
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			return result;
//		}
//		try {
//			cardHolderName = URLEncoder.encode(cardHolderName, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		String cardNumber = owner.getValue(R.id.zalosdk_card_code_ctl).replaceAll("\\s", "");
//		if (cardNumber.isEmpty()) {
//			JSONObject result = new JSONObject();
//			try {
//				result.put("resultCode", Integer.MIN_VALUE);
//				result.put("errorStep", 2);
//				result.put("shouldStop", false);
//				result.put("resultMessage", "Bạn chưa nhập mã số thẻ");
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			return result;
//		}
//		try {
//			cardNumber = URLEncoder.encode(cardNumber, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}

		if (1 == (1 & atmFlag)) {
			if (cardPassword.isEmpty()) {
				JSONObject result = new JSONObject();
				try {
					result.put("resultCode", Integer.MIN_VALUE);
					result.put("errorStep", 2);
					result.put("shouldStop", false);
					result.put("resultMessage", "Bạn chưa nhập mật mã thẻ");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return result;
			}
			try {
				cardPassword = URLEncoder.encode(cardPassword, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (2 == (2 & atmFlag) || 4 == (4 & atmFlag)) {
			int tmp = 0;
			try {
				tmp = Integer.parseInt(cardMonth);
			} catch (Exception e) {}
			if (tmp <= 0 || tmp > 12) {
				String tag = (String) owner.getTag(R.id.zalosdk_month_ctl);
				JSONObject result = new JSONObject();
				try {
					result.put("resultCode", Integer.MIN_VALUE);
					result.put("errorStep", 2);
					result.put("shouldStop", false);
					if (cardMonth.isEmpty()) {
						result.put("resultMessage", "Bạn chưa nhập tháng " + tag);
					} else {
						result.put("resultMessage", "Tháng " + tag + "không hợp lệ");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return result;
			}
			try {
				tmp = Integer.parseInt(cardYear);
			} catch (Exception e) {}
			if (tmp < 0 || tmp > 99) {
				String tag = (String) owner.getTag(R.id.zalosdk_year_ctl);
				JSONObject result = new JSONObject();
				try {
					result.put("resultCode", Integer.MIN_VALUE);
					result.put("errorStep", 2);
					result.put("shouldStop", false);
					if (cardMonth.isEmpty()) {
						result.put("resultMessage", "Bạn chưa nhập năm " + tag);
					} else {
						result.put("resultMessage", "Năm " + tag + "không hợp lệ");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return result;
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(_paymentUrl);
		sb.append("receiptID=").append(zacTranxID);
		sb.append("&UDID=").append(DeviceHelper.getUDID(owner.owner));
		sb.append("&mac=").append(mac);
		sb.append("&orderNo=").append(zacTranxID);
		sb.append("&bankCode=").append(bankCode);
		sb.append(paramDynamic.toString());
		//sb.append("&cardHolderName=").append(cardHolderName);
		//sb.append("&cardNumber=").append(cardNumber);
//		sb.append("&cardMonth=").append(cardMonth);
//		sb.append("&cardYear=").append(cardYear);
//		sb.append("&cardPassword=").append(cardPassword);
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
					owner.processingDlg.hide();
					SharedPreferences gVar = owner.owner.getSharedPreferences("zacPref", 0);
					Editor edit = gVar.edit();
					edit.putString("zacTranxID", result.optString("zacTranxID", ""));
					edit.putString("mac", result.optString("receiptMac", ""));
					edit.putString("bankCode", bankCode);
					edit.putString("captchaPngB64", result.optString("captchaPngB64", "").replaceFirst("data:image/png;base64,", ""));
					edit.commit();
					PaymentAdapterFactory.nextAdapter(owner, 2);
				} else {					
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