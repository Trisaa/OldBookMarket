package com.yunjian.activity;

import java.util.ArrayList;
import java.util.List;

import com.lei.activity.CaptureActivity;
import com.umeng.analytics.MobclickAgent;
import com.yunjian.adapter.ViewPageAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class WelcomeActivity extends Activity implements OnClickListener, OnPageChangeListener{
       private ViewPager vp;
       private ViewPageAdapter vpAdapter;
       private List<View> views;
       private View btCome;
       private SharedPreferences sharedPreferences;
      
       //引导图片资源
       private static final int[] pics = { R.drawable.welcome1,
               R.drawable.welcome2, R.drawable.welcome3};
      
//       //底部小店图片
//       private ImageView[] dots ;
      
       //记录当前选中位置
       private int currentIndex;
      
      
       @Override
       public void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
           setContentView(R.layout.welcome);
           views = new ArrayList<View>();
           sharedPreferences = getSharedPreferences("first", Activity.MODE_PRIVATE);
           if(sharedPreferences.getString("first_load", null)==null){
               btCome = (View)findViewById(R.id.bt_come);
               btCome.setClickable(true);
               btCome.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub
    				if(currentIndex==2){
    					Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
    					Toast.makeText(WelcomeActivity.this, "正在进入应用...", Toast.LENGTH_SHORT).show();
    					startActivity(intent);
    					Editor editor = sharedPreferences.edit();
    					editor.putString("first_load", "true");
    					editor.commit();
    					finish();
    				}
    				
    			}
    		});
            LinearLayout.LayoutParams mParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                       LinearLayout.LayoutParams.WRAP_CONTENT);
              
               //初始化引导图片列表
               for(int i=0; i<pics.length; i++) {
                   ImageView iv = new ImageView(this);
                   iv.setLayoutParams(mParams);
                   iv.setImageResource(pics[i]);
                   views.add(iv);
               }
               vp = (ViewPager) findViewById(R.id.viewpager);
               //初始化Adapter
               vpAdapter = new ViewPageAdapter(views);
               vp.setAdapter(vpAdapter);
               //绑定回调
               vp.setOnPageChangeListener(this);
	 		}
			else{
	 			Intent intent = new Intent(WelcomeActivity.this, LoadingActivity.class);
				startActivity(intent);
				finish();
	 		}
          
           //初始化底部小点
//           initDots();
          
       }
      
//       private void initDots() {
//           LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
//         
//
//           dots = new ImageView[pics.length];
//
//           //循环取得小点图片
//           for (int i = 0; i < pics.length; i++) {
//           //得到一个LinearLayout下面的每一个子元素
//               dots[i] = (ImageView) ll.getChildAt(i);
//               dots[i].setEnabled(true);//都设为灰色
//               dots[i].setOnClickListener(this);
//               dots[i].setTag(i);//设置位置tag，方便取出与当前位置对应
//           }
//
//           currentIndex = 0;
//           dots[currentIndex].setEnabled(false);//设置为白色，即选中状态
//       }
      
      
       private void setCurView(int position)
       {
           if (position < 0 || position >= pics.length) {

				
               return;
           }

           vp.setCurrentItem(position);
       }

      
//       private void setCurDot(int positon)
//       {
//           if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
//               return;
//           }
//
//           dots[positon].setEnabled(false);
//           dots[currentIndex].setEnabled(true);
//
//           currentIndex = positon;
//       }

       //当滑动状态改变时调用
       @Override
       public void onPageScrollStateChanged(int arg0) {
           // TODO Auto-generated method stub
          
       }

       //当当前页面被滑动时调用
       @Override
       public void onPageScrolled(int arg0, float arg1, int arg2) {
           // TODO Auto-generated method stub
    	   currentIndex = arg0;
    	   if(arg0==2){
    		   Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
    		   Editor editor = sharedPreferences.edit();
				editor.putString("first_load", "true");
				editor.commit();
				finish(); 
				startActivity(intent);
    	   }
       }

//       //当新的页面被选中时调用
       @Override
       public void onPageSelected(int arg0) {
           //设置底部小点选中状态
//           setCurDot(arg0);
       }

       @Override
       public void onClick(View v) {
           int position = (Integer)v.getTag();
           setCurView(position);
//           setCurDot(position);
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