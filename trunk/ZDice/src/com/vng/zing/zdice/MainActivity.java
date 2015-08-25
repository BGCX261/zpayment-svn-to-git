package com.vng.zing.zdice;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.plus_ctl).setOnClickListener(this);
		findViewById(R.id.exit_ctl).setOnClickListener(this);
		findViewById(R.id.add_1000).setOnClickListener(this);
		findViewById(R.id.add_20000).setOnClickListener(this);
		findViewById(R.id.add_24000).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		long amount = -1;
		switch (v.getId()) {
		case R.id.plus_ctl:		
			amount = 0;
			break;
		case R.id.add_1000:
			amount = 1000;
			break;
		case R.id.add_20000:
			amount = 20000;
			break;
		case R.id.add_24000:
			amount = 24000;
			break;
		default:
			finish();
			break;
		}
		if (amount >= 0) {
			DirectPaymentModel.Instance.pay(this, amount, "Nạp điểm cho tài khoản Peter Pan");
		}
	}
}
