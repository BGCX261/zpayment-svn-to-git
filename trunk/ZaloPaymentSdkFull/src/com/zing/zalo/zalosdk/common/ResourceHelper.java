package com.zing.zalo.zalosdk.common;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONException;
import org.json.JSONObject;
import com.zing.zalo.zalosdk.http.HttpClientRequestEx;
import com.zing.zalo.zalosdk.http.HttpClientRequestEx.Type;
import com.zing.zalo.zalosdk.payment.direct.PaymentGatewayActivity;
import com.zing.zalo.zalosdk.payment.direct.PaymentProcessingDialog;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.widget.Toast;

public class ResourceHelper {

	Activity owner;
	PaymentProcessingDialog processingDlg;
	ZaloPaymentInfo info;
	public ResourceHelper(Activity owner, ZaloPaymentInfo info) {
		this.owner = owner;
		this.info = info;
	}
	
	public void getResourceFromNetwork() {
		String unzipFolder = null;
		File downloadFolder = owner.getDir("temp", Context.MODE_PRIVATE);
		
		if (isExternalStorageAvailable()) {
			
			/*
			 * Download zip file from network, in this demo we assume all files were unziped
			 * and stored in temporary location in sdcard or internal memory 
			 */
			unzipFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
					.getAbsolutePath();

		} else /* Use Internal Storage to unzip file */{
			
			if (downloadFolder != null && downloadFolder.isDirectory()) {			
				unzipFolder = downloadFolder.getAbsolutePath();	
			}
		}
		
		HttpRequestTask task = new HttpRequestTask();
		task.unzipFolder = unzipFolder;
		task.execute((Void[])null);
	}
	public void decompress(String zipText, String location) throws IOException {
		
		byte[] compressed = Base64.decode(zipText, Base64.DEFAULT);
		InputStream is;
	     ZipInputStream zis;

	         String filename;
	         is = new ByteArrayInputStream(compressed);
	         zis = new ZipInputStream(new BufferedInputStream(is));          
	         ZipEntry ze;
	         byte[] buffer = new byte[1024];
	         int count;

	         while ((ze = zis.getNextEntry()) != null) 
	         {
	             filename = ze.getName();

	             // Need to create directories if not exists, or
	             // it will generate an Exception...
	             if (ze.isDirectory()) {
	                File fmd = new File(location + File.separator + filename);
	                fmd.mkdirs();
	                continue;
	             }

	             FileOutputStream fout = new FileOutputStream(location + File.separator + filename);

	             while ((count = zis.read(buffer)) != -1) 
	             {
	                 fout.write(buffer, 0, count);             
	             }

	             fout.close();               
	             zis.closeEntry();
	         }

	         zis.close();

	}

	public static boolean isExternalStorageAvailable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}

		return false;
	}
	
	private class HttpRequestTask extends AsyncTask<Void, Void, JSONObject> {
		
		final String _zipResUrl = "http://dev.add.credits.zaloapp.com/configuration/getUpdate?"; 
		String unzipFolder = null;
		String version = null;
		ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(owner);
            pd.setTitle("");
            pd.setMessage("Đang xử lý");
            pd.show();

        }
        
		@Override
		protected JSONObject doInBackground(Void... params) {
			SharedPreferences preferences = owner.getSharedPreferences("zaloCredit", 0);
			version = preferences.getString("versionConfig", "0.0.0");
			StringBuilder sb = new StringBuilder();
			sb.append(_zipResUrl);
			sb.append("version=").append(version);
			sb.append("&osType=Android");
			sb.append("&appID=").append(info.appID);
			HttpClientRequestEx request = new HttpClientRequestEx(Type.POST, sb.toString());
			return request.getJSON();
		}
		
		protected void onPostExecute(JSONObject result) {
			
			pd.dismiss();
			if (result != null) {
				
				try {
					if (result.getInt("resultCode") >=0) {
						
						
						if (result.getBoolean("isUpdated")) {
							// TODO Check unzip file exist
							Toast.makeText(owner, "co roi", Toast.LENGTH_SHORT).show();
						} else {
							String configurations = result.getString("configurations");		
							File f = new File(unzipFolder);
							if (!f.isDirectory()) {
								f.mkdirs();
							}
							decompress(configurations, unzipFolder);	
							
							String serverVersion = result.getString("version");
							SharedPreferences preferences = owner.getSharedPreferences("zaloCredit", 0);
							Editor editor = preferences.edit();
							editor.putString("versionConfig", serverVersion);
							editor.commit();
							Toast.makeText(owner, "chua co", Toast.LENGTH_SHORT).show();
							
							
						}
					}else {
						//Has error
					}

					Intent intent = new Intent(owner,PaymentGatewayActivity.class);

					intent.putExtra("payInfo", info.toJSONString());
					owner.startActivity(intent);
					
				} catch (JSONException e) {
					
					e.printStackTrace();
				} catch (IOException e) {					
					e.printStackTrace();
				}
			}else {
				Toast.makeText(owner, "Kiem tra lai ket noi mang", Toast.LENGTH_SHORT).show();	
			}
			
		}
	}
}
