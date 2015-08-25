package com.zing.zmusicstore;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentListener;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentResultCode;
import com.zing.zalo.zalosdk.payment.direct.ZaloPaymentStatus;

public class MusicAdapter extends ArrayAdapter<MusicItem> implements
		OnClickListener, DialogInterface.OnCancelListener {
	protected final Activity context;
	private final List<MusicItem> items;
	private Dialog creditDlg = null;
	private MusicItem previousItem = null;
	List<MusicItem> boughtItems = new ArrayList<MusicItem>();

	public MusicAdapter(Activity context, View cartInfo, List<MusicItem> values) {
		super(context, R.layout.activity_music_item, values);
		this.context = context;
		this.items = values;
		cartInfo.setOnClickListener(this);
		DirectPaymentModel.Instance
				.setOnPaymentCompleteListener(new ZaloPaymentListener() {
					@Override
					public void onComplete(ZaloPaymentResultCode zpCode,
							ZaloPaymentStatus status, long amount) {
						switch (zpCode) {
						case ZAC_RESULTCODE_FAIL:
							break;
						case ZAC_RESULTCODE_SUCCESS:
							addXu(amount / 1000);
							if (previousItem != null) {
								pay(previousItem);
							}
							break;
						default:
							break;
						}
					}

					@Override
					public void onCancel() {
						// Do something
					}
				});
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
		if (items.size() > position) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.activity_music_item,
					parent, false);
			MusicItem item = items.get(position);
			ImageView iv = (ImageView) rowView.findViewById(R.id.avatar_iv);
			iv.setImageResource(item.cover_id);
			TextView tv = (TextView) rowView.findViewById(R.id.title_tv);
			tv.setText(item.name);
			tv = (TextView) rowView.findViewById(R.id.singer_tv);
			tv.setText(item.singer);
			iv = (ImageView) rowView.findViewById(R.id.star_iv);
			iv.setImageResource(item.rating_id);
			Button button = (Button) rowView.findViewById(R.id.xu_bt);
			button.setTag(item);
			button.setText(formatPrice(item.price) + " xu");
			button.setOnClickListener(this);
			if (position % 2 == 0) {
				rowView.setBackgroundColor(Color.argb(125, 114, 152, 201));
			}
			return rowView;
		}
		return null;
	}

	public List<MusicItem> getBoughtItems() {
		return boughtItems;
	}

	private void showCredit(String content1, String content2) {
		if (creditDlg == null) {
			creditDlg = new Dialog(context);
			creditDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
			creditDlg.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			creditDlg.setOnCancelListener(this);
			creditDlg.setContentView(R.layout.activity_credit_info);
			((TextView) creditDlg.findViewById(R.id.content_1_tv)).setText(Html
					.fromHtml(content1));
			((TextView) creditDlg.findViewById(R.id.content_2_tv)).setText(Html
					.fromHtml(content2));
			creditDlg.findViewById(R.id.pay_tv).setOnClickListener(this);
			creditDlg.findViewById(R.id.cancel_tv).setOnClickListener(this);
			creditDlg.show();
		}
	}

	private long getTotalXu() {
		SharedPreferences totalXu = context.getSharedPreferences("credit",
				Context.MODE_PRIVATE);
		return totalXu.getLong("xu", 0);
	}

	public long getXu() {
		SharedPreferences totalXu = context.getSharedPreferences("credit",
				Context.MODE_PRIVATE);
		return totalXu.getLong("xu", 0);
	}

	public boolean addXu(long amount) {
		SharedPreferences totalXu = context.getSharedPreferences("credit",
				Context.MODE_PRIVATE);
		long xu = totalXu.getLong("xu", 0) + amount;
		if (xu < 0) {
			return false;
		}
		SharedPreferences.Editor editor = totalXu.edit();
		editor.putLong("xu", xu);
		editor.commit();
		return true;
	}

	private void getCreditsInfo() {
		String content1 = "Bạn có " + formatPrice(getTotalXu()) + " xu trong tài khoản";		
		String content2 = "Ấn \"Nạp thêm\" để thêm tiền vào tài khoản";
		showCredit(content1, content2);
	}

	public void closeDialog() {
		creditDlg.dismiss();
		creditDlg = null;
	}

	private void pay(MusicItem msItem) {
		if (msItem != null) {
			if (addXu(-msItem.price)) {
				previousItem = null;
				boughtItems.add(msItem);
				remove(msItem);
				Toast.makeText(
						context,
						"Mua thành công. Bạn còn " + formatPrice(getTotalXu())
								+ " xu trong tài khoản", Toast.LENGTH_LONG)
						.show();
			} else {
				previousItem = msItem;
				long totalXu = getTotalXu();
				String content1 = "Bạn có " + formatPrice(getTotalXu())
						+ " xu trong tài khoản";
				String content2 = "Bạn cần nạp thêm " + formatPrice(msItem.price - totalXu)
						+ " xu để mua bài hát này";
				showCredit(content1, content2);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.credit_iv:
			previousItem = null;			
			getCreditsInfo();
			break;
		case R.id.pay_tv:
			closeDialog();
			DirectPaymentModel.Instance.pay(context, "ZMusic store");
			break;
		case R.id.cancel_tv:
			creditDlg.dismiss();
			creditDlg = null;
			break;
		default:
			MusicItem bookItem = (MusicItem) v.getTag();
			pay(bookItem);
			break;
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		creditDlg = null;
	}
}