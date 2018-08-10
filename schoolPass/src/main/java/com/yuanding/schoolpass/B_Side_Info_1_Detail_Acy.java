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
import android.os.StrictMode;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yuanding.schoolpass.bangbang.pay.PayStrStatic;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Info_Detail;
import com.yuanding.schoolpass.bean.Cpk_Info_Detail_Comment_Hot;
import com.yuanding.schoolpass.bean.Cpk_Info_Detail_Comment_List;
import com.yuanding.schoolpass.bean.Cpk_Info_Detail_Comment_List_All;
import com.yuanding.schoolpass.bean.Cpk_Info_Detail_Content;
import com.yuanding.schoolpass.bean.Cpk_Info_ReportType;
import com.yuanding.schoolpass.service.Api.InterInfoDetail;
import com.yuanding.schoolpass.service.Api.InterInfo_Comment;
import com.yuanding.schoolpass.service.Api.InterInfo_Like;
import com.yuanding.schoolpass.service.Api.InterInfo_Type;
import com.yuanding.schoolpass.service.Api.InterInfo_addReport;
import com.yuanding.schoolpass.service.Api.InterInfo_reply;
import com.yuanding.schoolpass.service.Api.InterInfotCommentList;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubForwardOrShare;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.MyListView;
import com.yuanding.schoolpass.view.SolveClashListView;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月24日 下午2:47:12
 * 校园资讯详情
 */
public class B_Side_Info_1_Detail_Acy extends A_0_CpkBaseTitle_Navi {

	private View side_acy_detail_load_error,liner_acy_detail_whole,side_acy_detail_loading;
	private ImageView iv_acy_comment_send;
	private TextView tv_acy_detail_title,tv_acy_detail_time,tv_detail_comment_count,tv_acy_detail_join_count;
	private TextView tv_acy_deail_down_comment_num,tv_acy_deail_down_comment_new;
	private TextView tv_content_praise;
	
	private LinearLayout btn_sign_up_me;
	private LinearLayout liner_notice_detail_send;
	private WebView wv_acy_detail;
	private PullToRefreshListView lv_act_detail_comment;
	private MyListView lv_act_detail_comment_hot;
	private EditText tv_acy_detail_comment_content;
	
	private Mydapter adapter;
	private Mydapter_Hot mydapter_Hot;
	private Report_Adapter report_Adapter;
	private String acy_detail_id,comment_id="",temp="";
	
	private List<Cpk_Info_ReportType> reportTypes;
	
	private List<Cpk_Info_Detail_Comment_List_All> list;
	private List<Cpk_Info_Detail_Comment_List_All> list_hot;
	private Cpk_Info_Detail detail_Notice;
	private Cpk_Info_Detail_Content cpk_Info_Detail_Content;
	private Cpk_Info_Detail_Comment_Hot cpk_Info_Detail_Comment_Hot;
	private Cpk_Info_Detail_Comment_List cpk_Info_Detail_Comment_List;
	
	protected ImageLoader imageLoader,imageLoaderBanner;
	private DisplayImageOptions options;
	private SolveClashListView solveClashListView;
	
	private int have_read_page = 2;// 已经读的页数
	private boolean readCommentData =false;
	private ACache maACache;
	private JSONObject jsonObject;
	private int temp_like=0;//防止重复点击请求
	private int add_num=0;//手动增减数量
	private static long severTime=0;
	private Animation animation;
	private String praise_hot="0";
	private String praise_new="0";
	private TextView tv_side_info;
	private TextView tv_load_error,tv_reload;
	private LinearLayout liner_line_new,liner_line_hot;
	private int acy_type;//页面类型    推送1，正常列表进入2
	
