package com.yunjian.fragment;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.yunjian.activity.AddWishActivity;
import com.yunjian.activity.LoadingActivity;
import com.yunjian.activity.LoginActivity;
import com.yunjian.activity.R;
import com.yunjian.activity.WishDetailActivity;
import com.yunjian.adapter.BookAdapter;
import com.yunjian.adapter.WishAdapter;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.WishService;
import com.yunjian.util.Utils;
import com.yunjian.view.LoadingDialog;
import com.yunjian.view.PullToRefreshView;
import com.yunjian.view.PullToRefreshView.OnFooterRefreshListener;
import com.yunjian.view.PullToRefreshView.OnHeaderRefreshListener;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class WishFragment extends Fragment implements OnHeaderRefreshListener,
			OnFooterRefreshListener, OnClickListener{
	private PullToRefreshView mPullToRefreshView;
	private ListView listView;
	private LinearLayout productButton;
	private Button allbook,coursebook,english,japanese,technology,master,entertain;

	private WishAdapter adapter;
	private List<Map<String, Object>> list;
	private int page = 1;
	private String order_by = "clicks";
	private int type = 0;

	private WishService service;
	private OnQueryCompleteListener onQueryCompleteListener;
	private LoadingDialog loadingDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.wish_book, null);
		initView(view);
		return view;
	}
	
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initService();
	}
 	@Override
 	public void onPause() {
 	    super.onPause();
 	}



	public void initService(){
		service = new WishService();
		onQueryCompleteListener = new OnQueryCompleteListener() {

			@Override
			public void onQueryComplete(QueryId queryId, Object result,
					EHttpError error) {
				// TODO Auto-generated method stub
				loadingDialog.dismiss();
				if(WishService.LISTWISH.equals(queryId)){
					if (result != null) {
						if (page == 1) {
							list = (List<Map<String, Object>>) result;
							try {
								adapter = new WishAdapter(getActivity(), list);
							} catch (Exception e) {
								// TODO: handle exception
							}
							listView.setAdapter(adapter);
						} else {
							List<Map<String, Object>> temp = (List<Map<String, Object>>) result;
							for (int i = 0; i < temp.size(); i++) {
								list.add(temp.get(i));
							}
							adapter.notifyDataSetChanged();
						}

					} else {
						Toast.makeText(getActivity(), "网络连接超时", 2000).show();
						loadingDialog.dismiss();
					}
				}
				else if(queryId.equals(WishService.CLICKWISH)){
					System.out.println("click"+result);
				}
				
			}
		};
	    getCache();
		// 启动后台服务
	    if(LoadingActivity.isNetworkAvailable(getActivity())){
			resetService();
		}
 		else {
			Toast.makeText(getActivity(), "请检查你的网络", Toast.LENGTH_SHORT).show();
		}

	
	}

	public void resetService() {
		service.getWishes(type,String.valueOf(page), order_by,
				onQueryCompleteListener,getActivity());
		loadingDialog.show();
	}
	
	public void getCache(){
        String filename = "wishlist"; //获得读取的文件的名称  
        FileInputStream in = null;  
        ByteArrayOutputStream bout = null;  
        byte[]buf = new byte[1024];  
        bout = new ByteArrayOutputStream();  
        int length = 0;  
        try {  
            in = getActivity().openFileInput(filename); //获得输入流  
            while((length=in.read(buf))!=-1){  
                bout.write(buf,0,length);  
            }  
            byte[] content = bout.toByteArray();  
            Gson gson = new Gson();
			Type type = new TypeToken<Map<String, Object>>() {
			}.getType();
			Map<String, Object>wishes = gson.fromJson(new String(content,"UTF-8"), type);
			list = (List<Map<String, Object>>) wishes.get("wishes");
			adapter = new WishAdapter(getActivity(), list);
			listView.setAdapter(adapter);
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        try{  
            in.close();  
            bout.close();  
        }  
        catch(Exception e){}  
    } 
	
	public void resetButtonColor(){
		allbook.setTextColor(Color.BLACK);
		coursebook.setTextColor(Color.BLACK);
		english.setTextColor(Color.BLACK);
		japanese.setTextColor(Color.BLACK);
		technology.setTextColor(Color.BLACK);
		master.setTextColor(Color.BLACK);
		entertain.setTextColor(Color.BLACK);
		coursebook.setBackgroundResource(R.drawable.white);
		allbook.setBackgroundResource(R.drawable.white);
		english.setBackgroundResource(R.drawable.white);
		japanese.setBackgroundResource(R.drawable.white);
		technology.setBackgroundResource(R.drawable.white);
		master.setBackgroundResource(R.drawable.white);
		entertain.setBackgroundResource(R.drawable.white);
	}
	
	public void initView(View view)
	{
		mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.main_pull_refresh_view);
		listView = (ListView) view.findViewById(R.id.wish_book_list);
		productButton = (LinearLayout) view.findViewById(R.id.wish_product_btn);
		allbook = (Button)view.findViewById(R.id.all);
		coursebook = (Button)view.findViewById(R.id.coursebook);
		english = (Button)view.findViewById(R.id.english);
		japanese = (Button)view.findViewById(R.id.japanese);
		technology = (Button)view.findViewById(R.id.technology);
		master = (Button)view.findViewById(R.id.master);
		entertain = (Button)view.findViewById(R.id.entertain);
		productButton.setClickable(true);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						WishDetailActivity.class);
				intent.putExtra("wish_id", list.get(arg2).get("wish_id")
						.toString());
				startActivity(intent);
				
				service.clickListener(list.get(arg2).get("wish_id").toString(), onQueryCompleteListener);
			}
		});


		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		productButton.setOnClickListener(this);
		
		allbook.setOnClickListener(this);
		coursebook.setOnClickListener(this);
		english.setOnClickListener(this);
		japanese.setOnClickListener(this);
		technology.setOnClickListener(this);
		master.setOnClickListener(this);
		entertain.setOnClickListener(this);
		loadingDialog = new LoadingDialog(getActivity());

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.wish_product_btn:
			if(Utils.user_id.equals("")){
				Intent intent3 = new Intent(getActivity(),LoginActivity.class);
				startActivity(intent3);
			}
			else {
				Intent intent = new Intent(getActivity(), AddWishActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.all:
			type = 0;
			page = 1;
			resetService();
			resetButtonColor();
			loadingDialog.show();
			allbook.setTextColor(this.getResources().getColor(R.color.seagreen));
			allbook.setBackgroundResource(R.drawable.wish_type_bg);
			break;
		case R.id.coursebook:
			type = 1;
			page = 1;
			resetService();
			resetButtonColor();
			loadingDialog.show();
			coursebook.setTextColor(this.getResources().getColor(R.color.seagreen));
			coursebook.setBackgroundResource(R.drawable.wish_type_bg);
			break;
		case R.id.english:
			type = 2;
			page = 1;
			resetService();
			resetButtonColor();
			loadingDialog.show();
			english.setTextColor(this.getResources().getColor(R.color.seagreen));
			english.setBackgroundResource(R.drawable.wish_type_bg);
			break;
		case R.id.japanese:
			type = 3;
			page = 1;
			resetService();
			resetButtonColor();
			loadingDialog.show();
			japanese.setTextColor(this.getResources().getColor(R.color.seagreen));
			japanese.setBackgroundResource(R.drawable.wish_type_bg);
			break;
		case R.id.technology:
			type = 4;
			page = 1;
			resetService();
			resetButtonColor();
			loadingDialog.show();
			technology.setTextColor(this.getResources().getColor(R.color.seagreen));
			technology.setBackgroundResource(R.drawable.wish_type_bg);
			break;
		case R.id.master:
			type = 5;
			page = 1;
			resetService();
			resetButtonColor();
			loadingDialog.show();
			master.setTextColor(this.getResources().getColor(R.color.seagreen));
			master.setBackgroundResource(R.drawable.wish_type_bg);
			break;
		case R.id.entertain:
			type = 6;
			page = 1;
			resetService();
			resetButtonColor();
			loadingDialog.show();
			entertain.setTextColor(this.getResources().getColor(R.color.seagreen));
			entertain.setBackgroundResource(R.drawable.wish_type_bg);
			break;
		default:
			break;
		}
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				page++;
				System.out.println("页数" + page);
				service.getWishes(type,String.valueOf(page), order_by,
						onQueryCompleteListener,getActivity());
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 1000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				// 设置更新时间
				// mPullToRefreshView.onHeaderRefreshComplete("最近更新:01-23 12:01");
				list.clear();
				page = 1;
				service.getWishes(type,String.valueOf(page), order_by,
						onQueryCompleteListener,getActivity());
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 1000);
	}

}
