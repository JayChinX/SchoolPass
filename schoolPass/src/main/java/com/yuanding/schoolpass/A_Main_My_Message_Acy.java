package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.ConversationBehaviorListener;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.OnReceiveMessageListener;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
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
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle;
import com.yuanding.schoolpass.bean.Cpk_Banner_Mode;
import com.yuanding.schoolpass.bean.Cpk_Group_Info;
import com.yuanding.schoolpass.bean.Cpk_Group_Info_item;
import com.yuanding.schoolpass.bean.Cpk_Index_Notice_Message;
import com.yuanding.schoolpass.bean.Cpk_Module_List;
import com.yuanding.schoolpass.bean.Cpk_RongYun_True_Name;
import com.yuanding.schoolpass.bean.Cpk_Top_List;
import com.yuanding.schoolpass.service.Api.InterGetInfo;
import com.yuanding.schoolpass.service.Api.InterGroupInfo;
import com.yuanding.schoolpass.service.Api.InterIndexNoticeMess;
import com.yuanding.schoolpass.service.Api.InterRongYunName;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.service.Api.Inter_UpLoad_ChannelId;
import com.yuanding.schoolpass.service.BackReciverMessage;
import com.yuanding.schoolpass.service.BackService;
import com.yuanding.schoolpass.service.IBackService;
import com.yuanding.schoolpass.service.ReceivePushBro;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.utils.XmlUtils;
import com.yuanding.schoolpass.utils.download.ApkDownloader;
import com.yuanding.schoolpass.utils.download.AsyncDownLoadManager.OnDownLoadProgrossListener;
import com.yuanding.schoolpass.utils.download.WebResource;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.GeneralDialog;
import com.yuanding.schoolpass.view.MyListView;
import com.yuanding.schoolpass.view.rongyun.WYZFNoticeContent;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月2日 下午8:28:01 类说明
 * 首页 信息列表
 */
public class A_Main_My_Message_Acy extends A_0_CpkBaseTitle implements OnDownLoadProgrossListener,RongIM.UserInfoProvider, RongIM.GroupInfoProvider {

	private View mess_index_acy_load_error,liner_whole_view,mess_index_acy_loading;
	private ListView lv_message_list_rong;
	private MyListView lv_message_list;
	private Mydapter adapter;
	private MydapterRong adapterRong;
	private List<Cpk_Index_Notice_Message> mMessageList,mMessageListRong;//mMessageListRong
	private boolean firstLoad_Tag = false;//第一次进入
	public boolean clear_Tm_Histroy_Tag = false;//清除聊天记录
	private String banJiQunLiao,hint_title = "";
	
	private static A_Main_My_Message_Acy instance;
	protected ImageLoader imageLoader;
	private DisplayImageOptions optionsXiao,optionsYuan,optionsBan,optionsAssistant,optionsOfficial,optionsAttdence,optionsQun,optionsSingle;
	private boolean ifLoadUpLoadRongYunData = false;
	private ACache maACache;
    private JSONObject jsonObject;
    
    private boolean first_Enter_Eable = true;//融云监听结果
    private static final int REFRESH_DELAY_TIME = 400;
    private int count_list=0;//判断列表有未读没
    
    private final String str_delete_user = "该用户已删除";
    
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private static long severTime = 0;
    
	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        logD("onCreate()");
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
		instance = this;
		A_0_App.getInstance().addActivity(this);
		setView(R.layout.activity_main_message);
		setTitleText(R.string.str_my_mess_title);

		if(A_0_App.USER_STATUS.equals("2")){
			setZuiRightBtn(R.drawable.navigationbar_search_button);
			 showTitleBt(SEARCH_BUTTON, true);
		}else{
			showTitleBt(SEARCH_BUTTON, false);
		}
		
		banJiQunLiao = getResources().getString(R.string.str_group_chat);
		hint_title = getResources().getString(R.string.str_no_message_chat);
		firstLoad_Tag = true;
		mess_index_acy_load_error = findViewById(R.id.mess_index_acy_load_error);
		mess_index_acy_loading = findViewById(R.id.mess_index_acy_loading);
		liner_whole_view = findViewById(R.id.liner_whole_view);
		
		home_load_loading = (LinearLayout) mess_index_acy_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		lv_message_list_rong = (ListView) findViewById(R.id.lv_message_idnex_list_rong);
		mMessageList = new ArrayList<Cpk_Index_Notice_Message>();
		mMessageListRong = new ArrayList<Cpk_Index_Notice_Message>();
		
        imageLoader = A_0_App.getInstance().getimageLoader();
        optionsXiao = A_0_App.getInstance().getOptions(R.drawable.icon_mess_xiao, R.drawable.icon_mess_xiao, R.drawable.icon_mess_xiao);
        optionsYuan = A_0_App.getInstance().getOptions(R.drawable.icon_mess_yuan, R.drawable.icon_mess_yuan, R.drawable.icon_mess_yuan);
        optionsBan = A_0_App.getInstance().getOptions(R.drawable.icon_mess_ban, R.drawable.icon_mess_ban, R.drawable.icon_mess_ban);
        optionsAssistant = A_0_App.getInstance().getOptions(R.drawable.icon_mess_assistant, R.drawable.icon_mess_assistant, R.drawable.icon_mess_assistant);
        optionsOfficial = A_0_App.getInstance().getOptions(R.drawable.icon_mess_weixiaobang, R.drawable.icon_mess_weixiaobang, R.drawable.icon_mess_weixiaobang);
        optionsAttdence = A_0_App.getInstance().getOptions(R.drawable.icon_mess_attdence, R.drawable.icon_mess_attdence, R.drawable.icon_mess_attdence);
        optionsQun = A_0_App.getInstance().getOptions(R.drawable.icon_mess_ban_liao, R.drawable.icon_mess_ban_liao, R.drawable.icon_mess_ban_liao);
        optionsSingle = A_0_App.getInstance().getOptions(R.drawable.i_default_por_120, R.drawable.i_default_por_120, R.drawable.i_default_por_120);
        
		adapterRong = new MydapterRong();
		addHeadView(lv_message_list_rong);
		lv_message_list_rong.setAdapter(adapterRong);
		
