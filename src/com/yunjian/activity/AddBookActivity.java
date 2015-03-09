package com.yunjian.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.lei.activity.CaptureActivity;
import com.lei.model.Book;
import com.lei.util.BookUtil;
import com.umeng.analytics.MobclickAgent;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.image.ImageLoader;
import com.yunjian.service.BookService;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.util.Utils;
import com.yunjian.util.SerializableMap;
import com.yunjian.view.LoadingDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddBookActivity extends Activity implements OnClickListener,
		OnFocusChangeListener {
	private ImageView camaraImageView;
	private ImageView imageView1;
	private ImageView imageView2;
	private ImageView imageView3;
	private EditText booknameEditText;
	private EditText bookpriceEditText;
	private EditText bookqualityEditText;
	private GridLayout qualityLayout;
	private EditText bookwhoEditText;
	private GridLayout whoLayout;
	private EditText bookhelpEditText;
	private EditText phoneEditText;
	private EditText qqEditText;
	private EditText wechatEditText;
	private Button coursebook, english, japanese, technology, master,
			entertain;
	private Button[] qualityButtons;
	private Button[] whoButtons;
	private LinearLayout backButton;
	private LinearLayout okButton;

	private BookService bookService;
	private OnQueryCompleteListener onQueryCompleteListener;

	private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	private static final int PHOTO_REQUEST_ZXING = 4;// 结果
	private File tempFile;
	private String img1 = "";
	private String img2 = "";
	private String img3 = "";
	// 用来限制上传图片的张数
	private int i = 0;
	private String bookId = "";
	private Map<String, Object> map;
	private int booktype = 1;
	private LoadingDialog loadingDialog;

	private Handler handler;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_book);
		if (Utils.IFEDITBOOK == 1) {
			Bundle bundle = getIntent().getExtras();
			SerializableMap serMap = (SerializableMap) bundle.get("bookinfo");
			map = serMap.getMap();
		}else{
			Intent intent = new Intent(AddBookActivity.this, CaptureActivity.class);
			startActivityForResult(intent, PHOTO_REQUEST_ZXING);
		}
		initView();
		initButtons();

		// 接收来自下载线程的消息
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				Book book = (Book) msg.obj;
				// 进度条消失
				progressDialog.dismiss();
				if (book == null) {
					Toast.makeText(AddBookActivity.this, "未获取书籍信息, 请稍后再试或手动输入",
							Toast.LENGTH_LONG).show();
				} else {
					booknameEditText.setText(book.getTitle());
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					if(book.getBitmap()!=null){
						
						book.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100,
								out);
						try {
							out.flush();
							out.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						byte[] buffer = out.toByteArray();
						byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
						if (i == 0) {
							img1 = new String(encode);
							imageView1.setImageBitmap(book.getBitmap());
							i++;
						} 
					}
					else{
						Toast.makeText(AddBookActivity.this, "未获取书籍图片, 请重试或手动添加",
								Toast.LENGTH_LONG).show();
					}
					

					// Intent intent=new
					// Intent(AddBookActivity.this,AddBookActivity.class);
					// //通过Intent 传递 Object，需要让该实体类实现Parceable接口
					// intent.putExtra(Book.class.getName(),book);
					// finish();
					// startActivity(intent);
				}
			}
		};
		
		

	}

	/**
	 * @describe 异步下载并解析图书信息的线程类，线程结束后会发送Message消息，带有解析之后的Book对象
	 */
	private class LoadParseBookThread extends Thread {
		private String url;

		// 通过构造函数传递url地址
		public LoadParseBookThread(String urlstr) {
			url = urlstr;
		}

		public void run() {
			Message msg = Message.obtain();
			String result = BookUtil.getHttpRequest(url);
			try {
				Book book = new BookUtil().parseBookInfo(result);
				// 给主线程UI界面发消息，提醒下载信息，解析信息完毕
				msg.obj = book;
			} catch (Exception e) {
				e.printStackTrace();
			}
			handler.sendMessage(msg);
		}
	}

	

	public void initView() {
		camaraImageView = (ImageView) findViewById(R.id.takephoto);
		imageView1 = (ImageView) findViewById(R.id.img1);
		imageView2 = (ImageView) findViewById(R.id.img2);
		imageView3 = (ImageView) findViewById(R.id.img3);
		booknameEditText = (EditText) findViewById(R.id.addbook_name);
		bookpriceEditText = (EditText) findViewById(R.id.addbook_price);
		bookqualityEditText = (EditText) findViewById(R.id.addbook_quality);
		bookwhoEditText = (EditText) findViewById(R.id.addbook_who);
		bookhelpEditText = (EditText) findViewById(R.id.addbook_help);
		qualityLayout = (GridLayout) findViewById(R.id.quality_ll);
		whoLayout = (GridLayout) findViewById(R.id.who_ll);
		phoneEditText = (EditText) findViewById(R.id.addbook_phone);
		qqEditText = (EditText) findViewById(R.id.addbook_qq);
		wechatEditText = (EditText) findViewById(R.id.addbook_wechat);
		coursebook = (Button) findViewById(R.id.coursebook);
		english = (Button) findViewById(R.id.english);
		japanese = (Button) findViewById(R.id.japanese);
		technology = (Button) findViewById(R.id.technology);
		master = (Button) findViewById(R.id.master);
		entertain = (Button) findViewById(R.id.entertain);
		okButton = (LinearLayout) findViewById(R.id.addbook_ok_img);
		backButton = (LinearLayout) findViewById(R.id.addbook_back_img);
		okButton.setClickable(true);
		okButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
		camaraImageView.setOnClickListener(this);
		coursebook.setOnClickListener(this);
		english.setOnClickListener(this);
		japanese.setOnClickListener(this);
		technology.setOnClickListener(this);
		master.setOnClickListener(this);
		entertain.setOnClickListener(this);
		imageView1.setOnClickListener(this);
		imageView2.setOnClickListener(this);
		imageView3.setOnClickListener(this);

		bookqualityEditText.setOnFocusChangeListener(this);
		bookwhoEditText.setOnFocusChangeListener(this);
		bookhelpEditText.setOnFocusChangeListener(this);

		if (map != null) {
			System.out.println(map.toString());
			booknameEditText.setText(map.get("bookname").toString());
			bookpriceEditText.setText(map.get("price").toString());
			bookqualityEditText.setText(map.get("newness").toString());
			bookwhoEditText.setText(map.get("audience").toString());
			bookhelpEditText.setText(map.get("description").toString());
			phoneEditText.setText(map.get("mobile").toString());
			qqEditText.setText(map.get("qq").toString());
			wechatEditText.setText(map.get("weixin").toString());
			bookId = map.get("book_id").toString();
			resetButtonColor();
			if (map.get("type").toString().equals("1.0")) {
				coursebook.setTextColor(this.getResources().getColor(
						R.color.seagreen));
				booktype = 1;
			} else if (map.get("type").toString().equals("2.0")) {
				english.setTextColor(this.getResources().getColor(
						R.color.seagreen));
				booktype = 2;
			} else if (map.get("type").toString().equals("3.0")) {
				japanese.setTextColor(this.getResources().getColor(
						R.color.seagreen));
				booktype = 3;
			} else if (map.get("type").toString().equals("4.0")) {
				technology.setTextColor(this.getResources().getColor(
						R.color.seagreen));
				booktype = 4;
			} else if (map.get("type").toString().equals("5.0")) {
				master.setTextColor(this.getResources().getColor(
						R.color.seagreen));
				booktype = 5;
			} else if (map.get("type").toString().equals("6.0")) {
				entertain.setTextColor(this.getResources().getColor(
						R.color.seagreen));
				booktype = 6;
			}

			ImageLoader mImageLoader = ImageLoader.getInstance(this);
			// 图片加载
			try {
				mImageLoader.addTask(Utils.IMGURL
						+ map.get("imgs").toString().substring(1, 37),
						imageView1);
				mImageLoader.addTask(Utils.IMGURL
						+ map.get("imgs").toString().substring(39, 75),
						imageView2);
				mImageLoader.addTask(Utils.IMGURL
						+ map.get("imgs").toString().substring(77, 113),
						imageView3);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		} else {
			SharedPreferences sharedPreferences = getSharedPreferences(
					"userInfo", MODE_PRIVATE);

			String mobile = sharedPreferences.getString("mobile", "");
			String qq = sharedPreferences.getString("qq", "");
			String wechat = sharedPreferences.getString("wechat", "");

			phoneEditText.setText(mobile);
			qqEditText.setText(qq);
			wechatEditText.setText(wechat);

		}

		onQueryCompleteListener = new OnQueryCompleteListener() {

			@Override
			public void onQueryComplete(QueryId queryId, Object result,
					EHttpError error) {
				// TODO Auto-generated method stub
				loadingDialog.dismiss();
				if (result == null) {
					Toast.makeText(AddBookActivity.this, "网络连接超时", 2000).show();
				} else if (result.equals("success")) {
					String type[] = { "教材资料", "英语专区", "日语专区", "技术养成", "考研相关",
							"休闲阅读" };
					String st = "您的书籍已经发布到" + type[booktype - 1] + "类中";
					Toast.makeText(AddBookActivity.this, st, 2000).show();
					Utils.IFEDITBOOK = 0;
					finish();
				} else {
					Toast.makeText(AddBookActivity.this, "上传失败", 2000).show();
				}
			}
		};

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog();
		}
		return false;

	}

	public void initButtons() {
		qualityButtons = new Button[5];
		qualityButtons[0] = (Button) findViewById(R.id.new1);
		qualityButtons[1] = (Button) findViewById(R.id.new2);
		qualityButtons[2] = (Button) findViewById(R.id.new3);
		qualityButtons[3] = (Button) findViewById(R.id.new4);
		qualityButtons[4] = (Button) findViewById(R.id.new5);
		whoButtons = new Button[5];
		whoButtons[0] = (Button) findViewById(R.id.who1);
		whoButtons[1] = (Button) findViewById(R.id.who2);
		whoButtons[2] = (Button) findViewById(R.id.who3);
		whoButtons[3] = (Button) findViewById(R.id.who4);
		whoButtons[4] = (Button) findViewById(R.id.who5);
		Resources res = this.getResources(); // 这句放在onCreate中
		String[] arr = res.getStringArray(R.array.quality);
		for (int i = 0; i < 5; i++) {
			qualityButtons[i].setText(arr[i]);
			qualityButtons[i].setOnClickListener(this);
			whoButtons[i].setOnClickListener(this);
		}
		switch (booktype) {
		case 1:
			arr = res.getStringArray(R.array.coursebook);
			for (int i = 0; i < arr.length; i++)
				whoButtons[i].setText(arr[i]);
			break;
		case 2:
			arr = res.getStringArray(R.array.english);
			for (int i = 0; i < arr.length; i++)
				whoButtons[i].setText(arr[i]);
			break;
		case 3:
			arr = res.getStringArray(R.array.japanese);
			for (int i = 0; i < arr.length; i++)
				whoButtons[i].setText(arr[i]);
			break;
		case 4:
			arr = res.getStringArray(R.array.technology);
			for (int i = 0; i < arr.length; i++)
				whoButtons[i].setText(arr[i]);
			break;
		case 5:
			arr = res.getStringArray(R.array.master);
			for (int i = 0; i < arr.length; i++)
				whoButtons[i].setText(arr[i]);
			break;
		case 6:
			arr = res.getStringArray(R.array.entertain);
			for (int i = 0; i < arr.length; i++)
				whoButtons[i].setText(arr[i]);
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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.addbook_back_img:
			showDialog();
			break;
		case R.id.addbook_ok_img:
			loadingDialog = new LoadingDialog(AddBookActivity.this);
			loadingDialog.show();

			String bookname = booknameEditText.getText().toString();
			String bookquality = bookqualityEditText.getText().toString();
			String bookwho = bookwhoEditText.getText().toString();
			String bookhelp = bookhelpEditText.getText().toString();
			String phone = phoneEditText.getText().toString();
			String qq = qqEditText.getText().toString();
			String wechat = wechatEditText.getText().toString();
			if (bookname.equals("") || bookquality.equals("")
					|| bookwho.equals("") || bookhelp.equals("")
					|| bookpriceEditText.getText().toString().equals("")) {
				Toast.makeText(AddBookActivity.this, "信息填写不完整", 2000).show();
				loadingDialog.dismiss();
			} else if (phone.equals("")) {
				Toast.makeText(AddBookActivity.this, "电话号码是必填项喔", 2000).show();
				loadingDialog.dismiss();
			} 
			else if (bookhelp.length()<10) {
				Toast.makeText(AddBookActivity.this, "对书籍的描述不少于10个字喔", 2000).show();
				loadingDialog.dismiss();
			}
			else {
				float bookprice = Float.valueOf(bookpriceEditText.getText()
						.toString());
				if (bookprice>200) {
					Toast.makeText(AddBookActivity.this, "你的这本书也太贵了吧", 2000).show();
					loadingDialog.dismiss();
				}
				else {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("user_id", Utils.user_id);
					map.put("bookname", bookname);
					map.put("book_id", bookId);
					map.put("username", Utils.username);
					map.put("type", booktype);
					map.put("bookprice", bookprice);
					map.put("newness", bookquality);
					map.put("audience", bookwho);
					map.put("description", bookhelp);
					map.put("mobile", phone);
					map.put("qq", qq);
					map.put("wexin", wechat);
					map.put("img1", img1);
					map.put("img2", img2);
					map.put("img3", img3);
					bookService = new BookService();
					bookService.addBook(map, onQueryCompleteListener);
				}

			}
			break;
		case R.id.takephoto:
		case R.id.img1:
		case R.id.img2:
		case R.id.img3:
			
			if (i > 2) {
				Toast.makeText(AddBookActivity.this, "最多能上传三张图片喔", 2000).show();
			} else {
				String[] tempStrings = new String[] { "拍照上传", "从相册中选择" };
				Builder dialog = new AlertDialog.Builder(AddBookActivity.this)
						.setTitle("上传图片").setItems(tempStrings,
								new MyOnItemClickListener());
				dialog.show();
			}
			break;
		case R.id.coursebook:
			booktype = 1;
			resetButtonColor();
			initButtons();
			coursebook.setTextColor(this.getResources().getColor(
					R.color.seagreen));
			break;
		case R.id.english:
			booktype = 2;
			resetButtonColor();
			initButtons();
			english.setTextColor(this.getResources().getColor(R.color.seagreen));
			break;
		case R.id.japanese:
			booktype = 3;
			resetButtonColor();
			initButtons();
			japanese.setTextColor(this.getResources()
					.getColor(R.color.seagreen));
			break;
		case R.id.technology:
			booktype = 4;
			resetButtonColor();
			initButtons();
			technology.setTextColor(this.getResources().getColor(
					R.color.seagreen));
			break;
		case R.id.master:
			booktype = 5;
			resetButtonColor();
			initButtons();
			master.setTextColor(this.getResources().getColor(R.color.seagreen));
			break;
		case R.id.entertain:
			booktype = 6;
			resetButtonColor();
			initButtons();
			entertain.setTextColor(this.getResources().getColor(
					R.color.seagreen));
			break;
		case R.id.new1:
			bookqualityEditText.setText("这本书"+qualityButtons[0].getText());
			break;
		case R.id.new2:
			bookqualityEditText.setText("这本书"+qualityButtons[1].getText());
			break;
		case R.id.new3:
			bookqualityEditText.setText("这本书"+qualityButtons[2].getText());
			break;
		case R.id.new4:
			bookqualityEditText.setText("这本书"+qualityButtons[3].getText());
			break;
		case R.id.new5:
			bookqualityEditText.setText("这本书"+qualityButtons[4].getText());
			break;
		case R.id.who1:
			bookwhoEditText.setText("这本书属于"+whoButtons[0].getText());
			break;
		case R.id.who2:
			bookwhoEditText.setText("这本书属于"+whoButtons[1].getText());
			break;
		case R.id.who3:
			bookwhoEditText.setText("这本书属于"+whoButtons[2].getText());
			break;
		case R.id.who4:
			bookwhoEditText.setText("这本书属于"+whoButtons[3].getText());
			break;
		case R.id.who5:
			bookwhoEditText.setText("这本书属于"+whoButtons[4].getText());
			break;
//		case R.id.addbook_scan:
//			Intent intent = new Intent(AddBookActivity.this,
//					MainScanActivity.class);
//			finish();
//			startActivity(intent);
//			break;
		default:
			break;
		}
	}

	public class MyOnItemClickListener implements
			android.content.DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			if (arg1 == 1) {
				// 激活系统图库，选择一张图片
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
				startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
			} else if (arg1 == 0) {
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				// 判断存储卡是否可以用，可用进行存储
				if (hasSdcard()) {
					tempFile = new File(
							Environment.getExternalStorageDirectory(),
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

		if (requestCode == PHOTO_REQUEST_ZXING&&data!=null) {
			// 判断网络是否连接
			if (BookUtil.isNetworkConnected(this)) {
				progressDialog = new ProgressDialog(this);
				progressDialog.setMessage("请稍候，正在读取信息...");
				progressDialog.show();
				if(data.getExtras().getString("result")!=null){
					String urlstr = "https://api.douban.com/v2/book/isbn/"
							+ data.getExtras().getString("result");
					// 扫到ISBN后，启动下载线程下载图书信息
					new LoadParseBookThread(urlstr).start();
				}
			} else {
				Toast.makeText(this, "网络异常，请检查你的网络连接", Toast.LENGTH_LONG)
						.show();
			}
		} else if (requestCode == PHOTO_REQUEST_GALLERY) {
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
				Toast.makeText(AddBookActivity.this, "未找到存储卡，无法存储照片！", 0)
						.show();
			}

		} else if (requestCode == PHOTO_REQUEST_CUT) {
			// 从剪切图片返回的数据
			if (data != null) {
				Bitmap bitmap = data.getParcelableExtra("data");
				// photoImageView.setImageBitmap(bitmap);
				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
					out.flush();
					out.close();
					byte[] buffer = out.toByteArray();
					byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
					if (i == 0) {
						img1 = new String(encode);
						imageView1.setImageBitmap(bitmap);
						i++;
					} else if (i == 1) {
						img2 = new String(encode);
						imageView2.setImageBitmap(bitmap);
						i++;
					} else {
						img3 = new String(encode);
						imageView3.setImageBitmap(bitmap);
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
	public void onFocusChange(View arg0, boolean arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.addbook_quality:
			qualityLayout.setVisibility(View.VISIBLE);
			whoLayout.setVisibility(View.GONE);
			bookqualityEditText.clearFocus();
			break;
		case R.id.addbook_who:
			whoLayout.setVisibility(View.VISIBLE);
			qualityLayout.setVisibility(View.GONE);
			bookwhoEditText.clearFocus();
			break;
		case R.id.addbook_help:
			qualityLayout.setVisibility(View.GONE);
			whoLayout.setVisibility(View.GONE);
			break;

		default:
			break;
		}
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
