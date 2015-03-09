package com.yunjian.activity;

import java.util.List;

import com.umeng.analytics.MobclickAgent;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.fragment.SearchResultFragment;
import com.yunjian.service.BookService;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

public class SearchActivity extends Activity implements OnClickListener,OnQueryCompleteListener{
	private AutoCompleteTextView autoCompleteTextView;
	private Button searchButton;
	private ListView listView;
	
	private List<String>booksList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initView();
	}
	
	public void initView(){
		autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.search_et);
		listView = (ListView) findViewById(R.id.search_list);
		searchButton = (Button) findViewById(R.id.search_btn);
		
		autoCompleteTextView.addTextChangedListener(textWatcher);
		searchButton.setOnClickListener(this);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				listView.setVisibility(View.GONE);
				FragmentManager fManager = getFragmentManager();
				Fragment searchresultFragment =  new SearchResultFragment();
				((SearchResultFragment) searchresultFragment).setCountent(booksList.get(arg2));
				fManager.beginTransaction().replace(R.id.search_result_ll,searchresultFragment).commit();
			}
		});
		
		new BookService().getSimilarBook(" È", SearchActivity.this);
	}
	
	private TextWatcher textWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			String content = autoCompleteTextView.getText().toString();
			try {
				new BookService().getSimilarBook(content, SearchActivity.this);
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			
		}
	};

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.search_btn:
			listView.setVisibility(View.GONE);
			FragmentManager fManager = getFragmentManager();
			Fragment searchresultFragment =  new SearchResultFragment();
			((SearchResultFragment) searchresultFragment).setCountent(autoCompleteTextView.getText().toString());
			fManager.beginTransaction().replace(R.id.search_result_ll,searchresultFragment).commit();
			break;

		default:
			break;
		}
	}

	@Override
	public void onQueryComplete(QueryId queryId, Object result, EHttpError error) {
		// TODO Auto-generated method stub
		if(queryId.equals(BookService.AUTOCOMPLETE)){
			if(result != null){
				booksList = (List<String>) result;
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, booksList); 
				listView.setAdapter(adapter);
				listView.setVisibility(View.VISIBLE);
			}
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
