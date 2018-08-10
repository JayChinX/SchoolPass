package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Search_Mess_Con;
import com.yuanding.schoolpass.bean.Cpk_Search_Mess_Notice;
import com.yuanding.schoolpass.service.Api.InterMessSearchList;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;

/**
 * 
 * @version 创建时间：2015年11月12日 下午3:18:18 全局搜索更多信息
 */
public class B_Mess_Notice_All_Search_MoreMess extends A_0_CpkBaseTitle_Navi{
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, View> lmap = new HashMap<Integer, View>();
	private View mLinerReadDataError, mLinerNoContent,
			liner_lecture_list_whole_view, side_lecture__loading;
	private PullToRefreshListView mPullDownView;
	private List<Cpk_Search_Mess_Notice> mLecturesList;
	private Mydapter adapter;
	private int have_read_page = 1;// 已经读的页数
	private Boolean firstLoad = false;
	protected Context mContext;
	private EditText mSearchInput;
	protected ImageLoader imageLoader;
	private DisplayImageOptions options_photo;
	private String key="";
	
	/**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_notice_search);
		A_0_App.getInstance().addActivity_rongyun(this);
		setTitleText("搜索");
		firstLoad = true;
		imageLoader = A_0_App.getInstance().getimageLoader();
		options_photo  = A_0_App.getInstance().getOptions(R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center);
		mLecturesList = new ArrayList<Cpk_Search_Mess_Notice>();
		key=getIntent().getStringExtra("key");
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		mSearchInput = (EditText)findViewById(R.id.school_friend_member_search_input);
		mSearchInput.setText(key+"");
		liner_lecture_list_whole_view =findViewById(R.id.liner_lecture_list_whole_view);
		mPullDownView = (PullToRefreshListView)findViewById(R.id.lv_side_lecture_list);
		side_lecture__loading = findViewById(R.id.side_lecture__loading);
		mLinerReadDataError = findViewById(R.id.side_lecture_load_error);
		mLinerNoContent = findViewById(R.id.side_lecture_no_content);
		ImageView iv_blank_por = (ImageView) mLinerNoContent
				.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView) mLinerNoContent
				.findViewById(R.id.tv_blank_name);
		//tv_blank_name.setText("");
		//iv_blank_por.setBackgroundResource(R.drawable.noinfo);
		
		home_load_loading = (LinearLayout) side_lecture__loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		mLinerReadDataError.setOnClickListener(onClick);
		
		adapter = new Mydapter();
		mPullDownView.setMode(Mode.BOTH);
		mPullDownView.setAdapter(adapter);
		//addHeadView(mPullDownView);
		mPullDownView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// 下拉刷新
						String label = DateUtils.formatDateTime(
								B_Mess_Notice_All_Search_MoreMess.this,System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						have_read_page = 1;
						getLectureList(have_read_page,key, true);
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						
						if (repfresh==0) {
							repfresh=1;
							demo_swiperefreshlayout.setEnabled(false);
							demo_swiperefreshlayout.setRefreshing(false);  
							getMoreLecture(have_read_page,key);
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
							getLectureList(have_read_page,key, true);

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
		
		
		
//		mSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//			@Override
//			public boolean onEditorAction(TextView v, int actionId,
//					KeyEvent event) {
//				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//
//					if (mSearchInput.getText().toString().length() > 0) {
//						key=mSearchInput.getText().toString();
//						have_read_page = 1;
//						showLoadResult(true, false, false, false);
//						getLectureList(have_read_page, key,false);
//						 display();
//					}
//
//					return true;
//
//				}
//
//				return false;
//			}
//
//		});
//		
		 mSearchInput.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					
					
					
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
					
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					key=mSearchInput.getText().toString();
					have_read_page=1;
					 if (mHandler.hasMessages(MSG_SEARCH)) {
		                    mHandler.removeMessages(MSG_SEARCH);
		                }
		                if (TextUtils.isEmpty(arg0)) {
		                    showLoadResult(false, false, false, true);
		                } else {// 延迟搜索
		                    mHandler.sendEmptyMessageDelayed(MSG_SEARCH, TIME_INPUT_REQUEST); // 自动搜索功能
		                }
					
				}
			});
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
		getLectureList(have_read_page,key, false);
	}
	private static final int MSG_SEARCH = 1;
    public static final int TIME_INPUT_REQUEST = 800;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //搜索请求
            if (mSearchInput.getText().toString().length() > 0) {
                showLoadResult(true, false, false, false);
                getLectureList(have_read_page, mSearchInput.getText().toString(),false);
            }else{
            	getLectureList(have_read_page, mSearchInput.getText().toString(),false);
            }
        }
    };
    
	
	private void addHeadView(PullToRefreshListView mListview) {
        
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        View view = getLayoutInflater().inflate(R.layout.itme_search_text, mListview, false);
        view.setLayoutParams(layoutParams);
        TextView tv_balance=(TextView) view.findViewById(R.id.search_mess);
        ListView lv = mListview.getRefreshableView();
        tv_balance.setText("信息通知");
        lv.addHeaderView(view);
    }
	
	private void getLectureList(int page_no,String keyword,final boolean pullRefresh) {
		A_0_App.getApi().getMessSearchList(B_Mess_Notice_All_Search_MoreMess.this,A_0_App.USER_TOKEN,"2",String.valueOf(page_no),keyword,
				new InterMessSearchList() {
					@Override
					public void onSuccess(List<Cpk_Search_Mess_Con> mess_Con,List<Cpk_Search_Mess_Notice> mess_Notice,String userIsMore,String messageIsMore) {
						if (B_Mess_Notice_All_Search_MoreMess.this.isFinishing())
							return;
						if (mess_Notice != null && mess_Notice.size() > 0) {
							have_read_page = 2;
							clearBusinessList();
							mLecturesList = mess_Notice;
							adapter.notifyDataSetChanged();
							showLoadResult(false, true, false, false);
							if (pullRefresh)
								PubMehods.showToastStr(B_Mess_Notice_All_Search_MoreMess.this,
										"刷新成功");
						} else {
							mLecturesList.clear();
							adapter.notifyDataSetChanged();
							showLoadResult(false, true, false, false);
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
                        if (B_Mess_Notice_All_Search_MoreMess.this.isFinishing())
                            return;
                        //share_bottom.setVisibility(View.GONE);
                        PubMehods.showToastStr(B_Mess_Notice_All_Search_MoreMess.this, msg);
                        mLecturesList.clear();
                        adapter.notifyDataSetChanged();
                        showLoadResult(false, true, false, false);
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
	private void getMoreLecture(int page_no,String keyword) {
		if (A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
			return;
		A_0_App.getApi().getMessSearchList(B_Mess_Notice_All_Search_MoreMess.this,A_0_App.USER_TOKEN,"2",String.valueOf(page_no),keyword,
				new InterMessSearchList() {
					@Override
					public void onSuccess(List<Cpk_Search_Mess_Con> mess_Con,List<Cpk_Search_Mess_Notice> mess_Notice,String userIsMore,String messageIsMore) {
						if (B_Mess_Notice_All_Search_MoreMess.this.isFinishing())
							return;
						if (mess_Notice != null && mess_Notice.size() > 0) {
							have_read_page += 1;
							int totleSize = mLecturesList.size();
							for (int i = 0; i < mess_Notice.size(); i++) {
								mLecturesList.add(mess_Notice.get(i));
							}
							adapter.notifyDataSetChanged();
						} else {
							PubMehods.showToastStr(B_Mess_Notice_All_Search_MoreMess.this,
									"没有更多了");
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
                        if (B_Mess_Notice_All_Search_MoreMess.this.isFinishing())
                            return;
                        A_0_App.getInstance().CancelProgreDialog(
                                B_Mess_Notice_All_Search_MoreMess.this);
                        PubMehods.showToastStr(B_Mess_Notice_All_Search_MoreMess.this, msg);
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

	private void clearBusinessList() {
		if (mLecturesList != null && mLecturesList.size() > 0) {
			mLecturesList.clear();
		}
	}

	private void showLoadResult(boolean loading, boolean wholeView,
			boolean loadFaile, boolean noData) {
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
				side_lecture__loading.setVisibility(View.VISIBLE);
			} else {
				if (drawable != null) {
					drawable.stop();
				}
				side_lecture__loading.setVisibility(View.GONE);
			}
	}

	
	
	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.side_lecture_load_error:
				showLoadResult(true, false, false, false);
				have_read_page = 1;
				getLectureList(have_read_page,key, true);
				break;
			default:
				break;
			}
		}
	};
	public class Mydapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mLecturesList != null)
				return mLecturesList.size();
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

		@SuppressLint("InflateParams")
		@Override
		public View getView(final int posi, View converView, ViewGroup arg2) {
			ViewHolder holder;
			if (lmap.get(posi) == null) {
				
				holder = new ViewHolder();
				converView = LayoutInflater.from(B_Mess_Notice_All_Search_MoreMess.this)
						.inflate(R.layout.item_mess_message_search, null);
				holder.tv_mess_title = (TextView) converView
						.findViewById(R.id.tv_mess_title);
				holder.tv_mess_time = (TextView) converView
						.findViewById(R.id.tv_mess_time);
				holder.tv_mess_sort = (TextView) converView
						.findViewById(R.id.tv_mess_sort);
				lmap.put(posi, converView);
				converView.setTag(holder);
			}else{
				converView = lmap.get(posi);
				holder = (ViewHolder) converView.getTag();
			}
			if (key.toCharArray().length>0) {
				 String temp=mLecturesList.get(posi).getTitle();
            for (int i = 0; i < key.toCharArray().length; i++) {
           	 if (temp.contains(key.toCharArray()[i]+"")) {
           		 if (!("<font color='#49c433'></font>").contains(key.toCharArray()[i]+"")) {
       			  temp=temp.replace(key.toCharArray()[i]+"", "<font color='#49c433'>"+key.toCharArray()[i]+"</font>");
				}
				}}
            holder.tv_mess_title.setText(Html.fromHtml(temp));
			}
			holder.tv_mess_time.setText(PubMehods.getFormatDate(Long.valueOf(mLecturesList.get(posi).getCreate_time()),"MM/dd HH:mm"));
			holder.tv_mess_sort.setText(mLecturesList.get(posi).getApp_msg_sign());
			if(A_0_App.isShowAnimation==true){
			 if (posi > A_0_App.side_found_my_list_curPosi) {
				A_0_App.side_found_my_list_curPosi = posi;
				Animation an = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
				an.setDuration(400);
				an.setStartOffset(20 * posi);
				converView.startAnimation(an);
			 }
			}
			converView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
						if (mLecturesList.get(posi).getType().equals("4")) {// 短信文本消息
							Intent intent = new Intent(B_Mess_Notice_All_Search_MoreMess.this,B_Mess_Notice_Detail_MessText.class);
							intent.putExtra("acy_type", 2);// 正常列表进入
							intent.putExtra("message_id", mLecturesList.get(posi).getMessage_id());
							intent.putExtra("mess_content", mLecturesList.get(posi).getTitle());
							startActivityForResult(intent, 1);
						} else {// 正常消息
							Intent intent = new Intent(B_Mess_Notice_All_Search_MoreMess.this,B_Mess_Notice_Detail.class);
							intent.putExtra("acy_type", 2);// 正常列表进入
							intent.putExtra("message_id", mLecturesList.get(posi).getMessage_id());
							intent.putExtra("mess_content",mLecturesList.get(posi).getTitle());
							startActivityForResult(intent, 1);
						}
						
						
					
					
				}
			});
			return converView;
		}

	} 
	class ViewHolder {
		TextView tv_name,tv_mess_title,tv_mess_time,tv_mess_sort;
		TextView tv_phone;
		CircleImageView iv_photo;
		
	}


	@Override
	public void onResume() {
		super.onResume();
		if (!firstLoad) {
			have_read_page = 1;
			getLectureList(have_read_page,key, false);
			display();
		} else {
			mSearchInput.requestFocus();
			openKeybord(mSearchInput, B_Mess_Notice_All_Search_MoreMess.this);
			firstLoad = false;
		}

	}

	@Override
	public void onDestroy() {
		if (mLecturesList != null) {
			mLecturesList.clear();
			mPullDownView = null;
		}
		drawable.stop();
		drawable=null;
		adapter = null;
		super.onDestroy();
	}
	public static float getDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.density;
    }
	 @SuppressLint("ClickableViewAccessibility")
	    private  void openKeybord(EditText mEditText, Context mContext) {
	        InputMethodManager imm = (InputMethodManager) mContext
	                .getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
	        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
	    }
	void display(){
		 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.hideSoftInputFromWindow(mSearchInput.getWindowToken(), 0);

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
                    //A_0_App.getInstance().showExitDialog(B_Side_Found_Search.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Mess_Notice_All_Search_MoreMess.this,AppStrStatic.kicked_offline());
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
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			display();
			finish();
			overridePendingTransition(R.anim.animal_push_right_in_normal,
					R.anim.animal_push_right_out_normal);
			break;
		default:
			break;
		}
	}
	
}
