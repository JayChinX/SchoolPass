package com.yuanding.schoolpass;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Teach_Class;
import com.yuanding.schoolpass.service.Api.InterTeachOrganList;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.PubMehods;


/**
 * @author Jiaohaili 
 * @version 创建时间：2015年12月3日 上午9:40:29 
 * 个人资料修改班级
 */
public class A_3_3_Complete_Selcet_Class extends A_0_CpkBaseTitle_Navi {
	private View liner_whole_select_class,select_class_load_error,select_class_acy_loading;
	private ListView mPullDownView;
	private MyClassAdapter adapter;
	private List<Cpk_Teach_Class> mClassList,mClassSecondList = null;
	private String school_id,class_detail_name ="";
    private boolean firsetQuest = true;
	
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_mess_select_class);
		setTitleText(getIntent().getExtras().getString("title_name"));
		school_id = getIntent().getExtras().getString("schoolId");
		liner_whole_select_class = findViewById(R.id.liner_whole_select_class);
		select_class_load_error = findViewById(R.id.select_class_load_error);
		select_class_acy_loading = findViewById(R.id.select_class_acy_loading);
		
		home_load_loading = (LinearLayout) select_class_acy_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		mPullDownView = (ListView) findViewById(R.id.lv_mess_select_class);
		mClassList = new ArrayList<Cpk_Teach_Class>();
		mClassSecondList = new ArrayList<Cpk_Teach_Class>();
		adapter = new MyClassAdapter();
		mPullDownView.setAdapter(adapter);

		// 点击Item触发的事件
		mPullDownView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int posi,long arg3) {
				if (Integer.parseInt(mClassList.get(posi).getChild_total_num()) == 0) {
					mClassSecondList = mClassList;
					class_detail_name = mClassSecondList.get(posi).getOrgan_name();
					firsetQuest = false;
					startReadData(mClassList.get(posi).getOrgan_id(), school_id, firsetQuest);
				} else {
					class_detail_name += mClassList.get(posi).getOrgan_name();
					Intent intent = new Intent();
					intent.putExtra("class_id", mClassList.get(posi).getOrgan_id());
					intent.putExtra("modify_content",class_detail_name);
					setResult(3, intent);
					finish();
				}
			}
		});
		
		select_class_load_error.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showLoadResult(true, false, false);
				firsetQuest = true;
				startReadData(null, school_id, firsetQuest);
			}
		});

		startReadData(null, school_id, firsetQuest);
	}

	// 读取教学机构
	private void startReadData(String organ_id, String school_id,boolean firsetQuest) {
		A_0_App.getApi().getTeachOrgan(A_3_3_Complete_Selcet_Class.this,
				organ_id, school_id,firsetQuest, new InterTeachOrganList() {
					@Override
					public void onSuccess(List<Cpk_Teach_Class> mlist,String message) {
						if (isFinishing())
							return;
						if (mlist != null && mlist.size() > 0) {
							clearBusinessList();
							mClassList = mlist;
							adapter.notifyDataSetChanged();
						}else{
						    PubMehods.showToastStr(A_3_3_Complete_Selcet_Class.this, message);
						}
						showLoadResult(false, true, false);
					}
				},new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        if (isFinishing())
                            return;
                        PubMehods.showToastStr(A_3_3_Complete_Selcet_Class.this, msg);
                        showLoadResult(false, false, true);
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
	}
	
	private void showLoadResult(boolean loading,boolean wholeView,boolean loadFaile) {
		
		if (wholeView)
			liner_whole_select_class.setVisibility(View.VISIBLE);
		else
			liner_whole_select_class.setVisibility(View.GONE);
		
		if (loadFaile)
			select_class_load_error.setVisibility(View.VISIBLE);
		else
			select_class_load_error.setVisibility(View.GONE);
		
		if(loading){
			drawable.start();
			select_class_acy_loading.setVisibility(View.VISIBLE);
		}else{ 
			if (drawable!=null) {
        		drawable.stop();
			}
			select_class_acy_loading.setVisibility(View.GONE);
	}}

	 // 加载列表数据
	public class MyClassAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mClassList != null)
				return mClassList.size();
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int posi, View converView, ViewGroup arg2) {
			if (converView == null) {
				converView = LayoutInflater.from(A_3_3_Complete_Selcet_Class.this).inflate(R.layout.item_pub_text, null);
			}
			TextView tv_acy_name = (TextView) converView.findViewById(R.id.tv_item_pub_text);
			tv_acy_name.setText(mClassList.get(posi).getOrgan_name());
			if(A_0_App.isShowAnimation==true){
			 if(posi>A_0_App.selclass_curPosi)
			 {
				A_0_App.selclass_curPosi=posi;
				Animation an=new TranslateAnimation(Animation.RELATIVE_TO_SELF,1, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				an.setDuration(400);
				an.setStartOffset(50*posi);
			    converView.startAnimation(an);
			 }
			}
			return converView;
		}
		
	}
	
	private void clearBusinessList() {
		if (mClassList != null && mClassList.size() > 0) {
			mClassList.clear();
		}
	}
	
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    if (!firsetQuest) {
                        mClassSecondList.clear();
                        mClassList.clear();
                        class_detail_name ="";
                        firsetQuest = true;
                        startReadData(null, school_id, firsetQuest);
                    }else{
                        finish();
                    }
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    clearBusinessList();
        mClassList = null;
        mClassSecondList.clear();
        mClassSecondList = null;
        adapter = null;
	    school_id="";
	    class_detail_name ="";
	    drawable.stop();
	    drawable=null;
	}

}
