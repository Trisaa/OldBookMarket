package com.yunjian.activity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yunjian.adapter.BookDetailCommentAdapter;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.image.AsyncImageLoader;
import com.yunjian.image.ImageLoader;
import com.yunjian.service.BookService;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserManageService;
import com.yunjian.service.WishService;
import com.yunjian.util.ScreenShot;
import com.yunjian.util.Utils;
import com.yunjian.view.CircleImageView;
import com.yunjian.view.ConnectSellerPopwindow;
import com.yunjian.view.GestureListener;
import com.yunjian.view.InputPopwindow;
import com.yunjian.view.MyScrollView;
import com.yunjian.view.NoScrollListView;
import com.yunjian.view.ScrollListener;



public class BookDetailActivity extends Activity implements OnClickListener {

	private TextView title, readTime,publishDays,price,
	userName,userTel,userQQ,userWinxin,basicCondition,suitCrowd,
	myEvaluation,showAll,emptytTextView;
	private CircleImageView userImage;
	private LinearLayout llUserQQ,llUserWeChat;
	private RelativeLayout header,nextLayout;
	private ImageButton back;
	private ImageView bookDetailImage1,bookDetailImage2,bookDetailImage3;
	private Button nextSeller,frontSeller;
	private NoScrollListView comment;
	private ImageView bottomComment,bottomConnect,bottomShare,usersex;
	private Boolean showAllFlag=true;
	private List<Map<String, Object>> list;
	private List<Map<String, Object>> commentlist;
	private Map<String, Object>map;
	private int curPage=0,maxPage=0;
	private LinearLayout bottomLayout;
	private OnQueryCompleteListener onQueryCompleteListener;
	private ImageLoader imageLoader;
	private BookDetailCommentAdapter bookDetailCommentAdapter; 
	private BookService service;
	private String bookname,bookid;
	private int image_number = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.book_detail);

		imageLoader = ImageLoader.getInstance(this);
				
				
		initView();
		onQueryCompleteListener = new OnQueryCompleteListener() {
			
			@Override
			public void onQueryComplete(QueryId queryId, Object result, EHttpError error) {
				// TODO Auto-generated method stub
				if(result!=null){
					if(queryId.equals(BookService.GETBOOKBYNAME)){
						list = (List<Map<String, Object>>) result;
						getInfomation();
					}
					else if(queryId.equals(WishService.GETCOMMENT)){
						commentlist = (List<Map<String, Object>>) result;
                        if(commentlist.size() == 0){
                        	emptytTextView.setVisibility(View.VISIBLE);
                        	bookDetailCommentAdapter = new BookDetailCommentAdapter(BookDetailActivity.this,commentlist);
    						comment.setAdapter(bookDetailCommentAdapter);
                        }
                        else {
                        	emptytTextView.setVisibility(View.GONE);
                        	bookDetailCommentAdapter = new BookDetailCommentAdapter(BookDetailActivity.this,commentlist);
    						comment.setAdapter(bookDetailCommentAdapter);
						}
					}
					else if(queryId.equals(WishService.MAKECOMMENT)){
						if(result.equals("success")){
							Toast.makeText(BookDetailActivity.this, "评论成功", 2000).show();
						}
						else {
							Toast.makeText(BookDetailActivity.this, "评论失败", 2000).show();
						}
					}
					else if(queryId.equals(BookService.CLICKWISH)){
						if(result.equals("success")){
						}
						else {
						}
					}
				}
			}
		};
		
		service = new BookService();
		Intent intent = getIntent();
		bookname = intent.getStringExtra("bookname");
		service.getBooksByName(bookname, onQueryCompleteListener);
		
		

	}

	private void getInfomation()  {
		// TODO Auto-generated method stub
		if(list.size() == 1){
			nextLayout.setVisibility(View.GONE);
		}
		maxPage = list.size()-1;
		if(list.get(curPage).get("status").toString().equals("1.0")){
			bottomLayout.setVisibility(View.GONE);
		}
		title.setText((list.get(curPage).get("bookname")).toString());
		getDays();
		publishDays.setText(getDays()+"天前发布");
		price.setText(list.get(curPage).get("price").toString());
		userTel.setText(list.get(curPage).get("mobile").toString());
		String readtime = list.get(curPage).get("clicks").toString().substring(0, list.get(curPage).get("clicks").toString().length()-2);
		readTime.setText(readtime+"次浏览");
		
		map = list.get(curPage);
		bookid = list.get(curPage).get("book_id").toString();
		service.clickListener(bookid, onQueryCompleteListener);
		resetService();
		
		if(list.get(curPage).get("qq").toString().equals("")){
			llUserQQ.setVisibility(View.GONE);
		}else{
			llUserQQ.setVisibility(View.VISIBLE);
			userQQ.setText(list.get(curPage).get("qq").toString());
		}
		if(list.get(curPage).get("weixin").toString().equals("") ||list.get(curPage).get("weixin").toString().equals(null) ){
			llUserWeChat.setVisibility(View.GONE);
		}else{
			llUserWeChat.setVisibility(View.VISIBLE);
			userWinxin.setText(list.get(curPage).get("weixin").toString());
		}
		
		userName.setText(list.get(curPage).get("username").toString());
		System.out.println(list.get(curPage).get("gender").toString()+"  "+list.get(curPage).get("user_id").toString());
		if(list.get(curPage).get("gender").toString().equals("0.0")){
		    	usersex.setImageResource(R.drawable.user_sex_woman);
		}
		else if(list.get(curPage).get("gender").toString().equals("2.0")){
			usersex.setImageResource(R.drawable.user_sex_secret);
		}
		else if(list.get(curPage).get("gender").toString().equals("1.0")){
			usersex.setImageResource(R.drawable.user_sex_man);
		}
		
		basicCondition.setText(list.get(curPage).get("newness").toString());
		suitCrowd.setText(list.get(curPage).get("audience").toString());
		myEvaluation.setText(list.get(curPage).get("description").toString());
		imageLoader.addTask(Utils.URL+list.get(curPage).get("user_id").toString(), userImage);
		int length = list.get(curPage).get("imgs").toString().length();
		if(length<10){
			image_number = 0;
			bookDetailImage1.setVisibility(View.GONE);
			bookDetailImage2.setVisibility(View.GONE);
			bookDetailImage3.setVisibility(View.VISIBLE);
		}
		else if(length<40){
			image_number = 1;
			bookDetailImage1.setVisibility(View.GONE);
			bookDetailImage2.setVisibility(View.GONE);
			bookDetailImage3.setVisibility(View.VISIBLE);
			imageLoader.addTask(Utils.IMGURL+list.get(curPage).get("imgs").toString().substring(1,37), bookDetailImage3);
		}
		else if(length<80){
			image_number = 2;
			bookDetailImage1.setVisibility(View.GONE);
			bookDetailImage2.setVisibility(View.VISIBLE);
			bookDetailImage3.setVisibility(View.VISIBLE);
			imageLoader.addTask(Utils.IMGURL+list.get(curPage).get("imgs").toString().substring(1,37), bookDetailImage3);
			imageLoader.addTask(Utils.IMGURL+list.get(curPage).get("imgs").toString().substring(39,75), bookDetailImage2);
		}
		else{
			image_number = 3;
			bookDetailImage1.setVisibility(View.VISIBLE);
			bookDetailImage2.setVisibility(View.VISIBLE);
			bookDetailImage3.setVisibility(View.VISIBLE);
			imageLoader.addTask(Utils.IMGURL+list.get(curPage).get("imgs").toString().substring(1,37), bookDetailImage1);
			imageLoader.addTask(Utils.IMGURL+list.get(curPage).get("imgs").toString().substring(39,75), bookDetailImage2);
			imageLoader.addTask(Utils.IMGURL+list.get(curPage).get("imgs").toString().substring(77,113), bookDetailImage3);	
		}
		
	}
	public void resetService(){
		new WishService().getWishComment(bookid,  onQueryCompleteListener);
	}
	private int getDays() {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date adddate = null;
		try {
			adddate = sdf.parse(list.get(curPage).get("added_time").toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Date curDate = new Date(System.currentTimeMillis());//閼惧嘲褰囪ぐ鎾冲閺冨爼妫�

		Calendar cal0 = Calendar.getInstance();    
		cal0.setTime(adddate);
		Calendar cal1 = Calendar.getInstance();    
		cal1.setTime(curDate);    
		long time0 = cal0.getTimeInMillis(); 
		long time1 = cal1.getTimeInMillis(); 
		int days = (int) ((time1-time0)/(1000*3600*24));
		return days;
	}

	private void initView() {
		// TODO Auto-generated method stub
		title = (TextView)findViewById(R.id.book_detail_title);
		readTime = (TextView)findViewById(R.id.book_detail_read_time);
		publishDays = (TextView)findViewById(R.id.book_detail_publish_days);
		price = (TextView)findViewById(R.id.book_detail_price);
		userImage = (CircleImageView)findViewById(R.id.book_detail_user_image);
		userName = (TextView)findViewById(R.id.book_detail_user_name);
		userTel = (TextView)findViewById(R.id.book_detail_user_tel);
		userQQ = (TextView)findViewById(R.id.book_detail_user_QQ);
		userWinxin = (TextView)findViewById(R.id.book_detail_user_weixin);
		basicCondition = (TextView)findViewById(R.id.book_detail_basic_condition);
		suitCrowd = (TextView)findViewById(R.id.book_detail_suit_crowd);
		myEvaluation = (TextView)findViewById(R.id.book_detail_my_evaluation);
		showAll = (TextView)findViewById(R.id.book_detail_show_all);
		emptytTextView = (TextView)findViewById(R.id.empty_txv);
		usersex = (ImageView)findViewById(R.id.user_sex);
		
		llUserQQ = (LinearLayout)findViewById(R.id.ll_book_user_qq);
		llUserWeChat = (LinearLayout)findViewById(R.id.ll_book_user_wechat);
		
		bottomLayout = (LinearLayout)findViewById(R.id.bookdetail_bottomlayout);
		header = (RelativeLayout)findViewById(R.id.header);
		nextLayout = (RelativeLayout)findViewById(R.id.next_front_layout);
		
		back = (ImageButton)findViewById(R.id.bt_detail_back);
		
		bookDetailImage1 = (ImageView)findViewById(R.id.book_detail_image_1);
		bookDetailImage2 = (ImageView)findViewById(R.id.book_detail_image_2);
		bookDetailImage3 = (ImageView)findViewById(R.id.book_detail_image_3);
		
		nextSeller = (Button)findViewById(R.id.next_seller);
		frontSeller = (Button)findViewById(R.id.front_seller);
		
		comment = (NoScrollListView)findViewById(R.id.book_detail_comment);
		
		bottomComment = (ImageView)findViewById(R.id.book_detail_bottom_comment);
		bottomConnect = (ImageView)findViewById(R.id.book_detail_bottom_connect);
		bottomShare = (ImageView)findViewById(R.id.book_detail_bottom_share);
		back.setOnClickListener(this);
		showAll.setClickable(true);
		showAll.setOnClickListener(this);
		bottomComment.setClickable(true);
		bottomConnect.setClickable(true);
		bottomShare.setClickable(true);
		header.setLongClickable(true);
		header.setOnTouchListener(new MyGestureListener(this));
		nextSeller.setOnClickListener(this);
		frontSeller.setOnClickListener(this);
		bottomComment.setOnClickListener(this);
		bottomConnect.setOnClickListener(this);
		bottomShare.setOnClickListener(this);

	}

	public class MyGestureListener extends GestureListener {

		public MyGestureListener(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return super.onTouch(v, event);
		}

		@Override
		public boolean left() {
			if(image_number == 2){
				Bitmap bitmap = ((BitmapDrawable) bookDetailImage2.getDrawable())
						.getBitmap();
				bookDetailImage2.setImageBitmap(((BitmapDrawable) bookDetailImage3
						.getDrawable()).getBitmap());
				bookDetailImage3.setImageBitmap(bitmap);
					
			}else if(image_number == 3){
			Bitmap bitmap = ((BitmapDrawable) bookDetailImage1.getDrawable())
					.getBitmap();
			bookDetailImage1.setImageBitmap(((BitmapDrawable) bookDetailImage3
					.getDrawable()).getBitmap());
			bookDetailImage3.setImageBitmap(((BitmapDrawable) bookDetailImage2
					.getDrawable()).getBitmap());
			bookDetailImage2.setImageBitmap(bitmap);
			}
			return super.left();
		}

		@Override
		public boolean right() {
			if(image_number == 2){
				Bitmap bitmap = ((BitmapDrawable) bookDetailImage2.getDrawable())
						.getBitmap();
				bookDetailImage2.setImageBitmap(((BitmapDrawable) bookDetailImage3
						.getDrawable()).getBitmap());
				bookDetailImage3.setImageBitmap(bitmap);
			}else if(image_number == 3){
			Bitmap bitmap = ((BitmapDrawable) bookDetailImage1.getDrawable())
					.getBitmap();
			bookDetailImage1.setImageBitmap(((BitmapDrawable) bookDetailImage2
					.getDrawable()).getBitmap());
			bookDetailImage2.setImageBitmap(((BitmapDrawable) bookDetailImage3
					.getDrawable()).getBitmap());
			bookDetailImage3.setImageBitmap(bitmap);
			}
			return super.right();
		}

	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bt_detail_back:
			this.finish();
			break;
		case R.id.book_detail_show_all:
			if(showAllFlag){
				showAll.setText("向上收起");
				basicCondition.setSingleLine(false);
				suitCrowd.setSingleLine(false);
				myEvaluation.setSingleLine(false);
				showAllFlag=false;
			}else{
				basicCondition.setSingleLine(true);
				suitCrowd.setSingleLine(true);
				myEvaluation.setSingleLine(true);
				showAllFlag=true;
				showAll.setText("展开全部");
			}
			
			break;
			
		case R.id.front_seller:
			if(curPage>0){
				curPage--;
				getInfomation();
			}else{
				Toast.makeText(BookDetailActivity.this, "没有前一家", 2000).show();
			}
			break;
		case R.id.next_seller:
			if(curPage != maxPage){
				curPage++;
				getInfomation();
			}else{
				Toast.makeText(BookDetailActivity.this, "没有后一家", 2000).show();
			}
			break;
		case R.id.book_detail_bottom_comment:
			if(Utils.user_id.equals("")){
				Intent intent3 = new Intent(BookDetailActivity.this,LoginActivity.class);
				startActivity(intent3);
			}
			else {
				InputPopwindow inputPopwindow = new InputPopwindow(this,list.get(curPage).get("book_id").toString(),0);
	        	inputPopwindow.showAtLocation(this.findViewById(R.id.main_bottom), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			}
			break;
		case R.id.book_detail_bottom_connect:
			if(Utils.user_id.equals("")){
				Intent intent3 = new Intent(BookDetailActivity.this,LoginActivity.class);
				startActivity(intent3);
			}
			else {
				ConnectSellerPopwindow connectSellerPopwindow = new ConnectSellerPopwindow(BookDetailActivity.this, map);
				connectSellerPopwindow.showAtLocation(this.findViewById(R.id.main_bottom), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			}
			break;
		case R.id.book_detail_bottom_share:
			if(Utils.user_id.equals("")){
				Intent intent3 = new Intent(BookDetailActivity.this,LoginActivity.class);
				startActivity(intent3);
			}
			else {
				ScreenShot.shoot(this);
				shareMsg("/sdcard/share.png");
			}
			break;
		default:
			break;
		}
	}
	
	public void shareMsg(String imgPath) {  
	     Intent intent = new Intent(Intent.ACTION_SEND);  
	     if (imgPath == null || imgPath.equals("")) {  
	      intent.setType("text/plain"); // 纯文本  
	     } else {  
	      File f = new File(imgPath);  
	      if (f != null && f.exists() && f.isFile()) {  
	       intent.setType("image/png");
	        Uri u = Uri.fromFile(f);  
	       intent.putExtra(Intent.EXTRA_STREAM, u);  
	      }  
	     }  
	     intent.putExtra(Intent.EXTRA_SUBJECT, "分享");  
	     intent.putExtra(Intent.EXTRA_TEXT, "我在校园淘书上看到了这本书蛮有意思, 最便捷的二手书交易App, 人生之路, 淘书起步! http://120.27.51.45:5000/download/OldBookMarket.apk");  
	     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
	     startActivity(Intent.createChooser(intent, "请选择"));
	    }
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

}

