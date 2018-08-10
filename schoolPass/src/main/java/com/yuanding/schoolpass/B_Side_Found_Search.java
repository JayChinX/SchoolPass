package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xutils.image.ImageOptions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Side_Lost_List;
import com.yuanding.schoolpass.service.Api.InterLostSearchList;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月12日 下午3:18:18 失物招领搜索
 */
public class B_Side_Found_Search extends A_0_CpkBaseTitle_Navi{
	HashMap<Integer, View> lmap = new HashMap<Integer, View>();
	private View mLinerReadDataError, mLinerNoContent,
			liner_lecture_list_whole_view, side_lecture__loading;
	private PullToRefreshListView mPullDownView;
	private List<Cpk_Side_Lost_List> mLecturesList;
	private Mydapter adapter;
	private int have_read_page = 1;// 已经读的页数
	private Boolean firstLoad = false;
	protected Context mContext;
	private EditText mSearchInput;
	protected ImageLoader imageLoader;
	private DisplayImageOptions options, options_photo;
	private String key = "";
	private ImageOptions bitmapUtils;
	
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
		setView(R.layout.activity_side_lost_found_search);
		setTitleText("查找");
		firstLoad = true;
		imageLoader = A_0_App.getInstance().getimageLoader();
		options = A_0_App.getInstance().getOptions(
				R.drawable.ic_default_empty_bg, R.drawable.ic_default_empty_bg,
				R.drawable.ic_default_empty_bg);
		options_photo = A_0_App.getInstance().getOptions(
				R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center);
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		mSearchInput = (EditText) findViewById(R.id.school_friend_member_search_input);
		liner_lecture_list_whole_view = findViewById(R.id.liner_lecture_list_whole_view);
		mPullDownView = (PullToRefreshListView) findViewById(R.id.lv_side_lecture_list);
		side_lecture__loading = findViewById(R.id.side_lecture__loading);
		mLinerReadDataError = findViewById(R.id.side_lecture_load_error);
		mLinerNoContent = findViewById(R.id.side_lecture_no_content);
		
