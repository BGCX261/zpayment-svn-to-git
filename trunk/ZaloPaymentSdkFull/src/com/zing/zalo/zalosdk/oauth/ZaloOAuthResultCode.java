package com.zing.zalo.zalosdk.oauth;


public class ZaloOAuthResultCode {
	public static int RESULTCODE_USER_BACK = -1111;
	public static int RESULTCODE_USER_REJECT = -1114;
	public static int RESULTCODE_ZALO_UNKNOWN_ERROR = -1112;
	public static int RESULTCODE_UNEXPECTED_ERROR = -1000;
	public static int RESULTCODE_INVALID_APP_ID = -1001;
	public static int RESULTCODE_INVALID_PARAM = -1002;
	public static int RESULTCODE_INVALID_SECRET_KEY = -1003;
	public static int RESULTCODE_INVALID_OAUTH_CODE = -1004;
	public static int RESULTCODE_ACCESS_DENIED = -1005;
	public static int RESULTCODE_INVALID_SESSION = -1006;
	public static int RESULTCODE_CREATE_OAUTH_FAILED = -1007;
	public static int RESULTCODE_CREATE_ACCESS_TOKEN_FAILED = -1008;
	public static int RESULTCODE_USER_CONSENT_FAILED = -1009;
	
	public static int findById(int rawCode){
		switch(rawCode){
		case 1:
			return RESULTCODE_ZALO_UNKNOWN_ERROR;
		case 3:
			return RESULTCODE_USER_REJECT;
		case -1000:
			return RESULTCODE_UNEXPECTED_ERROR;
		case -1001:
			return RESULTCODE_INVALID_APP_ID;
		case -1002:
			return RESULTCODE_INVALID_PARAM;
		case -1003:
			return RESULTCODE_INVALID_SECRET_KEY;
		case -1004:
			return RESULTCODE_INVALID_OAUTH_CODE;
		case -1005:
			return RESULTCODE_ACCESS_DENIED;
		case -1006:
			return RESULTCODE_INVALID_SESSION;
		case -1007:
			return RESULTCODE_CREATE_OAUTH_FAILED;
		case -1008:
			return RESULTCODE_CREATE_ACCESS_TOKEN_FAILED;
		case -1009:
			return RESULTCODE_USER_CONSENT_FAILED;
		default:
			return rawCode;
		}
	}
}