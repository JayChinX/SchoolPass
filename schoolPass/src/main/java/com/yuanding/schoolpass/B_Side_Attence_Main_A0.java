package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Side_Attence;
import com.yuanding.schoolpass.service.Api.InterAttdenceListMain;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.SolveClashListView;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年2月22日 上午10:56:19
 * 课堂考勤主界面
 */
public class B_Side_Attence_Main_A0 extends A_0_CpkBaseTitle_Navi{
    private int[] cate_image = {
            R.drawable.c1, R.drawable.c2,R.drawable.c3, R.drawable.c4,
            R.drawable.c5, R.drawable.c6,R.drawable.c7, R.drawable.c8};
    
    public static final String ATTENCE_ATD_ID ="attence_atd_id";//传值
    public static final String ATTENCE_ACY_TYPE ="attence_activity_type";//1,表示列表进入 2，表示新建考勤进入
    public static final String ATTENCE_ACY_EARLY ="attence_activity_early";//true,表示较早考勤 false，表示当日考勤
    
    private int have_read_page = 1;// 已经读的页数
	private PullToRefreshListView mPullDownView;
	private ListView lv_message_index;
	private TextView tv_early_attence,tv_today_attence;
	
	private Myadapter adapter;
	private MyIndexAdapter indexAdapter;
	private List<Cpk_Side_Attence> mMainToadayList = new ArrayList<Cpk_Side_Attence>();
	private List<Cpk_Side_Attence> mMainEarlyList = new ArrayList<Cpk_Side_Attence>();
	private View mLinerReadDataError,mLinerNoContent,liner_lecture_list_whole_view,side_attdence_loading;
    private JSONObject jsonObject;
    private ACache maACache;

    private boolean firstLoad_Tag = false;//第一次进入
    private SolveClashListView solveClashListView;
    private boolean havaSuccessLoadData = false;
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
	    A_0_App.getInstance().addActivity(this);
		setView(R.layout.activity_side_attendence_main);
		setTitleText("协助考勤");
		
	    firstLoad_Tag = true;
	    demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		mPullDownView = (PullToRefreshListView) findViewById(R.id.side_lv_attendence);
		mLinerReadDataError=findViewById(R.id.side_attdence_load_error);
		mLinerNoContent=findViewById(R.id.side_attdence_no_content);
		TextView iv_blank_title = (TextView)mLinerNoContent.findViewById(R.id.tv_remind_name);
        Button add_click = (Button)mLinerNoContent.findViewById(R.id.btn_add_click);
        iv_blank_title.setText("没有考勤记录~");
        add_click.setText("现在创建一个");
        