		home_load_loading = (LinearLayout) side_lecture__loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		ImageView iv_blank_por = (ImageView) mLinerNoContent
				.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView) mLinerNoContent
				.findViewById(R.id.tv_blank_name);
		tv_blank_name.setText("没有符合条件的物品~");
		iv_blank_por.setBackgroundResource(R.drawable.no_zhaoling);
		mLinerReadDataError.setOnClickListener(onClick);
		mLecturesList = new ArrayList<Cpk_Side_Lost_List>();
		adapter = new Mydapter();
		mPullDownView.setMode(Mode.BOTH);
		mPullDownView.setAdapter(adapter);
		bitmapUtils=A_0_App.getBitmapUtils(B_Side_Found_Search.this, R.drawable.ic_default_empty_bg, R.drawable.ic_default_empty_bg,false);
		mPullDownView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// 下拉刷新
						String label = DateUtils.formatDateTime(
								B_Side_Found_Search.this,
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						have_read_page = 1;
						getLectureList(have_read_page, key, true);
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
		
		mSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {

					if (mSearchInput.getText().toString().length() > 0) {
						key=mSearchInput.getText().toString();
						have_read_page = 1;
						showLoadResult(true, false, false, false);
						getLectureList(have_read_page, key,false);
						 display();
					}

					return true;

				}

				return false;
			}

		});
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}

	private void getLectureList(int page_no, String keyword,
			final boolean pullRefresh) {
		A_0_App.getApi().getLostSearchList(B_Side_Found_Search.this,
				A_0_App.USER_TOKEN, String.valueOf(page_no), keyword,
				new InterLostSearchList() {
					@Override
					public void onSuccess(List<Cpk_Side_Lost_List> mList) {
						if (B_Side_Found_Search.this.isFinishing())
							return;
						if (mList != null && mList.size() > 0) {
							have_read_page = 2;
							clearBusinessList();
							mLecturesList = mList;
							adapter.notifyDataSetChanged();
							showLoadResult(false, true, false, false);
							if (pullRefresh)
								PubMehods.showToastStr(
										B_Side_Found_Search.this, "刷新成功");
						} else {
							PubMehods.showToastStr(B_Side_Found_Search.this,
									"没有符合条件的物品~");
							showLoadResult(false, false, false, true);
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
                        if (B_Side_Found_Search.this.isFinishing())
                            return;
                        PubMehods.showToastStr(B_Side_Found_Search.this, msg);
                        showLoadResult(false, false, true, false);
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
	private void getMoreLecture(int page_no, String keyword) {
		if (A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
			return;
		A_0_App.getApi().getLostSearchList(B_Side_Found_Search.this,
				A_0_App.USER_TOKEN, String.valueOf(page_no), keyword,
				new InterLostSearchList() {
					@Override
					public void onSuccess(List<Cpk_Side_Lost_List> mList) {
						if (B_Side_Found_Search.this.isFinishing())
							return;
						//A_0_App.getInstance().CancelProgreDialog(B_Side_Found_Search.this);
						if (mList != null && mList.size() > 0) {
							have_read_page += 1;
							int totleSize = mLecturesList.size();
							for (int i = 0; i < mList.size(); i++) {
								mLecturesList.add(mList.get(i));
							}
							adapter.notifyDataSetChanged();
							//mPullDownView.getRefreshableView().setSelection(totleSize + 1);
						} else {
							PubMehods.showToastStr(B_Side_Found_Search.this,
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
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        if (B_Side_Found_Search.this.isFinishing())
                            return;
                        A_0_App.getInstance().CancelProgreDialog(
                                B_Side_Found_Search.this);
                        PubMehods.showToastStr(B_Side_Found_Search.this, msg);
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
				getLectureList(have_read_page, key, true);
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
				converView = LayoutInflater.from(B_Side_Found_Search.this)
						.inflate(R.layout.item_lost_found_list, null);
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
				holder.tv_lost_phone = (TextView) converView
						.findViewById(R.id.tv_lost_phone);
				holder.tv_lost_desc = (TextView) converView
						.findViewById(R.id.tv_lost_desc);
				holder.iv_photo = (CircleImageView) converView
						.findViewById(R.id.iv_photo);
				holder.content_img_ll = (LinearLayout) converView
						.findViewById(R.id.image_container);
				holder.temp001=(TextView) converView.findViewById(R.id.tv_temp_001);
				holder.temp002=(TextView) converView.findViewById(R.id.tv_temp_002);
				holder.temp004=(TextView) converView.findViewById(R.id.tv_temp_004);
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
				holder.tv_lost_type.setText("完璧归赵");
				holder.tv_lost_type.setTextColor(getResources().getColor(R.color.GREENlIGHT));
			}
			if (mLecturesList.get(posi).getType().equals("1")) {
				holder.temp001.setText("拾获物品: ");
				holder.temp004.setText("拾获地点: ");
				holder.temp002.setText("拾获时间: ");
			} else if (mLecturesList.get(posi).getType().equals("2")) {
				holder.temp001.setText("丢失物品: ");
				holder.temp004.setText("丢失地点: ");
				holder.temp002.setText("丢失时间: ");
			}
			holder.tv_lost_phone.setText(mLecturesList.get(posi).getPhone());
			holder.tv_lost_name.setText(mLecturesList.get(posi).getName());
			holder.tv_lost_place.setText(mLecturesList.get(posi).getPlace());
			holder.tv_lost_time.setText(PubMehods.getFormatDate(mLecturesList
					.get(posi).getLost_time(), "MM/dd  HH:mm"));
			holder.tv_lost_desc.setText(mLecturesList.get(posi).getDesc());
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
			final ArrayList<String> path = new ArrayList<String>();
			if (mLecturesList.get(posi).getPhoto_url().length() > 0
					&& mLecturesList.get(posi).getPhoto_url() != "") {
				String photo_urls[] = mLecturesList.get(posi).getPhoto_url()
						.split(",");
				for (int i = 0; i < photo_urls.length; i++) {
					path.add(photo_urls[i]);
				}
				holder.content_img_ll.setVisibility(View.VISIBLE);
				if (photo_urls.length == 1) {
					holder.content_img_ll.removeAllViews();
					LinearLayout horizonLayout = new LinearLayout(
							B_Side_Found_Search.this);
					RelativeLayout.LayoutParams params;
					ImageView image1 = new ImageView(B_Side_Found_Search.this);
					float density = getDensity(B_Side_Found_Search.this);
					WindowManager wm = (WindowManager) B_Side_Found_Search.this
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
							Intent intent = new Intent(B_Side_Found_Search.this,
									B_Side_Found_BigImage.class);
							intent.putStringArrayListExtra("path", path);
							intent.putExtra("num", 0);
							startActivity(intent);
							//
							// Intent intent = new
							// Intent(B_Side_Found_Search.this,
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
					LinearLayout horizonLayout = new LinearLayout(
							B_Side_Found_Search.this);
					RelativeLayout.LayoutParams params;
					float density = getDensity(B_Side_Found_Search.this);
					WindowManager wm = (WindowManager) B_Side_Found_Search.this
							.getSystemService(Context.WINDOW_SERVICE);
					int width = wm.getDefaultDisplay().getWidth();
					float imageLayWidth = width - (10 + 12 + 10 + 50) * density;
					for (int i = 0; i < photo_urls.length; i++) {
						params = new RelativeLayout.LayoutParams(
								(int) (imageLayWidth / 3),
								(int) (imageLayWidth / 3));
						ImageView image2 = new ImageView(
								B_Side_Found_Search.this);
						final int a = i;
						image2.setScaleType(ScaleType.CENTER_CROP);
						image2.setPadding((int) (4 * density),
								(int) (4 * density), (int) (4 * density),
								(int) (4 * density));
						horizonLayout.addView(image2, params);
						if(image2.getTag() == null){
						    PubMehods.loadServicePic(imageLoader,photo_urls[i],image2, options);
						    image2.setTag(photo_urls[i]);
						}else{
						    if(!image2.getTag().equals(photo_urls[i])){
						        PubMehods.loadServicePic(imageLoader,photo_urls[i],image2, options);
						        image2.setTag(photo_urls[i]);
						    }
						}
						image2.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent(
										B_Side_Found_Search.this,
										B_Side_Found_BigImage.class);
								intent.putStringArrayListExtra("path", path);
								intent.putExtra("num", a);
								startActivity(intent);

							}
						});
					}
					holder.content_img_ll.addView(horizonLayout);
				}
			}

			converView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(B_Side_Found_Search.this,
							B_Side_Found_Detail.class);
					Bundle mBundle = new Bundle();
					mBundle.putSerializable("content",
							(Serializable) mLecturesList.get(posi));
					intent.putExtras(mBundle);
					startActivity(intent);
				}
			});
			return converView;
		}

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
		TextView tv_lost_phone;
		LinearLayout content_img_ll;
		CircleImageView iv_photo;
		TextView temp001,temp002,temp004;
	}

	void display(){
		 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.hideSoftInputFromWindow(mSearchInput.getWindowToken(), 0);

	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (!firstLoad) {
			have_read_page = 1;
			getLectureList(have_read_page, key, false);
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
		drawable.stop();
		drawable=null;
		super.onDestroy();
	}

	public static float getDensity(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		return dm.density;
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
                            A_0_App.getInstance().showExitDialog(B_Side_Found_Search.this, AppStrStatic.kicked_offline());
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
			finish();
			overridePendingTransition(R.anim.animal_push_right_in_normal,
					R.anim.animal_push_right_out_normal);
			break;
		default:
			break;
		}
	}

	
}
