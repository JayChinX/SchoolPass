package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_RongYun_True_Name;
import com.yuanding.schoolpass.service.Api.InterAttdenceContactList;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月11日 下午6:55:38 选择关联教师
 */
public class B_Side_Attence_Main_A2_Add_Contact extends A_0_CpkBaseTitle_Navi {
	private View mLinerReadDataError,mLinerNoContent,liner_lecture_list_whole_view,side_attdence_loading;
	private ListView lv_black_list;
	private MyAdapter adapter;
	private List<Cpk_RongYun_True_Name> list = new ArrayList<Cpk_RongYun_True_Name>();
	protected ImageLoader imageLoader;
	private DisplayImageOptions options;
	//private BitmapUtils bitmapUtils;
	private List<String> list_ids = new ArrayList<String>();
	private String ids="";
	private String names="";
	
	private LinearLayout home_load_loading;
	private AnimationDrawable drawable;
	
	private boolean havaSuccessLoadData = false;
	private ACache maACache;
	private JSONObject jsonObject;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_addcontact);
		setTitleText("选择关联教师");
		
		showTitleBt(ZUI_RIGHT_BUTTON, true);
		 setZuiRightBtn(R.drawable.navigationbar_save);
		
		 ids=getIntent().getStringExtra("organ_ids");
		 if (!ids.equals("")) {
			 ids=ids+",";
		}
		imageLoader = A_0_App.getInstance().getimageLoader();
		options = A_0_App.getInstance().getOptions(R.drawable.ic_defalut_person_center, R.drawable.ic_defalut_person_center, R.drawable.ic_defalut_person_center);
		//bitmapUtils=A_0_App.getBitmapUtils(this, R.drawable.ic_defalut_person_center, R.drawable.ic_defalut_person_center);
		lv_black_list = (ListView) findViewById(R.id.lv_black_list);

		liner_lecture_list_whole_view = findViewById(R.id.liner_add_list_whole_view);
		side_attdence_loading = findViewById(R.id.side_add_loading);
		mLinerReadDataError = findViewById(R.id.side_add_load_error);
		mLinerNoContent = findViewById(R.id.side_add_no_content);
		ImageView iv_blank_por = (ImageView) mLinerNoContent.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView) mLinerNoContent.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.ico_no_attendance);
		tv_blank_name.setText("没有关联教师~");
		
		home_load_loading = (LinearLayout) side_attdence_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start();
		mLinerReadDataError.setOnClickListener(onClick);
