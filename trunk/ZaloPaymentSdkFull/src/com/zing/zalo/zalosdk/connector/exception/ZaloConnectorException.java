package com.zing.zalo.zalosdk.connector.exception;

public class ZaloConnectorException extends Exception {
	public static final int ERROR_NONE = 0;
	public static final int ERROR_SERVICE_NOT_FOUND = 1;
	public static final int ERROR_SERVICE_PERMISSION_DENIED = 2;
	public static final int ERROR_SERVICE_NOT_CONNECTED = 3;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2820924671796181996L;
	
	private int mErrorCode = 0;

	public ZaloConnectorException(String detailMessage) {
		super(detailMessage);
	}
	
	public ZaloConnectorException(int errorCode) {
		mErrorCode = errorCode;
	}
	
	public int getErrorCode() {
		return mErrorCode;
	}
}
