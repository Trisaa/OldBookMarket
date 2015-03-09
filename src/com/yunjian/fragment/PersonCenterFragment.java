package com.yunjian.fragment;

import java.util.Map;

import com.umeng.analytics.MobclickAgent;
import com.yunjian.activity.EditPersonCenter;
import com.yunjian.activity.LoginActivity;
import com.yunjian.activity.MessageCenter;
import com.yunjian.activity.R;
import com.yunjian.activity.SettingCenter;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.image.AsyncImageLoader;
import com.yunjian.image.AsyncImageLoader.ImageCallback;
import com.yunjian.image.ImageLoader;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserManageService;
import com.yunjian.util.SerializableMap;
import com.yunjian.util.Utils;
import com.yunjian.view.CircleImageView;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PersonCenterFragment extends Fragment implements OnClickListener{

	private ImageView messageImageView;
	private ImageView settingImageView;
	private ImageView editImageView;
	private CircleImageView photoImageView;
	private Button goodsButton;
	private Button wishesButton;
	private TextView nickTextView;
	private TextView phoneTextView;
	private TextView qqTextView;
	private ImageView sexImageView;
	
	private Intent intent;
	private FragmentManager fManager;
	private FragmentTransaction fTransaction;
	private Fragment goodFragment,wishFragment,showFragment;
	
	private UserManageService userManageService;
	private OnQueryCompleteListener onQueryCompleteListener;
	private ImageLoader imageLoader;
	private Map<String, Object>map;
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.personcenter, null);
		if(Utils.user_id.equals("")){
			Intent intent = new Intent(getActivity(),LoginActivity.class);
			startActivity(intent);
		}
		else {
			initView(view);
			getFragment();
			showFragment = goodFragment;
			fManager.beginTransaction().add(R.id.list_ll, goodFragment).commit();
			goodsButton.setBackgroundResource(R.drawable.pc_btn_pressed);
		}
		return view;
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(!Utils.user_id.equals("")){
			initView(view);
			getFragment();
		}
	}
	@Override
 	public void onPause() {
 	    super.onPause();
 	}
	 


	public void getFragment(){
		wishFragment = new MyWishesFragment();
		goodFragment = new MyBooksFragment();
	}
	
	public void resetBunttonColor(){
		goodsButton.setBackgroundResource(R.drawable.pc_btn_unpressed);
		wishesButton.setBackgroundResource(R.drawable.pc_btn_unpressed);
	}
	
	public void initView(View view){
		messageImageView = (ImageView)view.findViewById(R.id.message_btn);
		settingImageView = (ImageView)view.findViewById(R.id.setting_btn);
		editImageView = (ImageView)view.findViewById(R.id.edit_img);
		photoImageView = (CircleImageView)view.findViewById(R.id.user_icon);
		goodsButton = (Button)view.findViewById(R.id.goods);
		wishesButton = (Button)view.findViewById(R.id.wishes);
		nickTextView = (TextView)view.findViewById(R.id.nick_txv);
		sexImageView = (ImageView)view.findViewById(R.id.sex_img);
		phoneTextView = (TextView)view.findViewById(R.id.phone_txv);
		qqTextView = (TextView)view.findViewById(R.id.qq_txv);
		intent = new Intent();
		fManager = getFragmentManager();
		
		messageImageView.setOnClickListener(this);
		settingImageView.setOnClickListener(this);
		editImageView.setOnClickListener(this);
		goodsButton.setOnClickListener(this);
		wishesButton.setOnClickListener(this);
		
		if(BookFragment.hasMessageString)
			messageImageView.setImageResource(R.drawable.pc_message_new);
		
		userManageService = new UserManageService();
		onQueryCompleteListener = new OnQueryCompleteListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void onQueryComplete(QueryId queryId, Object result, EHttpError error) {
				// TODO Auto-generated method stub
				if(result != null){
					map = (Map<String, Object>) result;
					
					if(map.get("username").toString().trim().equals("")){
						nickTextView.setText("������");
						map.put("username", "������");
					}
					else{
						nickTextView.setText(map.get("username").toString());
						Utils.username = map.get("username").toString();
					}
					if(map.get("mobile").toString().trim().equals("")||map.get("mobile") == null){
						phoneTextView.setText("�ֻ���"+Utils.user_id);
						map.put("mobile", "");
					}
					else{
						phoneTextView.setText("�ֻ���"+map.get("mobile").toString());
						map.put("mobile", map.get("mobile").toString());
					}
					if(!map.get("weixin").toString().trim().equals("")){
						qqTextView.setText("΢�ţ�"+map.get("weixin").toString());
					}
					else
						map.put("weixin", "");
					if(!map.get("qq").toString().trim().equals("")){
						 qqTextView.setText("QQ��"+map.get("qq").toString());
					}
					else
					{
						map.put("qq", "");
					}
					if(map.get("gender").toString().equals("2.0"))
						sexImageView.setImageResource(R.drawable.pe_sex_secret_pressed);
					else if(map.get("gender").toString().equals("0.0"))
						sexImageView.setImageResource(R.drawable.pe_sex_woman_pressed);
					else if (map.get("gender").toString().equals("1.0")) {
						sexImageView.setImageResource(R.drawable.pe_sex_man_pressed);
					}
				}
				else {
					Toast.makeText(getActivity(), "�������ӳ�ʱ", Toast.LENGTH_SHORT).show();
				}
				
				
			}
		};
		userManageService.getUserInfo(Utils.user_id, onQueryCompleteListener);
		imageLoader = ImageLoader.getInstance(getActivity());
		imageLoader.addTask(Utils.URL+Utils.user_id, photoImageView);
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		fTransaction = fManager.beginTransaction();
		switch (arg0.getId()) {
		case R.id.message_btn:
			intent.setClass(getActivity(),MessageCenter.class);
			startActivity(intent);
			break;
		case R.id.setting_btn:
			intent.setClass(getActivity(),SettingCenter.class);
			startActivity(intent);
			break;
		case R.id.goods:
			if(!goodFragment.isAdded()){
				fTransaction.remove(wishFragment);
				fTransaction.replace(R.id.list_ll, goodFragment);
				fTransaction.commit();
			}
			resetBunttonColor();
			goodsButton.setBackgroundResource(R.drawable.pc_btn_pressed);
			break;
		case R.id.wishes:
			if(!wishFragment.isAdded()){
				fTransaction.remove(goodFragment);
				fTransaction.replace(R.id.list_ll, wishFragment);
				fTransaction.commit();
			}
			resetBunttonColor();
			wishesButton.setBackgroundResource(R.drawable.pc_btn_pressed);
			break;
		case R.id.edit_img:
			intent.setClass(getActivity(), EditPersonCenter.class);
			Utils.IFEDITPERSON = 1;
			Bundle bundle = new Bundle();
			SerializableMap tmpmap=new SerializableMap();  
            tmpmap.setMap(map); 
            bundle.putSerializable("personinfo", tmpmap);
            intent.putExtras(bundle);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

}
