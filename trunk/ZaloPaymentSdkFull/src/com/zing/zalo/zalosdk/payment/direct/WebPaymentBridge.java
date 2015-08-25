package com.zing.zalo.zalosdk.payment.direct;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebPaymentBridge extends WebView {

	private String _jsName;
	boolean shouldHandle = false;

	public WebPaymentBridge(Context context) {
		super(context);
		if (!isInEditMode()) {
			init();
		}
	}

	public WebPaymentBridge(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (!isInEditMode()) {
			init();
		}
	}

	@Override
	public WebBackForwardList saveState(Bundle outState) {
		return super.saveState(outState);
	}

	public void setPaymentJsInteface(Object object) {
		super.addJavascriptInterface(object, _jsName);
	}

	@Override
	public final void addJavascriptInterface(Object object, String name) {
		throw new UnsupportedOperationException();
	}

	public WebPaymentBridge(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode()) {
			init();
		}
	}

	private void init() {
		_jsName = "zac_wpb";
		getSettings().setJavaScriptEnabled(true);
		getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		getSettings()
				.setUserAgentString(
						"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
		getSettings().setBlockNetworkImage(true);
		setWebViewClient(new Processor(this));
		setWebChromeClient(new WebChromeClient());		
	}

	public void initCardDate() {
		StringBuilder sb = new StringBuilder();
		sb.append("javascript:{try {");
		sb.append(_jsName);
		sb.append(".onJsInitCardDate(document.getElementById('cardissdate').innerText);}catch(e){}}");
		Log.i(getClass().getName(), sb.toString());
		loadUrl(sb.toString());
	}

	private void notifyPaymentEvent(boolean shouldStop, int pageId) {
		StringBuilder sb = new StringBuilder();
		sb.append("javascript:{");
		sb.append("var result = {");
		sb.append("pageId:");
		sb.append(pageId);
		sb.append("};");
		sb.append("if(document.getElementsByClassName('error').length > 0) {");
		sb.append("result.shouldStop=");
		sb.append(shouldStop);
		sb.append(";");
		sb.append("result.message=");
		sb.append("(document.getElementsByClassName('error')[0]).innerText");
		sb.append(";};");
		sb.append("try {");
		sb.append("result.date_lb=document.getElementById('cardissdate').innerText;}catch(e){};");
		sb.append(_jsName);
		sb.append(".onJsPaymentResult(JSON.stringify(result));}");
		Log.i(getClass().getName(), sb.toString());
		loadUrl(sb.toString());
	}
	
	private void notifyPaymentEvent(boolean shouldStop, int pageId, String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("javascript:{");
		sb.append("var result = {");
		sb.append("pageId:");
		sb.append(pageId);
		sb.append(",message:'");
		sb.append(text);
		sb.append("',shouldStop:");
		sb.append(shouldStop);
		sb.append("};");
		sb.append(_jsName);
		sb.append(".onJsPaymentResult(JSON.stringify(result));}");
		Log.i(getClass().getName(), sb.toString());
		loadUrl(sb.toString());
	}
	
	private void notifyVCBPaymentEvent(boolean shouldStop, int pageId) {
		StringBuilder sb = new StringBuilder();
		sb.append("javascript:{");
		sb.append("function isVisible(elem) {return elem && elem.style.visibility != 'hidden' && elem.textContent.trim() != '';}");
		sb.append("var result = {");
		sb.append("pageId:");
		sb.append(pageId);
		sb.append("};");
		sb.append("var eItem = document.getElementById('ctl00__Default_Content_Error_Lbl_Warning');");
		sb.append("if(isVisible(eItem)) {");
		sb.append("result.shouldStop=");
		sb.append(shouldStop);
		sb.append(";");
		sb.append("result.message=");
		sb.append("eItem.innerHTML;");
		sb.append("};");
		sb.append(_jsName);
		sb.append(".onJsPaymentResult(JSON.stringify(result));}");
		Log.i(getClass().getName(), sb.toString());
		loadUrl(sb.toString());
	}

	private void initOtpPage() {
		StringBuilder sb = new StringBuilder();
		sb.append("javascript:{var gI=null;var inputs = document.getElementsByTagName('input');for (var i = 0; i < inputs.length; ++i) {var input=inputs[i]; if(input.type=='image'){gI=input;break;}}; var result = {pageId:2}; if(document.getElementsByClassName('error').length > 0){result.shouldStop=false;result.message=document.getElementsByClassName('error')[0].innerText;};result.otpimg=gI.src;");
		sb.append(_jsName);
		sb.append(".onJsPaymentResult(JSON.stringify(result));}");
		Log.i(getClass().getName(), sb.toString());
		loadUrl(sb.toString());
	}
	
	private void initVCBOtpPage() {
		StringBuilder sb = new StringBuilder();
		sb.append("javascript:{var result = {pageId:12};");
		sb.append(_jsName);
		sb.append(".onJsPaymentResult(JSON.stringify(result));}");
		Log.i(getClass().getName(), sb.toString());
		loadUrl(sb.toString());
	}
	
	private void initVcbAccPage() {
		StringBuilder sb = new StringBuilder();
		sb.append("javascript:{");
		sb.append("function isVisible(elem) {return elem && elem.style.visibility != 'hidden' && elem.innerHTML.trim() != '';}");
		sb.append("var result = {");
		sb.append("pageId:11");
		sb.append("};");
		sb.append("var eItem = document.getElementById('ctl00__Default_Content_Center_CustomValidator1');");
		sb.append("if(isVisible(eItem)) {");
		sb.append("result.message=");
		sb.append("eItem.innerHTML;");
		sb.append("};");
		sb.append("console.log(eItem.outerHTML);");
		sb.append("eItem = document.getElementById('ctl00__Default_Content_Center_Lit_AuthWarn');");
		sb.append("if(isVisible(eItem)) {");
		sb.append("result.message=");
		sb.append("eItem.innerHTML;");
		sb.append("};");
		sb.append("result.captimg='");
		sb.append("_ScriptLibrary/JpegImage.aspx';");
		sb.append("console.log(eItem.outerHTML);");
		sb.append(_jsName);
		sb.append(".onJsPaymentResult(JSON.stringify(result));};");
		Log.i(getClass().getName(), sb.toString());
		loadUrl(sb.toString());
	}
	
	static class Processor extends WebViewClient {
		private final WebPaymentBridge owner;

		public Processor(WebPaymentBridge owner) {
			super();
			this.owner = owner;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Log.i("FP", url);
			super.onPageFinished(view, url);
			if (owner.shouldHandle) {
				int smlIndex = url.indexOf("smartlink");
				if (smlIndex > 0) {
					url = url.substring(smlIndex + 9);
					if (url.indexOf("/message.do") > 0
							|| url.indexOf("/errorex.jsp") > 0) {
						owner.notifyPaymentEvent(true, 1);
						view.stopLoading();
					} else if (url.indexOf("/otp.do") > 0
							|| url.indexOf("/otpcode.do?") > 0) {
						owner.initOtpPage();
					} else if (url.indexOf("/verifycard.do") > 0) {
						owner.notifyPaymentEvent(false, 1);
					}
				} else {
					smlIndex = url.indexOf("vietcombank");
					if (smlIndex > 0) {
						url = url.substring(smlIndex + 11);
						if (url.indexOf("/VPS.aspx") > 0) {
							owner.initVcbAccPage();							
						} else if (url.indexOf("/VPSC.aspx") > 0) {
							owner.initVCBOtpPage();
						} else if (url.indexOf("/Err.aspx") > 0) {
							owner.notifyVCBPaymentEvent(true, 12);
						}
					}
				}
			}
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.i("SP", url);
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.i("OP", url);
			if (url.indexOf("zaloapp.com/atm-callback") > 0) {
				owner.notifyPaymentEvent(true, 3);
				return false;
			}
			if (url.indexOf("/otp.do") > 0 || url.indexOf("/otpcode.do?") > 0) {
				owner.initOtpPage();
			}
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String url) {
			Log.i("EP: " + description, url);
			if (owner.shouldHandle) {
				int pageId = 0;
				int smlIndex = url.indexOf("smartlink");
				if (smlIndex > 0) {
					url = url.substring(smlIndex + 9);
					if (url.indexOf("/message.do") > 0
							|| url.indexOf("/errorex.jsp") > 0) {
						pageId = 1;
					} else if (url.indexOf("/otp.do") > 0
							|| url.indexOf("/otpcode.do?") > 0) {
						pageId = 2;
					} else if (url.indexOf("/verifycard.do") > 0) {
						pageId = 3;
					}
					owner.notifyPaymentEvent(true, pageId, "Không thể kết nối đến máy chủ, vui lòng thử lại sau!");
				}
			}
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			handler.proceed();
		}
	}
}
