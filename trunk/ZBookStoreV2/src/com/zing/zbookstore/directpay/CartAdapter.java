package com.zing.zbookstore.directpay;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentItem;


public class CartAdapter extends ArrayAdapter<ZaloPaymentItem> implements
		OnClickListener {
	private final BookAdapter owner;
	
	private int quantity = 0;
	private Button cart_bt;
	private final List<ZaloPaymentItem> items;

	private static class Tag {
		public ZaloPaymentItem item;
		public TextView quantity;
	}
	
	public CartAdapter(BookAdapter owner, Button cartInfo, List<ZaloPaymentItem> items) {
		super(owner.context, R.layout.activity_cart, items);
		this.items = items;		
		this.owner = owner;
		cart_bt = cartInfo;
	}

	public List<ZaloPaymentItem> getItems() {
		return items;		
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void putItem(ZaloPaymentItem item) {
		boolean isExist = false;
		for (ZaloPaymentItem r : items) {
			if (r.itemID == item.itemID) {
				r.itemQuantity += item.itemQuantity;
				isExist = true;
			}
		}
		if (!isExist) {
			items.add(item);
		}
		quantity += item.itemQuantity;
		if (cart_bt != null) {
			cart_bt.setText(String.valueOf(quantity));
			AnimationDrawable frameAnimation = (AnimationDrawable) cart_bt
					.getBackground();
			frameAnimation.stop();
			frameAnimation.start();
		}
	}

	public void resetCard() {
		quantity = 0;
		if (cart_bt != null) {
			cart_bt.setText(String.valueOf(quantity));
		}
		items.clear();
		notifyDataSetChanged();
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
		LayoutInflater inflater = (LayoutInflater) owner.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (position < items.size()) {
			View rowView = inflater.inflate(R.layout.activity_cart, parent,
					false);
			ZaloPaymentItem item = items.get(position);
			Tag tag = new Tag();
			tag.item = item;
			TextView tv = (TextView) rowView.findViewById(R.id.title_tv);
			tv.setText(item.itemName);
			tv = (TextView) rowView.findViewById(R.id.price_tv);
			tv.setText(formatPrice(item.itemPrice) + " VNĐ");
			tv = (TextView) rowView.findViewById(R.id.quantity_tv);
			tag.quantity = tv;
			tv.setText(String.valueOf(item.itemQuantity));			
			ImageButton button = (ImageButton) rowView.findViewById(R.id.add_bt);
			button.setTag(tag);
			button.setOnClickListener(this);
			button = (ImageButton) rowView.findViewById(R.id.minus_bt);
			button.setTag(tag);
			button.setOnClickListener(this);
			button = (ImageButton) rowView.findViewById(R.id.remove_bt);
			button.setTag(tag);
			button.setOnClickListener(this);
			if (position % 2 == 0) {
				rowView.setBackgroundColor(Color.argb(125, 114, 152, 201));
			}
			return rowView;
		}
		return null;
	}

	@Override
	public void onClick(View v) {
		Tag tag = (Tag) v.getTag();
		if (tag != null) {
			switch (v.getId()) {
			case R.id.add_bt:
				quantity += 1;
				tag.item.itemQuantity += 1;
				tag.quantity.setText(String.valueOf(tag.item.itemQuantity));
				if (cart_bt != null) {
					cart_bt.setText(String.valueOf(quantity));					
				}
				break;
			case R.id.minus_bt:
				if (tag.item.itemQuantity > 0) {
					quantity -= 1;
					tag.item.itemQuantity -= 1;
					tag.quantity.setText(String
							.valueOf(tag.item.itemQuantity));
					if (cart_bt != null) {
						cart_bt.setText(String.valueOf(quantity));					
					}
					if (tag.item.itemQuantity == 0) {
						items.remove(tag.item);
						notifyDataSetChanged();
						if (items.size() == 0) {
							owner.closeDialog();
						}
					}
				}
				break;
			case R.id.remove_bt:
				quantity -= tag.item.itemQuantity;
				items.remove(tag.item);
				notifyDataSetChanged();
				if (cart_bt != null) {
					cart_bt.setText(String.valueOf(quantity));					
				}
				if (items.size() == 0) {
					owner.closeDialog();
				}
				break;
			}
		}
	}
}
