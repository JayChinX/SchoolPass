package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Acy_Detail;
import com.yuanding.schoolpass.bean.Cpk_Comment_detail;
import com.yuanding.schoolpass.bean.Cpk_Info_Detail_Comment_List_All;
import com.yuanding.schoolpass.bean.Cpk_Info_ReportType;
import com.yuanding.schoolpass.service.Api.InterAcyDetail;
import com.yuanding.schoolpass.service.Api.InterAcy_Apply;
import com.yuanding.schoolpass.service.Api.InterAdd_Acy_Comment;
import com.yuanding.schoolpass.service.Api.InterCommentList;
import com.yuanding.schoolpass.service.Api.InterInfo_Ac_Comment;
import com.yuanding.schoolpass.service.Api.InterInfo_Type;
import com.yuanding.schoolpass.service.Api.InterInfo_addReport;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubForwardOrShare;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.GeneralDialog;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月24日 下午2:47:12
 * 活动详情
 */
public class B_Side_Acy_list_Detail_Acy extends A_0_CpkBaseTitle_Navi {

	private View side_acy_detail_load_error,liner_acy_detail_whole,side_acy_detail_loading;
	private ImageView iv_acy_detail_banner,iv_acy_comment_send;
	private LinearLayout liner_acy_comment_send;
	private TextView tv_acy_detail_title,tv_acy_detail_time,tv_detail_comment_count,tv_acy_detail_join_count,tv_acy_detail_join_name,tv_act_end_time;
	private TextView tv_act_detail_time,tv_act_detail_place,tv_acy_deail_down_comment_num;
	/**
	 * 要修改隐藏或显示
	 */
	private Button btn_sign_up_me;

	private WebView wv_acy_detail;
	private PullToRefreshListView lv_act_detail_comment;
	private EditText tv_acy_detail_comment_content;
	
	private Mydapter adapter;
	private String acy_detail_id,join_acy_state,acy_going_state,comment_id="";
	
	private List<Cpk_Comment_detail> list;
	private Cpk_Acy_Detail detail_Notice;
	
	protected ImageLoader imageLoader,imageLoaderBanner;
	private DisplayImageOptions options,optionsBanner;
	//private BitmapUtils bitmapUtils,bitmapUtilsBanner;
	//private SolveClashListView solveClashListView;
	
	private int have_read_page = 2;// 已经读的页数
	private int acy_type;//页面类型    推送3，正常列表进入2,转发进入为1
	private boolean readCommentData =false;
	private ACache maACache;
	private JSONObject jsonObject;
	private boolean havaSuccessLoadData = false;
	
	private int temp_like=0;//防止重复点击请求
	private int add_num=0;//手动增减数量
	private static long severTime=0;
	private Animation animation;
	private String praise_new="-1";
	private String praise_temp="";
	private Report_Adapter report_Adapter;
	private List<Cpk_Info_ReportType> reportTypes;
	private LinearLayout liner_acy_detail;
	
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
		setView(R.layout.activity_side_acy_detail);
		
		
		setTitleText("活动详情");
		acy_type =  getIntent().getExtras().getInt("acy_type");
		if(acy_type == 2){
			//列表进入,转发进入 
			acy_detail_id = getIntent().getExtras().getString("acy_detail_id");
			share_url_text = getIntent().getExtras().getString("share_url_text","");
			share_url_title = getIntent().getExtras().getString("share_url_title","");
			share_url_time = getIntent().getExtras().getString("share_url_time","");
			share_url_pic = getIntent().getExtras().getString("share_url_pic","");
		}else if(acy_type == 3){
			//推送进入3
			acy_detail_id = PubMehods.getSharePreferStr(this, "mCurrentClickNotificationMsgId");
		}else if(acy_type == 1){
			acy_detail_id = getIntent().getExtras().getString("acy_detail_id");
		}
		
		
		animation=AnimationUtils.loadAnimation(B_Side_Acy_list_Detail_Acy.this,R.anim.side_info);
		detail_Notice = new Cpk_Acy_Detail();
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		side_acy_detail_load_error = findViewById(R.id.side_acy_detail_load_error);
		liner_acy_detail_whole = findViewById(R.id.liner_acy_detail_whole);
		side_acy_detail_loading = findViewById(R.id.side_acy_detail_loading);
		
		home_load_loading = (LinearLayout) side_acy_detail_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		lv_act_detail_comment = (PullToRefreshListView)findViewById(R.id.lv_act_detail_comment);
		tv_acy_detail_comment_content = (EditText)findViewById(R.id.tv_acy_detail_comment_content);
		iv_acy_comment_send = (ImageView)findViewById(R.id.iv_acy_comment_send);
		liner_acy_comment_send = (LinearLayout)findViewById(R.id.liner_acy_comment_send);
		
		imageLoaderBanner = A_0_App.getInstance().getimageLoader();
		optionsBanner = A_0_App.getInstance().getOptions(R.drawable.ic_default_banner_empty_bg, R.drawable.ic_default_banner_empty_bg, 
				R.drawable.ic_default_banner_empty_bg);
		
		imageLoader =  A_0_App.getInstance().getimageLoader();
		options = A_0_App.getInstance().getOptions(R.drawable.ic_defalut_person_center, R.drawable.ic_defalut_person_center, R.drawable.ic_defalut_person_center);
//		bitmapUtilsBanner=A_0_App.getBitmapUtils(this, R.drawable.ic_default_banner_empty, R.drawable.ic_default_banner_empty);
//		bitmapUtils=A_0_App.getBitmapUtils(this, R.drawable.ic_defalut_person_center, R.drawable.ic_defalut_person_center);
		
		reportTypes=new ArrayList<Cpk_Info_ReportType>();
		list = new ArrayList<Cpk_Comment_detail>();
		adapter = new Mydapter();
		addHeadView(lv_act_detail_comment);
		lv_act_detail_comment.setAdapter(adapter);
		
