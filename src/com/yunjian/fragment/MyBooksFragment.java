package com.yunjian.fragment;

import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yunjian.activity.BookDetailActivity;
import com.yunjian.activity.R;
import com.yunjian.adapter.DealedGoodsAdapter;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserCenterService;
import com.yunjian.util.Utils;
import com.yunjian.view.LoadingDialog;

public class MyBooksFragment extends Fragment{
	public boolean isCommitAgain = false;
	private View view;
	private ListView dealedGoods;
	private ImageView erroImageView;
	private LoadingDialog loadingDialog;
	private List<Map<String, Object>>list;
	private DealedGoodsAdapter adapter;
	private OnQueryCompleteListener onQueryCompleteListener;
	private UserCenterService service;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.goodslist, null);
		initView(view);
		return view;
	}
	
	public boolean getIsCommitAgain(){
    	return isCommitAgain;
    }
	
	public void initView(View view){
		dealedGoods = (ListView)view.findViewById(R.id.goodslist);
		loadingDialog = new LoadingDialog(getActivity());
		erroImageView = (ImageView)view.findViewById(R.id.erro_img);
		
		resetService();
		dealedGoods.setOnItemClickListener(new OnItemClickListener() {

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
		service = new UserCenterService();
		
		onQueryCompleteListener = new OnQueryCompleteListener() {
			
			@Override
			public void onQueryComplete(QueryId queryId, Object result, EHttpError error) {
				// TODO Auto-generated method stub
				loadingDialog.dismiss();
				if(result==null){
					Toast.makeText(getActivity(), "ÍøÂçÁ¬½Ó³¬Ê±", 2000).show();
				}
				else {
					list = (List<Map<String, Object>>) result;
					if(list.size()==0){
						erroImageView.setVisibility(View.VISIBLE);
					}
					else {
						try {
							adapter = new DealedGoodsAdapter(getActivity(), list,MyBooksFragment.this);
						} catch (Exception e) {
							// TODO: handle exception
						}
						dealedGoods.setAdapter(adapter);
					}
				}
			}
		};
		service.getBooksByUser(Utils.user_id, onQueryCompleteListener);
		loadingDialog.show();
	}
	

}
