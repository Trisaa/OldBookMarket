package com.yunjian.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunjian.connection.HttpUtils;
import com.yunjian.connection.HttpUtils.EHttpError;

public class UserManageService {
	//��¼
	private final String LOGINWITHNAMEACTION = "user/login";
	//ע��
	private final String REGISTERACTION = "user/register";
	//�޸��û���Ϣ
	private final String SETUSERINFO = "user/setUserInfo";
	//��ȡ�û���Ϣ
	private final String GETUSERINFO = "user/getUserInfo";
	
	public static final QueryId LOGINWITHNAME = new QueryId();
	public static final QueryId GETUSERINFOMATION = new QueryId();	
	/*
	 * @function   �û���¼
	 * @param      �û��������롢�ص��ӿ� 
	 */
	public void UserLogin(String username,String password
			,OnQueryCompleteListener onQueryCompleteListener){
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("user_id", username));
		parms.add(new BasicNameValuePair("password", password));
		HttpUtils.makeAsyncPost(LOGINWITHNAMEACTION, parms,
				new QueryCompleteHandler(onQueryCompleteListener, LOGINWITHNAME) {
					
					@Override
					public void handleResponse(String jsonResult, EHttpError error) {
						  
						// TODO Auto-generated method stub
						System.out.println(jsonResult+"******************************");
						if (jsonResult != null
								&& error == EHttpError.KErrorNone) {
							this.completeListener.onQueryComplete(LOGINWITHNAME,
									jsonResult, error);
						}
						this.completeListener.onQueryComplete(LOGINWITHNAME,
								jsonResult, error);
					}
				});
	}
	/*
	 * @Function    �û��޸ĸ�����Ϣ
	 * @param       map���ص��ӿ�
	 * @return      
	 */
	public void SetUserInfo(Map<String, Object>map,OnQueryCompleteListener onQueryCompleteListener){
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		final String[] result = new String[1];
		result[0] = "failed";
		parms.add(new BasicNameValuePair("user_id", map.get("user_id").toString()));
		parms.add(new BasicNameValuePair("username", map.get("nick").toString()));
		parms.add(new BasicNameValuePair("gender", map.get("sex").toString()));
		parms.add(new BasicNameValuePair("mobile", map.get("mobile").toString()));
		parms.add(new BasicNameValuePair("qq", map.get("qq").toString()));
		parms.add(new BasicNameValuePair("weixin", map.get("wechat").toString()));
//		parms.add(new BasicNameValuePair("password", map.get("password").toString()));
		HttpUtils.makeAsyncPost(SETUSERINFO, parms,
				new QueryCompleteHandler(onQueryCompleteListener, new QueryId()) {
					
					@Override
					public void handleResponse(String jsonResult, EHttpError error) {
						// TODO Auto-generated method stub
						if (jsonResult != null
								&& error == EHttpError.KErrorNone) {
							if (!jsonResult.equals("failed")) {
								result[0] = "success";
							}
						}
						this.completeListener.onQueryComplete(new QueryId(),
								result[0], error);
						
					}
				});
	}
	
	/*
	 * @Function    �û��޸ĸ�����Ϣ
	 * @param       map���ص��ӿ�
	 * @return      
	 */
	public void ResetPassword(String user_id,String password,OnQueryCompleteListener onQueryCompleteListener){
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		final String[] result = new String[1];
		result[0] = "failed";
		parms.add(new BasicNameValuePair("user_id", user_id));
		parms.add(new BasicNameValuePair("password", password));
		HttpUtils.makeAsyncPost(SETUSERINFO, parms,
				new QueryCompleteHandler(onQueryCompleteListener, new QueryId()) {
					
					@Override
					public void handleResponse(String jsonResult, EHttpError error) {
						// TODO Auto-generated method stub
						if (jsonResult != null
								&& error == EHttpError.KErrorNone) {
							if (!jsonResult.equals("failed")) {
								result[0] = "success";
							}
						}
						this.completeListener.onQueryComplete(new QueryId(),
								result[0], error);
						
					}
				});
	}
	
	/*
	 * @Function  ��ȡ�û���Ϣ
	 * @param     user_id
	 * @return    json
	 */
	public void getUserInfo(String userId,OnQueryCompleteListener onQueryCompleteListener){
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("user_id", userId));
		HttpUtils.makeAsyncPost(GETUSERINFO, parms,
				new QueryCompleteHandler(onQueryCompleteListener, GETUSERINFOMATION) {
					
					@Override
					public void handleResponse(String jsonResult, EHttpError error) {
						// TODO Auto-generated method stub
						Map<String, Object> map = null;
						if (jsonResult != null
								&& error == EHttpError.KErrorNone) {
									Gson gson = new Gson();
									Type type = new TypeToken<Map<String, Object>>() {
									}.getType();
									map = gson.fromJson(jsonResult, type);
									System.out.println("�û���Ϣ"+map.toString());
									try {
										this.completeListener.onQueryComplete(GETUSERINFOMATION, map,
												error);
									} catch (Exception e) {
										// TODO: handle exception
										e.printStackTrace();
									}
					     }
						else {
							this.completeListener.onQueryComplete(GETUSERINFOMATION, null,
									error);
						}
					}
				});
	}
	
	/*
	 * @function �û�ע��
	 * @param  
	 */
	
	public void userRegister(String username,String userpassword,OnQueryCompleteListener onQueryCompleteListener){
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		final String[] result = new String[1];
		parms.add(new BasicNameValuePair("user_id", username));
		parms.add(new BasicNameValuePair("password", userpassword));
		HttpUtils.makeAsyncPost(REGISTERACTION, parms,
				new QueryCompleteHandler(onQueryCompleteListener, new QueryId()) {
					
					@Override
					public void handleResponse(String jsonResult, EHttpError error) {
						// TODO Auto-generated method stub
						System.out.println(jsonResult+"******************************");
						if (jsonResult != null
								&& error == EHttpError.KErrorNone) {
							this.completeListener.onQueryComplete(new QueryId(),
									jsonResult, error);
						}
						this.completeListener.onQueryComplete(new QueryId(),
								jsonResult, error);
					}
				});
	}
}
