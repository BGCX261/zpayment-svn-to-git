package com.vng.zing.zdice;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.zing.zalo.zalosdk.payment.direct.HMACHelper;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentInfo;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentItem;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentListener;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentResultCode;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentService;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentStatus;

public class DirectPaymentModel {
	private final long appID = 1577725557845407485l;
	private final int securityMode = 1;
	private final int ownPrivateKey = 0xfabc0f0c;
	private final String secretKey = "0ERBGT5NONB4p2TRQNDk";

	public static DirectPaymentModel Instance = new DirectPaymentModel();

	private DirectPaymentModel() {
	}

	public static class Order {
		public long timestamp;
		public long amount;
		public String appTranxID;
		public String paymentMac;
		public String description;
		public String embedData;
		ZaloPaymentItem item;
	}

	private Order createOrder(long amount, String description)
			throws UnsupportedEncodingException {
		Order order = new Order();
		if (amount > 0) {
			order.item = new ZaloPaymentItem();
			order.item.itemID = "score_" + amount;
			order.item.itemName = String.valueOf(amount) + " score";
			order.item.itemPrice = amount;
			order.item.itemQuantity = 1;
		}
		order.description = description;
		order.amount = amount;
		order.timestamp = System.currentTimeMillis();
		order.appTranxID = UUID.randomUUID().toString();
		order.appTranxID = "ms-"
				+ order.appTranxID.substring(order.appTranxID.indexOf('-') + 1);
		order.embedData = String
				.valueOf((order.timestamp >> 2) ^ ownPrivateKey);
		genPaymentMac(order);
		return order;
	}

	public static String buildItemMac(ZaloPaymentItem item) {
		if (item != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(item.itemID);
			sb.append(".");
			sb.append(item.itemName);
			sb.append(".");
			sb.append(item.itemPrice);
			sb.append(".");
			sb.append(item.itemQuantity);
			return sb.toString();
		}
		return "";
	}

	private void genPaymentMac(Order order) {
		if (order.description == null) {
			order.description = "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(appID)/* appID */.append(order.appTranxID)/* appTranxID */
		.append(order.amount)/* amount */.append(buildItemMac(order.item))
				.append(order.timestamp)/* appTime */
				.append(order.description)
				/* description */.append(order.embedData)/* embedData */;
		System.out.println("input for mac: " + sb.toString());
		order.paymentMac = HMACHelper.HMacHexStringEncode(
				HMACHelper.HMACS.get(securityMode), secretKey, sb.toString());
	}

	private void updateScore(Activity activity, long addAmount) {
		try {
			TextView scoreTv = (TextView) activity.findViewById(R.id.score_ctl);
			long current = Long.parseLong(scoreTv.getText().toString());
			current += addAmount;
			scoreTv.setText(String.valueOf(current));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void pay(final Activity owner, long amount, String description) {
		try {
			Order order = createOrder(amount, description);
			ZaloPaymentInfo paymentInfo = new ZaloPaymentInfo();
			paymentInfo.appTime = order.timestamp;
			paymentInfo.appTranxID = order.appTranxID;
			paymentInfo.appID = Instance.appID;
			paymentInfo.amount = amount;
			paymentInfo.items = null;
			if (amount > 0) {
				paymentInfo.items = new ArrayList<ZaloPaymentItem>(1);
				paymentInfo.items.add(order.item);
			}
			paymentInfo.description = order.description;
			paymentInfo.embedData = order.embedData;
			paymentInfo.mac = order.paymentMac;
			ZaloPaymentService.Instance.pay(owner, paymentInfo,
					new ZaloPaymentListener() {

						@Override
						public void onComplete(ZaloPaymentResultCode code,
								ZaloPaymentStatus status, long amount) {
							switch (code) {
							case ZAC_RESULTCODE_SUCCESS:
								updateScore(owner, amount);
								break;
							default:
								break;
							}
						}

						@Override
						public void onCancel() {
							Log.i(getClass().getName(), "cancel payment");
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
