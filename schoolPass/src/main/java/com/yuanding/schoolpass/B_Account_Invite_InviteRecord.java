package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Account_InviteInfo_ISendInviteInfo;
import com.yuanding.schoolpass.service.Api.ISendInvitionListCallBack;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;




/**
 * 邀请记录列表
 * @author Administrator
 *
 */
public class B_Account_Invite_InviteRecord extends A_0_CpkBaseTitle_Navi{

	TextView tx_allinvitenum;
	PullToRefreshListView lv_inviterecord;
	InviteRecordAdapter mInviteRecordAdapter;
	
	int currentPageNo = 1;
	Context mContext;
	
	List<Cpk_Account_InviteInfo_ISendInviteInfo> mCurrentinviteRecordList;
	
	boolean isBindedAdapter = false;
	private View liner_account_invite_record_whole,liner_account_invite_record_error,liner_account_invite_record_loading;
	private ACache maACache;
	private JSONObject jsonObject;
	private boolean havaSuccessLoadData = false;
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
		setView(R.layout.activity_acc_invite_inviterecord);
		mContext = this;
		setTitleText(mContext.getResources().getString(R.string.str_my_account_invite_record_title));		
		initView();
		//getData();
		readCache();
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}
	
	private void initView() {
		tx_allinvitenum = getViewById(R.id.tx_allinvitenum);
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		lv_inviterecord = (PullToRefreshListView) findViewById(R.id.lv_inviterecord);	
		liner_account_invite_record_error=findViewById(R.id.acount_invite_record_load_error);
		liner_account_invite_record_loading=findViewById(R.id.acount_invite_record_loading);
		liner_account_invite_record_whole=findViewById(R.id.ll_account_invite_record_whole);
		
		home_load_loading = (LinearLayout) liner_account_invite_record_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		liner_account_invite_record_error.setOnClickListener(onClick);
		mCurrentinviteRecordList = new ArrayList<Cpk_Account_InviteInfo_ISendInviteInfo>();
		 mInviteRecordAdapter = new InviteRecordAdapter(mContext);
		 lv_inviterecord.setMode(Mode.BOTH);
//		 lv_inviterecord.setAdapter(mInviteRecordAdapter);
		 lv_inviterecord.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
				@Override
				public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
					getData();
				}

				@Override
				public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
					if (repfresh==0) {
						repfresh=1;
						demo_swiperefreshlayout.setEnabled(false);
						demo_swiperefreshlayout.setRefreshing(false);  
						currentPageNo+=1;
						getMore_Data();
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
				if(null!=lv_inviterecord){
					lv_inviterecord.onRefreshComplete();
	            }
				demo_swiperefreshlayout
						.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
							public void onRefresh() {
								currentPageNo = 1;
								if(null!=lv_inviterecord){
									lv_inviterecord.setMode(Mode.DISABLED);
								}
								
								getData();

							};
						});
			}
			
			
			lv_inviterecord.setOnScrollListener(new OnScrollListener() {
				
				@Override
				public void onScrollStateChanged(AbsListView arg0, int arg1) {
					 if (demo_swiperefreshlayout!=null&&lv_inviterecord.getChildCount() > 0 && lv_inviterecord.getRefreshableView().getFirstVisiblePosition() == 0
				                && lv_inviterecord.getChildAt(0).getTop() >= lv_inviterecord.getPaddingTop()) {
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
		 
		 String count = getIntent().getStringExtra("count");
		 tx_allinvitenum.setText("总计 （"+count+"）");
		 
	}
	private void readCache() {
		// TODO Auto-generated method stub
		maACache = ACache.get(this);
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_invite_record+A_0_App.USER_UNIQID);
        if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        } else {
            updateInfo();
        }
	}
	private void showInfo(JSONObject jsonObject) {
		// TODO Auto-generated method stub
	        havaSuccessLoadData = true;
			int state = jsonObject.optInt("status");
			List<Cpk_Account_InviteInfo_ISendInviteInfo> mlistContact = new ArrayList<Cpk_Account_InviteInfo_ISendInviteInfo>();
			JSONArray jsonArrayItem = jsonObject.optJSONArray("list");
			if (state == 1) {
                mlistContact = JSON.parseArray(jsonArrayItem + "", Cpk_Account_InviteInfo_ISendInviteInfo.class);      
            }
			if (mlistContact != null && mlistContact.size() > 0) {
				clearListview();
				mCurrentinviteRecordList.addAll(mlistContact);
				mInviteRecordAdapter.setInviteRecordList(mCurrentinviteRecordList);
				
				if(!isBindedAdapter){
					isBindedAdapter = true;
					lv_inviterecord.setAdapter(mInviteRecordAdapter);
				}else {
					mInviteRecordAdapter.notifyDataSetChanged();
				}			
				showLoadResult(false, true, false);
			} else {
				PubMehods.showToastStr(mContext,mContext.getResources().getString(R.string.str_my_account_invite_record_noloadmore_prompt));
				lv_inviterecord.setMode(Mode.DISABLED);
			}
            demo_swiperefreshlayout.setRefreshing(false);  
            if(null!=lv_inviterecord){
                lv_inviterecord.onRefreshComplete();
                lv_inviterecord.setMode(Mode.PULL_UP_TO_REFRESH);
            }
            repfresh=0;
		
	}
//	private void showToast(String s) {
//		Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
//
//	}
	
	private void clearListview() {
		mCurrentinviteRecordList.clear();
		mInviteRecordAdapter.setInviteRecordList(mCurrentinviteRecordList);
		lv_inviterecord.setAdapter(mInviteRecordAdapter);
		
		currentPageNo=1;
		isBindedAdapter = false;

	}
	
	private void getData() {
		A_0_App.getApi().reqISendInvitionList(A_0_App.USER_TOKEN, currentPageNo, new ISendInvitionListCallBack() {
			
			@Override
			public void onSuccessMySendInvitionInfo(List<Cpk_Account_InviteInfo_ISendInviteInfo> mMySendInvitionInfo) {
				if(isFinishing())
				    return;
				havaSuccessLoadData = true;
				if(null!=lv_inviterecord){
					lv_inviterecord.onRefreshComplete();
				}
				if(null!=mMySendInvitionInfo){
					clearListview();
					mCurrentinviteRecordList.addAll(mMySendInvitionInfo);
					mInviteRecordAdapter.setInviteRecordList(mCurrentinviteRecordList);
					
					if(!isBindedAdapter){
						isBindedAdapter = true;
						lv_inviterecord.setAdapter(mInviteRecordAdapter);
					}else {
						mInviteRecordAdapter.notifyDataSetChanged();
					}			
					showLoadResult(false, true, false);
				}
				
				if(null==mMySendInvitionInfo || mMySendInvitionInfo.size()==0){
					PubMehods.showToastStr(mContext,mContext.getResources().getString(R.string.str_my_account_invite_record_noloadmore_prompt));
				}
				
				demo_swiperefreshlayout.setRefreshing(false);  
				if(null!=lv_inviterecord){
					lv_inviterecord.onRefreshComplete();
					lv_inviterecord.setMode(Mode.PULL_UP_TO_REFRESH);
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
                if(isFinishing())
                    return;
                if(!havaSuccessLoadData){
                    showLoadResult(false,false, true);
                }
                PubMehods.showToastStr(B_Account_Invite_InviteRecord.this, msg);
                demo_swiperefreshlayout.setRefreshing(false);  
                if(null!=lv_inviterecord){
                    lv_inviterecord.onRefreshComplete();
                    lv_inviterecord.setMode(Mode.PULL_UP_TO_REFRESH);
                }
                repfresh=0;
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });

	}
	
	
	private void getMore_Data() {
       A_0_App.getApi().reqISendInvitionList(A_0_App.USER_TOKEN, currentPageNo, new ISendInvitionListCallBack() {
			
			@Override
			public void onSuccessMySendInvitionInfo(List<Cpk_Account_InviteInfo_ISendInviteInfo> mMySendInvitionInfo) {
                if(isFinishing())
                    return;
				if(null!=lv_inviterecord){
					lv_inviterecord.onRefreshComplete();
				}
				if(null!=mMySendInvitionInfo){
					clearListview();
					mCurrentinviteRecordList.addAll(mMySendInvitionInfo);
					mInviteRecordAdapter.setInviteRecordList(mCurrentinviteRecordList);
					
					if(!isBindedAdapter){
						isBindedAdapter = true;
						lv_inviterecord.setAdapter(mInviteRecordAdapter);
					}else {
						mInviteRecordAdapter.notifyDataSetChanged();
					}			
					showLoadResult(false, true, false);
				}
				
				if(null==mMySendInvitionInfo || mMySendInvitionInfo.size()==0){
					PubMehods.showToastStr(mContext,mContext.getResources().getString(R.string.str_my_account_invite_record_noloadmore_prompt));
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
                PubMehods.showToastStr(B_Account_Invite_InviteRecord.this, msg);
                if(null!=lv_inviterecord){
                    lv_inviterecord.onRefreshComplete();
                }
                
                repfresh=0;
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });


	}

	
	/**
	 * 邀请列表适配器
	 * @author Administrator
	 *
	 */
	public static class  InviteRecordAdapter extends BaseAdapter{
		
		List<Cpk_Account_InviteInfo_ISendInviteInfo> inviteRecordList;
		Context mContext;
		LayoutInflater mLayoutInflater;

		public InviteRecordAdapter(Context mContext) {
			super();
			this.mContext = mContext;
			mLayoutInflater = LayoutInflater.from(mContext);  
		}
		

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return inviteRecordList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return inviteRecordList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;  
	        if (convertView == null)  
	        {  
	            holder = new ViewHolder();  
	            convertView = mLayoutInflater.inflate(R.layout.item_acc_invite_record, null);  
	            holder.tx_invitedname = getViewById(convertView,R.id.tx_invitedname);
	            holder.tx_invitedaddr = getViewById(convertView,R.id.tx_invitedaddr);
	            holder.tx_data = getViewById(convertView,R.id.tx_data);
	            holder.tx_isrenzheng = getViewById(convertView, R.id.tx_isrenzheng);
	            holder.v_down_line = getViewById(convertView, R.id.v_down_line);
	            convertView.setTag(holder);  
	        } else {  
	            holder = (ViewHolder) convertView.getTag();  
	        }  
			
			if(null!=this.inviteRecordList && this.inviteRecordList.size()>0){
				holder.tx_invitedname.setText(this.inviteRecordList.get(position).getTrue_name());
				holder.tx_invitedaddr.setText(this.inviteRecordList.get(position).getOrgan_name());
				holder.tx_data.setText(PubMehods.getFormatDate(Long.parseLong(""+this.inviteRecordList.get(position).getCreate_time()),"yyyy/MM/dd"));
				if(Integer.parseInt(this.inviteRecordList.get(position).getStatus())!=2){
					holder.tx_isrenzheng.setText("（未认证）");
				}else{
//					holder.tx_isrenzheng.setText("（已认证）");
				}
				//是否是最后一个元素
				if(position == (this.inviteRecordList.size()-1)){
					 holder.v_down_line.setVisibility(View.GONE);
				}else{
					 holder.v_down_line.setVisibility(View.VISIBLE);
				}
			}
			return convertView;
		}
		
		class ViewHolder {
			TextView tx_invitedname;
			TextView tx_invitedaddr;
			TextView tx_data;
			TextView tx_isrenzheng;
			View v_down_line;
		}

		public List<Cpk_Account_InviteInfo_ISendInviteInfo> getInviteRecordList() {
			return inviteRecordList;
		}

		public void setInviteRecordList(List<Cpk_Account_InviteInfo_ISendInviteInfo> inviteRecordList) {
			this.inviteRecordList = inviteRecordList;
		}

		public Context getmContext() {
			return mContext;
		}

		public void setmContext(Context mContext) {
			this.mContext = mContext;
		}
		
	}
	
	/**
	 * 根据listview的显示个数，设置listview的高度
	 * setAdapter以后设置
	 * @param listView
	 */
	 public static void setListViewHeightBasedOnChildren(ListView listView) {  
	        // 获取ListView对应的Adapter  
	        ListAdapter listAdapter = listView.getAdapter();  
	        if(listAdapter == null) {  
	            return;  
	        }  
	        int totalHeight = 0;  
	        for(int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目  
	            View listItem = listAdapter.getView(i, null, listView);  
	            listItem.measure(0, 0); // 计算子项View 的宽高  
	            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度  
	        }  
	        ViewGroup.LayoutParams params = listView.getLayoutParams();  
	        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
	        // listView.getDividerHeight()获取子项间分隔符占用的高度  
	        // params.height最后得到整个ListView完整显示需要的高度  
	        listView.setLayoutParams(params);  
	    }  
	
	
	public static <T extends View> T getViewById(View convertView,int id) {
		return (T) convertView.findViewById(id);
	}
	public <T extends View> T getViewById(int id) {
		return (T) this.findViewById(id);
	}
	private void showLoadResult(boolean loading,boolean whole,boolean loadFaile) {
		if (whole)
			liner_account_invite_record_whole.setVisibility(View.VISIBLE);
		else
			liner_account_invite_record_whole.setVisibility(View.GONE);
		
		if (loadFaile)
			liner_account_invite_record_error.setVisibility(View.VISIBLE);
		else
			liner_account_invite_record_error.setVisibility(View.GONE);
		if(loading){
			drawable.start();	
			liner_account_invite_record_loading.setVisibility(View.VISIBLE);}
		else{
			if (drawable!=null) {
        		drawable.stop();
			}
			liner_account_invite_record_loading.setVisibility(View.GONE);}
	}
	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.acount_invite_record_load_error:
				showLoadResult(true,false, false);
				getData();
				break;
			default:
				break;
			}
		}
	};
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
        	getData();
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

    public class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
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
                            A_0_App.getInstance().showExitDialog(B_Account_Invite_InviteRecord.this,AppStrStatic.kicked_offline());
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
		// TODO Auto-generated method stub
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		drawable.stop();
		drawable=null;
		super.onDestroy();
	}
}
