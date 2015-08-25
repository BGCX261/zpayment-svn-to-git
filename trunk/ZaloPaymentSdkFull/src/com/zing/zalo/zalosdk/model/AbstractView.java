package com.zing.zalo.zalosdk.model;

import org.json.JSONObject;

import android.app.Activity;
import android.view.View;

public abstract class AbstractView {
	
	int id;
	int height;
	int width;	
	String param;
	String value;
	int errClientCode;
	String errClientMess;
	int errServerCode;
	String errServerMess;
	Activity owner;
	public AbstractView (Activity owner, JSONObject o) {
		if (o != null) {
			id = Integer.parseInt(o.optString("id", "0"));
			param = o.optString("param", null);
			value = o.optString("value", null);
			errClientCode = o.optInt("errClientCode", -1);
			errClientMess = o.optString("errClientMess", "");
			errServerCode = o.optInt("errServerCode", -1);
			errServerMess = o.optString("errServerMess", "");
			height = Integer.parseInt(o.optString("height", "-2"));
			width = Integer.parseInt(o.optString("width", "-2"));
		}
		
		this.owner = owner;
	}

	public String getParamValue(String value1) {
		
		if (param != null) {
			
			if (value != null) {
				return "&"+param+"="+value;	
			}
			if (value1 != null) {
				return "&"+param+"="+value1;
			}
		}
		return "";
		
	}

	public String getScriptParamValue(String value1) {
		if (param != null) {
			if (value != null) {
				return "document.getElementById('" + param +"').value='" +value +"';";	
			}
			if (value1 != null) {
				return "document.getElementById('" + param +"').value='" +value1 +"';";	
			}
			
		}
		return "";
	}
	public abstract View generateView();
	
	public int getId() {
		return id;
	}
	
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getErrClientCode() {
		return errClientCode;
	}

	public void setErrClientCode(int errClientCode) {
		this.errClientCode = errClientCode;
	}

	public String getErrClientMess() {
		return errClientMess;
	}

	public void setErrClientMess(String errClientMess) {
		this.errClientMess = errClientMess;
	}

	public int getErrServerCode() {
		return errServerCode;
	}

	public void setErrServerCode(int errServerCode) {
		this.errServerCode = errServerCode;
	}

	public String getErrServerMess() {
		return errServerMess;
	}

	public void setErrServerMess(String errServerMess) {
		this.errServerMess = errServerMess;
	}

}
