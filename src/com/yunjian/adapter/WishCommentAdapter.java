package com.yunjian.adapter;

import java.util.List;
import java.util.Map;

import com.yunjian.activity.R;
import com.yunjian.image.ImageLoader;
import com.yunjian.util.Utils;
import com.yunjian.view.CircleImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WishCommentAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater layoutInflater;
	private List<Map<String, Object>>list;
	private ImageLoader mImageLoader;
	public WishCommentAdapter(Context context,List<Map<String, Object>>list){
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.list = list;
		mImageLoader = ImageLoader.getInstance(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressWarnings("unused")
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Item item = null;
		if(item == null){
			item = new Item();
			arg1 = layoutInflater.inflate(R.layout.wish_detail_comment_item, null);
			try {
				item.photoImageView = (CircleImageView)arg1.findViewById(R.id.wish_comment_photo);
				item.nameTextView = (TextView)arg1.findViewById(R.id.wish_comment_name);
				item.contentTextView = (TextView)arg1.findViewById(R.id.wish_comment_content);
				item.sexImageView = (ImageView)arg1.findViewById(R.id.sex);
				item.nameTextView.setText(list.get(arg0).get("username").toString());
				item.contentTextView.setText(list.get(arg0).get("content").toString());
				if(list.get(arg0).get("gender").toString().equals("0.0")){
					item.sexImageView.setImageResource(R.drawable.user_sex_woman);
				}
				else if(list.get(arg0).get("gender").toString().equals("2.0")){
					item.sexImageView.setImageResource(R.drawable.user_sex_secret);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			arg1.setTag(item);
		}
		else {
			item = (Item)arg1.getTag();
		}
		//Õº∆¨º”‘ÿ
		try {
			mImageLoader.addTask(Utils.URL+list.get(arg0).get("user_id").toString(), item.photoImageView);
		} catch (Exception e) {
		// TODO: handle exception
			e.printStackTrace();
		}
		return arg1;
	}
	
	public class Item{
		private CircleImageView photoImageView;
		private ImageView sexImageView;
		private TextView nameTextView;
		private TextView contentTextView;
	}

}
