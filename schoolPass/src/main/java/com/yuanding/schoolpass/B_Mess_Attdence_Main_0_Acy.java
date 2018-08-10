package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.view.KeyEvent;
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

import com.alipay.a.a.g;
import com.baidu.location.BDLocation;
import com.yuanding.schoolpass.baidu.BaiduLocation;
import com.yuanding.schoolpass.baidu.BaiduLocation.IGetLocation;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Side_Attence_List;
import com.yuanding.schoolpass.service.Api.InterGetMyAtdList;
import com.yuanding.schoolpass.service.Api.InterSubmitMyLocation;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;


/**
 * @author Jiaohaili 
 * @version 创建时间：2015年12月4日 上午10:46:19 
 * 学生考勤列表页面
 */
public class B_Mess_Attdence_Main_0_Acy extends A_0_CpkBaseTitle_Navi{
    private int[] cate_image = {
            R.drawable.c1, R.drawable.c2,R.drawable.c3, R.drawable.c4,
            R.drawable.c5, R.drawable.c6,R.drawable.c7, R.drawable.c8};
    
    private int have_read_page = 1;// 已经读的页数
    private PullToRefreshListView mPullDownView;
    
    private Myadapter adapter;
    private List<Cpk_Side_Attence_List> toadayList = new ArrayList<Cpk_Side_Attence_List>();
    private View mLinerReadDataError,mLinerNoContent,liner_lecture_list_whole_view,side_attdence_loading;
    private BaiduLocation baiduLocation;// 百度定位
    private SharedPreferences mSharePre;
    private boolean firstLoad_Tag = false;//第一次进入
    private int acy_type = 1;// 页面类型 推送1，正常列表进入2
    
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
        setView(R.layout.activity_mess_attendence_main);
        setTitleText(getResources().getString(R.string.str_attence_title));

        acy_type = getIntent().getExtras().getInt("acy_type");
        if (acy_type == 1) {
            // 推送进入
        } else if (acy_type == 2) {
            // 正常进入
        }
        
        firstLoad_Tag = true;
        demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
        mPullDownView = (PullToRefreshListView) findViewById(R.id.lv_attendence_location);
        mLinerReadDataError=findViewById(R.id.side_attdence_load_error);
        mLinerNoContent=findViewById(R.id.side_attdence_no_content);
        ImageView iv_blank_por = (ImageView)mLinerNoContent.findViewById(R.id.iv_blank_por);
        TextView tv_blank_name = (TextView)mLinerNoContent.findViewById(R.id.tv_blank_name);
        iv_blank_por.setBackgroundResource(R.drawable.ico_no_attendance);
        tv_blank_name.setText("你还没有收到任何课堂考勤信息~");
        
        liner_lecture_list_whole_view=findViewById(R.id.liner_attdence_list_whole_view);
        side_attdence_loading=findViewById(R.id.side_attdence_loading);
        
        home_load_loading = (LinearLayout) side_attdence_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
        
        mLinerReadDataError.setOnClickListener(onClick);
        adapter = new Myadapter();
        mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
        demo_swiperefreshlayout.setEnabled(false);
        mPullDownView.setAdapter(adapter);
        mPullDownView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
//                getAttdenceList(have_read_page);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                  
                  
                  if (repfresh==0) {
						repfresh=1;
						demo_swiperefreshlayout.setEnabled(false);
						demo_swiperefreshlayout.setRefreshing(false);  
						have_read_page = 1;	
						getAttdenceList(have_read_page,true);
//		                getMoreLecture(have_read_page);
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

							//select.setText("0");
//							have_read_page = 1;
							mPullDownView.setMode(Mode.DISABLED);
							//getAttdenceList(have_read_page);
							//getNoticeListData(level, have_read_page, true, false);
							getMoreLecture(have_read_page);

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
        mPullDownView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int posi, long id) {
//                int position_index = posi - 1;
//                goDtail(earlyList, position_index);
            }
        });
        
        if(A_0_App.USER_STATUS.equals("2")){
            getAttdenceList(have_read_page,true);
        }else{
            showLoadResult(false, false, false, true);
        }
        
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
        
    }
    
    private boolean getAttdenceResult(String str_attdence) {
        mSharePre = this.getSharedPreferences(AppStrStatic.SHARE_PREFER_USER_ATTDENCE,
                Activity.MODE_PRIVATE);
        return mSharePre.getBoolean(str_attdence, false);
    }
    
    private void saveAttdentceResult(String str_attdence) {
        // 看完之后才真正的退出
        SharedPreferences mSharePre = this.getSharedPreferences(AppStrStatic.SHARE_PREFER_USER_ATTDENCE, Activity.MODE_PRIVATE);
        Editor editor = mSharePre.edit();
        editor.putBoolean(str_attdence, true);
        editor.commit();
    }
    
