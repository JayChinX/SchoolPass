package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolpass.bangbang.B_Side_Befriend_C1_Task_Status_Details;
import com.yuanding.schoolpass.bean.Befriend_Acquire_Task_Bean;
import com.yuanding.schoolpass.service.Api.InterAcquireHistoryTaskListMain;
import com.yuanding.schoolpass.service.Api.InterAcquireTaskListMain;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.SolveClashListView;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年2月22日 上午10:56:19
 * 帮帮 我领取的任务
 */
public class B_Account_Befriend_Center_Main_2_Task_Acquire extends Fragment{
    
    private int have_read_page = 1;// 已经读的页数
	private PullToRefreshListView mPullDownView;
	private ListView lv_message_index;
	private TextView tv_early_attence,tv_today_attence;
	
	private Myadapter adapter;
	private MyIndexAdapter indexAdapter;
	private List<Befriend_Acquire_Task_Bean> mMainToadayList = new ArrayList<Befriend_Acquire_Task_Bean>();
	private List<Befriend_Acquire_Task_Bean> mMainEarlyList = new ArrayList<Befriend_Acquire_Task_Bean>();
	private View mLinerReadDataError,mLinerNoContent,liner_lecture_list_whole_view,side_attdence_loading;
    private JSONObject jsonObject,jsonObject_history;
    private ACache maACache;

    private boolean firstLoad_Tag = false;//第一次进入
    private SolveClashListView solveClashListView;
    
