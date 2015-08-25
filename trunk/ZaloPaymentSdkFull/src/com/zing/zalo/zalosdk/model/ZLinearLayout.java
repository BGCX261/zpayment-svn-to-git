package com.zing.zalo.zalosdk.model;
import org.json.JSONObject;
import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class ZLinearLayout extends AbstractView {

	int orientation;
	int layout_gravity;
	int gravity;
	int layout_marginTop;
	int layout_marginBottom;
	LinearLayout linearLayout;
	
	public LinearLayout getLinearLayout() {
		return linearLayout;
	}

	public ZLinearLayout(Activity owner, JSONObject o) {
		super(owner, o);
		orientation = Integer.parseInt(o.optString("orientation", "1"));
		layout_gravity = Integer.parseInt(o.optString("layout_gravity", "0")); 
		gravity = Integer.parseInt(o.optString("gravity", "0")); 
		layout_marginTop = Integer.parseInt(o.optString("layout_marginTop", "0")); 
		layout_marginBottom = Integer.parseInt(o.optString("layout_marginBottom", "0")); 
	}

	@Override
	public View generateView() {
		LinearLayout l = new LinearLayout(owner);
		l.setOrientation(orientation);
		l.setGravity(gravity);
		float density = owner.getResources().getDisplayMetrics().density;
		LayoutParams params = new LayoutParams(width,height);
    	layout_marginBottom = (int)(layout_marginBottom * density + 0.5f);
    	layout_marginTop = (int)(layout_marginTop * density + 0.5f);
    	params.setMargins(0,layout_marginTop,0,layout_marginBottom);
    	params.gravity = layout_gravity;
    	l.setLayoutParams(params);
    	
    	linearLayout = l;
		return l;
	}
	
}
