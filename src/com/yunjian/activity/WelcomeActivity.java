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
      
       //����ͼƬ��Դ
       private static final int[] pics = { R.drawable.welcome1,
               R.drawable.welcome2, R.drawable.welcome3};
      
//       //�ײ�С��ͼƬ
//       private ImageView[] dots ;
      
       //��¼��ǰѡ��λ��
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
    					Toast.makeText(WelcomeActivity.this, "���ڽ���Ӧ��...", Toast.LENGTH_SHORT).show();
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
              
               //��ʼ������ͼƬ�б�
               for(int i=0; i<pics.length; i++) {
                   ImageView iv = new ImageView(this);
                   iv.setLayoutParams(mParams);
                   iv.setImageResource(pics[i]);
                   views.add(iv);
               }
               vp = (ViewPager) findViewById(R.id.viewpager);
               //��ʼ��Adapter
               vpAdapter = new ViewPageAdapter(views);
               vp.setAdapter(vpAdapter);
               //�󶨻ص�
               vp.setOnPageChangeListener(this);
	 		}
			else{
	 			Intent intent = new Intent(WelcomeActivity.this, LoadingActivity.class);
				startActivity(intent);
				finish();
	 		}
          
           //��ʼ���ײ�С��
//           initDots();
          
       }
      
//       private void initDots() {
//           LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
//         
//
//           dots = new ImageView[pics.length];
//
//           //ѭ��ȡ��С��ͼƬ
//           for (int i = 0; i < pics.length; i++) {
//           //�õ�һ��LinearLayout�����ÿһ����Ԫ��
//               dots[i] = (ImageView) ll.getChildAt(i);
//               dots[i].setEnabled(true);//����Ϊ��ɫ
//               dots[i].setOnClickListener(this);
//               dots[i].setTag(i);//����λ��tag������ȡ���뵱ǰλ�ö�Ӧ
//           }
//
//           currentIndex = 0;
//           dots[currentIndex].setEnabled(false);//����Ϊ��ɫ����ѡ��״̬
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

       //������״̬�ı�ʱ����
       @Override
       public void onPageScrollStateChanged(int arg0) {
           // TODO Auto-generated method stub
          
       }

       //����ǰҳ�汻����ʱ����
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

//       //���µ�ҳ�汻ѡ��ʱ����
       @Override
       public void onPageSelected(int arg0) {
           //���õײ�С��ѡ��״̬
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