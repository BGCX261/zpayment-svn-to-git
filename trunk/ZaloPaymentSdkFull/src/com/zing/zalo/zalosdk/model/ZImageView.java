package com.zing.zalo.zalosdk.model;

import org.json.JSONObject;

import com.zing.zalo.zalosdk.common.BitmapHelper;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

public class ZImageView extends AbstractView{

	String type;
	ImageView imageView;
	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	public ImageView getImageView() {
		return imageView;
	}

	public String getType() {
		return type;
	}

	String scaleType;
	public ZImageView(Activity owner, JSONObject o) {
		super(owner, o);
		type = o.optString("type", "");
		scaleType = o.optString("scaleType", "");
	}

	@Override
	public View generateView() {
		ImageView i = new ImageView(owner);
		if (scaleType.equals("FIT_XY")) {
			i.setScaleType(ScaleType.FIT_XY);	
		}
		
		LayoutParams params = new LayoutParams(width,height);
		i.setLayoutParams(params);
		imageView = i;
		return i;
	}

	public void setCaptcha(String captcha) {
		imageView.setImageBitmap(BitmapHelper.b64ToImage(captcha));
		imageView.requestLayout();
	}
}
