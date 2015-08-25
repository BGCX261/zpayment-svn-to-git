package com.zing.zalo.zalosdk.oauth.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class OAuthCompleteListener {

	public void onGetOAuthComplete(long uId, String oauthCode){
		
	}
	
	public void onAuthenError(int errorCode){
		
	}
	
	public void onZaloOutOfDate(final Context context){
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
		alertBuilder.setMessage("Bản Zalo hiện tại không tương thích!")
					.setPositiveButton("Cập nhật", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							PackageUtils.launchMarketApp(context, "com.zing.zalo");
						}
					});
		alertBuilder.setNegativeButton("Bỏ qua", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alertBuilder.setCancelable(false).show();
	}
	
	public void onZaloNotInstalled(final Context context){
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
		alertBuilder.setMessage("Bạn chưa cài Zalo!")
					.setPositiveButton("Cài đặt", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							PackageUtils.launchMarketApp(context, "com.zing.zalo");
						}
					});
		alertBuilder.setNegativeButton("Bỏ qua", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alertBuilder.setCancelable(false).show();
	}
}
