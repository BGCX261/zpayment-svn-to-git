package com.zing.zalo.zalosdk.model;

import org.json.JSONObject;
import android.app.Activity;
import android.view.View;

public class ZHiddenEditView extends AbstractView {

	public ZHiddenEditView(Activity owner, JSONObject o) {
		super(owner, o);

	}

	@Override
	public View generateView() {

		return null;
	}

}
