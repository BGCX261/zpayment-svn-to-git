package com.zing.zmusicstore;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicCreditAdapter extends ArrayAdapter<MusicItem> implements OnClickListener{
	protected final Activity context;
	private final List<MusicItem> items;

	public MusicCreditAdapter(Activity context, List<MusicItem> values) {
		super(context, R.layout.activity_music_item, values);
		this.context = context;
		this.items = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (items.size() <= position) {
			return null;
		}
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.activity_credit_item, parent,
				false);
		MusicItem item = items.get(position);
		ImageView iv = (ImageView) rowView.findViewById(R.id.avatar_iv);
		iv.setImageResource(item.cover_id);
		TextView tv = (TextView) rowView.findViewById(R.id.title_tv);
		tv.setText(item.name);
		tv = (TextView) rowView.findViewById(R.id.singer_tv);
		tv.setText(item.singer);
		iv = (ImageView) rowView.findViewById(R.id.star_iv);
		iv.setImageResource(item.rating_id);
		iv = (ImageView) rowView.findViewById(R.id.play_iv);	
		iv.setTag(item);
		if(item.isPlaying) {
			iv.setImageResource(R.drawable.pause);
		} else {
			iv.setImageResource(R.drawable.play);
		}
		iv.setOnClickListener(this);
		if (position % 2 == 0) {
			rowView.setBackgroundColor(Color.argb(125, 114, 152, 201));
		}
		return rowView;
	}

	static MediaPlayer player = null;	
	static MusicItem prePlayingItem = null;	
	
	public static void releasePlayer() {
		if (player != null) {
			player.stop();
			player.release();
			player = null;
		}
	}
	
	@Override
	public void onClick(View v) {
		MusicItem item = (MusicItem) v.getTag();
		if (item != null) {
			if (item.isPlaying) {
				player.stop();
				item.isPlaying = false;
				notifyDataSetChanged();
			} else {				
				if (prePlayingItem != null) {
					prePlayingItem.isPlaying = false;
					
				}
				item.isPlaying = true;
				notifyDataSetChanged();
				prePlayingItem = item;				
				if (player == null) {
					player = new MediaPlayer();
				} else {
					player.reset();
				}
				AssetFileDescriptor afd;
				try {
					afd = context.getAssets().openFd("m" + item.id + ".mp3");
					if (afd != null) {
						player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
						player.setOnCompletionListener(new OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp) {
								if (prePlayingItem != null) {
									prePlayingItem.isPlaying = false;
									notifyDataSetChanged();
								}
							}
						});
						player.prepare();
						player.start();
					}					
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}				
	}

}