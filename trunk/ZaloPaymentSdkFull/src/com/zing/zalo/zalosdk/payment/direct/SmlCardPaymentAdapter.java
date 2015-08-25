package com.zing.zalo.zalosdk.payment.direct;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.zing.zalo.zalosdk.payment.R;
import com.zing.zalo.zalosdk.view.CommonXMLParser;
import com.zing.zalo.zalosdk.view.SmlCardInfoXMLParser;

public class SmlCardPaymentAdapter extends CommonPaymentAdapter {

	String zacTranxID;
	String mac;
	String bankCode;
	String payUrl;
	String from;
	String statusUrl;
	CommonXMLParser xmlParser;
	volatile int pageId;
	
	int atmFlag;
	WebPaymentBridge paymentBridge;

	public SmlCardPaymentAdapter(PaymentChannelActivity owner) {
		super(owner);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.zalosdk_activity_sml_card;
	}

	@Override
	protected void initPage() {
		
		try {
			xmlParser = new SmlCardInfoXMLParser(owner); 
			xmlParser.loadViewFromXml();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		SharedPreferences gVar = owner.getSharedPreferences("zacPref", 0);
		zacTranxID = gVar.getString("zacTranxID", "");
		mac = gVar.getString("mac", "");
		statusUrl = gVar.getString("statusUrl", "");
		from = gVar.getString("from", "");
		bankCode = gVar.getString("bankCode", "");
		atmFlag = gVar.getInt("atmFlag", 1);
		paymentBridge = new WebPaymentBridge(owner);
		paymentBridge.setPaymentJsInteface(this);
		
		payUrl = gVar.getString("payUrl", "");
		pageId = gVar.getInt("pageId", 0);		
		Log.i(getClass().getName(), "1: " + pageId);
		paymentBridge.shouldHandle = false;
		if (pageId == 0) {
			pageId = 1;
			Log.i(getClass().getName(), "reload");
			paymentBridge.shouldHandle = true;
			paymentBridge.loadUrl(payUrl);
		} else if (pageId == 2){
			showOtpPage();
		}
	}

	protected void showOtpPage() {
		owner.findViewById(R.id.zalosdk_back_ctl).setVisibility(View.GONE);
		owner.findViewById(R.id.view_root).setVisibility(View.GONE);
		owner.findViewById(R.id.sml_vcb_login_ctl).setVisibility(View.GONE);
		owner.findViewById(R.id.sml_card_otp_ctl).setVisibility(View.VISIBLE);
		owner.findViewById(R.id.zalosdk_captchar_img_ctl).setVisibility(View.VISIBLE);
		owner.findViewById(R.id.zalosdk_captchar_ctl).setVisibility(View.VISIBLE);
		owner.findViewById(R.id.otp_ok_ctl).setOnClickListener(
		new OnClickListener() {
			@Override
			public void onClick(View v) {
				paymentBridge.shouldHandle = true;
				SubmitSmlCardOtpTask task = new SubmitSmlCardOtpTask();
				task.owner = SmlCardPaymentAdapter.this;
				task.zacTranxID = zacTranxID;
				task.atmFlag = atmFlag;
				task.mac = mac;
				task.bankCode = bankCode;
				processingDlg.show();
				executePaymentTask(task);
			}
		});
	}
	
	protected void showVcbOtpPage() {
		owner.findViewById(R.id.zalosdk_back_ctl).setVisibility(View.GONE);
		owner.findViewById(R.id.view_root).setVisibility(View.GONE);
		owner.findViewById(R.id.sml_vcb_login_ctl).setVisibility(View.GONE);
		owner.findViewById(R.id.zalosdk_captchar_img_ctl).setVisibility(View.GONE);
		owner.findViewById(R.id.zalosdk_captchar_ctl).setVisibility(View.GONE);
		owner.findViewById(R.id.sml_card_otp_ctl).setVisibility(View.VISIBLE);		
		
		owner.findViewById(R.id.otp_ok_ctl).setOnClickListener(
		new OnClickListener() {
			@Override
			public void onClick(View v) {
				paymentBridge.shouldHandle = true;
				SubmitSmlCardVcbOtpTask task = new SubmitSmlCardVcbOtpTask();
				task.owner = SmlCardPaymentAdapter.this;
				task.zacTranxID = zacTranxID;
				task.atmFlag = atmFlag;
				task.mac = mac;
				task.bankCode = bankCode;
				processingDlg.show();
				executePaymentTask(task);
			}
		});
	}
	
	protected void showVcbAccPage(String captImg) {
		owner.findViewById(R.id.zalosdk_back_ctl).setVisibility(View.GONE);
		owner.findViewById(R.id.view_root).setVisibility(View.GONE);
		owner.findViewById(R.id.sml_vcb_login_ctl).setVisibility(View.VISIBLE);
		owner.findViewById(R.id.zalosdk_sml_login_ctl).setOnClickListener(
		new OnClickListener() {
			@Override
			public void onClick(View v) {
				paymentBridge.shouldHandle = true;
				SubmitSmlCardVcbAccTask task = new SubmitSmlCardVcbAccTask();
				task.owner = SmlCardPaymentAdapter.this;
				task.zacTranxID = zacTranxID;
				task.atmFlag = atmFlag;
				task.mac = mac;
				task.bankCode = bankCode;
				processingDlg.show();
				executePaymentTask(task);
			}
		});
		onVcbCaptchaChanged(captImg);
	}

	protected void initCardDate() {
		SharedPreferences gVar = owner.getSharedPreferences("zacPref", 0);
		String title = "Ngày phát hành:";		
		Editor edit = gVar.edit();
		edit.putInt("atmFlag", atmFlag);
		edit.commit();
		if (4 == (4 & atmFlag)) {
			title = "Ngày hết hạn:";
			owner.findViewById(R.id.zalosdk_month_ctl).setTag("hết hạn");
			owner.findViewById(R.id.zalosdk_year_ctl).setTag("hết hạn");
		} else {
			owner.findViewById(R.id.zalosdk_month_ctl).setTag("phát hành");
			owner.findViewById(R.id.zalosdk_year_ctl).setTag("phát hành");
		}
		((TextView)owner.findViewById(R.id.zalosdk_card_date_lb_ctl)).setText(title);		
		owner.findViewById(R.id.zalosdk_card_publish_date_ctn_ctl).setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

	protected void onCaptchaChanged(String imgSrc) {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html><html><head></head><body style='margin:0;padding:0'><img src='").append(imgSrc).append("' style='margin:0;padding:0;' width='120px' alt='' /></body>");
		WebView wv = (WebView) owner.findViewById(R.id.zalosdk_captchar_img_ctl);
		WebSettings webSettings = wv.getSettings(); 
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        wv.setBackgroundColor(Color.TRANSPARENT);
        webSettings.setLoadWithOverviewMode(true);
		wv.loadDataWithBaseURL(paymentBridge.getUrl(), sb.toString(), "text/html", null, null);
	}

	protected void onVcbCaptchaChanged(String imgSrc) {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html><html><head></head><body style='margin:0;padding:0'><img src='").append(imgSrc).append("' style='margin:0;padding:0;' width='120px' alt='' /></body>");
		WebView wv = (WebView) owner.findViewById(R.id.zalosdk_acc_captchar_img_ctl);
		WebSettings webSettings = wv.getSettings(); 
		
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        wv.setBackgroundColor(Color.TRANSPARENT);
        webSettings.setLoadWithOverviewMode(true);
		wv.loadDataWithBaseURL(paymentBridge.getUrl(), sb.toString(), "text/html", null, null);
	}

	
	@Override
	protected void onBackCtl() {
		PaymentAdapterFactory.nextAdapter(this, 0);
	}
	
	@JavascriptInterface
	public void onJsPaymentResult(String result) {
		try {
			final JSONObject obj = new JSONObject(result);
			Log.i(getClass().getName(), obj.toString());
			owner.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						processingDlg.hide();
						pageId = obj.optInt("pageId", 1);						
						Log.i(getClass().getName(), "" + pageId);
						String date_lb = obj.optString("date_lb", "").trim();
						if(!date_lb.isEmpty()) { 
							if ("Ngày phát hành".equals(date_lb)) {
								atmFlag = 2;
							} else {
								atmFlag = 4;
							}
							initCardDate();
						}
						if (obj.optString("message", "").isEmpty()) {
							if (pageId == 2) {
								showOtpPage();
								String otpImg = obj.optString("otpimg", "");
								onCaptchaChanged(otpImg);
							} else if (pageId == 3) {								
								GetStatusTask task = new GetStatusTask();
								task.owner = SmlCardPaymentAdapter.this;
								task.from = from;
								task.statusUrl = statusUrl;
								task.zacTranxID = zacTranxID;
							} else if (pageId == 11) {
								showVcbAccPage(obj.optString("captimg", ""));
							} else if (pageId == 12) {
								showVcbOtpPage();
							}
						} else {
							shouldStop = obj.optBoolean("shouldStop", false);
							alertDlg.showAlert(obj.optString("message", ""));
							if (pageId == 2) {
								String otpImg = obj.optString("otpimg", "");
								onCaptchaChanged(otpImg);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected PaymentTask getPaymentTask() {
		paymentBridge.shouldHandle = true;
		SubmitSmlCardInfoTask task = new SubmitSmlCardInfoTask();
		task.owner = this;
		task.zacTranxID = zacTranxID;
		task.atmFlag = atmFlag;
		task.mac = mac;
		task.bankCode = bankCode;
		return task;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		SharedPreferences gVar = owner.getSharedPreferences("zacPref", 0);
		Editor edit = gVar.edit();
		edit.putInt("pageId", pageId);
		Log.i(getClass().getName(), "" + pageId);
		edit.commit();
		paymentBridge.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		paymentBridge.restoreState(savedInstanceState);
	}
}
