package com.zing.zalo.zalosdk.view;

import com.zing.zalo.zalosdk.payment.direct.PaymentChannelActivity;

public class ATMCardInfoXMLParser extends CommonXMLParser{

	public ATMCardInfoXMLParser(PaymentChannelActivity activity) {
		super(activity,"zalosdk_activity_atm_card_info");	
	}

}
