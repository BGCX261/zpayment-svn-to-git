package com.zing.zalo.zalosdk.payment.direct;

import android.content.Intent;

import com.zing.zalo.zalosdk.payment.R;

public class PaymentAdapterFactory {
	public static CommonPaymentAdapter produce(PaymentChannelActivity owner) {
		int channel = owner.getIntent().getIntExtra("channel", R.id.zalosdk_zingcard_ctl);
		if (channel == R.id.zalosdk_zingcard_ctl) {
			return new ZingCardPaymentAdapter(owner);
		} else if (channel == R.id.zalosdk_mobile_card_ctl) {
			return new MobileCardPaymentAdapter(owner);
		} else if (channel == R.id.zalosdk_mobile_account_ctl) {
			if (owner.getIntent().getIntExtra("adapterid", 0) == 0) {
				return new MobileAccPaymentAdapter(owner);
			} else {
				return new MobileAccOtpPaymentAdapter(owner);
			}	
		}  else if (channel == R.id.zalosdk_atm_ctl) {
			int step = owner.getIntent().getIntExtra("adapterid", 0);
			switch (step) {
			case 1:
				return new AtmBankCardInfoPaymentAdapter(owner);
			case 2:
				return new AtmCardOtpPaymentAdapter(owner);
			case 11:
				return new SmlCardPaymentAdapter(owner);
			default:
				return new ATMBankCardSelectorPaymentAdapter(owner);
			}
		}
		return null;
//		could not use switch case since adt-v14
//		switch (owner.getIntent().getIntExtra("channel", R.id.zingcard_ctl)) {
//		case R.id.zingcard_ctl:
//			return new ZingCardPaymentAdapter(owner);
//		case R.id.mobile_card_ctl:
//			return new MobileCardPaymentAdapter(owner);
//		case R.id.mobile_account_ctl:
//			if (owner.getIntent().getIntExtra("adapterid", 0) == 0) {
//				return new MobileAccPaymentAdapter(owner);
//			} else {
//				return new MobileAccOtpPaymentAdapter(owner);
//			}
//		case R.id.atm_ctl:
//			int step = owner.getIntent().getIntExtra("adapterid", 0);
//			switch (step) {
//			case 1:
//				return new AtmCardInfoPaymentAdapter(owner);
//			case 2:
//				return new AtmCardOtpPaymentAdapter(owner);
//			case 11:
//				return new SmlCardPaymentAdapter(owner);
//			default:
//				return new ATMCardSelectorPaymentAdapter(owner);
//			}
//		default:
//			return null;
//		}
	}

	public static void nextAdapter(CommonPaymentAdapter adapter, int adapterID) {
		int channel = adapter.owner.getIntent().getIntExtra("channel",
				R.id.zalosdk_zingcard_ctl);
		Intent intent = new Intent(adapter.owner, PaymentChannelActivity.class);
		intent.putExtra("payInfo",
				adapter.owner.getIntent().getStringExtra("payInfo"));
		intent.putExtra("channel", channel);
		intent.putExtra("adapterid", adapterID);
		adapter.owner.finish();
		adapter.owner.startActivity(intent);
	}
}