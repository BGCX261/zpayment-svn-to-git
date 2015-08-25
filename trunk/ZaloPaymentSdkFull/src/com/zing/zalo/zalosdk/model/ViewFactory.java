package com.zing.zalo.zalosdk.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import android.app.Activity;

public class ViewFactory {
	
	public static final int TEXT_VIEW = 1;
	public static final int EDIT_VIEW = 2;
	public static final int LINEAR_LAYOUT = 3;
	public static final int PAGER = 4;
	public static final int BANK_PAGER = 5;
	public static final int HIDDEN_EDIT_VIEW = 6;
	public static final int IMAGE_VIEW = 7;
	List<AbstractView> listAbstractViews;
	
	public List<AbstractView> getListAbstractViews() {
		return listAbstractViews;
	}

	public ViewFactory() {
		listAbstractViews = new ArrayList<AbstractView>();
	}
	
	public AbstractView produce (Activity owner, int type, JSONObject o) {
		switch (type) {
		case TEXT_VIEW:
			ZTextView t = new ZTextView(owner, o);
			listAbstractViews.add(t);
			return t;
		case EDIT_VIEW:
			ZEditView e = new ZEditView(owner, o);
			listAbstractViews.add(e);
			return e;	
		case LINEAR_LAYOUT:
			ZLinearLayout l = new ZLinearLayout(owner, o);
			listAbstractViews.add(l);
			return l;
		case PAGER: 
			ZPager p = new ZPager(owner, o);
			listAbstractViews.add(p);
			return p;
		case BANK_PAGER:
			ZBankPager b = new ZBankPager(owner, o);
			return b;
		case HIDDEN_EDIT_VIEW:
			ZHiddenEditView h = new ZHiddenEditView(owner, o);
			listAbstractViews.add(h);
			return h;
		case IMAGE_VIEW:
			ZImageView i = new ZImageView(owner, o);
			listAbstractViews.add(i);
			return i;
		}
		return null;
		
	}
	
	public void addAbstractView(AbstractView v) {
		listAbstractViews.add(v);
	}
}
