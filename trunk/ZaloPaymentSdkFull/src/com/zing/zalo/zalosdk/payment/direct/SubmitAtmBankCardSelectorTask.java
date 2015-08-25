package com.zing.zalo.zalosdk.payment.direct;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import com.zing.zalo.zalosdk.model.AbstractView;
import com.zing.zalo.zalosdk.model.ZEditView;
import com.zing.zalo.zalosdk.payment.direct.CommonPaymentAdapter.PaymentTask;

class SubmitAtmBankCardSelectorTask implements PaymentTask {
	private final String _paymentUrl = "http://dev.atm.credits.zaloapp.com/dbcreate-order?";
	
	public ATMBankCardSelectorPaymentAdapter owner;
	String intputMoney;
	String value;
	@Override
	public JSONObject execute() {
		
//		intputMoney = owner.getValue(R.id.zalosdk_money_ctl).replaceAll("\\s", "");
//		if (intputMoney.isEmpty()) {
//			JSONObject result = new JSONObject();
//			try {
//				result.put("resultCode", Integer.MIN_VALUE);
//				result.put("errorStep", 2);
//				result.put("resultMessage", "Bạn chưa cho biết số tiền cần nạp");
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			return result;
//		}		

		ZaloPaymentInfo info = owner.info;
		StringBuilder paramDynamic = new StringBuilder();
		if (info.amount <= 0) {
			List<AbstractView> list = owner.xmlParser.getFactory().getListAbstractViews();
			
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
							intputMoney = value;
						}
						paramDynamic.append(e.getParamValue(value));
					}
				}
			}	
		} else {
			intputMoney = String.valueOf(info.amount);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(_paymentUrl);
		sb.append("appID=").append(info.appID);
		sb.append("&UDID=").append(DeviceHelper.getUDID(owner.owner));
		sb.append("&appTranxID=").append(info.appTranxID);
		sb.append("&appTime=").append(info.appTime);
		sb.append("&amount=").append(info.amount);
		sb.append("&embedData=").append(info.embedData);
		sb.append("&mac=").append(info.mac);
		sb.append("&chargeAmount=").append(intputMoney);
		//sb.append(paramDynamic.toString());
		sb.append("&bankCode=").append(owner.getCurrentCardCode());
		try {
			sb.append("&description=").append(URLEncoder.encode(info.description, "utf-8"));			
			if (info.items != null) {
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
		owner.saveCurrentBank();
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
					String bankCode = owner.getCurrentCardCode();
					SharedPreferences gVar = owner.owner.getSharedPreferences("zacPref", 0);
					Editor edit = gVar.edit();
					edit.putString("zacTranxID", result.optString("zacTranxID", ""));
					edit.putString("mac", result.optString("receiptMac", ""));
					try {
						edit.putString("payUrl", URLDecoder.decode(result.optString("payUrl", ""), "UTF-8"));
						edit.putString("statusUrl", URLDecoder.decode(result.optString("statusUrl", ""), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					edit.putString("bankCode", bankCode);
					edit.putString("intputMoney", intputMoney);
					edit.putInt("pageId", 0);
					edit.putInt("atmFlag", result.optInt("option", 1));				
					edit.commit();
					String src = result.optString("src", "aapi");
					edit.putString("from", src);
					if (src.equalsIgnoreCase("aapi")) {
						PaymentAdapterFactory.nextAdapter(owner, 1);
					} else if (src.equalsIgnoreCase("asml")) {
						PaymentAdapterFactory.nextAdapter(owner, 11);
					}
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