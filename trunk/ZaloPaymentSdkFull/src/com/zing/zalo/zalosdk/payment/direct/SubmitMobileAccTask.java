package com.zing.zalo.zalosdk.payment.direct;

import java.net.URLEncoder;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.EditText;
import com.zing.zalo.zalosdk.common.DeviceHelper;
import com.zing.zalo.zalosdk.http.HttpClientRequestEx;
import com.zing.zalo.zalosdk.http.HttpClientRequestEx.Type;
import com.zing.zalo.zalosdk.oauth.ZaloOAuth;
import com.zing.zalo.zalosdk.model.AbstractView;
import com.zing.zalo.zalosdk.model.ZEditView;
import com.zing.zalo.zalosdk.payment.direct.CommonPaymentAdapter.PaymentTask;

class SubmitMobileAccTask implements PaymentTask {
	private final String _paymentUrl = "http://dev.direct.credits.zaloapp.com/dbreq-otp?";
	
	public MobileAccPaymentAdapter owner;
	String value;
	String phone;
	
	@Override
	public JSONObject execute() {
		/*phone = owner.getValue(R.id.zalosdk_phone_ctl).replaceAll("\\s", "");
		if (phone.isEmpty()) {
			JSONObject result = new JSONObject();
			try {
				result.put("resultCode", Integer.MIN_VALUE);
				result.put("errorStep", 2);
				result.put("resultMessage", "Bạn chưa nhập số điện thoại");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return result;
		}*/

		List<AbstractView> list = owner.xmlParser.getFactory().getListAbstractViews();
		StringBuilder paramDynamic = new StringBuilder();
		for (AbstractView abstractView : list) {
			if (abstractView.getClass().getSimpleName().equals("ZEditView")) {
				ZEditView e = (ZEditView) abstractView;
				if (e.isRequire()) {
					value = ((EditText)owner.xmlParser.getViewRoot().findViewWithTag(e.getParam())).getText().toString().replaceAll("\\s", "");
					
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
					
					if (e.isCache()) {
						phone = value;
					}
					paramDynamic.append(e.getParamValue(value));
				}
			}
		}
		ZaloPaymentInfo info = owner.info;
		try {
			Log.i("info 1:", info.toJSONString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(_paymentUrl);
		sb.append("appID=").append(info.appID);
		sb.append("&appTranxID=").append(info.appTranxID);
		sb.append("&appTime=").append(info.appTime);
		sb.append("&amount=").append(info.amount);
		sb.append("&embedData=").append(info.embedData);
		sb.append("&mac=").append(info.mac);
		//sb.append(paramDynamic.toString());
		//sb.append("&inputPhone=").append(value);
		sb.append("&chargeAmount=").append(owner.getCurrentAmount());
		sb.append("&UDID=").append(DeviceHelper.getUDID(owner.owner));
		try {
			if (ZaloOAuth.Instance.getZaloId() < 1 || ZaloOAuth.Instance.getZaloPhoneNumber().isEmpty()) {
				//sb.append("&inputPhone=").append(phone);
				sb.append(paramDynamic.toString());
			} else {
				sb.append("&oauthCode=").append(ZaloOAuth.Instance.getOAuthCode());
			}
			sb.append("&description=").append(URLEncoder.encode(info.description, "utf-8"));			
			if (info.items != null && info.items.size() > 0) {
				JSONArray payItems = new JSONArray();
				for (ZaloPaymentItem item : info.items) {
					JSONObject payItem = new JSONObject();
					payItem.put("itemID", item.itemID);
					payItem.put("itemName", item.itemName);
					payItem.put("itemPrice", item.itemPrice);
					payItem.put("itemQuantity", item.itemQuantity);
					payItems.put(payItem);
				}
				sb.append("&items=").append(URLEncoder.encode(payItems.toString(), "UTF-8"));
			}				
		} catch (Exception ex) {
			Log.e(getClass().getName(), ex.getMessage(), ex);
		}
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
					edit.putString("otpMac", result.optString("receiptMac", ""));
					edit.putString("otpPhone", phone);
					edit.commit();
					PaymentAdapterFactory.nextAdapter(owner, 1);
				} else {
					if (result.getInt("errorStep") != 2) {
						owner.enableInputPhone();
						ZaloOAuth.Instance.unauthenticate();
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