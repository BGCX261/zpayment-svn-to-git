package com.zing.zalo.zalosdk.model;

import org.json.JSONObject;
import android.app.Activity;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ZTextView extends AbstractView{

	String text;
	String append;
	int margin;
	int gravity;
	String background;
	String textColor;
	public String getAppend() {
		return append;
	}

	public ZTextView(Activity owner, JSONObject o) {
		super(owner, o);
		// TODO Auto-generated constructor stub
		text = o.optString("text", "");
		append = o.optString("append", "");
		margin = Integer.parseInt(o.optString("margin", "0"));
		gravity = Integer.parseInt(o.optString("gravity", "0"));
		background = o.optString("background", "");
		textColor = o.optString("textColor", "");
	}

	@Override
	public View generateView() {
		float density = owner.getResources().getDisplayMetrics().density;
		
		TextView t = new TextView(owner);
		if (width > 0) {
			width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, owner.getResources().getDisplayMetrics());
		}
		if (height > 0) {
			height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, owner.getResources().getDisplayMetrics());
		}
    	t.setText(text);
    	if (!append.isEmpty()) {
    		t.setTag(append);	
    	}
    	if (value != null) {
    		t.setTag(value);	
    	}
    	int resID = owner.getResources().getIdentifier(background, "drawable", owner.getPackageName());
    	if (resID != 0) {
    		t.setBackgroundResource(resID);
    	}
    	resID = owner.getResources().getIdentifier(textColor, "color", owner.getPackageName());
    	if (resID != 0) {
    		t.setTextColor(owner.getResources().getColor(resID));
    	}
    	LayoutParams params = new LayoutParams(width,height);	
    	margin = (int)(margin * density + 0.5f);
    	params.setMargins(margin, margin, margin, margin);
    	t.setGravity(gravity);
    	t.setLayoutParams(params);
		return t;
	}

}