		lv_act_detail_comment.setMode(Mode.PULL_UP_TO_REFRESH);
		lv_act_detail_comment.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {


			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				
				String label = DateUtils.formatDateTime(
						getApplicationContext(),
						System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy()
						.setLastUpdatedLabel(label);
				readData(acy_detail_id);
				
			
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (repfresh==0) {
					repfresh=1;
					demo_swiperefreshlayout.setEnabled(false);
					demo_swiperefreshlayout.setRefreshing(false);  
							
								getMoreComment(acy_detail_id, have_read_page);
						
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
			if (lv_act_detail_comment!=null) {
				lv_act_detail_comment.onRefreshComplete();
			}
			demo_swiperefreshlayout
					.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
						public void onRefresh() {
							have_read_page = 1;
							lv_act_detail_comment.setMode(Mode.DISABLED);
							readData(acy_detail_id);

						};
					});
		}
		
		lv_act_detail_comment.setOnScrollListener(new OnScrollListener() {
				
				@Override
				public void onScrollStateChanged(AbsListView arg0, int arg1) {
					 if (demo_swiperefreshlayout!=null&&lv_act_detail_comment.getChildCount() > 0 && lv_act_detail_comment.getRefreshableView().getFirstVisiblePosition() == 0
				                && lv_act_detail_comment.getChildAt(0).getTop() >= lv_act_detail_comment.getPaddingTop()) {
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
		
		//报名成功
		btn_sign_up_me.setOnClickListener(new OnClickListener() {
			@Override
            public void onClick(View arg0) {
                if (!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)) {
//                    if (acy_going_state.equals("1")) {
//                        if (join_acy_state.equals("0")) {
//                            PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, "活动已经截止报名")
//                        } else {
//                            PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, "你已参加了该活动");
//                        }
//                    } else {
//                        if (join_acy_state.equals("0")) {
                            signUp(acy_detail_id);
//                        } else {
//                            PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, "你已参加了该活动");
//                        }
//                    }
//                } else {
//                    PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, "请勿重复操作！");
                }
            }
		});
		
		side_acy_detail_load_error.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showLoadResult(true,false, false);
				readData(acy_detail_id);
			}
		});
		
