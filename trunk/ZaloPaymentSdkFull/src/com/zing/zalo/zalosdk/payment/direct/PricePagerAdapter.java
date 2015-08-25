package com.zing.zalo.zalosdk.payment.direct;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.res.Resources;
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
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.zing.zalo.zalosdk.common.DimensionHelper;
import com.zing.zalo.zalosdk.common.StringHelper;
import com.zing.zalo.zalosdk.payment.R;

public class PricePagerAdapter extends FragmentPagerAdapter {
	
	private static PricePagerAdapter CurrentInstance;
	
	TextView currentAmountCTL;
	JSONArray pages;
	
	public PricePagerAdapter(FragmentManager fm) {
		super(fm);	
		CurrentInstance = this;
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

	public int getCurrentAmount() {
		int amount = 0;
		try {
			amount = (Integer) currentAmountCTL.getTag();
		} catch (Exception e) {			
		}
		return amount;
	}
	
	@Override
	public int getCount() {
		return pages.length();
	}
	
	public static class PricePageFragment extends Fragment {
		public static final String ARG_KEY = "index";		
		
		
		protected int getRID(String name) {
			return getResources().getIdentifier(name, "id", getClass().getPackage().getName());
		}
		
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            LinearLayout page = (LinearLayout) inflater.inflate(R.layout.zalosdk_page_content, container, false);
            Bundle args = getArguments();
            page.setGravity(Gravity.CENTER);
            
            try {
            	Resources r = getResources();
            	int w = (int)DimensionHelper.dipsToPXs(80, r);
            	int h = (int)DimensionHelper.dipsToPXs(40, r);
            	int m = (int)DimensionHelper.dipsToPXs(5, r);
            	LayoutParams itemLayoutParams = new LayoutParams(w, h);
                itemLayoutParams.setMargins(m, m, m, m);
                LayoutParams rowLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            	int index = args.getInt(ARG_KEY);
                JSONArray prices = CurrentInstance.pages.getJSONArray(index);
                
    			Log.i(getClass().getName(), prices.toString());
				for (int i = 0; i < prices.length(); ++i) {
	            	JSONArray subPrices = prices.getJSONArray(i);
	            	Log.i(getClass().getName(), subPrices.toString());
	            	if (subPrices.length() > 0) {
	            		LinearLayout row = new LinearLayout(container.getContext());
	            		row.setOrientation(LinearLayout.HORIZONTAL);
	            		row.setGravity(Gravity.CENTER);
	            		for (int j = 0; j < subPrices.length(); ++j) {
		            		TextView tv = new TextView(container.getContext());
		        			Integer price = subPrices.getInt(j);
		            		tv.setLayoutParams(itemLayoutParams);
		            		tv.setGravity(Gravity.CENTER);
		            		if (price > 0) { 
		        				tv.setId(Integer.MAX_VALUE - price);			            		
		        				tv.setText(StringHelper.formatPrice(price));
			        			tv.setTag(price);		        			
		        				if (CurrentInstance.currentAmountCTL == null) {
		        					CurrentInstance.currentAmountCTL = tv;
		        				}
			        			if (price == (Integer)CurrentInstance.currentAmountCTL.getTag()) {
			        				tv.setBackgroundResource(R.drawable.zalosdk_border07);
			        				tv.setTextColor(getResources().getColor(R.color.zalosdk_white));
			        			} else {
			        				tv.setBackgroundResource(R.drawable.zalosdk_border08);
			        				tv.setTextColor(getResources().getColor(R.color.zalosdk_zalo_blue));				
			        			}
		        				tv.setOnClickListener(new OnClickListener() {		
		        					@Override
		        					public void onClick(View v) {
		        						if (CurrentInstance.currentAmountCTL != null) {
		        							CurrentInstance.currentAmountCTL.setBackgroundResource(R.drawable.zalosdk_border08);
		        							CurrentInstance.currentAmountCTL.setTextColor(getResources().getColor(R.color.zalosdk_zalo_blue));
		        						}
		        						CurrentInstance.currentAmountCTL = (TextView) v;
		        						CurrentInstance.currentAmountCTL.setBackgroundResource(R.drawable.zalosdk_border07);
		        						CurrentInstance.currentAmountCTL.setTextColor(getResources().getColor(R.color.zalosdk_white));
		        					}
		        				});
		        			}
		            		row.addView(tv);
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
