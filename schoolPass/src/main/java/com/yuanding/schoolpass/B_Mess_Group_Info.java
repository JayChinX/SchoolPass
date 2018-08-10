package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationNotificationStatus;
import io.rong.imlib.model.Conversation.ConversationType;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.bean.Cpk_Group_Info;
import com.yuanding.schoolpass.bean.Cpk_Group_Info_item;
import com.yuanding.schoolpass.service.Api.InterGroupInfo;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.MyGridView;
import com.yuanding.schoolpass.view.toggle.ToggleButton;
import com.yuanding.schoolpass.view.toggle.ToggleButton.OnToggleChanged;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月16日 下午2:09:39
 * 群资料
 */
public class B_Mess_Group_Info  extends Activity {

    private LinearLayout liner_titlebar_back_goup_info;
    private TextView tv_titlebar_title_goup_info;
    private ImageView iv_distrouble_state;
	private View lienr_ifno_view_temp002,view_ifno_view_temp002,view_group_info_loading;
	private MyGridView gv_group_member;
	private GridAdapter adapter;
	private List<Cpk_Group_Info_item> mList = null;
	
	private TextView tv_group_info_banji,tv_group_info_fudao,tv_group_info_count,tv_clear_message;
	private String groupId;
	
	protected ImageLoader imageLoader;
	private DisplayImageOptions options;
	//private BitmapUtils bitmapUtils;
	private ACache maACache;
	private JSONObject jsonObject;
	
	private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
	private ToggleButton tb_set_message_no_trouble;
    
	private boolean havaSuccessLoadData = false;
    private int on_off = 0;//默认为关闭状态，1表示为打开状态
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		A_0_App.getInstance().addActivity(this);
		setContentView(R.layout.activity_mess_group_info);
		
		groupId = getIntent().getExtras().getString("uniqid");
		
		liner_titlebar_back_goup_info = (LinearLayout)findViewById(R.id.liner_titlebar_back_goup_info);
		tv_titlebar_title_goup_info = (TextView)findViewById(R.id.tv_titlebar_title_goup_info);
		iv_distrouble_state = (ImageView)findViewById(R.id.iv_distrouble_state);
		lienr_ifno_view_temp002 = findViewById(R.id.sv_group_info);
		view_ifno_view_temp002 = findViewById(R.id.view_group_info_error);
		view_group_info_loading = findViewById(R.id.view_group_info_loading);
		
		home_load_loading = (LinearLayout) view_group_info_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start();
		
		tv_group_info_banji = (TextView)findViewById(R.id.tv_group_info_banji);
		tv_group_info_fudao = (TextView)findViewById(R.id.tv_group_info_fudao);
		tv_group_info_count = (TextView)findViewById(R.id.tv_group_info_count);
		tv_clear_message = (TextView)findViewById(R.id.tv_clear_message);
		tb_set_message_no_trouble = (ToggleButton)findViewById(R.id.tb_set_message_no_trouble);
		
		gv_group_member = (MyGridView) findViewById(R.id.gv_group_member);
        adapter = new GridAdapter();
        gv_group_member.setAdapter(adapter);
        
		imageLoader = A_0_App.getInstance().getimageLoader();
		options = A_0_App.getInstance().getOptions(R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center, R.drawable.ic_defalut_person_center);
		//bitmapUtils=A_0_App.getBitmapUtils(this, R.drawable.ic_defalut_person_center, R.drawable.ic_defalut_person_center);
        mList = new ArrayList<Cpk_Group_Info_item>();
        readCache();
        
