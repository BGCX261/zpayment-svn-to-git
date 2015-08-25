package com.zing.zbookstore.directpay;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.zing.zalo.zalosdk.oauth.ZaloOAuth;

public class MainActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_title);
		findViewById(R.id.out_bt).setOnClickListener(this);
		ListView bookLv = (ListView) findViewById(R.id.books_lv);
		bookLv.setAdapter(new BookAdapter(this,
				(Button) findViewById(R.id.cart_bt), getBookList()));
	}

	private String getResourceText(int id) {
		StringBuilder sb = new StringBuilder();
		try {
			InputStream resource = getResources().openRawResource(id);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					resource));
			String line = reader.readLine();
			if (line != null) {
				sb.append(line);
				line = reader.readLine();
				while (line != null) {
					sb.append("\n");
					sb.append(line);					
					line = reader.readLine();
				}
			}
		} catch (Exception e) {
		}
		return sb.toString();
	}

	private BookItem[] getBookList() {
		try {
			JSONArray jsItems = new JSONArray(getResourceText(R.raw.items_qc));
			BookItem[] items = new BookItem[jsItems.length()];
			for (int i = 0; i < jsItems.length(); ++i) {
				items[i] = jsonToItem(jsItems.getJSONObject(i));
			}
			return items;
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		return new BookItem[0];
	}

	BookItem jsonToItem(JSONObject jsItem) {
		BookItem item = new BookItem();
		try {
			item.id = jsItem.getInt("id");
			item.name = jsItem.getString("name");
			item.author = jsItem.getString("author");
			item.price = jsItem.getLong("price");
			Class<?> drawableClass = R.drawable.class;
			Field drawableField = drawableClass.getField(jsItem
					.getString("cover_id"));
			item.cover_id = (Integer) drawableField.get(null);
		} catch (Exception e) {
		}
		return item;
	}

	@Override
	public void onClick(View v) {
		AppInfo.reAuthenticate = false;
		ZaloOAuth.Instance.unauthenticate();
		finish();
	}

	@Override
	public void onBackPressed() {
		AppInfo.reAuthenticate = false;
		super.onBackPressed();
	}
}
