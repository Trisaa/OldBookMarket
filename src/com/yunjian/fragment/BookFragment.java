package com.yunjian.fragment;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.yunjian.activity.AddBookActivity;
import com.yunjian.activity.BookDetailActivity;
import com.yunjian.activity.LoadingActivity;
import com.yunjian.activity.LoginActivity;
import com.yunjian.activity.MainActivity;
import com.yunjian.activity.R;
import com.yunjian.activity.RegisterActivity;
import com.yunjian.activity.SearchActivity;
import com.yunjian.adapter.BookAdapter;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.BookService;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserManageService;
import com.yunjian.util.Utils;
import com.yunjian.view.LoadingDialog;
import com.yunjian.view.MyGridView;
import com.yunjian.view.MyScrollView;
import com.yunjian.view.PullToRefreshView;
import com.yunjian.view.PullToRefreshView.OnFooterRefreshListener;
import com.yunjian.view.PullToRefreshView.OnHeaderRefreshListener;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class BookFragment extends Fragment implements 
         OnHeaderRefreshListener, OnFooterRefreshListener,OnClickListener{
	 private PullToRefreshView mPullToRefreshView;
	 private TextView middleText;
	 private LinearLayout topSelect1;
	 private LinearLayout topSelect2;
	 private LinearLayout topSelect3;
	 private LinearLayout topSelect4;
	 private LinearLayout topSelect5;
	 private LinearLayout topSelect6;
	 private LinearLayout searchButton;
     private MyGridView gridView; 
     private LinearLayout addBookButton;
     private RadioGroup radioGroup;
     private RadioButton highest;
     private RadioButton newest;
     
     private int page = 1;
     //书籍分类
     private String type = "1";
     private String order_by  = "added_time";
     
     private BookService service;
     private OnQueryCompleteListener onQueryCompleteListener;
     private List<Map<String, Object>>booklist;   
     private BookAdapter bookAdapter;
     public static boolean hasMessageString = false;

 	private LoadingDialog loadingDialog;
 	private View view;

 	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.main_page, null);
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
 		if(sharedPreferences.getString("user_id", null)!= null){
 			System.out.println("main_page user_id=============="+sharedPreferences.getString("user_id", null));
 			Utils.user_id = sharedPreferences.getString("user_id", null);
 			Utils.password = sharedPreferences.getString("password", null);
 			Utils.username = sharedPreferences.getString("username", "淘书者");
 			//UserManageService manageService  = new UserManageService();
 	 		//manageService.getUserInfo(Utils.user_id, onQueryCompleteListener);
 		}
 		else{
 			Editor editor = sharedPreferences.edit();
			editor.remove("user_id");
			editor.remove("username");
			editor.remove("password");
			editor.remove("mobile");
			editor.remove("qq");
			editor.remove("wechat");
			editor.commit();
			Utils.user_id = "";
			Utils.username = "";
 		}
 		loadingDialog = new LoadingDialog(getActivity());
 		initView(view);
		initView2(view);
		gridView.setOnItemClickListener(new OnItemClickListener() 
        { 
            @Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
            { 
            	Intent intent = new Intent(getActivity(),BookDetailActivity.class);
            	intent.putExtra("bookname", booklist.get(position).get("bookname").toString());
                startActivity(intent);
            }
           
        });
		gridView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                        return true ;
                default:
                        break;
                }
                return false;
			}
		});
	    searchButton.setClickable(true);
	    searchButton.setOnClickListener(this);
		return view;
	}
	
	
	public void initView(View view){
    	addBookButton = (LinearLayout)view.findViewById(R.id.main_page_add);
        mPullToRefreshView = (PullToRefreshView)view.findViewById(R.id.main_pull_refresh_view);
        gridView = (MyGridView)view.findViewById(R.id.gridview);	
        searchButton = (LinearLayout)view.findViewById(R.id.main_page_search);
        middleText = (TextView)view.findViewById(R.id.middle_text);
        
        radioGroup = (RadioGroup)view.findViewById(R.id.gendergroup);
        highest = (RadioButton)view.findViewById(R.id.highest);
        newest = (RadioButton)view.findViewById(R.id.newest);
    	addBookButton.setOnClickListener(this);
    	mPullToRefreshView.setOnHeaderRefreshListener(this);
 		mPullToRefreshView.setOnFooterRefreshListener(this);
 		addBookButton.setClickable(true);
 		
 		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				if(arg1 == highest.getId()){
					order_by = "added_time";
					page = 1;
					resetService();
				}
				else if(arg1 == newest.getId()){
					order_by = "clicks";
					page = 1;
					resetService();
				}
			}
		});
 		
 		onQueryCompleteListener = new OnQueryCompleteListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void onQueryComplete(QueryId queryId, Object result, EHttpError error) {
				// TODO Auto-generated method stub
				if(result !=null){
				if(queryId.equals(BookService.GETBOOKBYTAPE)){	
					loadingDialog.dismiss();
					if(page == 1 ){
						Map<String, Object>map = (Map<String, Object>) result;
						booklist = (List<Map<String, Object>>) map.get("books");
						hasMessageString = (Boolean) map.get("has_messages");
						System.out.println(hasMessageString);
						if(hasMessageString){
							((MainActivity) getActivity()).hasMessages();
						}
						try {
							bookAdapter = new BookAdapter(getActivity(), booklist);
						} catch (Exception e) {
							// TODO: handle exception
						}
						gridView.setAdapter(bookAdapter);
					}
					else {
						Map<String, Object>map = (Map<String, Object>) result;
						List<Map<String, Object>>temp = (List<Map<String, Object>>) map.get("books");
						for(int i=0;i<temp.size();i++){
							booklist.add(temp.get(i));
						}
					     bookAdapter.notifyDataSetChanged();
					}
				}else if(queryId.equals(UserManageService.GETUSERINFOMATION)){
					
					Map<String, Object> map = (Map<String, Object>) result;

					String mobile = map.get("mobile").toString();
					String qq = map.get("qq").toString();
					String wechat = map.get("weixin").toString();
					SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
					Editor editor = sharedPreferences.edit();
					editor.putString("mobile",mobile);
					editor.putString("qq",qq);
					editor.putString("wechat",wechat);
					editor.commit();
				}
				}
				else {
					Toast.makeText(getActivity(), "网络连接超时", 2000).show();
				}
			}
		};
		getCache();
		service = new BookService();
		if(LoadingActivity.isNetworkAvailable(getActivity())){
			resetService();
		}
 		else {
			Toast.makeText(getActivity(), "请检查你的网络", Toast.LENGTH_SHORT).show();
		}
	}
	
	 public void initView2(View view){
    	 topSelect1 = (LinearLayout)view.findViewById(R.id.top_select_1);
         topSelect2 = (LinearLayout)view.findViewById(R.id.top_select_2);
         topSelect3 = (LinearLayout)view.findViewById(R.id.top_select_3);
         topSelect4 = (LinearLayout)view.findViewById(R.id.top_select_4);
         topSelect5 = (LinearLayout)view.findViewById(R.id.top_select_5);
         topSelect6 = (LinearLayout)view.findViewById(R.id.top_select_6);
         
         topSelect1.setOnClickListener(this);
         topSelect2.setOnClickListener(this);
         topSelect3.setOnClickListener(this);
         topSelect4.setOnClickListener(this);
         topSelect5.setOnClickListener(this);
         topSelect6.setOnClickListener(this);
    }
    
    public void resetService(){
    	service.getBooksByType(type, order_by, page+"", onQueryCompleteListener,getActivity());
    	loadingDialog.show();
    }
    
    public void getCache(){
        String filename = "booklist"; //获得读取的文件的名称  
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
            Map<String, Object>books;
            Gson gson = new Gson();
			Type type = new TypeToken<Map<String, Object>>() {
			}.getType();
			books = gson.fromJson(new String(content,"UTF-8"), type);
			booklist = (List<Map<String, Object>>) books.get("books");
			bookAdapter = new BookAdapter(getActivity(), booklist);
			gridView.setAdapter(bookAdapter);
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        try{  
            in.close();  
            bout.close();  
        }  
        catch(Exception e){}  
    } 

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.main_page_add:
			if(Utils.user_id.equals("")){
				Intent intent3 = new Intent(getActivity(),LoginActivity.class);
				startActivity(intent3);
			}
			else {
				Utils.IFEDITBOOK = 0;
				Intent intent = new Intent(getActivity(),AddBookActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.top_select_1:
			type = "1";
			middleText.setText("教材资料");
			resetService();
			break;
		case R.id.top_select_2:
			type = "2";
			middleText.setText("英语专区");
			resetService();
			break;
		case R.id.top_select_3:
			type = "3";
			middleText.setText("日语专区");
			resetService();
			break;
		case R.id.top_select_4:
			type = "4";
			middleText.setText("技术养成");
			resetService();
			break;
		case R.id.top_select_5:
			type = "5";
			middleText.setText("考研相关");
			resetService();
			break;
		case R.id.top_select_6:
			type = "6";
			middleText.setText("休闲阅读");
			resetService();
			break;
		case R.id.main_page_search:
			Intent intent1 = new Intent(getActivity(),SearchActivity.class);
			startActivity(intent1);
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
				service.getBooksByType(type, order_by, page+"", onQueryCompleteListener,getActivity());
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
				booklist.clear();
				page = 1;
				service.getBooksByType(type, order_by, page+"", onQueryCompleteListener,getActivity());
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 1000);
	}

}
