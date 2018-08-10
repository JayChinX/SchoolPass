package com.yuanding.schoolpass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.xutils.image.ImageOptions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.bean.Cpk_Side_Lost_List;
import com.yuanding.schoolpass.service.Api.InterLostList;
import com.yuanding.schoolpass.service.Api.InterSideFoundDelete;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.GeneralDialog;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月12日 下午3:18:18 寻物————寻物————寻物
 */
public class B_Side_Found_Found extends Fragment {
	private View mLinerReadDataError, mLinerNoContent,
			liner_lecture_list_whole_view, side_lecture__loading;
	private PullToRefreshListView mPullDownView;
	private List<Cpk_Side_Lost_List> mLecturesList;
	private Mydapter adapter;
	private int have_read_page = 1;// 已经读的页数
	private Boolean firstLoad = false;
	private View viewone;
	protected Context mContext;
	protected ImageLoader imageLoader;
	private DisplayImageOptions options, options_photo;
	private ACache maACache;
	private JSONObject jsonObject;
	HashMap<Integer, View> lmap = new HashMap<Integer, View>();
	private ImageOptions bitmapUtils;
	public FinishReceiver_Found finishReceiver;
	
	 private LinearLayout home_load_loading;
	 private AnimationDrawable drawable;
	
	public B_Side_Found_Found() {
		super();
	}
	private boolean havaSuccessLoadData = false;
	/**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
	
	@SuppressWarnings("deprecation")
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity.getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		viewone = inflater.inflate(R.layout.activity_side_lost_found_main,
				container, false);
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)viewone.findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		firstLoad = true;
		imageLoader = A_0_App.getInstance().getimageLoader();
		options = A_0_App.getInstance().getOptions(
				R.drawable.ic_default_empty_bg, R.drawable.ic_default_empty_bg,
				R.drawable.ic_default_empty_bg);
		options_photo = A_0_App.getInstance().getOptions(
				R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center);
		liner_lecture_list_whole_view = viewone
				.findViewById(R.id.liner_lecture_list_whole_view);
		mPullDownView = (PullToRefreshListView) viewone
				.findViewById(R.id.lv_side_lecture_list);
		side_lecture__loading = viewone
				.findViewById(R.id.side_lecture__loading);
		mLinerReadDataError = viewone
				.findViewById(R.id.side_lecture_load_error);
		mLinerNoContent = viewone.findViewById(R.id.side_lecture_no_content);
		
		home_load_loading = (LinearLayout) side_lecture__loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		ImageView iv_blank_por = (ImageView) mLinerNoContent
				.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView) mLinerNoContent
				.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.no_zhaoling);
		tv_blank_name.setText("丢东西先去发寻物，暂时没人捡到遗失物品~");

		mLinerReadDataError.setOnClickListener(onClick);
		bitmapUtils=A_0_App.getBitmapUtils(getActivity(), R.drawable.ic_default_empty_bg, R.drawable.ic_default_empty_bg,false);
		mLecturesList = new ArrayList<Cpk_Side_Lost_List>();
		adapter = new Mydapter();
		mPullDownView.setMode(Mode.BOTH);
		mPullDownView.setAdapter(adapter);
		finishReceiver = new FinishReceiver_Found();
		IntentFilter intentFilter = new IntentFilter("found_found");
		getActivity().registerReceiver(finishReceiver, intentFilter);
		mPullDownView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// 下拉刷新
						String label = DateUtils.formatDateTime(getActivity(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						have_read_page = 1;
						getLectureList(have_read_page, true);
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
			demo_swiperefreshlayout
					.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
						public void onRefresh() {

							have_read_page = 1;
							if(null!=mPullDownView){
								mPullDownView.setMode(Mode.DISABLED);
							}
							
							getLectureList(have_read_page, true);

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
      
		// getLectureList(have_read_page, false);
		if (A_0_App.USER_STATUS.equals("5") || A_0_App.USER_STATUS.equals("0")) {
			showLoadResult(false, false, false, true);
		} else {
			readCache();
		}
		return viewone;

	}

	private void readCache() {
		// TODO Auto-generated method stub
		maACache = ACache.get(getActivity());
		jsonObject = maACache
				.getAsJSONObject(AppStrStatic.cache_key_side_found_found+A_0_App.USER_UNIQID);
		if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
			showInfo(jsonObject);
		}else{
		    updateInfo();
		}
	}

	private void updateInfo() {
		MyAsyncTask updateLectureInfo = new MyAsyncTask(getActivity());
		updateLectureInfo.execute();
	}

	class MyAsyncTask extends AsyncTask<Void, Integer, Integer> {
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
			// logD("上传融云数据完毕");
		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {

		}
	}

	private void showInfo(JSONObject jsonObject) {
		
			int state = jsonObject.optInt("status");
			List<Cpk_Side_Lost_List> mlistContact = new ArrayList<Cpk_Side_Lost_List>();
			if (state == 1) {
				mlistContact = JSON.parseArray(jsonObject.optJSONArray("llist")
						+ "", Cpk_Side_Lost_List.class);
			}
			if (mLecturesList == null)
				return;
			havaSuccessLoadData = true;
			if (mlistContact != null && mlistContact.size() > 0) {
				clearBusinessList();
				mLecturesList = mlistContact;
				if(adapter!=null){
				adapter.notifyDataSetChanged();
				}
				showLoadResult(false, true, false, false);
			} else {
				showLoadResult(false, false, false, true);
			}
			demo_swiperefreshlayout.setRefreshing(false);  
            if(null!=mPullDownView){
                mPullDownView.onRefreshComplete();
                mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
            }
            repfresh=0;
	}

	private void getLectureList(int page_no, final boolean pullRefresh) {
		A_0_App.getApi().getLostList(getActivity(), "2", A_0_App.USER_TOKEN,
				String.valueOf(page_no), new InterLostList() {
					@Override
					public void onSuccess(List<Cpk_Side_Lost_List> mList) {
						if (mLecturesList == null)
							return;
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
                        havaSuccessLoadData = true;
						if (mList != null && mList.size() > 0) {
							have_read_page = 2;
							clearBusinessList();
							mLecturesList = mList;
							if (adapter!=null) {
								adapter.notifyDataSetChanged();
							}
							
							showLoadResult(false, true, false, false);
							if (pullRefresh)
								PubMehods.showToastStr(getActivity(), "刷新成功");
						} else {
							showLoadResult(false, false, false, true);
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
                        if (mLecturesList == null)
                            return;
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
                        FragmentActivity mfa = getActivity();
                        if(null!=mfa){
                            PubMehods.showToastStr(mfa, msg);
                        }
                        if (!havaSuccessLoadData) {
                            showLoadResult(false, false, true, false);
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
		if (A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
			return;
		A_0_App.getApi().getLostList(getActivity(), "2", A_0_App.USER_TOKEN,
				String.valueOf(page_no), new InterLostList() {
					@Override
					public void onSuccess(List<Cpk_Side_Lost_List> mList) {
						if (mLecturesList==null)
							return;
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
						//A_0_App.getInstance().CancelProgreDialog(getActivity());
						if (mList != null && mList.size() > 0) {
							have_read_page += 1;
							int totleSize = mLecturesList.size();
							for (int i = 0; i < mList.size(); i++) {
								mLecturesList.add(mList.get(i));
							}
							if(adapter!=null){
							adapter.notifyDataSetChanged();
							}
							//mPullDownView.getRefreshableView().setSelection(totleSize + 1);
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
                        if (mLecturesList == null)
                            return;
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
                        if (jsonObject == null) {
                            PubMehods.showToastStr(getActivity(), msg);
                            showLoadResult(false, false, true, false);
                            if (mPullDownView!=null) {
                                mPullDownView.onRefreshComplete();
                            }
                            
                        } else {
                            showInfo(jsonObject);
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
		if (loading){
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
			case R.id.side_lecture_load_error:
				showLoadResult(true, false, false, false);
				clearBusinessList();
				have_read_page = 1;
				getLectureList(have_read_page, true);
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
				converView = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_lost_found_list, null);
				holder.tv_person_name = (TextView) converView
						.findViewById(R.id.tv_person_name);
				holder.tv_lost_type = (TextView) converView
						.findViewById(R.id.tv_lost_type);
				holder.tv_create_time = (TextView) converView
						.findViewById(R.id.tv_create_time);
				holder.tv_school = (TextView) converView
						.findViewById(R.id.tv_school);
				holder.tv_lost_name = (TextView) converView
						.findViewById(R.id.tv_lost_name);
				holder.tv_lost_place = (TextView) converView
						.findViewById(R.id.tv_lost_place);
				holder.tv_lost_time = (TextView) converView
						.findViewById(R.id.tv_lost_time);
				holder.tv_lost_desc = (TextView) converView
						.findViewById(R.id.tv_lost_desc);
				holder.iv_photo = (CircleImageView) converView
						.findViewById(R.id.iv_photo);
				holder.content_img_ll = (LinearLayout) converView
						.findViewById(R.id.image_container);
				holder.temp001=(TextView) converView.findViewById(R.id.tv_temp_001);
				holder.temp002=(TextView) converView.findViewById(R.id.tv_temp_002);
				holder.temp004=(TextView) converView.findViewById(R.id.tv_temp_004);
				holder.tv_lost_phone = (TextView) converView
						.findViewById(R.id.tv_lost_phone);
				lmap.put(posi, converView);
				converView.setTag(holder);
			} else {
				converView = lmap.get(posi);
				holder = (ViewHolder) converView.getTag();
			}

			if (posi % 8 == 0) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_one);

			} else if (posi % 8 == 1) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_two);
			} else if (posi % 8 == 2) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_three);
			} else if (posi % 8 == 3) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_four);
			} else if (posi % 8 == 4) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_five);
			} else if (posi % 8 == 5) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_six);
			} else if (posi % 8 == 6) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_seven);
			} else if (posi % 8 == 7) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_eight);
			}
			String uri = mLecturesList.get(posi).getUser_photo_url();
			if(holder.iv_photo.getTag() == null){
			    PubMehods.loadServicePic(imageLoader,uri,holder.iv_photo, options_photo);
			    holder.iv_photo.setTag(uri);
			}else{
			    if(!holder.iv_photo.getTag().equals(uri)){
			        PubMehods.loadServicePic(imageLoader,uri,holder.iv_photo, options_photo);
			        holder.iv_photo.setTag(uri);
			    }
			}
			holder.tv_person_name.setText(mLecturesList.get(posi)
					.getTrue_name());
			holder.tv_create_time.setText(PubMehods.getFormatDate(mLecturesList
					.get(posi).getCreate_time(), "MM/dd  HH:mm"));
			holder.tv_school.setText(mLecturesList.get(posi).getSchool_name());
			if (mLecturesList.get(posi).getStatus().equals("0")) {
				holder.tv_lost_type.setText("无人认领");
				holder.tv_lost_type.setTextColor(getResources().getColor(R.color.repair_blue));
			} else {
				holder.tv_lost_type.setText("物归原主");
				holder.tv_lost_type.setTextColor(getResources().getColor(R.color.GREENlIGHT));
			}
			holder.tv_lost_phone.setText(mLecturesList.get(posi).getPhone());
			holder.tv_lost_name.setText(mLecturesList.get(posi).getName());
			holder.tv_lost_place.setText(mLecturesList.get(posi).getPlace());
			holder.tv_lost_time.setText(PubMehods.getFormatDate(mLecturesList
					.get(posi).getLost_time(), "MM/dd  HH:mm"));
			holder.tv_lost_desc.setText(mLecturesList.get(posi).getDesc());
			holder.temp001.setText("丢失物品: ");
			holder.temp004.setText("丢失地点: ");
			holder.temp002.setText("丢失时间: ");
			if(A_0_App.isShowAnimation==true){
			 if (posi > A_0_App.side_found_found_curPosi) {
				A_0_App.side_found_found_curPosi = posi;
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
			final ArrayList<String> path = new ArrayList<String>();
			if (mLecturesList.get(posi).getPhoto_url().length() > 0
					&& mLecturesList.get(posi).getPhoto_url() != "") {
				String photo_urls[] = mLecturesList.get(posi).getPhoto_url()
						.split(",");
				holder.content_img_ll.setVisibility(View.VISIBLE);
				for (int i = 0; i < photo_urls.length; i++) {
					path.add(photo_urls[i]);
				}
				if (photo_urls.length == 1) {
					holder.content_img_ll.removeAllViews();
					LinearLayout horizonLayout = new LinearLayout(getActivity());
					RelativeLayout.LayoutParams params;
					ImageView image1 = new ImageView(getActivity());
					float density = getDensity(getActivity());
					WindowManager wm = (WindowManager) getActivity()
							.getSystemService(Context.WINDOW_SERVICE);
					int width = wm.getDefaultDisplay().getWidth();
					float imageLayWidth = width - (10 + 12 + 10 + 50) * density;
					params = new RelativeLayout.LayoutParams(
							(int) (imageLayWidth), (int) (imageLayWidth));
					horizonLayout.addView(image1, params);
					PubMehods.loadBitmapUtilsPic(bitmapUtils,image1,photo_urls[0]);
					image1.setScaleType(ScaleType.CENTER_CROP);
					image1.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							Intent intent = new Intent(getActivity(),
									B_Side_Found_BigImage.class);
							intent.putStringArrayListExtra("path", path);
							intent.putExtra("num", 0);
							startActivity(intent);
							//
							// Intent intent = new Intent(getActivity(),
							// B_Side_Found_Detail.class);
							// Bundle mBundle = new Bundle();
							// mBundle.putSerializable("content",(Serializable)
							// mLecturesList.get(posi));
							// intent.putExtras(mBundle);
							// startActivity(intent);

						}
					});
					holder.content_img_ll.addView(horizonLayout);
				} else if (photo_urls.length > 1) {
					holder.content_img_ll.removeAllViews();
					LinearLayout horizonLayout = new LinearLayout(getActivity());
					LinearLayout.LayoutParams params;
					float density = getDensity(getActivity());
					WindowManager wm = (WindowManager) getActivity()
							.getSystemService(Context.WINDOW_SERVICE);
					int width = wm.getDefaultDisplay().getWidth();
					float imageLayWidth = width - (10 + 12 + 10 + 50) * density;
					for (int i = 0; i < photo_urls.length; i++) {
						  params = new LinearLayout.LayoutParams(
			                        (int) (imageLayWidth / 3-8* density), (int) (imageLayWidth / 3-8* density));
			                params.setMargins((int) (4 * density), (int) (4 * density),
			                        (int) (4 * density), (int) (4 * density));
			                ImageView image2 = new ImageView(getActivity());
			                final int a = i;
			                image2.setScaleType(ScaleType.CENTER_CROP);
//			                image2.setPadding((int) (4 * density), (int) (4 * density),
//			                        (int) (4 * density), (int) (4 * density));
			                image2.setLayoutParams(params);
			                horizonLayout.addView(image2);
			                PubMehods.loadBitmapUtilsPic(bitmapUtils,image2,photo_urls[i]);
						image2.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent(getActivity(),
										B_Side_Found_BigImage.class);
								intent.putStringArrayListExtra("path", path);
								intent.putExtra("num", a);
								startActivity(intent);

							}
						});
					}
					holder.content_img_ll.addView(horizonLayout);
				}
			}else{
				holder.content_img_ll.setVisibility(View.GONE);
			}
			converView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View arg0) {
					if (mLecturesList.get(posi).getIs_self().equals("1")) {
						callSb(mLecturesList.get(posi).getLost_id(), posi);
					} else {
						PubMehods.showToastStr(getActivity(), "只能删除本人信息~");
					}
					return false;
				}
			});
			converView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (mLecturesList.size()>=posi) {
					Intent intent = new Intent(getActivity(),
							B_Side_Found_Detail.class);
					Bundle mBundle = new Bundle();
					mBundle.putSerializable("content",
							(Serializable) mLecturesList.get(posi));
					intent.putExtras(mBundle);
					startActivity(intent);}
				}
			});
			return converView;
		}

	}

	public void callSb(final String lost_id, final int posi) {
		final GeneralDialog upDateDialog = new GeneralDialog(getActivity(),
				R.style.Theme_GeneralDialog);
		upDateDialog.setTitle(R.string.pub_title);
		upDateDialog.setContent("删除该信息?");
		upDateDialog.showLeftButton(R.string.pub_cancel, new OnClickListener() {
			@Override
			public void onClick(View v) {
				upDateDialog.cancel();
			}
		});
		upDateDialog.showRightButton(R.string.pub_sure, new OnClickListener() {
			@Override
			public void onClick(View v) {
				upDateDialog.cancel();
				delete(lost_id, posi);
			}
		});

		upDateDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
			}
		});
		upDateDialog.show();
	}

	void delete(final String lost_id, final int posi) {
        if (mLecturesList == null)
            return;
        A_0_App.getInstance().showProgreDialog(getActivity(),
                "删除中，请稍候", true);
		A_0_App.getApi().SideFoundDelete(A_0_App.USER_TOKEN, lost_id,
				new InterSideFoundDelete() {

					@Override
					public void onSuccess() {
                        if (mLecturesList == null)
                            return;
						Intent intent1 = new Intent("found");
						intent1.putExtra("lost_id", lost_id);
						getActivity().sendBroadcast(intent1);
						A_0_App.getInstance().CancelProgreDialog(getActivity());
						PubMehods.showToastStr(getActivity(), "删除成功！");
						mLecturesList.remove(posi);
						if(adapter!=null){
						adapter.notifyDataSetChanged();
						}
					}
				},new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    

                    @Override
                    public void onFailure(String msg) {
                        A_0_App.getInstance().CancelProgreDialog(getActivity());
                        PubMehods.showToastStr(getContext(), msg);
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
	}

	class ViewHolder {
		TextView tv_person_name;
		TextView tv_lost_type;
		TextView tv_create_time;
		TextView tv_school;
		TextView tv_lost_name;
		TextView tv_lost_place;
		TextView tv_lost_time;
		TextView tv_lost_desc;
		LinearLayout content_img_ll;
		CircleImageView iv_photo;
		TextView temp001,temp002,temp004;
		TextView tv_lost_phone;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!firstLoad) {
			if (A_0_App.SIDE_NOTICE==1) {
			have_read_page = 1;
			getLectureList(have_read_page, false);}
		} else {
			firstLoad = false;
		}

	}

	@Override
	public void onDestroy() {
		if (mLecturesList != null) {
			mLecturesList.clear();
			mPullDownView = null;
		}
		adapter = null;
        try {
            getActivity().unregisterReceiver(finishReceiver);
        } catch (Exception e) {

        }
        drawable.stop();
        drawable=null;
		super.onDestroy();
	}

	public static float getDensity(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		return dm.density;
	}
	public class FinishReceiver_Found extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			for (int i = 0; i < mLecturesList.size(); i++) {
				if (arg1.getStringExtra("lost_id").equals(mLecturesList.get(i).getLost_id())) {
					mLecturesList.remove(i);
					if (adapter!=null) {
						adapter.notifyDataSetChanged();
					}
					
					if (mLecturesList.size()==0) {
						showLoadResult(false, false, false, true);
					}
				}
			}
			
		}}
}
