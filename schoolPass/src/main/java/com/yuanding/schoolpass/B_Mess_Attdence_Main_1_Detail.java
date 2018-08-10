
package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Side_Attdence_Detail_List;
import com.yuanding.schoolpass.service.Api.InterAttdenceList;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.MyListView;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年5月9日 下午2:09:28 学生考勤明细页面
 */
public class B_Mess_Attdence_Main_1_Detail extends A_0_CpkBaseTitle_Navi {
	private MyListView lv_attdence_location_detail;
	private View liner_attdence_deatil_list_whole_view,side_attdence_detail_load_error
	,side_attdence_deatil_loading,side_attdence_deatil_no_content;
	private MyListAdapter myAdapter;
	private ACache maACache;
	private JSONObject jsonObject;
	private Boolean firstLoad,havaSuccessLoadData = false;
	private List<Cpk_Side_Attdence_Detail_List> attdence_Lists;
	private String attdence_title;
	private TextView tv_attdence_title;
	
	 private LinearLayout home_load_loading;
	 private AnimationDrawable drawable;
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        A_0_App.getInstance().addActivity(this);
        setView(R.layout.activity_mess_attendence_deatil);
        attdence_Lists=new ArrayList<Cpk_Side_Attdence_Detail_List>();
        setTitleText("课堂考勤统计");
        firstLoad = true;
        myAdapter=new MyListAdapter();
        liner_attdence_deatil_list_whole_view=findViewById(R.id.liner_attdence_deatil_list_whole_view);
        side_attdence_deatil_loading=findViewById(R.id.side_attdence_deatil_loading);
        side_attdence_detail_load_error=findViewById(R.id.side_attdence_detail_load_error);
        side_attdence_deatil_no_content=findViewById(R.id.side_attdence_deatil_no_content);
        
        home_load_loading = (LinearLayout) side_attdence_deatil_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
        
        ImageView iv_blank_por = (ImageView)side_attdence_deatil_no_content.findViewById(R.id.iv_blank_por);
        TextView tv_blank_name = (TextView)side_attdence_deatil_no_content.findViewById(R.id.tv_blank_name);
        iv_blank_por.setBackgroundResource(R.drawable.ico_no_attendance);
        tv_blank_name.setText("暂无课堂考勤统计~");
        lv_attdence_location_detail=(MyListView) findViewById(R.id.lv_attdence_detail_mylist);
        tv_attdence_title=(TextView) findViewById(R.id.tv_attdence_detail_title);
        lv_attdence_location_detail.setAdapter(myAdapter);
       
