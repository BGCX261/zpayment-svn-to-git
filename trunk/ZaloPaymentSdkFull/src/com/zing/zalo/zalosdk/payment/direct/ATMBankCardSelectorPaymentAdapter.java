package com.zing.zalo.zalosdk.payment.direct;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import com.zing.zalo.zalosdk.common.DimensionHelper;
import com.zing.zalo.zalosdk.common.StringHelper;
import com.zing.zalo.zalosdk.model.AbstractView;
import com.zing.zalo.zalosdk.model.ZBankPager;
import com.zing.zalo.zalosdk.model.ZLinearLayout;
import com.zing.zalo.zalosdk.payment.R;
import com.zing.zalo.zalosdk.view.ATMCardSelectorXMLParser;
import com.zing.zalo.zalosdk.view.CommonXMLParser;

public class ATMBankCardSelectorPaymentAdapter extends CommonPaymentAdapter {

	public ATMBankCardSelectorPaymentAdapter(PaymentChannelActivity owner) {
		super(owner);
	}

	ATMBankCardSelectorPagerAdapter pagerAdapter;
	private ImageView currPageCTL;
	CommonXMLParser xmlParser;
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
				return 9;
			}
			return 12;
		case DisplayMetrics.DENSITY_HIGH:
		case DisplayMetrics.DENSITY_400:
		case DisplayMetrics.DENSITY_XHIGH:
		case DisplayMetrics.DENSITY_XXHIGH:
		case DisplayMetrics.DENSITY_XXXHIGH:
			if(metrics.widthPixels <= 1080) {
				return 6;
			}
			return 9;
		default:
			return 9;
		}		
	}
	
	protected JSONArray getBankLists() {
		List<AbstractView> list = xmlParser.getFactory().getListAbstractViews();
		for (AbstractView abstractView : list) {
			if (abstractView.getClass().getSimpleName().equals("ZBankPager")) {
				ZBankPager b = (ZBankPager) abstractView;
				List<String> list1 = b.getBankList();
				JSONArray bankList = new JSONArray();
				try {
					for (String bank : list1) {
						String[] s = bank.split(",");
						
						JSONObject obj = new JSONObject();
						obj.put("icon", s[0]);
						obj.put("code", s[1]);
						bankList.put(obj);
					}	
					return bankList;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		}
		JSONArray bankList = new JSONArray();
		try {
			JSONObject obj = new JSONObject();
			obj.put("icon", "zalosdk_vietcombank");
			obj.put("code", "123PVCB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_abbank");
			obj.put("code", "123PABB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_acb");
			obj.put("code", "123PACB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_agribank");
			obj.put("code", "123PAGB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_bacabank");
			obj.put("code", "123PBAB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_bidv");
			obj.put("code", "123PBIDV");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_daiabank");
			obj.put("code", "123PDAIAB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_eximbank");
			obj.put("code", "123PEIB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_gpbank");
			obj.put("code", "123PGPB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_hdbank");
			obj.put("code", "123PHDB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_maritimebank");
			obj.put("code", "123PMRTB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_mbbank");
			obj.put("code", "123PMB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_namabank");
			obj.put("code", "123PNAB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_navibank");
			obj.put("code", "123PNVB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_ocb");
			obj.put("code", "123POCB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_oceanbank");
			obj.put("code", "123POCEB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_pgbank");
			obj.put("code", "123PPGB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_sacombank");
			obj.put("code", "123PSCB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_saigonbank");
			obj.put("code", "123PSGB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_techcombank");
			obj.put("code", "123PTCB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_tienphongbank");
			obj.put("code", "123PTPB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_vib");
			obj.put("code", "123P");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_vietabank");
			obj.put("code", "123PVAB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_vietinbank");
			obj.put("code", "123PVTB");
			bankList.put(obj);
			obj = new JSONObject();
			obj.put("icon", "zalosdk_vpbank");
			obj.put("code", "123PVPB");
			bankList.put(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bankList;
	}
	
	protected void initBankLists(JSONObject currentBankCode) {
		try {
			JSONArray bankList = getBankLists();
			JSONArray pages = new JSONArray();
			int totalItems = bankList.length();
			int itemsPerPage = getItemsPerPage(totalItems);
			if (itemsPerPage < 3) {
				itemsPerPage = 3;
			}
			int itemsPerRow = itemsPerPage/3;
			for (int i = 0; i < totalItems; i += itemsPerPage) {
				JSONArray page = new JSONArray();
				for (int j = 0; j < itemsPerPage; j += itemsPerRow) {
					JSONArray row =  new JSONArray();
					for (int u = 0; u < itemsPerRow; ++u) {
						JSONObject bank;
						if (i + j + u < totalItems) {
							bank = bankList.getJSONObject(i + j + u);
						} else {
							bank = new JSONObject();
						}
						row.put(bank);
					}
					page.put(row);
				}
				pages.put(page);
			}
			pagerAdapter = new ATMBankCardSelectorPagerAdapter(owner.getSupportFragmentManager());
			pagerAdapter.setPages(pages);
			pagerAdapter.setOwner(this);
			if (bankList != null && bankList.length() > 0) {
				if (currentBankCode == null) {
					currentBankCode = bankList.getJSONObject(0);				
				}
			}
			pagerAdapter.setCurrentBank(currentBankCode);
			ViewPager pager = (ViewPager) owner.findViewById(R.id.zalosdk_pager_ctl);
			pager.setAdapter(pagerAdapter);
			
			if (pages.length() > 1) {
				LinearLayout pageIndicator = (LinearLayout) owner.findViewById(R.id.zalosdk_page_indicator_ctl);
				for (int i = 0; i < pages.length(); ++i) {
					ImageView iv = new ImageView(owner);
					float sf = DimensionHelper.getScaleFactor(owner);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (10*sf), (int) (10*sf));
					lp.setMargins((int) (5*sf), (int) (5*sf), (int) (5*sf), (int) (5*sf));
					iv.setLayoutParams(lp);			
					iv.setId(Integer.MAX_VALUE/2 + i);
					iv.setScaleType(ScaleType.FIT_CENTER);
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
		return R.layout.zalosdk_activity_atm_card_selector;
	}

	public void saveCurrentBank() {
		SharedPreferences gVar = owner.getSharedPreferences("zacPref", 0);
		Editor edit = gVar.edit();
		edit.putString("currentBank", pagerAdapter.getcCurrentBank().toString());
		edit.commit();
	}
	
	@Override
	protected void initPage() {
		try {
			xmlParser = new ATMCardSelectorXMLParser(owner); 
			xmlParser.loadViewFromXml();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		
		SharedPreferences gVar = owner.getSharedPreferences("zacPref", 0);
		String amount = gVar.getString("intputMoney", "");
		LinearLayout inputMoney = null;
		List<AbstractView> list = xmlParser.getFactory().getListAbstractViews();
		for (AbstractView abstractView : list) {
			if (abstractView.getClass().getSimpleName().equals("ZLinearLayout")) {
				ZLinearLayout l = (ZLinearLayout) abstractView;
				inputMoney = l.getLinearLayout();
				break;
			}
		}
		
		if (info.amount <= 0) {
			if (!amount.isEmpty()) {			
				//inputMoney.setText(amount);
				int count = inputMoney.getChildCount();
				for(int i =0 ; i< count; i++) {
					View v = inputMoney.getChildAt(i);
					if (v.getClass().getSimpleName().equals("EditText")) {
						((EditText) v).setText(amount);
					}
				}
			}
		} else {
			String chargedAmount = StringHelper.formatPrice(info.amount) + " đ";
			((TextView)owner.findViewById(R.id.zalosdk_charged_amount_ctl)).setText(chargedAmount);
			inputMoney.setVisibility(View.GONE);
			owner.findViewById(R.id.zalosdk_amount_info_ctl).setVisibility(View.VISIBLE);			
		}
		
		boolean canBack = gVar.getBoolean("step0_canBack", true);
		if (!canBack) {
			owner.findViewById(R.id.zalosdk_back_ctl).setVisibility(View.GONE);

		}

		JSONObject currentBank = null;
		String strCurrentBank = gVar.getString("currentBank", null);
		if (strCurrentBank != null) {
			try {
				currentBank = new JSONObject(strCurrentBank);
			} catch (Exception e) {
			}
		}
		initBankLists(currentBank);
		owner.findViewById(R.id.zalosdk_select_bank_ctl).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				owner.findViewById(R.id.zalosdk_card_selector_ctl).setVisibility(View.VISIBLE);
			}
		});
		owner.findViewById(R.id.zalosdk_card_selector_ctl).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {		
				v.setVisibility(View.GONE);
			}
		});
		owner.findViewById(R.id.zalosdk_card_selector_ctn_ctl).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				
			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

	protected void onCurrentCardChanged(String icon) {
		owner.findViewById(R.id.zalosdk_card_selector_ctl).setVisibility(View.GONE);
		((ImageView)owner.findViewById(R.id.zalosdk_current_bank_ctl)).setImageResource(getRDR(icon));
	}
	
	public String getCurrentCardCode() {
		try {
			return pagerAdapter.getCurrentBankCode();
		} catch (Exception e) {
		}
		return "";
	}

	@Override
	protected PaymentTask getPaymentTask() {
		SubmitAtmBankCardSelectorTask task = new SubmitAtmBankCardSelectorTask();
		task.owner = this;
		return task;
	}
}
