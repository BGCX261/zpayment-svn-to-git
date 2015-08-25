package com.zing.zalo.zalosdk.payment.direct;

import java.net.URLEncoder;
import java.util.List;

import org.json.JSONArray;
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

class SubmitZingCardTask implements PaymentTask {
	private final String _paymentUrl = "http://dev.zc.credits.zaloapp.com/dbverify?";
	
	public ZingCardPaymentAdapter owner;
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
						owner.showWarningIconInNUIContext(id);
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
					paramDynamic.append(e.getParamValue(value));
				}
			}
		}
		
//		String cardCode = owner.getValue(R.id.zalosdk_card_code_ctl).replaceAll("\\s", "");
//		if (cardCode.isEmpty()) {
//			JSONObject result = new JSONObject();
//			try {
//				result.put("resultCode", Integer.MIN_VALUE);
//				result.put("errorStep", 2);
//				result.put("resultMessage", "Bạn chưa nhập mật mã thẻ");
//				owner.showWarningIconInNUIContext(R.id.zalosdk_card_code_ctl);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			return result;
//		}		
//		String cardSeri = owner.getValue(R.id.zalosdk_card_seri_ctl).replaceAll("\\s", "");
//		if (cardSeri.isEmpty()) {
//			JSONObject result = new JSONObject();
//			try {
//				result.put("resultCode", Integer.MIN_VALUE);
//				result.put("errorStep", 2);
//				result.put("resultMessage", "Bạn chưa nhập số seri thẻ");
//				owner.showWarningIconInNUIContext(R.id.zalosdk_card_seri_ctl);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			return result;
//		}
		
		ZaloPaymentInfo info = owner.info;
		StringBuilder sb = new StringBuilder();
		sb.append(_paymentUrl);
		sb.append("appID=").append(info.appID);
		sb.append("&appTranxID=").append(info.appTranxID);
		sb.append("&appTime=").append(info.appTime);
		sb.append("&amount=").append(info.amount);
		sb.append("&embedData=").append(info.embedData);
		sb.append("&mac=").append(info.mac);
		sb.append(paramDynamic.toString());
		//sb.append("&cardCode=").append(cardCode);
		//sb.append("&cardSerialNo=").append(cardSeri);
		sb.append("&UDID=").append(DeviceHelper.getUDID(owner.owner));
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
		HttpClientRequestEx request = new HttpClientRequestEx(Type.POST, sb.toString());
		return request.getJSON();
	}
	
	@Override
	public void onCompleted(JSONObject result) {
		if (result != null) {
			try {
				Log.i(getClass().getName(), result.toString());
				int code = result.getInt("resultCode");
				if (code >= 0) {
					GetStatusTask task = new GetStatusTask();
					task.from = result.getString("src");
					task.zacTranxID = result.getString("zacTranxID");
					task.statusUrl = result.getString("statusUrl");
					task.owner = owner;
					owner.executePaymentTask(task);
				} else {
					if (code == -113220) {
						owner.showWarningIcon(R.id.zalosdk_card_code_ctl);
					} else if (code == -113223) {
						owner.showWarningIcon(R.id.zalosdk_card_seri_ctl);
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