		tv_acy_detail_comment_content.setBackgroundResource(R.drawable.bg_edit_source_normal);
		tv_acy_detail_comment_content.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if(arg1){
					tv_acy_detail_comment_content.setBackgroundResource(R.drawable.bg_edit_source_focus);
				}else{
					tv_acy_detail_comment_content.setTextColor(getResources().getColor(R.color.title_no_focus_login));
				}
			}
		});
		tv_acy_detail_comment_content.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !"".equals(s.toString()))
                {
                    iv_acy_comment_send.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_comment_send));
                    if (AppStrStatic.WORD_COMMENT_MAX_LIMIT < s.length()) {
                        PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, "已达到最大字数限制");
                        tv_acy_detail_comment_content.setText(tv_acy_detail_comment_content.getText().toString()
                                .substring(0, AppStrStatic.WORD_COMMENT_MAX_LIMIT));
                        tv_acy_detail_comment_content
                                .setSelection(AppStrStatic.WORD_COMMENT_MAX_LIMIT);
                    } else {
                        // sent_notice_tv.setText("还可以输入"+(content_limit-arg0.length())+"字");
                    }
                } else
                {
                    iv_acy_comment_send.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.ic_comment_send_enable));
                }
            }
		});
		
		// 评论
		liner_acy_comment_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String str = tv_acy_detail_comment_content.getText().toString();
				if(A_0_App.USER_STATUS.equals("2")){
    				if (str != null && !(str.equals("")) && str.length() >= AppStrStatic.WORD_COMMENT_MIN_LIMIT) {
    				    if(!judgeNoNullStr(str)){
    				        PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this,"请输入3个字以上的评论内容");
    				        return;
    				    }
    					if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_RONGYUN_CONNECT)) {
    						commentAcy(comment_id,acy_detail_id, str);
    					} else {
    						PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this,
    								"您的评论过于频繁！");
    					}
    					
    				} else {
    					PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this,
    							"请输入3个字以上的评论内容");
    				}
    				
				}else{
                    PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this,R.string.str_no_certified_not_comment);
                }
			}
		});
		
		lv_act_detail_comment.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Log.e("test", list.size() + "");
				
			}
		});
        if (A_0_App.USER_STATUS.equals("2")) {
            btn_sign_up_me.setVisibility(View.VISIBLE);
        } else {
            btn_sign_up_me.setVisibility(View.GONE);
        }
        tv_acy_detail_join_count.setVisibility(View.GONE);
        tv_acy_detail_join_name.setVisibility(View.GONE);
        
		readCache(acy_detail_id);
		//maACache = ACache.get(this);
		//jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_acy_detail+A_0_App.USER_UNIQID+acy_detail_id);
		//updateInfo();
		
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}
	
    /*
     * 不能为空字符串
     */
    private boolean judgeNoNullStr(String str_Str) {
        String str = str_Str.replaceAll(" ", "");
        if (!"".equals(str)) {
            return true;
        } else {
            return false;
        }
    }
	
    private void readCache(String acy_detail_id) {
        maACache = ACache.get(this);
        jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_acy_detail + A_0_App.USER_UNIQID + acy_detail_id);
        if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        }else{
            updateInfo();
        }
    }


    private void showInfo(JSONObject jsonObject) {
        Cpk_Acy_Detail notice = getNotice(jsonObject);
        if (notice.getContent().equals("") || notice.getContent() == null) {
            showLoadResult(false, false, true);
            PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, "信息不存在！");
        } else {
          
                String isJoin = jsonObject.optString("isJoin");
                String isStop = jsonObject.optString("isStop");
                Success(notice, isJoin, isStop);
            
        }
    }


	private Cpk_Acy_Detail getNotice(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		
			int state = jsonObject.optInt("status");
			
			Cpk_Acy_Detail notice = new Cpk_Acy_Detail();
			if (state == 1) {
				JSONObject dd = jsonObject.optJSONObject("info");

				notice=JSON.parseObject(dd+"", Cpk_Acy_Detail.class);
				List<Cpk_Comment_detail> mlistContact= new ArrayList<Cpk_Comment_detail>();
				if (jsonObject.optJSONArray("comment")!=null&&!jsonObject.optJSONArray("comment").equals("")) {
					mlistContact=JSON.parseArray(jsonObject.optJSONArray("comment")+"", Cpk_Comment_detail.class);
				}
				
				notice.setList(mlistContact);
			}
			return notice;
		
	}
	
	@SuppressLint("JavascriptInterface") 
	public void Success(Cpk_Acy_Detail notice,String isJoin,String isStop) {
		if(isFinishing())
			return;
		havaSuccessLoadData = true;   
		have_read_page = 2;
		list.clear();
		detail_Notice = notice;
		join_acy_state = isJoin;//是否报名，0：未报名，1：已报名
		acy_going_state = isStop;//活动是否截止，0：未截止，1：已截止，2：未开始
		if (A_0_App.USER_STATUS.equals("2")) {//前提是认证用户
            if (notice.getIs_enroll() != null && !notice.getIs_enroll().equals("")) {
		        if (notice.getIs_enroll().equals("1")) {//是否支持报名功能：是否报名：0-不可报名,1-可报名
                    if (acy_going_state.equals("0")) {//活动进行中
                        if (join_acy_state.equals("1")) {
                            btn_sign_up_me.setBackgroundResource(R.drawable.side_repair_detail_btn_disable_bg);
                            btn_sign_up_me.setText("已报名");
                            btn_sign_up_me.setEnabled(false);
                        } else if (join_acy_state.equals("0")){
                            if (notice.getJoin_end_time() > 0 && notice.getEnd_time() > 0) {
                                if (notice.getJoin_end_time() <= notice.getEnd_time()) {// 报名截止日期小于或者等于活动结束日期
                                    if (severTime/1000 > notice.getJoin_end_time()) {
                                        btn_sign_up_me.setBackgroundResource(R.drawable.side_repair_detail_btn_disable_bg);
                                        btn_sign_up_me.setText("报名已截止");
                                        btn_sign_up_me.setEnabled(false);
                                    }else{
                                        btn_sign_up_me.setBackgroundResource(R.drawable.navigationbar_text_button);
                                        btn_sign_up_me.setText("我要报名");
                                        btn_sign_up_me.setEnabled(true);
                                    }
                                } else {
                                    btn_sign_up_me.setBackgroundResource(R.drawable.side_repair_detail_btn_disable_bg);
                                    btn_sign_up_me.setText("报名已截止");
                                    btn_sign_up_me.setEnabled(false);
                                }
                            }else{
                                btn_sign_up_me.setBackgroundResource(R.drawable.side_repair_detail_btn_disable_bg);
                                btn_sign_up_me.setText("报名已截止");
                                btn_sign_up_me.setEnabled(false);
                            }
                        }
                    } else if (acy_going_state.equals("1")) {//活动已停止
                        if (join_acy_state.equals("1")) {
                            btn_sign_up_me.setBackgroundResource(R.drawable.side_repair_detail_btn_disable_bg);
                            btn_sign_up_me.setText("已报名");
                            btn_sign_up_me.setEnabled(false);
                        } else if (join_acy_state.equals("0")){
                            btn_sign_up_me.setBackgroundResource(R.drawable.side_repair_detail_btn_disable_bg);
                            btn_sign_up_me.setText("报名已截止");
                            btn_sign_up_me.setEnabled(false);
                        }
                    } else if (acy_going_state.equals("2")){//活动未开始
                        if (join_acy_state.equals("1")) {
                            btn_sign_up_me.setBackgroundResource(R.drawable.side_repair_detail_btn_disable_bg);
                            btn_sign_up_me.setText("已报名");
                            btn_sign_up_me.setEnabled(false);
                        } else if (join_acy_state.equals("0")){
                            btn_sign_up_me.setBackgroundResource(R.drawable.navigationbar_text_button);
                            btn_sign_up_me.setText("我要报名");
                            btn_sign_up_me.setEnabled(true);
                        }
                    }
                    tv_acy_detail_join_count.setVisibility(View.VISIBLE);
                    tv_acy_detail_join_name.setVisibility(View.VISIBLE);
                } else {
                    tv_acy_detail_join_count.setVisibility(View.GONE);
                    tv_acy_detail_join_name.setVisibility(View.GONE);
                    btn_sign_up_me.setVisibility(View.GONE);
                }
    		}else{
    		    tv_acy_detail_join_count.setVisibility(View.GONE);
                tv_acy_detail_join_name.setVisibility(View.GONE);
                btn_sign_up_me.setVisibility(View.GONE);
    		}
		}else{
		    tv_acy_detail_join_count.setVisibility(View.GONE);
            tv_acy_detail_join_name.setVisibility(View.GONE);
            btn_sign_up_me.setVisibility(View.GONE);
		}
		String img_url = notice.getBg_img();
		if(img_url != null && img_url.length()>0 && !img_url.equals("")){
			iv_acy_detail_banner.setVisibility(View.VISIBLE);
			if(iv_acy_detail_banner.getTag() == null){
			    PubMehods.loadServicePic(imageLoader,img_url,iv_acy_detail_banner, optionsBanner);
			    iv_acy_detail_banner.setTag(img_url);
			}else{
			    if(!iv_acy_detail_banner.getTag().equals(img_url)){
			        PubMehods.loadServicePic(imageLoader,img_url,iv_acy_detail_banner, optionsBanner);
			        iv_acy_detail_banner.setTag(img_url);
			    }
			}
		    //bitmapUtilsBanner.display(iv_acy_detail_banner, img_url);
		}else{
			iv_acy_detail_banner.setVisibility(View.GONE);
		}
		
		if (acy_going_state.equals("1")) {
			ForegroundColorSpan greenSpan = new ForegroundColorSpan(getResources().getColor(R.color.repair_grey)); 
			SpannableStringBuilder builder = new SpannableStringBuilder("[已结束]"+detail_Notice.getTitle());
			builder.setSpan(greenSpan, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			tv_acy_detail_title.setText(builder);  
		}else if(acy_going_state.equals("0")){
			ForegroundColorSpan greenSpan = new ForegroundColorSpan(getResources().getColor(R.color.GREENlIGHT)); 
			SpannableStringBuilder builder = new SpannableStringBuilder("[进行中]"+detail_Notice.getTitle());
			builder.setSpan(greenSpan, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			tv_acy_detail_title.setText(builder);  
		}else if(acy_going_state.equals("2")){
			ForegroundColorSpan greenSpan = new ForegroundColorSpan(getResources().getColor(R.color.repair_blue)); 
			SpannableStringBuilder builder = new SpannableStringBuilder("[未开始]"+detail_Notice.getTitle());
			builder.setSpan(greenSpan, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			tv_acy_detail_title.setText(builder);  
		}
		tv_act_end_time.setText(PubMehods.getFormatDate(notice.getJoin_end_time(), "MM/dd HH:mm"));
		tv_acy_detail_time.setText(PubMehods.getFormatDate(notice.getCreate_time(), "yyyy/MM/dd HH:mm:ss"));
		tv_acy_detail_join_count.setText(notice.getJoin_num());
		tv_detail_comment_count.setText(notice.getComment_num());
		tv_act_detail_time.setText(notice.getActivity_time());
		tv_act_detail_place.setText(notice.getPlace());
		//wv_acy_detail.loadDataWithBaseURL("",notice.getContent(), "text/html","UTF-8", null);
		 /**
         * 本地读取html
         */
        PubMehods.saveHtml(notice.getContent(), "TEMP_HTML");
        String html_path="file://"+Environment.getExternalStorageDirectory()+"/TEMP_HTML.html";
        wv_acy_detail.loadUrl(html_path);
        wv_acy_detail.addJavascriptInterface(new JavascriptInterface(B_Side_Acy_list_Detail_Acy.this), "imagelistner");
        wv_acy_detail.setWebViewClient(new MyWebViewClient());
		
		
		
		tv_acy_deail_down_comment_num.setText("评论（" + notice.getComment_num() +"）");
		
		list = notice.getList();
		if (list.size()==0) {
			Cpk_Comment_detail all=new Cpk_Comment_detail();
			all.setArticle_id("yuanding");
			list.add(all);
		}
		adapter.notifyDataSetChanged();
		//solveClashListView = new SolveClashListView(); 
		//solveClashListView.setListViewHeightBasedOnChildren(lv_act_detail_comment,1);
		showLoadResult(false,true, false);
		
        if(A_0_App.USER_STATUS.equals("2") && acy_type == 2){
            showTitleBt(ZUI_RIGHT_BUTTON, true);
            setZuiRightBtn(R.drawable.navigationbar_more_share);
        }else{
            showTitleBt(ZUI_RIGHT_BUTTON, false);
        }
	}
	

	 
	 // 注入js函数监听
	  	private void addImageClickListner() {
	  		// 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，在还是执行的时候调用本地接口传递url过去
	  		wv_acy_detail.loadUrl("javascript:(function(){" +
	  		"var objs = document.getElementsByTagName(\"img\"); " + 
	  				"for(var i=0;i<objs.length;i++)  " + 
	  		"{"+
	  				"objs[i].onclick=function()" +
	  		         "{ "+ 
	  		           "window.imagelistner.openImage(this.src);  " + 
	  		         "}" + 
	  		"}" + 
	  		"})()");
	  	}
	  	

	  	
	  	/**
	  	 * 获取当前页面所有img地址为list
	  	 * @author Administrator
	  	 *
	  	 */
	      private void getWebViewAllImgSrc() {
	    	  wv_acy_detail.loadUrl("javascript:(function(){" +
	      			"var objs = document.getElementsByTagName(\"img\"); " + 
	      			"var imgScr = '';"+
	      			   "for(var i=0;i<objs.length;i++){"+
	      			       "imgScr =imgScr +  '\\n'+ objs[i].src;"+
	      		       " };"+
	      		     "window.imagelistner.getAllImg(imgScr);  " + 
//	      			"return imgScr;"+
	      			"})()");

	  	}
	  	
	  	// js通信接口
	  	public class JavascriptInterface {
	  	
	 			
	 		private Context context;
	  		final ArrayList<String> image_List = new ArrayList<String>();
	  		int temp=0;
	  		public JavascriptInterface(Context context) {
	  			this.context = context;
	  			
	  		}

	  		public void openImage(String img) {
	  			
	  			for (int i = 0; i < image_List.size(); i++) {
	 				if (image_List.get(i).equals(img)) {
	 					temp=i;
	 				}
	 			}
	  			
	  			Intent intent = new Intent();
	  			intent.putStringArrayListExtra("path", image_List);
	 			intent.putExtra("num", temp);
	  			intent.setClass(context, B_Side_Found_BigImage.class);
	  			context.startActivity(intent);
	  			
	  		}
	  		
	  		public void getAllImg(String img) {
	  			image_List.clear();
	  			String [] temp = null;  
	  			temp = img.split("\n"); 
	  			//以换行符为url分隔符 第一个元素的null
	  			int counts = temp.length-1;
	  			for(int i=0;i<temp.length;i++){
	  				//以换行符为url分隔符 第一个元素的null
	  				if(i==0){continue;}
	  				image_List.add(temp[i]);
	  			}
	  		}
	  	}

	  	// 监听
	  	private class MyWebViewClient extends WebViewClient {
	  		@Override
	  		public boolean shouldOverrideUrlLoading(WebView view, String url) {

	  			if(url.contains("//img")){
	  				
	  				return true;
	  			}else {
	  				
	  				return super.shouldOverrideUrlLoading(view, url);
	  			}
	  			
	  		}

	  		@Override
	  		public void onPageFinished(WebView view, String url) {

	  			view.getSettings().setJavaScriptEnabled(true);

	  			super.onPageFinished(view, url);
	  			// html加载完成之后，添加监听图片的点击js函数
	  			addImageClickListner();
	  			getWebViewAllImgSrc();

	  		}

	  		@Override
	  		public void onPageStarted(WebView view, String url, Bitmap favicon) {
	  			view.getSettings().setJavaScriptEnabled(true);

	  			super.onPageStarted(view, url, favicon);
	  		}

	  		@Override
	  		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

	  			super.onReceivedError(view, errorCode, description, failingUrl);

	  		}
	  	}
	
	
	
    private void addHeadView(PullToRefreshListView listview) {
    	ListView listView2=listview.getRefreshableView();
        View view = LayoutInflater.from(B_Side_Acy_list_Detail_Acy.this).inflate(R.layout.activity_side_acy_head, null);
        
		iv_acy_detail_banner = (ImageView)view.findViewById(R.id.iv_acy_detail_banner);
		tv_acy_detail_title =(TextView)view.findViewById(R.id.tv_acy_detail_title);
		tv_acy_detail_time =(TextView)view.findViewById(R.id.tv_acy_detail_time);
		tv_act_end_time =(TextView)view.findViewById(R.id.tv_act_end_time);
		tv_detail_comment_count =(TextView)view.findViewById(R.id.tv_detail_comment_count);
		tv_acy_detail_join_count =(TextView)view.findViewById(R.id.tv_acy_detail_join_count);
		tv_acy_detail_join_name =(TextView)view.findViewById(R.id.tv_acy_detail_join_name);
		
		tv_act_detail_time =(TextView)view.findViewById(R.id.tv_act_detail_time);
		tv_act_detail_place =(TextView)view.findViewById(R.id.tv_act_detail_place);
		tv_acy_deail_down_comment_num =(TextView)view.findViewById(R.id.tv_acy_deail_down_comment_num);
		
		btn_sign_up_me = (Button)view.findViewById(R.id.btn_sign_up_me);
		liner_acy_detail = (LinearLayout)view.findViewById(R.id.liner_acy_detail);
		
		wv_acy_detail = new WebView(getApplicationContext());
		wv_acy_detail.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		liner_acy_detail.addView(wv_acy_detail);
		wv_acy_detail.setHorizontalScrollBarEnabled(false);//水平不显示  
		wv_acy_detail.setVerticalScrollBarEnabled(false); //垂直不显示  
		wv_acy_detail.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		wv_acy_detail.setBackgroundColor(getResources().getColor(R.color.col_account_bg));
		wv_acy_detail.getSettings().setJavaScriptEnabled(true);
		wv_acy_detail.getSettings().setDefaultTextEncodingName("utf-8"); //设置文本编码
		wv_acy_detail.setWebViewClient(new WebViewClient() { 
	            public boolean shouldOverrideUrlLoading(WebView view, String url) 
	              { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边 
	            	//view.loadUrl(url); 
	                return true; 
	             }
	        });
		listView2.addHeaderView(view);
        listView2.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				comment_id="";
				tv_acy_detail_comment_content.setHint("写评论");
				PubMehods.closeKeybord(tv_acy_detail_comment_content, B_Side_Acy_list_Detail_Acy.this);
				return false;
			}
		});
    }

	private PopupWindow statusPopup;
	private LinearLayout mLinerCollct,mLinerForward;

