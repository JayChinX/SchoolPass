package com.yuanding.schoolpass.bangbang;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.R;
import com.yuanding.schoolpass.bean.Befriend_Task_List;
import com.yuanding.schoolpass.service.Api.InterHelpTaskList;
import com.yuanding.schoolpass.service.Api.InterReportTaskSent;
import com.yuanding.schoolpass.service.Api.InterSideHelpGetTaskSent;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author qinxiaojie 
 * @version 创建时间：2017年1月13日 身边帮帮首页 任务列表
 */
public class B_Side_Befriend_A0_List_Fragment extends Fragment {
	private View view;
	private SimpleSwipeRefreshLayout simpleSwipeRefresh;
	private PullToRefreshListView pullToRefresh;
	private int repfresh = 0;// 避免下拉和上拉冲突

	private LinearLayout tishiLine, refresh;// 失败提示

	private int have_read_page = 1;
	public static List<Befriend_Task_List> mTaskList;
	private B_Side_Befriend_B0_Task_Details_Dialog detailsDialog;

	private TaskHelpAdapter taskHelpAdapter;
	private String token = A_0_App.USER_TOKEN;
	protected boolean havaSuccessLoadData = false;
	protected Object jsonObject;

	private AnimationDrawable drawable;
	private LinearLayout side_lecture_detail_loading, home_load_loading;
	private boolean yishenhe = false;
	private String shenhetishi;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		view = inflater.inflate(R.layout.help_task_for_help_task_list_fragment, container, false);
		if (A_0_App.USER_STATUS.equals("2")) {
			yishenhe = true;
		}else {
			switch (A_0_App.USER_STATUS) {
			case "0"://审核中
				shenhetishi = "您的认证资料正在审核中，无法使用该功能，请耐心等待。";
				break;
			case "5"://审核失败
				shenhetishi = "此功能需要您提交个人认证资料。";
				break;
			case AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO://未认证
				shenhetishi = "此功能需要您提交个人认证资料。";
				break;

			}
		}
		simpleSwipeRefresh = (SimpleSwipeRefreshLayout) view.findViewById(R.id.for_help_swiperefreshlayout_fragment);
		pullToRefresh = (PullToRefreshListView) view.findViewById(R.id.for_help_ptr_list_fragment);
		tishiLine = (LinearLayout) view.findViewById(R.id.for_help_tishi);
		refresh = (LinearLayout) view.findViewById(R.id.for_help_list_whole_view);

		side_lecture_detail_loading = (LinearLayout) view.findViewById(R.id.for_help_loading);

		home_load_loading = (LinearLayout) side_lecture_detail_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start();

