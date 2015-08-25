package com.zing.zalo.zalosdk.payment.direct;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.zing.zalo.zalosdk.common.DeviceHelper;
import com.zing.zalo.zalosdk.http.HttpClientRequestEx;
import com.zing.zalo.zalosdk.http.HttpClientRequestEx.Type;
import com.zing.zalo.zalosdk.payment.direct.CommonPaymentAdapter.PaymentTask;

class GetStatusTask implements PaymentTask {
	public String zacTranxID;
	public String statusUrl;
	public String from;
	public long timeOut = 60000;
	
	public CommonPaymentAdapter owner;
	
	@Override
	public JSONObject execute() {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(URLDecoder.decode(statusUrl, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		sb.append("?zacTranxID=").append(zacTranxID);
		sb.append("&UDID=").append(DeviceHelper.getUDID(owner.owner));
		sb.append("&from=").append(from);		
		HttpClientRequestEx request = new HttpClientRequestEx(Type.POST, sb.toString());
		JSONObject result = null;
		int interval = 1000;
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime <= timeOut) {
			try {
				result = request.getJSON();
				if (result.getInt("e") < 0 || result.getInt("status") == 0) {
					Thread.sleep(interval);
				} else {
					break;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}		
		return request.getJSON();
	}

	@Override
	public void onCompleted(JSONObject result) {
		if (result != null) {
			try {
				result.put("resultCode", 1);
				result.put("shouldStop", true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		owner.onPaymentComplete(result);
	}
}