        gv_group_member.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Intent intent = new Intent(B_Mess_Group_Info.this, B_Mess_Persion_Info.class);
				intent.putExtra("uniqid", mList.get(arg2).getUniqid());
				startActivity(intent);				
			}
		});
        
        tv_clear_message.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			    if(A_0_App.USER_QUNIQID != null&&A_0_App.USER_QUNIQID.length() > 0)
			        clearMessageHistroy(ConversationType.GROUP, A_0_App.USER_QUNIQID);
				 A_Main_My_Message_Acy mam = A_Main_My_Message_Acy.getInstance();
				 if(null!=mam){
					 mam.clearQunTMData();
					 mam.clear_Tm_Histroy_Tag = true;
				 }else{
					 Log.i("B_Mess_Group_Info", "----B_Mess_Group_Info.java :A_Main_My_Message_Acy is null!");
				 }
               
			}
		});
        
        view_ifno_view_temp002.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(mList != null){
					mList.clear();
				}
				showLoadResult(true,false, false);
				getContaceInfo();
			}
		});
        
        tb_set_message_no_trouble.setOnToggleChanged(new OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if(!A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()){
                    PubMehods.showToastStr(B_Mess_Group_Info.this, R.string.error_title_net_error);
                    if (on_off == 0) {
                        showTroubleState(false, iv_distrouble_state, tb_set_message_no_trouble);
                    }else{
                        showTroubleState(true, iv_distrouble_state, tb_set_message_no_trouble);
                    }
                    return;
                }
                if (RongIM.getInstance() == null) {
                    PubMehods.showToastStr(B_Mess_Group_Info.this, "系统连接错误，请重新登录");
                    return;
                }
                if (on_off == 0) {// 打开消息免打扰
                    RongIM.getInstance().setConversationNotificationStatus(
                            Conversation.ConversationType.GROUP,groupId, Conversation.ConversationNotificationStatus.DO_NOT_DISTURB,
                            new ResultCallback<Conversation.ConversationNotificationStatus>() {
                                @Override
                                public void onSuccess(ConversationNotificationStatus arg0) {
                                    showTroubleState(true, iv_distrouble_state, tb_set_message_no_trouble);
                                }

                                @Override
                                public void onError(ErrorCode arg0) {
                                    PubMehods.showToastStr(B_Mess_Group_Info.this, "系统错误,开启免打扰失败");
                                    if (on_off == 0) {
                                        showTroubleState(false, iv_distrouble_state, tb_set_message_no_trouble);
                                    }else{
                                        showTroubleState(true, iv_distrouble_state, tb_set_message_no_trouble);
                                    }
                                }
                            });
                } else {// 关闭消息免打扰
                    RongIM.getInstance().setConversationNotificationStatus(
                            Conversation.ConversationType.GROUP,groupId, Conversation.ConversationNotificationStatus.NOTIFY,
                            new ResultCallback<Conversation.ConversationNotificationStatus>() {
                                @Override
                                public void onSuccess(ConversationNotificationStatus arg0) {
                                    showTroubleState(false, iv_distrouble_state, tb_set_message_no_trouble);
                                }

                                @Override
                                public void onError(ErrorCode arg0) {
                                    PubMehods.showToastStr(B_Mess_Group_Info.this, "系统错误,关闭免打扰失败");
                                    if (on_off == 0) {
                                        showTroubleState(false, iv_distrouble_state, tb_set_message_no_trouble);
                                    }else{
                                        showTroubleState(true, iv_distrouble_state, tb_set_message_no_trouble);
                                    }
                                }
                            });
                }
            }
        });
        
        liner_titlebar_back_goup_info.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}
	
    // 获取融云消息免打扰状态
    private void getNoTroubleState() {
        RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.GROUP, groupId, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
            @Override
            public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.DO_NOT_DISTURB) {
                    showTroubleState(true, iv_distrouble_state, tb_set_message_no_trouble);
                } else {
                    showTroubleState(false, iv_distrouble_state, tb_set_message_no_trouble);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                showTroubleState(false, iv_distrouble_state, tb_set_message_no_trouble);
            }
        });
    }
    
    
    /**
     * 
     * @Title: showTroubleState
     * @Description: TODO(展示免打扰状态)
     * @param @param toggleOn   true，表示打开
     * @param @param iv_distrouble_state
     * @param @param tb_set_message_no_trouble    设定文件
     * @return void    返回类型
     * @throws
     */
    private void showTroubleState(boolean toggleOn,ImageView iv_distrouble_state,ToggleButton tb_set_message_no_trouble) {
        if(toggleOn){
            on_off = 1;
            iv_distrouble_state.setVisibility(View.VISIBLE);
            tb_set_message_no_trouble.setToggleOn();
        }else{
            on_off = 0;
            iv_distrouble_state.setVisibility(View.GONE);
            tb_set_message_no_trouble.setToggleOff();
        }
    }
	
	private void readCache() {
		// TODO Auto-generated method stub
		maACache = ACache.get(this);
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_group_info+A_0_App.USER_UNIQID);
        if (jsonObject!= null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
        	showInfo(jsonObject);
		}else{
		    updateInfo();
		}
	}

	private void showInfo(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		Cpk_Group_Info info = getInfo(jsonObject);
		showSuccessInfo(info);
	}

	private Cpk_Group_Info getInfo(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		
			int state = jsonObject.optInt("status");
			Cpk_Group_Info info = new Cpk_Group_Info();
			if (state == 1) {
				info.setClassname(jsonObject.optString("classname"));
				info.setCounsellor(jsonObject.optString("counsellor"));
				List<Cpk_Group_Info_item> mlistContact= new ArrayList<Cpk_Group_Info_item>();
				mlistContact=JSON.parseArray(jsonObject.optJSONArray("rlist")+"", Cpk_Group_Info_item.class);
				info.setMlist(mlistContact);
				
			}
			return info;
		
	}

	private void clearMessageHistroy(ConversationType type, String targetId) {
		RongIM.getInstance().clearMessages(type, targetId, new ResultCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean arg0) {
						PubMehods.showToastStr(B_Mess_Group_Info.this, "清除成功");
					}

					@Override
					public void onError(ErrorCode arg0) {
						PubMehods.showToastStr(B_Mess_Group_Info.this, "清空失败，重新提交");
					}
				});
	}
	
	private void showSuccessInfo(Cpk_Group_Info info) {
	    if(isFinishing())
            return;
	    havaSuccessLoadData = true;   
        tv_group_info_banji.setText(info.getClassname());
        tv_group_info_fudao.setText(info.getCounsellor());
        tv_group_info_count.setText(info.getMlist().size() + "人");
        String str_title = "班级信息(" + info.getMlist().size() + "人)";
        tv_titlebar_title_goup_info.setText(str_title);
        mList = info.getMlist();
        adapter.notifyDataSetChanged();
        showLoadResult(false,true, false);
        getNoTroubleState();
    }
	
	private void getContaceInfo() {
		A_0_App.getApi().getGroupInfo(B_Mess_Group_Info.this, A_0_App.USER_TOKEN, groupId, new InterGroupInfo() {
			@Override
			public void onSuccess(Cpk_Group_Info info) {
				if(isFinishing())
					return;
				showSuccessInfo(info);				
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
                PubMehods.showToastStr(B_Mess_Group_Info.this, msg);
                if(!havaSuccessLoadData){
                    showLoadResult(false,false, true);
                }
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });

	}
	
	private void showLoadResult(boolean loading,boolean whole,boolean loadFaile) {
		if (whole)
			lienr_ifno_view_temp002.setVisibility(View.VISIBLE);
		else
			lienr_ifno_view_temp002.setVisibility(View.GONE);
		
		if (loadFaile)
			view_ifno_view_temp002.setVisibility(View.VISIBLE);
		else
			view_ifno_view_temp002.setVisibility(View.GONE);
		if(loading){
			drawable.start();
			view_group_info_loading.setVisibility(View.VISIBLE);
		}else{
			if (drawable!=null) {
        		drawable.stop();
			}
			view_group_info_loading.setVisibility(View.GONE);
	}}
	
	public class GridAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			if (mList != null)
				return mList.size();
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int posi, View converView, ViewGroup arg2) {
			if (converView == null) {
				converView = LayoutInflater.from(B_Mess_Group_Info.this).inflate(R.layout.item_group_member, null);
			}
			CircleImageView por = (CircleImageView) converView.findViewById(R.id.iv_group_member_por);
			TextView name = (TextView) converView.findViewById(R.id.tv_group_member_name);
			String url = mList.get(posi).getPhoto_url();
			if (posi % 8 == 0) {
				por.setBackgroundResource(R.drawable.photo_one);
				
			} else if (posi % 8 == 1) {
				por.setBackgroundResource(R.drawable.photo_two);
			} else if (posi % 8 == 2) {
				por.setBackgroundResource(R.drawable.photo_three);
			} else if (posi % 8 == 3) {
				por.setBackgroundResource(R.drawable.photo_four);
			} else if (posi % 8 == 4) {
				por.setBackgroundResource(R.drawable.photo_five);
			} else if (posi % 8 == 5) {
				por.setBackgroundResource(R.drawable.photo_six);
			} else if (posi % 8 == 6) {
				por.setBackgroundResource(R.drawable.photo_seven);
			} else if (posi % 8 == 7) {
				por.setBackgroundResource(R.drawable.photo_eight);
			}
			if(por.getTag() == null){
			    PubMehods.loadServicePic(imageLoader,url,por, options);
			    por.setTag(url);
			}else{
			    if(!por.getTag().equals(url)){
			        PubMehods.loadServicePic(imageLoader,url,por, options);
			        por.setTag(url);
			    }
			}
			 
			//bitmapUtils.display(por, url);
			name.setText(mList.get(posi).getName());
			return converView;
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
	        	
	        	getContaceInfo();
	            return null;
	        }

	        /**
	         * 运行在ui线程中，在doInBackground()执行完毕后执行
	         */
	        @Override
	        protected void onPostExecute(Integer integer) {
//	            logD("上传融云数据完毕");
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

    private class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus) {
                case CONNECTED:// 连接成功。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
                    break;
                case DISCONNECTED:// 断开连接。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    //A_0_App.getInstance().showExitDialog(B_Mess_Group_Info.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Mess_Group_Info.this,AppStrStatic.kicked_offline());
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
