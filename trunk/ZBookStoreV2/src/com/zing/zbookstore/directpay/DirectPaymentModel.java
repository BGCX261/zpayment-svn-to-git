package com.zing.zbookstore.directpay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.zing.zalo.zalosdk.http.HttpClientRequest;
import com.zing.zalo.zalosdk.http.HttpClientRequest.Type;
import com.zing.zalo.zalosdk.oauth.ZaloOAuth;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentInfo;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentItem;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentListener;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentResultCode;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentService;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentStatus;

public class DirectPaymentModel {
	private final static String billingUrl = "http://dev.demo-zc.zapps.vn/mobile-app/payment/direct-pay?";
	private static Activity owner;
	private static Dialog dlg = null;
	private static CartAdapter cart = null;

	private static String buildParams(String items, long amount,
			String description) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append("oauthCode=").append(ZaloOAuth.Instance.getOAuthCode());
		sb.append("&items=").append(URLEncoder.encode(items, "UTF-8"));
		sb.append("&amount=").append(amount);
		sb.append("&description=").append(description);
		return sb.toString();
	}

	public static void pay(Activity owner, CartAdapter cart, String description) {
		DirectPaymentModel.owner = owner;
		DirectPaymentModel.cart = cart;
		JSONArray payItems = new JSONArray();
		long amount = 0;
		try {
			for (ZaloPaymentItem item : cart.getItems()) {
				JSONObject payItem = new JSONObject();
				payItem.put("itemID", item.itemID);
				payItem.put("itemName", item.itemName);
				payItem.put("itemPrice", item.itemPrice);
				payItem.put("itemQuantity", item.itemQuantity);
				amount += item.itemPrice * item.itemQuantity;
				payItems.put(payItem);
			}
			dlg = new Dialog(owner);
			dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dlg.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			try {
				BillingRequestTask task = new BillingRequestTask();
				task.execute(buildParams(payItems.toString(), amount,
						description));
				dlg.show();
			} catch (Exception e) {
				AppInfo.reAuthenticate = true;
				owner.finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static class BillingRequestTask extends
			AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {
			HttpClientRequest client = new HttpClientRequest(Type.POST,
					billingUrl + params[0]);
			try {
				return client.getJSON();
			} catch (Exception ex) {
				Log.e(getClass().getName(), ex.getMessage(), ex);
				return null;
			}
		}

		@Override
		protected void onPostExecute(JSONObject res) {
			dlg.dismiss();
			dlg = null;
			if (res != null) {
				try {
					int errorCode = res.getInt("errorCode");
					if (errorCode >= 0) {
						JSONObject billInfo = res.getJSONObject("bill");
						JSONArray items = billInfo.getJSONArray("items");
						ArrayList<ZaloPaymentItem> lsItem = new ArrayList<ZaloPaymentItem>(
								items.length());
						for (int i = 0; i < items.length(); ++i) {
							JSONObject item = items.getJSONObject(i);
							ZaloPaymentItem record = new ZaloPaymentItem();
							record.itemID = item.getString("itemID");
							record.itemName = item.getString("itemName");
							record.itemPrice = item.getLong("itemPrice");
							record.itemQuantity = item.getInt("itemQuantity");
							lsItem.add(record);
						}
						ZaloPaymentInfo paymentInfo = new ZaloPaymentInfo();
						paymentInfo.appTime = billInfo.getLong("time");
						paymentInfo.appTranxID = billInfo.getString("id");
						paymentInfo.appID = AppInfo.appID;
						paymentInfo.amount = billInfo.getLong("amount");
						paymentInfo.items = lsItem;
						paymentInfo.description = billInfo
								.getString("description");
						paymentInfo.embedData = billInfo.getString("embedData");
						paymentInfo.mac = billInfo.getString("mac");

						ZaloPaymentService.Instance.pay(owner,
								paymentInfo, new ZaloPaymentListener() {
									@Override
									public void onComplete(
											ZaloPaymentResultCode zpCode,
											ZaloPaymentStatus status,
											long amount) {
										switch (zpCode) {
										case ZAC_RESULTCODE_SUCCESS:
											cart.resetCard();
											break;
										case ZAC_RESULTCODE_FAIL:
											// Failed
											break;
										default:
											break;
										}
									}

									@Override
									public void onCancel() {
										// Do something
									}
								});
					} else {
						switch (errorCode) {
						case -1001: {
							Toast t = Toast
									.makeText(owner, "Phiên làm việc quá hạn",
											Toast.LENGTH_LONG);
							t.show();
							AppInfo.reAuthenticate = true;
							owner.finish();
							break;
						}
						case -1002:
						case -1003: {
							Toast t = Toast.makeText(owner,
									"Hệ thống quá tải, vui lòng thử lại sau",
									Toast.LENGTH_LONG);
							t.show();
							break;
						}
						case -1004: {
							Toast t = Toast.makeText(owner,
									"Dữ liệu thanh toán không hợp lệ",
									Toast.LENGTH_LONG);
							t.show();
							break;
						}
						default: {
							Toast t = Toast
									.makeText(
											owner,
											"Không thể kết nối, vui lòng kiểm tra lại đường truyền",
											Toast.LENGTH_LONG);
							t.show();
							break;
						}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Toast t = Toast
						.makeText(
								owner,
								"Không thể kết nối, vui lòng kiểm tra lại đường truyền",
								Toast.LENGTH_LONG);
				t.show();
			}
		}
	}
}
