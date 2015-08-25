package com.zing.zalo.zalosdk.payment.direct;

class ZaloPaymentResult {
	public ZaloPaymentResultCode code;
	public ZaloPaymentStatus status;
	public long amount;
	
	public ZaloPaymentResult() {
		code = ZaloPaymentResultCode.ZAC_RESULTCODE_FAIL;
		status = ZaloPaymentStatus.ZAC_TRANXSTATUS_FAIL;
		amount = 0;
	}
}