        if (A_0_App.USER_STATUS.equals("5") || A_0_App.USER_STATUS.equals("0")) {
			showLoadResult(false, false, false, true);
		} else {
			readCache();
		}
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
        side_attdence_detail_load_error.setOnClickListener(onClick);
    }
    private void readCache() {
		maACache = ACache.get(this);
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_attdence_detail+A_0_App.USER_UNIQID);
		
        if (jsonObject!= null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
        	showInfo(jsonObject);
		}else{
		    updateInfo();
		}
	}
    private void showInfo(JSONObject jsonObject) {
            havaSuccessLoadData = true;  
			int state = jsonObject.optInt("status");
			List<Cpk_Side_Attdence_Detail_List> mlistContact= new ArrayList<Cpk_Side_Attdence_Detail_List>();
			if (state == 1) {
				mlistContact=JSON.parseArray(jsonObject.optJSONArray("list")+"", Cpk_Side_Attdence_Detail_List.class);
			}
			attdence_title=jsonObject.optString("title");
			tv_attdence_title.setText(attdence_title);
			if (isFinishing())
				return;
			if (mlistContact != null && mlistContact.size() > 0) {
				clearBusinessList();
				attdence_Lists = mlistContact;
				lv_attdence_location_detail.setSelection(0);
				myAdapter.notifyDataSetChanged();
				showLoadResult(false, true, false, false);
			} else {
				showLoadResult(false, false, false, true);
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
        	getAttdenceDetailList();
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
	 private void getAttdenceDetailList() {
			A_0_App.getApi().getAttdenceList(B_Mess_Attdence_Main_1_Detail.this, A_0_App.USER_TOKEN,new InterAttdenceList() {
				@Override
				public void onSuccess(List<Cpk_Side_Attdence_Detail_List> mList,String title) {
					if (isFinishing())
						return;
					havaSuccessLoadData = true;  
					if (mList != null && mList.size() > 0) {
						clearBusinessList();
						attdence_Lists = mList;
						myAdapter.notifyDataSetChanged();
						showLoadResult(false,true, false, false);
					} else {
						showLoadResult(false,false, false, true);
					}
					tv_attdence_title.setText(title);
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
                    PubMehods.showToastStr(B_Mess_Attdence_Main_1_Detail.this, msg);
                }
                
                @Override
                public void onCancelled() {
                    // TODO Auto-generated method stub
                    
                }
            });
		}
		
		private void clearBusinessList() {
			if (attdence_Lists != null && attdence_Lists.size() > 0) {
				attdence_Lists.clear();
			}
		}
		
     private void showLoadResult(boolean loading,boolean wholeView,boolean loadFaile,boolean noData) {
		
		if (wholeView)
			liner_attdence_deatil_list_whole_view.setVisibility(View.VISIBLE);
		else
			liner_attdence_deatil_list_whole_view.setVisibility(View.GONE);
		
		if (loadFaile)
			side_attdence_detail_load_error.setVisibility(View.VISIBLE);
		else
			side_attdence_detail_load_error.setVisibility(View.GONE);
		
		if (noData)
			side_attdence_deatil_no_content.setVisibility(View.VISIBLE);
		else
			side_attdence_deatil_no_content.setVisibility(View.GONE);
		if(loading){
			drawable.start();
			side_attdence_deatil_loading.setVisibility(View.VISIBLE);
		}else{
			if (drawable!=null) {
        		drawable.stop();
			}
			side_attdence_deatil_loading.setVisibility(View.GONE);
	 }}
  // 数据加载，及网络错误提示
 	OnClickListener onClick = new OnClickListener() {
 		@Override
 		public void onClick(View v) {
 			switch (v.getId()) {
 			case R.id.side_attdence_detail_load_error:
 				showLoadResult(true,false, false, false);
 				clearBusinessList();
 				getAttdenceDetailList();
 				break;
 			default:
 				break;
 			}
 		}
 	};
	
    private class MyListAdapter extends BaseAdapter
    {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (attdence_Lists != null)
                return attdence_Lists.size();
             return 0;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(B_Mess_Attdence_Main_1_Detail.this).inflate(R.layout.item_mess_attdence_deatil_mylist_view, null);
				holder = new ViewHolder();
				holder.tv_attdence_develop_person=(TextView) convertView.findViewById(R.id.tv_attdence_developer);
				holder.tv_attdence_person_num=(TextView) convertView.findViewById(R.id.tv_attdence_person_num);
				holder.tv_attdence_ask_for_leave_person=(TextView) convertView.findViewById(R.id.tv_ask_for_leave_person);
				holder.tv_attdence_late_person=(TextView) convertView.findViewById(R.id.tv_attdence_late_person);
				holder.tv_attdence_earlyer_person=(TextView) convertView.findViewById(R.id.tv_attdence_earlyer_person);
				holder.tv_attdence_absent_person=(TextView) convertView.findViewById(R.id.tv_attdence_absent_person);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}

			holder.tv_attdence_develop_person.setText(attdence_Lists.get(position).getTrue_name());
			holder.tv_attdence_person_num.setText(attdence_Lists.get(position).getNormalCount());
			holder.tv_attdence_ask_for_leave_person.setText(attdence_Lists.get(position).getLeaveCount());
			holder.tv_attdence_late_person.setText(attdence_Lists.get(position).getLateCount());
			holder.tv_attdence_earlyer_person.setText(attdence_Lists.get(position).getEarlyCount());
			holder.tv_attdence_absent_person.setText(attdence_Lists.get(position).getAbsentCount());
			return convertView;
		}
    	
    }
    private class ViewHolder
    {
    	TextView tv_attdence_develop_person;
    	TextView tv_attdence_person_num;
    	TextView tv_attdence_ask_for_leave_person;
    	TextView tv_attdence_late_person;
    	TextView tv_attdence_earlyer_person;
    	TextView tv_attdence_absent_person;
//    	View view_bottom;
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
                    //A_0_App.getInstance().showExitDialog(B_Mess_Attdence_Main_1_Detail.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Mess_Attdence_Main_1_Detail.this,AppStrStatic.kicked_offline());
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
		super.onResume();
		if(!firstLoad){
			getAttdenceDetailList();
		}else{
			firstLoad = false;
		}
		
	}
	
	@Override
	protected void onDestroy() {
		drawable.stop();
		drawable=null;
		super.onDestroy();
	}
    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                finish();
                break;

            default:
                break;
        }
    }

}
