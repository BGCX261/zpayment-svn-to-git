package com.zing.zalo.zalosdk.payment.direct;

public interface ZaloPaymentListener {
	public void onComplete(ZaloPaymentResultCode code, ZaloPaymentStatus status, long amount);
	public void onCancel();
}