	/**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
	private FrameLayout liner_acy_detail;
	
	private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private boolean havaSuccessLoadData = false;
	@Override
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_info_detail);

		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		setTitleText("详情");
        acy_type = getIntent().getExtras().getInt("acy_type");
        if (acy_type == 2) {
            // 列表进入,转发进入
            acy_detail_id = getIntent().getExtras().getString("info_id");
			share_url_text = getIntent().getExtras().getString("share_url_text","");
			share_url_title = getIntent().getExtras().getString("share_url_title","");
			share_url_time = getIntent().getExtras().getString("share_url_time","");
			share_url_pic = getIntent().getExtras().getString("share_url_pic","");
			share_url_desc = getIntent().getExtras().getString("share_url_desc","");
			if (A_0_App.USER_STATUS.equals("2")) {
				showTitleBt(ZUI_RIGHT_BUTTON, true);
				setZuiRightBtn(R.drawable.navigationbar_more_share);
			}
        } else if (acy_type == 1) {
            // 推送进入1
            acy_detail_id = PubMehods.getSharePreferStr(this, "mCurrentClickNotificationMsgId");
        } else if (acy_type == 3) {
			//分享进入
			acy_detail_id = getIntent().getExtras().getString("info_id");
		}
	        
		detail_Notice = new Cpk_Info_Detail();
		cpk_Info_Detail_Content=new Cpk_Info_Detail_Content();
		cpk_Info_Detail_Comment_Hot=new Cpk_Info_Detail_Comment_Hot();
		cpk_Info_Detail_Comment_List=new Cpk_Info_Detail_Comment_List();
		
		animation=AnimationUtils.loadAnimation(B_Side_Info_1_Detail_Acy.this,R.anim.side_info);
		
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		side_acy_detail_load_error = findViewById(R.id.side_info_detail_load_error);
		liner_acy_detail_whole = findViewById(R.id.liner_info_detail_whole);
		side_acy_detail_loading=findViewById(R.id.side_info_detail_loading);
		
		home_load_loading = (LinearLayout) side_acy_detail_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		lv_act_detail_comment = (PullToRefreshListView)findViewById(R.id.lv_info_detail_comment);
		tv_acy_detail_comment_content = (EditText)findViewById(R.id.tv_info_detail_comment_content);
		iv_acy_comment_send = (ImageView)findViewById(R.id.iv_notice_detail_send);
		 liner_notice_detail_send = (LinearLayout)findViewById(R.id.liner_notice_detail_send);
		
		tv_load_error = (TextView)side_acy_detail_load_error.findViewById(R.id.tv_load_error);
		tv_reload = (TextView)side_acy_detail_load_error.findViewById(R.id.tv_reload);
	    
		imageLoader =  A_0_App.getInstance().getimageLoader();
		options = A_0_App.getInstance().getOptions(R.drawable.ic_defalut_person_center, R.drawable.ic_defalut_person_center, R.drawable.ic_defalut_person_center);
		addHeadView(lv_act_detail_comment);
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
		
		reportTypes = new ArrayList<Cpk_Info_ReportType>();
		list = new ArrayList<Cpk_Info_Detail_Comment_List_All>();
		list_hot = new ArrayList<Cpk_Info_Detail_Comment_List_All>();
		adapter = new Mydapter();
		
		lv_act_detail_comment.setAdapter(adapter);
		
		//点击赞
		btn_sign_up_me.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_LOGIN_TIME)) {
					if (temp_like==0) {
						temp_like=1;
						 if (detail_Notice.getIsLiked().equals("1")) {
						    	//资讯取消点赞
							 
						    	 signUp(cpk_Info_Detail_Content.getInfo_id(),"0");
							}else if(detail_Notice.getIsLiked().equals("0")){
								//点赞
								 signUp(cpk_Info_Detail_Content.getInfo_id(),"1");
							}
			                
					}
				} else {
					PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this,"您的操作过于频繁！");
				}
				
			}}
		);
		
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
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			    if (s.length() > 0 && !"".equals(s.toString()))
                {
			    	iv_acy_comment_send.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_comment_send));
                    if (AppStrStatic.WORD_COMMENT_MAX_LIMIT < s.length()) {
                        PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this, "已达到最大字数限制");
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
		liner_notice_detail_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String str = tv_acy_detail_comment_content.getText().toString();
				if (A_0_App.USER_STATUS.equals("2")) {
					
				
				if (str != null && !(str.equals("")) && str.length() >= AppStrStatic.WORD_COMMENT_MIN_LIMIT) {
                    if(!judgeNoNullStr(str)){
                        PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this,"请输入3个字以上的评论内容");
                        return;
                    }
					if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_RONGYUN_CONNECT)) {
						comment_reply(comment_id,acy_detail_id, str);
					} else {
						PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this,"您的评论过于频繁！");
					}
					
				} else {
					PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this,"请输入3个字以上的评论内容");
				}
			}else{
				PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this,R.string.str_no_certified_not_comment);
			}}
		});
		
		lv_act_detail_comment.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Log.e("test", list.size() + "");
				
			}
		});
		
		readCache(acy_detail_id);
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
			
	}
	 @SuppressLint("ClickableViewAccessibility")
	public static void openKeybord(EditText mEditText, Context mContext)
	    {
	        InputMethodManager imm = (InputMethodManager) mContext
	                .getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
	        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
	                InputMethodManager.HIDE_IMPLICIT_ONLY);
	    }
	
    private void readCache(String acy_detail_id) {
        maACache = ACache.get(this);
        jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_info_detail
                + A_0_App.USER_UNIQID + acy_detail_id);
        if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        } else {
            updateInfo();
        }
    }

	private void showInfo(JSONObject jsonObject) {
		 Cpk_Info_Detail notice = getDetail(jsonObject);

		if(isFinishing())
			return;
		havaSuccessLoadData = true;   
		severTime=Long.parseLong(notice.getTime())*1000;
        cpk_Info_Detail_Content =JSON.parseObject(notice.getInfo(), Cpk_Info_Detail_Content.class);
        cpk_Info_Detail_Comment_Hot=JSON.parseObject(notice.getHotComment(), Cpk_Info_Detail_Comment_Hot.class);
        cpk_Info_Detail_Comment_List=JSON.parseObject(notice.getComment(), Cpk_Info_Detail_Comment_List.class);
		have_read_page = 2;
		list.clear();
		list_hot.clear();
		detail_Notice = notice;
		
		tv_acy_detail_title.setText(cpk_Info_Detail_Content.getTitle());
		tv_acy_detail_time.setText(PubMehods.getFormatDate(cpk_Info_Detail_Content.getCreate_time(), "yyyy/MM/dd HH:mm:ss"));
		tv_acy_detail_join_count.setText(cpk_Info_Detail_Content.getLike_count());
		tv_detail_comment_count.setText(cpk_Info_Detail_Content.getComment_count());
		
//		wv_acy_detail.loadDataWithBaseURL("",cpk_Info_Detail_Content.getContent(), "text/html","UTF-8", null);
		 /**
            * 本地读取html
            */
           PubMehods.saveHtml("<style>table{table-collapse:collapse;}</style>"+cpk_Info_Detail_Content.getContent(), "TEMP_HTML");
           
           
           String html_path="file://"+Environment.getExternalStorageDirectory()+"/TEMP_HTML.html";
           wv_acy_detail.loadUrl(html_path);
           wv_acy_detail.addJavascriptInterface(new JavascriptInterface(B_Side_Info_1_Detail_Acy.this), "imagelistner");
           wv_acy_detail.setWebViewClient(new MyWebViewClient()
           {
        	   @Override
        	public boolean shouldOverrideUrlLoading(WebView view, String url) {
        		// TODO Auto-generated method stub
        		return true;
        	}
           });
		
