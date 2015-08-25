package com.zing.zalo.zalosdk.oauth.common;

public class Const {
	public static final String PARAM_APP_ID = "app_id";
	public static final String PARAM_OAUTH_CODE = "code";
	public static final String PARAM_TO_UID = "touid";
	public static final String PARAM_MSG_CONTENT = "content";
	public static final String PARAM_FROM = "from";
	public static final String PARAM_COUNT = "count";
	
	public static final String API_GET_PROFILE = "http://staging.3rd.demo.zaloapp.com/api/profile";
	public static final String API_GET_ACCESS_TOKEN = "http://staging.3rd.demo.zaloapp.com/api/access_token";
	public static final String API_SEND_TEXT_MESSAGE = "http://staging.3rd.demo.zaloapp.com/api/sendsms";
	public static final String API_PUSH_TEXT_FEED = "http://staging.3rd.demo.zaloapp.com/api/pushfeed";
	public static final String API_GET_FRIEND_LIST = "http://staging.3rd.demo.zaloapp.com/api/friends";
	
	
	public static final String AUTHORIZATION_LOGIN_SUCCESSFUL_ACTION = "com.zing.zalo.action.ZALO_LOGIN_SUCCESSFUL_FOR_AUTHORIZATION_APP";
	public static final String EXTRA_AUTHORIZATION_LOGIN_SUCCESSFUL = "loginSuccessful";
	
	public static final int RESULT_CODE_UNKNOWN_ERROR = 1;
	public static final int RESULT_CODE_CANCEL = 2;
	public static final int RESULT_CODE_REJECT = 3;
	public static final int RESULT_CODE_ZALO_NOT_LOGIN = 4;
	public static final int RESULT_CODE_SUCCESSFUL = 0;
	
	public static final String ZALO_PACKAGE_NAME = "com.zing.zalo";
}
