package com.zing.zmusicstore;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import android.app.Activity;

import com.zing.zalo.zalosdk.payment.direct.HMACHelper;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentInfo;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentListener;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentService;



public class DirectPaymentModel {
	private final long appID = 1577725557845407485l;
	private final int securityMode = 1;
	private final int ownPrivateKey = 0xfabc0f0c;
	private final String secretKey = "0ERBGT5NONB4p2TRQNDk";
	
	private ZaloPaymentListener lisener = null;
	
	
	public static DirectPaymentModel Instance = new DirectPaymentModel();
	private DirectPaymentModel() {}
	
	public static class Order {
		public long timestamp;
		public String appTranxID;
		public String paymentMac;
		public String description;
		public String embedData;
	}
	
	public void setOnPaymentCompleteListener(ZaloPaymentListener listener) {
		this.lisener = listener;
	}

	private Order createOrder(String description)
			throws UnsupportedEncodingException {
		Order order = new Order();
		order.timestamp = System.currentTimeMillis();
		order.appTranxID = "zms.android" + UUID.randomUUID().toString();
		order.embedData = String.valueOf((order.timestamp >> 2) ^ ownPrivateKey);
		genPaymentMac(order);
		return order;
	}

	private void genPaymentMac(Order order) {
		if (order.description == null) {
			order.description = "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(appID)/*appID*/.append(order.appTranxID)/*appTranxID*/
				.append(0)/*amount*/.append(order.timestamp)/*appTime*/
				.append(order.description)/*description*/.append(order.embedData)/*embedData*/;
		System.out.println("input for mac: " + sb.toString());
		order.paymentMac = HMACHelper.HMacHexStringEncode(HMACHelper.HMACS.get(securityMode), secretKey, sb.toString());
	}

	public void pay(Activity owner, String description) {
		try {
			Order order = createOrder(description);
			ZaloPaymentInfo paymentInfo = new ZaloPaymentInfo();			
			paymentInfo.appTime = order.timestamp;
			paymentInfo.appTranxID = order.appTranxID;
			paymentInfo.appID = Instance.appID;
			paymentInfo.amount = 0;
			paymentInfo.items = null;
			paymentInfo.description = order.description;
			paymentInfo.embedData = order.embedData;
			paymentInfo.mac = order.paymentMac;
			ZaloPaymentService.Instance.pay(owner, paymentInfo, Instance.lisener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
