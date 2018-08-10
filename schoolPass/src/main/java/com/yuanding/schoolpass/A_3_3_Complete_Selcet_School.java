package com.yuanding.schoolpass;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_School_List;
import com.yuanding.schoolpass.bean.Item_Cate;
import com.yuanding.schoolpass.service.Api.InterSchoolList;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年12月3日 上午9:40:11 
 * 个人资料修改学校
 */
public class A_3_3_Complete_Selcet_School extends A_0_CpkBaseTitle_Navi {

	private View liner_whole_view_school,select_school_load_error,select_school_acy_loading;
	private PullToRefreshListView mPullDownView;
	private MyAdapter adapter;
	private int have_read_page = 1;// 已经读的页数
	private List<Item_Cate> mSchoolList = null;
	private EditText mSearchInput;
	private TextView tx_btnserach;
	private String user_phone;
	
	/**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
	
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_mess_select_school);

		setTitleText(getIntent().getExtras().getString("title_name"));
		user_phone = getIntent().getExtras().getString("user_phone");
		
//		mLinerReadDataError.setOnClickListener(onClick);
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		liner_whole_view_school = findViewById(R.id.liner_whole_view);
		select_school_load_error = findViewById(R.id.select_school_load_error);
		select_school_acy_loading = findViewById(R.id.select_school_acy_loading);
		
		home_load_loading = (LinearLayout) select_school_acy_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		tx_btnserach = (TextView)findViewById(R.id.tx_btnserach);
		mSearchInput = (EditText) findViewById(R.id.CustomEditText_select_school);
		mPullDownView = (PullToRefreshListView) findViewById(R.id.lv_mess_select_school);
		mSchoolList = new ArrayList<Item_Cate>();
		adapter = new MyAdapter();
        mPullDownView.setMode(Mode.BOTH);
        mPullDownView.setAdapter(adapter);

		mPullDownView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(getApplicationContext(),System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME| DateUtils.FORMAT_SHOW_DATE| DateUtils.FORMAT_ABBREV_ALL);
						refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
						have_read_page = 1;
						startReadData(mSearchInput.getText().toString(),have_read_page,true);
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {						
						
						if (repfresh==0) {
							repfresh=1;
							demo_swiperefreshlayout.setEnabled(false);
							demo_swiperefreshlayout.setRefreshing(false);  
							inItMoreData(mSearchInput.getText().toString(),have_read_page);
						}
					}
				});
		
		/**
		 * 新增下拉使用 new add
		 */
		demo_swiperefreshlayout.setSize(SwipeRefreshLayout.DEFAULT);
		demo_swiperefreshlayout.setColorSchemeResources(R.color.main_color);
		if (repfresh == 0) {
			repfresh = 1;
			mPullDownView.onRefreshComplete();
			demo_swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
						public void onRefresh() {
							have_read_page = 1;
							if(null!=mPullDownView){
								mPullDownView.setMode(Mode.DISABLED);
							}
							
							startReadData(mSearchInput.getText().toString(),have_read_page,true);

						};
					});
		}
		
		
		mPullDownView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				 if (demo_swiperefreshlayout!=null&&mPullDownView.getChildCount() > 0 && mPullDownView.getRefreshableView().getFirstVisiblePosition() == 0
			                && mPullDownView.getChildAt(0).getTop() >= mPullDownView.getPaddingTop()) {
			            //解决滑动冲突，当滑动到第一个item，下拉刷新才起作用
					   demo_swiperefreshlayout.setEnabled(true);
			        } else {
			        	demo_swiperefreshlayout.setEnabled(false);
			        }
				
			}
			
			@Override
			public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				
			}
		});
		

		// 点击Item触发的事件
		mPullDownView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int posi,long arg3) {
				if (posi == 0) {
					return;
				}
				Intent intent = new Intent();
				intent.putExtra("school_id", mSchoolList.get(posi - 1).getSchool_id());
				intent.putExtra("modify_content", mSchoolList.get(posi - 1).getSchool_name());
				setResult(2, intent);
				finish();
				
			}
		});
		mSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {

					if (mSearchInput.getText()
							.toString().length() > 0) {
						have_read_page = 1;
						startReadData(mSearchInput.getText().toString(),have_read_page,false);
					}

					return true;

				}

				return false;
			}

		});
		
		mSearchInput.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

				if (mSearchInput.getText().toString().length() > 0) {
					have_read_page = 1;
					startReadData(mSearchInput.getText().toString(),have_read_page,false);
				}else{
					have_read_page = 1;
					startReadData(mSearchInput.getText().toString(),have_read_page,true);
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
        tx_btnserach.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (mSearchInput.getText()
						.toString().length() > 0) {
					have_read_page = 1;
					startReadData(mSearchInput.getText().toString(),have_read_page,false);
				}
				
			}
		});
		
		select_school_load_error.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showLoadResult(true,false, false);
				have_read_page = 1;
				startReadData(mSearchInput.getText().toString(),have_read_page,true);
			}
		});
		startReadData(mSearchInput.getText().toString(),have_read_page,true);
		
	}


	// 数据加载，及网络错误提示