    /**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private int havaSuccessLoadData = 0;//1：渲染的为缓存数据，2：服务器数据
    private View viewone;
    protected Context mContext;
    
    public B_Account_Befriend_Center_Main_2_Task_Acquire() {
		super();
	}
    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity.getApplicationContext();
	}
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewone = inflater.inflate(R.layout.activity_acc_task_take, container,
				false);
	    firstLoad_Tag = true;
	    demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)viewone.findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		mPullDownView = (PullToRefreshListView) viewone.findViewById(R.id.side_lv_attendence);
		mLinerReadDataError=viewone.findViewById(R.id.side_attdence_load_error);
		mLinerNoContent=viewone.findViewById(R.id.side_attdence_no_content);
		TextView iv_blank_title = (TextView)mLinerNoContent.findViewById(R.id.tv_remind_name);
        //Button add_click = (Button)mLinerNoContent.findViewById(R.id.btn_add_click);
        iv_blank_title.setText("还没有领取任务~");
       // add_click.setText("试一下");
        
		liner_lecture_list_whole_view=viewone.findViewById(R.id.liner_attdence_list_whole_view);
		side_attdence_loading=viewone.findViewById(R.id.side_attdence_loading);
		mLinerReadDataError.setOnClickListener(onClick);
		adapter = new Myadapter();
		mPullDownView.setMode(Mode.BOTH);
		
		home_load_loading = (LinearLayout) side_attdence_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start();
		
		addHeadView(mPullDownView);
		mPullDownView.setAdapter(adapter);

		mPullDownView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                have_read_page = 1;
                
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            	if (repfresh==0) {
					repfresh=1;
					demo_swiperefreshlayout.setEnabled(false);
					demo_swiperefreshlayout.setRefreshing(false);  
					if (havaSuccessLoadData == 1) {
                        have_read_page = 1;
                        getReleaseHistoryTaskList(have_read_page);
                    } else {
                    	getMoreReleaseHistoryTask(have_read_page);
                    }
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
							getReleaseTaskList();

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
		
		
		
		if(A_0_App.USER_STATUS.equals("2")){
	        readCache();
        }
		
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
        return viewone;
	}
	
	
    private void addHeadView(PullToRefreshListView mListview) {
        
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.FILL_PARENT);
        View view = getActivity().getLayoutInflater().inflate(R.layout.item_index_befriend_today, mListview, false);
        view.setLayoutParams(layoutParams);
        ListView lv = mListview.getRefreshableView();
        lv.addHeaderView(view);
        lv_message_index = (ListView)view.findViewById(R.id.lv_message_idnex_attence);
        tv_today_attence = (TextView)view.findViewById(R.id.tv_today_attence);
        tv_early_attence = (TextView)view.findViewById(R.id.tv_early_attence);
        indexAdapter = new MyIndexAdapter();
        lv_message_index.setAdapter(indexAdapter);
       
    }
	
    private void readCache( ) {
        maACache = ACache.get(getActivity());
        jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_query_acquire_task+ A_0_App.USER_UNIQID);
        jsonObject_history = maACache.getAsJSONObject(AppStrStatic.cache_key_query_history_acquire_task+ A_0_App.USER_UNIQID);
        if (jsonObject!= null&&jsonObject_history!= null&& !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
           showInfo(jsonObject,jsonObject_history);
        }else{
            have_read_page = 1;
            getReleaseTaskList();
            
        }
    }
    
    private void showInfo(JSONObject jsonObject,JSONObject json_history) {
      
            int state = jsonObject.optInt("status");
            int state_history = jsonObject.optInt("status");
            List<Befriend_Acquire_Task_Bean> toadayListTemp = null;
            List<Befriend_Acquire_Task_Bean> earlyListTemp = null;
            
            if (state == 1) {
                toadayListTemp = new ArrayList<Befriend_Acquire_Task_Bean>();
                JSONArray jsonArrayItem_early = jsonObject.optJSONArray("list");
                if (jsonArrayItem_early != null && jsonArrayItem_early.length() > 0) {
                    earlyListTemp = JSON.parseArray(jsonArrayItem_early + "",Befriend_Acquire_Task_Bean.class);
                }
            }
            if (state_history == 1) {
                earlyListTemp = new ArrayList<Befriend_Acquire_Task_Bean>();
                JSONArray jsonArrayItem_early = json_history.optJSONArray("list");

                if (jsonArrayItem_early != null && jsonArrayItem_early.length() > 0) {
                    earlyListTemp = JSON.parseArray(jsonArrayItem_early + "",Befriend_Acquire_Task_Bean.class);
                }
            }
            havaSuccessLoadData = 1;
           goActionData(toadayListTemp, earlyListTemp);
        
    }
    
    private void goActionData(List<Befriend_Acquire_Task_Bean> mToadayList, List<Befriend_Acquire_Task_Bean> mEarlyList) {
    	if(getActivity()==null||getActivity().isFinishing())
            return;
       
        
        if(mToadayList != null && mToadayList.size() == 0){
            tv_today_attence.setVisibility(View.GONE);
            lv_message_index.setVisibility(View.GONE);
        }else{
            tv_today_attence.setVisibility(View.VISIBLE);
            lv_message_index.setVisibility(View.VISIBLE);
        }
            
        if(mEarlyList != null && mEarlyList.size() == 0){
            tv_early_attence.setVisibility(View.GONE);
        }else{
            have_read_page = 2;
            tv_early_attence.setVisibility(View.VISIBLE);
        }
        
        if(mToadayList.size() == 0 && mEarlyList.size() == 0){
            showLoadResult(false, false, false, true);
        }else{
            showLoadResult(false, true, false, false);
        }
        mMainToadayList = mToadayList;
        mMainEarlyList = mEarlyList;
        indexAdapter.notifyDataSetChanged();
        solveClashListView = new SolveClashListView(); 
        solveClashListView.setListViewHeightBasedOnChildren(lv_message_index,1);
        
        if (mMainEarlyList.size()==0) {
        	Befriend_Acquire_Task_Bean acquire_Task_Bean=new Befriend_Acquire_Task_Bean();
        	acquire_Task_Bean.setId("yd");
        	acquire_Task_Bean.setCreateTime("1111111111");
        	acquire_Task_Bean.setCreateUsername("");
        	acquire_Task_Bean.setWorkStatus("1");
        	acquire_Task_Bean.setType("1");
        	acquire_Task_Bean.setTitle("pass");
        	mMainEarlyList.add(acquire_Task_Bean);
		}
        adapter.notifyDataSetChanged();
        demo_swiperefreshlayout.setRefreshing(false);  
        if(null!=mPullDownView){
        	mPullDownView.onRefreshComplete();
			mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
        }
		
		repfresh=0;
    }
	
    //获取待处理数据
   	private void getReleaseTaskList() {
   	    A_0_App.getApi().getAcquireTaskListMain(getActivity(), A_0_App.USER_TOKEN,new InterAcquireTaskListMain() {
               @Override
               public void onSuccess(List<Befriend_Acquire_Task_Bean> release_Task_Beans) {
               	if(getActivity()==null||getActivity().isFinishing())
                       return;
                clearToadayList(false);
            
               	mMainToadayList=release_Task_Beans;
               	getReleaseHistoryTaskList(have_read_page);
               }
           },new Inter_Call_Back() {
               
               @Override
               public void onFinished() {
                   
               }
               
               @Override
               public void onFailure(String msg) {
                   if(getActivity()==null||getActivity().isFinishing())
                       return;
                   if (havaSuccessLoadData == 0) {
                       showLoadResult(false, false, true, false);
                   }
                   PubMehods.showToastStr(getActivity(), msg);
               }
               
               @Override
               public void onCancelled() {
                   
               } 
           });
   	}
    
    //获取历史任务列表数据
	private void getReleaseHistoryTaskList(final int page_no) {
	    A_0_App.getApi().getAcquireHistoryTaskListMain(getActivity(), A_0_App.USER_TOKEN, String.valueOf(page_no), new InterAcquireHistoryTaskListMain() {
            @Override
            public void onSuccess(List<Befriend_Acquire_Task_Bean> release_Task_Beans) {
            	if(getActivity()==null||getActivity().isFinishing())
                    return;
                clearEarlyList(false);
            	mMainEarlyList=release_Task_Beans;
                goActionData(mMainToadayList,mMainEarlyList);
            }
        },new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                
            }
            
            @Override
            public void onFailure(String msg) {
                if(getActivity()==null||getActivity().isFinishing())
                    return;
                if (havaSuccessLoadData == 0) {
                    showLoadResult(false, false, true, false);
                }
                demo_swiperefreshlayout.setRefreshing(false);  
                if(null!=mPullDownView){
                    mPullDownView.onRefreshComplete();
                    mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
                }
                repfresh=0;
                PubMehods.showToastStr(getActivity(), msg);
            }
            
            @Override
            public void onCancelled() {
                
            }
        });
	}


    // 上拉刷新初始化数据历史
    private void getMoreReleaseHistoryTask(int page_no) {
        if(A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
            return;
        A_0_App.getApi().getAcquireHistoryTaskListMain(getActivity(), A_0_App.USER_TOKEN, String.valueOf(page_no), new InterAcquireHistoryTaskListMain() {
            @Override
            public void onSuccess(List<Befriend_Acquire_Task_Bean> release_Task_Beans) {
            	if(getActivity()==null||getActivity().isFinishing())
                    return;
                if (release_Task_Beans!= null && release_Task_Beans.size() > 0) {
                    have_read_page += 1;
                    for (int i = 0; i < release_Task_Beans.size(); i++) {
                        mMainEarlyList.add(release_Task_Beans.get(i));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                	PubMehods.showToastStr(getActivity(),"没有更多了");
                }
                if(null!=mPullDownView){
                    mPullDownView.onRefreshComplete();
                }
                repfresh=0;
            }
        },new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                
            }
            
            @Override
            public void onFailure(String msg) {
                if(getActivity()==null||getActivity().isFinishing())
                    return;
                PubMehods.showToastStr(getActivity(), msg);
                if(null!=mPullDownView){
                    mPullDownView.onRefreshComplete();
                }
                
                repfresh=0;
            }
            
            @Override
            public void onCancelled() {
                
            }
        });
        
    }
    
	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.side_attdence_load_error:
			    showLoadResult(true,false, false, false);
                have_read_page = 1;
                getReleaseTaskList();
				break;
			default:
				break;
			}
		}
	};
	
    
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
        if (loading) {
			drawable.start();
			side_attdence_loading.setVisibility(View.VISIBLE);
		} else {
			if (drawable != null) {
				drawable.stop();
			}
			side_attdence_loading.setVisibility(View.GONE);
		}
    }
	
    public class MyIndexAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mMainToadayList != null)
                return mMainToadayList.size();
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
        public View getView(final int position, View convertview, ViewGroup arg2) {
            ViewHolder holder;
            if (convertview == null) {
                holder = new ViewHolder();
                convertview = View.inflate(getActivity(),R.layout.item_help_task, null);
                holder.iv_help_task_photo=(CircleImageView) convertview.findViewById(R.id.iv_help_task_photo);
                holder.tv_title=(TextView) convertview.findViewById(R.id.tv_title);
                holder.tv_wait_money=(TextView) convertview.findViewById(R.id.tv_wait_money);
                holder.tv_name=(TextView) convertview.findViewById(R.id.tv_name);
                holder.tv_time=(TextView) convertview.findViewById(R.id.tv_time);
                
                convertview.setTag(holder);
            } else {
                holder = (ViewHolder) convertview.getTag();
            }
            
            if (mMainToadayList.get(position).getType().equals("1")) {
              	 holder.iv_help_task_photo.setBackgroundResource(R.drawable.bangbang_ico_q_list);
   			}else if(mMainToadayList.get(position).getType().equals("2")){
   				holder.iv_help_task_photo.setBackgroundResource(R.drawable.bangbang_ico_s_list);
   			}else if(mMainToadayList.get(position).getType().equals("3")){
   				holder.iv_help_task_photo.setBackgroundResource(R.drawable.bangbang_ico_j_list);
   			}else if(mMainToadayList.get(position).getType().equals("4")){
   				holder.iv_help_task_photo.setBackgroundResource(R.drawable.bangbang_ico_x_list);
   			}else{
   				holder.iv_help_task_photo.setBackgroundResource(R.drawable.bangbang_ico_t_list);
   			}
            holder.tv_title.setText(mMainToadayList.get(position).getTitle());
            holder.tv_name.setText("发起人:"+mMainToadayList.get(position).getCreateUsername());
            holder.tv_time.setText(PubMehods.getFormatDate(Long.valueOf(mMainToadayList.get(position).getCreateTime()), "MM/dd HH:mm"));
            switch (mMainToadayList.get(position).getWorkStatus()) {
			case "1":
				holder.tv_wait_money.setText("待收款");
				holder.tv_wait_money.setTextColor(getResources().getColor(R.color.attence_later));
				break;
			case "2":
				holder.tv_wait_money.setText("已收款");
				holder.tv_wait_money.setTextColor(getResources().getColor(R.color.title_no_focus_login));
				break;
			case "0":
				holder.tv_wait_money.setText("待完成");
				holder.tv_wait_money.setTextColor(getResources().getColor(R.color.attence_later));
				break;
			case "9":
				holder.tv_wait_money.setText("您已取消");
				holder.tv_wait_money.setTextColor(getResources().getColor(R.color.title_no_focus_login));
				break;
			default:
				break;
			}
            convertview.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
               Intent intent=new Intent(getActivity(), B_Side_Befriend_C1_Task_Status_Details.class);
               intent.putExtra("type", 2);
               intent.putExtra("id", mMainToadayList.get(position).getId());
               startActivity(intent);
				}
			});
            
          
            return convertview;
        }

    }
    
	private class Myadapter extends BaseAdapter {

		@Override
		public int getCount() {
            if (mMainEarlyList != null)
                return mMainEarlyList.size();
            return 0;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertview, ViewGroup arg2) {
            ViewHolder holder;
            if (convertview == null) {
                holder = new ViewHolder();
                convertview = View.inflate(getActivity(),R.layout.item_help_task, null);
                holder.iv_help_task_photo=(CircleImageView) convertview.findViewById(R.id.iv_help_task_photo);
                holder.tv_title=(TextView) convertview.findViewById(R.id.tv_title);
                holder.tv_wait_money=(TextView) convertview.findViewById(R.id.tv_wait_money);
                holder.tv_name=(TextView) convertview.findViewById(R.id.tv_name);
                holder.tv_time=(TextView) convertview.findViewById(R.id.tv_time);
                holder.liner_ac=(LinearLayout) convertview.findViewById(R.id.liner_ac);
                
                convertview.setTag(holder);
            } else {
                holder = (ViewHolder) convertview.getTag();
            }
           if (mMainEarlyList.get(position).getId().equals("yd")) {
        	   holder.liner_ac.setVisibility(View.GONE);
		}
            if (mMainEarlyList.get(position).getType().equals("1")) {
              	 holder.iv_help_task_photo.setBackgroundResource(R.drawable.bangbang_ico_q_list);
   			}else if(mMainEarlyList.get(position).getType().equals("2")){
   				holder.iv_help_task_photo.setBackgroundResource(R.drawable.bangbang_ico_s_list);
   			}else if(mMainEarlyList.get(position).getType().equals("3")){
   				holder.iv_help_task_photo.setBackgroundResource(R.drawable.bangbang_ico_j_list);
   			}else if(mMainEarlyList.get(position).getType().equals("4")){
   				holder.iv_help_task_photo.setBackgroundResource(R.drawable.bangbang_ico_x_list);
   			}else{
   				holder.iv_help_task_photo.setBackgroundResource(R.drawable.bangbang_ico_t_list);
   			}
            holder.tv_title.setText(mMainEarlyList.get(position).getTitle());
            holder.tv_name.setText("发起人:"+mMainEarlyList.get(position).getCreateUsername());
            holder.tv_time.setText(PubMehods.getFormatDate(Long.valueOf(mMainEarlyList.get(position).getCreateTime()), "MM/dd HH:mm"));
            switch (mMainEarlyList.get(position).getWorkStatus()) {
			case "1":
				holder.tv_wait_money.setText("待收款");
				holder.tv_wait_money.setTextColor(getResources().getColor(R.color.title_no_focus_login));
				break;
			case "2":
				holder.tv_wait_money.setText("已收款");
				holder.tv_wait_money.setTextColor(getResources().getColor(R.color.title_no_focus_login));
				break;
			case "0":
				holder.tv_wait_money.setText("待完成");
				holder.tv_wait_money.setTextColor(getResources().getColor(R.color.attence_later));
				break;
			case "9":
				holder.tv_wait_money.setText("您已取消");
				holder.tv_wait_money.setTextColor(getResources().getColor(R.color.title_no_focus_login));
				break;
			default:
				break;
			}
            convertview.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!mMainEarlyList.get(position).getId().equals("yd")) {
						 Intent intent=new Intent(getActivity(), B_Side_Befriend_C1_Task_Status_Details.class);
			               intent.putExtra("type", 2);
			               intent.putExtra("id", mMainEarlyList.get(position).getId());
			               startActivity(intent);
					}
              
				}
			});
            
            
            return convertview;
		}

	}
	
	private class ViewHolder {
	    CircleImageView  iv_help_task_photo;
		TextView tv_title;
		TextView tv_wait_money;
		TextView tv_name;
		TextView tv_time;
		LinearLayout liner_ac;
	}
	
	/**
     * 设置连接状态变化的监听器.
     */
    public void startListtenerRongYun() {
        RongIM.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
    }