//		for (int i = 0; i < 8; i++) {
//			Cpk_RongYun_True_Name cpk_RongYun_True_Name=new Cpk_RongYun_True_Name();
//			cpk_RongYun_True_Name.setName("海利"+i);
//			cpk_RongYun_True_Name.setTargetId(""+i);
//			list.add(cpk_RongYun_True_Name);
//		}
//		adapter = new MyAdapter(list.size());
//		lv_black_list.setAdapter(adapter);
		lv_black_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int posi,
					long arg3) {
				
					//adapter.chiceState(posi);
					if (ids.contains(list.get(posi).getId()+",")) {
						ids=ids.replace(list.get(posi).getId()+",", "");
						list_ids.remove(list.get(posi).getId());
						names=ids.replace(list.get(posi).getName()+",", "");
					}else{
						list_ids.add(list.get(posi).getId());
						ids=ids+list.get(posi).getId()+",";
						names=names+list.get(posi).getName()+",";
					}
					adapter.notifyDataSetChanged();
					}
				
				
		});
		
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
		 if(A_0_App.USER_STATUS.equals("2")){
			 readCache();
	        }else{
	            showLoadResult(false, false, false, true);
	        }
		 
		 
	}
	// 数据加载，及网络错误提示
		OnClickListener onClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.message_notice_load_error:
					showLoadResult(true, false, false, false);
					getAttdenceList();
					break;
			
				default:
					break;
				}
			}
		};
	private void readCache() {
		maACache = ACache.get(this);
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_attence_contact
						+ A_0_App.USER_UNIQID );
		if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
			showInfo(jsonObject);
		}else{
		    updateInfo();
		}
	}
	
	private void showInfo(JSONObject jsonObject) {
		List<Cpk_RongYun_True_Name> listNames = getList(jsonObject);
		if (isFinishing())
			return;
		havaSuccessLoadData = true;
		if (listNames != null && listNames.size() > 0) {
			showLoadResult(false, true, false, false);
			clearBusinessList(false);
			list = listNames;
			adapter = new MyAdapter(list.size());
		    lv_black_list.setAdapter(adapter);
		} else {
			showLoadResult(false, false, false, true);
		}
	}
	
	private void clearBusinessList(boolean setNull) {
		if (list != null) {
			list.clear();
			if (setNull)
				list = null;
		}
	}
	
	private List<Cpk_RongYun_True_Name> getList(JSONObject jsonObject) {
		
		int state = jsonObject.optInt("status");
		List<Cpk_RongYun_True_Name> mlistContact = new ArrayList<Cpk_RongYun_True_Name>();
		if (state == 1) {
			mlistContact = JSON.parseArray(jsonObject.optJSONArray("list")
					+ "", Cpk_RongYun_True_Name.class);
		}
		return mlistContact;
	
}

	private void updateInfo() {
		MyAsyncTask updateLectureInfo = new MyAsyncTask(this);
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

			getAttdenceList();
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
	 private void showLoadResult(boolean loading,boolean wholeView,boolean loadFaile,boolean noData) {
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
	        if(loading){
	        	drawable.start();
	            side_attdence_loading.setVisibility(View.VISIBLE);
	        }else{
	        	if (drawable!=null) {
	        		drawable.stop();
				}
	            side_attdence_loading.setVisibility(View.GONE);
	    }}
	 
	 private void getAttdenceList(){
		 A_0_App.getApi().getAttdenceContactList(B_Side_Attence_Main_A2_Add_Contact.this, A_0_App.USER_TOKEN, new InterAttdenceContactList() {
			
			@Override
			public void onSuccess(List<Cpk_RongYun_True_Name> mList) {
				if(isFinishing())
                    return;
				havaSuccessLoadData = true;
				if (mList.size()>0) {
					list.clear();
					list=mList;
					adapter = new MyAdapter(list.size());
				    lv_black_list.setAdapter(adapter);
				    showLoadResult(false, true, false, false);
				}else{
					showLoadResult(false, false, false, true);
				}
				
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
                if (!havaSuccessLoadData) {
                    showLoadResult(false, false, true, false);
                }
                PubMehods.showToastStr(B_Side_Attence_Main_A2_Add_Contact.this, msg);

            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
	 }
	 
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
				converView = LayoutInflater.from(B_Side_Attence_Main_A2_Add_Contact.this)
						.inflate(R.layout.item_add_contactlist, null);
			}
			TextView name = (TextView) converView
					.findViewById(R.id.tv_black_list_name);
			CircleImageView por = (CircleImageView) converView
					.findViewById(R.id.iv_black_list_por);
			ImageView state = (ImageView) converView
					.findViewById(R.id.iv_black_lsit_check);
			
				state.setVisibility(View.VISIBLE);
//				if (isChice[position]) {
//					state.setBackgroundResource(R.drawable.register_box_selected);
//				} else {
//					state.setBackgroundResource(R.drawable.register_box_unselected);
//				}
			if (ids.contains(list.get(position).getId()+",")) {
				state.setBackgroundResource(R.drawable.register_box_selected);
			}else {
				state.setBackgroundResource(R.drawable.register_box_unselected);
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


	}

	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;
		case ZUI_RIGHT_BUTTON:
			Intent data = new Intent();
			data.putExtra("organ_id", ids);
			data.putExtra("organ_name", names);
			B_Side_Attence_Main_A2_Add_Contact.this.setResult(B_Side_Attence_Main_A1_New_Attence.IMAGE_REQUEST_CODE,data);
			finish();
			
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
                            A_0_App.getInstance().showExitDialog(B_Side_Attence_Main_A2_Add_Contact.this,AppStrStatic.kicked_offline());
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
		super.onDestroy();
	}

}
