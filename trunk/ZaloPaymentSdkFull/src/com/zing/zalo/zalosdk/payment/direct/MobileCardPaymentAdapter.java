package com.zing.zalo.zalosdk.payment.direct;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.zing.zalo.zalosdk.common.DimensionHelper;
import com.zing.zalo.zalosdk.model.AbstractView;
import com.zing.zalo.zalosdk.model.ZTextView;
import com.zing.zalo.zalosdk.payment.R;
import com.zing.zalo.zalosdk.view.CommonXMLParser;
import com.zing.zalo.zalosdk.view.MobileCardXmlParser;

public class MobileCardPaymentAdapter extends CommonPaymentAdapter {

	TextView currentMobileProvider;
	ZTextView textView;
	CommonXMLParser xmlParser;
	public MobileCardPaymentAdapter(PaymentChannelActivity owner) {
		super(owner);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.zalosdk_activity_mobile_card;
	}

	@Override
	protected void initPage() {
		try {
			xmlParser = new MobileCardXmlParser(owner); 
			xmlParser.loadViewFromXml();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		List<AbstractView> list = xmlParser.getFactory().getListAbstractViews();
		int i=0;
		for (AbstractView abstractView : list) {
			String type = abstractView.getClass().getSimpleName(); 
			if (type.equals("ZTextView")) {
				ZTextView t = (ZTextView) abstractView;
				String value = t.getValue();
				if (value != null) {
					TextView tView = (TextView) xmlParser.getViewRoot().findViewWithTag(value);
					tView.setId(Integer.MAX_VALUE / 3 + i++);
					tView.setOnClickListener(this);	
					currentMobileProvider = tView;
					textView = t;
				}
				
			}
			
		}
//		currentMobileProvider = (TextView) owner.findViewById(R.id.mobi_ctl);
//		currentMobileProvider.setTag("VMS");
//		currentMobileProvider.setOnClickListener(this);
//		currentMobileProvider = (TextView) owner.findViewById(R.id.viettel_ctl);
//		currentMobileProvider.setTag("VTE");
//		currentMobileProvider.setOnClickListener(this);		
//		currentMobileProvider = (TextView) owner.findViewById(R.id.vina_ctl);
//		currentMobileProvider.setTag("VNP");
//		currentMobileProvider.setOnClickListener(this);
	}

	protected void selectProvider(TextView v) {
		if (currentMobileProvider != v) {
			currentMobileProvider.setTextColor(owner.getResources().getColor(R.color.zalosdk_zalo_blue));
			currentMobileProvider.setBackgroundResource(R.drawable.zalosdk_border08);
			currentMobileProvider = v;
			currentMobileProvider.setTextColor(owner.getResources().getColor(R.color.zalosdk_white));
			currentMobileProvider.setBackgroundResource(R.drawable.zalosdk_border07);
			List<AbstractView> list = xmlParser.getFactory().getListAbstractViews();
			for (AbstractView abstractView : list) {
				String type = abstractView.getClass().getSimpleName(); 
				if (type.equals("ZTextView")) {
					ZTextView t = (ZTextView) abstractView;
					if (t.getValue() != null && t.getValue().equals((String)v.getTag())) {
						textView = t;
					}
				}
			}
		}
	}
	
	protected void removeWarningIcon() {
		((EditText)owner.findViewById(R.id.zalosdk_card_code_ctl)).setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		((EditText)owner.findViewById(R.id.zalosdk_card_seri_ctl)).setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
	}
	
	protected void showWarningIcon(final int id) {
		removeWarningIcon();
		Drawable dr = owner.getResources().getDrawable(R.drawable.zalosdk_ic_alert);
		Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		float sf = DimensionHelper.getScaleFactor(owner);
		Log.i(getClass().getName(), "scale factor: " + sf);
		Drawable alert = new BitmapDrawable(owner.getResources(), Bitmap.createScaledBitmap(bitmap, (int)(35*sf), (int)(35*sf), true));
		
		EditText v = (EditText)owner.findViewById(id); 
		v.requestFocus();
		v.setCompoundDrawablesWithIntrinsicBounds(null, null, alert, null);
		v.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				((EditText)owner.findViewById(id)).setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			}
		});
	}
	
	protected void showWarningIconInNUIContext(final int id) {
		owner.runOnUiThread(new Runnable() {			
			@Override
			public void run() {
				showWarningIcon(id);
			}
		});		
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		//int id = v.getId();
		String tag = (String) v.getTag();
		//if (id == R.id.mobi_ctl || id == R.id.vina_ctl || id == R.id.viettel_ctl) {
		if (tag != null) {
			selectProvider((TextView) v);			
		}
//		could not use switch case since adt-v14		
//		switch (v.getId()) {
//		case R.id.mobi_ctl:
//		case R.id.vina_ctl:
//		case R.id.viettel_ctl:
//			selectProvider((TextView) v);
//			break;
//		}
	}

	@Override
	protected PaymentTask getPaymentTask() {
		SubmitMobileCardTask task = new SubmitMobileCardTask();
		task.owner = this;
		return task;
	}
}