    private class MyConnectionStatusListener implements
            RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {
            switch (connectionStatus) {
            case CONNECTED:// 连接成功。
                A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
                break;
            case DISCONNECTED:// 断开连接。
                A_Main_My_Message_Acy
                        .logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                //A_0_App.getInstance().showExitDialog(getActivity(),getResources().getString(R.string.token_timeout));
                break;
            case CONNECTING:// 连接中。
                A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接中");
                break;
            case NETWORK_UNAVAILABLE:// 网络不可用。
                A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接网络不可用");
                break;
            case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
                A_Main_My_Message_Acy
                        .logE("教师——connectRoogIm()，用户账户在其他设备登录，本机会被踢掉线");
                class LooperThread extends Thread {
                    public void run() {
                        Looper.prepare();
                        if (getActivity()!=null||!getActivity().isFinishing()) {
                        	A_0_App.getInstance().showExitDialog(getActivity(),
                                    AppStrStatic.kicked_offline());
						}
                        Looper.loop();
                    }
                }
                LooperThread looper = new LooperThread();
                looper.start();
                break;
            }
        }
    }
    

    private void clearToadayList(boolean setNull) {
        if (mMainToadayList != null) {
            mMainToadayList.clear();
            if (setNull)
                mMainToadayList = null;
        }
    }

    private void clearEarlyList(boolean setNull) {
        if (mMainEarlyList != null) {
            mMainEarlyList.clear();
            if (setNull)
                mMainEarlyList = null;
        }
    }
    
    
    @Override
	public void onResume() {
        if (!firstLoad_Tag) {
           have_read_page = 1;
            getReleaseTaskList();
        } else {
            firstLoad_Tag = false;
        }
        super.onResume();
    }
	
    @Override
	public void onDestroy() {
        clearToadayList(true);
        clearEarlyList(true);
        drawable.stop();
        drawable=null;
        adapter = null;
        indexAdapter = null;
        super.onDestroy();
    }
    
    

}