//	private void showWindow(View parent) {
//		if (statusPopup == null) {
//			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			View view = layoutInflater.inflate(R.layout.item_lecture_detail, null);
//			mLinerCollct = (LinearLayout) view.findViewById(R.id.liner_lecture_detail_collect);
//			mLinerForward = (LinearLayout) view.findViewById(R.id.liner_lecture_detail_forward);
//			statusPopup = new PopupWindow(view, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//		}
//		statusPopup.setFocusable(true);
//		statusPopup.setOutsideTouchable(true);
//		statusPopup.setBackgroundDrawable(new BitmapDrawable());
//		int x=DensityUtils.dip2px(B_Side_Acy_list_Detail_Acy.this, 165);
//		statusPopup.showAsDropDown(parent, -x,  A_0_App.getInstance().getShowViewHeight());// 第一参数负的向左，第二个参数正的向下
//
//		mLinerForward.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				if (statusPopup != null) {
//					statusPopup.dismiss();
//				}
//				Intent intent=new Intent(B_Side_Acy_list_Detail_Acy.this, B_Mess_Forward_Select.class);
//				intent.putExtra("title", detail_Notice.getTitle());
//				intent.putExtra("content", "时间："+PubMehods.getFormatDate(detail_Notice.getCreate_time(), "yyyy/MM/dd HH:kk")+"\n"+"地点："+detail_Notice.getPlace());
//				intent.putExtra("type", "2");
//				intent.putExtra("image", detail_Notice.getBg_img());
//				intent.putExtra("acy_type", 2);
//				intent.putExtra("noticeId", acy_detail_id);
//				startActivity(intent);
//			}
//		});
//		
//		mLinerCollct.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				if (statusPopup != null) {
//					statusPopup.dismiss();
//				}
//				PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, "后续开放");
//			}
//		});
//	}
//	
	private void showLoadResult(boolean loading,boolean whole,boolean loadFaile) {
		if (whole)
			liner_acy_detail_whole.setVisibility(View.VISIBLE);
		else
			liner_acy_detail_whole.setVisibility(View.GONE);
		
		if (loadFaile)
			side_acy_detail_load_error.setVisibility(View.VISIBLE);
		else
			side_acy_detail_load_error.setVisibility(View.GONE);
		if(loading){
			drawable.start();
			side_acy_detail_loading.setVisibility(View.VISIBLE);
		}else{
			if (drawable!=null) {
        		drawable.stop();
			}
			side_acy_detail_loading.setVisibility(View.GONE);
	}}
	
	private void readData(String id) {
		A_0_App.getApi().getAcyDetail(B_Side_Acy_list_Detail_Acy.this, A_0_App.USER_TOKEN, id, new InterAcyDetail() {
			@Override
			public void onSuccess(Cpk_Acy_Detail notice,String isJoin, String isStop,String time) {
                if (isFinishing())
                    return;
                  severTime=Long.parseLong(time)*1000;
                if (notice.getContent().equals("")||notice.getContent()==null) {
                 	showLoadResult(false, false, true);
                 	  PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, "信息不存在！");
        			}else{
				  Success(notice, isJoin,isStop);
							if (lv_act_detail_comment != null) {
								lv_act_detail_comment.onRefreshComplete();
								lv_act_detail_comment.setMode(Mode.PULL_UP_TO_REFRESH);
							}
							demo_swiperefreshlayout.setRefreshing(false);
							repfresh = 0;
						}
			}
		},new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                if(isFinishing())
                    return;
                if(!havaSuccessLoadData){
                 PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, msg);
                 showLoadResult(false,false, true);
                }else{
                 showInfo(jsonObject);
                }
                if (lv_act_detail_comment!=null) {
                    lv_act_detail_comment.onRefreshComplete();
                    lv_act_detail_comment.setMode(Mode.PULL_UP_TO_REFRESH);
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
	
	private void signUp(final String id) {
	    A_0_App.getInstance().showProgreDialog(B_Side_Acy_list_Detail_Acy.this,"", true);
		A_0_App.getApi().get_Acy_Apply(id, A_0_App.USER_TOKEN, new InterAcy_Apply() {
			
			@Override
			public void onSuccess() {
                if (isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Side_Acy_list_Detail_Acy.this);
				singResult(true);
				join_acy_state = "1";
				btn_sign_up_me.setBackgroundResource(R.drawable.side_repair_detail_btn_disable_bg);
				readData(id);
			}
		},new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                if(isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Side_Acy_list_Detail_Acy.this);
                PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, msg);
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
	}
	
	// 上拉刷新初始化数据
	private void getMoreComment(String message_id,int page_no) {
		if(readCommentData)
			return;
		readCommentData = true;
		A_0_App.getApi().getCommentList(B_Side_Acy_list_Detail_Acy.this,A_0_App.USER_TOKEN,message_id,String.valueOf(page_no),new InterCommentList(){

			@Override
			public void onSuccess(List<Cpk_Comment_detail> mList) {
				if (isFinishing())
					return;
				//A_0_App.getInstance().CancelProgreDialog(B_Side_Acy_list_Detail_Acy.this);
				readCommentData = false;
				if (mList != null && mList.size() > 0) {
					have_read_page += 1;
					int totleSize = list.size();
					for (int i = 0; i < mList.size(); i++) {
						list.add(mList.get(i));
					}
					adapter.notifyDataSetChanged();
					//solveClashListView = new SolveClashListView(); 
					//solveClashListView.setListViewHeightBasedOnChildren(lv_act_detail_comment,1);
					//lv_act_detail_comment.setSelection(totleSize + 1);
				} else {
					PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, "没有更多了");
				}
				if (lv_act_detail_comment!=null) {
					lv_act_detail_comment.onRefreshComplete();
				}
				//new add
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
                //A_0_App.getInstance().CancelProgreDialog(B_Side_Acy_list_Detail_Acy.this);
                readCommentData = false;
                if (lv_act_detail_comment!=null) {
                    lv_act_detail_comment.onRefreshComplete();
                }
                //new add
                repfresh=0;
                PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, msg);
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
		
	}
	
	private void commentAcy(final String to_comment_id,String article_id,String content) {
		A_0_App.getApi().add_Acy_Comment(to_comment_id,article_id, A_0_App.USER_TOKEN,content, new InterAdd_Acy_Comment() {
			@Override
			public void onSuccess() {
				if(isFinishing())
					return;
				if (to_comment_id.equals("")) {
					PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, "评论成功");
				}else{
					PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, "回复成功");
				}
				InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tv_acy_detail_comment_content.getWindowToken(), 0);
				tv_acy_detail_comment_content.getText().clear();
				comment_id="";
				tv_acy_detail_comment_content.setHint("写评论");
				readData(acy_detail_id);
			}
		},new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                if(isFinishing())
                    return;
                PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, msg);
                
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });

	}
	
	//获取举报类型
	private void getReportType() {
		A_0_App.getApi().get_info_Type(A_0_App.USER_TOKEN,new InterInfo_Type() {
			@Override
			public void onSuccess(List<Cpk_Info_ReportType> Types) {
				if(isFinishing())
					return;
				reportTypes=Types;
				report_Dialog();
			}
		},new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                if(isFinishing())
                    return;
                PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, msg);
                
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });

	}
	//评论点赞
			private void signUpComment(final String id,String like_type) {
				A_0_App.getApi().get_Info_Ac_Comment(id, A_0_App.USER_TOKEN, like_type,new InterInfo_Ac_Comment() {
					
					@Override
					public void onSuccess() {
						if(isFinishing())
							return;
//						A_0_App.getInstance().CancelProgreDialog(
//								B_Side_Acy_list_Detail_Acy.this);
					    temp_like=0;
					    
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getComment_id().equals(id)) {
								
								if (list.get(i).getIsLiked().equals("1")) {
									add_num=-1;
									list.get(i).setIsLiked("0");
								}else{
									add_num=1;
									list.get(i).setIsLiked("1");
								}
								
								String num=list.get(i).getLike_count();
								list.get(i).setLike_count((Integer.parseInt(num)+add_num)+"");
							}
						}
						
						adapter.notifyDataSetChanged();
					}
				},new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        if(isFinishing())
                            return;