//	OnClickListener onClick = new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.mess_select_school_load_error:
//				showLoadResult(false, false);
//				have_read_page = 1;
//				startReadData(mSearchInput.getText().toString(),have_read_page,true);
//				break;
//			default:
//				break;
//			}
//		}
//	};
        private void showLoadResult(boolean loading,boolean wholeView,boolean loadFaile) {
		
		if (wholeView)
			liner_whole_view_school.setVisibility(View.VISIBLE);
		else
			liner_whole_view_school.setVisibility(View.GONE);
		
		if (loadFaile)
			select_school_load_error.setVisibility(View.VISIBLE);
		else
			select_school_load_error.setVisibility(View.GONE);
		
		if(loading){
			drawable.start();
			select_school_acy_loading.setVisibility(View.VISIBLE);
		}else{ 
			if (drawable!=null) {
        		drawable.stop();
			}
			select_school_acy_loading.setVisibility(View.GONE);
	}}

	// 读取学校
	private void startReadData(String keyword,int page_no,final boolean pullRefresh) {
		mSearchInput.requestFocus();
		A_0_App.getApi().getSchoolList(A_3_3_Complete_Selcet_School.this, keyword,String.valueOf(page_no),user_phone,new InterSchoolList() {
			
			@Override
			public void onSuccess(String message,Cpk_School_List school) {
				if (isFinishing())
					return;
				clearBusinessList();
				have_read_page = 2;
				mSchoolList = school.getmList();
				adapter.notifyDataSetChanged();
				showLoadResult(false,true, false);
				if (mSchoolList == null || mSchoolList.size() <= 0) {
					PubMehods.showToastStr(A_3_3_Complete_Selcet_School.this, message);
				}

				demo_swiperefreshlayout.setRefreshing(false);  
				if(null!=mPullDownView){
					mPullDownView.onRefreshComplete();
					mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
				}
				repfresh=0;
				
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
                PubMehods.showToastStr(A_3_3_Complete_Selcet_School.this, msg);
                showLoadResult(false,false, true);
                
                demo_swiperefreshlayout.setRefreshing(false);  
                
                if(null!=mPullDownView){
                    mPullDownView.onRefreshComplete();
                    mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
                }
                repfresh=0;
                
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
	}
	
	private void inItMoreData(String keyword,int page_no) {
		A_0_App.getApi().getSchoolList(A_3_3_Complete_Selcet_School.this, keyword,String.valueOf(page_no),user_phone,new InterSchoolList() {
			
			@Override
			public void onSuccess(String message,Cpk_School_List school) {
				if (isFinishing())
					return;
				have_read_page += 1;
				int totleSize = mSchoolList.size();
				for (int i = 0; i < school.getmList().size(); i++) {
					mSchoolList.add(school.getmList().get(i));
				}
				adapter.notifyDataSetChanged();
				if (school.getmList() == null || school.getmList().size() <= 0)
					PubMehods.showToastStr(A_3_3_Complete_Selcet_School.this,"已是全部学校");
					
				if(null!=mPullDownView){
					mPullDownView.onRefreshComplete();
				}
				repfresh=0;
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
                PubMehods.showToastStr(A_3_3_Complete_Selcet_School.this, msg);
                if(null!=mPullDownView){
                    mPullDownView.onRefreshComplete();
                }
                repfresh=0;
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
	}

	// 加载列表数据
	public class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if(mSchoolList != null)
			   return mSchoolList.size();
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
				converView = LayoutInflater.from(A_3_3_Complete_Selcet_School.this).inflate(R.layout.item_pub_text, null);
			}
			TextView tv_acy_name = (TextView) converView.findViewById(R.id.tv_item_pub_text);
			tv_acy_name.setText(mSchoolList.get(posi).getSchool_name());
			if(A_0_App.isShowAnimation==true){
			if(posi>A_0_App.selschool_curPosi)
			 {
				A_0_App.selschool_curPosi=posi;
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
		if (mSchoolList != null && mSchoolList.size() > 0) {
			mSchoolList.clear();
		}
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
    protected void onResume() {
        if (mSearchInput != null) {
            mSearchInput.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mSearchInput.getWindowToken(), 0);
        }
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	drawable.stop();
    	drawable=null;
    	super.onDestroy();
    }
}
