package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Side_Course;
import com.yuanding.schoolpass.bean.Cpk_Side_Score_List;
import com.yuanding.schoolpass.service.Api.InterScoreList;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.MyListView;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月12日 下午3:18:18
 * 成绩————成绩————成绩
 */
public class B_Side_Achievement_Acy extends A_0_CpkBaseTitle_Navi{
	
	
	private View mLinerReadDataError,mLinerNoContent,liner_lecture_list_whole_view,side_lecture__loading;
	private PullToRefreshListView mPullDownView;
	private List<Cpk_Side_Score_List> score_Lists;
	private Mydapter adapter;
	private int have_read_page = 1;// 已经读的页数
	private Boolean firstLoad = false;
	private Mydapter_Itme adapter_itme;
	private ACache maACache;
	private JSONObject jsonObject;
	
	 /**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
	
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private boolean havaSuccessLoadData = false;
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		A_0_App.getInstance().addActivity(this);
		setView(R.layout.activity_side_achievement);
		setTitleText("成绩");
		firstLoad = true;
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		liner_lecture_list_whole_view = findViewById(R.id.liner_achievement_whole_view);
		mPullDownView = (PullToRefreshListView)findViewById(R.id.lv_side_achievement_list);
		side_lecture__loading=findViewById(R.id.side_achievement_loading);
		mLinerReadDataError = findViewById(R.id.side_achievement_load_error);
		mLinerNoContent = findViewById(R.id.side_achievement_no_content);
		
		home_load_loading = (LinearLayout) side_lecture__loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		ImageView iv_blank_por = (ImageView)mLinerNoContent.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView)mLinerNoContent.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.no_cj);
		tv_blank_name.setText("你的考试成绩暂未发布，再等一等~");
		mLinerReadDataError.setOnClickListener(onClick);
		score_Lists = new ArrayList<Cpk_Side_Score_List>();
		adapter = new Mydapter();
        //mPullDownView.setMode(Mode.BOTH);
        mPullDownView.setAdapter(adapter);
        mPullDownView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新
                String label = DateUtils.formatDateTime(getApplicationContext(),
                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				//clearBusinessList();
				have_read_page = 1;
				getLectureList(have_read_page,true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            	//getMoreLecture(have_read_page);
            	if (repfresh==0) {
					repfresh=1;
					demo_swiperefreshlayout.setEnabled(false);
					demo_swiperefreshlayout.setRefreshing(false);  
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
			if(null!=mPullDownView){
            	mPullDownView.onRefreshComplete();
            }
			demo_swiperefreshlayout
					.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
						public void onRefresh() {

							have_read_page = 1;
							if(null!=mPullDownView){
								mPullDownView.setMode(Mode.DISABLED);
			                }
							
							getLectureList(have_read_page,true);

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
      
      //**************************新增到这**********************
        
        
        
		mPullDownView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int posi,long arg3) {
				if (posi == 0) {
					return;
				}
				/*Intent intent = new Intent(B_Side_Wages_Acy.this,B_Side_Repair_Detail.class);
				intent.putExtra("lecture_id", score_Lists.get(posi-1).getArticle_id());
				startActivity(intent);*/
			}
		});
		if (A_0_App.USER_STATUS.equals("5") || A_0_App.USER_STATUS.equals("0")) {
			showLoadResult(false, false, false, true);
		} else {
			readCache();
		}
		//getLectureList(have_read_page, false);
		
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}
	
	private void readCache() {
		maACache = ACache.get(this);
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_score+A_0_App.USER_UNIQID);
		
        if (jsonObject!= null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
        	showInfo(jsonObject);
		}else{
		    updateInfo();
		}
	}
	private void showInfo(JSONObject jsonObject) {
		
			int state = jsonObject.optInt("status");
			List<Cpk_Side_Score_List> mlistContact= new ArrayList<Cpk_Side_Score_List>();
			if (state == 1) {
				mlistContact=JSON.parseArray(jsonObject.optJSONArray("alist")+"", Cpk_Side_Score_List.class);
			}
			if (isFinishing())
				return;
			havaSuccessLoadData = true;   
			if (mlistContact != null && mlistContact.size() > 0) {
				clearBusinessList();
				score_Lists = mlistContact;
				adapter.notifyDataSetChanged();
				showLoadResult(false, true, false, false);
			} else {
				showLoadResult(false, false, false, true);
			}
			demo_swiperefreshlayout.setRefreshing(false);  
            if(null!=mPullDownView){
                mPullDownView.onRefreshComplete();
                mPullDownView.setMode(Mode.DISABLED);
            }
            repfresh=0;
		
	}
	private void updateInfo(){
		MyAsyncTask updateLectureInfo = new MyAsyncTask(this);
		updateLectureInfo.execute();
    }

	class MyAsyncTask extends AsyncTask<Void,Integer,Integer>{
        private Context context;
        MyAsyncTask(Context context) {
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
        }
        /**
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
         */
        @Override
        protected Integer doInBackground(Void... params) {
        	getLectureList(have_read_page, false);
            return null;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {
//            logD("上传融云数据完毕");
        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
        	
        }
    }
    private void getLectureList(int page_no,final boolean pullRefresh) {
		A_0_App.getApi().getScoreList(B_Side_Achievement_Acy.this, A_0_App.USER_TOKEN,String.valueOf(page_no), new InterScoreList() {
			@Override
			public void onSuccess(List<Cpk_Side_Score_List> mList) {
				if (isFinishing())
					return;
				havaSuccessLoadData = true;   
				if (mList != null && mList.size() > 0) {
					have_read_page = 2;
					clearBusinessList();
					score_Lists = mList;
					adapter.notifyDataSetChanged();
					showLoadResult(false,true, false, false);
					if(pullRefresh)
						PubMehods.showToastStr(B_Side_Achievement_Acy.this, "刷新成功");
				} else {
					showLoadResult(false,false, false, true);
				}
				demo_swiperefreshlayout.setRefreshing(false);  
				if(null!=mPullDownView){
                	mPullDownView.onRefreshComplete();
                	mPullDownView.setMode(Mode.DISABLED);
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
                if(!havaSuccessLoadData)
                {
                showLoadResult(false,false, true, false);
                }
                PubMehods.showToastStr(B_Side_Achievement_Acy.this, msg);
                demo_swiperefreshlayout.setRefreshing(false);  
                if(null!=mPullDownView){
                    mPullDownView.onRefreshComplete();
                    mPullDownView.setMode(Mode.DISABLED);
                }
                repfresh=0;
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
	}
	
	// 上拉刷新初始化数据
	private void getMoreLecture(int page_no) {
		if(A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
			return;
		A_0_App.getInstance().showProgreDialog(B_Side_Achievement_Acy.this, "",true);
		A_0_App.getApi().getScoreList(B_Side_Achievement_Acy.this, A_0_App.USER_TOKEN,String.valueOf(page_no), new InterScoreList() {
			@Override
			public void onSuccess(List<Cpk_Side_Score_List> mList) {
				if (isFinishing())
					return;
				A_0_App.getInstance().CancelProgreDialog(B_Side_Achievement_Acy.this);
				if (mList != null && mList.size() > 0) {
					have_read_page += 1;
					int totleSize = score_Lists.size();
					for (int i = 0; i < mList.size(); i++) {
						score_Lists.add(mList.get(i));
					}
					adapter.notifyDataSetChanged();
					mPullDownView.getRefreshableView().setSelection(totleSize + 1);
				} else {
					PubMehods.showToastStr(B_Side_Achievement_Acy.this,"没有更多了");
				}
				if(null!=mPullDownView){
                	mPullDownView.onRefreshComplete();                	
                }
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
                PubMehods.showToastStr(B_Side_Achievement_Acy.this, msg);
                if(null!=mPullDownView){
                    mPullDownView.onRefreshComplete();                  
                }
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
		
	}
	
	private void clearBusinessList() {
		if (score_Lists != null && score_Lists.size() > 0) {
			score_Lists.clear();
		}
	}
	
	private void showLoadResult(boolean loading,boolean wholeView,boolean loadFaile,boolean noData) {
		if (wholeView)
			liner_lecture_list_whole_view.setVisibility(View.VISIBLE);
		else
			liner_lecture_list_whole_view.setVisibility(View.GONE);
		if (loadFaile)
			mLinerReadDataError.setVisibility(View.VISIBLE);
		else
			mLinerReadDataError.setVisibility(View.GONE);
		
		if (noData)
			mLinerNoContent.setVisibility(View.VISIBLE);
		else
			mLinerNoContent.setVisibility(View.GONE);
		if(loading){
			drawable.start();
			side_lecture__loading.setVisibility(View.VISIBLE);
		}else{
			if (drawable!=null) {
        		drawable.stop();
			}
			side_lecture__loading.setVisibility(View.GONE);
	}}
	
	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.side_achievement_load_error:
				showLoadResult(true,false, false, false);
				clearBusinessList();
				have_read_page = 1;
				getLectureList(have_read_page,true);
				break;
			default:
				break;
			}
		}
	};
	
	public class Mydapter extends BaseAdapter{
		@Override
		public int getCount() {
            if (score_Lists != null)
                return score_Lists.size();
            return 0;
		}

		@Override
		public Object getItem(int v) {
			return v;
		}
		@Override
		public long getItemId(int v) {
			return v;
		}

		@Override
		public View getView(int posi, View converView, ViewGroup arg2) {
			ViewHolder holder;
			if (converView == null) {
				holder = new ViewHolder();
				converView = LayoutInflater.from(B_Side_Achievement_Acy.this).inflate(R.layout.item_wages_list, null);
				holder.tv_name = (TextView) converView.findViewById(R.id.tv_name);
				holder.tv_title = (TextView) converView.findViewById(R.id.tv_title);
				holder.myListView = (MyListView) converView.findViewById(R.id.lv_mylist);
				holder.tv_time = (TextView) converView.findViewById(R.id.tv_time);
				holder.tv_from_person = (TextView) converView.findViewById(R.id.tv_from_person);
			    converView.setTag(holder);
			} else {
				holder = (ViewHolder) converView.getTag();
			}
			List<Cpk_Side_Course> list=JSON.parseArray(score_Lists.get(posi).getLists(), Cpk_Side_Course.class);
			float score=0;
			Cpk_Side_Course course=new Cpk_Side_Course();
			for (int i = 0; i < list.size(); i++) {
				score=Float.parseFloat(list.get(i).getScore())+score;
			}
			course.setCourse("合计");
			course.setScore(score+"");
			list.add(course);
			adapter_itme = new Mydapter_Itme(list);
			holder.myListView.setAdapter(adapter_itme);
			
			holder.tv_from_person.setText(score_Lists.get(posi).getTeacher_name());
			holder.tv_title.setText(score_Lists.get(posi).getTitle());
			holder.tv_time.setText(PubMehods.getFormatDate(score_Lists.get(posi).getTime(), "yyyy/MM/dd  HH:mm"));
			if(A_0_App.isShowAnimation==true){
			if(posi>A_0_App.side_achievement_Acy_curPosi)
			 {
				A_0_App.side_achievement_Acy_curPosi=posi;
				Animation an=new TranslateAnimation(Animation.RELATIVE_TO_SELF,1, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				an.setDuration(400);
				an.setStartOffset(20*posi);
			    converView.startAnimation(an);
			 }
			}
			return converView;
		}
		
	}
	
	class ViewHolder{
		TextView tv_name;
		TextView tv_title;
		TextView tv_time;
		TextView tv_from_person;
		MyListView myListView;
	}
	
	class ViewHolder2{
		TextView tv_class;
		TextView tv_srore;
		
	}
	public class Mydapter_Itme extends BaseAdapter{
		List<Cpk_Side_Course> list=new ArrayList<Cpk_Side_Course>();
		 public Mydapter_Itme(List<Cpk_Side_Course> list) {
	        this.list = list;
	    }
		@Override
		public int getCount() {
            if (list != null){
            	
            	  return list.size();
            }else{
            	
              
            return 0;}
		}

		@Override
		public Object getItem(int v) {
			return v;
		}
		@Override
		public long getItemId(int v) {
			return v;
		}

		@Override
		public View getView(int posi, View converView, ViewGroup arg2) {
			ViewHolder2 holder;
			if (converView == null) {
				holder = new ViewHolder2();
				converView = LayoutInflater.from(B_Side_Achievement_Acy.this).inflate(R.layout.item_itme_achievement, null);
				holder.tv_class = (TextView) converView.findViewById(R.id.tv_class);
				holder.tv_srore = (TextView) converView.findViewById(R.id.tv_score);
			    converView.setTag(holder);
			} else {
				holder = (ViewHolder2) converView.getTag();
			}
			
			holder.tv_class.setText(list.get(posi).getCourse());
			
			if("合计".endsWith(list.get(posi).getCourse())){				
				//更改tx_detailrule的部分文本颜色
				SpannableStringBuilder builder = new SpannableStringBuilder(list.get(posi).getScore());  		  
				//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色    
				ForegroundColorSpan greenSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.main_color));  
				builder.setSpan(greenSpan2, 0, list.get(posi).getScore().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
				holder.tv_srore.setText(builder); 
			}else{				
				holder.tv_srore.setText(list.get(posi).getScore());
			}
			
			
//			if (list.size()-2==posi) {
//			int score=0;
//				for (int i = 0; i < list.size()-1; i++) {
//					score=Integer.parseInt(list.get(posi).getScore())+score;
//				}
//				holder.tv_class.setText("合计");
//				holder.tv_srore.setText(score+"");
//			}
			return converView;
		}
		
	}
	@Override
	protected void handleTitleBarEvent(int resId,View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;
		case ZUI_RIGHT_BUTTON:
			/*Intent intent=new Intent(B_Side_Wages_Acy.this, B_Side_Add_Repair.class);
			startActivity(intent);*/
		default:
			break;
		}
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		if(!firstLoad){
			have_read_page = 1;
			getLectureList(have_read_page,false);
		}else{
			firstLoad = false;
		}
		
	}
	
	 /**
     * 设置连接状态变化的监听器.
     */
    public void startListtenerRongYun() {
        RongIM.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
    }

    private class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus) {
                case CONNECTED:// 连接成功。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
                    break;
                case DISCONNECTED:// 断开连接。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    //A_0_App.getInstance().showExitDialog(B_Side_Achievement_Acy.this,getResources().getString(R.string.token_timeout));
                    break;
                case CONNECTING:// 连接中。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接中");
                    break;
                case NETWORK_UNAVAILABLE:// 网络不可用。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接网络不可用");
                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，用户账户在其他设备登录，本机会被踢掉线");
                    class LooperThread extends Thread {
                        public void run() {
                            Looper.prepare();
                            A_0_App.getInstance().showExitDialog(B_Side_Achievement_Acy.this,AppStrStatic.kicked_offline());
                            Looper.loop();
                        }
                    }
                    LooperThread looper = new LooperThread();
                    looper.start();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (score_Lists != null) {
        	score_Lists.clear();
        	mPullDownView = null;
        }
        adapter = null;
        adapter_itme=null;
        drawable.stop();
        drawable=null;
        super.onDestroy();
    }
    
}