//                      A_0_App.getInstance().CancelProgreDialog(
//                              B_Side_Acy_list_Detail_Acy.this);
                        temp_like=0;
                        PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, msg);
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
			}
	
			//举报操作
			private void addReport(final String to_comment_id,String info_id,String type) {
				A_0_App.getApi().get_info_addReport(to_comment_id,info_id, A_0_App.USER_TOKEN,type, new InterInfo_addReport() {
					@Override
					public void onSuccess() {
						if(isFinishing())
							return;
						comment_id="";
						PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, "举报成功，已提交管理员审查处理！");
					}
				},new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        if(isFinishing())
                            return;
                        PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this, msg);
                        
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });

			}
			
			
	public class Mydapter extends BaseAdapter {

		@Override
		public int getCount() {
			if(list != null)
				return list.size();
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
		public View getView(final int posi, View converView, ViewGroup arg2) {
			
			final ViewHolder holder;
			if (converView == null) {
				converView = LayoutInflater.from(B_Side_Acy_list_Detail_Acy.this).inflate(R.layout.item_info_comment, null);
				holder = new ViewHolder();
				
				holder.iv_comment_user_por = (CircleImageView)converView.findViewById(R.id.iv_comment_user_por);
				holder.tv_comment_user_name = (TextView)converView.findViewById(R.id.tv_comment_user_name);
				holder.tv_comment_user_content = (TextView)converView.findViewById(R.id.tv_comment_user_content);
				holder.tv_comment_user_time = (TextView)converView.findViewById(R.id.tv_comment_user_time);
				holder.tv_detaili_info_surname_count = (TextView)converView.findViewById(R.id.tv_detaili_info_surname_count);
				holder.tv_comment_reply_name = (TextView)converView.findViewById(R.id.tv_comment_reply_name);
				holder.tv_comment_reply_content = (TextView)converView.findViewById(R.id.tv_comment_reply_content);
				holder.liner_info = (LinearLayout)converView.findViewById(R.id.liner_info);
				holder.liner_detail_info_count = (LinearLayout)converView.findViewById(R.id.liner_detail_info_count);
				holder.iv_detaili_info_surname_count = (ImageView)converView.findViewById(R.id.iv_detaili_info_surname_count);
				holder.rela_itme_info=(RelativeLayout)converView.findViewById(R.id.rela_itme_info);
				holder.rela_itme_info_down=(RelativeLayout)converView.findViewById(R.id.rela_itme_info_down);
				holder.tv_one = (TextView)converView.findViewById(R.id.tv_one);
				holder.tv_comment_user_order = (TextView)converView.findViewById(R.id.tv_comment_user_order);
				converView.setTag(holder);
			}else{
				holder = (ViewHolder)converView.getTag();
			}
			if (list.size()==1&&list.get(posi).getArticle_id().equals("yuanding")) {
				holder.rela_itme_info.setVisibility(View.GONE);

			}else
			{
				holder.rela_itme_info_down.setVisibility(View.VISIBLE);
				holder.rela_itme_info.setVisibility(View.VISIBLE);
			if (posi % 8 == 0) {
				holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_one);
				
			} else if (posi % 8 == 1) {
				holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_two);
			} else if (posi % 8 == 2) {
				holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_three);
			} else if (posi % 8 == 3) {
				holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_four);
			} else if (posi % 8 == 4) {
				holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_five);
			} else if (posi % 8 == 5) {
				holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_six);
			} else if (posi % 8 == 6) {
				holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_seven);
			} else if (posi % 8 == 7) {
				holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_eight);
			}
			String uri = list.get(posi).getPhoto_url();
			if(holder.iv_comment_user_por.getTag() == null){
			    PubMehods.loadServicePic(imageLoader,uri,holder.iv_comment_user_por, options);
			    holder.iv_comment_user_por.setTag(uri);
			}else{ 
			    if(!holder.iv_comment_user_por.getTag().equals(uri)){
			        PubMehods.loadServicePic(imageLoader,uri,holder.iv_comment_user_por, options);
			        holder.iv_comment_user_por.setTag(uri);
			    }
			}
			//bitmapUtils.display(holder.iv_comment_user_por, list.get(posi).getPhoto_url());
			holder.tv_comment_user_name.setText(list.get(posi).getName()+"-"+list.get(posi).getSchool_name());
			holder.tv_comment_user_content.setText(list.get(posi).getContent());
			holder.tv_comment_user_time.setText(PubMehods.getTimeDifference(severTime,Long.valueOf(list.get(posi).getCreate_time())*1000));
			if (list.get(posi).getReply_comment_info()!=null&&!list.get(posi).getReply_comment_info().equals("{}")&&!list.get(posi).getReply_comment_info().equals("")) {
				Cpk_Info_Detail_Comment_List_All comment_List_All=JSON.parseObject(list.get(posi).getReply_comment_info(), Cpk_Info_Detail_Comment_List_All.class);
				if (comment_List_All.getName()!=null&&!comment_List_All.getName().equals("")) {
					holder.liner_info .setVisibility(View.VISIBLE);
					SpannableStringBuilder builder = new SpannableStringBuilder("回复@"+comment_List_All.getName());  
					ForegroundColorSpan greenSpan = new ForegroundColorSpan(getResources().getColor(R.color.info_green_name));  
					builder.setSpan(greenSpan, 2, comment_List_All.getName().length()+3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
					holder.tv_comment_reply_name.setText(builder);
					holder.tv_comment_reply_content.setText("    "+comment_List_All.getContent());
				}else{
					holder.liner_info .setVisibility(View.GONE);
				}
			}else{
				holder.liner_info .setVisibility(View.GONE);
			}
			
			holder.tv_comment_user_order.setText(list.get(posi).getStorey()+"楼");
			holder.tv_detaili_info_surname_count.setText(list.get(posi).getLike_count());
			if (list.get(posi).getIsLiked()!=null&&list.get(posi).getIsLiked().equals("1")) {
				holder.iv_detaili_info_surname_count.setBackgroundResource(R.drawable.icon_info_click_like_hover);
			}else{
				holder.iv_detaili_info_surname_count.setBackgroundResource(R.drawable.icon_info_click_like);
			}
			holder.tv_comment_user_content.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					comment_id=list.get(posi).getComment_id();
					sent(list.get(posi).getName());
					
					
				}
			});
			holder.tv_comment_user_content.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View arg0) {
					comment_id=list.get(posi).getComment_id();
					sent(list.get(posi).getName());
					return false;
				}
			});
			
       if (list.get(posi).getIsLiked()!=null&&praise_new.equals(list.get(posi).getIsLiked())&&praise_temp.equals(list.get(posi).getComment_id())&&praise_new.equals("1")) {
				
				holder.tv_one.setVisibility(View.VISIBLE);
				holder.tv_one.startAnimation(animation);
				new Handler().postDelayed(new Runnable(){
		            public void run() {
		            	holder.tv_one.setVisibility(View.GONE);
		            	praise_new="-1";
		            } 
				}, 500);
			
			}
			holder.liner_detail_info_count.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					//1已点过0未点击
					if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_LOGIN_TIME)||!praise_temp.equals(list.get(posi).getComment_id())) {
						if (temp_like==0) {
							temp_like=1;
							if (list.get(posi).getIsLiked()!=null) {
								if (list.get(posi).getIsLiked().equals("1")) {
									 praise_new="0";
									 praise_temp=list.get(posi).getComment_id();
									signUpComment(list.get(posi).getComment_id(), "2");
								}else{
									praise_temp=list.get(posi).getComment_id();
									    praise_new="1";
										signUpComment(list.get(posi).getComment_id(), "1");
									}
							}
						
						
						}
					} else {
						PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this,"您的操作过于频繁！");
					}
					
					
					
				}
			});}
			return converView;
		}

	}
	 public void sent(final String comment_name) {
			final Dialog dialog = new Dialog(B_Side_Acy_list_Detail_Acy.this,
					android.R.style.Theme_Translucent_NoTitleBar);
			dialog.setContentView(R.layout.itme_dialog_info);
			dialog.show();
			Button btn1 = (Button) dialog.findViewById(R.id.btn_info_reply);
			Button btn2 = (Button) dialog.findViewById(R.id.btn_info_report);
		
			LinearLayout layout=(LinearLayout) dialog.findViewById(R.id.liner_info_dailog);
			
			layout.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					comment_id="";
					dialog.dismiss();
					return false;
				}
			});
			btn1.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View arg0) {
				
					dialog.dismiss();
					tv_acy_detail_comment_content.setHint("回复@"+comment_name);
					tv_acy_detail_comment_content.setFocusable(true);
					tv_acy_detail_comment_content.requestFocus();
					PubMehods.openKeybord(tv_acy_detail_comment_content, B_Side_Acy_list_Detail_Acy.this);
				}
			});
			btn2.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View arg0) {
					
//					if (reportTypes!=null&&reportTypes.size()>0) {
//						report_Dialog();
//					}else{
//						getReportType();
//					}
					
					dialog.dismiss();
					 PubMehods.showToastStr(B_Side_Acy_list_Detail_Acy.this,"此功能暂未开放！");
				}
			});
			
	 };
	 @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(tv_acy_detail_comment_content.getWindowToken(), 0);
		super.onResume();
	}
	  /**
		 * 举报列表
		 */
		void report_Dialog() {
			final Dialog dialog = new Dialog(B_Side_Acy_list_Detail_Acy.this,
					android.R.style.Theme_Translucent_NoTitleBar);
			dialog.setContentView(R.layout.activity_side_select_report_type);
			dialog.show();
			report_Adapter=new Report_Adapter();
			ListView listView = (ListView) dialog.findViewById(R.id.lv_side_select_sign);
			listView.setAdapter(report_Adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					
					addReport(acy_detail_id, acy_detail_id, reportTypes.get(arg2).getId());
					dialog.dismiss();
				}
			});
		}
	    
		/**
		 * 
		 * @author MyPC签名adpter
		 * 
		 */
		// 加载列表数据
		public class Report_Adapter extends BaseAdapter {

			@Override
			public int getCount() {
				if (reportTypes != null)
					return reportTypes.size();
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
					converView = LayoutInflater.from(B_Side_Acy_list_Detail_Acy.this).inflate(R.layout.item_pub_text, null);
				}
				TextView tv_acy_name = (TextView) converView
						.findViewById(R.id.tv_item_pub_text);
				tv_acy_name.setText(reportTypes.get(posi).getName());
				
				return converView;
			}

		}
	class ViewHolder {
		CircleImageView iv_comment_user_por;
		TextView tv_comment_user_name,tv_comment_reply_name;
		TextView tv_comment_user_content,tv_comment_reply_content;
		TextView tv_comment_user_time,tv_detaili_info_surname_count;
		LinearLayout liner_info,liner_detail_info_count;
		ImageView iv_detaili_info_surname_count;
		TextView tv_one;
		RelativeLayout rela_itme_info,rela_itme_info_down;
		TextView tv_comment_user_order;
	}

	private String share_url_text = "";//分享的URL
	private String share_url_title = "";//分享得标题
	private String share_url_time = "";//分享得时间
	private String share_url_pic = "";//分享得图片
	@Override
	protected void handleTitleBarEvent(int resId,View v) {
		switch (resId) {
		case BACK_BUTTON:
		    goAcy();
			break;
		case ZUI_RIGHT_BUTTON:
			String content = "时间："+PubMehods.getFormatDate(detail_Notice.getCreate_time(), "yyyy/MM/dd HH:kk")+"\n"+"地点："+detail_Notice.getPlace();
			PubForwardOrShare share = new PubForwardOrShare(share_url_text,detail_Notice.getTitle(),share_url_time,share_url_pic,
					B_Side_Acy_list_Detail_Acy.this,acy_detail_id,"2","2",content);
			share.showDialog();
			break;
		default:
			break;
		}
	}
	   
    private void goAcy() {
        if (acy_type == 3) {// 推送
            if (A_Main_Acy.getInstance() != null) {
                finish();
            } else {
                startAcy(B_Side_Acy_list_Detail_Acy.this, A_Main_Acy.class);
            }
        } else {// 正常进入
            goData();
            finish();
            overridePendingTransition(R.anim.animal_push_right_in_normal,R.anim.animal_push_right_out_normal);
        }
        PubMehods.closeKeybord(tv_acy_detail_comment_content, B_Side_Acy_list_Detail_Acy.this);
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
	
	public void singResult(boolean success) {
		final GeneralDialog upDateDialog = new GeneralDialog(B_Side_Acy_list_Detail_Acy.this,R.style.Theme_GeneralDialog);
        upDateDialog.setTitle(R.string.pub_title);
		if(success){
	        upDateDialog.setContent("恭喜你报名成功");
	        upDateDialog.showMiddleButton(R.string.pub_sure, new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                upDateDialog.cancel();
	            }
	        });
	        upDateDialog.show();
		}else{
			
		}
	}
	
	private void goData() {
		Intent it = new Intent();
		it.putExtra("comment_count",detail_Notice.getComment_num());
		it.putExtra("join_count", detail_Notice.getJoin_num());
		setResult(1, it);
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Acy_list_Detail_Acy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Acy_list_Detail_Acy.this,AppStrStatic.kicked_offline());
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
		
		if (list != null) {
			list.clear();
			list = null;
		}
		liner_acy_detail.removeAllViews();
		wv_acy_detail.removeAllViews();
		wv_acy_detail.destroy();
		wv_acy_detail = null;
		adapter = null;
		detail_Notice = null;
		drawable.stop();
		drawable=null;
//		bitmapUtils=null;
//		bitmapUtilsBanner=null;
		System.gc();
		super.onDestroy();
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
        	readData(acy_detail_id);
        	
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
}