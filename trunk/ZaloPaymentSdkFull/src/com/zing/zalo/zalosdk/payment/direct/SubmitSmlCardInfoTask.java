package com.zing.zalo.zalosdk.payment.direct;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.EditText;

import com.zing.zalo.zalosdk.model.AbstractView;
import com.zing.zalo.zalosdk.model.ZEditView;
import com.zing.zalo.zalosdk.payment.R;
import com.zing.zalo.zalosdk.payment.direct.CommonPaymentAdapter.PaymentTask;

class SubmitSmlCardInfoTask implements PaymentTask {
	
	public SmlCardPaymentAdapter owner;
	String zacTranxID;
	String mac;
	String bankCode;	
	int atmFlag = 1;
	String payUrl;
	String value;
	@Override
	public JSONObject execute() {
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
					value = value.replace("'", "\'");
					value = value.replace("\"", "\\\"");
					paramDynamic.append(e.getScriptParamValue(value));
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
//		cardHolderName = cardHolderName.replace("'", "\'");
//		cardHolderName = cardHolderName.replace("\"", "\\\"");
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
		String cardPassword = owner.getValue(R.id.card_password_ctl);
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
		String cardMonth = (String) owner.getValue(R.id.zalosdk_month_ctl);
		String cardYear = (String) owner.getValue(R.id.zalosdk_year_ctl);		
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
		sb.append("javascript:{");
		sb.append(paramDynamic.toString());
//		sb.append("document.getElementById('cardHolderName').value='");
//		sb.append(cardHolderName);
//		sb.append("';");
//		sb.append("document.getElementById('cardNumber').value='");
//		sb.append(cardNumber);
//		sb.append("';");
		sb.append("document.getElementById('cardMonth').value='");
		sb.append(cardMonth);
		sb.append("';");
		sb.append("document.getElementById('cardYear').value='");
		sb.append(cardYear);
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