package com.zing.zalo.zalosdk.payment.direct;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zing.zalo.zalosdk.common.DimensionHelper;
import com.zing.zalo.zalosdk.common.StringHelper;
import com.zing.zalo.zalosdk.model.AbstractView;
import com.zing.zalo.zalosdk.model.ZEditView;
import com.zing.zalo.zalosdk.model.ZPager;
import com.zing.zalo.zalosdk.oauth.ZaloOAuth;
import com.zing.zalo.zalosdk.payment.R;
import com.zing.zalo.zalosdk.view.CommonXMLParser;
import com.zing.zalo.zalosdk.view.MobileAccXMLParser;

public class MobileAccPaymentAdapter extends CommonPaymentAdapter {

	public MobileAccPaymentAdapter(PaymentChannelActivity owner) {
		super(owner);
	}

	PricePagerAdapter pricePagerAdapter;
	CommonXMLParser xmlParser;
	private ImageView currPageCTL;

	protected int getItemsPerPage(int totalItems) {
		Display display = owner.getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		Log.i(getClass().getName(), metrics.widthPixels + "");
		switch (metrics.densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
		case DisplayMetrics.DENSITY_MEDIUM:
			if (metrics.widthPixels <= 320) {
				return 6;
			} 
			else if (metrics.widthPixels <= 480) {
				return 8;
			}
			return 10;
		case DisplayMetrics.DENSITY_HIGH:
		case DisplayMetrics.DENSITY_400:
		case DisplayMetrics.DENSITY_XHIGH:
		case DisplayMetrics.DENSITY_XXHIGH:
		case DisplayMetrics.DENSITY_XXXHIGH:
			if(metrics.widthPixels <= 1080) {
				return 6;
			}
			return 8;
		default:
			return 8;
		}		
	}
	
	private static JSONArray amounts;
	static {
		staticInit();
	}
	static void staticInit() {
		try {
			amounts = new JSONArray(
				"[1000, 2000, 3000, 4000, 5000, 10000, 15000, 20000, 30000, 40000, 50000, 100000]");
		} catch (Exception ex) {}
	}
	
	public static boolean isSupported(long amount) {
		//prevent crash by GC
		if (amounts == null) {
			staticInit();
		}
		int size = amounts.length();
		for (int i = 0; i < size; ++i) {
			if (amounts.optInt(i, 0) == amount) {
				return true;
			}
		}
		return false;
	}
	
