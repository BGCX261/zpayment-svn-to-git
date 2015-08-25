package com.zing.zalo.zalosdk.payment.direct;

import java.util.Iterator;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.zing.zalo.zalosdk.common.ResourceHelper;
import com.zing.zalo.zalosdk.http.HttpClientRequestEx;

public class ZaloPaymentService {
	public static final ZaloPaymentService Instance = new ZaloPaymentService();

	private ZaloPaymentService() {
	}

	private SharedPreferences sp;
	private ZaloPaymentListener paymentListener;
	
	public void pay(Activity owner, ZaloPaymentInfo info, ZaloPaymentListener listener) {
		this.sp = owner.getSharedPreferences("zacCookie", 0);
		this.paymentListener = listener;
		initCookie();
		
		// Update resource file
		new ResourceHelper(owner, info).getResourceFromNetwork();
		
		
	}

	protected ZaloPaymentListener getPaymentListner() {
		return paymentListener;
	}
	
	public void initCookie() {
		try {
			JSONObject cookies = new JSONObject(sp.getString("cookie", "{}"));
			Iterator<?> keys = cookies.keys();
			while (keys.hasNext()) {
				String key = keys.next().toString();
				JSONObject cookie = cookies.optJSONObject(key);
				if (cookie != null) {
					HttpClientRequestEx.addCookie(key,
							cookie.optString("value", ""),
							cookie.optString("path", "/"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void saveCookie() {
		if (HttpClientRequestEx.gCookie == null) {
			HttpClientRequestEx.gCookie = new BasicCookieStore();
		}
		List<Cookie> cookies = HttpClientRequestEx.gCookie.getCookies();
		int count = cookies.size();
		JSONObject jsCookies = new JSONObject();
		for (int i = 0; i < count; i++) {
			Cookie cookie = cookies.get(i);
			JSONObject jsCookie = new JSONObject();
			try {
				jsCookie.put("value", cookie.getValue());
				jsCookie.put("path", cookie.getPath());
				jsCookies.put(cookie.getName(), jsCookie);				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			Editor edit = sp.edit();
			edit.putString("cookie", jsCookies.toString());
			edit.commit();
		} catch (Exception e) {}
	}
}
