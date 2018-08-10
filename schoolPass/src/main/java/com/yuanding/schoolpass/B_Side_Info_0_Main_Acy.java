package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.xutils.image.ImageOptions;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Info_list;
import com.yuanding.schoolpass.service.Api.InterInfoList;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月12日 下午1:33:48
 * 身边中校园资讯
 */
public class B_Side_Info_0_Main_Acy extends A_0_CpkBaseTitle_Navi{

	private View mLinerReadDataError,mLinerNoContent,liner_acy_list_whole_view,side_acy_loading;
	private PullToRefreshListView mPullDownView;
	private MyAdapter adapter;
	private int have_read_page = 1;// 已经读的页数
	private List<Cpk_Info_list> mCourseList = null;
//	protected ImageLoader imageLoader;
//	private DisplayImageOptions options;
	//private BitmapUtils bitmapUtils;
	private int click_posi = 0;
	private ACache maACache;
	private JSONObject jsonObject;
	private long mSystemTime;
	private int screenWidth;
	private WindowManager wm;
	protected ImageOptions bitmapUtils;
	private String cate_id="";
	
	/**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
	
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private boolean havaSuccessLoadData = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_news_list);
		setTitleText("校园头条");
		
		
		cate_id=getIntent().getStringExtra("cate_id");
		wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		screenWidth = wm.getDefaultDisplay().getWidth();
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		liner_acy_list_whole_view = findViewById(R.id.liner_info_list_whole_view);
		mLinerReadDataError = findViewById(R.id.side_info_load_error);
		mLinerNoContent = findViewById(R.id.side_info_no_content);
		side_acy_loading=findViewById(R.id.side_info_loading);
		
		home_load_loading = (LinearLayout) side_acy_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		ImageView iv_blank_por = (ImageView)mLinerNoContent.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView)mLinerNoContent.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.icon_no_info);
		tv_blank_name.setText("太懒了，一条都没有~");
		bitmapUtils=A_0_App.getBitmapUtils(B_Side_Info_0_Main_Acy.this,R.drawable.ic_default_empty_bg,R.drawable.ic_default_empty_bg,false);
		
		mLinerReadDataError.setOnClickListener(onClick);
		
//		imageLoader = A_0_App.getInstance().getimageLoader();
		//bitmapUtils=A_0_App.getBitmapUtils(this,R.drawable.ic_default_acy_empty,R.drawable.ic_default_acy_empty);
       
//		options = A_0_App.getInstance().getOptions(R.drawable.ic_default_banner_empty_bg, R.drawable.ic_default_banner_empty_bg,
//				R.drawable.ic_default_banner_empty_bg);
		mPullDownView = (PullToRefreshListView) findViewById(R.id.lv_side_info_list);
		mCourseList = new ArrayList<Cpk_Info_list>();
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
						startReadData(have_read_page, true);
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
							    mPullDownView.onRefreshComplete();
							    mPullDownView.setMode(Mode.DISABLED);
							}
							
							startReadData(have_read_page, true);

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
		readCache();
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
		
	}
	
    private void readCache() {
        // TODO Auto-generated method stub
        maACache = ACache.get(this);
        jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_info + A_0_App.USER_UNIQID);
        if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        } else {
            updateInfo();
        }
    }

	private void showInfo(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		
			int state = jsonObject.optInt("status");
			List<Cpk_Info_list> mlistContact = new ArrayList<Cpk_Info_list>();
			if (state == 1) {
					mlistContact=JSON.parseArray(jsonObject.optJSONArray("list")+"",Cpk_Info_list.class);
			}
			if (isFinishing())
				return;
			havaSuccessLoadData = true;   
			if (mlistContact != null && mlistContact.size() > 0) {
				clearBusinessList(false);
				mCourseList = mlistContact;
				adapter.notifyDataSetChanged();
				showLoadResult(false,true, false, false);
			} else {
				showLoadResult(false,false, false, true);
				PubMehods.showToastStr(B_Side_Info_0_Main_Acy.this,"没有资讯");
			}
			mSystemTime=jsonObject.optLong("time");
            if (mPullDownView!=null) {
                mPullDownView.onRefreshComplete();
                mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
            }
            
            demo_swiperefreshlayout.setRefreshing(false);                       
            repfresh=0;
		
	}

	private void showLoadResult(boolean loading,boolean wholeView,boolean loadFaile,boolean noData) {
		
		if (wholeView)
			liner_acy_list_whole_view.setVisibility(View.VISIBLE);
		else
			liner_acy_list_whole_view.setVisibility(View.GONE);
		
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
			side_acy_loading.setVisibility(View.VISIBLE);
		}else{
			if (drawable!=null) {
        		drawable.stop();
			}
			side_acy_loading.setVisibility(View.GONE);
	}}
	
	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.side_info_load_error:
				showLoadResult(true,false, false, false);
				have_read_page = 1;
				startReadData(have_read_page,false);
				break;
			default:
				break;
			}
		}
	};
	// 读取推荐课程
	private void startReadData(final int page_no, final boolean pullRefresh) {
		A_0_App.getApi().getInfoList(B_Side_Info_0_Main_Acy.this,A_0_App.USER_TOKEN,String.valueOf(page_no),cate_id, new InterInfoList() {
			
			@Override
			public void onSuccess(List<Cpk_Info_list> mList,long servertime) {
				if (isFinishing())
					return;
				havaSuccessLoadData = true;   
				if (mList != null && mList.size() > 0) {
					clearBusinessList(false);
					have_read_page = 2;
					mCourseList = mList;
					adapter.notifyDataSetChanged();
					showLoadResult(false,true, false, false);
					if(pullRefresh)
						PubMehods.showToastStr(B_Side_Info_0_Main_Acy.this, "刷新成功");
				} else {
					showLoadResult(false,false, false, true);
					PubMehods.showToastStr(B_Side_Info_0_Main_Acy.this,"没有资讯");
				}
				mSystemTime=servertime;
				
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
                if(!havaSuccessLoadData)
                {
                showLoadResult(false,false, true, false);
                }
                PubMehods.showToastStr(B_Side_Info_0_Main_Acy.this, msg);
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

	// 上拉刷新初始化数据
	private void getMoreLecture(int page_no) {
		if(A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
			return;
		A_0_App.getApi().getInfoList(B_Side_Info_0_Main_Acy.this,A_0_App.USER_TOKEN,String.valueOf(page_no), cate_id,new InterInfoList() {
			@Override
			public void onSuccess(List<Cpk_Info_list> mList,long servertime) {
				if (isFinishing())
					return;
				//A_0_App.getInstance().CancelProgreDialog(B_Side_Info_0_Main_Acy.this);
				if (mList != null && mList.size() > 0) {
					have_read_page += 1;
					int totleSize = mCourseList.size();
					for (int i = 0; i < mList.size(); i++) {
						mCourseList.add(mList.get(i));
					}
					adapter.notifyDataSetChanged();
					//mPullDownView.getRefreshableView().setSelection(totleSize + 1);
				} else {
					PubMehods.showToastStr(B_Side_Info_0_Main_Acy.this,"没有更多了");
				}
				mSystemTime=servertime;
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
                A_0_App.getInstance().CancelProgreDialog(B_Side_Info_0_Main_Acy.this);
                PubMehods.showToastStr(B_Side_Info_0_Main_Acy.this, msg);
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
			if (mCourseList != null)
				return mCourseList.size();
			else
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
		public View getView(final int posi, View converView, ViewGroup arg2) {
			ViewHolder holder;
			if (converView == null) {
				holder = new ViewHolder();
				converView = LayoutInflater.from(B_Side_Info_0_Main_Acy.this).inflate(R.layout.item_side_infomation, null);
				holder.iv_side_info = (ImageView) converView.findViewById(R.id.iv_side_info);
				holder.tv_side_info_comment_count = (TextView) converView.findViewById(R.id.tv_side_info_comment_count);
				holder.tv_side_info_surname_count = (TextView) converView.findViewById(R.id.tv_side_info_surname_count);
				holder.tv_side_info_content = (TextView) converView.findViewById(R.id.tv_side_info_content);
				holder.tv_side_info_title = (TextView) converView.findViewById(R.id.tv_side_info_title);
				holder.tv_side_info_time = (TextView) converView.findViewById(R.id.tv_side_info_time);
				converView.setTag(holder);
			} else {
				holder = (ViewHolder) converView.getTag();
			}
		
			LayoutParams lp;
			lp = (LayoutParams) holder.iv_side_info.getLayoutParams();
			lp.width = screenWidth *28/108;
			lp.height = screenWidth *28/108;
			holder.iv_side_info.setLayoutParams(lp);
            if(holder.iv_side_info.getTag() == null){
                PubMehods.loadBitmapUtilsPic(bitmapUtils,holder.iv_side_info, mCourseList.get(posi).getBg_img());
                holder.iv_side_info.setTag(mCourseList.get(posi).getBg_img());
            }else{
                if(!holder.iv_side_info.getTag().equals(mCourseList.get(posi).getBg_img())){
                    PubMehods.loadBitmapUtilsPic(bitmapUtils,holder.iv_side_info, mCourseList.get(posi).getBg_img());
                    holder.iv_side_info.setTag(mCourseList.get(posi).getBg_img());
                }
            }
			holder.tv_side_info_comment_count.setText(mCourseList.get(posi).getComment_count());
			holder.tv_side_info_surname_count.setText(mCourseList.get(posi).getBrows_count());
			holder.tv_side_info_content.setText(mCourseList.get(posi).getDesc());
			holder.tv_side_info_title.setText(mCourseList.get(posi).getTitle());
			holder.tv_side_info_time.setText(PubMehods.getFormatDate(mCourseList.get(posi).getCreate_time(), "MM/dd HH:mm"));
			converView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					click_posi=posi;
					if (mCourseList.get(posi).getIs_jump().equals("1")) {
						
						Intent intent = new Intent(B_Side_Info_0_Main_Acy.this,Pub_WebView_Banner_Acy.class);
						intent.putExtra("title_text",mCourseList.get(posi).getTitle());// 正常列表进入
						intent.putExtra("url_text", mCourseList.get(posi).getJump_url());
						intent.putExtra("tag_skip", "1");
						intent.putExtra("tag_show_refresh_btn", "1");
						intent.putExtra("tag_show_forward_btn", "1");
						intent.putExtra("share_url_text", mCourseList.get(posi).getShare_url());//分享的URL
						intent.putExtra("share_url_title", mCourseList.get(posi).getTitle());//分享的标题
						intent.putExtra("share_url_time", mCourseList.get(posi).getCreate_time());//分享的时间
						intent.putExtra("share_url_pic", mCourseList.get(posi).getBg_img());//分享的图片
						intent.putExtra("share_url_desc", mCourseList.get(posi).getDesc());//分享的内容采用描述
						startActivityForResult(intent, 1);
					}else{
						Intent intent = new Intent(B_Side_Info_0_Main_Acy.this,B_Side_Info_1_Detail_Acy.class);
						intent.putExtra("acy_type", 2);// 正常列表进入
						intent.putExtra("info_id", mCourseList.get(posi).getInfo_id());
						intent.putExtra("share_url_text", mCourseList.get(posi).getShare_url());//分享的URL
						intent.putExtra("share_url_title", mCourseList.get(posi).getTitle());//分享的标题
						intent.putExtra("share_url_time", mCourseList.get(posi).getCreate_time());//分享的时间
						intent.putExtra("share_url_pic", mCourseList.get(posi).getBg_img());//分享的图片
						intent.putExtra("share_url_desc", mCourseList.get(posi).getDesc());//分享的内容采用描述
						startActivityForResult(intent, 1);
					}
					
				}
			});
			return converView;
		}
	}

	class ViewHolder {
		ImageView iv_side_info;
		TextView tv_side_info_comment_count;
		TextView tv_side_info_surname_count;
		TextView tv_side_info_content;
		TextView tv_side_info_title;
		TextView tv_side_info_time;
		
	}

	private void clearBusinessList(boolean setNull) {
		if (mCourseList != null && mCourseList.size() > 0) {
			mCourseList.clear();
			if (setNull)
				mCourseList = null;
		}
	}			
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			String comment_count = data.getExtras().getString("comment_count");
			String liked_count = data.getExtras().getString("liked_count");
			String brows_count = data.getExtras().getString("brows_count");
			if (requestCode == 1) {
			    if (comment_count == null)
                    comment_count = "";
                if (liked_count == null)
                    liked_count = "";
                if (brows_count == null)
                    brows_count = "";
				mCourseList.get(click_posi).setLike_count(liked_count);
				mCourseList.get(click_posi).setComment_count(comment_count);
				mCourseList.get(click_posi).setBrows_count(brows_count);
				adapter.notifyDataSetChanged();
			}
		}
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
        	
        	startReadData(have_read_page, false);
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
	@Override
	protected void onResume() {
		
		super.onResume();
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Acy_list_Scy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Info_0_Main_Acy.this,AppStrStatic.kicked_offline());
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
		clearBusinessList(true);
		adapter= null;
		//bitmapUtils=null;
		drawable.stop();
		drawable=null;
		super.onDestroy();
	}
	
	
	@Override
	protected void handleTitleBarEvent(int resId,View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;

		default:
			break;
		}
		
	}

}