		mess_index_acy_load_error.setOnClickListener(onClick);
		try {
			RongIM.setUserInfoProvider(this, true);//设置由 IMKit 来缓存用户信息
			RongIM.setGroupInfoProvider(this, true);//设置由 IMKit 来缓存用户信息
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		updateContactList();//上传群组信息到服务器,上传CHannelID
		readCache();
		receiveMessageData();//初始加载接收融云消息
	    
        if (A_0_App.getInstance().regedit_enter) {
            A_0_App.getInstance().regedit_enter = false;
            if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_HAVA_CERTIFIED)) {
                A_0_App.getInstance().showPointDialog(A_Main_My_Message_Acy.this, AppStrStatic.str_guide_certified(),false);
            } else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)){
                A_0_App.getInstance().showPointDialog(A_Main_My_Message_Acy.this, AppStrStatic.str_guide_not_certified(),false);
            }
        }
        
        //这个是注册局部文件中的广播
        bro=new ReceivePushBro();
        IntentFilter filter=new IntentFilter(PubMehods.getAppPackageName(instance));
        this.registerReceiver(bro, filter);
        
        //身边红点的广播监听
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mReciver = new BackReciverMessage();
        mServiceIntent = new Intent(this, BackService.class);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BackService.HEART_BEAT_ACTION);
        mIntentFilter.addAction(BackService.MESSAGE_ACTION);
	}
	
	private ReceivePushBro bro;
	//广播要用
	public void refreshMessageList(){
	   logD("学生——收到了广播消息通知刷新列表");
		getIndexMessList(firstLoad_Tag);
	}
	
    private void addHeadView(ListView listview) {
        View view = LayoutInflater.from(A_Main_My_Message_Acy.this).inflate(R.layout.item_index_messge, null);
        lv_message_list = (MyListView)view.findViewById(R.id.lv_message_idnex_list);
        adapter = new Mydapter();
        lv_message_list.setAdapter(adapter);
        listview.addHeaderView(view);
        
       //前面的
	   lv_message_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int posi,long arg3) {
				
				if (mMessageList.get(posi).getCount() != null && !mMessageList.get(posi).getCount().equals("")) {
					int no_count = Integer.parseInt(mMessageList.get(posi).getCount());
					if (no_count>0) {
						count_list=1;
					}else{
						count_list=0;
					}
				}
				Intent intent = new Intent();
				if (posi == 0) {//学校
//                    if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)) {
//                        A_0_App.getInstance().enter_Perfect_information(A_Main_My_Message_Acy.this);
//                    } else {
				        go_TO_Enter(intent, posi, "1", mMessageList.get(posi).getMsg_name(), count_list);
//                    }
				}else if (posi == 1) {//院系
				    go_TO_Enter(intent, posi, "2", mMessageList.get(posi).getMsg_name(), count_list);
				}else if (posi == 2) {//班级
				    go_TO_Enter(intent, posi, "3", mMessageList.get(posi).getMsg_name(), count_list);
				}else{
				    if(AppStrStatic.TAG_MESSAGE_ID_ASSISTANT.equals(mMessageList.get(posi).getMessage_id())){
                        intent.setClass(A_Main_My_Message_Acy.this, B_Mess_School_Assistant_Main.class);
                        startActivity(intent);
                    }else if(AppStrStatic.TAG_MESSAGE_ID_OFFICIAL.equals(mMessageList.get(posi).getMessage_id())){
                        intent.setClass(A_Main_My_Message_Acy.this, B_Mess_Official_List.class);
                        intent.putExtra("title_text", mMessageList.get(posi).getMsg_name());
                        intent.putExtra("count", count_list);
                        startActivity(intent);
                    }else if(AppStrStatic.TAG_MESSAGE_ID_ATTENCE.equals(mMessageList.get(posi).getMessage_id())){
                        intent.setClass(A_Main_My_Message_Acy.this, B_Mess_Attdence_Main_0_Acy.class);
                        intent.putExtra("acy_type", 2);//正常列表进入
                        startActivity(intent);
                    }else if(AppStrStatic.TAG_MESSAGE_ID_GROUP.equals(mMessageList.get(posi).getMessage_id())){
                        String targetId = mMessageList.get(posi).getTargetId();
                        if (mListContacts != null && mListContacts.size() > 0) {
                            if (targetId != null && !targetId.equals("")) {
                                mMessageList.get(posi).setCount("0");
                                goChatJudge(A_Main_My_Message_Acy.this, 
                                        Conversation.ConversationType.GROUP, mMessageList.get(posi).getTargetId(), banJiQunLiao);
                            }else{
                                PubMehods.showToastStr(A_Main_My_Message_Acy.this, R.string.str_title_no_group);
                            }
                        }else{
                            if (!A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {
                                PubMehods.showToastStr(A_Main_My_Message_Acy.this, R.string.error_title_net_error);
                                return;
                            }
                            if (targetId != null && !targetId.equals("")) {
                                mMessageList.get(posi).setCount("0");
                                goChatJudge(A_Main_My_Message_Acy.this, 
                                        Conversation.ConversationType.GROUP, mMessageList.get(posi).getTargetId(), banJiQunLiao);
                            }else{
                                PubMehods.showToastStr(A_Main_My_Message_Acy.this, R.string.str_title_no_group);
                            }
                        }
                    }
				}
			}
	   });
    }
    
    private void go_TO_Enter(Intent intent,int posi,String level,String title_text,int count) {
        if(AppStrStatic.TAG_MESSAGE_ID_ASSISTANT.equals(mMessageList.get(posi).getMessage_id())){
            intent.setClass(A_Main_My_Message_Acy.this, B_Mess_School_Assistant_Main.class);
            startActivity(intent);
        }else if(AppStrStatic.TAG_MESSAGE_ID_OFFICIAL.equals(mMessageList.get(posi).getMessage_id())){
            intent.setClass(A_Main_My_Message_Acy.this, B_Mess_Official_List.class);
            intent.putExtra("title_text", mMessageList.get(posi).getMsg_name());
            intent.putExtra("count", count_list);
            startActivity(intent);
        }else if(AppStrStatic.TAG_MESSAGE_ID_ATTENCE.equals(mMessageList.get(posi).getMessage_id())){
            intent.setClass(A_Main_My_Message_Acy.this, B_Mess_Attdence_Main_0_Acy.class);
            intent.putExtra("acy_type", 2);//正常列表进入
            startActivity(intent);
        }else if(AppStrStatic.TAG_MESSAGE_ID_GROUP.equals(mMessageList.get(posi).getMessage_id())){
            String targetId = mMessageList.get(posi).getTargetId();
            if (mListContacts != null && mListContacts.size() > 0) {
                if (targetId != null && !targetId.equals("")) {
                    mMessageList.get(posi).setCount("0");
                    goChatJudge(A_Main_My_Message_Acy.this, 
                            Conversation.ConversationType.GROUP, mMessageList.get(posi).getTargetId(), banJiQunLiao);
                }else{
                    PubMehods.showToastStr(A_Main_My_Message_Acy.this, R.string.str_title_no_group);
                }
            }else{
                if (!A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {
                    PubMehods.showToastStr(A_Main_My_Message_Acy.this, R.string.error_title_net_error);
                    return;
                }
                if (targetId != null && !targetId.equals("")) {
                    mMessageList.get(posi).setCount("0");
                    goChatJudge(A_Main_My_Message_Acy.this, 
                            Conversation.ConversationType.GROUP, mMessageList.get(posi).getTargetId(), banJiQunLiao);
                }else{
                    PubMehods.showToastStr(A_Main_My_Message_Acy.this, R.string.str_title_no_group);
                }
            }
        }else{
            intent.setClass(A_Main_My_Message_Acy.this, B_Mess_Notice_List.class);
            intent.putExtra("level", level);
            intent.putExtra("title_text", title_text);
            intent.putExtra("count", count);
            startActivity(intent);
        }
    }
	
	public  void callSb(final int no) {
		final GeneralDialog upDateDialog = new GeneralDialog(A_Main_My_Message_Acy.this,R.style.Theme_GeneralDialog);
        upDateDialog.setTitle(R.string.pub_title);
        upDateDialog.setContent("删除该聊天?");
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
                removeSb(mMessageListRong.get(no).getTargetId(),no);
            }
        });
        
        upDateDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {

			}
		});
        upDateDialog.show();
	}
	
    //读取缓存
    private void readCache() {
        maACache = ACache.get(this);
        try {
            jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_my_message+A_0_App.USER_UNIQID + A_0_App.USER_STATUS);
        } catch (Exception e) {
            jsonObject = null;
        }
    
        if (jsonObject!= null) {//说明有缓存
            mMessageListRong.clear();
            List<Cpk_Index_Notice_Message> tempList = getCacheAllList();
            if (tempList != null && tempList.size() > 0) {
                logTest("没有单聊缓存");
                mMessageListRong = tempList;
                for (int i = 0; i < tempList.size(); i++) {
                    Cpk_RongYun_True_Name contact = new Cpk_RongYun_True_Name();
                    contact.setName(tempList.get(i).getMsg_name());
                    contact.setPhoto_url(tempList.get(i).getBg_img());
                    contact.setTargetId(tempList.get(i).getTargetId());
                    contact.setIs_delete("0");
                    add_Contacts_Object(contact);
                }
            }
            showInfo(jsonObject);
            adapter.notifyDataSetChanged();
            adapterRong.notifyDataSetChanged();
            calculateNoReadCount();
            showLoadResult(false,true, false);
            if (A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {
                getIndexMessList(firstLoad_Tag);
            }else {
                PubMehods.showToastStr(A_Main_My_Message_Acy.this, R.string.error_title_net_error);
            }
        }else{
            logD("没有数据缓存");
            if (A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {
                getIndexMessList(firstLoad_Tag);
            } else {
                showLoadResult(false, false, true);
                PubMehods.showToastStr(A_Main_My_Message_Acy.this, R.string.error_title_net_error);
            }
        }
    }
    //展示缓存信息
    private void showInfo(JSONObject jsonObject) {
        // TODO Auto-generated method stub
        try {
            int state = jsonObject.optInt("status");
            String message = jsonObject.optString("msg");
            long time=jsonObject.optLong("time");
            
            boolean tag_assistant = false;
            JSONObject jsonObject_Assistant = jsonObject.optJSONObject("assistantMsg");
            if (jsonObject_Assistant != null) {
                String judgeTemp = jsonObject_Assistant.optString("title", "");
                if (judgeTemp != null && !judgeTemp.equals("") && !judgeTemp.equals("null")) {
                    tag_assistant = true;
                } else {
                    tag_assistant = false;
                }
            }
            JSONObject jsonObject_Official = jsonObject.optJSONObject("systemMsg");
            JSONObject jsonObject_Attdence = jsonObject.optJSONObject("attendance");
            
            List<Cpk_Index_Notice_Message> mlistContact = new ArrayList<Cpk_Index_Notice_Message>();
            if (state == 1) {
                JSONArray jsonArrayItem = jsonObject.optJSONArray("msg");
                if (jsonArrayItem != null && jsonArrayItem.length() > 0) {
                    mlistContact=JSON.parseArray(jsonObject.optJSONArray("msg")+"",Cpk_Index_Notice_Message.class);
                    if (mlistContact.size() > 0 && jsonObject_Assistant != null && tag_assistant) {
                        Cpk_Index_Notice_Message message_one = new Cpk_Index_Notice_Message();
                        message_one.setMessage_id(AppStrStatic.TAG_MESSAGE_ID_ASSISTANT);
                        message_one.setMsg_name(getResources().getString(R.string.str_school_assistant));
                        message_one.setCreate_time(jsonObject_Assistant.optString("create_time",""));
                        message_one.setCount(jsonObject_Assistant.optString("count",""));
                        message_one.setApp_msg_sign("");//消息来源
                        message_one.setTitle(jsonObject_Assistant.optString("title",""));
                        mlistContact.add(message_one);
                    }
                    if(mlistContact.size() > 0 && jsonObject_Official != null){
                        Cpk_Index_Notice_Message message_one = new Cpk_Index_Notice_Message();
                        message_one.setMessage_id(AppStrStatic.TAG_MESSAGE_ID_OFFICIAL);
                        message_one.setMsg_name(getResources().getString(R.string.app_name));
                        message_one.setCreate_time(jsonObject_Official.optString("create_time",""));
                        message_one.setCount(jsonObject_Official.optString("count",""));
                        message_one.setApp_msg_sign("");//消息来源
                        message_one.setTitle(jsonObject_Official.optString("title",""));
                        mlistContact.add(message_one);
                    }
                    if(mlistContact.size() > 0 && jsonObject_Attdence != null){
                        Cpk_Index_Notice_Message message_one = new Cpk_Index_Notice_Message();
                        message_one.setMessage_id(AppStrStatic.TAG_MESSAGE_ID_ATTENCE);
                        message_one.setMsg_name(getResources().getString(R.string.str_attence_title));
                        message_one.setCreate_time(jsonObject_Attdence.optString("create_time",""));
                        message_one.setCount(jsonObject_Attdence.optString("atd_count",""));
                        message_one.setApp_msg_sign("");
                        message_one.setTitle(jsonObject_Attdence.optString("title",""));
                        mlistContact.add(message_one);
                    }
                    if (mlistContact.size() > 0 && A_0_App.USER_STATUS.equals("2")) {// 加入群聊
                        Cpk_Index_Notice_Message qunliao = new Cpk_Index_Notice_Message();
                        qunliao.setMessage_id(AppStrStatic.TAG_MESSAGE_ID_GROUP);
                        qunliao.setMsg_name(getResources().getString(R.string.str_group_chat));
                        qunliao.setApp_msg_sign(getResources().getString(R.string.str_no_message_chat));
                        qunliao.setCount("0");
                        qunliao.setCreate_time("");
                        qunliao.setTitle("");
                        qunliao.setTargetId(A_0_App.USER_QUNIQID);
                        qunliao.setConversationtype(ConversationType.GROUP + "");
                        mlistContact.add(qunliao);
                    }
                }
            }
            successResult(mlistContact,time);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
            
    public void successResult(List<Cpk_Index_Notice_Message> mlistContact,long time){
        if(isFinishing())
            return;
        logE("获取IndexNoticeMess数据成功");
        try {
            severTime=time*1000;
        } catch (Exception e) {
            severTime=0;
        }
        getIndexData = false;
        if (mMessageList == null) {
            mMessageList = new ArrayList<Cpk_Index_Notice_Message>();
        }
        if (mMessageListRong == null) {
            mMessageListRong = new ArrayList<Cpk_Index_Notice_Message>();
        }
       String tempQunMess = "",noCount ="0",tempQunAuterh ="",tempQunTime ="";
       //解决群组信息闪动的问题
       if (mMessageList!=null&&mMessageList.size() > 0 && !firstLoad_Tag) {
              if (!clear_Tm_Histroy_Tag) {
                  int mess_posi = searchPosiWhereMessId(mMessageList, AppStrStatic.TAG_MESSAGE_ID_GROUP);
                  if(mess_posi >= 0){
                      tempQunMess = mMessageList.get(mess_posi).getTitle();// 群聊内容
                      tempQunAuterh = mMessageList.get(mess_posi).getApp_msg_sign();// 群聊发送者名字
                      noCount = mMessageList.get(mess_posi).getCount();// 群聊未读数量
                      tempQunTime = mMessageList.get(mess_posi).getCreate_time();
                  }
              } else {
                  clear_Tm_Histroy_Tag = false;
              }
          }
        clearData(false);
        mMessageList = mlistContact;
        int mess_posi = searchPosiWhereMessId(mMessageList, AppStrStatic.TAG_MESSAGE_ID_GROUP);
        if(mess_posi >= 0){
            mMessageList.get(mess_posi).setTitle(tempQunMess);// 群聊内容
            mMessageList.get(mess_posi).setApp_msg_sign(tempQunAuterh);// 群聊发送者名字
            mMessageList.get(mess_posi).setCount(noCount);// 群聊未读数量
            mMessageList.get(mess_posi).setCreate_time(tempQunTime);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            if (isFinishing())
                return;
            A_0_App.getInstance().showExitToast(A_Main_My_Message_Acy.this, "");
        }
        calculateNoReadCount();
    }
    
    private boolean getIndexData =false;
	private void getIndexMessList(final boolean firstLoad) {
        if (A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals("") || A_0_App.USER_TOKEN.length() <= 0)
            System.exit(0);
	    if(getIndexData){
            logE("正在获取IndexNoticeMess数据，切勿重复请求");
            return;
        }
	    getIndexData = true;
		A_0_App.getApi().getIndexNoticeMess(A_Main_My_Message_Acy.this, A_0_App.USER_TOKEN,firstLoad, new InterIndexNoticeMess() {
			@Override
			public void onSuccess(long time,List<Cpk_Index_Notice_Message> mList) {
			    successResult(mList, time);
				if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {// 重读融云数据
	                connectRoogIm(A_0_App.USER_QUTOKEN);
	            } else {
	                if(jsonObject == null)
	                    showLoadResult(false, true, false);
	            }
			}
		},new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                logE("获取获取IndexNoticeMess数据失败");
                getIndexData = false;
                if (isFinishing())
                    return;
                if (jsonObject == null) {
                    if(mMessageList.size() > 0)
                        showLoadResult(false, true, false);
                    else
                        showLoadResult(false, false, true);
                }
                if(firstLoad)
                    PubMehods.showToastStr(A_Main_My_Message_Acy.this,msg);
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });

	}
	
	private void showLoadResult(boolean Loading,boolean whole,boolean loadFaile) {
		if (whole)
			liner_whole_view.setVisibility(View.VISIBLE);
		else
			liner_whole_view.setVisibility(View.GONE);
		
		if (loadFaile)
			mess_index_acy_load_error.setVisibility(View.VISIBLE);
		else
			mess_index_acy_load_error.setVisibility(View.GONE);
			
		if (Loading){
			drawable.start();
			mess_index_acy_loading.setVisibility(View.VISIBLE);
		}else{	
			if (drawable!=null) {
        		drawable.stop();
			}
			mess_index_acy_loading.setVisibility(View.GONE);
		}
	}
	
	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.mess_index_acy_load_error:
				showLoadResult(true,false, false);
				clearData(false);
				updateContactList();
				readCache();
				receiveMessageData();//接收消息
				break;
			default:
				break;
			}
		}
	};
	
	public class Mydapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mMessageList != null)
				return mMessageList.size();
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
			
			ViewHolder holder;
			if (converView == null) {
				converView = LayoutInflater.from(A_Main_My_Message_Acy.this).inflate(R.layout.item_message, null);
				holder = new ViewHolder();
				
				holder.iv_message_type_pic = (CircleImageView)converView.findViewById(R.id.iv_index_message_type_pic);
				holder.tv_message_auther = (TextView)converView.findViewById(R.id.tv_index_message_auther);
				holder.tv_message_time = (TextView)converView.findViewById(R.id.tv_index_message_time);
				holder.tv_message_content = (TextView)converView.findViewById(R.id.tv_index_message_content);
				holder.tv_index_message_from = (TextView)converView.findViewById(R.id.tv_index_message_from);
				holder.tv_no_read_count = (TextView)converView.findViewById(R.id.tv_no_read_count);
				holder.iv_message_block = (ImageView)converView.findViewById(R.id.iv_message_block);
				
				converView.setTag(holder);
			}else{
				holder = (ViewHolder) converView.getTag();
			}
			
			//设置头像和群组标记
			String img_url = mMessageList.get(posi).getBg_img();
			if(img_url == null || img_url.equals(""))
                img_url = "";
