package com.yunjian.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.umeng.analytics.MobclickAgent;
import com.yunjian.activity.AddBookActivity.MyOnItemClickListener;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.image.ImageLoader;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.util.Utils;
import com.yunjian.service.WishService;
import com.yunjian.util.SerializableMap;
import com.yunjian.view.LoadingDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddWishActivity extends Activity implements OnClickListener {
	private LinearLayout backButton;
	private Button okButton;
	private ImageView photoImageView,bookImageView;
	private EditText wishnameEditText;
	private EditText wishdescripEditText;
	private EditText phoneEditText;
	private EditText qqEditText;
	private EditText wechaText;
	private Button coursebook, english, japanese, technology, master,
			entertain;
	private LoadingDialog loadingDialog;

	private WishService wishService;
	private OnQueryCompleteListener onQueryCompleteListener;
	private String wishId = "";
	private int type = -1;
	private Map<String, Object> map;
	private static final int PHOTO_REQUEST_CAREMA = 1;//拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;//从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;//结果
	private File tempFile;
	private String img1="";
	//用来限制上传图片的张数
	private int i=0;
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_wish);
		if (Utils.IFEDITWISH == 1) {
			Bundle bundle = getIntent().getExtras();
			SerializableMap serMap = (SerializableMap) bundle.get("wishinfo");
			map = serMap.getMap();
		}
		initView();
	}

	public void initView() {
		backButton = (LinearLayout) findViewById(R.id.addwish_back_img);
		okButton = (Button) findViewById(R.id.addwish_ok_img);
		photoImageView = (ImageView) findViewById(R.id.addwish_takephoto);
		bookImageView = (ImageView)findViewById(R.id.addwish_img1);
		wishnameEditText = (EditText) findViewById(R.id.addwish_name);
		wishdescripEditText = (EditText) findViewById(R.id.addwish_description);
		phoneEditText = (EditText) findViewById(R.id.addwish_phone);
		qqEditText = (EditText) findViewById(R.id.addwish_qq);
		wechaText = (EditText) findViewById(R.id.addwish_wechat);
		coursebook = (Button) findViewById(R.id.addwish_coursebook);
		english = (Button) findViewById(R.id.addwish_english);
		japanese = (Button) findViewById(R.id.addwish_japanese);
		technology = (Button) findViewById(R.id.addwish_technology);
		master = (Button) findViewById(R.id.addwish_master);
		entertain = (Button) findViewById(R.id.addwish_entertain);

		backButton.setOnClickListener(this);
		okButton.setOnClickListener(this);
		photoImageView.setOnClickListener(this);
		coursebook.setOnClickListener(this);
		english.setOnClickListener(this);
		japanese.setOnClickListener(this);
		technology.setOnClickListener(this);
		master.setOnClickListener(this);
		entertain.setOnClickListener(this);
		bookImageView.setOnClickListener(this);
		if (map != null) {
			wishnameEditText.setText(map.get("bookname").toString());
			wishdescripEditText.setText(map.get("description").toString());
			phoneEditText.setText(map.get("mobile").toString());
			qqEditText.setText(map.get("qq").toString());
			wechaText.setText(map.get("weixin").toString());
			wishId = map.get("wish_id").toString();
			 //图片加载
			try {
				int length = map.get("imgs").toString().length();
				if(length>10){
					ImageLoader mImageLoader = ImageLoader.getInstance(AddWishActivity.this);
					mImageLoader.addTask(Utils.IMGURL+map.get("imgs").toString().substring(1,37), bookImageView);
				}
			} catch (Exception e) {
			// TODO: handle exception
				e.printStackTrace();
			}
			resetButtonColor();
			if (map.get("type").toString().equals("1.0")) {
				coursebook
						.setBackgroundResource(R.drawable.addwish_btn_pressed);
				type = 1;
			} else if (map.get("type").toString().equals("2.0")) {
				english.setBackgroundResource(R.drawable.addwish_btn_pressed);
				type = 2;
			} else if (map.get("type").toString().equals("3.0")) {
				japanese.setBackgroundResource(R.drawable.addwish_btn_pressed);
				type = 3;
			} else if (map.get("type").toString().equals("4.0")) {
				technology
						.setBackgroundResource(R.drawable.addwish_btn_pressed);
				type = 4;
			} else if (map.get("type").toString().equals("5.0")) {
				master.setBackgroundResource(R.drawable.addwish_btn_pressed);
				type = 5;
			} else if (map.get("type").toString().equals("6.0")) {
				entertain.setBackgroundResource(R.drawable.addwish_btn_pressed);
				type = 6;
			}
		}else{
			SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);

			String mobile = sharedPreferences.getString("mobile","");
			String qq = sharedPreferences.getString("qq","");
			String wechat = sharedPreferences.getString("wechat","");
			
			phoneEditText.setText(mobile);
			qqEditText.setText(qq);
			wechaText.setText(wechat);
			
		}

		onQueryCompleteListener = new OnQueryCompleteListener() {

			@Override
			public void onQueryComplete(QueryId queryId, Object result,
					EHttpError error) {
				// TODO Auto-generated method stub
				loadingDialog.dismiss();
				if (result==null) {
					Toast.makeText(AddWishActivity.this, "网络连接超时", 2000).show();
				} else if (result.equals("success")) {
					String stype[]={"教材资料","英语专区","日语专区","技术养成","考研相关","休闲阅读"};
					String st = "您的心愿已经发布到"+stype[type-1]+"类中";
					Toast.makeText(AddWishActivity.this, st, 2000).show();Utils.IFEDITWISH = 0;
					finish();
				} else {
					Toast.makeText(AddWishActivity.this, "发布失败", 2000).show();
				}
			}
		};
	}
	
	 @Override  
	    public boolean onKeyDown(int keyCode, KeyEvent event)  
	    {  
	        if (keyCode == KeyEvent.KEYCODE_BACK )  
	        {  
	           showDialog();
	        }  
	        return false;  
	          
	    }  

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.addwish_back_img:
			showDialog();
			break;
		case R.id.addwish_ok_img:
			String wishname = wishnameEditText.getText().toString();
			String wishdescrip = wishdescripEditText.getText().toString();
			String phone = phoneEditText.getText().toString();
			String qq = qqEditText.getText().toString();
			String wechat = wechaText.getText().toString();
			if (wishname.equals("") || wishdescrip.equals("")) {
				Toast.makeText(AddWishActivity.this, "所填信息不完整呢", 2000).show();
			} else if (type == -1) {
				Toast.makeText(AddWishActivity.this, "为你的心愿单选个分类吧", 2000)
						.show();
			} else if (phone.equals("")) {
				Toast.makeText(AddWishActivity.this, "大侠，留下你的电话吧", 2000).show();
			} else {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("user_id", Utils.user_id);
				map.put("username", Utils.username);
				map.put("wish_id", wishId);
				map.put("bookname", wishname);
				map.put("description", wishdescrip);
				map.put("mobile", phone);
				map.put("qq", qq);
				map.put("wexin", wechat);
				map.put("type", type);
				map.put("img1", img1);
				wishService = new WishService();
				wishService.addWish(map, onQueryCompleteListener);
				loadingDialog = new LoadingDialog(this);
				loadingDialog.show();
			}
			break;
		case R.id.addwish_takephoto:
		case R.id.addwish_img1:
			if(i>0){
				Toast.makeText(AddWishActivity.this, "最多能上传一张图片喔", 2000).show();
			}
			else {
				String[] tempStrings = new String[]{"拍照上传", "从相册中选择"};
				Builder dialog = new AlertDialog.Builder(AddWishActivity.this)
					.setTitle("上传图片").setItems(tempStrings, new MyOnItemClickListener());
				dialog.show();
			}
			break;
		case R.id.addwish_coursebook:
			type = 1;
			resetButtonColor();
			coursebook.setBackgroundResource(R.drawable.addwish_btn_pressed);
			break;
		case R.id.addwish_english:
			type = 2;
			resetButtonColor();
			english.setBackgroundResource(R.drawable.addwish_btn_pressed);
			break;
		case R.id.addwish_japanese:
			type = 3;
			resetButtonColor();
			japanese.setBackgroundResource(R.drawable.addwish_btn_pressed);
			break;
		case R.id.addwish_technology:
			type = 4;
			resetButtonColor();
			technology.setBackgroundResource(R.drawable.addwish_btn_pressed);
			break;
		case R.id.addwish_master:
			type = 5;
			resetButtonColor();
			master.setBackgroundResource(R.drawable.addwish_btn_pressed);
			break;
		case R.id.addwish_entertain:
			type = 6;
			resetButtonColor();
			entertain.setBackgroundResource(R.drawable.addwish_btn_pressed);
			break;

		default:
			break;
		}
	}

	public void resetButtonColor() {
		coursebook.setTextColor(Color.BLACK);
		english.setTextColor(Color.BLACK);
		japanese.setTextColor(Color.BLACK);
		technology.setTextColor(Color.BLACK);
		master.setTextColor(Color.BLACK);
		entertain.setTextColor(Color.BLACK);
		coursebook.setBackgroundResource(R.drawable.addwish_btn_unpressed);
		english.setBackgroundResource(R.drawable.addwish_btn_unpressed);
		japanese.setBackgroundResource(R.drawable.addwish_btn_unpressed);
		technology.setBackgroundResource(R.drawable.addwish_btn_unpressed);
		master.setBackgroundResource(R.drawable.addwish_btn_unpressed);
		entertain.setBackgroundResource(R.drawable.addwish_btn_unpressed);
	}
	
	private void showDialog() {
		new AlertDialog.Builder(this).setTitle("提醒")
				.setMessage("确定要放弃正在编辑的内容么？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						arg0.cancel();
					}
				}).show();
	}
	
	public class MyOnItemClickListener implements android.content.DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			if(arg1 == 1){
				// 激活系统图库，选择一张图片
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
				startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
			}else if(arg1 == 0){
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				// 判断存储卡是否可以用，可用进行存储
				if (hasSdcard()) {
					tempFile = new File(Environment.getExternalStorageDirectory(),
							Utils.user_id);
					// 从文件中创建uri
					Uri uri = Uri.fromFile(tempFile);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				}
				// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
				startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
			}
		}
		
	}
	
	private void crop(Uri uri) {
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1.5);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 172);
		intent.putExtra("outputY", 243);

		intent.putExtra("outputFormat", "JPEG");// 图片格式
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);
		// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	/*
	 * 判断sdcard是否被挂载
	 */
	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTO_REQUEST_GALLERY) {
			// 从相册返回的数据
			if (data != null) {
				// 得到图片的全路径
				Uri uri = data.getData();
				crop(uri);
			}

		} else if (requestCode == PHOTO_REQUEST_CAREMA) {
			// 从相机返回的数据
			if (hasSdcard()) {
				crop(Uri.fromFile(tempFile));
			} else {
				Toast.makeText(AddWishActivity.this, "未找到存储卡，无法存储照片！", 0).show();
			}

		} else if (requestCode == PHOTO_REQUEST_CUT) {
			// 从剪切图片返回的数据
			if (data != null) {
				Bitmap bitmap = data.getParcelableExtra("data");
				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
					out.flush();
					out.close();
					byte[] buffer = out.toByteArray();  
			        byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
			        if(i==0){
			        	img1 = new String(encode);
			        	bookImageView.setImageBitmap(bitmap);
			        	i++;
			        }
			        
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			try {
				// 将临时文件删除
				tempFile.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
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
