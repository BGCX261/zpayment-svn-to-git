package com.zing.zbookstore.directpay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.zing.zalo.zalosdk.oauth.ZaloOAuth;
import com.zing.zalo.zalosdk.oauth.common.OAuthCompleteListener;

public class SplashActivity extends Activity implements OnClickListener {
	private final int authenRequestCode = 1;
	private final int exitRequestCode = 2;	
	private View reloadCtl = null;
	
	private LoginListener listener;
	private class LoginListener extends OAuthCompleteListener {

		@Override
		public void onAuthenError(int errorCode) {
			switch (errorCode) {
			case 2:
				finish();
				break;
			case -1004:
				ZaloOAuth.Instance.unauthenticate();
				ZaloOAuth.Instance.authenticate(SplashActivity.this, authenRequestCode);
				break;
			default:
				break;
			}
			super.onAuthenError(errorCode);
		}

		@Override
		public void onGetOAuthComplete(long uId, String oauthCode) {
			super.onGetOAuthComplete(uId, oauthCode);
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			startActivityForResult(intent, exitRequestCode);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		reloadCtl = findViewById(R.id.reload_bt);
		reloadCtl.setOnClickListener(this);
		reloadCtl.setVisibility(View.INVISIBLE);
		authen();
	}	
	
	private void authen() {
		listener = new LoginListener();
		ZaloOAuth.initialize(AppInfo.appID, this);
		ZaloOAuth.Instance.setOAuthCompleteListener(listener);
		ZaloOAuth.Instance.authenticate(this, authenRequestCode);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {	
		case authenRequestCode:
			reloadCtl.setVisibility(View.VISIBLE);
			ZaloOAuth.Instance.receiveOAuthData(data);
			AppInfo.reAuthenticate = false;
			break;
		default:
			if (AppInfo.reAuthenticate) {
				ZaloOAuth.Instance.authenticate(this, authenRequestCode);
			} else {
				finish();
			}
			break;
		}
	}
	
	@Override
	public void onClick(View arg0) {
		authen();
	}
}