//			if(holder.iv_message_type_pic.getTag() == null){
			    showloadPicResult(posi, imageLoader, img_url, holder.iv_message_block,holder.iv_message_type_pic,trouble_On_Off);
//			}else{
//			    if(!holder.iv_message_type_pic.getTag().equals(img_url)){
//			        showloadPicResult(posi, imageLoader, img_url, holder.iv_message_block, holder.iv_message_type_pic,trouble_On_Off);
//			    }else{
//			        if(AppStrStatic.TAG_MESSAGE_ID_GROUP.equals(mMessageList.get(posi).getMessage_id())){
//                        showTroubleState(holder.iv_message_block, trouble_On_Off);
//                    }else{
//                        showTroubleState(holder.iv_message_block, 0);
//                    }
//			    }
//			}
			
			//未读数量
            if (mMessageList != null && mMessageList.size() > 0 && mMessageList.get(posi).getCount() != null 
			        && !mMessageList.get(posi).getCount().equals("")) {
				int no_count = Integer.parseInt(mMessageList.get(posi).getCount());
				if (no_count == 0) {
					holder.tv_no_read_count.setVisibility(View.GONE);
				} else if (0 < no_count && no_count <= 99) {
					holder.tv_no_read_count.setVisibility(View.VISIBLE);
					holder.tv_no_read_count.setBackgroundResource(R.drawable.icon_message_noread);
					holder.tv_no_read_count.setText(no_count + "");
				}else if(99<no_count&&no_count<=999){
					holder.tv_no_read_count.setVisibility(View.VISIBLE);
					holder.tv_no_read_count.setBackgroundResource(R.drawable.icon_rong_message_noread);
					holder.tv_no_read_count.setText(no_count+"");
				}else if(no_count>999)
				{
					holder.tv_no_read_count.setVisibility(View.VISIBLE);
					holder.tv_no_read_count.setBackgroundResource(R.drawable.icon_rong_message_noread);
					holder.tv_no_read_count.setText("999+");
				}
			}else{
				holder.tv_no_read_count.setVisibility(View.GONE);
			}
			
			//消息名字
			if(mMessageList != null && mMessageList.size() > 0 && mMessageList.get(posi).getMsg_name() != null 
			        && !mMessageList.get(posi).getMsg_name().equals(""))
			     holder.tv_message_auther.setText(mMessageList.get(posi).getMsg_name());
			else
				 holder.tv_message_auther.setText("");
			
            //消息来源
            String notice_from = mMessageList.get(posi).getApp_msg_sign();
            //消息内容
            String content = mMessageList.get(posi).getTitle();
            logE(notice_from + "******************" + content);
            
            if (mMessageList != null && mMessageList.size() > 0) {
                if(AppStrStatic.TAG_MESSAGE_ID_ASSISTANT.equals(mMessageList.get(posi).getMessage_id())){
                    holder.tv_index_message_from.setVisibility(View.INVISIBLE);
                }else if(AppStrStatic.TAG_MESSAGE_ID_OFFICIAL.equals(mMessageList.get(posi).getMessage_id())){
                    holder.tv_index_message_from.setVisibility(View.INVISIBLE);
                }else if(AppStrStatic.TAG_MESSAGE_ID_ATTENCE.equals(mMessageList.get(posi).getMessage_id())){
                    holder.tv_index_message_from.setVisibility(View.INVISIBLE);
                }else if(AppStrStatic.TAG_MESSAGE_ID_GROUP.equals(mMessageList.get(posi).getMessage_id())){
                    if (notice_from != null && !notice_from.equals("")&& !notice_from.equals("null")){
                        holder.tv_index_message_from.setVisibility(View.VISIBLE);
                        holder.tv_index_message_from.setText("["+notice_from+"]" + " ");
                    } else {
                        holder.tv_index_message_from.setText("");
                        holder.tv_index_message_from.setVisibility(View.INVISIBLE);
                    }
                }else{
                    if (notice_from != null && !notice_from.equals("")&& !notice_from.equals("null")){
                        holder.tv_index_message_from.setVisibility(View.VISIBLE);
                        holder.tv_index_message_from.setText("["+notice_from+"]" + " ");
                    } else {
                        holder.tv_index_message_from.setText("");
                        holder.tv_index_message_from.setVisibility(View.INVISIBLE);
                    }
                }
                if (content == null || content.equals("")) {
                    holder.tv_message_content.setText(hint_title);
                } else {
                    holder.tv_message_content.setText(content);
                }
            }
            
            //消息时间
            if (mMessageList != null && mMessageList.size() > 0) {
                String time = mMessageList.get(posi).getCreate_time();
                if (time != null && !time.equals("")) {
                    if(content == null || content.equals("")|| content.equals(hint_title)){
                        holder.tv_message_time.setText("");
                    }else{
                        holder.tv_message_time.setText(PubMehods.getTimeHint(Long.valueOf(time),severTime));
                    }
                } else {
                    holder.tv_message_time.setText("");
                }
            }
            
			return converView;
		}

	}
	
	//加载图片处理结果
	private void showloadPicResult(int posi,ImageLoader imageLoader, String img_url,ImageView iv_message_block, ImageView imageAware,int trouble_On_Off) {
        if (posi == 0) {
            showloadPic(posi, imageLoader, img_url, iv_message_block, imageAware, trouble_On_Off, optionsXiao);
        } else if (posi == 1) {
            showloadPic(posi, imageLoader, img_url, iv_message_block, imageAware, trouble_On_Off, optionsYuan);
        } else if (posi == 2) {
            showloadPic(posi, imageLoader, img_url, iv_message_block, imageAware, trouble_On_Off, optionsBan);
        } else{
            if(AppStrStatic.TAG_MESSAGE_ID_ASSISTANT.equals(mMessageList.get(posi).getMessage_id())){
                showTroubleState(iv_message_block, 0);
                PubMehods.loadServicePic(imageLoader,img_url,imageAware, optionsAssistant);
            }else if(AppStrStatic.TAG_MESSAGE_ID_OFFICIAL.equals(mMessageList.get(posi).getMessage_id())){
                showTroubleState(iv_message_block, 0);
                PubMehods.loadServicePic(imageLoader,img_url,imageAware, optionsOfficial);
            }else if(AppStrStatic.TAG_MESSAGE_ID_ATTENCE.equals(mMessageList.get(posi).getMessage_id())){
                showTroubleState(iv_message_block, 0);
                PubMehods.loadServicePic(imageLoader,img_url,imageAware, optionsAttdence);
            }else if(AppStrStatic.TAG_MESSAGE_ID_GROUP.equals(mMessageList.get(posi).getMessage_id())){
                showTroubleState(iv_message_block, trouble_On_Off);
                PubMehods.loadServicePic(imageLoader,img_url,imageAware, optionsQun);
            }
        }
        imageAware.setTag(img_url);
    }
	
	private void showloadPic(int posi,ImageLoader imageLoader, String img_url,ImageView iv_message_block,
	        ImageView imageAware,int trouble_On_Off,DisplayImageOptions options) {
	    if(AppStrStatic.TAG_MESSAGE_ID_ASSISTANT.equals(mMessageList.get(posi).getMessage_id())){
            showTroubleState(iv_message_block, 0);
            PubMehods.loadServicePic(imageLoader,img_url,imageAware, optionsAssistant);
        }else if(AppStrStatic.TAG_MESSAGE_ID_OFFICIAL.equals(mMessageList.get(posi).getMessage_id())){
            showTroubleState(iv_message_block, 0);
            PubMehods.loadServicePic(imageLoader,img_url,imageAware, optionsOfficial);
        }else if(AppStrStatic.TAG_MESSAGE_ID_ATTENCE.equals(mMessageList.get(posi).getMessage_id())){
            showTroubleState(iv_message_block, 0);
            PubMehods.loadServicePic(imageLoader,img_url,imageAware, optionsAttdence);
        }else if(AppStrStatic.TAG_MESSAGE_ID_GROUP.equals(mMessageList.get(posi).getMessage_id())){
            showTroubleState(iv_message_block, trouble_On_Off);
            PubMehods.loadServicePic(imageLoader,img_url,imageAware, optionsQun);
        }else{
            showTroubleState(iv_message_block, 0);
            PubMehods.loadServicePic(imageLoader,img_url,imageAware, options);
        }
    }
	
	HashMap<Integer, View> lmap = new HashMap<Integer, View>();
	public class MydapterRong extends BaseAdapter {

		@Override
		public int getCount() {
			if (mMessageListRong != null)
				return mMessageListRong.size();
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
		public View getView(final int posi, View converView, ViewGroup arg2) {
			
			ViewHolder holder;
			if (lmap.get(posi) == null) {
				converView = LayoutInflater.from(A_Main_My_Message_Acy.this).inflate(R.layout.item_message, null);
				holder = new ViewHolder();
				
				holder.iv_message_type_pic = (CircleImageView)converView.findViewById(R.id.iv_index_message_type_pic);
				holder.tv_message_auther = (TextView)converView.findViewById(R.id.tv_index_message_auther);
				holder.tv_message_time = (TextView)converView.findViewById(R.id.tv_index_message_time);
				holder.tv_message_content = (TextView)converView.findViewById(R.id.tv_index_message_content);
				holder.tv_index_message_from = (TextView)converView.findViewById(R.id.tv_index_message_from);
				holder.tv_no_read_count = (TextView)converView.findViewById(R.id.tv_no_read_count);
				holder.iv_message_block = (ImageView)converView.findViewById(R.id.iv_message_block);
				
				lmap.put(posi, converView);
				converView.setTag(holder);
			}else{
				converView = lmap.get(posi);
				holder = (ViewHolder) converView.getTag();
			}
			holder.iv_message_block.setVisibility(View.GONE);
			//头像
			String img_url = mMessageListRong.get(posi).getBg_img();
            if(img_url != null && img_url.length()>0 && !img_url.equals("")){
                PubMehods.loadServicePic(imageLoader,img_url,holder.iv_message_type_pic, optionsSingle);
            }else{
                PubMehods.loadServicePic(imageLoader,"",holder.iv_message_type_pic, optionsSingle);
            }
			//未读数量
			int no_count = Integer.parseInt(mMessageListRong.get(posi).getCount());
			if (no_count == 0) {
				holder.tv_no_read_count.setVisibility(View.GONE);
			} else if (0 < no_count && no_count <= 99) {
				holder.tv_no_read_count.setVisibility(View.VISIBLE);
				holder.tv_no_read_count.setBackgroundResource(R.drawable.icon_message_noread);
				holder.tv_no_read_count.setText(no_count + "");
			} else if(99<no_count&&no_count<=999)
			{
				holder.tv_no_read_count.setVisibility(View.VISIBLE);
				holder.tv_no_read_count.setBackgroundResource(R.drawable.icon_rong_message_noread);
				holder.tv_no_read_count.setText(no_count+"");
			}else if(no_count>999)
			{
				holder.tv_no_read_count.setVisibility(View.VISIBLE);
				holder.tv_no_read_count.setBackgroundResource(R.drawable.icon_rong_message_noread);
				holder.tv_no_read_count.setText("999+");
			}
			
			//和我聊天者名字
			holder.tv_message_auther.setText(mMessageListRong.get(posi).getMsg_name());
			
			//收到消息时间
			String time = mMessageListRong.get(posi).getCreate_time();
            if (time != null && !time.equals("")) {
                holder.tv_message_time.setText(PubMehods.getTimeHint(Long.valueOf(time),severTime));
            } else {
                holder.tv_message_time.setText("");
            }
			//消息内容
			holder.tv_message_content.setText(mMessageListRong.get(posi).getTitle());
			holder.tv_index_message_from.setVisibility(View.INVISIBLE);//隐藏掉群组中的消息
			
//			logD("教师##########################单聊会话列表");
			converView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()){
                        callSb(posi);
                    }else{
                        PubMehods.showToastStr(A_Main_My_Message_Acy.this, "请联网后执行此操作");
                    }
                    return false;
                }
            });
            
            converView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
