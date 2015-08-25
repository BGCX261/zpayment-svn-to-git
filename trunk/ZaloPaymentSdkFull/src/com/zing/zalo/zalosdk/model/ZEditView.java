package com.zing.zalo.zalosdk.model;

import org.json.JSONObject;

import android.app.Activity;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;

public class ZEditView extends AbstractView{

	// TODO Other variable use for styling
	String hint;
	boolean require;
	boolean cache;
	String background;
	int imeOptions;
	int inputType;
	int maxLength;
	int padding;
	int layout_marginBottom;
	int layout_marginTop;
	boolean singleLine;
	int textSize;
	int textStyle;
	public boolean isCache() {
		return cache;
	}

	public ZEditView(Activity owner, JSONObject o) {
		super(owner,o);
		
		// TODO Set variable for styling
		hint = o.optString("hint", "");
		require = Boolean.parseBoolean(o.optString("require", "false"));
		cache = Boolean.parseBoolean(o.optString("cache", "false"));
		background = o.optString("background", "");
		imeOptions = Integer.parseInt(o.optString("imeOptions", "0"));
		inputType = Integer.parseInt(o.optString("inputType", "0"));
		maxLength = Integer.parseInt(o.optString("maxLength", "20"));
		padding = Integer.parseInt(o.optString("padding", "0"));
		layout_marginBottom = Integer.parseInt(o.optString("layout_marginBottom", "0")); 
		layout_marginTop = Integer.parseInt(o.optString("layout_marginTop", "0")); 
		singleLine = Boolean.parseBoolean(o.optString("singleLine", "false")); 
		textSize =  Integer.parseInt(o.optString("textSize", "13")); 
		textStyle =  Integer.parseInt(o.optString("textStyle", "0")); 
	}

	@Override
	public View generateView() {
		EditText e = new EditText(owner);
    	e.setHint(hint);
    	if (param != null) {
    		e.setTag(param);	
    	}
    	int resID = owner.getResources().getIdentifier(background, "drawable", owner.getPackageName());
    	if (resID != 0) {
    		e.setBackgroundResource(resID);
    	}
    	e.setImeOptions(imeOptions);
    	if (inputType != 0) {
    		e.setInputType(inputType);	
    	}
    	e.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength) });
    	float density = owner.getResources().getDisplayMetrics().density;
    	padding = (int)(padding * density + 0.5f);
    	e.setPadding(padding,padding,padding,padding);

    	LayoutParams params = new LayoutParams(width,height);
    	layout_marginBottom = (int)(layout_marginBottom * density + 0.5f);
    	layout_marginTop = (int)(layout_marginTop * density + 0.5f);
    	params.setMargins(0,layout_marginTop,0,layout_marginBottom);
    	e.setSingleLine(singleLine);
    	e.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);	
    	e.setTypeface(null, textStyle);
    	
    	e.setLayoutParams(params);
		return e;
	}
	
	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public boolean isRequire() {
		return require;
	}

	public void setRequire(boolean require) {
		this.require = require;
	}
}
