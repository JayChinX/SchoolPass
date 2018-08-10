package com.yuanding.schoolpass;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.bean.Cpk_SysNotice_ItemUserDetail;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.service.Api.SysNoticeListCallBack;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;

public class B_Account_SystemNotice_Acy extends Fragment{

	protected Context mContext;
	private View viewone;
	
	private View mLinerReadDataError, mLinerNoContent,side_acy_loading,
	liner_acy_list_whole_view;
    private PullToRefreshListView mPullDownView;
    private MyAdapter adapter;
    private int have_read_page = 1;// 已经读的页数
    private List<Cpk_SysNotice_ItemUserDetail> mCourseList = null;
//    protected BitmapUtils bitmapUtils;
    private int click_posi = 0;
    private String type = "1";   // 1 活动 2 校园资讯
    private JSONObject jsonObject;
    private ACache maACache;
	private boolean havaSuccessLoadData = false;
    /**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
    
	protected ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	public static long severTime=0; //服务器时间
    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity.getApplicationContext();
	}
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		viewone = inflater.inflate(R.layout.activity_acc_sysnotice_acy, container, false);
		initView(viewone);
		return viewone;
	}
	
	private void initView(View rootview) {
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)viewone.findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		liner_acy_list_whole_view = viewone.findViewById(R.id.liner_acy_list_whole_view);
		mLinerReadDataError = viewone.findViewById(R.id.side_acy_load_error);
		mLinerNoContent = viewone.findViewById(R.id.side_acy_no_content);
		side_acy_loading=viewone.findViewById(R.id.side_acy_loading);
		ImageView iv_blank_por = (ImageView) mLinerNoContent.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView) mLinerNoContent.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.no_send);
		tv_blank_name.setText("没有通知记录~");

		mLinerReadDataError.setOnClickListener(onClick);

		imageLoader = A_0_App.getInstance().getimageLoader();
		options = A_0_App.getInstance().getOptions(
				R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center);
//		bitmapUtils=A_0_App.getBitmapUtils(getActivity(),R.drawable.ic_default_empty_bg,R.drawable.ic_default_empty_bg);
		mPullDownView = (PullToRefreshListView) viewone.findViewById(R.id.lv_side_acy_list);
		mCourseList = new ArrayList<Cpk_SysNotice_ItemUserDetail>();
		adapter = new MyAdapter();
		mPullDownView.setMode(Mode.BOTH);
		mPullDownView.setAdapter(adapter);
		mPullDownView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(getActivity(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						have_read_page = 1;
						startReadData(have_read_page, true);
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
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
			demo_swiperefreshlayout .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
						public void onRefresh() {

							have_read_page = 1;
							if(null!=mPullDownView){
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
      
		if (A_0_App.USER_STATUS.equals("2")) {
			readCache();
		} else {
			showLoadResult(false,false, false, true);
		}

	}
	
	private void readCache() {
		// TODO Auto-generated method stub
		maACache = ACache.get(getActivity());
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_sysnotice + type + A_0_App.USER_UNIQID);
		
		/**
		 * 假数据 先不读缓存
		 */
		if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
			showInfo(jsonObject);
		}else{
		    updateInfo();
		}
	}

	private void showInfo(JSONObject jsonObject) {
		// TODO Auto-generated method stub
//		severTime=Long.parseLong(jsonObject.optString("time"))*1000;
		List<Cpk_SysNotice_ItemUserDetail> mlist = getList(jsonObject);
		if (mCourseList == null){
			mCourseList = new ArrayList<Cpk_SysNotice_ItemUserDetail>();
		}
		havaSuccessLoadData = true;
		if (mlist != null && mlist.size() > 0) {
			clearBusinessList(false);
			mCourseList = mlist;
			if (adapter!=null) {
				adapter.notifyDataSetChanged();
			}
			showLoadResult(false,true, false, false);
		} else {
			showLoadResult(false,false, false, true);
		}
		demo_swiperefreshlayout.setRefreshing(false);  
        if(null!=mPullDownView){
            mPullDownView.onRefreshComplete();
            mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
        }
        repfresh=0;
		
		
	}

	private List<Cpk_SysNotice_ItemUserDetail> getList(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		
			int state = jsonObject.optInt("status");
			List<Cpk_SysNotice_ItemUserDetail> mlistContact = new ArrayList<Cpk_SysNotice_ItemUserDetail>();
			
			if (state == 1) {
				JSONArray jsonArrayItem = jsonObject.optJSONArray("list");
				mlistContact=JSON.parseArray(jsonArrayItem+"", Cpk_SysNotice_ItemUserDetail.class);
			}
			return mlistContact;
		
	}

	private void showLoadResult(boolean loading,boolean wholeView, boolean loadFaile,
			boolean noData) {

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
		if(loading)
		side_acy_loading.setVisibility(View.VISIBLE);
		else
			side_acy_loading.setVisibility(View.GONE);
			
	}

	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.side_acy_load_error:
				showLoadResult(true,false, false, false);
				have_read_page = 1;
				startReadData(have_read_page, false);
				break;
			default:
				break;
			}
		}
	};

	// 读取推荐课程
	private void startReadData(int page_no, final boolean pullRefresh) {
		
		A_0_App.getApi().getSysNoticeList(type, getActivity(),A_0_App.USER_TOKEN, String.valueOf(page_no),
				new SysNoticeListCallBack() {

					@Override
					public void onSuccess(List<Cpk_SysNotice_ItemUserDetail> mList,String servertime) {
					    if (mCourseList == null)
                            return;
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
                        havaSuccessLoadData = true;
						if (mList != null && mList.size() > 0) {
							severTime=Long.parseLong(servertime)*1000;
							Log.i("abc", "success --severTime:"+severTime);
							
							clearBusinessList(false);
							have_read_page = 2;
							mCourseList = mList;
							if (adapter!=null) {
								adapter.notifyDataSetChanged();
							}
							
							showLoadResult(false,true, false, false);
							if (pullRefresh)
								PubMehods.showToastStr(getActivity(), "刷新成功");
						} else {
							showLoadResult(false,false, false, true);
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
                        if (mCourseList == null)
                            return;
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
                        /*if (showDialog)
                            A_0_App.getInstance().CancelProgreDialog(
                                    getActivity());*/
                        if(!havaSuccessLoadData)
                        {
                        showLoadResult(false,false, true, false);
                        }
                        PubMehods.showToastStr(getActivity(), msg);

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
		
		if (A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
			return;
		
		A_0_App.getApi().getSysNoticeList(type, getActivity(),
				A_0_App.USER_TOKEN, String.valueOf(page_no),
				new SysNoticeListCallBack() {
					@Override
					public void onSuccess(List<Cpk_SysNotice_ItemUserDetail> mList,String servertime) {
					    if (mCourseList == null)
							return;
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
						//A_0_App.getInstance().CancelProgreDialog(getActivity());
						if (mList != null && mList.size() > 0) {
							have_read_page += 1;
							int totleSize = mCourseList.size();
							for (int i = 0; i < mList.size(); i++) {
								mCourseList.add(mList.get(i));
							}
							if (adapter!=null) {
								
								adapter.notifyDataSetChanged();
								//mPullDownView.getRefreshableView().setSelection(totleSize + 1);
							}
						} else {
							PubMehods.showToastStr(getActivity(), "没有更多了");
						}
						if (mPullDownView!=null) {
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
                        if (mCourseList == null)
                            return;
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
//                      A_0_App.getInstance().CancelProgreDialog(getActivity());
                        PubMehods.showToastStr(getActivity(), msg);
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

		/**
		 * 列表返回 type 1 图文，4短消息
		 */
		@Override
		public View getView(final int posi, View converView, ViewGroup arg2) {
			ViewHolder holder;
			if (converView == null) {
				holder = new ViewHolder();
				converView = LayoutInflater.from(getActivity()).inflate(R.layout.item_acc_sysnotice_list, null);
				holder.iv_userheadimg = (CircleImageView)converView.findViewById(R.id.iv_userheadimg);				
				holder.tx_replaymy_username = (TextView)converView.findViewById(R.id.tx_replaymy_username);				
				holder.tx_replaymy_content = (TextView)converView.findViewById(R.id.tx_replaymy_content);
				holder.tx_replaymy_time = (TextView)converView.findViewById(R.id.tx_replaymy_time);
				holder.tx_mypinglun_content = (TextView)converView.findViewById(R.id.tx_mypinglun_content);
				holder.tx_original_type = (TextView)converView.findViewById(R.id.tx_original_type);
				holder.tx_original_zhaiyao = (TextView)converView.findViewById(R.id.tx_original_zhaiyao);
				holder.view_bottom = (View)converView.findViewById(R.id.view_bottom);
				converView.setTag(holder);
			} else {
				holder = (ViewHolder) converView.getTag();
			}
			
			if(null!=mCourseList && mCourseList.size()>0 && null!=holder){			
				
				//回复我的人头像
			    PubMehods.loadServicePic(imageLoader,mCourseList.get(posi).getComment_photo_url(), holder.iv_userheadimg,options);
//				imageLoader.displayImage("http://a.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=996c11de247f9e2f7060150c2a00c512/d788d43f8794a4c245f944130df41bd5ad6e392b.jpg", holder.iv_userheadimg,options);
				
				
				//回复我的人姓名和学校
				holder.tx_replaymy_username.setText(mCourseList.get(posi).getComment_true_name() +" - "+ mCourseList.get(posi).getComment_school_name());
				
				//回复内容
				switch (mCourseList.get(posi).getComment_type()) {
				case 1:
					//被回复
					//更改tx_detailrule的部分文本颜色
					SpannableStringBuilder builder = new SpannableStringBuilder("回复我："+mCourseList.get(posi).getComment_content());  		  
					//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色    
					ForegroundColorSpan greenSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.main_color));  
					builder.setSpan(greenSpan2, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
					holder.tx_replaymy_content.setText(builder); 
					
					//部分文本加粗
//					SpannableStringBuilder builder = new SpannableStringBuilder("回复我："+mCourseList.get(posi).getComment_content());  		  
//					//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色    
//					ForegroundColorSpan greenSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.main_color));  
//					builder.setSpan(greenSpan2, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
//					builder.setSpan(new MyStyleSpan(Typeface.NORMAL), 4, 4+mCourseList.get(posi).getComment_content().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //加粗
//					holder.tx_replaymy_content.setText(builder); 
//					holder.tx_replaymy_content.setMovementMethod(LinkMovementMethod.getInstance());
										
					break;
					
				case 2:		
					//被赞
					SpannableStringBuilder builder2 = new SpannableStringBuilder("赞了我的评论");  		  
					//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色    
					ForegroundColorSpan greenSpan3 = new ForegroundColorSpan(getResources().getColor(R.color.main_color));  
					builder2.setSpan(greenSpan3, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
					holder.tx_replaymy_content.setText(builder2); 
					break;
				case 3:		
					//被删除
					SpannableStringBuilder builder3 = new SpannableStringBuilder("删除了我的评论");  		  
					//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色    
					ForegroundColorSpan greenSpan4 = new ForegroundColorSpan(getResources().getColor(R.color.red2));  
					builder3.setSpan(greenSpan4, 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
					holder.tx_replaymy_content.setText(builder3);
					
					//被删除需要固定的信息
					holder.tx_replaymy_username.setText(A_0_App.APP_NAME+"管理员");
					holder.iv_userheadimg.setImageDrawable(getResources().getDrawable(R.drawable.info_head_weixiaobang120));
					break;

				default:
					//被回复
					//更改tx_detailrule的部分文本颜色
					SpannableStringBuilder builder4 = new SpannableStringBuilder("回复我："+mCourseList.get(posi).getComment_content());  		  
					//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色    
					ForegroundColorSpan greenSpan5 = new ForegroundColorSpan(getResources().getColor(R.color.main_color));  
					builder4.setSpan(greenSpan5, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
					holder.tx_replaymy_content.setText(builder4); 
					break;
				}
				
				//回复时间
				holder.tx_replaymy_time.setText(PubMehods.getTimeDifference(severTime,Long.valueOf(mCourseList.get(posi).getComment_create_time()) * 1000));
				
				//我的评论内容
				holder.tx_mypinglun_content.setText(mCourseList.get(posi).getTo_comment_content());
				
				//原文类型
				holder.tx_original_type.setText("活动: ");
				
				//原文标题
				holder.tx_original_zhaiyao.setText(mCourseList.get(posi).getTitle());
				
				if(posi==mCourseList.size()-1){
					holder.view_bottom.setVisibility(View.VISIBLE);
				}else{
					holder.view_bottom.setVisibility(View.GONE);
				}
				
				converView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						
						if(PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)){
		                    Log.e("test", "重复点击登录按钮");
		                    return;
		                }
						
						Intent intent = new Intent(getActivity(),B_Side_Acy_list_Detail_Acy.class);
						intent.putExtra("acy_type", 2);// 正常列表进入
						intent.putExtra("acy_detail_id", mCourseList.get(posi).getMessage_id()+"");
						startActivityForResult(intent, 1);
					}
				});
				
                
				
			}			
			
			return converView;
		}
	}
	
	
	class ViewHolder {

		CircleImageView iv_userheadimg;
		TextView tx_replaymy_username;
		TextView tx_replaymy_content;
		TextView tx_replaymy_time;
		TextView tx_mypinglun_content;
		TextView tx_original_type;
		TextView tx_original_zhaiyao;
		
		View view_bottom;
		
		
		
	}
	
	/**
	 * textView部分文本加粗span
	 * @author Administrator
	 *
	 */
//	public class MyStyleSpan extends StyleSpan {
//		public MyStyleSpan(int style) {
//			super(style);
//		}
//
//		@Override
//		public int describeContents() { 
//
//			return super.describeContents();
//		}
//
//		@Override
//		public int getSpanTypeId() {
//			return super.getSpanTypeId();
//		}
//
//		@Override
//		public int getStyle() {
//			return super.getStyle();
//		}
//
//		@Override
//		public void updateDrawState(TextPaint ds) {
//			ds.setFakeBoldText(true);
//			super.updateDrawState(ds);
//		}
//
//		@Override
//		public void updateMeasureState(TextPaint paint) {
//			paint.setFakeBoldText(true);
//			super.updateMeasureState(paint);
//		}
//
//		@Override
//		public void writeToParcel(Parcel dest, int flags) { 															
//			super.writeToParcel(dest, flags);
//		}
//	}
	

	private void clearBusinessList(boolean setNull) {
		if (mCourseList != null) {
			mCourseList.clear();
			if (setNull)
				mCourseList = null;
		}
	}
	private void updateInfo(){
		MyAsyncTask updateLectureInfo = new MyAsyncTask(getActivity());
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
    public void onResume() {
    	super.onResume();
    	if (A_0_App.SIDE_NOTICE==0) {
    		clearBusinessList(false);
    		have_read_page = 1;
    		startReadData(have_read_page, false);
    	} 
    	
    }
    
    @Override
    public void onDestroy() {
        clearBusinessList(true);
        adapter = null;
        super.onDestroy();
    }
	
}
