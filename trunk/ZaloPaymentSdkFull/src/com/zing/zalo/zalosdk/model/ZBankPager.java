package com.zing.zalo.zalosdk.model;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import android.app.Activity;
import android.view.View;

public class ZBankPager extends AbstractView {

	public ZBankPager(Activity owner, JSONObject o) {
		super(owner, o);
		bankList = new ArrayList<String>();
	}

	List<String> bankList;

	public List<String> getBankList() {
		return bankList;
	}
	public void setBank (String bank) {
		bankList.add(bank);
	}

	@Override
	public View generateView() {

		return null;
	}
}
