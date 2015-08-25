package com.zing.zalo.zalosdk.payment.direct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.zing.zalo.zalosdk.payment.R;

public class ATMBankCardSelectorPagerAdapter extends FragmentPagerAdapter {
	
	private static ATMBankCardSelectorPagerAdapter CurrentInstance;
	
	
	private JSONObject currentBank;
	private JSONArray pages;
	private ATMBankCardSelectorPaymentAdapter owner;
	
	public ATMBankCardSelectorPagerAdapter(FragmentManager fm) {
		super(fm);	
		CurrentInstance = this;
	}
	
	public void setOwner(ATMBankCardSelectorPaymentAdapter owner) {
		this.owner = owner;
	}
	
	public void setPages(JSONArray pages) {
		this.pages = pages;
		notifyDataSetChanged();		
	}
	
	@Override
	public Fragment getItem(int index) {
		Fragment fragment = new PricePageFragment();
        Bundle args = new Bundle();
        args.putInt(PricePageFragment.ARG_KEY, index);
        fragment.setArguments(args);
        fragment.setRetainInstance(false);
		return fragment;
	}

	public String getCurrentBankCode() {
		String cardCode = "";
		try {
			cardCode = (String) currentBank.optString("code", null);
		} catch (Exception e) {			
		}
		return cardCode;
	}
	
	public void setCurrentBank(JSONObject currentBank) {
		this.currentBank = currentBank;
		owner.onCurrentCardChanged(currentBank.optString("icon", ""));
	}
	
	public JSONObject getcCurrentBank() {
		return currentBank;
	}
	
	@Override
	public int getCount() {
		return pages.length();
	}
	
	public static class PricePageFragment extends Fragment {
		public static final String ARG_KEY = "index";
		
		
		@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            LinearLayout page = (LinearLayout) inflater.inflate(R.layout.zalosdk_page_content, container, false);
            Bundle args = getArguments();
            page.setGravity(Gravity.CENTER);
            
            try {
            	LayoutParams rowLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            	int index = args.getInt(ARG_KEY);
                JSONArray jsRows = CurrentInstance.pages.getJSONArray(index);                
    			Log.i(getClass().getName(), jsRows.toString());
				for (int i = 0; i < jsRows.length(); ++i) {
	            	JSONArray jsRow = jsRows.getJSONArray(i);
	            	Log.i(getClass().getName(), jsRow.toString());
	            	if (jsRow.length() > 0) {
	            		LinearLayout row = new LinearLayout(container.getContext());
	            		row.setOrientation(LinearLayout.HORIZONTAL);
	            		row.setGravity(Gravity.CENTER);
	            		for (int j = 0; j < jsRow.length(); ++j) {
	            			View bankItem = inflater.inflate(R.layout.zalosdk_bank_item, null, true);
	            			ImageView itemCtl = (ImageView) bankItem.findViewById(R.id.zalosdk_bank_item);		            		
	            			JSONObject card = jsRow.getJSONObject(j);
		        			if (card.has("code")) {
		        				itemCtl.setImageResource(CurrentInstance.owner.getRDR(card.getString("icon")));
		        				itemCtl.setTag(card);
		        				itemCtl.setOnClickListener(new OnClickListener() {		
		        					@Override
		        					public void onClick(View v) {
		        						CurrentInstance.setCurrentBank((JSONObject) v.getTag());
		        					}
		        				});
		        			} else {
		        				itemCtl.setImageBitmap(null);	
		        			}
		            		row.addView(bankItem);
		            	}            		
	            		page.addView(row, rowLayoutParams);
	            	}
	            }
            } catch (JSONException e) {
				e.printStackTrace();
			}			
            return page;
        }
	}
}