	protected void initAmount() {
		try {
			String priceValues = "[1000, 2000, 3000, 4000, 5000, 10000, 15000, 20000, 30000, 40000, 50000, 100000]";
			List<AbstractView> list = xmlParser.getFactory().getListAbstractViews();
			for (AbstractView abstractView : list) {
				if (abstractView.getClass().getSimpleName().equals("ZPager")) {
					ZPager p = (ZPager) abstractView;
					String value = p.getValue();
					if (value != null && !value.isEmpty()) {
						priceValues = value;
					}
				}
			}
			JSONArray amounts = new JSONArray(priceValues);

			//prevent crash by GC
			if (amounts == null) {
				staticInit();
			}

			JSONArray pages = new JSONArray();
			int totalItems = amounts.length();
			int itemsPerPage = getItemsPerPage(totalItems);
			if (itemsPerPage < 2) {
				itemsPerPage = 2;
			}
			int itemsPerRow = itemsPerPage/2;
			for (int i = 0; i < totalItems; i += itemsPerPage) {
				JSONArray page = new JSONArray();
				for (int j = 0; j < itemsPerPage; j += itemsPerRow) {
					JSONArray row =  new JSONArray();
					for (int u = 0; u < itemsPerRow; ++u) {
						int price = 0;
						if (i + j + u < totalItems) {
							price = amounts.getInt(i + j + u);
						}
						row.put(price);
					}
					page.put(row);
				}
				pages.put(page);
			}
			pricePagerAdapter = new PricePagerAdapter(owner.getSupportFragmentManager());
			pricePagerAdapter.setPages(pages);
			ViewPager pager = (ViewPager) owner.findViewById(R.id.zalosdk_pager_ctl);
			pager.setAdapter(pricePagerAdapter);
			float sf = DimensionHelper.getScaleFactor(owner);
			if (pages.length() > 1) {
				LinearLayout pageIndicator = (LinearLayout) owner.findViewById(R.id.zalosdk_page_indicator_ctl);
				for (int i = 0; i < pages.length(); ++i) {
					ImageView iv = new ImageView(owner);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (10*sf), (int) (10*sf));
					lp.setMargins((int) (5*sf), (int) (5*sf), (int) (5*sf), (int) (5*sf));
					iv.setLayoutParams(lp);
					iv.setId(Integer.MAX_VALUE/2 + i);
					if (i == 0) {
						iv.setImageResource(R.drawable.zalosdk_ic_page_active_xml);
						currPageCTL = iv;
					} else {
						iv.setImageResource(R.drawable.zalosdk_ic_page_xml);
					}
					pageIndicator.addView(iv);
				}
				pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						currPageCTL.setImageResource(R.drawable.zalosdk_ic_page_xml);
						currPageCTL = (ImageView) owner.findViewById(Integer.MAX_VALUE/2 + position);
						currPageCTL.setImageResource(R.drawable.zalosdk_ic_page_active_xml);
					}		
				});
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected int getLayoutId() {
		return R.layout.zalosdk_activity_mobile_acc;
	}

	@Override
	protected void initPage() {
		
		try {
			xmlParser = new MobileAccXMLParser(owner);
			xmlParser.loadViewFromXml();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if (info.amount <= 0) {
			initAmount();			
		} else {
			String chargedAmount = StringHelper.formatPrice(info.amount) + " đ";
			((TextView)owner.findViewById(R.id.zalosdk_charged_amount_ctl)).setText(chargedAmount);
			owner.findViewById(R.id.zalosdk_amount_selector_ctl).setVisibility(View.GONE);
			owner.findViewById(R.id.zalosdk_amount_info_ctl).setVisibility(View.VISIBLE);
		}

		SharedPreferences gVar = owner.getSharedPreferences("zacPref", 0);		

		List<AbstractView> list = xmlParser.getFactory().getListAbstractViews();
		for (AbstractView abstractView : list) {
			if (abstractView.getClass().getSimpleName().equals("ZEditView")) {
				ZEditView e = (ZEditView) abstractView;
				if (e.isCache()) {
					EditText phoneInput = (EditText) owner.findViewById(R.id.view_root).findViewWithTag(e.getParam());
					
					if (ZaloOAuth.Instance != null) {
						
						long zaloId =ZaloOAuth.Instance.getZaloId();
						String phone = ZaloOAuth.Instance.getZaloPhoneNumber();
						if (phone != null && !phone.isEmpty() && zaloId >=1) {
							phoneInput.setEnabled(false);
							phoneInput.setText(ZaloOAuth.Instance.getZaloPhoneNumber());
						}else {
							phoneInput.setText(gVar.getString("otpPhone", ""));
						}
					}else {
						phoneInput.setText(gVar.getString("otpPhone", ""));
					}
					
					break;
				}
			}
		}
	}

	protected void enableInputPhone() {
		SharedPreferences gVar = owner.getSharedPreferences("zacPref", 0);
		List<AbstractView> list = xmlParser.getFactory().getListAbstractViews();
		for (AbstractView abstractView : list) {
			if (abstractView.getClass().getSimpleName().equals("ZEditView")) {
				ZEditView e = (ZEditView) abstractView;
				if (e.isCache()) {
					EditText phoneInput = (EditText) owner.findViewById(R.id.view_root).findViewWithTag(e.getParam());
					phoneInput.setText(gVar.getString("otpPhone", ""));
					phoneInput.setEnabled(true);		
				}
			}
		}
//		EditText phoneInput = (EditText)owner.findViewById(R.id.zalosdk_phone_ctl);
//		
//		phoneInput.setText(gVar.getString("otpPhone", ""));
//		phoneInput.setEnabled(true);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

	public int getCurrentAmount() {
		if (info.amount != 0) {
			return (int)info.amount;
		}
		try {
			return pricePagerAdapter.getCurrentAmount();
		} catch (Exception e) {
		}
		return 0;
	}

	@Override
	protected PaymentTask getPaymentTask() {
		SubmitMobileAccTask task = new SubmitMobileAccTask();
		task.owner = this;
		return task;
	}
}
