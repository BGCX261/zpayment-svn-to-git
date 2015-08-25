package com.zing.zalo.zalosdk.oauth;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.zing.zalo.zalosdk.http.HttpClientRequest;
import com.zing.zalo.zalosdk.http.HttpClientRequest.Type;
import com.zing.zalo.zalosdk.oauth.common.Const;
import com.zing.zalo.zalosdk.oauth.common.OAuthCompleteListener;
import com.zing.zalo.zalosdk.oauth.common.PackageUtils;

public class ZaloOAuth {
	public static ZaloOAuth Instance = null;

	public static void initialize(long appID, Activity ac) {
		Instance = new ZaloOAuth(appID, ac);
	}

	private Activity mActivity = null;
	private long mAppId;
	private int mRequestCode;

	private OAuthCompleteListener listener = new OAuthCompleteListener();
	private LocalPreferences mLocalPreferences = null;

	private boolean bIsZaloLoginSuccessful = false;
	private boolean bIsReauthen = false;
	private boolean bIsZaloOutOfDate = false;

	private ProgressDialog mProgressDialog = null;

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Const.AUTHORIZATION_LOGIN_SUCCESSFUL_ACTION.equals(intent
					.getAction())) {
				bIsZaloLoginSuccessful = intent.getBooleanExtra(
						Const.EXTRA_AUTHORIZATION_LOGIN_SUCCESSFUL, false);
			}
		}
	};
	IntentFilter intentFilter = new IntentFilter(
			Const.AUTHORIZATION_LOGIN_SUCCESSFUL_ACTION);

	private ZaloOAuth(long appId, Activity ac) {
		mAppId = appId;
		mActivity = ac;
		mLocalPreferences = new LocalPreferences(mActivity);
	}
	
	public void setOAuthCompleteListener(OAuthCompleteListener listener) {
		this.listener = listener;
	}

	public void receiveOAuthData(Intent data) {
		try {
			mActivity.unregisterReceiver(receiver);
		} catch (Exception e) {			
		}
		if (bIsZaloOutOfDate) {
			return;
		}
		if (data != null) {
			int error = data.getIntExtra("error", Const.RESULT_CODE_SUCCESSFUL);
			if (error == Const.RESULT_CODE_SUCCESSFUL) {
				long id = data.getLongExtra("uid", 0);
				String oauth = data.getStringExtra("code");
				mLocalPreferences.setOAuthCode(oauth);
				mLocalPreferences.setZaloId(id);
				try {
					String jsData = data.getStringExtra("data");
					JSONObject exData = new JSONObject(jsData).getJSONObject("data");
					String phone = exData.getString("phone");
					mLocalPreferences.setZaloPhoneNumber(phone);
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				listener.onGetOAuthComplete(id, oauth);
			} else {
				if (error == Const.RESULT_CODE_ZALO_NOT_LOGIN) {
					bIsReauthen = true;
					resendRequestOAuthIfNeed();
				} else {
					int e = ZaloOAuthResultCode.findById(error);
					listener.onAuthenError(e);
				}
			}
		} else {
			// user press back
			listener.onAuthenError(ZaloOAuthResultCode.RESULTCODE_USER_BACK);
		}
	}

	private void resendRequestOAuthIfNeed() {
		if (bIsReauthen) {
			bIsReauthen = false;

			if (bIsZaloLoginSuccessful) {
				authenticate(mActivity, mRequestCode);
			}
		}
	}
	
	public void authenticate(Activity activity, int requestCode) {
		authenticate(activity, requestCode, null);
	}

	public void authenticate(Activity activity, int requestCode, ProgressDialog dialog) {
		mActivity = activity;		
		if (mLocalPreferences == null) {
			mLocalPreferences = new LocalPreferences(mActivity);			
		}
		if (dialog == null) {
			mProgressDialog = new ProgressDialog(mActivity);
			mProgressDialog.setMessage("Đang tải...");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(true);
		}
		mRequestCode = requestCode;
		String oauth = mLocalPreferences.getOAuthCode();
		if (oauth.length() == 0) {
			sendOAuthRequest(requestCode);
		} else {
			// vadidate oauth code
			ValidateOAuthCodeTask task = new ValidateOAuthCodeTask();
			task.execute();
		}
	}

	private void sendOAuthRequest(int requestCode) {
		if (PackageUtils.isPackageExists(mActivity, Const.ZALO_PACKAGE_NAME)) {
			try {
				Intent i = new Intent(
						"com.zing.zalo.intent.action.THIRD_PARTY_APP_AUTHORIZATION");
				i.putExtra(Intent.EXTRA_UID, mAppId);
				mActivity.registerReceiver(receiver, intentFilter);
				mActivity.startActivityForResult(i, requestCode);
			} catch (ActivityNotFoundException ex) {
				bIsZaloOutOfDate = true;
				listener.onZaloOutOfDate(mActivity);
			}
		} else {
			listener.onZaloNotInstalled(mActivity);
		}
	}

	public void unauthenticate() {
		try{
			mLocalPreferences.setOAuthCode("");
			mLocalPreferences.setZaloId(0);
		}
		catch(Exception ex){
		}
	}

	public String getZaloPhoneNumber() {
		return mLocalPreferences.getZaloPhoneNumber();
	}
	
	public String getOAuthCode() {
		if (mLocalPreferences != null) {
			return mLocalPreferences.getOAuthCode();
		} else {
			return null;
		}
	}

	public long getAppID() {
		return mAppId;
	}

	public long getZaloId() {
		if (mLocalPreferences != null) {
			return mLocalPreferences.getZaloId();
		} else {
			return 0;
		}
	}

	private class LocalPreferences {
		private SharedPreferences localPref;
		private final String PREF_OAUTH_CODE = "PREFERECE_ZALO_SDK_OAUTH_CODE";
		private final String PREF_ZALO_ID = "PREFERECE_ZALO_SDK_ZALO_ID";
		private final String PREF_ZALO_PHONE = "PREFERECE_ZALO_SDK_ZALO_PHONE";
		
		public LocalPreferences(Context c) {
			localPref = PreferenceManager.getDefaultSharedPreferences(c);
		}

		public String getOAuthCode() {
			return localPref.getString(PREF_OAUTH_CODE, "");
		}

		public void setOAuthCode(String code) {
			Editor edit = localPref.edit();
			edit.putString(PREF_OAUTH_CODE, code);
			edit.commit();
		}

		public long getZaloId() {
			return localPref.getLong(PREF_ZALO_ID, 0);
		}
		
		public void setZaloId(long id) {
			Editor edit = localPref.edit();
			edit.putLong(PREF_ZALO_ID, id);
			edit.commit();
		}
		
		public String getZaloPhoneNumber() {
			return localPref.getString(PREF_ZALO_PHONE, "");
		}
		
		public void setZaloPhoneNumber(String phone) {
			Editor edit = localPref.edit();
			edit.putString(PREF_ZALO_PHONE, phone);
			edit.commit();
		}
	}

	private class ValidateOAuthCodeTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(mProgressDialog != null)
				mProgressDialog.show();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpClientRequest request = new HttpClientRequest(Type.GET,
					"https://oauth.zaloapp.com/mobile/v2/validate_oauth_code");
			request.addParams(Const.PARAM_APP_ID, String.valueOf(mAppId));
			request.addParams(Const.PARAM_OAUTH_CODE,
					mLocalPreferences.getOAuthCode());
			return request.getText();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(mProgressDialog != null){
				try{
					if(mProgressDialog.isShowing()){
						mProgressDialog.dismiss();
					}
				}
				catch(Exception ex){}
			}
				
			try {
				JSONObject object = new JSONObject(result);
				int errorCode = object.getInt("error");
				if (errorCode == 0) {
					JSONObject data = object.getJSONObject("data");
					long id = data.getLong("uid");
					String oauth = mLocalPreferences.getOAuthCode();
					listener.onGetOAuthComplete(id, oauth);
				} else {
					unauthenticate();
					sendOAuthRequest(mRequestCode);
				}
			} catch (Exception ex) {
				unauthenticate();
				sendOAuthRequest(mRequestCode);
			}
		}

	}

}
