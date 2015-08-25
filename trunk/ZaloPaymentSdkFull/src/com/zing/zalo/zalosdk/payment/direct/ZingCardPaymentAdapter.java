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
import android.widget.EditText;
import com.zing.zalo.zalosdk.common.DimensionHelper;
import com.zing.zalo.zalosdk.model.AbstractView;
import com.zing.zalo.zalosdk.model.ZEditView;
import com.zing.zalo.zalosdk.payment.R;
import com.zing.zalo.zalosdk.view.CommonXMLParser;
import com.zing.zalo.zalosdk.view.ZingCardXMLParser;


public class ZingCardPaymentAdapter extends CommonPaymentAdapter {
	
	CommonXMLParser xmlParser;
	public ZingCardPaymentAdapter(PaymentChannelActivity owner) {
		super(owner);
	}

	@Override
	protected void initPage() {
		try {
			xmlParser = new ZingCardXMLParser(owner);
			xmlParser.loadViewFromXml();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	protected void removeWarningIcon() {
		List<AbstractView> list = xmlParser.getFactory().getListAbstractViews();
		for (AbstractView abstractView : list) {
			if (abstractView.getClass().getSimpleName().equals("ZEditView")) {
				ZEditView e = (ZEditView) abstractView;
				((EditText)xmlParser.getViewRoot().findViewWithTag(e.getParam())).setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			}
			
		}
		//((EditText)owner.findViewById(R.id.zalosdk_card_code_ctl)).setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		//((EditText)owner.findViewById(R.id.zalosdk_card_seri_ctl)).setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
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
	protected PaymentTask getPaymentTask() {
		SubmitZingCardTask task = new SubmitZingCardTask();
		task.owner = this;
		return task;
	}
}
