package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.GetBlacklistCallback;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_RongYun_True_Name;
import com.yuanding.schoolpass.service.Api.InterRongYunName;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月11日 下午6:55:38 黑名单列表
 */
public class B_Account_BlackList_Acy extends A_0_CpkBaseTitle_Navi {

	private View mLinerReadDataError,mLinerNoContent,liner_acy_list_whole_view,black_acy_loading;
	private boolean showSelect;
	private ListView lv_black_list;
	private MyAdapter adapter;
	private RelativeLayout rel_black_operating;
	/**
	 * 移除，取消
	 */
	private Button remove, cancel;
	private String userid = "";
	List<Cpk_RongYun_True_Name> list = new ArrayList<Cpk_RongYun_True_Name>();
	protected ImageLoader imageLoader;
	private DisplayImageOptions options;
	//private BitmapUtils bitmapUtils;
	private List<String> list_ids = new ArrayList<String>();
	private String ids="";
	private TextView select;
	
	 private LinearLayout home_load_loading;
	 private AnimationDrawable drawable;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_acc_blacklist);
		setTitleText("黑名单");
		
		liner_acy_list_whole_view = findViewById(R.id.liner_black_whole_view);
		mLinerReadDataError = findViewById(R.id.black_acy_load_error);
		mLinerNoContent = findViewById(R.id.black_acy_no_content);
		black_acy_loading = findViewById(R.id.black_acy_loading);
		
		home_load_loading = (LinearLayout) black_acy_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		TextView tv_blank_name = (TextView)mLinerNoContent.findViewById(R.id.tv_blank_name);
		ImageView iv_blank_por = (ImageView)mLinerNoContent.findViewById(R.id.iv_blank_por);
		iv_blank_por.setBackgroundResource(R.drawable.no_tongxunlu);
		tv_blank_name.setText("暂无~");
		
		imageLoader = A_0_App.getInstance().getimageLoader();
		options = A_0_App.getInstance().getOptions(R.drawable.ic_defalut_person_center, R.drawable.ic_defalut_person_center, R.drawable.ic_defalut_person_center);
		//bitmapUtils=A_0_App.getBitmapUtils(this, R.drawable.ic_defalut_person_center, R.drawable.ic_defalut_person_center);
		lv_black_list = (ListView) findViewById(R.id.lv_black_list);
		select=(TextView) findViewById(R.id.select);
		/**
		 * 移除取消事件
		 */
		remove = (Button) findViewById(R.id.btn_black_move);
		cancel = (Button) findViewById(R.id.btn_black_cancel);
		remove.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (list.size()>0) {
					if(list.size()==list_ids.size())
					{
						showLoadResult(false, false, false, true);
						showTitleBt(ZUI_RIGHT_BUTTON, false);
					}
					for (int i = 0; i < list.size(); i++) {
						if (ids.contains(list.get(i).getTargetId())) {
							final int postion=i;
							RongIM.getInstance().removeFromBlacklist(list.get(i).getTargetId(),
									new RongIMClient.OperationCallback() {

										@Override
										public void onError(ErrorCode arg0) {
										}

										@Override
										public void onSuccess() {
											list.remove(0);
											isChice = new boolean[list.size()];
											for (int i = 0; i < list.size(); i++) {
												isChice[i] = false;
											}

											new Thread(mRunnable).start();
										}
									});
						}
						
					}
					rel_black_operating.setVisibility(View.GONE);
					showSelect = false;
					list_ids.clear();
					adapter.notifyDataSetChanged();
				}
			}

		});

		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				
				rel_black_operating.setVisibility(View.GONE);
				showSelect = false;
				if (list.size()>0) {
					
					for (int i = 0; i < list.size(); i++) {
						if (isChice[i]==true) {
							adapter.chiceState(i);
							if (ids.contains(list.get(i).getTargetId())) {
								ids=ids.replace(list.get(i).getTargetId(), "");
								list_ids.remove(list.get(i).getTargetId());
							}
						}
						
					}
					
				}
				
			}
		});
		
		mLinerReadDataError.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showLoadResult(true,false, false, false);
				getBlackList();
			}
		});
		
		
		rel_black_operating = (RelativeLayout) findViewById(R.id.rel_black_operating);

		lv_black_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int posi,
					long arg3) {
				if (showSelect==true) {
					adapter.chiceState(posi);
					if (ids.contains(list.get(posi).getTargetId())) {
						ids=ids.replace(list.get(posi).getTargetId(), "");
						list_ids.remove(list.get(posi).getTargetId());
					}else{
						list_ids.add(list.get(posi).getTargetId());
						ids=ids+list.get(posi).getTargetId();
					}
					select.setText(list_ids.size()+"");
					}
				}
				
		});
		
		if (A_0_App.USER_STATUS.equals("2")) {
			getBlackList();
		} else {
			showLoadResult(false,false, false, true);
		}
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
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
			black_acy_loading.setVisibility(View.VISIBLE);
			drawable.start();	
		}
		else
			{black_acy_loading.setVisibility(View.GONE);
			if (drawable!=null) {
        		drawable.stop();
			}
			}
			
	}
	
	// 获得黑名单
	private void getBlackList() {
	RongIM.getInstance().getBlacklist(new GetBlacklistCallback() {
				@Override
				public void onSuccess(String[] arg0) {//此处为Uniqid
					
					
					try {
						if(arg0 != null && arg0.length > 0){
							for (int i = 0; i < arg0.length; i++) {
								userid += arg0[i] + ",";
							}
//							showTitleBt(TEXT_BUTTON, true);
//							setTextBtn("管理");
					        setZuiRightBtn(R.drawable.navigationbar_save);
							showTitleBt(ZUI_RIGHT_BUTTON, true);
							showLoadResult(false,true, false, false);
							getRongYunNipick(userid);
						}else{
							showLoadResult(false,false, false, true);
						}
						
					} catch (Exception e) {
						showLoadResult(false,false, true, false);
					}
				}

				@Override
				public void onError(ErrorCode arg0) {
					showLoadResult(false,false, true, false);
					PubMehods.showToastStr(B_Account_BlackList_Acy.this, "网络错误，获取失败");
				}
			});

	}

	private void getRongYunNipick(final String temp) {
		String str = temp.substring(0, temp.length() - 1);
		A_0_App.getApi().getRongYunName(B_Account_BlackList_Acy.this, str,
				A_0_App.USER_TOKEN, new InterRongYunName() {
					@Override
					public void onSuccess(List<Cpk_RongYun_True_Name> mList) {
		                if (isFinishing())
		                    return;
						list.addAll(mList);
						adapter = new MyAdapter(list.size());
						lv_black_list.setAdapter(adapter);
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
//                      adapter.notifyDataSetChanged();
                        showLoadResult(false,false, true, false);
                        PubMehods.showToastStr(B_Account_BlackList_Acy.this,"获取数据失败");

                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });

	}

	/*
     */
	private Runnable mRunnable = new Runnable() {
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
					mHandler.sendMessage(mHandler.obtainMessage());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			adapter.notifyDataSetChanged();

		};
	};
	private boolean isChice[];

	public class MyAdapter extends BaseAdapter {

		public MyAdapter(int count) {
			isChice = new boolean[count];
			for (int i = 0; i < count; i++) {
				isChice[i] = false;
			}
		}

		@Override
		public int getCount() {
			if (list != null)
				return list.size();
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
		public View getView(int position, View converView, ViewGroup arg2) {
			if (converView == null) {
				converView = LayoutInflater.from(B_Account_BlackList_Acy.this)
						.inflate(R.layout.item_acc_balcklist, null);
			}
			TextView name = (TextView) converView
					.findViewById(R.id.tv_black_list_name);
			CircleImageView por = (CircleImageView) converView
					.findViewById(R.id.iv_black_list_por);
			ImageView state = (ImageView) converView
					.findViewById(R.id.iv_black_lsit_check);
			if (showSelect) {
				state.setVisibility(View.VISIBLE);
				if (isChice[position]) {
					state.setBackgroundResource(R.drawable.register_box_selected);
				} else {
					state.setBackgroundResource(R.drawable.register_box_unselected);
				}
			} else {
				state.setVisibility(View.GONE);
			}
			if (position % 8 == 0) {
				por.setBackgroundResource(R.drawable.photo_one);
				
			} else if (position % 8 == 1) {
				por.setBackgroundResource(R.drawable.photo_two);
			} else if (position % 8 == 2) {
				por.setBackgroundResource(R.drawable.photo_three);
			} else if (position % 8 == 3) {
				por.setBackgroundResource(R.drawable.photo_four);
			} else if (position % 8 == 4) {
				por.setBackgroundResource(R.drawable.photo_five);
			} else if (position % 8 == 5) {
				por.setBackgroundResource(R.drawable.photo_six);
			} else if (position % 8 == 6) {
				por.setBackgroundResource(R.drawable.photo_seven);
			} else if (position % 8 == 7) {
				por.setBackgroundResource(R.drawable.photo_eight);
			}
			PubMehods.loadServicePic(imageLoader,list.get(position).getPhoto_url(), por,options);
			//bitmapUtils.display(por, list.get(position).getPhoto_url());
			name.setText(list.get(position).getName());
			if(A_0_App.isShowAnimation==true){
			if(position>A_0_App.black_curPosi)
				
			 {
				A_0_App.black_curPosi=position;
				Animation an=new TranslateAnimation(Animation.RELATIVE_TO_SELF,1, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				an.setDuration(400);
				an.setStartOffset(50*position);
			    converView.startAnimation(an);
			 }
			}
			return converView;
		}

		public void chiceState(int post) {
			
			isChice[post] = isChice[post] == true ? false : true;

			this.notifyDataSetChanged();
		}

		// private String getCateIDString() {
		// String str = "";
		// boolean firstData = false;
		// for (int i = 0; i < isChice.length; i++) {
		// if (isChice[i]) {
		// if (!firstData) {
		// firstData = true;
		// str += mlist.get(i).getCate_id();
		// } else {
		// str += "," + mlist.get(i).getCate_id();
		// }
		// }
		// }
		// return str;
		// }

	}

	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;
		case ZUI_RIGHT_BUTTON:
          
			if (!showSelect) {
				rel_black_operating.setVisibility(View.VISIBLE);
				showSelect = true;
				// adapter.notifyDataSetChanged();
			} else {
				rel_black_operating.setVisibility(View.GONE);
				showSelect = false;
				// adapter.notifyDataSetChanged();
			}
			select.setText("0");
			break;
		default:
			break;
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
                    //A_0_App.getInstance().showExitDialog(B_Account_BlackList_Acy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Account_BlackList_Acy.this, AppStrStatic.kicked_offline());
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
		// TODO Auto-generated method stub
		//bitmapUtils=null;
		drawable.stop();
		drawable=null;
		super.onDestroy();
	}

}