		liner_lecture_list_whole_view=findViewById(R.id.liner_attdence_list_whole_view);
		side_attdence_loading=findViewById(R.id.side_attdence_loading);
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
                String label = DateUtils.formatDateTime(getApplicationContext(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                have_read_page = 1;
                getAttdenceList(have_read_page);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            	if (repfresh==0) {
					repfresh=1;
					demo_swiperefreshlayout.setEnabled(false);
					demo_swiperefreshlayout.setRefreshing(false);  
					getMoreLecture(have_read_page);
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
							 getAttdenceList(have_read_page);

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
            public void onItemClick(AdapterView<?> parent, View view, int posi, long id) {
                int position_index = posi - 2;
                goDtail(mMainEarlyList, position_index,true);
            }
        });
		
		add_click.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(B_Side_Attence_Main_A0.this, B_Side_Attence_Main_A1_New_Attence.class));
            }
        });
		
		if(A_0_App.USER_STATUS.equals("2")){
	        setZuiRightBtn(R.drawable.navigationbar_add_button);
	        showTitleBt(ZUI_RIGHT_BUTTON, true);
	        setPianRightBtn(R.drawable.navigationbar_help);
	        showTitleBt(PIAN_RIGHT_BUTTON, true);
	        add_click.setVisibility(View.VISIBLE);
	        readCache();
        }else{
            showTitleBt(ZUI_RIGHT_BUTTON, false);
            showTitleBt(PIAN_RIGHT_BUTTON, false);
            showLoadResult(false, false, false, true);
        }
		
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }

	}
	
	private void goDtail(List<Cpk_Side_Attence> list,int posi,boolean earlyAttence) {
        if(posi<0)
        {
        	return;
        }
		String attence_atd_id = list.get(posi).getAtd_id();
        if(attence_atd_id == null || attence_atd_id.equals(""))
            return;
        Intent intent=new Intent(B_Side_Attence_Main_A0.this, B_Side_Attence_Main_A3_Detail.class);
        intent.putExtra(ATTENCE_ATD_ID, attence_atd_id);
        intent.putExtra(ATTENCE_ACY_TYPE, 1);
        intent.putExtra(ATTENCE_ACY_EARLY, earlyAttence);
        startActivity(intent);
    }
	
    private void addHeadView(PullToRefreshListView mListview) {
        
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        View view = getLayoutInflater().inflate(R.layout.item_index_side_accence, mListview, false);
        view.setLayoutParams(layoutParams);
        ListView lv = mListview.getRefreshableView();
        lv.addHeaderView(view);
        
        lv_message_index = (ListView)view.findViewById(R.id.lv_message_idnex_attence);
        tv_today_attence = (TextView)view.findViewById(R.id.tv_today_attence);
        tv_early_attence = (TextView)view.findViewById(R.id.tv_early_attence);
        indexAdapter = new MyIndexAdapter();
        
        lv_message_index.setAdapter(indexAdapter);
        //前面的
        lv_message_index.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int posi,long arg3) {
                goDtail(mMainToadayList, posi,false);
            }
       });
    }
	
    private void readCache( ) {
        // TODO Auto-generated method stub
        maACache = ACache.get(this);
        jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_side_attdence_list+A_0_App.USER_UNIQID);
        if (jsonObject!= null&& !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        }else{
            have_read_page = 1;
            getAttdenceList(have_read_page);
        }
    }
    
    private void showInfo(JSONObject jsonObject) {
        // TODO Auto-generated method stub
      
            int state = jsonObject.optInt("status");
            List<Cpk_Side_Attence> toadayListTemp = null;
            List<Cpk_Side_Attence> earlyListTemp = null;
            if (state == 1) {
                toadayListTemp = new ArrayList<Cpk_Side_Attence>();
                earlyListTemp = new ArrayList<Cpk_Side_Attence>();
                JSONArray jsonArrayItem_today = jsonObject.optJSONArray("today");
                JSONArray jsonArrayItem_early = jsonObject.optJSONArray("early");

                if (jsonArrayItem_today != null && jsonArrayItem_today.length() > 0) {
                    toadayListTemp = JSON.parseArray(jsonArrayItem_today + "",Cpk_Side_Attence.class);
                }
                if (jsonArrayItem_early != null && jsonArrayItem_early.length() > 0) {
                    earlyListTemp = JSON.parseArray(jsonArrayItem_early + "",Cpk_Side_Attence.class);
                }
            }
            goActionData(toadayListTemp, earlyListTemp);
        
    }
    
    private void goActionData(List<Cpk_Side_Attence> mToadayList, List<Cpk_Side_Attence> mEarlyList) {
        if (isFinishing())
            return;
        havaSuccessLoadData = true;
        clearToadayList(false);
        clearEarlyList(false);
        
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
        
        adapter.notifyDataSetChanged();
        demo_swiperefreshlayout.setRefreshing(false);  
        if(null!=mPullDownView){
            mPullDownView.onRefreshComplete();
            mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
        }
        repfresh=0;
    }
	
    //获取课堂考勤列表数据
	private void getAttdenceList(final int page_no) {
	    A_0_App.getApi().getAttdenceListMain(B_Side_Attence_Main_A0.this, A_0_App.USER_TOKEN, String.valueOf(page_no), new InterAttdenceListMain() {
            @Override
            public void onSuccess(List<Cpk_Side_Attence> mToadayList, List<Cpk_Side_Attence> mEarlyList) {
            	goActionData(mToadayList, mEarlyList);
            }
        },new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                if (!havaSuccessLoadData) {
                    showLoadResult(false,false, true, false);
                }
                
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


    // 上拉刷新初始化数据
    private void getMoreLecture(int page_no) {
        if(A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
            return;
        A_0_App.getApi().getAttdenceListMain(B_Side_Attence_Main_A0.this, A_0_App.USER_TOKEN, String.valueOf(page_no), new InterAttdenceListMain() {
            @Override
            public void onSuccess(List<Cpk_Side_Attence> mToadayList, List<Cpk_Side_Attence> mEarlyList) {
                if (isFinishing())
                    return;
                //A_0_App.getInstance().CancelProgreDialog(B_Side_Attence_Main_A0.this);
                if (mEarlyList!= null && mEarlyList.size() > 0) {
                    have_read_page += 1;
                    int totleSize = mMainEarlyList.size();
                    for (int i = 0; i < mEarlyList.size(); i++) {
                        mMainEarlyList.add(mEarlyList.get(i));
                    }
                    adapter.notifyDataSetChanged();
                    //mPullDownView.getRefreshableView().setSelection(totleSize + 1);
                } else {
                    PubMehods.showToastStr(B_Side_Attence_Main_A0.this,R.string.pub_is_lastest);
                }
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
                A_0_App.getInstance().CancelProgreDialog(B_Side_Attence_Main_A0.this);
                PubMehods.showToastStr(B_Side_Attence_Main_A0.this, msg);
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
    
	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.side_attdence_load_error:
			    showLoadResult(true,false, false, false);
                have_read_page = 1;
                getAttdenceList(have_read_page);
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
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int position, View convertview, ViewGroup arg2) {
            ViewHolder holder;
            if (convertview == null) {
                holder = new ViewHolder();
                convertview = View.inflate(B_Side_Attence_Main_A0.this,R.layout.item_side_attence_main, null);
                holder.iv_attdence_picture=(ImageView) convertview.findViewById(R.id.iv_attdence_picture);
                holder.iv_attendence_status=(ImageView) convertview.findViewById(R.id.iv_attendence_status);
                holder.tv_attendence_title=(TextView) convertview.findViewById(R.id.tv_attendence_title);
                holder.tv_attendece_create_time=(TextView) convertview.findViewById(R.id.tv_attendece_create_time);
                holder.tv_attence_leave=(TextView) convertview.findViewById(R.id.tv_attence_leave);
                holder.tv_attence_absent = (TextView) convertview.findViewById(R.id.tv_attence_absent);
                holder.tv_attence_late=(TextView) convertview.findViewById(R.id.tv_attence_late);
                holder.tv_attence_early=(TextView) convertview.findViewById(R.id.tv_attence_early);
                
                holder.liner_leave_start_or_end=(LinearLayout) convertview.findViewById(R.id.liner_leave_start_or_end);
                holder.liner_attence_result=(LinearLayout) convertview.findViewById(R.id.liner_attence_result);
                holder.tv_leave_start_or_end=(TextView) convertview.findViewById(R.id.tv_leave_start_or_end);
                
                
                holder.tv_late_left=(TextView) convertview.findViewById(R.id.tv_late_left);
                holder.tv_late_right=(TextView) convertview.findViewById(R.id.tv_late_right);
                holder.tv_early_left=(TextView) convertview.findViewById(R.id.tv_early_left);
                holder.tv_early_right=(TextView) convertview.findViewById(R.id.tv_early_right);
                
                holder.tv_leave_left=(TextView) convertview.findViewById(R.id.tv_leave_left);
                holder.tv_leave_right=(TextView) convertview.findViewById(R.id.tv_leave_right);
                holder.tv_absent_left=(TextView) convertview.findViewById(R.id.tv_absent_left);
                holder.tv_absent_right=(TextView) convertview.findViewById(R.id.tv_absent_right);
                
                convertview.setTag(holder);
            } else {
                holder = (ViewHolder) convertview.getTag();
            }
            
            if (position % 8 == 0) {
                holder.iv_attdence_picture.setBackgroundResource(cate_image[0]);
            } else if (position % 8 == 1) {
                holder.iv_attdence_picture.setBackgroundResource(cate_image[1]);
            } else if (position % 8 == 2) {
                holder.iv_attdence_picture.setBackgroundResource(cate_image[2]);
            } else if (position % 8 == 3) {
                holder.iv_attdence_picture.setBackgroundResource(cate_image[3]);
            } else if (position % 8 == 4) {
                holder.iv_attdence_picture.setBackgroundResource(cate_image[4]);
            } else if (position % 8 == 5) {
                holder.iv_attdence_picture.setBackgroundResource(cate_image[5]);
            } else if (position % 8 == 6) {
                holder.iv_attdence_picture.setBackgroundResource(cate_image[6]);
            } else if (position % 8 == 7) {
                holder.iv_attdence_picture.setBackgroundResource(cate_image[7]);
            }
//          holder.iv_attdence_picture.setBackgroundResource(cate_image[PubMehods.getRandomIntData(cate_image.length)]);
            
            String title = mMainToadayList.get(position).getTitle();
            holder.tv_attendence_title.setText(title);
            holder.tv_attendece_create_time.setText(PubMehods.getFormatDate(Long.valueOf(mMainToadayList.get(position).getCreate_time()), "HH:mm"));
            
            String str_leave = mMainToadayList.get(position).getLeave();
            String str_absent = mMainToadayList.get(position).getAbsent();
            String str_late= mMainToadayList.get(position).getLate();
            String str_early = mMainToadayList.get(position).getEarly();
            
            if(str_leave.equals("0")){
                holder.tv_leave_left.setVisibility(View.GONE);
                holder.tv_leave_right.setVisibility(View.GONE);
                holder.tv_attence_leave.setVisibility(View.GONE);
            }else{
                holder.tv_leave_left.setVisibility(View.VISIBLE);
                holder.tv_leave_right.setVisibility(View.VISIBLE);
                holder.tv_attence_leave.setVisibility(View.VISIBLE);
                holder.tv_attence_leave.setText(str_leave); 
            }
            
            if(str_absent.equals("0")){
                holder.tv_absent_left.setVisibility(View.GONE);
                holder.tv_absent_right.setVisibility(View.GONE);
                holder.tv_attence_absent.setVisibility(View.GONE);
            }else{
                holder.tv_absent_left.setVisibility(View.VISIBLE);
                holder.tv_absent_right.setVisibility(View.VISIBLE);
                holder.tv_attence_absent.setVisibility(View.VISIBLE);
                holder.tv_attence_absent.setText(str_absent); 
            }
            if(str_late.equals("0")){
                holder.tv_late_left.setVisibility(View.GONE);
                holder.tv_late_right.setVisibility(View.GONE);
                holder.tv_attence_late.setVisibility(View.GONE);
            }else{
                holder.tv_late_left.setVisibility(View.VISIBLE);
                holder.tv_late_right.setVisibility(View.VISIBLE);
                holder.tv_attence_late.setVisibility(View.VISIBLE);
                holder.tv_attence_late.setText(str_late); 
            }
            
            if(str_early.equals("0")){
                holder.tv_early_left.setVisibility(View.GONE);
                holder.tv_early_right.setVisibility(View.GONE);
                holder.tv_attence_early.setVisibility(View.GONE);
            }else{
                holder.tv_early_left.setVisibility(View.VISIBLE);
                holder.tv_early_right.setVisibility(View.VISIBLE);
                holder.tv_attence_early.setVisibility(View.VISIBLE);
                holder.tv_attence_early.setText(str_early); 
            }
            
            if(mMainToadayList.get(position).getStatus().equals("0")){//还未开始
                if(str_absent.equals("0") && str_late.equals("0") && str_early.equals("0") && str_leave.equals("0")){
                    holder.liner_leave_start_or_end.setVisibility(View.VISIBLE);
                    holder.liner_attence_result.setVisibility(View.GONE);
                    holder.tv_leave_start_or_end.setText("当前为全勤。");
                    
                }else{
                    holder.liner_leave_start_or_end.setVisibility(View.GONE);
                    holder.liner_attence_result.setVisibility(View.VISIBLE);
                }
                holder.iv_attendence_status.setVisibility(View.GONE);
            }else if(mMainToadayList.get(position).getStatus().equals("1")){//进行中
                if(str_absent.equals("0") && str_late.equals("0") && str_early.equals("0") && str_leave.equals("0")){
                    holder.liner_leave_start_or_end.setVisibility(View.VISIBLE);
                    holder.liner_attence_result.setVisibility(View.GONE);
                    holder.tv_leave_start_or_end.setText("正在进行课堂考勤。");
                }else{
                  holder.liner_leave_start_or_end.setVisibility(View.GONE);
                  holder.liner_attence_result.setVisibility(View.VISIBLE);
                }
                String atd_time = mMainToadayList.get(position).getAtd_time();
                if(atd_time != null && !atd_time.equals("") && !atd_time.equals("<null>")){
                    holder.iv_attendence_status.setVisibility(View.VISIBLE);
                    holder.iv_attendence_status.setBackgroundResource(R.drawable.icon_attence_checking);
                }else{
                    holder.iv_attendence_status.setVisibility(View.GONE);
                }
            }else if(mMainToadayList.get(position).getStatus().equals("2")){//已结束
                if(str_absent.equals("0") && str_late.equals("0") && str_early.equals("0") && str_leave.equals("0")){
                    holder.liner_leave_start_or_end.setVisibility(View.VISIBLE);
                    holder.liner_attence_result.setVisibility(View.GONE);
                    holder.tv_leave_start_or_end.setText("全勤");
                }else{
                    holder.liner_leave_start_or_end.setVisibility(View.GONE);
                    holder.liner_attence_result.setVisibility(View.VISIBLE);
                }
                String atd_time = mMainToadayList.get(position).getAtd_time();
                if(atd_time != null && !atd_time.equals("") && !atd_time.equals("<null>")){
                    holder.iv_attendence_status.setVisibility(View.VISIBLE);
                    holder.iv_attendence_status.setBackgroundResource(R.drawable.icon_attence_checked);
                }else{
                    holder.iv_attendence_status.setVisibility(View.GONE);
                }
            }
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertview, ViewGroup arg2) {
            ViewHolder holder;
            if (convertview == null) {
                holder = new ViewHolder();
                convertview = View.inflate(B_Side_Attence_Main_A0.this,R.layout.item_side_attence_main, null);
                holder.iv_attdence_picture=(ImageView) convertview.findViewById(R.id.iv_attdence_picture);
                holder.iv_attendence_status=(ImageView) convertview.findViewById(R.id.iv_attendence_status);
                holder.tv_attendence_title=(TextView) convertview.findViewById(R.id.tv_attendence_title);
                holder.tv_attendece_create_time=(TextView) convertview.findViewById(R.id.tv_attendece_create_time);
                holder.tv_attence_leave=(TextView) convertview.findViewById(R.id.tv_attence_leave);
                holder.tv_attence_absent = (TextView) convertview.findViewById(R.id.tv_attence_absent);
                holder.tv_attence_late=(TextView) convertview.findViewById(R.id.tv_attence_late);
                holder.tv_attence_early=(TextView) convertview.findViewById(R.id.tv_attence_early);
                
                holder.liner_leave_start_or_end=(LinearLayout) convertview.findViewById(R.id.liner_leave_start_or_end);
                holder.liner_attence_result=(LinearLayout) convertview.findViewById(R.id.liner_attence_result);
                holder.tv_leave_start_or_end=(TextView) convertview.findViewById(R.id.tv_leave_start_or_end);
                
                
                holder.tv_late_left=(TextView) convertview.findViewById(R.id.tv_late_left);
                holder.tv_late_right=(TextView) convertview.findViewById(R.id.tv_late_right);
                holder.tv_early_left=(TextView) convertview.findViewById(R.id.tv_early_left);
                holder.tv_early_right=(TextView) convertview.findViewById(R.id.tv_early_right);
                
                holder.tv_leave_left=(TextView) convertview.findViewById(R.id.tv_leave_left);
                holder.tv_leave_right=(TextView) convertview.findViewById(R.id.tv_leave_right);
                holder.tv_absent_left=(TextView) convertview.findViewById(R.id.tv_absent_left);
                holder.tv_absent_right=(TextView) convertview.findViewById(R.id.tv_absent_right);
                
                convertview.setTag(holder);
            } else {
                holder = (ViewHolder) convertview.getTag();
            }
            
            if (position % 8 == 0) {
                holder.iv_attdence_picture.setBackgroundResource(cate_image[0]);
            } else if (position % 8 == 1) {
                holder.iv_attdence_picture.setBackgroundResource(cate_image[1]);
            } else if (position % 8 == 2) {
                holder.iv_attdence_picture.setBackgroundResource(cate_image[2]);
            } else if (position % 8 == 3) {
                holder.iv_attdence_picture.setBackgroundResource(cate_image[3]);
            } else if (position % 8 == 4) {
                holder.iv_attdence_picture.setBackgroundResource(cate_image[4]);
            } else if (position % 8 == 5) {
                holder.iv_attdence_picture.setBackgroundResource(cate_image[5]);
            } else if (position % 8 == 6) {
                holder.iv_attdence_picture.setBackgroundResource(cate_image[6]);
            } else if (position % 8 == 7) {
                holder.iv_attdence_picture.setBackgroundResource(cate_image[7]);
            }
//          holder.iv_attdence_picture.setBackgroundResource(cate_image[PubMehods.getRandomIntData(cate_image.length)]);
            
            String title = mMainEarlyList.get(position).getTitle();
            holder.tv_attendence_title.setText(title);
            holder.tv_attendece_create_time.setText(PubMehods.getFormatDate(Long.valueOf(mMainEarlyList.get(position).getCreate_time()), "MM/dd"));
            
            String str_leave = mMainEarlyList.get(position).getLeave();
            String str_absent = mMainEarlyList.get(position).getAbsent();
            String str_late= mMainEarlyList.get(position).getLate();
            String str_early = mMainEarlyList.get(position).getEarly();
            
            if(str_leave.equals("0")){
                holder.tv_leave_left.setVisibility(View.GONE);
                holder.tv_leave_right.setVisibility(View.GONE);
                holder.tv_attence_leave.setVisibility(View.GONE);
            }else{
                holder.tv_leave_left.setVisibility(View.VISIBLE);
                holder.tv_leave_right.setVisibility(View.VISIBLE);
                holder.tv_attence_leave.setVisibility(View.VISIBLE);
                holder.tv_attence_leave.setText(str_leave); 
            }
            
            if(str_absent.equals("0")){
                holder.tv_absent_left.setVisibility(View.GONE);
                holder.tv_absent_right.setVisibility(View.GONE);
                holder.tv_attence_absent.setVisibility(View.GONE);
            }else{
                holder.tv_absent_left.setVisibility(View.VISIBLE);
                holder.tv_absent_right.setVisibility(View.VISIBLE);
                holder.tv_attence_absent.setVisibility(View.VISIBLE);
                holder.tv_attence_absent.setText(str_absent); 
            }
            if(str_late.equals("0")){
                holder.tv_late_left.setVisibility(View.GONE);
                holder.tv_late_right.setVisibility(View.GONE);
                holder.tv_attence_late.setVisibility(View.GONE);
            }else{
                holder.tv_late_left.setVisibility(View.VISIBLE);
                holder.tv_late_right.setVisibility(View.VISIBLE);
                holder.tv_attence_late.setVisibility(View.VISIBLE);
                holder.tv_attence_late.setText(str_late); 
            }
            
            if(str_early.equals("0")){
                holder.tv_early_left.setVisibility(View.GONE);
                holder.tv_early_right.setVisibility(View.GONE);
                holder.tv_attence_early.setVisibility(View.GONE);
            }else{
                holder.tv_early_left.setVisibility(View.VISIBLE);
                holder.tv_early_right.setVisibility(View.VISIBLE);
                holder.tv_attence_early.setVisibility(View.VISIBLE);
                holder.tv_attence_early.setText(str_early); 
            }
            
            if(mMainEarlyList.get(position).getStatus().equals("0")){//还未开始
                if(str_absent.equals("0") && str_late.equals("0") && str_early.equals("0") && str_leave.equals("0")){
                    holder.liner_leave_start_or_end.setVisibility(View.VISIBLE);
                    holder.liner_attence_result.setVisibility(View.GONE);
                    holder.tv_leave_start_or_end.setText("当前为全勤。");
                    
                }else{
                    holder.liner_leave_start_or_end.setVisibility(View.GONE);
                    holder.liner_attence_result.setVisibility(View.VISIBLE);
                }
                holder.iv_attendence_status.setVisibility(View.GONE);
            }else if(mMainEarlyList.get(position).getStatus().equals("1")){//进行中
                if(str_absent.equals("0") && str_late.equals("0") && str_early.equals("0") && str_leave.equals("0")){
                    holder.liner_leave_start_or_end.setVisibility(View.VISIBLE);
                    holder.liner_attence_result.setVisibility(View.GONE);
                    holder.tv_leave_start_or_end.setText("正在进行课堂考勤。");
                }else{
                  holder.liner_leave_start_or_end.setVisibility(View.GONE);
                  holder.liner_attence_result.setVisibility(View.VISIBLE);
                }
                String atd_time = mMainEarlyList.get(position).getAtd_time();
                if(atd_time != null && !atd_time.equals("") && !atd_time.equals("<null>")){
                    holder.iv_attendence_status.setVisibility(View.VISIBLE);
                    holder.iv_attendence_status.setBackgroundResource(R.drawable.icon_attence_checking);
                }else{
                    holder.iv_attendence_status.setVisibility(View.GONE);
                }
            }else if(mMainEarlyList.get(position).getStatus().equals("2")){//已结束
                if(str_absent.equals("0") && str_late.equals("0") && str_early.equals("0") && str_leave.equals("0")){
                    holder.liner_leave_start_or_end.setVisibility(View.VISIBLE);
                    holder.liner_attence_result.setVisibility(View.GONE);
                    holder.tv_leave_start_or_end.setText("全勤");
                }else{
                    holder.liner_leave_start_or_end.setVisibility(View.GONE);
                    holder.liner_attence_result.setVisibility(View.VISIBLE);
                }
                String atd_time = mMainEarlyList.get(position).getAtd_time();
                if(atd_time != null && !atd_time.equals("") && !atd_time.equals("<null>")){
                    holder.iv_attendence_status.setVisibility(View.VISIBLE);
                    holder.iv_attendence_status.setBackgroundResource(R.drawable.icon_attence_checked);
                }else{
                    holder.iv_attendence_status.setVisibility(View.GONE);
                }
            }
            return convertview;
		}

	}
	
	private class ViewHolder {
	    ImageView iv_attdence_picture;
		TextView tv_attendence_title;
		ImageView iv_attendence_status;
		TextView tv_attendece_create_time;
		
		LinearLayout liner_leave_start_or_end,liner_attence_result;
		TextView tv_leave_start_or_end;
		
		TextView tv_attence_leave;
		TextView tv_attence_absent;
		TextView tv_attence_late;
		TextView tv_attence_early;
		
		TextView tv_leave_left;
		TextView tv_leave_right;
		TextView tv_absent_left;
		TextView tv_absent_right;
		TextView tv_late_left;
		TextView tv_late_right;
		TextView tv_early_left;
		TextView tv_early_right;
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
                //A_0_App.getInstance().showExitDialog(B_Side_Attence_Main_A0.this,getResources().getString(R.string.token_timeout));
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
                        A_0_App.getInstance().showExitDialog(B_Side_Attence_Main_A0.this,
                                AppStrStatic.kicked_offline());
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
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;
		case ZUI_RIGHT_BUTTON:
            startActivity(new Intent(B_Side_Attence_Main_A0.this, B_Side_Attence_Main_A1_New_Attence.class));
            break;
		case PIAN_RIGHT_BUTTON:
		    Intent intent=new Intent();
            intent.setClass(B_Side_Attence_Main_A0.this,Pub_WebView_Load_Other_Acy.class);
            intent.putExtra("title_text", getResources().getString(R.string.str_attence_help));
            intent.putExtra("tag_skip", "1");
            intent.putExtra("url_text", AppStrStatic.LINK_USER_ATTDENCE_HELP);
            startActivity(intent);
            break;
		default:
			break;
		}
	}
	
    
    @Override
    protected void onResume() {
        if (!firstLoad_Tag) {
            have_read_page = 1;
            getAttdenceList(have_read_page);
        } else {
            logD(" 第一次进入页面不刷新");
            firstLoad_Tag = false;
        }
        super.onResume();
    }
	
    @Override
    protected void onDestroy() {
        clearToadayList(true);
        clearEarlyList(true);
        drawable.stop();
        drawable=null;
        adapter = null;
        indexAdapter = null;
        super.onDestroy();
    }
    
    
    public static void logD(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logD("B_Side_Attence_Main_A0", "B_Side_Attence_Main_A0==>" + msg);
    }

    public static void logE(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logE("B_Side_Attence_Main_A0", "B_Side_Attence_Main_A0==>" + msg);
    }
    
    

}