//                  if (A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {//没网不可以看，会出现空输入框
                      mMessageListRong.get(posi).setCount("0");
                      goChatJudge(A_Main_My_Message_Acy.this, Conversation.ConversationType.PRIVATE, 
                                  mMessageListRong.get(posi).getTargetId(), mMessageListRong.get(posi).getMsg_name());
                      updateRongAdapter();
//                  } else {
//                      PubMehods.showToastStr(A_Main_My_Message_Acy.this,
//                              R.string.error_title_net_error);
//                  }
                }
            });
            
			return converView;
		}

	}
	
	class ViewHolder {
		TextView tv_no_read_count;
		CircleImageView iv_message_type_pic;
		TextView tv_message_auther;
		TextView tv_message_time;
		TextView tv_message_content;
		TextView tv_index_message_from;
	    ImageView iv_message_block;
	}

	/**************************************************融云模块*开始***************************************************/
	
	Cpk_Index_Notice_Message groupInfo = null;
	//连接融云
	private void connectRoogIm(String rongToken) {
        logE("学生——connectRoogIm()，融云连接开始~~~~~~~");
        if (getApplicationInfo() == null || getApplicationInfo().packageName == null || getApplicationContext() == null) {
            showConnRongYunResult();
            return;
        }
		if (getApplicationInfo().packageName.equals(A_0_App.getCurProcessName(getApplicationContext()))) {
	        logE("学生——()，融云连接开始~~~~22~~~");
	        RongIM.connect(rongToken, new RongIMClient.ConnectCallback() {
	            @Override
	            public void onTokenIncorrect() {
	                logE("学生——connectRoogIm()，融云连接断开");
	            	if (isFinishing())
						return;
	            	 if(A_0_App.USER_STATUS.equals("2"))
					    A_0_App.getInstance().showExitDialog(A_Main_My_Message_Acy.this, getResources().getString(R.string.str_token_timeout));
					 if (mMessageList != null && mMessageList.size() > 0)
                         showLoadResult(false, true, false);
                    else
                         showLoadResult(false, false, false);
	            }
	            
	            @Override
	            public void onSuccess(String userid) {
                    if (userid == null) {
                        logE("连接融云成功，userid = null");
                        logTest("连接融云成功，userid = null");
                    }else{
                        logE("连接融云成功，userid !!= null");
                    }
                    if(RongIM.getInstance() != null && A_0_App.USER_UNIQID != null && A_0_App.USER_NAME != null){
	                    RongIM.getInstance().setCurrentUserInfo(new UserInfo(A_0_App.USER_UNIQID, A_0_App.USER_NAME, Uri.parse(A_0_App.USER_POR_URL)));
	                }
	                RongIM.getInstance().setMessageAttachedUserInfo(true);
	            	if(!ifLoadUpLoadRongYunData){
	            	    ifLoadUpLoadRongYunData = true;
	            	    updateRongYunInfoFirstEnter();//首次进入   异步加载监听和传送融云数据
	            	}else{
	            	    logE("不是首次进入 已经 异步加载监听和传送融云数据");
	            	}
	                
	            	getNoTroubleState();//获取群聊免打扰状态
	            	  RongIM.getInstance().getConversationList(new ResultCallback<List<Conversation>>() {
						
						@Override
						public void onSuccess(List<Conversation> conversationList) {
							 if(conversationList == null || conversationList.size() <= 0){
				                	//没有聊天return
				                   clearDataRong(false);
								   adapter.notifyDataSetChanged();
								   adapterRong.notifyDataSetChanged();
				                   if (mMessageList != null && mMessageList.size() > 0)
				                        showLoadResult(false, true, false);
				                   else
				                        showLoadResult(false, false, false);
				                   return;
				                }
				                clearDataRong(false);
				                logTest("融云绘画列表个数 =" + conversationList.size());
				                boolean singleQun = false;
				                String targetIdLength = "";
								for (int i = 0; i < conversationList.size(); i++) {
									
			                        if (RongIM.getInstance() == null) {
			                            logTest("RongIM.getInstance() == null");
			                            return;
			                        }
									Conversation conversation  = conversationList.get(i);
									ConversationType type = conversation.getConversationType();
									MessageContent message = conversation.getLatestMessage();
									
									Cpk_Index_Notice_Message rongyun = new Cpk_Index_Notice_Message();
									
									int count = conversation.getUnreadMessageCount();//数量
									String time = PubMehods.subStrTime(conversation.getSentTime());//时间
									String content = hint_title;//内容
									
									if (message instanceof TextMessage) {
										TextMessage textMessage=(TextMessage) message;
										textMessage.getExtra();
										content = textMessage.getContent();
									} else if(message instanceof ImageMessage){
										content = "[图片]";
									}else if(message instanceof VoiceMessage){
										content = "[语音]";
									}else if(message instanceof LocationMessage){
										content = "[位置]";
									}else if(message instanceof WYZFNoticeContent){
										WYZFNoticeContent wyzfNoticeContent=(WYZFNoticeContent) message;
										content=wyzfNoticeContent.getTitleStr();
										if (!wyzfNoticeContent.getNoticetime().equals("")||wyzfNoticeContent.getNoticetime()!=null) {
											try {
												time = PubMehods.subStrTime(Long.parseLong(wyzfNoticeContent.getNoticetime()));
											} catch (Exception e) {
												// TODO: handle exception
											}
										}
									}
									// 连接融云接收到的消息
									if (type.equals(ConversationType.GROUP)) {//target为群组id
									    logE("群组=content=" + content );
										if (!singleQun) {
											singleQun = true;
											int mess_posi = searchPosiWhereMessId(mMessageList, AppStrStatic.TAG_MESSAGE_ID_GROUP);
											if (mMessageList.size() > 0 && mess_posi >= 0) {
											    String sendUserId = conversation.getSenderUserId();
			                                    List<String> dd = receiveMessCompare(sendUserId);
			                                    if (sendUserId != null && !"".equals(sendUserId))
			                                        getAndUploadNipickName(sendUserId, 0);
			                                    mMessageList.get(mess_posi).setTitle(content);//发送的内容
			                                    if (dd.size() >= 1) {
			                                        mMessageList.get(mess_posi).setApp_msg_sign(dd.get(0));//发送者名字
			                                    }else{
                                                    if (sendUserId != null && !"".equals(sendUserId))
                                                        mMessageList.get(mess_posi).setApp_msg_sign("获取中…");// 发送者名字
                                                    else
                                                        mMessageList.get(mess_posi).setApp_msg_sign("");
			                                    }
											    
												mMessageList.get(mess_posi).setCount(String.valueOf(count));
												mMessageList.get(mess_posi).setCreate_time(time);
												mMessageList.get(mess_posi).setTargetId(conversation.getTargetId());
												mMessageList.get(mess_posi).setConversationtype(type + "");
											}
											updateBaseAdapter();
										}else{
											logE("融云返回了两个群，严重--------");
										}
									}else{
									    String targetId = conversation.getTargetId();
									    targetIdLength += targetId+",";
									    List<String> dd = receiveMessCompare(targetId);
			                            if (dd.size() >= 2) {
			                                rongyun.setMsg_name(dd.get(0));//给我发送消息者名字
			                                rongyun.setBg_img(dd.get(1));
			                            }else{
			                                rongyun.setMsg_name("获取中…");
			                                rongyun.setBg_img("");
			                            }
										rongyun.setCount(String.valueOf(count));
			                            rongyun.setTitle(content);
			                            rongyun.setCreate_time(time);
			                            rongyun.setTargetId(targetId);
			                            rongyun.setConversationtype(type +"");
                                        if (rongyun != null)
                                            mMessageListRong.add(rongyun);
									}
								}
			                    if (targetIdLength != "" && targetIdLength.length() > 0) {
			                        String str = targetIdLength.substring(0, targetIdLength.length() - 1);
			                        getAndUploadNipickName(str, 1);
			                    }else{
			                        logE("targetIdLength = kong");
			                    }
			                    saveCacheList(mMessageListRong);
								logD("连接融云成功，教师--获取数据成功展示结果");
								updateRongAdapter();
			                    if (mMessageList != null && mMessageList.size() > 0){
			                        logD("连接融云成功，教师--获取数据成功展示结果--111");
			                        showLoadResult(false, true, false);
			                    }else{
			                        logD("连接融云成功，教师--获取数据成功展示结果--222");
			                        showLoadResult(false, false, false);
			                    }
						}
						
						@Override
						public void onError(ErrorCode arg0) {
							
						}
					});
	            }
	            @Override
	            public void onError(RongIMClient.ErrorCode errorCode) {
	                   logE( "-连接融云-onError" + errorCode);
	                    if (mMessageList != null && mMessageList.size() > 0)
	                        showLoadResult(false, true, false);
	                    else
	                        showLoadResult(false, false, true);
	            }
	        });
	    }else{
	        showConnRongYunResult();
	    }
	}
	
	private void showConnRongYunResult() {
	    if (mMessageList != null && mMessageList.size() > 0)
            showLoadResult(false, true, false);
        else
            showLoadResult(false, false, true);
        logE( "-连接融云第一层次" + "getApplicationInfo().packageName.equals(A_0_App.getCurProcessName(getApplicationContext()))");
    }
	
    // 接收融云聊天信息
    @SuppressWarnings("static-access")
	private void receiveMessageData() {
        RongIM.getInstance().setOnReceiveMessageListener(new OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message receverMessage, int arg1) {
                if (!firstLoad_Tag) {
                    logD("开始接收融云消息===是接收到融云消息不是连接融云");
                    String type;
                    String senderTargetId;
                    String senderPortrait = null;// 发送者头像
                    String senderName = null;// 发送者名字
                    String content = null;// 聊天内容
                    String create_time;// 发送时间

                    senderTargetId = receverMessage.getTargetId();
                    type = receverMessage.getConversationType() + "";
                    if(receverMessage.getContent().getUserInfo() != null){
                        senderPortrait = receverMessage.getContent().getUserInfo().getPortraitUri().toString();
                        senderName = receverMessage.getContent().getUserInfo().getName();
                    }
                    create_time = PubMehods.subStrTime(receverMessage.getSentTime());

                    MessageContent mess_Content = receverMessage.getContent();
                    if (mess_Content instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) mess_Content;
                        textMessage.getExtra();
                        content = textMessage.getContent();
                    } else if (mess_Content instanceof ImageMessage) {
                        content = "[图片]";
                    } else if (mess_Content instanceof VoiceMessage) {
                        content = "[语音]";
                    } else if (mess_Content instanceof LocationMessage) {
                        content = "[位置]";
                    } else if (mess_Content instanceof WYZFNoticeContent) {
                        WYZFNoticeContent wyzfNoticeContent = (WYZFNoticeContent) mess_Content;
                        content = wyzfNoticeContent.getTitleStr();
                        logD(wyzfNoticeContent.getNoticetime());
                        if (wyzfNoticeContent.getNoticetime() != "") {
                            try {
                                create_time = PubMehods.subStrTime(Long.parseLong(wyzfNoticeContent
                                        .getNoticetime()));
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                        }
                    }

                    if (receverMessage.getConversationType().equals(ConversationType.GROUP)) {
                        // 接收群消息，第一个listview中更新
                        if (mMessageList != null && mMessageList.size() > 0) {
                            int mess_posi = searchPosiWhereMessId(mMessageList, AppStrStatic.TAG_MESSAGE_ID_GROUP);
                            if(mess_posi >= 0){
                                mMessageList.get(mess_posi).setCreate_time(create_time);
                                mMessageList.get(mess_posi).setCount((Integer.parseInt(mMessageList.get(mess_posi).getCount()) + 1) + "");

                                mMessageList.get(mess_posi).setTitle(content);
                                mMessageList.get(mess_posi).setApp_msg_sign(senderName);// 发送者名字
                            }
                        }
                        updateBaseAdapter();
                    } else {// 接收融云消息Receive消息
                        if (receverMessage.getContent().getUserInfo() == null) {
                            List<String> dd = receiveMessCompare(senderTargetId);
                            getAndUploadNipickName(senderTargetId, 1);
                            if (dd.size() >= 2) {
                                senderName = dd.get(0);// 给我发送消息者名字
                                senderPortrait = dd.get(1);
                            } else {
                                senderName = "获取中…";
                                senderPortrait = "";
                            }
                        }
                        if (!judgeTarget(senderTargetId)) {// 没有就插入
                            Cpk_Index_Notice_Message notice_message = new Cpk_Index_Notice_Message();
                            notice_message.setCount("1");// 未读数量
                            notice_message.setMsg_name(senderName);
                            notice_message.setBg_img(senderPortrait);
                            notice_message.setTitle(content);// 内容
                            notice_message.setCreate_time(create_time);
                            notice_message.setTargetId(senderTargetId);
                            notice_message.setConversationtype(type);

                            mMessageListRong.add(0,notice_message);
                            saveCacheList(mMessageListRong);
                        } else {
                            upDateTarget(senderTargetId, content, create_time);
                        }
                        updateRongAdapter();
                    }
                } else {
                    logE("第一次不加载接收融云——setOnReceiveMessageListener");
                }
                showViewResults();
                return false;
            }
        });

    }
    
    private boolean judgeTarget(String targetId) {
        for (int i = 0; i < mMessageListRong.size(); i++) {
            if (targetId.equals(mMessageListRong.get(i).getTargetId())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 
    * @Title: upDateTarget
    * @Description: TODO(更新聊天个人信息：//内容//时间//未读数量)
    * @param @param targetId
    * @param @param content
    * @param @param create_time    设定文件
    * @return void    返回类型
    * @throws
     */
    private void upDateTarget(String targetId,String content,String create_time) {
        Cpk_Index_Notice_Message temp = null;
        for (int i = 0; i < mMessageListRong.size(); i++) {
            if (targetId.equals(mMessageListRong.get(i).getTargetId())) {
                mMessageListRong.get(i).setTitle(content);
                mMessageListRong.get(i).setCreate_time(create_time);
                String count = (Integer.parseInt(mMessageListRong.get(i).getCount()) +1) +"";
                mMessageListRong.get(i).setCount(count);
                temp = mMessageListRong.get(i);
                mMessageListRong.remove(i);
                i--;
            }
        }
        if (temp != null) {
            mMessageListRong.add(0, temp);
        }
    }
	
	/**
	 * 首次进入页面上传融云所需要的转发信息、位置分享信息
	 */
	private void updateRongYunInfoFirstEnter(){
        UpdateTextTask updateTextTask = new UpdateTextTask(this);
        updateTextTask.execute();
    }

    class UpdateTextTask extends AsyncTask<Void,Integer,Integer>{
        private Context context;
        UpdateTextTask(Context context) {
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
        	 logD("开始上传融云数据");
        }
        /**
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
         */
        @Override
        protected Integer doInBackground(Void... params) {
        	inIt();
        	RongIM.getInstance().setMessageAttachedUserInfo(true);
            return null;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {
            logD("上传融云数据完毕");
        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
        	
        }
    }
    
    // 打开聊天框判断——单聊和群组聊天
    private void goChatJudge(final Context context, final ConversationType conversationType, final String targetId, final String title) {
        if (RongIM.getInstance() == null) {
            reconnect(A_0_App.USER_QUTOKEN, context, conversationType, targetId, title);
            
        }else{
            RongIM.getInstance().startConversation(context, conversationType, targetId, title);
        }
    }
    
    private void reconnect(String token,final Context context, final ConversationType conversationType, final String targetId, final String title) {
        A_0_App.getInstance().showProgreDialog(A_Main_My_Message_Acy.this, "",true);
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
              A_0_App.getInstance().CancelProgreDialog(A_Main_My_Message_Acy.this);
              PubMehods.showToastStr(A_Main_My_Message_Acy.this, "抱歉，重连接服务器失败，请检查您的网络设置");
            }

            @Override
            public void onSuccess(String s) {
              A_0_App.getInstance().CancelProgreDialog(A_Main_My_Message_Acy.this);
              RongIM.getInstance().startConversation(context, conversationType, targetId, title);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
              A_0_App.getInstance().CancelProgreDialog(A_Main_My_Message_Acy.this);
              PubMehods.showToastStr(A_Main_My_Message_Acy.this, "抱歉，重连接服务器失败，请检查您的网络设置");
            }
        });

    }
    
    /* **********************异步把通讯录信息送给融云********************************************* */
	
	private void inIt() {
        RongIM.setConversationBehaviorListener(new ConversationBehaviorListener() {
			@Override
			public boolean onUserPortraitLongClick(Context arg0,ConversationType arg1, UserInfo arg2) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onUserPortraitClick(Context arg0,ConversationType arg1, UserInfo arg2) {
				Intent intent=new Intent(arg0, B_Mess_Persion_Info.class);
				intent.putExtra("uniqid", arg2.getUserId());
				arg0.startActivity(intent);
				return false;
			}

			@Override
			public boolean onMessageLongClick(Context arg0, View arg1,
					Message arg2) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onMessageLinkClick(Context arg0, String arg1) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onMessageClick(Context arg0, View arg1, Message arg2) {
				// TODO Auto-generated method stub
				if (arg2.getContent() instanceof LocationMessage) {
					Intent in = new Intent(arg0,Conversation_Location_Acy.class);// 位置
					in.putExtra("location", arg2.getContent());
					arg0.startActivity(in);
				} else if (arg2.getContent() instanceof ImageMessage) {
					ImageMessage imageMessage = (ImageMessage) arg2
							.getContent();
					Intent intent = new Intent(arg0,
							B_Side_Found_BigImage2.class); // 图片
					intent.putExtra("photo",imageMessage.getLocalUri() == null ? imageMessage
									.getRemoteUri() : imageMessage
									.getLocalUri());
					if (imageMessage.getThumUri() != null) {
						intent.putExtra("thumbnail",imageMessage.getRemoteUri());
					}
					arg0.startActivity(intent);
				} else if (arg2.getContent() instanceof WYZFNoticeContent) {
					WYZFNoticeContent customizeMessage = (WYZFNoticeContent) arg2.getContent();
					if (customizeMessage.getType().equals("1")) {
						Intent in = new Intent(arg0, B_Mess_Notice_Detail.class);//通知详情
						in.putExtra("message_id",customizeMessage.getNoticeId());
						in.putExtra("mess_content", customizeMessage.getDetailStr());//同列表进入
						in.putExtra("acy_type", 1);//转发
						arg0.startActivity(in);
					} else if (customizeMessage.getType().equals("2")) {
						Intent in = new Intent(arg0,B_Side_Acy_list_Detail_Acy.class);//活动详情
						in.putExtra("acy_detail_id",customizeMessage.getNoticeId());
						in.putExtra("acy_type", 1);//转发进入
						arg0.startActivity(in);
					} else if (customizeMessage.getType().equals("3")) {
						Intent in = new Intent(arg0,B_Side_Lectures_Detail_Acy.class);//讲座详情
						in.putExtra("lecture_id",customizeMessage.getNoticeId());
						in.putExtra("acy_type", 2);//同列表进入
						arg0.startActivity(in);

					}else if (customizeMessage.getType().equals("4")) {
						Intent in = new Intent(arg0,B_Mess_Notice_Detail_MessText.class);//短信通知详情
						in.putExtra("message_id",customizeMessage.getNoticeId());
						in.putExtra("acy_type", 2);//同列表进入
						in.putExtra("mess_content", customizeMessage.getDetailStr());//同列表进入
						arg0.startActivity(in);

					}else if (customizeMessage.getType().equals("5")){
						Intent in = new Intent(arg0,B_Side_Found_Detail_Forward.class);//失物招领详情
						in.putExtra("lost_id",customizeMessage.getNoticeId());
						
						arg0.startActivity(in);
					}else if (customizeMessage.getType().equals("6")){
					    String share_time  = customizeMessage.getNoticetime();
                        Intent in = new Intent(arg0,B_Side_Course_Acy.class);//课程表分享详情
                        in.putExtra("acy_type",10);
                        in.putExtra("course_id",customizeMessage.getNoticeId());
                        in.putExtra("course_user_uniqid",customizeMessage.getCourse_user_uniqid());
                        if(share_time.length()>10)
                            in.putExtra("share_time",share_time.substring(0, 10));
                        else
                            in.putExtra("share_time",share_time);
                        arg0.startActivity(in);
                    }else if (customizeMessage.getType().equals("7")){
                        Intent in = new Intent(arg0,B_Side_Info_1_Detail_Acy.class);//资讯转发
                        in.putExtra("info_id",customizeMessage.getNoticeId());
                        in.putExtra("acy_type", 3);//转发进入
                        arg0.startActivity(in);
                    }else if (customizeMessage.getType().equals("8")){
                        Intent intent = new Intent(arg0,Pub_WebView_Banner_Acy.class);//资讯专题转发
                        intent.putExtra("title_text",customizeMessage.getTitleStr());// 正常列表进入
                        intent.putExtra("url_text", customizeMessage.getNoticeId());//URL链接
                        intent.putExtra("tag_show_refresh_btn", "1");
                        intent.putExtra("tag_show_forward_btn", "0");
                        intent.putExtra("tag_skip", "1");
                        arg0.startActivity(intent);
                    }
				}
				return false;
			}
		});

	}
	
	//删除好友
   private void removeSb(final String targetId,final int no) {
        if (RongIM.getInstance() == null) {
            PubMehods.showToastStr(A_Main_My_Message_Acy.this, "聊天服务器已断开，请检查您的网络");
            RongIM.connect(A_0_App.USER_QUTOKEN, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                  A_0_App.getInstance().CancelProgreDialog(A_Main_My_Message_Acy.this);
                  PubMehods.showToastStr(A_Main_My_Message_Acy.this, "抱歉，重连接服务器失败，请检查您的网络设置");
                }

                @Override
                public void onSuccess(String s) {
                  A_0_App.getInstance().CancelProgreDialog(A_Main_My_Message_Acy.this);
                  removeToSb(targetId, no);
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {
                  A_0_App.getInstance().CancelProgreDialog(A_Main_My_Message_Acy.this);
                  PubMehods.showToastStr(A_Main_My_Message_Acy.this, "抱歉，重连接服务器失败，请检查您的网络设置");
                }
            });
        }else{
            removeToSb(targetId, no);
        }
    }
	   
	private void removeToSb(String targetId,final int no) {
		RongIM.getInstance().removeConversation(ConversationType.PRIVATE, targetId, new ResultCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean arg0) {
                if (no >= mMessageListRong.size())
                    return;
				PubMehods.showToastStr(A_Main_My_Message_Acy.this, "删除该聊天成功");
				mMessageListRong.remove(no);
                saveCacheList(mMessageListRong);
				adapterRong.notifyDataSetChanged();
			}
			
			@Override
			public void onError(ErrorCode arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	/**
	 * 
	* @Title: getContaceInfo
	* @Description: TODO(获取群组信息)
	* @param     设定文件
	* @return void    返回类型
	 */
	private void getGourpInfoList() {
	    if(A_0_App.USER_QUNIQID == null||A_0_App.USER_QUNIQID.length() <= 0){
	        logE("群组ID为空");
	        return;
	    }
        A_0_App.getApi().getGroupInfo(A_Main_My_Message_Acy.this, A_0_App.USER_TOKEN, A_0_App.USER_QUNIQID, new InterGroupInfo() {
            @Override
            public void onSuccess(Cpk_Group_Info info) {
                if (isFinishing())
                    return;
                List<Cpk_Group_Info_item> list = info.getMlist();
                if (list != null && list.size() > 0) {
                   joinContaces( list);
                } else {
                    logE("没有群聊组信息");
                }
                logTest("成功--获取群组信息" + list.size());
            }
        },new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
    }
	

	/**
	 * 
	* @Title: getAndUploadNipickName
    * @Description: TODO(获取名字和头像，上传数据)
    * @param @param strTemp 最后一位不带有逗号
    * @param @param type    返回类型  type =0 群组，type =1 单聊  
	* @throws
	 */
    private void getAndUploadNipickName(final String strTemp, final int type) {
        A_0_App.getApi().getRongYunName(A_Main_My_Message_Acy.this, strTemp, A_0_App.USER_TOKEN,
                new InterRongYunName() {
                    @Override
                    public void onSuccess(List<Cpk_RongYun_True_Name> mList) {
                        logTest(strTemp +",getAndUploadNipickName," +mList.size());
                        if (isFinishing())
                            return;
                        int mess_posi = searchPosiWhereMessId(mMessageList, AppStrStatic.TAG_MESSAGE_ID_GROUP);
                        if (mList != null && mList.size() > 0) {
                            if (type == 0) {//更新群组列表数据并且刷新页面 
                                if (mList.size() == 1) {
                                    Cpk_RongYun_True_Name persion_Info = mList.get(0);
                                    if (mess_posi >= 0) {
                                        if (persion_Info.getIs_delete().equals("0"))
                                            mMessageList.get(mess_posi).setApp_msg_sign(
                                                    persion_Info.getName());
                                        else
                                            mMessageList.get(mess_posi).setApp_msg_sign(
                                                    persion_Info.getName() + "(已删除)");
                                    }
                                } else {
                                    logTest("群组获取名字和头像，上传数据错误");
                                }
                                updateBaseAdapter();
                            } else if (type == 1) {//更新个人聊天列表数据并且刷新页面 
                                for (int j = 0; j < mList.size(); j++) {
                                    if (mList.get(j).getIs_delete().equals("0"))//存储用
                                        add_Contacts_Object(mList.get(j));
                                }
                                updateListPersionData(mList);
                            }
                        }else{
                            if (type == 0) {// 群发送者为“”
                                if (strTemp.equals("")) {
                                    if (mess_posi >= 0) {
                                        mMessageList.get(mess_posi).setApp_msg_sign("");
                                    }
                                }
                                updateBaseAdapter();
                            }
                        }
                    }
                },new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });

    }
    
    private void joinContaces(List<Cpk_Group_Info_item> list) {
        for (int i = 0; i < list.size(); i++) {
            Cpk_RongYun_True_Name contact = new Cpk_RongYun_True_Name();
            contact.setName(list.get(i).getName());
            contact.setPhoto_url(list.get(i).getPhoto_url());
            contact.setTargetId(list.get(i).getUniqid());
            add_Contacts_Object(contact);
        }
    }
	
    private List<Cpk_RongYun_True_Name> mListContacts = new ArrayList<Cpk_RongYun_True_Name>();
    private void add_Contacts_Object(Cpk_RongYun_True_Name obj) {
        if (obj != null && mListContacts != null) {
            if (mListContacts.size() > 0) {
                for (int i = 0; i < mListContacts.size(); i++) {
                    if (obj.getTargetId().equals(mListContacts.get(i).getTargetId())) {
                        mListContacts.remove(i);
                        i--;
                    }
                }
            }
            mListContacts.add(obj);
        }
    }
    
    private void updateListPersionData(List<Cpk_RongYun_True_Name> mServiceList) {
        if (mListContacts != null && mListContacts.size() > 0) {
            for (int i = 0; i < mMessageListRong.size(); i++) {
                for (int j = 0; j < mServiceList.size(); j++) {
                    if(mMessageListRong.get(i).getTargetId().equals(mServiceList.get(j).getTargetId())){
                        if(mServiceList.get(j).getIs_delete().equals("0")){
                            mMessageListRong.get(i).setMsg_name(mServiceList.get(j).getName());
                            mMessageListRong.get(i).setBg_img(mServiceList.get(j).getPhoto_url());
                        }else{//移除掉已经删除的用户
                            mMessageListRong.get(i).setMsg_name(mServiceList.get(j).getName() + "(已删除)");
                            mMessageListRong.get(i).setBg_img(mServiceList.get(j).getPhoto_url());
                        }
                    }
                }
            }
        }
        updateRongAdapter();
        saveCacheList(mMessageListRong);
    }
    
    
    //接收消息用到的，匹配昵称和名字 0:名字，1是图片
    private List<String> receiveMessCompare(String targetId) {
        List<String> temp = new ArrayList<String>();
        if (mListContacts != null && mListContacts.size() > 0 && targetId != null && !targetId.equals("")) {
            for (int j = 0; j < mListContacts.size(); j++) {
                if (targetId.equals(mListContacts.get(j).getTargetId())) {
                    temp.add(mListContacts.get(j).getName().toString());// 名字
                    String imageUrl = mListContacts.get(j).getPhoto_url().toString();
                    
                    if(imageUrl != null && imageUrl.length()>0 && !imageUrl.equals("")&& !imageUrl.equals("null"))
                        temp.add(imageUrl);// 图片
                    else
                        temp.add("");// 图片
                }
            }
        }
        return temp;
    }
    
    //上传百度推送和个推Id
    private void upLoadAllChannel() {
        logD("获取到的channel_id=" + A_0_App.getInstance().getChannelId());
        logD("获取到的client_id=" + A_0_App.getInstance().getClientid());
        
        String channel_Id = A_0_App.getInstance().getChannelId();
        String client_Id = A_0_App.getInstance().getClientid();
        
        if ("".equals(A_0_App.getInstance().getChannelIdStr(AppStrStatic.KEY_USER_LOGIN_CHANNEL_ID))) {
            logE("需要上传ChannelID");
            if (channel_Id != null && !channel_Id.equals("")) {
                logE("开始上传ChannelId");
                upLoadChannelID(channel_Id,"");
            } else {
                logE("获取到的channel_id=空，不能上传");
            }
        } else {
            logE("已传过ChannelID,不需重新上传");
        }
        
        if ("".equals(A_0_App.getInstance().getChannelIdStr(AppStrStatic.KEY_USER_LOGIN_CLIENT_ID))) {
            logE("需要上传CLIENT_ID");
            if (client_Id != null && !client_Id.equals("")) {
                logE("开始上传client_Id");
                upLoadChannelID("",client_Id);
            } else {
                logE("获取到的client_Id=空，不能上传");
            }
        } else {
            logE("已传过client_Id,不需重新上传");
        }
    }
    
	   
   /**
   * @Description: TODO(上传群组信息到服务器，上传CHannelID)
    */
    private void updateContactList(){
        UpdateContactTaskRong updateTextTask = new UpdateContactTaskRong(this);
        updateTextTask.execute();
    }

    class UpdateContactTaskRong extends AsyncTask<Void,Integer,Integer>{
        private Context context;
        UpdateContactTaskRong(Context context) {
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
            getGourpInfoList();
            upLoadAllChannel();
            
        	ACache maACache = ACache.get(A_Main_My_Message_Acy.this);
        	JSONObject jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_mySide+A_0_App.USER_UNIQID);
            if (jsonObject==null) {
            	getGetInfoList();//身边数据加载
			}
        	
            return null;
        }

        /**
         * 运行在ui线程中，在doInBackground
         * ()执行完毕后执行
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
    * @Description: TODO(上传用户ChannelID)
     */
    private void upLoadChannelID(final String device_token,final String getui_client_id) {
        A_0_App.getApi().upLoad_Chanel_Id(A_Main_My_Message_Acy.this,device_token,getui_client_id,
                A_0_App.USER_TOKEN, new Inter_UpLoad_ChannelId() {
            @Override
            public void onSuccess(String msg) {
                if(device_token != null && !"".equals(device_token) ){
                    logD("上传ChannelID成功" + msg);
                    A_0_App.getInstance().saveChannelId(AppStrStatic.KEY_USER_LOGIN_CHANNEL_ID,
                            A_0_App.getInstance().getChannelId());
                }
                if(getui_client_id != null && !"".equals(getui_client_id) ){
                    logD("上传getui_client_id成功" + msg);
                    A_0_App.getInstance().saveChannelId(AppStrStatic.KEY_USER_LOGIN_CLIENT_ID,
                            A_0_App.getInstance().getClientid());
                }
            }
        },new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    
    //RongIM.getInstance().refreshUserInfoCache(new UserInfo("userId", "啊明", Uri.parse("http:/.png")));
    @Override
    public UserInfo getUserInfo(String userId) {
        for (Cpk_RongYun_True_Name persion : mListContacts) {
            logTest("IMkit--getUserInfo" + persion.getName()+",,," + persion.getPhoto_url());
            if (A_0_App.USER_UNIQID.equals(persion.getTargetId())) {
                persion.setPhoto_url(A_0_App.USER_POR_URL);
                persion.setName(A_0_App.USER_NAME);
            }
            if (userId != null && persion.getTargetId().equals(userId)) {
                return new UserInfo(persion.getTargetId(), persion.getName(), Uri.parse(persion.getPhoto_url()));
            }
        }
          return new UserInfo(userId, str_delete_user,  null);
    }
    
    @Override
    public Group getGroupInfo(String arg0) {
        logTest("上传群组信息" + arg0);
        return new Group(A_0_App.USER_QUNIQID, banJiQunLiao, null);
    }
	
	/**************************************************融云模块*结束***************************************************/
    final Handler handle_receiver_mess = new Handler();
    final Runnable mUpadateView = new Runnable() {
        public void run() {
            showLoadResult(false, true, false);
        }
    };

    private void showViewResults() {
        Thread t = new Thread() {
            public void run() {
                handle_receiver_mess.post(mUpadateView); // 高速UI线程可以更新结果了
            }
        };
        t.start();
    }
    
    final Handler handle_Adapter = new Handler();
    final Runnable mUpdateResultshandle_Adapter = new Runnable() {
        public void run() {
            if(adapter != null)
                adapter.notifyDataSetChanged();
            calculateNoReadCount();
        }
    };

    private void updateBaseAdapter() {
        Thread t = new Thread() {
            public void run() {
                handle_Adapter.post(mUpdateResultshandle_Adapter); // 高速UI线程可以更新结果了
            }
        };
        t.start();
        // try {
        // new Handler().postDelayed(new Runnable() {
        // @SuppressWarnings("deprecation")
        // public void run() {
        // logD("updateRongAdapter  adapterRong——刷新");
        // if (adapterRong != null) {
        // adapterRong.notifyDataSetChanged();
        // }
        // }
        // }, REFRESH_DELAY_TIME);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
    }
   
    
    final Handler cwjHandler_one = new Handler();

    final Runnable mUpdateResults_one = new Runnable() {
        public void run() {
            if(adapterRong != null)
                adapterRong.notifyDataSetChanged();
            calculateNoReadCount();
        }
    };

    private void updateRongAdapter() {
        Thread t = new Thread() {
            public void run() {
                cwjHandler_one.post(mUpdateResults_one); // 高速UI线程可以更新结果了
            }
        };
        t.start();
        // try {
        // new Handler().postDelayed(new Runnable() {
        // @SuppressWarnings("deprecation")
        // public void run() {
        // logD("updateBaseAdapter  adapter——刷新");
        // if (adapter != null) {
        // adapter.notifyDataSetChanged();
        // }
        // }
        // }, REFRESH_DELAY_TIME);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
    }
    
    private void calculateNoReadCount() {
        int count = 0;
        if (mMessageList != null && mMessageList.size() > 0) {
            if (mMessageList != null && mMessageList.size() > 0) {
                for (int i = 0; i < mMessageList.size(); i++) {
                    if (mMessageList.get(i).getCount() != null && !mMessageList.get(i).getCount().equals("")) {
                        int temp = Integer.parseInt(mMessageList.get(i).getCount());
                        count += temp;
                    }

                }
            }
        }
        
        if (mMessageListRong != null && mMessageListRong.size() > 0) {
            for (int i = 0; i < mMessageListRong.size(); i++) {
                if (mMessageListRong.get(i).getCount() != null && !mMessageListRong.get(i).getCount().equals("")) {
                    int temp = Integer.parseInt(mMessageListRong.get(i).getCount());
                    count += temp;
                }
            }
        }
        logE(count + "未读数量--count");
        A_Main_Acy.getInstance().showMainNoReadMessCount(count);
    }
    
    /**
     * 身边数据异步加载******************************
     */
    private void getGetInfoList() {
    	A_0_App.getApi().GetInfoList(A_0_App.USER_TOKEN, new InterGetInfo() {
			
			@Override
			public void onSuccess(List<Cpk_Banner_Mode> mList,List<Cpk_Module_List> moduleContact, Cpk_Top_List top_List) {}
		},new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
	/***************************************升级*****************************************************/
	private static ProgressDialog updateDialog;
	public void startDownloadApp(String url) {
		if (updateDialog == null) {
			updateDialog = new ProgressDialog(this);
		}
		updateDialog.setIcon(android.R.drawable.ic_dialog_info);
		updateDialog.setTitle("温馨提示");
		updateDialog.setCancelable(false);
		updateDialog.setMessage("正在下载");
		updateDialog.setMax(100);
		ApkDownloader downloader = new ApkDownloader(A_Main_My_Message_Acy.this, url,R.drawable.ic_launcher,
				"", A_Main_My_Message_Acy.this);
		downloader.downLoadApp();
		//updateDialog.show();
	}

	public void onUpdataDownLoadProgross(WebResource resource, int progross) {
		updateDialog.setMessage("正在下载..." + progross + " %");
	}
	
	private void clearData(boolean setNull) {
		if (mMessageList != null) {
			mMessageList.clear();
			if(setNull)
			   mMessageList = null;
		}
	}
	
    // 清除聊天记录
    public void clearQunTMData() {
        if (mMessageList != null && mMessageList.size() > 0) {
            int posi = searchPosiWhereMessId(mMessageList, AppStrStatic.TAG_MESSAGE_ID_GROUP);
            if (posi >= 0) {
                mMessageList.get(posi).setApp_msg_sign("");// 发送者
                mMessageList.get(posi).setTitle(hint_title);// 发送内容
                if (adapterRong != null)
                    adapterRong.notifyDataSetChanged();
            }
        }
    }
    
    // 根据messageId查询此条消息位置
    private int searchPosiWhereMessId(List<Cpk_Index_Notice_Message> mList, String messageId) {
        int posi = -1;
        if (mList != null && mList.size() > 0 && messageId != null && !"".equals(messageId)) {
            for (int i = 0; i < mList.size(); i++) {
                if (messageId.equals(mList.get(i).getMessage_id())) {
                    posi = i;
                }
            }
        }
        return posi;
    }
	
	private void clearDataRong(boolean setNull) {
		try {
            if (mMessageListRong != null) {
            	mMessageListRong.clear();
            	if(setNull)
            		mMessageListRong = null;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
   /*********************************onStart***实例身边的广播和服务**************************************************/
    
    @Override
    protected void onStart() {
        super.onStart();
        logD("onStart()");
        mLocalBroadcastManager.registerReceiver(mReciver, mIntentFilter);
        this.getApplicationContext().bindService(mServiceIntent, conn, BIND_AUTO_CREATE);
        
        if(!A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()){
            A_0_App.getInstance().display_red();
        }
        
    }
    
    public static IBackService iBackService;
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iBackService = null;
            Log.e("BackService", "连接服务器失败");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("BackService", "连接服务器成功");
            iBackService = IBackService.Stub.asInterface(service);
            ToSayServiceLogin();//告诉服务器登录信息          
        }
    };
    
    //告诉用户信息给服务器
    public void ToSayServiceLogin() {
//        JSONObject params = new JSONObject();
//        try {
//            params.put("token", A_0_App.USER_TOKEN);
//            params.put("command", "LOGIN");
//            String app_version = PubMehods.getVerName(A_Main_My_Message_Acy.this);
//            params.put("app_version", app_version);
//            params.put("type", "2");//1是教师，2是学生
//            String sign = PubMehods.getMD5("app_version" + app_version + "command" + "LOGIN"
//                    + "token" + A_0_App.USER_TOKEN + "type" + "2" + AppStrStatic.ZCODE
//                    + AppStrStatic.ZCODE_VALUE);
//            params.put(AppStrStatic.SIGN, sign);
//            
//            String msg = params.toString();
//            boolean isSend = iBackService.sendMessage(msg);
//            Log.e("BackService","用户身份客户端请求==>" + msg);
//            Log.e("BackService", isSend ? "发送用户信息成功" : "发送用户信息失败");
//            if(!isSend){
//                boolean isSend_Once = iBackService.sendMessage(msg);
//                Log.e("BackService", isSend_Once ? "再次用户信息发送成功" : "再次用户信息发送失败");
//            } else {
//                new Handler().postDelayed(new Runnable() {
//                    @SuppressWarnings("deprecation")
//                    public void run() {
//                        //解析服务器返回的信息
//                        JSONObject object;
//                        String message = A_0_App.getInstance().getLoginReciverMessage();
//                        Log.e("BackService","用户身份客户端接收到的登录成功消息==>" + message);
//                        try {
//                            if (message != null && !"".equals(message)) {
//                        		 object = new JSONObject(message);
//                                 String status=object.getString("status");
//                                 String command = object.getString("command");
//                                 if (status.equals("1") && command.equals("LOGIN")) {
//                                     A_0_App.getInstance().setSocket_login(true);
//                                     Log.e("BackService","成功==>验证客户端身份");
//                                     ToSayServiceInfo();
//                                 }else{
//                                     A_0_App.getInstance().setSocket_login(false);
//                                     Log.e("BackService","失败==>验证客户端身份");
//                                 }
//    						}else{
//    						    A_0_App.getInstance().setSocket_login(false);
//    						}
//                           
//                        } catch (JSONException e) {
//                            A_0_App.getInstance().setSocket_login(false);
//                            e.printStackTrace();
//                        }
//                    }
//                }, 300);
//
//            }
//        } catch (Exception e) {
//            Log.e("BackService", "ToSayServiceInfo Exception 发送用户信息失败");
//        }
    }
    
    //告诉用户信息给服务器
    private void ToSayServiceInfo() {
        JSONObject params = new JSONObject();
        try {
            params.put("token", A_0_App.USER_TOKEN);
            params.put("command", "SIDE");
            String app_version = PubMehods.getVerName(A_Main_My_Message_Acy.this);
            params.put("app_version", app_version);
            params.put("type", "2");//1是教师，2是学生
            
            String sign = PubMehods.getMD5("app_version" + app_version + "command" + "SIDE"
                    + "token" + A_0_App.USER_TOKEN + "type" + "2" + AppStrStatic.ZCODE
                    + AppStrStatic.ZCODE_VALUE);
            params.put(AppStrStatic.SIGN, sign);
            
            String msg = params.toString();
            boolean isSend = iBackService.sendMessage(msg);
            Log.e("BackService","客户端请求的参数==>" + msg);
            Log.e("BackService", isSend ? "发送身边信息成功" : "发送身边信息失败");
            if(!isSend){
                boolean isSend_Once = iBackService.sendMessage(msg);
                Log.e("BackService", isSend_Once ? "再次发送身边信息成功" : "再次发送身边信息失败");
            }else{
                new Handler().postDelayed(new Runnable() {
                    @SuppressWarnings("deprecation")
                    public void run() {
                        //解析服务器返回的信息
                        JSONObject object;
                        String message = A_0_App.getInstance().getSideReciverMessage();
                        Log.e("BackService","客户端接收到的身边消息==>" + message);
                        try {
                            if (message != null && !"".equals(message)) {
                                object = new JSONObject(message);
                                String status=object.getString("status");
                                if (status.equals("1")) {
                                    if(object.getString("command").equals("SIDE")){
                                        Log.e("BackService","成功==>身边获取信息");
                                        if(object.getString("badge").equals("1")){
                                            A_Main_Acy.getInstance().showSideNoReadTag(true);
                                        }else{
                                            A_Main_Acy.getInstance().showSideNoReadTag(false);
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("BackService","error---客户端接收到的身边消息==>");
                        }
                    }
                }, 300);
            }
        } catch (Exception e) {
            Log.e("BackService", "ToSayServiceInfo Exception 发送身边信息失败");
        }
    }
    
    private Intent mServiceIntent;
    private BackReciverMessage mReciver;
    private IntentFilter mIntentFilter;
    private LocalBroadcastManager mLocalBroadcastManager;
    
	/*********************************onResume***重读列表数据**************************************************/
    /**
     * OnResume 重新连接融云获取数据
     */
	private void updateListRong(){
		UpdateTextTaskRong updateTextTask = new UpdateTextTaskRong(this);
        updateTextTask.execute();
    }

    class UpdateTextTaskRong extends AsyncTask<Void,Integer,Integer>{
        private Context context;
        UpdateTextTaskRong(Context context) {
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
            return null;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {
            getIndexMessList(firstLoad_Tag);
            getGourpInfoList();//防止删除用户闪动
        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
        	
        }
    }
    
  /********************************************************************************************************************/
    
    // 保存Xml文件list  连接融云成功，更新单聊数据成功，删除聊天人，接收消息数据的时候
    public void saveCacheList(List<Cpk_Index_Notice_Message> mCahcheList) {
        logTest("保存聊天list" + mCahcheList.size());
        XmlUtils.serialize(A_Main_My_Message_Acy.this, "students_" + A_0_App.USER_UNIQID +".xml",mCahcheList);
    }

    // 获取Xml文件list
    public List<Cpk_Index_Notice_Message> getCacheAllList() {
        return XmlUtils.parse(A_Main_My_Message_Acy.this, "students_" + A_0_App.USER_UNIQID +".xml");
    }
    
	/*********************************onResume***异步上传用户信息到融云数据**************************************************/
    
	@Override
	protected void onResume() {
		super.onResume();
        logD("onResume()");
		if (!firstLoad_Tag) {
            if(PubMehods.isFastClick(AppStrStatic.INTERVAL_RONGYUN_CONNECT)){
                logE(AppStrStatic.INTERVAL_RONGYUN_CONNECT + "秒时间内onResume，，return，return");
                return;
            }
		    logD("onResume 重读融云-选择性度学生、教师联系人、上传CHannelID");
		    if(A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()){
		        upLoadAllChannel();//处理推送ID
		        updateListRong();//连接获取融云数据
		    }
	        if (!A_0_App.getInstance().getSocket_login()) {
	            if (A_Main_My_Message_Acy.getInstance() != null
	                    && A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {
	                A_Main_My_Message_Acy.getInstance().ToSayServiceLogin();
	            }
	        }
		} else {
		    logD(" 第一次进入页面不刷新");
			firstLoad_Tag = false;
		}

	}
	
    /**
     * 设置连接状态变化的监听器.
     */
    @SuppressWarnings("static-access")
	public void startListtenerRongYun() {
        RongIM.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
    }

    private class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus) {
                case CONNECTED:// 连接成功。
                    if(!firstLoad_Tag)
                        first_Enter_Eable = true;
                    A_Main_My_Message_Acy.logD("首页融云监听=教师—，融云连接成功");
                    if (!firstLoad_Tag) {
                        if(PubMehods.isFastClick(AppStrStatic.INTERVAL_RONGYUN_CONNECT)){
                            logE(AppStrStatic.INTERVAL_RONGYUN_CONNECT + "秒时间内onResume，，return，return");
                            return;
                        }
                        if(A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()){
                            if(!first_Enter_Eable){
                                updateListRong();//连接获取融云数据
                            }
                        }
                    }
                    break;
                case DISCONNECTED:// 断开连接。
                    first_Enter_Eable = false;
//                    new Handler().postDelayed(new Runnable() {
//                        @SuppressWarnings("deprecation")
//                        public void run() {
//                            showLoadResult(false, true, false);
//                        }
//                    }, REFRESH_DELAY_TIME);
                    A_Main_My_Message_Acy.logD("首页融云监听=教师——，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
//                    if(!firstLoad_Tag){
//                        if(A_0_App.USER_STATUS.equals("2"))
//                            PubMehods.showToastStr(A_Main_My_Message_Acy.this, "网络连接断开");
//                            A_0_App.getInstance().showExitDialog(A_Main_My_Message_Acy.this,getResources().getString(R.string.token_timeout));
//                    }
                    break;
                case CONNECTING:// 连接中。
                    A_Main_My_Message_Acy.logD("首页融云监听=教师——，融云连接中");
                    break;
                case NETWORK_UNAVAILABLE:// 网络不可用。
                    first_Enter_Eable = false;
                    A_Main_My_Message_Acy.logD("首页融云监听=教师——，融云连接网络不可用");
//                    new Handler().postDelayed(new Runnable() {
//                        @SuppressWarnings("deprecation")
//                        public void run() {
//                            showLoadResult(false, true, false);
//                        }
//                    }, REFRESH_DELAY_TIME);
                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
                    A_Main_My_Message_Acy.logD("首页融云监听=教师—，用户账户在其他设备登录，本机会被踢掉线");
                    if(isFinishing())
                        return;
                    class LooperThread extends Thread {
                        public void run() {
                            Looper.prepare();
                            A_0_App.getInstance().showExitDialog(A_Main_My_Message_Acy.this,AppStrStatic.kicked_offline());
                            Looper.loop();
                        }
                    }
                    LooperThread looper = new LooperThread();
                    looper.start();
                    break;
            }
        }
    }
	
    private void showTroubleState(ImageView imageview, int trouble_On_Off) {
        if (imageview == null)
            return;
        if (trouble_On_Off == 1) {
            imageview.setVisibility(View.VISIBLE);
        } else {
            imageview.setVisibility(View.GONE);
        }
    }
    
    public int trouble_On_Off = 0;//免打扰默认为关闭状态，1表示为打开状态
    // 获取融云消息免打扰状态
    private void getNoTroubleState() {
        if (RongIM.getInstance() != null && A_0_App.USER_STATUS.equals("2")&&A_0_App.USER_QUNIQID != null&&A_0_App.USER_QUNIQID.length() > 0) {
            RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.GROUP, A_0_App.USER_QUNIQID,
                    new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                @Override
                public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                    if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.DO_NOT_DISTURB) {
                          trouble_On_Off = 1;
                    }else{
                        trouble_On_Off = 0;
                    }
                    adapter.notifyDataSetChanged();
                }
    
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    trouble_On_Off = 0;
                    logE(trouble_On_Off + "获取群免打扰状态失败");
                }
            });
        }
    }
    
	@Override
	protected void handleTitleBarEvent(int resId) {
		switch (resId) {
		case SEARCH_BUTTON:
            if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_LOGIN_TIME)) {
			 Intent intent=new Intent(A_Main_My_Message_Acy.this, B_Mess_Notice_All_Search.class);
		      startActivity(intent);}
			break;
		
		default:
			break;
		}
		
	}

	public static A_Main_My_Message_Acy getInstance() {
		return instance;
	}
	
	public List<Cpk_Index_Notice_Message> getmMessageList() {
		return mMessageList;
	}

	public void setmMessageList(List<Cpk_Index_Notice_Message> mMessageList) {
		this.mMessageList = mMessageList;
	}

	@Override
    protected void onDestroy() {
        super.onDestroy();
        logD("onDestroy()");
        clearData(true);
        clearDataRong(true);
        if (mListContacts != null) {
            mListContacts.clear();
            mListContacts = null;
        }
        adapter = null;
        this.unregisterReceiver(bro);
        
        this.getApplicationContext().unbindService(conn);
        mLocalBroadcastManager.unregisterReceiver(mReciver);
        drawable.stop();
        drawable=null;
    }
	
	public static void logD(String msg) {
		com.yuanding.schoolpass.utils.LogUtils.logD("A_Main_My_Message_Acy", "A_Main_My_Message_Acy==>" + msg);
	}

	public static void logE(String msg) {
		com.yuanding.schoolpass.utils.LogUtils.logE("A_Main_My_Message_Acy", "A_Main_My_Message_Acy==>" + msg);
	}
	
    public static void logTest(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logD("test","test==>" + msg);
    }

    
   
    
}
