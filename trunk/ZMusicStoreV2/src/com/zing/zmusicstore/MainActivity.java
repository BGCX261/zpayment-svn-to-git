package com.zing.zmusicstore;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	MusicAdapter msAdapter;
	ListView bookLv;
	boolean isHomePage = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_title);
		bookLv = (ListView) findViewById(R.id.books_lv);
		findViewById(R.id.home_iv).setOnClickListener(this);
		msAdapter = new MusicAdapter(this, (ImageView) findViewById(R.id.credit_iv), getBookList());
		long xu = msAdapter.getXu();
		if (xu < 50) {
			msAdapter.addXu(50 - xu);
		}
		bookLv.setAdapter(msAdapter);		
	}

	private List<MusicItem> getBookList() {
		List<MusicItem> items = new ArrayList<MusicItem>();
		MusicItem item = new MusicItem();
		item.id = 1;
		item.cover_id = R.drawable.i001;
		item.name = "Chiếc Khăn Gió Ấm";
		item.price = 10;
		item.singer = "Tiên Cookie";
		item.rating_id = R.drawable.star_5;
		items.add(item);
		
		item = new MusicItem();
		item.id = 2;
		item.cover_id = R.drawable.i002;
		item.name = "Em Yêu Anh (Single)";
		item.price = 20;
		item.singer = "Lương Bích Hữu";
		item.rating_id = R.drawable.star_4;
		items.add(item);
		
		item = new MusicItem();
		item.id = 3;
		item.cover_id = R.drawable.i003;
		item.name = "Chỉ Là Em Giấu Đi";
		item.price = 35;
		item.singer = "Bích Phương";
		item.rating_id = R.drawable.star_5;
		items.add(item);
		
		item = new MusicItem();
		item.id = 4;
		item.cover_id = R.drawable.i004;
		item.name = "Em Yêu Anh";
		item.price = 15;
		item.singer = "Miu Lê";
		item.rating_id = R.drawable.star_3;
		items.add(item);
		
		item = new MusicItem();
		item.id = 5;
		item.cover_id = R.drawable.i005;
		item.name = "Let’s Talk About Love";
		item.price = 75;
		item.singer = "SeungRi";
		item.rating_id = R.drawable.star_5;
		items.add(item);
		
		item = new MusicItem();
		item.id = 6;
		item.cover_id = R.drawable.i006;
		item.name = "Ký Ức Học Trò";
		item.price = 60;
		item.singer = "Nam Cường ft. Việt My";
		item.rating_id = R.drawable.star_4;
		items.add(item);
		
		item = new MusicItem();
		item.id = 7;
		item.cover_id = R.drawable.i007;
		item.name = "Merry Christmas 2013";
		item.price = 1000;
		item.singer = "Various Artists";
		item.rating_id = R.drawable.star_4;
		items.add(item);
		
		item = new MusicItem();
		item.id = 8;
		item.cover_id = R.drawable.i008;
		item.name = "Còn Mãi Nồng Nàn";
		item.price = 125;
		item.singer = "Dương Triệu Vũ";
		item.rating_id = R.drawable.star_5;
		items.add(item);
		
		item = new MusicItem();
		item.id = 9;
		item.cover_id = R.drawable.i009;
		item.name = "Lối Thoát";
		item.price = 300;
		item.singer = "MicroWave";
		item.rating_id = R.drawable.star_3;
		items.add(item);
		
		item = new MusicItem();
		item.id = 10;
		item.cover_id = R.drawable.i010;
		item.name = "Mỗi Người Một Định Mệnh";
		item.price = 700;
		item.singer = "Phạm Thanh Thảo";
		item.rating_id = R.drawable.star_4;
		items.add(item);
		
		item = new MusicItem();
		item.id = 11;
		item.cover_id = R.drawable.i011;
		item.name = "Những Nụ Cười";
		item.price = 7000;
		item.singer = "Bé Bảo An";
		item.rating_id = R.drawable.star_3;
		items.add(item);
		
		item = new MusicItem();
		item.id = 12;
		item.cover_id = R.drawable.i012;
		item.name = "Savannah Outen (Vol. 6)";
		item.price = 5;
		item.singer = "Savannah Outen";
		item.rating_id = R.drawable.star_3;
		items.add(item);
		return items;
	}

	@Override
	public void onClick(View v) {
		isHomePage = !isHomePage;
		if (isHomePage) {
			bookLv.setAdapter(msAdapter);
			((ImageView)v).setImageResource(R.drawable.credit_items);
			((TextView)findViewById(R.id.title_tv)).setText(R.string.app_name);
		} else {
			((ImageView)v).setImageResource(R.drawable.home);
			((TextView)findViewById(R.id.title_tv)).setText(R.string.your_ms);
			bookLv.setAdapter(new MusicCreditAdapter(this, msAdapter.getBoughtItems()));
		}
	}
	
	@Override
	protected void onPause() {
		MusicCreditAdapter.releasePlayer();
		super.onPause();
	}

	@Override
	protected void onStop() {
		MusicCreditAdapter.releasePlayer();
		super.onStop();
	}
}
