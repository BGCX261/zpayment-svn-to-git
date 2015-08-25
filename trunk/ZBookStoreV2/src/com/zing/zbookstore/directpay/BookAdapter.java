package com.zing.zbookstore.directpay;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentItem;


public class BookAdapter extends ArrayAdapter<BookItem> implements
		OnClickListener, DialogInterface.OnCancelListener {
	protected final Context context;
	private final BookItem[] items;
	private final CartAdapter shoppingCart;
	private Dialog cartDialog = null;
	
	public BookAdapter(Context context, Button cartInfo, BookItem[] values) {
		super(context, R.layout.activity_main, values);
		this.context = context;
		this.items = values;
		this.shoppingCart =  new CartAdapter(this, cartInfo, new ArrayList<ZaloPaymentItem>());
		cartInfo.setOnClickListener(this);
	}

	private String formatPrice(long val) {
		String balance = String.valueOf(val);
		String rs = balance;
		if (rs.length() > 3) {
			int i = balance.length() - 3;
			rs = balance.substring(i);
			i -= 3;
			while (i >= 0) {
				rs = balance.substring(i, i + 3) + "." + rs;
				i -= 3;
			}
			i = 3 + i;
			if (i > 0) {
				rs = balance.substring(0, i) + "." + rs;
			}
		}
		return rs;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.activity_main, parent, false);
		BookItem item = items[position];
		ImageView avatar = (ImageView) rowView.findViewById(R.id.avatar_iv);
		avatar.setImageResource(item.cover_id);
		TextView tv = (TextView) rowView.findViewById(R.id.title_tv);
		tv.setText(item.name);
		tv = (TextView) rowView.findViewById(R.id.author_tv);
		tv.setText(item.author);
		tv = (TextView) rowView.findViewById(R.id.price_tv);
		tv.setText(formatPrice(item.price) + " VNĐ");
		View button = (View) rowView.findViewById(R.id.buy_bt);
		button.setTag(item);
		button.setOnClickListener(this);
		if (position % 2 == 0) {
			rowView.setBackgroundColor(Color.argb(125, 114, 152, 201));
		}
		return rowView;
	}

	private void showShoppingCart() {
		if (cartDialog == null) {
			cartDialog = new Dialog(context);
			cartDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			cartDialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			cartDialog.getWindow().setLayout(230, 250);
			cartDialog.setOnCancelListener(this);
			cartDialog.setContentView(R.layout.activity_cart_info);
			ListView booksLV = (ListView) cartDialog.findViewById(R.id.books_lv);			
			cartDialog.findViewById(R.id.ok_bt).setOnClickListener(this);
			cartDialog.findViewById(R.id.cancel_bt).setOnClickListener(this);
			booksLV.setAdapter(shoppingCart);
			cartDialog.show();
		}
	}
	
	public void closeDialog() {
		cartDialog.dismiss();
		cartDialog = null;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cart_bt:
			if (shoppingCart.getQuantity() > 0) {
				showShoppingCart();
			} else {
				Toast.makeText(context, "Không có sách trong giỏ hàng", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.ok_bt:
			cartDialog.dismiss();
			cartDialog = null;
			if (shoppingCart.getQuantity() > 0) {
				DirectPaymentModel.pay((Activity) context, shoppingCart, "");				
			} else {
				Toast.makeText(context, "Không có sách trong giỏ hàng", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.cancel_bt:
			cartDialog.dismiss();
			cartDialog = null;
			break;
		default:
			BookItem bookItem = (BookItem) v.getTag();
			if (bookItem != null) {
				ZaloPaymentItem item = new ZaloPaymentItem();
				item.itemID = String.valueOf(bookItem.id);
				item.itemName = bookItem.name;
				item.itemPrice = bookItem.price;
				item.itemQuantity = 1;
				shoppingCart.putItem(item);
			}
			break;
		}		
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		cartDialog = null;
	}
}