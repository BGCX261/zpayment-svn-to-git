package com.zing.zalo.zalosdk.payment.direct;

public enum ZaloPaymentResultCode {
	ZAC_RESULTCODE_UNKNOWN,
	ZAC_RESULTCODE_NETWORK_TIMEOUT,
	ZAC_RESULTCODE_PROCESSING,
	ZAC_RESULTCODE_SUCCESS,
	ZAC_RESULTCODE_FAIL,
	ZAC_RESULTCODE_DUPLICATE,
	ZAC_RESULTCODE_AGENT_NOT_SUPPORT,
	ZAC_RESULTCODE_AMOUNT_NOT_VALID,
	ZAC_RESULTCODE_MISS_PARAM,
	ZAC_RESULTCODE_INVALID_PARAM,
	ZAC_RESULTCODE_SIGNATURE_NOT_MATCH,
	ZAC_RESULTCODE_ZALO_CREDITS_ERROR;
	
	static ZaloPaymentResultCode findById(int id) {
		switch (id) {
		case 0:
			return ZAC_RESULTCODE_UNKNOWN;
		case -2:
		case -3:
		case -4:
			return ZAC_RESULTCODE_AGENT_NOT_SUPPORT;
		case -5:
			return ZAC_RESULTCODE_DUPLICATE;
		case -6:
			return ZAC_RESULTCODE_AMOUNT_NOT_VALID;
		case -8:
			return ZAC_RESULTCODE_MISS_PARAM;
		case -9:
			return ZAC_RESULTCODE_INVALID_PARAM;
		case -12:
			return ZAC_RESULTCODE_SIGNATURE_NOT_MATCH;
		default:
			return ZAC_RESULTCODE_ZALO_CREDITS_ERROR;
		}
	}
	static ZaloPaymentResultCode findByStatus(ZaloPaymentStatus status) {
		switch (status) {
		case ZAC_TRANXSTATUS_SUCCESS:
			return ZaloPaymentResultCode.ZAC_RESULTCODE_SUCCESS;
		case ZAC_TRANXSTATUS_PROCESSING:
			return ZAC_RESULTCODE_PROCESSING;
		default:
			return ZaloPaymentResultCode.ZAC_RESULTCODE_FAIL;
		}
	}
}