           tv_content_praise.setText(cpk_Info_Detail_Content.getLike_count());
           if (detail_Notice.getIsLiked().equals("1")) {
        	   btn_sign_up_me.setBackgroundResource(R.drawable.side_info_bg);
		}else{
			 btn_sign_up_me.setBackgroundResource(R.drawable.icon_info_click_like_grey);
			
		}
          
           
		   tv_acy_deail_down_comment_num.setText("热门评论（" + cpk_Info_Detail_Comment_Hot.getTotalCount() +"）");
		   tv_acy_deail_down_comment_new.setText("最新评论（" + cpk_Info_Detail_Comment_List.getTotalCount() +"）");
		
		list = JSON.parseArray(cpk_Info_Detail_Comment_List.getList(), Cpk_Info_Detail_Comment_List_All.class);
		list_hot=JSON.parseArray(cpk_Info_Detail_Comment_Hot.getList(), Cpk_Info_Detail_Comment_List_All.class);
		if (list.size()==0) {
			Cpk_Info_Detail_Comment_List_All all=new Cpk_Info_Detail_Comment_List_All();
			all.setComment_id("yuanding");
			list.add(all);
		}
		
		adapter.notifyDataSetChanged();
		mydapter_Hot.notifyDataSetChanged();
		//solveClashListView = new SolveClashListView(); 
		//solveClashListView.setListViewHeightBasedOnChildren(lv_act_detail_comment,1);
		 new Handler().postDelayed(new Runnable(){
	            public void run() {
	            	showLoadResult(false,true, false);
	            } 
			}, 500);
      }

	 // 注入js函数监听
	   private void addImageClickListner() {
	  		// 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，在还是执行的时候调用本地接口传递url过去
	  		wv_acy_detail.loadUrl("javascript:(function(){"
	  		        + "var imgs=document.getElementsByTagName('img');"
	  		        + "for(var i = 0, len = imgs.length; i < len; i++){"
	  		        +      "(function(j){"
	  		        +          "imgs[j].onclick=function(){"
	  		        +              "var _src = imgs[j].getAttribute('data-echo') || '';"
	  		        +              "if( _src == ''){"
	  		        +                  "_src = imgs[j].src;}"
	  		        +              "window.imagelistner.openImage(_src);}"
	  		        +      "})(i);"
	  		        + "}"
	        + "})()"
	        );
	  	}
	   
	  	/**
	  	 * 获取当前页面所有img地址为list
	  	 * @author Administrator
	  	 *
	  	 */
         private void getWebViewAllImgSrc() {
             Log.e("test", "getWebViewAllImgSrc");
        	  wv_acy_detail.loadUrl("javascript:(function (){"
        	      + "var objs = document.getElementsByTagName('img');"
                  + "var imgSrc = '';"
                  + "for(var i=0,len=objs.length;i<len;i++){"
                  +     "var _src = objs[i].getAttribute('data-echo') || '';"
                  +     "if(_src==''){_src=objs[i].src;}"
                  +     "imgSrc!=''?(imgSrc+=','+_src):(imgSrc=_src);"
                  + "}"
                  + "window.imagelistner.getAllImg(imgSrc);"
              + "})()"
              );
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
	  			temp = img.split(","); 
	  			//以换行符为url分隔符 第一个元素的null
	  			for(int i=0;i<temp.length;i++){
	  				//以换行符为url分隔符 第一个元素的null
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
	  	
	
	
	
	private Cpk_Info_Detail getDetail(JSONObject jsonObject) {
		int state;
		
			state = jsonObject.optInt("status");
			if (state == 1) {

				Cpk_Info_Detail notice = JSON.parseObject(jsonObject+"", Cpk_Info_Detail.class);
				
				return notice;
			}
		
		return null;
	}
	

	
    private void addHeadView(PullToRefreshListView listview) {
    	
    	ListView listView2=listview.getRefreshableView();
        View view = LayoutInflater.from(B_Side_Info_1_Detail_Acy.this).inflate(R.layout.activity_side_info_head, null);
        
        liner_line_hot =(LinearLayout)view.findViewById(R.id.liner_line_hot);
        liner_line_new =(LinearLayout)view.findViewById(R.id.liner_line_new);
		tv_acy_detail_title =(TextView)view.findViewById(R.id.tv_acy_detail_title);
		tv_acy_detail_time =(TextView)view.findViewById(R.id.tv_acy_detail_time);
		tv_detail_comment_count =(TextView)view.findViewById(R.id.tv_detail_comment_count);
		tv_acy_detail_join_count =(TextView)view.findViewById(R.id.tv_acy_detail_join_count);
		tv_content_praise =(TextView)view.findViewById(R.id.tv_content_praise);
		tv_side_info =(TextView)view.findViewById(R.id.tv_side_info);
		
		lv_act_detail_comment_hot=(MyListView)view.findViewById(R.id.lv_info_detail_comment_hot);
		liner_acy_detail = (FrameLayout)view.findViewById(R.id.liner_acy_detail);
		tv_acy_deail_down_comment_num =(TextView)view.findViewById(R.id.tv_acy_deail_down_comment_num_hot);
		tv_acy_deail_down_comment_new =(TextView)view.findViewById(R.id.tv_acy_deail_down_comment_new);
		
		btn_sign_up_me = (LinearLayout)view.findViewById(R.id.liner_info_praise);
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
		
//		wv_acy_detail.getSettings().setUseWideViewPort(true); 
//		wv_acy_detail.getSettings().setLoadWithOverviewMode(true); 
		
		listView2.addHeaderView(view);
		mydapter_Hot=new Mydapter_Hot();
		lv_act_detail_comment_hot.setAdapter(mydapter_Hot);
		listView2.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				comment_id="";
				tv_acy_detail_comment_content.setHint("写评论");
				PubMehods.closeKeybord(tv_acy_detail_comment_content, B_Side_Info_1_Detail_Acy.this);
				return false;
			}
		});
    }

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
	//详情数据
	private void readData(String id) {
		A_0_App.getApi().getInfoDetail(B_Side_Info_1_Detail_Acy.this, A_0_App.USER_TOKEN, id, new InterInfoDetail() {
			@Override
			public void onSuccess(Cpk_Info_Detail notice,String time) {
				if(isFinishing())
					return;
				havaSuccessLoadData = true;   
				severTime=Long.parseLong(time)*1000;
		        cpk_Info_Detail_Content =JSON.parseObject(notice.getInfo(), Cpk_Info_Detail_Content.class);
		        cpk_Info_Detail_Comment_Hot=JSON.parseObject(notice.getHotComment(), Cpk_Info_Detail_Comment_Hot.class);
		        cpk_Info_Detail_Comment_List=JSON.parseObject(notice.getComment(), Cpk_Info_Detail_Comment_List.class);
				have_read_page = 2;
				list.clear();
				list_hot.clear();
				detail_Notice = notice;
				
				tv_acy_detail_title.setText(cpk_Info_Detail_Content.getTitle());
				tv_acy_detail_time.setText(PubMehods.getFormatDate(cpk_Info_Detail_Content.getCreate_time(), "yyyy/MM/dd HH:mm:ss"));
				tv_acy_detail_join_count.setText(cpk_Info_Detail_Content.getLike_count());
				tv_detail_comment_count.setText(cpk_Info_Detail_Content.getComment_count());
				
//				wv_acy_detail.loadDataWithBaseURL("",cpk_Info_Detail_Content.getContent(), "text/html","UTF-8", null);
				 /**
	                * 本地读取html
	                */
	               PubMehods.saveHtml("<style>table{table-collapse:collapse;}</style>"+cpk_Info_Detail_Content.getContent(), "TEMP_HTML");
	               String html_path="file://"+Environment.getExternalStorageDirectory()+"/TEMP_HTML.html";
	               wv_acy_detail.loadUrl(html_path);
	               wv_acy_detail.addJavascriptInterface(new JavascriptInterface(B_Side_Info_1_Detail_Acy.this), "imagelistner");
	               wv_acy_detail.setWebViewClient(new MyWebViewClient()
	               {
	            	   @Override
	            	public boolean shouldOverrideUrlLoading(WebView view,
	            			String url) {
	            		// TODO Auto-generated method stub
	            		return true;
	            	}
	               });
				
	               tv_content_praise.setText(cpk_Info_Detail_Content.getLike_count());
	               if (detail_Notice.getIsLiked().equals("1")) {
	            	   btn_sign_up_me.setBackgroundResource(R.drawable.side_info_bg);
				}else{
					  btn_sign_up_me.setBackgroundResource(R.drawable.icon_info_click_like_grey);
					
				}
	               
				tv_acy_deail_down_comment_num.setText("热门评论（" + cpk_Info_Detail_Comment_Hot.getTotalCount() +"）");
				tv_acy_deail_down_comment_new.setText("最新评论（" + cpk_Info_Detail_Comment_List.getTotalCount() +"）");
				
				list = JSON.parseArray(cpk_Info_Detail_Comment_List.getList(), Cpk_Info_Detail_Comment_List_All.class);
				list_hot=JSON.parseArray(cpk_Info_Detail_Comment_Hot.getList(), Cpk_Info_Detail_Comment_List_All.class);
				if (list.size()==0) {
					Cpk_Info_Detail_Comment_List_All all=new Cpk_Info_Detail_Comment_List_All();
					all.setComment_id("yuanding");
					list.add(all);
				}
				adapter.notifyDataSetChanged();
				mydapter_Hot.notifyDataSetChanged();
				//solveClashListView = new SolveClashListView(); 
				//solveClashListView.setListViewHeightBasedOnChildren(lv_act_detail_comment,1);
				
				 new Handler().postDelayed(new Runnable(){
			            public void run() {
			            	showLoadResult(false,true, false);
			            } 
					}, 500);
				 
				 if (lv_act_detail_comment!=null) {
						lv_act_detail_comment.onRefreshComplete();
						lv_act_detail_comment.setMode(Mode.PULL_UP_TO_REFRESH);
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
                if(isFinishing())
                    return;
                PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this, msg);
                if (!havaSuccessLoadData) {
                    showLoadResult(false, false, true);
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
	//资讯点赞
	private void signUp(final String id,String like_type) {
		A_0_App.getApi().get_Info_Like(id, A_0_App.USER_TOKEN, like_type,new InterInfo_Like() {
			
			@Override
			public void onSuccess() {
				if(isFinishing())
					return;
//				A_0_App.getInstance().CancelProgreDialog(
//						B_Side_Info_1_Detail_Acy.this);
			    temp_like=0;
				if (detail_Notice.getIsLiked().equals("1")) {
					add_num=-1;
					detail_Notice.setIsLiked("0");
					 btn_sign_up_me.setBackgroundResource(R.drawable.icon_info_click_like_grey);
					
				}else{
					add_num=1;
					btn_sign_up_me.setBackgroundResource(R.drawable.side_info_bg);
					detail_Notice.setIsLiked("1");
					
					tv_side_info.setVisibility(View.VISIBLE);
					tv_side_info.startAnimation(animation);
					new Handler().postDelayed(new Runnable(){
			            public void run() {
			            	tv_side_info.setVisibility(View.INVISIBLE);
			            } 
					}, 500);
				
				}
				String num= tv_content_praise.getText().toString();
				 tv_content_praise.setText((Integer.parseInt(num)+add_num)+"");
				tv_acy_detail_join_count.setText((Integer.parseInt(num)+add_num)+"");
				
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
//              A_0_App.getInstance().CancelProgreDialog(
//                      B_Side_Info_1_Detail_Acy.this);
                PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this, msg);
                temp_like=0;
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
	}
	   //评论点赞
		private void signUpComment(final String id,String like_type) {
			A_0_App.getApi().get_Info_Comment(id, A_0_App.USER_TOKEN, like_type,new InterInfo_Comment() {
				
				@Override
				public void onSuccess() {
					if(isFinishing())
						return;
//					A_0_App.getInstance().CancelProgreDialog(
//							B_Side_Info_1_Detail_Acy.this);
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
					for (int i = 0; i < list_hot.size(); i++) {
						if (list_hot.get(i).getComment_id().equals(id)) {
							
							if (list_hot.get(i).getIsLiked().equals("1")) {
								add_num=-1;
								list_hot.get(i).setIsLiked("0");
							}else{
								add_num=1;
								list_hot.get(i).setIsLiked("1");
							}
							
							String num=list_hot.get(i).getLike_count();
							list_hot.get(i).setLike_count((Integer.parseInt(num)+add_num)+"");
						}
					}
					adapter.notifyDataSetChanged();
					mydapter_Hot.notifyDataSetChanged();
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
//                  A_0_App.getInstance().CancelProgreDialog(
//                          B_Side_Info_1_Detail_Acy.this);
                    temp_like=0;
                    PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this, msg);
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
		A_0_App.getApi().getInfotCommentList(B_Side_Info_1_Detail_Acy.this,A_0_App.USER_TOKEN,message_id,String.valueOf(page_no),new InterInfotCommentList(){

			@Override
			public void onSuccess(List<Cpk_Info_Detail_Comment_List_All> mList,String total) {
				if (isFinishing())
					return;
				//A_0_App.getInstance().CancelProgreDialog(B_Side_Info_1_Detail_Acy.this);
				readCommentData = false;
				if (mList != null && mList.size() > 0) {
					have_read_page += 1;
					int totleSize = list.size();
					for (int i = 0; i < mList.size(); i++) {
						list.add(mList.get(i));
					}
					adapter.notifyDataSetChanged();
					solveClashListView = new SolveClashListView(); 
					//solveClashListView.setListViewHeightBasedOnChildren(lv_act_detail_comment,1);
					//lv_act_detail_comment.setSelection(totleSize + 1);
				} else {
					PubMehods.showToastStr(
							B_Side_Info_1_Detail_Acy.this, "没有更多了");
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
                //A_0_App.getInstance().CancelProgreDialog(B_Side_Info_1_Detail_Acy.this);
                readCommentData = false;
                if (lv_act_detail_comment!=null) {
                    lv_act_detail_comment.onRefreshComplete();
                }
                //new add
                repfresh=0;
               PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this, msg);
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
		
	}
	
	//回复,评论
		private void comment_reply(final String to_comment_id,String info_id,String content) {
			A_0_App.getApi().get_info_reply(to_comment_id,info_id, A_0_App.USER_TOKEN,content, new InterInfo_reply() {
				@Override
				public void onSuccess() {
					if(isFinishing())
						return;
					if (to_comment_id.equals("")) {
						PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this, "评论成功");
					}else{
						PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this, "回复成功");
					}
					PubMehods.closeKeybord(tv_acy_detail_comment_content, B_Side_Info_1_Detail_Acy.this);
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
                    PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this, msg);
                    
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
							PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this, "举报成功，已提交管理员审查处理！");
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
                            PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this, msg);
                            
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
                            PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this, msg);
                            
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
				converView = LayoutInflater.from(B_Side_Info_1_Detail_Acy.this).inflate(R.layout.item_info_comment, null);
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
			if (list.size()==1&&list.get(posi).getComment_id().equals("yuanding")) {
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
			holder.tv_comment_user_order.setText(list.get(posi).getStorey()+"楼");
			//bitmapUtils.display(holder.iv_comment_user_por, list.get(posi).getPhoto_url());t
			holder.tv_comment_user_name.setText(list.get(posi).getTrue_name()+"-"+list.get(posi).getSchool_name());
			holder.tv_comment_user_content.setText(list.get(posi).getContent());
			holder.tv_comment_user_time.setText(getTimeDifference(Long.valueOf(list.get(posi).getCreate_time())*1000));
			Cpk_Info_Detail_Comment_List_All comment_List_All=JSON.parseObject(list.get(posi).getReply_comment_info(), Cpk_Info_Detail_Comment_List_All.class);
			if (comment_List_All.getTrue_name()!=null&&!comment_List_All.getTrue_name().equals("")) {
				holder.liner_info .setVisibility(View.VISIBLE);
				SpannableStringBuilder builder = new SpannableStringBuilder("回复@"+comment_List_All.getTrue_name());  
				ForegroundColorSpan greenSpan = new ForegroundColorSpan(getResources().getColor(R.color.info_green_name));  
				builder.setSpan(greenSpan, 2, comment_List_All.getTrue_name().length()+3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
				holder.tv_comment_reply_name.setText(builder);
				holder.tv_comment_reply_content.setText("    "+comment_List_All.getContent());
			}else{
				holder.liner_info .setVisibility(View.GONE);
			}
			holder.tv_detaili_info_surname_count.setText(list.get(posi).getLike_count());
			if (list.get(posi).getIsLiked().equals("1")) {
				holder.iv_detaili_info_surname_count.setBackgroundResource(R.drawable.icon_info_click_like_hover);
			}else{
				holder.iv_detaili_info_surname_count.setBackgroundResource(R.drawable.icon_info_click_like);
			}
			holder.tv_comment_user_content.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					comment_id=list.get(posi).getComment_id();
					sent(list.get(posi).getTrue_name());
					
					
				}
			});
			holder.tv_comment_user_content.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View arg0) {
					comment_id=list.get(posi).getComment_id();
					sent(list.get(posi).getTrue_name());
					return false;
				}
			});
			
       if (praise_new.equals(list.get(posi).getComment_id())) {
				
				holder.tv_one.setVisibility(View.VISIBLE);
				holder.tv_one.startAnimation(animation);
				new Handler().postDelayed(new Runnable(){
		            public void run() {
		            	holder.tv_one.setVisibility(View.GONE);
		            	praise_new="0";
		            } 
				}, 500);
			
			}
			holder.liner_detail_info_count.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					//1已点过0未点击
					if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_LOGIN_TIME)||!temp.equals(list.get(posi).getComment_id())) {
						if (temp_like==0) {
							temp_like=1;
						if (list.get(posi).getIsLiked().equals("1")) {
							temp=list.get(posi).getComment_id();
							signUpComment(list.get(posi).getComment_id(), "0");
						}else{
							temp=list.get(posi).getComment_id();
							    praise_new=list.get(posi).getComment_id();
								signUpComment(list.get(posi).getComment_id(), "1");
							}
						
						}
					} else {
						
						PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this,"您的操作过于频繁！");
					}
					
					
					
				}
			});}
			return converView;
		}

	}
	
	
	public class Mydapter_Hot extends BaseAdapter {

		@Override
		public int getCount() {
			if(list_hot != null)
				return list_hot.size();
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
				converView = LayoutInflater.from(B_Side_Info_1_Detail_Acy.this).inflate(R.layout.item_info_comment, null);
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
				holder.tv_one = (TextView)converView.findViewById(R.id.tv_one);
				holder.tv_comment_user_order = (TextView)converView.findViewById(R.id.tv_comment_user_order);
				holder.rela_itme_info_down=(RelativeLayout)converView.findViewById(R.id.rela_itme_info_down);
				converView.setTag(holder);
			}else{
				holder = (ViewHolder)converView.getTag();
			}
			holder.rela_itme_info.setVisibility(View.VISIBLE);
			holder.rela_itme_info_down.setVisibility(View.VISIBLE);
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
			String uri = list_hot.get(posi).getPhoto_url();
			if(holder.iv_comment_user_por.getTag() == null){
			    PubMehods.loadServicePic(imageLoader,uri,holder.iv_comment_user_por, options);
			    holder.iv_comment_user_por.setTag(uri);
			}else{
			    if(!holder.iv_comment_user_por.getTag().equals(uri)){
			        PubMehods.loadServicePic(imageLoader,uri,holder.iv_comment_user_por, options);
			        holder.iv_comment_user_por.setTag(uri);
			    }
			}
			holder.tv_comment_user_order.setText(list_hot.get(posi).getStorey()+"楼");
			holder.tv_comment_user_name.setText(list_hot.get(posi).getTrue_name()+"-"+list_hot.get(posi).getSchool_name());
			holder.tv_comment_user_content.setText(list_hot.get(posi).getContent());
			holder.tv_comment_user_time.setText(getTimeDifference(Long.valueOf(list_hot.get(posi).getCreate_time())*1000));
			Cpk_Info_Detail_Comment_List_All comment_List_All=JSON.parseObject(list_hot.get(posi).getReply_comment_info(), Cpk_Info_Detail_Comment_List_All.class);
			
			holder.tv_detaili_info_surname_count.setText(list_hot.get(posi).getLike_count());
			if (list_hot.get(posi).getIsLiked().equals("1")) {
				holder.iv_detaili_info_surname_count.setBackgroundResource(R.drawable.icon_info_click_like_hover);
			}else{
				holder.iv_detaili_info_surname_count.setBackgroundResource(R.drawable.icon_info_click_like);
			}
			if (comment_List_All.getTrue_name()!=null&&!comment_List_All.getTrue_name().equals("")) {
				holder.liner_info .setVisibility(View.VISIBLE);
				SpannableStringBuilder builder = new SpannableStringBuilder("回复@"+comment_List_All.getTrue_name());  
				ForegroundColorSpan greenSpan = new ForegroundColorSpan(getResources().getColor(R.color.info_green_name));  
				builder.setSpan(greenSpan, 2, comment_List_All.getTrue_name().length()+3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
				holder.tv_comment_reply_name.setText(builder);
				holder.tv_comment_reply_content.setText("    "+comment_List_All.getContent());
			}else{
				holder.liner_info .setVisibility(View.GONE);
			}
			if (praise_hot.equals(list_hot.get(posi).getComment_id())) {
				
				holder.tv_one.setVisibility(View.VISIBLE);
				holder.tv_one.startAnimation(animation);
				new Handler().postDelayed(new Runnable(){
		            public void run() {
		            	holder.tv_one.setVisibility(View.GONE);
		            	praise_hot="0";
		            } 
				}, 500);
			
			}
			
			
			holder.tv_comment_user_content.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					sent(list_hot.get(posi).getTrue_name());
					comment_id=list_hot.get(posi).getComment_id();
					
				}
			});
			holder.tv_comment_user_content.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View arg0) {
					sent(list_hot.get(posi).getTrue_name());
					comment_id=list_hot.get(posi).getComment_id();
					return false;
				}
			});
       holder.liner_detail_info_count.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {

					//1已点过0未点击
					if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_LOGIN_TIME)||!temp.equals(list.get(posi).getComment_id())) {if (temp_like==0) {
						temp_like=1;
					if (list_hot.get(posi).getIsLiked().equals("1")) {
						//-1
						temp=list_hot.get(posi).getComment_id();
						signUpComment(list_hot.get(posi).getComment_id(), "0");
					}else{
						//+1
						   temp=list_hot.get(posi).getComment_id();
							signUpComment(list_hot.get(posi).getComment_id(), "1");
							praise_hot=list_hot.get(posi).getComment_id();
						}
					
					}} else {
						PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this,"您的操作过于频繁！");
					}
					
					
					
				}
			});
			return converView;
		}

	}
	
	
        public void sent(final String comment_name) {
			final Dialog dialog = new Dialog(B_Side_Info_1_Detail_Acy.this,
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
				    if (A_0_App.USER_STATUS.equals("2")) {
    					tv_acy_detail_comment_content.setHint("回复@"+comment_name);
    					tv_acy_detail_comment_content.setFocusable(true);
    					tv_acy_detail_comment_content.requestFocus();
    					openKeybord(tv_acy_detail_comment_content, B_Side_Info_1_Detail_Acy.this);
                    } else {
                        PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this,R.string.str_no_certified_not_comment);
                    }
                    dialog.dismiss();
				}
			});
			btn2.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View arg0) {
                    if (A_0_App.USER_STATUS.equals("2")) {
                        if (reportTypes.size() > 0) {
                            report_Dialog();
                        } else {
                            getReportType();
                        }
                    } else {
                        PubMehods.showToastStr(B_Side_Info_1_Detail_Acy.this,R.string.str_no_certified_not_use);
                    }
                    dialog.dismiss();
				}
			});
			
	 };
	
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
				converView = LayoutInflater.from(B_Side_Info_1_Detail_Acy.this).inflate(R.layout.item_pub_text, null);
			}
			TextView tv_acy_name = (TextView) converView
					.findViewById(R.id.tv_item_pub_text);
			tv_acy_name.setText(reportTypes.get(posi).getName());
			
			return converView;
		}

	}

	private String share_url_text = "";//分享的URL
	private String share_url_title = "";//分享得标题
	private String share_url_time = "";//分享得时间
	private String share_url_pic = "";//分享得图片
	private String share_url_desc = "";//分享得图片
	@Override
	protected void handleTitleBarEvent(int resId,final View v) {
		switch (resId) {
		case BACK_BUTTON:
			goAcy();
			break;
		case ZUI_RIGHT_BUTTON:
			PubForwardOrShare share = new PubForwardOrShare(share_url_text,share_url_title,share_url_time,share_url_pic,
					B_Side_Info_1_Detail_Acy.this,acy_detail_id,"2","7",share_url_desc);
			share.showDialog();
			break;
		default:
			break;
		}
	}
	
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
        			goAcy();
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
    
    private void goAcy() {
        if (acy_type == 1) {// 推送
            if (A_Main_Acy.getInstance() != null) {
                finish();
            } else {
                startAcy(B_Side_Info_1_Detail_Acy.this, A_Main_Acy.class);
            }
        } else {// 正常进入
            goData();
            finish();
            overridePendingTransition(R.anim.animal_push_right_in_normal,R.anim.animal_push_right_out_normal);
        }
        PubMehods.closeKeybord(tv_acy_detail_comment_content, B_Side_Info_1_Detail_Acy.this);
    }
    
    private void startAcy(Context packageContext, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(packageContext, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.animal_push_left_in_normal,R.anim.animal_push_left_out_normal);
        finish();
    }
    
    /**
	 * 举报列表
	 */
	void report_Dialog() {
		final Dialog dialog = new Dialog(B_Side_Info_1_Detail_Acy.this,
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
				
				addReport(comment_id, acy_detail_id, reportTypes.get(arg2).getId());
				dialog.dismiss();
			}
		});
	}
    
	
	private void goData() {
		Intent it = new Intent();
		it.putExtra("comment_count",cpk_Info_Detail_Content.getComment_count());
		it.putExtra("liked_count", tv_content_praise.getText().toString());
	    it.putExtra("brows_count", cpk_Info_Detail_Content.getBrows_count());
		setResult(1, it);
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Acy_list_Detail_Acy_Teacher.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Info_1_Detail_Acy.this,AppStrStatic.kicked_offline());
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
    protected void onResume() {
        PubMehods.closeKeybord(tv_acy_detail_comment_content, B_Side_Info_1_Detail_Acy.this);
        super.onResume();
    }
    
	@Override
	protected void onDestroy() {
		
		if (list != null) {
			list.clear();
			list = null;
		}
		liner_acy_detail.removeAllViews();
		wv_acy_detail.stopLoading();
		wv_acy_detail.removeAllViews();
		wv_acy_detail.destroy();
		wv_acy_detail = null;
		adapter = null;
		liner_acy_detail=null;
		detail_Notice = null;
		drawable.stop();
		drawable=null;
		System.gc();
		super.onDestroy();
	}
	
	/**
	 * 计算相差多长时间
	 * 
	 * @param millis
	 * @return
	 */
	public static String getTimeDifference(long millis) {
		if (severTime==0) {
			severTime=System.currentTimeMillis();
		}
		long minute = getSubTractLongTime(millis, severTime);
		if (minute < 60) {
			if(minute <1){
				return "1分钟前";
			}else{
				return "" + minute + "分钟前";
			}
		} else if (minute >= 60 && minute < 60 * 24) {
			return "" + minute / 60 + "小时前";
		}else{
			return "" + PubMehods.getFormatDate(millis/1000, "yyyy/MM/dd HH:mm");
		}
	}
	/**
	 * 获取日期相差分钟
	 * 
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@SuppressLint("UseValueOf")
	public static int getSubTractLongTime(Long beginDate, Long endDate) {
		long day = (endDate - beginDate) / (60 * 1000);
		if(day<0){
			day=0;
		}
		return new Long(day).intValue();
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
}