package com.yunjian.fragment;

import java.util.List;
import java.util.Map;

import com.umeng.analytics.MobclickAgent;
import com.yunjian.activity.BookDetailActivity;
import com.yunjian.activity.R;
import com.yunjian.adapter.SearchResultAdapter;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.BookService;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.view.LoadingDialog;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchResultFragment extends Fragment implements OnClickListener,OnQueryCompleteListener{
	private Button allbook,coursebook,english,japanese,technology,master,entertain;
	private ListView resultListView;
	private TextView emptyTextView;
	private SearchResultAdapter searchResultAdapter;
	private int type = 0;
	private List<Map<String, Object>> list;
	private String countent;
	private LoadingDialog loadingDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.search_result_fragment, null);
		loadingDialog = new LoadingDialog(getActivity());
		initView(view);
		resetService();
		return view;
	}
	
	public void initView(View view){
		resultListView = (ListView) view.findViewById(R.id.search_result_list);
		allbook = (Button)view.findViewById(R.id.all);
		coursebook = (Button)view.findViewById(R.id.coursebook);
		english = (Button)view.findViewById(R.id.english);
		japanese = (Button)view.findViewById(R.id.japanese);
		technology = (Button)view.findViewById(R.id.technology);
		master = (Button)view.findViewById(R.id.master);
		entertain = (Button)view.findViewById(R.id.entertain);
		emptyTextView = (TextView)view.findViewById(R.id.empty_txv);
		allbook.setOnClickListener(this);
		coursebook.setOnClickListener(this);
		english.setOnClickListener(this);
		japanese.setOnClickListener(this);
		technology.setOnClickListener(this);
		master.setOnClickListener(this);
		entertain.setOnClickListener(this);
		
		resultListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),BookDetailActivity.class);
            	intent.putExtra("bookname", list.get(arg2).get("bookname").toString());
                startActivity(intent);
			}
		});
	}
	
	public void resetService(){
		new BookService().searchBook(countent,type+"","1",this);
		loadingDialog.show();
	}
	
	public void setCountent(String countent){
		this.countent = countent;
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

	@Override
	public void onQueryComplete(QueryId queryId, Object result, EHttpError error) {
		// TODO Auto-generated method stub
		loadingDialog.dismiss();
		if (result != null) {
			list = (List<Map<String, Object>>) result;
			if(list.size() == 0){
				emptyTextView.setVisibility(View.VISIBLE);
				searchResultAdapter = new SearchResultAdapter(getActivity(), list);
				resultListView.setAdapter(searchResultAdapter);
			}
			else {
				emptyTextView.setVisibility(View.GONE);
				searchResultAdapter = new SearchResultAdapter(getActivity(), list);
				resultListView.setAdapter(searchResultAdapter);
			}
			} else {
				Toast.makeText(getActivity(), "服务器连接失败", 2000).show();
			}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		
		case R.id.all:
			type = 0;
			resetService();
			resetButtonColor();
			allbook.setTextColor(this.getResources().getColor(R.color.seagreen));
			allbook.setBackgroundResource(R.drawable.wish_type_bg);
			break;
		case R.id.coursebook:
			type = 1;
			resetService();
			resetButtonColor();
			coursebook.setTextColor(this.getResources().getColor(R.color.seagreen));
			coursebook.setBackgroundResource(R.drawable.wish_type_bg);
			break;
		case R.id.english:
			type = 2;
			resetService();
			resetButtonColor();
			english.setTextColor(this.getResources().getColor(R.color.seagreen));
			english.setBackgroundResource(R.drawable.wish_type_bg);
			break;
		case R.id.japanese:
			type = 3;
			resetService();
			resetButtonColor();
			japanese.setTextColor(this.getResources().getColor(R.color.seagreen));
			japanese.setBackgroundResource(R.drawable.wish_type_bg);
			break;
		case R.id.technology:
			type = 4;
			resetService();
			resetButtonColor();
			technology.setTextColor(this.getResources().getColor(R.color.seagreen));
			technology.setBackgroundResource(R.drawable.wish_type_bg);
			break;
		case R.id.master:
			type = 5;
			resetService();
			resetButtonColor();
			master.setTextColor(this.getResources().getColor(R.color.seagreen));
			master.setBackgroundResource(R.drawable.wish_type_bg);
			break;
		case R.id.entertain:
			type = 6;
			resetService();
			resetButtonColor();
			entertain.setTextColor(this.getResources().getColor(R.color.seagreen));
			entertain.setBackgroundResource(R.drawable.wish_type_bg);
			break;

		default:
			break;
		}
	}
	

}
