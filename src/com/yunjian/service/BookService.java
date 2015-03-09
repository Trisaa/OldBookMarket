package com.yunjian.service;

import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunjian.connection.HttpUtils;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.util.Utils;

public class BookService extends Service{
	private final String ADDBOOKACTION = "book/setBookInfo";
	
	private final String GETBOOKSBYTYPEACTION = "book/getBooksByType";
	
	private final String GETBOOKBYNAMEACTION = "book/getBooksByName";
	
	private final String SEARCHBOOKACTION = "book/searchBook";
	
	private final String GETSIMILARBOOKNAME = "book/getSimilarBookname";
	
	private final String CLICKBOOKACTION = "book/bookClicked";
	
	public final static QueryId GETBOOKBYNAME = new QueryId();
	public static final QueryId GETBOOKBYTAPE = new QueryId();	
	public final static QueryId CLICKWISH = new QueryId();
	public static final QueryId AUTOCOMPLETE = new QueryId();
	/*
	 * @function   发布二手书
	 * @param      
	 * @return     
	 */
	
	public void addBook(Map<String, Object>map,OnQueryCompleteListener onQueryCompleteListener){
		List<BasicNameValuePair>parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("user_id", map.get("user_id").toString()));
		parms.add(new BasicNameValuePair("bookname", map.get("bookname").toString()));
		parms.add(new BasicNameValuePair("book_id", map.get("book_id").toString()));
		parms.add(new BasicNameValuePair("username", map.get("username").toString()));
		parms.add(new BasicNameValuePair("price", map.get("bookprice").toString()));
		parms.add(new BasicNameValuePair("type", map.get("type").toString()));
		parms.add(new BasicNameValuePair("newness", map.get("newness").toString()));
		parms.add(new BasicNameValuePair("audience", map.get("audience").toString()));
		parms.add(new BasicNameValuePair("description", map.get("description").toString()));
		parms.add(new BasicNameValuePair("mobile", map.get("mobile").toString()));
		parms.add(new BasicNameValuePair("qq", map.get("qq").toString()));
		parms.add(new BasicNameValuePair("weixin", map.get("wexin").toString()));
		parms.add(new BasicNameValuePair("img1", map.get("img1").toString()));
		parms.add(new BasicNameValuePair("img2", map.get("img2").toString()));
		parms.add(new BasicNameValuePair("img3", map.get("img3").toString()));
		System.out.println("addbook");
		HttpUtils.makeAsyncPost(ADDBOOKACTION, parms,
				new QueryCompleteHandler(onQueryCompleteListener, new QueryId()) {
					
					@Override
					public void handleResponse(String jsonResult, EHttpError error) {
						// TODO Auto-generated method stub
						if(jsonResult!=null&&error == EHttpError.KErrorNone){
							System.out.println("发布书单"+jsonResult);
							this.completeListener.onQueryComplete(new QueryId(), jsonResult, error);
						}
						else {
							this.completeListener.onQueryComplete(new QueryId(), null, error);
						}
					}
				});
 	}
	
	/*
	 * @function   得到二手书
	 * @param      
	 * @return     
	 */
	
	public void getBooksByType(String type,String order_by,String page,OnQueryCompleteListener onQueryCompleteListener,final Context context){
		List<BasicNameValuePair>parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("type", type));
		parms.add(new BasicNameValuePair("order_by", order_by));
		parms.add(new BasicNameValuePair("page", page));
		parms.add(new BasicNameValuePair("pagesize","18"));
		parms.add(new BasicNameValuePair("user_id",Utils.user_id));
		HttpUtils.makeAsyncPost(GETBOOKSBYTYPEACTION, parms,
				new QueryCompleteHandler(onQueryCompleteListener, GETBOOKBYTAPE) {
					Map<String, Object>books;
					@Override
					public void handleResponse(String jsonResult, EHttpError error) {
						// TODO Auto-generated method stub
						if(jsonResult!=null&&error == EHttpError.KErrorNone){
							Gson gson = new Gson();
							Type type = new TypeToken<Map<String, Object>>() {
							}.getType();
							books = gson.fromJson(jsonResult, type);
							List<Map<String, Object>>list = (List<Map<String, Object>>) books.get("books");
                            saveCache(context, jsonResult);
							System.out.println("旧书"+list);
							this.completeListener.onQueryComplete(GETBOOKBYTAPE, books, error);
						}
						else {
							this.completeListener.onQueryComplete(GETBOOKBYTAPE, null, error);
						}
					}
				});
	}
	
	public void saveCache(Context context,String filecontent){
		FileOutputStream out = null;  
        try {  
            out = context.openFileOutput("booklist", Context.MODE_PRIVATE);  
            out.write(filecontent.getBytes("UTF-8")); 
            System.out.println("写入成功");
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        finally{  
            try {  
                out.close();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
	}
	
	/*
	 * @function   得到书籍详情
	 * @param      
	 * @return     
	 */
	public void getBooksByName(String bookname,OnQueryCompleteListener onQueryCompleteListener){
		List<BasicNameValuePair>parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("bookname", bookname));
		HttpUtils.makeAsyncPost(GETBOOKBYNAMEACTION, parms,
				new QueryCompleteHandler(onQueryCompleteListener, GETBOOKBYNAME) {
					Map<String, Object>books;
					@Override
					public void handleResponse(String jsonResult, EHttpError error) {
						// TODO Auto-generated method stub
						if(jsonResult!=null&&error == EHttpError.KErrorNone){
							Gson gson = new Gson();
							Type type = new TypeToken<Map<String, Object>>() {
							}.getType();
							books = gson.fromJson(jsonResult, type);
							List<Map<String, Object>>list = (List<Map<String, Object>>) books.get("books");
							System.out.println("书籍详情"+list);
							this.completeListener.onQueryComplete(GETBOOKBYNAME, list, error);
						}
						else {
							this.completeListener.onQueryComplete(GETBOOKBYNAME, null, error);
						}
					}
				});
	}
	
	/*
	 * @Function  搜索二手书
	 */
	public void searchBook(String bookname,String type,String page,OnQueryCompleteListener onQueryCompleteListener){
		List<BasicNameValuePair>parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("keyword", bookname));
		parms.add(new BasicNameValuePair("page", page));
		parms.add(new BasicNameValuePair("pagesize", "6"));
		parms.add(new BasicNameValuePair("type", type));
		HttpUtils.makeAsyncPost(SEARCHBOOKACTION, parms,
				new QueryCompleteHandler(onQueryCompleteListener, new QueryId()) {
					Map<String, Object>books;
					@Override
					public void handleResponse(String jsonResult, EHttpError error) {
						// TODO Auto-generated method stub
						if(jsonResult!=null&&error == EHttpError.KErrorNone){
							Gson gson = new Gson();
							Type type = new TypeToken<Map<String, Object>>() {
							}.getType();
							books = gson.fromJson(jsonResult, type);
							List<Map<String, Object>>list = (List<Map<String, Object>>) books.get("books");
							System.out.println("搜索到的书"+list);
							this.completeListener.onQueryComplete(new QueryId(), list, error);
						}
						else {
							this.completeListener.onQueryComplete(new QueryId(), null, error);
						}
					}
				});
	}
	
	/*
	 * 近似书名
	 */
	public void getSimilarBook(String bookname,OnQueryCompleteListener onQueryCompleteListener){
		List<BasicNameValuePair>parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("bookname", bookname));
		parms.add(new BasicNameValuePair("limit", 10+""));
		HttpUtils.makeAsyncPost(GETSIMILARBOOKNAME, parms, 
				new QueryCompleteHandler(onQueryCompleteListener, AUTOCOMPLETE) {
					
					@Override
					public void handleResponse(String jsonResult, EHttpError error) {
						// TODO Auto-generated method stub
						Map<String, Object>books;
						if(jsonResult!=null&&error == EHttpError.KErrorNone){
							Gson gson = new Gson();
							Type type = new TypeToken<Map<String, Object>>() {
							}.getType();
							books = gson.fromJson(jsonResult, type);
							List<String>list = (List<String>) books.get("booknames");
							System.out.println("搜索到的书"+list);
							this.completeListener.onQueryComplete(AUTOCOMPLETE, list, error);
						}
						else {
							this.completeListener.onQueryComplete(AUTOCOMPLETE, null, error);
						}
					}
				});
	}
	
	
	/*
	 * 最新
	 */
	public void clickListener(String bookid,OnQueryCompleteListener onQueryCompleteListener){
		List<BasicNameValuePair>parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("book_id", bookid));
		HttpUtils.makeAsyncPost(CLICKBOOKACTION, parms, 
				new QueryCompleteHandler(onQueryCompleteListener, CLICKWISH) {
					
					@Override
					public void handleResponse(String jsonResult, EHttpError error) {
						// TODO Auto-generated method stub
						if(jsonResult!=null&&error == EHttpError.KErrorNone){
							this.completeListener.onQueryComplete(CLICKWISH, jsonResult, error);
						}
						else {
							this.completeListener.onQueryComplete(CLICKWISH, jsonResult, error);
						}
					}
				});
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
