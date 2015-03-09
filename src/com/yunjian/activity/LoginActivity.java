package com.yunjian.activity;

import java.util.HashMap;
import java.util.Map;


import com.umeng.analytics.MobclickAgent;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserManageService;
import com.yunjian.util.Utils;

import android.R.integer;
import android.annotation.SuppressLint;

import com.yunjian.view.LoadingDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private EditText username;
	private EditText password;
	private Button loginButton;
	private LinearLayout registerButton;
	private String usernameString;
	private String passwordString;
	private LinearLayout back;
	//private TextView sinaTextView,qqTextView;

	private UserManageService service;
	private OnQueryCompleteListener queryCompleteListener;
	private LoadingDialog loadingDialog;
	public static int from  = 0;
	public static Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		activity = this;
		initView();

	}

	public void initView() {
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.login_btn);
		registerButton = (LinearLayout) findViewById(R.id.register_btn);
		back = (LinearLayout) findViewById(R.id.login_back);
		/*sinaTextView = (TextView)findViewById(R.id.login_sina_txv);
		qqTextView = (TextView)findViewById(R.id.login_wechat_txv);*/
		service = new UserManageService();
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				usernameString = username.getText().toString();
				passwordString = password.getText().toString();
				if (usernameString.equals("")) {
					Toast.makeText(LoginActivity.this, "用户名不能为空", 2000).show();
				} else if (passwordString.equals("")) {
					Toast.makeText(LoginActivity.this, "密码不能为空", 2000).show();
				} else {
					service.UserLogin(usernameString, RegisterActivity.MD5(passwordString),
							queryCompleteListener);
					loadingDialog = new LoadingDialog(LoginActivity.this);
					loadingDialog.show();
				}

			}
		});
		registerButton.setClickable(true);
		registerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}
		});
		/*qqTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// qq
				Platform qq = ShareSDK.getPlatform(QQ.NAME);
				authorize(qq);
			}
		});*/

		queryCompleteListener = new OnQueryCompleteListener() {

			@SuppressLint("ShowToast")
			@Override
			public void onQueryComplete(QueryId queryId, Object result,
					EHttpError error) {
				// TODO Auto-generated method stub
					if (queryId == UserManageService.LOGINWITHNAME) {
						if (result.equals("success")) {
							Toast.makeText(LoginActivity.this, "登录成功", 2000).show();
							loadingDialog.dismiss();
							SharedPreferences sharedPreferences = getSharedPreferences(
									"userInfo", MODE_WORLD_READABLE);
							Editor editor = sharedPreferences.edit();
							editor.putString("user_id", usernameString);
							editor.putString("password", RegisterActivity.MD5(passwordString));
							Utils.user_id = usernameString;
							Utils.password = RegisterActivity.MD5(passwordString);
							editor.commit();
							if(LoginActivity.from == 1){
								Intent intent = new Intent(LoginActivity.this,MainActivity.class);
								startActivity(intent);
								LoginActivity.from = 0;
								finish();
							}
							else {
								finish();
							}
							UserManageService manageService = new UserManageService();
							manageService.getUserInfo(Utils.user_id,
									queryCompleteListener);
						}
						else{
							loadingDialog.dismiss();
							Toast.makeText(LoginActivity.this, "用户名或密码错误", 2000).show();
						}
					}
					
					else if (queryId == UserManageService.GETUSERINFOMATION) {
						if (result!=null) {
							Map<String, Object> map = (Map<String, Object>) result;
							String mobile = map.get("mobile").toString();
							String qq = map.get("qq").toString();
							String wechat = map.get("weixin").toString();
							String username = map.get("username").toString();
							SharedPreferences sharedPreferences = LoginActivity.this
									.getSharedPreferences("userInfo",
											Context.MODE_PRIVATE);
							Editor editor = sharedPreferences.edit();
							editor.putString("mobile", mobile);
							editor.putString("qq", qq);
							editor.putString("wechat", wechat);
							editor.putString("username", username);
							editor.commit();

							Utils.username = map.get("username").toString();
						}
					}
				} 
		};
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
