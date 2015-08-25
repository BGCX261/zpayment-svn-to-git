package com.zing.zalo.zalosdk.payment.direct;

public enum ZaloPaymentStatus {
	/**
	 * Transaction is processing
	 */
	ZAC_TRANXSTATUS_PROCESSING, 
	/**
	 * Transaction success(confirmed by application's server).
	 */
	ZAC_TRANXSTATUS_SUCCESS, 
	/**
	 * Transaction failed.
	 */	
	ZAC_TRANXSTATUS_FAIL;

	static ZaloPaymentStatus findById(int id) {
		switch (id) {
		case 1:
			return ZAC_TRANXSTATUS_SUCCESS;
		case 0:
			return ZAC_TRANXSTATUS_PROCESSING;
		}
		return ZAC_TRANXSTATUS_FAIL;
	}
}
