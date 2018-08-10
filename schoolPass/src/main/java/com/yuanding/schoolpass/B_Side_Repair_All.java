package com.yuanding.schoolpass;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolpass.bean.Cpk_Side_Repair_List;
import com.yuanding.schoolpass.service.Api.InteRepairList;
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
 * @version 创建时间：2015年11月12日 下午3:18:18 报修————报修————报修全部
 * 
 * 废弃
 */
public class B_Side_Repair_All extends Fragment {

	private View mLinerReadDataError, mLinerNoContent,
			liner_lecture_list_whole_view, side_lecture__loading;
	private PullToRefreshListView mPullDownView;
	private List<Cpk_Side_Repair_List> mLecturesList;
	private Mydapter adapter;
	private int have_read_page = 1;// 已经读的页数
	private Boolean firstLoad = false;
	private ACache maACache;
	private JSONObject jsonObject;
	protected Context mContext;
	private View viewone;

	/**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
	
	public B_Side_Repair_All() {
		super();
	}
	private boolean havaSuccessLoadData = false;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity.getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewone = inflater.inflate(R.layout.activity_side_repair, container,
				false);
		firstLoad = true;
		 demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)viewone.findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		liner_lecture_list_whole_view = viewone
				.findViewById(R.id.liner_lecture_list_whole_view);
		mPullDownView = (PullToRefreshListView) viewone
				.findViewById(R.id.lv_side_lecture_list);
		side_lecture__loading = viewone
				.findViewById(R.id.side_lecture__loading);
		mLinerReadDataError = viewone
				.findViewById(R.id.side_lecture_load_error);
		mLinerNoContent = viewone.findViewById(R.id.side_lecture_no_content);
		ImageView iv_blank_por = (ImageView) mLinerNoContent
				.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView) mLinerNoContent
				.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.no_baoxiu);
		tv_blank_name.setText("暂无报修项目~");

		mLinerReadDataError.setOnClickListener(onClick);

		mLecturesList = new ArrayList<Cpk_Side_Repair_List>();
		adapter = new Mydapter();
		mPullDownView.setMode(Mode.BOTH);
		mPullDownView.setAdapter(adapter);

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
						getRepairList(have_read_page, true);
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
							    mPullDownView.onRefreshComplete();
							    mPullDownView.setMode(Mode.DISABLED);
							}
							getRepairList(have_read_page, true);

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
			public void onItemClick(AdapterView<?> arg0, View arg1, int posi,
					long arg3) {
				if (posi == 0) {
					return;
				}
				Intent intent = new Intent(getActivity(),
						B_Side_Repair_Detail.class);
				// Bundle mBundle = new Bundle();
				// mBundle.putSerializable("content",(Serializable)
				// mLecturesList.get(posi-1));
				// intent.putExtras(mBundle);
				intent.putExtra("repair_id", mLecturesList.get(posi - 1)
						.getRepair_id());
				startActivity(intent);
			}
		});
		if (A_0_App.USER_STATUS.equals("5") || A_0_App.USER_STATUS.equals("0")) {
			showLoadResult(false, false, false, true);
		} else {
			readCache();
		}

		// getRepairList(have_read_page, false);
		return viewone;
	}

	private void readCache() {
		// TODO Auto-generated method stub
		maACache = ACache.get(getActivity());
		jsonObject = maACache
				.getAsJSONObject(AppStrStatic.cache_key_repair_all+A_0_App.USER_UNIQID);
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
			getRepairList(have_read_page, false);
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
		try {
			int state = jsonObject.getInt("status");
			List<Cpk_Side_Repair_List> mlistContact = new ArrayList<Cpk_Side_Repair_List>();
			if (state == 1) {
				mlistContact = JSON.parseArray(jsonObject.optJSONArray("rlist")
						+ "", Cpk_Side_Repair_List.class);
			}
			if (mLecturesList == null)
				return;
			havaSuccessLoadData = true;  
			if (mlistContact != null && mlistContact.size() > 0) {
				clearBusinessList();
				mLecturesList = mlistContact;
				adapter.notifyDataSetChanged();
				showLoadResult(false, true, false, false);
			} else {
				showLoadResult(false, false, false, true);
			}
            if (mPullDownView!=null) {
                mPullDownView.onRefreshComplete();
                mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
            }
            
            demo_swiperefreshlayout.setRefreshing(false);                       
            repfresh=0;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getRepairList(int page_no, final boolean pullRefresh) {
		A_0_App.getApi().getRepairList(getActivity(), A_0_App.USER_TOKEN,
				String.valueOf(page_no), "1", new InteRepairList() {
					@Override
					public void onSuccess(List<Cpk_Side_Repair_List> mList) {
						if (mLecturesList == null)
							return;
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
                        havaSuccessLoadData = true;  
						if (mList != null && mList.size() > 0) {
							have_read_page = 2;
							clearBusinessList();
							mLecturesList = mList;
							adapter.notifyDataSetChanged();
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
                        PubMehods.showToastStr(getActivity(), msg);
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
		A_0_App.getApi().getRepairList(getActivity(), A_0_App.USER_TOKEN,
				String.valueOf(page_no), "1", new InteRepairList() {
					public void onSuccess(List<Cpk_Side_Repair_List> mList) {
						if (mLecturesList == null)
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
							adapter.notifyDataSetChanged();
							//mPullDownView.getRefreshableView().setSelection(totleSize + 1);
						} else {
							PubMehods.showToastStr(getActivity(), "没有更多了");
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
                        if (mLecturesList == null)
                            return;
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
                        if (jsonObject == null) {
                            PubMehods.showToastStr(getActivity(), msg);
                            showLoadResult(false, false, true, false);
                            if(null!=mPullDownView){
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
		if (loading)
			side_lecture__loading.setVisibility(View.VISIBLE);
		else
			side_lecture__loading.setVisibility(View.GONE);
	}

	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.side_lecture_load_error:
				showLoadResult(true, false, false, false);
				clearBusinessList();
				have_read_page = 1;
				getRepairList(have_read_page, true);
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
			// TODO Auto-generated method stub
			return v;
		}

		@Override
		public long getItemId(int v) {
			// TODO Auto-generated method stub
			return v;
		}

		@Override
		public View getView(int posi, View converView, ViewGroup arg2) {
			ViewHolder holder;
			if (converView == null) {
				holder = new ViewHolder();
				converView = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_repair_list, null);
				holder.tv_repair_type = (TextView) converView
						.findViewById(R.id.tv_repair_type);
				holder.tv_repair_title = (TextView) converView
						.findViewById(R.id.tv_repair_title);
				holder.tv_repair_sort = (TextView) converView
						.findViewById(R.id.tv_repair_sort);
				holder.tv_repair_place = (TextView) converView
						.findViewById(R.id.tv_repair_place);
				holder.tv_repair_time = (TextView) converView
						.findViewById(R.id.tv_repair_time);
				holder.tv_repair_name = (TextView) converView
						.findViewById(R.id.tv_repair_name);
				holder.tv_repair_desc = (TextView) converView
						.findViewById(R.id.tv_repair_desc);
				converView.setTag(holder);
			} else {
				holder = (ViewHolder) converView.getTag();
			}
			if (mLecturesList.get(posi).getStatus().equals("2")) {
				holder.tv_repair_type.setText("[已受理]");
				holder.tv_repair_type.setTextColor(getResources().getColor(
						R.color.repair_qing));
			} else if (mLecturesList.get(posi).getStatus().equals("0")) {
				holder.tv_repair_type.setTextColor(getResources().getColor(
						R.color.repair_blue));
				holder.tv_repair_type.setText("[未受理]");
			} else if (mLecturesList.get(posi).getStatus().equals("3")) {
				holder.tv_repair_type.setTextColor(getResources().getColor(
						R.color.repair_grey));
				holder.tv_repair_type.setText("[已完工]");
			} else if (mLecturesList.get(posi).getStatus().equals("1")) {
				holder.tv_repair_type.setTextColor(getResources().getColor(
						R.color.repair_red));
				holder.tv_repair_type.setText("[已驳回]");
			}
			holder.tv_repair_title.setText(mLecturesList.get(posi).getTitle());
			holder.tv_repair_sort.setText(mLecturesList.get(posi)
					.getType_name());
			holder.tv_repair_place.setText(mLecturesList.get(posi).getPlace());
			holder.tv_repair_time.setText(PubMehods.getFormatDate(mLecturesList
					.get(posi).getCreate_time(), "MM/dd  HH:mm"));
			holder.tv_repair_name.setText(mLecturesList.get(posi)
					.getTrue_name());
			holder.tv_repair_desc.setText(mLecturesList.get(posi).getDetails());
			if(A_0_App.isShowAnimation==true){
			 if (posi > A_0_App.repair_all_list_curPosi) {
				A_0_App.repair_all_list_curPosi = posi;
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
			return converView;
		}

	}

	class ViewHolder {
		TextView tv_repair_type;
		TextView tv_repair_title;
		TextView tv_repair_sort;
		TextView tv_repair_place;
		TextView tv_repair_time;
		TextView tv_repair_name;
		TextView tv_repair_desc;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!firstLoad) {
			have_read_page = 1;
			getRepairList(have_read_page, false);
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
		super.onDestroy();
	}

}