		initData();
		return view;
	}

	private void initData() {
		// TODO Auto-generated method stub
		// 1帮我取 2帮我送 3 教技能 4修东西 99帮其他
		mTaskList = new ArrayList<Befriend_Task_List>();
		taskHelpAdapter = new TaskHelpAdapter(getActivity());
		// 获取待领取任务列表 参数为 页数
		getLectureList(have_read_page);

		/*****************************************
		 * 下拉刷新 上拉加载
		 */
		pullToRefresh.setMode(Mode.BOTH);
		pullToRefresh.setAdapter(taskHelpAdapter);

		pullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// 下拉刷新
				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				have_read_page = 1;
				getLectureList(have_read_page);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

				if (repfresh == 0) {
					repfresh = 1;
					simpleSwipeRefresh.setEnabled(false);
					simpleSwipeRefresh.setRefreshing(false);
					getMoreLectureList(have_read_page, false);
				}
			}

		});

		/**
		 * 新增下拉使用 new add
		 */
		simpleSwipeRefresh.setSize(SwipeRefreshLayout.DEFAULT);
		simpleSwipeRefresh.setColorSchemeResources(R.color.main_color);
		if (repfresh == 0) {
			repfresh = 1;
			if (null != pullToRefresh) {
				pullToRefresh.onRefreshComplete();
			}
			simpleSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {

					have_read_page = 1;
					if (null != pullToRefresh) {
						pullToRefresh.setMode(Mode.DISABLED);
					}

					getLectureList(have_read_page);

				};
			});
		}

		pullToRefresh.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				if (simpleSwipeRefresh != null && pullToRefresh.getChildCount() > 0
						&& pullToRefresh.getRefreshableView().getFirstVisiblePosition() == 0
						&& pullToRefresh.getChildAt(0).getTop() >= pullToRefresh.getPaddingTop()) {
					// 解决滑动冲突，当滑动到第一个item，下拉刷新才起作用
					repfresh = 0;
					simpleSwipeRefresh.setEnabled(true);
				} else {
					simpleSwipeRefresh.setEnabled(false);
				}

			}

			@Override
			public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		});

	}

	protected void getLectureList(int i) {
		// TODO Auto-generated method stub
		// A_0_App.getInstance().showProgreDialog(getActivity(), "", true);
		if (A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
			return;
		A_0_App.getApi().getTaskList(getActivity(), A_0_App.USER_TOKEN, String.valueOf(i), new InterHelpTaskList() {
			@Override
			public void onSuccess(List<Befriend_Task_List> mList) {
				if (mTaskList == null)

					return;
				if (getActivity() == null || getActivity().isFinishing())
					return;
				mTaskList = new ArrayList<Befriend_Task_List>();
				if (mList != null && mList.size() > 0) {
					have_read_page = 2;
					mTaskList = mList;
					//
					if (taskHelpAdapter != null) {
						taskHelpAdapter.notifyDataSetChanged();
					}
					tishiLine.setVisibility(View.GONE);
					refresh.setVisibility(View.VISIBLE);
					//
				} else {
					mTaskList = mList;
					if (taskHelpAdapter != null) {
						taskHelpAdapter.notifyDataSetChanged();
					}
					tishiLine.setVisibility(View.VISIBLE);
					refresh.setVisibility(View.GONE);
					simpleSwipeRefresh.setEnabled(false);
				}
				simpleSwipeRefresh.setRefreshing(false);

				if (null != pullToRefresh) {
					pullToRefresh.onRefreshComplete();
					pullToRefresh.setMode(Mode.PULL_UP_TO_REFRESH);

				}
				repfresh = 0;
				if (drawable != null) {
					drawable.stop();
					side_lecture_detail_loading.setVisibility(View.GONE);
				}
			}
		}, new Inter_Call_Back() {

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(String msg) {
				if(getActivity()==null||getActivity().isFinishing())
                    return;
				 PubMehods.showToastStr(getActivity(), msg);
				//// showLoadResult(false, false, true, false);
				simpleSwipeRefresh.setRefreshing(false);
				if (pullToRefresh != null) {
					pullToRefresh.onRefreshComplete();
					pullToRefresh.setMode(Mode.PULL_UP_TO_REFRESH);
				}
				//
				// } else {
				//// showInfo(jsonObject);
				// }

				if (mTaskList == null) {
					tishiLine.setVisibility(View.VISIBLE);
					refresh.setVisibility(View.GONE);
					if (taskHelpAdapter != null) {
						taskHelpAdapter.notifyDataSetChanged();
					}
				}
				if (drawable != null) {
					drawable.stop();
					side_lecture_detail_loading.setVisibility(View.GONE);
				}
				repfresh = 0;

			}

			@Override
			public void onCancelled() {
				// TODO Auto-generated method stub

			}
		});
	}

	private void getMoreLectureList(int page_no, final boolean noRefresh) {

		A_0_App.getApi().getTaskList(getActivity(), token, String.valueOf(page_no), new InterHelpTaskList() {
			@Override
			public void onSuccess(List<Befriend_Task_List> mList) {
				if (mTaskList == null)
					return;
				if (getActivity() == null || getActivity().isFinishing())
					return;
				if (mList != null && mList.size() > 0) {
					have_read_page++;

					for (int i = 0; i < mList.size(); i++) {
						mTaskList.add(mList.get(i));
					}
					if (taskHelpAdapter != null) {
						taskHelpAdapter.notifyDataSetChanged();
					}
					// showLoadResult(false, true, false, false);
					tishiLine.setVisibility(View.GONE);
					if (noRefresh) {

						PubMehods.showToastStr(getActivity(), "刷新成功");
					}
					//
				} else {
					// tishiTask.setVisibility(View.VISIBLE);
					if (noRefresh) {

						PubMehods.showToastStr(getActivity(), "刷新失败");
					} else {
						PubMehods.showToastStr(getActivity(), "没有更多了");
						simpleSwipeRefresh.setRefreshing(false);
					}

					//

				}
				if (null != pullToRefresh) {
					pullToRefresh.onRefreshComplete();
					pullToRefresh.setMode(Mode.PULL_UP_TO_REFRESH);
				}

				simpleSwipeRefresh.setRefreshing(false);
				repfresh = 0;
			}
		}, new Inter_Call_Back() {

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(String msg) {
				if (mTaskList == null)
					return;
				if (getActivity() == null || getActivity().isFinishing())
					return;
				PubMehods.showToastStr(getActivity(), msg);

				if (null != pullToRefresh) {
					pullToRefresh.onRefreshComplete();
					pullToRefresh.setMode(Mode.PULL_UP_TO_REFRESH);
				}

				simpleSwipeRefresh.setRefreshing(false);
				repfresh = 0;
				if (drawable != null) {
					drawable.stop();
					side_lecture_detail_loading.setVisibility(View.GONE);
				}
			}

			@Override
			public void onCancelled() {
				// TODO Auto-generated method stub

			}
		});

	}

	protected void clearBusinessList() {
		// TODO Auto-generated method stub
		if (mTaskList != null && mTaskList.size() > 0) {
			mTaskList.clear();
		}
	}

	/**************
	 * 领取任务 点击我去帮时的动作
	 */
	public void sendGetTask(String id, final int position) {
		// A_0_App.getInstance().showProgreDialog(getActivity(), "", true);

		A_0_App.getApi().SideHelpGetTaskSent(token, id, new InterSideHelpGetTaskSent() {

			@Override
			public void onSuccess(int result, String sId) {
				// TODO Auto-generated method stub
				// if(isFinishing())
				// return;
				// A_0_App.getInstance().CancelProgreDialog(getActivity());
				// Intent intent1 = new Intent("notice");
				// sendBroadcast(intent1);
				//
				// PubMehods.showToastStr(getActivity(), "提交成功！");

				switch (result) {
				case 0:

					Intent intent = new Intent(getActivity(), B_Side_Befriend_C1_Task_Status_Details.class);
					intent.putExtra("type", 5);
					intent.putExtra("id", sId);
					startActivity(intent);
					
					break;

				case 1:
					// 被抢帮
					/// 领取失败警告框
					final B_Side_Befriend_B0_Take_Result_Dialog taskSBDialog = new B_Side_Befriend_B0_Take_Result_Dialog(
							getActivity(), R.style.Theme_dim_Dialog, 2,null);
					taskSBDialog.show();
					taskSBDialog.setClicklistener(new B_Side_Befriend_B0_Take_Result_Dialog.ClickListenerInterface() {

						@Override
						public void doConfirm() {
							// TODO Auto-generated method stub

						}

						@Override
						public void doCancel() {
							// TODO Auto-generated method stub
							taskSBDialog.dismiss();
							
							if (mTaskList.size() - 1 > 0) {
								mTaskList.remove(position);
								taskHelpAdapter.notifyDataSetChanged();
							}else {
								have_read_page = 1;
								getLectureList(have_read_page);
							}
						}
					});
					break;
				case 2:
					// 提示不能领取自己发布的任务
					// 提示框
					final B_Side_Befriend_B0_Take_Result_Dialog b0_Take_Result_Dialog = new B_Side_Befriend_B0_Take_Result_Dialog(
							getActivity(), R.style.Theme_dim_Dialog, 9,null);
					b0_Take_Result_Dialog.show();
					b0_Take_Result_Dialog
							.setClicklistener(new B_Side_Befriend_B0_Take_Result_Dialog.ClickListenerInterface() {

								@Override
								public void doConfirm() {
									// TODO Auto-generated method stub
									b0_Take_Result_Dialog.dismiss();
								}

								@Override
								public void doCancel() {
									// TODO Auto-generated method stub
									b0_Take_Result_Dialog.dismiss();
								}
							});
					break;
				case 3:
					// 过期任务

					final B_Side_Befriend_B0_Take_Result_Dialog taskTimeDialog = new B_Side_Befriend_B0_Take_Result_Dialog(
							getActivity(), R.style.Theme_dim_Dialog, 3,null);
					taskTimeDialog.show();
					taskTimeDialog.setClicklistener(new B_Side_Befriend_B0_Take_Result_Dialog.ClickListenerInterface() {

						@Override
						public void doConfirm() {
							// TODO Auto-generated method stub

						}

						@Override
						public void doCancel() {
							// TODO Auto-generated method stub
							taskTimeDialog.dismiss();
							
							if (mTaskList.size() - 1 > 0) {
								mTaskList.remove(position);
								taskHelpAdapter.notifyDataSetChanged();
							}else {
								have_read_page = 1;
								getLectureList(have_read_page);
							}
						}
					});
					break;
				}

			}
		}, new Inter_Call_Back() {

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(String msg) {
				// TODO Auto-generated method stub
				// A_0_App.getInstance().CancelProgreDialog(getActivity());
				if(getActivity()==null||getActivity().isFinishing())
                    return;
				 PubMehods.showToastStr(getActivity(), msg);

			}

			@Override
			public void onCancelled() {
				// TODO Auto-generated method stub

			}
		});
	}
	/**
	 * 
	 * 推送的任务列表适配 taskForHelpFormList
	 */
	public class TaskHelpAdapter extends BaseAdapter {

		Context context;

		public TaskHelpAdapter(Context context) {
			// TODO Auto-generated constructor stub
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mTaskList != null ? mTaskList.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mTaskList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.item_help_task_for_help_taskes, parent,
						false);
				holder = new ViewHolder();
				holder.task_phone = (ImageView) convertView.findViewById(R.id.for_help_task_photo);
				holder.task_money = (TextView) convertView.findViewById(R.id.for_help_task_money);
				holder.task_name = (TextView) convertView.findViewById(R.id.for_help_task_name);
				holder.task_namer = (TextView) convertView.findViewById(R.id.for_help_task_namer);
				holder.task_take_address = (TextView) convertView.findViewById(R.id.for_help_task_take);
				holder.task_send_address = (TextView) convertView.findViewById(R.id.for_help_task_send);
				holder.task_time = (TextView) convertView.findViewById(R.id.for_help_task_data);
				holder.task_data = (TextView) convertView.findViewById(R.id.for_help_task_time);
				holder.task_send_add = (LinearLayout) convertView.findViewById(R.id.for_help_task_send_address);
				holder.task_take_add = (LinearLayout) convertView.findViewById(R.id.for_help_task_address);
				holder.getTask = (Button) convertView.findViewById(R.id.for_help_get_task);
				convertView.setTag(holder);//btn_get_yanzheng_code_error
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (mTaskList != null && mTaskList.size() > 0) {
				if(mTaskList.get(position).getTaskStatus().equals("1")){
					holder.getTask.setBackgroundResource(R.drawable.btn_huizhi_queren);
				}else{
					holder.getTask.setBackgroundResource(R.drawable.btn_get_yanzheng_code_error);
				}
				final Befriend_Task_List helpTask = mTaskList.get(position);
				if (helpTask.getItems() != null && helpTask.getItems().size() > 0) {
					
					DecimalFormat df = new DecimalFormat("0.00");
					double money = (double)helpTask.getTotalAmount()/100.00;
					holder.task_money.setText("￥" + df.format(money));
//					holder.task_money.setText("￥" + (double)(Math.round(helpTask.getTotalAmount())/100.0) );
//					if (helpTask.getTotalAmount() > 0) {
////						float money = (float) helpTask.getTotalAmount() / 100;
//						holder.task_money.setText("￥" + (double)(Math.round(helpTask.getTotalAmount())/100.0) );
//					} else {
//						holder.task_money.setText("￥" + helpTask.getTotalAmount() + ".00");
//					}
					Log.i("任务酬金", "" + helpTask.getTotalAmount());
					;
					holder.task_namer.setText(helpTask.getItems().get(0).getContent());
					holder.task_name.setText(helpTask.getItems().get(0).getTitle() + "：");

					switch (helpTask.getType()) {
					case 1:
						holder.task_send_add.setVisibility(View.VISIBLE);
						holder.task_take_add.setVisibility(View.VISIBLE);
						holder.task_phone.setImageResource(R.drawable.bangbang_ico_q);

						holder.task_take_address.setText(helpTask.getItems().get(1).getContent());
						holder.task_send_address.setText(helpTask.getItems().get(2).getContent());
						holder.task_time.setText(helpTask.getItems().get(3).getTitle() + "：");
						holder.task_data.setText(times(helpTask.getItems().get(3).getContent()));

						break;
					case 2:
						holder.task_send_add.setVisibility(View.VISIBLE);
						holder.task_take_add.setVisibility(View.VISIBLE);
						holder.task_phone.setImageResource(R.drawable.bangbang_ico_s);
						holder.task_take_address.setText(helpTask.getItems().get(1).getContent());
						holder.task_send_address.setText(helpTask.getItems().get(2).getContent());
						holder.task_time.setText(helpTask.getItems().get(3).getTitle() + "：");
						holder.task_data.setText(times(helpTask.getItems().get(3).getContent()));
						break;
					case 3:
						holder.task_send_add.setVisibility(View.GONE);
						holder.task_take_add.setVisibility(View.GONE);

						holder.task_phone.setImageResource(R.drawable.bangbang_ico_j);
						holder.task_time.setText(helpTask.getItems().get(1).getTitle() + "：");
						holder.task_data.setText(times(helpTask.getItems().get(1).getContent()));
						break;
					case 4:
						holder.task_send_add.setVisibility(View.GONE);
						holder.task_take_add.setVisibility(View.GONE);

						holder.task_phone.setImageResource(R.drawable.bangbang_ico_x);
						holder.task_time.setText(helpTask.getItems().get(1).getTitle() + "：");
						holder.task_data.setText(times(helpTask.getItems().get(1).getContent()));
						break;
					case 99:
						holder.task_send_add.setVisibility(View.GONE);
						holder.task_take_add.setVisibility(View.GONE);

						holder.task_phone.setImageResource(R.drawable.bangbang_ico_t);
						holder.task_time.setText(helpTask.getItems().get(1).getTitle() + "：");
						holder.task_data.setText(times(helpTask.getItems().get(1).getContent()));
						break;
					}
					holder.getTask.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (mTaskList.size() > 0&&mTaskList.get(position).getTaskStatus().equals("1")) {
								if (yishenhe) {
									GetTask(position);
								}else {
									PubMehods.showToastStr(getActivity(), shenhetishi);
								}
							}
						}
					}
					);
				}
			}
			convertView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (mTaskList.size() > 0&&mTaskList.get(position).getTaskStatus().equals("1")) {
						if (yishenhe) {

							GetTask(position);
						}else {
							PubMehods.showToastStr(getActivity(), shenhetishi);
						}
					}
				
				}
			});

			return convertView;
		}

		public void GetTask(final int position) {
			if (mTaskList == null ) {
				return;
			}
			final String id = mTaskList.get(position).getId();

			detailsDialog = new B_Side_Befriend_B0_Task_Details_Dialog(getActivity(), R.style.Theme_dim_Dialog,
					position);
			detailsDialog.show();
			detailsDialog.setClicklistener(new B_Side_Befriend_B0_Task_Details_Dialog.ClickListenerInterface() {

				@Override
				public void doReport() {
					// TODO Auto-generated method stub
					// 任务详情弹窗
					if (detailsDialog != null) {
						detailsDialog.dismiss();
					}
					// 举报
					final B_Side_Befriend_C0_Task_Report_Dialog reportDialog = new B_Side_Befriend_C0_Task_Report_Dialog(
							getActivity(), R.style.Theme_dim_Dialog);
					reportDialog.show();
					reportDialog.setClicklistener(new B_Side_Befriend_C0_Task_Report_Dialog.ClickListenerInterface() {

						@Override
						public void doConfirm() {
							// TODO Auto-generated method stub
							if (reportDialog != null) {
								reportDialog.dismiss();
							}
						}

						@Override
						public void doCancel(String report) {
							// TODO Auto-generated method stub
							// PubMehods.showToastStr(getContext(), "举报任务id +
							// ......." + id);
							A_0_App.getApi().ReportTaskSent(token, id, report, new InterReportTaskSent() {
								@Override
								public void onSuccess(String msg) {
									// TODO Auto-generated method stub
									if (reportDialog != null) {
										reportDialog.dismiss();
									}
									// 举报成功提示框
									final B_Side_Befriend_B0_Take_Result_Dialog b0_Take_Result_Dialog = new B_Side_Befriend_B0_Take_Result_Dialog(
											getActivity(), R.style.Theme_dim_Dialog, 8,msg);
									b0_Take_Result_Dialog.show();
									b0_Take_Result_Dialog.setClicklistener(
											new B_Side_Befriend_B0_Take_Result_Dialog.ClickListenerInterface() {

												@Override
												public void doConfirm() {
													// TODO Auto-generated
													// method stub
													b0_Take_Result_Dialog.dismiss();
//													if (mTaskList.size() - 1 > 0) {
//														mTaskList.remove(position);
//														taskHelpAdapter.notifyDataSetChanged();
//													}else {
														have_read_page = 1;
														getLectureList(have_read_page);
													//}
												}

												@Override
												public void doCancel() {
													// TODO Auto-generated
													// method stub
													b0_Take_Result_Dialog.dismiss();
//													if (mTaskList.size() - 1 > 0) {
//														mTaskList.remove(position);
//														taskHelpAdapter.notifyDataSetChanged();
//													}else {
														have_read_page = 1;
														getLectureList(have_read_page);
													//}
												}
											});

								}
							}, new Inter_Call_Back() {
								@Override
								public void onFinished() {
									// TODO Auto-generated method stub
								}

								@Override
								public void onFailure(String msg) {
									// TODO Auto-generated method stub
									if(getActivity()==null||getActivity().isFinishing())
					                    return;
									 PubMehods.showToastStr(getActivity(), msg);
								}

								@Override
								public void onCancelled() {
									// TODO Auto-generated method stub

								}
							});
						}
					});
				}

				@Override
				public void doConfirm() {
					// TODO Auto-generated method stub
					// 取消
					detailsDialog.dismiss();
				}

				@Override
				public void doCancel() {
					// TODO Auto-generated method stub
					// 我去帮
					detailsDialog.dismiss();
					sendGetTask(id, position);
				}
			});
		}

		String one;

		public String times(String time) {
			@SuppressWarnings("unused")
			long lcc = Long.valueOf(time);
			int i = Integer.parseInt(time);
			Log.i("时间戳", i + "");
			Date date = new Date(lcc * 1000);
			SimpleDateFormat sd = new SimpleDateFormat("MM/dd HH:mm");
			one = sd.format(date);
			return one;

		}

		public class ViewHolder {
			ImageView task_phone;
			TextView task_money, task_name, task_namer, task_take_address, task_send_address, task_time, task_data;
			LinearLayout task_take_add, task_send_add;
			Button getTask;
		}

	}
}
