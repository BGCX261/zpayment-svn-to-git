package com.zing.zalo.zalosdk.payment.direct;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ZaloPaymentInfo {
	public long appID;
	public String appTranxID;
	public long appTime;
	
	public long amount;
	public List<ZaloPaymentItem> items;	
	public String description;
	public String embedData;
	
	public String mac;
	
	public static ZaloPaymentInfo parse(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		ZaloPaymentInfo info = new ZaloPaymentInfo();
		info.appID = obj.getLong("appID");
		info.appTranxID = obj.getString("appTranxID");
		info.appTime = obj.getLong("appTime");
		info.amount = obj.getLong("amount");
		if (info.amount > 0) {
			info.items = new ArrayList<ZaloPaymentItem>();
			try {
				JSONArray items = obj.getJSONArray("items");
				for (int i = 0; i < items.length(); ++i) {
					ZaloPaymentItem item = new ZaloPaymentItem();
					JSONObject jsItem = items.getJSONObject(i);
					item.itemID = jsItem.getString("itemID");
					item.itemName = jsItem.getString("itemName");
					item.itemPrice = jsItem.getLong("itemPrice");
					item.itemQuantity = jsItem.getInt("itemQuantity");
					info.items.add(item);
				}
				Log.i("info 0:", info.toJSONString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			info.description = obj.getString("description");
		} catch (Exception e) {
			info.description = "";
		}
		try {
			info.embedData = obj.getString("embedData");
		} catch (Exception e) {
			info.embedData = "";
		}
		try {
			info.mac = obj.getString("mac");
		} catch (Exception e) {
			info.mac = "";
		}
		return info;
	}
	
	public String toJSONString() throws JSONException {
		JSONObject obj =  new JSONObject();
		obj.put("appID", appID);
		obj.put("appTranxID", appTranxID);
		obj.put("appTime", appTime);
		obj.put("amount", amount);
		if (items != null && items.size() > 0) {
			JSONArray jsItems = new JSONArray();
			for (int i = 0; i < items.size(); ++i) {
				ZaloPaymentItem item = items.get(i);
				JSONObject jsItem = new JSONObject();
				jsItem.put("itemID", item.itemID);
				jsItem.put("itemName", item.itemName);
				jsItem.put("itemPrice", item.itemPrice);
				jsItem.put("itemQuantity", item.itemQuantity);
				jsItems.put(jsItem);
			}
			obj.put("items", jsItems);
		}
		obj.put("description", description);
		obj.put("embedData", embedData);
		obj.put("mac", mac);		
		return obj.toString();
	}
}