//    private void goDtail(List<Cpk_Side_Attence_List> list,int posi) {
//        String attence_atd_id = list.get(posi).getAtd_id();
//        if(attence_atd_id == null || attence_atd_id.equals(""))
//            return;
//        Intent intent=new Intent(B_Mess_Attdence_Main_Acy.this, B_Side_Attence_Main_A3_Detail.class);
//        intent.putExtra(ATTENCE_ATD_ID, attence_atd_id);
//        intent.putExtra(ATTENCE_ACY_TYPE, 1);
//        startActivity(intent);
////        PubMehods.showToastStr(B_Mess_Attdence_Main_Acy.this, list.get(posi).getAtd_id());
//    }
    
    private void goActionData(List<Cpk_Side_Attence_List> mList,boolean goLastPosi) {
        if (isFinishing())
            return;
        if (mList != null && mList.size() > 0) {
            showTitleBt(ZUI_RIGHT_TEXT, true);
            showLoadResult(false, true, false, false);
            have_read_page = 2;
            clearToadayList(false);
            toadayList = mList;
            adapter.notifyDataSetChanged();
            if(goLastPosi)
                mPullDownView.getRefreshableView().setSelection(toadayList.size() + 1);
            setZuiYouText("课堂考勤统计");
        } else {
            showLoadResult(false, false, false, true);
            showTitleBt(ZUI_RIGHT_TEXT, false);
            setZuiYouText("课堂考勤统计");
        }

        if(null!=mPullDownView){
        	mPullDownView.onRefreshComplete();
        }
    }
    
    //获取考勤列表数据
    private void getAttdenceList(final int page_no,final boolean goLastPosi) {
        A_0_App.getApi().getMyAtdList(B_Mess_Attdence_Main_0_Acy.this, A_0_App.USER_TOKEN, String.valueOf(page_no), new InterGetMyAtdList(){
            @Override
            public void onSuccess(List<Cpk_Side_Attence_List> mList,long sysTime) {
                goActionData(mList,goLastPosi);
                
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
                showTitleBt(ZUI_RIGHT_TEXT, true);
                setZuiYouText("课堂考勤统计");
                if (toadayList == null || toadayList.size() == 0)
                    showLoadResult(false, false, true, false);
                PubMehods.showToastStr(B_Mess_Attdence_Main_0_Acy.this, msg);
                
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


    // 上拉刷新初始化数据
    private void getMoreLecture(int page_no) {
        if(A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
            return;
        A_0_App.getApi().getMyAtdList(B_Mess_Attdence_Main_0_Acy.this, A_0_App.USER_TOKEN, String.valueOf(page_no), new InterGetMyAtdList(){
            @Override
            public void onSuccess(List<Cpk_Side_Attence_List> mList,long sysTime) {
                if (isFinishing())
                    return;
                //A_0_App.getInstance().CancelProgreDialog(B_Mess_Attdence_Main_0_Acy.this);
                if (mList != null && mList.size() > 0) {
                    have_read_page += 1;
                    int totleSize = toadayList.size();
                    for (int i = 0; i < mList.size(); i++) {
                        toadayList.add(0,mList.get(i));
                    }
                    adapter.notifyDataSetChanged();
                    
                } else {
                    PubMehods.showToastStr(B_Mess_Attdence_Main_0_Acy.this,"没有更多了");
                }
                
                if(null!=mPullDownView){
                	mPullDownView.onRefreshComplete();
                	mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
                }              
                demo_swiperefreshlayout.setRefreshing(false);  
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
                A_0_App.getInstance().CancelProgreDialog(B_Mess_Attdence_Main_0_Acy.this);
                PubMehods.showToastStr(B_Mess_Attdence_Main_0_Acy.this, msg);
                
                
                if(null!=mPullDownView){
                    mPullDownView.onRefreshComplete();
                    mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
                }                
                demo_swiperefreshlayout.setRefreshing(false);  
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
                getAttdenceList(have_read_page,true);
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
        if(loading){
        	drawable.start();
            side_attdence_loading.setVisibility(View.VISIBLE);
        }else{
        	if (drawable!=null) {
        		drawable.stop();
			}
            side_attdence_loading.setVisibility(View.GONE);
    }}
    
    
    private class Myadapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (toadayList != null)
                return toadayList.size();
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
        public View getView(final int position, View convertview, ViewGroup arg2) {
            // TODO Auto-generated method stub

            final ViewHolder holder;
            if (convertview == null) {
                holder = new ViewHolder();
                convertview = View.inflate(B_Mess_Attdence_Main_0_Acy.this,R.layout.item_mess_attdence_main, null);
                holder.iv_attdence_head = (ImageView)convertview.findViewById(R.id.attdence_head);
                holder.tv_attendence_person = (TextView) convertview.findViewById(R.id.tv_attdence_person);
                holder.tv_attdence_subject = (TextView) convertview.findViewById(R.id.tv_attdence_subject);
                holder.tv_attendence_classroom = (TextView) convertview.findViewById(R.id.tv_attdence_classroom);
                
                holder.tv_attendence_date = (TextView) convertview.findViewById(R.id.tv_attdence_date_time);
                holder.tv_attdence_date_status = (TextView) convertview.findViewById(R.id.tv_attdence_date_status);
                holder.btn_start_attdence = (Button) convertview.findViewById(R.id.btn_start_attdence);
                convertview.setTag(holder);
            } else {
                holder = (ViewHolder) convertview.getTag();
            }
            
            Cpk_Side_Attence_List attence = toadayList.get(position);
            
            if (position % 8 == 0) {
                holder.iv_attdence_head.setBackgroundResource(cate_image[0]);
            } else if (position % 8 == 1) {
                holder.iv_attdence_head.setBackgroundResource(cate_image[1]);
            } else if (position % 8 == 2) {
                holder.iv_attdence_head.setBackgroundResource(cate_image[2]);
            } else if (position % 8 == 3) {
                holder.iv_attdence_head.setBackgroundResource(cate_image[3]);
            } else if (position % 8 == 4) {
                holder.iv_attdence_head.setBackgroundResource(cate_image[4]);
            } else if (position % 8 == 5) {
                holder.iv_attdence_head.setBackgroundResource(cate_image[5]);
            } else if (position % 8 == 6) {
                holder.iv_attdence_head.setBackgroundResource(cate_image[6]);
            } else if (position % 8 == 7) {
                holder.iv_attdence_head.setBackgroundResource(cate_image[7]);
            }
//     + ":" holder.iv_attdence_head.setBackgroundResource(cate_image[PubMehods.getRandomIntData(cate_image.length)]);
            holder.tv_attendence_person.setText(attence.getTrue_name());
            holder.tv_attdence_subject.setText(attence.getTitle());
            holder.tv_attendence_classroom.setText(attence.getPlace());
            if (attence.getAtd_time() != null && !attence.getAtd_time().equals("")) {
                long time_conver = Long.valueOf(attence.getAtd_time());
                holder.tv_attendence_date.setText(PubMehods.getFormatDate(time_conver,"MM/dd  HH:mm"));
            }else{
                holder.tv_attendence_date.setText("");
            }
            
            if (attence.getAtd_status().equals("1")) {//正常进行中
                if (attence.getMeter().equals("")) {//是否点击签到
                   if(attence.getStatus().equals("2")){
                       holder.btn_start_attdence.setEnabled(true);
                       holder.btn_start_attdence.setBackgroundResource(R.drawable.btn_attdence_start_normal);
                       holder.btn_start_attdence.setText("点击考勤");
                       holder.tv_attdence_date_status.setTextColor(getResources().getColor(R.color.attence_normal));
                       holder.tv_attendence_date.setTextColor(getResources().getColor(R.color.attence_normal));
                   }else{
                       holder.btn_start_attdence.setEnabled(false);
                       holder.btn_start_attdence.setBackgroundResource(R.drawable.btn_attdence_start_error);
                       holder.tv_attendence_date.setTextColor(getResources().getColor(R.color.start_title_col));
                       holder.tv_attdence_date_status.setTextColor(getResources().getColor(R.color.start_title_col));
                       
                       switch (Integer.valueOf(attence.getStatus())) {
                           case 1:
                               holder.btn_start_attdence.setText(R.string.str_attence_pass_title);
                               break;
                           case 2:
                               holder.btn_start_attdence.setText("被标记为   缺勤");
                               break;
                           case 3:
                               holder.btn_start_attdence.setText("被标记为   迟到");
                               break;
                           case 4:
                               holder.btn_start_attdence.setText("被标记为   早退");
                               break;
                           case 5:
                               holder.btn_start_attdence.setText("被标记为   请假");
                               break;
                           default:
                               break;
                       }
                   }
                } else {
                    holder.btn_start_attdence.setEnabled(false);
                    holder.btn_start_attdence.setBackgroundResource(R.drawable.btn_attdence_start_error);
                    holder.tv_attendence_date.setTextColor(getResources().getColor(R.color.start_title_col));
                    holder.tv_attdence_date_status.setTextColor(getResources().getColor(R.color.start_title_col));
                    switch (Integer.valueOf(attence.getStatus())) {
                        case 1:
                            holder.btn_start_attdence.setText(R.string.str_attence_pass_title);
                            break;
                        case 2:
                            holder.btn_start_attdence.setText("被标记为   缺勤");
                            break;
                        case 3:
                            holder.btn_start_attdence.setText("被标记为   迟到");
                            break;
                        case 4:
                            holder.btn_start_attdence.setText("被标记为   早退");
                            break;
                        case 5:
                            holder.btn_start_attdence.setText("被标记为   请假");
                            break;
                        default:
                            break;
                    }
                }
                holder.tv_attdence_date_status.setText("截止");
            } else {
                switch (Integer.valueOf(attence.getStatus())) {
                    case 1:
                        holder.btn_start_attdence.setText(R.string.str_attence_pass_title);
                        break;
                    case 2:
                        holder.btn_start_attdence.setText("被标记为   缺勤");
                        break;
                    case 3:
                        holder.btn_start_attdence.setText("被标记为   迟到");
                        break;
                    case 4:
                        holder.btn_start_attdence.setText("被标记为   早退");
                        break;
                    case 5:
                        holder.btn_start_attdence.setText("被标记为   请假");
                        break;
                    default:
                        break;
                }
                holder.tv_attdence_date_status.setText("已截止");
                holder.btn_start_attdence.setEnabled(false);
                holder.btn_start_attdence.setBackgroundResource(R.drawable.btn_attdence_start_error);
                holder.tv_attendence_date.setTextColor(getResources().getColor(R.color.start_title_col));
                holder.tv_attdence_date_status.setTextColor(getResources().getColor(R.color.start_title_col));
            }

            holder.btn_start_attdence.setTag(toadayList.get(position).getAtd_id());
            if (holder.btn_start_attdence.getText().equals("点击考勤")){
                holder.btn_start_attdence.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        atd_id = String.valueOf(v.getTag());
                        if (A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {
                            if(PubMehods.isFastClick(AppStrStatic.INTERVAL_ATTDENCE_TIME)){
                                if(atd_id.equals(tag_Attdence)){
                                    logD("同一个多次签到");
                                    return;
                                }
                            }
                        } else {
                            PubMehods.showToastStr(B_Mess_Attdence_Main_0_Acy.this,R.string.error_title_net_error);
                            return;
                        }
                        if (getAttdenceResult(atd_id)) {
                            PubMehods.showToastStr(B_Mess_Attdence_Main_0_Acy.this, "请不要切换账户重复签到");
                            return;
                        }
                        tag_Attdence = atd_id;
                        i = position;
                        A_0_App.getInstance().showProgreDialog(B_Mess_Attdence_Main_0_Acy.this, "", true);
                        PermissionGen.needPermission(B_Mess_Attdence_Main_0_Acy.this, REQUECT_CODE_ACCESS_FINE_LOCATION,
                                new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                });

                    }
                });
            }else {
                holder.btn_start_attdence.setEnabled(false);
            }

            return convertview;
        }



    }
    String atd_id;
    int i;
    private static final int REQUECT_CODE_ACCESS_FINE_LOCATION = 3;
    @PermissionSuccess(requestCode = REQUECT_CODE_ACCESS_FINE_LOCATION)
    public void requestDingWeiSuccess() {
        dingWei();
    }

    private void dingWei() {
        baiduLocation = new BaiduLocation(B_Mess_Attdence_Main_0_Acy.this);
        baiduLocation.startLocation(true); // 10s的超时定位
        baiduLocation.setCallBack(new IGetLocation() {
            @Override
            public void successful(BDLocation loca) {
                if(isFinishing())
                    return;
                AddAttence(atd_id, String.valueOf(loca.getLatitude()),  String.valueOf(loca.getLongitude()));
                baiduLocation.stopLocation(true);
                baiduLocation = null;
            }

            @Override
            public void overTimeDo() {
                if(isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Mess_Attdence_Main_0_Acy.this);
                adapter.notifyDataSetChanged();
                baiduLocation.stopLocation(true);
                PubMehods.showToastStr(B_Mess_Attdence_Main_0_Acy.this,R.string.str_attence_overtime);
                baiduLocation = null;
            }

            @Override
            public void failure(String msg) {
                if(isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Mess_Attdence_Main_0_Acy.this);
                baiduLocation.stopLocation(true);
                PubMehods.showToastStr(B_Mess_Attdence_Main_0_Acy.this, msg);
                baiduLocation = null;
            }
        });
    }

    @PermissionFail(requestCode = REQUECT_CODE_ACCESS_FINE_LOCATION)
    public void requestDingWeiFailed() {
        A_0_App.getInstance().CancelProgreDialog(
                B_Mess_Attdence_Main_0_Acy.this);
        A_0_App.getInstance().PermissionToas("位置", B_Mess_Attdence_Main_0_Acy.this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
    private String tag_Attdence = "";
    private class ViewHolder {
        ImageView iv_attdence_head;
        TextView tv_attendence_person;
        TextView tv_attdence_subject;
        TextView tv_attendence_classroom;
        
        TextView tv_attendence_date;
        TextView tv_attdence_date_status;
        Button btn_start_attdence;
    }
    
    //开始考勤
    private void AddAttence(final String atd_id, String lat,String lng) {
        A_0_App.getApi().submitMyLocation(A_0_App.USER_TOKEN, atd_id, lat, lng, new InterSubmitMyLocation() {
            @Override
            public void onSuccess() {
                if (isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Mess_Attdence_Main_0_Acy.this);
                have_read_page = 1;
                saveAttdentceResult(atd_id);
                getAttdenceList(have_read_page,false);
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
                A_0_App.getInstance().CancelProgreDialog(B_Mess_Attdence_Main_0_Acy.this);
                PubMehods.showToastStr(B_Mess_Attdence_Main_0_Acy.this, msg);
                
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
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
                //A_0_App.getInstance().showExitDialog(B_Mess_Attdence_Main_0_Acy.this,getResources().getString(R.string.token_timeout));
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
                        A_0_App.getInstance().showExitDialog(B_Mess_Attdence_Main_0_Acy.this,
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
        if (toadayList != null) {
            toadayList.clear();
            if (setNull)
                toadayList = null;
        }
    }

    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                goAcy();
                break;
            case ZUI_RIGHT_TEXT:
                startActivity(new Intent(B_Mess_Attdence_Main_0_Acy.this, B_Mess_Attdence_Main_1_Detail.class));
                break;
            default:
                break;
        }
    }
    
    private void goAcy() {
        if (acy_type == 1) {// 推送
            if (A_Main_Acy.getInstance() != null) {
                finish();
            } else {
                startAcy(B_Mess_Attdence_Main_0_Acy.this, A_Main_Acy.class);
            }
        } else {// 正常进入
            finish();
        }
    }
    
    private void startAcy(Context packageContext, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(packageContext, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.animal_push_left_in_normal,R.anim.animal_push_left_out_normal);
        finish();
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    goAcy();
                    break;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
    
    @Override
    protected void onResume() {
        if (!firstLoad_Tag) {
            have_read_page = 1;
            getAttdenceList(have_read_page,true);
        } else {
            logD(" 第一次进入页面不刷新");
            firstLoad_Tag = false;
        }
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearToadayList(true);
        adapter = null;
        drawable.stop();
        drawable=null;
        if (baiduLocation != null) {
            baiduLocation.appOnDestroy();
        }
    }
    
    
    public static void logD(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logD("B_Mess_Attdence_Main_Acy", "B_Mess_Attdence_Main_Acy==>" + msg);
    }

    public static void logE(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logE("B_Mess_Attdence_Main_Acy", "B_Mess_Attdence_Main_Acy==>" + msg);
    }
}
