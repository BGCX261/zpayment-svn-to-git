package com.zing.zalo.zalosdk.common;

import java.lang.reflect.Field;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;

public class UiHelper {
	public static class MYDatePickerDialog extends DatePickerDialog {

		String title;		
		public MYDatePickerDialog(Context context, String title, OnDateSetListener callBack,
				int year, int monthOfYear, int dayOfMonth) {
			super(context, callBack, year, monthOfYear, dayOfMonth);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.title = title;
			setTitle(null);
			setButton(DatePickerDialog.BUTTON_POSITIVE, "Đồng ý", this);
			try {
				DatePicker dp = getDatePicker();
				dp.setCalendarViewShown(false);
				Field[] fs = dp.getClass().getDeclaredFields();
				for (Field f : fs) {
					if ("mDaySpinner".equalsIgnoreCase(f.getName())) {
						f.setAccessible(true);
						Object dayPicker = new Object();
						dayPicker = f.get(dp);
						((View) dayPicker).setVisibility(View.GONE);
						break;
					}
				}
				Field field = dp.getClass().getDeclaredField("mShortMonths");
		        field.setAccessible(true);
		        String[] value = (String[]) field.get(dp);
		        for (int i = 0; i < value.length; ++i) {
		        	value[i] = String.valueOf(i + 1);
		        	if (i < 9) {
		        		value[i] = "0" + value[i];
		        	}
		        }
	        } catch (Exception ex) {
	        	Log.e(getClass().getName(), ex.getMessage(), ex);
	        }
			updateDate(year, monthOfYear, dayOfMonth);
		}
		
		
		
		@Override
		public void onDateChanged(DatePicker view, int year, int month, int day) {
			super.onDateChanged(view, year, month, day);
			setTitle(null);			
		}


		@Override
		public void show() {
			super.show();			
		}
	}
	
	public static MYDatePickerDialog createMYDatePicker(Context context, String title, int month, int year, OnDateSetListener listener) {
		
        return new MYDatePickerDialog(context, title, listener, year, month, 1);
    }
}
