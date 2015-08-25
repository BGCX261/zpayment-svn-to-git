package com.zing.zalo.zalosdk.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentService;

public class HttpClientRequestEx {
	public enum Type {
		GET, POST
	}

	private Type mType;
	private String mUrl;
	public static CookieStore gCookie = new BasicCookieStore();
	private List<NameValuePair> mParams;
	private List<NameValuePair> mHeader;

	public HttpClientRequestEx(Type type, String url) {
		this.mType = type;
		this.mUrl = url;
		this.mHeader = new ArrayList<NameValuePair>();
		this.mParams = new ArrayList<NameValuePair>();		
	}

	public void addHeader(String name, String value) {
		mHeader.add(new BasicNameValuePair(name, value));
	}

	public void addParams(String name, String value) {
		mParams.add(new BasicNameValuePair(name, value));
	}

	public static void addCookie(String name, String value, String domain) {
		BasicClientCookie cookie = new BasicClientCookie(name, value);
		cookie.setDomain(domain);
		cookie.setPath("/");
		gCookie.addCookie(cookie);
	}

	public InputStream getInputStream() {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));

		HttpParams params = new BasicHttpParams();
		params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
		params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

		ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);

		HttpClient client = new DefaultHttpClient(cm, params);
		
		try {
			HttpUriRequest request;
			switch (mType) {
			case GET:
				StringBuilder sb = new StringBuilder(mUrl);
				sb.append("?platform=android");
				for (NameValuePair item : mParams) {
					sb.append("&");
					sb.append(item.getName());
					sb.append("=");
					String encodeData = URLEncoder.encode(item.getValue(),"UTF-8").replace("+", "%20");
					sb.append(encodeData);
				}
				request = new HttpGet(sb.toString());
				break;
			default:
				HttpPost post = new HttpPost(mUrl);
				if (!mParams.isEmpty()) {
					post.setEntity(new UrlEncodedFormEntity(mParams));
				}
				request = post;
				break;
			}
			for (NameValuePair item : mHeader) {
				request.setHeader(item.getName(), item.getValue());
			}
			HttpContext context = new BasicHttpContext();			
			context.setAttribute(ClientContext.COOKIE_STORE, gCookie);			
			HttpEntity entity = client.execute(request, context).getEntity();
			ZaloPaymentService.Instance.saveCookie();
			if (entity != null) {
				return entity.getContent();
			}
		} catch (Exception ex) {
			Log.e(getClass().getName(), ex.getMessage(), ex);
		}
		return null;
	}

	public String getText() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream()));
			String line;
			StringBuilder result = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				result.append(line);
				result.append("\n");
			}
			reader.close();
			return result.toString();
		} catch (Exception ex) {
			Log.e(getClass().getName(), ex.getMessage(), ex);
			return null;
		}
	}

	public JSONObject getJSON() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream()));
			String line;
			StringBuilder result = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				result.append(line);
				result.append("\n");
			}
			reader.close();
			return new JSONObject(result.toString());
		} catch (Exception ex) {
			Log.e(getClass().getName(), ex.getMessage(), ex);
			return null;
		}
	}

	public Bitmap getImage() {
		try {
			InputStream inputStream = getInputStream();
			Bitmap image =null;
			if (inputStream != null) {
				 image = BitmapFactory.decodeStream(inputStream);
				 inputStream.close();
			}
			return image;
		} catch (Exception ex) {
			Log.e(getClass().getName(), ex.getMessage(), ex);
			return null;
		}
	}
}
