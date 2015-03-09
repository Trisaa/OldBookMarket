package com.yunjian.adapter;

import java.util.List;
import java.util.Map;

import com.yunjian.activity.LoginActivity;
import com.yunjian.activity.R;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.image.ImageLoader;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserCenterService;
import com.yunjian.util.Utils;
import com.yunjian.view.CircleImageView;
import com.yunjian.view.HelpAchievePop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WishAdapter extends BaseAdapter implements OnQueryCompleteListener{
	private LayoutInflater layoutInflater;
	private Context context;
	private List<Map<String, Object>>list;
	private ImageLoader mImageLoader;
	
	public WishAdapter(Context context,List<Map<String, Object>>list){
		this.context = context;
		this.list = list;
		this.layoutInflater = LayoutInflater.from(context);
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
		if (item == null) {
			item = new Item();
			arg1 = layoutInflater.inflate(R.layout.list_item, null);
			item.userImage = (CircleImageView)arg1.findViewById(R.id.user_image);
			item.userName = (TextView)arg1.findViewById(R.id.user_name);
			item.userSex = (ImageView)arg1.findViewById(R.id.user_sex);
			item.achieve = (Button)arg1.findViewById(R.id.achieve);
			item.bookName = (TextView)arg1.findViewById(R.id.book_name);
			item.wishContent = (TextView)arg1.findViewById(R.id.wish_content);
			//图片加载
			try {
				mImageLoader.addTask(Utils.URL+list.get(arg0).get("user_id"), item.userImage);
			} catch (Exception e) {
			// TODO: handle exception
				e.printStackTrace();
			}
			item.userName.setText(list.get(arg0).get("username").toString());
			item.bookName.setText(list.get(arg0).get("bookname").toString());
			item.wishContent.setText(list.get(arg0).get("description").toString());
			if(list.get(arg0).get("gender").toString().equals("0.0")){
				item.userSex.setImageResource(R.drawable.user_sex_woman);
			}
			else if(list.get(arg0).get("gender").toString().equals("2.0")){
				item.userSex.setImageResource(R.drawable.user_sex_secret);
			}
			arg1.setTag(item);
		}
		else {
			item = (Item)arg1.getTag();
		}
		//图片加载
		try {
			//mImageLoader.addTask(Utils.IMGURL+list.get(arg0).get("img"), item.imageView);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		ItemClickListener itemClickListener = new ItemClickListener(arg0);
		item.achieve.setOnClickListener(itemClickListener);
		return arg1;
	}
	
	public class ItemClickListener implements OnClickListener{
    	private int pos;
    	
    	public ItemClickListener(int position){
    		this.pos = position;
    	}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(arg0.getId() == R.id.achieve){
				if(list.get(pos).get("user_id").toString().equals(Utils.user_id)){
					Toast.makeText(context, "这是你自己的心愿单喔", 2000).show();
				}
				else if(Utils.user_id.equals("")){
					Intent intent3 = new Intent(context,LoginActivity.class);
					context.startActivity(intent3);
				}
				else {
					HelpAchievePop helpAchievePop = new HelpAchievePop(context,list.get(pos));
					helpAchievePop.showAtLocation(((Activity) context)
							.findViewById(R.id.wishmain), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
				}
			}
		}
	}
	
	private class Item{
		private CircleImageView userImage;
		private TextView userName;
		private ImageView userSex;
		private Button achieve;
		private TextView bookName;
		private TextView wishContent;
	}

	@Override
	public void onQueryComplete(QueryId queryId, Object result, EHttpError error) {
		// TODO Auto-generated method stub
		if(result.equals("success")){
			Toast.makeText(context, "已经接下心愿单", 2000).show();
		}
	}

}
